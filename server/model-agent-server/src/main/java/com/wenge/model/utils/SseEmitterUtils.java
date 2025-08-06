package com.wenge.model.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.WebsiteConfig;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wg.appframe.core.constant.StringConstant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo yayi-common
 *
 * @author zwc
 * @since 2023/9/22
 */
public class SseEmitterUtils {

    private static final Logger logger = LoggerFactory.getLogger(SseEmitterUtils.class);

    private static final Integer EMITTER_COMPLETE_DELAY_MILLISECONDS = 500;

    private static final Map<String, SseEmitter> SSE_CACHE = new ConcurrentHashMap<>();

    //public static Map<String, String> WEB_CACHE = Maps.newHashMap();

    public static SseEmitter getConnection(@NotBlank String clientId) {
        return getConnection(clientId, 600000);
    }

    public static SseEmitter getConnection(@NotBlank String clientId, long timeout) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);

        if (sseEmitter != null) {
            return sseEmitter;
        } else {
            final SseEmitter emitter = new SseEmitter(timeout);

            emitter.onTimeout(() -> {
                logger.info("[SseEmitter] 连接已超时，正准备关闭，clientId = {}", clientId);
                SSE_CACHE.remove(clientId);
            });

            emitter.onCompletion(() -> {
                SSE_CACHE.remove(clientId);
                logger.info("[SseEmitter] 连接已释放，clientId = {}", clientId);
            });

            emitter.onError(throwable -> {
                throwable.printStackTrace();
                logger.error("[SseEmitter] 连接已异常，正准备关闭，clientId = {}，异常:{}", clientId, throwable.getMessage());
                SSE_CACHE.remove(clientId);
            });

            SSE_CACHE.put(clientId, emitter);
            logger.info("[SseEmitter] 连接已建立，clientId = {}", clientId);

            return emitter;
        }
    }

    public static void closeConnection(@NotBlank String clientId) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        logger.info("[流式响应-停止生成] 收到客户端关闭连接指令，Emitter is {}，clientId = {}",
                null == sseEmitter ? "NOT-Exist" : "Exist", clientId);
        if (sseEmitter != null) {
            sseEmitter.complete();
            SSE_CACHE.remove(clientId);
        }

        try {
            TimeUnit.MILLISECONDS.sleep(EMITTER_COMPLETE_DELAY_MILLISECONDS);
        } catch (InterruptedException ex) {
            logger.error("流式响应异常", ex);
            Thread.currentThread().interrupt();
        }
    }

    public static void send(String clientId, String msg) throws IOException {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter != null) {
            sseEmitter.send(msg);
        }
    }

    /**
     * 推送消息
     *
     * @param clientId 客户端 ID
     * @param msg      消息
     * @return 连接是否存在
     * @throws IOException IO异常
     */
    public static boolean sendMsg(String clientId, String msg) throws IOException {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(msg);
            } catch (Exception e) {
                logger.error("[流式响应-停止生成] ");
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 延迟关闭 SseEmitter
     * <br/>
     * 延迟为了尽量保证最后一次推送数据被前端完整接收
     *
     * @param clientId 客户端ID
     */
    public static void completeDelay(String clientId) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter != null) {
            try {
                TimeUnit.MILLISECONDS.sleep(EMITTER_COMPLETE_DELAY_MILLISECONDS);
                sseEmitter.complete();
            } catch (InterruptedException ex) {
                logger.error("流式响应异常", ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void complete(String clientId) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter != null) {
            sseEmitter.complete();
            // closeConnection(clientId);
        }
    }

    /**
     * 输出流
     *
     * @param dto
     * @param result
     */
    public static JSONObject sendSpeed(DialogueByStreamParam dto, String result) {
        JSONObject data = new JSONObject();
        try {
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
            dto.setAnswerFlag("true");
            if (applicationInfo.getFailureTalk().equals(result)) {
                dto.setAnswerFlag("false");
            }
            dto.setListMsg(Lists.newArrayList());
            if (StringUtils.isNotBlank(applicationInfo.getButtonStyle())) {
                //result = getButton(result, dto.getWebsiteConfigs());
            }
            dto.setWebsiteConfigs(null);
            data = JSON.parseObject(JSON.toJSONString(dto));
            data.put("question", dto.getContent());
            data.put("id", dto.getDialogueId());
            data.put("dialogueId", dto.getDialogueId());
            data.put("createTime", LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd HH:mm:ss"));
            data.remove("dialogueFileIds");
            data.remove("dialogueFolderIds");
            // 输出流
            if ("true".equals(dto.getStream())) {
                // 输出按钮的时候，由于样式太长聊天框会打印样式代码，通过该变量可以控制跳过打印样式代码：直接一次性输出样式代码而不是逐个输出样式代码
                boolean beginFlag = false;
                // 按钮样式输出后，聊天框会因为输出按钮而跳动，通过该变量配合extendFlag延长标记，可以和按钮后几个字符一起输出，从而避免聊天框跳动
                int fix = 0;
                boolean extendFlag = false;
                for (int i = 0; i < result.length(); i++) {
                    // 遇到a>标签时，结束跳过的逐个打印的文本
                    if (result.length() >= (i + 2) && result.substring(i, i + 2).equals("a>")) {
                        if (beginFlag) {
                            beginFlag = false;
                            extendFlag = true;
                        }
                    }

                    // 遇到<a标签，启动跳过打印的文本
                    if (result.length() >= (i + 2) && result.substring(i, i + 2).equals("<a")) {
                        beginFlag = true;
                    }

                    // 还处于跳过的文本
                    if (beginFlag) {
                        continue;
                    }
                    // 处于延长文本
                    if (extendFlag) {
                        // 在延长3个字后，继续打印
                        if (fix < 3) {
                            fix++;
                            continue;
                        } else {
                            fix = 0;
                            extendFlag = false;
                        }
                    }
                    // 输出速度
                    ThreadUtil.sleep(10);
                    if (i == result.length() - 1) {
                        String plainText = plainText(result);
                        data.put("plainText", plainText);
                    }
                    data.put("answer", result.substring(0, i + 1));
                    if (sendMsg(dto.getClientId(), JSON.toJSONString(data))) {
                        break;
                    }
                }
                data.put("answer", result);
            } else {
                String plainText = plainText(result);
                data.put("plainText", plainText);
                data.put("answer", result);
                sendMsg(dto.getClientId(), JSON.toJSONString(data));
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
            complete(dto.getClientId());
        }
        return data;
    }

    /**
     * 网页链接展示按钮
     *
     * @param originalString
     * @return
     */
    public static String getButton(String originalString, List<WebsiteConfig> configs, JSONObject urlReplace) {
        //问我们的官方网站「[龙华区政府官方网站](https://www.szlhq.gov.cn/)」。
        String regex = "「<?https?[^)）]*?>?」";
        originalString = replaceHttp(originalString, regex, configs, urlReplace);

        regex = "<https?[^)）]*?>";
        originalString = replaceHttp(originalString, regex, configs, urlReplace);

        regex = "「?\\[.*?\\]\\((.*?)\\)」?";
        originalString = replaceHttp(originalString, regex, configs, urlReplace);

        //regex = "h-t-t-ps?://\\S+";
        //originalString = replaceHttp(originalString, regex, configs);

        //regex = "https?://\\S+";
        //regex = "\\bhttps?://[^\\u4e00-\\u9fa5\\s]+";
        //originalString = replaceHttp(originalString, regex, configs, urlReplace);

        //originalString = originalString.replace("</br>", " </br>");
        //originalString = replaceHttp(originalString, regex, configs);
        //regex = "https?://\\S+";
        //originalString = getHttp(originalString, regex, configs);

        originalString = getOrgUrl(originalString);
        return originalString;
    }

    /**
     * 解析url
     *
     * @param originalString
     * @param regex
     * @return
     */
    private static String replaceHttp(String originalString, String regex, List<WebsiteConfig> configs, JSONObject urlReplace) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalString);

        // 逐个替换匹配的URL
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            // 原始url
            String tempUrl = matcher.group(0);
            String url = extractUrl(tempUrl);
            int groupCount = matcher.groupCount();

            // 这种情况：我们的官方网站「[龙华区政府官方网站](https://www.szlhq.gov.cn/)」。
            if (1 == groupCount) {
                url = matcher.group(1);
            }
            if (!url.startsWith("h-t-t-p") && !url.startsWith("http")) {
                continue;
            }
            String replacement = "";
            if (urlReplace.containsKey(tempUrl)) {
                replacement = urlReplace.getString(tempUrl);
            } else {
                replacement = showButton(url, configs);
                urlReplace.put(tempUrl, replacement);
            }
            //if (1 == groupCount) {
            //    if (urlReplace.containsKey(tempUrl)) {
            //        replacement = urlReplace.getString(tempUrl);
            //    } else {
            //        replacement = showButton(url, configs);
            //        urlReplace.put(tempUrl, replacement);
            //    }
            //} else {
            //    if (urlReplace.containsKey(url)) {
            //        replacement = urlReplace.getString(url);
            //    } else {
            //        replacement = showButton(url, configs);
            //        urlReplace.put(url, replacement);
            //    }
            //}
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 提取url
     *
     * @param url
     * @return
     */
    private static String showButton(String url, List<WebsiteConfig> configs) {
        try {
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
            String orGurl = url;
            if (url.startsWith("「") || url.startsWith("（") || url.startsWith("(")) {
                url = url.substring(1);
            }
            if (url.endsWith("」") || url.endsWith(")") || url.endsWith("）")) {
                url = url.substring(0, url.length() - 1);
            }
            url = url.replace("h-t-t-p", "http");
            String regex = "<title>(.*?)</title>";
            Pattern pattern = Pattern.compile(regex);
            String title = "";
            if (!isFileURL(url)) {
                if (CollectionUtils.isNotEmpty(configs)) {
                    String urlTemp = url;
                    // 先精确匹配
                    Optional<WebsiteConfig> any = configs.stream().filter(p -> p.getWebUrl().equals(urlTemp)).findAny();
                    if (any.isPresent()) {
                        title = any.get().getShowText();
                    } else {
                        // 前缀匹配
                        any = configs.stream().filter(p -> p.getWebUrl().replace("/**","").contains(urlTemp)).findAny();
                        if (any.isPresent()) {
                            title = any.get().getShowText();
                        }
                    }
                }

                // 请求网页获取标题
                if (StringUtils.isBlank(title)) {
                    String html = http(url);
                    Matcher matcher = pattern.matcher(html);
                    // 查找匹配项
                    if (matcher.find()) {
                        // 返回匹配到的第一个分组的内容（即<title>标签内的内容）
                        title = matcher.group(1);
                        if (StrUtil.isNotBlank(title)) {
                            String buttonStyle = applicationInfo.getButtonStyle();
                            buttonStyle = buttonStyle.replace("{title}", title)
                                    .replace("{url}", replaceUrl(orGurl));
                            return buttonStyle;
                        }
                    }
                }
            }

            if (StringUtils.isBlank(title)) {
                title = applicationInfo.getButtonText();
            }
            String buttonStyle = applicationInfo.getButtonStyle();
            buttonStyle = buttonStyle.replace("{title}", title)
                    .replace("{url}", replaceUrl(orGurl));
            return buttonStyle;
        } catch (Exception e) {
            logger.error("showButton.error:{},url:{}", e.getMessage(), url);
        }
        return url;
    }

    /**
     * 调整http文本
     * @param url
     * @return
     */
    private static String replaceUrl(String url) {
        //if (url.startsWith("」")) {
        //    url = url.substring(1);
        //}
        //if (url.endsWith("「")) {
        //    url = url.substring(0, url.length() - 1);
        //}
        url = url.replace("」", "").replace("「", "")
                .replace("http", "h-t-t-p");
        return url;
    }

    /**
     * 获取原始的http
     * @param content
     * @return
     */
    private static String getOrgUrl(String content) {
        content = content.replace("<h|t|t|p", "http");
        content = content.replace("h|t|t|p", "http");
        content = content.replace("h|t|t|p", "http");
        content = content.replace("h-t-t-p", "http");
        return content;
    }

    /**
     * 替换文本中的http地址
     *
     * @param originalString
     * @param regex
     * @return
     */
    private static String getHttp(String originalString, String regex, List<WebsiteConfig> configs) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalString);

        // 逐个替换匹配的URL
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String url = matcher.group();
            if (!url.startsWith("http")) {
                continue;
            }
            String replacement = showButton(url, configs);
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 请求url
     * @param url
     * @return
     */
    private static String http(String url) {
        // 创建OkHttpClient实例
        OkHttpClient client = new OkHttpClient();

        // 定义要请求的URL

        // 构建Request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        String html = "";
        try {
            // 发送请求并获取响应
            Response response = client.newCall(request).execute();

            // 检查响应是否成功
            if (response.isSuccessful()) {
                // 获取响应体的字符串形式
                html = response.body().string();
                // 打印响应内容
                //System.out.println("Response Body: " + html);
            } else {
                // 处理响应失败的情况
                System.out.println("Request failed. Response code: " + response.code());
            }
        } catch (Exception e) {
            // 处理异常情况
            e.printStackTrace();
        }
        return html;
    }

    /**
     * 判断url是否为链接
     * @param urlString
     * @return
     */
    public static boolean isFileURL(String urlString) {
        // 先通过正则判断
        boolean fileRegex = isFileRegex(urlString);
        if (fileRegex) {
            return true;
        }

        //OkHttpClient client = new OkHttpClient();
        //Request request = new Request.Builder()
        //        .url(urlString)
        //        .head() // 使用HEAD请求来获取headers而不下载内容
        //        .build();
        //try (Response response = client.newCall(request).execute()) {
        //    String contentType = response.header("Content-Type");
        //
        //    // 根据Content-Type判断是否为文件类型
        //    if (StringUtils.isNotBlank(contentType) && !contentType.startsWith("application/json") && (contentType.startsWith("application/") || contentType.startsWith("image/"))) {
        //        return true;
        //    }
        //    String contentDisposition = response.header("Content-Disposition", "");
        //    if (StringUtils.isNotBlank(contentDisposition) && contentDisposition.contains("attachment")) {
        //        return true;
        //    }
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        return false;
    }

    /**
     * 正则判断是否为文件
     * @param url
     * @return
     */
    public static boolean isFileRegex(String url) {
        // 正则表达式，匹配常见的文件扩展名
        String regex = "^.+\\.(jpg|jpeg|png|gif|bmp|pdf|doc|docx|ppt|pptx|xls|xlsx|txt|zip|rar|7z|mp3|mp4|avi|wmv|flv|mkv)$";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        // 检查URL是否匹配
        Matcher matcher = pattern.matcher(url);

        return matcher.find();
    }


    /**
     * 提取纯文本
     * @param content
     * @return
     */
    private static String plainText(String content) {
        try {
            // 将Markdown转换为纯文本
            String textContent = markdownText(content);

            return textContent.replaceAll("<.*?>", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 提取markdown中的纯文本
     * @param content
     * @return
     */
    private static String markdownText(String content) {
        // 创建Markdown解析器
        Parser parser = Parser.builder().build();
        Node document = parser.parse(content);

        // 创建纯文本渲染器
        TextContentRenderer renderer = TextContentRenderer.builder().build();

        // 将Markdown转换为纯文本
        return renderer.render(document);
    }


    /**
     * 提取url
     */
    private static String extractUrl(String tempUrl) {
        String url = tempUrl.replace("「<", StringConstant.BLANK)
                .replace(">」", StringConstant.BLANK);
        if (url.startsWith("「") || url.startsWith("（") || url.startsWith("(") || url.startsWith("<")) {
            url = url.substring(1);
        }
        if (url.endsWith("」") || url.endsWith(")") || url.endsWith("）") || url.endsWith(">")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
