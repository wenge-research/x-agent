package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserBindingRoleParam extends WGParam {

    private static final long serialVersionUID = -6496311865554465134L;

    /**
     * 用户id列表
     */
    private List<String> userIdList;

    /**
     * 角色id列表
     */
    private List<String> roleIdList;

    /**
     * 角色code列表
     */
    private List<String> roleCodeList;
}
