package com.wenge.model.constants;

/**
 * <P>
 * 对话相关常量
 * </p>
 *
 * @author zs
 * @date 2024/09/05
 */
public final class DialogueConstant {
    // 回答超时时间默认值
    public static final Integer ANSWER_TIMEOUT = 180;

    // 根据大学城用户获取推荐问题的访客key后缀
    public static final String VISITOR = "visitor";

    // 根据大学城用户获取推荐问题的缓存失效时间
    public static final long RECOMMENT_QUESTION_EXPIRE_TIME = 60 * 60 * 24 * 2;

}
