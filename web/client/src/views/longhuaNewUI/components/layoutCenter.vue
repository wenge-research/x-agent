<template>
  <div class="relative flex flex-col h-full w-full" :class="{ centerMobile: isMobile }">
    <div class="center-side-parent" id="bg-main" :style="`height: 100%;`">
      <w-scrollbar ref="layoutScrollbarRef" class="center-side1">
        <div class="container-center">
          <div
            style=""
            :style="{
              overflow: isMobile ? 'hidden' : 'scroll',
              height: heightSide,
              marginTop: !isMobile ? '30px' : '',
            }"
            class="center-side"
            v-if="!loading"
          >
            <div class="message-list">
              <div class="question-box-longhua" v-if="!isMobile">
                <div class="left-txt">
                  <div class="box">
                    <div class="title" v-html="getAppDetail()?.greeting"></div>
                  </div>
                  <div class="quest-list">
                    <div
                      class="item"
                      :class="{ active: curQuestion == item.question }"
                      v-for="(item, index) in tjQuestList"
                      @click="sendQuestion(item.question)"
                      :key="index"
                    >
                      <span>{{ item.question }}</span>
                    </div>
                  </div>
                </div>

                <img
                  v-if="getAppDetail()?.virtualHumanLogo"
                  :src="getAppDetail()?.virtualHumanLogo"
                />
                <img v-else src="/src/assets/longhuaNewUI/e_big.png" alt="" />
              </div>
              <div
                style="display: flex; margin-bottom: 24px; padding: 9px"
                v-if="isMobile"
              >
                <div
                  style="
                    border: 1px solid #fff;
                    background: linear-gradient(
                        180deg,
                        rgba(116, 187, 250, 0.04) 0%,
                        rgba(64, 133, 244, 0.1) 100%
                      ),
                      #ffffff;
                    border-radius: 16px;
                    position: relative;
                  "
                  class="fontSize16 text-black min-w-[20px] text-box-1716763096571080706 px-4 py-2 message-request text-black border-right"
                >
                  <div
                    style="
                      display: flex;
                      justify-content: space-between;
                      align-items: center;
                      margin-bottom: 16px;
                    "
                  >
                    <div
                      v-if="getAppDetail()?.identityIcon"
                      :style="{ marginTop: isMobile ? '12px' : '' }"
                    >
                      <div>
                        <img
                          style="height: 18px"
                          :src="getAppDetail()?.identityIcon"
                          alt="智能问答助手"
                        />
                      </div>
                    </div>
                    <div style="position: absolute; right: 0; top: -9px">
                      <img
                        style="height: 64px"
                        src="/src/assets/zc/right-bg.png"
                        alt="智能问答助手"
                      />
                    </div>
                  </div>
                  <div style="color: #646479">
                    <div v-html="getAppDetail()?.greeting"></div>
                  </div>
                  <div class="quest-list-mobile">
                    <div
                      class="item"
                      v-for="(item, index) in tjQuestList"
                      @click="sendQuestion(item.question)"
                      :key="index"
                    >
                      <img
                        style="height: 18px; margin-right: 12px"
                        src="/src/assets/zc/send.svg"
                        alt=""
                      />
                      <div>{{ item.question }}</div>
                    </div>
                  </div>
                </div>
              </div>
              <div
                style="display: flex"
                :style="{
                  padding: isMobile ? ' 0 10px' : ' 0 40px',
                  marginTop: isMobile ? '0' : '35px',
                  marginRight: isMobile ? '0' : '80px',
                }"
              >
                <w-avatar v-if="!isMobile" style="margin-right: 18px" :size="62"
                  ><img :src="getAppDetail()?.robotIcon || BotAvatar"
                /></w-avatar>
                <div
                  style="border: 1px solid #fff; margin-top: 15px"
                  class="fontSize16 min-w-[20px]"
                  :class="[!isMobile && 'left-bg']"
                >
                  {{
                    getAppDetail()?.prologue ||
                    "您好，很高兴为您服务！请问有什么可以帮您的？"
                  }}
                </div>
              </div>
              <div
                :style="{ padding: isMobile ? ' 0 10px' : ' 0 30px' }"
                v-for="(item, index) of chatList"
                :key="index"
              >
                <YYMessageLonghua
                  :answer="item.answer"
                  :citations="item.citations || []"
                  :createTime="item.createTime"
                  :id="item.id + ''"
                  :dialogueFileIds="item.dialogueFileIds || []"
                  :dialogueFolderIds="item.dialogueFolderIds || []"
                  :paragraph="item.paragraph || ''"
                  :skillId="item.skillId || ''"
                  :param="item.param"
                  :question="item.question"
                  :loading="item.loading"
                  :inversion="true"
                  :isLast="false"
                  :key="index + 'r'"
                  :feedBackStatus="item.feedBackStatus"
                  :imgUrl="item.imgUrl"
                  :sensitive="item.sensitive"
                  :stopChatLoading="stopChatLoading"
                ></YYMessageLonghua>
                <YYMessageLonghua
                  :answer="item.answer"
                  :citations="item.citations || []"
                  :createTime="item.createTime"
                  :id="item.id + ''"
                  :dialogueFileIds="item.dialogueFileIds || []"
                  :dialogueFolderIds="item.dialogueFolderIds || []"
                  :paragraph="item.paragraph || ''"
                  :skillId="item.skillId || ''"
                  :param="item.param"
                  :question="item.question"
                  :loading="item.loading"
                  :isLast="index == chatList.length - 1"
                  :key="index + 'l'"
                  :feedBackStatus="item.feedBackStatus"
                  :imgUrl="item.imgUrl"
                  :sensitive="item.sensitive"
                  :stopChatLoading="stopChatLoading"
                  :suggestOrg="item.suggestOrg"
                  :contentQaType="item.contentQaType"
                  :answerFlag="item.answerFlag"
                  :plainText="item.plainText"
                ></YYMessageLonghua>
              </div>
            </div>
            <div class="text-center" :style="{ height: isMobile ? '56px' : '50px' }">
              <w-button
                type="outline"
                :loading="stopChatLoading"
                shape="round"
                class="stopBtn"
                :class="[!isMobile && 'stopBtnPC']"
                v-if="!isSensitive && dialogueLoading && !chatStore.uploadImgStatus"
                @click="handleclickStopChat()"
              >
                停止生成
              </w-button>
            </div>
            <div v-if="!isMobile" :style="{ 'padding-bottom': '60px' }"></div>
          </div>
          <ChatModule ref="chatModuleRef"></ChatModule>
        </div>
      </w-scrollbar>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, computed, onMounted, watch, onUnmounted } from "vue";
