package translate;

//import com.google.cloud.translate.Translate;
//import com.google.cloud.translate.TranslateOptions;
//import javafx.scene.transform.Translate;
import com.aspose.words.Document;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Run;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
 
public class DocxTranslator {
 
    public static void main(String[] args) throws IOException {
        File inputFile = new File("C:\\Users\\DELL\\Desktop\\时间贩卖机\\知识库问答 - 副本.docx");
        File outputFile = new File("C:\\Users\\DELL\\Desktop\\时间贩卖机\\知识库问答 - 副本_out.docx");

        // 加载文档
        Document doc = null;
        try {
            doc = new Document("C:\\Users\\DELL\\Desktop\\时间贩卖机\\翻译\\新建 DOCX 文档.docx");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 遍历文档中的所有段落
        for (Paragraph para : (Iterable<Paragraph>) doc.getChildNodes(NodeType.PARAGRAPH, true)) {
            // 清除段落中的所有内容
            para.removeAllChildren();

            // 假设我们有一个方法可以从段落中提取原始文本（这里简化为直接使用空字符串，因为我们要清除）
            // 但实际上，你可能需要从para中提取原始文本，然后调用translateText
            // String originalText = extractTextFromParagraph(para); // 这是一个假设的方法

            // 为了示例，我们直接假设每个段落都被替换为相同的翻译文本
            // 在实际应用中，你可能需要为每个段落或文档的不同部分调用不同的翻译逻辑
            String translatedText = "这是段落" + (doc.getChildNodes(NodeType.PARAGRAPH, true).indexOf(para) + 1) + "的翻译文本";
            // 或者，如果你有一个具体的翻译需求，可以调用translateText函数
            // String translatedText = translateText("示例文本", fromLang, toLang);

            // 添加翻译后的文本到段落
            para.appendChild(new Run(doc, translatedText));
        }

        // 保存修改后的文档
        try {
            doc.save("C:\\Users\\DELL\\Desktop\\时间贩卖机\\翻译\\新建 DOCX 文档_demo.docx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}