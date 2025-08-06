package com.wenge.model.workflow.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class JsonUtil {

    /**
     * json转markdown
     * @param json
     * @param markdown
     * @param indentLevel
     * @return
     */
    public static String jsonToMarkdown(Object json, StringBuilder markdown, int indentLevel) {
        try {
            json = JSONUtil.parse(json);
        } catch (Exception ignored) {
        }
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                markdown.append(generateMarkdownHeader(indentLevel + 1)).append(" ").append(key).append("\n\n");
                jsonToMarkdown(value, markdown, indentLevel + 1);
            }
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            for (int i = 0; i < jsonArray.size(); i++) {
                Object value = jsonArray.get(i);
                jsonToMarkdown(value, markdown, indentLevel);
            }
        } else {
            markdown.append(json.toString()).append("\n\n");
        }
        return markdown.toString();
    }

    private static String generateMarkdownHeader(int level) {
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < level; i++) {
            header.append("#");
        }
        return header.toString();
    }

}
