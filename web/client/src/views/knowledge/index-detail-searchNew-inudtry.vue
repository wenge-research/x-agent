<template>
  <div class="ai-mian">
    <!-- 旧版的侧边栏 -->
    <!-- <div class="side-box">
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
      <div class="user">
        <el-avatar :src="iconUser"></el-avatar>
      </div>
    </div> -->
    <!-- v-if="isSearch" -->
    <div class="mian-box" v-if="isSearch">
      <div class="user-menu" v-if="isToken && isHovered" @mouseenter="handleMouseEnter"
        @mouseleave="handleMouseLeave">
        <div @click="outLoginDialogVisible = true">退出登录</div>
      </div>
      <div class="content-box">
        <img class="logo-img" v-if="logoUrl()" :src="logoUrl()" alt="" />
        <div class="title-box">
            <img v-if="getAppDetail()?.identityIcon?.includes('.png')" :src="getAppDetail()?.identityIcon" style="max-height: 66px" />
            <div v-else class="title-text" style=" text-align: center; font-size: 40px; ">{{ getAppDetail()?.identityIcon }}</div>
        </div>
        <!-- 切换模型功能 -->
        <!-- <div class="tab-box">
        <div
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
        </div>
      </div> -->
        <div class="input-box" style="margin-top:20px;">
          <!-- :fetch-suggestions="querySearch" -->
          <w-textarea class="inline-input" v-model="searchText" 
            placeholder="输入您的问题" @keydown.enter="sendSearch"></w-textarea>
          <div class="btn-box audio">
            <div class="left-btn">
              <!-- <w-space style="background: #f4f6f9; border-radius: 8px">
                <img :src="appsFill" alt="" style="margin-left: 20px" />
                <w-select :style="{ width: '100px' }" placeholder="全部" :bordered="false" v-model="searchType">
                  <w-option style="color: #828894" v-for="(item, index) in searchOptions" :key="index" :value="item.id"
                    :label="item.name"></w-option>
                </w-select>
              </w-space> -->
            </div>
            <w-space>
              <span class="search-btn" @click="sendSearch">
                <img :src="imgSearch" alt="" />
              </span>
            </w-space>
          </div>
        </div>
        <div class="country">
          <ul>
            <li v-for="(item, index) in countryData" :key="index" @click="handleCountryData(item)">
              <span class="icon">#</span>
              <span class="name">{{ item.keyword }}</span>
              <span class="num">{{ item.count }}</span>
            </li>
          </ul>
        </div>

      </div>
    </div>
    <comDocPreview ref="previewRef" />
    <!-- 新版回答结果页 -->
    <div v-if="!isSearch" class="result-box">
      <div class="result-header">
        <div class="result-header-left" @click="transitionisSearchZanShi">
          <!-- /assets/images/img/logo.png -->
          <img src="/src/assets/images/img-logo.png" alt="">
        </div>
        <div class="result-header-right">

          <div class="inputRe-box">
            <div class="inputRe-content">
              <!-- <img
                  :src="valueName == 'MIXED' ? ks : valueName == 'FULL_TEXT' ? zq : yj"
                  class="icon"
                /> -->

              <span class="inputRe-left-icon">
                <iconpark-icon name="apps-line"></iconpark-icon>
              </span>
              <w-select v-if="isShow" v-model="valueName" :options="options" :field-names="fieldNames" :style="{ width: '120px' }"
                style="margin-left: 0;padding:0 0 0 10px;" placeholder="请选择" :bordered="false" />
              <div class="line"></div>
              <w-input ref="textareaRef" v-model="searchText" placeholder="请输入搜索内容" :max-length="80"
                :style="{ width: '460px', paddingLeft: '10px' }" @keydown.enter.prevent="sendSearch" />
              <div class="btn-box audio">
                <w-space>
                  <!-- <i class="vedioBtn" id="btn_control">
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
                    </i> -->
                  <w-button type="primary" class="sendBtn2" @click="sendSearch">
                    <template #icon>
                      <img :src="imgSearch" alt="" />
                    </template>
                  </w-button>
                </w-space>
              </div>
            </div>
          </div>

        </div>
        <!-- <div class="flex items-center new-user-box">
          <div class="mr10">欢迎您，admin</div>
          <iconpark-icon size="35" name="account-circle-line"></iconpark-icon>
        </div> -->

      </div>
      <div class="result-tab-box">
        <div class="result-tab-box-content">
          <div class="result-tab-title">{{ searchTextvalue }}</div>
          <div>
            <!-- 新增的内容 -->
            <div class="result-tabs">
              <div v-for="(item, index) in resultTabList" :key="index" class="tab-item"
                :class="{ active: resultActiveTab === item }" @click="resultActiveTabFun(item)">
                {{ capitalizeFirstLetter(item) }}
              </div>
            </div>
          </div>
        </div>
        <div class="result-border" v-show="resultActiveTab !== 'AI搜索'"></div>
      </div>
      <div class="result-content-box">
        <div v-show="resultActiveTab === 'AI搜索'">
          <searchResultAI v-if="isShowResult" :loading="isSearch" :searchText="searchSureText" :searchType="searchType"
            :processLi="10" type="zh" :searchWay="isSearch ? activeName : valueName" @previewClickli="previewClickli">
          </searchResultAI>
        </div>
        <div v-show="resultActiveTab === '结构化数据'">
          <indexDetailResultStructured type="zh" 
          :fileName="searchText"
		   ref="childComponent2"
          :applicationId="applicationId" 
          :question="searchSureText"></indexDetailResultStructured>
        </div>
        <div v-show="resultActiveTab === '企业库'">
          企业库
        </div>
      </div>
    </div>

    <!-- 旧版回答结果页 -->
    <!-- v-if="!isSearch" -->
    <div v-if="false" :class="['mian-box', 'mian-box1', 'flex-c', previewClicklist.transPdfUrl ? 'abbbb' : '']">
      <div>
        <div class="result" :class="{ isInput: showResultInput }">
          <!-- <w-space class="result-btn">
            <img :src="save" alt="" />
            <p>收藏</p>
          </w-space> -->
          <div class="inputRe-box" v-show="showResultInput">
            <div class="inputRe-content">
              <img :src="valueName == 'MIXED' ? ks : valueName == 'FULL_TEXT' ? zq : yj" class="icon" v-if="isShow" />
              <w-select v-model="valueName" :options="options" :field-names="fieldNames" :style="{ width: 'auto' }"
                style="margin-left: 0px" placeholder="请选择" clearable :bordered="false" />
              <div class="line"></div>
              <w-input ref="textareaRef" v-model="searchText"  placeholder="输入您要搜索的问题" :max-length="80"
                :style="{ width: '460px' }" @keydown.enter.prevent="sendSearch" />
              <div class="btn-box audio">
                <w-space>
                  <i class="vedioBtn" id="btn_control">
                    <img src="/src/assets/chatImages/yuyin.svg" class="sendVoiceBtn" v-if="
                      btnStatus === 'UNDEFINED' ||
                      btnStatus === 'CLOSED' ||
                      btnStatus === 'CLOSING'
                    " @click="openYY('ly')" />
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
                      <CoolStopCircleLineWe size="28" color="var(--w-color-primary)" @click="recorderStop" />
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
          <div class="input-title" v-show="!showResultInput">
            <span class="title">{{ searchSureText }}</span>
            <img :src="editLine" alt="" @click="showInputEdit" />
          </div>
        </div>
        <searchResultAI v-if="isShowResult" :loading="isSearch" :searchText="searchSureText" :searchType="searchType"
          :processLi="10" type="zh" :searchWay="isSearch ? activeName : valueName" @previewClickli="previewClickli">
        </searchResultAI>
      </div>

    </div>
    <!-- 旧版回答结果页 -->
    <!-- v-if="!isSearch && previewClicklist.transPdfUrl" -->
    <div v-if="false" class="previewPdf">
      <div class="flex-just">
        <div style="
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 16px;
              color: #434649;
              line-height: 24px;
              margin-bottom: 10px;
            ">
          {{ previewClicklist.fileName }}
        </div>
        <div>
          <CoolCloseLineWe class="close" size="18" style="color:#828894" @click="previewClicklist = {}" />
        </div>
      </div>
      <div>
        <layoutCenterPdfPdf v-if="previewClicklist.transPdfUrl" :url="previewClicklist.transPdfUrl">
        </layoutCenterPdfPdf>
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
// import { setScrollPosition } from "/@/utils/other";
import { getPresetQuestionList, questionChartsTop50, getHotSearchWord, getFileSizeList, getHotSearchWordByQuestion, getHotSearch,
   getSubscribeRecommendList, getHotSearchFileList, getMySubscribeList, subscribe, addFileLike, getFileContent, getFileListByAppId } from "/@/api/knowledge";
