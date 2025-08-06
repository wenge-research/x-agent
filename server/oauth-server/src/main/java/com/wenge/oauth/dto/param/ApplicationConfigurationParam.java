package com.wenge.oauth.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationConfigurationParam extends WgPageInfo {

    private String keyword;

    /**
     * 主键，自增id
     */
    private Long id;

    /**
     * 应用id，有业务作用
     */
    private String applicationId;

    /**
     * key值
     */

    private String keyInfo;

    /**
     * value值
     */
    private String valueInfo;

    /**
     *   备注说明
     */
    private String remark;

    /**
     * 是否有效 0-有效 1-无效
     */
    private Integer status;

    /**
     * 需要删除的配置ids
     **/
    private List<Long> delIds;
}
