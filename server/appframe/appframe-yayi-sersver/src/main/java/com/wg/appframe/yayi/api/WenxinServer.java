package com.wg.appframe.yayi.api;

import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.WenxinChatParam;
import com.wg.appframe.yayi.result.WenxinChatResult;
import com.wg.appframe.yayi.strategy.WenxinChatStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@Slf4j
public class WenxinServer {
    
    @Autowired(required = false)
    private WenxinChatStrategy wenxinChatStrategy;

    /**
     * 调用chat接口
     *
     * @param content
     * @return
     */
    public WenxinChatResult generate(String content) {
        if (null == wenxinChatStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.wenxin.chat >>>>>>>>>>>>");
            return new WenxinChatResult();
        }

        WenxinChatParam param = new WenxinChatParam();
        param.setStream(false);
        return wenxinChatStrategy.run(content, param, null);
    }

    /**
     * 调用chat接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateStr(String content) {
        WenxinChatResult result = generate(content);
        try {
            return result.getResult();
        } catch (Exception e) {
            log.info("===> generateStr.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用chat接口
     *
     * @param content
     * @return
     */
    public WenxinChatResult generate(String content, WenxinChatParam param) {
        if (null == wenxinChatStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.wenxin.chat >>>>>>>>>>>>");
            return new WenxinChatResult();
        }
        if (null == param) {
            param = new WenxinChatParam();
        }
        param.setStream(false);
        return wenxinChatStrategy.run(content, param, null);
        
    }

    /**
     * 调用chat接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateString(String content, WenxinChatParam param) {
        WenxinChatResult result = generate(content, param);
        try {
            return result.getResult();
        } catch (Exception e) {
            log.info("===> generateString.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用chat接口
     *
     * @param content
     * @return
     */
    public void generateStream(String content, Consumer<WenxinChatResult> consumer) {
        if (null == wenxinChatStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.wenxin.chat >>>>>>>>>>>>");
            return;
        }
        WenxinChatParam param = new WenxinChatParam();
        param.setStream(true);
         wenxinChatStrategy.run(content, param, consumer);
    }

    /**
     * 调用chat接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, Consumer<String> consumer) {
        generateStream(content, p -> {
            try {
                String result = p.getResult();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generateStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });
    }

    /**
     * 调用chat接口
     *
     * @param content
     * @return
     */
    public void generateStream(String content, WenxinChatParam param, Consumer<WenxinChatResult> consumer) {
        if (null == wenxinChatStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.wenxin.chat >>>>>>>>>>>>");
            return;
        }
        if (null == param) {
            param = new WenxinChatParam();
        }
        param.setStream(true);
        wenxinChatStrategy.run(content, param, consumer);
    }

    /**
     * 调用chat接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, WenxinChatParam param, Consumer<String> consumer) {
        generateStream(content, param,p -> {
            try {
                String result = p.getResult();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generateStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });

    }
}
