<template>
	<div class="ai-mian" :class="{ bgstyle: !isSearch }">
		<div class="user-box">
			<img class="img-logo" style="height: 40px; width: 40px; cursor: pointer" src="/src/assets/ai/img-logo.png" alt="" @click="backHome" />
			<div class="user-info">
				<span>歡迎您，admin </span>
				<img :src="user" />
			</div>
		</div>
		<div class="inputRe-box" v-if="!isSearch">
			<div class="inputRe-content">
				<img :src="valueName == 'MIXED' ? ks : valueName == 'FULL_TEXT' ? zq : yj" class="icon" />
				<w-select
					v-model="valueName"
					:options="options"
					:field-names="fieldNames"
					:style="{ width: '120px' }"
					style="margin-left: 12px"
					placeholder="請選擇"
					clearable
					:bordered="false"
				/>
				<div class="line"></div>
				<w-input
					ref="textareaRef"
					v-model="searchText"
					placeholder="輸入您要搜索的問題"
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
								v-if="btnStatus === 'UNDEFINED' || btnStatus === 'CLOSED' || btnStatus === 'CLOSING'"
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
		<div class="mian-box" v-if="isSearch">
			<img class="logo-img" src="/src/assets/ai/img-logo2.png" alt="" />
			<div class="tab-box">
				<div class="tab" v-for="(item, index) in options" :key="index" :class="{ active: activeName == item.id }" @click="handleActiveName(item.id)">
					<img v-if="index == 0" :src="activeName == item.id ? ksAc : ks" />
					<img v-else-if="index == 1" :src="activeName == item.id ? zqAc : zq" />
					<img v-else :src="activeName == item.id ? yjAc : yj" />
					<span>{{ item.name }}</span>
				</div>
			</div>
			<div class="input-box">
				<w-textarea
					ref="textareaRef"
					v-model="searchText"
					placeholder="輸入您要搜索的問題"
					:auto-size="{ minRows: 1, maxRows: 6 }"
					:max-length="80"
					@keydown.enter="sendSearch"
					clearable
				/>
				<div class="btn-box audio">
					<div></div>
					<!-- <w-select :style="{ width: '120px' }" v-model="value" placeholder="请选择" clearable size="medium">
						<w-option>全库搜索</w-option>
						<w-option>知识库搜索</w-option>
					</w-select> -->
					<w-space>
						<i class="vedioBtn" id="btn_control">
							<img
								src="/src/assets/chatImages/yuyin.svg"
								class="sendVoiceBtn"
								@click="openYY('ly')"
								v-if="btnStatus === 'UNDEFINED' || btnStatus === 'CLOSED' || btnStatus === 'CLOSING'"
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
								<CoolStopCircleLineWe size="28" color="var(--w-color-primary)" @click="recorderStop" />
							</div>
						</i>
						<w-button class="sendBtn" @click="sendSearch"> 發 送 </w-button>
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
						@click="sendQuestion(simpleTransferTcc(item.question))"
					>
						<template #default> <img :src="corner" style="width: 16px; height: 16px" /></template>
						<template #icon>{{ simpleTransferTcc(item.question) }} </template>
					</w-button>
					<w-button type="secondary" shape="round">
						<template #icon>
							<CoolRefreshLineWe size="14" color="#646479" />
						</template>
						<span @click="getPresetQuestionListFun">換一批</span>
					</w-button>
				</w-space>
			</div>
			<!-- <div class="bottom-box">
				<span style="margin-right: 28px">已收集</span>
				<div class="file-item">
					<img :src="word" />
					<span>文档</span>
					<span class="data">2465888 个</span>
				</div>
				<div class="file-item">
					<img :src="audio" />
					<span>音频</span>
					<span class="data">1465839 个</span>
				</div>
				<div class="file-item">
					<img :src="piceture" />
					<span>图片</span>
					<span class="data">254877 张</span>
				</div>
			</div> -->
		</div>
		<searchResultAI v-else :loading="isSearch" :searchText="searchSureText" :searchWay="isSearch ? activeName : valueName"></searchResultAI>
	</div>
</template>
  
<script lang="ts" setup>
import 'splitpanes/dist/splitpanes.css';
import { computed, defineAsyncComponent, onMounted, onUpdated, ref, watch, reactive } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { useRoute } from 'vue-router';
import { setScrollPosition } from '/@/utils/other';
import { getPresetQuestionList } from '/@/api/knowledge';
import { simpleTransferTcc } from '/@/utils/conv';
import { ElMessage } from 'element-plus';
import { getRandomConfig } from '/@/utils/ttsConfig';

const chatStore = useChatStore();
const chatList = computed(() => chatStore.chatList);
const route = useRoute();

