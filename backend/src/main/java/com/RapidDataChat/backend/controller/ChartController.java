package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.Chart;
import com.RapidDataChat.backend.service.ChartService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charts")
public class ChartController {

    @Autowired
    private ChartService chartService;

    @PostMapping("/getAll")
    public ApiResponse<List<Chart>> getAllCharts() {
        List<Chart> charts = chartService.findAll();
        return new ApiResponse<>(charts, "获取所有图表配置", true);
    }

    @PostMapping("/getByUserId")
    public ApiResponse<List<Chart>> getChartsByUserId(@RequestBody Chart chart) {
        List<Chart> charts = chartService.findByUserId(chart.getUserId());
        if (charts.isEmpty()) {
            return new ApiResponse<>(null, "未找到用户的图表配置", true);
        }
        return new ApiResponse<>(charts, "获取用户的图表配置", true);
    }

    @PostMapping("/add")
    public ApiResponse<Chart> addChart(@RequestBody Chart chart) {
        Chart savedChart = chartService.save(chart);
        return new ApiResponse<>(savedChart, "图表配置已添加", true);
    }

    @PostMapping("/update")
    public ApiResponse<Chart> updateChart(@RequestBody Chart chart) {
        boolean isUpdated = chartService.update(chart, chart.getChartId());
        if (!isUpdated) {
            return new ApiResponse<>(null, "未找到要更新的图表配置", true);
        }
        return new ApiResponse<>(chart, "图表配置已更新", true);
    }

    @PostMapping("/delete")
    public ApiResponse<String> deleteChart(@RequestBody Chart chart) {
        boolean isDeleted = chartService.delete(chart.getChartId());
        if (!isDeleted) {
            return new ApiResponse<>("", "删除图表配置时出错，ID：" + chart.getChartId(), false);
        }
        return new ApiResponse<>("图表配置已成功删除", "图表配置已删除", true);
    }
}
