<template>
  <div class="ai-mian">
    <div class="side-box">
      <img
        class="img-logo"
        style="height: 40px; width: 40px; cursor: pointer"
        src="/src/assets/ai/img-logo.png"
        alt=""
        @click="backHome"
      />
      <div class="side-btn" style="background: #cddefd; margin-top: 32px">
        <img :src="iconSearch" alt="" />
        <p>搜索</p>
      </div>
      <!-- <div class="side-btn">
        <img :src="save" alt="" />
        <p>收藏</p>
      </div> -->
      <div class="user">
        <el-avatar :src="iconUser"></el-avatar>
      </div>
    </div>
    <div class="mian-box" v-if="isSearch">
      <img class="logo-img" src="/src/assets/ai/img-logo2.png" alt="" />
      <div class="tab-box">
        <!-- <div
          class="tab"
          v-for="(item, index) in options"
          :key="index"
          :class="{ active: activeName == item.id }"
          @click="handleActiveName(item.id)"
        >
          <img v-if="index == 0" :src="activeName == item.id ? ksAc : ks" />
          <img v-else-if="index == 1" :src="activeName == item.id ? zqAc : zq" />
          <img v-else :src="activeName == item.id ? yjAc : yj" />
          <span>{{ item.name }}</span>
        </div> -->
      </div>
      <div class="input-box">
        <!-- :fetch-suggestions="querySearch" -->
        <el-autocomplete
          class="inline-input"
          v-model="searchText"
          :fetch-suggestions="querySearch"
          placeholder="输入您要搜索的问题"
          :trigger-on-focus="false"
          @keydown.enter="sendSearch"
        ></el-autocomplete>
        <div class="btn-box audio">
          <div class="left-btn">
            <!-- <w-space style="background: #f4f6f9; border-radius: 8px">
              <img :src="appsFill" alt="" style="margin-left: 20px" />
              <span>类型：</span>
              <w-select :style="{ width: '100px' }" placeholder="全部" :bordered="false" v-model="searchType">
                <w-option style="color: #828894" v-for="(item, index) in searchOptions" :key="index" :value="item.id" :label="item.name"></w-option>
                
              </w-select>
            </w-space> -->
            <!-- <w-space style="margin-left: 20px">
              <img :src="calendarLine" alt="" />
              <span>时间： 不限</span>
            </w-space>
            <w-space style="margin-left: 20px">
              <img :src="linkM" alt="" />
              <span>来源： 全部</span>
            </w-space> -->
          </div>
          <w-space>
            <i class="vedioBtn" id="btn_control">
              <img
                src="/src/assets/chatImages/yuyin.svg"
                class="sendVoiceBtn"
                @click="openYY('ly')"
                v-if="
                  btnStatus === 'UNDEFINED' ||
                  btnStatus === 'CLOSED' ||
                  btnStatus === 'CLOSING'
                "
              />
              <div v-else class="vedioLoaingBtn" style="top: -2px">
                <div class="time-box">
                  <span class="start-taste-line">
                    <hr class="hr1" />
                    <hr class="hr2" />
                    <hr class="hr3" />
                    <hr class="hr4" />
                    <hr class="hr5" />
                    <hr class="hr6" />
                    <hr class="hr7" />
                    <hr class="hr8" />
                    <hr class="hr9" />
                  </span>
                </div>
                <CoolStopCircleLineWe
                  size="28"
                  color="var(--w-color-primary)"
                  @click="recorderStop"
                />
              </div>
            </i>
            <span class="search-btn" @click="sendSearch">
              <img :src="searchFFF" alt="" />
            </span>
          </w-space>
        </div>
      </div>
      <div class="question-tab" v-if="tjQuestList.length > 0">
        <w-space>
          <w-button
            type="secondary"
            shape="round"
            v-for="(item, index) in tjQuestList"
            :key="index"
            @click="sendLinkQuestion(item.question)"
          >
            <template #default>
              <img :src="corner" style="width: 16px; height: 16px"
            /></template>
            <template #icon>{{ item.question }} </template>
          </w-button>
          <w-button type="secondary" shape="round">
            <template #icon>
              <CoolRefreshLineWe size="14" color="#646479" />
            </template>
            <span @click="getPresetQuestionListFun(1)">换一批</span>
          </w-button>
        </w-space>
      </div>
      <div class="topic-box">
        <div class="topic-title" v-if="questionChartsTop50List.length">
          <img :src="hot" alt="" />
          <span>热门话题</span>
        </div>
        <w-list
          :gridProps="{ gutter: 0, span: 12 }"
          :bordered="false"
          style="border: none"
          v-if="questionChartsTop50List.length"
        >
          <w-list-item>
            <w-list>
              <w-list-item v-for="(item, index) in questionChartsTop50List" :key="index" @click="sendClickSearch(item.question)">
                <span class="sort">{{ index * 2 + 1 }}</span>
                <span class="text">{{ item.question }}</span>
              </w-list-item>
            </w-list>
          </w-list-item>
          <w-list-item v-if="questionChartsTop50ListData.length">
            <w-list>
              <w-list-item
                v-for="(item, index) in questionChartsTop50ListData"
                :key="index"
                @click="sendClickSearch(item.question)"
              >
                <span class="sort">{{ index * 2 + 2 }}</span>
                <span class="text">{{ item.question }}</span>
              </w-list-item>
            </w-list>
          </w-list-item>
        </w-list>
      </div>
     <!-- <div class="bottom-box">
        <span style="margin-right: 28px">已收集</span>
        <div class="file-item">
          <img :src="word" />
          <span>文档</span>
          <span class="data">{{ numberData.fileCount }} 个</span>
        </div>
        <div class="file-item">
          <img :src="audio" />
          <span>音频</span>
          <span class="data">{{ numberData.audioCount }} 个</span>
        </div>
        <div class="file-item">
          <img :src="piceture" />
          <span>图片</span>
          <span class="data">{{ numberData.imageCount }} 张</span>
        </div>
        <div class="file-item">
          <img :src="videooo" />
          <span>视频</span>
          <span class="data">{{ numberData.videoCount }} 个</span>
        </div>
      </div> -->
    </div>
    <div :class="['mian-box', 'mian-box1', 'flex-c', previewClicklist.transPdfUrl ? 'abbbb' : '']" v-if="!isSearch">
      <div>
        <div class="result" :class="{ isInput: showResultInput }">
          <!-- <w-space class="result-btn">
            <img :src="save" alt="" />
            <p>收藏</p>
          </w-space> -->
          <div class="inputRe-box">
            <div class="inputRe-content">
             <!-- <img
                :src="valueName == 'MIXED' ? ks : valueName == 'FULL_TEXT' ? zq : yj"
                class="icon"
              />
              <w-select
                v-model="valueName"
                :options="options"
                :field-names="fieldNames"
                :style="{ width: '120px' }"
                style="margin-left: 12px"
                placeholder="请选择"
                clearable
                :bordered="false"
              />
              <div class="line"></div> -->
              <w-input
                ref="textareaRef"
                v-model="searchText"
                placeholder="输入您要搜索的问题"
                :max-length="80"
                :style="{ width: '460px' }"
                @keydown.enter.prevent="sendSearch"
              />
              <div class="btn-box audio">
                <w-space>
                  <i class="vedioBtn" id="btn_control">
                    <img
                      src="/src/assets/chatImages/yuyin.svg"
                      class="sendVoiceBtn"
                      v-if="
                        btnStatus === 'UNDEFINED' ||
                        btnStatus === 'CLOSED' ||
                        btnStatus === 'CLOSING'
                      "
                      @click="openYY('ly')"
                    />
                    <div v-else class="vedioLoaingBtn">
                      <div class="time-box">
                        <span class="start-taste-line">
                          <hr class="hr1" />
                          <hr class="hr2" />
                          <hr class="hr3" />
                          <hr class="hr4" />
                          <hr class="hr5" />
                          <hr class="hr6" />
                          <hr class="hr7" />
                          <hr class="hr8" />
                          <hr class="hr9" />
                        </span>
                      </div>
                      <CoolStopCircleLineWe
                        size="28"
                        color="var(--w-color-primary)"
                        @click="recorderStop"
                      />
                    </div>
                  </i>
                  <w-button type="primary" class="sendBtn2" @click="sendSearch">
                    <template #icon>
                      <img :src="sendIcon" alt="" />
                    </template>
                  </w-button>
                </w-space>
              </div>
            </div>
          </div>
          <!-- <div class="input-title" v-show="!showResultInput">
            <span class="title">{{ searchSureText }}</span>
            <img :src="editLine" alt="" @click="showInputEdit" />
          </div> -->
        </div>
        <searchResultAI
          v-if="isShowResult"
          :loading="isSearch"
          :searchText="searchSureText"
          :searchType="searchType"
          :processLi="10"
		  :syFlag = "syFlag"
          :searchWay="isSearch ? activeName : valueName"
          @previewClickli="previewClickli"
        ></searchResultAI>
      </div>
      
    </div>
    
  </div>
