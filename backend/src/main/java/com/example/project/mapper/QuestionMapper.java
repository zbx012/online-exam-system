package com.example.project.mapper;

import com.example.project.entity.Question;
import com.example.project.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;

@Mapper
public interface QuestionMapper {
    @Select("SELECT question_id, question_content, score,type FROM questions WHERE question_id = #{questionId}")
    @Results({
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "question_content", property = "questionContent"),
            @Result(column = "score", property = "score"),
            @Result(column = "type", property = "type")
    })
    Question selectByQuestionId(@Param("questionId") int questionId);



    @Insert("INSERT INTO questions(question_id, question_content, score,type) " +
            "VALUES(#{questionId}, #{questionContent},  #{score},#{type})")
    boolean insertQuestion(Question question);

    @Update({"<script>",
            "UPDATE questions",
            "<set>",
            "<if test='questionContent != null'>",
            "question_content = #{questionContent},",
            "</if>",
            "<if test='score != null'>",
            "score = #{score},",
            "</if>",
            "<if test='type != null'>",
            "type = #{type},",
            "</if>",
            "</set>",
            "WHERE question_id = #{questionId}",
            "</script>"})
    @Results({
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "question_content", property = "questionContent"),
            @Result(column = "score", property = "score"),
            @Result(column = "type", property = "type")
    })
    int updateQuestion(Question question);

    @Delete("DELETE FROM questions WHERE question_id = #{questionId}")
    int deleteQuestion(@Param("questionId") int questionId);

    @Select("SELECT * FROM questions")
    @Results({
            @Result(column = "question_id", property = "questionId"),
            @Result(column = "question_content", property = "questionContent"),
            @Result(column = "score", property = "score"),
            @Result(column = "type", property = "type")
    })
    ArrayList<Question> selectAll();

}
