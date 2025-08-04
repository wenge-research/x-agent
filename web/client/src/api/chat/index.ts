import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;
let baseGXZXUrl =  import.meta.env.VITE_BASE_GXZX_URL;

// 登录
export function login(data: object) {
	return request({
	  url: baseUrl + "/login",
	  method: "post",
	  data
	});
  }
//对话
export function dialogue(data: object) {
	return request({
		url: baseUrl + '/dialogue/dialogue',
		method: 'post',
		data,
	});
}
export function closeDialogueConn(data: Object) {
	return request({
		url: baseUrl + '/yayi/closeSseEmitter',
		method: 'post',
		data
	});
}

// 获取相关参数
export function getDialogueParam(appId: number | string) {
	return request({
		url: baseUrl + '/conversation/getDialogueParam/' + appId,
		method: 'get',
	});
}

// 获取会话 id 
export function conversationIdApi(data?: object) {
	return request({
		url: baseUrl + '/conversation/addConversation',
		method: 'post',
		data
	});
}

//获取问答echarts图表
export function getEchartsData(data: object) {
	return request({
		url: baseUrl + '/dialogue/genEChartsDataFromModel',
		method: 'post',
		data,
	});
}

//上传
export function uploadBatch(data?: object, id?: Number) {
	return request({
		url: baseUrl + '/dialogue/uploadBatch/' + id,
		method: 'post',
		data,
	});
}
//上传到minio
export function uploadMinio(data?: object) {
	return request({
		url: baseUrl + '/wos/file/upload/',
		method: 'post',
		data,
	});
}
// 多媒体上传-音频
export function addAudio(params: object) {
  return request({
    url: baseUrl + "/multimedia/addAudio",
    method: "post",
    data: params,
  });
}
//获取文件列表
export function listFile(params?: object) {
	return request({
		url: baseUrl + '/dialogue/listFile/' + params.conversationId,
		method: 'get',
	});
}
//删除文件
export function delFile(id?: Number) {
	return request({
		url: baseUrl + '/dialogue/delFile/' + id,
		method: 'delete',
	});
}
//获取所有对话
export function listDialogues(id: Number | String) {
	return request({
		url: baseUrl + '/conversation/listDialogues/' + id,
		method: 'get',
	});
}
// 新建对话
export function addConversation(data: object) {
	return request({
		url: baseUrl + '/conversation/addConversation/',
		method: 'post',
		data,
	});
}
//获取应用列表
export function getAppList() {
	return request({
		url: baseUrl + '/app/v3/tree',
		method: 'get',
	});
}
// 获取会话列表
export function listConversations(data: object) {
	return request({
		url: baseUrl + '/conversation/listConversations',
		method: 'get',
		params: data,
	});
}
// 分页获取会话列表
export function listConversationsByPage(data: object) {
	return request({
		url: baseUrl + '/conversation/pageConversations',
		method: 'get',
		params: data,
	});
}
// 更新会话
export function updateConversation(data: object) {
	return request({
		url: baseUrl + '/conversation/update',
		method: 'put',
		data,
	});
}
// 更新会话名称
export function updateNameConversation(id: string) {
	return request({
		url: baseUrl + `/conversation/updateName/${id}`,
		method: 'put',
	});
}
// 删除会话
export function deleteConversation(id: Number | String) {
	return request({
		url: baseUrl + '/conversation/delete/' + id,
		method: 'delete',
	});
}

// 踩赞标签选项
export function feedbackListLabel(id: Number | String) {
	return request({
		url: baseUrl + '/feedback/listLabel/' + id,
		method: 'get',
	});
}

// 踩赞对话反馈(旧)
export function feedback(data: object) {
	return request({
		url: baseUrl + '/feedback/feedback',
		method: 'post',
		data,
	});
}

// 踩赞对话反馈(新)
export function apiFeedback(data: object) {
	return request({
		url: baseUrl + '/record/feedback',
		method: 'post',
		data,
	});
}

