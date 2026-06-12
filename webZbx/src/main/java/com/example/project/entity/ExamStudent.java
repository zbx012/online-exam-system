package com.example.project.entity;

public class ExamStudent {
    int studentId;
    int examId;
    int score;

    public ExamStudent() {
    }

    public ExamStudent(int studentId, int examId,int score) {
        this.studentId = studentId;
        this.examId = examId;
        this.score = score;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
