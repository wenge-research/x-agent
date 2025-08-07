package com.wenge.model.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "对话响应参数")
public class DialogueVO implements Serializable {

    private static final long serialVersionUID = 905593815469591534L;

    @ApiModelProperty(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "问题")
    private String question;

    @ApiModelProperty(value = "提示词")
    private String prompt;

    @ApiModelProperty(value = "答案")
    private String answer;

    @ApiModelProperty(value = "参数")
    private String param;

    @ApiModelProperty(value = "模型版本")
    private String modelVersion;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "【对话】来源插件：cailianshe 财联社；wenhai 闻海")
    private String source;

    @ApiModelProperty(value = "1 赞 ,0 踩")
    private Integer feedBackStatus;

    @ApiModelProperty(value = "是否欠费：true 是")
    private Boolean isOverdue;

    @ApiModelProperty("[长文档问答]文件id")
    @JsonSerialize(using = ToStringSerializer.class)
    private String fileId;

    @ApiModelProperty("[长文档问答]文件名称/网址title")
    private String fileName;

    @ApiModelProperty("[长文档问答]文件地址/网址")
    private String fileUrl;

    @ApiModelProperty("[长文档问答]字数")
    private Integer wordCount;

    @ApiModelProperty("[长文档问答]上传内容类型：img：图片、doc：文档、web：网址")
    private String type;

    @ApiModelProperty("指定文稿id")
    private String docId;

    @ApiModelProperty("指定文稿信息")
    private String docInfo;

    @ApiModelProperty("提问涉及敏感内容：true 是; false 否")
    private Boolean sensitive;

    @ApiModelProperty("耗时，单位：秒")
    private Float consumingTime;

    @ApiModelProperty("插件输出")
    private String pluginOutput;

    @ApiModelProperty("推荐插件")
    private String pluginRecommended;

    @ApiModelProperty("[雅意file] 对话文件 ID")
    private List<String> dialogueFileIds;

    @ApiModelProperty("[雅意file] 对话文件夹 ID")
    private List<String> dialogueFolderIds;

    @ApiModelProperty("[雅意file] 引用段落")
    private String paragraph;

    @ApiModelProperty("[雅意file] 指令ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long skillId;

    @ApiModelProperty(value = "text")
    private JSONObject data;


}