import user from '/@/assets/ai/user.svg';
import ks from '/@/assets/ai/ks.svg';
import ksAc from '/@/assets/ai/ks-ac.svg';
import zq from '/@/assets/ai/zq.svg';
import zqAc from '/@/assets/ai/zq-ac.svg';
import yj from '/@/assets/ai/yj.svg';
import yjAc from '/@/assets/ai/yj-ac.svg';
import search from '/@/assets/ai/search.svg';
import { fa, tr } from 'element-plus/es/locale';
import sendIcon from '/@/assets/chatImages/send.svg';
import audio from '/@/assets/ai/audio.svg';
import piceture from '/@/assets/ai/piceture.svg';
import word from '/@/assets/ai/word.svg';
import corner from '/@/assets/ai/corner.svg';

// 结果页
const searchResultAI = defineAsyncComponent(() => import('./components/searchResultAI.vue'));
// const chatInputAI = defineAsyncComponent(() => import('./components/chatInput-ai.vue'));

const datapanelParas = computed(() => chatStore.datapanelParas);
const DatapanelRef = ref();
const startT = ref(0);
const endT = ref(0);
const textareaRef: any = ref(null);
const isInsertPrompt = ref(false);
const insertpromptText = ref('');
const insertpromptbox = ref();

//  快速、增强，研究
const activeName = ref('MIXED');
// 搜索关键词
const searchText = ref('');
const searchSureText = ref('');
const curAsktext = ref('');
// 下拉库选择
const value = ref('全库搜索');
// 是否显示搜索结果页
const isSearch = ref(true);
// 音频按钮状态
const btnStatus = ref('UNDEFINED');
// 推荐问题
const tjQuestList = ref([]);
const valueName = ref('MIXED');
const fieldNames = { value: 'id', label: 'name' };
const options = reactive([
	{
		id: 'MIXED',
		name: '混合檢索',
	},
	{
		id: 'FULL_TEXT',
		name: '全文檢索',
	},
	{
		id: 'SEMANTIC',
		name: '語義檢索',
	},
]);

// 切换
const handleActiveName = async (data: any) => {
	activeName.value = data;
	valueName.value = data;
};
// 返回首页
const backHome = async () => {
	isSearch.value = true;
	chatStore.chatList = [];
	chatStore.dialogueLoading = false;
};

// 进入搜索结果页
const sendSearch = async () => {
	// 重新搜索之前 清空对话
	if (searchText.value) {
		chatStore.chatList = [];
		chatStore.dialogueLoading = false;
		isSearch.value = false;
		searchSureText.value = searchText.value;
		sendQuestion(searchText.value);
	}
};
// 获取推荐问题
const getPresetQuestionListFun = async () => {
	tjQuestList.value = [];
	let res = await getPresetQuestionList({
		pageNo: 1,
		pageSize: 4,
		type: '推荐问题',
		status: 1,
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
	});
	tjQuestList.value = res.data.records;
};
// 搜索问题
const sendQuestion = async (item: string) => {
	searchSureText.value = item;
	searchText.value = item;
	isSearch.value = false;

	if (chatStore.dialogueLoading) return;
	const data: any = {
		content: item,
		conversationId: route.params.conversationId,
		knowledgeBaseId: route.params.appId,
		appId: route.params.appId,
		searchWay: !isSearch ? valueName.value : activeName.value,
	};
	chatStore.setChatList(data);
	return;
};
const recorder = new RecorderManager('./iat');
const audioPlayer = new AudioPlayer('./tts');
// 语音
const openYY = (type: string = '') => {
	searchText.value = '';
	btnControlFun(type);
};
// 录音相关开始
const btnControlFun = (type: string = '') => {
	if (btnStatus.value === 'UNDEFINED' || btnStatus.value === 'CLOSED') {
		connectWebSocket(type);
	} else {
		// 结束录音
		recorder.stop();
	}
};
// 暂停录音
const recorderStop = () => {
	recorder.stop();
	changeBtnStatus('CLOSED');
};
const chatFlag = ref(false);
const chatSBFlag = ref(false);
const resultVal = ref('');

