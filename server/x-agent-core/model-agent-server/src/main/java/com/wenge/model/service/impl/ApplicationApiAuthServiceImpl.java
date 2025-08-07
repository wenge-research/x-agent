package com.wenge.model.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.ApplicationApiAuth;
import com.wenge.model.mapper.ApplicationApiAuthMapper;
import com.wenge.model.service.ApplicationApiAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApplicationApiAuthServiceImpl extends ServiceImpl<ApplicationApiAuthMapper, ApplicationApiAuth> implements ApplicationApiAuthService {


}