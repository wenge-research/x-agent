import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;
let writerUrl = import.meta.env.VITE_WRTTER_URL;

//查询知识库
export function userPage(data: object) {
	return request({
		url: baseUrl + '/knowledge-base/userPage',
		method: 'post',
		data,
	});
}
//创建知识库
export function createKnowledge(data: object) {
	return request({
		url: baseUrl + '/knowledge-base/createKnowledge',
		method: 'post',
		data,
	});
}
//编辑知识库
export function deleteKnowledge(data: object) {
	return request({
		url: baseUrl + '/knowledge-base/deleteKnowledge',
		method: 'post',
		data,
	});
}
//删除知识库
export function editKnowledge(data: object) {
	return request({
		url: baseUrl + '/knowledge-base/editKnowledge',
		method: 'post',
		data,
	});
}
//获取用户所属知识库容量总信息
export function userKnowledgesSize() {
	return request({
		url: baseUrl + '/knowledge-base/userKnowledgesSize',
		method: 'get',
	});
}

//根据ID查询知识库详情
export function knowledgeDetail(id: string) {
	return request({
		url: baseUrl + '/knowledge-base/detail/' + id,
		method: 'get',
	});
}
//根据ID获取文档库文档树结构
export function fileTree(id: string) {
	return request({
		url: baseUrl + '/knowledge-file/fileTree/' + id,
		method: 'get',
	});
}
//根据文件ID获取文件信息
export function getById(id: string) {
	return request({
		url: baseUrl + '/knowledge-file/file/getById/' + id,
		method: 'get',
	});
}
//文件批量上传
export function fileUploadBatch(data: object) {
	return request({
		url: baseUrl + '/knowledge-file/file/uploadBatch',
		method: 'post',
		data,
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		}
	});
}
//文件失败重试
export function fileReLoad(fileId: string) {
	return request({
		url: baseUrl + '/knowledge-file/file/reLoad/' + fileId,
		method: 'post',
	});
}
//文件失败重试--需上传文件
export function reLoadUploadBatch(data: object) {
	return request({
		url: baseUrl + '/knowledge-file/file/reLoad/uploadBatch',
		method: 'post',
		data,
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		}
	});
}
//文件或文件夹拖拽移动
export function fileTreeDrag(data: object) {
	return request({
		url: baseUrl + '/knowledge-file/fileTree/drag',
		method: 'post',
		data,
	});
}
//更新文件
export function fileUpdateName(params: object) {
	return request({
		url: baseUrl + '/knowledge-file/file/update',
		method: 'PUT',
		params,
	});
}
//删除文件
export function fileDelete(id: string) {
	return request({
		url: baseUrl + '/knowledge-file/file/delete/' + id,
		method: 'DELETE',
	});
}
//获取上传任务
export function getUploadTaskData(id: string) {
	return request({
		url: baseUrl + '/knowledge-file/file/getUploadTaskData/' + id,
		method: 'get',
	});
}
//创建知识库文件夹
export function createDir(data: object) {
	return request({
		url: baseUrl + '/knowledge-dir/create',
		method: 'post',
		data,
	});
}
//更新文件夹
export function dirUpdateName(data: object) {
	return request({
		url: baseUrl + '/knowledge-dir/edit',
		method: 'post',
		data,
	});
}
//删除知识库文件夹
export function dirDelete(id: string) {
	return request({
		url: baseUrl + '/knowledge-dir/delete/' + id,
		method: 'get',
	});
}

// 获取相关参数
export function getDialogueParam(appId: number | string) {
	return request({
		url: baseUrl + '/conversation/getDialogueParam/' + appId,
		method: 'get',
	});
}
// [知识库问答] @文件列表
export function listKnowledgeBaseFile(params: object) {
	return request({
		url: baseUrl + '/dialogue/listKnowledgeBaseFile',
		method: 'get',
		params
	});
}
// [知识库问答] @指令列表
export function listKnowledgeBasePrompt(params: object) {
	return request({
		url: baseUrl + '/dialogue/listKnowledgeBasePrompt',
		method: 'get',
		params
	});
}
// [知识库问答] @指令列表 文件文件夹
export function knowledgeAt(params: object) {
	return request({
		url: baseUrl + '/knowledge-base/knowledgeAt',
		method: 'get',
		params
	});
}

// [智能问答] @指令列表
export function listChatBasePrompt(params: object) {
	return request({
		url: baseUrl + '/app/v3/fuzzyMatching',
		method: 'get',
		params
	});
}
// [智能问答] 知识库推荐问答
export function listRecommendQuestion(data: object) {
	return request({
		url: baseUrl + '/dialogue/listRecommendQuestion/',
		method: 'post',
		data
	});
}

