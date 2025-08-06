package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PwChangParam extends WGParam {

    private static final long serialVersionUID = -6595449717976591103L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 原密码
     */
    private String oldPw;

    /**
     * 新密码
     */
    private String newPw;

    /**
     * 确认新密码
     */
    private String confirmNewPw;

}
