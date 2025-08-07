package com.wg.appframe.yayi.api;

import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.DoubaoCompletionParam;
import com.wg.appframe.yayi.result.DoubaoCompletionResult;
import com.wg.appframe.yayi.strategy.DoubaoStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@Slf4j
public class DoubaoServer {


    @Autowired(required = false)
    private DoubaoStrategy doubaoCompletionStrategy;

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public DoubaoCompletionResult generate(String content) {
        if (null == doubaoCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.doubao.completions >>>>>>>>>>>>");
            return new DoubaoCompletionResult();
        }

        DoubaoCompletionParam param = new DoubaoCompletionParam();
        param.setStream(false);
        return doubaoCompletionStrategy.run(content, param, null);
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
    public DoubaoCompletionResult generate(String content, DoubaoCompletionParam param) {
        if (null == doubaoCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.doubao.completions >>>>>>>>>>>>");
            return new DoubaoCompletionResult();
        }
        if (null == param) {
            param = new DoubaoCompletionParam();
        }
        param.setStream(false);
        return doubaoCompletionStrategy.run(content, param, null);

    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateString(String content, DoubaoCompletionParam param) {
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
    public void generateStream(String content, Consumer<DoubaoCompletionResult> consumer) {
        if (null == doubaoCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.kimi.completions >>>>>>>>>>>>");
            return;
        }
        DoubaoCompletionParam param = new DoubaoCompletionParam();
        doubaoCompletionStrategy.run(content, param, consumer);
    }

    /**
     * 调用completions接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, Consumer<String> consumer) {
        generateStream(content, p -> {
            try {
                String result = p.getChoices().get(0).getDelta().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generateStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });
    }

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public void generateStream(String content, DoubaoCompletionParam param, Consumer<DoubaoCompletionResult> consumer) {
        if (null == doubaoCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.kimi.completions >>>>>>>>>>>>");
            return;
        }
        if (null == param) {
            param = new DoubaoCompletionParam();
        }
        param.setStream(true);
        doubaoCompletionStrategy.run(content, param, consumer);
    }

    /**
     * 调用completions接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, DoubaoCompletionParam param, Consumer<String> consumer) {
        generateStream(content, param, p -> {
            try {
                String result = p.getChoices().get(0).getDelta().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generateStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });

    }
}
