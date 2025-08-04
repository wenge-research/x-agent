<template>
	<div class="w-menu-horizontal-warp">
		<w-menu
			v-model:selected-keys="selectedKey"
			v-model:open-keys="openKeys"
			@menu-item-click="goto"
			:trigger-props="{ 'popup-offset': -2 }"
			mode="horizontal"
			v-if="route.name !== 'docs'"
		>
			<template v-for="val in menuLists">
				<w-sub-menu v-if="val.children && val.children.length > 0" :key="val.name">
					<template #icon><SvgIcon :size="16" :name="val.meta.icon" /></template>
					<template #title>
						<span>{{ $t(val.meta.title) }}</span>
					</template>
					<SubItem :chil="val.children" />
				</w-sub-menu>
				<template v-else>
					<w-menu-item :key="val.name" v-if="!storesUserInfo.offline || val.name != 'virtualHuman'">
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
	</div>
</template>

<script setup lang="ts" name="navMenuHorizontal">
import { defineAsyncComponent, reactive, computed, onMounted, nextTick, onBeforeMount, ref } from 'vue';
import { useRoute, useRouter, onBeforeRouteUpdate, RouteRecordRaw } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useRoutesList } from '/@/stores/routesList';
import { useThemeConfig } from '/@/stores/themeConfig';
import { useUserInfo } from '/@/stores/userInfo';
import other from '/@/utils/other';
import mittBus from '/@/utils/mitt';

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
const elMenuHorizontalScrollRef = ref();
const stores = useRoutesList();
const storesThemeConfig = useThemeConfig();
const { routesList } = storeToRefs(stores);
const { themeConfig } = storeToRefs(storesThemeConfig);
const route = useRoute();
const router = useRouter();
const state = reactive({
	defaultActive: '' as string | undefined,
});
const storesUserInfo = useUserInfo();
// 获取父级菜单数据
const menuLists = computed(() => {
	return <RouteItems>props.menuList;
});
// 路由过滤递归函数
const filterRoutesFun = <T extends RouteItem>(arr: T[]): T[] => {
	return arr
		.filter((item: T) => !item.meta?.isHide)
		.map((item: T) => {
			item = Object.assign({}, item);
			if (item.children) item.children = filterRoutesFun(item.children);
			return item;
		});
};
// 传送当前子级数据到菜单中
const setSendClassicChildren = (path: string) => {
	const currentPathSplit = path.split('/');
	let currentData: MittMenu = { children: [] };
	filterRoutesFun(routesList.value).map((v, k) => {
		if (v.path === `/${currentPathSplit[1]}`) {
			v['k'] = k;
			currentData['item'] = { ...v };
			currentData['children'] = [{ ...v }];
			if (v.children) currentData['children'] = v.children;
		}
	});
	return currentData;
};
// 设置页面当前路由高亮
const setCurrentRouterHighlight = (currentRoute: RouteToFrom) => {
	const { path, meta } = currentRoute;
	if (themeConfig.value.layout === 'classic') {
		state.defaultActive = `/${path?.split('/')[1]}`;
	} else {
		const pathSplit = meta?.isDynamic ? meta.isDynamicPath!.split('/') : path!.split('/');
		if (pathSplit.length >= 4 && meta?.isHide) state.defaultActive = pathSplit.splice(0, 3).join('/');
		else state.defaultActive = path;
	}
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
};

const openKeys = ref<string[]>([]);
const selectedKey = ref<string[]>([]);

const listenerRouteChange = (newRoute) => {
	const { activeMenu } = newRoute.meta;
	const menuOpenKeys = findMenuOpenKeys((activeMenu || newRoute.name) as string);
	const keySet = new Set([...menuOpenKeys, ...openKeys.value]);
	openKeys.value = [...keySet];

	selectedKey.value = [activeMenu || menuOpenKeys[menuOpenKeys.length - 1]];
};
const goto = (item) => {
	if (route.name === item) {
		selectedKey.value = [item as string];
		return;
	}
	router.push({
		name: item,
	});
};
// 打开外部链接
const onALinkClick = (val: RouteItem) => {
	other.handleOpenLink(val);
};
// 获取布局配置信息
const getThemeConfig = computed(() => {
	return themeConfig.value;
});
// 页面加载前
onBeforeMount(() => {
	listenerRouteChange(route);
});
// 页面加载时
onMounted(() => {});
// 路由更新时
onBeforeRouteUpdate((to) => {
	listenerRouteChange(to);
	// 修复经典布局开启切割菜单时，点击tagsView后左侧导航菜单数据不变的问题
	let { layout, isClassicSplitMenu } = themeConfig.value;
	if (layout === 'classic' && isClassicSplitMenu) {
		mittBus.emit('setSendClassicChildren', setSendClassicChildren(to.path));
	}
});
</script>

<style scoped lang="scss">
.w-menu-horizontal-warp {
	:deep(.w-menu-light) {
		background: none;
		.w-menu-item {
			font-size: 18px;
			background: none;
		}
		.w-menu-selected {
			&:hover {
				background: none;
			}
		}
	}
}
.scrollbarOut {
	width: 100%;
}
:deep(.scrollbarWap) {
	width: 100%;
	overflow-x: auto;
	overflow-y: hidden;
}
:deep(.w-scrollbar-track-direction-vertical) {
	display: none;
}
</style>
