package com.wenge.model.dto.param.wenhai;

/**
 * HiddenMark, 发文显隐标记信息</br><span
 * style='color:red;'>注意</span>：hideMarkId和isHidden两个字段都不为空，才会生效
 */
@lombok.Data
public class HiddenMark {
    /**
     * 隐藏字段标识【格式：用户ID-方案ID】
     */
    private String hideMark;
    /**
     * 是否显示剔除内容【true-显示剔除内容、false-不显示剔除内容】
     */
    private Boolean isHidden;
}