import { useBasicLayout } from "/@/hooks/useBasicLayout";
import { useChatStore } from "/@/stores/chat";
import { useThemeConfig } from "/@/stores/themeConfig";
import { storeToRefs } from "pinia";
import { useRoute } from "vue-router";
import { getPresetQuestionList } from "/@/api/knowledge";
const { isMobile, isxl } = useBasicLayout();
const chatStore = useChatStore();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
import { setScrollPosition } from "/@/utils/other";
import sendIcon from "/@/assets/chat/top.svg";
// const ChatModule = defineAsyncComponent(() => import('/@/views/chat/components/chatModule/index.vue'));
const ChatModule = defineAsyncComponent(() => import("./chatModule/index.vue"));

const dialogueLoading = computed(() => chatStore.dialogueLoading);
const loading = computed(() => chatStore.chatLoading);
const chatList = computed(() => chatStore.chatList);
const isSensitive = computed(() => chatStore.isSensitive);
const stopChatLoading = ref(false);
import BotAvatar from "/@/assets/zc/jqr.png";
const route = useRoute();
const maxWidth = ref(1000);
const rightNum = ref("8%");
const curQuestion = ref("");
const chatModuleRef = ref(null);
const tjQuestList = ref([]);
const handleclickStopChat = async () => {
  stopChatLoading.value = true;
  await chatStore.clearChat();
  stopChatLoading.value = false;
  chatStore.scrollbarBottom = true;
  setScrollPosition(isMobile.value ? ".message-list" : ".center-side");
};

