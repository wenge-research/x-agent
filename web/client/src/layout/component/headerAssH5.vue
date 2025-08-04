<template>
	<w-layout-header class="top-header" style="position:relative;">
		<img class="lh-logo" style="cursor: pointer" :src="logoUrl()?logoUrl():'/src/assets/chatImages/pageTitle.svg'"/>
		<div class="voice-setting" v-if="hasStreamVoice()">
			<iconpark-icon name="volume-down-line" v-if="chatStore.streamVoiceFlag " @click="stopPlayVoice" size="26" color="#626d68"></iconpark-icon>
			<iconpark-icon name="volume-mute-line" v-else @click="chatStore.streamVoiceFlag = true;" size="26" color="#626d68"></iconpark-icon>
		</div>
    <div style="text-align: right;width: calc(100% - 150px);">
      <iconpark-icon v-if="virtualHumanLogoUrl()" name="home-3-line" size="24" color="#494C4F" style="margin-left: auto" @click="comeBack"></iconpark-icon>
      <el-dropdown popper-class="moreMenu" style="margin: 0 4px 0 28px !important;">
        <iconpark-icon name="align-left" color="#494C4F" size="24"></iconpark-icon>
        <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="newChat">
            <img src="/src/assets/chatImages/newchat.svg" style="width:18px;height:18px;margin-right:5px;">
            <span style="font-size: 16px;color: #181B49;font-weight:500;">新建对话</span>
          </el-dropdown-item>
          <el-dropdown-item @click="chatOpens" v-if="isHaveTtsId()">
            <img src="/src/assets/chatImages/phone.svg" style="width:18px;height:18px;margin-right:5px;">
            <span style="font-size: 16px;color: #181B49;font-weight:500;">发起语音</span>
          </el-dropdown-item>
          <el-dropdown-item @click="chatPersonalCenter">
            <img src="/src/assets/chatImages/account-circle-line.svg" style="width:18px;height:18px;margin-right:5px;">
            <span style="font-size: 16px;color: #181B49;font-weight:500;">个人中心</span>
          </el-dropdown-item>
          <el-dropdown-item @click="opendialog">
						<iconpark-icon name="information-line" size="18" style="margin-right:5px;"></iconpark-icon>
            <span style="font-size: 16px;color: #181B49;font-weight:500;">免责声明</span>
          </el-dropdown-item>
          <!-- <el-dropdown-item>
            <img src="/src/assets/chatImages/personcenter.svg" style="width:18px;height:18px;margin-right:5px;">
            <span style="font-size: 16px;color: #181B49;font-weight:500;">个人中心</span>
          </el-dropdown-item> -->
        </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
		<el-dialog v-model="dialogVisible" width="90%" top="50%" class="mzsmDialog">
			<div class="dialogTitle">免责声明</div>
			<div v-html="getmzsm()" style="padding:0 15px;line-height:24px;font-size:16px;color:#181b49;white-space:pre-wrap;"></div>
			<div style="text-align:center;margin-top:24px;padding:0 24px;">
				<el-button type="primary" @click="closedialog">我知道了</el-button>
			</div>
		</el-dialog>
		<!-- <img @click="changeFontSize" style="cursor: pointer; height: 20px" title="切换字体大小" src="/src/assets/zc/zt.png" alt="切换字体大小" /> -->
	</w-layout-header>
</template>

<script setup lang="ts" name="layoutHeader">
import mittBus from '/@/utils/mitt';
import { stopPlay } from '/@/utils/newVoiceFun';
import { ref } from 'vue';
import { useChatStore } from '/@/stores/chat';
const chatStore = useChatStore();
import { useRoute, useRouter } from 'vue-router';
const route = useRoute();
const router = useRouter();

const curStatus = ref(false);
const goToHome = (url) => {
	window.open(url, '_blank');
};
const dialogVisible = ref(false);
const opendialog = () => {
	dialogVisible.value = true;
};
const closedialog = () => {
	dialogVisible.value = false;
};
const getmzsm = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.disclaimer : '';
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
const virtualHumanLogoUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return !appInfo.virtualHumanLogo;
};
const isHaveTtsId = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo&&appInfo.voiceDialogueFlag=='是' ? true : false;
};

const newChat = () => {
	chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
};
const stopPlayVoice = () => {
  chatStore.streamVoiceFlag = false;
  stopPlay()
};
const hasStreamVoice = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
 	return appInfo && appInfo.streamVoice === '是';
};
const chatPersonalCenter = () => {
	router.push(`/assPersonalCenter/${getAppDetail()?.applicationCode}`);
}
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo : '';
};
const comeBack = () => {
	router.push(`/assistantHome/${getAppDetail()?.applicationCode}/`);
};
</script>
<style lang="scss">
.moreMenu{
	inset: 62px 10px auto auto !important;
	.el-dropdown-menu{
		padding: 8px 2px;
		.el-dropdown-menu__item{
			padding: 9px 10px;
		}
	}
}
</style>
<style scoped lang="scss">
:deep(.mzsmDialog) {
	border-radius: 12px;
	padding: 0;
	padding-bottom: 15px;

	.el-dialog__header {
		display: none;
	}

	.el-dialog__headerbtn {
		display: none;
	}

	.dialogTitle {
		font-weight: bold;
		font-size: 18px;
		color: #181b49;
		line-height: 24px;
		padding: 17px 0 12px 17px;
		background: linear-gradient(180deg, rgba(26, 109, 210, 0.1) 0%, rgba(26, 109, 210, 0) 100%);
	}

	p {
		font-size: 14px;
		color: #646479;
		line-height: 22px;
		padding: 0 29px;
		margin-bottom: 10px;
	}

	.el-button {
		width: calc(100% - 30px);
		padding: 24px 0;
		background: #fff !important;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 18px;
		color: #1a6dd2 !important;
		border: 1px solid #1a6dd2 !important;
		font-style: normal;
		border-radius: 24px;
	}
}
.top-header {
	height: 64px;
	display: flex;
	align-items: center;
	// justify-content: space-between;
	padding-left: 32px;
	padding-right: 22px;
	background-image: url('/src/assets/assistantH5/zhuli-bg.png');
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
.flex {
  display: flex;
  align-items: center;
}
</style>
