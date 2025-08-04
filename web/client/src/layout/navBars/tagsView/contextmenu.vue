<template>
	<transition class="w-popover-popup-content">
		<div
			class="w-dropdown-list-wrapper custom-contextmenu"
			:style="`top: ${dropdowns.y + 5}px;left: ${dropdowns.x}px;`"
			:key="Math.random()"
			v-show="state.isShow"
		>
			<ul class="w-dropdown-list">
				<template v-for="(v, k) in state.dropdownList">
					<li class="w-dropdown-option" :key="k" v-if="!v.affix" @click="onCurrentContextmenuClick(v.contextMenuClickId)">
						<SvgIcon class="w-dropdown-option-icon" :name="v.icon" />
						<span class="w-dropdown-option-content">{{ $t(v.txt) }}</span>
					</li>
				</template>
			</ul>
		</div>
	</transition>
</template>

<script setup lang="ts" name="layoutTagsViewContextmenu">
import { computed, reactive, onMounted, onUnmounted, watch } from 'vue';

// 定义父组件传过来的值
const props = defineProps({
	dropdown: {
		type: Object,
		default: () => {
			return {
				x: 0,
				y: 0,
			};
		},
	},
});

// 定义子组件向父组件传值/事件
const emit = defineEmits(['currentContextmenuClick']);

// 定义变量内容
const state = reactive({
	isShow: false,
	dropdownList: [
		{ contextMenuClickId: 0, txt: 'message.tagsView.refresh', affix: false, icon: 'cool-refresh-line-we' },
		{ contextMenuClickId: 1, txt: 'message.tagsView.close', affix: false, icon: 'cool-close-line-we' },
		{ contextMenuClickId: 2, txt: 'message.tagsView.closeOther', affix: false, icon: 'cool-close-circle-line-we' },
		{ contextMenuClickId: 3, txt: 'message.tagsView.closeAll', affix: false, icon: 'cool-delete-column-we' },
		{
			contextMenuClickId: 4,
			txt: 'message.tagsView.fullscreen',
			affix: false,
			icon: 'cool-fullscreen-line',
		},
	],
	item: {},
	arrowLeft: 10,
});

// 父级传过来的坐标 x,y 值
const dropdowns = computed(() => {
	// 117 为 `Dropdown 下拉菜单` 的宽度
	if (props.dropdown.x + 117 > document.documentElement.clientWidth) {
		return {
			x: document.documentElement.clientWidth - 117 - 5,
			y: props.dropdown.y,
		};
	} else {
		return props.dropdown;
	}
});
// 当前项菜单点击
const onCurrentContextmenuClick = (contextMenuClickId: number) => {
	emit('currentContextmenuClick', Object.assign({}, { contextMenuClickId }, state.item));
};
// 打开右键菜单：判断是否固定，固定则不显示关闭按钮
const openContextmenu = (item: RouteItem) => {
	state.item = item;
	item.meta?.isAffix ? (state.dropdownList[1].affix = true) : (state.dropdownList[1].affix = false);
	closeContextmenu();
	setTimeout(() => {
		state.isShow = true;
	}, 10);
};
// 关闭右键菜单
const closeContextmenu = () => {
	state.isShow = false;
};
// 监听页面监听进行右键菜单的关闭
onMounted(() => {
	document.body.addEventListener('click', closeContextmenu);
});
// 页面卸载时，移除右键菜单监听事件
onUnmounted(() => {
	document.body.removeEventListener('click', closeContextmenu);
});
// 监听下拉菜单位置
watch(
	() => props.dropdown,
	({ x }) => {
		if (x + 117 > document.documentElement.clientWidth) state.arrowLeft = 117 - (document.documentElement.clientWidth - x);
		else state.arrowLeft = 10;
	},
	{
		deep: true,
	}
);

// 暴露变量
defineExpose({
	openContextmenu,
});
</script>

<style scoped lang="scss">
.custom-contextmenu {
	transform-origin: center top;
	z-index: 2190;
	position: fixed;
	.w-dropdown-option {
		display: flex;
		align-items: center;
	}
}
</style>
