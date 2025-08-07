package com.wg.appframe.yayi.param;

import com.wg.appframe.yayi.config.DoubaoImageConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DoubaoImageParam extends DoubaoImageConfig {

    private static final long serialVersionUID = 2218760029024756726L;

    private String prompt;
    private String appKey;
}
