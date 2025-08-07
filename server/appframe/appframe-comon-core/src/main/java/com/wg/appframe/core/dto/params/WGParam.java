package com.wg.appframe.core.dto.params;

import com.wg.appframe.core.exception.ParamException;
import com.wg.appframe.core.constant.ResultCodeBase;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * @Description: 统一入参定级父类
 * @Author: CHENZHIWEI
 * @CreateTime: 2022-12-01 09:51:33
 */
@ApiModel("请求参数")
public class WGParam implements Serializable {

    private static final long serialVersionUID = 3202536787142081425L;

    /**
     * 参数不能为空
     *
     * @param params
     * @param resultCodeBase
     * @return
     */
    public WGParam notBlank(ResultCodeBase resultCodeBase, String... params) {
        if (StringUtils.isAnyBlank(params)) {
            throw new ParamException(resultCodeBase);
        }
        return this;
    }

    /**
     * 参数不能为空
     * @param params
     * @return
     */
    public WGParam notBlank(String... params) {
        return notBlank(ResultCodeEnum.PARAM_NOT_BLANK, params);
    }


    /**
     * 参数不能为空
     * @param params
     * @return
     */
    public WGParam notBlank(Supplier<String>... params) {
        return notBlank(ResultCodeEnum.PARAM_NOT_BLANK, params);
    }

    /**
     * 参数不能为空
     *
     * @param params
     * @param resultCodeBase
     * @return
     */
    public WGParam notBlank(ResultCodeBase resultCodeBase, Supplier<String>... params) {
        if (null == params || params.length == 0) {
            throw new ParamException(resultCodeBase);
        }
        String[] paramArray = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            paramArray[i] = params[i].get();
        }
        return notBlank(resultCodeBase, paramArray);
    }

    /**
     * 参数不能为空
     *
     * @param params
     * @param resultCodeBase
     * @return
     */
    public WGParam notNull(ResultCodeBase resultCodeBase, Object... params) {
        if (ObjectUtils.anyNull(params)) {
            throw new ParamException(resultCodeBase);
        }
        return this;
    }

    /**
     * 参数不能为空
     * @param params
     * @return
     */
    public WGParam notNull(Object... params) {
        return notNull(ResultCodeEnum.PARAM_NOT_BLANK,params);
    }


    /**
     * 参数不能为空
     * @param params
     * @return
     */
    public WGParam notNull(Supplier<Object>... params) {
        return notNull(ResultCodeEnum.PARAM_NOT_BLANK, params);
    }

    /**
     * 参数不能为空
     *
     * @param params
     * @param resultCodeBase
     * @return
     */
    public WGParam notNull(ResultCodeBase resultCodeBase, Supplier<Object>... params) {
        if (null == params || params.length == 0) {
            throw new ParamException(resultCodeBase);
        }
        Object[] paramArray = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            paramArray[i] = params[i].get();
        }

        return notNull(resultCodeBase, paramArray);
    }

    /**
     * 自定义参数是否正确
     *
     * @param successFlag
     * @return
     */
    public WGParam isSuccess(ResultCodeBase resultCodeBase, Boolean... successFlag) {
        boolean isSuccess = true;
        for (Boolean aBoolean : successFlag) {
            isSuccess = isSuccess && aBoolean;
        }
        if (!isSuccess) {
            throw new ParamException(resultCodeBase);
        }
        return this;
    }

    /**
     * 自定义参数是否正确
     *
     * @param successFlag
     * @return
     */
    public WGParam isSuccess(Boolean... successFlag) {
        return isSuccess(ResultCodeEnum.PARAM_NOT_SUCCESS, successFlag);
    }


}
