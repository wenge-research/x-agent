package com.wenge.model.strategy.matterGuide;

import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.entity.MatterGuideFiled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/15 18:06
 */

@AllArgsConstructor
public class PeopleProfileImpl extends BaseAbstract implements MatterGuideStrategy<PeopleProfileImpl.InnerFormInfoVo>{

    private String type;

    private List<MatterGuideFiled> guideFileds;

    @Override
    public InnerFormInfoVo getFormInfo() {
        InnerFormInfoVo innerFormInfoVo = new InnerFormInfoVo();
        List<FormItem> forms = new ArrayList<>();
        List<ConfigItem> configs = new ArrayList<>();
        FormItem formItem = new FormItem();
        JSONObject params = new JSONObject();
        for (MatterGuideFiled guideFiled : guideFileds) {
            if("title".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setTitle(guideFiled.getTip());
            }else if("message".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setMessage(guideFiled.getTip());
            } else if("basicInfo".equals(guideFiled.getFiledCode())){
                formItem.setBasicInfo(guideFiled.getTip());
            }else {
                params.put(guideFiled.getFiledCode(),"");
                configs.add(buildConfigItem(guideFiled));
            }
        }
        formItem.setConfigs(configs);
        forms.add(formItem);
        innerFormInfoVo.setForms(forms);
        innerFormInfoVo.setParams(params);
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
    }

    private ConfigItem buildConfigItem(MatterGuideFiled guideFiled){
        ConfigItem configItem = new ConfigItem();
        configItem.setFiledId(guideFiled.getFiledId());
        configItem.setLabel(guideFiled.getFiledName());
        configItem.setType(guideFiled.getFormType());
        configItem.setField(guideFiled.getFiledCode());
        configItem.setPlaceholder(guideFiled.getTip());
        return configItem;
    }
}