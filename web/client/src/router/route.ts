import { RouteRecordRaw } from 'vue-router';
import { defineAsyncComponent } from 'vue';

export const dynamicRoutes: Array<RouteRecordRaw> = [
	{
		path: '/',
		name: '/',
		component: defineAsyncComponent(() => import('/@/layout/index.vue')),
		children: [
			{
				//AI出题试题预览页
				path: '/testPreview/:appId?/:dialogueId?',
				name: 'testPreview',
				component: defineAsyncComponent(() => import('/@/views/appTemplate/components/testPreview.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//老版h5、pc
				path: '/knowledgeDetails/:appId?/:conversationId?',
				name: 'knowledgeDetails',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//老版h5、pc
				path: '/knowledgeDetailLh/:appId?/:conversationId?',
				name: 'knowledgeDetailLh',
				component: defineAsyncComponent(() => import('/@/views/lh-gpt/index.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//老版h5、pc
				path: '/knowledgeDetailSjj/:appId?/:conversationId?',
				name: 'knowledgeDetailSjj',
				component: defineAsyncComponent(() => import('/@/views/sjj-gpt/index.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//虚拟园区
				path: '/virtualTemplate/:appId?/:conversationId?',
				name: 'virtualTemplate',
				component: defineAsyncComponent(() => import('/@/views/virtualParkNew/index.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//新版h5
				path: '/chat/:appId?/:conversationId?',
				name: 'chat',
				component: defineAsyncComponent(() => import('/@/views/chatIndex/index-detail.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//新版pc
				path: '/knowledgeDetails-new/:appId?/:conversationId?',
				name: 'knowledgeDetailsNew',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail-new.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//助手pc
				path: '/assistantPc/:appId?/:conversationId?',
				name: 'assistantPc',
				component: defineAsyncComponent(() => import('/@/views/chatIndexAssPc/index-detail.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//助手H5
				path: '/assistantMobile/:appId?/:conversationId?',
				name: 'assistantMobile',
				component: defineAsyncComponent(() => import('/@/views/chatIndexAssH5/index-detail.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//合肥
				path: '/knowledgeDetails-hf/:appId?/:conversationId?',
				name: 'knowledgeDetailsHf',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail-hf.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//合肥
				path: '/knowledgeDetails-tz/:appId?/:conversationId?',
				name: 'knowledgeDetailsTz',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail-tz.vue')),
				meta: {
					title: '对话',
				},
			},

			{
				//AI搜索
				path: '/knowledgeDetails-ai/:appId?/:conversationId?',
				name: 'knowledgeDetailsAI',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail-AI.vue')),
				meta: {
					title: '搜索',
				},
			},
			{
				//智能搜索
				path: '/knowledgeDetails-search/:appId?/:conversationId?',
				name: 'knowledgeDetailsSearch',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail-search.vue')),
			},
			{
				//新的智能搜索
				path: '/knowledgeDetails-searchNew/:appId?/:conversationId?',
				name: 'knowledgeDetailsSearchNew',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail-searchNew.vue')),
				meta: {
					title: '智能搜索',
				},
			},
			{
				//产业链智能搜索
				path: '/knowledgeDetails-searchNew-inudtry/:appId?/:conversationId?',
				name: 'knowledgeDetailsSearchNewInudtry',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail-searchNew-inudtry.vue')),
				meta: {
					title: '产业链智能搜索',
				},
			},
			{
				//香港智能搜索
				path: '/knowledgeDetails-searchNew-hk/:appId?/:conversationId?',
				name: 'knowledgeDetailsSearchNewHk',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail-searchNew-hk.vue')),
				meta: {
					title: '香港智能搜索',
				},
			},
			{
				//香港智能搜索
				path: '/knowledgeDetails-searchNew-zh/:appId?/:conversationId?',
				name: 'knowledgeDetailsSearchNewZh',
				component: defineAsyncComponent(() => import('/@/views/knowledge/index-detail-searchNew-zh.vue')),
				meta: {
					title: '香港智能搜索',
				},
			},
			{
				//深圳交易所
				path: '/stockExchangeSZ/:appId?/:conversationId?',
				name: 'stockExchangeSZ',
				component: defineAsyncComponent(() => import('/@/views/knowledge/stockExchangeSZ.vue')),
				meta: {
					title: '深圳交易所',
				},
			},
			{
				//文档解析
				path: '/fileParsing/:appId?/:conversationId?',
				name: 'fileParsing',
				component: defineAsyncComponent(() => import('/@/views/knowledge/fileParsing.vue')),
				meta: {
					title: '文档解析',
				},
			},
			{
				//智能翻译
				path: '/intelligentTranslation/:appId?/:conversationId?',
				name: 'intelligentTranslation',
				component: defineAsyncComponent(() => import('/@/views/knowledge/intelligentTranslation/index.vue')),
				meta: {
					title: '智能翻译',
				},
			},
			{
				//AI报告
				path: '/intelligentReport/:appId?/:conversationId?',
				name: 'intelligentReport',
				component: defineAsyncComponent(() => import('/@/views/intelligentReport/index.vue')),
				meta: {
					title: '智能翻译',
				},
			},
			{
				//AI创作
				path: '/createTemplate/:appId?/:conversationId?',
				name: 'createTemplate',
				component: defineAsyncComponent(() => import('/@/views/createTemplate/index.vue')),
				meta: {
					title: '智能翻译',
				},
			},
			{
				//AI图片
				path: '/aiImageTemplate/:appId?/:conversationId?',
				name: 'aiImageTemplate',
				component: defineAsyncComponent(() => import('/@/views/aiImageTemplate/index.vue')),
				meta: {
					title: '智能翻译',
				},
			},
			{
				//AI视频
				path: '/aiVideoTemplate/:appId?/:conversationId?',
				name: 'aiVideoTemplate',
				component: defineAsyncComponent(() => import('/@/views/aiVideoTemplate/index.vue')),
				meta: {
					title: '智能翻译',
				},
			},
			{
				//数字人
				path: '/digitalPeople/:appId?/:conversationId?',
				name: 'digitalPeople',
				component: defineAsyncComponent(() => import('../views/knowledge/digitalPeople/index.vue')),
				meta: {
					title: '数字人',
				},
			},
			{
				//数字人新一套-企商
				path: '/szr/:appId?/:conversationId?',
				name: 'DHTemplate',
				component: defineAsyncComponent(() => import('/@/views/DHtemplate/index.vue')),
				meta: {
					title: '数字人',
				},
			},
			{
				// 市监局
				path: '/municipalSupervisionBureau/:appId?/:conversationId?',
				name: 'municipalSupervisionBureau',
				component: defineAsyncComponent(() => import('/@/views/municipalSupervisionBureau/index.vue')),
				meta: {
					title: '市监局',
				},
			},
			{
				// 市监局-新版UI
				path: '/smqa/:appId?/:conversationId?',
				name: 'municipalSupervisionBureauNewUI',
				component: defineAsyncComponent(() => import('/@/views/municipalSupervisionBureau-newUI/index.vue')),
				meta: {
					title: '市监局',
				},
			},
			{
				path: '/personalCenter/:appId?',
				name: 'personalCenter',
				component: defineAsyncComponent(() => import('/@/views/personalCenter/index.vue')),
				meta: {
					title: '个人中心',
				},
			},
			{
				path: '/personalCenterEdit/:appId?',
				name: 'personalCenterEdit',
				component: defineAsyncComponent(() => import('/@/views/personalCenter/edit.vue')),
				meta: {
					title: '个人中心',
				},
			},
			{
				path: '/information/:appId?/:matterId?',
				name: 'information',
				component: defineAsyncComponent(() => import('/@/views/information/index.vue')),
				meta: {
					title: '表单',
				},
			},
			{
				// 重庆高新区掌上两城
				path: '/twoCitiesPlam-cq/:appId?/:conversationId?',
				name: 'twoCitiesPlamCq',
				component: defineAsyncComponent(() => import('/@/views/twoCitiesPlamCq/index-detail.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				// 重庆高新区政策帮
				path: '/policyHelp-cq/:appId?/:conversationId?',
				name: 'policyHelpCq',
				component: defineAsyncComponent(() => import('/@/views/policyHelpCq/index-detail.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				// 标准应用（集成所有功能）
				path: '/appTemplate/:appId?/:conversationId?',
				name: 'appTemplate',
				component: defineAsyncComponent(() => import('/@/views/appTemplate/index.vue')),
				meta: {
					title: '标准应用',
				},
			},
			{
				// 视频背景大屏
				path: '/videoLargeScreen/:appId?/:conversationId?',
				name: 'videoLargeScreen',
				component: defineAsyncComponent(() => import('/@/views/videoLargeScreen/index.vue')),
				meta: {
					title: '视频背景大屏',
				},
			},
			{
				// H5通用模板
				path: '/mobileUniversalTemplate/:appId?/:conversationId?',
				name: 'mobileUniversalTemplate',
				component: defineAsyncComponent(() => import('/@/views/mobileUniversalTemplate/index.vue')),
				meta: {
					title: '标准应用',
				},
			},
			{
				// xxxxxxx移动模板
				path: '/mobileDazhouTemplate/:appId?/:conversationId?',
				name: 'mobileDazhouTemplate',
				component: defineAsyncComponent(() => import('/@/views/mobileDazhouTemplate/index.vue')),
				meta: {
					title: '标准应用',
				},
			},
			{
				//郑小东模板
				path: '/zhengxiaodong/:appId?/:conversationId?',
				name: 'zhengxiaodong',
				component: defineAsyncComponent(() => import('/@/views/zhengxiaodong/index-detail.vue')),
				meta: {
					title: '郑小东模板',
				},
			},
			{
				// 标准应用（粤语版语音转文字-集成所有功能）
				path: '/cantoneseTemplate/:appId?/:conversationId?',
				name: 'cantoneseTemplate',
				component: defineAsyncComponent(() => import('/@/views/cantoneseTemplate/index.vue')),
				meta: {
					title: '标准应用',
				},
			},
			{
				//深圳网信办
				path: '/sz-cac/:appId?/:conversationId?',
				name: 'sz-cac',
				component: defineAsyncComponent(() => import('/@/views/sz-cac/index-detail.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				//四川xxxxxxx
				path: '/dazhouTemplate/:appId?/:conversationId?',
				name: 'dazhouTemplate',
				component: defineAsyncComponent(() => import('/@/views/dazhouTemplate/index.vue')),
				meta: {
					title: '对话',
				},
			},
			{
				// 基础应用
				path: '/basicTemplate/:appId?/:conversationId?',
				name: 'basicTemplate',
				component: defineAsyncComponent(() => import('/@/views/basicTemplate/index.vue')),
				meta: {
					title: '基础应用',
				},
			},
			{
				//公文写作
				path: '/documentWriting/:appId?/:conversationId?',
				name: 'documentWriting',
				component: defineAsyncComponent(() => import('/@/views/documentWriting/index-detail.vue')),
				meta: {
					title: '公文写作',
				},
			},
			{
				//AI视频拆分
				path: '/aiVideoSplitting/:appId?/:conversationId?',
				name: 'aiVideoSplitting',
				component: defineAsyncComponent(() => import('/@/views/knowledge/aiVideoSplitting.vue')),
				meta: {
					title: 'AI视频拆分',
				},
			},
			{
				// 智库数据分析（集成所有功能）
				path: '/TTAnalysis/:appId?/:conversationId?',
				name: 'TTAnalysis',
				component: defineAsyncComponent(() => import('/@/views/ttAnalysis/index.vue')),
				meta: {
					title: '智库数据分析',
				},
			},
			{
				//AI报告生成
				path: '/reportGenerate/:appId?/:conversationId?',
				name: 'reportGenerate',
				component: defineAsyncComponent(() => import('/@/views/reportGenerate/index.vue')),
				meta: {
					title: 'AI报告生成',
				},
			},
		],
	},
	{
		path: '/forbidden',
		name: 'forbidden',
		component: defineAsyncComponent(() => import('/@/views/forbidden/Forbidden.vue')),
		meta: {
			title: '禁止访问',
		},
	},
	{
		path: '/pcLogin',
		name: 'pcLogin',
		component: defineAsyncComponent(() => import('/@/views/pcLogin/index.vue')),
		meta: {
			title: '登录',
		},
	},
	{
		path: '/previewChat/:appId?',
		name: 'previewChat',
		component: defineAsyncComponent(() => import('/@/views/chatIndex/previewChat.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/assistantHome/:appId?',
		name: 'assistantHome',
		component: defineAsyncComponent(() => import('/@/views/chatIndexAssH5/previewChat.vue')),
		meta: {
			title: '关芯助理',
		},
	},
	// 关芯客服 首页个人中心 新
	{
		path: '/homePersonalCenter/:appId?',
		name: 'homePersonalCenter',
		component: defineAsyncComponent(() => import('/@/views/chatIndex/homePersonalCenter.vue')),
		meta: {
			title: '个人中心',
		},
	},
	{
		path: '/enterpriseServices/:appId?',
		name: 'enterpriseServices',
		meta: {
			title: '企业服务',
		},
		component: () => import('/@/views/enterpriseServices/index.vue'),
	},
	{
		path: '/eTime/:appId?',
		name: 'eTime',
		meta: {
			title: 'E刻钟',
		},
		component: () => import('../views/enterpriseServices/eTime.vue'),
	},
	{
		path: '/talent/:appId?',
		name: 'talent',
		meta: {
			title: '找人才',
		},
		component: () => import('../views/enterpriseServices/talent.vue'),
	},
	{
		path: '/talentDetails/:appId?',
		name: 'talentDetails',
		meta: {
			title: '北京市引进人才管理办法(试行)',
		},
		component: () => import('../views/enterpriseServices/attractTalents.vue'),
	},
	{
		path: '/overseasStudy/:appId?',
		name: 'overseasStudy',
		meta: {
			title: '北京市促进留学人员来京创业和工作暂行办法',
		},
		component: () => import('../views/enterpriseServices/overseasStudy.vue'),
	},
	{
		path: '/graduateManagement/:appId?',
		name: 'graduateManagement',
		meta: {
			title: '北京市引进毕业生管理办法',
		},
		component: () => import('../views/enterpriseServices/graduateManagement.vue'),
	},
	{
		path: '/applicationPolicy/:appId?',
		name: 'applicationPolicy',
		meta: {
			title: '申请政策',
		},
		component: () => import('../views/enterpriseServices/applicationPolicy.vue'),
	},
	{
		path: '/projectList/:appId?',
		name: 'projectList',
		meta: {
			title: '找项目',
		},
		component: () => import('/@/views/enterpriseServices/list.vue'),
	},
	// 关芯助理等统一 个人中心历史会话
	{
		path: '/assPersonalCenter/:appId?',
		name: 'assPersonalCenter',
		component: defineAsyncComponent(() => import('/@/views/chatIndexAssH5/homePersonalCenter.vue')),
		meta: {
			title: '个人中心',
		},
	},
	{
		path: '/homePage/:appId?',
		name: 'homePage',
		component: defineAsyncComponent(() => import('/@/views/homePage/index.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/homePageMz/:appId?',
		name: 'homePageMz',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/index.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/map/:appId?',
		name: 'map',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/map.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/mapKj/:appId?',
		name: 'mapKj',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/mapKj.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/bannerRenList/:appId?',
		name: 'bannerRenList',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/bannerRenList.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/bannerDiList/:appId?',
		name: 'bannerDiList',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/bannerDiList.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/bannerFangList/:appId?',
		name: 'bannerFangList',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/bannerFangList.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/bannerQianList/:appId?',
		name: 'bannerQianList',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/bannerQianList.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/itemFl/:appId?',
		name: 'itemFl',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/itemFl.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/itemHqzc/:appId?',
		name: 'itemHqzc',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/itemHqzc.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/itemQysq/:appId?',
		name: 'itemQysq',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/itemQysq.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/investCollection/:appId?',
		name: 'investCollection',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/investCollection.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/investCollectionTwo/:appId?',
		name: 'investCollectionTwo',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/investCollectionTwo.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/policyCompilation/:appId?',
		name: 'policyCompilation',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/policyCompilation.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/policyDetail/:appId?',
		name: 'policyDetail',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/policyDetail.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/appendix/:appId?',
		name: 'appendix',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/appendix.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/understand/:appId?',
		name: 'understand',
		component: defineAsyncComponent(() => import('/@/views/homePageMz/understand.vue')),
		meta: {
			title: '首页',
		},
	},
	// 重庆高新区首页
	{
		path: '/twoCitiesPlamChat/:appId?',
		name: 'twoCitiesPlamChat',
		component: defineAsyncComponent(() => import('/@/views/twoCitiesPlamCq/index.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/noLogin/:appId?',
		name: 'noLogin',
		component: defineAsyncComponent(() => import('/@/views/homePage/noLoginList.vue')),
		meta: {
			title: '未登录名单',
		},
	},
	// 深圳网信办首页
	{
		path: '/szPreviewChat/:appId?',
		name: 'szPreviewChat',
		component: defineAsyncComponent(() => import('/@/views/sz-cac/previewChat.vue')),
		meta: {
			title: '首页',
		},
	},
	{
		path: '/szPreviewChat/list',
		name: 'szPreviewChatList',
		component: defineAsyncComponent(() => import('/@/views/sz-cac/list.vue')),
		meta: {
			title: '列表页',
		},
	},
	{
		path: '/szPreviewChat/details',
		name: 'szPreviewChatDetails',
		component: defineAsyncComponent(() => import('/@/views/sz-cac/details.vue')),
		meta: {
			title: '详情页',
		},
	},
	// 关芯助理 公文写作首页
	{
		path: '/documentChat/:appId?',
		name: 'documentChat',
		component: defineAsyncComponent(() => import('/@/views/documentWriting/previewChat.vue')),
		meta: {
			title: '首页',
		},
	},
];
export const notFoundAndNoPower = [
	{
		path: '/:path(.*)*',
		name: 'notFound',
		component: () => import('/@/views/error/404.vue'),
		meta: {
			title: 'message.staticRoutes.notFound',
			isHide: true,
		},
	},
];