</template>

<script lang="ts" setup>
import "splitpanes/dist/splitpanes.css";
import {
  computed,
  defineAsyncComponent,
  onMounted,
  onUpdated,
  ref,
  watch,
  reactive,
} from "vue";
import { useBasicLayout } from "/@/hooks/useBasicLayout";
import { useChatStore } from "/@/stores/chat";
import { useRoute } from "vue-router";
import { setScrollPosition } from "/@/utils/other";
import { getPresetQuestionList, questionChartsTop50, questionRealtimeRecommendTopN } from "/@/api/knowledge";
import { simpleTransferTcc } from "/@/utils/conv";
import { ElMessage } from "element-plus";
const chatStore = useChatStore();
const chatList = computed(() => chatStore.chatList);
const route = useRoute();

import user from "/@/assets/ai/user.svg";
import ks from "/@/assets/ai/ks.svg";
import ksAc from "/@/assets/ai/ks-ac.svg";
import zq from "/@/assets/ai/zq.svg";
import zqAc from "/@/assets/ai/zq-ac.svg";
import yj from "/@/assets/ai/yj.svg";
import yjAc from "/@/assets/ai/yj-ac.svg";
import search from "/@/assets/ai/search.svg";
import { fa, tr } from "element-plus/es/locale";
import sendIcon from "/@/assets/chatImages/send.svg";
import audio from "/@/assets/searchNew/folder-music-fill.svg";
import piceture from "/@/assets/searchNew/file-image-fill.svg";
import videooo from "/@/assets/searchNew/movie-2-fill.svg";
import word from "/@/assets/searchNew/file-list-3-fill.svg";
import corner from "/@/assets/ai/corner.svg";
import appsFill from "/@/assets/ai/apps-fill.svg";
import calendarLine from "/@/assets/ai/calendar-line.svg";
import iconSearch from "/@/assets/ai/icon-search.svg";
import iconUser from "/@/assets/ai/icon-user.svg";
import save from "/@/assets/ai/save.svg";
import linkM from "/@/assets/ai/link-m.svg";
import searchFFF from "/@/assets/ai/search-fff.svg";
import hot from "/@/assets/ai/hot.svg";
import editLine from "/@/assets/ai/edit-line.svg";

