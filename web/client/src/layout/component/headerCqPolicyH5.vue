<template>
  <w-layout-header class="top-header" style="position: absolute; top: 0">
    <div class="top-header-bg">
      <div class="flex header-left">
        <img class="arrow-up" src="/src/assets/chongqing/arrow-left-wide-line.svg" @click="homeclick"/>
        <div class="market">政策帮</div>
        <img class="bianzu-logo" src="/src/assets/chongqing/menu-2-fill.svg" />
      </div>
    </div>
	  <div class="bottom-bg"></div>

  </w-layout-header>
</template>

<script setup lang="ts" name="layoutHeader">
import mittBus from "/@/utils/mitt";
import { ref } from "vue";
import { useChatStore } from "/@/stores/chat";
const chatStore = useChatStore();
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

const curStatus = ref(false);
const goToHome = (url) => {
  window.open(url, "_blank");
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
	return appInfo ? appInfo : '';
};
const homeclick = () => {
  router.push(`/twoCitiesPlamChat/${getAppDetail()?.applicationCode}`);
}
</script>
<style lang="scss">
.moreMenu {
  inset: 62px auto auto 257px !important;
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
  width: 100%;
  .top-header-bg {
    height: 244px;
    width: 100%;
    display: flex;
    // align-items: center;
    justify-content: space-between;
    padding: 10px 25px;
    // padding-right: 42px;
    background-image: url("/src/assets/chongqing/headerBg.png");
    background-size: 100% 100%;
    .arrow-up {
      width: 20px;
    }
    .header-left {
      height: 40px;
      width: 100%;
      z-index: 9999;
    }
    .homePage {
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 22px;
      color: #383d47;
      line-height: 28px;
      margin-right: 20px;
    }
    .market {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 18px;
      color: #383d47;
      line-height: 24px;
      margin-right: 20px;
    }
    .bianzu-logo {
      width: 20px;
      height: 20px;
    }
  }
  .bottom-bg {
	width: 100%;
    height: 593px;
    background: #F0F6FC;
  }
}

.flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
