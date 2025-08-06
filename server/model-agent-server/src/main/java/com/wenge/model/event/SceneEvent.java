package com.wenge.model.event;

import com.wenge.model.entity.InterceptWord;
import org.springframework.context.ApplicationEvent;

/**
 * 监听场景变化事件
 */
public class SceneEvent extends ApplicationEvent {

    private static final long serialVersionUID = 5161925988787285780L;

    public SceneEvent(InterceptWord interceptWord) {
        super(null == interceptWord? InterceptWord.create() : interceptWord);
    }

}
