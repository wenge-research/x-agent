package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@Table(value = "youya_data_entries", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class YouyaDataEntries {

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id", value = "主键", dataType = "String")
    private int id;

    @Column("analysis_id")
    @ApiModelProperty(name = "analysisId", value = "关联的任务ID", dataType = "String")
    private String analysisId;

    @Column("start")
    @ApiModelProperty(name = "start", value = "拆条视频开始时间戳", dataType = "String")
    private float start;

    @Column("length")
    @ApiModelProperty(name = "length", value = "视频长度", dataType = "String")
    private float length;

    @Column( "angle")
    @ApiModelProperty(name = "angle", value = "拍摄角度", dataType = "String")
    private String angle;

    @Column("atmosphere")
    @ApiModelProperty(name = "atmosphere", value = "氛围", dataType = "String")
    private String atmosphere;

    @Column("camera")
    @ApiModelProperty(name = "camera", value = "视角", dataType = "String")
    private String camera;

    @Column("caption")
    @ApiModelProperty(name = "caption", value = "视频描述", dataType = "String")
    private String caption;

    @Column( "day")
    @ApiModelProperty(name = "day", value = "时间段", dataType = "String")
    private String day;

    @Column("element")
    @ApiModelProperty(name = "element", value = "元素", dataType = "String")
    private String element;

    @Column("field")
    @ApiModelProperty(name = "field", value = "领域", dataType = "String")
    private String field;

    @Column("season")
    @ApiModelProperty(name = "season", value = "季节", dataType = "String")
    private String season;

    @Column("style")
    @ApiModelProperty(name = "style", value = "风格", dataType = "String")
    private String style;

    @Column("topic")
    @ApiModelProperty(name = "topic", value = "主题", dataType = "String")
    private String topic;

    @Column("height")
    @ApiModelProperty(name = "height", value = "视频高度", dataType = "String")
    private int height;

    @Column("width")
    @ApiModelProperty(name = "width", value = "视频宽度", dataType = "String")
    private int width;

    @Column("video")
    @ApiModelProperty(name = "video", value = "拆条视频URL", dataType = "String")
    private String video;

    @Column("video_preview")
    @ApiModelProperty(name = "videoPreview", value = "预览视频URL", dataType = "String")
    private String videoPreview;

    @Column("video_cover")
    @ApiModelProperty(name = "videoCover", value = "拆条封面图URL", dataType = "String")
    private String videoCover;

    @Column("behavior")
    @ApiModelProperty(name = "behavior", value = "事件行为", dataType = "String")
    private String behavior;

    @Column("feel")
    @ApiModelProperty(name = "feel;", value = "情感感受", dataType = "String")
    private String feel;

    @Column("character")
    @ApiModelProperty(name = "character", value = "人物属性", dataType = "String")
    private String character;

    @Column("scene")
    @ApiModelProperty(name = "scene", value = "场景", dataType = "String")
    private String scene;

    @Column("era")
    @ApiModelProperty(name = "era", value = "时代", dataType = "String")
    private String era;

    @Column("subtitles")
    @ApiModelProperty(name = "subtitles", value = "幕信息", dataType = "String")
    private String subtitles;

    @Column("cover_url")
    @ApiModelProperty(name = "coverUrl", value = "拆条起始帧截图URL", dataType = "String")
    private String coverUrl;

}
