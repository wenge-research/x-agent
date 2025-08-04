<template>
	<citationContent v-if="!isChat && citationText" @instructSend="instructSend" @instructClear="instructSet">
	</citationContent>

	<div style="position:relative;" class="apptemplate-chatInput"
		:class="{ 'chatInput-mobile': isMobile, 'chatInput-pc': !isMobile }">
		<div v-show="isInsertPrompt" class="yayi-editor-wrapper">
			<div ref="insertpromptbox" class="yayi-editor scroll-display-none" @input="setDivInput()"
				contenteditable="true"></div>
			<span v-if="insertpromptText.length > 2000" class="w-textarea-word-limit yayi-editor-limit">{{
				insertpromptText.length }}/{{ maxLength }}</span>
		</div>
		<!-- :class="text ? 'lineHeight1' : 'lineHeight2'" -->
		<!-- 关芯助理 -->
		<div v-if="isAssistantPc" class="select-template">
			<div class="select-template-hd">
				<img :src="computerIcon" alt="" class="tip-img" />
				<span class="tip-title">帮我写作</span>
				<w-button @click="toggleTemplateSelector" v-show="!showTemplateSelector" class="select-btn">
					{{ selectedTemplateName ? selectedTemplateName : '选择模板' }}
					<i @click.stop="removeTemplate" v-show="selectedTemplateName">
						<CoolCloseLineWe size="16" color="#fff" />
					</i>
				</w-button>
				<i @click="showTemplateSelector = false" v-show="showTemplateSelector">
					<CoolCloseLineWe size="22" color="#9a99aa" />
				</i>
			</div>
			<template-selector v-if="showTemplateSelector" :templates-list="templatesList"
				:selected-template-id="selectedTemplateId" @template-selected="onTemplateSelected"></template-selector>
		</div>
		<div class="textarea">
			<!-- 多参数动态表单 -->
			<div class="title-text">请告诉我您的需求</div>
			<div class="scroll-box">
				<el-form label-position="top" label-width="auto" :model="fromList" style="max-width: 815px">
					<el-form-item v-for="(item, index) in fromList" :key="index" :label="item.desc"
						label-position="top">
						<template #default>
							<div v-if="isFileUploadType(item.type)" style="width: 813px;">
								<el-upload ref="`upload_${index}`" action="#" :multiple="true" :limit="5"
									style="width: 813px;" :show-file-list="false"
									:http-request="createUploadHandler(item)"
									:before-upload="(file) => beforeUpload(file, index)" :on-remove="handleRemove(item)"
									:accept="acceptFile[item.valueType].type">
									<div class="upload-trigger">
										<div class="icon-box">
											<img src="/@/assets/img/updade_icon.png" alt="">
										</div>
										<!-- <div>点击上传文件，支持.doc、.docx等格式</div> -->
										<div>点击上传文件，支持{{ acceptFile[item.valueType].name }}格式</div>
									</div>
								</el-upload>
								<div class="fileList" v-if="item.files?.length > 0">
									<div class="file-detail" v-for="(elem, elemIndex) in item.files" :key="elem.uid">
										<div class="left">
											<div class="img" v-if="fileFormat.doc.includes(elem.fileType)">
												<iconpark-icon name="file-word-2-fill" size="28"
													color="#2862FF"></iconpark-icon>
											</div>
											<div class="img" v-else-if="fileFormat.image.includes(elem.fileType)">
												<iconpark-icon name="file-image" size="28"
													color="#2862FF"></iconpark-icon>
											</div>
											<div class="img" v-else-if="fileFormat.ppt.includes(elem.fileType)">
												<iconpark-icon name="file-ppt" size="28"
													color="#2862FF"></iconpark-icon>
											</div>
											<div class="img" v-else-if="fileFormat.excel.includes(elem.fileType)">
												<iconpark-icon name="file-excel" size="28"
													color="#2862FF"></iconpark-icon>
											</div>
											<div class="img" v-else-if="fileFormat.txt.includes(elem.fileType)">
												<iconpark-icon name="file-txt" size="28"
													color="#2862FF"></iconpark-icon>
											</div>
											<div class="img" v-else>
												<iconpark-icon name="file-default" size="28"
													color="#2862FF"></iconpark-icon>
											</div>
										</div>
										<div class="text">
											<div class="text-header">
												{{ elem.fileName }}
											</div>
											<div class="text-kb">
												{{ (elem.size / 1024).toFixed(2) }}KB
											</div>
										</div>
										<div class="delete" @click="deleteFile(item, elemIndex)">
											<iconpark-icon name="close-large-line" size="10"
												color="#fff"></iconpark-icon>
										</div>
									</div>
								</div>
							</div>
							<div v-else style="width: 813px;">
								<div style="display: flex;width: 813px;">


									<div v-if="item.desc == '部门名称' || item.desc.includes('季度信息')"
										style="height: 40px;width: 100%;">
										<el-select v-if="item.desc == '部门名称'" style="height: 40px;width: 100%;"
											v-model="item.value" placeholder="请选择" size="large">
											<el-option label="办公室(法制科)" value="办公室(法制科)" />
											<el-option label="规划与项目管理科" value="规划与项目管理科" />
											<el-option label="政务服务科" value="政务服务科" />
											<el-option label="数据要素科" value="数据要素科" />
											<el-option label="应用推进科(数字安全科)" value="应用推进科(数字安全科)" />
											<el-option label="大数据管理部" value="大数据管理部" />
											<el-option label="数字化平台部" value="数字化平台部" />
											<el-option label="应用系统管理部" value="应用系统管理部" />
											<el-option label="信息安全管理部" value="信息安全管理部" />
											<el-option label="政务大厅管理部" value="政务大厅管理部" />
											<el-option label="智慧政务推进部" value="智慧政务推进部" />
											<el-option label="政务信息管理部" value="政务信息管理部" />
										</el-select>
										<el-select v-else style="height: 40px;width: 100%;" v-model="item.value"
											placeholder="请选择" size="large">
											<el-option label="第一季度" value="第一季度" />
											<el-option label="第二季度" value="第二季度" />
											<el-option label="第三季度" value="第三季度" />
											<el-option label="第四季度" value="第四季度" />

										</el-select>
									</div>
									<div v-else style="width: 100%;">
										<div v-if="item.type == 'number'">
											<el-input v-if="item.inputType == 'textarea'" type="textarea"
												v-model="item.value" :placeholder="'请输入' + item.desc"
												@input="item.value = item.value.replace(/[^\d.]/g, '').replace(/(\..*)\./g, '$1')" />
											<el-select v-else-if="item.inputType == 'select'" v-model="item.value"
												:placeholder="'请选择' + item.desc" style="height: 40px;">
												<el-option v-for="ele in optionList(item.selectValues)" :key="ele.value"
													:label="ele.label" :value="ele.value">
												</el-option>
											</el-select>
											<el-input v-else type="number" v-model="item.value"
												:placeholder="'请输入' + item.desc"></el-input>
										</div>
										<div v-else>
											<el-input v-if="item.inputType == 'textarea'" :rows="6" type="textarea"
												v-model="item.value" :placeholder="'请输入' + item.desc" />
											<el-select v-else-if="item.inputType == 'select'" v-model="item.value"
												:placeholder="'请选择' + item.desc" style="height: 40px;">
												<el-option v-for="ele in optionList(item.selectValues)" :key="ele.value"
													:label="ele.label" :value="ele.value">
												</el-option>
											</el-select>
											<el-input v-else v-model="item.value" :placeholder="'请输入' + item.desc" />
										</div>
									</div>
									<!-- <el-input v-else style="height: 40px;" v-model="item.value" :placeholder="'请输入' + item.desc" /> -->
								</div>
							</div>

						</template>
					</el-form-item>
				</el-form>
			</div>
		</div>

		<!-- 按钮盒子 -->
		<div class="btn-box">
			<w-button type="primary" class="sendBtn" :class="{ sendBtnMobile: isMobile }" @click="setInput"
				:disabled="hasEmptyFormValues">
				立即生成
			</w-button>
			<w-button type="" class="cencelBtn" @click="cenelInput">
				重置
			</w-button>
		</div>

		<CvUpload v-if="route.name == 'chat'"></CvUpload>
		<div v-if="dialogueInputLoading && uploadImgStatusLoading" class="sendBtn"><w-spin /></div>
		<Vue3Lottie v-if="dialogueInputLoading && !uploadImgStatusLoading" class="loadingAnimate"
			:animationData="toJSON" :height="55" :width="55" :style="isMobile ? '' : 'top:5px;right:10px;'" />
	</div>

	<el-dialog title="" v-model="chatFlag" width="80%" :modal="false" :append-to-body="true" custom-class="chat-dialog"
		class="chat-dialog" :class="{ mobile: isMobile }" :close-on-click-modal="false" :before-close="handleClose">
		<div class="box">
			<div style="display: flex; justify-content: flex-start">
				<img src="/src/assets/chatImages/left.svg" style="width: 36px; height: 51px; margin: 0 0 0 16px"
					@click="quitYY" />
				<i class="el-icon-arrow-left"></i>
				<!-- <p style="width:calc(100% - 36px);color:#000;font-size:20px;margin:0;line-height:51px;text-align:center;">当前对话</p> -->
			</div>
			<div
				style="width: 364px; height: 364px; border-radius: 50%; background: rgba(255, 255, 255, 0.1); margin: 20px auto 0; position: relative">
				<div style="
						width: 264px;
						height: 264px;
						border-radius: 50%;
						background: rgba(255, 255, 255, 0.1);
						margin: auto;
						position: absolute;
						top: 50%;
						left: 50%;
						transform: translate(-50%, -50%);
					">
					<div style="
							width: 192px;
							height: 192px;
							border-radius: 50%;
							background: rgba(255, 255, 255, 0.1);
							margin: auto;
							position: absolute;
							top: 50%;
							left: 50%;
							transform: translate(-50%, -50%);
						">
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
						<img src="/src/assets/chatImages/guaduan.svg" style="width: 64px; height: 64px"
							@click="quitYY" />
					</div>
					<p style="font-size: 15px; color: #fff; text-align: center; margin-top: 24px">结束语音对话</p>
				</div>
			</div>
			<div class="chatType" v-if="btnStatus == 'CLOSED' && btnStatus_tts != 'PLAY' && chatSBFlag">
				<span style="font-size: 22px; color: #fff; margin-top: 24px">正在思考...</span>
				<img src="/src/assets/chatImages/guaduan.svg" style="width: 64px; height: 64px; margin: 90px auto 0"
					@click="quitYY" />
				<p style="font-size: 15px; color: #fff; text-align: center; margin-top: 12px">结束语音对话</p>
			</div>
			<div class="chatType" v-if="btnStatus == 'CLOSED' && btnStatus_tts != 'PLAY' && !chatSBFlag">
				<span style="font-size: 15px; color: #fff; text-align: center; margin-top: 24px">未识别到声音</span>
				<div class="stopBtn" @click="reStartBtnFun">重新开始</div>
			</div>
			<div class="chatType" v-if="btnStatus_tts == 'PLAY'">
				<div class="chatTypeText">正在回答...</div>
				<!-- <div class="stopBtn"
          style="font-family: MiSans, MiSans;font-weight: 400;font-size: 15px;color: #7F84A4;text-align: center;font-style: normal;">
          <i class="el-icon-video-pause"></i>双击屏幕，可以打断我说话
        </div> -->
				<img src="/src/assets/chatImages/guaduan.svg" style="width: 64px; height: 64px; margin: 60px auto 0"
					@click="quitYY" />
				<p style="font-size: 15px; color: #c6c6d2; text-align: center; margin-top: 12px">结束语音对话</p>
			</div>
		</div>
		<!-- <div class="abortBtn" @click="quitYY">退出沉浸式问答</div> -->
	</el-dialog>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, reactive, onMounted, computed, onUnmounted, watch, nextTick } from 'vue';
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
import { copyText, getcopyText } from '/@/utils/format';
import { getRandomConfig } from '/@/utils/ttsConfig';
import { ElMessage } from 'element-plus';
import chatBG from '/@/assets/img/chatBG.png';
import boxbg from '/@/assets/img/boxbg.png';
import zzlt from '/@/assets/img/zzlt.png';
import zzsk from '/@/assets/img/zzsk.png';
import zzsc from '/@/assets/img/zzsc.png';
import toChat from '/@/assets/img/toChat.png';
import { tr } from 'element-plus/es/locale';
import event from '/@/assets/chat/event.svg';
import axios from 'axios'
const getAudio = defineAsyncComponent(() => import('./getAudio.vue'));
// 写作
import { getPresetQuestionList, getTtsConfigList, getCategoryTemplates } from '/@/api/knowledge';
import computerIcon from '/@/assets/chatImages/computer.png';

