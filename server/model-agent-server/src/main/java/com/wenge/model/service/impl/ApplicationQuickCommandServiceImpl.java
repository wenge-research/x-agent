package com.wenge.model.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.ApplicationQuickCommand;
import com.wenge.model.mapper.ApplicationQuickCommandMapper;
import com.wenge.model.service.ApplicationQuickCommandService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationQuickCommandServiceImpl extends ServiceImpl<ApplicationQuickCommandMapper, ApplicationQuickCommand> implements ApplicationQuickCommandService {

}
