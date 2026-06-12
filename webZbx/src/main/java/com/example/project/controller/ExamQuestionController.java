package com.example.project.controller;

import com.example.project.entity.ExamQuestion;
import com.example.project.entity.Question;
import com.example.project.service.ExamQuestionService;
import com.example.project.service.QuestionService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/api/examQuestion")
public class ExamQuestionController {
    @Autowired
    private ExamQuestionService examQuestionService;
    @Autowired
    private QuestionService questionService;

    @PostMapping("/insert")
    public ResponseEntity<Boolean> insertExamQuestion(@RequestParam int examId, @RequestParam int questionId, @RequestParam int orderNum) {
        try {
            examQuestionService.insertExamQuestion(examId, questionId, orderNum);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteExamQuestion(@RequestParam int examId, @RequestParam int questionId) {
        try {
            examQuestionService.deleteExamQuestion(examId, questionId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PatchMapping("/update")
    public ResponseEntity<ExamQuestion> updateExamQuestion(@RequestBody ExamQuestion examQuestion) {
        try {
            ExamQuestion updateExamQuestion = examQuestionService.updateExamQuestionOrder(examQuestion);
            if (updateExamQuestion != null) {
                return ResponseEntity.ok(updateExamQuestion);
            }
            return ResponseEntity.status(500).build();
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/select")
    public ResponseEntity<ExamQuestion> selectQuestionsOrderByOrderNum(@RequestParam int examId, @RequestParam int questionId) {
        try {
            ExamQuestion examQuestion = examQuestionService.selectExamQuestion(examId, questionId);
            return ResponseEntity.ok(examQuestion);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/selectQuestionsByExamId")
    public ResponseEntity<ArrayList<Question>> selectQuestionsOrderByOrderNum(@RequestParam int examId) {
        try {
            ArrayList<Integer> questionIds = examQuestionService.selectQuestionsOrderByOrderNum(examId);
            ArrayList<Question> questions = new ArrayList<>();
            for(int questionId:questionIds){
                Question question = questionService.selectQuestion(questionId);
                questions.add(question);
            }
            return ResponseEntity.ok(questions);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
