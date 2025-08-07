package com.wg.appframe.yayi.api;

import com.wg.appframe.yayi.config.GeneralConfig;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.result.DoubaoCompletionResult;
import com.wg.appframe.yayi.strategy.GeneralStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@Slf4j
public class GeneralServer {
    @Autowired(required = false)
    private GeneralStrategy generalStrategy;

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public DoubaoCompletionResult generate(String content) {
        if (null == generalStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.general.completions >>>>>>>>>>>>");
            return new DoubaoCompletionResult();
        }

        GeneralConfig param = new GeneralConfig();
        param.setStream(false);
        return generalStrategy.run(content, param, null);
    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateStr(String content) {
        DoubaoCompletionResult result = generate(content);
        try {
            return result.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generateStr.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public DoubaoCompletionResult generate(String content, GeneralConfig param) {
        if (null == generalStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.general.completions >>>>>>>>>>>>");
            return new DoubaoCompletionResult();
        }
        if (null == param) {
            param = new GeneralConfig();
        }
        param.setStream(false);
        return generalStrategy.run(content, param, null);

    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateString(String content, GeneralConfig param) {
        DoubaoCompletionResult result = generate(content, param);
        try {
            return result.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.info("===> generateString.error:{}", e.getMessage());
        }
        return StringConstans.BLANK;
    }

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
//    public void generateStream(String content, Consumer<DoubaoCompletionResult> consumer) {
//        if (null == generalStrategy) {
//            log.error(">>>>>>>>>>>> 缺少配置：appframe.general.completions >>>>>>>>>>>>");
//            return;
//        }
//        GeneralConfig param = new GeneralConfig();
//        generalStrategy.run(content, param, consumer);
//    }

    /**
     * 调用completions接口，返回文本
     *
     * @param content
     * @return
     */
//    public void generateStreamStr(String content, Consumer<String> consumer) {
//        generateStream(content, p -> {
//            try {
//                String result = p.getChoices().get(0).getDelta().getContent();
//                consumer.accept(result);
//            } catch (Exception e) {
//                log.error("===> generateStreamStr.error:{}", e.getMessage());
//                consumer.accept(StringConstans.BLANK);
//            }
//        });
//    }

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public void generateStream(String content, GeneralConfig param, Consumer<DoubaoCompletionResult> consumer) {
        if (null == generalStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.general.completions >>>>>>>>>>>>");
            return;
        }
        if (null == param) {
            param = new GeneralConfig();
        }
        param.setStream(true);
        generalStrategy.run(content, param, consumer);
    }

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public DoubaoCompletionResult generate(String content, GeneralConfig param, Consumer<DoubaoCompletionResult> consumer) {
        if (null == generalStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.general.completions >>>>>>>>>>>>");
            return new DoubaoCompletionResult();
        }
        if (null == param) {
            param = new GeneralConfig();
        }
        param.setStream(true);
        return generalStrategy.run(content, param, consumer);
    }

}
