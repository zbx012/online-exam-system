package com.example.project.mapper;

import com.example.project.entity.ExamQuestion;
import com.example.project.entity.Question;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;

@Mapper
public interface ExamQuestionMapper {

    @Select("SELECT exam_id,question_id,order_num FROM exam_question WHERE exam_id = #{examId} and question_id=#{questionId}")
    @Results({
            @Result(column = "exam_id", property = "examId"),
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "order_num", property = "order"),
    })
    ExamQuestion selectExamQuestion(@Param("examId") int examId, @Param("questionId") int questionId);




    @Insert("INSERT INTO exam_question (exam_id, question_id,order_num) VALUES (#{examId}, #{questionId},#{orderNum})")
    int insertExamQuestion(@Param("examId") int examId, @Param("questionId") int questionId, @Param("orderNum") int orderNum);


    @Update("UPDATE exam_question SET order_num = #{order} WHERE exam_id = #{examId} and question_id=#{questionId} ")
    @Results({
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "order_num", property = "order"),
            @Result(column = "exam_id", property = "examId")
    })
    int updateExamQuestionOrder(ExamQuestion question);

    @Select("SELECT question_id FROM exam_question WHERE exam_id = #{examId} ORDER BY order_num")
    @Results({
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "exam_id", property = "examId")
    })
    ArrayList<Integer> selectQuestionsOrderByOrderNum(@Param("examId") int examId);

    @Delete("DELETE FROM exam_question WHERE exam_id = #{examId} and question_id=#{questionId}")
    int deleteExamQuestion(@Param("examId") int examId, @Param("questionId") int questionId);


}
