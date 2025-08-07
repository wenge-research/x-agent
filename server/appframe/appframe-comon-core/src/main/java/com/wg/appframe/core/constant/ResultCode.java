/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.constant;

import lombok.Getter;

/**
 * <p>
 * 返回结果枚举
 * </p>
 *
 * @author yangyunjun
 * @since 2021-07-28
 */
@Getter
public enum ResultCode {
    /**==============================系统层面异常定义开始=========================================*/

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 已创建
     */
    CREATED(10201, "已创建异常"),

    /**
     * 没有权限信息
     */
    NON_AUTHORITATIVE_INFORMATION(10203, "没有权限信息异常"),

    /**
     * 没有内容
     */
    NO_CONTENT(10204, "没有内容异常"),

    /**
     * 多个状态
     */
    MULTI_STATUS(10207, "多个状态异常"),

    /**
     * 已上报
     */
    ALREADY_REPORTED(10208, "已上报异常"),

    /**
     * 重定向异常
     */
    REDIRECT_ERROR(10303, "重定向异常"),

    /**
     * 没有定义
     */
    NOT_MODIFIED(10304, "没有定义异常"),


    /**
     * 错误请求
     */
    BAD_REQUEST(10400, "错误请求异常"),

    /**
     * 未授权
     */
    UNAUTHORIZED(10401, "未授权异常"),

    /**
     * 无效Token异常
     */
    TOKEN_INVALID(10402, "无效或者不存在Token"),

    /**
     * 禁止访问
     */
    FORBIDDEN(10403, "禁止访问异常"),

    /**
     * 请求URL不存在
     */
    NOT_FOUND(10404, "请求URL不存在异常"),

    /**
     * 方法不允许
     */
    METHOD_NOT_ALLOWED(10405, "方法不允许异常"),

    /**
     * 不可接受
     */
    NOT_ACCEPTABLE(10406, "不可接受异常"),

