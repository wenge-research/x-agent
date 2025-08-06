package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.MybatisFileConstant;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:政务组件
 * @Author: Levi
 * @Date: 2024/6/21 14:15
 */
@ApiModel
@Data
@Table(value = "voice_component_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class VoiceComponentInfo extends FlexModel<VoiceComponentInfo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    /**
     * 主键，自增id，没有业务作用
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id",value = "主键，自增id，没有业务作用", dataType = "Long")
    private Long id;

    /**
     * 组件业务ID
     */
    @Column("component_id")
    @ApiModelProperty(name = "componentId",value = "组件业务ID", dataType = "String")
    private String componentId;

    /**
     * 组件名称
     */
    @Column("component_name")
    @ApiModelProperty(name = "componentName",value = "组件名称", dataType = "String")
    private String componentName;

    /**
     * 组件简介
     */
    @Column("introduce")
    @ApiModelProperty(name = "introduce",value = "组件简介", dataType = "String")
    private String introduce;

    /**
     * 组件图标
     */
    @Column("component_icon")
    @ApiModelProperty(name = "componentIcon", value = "组件图标", dataType = "String")
    private String componentIcon;

    /**
     * 组件分类
     */
    @Column("category")
    @ApiModelProperty(name = "category", value = "组件分类", dataType = "String")
    private String category;

    /**
     * 组件标签，多个以逗号隔开
     */
    @Column("tag")
    @ApiModelProperty(name = "category", value = "组件标签，多个以逗号隔开", dataType = "String")
    private String tag;

    /**
     * 语音链接
     */
    @Column("voice_link")
    @ApiModelProperty(name = "voiceLink", value = "语音链接", dataType = "String")
    private String voiceLink;

    /**
     * 音频编码
     */
    @Column("audio_frequency_encoding")
    @ApiModelProperty(name = "audioFrequencyEncoding", value = "音频编码", dataType = "String")
    private String audioFrequencyEncoding;

    /**
     * 发音人
     */
    @Column("voice_people")
    @ApiModelProperty(name = "voicePeople", value = "发音人", dataType = "String")
    private String voicePeople;

    /**
     * apiKey
     */
    @Column("api_key")
    @ApiModelProperty(name = "apiKey", value = "apiKey", dataType = "String")
    private String apiKey;

    /**
     * apiSecret
     */
    @Column("api_secret")
    @ApiModelProperty(name = "apiSecret", value = "apiSecret", dataType = "String")
    private String apiSecret;

    /**
     * 是否开启流式 1开启 2关闭
     */
    @Column("stream_flag")
    @ApiModelProperty(name = "streamFlag", value = "是否开启流式 1开启 2关闭", dataType = "Integer")
    private Integer streamFlag;

    /**
     * 语速
     */
    @Column("voice_speed")
    @ApiModelProperty(name = "voiceSpeed", value = "语速", dataType = "String")
    private BigDecimal voiceSpeed;

    /**
     * 音量
     */
    @Column("volume")
    @ApiModelProperty(name = "volume", value = "音量", dataType = "String")
    private String volume;

    /**
     * 音高
     */
    @Column("pitch")
    @ApiModelProperty(name = "pitch", value = "音高", dataType = "String")
    private String pitch;

    /**
     * 数字发音方式
     */
    @Column("number_pronunciation_type")
    @ApiModelProperty(name = "numberPronunciationType", value = "数字发音方式", dataType = "String")
    private String numberPronunciationType;

    /**
     * 音频采样率
     */
    @Column("audio_sample_rate")
    @ApiModelProperty(name = "audioSampleRate", value = "音频采样率", dataType = "Integer")
    private Integer audioSampleRate;

    /**
     * 文本编码格式
     */
    @Column("text_encoding_format")
    @ApiModelProperty(name = "textEncodingFormat", value = "文本编码格式", dataType = "String")
    private String textEncodingFormat;

    /**
     * 背景音
     */
    @Column("bgm")
    @ApiModelProperty(name = "bgm", value = "背景音", dataType = "String")
    private String bgm;

    @Column(value = "delete_flag", isLogicDelete = true)
    @ApiModelProperty(name = "deleteFlag", value = "是否删除[1-删除,0-未删除]", dataType = "Integer")
    private Integer deleteFlag = 0;

    /**
     * 更新时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 更新人，OnFieldFill注解可以自动填充，fill为触发机制，mdcKey为MDC中key，提取其中的值
     */
    @Column("update_user")
    @ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFileConstant.MDC_USER_NAME)
    private String updateUser;

    /**
     * 创建时间
     */
    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    /**
     * 创建人
     */
    @Column("create_user")
    @ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFileConstant.MDC_USER_NAME)
    private String createUser;

    /**
     * 合成音频的格式，例如mp3
     */
    @Column("audio_type")
    @ApiModelProperty(name = "audioType", value = "合成音频的格式，例如mp3", dataType = "String")
    private String audioType;

    /**
     * 厂商策略，mobvoiStrategy：魔音工坊
     */
    @Column("strategy")
    @ApiModelProperty(name = "strategy", value = "厂商策略，mobvoiStrategy：魔音工坊", dataType = "String")
    private String strategy;

    /**
     * 性别[F-女性,M-男性]
     */
    @Column("gender")
    @ApiModelProperty(name = "gender", value = "性别[F-女性,M-男性]", dataType = "String")
    private String gender;

    /**
     * 头像图片地址
     */
    @Column("head_portrait_url")
    @ApiModelProperty(name = "headPortraitUrl", value = "头像图片地址", dataType = "String")
    private String headPortraitUrl;

    /**
     * 语音形式(tts：语音识别,stt：语音合成)
     */
    @Column("module")
    @ApiModelProperty(name = "module", value = "语音形式(tts：语音识别,stt：语音合成)", dataType = "String")
    private String module;

    /**
     * 取值范围[1,5]，通过设置此参数，获取在发音相似时的词语多侯选结果。设置多候选会影响性能，响应时间延迟200ms左右。
     * 注：该扩展功能若未授权无法使用，可到控制台-语音听写（流式版）-高级功能处免费开通；若未授权状态下设置该参数并不会报错，但不会生效。
     */
    @Column("wbest")
    @ApiModelProperty(name = "wbest", value = "获取在发音相似时的词语多侯选结果", dataType = "String")
    private Integer wbest;

    /**
     * 取值范围[1,5]，通过设置此参数，获取在发音相似时的句子多侯选结果。设置多候选会影响性能，响应时间延迟200ms左右。
     * 注：该扩展功能若未授权无法使用，可到控制台-语音听写（流式版）-高级功能处免费开通；若未授权状态下设置该参数并不会报错，但不会生效。
     */
    @Column("nbest")
    @ApiModelProperty(name = "nbest", value = "获取在发音相似时的句子多侯选结果", dataType = "String")
    private Integer nbest;

    /**
     * （仅中文支持）字体
     * zh-cn :简体中文（默认值）
     * zh-hk :繁体香港
     * 注：该繁体功能若未授权无法使用，可到控制台-语音听写（流式版）-高级功能处免费开通；若未授权状态下设置为繁体并不会报错，但不会生效。
     */
    @Column("rlang")
    @ApiModelProperty(name = "rlang", value = "字体", dataType = "String")
    private String rlang;

    /**
     * 仅中文支持）是否开启标点符号添加
     * 1：开启（默认值）
     * 0：关闭
     */
    @Column("ptt")
    @ApiModelProperty(name = "ptt", value = "是否开启标点符号添加", dataType = "String")
    private Integer ptt;

    /**
     * （中文普通话和日语支持）将返回结果的数字格式规则为阿拉伯数字格式，默认开启
     * 0：关闭
     * 1：开启
     */
    @Column("nunum")
    @ApiModelProperty(name = "nunum", value = "将返回结果的数字格式规则为阿拉伯数字格式", dataType = "Integer")
    private Integer nunum;

    /**
     * 返回子句结果对应的起始和结束的端点帧偏移值。端点帧偏移值表示从音频开头起已过去的帧长度。
     * 0：关闭（默认值）
     * 1：开启
     * 开启后返回的结果中会增加data.result.vad字段，详见下方返回结果。
     * 注：若开通并使用了动态修正功能，则该功能无法使用。
     */
    @Column("vinfo")
    @ApiModelProperty(name = "vinfo", value = "返回子句结果对应的起始和结束的端点帧偏移值", dataType = "Integer")
    private Integer vinfo;

    /**
     * 用于设置后端点检测的静默时间，单位是毫秒。
     * 即静默多长时间后引擎认为音频结束。
     * 默认2000（小语种除外，小语种不设置该参数默认为未开启VAD）。
     */
    @Column("vad_eos")
    @ApiModelProperty(name = "vadEos", value = "用于设置后端点检测的静默时间，单位是毫秒", dataType = "String")
    private Integer vadEos;

    /**
     * 方言，当前仅在language为中文时，支持方言选择。
     * mandarin：中文普通话、其他语种
     * 其他方言：可到控制台-语音听写（流式版）-方言/语种处添加试用或购买，添加后会显示该方言参数值；方言若未授权无法使用会报错11200。
     */
    @Column("accent")
    @ApiModelProperty(name = "accent", value = "方言", dataType = "String")
    private String accent;

    /**
     *应用领域
     * iat：日常用语
     * medical：医疗
     * gov-seat-assistant：政务坐席助手
     * seat-assistant：金融坐席助手
     * gov-ansys：政务语音分析
     * gov-nav：政务语音导航
     * fin-nav：金融语音导航
     * fin-ansys：金融语音分析
     * 注：除日常用语领域外其他领域若未授权无法使用，可到控制台-语音听写（流式版）-高级功能处添加试用或购买；若未授权无法使用会报错11200。
     * 坐席助手、语音导航、语音分析相关垂直领域仅适用于8k采样率的音频数据，另外三者的区别详见下方。
     */
    @Column("domain")
    @ApiModelProperty(name = "domain", value = "应用领域", dataType = "String")
    private String domain;

    /**
     * 语种
     * zh_cn：中文（支持简单的英文识别）
     * en_us：英文
     * 其他小语种：可到控制台-语音听写（流式版）-方言/语种处添加试用或购买，添加后会显示该小语种参数值，若未授权无法使用会报错11200。
     * 另外，小语种接口URL与中英文不同
     */
    @Column("language")
    @ApiModelProperty(name = "language", value = "语种", dataType = "String")
    private String language;

    /**
     * 应用id
     * appId
     */
    @Column("app_id")
    @ApiModelProperty(name = "appId", value = "应用id", dataType = "String")
    private String appId;

    /**
     * 租户id
     */
    @Column(value = "tenant_id")
    @ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
    private String tenantId;

    /**
     * 常用标识 0-非常用 1-常用
     */
    @Column(value = "frequence_use_flag")
    @ApiModelProperty(name = "frequenceUseFlag", value = "常用标识 0-非常用 1-常用", dataType = "Integer")
    private Integer frequenceUseFlag;

    /**
     * 语音创建归属,是官方创建还是个人创建
     */
    @Column(value = "owner_type")
    @ApiModelProperty(name = "ownerType",value = "语音创建归属,是官方创建还是个人创建", dataType = "String", notes = "personal:个人创建, official:官方创建")
    private String ownerType;

    /**
     * 声音组件配置列表
     */
    @Column(ignore = true)
    private List<VoiceComponentInfoConfig> voiceComponentInfoConfigList;

    /**
     * 组件标签列表（标签逗号分隔出来形成的列表）
     */
    @Column(ignore = true)
    private List<String> tagList;
}