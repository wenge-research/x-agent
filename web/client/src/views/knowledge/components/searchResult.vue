<template>
	<div class="result-main flex-c">
		<div class="mian-box">
			<div class="mian-content">
				<span class="title-text"></span>
				<div class="flex-s">
					<div class="span-btn">
						<span :class="{ active: showRresultType == 1 }" @click="handleRresultType(1)">总结</span>
						<span :class="{ active: showRresultType == 2 }" @click="handleRresultType(2)">文件</span>
					</div>
					<div class="flex-r-c" v-if="showRresultType == 1 && syList.length > 0">
						<img :src="linkM" style="width: 14px; height: 14px; margin-right: 5px" />
						<span class="link-icon">{{ syList.length }}</span>
					</div>
				</div>
				<div v-if="showRresultType == 1">
					<div class="flex-s answer-btn">
						<div class="flex-r-c">
							<img :src="answer" />
							<span class="answer-text">回答</span>
						</div>
					</div>
					<div class="answer-content">
						<p class="title">
							设计的基本要素通常包括以下几个方面：<br />
							&nbsp;&nbsp;&nbsp;&nbsp;线条（Line）：线条是设计中最基本的元素，可以用来定义形状、分割空间、引导视线等。<br />
							&nbsp;&nbsp;&nbsp;&nbsp;形状（Shape）：形状是由线条围成的封闭区域，可以是几何形状（如圆形、方形、三角形）或自由形状。<br />
							&nbsp;&nbsp;&nbsp;&nbsp;色彩（Color）：色彩在设计中起到传达情感、区分元素、吸引注意等作用。色彩的选择和搭配对设计的整体效果至关重要。<br />
							&nbsp;&nbsp;&nbsp;&nbsp;纹理（Texture）：纹理可以增加设计的触感和视觉深度，使设计更加丰富和真实。<br />
							&nbsp;&nbsp;&nbsp;&nbsp;空间（Space）：空间是指设计中的空白区域，它与实体元素同样重要，可以影响设计的平衡和流动感。<br />
							&nbsp;&nbsp;&nbsp;&nbsp;尺寸（Size）：尺寸指的是设计元素的大小，不同大小的元素可以产生对比效果，引导观众的注意力。<br />
							&nbsp;&nbsp;&nbsp;&nbsp;价值（Value）：价值是指颜色的明暗程度，它影响设计的层次感和立体感。<br />
							&nbsp;&nbsp;&nbsp;&nbsp;方向（Direction）：方向是指设计元素的排列和移动趋势，它可以影响观众的视线流动和心理感受。<br />
							这些基本要素在设计中相互作用，共同构成一个有意义和有吸引力的视觉作品。设计师需要根据具体的设计目标和受众需求，巧妙地运用这些要素来创造出有效的设计作品。
						</p>
						<div class="btns flex-s">
							<w-button type="outline" shape="round" @click="followUpSearch">追问</w-button>
							<div style="position: relative">
								<w-tooltip content="复制" placement="top" mini>
									<w-button type="text" shape="circle">
										<CoolFuzhi_9oe2ecmc size="14" />
									</w-button>
								</w-tooltip>
								<w-tooltip content="播放" placement="top" mini>
									<w-button type="text" shape="circle">
										<CoolVolumeUpLineWe size="14" />
									</w-button>
								</w-tooltip>
							</div>
						</div>
					</div>
					<div v-if="isFollwUp">
						<div class="answer-content" v-for="(item, index) of chatList" :key="index" ref="textRef">
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
												<w-button type="text" shape="circle" @click="handleSelect('down')">
													<CoolThumbDownLineWe v-if="!isdown" size="14" />
													<CoolThumbDownFillWe v-else size="14" />
												</w-button>
												<w-tooltip :content="!iscopy ? '复制' : '已复制'" placement="top" mini>
													<w-button type="text" shape="circle" @click="handleSelect('copyText', item.answer)">
														<CoolFuzhi_9oe2ecmc v-if="!iscopy" size="14" />
														<CoolCheckLineWe v-else size="14" />
													</w-button>
												</w-tooltip>
												<w-tooltip content="播放" placement="top" mini>
													<w-button type="text" shape="circle" v-if="!item.inversion" @click="handleBofang(item.answer)">
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
									<w-button class="sendBtn" @click="sendQuestion(continueSearchText)">发送</w-button>
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
					<div class="link-box" v-if="syList.length > 0">
						<div class="title-btn flex-r-c">
							<img :src="linkM" style="width: 14px; height: 14px; margin-right: 5px" />
							<span class="title">参考资料</span>
						</div>
						<ul class="link-list">
							<li v-for="(item, index) in syList" :key="index">
								<span>{{ index + 1 }}</span>
								<w-link :href="item.fileLink" target="_blank">{{ item.fileName != null ? item.fileName : item.fileLink }}</w-link>
							</li>
						</ul>
						<w-divider />
						<span class="tips">已经到底了~</span>
					</div>
				</div>
				<div v-if="showRresultType == 2">
					<div class="file-box">
						<w-space class="file-title">
							<img :src="answerFill" style="width: 20px; height: 20px; margin-right: 5px" />
							<span>
								搜索出 <i>{{ 5 }}</i>
								个相关资源
							</span>
							<img :src="filter" style="width: 14px; height: 14px; margin-left: 10px" />
						</w-space>
						<w-space class="file-title">
							<w-select :style="{ width: '152px' }" placeholder="相关度从高到低" :bordered="false">
								<w-option style="color: #828894">相关度从高到低</w-option>
								<w-option style="color: #828894">相关度从低到高</w-option>
							</w-select>
							<w-divider direction="vertical" />
							<w-select :style="{ width: '96px' }" placeholder="按评分" :bordered="false">
								<w-option style="color: #828894">按评分</w-option>
							</w-select>
						</w-space>
					</div>
					<div class="file-list">
						<ul>
							<li>
								<div class="tips-text">相关度最高</div>
								<div class="list-one">
									<div class="list-fileName">
										<w-space>
											<img :src="docx" class="icon1" />
											<span> 好设计是不过时的.docx </span>
											<img :src="arrowLeft" class="icon2" />
										</w-space>
										<w-space>
											<img :src="save" style="margin-right: 15px" class="icon3" />
											<img :src="downloadLine" class="icon3" />
										</w-space>
									</div>
									<div class="list-fileText">
										<p>
											<em>设计有哪些要素</em
											>:理解设计的基本原理是创建具有凝聚力和和谐视觉效果的第一步。当我们看一个设计作品时，我们的眼睛在看一个构图。通过仔细，周到地在页面上排列元素，您可以描绘的不仅仅是视觉效果。
										</p>
									</div>
								</div>
							</li>
							<li>
								<div class="list-fileName">
									<w-space>
										<img :src="txt" class="icon1" />
										<span> 好设计是不唐突的.txt </span>
										<img :src="arrowLeft" class="icon2" />
									</w-space>
									<w-space>
										<img :src="save" style="margin-right: 15px" class="icon3" />
										<img :src="downloadLine" class="icon3" />
									</w-space>
								</div>
								<div class="list-fileText">
									<p>
										设计是一门综合性的艺术，它涉及到多个领域和学科，包括视觉艺术、室内设计、平面设计等。<em>设计有哪些要素</em>被认为是构成设计的基础。形状是设计中最基本的元素之一，它是封闭线形成的边界。形状可以分为几何形状、有机形状和抽象形状。
									</p>
								</div>
							</li>
							<li>
								<div class="list-fileName">
									<w-space>
										<img :src="pdf" class="icon1" />
										<span> 好设计是有创意的.pdf </span>
										<img :src="arrowLeft" class="icon2" />
									</w-space>
									<w-space>
										<img :src="save" style="margin-right: 15px" class="icon3" />
										<img :src="downloadLine" class="icon3" />
									</w-space>
								</div>
								<div class="list-fileText">
									<p>
										一个好的方案<em>设计有哪些要素</em>？应该具备以下要素：1. 实用性：满足用户的需求，解决用户的问题，并提供实用的功能和服务。2. 美观性：具有美观的外观和良好的用户体验，以吸引用户的注意力并提高用户的满意度。
									</p>
								</div>
							</li>
							<li>
								<div class="list-fileName">
									<w-space>
										<img :src="PPT" class="icon1" />
										<span> 好设计关心环境因素.ppt </span>
										<img :src="arrowLeft" class="icon2" />
									</w-space>
									<w-space>
										<img :src="save" style="margin-right: 15px" class="icon3" />
										<img :src="downloadLine" class="icon3" />
									</w-space>
								</div>
								<div class="list-fileText">
									
									<p>产品设计三要素指的是造型、材料以及质感，产品设计的外观必须按照美学原则以及人们对美的需求进行具体设计，影响产品<em>设计有哪些要素</em>呢？，例如工艺、材料等，所以产品进行设计时，要考虑到造型、材料以及质感等方面的因素。</p>
								</div>
							</li>
							<li>
								<div class="list-fileName">
									<w-space>
										<img :src="docx" class="icon1" />
										<span> 好设计是不过时的.docx </span>
										<img :src="arrowLeft" class="icon2" />
									</w-space>
									<w-space>
										<img :src="save" style="margin-right: 15px" class="icon3" />
										<img :src="downloadLine" class="icon3" />
									</w-space>
								</div>
								<div class="list-fileText">
									<p>
										实际上，设计的过程是一个复杂的多步骤流程，涉及到创意思维、技术技能、美学理论等多个方面。设计师需要不断地学习和实践，才能在设计中灵活运用各种元素，创作出既美观又实用的设计作品。<em>设计有哪些要素</em>
									</p>
								</div>
							</li>
							<li class="flex-c">
								<span class="tips">已经到底了~</span>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>
  
