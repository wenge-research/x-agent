package com.wenge.model.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.KnowledgeApiRequestRecordPageQueryParam;
import com.wenge.model.entity.KnowledgeApiRequestRecord;
import com.wenge.model.mapper.KnowledgeApiRequestRecordMapper;
import com.wenge.model.service.KnowledgeApiRequestRecordService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.wenge.model.entity.table.KnowledgeApiRequestRecordTableDef.KNOWLEDGE_API_REQUEST_RECORD;

@Service
@Slf4j
public class KnowledgeApiRequestRecordServiceImpl extends ServiceImpl<KnowledgeApiRequestRecordMapper, KnowledgeApiRequestRecord>
        implements KnowledgeApiRequestRecordService {

    @Autowired
    private KnowledgeApiRequestRecordMapper knowledgeApiRequestRecordMapper;


    @Override
    public Result<Page<KnowledgeApiRequestRecord>> pageQuery(KnowledgeApiRequestRecordPageQueryParam param) {
        if (StringUtils.isBlank(param.getKnowledgeApiId())) {
            throw new IllegalArgumentException("api业务id为空");
        }

        Wrappers<Object> queryWrapper = Wrappers.init();
        queryWrapper.and(KNOWLEDGE_API_REQUEST_RECORD.KNOWLEDGE_API_ID.eq(param.getKnowledgeApiId()));
        if (StringUtils.isNotBlank(param.getStartTime()) && StringUtils.isNotBlank(param.getEndTime())) {
            queryWrapper.and(KNOWLEDGE_API_REQUEST_RECORD.CREATE_TIME.between(param.getStartTime(), param.getEndTime()))
                    .orderBy(KNOWLEDGE_API_REQUEST_RECORD.CREATE_TIME.desc());
        }
        Page<KnowledgeApiRequestRecord> page = page(Page.of(param.getPageNo(), param.getPageSize()), queryWrapper);

        return Result.success(page);
    }

    @Override
    public Result saveRecord(KnowledgeApiRequestRecord knowledgeApiRequestRecord) {
        return null;
    }
}