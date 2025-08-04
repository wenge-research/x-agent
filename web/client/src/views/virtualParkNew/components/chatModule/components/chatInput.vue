<template>
	<citationContent v-if="!isChat && citationText" @instructSend="instructSend" @instructClear="instructSet"> </citationContent>
	<div class="tj" v-if="!isMobile" :style="{ 'justify-content': 'flex-end' }">
		<div @click="gotoTop" class="right-back">
			<div class="back">
				<img :src="backIcon" alt="" />
			</div>
		</div>
	</div>
	<div class="chatInput" :class="{ 'chatInput-mobile': isMobile, 'chatInput-pc': !isMobile }">
		<div v-show="isInsertPrompt" class="yayi-editor-wrapper">
			<div ref="insertpromptbox" class="yayi-editor scroll-display-none" @input="setDivInput()" contenteditable="true"></div>
			<span v-if="insertpromptText.length > 2000" class="w-textarea-word-limit yayi-editor-limit">{{ insertpromptText.length }}/{{ maxLength }}</span>
		</div>
		<w-textarea
			v-show="!isInsertPrompt"
			ref="textareaRef"
			@keydown="setInputEnter($event)"
			v-model="text"
			:placeholder="placeholder"
			:auto-size="{ minRows: 1, maxRows: 6 }"
			:max-length="80"
		/>
		<w-button
			type="primary"
			v-if="!dialogueInputLoading"
			:disabled="imgUrl == '' && insertpromptText == '' && text == ''"
			class="sendBtn"
			:class="{ sendBtnMobile: isMobile }"
			@click="setInput"
		>
			<template #icon>
				<img :src="sendIcon" alt="" />
			</template>
		</w-button>
		<CvUpload v-if="route.name == 'chat'"></CvUpload>
		<div v-if="dialogueInputLoading && uploadImgStatusLoading" class="sendBtn"><w-spin /></div>
		<Vue3Lottie v-if="dialogueInputLoading && !uploadImgStatusLoading" class="loadingAnimate" :animationData="toJSON" :height="55" :width="55" />
	</div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, onMounted, computed, onUnmounted, watch, nextTick } from 'vue';
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
import sendIcon from '/@/assets/zc/send.png';
import { getPresetQuestionList } from '/@/api/knowledge';
interface Props {
	row?: number;
}
const promptId = ref('');
const maxLength = ref(4000);
// const maxRow = ref(props.row || 5);
const startT = ref(0);
const endT = ref(0);
const uploadingStatus = ref(false);

// const recorder = new RecorderManager('./static');
// 移动端自适应相关
const { isMobile } = useBasicLayout();
const route = useRoute();
// const router = useRouter();
const chatStore = useChatStore();
const knowledgeState = useKnowledgeState();
// const dblclickName: any = computed(() => knowledgeState.dblclickName);
// const previewData: any = computed(() => knowledgeState.previewData);
const citationText = computed(() => knowledgeState.citationText);
const imgUrl: any = computed(() => chatStore.fileUpdateCV.url);

const isChat = computed(() => route.path.indexOf('chat') != -1);
const text = ref('');
const btnStatus = ref('UNDEFINED');
// const resultVal = ref('');

const textareaRef: any = ref(null);

