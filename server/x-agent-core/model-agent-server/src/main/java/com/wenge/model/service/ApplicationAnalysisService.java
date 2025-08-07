package com.wenge.model.service;

import com.wenge.model.dto.param.ApplicationOverviewIndicatorsParam;
import com.wenge.model.dto.param.ApplicationQuestionRecommendParam;
import com.wg.appframe.core.bean.Result;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * @author: caohaifeng
 * @date: 2024/8/12 11:48
 * @Description:
 * @Version:1.0
 **/

public interface ApplicationAnalysisService {
    /**
     * @description: 应用统计word文档
     * @author: yijiazheng
     * @date: 2025/4/30
     **/
    public Result applicationStatistics(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, List<InputStream> file, HttpServletResponse response) throws IOException, InvalidFormatException, ParseException;
    /**
     * @description: 应用概览指标获取
     * @author: caohaifeng
     * @date: 2024/8/12 14:03
     **/
    Result applicationOverviewIndicators(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);

    /**
     * @description: 应用概览指标-某一类详细指标获取-问答质量
     * @author: caohaifeng
     * @date: 2025/1/6 15:25
     **/
    Result getApplicationOverviewQualityByType(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);
    void getApplicationOverviewQualityByTypeExport(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response);

    /**
     * @description: 应用概览指标-某一类详细指标获取
     * @author: caohaifeng
     * @date: 2024/8/12 14:03
     **/
    Result getApplicationOverviewIndicatorsByType(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);


    /**
     * @description: 问题排行榜Top50/topN
     * @author: caohaifeng
     * @date: 2024/8/12 18:15
     **/
    Result questionChartsTop50(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);

    Result questionRealtimeRecommendTopN(ApplicationQuestionRecommendParam param);

    /**
     * @description: 分析应用回复的成功率、失败率、点赞率
     * @author: caohaifeng
     * @date: 2024/8/27 16:21
     * @param:
     * @return:
     **/
    Result getApplicationUsageRate(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);



    /**
     * @description: 应用分析-查阅点赞点踩数排名（部门维度）
     * @author: caohaifeng
     * @date: 2024/9/18 14:04
     **/
    Result getApplicationLikeStepRanking(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);


    /**
     * @description: 应用分析-查阅对话日志审核数排名（部门维度）
     * @author: caohaifeng
     * @date: 2024/9/18 14:04
     **/
    Result getApplicationDialogueReviewRanking(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);


    /**
     * @description: 应用分析-查阅知识库问答QA对添加数排名（部门维度）
     * @author: caohaifeng
     * @date: 2024/9/18 14:04
     **/
    Result getApplicationAddQARanking(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);



    /**
     *  三个导出接口
     **/
    void getApplicationLikeStepRankingExport(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response);
    void getApplicationDialogueReviewRankingExport(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response);
    void getApplicationAddQARankingExport(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam, HttpServletResponse response);



    /**
     * @description: 应用分析-查阅共计点踩数、推送公开知识库数、推送私有知识库数
     * 查阅共计审核数（系统审核除外）、其中修正后的审核且推送公开知识库数、推送私有知识库数
     * @author: caohaifeng
     * @date: 2024/9/18 15:06
     **/
    Result getApplicationLikesAndReviewsCount(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);


    Result getApplicationOverviewIndicatorsByTypeSuccess(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);

    Result getApplicationOverviewIndicatorsBySelect(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);

    /**
     * 应用分析-应用分析-查阅详细指标-用户活跃度
     * @param applicationOverviewIndicatorsParam
     * @return
     */
    Result getApplicationActiveUser(ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam);
}
