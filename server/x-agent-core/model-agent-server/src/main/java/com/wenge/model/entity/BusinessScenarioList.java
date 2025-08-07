package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.model.constants.MybatisFileConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@ApiModel
//@Data
@Data(staticConstructor = "create")
@Table(value = "business_scenario_list", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class BusinessScenarioList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id",value = "主键，自增id，没有业务作用", dataType = "Long")
    private Long id;

    @Column("business_scenari_pid")
    @ApiModelProperty(name = "businessScenariPid",value = "上一级-业务场景ID 最高级默认为null或0", dataType = "String")
    private String businessScenariPid;

    @Column("business_scenari_id")
    @ApiModelProperty(name = "businessScenariId",value = "业务场景ID", dataType = "String")
    private String businessScenariId;

    @Column("application_id")
    @ApiModelProperty(name = "applicationId",value = "应用ID", dataType = "String")
    private String applicationId;

    @Column("name")
    @ApiModelProperty(name = "name", value = "场景名称", dataType = "String")
    private String name;

    @Column("description")
    @ApiModelProperty(name = "description", value = "场景描述", dataType = "String")
    private String description;

    @Column("talk_content")
    @ApiModelProperty(name = "talkContent", value = "场景描述", dataType = "String")
    private String talkContent;

    @Column("hit_keywords")
    @ApiModelProperty(name = "hitKeywords", value = "场景描述", dataType = "String")
    private String hitKeywords;

    @Column("upload_pic_hit_keywords")
    @ApiModelProperty(name = "pidHitKeywords", value = "上传图片或者文件触发的场景文本", dataType = "String")
    private String pidHitKeywords;

    @Column("category")
    @ApiModelProperty(name = "category", value = "类别 例如：卫生、安全等", dataType = "String")
    private String category;

    @Column("icon")
    @ApiModelProperty(name = "icon", value = "场景图标", dataType = "String")
    private String icon;

    @Column("show_flag")
    @ApiModelProperty(name = "showFlag", value = "是否显示[是/否]", dataType = "String")
    private String showFlag;


    @Column("delete_flag")
    @ApiModelProperty(name = "deleteFlag", value = "是否删除[1-删除,0-未删除]", dataType = "Integer")
    private Integer deleteFlag = 0;

    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    @Column("update_user")
    @ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFileConstant.MDC_USER_NAME)
    private String updateUser;

    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    @Column("create_user")
    @ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFileConstant.MDC_USER_NAME)
    private String createUser;


    @Column("business_scenario_upload_pic")
    @ApiModelProperty(name = "businessScenarioUploadPicStatus", value = "是否需要上传图片[是/否]", dataType = "String")
    private String businessScenarioUploadPicStatus;




}
