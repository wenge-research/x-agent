package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ApplicationDatasetParam;
import com.wenge.model.dto.template.ApplicationDatasetDTO;
import com.wenge.model.entity.ApplicationDataset;
import com.wenge.model.mapper.ApplicationDatasetMapper;
import com.wenge.model.service.ApplicationDatasetService;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.wenge.model.entity.table.ApplicationDatasetTableDef.APPLICATION_DATASET;


/**
 * ApplicationEvaluationService业务层处理
 * 
 * @author yjz
 * @date 2025-04-22
 */
@Service
@Slf4j
public class ApplicationDatasetServiceImpl extends ServiceImpl<ApplicationDatasetMapper, ApplicationDataset> implements ApplicationDatasetService
{
    @Autowired
    private UserService userService;

    @Override
    public Result<List<ApplicationDataset>> selectApplicationEvaluationById(ApplicationDatasetParam applicationDatasetParam) {
        Wrappers<Object> wrappers = Wrappers.init()//makeType
                .and(applicationDatasetParam.getDatasetId()!=null,APPLICATION_DATASET.DATASET_ID.eq(applicationDatasetParam.getDatasetId()));
        List<ApplicationDataset> applicationDatasets = mapper.selectListByQuery(wrappers);
        return Result.success(applicationDatasets);
    }

