package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserRepository extends BaseMapper<User> {

    /**
     * 根据用户名查找用户。
     * 返回 Optional<User> 来优雅地处理可能的 null 值。
     * @param username 用户名
     * @return 包含用户信息的Optional对象，如果未找到则为empty
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    Optional<User> findByUsername(String username);

    /**
     * 根据电子邮箱查找用户。
     * 返回 Optional<User> 来优雅地处理可能的 null 值。
     * @param email 电子邮箱
     * @return 包含用户信息的Optional对象，如果未找到则为empty
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    Optional<User> findByEmail(String email);
}
