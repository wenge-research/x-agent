<template>
  <div class="container_chat-longhua relative" :class="wrapClass" @click="openAllDialog">
    <w-layout :class="{ MobileH: !isMobile }">
      <w-layout-content v-if="previewData.active && route.params.appId != 0">
        <layoutCenterPdf v-if="previewData.active && route.params.appId != 0" />
      </w-layout-content>
      <w-layout-sider
        class="w-layout-sider-right flex zcflex"
        :resize-directions="previewData.active && route.params.appId != 0 ? ['left'] : []"
      >
        <div
          class="chatDivFlex"
          :class="{ chatDivFlex2: !isMobile }"
          :style="particlesContainerStyle"
        >
          <vue-particles
            id="wft-tsparticles"
            :particlesInit="particlesInit"
            :options="particlesOpt"
          />
          <div class="chat-content">
            <div class="top-logo" v-if="!isMobile">
              <div>
                <img class="gh" src="/src/assets/longhuaNewUI/guohui.png" alt="" />
                <img
                  v-if="logoUrl()"
                  class="lh-logo"
                  title="xxxxxxx政府在线"
                  @click="goToHome('https://www.szlhq.gov.cn/')"
                  :src="logoUrl()"
                  alt="xxxxxxx政府在线"
                />
              </div>
              <div class="right">
                <div class="change-box">
                  <CoolBanbenqiehuan size="18" color="#4085F4" />
                  <span @click="backOld">返回旧版</span>
                </div>
                <div class="voice-setting" v-if="hasStreamVoice()">
                  <iconpark-icon
                    name="volume-down-line"
                    v-if="chatStore.streamVoiceFlag"
                    @click="stopPlayVoice"
                    size="24"
                    color="#181b49"
                  ></iconpark-icon>
                  <iconpark-icon
                    name="volume-mute-line"
                    v-else
                    @click="chatStore.streamVoiceFlag = true"
                    size="24"
                    color="#181b49"
                  ></iconpark-icon>
                </div>
                <div class="font-setting">
                  <div>
                    字号设置：<span
                      @click="changeFontSize('2')"
                      :style="{ color: sizeType === '2' ? '#4186f4' : '#333333' }"
                      >大</span
                    >
                    |
                    <span
                      @click="changeFontSize('1')"
                      :style="{ color: sizeType === '1' ? '#4186f4' : '#333333' }"
                      >中</span
                    >
                    |
                    <span
                      @click="changeFontSize('0')"
                      :style="{ color: sizeType === '0' ? '#4186f4' : '#333333' }"
                      >小</span
                    >
                  </div>
                </div>
                <!-- <div class="chat-box" @click="historyDialogClick" title="历史对话">
									<img :src="chatLine" alt="历史对话" />
								</div> -->
                <!-- <div class="time_box">
									<div>{{ date }}&nbsp;&nbsp;</div>
									<div>{{ time }}</div>
								</div> -->
              </div>
            </div>

            <div class="time-center" v-if="!isMobile">
              <div class="right">
                <div class="voice-setting" v-if="hasStreamVoice()">
                  <iconpark-icon
                    name="volume-down-line"
                    v-if="chatStore.streamVoiceFlag"
                    @click="stopPlayVoice"
                    size="24"
                    color="#181b49"
                  ></iconpark-icon>
                  <iconpark-icon
                    name="volume-mute-line"
                    v-else
                    @click="chatStore.streamVoiceFlag = true"
                    size="24"
                    color="#181b49"
                  ></iconpark-icon>
                </div>
                <div class="font-setting">
                  <div>
                    字号设置：<span
                      @click="changeFontSize('2')"
                      :style="{ color: sizeType === '2' ? '#4186f4' : '#333333' }"
                      >大</span
                    >
                    |
                    <span
                      @click="changeFontSize('1')"
                      :style="{ color: sizeType === '1' ? '#4186f4' : '#333333' }"
                      >中</span
                    >
                    |
                    <span
                      @click="changeFontSize('0')"
                      :style="{ color: sizeType === '0' ? '#4186f4' : '#333333' }"
                      >小</span
                    >
                  </div>
                </div>
                <div class="time_box">
                  <div
                    style="
                      font-weight: bold;
                      color: #404467;
                      font-size: 20px;
                      margin-top: -4px;
                    "
                  >
                    {{ time }}
                  </div>
                  <div style="font-size: 14px; color: #404467">{{ date }}</div>
                  <div class="change-box">
                    <CoolBanbenqiehuan size="18" color="#4085F4" />
                    <span @click="backOld">返回旧版</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="chat-bottom" :class="{ 'chat-bottom-mobile': isMobile }">
              <layoutCenterRight v-if="!isMobile" />
              <LayoutCenter />
            </div>
          </div>
        </div>
      </w-layout-sider>
    </w-layout>
    <div
      class="footer-copyrightInfo"
      :class="{ 'footer-copyrightInfo-mobile': isMobile }"
      v-if="!isMobile"
    >
      <div class="copyrightInfo-inner">
        <div class="copyright-left" style="width: 495px">
          <p>
            <a
              href="https://www.szlhq.gov.cn/lhxinqu/fzlm/gywm/"
              title="关于我们"
              target="_blank"
              >关于我们</a
            >&nbsp;&nbsp;|&nbsp;&nbsp;
            <a
              href="https://www.szlhq.gov.cn/lhxinqu/fzlm/wzdt/"
              title="网站地图"
              target="_blank"
              >网站地图</a
            >&nbsp;&nbsp;|&nbsp;&nbsp;
            <a
              href="https://www.szlhq.gov.cn/lhxinqu/fzlm/wzsm/"
              title="版权保护"
              target="_blank"
              >版权保护</a
            >&nbsp;&nbsp;|&nbsp;&nbsp;
            <a
              href="https://www.szlhq.gov.cn/lhxinqu/fzlm/ysbh/"
              title="隐私声明"
              target="_blank"
              >隐私声明</a
            >
            &nbsp;&nbsp;|&nbsp;&nbsp;
            <a
              href="https://www.szlhq.gov.cn/wzasm/index.html"
              target="_blank"
              title="无障碍声明"
              ><span>无障碍声明</span></a
            >
          </p>
          <p>
            深圳市xxxxxxx区人民政府办公室主办<span style="margin-left: 30px; color: #666"
              >网站技术维护电话：0755-23332038</span
            >
          </p>

          <p></p>
        </div>

        <div class="copyright-right" style="width: 60%">
          <div class="copyrightR-info">
            <div class="tel">
              <p class="beian">
                <span
                  class="link"
                  @click="goToOtherLink('https://beian.miit.gov.cn/#/Integrated/index')"
                  >备案许可证号：粤ICP备17147563号-1</span
                >
              </p>
              <p class="beian" style="display: flex">
                <span
                  class="link"
                  style="display: flex"
                  @click="
                    goToOtherLink(
                      'http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=44030902000263'
                    )
                  "
                  >粤公网安备44030902000263号</span
                ><span style="margin-left: 6px; color: #333">网站标识码：4403920006</span>
              </p>
            </div>
            <div class="copyrightR-slogan">
              <span
                ><span
                  @click="
                    goToOtherLink(
                      'http://bszs.conac.cn/sitename?method=show&amp;id=278CB4A58F6C2E73E053012819AC872B'
                    )
                  "
                  ><img
                    id="imgConac"
                    style="max-width: 60px; cursor: pointer"
                    vspace="0"
                    hspace="0"
                    border="0"
                    :src="icon2"
                    alt="党政机关"
                    data-bd-imgshare-binded="1" /></span
              ></span>
              <span id="_span_jiucuo"
                ><span
                  @click="
                    goToOtherLink(
                      'https://zfwzgl.www.gov.cn/exposure/jiucuo.html?site_code=4403920006&url=http%3A%2F%2Fwww.szlhq.gov.cn%2Fxxgk%2Findex.html'
                    )
                  "
                  ><img
                    style="max-width: 120px; margin: 0; border: 0; cursor: pointer"
                    src="https://zfwzgl.www.gov.cn/exposure/images/jiucuo.png?v=4403920006"
                    role="imagelink"
                    tabindex="0"
                    aria-label="政府网站找错"
                    alt="政府网站找错"
                    setedaria="true"
                    id="aria2tq9wcbrpf8"
                    class="_e0f2a83eeffae7d02f75f35e50a5cac6" /></span
              ></span>
              <span><img style="max-width: 122px" :src="icon3" alt="无障碍" /></span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-drawer
      title="历史对话"
      v-model="historyDialog"
      direction="rtl"
      :before-close="handleClose"
      class="elDrawer"
      size="30%"
    >
      <historicalDialogue
        :historyDialog="historyDialog"
        @closeDialog="closeDialog"
      ></historicalDialogue>
    </el-drawer>
  </div>
