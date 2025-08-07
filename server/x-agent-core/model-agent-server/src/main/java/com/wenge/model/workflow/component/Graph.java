package com.wenge.model.workflow.component;

import cn.hutool.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 有向图
 */
public interface Graph<V> {
    /**
     * 添加节点
     * @param node
     */
    void addNode(V node);

    /**
     * 添加边
     * @param from
     * @param to
     */
    void addEdge(V from, V to);

    /**
     * 删除节点
     * @param node
     */
    void removeNode(V node);

    /**
     * 删除边
     * @param sourceNode
     * @param targetNode
     */
    void removeEdge(V sourceNode, V targetNode);

    void clear();

    boolean isEmpty();

    int size();

    /**
     * 广度优先
     *
     * @return
     */
    List<Map<String, JSONObject>> bfs();

    /**
     * 深度优先
     */
    List<Map<String, JSONObject>> dfs();

}
