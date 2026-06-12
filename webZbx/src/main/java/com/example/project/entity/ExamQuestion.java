package com.example.project.entity;


//考试-题目关联表
public class ExamQuestion {
    private int questionId;
    private int examId;
    private int order;

    public ExamQuestion() {
    }

    public ExamQuestion(int questionId, int examId, int order) {
        this.questionId = questionId;
        this.examId = examId;
        this.order = order;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
