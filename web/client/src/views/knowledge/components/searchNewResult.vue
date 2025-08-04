<template>
  <div class="result-main flex-c">
    <div :class="syFlag==true?'mian-box-100':'mian-box'">
      <div class="mian-content" :style="syFlag==true?'width:30%;':'width:59%;'">
        <span class="title-text">{{searchTitle}}</span>
        <div class="progress-box" v-if="progress < 100 || !dialogueLoading">
          <div class="flex-s file-info">
            <div class="flex-r-c">
              <img :src="ks" />
              <span style="color: #434649; margin-left: 10px" v-show="progress < 100"
                >正在搜索全库</span
              >
              <span
                style="color: #434649; margin-left: 10px"
                v-show="progress == 100"
                >搜索完成</span
              >
            </div>
            <!-- v-show="progress < 90" -->
            <span style="color: #646479" v-show="progress > 30 && progress < 50"
              >正在提炼关键字</span
            >
            <span style="color: #646479" v-show="progress > 70 && progress < 100"
              >正在总结并生成回答</span
            >
            <span style="color: #646479" v-if="progress == 100 "
              >
			  <!-- 查找了 <span>{{ fileTotalCount }}</span> 篇相关资料， -->
			  其中精选出文件
              <span>{{ syList.length }}</span> 篇</span
            >
          </div>
          <w-progress
            class="progress"
            size="small"
            :percent="progress / 100"
            :style="{ width: progressBarWidth }"
            :show-text="false"
            status="normal"
          >
          </w-progress>
        </div>
        

        <div>
          <div class="flex-s answer-btn">
            <div class="flex-r-c">
              <img :src="answer" />
              <span class="answer-text">回答</span>
            </div>
          </div>
          <div
            class="answer-content"
            v-for="(item, index) of chatList"
            :key="index"
            ref="textRef"
          >
		 
            <ChatTextComponent
              v-if="!item.inversion && index == 0"
              :inversion="item.inversion"
              :loading="item.loading"
              :citations="item.citations"
              :dialogueFileIds="item.dialogueFileIds"
              :dialogueFolderIds="item.dialogueFolderIds"
              :paragraph="item.paragraph || ''"
              :skillId="item.skillId"
              :text="item.inversion ? item.question : item.answer"
              :as-raw-text="false"
              :asRawText="item.asRawText"
              :params="item.param"
              :imgUrl="item.imgUrl"
              v-model:item.isHighlightCode="item.isHighlightCode"
              :matterGuide="item.matterGuide"
              :finishReason="item.finishReason"
              @handleFillForm="item.handleFillForm"
            >
              <template #footer>
                <div v-if="!item.inversion && !item.loading">
                  <div class="tips">以上內容均由AI搜集并总结生成，仅供参考</div>
                  <div class="btns adaption flex-s">
                    <w-button type="outline" shape="round" @click="followUpSearch"
                      >追问</w-button
                    >
                    <div v-if="appId != '20'" style="position: relative">
                      <!-- <w-button type="text" shape="circle" @click="handleSelect('up')">
                        <CoolThumbUpLineWe v-if="!isup" size="14" />
                        <CoolThumbUpFillWe v-else size="14" />
                      </w-button>
                      <w-button type="text" shape="circle" @click="handleSelect('down')">
                        <CoolThumbDownLineWe v-if="!isdown" size="14" />
                        <CoolThumbDownFillWe v-else size="14" />
                      </w-button> -->
                      <!-- 踩和赞 暂时隐藏 关芯助理Pc -->
                      <w-popconfirm
                        v-if="!isup"
                        update-at-scroll
                        content-class="popoverClass"
                        @popup-visible-change="popupchange"
                        ok-text="提交"
                        @ok="onSubmit(1)"
                        placement="top"
                        trigger="click"
                        mini
                      >
                        <w-button type="text" shape="circle">
                          <CoolThumbUpLineWe v-if="!isup" size="14" />
                          <CoolThumbUpFillWe v-else size="14" />
                        </w-button>
                        <template #content>
                          <div class="popcontent">
                            <h2>{{ chatStore.zanForm.popTitle }}</h2>
                            <w-checkbox-group v-model="zanValue" direction="vertical">
                              <w-checkbox
                                v-for="(item, index) in chatStore.zanForm.options"
                                :key="index"
                                :value="item.id"
                                >{{ item.content }}</w-checkbox
                              >
                            </w-checkbox-group>
                            <w-textarea
                              v-model="zanText"
                              :placeholder="chatStore.zanForm.placeholder"
                              :max-length="1000"
                              show-word-limit
                              :auto-size="{ minRows: 6, maxRows: 10 }"
                            />
                          </div>
                        </template>
                      </w-popconfirm>
                      <w-button type="text" v-else shape="circle">
                        <CoolThumbUpLineWe v-if="!isup" size="14" />
                        <CoolThumbUpFillWe v-else size="14" />
                      </w-button>
                      <!-- :popup-container="textRef" -->
                      <w-popconfirm
                        v-if="!isdown"
                        update-at-scroll
                        content-class="popoverClass"
                        @popup-visible-change="popupchange"
                        ok-text="提交"
                        @ok="onSubmit(0)"
                        :content="!isdown ? '踩' : '已踩'"
                        placement="top"
                        trigger="click"
                        mini
                      >
                        <w-button type="text" shape="circle">
                          <CoolThumbDownLineWe size="14" />
                        </w-button>
                        <template #content>
                          <div class="popcontent">
                            <h2>{{ chatStore.caiForm.popTitle }}</h2>
                            <w-checkbox-group v-model="caiValue" direction="vertical">
                              <w-checkbox
                                v-for="(item, index) in chatStore.caiForm.options"
                                :key="index"
                                :value="item.id"
                                >{{ item.content }}</w-checkbox
                              >
                            </w-checkbox-group>
                            <w-textarea
                              v-model="caiText"
                              :placeholder="chatStore.caiForm.placeholder"
                              :max-length="1000"
                              show-word-limit
                              :auto-size="{ minRows: 6, maxRows: 10 }"
                            />
                            <div class="public-option">
                              <div>是否公开:</div>
                              <w-radio-group v-model="basePublic" direction="vertical">
                                <w-radio :value="true">是</w-radio>
                                <w-radio :value="false">否</w-radio>
                              </w-radio-group>
                            </div>
                          </div>
                        </template>
                      </w-popconfirm>
                      <w-button type="text" v-else shape="circle">
                        <CoolThumbDownLineWe v-if="!isdown" size="14" />
                        <CoolThumbDownFillWe v-else size="14" />
                      </w-button>
                      <w-tooltip
                        :content="!iscopy ? '复制' : '已复制'"
                        placement="top"
                        mini
                      >
                        <w-button
                          type="text"
                          shape="circle"
                          @click="handleSelect('copyText', item.answer)"
                        >
                          <CoolFuzhi_9oe2ecmc v-if="!iscopy" size="14" />
                          <CoolCheckLineWe v-else size="14" />
                        </w-button>
                      </w-tooltip>
                      <w-tooltip content="播放" placement="top" mini>
                        <w-button
                          type="text"
                          shape="circle"
                          v-if="!item.inversion"
                          @click="handleBofang(item.answer)"
                        >
                          <CoolVolumeUpLineWe size="14" />
                        </w-button>
                      </w-tooltip>
                    </div>
                  </div>
                </div>
              </template>
            </ChatTextComponent>
          </div>
          <div v-if="isFollwUp">
            <div
              class="answer-content"
              v-for="(item, index) of chatList"
              :key="index"
              ref="textRef"
            >
              <div v-if="index > 0" class="answer-title">{{ item.question }}</div>
              <ChatTextComponent
                v-if="!item.inversion && index > 0"
                :inversion="item.inversion"
                :loading="item.loading"
                :citations="item.citations"
                :dialogueFileIds="item.dialogueFileIds"
                :dialogueFolderIds="item.dialogueFolderIds"
                :paragraph="item.paragraph || ''"
                :skillId="item.skillId"
                :text="item.inversion ? item.question : item.answer"
                :as-raw-text="false"
                :asRawText="item.asRawText"
                :params="item.param"
                :imgUrl="item.imgUrl"
                v-model:item.isHighlightCode="item.isHighlightCode"
                :matterGuide="item.matterGuide"
                :finishReason="item.finishReason"
                @handleFillForm="item.handleFillForm"
              >
                <template #footer>
                  <div v-if="!item.inversion && !item.loading">
                    <div class="tips">以上內容均由AI搜集并总生成，仅供参考</div>
                    <div class="btns adaption flex-s">
                      <div></div>
                      <div style="position: relative">
                        <w-button type="text" shape="circle" @click="handleSelect('up')">
                          <CoolThumbUpLineWe v-if="!isup" size="14" />
                          <CoolThumbUpFillWe v-else size="14" />
                        </w-button>
                        <w-button
                          type="text"
                          shape="circle"
                          @click="handleSelect('down')"
                        >
                          <CoolThumbDownLineWe v-if="!isdown" size="14" />
                          <CoolThumbDownFillWe v-else size="14" />
                        </w-button>
                        <w-tooltip
                          :content="!iscopy ? '复制' : '已复制'"
                          placement="top"
                          mini
                        >
                          <w-button
                            type="text"
                            shape="circle"
                            @click="handleSelect('copyText', item.answer)"
                          >
                            <CoolFuzhi_9oe2ecmc v-if="!iscopy" size="14" />
                            <CoolCheckLineWe v-else size="14" />
                          </w-button>
                        </w-tooltip>
                        <w-tooltip content="播放" placement="top" mini>
                          <w-button
                            type="text"
                            shape="circle"
                            v-if="!item.inversion"
                            @click="handleBofang(item.answer)"
                          >
                            <CoolVolumeUpLineWe size="14" />
                          </w-button>
                        </w-tooltip>
                      </div>
                    </div>
                  </div>
                </template>
              </ChatTextComponent>
            </div>
            <div class="input-box">
              <w-textarea
                ref="textareaRef"
                v-model="continueSearchText"
                placeholder="继续提问..."
                :auto-size="{ minRows: 1, maxRows: 6 }"
                :max-length="80"
                @keydown.enter.prevent="sendQuestion(continueSearchText)"
                clearable
              />
              <div class="btn-box">
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
                  <w-button class="sendBtn" @click="sendQuestion(continueSearchText)"
                    >发送</w-button
                  >
                </w-space>
              </div>
            </div>
            <div class="question-tab" v-if="tjList.length > 0">
              <w-space>
                <w-button
                  type="secondary"
                  shape="round"
                  v-for="(item, index) in tjList"
                  :key="index"
                  @click="sendQuestion(item.question)"
                  :title="item.question"
                >
                  <template #default> <img :src="corner" class="icon3" /></template>
                  <template #icon>{{ stringSub(item.question, 18) }} </template>
                </w-button>
              </w-space>
            </div>
          </div>
          <div class="link-box" v-if="listNaoTu.length">
            <div class="flex-b-c">
              <div class="title-btn flex-r-c">
                <img :src="linkM" style="width: 18px; height: 18px; margin-right: 5px" />
                <span class="title">脑图</span>
              </div>
              <div>
                <div class="title-btn flex-r-c" @click="exportImg">
                  <img
                    :src="linImg"
                    style="width: 18px; height: 18px; margin-right: 5px"
                  />
                  <span class="titleImg">导出图片</span>
                </div>
                
              </div>
            </div>

            <div class="brainMap" id="brainMap">
              <div
                ref="EcharRef"
                style="width: 600px; height: 200px; margin-top: 20px"
              ></div>
            </div>
            <!-- <w-divider /> -->
          </div>
          
        </div>
        
      </div>
	 
		 
		<div class="mian-right" :style="syFlag==true?'width:25%;margin-right:30px;':'width:39%;'" v-if="syList.length>0">
			<div class="title-btn flex-r-c">
			  <img :src="linkM" style="width: 18px; height: 18px; margin-right: 5px" />
			  <span class="title">参考资料</span>
			</div>
			<w-divider />
		  <div class="file-list">
			
			<ul>
			  <li v-for="(item, index) in syList" :key="index">
				<!-- <div class="tips-text" v-if="index == 0 && relevance == 0">
				  相关度最高
				</div> -->
				
				<div class="list-fileName" @click="addsyListIndex(index)">					
					  <img :src="pdf" class="icon1" v-if="item.type==1" />
					 <img :src="pdf" class="icon1" v-else />
					  <span> {{ item.title }} </span>									
				</div>
				<div v-if="syIndex==index">
					<div class="list-fileText" v-html="item.content"></div>
					<div class="list-btn" @click="previewClick(item)">{{item.type==1?'查看文档':'查看链接'}}</div>
				</div>
			  </li>
			  
			  <!-- <li class="flex-c">
				<span class="tips">已经到底了~</span>
			  </li> -->
			</ul>
		  </div>
		</div>
		<div class="previewPdf" v-if="syFlag&&previewClicklist.url">
		    <div class="flex-just">
		      <div
		        style="
		          font-family: MiSans, MiSans;
		          font-weight: 500;
		          font-size: 16px;
		          color: #434649;
		          line-height: 24px;
		          margin-bottom: 10px;
		        "
		      >
		        {{ previewClicklist.title }}
		      </div>
		      <div>
		        <CoolCloseLineWe class="close" size="18" style="color:#828894;cursor: pointer;" @click="addpreviewClicklist"/>
		      </div>
		    </div>
		    <div>
		      <layoutCenterPdfPdf v-if="previewClicklist.url" :url="previewClicklist.url"></layoutCenterPdfPdf>
		    </div>
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
  onUnmounted,
   toRefs
} from "vue";
import { useRoute } from "vue-router";
import { copyText } from "/@/utils/format";
import { simpleTransferTcc } from "/@/utils/conv";
import answer from "/@/assets/ai/answer.svg";
import linkM from "/@/assets/ai/link-m.svg";
import linImg from "/@/assets/searchNew/download-line.svg";
import ks from "/@/assets/ai/yanjiu.svg";
import answerFill from "/@/assets/ai/answer-fill.svg";
import docx from "/@/assets/ai/docx.svg";
import save from "/@/assets/ai/save1.svg";
import downloadLine from "/@/assets/ai/download-line.svg";
import pdf from "/@/assets/ai/pdf.svg";
import PPT from "/@/assets/ai/PPT.svg";
import txt from "/@/assets/ai/txt.svg";
import filter from "/@/assets/ai/filter.svg";
import arrowLeft from "/@/assets/ai/arrow-left-s-line.svg";
import sjx from "/@/assets/ai/sjx.svg";

