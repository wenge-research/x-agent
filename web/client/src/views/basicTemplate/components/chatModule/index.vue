<template>
	<div :class="isMobile ? 'chatModule-mobile' : 'chatModule'" @click="stop" ref="$el">
		<ChatInput></ChatInput>
		<div class="chatModule-tips">内容由AI生成，无法确保真实准确，仅供参考</div>
		<!-- <getAudio></getAudio> -->
	</div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, defineExpose, ref, onMounted, onUnmounted } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
// const { isMobile } = useBasicLayout();
const isMobile = ref(false);
const $el = ref(null);
const $height = ref(400);
const observer = new ResizeObserver((entries) => {
	$height.value = Math.max.apply(
		null,
		entries.map((entry) => entry.contentRect.height)
	);
});
const curRouteId = ref('');
onMounted(() => {
	observer.observe($el.value);
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;

	updateWindowHeight();
	window.addEventListener('resize', updateWindowHeight);
});
onUnmounted(() => {
	observer.disconnect();
});
defineExpose({
	$height,
});
const ChatInput = defineAsyncComponent(() => import('./components/chatInput-new.vue'));
const getAudio = defineAsyncComponent(() => import('./components/getAudio.vue'));
const stop = (e) => {
	e.stopPropagation();
};
const updateWindowHeight = () => {
	let html = document.querySelector(':root') as any;
	if (html && isMobile.value) {
		let userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
		let browserList = ['MQQBrowser', 'Opera', 'Edg', 'Firefox', 'Chrome', 'IEMobile', 'wOSBrowser', 'BlackBerry', 'BrowserNG', 'WebOS'];
		if (userAgent.indexOf('iPhone') > -1 && browserList.every((item) => userAgent.indexOf(item) == -1)) {
			//判断是否Safari浏览器
			if (userAgent.indexOf('Safari') > -1) {
				html.style.setProperty('--i-bottom', `108px`);
			} else if (userAgent.indexOf('MicroMessenger') > -1 && userAgent.indexOf('wxwork') == -1) {
				html.style.setProperty('--i-bottom', `20px`);
			} else if (userAgent.indexOf('MicroMessenger') > -1 && userAgent.indexOf('wxwork') > -1) {
				html.style.setProperty('--i-bottom', `58px`);
			} else {
				html.style.setProperty('--i-bottom', `60px`);
			}
		} else {
			if (userAgent.indexOf('iPhone') > -1) {
				html.style.setProperty('--i-bottom', `50px`);
			} else {
				html.style.setProperty('--i-bottom', `70px`);
			}
		}
	}
};
</script>

<style scoped lang="scss">
.chatModule {
	position: absolute;
	// height: 128px;
	// width: calc(100% - 20px);
	width: 100%; // bottom: 16px;
	box-sizing: border-box;
	z-index: 100;
	left: 50%;
	transform: translateX(-50%);
	bottom: 0;
	&-tips {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 12px;
		color: #bcc1cc;
		line-height: 16px;
		text-align: center;
		margin-top: 6px;
	}
}

.chatModule-mobile {
	position: absolute;
	height: 74px;
	width: calc(100% - 20px);
	bottom: 30px;
	box-sizing: border-box;
	z-index: 100;
	left: 50%;
	transform: translateX(-50%);
}
</style>
