package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationInfoPageParam extends WgPageInfo {

    private static final long serialVersionUID = -2636192558690322065L;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 应用id
     */
    private List<String> applicationIdLists;

    /**
     * 应用类型，null或空字符串:问答助手； search:智能搜索； workflow:工作流；dialogue-search:智能搜索和问答助手
     */
    private String type;

    private List<String> typeLists;


    private boolean onlyMine;

    private boolean operationXiazhuan; //是否运营模块下砖

    private Integer publishAppStore; //0-未发布 1-已发布 2-待审核

    private String publishType;  //发布分类（聊天助手、AI翻译、AI搜索、文案写作、行业报告、图片创作、学习助手、合规审查


    @ApiModelProperty(value = "来源 1-应用 2-插件 3-其他")
    private Integer messageSource = 1;

    /**
     * 收藏标识 0：未收藏，1：已收藏
     */
    private Integer favoriteFlag;

    private String knowledgeId; // 知识库id

    private boolean needParam = false;
    private String remark;
    private String startTime;
    private String endTime;
}