const visible = computed(() => {
  return themeConfig.value.isVisible;
});
const handleClick = () => {
  themeConfig.value.isVisible = true;
};
const handleOk = () => {
  themeConfig.value.isVisible = false;
};
const handleCancel = () => {
  themeConfig.value.isVisible = false;
};
const gotoTop = () => {
  const dom = document.querySelector(".center-side") as HTMLElement;
  dom.scrollTop = 0;
};
const getAppDetail = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo ? appInfo : "";
};
watch(
  () => route.params.conversationId,
  () => {
    if (route.params.conversationId && route.params.conversationId != "" && isMobile) {
      themeConfig.value.isVisible = false;
    }
  },
  { immediate: true }
);
watch(
  () => route.params.appId,
  () => {
    if (route.params.appId && route.params.appId != "" && isMobile) {
      themeConfig.value.isCollapse = false;
    }
  },
  { immediate: true }
);
watch(
  route,
  () => {
    //切换路由清空会话记录
    chatStore.chatList = [];
    chatStore.dialogueLoading = false;
  },
  { immediate: true }
);
const heightSide = computed(() => {
  var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
  let browserList = [
    "MQQBrowser",
    "Opera",
    "Edg",
    "Firefox",
    "Chrome",
    "IEMobile",
    "wOSBrowser",
    "BlackBerry",
    "BrowserNG",
    "WebOS",
  ];
  let height = "calc(100% - 98px - 30px)";
  if (isMobile.value) {
    //判断是否Safari浏览器
    if (
      userAgent.indexOf("Safari") > -1 &&
      browserList.every((item) => userAgent.indexOf(item) == -1)
    ) {
      height = "calc(100% - 134px - 30px)";
    } else if (
      userAgent.indexOf("MicroMessenger") > -1 &&
      userAgent.indexOf("wxwork") == -1
    ) {
      height = "calc(100% - 68px - 30px)";
    } else if (
      userAgent.indexOf("MicroMessenger") > -1 &&
      userAgent.indexOf("wxwork") > -1
    ) {
      height = "calc(100% - 98px - 30px)";
    } else if (browserList.some((item) => userAgent.indexOf(item) == -1)) {
      height = "calc(100% - 78px - 30px)";
    } else {
      height = "calc(100% - 98px - 30px)";
    }
  }
  return height;
});
const userAgent = computed(() => {
  return navigator.userAgent;
});
const handleScroll = (event) => {
  const dom = document.querySelector(".center-side");
  // const dom = event
  const clientHeight = dom.clientHeight;
  const scrollTop = dom.scrollTop;
  const scrollHeight = dom.scrollHeight;
  if (clientHeight + scrollTop + 15 >= scrollHeight) {
    chatStore.scrollbarBottom = true;
  } else {
    chatStore.scrollbarBottom = false;
  }
};
const setHeight = ref(0);
const curRouteId = ref("");
onMounted(() => {
  curRouteId.value = sessionStorage.getItem("curRouteId") as string;
  // chatStore.getFeedbackList();
  resizeScrollHeight();
  resizeMaxWidth();
  updateWindowHeight();
  getPresetQuestionListFun();
  const dom = document.querySelector(".center-side");
  dom.addEventListener("scroll", handleScroll);
  window.addEventListener("resize", resizeScrollHeight);
  window.addEventListener("resize", resizeMaxWidth);
  window.addEventListener("resize", updateWindowHeight);
  // const cacheList = localStorage.getItem(`${route.params.appId}TotalList`) ? JSON.parse(localStorage.getItem(`${route.params.appId}TotalList`)) : []
  // chatStore.cacheChatList(cacheList);
});

//
const getPresetQuestionListFun = async () => {
  let res = await getPresetQuestionList({
    pageNo: 1,
    pageSize: 4,
    type: "推荐问题",
    status: 1,
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
  });
  tjQuestList.value = res.data.records;
};

