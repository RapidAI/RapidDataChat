package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.Model;
import com.RapidDataChat.backend.service.ModelService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/model") // RESTful API 的标准实践是使用复数形式
public class ModelController {

    @Autowired
    private ModelService modelService;

    /**
     * 获取所有AI模型信息。
     * @return ApiResponse 包含所有AI模型信息列表和操作结果
     */
    @PostMapping("/getAll")
    public ApiResponse<List<Model>> getAllModels() {
        List<Model> models = modelService.findAll();
        return new ApiResponse<>(models, "获取所有AI模型信息", true);
    }

    /**
     * 根据AI模型ID获取单个AI模型信息。
     * @param model AI模型ID
     * @return ApiResponse 包含单个AI模型信息和操作结果
     */
    @PostMapping("/getById")
    public ApiResponse<Model> getModelById(@RequestBody Model model) {
        Model newModel = modelService.findById(model.getModelId());
        if (newModel == null) {
            return new ApiResponse<>(null, "未找到AI模型信息", true);
        }
        return new ApiResponse<>(newModel, "获取AI模型信息", true);
    }

    /**
     * 添加新的AI模型。
     * @param model 要添加的AI模型信息
     * @return ApiResponse 包含被添加的AI模型信息和操作结果
     */
    @PostMapping("/add")
    public ApiResponse<Model> addModel(@RequestBody Model model) {
        Model savedModel = modelService.save(model);
        return new ApiResponse<>(savedModel, "AI模型已添加", true);
    }

    /**
     * 更新现有AI模型信息。
     * @param model 包含AI模型ID和更新后的AI模型信息的对象
     * @return ApiResponse 包含更新后的AI模型信息和操作结果
     */
    @PostMapping("/update")
    public ApiResponse<Model> updateModel(@RequestBody Model model) {
        Model updatedModel = modelService.update(model, model.getModelId());
        if (updatedModel == null) {
            return new ApiResponse<>(null, "未找到AI模型信息", true);
        }
        return new ApiResponse<>(updatedModel, "AI模型信息已更新", true);
    }

    /**
     * 删除AI模型。
     * @param model AI模型ID
     * @return ApiResponse 包含删除操作的结果消息
     */
    @PostMapping("/delete")
    public ApiResponse<String> deleteModel(@RequestBody Model model) {
        boolean isDeleted = modelService.delete(model.getModelId());
        if (!isDeleted) {
            return new ApiResponse<>("", "删除AI模型信息时出错，AI模型ID：" + model.getModelId(), false);
        }
        return new ApiResponse<>("AI模型已成功删除", "AI模型已删除", true);
    }
}
