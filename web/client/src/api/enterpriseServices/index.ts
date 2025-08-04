import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;
// 获取栏目列表
export function getColumnList(data: object) {
	return request({
		url: baseUrl + '/gkStory/getColumnList',
		method: 'post',
		data,
	});
}
// 获取稿件列表
export function getStoryByColumnId(data: object) {
	return request({
		url: baseUrl + '/gkStory/getStoryByColumnId',
		method: 'post',
		data,
	});
}
// 查看一条详情
export function getStoryById(data: object) {
	return request({
		url: baseUrl + '/gkStory/getStoryById',
		method: 'post',
		data,
	});
}

