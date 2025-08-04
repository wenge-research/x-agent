<template>
	<citationContent v-if="!isChat && citationText" @instructSend="instructSend" @instructClear="instructSet"> </citationContent>
	<!-- <div class="tj"  v-if="!isMobile" :style="{ 'justify-content': curRouteId == judicialUrl ? 'flex-end' : 'flex-start' }">
		<div class="left-title" v-if="curRouteId == policyUrl">推荐主题：</div>
		<div class="center-box" v-if="curRouteId == policyUrl">
			<div class="center-item" v-for="(item, index) in tjQuestList" @click="sendQuestion(item.question)" :key="index">
				<div class="box">{{ item.question }}</div>
			</div>
		</div>
		<div @click="gotoTop" class="right-back">
			<div class="back">
				<img :src="backIcon" alt="" />
			</div>
		</div>
	</div> -->
	<div class="tj" v-if="!isMobile" :style="{ 'justify-content': 'flex-end' }">
		<div @click="gotoTop" class="right-back">
			<div class="back">
				<img :src="backIcon" alt="" />
			</div>
		</div>
	</div>
	<div class="chatInput" :class="{ 'chatInput-mobiles': isMobile, 'chatInput-pc': !isMobile }">
    
		<div v-show="isInsertPrompt" class="yayi-editor-wrapper">
			<div ref="insertpromptbox" class="yayi-editor scroll-display-none" @input="setDivInput()" contenteditable="true"></div>
			<span v-if="insertpromptText.length > 2000" class="w-textarea-word-limit yayi-editor-limit">{{ insertpromptText.length }}/{{ maxLength }}</span>
		</div>
		<!-- :class="text ? 'lineHeight1' : 'lineHeight2'" -->
		<w-textarea
			:class="{ active: (btnStatus === 'UNDEFINED' || btnStatus === 'CLOSED' || btnStatus === 'CLOSING') && !chatFlag }"
			v-show="!isInsertPrompt"
			ref="textareaRef"
			@keydown="setInputEnter($event)"
			v-model="text"
			:placeholder="isContentLengthLimit() ? placeholder : '在此输入您的问题'"
			:auto-size="{ minRows: 1, maxRows: 6 }"
			:max-length="isContentLengthLimit() ? 500 : null"
		/>
    <w-select v-model="model" placeholder="请选择模型" style="width: auto;min-width: 220px;position: absolute;left: 16px;bottom: 12px;">
      <w-option v-for="(item, index) in llmList" style="color: #828894" :label="item.modelName" :value="item.modelId"></w-option>
    </w-select>
	
		<div v-if="text">
			<span
				v-if="!dialogueInputLoading"
				:disabled="imgUrl == '' && insertpromptText == '' && text == ''"
				class="sendBtn"
				:class="{ sendBtnMobile: isMobile }"
				@click="setInput"
				style="width: 40px; height: 40px"
				><img style="width: 32px; height: 32px" src="/src/assets/mobileUniversalTemplate/send.svg"
			/></span>
		</div>
		<div v-else>
			<i
				v-if="!dialogueInputLoading && isHaveTtsId()"
				class="vedioBtn"
				id="btn_control"
				@click="openYY('ly')"
				:style="isMobile ? '' : 'top:28px;right:100px;'"
			>
				

				<div
					v-if="(btnStatus === 'UNDEFINED' || btnStatus === 'CLOSED' || btnStatus === 'CLOSING') && !chatFlag"
					style="width: 40px; height: 40px; display: flex; align-items: center; justify-content: center"
				>
					<iconpark-icon name="mic-line" size="20" color="#3F4247"></iconpark-icon>
				</div>
				<div v-else class="vedioLoaingBtn" :style="isMobile ? '' : 'top:-2px;'">
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
					<CoolStopCircleLineWe size="28" color="var(--w-color-primary)" />
				</div>
			</i>
		</div>
		<CvUpload v-if="route.name == 'chat'"></CvUpload>
		<div v-if="dialogueInputLoading && uploadImgStatusLoading" class="sendBtn"><w-spin /></div>
		
		<span
			v-if="dialogueInputLoading && !uploadImgStatusLoading"
			@click="handleclickStopChat"
			class="sendBtn"
			:class="{ sendBtnMobile: isMobile }"
			style="width: 40px; height: 40px"
			>
      <img style="width: 32px; height: 32px" src="/src/assets/mobileUniversalTemplate/stop.svg"
		/></span>
	</div>

	<el-dialog
		title=""
		v-model="chatFlag"
		width="80%"
		:modal="false"
		:append-to-body="true"
		custom-class="chat-dialog"
		class="chat-dialog"
		:class="{ mobile: isMobile }"
		:close-on-click-modal="false"
		:before-close="handleClose"
	>
		<div class="box">
			<div style="display: flex; justify-content: flex-start">
				<img src="/src/assets/chatImages/left.svg" style="width: 36px; height: 51px; margin: 0 0 0 16px" @click="quitYY" />
				<!-- <p style="width:calc(100% - 36px);color:#000;font-size:20px;margin:0;line-height:51px;text-align:center;">当前对话</p> -->
			</div>
			<div style="width: 364px; height: 364px; border-radius: 50%; background: rgba(255, 255, 255, 0.03); margin: 20px auto 0; position: relative">
				<div
					style="
						width: 264px;
						height: 264px;
						border-radius: 50%;
						background: rgba(255, 255, 255, 0.03);
						margin: auto;
						position: absolute;
						top: 50%;
						left: 50%;
						transform: translate(-50%, -50%);
					"
				>
					<div
						style="
							width: 192px;
							height: 192px;
							border-radius: 50%;
							background: rgba(255, 255, 255, 0.03);
							margin: auto;
							position: absolute;
							top: 50%;
							left: 50%;
							transform: translate(-50%, -50%);
						"
					>
						<img src="/src/assets/chatImages/womanchat.png" class="boxbg" />
					</div>
				</div>
			</div>
			<div class="chatType" v-if="btnStatus == 'OPEN' && btnStatus_tts != 'PLAY'">
				<span style="font-size: 22px; color: #fff; font-weight: 500; margin: 12px 0 8px">正在聆听…</span>
				<div style="color: #fff; font-size: 15px">提问将在语音输入中断5秒后结束</div>
				<div style="margin-top: 60px">
					<div style="display: flex; justify-content: center; align-items: center">
						<!-- <span style="display:inline-block;width:48px;height:48px;border-radius:50%;background:#E8EDF4;position:relative;margin:0 16px 0 0;">
							<img src="/src/assets/chatImages/zanting.svg" style="width:21px;height:21px;position:absolute;top:13px;left:13px;margin:0;">
						</span>
						<img src="/src/assets/chatImages/guaduan.svg" style="width:64px;height:64px;margin:0 67px 0 0;"> -->
						<img src="/src/assets/chatImages/guaduan.svg" style="width: 64px; height: 64px" @click="quitYY" />
					</div>
					<p style="font-size: 15px; color: #fff; text-align: center; margin-top: 24px">结束语音对话</p>
				</div>
			</div>
			<div class="chatType" v-if="btnStatus == 'CLOSED' && btnStatus_tts != 'PLAY' && chatSBFlag">
				<span style="font-size: 22px; color: #fff; margin-top: 24px">正在思考...</span>
				<img src="/src/assets/chatImages/guaduan.svg" style="width: 64px; height: 64px; margin: 90px auto 0" @click="quitYY" />
				<p style="font-size: 15px; color: #fff; text-align: center; margin-top: 12px">结束语音对话</p>
			</div>
			<div class="chatType" v-if="btnStatus == 'CLOSED' && btnStatus_tts != 'PLAY' && !chatSBFlag">
				<span style="font-size: 15px; color: #fff; text-align: center; margin-top: 24px">未识别到声音</span>
				<div class="stopBtn" @click="reStartBtnFun">重新开始</div>
			</div>
			<div class="chatType" v-if="btnStatus_tts == 'PLAY'">
				<div class="chatTypeText">正在回答...</div>
				<!-- <div class="stopBtn" style="font-family: MiSans, MiSans;font-weight: 400;font-size: 15px;color: #7F84A4;text-align: center;font-style: normal;">
				<i class="el-icon-video-pause"></i>双击屏幕，可以打断我说话</div> -->
				<img src="/src/assets/chatImages/guaduan.svg" style="width: 64px; height: 64px; margin: 60px auto 0" @click="quitYY" />
				<p style="font-size: 15px; color: #fff; text-align: center; margin-top: 12px">结束语音对话</p>
			</div>
		</div>
		<!-- <div class="abortBtn" @click="quitYY">退出沉浸式问答</div> -->
	</el-dialog>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, onMounted, computed, onUnmounted, watch, nextTick } from 'vue';
