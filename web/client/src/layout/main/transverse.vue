<template>
	<w-layout class="layout-container flex-center layout-backtop" >
		<!-- <LayoutHeader /> -->
		<w-layout class="layout-mian-height-50" style="height: 100%;" :class="{'manage-main': isManage}">
			<w-layout-sider v-if="isManage"><LayoutAside /></w-layout-sider>
			<w-layout-content class="flex-center layout-backtop">
				<LayoutMain ref="layoutMainRef"  />
			</w-layout-content>
		</w-layout>
	</w-layout>
</template>

<script setup lang="ts" name="layoutTransverse">
import { defineAsyncComponent, ref, watch, nextTick, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';

// 引入组件
const LayoutHeader = defineAsyncComponent(() => import('/@/layout/component/header.vue'));
const LayoutMain = defineAsyncComponent(() => import('/@/layout/component/main.vue'));
const LayoutAside = defineAsyncComponent(() => import('/@/layout/component/aside.vue'));
// 定义变量内容
const layoutMainRef = ref<InstanceType<typeof LayoutMain>>();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const route = useRoute();
const isManage = ref(false)

watch(route, () => {
	isManage.value = route.meta.isManage 
}, { immediate: true })
// 重置滚动条高度，更新子级 scrollbar
const updateScrollbar = () => {
	//layoutMainRef.value!.layoutMainScrollbarRef?.handleResize();
};
// 重置滚动条高度，由于组件是异步引入的
const initScrollBarHeight = () => {
	nextTick(() => {
		setTimeout(() => {
			updateScrollbar();
			// if (layoutMainRef.value!.layoutMainScrollbarRef) {
			// 	layoutMainRef.value!.layoutMainScrollbarRef.containerRef.scrollTop = 0;
			// }
			
		}, 500);
	});
};
// 页面加载时
onMounted(() => {
	initScrollBarHeight();
});
// 监听路由的变化，切换界面时，滚动条置顶
watch(
	() => route.path,
	() => {
		initScrollBarHeight();
	}
);
// 监听 themeConfig 配置文件的变化，更新菜单 w-scrollbar 的高度
watch(
	themeConfig,
	() => {
		updateScrollbar();
	},
	{
		deep: true,
	}
);
</script>

