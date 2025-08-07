package com.wg.appframe.yayi.api;

import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.ZhipuParam;
import com.wg.appframe.yayi.result.ZhipuResult;
import com.wg.appframe.yayi.strategy.ZhipuStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@Slf4j
public class ZhipuServer {

    @Autowired(required = false)
    private ZhipuStrategy zhipuStrategy;

    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public ZhipuResult generate(String content) {
        if (null == zhipuStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.zhipu.chat >>>>>>>>>>>>");
            return new ZhipuResult();
        }

        ZhipuParam param = new ZhipuParam();
        param.setStream(false);
        return zhipuStrategy.run(content, param, null);
    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateStr(String content) {
        ZhipuResult result = generate(content);
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
    public ZhipuResult generate(String content, ZhipuParam param) {
        if (null == zhipuStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.zhipu.chat >>>>>>>>>>>>");
            return new ZhipuResult();
        }
        if (null == param) {
            param = new ZhipuParam();
        }
        param.setStream(false);
        return zhipuStrategy.run(content, param, null);

    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateString(String content, ZhipuParam param) {
        ZhipuResult result = generate(content, param);
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
    public void generateStream(String content, Consumer<ZhipuResult> consumer) {
        if (null == zhipuStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.zhipu.chat >>>>>>>>>>>>");
            return;
        }
        ZhipuParam param = new ZhipuParam();
        param.setStream(true);
        zhipuStrategy.run(content, param, consumer);
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
                String result = p.getChoices().get(0).getMessage().getContent();
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
    public void generateStream(String content, ZhipuParam param, Consumer<ZhipuResult> consumer) {
        if (null == zhipuStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.zhipu.chat >>>>>>>>>>>>");
            return;
        }
        if (null == param) {
            param = new ZhipuParam();
        }
        param.setStream(true);
        zhipuStrategy.run(content, param, consumer);
    }

    /**
     * 调用completions接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, ZhipuParam param, Consumer<String> consumer) {
        generateStream(content, param,p -> {
            try {
                String result = p.getChoices().get(0).getMessage().getContent();
                consumer.accept(result);
            } catch (Exception e) {
                log.error("===> generateStreamStr.error:{}", e.getMessage());
                consumer.accept(StringConstans.BLANK);
            }
        });

    }    
}
