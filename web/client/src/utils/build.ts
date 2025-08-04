import importToCDN, { autoComplete } from 'vite-plugin-cdn-import';

/**
 * 打包相关
 * @description importToCDN https://github.com/mmf-fe/vite-plugin-cdn-import
 * @description cdn 在线引入的 cdn 地址配置。path：https://www.jsdelivr.com/
 * @description external 打包时，过滤包导入。参考：https://rollupjs.org/configuration-options/#external
 */
export const buildConfig = {
	cdn() {
		return importToCDN({
			modules: [
				autoComplete('vue'),
				autoComplete('axios'),
				{
					name: 'vue-demi',
					var: 'VueDemi',
					path: 'lib/index.iife.min.js',
				},
				{
					name: 'vue-router',
					var: 'VueRouter',
					path: 'dist/vue-router.global.min.js',
				},
				{
					name: 'echarts',
					var: 'echarts',
					path: 'dist/echarts.min.js',
				},
			],
		});
	},
	external: [
		'vue',
		'axios',
		'vue-router',
		'echarts',
	],
};

