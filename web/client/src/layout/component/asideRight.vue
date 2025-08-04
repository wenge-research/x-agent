<template>
	<div class="right-aside">
		<div class="list" :class="isOpen ? 'list2': ''" >
			<div class="icon">
				<i @click="openList">
					<CoolArrowDownDLine  size="24" color="#272a31"/>
				</i>
			</div>
			<div>
				列表{{isMobile}}
			</div>
			
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
import mittBus from '/@/utils/mitt';
import { useBasicLayout } from '/@/hooks/useBasicLayout'

// 移动端自适应相关
const { isMobile,isxl } = useBasicLayout()

const isOpen = ref(false)

// 页面加载时
onMounted(() => {
	isOpen.value = !isxl.value
});

//监听屏幕宽度小于1200
watch(
	isxl,
	(v) => {   
		isOpen.value = !v
	},
	{
		deep: true,
	}
);

const openList = () => {
	isOpen.value = !isOpen.value
}


</script>
<style scoped lang="scss">
.right-aside{
	position: relative;
	z-index: 3;
	.list{
		width: 0px;
		height: 100%;
		position: absolute;
		right: 0;
		background: #fff;
		position: relative;
		.icon{
			position: absolute;
			top: 100px;
			left: -15px;
			width: 30px;
			height: 30px;
			border-radius: 50%;
			cursor: pointer;

		}
		
	}
	.list2{
		width: 300px;
		transition: width .2s cubic-bezier(.34,.69,.1,1);
	}
}
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