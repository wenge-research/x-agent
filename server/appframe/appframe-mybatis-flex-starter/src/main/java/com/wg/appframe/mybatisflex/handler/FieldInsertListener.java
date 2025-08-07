package com.wg.appframe.mybatisflex.handler;

import com.mybatisflex.annotation.InsertListener;
import com.wg.appframe.mybatisflex.enums.FieldFill;

/**
 * 插入时，更新user
 */
public class FieldInsertListener implements InsertListener {

    @Override
    public void onInsert(Object entity) {
        FlexListenerAssistant.onFill(entity, fill -> fill == FieldFill.INSERT_UPDATE || fill == FieldFill.INSERT);
    }

}