const TemplateSelector = defineAsyncComponent(() => import('./TemplateSelector.vue'));
const speechAli = defineAsyncComponent(() => import('./speechAli.vue'));

interface Props {
	row?: number;
}
const promptId = ref('');
// const props = defineProps<Props>();
const maxLength = ref(4000);
// const maxRow = ref(props.row || 5);
const startT = ref(0);
const endT = ref(0);
const uploadingStatus = ref(false);
const fileListOrgin: any = ref([]);
const fileList: any = ref([])
const uploading = ref(false)
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
//人社局-语音转文字
const rsjTts = ref(false);
// 粤语
const isCantonese = ref(false);
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

// 写作
const showTemplateSelector = ref(false);
const selectedTemplateId = ref(null);
const selectedTemplateName = ref('');

const deletFileIndex = ref(null)

// 用于根据 type 返回组件类型的函数
const isFileUploadType = (type) => ['image', 'doc', "txt", "excel", "ppt", 'code', 'file', "array[file]"].includes(type);

//表单数组
const fromList = ref([]);

const hasEmptyFormValues = computed(() => {
	// 使用 every 检查是否所有项都为空
	return fromList.value.every(item => {
		if (isFileUploadType(item.type)) {
			// 文件上传类型检查文件列表是否为空
			return !item.files?.length;
		}
		// 其他类型检查值是否为空或仅含空白字符
		return !item.value?.trim();
	});

	//需填写所有的逻辑
	// return fromList.value.some(item => {
	//     if (isFileUploadType(item.type)) {
	//         return !item.files?.length;
	//     }
	//     return !item.value?.trim();
	// });
});
// 文件列表
const acceptFile = ref({
	doc: {
		name: ".doc,.docx",
		type: ".doc,.docx"
	},
	image: {
		name: ".jpg,.png,.svg等",
		type: "image/*"
	},
	txt: {
		name: ".txt",
		type: ".txt"
	},
	ppt: {
		name: ".ppt,.pdf",
		type: ".ppt,.pdf"
	},
	excel: {
		name: ".xls,.xlsx",
		type: ".xls,.xlsx"
	},
	default: {
		name: "任意",
		type: ""
	},
	"array[file]": {
		name: "任意",
		type: ""
	},
	file: {
		name: "任意",
		type: ""
	}
})
// 文件格式
const fileFormat = ref({
	doc: ['doc', 'docx'],
	ppt: ['ppt', 'pdf'],
	excel: ['xls', 'xlsx'],
	txt: ['txt'],
	image: ['png', 'jpg', 'jpeg', 'gif', 'bmp', 'svg']
})


