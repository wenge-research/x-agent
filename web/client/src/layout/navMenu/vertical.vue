<template>
	<w-menu
		:level-indent="16"	
		v-model:selected-keys= selectedKey
		v-model:open-keys= openKeys
		:collapsed ="state.isCollapse"
		:auto-open = "false"
		:accordion="getThemeConfig.isUniqueOpened"
		@menu-item-click="goto"
		style="height: 100%;width:100%;"
	>
		<template v-for="val in menuLists">
			<w-sub-menu v-if="val.children && val.children.length > 0" :key="val.name">
				<template #icon><SvgIcon :size="20" :name="selectedKey== val.name ? `${val.meta.icon}-xuanzhong` : `${val.meta.icon}-moren`" /></template>
				<template #title>
					<span>{{ $t(val.meta.title) }}</span>
				</template>
				<SubItem :chil="val.children" />
			</w-sub-menu>
			<template v-else>
				<w-menu-item :key="val.name">
					<template #icon><SvgIcon :size="20" :name="selectedKey== val.name ? `${val.meta.icon}-xuanzhong` : `${val.meta.icon}-moren`" /></template>
					<template v-if="!val.meta.isLink || (val.meta.isLink && val.meta.isIframe)">
						<span>{{ $t(val.meta.title) }}</span>
					</template>
					<template v-else>
						<a class="w100" @click.prevent="onALinkClick(val)">{{ $t(val.meta.title) }}</a>
					</template>
				</w-menu-item>
			</template>
		</template>
	</w-menu>
</template>

<script setup lang="ts" name="navMenuVertical">
import { defineAsyncComponent, reactive, computed, onMounted, watch,ref } from 'vue';
import { useRoute, useRouter,onBeforeRouteUpdate, RouteRecordRaw } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import other from '/@/utils/other';

// 引入组件
const SubItem = defineAsyncComponent(() => import('/@/layout/navMenu/subItem.vue'));

// 定义父组件传过来的值
const props = defineProps({
	// 菜单列表
	menuList: {
		type: Array<RouteRecordRaw>,
		default: () => [],
	},
});

// 定义变量内容
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const route = useRoute();
const router = useRouter();


const state = reactive({
	defaultActive: route.meta.isDynamic ? route.meta.isDynamicPath : route.path,
	isCollapse: false,
});



// 获取父级菜单数据
const menuLists = computed(() => {
	return <RouteItems>props.menuList;
});
// 获取布局配置信息
const getThemeConfig = computed(() => {
	return themeConfig.value;
});
// 菜单高亮（详情时，父级高亮）
const setParentHighlight = (currentRoute: RouteToFrom) => {
	const { path, meta } = currentRoute;
	const pathSplit = meta?.isDynamic ? meta.isDynamicPath!.split('/') : path!.split('/');
	if (pathSplit.length >= 4 && meta?.isHide) return pathSplit.splice(0, 3).join('/');
	else return path;
};
// 打开外部链接
const onALinkClick = (val: RouteItem) => {
	other.handleOpenLink(val);
};

const findMenuOpenKeys = (target: string) => {
	const result: string[] = [];
	let isFind = false;
	const backtrack = (item: RouteRecordRaw, keys: string[]) => {
		if (item.name === target) {
		isFind = true;
		result.push(...keys);
		return;
		}
		if (item.children?.length) {
		item.children.forEach((el) => {
			backtrack(el, [...keys, el.name as string]);
		});
		}
	};
	menuLists.value.forEach((el: RouteRecordRaw) => {
		if (isFind) return; // Performance optimization
		backtrack(el, [el.name as string]);
	});
	return result;
}

const openKeys = ref<string[]>([]);
const selectedKey = ref<string[]>([]);

const listenerRouteChange = (newRoute) => {
	const {  activeMenu } = newRoute.meta;
	const menuOpenKeys = findMenuOpenKeys(
		(activeMenu || newRoute.name) as string
	);
	const keySet = new Set([...menuOpenKeys, ...openKeys.value]);
	openKeys.value = [...keySet];

	selectedKey.value = [
	activeMenu || menuOpenKeys[menuOpenKeys.length - 1],
	];
}
const goto = (item) => {
	if (route.name === item ) {
		selectedKey.value = [item as string];
		return;
	}
	router.push({
		name: item,
	});
};

// 页面加载时
onMounted(() => {
	state.defaultActive = setParentHighlight(route);
});
// 路由更新时
onBeforeRouteUpdate((to) => {
	state.defaultActive = setParentHighlight(to);
	const clientWidth = document.body.clientWidth;
	if (clientWidth < 1000) themeConfig.value.isCollapse = false;
});
// 设置菜单的收起/展开
watch(
	themeConfig.value,
	() => {
		document.body.clientWidth <= 1000 ? (state.isCollapse = false) : (state.isCollapse = themeConfig.value.isCollapse);
	},
	{
		immediate: true,
	}
);
// 监听路由变化
watch(
	() => route.path,
	() => {
		setTimeout(() => {
			listenerRouteChange(route)
		},500)
		
	},
	{
		immediate: true,deep: true
	}
);
</script>
