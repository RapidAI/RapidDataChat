package com.RapidDataChat.backend.service;

import com.RapidDataChat.backend.model.ColumnInfo;
import com.RapidDataChat.backend.repository.ColumnInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnInfoService {

    @Autowired
    private ColumnInfoRepository columnInfoRepository;

    /**
     * 从数据库中检索所有列。
     * @return ColumnInfo列表
     */
    public List<ColumnInfo> findAll() {
        return columnInfoRepository.selectList(null);
    }


    /**
     * 根据列ID检索列信息。
     * @param id 列的ID
     * @return 如果找到则返回相应的ColumnInfo对象，否则返回null
     */
    public ColumnInfo findById(int id) {
        return columnInfoRepository.selectById(id);
    }

    /**
     * 根据表信息ID检索列信息。
     * @param tableInfoId 表信息ID
     * @return ColumnInfo列表
     */
    public List<ColumnInfo> findByTableInfoId(int tableInfoId) {
        return columnInfoRepository.selectByTableInfoId(tableInfoId);
    }

    /**
     * 将新列保存到数据库。
     * @param columnInfo 要保存的列实体
     * @return 保存的ColumnInfo对象
     */
    public ColumnInfo save(ColumnInfo columnInfo) {
        columnInfoRepository.insert(columnInfo);
        return columnInfo;
    }

    /**
     * 在数据库中更新现有列。
     * @param columnInfo 带有更新字段的列实体
     * @param id 要更新的列的ID
     * @return 如果更新成功返回true，否则返回false
     */
    public boolean update(ColumnInfo columnInfo, int id) {
        ColumnInfo existingColumnInfo = columnInfoRepository.selectById(id);
        if (existingColumnInfo != null) {
            existingColumnInfo.setTableInfoId(columnInfo.getTableInfoId());
            existingColumnInfo.setDatabaseInfoId(columnInfo.getDatabaseInfoId());
            existingColumnInfo.setColumnName(columnInfo.getColumnName());
            existingColumnInfo.setColumnComment(columnInfo.getColumnComment());
            existingColumnInfo.setColumnDescription(columnInfo.getColumnDescription());
            existingColumnInfo.setDataType(columnInfo.getDataType());
            existingColumnInfo.setDataTypeComment(columnInfo.getDataTypeComment());
            existingColumnInfo.setIsCurrent(columnInfo.getIsCurrent());
            columnInfoRepository.updateById(existingColumnInfo);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从数据库中删除列。
     * @param id 要删除的列的ID
     * @return 如果列成功删除则返回true，否则返回false
     */
    public boolean delete(int id) {
        ColumnInfo columnInfo = columnInfoRepository.selectById(id);
        if (columnInfo != null) {
            columnInfoRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }


    /**
     * 检索包含表信息的所有列。
     * @return ColumnInfo列表
     */
    public List<ColumnInfo> getAllColumnInfoWithTableInfo() {
        return columnInfoRepository.selectAllWithTableName();
    }
    /**
     * 根据数据库ID检索包含表信息的所有列。
     * @param databaseId 数据库ID
     * @return ColumnInfo列表
     */
    public List<ColumnInfo> getAllByDatabaseInfoId(int databaseId) {
        return columnInfoRepository.getAllByDatabaseInfoId(databaseId);
    }
}
