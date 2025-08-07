package com.wenge.model.workflow.component.file;


import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wenge.model.entity.File;
import com.wenge.model.strategy.documentAnalysis.DocumentAnalysisStrategy;
import com.wenge.model.strategy.file.FileStrategy;
import com.wenge.model.strategy.file.FileStrategyFactory;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.yayi.result.DocumentResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wenge.model.constants.DocumentAnalysisConstant.DOCUMENT_CONTENT_ANALYSIS_SERVER_YAYI;

@AllArgsConstructor
@Getter
public enum FileTransferMethod {

    REMOTE_URL("remote_url", (fileUrl) -> {
        try {

            ApplicationContext context = CoreContextProvider.getContext();
            Map<String, DocumentAnalysisStrategy> beansOfType = context.getBeansOfType(DocumentAnalysisStrategy.class);
            // 采用雅意解析文档
            DocumentAnalysisStrategy documentAnalysisStrategy = beansOfType.get(DOCUMENT_CONTENT_ANALYSIS_SERVER_YAYI);
            File file = new File();
            file.setFileUrl(fileUrl);
            String fileType = fileUrl.substring(fileUrl.lastIndexOf(".") + 1);
            // 简单的文本文档，则直接提取文本
            boolean simpleFormatterFlag = "json".equals(fileType) || "txt".equals(fileType);
            List<String> list = Lists.newArrayList();
            if (!simpleFormatterFlag) {
                List<DocumentResult.FileContent> fileContents = documentAnalysisStrategy.documentAnalysis(file);
                list = fileContents.stream().map(DocumentResult.FileContent::getText).collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(list) || StringUtils.isBlank(list.get(0))) {
                // 清空原来的数据
                list.clear();
                String encodedFileUrl = URLEncoder.encode(fileUrl, StandardCharsets.UTF_8.toString());
                // 创建URL对象
                URL url = new URL(encodedFileUrl.replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("\\+", "%20"));
                // 打开连接
                URLConnection connection = url.openConnection();
                // 获取输入流
                InputStream stream = connection.getInputStream();
                // 如果远程文本解析为空，尝试使用本地解析
                FileStrategy strategy = FileStrategyFactory.createStrategy(stream, getFileExtension(fileUrl));
                if (strategy != null) {
                    String content = strategy.getContent();
                    list.add(content);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, (fileUrl) -> {
        ApplicationContext context = CoreContextProvider.getContext();
        Map<String, DocumentAnalysisStrategy> beansOfType = context.getBeansOfType(DocumentAnalysisStrategy.class);
        // 采用雅意解析文档
        DocumentAnalysisStrategy documentAnalysisStrategy = beansOfType.get(DOCUMENT_CONTENT_ANALYSIS_SERVER_YAYI);
        File file = new File();
        file.setFileUrl(fileUrl);
        return documentAnalysisStrategy.documentAnalysis(file);
    }),
//    LOCAL_FILE("local_file"),
//    TOOL_FILE("tool_file"),
    ;
    @JsonValue
    private final String method;

    private final Function<String, List<String>> handler;
    /**
     * 返回文档解析原始内容
     */
    private final Function<String, List<DocumentResult.FileContent>> handlerRawContent;

    // public static void main(String[] args) {
    //     String url = "https://city.wengegroup.com/smart-agent-api-demo/wos/file/download/agent_source/存疑文件.docx";
    //     InputStream apply = REMOTE_URL.handler.apply(url);
    //
    // }

    public static String getFileExtension(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        int queryIndex = url.lastIndexOf('?');
        int slashIndex = url.lastIndexOf('/');
        int dotIndex = url.lastIndexOf('.');

        if (dotIndex > slashIndex && (queryIndex == -1 || dotIndex < queryIndex)) {
            return url.substring(dotIndex + 1);
        } else {
            return "";
        }
    }

    public static FileTransferMethod getFileTransferMethod(String method) {
        for (FileTransferMethod fileTransferMethod : FileTransferMethod.values()) {
            if (fileTransferMethod.getMethod().equals(method)) {
                return fileTransferMethod;
            }
        }
        return null;
    }
}
