<template>
  <w-layout-header class="top-header" style="position: relative">
    <div class="logo-instru">
      <img
        v-if="logoUrl()"
        class="lh-logo"
        style="cursor: pointer"
        :src="logoUrl()"
      />
      <div>
        <div>网络普法小助手</div>
        <div class="qusetion">为您解答网络法律法规相关问题</div>
      </div>
    </div>

    <div style="display: flex; align-items: center">
      <!-- <div class="voice-setting" v-if="hasStreamVoice()">
				<iconpark-icon name="volume-down-line" v-if="chatStore.streamVoiceFlag " @click="stopPlayVoice" size="26" color="#626d68"></iconpark-icon>
				<iconpark-icon name="volume-mute-line" v-else @click="chatStore.streamVoiceFlag = true;" size="26" color="#626d68"></iconpark-icon>
			</div> -->
      <img
        src="/src/assets/chatTheme/home-4-line.svg"
        style="width: 20px; height: 20px; cursor: pointer; margin-right: 20px"
        @click="backHome"
      />
      <el-dropdown popper-class="moreMenu">
        <img
          src="/src/assets/chatTheme/more1.svg"
          style="width: 20px; height: 20px; cursor: pointer"
        />
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="newChat">
              <img
                src="/src/assets/chatImages/newchat.svg"
                style="width: 18px; height: 18px; margin-right: 5px"
              />
              <span style="font-size: 16px; color: #181b49; font-weight: 500"
                >新建对话</span
              >
            </el-dropdown-item>
            <!-- <el-dropdown-item @click="chatOpens" v-if="isHaveTtsId()">
              <img
                src="/src/assets/chatImages/phone.svg"
                style="width: 18px; height: 18px; margin-right: 5px"
              />
              <span style="font-size: 16px; color: #181b49; font-weight: 500"
                >发起语音</span
              >
            </el-dropdown-item> -->
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <!-- <img @click="changeFontSize" style="cursor: pointer; height: 20px" title="切换字体大小" src="/src/assets/zc/zt.png" alt="切换字体大小" /> -->
  </w-layout-header>
</template>

<script setup lang="ts" name="layoutHeader">
import mittBus from "/@/utils/mitt";
import { ref } from "vue";
import { useChatStore } from "/@/stores/chat";
const chatStore = useChatStore();
import { stopPlay } from "/@/utils/newVoiceFun";
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();
const curStatus = ref(false);
const goToHome = (url) => {
  window.open(url, "_blank");
};
const stopPlayVoice = () => {
  chatStore.streamVoiceFlag = false;
  stopPlay();
};
const hasStreamVoice = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo && appInfo.streamVoice === "是";
};
const changeHelper = () => {
  mittBus.emit("openHelper");
};
const changeFontSize = () => {
  curStatus.value = !curStatus.value;
  window.document.documentElement.setAttribute("data-size", curStatus.value ? 2 : 1);
};
const chatOpens = () => {
  mittBus.emit("chatOpen");
};
const logoUrl = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo ? appInfo.logo : "";
};

const isHaveTtsId = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo && appInfo.voiceDialogueFlag == "是" ? true : false;
};

const newChat = () => {
  chatStore.addHistory({ appId: route.params.appId }, { name: "新建会话" });
};
const getAppDetail = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo ? appInfo : "";
};
const backHome = () => {
  router.push(`/szPreviewChat/${getAppDetail()?.applicationCode}`);
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
  padding-left: 18px;
  padding-right: 30px;
  background-image: url("/src/assets/sz-cac/headbg.png");
  background-size: 100% 100%;
  .logo-instru {
    display: flex;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 16px;
    color: #ffffff;
    line-height: 20px;
    .qusetion {
      font-weight: 400;
      font-size: 14px;
      line-height: 18px;
      margin-top: 2px;
    }
  }
  .lh-logo {
    width: 40px;
    border: 1px solid #fff;
    border-radius: 20px;
    margin-right: 12px;
  }
  .voice-setting {
    position: relative;
    right: 20px;
    top: 4px;
  }
}
</style>
