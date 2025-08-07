package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.KnowledgeInoDataNumResult;
import com.wenge.model.entity.DataSourceParserInfo;
import com.wenge.model.utils.PageInfo;
import com.wenge.model.vo.StructureParserVo;
import com.wg.appframe.core.bean.Result;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Description: 文件服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:36
 *
 */
public interface DataSourceParserInfoService extends IService<DataSourceParserInfo> {

    Result addDataSourceParserInfo(AddDataSourceParserInfoParam param);

    Result getDetail(String id);

    Result<PageInfo<StructureParserVo>> unionData(UnionParam param);

    Result updateSynchStatusUnionData(UnionParam param);

    Result runTask(RunTaskDataSourceParserInfoParam param) throws ClassNotFoundException;

    Result getTableList(String businessId);

    Result getDataSourceDataList(DataSourceTableDataPageParam param) throws SQLException;

    Result del(StrutsDelParam param);

    Result enable(EnableParam param);

    Result strutsList(StrutsResultPageParam param) throws IOException;

    Result updateValidDate(UpdateStructDataValidDateParam param);

    /**
     * 根据数据源id获取数据源信息
     * @param datasourceId
     * @return
     */
    DataSourceParserInfo getDatasourceFroNode(String datasourceId);

    /**
     * 获取每个知识库的数据库文件数量
     * @return
     */
    Map<String, Long> getDataSourceParserInfoNum();
}
