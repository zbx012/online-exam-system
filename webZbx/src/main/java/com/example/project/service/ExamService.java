package com.example.project.service;

import com.example.project.entity.Exam;
import com.example.project.mapper.ExamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class ExamService {
    @Autowired
    private ExamMapper examMapper;
    public boolean addExam(String title, int teacherId,LocalDateTime startTime,LocalDateTime endTime, String status) {
        Exam exam = new Exam();
        exam.setExamName(title);
        exam.setTeacherId(teacherId);
        exam.setStatus(status);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);
        return examMapper.insertExam(exam) > 0;
    }

    public Exam getExamById(int examId) {
        try {
            Exam exam = examMapper.selectByExamId(examId);
            if (exam != null) {
                return exam;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Exam updateExam(Exam exam) {
        examMapper.updateExam(exam);
        return examMapper.selectByExamId(exam.getExamId());
    }

    public boolean deleteExam(String examId) {
        return examMapper.deleteExam(examId) > 0;
    }

    public ArrayList<Exam> getAllExam() {
        return examMapper.selectAll();
    }

    public ArrayList<Exam> getExamByTeacherId(int teacherId) {
        return examMapper.selectByTeacherId(teacherId);
    }
}