const isInsertPrompt = ref(false);
const insertpromptbox = ref();
const insertpromptText = ref('');
const datapanelParas = computed(() => chatStore.datapanelParas);
const param = computed(() => {
	return chatStore.dialogueParamsList;
});
const policyUrl: any = ref(import.meta.env.VITE_POLICY_QA);
const judicialUrl: any = ref(import.meta.env.VITE_JUDICIAL_QA);
const curRouteId = ref('');
//录音相关开始
// const btnControlFun = () => {
// 	if (btnStatus.value === 'UNDEFINED' || btnStatus.value === 'CLOSED') {
// 		connectWebSocket();
// 	} else {
// 		// 结束录音
// 		recorder.stop();
// 	}
// };
// const APPID = 'xxxxxxx';
// const API_SECRET = 'xxxxxxx';
// const API_KEY = 'ddc4e7ce8e45298cd90d5a6a956f2dc8';
// let iatWS;
// let resultText = '';
// let resultTextTemp = '';
// let countdownInterval;
const textFileIds: any = ref([]);
const textFolderIds: any = ref([]);
const tjQuestList = ref([]);
const instructObj: any = ref({});
// const getWebSocketUrl = () => {
// 	// 请求地址根据语种不同变化
// 	var url = 'wss://iat-api.xfyun.cn/v2/iat';
// 	var host = 'iat-api.xfyun.cn';
// 	var apiKey = API_KEY;
// 	var apiSecret = API_SECRET;
// 	var date = new Date().toGMTString();
// 	var algorithm = 'hmac-sha256';
// 	var headers = 'host date request-line';
// 	var signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v2/iat HTTP/1.1`;
// 	var signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret);
// 	var signature = CryptoJS.enc.Base64.stringify(signatureSha);
// 	var authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
// 	var authorization = btoa(authorizationOrigin);
// 	url = `${url}?authorization=${authorization}&date=${date}&host=${host}`;
// 	return url;
// };
// const connectWebSocket = () => {
// 	const websocketUrl = getWebSocketUrl();
// 	if ('WebSocket' in window) {
// 		iatWS = new WebSocket(websocketUrl);
// 	} else if ('MozWebSocket' in window) {
// 		iatWS = new MozWebSocket(websocketUrl);
// 	} else {
// 		alert('浏览器不支持WebSocket');
// 		return;
// 	}
// 	changeBtnStatus('CONNECTING');
// 	iatWS.onopen = (e) => {
// 		// 开始录音
// 		recorder.start({
// 			sampleRate: 16000,
// 			frameSize: 1280,
// 		});
// 		var params = {
// 			common: {
// 				app_id: APPID,
// 			},
// 			business: {
// 				language: 'zh_cn',
// 				domain: 'iat',
// 				accent: 'mandarin',
// 				vad_eos: 5000,
// 				dwa: 'wpgs',
// 			},
// 			data: {
// 				status: 0,
// 				format: 'audio/L16;rate=16000',
// 				encoding: 'raw',
// 			},
// 		};
// 		iatWS.send(JSON.stringify(params));
// 		changeBtnStatus('OPEN');
// 	};
// 	iatWS.onmessage = (e) => {
// 		renderResult(e.data);
// 	};
// 	iatWS.onerror = (e) => {
// 		recorder.stop();
// 		changeBtnStatus('CLOSED');
// 	};
// 	iatWS.onclose = (e) => {
// 		recorder.stop();
// 		changeBtnStatus('CLOSED');
// 	};
// };

