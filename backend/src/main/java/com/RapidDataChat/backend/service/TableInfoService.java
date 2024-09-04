package com.RapidDataChat.backend.service;

import com.RapidDataChat.backend.model.TableInfo;
import com.RapidDataChat.backend.repository.TableInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TableInfoService {

    @Autowired
    private TableInfoRepository tableInfoRepository;

    /**
     * 根据ID检索表信息。
     *
     * @param id 表信息ID
     * @return 如果找到则返回相应的TableInfo对象，否则返回null
     */
    public TableInfo findById(int id) {
        return tableInfoRepository.selectById(id);
    }

    /**
     * 根据 databaseInfoId 查找所有表信息。
     *
     * @param databaseInfoId 关联的数据库信息ID
     * @return 表信息列表
     */
    public List<TableInfo> findByDatabaseInfoId(int databaseInfoId) {
        return tableInfoRepository.selectByDatabaseInfoId(databaseInfoId);
    }

    /**
     * 将新表信息保存到数据库。
     *
     * @param tableInfo 要保存的表信息实体
     * @return 保存的TableInfo对象
     */
    public TableInfo save(TableInfo tableInfo) {
        tableInfoRepository.insert(tableInfo);
        return tableInfo;
    }

    /**
     * 更新数据库中的现有表信息。
     *
     * @param tableInfo 带有更新字段的表信息实体
     * @param id        要更新的表信息的ID
     * @return 如果更新成功返回true，否则返回false
     */
    @Transactional
    public boolean update(TableInfo tableInfo, int id) {
        TableInfo existingTableInfo = tableInfoRepository.selectById(id);
        if (existingTableInfo != null) {
            existingTableInfo.setDatabaseInfoId(tableInfo.getDatabaseInfoId());
            existingTableInfo.setTableName(tableInfo.getTableName());
            existingTableInfo.setTableComment(tableInfo.getTableComment());
            existingTableInfo.setTableDescription(tableInfo.getTableDescription());
            existingTableInfo.setCreateTime(tableInfo.getCreateTime()); // Optional: 如果需要更新创建时间
            existingTableInfo.setUpdateTime(tableInfo.getUpdateTime());
            tableInfoRepository.updateById(existingTableInfo);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从数据库中删除指定ID的表信息。
     *
     * @param id 要删除的表信息ID
     * @return 删除成功返回true，否则返回false
     */
    @Transactional
    public boolean delete(int id) {
        TableInfo tableInfo = tableInfoRepository.selectById(id);
        if (tableInfo != null) {
            tableInfoRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 批量保存表信息。
     *
     * @param tableInfoList 要保存的表信息列表
     * @return 保存的表信息列表
     */
    public List<TableInfo> saveAll(List<TableInfo> tableInfoList) {
        for (TableInfo tableInfo : tableInfoList) {
            // 根据表名检查表是否已存在
            TableInfo existingTableInfo = tableInfoRepository.selectByDatabaseInfoIdAndTableName(tableInfo.getDatabaseInfoId(), tableInfo.getTableName());

            if (existingTableInfo != null) {
                // 表已存在，执行更新操作
                existingTableInfo.setDatabaseInfoId(tableInfo.getDatabaseInfoId());
                existingTableInfo.setTableComment(tableInfo.getTableComment());
                existingTableInfo.setTableDescription(tableInfo.getTableDescription());
                existingTableInfo.setCreateTime(tableInfo.getCreateTime());
                existingTableInfo.setUpdateTime(tableInfo.getUpdateTime());
                tableInfoRepository.updateById(existingTableInfo);
            } else {
                // 表不存在，执行插入操作
                tableInfoRepository.insert(tableInfo);
            }
        }
        return tableInfoList;
    }
}
