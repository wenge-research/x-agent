package com.wenge.model.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.MemoryParam;
import com.wenge.model.mapper.MemoryMapper;
import com.wenge.model.service.MemoryService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.workflow.entity.Memory;
import com.wg.appframe.mybatisflex.core.Wrappers;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.workflow.entity.table.MemoryTableDef.MEMORY;

@Service
public class MemoryServiceImpl extends ServiceImpl<MemoryMapper, Memory> implements MemoryService {

    @Override
    public Map<String, List<String>> selectMemoryList(Memory memory) {
        // todo 考虑采用redis做缓存优化查询性能
        Wrappers<Memory> wrapper = Wrappers.init();
        wrapper.where(StrUtil.isNotBlank(memory.getApplicationId()), MEMORY.APPLICATION_ID.eq(memory.getApplicationId()))
                .and(StrUtil.isNotBlank(memory.getDialogueId()), MEMORY.DIALOGUE_ID.eq(memory.getDialogueId()))
                .and(StrUtil.isNotBlank(memory.getSubject()), MEMORY.SUBJECT.eq(memory.getSubject()))
                .and(StrUtil.isNotBlank(memory.getUserId()), MEMORY.USER_ID.eq(memory.getUserId()));
        List<Memory> memories = list(wrapper);
        return memories.stream().collect(
                Collectors.groupingBy(Memory::getSubject, Collectors.mapping(Memory::getContent, Collectors.toList())));
    }

    @Override
    public void saveBatch(List<Memory> data) {
        String createTime = DateUtil.format(new Date(), DateUtil.PATTERN_1);
        List<Memory> memories = data.stream().peek(memory -> memory.setCreateTime(createTime)).collect(Collectors.toList());
        super.saveBatch(memories);
    }

    @Override
    public Page<Memory> page(MemoryParam memory) {
        Wrappers<Memory> wrapper = Wrappers.init();
        wrapper.where(StrUtil.isNotBlank(memory.getApplicationId()), MEMORY.APPLICATION_ID.eq(memory.getApplicationId()))
                .and(StrUtil.isNotBlank(memory.getDialogueId()), MEMORY.DIALOGUE_ID.eq(memory.getDialogueId()))
                .and(StrUtil.isNotBlank(memory.getSubject()), MEMORY.SUBJECT.eq(memory.getSubject()))
                .and(StrUtil.isNotBlank(memory.getUserId()), MEMORY.USER_ID.eq(memory.getUserId()));
        return page(new Page<>(memory.getPageNo(), memory.getPageSize()), wrapper);
    }

    @Override
    public Memory update(Memory memory) {
        Wrappers<Memory> wrapper = Wrappers.init();
        wrapper.where(MEMORY.ID.eq(memory.getId()));
        boolean update = update(memory, wrapper);
        return update ? memory : null;
    }
}
