// 定义内容
export default {
	stepForm: {
		success:{
			title:"提交成功",
			subTitle:'表單提交成功',
		},
		button:{
			view: '查看詳情',
			again: '再次創建',
		},
		form:{
			description: {
				title: '渠道表单说明',
				text: '广告商渠道推广支持追踪在第三方广告商投放广告下载App用户的场景，例如在今日头条渠道投放下载App广告，追踪通过在渠道下载激活App的用户',
			},
			label: {
				activityName: '活動名稱',
				channelType: '渠道類型',
				promotionTime: '推廣時間',
				promoteLink: '推廣地址',
				advertisingSource: '廣告來源',
				advertisingMedia: '廣告媒介',
				keyword: '關鍵詞',
				pushNotify: '推送提醒',
				advertisingContent: '廣告内容'
			},
			error: {
				activityName: {
					required: '請輸入活動名稱',
					pattern: '輸入漢字、字母或數字，最多20字符',
				},
				channelType: {
					required:'請選擇渠道類型',
				},
				promotionTime: {
					required: '請輸入推廣時間'
				},
				promoteLink: {
					required: '請輸入推廣地址',
					pattern: '如 Android 或 iOS 的下載地址、中間跳轉URL，網址必須以 http:// 或 https:// 開頭'
				},
				advertisingSource: {
					required: '請輸入廣告來源'
				},
				advertisingMedia: {
					required: '請輸入廣告媒介'
				},
				keyword: {
					required: '請選擇管檢測i'
				},
				advertisingContent: {
					required: '請輸入廣告内容',
					maxLength: '最多不超過200字'
				}
			},
			tip: {
				promoteLink: '如 Android 或 iOS 的下載地址、中間跳轉URL，網址必須以 http:// 或 https:// 開頭',
			}
		},
		placeholder: {
			activityName: '輸入漢字、字母或數字，最多20字符',
			channelType: '請選擇渠道類型',
			promoteLink: '請輸入推廣頁面地址',
			advertisingSource: '引薦來源地址：sohu、sina',
			advertisingMedia: '營銷媒介：cpc、banner、edm',
			keyword: '請選擇關鍵詞',
			advertisingContent: '請輸入廣告内容介紹，最多不超過200字。'

		}
	}
};
