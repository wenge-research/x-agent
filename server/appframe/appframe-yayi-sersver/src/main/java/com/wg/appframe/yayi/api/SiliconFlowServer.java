package com.wg.appframe.yayi.api;

import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.SiliconFlowCompletionParam;
import com.wg.appframe.yayi.result.SiliconFlowCompletionResult;
import com.wg.appframe.yayi.strategy.SiliconFlowCompletionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@Slf4j
public class SiliconFlowServer {
    
    @Autowired(required = false)
    private SiliconFlowCompletionStrategy siliconFlowCompletionStrategy;

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public SiliconFlowCompletionResult generate(String content) {
        if (null == siliconFlowCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.siliconflow.completions >>>>>>>>>>>>");
            return new SiliconFlowCompletionResult();
        }

        SiliconFlowCompletionParam param = new SiliconFlowCompletionParam();
        param.setStream(false);
        return siliconFlowCompletionStrategy.run(content, param, null);
    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateStr(String content) {
        SiliconFlowCompletionResult result = generate(content);
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
    public SiliconFlowCompletionResult generate(String content, SiliconFlowCompletionParam param) {
        if (null == siliconFlowCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.siliconflow.completions >>>>>>>>>>>>");
            return new SiliconFlowCompletionResult();
        }
        if (null == param) {
            param = new SiliconFlowCompletionParam();
        }
        param.setStream(false);
        return siliconFlowCompletionStrategy.run(content, param, null);
        
    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateString(String content, SiliconFlowCompletionParam param) {
        SiliconFlowCompletionResult result = generate(content, param);
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
    public void generateStream(String content, Consumer<SiliconFlowCompletionResult> consumer) {
        if (null == siliconFlowCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.siliconflow.completions >>>>>>>>>>>>");
            return;
        }
        SiliconFlowCompletionParam param = new SiliconFlowCompletionParam();
        param.setStream(true);
         siliconFlowCompletionStrategy.run(content, param, consumer);
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
    public void generateStream(String content, SiliconFlowCompletionParam param, Consumer<SiliconFlowCompletionResult> consumer) {
        if (null == siliconFlowCompletionStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.siliconflow.completions >>>>>>>>>>>>");
            return;
        }
        if (null == param) {
            param = new SiliconFlowCompletionParam();
        }
        param.setStream(true);
        siliconFlowCompletionStrategy.run(content, param, consumer);
    }

    /**
     * 调用completions接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, SiliconFlowCompletionParam param, Consumer<String> consumer) {
        generateStream(content, param,p -> {
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
