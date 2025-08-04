/* 测试*/
import request from "@/utils/request";
// const baseUrl = process.env.VUE_APP_API_NEW
/* 线索列表*/

// 认证
export function authorizationApi(params) {
  return request({
    url: "/wx/jsapi/authorization/url",
    method: "get",
    params
  });
}

// 获取短信验证码
export function sendSmsAuthentication(params) {
  return request({
    url: "/auth/sendSmsAuthentication",
    method: "post",
    data:params
  });
}

// 通过手机号找回密码
export function retrievePassword(params) {
  return request({
    url: "/user/retrievePassword",
    method: "post",
    data:params
  });
}

// 登录
export function Login(params) {
  return request({
    url: "/login",
    method: "post",
    params
  });
}
// 登出
export function logout(params) {
  return request({
    url: "/doLogout",
    method: "post",
    params
  });
}
// 修改密码
export function apiChangPw(data) {
  return request({
    url: "/user/changPw",
    method: "post",
    data
  });
}

// 免登
export function analyzePortalToken(token) {
  return request({
    url: `/oauthLoginLog/analyzePortalToken?portaltoken=${token}`,
    method: 'get'
  });
}

// 删除密钥
export function deleteModelPluginApiAuthUser(data) {
    return request({
        url: "/ModelPluginApiAuthUserAuthUser/deleteModelPluginApiAuthUser",
        method: 'post',
        data
    });
}

//添加密钥
export function addModelPluginApiAuthUser(params) {
  return request({
    url: "ModelPluginApiAuthUserAuthUser/addModelPluginApiAuthUser",
    method: "post",
    data: params
  });
}
//密钥列表
export function getModelPluginApiAuthUserList(params) {
  return request({
    url: "/ModelPluginApiAuthUserAuthUser/getModelPluginApiAuthUserList",
    method: "post",
    data: params
  });
}

//更改密钥状态
export function updateModelPluginApiAuthUserList(params) {
  return request({
    url: "/ModelPluginApiAuthUserAuthUser/updateModelPluginApiAuthUser",
    method: "post",
    data: params
  });
}

// 知识库列表
export function getKnowledgeInfoList(params) {
  return request({
    url: "/knowledgeInfo/getKnowledgeInfoList",
    method: "post",
    data: params
  });
}
// 知识库列表
export function exportApi(params) {
  return request({
    url: "/matterGuideInfo/export",
    method: "post",
    data: params,
    responseType: 'blob',
  });
}
// 删除知识库
export function deleteKnowledgeInfo(params) {
  return request({
    url: "/knowledgeInfo/deleteKnowledgeInfo",
    method: "post",
    data: params
  });
}

// 更新状态
export function updateStatus(params) {
  return request({
    url: "/knowledgeInfo/updateStatus",
    method: "post",
    data: params
  });
}

// 保存/编辑知识库
export function addKnowledgeInfo(params) {
  return request({
    url: "/knowledgeInfo/addKnowledgeInfo",
    method: "post",
    data: params
  });
}


// QA对列表
export function getKnowledgeDataList(params) {
  return request({
    url: "/knowledgeData/getKnowledgeDataList",
    method: "post",
    data: params
  });
}

// 分类列表
export function getKnowledgeDataTypeList(params) {
  return request({
    url: "/knowledgeDataType/getKnowledgeDataTypeList",
    method: "post",
    data: params
  });
}

// 添加/编辑QA对
export function addKnowledgeData(params) {
  return request({
    url: "/knowledgeData/addKnowledgeData",
    method: "post",
    data: params,
  });
}

// 删除QA对
export function deleteKnowledgeData(params) {
  return request({
    url: "/knowledgeData/deleteKnowledgeData",
    method: "post",
    data: params,
  });
}

// 文件夹列表
export function getFoldersList(params) {
  return request({
    url: "/folders/getFoldersList",
    method: "post",
    data: params,
  });
}

// 添加文件夹
export function addFolders(params) {
  return request({
    url: "/folders/addFolders",
    method: "post",
    data: params,
  });
}

// 删除文件夹
export function deleteFolders(params) {
  return request({
    url: "/folders/deleteFolders",
    method: "post",
    data: params,
  });
}

// 文件列表
export function getFileList(params) {
  return request({
    url: "/file/getFileList",
    method: "post",
    data: params,
  });
}

