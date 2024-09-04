package com.RapidDataChat.backend.service;

import com.RapidDataChat.backend.model.Session;
import com.RapidDataChat.backend.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    /**
     * 从数据库中检索所有会话。
     *
     * @return Session列表
     */
    public List<Session> findAll() {
        return sessionRepository.selectList(null);
    }

    /**
     * 获取按创建时间倒序排列的用户会话列表
     * @param userId 用户ID
     * @return 按创建时间倒序排列的会话列表
     */
    public List<Session> getSessionsByUserId(int userId) {
        return sessionRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 根据用户ID检索会话。
     *
     * @param userId 用户ID
     * @return 对应用户的会话列表
     */
    public List<Session> findByUserId(int userId) {
        return sessionRepository.findByUserId(userId);
    }

    /**
     * 将新会话保存到数据库。
     *
     * @param session 要保存的会话实体
     * @return 保存的Session对象
     */
    public Session save(Session session) {
        sessionRepository.insert(session);
        return session;
    }

    /**
     * 在数据库中更新现有会话。
     *
     * @param session 带有更新字段的会话实体
     * @param id      要更新的会话的ID
     * @return 如果更新成功返回true，否则返回false
     */
    public boolean update(Session session, int id) {
        Session existingSession = sessionRepository.selectById(id);
        if (existingSession != null) {
            existingSession.setTitle(session.getTitle());
            existingSession.setMessages(session.getMessages());
            existingSession.setUserId(session.getUserId());
            existingSession.setDatabaseInfo(session.getDatabaseInfo());
            existingSession.setDatabaseInfoCheked(session.getDatabaseInfoCheked());
            existingSession.setModelId(session.getModelId());
            sessionRepository.updateById(existingSession);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从数据库中删除会话。
     *
     * @param id 要删除的会话的ID
     * @return 如果会话成功删除则返回true，否则返回false
     */
    public boolean delete(int id) {
        int rows = sessionRepository.deleteById(id);
        return rows > 0;
    }
}
