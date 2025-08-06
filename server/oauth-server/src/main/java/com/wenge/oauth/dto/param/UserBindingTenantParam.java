package com.wenge.oauth.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.catalina.User;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserBindingTenantParam extends WGParam {

    private static final long serialVersionUID = -6496311865554465134L;

    /**
     * 用户id
     */
    private User user;

    /**
     * 角色id列表
     */
    private String tenantName;
}
