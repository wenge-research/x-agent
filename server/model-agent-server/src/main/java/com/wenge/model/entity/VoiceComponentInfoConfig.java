package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.MybatisFileConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description:语音组件配置
 * @Author: zs
 * @Date: 2025/4/22 10:30
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "voice_component_info_config", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class VoiceComponentInfoConfig extends FlexModel<VoiceComponentInfoConfig> implements Serializable {
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
     * 配置id（业务id）
     */
    @Column("config_id")
    @ApiModelProperty(name = "configId",value = "配置id（业务id）", dataType = "String")
    private String configId;

    /**
     * 关联声音主键id（主要是兼容老业务）
     */
    @Column(value = "voice_id")
    @ApiModelProperty(name = "voiceId", value = "关联声音主键id", dataType = "Long")
    private Long voiceId;


    /**
     * 关联声音组件id
     */
    @Column("voice_component_id")
    @ApiModelProperty(name = "voiceComponentId",value = "关联声音组件id", dataType = "String")
    private String voiceComponentId;

    /**
     * 风格
     */
    @Column("style")
    @ApiModelProperty(name = "style",value = "风格", dataType = "String")
    private String style;

    /**
     * 常用标识 0-非常用 1-常用
     */
    @Column(value = "frequence_use_flag")
    @ApiModelProperty(name = "frequenceUseFlag", value = "常用标识 0-非常用 1-常用", dataType = "Integer")
    private Integer frequenceUseFlag;

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
     * 完整风格（声音组件名称-风格）
     */
    @Column(ignore = true)
    private String fullStyle;

}