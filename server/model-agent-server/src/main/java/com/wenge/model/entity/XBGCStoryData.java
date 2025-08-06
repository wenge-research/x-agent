package com.wenge.model.entity;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serializable;

@IndexName
@Data
public class XBGCStoryData implements Serializable {

    private static final long serialVersionUID = -6975294494536565352L;

    @IndexId(type = IdType.CUSTOMIZE)
    private String esId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 唯一标识符
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 评论
     */
    private String comment;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 更新时间
     */
    private String updated;

    /**
     * 更新者
     */
    private String updatedBy;

    /**
     * 属性对象
     */
    @IndexField(fieldType = FieldType.NESTED, nestedClass = Properties.class)
    private Properties properties;

    /**
     * 密级
     */
    private String secretLevel;

    /**
     * 频道ID
     */
    private String channelId;

    /**
     * 栏目ID
     */
    private String columnId;

    /**
     * 作者
     */
    private String author;

    /**
     * 文章URL
     */
    private String url;

    /**
     * 故事类型
     */
    private String storyType;

    /**
     * 拖动排序时间戳
     */
    private String dragOrderTimestamp;

    /**
     * 是否置顶
     */
    private String isTop;

    /**
     * 栏目是否置顶
     */
    private String columnIsTop;

    /**
     * 缩略图样式
     */
    private String thumbnailStyle;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否允许评论
     */
    private String canComment;

    /**
     * 发布时间
     */
    private String publishTime;

    /**
     * 发布状态
     */
    private String publishStatus;

    /**
     * 字数
     */
    private String wordCount;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 编辑者
     */
    private String editor;

    /**
     * 混合媒体故事ID
     */
    private String mixmediaStoryId;

    /**
     * 发布来源
     */
    private String publishSource;

    /**
     * 主栏目
     */
    private String mainColumn;

    /**
     * 是否被类型集系统使用
     */
    private String usedByTypeSetSystem;

    /**
     * 封面类型
     */
    private String coverType;

    /**
     * 栏目名称
     */
    private String columnName;

    /**
     * 来源
     */
    private String source;

    /**
     * 审核编辑
     */
    private String reviewEditor;

    /**
     * JSON URL
     */
    private String jsonUrl;

    /**
     * 分享URL
     */
    private String shareUrl;

    /**
     * 栏目目录名
     */
    private String columnDirname;

    /**
     * 缩略图数组
     */
    @IndexField(fieldType = FieldType.NESTED, nestedClass = Thumbnails.class)
    private Thumbnails[] thumbnails;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 图片数组
     */
    @IndexField(fieldType = FieldType.NESTED, nestedClass = Images.class)
    private Images[] images;

    /**
     * 频道名称
     */
    private String channelName;

    /**
     * 图片数量
     */
    private String imageCount;

    /**
     * 媒体流对象
     */
    @IndexField(fieldType = FieldType.NESTED, nestedClass = MediaStream.class)
    private MediaStream mediaStream;

    /**
     * 引导线
     */
    private String leadinLine;

    /**
     * 分类
     */
    private String categories;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 是否锁定
     */
    private String locked;

    @Data
    public static class Properties implements Serializable {

        private static final long serialVersionUID = 1081975344771474917L;

        /**
         * 编辑者ID
         */
        private String editorId;

        /**
         * 引导线URL
         */
        private String leadinLineUrl;

        /**
         * 审核编辑ID
         */
        private String reviewEditorId;

        /**
         * 是否来自API
         */
        private String isFromApi;

        /**
         * 广告标志
         */
        private String ad;

        /**
         * 水印标志
         */
        private String watermark;

        /**
         * 创建者ID
         */
        private String creatorId;

        /**
         * 原始分类
         */
        private String originalCategory;

        /**
         * 初审人
         */
        private String firstPass;

        /**
         * 链接标题
         */
        private String linkTitle;

        /**
         * 引导线样式
         */
        private String leadinLineStyle;
    }

    @Data
    public static class Thumbnails implements Serializable {

        private static final long serialVersionUID = 8267443482661157114L;

        /**
         * 缩略图URL
         */
        private String thumbnailUrl;

        /**
         * 类型
         */
        private String type;

        /**
         * 比例
         */
        private String proportion;

        /**
         * URL
         */
        private String url;
    }

    @Data
    public static class Images implements Serializable{

        private static final long serialVersionUID = 3166178742441924463L;

        /**
         * 摘要
         */
        private String abstractText;

        /**
         * URL
         */
        private String url;
    }

    @Data
    public static class MediaStream implements Serializable{

        private static final long serialVersionUID = -4991006884988676826L;

        /**
         * URL
         */
        private String url;

        /**
         * 时长
         */
        private String duration;
    }
}
