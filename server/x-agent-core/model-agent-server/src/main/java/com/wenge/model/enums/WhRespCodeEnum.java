package com.wenge.model.enums;

import lombok.Getter;

/**
 * @Description: 闻海接口返回码
 * @Author: CHENZHIWEI
 * @CreateTime: 2021-12-27 20:35:41
 */
@Getter
public enum WhRespCodeEnum {
    SUCCESS("0", "成功", false),
    SERVICE_INVALID("1001", "服务不可用", false),
    TIMEOUT("1002", "接口超时，重新调用", false),
    LOG_FAILD("1003", "接口请求记录失败", false),
    QUOTA_QUERY_FAILD("1004", "接口配额查询失败", false),
    BALANCE_NO_ENOUGH("1005", "用户余额不足", true),
    APPKEY_EMPTY("2001", "appKey为空", false),
    APPKEY_INVALID("2002", "无效appKey", false),
    SIGN_EMPTY("2003", "sign为空", false),
    SIGN_INVALID("2004", "无效sign", false),
    TIMESTAMP_EMPTY("2005", "timeStamp为空", false),
    TIMESTAMP_INVALID("2006", "无效timeStamp,超过3分钟", false),
    USER_NO_EXIST("2007", "用户不存在", true),
    AUTH_EXCEPTION("2008", "鉴权服务异常", false),
    REQ_TOO_MANY("3001", "请求频率过高", false),
    BALANCE_NO_ENOUGH2("3002", "余额不足", true),
    IP_INVALID("3003", "IP无效", true),
    USER_INVALID("3004", "此接口用户无权限", true),
    REQ_TOO_MANY_HOUR("3005", "超过每小时的最高频率", true),
    REQ_TOO_MANY_DAY("3006", "超过每天的最高频率", true),
    TIME_INVALIID("4001", "时间格式错误", false),
    TIME_START_LT_END("4002", "开始时间必须小于结束时间", false),
    TIME_INVALIID_ONE_DAY("4003", "时间跨度不正确，必须在一天以内", false),
    KEY_WORD_LENGTH("4011", "关键词长度不正确", false),
    KEY_WORD_INVALID("4012", "关键词含有特殊字符，解析失败", false),
    QUERY_VAGUE_LENGTH("4021", "模糊查询的输入长度不正确", false),
    EMOTION_EMPTY("4033", "emotion为空", false),
    EMOTION_INVALID("4034", "无效emotion", false),
    TAG_EMPTY("4035", "tag为空", false),
    TAG_INVALID("4036", "无效tag", false),
    CATEGORY_EMPTY("4037", "category为空", false),
    CATEGORY_INVALID("4038", "无效category", false),
    ARTICLE_TYPE_EMPTY("4039", "article_type为空", false),
    ARTICLE_TYPE_INVALID("4040", "无效article_type", false),
    DATA_SOURCE("4045", "指定数据源不在订阅时配置的数据源集合中", true);

    private String code;
    private String msg;
    private boolean changeUser;

    WhRespCodeEnum(String code, String msg, boolean changeUser) {
        this.code = code;
        this.msg = msg;
        this.changeUser = changeUser;
    }

    public static WhRespCodeEnum getWhRespEnumByCode(String code) {
        WhRespCodeEnum[] values = WhRespCodeEnum.values();
        for(WhRespCodeEnum temp : values) {
            if(temp.getCode().equals(code)){
                return temp;
            }
        }
        return null;
    }
}
