package com.wenge.model.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.TableDirectoryDataChooseFieldParam;
import com.wenge.model.dto.param.TableDirectoryDataPageParam;
import com.wenge.model.dto.param.TableDirectoryInfoPageParam;
import com.wenge.model.entity.TableDirectoryInfo;
import com.wenge.model.mapper.TableDirectoryInfoMapper;
import com.wenge.model.service.TableDirectoryInfoService;
import com.wenge.model.utils.PageInfo;
import com.wenge.model.vo.TableInfoVo;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TableDirectoryInfoServiceImpl extends ServiceImpl<TableDirectoryInfoMapper, TableDirectoryInfo> implements TableDirectoryInfoService {

    @Autowired
    private TableDirectoryInfoMapper mapper;

    @Override
    public Result<Page<TableDirectoryInfo>> getInfoList(TableDirectoryInfoPageParam param) {
        Wrappers wrappers = Wrappers.init();
        Page<TableDirectoryInfo> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrappers);
        return Result.success(page);
    }

    @Override
    public Result getDataList(TableDirectoryDataPageParam param) {
        String jdbcUrl = param.getJdbcUrl();
        String username = param.getJdbcName();
        String password = param.getJdbcPassword();
        String tableName = param.getTableName();
        int pageNumber = param.getPageNo();
        int pageSize = param.getPageSize();
        List<JSONObject> resultData = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            String sql = "SELECT * FROM " + tableName + "  LIMIT ?, ?";
            int offset = (pageNumber - 1) * pageSize;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, offset);
                stmt.setInt(2, pageSize);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String comment = rs.getString("comment");
                        String content = rs.getString("content");
                        JSONObject data = new JSONObject();
                        data.put(comment,content);
                        resultData.add(data);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PageInfo<JSONObject> pageInfo = new PageInfo<>(param.getPageNo(), param.getPageSize(), 100, resultData);
        return Result.success(pageInfo);
    }

    @Override
    public Result chooseField(TableDirectoryDataChooseFieldParam param) {
        String businessId = param.getBusinessId();
        String tableName = param.getTableName();
        QueryWrapper queryWrapper = new QueryWrapper()
                .and(new QueryColumn("business_id").eq(businessId))
                .and(new QueryColumn("table_name").eq(tableName));
        TableDirectoryInfo tableDirectoryInfo = mapper.selectOneByQuery(queryWrapper);
        Map<String, TableInfoVo> tableInfoVoMap = param.getTableInfoVos().stream()
                .collect(Collectors.toMap(TableInfoVo::getColumnName, obj -> obj));
        List<TableInfoVo> tableInfoVos = JSON.parseArray(tableDirectoryInfo.getAllField(), TableInfoVo.class);
        for (TableInfoVo tableInfoVo : tableInfoVos) {
            if(tableInfoVoMap.containsKey(tableInfoVo.getColumnName())){
                tableInfoVo.setFlag(tableInfoVoMap.get(tableInfoVo.getColumnName()).getFlag());
            }
        }
        tableDirectoryInfo.setAllField(JSONObject.toJSONString(tableInfoVos));
        tableDirectoryInfo.setParserFlag(1);
        mapper.update(tableDirectoryInfo);
        return Result.success();
    }
}
