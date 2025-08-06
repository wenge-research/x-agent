package com.wenge.oauth.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.rely.IdType;

/**
 * @description: 系统操作日志实体param
 * @author: caohaifeng
 * @date: 2024/8/23 15:08
 **/
@Data
public class UmsOperationParam extends WgPageInfo {

    private static final long serialVersionUID = 8901334677509447888L;

    private String keyword;
//    private String basePath;
    private String uri;
//    private String url;
    private String method;
    private Integer logType;
    private String ip;
    private String description;
    private String startAccessTime;//必须为yyyy-MM-dd HH:mm:ss形式的时间
    private String endAccessTime;//必须为yyyy-MM-dd HH:mm:ss形式的时间
//    private String insertTime;//必须为yyyy-MM-dd HH:mm:ss形式的时间
    private Integer spend;//访问接口消耗时间
    private String systemName;
    private String browserName;
    private String operatorTagUserName; // 操作人（用户名）
    private String operatorTagAccountName; // 操作人（用户名-登录使用）
}