package com.wg.appframe.elasticsearch.service;


import com.wg.appframe.elasticsearch.dto.EsDocumentVo;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EsRestHighLevelClientService
 * @Package: com.springboot.cloud.elasticsearch.service
 * @Description:
 * @Author: ztf
 * @Date: 2021/5/1816:23
 * @Version: 1.0
 */
public interface EsRestHighLevelClientService {

    /**
     * @param index      索引
     * @param pageNo     页码
     * @param pageSize   页内容大小
     * @param keyword    关键词
     * @param fieldList  匹配字段名
     * @param orderField 排序字段
     * @param orderBy    排序  ASC|DESC
     * @Description: 分页查询_分词查询
     * @Return: org.elasticsearch.action.search.SearchResponse
     * @Author: wsj
     * @Date: 2021/9/15 18:15
     */
    SearchResponse searchByMatchQuery(String index, Integer pageNo, Integer pageSize, String keyword, List<String> fieldList, String orderField, String orderBy) throws Exception;

    /**
     * @param index      索引
     * @param pageNo     页码
     * @param pageSize   页内容大小
     * @param keyword    关键词
     * @param fieldList  匹配字段名
     * @param orderField 排序字段
     * @param orderBy    排序  ASC|DESC
     * @Description: 分页查询_完全匹配查询
     * @Return: org.elasticsearch.action.search.SearchResponse
     * @Author: wsj
     * @Date: 2021/9/22 13:55
     */
    SearchResponse searchByTermQuery(String index, Integer pageNo, Integer pageSize, String keyword, List<String> fieldList, String orderField, String orderBy) throws Exception;

    SearchResponse searchByTermQueryMust(String index, Integer pageNo, Integer pageSize, Map<String,String> conditionMap, String orderField, String orderBy) throws Exception;

    /**
     * 批量保存数据
     * @param index 索引名称
     * @param documentVoList 文档内容
     * @return 返回保存数据结果
     */
    boolean saveBatch(String index, List<EsDocumentVo> documentVoList) throws Exception;

    /**
     * 判断是否存在索引名
     * @param index 传入索引名
     * @return
     * @throws IOException
     */
    boolean indexExits(String index) throws IOException;

    /**
     * 通过id更新数据
     * @param object 数据实体
     * @param index 索引名
     * @param id es的id
     * @param isRealTime
     * @return
     * @throws IOException
     */
    UpdateResponse updateDataById(Object object, String index, String id, Boolean isRealTime) throws IOException;

    /**
     * 添加单条数据
     * @param object 数据实体
     * @param index 索引名
     * @return
     * @throws IOException
     */
    IndexResponse addData(Object object, String index) throws IOException;

    /**
     * 添加单条数据，指定id
     *
     * @param object 数据实体
     * @param index  索引名
     * @param id     索引id
     * @return
     */
    IndexResponse addData(Object object, String index, String id) throws IOException;

    SearchResponse query(SearchRequest request ) throws Exception;

    boolean clearEsCache(String indexName);

    SearchResponse getDataByScroll(String scrollId) throws IOException;

    void cleanScroll(String scrollId) throws IOException;

    SearchResponse queryStatistical(SearchRequest request ) throws Exception;


}
