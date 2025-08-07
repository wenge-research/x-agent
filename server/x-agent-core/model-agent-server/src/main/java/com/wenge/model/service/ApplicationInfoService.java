package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.AiImageResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Description: 应用信息服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:51
 *
 */
public interface ApplicationInfoService extends IService<ApplicationInfo> {

    Result<ApplicationInfo> addApplicationInfo(ApplicationInfo applicationInfo);

    Result<Page<ApplicationInfo>> getApplicationInfoList(ApplicationInfoPageParam applicationInfo);

    Result updateApplicationInfo(ApplicationInfo applicationInfo);

    Result deleteApplicationInfo(StringParam appId);

    Result bindingKnowledge(ApplicationKnowledgeBindedParam param);

    Result<ApplicationInfo> getApplicationDetail(StringParam applicationCode);

    ApplicationInfo getActiveApp(String appId, String apiKey, String debuggerFlag);

    Result copyApp(CopyAppParam param);

    Result<ApplicationInfo> getDefaultApp(EmptyParam param);

    /**
     * 根据appId获取应用信息
     * @param appId
     * @return
     */
    ApplicationInfo getByAppId(String appId);

    /**
     * 根据appIds获取应用信息
     * @param appIds
     * @return
     */
    List<ApplicationInfo> getActiveApp(List<String> appIds);


    /**
     * 根据appId获取应用信息
     * @param appIds
     * @return
     */
    List<ApplicationInfo> getByAppId(List<String> appIds);

    /**
     * 生成提示
     * @param param
     * @return
     */
    SseEmitter generatePrompt(PromptGenerateParam param);

    /**
     * 生成图片
     * @param param
     * @return
     */
    Result<String> aiImage(PromptGenerateParam param);

    /**
     * 生成视频
     * @param param
     * @return
     */
    Result<String> aiVideo(AiVideoParam param);


    /**
     * @author: caohaifeng
     * @date: 2025/2/15 16:22
     * @Description: 审核通过 更新上架字段
     * @Version:1.0
     **/
    int updateAppStoreByApplicationId(String applicationId, Integer publishAppStore);

    /**
     * 只删除application_info 不考虑其他关联
     * 其他关联使用{@link ApplicationInfoService#deleteApplicationInfo}
     * @param appId 应用id
     */
    void deleteByApplicationId(String appId);
    public long getApplicationCount(ApplicationInfoPageParam applicationInfo);
    Result export(ApplicationInfo applicationInfo,HttpServletResponse response) throws IOException;
    Result importApp(MultipartFile file) throws IOException;

    Result<String> aiAudio(AiAudioParam param);

    void updateApp(ApplicationInfo applicationInfo);

    /**
     * 更新应用上架状态
     */
    Result updateApplicationPublishAppStoreState(ApplicationPublishAppStoreUpdateParam param);

    Result<AiImageResult> generaImage(PromptGenerateParam param);
}