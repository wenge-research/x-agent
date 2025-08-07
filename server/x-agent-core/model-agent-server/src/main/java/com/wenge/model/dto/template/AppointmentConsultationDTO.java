package com.wenge.model.dto.template;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@ApiModel
public class AppointmentConsultationDTO {

    /**
     * 序号
     */
    @ExcelProperty("序号")
    private String serialNumber;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private String createTime;

    /**
     * 业务场景
     */
    @ExcelProperty("业务场景")
    private String businessScenarios;

    /**
     * 咨询人姓名
     */
    @ExcelProperty("姓名")
    private String name;

    /**
     * 咨询人电话
     */
    @ExcelProperty("联系电话")
    private String phone;

    /**
     * 咨询人邮箱
     */
    @ExcelProperty("邮箱")
    private String email;

    /**
     * 公司名称
     */
    @ExcelProperty("公司名称")
    private String companyName;

    /**
     * 公司主页
     */
    @ExcelProperty("公司主页")
    private String companyWebsite;

    /**
     * 公司类型（初创公司、小微公司、中型公司、大型公司、国有企业、政府部门）
     */
    @ExcelProperty("公司类型")
    private String companyType;

    /**
     * 公司职务
     */
    @ExcelProperty("职务")
    private String position;
}
