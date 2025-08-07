package com.wenge.model.utils;


import com.wenge.model.enums.HeadStyleEnum;
import com.wenge.model.enums.ParagraphEnum;
import lombok.Data;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class WordBean implements Serializable {

    private static final long serialVersionUID = 5742383669261125752L;

    private ParagraphEnum type;

    private HeadStyleEnum titleLevel;

    private ParagraphAlignment alignment;

    private String text;

    /**
     * 表格数据
     */
    private List<List<String>> tableDataList;

    /**
     * 图表标题
     */
    private String charTitle;

    /**
     * 饼图图例
     */
    private List<String> pieTitleList;

    /**
     * 饼图数据
     */
    private List<Double> pieDataList;

    /**
     * 横坐标标题
     */
    private String lineXAxisTitle;

    /**
     * 纵坐标标题
     */
    private String lineYAxisTitle;

    /**
     * 横坐标数据
     */
    private List<String> quarterList;

    /**
     * 折线数据序列
     */
    private Map<String, List<Number>> lineMapData;

    /**
     * 条形数据序列
     */
    private Map<String, List<Number>> barMapData;

    /**
     * 图片路径
     */
    private String imagePath;

    /**
     * 图片宽度
     */
    private Integer imgWidth;

    /**
     * 图片高度
     */
    private Integer imgHeight;
}