import { simpleTransferTcc } from "/@/utils/conv";
import { ElMessage } from "element-plus";

import indexDetailResultStructured from "./index-detail-result-structured.vue";
import searchNewTop from "./components/index-detail-searchNew-top.vue"
const comDocPreview = defineAsyncComponent(() => import('/@/components/comDocPreview.vue'));
const chatStore = useChatStore();
const chatList = computed(() => chatStore.chatList);
const c = computed(() => chatStore.chatList);
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
import imgSearch from "/@/assets/images/img-search.png";
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
import editLine from "/@/assets/ai/edit-line.svg";
import titileAnswer from "/@/assets/ai/titileAnswer.png";
import list from "/@/assets/ai/ai-data.png";
import list1 from "/@/assets/ai/ai-data1.png";
import { debounce } from 'lodash-es';
import { useRouter } from 'vue-router';
// const router = useRouter();
// // 结果页
const searchResultAI = defineAsyncComponent(
  () => import("./components/searchNewResultHk.vue")
);
const layoutCenterPdfPdf = defineAsyncComponent(
  () => import("/src/components/previewPdf.vue")
);
const itemsImg = ref([
  { imageUrl: '/@/assets/ai/ai-data.png' },
  { imageUrl: '/@/assets/ai/ai-data1.png' },
  { imageUrl: '/@/assets/ai/list2.png' },
  { imageUrl: '/@/assets/ai/list3.png' }
]);
const dialogVisible = ref(false);
const isShow = ref(false);
const datapanelParas = computed(() => chatStore.datapanelParas);
const DatapanelRef = ref();
const startT = ref(0);
const endT = ref(0);
const textareaRef: any = ref(null);
const isInsertPrompt = ref(false);
const insertpromptText = ref("");
const insertpromptbox = ref();
const childComponent = ref<{ getSearchSourceFileListData: () => void } | null>(null);
const childComponent1 = ref<{ getSearchSourceFileListData: () => void } | null>(null)
const childComponent2 = ref<{ getStructuredDataFun: () => void} | null>(null)
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
const reSearch = ref(true);
const isShowResult = ref(false)
// 音频按钮状态
const btnStatus = ref("UNDEFINED");
// 推荐问题
const tjQuestList = ref([]);