import { useChatStore } from "/@/stores/chat";
import { setScrollPosition } from "/@/utils/other";
import mittBus from "/@/utils/mitt";
import ChatTextComponent from "/src/components/yy-message-searchNew/ChatText.vue";
import {
  listDialogueQuestion,
  tjQuestion,
  sourceAnswer,
  feedback,
  apiFeedback,
} from "/@/api/chat";
import corner from "/@/assets/ai/corner.svg";
import layoutCenterPdfPdf from "/src/components/previewWord.vue";
import previewdocpdf from "./previewdocpdf.vue";
import follw from "/@/assets/ai/follw.svg";
import fileWordFill from "/@/assets/ai/file-word-fill.png";
import * as echarts from "echarts";
import html2canvas from "html2canvas";
import { formatDate } from "/@/utils/formatTime";
import { Message } from 'winbox-ui-next';

const EcharRef = ref();
// import 'file-viewer/dist/icons/main.css'
interface Emit {
  (ev: "regenerate"): void;
  (ev: "delete"): void;
}

const route = useRoute();
const chatStore = useChatStore();
const chatList = computed(() => chatStore.chatList);

const isSensitive = computed(() => chatStore.isSensitive);
const dialogueLoading = computed(() => chatStore.dialogueLoading);
const { appId } = route.params as { appId: string };

