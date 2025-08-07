package com.wenge.model.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.google.common.collect.Lists;
import com.wenge.model.constants.StructureConstant;
import com.wenge.model.entity.StructuredOriginalData;
import com.wenge.model.mapper.es.StructuredOriginalDataMapper;
import com.wenge.model.vo.TableInfoVo;
import com.wg.appframe.core.config.CoreContextProvider;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.annotation.rely.RefreshPolicy;
import org.dromara.easyes.common.utils.ExceptionUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.context.ApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ExcelToMySQL {

    // 大写字母
    private static final String BIG_LATTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static List<TableInfoVo> excel2Mysql(String knowledgeId, String tableId, FileInputStream excelStream) {
        ExcelReader reader = ExcelUtil.getReader(excelStream);
        // 第一行为 标题，将会转为字段名
        // 从第二行开始读取
        int row = 1;
        List<List<Object>> read = reader.read();
        // 获取表头
        List<Object> titleList = read.get(0);
        if (CollectionUtils.isEmpty(titleList)) {
            return Lists.newArrayList();
        }

        // 随机生成12位大写字母用来当做字段名
        List<TableInfoVo> allFieldList = titleList.stream().filter(p -> null != p && StringUtils.isNotBlank(p.toString()))
                .map(p -> TableInfoVo.builder().columnName(RandomUtil.randomString(BIG_LATTER, 12)).remarks(p.toString())
                        .flag(StructureConstant.FIELD_UNCHECK).build()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(allFieldList)) {
            return Lists.newArrayList();
        }

        // 如果存在相同字段名，则添加计数
        Map<String, List<TableInfoVo>> columnGroup = allFieldList.stream().collect(Collectors.groupingBy(TableInfoVo::getColumnName));
        columnGroup.forEach((columnName, list) -> {
            if (list.size() > 1) {
                int index = 1;
                for (TableInfoVo tableInfoVo : list) {
                    tableInfoVo.setColumnName(tableInfoVo.getColumnName() + (index++));
                }
            }
        });

        // 读取Excel文件,跳过第一行
        read = read.subList(row, read.size());
        if (CollectionUtils.isEmpty(read)) {
            return Lists.newArrayList();
        }

        ApplicationContext context = CoreContextProvider.getContext();
        StructuredOriginalDataMapper originalDataMapper = context.getBean(StructuredOriginalDataMapper.class);
        RestHighLevelClient restHighLevelClient = context.getBean(RestHighLevelClient.class);

        String time = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.PATTERN_1);
        List<StructuredOriginalData> originalData = read.stream().map(list -> {
            StructuredOriginalData data = new StructuredOriginalData();
            data.setCreateTime(time);
            data.setKnowledgeId(knowledgeId);
            data.setTableId(tableId);
            data.setId(IdUtil.simpleUUID());
            int size = allFieldList.size();

            for (int i = 0; i < size; i++) {
                String value1 = getValue(list, i);
                data.put(allFieldList.get(i).getColumnName(), value1);
            }
            return data;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(originalData)) {
            GetIndexResponse index = originalDataMapper.getIndex();

            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.setRefreshPolicy(RefreshPolicy.NONE.getValue());

            originalData.forEach(detail -> {
                IndexRequest indexRequest = new IndexRequest();
                bulkRequest.add(indexRequest);

                indexRequest.index(index.getIndices()[0]);
                indexRequest.source(detail, XContentType.JSON);
                indexRequest.id(detail.getId());
            });
            try {
                BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (bulkResponse.hasFailures()) {
                    log.error("bulkRequest error:{}", bulkResponse.buildFailureMessage());
                }
            } catch (IOException var8) {
                IOException e = var8;
                throw ExceptionUtils.eee("bulkRequest exception", e, new Object[0]);
            }
        }

        return allFieldList;
    }

    /**
     * 方法描述 汉字转拼音
     * @param:  chinese 汉字
     * @return: 拼音
     * @author: LWQ
     * @date: 2024/7/10
     */
    public static String getPinyin(String chinese) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        for (char ch : chars) {
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (ch > 128) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                    if (pinyinArray != null) {
                        sb.append(pinyinArray[0]);
                    } else {
                        sb.append(ch);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * 从单元格获取数据
     * @param dataList
     * @param index
     * @return
     */
    public static String getValue(List<Object> dataList, Integer index) {
        if (dataList.size() > index && null != dataList.get(index) && StringUtils.isNotBlank(dataList.get(index).toString())) {
            return dataList.get(index).toString();
        }
        return "";
    }
}