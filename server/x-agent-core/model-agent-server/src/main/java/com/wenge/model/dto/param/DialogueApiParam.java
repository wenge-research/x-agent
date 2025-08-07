package com.wenge.model.dto.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class DialogueApiParam implements Serializable {

    private static final long serialVersionUID = 7607856650490028848L;

    private DialogueByStreamParam param;
    private String apiKey;

}
