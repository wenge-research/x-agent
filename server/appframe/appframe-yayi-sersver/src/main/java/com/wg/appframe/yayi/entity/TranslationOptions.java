package com.wg.appframe.yayi.entity;

import lombok.Data;

import java.util.List;

@Data
public class TranslationOptions {
    /**
     * 源语言的英文全称
     */
    private String source_lang;
    /**
     * 目标语言的英文全称
     */
    private String targer_lang;

    private List<Unit> terms;
    private List<Unit> tm_list;
    private String domains;

    @Data
    public static class Unit {

        /**
         * 源语言的术语。
         */
        private String source;
        /**
         * 目标语言的术语。
         */
        private String target;
    }

}