import { getLlmPageList } from '/@/api/chat';

import { useChatStore } from '/@/stores/chat';
import { useKnowledgeState } from '/@/stores/knowledge';
import mittBus from '/@/utils/mitt';
import { useRoute } from 'vue-router';
// import { useUserInfo } from '/@/stores/userInfo';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import backIcon from '/@/assets/chat/top.svg';
// const storesUserInfo = useUserInfo();
import CryptoJS from 'crypto-js';
// const Datapanel = defineAsyncComponent(() => import('./Datapanel.vue'));
const CvUpload = defineAsyncComponent(() => import('./cvUpload.vue'));
const citationContent = defineAsyncComponent(() => import('./citationContent.vue'));
const DatapanelRef = ref();
import toJSON from '/@/assets/json/circle.json';
import sendIcon from '/@/assets/chatImages/send.svg';
import { getPresetQuestionList, getTtsConfigList } from '/@/api/knowledge';
import { copyText, getcopyText } from '/@/utils/format';
import { ElMessage } from 'element-plus';
import { tr } from 'element-plus/es/locale';
import { textToSpeechAndPlay, stopPlayback, isPlaying } from '/@/utils/voiceFun';
import { getRandomConfig } from '/@/utils/ttsConfig';
import { setScrollPosition } from '/@/utils/other';

interface Props {
	row?: number;
}
const model = ref('')
const llmList = ref([])

const promptId = ref('');
// const props = defineProps<Props>();
const maxLength = ref(4000);
// const maxRow = ref(props.row || 5);
const startT = ref(0);
const endT = ref(0);
const uploadingStatus = ref(false);

const recorder = new RecorderManager('./iat');
const audioPlayer = new AudioPlayer('./tts');
// 移动端自适应相关
const { isMobile } = useBasicLayout();
const route = useRoute();
// const router = useRouter();
const chatStore = useChatStore();
const chatList = computed(() => chatStore.chatList);

const knowledgeState = useKnowledgeState();
// const dblclickName: any = computed(() => knowledgeState.dblclickName);
// const previewData: any = computed(() => knowledgeState.previewData);
const citationText = computed(() => knowledgeState.citationText);
const imgUrl: any = computed(() => chatStore.fileUpdateCV.url);

const isChat = computed(() => route.path.indexOf('chat') != -1);
const text = ref('');
const btnStatus = ref('UNDEFINED');
// let btnStatus = "UNDEFINED"; // "UNDEFINED" "CONNECTING" "PLAY" "STOP"
const btnStatus_tts = ref('UNDEFINED');
const resultVal = ref('');

const textareaRef: any = ref(null);

