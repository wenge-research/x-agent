package com.wenge.model.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.BusinessScenarioList;
import com.wenge.model.mapper.BusinessScenarioListMapper;
import com.wenge.model.service.BusinessScenarioListService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import static com.wenge.model.entity.table.VirtualHumanComponentInfoTableDef.VIRTUAL_HUMAN_COMPONENT_INFO;

@Slf4j
@Service
public class BusinessScenarioListServiceImpl extends ServiceImpl<BusinessScenarioListMapper,
		BusinessScenarioList> implements BusinessScenarioListService {



}