package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.ApplicationDatasetParam;
import com.wenge.model.entity.ApplicationDataset;
import com.wg.appframe.core.bean.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * ApplicationEvaluationService接口
 * 
 * @author yjz
 * @date 2025-04-22
 */
public interface ApplicationDatasetService extends IService<ApplicationDataset>
{
    /**
     * 查询ApplicationEvaluation
     * 
     * @param applicationDatasetParam
     * @return ApplicationEvaluation
     */
    public Result<List<ApplicationDataset>> selectApplicationEvaluationById(ApplicationDatasetParam applicationDatasetParam);

    /**
     * 查询ApplicationEvaluation列表
     * 
     * @param applicationDatasetParam ApplicationEvaluation
     * @return ApplicationEvaluation集合
     */
    public Result<Page<ApplicationDataset>> selectApplicationEvaluationList(ApplicationDatasetParam applicationDatasetParam);

    /**
     * 新增ApplicationEvaluation
     * 
     * @param applicationDatasetParam ApplicationEvaluation
     * @return 结果
     */
    public Result<ApplicationDataset> insertApplicationEvaluation(ApplicationDatasetParam applicationDatasetParam);

    /**
     * 修改ApplicationEvaluation
     * 
     * @param applicationDatasetParam ApplicationEvaluation
     * @return 结果
     */
    public Result<ApplicationDataset> updateApplicationEvaluation(ApplicationDatasetParam applicationDatasetParam);
    /**
     * 删除ApplicationEvaluation信息
     * 
     * @param applicationDatasetParam
     * @return 结果
     */
    public Result deleteApplicationEvaluationById(ApplicationDatasetParam applicationDatasetParam);
    Result export(ApplicationDatasetParam applicationDatasetParam, HttpServletResponse response) throws IOException;
    Result importData(MultipartFile file,String datasetId) throws IOException;
}