const sendQuestion = async (item: string) => {
  curQuestion.value = item;
  console.log(curQuestion.value);

  if (chatStore.dialogueLoading) return;
  if (!route.params.conversationId) {
    await chatStore.addHistory({ appId: route.params.appId }, { name: "新建会话" });
  }
  const data: any = {
    content: item,
    conversationId: route.params.conversationId,
    knowledgeBaseId: route.params.appId,
    appId: route.params.appId,
  };
  chatStore.setChatList(data);
  return;
};
watch(
  () => chatStore.isOpen,
  () => {
    resizeMaxWidth();
  },
  {
    deep: true,
  }
);
watch(
  () => chatStore.collapsedMeun,
  () => {
    resizeMaxWidth();
  },
  {
    deep: true,
  }
);
const resizeScrollHeight = () => {
  let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
  let h = isMobile.value ? clientHeight - 64 - 36 : clientHeight - 64;
  setHeight.value = h;
};
const updateWindowHeight = () => {
  let windowHeight = window.innerHeight;
  let html = document.querySelector(":root") as any;
  if (windowHeight > 0 && html) {
    html.style.setProperty("--i-window-height", `${windowHeight}px`);
  }
};
const resizeMaxWidth = () => {
  let clientWidth = document.documentElement.clientWidth || document.body.clientWidth;
  let num = 0;
  if (chatStore.isOpen) {
    num = chatStore.rightFlag == 1 ? 300 : 440;
  } else {
    num = 0;
  }

  let w = chatStore.collapsedMeun ? clientWidth - num - 80 : clientWidth - num - 200;
  maxWidth.value = w > 1000 ? 1000 : w;
  rightNum.value = w > 1000 ? "8%" : "2.5%";
  let curW = (clientWidth - 1650) / 2 - 40;
  rightNum.value = clientWidth > 1650 && curW > 0 ? curW + "px" : "2.5%";
};

onUnmounted(() => {
  try {
    const dom = document.querySelector(".center-side");
    dom.removeEventListener("scroll", handleScroll);
  } catch (error) {}
});
</script>

<style scoped lang="scss">
@import "/@/theme/mixins/index.scss";

.containersMobile {
  height: calc(var(--i-window-height) - 134px);
}

.fade {
  position: absolute;
  height: 270px;
  width: calc(100% - 5px);
  z-index: 1;
  overflow: hidden;
  clip-path: inset(0px);
  pointer-events: none;

  &.bottom {
    bottom: 0;
  }

  .background {
    height: 100%;
    transition-property: transform;
    transition-duration: 1000ms;
    transition-timing-function: cubic-bezier(0.75, 0, 0.25, 1);

    .image {
      height: 100%;
      width: 100%;
      //background-image: url('../../../assets/chat/bg.png');
      background: #f4f6f9;
      // filter: blur(30px);
      -webkit-mask-image: -webkit-gradient(
        linear,
        0 181,
        0 0,
        from(rgba(0, 0, 0, 1)),
        color-stop(80%, rgba(0, 0, 0, 1)),
        to(rgba(0, 0, 0, 0))
      );
    }
  }
}

.stopBtn {
  background: #fff;
  border: none;
  border-radius: 8px;
  color: #9a99aa;
  cursor: pointer;

  &:hover {
    background: rgba(53, 94, 255, 0.06);
    color: #9a99aa;
  }
}

.stopBtn.w-btn-loading {
  border: none;
  background: #fff;
  border-radius: 8px;
  color: #9a99aa;
}

.stopBtnPC {
  display: block;
  width: 180px;
  height: 50px;
  background-color: #fff;
  text-align: center;
  line-height: 50px;
  border-radius: 10px;
  color: #666;
  margin: 20px auto;
  font-size: 12px;
  &:hover {
    background-color: #fff;
    color: #666;
  }
}

.SecondaryTitle {
  display: flex;
  align-items: center;
  justify-content: space-between;

  :deep(.w-drawer-body) {
    padding: 0;
  }
}

.isSensitiveLine {
  width: 100%;
  height: 50px;
  position: relative;
  padding-left: 36px;
  padding-right: 36px;
  margin-top: 15px;

  .line {
    width: calc((100% - 284px) / 2);
    display: inline-block;
    border-top: 1px solid #e4e8ee;
    height: 5px;
  }

  .text {
    display: inline-block;
    width: 260px;
    height: 20px;
    margin: 0 12px;
    color: #9a99aa;
    @include add-size($font-size-base14, $size);
  }
}

