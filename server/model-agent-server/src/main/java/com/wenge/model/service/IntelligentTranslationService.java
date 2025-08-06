package com.wenge.model.service;

import com.wenge.model.entity.IntelligentTranslationRecord;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.wos.exception.WosException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: caohaifeng
 * @date: 2024/9/23 13:51
 * @Description:
 * @Version:1.0
 **/

public interface IntelligentTranslationService {

    /**
     * @description: 智能翻译接口
     * @author: caohaifeng
     * @date: 2024/9/23 13:52
     **/
    Result translateTextOrFile(IntelligentTranslationRecord intelligentTranslationRecord) throws WosException;


    /**
     * @description: 智能识别语种
     * @author: caohaifeng
     * @date: 2024/9/23 17:52
     **/
    Result identifyLanguage(IntelligentTranslationRecord intelligentTranslationRecord);


    /**
     * 闻歌ai翻译接口，返回流式数据
     * @param record
     * @param request
     * @return
     */
    SseEmitter executeTranslateTextWG(IntelligentTranslationRecord record,
                                      HttpServletRequest request);
}
