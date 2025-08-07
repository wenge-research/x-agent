package com.wenge.model.utils;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

/**
 * @Description: 简单的excel导出
 * @Author: CHENZHIWEI
 * @CreateTime: 2022-12-08 14:36:10
 */
@Slf4j
public class EasyExcelUtil {

    /**
     * 最简单的导出,标题和具体严格按照顺序不可错乱
     * 传入2层数组，第一层的第一元素为标题，剩下为填充的数据，用完一定要关闭流:writer.close()
     *
     * @param dataList
     * @param out
     */
    public static void export(List<List<String>> dataList, OutputStream out) {
        // 设置文件名
        ExcelWriter writer = null;
        File file = null;
        try {
            file = new File("downloadTemp.xlsx");
            writer = ExcelUtil.getWriter(file);
            // 定义单元格背景色
            writer.write(dataList, true);

            // 设置单元格样式格式
            Workbook workbook = writer.getWorkbook();
            Sheet sheetAt = workbook.getSheetAt(0);
            Row row = sheetAt.getRow(0);
            row.setHeight((short) 500);
            short lastCellNum = row.getLastCellNum();
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 14);
            cellStyle.setFont(font);
            Cell cell;
            for (int i = 0; i < lastCellNum; i++) {
                sheetAt.setColumnWidth(i, 20 * 256);
                cell = row.getCell(i);
                cell.setCellStyle(cellStyle);
            }
            writer.flush(out);

        } catch (Exception e) {
            log.error("==> error:{}", e.getMessage(), e);
        } finally {
            if (null != writer) {
                writer.close();
            }
            if (null != file) {
                file.delete();
            }
        }
    }




}
