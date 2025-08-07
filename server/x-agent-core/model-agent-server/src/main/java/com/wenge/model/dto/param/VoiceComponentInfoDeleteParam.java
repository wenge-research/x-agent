package com.wenge.model.dto.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class VoiceComponentInfoDeleteParam{

    private static final long serialVersionUID = -2636192558690322065L;

    @ApiModelProperty(name = "ids",value = "id数组", dataType = "Long")
    private List<Long> ids;

}