// 结果页
const searchResultAI = defineAsyncComponent(
  () => import("./components/searchNewResult.vue")
);
const layoutCenterPdfPdf = defineAsyncComponent(
  () => import("/src/components/previewWord.vue")
);

const datapanelParas = computed(() => chatStore.datapanelParas);
const DatapanelRef = ref();
const startT = ref(0);
const endT = ref(0);
const textareaRef: any = ref(null);
const isInsertPrompt = ref(false);
const insertpromptText = ref("");
const insertpromptbox = ref();
const syFlag = ref(false);
//  快速、增强，研究
const activeName = ref("MIXED");
// 搜索关键词
const searchText = ref("");
const searchSureText = ref("");
const curAsktext = ref("");
// 下拉库选择
const value = ref("全库搜索");
// 是否显示搜索结果页
const isSearch = ref(true);
const isShowResult = ref(false)
// 音频按钮状态
const btnStatus = ref("UNDEFINED");
// 推荐问题
const tjQuestList = ref([]);
const questionChartsTop50List = ref([]);
const questionChartsTop50ListData = ref([]);
const valueName = ref("MIXED");
const fieldNames = { value: "id", label: "name" };
const previewClicklist = ref({});
const options = reactive([
  {
    id: "MIXED",
    name: "混合检索",
  },
  {
    id: "FULL_TEXT",
    name: "全文检索",
  },
  {
    id: "SEMANTIC",
    name: "语义检索",
  },
]);

