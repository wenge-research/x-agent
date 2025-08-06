package com.wenge.model.enums;

import com.mybatisflex.core.query.QueryColumn;
import com.wenge.oauth.service.TablePermissionService;
import lombok.Getter;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.ComponentNodeTableInfoTableDef.COMPONENT_NODE_TABLE_INFO;
import static com.wenge.model.entity.table.InterceptWordHouseTableDef.INTERCEPT_WORD_HOUSE;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;
import static com.wenge.model.entity.table.LlmInfoTableDef.LLM_INFO;
import static com.wenge.model.entity.table.MatterGuideTableDef.MATTER_GUIDE;
import static com.wenge.model.entity.table.McpServerTableDef.MCP_SERVER;
import static com.wenge.model.entity.table.PromptConfigTableDef.PROMPT_CONFIG;
import static com.wenge.model.entity.table.SceneManagementTableDef.SCENE_MANAGEMENT;
import static com.wenge.model.entity.table.VoiceComponentInfoTableDef.VOICE_COMPONENT_INFO;
import static com.wenge.model.workflow.entity.table.ComponentTableDef.COMPONENT;

@Getter
public enum BusinessPermissionEnum implements TablePermissionService {

    // 应用，知识库
    APPLICATION(APPLICATION_INFO.TENANT_ID, APPLICATION_INFO.CREATE_USER, APPLICATION_INFO.PUBLISH_APP_STORE, APPLICATION_INFO.APPLICATION_ID, null, "app"),

    // 知识库
    KNOWLEDGE(KNOWLEDGE_INFO.TENANT_ID, KNOWLEDGE_INFO.CREATE_USER, null, KNOWLEDGE_INFO.KNOWLEDGE_ID, KNOWLEDGE_INFO.OWNER_TYPE, "knowledge"),

    // 大模型
    LLM(LLM_INFO.TENANT_ID, LLM_INFO.CREATE_USER, null, LLM_INFO.MODEL_ID, LLM_INFO.OWNER_TYPE, "llmInfo"),

    // 工作流
    WORKFLOW(COMPONENT.TENANT_ID, COMPONENT.CREATE_USER, null, COMPONENT.COMPONENT_ID, COMPONENT.OWNER_TYPE, "workflow"),
    // 场景
    SCENE(SCENE_MANAGEMENT.TENANT_ID, SCENE_MANAGEMENT.CREATE_USER, null, SCENE_MANAGEMENT.SCENE_ID, null, "scene"),

    // 提示词
    PROMPT(PROMPT_CONFIG.TENANT_ID, PROMPT_CONFIG.CREATE_USER, null, PROMPT_CONFIG.PROMPT_ID, PROMPT_CONFIG.OWNER_TYPE, "prompt"),

    // 敏感词
    INTERCEPT_WORD(INTERCEPT_WORD_HOUSE.TENANT_ID, INTERCEPT_WORD_HOUSE.CREATE_USER_ACCOUNT, null, INTERCEPT_WORD_HOUSE.ID, INTERCEPT_WORD_HOUSE.OWNER_TYPE, "intercept"),

    // 语音
    VOICE(VOICE_COMPONENT_INFO.TENANT_ID, VOICE_COMPONENT_INFO.CREATE_USER, null, VOICE_COMPONENT_INFO.COMPONENT_ID, null, "voice"),

    // 插件
    PLUGIN(COMPONENT.TENANT_ID, COMPONENT.CREATE_USER, COMPONENT.PUBLISH_APP_STORE, COMPONENT.COMPONENT_ID, COMPONENT.OWNER_TYPE, "plugin"),

    // 事项
    MATTER(MATTER_GUIDE.TENANT_ID, MATTER_GUIDE.CREATE_USER_NAME, null, MATTER_GUIDE.MATTER_ID, null, "matter"),

    // 工作流数据库节点
    WORKFLOW_MYSQL_NODE(COMPONENT_NODE_TABLE_INFO.TENANT_ID, COMPONENT_NODE_TABLE_INFO.CREATE_USER, null, COMPONENT_NODE_TABLE_INFO.TABLE_ID, null, "workflow_mysql_node"),

    // mcp
    MCP(null, MCP_SERVER.CREATE_USER_NAME, null, MCP_SERVER.MCP_ID, MCP_SERVER.OWNER_TYPE, "mcp_server"),


    ;

    // 租户id，创建用户，数据id，数据类型
    private final QueryColumn tenantId;
    private final QueryColumn createUser;
    private final QueryColumn dataId;
    private final QueryColumn ownerType;
    private final String dataType;
    private final QueryColumn publishAppStore;

    BusinessPermissionEnum(QueryColumn tenantId, QueryColumn createUser, QueryColumn publishAppStore, QueryColumn dataId, QueryColumn ownerType, String dataType) {
        this.tenantId = tenantId;
        this.createUser = createUser;
        this.publishAppStore = publishAppStore;
        this.dataId = dataId;
        this.dataType = dataType;
        this.ownerType = ownerType;
    }
}