</template>

<script lang="ts" setup>
import "splitpanes/dist/splitpanes.css";
import { computed, defineAsyncComponent, onMounted, onUpdated, ref, watch } from "vue";
import { useBasicLayout } from "/@/hooks/useBasicLayout";
import { useChatStore } from "/@/stores/chat";
import { useRoute } from "vue-router";
import { useKnowledgeState } from "/@/stores/knowledge";
import { fileTree, userKnowledgesSize } from "/@/api/knowledge";
import { Message } from "winbox-ui-next";
import icon1 from "/@/assets/img/P020180126374347718565.png";
import icon2 from "/@/assets/longhuaNewUI/red.png";
import icon3 from "/@/assets/img/wza2022120603.png";
import chatLine from "/@/assets/ai/chat-history-line.svg";
import particlesOpt from "./config/particles1";
import { loadSlim } from "tsparticles-slim";
import { stopPlay } from "/@/utils/newVoiceFun";
import { Modal } from "winbox-ui-next";
const knowledgeState = useKnowledgeState();
const previewData: any = computed(() => knowledgeState.previewData);
const chatStore = useChatStore();
import { formatDate } from "/@/utils/formatTime";
import axios from "axios";
const LayoutCenter = defineAsyncComponent(() => import("./components/layoutCenter.vue"));
const layoutCenterPdf = defineAsyncComponent(
  () => import("./components/layoutCenterPdf.vue")
);
const layoutCenterRight = defineAsyncComponent(
  () => import("./components/layoutCenterRight.vue")
);
const layoutCenterLeft = defineAsyncComponent(
  () => import("./components/layoutCenterLeft.vue")
);
const historicalDialogue = defineAsyncComponent(
  () => import("/@/views/lh-gpt/components/historicalDialogue.vue")
);
// 移动端自适应相关
const { isMobile } = useBasicLayout();
const historyDialog = ref(false);

