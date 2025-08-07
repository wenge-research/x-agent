package com.wenge.model.strategy.matterGuide;

import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.entity.MatterGuideFiled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 18:06
 */

@AllArgsConstructor
public class PeopleProfile2Impl extends BaseAbstract implements MatterGuideStrategy<PeopleProfile2Impl.InnerFormInfoVo>{

    private String type;

    private List<MatterGuideFiled> guideFileds;

    @Override
    public InnerFormInfoVo getFormInfo() {
        InnerFormInfoVo innerFormInfoVo = new InnerFormInfoVo();

        List<MatterGuideFiled> opList = new ArrayList<>();
        // 构建表头
        for (MatterGuideFiled guideFiled : guideFileds) {
            if("title".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setTitle(guideFiled.getTip());
            }else if("message".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setMessage(guideFiled.getTip());
            }else {
                opList.add(guideFiled);
            }
        }

        List<MatterGuideFiled> formList = opList.stream().filter(guideFiled -> "form".equals(guideFiled.getFormType()))
                .collect(Collectors.toList());
        List<FormItem> forms = new ArrayList<>(formList.size());

        for (MatterGuideFiled formGuideFiled : formList) {
            // 构建forms
            List<ConfigItem> configs = new ArrayList<>();
            FormItem formItem = new FormItem();
            JSONObject params = new JSONObject();
            for (MatterGuideFiled guideFiled : opList) {

                // 表单开始,构建表头
                if(formGuideFiled.getFiledId().equals(guideFiled.getFiledId())){
                    formItem.setBasicInfo(guideFiled.getTip());
                    continue;
                }

                // 构建configs
                configs.add(buildConfigItem(guideFiled));
                params.put(guideFiled.getFiledCode(),"");
            }
            formItem.setConfigs(configs);
            forms.add(formItem);
            innerFormInfoVo.setParams(params);
        }
        innerFormInfoVo.setForms(forms);
        return innerFormInfoVo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class InnerFormInfoVo implements Serializable {
        private String title;
        private String message;
        private List<FormItem> forms;
        private JSONObject params;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class FormItem{
        private String basicInfo;
        private List<ConfigItem> configs;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class ConfigItem{
        private String filedId;
        private String label;
        private String type;
        private String field;
        private String placeholder;
        private List<Option> options;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Option{
        private String filedId;
        private String id;
        private String label;
    }

    private ConfigItem buildConfigItem(MatterGuideFiled guideFiled) {
        ConfigItem configItem = new ConfigItem();
        configItem.setFiledId(guideFiled.getFiledId());
        configItem.setLabel(guideFiled.getFiledName());
        configItem.setType(guideFiled.getFormType());
        configItem.setField(guideFiled.getFiledCode());
        configItem.setPlaceholder(guideFiled.getTip());
        if("radio".equals(guideFiled.getFormType())){


        }
        return configItem;
    }
}