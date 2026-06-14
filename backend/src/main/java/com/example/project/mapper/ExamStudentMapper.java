package com.example.project.mapper;

import com.example.project.entity.ExamStudent;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;

@Mapper
public interface ExamStudentMapper {
  @Select("SELECT * FROM exam_student WHERE exam_id = #{exam_id}")
  @Results({
      @Result(column = "exam_id", property = "examId"),
      @Result(column = "student_id", property = "studentId"),
      @Result(column = "score", property = "score"),
  })
  ArrayList<ExamStudent> selectByExamId(@Param("exam_id") int examId);

  @Select("SELECT * FROM exam_student WHERE student_id = #{student_id}")
    @Results({
      @Result(column = "exam_id", property = "examId"),
      @Result(column = "student_id", property = "studentId"),
      @Result(column = "score", property = "score"),
  })
  ArrayList<ExamStudent> selectByStudentId(@Param("student_id") int studentId);

  @Select("SELECT * FROM exam_student WHERE exam_id = #{exam_id} AND student_id = #{student_id}")
  @Results({
      @Result(column = "exam_id", property = "examId"),
      @Result(column = "student_id", property = "studentId"),
      @Result(column = "score", property = "score"),
  })
  ExamStudent selectByExamIdAndStudentId(@Param("exam_id") int examId,@Param("student_id") int studentId);

  @Insert("INSERT INTO exam_student(exam_id, student_id) VALUES(#{examId}, #{studentId})")
    int insertExamStudent(@Param("examId") int examId,@Param("studentId") int studentId);

  @Update("UPDATE exam_student SET score = #{score} WHERE exam_id = #{examId} AND student_id = #{studentId}")
  @Results({
      @Result(column = "exam_id", property = "examId"),
      @Result(column = "student_id", property = "studentId"),
      @Result(column = "score", property = "score"),
  })
    int updateScore(ExamStudent examStudent);

  @Delete("DELETE FROM exam_student WHERE exam_id = #{examId} AND student_id = #{studentId}")
    int deleteExamStudent(@Param("examId") int examId, @Param("studentId") int studentId);





}