const searchOptions = reactive([
  {
    id: -1,
    name: "全部",
  },
  {
    id: 0,
    name: "文本",
  },
  {
    id: 1,
    name: "音频",
  },
  {
    id: 2,
    name: "图片",
  },
  {
    id: 3,
    name: "视频",
  },
]);
const searchType = ref(-1)
// 切换
const handleActiveName = async (data: any) => {
  activeName.value = data;
  valueName.value = data;
};
// 显示结果页输入框
const showResultInput = ref(false);
const showInputEdit = async () => {
  showResultInput.value = !showResultInput.value;
};

const querySearch = async (queryString: any, cb: any) => {
  let res = await questionRealtimeRecommendTopN({
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
    topNType: "count",
    question: searchText.value
  })
  res.data.forEach(item => {
    item.value = item.question;
  });
  let results = res.data || []
  // 调用 callback 返回建议列表的数据
  cb(results);
};

// 返回首页
const backHome = async () => {
  isSearch.value = true;
  chatStore.chatList = [];
  chatStore.dialogueLoading = false;
};
const pageNo = ref(1)
const sendClickSearch = (valueText) => {
  searchText.value = valueText;
  sendSearch();
}
const sendLinkQuestion = async (value) => {
  searchText.value = value;
  sendSearch();
}
const addpreviewClicklist = async () => {
  syFlag.value = false;
  previewClicklist.value = {}
}
// 进入搜索结果页
const sendSearch = async () => {
  // 重新搜索之前 清空对话
  if (searchText.value) {
    chatStore.chatList = [];
    chatStore.dialogueLoading = false;
    isSearch.value = false;
    searchSureText.value = searchText.value;
    isShowResult.value = true;
    console.log("rrrreee");
    sendQuestion(searchText.value);
  }
};
// 获取推荐问题
const getPresetQuestionListFun = async (type) => {
  if (type == 1) {
    pageNo.value = pageNo.value + 1;
  }
  tjQuestList.value = [];
  let res = await getPresetQuestionList({
    pageNo: pageNo.value,
    pageSize: 3,
    type: "推荐问题",
    status: 1,
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
  });
  if (res?.data?.records.length) {
    tjQuestList.value = res.data.records.filter((item) => item.question);
  }
};
// 获取问题前十
const questionChartsTop50Fun = async () => {
  questionChartsTop50List.value = [];
  questionChartsTop50ListData.value = [];
  let res = await questionChartsTop50({
    // sourceType: "PC",
    applicationId: localStorage.getItem(`${route.params.appId}appId`),

    // applicationId: "3f14fd90d9414ebe876045d13a67d83f",
    top50Type: "count",
    count: 10,
  });
  if (res?.data.length) {
    res.data = res.data.filter(item => item.question);
    questionChartsTop50List.value = res.data.filter(
      (item, index) => (index + 1) % 2 == 1
    );
    questionChartsTop50ListData.value = res.data.filter(
      (item, index) => (index + 1) % 2 == 0
    );
  }
};

