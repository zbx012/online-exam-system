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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);
    private static final String QUESTION_CACHE_PREFIX = "question:";
    private static final String ALL_QUESTIONS_CACHE_KEY = "questions:all";
    private static final long CACHE_TTL_MINUTES = 30;

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ChoiceQuestionMapper choiceQuestionMapper;
    @Autowired
    private FillQuestionMapper fillQuestionMapper;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public boolean insertQuestion(Question question) {
        try {
            boolean success = false;
            if ("CQ".equals(question.getType())) {
                Question question1 = new Question();
                BeanUtils.copyProperties(question, question1);
                success = questionMapper.insertQuestion(question1) && choiceQuestionMapper.insertChoiceQuestion((ChoiceQuestion) question);
            } else if ("FB".equals(question.getType())) {
                Question question1 = new Question();
                BeanUtils.copyProperties(question, question1);
                success = questionMapper.insertQuestion(question1) && fillQuestionMapper.insertFillQuestion((FillQuestion) question);
            } else {
                return false;
            }
            if (success) {
                clearQuestionCache(question.getQuestionId());
            }
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Question selectQuestion(int questionId) {
        try {
            // 缓存穿透保护：先查 Redis
            String cacheKey = QUESTION_CACHE_PREFIX + questionId;
            if (redisTemplate != null) {
                try {
                    Question cached = (Question) redisTemplate.opsForValue().get(cacheKey);
                    if (cached != null) {
                        log.debug("从 Redis 缓存获取题目: {}", questionId);
                        return cached;
                    }
                } catch (Exception e) {
                    log.warn("Redis 读取失败，回源数据库: {}", e.getMessage());
                }
            }

            // 缓存未命中，查数据库
            Question question = questionMapper.selectByQuestionId(questionId);
            if (question == null) return null;

            Question result;
            if ("CQ".equals(question.getType())) {
                result = choiceQuestionMapper.selectByQuestionId(questionId);
            } else if ("FB".equals(question.getType())) {
                result = fillQuestionMapper.selectByQuestionId(questionId);
            } else {
                return null;
            }

            // 写入缓存
            if (redisTemplate != null && result != null) {
                try {
                    redisTemplate.opsForValue().set(cacheKey, result, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
                } catch (Exception e) {
                    log.warn("Redis 写入失败: {}", e.getMessage());
                }
            }

            return result;
        } catch (Exception e) {
            log.error("查询题目失败", e);
            throw new RuntimeException(e);
        }
    }


    public Question updateQuestion(Question question) {
        try {
            Question existingQuestion = selectQuestion(question.getQuestionId());
            if (existingQuestion == null) {
                throw new IllegalArgumentException("Question not found with id: " + question.getQuestionId());
            }
            String type = existingQuestion.getType();
            Question result;
            if ("CQ".equals(type)) {
                questionMapper.updateQuestion(question);
                choiceQuestionMapper.updateChoiceQuestion((ChoiceQuestion) question);
                result = choiceQuestionMapper.selectByQuestionId(question.getQuestionId());
            } else if ("FB".equals(type)) {
                questionMapper.updateQuestion(question);
                fillQuestionMapper.updateFillQuestion((FillQuestion) question);
                result = fillQuestionMapper.selectByQuestionId(question.getQuestionId());
            } else {
                result = questionMapper.selectByQuestionId(question.getQuestionId());
            }
            clearQuestionCache(question.getQuestionId());
            return result;
        } catch (Exception e) {
            log.error("更新题目失败", e);
            throw new RuntimeException(e);
        }
    }

    public int deleteQuestion(int questionId) {
        try {
            int result = 0;
            Question question = questionMapper.selectByQuestionId(questionId);
            if (question == null) return 0;
            if ("CQ".equals(question.getType())) {
                choiceQuestionMapper.deleteChoiceQuestion(questionId);
                result = questionMapper.deleteQuestion(questionId);
            } else if ("FB".equals(question.getType())) {
                fillQuestionMapper.deleteFillQuestion(questionId);
                result = questionMapper.deleteQuestion(questionId);
            }
            if (result > 0) {
                clearQuestionCache(questionId);
            }
            return result;
        } catch (Exception e) {
            log.error("删除题目失败", e);
            return 0;
        }
    }


    public ArrayList<Question> selectAll() {
        try {
            // 先查缓存
            if (redisTemplate != null) {
                try {
                    Object cached = redisTemplate.opsForValue().get(ALL_QUESTIONS_CACHE_KEY);
                    if (cached instanceof ArrayList) {
                        @SuppressWarnings("unchecked")
                        ArrayList<Question> result = (ArrayList<Question>) cached;
                        log.debug("从 Redis 缓存获取全部题目列表，共 {} 道", result.size());
                        return result;
                    }
                } catch (Exception e) {
                    log.warn("Redis 读取失败，回源数据库: {}", e.getMessage());
                }
            }

            ArrayList<Question> questions = questionMapper.selectAll();
            ArrayList<Question> result = new ArrayList<>();
            for (Question question : questions) {
                if ("CQ".equals(question.getType())) {
                    result.add(choiceQuestionMapper.selectByQuestionId(question.getQuestionId()));
                } else if ("FB".equals(question.getType())) {
                    result.add(fillQuestionMapper.selectByQuestionId(question.getQuestionId()));
                }
            }

            // 写入缓存
            if (redisTemplate != null && !result.isEmpty()) {
                try {
                    redisTemplate.opsForValue().set(ALL_QUESTIONS_CACHE_KEY, result, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
                } catch (Exception e) {
                    log.warn("Redis 写入失败: {}", e.getMessage());
                }
            }

            return result;
        } catch (Exception e) {
            log.error("查询全部题目失败", e);
            return null;
        }
    }

    /**
     * 清除题目相关缓存（增/删/改题目时调用）
     */
    private void clearQuestionCache(int questionId) {
        if (redisTemplate == null) return;
        try {
            redisTemplate.delete(QUESTION_CACHE_PREFIX + questionId);
            redisTemplate.delete(ALL_QUESTIONS_CACHE_KEY);
            log.debug("已清除题目缓存: questionId={}", questionId);
        } catch (Exception e) {
            log.warn("清除缓存失败: {}", e.getMessage());
        }
    }

}