interface Props {
  searchText?: string;
  searchWay?: string;
  processLi?: Number;
  searchType?: Number;
  syFlag ?: boolean;
}
const relevance = ref(0);
const props = defineProps<Props>();
let listNaoTu = ref([]);
// 加载进度条
const progress = ref(props.processLi);
const interval = ref();
const progressBarWidth = computed(() => {
  return `${progress.value}%`;
});
const zanValue = ref([]);
const caiValue = ref([]);
const zanText = ref("");
const caiText = ref("");
const syIndex = ref(null);
const basePublic = ref(false);
const { index: curIndex } = toRefs(props)
const syList = computed(() => chatStore.refList);
const previewClicklist = ref({});
// 进度条定时器
const progressInterval = () => {
  interval.value = setInterval(() => {
    if (progress.value < 100) {
      progress.value += 10;
    } else {
      clearInterval(interval.value);
    }
  }, 1000);
};

// 总结 or 文件切换
const showRresultType = ref(1);
const syFlag = ref(props.syFlag);
const searchTitle = ref(props.searchText);
const searchTypeList = ref(props.searchType);
// 展示图标
const iscopy = ref(false);
const isdown = ref(false);
const isup = ref(false);

const curText = ref("");
const stopChatLoading = ref(false);
const textRef = ref<HTMLElement>();

