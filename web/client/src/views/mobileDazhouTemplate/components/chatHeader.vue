<template>
	<w-layout-header class="top-header-mobileUniversalTemplate" style="position: relative">
		<img v-if="logoUrl()" class="logo" @click="backHome" style="cursor: pointer" :src="logoUrl()" />
		<img v-else class="logo" style="cursor: pointer" src="/src/assets/chatImages/pageTitle.svg" />
		<iconpark-icon name="chat-new-line" color="#3F4247" size="22" style="cursor: pointer" @click="newChat"></iconpark-icon>
	</w-layout-header>
</template>

<script setup lang="ts" name="layoutHeader">
import mittBus from '/@/utils/mitt';
import { ref } from 'vue';
import { useChatStore } from '/@/stores/chat';
const chatStore = useChatStore();
import { stopPlay } from '/@/utils/newVoiceFun';
import { useRoute, useRouter } from 'vue-router';
const emit = defineEmits(['backHome']);

const route = useRoute();
const router = useRouter();
const curStatus = ref(false);
const goToHome = (url) => {
	window.open(url, '_blank');
};
const stopPlayVoice = () => {
  chatStore.streamVoiceFlag = false;
  stopPlay()
};
const hasStreamVoice = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
 	return appInfo && appInfo.streamVoice === '是';
};
const changeHelper = () => {
	mittBus.emit('openHelper');
};
const changeFontSize = () => {
	curStatus.value = !curStatus.value;
	window.document.documentElement.setAttribute('data-size', curStatus.value ? 2 : 1);
};
const chatOpens = () => {
	mittBus.emit('chatOpen');
};
const logoUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.logo : '';
};

const isHaveTtsId = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo && appInfo.voiceDialogueFlag == '是' ? true : false;
};

const newChat = () => {
	chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
};
const backHome = () => {
	emit('backHome')
};
</script>
<style lang="scss">
.moreMenu {
	inset: 62px 10px auto auto !important;
	.el-dropdown-menu {
		padding: 8px 2px;
		.el-dropdown-menu__item {
			padding: 9px 10px;
		}
	}
}
</style>
<style scoped lang="scss">
.top-header-mobileUniversalTemplate {
	height: 64px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 12px 20px 12px 12px;
	border-bottom: 1px solid rgba(0,0,0,0.12);
	background: rgba(255,255,255,.8);
	.logo {
		height: 40px;
	}
	.voice-setting{
		position: relative;
		right: 20px;
		top:4px;
	  }
}
</style>
