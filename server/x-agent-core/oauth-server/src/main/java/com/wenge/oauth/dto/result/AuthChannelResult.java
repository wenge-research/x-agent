package com.wenge.oauth.dto.result;

import com.wg.appframe.core.dto.results.WGResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthChannelResult extends WGResult {

    private static final long serialVersionUID = -1666830195418388472L;

    /**
     * 认证渠道id
     */
    @ApiModelProperty(name = "authChannelId", value = "认证渠道id", dataType = "String")
    private String authChannelId;

    /**
     * 认证渠道名称
     */
    @ApiModelProperty(name = "channelName", value = "认证渠道名称", dataType = "String")
    private String channelName;

    /**
     * 认证渠道编码
     */
    @ApiModelProperty(name = "channelCode", value = "认证渠道编码", dataType = "String")
    private String channelCode;

}
