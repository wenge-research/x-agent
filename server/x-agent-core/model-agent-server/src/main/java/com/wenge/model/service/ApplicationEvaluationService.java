package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ApplicationDatasetParam;
import com.wenge.model.dto.param.ApplicationEvaluationParam;
import com.wenge.model.entity.ApplicationDataset;
import com.wenge.model.entity.ApplicationEvaluation;
import com.wg.appframe.core.bean.Result;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * ApplicationEvaluationService接口
 * 
 * @author yjz
 * @date 2025-04-22
 */
public interface ApplicationEvaluationService extends IService<ApplicationEvaluation>
{
    /**
     * 查询ApplicationEvaluation
     * 
     * @param applicationEvaluation
     * @return ApplicationEvaluation
     */
    public Result<ApplicationEvaluation> selectApplicationEvaluationById(ApplicationEvaluationParam applicationEvaluation);

    /**
     * 查询ApplicationEvaluation列表
     * 
     * @param applicationEvaluation ApplicationEvaluation
     * @return ApplicationEvaluation集合
     */
    public Result<Page<ApplicationEvaluation>> selectApplicationEvaluationList(ApplicationEvaluationParam applicationEvaluation);

    /**
     * 新增ApplicationEvaluation
     * 
     * @param applicationEvaluation ApplicationEvaluation
     * @return 结果
     */
    public Result<ApplicationEvaluation> insertApplicationEvaluation(ApplicationEvaluationParam applicationEvaluation);

    /**
     * 修改ApplicationEvaluation
     * 
     * @param applicationEvaluation ApplicationEvaluation
     * @return 结果
     */
    public Result<ApplicationEvaluation> updateApplicationEvaluation(ApplicationEvaluationParam applicationEvaluation);
    /**
     * 删除ApplicationEvaluation信息
     * 
     * @param applicationEvaluation
     * @return 结果
     */
    public Result deleteApplicationEvaluationById(ApplicationEvaluationParam applicationEvaluation);
    public Result<List<ApplicationDataset>> answerModel(ApplicationEvaluationParam applicationEvaluation);
    public Result export(ApplicationEvaluationParam applicationDatesetParam, HttpServletResponse response) throws IOException;
    void downloadInterceptWordDataTemp(HttpServletResponse response);
    void downloadInterceptWordDataTemp2(HttpServletResponse response);
}