const searchTextvalue = ref('');
const valueName = ref("MIXED");
const fieldNames = { value: "id", label: "name" };
const previewClicklistData = ref({});
const isLogin = ref(false)
const options = reactive([
  {
    id: "MIXED",
    name: "MIXED",
  },
  {
    id: "FULL_TEXT",
    name: "FULL_TEXT",
  },
  {
    id: "SEMANTIC",
    name: "SEMANTIC",
  },
]);
const countryData = ref([])
const popularDocuments = ref([])
const mySubscriptions = ref([]); // 我的订阅列表
const previewClicklist = ref({})
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
const hotSearchFileListData = ref([])
let resultActiveTab = ref('AI搜索')
let resultTabList = ref(['AI搜索', '结构化数据', '企业库'])



function resultActiveTabFun(activeTab: string) {
if (chatStore.dialogueLoading && (activeTab === '结构化数据'|| activeTab === '企业库')) {
    return;
  }
  resultActiveTab.value = activeTab
  switch (activeTab) {
    case 'AI搜索':
      break;
    case '结构化数据':	
	if (childComponent2.value && reSearch.value) {
	   childComponent2.value.getStructuredDataFun();
     reSearch.value = false;
	}
      break;
    case '企业库':
      break;
    case '文档':	
      if (childComponent.value) {
         childComponent.value.getSearchSourceFileListData();
      }
      break;
    case '图片':
      if (childComponent1.value) {
         childComponent1.value.getSearchSourceFileListData();
      }
      break;
    default:
      break;
  }
}
const logoUrl = () => {
    let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
    return appInfo ? appInfo.logo : '';
};
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo : '';
};
//判断是否登录
const isToken = ref(false);