<script lang="ts" setup>
import 'splitpanes/dist/splitpanes.css';
import { computed, defineAsyncComponent, onMounted, onUpdated, ref, watch, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import { copyText } from '/@/utils/format';
import { simpleTransferTcc } from '/@/utils/conv';
import answer from '/@/assets/ai/answer.svg';
import linkM from '/@/assets/ai/link-m.svg';
import ks from '/@/assets/ai/ks.svg';
import answerFill from '/@/assets/ai/answer-fill.svg';
import docx from '/@/assets/ai/docx.svg';
import save from '/@/assets/ai/save1.svg';
import downloadLine from '/@/assets/ai/download-line.svg';
import pdf from '/@/assets/ai/pdf.svg';
import PPT from '/@/assets/ai/PPT.svg';
import txt from '/@/assets/ai/txt.svg';
import filter from '/@/assets/ai/filter.svg';
import arrowLeft from '/@/assets/ai/arrow-left-s-line.svg';
import sjx from '/@/assets/ai/sjx.svg';

import { useChatStore } from '/@/stores/chat';
import { setScrollPosition } from '/@/utils/other';
import mittBus from '/@/utils/mitt';
import { getRandomConfig } from '/@/utils/ttsConfig';

import ChatTextComponent from '/src/components/yy-message-ai/ChatText.vue';
import { listDialogueQuestion, tjQuestion, sourceAnswer } from '/@/api/chat';
import corner from '/@/assets/ai/corner.svg';

import follw from '/@/assets/ai/follw.svg';
import fileWordFill from '/@/assets/ai/file-word-fill.png';
interface Emit {
	(ev: 'regenerate'): void;
	(ev: 'delete'): void;
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
}
const props = defineProps<Props>();

// 加载进度条
const progress = ref(10);
const interval = ref();
const progressBarWidth = computed(() => {
	return `${progress.value}%`;
});
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
const searchTitle = ref(props.searchText);
// 展示图标
const iscopy = ref(false);
const isdown = ref(false);
const isup = ref(false);

const curText = ref('');
const stopChatLoading = ref(false);
const textRef = ref<HTMLElement>();
// 继续追问
const continueSearchText = ref('');
//是否追问
const isFollwUp = ref(false);
// 音频按钮状态
const btnStatus = ref('UNDEFINED');

// 暂停录音
const recorderStop = () => {
	recorder.stop();
	changeBtnStatus('CLOSED');
};
// 收起 展开
const showEvent = ref(true);
const showOrg = ref(true);

// 文件瀑布流
const fileWaterfallFlowList = ref([]);
const fileWaterfallFlowLoading = ref(false);

// 显示瀑布流 or 文件列表
const isWaterfall = ref(false);

const stringSub = (text: string, length: any) => {
	if (text.length > length) {
		text = text.substring(0, length - 1) + '...';
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
	setScrollPosition('.center-side');
};
// 复制 点赞
const handleSelect = (key: string, data: string) => {
	if (key === 'up') {
		isup.value = true;
		isdown.value = false;
		return;
	}
	if (key === 'down') {
		isdown.value = true;
		isup.value = false;
		return;
	}
	if (key === 'event') {
		showEvent.value = !showEvent.value;
		return;
	}
	if (key === 'organization') {
		showOrg.value = !showOrg.value;
		return;
	}
	if (key === 'copyText') {
		clearTimeout(timer.value);
		iscopy.value = true;
		let text = data ?? '';
		text = text.replace(/:::r|:::|/g, '');
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
const emit = defineEmits<Emit>();
const handleBofang = (data: string) => {
	let text = data ?? '';
	text = text.replace(/:::r|:::|/g, '');
	let newText = text.split('<p style="padding: 6px 0;border-top: 1px solid rgba(0, 0, 0, 0.12);height: 1px;margin-top:12px;"/>') || [];
	play_tts(newText ? newText[0] : '');
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
const connectWebSocket_tts = (tt) => {
	const tts_config = getRandomConfig();
  APPID =  tts_config.APPID;
  API_SECRET =  tts_config.API_SECRET;
  API_KEY =  tts_config.API_KEY;
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
		let ttext = tt || sayStr.value || '抱歉，未正确识别到语音，请重新提问。';
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

// 语言合成代码开始*
const btnStatus_tts = ref('UNDEFINED');
const audioPlayer = new AudioPlayer('./tts');
let APPID =  '';
let API_SECRET =  '';
let API_KEY =  '';
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
		if (type != 'quit') {
			// setTimeout(() => {
			// 	btnControlFun()
			// }, 100);
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

// 切换展示列表格式
const handleRresultType = async (data: any) => {
	showRresultType.value = data;
};
const syList = ref([]);
const arrData = ref([]);
const syListAll = ref([]);
// 获取参考资料
const sourceAnswerList = async () => {
	let res = await sourceAnswer({
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
		dialogueId: sessionStorage.getItem('dialogueId'),
	});
	if (res?.code == '000000') {
		if (res?.data?.sourceAnswerResultList.length > 0) {
			if (res.data.sourceAnswerResultList[0].sourceType == 1) {
				arrData.value = res.data.sourceAnswerResultList.filter((obj, index, self) => obj.fileLink && index === self.findIndex((t) => t.fileName === obj.fileName));
			} else {
				arrData.value = res.data.sourceAnswerResultList.filter((obj, index, self) => obj.fileLink && index === self.findIndex((t) => t.fileLink === obj.fileLink));
			}
			if (!isFollwUp.value) {
				syList.value = arrData.value;
				syListAll.value.push(syList.value);
			} else {
				syListAll.value.push(arrData.value);
			}
			setScrollPosition('.center-side', 'auto');
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
	if (res?.code == '000000') {
		tjList.value = res.data || [];
		setScrollPosition('.center-side', 'auto');
	} else {
		tjList.value = [];
	}
	mittBus.emit('getPresetQuestionList', tjList.value);
};
const searchWay = ref('MIXED');
// 追问搜索问题
const sendQuestion = async (item: string) => {
	console.log('dialogueLoading', dialogueLoading, isSensitive);
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
		continueSearchText.value = '';
		return;
	}
	return;
};
const recorder = new RecorderManager('./iat');
// 语音
const openYY = (type: string = '') => {
	continueSearchText.value = '';
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
const chatFlag = ref(false);
const chatSBFlag = ref(false);
const resultVal = ref('');


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
		continueSearchText.value = '';
		resultText = '';
		resultTextTemp = '';
	} else if (status === 'OPEN') {
		countdown();
	} else if (status === 'CLOSING') {
		resultVal.value = '关闭连接中';
	} else if (status === 'CLOSED') {
		continueSearchText.value = '';
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
		continueSearchText.value = resultTextTemp || resultText || '';
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

watch(
	() => props.searchText,
	(newVal: any) => {
		console.log('==>>>>>>>>>>>watch', newVal);
		searchTitle.value = newVal;
		isFollwUp.value = false;
		continueSearchText.value = '';
		syList.value = [];
		tjList.value = [];
		iscopy.value = false;
		isdown.value = false;
		isup.value = false;
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
		console.log('==>>>>>>>>>>>watchsearchWay', newVal);
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

	mittBus.on('dialogueId', (appInfo) => {
		setTimeout(() => {
			appInfo.sourceShowFlag === '1' && sourceAnswerList();
        appInfo.recommendQuestionsShowFlag === '1' && tjQuestionList();
				mittBus.all.delete('dialogueId', () => {});
		}, 1000);
	});
});
onUnmounted(() => {
	mittBus.all.delete('dialogueId', () => {});
	if (interval.value) {
		clearInterval(interval.value);
	}
});
</script>
  
<style lang="scss" scoped>
.result-main {
	width: 100%;
	height: 880px;
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
.mian-box {
	width: 800px;
}
.mian-content {
	padding-right: 20px;

	.flex-s {
		display: flex;
		justify-content: space-between;
		align-items: center;

		.span-btn {
			font-size: 16px;
			color: #828894;

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
				background: #e3ecfd;
				color: #434649;
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
			padding: 10px;

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
		margin: 30px 0px 20px 0px;

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
				font-size: 14px;
				color: #434649;
				margin-right: 5px;
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
	margin-top: 32px;
	height: calc(100vh - 400px);
	overflow-y: auto;

	li {
		margin-bottom: 12px;

		&:hover {
			.tips-text {
				display: block;
			}

			.list-one {
				border: 1px solid #2c70f6;
				border-radius: 8px;
				position: relative;
			}
		}
	}
	.list-fileName {
		height: 56px;
		display: flex;
		justify-content: space-between;
		align-items: center;
		background: linear-gradient(90deg, #d4e8fe 0%, #ecf5ff 100%);
		border-radius: 8px 8px 0px 0px;
		border: 1px solid;
		border-image: linear-gradient(270deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0)) 1 1;
		padding: 0 12px;

		span {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 16px;
			color: #383d47;
			cursor: pointer;
		}
		.icon1 {
			width: 28px;
			height: 28px;
			margin-right: 5px;
		}
		.icon2 {
			width: 14px;
			height: 14px;
			margin: 2px 0px 0px 5px;
			transform: rotate(180deg);
		}
		.icon3 {
			width: 16px;
			height: 16px;
			cursor: pointer;
		}
	}

	.list-fileText {
		height: 75px;
		background: #ffffff;
		padding: 12px;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 14px;
		color: #828894;
		line-height: 24px;
		border-radius: 8px;

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
</style>