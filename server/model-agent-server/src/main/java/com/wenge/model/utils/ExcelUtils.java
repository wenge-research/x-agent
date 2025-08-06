package com.wenge.model.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class ExcelUtils {

    public static List<Object> getExcelData(FileInputStream inputStream){
        try {
            // 根据文件后缀（xls/xlsx）选择合适的工作簿对象
            Workbook workbook = WorkbookFactory.create(inputStream);
            // 获取第一个工作表（在 Excel 中，第一个工作表的索引是 0）
            Sheet sheet = workbook.getSheetAt(0);
            // 迭代每一行，并读取数据
            for (Row row : sheet) {
                // 迭代每个单元格
                for (Cell cell : row) {
                    // 根据单元格类型读取数据
                    switch (cell.getCellType()) {
                        case STRING:
                            System.out.print(cell.getStringCellValue() + "\t");
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                System.out.print(cell.getDateCellValue() + "\t");
                            } else {
                                System.out.print(cell.getNumericCellValue() + "\t");
                            }
                            break;
                        case BOOLEAN:
                            System.out.print(cell.getBooleanCellValue() + "\t");
                            break;
                        case FORMULA:
                            System.out.print(cell.getCellFormula() + "\t");
                            break;
                        default:
                            System.out.print("\t");
                    }
                }
                System.out.println(); // 换行，表示下一行数据
            }
            // 关闭流和释放资源
            workbook.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取excel列的类型
    public static Map<String,Object> getExcelCellType(FileInputStream inputStream){
        Map<String,Object> result = new HashMap<>();
        try(Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            // 假设第一行为标题行，获取列数
            int columnCount = sheet.getRow(0).getLastCellNum();
            // 获取第一行作为标题行的列名和类型
            Row headerRow = sheet.getRow(0);
            for (int col = 0; col < columnCount; col++) {
                Cell cell = headerRow.getCell(col);
                if (cell != null) {
                    // 获取列名
                    String columnName = cell.getStringCellValue();
                    // 获取列类型
                    CellType columnType = getColumnType(sheet, col);
                    result.put(columnName,columnType.name());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(Objects.nonNull(inputStream)){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    // 获取excel每一列的类型
    private static CellType getColumnType(Sheet sheet, int columnIndex) {
        // 默认假设第一行以下都是数据行，根据第一个数据行的单元格类型来确定列类型
        int firstDataRowIndex = 1; // 第一行以下为数据行
        Row firstDataRow = sheet.getRow(firstDataRowIndex);
        Cell cell = firstDataRow.getCell(columnIndex);
        if (cell != null) {
            return cell.getCellType();
        }
        return CellType.BLANK; // 如果为空则返回空类型
    }
}
