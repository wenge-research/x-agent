<template>
	<div class="left-side" v-show="!isTagsViewCurrenFull">
		<div class="layout-aside  h100" :class="setCollapseStyle">
			<w-scrollbar class="scrollbarWap" ref="layoutAsideScrollbarRef" outer-class="scrollbarOut flex-auto">
				<Vertical :menuList="state.menuList" />
			</w-scrollbar>
			
		</div>
	</div>
</template>

<script setup lang="ts" name="layoutAside">
import { defineAsyncComponent, reactive, computed, watch, onBeforeMount, ref, onMounted, nextTick } from 'vue';
import { storeToRefs } from 'pinia';
import pinia from '/@/stores/index';
import { useRoutesList } from '/@/stores/routesList';
import { useThemeConfig } from '/@/stores/themeConfig';
import { useTagsViewRoutes } from '/@/stores/tagsViewRoutes';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import mittBus from '/@/utils/mitt';
import router from '/@/router';
import { useRoute } from 'vue-router';
// 引入组件

const Vertical = defineAsyncComponent(() => import('/@/layout/navMenu/vertical.vue'));
const route = useRoute();
// 定义变量内容
const layoutAsideScrollbarRef = ref();
const stores = useRoutesList();
const storesThemeConfig = useThemeConfig();
const storesTagsViewRoutes = useTagsViewRoutes();
const { isMobile } = useBasicLayout();

const { routesList } = storeToRefs(stores);
const { themeConfig } = storeToRefs(storesThemeConfig);
const { isTagsViewCurrenFull } = storeToRefs(storesTagsViewRoutes);
const state = reactive<AsideState>({
	menuList: [],
	clientWidth: 0,
});

// 设置菜单展开/收起时的宽度
const setCollapseStyle = computed(() => {
	const {isCollapse, menuBar } = themeConfig.value;
	const asideBrTheme = ['#FFFFFF', '#FFF', '#fff', '#ffffff'];
	const asideBrColor = asideBrTheme.includes(menuBar) ? 'layout-w-aside-br-color' : '';
	// 判断是否是手机端
	if (isMobile) {
		if (isCollapse) {
			document.body.setAttribute('class', 'w-popup-parent--hidden');
			const asideEle = document.querySelector('.layout-container') as HTMLElement;
			const modeDivs = document.createElement('div');
			modeDivs.setAttribute('class', 'layout-aside-mobile-mode');
			asideEle.appendChild(modeDivs);
			modeDivs.addEventListener('click', closeLayoutAsideMobileMode);
			return [asideBrColor, 'layout-aside-mobile', 'layout-aside-mobile-open'];
		} else {
			// 关闭弹窗
			closeLayoutAsideMobileMode();
			return [asideBrColor, '', ''];
		}
	} else {
		// 其它布局给 64px
			if (isCollapse) return [asideBrColor, 'layout-aside-pc-64'];
			else return [asideBrColor, 'layout-aside-pc-220'];
	}
});

// 关闭移动端蒙版
const closeLayoutAsideMobileMode = () => {
	const el = document.querySelector('.layout-aside-mobile-mode');
	el?.setAttribute('style', 'animation: error-img-two 0.3s');
	setTimeout(() => {
		el?.parentNode?.removeChild(el);
	}, 300);
	const clientWidth = document.body.clientWidth;
	if (clientWidth < 768) themeConfig.value.isCollapse = false;
	document.body.setAttribute('class', '');
};
// 设置/过滤路由（非静态路由/是否显示在菜单中）
const setFilterRoutes = () => {
	if(route.fullPath.indexOf('/manage') > -1 ){
		let item = filterRoutesFun(routesList.value).filter((item) => item.path ==  '/manage')
		state.menuList = item[0].children;
	}else if(route.fullPath.indexOf('/personal') > -1){
		let item = filterRoutesFun(routesList.value).filter((item) => item.path ==  '/personal')
		state.menuList = item[0].children;
	}else{
		state.menuList = filterRoutesFun(routesList.value);
	}
	
};
// 路由过滤递归函数
const filterRoutesFun = <T extends RouteItem>(arr: T[]): T[] => {
	return arr
		.filter((item: T) => !item.meta?.isHide && item.meta?.isManage)
		.map((item: T) => {
			item = Object.assign({}, item);
			if (item.children) item.children = filterRoutesFun(item.children);
			return item;
		});
};
// 设置菜单导航是否固定（移动端）
const initMenuFixed = (clientWidth: number) => {
	state.clientWidth = clientWidth;
};
// 页面加载前
onBeforeMount(() => {
	initMenuFixed(document.body.clientWidth);
	setFilterRoutes();
	mittBus.on('setSendClassicChildren', (res: MittMenu) => {
		let { layout, isClassicSplitMenu } = themeConfig.value;
		if (layout === 'classic' && isClassicSplitMenu) {
			state.menuList = [];
			state.menuList = res.children;
		}
	});
	mittBus.on('getBreadcrumbIndexSetFilterRoutes', () => {
		setFilterRoutes();
	});
	mittBus.on('layoutMobileResize', (res: LayoutMobileResize) => {
		initMenuFixed(res.clientWidth);
		closeLayoutAsideMobileMode();
	});
});
// 页面加载时
onMounted(() => {
	nextTick(() => {
		setTimeout(() => {
			layoutAsideScrollbarRef.value.handleResize()
		}, 500);
});
});
// 监听 themeConfig 配置文件的变化，更新菜单 w-scrollbar 的高度
watch(themeConfig.value, (val) => {
	if (val.isShowLogoChange !== val.isShowLogo) {
		if (layoutAsideScrollbarRef.value) layoutAsideScrollbarRef.value.handleResize();
	}
});
// 监听 pinia 值的变化，动态赋值给菜单中
watch(
	pinia.state,
	(val) => {
		let { layout, isClassicSplitMenu } = val.themeConfig.themeConfig;
		if (layout === 'classic' && isClassicSplitMenu) return false;
		setFilterRoutes();
	},
	{
		deep: true,
	}
);
</script>
<style scoped>
.scrollbarOut{
	height: 100%;
}
:deep(.scrollbarWap){
	height: 100%;
	overflow: auto;
	
}
:deep(.w-scrollbar-track-direction-horizontal){
	display: none;
}
</style>