    /**
     * 需要代理权限
     */
    PROXY_AUTHENTICATION_REQUIRED(10407, "代理权限异常"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(10408, "请求超时异常"),

    /**
     * 冲突
     */
    CONFLICT(10409, "冲突异常"),


    REPEAT_LOGIN_NAME(10410, "登入名称重复，请重新填写"),

    USER_NOT_FIND(10411, "没有找到匹配的用户"),

    USER_KEY_NOT_BIND(10412, "用户没有绑定密钥"),

    USER_NO_LOGIN_PERMISSION(10413, "该用户没有登录权限"),


    /**
     * 请求对象过大
     */
    REQUEST_ENTITY_TOO_LARGE(10414, "请求对象过大异常"),

    /**
     * URI过长
     */
    URI_TOO_LONG(10415, "URI过长异常"),

    /**
     * 请求URI过长
     */
    REQUEST_URI_TOO_LONG(10416, "请求URI过长异常"),

    NO_PERMISSION(10417, "没有权限操作"),

    USER_PSW_ERROR(10418, "用户名或密码错误"),

    LOGIN_INFORMATION_INVALID(10419, "登录信息失效"),

    /**
     * 方法失败
     */
    METHOD_FAILURE(10420, "方法失败异常"),

    /**
     * 没有开启权限校验异常
     */
    NOT_CHECK_PERMISSION_ERROR(10420, "没有开启权限校验异常"),


    /**
     * 登录失败
     */
    LOGIN_ERROR(10421, "登录失败"),

    /**
     * 登录类型错误
     */
    LOGIN_TYPE_ERROR(10422, "登录类型错误"),

    /**
     * 流控异常
     */
    REQUEST_FLOW_LIMIT_ERROR(10429, "请求被流控异常"),

    /**
     * 熔断降级异常
     */
    REQUEST_DEGRADE_ERROR(10430, "请求被熔断异常"),

    /**
     * 请求头过大
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE(10431, "请求头过大异常"),

    /**
     * 系统保护异常
     */
    SYSTEM_BLOCK_ERROR(10432, "系统保护异常"),

    /**
     * 系统保护异常
     */
    SYSTEM_ERROR(10435, "系统异常"),

    /**
     * 热点参数限流异常
     */
    PARAM_FLOW_ERROR(10433, "热点参数限流异常"),

    /**
     * 请求未被授权异常
     */
    REQUEST_UNAUTHORIZED_ERROR(10434, "请求未被授权异常"),


    /**
     * 因法律原因不可用异常
     */
    UNAVAILABLE_FOR_LEGAL_REASONS(10451, "因法律原因不可用异常"),

    /**
     * 内部服务器错误异常
     */
    INTERNAL_SERVER_ERROR(10500, "内部服务器错误异常"),

    /**
     * 没有实现类异常
     */
    NOT_IMPLEMENTED(10501, "没有实现类异常"),

    /**
     * 网关异常
     */
    BAD_GATEWAY(10502, "网关转发异常"),

    /**
     * 服务不可用异常
     */
    SERVICE_UNAVAILABLE(10503, "服务不可用异常"),

    /**
     * 网关超时异常
     */
    GATEWAY_TIMEOUT(10504, "网关超时异常"),

    /**
     * HTTP版本不支持异常
     */
    HTTP_VERSION_NOT_SUPPORTED(10505, "HTTP版本不支持异常"),

    /**
     * 系统繁忙
     */
    SYSTEM_SO_BUSY_ERROR(10506, "系统繁忙"),


    /**
     * 超出带宽限制异常
     */
    BANDWIDTH_LIMIT_EXCEEDED(10509, "超出带宽限制异常"),


    /**
     * 需要网络身份验证异常
     */
    NETWORK_AUTHENTICATION_REQUIRED(10511, "需要网络身份验证异常"),

    /**
     * 请求头部缺少加密密文[cipher]
     */
    CIPHER_IS_BLANK(10512, "请求头部缺少加密密文[cipher]"),

    /**
     * 密文错误
     */
    CIPHER_IS_FAILED(10513, "密文错误"),

    /**
     * 只允许POST请求
     */
    ONLY_ALLOWED_POST(10514, "只允许POST请求"),

    /**==============================系统层面异常定义结束=========================================*/


    /**
     * ==============================业务层面异常定义开始=========================================
     */
    DUPLICATE_PRIMARY_KEY(20001, "唯一键冲突"),

    DB_FAILED(20002, "读写数据库异常"),

    ROUTE_DUPLICATE(20003, "路由冲突，相同服务不能拥有相同的过滤规则"),

    TIME_ERROR(20004, "开始时间不能大于结束时间"),

    DEL_EORRE(20005, "服务绑定路由，禁止删除"),

    //认证
    APP_ID_NOT_EXIST(20006, "client_id不存在"),

    SYSYTEM_CLIENT_ERROR(20007, "客户端请求中的语法错误"),

    K8S_SERVER_API_ERROR(20008, "调用K8S API异常"),

    /**
     * 发邮件异常
     */
    MESSAGE_EMAIL_ERROR(20009, "发邮件异常"),

    /**
     * 上传文件超过最大值
     */
    UPLOAD_MAX(20010, "上传文件超过最大值"),

    /**
     * 上传文件不存在
     */
    FILE_NOT_FOUNT(20011, "上传文件不存在"),

    /**
     * 存储Key不合法
     */
    STORE_KEY_ILLEGAL(20012, "存储Key不合法"),

    /**
     * header存储key不存在
     */
    STORE_KEY_NOT_EXIST(20013, "header存储key不存在"),

    /**
     * 上传文件为空
     */
    FILES_EMPTY(20014, "上传文件为空"),

    /**
     * 文件名称为空
     */
    FILES_NAME_EMPTY(20015, "文件名称为空"),

    /**
     * 文件名称不合法
     */
    FILES_NAME_NOT_ALLOW(20016, "文件名称不合法"),

    /**
     * 文件不存在
     */
    FILES_NOT_EXIST(20017, "文件不存在"),

    /**
     * 文件下载错误
     */
    FILE_DOWNLOAD_ERROR(20018, "文件下载错误"),

    /**
     * 文件上传错误
     */
    FILE_UPLOAD_ERROR(20019, "文件上传错误"),

    /**
     * 文件删除错误
     */
    FILE_DELETE_ERROR(20020, "文件删除错误"),

    /**
     * 先绑定再删除
     */
    UNBIND_DELETE_ERROR(20021, "先解绑再删除"),

    /**
     * 先删除相关需求
     */
    DELETE_REQUIREMENT_ERROR(20022, "先删除相关需求"),

    /**
     * 先删除相关文档
     */
    DELETE_DOCUMENT_ERROR(20023, "先删除相关文档"),

    /**
     * 请求参数校验不通过
     */
    PARAM_INVALID(20024, "请求参数校验不通过"),

    /**
     * 参数为空异常
     */
    PARAM_IS_EMPTY(20025, "参数为空异常"),

    SENTINEL_API_ERROR(20026, "调用sentinel接口异常"),
    /**
     * 请求参数数量过多
     */
    PARAM_NUMBER_OUT_OF_BOUNDS(20027, "请求参数数量过多"),


    GITLAB_API_ERROR(20028, "调用Gitlab接口异常"),

    GITLAB_DUPLICATE_FILE_NAME(20029, "新建文件失败，同级目录下已存在重名文件"),

    GITLAB_FILE_NOT_EXIT(20030, "编辑文件失败，找不到该文件，请检查文件路径名"),

    PIPELINE_EXISTS_ERROR(20031, "流水线已经存在"),

    PIPELINE_CREATE_ERROR(20032, "创建流水线异常"),


    PARAM_NULL(20033, "[%s]参数不能为空"),

    PARAM_FORMAT_ERROR(20034, "[%s]参数格式错误，应该是[%s]"),

    CLIENT_NOT_USER(20035, "该用户与应用程序没有关联"),

    CLIENT_NOT_FIND(20036, "客户端应用程序不存在"),

    EXIST_VALUE(20037, "值[%s]已存在"),

    NOT_EXIST_GROUP(20038, "组 [%s] 不存在"),

    USER_UN_LOGIN(20039, "用户登录状态无效或未登录"),

    USER_NO_ENTERPRISE(20040, "该用户没有关联企业信息！！"),

    USER_AFFILIATED_MULTI_ENTERPRISE(20041, "该用户关联多个企业！！"),

    VISITOR_MODE_LOGIN(20042, "游客模式登录！！"),

    INVALID_ENTERPRISE(20043, "无效企业！！"),

    CLIENT_NO_USER(20044, "用户不属于该系统，无法登录，请联系管理员"),

    CLIENT_CONNECT_SERVER(20045, "网络异常，登录认证中心失败"),

    AC_CONFIG_UN_INIT(20046, "认证中心核心配置未初始化"),

    NOT_FOUND_CLIENT(20047, "客户端不存在"),

    NOT_FOUND_USER(20048, "用户不存在"),

    NOT_MATCH_CLIENT_PERMISSION(20049, "客户端中没有该权限"),

    REDIS_READ_WRITE_EXCEPTION(20050, "redis读写异常"),

    TRANS_BEAN_ERROR(20052, "对象转换异常"),

    HTTP_LAUNCHER_ERROR(20053, "HTTP请求发送异常"),

    ROUTE_ID_DUPLICATE(20054, "路由名称与已有路由冲突，请修改路由名称"),

    LABEL_KEY_DUPLICATE(20055, "K8S标签key不可重复"),

    WRONG_LABEL_KEY_VALUE(20056, "K8S标签key和标签值只能由字母数字或部分英文字符组成, 且必须以字母或数字开头和结尾"),

    TENANT_DUPLICATE(20057, "租户用户名不可重复"),

    SERVICE_NAME_DUPLICATE(20058, "服务名称与已有服务冲突，请修改服务名称"),

    FILE_SUFFIX_NOT_ZIP(20059, "文件格式不是.zip"),

    HARBOR_REPOS_NOT_EMPTY(20060, "该仓库下面存在镜像库"),

    HARBOR_REPOS_NOT_EXISTS(20061, "该镜像仓库不存在"),

    DEPT_EXISTS_USER(20062, "该部门中还有用户存在，无法删除"),

    GROUP_EXISTS_USER(20063, "该用户组中还有用户存在，无法删除"),

    MENU_EXISTS_SON(20064, "该菜单中还有子菜单存在，无法删除"),

    ROLE_EXISTS_LINK(20065, "该角色还有：用户/用户组/部门关联，无法删除"),

    DICT_TYPE_DUPLICATE(20066, "字典类型不可重复"),

    DICT_ITEM_DUPLICATE(20067, "字典条目不可重复"),

    SYS_CONFIG_KEY_DUPLICATE(20068, "配置键不可重复"),

    IAM_PROJECT_DUPLICATE(20069, "项目编码不可重复"),

    IAM_APP_DUPLICATE(20070, "应用编码不可重复"),

    OLD_PASSWORD_ERROR(20071, "旧密码输入错误"),

    ROLE_LINK_ERROR(20072, "角色关联失败"),

    USER_LINK_ERROR(20073, "用户关联操作失败"),

    CREATE_OR_UPDATE_ERROR(20074, "创建或修改操作失败"),

    DELETE_ERROR(20075, "删除操作失败"),

    CREATE_ERROR(20076, "创建操作失败"),

    UPDATE_PASSWORD_ERROR(20077, "修改密码失败，请检查密码加密方式是否正确"),

    SET_STATUS_ERROR(20078, "设置状态失败"),

    ROLE_GRANT_ERROR(20079, "角色授权失败"),

    USERNAME_IS_NULL(20080, "用户名不可为空"),

    PASSWORD_IS_NULL(20081, "密码不可为空"),

    MOBILE_IS_NULL(20082, "手机号码不可为空"),

    SMS_CODE_IS_NULL(20083, "短信验证码不可为空"),

    ID_CARD_IS_NULL(20084, "身份证号不可为空"),

    OPEN_ID_IS_NULL(20085, "openId不可为空"),

    LOGIN_TYPE_IS_NULL(20086, "登录类型不可为空"),

    CLIENT_ID_IS_NULL(20087, "clientId不可为空"),

    IMAGE_CODE_IS_NULL(20088, "图形验证码错误"),

    USER_ACCOUNT_NAME_UNI(20089, "用户登录名不可重复"),


    MENU_CODE_UNI(20090, "菜单编码不可重复"),

    DEPT_JOBINFO_UNI(20091, "同一部门下不能有重复的职位"),

    GROUP_UNI(20092, "用户组不允许重复"),

    DEPT_CODE_UNI(20093, "部门编码不可重复"),

    ON_USER_ERROR(20094, "仅停用的用户可启动"),

    OFF_USER_ERROR(20095, "仅启用的用户可停用"),

    RESET_USER_ERROR(20096, "仅删除的用户可恢复"),

    TENANT_ID_IS_NULL(20097, "获取不到租户id，请先登录"),

    MENU_CODE_USED(20098, "其他租户已使用过该菜单编码"),

    DICT_TYPE_USED(20099, "其他租户已使用过该字典类型"),

    IAM_PROJECT_USED(20100, "其他租户已使用过该项目编码"),

    IAM_APP_USED(20101, "其他租户已使用过该应用编码"),

    USER_ACCOUNT_NAME_USED(20102, "其他租户已使用过该用户登录名"),

    ROLE_CODE_USED(20103, "其他租户已使用过该角色编码"),

    DEPT_CODE_USED(20104, "其他租户已使用过该部门编码"),

    MESSAGE_TEMPLATE_IS_NOT_EXISTED(20105, "消息模板不存在"),

    USER_OR_MOBILE_IS_NULL(20106, "用户不存在或者该用户没有绑定手机号码"),

    HAVE_HIS_TASK(20107, "该流程定义存在历史数据，不允许删除！"),

    HAVE_DEPLOYMENT_TASK(20108, "该流程定义存在已部署流程实例，请先删除部署实例！"),

    TASK_IS_NULL(20109, "任务不存在！"),

    TASK_ASSIGNEE_IS_NULL(20110, "当前任务未设置处理人，请先设置处理人或认领！"),

    TASK_NOT_EXISTS(20111, "流程实例不存在！"),

    PARAM_TOO_LONG_100(20112, "请求参数超过限制，最大不能超过100"),

    NOT_ASSIGNEE(20113, "非处理人无权限操作"),

    TABLE_NAME_DUPLICATE(20114, "数据表名不可重复"),

    DATA_SOURCE_DELETED(20115, "该实体表对应的数据源已被删除"),

    DB_PARAM_ERROR(20116, "数据库操作执行sql构成失败，请检查参数！"),

    TABLE_EXISTED(20117, "数据表已存在！"),

    DATA_NOT_EXISTS(20118, "数据不存在，请检查参数！"),

    APP_CODE_DUPLICATE(20119, "应用模块编码不可重复！"),

    TABLED_HAS_SYNC(20120, "该数据表已完成同步操作！"),

    DATA_SOURCE_NOT_EXISTS(20121, "数据源不存在"),

    TABLE_INFO_NOT_FOUND(20122, "查找不到对应的实体表信息！"),

    PAGE_CODE_DUPL_ERROR(20123, "页面编码不可重复！"),

    FORM_CODE_DUPL_ERROR(20124, "表单编码不可重复！"),

    TASK_HIS_NOT_EXISTS(20125, "流程历史未经过该流程节点，请检查参数！"),

    DEPT_NOT_EXISTS_USER(20126, "该部门中不存在用户，请检查部门参数！"),

    ID_CARD_ERROR(20127, "身份证无效，不是合法的身份证号码！"),

    DEPT_NOT_EXISTS(20128, "该用户部门不存在，请检查部门是否已移除！"),

    CONFIG_NOT_EXISTS(20129, "系统配置项不存在！"),

    FORM_MODULE_AND_ER_ID_DUPL_ERROR(20130, "该模块下对应的ER模型已生成过表单！"),

    ROLE_CODE_UNI(20131, "角色编码不可重复"),

    DEPT_EXISTS_ROLE(20132, "该部门中还有角色存在，无法删除"),

    BUSINESS_FAILED(999999, "业务异常")
    ;

    /**
     * ==============================业务层面异常定义结束=========================================
     */

    private int code;

    private String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}

