package com.wenge.model.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface DatasourceDiffService {

    void exportDatasource(HttpServletResponse response, String key, String schema);

    void exportDdl(MultipartFile file, HttpServletResponse response, String key, String schema);
}
