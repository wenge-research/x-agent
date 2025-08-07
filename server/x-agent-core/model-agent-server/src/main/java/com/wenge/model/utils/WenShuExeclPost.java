package com.wenge.model.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class WenShuExeclPost {

    private static RestTemplate restTemplate;


    private static String add_table_url;

    private static String get_answer_url;

    @Autowired
    WenShuExeclPost(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Value("${wenShu.get_answer_url}")
    public void setWenShuGetAnswerUrl(String get_answer_url) {
        this.get_answer_url = get_answer_url;
    }

    @Value("${wenShu.add_table_url}")
    public void setWenShuAddTableUrl(String add_table_url) {
        this.add_table_url = add_table_url;
    }


    public static JSONObject addExcel(String file_url, String table_name, String knowledgeId) {
        //表名横写替换为下划线
        table_name = table_name.replaceAll("-", "_");
//        RestTemplate restTemplate = new RestTemplate();
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("file_url", "https://city.wengegroup.com/smart-agent-api-demo/wos/file/download/1730190230774.xlsx");
//        jsonObject.put("table_name", "b1298d20_f31b_4e90_b881_fb3008e2545b_test");
//        jsonObject.put("knowledgeId", "a1bf49ac934c446e84fe62a53202d8c2");
        jsonObject.put("file_url", file_url);
        jsonObject.put("table_name", table_name);
        jsonObject.put("knowledgeId", knowledgeId);
        log.info(jsonObject.toJSONString());
        JSONObject result = restTemplate.postForObject(
                add_table_url,
                jsonObject,
                JSONObject.class);
//        log.info(result.toJSONString());
        return result;
    }


    public static JSONObject getAnswerByExecl(String query, List<String> table_names, List<String> knowledge_ids) {
        //表名横写替换为下划线
        if (table_names != null && table_names.size() > 0) {
            for (int i = 0; i < table_names.size(); i++) {
                table_names.set(i, table_names.get(i).replaceAll("-", "_"));
            }
        }
//        RestTemplate restTemplate = new RestTemplate();
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("query", "工业企业如何选择合适的贸易模式？");
//        table_names = new String[]{"667ffe62_680c_47e3_98b5_fdd1dfb68e5f"};
//        jsonObject.put("table_names", table_names);
//        knowledge_ids = new String[]{"a1bf49ac934c446e84fe62a53202d8c2"};
//        jsonObject.put("knowledge_ids", knowledge_ids);
        jsonObject.put("query", query);
        jsonObject.put("table_names", table_names);
        jsonObject.put("knowledge_ids", knowledge_ids);
        log.info(jsonObject.toJSONString());
        JSONObject result = restTemplate.postForObject(
                get_answer_url,
                jsonObject,
                JSONObject.class);
        log.info(result.toJSONString());
        return result;
    }

    public static void main(String[] args) {
        getAnswerByExecl(null, null, null);
    }


}