// 继续追问
const continueSearchText = ref("");
//是否追问
const isFollwUp = ref(false);
// 音频按钮状态
const btnStatus = ref("UNDEFINED");

// 暂停录音
const recorderStop = () => {
  recorder.stop();
  changeBtnStatus("CLOSED");
};
// 收起 展开
const showEvent = ref(true);
const showOrg = ref(true);

// 文件瀑布流
const fileWaterfallFlowList = ref([]);
const fileWaterfallFlowLoading = ref(false);

// 显示瀑布流 or 文件列表
const isWaterfall = ref(false);
const fileLinkList = ref("");
const stringSub = (text: string, length: any) => {
  if (text.length > length) {
    text = text.substring(0, length - 1) + "...";
    return text;
  } else {
    return text;
  }
};

// 停止生成
const handleclickStopChat = async () => {
  stopChatLoading.value = true;
  await chatStore.clearChat();
  stopChatLoading.value = false;
  chatStore.scrollbarBottom = true;
  setScrollPosition(".center-side");
};
// 复制 点赞
const handleSelect = (key: string, data: string) => {
  if (key === "up") {
    isup.value = true;
    isdown.value = false;
    return;
  }
  if (key === "down") {
    isdown.value = true;
    isup.value = false;
    return;
  }
  if (key === "event") {
    showEvent.value = !showEvent.value;
    return;
  }
  if (key === "organization") {
    showOrg.value = !showOrg.value;
    return;
  }
  if (key === "copyText") {
    clearTimeout(timer.value);
    iscopy.value = true;
    let text = data ?? "";
    text = text.replace(/:::r|:::|/g, "");
    if (text === chatStore.errorText) {
      return;
    }
    copyText({ text });
    timer.value = setTimeout(() => {
      iscopy.value = false;
    }, 2000);
    return;
  }
};
// 播放
const emit = defineEmits(["previewClickli"]);
const handleBofang = (data: string) => {
  let text = data ?? "";
  text = text.replace(/:::r|:::|/g, "");
  let newText =
    text.split(
      '<p style="padding: 6px 0;border-top: 1px solid rgba(0, 0, 0, 0.12);height: 1px;margin-top:12px;"/>'
    ) || [];
  play_tts(newText ? newText[0] : "");
};
const encodeText = (text, type) => {
  if (type === "unicode") {
    let buf = new ArrayBuffer(text.length * 4);
    let bufView = new Uint16Array(buf);
    for (let i = 0, strlen = text.length; i < strlen; i++) {
      bufView[i] = text.charCodeAt(i);
    }
    let binary = "";
    let bytes = new Uint8Array(buf);
    let len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
  } else {
    return Base64.encode(text);
  }
};
let ttsWS;
const connectWebSocket_tts = (tt) => {
  const tts_config = getRandomConfig();
  APPID =  tts_config.APPID;
  API_SECRET =  tts_config.API_SECRET;
  API_KEY =  tts_config.API_KEY;
	const url = getWebSocketUrl_tts();
  if ("WebSocket" in window) {
    ttsWS = new WebSocket(url);
  } else if ("MozWebSocket" in window) {
    ttsWS = new MozWebSocket(url);
  } else {
    alert("浏览器不支持WebSocket");
    return;
  }
  changeBtnStatus_tts("CONNECTING");

  ttsWS.onopen = (e) => {
    audioPlayer.start({
      autoPlay: true,
      sampleRate: 16000,
      resumePlayDuration: 1000,
    });
    changeBtnStatus_tts("PLAY");
    let ttext = tt || sayStr.value || "抱歉，未正确识别到语音，请重新提问。";
    var tte = "UTF8";
    var params = {
      common: {
        app_id: APPID,
      },
      business: {
        aue: "raw",
        auf: "audio/L16;rate=16000",
        vcn: "xiaoyan", //'aisjiuxu' 'aisxping' 'aisjinger' 'aisbabyxu' 'vixq' 'xiaoyan'
        speed: 70,
        volume: 50,
        pitch: 50,
        bgs: 0,
        tte,
      },
      data: {
        status: 2,
        text: encodeText(ttext, tte),
      },
    };
    ttsWS.send(JSON.stringify(params));
  };
  ttsWS.onmessage = (e) => {
    let jsonData = JSON.parse(e.data);
    // 合成失败
    if (jsonData.code !== 0) {
      console.error(jsonData);
      changeBtnStatus_tts("UNDEFINED");
      return;
    }
    audioPlayer.postMessage({
      type: "base64",
      data: jsonData.data.audio,
      isLastData: jsonData.data.status === 2,
    });
    if (jsonData.code === 0 && jsonData.data.status === 2) {
      ttsWS.close();
    }
  };
  ttsWS.onerror = (e) => {
    console.error(e);
  };
  ttsWS.onclose = (e) => {
    // console.log(e);
  };
};
const play_tts = (tt) => {
  if (btnStatus_tts.value === "UNDEFINED") {
    // 开始合成
    connectWebSocket_tts(tt);
  } else if (btnStatus_tts.value === "CONNECTING") {
    // 停止合成
    changeBtnStatus_tts("UNDEFINED");
    ttsWS?.close();
    audioPlayer.reset();
    return;
  } else if (btnStatus_tts.value === "PLAY") {
    audioPlayer.stop();
  } else if (btnStatus_tts.value === "STOP") {
    audioPlayer.play();
  }
};

