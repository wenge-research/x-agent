package com.wenge.model.task;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.config.WebSocketServer;
import com.wenge.model.constants.StructureConstant;
import com.wenge.model.entity.ExcelParserInfo;
import com.wenge.model.entity.TableDirectoryInfo;
import com.wenge.model.mapper.ExcelParserInfoMapper;
import com.wenge.model.mapper.TableDirectoryInfoMapper;
import com.wenge.model.utils.ExcelToMySQL;
import com.wenge.model.vo.TableInfoVo;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
/**
 * 把EXCEL解析成为MYSQL数据，建表写数据
 */
@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelParserTask implements Callable<Boolean> {
    private static final String EXCEL_TABLE_PREFIX_ = "structure_excel_";

    private String knowledgeId;
    private MultipartFile file;
    private InputStream inputStream;
    private ExcelParserInfo excelParserInfo;
    private TableDirectoryInfoMapper tableDirectoryInfoMapper;
    private ExcelParserInfoMapper excelParserInfoMapper;
    // 新增字段
    private JSONObject websocket;
    private static final int TOTAL_STEPS = 4;

    @Override
    public Boolean call() throws IOException {
        String excelId = excelParserInfo.getExcelId();
        try {

            sendProgress(0, "开始解析文件");
            //String fileName = file.getOriginalFilename();
            // 读取源端数据，写入本地数据库，建表写数据
            String tableName = EXCEL_TABLE_PREFIX_ + System.currentTimeMillis();
            FileInputStream fileStream = new FileInputStream(convertInputStreamToFile(inputStream));
            sendProgress(25, "文件解析中");
            // 读取excel在数据库中创建表并把数据写入，返回值存储了excel中每一列的名字以及它在本地表中的名字，相当于本地表的列注释
            List<TableInfoVo> excelList = ExcelToMySQL.excel2Mysql(knowledgeId, excelId, fileStream);
            sendProgress(50, "数据解析完成");
            if (CollectionUtils.isEmpty(excelList)) {
                updateStatus(excelId, StructureConstant.PARSER_FAIL);
                return false;
            }
            // 写配置表
            tableDirectoryInfoMapper.insert(buildTableDirectoryInfo(excelParserInfo, tableName, excelList));
            sendProgress(75, "数据存储中");
            // 更新任务状态为成功
            updateStatus(excelId, StructureConstant.PARSER_SUCCESS);
            sendProgress(100, "任务完成");
            return true;
        }catch (Exception e){
            // 更新任务状态为失败
            updateStatus(excelId,StructureConstant.PARSER_FAIL);
            websocket.put("nums", -1);
            websocket.put("msg", "任务失败：" + e.getMessage());
            WebSocketServer.sendInfo(JSON.toJSONString(websocket), AppContextHolder.getAccountName());
            e.printStackTrace();
            return false;
        }
    }

    private void updateStatus(String excelId,Integer status){
        QueryWrapper queryWrapper = Wrappers.init().and(new QueryColumn("excel_id").eq(excelId));
        ExcelParserInfo excelParserInfo = excelParserInfoMapper.selectOneByQuery(queryWrapper);
        excelParserInfo.setParserFlag(status);
        excelParserInfo.setUpdateTime(new Date());
        excelParserInfoMapper.update(excelParserInfo);
    }

    private TableDirectoryInfo buildTableDirectoryInfo(ExcelParserInfo excelParserInfo, String tableName, List<TableInfoVo> excelList) {
        return TableDirectoryInfo.builder()
                .tableName(tableName)
                .allField(JSONObject.toJSONString(excelList))
                .businessId(excelParserInfo.getExcelId())
                .parserFlag(0)
                .type(StructureConstant.EXCEL)
                .build();
    }

    private static File convertInputStreamToFile(InputStream inputStream) throws IOException {
        File file = File.createTempFile("temp", ".tmp");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
        return file;
    }

    /**
     * 生成表名
     * @param fileName
     * @return
     */
    public static String buildPyinName(String fileName) {
        return PinyinUtil.getFirstLetter(fileName, " ").replaceAll("[^a-zA-Z0-9_\\u4E00-\\u9FA5]", "");
    }

    private void sendProgress(int progress, String message) throws IOException {
        JSONObject progressMsg = new JSONObject();  // 创建新对象，避免共享
        progressMsg.put("business", "Excel解析任务进度");
        progressMsg.put("taskId", this.excelParserInfo.getExcelId());  // 使用任务ID作为唯一标识
        progressMsg.put("nums", progress);
        progressMsg.put("msg", message);
        WebSocketServer.sendInfo(progressMsg.toJSONString(), AppContextHolder.getAccountName());  // 明确指定目标用户
    }

}
