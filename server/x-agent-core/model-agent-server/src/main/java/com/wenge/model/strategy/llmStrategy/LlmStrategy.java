package com.wenge.model.strategy.llmStrategy;

import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.entity.StepEntity;
import com.wg.appframe.yayi.entity.YayiMessage;

import java.util.List;
import java.util.function.Consumer;

public interface LlmStrategy {

    /**
     * 生成答案
     * @param question
     * @param messagesList
     * @param step
     * @param longFlag
     * @return
     */
    String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag);

    /**
     * 生成答案，支持流式输出
     * @param question
     * @param messagesList
     * @param step
     * @param longFlag
     * @param answerConsumer
     * @return
     */
    String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag, Consumer<AnswerStepData> answerConsumer);

    default List<YayiMessage.ToolCall> generateTools(String question, List<YayiMessage> messagesList, StepEntity step) {
        return null;
    }

}
