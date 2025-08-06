package com.wenge.model.vo;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/6/19 17:27
 */

public class ParserUrlContent {
    private String title;
    private String content;

    public ParserUrlContent() {
    }

    public ParserUrlContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}