    /**
     * 查询ApplicationEvaluation列表
     * 
     * @param applicationDatasetParam ApplicationEvaluation
     * @return ApplicationEvaluation
     */
    @Override
    public Result<Page<ApplicationDataset>> selectApplicationEvaluationList(ApplicationDatasetParam applicationDatasetParam)
    {
        //数据隔离
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        String tenantId =null;
        String userid=null;
        String dept_id=null;
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            tenantId = oauthUser.getTenantId();
            userid= String.valueOf(oauthUser.getId());
            dept_id=oauthUser.getDeptId();
            if(StringUtils.isEmpty(oauthUser.getTenantId())){
                tenantId=tokenUserInfo.getTenantId();
            }
        }
        Wrappers<Object> wrappers = Wrappers.init()//makeType
                .and(StringUtils.isNotEmpty(tenantId)&&!tenantId.equals(""),APPLICATION_DATASET.TENANT_ID.eq(tenantId))
                .and(StringUtils.isNotEmpty(userid)&&!userid.equals(""),APPLICATION_DATASET.USER_ID.eq(userid))
                .and(StringUtils.isNotEmpty(dept_id)&&!dept_id.equals(""),APPLICATION_DATASET.DEPT_ID.eq(dept_id))
                .and(APPLICATION_DATASET.DELETE_FLAG.eq(0))
                .orderBy(APPLICATION_DATASET.CREATE_TIME.desc());
        Page<ApplicationDataset> page = page(Page.of(applicationDatasetParam.getPageNo(), applicationDatasetParam.getPageSize()), wrappers);
        return Result.success(page);
    }

    /**
     * 新增ApplicationEvaluation
     * 
     * @param applicationDatasetParam ApplicationEvaluation
     * @return 结果
     */
    @Override
    public Result<ApplicationDataset> insertApplicationEvaluation(ApplicationDatasetParam applicationDatasetParam)
    {
        ApplicationDataset applicationDataset = new ApplicationDataset();
        BeanUtil.copyProperties(applicationDatasetParam,applicationDataset);
        //数据隔离
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            if(StringUtils.isNotEmpty(oauthUser.getTenantId())){
                applicationDataset.setTenantId(oauthUser.getTenantId());
            }
            if(StringUtils.isNotEmpty(oauthUser.getAccountName())){
                applicationDataset.setCreateUser(oauthUser.getAccountName());
                applicationDataset.setUpdateUser(oauthUser.getAccountName());
            }
            if(StringUtils.isNotEmpty(oauthUser.getDeptId())){
                applicationDataset.setDeptId(oauthUser.getDeptId());
            }
            if(ObjectUtil.isNotEmpty(oauthUser.getId())){
                applicationDataset.setUserId(String.valueOf(oauthUser.getId()));
            }
        }
        applicationDataset.setCreateTime(DateUtil.getCurrentTime());
        applicationDataset.setDeleteFlag(0);
        mapper.insert(applicationDataset);
        return Result.success(applicationDataset);
    }

    /**
     * 修改ApplicationEvaluation
     * 
     * @param applicationDatasetParam ApplicationEvaluation
     * @return 结果
     */
    @Override
    public Result<ApplicationDataset> updateApplicationEvaluation(ApplicationDatasetParam applicationDatasetParam)
    {
        ApplicationDataset applicationDataset = new ApplicationDataset();
        BeanUtil.copyProperties(applicationDatasetParam,applicationDataset);
        //数据隔离
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
            if(StringUtils.isNotEmpty(oauthUser.getTenantId())){
                applicationDataset.setTenantId(oauthUser.getTenantId());
            }
            if(StringUtils.isNotEmpty(oauthUser.getAccountName())){
                applicationDataset.setCreateUser(oauthUser.getAccountName());
                applicationDataset.setUpdateUser(oauthUser.getAccountName());
            }
            if(StringUtils.isNotEmpty(oauthUser.getDeptId())){
                applicationDataset.setDeptId(oauthUser.getDeptId());
            }
            if(ObjectUtil.isNotEmpty(oauthUser.getId())){
                applicationDataset.setUserId(String.valueOf(oauthUser.getId()));
            }
        }
        applicationDataset.setCreateTime(DateUtil.getCurrentTime());
        mapper.update(applicationDataset);
        return Result.success(applicationDataset);
    }
    /**
     * 删除ApplicationEvaluation信息
     * 
     * @param applicationDatasetParam
     * @return 结果
     */
    @Override
    public Result deleteApplicationEvaluationById(ApplicationDatasetParam applicationDatasetParam)
    {
        Wrappers<Object> wrappers = Wrappers.init()//makeType
                .and(applicationDatasetParam.getDatasetId()!=null,APPLICATION_DATASET.DATASET_ID.eq(applicationDatasetParam.getDatasetId()));
        mapper.deleteByQuery(wrappers);
        return Result.success();
    }

    @Override
    public Result export(ApplicationDatasetParam applicationDatasetParam, HttpServletResponse response) throws IOException {
        Wrappers<Object> wrappers = Wrappers.init()//makeType
                .and(applicationDatasetParam.getDatasetId()!=null,APPLICATION_DATASET.DATASET_ID.eq(applicationDatasetParam.getDatasetId()));
        List<ApplicationDataset> applicationDatasets = mapper.selectListByQuery(wrappers);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode("数据集.xlsx", String.valueOf(StandardCharsets.UTF_8)));
        //resetCellMaxTextLength();
        List<ApplicationDatasetDTO> applicationDatasetDTOS =new ArrayList<>();
        for (ApplicationDataset applicationDataset : applicationDatasets) {
            ApplicationDatasetDTO applicationDatasetDTO = new ApplicationDatasetDTO();
            BeanUtil.copyProperties(applicationDataset,applicationDatasetDTO);
            applicationDatasetDTOS.add(applicationDatasetDTO);
        }
        EasyExcel.write(response.getOutputStream(), ApplicationDatasetDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("数据集")
                .doWrite(applicationDatasetDTOS);
        return Result.success();
    }

    @Override
    public Result importData(MultipartFile file,String datasetId) throws IOException {
        //获取文件的输入流
        try {
            InputStream inputStream = file.getInputStream();
            List<ApplicationDataset> lst = EasyExcel.read(inputStream) //调用read方法
                    //注册自定义监听器，字段校验可以在监听器内实现
                    //.registerReadListener(new UserListener())
                    .head(ApplicationDataset.class) //对应导入的实体类
                    .sheet(0) //导入数据的sheet页编号，0代表第一个sheet页，如果不填，则会导入所有sheet页的数据
                    .headRowNumber(1) //列表头行数，1代表列表头有1行，第二行开始为数据行
                    .doReadSync(); //开始读Excel，返回一个List<T>集合，继续后续入库操作
            //数据隔离
            TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
            Iterator<ApplicationDataset> iterator = lst.iterator();
            while (iterator.hasNext()) {
                ApplicationDataset applicationDataset = iterator.next();
                if (null != tokenUserInfo&& ObjectUtil.isNotEmpty(tokenUserInfo)&&ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                    OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                    if(StringUtils.isNotEmpty(oauthUser.getTenantId())){
                        applicationDataset.setTenantId(oauthUser.getTenantId());
                    }
                    if(StringUtils.isNotEmpty(oauthUser.getAccountName())){
                        applicationDataset.setCreateUser(oauthUser.getAccountName());
                        applicationDataset.setUpdateUser(oauthUser.getAccountName());
                    }
                    if(StringUtils.isNotEmpty(oauthUser.getDeptId())){
                        applicationDataset.setDeptId(oauthUser.getDeptId());
                    }
                    if(ObjectUtil.isNotEmpty(oauthUser.getId())){
                        applicationDataset.setUserId(String.valueOf(oauthUser.getId()));
                    }
                }
                applicationDataset.setQuestionId(IdUtil.simpleUUID());
                applicationDataset.setCreateTime(DateUtil.getCurrentTime());
                applicationDataset.setUpdateTime(DateUtil.getCurrentTime());
                applicationDataset.setDatasetId(datasetId);
            }
            mapper.insertBatch(lst);
        } catch (Exception e) {
            return Result.fail("请按模板上传数据！");
        }
        return Result.success();
    }
}
