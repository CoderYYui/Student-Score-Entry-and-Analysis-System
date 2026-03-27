package com.student.score.system.mapper;

import com.student.score.system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users WHERE role = #{role}")
    java.util.List<User> findByRole(@Param("role") String role);

    @Update("UPDATE users SET username = #{username}, password = #{password}, role = #{role}, name = #{name}, email = #{email}, failed_attempts = #{failedAttempts}, locked = #{locked}, status = #{status} WHERE id = #{id}")
    void update(User user);
    
    @org.apache.ibatis.annotations.Insert("INSERT INTO users(username, password, role, name, email, status) VALUES(#{username}, #{password}, #{role}, #{name}, #{email}, #{status})")
    @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    void deleteById(Long id);
}
