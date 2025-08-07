package com.wenge.model.dto.param;

import com.wenge.model.entity.FileLanguage;
import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class AnswerRefParam extends WGParam {

    private static final long serialVersionUID = 3833438229494121809L;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 摘要
     */
    private String summarize;

    /**
     * 链接
     */
    private String url;

    /**
     * 译文链接
     */
    private Map<String, String> translationUrlMap;

    /**
     * 译文链接
     */
    private List<FileLanguage> fileLanguageList;

    /**
     * 索引
     */
    private Integer index;

    /**
     * 模块
     */
    private List<String> module;

    /**
     * 来源类型：
     * 1、知识库：显示fileName文件名，跳转链接或者下载文件，需要根据文件名去重显示
     * 2、内置问题，检索QA【问题】、检索QA【答案】：显示fileLink，跳转链接，根据fileLink去重
     * 3、多媒体知识库：显示多媒体文件名，跳转链接或者下载视频(.mp4)、图片(jpg, jpeg, png)，需要根据文件名去重显示
     * 4、网站url
     */
    private Integer sourceType;

    @Override
    public boolean equals(Object o) {
        // 1. 检查是否是同一个对象
        if (this == o) {
            return true;
        }
        // 2. 检查是否为null或类不匹配
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        // 3. 比较title字段
        AnswerRefParam book = (AnswerRefParam) o;
        return Objects.equals(title, book.title);
    }

    @Override
    public int hashCode() {
        // 只使用title字段生成哈希码
        return Objects.hash(title);
    }
}
