package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.Score;
import org.dromara.easyes.annotation.rely.FieldStrategy;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@IndexName
@Data
public class KnowledgeData implements Serializable {

    private static final long serialVersionUID = 7010729784945401865L;

    @IndexId(type = IdType.UUID)
    private String id;


    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String knowledgeId;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String dialogueId;

    @Column(ignore = true)
    private String knowledgeName;

    /**
     * 是否精确回答，是/否
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String accurate;

    /**
     * 标题
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String title;
    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> titleDense;

    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> titleDense1024;

    /**
     * 内容
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String content;

    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> contentDense;

    @IndexField(fieldType = FieldType.DENSE_VECTOR)
    private List<Double> contentDense1024;

    /**
     * 更新时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;

    /**
     * 更新人
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String updateUser;

    /**
     * 数据连接
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String link;

    /**
     * 是否推荐【推荐/不推荐】
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String suggest;

    /**
     * 数据分类
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String category;

    /**
     * 是否需要润色，默认是，是/否
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String polishFlag;

    /**
     * 数据来源 1：页面添加 2：页面导入 3：审核推送(用户审核) 4：审核推送（系统自动审核） 5：点踩推送
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String dataSource;

    /**
     * 添加数据用户人id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String userId;

    /**
     * 添加数据用户人姓名
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String userName;

    /**
     * 核实部门id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String deptId;

    /**
     * 核实部门名称
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String deptName;

    /**
     * 文件夹id（暂未使用）
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String foldersId;

    /**
     * 是否删除有效  0或者null-未删除（默认值） 1-删除
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String delStatus = "0";

    /**
     * 市监局需求, 增加附件
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String attachment;

    @Score
    private Float score;

    @IndexField(exist = false)
    private BigDecimal rangeScore;

    /**
     * 启用状态
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String status;

    /**
     * 上传人
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String createUser;

    /**
     * 上传时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 生效开始时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss", strategy = FieldStrategy.IGNORED)
    private String effectiveStartTime;

    /**
     * 生效结束时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss", strategy = FieldStrategy.IGNORED)
    private String effectiveEndTime;

    /**
     * 创建人部门
     */
    @Column(ignore = true)
    private String createUserDeptName;

    /**
     * 创建人部门
     */
    @Column(ignore = true)
    private String createUserDeptId;

    public String obtainAnswer() {
        // 获取内容的时候,拼接上附件链接
        String contentWithAttachment = content;
        if (StringUtils.isNotBlank(attachment)) {
            String extension = attachment.substring(attachment.lastIndexOf('.') + 1).toLowerCase();
            if (extension.matches("jpg|jpeg|png|gif|bmp")) {
                // 图片格式
                contentWithAttachment += "<img src='" + attachment + "' alt='Attachment'>";
            } else if (extension.matches("mp4|avi|mov|mkv|flv")) {
                // 视频格式
                contentWithAttachment += "<video controls><source src='" + attachment + "' type='video/" + extension + "'>Your browser does not support the video tag.</video>";
            }
        }
        return contentWithAttachment;
    }

    public static String getHttpAddress(String input) {
        // 正则表达式模式，匹配HTTP或HTTPS地址
        String regex = "([(（<]*https?://\\S+[>)）]*)";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建匹配器
        Matcher matcher = pattern.matcher(input);

        // 查找匹配项
        if (matcher.find()) {
            return matcher.group(1); // 返回第一个匹配到的组
        }

        return ""; // 如果没有匹配项，返回null
    }
}
