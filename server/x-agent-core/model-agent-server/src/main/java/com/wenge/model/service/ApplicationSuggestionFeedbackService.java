package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ApplicationOverviewIndicatorsParam;
import com.wenge.model.dto.param.ApplicationSuggestionFeedbackPageParam;
import com.wenge.model.entity.ApplicationSuggestionFeedback;
import com.wg.appframe.core.bean.Result;

import javax.servlet.http.HttpServletResponse;

public interface ApplicationSuggestionFeedbackService extends IService<ApplicationSuggestionFeedback> {
    
    Result add(ApplicationSuggestionFeedback ApplicationSuggestionFeedback);

    Result delete(ApplicationSuggestionFeedback ApplicationSuggestionFeedback);

    Result update(ApplicationSuggestionFeedback ApplicationSuggestionFeedback);

    Result<Page<ApplicationSuggestionFeedback>> getListPage(ApplicationSuggestionFeedbackPageParam param);

    void export(ApplicationSuggestionFeedbackPageParam param, HttpServletResponse response);

}