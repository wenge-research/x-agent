package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.MemoryParam;
import com.wenge.model.workflow.entity.Memory;

import java.util.List;
import java.util.Map;

public interface MemoryService {

    /* 查询接口 */
    Map<String, List<String>> selectMemoryList(Memory memory);

    /* 保存接口 */
    void saveBatch(List<Memory> data);

    /* 分页查询记忆 */
    Page<Memory> page(MemoryParam memory);

    /* 根据id修改记忆 */
    Memory update(Memory memory);
}
