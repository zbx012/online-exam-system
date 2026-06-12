package com.example.project.controller;

import com.example.project.entity.Exam;
import com.example.project.entity.User;
import com.example.project.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController  // 声明这是一个REST API控制器
@RequestMapping("/api/exam")  // 所有api的前缀
public class ExamController {
    @Autowired
    private ExamService examService;

    //添加考试api
    @PostMapping("/add")
    public boolean addExam(
            @RequestParam String title,
            @RequestParam(required = false) int teacherId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endTime,
            @RequestParam(required = false) String status) {
        return examService.addExam(title, teacherId, startTime, endTime, status);
    }

    @GetMapping("/info")
    public ResponseEntity<Exam> info(@RequestParam int examId) {
        try {
            Exam exam = examService.getExamById(examId);
            if (exam != null) {
                return ResponseEntity.ok(exam);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<Exam> update(
            @RequestBody Exam exam) {
        try {
            //String token = userService.validateToken(user.getUsername());
            //if (token != null) {
            Exam updatedExam = examService.updateExam(exam);
            if (updatedExam != null) {
                return ResponseEntity.ok(updatedExam);
            } else {
                return ResponseEntity.status(500).build();
            }
            //}
            //return ResponseEntity.status(401).body()
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteExam(@RequestParam String examId) {
        try {
            boolean sussess = examService.deleteExam(examId);
            if (sussess) {
                return ResponseEntity.ok("删除成功！");
            }
            return ResponseEntity.status(500).body("删除失败：考试不存在");
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/selectAll")
    public ResponseEntity<ArrayList<Exam>> selectAll() {
        try {
            ArrayList<Exam> exams = examService.getAllExam();
            if (exams != null) {
                return ResponseEntity.ok(exams);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/selectByTeacherId")
    public ResponseEntity<ArrayList<Exam>> selectByTeacherId(@RequestParam int teacherId) {
        try {
            ArrayList<Exam> exams = examService.getAllExam();
            if (exams != null) {
                ArrayList<Exam> result = new ArrayList<>();
                for (Exam exam : exams) {
                    if (exam.getTeacherId()==teacherId) {
                        result.add(exam);
                    }
                }
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.notFound().build();
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