// 应用id策略
export function listRecommend() {
	return request({
		url: baseUrl + '/app/listRecommend',
		method: 'get',
	});
}
// 示例代码
export function dialogueCode(data: object) {
	return request({
		url: baseUrl + `/dialogue/code/JAVA`,
		method: 'post',
		data,
	});
}
//[首页技能] 推荐问题
// export function listDialogueQuestion(dialogueId: Number | String) {
// 	return request({
// 		url: baseUrl + `/dialogue/listDialogueQuestion/` + dialogueId,
// 		method: 'get',
// 	});
// }
//[首页技能] 推荐问题
export function listDialogueQuestion(data:any) {
	return request({
		url: baseUrl + `/dialogue/sourceAnswer`,
		method: 'post',
		data
	});
}
// 元枢（智库数据分析） - 对话总结
export function apiConversationSummary(data:any) {
	return request({
		url: baseUrl + `/dialogue/conversationSummary`,
		method: 'post',
		data
	});
}
export function listRecommendApps() {
	return request({
		url: baseUrl + `/app/v3/listRecommendApps`,
		method: 'get',
	});
}
//CV上传图片
export function uploadChatImg(data: object) {
	return request({
		url: baseUrl + '/dialogue/uploadChatImg',
		method: 'post',
		data,
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
	});
}
//CV删除图片
export function delChatImg(id: string) {
	return request({
		url: baseUrl + '/dialogue/delChatImg/' + id,
		method: 'delete',
	});
}

//查询应用基本信息
export function getApplication(data:any) {
	return request({
		url: `${baseUrl}/applicationInfo/getApplicationDetail/${data.applicationId}`,
		method: 'POST',
		data
	});
}
//查询应用基本信息-工作流，对话流
export function getWorkflowDetail(data:any) {
	return request({
		url: `${baseUrl}/workflow/queryDetail`,
		method: 'POST',
		data
	});
}
//查询模型列表
export function getLlmPageList(data:any) {
	return request({
		url: `${baseUrl}/llmInfo/getLlmPageList`,
		method: 'POST',
		data
	});
}
//查询语音配置
export function getTtsConfig(data:any) {
	return request({
		url: `${baseUrl}/tts/getTtsConfig`,
		method: 'POST',
		data
	});
}
//推荐问题
export function tjQuestion(data:any) {
	return request({
		url: baseUrl + '/dialogue/recommendQuestion',
		method: 'POST',
		data
	});
}
//溯源列表
export function sourceAnswer(data:any) {
	return request({
		url: baseUrl + '/dialogue/sourceAnswer',
		method: 'POST',
		data
	});
}
//溯源过程接口列表
export function getStepByDialogId(data:any) {
	return request({
		url: baseUrl + '/dialogue/getStepByDialogId',
		method: 'POST',
		data
	});
}
//服务菜单
export function serviceList(data:any) {
	return request({
		url: baseUrl + '/serviceMenu/getServiceMenuList',
		method: 'POST',
		data
	});
}

// 表单结构
export function getFormInfo(data:any) {
	return request({
		url: baseUrl + '/matterGuideFiled/getFormInfo',
		method: 'POST',
		data
	});
}

// 表单结构(新)
export function getFormInfoNew(data:any) {
	return request({
		url: baseUrl + '/matterGuideFiled/getFormInfoNew',
		method: 'POST',
		data
	});
}

// 上传资源
export function uploadPic(data:any) {
  return request({
		url: baseUrl + '/matterGuideFiled/uploadPic',
		method: 'POST',
		data,
    headers:{
			'Content-Type': 'application/x-www-form-urlencoded'
		}
	});
}

// 表单提交
export function submitFormInfo(data:any) {
	return request({
		url: baseUrl + '/matterGuideData/submitFormInfo',
		method: 'POST',
		data
	});
}

// 文字转语音
export function textToVoice(data:any) {
	return request({
		url: baseUrl + '/tts/textToVoice',
		method: 'POST',
		data,
		responseType:'blob',
	});
}
// 中英文互译
export function translateText(data:any) {
	return request({
		url: baseUrl + '/dialogue/translateText',
		method: 'POST',
		data,
	});
}
// rsj文字转语音
export function rsjTextToVoice(data:any) {
	return request({
		url: '/synthesize',
		method: 'POST',
		data,
		responseType:'blob',
	});
}
// 长沙文字转语音
export function csTextToVoice(data:any){
	return request({
		url:baseUrl + '/tts/changsha/textToVoice',
		method: 'POST',
		data
	});
}

// 长沙语言转文字
export function csVoiceToText(data:any){
	return request({
		url:baseUrl +"/tts/changsha/voiceToText",
		method:"POST",
		data
	})
}

// 关芯质询 手机号
export function guanxinCheckPhone(data:any) {
	return request({
		url: baseUrl + '/guanxin/checkPhone',
		method: 'POST',
		data
	});
}

export function todayNoLogin(data:any) {
	return request({
		url: baseUrl + "/guanxin/todayNoLogin",
		method: 'POST',
		data
	});
}

export function recordGetRecord(data:any) {
	return request({
		url: baseUrl + "/record/getRecord",
		method: 'POST',
		data
	});
}
export function getRecordByAccountName(data:any) {
	return request({
		url: baseUrl + "/getRecord/getRecordByAccountName",
		method: 'POST',
		data
	});
}