const wrapClass = computed(() => {
  return [isMobile.value ? "container_chat_mobile" : ""];
});
const route = useRoute();
const openAllDialog = () => {
  chatStore.uploadDrawerVisible = false;
  chatStore.paramsDrawerVisible = false;
};
const particlesContainerStyle = computed(() => {
  return {
    width: "100%",
    height: "100%",
  };
});
const particlesInit = async (engine: any) => {
  await loadSlim(engine);
};

const getKnowledgesSize = async () => {
  let resSize = await userKnowledgesSize();
  if (resSize?.code === 200 && resSize?.data) {
    knowledgeState.setKnowledgesSize(resSize.data);
  }
};
const time = ref("");
const sizeType = ref("0");
const date = ref("");
const mainDomW = ref(0);
const curRouteId = ref("");
// 计算pane的宽度百分比
const calcPaneSize = () => {
  const mainDom: any = document.querySelector(".container_chat-longhua");
  mainDomW.value = mainDom.offsetWidth;
};
const getTree = async () => {
  let res = await fileTree(curRouteId.value);
  if (res?.code === 200 && res?.data) {
    knowledgeState.setFileList(res.data);
    getKnowledgesSize();
  } else {
    Message.warning(res.msg);
  }
};
const logoUrl = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo ? appInfo.logo : "";
};
const getIpFun = async () => {
  axios.get("https://api.ipify.org/").then((res) => {
    sessionStorage.setItem("ipAddress", res.data);
  });
};
const changeFontSize = (size: string) => {
  sizeType.value = size;
  window.document.documentElement.setAttribute("data-size", size);
};
// 点击返回旧版
const backOld = () => {
  window.open("https://www.szlhq.gov.cn/znjqr", "_blank");
};
const stopPlayVoice = () => {
  chatStore.streamVoiceFlag = false;
  stopPlay();
};
const hasStreamVoice = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo && appInfo.streamVoice === "是";
};
const goToOtherLink = (url) => {
  console.log(url);

  Modal.confirm({
    title: "szlhq.gov.cn 显示",
    content: `您访问的链接即将离开“xxxxxxx政府在线”门户网站，是否继续？`,
    closable: true,
    okText: "确定",
    cancelText: "取消",
    hideCancel: false,
    modalClass: "myConfirm",
    onOk: async () => {
      window.open(url, "_blank");
    },
  });
};
const goToHome = (url) => {
  window.open(url, "_blank");
};
const historyDialogClick = () => {
  historyDialog.value = true;
};
const handleClose = () => {
  historyDialog.value = false;
};
const closeDialog = (val) => {
  historyDialog.value = val;
};
onMounted(async () => {
  if (!route.params.conversationId) {
    await chatStore.addHistory({ appId: route.params.appId }, { name: "新建会话" });
  }
  curRouteId.value = sessionStorage.getItem("curRouteId") as string;
  calcPaneSize();
  setInterval(() => {
    date.value = formatDate(new Date(), "YYYY/mm/dd");
    time.value = formatDate(new Date(), "HH:MM:SS");
  }, 1000);
  // getTree();
  getIpFun();
});
// watch(
// 	() => route.path,
// 	(value) => {
// 		document.title = 'GPT智能客服-xxxxxxx政府在线';
// 	},
// 	{
// 		deep: true,
// 		immediate: true,
// 	}
// );
onUpdated(() => {
  sessionStorage.removeItem("ModalFlag");
});
</script>

