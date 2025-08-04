// aside
declare type AsideState = {
	menuList: RouteRecordRaw[];
	clientWidth: number;
};



// navBars breadcrumb
declare type BreadcrumbState<T = any> = {
	breadcrumbList: T[];
	routeSplit: string[];
	routeSplitFirst: string;
	routeSplitIndex: number;
};


// navBars tagsView
declare type TagsViewState<T = any> = {
	routeActive: string | T;
	routePath: string | unknown;
	dropdown: {
		x: string | number;
		y: string | number;
	};
	sortable: T;
	tagsRefsIndex: number;
	tagsViewList: T[];
	tagsViewRoutesList: T[];
};

// navBars parent
declare type ParentViewState<T = any> = {
	refreshRouterViewKey: string;
	iframeRefreshKey: string;
	keepAliveNameList: string[];
	iframeList: T[];
};

// navBars link
declare type LinkViewState = {
	title: string;
	isLink: string;
};
