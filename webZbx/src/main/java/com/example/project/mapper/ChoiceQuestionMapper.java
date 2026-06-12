package com.example.project.mapper;

import com.example.project.entity.ChoiceQuestion;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Results;

import java.util.ArrayList;

@Mapper
public interface ChoiceQuestionMapper extends QuestionMapper{
    @Select("SELECT b.question_id, b.question_content, b.score, b.type, " +
            "a.optionA, a.optionB, a.optionC, a.optionD, a.answer " +
            "FROM choice_questions a " +
            "JOIN questions b ON a.question_id = b.question_id " +
            "WHERE b.question_id = #{question_Id}")
    @Results({
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "question_content", property = "questionContent"),
            @Result(column = "score", property = "score"),
            @Result(column = "type", property = "type")
    })
    @Override
    ChoiceQuestion selectByQuestionId(@Param("question_Id") int questionId);

    @Insert("INSERT INTO choice_questions(question_id,optionA,optionB,optionC,optionD,answer) " +
            "VALUES(#{questionId}, #{optionA}, #{optionB}, #{optionC}, #{optionD}, #{answer})")
    boolean insertChoiceQuestion(ChoiceQuestion choiceQuestion);

    @Update({"<script>",
            "UPDATE choice_questions",
            "<set>",
            "<if test='optionA != null'>",
            "optionA = #{optionA},",
            "</if>",
            "<if test='optionB != null'>",
            "optionB = #{optionB},",
            "</if>",
            "<if test='optionC != null'>",
            "optionC = #{optionC},",
            "</if>",
            "<if test='optionD != null'>",
            "optionD = #{optionD},",
            "</if>",
            "<if test='answer != null'>",
            "answer = #{answer},",
            "</if>",
            "</set>",
            "WHERE question_id = #{questionId}",
            "</script>"})
    @Results({
            @Result(column = "question_id", property = "questionId"),
    })
    int updateChoiceQuestion(ChoiceQuestion choiceQuestion);

    @Delete("DELETE FROM choice_questions WHERE question_id = #{questionId}")
    int deleteChoiceQuestion(@Param("questionId") int questionId);

    /**
    @Select("SELECT b.question_id, b.question_content, b.score, b.type, " +
            "a.optionA, a.optionB, a.optionC, a.optionD, a.answer " +
            "FROM choice_questions a " +
            "JOIN questions b ON a.question_id = b.question_id ")
    @Results({
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "question_content", property = "questionContent"),
            @Result(column = "score", property = "score"),
            @Result(column = "type", property = "type")
    })
    ArrayList<ChoiceQuestion> selectAll();

     **/


}