<style lang="scss" scoped>
:deep(canvas) {
  // margin-top: 100px !important;
}
.container_chat-longhua {
  padding-bottom: 0;
  height: 100%;
  overflow: auto;
  /* 整个滚动条轨道 */
  ::-webkit-scrollbar {
    width: 3px; /* 垂直滚动条宽度 */
  }
  /* 滚动条滑块 */
  ::-webkit-scrollbar-thumb {
    background: #7bb4e0;
  }
}
:deep(.MobileH) {
  height: 100vh;
}
.container_chat_mobile {
  background: none;
}
.container_chat_bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: calc(100vh - 105px);
  background: url(../../assets/zc/bg.png) no-repeat;
  background-size: 100% 100%;
}
.chat-bg2 {
  background: url(../../assets/zc/bg.png) no-repeat;
  background-size: 100% 100%;
}
:deep(.splitpanes--vertical) {
  .splitpanes__splitter {
    width: 7px !important;
    background-color: var(--color-neutral-3);
  }
  & > .splitpanes__splitter:before,
  & > .splitpanes__splitter:after {
    width: 7px;
    // background-color: #8b93a1;
  }
}
.container_chat-longhua :deep(.w-layout-sider-children),
.container_chat-longhua :deep(.w-layout-content) {
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  //   color: #fff;
  //   font-size: 16px;
  //   font-stretch: condensed;
  //   text-align: center;
}
.container_chat-longhua .sfLayout {
  justify-content: center;
}
.container_chat-longhua :deep(.w-layout-sider-left) {
  width: 300px;
  min-width: 300px;
  max-width: 500px;
  box-shadow: 2px 0 6px rgb(0 21 41 / 1%);
  // background-color: rgba(255, 255, 255, 0.5);
}
.container_chat-longhua :deep(.w-layout-content) {
  // width: calc(100% - 1050px);
  flex: 1;
  max-width: calc(100vw - 1050px);
  // min-width:  20%;
  // background-color: rgba(255, 255, 255, 0.5);
}
.container_chat-longhua :deep(.w-layout-sider-right) {
  width: 59%;
  background: none;
  // background: #f4f6f9;
  // background-size: 100% 100%;
  &.zcflex {
    padding: 0 0 0 0 !important;
  }
  &.flex {
    width: 59% !important;
    padding: 56px 72px;
    flex: 1;
  }
  .chatDivFlex {
    display: flex;
    justify-content: space-between;
    width: 100%;
    height: 100%;
    overflow: hidden;
  }
  .chatDivFlex2 {
    height: calc(100% - 85px);
    // position: relative;
    background: #d5e2f2 url(https://www.szlhq.gov.cn/xinbandemo/znkfBg.png) no-repeat
      center center;
    background-size: 100% 100%;
    .chat-content {
      position: absolute;
      width: 79%;
      height: 100%;
      padding: 15px 0 30px;
      top: 0;
      left: 50%;
      transform: translateX(-50%);
      .top-logo {
        height: 9.5%;
        margin-bottom: 10px;
        display: flex;
        align-items: flex-end;
        justify-content: space-between;
        > div {
          height: 100%;
          display: flex;
          align-items: center;
        }
        .right {
          align-items: flex-start;
        }
        .gh {
          // height: 100%;
          margin-right: 20px;
        }
        .lh-logo {
          // height: 80.2%;
          cursor: pointer;
        }
        .right {
          display: flex;
          padding-top: 30px;
          .voice-setting {
            position: relative;
            right: 10px;
            top: 4px;
            cursor: pointer;
          }
          .font-setting {
            > div {
              width: 196px;
              height: 36px;
              border-radius: 18px;
              border: 1px solid #4186f4;
              font-size: 15px;
              font-family: MicrosoftYaHei;
              color: #000;
              display: flex;
              justify-content: center;
              align-items: center;
              span {
                cursor: pointer;
                margin: 0 5px;
              }
            }
          }
          .chat-box {
            margin-right: 16px;
            margin-top: 8px;
            line-height: 26px;
            height: 26px;
            cursor: pointer;
            img {
              width: 22px;
              height: 22px;
            }
          }
          .time_box {
            height: 36px;
            // padding: 0 8px;
            line-height: 36px;
            // font-size: 24px;
            // font-family: DINPro-Medium, DINPro;
            // font-weight: 500;
            // color: rgba(24, 27, 73, 1);
            display: flex;
            align-items: center;
            font-family: DINPro, DINPro;
            font-weight: 700;
            font-size: 18px;
            color: #181b49;
          }
          .change-box {
            width: 112px;
            height: 36px;
            display: flex;
            justify-content: center;
            align-items: center;
            border-radius: 18px;
            border: 1px solid #4186f4;
            font-size: 15px;
            font-family: PingFangSC, PingFang SC;
            font-weight: 400;
            color: #4186f4;
            margin-right: 15px;
            cursor: pointer;
            img {
              width: 14px;
              height: 16px;
              margin-right: 4px;
            }
          }
        }
      }
      .time-center {
        display: flex;
        justify-content: space-between;
        z-index: auto;
        position: absolute;
        width: 100%;
        height: 18.5%;
        display: none;
        .left {
          display: flex;
          height: 100%;
          .left-logo {
            height: 100%;
            z-index: 4;
            margin-top: 10px;
          }
          .title1 {
            // width: 357px;
            height: 37%;
          }
          .title2 {
            // width: 229px;
            height: 24%;
            position: absolute;
            top: 30.6%;
            right: 40px;
          }
          .tip {
            position: absolute;
            top: 7.7%;
            right: -95px;

            font-size: 14px;
            font-family: PingFangSC, PingFang SC;
            font-weight: 400;
            color: #4186f4;
            padding: 1px 4px;
            border-radius: 4px;
            border: 1px solid #4186f4;
          }
        }
        .right {
          display: flex;
          .voice-setting {
            position: relative;
            right: 10px;
            top: 4px;
            cursor: pointer;
          }
          .font-setting {
            margin-right: 40px;
            > div {
              width: 170px;
              height: 30px;
              background: #edf8fd;
              border-radius: 18px 18px 18px 18px;
              border: 1px solid #4186f4;
              font-size: 12px;
              font-family: MicrosoftYaHei;
              color: #333333;
              display: flex;
              justify-content: center;
              align-items: center;
              margin-top: 52px;
              span {
                cursor: pointer;
                margin: 0 5px;
              }
            }
          }
          .time_box {
            height: 56px;
            padding: 0 8px;
            font-size: 24px;
            font-family: DINPro-Medium, DINPro;
            font-weight: 500;
            color: rgba(24, 27, 73, 1);
            .change-box {
              display: flex;
              justify-content: center;
              align-items: flex-end;
              border-radius: 15px;
              border: 1px solid #4186f4;
              font-size: 14px;
              font-family: PingFangSC, PingFang SC;
              font-weight: 400;
              color: #4186f4;
              padding: 2px 10px;
              margin-top: 8px;
              cursor: pointer;
              img {
                width: 14px;
                height: 16px;
                margin-right: 4px;
              }
            }
          }
        }
      }

      .chat-bottom {
        position: absolute;
        left: 0;
        top: 11.5%;
        width: 100%;
        height: 85%;
        box-shadow: 0 0 10px rgba(93, 155, 255, 0.25);
        // background: rgba(225, 233, 245, 1);
        border-radius: 30px;
        border: 1px solid #ffffff;
        backdrop-filter: blur(1px);
        display: flex;
        justify-content: center;
        align-items: center;
        // flex: 1;
        // min-width: 0;
      }
    }
  }
}
:deep(.w-layout-sider-light) {
  box-shadow: none;
}

