package com.academic.mapper;

import com.academic.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);
}
