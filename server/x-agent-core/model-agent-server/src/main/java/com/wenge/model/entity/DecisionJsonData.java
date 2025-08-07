package com.wenge.model.entity;

import cn.hutool.json.JSONObject;
import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;

import java.io.Serializable;

@Data
@IndexName
public class DecisionJsonData implements Serializable {

    private static final long serialVersionUID = -571170345546253490L;

    @IndexId
    private String id;

    @IndexField(fieldType = FieldType.KEYWORD)
    private String sessionId;

    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String fileName;

    @IndexField(fieldType = FieldType.TEXT)
    private JSONObject detail;

}