.footer-copyrightInfo {
  height: 94px;
  background: #fff;
  z-index: 1;
}

.copyrightInfo-inner {
  width: 70%;
  min-width: 1242px;
  height: 85px;
  margin: 0 auto;
  overflow: hidden;
  display: flex;
  align-items: center;
  *zoom: 1;
}

.copyright-left {
  width: 40%;
}

.copyright-right {
  width: 60%;
}

.copyright-left {
  float: left;
}

.copyright-left p,
.copyright-left a,
.copyright-right p {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

.copyright-left,
.copyright-right {
}

.copyright-right {
  float: right;
}

.copyrightR-info {
  float: right;
  width: auto;
}

.tel {
  float: left;
  margin-right: 10px;
}

.copyrightR-info p {
  text-align: left;
}

.copyrightR-slogan {
  float: right;
  margin-left: 0px;
  display: flex;
}

.copyrightR-slogan img {
  height: 60px;
}
a:hover {
  text-decoration: underline;
}
.beian {
  .link {
    cursor: pointer;
    color: #333;
  }
  .link:hover {
    text-decoration: underline;
  }
}
.footer-copyrightInfo-mobile {
}
.chat-bottom-mobile {
  height: calc(100vh - 64px);
  background: rgba(225, 233, 245, 1);
  border-radius: 24px 24px 0px 0px;
  border: 1px solid #ffffff;
}
</style>
<style>
.myConfirm {
  background: #292a2d;
  border: none;
}
.el-message-box__content {
  padding: 0 15px;
  margin-bottom: 10px;
}
.el-overlay.is-message-box .el-overlay-message-box {
  bottom: auto;
}
.el-message-box__title,
.el-message-box__message {
  color: #e8eaed;
}

.elDrawer {
  background: #f1f3f7;
  border-radius: 16px 0px 0px 16px;
  .el-drawer__header {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #383d47;
    margin-bottom: 0px;
  }
}
</style>
