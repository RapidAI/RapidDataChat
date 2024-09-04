package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.QueryVector;
import com.RapidDataChat.backend.service.QueryVectorService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queryVectors") // RESTful API 的标准实践是使用复数形式
public class QueryVectorController {

    @Autowired
    private QueryVectorService queryVectorService;

    /**
     * 获取所有向量信息。
     *
     * @return ApiResponse 包含所有向量信息列表和操作结果
     */
    @PostMapping("/getAll")
    public ApiResponse<List<QueryVector>> getAllQueryVectors() {
        List<QueryVector> queryVectors = queryVectorService.findAll();
        return new ApiResponse<>(queryVectors, "获取所有向量信息", true);
    }

    /**
     * 根据 databaseInfoId 获取所有向量信息。
     *
     * @param queryVector 数据库信息ID
     * @return ApiResponse 包含所有向量信息列表和操作结果
     */
    @PostMapping("/getAllByDatabaseId")
    public ApiResponse<List<QueryVector>> getAllByDatabaseId(@RequestBody QueryVector queryVector) {
        List<QueryVector> queryVectors = queryVectorService.findAllByDatabaseInfoId(queryVector);
        return new ApiResponse<>(queryVectors, "获取所有向量信息", true);
    }


    /**
     * 根据向量ID获取单个向量信息。
     *
     * @param queryVector 向量ID
     * @return ApiResponse 包含单个向量信息和操作结果
     */
    @PostMapping("/getById")
    public ApiResponse<QueryVector> getQueryVectorById(@RequestBody QueryVector queryVector) {
        QueryVector newQueryVector = queryVectorService.findById(queryVector.getQueryVectorId());
        if (newQueryVector == null) {
            return new ApiResponse<>(null, "未找到向量信息", true);
        }
        return new ApiResponse<>(newQueryVector, "获取向量信息", true);
    }

    /**
     * 添加新的向量。
     *
     * @param queryVector 要添加的向量信息
     * @return ApiResponse 包含被添加的向量信息和操作结果
     */
    @PostMapping("/add")
    public ApiResponse<QueryVector> addQueryVector(@RequestBody QueryVector queryVector) {
        QueryVector savedQueryVector = queryVectorService.save(queryVector);
        return new ApiResponse<>(savedQueryVector, "向量已添加", true);
    }

    /**
     * 更新现有向量信息。
     *
     * @param queryVector 包含向量ID和更新后的向量信息的对象
     * @return ApiResponse 包含更新后的向量信息和操作结果
     */
    @PostMapping("/update")
    public ApiResponse<QueryVector> updateQueryVector(@RequestBody QueryVector queryVector) {
        QueryVector updatedQueryVector = queryVectorService.update(queryVector, queryVector.getQueryVectorId());
        if (updatedQueryVector == null) {
            return new ApiResponse<>(null, "未找到向量信息", true);
        }
        return new ApiResponse<>(updatedQueryVector, "向量信息已更新", true);
    }

    /**
     * 删除向量。
     *
     * @param queryVector 向量ID
     * @return ApiResponse 包含删除操作的结果消息
     */
    @PostMapping("/delete")
    public ApiResponse<String> deleteQueryVector(@RequestBody QueryVector queryVector) {
        boolean isDeleted = queryVectorService.delete(queryVector.getQueryVectorId());
        if (!isDeleted) {
            return new ApiResponse<>("", "删除向量信息时出错，向量ID：" + queryVector.getQueryVectorId(), false);
        }
        return new ApiResponse<>("向量已成功删除", "向量已删除", true);
    }

    /**
     * 根据输入文本进行相似性检索。
     *
     * @param queryVector 包含查询文本和返回结果数量限制的请求
     * @return ApiResponse 包含相似的向量列表和操作结果
     */
    @PostMapping("/searchByVectorAndDatabaseInfoId")
    public ApiResponse<List<QueryVector>> searchByVectorAndDatabaseInfoId(@RequestBody QueryVector queryVector) {
        List<QueryVector> queryVectors = queryVectorService.searchByVectorAndDatabaseInfoId(queryVector, 5);
        return new ApiResponse<>(queryVectors, "相似向量检索结果", true);
    }

    /**
     * 批量导入向量信息。
     *
     * @param queryVectors 包含多个向量信息的列表
     * @return ApiResponse 包含批量导入的结果和操作结果
     */
    @PostMapping("/batchImport")
    public ApiResponse<List<QueryVector>> batchImportQueryVectors(@RequestBody List<QueryVector> queryVectors) {
        List<QueryVector> savedQueryVectors = queryVectorService.batchSave(queryVectors);
        return new ApiResponse<>(savedQueryVectors, "向量批量导入成功", true);
    }
}