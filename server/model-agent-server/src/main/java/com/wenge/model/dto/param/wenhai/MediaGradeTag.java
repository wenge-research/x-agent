package com.wenge.model.dto.param.wenhai;

import java.util.List;

/**
 * MediaGradeTag, 标签信息分类,详见《字典接口模块》,各个字段取值详见字段说明</br><span
 * style='color:red;'>注意</span>：输入的是多个对象的层级标签，对象内部的字段属性是或的关系，对象与对象之间关系是与的关系
 */
@lombok.Data
public class MediaGradeTag {
    /**
     * 一级标签,详见《字典接口模块》指定标签层级查询该层级下子标签列表接口【level=1,取返回结果levelId字段值】
     */
    private List<String> tags1;
    /**
     * 二级标签,详见《字典接口模块》指定标签层级查询该层级下子标签列表接口【level=2,取返回结果levelId字段值】
     */
    private List<String> tags2;
    /**
     * 三级标签,详见《字典接口模块》指定标签层级查询该层级下子标签列表接口【level=3,取返回结果levelId字段值】
     */
    private List<String> tags3;
    /**
     * 四级标签,详见《字典接口模块》指定标签层级查询该层级下子标签列表接口【level=4,取返回结果levelId字段值】
     */
    private List<String> tags4;
    /**
     * 五级标签,详见《字典接口模块》指定标签层级查询该层级下子标签列表接口【level=5,取返回结果levelId字段值】
     */
    private List<String> tags5;
    /**
     * 六级标签,详见《字典接口模块》指定标签层级查询该层级下子标签列表接口【level=6,取返回结果levelId字段值】
     */
    private List<String> tags6;
    /**
     * 七级标签,详见《字典接口模块》指定标签层级查询该层级下子标签列表接口【level=7,取返回结果levelId字段值】
     */
    private List<String> tags7;
}
