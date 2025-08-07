package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionConfig extends YayiConfigSupper{
    private static final long serialVersionUID = 3505301808560497113L;

    private String uri;
    private String model;
    private Integer maxLength;
    private Integer noRepeatNgramSize;
    private Integer numBeams;
    private Integer questionsNum;
}
