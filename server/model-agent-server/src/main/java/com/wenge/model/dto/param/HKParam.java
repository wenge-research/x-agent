package com.wenge.model.dto.param;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HKParam  implements Serializable {

    private static final long serialVersionUID = -1L;

    private List<String> fileIds;

    private String requestId;

    private Integer pageNo;

    private Integer pageSize;
}
