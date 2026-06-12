package com.example.project.service;

import com.example.project.entity.ChoiceQuestion;
import com.example.project.entity.FillQuestion;
import com.example.project.entity.Question;
import com.example.project.mapper.ChoiceQuestionMapper;
import com.example.project.mapper.FillQuestionMapper;
import com.example.project.mapper.QuestionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ChoiceQuestionMapper choiceQuestionMapper;
    @Autowired
    private FillQuestionMapper fillQuestionMapper;

    @Transactional
    @CacheEvict(value = "questions", allEntries = true)
    public boolean insertQuestion(Question question) {
        try {
            if ("CQ".equals(question.getType())) {
                Question question1 = new Question();
                BeanUtils.copyProperties(question, question1);
                return questionMapper.insertQuestion(question1) && choiceQuestionMapper.insertChoiceQuestion((ChoiceQuestion) question);
            } else if ("FB".equals(question.getType())) {
                Question question1 = new Question();
                BeanUtils.copyProperties(question, question1);
                return questionMapper.insertQuestion(question1) && fillQuestionMapper.insertFillQuestion((FillQuestion) question);
            }
            return false;
        } catch (Exception e) {
            log.error("新增题目失败", e);
            throw new RuntimeException(e);
        }
    }

    @Cacheable(value = "questions", key = "'question:' + #questionId")
    public Question selectQuestion(int questionId) {
        try {
            Question question = questionMapper.selectByQuestionId(questionId);
            if (question == null) return null;

            if ("CQ".equals(question.getType())) {
                return choiceQuestionMapper.selectByQuestionId(questionId);
            } else if ("FB".equals(question.getType())) {
                return fillQuestionMapper.selectByQuestionId(questionId);
            }
            return null;
        } catch (Exception e) {
            log.error("查询题目失败", e);
            throw new RuntimeException(e);
        }
    }

    @CacheEvict(value = "questions", allEntries = true)
    public Question updateQuestion(Question question) {
        try {
            Question existingQuestion = selectQuestion(question.getQuestionId());
            if (existingQuestion == null) {
                throw new IllegalArgumentException("题目不存在: " + question.getQuestionId());
            }
            String type = existingQuestion.getType();
            if ("CQ".equals(type)) {
                questionMapper.updateQuestion(question);
                choiceQuestionMapper.updateChoiceQuestion((ChoiceQuestion) question);
                return choiceQuestionMapper.selectByQuestionId(question.getQuestionId());
            } else if ("FB".equals(type)) {
                questionMapper.updateQuestion(question);
                fillQuestionMapper.updateFillQuestion((FillQuestion) question);
                return fillQuestionMapper.selectByQuestionId(question.getQuestionId());
            }
            return questionMapper.selectByQuestionId(question.getQuestionId());
        } catch (Exception e) {
            log.error("更新题目失败", e);
            throw new RuntimeException(e);
        }
    }

    @CacheEvict(value = "questions", allEntries = true)
    public int deleteQuestion(int questionId) {
        try {
            Question question = questionMapper.selectByQuestionId(questionId);
            if (question == null) return 0;
            if ("CQ".equals(question.getType())) {
                choiceQuestionMapper.deleteChoiceQuestion(questionId);
                return questionMapper.deleteQuestion(questionId);
            } else if ("FB".equals(question.getType())) {
                fillQuestionMapper.deleteFillQuestion(questionId);
                return questionMapper.deleteQuestion(questionId);
            }
            return 0;
        } catch (Exception e) {
            log.error("删除题目失败", e);
            return 0;
        }
    }

    @Cacheable(value = "questions", key = "'all'")
    public ArrayList<Question> selectAll() {
        try {
            ArrayList<Question> questions = questionMapper.selectAll();
            ArrayList<Question> result = new ArrayList<>();
            for (Question question : questions) {
                if ("CQ".equals(question.getType())) {
                    result.add(choiceQuestionMapper.selectByQuestionId(question.getQuestionId()));
                } else if ("FB".equals(question.getType())) {
                    result.add(fillQuestionMapper.selectByQuestionId(question.getQuestionId()));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("查询全部题目失败", e);
            return null;
        }
    }
}
