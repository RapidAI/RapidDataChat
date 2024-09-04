package com.RapidDataChat.backend.service;

import cn.hutool.crypto.digest.BCrypt;
import com.RapidDataChat.backend.model.User;
import com.RapidDataChat.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 查询所有用户。
     *
     * @return 用户列表
     */
    public List<User> findAll() {
        return userRepository.selectList(null);
    }

    /**
     * 根据用户ID查询用户。
     *
     * @param id 用户ID
     * @return 对应的用户信息，如果不存在返回null
     */
    public User findById(int id) {
        return userRepository.selectById(id);
    }

    /**
     * 根据用户名查找用户。
     *
     * @param username 用户名
     * @return 对应的用户，如果不存在返回null
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * 保存新用户信息到数据库。
     *
     * @param user 用户信息
     * @return 保存后的用户信息
     */
    public User save(User user) {
        // 对密码进行哈希处理
        if (user.getPassword() != null) {
            user.setPasswordHash(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        userRepository.insert(user);
        return user;
    }

    /**
     * 更新已存在的用户信息。
     *
     * @param user 新的用户信息
     * @param id   用户ID
     * @return 更新后的用户信息，如果用户不存在则返回null
     */
    public User update(User user, int id) {
        User existingUser = userRepository.selectById(id);
        if (existingUser != null) {
            if (user.getUsername() != null) {
                existingUser.setUsername(user.getUsername());
            }
            if (user.getPassword() != null) {
                // 对密码进行哈希处理
                existingUser.setPasswordHash(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            }
            if (user.getEmail() != null) {
                existingUser.setEmail(user.getEmail());
            }
            if (user.getAvatar() != null) {
                existingUser.setAvatar(user.getAvatar());
            }
            // 保留原有的 token
            if (user.getToken() != null) {
                existingUser.setToken(user.getToken());
            }
            userRepository.updateById(existingUser);
            return existingUser;
        } else {
            return null;
        }
    }

    /**
     * 根据用户ID删除用户。
     *
     * @param id 用户ID
     * @return 如果成功删除返回true，否则返回false
     */
    public boolean delete(int id) {
        int rows = userRepository.deleteById(id);
        return rows > 0;
    }

    /**
     * 根据电子邮件地址查找用户。
     *
     * @param email 电子邮件地址
     * @return 对应的用户，如果不存在返回null
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
