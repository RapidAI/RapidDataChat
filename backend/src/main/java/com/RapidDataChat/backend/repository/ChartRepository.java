package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.Chart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChartRepository extends BaseMapper<Chart> {

    /**
     * 根据用户ID查找图表配置。
     * @param userId 用户ID
     * @return 与用户ID匹配的所有图表配置列表
     */
    @Select("SELECT * FROM charts WHERE user_id = #{userId}")
    List<Chart> findByUserId(int userId);

}
