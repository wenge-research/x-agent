package com.wenge.model.utils.translate;

import com.aspose.words.Document;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Run;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:caohaifeng
 * @createTime:2024-09-24 17:11
 * @Description: word翻译类
 * @Version:1.0
 */
public class WordTranslate {
    /**
     * 翻译 docx文档
     **/
    public static void translateDocx(InputStream fileInputStream, String fileUrl, String srcLang, String tgtLang) {
        // 加载文档
        Document doc = null;
        try {
            doc = new Document(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 遍历文档中的所有段落
        for (Paragraph para : (Iterable<Paragraph>) doc.getChildNodes(NodeType.PARAGRAPH, true)) {
            // 假设我们有一个方法可以从段落中提取原始文本（这里简化为直接使用空字符串，因为我们要清除）
            // 但实际上，你可能需要从para中提取原始文本，然后调用translateText
            // String originalText = extractTextFromParagraph(para); // 这是一个假设的方法

            // 为了示例，我们直接假设每个段落都被替换为相同的翻译文本
            // 在实际应用中，你可能需要为每个段落或文档的不同部分调用不同的翻译逻辑
            String translatedText = TranslateUtils.getTranslateContent(para.getText(), srcLang, tgtLang);
            // 清除段落中的所有内容
            para.removeAllChildren();
            // 或者，如果你有一个具体的翻译需求，可以调用translateText函数
            // String translatedText = translateText("示例文本", fromLang, toLang);
            // 添加翻译后的文本到段落
            para.appendChild(new Run(doc, translatedText));
        }
        // 保存修改后的文档
        try {
            doc.save(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
