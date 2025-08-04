<template>
	<template v-for="val in chils">
		<w-sub-menu  :key="val.name" v-if="val.children && val.children.length > 0">
			<template #icon><SvgIcon :size="16" :name="val.meta.icon" /></template>
			<template #title>
				<span>{{ $t(val.meta.title) }}1</span>
			</template>
			<sub-item :chil="val.children" />
		</w-sub-menu>
		<template v-else>
			<w-menu-item  :key="val.name">
				<template #icon><div class="subMenutIcon"><CoolCheckboxBlankCircleFillWe  size="4" /></div></template>
				<template v-if="!val.meta.isLink || (val.meta.isLink && val.meta.isIframe)">
					<span class="subMenutTitle">{{ $t(val.meta.title) }}</span>
				</template>
				<template v-else>
					<a class="w100" @click.prevent="onALinkClick(val)">
						{{ $t(val.meta.title) }}
					</a>
				</template>
			</w-menu-item>
		</template>
	</template>
</template>

<script setup lang="ts" name="navMenuSubItem">
import { computed } from 'vue';
import { RouteRecordRaw } from 'vue-router';
import other from '/@/utils/other';

// 定义父组件传过来的值
const props = defineProps({
	// 菜单列表
	chil: {
		type: Array<RouteRecordRaw>,
		default: () => [],
	},
});

// 获取父级菜单数据
const chils = computed(() => {
	return <RouteItems>props.chil;
});
// 打开外部链接
const onALinkClick = (val: RouteItem) => {
	other.handleOpenLink(val);
};
</script>