// 语言合成代码开始*
const btnStatus_tts = ref("UNDEFINED");
const audioPlayer = new AudioPlayer("./tts");
let APPID =  '';
let API_SECRET =  '';
let API_KEY =  '';
const btnControl = ref("");
const changeBtnStatus_tts = (status, type = "") => {
  btnStatus_tts.value = status;
  if (status === "UNDEFINED") {
    btnControl.value = "立即合成";
  } else if (status === "CONNECTING") {
    btnControl.value = "正在合成";
  } else if (status === "PLAY") {
    btnControl.value = "停止播放";
  } else if (status === "STOP") {
    if (type != "quit") {
      // setTimeout(() => {
      // 	btnControlFun()
      // }, 100);
    }
    btnControl.value = "重新播放";
  }
};
audioPlayer.onPlay = () => {
  changeBtnStatus_tts("PLAY");
};
audioPlayer.onStop = (audioDatas) => {
  console.log(audioDatas);
  btnStatus_tts.value === "PLAY" && changeBtnStatus_tts("STOP");
};
const getWebSocketUrl_tts = () => {
  var apiKey = API_KEY;
  var apiSecret = API_SECRET;
  var url = "wss://tts-api.xfyun.cn/v2/tts";
  var host = location.host;
  var date = new Date().toGMTString();
  var algorithm = "hmac-sha256";
  var headers = "host date request-line";
  var signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v2/tts HTTP/1.1`;
  var signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret);
  var signature = CryptoJS.enc.Base64.stringify(signatureSha);
  var authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
  var authorization = btoa(authorizationOrigin);
  url = `${url}?authorization=${authorization}&date=${date}&host=${host}`;
  return url;
};

// 切换展示列表格式
const handleRresultType = async (data: any) => {
  showRresultType.value = data;
};
// const syList = ref([]);
const arrData = ref([]);
const syListAll = ref([]);
// 获取参考资料
const sourceAnswerList = async () => {
  let res = await sourceAnswer({
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
    dialogueId: sessionStorage.getItem("dialogueId"),
    searchType: searchTypeList.value,
    relevance: relevance.value,
  });
  if (res?.code == "000000") {
    fileTotalCount.value = res?.data?.fileTotalCount;
    selectedFileCount.value = res?.data?.selectedFileCount;
    if (res?.data?.sourceAnswerResultList.length > 0) {
      // if (res.data.sourceAnswerResultList[0].sourceType == 1) {
      //   arrData.value = res.data.sourceAnswerResultList.filter(
      //     (obj, index, self) =>
      //       obj.fileLink && index === self.findIndex((t) => t.fileName === obj.fileName)
      //   );
      // } else {
      arrData.value = res.data.sourceAnswerResultList.filter(
        (obj, index, self) =>
          obj.fileLink && index === self.findIndex((t) => t.fileLink === obj.fileLink)
      );
      // }
      // 例如：需要标红的的词组
      arrData.value.forEach((item) => {
        let regex = RegExp("(" + searchTitle.value.replace("\\$1") + ")", "ig");
        let html =
          '<span style="color:#F52A2A;background:rgba(245, 42, 42, 0.1);border-radius: 4px;padding: 0 4px;">$1</span>';
        item.text = item.text.replace(regex, html);
      });
      if (!isFollwUp.value) {
        syList.value = arrData.value;
        syListAll.value.push(syList.value);
      } else {
        syListAll.value.push(arrData.value);
      }

      setScrollPosition(".center-side", "auto");
    } else {
      syList.value = [];
    }
  } else {
    syList.value = [];
  }
};
// 追问
const followUpSearch = async () => {
  isFollwUp.value = true;
  tjQuestionList();
};
// 获取推荐问题
const tjList = ref([]);
const tjQuestionList = async () => {
  let res = await tjQuestion({
    question: continueSearchText.value ? continueSearchText.value : searchTitle.value,
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
  });
  if (res?.code == "000000") {
    tjList.value = res.data || [];
    setScrollPosition(".center-side", "auto");
  } else {
    tjList.value = [];
  }
  mittBus.emit("getPresetQuestionList", tjList.value);
};
const searchWay = ref("MIXED");
// 追问搜索问题
const sendQuestion = async (item: string) => {
  console.log("dialogueLoading", dialogueLoading, isSensitive);
  if (item) {
    continueSearchText.value = item;
    tjList.value = [];
    tjQuestionList();

    if (chatStore.dialogueLoading) return;
    const data: any = {
      content: item,
      conversationId: route.params.conversationId,
      knowledgeBaseId: route.params.appId,
      appId: route.params.appId,
      searchWay: searchWay.value,
    };
    chatStore.setChatList(data);
    continueSearchText.value = "";
    return;
  }
  return;
};
const recorder = new RecorderManager("./iat");
// 语音
const openYY = (type: string = "") => {
  continueSearchText.value = "";
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
const chatFlag = ref(false);
const chatSBFlag = ref(false);
const resultVal = ref("");
const fileTotalCount = ref("");
const selectedFileCount = ref("");

let iatWS;
let resultText = "";
let resultTextTemp = "";
let countdownInterval;
const textFileIds: any = ref([]);
const textFolderIds: any = ref([]);
const instructObj: any = ref({});
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
    continueSearchText.value = "";
    resultText = "";
    resultTextTemp = "";
  } else if (status === "OPEN") {
    countdown();
  } else if (status === "CLOSING") {
    resultVal.value = "关闭连接中";
  } else if (status === "CLOSED") {
    continueSearchText.value = "";
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
    continueSearchText.value = resultTextTemp || resultText || "";
  }
  continueSearchText.value = continueSearchText.value.trim();
  console.log(continueSearchText.value);
  if (continueSearchText.value) {
    // setInput();
    sendQuestion(continueSearchText.value);
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
const previewClick = (item) => {
	if(item.sourceType=='1'){
		previewClicklist.value = item
		 syFlag.value = true
	}else{
		window.location.href = item.url
	}

};
const addpreviewClicklist = async () => {
  syFlag.value = false;
  previewClicklist.value = {}
}
const addsyListIndex =  (index) => {
	if(syIndex.value==index){
		syIndex.value = null
	}else{
		syIndex.value = index
	}
  
};

function init(data) {
  console.log(data);
  var myChart = echarts.init(EcharRef.value);
  // 指定图表的配置项和数据
  var option = {
    tooltip: {
      show: false,
    },
    series: [
      {
        type: "tree",
        data: data,
        top: "1%",
        left: "20%",
        bottom: "1%",
        right: "20%",
        symbolSize: 7,
        label: {
          position: "left",
          verticalAlign: "middle",
          align: "right",
          fontSize: 14,
        },
        leaves: {
          label: {
            position: "right",
            verticalAlign: "middle",
            align: "left",
          },
        },
        emphasis: {
          focus: "descendant",
        },
        lineStyle: {
          color: "#2A70F5", // 这里设置线条颜色为红色
          type: "curve",
          curve: 0.5,
        },
        expandAndCollapse: true,
        // animationDuration: 550,
        // animationDurationUpdate: 750,
      },
    ],
  };
  // 使用刚指定的配置项和数据显示图表。
  myChart.setOption(option);
}

const exportImg = () => {
  // 转换成canvas
  html2canvas(document.getElementById("brainMap")).then(function (canvas) {
    var img = canvas.toDataURL("image/png").replace("image/png", "image/octet-stream");
    // 创建a标签，实现下载
    var creatIMg = document.createElement("a");
    creatIMg.download = "导出图片.png"; // 设置下载的文件名，
    creatIMg.href = img; // 下载url
    document.body.appendChild(creatIMg);
    creatIMg.click();
    creatIMg.remove(); // 下载之后把创建的元素删除
  });
};

const popupchange = (val) => {
  if (!val) {
    caiValue.value = [];
    zanValue.value = [];
    caiText.value = "";
    zanText.value = "";
  }
};
const onSubmit = async (type) => {
  let data = {
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
    dialogueId: sessionStorage.getItem("dialogueId"),
    feedbackType: type,
    feedbackContent: type == 1 ? zanText.value : "",
    feedbackTime: formatDate(new Date(), "YYYY-mm-dd HH:MM:SS"),
  };
  if (type == 0) {
    data.title = props.question;
    data.basePublic = basePublic.value;
    data.content = caiText.value;
  }
  type == 1 ? handleSelect("up") : handleSelect("down");
  //chatStore.feedbacksubmit(data)
  let res = await apiFeedback(data);
  let msg = type == 1 ? "点赞" : "点踩";
  if (res.code == "000000") {
    Message.success(msg + "成功");
    basePublic.value = false;
  } else {
    isup.value = false;
    isdown.value = false;
    if (res?.msg) {
      Message.warning(res.msg);
    }
  }
};
watch(
  () => props.searchText,
  (newVal: any) => {
    console.log("==>>>>>>>>>>>watch", newVal);
    searchTitle.value = newVal;
    isFollwUp.value = false;
    continueSearchText.value = "";
    listNaoTu.value = [];
    syList.value = [];
    tjList.value = [];
    iscopy.value = false;
    isdown.value = false;
    isup.value = false;
    fileTotalCount.value = 0;
    selectedFileCount.value = 0;
    emit("previewClickli", {});
    progress.value = 10;
    progressInterval();
  },
  {
    immediate: true,
    deep: true,
  }
);
watch(
  () => props.searchWay,
  (newVal: any) => {
    console.log("==>>>>>>>>>>>watchsearchWay", newVal);
    searchWay.value = newVal;
  },
  {
    immediate: true,
    deep: true,
  }
);
let timer = ref();
let curNum = ref(1);

onMounted(() => {
  timer.value = setInterval(() => {
    curNum.value += 1;
    if (curNum.value > 3) {
      curNum.value = 1;
    }
  }, 350);
  progressInterval();
  mittBus.on("dialogueId", (appInfo) => {
    setTimeout(() => {
      // appInfo.sourceShowFlag === '1' && sourceAnswerList();
        appInfo.recommendQuestionsShowFlag === '1' && tjQuestionList();
				//mittBus.all.delete('dialogueId', () => {});
    }, 1000);
  });
  mittBus.on("outline", (value) => {
    listNaoTu.value = convertToArrayToTree(value);

    if (listNaoTu.value.length > 1) {
      listNaoTu.value = [
        {
          name: searchTitle.value,
          children: listNaoTu.value,
        },
      ];
    }
    setTimeout(() => {
      if (listNaoTu.value.length) {
        init(listNaoTu.value);
      }
    }, 1000);
  });
  mittBus.on('handleClickIndex', (data) => {
  	console.log('handleClickIndex',data)
  	syIndex.value = data
  });
});
function convertToArrayToTree(value) {
  let rootItems = []; // 用于存放根级别的items
  value.forEach((item) => {
    if (item.content.length) {
      let list = [];
      item.content.forEach((itemCon) => {
        list.push({
          name: itemCon,
          value: 2,
        });
      });
      rootItems.push({
        name: item.title,
        children: list,
      });
    } else {
      rootItems.push({
        name: item.title,
        value: 2,
      });
    }
  });
  return rootItems;
}
onUnmounted(() => {
  mittBus.all.delete("dialogueId", () => {});
  if (interval.value) {
    clearInterval(interval.value);
  }
  
});
</script>

<style lang="scss" scoped>
.result-main {
  width: 100%;
  height: calc(100vh - 150px);
  overflow-y: auto;
  .title-text {
    display: inline-block;
    margin: 10px 0px 20px 0px;
    font-weight: 500;
    font-size: 32px;
    color: #434649;
    line-height: 42px;
    text-align: left;
    font-style: normal;
    overflow-wrap: break-word;
  }
}
.result-main::-webkit-scrollbar {
    width: 0;
  }
   
  /* 隐藏水平滚动条 */
  .result-main::-webkit-scrollbar {
    height: 0;
  }
.mian-box {
  width:1400px;
  display: flex;
  justify-content: space-between;
  
}
.mian-box-100{
	width:1800px;
	display: flex;
	justify-content: space-between;
}
.mian-right{
	width: 39%;
	margin-top: 80px;
	.title{
		font-size: 18px;
	}
}
.mian-content {
  padding-right: 20px;
  width: 59%;
  .flex-s {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .span-btn {
      font-size: 16px;
      color: #828894;
      margin-top: 20px;

      span {
        display: inline-block;
        width: 88px;
        height: 40px;
        background: #f2f5fa;
        border-radius: 8px;
        line-height: 40px;
        margin-right: 12px;
        text-align: center;
        font-style: normal;
        cursor: pointer;
      }
      .active {
        background: rgba(42, 112, 245, 0.1);
        color: #2a70f5;
        font-weight: 500;
      }
    }

    .link-icon {
      font-size: 16px;
      color: #383d47;
    }
  }

  .progress-box {
    width: 100%;
    height: 56px;
    background: #ffffff;
    border-radius: 8px;
    border: 1px solid rgba(0, 0, 0, 0.12);
    position: relative;

    .file-info {
      padding: 15px 10px;

      .title {
        color: #434649;
      }

      i {
        color: #2a70f5;
      }
    }

    .progress {
      position: absolute;
      bottom: 0;
      left: 0;
    }
  }

  .answer-btn {
    margin: 20px 0px 20px 0px;

    .answer-text {
      margin-left: 5px;
      font-weight: 500;
      font-size: 16px;
      color: #434649;
    }
    .answer-read {
      margin-left: 5px;
      font-size: 14px;
      cursor: pointer;
    }
  }
  .answer-content {
    .title {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #383d47;
      line-height: 32px;
    }

    .btns {
      margin-top: 30px;
    }
    .answer-title {
      // height: 42px;
      margin-top: 20px;
      padding: 10px 18px;
      // background: #fff;
      font-weight: 600;
      font-size: 18px;
      color: #434649;
      line-height: 42px;
      text-align: left;
      font-style: normal;
    }
  }
  .tips {
    margin: 20px 0;
    font-size: 14px;
    color: #a6a5b8;
  }
  .link-box {
    margin-top: 40px;

    .title-btn {
      cursor: pointer;
      .title {
        font-weight: 600;
        font-size: 18px;
        color: #434649;
        margin-right: 5px;
      }
      .titleImg {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #2a70f5;
        line-height: 24px;
      }
      .transfer {
        transform: rotate(180deg);
      }
    }

    .link-list {
      margin-top: 20px;
      color: #646479;
      font-size: 14px;

      li {
        margin: 5px 0;
        span {
          margin-right: 20px;
          text-align: left;
        }
      }
    }
    :deep(.w-link) {
      color: #646479;
      font-size: 14px;
    }
    .type {
      font-size: 12px;
      color: #a6a5b8;
      line-height: 25px;
      text-align: left;
      font-style: normal;
    }
    .content {
      font-weight: 400;
      font-size: 14px;
      color: #434649;
      line-height: 19px;
      text-align: left;
      font-style: normal;
    }
  }
}
.mian-img {
  flex: 1;
  padding: 0 20px;
}
.flex-r-c {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.input-box {
  margin-top: 20px;
  width: 100%;
  height: 96px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  border: 1px solid #e1e4eb;
  position: relative;

  :deep(.w-textarea) {
    height: 56px !important;
    // line-height: 48px !important;
  }

  .w-textarea-wrapper {
    border: none;
    background: none;
    width: 100%;
  }

  .btn-box {
    position: absolute;
    bottom: 5px;
    width: 98%;
    display: flex;
    justify-content: flex-end;

    .sendBtn {
      width: 72px;
      height: 32px;
      background: #e0ecfe;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 500;
      font-size: 14px;
      color: #2a70f5;
      line-height: 32px;
      text-align: center;
    }
    .sendVoiceBtn {
      margin-top: 2px;
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
}
.question-tab {
  width: 100%;
  margin: 10px 0px 20px 0px;
  overflow: hidden;
}
.file {
  width: 100%;
  border: 1px solid red;
}
.file-box {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  position: relative;
  .icon-sjx {
    position: absolute;
    bottom: 0;
    right: 5px;
  }
}
.file-title {
  span {
    font-weight: 500;
    font-size: 18px;
    color: #383d47;
    line-height: 24px;
  }
  i {
    color: #2c70f6;
    margin: 0 5px;
  }
}
.file-list {
  height: calc(100vh - 100px);
  overflow-y: auto;
  
  li {
  	margin: 15px 0;
  	padding: 10px 20px;
      border:1px solid #eee;
  	border-radius: 6px;
  	background: #fff;
  	 
  	span {
  		display: inline-block;
		
  		text-align: left;
		white-space: nowrap; /* 防止文本换行 */
		  overflow: hidden;    /* 隐藏溢出的内容 */
		  text-overflow: ellipsis; /* 显示省略号来代表被修剪的文本 */
  	}
  }
  .list-fileName{
	  display: flex;
	  font-weight: 550;
	
	cursor: pointer;
	
  }
    .icon1 {
      width: 20px;
      height: 20px;
      margin-right: 5px;
    }
   
   .list-btn{
	   width:90px;
	   padding: 5px 0px;
	   border:1px solid #2a70f5;
	   color: #2a70f5;
	   text-align: center;
	   margin-top: 10px;
	   border-radius: 4px;
	   cursor: pointer;
   }
  .list-fileText {
    // height: 75px;
     display: -webkit-box;
	  -webkit-box-orient: vertical;
	  -webkit-line-clamp: 3; /* 限制显示的行数为3行 */
	  overflow: hidden;
    // padding: 12px 0;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #828894;
    line-height: 24px;
	 margin-top: 10px;

    em {
      background: #fee9e9;
      color: #f52a2a;
      font-style: normal;
      padding: 5px;
      border-radius: 4px;
    }
  }

  .tips-text {
    display: none;
    width: 96px;
    height: 32px;
    background: #2a70f5;
    font-weight: 400;
    font-size: 14px;
    color: #ffffff;
    text-align: center;
    line-height: 24px;
    border-radius: 8px;
    margin-bottom: -8px;
  }
}

.flex-c {
  display: flex;
  justify-content: center;
}
.flex-nc {
  display: flex;
}

.brainMap {
  background: rgba(255, 255, 255, 0.4);
  border-radius: 8px;
  border: 1px solid;
  margin-top: 12px;
  border-image: linear-gradient(163deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0.08))
    1 1;
}

.flex-b-c {
  display: flex;
  justify-content: space-between;
}

.popoverClass {
  background: linear-gradient(130deg, #FFFFFF 0%, #FFFFFF 100%), linear-gradient(180deg, #F7F5FF 0%, rgba(239, 243, 251, 0) 100%) !important;
  box-shadow: 0px 6px 20px 0px rgba(30, 64, 175, 0.06) !important;

  h2 {
    color: #646479;
  }

  .popcontent {
    width: 240px;
  }

  .w-popconfirm-icon {
    display: none !important;
  }

  .w-textarea-word-limit {
    background: #fff;
  }
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
</style>