// 删除方法定义
const deleteFile = (item, index) => {
	item.files.splice(index, 1);
	item.value = item.files.map(file => file.urlPath).join(', ');
	chatStore.setFileUploadList(fromList.value.flatMap(item => item.files));
};
const beforeUpload = (file, index) => {
	// console.log('file', file,index);
	const { name } = file;
	const arr = name.split(".")
	const fileType = arr[arr.length - 1]
	const type = fromList.value[index].valueType
	if (type == "image") {

		if (!file.type.includes("image")) {

			ElMessage({
				message: '文件格式错误',
				center: true,
			})
			return false;
		}
	}
	else if (type == "default" || type == "array[file]" || type == "file") {
		return true
	}
	else if (!acceptFile.value[type].type.includes(fileType)) {
		console.log(666);

		ElMessage({
			message: '文件格式错误',
			center: true,
		})
		return false;
	}
	// if (
	// 	file.type != 'application/msword' &&
	// 	file.type != 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' &&
	// 	file.type != 'application/pdf'
	// 	&& file.type != 'text/plain' && file.type != 'image/png'
	// ) {
	// 	ElMessage({
	// 		message: '文件格式错误',
	// 		center: true,
	// 	})
	// 	return false;
	// }
}
// 创建一个上传处理函数，该函数将 item 作为闭包的一部分
const createUploadHandler = (item) => (request) => uploadHandler(item, request);
//文件上传成功
const uploadHandler = async (item, params) => {
	const formData = new FormData();
	formData.append('file', params.file);
	formData.append('rename', true);
	formData.append('filePath', 'agent_source');
	try {
		uploading.value = true;
		const res = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/wos/file/upload`, formData, {
			headers: {
				'Content-Type': 'audio/wave;multipart/form-data',
			},
		});

		// 添加新文件到当前表单项的files数组
		const newFile = {
			...res.data.data[0],
			uid: Date.now() + Math.random().toString(36).substr(2), // 生成唯一ID
			size: params.file.size
		};
		item.files = item.files ? item.files : [];
		item.files.push(newFile);
		item.value = item.files.map(file => file.urlPath).join(', ');
		// console.log('fromList.value.flatMap(item => item.files)',fromList.value.flatMap(item => item.files))
		// chatStore.setFileUploadList(fileList.value);
		chatStore.setFileUploadList(fromList.value.flatMap(item => item.files));
	} catch (err) {
		uploading.value = false;
		console.error('Error:', err);
	} finally {
		uploading.value = false;
	}
};

//移除文件清空上传value
const handleRemove = (item) => (file) => {
	const index = item.files.findIndex(f => f.uid === file.uid);
	if (index > -1) {
		deleteFile(item, index);
	}
};
const toggleTemplateSelector = () => {
	showTemplateSelector.value = !showTemplateSelector.value;
};

const removeTemplate = () => {
	selectedTemplateId.value = null;
	selectedTemplateName.value = '';
};
const onTemplateSelected = (templateId: any, templateName: any) => {
	selectedTemplateId.value = templateId;
	selectedTemplateName.value = templateName;
	showTemplateSelector.value = false;
};

const templatesList = ref([]);
const getCategoryTemplatesFun = async () => {
	let res = await getCategoryTemplates();
	templatesList.value = res.data;
};

const isHaveTtsId = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo && appInfo.voiceDialogueFlag == '是' ? true : false;
};
const getSttId = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo.sttId ? appInfo.sttId : '';
};
const changeResultText = (data) => {
	text.value = data
};
const isContentLengthLimit = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo && appInfo.contentLengthLimit == '是' ? true : false;
};

const quitYY = () => {
	chatFlag.value = false;
	chatSBFlag.value = false;
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
const tjQuestListRecommend = ref([]);
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
				accent: isCantonese.value ? 'cn_cantonese' : 'mandarin', // cn_cantonese粤语 mandarin中文普通话
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
	// if (!route.params.conversationId || route.params.conversationId == '' || chatStore.isSensitive) {
	// 	chatStore.isSensitive = false;
	// 	await chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
	// }
	const data: any = {
		content: item,
		conversationId: route.params.conversationId,
		knowledgeBaseId: route.params.appId,
		templateId: selectedTemplateId.value,
		templateName: selectedTemplateName.value,
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

	// if (!route.params.conversationId || route.params.conversationId == '' || chatStore.isSensitive) {
	// 	chatStore.isSensitive = false;
	// 	await chatStore.addHistory({ appId: route.params.appId }, { name: text });
	// }
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
		templateId: selectedTemplateId.value,
		templateName: selectedTemplateName.value,
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
const textFn = async () => {
	console.log('jiekou', fromList.value)
	const data: any = {
		dataParms: fromList.value,
		conversationId: route.params.conversationId,
		knowledgeBaseId: route.params.appId,
		templateId: selectedTemplateId.value,
		templateName: selectedTemplateName.value,
		appId: route.params.appId,
	};
	await chatStore.setChatList(data);
}

//提交按钮
const setInput = async () => {
	mittBus.emit('instructSet', false);
	textFn();
};

//重置按钮
const cenelInput = () => {
	fileList.value.splice(deletFileIndex.value, 1)
	fromList.value.forEach(item => {
		item.value = '';
		if (item.files) {
			item.files = []
		}
	});
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
		let spanHtml = `<span class="span-highlight-input ${data?.id ? 'dataId' : ''
			}" style="margin: 0 0 0 4px; padding: 3px 3px; border-radius: 4px;" data-id="${data?.id ? data.id : ''}" data-type="${data?.type ? data.type : ''
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
const handleClose = () => {

}
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
		if (appInfo?.sttId === '9') {
			//人社局-语音转文字
			rsjTts.value = true;
		}
		if (appInfo?.sttId === '35') {
			//人社局-语音转文字
			isCantonese.value = true;
		}
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

// const refreshPage = () => {
// 	chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
// };
const sendAudio = (data) => {
	setDialogueData(data.transcription);
};

const isAssistantPc = computed(() => {
	return route?.path?.includes('assistantPc') ? true : false;
})

// 生成下拉框的数据
const optionList = computed(() => {
	return (data) => {
		if (!data) return []
		const arr = []
		data.split(",").map(item => {
			if (item) {
				arr.push({
					value: item,
					label: item
				})
			}
		})
		return arr
	}
})

const outputSettings = ref()

onMounted(() => {
	fromList.value = chatStore.getStartParams();
	outputSettings.value = chatStore.getStartSettings().output;
	if (!outputSettings.value) {
		outputSettings.value = chatStore.getStartParams()
	}
	console.log(fromList.value, outputSettings.value, 11111111111111111111111)
	fromList.value.forEach((item, index) => {
		if (item.type === 'file' || item.type === 'array[file]') {
			item.files = []; // 初始化为空数组，或从接口获取数据填充  
		}
		item.inputType = outputSettings.value[index].inputType ? outputSettings.value[index].inputType : null
		item.selectValues = outputSettings.value[index].selectValues ? outputSettings.value[index].selectValues : null
	});
	// outputSettings.value=chatStore.getStartSettings().output;
	console.log(fromList.value, 11111111111111111111111)
	console.log(outputSettings.value, 222222);

	console.log("route", route)
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
	getTtsConfigListFun();
	if (isAssistantPc.value) {
		getCategoryTemplatesFun();
	}
	getPresetQuestionListFun();
	text.value = chatStore.chatInputText;
	param.value = chatStore.dialogueParamsList;
	mittBus.on('chatOpen', () => {
		openYY('toChat');
	});
	mittBus.on('setsendMessage', (data) => {
		console.log(data);

		setDialogueData(data.textContent, data.params);
	});
	mittBus.on('setsendAudioMessage', (data) => {
		setDialogueData(data.transcription);
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
	mittBus.on('getPresetQuestionList', (tjArr) => {
		let arr = new Map();
		tjQuestListRecommend.value = tjArr.filter((a) => a.category && !arr.has(a.category) && arr.set(a.category, 1));
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
	mittBus.all.delete('setsendMessage', () => { });
	mittBus.all.delete('setsendAudioMessage', () => { });
	mittBus.all.delete('promptInsert', () => { });
	mittBus.all.delete('InsertHtml', () => { });
	mittBus.all.delete('editQestionToInput', () => { });
	mittBus.all.delete('instructSet', () => { });
	mittBus.all.delete('uploadingStatus', () => { });
	mittBus.all.delete('getPresetQuestionList', () => { });
	document.removeEventListener('keydown', () => { });
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
			if (!text) return
			text = text.replace(/:::r|:::|/g, '');
			if (text === chatStore.errorText) {
				return;
			}
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

		img {
			margin-top: 12px;
			width: 64px;
			height: 32px;
		}
	}

	.center-box {
		margin-left: 10px;
		width: calc(100% - 150px);
		display: flex;
		align-items: center;

		.center-item {
			padding: 10px 5px;
			cursor: pointer;
			background: #fff;
			border-radius: 2px 8px 8px 8px;
			margin-right: 10px;

			&:hover {
				color: #355eff;
			}

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
			background: #fff;
		}
	}
}

.apptemplate-chatInput {
	width: 100%;
	position: relative;
	// padding: 10px;

	:deep(.w-textarea) {
		line-height: 1.5 !important;
		@include add-size($font-size-base16, $size);
		padding: 4px 120px 4px 62px;
	}

	.w-textarea-wrapper {
		background: rgba(255, 255, 255, 0.9);
		box-shadow: 0px 4px 16px 0px rgba(0, 0, 0, 0.1);
		border-radius: 8px;
		border: 1px solid #e1e4eb;
		backdrop-filter: blur(2px);
		width: 100%;
	}

	.btn-box {
		display: flex;
		margin-top: 50px;
	}

	.sendBtn {
		margin-right: 20px;
		width: 176px;
		height: 40px;
		background: #1747E5;
		border-radius: 8px;
		display: flex;
		align-items: center;
		justify-content: center;
		// position: absolute;
		// right: 0px;
		// top: 0px;
		// background: none;
		border: none;
		font-weight: 500;
		font-size: 14px;
		color: #ffffff;
		line-height: 40px;
		text-align: center;

		&.yy {
			right: 60px;
		}
	}

	.cencelBtn {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 72px;
		height: 40px;
		border-radius: 8px;
		border: 1px solid #D5D8DE;
		font-size: 14px;
		color: #494E57;
	}

	.sendBtnMobile {
		bottom: 24px;
	}

	.vedioBtn {
		position: absolute;
		right: 100px;
		bottom: 16px;
	}

	.refresh {
		width: 40px;
		height: 40px;
		position: absolute;
		left: 10px;
		bottom: calc(50% - 18px);
		display: flex;
		align-items: center;
		cursor: pointer;
		justify-content: center;

		img {
			width: 20px;
			height: 20px;
		}
	}

	.loadingAnimate {
		position: absolute;
		right: 0px;
		bottom: 0px;
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

	.select-template {
		background: #f4f6f9;
		position: absolute;
		width: 100%;
		font-size: 16px;
		color: #383d47;
		font-family: PingFangSC-Medium;
		font-weight: 600;
		bottom: 64px;
		z-index: 100;

		.select-template-hd {
			height: 56px;
			padding: 0 0 0 20px;
			display: flex;
			align-items: center;

			span.tip-title {
				margin: 0 12px 0 6px;
			}
		}

		.tip-img {
			display: inline;
			width: 24px;
		}

		.select-btn {
			background-color: rgba(20, 119, 227, 0.1);
			color: #1477e3;
			width: 80px;
			height: 32px;
			border-radius: 8px;
			position: relative;
			border: 1px solid rgba(20, 119, 227, 0.1);

			&:hover {
				background-color: rgba(20, 119, 227, 0.1);
				color: #1477e3;
				border: 1px solid rgba(20, 119, 227, 0.1);
			}

			i {
				position: absolute;
				right: -8px;
				top: -6px;
				background: #355eff;
				width: 20px;
				height: 20px;
				text-align: center;
				border-radius: 100%;
			}
		}

		i {
			position: absolute;
			right: 10px;
			cursor: pointer;
		}
	}
}

.textarea {
	// background-color:#fff;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	border-radius: 12px;

	.scroll-box {
		max-height: 500px;
		width: 72vw;
		overflow-y: scroll;
	}

	.scroll-box::-webkit-scrollbar {
		width: 6px;
		/* 滚动条宽度 */
	}


	.scroll-box::-webkit-scrollbar-thumb {
		background: #eef0f4;
		border-radius: 4px;
	}

	.scroll-box::-webkit-scrollbar-thumb:hover {
		background: #c0c0c0;
		/* 鼠标悬停时稍深的灰色 */
	}

	// border: 1px solid #FFFFFF;
	// backdrop-filter: blur(4px);
	.fileList {
		width: 100%;
		height: 60px;
		display: flex;
		align-items: center;
		overflow: scroll;

		.file-detail {
			position: relative;
			border-radius: 8px;
			display: flex;
			height: 50px;
			background-color: #F2F3F5;
			margin-left: 9px;

			&:first-child {
				margin-left: 14px;
			}

			.left {
				width: 56px;
				height: 56px;
				display: flex;
				justify-content: center;
				align-items: center;

				.img {
					width: 40px;
					height: 40px;
					display: flex;
					justify-content: center;
					align-items: center;
				}
			}

			.text {
				flex: 1;

				.text-header {
					margin-top: 8px;
					width: 182px;
					height: 18px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #383D47;
					line-height: 18px;
					text-align: left;
					font-style: normal;
					width: 200px;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}

				.text-kb {
					margin-top: 4px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #B4BCCC;
					line-height: 18px;
					text-align: left;
					font-style: normal;
				}
			}

			.delete {
				position: absolute;
				top: 0;
				right: 0;
				width: 15px;
				height: 15px;
				border-radius: 50%;
				background: #3F4247;
				display: flex;
				justify-content: center;
				align-items: center;

				&:hover {
					cursor: pointer;
				}
			}
		}

	}

	// :deep(.w-textarea) {
	// 	line-height: 1.5 !important;
	// 	@include add-size($font-size-base16, $size);
	// 	padding: 16px 120px 4px 16px;
	// 	backdrop-filter: blur(4px);
	// 	background: rgba(255,255,255,0.8);
	// }

	// .w-textarea-wrapper {
	// 	// background: rgba(255, 255, 255, 0.9);
	// 	background-color: rgba(244, 246, 246, 1);
	// 	box-shadow: 0px 4px 16px 0px rgba(0, 0, 0, 0.1);
	// 	border-radius: 12px 12px 12px 12px;
	// 	border: 1px solid rgba(255,255,255,0.8);;
	// 	backdrop-filter: blur(2px);
	// 	width: 100%;
	// 	font-family: MiSans, MiSans;

	// }
}

.chatInput-pc {
	:deep(.w-textarea) {
		padding: 16px 120px 4px 24px;
		height: 56px !important;
	}

	.w-textarea-wrapper {
		height: 56px;
		// border: 1px solid #d0d5dc;
		overflow-y: scroll;

		&::-webkit-scrollbar {
			display: none !important;
		}
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
	bottom: 0;
	width: 100%;
	padding: 0;

	.w-textarea-wrapper {
		width: calc(100vw - 70px);
		height: 56px;
		// border: 1px solid #d0d5dc;
		overflow-y: scroll;
	}

	:deep(.w-textarea) {
		height: 56px !important;
		border: 0;
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

.yayi-editor:only-child>p:first-child:last-child {
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
	// background: url("/@/assets/img/chatBG.png");
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
			// padding: 55px 0;
			margin: 0 auto;
			text-align: center;
			backdrop-filter: blur(10px);
			background-size: 100% 100%;

			&::before {
				content: '';
				position: absolute;
				top: 0;
				left: 0;
				right: 0;
				bottom: 0;
				background: url('../../../../../assets/chatTheme/chatBgimage.png') no-repeat center center;
				background-size: 100% 100%;
				filter: blur(5px);
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
					color: #2065d6;
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

.title-text {
	font-size: 18px;
	color: #3F4247;
	font-weight: 600;
	margin-bottom: 20px;
}

.upload-trigger {
	box-sizing: border-box;
	width: 813px;
	padding: 12px;
	border: 1px solid #e5e7eb;
	background-color: #fff;
	border-radius: 8px;
	display: flex;
	align-items: center;
	font-weight: 400;
	font-size: 12px;
	color: #3F4247;

	.icon-box {
		width: 40px;
		height: 40px;
		border-radius: 8px;
		background-color: #F7F8FA;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 10px;
	}

	img {
		width: 18px;
		height: 18px;
	}
}
</style>