//查询链接
export function getPolicyLink(data: object) {
	return request({
		url: '/gpt-server/policyQa/getPolicyLink',
		method: 'post',
		data
	});
}
//政策性文件接口
export function get4317Json() {
	return request({
		url: '/knowledge-pc/postmeta/i/4317.json',
		method: 'get',
	});
}
//规范性文件接口
export function get4316Json() {
	return request({
		url: '/knowledge-pc/postmeta/i/4316.json',
		method: 'get'
	});
}
//政策解读接口
export function get4310Json() {
	return request({
		url: '/knowledge-pc/postmeta/i/4310.json',
		method: 'get'
	});
}
// 查询政策信息
export function getPolicyInfo(data: object) {
	return request({
		url: '/gpt-server/policyQa/getPolicyInfo',
		method: 'post',
		data
	});
}
// 查询预置问题列表
export function getPresetQuestionList(data: object) {
	return request({
		url: baseUrl + '/presetQuestion/getPresetQuestionList',
		method: 'post',
		data
	});
}
// 查询语音配置-阿里token
export function getAliToken(data: object) {
	return request({
		url: baseUrl + '/tts/getAliToken',
		method: 'post',
		data
	});
}
// 查询语音配置
export function getTtsConfigList(sttId: object) {
	return request({
		url: baseUrl + '/voiceComponentInfo/getDetail/' + sttId,
		method: 'get',
	});
}
// 查询公文模板
export function getCategoryTemplates() {
	return request({
		url: writerUrl + '/wrt-template-category-entity/getCategoryTemplates',
		method: 'get',
	});
}

// 热门搜索
export function questionChartsTop50(data: object) {
	return request({
		url: baseUrl + '/applicationAnalysis/questionChartsTop50',
		method: 'post',
		data
	});
}

// 提示搜索页面
export function questionRealtimeRecommendTopN(data: object) {
	return request({
		url: baseUrl + '/applicationAnalysis/questionRealtimeRecommendTopN',
		method: 'post',
		data
	});
}

// 智能翻译
export function apiTranslateTextOrFile(data: object) {
	return request({
		url: baseUrl + '/intelligentTranslation/executeTranslateTextWG',
		method: 'post',
		data
	});
}

//收录数量
export function getFileSizeList(params: object={}) {
	return request({
		url: baseUrl + '/aiSearchSourceFile/getFileSizeList',
		method: 'get',
		params
	});
}

//找寻文件列表
export function getSearchSourceFileList(data: object= {}) {
	return request({
		url: baseUrl + '/ai/search/getFileList',
		method: 'post',
		data
	});
}
// 重庆大学城 - 政策帮推荐问题
export function apiGetRecommendByUserInfo(data: object) {
	return request({
		url: baseUrl + '/dialogue/getRecommendByUserInfo',
		method: 'post',
		data
	});
}

// 根据ai搜索结果生成结构化数据
export function getStructuredData(data: object) {
	return request({
		url: baseUrl + '/ai/search/byJgh',
		method: 'post',
		data
	});
	
}


// 查询热门搜索词排名前N的数据(支持拼音搜索)
export function getHotSearchWord(data: object) {
	return request({
		url: baseUrl + '/hotSearchWord/queryHotSearchWordTopN',
		method: 'post',
		data
	});	
}

// 手动根据问题提取热门词
export function getHotSearchWordByQuestion(data: object) {
	return request({
		url: baseUrl + '/hotSearchWord/addHotSearchWord',
		method: 'post',
		data
	});
}

//查询订阅推荐列表
export function getSubscribeRecommendList(data: object) {
	return request({
		url: baseUrl + '/ai/search/getSubFileList',
		method: 'post',
		data
	});	
}

// 获取热搜文档列表
export function getHotSearchFileList(data: object) {
	return request({
		url: baseUrl + '/ai/search/getHotFileList',
		method: 'post',
		data
	});	
}

// 查询我的订阅列表
export function getMySubscribeList(data: object) {
	return request({
		url: baseUrl + '/ai/search/getMySubFileList',
		method: 'post',
		data
	});	
}

// 订阅/取消订阅接口
export function subscribe(data: object) {
	return request({
		url: baseUrl + '/aiSearchDataAnalysis/subOrUnSub',
		method: 'post',
		data
	});
	
}

// 查看文档
export function getFileContent(data: object) {
	return request({
		url: baseUrl + '/aiSearchDataAnalysis/read',
		method: 'post',
		data
	});
}

// 点赞文档
export function addFileLike(data: object) {
	return request({
		url: baseUrl + '/aiSearchDataAnalysis/like',
		method: 'post',
		data
	});
}

// 查询应用关联的文件列表
export function getFileListByAppId(data: object) {
	return request({
		url: baseUrl + '/ai/search/queryKnowledgeList',
		method: 'post',
		data
	});	
}

// 图文分析
export function apiMathModel(data: object) {
	return request({
		url: baseUrl + '/smartApi/llmSmart/mathModel',
		method: 'post',
		data
	});	
}

// 全文概况/大纲提取/知识提取
export function apiSummary(data: object) {
	return request({
		url: baseUrl + '/smartApi/llmSmart/summary',
		method: 'post',
		data
	});	
}

// 查询热门搜索
export function getHotSearch(data: object) {
	return request({
		url: baseUrl + '/ai/search/questionChartsTop50',
		method: 'post',
		data
	});
}

// 查询联想问题列表
export function getAssociationQuestionList(data: object) {
	return request({
		url: baseUrl + '/dialogue/associationQuestion',
		method: 'post',
		data
	});
}

// 查询模糊问题列表
export function getVagueQuestionList(data: object) {
	return request({
		url: baseUrl + '/dialogue/recommendQuestion',
		method: 'post',
		data
	});
}