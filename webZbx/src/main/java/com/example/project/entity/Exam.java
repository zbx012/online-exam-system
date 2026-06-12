package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.LocalDateTime;

//考试表
public class Exam {
    private int examId;
    private String examName ;
    private int teacherId ;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime ;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private LocalDateTime endTime ;
    private String status ;

    public Exam() {
    }

    public Exam(int examId, String examName, int teacherId, LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.examId = examId;
        this.examName = examName;
        this.teacherId = teacherId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
