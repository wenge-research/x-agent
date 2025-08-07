//package docx;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFParagraph;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class DocxReader {
//
//    public static void main(String[] args) {
//        List<Map<String, String>> qaList = new ArrayList<>();
//        StringBuffer stringBuffer = null;
//
//        String answer = null;
//        try (FileInputStream fis = new FileInputStream("C:\\Users\\DELL\\Desktop\\时间贩卖机\\知识库问答.docx")) {
//            XWPFDocument document = new XWPFDocument(fis);
//            Map<String, String> map = new HashMap<>();
//            for (XWPFParagraph paragraph : document.getParagraphs()) {
//                System.out.println(paragraph.getText());
//
//                if(0 == paragraph.getText().length())
//                    continue;
//                if(paragraph.getText().startsWith("以下是一些"))
//                    continue;
//                if(paragraph.getText().startsWith("以下是关于"))
//                    continue;
//                if(paragraph.getText().endsWith("一些问答："))
//                    continue;
//
//                //一问一答内容处理
//                if (paragraph.getText().startsWith("问：") || paragraph.getText().trim().contains(".\t问：") || paragraph.getText().trim().contains("、问：")) {
//                    map = new HashMap<>();
//                    map.put("question", paragraph.getText());
//                } else if (paragraph.getText().contains("答：")) {
//                    if (map != null) {
//                        if (paragraph.getText().endsWith("。")) {
//                            map.put("answer", paragraph.getText());
//                            qaList.add(map);
//                        }else {
//                            answer = paragraph.getText();
//                        }
//                    }
////                    map = null;
//                } else if (StringUtils.isNotBlank(answer)) {
//                    if (paragraph.getText().endsWith("。")) {
//                        try {
//                            map.put("answer", paragraph.getText());
//                        }catch (Exception e){
//                            System.err.println("@@@" + paragraph.getText());
//                            e.printStackTrace();
//                        }
//                        qaList.add(map);
//                        answer = null;
//                        map = null;
//                    }else {
//                        answer += paragraph.getText();
//                    }
//                } else if (paragraph.getText().endsWith("？")) {
//                    if (map != null) {
//                        map.put("answer", stringBuffer != null ? stringBuffer.toString() : " ");
//                        qaList.add(map);
//                        stringBuffer = null;
//                    }
//                    map = new HashMap<>();
//                    map.put("question", paragraph.getText());
//                    stringBuffer = new StringBuffer("###");
//                } else if (stringBuffer != null) {
//                    stringBuffer.append(paragraph.getText());
//                }
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        for (Map<String, String> map : qaList) {
////            System.out.println(map.toString());
////        }
//        createSheet(qaList);
//
//    }
//
//    public static void createSheet (List<Map<String, String>> qaList) {
//        // 创建一个Workbook，对应一个Excel文件
//        Workbook workbook = new XSSFWorkbook();
//        // 在Workbook中创建一个Sheet，对应Excel文件中的sheet
//        Sheet sheet = workbook.createSheet("Sheet1");
//
//        int rowNum = 0;
//        for (Map<String, String> map : qaList) {
//            // 创建行（Row），0表示第一行
//            Row row = sheet.createRow(rowNum);
//            Cell cell = row.createCell(0);
//            cell.setCellValue(map.get("question").replaceFirst("问：", "")
//                    .replaceFirst("[1-9]","")
//                    .replaceFirst("\\.","").replaceFirst("\\、",""));
//
//            cell = row.createCell(1);
//            cell.setCellValue(map.get("answer").replaceFirst("答：", ""));
//            rowNum++;
//        }
//        // 将Workbook写入文件
//        try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\DELL\\Desktop\\时间贩卖机\\知识库问答对.xlsx")) {
//            workbook.write(outputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                // 关闭Workbook释放内存
//                workbook.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    //引入依赖
//
////    <!--        &lt;!&ndash;解析docx文档&ndash;&gt;-->
////<!--        &lt;!&ndash; Apache POI Word &ndash;&gt;-->
////<!--        <dependency>-->
////<!--            <groupId>org.apache.poi</groupId>-->
////<!--            <artifactId>poi-ooxml</artifactId>-->
////<!--            <version>5.2.3</version> &lt;!&ndash; 请使用最新版本 &ndash;&gt;-->
////<!--        </dependency>-->
////
////<!--        &lt;!&ndash; Apache POI Common libs &ndash;&gt;-->
////<!--        <dependency>-->
////<!--            <groupId>org.apache.poi</groupId>-->
////<!--            <artifactId>poi</artifactId>-->
////<!--            <version>5.2.3</version> &lt;!&ndash; 请使用最新版本 &ndash;&gt;-->
////<!--        </dependency>-->
////
////<!--        &lt;!&ndash; Apache Commons libs for POI &ndash;&gt;-->
////<!--        <dependency>-->
////<!--            <groupId>commons-io</groupId>-->
////<!--            <artifactId>commons-io</artifactId>-->
////<!--            <version>2.11.0</version> &lt;!&ndash; 使用适合你的项目的版本 &ndash;&gt;-->
////<!--        </dependency>-->
//}