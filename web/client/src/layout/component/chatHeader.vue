<template>
	<w-layout-header class="top-header" style="position: relative">
		<img v-if="logoUrl()" class="lh-logo" style="cursor: pointer" :src="logoUrl()" />
		<img v-else class="lh-logo" style="cursor: pointer" src="/src/assets/chatImages/pageTitle.svg" />
		<div style="display: flex; align-items: center">
			<!-- <div class="voice-setting" v-if="hasStreamVoice()">
				<iconpark-icon name="volume-down-line" v-if="chatStore.streamVoiceFlag " @click="stopPlayVoice" size="26" color="#626d68"></iconpark-icon>
				<iconpark-icon name="volume-mute-line" v-else @click="chatStore.streamVoiceFlag = true;" size="26" color="#626d68"></iconpark-icon>
			</div> -->
			<img src="/src/assets/chatTheme/home-3-line.svg" style="width: 20px; height: 20px; cursor: pointer; margin-right: 20px" @click="backHome" />
			<el-dropdown popper-class="moreMenu">
				<img src="/src/assets/chatTheme/more.svg" style="width: 20px; height: 20px; cursor: pointer" />
				<template #dropdown>
					<el-dropdown-menu>
						<el-dropdown-item @click="newChat">
							<img src="/src/assets/chatImages/newchat.svg" style="width: 18px; height: 18px; margin-right: 5px" />
							<span style="font-size: 16px; color: #181b49; font-weight: 500">新建对话</span>
						</el-dropdown-item>
						<el-dropdown-item @click="chatOpens" v-if="isHaveTtsId()">
							<img src="/src/assets/chatImages/phone.svg" style="width: 18px; height: 18px; margin-right: 5px" />
							<span style="font-size: 16px; color: #181b49; font-weight: 500">发起语音</span>
						</el-dropdown-item>
						<!-- <el-dropdown-item @click="changeHelper">
					<img src="/src/assets/chatTheme/changehelp.svg" style="width:18px;height:18px;margin-right:5px;">
					<span style="font-size: 16px;color: #181B49;font-weight:500;">更换助手</span>
				</el-dropdown-item> -->
						<!-- <el-dropdown-item>
					<img src="/src/assets/chatImages/personcenter.svg" style="width:18px;height:18px;margin-right:5px;">
					<span style="font-size: 16px;color: #181B49;font-weight:500;">个人中心</span>
				</el-dropdown-item> -->
					</el-dropdown-menu>
				</template>
			</el-dropdown>
		</div>
		<!-- <img @click="changeFontSize" style="cursor: pointer; height: 20px" title="切换字体大小" src="/src/assets/zc/zt.png" alt="切换字体大小" /> -->
	</w-layout-header>
</template>

<script setup lang="ts" name="layoutHeader">
import mittBus from '/@/utils/mitt';
import { ref } from 'vue';
import { useChatStore } from '/@/stores/chat';
const chatStore = useChatStore();
import { stopPlay } from '/@/utils/newVoiceFun';
import { useRoute, useRouter } from 'vue-router';
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
	router.push({
		path: '/previewChat/zgc',
	});
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
.top-header {
	height: 80px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-left: 32px;
	padding-right: 30px;
	background-image: url('/src/assets/chatTheme/headbg.png');
	background-size: 100% 100%;
	.lh-logo {
		width: 165px;
	}
	.voice-setting{
		position: relative;
		right: 20px;
		top:4px;
	  }
}
</style>
