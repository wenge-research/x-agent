package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RetrievePasswordParam extends WGParam {

    private static final long serialVersionUID = 5096834168092287376L;

    private String phoneNum;

    private String code;

    private String codeKey;

    private String password;

    private String confirmPassword;
}
