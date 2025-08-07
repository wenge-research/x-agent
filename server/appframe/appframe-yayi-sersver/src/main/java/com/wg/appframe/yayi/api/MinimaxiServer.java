package com.wg.appframe.yayi.api;

import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.param.MinmaxCompletionParam;
import com.wg.appframe.yayi.param.MinmaxImageParam;
import com.wg.appframe.yayi.param.MinmaxMusicParam;
import com.wg.appframe.yayi.param.MinmaxVideoParam;
import com.wg.appframe.yayi.result.*;
import com.wg.appframe.yayi.strategy.MinmaxImageStrategy;
import com.wg.appframe.yayi.strategy.MinmaxMusicStrategy;
import com.wg.appframe.yayi.strategy.MinmaxStrategy;
import com.wg.appframe.yayi.strategy.MinmaxVideoStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@Slf4j
public class MinimaxiServer {
    
    @Autowired(required = false)
    private MinmaxStrategy minmaxStrategy;

    @Autowired(required = false)
    private MinmaxImageStrategy minmaxImageStrategy;

    @Autowired(required = false)
    private MinmaxVideoStrategy minmaxVideoStrategy;

    @Autowired(required = false)
    private MinmaxMusicStrategy minmaxMusicStrategy;
    
    /**
     * 调用completions接口
     *
     * @param content
     * @return
     */
    public MinmaxCompletionResult generate(String content) {
        if (null == minmaxStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.minimax.completions >>>>>>>>>>>>");
            return new MinmaxCompletionResult();
        }

        MinmaxCompletionParam param = new MinmaxCompletionParam();
        param.setStream(false);
        return minmaxStrategy.run(content, param, null);
    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateStr(String content) {
        MinmaxCompletionResult result = generate(content);
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
    public MinmaxCompletionResult generate(String content, MinmaxCompletionParam param) {
        if (null == minmaxStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.minimax.completions >>>>>>>>>>>>");
            return new MinmaxCompletionResult();
        }
        if (null == param) {
            param = new MinmaxCompletionParam();
        }
        param.setStream(false);
        return minmaxStrategy.run(content, param, null);

    }

    /**
     * 调用completions接口，返回文本回答
     *
     * @param content
     * @return
     */
    public String generateString(String content, MinmaxCompletionParam param) {
        MinmaxCompletionResult result = generate(content, param);
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
    public void generateStream(String content, Consumer<MinmaxCompletionResult> consumer) {
        if (null == minmaxStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.minimax.completions >>>>>>>>>>>>");
            return;
        }
        MinmaxCompletionParam param = new MinmaxCompletionParam();
        minmaxStrategy.run(content, param, consumer);
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
    public void generateStream(String content, MinmaxCompletionParam param, Consumer<MinmaxCompletionResult> consumer) {
        if (null == minmaxStrategy) {
            log.error(">>>>>>>>>>>> 缺少配置：appframe.minimax.completions >>>>>>>>>>>>");
            return;
        }
        if (null == param) {
            param = new MinmaxCompletionParam();
        }
        param.setStream(true);
        minmaxStrategy.run(content, param, consumer);
    }

    /**
     * 调用completions接口，返回文本
     *
     * @param content
     * @return
     */
    public void generateStreamStr(String content, MinmaxCompletionParam param, Consumer<String> consumer) {
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


    /**
     * 文生图
     * @param content
     * @param param
     * @return
     */
    public MinmaxImageResult aiImage(String content, MinmaxImageParam param) {
        return minmaxImageStrategy.run(content, param);
    }

    /**
     * 文生图
     * @param content
     * @return
     */
    public MinmaxImageResult aiImage(String content) {
        return minmaxImageStrategy.run(content, new MinmaxImageParam());
    }


    /**
     * 视频生成
     * @param content
     * @return
     */
    public MinmaxVideoResult aiVideo(String content) {
        return minmaxVideoStrategy.run(content, new MinmaxVideoParam());
    }

    /**
     * 视频生成
     * @param content
     * @param param
     * @return
     */
    public MinmaxVideoResult aiVideo(String content, MinmaxVideoParam param) {
        return minmaxVideoStrategy.run(content, param);
    }

    /**
     * 视频任务查询
     * @param id
     * @param apikey
     * @return
     */
    public MinmaxVideoResult tasks(String id, String apikey) {
        return minmaxVideoStrategy.tasks(id, apikey);
    }

    /**
     * 视频任务查询
     * @param id
     * @param fileId
     * @return
     */
    public MinmaxFileResult download(String id, String fileId) {
        return minmaxVideoStrategy.download(id, fileId);
    }



    /**
     * 文生音频
     * @param content
     * @param param
     * @return
     */
    public MinmaxMusicResult aiMusic(String content, MinmaxMusicParam param) {
        return minmaxMusicStrategy.run(content, param);
    }

    /**
     * 文生音频
     * @param content
     * @return
     */
    public MinmaxMusicResult aiMusic(String content) {
        return minmaxMusicStrategy.run(content, new MinmaxMusicParam());
    }


}