// 搜索问题
const sendQuestion = async (item: string) => {
  searchSureText.value = item;
  searchText.value = item;
  isSearch.value = false;
  showResultInput.value = false;

  if (chatStore.dialogueLoading) return;
  if (!route.params.conversationId || route.params.conversationId == '' || chatStore.isSensitive) {
    chatStore.isSensitive = false;
    await chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
  }
  const data: any = {
    content: item,
    conversationId: route.params.conversationId,
    knowledgeBaseId: route.params.appId,
    appId: route.params.appId,
    searchWay: !isSearch.value ? valueName.value : activeName.value,
    searchType: searchType.value
  };
  chatStore.setChatList(data);
  return;
};
const recorder = new RecorderManager("./iat");
const audioPlayer = new AudioPlayer("./tts");
// 语音
const openYY = (type: string = "") => {
  searchText.value = "";
  btnControlFun(type);
};
// 录音相关开始
const btnControlFun = (type: string = "") => {
  if (btnStatus.value === "UNDEFINED" || btnStatus.value === "CLOSED") {
    connectWebSocket(type);
  } else {
    // 结束录音
    recorder.stop();
  }
};
// 暂停录音
const recorderStop = () => {
  recorder.stop();
  changeBtnStatus("CLOSED");
};
const chatFlag = ref(false);
const chatSBFlag = ref(false);
const resultVal = ref("");

