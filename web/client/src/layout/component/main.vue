<template>
	<w-layout-content
		class="layout-main"
		:class="{ 'home-main-style ': isHome, 'bg-main': !isHome, 'overview-main': isOverview, isMobile: isMobile }"
		:style="isFixedHeader ? `height: calc(100% - ${setMainHeight})` : `minHeight: calc(100% - ${setMainHeight})`"
	>
		<div class="scrollbarOut layout-main-scroll layout-backtop-header-fixed">
			<LayoutParentView />
			<LayoutFooter v-if="isFooter" />
		</div>
		<w-back-top :target-container="setBacktopClass" />
	</w-layout-content>
</template>

<script setup lang="ts" name="layoutMain">
import { defineAsyncComponent, onMounted, computed, ref, watch, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import { NextLoading } from '/@/utils/loading';
import { useBasicLayout } from '/@/hooks/useBasicLayout';

// 引入组件
const LayoutParentView = defineAsyncComponent(() => import('/@/layout/routerView/parent.vue'));
const LayoutFooter = defineAsyncComponent(() => import('/@/layout/footer/index.vue'));

// 定义变量内容
const layoutMainScrollbarRef = ref();
const route = useRoute();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
import { Session } from '/@/utils/storage';
const { isMobile } = useBasicLayout();

// 设置 footer 显示/隐藏
const isFooter = computed(() => {
	return themeConfig.value.isFooter && !route.meta.isIframe;
});
// 设置 header 固定
const isFixedHeader = computed(() => {
	return themeConfig.value.isFixedHeader;
});
//设置 Backtop 回到顶部
const setBacktopClass = computed(() => {
	if (themeConfig.value.isFixedHeader) return `.layout-backtop-header-fixed .w-scrollbar-container`;
	else return `.layout-backtop .w-scrollbar-container`;
});
// 设置主内容区的高度
const setMainHeight = computed(() => {
	let height = ''
	if(isMobile.value) {
		if(route.path.indexOf('twoCitiesPlam-cq') != -1 || route.path.indexOf('policyHelp-cq') != -1 || route.path.indexOf('/basicTemplate/') != -1 || route.path.indexOf('mobileUniversalTemplate') != -1 || route.path.indexOf('mobileDazhouTemplate') != -1 || route.path.indexOf('videoLargeScreen') != -1 ) {
			height = '0px';
		} else if(route.path.indexOf('/partyMassScreen/') != -1) {
			height = '377px';
		} else {
			height = '64px';
		}
	} else {
		height = route.path.indexOf('/basicTemplate/') != -1 ? '0px' : '65px';
	}
	// return isMobile.value ? (route.path.indexOf('twoCitiesPlam-cq') != -1 || route.path.indexOf('policyHelp-cq') != -1) ? '0px' :'64px' : '65px';
	return height;
});
// 页面加载前
onMounted(() => {
	NextLoading.done(600);
	// if(Session.get('role') == 0){
	// 	setTimeout(() => {
	// 	initRoot()
	// 	}, 1000)
	// }
});
const initRoot = () => {
	const robo = new RobotClass(); // 初始化实例
	robo.init({
		// 传递参数 登录用户信息与机构信息
		productUserId: Session.get('userId'), // 登录用户id
		productCode: 'T14', // 产品编号
		productUserName: Session.get('userName'), // 登录用户名称
		tenantName: 'yayi', // 租户名称
		instId: '25b433c7713c4655bac88cc0a0ed4a58', // 机构id
		proName: '雅意问题反馈平台', // 产品名称
		mobile: Session.get('userNumber'), // 登录用户电话
	});

	setInterval(() => {
		robo.getNewMsg(); // 实时刷新数据
	}, 8000);
	window.onmessage = () => {
		// 小融窗口关闭事件监听
		if (event.data.refresh) {
			const dom = document.getElementById('dialogBox');
			dom && dom.parentNode.removeChild(dom);
		}
	};
};
const isHome = ref(false);
watch(
	route,
	() => {
		isHome.value = route.name === 'home';
	},
	{ immediate: true }
);

const isOverview = ref(false);
watch(
	route,
	() => {
		isOverview.value = route.name === 'personalOverview' || route.name === 'manageOverview';
	},
	{ immediate: true }
);

// 暴露变量
defineExpose({
	layoutMainScrollbarRef,
});
</script>
<style scoped>
.scrollbarOut {
	height: 100%;
}
:deep(.scrollbarWap) {
	height: 100%;
	overflow: auto;
}
.isMobile {

	background: none;
}
/* :deep(.w-scrollbar-track-direction-horizontal){
	display: none;
} */
</style>
