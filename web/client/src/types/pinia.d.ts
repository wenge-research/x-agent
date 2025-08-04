/**
 * pinia 类型定义
 */

// 用户信息
declare interface UserInfosState<T = any> {
	userInfos: {
		authBtnList: string[];
		photo: string;
		roles: string[];
		time: number;
		userName: string;
		firstLogin: boolean;
		[key: string]: T;
	};
	offline: boolean | string;
}

// 路由缓存列表
declare interface KeepAliveNamesState {
	keepAliveNames: string[];
	cachedViews: string[];
}

// 后端返回原始路由(未处理时)
declare interface RequestOldRoutesState {
	requestOldRoutes: string[];
}

// TagsView 路由列表
declare interface TagsViewRoutesState<T = any> {
	tagsViewRoutes: T[];
	isTagsViewCurrenFull: Boolean;
}

// 路由列表
declare interface RoutesListState<T = any> {
	routesList: T[];
}

// 布局配置
declare interface ThemeConfigState {
	themeConfig: {
		isDrawer: boolean;
		primary: string;
		topBar: string;
		topBarColor: string;
		menuBar: string;
		menuBarColor: string;
		menuBarActiveColor: string;
		isCollapse: boolean;
		isUniqueOpened: boolean;
		isFixedHeader: boolean;
		isFixedHeaderChange: boolean;
		isClassicSplitMenu: boolean;
		isShowLogo: boolean;
		isShowLogoChange: boolean;
		isBreadcrumb: boolean;
		isTagsview: boolean;
		isBreadcrumbIcon: boolean;
		isTagsviewIcon: boolean;
		isCacheTagsView: boolean;
		isSortableTagsView: boolean;
		isShareTagsView: boolean;
		isFooter: boolean;
		isGrayscale: boolean;
		isIsDark: boolean;
		isWartermark: boolean;
		wartermarkText: string;
		tagsStyle: string;
		animation: string;
		layout: string;
		isRequestRoutes: boolean;
		globalTitle: string;
		globalI18n: string;
		isVisible: boolean;
	};
}

//chat
declare namespace Chat {
	interface Chat {
		dateTime: string;
		text: string;
		inversion?: boolean;
		error?: boolean;
		loading?: boolean;
		conversationOptions?: ConversationRequest | null;
		requestOptions: { prompt: string; options?: ConversationRequest | null };
	}

	interface History {
		name: string;
		isEdit: boolean;
		id: number | string;
		createTime: string;
	}

	interface ChatState {
		errorText: string;
		streamVoiceFlag: boolean;
		isLightTheme: boolean;
		dialogueParams: any;
		active: number | null | string;
		conversationsId: string;
		conversationsIdforUploadFlag: string;
		usingContext: boolean;
		dialogueLoading: boolean;
		history: History[];
		chat: { id: number; data: Chat[] }[];
		chatList: [];
		fileList: [];
		refList: [];
		fileUploadList: [];
		appTreeList: [];
		paramsDrawerVisible: boolean;
		uploadDrawerVisible: boolean;
		chatLoading: boolean;
		paramsNum: number;
		dialogueParamsList: [];
		dialogueParamsListOld: [];
		abortController: any;
		clientId: string;
		tempText: string;
		answerStr: string;
		isOpen: boolean;
		rightFlag: number;
		scrollbarBottom: boolean;
		timer: any;
		chatInputText: string;
		chatInputTextValue: string;
		webSourceList: [],
		isShowNoice: boolean,
		noticeData: Object,
		currentBalance: number,
		isFirstConsume: boolean,
		collapsedMeun: boolean,
		categoryId: string,
		fileUpdateCV: Object,
		isSensitive: boolean,
		uploadImgStatus: boolean,
		caiForm: { popTitle: string; placeholder: string,options: []};
		zanForm: { popTitle: string; placeholder: string,options: []};
		datapanelParas: { show: number, list: any, value: any, chat: any, document:any, config: any};
	}

}

//chat
declare namespace knowledge {

	interface knowledgeState {
		moving: any;
		dataItem: {};
		addEditModal: any;
		fileList: any;
		fileActive: any;
		currentLibrary: {};
		fileUpdate: {};
		previewData: any;
		dblclickName: string;
		textFileIds: [];
		citationText: string;
		knowledgesSize: any;
	}

}

declare namespace Robot {
	interface RobotState {
		ws: null;
		getresult: boolean;
		delayFlag: boolean;
		chatInterVal: null;
		player: null;
		videoDom: null;
		notShowIndividual: boolean;
		muted: boolean;
		personSize: [number,number];
		personPos: [];
		fromUser: string;
		referX: number;
		referY: number;
		keyEve: boolean;
	}
}