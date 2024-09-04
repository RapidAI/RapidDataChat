package com.RapidDataChat.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.RapidDataChat.backend.model.Model;
import com.RapidDataChat.backend.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelService {

    @Autowired
    private ModelRepository modelRepository;

    /**
     * 查询所有AI模型。
     *
     * @return AI模型列表
     */
    public List<Model> findAll() {
        return modelRepository.selectList(null);
    }

    /**
     * 根据AI模型ID查询模型。
     *
     * @param id AI模型ID
     * @return 对应的AI模型信息，如果不存在返回null
     */
    public Model findById(int id) {
        return modelRepository.selectById(id);
    }

    /**
     * 根据模型名称查询模型。
     *
     * @param modelName 模型名称
     * @return 对应的AI模型信息，如果不存在返回null
     */
    public Model findByModelName(String modelName) {
        QueryWrapper<Model> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model_name", modelName);
        return modelRepository.selectOne(queryWrapper);
    }

    /**
     * 保存新AI模型信息到数据库。
     *
     * @param model AI模型信息
     * @return 保存后的AI模型信息
     */
    public Model save(Model model) {
        modelRepository.insert(model);
        return model;
    }

    /**
     * 更新已存在的AI模型信息。
     *
     * @param model 新的AI模型信息
     * @param id      AI模型ID
     * @return 更新后的AI模型信息，如果模型不存在则返回null
     */
    public Model update(Model model, int id) {
        Model existingModel = modelRepository.selectById(id);
        if (existingModel != null) {
            if (model.getModelName() != null) {
                existingModel.setModelName(model.getModelName());
            }
            if (model.getApiKey() != null) {
                existingModel.setApiKey(model.getApiKey());
            }
            existingModel.setUseProxy(model.isUseProxy());
            if (model.getProxyHost() != null) {
                existingModel.setProxyHost(model.getProxyHost());
            }
            if (model.getProxyPort() != null) {
                existingModel.setProxyPort(model.getProxyPort());
            }
            if (model.getBaseUrl() != null) {
                existingModel.setBaseUrl(model.getBaseUrl());
            }
            if (model.getModel() != null) {
                existingModel.setModel(model.getModel());
            }
            if (model.getDescription() != null) {
                existingModel.setDescription(model.getDescription());
            }
            modelRepository.updateById(existingModel);
            return existingModel;
        } else {
            return null;
        }
    }

    /**
     * 根据AI模型ID删除模型。
     *
     * @param id AI模型ID
     * @return 如果成功删除返回true，否则返回false
     */
    public boolean delete(int id) {
        int rows = modelRepository.deleteById(id);
        return rows > 0;
    }
}