// 删除对话
export function recordLogicDelete(data:any) {
	return request({
		url: baseUrl + "/record/logicDelete",
		method: 'POST',
		data
	});
}

// 关芯智巡相关接口-居民上报
export function extendTaskReport(data:any) {
	return request({
		url: baseGXZXUrl + "/extend/common/extendTaskReport",
		method: 'POST',
		data
	});
}

// 关芯智巡相关接口-最近点位
export function nearPointList(data:any) {
    // baseGXZXUrl + 
	return request({
		url: baseGXZXUrl + "/extend/common/nearPointList",
		method: 'POST',
		data
	});
}

// 上传文件文件上传返回文件信息
export function commonUploadFile(data:any) {
    // baseGXZXUrl + 
	return request({
		url: baseGXZXUrl + "/extend/common/uploadFile",
		method: 'post',
		data,
        headers:{
			'Content-Type': 'application/x-www-form-urlencoded'
		}
	});
}
// 网信办列表
export function apiGetLegalAndRegulatoryDataList(data:any) {
	// baseGXZXUrl + 
return request({
	url: baseUrl + "/legalAndRegulatory/getLegalAndRegulatoryDataList",
	method: 'POST',
	data
});
}
// 网信办列表
export function apiAddSuggestionFeedback(data:any) {
	// baseGXZXUrl + 
	return request({
		url: baseUrl + "/suggestionFeedback/add",
		method: 'POST',
		data
	});
}
// 敏感词检测
export function apiCheckSensitiveWord(data:any) {
	return request({
		url: baseUrl + "/dialogue/checkSensitiveWord",
		method: 'POST',
		data
	});
}

// 视频解析
export function apiAddCatalog(data:any) {
	return request({
		url: baseUrl + "/youya/addCatalog",
		method: 'POST',
		data
	});
}


// 视频内容解析
export function apiQueryVideoInfo(data:any) {
	return request({
		url: baseUrl + "/youya/queryVideoInfo",
		method: 'POST',
		data
	});
}
//上传要分享的对话
export function apiUploadShare(data){
	return request({
		url:baseUrl + '/DialogueCache/addDialogueCache',
		headers: {
			'Content-Type': 'application/json' // 明确告诉服务器发送的是 JSON 数组
		},
		method:'POST',
		data
	})
}
//获取分享了的对话
export function apiGetShare(data:any){
	return request({
		url:baseUrl + `/DialogueCache/getDialogueCache?key=${data}`,  
		method:'GET'
	})
}

//应用对话数据插入
export function apiInsertApplicationInfoDialogue(data:any) {
	return request({
		url: `${baseUrl}/applicationDialogueInfo/insertApplicationInfoDialogue`,
		method: 'POST',
		data
	});
}

//应用对话数据查询
export function apiGetApplicationInfoDialogueList(data:any) {
	return request({
		url: `${baseUrl}/applicationDialogueInfo/getApplicationInfoDialogueList`,
		method: 'POST',
		data
	});
}

//应用对话做题数据保存
export function apiSaveDialogueRecord(data:any) {
	return request({
		url: `${baseUrl}/applicationDialogueRecordInfo/saveDialogueRecord`,
		method: 'POST',
		data
	});
}

//文件解析
export function apiGetText(data:any){
return request({
	url: baseUrl + `/file/analysisFile`,
	method:'POST',
	data,
})
}
//图片解析
export function apiGetImg(data:any){
	return request({
		url:baseUrl + `/multimedia/analysisImage`,
		method:'POST',
		data,
	})
}

//视频接口
export function getVidioeData(data: object) {
	return request({
	  url: baseUrl + "/applicationInfo/aiVideo",
	  method: "post",
	  data
	});
  }


  //图片接口
export function getImgData(data: object) {
	return request({
	  url: baseUrl + "/applicationInfo/aiImage",
	  method: "post",
	  data
	});
  }
  export function getGeneraImage(data: object) {
	return request({
	  url: baseUrl + "/applicationInfo/generaImage",
	  method: "post",
	  data
	});
  }
  //切换视频图片模型
  export function changeModel(data: object) {
	return request({
	  url: baseUrl + "/llmInfo/getLlmInfoList",
	  method: "post",
	  data
	});
  }

  //查询视频图片数据
  export function getImgVideoList(data: object) {
	return request({
	  url: baseUrl + "/video/display",
	  method: "post",
	  data
	});
  }
    //保存到个人中心
  export function saveMine(data: object) {
	return request({
	  url: baseUrl + "/image/saveImageInfo",
	  method: "post",
	  data
	});
  }

