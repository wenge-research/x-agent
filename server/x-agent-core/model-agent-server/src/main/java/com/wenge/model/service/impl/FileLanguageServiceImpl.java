package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.FileLanguage;
import com.wenge.model.mapper.FileLanguageMapper;
import com.wenge.model.service.FileLanguageService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.wenge.model.entity.table.FileLanguageTableDef.FILE_LANGUAGE;

@Service
public class FileLanguageServiceImpl extends ServiceImpl<FileLanguageMapper, FileLanguage> implements FileLanguageService {

    @Override
    public List<FileLanguage> getLanguageFile(String fileId) {
        QueryWrapper where = QueryWrapper.create()
                .where(FILE_LANGUAGE.FILE_ID.eq(fileId));
        List<FileLanguage> list = list(where);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list;
    }
}
