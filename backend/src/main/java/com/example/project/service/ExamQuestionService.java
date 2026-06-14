package com.example.project.service;

import com.example.project.entity.ExamQuestion;
import com.example.project.mapper.ExamQuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ExamQuestionService {
    @Autowired
    private ExamQuestionMapper examQuestionMapper;
    public boolean insertExamQuestion(int examId, int questionId, int orderNum) {
        try {
            if(examQuestionMapper.selectExamQuestion(examId,questionId)!=null){
                return false;
            }
            return examQuestionMapper.insertExamQuestion(examId, questionId, orderNum) > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteExamQuestion(int examId,int questionId) {
        try {
            if(examQuestionMapper.selectExamQuestion(examId,questionId)==null){
                return false;
            }
            return examQuestionMapper.deleteExamQuestion(examId,questionId)>0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public ExamQuestion selectExamQuestion(int examId, int questionId) {
        try {
            return examQuestionMapper.selectExamQuestion(examId,questionId);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Integer> selectQuestionsOrderByOrderNum(int examId) {
        try {
            return examQuestionMapper.selectQuestionsOrderByOrderNum(examId);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ExamQuestion updateExamQuestionOrder(ExamQuestion examQuestion) {
        try {
            if(examQuestionMapper.selectExamQuestion(examQuestion.getExamId(),examQuestion.getQuestionId())==null){
                return null;
            }
            examQuestionMapper.updateExamQuestionOrder(examQuestion);
            return examQuestionMapper.selectExamQuestion(examQuestion.getExamId(),examQuestion.getQuestionId());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }




}
