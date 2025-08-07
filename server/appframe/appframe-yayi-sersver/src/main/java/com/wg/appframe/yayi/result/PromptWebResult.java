package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PromptWebResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = -4745842948490536603L;


    private Data data;
    private List<ResInfoDetail> resInfoDetailList;

    @lombok.Data
    public static class Data implements Serializable {

        private static final long serialVersionUID = -3369375366578264386L;

        private String res;

        private List<ResInfoDetail> resInfo;
    }


    @lombok.Data
    public static class ResInfoDetail implements Serializable {

        private static final long serialVersionUID = 2044042651923982972L;

        private String context;
        private String abstracts;
        private String description;
        private String title;
        private String pubtime;
        private String url;
        private String index;
    }
}
