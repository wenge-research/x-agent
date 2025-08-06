package com.wenge.oauth.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.OauthLoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * Description: 数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 15:48:49
 *
 */
@Mapper
public interface OauthLoginLogMapper extends BaseMapper<OauthLoginLog> {

    @Select("select * from oauth_user where phone = #{phone} and status = 1 order by create_time desc limit 1")
    Map<String, Object> getOauthUserByPhone(@Param("phone") String phone);

    @Select("select id,application_id,application_name,application_code from application_info where application_id = #{applicationId} limit 1")
    Map<String, Object> getApplicationInfoByAppId(@Param("applicationId") String applicationId);

    @Select("select * from oauth_user where id = #{id} and status = 1")
    Map<String, Object> getOauthUserById(@Param("id") Integer id);

}