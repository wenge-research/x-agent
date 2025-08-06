package com.wenge.model.strategy.matterGuide;

import com.alibaba.fastjson2.JSONArray;
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
public class MedicalInsuranceImpl extends BaseAbstract implements MatterGuideStrategy<MedicalInsuranceImpl.InnerFormInfoVo>{

    private String type;

    private List<MatterGuideFiled> guideFileds;

    @Override
    public InnerFormInfoVo getFormInfo() {
        InnerFormInfoVo innerFormInfoVo = new InnerFormInfoVo();
        List<FormItem> forms = new ArrayList<>();
        List<JSONObject> configs = new ArrayList<>();
        FormItem formItem = new FormItem();
        JSONObject params = new JSONObject();
        for (MatterGuideFiled guideFiled : guideFileds) {
            if("title".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setTitle(guideFiled.getTip());
            }else if("message".equals(guideFiled.getFiledCode())){
                innerFormInfoVo.setMessage(guideFiled.getTip());
            } else if("basicInfo".equals(guideFiled.getFiledCode())){
                formItem.setBasicInfo(guideFiled.getTip());
            }else if("peopleName".equals(guideFiled.getFiledCode()) || "idCard".equals(guideFiled.getFiledCode())
                    || "phone".equals(guideFiled.getFiledCode()) || "idCardFrontPic".equals(guideFiled.getFiledCode())
                    || "idCardBackPic".equals(guideFiled.getFiledCode())) {
                params.put(guideFiled.getFiledCode(),"");
                configs.add(buildConfigItem(guideFiled));
            }
        }

        configs.add(buildConfigItem2());

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
        private List<JSONObject> configs;
    }

    private JSONObject buildConfigItem2(){
        JSONObject res = new JSONObject();
        List<MatterGuideFiled> medicalTypeList = this.guideFileds.stream()
                .filter(ele -> "medicalType".equals(ele.getFiledCode())).collect(Collectors.toList());
        JSONArray options = new JSONArray();
        for (MatterGuideFiled guideFiled : medicalTypeList) {
            if("radio".equals(guideFiled.getFormType())){
                res.put("filedId",guideFiled.getFiledId());
                res.put("label",guideFiled.getFiledName());
                res.put("type",guideFiled.getFormType());
                res.put("filed",guideFiled.getFiledCode());
                res.put("placeholder",guideFiled.getTip());
            }

            if("options".equals(guideFiled.getFormType())){
                JSONObject option = new JSONObject();
                option.put("id",guideFiled.getId());
                option.put("label",guideFiled.getTip());
                options.add(option);
            }
        }

        List<MatterGuideFiled> medicalInputsList = this.guideFileds.stream()
                .filter(ele -> "medicalInputs".equals(ele.getFiledCodeGroup())).collect(Collectors.toList());
        JSONObject medical = new JSONObject();
        JSONArray inputs = new JSONArray();
        for (MatterGuideFiled guideFiled : medicalInputsList) {
            if("medicalTitle".equals(guideFiled.getFiledCode())){
                medical.put("mark",guideFiled.getTip());
                medical.put("num",medicalInputsList.size());
                medical.put("field",guideFiled.getFiledCode());
            }
            if("medicalInputs".equals(guideFiled.getFiledCode())){
                JSONObject input = new JSONObject();
                input.put("id",guideFiled.getId());
                input.put("type",guideFiled.getFormType());
                input.put("placeholder",guideFiled.getTip());
                inputs.add(input);
            }
        }
        medical.put("medicalOptions",inputs);

        res.put("options",options);
        res.put("medical",medical);
        return res;
    }

    private JSONObject buildConfigItem(MatterGuideFiled guideFiled){
        JSONObject res = new JSONObject();
        res.put("filedId",guideFiled.getFiledId());
        res.put("label",guideFiled.getFiledName());
        res.put("type",guideFiled.getFormType());
        res.put("filed",guideFiled.getFiledCode());
        res.put("placeholder",guideFiled.getTip());
        return res;
    }
}