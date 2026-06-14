package com.example.project.mapper;


import com.example.project.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;


@Mapper  // 告诉Spring这是一个MyBatis的Mapper
public interface UserMapper {
    @Select("SELECT user_id, username, password, email,user_type FROM users WHERE username = #{username}")
    @Results({
            @Result(column = "user_id", property = "id"),
            @Result(column = "user_type", property = "type")
    })
    User selectByUsername(@Param("username") String username);

    // 插入新用户（用于注册）
    @Insert("INSERT INTO users(username, password, email,user_type) " +
            "VALUES(#{username}, #{password}, #{email},#{type})")
    int insertUser(User user);

    @Update({"<script>",
            "UPDATE users",
            "<set>",
            "<if test='username != null'>",
            "username = #{username},",
            "</if>",
            "<if test='password != null'>",
            "password = #{password},",
            "</if>",
            "<if test='email != null'>",
            "email = #{email},",
            "</if>",
            "</set>",
            "WHERE username = #{username}",
            "</script>"
            })
    @Results({
            @Result(column = "user_id", property = "id"),
            @Result(column = "user_type", property = "type")
    })
    int updateUser(User user);

    @Delete("DELETE FROM users WHERE username = #{username}")
    int deleteByUserName(@Param("username") String username);

    @Select("SELECT user_id, username, password, email,user_type FROM users WHERE user_id = #{user_id}")
    @Results({
            @Result(column = "user_id", property = "id"),
            @Result(column = "user_type", property = "type")
    })
    User selectByUserId(@Param("user_id") int user_id);

    @Select("SELECT user_id, username, password, email,user_type FROM users")
    @Results({
            @Result(column = "user_id", property = "id"),
            @Result(column = "user_type", property = "type")
    })
    ArrayList<User> selectAll();
}
