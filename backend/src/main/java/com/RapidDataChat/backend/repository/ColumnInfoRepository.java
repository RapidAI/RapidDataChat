package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.ColumnInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ColumnInfoRepository extends BaseMapper<ColumnInfo> {

    /**
     * 根据 tableInfoId 和 columnName 查找 ColumnInfo 记录。
     * @param tableInfoId 关联的表信息ID
     * @param columnName 列名称
     * @return 匹配的 ColumnInfo 对象，如果未找到则返回 null
     */
    @Select("SELECT * FROM column_info WHERE table_info_id = #{tableInfoId} AND column_name = #{columnName}")
    ColumnInfo selectByTableInfoIdAndColumnName(@Param("tableInfoId") int tableInfoId, @Param("columnName") String columnName);

    /**
     * 根据 tableInfoId 查找所有 ColumnInfo 记录。
     * @param tableInfoId 关联的表信息ID
     * @return ColumnInfo 列表
     */
    @Select("SELECT * FROM column_info WHERE table_info_id = #{tableInfoId}")
    List<ColumnInfo> selectByTableInfoId(@Param("tableInfoId") int tableInfoId);

    /**
     * 查询所有 ColumnInfo 记录，并同时加载关联的 tableInfo 和 databaseInfo 信息。
     * @return 包含 tableInfo 和 databaseInfo 信息的 ColumnInfo 列表
     */
    @Select("SELECT c.*, " +
            "t.table_info_id AS t_table_info_id, t.database_info_id AS t_database_info_id, t.table_name, t.table_comment, t.table_description, t.create_time AS table_create_time, t.update_time AS table_update_time, " +
            "d.database_info_id AS d_database_info_id, d.database_name, d.host, d.port, d.username, d.password, d.auth_method, d.database_type, d.create_time AS database_create_time, d.update_time AS database_update_time, d.sync_time, d.analyze_time " +
            "FROM column_info c " +
            "JOIN table_info t ON c.table_info_id = t.table_info_id " +
            "JOIN database_info d ON t.database_info_id = d.database_info_id")
    @Results({
            @Result(column = "column_info_id", property = "columnInfoId"),
            @Result(column = "table_info_id", property = "tableInfoId"),
            @Result(column = "database_info_id", property = "databaseInfoId"),
            @Result(column = "column_name", property = "columnName"),
            @Result(column = "column_comment", property = "columnComment"),
            @Result(column = "column_description", property = "columnDescription"),
            @Result(column = "data_type", property = "dataType"),
            @Result(column = "data_type_comment", property = "dataTypeComment"),
            @Result(column = "is_current", property = "isCurrent"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "t_table_info_id", property = "tableInfo.tableInfoId"),
            @Result(column = "t_database_info_id", property = "tableInfo.databaseInfoId"),
            @Result(column = "table_name", property = "tableInfo.tableName"),
            @Result(column = "table_comment", property = "tableInfo.tableComment"),
            @Result(column = "table_description", property = "tableInfo.tableDescription"),
            @Result(column = "table_create_time", property = "tableInfo.createTime"),
            @Result(column = "table_update_time", property = "tableInfo.updateTime"),
            @Result(column = "d_database_info_id", property = "tableInfo.databaseInfo.databaseInfoId"),
            @Result(column = "database_name", property = "tableInfo.databaseInfo.databaseName"),
            @Result(column = "host", property = "tableInfo.databaseInfo.host"),
            @Result(column = "port", property = "tableInfo.databaseInfo.port"),
            @Result(column = "username", property = "tableInfo.databaseInfo.username"),
            @Result(column = "password", property = "tableInfo.databaseInfo.password"),
            @Result(column = "auth_method", property = "tableInfo.databaseInfo.authMethod"),
            @Result(column = "database_type", property = "tableInfo.databaseInfo.databaseType"),
            @Result(column = "database_create_time", property = "tableInfo.databaseInfo.createTime"),
            @Result(column = "database_update_time", property = "tableInfo.databaseInfo.updateTime"),
            @Result(column = "sync_time", property = "tableInfo.databaseInfo.syncTime"),
            @Result(column = "analyze_time", property = "tableInfo.databaseInfo.analyzeTime")
    })
    List<ColumnInfo> selectAllWithTableName();

    /**
     * 根据 databaseInfoId 查找所有 ColumnInfo 记录，并同时加载关联的 tableInfo 和 databaseInfo 信息。
     * @param databaseInfoId 关联的数据库信息ID
     * @return 包含 tableInfo 和 databaseInfo 信息的 ColumnInfo 列表
     */
    @Select("SELECT c.*, " +
            "t.table_info_id AS t_table_info_id, t.database_info_id AS t_database_info_id, t.table_name, t.table_comment, t.table_description, t.create_time AS table_create_time, t.update_time AS table_update_time, " +
            "d.database_info_id AS d_database_info_id, d.database_name, d.host, d.port, d.username, d.password, d.auth_method, d.database_type, d.create_time AS database_create_time, d.update_time AS database_update_time, d.sync_time, d.analyze_time " +
            "FROM column_info c " +
            "JOIN table_info t ON c.table_info_id = t.table_info_id " +
            "JOIN database_info d ON t.database_info_id = d.database_info_id " +
            "WHERE d.database_info_id = #{databaseInfoId}")
    @Results({
            @Result(column = "column_info_id", property = "columnInfoId"),
            @Result(column = "table_info_id", property = "tableInfoId"),
            @Result(column = "database_info_id", property = "databaseInfoId"),
            @Result(column = "column_name", property = "columnName"),
            @Result(column = "column_comment", property = "columnComment"),
            @Result(column = "column_description", property = "columnDescription"),
            @Result(column = "data_type", property = "dataType"),
            @Result(column = "data_type_comment", property = "dataTypeComment"),
            @Result(column = "is_current", property = "isCurrent"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "t_table_info_id", property = "tableInfo.tableInfoId"),
            @Result(column = "t_database_info_id", property = "tableInfo.databaseInfoId"),
            @Result(column = "table_name", property = "tableInfo.tableName"),
            @Result(column = "table_comment", property = "tableInfo.tableComment"),
            @Result(column = "table_description", property = "tableInfo.tableDescription"),
            @Result(column = "table_create_time", property = "tableInfo.createTime"),
            @Result(column = "table_update_time", property = "tableInfo.updateTime"),
            @Result(column = "d_database_info_id", property = "tableInfo.databaseInfo.databaseInfoId"),
            @Result(column = "database_name", property = "tableInfo.databaseInfo.databaseName"),
            @Result(column = "host", property = "tableInfo.databaseInfo.host"),
            @Result(column = "port", property = "tableInfo.databaseInfo.port"),
            @Result(column = "username", property = "tableInfo.databaseInfo.username"),
            @Result(column = "password", property = "tableInfo.databaseInfo.password"),
            @Result(column = "auth_method", property = "tableInfo.databaseInfo.authMethod"),
            @Result(column = "database_type", property = "tableInfo.databaseInfo.databaseType"),
            @Result(column = "database_create_time", property = "tableInfo.databaseInfo.createTime"),
            @Result(column = "database_update_time", property = "tableInfo.databaseInfo.updateTime"),
            @Result(column = "sync_time", property = "tableInfo.databaseInfo.syncTime"),
            @Result(column = "analyze_time", property = "tableInfo.databaseInfo.analyzeTime")
    })
    List<ColumnInfo> getAllByDatabaseInfoId(@Param("databaseInfoId") int databaseInfoId);

    /**
     * 根据 tableInfoId 删除所有 ColumnInfo 记录。
     * @param tableInfoId 关联的表信息ID
     */
    @Delete("DELETE FROM column_info WHERE table_info_id = #{tableInfoId}")
    void deleteByTableInfoId(@Param("tableInfoId") int tableInfoId);
}
