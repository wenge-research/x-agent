// 定义内容
export default {
	stepForm: {
		step: {
			title: '创建渠道表单',
			title2: {
				finish: '完成创建',
				channel: '输入渠道信息',
				baseInfo: '选择基本信息'
			},
			subTitle: {
				baseInfo: '创建渠道活动',
				channel: '输入详细的渠道信息',
				finish: '创建成功',
			}
		},
		success:{
			title:"提交成功",
			subTitle:'表单提交成功',
		},
		button:{
			view: '查看详情',
			again: '再次创建',
			submit: '提交',
			next: '下一步',
			prev: '上一步',

		},
		form:{
			description: {
				title: '渠道表单说明',
				text: '广告商渠道推广支持追踪在第三方广告商投放广告下载App用户的场景，例如在今日头条渠道投放下载App广告，追踪通过在渠道下载激活App的用户',
			},
			label: {
				activityName: '活动名称',
				channelType: '渠道类型',
				promotionTime: '推广时间',
				promoteLink: '推广地址',
				advertisingSource: '广告来源',
				advertisingMedia: '广告媒介',
				keyword: '关键词',
				pushNotify: '推送提醒',
				advertisingContent: '广告内容'
			},
			error: {
				activityName: {
					required: '请输入活动名称',
					pattern: '输入汉字、字母或数字，最多20字符',
				},
				channelType: {
					required:'请选择渠道类型',
				},
				promotionTime: {
					required: '请选择推广时间'
				},
				promoteLink: {
					required: '请输入推广地址',
					pattern: '如 Android 或 iOS 的下载地址、中间跳转URL，网址必须以 http:// 或 https:// 开头'
				},
				advertisingSource: {
					required: '请输入广告来源'
				},
				advertisingMedia: {
					required: '请输入广告媒介'
				},
				keyword: {
					required: '请选择关键词'
				},
				advertisingContent: {
					required: '请输入广告内容',
					maxLength: '最多不超过200字'
				}
			},
			tip: {
				promoteLink: '如 Android 或 iOS 的下载地址、中间跳转URL，网址必须以 http:// 或 https:// 开头',
			}
		},
		placeholder: {
			activityName: '输入汉字、字母或数字，最多20字符',
			channelType: '请选择渠道类型',
			promoteLink: '请输入推广页面地址',
			advertisingSource: '引荐来源地址：sohu、sina',
			advertisingMedia: '营销媒介：cpc、banner、edm',
			keyword: '请选择关键词',
			advertisingContent: '请输入广告内容介绍，最多不超过200字。'

		}

	}


};
