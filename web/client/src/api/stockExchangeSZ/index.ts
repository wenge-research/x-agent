import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;

// 文件对比
export function apiDocumentDialogue(data: object) {
	return request({
		url: baseUrl + '/dialogue/documentDialogue',
		method: 'post',
		data,
	});
}

// 匹配招投标文件
export function apiMatching(data: object) {
	return request({
		url: baseUrl + '/dialogue/matching',
		method: 'post',
		data,
	});
}
// 保存审查结果
export function apiBatchSave(data: object) {
	return request({
		url: baseUrl + '/reviewResults/batchSave',
		method: 'post',
		data,
	});
}
// 查询prompt
export function apiGetSmartPromptConfigList(data: object) {
	return request({
		url: baseUrl + '/smartPromptConfig/getSmartPromptConfigList',
		method: 'post',
		data,
	});
}
// 新增prompt
export function apiAddSmartPromptConfig(data: object) {
	return request({
		url: baseUrl + '/smartPromptConfig/addSmartPromptConfig',
		method: 'post',
		data,
	});
}
// 修改prompt
export function apiUpdateSmartPromptConfig(data: object) {
	return request({
		url: baseUrl + '/smartPromptConfig/updateSmartPromptConfig',
		method: 'post',
		data,
	});
}
