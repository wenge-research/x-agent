package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.LabelParam;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.entity.Label;
import com.wenge.model.mapper.LabelMapper;
import com.wenge.model.service.LabelService;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.ContextHolders;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.LabelTableDef.LABEL;

@Service
@Slf4j
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label> implements LabelService {

    public void add(Label label) {

        // 判断标签是否存在，不存在则新增
        List<Label> labelList = this.getLabels(label);
        TokenUser tokenUserInfo = ContextHolders.getTokenUserInfo();
        String now = DateUtil.now();
        if (labelList == null || labelList.isEmpty()) {
            Label newLabel = new Label();
            newLabel.setLabelId(IdUtil.simpleUUID());
            newLabel.setLabelId(IdUtil.simpleUUID());
            newLabel.setLabelType(label.getLabelType());
            newLabel.setLabelName(label.getLabelName());
            newLabel.setCreateUser(tokenUserInfo.getAccountName());
            newLabel.setUpdateUser(tokenUserInfo.getAccountName());
            newLabel.setCreateTime(now);
            newLabel.setUpdateTime(now);
            this.save(newLabel);
        }
    }

    public void batchAdd(List<Label> labels, String labelType) {
        if (labels == null || labels.isEmpty()) {
            return;
        }
        // 提取所有待添加的标签名
        Set<String> labelNames = labels.stream()
                .map(Label::getLabelName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .collect(Collectors.toCollection(HashSet::new));

        // 查询数据库中已存在的同类型标签
        List<Label> existingLabels = this.getLabels(labelType, new ArrayList<>(labelNames));

        // 提取已存在的标签名
        Set<String> existingNames = Optional.ofNullable(existingLabels).orElse(Collections.emptyList()).stream()
                .map(Label::getLabelName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        TokenUser tokenUserInfo = ContextHolders.getTokenUserInfo();
        // 找出需要新增的标签名
        String now = DateUtil.now();
        List<Label> labelsToCreate = labelNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> {
                    Label label = new Label();
                    label.setLabelId(IdUtil.simpleUUID());
                    label.setLabelType(labelType);
                    label.setLabelName(name);
                    label.setCreateUser(tokenUserInfo.getAccountName());
                    label.setUpdateUser(tokenUserInfo.getAccountName());
                    label.setCreateTime(now);
                    label.setUpdateTime(now);
                    return label;
                })
                .collect(Collectors.toList());
        // 批量新增不存在的标签
        if (!labelsToCreate.isEmpty()) {
            this.saveBatch(labelsToCreate);
        }
    }

    @Override
    public List<Label> getLabels(String labelType, List<String> labelNames) {
        return Wrappers.of(mapper)
                .where(StringUtils.isNotBlank(labelType), LABEL.LABEL_TYPE.eq(labelType))
                .and(CollectionUtil.isNotEmpty(labelNames), LABEL.LABEL_NAME.in(labelNames))
                .list();
    }

    @Override
    public List<Label> getLabels(Label label) {
        return Wrappers.of(mapper)
                .where(StringUtils.isNotBlank(label.getLabelType()), LABEL.LABEL_TYPE.eq(label.getLabelType()))
                .and(StringUtils.isNotBlank(label.getLabelName()), LABEL.LABEL_NAME.eq(label.getLabelName()))
                .list();
    }



    @Override
    public Page<Label> getLabelsByWord(LabelParam label) {
        Wrappers<Object> wrapper =  Wrappers.init()
                .where(StringUtils.isNotBlank(label.getLabelType()),
                        LABEL.LABEL_TYPE.eq(label.getLabelType()))
                .and(StringUtils.isNotBlank(label.getLabelName()),
                        LABEL.LABEL_NAME.like(label.getLabelName()));
        return page(Page.of(label.getPageNo(), label.getPageSize()), wrapper);
    }
}
