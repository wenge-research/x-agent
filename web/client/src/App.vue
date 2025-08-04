<template>
	<w-config-provider :locale="getGlobalI18n">
		<router-view />
		<Setings ref="setingsRef" />
		<!-- <CloseFull /> -->
		<!-- <Noice v-if="isNoice" /> -->
	</w-config-provider>
</template>

<script setup lang="ts" name="app">
import { defineAsyncComponent, computed, ref, onBeforeMount, onMounted, onUnmounted, nextTick, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useThemeConfig } from '/@/stores/themeConfig';
import { useKnowledgeState } from '/@/stores/knowledge';
import { Local, Session } from '/@/utils/storage';
import mittBus from '/@/utils/mitt';
import setIntroduction from '/@/utils/setIconfont';
import { useUserInfo } from '/@/stores/userInfo';
import { useChatStore } from '/@/stores/chat';
import { getApplication } from '/@/api/chat';
import { getCurTtsConfig } from '/@/utils/ttsConfig.ts';
const stores = useUserInfo();
// 引入组件
const Setings = defineAsyncComponent(() => import('/@/layout/navBars/breadcrumb/setings.vue'));
// const CloseFull = defineAsyncComponent(() => import('/@/layout/navBars/breadcrumb/closeFull.vue'));
// const Noice = defineAsyncComponent(() => import('/@/layout/noice/index.vue'));

const chatStore = useChatStore();
const knowledgeState = useKnowledgeState();
// 定义变量内容
const { messages, locale } = useI18n();
const setingsRef = ref();
const route = useRoute();
const router = useRouter();
const storesThemeConfig = useThemeConfig();
// const isNoice = computed(() => chatStore.isShowNoice);
const policyUrl: any = ref(localStorage.getItem(`${route.params.appId}appId`));
const judicialUrl: any = ref(import.meta.env.VITE_JUDICIAL_QA);
// 获取全局 i18n
const getGlobalI18n = computed(() => {
	return messages.value[locale.value];
});
// const orient = () => {
// 	if (window.orientation == 0 || window.orientation == 180) {
// 		let body = document.getElementsByTagName('body')[0];
// 		body.setAttribute('class', 'portrait');
// 		orientation = 'portrait';
// 		return false;
// 	} else if (window.orientation == 90 || window.orientation == -90) {
// 		let body = document.getElementsByTagName('body')[0];
// 		body.setAttribute('class', 'landscape');
// 		orientation = 'landscape';
// 		return false;
// 	}
// };
// 设置初始化，防止刷新时恢复默认
onBeforeMount(async () => {
	// 设置批量第三方 icon 图标
	setIntroduction.cssCdn();
	// 设置批量第三方 js
	setIntroduction.jsCdn();
});
// 应用基本信息查询
const getApplicationHandler = async () => {
	const responseData = await getApplication({ applicationId: route.params?.appId });
	if (responseData?.code === '000000') {
		console.log('111',responseData.data);
		localStorage.setItem(`${route.params?.appId}appId`, responseData.data?.applicationId);
		localStorage.setItem(`appId`, responseData.data?.applicationId);
    getCurTtsConfig(responseData.data?.applicationId)
		sessionStorage.setItem('curRouteId', responseData.data?.applicationId);
		sessionStorage.setItem('clientLink', responseData.data?.clientLink);
		sessionStorage.setItem('applicationCode', responseData.data?.applicationCode);
		sessionStorage.setItem('applicationQuickCommandList', responseData.data?.applicationQuickCommandList);
		sessionStorage.setItem('tenantId', responseData.data?.tenantId);
		sessionStorage.setItem('numberData', JSON.stringify({
			audioCount: responseData.data?.audioCount,
			fileCount: responseData.data?.fileCount,
			videoCount: responseData.data?.videoCount,
			imageCount: responseData.data?.imageCount,
		}));
		localStorage.setItem(`${route.params?.appId}`, JSON.stringify(responseData?.data));
		document.title = responseData.data ? responseData.data.webTitle : '';
		document.querySelector('meta[name="SiteName"]').content = import.meta.env.VITE_SITE_NAME;
		document.querySelector('meta[name="SiteDomain"]').content = import.meta.env.VITE_SITE_DOMAIN;
	} else {
		
	}
} 
// 页面加载时
onMounted(() => {
	console.log(1111111);

	window.addEventListener('message', (event) => {
		console.log(event.data, 'event.data是我啦');

		const isIframe = event.data.isIframe;
		const param1 = event.data.param1;
		// 基础模板只刷新接口配置 不刷新页面
		const isRfresh = event?.data?.refresh
		// userId: 大学城APP用户ID universityCityToken: 大学城用户token
		const userId = event.data.userId;
		// const universityCityToken = event.data.universityCityToken;
		if(userId) {
			sessionStorage.setItem('userId', userId);
		}
		// if(universityCityToken) {
		// 	sessionStorage.setItem('universityCityToken', universityCityToken);
		// }
		if (isIframe) {
			sessionStorage.setItem('isIframe', true);
			sessionStorage.setItem('wxAccessToken', '1_f3abf74f91284781ab60881c40a7987e');
		} else {
			sessionStorage.setItem('isIframe', '');
		}
		if(param1) {
			knowledgeState.setparam1(param1)
		}
		if(isRfresh) {
			getApplicationHandler();
		}
	});
	// orient();
	// setInterval(() => {
	// 	getNoticeFun();
	// }, 1000 * 60 * 10);
	nextTick(() => {
		window.parent.postMessage('ready', '*');
		// 监听布局配'置弹窗点击打开
		mittBus.on('openSetingsDrawer', () => {
			setingsRef.value.openDrawer();
		});
		// 获取缓存中的布局配置
		if (Local.get('themeConfig')) {
			storesThemeConfig.setThemeConfig({ themeConfig: Local.get('themeConfig') });
			document.documentElement.style.cssText = Local.get('themeConfigStyle');
		}
		// 获取缓存中的全屏配置
		if (Session.get('isTagsViewCurrenFull')) {
			stores.setCurrenFullscreen(Session.get('isTagsViewCurrenFull'));
		}
	});
	sessionStorage.setItem('accessToken', 'fd595fcd-5a0a-488d-8ba2-10dcfaa09629');
	sessionStorage.setItem('loginPlat', 'longyan-gpt-login');
	let curHerf = window.location.href;
	if (curHerf.indexOf(policyUrl.value) > -1) {
		sessionStorage.setItem('curRouteId', policyUrl.value);
	} else if (curHerf.indexOf(judicialUrl.value) > -1) {
		sessionStorage.setItem('curRouteId', judicialUrl.value);
	}
});
function getUrlParamsLegacy() {
	const search = window.location.search.substring(1);
	const params = search.split('&');
	const paramObject = {};

	for (let i = 0; i < params.length; i++) {
		const keyValue = params[i].split('=');
		if (keyValue.length === 2) {
			paramObject[decodeURIComponent(keyValue[0])] = decodeURIComponent(keyValue[1]);
		}
	}

	return paramObject;
}
// 页面销毁时，关闭监听布局配置/i18n监听
onUnmounted(() => {
	// mittBus.off('openSetingsDrawer', () => {});
	sessionStorage.removeItem("isH5")
	sessionStorage.removeItem('universityCityToken');
});
// 监听路由的变化，设置网站标题
// watch(
// 	() => route.path,
// 	() => {
// 		other.useTitle();
// 	},
// 	{
// 		deep: true,
// 	}
// );
watch(
	() => route.name,
	() => {}
);
</script>
<style lang="scss">
  /* 直接引入或编写全局样式 */
  @import './styles/global.scss';
  </style>