package com.example.project.service;

import com.example.project.entity.ExamStudent;
import com.example.project.mapper.ExamStudentMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ExamStudentService {
    @Autowired
    private ExamStudentMapper examStudentMapper;
    public boolean insertExamStudent(int examId,int studentId){
        try {
            return examStudentMapper.insertExamStudent(examId,studentId)>0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public ExamStudent updateScore(ExamStudent examStudent){
        try {
            if( examStudentMapper.updateScore(examStudent)>0){
                return examStudentMapper.selectByExamIdAndStudentId(examStudent.getExamId(),examStudent.getStudentId());
            };
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteExamStudent(int examId,int studentId){
        try {
            return examStudentMapper.deleteExamStudent(examId,studentId)>0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public ArrayList<ExamStudent> selectByExamId(int examId){
        try {
            return examStudentMapper.selectByExamId(examId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public ArrayList<ExamStudent> selectByStudentId(int studentId){
        try {
            return examStudentMapper.selectByStudentId(studentId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public ExamStudent selectByExamIdAndStudentId(int examId,int studentId){
        try {
            return examStudentMapper.selectByExamIdAndStudentId(examId,studentId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