const checkAccessToken = () => {
  const storedUserInfoString = sessionStorage.getItem('manageAccessToken');
  if (storedUserInfoString) {
   return true;
  }
  return false;
};


// 新内容位置
let isSearchZanShi = ref(true)
function transitionisSearchZanShi() {
  isSearch.value = !isSearch.value;
}


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
const applicationId = ref(localStorage.getItem(`${route.params.appId}appId`));



// 切换标签页
const switchTab = (tab) => {
      activeTab.value = tab;
      if (tab === 'my' && mySubscriptions.value.length === 0) {
        getMySubscribeListFun();
      }
    };
    
    // 订阅项目
    const subscribeItem = async (item:any) => {
      if (item.isSub === '1') return;
      try {
        await subscribe({
          applicationId: localStorage.getItem(`${route.params.appId}appId`),
          fileId: item.fileId,
          status: 1
        });
        // 添加到我的订阅列表
        mySubscriptions.value.push(item);
        // 更新推荐列表中的订阅数
        item.isSub = '1';
        item.subNum = (parseInt(item.subNum) + 1).toString();
      } catch (error) {
        console.error('订阅失败:', error);
      }
    };
    
    // 取消订阅项目
    const unsubscribeItem = async (item:any) => {
       if (item.isSub === '0') return;
      try {
        await subscribe({
          applicationId: localStorage.getItem(`${route.params.appId}appId`),
          fileId: item.fileId,
          status: 0
        });
        item.isSub = '0';
        item.subNum = (parseInt(item.subNum) - 1).toString();
        const index = mySubscriptions.value.findIndex((subItem: any) => subItem.fileId === item.fileId);
        if (index !== -1) {
          mySubscriptions.value.splice(index, 1);
        }
        const existingIndex = popularDocuments.value.findIndex((doc: any) => doc.fileId === item.fileId);
        if (existingIndex !== -1) {
          popularDocuments.value.splice(existingIndex, 1);
        }
        popularDocuments.value.unshift(item);
      } catch (error) {
        console.error('取消订阅失败:', error);
      }
    };
    
//获取热门文档列表
const getHotSearchFileListFun = async () => {
  let res = await getHotSearchFileList({
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
    pageNo: 1,
    pageSize: 20,
    types: [0], 
  })
  hotSearchFileListData.value = res.data.records;
}
//点赞文档
const handleLike = async (item:any) => {
    await addFileLike({
      applicationId: localStorage.getItem(`${route.params.appId}appId`),
      fileId: item.fileId,
    }); 
    // 更新文档的点赞数
    item.likeNum += 1;
}
const previewRef = ref(null);
//查看文档
const handlePreview = async (item:any) => {
    await getFileListByAppId({
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
    pageNo: 1,
    pageSize: 20,
    question: searchText.value,
    types: [0],   
    fileTypes: ["pdf"],
  });
  previewClicklist.value = item;
  previewRef.value.openPreview(item, '预览文件', 'pdf');
  await getFileContent({
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
    fileId: item.fileId,
  })
  item.readNum += 1;
}

const handleCountryData = (item:any) => {
  searchText.value = item.keyword;
  sendSearch(); 
}

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

//登录
const toLogin = () => {
	if (isToken.value) return;
	dialogVisible.value = true;
};

// 进入搜索结果页
const sendSearch = debounce(() => {
  // 重新搜索之前 清空对话
  if (searchText.value) {
    reSearch.value = true;
    chatStore.chatList = [];
    chatStore.dialogueLoading = false;
    isSearch.value = false;
    searchSureText.value = searchText.value;
    isShowResult.value = true;
    searchTextvalue.value = searchText.value;
    resultActiveTab.value = 'AI搜索';
    console.log("rrrreee");
    sendQuestion(searchText.value);
    if (childComponent.value) {
      childComponent.value.getSearchSourceFileListData();
    }
    if (childComponent1.value) {
      childComponent1.value.getSearchSourceFileListData();
    }
  }
}, 1000);
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


