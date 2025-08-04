<template>
	<div class="chatInput">
		<div class="tip">enter发送，shift + enter换行</div>
		<w-textarea
			ref="textareaRef"
			@keydown="setInputEnter($event)"
			v-model="text"
			:placeholder="placeholder"
			:auto-size="{ minRows: 3, maxRows: 3 }"
			:max-length="maxLength"
			show-word-limit
		/>
		<i v-if="!dialogueInputLoading" class="sendBtn" @click="setInput"><CoolFasong_932fep2l size="24" color="#fff" /></i>
		<i v-if="!storesUserInfo.offline && !dialogueInputLoading && isHaveTtsId()" id="btn_control" @click="btnControlFun()">
			<CoolMicLineWe
				class="sendVoiceBtn"
				v-if="btnStatus === 'UNDEFINED' || btnStatus === 'CLOSED' || btnStatus === 'CLOSING'"
				size="24"
				color="var(--w-color-primary)"
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
				<CoolStopCircleLineWe size="28" color="var(--w-color-primary)" />
			</div>
		</i>
	</div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed, onUnmounted, watch, nextTick } from 'vue';
import { notice, getPersonagePrice } from '/@/api/personal';
import { useChatStore } from '/@/stores/chat';
import mittBus from '/@/utils/mitt';
import { getRandomConfig } from '/@/utils/ttsConfig';
import { useRoute, useRouter } from 'vue-router';
import { useUserInfo } from '/@/stores/userInfo';
const storesUserInfo = useUserInfo();
import CryptoJS from 'crypto-js';
interface Props {
	row?: number;
}
const props = defineProps<Props>();
const maxLength = ref(2000);
const maxRow = ref(props.row || 5);

// const recorder = new RecorderManager('./static');

const route = useRoute();
const router = useRouter();
const chatStore = useChatStore();
const text = ref('');
const btnStatus = ref('UNDEFINED');
const resultVal = ref('');

const textareaRef = ref(null);
const chatInputText = computed(() => {
	return chatStore.chatInputText;
});
const datapanelParas = computed(() => chatStore.datapanelParas);
const param = computed(() => {
	return chatStore.dialogueParamsList;
});
//录音相关开始
const btnControlFun = () => {
	if (btnStatus.value === 'UNDEFINED' || btnStatus.value === 'CLOSED') {
		connectWebSocket();
	} else {
		// 结束录音
		recorder.stop();
	}
};
let APPID =  '';
let API_SECRET =  '';
let API_KEY =  '';
let iatWS;
let resultText = '';
let resultTextTemp = '';
let countdownInterval;
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

const isHaveTtsId = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo&&appInfo.voiceDialogueFlag=='是' ? true : false;
};
const connectWebSocket = () => {
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
				vad_eos: 5000,
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
		renderResult(e.data);
	};
	iatWS.onerror = (e) => {
		console.error(e);
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
		text.value = '';
		resultText = '';
		resultTextTemp = '';
	} else if (status === 'OPEN') {
		countdown();
	} else if (status === 'CLOSING') {
		resultVal.value = '关闭连接中';
	} else if (status === 'CLOSED') {
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
	if (jsonData.code === 0 && jsonData.data.status === 2) {
		iatWS.close();
	}
	if (jsonData.code !== 0) {
		iatWS.close();
		console.error(jsonData);
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
//录音相关结束

const setDialogueData = async (text: string, params?: object) => {
	if (text == '') {
		clearQuery();
		return;
	}

	if (!route.params.conversationId || route.params.conversationId == '') {
		await chatStore.addHistory({ appId: route.params.appId }, { name: text });
	}
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
	const data = {
		appId: route.params.appId,
		content: text,
		conversationId: route.params.conversationId,
		param: JSON.stringify(arr),
	};

	await chatStore.setChatList(data);
	clearQuery();
};

const clearQuery = () => {
	if (route.query.q || route.query.q == '') {
		let url = window.location.href.split('?')[0];
		window.history.pushState({}, 0, url);
	}
};

const placeholder = computed(() => {
	return chatStore.dialogueParams?.textPlaceHolder ?? '有什么问题尽管问我';
});
const dialogueInputLoading = computed(() => {
	return chatStore.dialogueLoading;
});
const setInputEnter = async (e) => {
	if (!e.shiftKey && e.keyCode == 13) {
		e.cancelBubble = true;
		e.stopPropagation();
		e.preventDefault();

		if (e.keyCode == 13) {
			setInput();
		}
	}
};

const setInput = async (e) => {
	if (route.params.appId == 0) {
		const appId = 21;
		router.push({
			name: 'chat',
			params: { appId },
			query: {
				q: text.value,
				source: 'offline',
			},
		});
		return;
	}
	if (chatStore.dialogueLoading) {
		return;
	}

	setDialogueData(text.value.trim());
	text.value = '';
};
onMounted(() => {
	text.value = chatStore.chatInputText;
	param.value = chatStore.dialogueParamsList;
	mittBus.on('setsendMessage', (data) => {
		setDialogueData(data.textContent, data.params);
	});
	nextTick(() => {
		document.addEventListener('keydown', (e) => {
			if (e.keyCode == 27) {
				recorder.stop();
			}
		});
	});
});
// 页面销毁时，关闭监听
onUnmounted(() => {
	mittBus.all.delete('setsendMessage', () => {});
	document.removeEventListener('keydown', () => {});
});
let status = 0;
watch(
	() => text.value,
	(val) => {
		if (text.value.endsWith('@')) {
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
	() => datapanelParas.value.value,
	(val) => {
		if (status == 2) {
			text.value = text.value.replace(/@([^@\s]*)$/, '@' + val);
		} else if (status == 1) {
			text.value = text.value.replace(/\/[^/]*$/, val);
		}
		status = 0;
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
</script>

<style scoped lang="scss">
.chatInput {
	position: relative;
	border-top: 1px solid #dfe2eb;
	.tip {
		position: absolute;
		bottom: 12px;
		z-index: 1;
		left: 110px;
		color: #ccc;
		user-select: none;
		font-size: 12px;
	}
	:deep(.w-textarea) {
		padding-top: 20px;
		padding-right: 32px;
		padding-left: 32px;
		font-size: var(--font16);
	}
	.w-textarea-wrapper {
		border: none;
		border-radius: 0px 0px 16px 16px;
		padding: 0px 0px 40px 0px;
		:deep(.w-textarea-word-limit) {
			left: 34px;
			bottom: 12px;
			right: initial;
		}
	}
	.sendBtn {
		width: 40px;
		height: 40px;
		border-radius: 50%;
		background: var(--w-color-primary);
		display: flex;
		align-items: center;
		justify-content: center;
		position: absolute;
		right: 36px;
		bottom: 24px;
		cursor: pointer;
	}
	.sendVoiceBtn {
		width: 36px;
		height: 36px;
		border-radius: 50%;
		background: rgba(255, 255, 255, 0.9);
		display: flex;
		align-items: center;
		justify-content: center;
		position: absolute;
		right: 96px;
		bottom: 24px;
		cursor: pointer;
		box-shadow: 0px 6px 20px 0px rgba(30, 64, 175, 0.1);
	}
	.vedioLoaingBtn {
		position: absolute;
		right: 96px;
		bottom: 24px;
		height: 36px;
		// width: 100px;
		cursor: pointer;
		display: flex;
		padding: 0 6px 0 14px;
		justify-content: space-between;
		align-items: center;
		background: rgba(255, 255, 255, 0.9);
		box-shadow: 0px 6px 20px 0px rgba(30, 64, 175, 0.1);
		border-radius: 18px;
		.cool-icon {
		}
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