// const changeBtnStatus = (status) => {
// 	btnStatus.value = status;
// 	if (status === 'CONNECTING') {
// 		resultVal.value = '建立连接中';
// 		text.value = '';
// 		resultText = '';
// 		resultTextTemp = '';
// 	} else if (status === 'OPEN') {
// 		countdown();
// 	} else if (status === 'CLOSING') {
// 		resultVal.value = '关闭连接中';
// 	} else if (status === 'CLOSED') {
// 		resultVal.value = '开始录音';
// 	}
// };
// const countdown = () => {
// 	let seconds = 60;
// 	countdownInterval = setInterval(() => {
// 		seconds = seconds - 1;
// 		if (seconds <= 0) {
// 			clearInterval(countdownInterval);
// 			recorder.stop();
// 		} else {
// 			resultVal.value = `录音中（${seconds}s）`;
// 		}
// 	}, 1000);
// };
// const toBase64 = (buffer) => {
// 	var binary = '';
// 	var bytes = new Uint8Array(buffer);
// 	var len = bytes.byteLength;
// 	for (var i = 0; i < len; i++) {
// 		binary += String.fromCharCode(bytes[i]);
// 	}
// 	return window.btoa(binary);
// };
// const renderResult = (resultData) => {
// 	// 识别结束
// 	let jsonData = JSON.parse(resultData);
// 	if (jsonData.data && jsonData.data.result) {
// 		let data = jsonData.data.result;
// 		let str = '';
// 		let ws = data.ws;
// 		for (let i = 0; i < ws.length; i++) {
// 			str = str + ws[i].cw[0].w;
// 		}
// 		// 开启wpgs会有此字段(前提：在控制台开通动态修正功能)
// 		// 取值为 "apd"时表示该片结果是追加到前面的最终结果；取值为"rpl" 时表示替换前面的部分结果，替换范围为rg字段
// 		if (data.pgs) {
// 			if (data.pgs === 'apd') {
// 				// 将resultTextTemp同步给resultText
// 				resultText = resultTextTemp;
// 			}
// 			// 将结果存储在resultTextTemp中
// 			resultTextTemp = resultText + str;
// 		} else {
// 			resultText = resultText + str;
// 		}
// 		text.value = resultTextTemp || resultText || '';
// 	}
// 	if (jsonData.code === 0 && jsonData.data.status === 2) {
// 		iatWS.close();
// 	}
// 	if (jsonData.code !== 0) {
// 		iatWS.close();
// 	}
// };
// recorder.onFrameRecorded = ({ isLastFrame, frameBuffer }) => {
// 	if (iatWS.readyState === iatWS.OPEN) {
// 		iatWS.send(
// 			JSON.stringify({
// 				data: {
// 					status: isLastFrame ? 2 : 1,
// 					format: 'audio/L16;rate=16000',
// 					encoding: 'raw',
// 					audio: toBase64(frameBuffer),
// 				},
// 			})
// 		);
// 		if (isLastFrame) {
// 			changeBtnStatus('CLOSING');
// 		}
// 	}
// };
// recorder.onStop = () => {
// 	clearInterval(countdownInterval);
// };
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
};
//
const getPresetQuestionListFun = async () => {
	let res = await getPresetQuestionList({
		pageNo: 1,
		pageSize: 8,
		type: '主题',
		status: 1,
	});
	tjQuestList.value = res.data.list;
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

onMounted(() => {
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
	getPresetQuestionListFun();
	text.value = chatStore.chatInputText;
	param.value = chatStore.dialogueParamsList;
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
// watch(
// 	() => datapanelParas.value.value,
// 	(val) => {
// 		if (status == 2) {
// 			let value = '@'+val.split('=-+')[0]
// 			let id = val.split('=-+')[1]
// 			let params = {
// 				value,
// 				id
// 			}
// 			InsertHtml(params)
// 			// text.value = text.value.replace(/@([^@\s]*)$/, '@' + val);
// 		} else if (status == 1) {
// 			if(isInsertPrompt.value){
// 				InsertHtml(val)
// 			}else{
// 				text.value = text.value.replace(/\/[^/]*$/, val);
// 			}
// 		}
// 		status = 0;
// 	}
// );
// watch(
// 	() => dblclickName.value,
// 	(val) => {
// 		if(val && text.value.indexOf(val) == -1){
// 			// text.value += ' @'+dblclickName.value+' '
// 			let params = {
// 				value : ' @'+dblclickName.value,
// 				id: previewData.value.active
// 			}
// 			InsertHtml(params)
// 		}
// 	}
// );
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
// watch(
// 	() => chatStore.chatInputTextValue,
// 	(val) => {
// 		//cleatInput()
// 		text.value = val;
// 	}
// );
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
		mittBus.emit('isVedioIng', flag);
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

			.box {
				@include add-size($font-size-base14, $size);
				font-family: MicrosoftYaHei;
				color: #010101;
				padding: 2px 10px;
				background: #fff;
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
	}

	.w-textarea-wrapper {
		border: 1px solid #4085f4;
		border-radius: 12px;
		width: 100%;
	}

	.sendBtn {
		width: 44px;
		height: 44px;
		border-radius: 22px;
		display: flex;
		align-items: center;
		justify-content: center;
		position: absolute;
		right: 20px;
		bottom: 24px;
	}

	.sendBtnMobile {
		bottom: 24px;
	}

	.vedioBtn {
		position: absolute;
		right: 96px;
		bottom: 14px;
	}

	.loadingAnimate {
		position: absolute;
		right: 20px;
		bottom: 14px;
	}

	.sendVoiceBtn {
		width: 32px;
		height: 32px;
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

.chatInput-mobile {
	position: fixed;
	left: 0;
	bottom: 10px;
	width: 100%;
	padding: 0;

	.w-textarea-wrapper {
		padding: 10px 0px 10px 0px;
	}

	:deep(.w-textarea) {
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
