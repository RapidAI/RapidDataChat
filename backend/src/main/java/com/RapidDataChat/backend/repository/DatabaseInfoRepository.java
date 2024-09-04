package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.DatabaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DatabaseInfoRepository extends BaseMapper<DatabaseInfo> {

    /**
     * 根据 ID 查找 DatabaseInfo 记录。
     *
     * @param databaseInfoId 数据库信息ID
     * @return DatabaseInfo 对象
     */
    @Select("SELECT * FROM database_info WHERE database_info_id = #{databaseInfoId}")
    DatabaseInfo selectById(@Param("databaseInfoId") Integer databaseInfoId);
}
