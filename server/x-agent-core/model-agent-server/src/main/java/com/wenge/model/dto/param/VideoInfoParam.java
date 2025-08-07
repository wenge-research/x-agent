package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.wenge.model.constants.MybatisFileConstant;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.core.bean.WgPageInfo;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class VideoInfoParam extends WgPageInfo {

    /**
     * 视频标题
     */
    private String title;

    /**
     * 发布状态(0:未发布,1:已发布)
     */
    private Integer publishedStatus;

    /**
     * 审核状态(0:未审核,1:审核通过,2:审核不通过)
     */
    private Integer reviewStatus;

    /**
     * 业务视频ID(32位)
     */
    private String videoId;

    /**
     * 0-首页  1-个人
     */
    private Integer displayScope;

}

