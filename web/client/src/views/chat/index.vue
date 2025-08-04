<template>
	<div class="container_chat relative" @click="openAllDialog" :class="wrapClass">
		<w-layout-sider hide-trigger collapsible :width="300" :collapsed-width="80" :collapsed="collapsed">
			<LayoutAside />
			<!-- <div class="btn" v-if="!collapsed" @click="collapsedChange"><CoolZhankai size="20" color="#9A99AA" /></div> -->
		</w-layout-sider>
		<LayoutCenter v-if="route.params.appId != 0" />
		<LayoutAsideRight v-if="route.params.appId != 0" v-show="!isMobile" />
		<AccountTip v-if="isFirstLogin" />
		<!-- <layoutCenterHf v-if="route.params.appId != 0" /> -->
	</div>
</template>

<script lang="ts" setup>
import { computed, defineAsyncComponent, onMounted, ref} from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { getPersonagePrice } from '/@/api/personal'
import { onBeforeRouteUpdate,useRoute,onBeforeRouteLeave } from 'vue-router';
import { useUserInfo } from '/@/stores/userInfo';
import { storeToRefs } from 'pinia';
import router from '/@/router';
const stores = useUserInfo();
const { userInfos } = storeToRefs(stores);
const AccountTip = defineAsyncComponent(() => import('/@/layout/accountTip/index.vue'));
const chatStore = useChatStore();
const LayoutAside = defineAsyncComponent(() => import('./components/LayoutAside.vue'));
const LayoutCenter = defineAsyncComponent(() => import('./components/layoutCenter.vue'));
const LayoutAsideRight = defineAsyncComponent(() => import('./components/LayoutAsideRight.vue'));
const layoutCenterHf = defineAsyncComponent(() => import('./components/layoutCenterHf.vue'));
// 移动端自适应相关
const { isMobile,isxl } = useBasicLayout();
const collapsed = computed(() => {
	return chatStore.collapsedMeun
});
const wrapClass = computed(() => {
	return [isMobile.value ? '' : 'flex'];
});
const route = useRoute();
const openAllDialog = () => {
	chatStore.uploadDrawerVisible = false
	chatStore.paramsDrawerVisible = false
}
const getAppIdLIst = computed(() => {
	let arr = ['0' ,'21'];
	chatStore.appTreeList.forEach((item) => {
		item.appList.forEach((i) => {
			arr.push(i.id)
		});
	});
	return arr;
});
const collapsedChange = () => {
	chatStore.collapsedMeun = true
}
const initPersonagePrice = async() => {
	try {
		const res = await getPersonagePrice(false)
		chatStore.currentBalance = res?.data.currentBalance
	} catch (err) {
		throw new Error(err)
	}
}

// 是否首次登陆
const isFirstLogin = computed(() => {
	let isFirst = false
	isFirst = stores.userInfos.firstLogin == 'true' ? true : false;
	return isFirst
});
const { appId } = route.params as { appId: string }
onMounted(() => {
	// let arr = getAppIdLIst.value.filter((item) => item == appId)
	// if(arr.length == 0){
	// 	router.push('/chat/0')
	// }
	if(route.name == 'chat'){
		if(appId != 21){
			router.push('/chat/21')
		}
	}
	initPersonagePrice()
})



onBeforeRouteUpdate((to) => {
	console.log(route.name)
	// let arr = getAppIdLIst.value.filter((item) => item == to.params.appId)
	// if(arr.length == 0){
	// 	router.push('/chat/0')
	// }
	if(route.name == 'chat'){
		if(to.params.appId != 21){
			router.push('/chat/21')
		}
	}
})
</script>

<style lang="scss" scoped>
.container_chat {
	padding-bottom: 0;
	height: 100%;
	justify-content: space-between;
	:deep(.w-layout-sider-children){
		overflow-x: hidden;
	}
	:deep(.w-layout-sider){
		background: rgba(255,255,255,0.4);
		background: #fff;
	}
	.left-side{
		height: 100%;
	}
	
	.btn{
		position: absolute;
		top: 30px;
		right: -22px;
		z-index: 11;
		width: 22px;
		height: 32px;
		border: 1px solid #e7e7e7;
		border-left: 0;
		border-radius: 0 4px 4px 0;
		cursor: pointer;
		background: #fff;
		display: flex;
		align-items: center;
		justify-content: center;
	}
}
</style>
