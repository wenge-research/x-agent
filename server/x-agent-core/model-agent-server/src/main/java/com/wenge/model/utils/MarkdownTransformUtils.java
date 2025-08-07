//package com.wenge.model.utils;
//
//import io.woo.htmltopdf.HtmlToPdf;
//import io.woo.htmltopdf.HtmlToPdfObject;
//import org.commonmark.node.Node;
//import org.commonmark.parser.Parser;
//import org.commonmark.renderer.html.HtmlRenderer;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class MarkdownTransformUtils {
//    /**
//     * 将原始字符串转换为标准 Markdown 文本
//     */
//    public static String convertToMarkdown(String input) {
//        // 步骤1：替换 \n 为系统换行符
//        String text = input.replace("\\n", "\n");
//
//        // 步骤2：处理 markdown 标题：将 ### **X. 名称** 转换为 ## X. 名称
//        Pattern headerPattern = Pattern.compile("###\\s*\\*\\*(.*?)\\*\\*");
//        Matcher headerMatcher = headerPattern.matcher(text);
//        StringBuffer sb = new StringBuffer();
//        while (headerMatcher.find()) {
//            String title = headerMatcher.group(1).trim();
//            headerMatcher.appendReplacement(sb, "## " + title);
//        }
//        headerMatcher.appendTail(sb);
//        text = sb.toString();
//
//        // 步骤3：去除所有 "- --" 分隔线
//        text = text.replaceAll("- --", "");
//
//        // 步骤4：清理多余的空白行
//        text = text.replaceAll("(\\r?\\n){3,}", "\n\n");
//
//        return text;
//    }
//
//    /**
//     * markdown格式转换成HTML格式
//     * @param markdown
//     * @return
//     */
//    public static String markdownToHtml(String markdown) {
//        Parser parser = Parser.builder().build();
//        Node document = parser.parse(markdown);
//        HtmlRenderer renderer = HtmlRenderer.builder().build();
//        return renderer.render(document);
//    }
//
//    public static void main(String[] args) throws Exception {
//        String input = "人工智能（AI）涵盖了多个领域，包括但不限于：\n\n1. **机器学习**：通过数据训练模型，使计算机能够进行预测和决策。\n2. **自然语言处理（NLP）**：使计算机能够理解、生成和处理人类语言。\n3. **计算机视觉**：使计算机能够理解和分析图像和视频。\n4. **机器人技术**：结合AI和机械工程，开发能够执行复杂任务的机器人。\n5. **专家系统**：模拟人类专家的决策过程，提供专业建议。\n6. **语音识别**：将人类语音转换为文本或命令。\n7. **自动驾驶**：利用AI技术实现车辆的自主驾驶。\n\n###  人工智能给人类带来的便捷：\n\n1. **自动化**：AI可以自动化重复性任务，提高生产效率，减少人力成本。\n2. **医疗诊断**：AI可以帮助医生进行更准确的诊断，提高医疗水平。\n3. **个性化服务**：AI可以根据用户的行为和偏好提供个性化的推荐和服务。\n4. **智能助手**：如智能音箱、手机助手等，帮助用户管理日常事务。\n5. **数据分析**：AI可以快速处理和分析大量数据，提供有价值的洞察。\n6. **交通优化**：AI可以优化交通流量，减少拥堵和事故。\n\n###  人工智能可能带来的危害：\n\n1. **就业影响**：AI自动化可能导致某些工作岗位的减少，引发失业问题。\n2. **隐私问题**：AI需要大量数据，可能侵犯个人隐私。\n3. **安全风险**：AI系统可能被黑客攻击，导致数据泄露或系统失控。\n4. **伦理问题**：AI决策可能缺乏透明性，引发伦理争议。\n5. **依赖性**：过度依赖AI可能导致人类技能的退化。\n6. **偏见和歧视**：AI模型可能继承训练数据中的偏见，导致不公平的决策。\n\n###  民治街道招商政策解读与办事指南\n\n在民治街道，我们积极推动人工智能产业的发展，提供多项政策支持，包括税收优惠、资金补贴、人才引进等。如果您有相关项目或企业需要落地，欢迎联系我们的招商智能客服助手，我们将为您提供详细的政策解读和办事指南，帮助您顺利开展业务。\n\n如需进一步了解或申请相关政策支持，请访问我们的官方网站或直接联系我们的招商服务团队。我们期待与您合作，共同推动人工智能产业的发展，为人类带来更多便捷与福祉。";
//
//        String markdown =convertToMarkdown(input);
//        System.out.println(markdown);
//        String htmlContent = markdownToHtml(markdown);
//        System.out.println(htmlContent);
//
//        String pdfOutput = "/Users/Zhuanz1/IdeaProjects/yayi-model-application/output.pdf"; // PDF 输出文件名
//
//        HtmlToPdf.create().object(HtmlToPdfObject.forHtml(htmlContent).defaultEncoding("utf8"))
//                .convert(pdfOutput);
//
//触发器
//
//    }
//
//
//}
