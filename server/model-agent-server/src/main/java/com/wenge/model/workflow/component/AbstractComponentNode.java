// package com.wenge.model.workflow.component;
//
// import cn.hutool.core.collection.CollectionUtil;
// import cn.hutool.core.collection.ListUtil;
// import cn.hutool.core.thread.ExecutorBuilder;
// import cn.hutool.core.thread.ThreadUtil;
// import cn.hutool.core.util.StrUtil;
// import cn.hutool.json.JSONObject;
// import cn.hutool.json.JSONUtil;
// import com.alibaba.fastjson.JSON;
// import com.alibaba.fastjson.JSONArray;
// import com.google.common.collect.Lists;
// import com.google.common.collect.Maps;
// import com.wenge.model.dto.result.ComponentNodeDto;
// import com.wenge.model.entity.ApplicationInfo;
// import com.wenge.model.entity.StepEntity;
// import com.wenge.model.enums.VariableStatusEnum;
// import com.wenge.model.utils.DateUtil;
// import com.wenge.model.utils.SseEmitterUtils;
// import com.wenge.model.workflow.component.dto.NodeRunStatus;
// import com.wenge.model.workflow.component.dto.WorkflowRunStatus;
// import com.wenge.model.workflow.component.event.RunCompletedEvent;
// import com.wenge.model.workflow.component.exception.NodeException;
// import com.wenge.model.workflow.component.gateway.IterationNode;
// import com.wenge.model.workflow.component.variable.IntegerSegment;
// import com.wenge.model.workflow.component.variable.Segments;
// import com.wenge.model.workflow.component.variable.Variable;
// import com.wenge.model.workflow.component.variable.VariablePool;
// import com.wenge.model.workflow.entity.MetaParam;
// import com.wenge.model.workflow.enums.ComponentNodeEnum;
// import com.wenge.model.workflow.enums.MetaParamEnum;
// import com.wenge.model.workflow.enums.WorkflowNodeExecutionStatus;
// import com.wenge.model.workflow.factory.ComponentNodeFactory;
// import com.wenge.model.workflow.factory.VariableFactory;
// import com.wg.appframe.core.constant.StringConstant;
// import com.wg.appframe.graylog.trace.MDCTraceUtils;
// import lombok.Getter;
// import lombok.Setter;
// import lombok.extern.slf4j.Slf4j;
// import org.dromara.easyes.common.utils.CollectionUtils;
// import org.dromara.easyes.common.utils.StringUtils;
// import org.slf4j.MDC;
//
// import java.io.Serializable;
// import java.util.*;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.LinkedBlockingQueue;
// import java.util.concurrent.ThreadPoolExecutor;
// import java.util.concurrent.locks.ReentrantLock;
// import java.util.function.Consumer;
// import java.util.stream.Collectors;
//
// import static com.wenge.model.workflow.constants.SettingConstants.*;
//
// /**
//  * 组件节点抽象类 有向图
//  */
// @Setter
// @Getter
// @Slf4j
// public abstract class AbstractComponentNode implements Serializable, ComponentNodeFactory, Cloneable {
//
//     private static final long serialVersionUID = -5011344895463438617L;
//
//     /**
//      * 迭代模式的时候，需要这里绑定迭代节点的id，用于获取参数
//      */
//     protected String iterationId;
//
//     /**
//      * 迭代号
//      */
//     protected Integer iterationNum;
//
//     /**
//      * 运行模式
//      */
//     protected String runModel = "debug";
//
//     /**
//      * sseId，用于从SseClientUtil中获取SseEmitter对象
//      */
//     protected String sseClientId;
//
//     /**
//      * 工作流关联的应用信息 用于取出应用配置
//      */
//     protected ApplicationInfo application;
//
//     /**
//      * 节点id
//      */
//     protected String nodeId;
//
//     /**
//      * 节点名称
//      */
//     protected String nodeName;
//
//     /**
//      * 节点入参
//      */
//     protected List<MetaParam> input;
//
//
//     /**
//      * 节点出参
//      */
//     protected List<MetaParam> output;
//
//     /**
//      * 特殊节点配置
//      */
//     protected JSONObject settings;
//
//     /**
//      * 节点类型
//      */
//     protected Integer nodeType;
//
//     /**
//      * 节点输入参数
//      */
//     protected Map<String, Object> paramMap;
//
//     /**
//      * 后继节点
//      */
//     protected List<AbstractComponentNode> nextNodes;
//
//     /**
//      * 前驱节点
//      */
//     protected List<AbstractComponentNode> predecessorNodes;
//
//     /**
//      * 节点执行状态
//      */
//     private WorkflowNodeExecutionStatus status = WorkflowNodeExecutionStatus.NOT_RUN;
//
//     /**
//      * 节点锁
//      */
//     protected ReentrantLock lock;
//
//     /**
//      * 节点运行状态
//      */
//     protected NodeRunStatus nodeRunStatus;
//
//     /**
//      * 节点执行事件
//      */
//     protected RunCompletedEvent event;
//
//     /**
//      * 每个节点的执行逻辑
//      *
//      * @param variablePool nodeName->每个节点的输出参数
//      */
//     public abstract RunCompletedEvent process(VariablePool variablePool) throws Exception;
//
//     /**
//      * 节点执行前，需要先锁定当前节点，防止重复执行，锁住后可以无需解锁
//      */
//     public void waitingPreNodes() {
//         log.info("等待前驱节点：{}", nodeName);
//         // 初始化节点运行状态
//         if (null == nodeRunStatus) {
//             nodeRunStatus = new NodeRunStatus(nodeId, nodeName);
//         }
//         // 初始化节点执行事件
//         if (null == event) {
//             event = new RunCompletedEvent(nodeRunStatus);
//         }
//
//         // 迭代模式不能用锁,无前驱节点也不需要锁
//         if (null == lock || CollectionUtil.isEmpty(predecessorNodes)) {
//             setStatus(WorkflowNodeExecutionStatus.RUNNING);
//             return;
//         }
//         try {
//             //  监听前驱节点
//             listenPreNodes();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         log.info("【{}】节点获释放锁", nodeName);
//         setStatus(WorkflowNodeExecutionStatus.RUNNING);
//     }
//
//     /**
//      * 执行后续节点
//      *
//      * @param variablePool
//      */
//     protected void next(VariablePool variablePool) {
//         next(variablePool, ListUtil.toList());
//     }
//
//     /**
//      * 执行后续节点
//      *
//      * @param variablePool
//      * @param stepEntityList
//      */
//     protected void next(VariablePool variablePool, List<StepEntity> stepEntityList) {
//         // 记录上下文
//         addContent(variablePool, stepEntityList);
//         // 记录运行时间
//         if (null == nodeRunStatus.getElapsedTime()) {
//             nodeRunStatus.setElapsedTime(System.currentTimeMillis() - nodeRunStatus.getStartTimestamp());
//             nodeRunStatus.setEndTime(DateUtil.getCurrentTime());
//         }
//
//         // 节点运行结果
//         if (null != output) {
//             Map<String, String> map = output.stream().collect(Collectors.toMap(
//                     MetaParam::getVariable,
//                     p -> StringUtils.isBlank(p.getValue()) ? StringConstant.BLANK : p.getValue(),
//                     (k1, k2) -> k1,
//                     Maps::newHashMap
//             ));
//
//             // 节点运行结果
//             if (null == nodeRunStatus.getOutput()) {
//                 nodeRunStatus.setOutput(JSONUtil.parseObj(map));
//             }
//         }
//
//         // 节点状态切换到等待
//         setStatus(WorkflowNodeExecutionStatus.WAITING_NEXT);
//
//         // 如果没有后续节点，那么就直接返回成功
//         if (CollectionUtil.isEmpty(nextNodes)) {
//             setStatus(WorkflowNodeExecutionStatus.SUCCESS);
//             return;
//         }
//
//         // 运行后续节点
//         ThreadPoolExecutor executor = triggerNextNode(variablePool);
//
//         // 监听线程池
//         if (null != executor) {
//             listenThreadPoo(executor);
//         }
//
//         // 监听子节点
//         listenNextNode();
//         setStatus(WorkflowNodeExecutionStatus.SUCCESS);
//     }
//
//     /**
//      * 填充节点公共参数
//      *
//      * @param componentNode
//      * @param node
//      */
//     protected void fill(AbstractComponentNode componentNode, ComponentNodeDto node) {
//         if (null != componentNode) {
//             componentNode.setNodeId(node.getNodeId());
//             componentNode.setNodeName(node.getNodeName());
//             componentNode.setInput(node.getInput());
//             componentNode.setOutput(node.getOutput());
//             componentNode.setNodeType(node.getNodeType());
//             componentNode.setSettings(JSONUtil.parseObj(node.getSettings()));
//             componentNode.setSseClientId(node.getSseClientId());
//             componentNode.setLock(new ReentrantLock());
//         }
//     }
//
//     /**
//      * 溯源 每个节点需要记录该步骤的执行情况
//      */
//     protected void addContent(VariablePool variablePool) {
//         StepEntity stepEntity = getStep(variablePool);
//         addContent(variablePool, stepEntity);
//     }
//
//     /**
//      * 溯源 每个节点需要记录该步骤的执行情况
//      *
//      * @param variablePool
//      * @return
//      */
//     private StepEntity getStep(VariablePool variablePool) {
//         StepEntity stepEntity = new StepEntity();
//         stepEntity.setCreateTime(nodeRunStatus.getStartTime());
//         stepEntity.setCostTime(System.currentTimeMillis() - nodeRunStatus.getStartTimestamp());
//         Object globalDialogueId = variablePool.userInputs.get(GLOBAL_DIALOGUE_ID);
//         if (null != globalDialogueId) {
//             stepEntity.setDialogueId(globalDialogueId.toString());
//         }
//         Object globalConversationId = variablePool.userInputs.get(GLOBAL_CONVERSATION_ID);
//         if (null != globalConversationId) {
//             stepEntity.setConversationId(globalConversationId.toString());
//         }
//         String iterationNumTemp = StringConstant.BLANK;
//         if (null != iterationNum) {
//             iterationNumTemp = "{" + iterationNum + "}";
//         }
//         stepEntity.setStep(nodeName + "【节点】" + iterationNumTemp);
//         stepEntity.setPrompt(paramMap);
//         stepEntity.setEndTime(DateUtil.getCurrentTime());
//         stepEntity.setResult(JSONUtil.toJsonStr(output));
//         return stepEntity;
//     }
//
//     /**
//      * 溯源 每个节点需要记录该步骤的执行情况
//      *
//      * @param variablePool
//      * @param stepEntityList 额外的参数
//      */
//     protected void addContent(VariablePool variablePool, List<StepEntity> stepEntityList) {
//         StepEntity stepEntity = getStep(variablePool);
//         if (CollectionUtils.isEmpty(stepEntityList)) {
//             stepEntityList = Lists.newArrayList();
//         }
//         stepEntityList.add(0, stepEntity);
//         for (StepEntity step : stepEntityList) {
//             variablePool.addStep(step);
//         }
//     }
//
//     protected void addContent(VariablePool variablePool, StepEntity stepEntity) {
//         variablePool.addStep(stepEntity);
//     }
//
//     @Override
//     public boolean equals(Object o) {
//         if (this == o) return true;
//         if (o == null || getClass() != o.getClass()) return false;
//         AbstractComponentNode node = (AbstractComponentNode) o;
//         return Objects.equals(nodeId, node.nodeId);
//     }
//
//     @Override
//     public int hashCode() {
//         return Objects.hashCode(nodeId);
//     }
//
//     /**
//      * 监听变量池变量状态
//      * @param input
//      * @param variablePool
//      * @param consumer
//      * @return
//      */
//     protected Map<String, Object> listenVariable(List<MetaParam> input, VariablePool variablePool, Consumer<Map<String, Object>> consumer) {
//         try {
//             // 默认输出text
//             return listenVariable(input, variablePool, consumer, StringConstant.BLANK);
//         } catch (Exception e) {
//             nodeFailStatus("获取变量失败:" + e.getMessage());
//             e.printStackTrace();
//         }
//         return Maps.newHashMap();
//     }
//
//     /**
//      * 监听变量池变量状态
//      *
//      * @param input
//      * @param variablePool
//      */
//     protected Map<String, Object> listenVariable(List<MetaParam> input, VariablePool variablePool, Consumer<Map<String, Object>> consumer, String format) {
//         // 获取变量值
//         Map<String, Object> concurrentHashMap = getValue(input, variablePool, format);
//         // 获取节点集合
//         HashMap<String, AbstractComponentNode> nodeMap = getNodeMap(variablePool);
//
//         if (null != consumer) {
//             boolean allNotInit = input.stream()
//                     // 过滤掉不执行的节点
//                     .filter(p -> {
//                         AbstractComponentNode abstractComponentNode = null;
//                         if (StringUtils.isNotBlank(p.getReferenceNodeId())) {
//                             abstractComponentNode = nodeMap.get(p.getReferenceNodeId());
//                         }
//                         return null == abstractComponentNode || !WorkflowNodeExecutionStatus.NOT_RUN.equals(abstractComponentNode.getStatus());
//                     })
//                     .allMatch(p -> VariableStatusEnum.UNINITIALIZED.getCode().equals(p.getStatus()));
//
//             if (!allNotInit) {
//                 consumer.accept(concurrentHashMap);
//             }
//         }
//
//         // 输出参数中，有LLM节点的引用，则需要等待所有LLM节点的输出
//         boolean anyLlmNodeFlag = input.stream().anyMatch(p -> VariableStatusEnum.UNINITIALIZED.getCode().equals(p.getStatus()) || VariableStatusEnum.UPDATING.getCode().equals(p.getStatus()));
//         if (!anyLlmNodeFlag) {
//             // 到这里，说明输出变量中，没有大模型节点的变量，那么直接输出结果
//             return concurrentHashMap;
//         } else {
//             // 监听变量更新
//             concurrentHashMap = listenVarUpdate(input, variablePool, consumer, format, nodeMap, concurrentHashMap);
//         }
//         return concurrentHashMap;
//     }
//
//     /**
//      * 替换占位符
//      *
//      * @param content
//      * @param concurrentHashMap
//      * @return
//      */
//     protected String replacePlaceholder(Map<String, Object> concurrentHashMap, String content) {
//         if (StringUtils.isBlank(content)) {
//             return content;
//         }
//
//         StringBuffer sb = new StringBuffer(content);
//         if (null != concurrentHashMap && !concurrentHashMap.isEmpty()) {
//             concurrentHashMap.forEach((k, v) -> {
//                 Optional<MetaParam> any = input.stream().filter(p -> k.equals(p.getVariable()))
//                         .findAny();
//                 if (!any.isPresent()) {
//                     return;
//                 }
//
//                 String toStr = sb.toString();
//                 if (null != v) {
//                     toStr = toStr.replace("${" + k + "}", v.toString());
//                     sb.delete(0, sb.length());
//                     sb.append(toStr);
//                 }
//             });
//         }
//
//         // 判断如果content中还有变量${变量名}，全部设置为空
//         if (sb.toString().contains("${")) {
//             return sb.toString().replaceAll("\\$\\{[^}]*}", StringConstant.BLANK);
//         }
//         return sb.toString();
//     }
//
//     /**
//      * 获取输入参数的值
//      *
//      * @param input
//      * @param variablePool
//      * @param format       字段的输出格式 text markdown
//      * @return
//      */
//     private Map<String, Object> getValue(List<MetaParam> input, VariablePool variablePool, String format) {
//         Map<String, Object> concurrentHashMap = new ConcurrentHashMap<>();
//         input.forEach(metaParam -> {
//             try {
//                 if (Objects.equals(runModel, WorkflowRunStatus.ITERATION)) {
//                     Optional<Variable> o = variablePool.get(Lists.newArrayList(iterationId, "index"));
//                     if (IterationNode.iterationNodeCurrentIndex.get() != null) {
//                         o = Optional.ofNullable(Variable.builder().segments(new IntegerSegment(metaParam.getVariable(), IterationNode.iterationNodeCurrentIndex.get())).build());
//                     }
//                     o.ifPresent(index -> {
//                         // 常量值
//                         String valueType = metaParam.getValueType();
//                         MetaParamEnum type = MetaParamEnum.getType(valueType);
//                         if (type != MetaParamEnum.REFERENCE) {
//                             concurrentHashMap.put(metaParam.getVariable(), metaParam.getValue());
//                             return;
//                         }
//                         // 迭代模式运行的时候，需要把类型改成非Array
//                         List<String> selector = new ArrayList<>();
//                         String type1 = metaParam.getType();
//                         MetaParamEnum type2 = MetaParamEnum.getType(type1);
//                         metaParam.setType(MetaParamEnum.getNotArrayType(type2).getName());
//                         String replace = metaParam.getValue() + index.getSegments().text();
//                         String[] split = replace.split("\\.");
//                         selector.add(metaParam.getReferenceNodeId());
//                         selector.addAll(Arrays.asList(split));
//                         Optional<Variable> variable = variablePool.get(selector);
//                         variable.ifPresent(var -> {
//                             Segments segments = var.getSegments();
//                             // 迭代模式下，同样需要更新变量状态，否则导致
//                             metaParam.setStatus(segments.getStatusEnum().getCode());
//                             getValueByType(metaParam, segments, nodeName, concurrentHashMap, format);
//                         });
//                     });
//                 } else {
//                     Variable variable = variablePool.get(metaParam, nodeName);
//                     Segments<?> segments = variable.getSegments();
//                     if (null != segments) {
//                         // 两种情况 1 前节点未执行 2 前节点未输出完
//                         metaParam.setStatus(segments.getStatusEnum().getCode());
//                         getValueByType(metaParam, segments, nodeName, concurrentHashMap, format);
//                     }
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//                 metaParam.setStatus(VariableStatusEnum.ERROR.getCode());
//                 nodeFailStatus("获取入参失败：" + e.getMessage());
//             }
//         });
//
//         Map<String, Object> userInputs = variablePool.userInputs;
//         // 全局变量
//         userInputs.forEach((k, v) -> {
//             if (k.startsWith(GLOBAL_START)) {
//                 concurrentHashMap.put(k, v);
//             }
//         });
//         // 状态节点设置输入参数
//         if (null == nodeRunStatus.getInput()) {
//             nodeRunStatus.setInput(JSONUtil.parseObj(concurrentHashMap));
//         }
//         return concurrentHashMap;
//     }
//
//     /**
//      * 根据类型获取值
//      *
//      * @param metaParam
//      * @param segments
//      * @param concurrentHashMap
//      * @param format
//      */
//     private void getValueByType(MetaParam metaParam, Segments segments, String nodeName, Map<String, Object> concurrentHashMap, String format) {
//         if (null == segments) {
//             return;
//         }
//         Object value = null;
//
//         // 2025-05-20,这里暂时不支持格式转换,最终还是以变量类型为输出
//         format = StringConstant.BLANK;
//         // 如果传了格式，则说明需要最终输出,而不是格式转换
//         if (StrUtil.isNotBlank(format)) {
//             if (Segments.MARKDOWN.equals(format)) {
//                 value = segments.markdown();
//             } else if (Segments.JSON_TYPE.equals(format)) {
//                 value = segments.json();
//             } else if (Segments.TEXT.equals(format)) {
//                 value = segments.text();
//             } else {
//                 throw new RuntimeException("不支持的格式");
//             }
//         } else {
//             String valueType = metaParam.getType();
//             MetaParamEnum type = MetaParamEnum.getType(valueType);
//             if (null == type) {
//                 throw new NodeException(StringConstant.BLANK, "字段类型不能为空");
//             }
//             Object segmentsValue = segments.getValue();
//             value = getValue(nodeName, metaParam.getVariable(), type, segmentsValue);
//         }
//         if (null != value) {
//             concurrentHashMap.put(metaParam.getVariable(), value);
//         }
//     }
//
//     /**
//      * 设置节点状态并推送节点日志
//      *
//      * @param status
//      */
//     public void setOrgriStatus(WorkflowNodeExecutionStatus status) {
//         this.status = status;
//     }
//
//     /**
//      * 初始化运行状态
//      */
//     protected void initRunStatus() {
//         if (null == nodeRunStatus) {
//             nodeRunStatus = new NodeRunStatus(nodeId.split(":")[0], nodeName);
//             nodeRunStatus.setIterationNum(iterationNum);
//             nodeRunStatus.setStartTimestamp(System.currentTimeMillis());
//         }
//     }
//     public void setStatus(WorkflowNodeExecutionStatus status) {
//         this.status = status;
//         if (null == nodeRunStatus) {
//             initRunStatus();
//         }
//         // 节点开始时
//         if (null == nodeRunStatus.getStartTime() && WorkflowNodeExecutionStatus.RUNNING.equals(status)) {
//             nodeRunStatus.setStartTime(DateUtil.getCurrentTime());
//         }
//         if (null == nodeRunStatus.getStartTimestamp() && WorkflowNodeExecutionStatus.RUNNING.equals(status)) {
//             // 节点开始时间戳
//             nodeRunStatus.setStartTimestamp(System.currentTimeMillis());
//         }
//         nodeRunStatus.setStatus(status);
//         // 非运行模式下的工作流，再推送节点日志
//         try {
//             // log.info(">>>>>>{}【{}】节点推送节点日志：{}", nodeName, iterationNum, JSON.toJSONString(nodeRunStatus));
//             SseEmitterUtils.send(sseClientId, JSON.toJSONString(nodeRunStatus));
//         } catch (Exception e) {
//             log.info("SSE发送失败:{0}", e);
//         }
//     }
//
//     /**
//      * 获取节点变量
//      *
//      * @param metaParam
//      * @param segments
//      * @param variablePool
//      * @return
//      */
//     private Object getFromNode(MetaParam metaParam, Segments segments, VariablePool variablePool) {
//         List<ComponentNodeDto> nodes = variablePool.getNodes();
//         if (CollectionUtils.isNotEmpty(nodes) && StringUtils.isNotBlank(metaParam.getReferenceNodeId())) {
//             Optional<ComponentNodeDto> any = nodes.stream().filter(p -> p.getNodeId().equals(metaParam.getReferenceNodeId())).findAny();
//             if (any.isPresent()) {
//                 ComponentNodeDto componentNodeDto = any.get();
//                 if (componentNodeDto.getNodeType().equals(ComponentNodeEnum.VARIABLE.getCode())) {
//                     List<MetaParam> metaParamList = componentNodeDto.getOutput();
//                     if (CollectionUtils.isNotEmpty(metaParamList)) {
//                         MetaParam outMetaParam = metaParamList.get(0);
//                         String valueType1 = outMetaParam.getValueType();
//                         MetaParamEnum valueType = MetaParamEnum.getType(valueType1);
//                         if (null != valueType) {
//                             switch (valueType) {
//                                 case STRING:
//                                     return segments.markdown();
//                                 case OBJECT:
//                                     return segments.getValue();
//                                 case ARRAY_STR:
//                                 case ARRAY_OBJ:
//                                     String jsonStr = JSONUtil.toJsonStr(segments.getValue());
//                                     if (StringUtils.isNotBlank(jsonStr) && JSONUtil.isTypeJSONArray(jsonStr)) {
//                                         return JSONUtil.parseArray(jsonStr);
//                                     }
//                                     break;
//                                 default:
//                                     break;
//                             }
//                         }
//                     }
//
//                 }
//             }
//         }
//         return null;
//     }
//
//     /**
//      * 节点出参添加进变量池
//      *
//      * @param out
//      * @param variablePool
//      * @param answer
//      */
//     protected void addVariable(MetaParam out, VariablePool variablePool, Object answer) {
//         addVariable(out, variablePool, answer, null);
//     }
//
//     /**
//      * 节点出参添加进变量池
//      *
//      * @param out
//      * @param variablePool
//      * @param answer
//      */
//     protected void addVariable(MetaParam out, VariablePool variablePool, Object answer, VariableStatusEnum status) {
//         if (null == status) {
//             status = VariableStatusEnum.COMPLETE;
//         }
//         VariableStatusEnum statusTemp = status;
//         try {
//             if (Objects.equals(runModel, WorkflowRunStatus.ITERATION)) {
//                 Optional<Variable> o = variablePool.get(Lists.newArrayList(iterationId, "index"));
//                 if (IterationNode.iterationNodeCurrentIndex.get() != null) {
//                     o = Optional.ofNullable(Variable.builder().segments(new IntegerSegment(out.getVariable(), IterationNode.iterationNodeCurrentIndex.get())).build());
//                 }
//                 // 迭代节点添加参数的时候需要加上索引，防止同变量名覆盖
//                 o.ifPresent(index -> variablePool.add(VariableFactory.createVariable(nodeName, out.getType(), nodeId, out.getVariable() + index.getSegments().text(), answer, statusTemp)));
//             } else {
//                 variablePool.add(VariableFactory.createVariable(nodeName, out.getType(), nodeId, out.getVariable(), answer, status));
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//             nodeFailStatus("【" + out.getVariable() + "】类型转化错误:" + e.getMessage());
//             MetaParamEnum type = MetaParamEnum.getType(out.getType());
//             Segments<?> segments = new Segments(out.getVariable(), type, null);
//             segments.setStatusEnum(VariableStatusEnum.ERROR);
//             Variable build = Variable.builder().nodeId(nodeId).name(out.getVariable()).segments(segments).build();
//             variablePool.add(build);
//         }
//         setOutputValue(out, answer);
//     }
//
//     /**
//      * 设置输出参数
//      *
//      * @param metaParam
//      * @param value
//      */
//     protected void setOutputValue(MetaParam metaParam, Object value) {
//         String type1 = metaParam.getType();
//         MetaParamEnum type = MetaParamEnum.getType(type1);
//         if (null == type) {
//             String valueType = metaParam.getValueType();
//             type = MetaParamEnum.getType(valueType);
//         }
//         if (null == type) {
//             return;
//         }
//         switch (type) {
//             case STRING:
//             case INTEGER:
//             case NUMBER:
//             case BOOLEAN:
//                 if (null != value) {
//                     metaParam.setValue(String.valueOf(value));
//                 }
//                 break;
//             case OBJECT:
//             case ARRAY_OBJ:
//             case ARRAY_INT:
//             case ARRAY_BOOL:
//             case ARRAY_STR:
//                 if (null != value) {
//                     metaParam.setValue(JSONUtil.toJsonStr(value));
//                 }
//                 break;
//             case FILE:
//             case ARRAY_FILE:
//                 break;
//             default:
//                 break;
//         }
//     }
//
//     /**
//      * 节点入参类型转换,string
//      *
//      * @param metaParam
//      * @param segments
//      * @return
//      */
//     private Object caseString(MetaParam metaParam, Segments segments) {
//         Object value = segments.text();
//         if (null == segments.getValue() || !(segments.getValue() instanceof String)) {
//             return null;
//         }
//         String segmentsValue = (String) segments.getValue();
//         String type = metaParam.getType();
//         MetaParamEnum type1 = MetaParamEnum.getType(type);
//         if (null != type1) {
//             switch (type1) {
//                 case STRING:
//                     value = segmentsValue;
//                     break;
//                 case ARRAY_OBJ:
//                     if (JSONUtil.isTypeJSONArray(segmentsValue)) {
//                         value = JSON.parseArray(segmentsValue);
//                     }
//                     break;
//                 case OBJECT:
//                     if (JSONUtil.isTypeJSONObject(segmentsValue)) {
//                         value = JSON.parseObject(segmentsValue);
//                     }
//                     break;
//                 case ARRAY_STR:
//                     if (JSONUtil.isTypeJSONArray(segmentsValue)) {
//                         JSONArray array = JSON.parseArray(segmentsValue);
//                         value = array.stream().map(JSON::toJSONString).collect(Collectors.toList());
//                     }
//                     break;
//                 default:
//                     break;
//             }
//         }
//         return value;
//     }
//
//     /**
//      * 节点入参类型转换,array_obj
//      *
//      * @param metaParam
//      * @param segments
//      * @return
//      */
//     private Object caseArrayObj(MetaParam metaParam, Segments segments) {
//         Object value = null;
//         if (null == segments.getValue() || !(segments.getValue() instanceof List)) {
//             return value;
//         }
//         String type = metaParam.getType();
//         MetaParamEnum type1 = MetaParamEnum.getType(type);
//         if (null != type1) {
//             List dataList = (List) segments.getValue();
//             switch (type1) {
//                 case STRING:
//                     value = JSON.toJSONString(dataList);
//                     break;
//                 case ARRAY_OBJ:
//                     value = dataList;
//                     break;
//                 case OBJECT:
//                     value = dataList;
//                     break;
//                 case ARRAY_STR:
//                     value = dataList.stream().map(JSON::toJSONString).collect(Collectors.toList());
//                     break;
//                 default:
//                     break;
//             }
//         }
//         return value;
//     }
//
//     /**
//      * 节点入参类型转换,object
//      *
//      * @param metaParam
//      * @param segments
//      * @return
//      */
//     private Object caseObj(MetaParam metaParam, Segments segments, String format) {
//         Object value = null;
//         if (null == segments.getValue()) {
//             return value;
//         }
//         String type = metaParam.getType();
//         MetaParamEnum type1 = MetaParamEnum.getType(type);
//         if (null != type1) {
//             Object value1 = segments.getValue();
//             switch (type1) {
//                 case STRING:
//                     if (Segments.MARKDOWN.equals(format)) {
//                         // 特殊情况需要输出markdown格式
//                         value = segments.markdown();
//                     } else {
//                         // 一般情况直接赋值
//                         value = JSON.toJSONString(value1);
//                     }
//
//                     break;
//                 case ARRAY_OBJ:
//                     value = ListUtil.toList(value1);
//                     break;
//                 case OBJECT:
//                     value = value1;
//                     break;
//                 case ARRAY_STR:
//                     value = ListUtil.toList(value1).stream().map(JSON::toJSONString).collect(Collectors.toList());
//                     break;
//                 default:
//                     break;
//             }
//         }
//
//         return value;
//     }
//
//     /**
//      * 去除全局参数
//      */
//     protected void removeGlobalParameters(Map<String, Object> paramMap) {
//         List<String> metaParams = input.stream().map(MetaParam::getVariable).collect(Collectors.toList());
//         // 去除全局参数
//         for (String s : paramMap.keySet()) {
//             if (!metaParams.contains(s)) {
//                 paramMap.remove(s);
//             }
//         }
//     }
//
//     /**
//      * 设置节点执行状态
//      *
//      * @param msg
//      */
//     protected synchronized void nodeFailStatus(String msg) {
//         if (null == nodeRunStatus) {
//             initRunStatus();
//         }
//         nodeRunStatus.setErrorMessage(msg);
//         nodeRunStatus.setElapsedTime(System.currentTimeMillis() - nodeRunStatus.getStartTimestamp());
//         setStatus(WorkflowNodeExecutionStatus.FAIL);
//     }
//
//     /**
//      * 设置节点执行状态
//      *
//      * @param msg
//      */
//     protected synchronized void nodeRunningStatus(String msg, JSONObject processData) {
//         if (null == nodeRunStatus) {
//             initRunStatus();
//         }
//         nodeRunStatus.setMessage(msg);
//         nodeRunStatus.setElapsedTime(System.currentTimeMillis() - nodeRunStatus.getStartTimestamp());
//         if (null != processData) {
//             nodeRunStatus.setProcessData(processData);
//         }
//         setStatus(WorkflowNodeExecutionStatus.RUNNING);
//         nodeRunStatus.setMessage(StringConstant.BLANK);
//     }
//
//     @Override
//     public AbstractComponentNode clone() {
//         try {
//             AbstractComponentNode cloned = (AbstractComponentNode) super.clone();
//             if (null != this.application) {
//                 cloned.application = this.application.clone();
//             }
//             cloned.input = null;
//             cloned.output = null;
//             cloned.nextNodes = null;
//             cloned.predecessorNodes = null;
//             cloned.nodeRunStatus = null;
//             cloned.event = null;
//             cloned.status = null;
//             cloned.lock = null;
//             if (null != settings) {
//                 cloned.settings = settings.clone();
//             }
//
//             return cloned;
//         } catch (CloneNotSupportedException e) {
//             throw new AssertionError(); // 不会发生
//         }
//     }
//
//     /**
//      * 判断当前节点是否全部前置节点执行完毕，true:继续等待(将会执行到该节点)，false:跳过该节点（不会执行到该节点）
//      *
//      * @param currentNode
//      * @return
//      */
//     public static boolean findPreNodeAllNot(AbstractComponentNode enterNode, AbstractComponentNode currentNode) {
//         // Integer nodeType = currentNode.getNodeType();
//         // log.info("111111->{}, 99999-->{}", enterNode.getNodeName(), currentNode.getNodeName());
//
//         // 获取前驱节点
//         List<AbstractComponentNode> predecessorNodesList = currentNode.getPredecessorNodes();
//
//         if (CollectionUtil.isEmpty(predecessorNodesList)) {
//             return false;
//         }
//
//         // 判断前置节点是否为入口节点，如果只有入口节点，则不等待
//         boolean onlyEnterNodeFlag = predecessorNodesList.stream().allMatch(p -> p.getNodeId().equals(enterNode.getNodeId()));
//         if (onlyEnterNodeFlag) {
//             return false;
//         }
//
//         // 遍历前置节点，只要有一个返回 ture，那么就继续等待
//         for (AbstractComponentNode node : predecessorNodesList) {
//             if (node.getNodeId().equals(enterNode.getNodeId())) {
//                 continue;
//             }
//
//             // 如果有条件节点或则是分类节点，  这两个节点，下级节点只会选择一条路，不会全部执行全部子级节点
//             if (Objects.equals(node.getNodeType(), ComponentNodeEnum.CLASSIFICATION.getCode())
//                     || Objects.equals(node.getNodeType(), ComponentNodeEnum.EXCLUSIVE.getCode())) {
//                 // 如果条件节点或则是分类节点状态为 NOT_RUN，则继续往前找
//                 if (WorkflowNodeExecutionStatus.NOT_RUN.equals(node.getStatus())) {
//                     // return findPreNodeAllNot(enterNode, node);
//                     boolean preNodeAllNot = findPreNodeAllNot(enterNode, node);
//                     if (!preNodeAllNot) {
//                         continue;
//                     }
//                     return true;
//                 } else {
//                     // 如果条件节点或则是分类节点状态不为 NOT_RUN
//
//                     // 获取子级节点
//                     List<AbstractComponentNode> nextNodesList = node.getNextNodes();
//
//                     // 判断子级节点是否还没开始执行
//                     boolean allMatch = nextNodesList.stream()
//                             .allMatch(nextNode ->
//                                     WorkflowNodeExecutionStatus.NOT_RUN.equals(nextNode.getStatus()));
//
//                     // 如果所有子级节点都为 NOT_RUN，则继续等待，因为还不确定是否会是当前节点会执行
//                     if (allMatch) {
//                         // 有可能没有配置其他分支，只有当前分支，那么不等待
//                         if (1 != nextNodesList.size()) {
//                             return  false;
//                         }
//                         return true;
//                     } else {
//                         // 获取到执行的子级节点，并判断这个节点是否为当前节点，如果不为当前节点，则继续等待
//                         return nextNodesList.stream()
//                                 .filter(nextNode -> !WorkflowNodeExecutionStatus.NOT_RUN.equals(nextNode.getStatus()))
//                                 .anyMatch(p -> p.getNodeId().equals(currentNode.getNodeId()));
//                     }
//                 }
//             } else {
//                 if (node.getStatus().equals(WorkflowNodeExecutionStatus.NOT_RUN)) {
//                     boolean preNodeAllNot = findPreNodeAllNot(enterNode, node);
//                     if (!preNodeAllNot) {
//                         continue;
//                     }
//                     return true;
//                 }
//                 return true;
//             }
//         }
//         return false;
//     }
//
//     /**
//      * 触发下一个节点
//      * @param variablePool
//      */
//     private ThreadPoolExecutor triggerNextNode(VariablePool variablePool) {
//         String traceId = StringUtils.isNotBlank(MDC.get(MDCTraceUtils.KEY_TRACE_ID)) ? MDC.get(MDCTraceUtils.KEY_TRACE_ID) : Thread.currentThread().getName();
//         if (CollectionUtil.isNotEmpty(nextNodes)) {
//             // 只有一个子节点，那么不需要使用线程池
//             if (nextNodes.size() == 1) {
//                 runNextNode(variablePool, nextNodes.get(0), traceId, false);
//             } else {
//                 // 3. 创建线程池
//                 ExecutorBuilder executorBuilder = ExecutorBuilder.create().setCorePoolSize(16).setMaxPoolSize(32).setWorkQueue(new LinkedBlockingQueue<>());
//                 ThreadPoolExecutor executor = executorBuilder.useSynchronousQueue().build();
//                 for (AbstractComponentNode node : nextNodes) {
//                     // log.info("【{}】节点开始执行子级:{}", nodeName, node.getNodeName());
//                     executor.submit(() -> {
//                         MDC.put(MDCTraceUtils.KEY_TRACE_ID, traceId);
//                         runNextNode(variablePool, node, traceId, true);
//                     });
//                 }
//                 return executor;
//             }
//         }
//         return null;
//     }
//
//     /**
//      * 监听线程池
//      *
//      * @param executor
//      */
//     private void listenThreadPoo(ThreadPoolExecutor executor) {
//         log.info("【{}】节点开始监听子级线程:{}", nodeName, executor.getActiveCount());
//         long max = 30 * 60 * 1000;
//         long start = System.currentTimeMillis();
//         while (executor.getActiveCount() != 0 && !executor.isShutdown() && !executor.isTerminated()) {
//             ThreadUtil.sleep(100);
//             if (System.currentTimeMillis() - start > max) {
//                 break;
//             }
//         }
//         executor.shutdown();
//         log.info("【{}】节点结束监听子级线程:{}", nodeName, executor.getActiveCount());
//     }
//
//     /**
//      *  监听下一个节点
//      */
//     private void listenNextNode() {
//         String traceId = MDC.get(MDCTraceUtils.KEY_TRACE_ID);
//         log.info("【{}】节点开始监听下一个节点", nodeName);
//         ThreadUtil.sleep(100);
//         long start = System.currentTimeMillis();
//         long maxWait = 30 * 60 * 1000;
//         if (CollectionUtil.isNotEmpty(nextNodes)) {
//             while (LlmNode.listeningNextNode(nextNodes)) {
//                 if (System.currentTimeMillis() - start > maxWait) {
//                     log.info("大模型节点监听超时");
//                     break;
//                 }
//                 ThreadUtil.sleep(100);
//             }
//         }
//          log.info("【{}】节点结束监听下一个节点", nodeName);
//     }
//
//     /**
//      * 监听前驱节点
//      */
//     private void listenPreNodes() {
//         log.info("【{}】节点开始监听前驱节点", nodeName);
//         long maxWaite = 30 * 60 * 1000;
//         long start = System.currentTimeMillis();
//         // 监听前驱节点是否在等待子节点，必须等上级节点全部执行完后才能执行本节点
//         while (true) {
//             if (System.currentTimeMillis() - start > maxWaite) {
//                 throw new NodeException(nodeName, "等待前驱节点超时");
//             }
//             // 如果前驱节点只有一个，就没必要监听
//             if (predecessorNodes.size() == 1) {
//                 break;
//             }
//             // 如果所有前驱节点都已经执行完成，那么就可以执行了
//             boolean allMatch = predecessorNodes.stream()
//                     .filter(node -> {
//                         boolean currentNodeRun = !node.getStatus().equals(WorkflowNodeExecutionStatus.NOT_RUN);
//                         if (!currentNodeRun) {
//                             return findPreNodeAllNot(node, node);
//                         }
//                         return true;
//                     })
//                     .allMatch(p -> p.getStatus().equals(WorkflowNodeExecutionStatus.WAITING_NEXT));
//             if (allMatch) {
//                 break;
//             }
//             ThreadUtil.safeSleep(100);
//         }
//          log.info("【{}】节点结束监听前驱节点", nodeName);
//     }
//
//     /**
//      * 监听变量更新
//      *
//      * @param input
//      * @param variablePool
//      * @param consumer
//      * @param format
//      * @param nodeMap
//      * @param concurrentHashMap
//      */
//     private Map<String, Object> listenVarUpdate(List<MetaParam> input, VariablePool variablePool, Consumer<Map<String, Object>> consumer, String format, HashMap<String, AbstractComponentNode> nodeMap, Map<String, Object> concurrentHashMap) {
//         log.info("【{}】节点开始监听变量更新", nodeName);
//         long startTime = System.currentTimeMillis();
//         boolean allMatch = false;
//         boolean allNotInit = false;
//         boolean updatingFlag = false;
//         long maxTime = 20 * 60 * 1000;
//         List<MetaParam> filterList = Lists.newArrayList();
//         Map<String, Object> thisResult = Maps.newHashMap();
//         while (true) {
//             // 最多等待180秒
//             if (System.currentTimeMillis() - startTime > 1000 * 180) {
//                 // 如果有变量还在持续更新，那么继续监听
//                 MetaParam metaParam = input.get(0);
//                 updatingFlag = input.stream().anyMatch(p -> {
//                     String referenceNodeId = metaParam.getReferenceNodeId();
//                     AbstractComponentNode componentNodeDto = null;
//                     if (StringUtils.isNotBlank(referenceNodeId)) {
//                         componentNodeDto = nodeMap.get(referenceNodeId);
//                     }
//                     // 先过滤掉不执行的节点，然后判断是否有而且正在更新的变量
//                     return (null == componentNodeDto || !WorkflowNodeExecutionStatus.NOT_RUN.equals(componentNodeDto.getStatus())
//                             && VariableStatusEnum.UPDATING.getCode().equals(p.getStatus()));
//                 });
//
//                 // 避免节点异常，无法刷新字段，导致字段一直都是更新状态，在超时后无论是否在推流，直接退出监听
//                 if (System.currentTimeMillis() - startTime > maxTime) {
//                     log.info("直接退出监听");
//                     break;
//                 }
//                 if (!updatingFlag) {
//                     break;
//                 }
//             }
//
//             // 过滤掉不执行的节点，并过滤掉已经完成或者错误的变量
//             filterList = input.stream()
//                     .filter(metaParam -> !VariableStatusEnum.ERROR.getCode().equals(metaParam.getStatus()))
//                     .filter(metaParam -> filterNode(metaParam, nodeMap))
//                     .filter(this::filterNotStop)
//                     .collect(Collectors.toList());
//
//             // AtomicReference<String> question = new AtomicReference<>();
//             // 如果没有自定义指令,则直接用输入的问题
//             thisResult = getValue(filterList, variablePool, format);
//             concurrentHashMap.putAll(thisResult);
//
//             ThreadUtil.safeSleep(100);
//             // 判断是否需要更新
//             allMatch = input.stream()
//                     // 过滤掉不执行的节点
//                     .filter(p -> filterNode(p, nodeMap))
//                     .noneMatch(this::filterNotStop);
//
//             allNotInit = input.stream()
//                     // 过滤掉不执行的节点
//                     .filter(p -> filterNode(p, nodeMap))
//                     .allMatch(p -> VariableStatusEnum.UNINITIALIZED.getCode().equals(p.getStatus()));
//             // 持续输出结果
//             if (null != consumer && !allNotInit) {
//                 consumer.accept(concurrentHashMap);
//             }
//             // 如果全部匹配，则跳出循环（所有变量完成更新）
//             if (allMatch) {
//                 log.info("变量全部更新完成");
//                 break;
//             }
//         }
//         log.info("【{}】节点结束监听变量更新", nodeName);
//         return concurrentHashMap;
//     }
//
//     /**
//      * 运行下一个节点
//      *
//      * @param variablePool
//      * @param node
//      */
//     private void runNextNode(VariablePool variablePool, AbstractComponentNode node, String traceId, boolean clearMdcFlag) {
//         try {
//             // 设置线程名
//             Thread.currentThread().setName(traceId);
//             MDC.put(MDCTraceUtils.KEY_TRACE_ID, traceId);
//             // 如果子节点没有获取到锁，那么就不执行子节点，不需要解锁
//             if (null == node.getLock() || node.getLock().tryLock()) {
//                 log.info("【{}】节点获取到子级的锁:{}", nodeName, node.getNodeName());
//                 node.setStatus(WorkflowNodeExecutionStatus.INIT);
//                 node.waitingPreNodes();
//                 node.process(variablePool);
//             } else {
//                 log.info("【{}】节点未获取到子级的锁:{}", nodeName, node.getNodeName());
//             }
//         } catch (Exception e) {
//             if (clearMdcFlag) {
//                 MDC.clear();
//             }
//             e.printStackTrace();
//             throw new NodeException(node.nodeName, e.getMessage());
//         } finally {
//             if (clearMdcFlag) {
//                 MDC.clear();
//             }
//         }
//     }
//
//     /**
//      * 获取变量值
//      *
//      * @param variable
//      * @param type
//      * @param segmentsValue
//      * @return
//      */
//     public static Object getValue(String nodeName, String variable, MetaParamEnum type, Object segmentsValue) {
//         if (null == type) {
//             throw new NodeException(nodeName, "变量【" + variable + "】类型不能为空");
//         }
//         if (null == segmentsValue) {
//             return null;
//         }
//         Object value = null;
//         switch (type) {
//             // todo 该switch可以删除，可以通过segment自动找到实现，目前先保留原逻辑，只修改ArrayInt
//             case STRING:
//                 value = VariableFactory.caseString(nodeName, variable, segmentsValue);
//                 break;
//             case OBJECT:
//                 value = VariableFactory.caseObj(nodeName, variable, segmentsValue);
//                 break;
//             case INTEGER:
//                 value = VariableFactory.caseLong(nodeName, variable, segmentsValue);
//                 break;
//             case NUMBER:
//                 value = VariableFactory.caseNumber(nodeName, variable, segmentsValue);
//                 break;
//             case FILE:
//                 value = VariableFactory.caseFile(nodeName, variable, segmentsValue);
//                 break;
//             case BOOLEAN:
//                 value = VariableFactory.caseString(nodeName, variable, segmentsValue);
//                 break;
//             case ARRAY_OBJ:
//                 value = VariableFactory.caseArrayObj(nodeName, variable, segmentsValue);
//                 break;
//             case ARRAY_STR:
//                 value = VariableFactory.caseArrayStr(nodeName, variable, segmentsValue);
//                 break;
//             case ARRAY_INT:
//                 value = VariableFactory.caseArrayInt(nodeName, variable, segmentsValue);
//                 break;
//             case ARRAY_FILE:
//                 value = VariableFactory.caseArrayFile(nodeName, variable, segmentsValue);
//                 break;
//             case ARRAY_NUMBER:
//                 value = VariableFactory.caseArrayNum(nodeName, variable, segmentsValue);
//                 break;
//             default:
//                 break;
//         }
//         return value;
//     }
//
//     /**
//      * 过滤节点
//      * @param metaParam
//      * @param nodeMap
//      * @return
//      */
//     private boolean filterNode(MetaParam metaParam, HashMap<String, AbstractComponentNode> nodeMap) {
//         String referenceNodeId = metaParam.getReferenceNodeId();
//         AbstractComponentNode componentNodeDto = null;
//         if (StringUtils.isNotBlank(referenceNodeId)) {
//             componentNodeDto = nodeMap.get(referenceNodeId);
//             if (null == componentNodeDto) {
//                 return false;
//             }
//         }
//         // 先过滤掉不执行的节点，然后判断是否有而且正在更新的变量
//         return null == componentNodeDto || !WorkflowNodeExecutionStatus.NOT_RUN.equals(componentNodeDto.getStatus()) && !WorkflowNodeExecutionStatus.FAIL.equals(componentNodeDto.getStatus());
//     }
//
//     /**
//      * 过滤停止更新状态的变量
//      * @param metaParam
//      * @return
//      */
//     private boolean filterNotStop(MetaParam metaParam) {
//         return !VariableStatusEnum.ERROR.getCode().equals(metaParam.getStatus()) && !VariableStatusEnum.COMPLETE.getCode().equals(metaParam.getStatus());
//     }
//
//
//     private HashMap<String, AbstractComponentNode> nodeMap;
//
//     /**
//      * 获取所有节点
//      * @return
//      */
//     protected HashMap<String, AbstractComponentNode> getNodeMap(VariablePool variablePool) {
//         List<AbstractComponentNode> componentNodes = variablePool.getComponentNodes();
//         if (null == nodeMap || nodeMap.isEmpty() || nodeMap.size() != componentNodes.size()) {
//             // 获取所有节点,并且根据nodeId封装 map
//             nodeMap = componentNodes.stream().collect(Collectors.toMap(
//                     AbstractComponentNode::getNodeId,
//                     p -> p,
//                     (k1, k2) -> k1,
//                     Maps::newHashMap
//             ));
//         }
//         return nodeMap;
//     }
// }
