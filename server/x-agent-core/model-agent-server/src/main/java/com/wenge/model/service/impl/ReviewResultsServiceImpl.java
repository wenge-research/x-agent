package com.wenge.model.service.impl;

import com.wenge.model.dto.param.ReviewResultsParam;
import com.wenge.model.entity.ReviewResults;
import com.wenge.model.entity.table.ApplicationInfoTableDef;
import com.wenge.model.mapper.ReviewResultsMapper;
import com.wenge.model.service.ReviewResultsService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewResultsServiceImpl implements ReviewResultsService {
    @Autowired
    private ReviewResultsMapper reviewResultsMapper;

    @Override
    public Result batchSave(ReviewResultsParam reviewResults) {
        if (reviewResults == null) {
            throw new RuntimeException("参数不能为空");
        }
        List<ReviewResults> results = reviewResults.getTerms().stream().map(term -> {
            ReviewResults result = new ReviewResults();
            result.setApplicationId(reviewResults.getApplicationId());
            result.setCreateTime(reviewResults.getCreateTime());
            result.setBiddingFileUrl(reviewResults.getBiddingFileUrl());
            result.setBidFileUrl(reviewResults.getBidFileUrl());
            result.setId(reviewResults.getId());
            result.setTerm(term.getTerm());
            result.setResult(term.getResult());
            return result;
        }).collect(Collectors.toList());
        reviewResultsMapper.insertBatch(results);
        return Result.success();
    }

    @Override
    public Result getList(ReviewResults reviewResults) {
        Wrappers wrappers = Wrappers.init()
                .where(ApplicationInfoTableDef.APPLICATION_INFO.APPLICATION_ID.eq(reviewResults.getApplicationId()));
        return Result.success(reviewResultsMapper.selectListByQuery(wrappers));
    }

    @Override
    public Result deleteByIds(List<String> idList) {
        reviewResultsMapper.deleteBatchByIds(idList);
        return Result.success();
    }

    @Override
    @Transactional
    public Result update(ReviewResultsParam reviewResults) {
        String applicationId = reviewResults.getApplicationId();
        reviewResultsMapper.deleteByQuery(Wrappers.init().where(ApplicationInfoTableDef.APPLICATION_INFO.APPLICATION_ID.eq(applicationId)));

        batchSave(reviewResults);
        return Result.success();
    }
}