const isHovered = ref(false);
const handleMouseEnter = () => {
	if (isToken.value) {
		isHovered.value = true;
	}
};

const handleMouseLeave = () => {
	isHovered.value = false;
};

const outLoginDialogVisible = ref(false)




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

let APPID = '';
let API_SECRET = '';
let API_KEY = '';
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
  APPID = tts_config.APPID;
  API_SECRET = tts_config.API_SECRET;
  API_KEY = tts_config.API_KEY;
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
const capitalizeFirstLetter = (str: string) => {
  if (typeof str !== 'string' || str.length === 0) {
    return str;
  }
  return str.replace(/^./, (firstChar) => firstChar.toUpperCase());
}
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
});

const activeTab = ref('recommend');
onUpdated(() => { });
</script>

<style lang="scss" scoped>
::v-deep(.zidingyi-w-list-item-content .w-list-item-content) {
  display: block !important;
}

.isSearchZanShiClass {
  position: fixed;
  top: 10px;
  right: 300px;
}

.result-box {
  width: 100%;
  height: 100%;
  background: #f4f9ff;
  position: relative;
  background-color: #fafbfc;

  .result-header-left {
    position: absolute;
    top: 10px;
    left: 20px;
    width: 44px;
    height: 44px;
    cursor: pointer;
  }

  .result-header {
    height: 72px;
    position: relative;
    background-color: #ffffff;
    display: flex;
    align-items: center;

    .result-header-right {
      width: 1200px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;

    }


  }

  .result-tab-box {
    .result-tab-title {
      font-size: 32px;
      margin: 24px 0;
    }

    .result-border {
      margin-top: 16px;
      width: 100%;
      height: 1px;
      background: rgba(0, 0, 0, 0.08);
    }

    .result-tab-box-content {
      width: 1200px;
      margin: 0 auto;
    }

    .result-tabs {
      display: flex;

      .result-tab-title {
        font-size: 32px;
      }

      .tab-item {
        cursor: pointer;
        width: 113px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        font-size: 16px;
        border-radius: 10px;
      }

      .active {
        color: #365FAC;
        background: #fff;
      }
    }
  }

  .result-content-box {
    width: 1200px;
    margin: 0 auto;
  }

}

.new-user-box {
  position: absolute;
  right: 30px;
  top: 50%;
  transform: translate(0, -50%);

}

