package com.wenge.model.dto.param.wenhai;

import java.util.List;

/**
 * ArticleUser, 发文账号相关信息
 */
@lombok.Data
public class ArticleUser {
    /**
     * 平台账号ID,最多支持20个(包含20，总长度300)
     */
    private List<String> customIds;
    private FanRange fanRange;
    /**
     * 是否账号认证,【空/-1默认全部、-1-全部、0-未认证、1-已认证】
     */
    private Long isVerified;
    /**
     * 账号ID,最多支持20个(包含20，总长度300)
     */
    private List<String> userIds;
    /**
     * 账号名称,最多支持20个(包含20，总长度300)
     */
    private List<String> userNames;
    /**
     * 账号注册地域信息,具体详见《字典接口模块》，各个字段取值详见字段说明
     */
    private List<UserRegisterArea> userRegisterAreas;
    /**
     * 账号博主性别，m：男 f：女
     */
    private String userSex;
    /**
     * 认证类型【0-未认证、1-个人、2-机构组织、3-公司企业、4-其他】
     */
    private List<Long> verifiedTypes;
}
