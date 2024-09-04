package com.RapidDataChat.backend.service;

import com.RapidDataChat.backend.model.Chart;
import com.RapidDataChat.backend.repository.ChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChartService {

    @Autowired
    private ChartRepository chartRepository;

    /**
     * 从数据库中检索所有图表配置。
     * @return 图表配置列表
     */
    public List<Chart> findAll() {
        return chartRepository.selectList(null);
    }

    /**
     * 根据用户ID检索图表配置。
     * @param userId 用户的ID
     * @return 图表配置列表
     */
    public List<Chart> findByUserId(int userId) {
        return chartRepository.findByUserId(userId);
    }

    /**
     * 根据ID查找图表配置。
     * @param id 图表配置的ID
     * @return 对应的图表配置，如果不存在返回null
     */
    public Chart findById(int id) {
        return chartRepository.selectById(id);
    }

    /**
     * 将新图表配置保存到数据库。
     * @param chart 要保存的图表配置实体
     * @return 保存的图表配置对象
     */
    public Chart save(Chart chart) {
        chartRepository.insert(chart);
        return chart;
    }

    /**
     * 在数据库中更新现有图表配置。
     * @param chart 带有更新字段的图表配置实体
     * @param id 要更新的图表配置的ID
     * @return 如果更新成功返回true，否则返回false
     */
    public boolean update(Chart chart, int id) {
        Chart existingChart = chartRepository.selectById(id);
        if (existingChart != null) {
            existingChart.setUserId(chart.getUserId());
            existingChart.setQueryId(chart.getQueryId());
            existingChart.setChartConfig(chart.getChartConfig());
            chartRepository.updateById(existingChart);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从数据库中删除图表配置。
     * @param id 要删除的图表配置的ID
     * @return 如果图表配置成功删除则返回true，否则返回false
     */
    public boolean delete(int id) {
        int rows = chartRepository.deleteById(id);
        return rows > 0;
    }
}