.ai-mian {
  padding-bottom: 0;
  height: 100%;
  background: url("/@/assets/ai/mainBeiJing.png") no-repeat;
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
  width: 100%;
  height: 100%;
  // margin: 20px 20px 20px 0px;

  border-radius: 16px;
  backdrop-filter: blur(3px);
  display: flex;
  flex-direction: column;
  // justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden; // 外层容器隐藏溢出
  .user-menu {
		background-color: #fff;
		position: absolute;
		right: 72px;
		top: 12px;
		box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.1);
		border-radius: 8px;
		box-sizing: border-box;
		padding: 8px;
		z-index: 99;
    font-size: 12px;
	}

	.user-menu:hover {
		cursor: pointer;
		color: #2065d6;
	}
  .content-box {
    flex: 1; // 中间内容区域占据剩余空间
    width: 100%;
    overflow-y: auto; // 中间内容超出时出现滚动条
    position: relative; // 相对定位
  }
  .bigImg {
					width: 40px;
					height: 40px;
					display: flex;
					justify-content: center;
					align-items: center;

					&:hover {
						cursor: pointer;
					}

					.img {
						width: 40px;
						height: 40px;
						display: flex;
						justify-content: center;
						align-items: center;
						// border-radius: 50%;
						// background: #EBEDF0;

						&:hover {
							background: rgb(134, 144, 156, 0.1);
							backdrop-filter: blur(4px);
							cursor: pointer;
							width: 40px;
							height: 40px;
							border-radius: 50%;
						}

						.login {
							font-family: MiSans, MiSans;
							font-weight: 500;
							font-size: 14px;
							color: #797F8A;
							line-height: 20px;
							text-align: left;
							font-style: normal;
						}
					}
				}
  .title-box {
    margin-top: 106px;
    img {
      // width: 100%;
      // height: 100%; 
      margin: 0 auto;
    }
  }

  .logo-img {
    width: 173px;
    position: absolute;
    left: 24px;
    top: 16px;
  }

  .new-user-box {
    position: absolute;
    right: 30px;
    top: 3%;
    transform: translate(0, -50%);
  }

  .tab-box {
    width: 340px;
    height: 40px;
    background: #ffffff;
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
    // position: fixed;
    // bottom: 0;
    // left: 0;
    width: 100%;
    height: 112px;
    background: linear-gradient(174deg, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.2) 100%);
    box-shadow: inset 0px 1px 3px 0px rgba(255, 255, 255, 0.5);
    border-radius: 8px 8px 0px 0px;
    backdrop-filter: blur(4px);
    display: flex;
    justify-content: center;
    align-items: center;

    .file-item {
      // display: flex;
      // justify-content: center;
      // align-items: center;
      margin-left: 32px;

      >div:nth-child(1) {
        display: flex;
        align-items: center;
        color: #828894;
        margin-bottom: 5px;
      }

      >div:nth-child(2) {
        text-align: center;
        color: #383D47;

        span:nth-child(1) {
          font-size: 18px;
        }

        span:nth-child(2) {
          font-size: 14px;
        }
      }

      img {
        height: 13px;
        width: 13px;
        margin-right: 5px;
      }

      .data {
        // margin-left: 16px;
      }
    }
  }

  .input-box {
    width: 800px;
    height: 96px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 16px;
    border: 2px solid #828894;
    backdrop-filter: blur(1px);
    padding: 5px;
    position: relative;
    margin: 0 auto;
    :deep(.el-input__wrapper) {
      box-shadow: none;
      height: 40px;
      line-height: 40px;
    }
    :deep(.w-textarea-wrapper) {
      padding-left: 1px;
      border: 0 !important;
      background-color: #fff;
      border-radius: 12px;
      textarea{
        resize: none;
      }
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
        border-radius: 16px;
        cursor: pointer;

        img {
          width: 100%;
          height: 100%;
        }
      }
    }
  }

  .topic-box {
    width: 830px;
    height: 300px;
    margin-top: 6vh;
    background-color: rgba(255, 255, 255, 0.7);
    padding-top: 30px;

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
      white-space: nowrap;
      /* 不换行 */
      overflow: hidden;
      /* 隐藏超出的内容 */
      text-overflow: ellipsis;
      /* 用省略号表示被隐藏的部分 */
      max-width: 300px;
      /* 设置最大宽度以限制文本的显示长度 */
      cursor: pointer;
    }

    .activehove:hover {
      background-color: #fff;
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


.country {
  width: 100%;
  margin-top: 30px;

  ul {
    width: 856px;
    // height: 76px;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    margin: 0 auto;

    li {
      height: 32px;
      // width: calc(100% / 7 - 10px);
      padding: 6px 8px 6px 11px;
      background-color: #ffffff;
      border-radius: 16px;
      margin-right: 12px;
      margin-bottom: 12px;
      cursor: pointer;

      // &:not(:nth-child(7n)) {
      //   margin-right: 10px; // 每行前6个添加右边距
      // }
      .icon {
        color: #7197F7;
        font-size: 14px;
      }

      .name {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #1D2129;
        line-height: 20px;
        text-align: left;
        font-style: normal;
        padding-left: 4px;
        padding-right: 11px;
      }

      .num {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #86909C;
        line-height: 16px;
        text-align: center;
        font-style: normal;
      }
    }
  }
}

.popular-documents {
  width: 100%;
  margin-top: 24px;
  display: flex;

  .popular-documents-box {
    // height: 662px;
    margin-right: 24px;
  }

  .popular-left {
    margin-left: 192px;
    width: 1122px;
    height: 312px;
    background: #FFFFFF;
    border-radius: 12px;
    margin-bottom: 24px;
    // opacity: 0.7;
    backdrop-filter: blur(2px);
    padding: 20px 18px 16px 16px;

    .title {
      height: 24px;
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 18px;
      color: #383D47;
      line-height: 24px;
      text-align: left;
      font-style: normal;
      text-transform: uppercase;
    }

    ul {
      margin-top: 20px;
      display: flex;
      // justify-content: space-between;
    }

    .popular-list {
      padding: 8px;
      width: 260px;
      height: 232px;
      background: #FFFFFF;
      border-radius: 8px;
      border: 1px solid #E7E7E7;
      margin-right: 16px;
      &:last-child {
       margin-right: 0; 
      }
      .img {
        width: 244px;
        height: 167px;
        border-radius: 4px;
        img {
          width: 100%;
          height: 100%;
        }
        // background-color: #2560cf;
      }

      .text {
        margin-top: 8px;
        height: 20px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 14px;
        color: #383D47;
        line-height: 20px;
        text-align: left;
        font-style: normal;
        margin-bottom: 5px;
        white-space: nowrap;
        /* 不换行 */
        overflow: hidden;
        /* 隐藏超出的内容 */
        text-overflow: ellipsis;
      }

      .info {
        display: flex;
        align-items: center;
        height: 16px;

        .info-left {
          margin-right: 13px;
          display: flex;
        }

        .info-right {
          display: flex;
        }

        .info-num {
          height: 16px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          color: #828894;
          line-height: 16px;
          text-align: left;
          font-style: normal;
          text-transform: none;
        }
      }
    }
  }

  .subscribe-box {
    width: 1122px;
    height: 326px;
    background: #FFFFFF;
    border-radius: 12px;
    backdrop-filter: blur(2px);
    margin-left: 192px;
    padding: 20px 18px 16px 16px;
    margin-bottom: 24px;

    .title {
      height: 24px;
      display: flex;
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 18px;
      color: #828894;
      line-height: 24px;
      text-align: left;
      font-style: normal;
      text-transform: uppercase;
      cursor: pointer;

      .title-left {
        margin-right: 16px;
      }

    }

    .active {
      color: #383D47;
    }

    ul {
      margin-top: 20px;
      display: flex;
      // justify-content: space-between;
    }

    .popular-list {
      width: 260px;
      height: 246px;
      background: #FFFFFF;
      border-radius: 8px;
      border: 1px solid #E7E7E7;
      padding: 8px;
      margin-right: 16px;
      &:last-child {
      margin-right: 0;
    }
      .img {
        width: 244px;
        height: 138px;
        border-radius: 4px;
        // background-color: #2560cf;
        img {
          width: 100%;
          height: 100%; 
        }
      }

      .text {
        margin-top: 8px;
        height: 20px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 14px;
        color: #383D47;
        line-height: 20px;
        text-align: left;
        font-style: normal;
        margin-bottom: 4px;
        white-space: nowrap;
        /* 不换行 */
        overflow: hidden;
        /* 隐藏超出的内容 */
        text-overflow: ellipsis;
      }

      .info {
        height: 16px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #828894;
        line-height: 16px;
        text-align: left;
        font-style: normal;
        text-transform: none;
        margin-bottom: 12px;
      }

      .but-subscribe {
        width: 244px;
        height: 32px;
        background: #4D77EF;
        border-radius: 4px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #FFFFFF;
        line-height: 32px;
        text-align: center;
        font-style: normal;
        text-transform: none;
        cursor: pointer;
      }
      .but-subscribe.disabled {
        background-color: #cccccc;
        cursor: not-allowed;
      }
      .but-unsubscribe {
        width: 244px;
        height: 32px;
        background: #828894;
        border-radius: 4px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #FFFFFF;
        line-height: 32px;
        text-align: center;
        font-style: normal;
        text-transform: none;
        cursor: pointer;
      }
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
  border-bottom: 1px solid #d0d7df;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
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
  width: 720px;
  height: 40px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #B4BCCC;
  backdrop-filter: blur(1px);
  display: flex;
  align-items: center;

  .inputRe-content {
    width: 100%;
    display: flex;
    align-items: center;
    position: relative;

    .inputRe-left-icon {
      margin-left: 12px;
      display: flex;
      flex-direction: column;
      align-items: center;
    }
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
    height: 40px;
    background: transparent;
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
  width: 700px;
  height: calc(100vh - 40px);
  padding: 20px;
  border-radius: 16px;
  border-image: linear-gradient(139deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0)) 1 1;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(3px);
}

.flex-just {
  justify-content: space-between;
  display: flex;
}
</style>