const isInsertPrompt = ref(false);
const insertpromptbox = ref();
const insertpromptText = ref('');
const datapanelParas = computed(() => chatStore.datapanelParas);
const param = computed(() => {
	return chatStore.dialogueParamsList;
});
const policyUrl: any = ref(localStorage.getItem(`${route.params.appId}appId`));
const judicialUrl: any = ref(import.meta.env.VITE_JUDICIAL_QA);
const curRouteId = ref('');
const chatFlag = ref(false);
const chatSBFlag = ref(false);
const openYY = (type: string = '') => {
	btnControlFun(type);
};
const apiGetLlmPageList = async () => {
  if(sessionStorage.getItem('llmList')){
    llmList.value = JSON.parse(sessionStorage.getItem('llmList'));
  } else {
    llmList.value = [];
    const params = {"modelName":"","status":"",fromClientFlag: '是',"manufacturer":"","pageSize":1000,"pageNo":1};
    const res = await getLlmPageList(params);
    if (res.code == "000000") {
      llmList.value = res.data.records || [];
      sessionStorage.setItem('llmList', JSON.stringify(llmList.value));
    }
  }
  model.value = sessionStorage.getItem('dazhouModel') ? sessionStorage.getItem('dazhouModel') : llmList.value[0].modelId;
}
const isHaveTtsId = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo && appInfo.voiceDialogueFlag == '是' ? true : false;
};
const isContentLengthLimit = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo && appInfo.contentLengthLimit == '是' ? true : false;
};
const quitYY = () => {
	chatFlag.value = false;
	chatSBFlag.value = false;
	stopPlayback();
	changeBtnStatus('CLOSED');
	changeBtnStatus_tts('STOP', 'quit');
	ttsWS?.close();
	audioPlayer.reset();
};
const stopBtnFun = () => {
	changeBtnStatus_tts('STOP');
	ttsWS?.close();
	audioPlayer.reset();
	btnControlFun();
};
const reStartBtnFun = () => {
	connectWebSocket();
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
let APPID = '';
let API_SECRET = '';
let API_KEY = '';
let iatWS;
let resultText = '';
let resultTextTemp = '';
let countdownInterval;
const textFileIds: any = ref([]);
const textFolderIds: any = ref([]);
const tjQuestList = ref([]);
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
	// text.value = text.value || '介绍下深圳';
	// console.log(text.value);
	// if(text.value){
	// 	setDialogueData(text.value)
	// }
	// return
	const tts_config = getRandomConfig();
	APPID = tts_config.APPID;
	API_SECRET = tts_config.API_SECRET;
	API_KEY = tts_config.API_KEY;
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

const handleclickStopChat = async () => {
	await chatStore.clearChat();
	chatStore.scrollbarBottom = true;
	setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
};

const changeBtnStatus = (status) => {
	btnStatus.value = status;
	if (status === 'CONNECTING') {
		resultVal.value = '建立连接中';
		text.value = '';
		resultText = '';
		resultTextTemp = '';
	} else if (status === 'OPEN') {
		countdown();
	} else if (status === 'CLOSING') {
		resultVal.value = '关闭连接中';
	} else if (status === 'CLOSED') {
		text.value = '';
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
const toBase64 = (buffer) => {
	var binary = '';
	var bytes = new Uint8Array(buffer);
	var len = bytes.byteLength;
	for (var i = 0; i < len; i++) {
		binary += String.fromCharCode(bytes[i]);
	}
	return window.btoa(binary);
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
		text.value = resultTextTemp || resultText || '';
	}
	// text.value = text.value || 'xxxxxxx副区长是谁';
	text.value = text.value.trim();
	console.log(text.value);
	if (text.value) {
		// setDialogueData(text.value)
		setInput();
	} else {
		// ElMessage.error('没有声音输入，将退出沉浸式问答！')
		// quitYY()
		chatSBFlag.value = false;
	}
	if (jsonData.code === 0 && jsonData.data.status === 2) {
		iatWS.close();
	}
	if (jsonData.code !== 0) {
		iatWS.close();
	}
};
recorder.onFrameRecorded = ({ isLastFrame, frameBuffer }) => {
	if (iatWS.readyState === iatWS.OPEN) {
		iatWS.send(
			JSON.stringify({
				data: {
					status: isLastFrame ? 2 : 1,
					format: 'audio/L16;rate=16000',
					encoding: 'raw',
					audio: toBase64(frameBuffer),
				},
			})
		);
		if (isLastFrame) {
			changeBtnStatus('CLOSING');
		}
	}
};
recorder.onStop = () => {
	clearInterval(countdownInterval);
};
const gotoTop = () => {
	const dom = document.querySelector('.center-side') as HTMLElement;
	dom.scrollTop = 0;
};
const sendQuestion = async (item: string) => {
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
	};
	chatStore.setChatList(data);
	return;
};
//录音相关结束

const setDialogueData = async (text: string, params?: object) => {
	let cvImgUrl = chatStore.fileUpdateCV.url;
	if (text == '' && cvImgUrl == '') {
		clearQuery();
		return;
	}

	if (!route.params.conversationId || route.params.conversationId == '' || chatStore.isSensitive) {
		chatStore.isSensitive = false;
		await chatStore.addHistory({ appId: route.params.appId }, { name: text });
		setTimeout(async () => {
			console.log(3, '关心助理h5 ques');
			let arr = {};
			if (params) {
				param.value.forEach((item) => {
					Object.keys(params).forEach((param) => {
						if (item.key == param) {
							item.defaultValue = params[param];
						}
					});
				});
			}
			chatStore.dialogueParamsList = param.value;

			param.value.forEach((item) => {
				arr[item.key] = item.defaultValue;
			});
			arr.promptId = promptId.value == '' ? route.params.appId : promptId.value;
			const data = {
				appId: promptId.value == '' ? route.params.appId : promptId.value,
				content: text,
				conversationId: route.params.conversationId,
				param: JSON.stringify(arr),
			};
			if (!isChat.value) {
				data['dialogueFileIds'] = textFileIds.value;
				data['dialogueFolderIds'] = textFolderIds.value;
				if (instructObj.value?.id) {
					data['skillId'] = instructObj.value.id;
				}
				if (citationText.value) {
					data['paragraph'] = citationText.value;
				}
			} else {
				if (chatStore.categoryId != '-1') {
					data['categoryId'] = chatStore.categoryId;
				}
				if (cvImgUrl != '') {
					data['imgUrl'] = [cvImgUrl];
				}
			}
			chatStore.fileUpdateCV.url = '';
			await chatStore.setChatList(data);
			promptId.value = '';
			clearQuery();
			instructSet('');
		}, 100);
	} else {
		let arr = {};
		if (params) {
			param.value.forEach((item) => {
				Object.keys(params).forEach((param) => {
					if (item.key == param) {
						item.defaultValue = params[param];
					}
				});
			});
		}
		chatStore.dialogueParamsList = param.value;

		param.value.forEach((item) => {
			arr[item.key] = item.defaultValue;
		});
		arr.promptId = promptId.value == '' ? route.params.appId : promptId.value;
		const data = {
			appId: promptId.value == '' ? route.params.appId : promptId.value,
			content: text,
			conversationId: route.params.conversationId,
			param: JSON.stringify(arr),
		};
		if (!isChat.value) {
			data['dialogueFileIds'] = textFileIds.value;
			data['dialogueFolderIds'] = textFolderIds.value;
			if (instructObj.value?.id) {
				data['skillId'] = instructObj.value.id;
			}
			if (citationText.value) {
				data['paragraph'] = citationText.value;
			}
		} else {
			if (chatStore.categoryId != '-1') {
				data['categoryId'] = chatStore.categoryId;
			}
			if (cvImgUrl != '') {
				data['imgUrl'] = [cvImgUrl];
			}
		}
		chatStore.fileUpdateCV.url = '';
		await chatStore.setChatList(data);
		promptId.value = '';
		clearQuery();
		instructSet('');
	}
};
//
const getPresetQuestionListFun = async () => {
	let res = await getPresetQuestionList({
		pageNo: 1,
		pageSize: 8,
		type: '主题',
		status: 1,
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
	});
	tjQuestList.value = res.data.records;
};
const clearQuery = () => {
	if (route.query.q || route.query.q == '') {
		let url = window.location.href.split('?')[0];
		window.history.pushState({}, 0, url);
	}
};

const placeholder = computed(() => {
	return JSON.parse(window.localStorage.getItem(`${route.params.appId}`))?.inputPlaceholder || '在此输入您的问题';
});
const dialogueInputLoading = computed(() => {
	return chatStore.dialogueLoading;
});

const uploadImgStatusLoading = computed(() => {
	return chatStore.uploadImgStatus;
});

const setInputEnter = async (e) => {
	chatStore.setPolicy({
		id: '',
		title: '',
	});
	if (datapanelParas.value.show) {
		if (e.key == 'ArrowUp') {
			e.stopPropagation();
			e.preventDefault();
			DatapanelRef.value.penelItemMove('up');
		}

		if (e.key == 'ArrowDown') {
			e.stopPropagation();
			e.preventDefault();
			DatapanelRef.value.penelItemMove('down');
		}
		if (!e.shiftKey && e.keyCode == 13) {
			e.cancelBubble = true;
			e.stopPropagation();
			e.preventDefault();
			if (e.keyCode == 13) {
				DatapanelRef.value.penelItemConfirm();
				return;
			}
		}
	}
	if ((e.shiftKey || e.metaKey || e.altKey) && 13 === e.keyCode) {
		e.stopPropagation();
		e.preventDefault();

		// text.value = text.value + '\n'; //增加换行符
		//获取光标位置
		getCursorPosition();
		text.value = text.value.slice(0, startT.value) + '\n' + text.value.slice(endT.value);
		let chatbox = document.querySelector('textarea');
		setCursorPosition(chatbox, startT.value + 1);
	} else if (13 === e.keyCode) {
		e.stopPropagation();
		e.preventDefault();
		setInput();
	}

	// if (!e.shiftKey && e.keyCode == 13) {
	// 	e.cancelBubble = true;
	// 	e.stopPropagation();
	// 	e.preventDefault();

	// 	if (e.keyCode == 13 && datapanelParas.value.show === 0) {
	// 		setInput();
	// 	}
	// }
};
//获取光标位置
const getCursorPosition = () => {
	var isIE = !!document.all;
	var start = 0,
		end = 0;
	var oTextarea = document.querySelector('textarea');
	if (isIE) {
		var sTextRange = document.selection.createRange();
		if (sTextRange.parentElement() == oTextarea) {
			var oTextRange = document.body.createTextRange();
			oTextRange.moveToElementText(oTextarea);
			for (start = 0; oTextRange.compareEndPoints('StartToStart', sTextRange) < 0; start++) {
				oTextRange.moveStart('character', 1);
			}
			for (var i = 0; i <= start; i++) {
				if (oTextarea.value.charAt(i) == '\n') {
					start++;
				}
			}
			oTextRange.moveToElementText(oTextarea);
			for (end = 0; oTextRange.compareEndPoints('StartToEnd', sTextRange) < 0; end++) {
				oTextRange.moveStart('character', 1);
			}
			for (var i = 0; i <= end; i++) {
				if (oTextarea.value.charAt(i) == '\n') {
					end++;
				}
			}
		}
	} else {
		start = oTextarea.selectionStart;
		end = oTextarea.selectionEnd;
	}
	startT.value = start;
	endT.value = end;
};

//设置光标位置
const setCursorPosition = (elem, index) => {
	var val = elem.value;
	var len = val.length;
	if (len < index) return;
	setTimeout(function () {
		elem.focus();
		if (elem.setSelectionRange) {
			// 标准浏览器
			elem.setSelectionRange(index, index);
		} else {
			// IE9-
			var range = elem.createTextRange();
			range.moveStart('character', -len);
			range.moveEnd('character', -len);
			range.moveStart('character', index);
			range.moveEnd('character', 0);
			range.select();
		}
	}, 10);
};
const setInput = async () => {
  sessionStorage.setItem('dazhouModel', model.value);
	if (chatStore.dialogueLoading) {
		return;
	}
	let str = text.value;
	if (isInsertPrompt.value) {
		let ids = await getInsertHtmlId();
		// knowledgeState.setTextFileIds(ids);
		textFileIds.value = ids.fileIds || [];
		textFolderIds.value = ids.folderIds || [];
		if (ids.skillId) {
			instructObj.value.id = ids.skillId;
		}
		str = insertpromptText.value;
	}
	if (instructObj.value?.name) {
		str = instructObj.value?.name + str;
	}
	console.log(str.trim(), 'str.trim()');
	setDialogueData(str.trim());
	text.value = '';
	cleatInput();
};

const instructSend = (item: any) => {
	instructObj.value = item;
	setInput();
};
const instructSet = (val: string) => {
	knowledgeState.setCitationText(val);
	instructObj.value = {};
};

const getInsertHtmlId = () => {
	const elements = insertpromptbox.value.querySelectorAll('.dataId');
	const ids = {
		fileIds: [],
		folderIds: [],
		skillId: '',
	};
	for (let i = 0; i < elements.length; i++) {
		const id = elements[i].attributes['data-id'].value;
		const type = elements[i].attributes['data-type'].value;
		const prompt = elements[i].attributes['data-prompt'].value;
		if (prompt) {
			ids.skillId = id;
		} else {
			if (type == '1') {
				ids.folderIds.push(id);
			} else {
				ids.fileIds.push(id);
			}
		}
	}
	return ids;
};

// 指令输入框代码开始****************************
const cleatInput = () => {
	insertpromptbox.value && (insertpromptbox.value.innerHTML = '');
	isInsertPrompt.value = false;
	insertpromptText.value = '';
	text.value = '';
};
const limitWords = () => {
	if (insertpromptbox.value.innerText.length > maxLength.value) {
		insertpromptbox.value.innerText = insertpromptbox.value.innerText.substring(0, maxLength.value);
		setTimeout(() => {
			insertpromptbox.value.focus();
			document.execCommand('selectAll', false, null);
			document.getSelection().collapseToEnd();
		}, 0);
	}
};
const setDivInput = () => {
	limitWords();
	let text = '';
	text = insertpromptbox.value.innerText;
	insertpromptText.value = text;
	if (text == '\n') {
		cleatInput();
	}
	return text;
};

const promptInsertHtml = (data) => {
	let text = data.prompt;
	if (!data) {
		return;
	}
	// for (const key in data.promptParam) {
	// 	text = text.replaceAll('{' + key + '}', '{{{' + data.promptParam[key] + '}}}');
	// }
	data.promptParam &&
		data.promptParam.forEach((item) => {
			text = text.replaceAll('{' + item.name + '}', '{{{' + item.value + '}}}');
		});
	// text = '请针对{8月24日日本排放核污染水}提供{5}条新闻报道选题\n要求：\n1、{选题新颖有创意，能吸引读者兴趣}\n2、{采访可操作性强}\n3、{内容发布在《都市晚报》上}';
	var inputText = text;
	if (!text) {
		return;
	}
	insertpromptText.value = '';
	isInsertPrompt.value = true;
	const inputValue = inputText.replace(/\{{{(.*?)\}}}/g, '$1');
	insertpromptText.value = inputValue;
	let insertPromptLight = [];

	nextTick(() => {
		const element = insertpromptbox.value;
		const regexPattern = /\{{{(.*?)\}}}/g;
		// 在字符串中查找匹配项并添加到数组中
		let matchResult;
		while ((matchResult = regexPattern.exec(inputText)) !== null) {
			const capturedGroup = matchResult[1]; // 获取第一个捕获组的内容
			insertPromptLight.push(capturedGroup);
		}
		inputText = inputText.replace(/\r\n/g, '<br/>');
		inputText = inputText.replace(/\n/g, '<br/>');
		inputText = inputText.replace(/\r/g, '<br/>');
		inputText = inputText.replace(
			/\{{{(.*?)\}}}/g,
			'<span class="span-highlight-input" style="margin: 0 0 0 4px; padding: 3px 3px; border-radius: 4px;"><highlight-input contenteditable="true" class="highlight-input" style="background: #EDF3FF; padding: 2px 8px 2px 4px; color: #4f5866; cursor: pointer;"> $1</highlight-input></span><span> </span>'
		);
		var paragraphs = inputText.replace(/(.+?)\n/g, '<p>$1</p>');
		element.innerHTML = paragraphs;
		insertpromptText.value = element.innerText;

		var highlightInput = document.querySelector('highlight-input');
		if (highlightInput) {
			highlightInput.contentEditable = true;
			highlightInput.parentElement.classList.add('hover-highlight-input');
			highlightInput.style.background = '#fff';
			highlightInput.style.cursor = 'text';
			highlightInput.style.color = '#1A2029';
			highlightInput.style.borderRadius = '4px';
			// highlightInput.parentElement.style.border = 'rgb(36, 84, 255) solid 1px !important';
			highlightInput.style.setProperty('border', '1px solid rgb(36, 84, 255)', 'important');

			var range = document.createRange();
			range.selectNodeContents(highlightInput);
			range.setStartAfter(highlightInput);
			range.collapse(true);

			var selection = window.getSelection();
			selection.removeAllRanges();
			selection.addRange(range);
			highlightInput.focus();
		}
		setTimeout(function () {
			var highlightInput = document.querySelector('highlight-input');
			if (highlightInput) {
				highlightInput.parentElement.classList.add('hover-highlight-input');
				highlightInput.style.background = '#fff';
				highlightInput.style.cursor = 'text';
				highlightInput.style.color = '#1A2029';
				highlightInput.style.borderRadius = '4px';
				// highlightInput.parentElement.style.border = '1px solid rgb(36, 84, 255)';
				highlightInput.style.setProperty('border', '1px solid rgb(36, 84, 255)', 'important');
			}
		});
	});
};

const InsertHtml = (data: any) => {
	function elFoucs() {
		setTimeout(() => {
			const el = insertpromptbox.value;
			el.focus(); //解决ff不获取焦点无法定位问题
			if (window.getSelection) {
				//ie11 10 9 ff safari
				var range = window.getSelection(); //创建range
				range.selectAllChildren(el); //range 选择obj下所有子内容
				range.collapseToEnd(); //光标移至最后
			} else if (document.selection) {
				//ie10 9 8 7 6 5
				var range = document.selection.createRange(); //创建选择对象
				range.collapse(false); //光标移至最后
				range.select();
			}
			setInput();
		});
	}
	let isdblclick = data?.dblclick ? data.dblclick : false;
	let val = data?.value ? data.value : data;
	if (!data) {
		return;
	}
	isInsertPrompt.value = true;
	nextTick(async () => {
		if (text.value) {
			insertpromptbox.value.innerText = text.value;
			text.value = '';
		}
		let reg = /@([^@\s]*)$/;
		if (reg.test(insertpromptbox.value.innerText) && !isdblclick) {
			let spans = insertpromptbox.value.querySelectorAll('span');
			if (spans.length) {
				if (spans[spans.length - 1].innerText.indexOf('@') != -1) {
					spans[spans.length - 1].remove();
				}
			} else {
				insertpromptbox.value.innerText = insertpromptbox.value.innerText.replace(/@([^@\s]*)$/, '');
			}
			// insertpromptbox.value.innerText = insertpromptbox.value.innerText.replace(/@([^@\s]*)$/, '');
		} else if (insertpromptbox.value.innerText.endsWith('/')) {
			// let spans = insertpromptbox.value.querySelectorAll('span');
			// if (spans.length) {
			// 	if (spans[spans.length - 1].innerText.indexOf('/') != -1) {
			// 		spans[spans.length - 1].remove();
			// 	}
			// } else {
			// insertpromptbox.value.innerText = insertpromptbox.value.innerText.replace(/\/[^/]*$/, '');
			// }
			insertpromptbox.value.innerText = '';
			insertpromptText.value = '';
			// insertpromptbox.value.innerText = insertpromptbox.value.innerText.replace(/\/[^/]*$/, val+'@' )
			// insertpromptText.value = insertpromptbox.value.innerText;
			// elFoucs()
			// return
		}
		if (isdblclick) {
			let ids = await getInsertHtmlId();
			let index = ids.fileIds.findIndex((i) => {
				return i == data.id;
			});
			if (index != -1) {
				return;
			}
		}
		// insertpromptText.value = insertpromptbox.value.innerText;
		// text = '请针对{8月24日日本排放核污染水}提供{5}条新闻报道选题\n要求：\n1、{选题新颖有创意，能吸引读者兴趣}\n2、{采访可操作性强}\n3、{内容发布在《都市晚报》上}';

		// if(insertpromptText.value.endsWith('@')){
		// 	insertpromptText.value.replace(/@([^@\s]*)$/, text);
		// }else if(insertpromptText.value.endsWith('/')){
		// 	insertpromptText.value.replace(/\/[^/]*$/, text);
		// }else{

		// insertpromptText.value += val;

		// }
		const element = insertpromptbox.value;
		let spanHtml = `<span class="span-highlight-input ${
			data?.id ? 'dataId' : ''
		}" style="margin: 0 0 0 4px; padding: 3px 3px; border-radius: 4px;" data-id="${data?.id ? data.id : ''}" data-type="${
			data?.type ? data.type : ''
		}" data-prompt="${data?.prompt ? data.prompt : ''}">${val}</span><span>${data?.prompt ? ' @' : ' '}</span>`;
		// if(!data?.value){
		// 	spanHtml = `<span class="span-highlight-input" style="margin: 0 0 0 4px; padding: 3px 3px; border-radius: 4px;">${text}</span><span> </span>`
		// }
		console.log(spanHtml);
		element.innerHTML += spanHtml;
		datapanelParas.value.show = 0;
		insertpromptText.value = insertpromptbox.value.innerText;
		elFoucs();
	});
};

const handleClick = (event) => {
	const highlightInputs = document.querySelectorAll('highlight-input');

	if (event.target.closest('highlight-input')) {
		highlightInputs.forEach((input) => {
			if (input.contains(event.target)) {
				applyHighlightStyles(input);
			} else {
				resetHighlightStyles(input);
			}
		});
	} else if (!event.target.classList.contains('insert-prompt')) {
		let ancestor = event.target.parentElement;

		while (ancestor) {
			if (ancestor.classList.contains('insert-prompt')) {
				break;
			}

			if (ancestor === document.body) {
				highlightInputs.forEach((input) => {
					resetHighlightStyles(input);
				});
				break;
			}

			ancestor = ancestor.parentElement;
		}
	}
};
const applyHighlightStyles = (input) => {
	// input.parentElement.style.outline = 'rgb(36, 84, 255) !important';
	input.style.setProperty('border', '1px solid rgb(36, 84, 255)', 'important');
	input.parentElement.classList.add('hover-highlight-input');
	input.style.background = '#fff';
	input.style.borderRadius = '4px';
	input.style.color = '#1A2029';
	input.style.cursor = 'text';
};

const resetHighlightStyles = (input) => {
	// input.parentElement.style.outline = '1px solid transparent';
	input.style.setProperty('border', '1px solid transparent', 'important');
	input.parentElement.classList.remove('hover-highlight-input');
	input.parentElement.style.boxShadow = 'none';
	input.style.background = 'rgba(36, 84, 255, 0.1)';
	input.style.color = '#4f5866';
	input.style.borderRadius = '4px';
	input.style.cursor = 'pointer';
};

const handleKeydown = (t) => {
	if (datapanelParas.value.show) {
		if (t.key == 'ArrowUp') {
			t.stopPropagation();
			t.preventDefault();
			DatapanelRef.value.penelItemMove('up');
		}

		if (t.key == 'ArrowDown') {
			t.stopPropagation();
			t.preventDefault();
			DatapanelRef.value.penelItemMove('down');
		}
		if (!t.shiftKey && t.keyCode == 13) {
			t.cancelBubble = true;
			t.stopPropagation();
			t.preventDefault();
			if (t.keyCode == 13) {
				DatapanelRef.value.penelItemConfirm();
				return;
			}
		}
	}
	if (t.shiftKey && 13 === t.keyCode) {
	} else if ('Tab' === t.key || 9 === t.keyCode) {
		t.preventDefault();
		var i = document.querySelectorAll('highlight-input'),
			a = window.getSelection().getRangeAt(0),
			e = -1;
		if (
			(i.forEach(function (t, i) {
				t.style.setProperty('border', '1px solid transparent', 'important'),
					t.parentElement.classList.remove('hover-highlight-input'),
					(t.parentElement.style.boxShadow = 'none'),
					(t.style.background = 'rgba(36, 84, 255, 0.1)'),
					(t.style.color = '#4f5866'),
					(t.style.borderRadius = '4px'),
					(t.style.cursor = 'pointer'),
					a.intersectsNode(t.parentElement) && (e = i);
			}),
			-1 !== e)
		) {
			var s = (e + 1) % i.length,
				n = i[s];
			n.style.setProperty('border', '1px solid rgb(36, 84, 255)', 'important'),
				n.parentElement.classList.add('hover-highlight-input'),
				(n.style.background = '#fff'),
				(n.style.color = '#1A2029'),
				(n.style.borderRadius = '4px'),
				(n.style.cursor = 'text');
			var o = document.createRange();
			o.selectNodeContents(n), o.setStartAfter(n), o.collapse(!0);
			var l = window.getSelection();
			l.removeAllRanges(), l.addRange(o);
		} else {
			var f = document.querySelector('highlight-input');
			if (f) {
				var r = document.createRange();
				r.selectNodeContents(f),
					(f.parentElement.style.border = '1px solid rgb(36, 84, 255)'),
					f.parentElement.classList.add('hover-highlight-input'),
					(f.style.background = '#fff'),
					(f.style.color = '#1A2029'),
					(f.style.cursor = 'text'),
					(f.style.borderRadius = '4px');
				var m = window.getSelection();
				m.removeAllRanges(), m.addRange(r);
			}
		}
	} else if ((t.ctrlKey || t.metaKey || t.altKey) && 13 === t.keyCode) {
		var c = document.querySelector('.yayi-editor');
		if (c) {
			var p = window.getSelection(),
				y = p.getRangeAt(0),
				h = document.createElement('span');
			(h.textContent = '\n'), y.deleteContents(), y.insertNode(h), y.setStartAfter(h), y.collapse(!0), p.removeAllRanges(), p.addRange(y);
			insertpromptText.value = c.innerText;
		}
	} else if (13 === t.keyCode) {
		t.stopPropagation();
		t.preventDefault();
		var g = document.querySelector('.yayi-editor');
		var v = g.innerText;
		insertpromptText.value = v.replace(/^\s*[\r\n]/gm, '');
		setTimeout(function () {
			setInput();
			g.innerHTML = '';
			isInsertPrompt.value = false;
		}, 1);
	}
};

const handlePaste = (e) => {
	e.preventDefault();
	var text;
	var clp = (e.originalEvent || e).clipboardData;
	if (clp === undefined || clp === null) {
		text = window.clipboardData.getData('text') || '';
		if (text !== '') {
			if (window.getSelection) {
				var newNode = document.createElement('span');
				newNode.innerHTML = text;
				window.getSelection().getRangeAt(0).insertNode(newNode);
			} else {
				document.selection.createRange().pasteHTML(text);
			}
		}
	} else {
		text = clp.getData('text/plain') || '';
		if (text !== '') {
			document.execCommand('insertText', false, text);
			// document.execCommand('insertText', false, 'ab1');
			// // document.getElementsByClassName('hover-highlight-input')[0].getElementsByTagName('highlight-input')[0].innerHTML = '123'
			// setTimeout(() => {
			// 	if (window.getSelection) {
			// 	var sel = window.getSelection();
			// 	// var textNode = insertpromptbox.value;
			// 		var textNode = document.getElementsByClassName('hover-highlight-input')[0].getElementsByTagName('highlight-input')[0]
			// 		var range = document.createRange();
			// 		range.setStart(textNode, 1);
			// 		range.setStartAfter(lastNode);
			// 	range.collapse(false);
			// 	sel.removeAllRanges();
			// 	sel.addRange(range);
			// }
			// }, 100);
			// setTimeout(() => {
			// 	document.execCommand('insertText', false, text);
			// }, 300);
		}
	}
};

// 指令输入框代码结束****************************

let ttsConfig = ref();
const getTtsConfigListFun = async () => {
	let appInfo = localStorage.getItem(`${route.params.appId}`) ? JSON.parse(localStorage.getItem(`${route.params.appId}`)) : '';
	if (appInfo?.voiceDialogueFlag == '是') {
		let res = await getTtsConfigList(appInfo?.sttId);
		ttsConfig.value = res.data;
	}
};

// 语言合成代码开始*
const btnControl = ref('');
const changeBtnStatus_tts = (status, type = '') => {
	btnStatus_tts.value = status;
	if (status === 'UNDEFINED') {
		btnControl.value = '立即合成';
	} else if (status === 'CONNECTING') {
		btnControl.value = '正在合成';
	} else if (status === 'PLAY') {
		btnControl.value = '停止播放';
	} else if (status === 'STOP') {
		if (chatFlag.value && type != 'quit') {
			setTimeout(() => {
				btnControlFun();
			}, 100);
		}
		btnControl.value = '重新播放';
	}
};
audioPlayer.onPlay = () => {
	changeBtnStatus_tts('PLAY');
};
audioPlayer.onStop = (audioDatas) => {
	console.log(audioDatas);
	btnStatus_tts.value === 'PLAY' && changeBtnStatus_tts('STOP');
};
const getWebSocketUrl_tts = () => {
	var apiKey = API_KEY;
	var apiSecret = API_SECRET;
	var url = 'wss://tts-api.xfyun.cn/v2/tts';
	var host = location.host;
	var date = new Date().toGMTString();
	var algorithm = 'hmac-sha256';
	var headers = 'host date request-line';
	var signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v2/tts HTTP/1.1`;
	var signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret);
	var signature = CryptoJS.enc.Base64.stringify(signatureSha);
	var authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
	var authorization = btoa(authorizationOrigin);
	url = `${url}?authorization=${authorization}&date=${date}&host=${host}`;
	return url;
};
const encodeText = (text, type) => {
	if (type === 'unicode') {
		let buf = new ArrayBuffer(text.length * 4);
		let bufView = new Uint16Array(buf);
		for (let i = 0, strlen = text.length; i < strlen; i++) {
			bufView[i] = text.charCodeAt(i);
		}
		let binary = '';
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
let sayStr = ref('');
const connectWebSocket_tts = (tt) => {
	const tts_config = getRandomConfig();
	APPID = tts_config.APPID;
	API_SECRET = tts_config.API_SECRET;
	API_KEY = tts_config.API_KEY;
	const url = getWebSocketUrl_tts();
	if ('WebSocket' in window) {
		ttsWS = new WebSocket(url);
	} else if ('MozWebSocket' in window) {
		ttsWS = new MozWebSocket(url);
	} else {
		alert('浏览器不支持WebSocket');
		return;
	}
	changeBtnStatus_tts('CONNECTING');

	ttsWS.onopen = (e) => {
		audioPlayer.start({
			autoPlay: true,
			sampleRate: 16000,
			resumePlayDuration: 1000,
		});
		changeBtnStatus_tts('PLAY');
		//   var text =
		//     document.getElementById("textarea").value.trim() ||
		//     "请输入您要合成的文本";
		let ttext = tt || sayStr.value || '抱歉，未正确识别到语音，请重新提问。';
		//  '找到了相关信息如下'
		if (ttext.lastIndexOf('找到了相关信息如下') > -1) {
			ttext = ttext.substring(0, ttext.lastIndexOf('找到了相关信息如下'));
		}
		if (ttext.lastIndexOf('我还在成长中') > -1 && ttext.lastIndexOf('抱歉，我还在成长中') == -1) {
			ttext = ttext.substring(0, ttext.lastIndexOf('我还在成长中'));
		}
		console.log('ttext', ttext);

		var tte = 'UTF8';
		var params = {
			common: {
				app_id: APPID,
			},
			business: {
				aue: 'raw',
				auf: 'audio/L16;rate=16000',
				vcn: 'xiaoyan', //'aisjiuxu' 'aisxping' 'aisjinger' 'aisbabyxu' 'vixq' 'xiaoyan'
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
			changeBtnStatus_tts('UNDEFINED');
			return;
		}
		audioPlayer.postMessage({
			type: 'base64',
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
	if (btnStatus_tts.value === 'UNDEFINED') {
		// 开始合成
		connectWebSocket_tts(tt);
	} else if (btnStatus_tts.value === 'CONNECTING') {
		// 停止合成
		changeBtnStatus_tts('UNDEFINED');
		ttsWS?.close();
		audioPlayer.reset();
		return;
	} else if (btnStatus_tts.value === 'PLAY') {
		audioPlayer.stop();
	} else if (btnStatus_tts.value === 'STOP') {
		audioPlayer.play();
	}
};

// 语言合成代码结束****************************

onMounted(() => {
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
  apiGetLlmPageList()
  text.value = sessionStorage.getItem('dazhouText');
  if(text.value){
    setInput();
  }
	getTtsConfigListFun();
	getPresetQuestionListFun();
	//text.value = chatStore.chatInputText;
	param.value = chatStore.dialogueParamsList;
	mittBus.on('chatOpen', () => {
		openYY('toChat');
	});
	mittBus.on('setsendMessage', (data) => {
		console.log(data);

		setDialogueData(data.textContent, data.params);
	});

	mittBus.on('promptInsert', (data) => {
		console.log(data, 'daata1111');

		if (!data?.id) {
			InsertHtml(data);
			return;
		}
		if (isChat.value) {
			promptId.value = data.id;
		}
		promptInsertHtml(data);
		// setDialogueData(data.textContent, data.params);
	});
	mittBus.on('InsertHtml', (data) => {
		InsertHtml(data);
		// setDialogueData(data.textContent, data.params);
	});
	mittBus.on('editQestionToInput', (data) => {
		cleatInput();
		setTimeout(() => {
			if (data.paragraph) {
				instructSet(data.paragraph);
			}
			text.value = data.question;
			chatStore.fileUpdateCV.url = data.imgUrl ?? '';
		});
	});
	mittBus.on('instructSet', (val: string) => {
		instructSet(val);
	});
	mittBus.on('uploadingStatus', (data) => {
		uploadingStatus.value = data;
	});
	nextTick(() => {
		document.addEventListener('keydown', (e) => {
			if (e.keyCode == 27) {
				recorder.stop();
			}
		});
		insertpromptbox.value.addEventListener('click', handleClick);
		document.addEventListener('click', handleClick);
		insertpromptbox.value.addEventListener('keydown', handleKeydown);
		insertpromptbox.value.addEventListener('paste', handlePaste);
	});
});
// 页面销毁时，关闭监听
onUnmounted(() => {
	mittBus.all.delete('setsendMessage', () => {});
	mittBus.all.delete('promptInsert', () => {});
	mittBus.all.delete('InsertHtml', () => {});
	mittBus.all.delete('editQestionToInput', () => {});
	mittBus.all.delete('instructSet', () => {});
	mittBus.all.delete('uploadingStatus', () => {});
	document.removeEventListener('keydown', () => {});
	insertpromptbox.value && insertpromptbox.value.removeEventListener('click', handleClick);
	document.removeEventListener('click', handleClick);
	insertpromptbox.value && insertpromptbox.value.removeEventListener('keydown', handleKeydown);
	insertpromptbox.value && insertpromptbox.value.removeEventListener('paste', handlePaste);
});
let status = 0;
watch(
	() => text.value,
	(val) => {
		if (text.value.endsWith('@') && !isChat.value) {
			status = 2;
		} else if (text.value.endsWith('/')) {
			status = 1;
		}
		if (status == 2 && text.value.indexOf('@') == -1) {
			status = 0;
		}
		if (status == 1 && text.value.indexOf('/') == -1) {
			status = 0;
		}
		datapanelParas.value.show = status;
		chatStore.chatInputTextValue = val;
	}
);
watch(
	() => insertpromptText.value,
	(val) => {
		if (insertpromptText.value.endsWith('@') && !isChat.value) {
			status = 2;
		} else if (insertpromptText.value.endsWith('/')) {
			status = 1;
		}
		if (status == 2 && insertpromptText.value.indexOf('@') == -1) {
			status = 0;
		}
		if (status == 1 && insertpromptText.value.indexOf('/') == -1) {
			status = 0;
		}
		datapanelParas.value.show = status;
		chatStore.chatInputTextValue = val;
	}
);
watch(
	() => route.params.appId,
	(val) => {
		maxLength.value = val == 18 ? 800 : 2000;
		if (val == 0) {
			chatStore.dialogueParams.textPlaceHolder = '有什么问题尽管问我';
		}
		text.value = '';
		instructSet('');
	}
);
watch(
	() => route.params.conversationId,
	() => {
		text.value = '';
	}
);
watch(
	() => chatStore.dialogueParamsList,
	() => {
		if (route.query.source && route.params.appId == 21) {
			//路由跳转传参过来后发起会话
			param.value.forEach((item) => {
				if (item.key === 'web_source_list') {
					item.defaultValue = route.query.source;
					setDialogueData(route.query.q);
				}
			});
		}
	}
);
watch(
	() => chatStore.dialogueLoading,
	(val) => {
		if (!val) {
			let text = chatStore.plainText || chatStore.answerStr;
			if (!text) return;
			text = text.replace(/:::r|:::|/g, '');
			// if (text === chatStore.errorText) {
			// 	return
			// }
			console.log('text', text);
			sayStr.value = text.trim();
			changeBtnStatus_tts('UNDEFINED');
			ttsWS?.close();
			audioPlayer.reset();
			console.log('这是chatFlag.value', chatFlag.value);
			if (chatFlag.value) {
				let trimText = text.trim();
				let newText = trimText.split('<p style="padding: 6px 0;border-top: 1px solid rgba(0, 0, 0, 0.12);height: 1px;margin-top:12px;"/>') || [];
				play_tts(newText ? newText[0] : '');
				// textToSpeechAndPlay(newText?newText[0]:'',15,1);
				// btnStatus_tts.value = 'PLAY'
			}
		}
	}
);
watch(
	() => insertpromptText.value,
	(val) => {
		if (val.trim() === '') {
			isInsertPrompt.value = false;
		}
	}
);
watch(
	() => btnStatus.value,
	(val) => {
		let flag = false;
		if (val === 'UNDEFINED' || val === 'CLOSED' || val === 'CLOSING') {
			flag = false;
		} else {
			flag = true;
		}
		console.log('btnStatus', val);
		mittBus.emit('isVedioIng', flag);
	}
);
watch(
	() => btnStatus_tts.value,
	(val) => {
		console.log('btnStatus_ttsssssssssssss', val);
	}
);
</script>

<style scoped lang="scss">
@import '/@/theme/mixins/index.scss';

.tj {
	padding: 0 10px 0 30px;
	height: 48px;
	margin-bottom: 6px;
	display: flex;
	align-items: center;

	.left-title {
		@include add-size($font-size-base14, $size);
		font-family: MicrosoftYaHei;
		color: #6097f5;
	}

	.center-box {
		width: calc(100% - 150px);
		display: flex;
		align-items: center;

		.center-item {
			padding: 10px 5px;
			cursor: pointer;
			margin-right: 10px;
			.box {
				@include add-size($font-size-base14, $size);
				font-family: MicrosoftYaHei;
				color: #010101;
				padding: 2px 10px;
				border-radius: 18px;
			}
		}
	}

	.right-back {
		width: 58px;
		height: 58px;
		display: flex;
		justify-content: center;
		align-items: center;
		cursor: pointer;

		.back {
			width: 36px;
			height: 38px;
			display: flex;
			justify-content: center;
			align-items: center;
			box-shadow: 0px 0px 10px 0px rgba(53, 125, 242, 0.2);
		}
	}
}

.chatInput {
	width: 100%;
	position: relative;
	padding: 10px;

	:deep(.w-textarea) {
		line-height: 1.5 !important;
		@include add-size($font-size-base16, $size);
		padding: 16px 70px 14px 16px;
	}
	.active {
		:deep(.w-textarea) {
			padding: 0 16px 14px 6px;
		}
	}

	.w-textarea-wrapper {
		border: 1px solid #4085f4;
		border-radius: 4px !important;
		width: 100%;
		border: none !important;
		color: #3f4247;
	}

	.sendBtn {
		// width: 44px;
		height: 44px;
		border-radius: 22px;
		display: flex;
		align-items: center;
		justify-content: center;
		position: absolute;
		background: none;
		right: 18px;
		top: 18px;
		&.yy {
			right: 60px;
		}
	}
	.sendBtnMobile {
		right: 12px;
		top: calc(50% - 0px);
	}

	.vedioBtn {
		position: absolute;
		right: 16px;
		background: #fff;
		bottom: calc(50% - 18px);
	}

	.loadingAnimate {
		position: absolute;
		right: 0px;
		top: 2px;
	}

	.sendVoiceBtn {
		width: 24px;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
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
		top: -4px;
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

.chatInput-pc {
	:deep(.w-textarea) {
		height: 56px !important;
		line-height: 48px !important;
	}

	// .lineHeight1 {
	// 	:deep(.w-textarea) {
	// 		line-height: 1.5 !important;
	// 	}
	// }
	// .lineHeight2 {
	// 	:deep(.w-textarea) {
	// 		line-height: 48px !important;
	// 	}
	// }
}

.chatInput-mobiles {
	position: fixed;
	left: 0;
	bottom: 0;
	width: 100%;
  padding: 12px;
    background: #FFFFFF;
    box-shadow: 0px 2px 8px 0px rgba(0, 0, 0, 0.06);
    border-radius: 12px;
    border: 1px solid #D7DAE0;
    height: 100px;
	.w-textarea-wrapper {
		max-height: 70px;
		min-height: 56px;
		border: 1px solid #d0d5dc;
		overflow-y: scroll;
		color: #3f4247;
		&::-webkit-scrollbar {
			display: none !important;
		}
	}
	:deep(.w-textarea) {
		max-height: 70px;
		min-height: 56px !important;
	}
}
</style>
<style lang="scss">
@import '/@/theme/mixins/index.scss';

highlight-input {
	caret-color: #2454ff;
}

.span-highlight-input {
	border: transparent solid 1px !important;
}

highlight-input::selection {
	color: #4f5866;
	background: none;
}

.yayi-editor-wrapper {
	border-radius: 16px;
	padding: 20px 0px 55px 0px;
	background: #fff;
	position: relative;

	.yayi-editor-limit {
		position: absolute;
		left: 34px;
		bottom: 12px;
	}
}

.yayi-editor {
	border-radius: 16px;
	max-height: 186px;
	min-height: 36px;
	padding-top: 0px;
	padding-right: 16px;
	padding-left: 16px;
	color: #1a2029;
	text-align: left;
	line-height: 27px;
	overflow-y: auto;
	background: #fff;
}

.yayi-editor,
.yayi-editor span {
	outline: none;
	@include add-size(15px, $size);
	word-break: break-word;
}

.yayi-editor span {
	white-space: pre-wrap;
	background: none !important;
}

.yayi-editor span:hover highlight-input {
	color: #1a2029;
	border-radius: 4px;
	background: #c8deff !important;
}

.yayi-editor .hover-highlight-input {
	background: transparent !important;
}

.yayi-editor .hover-highlight-input:hover highlight-input {
	color: #1a2029;
	border-radius: 4px;
	background: transparent !important;
}

.yayi-editor span::selection {
	color: #1a2029;
	background: #c8deff;
}

.yayi-editor p::selection {
	color: #1a2029;
	background: #c8deff;
}

.yayi-editor .highlight-input {
	border-radius: 4px;
}

.yayi-editor .highlight-input::selection {
	color: #1a2029;
	background-color: #c8deff !important;
}

.yayi-editor div::selection {
	color: #1a2029;
	background: #c8deff;
}

.yayi-editor .ProseMirror {
	display: block;
	width: 100%;
	height: 60px;
	outline: none;
}

.yayi-editor p {
	outline: none;
	padding-bottom: 4px;
}

.yayi-editor:only-child > p:first-child:last-child {
	padding-bottom: 0;
}

.yayi-editor::selection {
	color: #1a2029;
	background: #c8deff;
}
</style>
<style lang="scss">
.chat-dialog {
	margin: 0 !important;
	height: 100%;
	background: #fff;
	// margin-top: 73px;
	margin-top: 0;
	background: #fff;
	padding: 0;
	// background: radial-gradient( 70% 50% at 91% 76%, rgba(103,209,255,0.2) 0%, rgba(103,209,255,0) 100%), radial-gradient( 69% 49% at 97% 4%, rgba(162,119,255,0.1) 0%, rgba(162,119,255,0) 100%), rgba(255,255,255,0.8);
	// border-radius: 16px;
	// border: 1px solid #355EFF;
	.chatbg,
	.el-dialog__body,
	.el-dialog__header {
		background: radial-gradient(70% 50% at 91% 76%, rgba(103, 209, 255, 0.2) 0%, rgba(103, 209, 255, 0) 100%),
			radial-gradient(69% 49% at 97% 4%, rgba(162, 119, 255, 0.1) 0%, rgba(162, 119, 255, 0) 100%), rgba(255, 255, 255, 0.8);
		border-radius: 16px 16px 0 0;
	}
	.el-dialog__headerbtn {
		display: none;
	}
	.el-dialog__header {
		display: none;
	}
	.el-dialog__body {
		// height: 716px;
		height: 100%;
		border-radius: 0 0 16px 16px;
		.abortBtn {
			position: absolute;
			left: 0;
			bottom: 0;
			width: 144px;
			height: 48px;
			background: #cfddfe;
			border-radius: 0px 16px 0px 16px;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 14px;
			color: #355eff;
			line-height: 20px;
			text-align: left;
			font-style: normal;
			line-height: 48px;
			text-align: center;
			cursor: pointer;
		}
		.box {
			width: 100%;
			height: 100%;
			// padding:55px 0;
			margin: 0 auto;
			text-align: center;
			backdrop-filter: blur(10px);
			background-size: 100% 100%;
			position: relative;
			&::before {
				content: '';
				position: absolute;
				top: 0;
				left: 0;
				right: 0;
				bottom: 0;
				background: url('../../../../../assets/zhushou/yuyinbg.png') no-repeat center center;
				background-size: 100% 100%;
				// filter: blur(5px);
				z-index: -1;
			}
			img {
				margin: 0 auto;
			}
			.title {
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 22px;
				color: #355eff;
				line-height: 29px;
				text-align: left;
				font-style: normal;
				text-align: center;
				margin: 6px 0;
			}
			.tp {
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 15px;
				color: #7f84a4;
				line-height: 20px;
				text-align: left;
				font-style: normal;
				text-align: center;
				margin-bottom: 45px;
			}
			.chatType {
				margin-top: 12px;
				img {
					margin-bottom: 13px;
				}
				.chatTypeText {
					font-family: MiSans, MiSans;
					font-weight: 500;
					font-size: 22px;
					color: #3976f6;
					line-height: 29px;
					text-align: center;
					font-style: normal;
					margin-top: 24px;
				}
				.stopBtn {
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #355eff;
					line-height: 20px;
					text-align: center;
					font-style: normal;
					margin-top: 30px;
					cursor: pointer;
				}
			}
		}
	}
	&.mobile {
		width: 100%;
		height: 100%;
		.el-dialog__body {
			.box {
				.boxbg {
					width: 120px;
					position: absolute;
					top: 50%;
					left: 50%;
					transform: translate(-50%, -50%);
				}
				img {
					margin: 0 auto;
				}
				.tp {
					margin-bottom: 38px;
				}
				.stopBtn {
					margin-top: 17px;
				}
			}
			.abortBtn {
				position: absolute;
				left: 50%;
				transform: translateX(-50%);
				border-radius: 40px;
				// bottom: 84px;
				bottom: var(--i-bottom);
			}
		}
	}
}
.toChat {
	position: absolute;
	position: absolute;
	left: 10px;
	top: -29px;
	cursor: pointer;
	&.mobile {
		left: 0px;
		top: -39px;
	}
}
</style>
