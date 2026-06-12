package com.example.project.mapper;

import com.example.project.entity.Exam;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;

@Mapper
public interface ExamMapper {
    @Select("SELECT exam_id,title,teacher_id,start_time,end_time,status FROM exams WHERE exam_id=#{exam_id}")
    @Results({
        @Result(column = "exam_id", property = "examId"),
            @Result(column = "title", property = "examName"),
        @Result(column = "teacher_id", property = "teacherId"),
        @Result(column = "start_time", property = "startTime"),
        @Result(column = "end_time", property = "endTime"),
    })
    Exam selectByExamId(@Param("exam_id") int examId);

    @Insert("INSERT INTO exams(title,teacher_id,start_time,end_time,status) " +
            "VALUES(#{examName}, #{teacherId}, #{startTime}, #{endTime}, #{status})")
    int insertExam(Exam exam);

    @Update({"<script>",
            "UPDATE exams",
            "<set>",
            "<if test='examName != null'>",
            "title = #{examName},",
            "</if>",
            "<if test='teacherId != null'>",
            "teacher_id = #{teacherId},",
            "</if>",
            "<if test='startTime != null'>",
            "start_time = #{startTime},",
            "</if>",
            "<if test='endTime != null'>",
            "end_time = #{endTime},",
            "</if>",
            "</set>",
            "WHERE exam_id = #{examId}",
            "</script>"
    })
    int updateExam(Exam exam);

    @Delete("DELETE FROM exams WHERE exam_id = #{examId}")
    int deleteExam(@Param("examId") String examId);

    @Select("SELECT exam_id,title,teacher_id,start_time,end_time,status FROM exams")
    @Results({
        @Result(column = "exam_id", property = "examId"),
            @Result(column = "title", property = "examName"),
        @Result(column = "teacher_id", property = "teacherId"),
        @Result(column = "start_time", property = "startTime"),
        @Result(column = "end_time", property = "endTime"),
    })
    ArrayList<Exam> selectAll();

    @Select("SELECT exam_id,title,teacher_id,start_time,end_time,status FROM exams WHERE teacher_id = #{teacherId}")
    @Results({
        @Result(column = "exam_id", property = "examId"),
            @Result(column = "title", property = "examName"),
        @Result(column = "teacher_id", property = "teacherId"),
        @Result(column = "start_time", property = "startTime"),
        @Result(column = "end_time", property = "endTime"),
    })
    ArrayList<Exam> selectByTeacherId(@Param("teacherId") int teacherId);
}
