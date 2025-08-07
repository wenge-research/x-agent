package com.wg.appframe.wos.dto.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FileKeyParam implements Serializable {

    private static final long serialVersionUID = 449459952014638009L;

    private String fileKey;
    private List<String> fileKeyList;
}
