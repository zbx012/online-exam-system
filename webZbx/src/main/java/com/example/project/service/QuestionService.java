package com.example.project.service;

import com.example.project.entity.ChoiceQuestion;
import com.example.project.entity.FillQuestion;
import com.example.project.entity.Question;
import com.example.project.mapper.ChoiceQuestionMapper;
import com.example.project.mapper.FillQuestionMapper;
import com.example.project.mapper.QuestionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ChoiceQuestionMapper choiceQuestionMapper;
    @Autowired
    private FillQuestionMapper fillQuestionMapper;

    @Transactional
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
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Question selectQuestion(int questionId) {
        try {
            if (questionMapper.selectByQuestionId(questionId).getType().equals("CQ")) {
                return choiceQuestionMapper.selectByQuestionId(questionId);
            } else if (questionMapper.selectByQuestionId(questionId).getType().equals("FB")) {
                return fillQuestionMapper.selectByQuestionId(questionId);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public Question updateQuestion(Question question) {
        try {
            // 只查询一次，避免重复查询
            Question existingQuestion = selectQuestion(question.getQuestionId());
            // 添加空值检查
            if (existingQuestion == null) {
                throw new IllegalArgumentException("Question not found with id: " + question.getQuestionId());
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int deleteQuestion(int questionId) {
        try {
            if ("CQ".equals(questionMapper.selectByQuestionId(questionId).getType())) {
                choiceQuestionMapper.deleteChoiceQuestion(questionId);
                return questionMapper.deleteQuestion(questionId);
            } else if ("FB".equals(questionMapper.selectByQuestionId(questionId).getType())) {
                fillQuestionMapper.deleteFillQuestion(questionId);
                return questionMapper.deleteQuestion(questionId);
            }else{
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


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
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


}
