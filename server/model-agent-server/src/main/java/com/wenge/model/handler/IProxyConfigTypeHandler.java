package com.wenge.model.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenge.model.entity.IProxyConfig;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IProxyConfigTypeHandler extends BaseTypeHandler<IProxyConfig> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, IProxyConfig parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = objectMapper.writeValueAsString(parameter);
            ps.setString(i, json);
        } catch (Exception e) {
            throw new SQLException("实体类IProxyConfig转JSON异常", e);
        }
    }

    @Override
    public IProxyConfig getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            String json = rs.getString(columnName);
            return objectMapper.readValue(json, IProxyConfig.class);
        } catch (Exception e) {
            throw new SQLException("JSON转换实体类IProxyConfig异常", e);
        }
    }

    @Override
    public IProxyConfig getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            String json = rs.getString(columnIndex);
            return objectMapper.readValue(json, IProxyConfig.class);
        } catch (Exception e) {
            throw new SQLException("JSON转换实体类IProxyConfig异常", e);
        }
    }

    @Override
    public IProxyConfig getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            String json = cs.getString(columnIndex);
            return objectMapper.readValue(json, IProxyConfig.class);
        } catch (Exception e) {
            throw new SQLException("实体IProxyConfig转换json异常", e);
        }
    }
}
