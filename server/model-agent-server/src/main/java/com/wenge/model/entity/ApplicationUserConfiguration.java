package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Table(value = "application_user_configuration")
public class ApplicationUserConfiguration {

    /**
     * 主键，自增id，没有业务作用
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(name = "id", value = "主键，自增id，没有业务作用", dataType = "Long")
    private Long id;

    /**
     * 应用id
     */
    @Column("application_id")
    private String applicationId;

    /**
     * 用户id
     */
    @Column("user_id")
    private String userId;

    /**
     * 流式语音开关 1打开 0关闭
     */
    @Column("stream_voice")
    private Integer streamVoice;


}
