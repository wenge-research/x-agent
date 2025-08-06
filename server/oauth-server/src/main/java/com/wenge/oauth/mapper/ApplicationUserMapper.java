package com.wenge.oauth.mapper;


import com.mybatisflex.core.BaseMapper;
import com.wenge.oauth.entity.ApplicationUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Description: 应用用户关联表数据库处理类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-12 13:41:05
 *
 */
@Mapper
public interface ApplicationUserMapper extends BaseMapper<ApplicationUser> {

    /**
     * 获取应用今日用户数量
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("<script> select count(1) as total from oauth_user oa inner join application_user au on oa.id = au.user_id" +
            "<where> " +
            " <if test=\"applicationId != null and applicationId != '' \">\n" +
            "                        and au.application_id = #{applicationId}\n" +
            " </if>" +
            " <if test=\"applicationIds != null and applicationIds.size() > 0\">\n" +
            "                        AND au.application_id IN\n" +
            "                        <foreach collection=\"applicationIds\" item=\"id\" open=\"(\" separator=\",\" close=\")\"> #{id}\n" +
            "                        </foreach>\n" +
            "</if>" +
            " <if test=\"startTime != null and startTime != ''\">  \n" +
            "        and oa.create_time >= #{startTime}  \n" +
            " </if> " +
            " <if test=\"endTime != null and endTime != ''\">  \n" +
            "        and oa.create_time &lt;= #{endTime}  \n" +
            " </if> </where>" +
            "</script>  ")
    long getApplicationUserCount(@Param("startTime") String startTime, @Param("endTime") String endTime,@Param("applicationId") String applicationId,
                                 @Param("applicationIds") List<String> applicationIds);

    /**
     * @description:
     * @author: caohaifeng
     * @date: 2024/8/15 13:52
     * @return: long
     **/
    @Select("select count(distinct (user_id)) as total from  application_user au where au.application_id = #{applicationId}  ")
    long getApplicationBindUserCount(@Param("applicationId") String applicationId);

}