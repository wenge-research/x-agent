package com.wenge.model.dto.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class HotSearchWordParam  implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer queryCount;

    private String applicationId;

    private String question;

}
