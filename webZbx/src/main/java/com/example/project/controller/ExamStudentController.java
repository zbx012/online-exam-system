package com.example.project.controller;

import com.example.project.entity.ExamStudent;
import com.example.project.service.ExamStudentService;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/api/examStudent")
public class ExamStudentController {
    @Autowired
    private ExamStudentService examStudentService;

    @PostMapping("/insert")
    public ResponseEntity<Boolean> insertExamStudent(@RequestParam int examId, @RequestParam int studentId) {
        try {
            examStudentService.insertExamStudent(examId, studentId);
            return ResponseEntity.ok(true);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteExamStudent(@RequestParam int examId, @RequestParam int studentId) {
        try {
            examStudentService.deleteExamStudent(examId, studentId);
            return ResponseEntity.ok(true);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/updateScore")
    public ResponseEntity<Boolean> updateExamStudent(@RequestBody ExamStudent examStudent) {
        try {
            ExamStudent updateExamStudent=examStudentService.updateScore(examStudent);
            return ResponseEntity.ok(updateExamStudent!=null);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/selectByStudent")
    public ResponseEntity<ArrayList<ExamStudent>> selectByStudent(@RequestParam int studentId) {
        try {
            return ResponseEntity.ok(examStudentService.selectByStudentId(studentId));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/selectByExam")
    public ResponseEntity<ArrayList<ExamStudent>> selectByExam(@RequestParam int examId) {
        return ResponseEntity.ok(examStudentService.selectByExamId(examId));
    }

    @GetMapping("/selectByExamAndStudent")
    public ResponseEntity<ExamStudent> selectByExamAndStudent(@RequestParam int examId,@RequestParam int studentId) {
        return ResponseEntity.ok(examStudentService.selectByExamIdAndStudentId(examId,studentId));
    }


}
