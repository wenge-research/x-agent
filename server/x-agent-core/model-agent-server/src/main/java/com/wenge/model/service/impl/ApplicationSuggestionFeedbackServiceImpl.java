package com.wenge.model.service.impl;

import cn.hutool.core.io.FileUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.ApplicationSuggestionFeedbackPageParam;
import com.wenge.model.entity.*;
import com.wenge.model.mapper.ApplicationSuggestionFeedbackMapper;
import com.wenge.model.service.*;
import com.wenge.model.utils.EasyExcelUtil;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wenge.model.entity.table.ApplicationSuggestionFeedbackTableDef.APPLICATION_SUGGESTION_FEEDBACK;

@Service
@Slf4j
public class ApplicationSuggestionFeedbackServiceImpl extends ServiceImpl<ApplicationSuggestionFeedbackMapper, ApplicationSuggestionFeedback> implements ApplicationSuggestionFeedbackService {

    @Autowired
    private ApplicationInfoService appInfoService;



    @Override
    public Result add(ApplicationSuggestionFeedback applicationSuggestionFeedback) {
        if (applicationSuggestionFeedback == null) {
            return Result.fail("参数错误");
        }
        if (StringUtils.isBlank(applicationSuggestionFeedback.getApplicationId())) {
            return Result.fail("参数错误，应用ID不能为空");
        }
        ApplicationInfo applicationInfo = appInfoService.getByAppId(applicationSuggestionFeedback.getApplicationId());
        applicationSuggestionFeedback.setCreateTime(new Date());
        applicationSuggestionFeedback.setApplicationName(applicationInfo.getApplicationName());
        applicationSuggestionFeedback.setApplicationCode(applicationInfo.getApplicationCode());
        return Result.success(this.save(applicationSuggestionFeedback));
    }

    @Override
    public Result delete(ApplicationSuggestionFeedback applicationSuggestionFeedback) {
        if (applicationSuggestionFeedback == null) {
            return Result.fail("参数错误");
        }
        if (this.removeByIds(applicationSuggestionFeedback.getDelIds())) {
            return Result.success();
        }
        return Result.fail();
    }

    @Override
    public Result update(ApplicationSuggestionFeedback applicationSuggestionFeedback) {
        if (applicationSuggestionFeedback == null || applicationSuggestionFeedback.getId() == null) {
            return Result.fail("参数错误");
        }
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (tokenUserInfo == null) {
            return Result.fail("用户信息获取失败");
        }
        return Result.success(this.updateById(applicationSuggestionFeedback));
    }

    @Override
    public Result<Page<ApplicationSuggestionFeedback>> getListPage(ApplicationSuggestionFeedbackPageParam param) {
        if (param == null) {
            return Result.fail("参数错误");
        }
        if (param.getPageNo() == null) {
            param.setPageNo(1);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(10);
        }
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (tokenUserInfo == null) {
            return Result.fail("用户信息获取失败");
        }
        Wrappers wrappers = Wrappers.init()
                .where(APPLICATION_SUGGESTION_FEEDBACK.STATUS.eq(0))
                .and(APPLICATION_SUGGESTION_FEEDBACK.TYPE.eq(param.getType()))
                .and(StringUtils.isNotBlank(param.getApplicationName()), APPLICATION_SUGGESTION_FEEDBACK.APPLICATION_NAME.like("%" + param.getApplicationName() + "%"))
                .and(StringUtils.isNotBlank(param.getCreateTimeStart()), APPLICATION_SUGGESTION_FEEDBACK.CREATE_TIME.ge(param.getCreateTimeStart()))
                .and(StringUtils.isNotBlank(param.getCreateTimeEnd()), APPLICATION_SUGGESTION_FEEDBACK.CREATE_TIME.le(param.getCreateTimeEnd()))
                .orderBy(APPLICATION_SUGGESTION_FEEDBACK.CREATE_TIME.desc());
        Page<ApplicationSuggestionFeedback> page = page(Page.of(param.getPageNo(), param.getPageSize()), wrappers);
        return Result.success(page);
    }

    @Override
    public void export(ApplicationSuggestionFeedbackPageParam param, HttpServletResponse response) {
        param.setPageNo(1);
        param.setPageSize(Integer.MAX_VALUE);
        final Result result = this.getListPage(param);
        if (!result.getCode().equals("000000")) {
            log.error("导出异常，请检查！！！");
            return;
        }
        Page<ApplicationSuggestionFeedback> page = (Page<ApplicationSuggestionFeedback>)result.getData();
        List<ApplicationSuggestionFeedback> list = new ArrayList<>();
        if (page != null) {
            list = page.getRecords();
        }
        String fileName = "建议与反馈_";
        exportData(list, fileName, Arrays.asList("序号", "来源应用", "应用ID", "反馈类型", "内容", "附件", "姓名", "联系电话", "提交时间"), response);
    }

    private void exportData(List<ApplicationSuggestionFeedback> list, String fileName,List<String> titles, HttpServletResponse response) {
        java.io.File file = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm");
        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(dateFormatter);
        try {
            // 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            OutputStream outputStream = response.getOutputStream();
            List<List<String>> rows = Lists.newArrayList();
            rows.add(titles);
            AtomicInteger res = new AtomicInteger(1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            list.forEach(data -> {
                List<String> stringList = new ArrayList<>();
                stringList.add("" + (res.getAndIncrement()));
                stringList.add(data.getApplicationName());
                stringList.add(data.getApplicationId());
                stringList.add(data.getType());
                stringList.add(data.getContent());
                stringList.add(data.getImgsUrl());
                stringList.add(data.getCreateUserName());
                stringList.add(data.getCreateUserPhone());
                stringList.add(sdf.format(data.getCreateTime()) + "");
                rows.add(stringList);
            });
            EasyExcelUtil.export(rows, outputStream);
            file = new File(fileName + formattedDateTime + ".xls");
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        } finally {
            if (null != file) {
                FileUtil.del(file);
            }
        }
    }
}