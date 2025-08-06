package com.wenge.model.dto.result;

import com.mybatisflex.annotation.Column;
import com.wenge.model.entity.FileLanguage;
import com.wg.appframe.core.dto.results.WGResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class SourceAnswerResult extends WGResult {

    private static final long serialVersionUID = -6415080641520901306L;

    /**
     * 知识库名称
     */
    private String knowledgeName;

    /**
     * 层级路由
     */
    private List<String> route;

    /**
     * 具体文本
     */
    private String text;

    /**
     * 无法回答的标识，默认是
     */
    private String answerFlag = "是";

    /**
     * 来源：名称
     * 当回答匹配：检索知识库，给出文件名称；
     */
    private String fileName;

    /**
     * 来源：地址
     * 当回答匹配：检索知识库，给出文件地址（文件下载）/文件源地址（跳转链接）；
     * 当回答匹配：内置问题，检索QA【问题】、检索QA【答案】，给QA link（跳转链接）
     *
     */
    private String fileLink;

    /**
     * 多语言文件链接 lang->url
     */
    private Map<String, String> translationUrlMap;

    /**
     * 多语言文件汇总等字段
     */
    private List<FileLanguage> fileLanguageList;


    /**
     * 文档链接类型[1-原始文件链接，2-网页链接]
     * 前端使用这个字段判定展示文件或者跳转链接
     */
    private String docLinkType;

    /**
     * 来源类型：
     * 1、知识库：显示fileName文件名，跳转链接或者下载文件，需要根据文件名去重显示
     * 2、内置问题，检索QA【问题】、检索QA【答案】：显示fileLink，跳转链接，根据fileLink去重
     * 3、多媒体知识库：显示多媒体文件名，跳转链接或者下载视频(.mp4)、图片(jpg, jpeg, png)，需要根据文件名去重显示
     * 4、网站url
     */
    private Integer sourceType;

    /**
     * 文件转换后的PDF文件路径
     */
    private String transPdfUrl;

    /**
     * 评分
     */
    private Float score;


    // hash和equals方法
    @Override
    public int hashCode() {
        if (StringUtils.isNotBlank(fileName)) {
            return Objects.hash(fileName);
        }
        if (StringUtils.isNotBlank(text)) {
            return Objects.hash(text);
        }
        return Objects.hash(knowledgeName, route, text, fileName, fileLink, sourceType, transPdfUrl, score);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SourceAnswerResult other = (SourceAnswerResult) obj;
        if (StringUtils.isNotBlank(fileName)) {
            return Objects.equals(fileName, other.fileName);
        }
        if (StringUtils.isNotBlank(text)) {
            return Objects.equals(text, other.text);
        }
        return Objects.equals(knowledgeName, other.knowledgeName) &&
                Objects.equals(text, other.text);
    }

}
