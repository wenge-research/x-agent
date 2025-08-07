package com.wg.appframe.mybatisflex.handler;

import com.mybatisflex.annotation.UpdateListener;
import com.wg.appframe.mybatisflex.enums.FieldFill;

/**
 * 更新时修改user
 */
public class FieldUpdateListener implements UpdateListener {

    @Override
    public void onUpdate(Object entity) {
        FlexListenerAssistant.onFill(entity, fill -> fill == FieldFill.INSERT_UPDATE || fill == FieldFill.UPDATE);
    }
}
