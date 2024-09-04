package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.Model;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface ModelRepository extends BaseMapper<Model> {

/**
     * 根据模型名称查找AI模型。
     * @param modelName 模型名称
     * @return 与模型名称匹配的AI模型
     */
    @Select("SELECT * FROM model WHERE model_name = #{modelName}")
    Model findByModelName(String modelName);
}
