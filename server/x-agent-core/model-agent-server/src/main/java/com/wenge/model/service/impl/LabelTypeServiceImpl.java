package com.wenge.model.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.LabelType;
import com.wenge.model.mapper.LabelTypeMapper;
import com.wenge.model.service.LabelTypeService;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.LabelTypeTableDef.LABEL_TYPE;

@Service
@Slf4j
public class LabelTypeServiceImpl extends ServiceImpl<LabelTypeMapper, LabelType> implements LabelTypeService {

    @Override
    public List<LabelType> getLabelTypes(Integer type) {
        Wrappers wrappers = Wrappers.init()
                .where(LABEL_TYPE.STATUS.eq(0))
                .and(LABEL_TYPE.TYPE.eq(type))
                .orderBy(LABEL_TYPE.CREATE_TIME.asc());
        return list(wrappers);
    }
}