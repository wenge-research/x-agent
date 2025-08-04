import { defineStore } from 'pinia';
/**
 * 布局配置
 * 修改配置时：
 * 1、需要每次都清理 `window.localStorage` 浏览器永久缓存
 * 2、或者点击布局配置最底部 `一键恢复默认` 按钮即可看到效果
 */
export const useThemeConfig = defineStore('themeConfig', {
	state: (): ThemeConfigState => ({
		themeConfig: {
			// 是否开启布局配置抽屉
			isDrawer: false,

			/**
			 * 全局主题
			 */
			// 默认 primary 主题颜色
			primary: '#2055f4',
			// 是否开启深色模式
			isIsDark: false,

			/**
			 * 顶栏设置
			 */
			// 默认顶栏导航背景颜色
			topBar: '#ffffff',
			// 默认顶栏导航字体颜色
			topBarColor: '#606266',

			/**
			 * 菜单设置
			 */
			// 默认菜单导航背景颜色
			menuBar: '#545c64',
			// 默认菜单导航字体颜色
			menuBarColor: '#eaeaea',
			// 默认菜单高亮背景色
			menuBarActiveColor: 'rgba(0, 0, 0, 0.2)',

			/**
			 * 界面设置
			 */
			// 是否开启菜单水平折叠效果
			isCollapse: false,
			// 是否开启菜单手风琴效果
			isUniqueOpened: true,
			// 是否开启固定 Header
			isFixedHeader: true,
			// 初始化变量，用于更新菜单 w-scrollbar 的高度，请勿删除
			isFixedHeaderChange: false,
			// 是否开启经典布局分割菜单（仅经典布局生效）
			isClassicSplitMenu: false,

			/**
			 * 界面显示
			 */
			// 是否开启侧边栏 Logo
			isShowLogo: true,
			// 初始化变量，用于 w-scrollbar 的高度更新，请勿删除
			isShowLogoChange: false,
			// 是否开启 Breadcrumb，强制经典、横向布局不显示
			isBreadcrumb: true,
			// 是否开启 Tagsview
			isTagsview: false,
			// 是否开启 Breadcrumb 图标
			isBreadcrumbIcon: false,
			// 是否开启 Tagsview 图标
			isTagsviewIcon: false,
			// 是否开启 TagsView 缓存
			isCacheTagsView: false,
			// 是否开启 TagsView 拖拽
			isSortableTagsView: true,
			// 是否开启 TagsView 共用
			isShareTagsView: false,
			// 是否开启 Footer 底部版权信息
			isFooter: false,
			// 是否开启灰色模式
			isGrayscale: false,
			// 是否开启水印
			isWartermark: true,
			// 水印文案
			wartermarkText: 'we-design-pro',

			/**
			 * 其它设置
			 */
			// Tagsview 风格：可选值"<tags-style-one|tags-style-four|tags-style-five>"，默认 tags-style-five
			// 定义的值与 `/src/layout/navBars/tagsView/tagsView.vue` 中的 class 同名
			tagsStyle: 'tags-style-five',
			// 主页面切换动画：可选值"<slide-right|slide-left|opacitys>"，默认 slide-right
			animation: 'slide-right',

			/**
			 * 布局切换
			 * 注意：为了演示，切换布局时，颜色会被还原成默认，代码位置：/@/layout/navBars/breadcrumb/setings.vue
			 * 中的 `initSetLayoutChange(设置布局切换，重置主题样式)` 方法
			 */
			// 布局切换：可选值"<defaults|classic|transverse>"，默认 defaults
			layout: 'transverse',

			/**
			 * 后端控制路由
			 */
			// 是否开启后端控制路由
			isRequestRoutes: false,

			/**
			 * 全局网站标题 / 副标题
			 */
			// 网站主标题（菜单导航、浏览器当前网页标题）
			// globalTitle: 'GPT智能客服-xxxxxxx政府在线',
			// 默认初始语言，可选值"<zh-cn|en|zh-tw>"，默认 zh-cn
			globalI18n: 'zh-cn',
			isVisible: false
		},
	}),
	actions: {
		setThemeConfig(data: ThemeConfigState) {
			this.themeConfig = data.themeConfig;
		},
	},
});
