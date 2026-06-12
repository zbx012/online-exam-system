package com.example.project.mapper;

import com.example.project.entity.FillQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;

@Mapper
public interface FillQuestionMapper extends QuestionMapper{
    @Select("SELECT b.question_id, b.question_content, b.score, b.type, " +
            "a.answer " +
            "FROM fill_questions a " +
            "JOIN questions b ON a.question_id = b.question_id " +
            "WHERE b.question_id = #{question_Id}")
    @Results({
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "question_content", property = "questionContent"),
            @Result(column = "score", property = "score"),
            @Result(column = "type", property = "type")
    })
    @Override
    FillQuestion selectByQuestionId(@Param("question_Id") int questionId);

    @Insert("INSERT INTO fill_questions(question_id,answer) " +
            "VALUES(#{questionId}, #{answer})")
    boolean insertFillQuestion(FillQuestion fillQuestion);

    @Update({"<script>",
            "UPDATE fill_questions",
            "<set>",
            "<if test='answer != null'>",
            "answer = #{answer},",
            "</if>",
            "</set>",
            "WHERE question_id = #{questionId}",
            "</script>"})
    @Results({@Result (column = "question_id", property = "questionId"),})
    int updateFillQuestion(FillQuestion fillQuestion);

    @Delete("DELETE FROM fill_questions WHERE question_id = #{questionId}")
    int deleteFillQuestion(@Param("questionId") int questionId);

    /**
    @Select("SELECT b.question_id, b.question_content, b.score, b.type, " +
            "a.answer " +
            "FROM fill_questions a " +
            "JOIN questions b ON a.question_id = b.question_id " )
    @Results({
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "question_content", property = "questionContent"),
            @Result(column = "score", property = "score"),
            @Result(column = "type", property = "type")
    })
    ArrayList<FillQuestion> selectAll();
            **/
}
