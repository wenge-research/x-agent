package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.wenge.model.dto.result.TableResult;
import com.wenge.model.service.DatasourceDiffService;
import com.wenge.model.utils.DateUtil;
import com.wg.appframe.core.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DatasourceDiffServiceImpl implements DatasourceDiffService {

    private final String USER_DIR = System.getProperty("user.dir");
    private final String TABLE_PATH = USER_DIR + "/table";
    private final String UNZIP_TABLE_PATH = USER_DIR + "/un_table";
    private final String NEW_TABLE_PATH = USER_DIR + "/new_table";

    @Override
    public void exportDatasource(HttpServletResponse response, String key, String schema) {
        if (StringUtils.isBlank(key) || key.trim().length() != 16 || StringUtils.isBlank(schema)) {
            try {
                response.setContentType("application/json; charset=UTF-8");
                response.setStatus(200);
                response.getWriter().println("key为空或者长度不为16位,schema不能为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("======>导出失败:{}", "key为空或者长度不为16位");
            return;
        }
        // 获取表结构
        List<TableResult> tableResults = getTable(schema);
        if (CollectionUtil.isEmpty(tableResults)) {
            try {
                response.setContentType("application/json; charset=UTF-8");
                response.setStatus(200);
                response.getWriter().println("空数据库");
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("======>空数据库");
            return;
        }

        // String path = USER_DIR + "/table";
        File file = new File(TABLE_PATH);
        // 创建目录
        if (!file.exists()) {
            file.mkdirs();
        }
        AES aes = SecureUtil.aes(key.getBytes());

        Vector<File> allFile = new Vector<>();
        tableResults.stream().parallel().forEach(table -> {
            String ddlFile = TABLE_PATH + "/" + table.getTableName() + "_ddl.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ddlFile))) {
                allFile.add(new File(ddlFile));
                // 写入文本内容
                String decryptStr = aes.encryptBase64(table.getTableDdl());
                writer.write(decryptStr);
                writer.flush();
                log.info("======>导出表结构:{}", table.getTableName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            String columetFile = TABLE_PATH + "/" + table.getTableName() + "_column.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(columetFile))) {
                allFile.add(new File(columetFile));
                // 写入文本内容
                String decryptStr = aes.encryptBase64(JSONUtil.toJsonStr(table.getColumnList()));
                writer.write(decryptStr);
                writer.flush();
                log.info("======>导出表字段:{}", table.getTableName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
            File zip = ZipUtil.zip(TABLE_PATH, USER_DIR + "/table.zip");

            String currentTime = DateUtil.getCurrentDateCn();
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"table_" + currentTime + ".zip\"");
            // 将 ZIP 文件写入响应输出流
            try (OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(FileUtil.readBytes(zip));
            }
            zip.delete();

            for (File file1 : allFile) {
                file1.delete();
            }
            Path paths = Paths.get(TABLE_PATH);
            Files.delete(paths);
            System.out.println("文件删除成功");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件删除失败");
        }

    }

    @Override
    public void exportDdl(MultipartFile multipartFile, HttpServletResponse response, String key, String schema) {
        if (null == multipartFile || StringUtils.isBlank(key) || key.trim().length() != 16 || StringUtils.isBlank(schema)) {
            try {
                response.setContentType("application/json; charset=UTF-8");
                response.setStatus(200);
                response.getWriter().println("key为空或者长度不为16位,schema不能为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("======>导出失败:{}", "key为空或者长度不为16位");
            return;
        }
        List<TableResult> tableResults = getTable(schema);

        try {
            // 将MultipartFile转换为File对象
            File tempFile = File.createTempFile("temp", null);
            multipartFile.transferTo(tempFile);

            // 使用Hutool的ZipUtil.unzip方法解压文件
            File file = new File(UNZIP_TABLE_PATH);
            ZipUtil.unzip(tempFile, file);
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                return;
            }

            Map<String, String> tableDdl = Maps.newHashMap();
            Map<String, String> columeDdl = Maps.newHashMap();
            for (File file1 : files) {
                String content = FileUtil.readString(file1, StandardCharsets.UTF_8);

                String tableName = file1.getName();
                tableName = tableName.replace("_ddl.txt", "")
                        .replace("_column.txt", "");
                if (file1.getName().contains("_ddl.txt")) {
                    tableDdl.put(tableName, content);
                } else if (file1.getName().contains("_column.txt")) {
                    columeDdl.put(tableName, content);
                }
                file1.delete();
            }

            List<TableResult> tableResults1 = Lists.newArrayList();
            AES aes = SecureUtil.aes(key.getBytes());
            tableDdl.forEach((k, v)->{
                TableResult tableResult = new TableResult();
                tableResult.setTableName(k);
                String ddl = aes.decryptStr(v);
                tableResult.setTableDdl(ddl);

                String columnStr = columeDdl.get(k);
                if (StringUtils.isNotBlank(columnStr)) {
                    String columnJson = aes.decryptStr(columnStr);

                    JSONArray array = JSONUtil.parseArray(columnJson);
                    if (CollectionUtil.isNotEmpty(array)) {
                        List<TableResult.Column> list = array.toList(TableResult.Column.class);
                        tableResult.setColumnList(list);
                    }
                }
                tableResults1.add(tableResult);
            });

            // 比对
            File file1 = compareTable(tableResults1, tableResults);

            String fileName = "sql.sql"; // 客户端下载的文件名
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // 设置文件的 MIME 类型（可选）
            String mimeType = "application/octet-stream"; // 默认二进制流
            response.setContentType(mimeType);

            // 设置文件大小（可选）
            response.setContentLength((int) file1.length());

            // 将文件内容写入响应输出流
            try (FileInputStream fileInputStream = new FileInputStream(file1);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "文件下载失败");
            } finally {
                file1.delete();
            }

            System.out.println(tableResults1);
            // 删除临时文件
            tempFile.delete();
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 对比表，补充缺失表
    }


    /**
     * 获取表结构
     * @param schema
     * @return
     */
    private List<TableResult> getTable(String schema) {
        // mysql 版本
        // 查询所有表
        List<Row> rows = Db.selectListBySql("SELECT TABLE_NAME, COLUMN_NAME,DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA ='" + schema + "'");
        Map<String, List<TableResult.Column>> listMap = rows.stream()
                .collect(Collectors.groupingBy(p -> p.getString("TABLE_NAME"), Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().map(t -> {
                    TableResult.Column column = new TableResult.Column();
                    column.setColumnName(t.getString("COLUMN_NAME"));
                    column.setColumnType(t.getString("DATA_TYPE"));
                    return column;
                }).collect(Collectors.toList()))));
        List<TableResult> tableResults = Lists.newArrayList();

        // 封装表结构和字段
        listMap.forEach((k, v) -> {
            List<Row> rows1 = Db.selectListBySql("SHOW CREATE TABLE " + schema + "." + k);
            TableResult tableResult = new TableResult();
            tableResult.setTableName(k);
            tableResult.setTableDdl(rows1.get(0).getString("Create Table"));
            tableResult.setColumnList(v);
            tableResults.add(tableResult);
        });

        return tableResults;
    }

    /**
     * 对比表，补充缺失表
     */
    private File compareTable(List<TableResult> outTable, List<TableResult> currentTable) {

        StringBuffer sb = new StringBuffer();
        File file = new File(NEW_TABLE_PATH);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            List<String> outTables = outTable.stream().map(TableResult::getTableName).collect(Collectors.toList());
            List<String> outTablesCopy = new ArrayList<>(outTables);
            List<String> currentTables = currentTable.stream().map(TableResult::getTableName).collect(Collectors.toList());
            // 取差集
            // 从 result 中移除 listB 中存在的元素
            outTables.removeAll(currentTables);

            // 新增的表：如果outTables不为空，说明还有未创建的表
            if (CollectionUtil.isNotEmpty(outTables)) {
                List<TableResult> newTable = outTable.stream().filter(p -> outTables.contains(p.getTableName())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(newTable)) {
                    String newTableDdl = newTable.stream().map(p->{
                        String tableDdl = p.getTableDdl();
                                return tableDdl.replace(" DEFAULT (now()) ", " DEFAULT CURRENT_TIMESTAMP ");
                            })
                            .collect(Collectors.joining(";\n\n\n"));

                    sb.append(newTableDdl);
                }
            }
            if (StringUtils.isNotBlank(sb.toString())) {
                sb.append(";\n\n\n");
            }
            // 获取创建字段的 sql
            Map<String, List<String>> tableColumeMap = outTable.stream()
                    .filter(p -> !outTables.contains(p.getTableName()))
                    .collect(Collectors.toMap(
                            TableResult::getTableName,
                            p -> {
                                String tableDdl = p.getTableDdl();
                                int startOf = tableDdl.indexOf("(");
                                int endOf = tableDdl.lastIndexOf(")");
                                String substring = tableDdl.substring(startOf + 1, endOf);
                                return Arrays.stream(substring.split(",")).map(t -> {
                                    // `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '应用类型，空-问答助手，workflow-工作流，search-智能搜索'
                                    if (t.contains(" DEFAULT")) {
                                        int i = t.indexOf(" DEFAULT");
                                        if (t.contains(" COMMENT")) {
                                            int i1 = t.indexOf(" COMMENT");
                                            String substring1 = t.substring(i, i1);
                                            substring1 = substring1.replace("(", StringConstant.BLANK);
                                            substring1 = substring1.replace(")", StringConstant.BLANK);
                                            t = t.substring(0, i) + substring1 + t.substring(i1);
                                        } else {
                                            String substring1 = t.substring(i);
                                            substring1 = substring1.replace("(", StringConstant.BLANK);
                                            substring1 = substring1.replace(")", StringConstant.BLANK);
                                            t = t.substring(0, i) + substring1;
                                        }
                                    }
                                    if (t.contains("COMMENT")) {
                                        if (!t.endsWith("'")) {
                                            t = t + "'";
                                        }
                                    }
                                    return "alter table " + p.getTableName() + " add " + t + ";\n\n";
                                }).collect(Collectors.toList());
                            },
                            (k1, k2) -> k1,
                            Maps::newHashMap
                    ));

            // 新增的字段
            List<String> outColumns = outTable.stream()
                    .filter(p -> !outTables.contains(p.getTableName()))
                    .flatMap(p -> {
                        return p.getColumnList().stream().map(t -> p.getTableName() + "@@" + t.getColumnName());
                    }).collect(Collectors.toList());

            // 获取新增字段的列
            List<String> outColumnsCopy = new ArrayList<>(outColumns);

            List<String> currentClomns = currentTable.stream()
                    .flatMap(p -> p.getColumnList().stream()
                            .map(t -> p.getTableName() + "@@" + t.getColumnName()))
                    .collect(Collectors.toList());

            outColumns.removeAll(currentClomns);
            // 获取新增字段的sql
            if (CollectionUtil.isNotEmpty(outColumns)) {
                outColumns.forEach(item -> {
                    String[] split = item.split("@@");
                    String tableName = split[0];
                    String columName = split[1];
                    List<String> createColumSql = tableColumeMap.get(tableName);
                    Optional<String> any = createColumSql.stream()
                            .filter(p -> p.contains("`" + columName.replace("\n", StringConstant.BLANK) + "`"))
                            .findAny();
                    any.ifPresent(sb::append);
                });
            }

            // 删除的字段
            currentClomns.removeAll(outColumnsCopy);
            if (CollectionUtil.isNotEmpty(currentClomns)) {
                currentClomns.stream()
                        .filter(p -> outTablesCopy.contains(p.substring(0, p.indexOf("@@"))))
                        .forEach(item -> {
                            // ALTER TABLE users DROP COLUMN age;
                            String[] split = item.split("@@");
                            String tableName = split[0];
                            String columName = split[1];
                            String dd = "ALTER TABLE " + tableName + " DROP COLUMN `" + columName + "`;\n\n";
                            sb.append(dd);
                        });
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(sb.toString());
                System.out.println("内容已成功写入文件。");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("写入文件时发生错误。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }
}

