package com.RapidDataChat.backend.service;

import com.RapidDataChat.backend.model.UserInputRecord;
import com.RapidDataChat.backend.repository.UserInputRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInputRecordService {

    @Autowired
    private UserInputRecordRepository userInputRecordRepository;

    /**
     * 查询所有用户输入记录。
     * @return 用户输入记录列表
     */
    public List<UserInputRecord> findAll() {
        return userInputRecordRepository.selectList(null);
    }

    /**
     * 根据用户ID查询用户的所有输入记录。
     * @param userId 用户ID
     * @return 用户的所有输入记录列表
     */
    public List<UserInputRecord> findByUserId(int userId) {
        return userInputRecordRepository.findByUserId(userId);
    }

    /**
     * 保存新用户输入记录到数据库。
     * @param record 用户输入记录
     * @return 保存后的用户输入记录
     */
    public UserInputRecord save(UserInputRecord record) {
        userInputRecordRepository.insert(record);
        return record;
    }

    /**
     * 更新已存在的用户输入记录。
     * @param record 新的用户输入记录
     * @param inputId 输入ID
     * @return 更新后的用户输入记录，如果记录不存在则返回null
     */
    public UserInputRecord update(UserInputRecord record, int inputId) {
        UserInputRecord existingRecord = userInputRecordRepository.selectById(inputId);
        if (existingRecord != null) {
            if (record.getInputText() != null) {
                existingRecord.setInputText(record.getInputText());
            }
            userInputRecordRepository.updateById(existingRecord);
            return existingRecord;
        } else {
            return null;
        }
    }

    /**
     * 根据输入ID删除用户输入记录。
     * @param inputId 输入ID
     * @return 如果成功删除返回true，否则返回false
     */
    public boolean delete(int inputId) {
        int rows = userInputRecordRepository.deleteById(inputId);
        return rows > 0;
    }
}