let APPID =  '';
let API_SECRET =  '';
let API_KEY =  '';
let iatWS;
let resultText = '';
let resultTextTemp = '';
let countdownInterval;
const textFileIds: any = ref([]);
const textFolderIds: any = ref([]);
const instructObj: any = ref({});
const getWebSocketUrl = () => {
	// 请求地址根据语种不同变化
	var url = 'wss://iat-api.xfyun.cn/v2/iat';
	var host = 'iat-api.xfyun.cn';
	var apiKey = API_KEY;
	var apiSecret = API_SECRET;
	var date = new Date().toGMTString();
	var algorithm = 'hmac-sha256';
	var headers = 'host date request-line';
	var signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v2/iat HTTP/1.1`;
	var signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret);
	var signature = CryptoJS.enc.Base64.stringify(signatureSha);
	var authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
	var authorization = btoa(authorizationOrigin);
	url = `${url}?authorization=${authorization}&date=${date}&host=${host}`;
	return url;
};
const connectWebSocket = (type: string = '') => {
  const tts_config = getRandomConfig();
  APPID =  tts_config.APPID;
  API_SECRET =  tts_config.API_SECRET;
  API_KEY =  tts_config.API_KEY;
	const websocketUrl = getWebSocketUrl();
	if ('WebSocket' in window) {
		iatWS = new WebSocket(websocketUrl);
	} else if ('MozWebSocket' in window) {
		iatWS = new MozWebSocket(websocketUrl);
	} else {
		alert('浏览器不支持WebSocket');
		return;
	}
	changeBtnStatus('CONNECTING');
	iatWS.onopen = (e) => {
		// 开始录音
		if (type == 'toChat') {
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
				language: 'zh_cn',
				domain: 'iat',
				accent: 'mandarin',
				vad_eos: 2400000,
				dwa: 'wpgs',
			},
			data: {
				status: 0,
				format: 'audio/L16;rate=16000',
				encoding: 'raw',
			},
		};
		iatWS.send(JSON.stringify(params));
		changeBtnStatus('OPEN');
	};
	iatWS.onmessage = (e) => {
		chatSBFlag.value = true;
		renderResult(e.data);
	};
	iatWS.onerror = (e) => {
		ElMessage.error('录音异常，请检查是否插入麦克风设备及开启麦克风权限');
		recorder.stop();
		changeBtnStatus('CLOSED');
	};
	iatWS.onclose = (e) => {
		recorder.stop();
		changeBtnStatus('CLOSED');
	};
};
const changeBtnStatus = (status) => {
	btnStatus.value = status;
	if (status === 'CONNECTING') {
		resultVal.value = '建立连接中';
		searchText.value = '';
		resultText = '';
		resultTextTemp = '';
	} else if (status === 'OPEN') {
		countdown();
	} else if (status === 'CLOSING') {
		resultVal.value = '关闭连接中';
	} else if (status === 'CLOSED') {
		searchText.value = '';
		resultVal.value = '开始录音';
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
	console.log('识别结束识别结束', resultData);
	recorder.stop();
	changeBtnStatus('CLOSED');
	// 识别结束
	let jsonData = JSON.parse(resultData);
	if (jsonData.data && jsonData.data.result) {
		let data = jsonData.data.result;
		let str = '';
		let ws = data.ws;
		for (let i = 0; i < ws.length; i++) {
			str = str + ws[i].cw[0].w;
		}
		// 开启wpgs会有此字段(前提：在控制台开通动态修正功能)
		// 取值为 "apd"时表示该片结果是追加到前面的最终结果；取值为"rpl" 时表示替换前面的部分结果，替换范围为rg字段
		if (data.pgs) {
			if (data.pgs === 'apd') {
				// 将resultTextTemp同步给resultText
				resultText = resultTextTemp;
			}
			// 将结果存储在resultTextTemp中
			resultTextTemp = resultText + str;
		} else {
			resultText = resultText + str;
		}
		searchText.value = resultTextTemp || resultText || '';
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
	getPresetQuestionListFun();
});
onUpdated(() => {});
</script>
  
<style lang="scss" scoped>
.ai-mian {
	padding-bottom: 0;
	height: 100%;
	background: url('/@/assets/ai/ai-bg.png') no-repeat;
	background-size: 100% 100%;
	position: relative;
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
	height: calc(100% - 20%);
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-top: 4%;
	position: relative;

	.logo-img {
		height: 52px;
		width: 203px;
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
		position: absolute;
		bottom: 5%;
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
		background: linear-gradient(180deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
		box-shadow: 0px 4px 0px 0px rgba(37, 96, 207, 0.2);
		border-radius: 12px;
		border: 1px solid #2560cf;
		backdrop-filter: blur(1px);
		padding: 5px;
		position: relative;

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
			justify-content: space-between;

			:deep(.w-select) {
				border-radius: 8px;
			}
			:deep(.w-select-view-single) {
				background-color: #f0f2f5;
			}
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
		}
	}
}

.inputRe-box {
	position: absolute;
	top: 10px;
	left: 200px;
	width: 720px;
	height: 48px;
	background: linear-gradient(180deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
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
</style>