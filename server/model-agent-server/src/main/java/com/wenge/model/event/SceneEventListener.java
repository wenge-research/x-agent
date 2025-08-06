package com.wenge.model.event;

import com.wenge.model.constants.RedisKey;
import com.wenge.model.service.impl.CacheClearServiceImpl;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.redis.service.RedisService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SceneEventListener implements ApplicationListener<SceneEvent> {

    @Override
    public void onApplicationEvent(SceneEvent event) {
        clear();
        CacheClearServiceImpl.clearComCache("/cacheClear/cleanWord", null);
    }

    /**
     * 清除缓存
     */
    public static void clear() {
        // 当场景有发生变化，清除缓存
        DialogueServiceImpl.INTERCEPT_WORD_HOUSE_MAP.clear();
        DialogueServiceImpl.APP_WORD_MAP.clear();
        DialogueServiceImpl.APP_INTERCEPT_WORD_MAP.clear();
        DialogueServiceImpl.APP_INTERCEPT_WORD_HOUSE_ID_MAP.clear();
        ApplicationContext context = CoreContextProvider.getContext();
        RedisService redisService = context.getBean(RedisService.class);
        redisService.batchDel(RedisKey.MATTER_DATA);
        redisService.batchDel(RedisKey.SCENE);
    }
}