// 上传文件
export function addFile(params) {
  return request({
    url: "/file/addFile",
    method: "post",
    data: params,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

// 删除文件
export function deleteFile(params) {
  return request({
    url: "/file/deleteFile",
    method: "post",
    data: params,
  });
}
// 文档切片详情
export function getFileDatas(params) {
  return request({
    url: "/fileData/getFileDatas",
    method: "post",
    data: params,
  });
}
// 复制文档切片
export function copyFileData(params) {
  return request({
    url: "/fileData/copyFileData",
    method: "post",
    data: params,
  });
}

// 新增文档切片信息
export function addFileData(params) {
  return request({
    url: "/fileData/addFileData",
    method: "post",
    data: params,
  });
}

// 删除文档切片信息
export function deleteFileData(params) {
  return request({
    url: "/fileData/deleteFileData",
    method: "post",
    data: params,
  });
}
// 多媒体详情
export function getMediaDatas(params) {
  return request({
    url: "/multiMediaData/getFileDatas",
    method: "post",
    data: params,
  });
}

// 新增多媒体信息
export function addMediaData(params) {
  return request({
    url: "/multiMediaData/addFileData",
    method: "post",
    data: params,
  });
}

// 删除多媒体信息
export function deleteMediaData(params) {
  return request({
    url: "/multiMediaData/deleteFileData",
    method: "post",
    data: params,
  });
}
// 重试多媒体解析
export function singleMediumAnalysis(params) {
  return request({
    url: "/medium/singleMediumAnalysis",
    method: "get",
    params,
  });
}

// 播放段落
export function textToVoice(params) {
  return request({
    url: "/tts/textToVoice",
    method: "post",
    data: params,
  });
}

// 新增主题
export function addKnowledgeDataType(params) {
  return request({
    url: "/knowledgeDataType/addKnowledgeDataType",
    method: "post",
    data: params,
  });
}

// 更新主题
export function updateKnowledgeDataType(params) {
  return request({
    url: "/knowledgeDataType/updateKnowledgeDataType",
    method: "post",
    data: params,
  });
}

// 删除主题
export function deleteKnowledgeDataType(params) {
  return request({
    url: "/knowledgeDataType/deleteKnowledgeDataType",
    method: "post",
    data: params,
  });
}

// QA对文件上传
export function importKnowledgeData(params) {
  return request({
    url: "/knowledgeData/importKnowledgeData",
    method: "post",
    data: params,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}
// URL上传
export function importUrlParserInfoData(params) {
  return request({
    url: "/urlParserInfo/importUrlParserInfoData",
    method: "post",
    data: params,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}


// 切片信息
export function getSliceDetail(params) {
  return request({
    url: "/urlParserInfo/getDetail",
    method: "post",
    data: params,
  });
}
// 切片信息 - 删除
export function apiDeleteKnowledgeUrlData(params) {
  return request({
    url: "knowledgeUrl/deleteKnowledgeUrlData",
    method: "post",
    data: params,
  });
}

// url列表数据
export function getUrlList(params) {
  return request({
    url: "/urlParserInfo/getList",
    method: "post",
    data: params,
  });
}

// 新增url
export function addKnowledgeUrlData(params) {
  return request({
    url: "/knowledgeUrl/addKnowledgeUrlData",
    method: "post",
    data: params,
  });
}

// 新增url切片
export function addUrlSlice(params) {
  return request({
    url: "/knowledgeUrl/addKnowledgeData",
    method: "post",
    data: params,
  });
}

// 编辑url切片
export function setUrlSlice(params) {
  return request({
    url: "/knowledgeUrl/updateKnowledgeData",
    method: "post",
    data: params,
  });
}

// 获取url二次切片
export function getKnowledgeUrlDataDetail(params) {
  return request({
    url: "/knowledgeUrl/getKnowledgeUrlDataListByParentId",
    method: "post",
    data: params,
  });
}
export function getUrlDataList(params) {
  return request({
    url: "/knowledgeUrl/getUrlDataList",
    method: "post",
    data: params,
  });
}

// 应用信息列表
export function getApplicationInfoList(params) {
  return request({
    url: "/applicationInfo/getApplicationInfoList",
    method: "post",
    data: params,
  });
}

// 删除url
export function deleteUrlParserInfo(params) {
  return request({
    url: "/urlParserInfo/delete",
    method: "post",
    data: params,
  });
}

// 结构化数据列表
export function getUnionList(params) {
  return request({
    url: "/dataSourceParserInfo/unionData",
    method: "post",
    data: params,
  });
}

// 结构化数据列表 - 同步
export function apiUpdateSynchStatusUnionData(params) {
  return request({
    url: "/dataSourceParserInfo/updateSynchStatusUnionData",
    method: "post",
    data: params,
  });
}

// 新建结构化数据-数据库
export function addDataSourceParserInfo(params) {
  return request({
    url: "/dataSourceParserInfo/addDataSourceParserInfo",
    method: "post",
    data: params,
  });
}

// 新建结构化数据-文件
export function addExcelParserInfo(params) {
  return request({
    url: "/excelParserInfo/addExcelParserInfo",
    method: "post",
    data: params,
  });
}

// 获取excel数据列表
export function getExcelTableDataList(params) {
  return request({
    url: "/excelParserInfo/getExcelTableDataList",
    method: "post",
    data: params,
  });
}

// 字段勾选保存
export function chooseField(params) {
  return request({
    url: "/tableDirectoryInfo/chooseField",
    method: "post",
    data: params,
  });
}

// excel结构化
export function excelRunTask(params) {
  return request({
    url: "/excelParserInfo/runTask",
    method: "post",
    data: params,
  });
}

// 数据库下表列
export function getTableList(params) {
  return request({
    url: `/dataSourceParserInfo/getTableList/${params.businessId}`,
    method: "get",
  });
}

// 列数据
export function getDataSourceDataList(params) {
  return request({
    url: "/dataSourceParserInfo/getDataSourceDataList",
    method: "post",
    data: params,
  });
}

// 数据源结构化 
export function dataSourceRunTask(params) {
  return request({
    url: "/dataSourceParserInfo/runTask",
    method: "post",
    data: params,
  });
}

// 多媒体上传-音频
export function addAudio(params) {
  return request({
    url: "/multimedia/addAudio",
    method: "post",
    data: params,
  });
}
// 多媒体上传-视频
export function addVideo(params) {
  return request({
    url: "/multimedia/addVideo",
    method: "post",
    data: params,
  });
}

// 多媒体上传-图片
export function addImage(params) {
  return request({
    url: "/multimedia/addImage",
    method: "post",
    data: params,
  });
}

// 修改链接
export function updateFileWebLink(params) {
  return request({
    url: "/file/updateFileWebLink",
    method: "post",
    data: params,
  });
}
// 修改文件密码
export function updatePassword(params) {
  return request({
    url: "/file/update",
    method: "post",
    data: params,
  });
}
// 文件切片拼接
export function getDataByFileId(params) {
  return request({
    url: "/fileData/getDataByFileId",
    method: "post",
    data: params,
  });
}
// 多媒体切片拼接
export function getDataByMediaId(params) {
  return request({
    url: "/multiMediaData/getDataByFileId",
    method: "post",
    data: params,
  });
}

// 删除数据源
export function delDataSourceParserInfo(params) {
  return request({
    url: "/dataSourceParserInfo/del",
    method: "post",
    data: params,
  });
}

// 数据源详情
export function getDataSourceDetail(params) {
  return request({
    url: `/dataSourceParserInfo/getDetail/${params.businessId}`,
    method: "get",
  });
}

// excel启用
export function excelEnable(params) {
  return request({
    url: "/excelParserInfo/enable",
    method: "post",
    data: params,
  });
}

// 数据源启用
export function dataSourceEnable(params) {
  return request({
    url: "/dataSourceParserInfo/enable",
    method: "post",
    data: params,
  });
}
// 获取切片详情 图片
export function getImage(params) {
  return request({
    url: "/multimedia/getImage/" + params.fileId,
    method: "post",
    data: {},
  });
}
// 表单信息列表
export function getMatterGuideList(params) {
  return request({
    url: "/matterGuide/getList",
    method: "post",
    data: params,
  });
}

// 表单信息列表
export function getMatterGuideInfoList(params) {
  return request({
    url: "/matterGuideInfo/getList",
    method: "post",
    data: params,
  });
}

// 表单详情
export function getMatterGuideInfoDetail(params) {
  return request({
    url: `/matterGuideInfo/getDetail/${params.infoId}`,
    method: "get",
  });
}

// 表单详情(新)
export function apiGetDetailNew(params) {
  return request({
    url: `/matterGuideInfo/getDetailNew/${params.infoId}`,
    method: "get",
  });
}

// 更新表单
export function matterGuideInfoUpdate(params) {
  return request({
    url: "/matterGuideInfo/update",
    method: "post",
    data: params,
  });
}

// 向量模型列表
export function apiGetDenseVectorList(data) {
  return request({
    url: "/denseVector/getDenseVectorList",
    method: 'post',
    data
  });
}

// 知识库-文档-重试
export function apiDocumentAnalysisById(data) {
  return request({
    url: "/document/documentAnalysisById",
    method: 'post',
    params: data
  });
}

// 知识库-url上传-重试
export function apiUrlParserById(data) {
  return request({
    url: "/url/urlParserById",
    method: 'post',
    params: data
  });
}

// 知识库-多媒体-重试
export function apiSingleMediumAnalysis(data) {
  return request({
    url: "/medium/singleMediumAnalysis",
    method: 'get',
    params: data
  });
}

export function deleteBigModel(data){
   return request({
    url:'/llmInfo/deleteLlm',
    method:'post',
    data
   })
}

//新建对话
export function addConversation(data) {
	return request({
		url:'/conversation/addConversation/',
		method: 'post',
		data,
	});
}