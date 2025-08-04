import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;

export interface BaseInfoModel {
	activityName: string;
	channelType: string;
	promotionTime: string[];
	promoteLink: string;
}
export interface ChannelInfoModel {
	advertisingSource: string;
	advertisingMedia: string;
	keyword: string[];
	pushNotify: boolean;
	advertisingContent: string;
}
export type UnitChannelModel = BaseInfoModel & ChannelInfoModel;

export function submitChannelForm(data: UnitChannelModel) {
	return request({
		url: baseUrl + '/channel-form/submit',
		method: 'post',
		data,
	})
}

