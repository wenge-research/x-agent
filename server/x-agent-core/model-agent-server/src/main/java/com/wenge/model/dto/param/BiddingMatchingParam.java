package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BiddingMatchingParam extends WGParam {

    private static final long serialVersionUID = 8423288429253667684L;

    /**
     * AppId
     */
    private String appId;

    /**
     * 招标资质
     */
    private String bidding;

    /**
     * 投标内容
     */
    private String bid;

    /**
     * 对话内容
     */
    private String conversation;

}
