package com.wenge.model.dto.param;

import com.mybatisflex.annotation.Column;
import com.wenge.model.entity.LlmInfo;
import lombok.Data;

/**
 * @author: caohaifeng
 * @date: 2024/8/14 14:43
 * @Description: 大模型
 * @Version:1.0
 **/
@Data
public class LlmInfoAddUpdateParam extends LlmInfo {

    /**
     * 默认为空，deploy：一键部署
     */
    @Column(ignore = true)
    private String way;

    /**
     * 模型型号
     */
    @Column(ignore = true)
    private String modelVersion;

    /**
     * 服务器 ip
     */
    @Column(ignore = true)
    private String hostIp;

    /**
     * 平台，window linux macos
     */
    @Column(ignore = true)
    private String platform;
}
