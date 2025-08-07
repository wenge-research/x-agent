package com.wenge.model.vo;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 17:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormInfoVo {

    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    @Column("filed_Name")
    @ApiModelProperty(value = "字段中文名")
    private String filedName;

    @Column("filed_code")
    @ApiModelProperty(value = "字段名")
    private String filedCode;


    @Column("form_type")
    @ApiModelProperty(value = "表单类型，input/textarea/select/radio/checkbox/date/time/datetime/file/image/video/audio/number/email/url/tel/password/hidden/button/submit/reset/text/textarea/select/radio/checkbox/date/time/datetime/file/image/video/audio/number/email/url/tel/password/hidden/button/submit/reset/text/textarea/")
    private String formType;


    @Column("show_flag")
    @ApiModelProperty(value = "是否显示，是/否")
    private String showFlag;

}