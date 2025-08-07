package com.wenge.model.dto.result;

import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.workflow.entity.ComponentNodeRel;
import com.wenge.model.workflow.entity.MetaParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ComponentDto {
    private Long id;

    @ApiModelProperty(name = "componentId", value = "组件id 有业务作用", dataType = "String")
    private String componentId;

    @ApiModelProperty(name = "componentName", value = "组件名称", dataType = "String")
    private String componentName;

    @ApiModelProperty(name = "componentDesc", value = "组件描述", dataType = "String")
    private String componentDesc;

    @ApiModelProperty(name = "status", value = "组件状态 0未发布 1已发布", dataType = "Integer")
    private Integer status;

    @ApiModelProperty(name = "type", value = "类型 1插件", dataType = "String")
    private Integer type;

    /**
     * 组件图标
     */
    @ApiModelProperty(name = "icon", value = "组件图标", dataType = "String")
    private String icon;

    /**
     * 画布数据
     */
    @ApiModelProperty(name = "canvas", value = "画布数据", dataType = "String")
    private String canvas;

    /**
     * 创建人
     */
    @ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
    private String createUser;

    /**
     * 创建人真实姓名
     */
    @ApiModelProperty(name = "username", value = "创建人真实姓名", dataType = "String")
    private String username;

    /**
     * 创建时间
     */
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 更新人，OnFieldFill注解可以自动填充，fill为触发机制，mdcKey为MDC中key，提取其中的值
     */
    @ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
    private String updateUser;

    /**
     * 租户id
     */
    @ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
    private String tenantId;

    private String labels;

    private Integer systemPlugin;

    @ApiModelProperty(name = "nodes", value = "节点信息", dataType = "List")
    private List<ComponentNodeDto> nodes;

    @ApiModelProperty(name = "nodeRel", value = "节点关系", dataType = "List")
    private List<ComponentNodeRel> nodeRel;

    @ApiModelProperty(name = "metaParams", value = "参数元数据", dataType = "List")
    private List<MetaParam> metaParams;

    /**
     * sse连接id
     */
    private String sseClientId;

    /**
     * 工作流关联的默认应用
     */
    private ApplicationInfo applicationInfo;

    private String clientType;

    /**
     * 临时存放token
     */
    private String token;

    @ApiModelProperty(name = "publishAppStore", value = "0-未发布（默认） 1-已发布  2-待审核", dataType = "Integer")
    private Integer publishAppStore;

    private Boolean clickPublish;  //点击发布  写死传-true

    private Date publishTime;

    @ApiModelProperty(name = "publishDesc",value = "发布描述", dataType = "String")
    private String publishDesc;

    @ApiModelProperty(value = "是否所有人可见, 1:所有人可见,0:只有自己可见")
    private Integer allVisible;

    /**
     * 查询个人时区分自己和他人
     */
    @ApiModelProperty(name = "isMe",value = "查询个人时区分自己和他人", dataType = "Boolean", notes = "true:自己, flase:他人授权")
    private Boolean isMe;

    /**
     * 知识库归属,是官方创建还是个人创建
     */
    @ApiModelProperty(name = "ownerType",value = "知识库归属,是官方创建还是个人创建", dataType = "String", notes = "personal:个人创建, official:官方创建")
    private String ownerType;

    /**
     * 是否授权
     */
    private Boolean granted;

    /**
     * 克隆一个简单的对象
     *
     * @return
     */
    public ComponentDto cloneSimple() {
        ComponentDto componentDto = new ComponentDto();
        componentDto.setId(this.id);
        componentDto.setComponentId(this.componentId);
        componentDto.setComponentDesc(this.componentDesc);
        componentDto.setComponentName(this.componentName);
        componentDto.setStatus(this.status);
        componentDto.setType(this.type);
        componentDto.setIcon(this.icon);
        return componentDto;
    }

}
