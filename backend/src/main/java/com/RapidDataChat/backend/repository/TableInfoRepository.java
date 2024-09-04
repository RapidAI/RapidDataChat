package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.TableInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TableInfoRepository extends BaseMapper<TableInfo> {

    /**
     * 根据 databaseInfoId 查找所有 TableInfo 记录，并按 table_name 排序。
     *
     * @param databaseInfoId 关联的数据库信息ID
     * @return TableInfo 列表
     */
    @Select("SELECT * FROM table_info WHERE database_info_id = #{databaseInfoId} ORDER BY table_name")
    List<TableInfo> selectByDatabaseInfoId(@Param("databaseInfoId") int databaseInfoId);

    /**
     * 根据 databaseInfoId 和 tableName 查找 TableInfo 记录。
     *
     * @param databaseInfoId 关联的数据库信息ID
     * @param tableName      表名称
     * @return 匹配的 TableInfo 对象，如果未找到则返回 null
     */
    @Select("SELECT * FROM table_info WHERE database_info_id = #{databaseInfoId} AND table_name = #{tableName}")
    TableInfo selectByDatabaseInfoIdAndTableName(@Param("databaseInfoId") int databaseInfoId, @Param("tableName") String tableName);

    /**
     * 根据 databaseInfoId 删除所有关联的 TableInfo 记录。
     *
     * @param databaseInfoId 关联的数据库信息ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM table_info WHERE database_info_id = #{databaseInfoId}")
    int deleteByDatabaseInfoId(@Param("databaseInfoId") int databaseInfoId);
}