.center-side-parent {
  flex: 1;
  height: 100%;

  .container-center {
    height: 100%;
    width: 100%;
    position: relative;
    background: rgba(255, 255, 255, 0.15);
  }

  :deep(.w-scrollbar) {
    height: 100%;
  }

  :deep(.w-scrollbar-container) {
    position: relative;
    width: 100%;
    height: 100%;
  }

  .center-side1 {
    position: relative;
  }
}

.fontSize16 {
  @include add-size($font-size-base16, $size);
}
</style>
<style lang="scss">
@import "/@/theme/mixins/index.scss";

.secondaryTitleDrawer {
  :deep(.w-drawer-body) {
    padding: 0;
  }
}

.maxWidth800 .message-list .adaption {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  align-items: flex-start;
  position: relative;
  height: 65px;

  > div:nth-child(2) {
    position: absolute;
    top: 35px;
    right: 12px;
  }
}

.box {
  border-radius: 8px;
  width: 100%;
  // padding: 20px;
  padding-top: 28px;

  overflow: hidden;
  // background: url('/@/assets/zc/card.png') #f9f9f9 no-repeat;
  // background-size: cover;
}

.quest-list-mobile {
  .item {
    padding: 8px;
    margin-bottom: 8px;
    @include add-size(16px, $size);
    background: #fff;
    border-radius: 6px;
    cursor: pointer;
    font-family: MiSans, MiSans;
    color: #646479;
    display: flex;
    align-items: center;
  }
}

.question-box-longhua {
  display: flex;
  margin: 0 15px;
  background-size: cover;
  display: flex;
  align-items: center;
  padding: 10px 0 0 20px;

  .left-txt {
    width: 100%;
    padding: 20px;
    border: 1px solid #fff;
    border-radius: 40px;
    border-top-left-radius: 0;
    color: #333;
    box-shadow: 0 0 10px rgba(93, 155, 255, 0.35);
    background-color: rgba(255, 255, 255, 0.3);
    margin-top: 10px;
    margin-right: 8px;
  }

  .box {
    padding: 0;

    .title {
      @include add-size($font-size-base16, $size);
      line-height: 2;
      color: #333;
    }
  }

  .quest-list {
    margin-top: 15px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    flex-wrap: nowrap;

    .item {
      @include add-size($font-size-base16, $size);
      font-family: MicrosoftYaHei;
      color: #333;
      cursor: pointer;
      border-radius: 23px;
      padding: 0 25px;
      background: #fff;
      margin-right: 20px;
      margin-bottom: 10px;
      height: 46px;
      line-height: 46px;
      box-shadow: 0 0 10px rgba(93, 155, 255, 0.25);
    }

    .item.active,
    .item:hover {
      .box {
        box-shadow: 0px 5px 8px 0px rgba(45, 55, 85, 0.1);
        font-family: MicrosoftYaHei;
        color: #4085f4;

        img {
          display: block;
        }
      }
    }

    .item:last-child {
      margin-right: 0;
    }
  }

  .tip {
    padding: 2px 6px;
    background: rgba(64, 133, 244, 0.1);
    border-radius: 6px 6px 6px 2px;
    font-family: MicrosoftYaHei;
    font-size: 14px;
    color: #4085f4;
    margin-left: 6px;
  }
}

.centerMobile {
  .message-list {
    height: calc(100% - 80px);
    overflow: scroll;

    .title {
      height: 40px;
      line-height: 40px;
      font-weight: 500;
      color: #008dd6ff;
      font-size: 16px;
    }
  }
}
.left-bg {
  background: url("/src/assets/longhuaNewUI/QABg.png") no-repeat center;
  background-size: cover;
  line-height: 1.6;
  max-width: 90%;
  border-radius: 26px;
  border: 1px solid #fff;
  box-shadow: 0 0 10px rgba(93, 155, 255, 0.35);
  padding: 15px 30px;
  color: #000;
  border-top-left-radius: 0;
}
</style>
