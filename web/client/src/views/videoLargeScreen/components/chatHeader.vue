<template>
	<w-layout-header class="top-header-mobileUniversalTemplate" style="position: relative">
		<img v-if="logoUrl()" class="logo" style="cursor: pointer" :src="logoUrl()" />
		<img v-else class="logo" style="cursor: pointer" src="/src/assets/chatImages/pageTitle.svg" />
		<!-- <div class="img">
			<iconpark-icon name="chat-new-line" color="#3F4247" size="22" style="cursor: pointer" @click="newChat"></iconpark-icon>
		</div> -->
		<div class="img" :style="'transform: rotate(180deg);'">
			<iconpark-icon  name="menu-2-fill" color="#3F4247" size="22" style="cursor: pointer" @click="showMenu"></iconpark-icon>
		</div>
		<!-- <div style="display: flex; align-items: center">
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
					</el-dropdown-menu>
				</template>
			</el-dropdown>
		</div> -->
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
import { ElMessage } from 'element-plus';
import {SuccessFilled} from '@element-plus/icons-vue'
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

const showMenu = () => {
	mittBus.emit('showMenu');
};
const newChat = () => {
	chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
	ElMessage({
		icon:SuccessFilled,
		message:'已是最新对话',
		customClass: 'custom-message',
	})
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
.top-header-mobileUniversalTemplate {
	height: 64px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 12px;
	border-bottom: 1px solid rgba(0,0,0,0.12);
	background: rgba(255,255,255,.8);
	.img{
		width: 40px;
		height: 40px;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.logo {
		height: 40px;
	}
	.voice-setting{
		position: relative;
		right: 20px;
		top:4px;
	  }
	  
}
.custom-message {
		background: rgba(51,51,51,0.8);
		color: #FFFFFF;
}
 
.custom-message .el-message__icon {
  color: #FFFFFF;
}
</style>
