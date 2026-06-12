package com.example.project.entity;

//填空题
public class FillQuestion extends  Question{
    private String answer;
    public FillQuestion(){
    }
    public FillQuestion(int id,String content,int score,String type){
        super(id,content,score,type);
        this.type="FB";
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
