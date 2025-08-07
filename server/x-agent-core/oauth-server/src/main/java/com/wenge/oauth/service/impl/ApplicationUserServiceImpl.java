package com.wenge.oauth.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.oauth.entity.ApplicationUser;
import com.wenge.oauth.mapper.ApplicationUserMapper;
import com.wenge.oauth.service.ApplicationUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.wenge.oauth.entity.table.ApplicationUserTableDef.APPLICATION_USER;

/**
 * Description: 应用用户关联表服务实现类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-12 13:41:05
 *
 */
@Service
@Slf4j
public class ApplicationUserServiceImpl extends ServiceImpl<ApplicationUserMapper, ApplicationUser> implements ApplicationUserService {
	/**
	 * 	应用用户关联表数据库处理类
	 */
	@Autowired
	private ApplicationUserMapper applicationUserMapper;

	@Override
	public void addApplicationUser(ApplicationUser applicationUser){
		if (StringUtils.isBlank(applicationUser.getApplicationId())) {
			return;
		}
		boolean exists = queryChain()
				.where(APPLICATION_USER.APPLICATION_ID.eq(applicationUser.getApplicationId()))
				.and(APPLICATION_USER.USER_ID.eq(applicationUser.getUserId()))
				.exists();
		if (exists){
			return;
		}
		save(applicationUser);
	}

}