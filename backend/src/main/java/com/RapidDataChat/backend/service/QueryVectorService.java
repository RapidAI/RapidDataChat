package com.RapidDataChat.backend.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.RapidDataChat.backend.model.QueryVector;
import com.RapidDataChat.backend.repository.QueryVectorRepository;
import com.pgvector.PGvector;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryVectorService {

    @Value("${embedding.api.url}")
    private String embeddingApiUrl;

    @Autowired
    private QueryVectorRepository queryVectorRepository;

    /**
     * 查询所有向量。
     *
     * @return 向量列表
     */
    public List<QueryVector> findAll() {
        return queryVectorRepository.selectList(null);
    }

    /**
     * 根据 databaseInfoId 查询所有向量。
     *
     * @param queryVector 数据库信息ID
     * @return 向量列表
     */
    public List<QueryVector> findAllByDatabaseInfoId(QueryVector queryVector) {
        return queryVectorRepository.selectByDatabaseInfoId(queryVector.getDatabaseInfoId());
    }


    /**
     * 根据向量ID查询向量。
     *
     * @param id 向量ID
     * @return 对应的向量信息，如果不存在返回null
     */
    public QueryVector findById(int id) {
        return queryVectorRepository.selectById(id);
    }

    /**
     * 保存新向量信息到数据库。
     *
     * @param queryVector 向量信息
     * @return 保存后的向量信息
     */
    public QueryVector save(QueryVector queryVector) {
        // 获取queryText
        String queryText = queryVector.getQueryText();

        // 调用embedding接口
        PGvector embeddingVector = getEmbeddingVector(queryText);
        queryVector.setEmbeddingVector(embeddingVector);

        // 保存到数据库
        queryVectorRepository.insert(queryVector);
        return queryVector;
    }

    /**
     * 调用指定API获取向量。
     *
     * @param queryText 查询文本
     * @return 获取的向量
     */
    @SneakyThrows
    private PGvector getEmbeddingVector(String queryText) {
        String requestBody = JSONUtil.createObj()
                .set("model", "bge-base-zh-v1.5")
                .set("input", queryText)
                .toString();

        HttpResponse response = HttpRequest.post(embeddingApiUrl)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .execute();

        if (response.getStatus() != 200) {
            throw new RuntimeException(
                    String.format("请求失败，状态码：%d，响应体：%s", response.getStatus(), response.body()));
        }

        JSONObject jsonResponse = JSONUtil.parseObj(response.body());
        List<?> embeddingList = jsonResponse.getByPath("data[0].embedding", List.class);

        return new PGvector(embeddingList.toString());
    }

    /**
     * 更新已存在的向量信息。
     *
     * @param queryVector 新的向量信息
     * @param id          向量ID
     * @return 更新后的向量信息，如果向量不存在则返回null
     */
    public QueryVector update(QueryVector queryVector, int id) {
        QueryVector existingVector = queryVectorRepository.selectById(id);
        if (existingVector != null) {
            if (queryVector.getDatabaseInfoId() != null) {
                existingVector.setDatabaseInfoId(queryVector.getDatabaseInfoId());
            }
            if (queryVector.getSessionId() != null) {
                existingVector.setSessionId(queryVector.getSessionId());
            }
            if (queryVector.getUserId() != null) {
                existingVector.setUserId(queryVector.getUserId());
            }
            if (!queryVector.getQueryText().equals(existingVector.getQueryText())) {
                PGvector embeddingVector = getEmbeddingVector(queryVector.getQueryText());
                queryVector.setEmbeddingVector(embeddingVector);
                existingVector.setEmbeddingVector(embeddingVector);
                existingVector.setQueryText(queryVector.getQueryText());
            }
            if (queryVector.getResultText() != null) {
                existingVector.setResultText(queryVector.getResultText());
            }
            if (queryVector.getSuccess() != null) {
                existingVector.setSuccess(queryVector.getSuccess());
            }
            queryVectorRepository.updateById(existingVector);
            return existingVector;
        } else {
            return null;
        }
    }

    /**
     * 根据向量ID删除向量。
     *
     * @param id 向量ID
     * @return 如果成功删除返回true，否则返回false
     */
    public boolean delete(int id) {
        int rows = queryVectorRepository.deleteById(id);
        return rows > 0;
    }

    /**
     * 根据向量进行相似性检索
     *
     * @param queryVector 查询文本
     * @param limit     返回的结果数量限制
     * @return 相似的向量列表
     */
    public List<QueryVector> searchByVectorAndDatabaseInfoId(QueryVector queryVector, int limit) {
        queryVector.setEmbeddingVector(getEmbeddingVector(queryVector.getQueryText()));
        return queryVectorRepository.searchByVectorAndDatabaseInfoId(queryVector, limit);
    }

    /**
     * 批量保存向量信息到数据库。
     *
     * @param queryVectors 向量信息列表
     * @return 保存后的向量信息列表
     */
    public List<QueryVector> batchSave(List<QueryVector> queryVectors) {
        for (QueryVector queryVector : queryVectors) {
            // 获取queryText
            String queryText = queryVector.getQueryText();

            // 调用embedding接口
            PGvector embeddingVector = getEmbeddingVector(queryText);
            queryVector.setEmbeddingVector(embeddingVector);
        }

        // 批量插入到数据库
        queryVectorRepository.batchInsert(queryVectors);
        return queryVectors;
    }


}
