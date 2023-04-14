package com.bootstart.mapper;

import com.bootstart.entity.UserData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from users where username = #{username}")
    UserData findUserByName(String username);
}
