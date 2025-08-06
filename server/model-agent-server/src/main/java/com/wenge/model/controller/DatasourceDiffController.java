package com.wenge.model.controller;

import com.wenge.model.service.DatasourceDiffService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/dataDiff")
@Slf4j
@Api("对比数据库")
public class DatasourceDiffController {

    @Autowired
    private DatasourceDiffService datasourceDiffService;

    /**
     * 导出数据库表结构
     */
    @GetMapping("/exportDatasource")
    public void exportDatasource(HttpServletResponse response, String key, String schema) {
        datasourceDiffService.exportDatasource(response, key, schema);
    }

    /**
     * 导出数据库表结构差异点
     */
    @GetMapping("/exportDdl")
    public void exportDdl(MultipartFile file, HttpServletResponse response, String key, String schema) {
        datasourceDiffService.exportDdl(file, response, key, schema);
    }
}
