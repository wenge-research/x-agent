package com.wg.appframe.yayi.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DoubaoTokenizationParam implements Serializable {


    private static final long serialVersionUID = 8761651125568706804L;

    private String model;
    private List<String> text;
}
