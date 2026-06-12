package com.example.project.controller;


import com.example.project.entity.ChoiceQuestion;
import com.example.project.entity.FillQuestion;
import com.example.project.entity.Question;
import com.example.project.service.QuestionService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/api/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @PostMapping("/addChoice")
    public ResponseEntity<Boolean> addChoiceQuestion(@RequestBody ChoiceQuestion question) {
        boolean result = questionService.insertQuestion(question);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/addFill")
    public ResponseEntity<Boolean> addFillQuestion(@RequestBody FillQuestion question) {
        boolean result = questionService.insertQuestion(question);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/updateChoice")
    public ResponseEntity<ChoiceQuestion> updateChoiceQuestion(@RequestBody ChoiceQuestion question){
        try {
            Question updateQuestion=questionService.updateQuestion(question);
            if (updateQuestion instanceof ChoiceQuestion){
                ChoiceQuestion choiceQuestion = (ChoiceQuestion) updateQuestion;
                return ResponseEntity.ok(choiceQuestion);
            }
            return ResponseEntity.status(500).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PatchMapping("/updateFill")
    public ResponseEntity<FillQuestion> updateFillQuestion(@RequestBody FillQuestion question){
        try {
            Question updateQuestion=questionService.updateQuestion(question);
            if (updateQuestion instanceof FillQuestion){
                FillQuestion fillQuestion = (FillQuestion) updateQuestion;
                return ResponseEntity.ok(fillQuestion);
            }
            return ResponseEntity.status(500).build();
        } catch (IllegalArgumentException e) {
            // 处理业务逻辑异常，如题目不存在
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/deleteChoice")
    public ResponseEntity<ChoiceQuestion> deleteChoiceQuestion(int questionId){
        try {
            questionService.deleteQuestion(questionId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }
    @DeleteMapping("/deleteFill")
    public ResponseEntity<FillQuestion> deleteFillQuestion(int questionId){
        try {
            questionService.deleteQuestion(questionId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/selectChoice")
    public ResponseEntity<ChoiceQuestion> selectChoiceQuestion(@RequestParam int questionId) {
        try {
            Question question = questionService.selectQuestion(questionId);
            return ResponseEntity.ok((ChoiceQuestion) question);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/selectFill")
    public ResponseEntity<FillQuestion> selectFillQuestion(@RequestParam int questionId) {
        try {
            Question question = questionService.selectQuestion(questionId);
            return ResponseEntity.ok((FillQuestion)question);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/selectAll")
    public ResponseEntity<ArrayList<Question>> selectAllQuestions() {
        try {
            ArrayList<Question> questions = questionService.selectAll();
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
