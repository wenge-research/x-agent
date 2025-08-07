package com.wenge.model.utils.translate;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author:caohaifeng
 * @createTime:2024-09-24 17:11
 * @Description: execl翻译类
 * @Version:1.0
 */
public class ExeclTranslate {
    /**
     * 翻译 xlsx文档
     **/
    public static void translateXlsx(InputStream inputStream, String fileUrl, String srcLang, String tgtLang) {
        InputStream fis = null;
        Workbook workbook = null;
        Sheet sheet = null;
        FileOutputStream fos = null;
        try {
            fis = inputStream;
            workbook = new HSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0); // 假设我们处理第一个sheet

            // 遍历行（Row）
            for (Row row : sheet) {
                // 遍历列（Cell）
                for (Cell cell : row) {
                    // 根据Cell类型处理数据
                    switch (cell.getCellType()) {
                        case STRING:
//                            System.out.print(cell.getStringCellValue() + "\t");
                            String translatedText = TranslateUtils.getTranslateContent(cell.getStringCellValue(), srcLang, tgtLang);
                            cell.setCellValue(translatedText);
                            break;
                        case NUMERIC:
//                            // 注意：日期也会作为NUMERIC类型处理
//                            if (DateUtil.isCellDateFormatted(cell)) {
//                                System.out.print(cell.getDateCellValue() + "\t");
//                            } else {
//                                System.out.print(cell.getNumericCellValue() + "\t");
//                            }
                            break;
                        case BOOLEAN:
//                            System.out.print(cell.getBooleanCellValue() + "\t");
//                            break;
                        case FORMULA:
//                            // 处理公式计算结果
//                            System.out.print(cell.getCellFormula() + "\t");
                            break;
                        case BLANK:
//                            // 空单元格
//                            System.out.print("\t");
                            break;
                        default:
//                            System.out.print("Unknown cell type\t");
                    }
                }
                // 每处理完一行，输出一个换行符
                System.out.println();
            }
            // 假设翻译已完成，现在我们将workbook写入到新文件
            fos = new FileOutputStream(new File(fileUrl));
            workbook.write(fos);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 翻译 xls文档
     **/
    public static void translateXls(InputStream inputStream, String fileUrl, String srcLang, String tgtLang) {
        InputStream fis = null;
        Workbook workbook = null;
        Sheet sheet = null;
        FileOutputStream fos = null;
        try {
            fis = inputStream;
            workbook = new HSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0); // 假设我们处理第一个sheet

            // 遍历行（Row）
            for (Row row : sheet) {
                // 遍历列（Cell）
                for (Cell cell : row) {
                    // 根据Cell类型处理数据
                    switch (cell.getCellType()) {
                        case STRING:
//                            System.out.print(cell.getStringCellValue() + "\t");
                            String translatedText = TranslateUtils.getTranslateContent(cell.getStringCellValue(), srcLang, tgtLang);
                            cell.setCellValue(translatedText);
                            break;
                        case NUMERIC:
//                            // 注意：日期也会作为NUMERIC类型处理
//                            if (DateUtil.isCellDateFormatted(cell)) {
//                                System.out.print(cell.getDateCellValue() + "\t");
//                            } else {
//                                System.out.print(cell.getNumericCellValue() + "\t");
//                            }
                            break;
                        case BOOLEAN:
//                            System.out.print(cell.getBooleanCellValue() + "\t");
//                            break;
                        case FORMULA:
//                            // 处理公式计算结果
//                            System.out.print(cell.getCellFormula() + "\t");
                            break;
                        case BLANK:
//                            // 空单元格
//                            System.out.print("\t");
                            break;
                        default:
//                            System.out.print("Unknown cell type\t");
                    }
                }
                // 每处理完一行，输出一个换行符
                System.out.println();
            }
            // 假设翻译已完成，现在我们将workbook写入到新文件
            fos = new FileOutputStream(new File(fileUrl));
            workbook.write(fos);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
