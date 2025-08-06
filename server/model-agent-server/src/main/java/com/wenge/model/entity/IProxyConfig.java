package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IProxyConfig {

    @Column("host")
    @ApiModelProperty(name = "host", value = "IProxyConfig类型属性之一，代理服务域名", dataType = "String")
    private String host;

    @Column("protocol")
    @ApiModelProperty(name = "protocol", value = "IProxyConfig类型属性之一，代理服务器的协议类型，如 'http' 或 'https'，", dataType = "String")
    private String protocol;
}
