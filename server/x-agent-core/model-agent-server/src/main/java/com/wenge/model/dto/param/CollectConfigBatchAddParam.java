package com.wenge.model.dto.param;

import lombok.Data;

import java.util.List;

@Data
public class CollectConfigBatchAddParam {

    List<CollectConfigAddParam> collectConfigAddParamList;
}
