import { defineStore } from 'pinia';

/**
 * 路由列表
 * @methods setRoutesList 设置路由数据
 */
export const useRoutesList = defineStore('routesList', {
	state: (): RoutesListState => ({
		routesList: [],
	}),
	actions: {
		async setRoutesList(data: Array<string>) {
			this.routesList = data;
		},
	},
});
