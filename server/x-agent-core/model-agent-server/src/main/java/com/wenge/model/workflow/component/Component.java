// package com.wenge.model.workflow.component;
//
// import cn.hutool.core.collection.CollUtil;
// import cn.hutool.json.JSONObject;
// import com.wenge.model.workflow.constants.SettingConstants;
// import com.wenge.model.workflow.enums.ComponentNodeEnum;
// import lombok.extern.slf4j.Slf4j;
//
// import java.util.*;
//
// @Slf4j
// @Deprecated
// public class Component implements Graph<AbstractComponentNode> {
//     private final Map<AbstractComponentNode, List<AbstractComponentNode>> adjacencyList = new HashMap<>();
//
//     /**
//      * 开始节点的输入参数
//      */
//     private final JSONObject input;
//
//     public Component(JSONObject input) {
//         this.input = input;
//     }
//
//     @Override
//     public void addNode(AbstractComponentNode node) {
//         adjacencyList.putIfAbsent(node, new ArrayList<>());
//     }
//
//     @Override
//     public void addEdge(AbstractComponentNode from, AbstractComponentNode to) {
//         List<AbstractComponentNode> nodes = adjacencyList.get(from);
//         if (nodes == null) {
//             return;
//         }
//         nodes.add(to);
//     }
//
//     @Override
//     public void removeNode(AbstractComponentNode node) {
//         adjacencyList.remove(node);
//     }
//
//     @Override
//     public void removeEdge(AbstractComponentNode sourceNode, AbstractComponentNode targetNode) {
//         List<AbstractComponentNode> nodes = adjacencyList.get(sourceNode);
//         if (CollUtil.isEmpty(nodes)) {
//             return;
//         }
//         nodes.remove(targetNode);
//     }
//
//     @Override
//     public void clear() {
//         adjacencyList.clear();
//     }
//
//     @Override
//     public boolean isEmpty() {
//         return adjacencyList.isEmpty();
//     }
//
//     @Override
//     public int size() {
//         return adjacencyList.size();
//     }
//
//     @Override
//     public List<Map<String, JSONObject>> bfs() {
//         Set<AbstractComponentNode> visited = new HashSet<>();
//         Queue<AbstractComponentNode> queue = new LinkedList<>();
//
//         AbstractComponentNode start = adjacencyList.keySet().stream()
//                 .filter(node -> Objects.equals(node.getNodeType(), ComponentNodeEnum.START.getCode())).findFirst()
//                 .orElseThrow(() -> new RuntimeException("未找到开始节点"));
//
//         visited.add(start);
//         queue.offer(start);
//         // 给定开始节点的参数
//         Map<String, JSONObject> map = new HashMap<>();
//         map.put(SettingConstants.START, input);
//         List<Map<String, JSONObject>> list = new ArrayList<>();
//         while (!queue.isEmpty()) {
//             AbstractComponentNode node = queue.poll();
//             log.info("当前节点：{} {}", node.nodeId, node.nodeName);
//             List<AbstractComponentNode> nodes = adjacencyList.get(node);
//             // 执行节点功能
// //            JSONObject result = node.process(map);
// //            JSONObject result = node.process(new VariablePool(null, null, null, null));
// //            switch (node.nodeType) {
// //                case EXCLUSIVE:
// //                    Integer index = (Integer) result.get(SettingConstants.INDEX);
// //                    AbstractComponentNode next = nodes.get(index);
// //                    // 只保留排他节点执行后的结果
// //                    nodes.removeIf(n -> n != next);
// //                    break;
// //                case END:
// //                    log.info("执行结果：{}", result);
// //                    Map<String, JSONObject> r = new HashMap<>();
// //                    r.put(node.getNodeName(), result);
// //                    list.add(r);
// //                    return list;
// //            }
//
//             for (AbstractComponentNode neighbor : nodes) {
//                 if (!visited.contains(neighbor)) {
//                     visited.add(neighbor);
//                     queue.offer(neighbor);
//                 }
//             }
//         }
//         return list;
//     }
//
//     @Override
//     public List<Map<String, JSONObject>> dfs() {
//         Set<AbstractComponentNode> visited = new HashSet<>();
//
//         AbstractComponentNode start = adjacencyList.keySet().stream()
//                 .filter(node -> Objects.equals(node.getNodeType(), ComponentNodeEnum.START.getCode())).findFirst()
//                 .orElseThrow(() -> new RuntimeException("未找到开始节点"));
//         // 给定开始节点的参数
//         Map<String, JSONObject> map = new HashMap<>();
//         map.put(SettingConstants.START, input);
//         List<Map<String, JSONObject>> result = new ArrayList<>();
//         dfsHelper(start, visited, map, result);
//         return result;
//     }
//
//
//     private void dfsHelper(AbstractComponentNode node, Set<AbstractComponentNode> visited, Map<String, JSONObject> map, List<Map<String, JSONObject>> list) {
//         if (visited.contains(node)) {
//             return;
//         }
//         visited.add(node);
//         log.info("当前节点：{} {}", node.nodeId, node.nodeName);
//         List<AbstractComponentNode> nodes = adjacencyList.get(node);
//         // 执行节点功能
// //        JSONObject result = node.process(map);
// //        JSONObject result = node.process(new VariablePool(null, null, null, null));
// //        switch (node.nodeType) {
// //            case EXCLUSIVE:
// //                Integer index = (Integer) result.get(SettingConstants.INDEX);
// //                AbstractComponentNode next = nodes.get(index);
// //                // 只保留排他节点执行后的结果
// //                nodes.removeIf(n -> n != next);
// //                break;
// //            case END:
// //                log.info("执行结果：{}", result);
// //                Map<String, JSONObject> r = new HashMap<>();
// //                r.put(node.getNodeName(), result);
// //                list.add(r);
// //        }
//
//         for (AbstractComponentNode neighbor : nodes) {
//             dfsHelper(neighbor, visited, map, list);
//         }
//     }
//
//
// }