let APPID =  '';
let API_SECRET =  '';
let API_KEY =  '';
let iatWS;
let resultText = "";
let resultTextTemp = "";
let countdownInterval;
const textFileIds: any = ref([]);
const textFolderIds: any = ref([]);
const instructObj: any = ref({});
const numberData: any = ref({});
const getWebSocketUrl = () => {
  // 请求地址根据语种不同变化
  var url = "wss://iat-api.xfyun.cn/v2/iat";
  var host = "iat-api.xfyun.cn";
  var apiKey = API_KEY;
  var apiSecret = API_SECRET;
  var date = new Date().toGMTString();
  var algorithm = "hmac-sha256";
  var headers = "host date request-line";
  var signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v2/iat HTTP/1.1`;
  var signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret);
  var signature = CryptoJS.enc.Base64.stringify(signatureSha);
  var authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
  var authorization = btoa(authorizationOrigin);
  url = `${url}?authorization=${authorization}&date=${date}&host=${host}`;
  return url;
};
const connectWebSocket = (type: string = "") => {
  const tts_config = getRandomConfig();
  APPID =  tts_config.APPID;
  API_SECRET =  tts_config.API_SECRET;
  API_KEY =  tts_config.API_KEY;
  const websocketUrl = getWebSocketUrl();
  if ("WebSocket" in window) {
    iatWS = new WebSocket(websocketUrl);
  } else if ("MozWebSocket" in window) {
    iatWS = new MozWebSocket(websocketUrl);
  } else {
    alert("浏览器不支持WebSocket");
    return;
  }
  changeBtnStatus("CONNECTING");
  iatWS.onopen = (e) => {
    // 开始录音
    if (type == "toChat") {
      chatFlag.value = true;
    }
    recorder.start({
      sampleRate: 16000,
      frameSize: 1280,
    });
    var params = {
      common: {
        app_id: APPID,
      },
      business: {
        language: "zh_cn",
        domain: "iat",
        accent: "mandarin",
        vad_eos: 2400000,
        dwa: "wpgs",
      },
      data: {
        status: 0,
        format: "audio/L16;rate=16000",
        encoding: "raw",
      },
    };
    iatWS.send(JSON.stringify(params));
    changeBtnStatus("OPEN");
  };
  iatWS.onmessage = (e) => {
    chatSBFlag.value = true;
    renderResult(e.data);
  };
  iatWS.onerror = (e) => {
    ElMessage.error("录音异常，请检查是否插入麦克风设备及开启麦克风权限");
    recorder.stop();
    changeBtnStatus("CLOSED");
  };
  iatWS.onclose = (e) => {
    recorder.stop();
    changeBtnStatus("CLOSED");
  };
};
const changeBtnStatus = (status) => {
  btnStatus.value = status;
  if (status === "CONNECTING") {
    resultVal.value = "建立连接中";
    searchText.value = "";
    resultText = "";
    resultTextTemp = "";
  } else if (status === "OPEN") {
    countdown();
  } else if (status === "CLOSING") {
    resultVal.value = "关闭连接中";
  } else if (status === "CLOSED") {
    searchText.value = "";
    resultVal.value = "开始录音";
  }
};
const countdown = () => {
  let seconds = 60;
  countdownInterval = setInterval(() => {
    seconds = seconds - 1;
    if (seconds <= 0) {
      clearInterval(countdownInterval);
      recorder.stop();
    } else {
      resultVal.value = `录音中（${seconds}s）`;
    }
  }, 1000);
};
const renderResult = (resultData) => {
  console.log("识别结束识别结束", resultData);
  recorder.stop();
  changeBtnStatus("CLOSED");
  // 识别结束
  let jsonData = JSON.parse(resultData);
  if (jsonData.data && jsonData.data.result) {
    let data = jsonData.data.result;
    let str = "";
    let ws = data.ws;
    for (let i = 0; i < ws.length; i++) {
      str = str + ws[i].cw[0].w;
    }
    // 开启wpgs会有此字段(前提：在控制台开通动态修正功能)
    // 取值为 "apd"时表示该片结果是追加到前面的最终结果；取值为"rpl" 时表示替换前面的部分结果，替换范围为rg字段
    if (data.pgs) {
      if (data.pgs === "apd") {
        // 将resultTextTemp同步给resultText
        resultText = resultTextTemp;
      }
      // 将结果存储在resultTextTemp中
      resultTextTemp = resultText + str;
    } else {
      resultText = resultText + str;
    }
    searchText.value = resultTextTemp || resultText || "";
  }
  searchText.value = searchText.value.trim();
  console.log(searchText.value);
  if (searchText.value) {
    // setInput();
    sendQuestion(searchText.value);
  } else {
    chatSBFlag.value = false;
  }
  if (jsonData.code === 0 && jsonData.data.status === 2) {
    iatWS.close();
  }
  if (jsonData.code !== 0) {
    iatWS.close();
  }
};

const previewClickli = async (value: string) => {
  previewClicklist.value = {};
  setTimeout(() => {
    previewClicklist.value = value;
  }, 10);
  
};
watch(
  route,
  () => {
    //切换路由清空会话记录
    chatStore.chatList = [];
    chatStore.dialogueLoading = false;
  },
  { immediate: true }
);

onMounted(async () => {
  if (!route.params.conversationId) {
		await chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
	}
  numberData.value = JSON.parse(sessionStorage.getItem("numberData")) as Object;
  getPresetQuestionListFun();
  questionChartsTop50Fun();
});
onUpdated(() => {});
</script>

<style lang="scss" scoped>
.ai-mian {
  padding-bottom: 0;
  height: 100%;
  background: url("/@/assets/ai/ai-bg.png") no-repeat;
  background-size: 100% 100%;
  position: relative;
  display: flex;
}
.bgstyle {
  // background: #f5fafd;
  background: linear-gradient(#ffffff, #f5fafd);
}
.user-box {
  display: flex;
  justify-content: space-between;
  height: 72px;
  padding: 20px;

  .user-info {
    display: flex;
    justify-content: center;
    align-items: center;

    img {
      margin-left: 5px;
    }
  }
}
.mian-box {
  width: calc(100% - 70px);
  height: calc(100% - 40px);
  margin: 20px 20px 20px 0px;
  background: #f4f9ff;
  border-radius: 16px;
  backdrop-filter: blur(3px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;

  .logo-img {
    margin-top: 5vh;
    height: 52px;
    width: 203px;
  }
  .tab-box {
    width: 340px;
    height: 40px;
    // background: #ffffff;
    border-radius: 14px;
    font-size: 14px;
    margin: 40px 0px 24px 0px;
    color: #434649;
    display: flex;
    justify-content: space-around;
    align-items: center;

    .tab {
      width: 110px;
      height: 32px;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;

      img {
        width: 16px;
        height: 16px;
        margin-right: 5px;
      }
    }
    .active {
      color: #fff;
      background: #2a70f5;
      border-radius: 8px;
      border: 2px solid rgba(43, 153, 255, 0.1);
    }
    .disable {
      // background: #f0f2f5;
      border-radius: 8px;
      color: #999;
      cursor: auto;
    }
  }
  .question-tab {
    width: 720px;
    margin-top: 26px;
  }
  .bottom-box {
    position: absolute;
    bottom: 2%;
    display: flex;
    justify-content: space-around;

    .file-item {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-left: 32px;

      img {
        height: 13px;
        width: 13px;
        margin-right: 5px;
      }

      .data {
        margin-left: 16px;
      }
    }
  }
  .input-box {
    width: 760px;
    height: 112px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 16px;
    border: 2px solid #b4bccc;
    backdrop-filter: blur(1px);
    padding: 5px;
    position: relative;

    :deep(.el-input__wrapper) {
      box-shadow: none;
      height: 40px;
      line-height: 40px;
    }
    .btn-box {
      position: absolute;
      bottom: 5px;
      width: 98%;
      display: flex;
      justify-content: space-between;

      .left-btn {
        display: flex;
        align-items: center;
        color: #828894;

        img {
          width: 13px;
          height: 13px;
        }
      }

      :deep(.w-select) {
        border-radius: 8px;
        color: #828894;
      }
      :deep(.w-select-view-single) {
        background-color: #f0f2f5;
      }
      .search-btn {
        display: inline-block;
        width: 32px;
        height: 32px;
        background: #4784f6;
        border-radius: 16px;
        cursor: pointer;

        img {
          margin: 9px;
          width: 14px;
          height: 14px;
        }
      }
    }
  }
  .topic-box {
    width: 830px;
    height: 300px;
    margin-top: 6vh;

    .topic-title {
      margin-left: 32px;
      margin-bottom: 10px;
      display: flex;
      align-items: center;

      img {
        width: 19px;
        height: 24px;
        margin-right: 5px;
      }

      span {
        font-family: DOUYINSANSBOLD, DOUYINSANSBOLD;
        font-weight: 600;
        font-size: 20px;
        color: #383d47;
        line-height: 24px;
        text-align: left;
        font-style: normal;
      }
    }
    .sort {
      font-weight: 500;
      font-size: 18px;
      color: #b4bccc;
      margin-right: 10px;
    }
    .text {
      display: inline-block;
      font-weight: 400;
      font-size: 16px;
      color: #383d47;
      white-space: nowrap; /* 不换行 */
      overflow: hidden; /* 隐藏超出的内容 */
      text-overflow: ellipsis; /* 用省略号表示被隐藏的部分 */
      max-width: 300px; /* 设置最大宽度以限制文本的显示长度 */
      cursor: pointer;
    }
    :deep(.w-list-item) {
      border: none;
      padding: 10px 12px;
    }
    :deep(.w-list-bordered) {
      border: none;
    }
    :deep(.w-list-item-content) {
      display: flex;
      align-items: center;
    }
  }
}
.mian-box1 {
  justify-content: center;
}
.isInput {
  background: #fff;
  border-bottom: 1px solid #fff !important;
}

.result {
  width: 100%;
  height: 80px;
  backdrop-filter: blur(3px);
  // border-bottom: 1px solid #d0d7df;
  position: relative;
  display: flex;
  align-items: center;
  // justify-content: center;
  // margin-top: 80px;

  .result-btn {
    display: flex;
    align-items: center;
    color: #2560cf;
    position: absolute;
    right: 48px;
    top: 28px;

    img {
      width: 15px;
      height: 15px;
      margin-right: 5px;
    }
  }
  .input-title {
    height: 80px;
    width: 800px;
    display: flex;
    align-items: center;
    .title {
      font-weight: 500;
      font-size: 24px;
      color: #383d47;
    }
    img {
      width: 20px;
      height: 20px;
      margin-left: 10px;
      cursor: pointer;
    }
  }
}
.inputRe-box {
  width: 800px;
  height: 48px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #2560cf;
  backdrop-filter: blur(1px);
  display: flex;
  align-items: center;

  .inputRe-content {
    width: 100%;
    display: flex;
    align-items: center;
    position: relative;
  }
  .w-input-wrapper {
    border: none;
    background: none;
  }
  .icon {
    position: absolute;
    top: 16px;
    left: 8px;
    width: 16px;
    height: 16px;
  }
  .line {
    width: 1px;
    height: 48px;
    background: #2560cf;
  }

  .btn-box {
    position: absolute;
    right: 5px;

    .sendBtn2 {
      width: 32px;
      height: 32px;
      border-radius: 22px;
      margin-top: 6px;
      background: none;
    }
  }
}
.audio {
  .sendVoiceBtn {
    // margin-top: 2px;
    margin-right: 5px;
    width: 16px;
    border-radius: 50%;
    cursor: pointer;
  }
  .vedioLoaingBtn {
    height: 32px;
    cursor: pointer;
    display: flex;
    padding: 0 6px 0 14px;
    justify-content: space-between;
    align-items: center;
    border-radius: 18px;
    position: relative;
    top: 4px;
  }
  .time-box {
    margin-top: -6px;

    .start-taste-line {
      hr {
        background-color: var(--w-color-primary); //声波颜色
        width: 2px;
        height: 3.5px;
        margin: 0 0.1rem;
        display: inline-block;
        border: none;
        border-radius: 0.5px;
      }
    }

    hr {
      animation: note 0.4s ease-in-out;
      animation-iteration-count: infinite;
      animation-direction: alternate;
    }

    .hr1 {
      animation-delay: -1s;
    }

    .hr2 {
      animation-delay: -0.9s;
    }

    .hr3 {
      animation-delay: -0.8s;
    }

    .hr4 {
      animation-delay: -0.7s;
    }

    .hr5 {
      animation-delay: -0.6s;
    }

    .hr6 {
      animation-delay: -0.5s;
    }

    .hr7 {
      animation-delay: -0.4s;
    }

    .hr8 {
      animation-delay: -0.3s;
    }

    .hr9 {
      animation-delay: -0.2s;
    }

    .hr10 {
      animation-delay: -0.1s;
    }

    @keyframes note {
      from {
        transform: scaleY(1);
      }

      to {
        transform: scaleY(4);
      }
    }
  }
}
.side-box {
  height: 100%;
  width: 69px;
  position: relative;

  .img-logo {
    margin: 12px;
  }

  .side-btn {
    width: 48px;
    height: 48px;
    border-radius: 8px;
    margin: 0px 8px 20px 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;

    img {
      width: 17px;
      height: 17px;
      margin-bottom: 5px;
    }

    p {
      font-weight: 400;
      font-size: 12px;
      color: #2a70f5;
      line-height: 16px;
    }
  }
  .user {
    position: absolute;
    left: 10px;
    bottom: 4%;
    :deep(.el-avatar) {
      background: #7eb1fc;
    }
    :deep(.el-avatar > img) {
      width: 50%;
      height: 50%;
    }
  }
}

.flex-c {
  display: flex;
  flex-direction: row;
}

.previewPdf {
  margin: 20px 20px 20px -10px;
  width: 1000px;
  height: calc(100vh - 40px);
  padding: 20px;
  border-radius: 16px;
  border-image: linear-gradient(139deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0)) 1
    1;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(3px);
  overflow-y: auto;
}

.flex-just {
  justify-content: space-between;
  display: flex;
}
</style>
