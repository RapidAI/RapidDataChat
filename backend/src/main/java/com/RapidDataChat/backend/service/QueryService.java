package com.RapidDataChat.backend.service;

import com.RapidDataChat.backend.model.DatabaseInfo;
import com.RapidDataChat.backend.model.Query;
import com.RapidDataChat.backend.repository.QueryRepository;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static com.RapidDataChat.backend.util.DatabaseUtil.createDataSource;

@Service
public class QueryService {

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private DatabaseInfoService databaseInfoService;

    /**
     * 执行查询中的SQL语句。
     *
     * @param query 要执行的查询记录
     * @return 执行结果
     */
    public ApiResponse<Query> executeQuery(Query query) {
        DatabaseInfo databaseInfo = databaseInfoService.findById(query.getDatabaseInfoId());
        if (databaseInfo == null) {
            return new ApiResponse<>(null, "未找到数据库配置信息", false);
        }

        long startTime = System.currentTimeMillis();
        DataSource dataSource = null;
        try {
            dataSource = createDataSource(databaseInfo);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Map<String, Object>> result;
            if (isSelectQuery(query.getSqlText())) {
                result = jdbcTemplate.queryForList(query.getSqlText());
            } else {
                int updateCount = jdbcTemplate.update(query.getSqlText());
                result = List.of(Map.of("Affected Rows", updateCount));
            }
            long endTime = System.currentTimeMillis();

            query.setResponseText(convertResultToMarkdown(result));
            query.setExecutionTime((endTime - startTime) / 1000.0f);
            query.setSuccess(true);
            query.setExecutedAt(new Date());

            queryRepository.insert(query);
            return new ApiResponse<>(query, "执行SQL成功", true);
        } catch (Exception e) {
            query.setSuccess(false);
            query.setResponseText(e.getCause().getMessage());
            query.setExecutionTime((System.currentTimeMillis() - startTime) / 1000.0f);
            query.setExecutedAt(new Date());
            queryRepository.insert(query);
            return new ApiResponse<>(query, "执行SQL出错", true);
        } finally {
        if (dataSource != null) {
            try {
                dataSource.getConnection().close();
            } catch (SQLException e) {
                // 处理关闭连接时的异常，但不影响返回结果
            }
        }
    }
    }

    private static boolean isSelectQuery(String sql) {
        return sql.trim().toLowerCase().startsWith("select");
    }


    private String convertResultToMarkdown(List<Map<String, Object>> result) {
        if (result.isEmpty()) {
            return "查询无结果";
        }

        StringBuilder markdown = new StringBuilder();
        // 处理表头
        Map<String, Object> firstRow = result.get(0);
        StringJoiner headerJoiner = new StringJoiner(" | ", "| ", " |");
        StringJoiner separatorJoiner = new StringJoiner(" | ", "| ", " |");
        for (String column : firstRow.keySet()) {
            headerJoiner.add(column);
            separatorJoiner.add(":---:");
        }
        markdown.append(headerJoiner.toString()).append("\n");
        markdown.append(separatorJoiner.toString()).append("\n");

        // 处理表格内容
        for (Map<String, Object> row : result) {
            StringJoiner rowJoiner = new StringJoiner(" | ", "| ", " |");
            for (Object value : row.values()) {
                rowJoiner.add(value == null ? "" : value.toString());
            }
            markdown.append(rowJoiner.toString()).append("\n");
        }

        return markdown.toString();
    }

    /**
     * 检索所有查询记录。
     *
     * @return 查询记录列表
     */
    public List<Query> findAll() {
        return queryRepository.selectList(null);
    }

    /**
     * 获取按创建时间倒序排列的用户查询记录列表。
     *
     * @param userId 用户ID
     * @return 按创建时间倒序排列的查询记录列表
     */
    public List<Query> getQuerysByUserId(int userId) {
        return queryRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 根据ID查找单个查询记录。
     *
     * @param id 查询记录的ID
     * @return 查询记录实体，如果未找到返回null
     */
    public Query findById(int id) {
        return queryRepository.selectById(id);
    }

    /**
     * 保存新的查询记录到数据库。
     *
     * @param query 要保存的查询记录实体
     * @return 保存的查询记录对象
     */
    public Query save(Query query) {
        queryRepository.insert(query);
        return query;
    }

    /**
     * 更新数据库中的现有查询记录。
     *
     * @param query 带有更新数据的查询记录实体
     * @param id    要更新的查询记录的ID
     * @return 如果更新成功返回true，否则返回false
     */
    public boolean update(Query query, int id) {
        Query existingQuery = queryRepository.selectById(id);
        if (existingQuery != null) {
            existingQuery.setSessionId(query.getSessionId());
            existingQuery.setUserId(query.getUserId());
            existingQuery.setQueryText(query.getQueryText());
            existingQuery.setSqlText(query.getSqlText());
            existingQuery.setResponseText(query.getResponseText());
            existingQuery.setExecutionTime(query.getExecutionTime());
            existingQuery.setSuccess(query.getSuccess());
            existingQuery.setExecutedAt(query.getExecutedAt());
            queryRepository.updateById(existingQuery);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从数据库中删除指定的查询记录。
     *
     * @param id 要删除的查询记录的ID
     * @return 如果成功删除则返回true，否则返回false
     */
    public boolean delete(int id) {
        return queryRepository.deleteById(id) > 0;
    }
}