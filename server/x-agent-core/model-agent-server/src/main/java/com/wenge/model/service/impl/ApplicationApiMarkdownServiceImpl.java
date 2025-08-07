package com.wenge.model.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.ApplicationApiMarkdown;
import com.wenge.model.mapper.ApplicationApiMarkdownMapper;
import com.wenge.model.service.ApplicationApiMarkdownService;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.wenge.model.entity.table.ApplicationApiMarkdownTableDef.APPLICATION_API_MARKDOWN;

@Service
@Slf4j
public class ApplicationApiMarkdownServiceImpl extends ServiceImpl<ApplicationApiMarkdownMapper, ApplicationApiMarkdown> implements ApplicationApiMarkdownService {

    @Override
    public ApplicationApiMarkdown getApplicationApiMarkdown() {
        Wrappers wrappers = Wrappers.init()
                .where(APPLICATION_API_MARKDOWN.STATUS.eq(0))
                .orderBy(APPLICATION_API_MARKDOWN.CREATE_TIME.asc()).limit(1);
        return getOne(wrappers);
    }
}