package com.wenge.model.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wenge.model.dto.param.UnionParam;
import com.wenge.model.dto.result.KnowledgeInoDataNumResult;
import com.wenge.model.entity.DataSourceParserInfo;
import com.wenge.model.vo.StructureParserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DataSourceParserInfoMapper extends BaseMapper<DataSourceParserInfo> {


    List<StructureParserVo> unionData(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize,
                                      @Param("param") UnionParam param, @Param("foldersIdList") List<String> foldersIdList);

    Integer unionDataCount(@Param("queryDesc") String queryDesc,@Param("param") UnionParam param, @Param("foldersIdList") List<String> foldersIdList);

    List<KnowledgeInoDataNumResult> getDataSourceParserInfoNum();
}