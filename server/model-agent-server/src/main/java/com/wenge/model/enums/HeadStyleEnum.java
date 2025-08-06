package com.wenge.model.enums;

import lombok.Getter;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

@Getter
public enum HeadStyleEnum {

    H_1("H1", 22, 17, 17, ParagraphAlignment.LEFT, 1),
    H_2("H2", 20, 13, 13, ParagraphAlignment.LEFT,2),
    H_3("H3", 20, 13, 13, ParagraphAlignment.LEFT,3),
    H_4("H4", 18, 11, 11, ParagraphAlignment.LEFT,4),
    H_5("H5", 18, 11, 11, ParagraphAlignment.LEFT,5),
    H_6("H6", 16, 11, 11, ParagraphAlignment.LEFT,6),
    H_7("H7", 16, 11, 11, ParagraphAlignment.LEFT,7),
    H_8("H8", 16, 11, 11, ParagraphAlignment.LEFT,8),
    H_9("H9", 14, 10, 10, ParagraphAlignment.LEFT,9),
    H_10("H10", 14, 10, 10, ParagraphAlignment.LEFT,10),
    ;


    private String name;
    private Integer fontSize;
    private Integer spacingAfter;
    private Integer spacingBefore;
    private ParagraphAlignment alignment;
    private Integer headingLevel;

    HeadStyleEnum(String name, Integer fontSize, Integer spacingAfter, Integer spacingBefore, ParagraphAlignment alignment, Integer headingLevel) {
        this.name = name;
        this.fontSize = fontSize;
        this.spacingAfter = spacingAfter;
        this.spacingBefore = spacingBefore;
        this.headingLevel = headingLevel;
        this.alignment = alignment;
    }
}
