package com.wenge.model.entity;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

@EqualsAndHashCode(callSuper = true)
@IndexName
public class StructuredOriginalData extends JSONObject {

    private static final long serialVersionUID = 6623794465706444529L;

    /**
     * id
     */
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    @IndexId(type = IdType.UUID)
    private String id;

    /**
     * 结构表id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String tableId;

    /**
     * 知识库id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String knowledgeId;

    /**
     * 创建时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    public String getCreateTime() {
        return getString("createTime");
    }

    public String getKnowledgeId() {
        return getString("knowledgeId");
    }

    public String getTableId() {
        return getString("tableId");
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
        put("tableId", tableId);
    }

    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId;
        put("knowledgeId", knowledgeId);
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        put("createTime", createTime);
    }

    public String getId() {
        return getString("id");
    }

    public void setId(String id) {
        this.id = id;
        put("id", id);
    }
}
