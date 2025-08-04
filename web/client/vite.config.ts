import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
import { defineConfig, loadEnv, ConfigEnv } from 'vite';
import vueSetupExtend from 'vite-plugin-vue-setup-extend-plus';
import vueJsx from '@vitejs/plugin-vue-jsx';
//import { viteVConsole } from 'vite-plugin-vconsole'

import Markdown from 'vite-plugin-vue-markdown';
const pathResolve = (dir: string) => {
	return resolve(__dirname, '.', dir);
};

const alias: Record<string, string> = {
	'/@': pathResolve('./src/'),
	'vue-i18n': 'vue-i18n/dist/vue-i18n.cjs.js',
};

const viteConfig = defineConfig((mode: ConfigEnv) => {
	const env = loadEnv(mode.mode, process.cwd());
	return {
		plugins: [
			vue({
				include: [/\.vue$/, /\.md$/],
			}),
			vueSetupExtend(),
			vueJsx({}),
			Markdown(),
			// viteVConsole({
			// 	entry: pathResolve('./src/main.ts'), // 入口文件，或者可以使用这个配置: [path.resolve('src/main.js')]
			// 	localEnabled: true, // 本地是否启用
			// 	enabled: true, // 是否启用
			// 	config: {
			// 	  maxLogNumber: 1000,
			// 	  theme: 'light' // 主题颜色 'dark'|'light'd
			// 	}
			//   })
		],
		root: process.cwd(),
		resolve: { alias },
		base: './',
		optimizeDeps: {
			include: ['winbox-ui-next/es/locale/lang/zh-cn', 'winbox-ui-next/es/locale/lang/en', 'winbox-ui-next/es/locale/lang/zh-tw'],
		},
		server: {
			host: 'localhost',
			// port: env.VITE_PORT as unknown as number,
			port: 8082,
			open: env.VITE_OPEN === 'true',
			hmr: true,
			proxy: {
				'/smart-agent-api-demo': {
					target: 'http://192.168.1.115:8686',
					ws: true,
					changeOrigin: true, // 允许跨域
					// rewrite: (path) => path.replace('/smart-agent-api-demo', ''),
					rewrite: (path) => path.replace('/smart-agent-api-demo', '/smart-agent-api-uat'),
				},
				'/dev-zgc-api-gw/gh-app-server': {
					target: 'http://192.168.1.69:8686',
					ws: true,
					changeOrigin: true, // 允许跨域
					rewrite: (path) => path.replace('/dev-zgc-api-gw/gh-app-server', '/dev-zgc-api-gw/gh-app-server'),
				},
				'/api-dev/writer': {
					target: 'http://192.168.1.69:8686',
					ws: true,
					changeOrigin: true, // 允许跨域
					rewrite: (path) => path.replace('/smart-agent-api-demo', ''),
				},
				'/gpt-server': {
					target: 'http://192.168.1.69:8686',
					ws: true,
					changeOrigin: true,
					rewrite: (path) => path.replace('/gpt-server/', '/'),
				},
				'/transcribe': {
					target: 'http://10.97.1.9:7017',
					ws: true,
					changeOrigin: true,
				},
				'/synthesize': {
					target: 'http://10.97.1.9:7021',
					ws: true,
					changeOrigin: true,
				},
				'/lhmz': {
					target: 'http://chengk.nat300.top',
					ws: true,
					changeOrigin: true,
				},
			},
		},
		build: {
			outDir: 'dist',
			chunkSizeWarningLimit: 1500,
			sourcemap: false,
			rollupOptions: {
				output: {
					entryFileNames: `assets/[name]-[hash].js`,
					chunkFileNames: `assets/[name]-[hash].js`,
					assetFileNames: `assets/[name].[hash].[ext]`,
					compact: true,
					manualChunks: {
						vue: ['vue', 'vue-router', 'pinia', 'vue-i18n'],
						echarts: ['echarts'],
					},
				},
			},
		},
		css: { preprocessorOptions: { css: { charset: false } } },
		define: {
			__VUE_I18N_LEGACY_API__: JSON.stringify(false),
			__VUE_I18N_FULL_INSTALL__: JSON.stringify(false),
			__INTLIFY_PROD_DEVTOOLS__: JSON.stringify(false),
			__VERSION__: JSON.stringify(process.env.npm_package_version),
			__GUIDE__: JSON.stringify(1001),
		},
	};
});

export default viteConfig;
