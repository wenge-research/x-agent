import { defineStore } from 'pinia';
import { router } from '/@/router';
import { h } from 'vue';
import { Session } from '/@/utils/storage';
import { getPersonagePrice } from '/@/api/personal';
import { apiCheckSensitiveWord } from '/@/api/chat';
import { textToSpeechAndPlay, splitTextIntoChunks } from '/@/utils/newVoiceFun';
import { getUrlParamValue } from '/@/utils/tool';
import { getVidioeData, getImgData } from '../api/chat';
import { debounce } from 'lodash-es';

import {
	listDialogues,
	dialogue,
	listFile,
	listConversations,
	updateConversation,
	deleteConversation,
	addConversation,
	closeDialogueConn,
	feedbackListLabel,
	updateNameConversation,
	listConversationsByPage,
	recordGetRecord,
} from '/@/api/chat';
import { knowledgeAt, listKnowledgeBasePrompt, listChatBasePrompt } from '/@/api/knowledge';
import { setScrollPosition, isInIframe } from '/@/utils/other';
import useEventSource from '/@/hooks/useEventSource';
import { useRobotStore } from '/@/stores/robot';
import { Message, Notification, Modal } from 'winbox-ui-next';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import mittBus from '/@/utils/mitt';

const robotStore = useRobotStore();
import removeMd from 'remove-markdown';
const { isMobile } = useBasicLayout();
const id = 1002;
let previousAnswer = '';
let previousChunks = [];

const setLocalState = (state: Chat.ChatState) => {
	Session.set('chatStorage', state);
};
let appCode = location.hash.split('/') ? location.hash.split('/')[2] : '';
let appInfo = JSON.parse(localStorage.getItem(`${appCode}`));
// 元枢模板
let summarizePending = false;
let summarizeDebounceTimer = null;
// 封装防抖函数
const emitSummarizeDebounced = () => {
	if (summarizePending) return; // 已有待处理的触发

	summarizePending = true;

	if (summarizeDebounceTimer) {
		clearTimeout(summarizeDebounceTimer);
	}

	summarizeDebounceTimer = setTimeout(() => {
		console.log('执行总结分析');
		mittBus.emit('ttAnalysisSummarize');
		summarizePending = false;
		summarizeDebounceTimer = null;
	}, 5000);
};
export const useChatStore = defineStore('chat-store', {
	state: (): Chat.ChatState => ({
		hidden: [],
		inShare: false,
		isShare: false,
		errorText: appInfo ? appInfo.failureTalk : '服务器开小差了，请稍后再试',
		streamVoiceFlag: false,
		active: id,
		isLightTheme: true,
		conversationsId: 'new',
		conversationsIdforUploadFlag: 'new',
		usingContext: true,
		dialogueParams: {},
		dialogueLoading: false,
		history: [],
		isNewWork: false,
		chat: [{ id, data: [] }],
		tempFileList: [],
		chatList: [
			// {
			// 	citations: [],
			// 	createTime: '',
			// 	answer: '您好，很高兴为您服务！请问有什么可以帮您的？',
			// 	param: '{"promptId":"1716763096571080706"}',
			// 	question: 'xxxxxxx区',
			// 	loading: false,
			// 	dialogueFileIds: [],
			// 	dialogueFolderIds: [],
			// },
		],
		answerFlag: true,
		progressList: [],
		interligentLeftList: [],
		fileAttachmentList: [],
		fileListTxt: '',
		fileList: [],
		refList: [],
		fileUploadList: [], //多模态传的文件
		appTreeList: [],
		paramsDrawerVisible: false,
		uploadDrawerVisible: false,
		chatLoading: false,
		paramsNum: 3,
		dialogueParamsList: [],
		dialogueParamsListOld: [],
		chunks: [],
		abortController: null,
		clientId: '',
		answerStr: '',
		answerNew: '',
		isOpen: true,
		rightFlag: 1,
		scrollbarBottom: true,
		timer: null,
		chatInputText: '', //指令传过来的文字
		chatInputTextValue: '', //input输入框中的文字
		webSourceList: [],
		isShowNoice: false,
		noticeData: null,
		currentBalance: 4,
		categoryId: '',
		isSensitive: false,
		isFirstConsume: false,
		collapsedMeun: false,
		uploadImgStatus: false,
		fileUpdateCV: {
			accept: '.png,.jpg,.jpeg',
			suffixArr: ['png', 'jpg', 'jpeg'],
			list: [],
			status: false,
			url: '',
		},
		caiForm: {
			popTitle: '您的反馈对我们意义重大，希望您填写您认为',
			placeholder: '您认为更理想的答案是什么？',
			options: [],
		},
		zanForm: {
			popTitle: '您的反馈对我们意义重大，希望您填写您认为',
			placeholder: '请输入',
			options: [],
		},
		datapanelParas: {
			show: 0,
			list: [],
			value: null,
			chat: [listChatBasePrompt],
			document: [listKnowledgeBasePrompt],
			config: {
				chat: [
					{
						icon: false,
						name: 'name',
						desc: 'prompt',
						category: 'categories',
						multiple: true,
					},
				],
				document: [
					{
						icon: false,
						name: 'name',
						desc: 'promptShow',
						multiple: false,
					},
					{
						icon: true,
						name: 'name',
						desc: '',
						multiple: true,
					},
				],
			},
		},
		policyList: {},
		threeList: [], // xxxxxxx政务问答、虚拟园区缓存近3条信息：问题，答案，相关推荐
		attachmentList: [], // 视频解析功能参数
		plainText: '',
		startPlaying: false, // 播放视频
		answerParams: '',
		completeSummary: true, // 是否完成总结
	}),
	getters: {
		getChatHistoryByCurrentActive(state: Chat.ChatState) {
			const index = state.history.findIndex((item) => item.id === state.active);
			if (index !== -1) return state.history[index];
			return null;
		},
		getChatByid(state: Chat.ChatState) {
			return (id?: number) => {
				if (id) return state.chat.find((item) => item.id === id)?.data ?? [];
				return state.chat.find((item) => item.id === state.active)?.data ?? [];
			};
		},
	},
	actions: {
		// 视频背景大屏模板-开始语音播报
		startPlayback() {
			this.startPlaying = true; // 更新状态，触发组件响应
		},
		// 视频背景大屏模板-停止语音播报
		stopPlayback() {
			this.startPlaying = false; // 更新状态，触发组件响应
			this.dialogueLoading = false;
		},
		// 缓存问答
		cacheChatList(list: any) {
			this.chatList = [...list];
		},
		// 缓存相关推荐
		cacheSyList(list: any, dialogueId: string) {
			this.chatList.forEach((item) => {
				if (item.dialogueId == dialogueId) {
					item.syList = [...list];
				}
			});
			this.threeList = this.chatList?.slice(-3);
			const route = router.currentRoute.value;
			localStorage.setItem(`${route.params.appId}TotalList`, JSON.stringify(this.threeList));
		},

		async setChatList(data: never) {
			// await this.checkSensitiveWord(content);
			const appData = JSON.parse(localStorage.getItem(`${data.appId}`));
			const workflowValue = ['multi_agent', 'workflow', 'dialogue', 4, 5, 7].includes(appData.type);
			previousAnswer = '';
			previousChunks = [];
			this.refList = [];
			this.paramsDrawerVisible = false;
			this.uploadDrawerVisible = false;
			this.dialogueLoading = true;
			this.completeSummary = false;
			//数据
			data.clientId = this.clientId = Math.round(Math.random() * 1000000000000000000).toString(36);
			data.policyId = data.content == this.policyList?.title ? this.policyList?.id : '';

			const route = router.currentRoute.value;
			let chatMode = route.path.indexOf('chat') != -1;
			let type = chatMode ? 'QA' : 'KNOWLEDGE_BASE';
			data.type = type;
			data.ipAddress = sessionStorage.getItem('ipAddress');
			if (!chatMode) {
				data.knowledgeBaseId = data['appId'];
			}
			//联网检索
			let networkFlag = appData.networkFlag;
			if (route.path.indexOf('appTemplate') != -1) {
				networkFlag = this.isNewWork ? '是' : '否';
			}
			const { content, param, paragraph, skillId } = JSON.parse(JSON.stringify(data));
			const tempData = JSON.parse(
				JSON.stringify({
					// answer: "近三个月俄罗斯与乌克兰的热点信息:\n:::r 1. 英国将向乌克兰提供巡航导弹俄罗斯称将予以回应,100\n:::r 2. 俄罗斯别尔哥罗德州在与乌克兰接壤地区建立七个国土防御营,89\n:::r 3. 一名乌克兰公民因间谍罪被俄罗斯判处16年有期徒刑,70\n:::r 4. 美国首次批准将被没收的俄罗斯资金用于重建乌克兰,70\n:::r 5. 俄罗斯国防部称摧毁乌克兰海军最后一艘作战舰艇,58\n:::r 6. 乌克兰宣布对俄罗斯258个实体及个人实施制裁,50\n:::r 7. 中钢网新闻中心俄罗斯外交部：西方是乌克兰军事规划的幕后推手,39\n:::r 8. 13日，俄罗斯国防部发言人科纳申科夫通报,39\n:::r 9. 俄乌和平谈判中包括乌克兰宣布中立的选项,39\n:::r 10. 俄罗斯国防部承认俄军在巴赫穆特北部撤退,39",
					citations: [],
					// answer:'此前中国的探空火箭已经成功将探测器送入国际空间站进行科学实验和研究工作，此次计划的实施将\n```js 123123 ```\n为未来开展深层次的科学研究提供更加广阔的空间和资源平台。\n\n    目前该项目正在积极筹备当中，预计将于2021年发射升空的载人飞船也将搭载着宇航员前往火星和其他星球上执行各种重要的科研考察活动。同时还将建立一系列的基础设施和服务体系来支持未来的发展需求。',
					createTime: '',
					answer: '',
					param: param,
					question: content,
					loading: true,
					dialogueFileIds: JSON.parse(JSON.stringify(data)).dialogueFileIds,
					dialogueFolderIds: JSON.parse(JSON.stringify(data)).dialogueFolderIds,
					imgUrl: JSON.parse(JSON.stringify(data)).imgUrl,
					paragraph: paragraph,
					skillId: skillId,
				})
			);
			// 网信办敏感词过滤
			if (route?.path?.includes('/sz-cac/')) {
				const isResult = await this.checkSensitiveWord(data?.content?.trim());
				if (!isResult) {
					this.addHistory({ appId: route.params.appId }, { name: '新建会话' });
					return;
				}
			}
			this.chatList.push(tempData);
			this.scrollbarBottom = true;
			if (route.path.indexOf('videoLargeScreen') != -1) {
				setScrollPosition('.message-list');
			} else {
				setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
			}
			// return
			this.answerStr = '';
			let dialogueByStream: any = tempData;
			try {
				sessionStorage.setItem('outline', '11');
				this.setAiRobotChat('break', false, 'break');
				this.abortController = new AbortController();
				let originalAnswer = '';
				let url = data.templateId
					? import.meta.env.VITE_API_URL + import.meta.env.VITE_WRTTER_URL + '/wrt-template-entity/kefu-text-generate-stream'
					: workflowValue
					? import.meta.env.VITE_API_URL + import.meta.env.VITE_STREEM_PATH + import.meta.env.VITE_BASE_API_URL + '/workflow/dialogueRun'
					: import.meta.env.VITE_API_URL + import.meta.env.VITE_STREEM_PATH + import.meta.env.VITE_BASE_API_URL + '/dialogue/dialogueByStream';
				let writeParams = {
					clientId: data.clientId,
					templateId: data.templateId,
					templateItems: [
						{
							id: 'ad7523d8-d128-45c3-9478-820b49c51249',
							name: 'TITLE',
							nameCn: '标题',
							valueDefault: '标题',
							value: data.templateName,
							sort: 6,
							style: '',
							isChecked: true,
						},
						{
							id: 'b5d551d3-4022-4262-91e0-780d36bf8d08',
							name: 'SUMMARY',
							nameCn: '摘要',
							valueDefault: '摘要',
							value: data.content,
							sort: 8,
							style: '',
							isChecked: true,
						},
					],
				};
				let inputs = {};
				// 判断是否是 workflow 类型
				if (workflowValue && data.dataParms && Array.isArray(data.dataParms)) {
					data.dataParms.forEach((item) => {
						if (!('files' in item)) {
							inputs[item.label] = item.value;
						}
					});
				} else {
					// 其他情况
					inputs.question = data.content;
				}
				// 文档提问
				let fileVals = {};
				if (workflowValue && this.fileUploadList.length) {
					let nodeData = JSON.parse(sessionStorage.getItem(`nodeData`));
					let cells = nodeData.nodes;
					let startNode = cells.find((el: any) => el.nodeType === 1).output;
					this.fileUploadList = this.fileUploadList.filter((item) => item != null);
					let fileUploadList = this.fileUploadList.map((item) => {
						let extensionArr = item.fileName.split('.');
						return {
							type: 'default',
							transferMethod: 'remote_url',
							remoteUrl: item.urlPath,
							filename: item.fileName,
							extension: extensionArr[extensionArr.length - 1],
						};
					});
					startNode.forEach((item) => {
						if (item.type && ['default', 'image', 'doc', 'code', 'file', 'ppt', 'array[file]', 'txt', 'excel'].includes(item.type)) {
							if (item.type === 'array[file]' || fileUploadList.length > 1) {
								fileVals[item.label] = fileUploadList;
							} else {
								fileVals[item.label] = fileUploadList[0];
							}
						}
					});
				}
				// const workflowValue = appData.type === 'workflow' || appData.type === 'dialogue';
				let paramInputs = {
					...inputs,
					...fileVals,
				};
				let workflowParams;
				if (workflowValue) {
					workflowParams = {
						componentId: JSON.parse(sessionStorage.getItem(`nodeData`)).componentId,
						inputs: paramInputs,
						clientId: Math.floor(Math.random() * 10000000000),
						conversationId: data.conversationId,
					};
				}
				let params = workflowValue
					? workflowParams
					: data.templateId
					? writeParams
					: {
							applicationId: localStorage.getItem(`${route.params.appId}appId`),
							clientId: data.clientId,
							content: data.content,
							conversationId: data.conversationId,
							dialogueFileIds: data.dialogueFileIds,
							dialogueFolderIds: data.dialogueFolderIds,
							ipAddress: data.ipAddress,
							knowledgeBaseId: data.knowledgeBaseId,
							policyId: data.policyId,
							type: data.type,
							searchWay: data.searchWay,
							searchType: data.searchType,
							debuggerFlag: '否',
							networkFlag: this.isNewWork ? '是' : '否',
							attachmentList: JSON.parse(JSON.stringify(this.fileAttachmentList)),
					  };
				this.fileAttachmentList = [];
				// 元枢：视频解析内容
				if (data?.attachmentList && data?.attachmentList?.length) {
					params.attachmentList = data.attachmentList;
				}
				let headerParams = {};
				// 访问url带accountName参数 对话接口传入
				const accountName = sessionStorage.getItem('accountName');
				if (accountName) {
					headerParams['Account-name'] = accountName;
					headerParams['Application-id'] = localStorage.getItem(`${route.params.appId}appId`);
				}
				this.tempText = '';
				if (isInIframe() && !workflowValue && !isMobile.value) {
					params.debuggerFlag = '是';
					const question = params.content;
					const conversationId = params.conversationId;
					const data = { question: question, conversationId: conversationId };
					window.parent.postMessage(data, '*');
				}
				//xxxxxxx模板
				if (location?.hash?.includes('azhouTemplate')) {
					params.modelId = sessionStorage.getItem('dazhouModel');
				}
				// iframe嵌入的userName
				if (getUrlParamValue('accountName')) {
					params.userName = getUrlParamValue('accountName');
				}

				if (route.path.indexOf('aiVideoTemplate') !== -1) {
					//增加视频条件
					let videoParmas = {
						applicationId: localStorage.getItem(`${route.params.appId}appId`),
						content: data.content,
						resolution: data.resolution || '',
						saveFlag: '否',
						ratio: data.ratio || '',
						duration: data.duration,
					};
					getVidioeData(videoParmas)
						.then((response) => {
							try {
								const answer = response.data;
								const dialogueByStream = {
									answer: `<video controls width="100%"><source src="${answer}" type="video/mp4"></video>`,
									question: data.content,
									loading: false,
								};
								this.chatList[this.chatList.length - 1] = dialogueByStream;
								setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
								this.dialogueLoading = false;
							} catch (error) {
								this.chatList[this.chatList.length - 1] = {
									answer: this.errorText,
									question: data.content,
									loading: false,
								};
								this.dialogueLoading = false;
							}
						})
						.catch((error) => {
							this.chatList[this.chatList.length - 1] = {
								answer: this.errorText,
								question: data.content,
								loading: false,
							};
							this.dialogueLoading = false;
						});
				} else if (route.path.indexOf('aiImageTemplate') !== -1) {
					//增加图片条件
					let imgParmas = {
						applicationId: localStorage.getItem(`${route.params.appId}appId`),
						description: data.content,
						topic: data.content,
						// resolution: data.resolution || '',
						width: data.resolution.width,
						height: data.resolution.height,
						strategy: 'dblmageStrategy',
						saveFlag: '否',
						clientld: data.clientId,
					};
					// 新增 aiImageTemplate 处理逻辑
					getImgData(imgParmas)
						.then((response) => {
							try {
								const imgUrl = response.data; // 假设接口返回图片URL
								const dialogueByStream = {
									answer: `<img src="${imgUrl}" style="max-width:100%; border-radius:8px;" alt="生成的图片" onclick="window.imagePreviewer && window.imagePreviewer('${imgUrl}')">`,
									question: data.content,
									loading: false,
									renderType: 'image',
								};
								// 初始化全局预览方法
								if (!window.imagePreviewer) {
									window.imagePreviewer = (url) => {
										const modal = document.createElement('div');
										modal.style = `
        position: fixed; top: 0; left: 0; width: 100%; height: 100%; 
        background: rgba(0,0,0,0.8); display: flex; justify-content: center; 
        align-items: center; z-index: 9999; cursor: zoom-out;
      `;
										modal.innerHTML = `
        <img src="${url}" style="max-width: 90%; max-height: 90%; object-fit: contain;">
      `;
										modal.onclick = () => document.body.removeChild(modal);
										document.body.appendChild(modal);
									};
								}

								this.chatList[this.chatList.length - 1] = dialogueByStream;
								setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
								this.dialogueLoading = false;
							} catch (error) {
								this.chatList[this.chatList.length - 1] = {
									answer: this.errorText,
									question: data.content,
									loading: false,
								};
								this.dialogueLoading = false;
							}
						})
						.catch((error) => {
							this.chatList[this.chatList.length - 1] = {
								answer: this.errorText,
								question: data.content,
								loading: false,
							};
							this.dialogueLoading = false;
						});
				} else {
					useEventSource(
						url,
						params,
						this.abortController,
						(list) => {
							try {
								this.isFirstConsume = true;
								dialogueByStream = JSON.parse(list);
								// let obj = JSON.parse(list); //去掉data:前缀
								// if (route.path.indexOf('knowledgeDetails-searchNew-zh') != -1){
								//   if(JSON.parse(list).clientId==data.clientId){
								//     dialogueByStream = JSON.parse(list);
								//   }
								// }else{
								//   dialogueByStream = JSON.parse(list);
								// }
								if (dialogueByStream && !dialogueByStream.answer && dialogueByStream.output && dialogueByStream.output.text) {
									dialogueByStream.answer = dialogueByStream.output.text;
								} else {
									dialogueByStream.answer = dialogueByStream.answer;
								}
								dialogueByStream.question = data.content;
								if (dialogueByStream.nodeId) {
									if (dialogueByStream.status == 'WAITING_NEXT') {
										dialogueByStream.flag = true;
										const jsonObj = dialogueByStream.output; // 将字符串解析为一个对象
										if (typeof jsonObj === 'object' && jsonObj !== null) {
											const jsonFormatted = JSON.stringify(jsonObj, null, 2);
											dialogueByStream.output = '\n' + jsonFormatted + '\n';
										}
										this.interligentLeftList.push(dialogueByStream);
									}

									return;
								}
								if (data.templateId) {
									dialogueByStream = JSON.parse(list).data;
									this.tempText = this.tempText + dialogueByStream.text;
									dialogueByStream.answer = this.tempText;
									dialogueByStream.question = data.content;
								}
								this.answerFlag = dialogueByStream.answerFlag;
								sessionStorage.setItem('answerFlag', dialogueByStream.answerFlag);
								// mittBus.emit('dialogueId', dialogueByStream.dialogueId);
								sessionStorage.setItem('dialogueId', dialogueByStream.dialogueId);
								if (dialogueByStream.finishReason == 'ANSWER_COMPLETE') {
									// 元枢模板
									emitSummarizeDebounced();
									dialogueByStream.matterGuide = dialogueByStream.matterGuide
										? typeof dialogueByStream.matterGuide === 'string'
											? JSON.parse(dialogueByStream.matterGuide)
											: dialogueByStream.matterGuide
										: null;

									if (dialogueByStream.outline) {
										sessionStorage.setItem('outline', JSON.stringify(dialogueByStream.outline));
										mittBus.emit('outline', dialogueByStream.outline);
									}
									sessionStorage.setItem('matterGuide', JSON.stringify(dialogueByStream && dialogueByStream.matterGuide) || '[]');
									mittBus.emit('matterGuide', dialogueByStream && dialogueByStream.matterGuide);
									if (window.location.href.includes('/virtualTemplate/')) {
										this.threeList = this.chatList?.slice(-3);
										const route = router.currentRoute.value;
										localStorage.setItem(`${route.params.appId}TotalList`, JSON.stringify(this.threeList));
									}
								}
								this.isSensitive = dialogueByStream.sensitive;
								let index = this.history.findIndex((item) => item.id == data.conversationId);
								if (index !== -1) {
									this.history[index].isSensitive = dialogueByStream.sensitive;
								}
								if (dialogueByStream.isOverdue) {
									this.noticeData = {
										msg: '余额不足',
										id: -1,
									};
									this.isShowNoice = true;
								}
								if (Session.get('yayiFlag') === 'true') {
									const { answer = '' } = dialogueByStream;
									this.setAiRobotChat(answer.slice(originalAnswer.length, answer.length));
									originalAnswer = answer;
								}
								if (dialogueByStream.reasoningContent) {
									let reasoningContent = `<p style=" color: #8b8b8b;  ">${dialogueByStream.reasoningContent}</p>`;
									this.answerStr = reasoningContent + dialogueByStream.answer;
								} else {
									this.answerStr = dialogueByStream.answer;
								}
								const route = router.currentRoute.value;
								this.chunks = this.answerStr ? splitTextIntoChunks(this.answerStr, previousAnswer, previousChunks) : [];
								if (this.chunks.length > 0 && this.streamVoiceFlag && appInfo && appInfo.streamVoice === '是') {
									textToSpeechAndPlay(this.chunks, this.clientId);
								}
								// 市监局数字人流式播报
								if (this.chunks.length > 0 && appInfo && appInfo.virtualHumanFlag === '是') {
									mittBus.emit('textToSpeechAndPlay', { newChunks: this.chunks, clientId: this.clientId });
								}
								// 重庆（小冰）/标准应用模板数字人流式播报
								if (this.chunks.length > 0 && appInfo && appInfo.virtualHumanFlag === '是') {
									mittBus.emit('szrPlayStart', { chunksList: this.chunks, clientId: this.clientId });
								}
								// 重庆科学城H5
								if (this.chunks.length > 0) {
									mittBus.emit('szrPlayStartH5', { chunksList: this.chunks, clientId: this.clientId });
								}
								if (this.chunks.length > 0 && route.path.indexOf('videoLargeScreen') != -1) {
									this.startPlayback();
									textToSpeechAndPlay(this.chunks, this.clientId, () => {
										this.stopPlayback();
									});
								}

								this.plainText = dialogueByStream.plainText ? dialogueByStream.plainText : dialogueByStream.answer;

								if (route.path.indexOf('twoCitiesPlam-cq') != -1 || route.path.indexOf('policyHelp-cq') != -1) {
									if (!dialogueByStream.matterGuide) {
										dialogueByStream.matterGuide = [];
									} else {
										if (!Array.isArray(dialogueByStream.matterGuide)) {
											dialogueByStream.matterGuide = this.convertToNameTextArray(dialogueByStream.matterGuide);
											dialogueByStream.benkeGuide = true;
										} else {
											dialogueByStream.benkeGuide = false;
										}
									}
								}
								if (dialogueByStream.reasoningContent) {
									let reasoningContents = dialogueByStream.reasoningContent
										.split('\n\n') // 按双换行符分割段落
										.filter((paragraph) => paragraph.trim() !== '') // 过滤空段落
										.map(
											(paragraph, index) =>
												`<span key=${index} class="reason-paragraph">
                      ${paragraph.trim()}
                    </span>`
										)
										.join('');
									let reasoningContent = `<span class="reason-cont"><span class="reason-line"></span>${reasoningContents}</span>`;
									let answer = dialogueByStream.answer ? dialogueByStream.answer : '';
									// 元枢对话总结所用字段
									this.answerParams = answer;
									dialogueByStream.answer = reasoningContent + answer;
								}
								this.progressList =
									dialogueByStream.progressList && dialogueByStream.progressList.length > 0 ? dialogueByStream.progressList : this.progressList;
								if (dialogueByStream.answer) {
									this.answerNew = dialogueByStream.answer;
								}
								this.refList = dialogueByStream.refList ? dialogueByStream.refList : this.refList;
								if (
									dialogueByStream &&
									dialogueByStream.answer &&
									dialogueByStream.answer.length > 0 &&
									typeof dialogueByStream.answer === 'string'
								) {
									if (route.path.indexOf('TTAnalysis') != -1) {
										// dialogueByStream.answer = `"\n\n###  近期中美局势深度分析\n\n####  一、结构性矛盾与战略互疑深化◹[2]◸\n\n1. **\"信心焦虑情结\"的螺旋效应**  ◹[4]◸\n\n   - 根据叶雪等学者研究，中美陷入\"自信驱动-焦虑反制\"的恶性循环。中国通过\"新质生产力\"（如DeepSeek大模型）展现技术自信，但半导体封锁暴露产业链脆弱性（7nm以下芯片国产化率仅12%）；美国虽维持军事霸权（2025年国防预算达8860亿美元），却对华为5G全球市占率回升至28%产生战略焦虑。◹[2][4]◸\n\n2. **意识形态叙事固化**  ◹[4]◸\n\n   - 美国将\"民主vs威权\"框架制度化，通过《2025印太数字同盟协定》限制中国技术标准输出；中国则加速构建\"东升西降\"话语体系，在G77峰会推动发展权议题，双方认知鸿沟扩大至37个政策领域（较2020年增加15项）。◹[1][5]◸\n\n####  二、经济博弈进入制度竞争阶段◹[1]◸\n\n1. **关税武器智能化升级**  ◹[5]◸\n\n   - 特朗普政府实施\"动态关税\"机制，对电动汽车组件征收154%的浮动税率（随中国产能利用率调整），导致比亚迪墨西哥工厂出口成本激增22%。中国反制措施聚焦稀土加工技术出口限制，直接影响美F-35战机供应链。◹[3][5]◸\n\n2. **金融脱钩加速显性化**  \n\n   - SWIFT替代系统CIPS跨境支付额突破86万亿元（同比+41%），但美元结算占比仍达63%。中概股除名新规引发港股流动性危机，2025年Q2日均成交额萎缩至683亿港元（创2016年来新低）。◹[2][4]◸\n\n####  三、科技冷战2.0版特征显现\n\n1. **半导体封锁立体化**  \n\n   - 美国组建\"芯片四方联盟2.0\"，将成熟制程（28nm）纳入管制，导致中芯国际扩产计划延迟9个月。中国启动\"193nm ArF激光器\"攻关专项，但光刻胶国产化率仍不足19%。◹[3][5]◸\n\n2. **AI竞赛规则权争夺**  ◹[3]◸\n\n   - 双方在联合国AI伦理框架谈判僵持：美国主张\"人权红线\"审查机制，中国推动\"发展优先\"原则。DeepSeek与OpenAI在AGI安全协议上的技术路线分歧，反映标准制定权争夺白热化。◹[1][4]◸\n\n####  四、地缘战略对冲加剧\n1. **印太军事部署升级**  \n\n   - 美军在菲律宾新增巴丹群岛基地，部署中程导弹系统，将南海侦察频次提升至日均7.2架次（较2024年+35%）。中国反制措施包括与所罗门群岛签订安全协定2.0版，允许军舰补给设施前置。◹[2][5]◸\n\n2. **经济伙伴关系重构**  ◹[2]◸\n\n   - RCEP框架下中国-东盟数字贸易额突破2.1万亿美元，但越南对华贸易逆差扩大至创纪录的632亿美元，引发产业链转移争议。欧盟《关键原材料法案》使中企锂矿投资受阻，刚果（金）项目65%面临重审。◹[3][4]◸\n\n####  五、风险与转机并存\n1. **危机管控机制失效**  \n\n   - 网络安全对话停滞导致2025年5月发生跨境数据泄露事件，涉及1800万用户信息被相互指控。但二轨外交（如清华-布鲁金斯AI治理工作组）仍保持季度会晤机制。◹[2][5]◸\n\n2. **非传统领域合作窗口**  \n\n   - 在北极科考领域达成《斯瓦尔巴数据共享协议》，联合建立第3个观测站。禁毒合作查获芬太尼前体化学品23吨，但美方引渡请求执行率仍低于12%。◹[1][4]◸\n\n####  六、趋势预判与政策建议◹[5]◸\n\n1. **短期（2025-2027）**  ◹[4]◸\n\n   - 科技\"小院高墙\"政策将扩展至量子计算领域，但AI治理或成新谈判切入点  ◹[5]◸\n\n   - 人民币跨境支付系统（CIPS）可能遭遇SWIFT+数字美元联盟反制◹[5]◸\n\n\n2. **中长期破局建议**  \n\n   - 建立\"数字丝绸之路\"与\"印太数字贸易区\"的规则互认机制  ◹[3]◸\n\n   - 将气候技术转让纳入贸易谈判框架，创建碳关税与光伏技术捆绑协议  ◹[2]◸\n\n   - 重启\"中美青年科学家交流计划\"，设立联合科研基金（建议规模50亿美元/年）\n\n\n####  结语\n当前中美博弈已超越传统大国竞争范畴，演变为数字文明时代的发展范式之争。历史经验表明，技术革命周期（约15-20年）往往重塑国际格局。双方需在确保核心利益前提下，探索\"量子纠缠式\"竞合关系——既保持必要竞争张力，又在人类共同挑战领域构建协同框架。正如伯纳姆所言：\"21世纪的大国博弈，胜负不在歼灭而在演化速度。\"◹[2][4]◸`;
										dialogueByStream.answer =
											dialogueByStream.answer &&
											dialogueByStream.answer
												// 1. 先替换 `<span>` 为 `<div>`
												.replace(/<span/g, '<div')
												.replace(/<\/span>/g, '</div>')
												// 2. 转义 [xxx]: 开头的模式（防止被识别为链接引用）
												.replace(/- \[(.*?)\]:/g, '- \\[$1\\]:')
												// 3. 处理带链接的条目（保留链接格式）
												.replace(/(\[.*?\])(\(.*?\))/g, '$1$2')
												// 4. 先把 ◹[2][4]◸ 拆分成 ◹[2]◸◹[4]◸
												.replace(/◹(\[\d+\])+◸/g, (match) => {
													// 提取所有 [数字] 部分，如 [2][4] → ["[2]", "[4]"]
													const indices = match.match(/\[\d+\]/g) || [];
													// 拼接成 ◹[2]◸◹[4]◸
													return indices.map((index) => `◹${index}◸`).join('');
												})
												// 5. 最后正常渲染每个单独的 ◹[2]◸
												.replace(/◹\[(\d+)\]◸/g, (match, p1) => {
													return isMobile.value
														? ``
														: `<span class="ragTag-TTAnalysis" style="margin-right: 8px;z-index: 88" data-index="${p1}"><iconpark-icon name="file-text-fill" size="14" fill="#86909C"></iconpark-icon></span>`;
												});
									} else if (route.path.indexOf('knowledgeDetails-searchNew') != -1) {
										dialogueByStream.answer =
											dialogueByStream.answer &&
											dialogueByStream.answer
												.replace(/<span/g, '<div')
												.replace(/<\/span>/g, '</div>')
												// 转义 [xxx]: 开头的模式（保留方括号但防止被识别为链接引用）
												.replace(/- \[(.*?)\]:/g, '- \\[$1\\]:')
												// 处理带链接的条目（保留链接格式）
												.replace(/(\[.*?\])(\(.*?\))/g, '$1$2') // 保持原有链接不变
												.replace(/◹\[(\d+)\]◸/g, (match, p1) => {
													// const index = parseInt(p1, 10) - 1; // 转换为 0 开始的索引
													// if (index >= 0 && index < this.refList.length) {
													//   const refItem = this.refList[index];
													//   if (refItem.content) {
													//     refItem.content = refItem.content.replace(/(?:\n)?<em>(.*?)<\/em>/g, (_, content) => {
													//       console.log( refItem.content,' refItem.content');

													//       return `<span style="font-weight: bold !important; color: #7E56EB !important;>${content}</span>`;
													//     });
													//   }
													// }
													return isMobile.value || appInfo.sourceShowFlag !== '1' || appInfo.refIndexFlag !== '是'
														? ``
														: `<span class="ragTag-docx" style="margin-right: 8px;z-index: 88" data-index="${p1}">${p1} | docx</span>`;
												}); // 下标
									} else {
										dialogueByStream.answer =
											dialogueByStream.answer &&
											dialogueByStream.answer
												.replace(/<span/g, '<div')
												.replace(/<\/span>/g, '</div>')
												// 转义 [xxx]: 开头的模式（保留方括号但防止被识别为链接引用）
												.replace(/- \[(.*?)\]:/g, '- \\[$1\\]:')
												// 处理带链接的条目（保留链接格式）
												.replace(/(\[.*?\])(\(.*?\))/g, '$1$2') // 保持原有链接不变
												.replace(/◹\[(\d+)\]◸/g, (match, p1) => {
													// const index = parseInt(p1, 10) - 1; // 转换为 0 开始的索引
													// if (index >= 0 && index < this.refList.length) {
													//   const refItem = this.refList[index];
													//   if (refItem.content) {
													//     refItem.content = refItem.content.replace(/(?:\n)?<em>(.*?)<\/em>/g, (_, content) => {
													//       console.log( refItem.content,' refItem.content');

													//       return `<span style="font-weight: bold !important; color: #7E56EB !important;>${content}</span>`;
													//     });
													//   }
													// }
													return isMobile.value || appInfo.sourceShowFlag !== '1' || appInfo.refIndexFlag !== '是'
														? ``
														: `<span class="ragTag" data-index="${p1}"><svg viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg" width="61" height="61"><path fill="#0071be" d="M11.997 5v3.336a3.517 3.517 0 0 1-.879 2.464 3.728 3.728 0 0 1-2.32 1.2v-1.44c.572-.189.962-.469 1.172-.84.211-.37.315-.824.315-1.362H8.798V5h3.2ZM7.2 5v3.336a3.502 3.502 0 0 1-1.903 3.26A3.733 3.733 0 0 1 4 12v-1.44c.571-.188.962-.468 1.171-.838.212-.371.316-.825.316-1.363H4V5h3.2Z" data-follow-fill="#9A99AA"></path></svg></span>`;
												}); // 下标
									}
								}
								this.chatList[this.chatList.length - 1] = JSON.parse(JSON.stringify(dialogueByStream));
								this.chatList[this.chatList.length - 1].loading = true;
								this.chatList[this.chatList.length - 1].progressList = this.progressList;

								if (route.path.indexOf('videoLargeScreen') != -1) {
									setScrollPosition('.message-list');
								} else {
									setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
								}
							} catch (error) {
								if (this.abortController) {
									this.abortController?.abort();
								}
								this.chatList[this.chatList.length - 1].answer = this.answerStr ? this.answerStr : this.errorText;
								this.chatList[this.chatList.length - 1].question = data.content;
								sessionStorage.setItem('answerFlag', false);
								this.chatList[this.chatList.length - 1].loading = false;
								this.dialogueLoading = false;
							}
						},
						() => {
							this.updateNameConversation();
							this.firstConsumeTip();
							this.setAiRobotChat('。');
							this.refList && mittBus.emit('updateReList', JSON.stringify(this.refList));
							// try {
							// 	const jsonObj = JSON.parse(text); // 将字符串解析为一个对象
							// 	if (typeof jsonObj === 'object' && jsonObj !== null) {
							// 		const jsonFormatted = JSON.stringify(jsonObj, null, 2);
							// 		dialogueByStream.answer = '```json\n' + jsonFormatted + '\n```';
							// 	}
							// } catch (error) {}
							if (!dialogueByStream.nodeId) {
								this.chatList[this.chatList.length - 1] = dialogueByStream;
							}
							this.chatList[this.chatList.length - 1].loading = false;

							//溯源接口，在调用之前，都要等3秒
							// appInfo && dialogueByStream.answerFlag && mittBus.emit('dialogueId', appInfo);
							appInfo && mittBus.emit('dialogueId', appInfo);
							appInfo && !dialogueByStream.answerFlag && mittBus.emit('noAnswerFlag');
							const route = router.currentRoute.value;
							if (route.path.indexOf('videoLargeScreen') == -1) {
								this.dialogueLoading = false;
							}

							if (this.abortController) {
								this.abortController?.abort();
							}
							if (this.chatList[this.chatList.length - 1].answer === '') {
								this.chatList[this.chatList.length - 1].answer = this.errorText;
								this.chatList[this.chatList.length - 1].question = data.content;
								sessionStorage.setItem('answerFlag', false);
							}

							clearInterval(this.timer);
							if (route.path.indexOf('videoLargeScreen') != -1) {
								setScrollPosition('.message-list');
							} else {
								setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
							}
						},
						() => {
							if (this.abortController) {
								this.abortController?.abort();
							}
							if (this.chatList[this.chatList.length - 1].answer === '') {
								this.chatList[this.chatList.length - 1].answer = this.errorText;
								this.chatList[this.chatList.length - 1].question = data.content;
								sessionStorage.setItem('answerFlag', false);
							}
							this.chatList[this.chatList.length - 1].loading = false;
							const route = router.currentRoute.value;
							if (route.path.indexOf('videoLargeScreen') == -1) {
								this.dialogueLoading = false;
							}
							clearInterval(this.timer);
						},
						headerParams
					);
				}
			} catch (error) {
				console.log(error, '000heierror');
				this.fileAttachmentList = [];
				if (this.abortController) {
					this.abortController?.abort();
				}
				this.chatList[this.chatList.length - 1].answer = this.answerStr ? this.answerStr : this.errorText;
				this.chatList[this.chatList.length - 1].question = data.content;
				sessionStorage.setItem('answerFlag', false);
				this.chatList[this.chatList.length - 1].loading = false;
				this.dialogueLoading = false;
			} finally {
				// this.chatList[this.chatList.length - 1].loading = false;
				this.fileAttachmentList = [];
			}
		},
		async addChatList(data: any) {
			this.chatList = data;
		},
		async updateNameConversation() {
			const route = router.currentRoute.value;
			const id = route.params.conversationId;
			// const list = await updateNameConversation(id);
			// let index = this.history.findIndex((item) => item.id == id);
			// this.history[index].name = list.data;
		},
		async initChatList(id: number | string) {
			this.clearChat();
			this.chatLoading = true;
			const list = await listDialogues(id);

			this.chatLoading = false;
			this.chatList = [];
			this.chatList = list.data;

			let arr = this.history.filter((m) => m.id == id);
			this.isSensitive = arr[0]?.isSensitive;

			if (!list.data) {
				const route = router.currentRoute.value;
				router.push({ name: `chat`, params: { appId: route.params.appId, conversationId: '' } });
				return true;
			}
			if (this.chatList.length) {
				this.scrollbarBottom = true;
				const route = router.currentRoute.value;
				if (route.path.indexOf('videoLargeScreen') != -1) {
					// 视频背景大屏模板是固定尺寸，固定使用.message-list
					setScrollPosition('.message-list');
				} else {
					setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
				}
			}
		},
		canUpload() {
			let nodeData = JSON.parse(sessionStorage.getItem(`nodeData`));
			if (!nodeData) {
				return false;
			}
			let cells = nodeData.nodes;
			let startNode = cells.find((el: any) => el.nodeType === 1).output;
			return (
				startNode.length &&
				startNode.some((item) => item.type && ['default', 'image', 'doc', 'code', 'file', 'ppt', 'array[file]', 'txt', 'excel'].includes(item.type))
			);
		},
		getStartParams() {
			let nodeData = JSON.parse(sessionStorage.getItem(`nodeData`));
			if (!nodeData) {
				return false;
			}
			let cells = nodeData.nodes;
			return cells.find((el: any) => el.nodeType === 1).output;
		},
		getStartSettings() {
			let nodeData = JSON.parse(sessionStorage.getItem(`nodeData`));
			if (!nodeData) {
				return false;
			}
			let cells = nodeData.nodes;
			return JSON.parse(cells.find((el: any) => el.nodeType === 1).settings);
		},
		getConversationName() {
			const route = router.currentRoute.value;
			const id = route.params.conversationId;
			let index = this.history.findIndex((item) => item.id == id);
			return this.history[index].name;
		},
		async clearChat() {
			if (this.abortController) {
				this.abortController?.abort();
			}
			if (this.clientId && this.chatList.length) {
				clearInterval(this.timer);
				//this.setAiRobotChat(this.chatList[this.chatList.length - 1].answer)
				await closeDialogueConn({ clientId: this.clientId });
				this.updateNameConversation();
				this.chatList[this.chatList.length - 1].loading = false;
				this.chatList[this.chatList.length - 1].answer = this.answerStr || '停止生成';
				this.dialogueLoading = false;
				this.setAiRobotChat('break', false, 'break');
				this.firstConsumeTip();
			}
		},
		setFileList(data: never) {
			if (this.fileList.length >= 5) {
				this.fileList.unshift();
			} else {
				this.fileList.push(data);
			}
		},
		setFileUploadList(data: never) {
			this.fileUploadList = data;
		},
		setFileUpdate(params: any) {
			for (let i in params) {
				this.fileUpdateCV[i] = params[i];
			}
		},
		async initFileList(data: never) {
			const list = await listFile(data);
			this.fileList = list.data;
		},
		setAiRobotChat(str: string, mark: boolean = false, type: boolean = false) {
			if (str === this.errorText) {
				sessionStorage.setItem('answerFlag', false);

				return;
			}
			str = str.replace(/\n/g, '。');
			str = removeMd(str);
			str = str.replace(/:::r|:::|\n/g, '');

			str = str.replace(/\[\d+\]/g, '');
			if (mark || (this.isOpen && this.rightFlag == 2)) {
				robotStore.muted = false;
				robotStore.sendMsg(str, type);
			}
		},
		delFileList(id: number | string) {
			this.fileList.filter((m) => m.id == id);
			let id1 = this.fileList.findIndex((item) => item.id == id);
			this.fileList.splice(id1, 1);
		},
		setUsingContext(context: boolean) {
			this.usingContext = context;
			this.recordState();
		},

		async getConversationList(aid: number | string, cid: number | string) {
			this.clearChat();
			let data = {};
			const route = router.currentRoute.value;
			let chatMode = route.path.indexOf('chat') != -1;

			if (!chatMode) {
				data.knowledgeBaseId = aid;
			} else {
				data.appId = aid;
			}
			const list = await listConversations(data);
			list.data = list.data || [];
			this.history = list.data;
			this.active = cid;
			this.reloadRoute(cid);
		},

		async getConversationListByPage(appId: string, c: number, cid: number | string) {
			let data = {
				current: c,
				size: 20,
			};
			const route = router.currentRoute.value;
			let chatMode = route.path.indexOf('chat') != -1;

			if (!chatMode) {
				data.knowledgeBaseId = appId;
			} else {
				data.appId = appId;
			}
			const list = await listConversationsByPage(data);
			const isFirstPage = c === 1;
			const {
				data: { current, size, total, records = [] },
			} = list;

			if (isFirstPage) {
				this.clearChat();
				this.history = records;
			} else {
				this.history.push(...records);
			}
			this.active = cid;
			this.reloadRoute(cid);
			return current * size >= total;
		},

		async addHistory(data: never, history: Chat.History, chatData: Chat.Chat[] = []) {
			if (this.abortController) {
				this.abortController?.abort();
			}
			const route = router.currentRoute.value;
			let chatMode = route.path.indexOf('chat') != -1;

			if (!chatMode) {
				data.knowledgeBaseId = data['appId'];
			}
			const list = await addConversation(history);
			let index = this.history.findIndex((item) => item.id == list.data.conversationId);

			this.conversationsId = list.data.conversationId;
			this.conversationsIdforUploadFlag = list.data.conversationId;

			const params = {
				name: list.data.name,
				id: list.data.conversationId,
				isEdit: false,
				isSensitive: list.data.isSensitive,
				createTime: list.data.createTime,
			};
			if (index < 0) {
				this.history.unshift(params);
				this.chat.unshift({ id: history.id, data: chatData });
			}

			this.active = list.data.conversationId;
			await this.reloadRoute(list.data.conversationId);
		},

		async updateHistory(id: number, name: string, edit: Partial<Chat.History>) {
			if (!edit.isEdit) await updateConversation({ id, name });
			const index = this.history.findIndex((item) => item.id === id);
			if (index !== -1) {
				this.history[index] = { ...this.history[index], ...edit };
				this.recordState();
			}
		},

		async deleteHistory(id: number, index: number) {
			await deleteConversation(id);
			this.history.splice(index, 1);
			this.chat.splice(index, 1);

			if (this.history.length === 0) {
				this.active = null;
				this.reloadRoute();
				return;
			}

			if (index > 0 && index <= this.history.length) {
				const id = this.history[index - 1].id;
				this.active = id;
				this.reloadRoute(id, {});
				return;
			}

			if (index === 0) {
				if (this.history.length > 0) {
					const id = this.history[0].id;
					this.active = id;
					this.reloadRoute(id, {});
				}
			}

			if (index > this.history.length) {
				const id = this.history[this.history.length - 1].id;
				this.active = id;
				this.reloadRoute(id, {});
			}
		},

		async setActive(id: number | string, isSensitive: boolean) {
			this.isSensitive = isSensitive;
			this.active = id;
			await this.reloadRoute(id, {});
		},

		async getChatList(id: number | string) {
			this.active = id;
			this.chatList = [];
			if (id) {
				await this.initChatList(id);
			}
		},
		getHistoryChatRecordsList(id: number | string, item: any) {
			this.clearChat();
			this.active = id;
			this.chatList = item.sort(function (a, b) {
				return new Date(a.createTime).getTime() - new Date(b.createTime).getTime();
			});
			this.isSensitive = item[0]?.isSensitive;
			if (this.chatList.length) {
				this.scrollbarBottom = true;
				const route = router.currentRoute.value;
				if (route.path.indexOf('videoLargeScreen') != -1) {
					setScrollPosition('.message-list');
				} else {
					setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
				}
			}
		},
		async getChatRecordsList(id: number | string) {
			this.active = id;
			this.chatList = [];
			if (id) {
				await this.initChatRecordsList(id);
			}
		},
		async initChatRecordsList(id) {
			this.clearChat();
			this.chatLoading = true;
			const route = router.currentRoute.value;
			const list = await recordGetRecord({
				applicationId: localStorage.getItem(`${route.params.appId}appId`),
				pageNo: 1,
				pageSize: 999,
				verifyDeptId: '',
				verifyStatus: '',
				text: '',
				conversationId: id,
				deleted: 0,
				userId: sessionStorage.getItem('userId') || '',
			});

			this.chatLoading = false;
			this.chatList = [];
			list.data.list.sort(function (a, b) {
				return new Date(a.createTime).getTime() - new Date(b.createTime).getTime();
			});
			this.chatList = list.data.list;

			let arr = this.history.filter((m) => m.id == id);
			this.isSensitive = arr[0]?.isSensitive;

			if (!list.data.list) {
				const route = router.currentRoute.value;
				router.push({ name: `chat`, params: { appId: route.params.appId, conversationId: id } });
				return true;
			}
			if (this.chatList.length) {
				this.scrollbarBottom = true;
				const route = router.currentRoute.value;
				if (route.path.indexOf('videoLargeScreen') != -1) {
					setScrollPosition('.message-list');
				} else {
					setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
				}
			}
		},
		setParamsDrawerVisible(flag: boolean) {
			this.paramsDrawerVisible = flag;
		},
		setUploadDrawerVisible(flag: boolean) {
			this.uploadDrawerVisible = flag;
		},
		async getFeedbackList() {
			const res = await feedbackListLabel(1);
			if (res.code == 200) {
				this.zanForm.options = res.data;
			}
			const list = await feedbackListLabel(0);
			if (list.code == 200) {
				this.caiForm.options = list.data;
			}
		},
		// // 敏感词过滤
		// async checkSensitiveWord(text: string) {
		//   const route = router.currentRoute.value;
		//   const parmas = {
		//     applicationId: localStorage.getItem(`${route.params.appId}appId`),
		//     content: text,
		//   };
		//   const res = await apiCheckSensitiveWord(parmas);
		//   if (res.data) {
		//     Message.warning('无法回答该问题，我们换个话题聊聊吧');
		//     this.addHistory({ appId: route.params.appId }, { name: '新建会话' });
		//     return;
		//   }
		// },
		// 敏感词检测
		async checkSensitiveWord(text: string) {
			let isCheckSensitiveWord = false;
			const route = router.currentRoute.value;
			const parmas = {
				applicationId: localStorage.getItem(`${route.params.appId}appId`),
				content: text,
			};
			const res = await apiCheckSensitiveWord(parmas);
			if (res.code == '000000') {
				if (!res.data) {
					isCheckSensitiveWord = true;
				} else {
					// showToast.value = true;
					mittBus.emit('showSensitiveWordToast');
					isCheckSensitiveWord = false;
				}
			} else {
				isCheckSensitiveWord = false;
			}
			return isCheckSensitiveWord;
		},
		async getPersonagePriceFun() {
			const res = await getPersonagePrice(true);
			const count = this.currentBalance - res?.data.currentBalance;
			if (this.currentBalance == res?.data.currentBalance) {
				this.getPersonagePriceFun();
			} else {
				Message.warning({
					id: 'firstConsumeTip',
					content: () => {
						return h('div', [
							`该次服务调用扣费${count.toFixed(6)}元，您账户当前剩余金额为${res?.data.currentBalance}元`,
							h(
								'span',
								{
									id: 'detail-text',
									onClick: () => {
										this.handleClickDetail();
									},
								},
								'去充值>'
							),
							'',
						]);
					},
					duration: 3000,
				});
				Session.set('firstPay', false);
			}
		},
		async firstConsumeTip() {
			if (Session.get('firstPay') == 'true' && this.isFirstConsume) {
				try {
					this.getPersonagePriceFun();
				} catch (err) {
					throw new Error(err);
				}
			}
		},
		async handleClickDetail() {
			router.push({
				path: '/personal/voucherPage',
			});
		},
		async reloadRoute(id?: number | string, querys?: object) {
			const route = router.currentRoute.value;
			const query = querys ? querys : route.query;
			// let name = route.path.indexOf('chat') != -1 ? 'chat' : route.path.indexOf('knowledgeDetails') != -1 ? 'knowledgeDetails' : 'knowledge';
			let name = '';
			if (route.path.indexOf('chat') != -1) {
				name = 'chat';
			}
			if (route.path.indexOf('knowledgeDetails') != -1) {
				name = 'knowledgeDetails';
			}
			if (route.path.indexOf('knowledgeDetails-new') != -1) {
				name = 'knowledgeDetailsNew';
			}
			if (route.path.indexOf('knowledgeDetails-hf') != -1) {
				name = 'knowledgeDetailsHf';
			}
			if (route.path.indexOf('knowledgeDetails-tz') != -1) {
				name = 'knowledgeDetailsTz';
			}
			if (route.path.indexOf('knowledgeDetails-ai') != -1) {
				name = 'knowledgeDetailsAI';
			}
			if (route.path.indexOf('stockExchangeSZ') != -1) {
				name = 'stockExchangeSZ';
			}
			if (route.path.indexOf('fileParsing') != -1) {
				name = 'fileParsing';
			}
			if (route.path.indexOf('aiVideoSplitting') != -1) {
				name = 'aiVideoSplitting';
			}
			if (route.path.indexOf('reportGenerate') != -1) {
				name = 'reportGenerate';
			}
			if (route.path.indexOf('digitalPeople') != -1) {
				name = 'digitalPeople';
			}
			if (route.path.indexOf('intelligentTranslation') != -1) {
				name = 'intelligentTranslation';
			}
			if (route.path.indexOf('intelligentReport') != -1) {
				name = 'intelligentReport';
			}
			if (route.path.indexOf('aiImageTemplate') != -1) {
				name = 'aiImageTemplate';
			}
			if (route.path.indexOf('createTemplate') != -1) {
				name = 'createTemplate';
			}
			if (route.path.indexOf('aiVideoTemplate') != -1) {
				name = 'aiVideoTemplate';
			}
			if (route.path.indexOf('municipalSupervisionBureau') != -1) {
				name = 'municipalSupervisionBureau';
			}
			if (route.path.indexOf('smqa') != -1) {
				name = 'municipalSupervisionBureauNewUI';
			}
			if (route.path.indexOf('knowledgeDetailSjj') != -1) {
				name = 'knowledgeDetailSjj';
			}
			// 标准应用
			if (route.path.indexOf('appTemplate') != -1) {
				name = 'appTemplate';
			}
			// H5通用模板
			if (route.path.indexOf('mobileUniversalTemplate') != -1) {
				name = 'mobileUniversalTemplate';
			}
			// 视频背景大屏
			if (route.path.indexOf('videoLargeScreen') != -1) {
				name = 'videoLargeScreen';
			}
			// xxxxxxx移动端模板
			if (route.path.indexOf('mobileDazhouTemplate') != -1) {
				name = 'mobileDazhouTemplate';
			}
			// 标准应用-粤语版
			if (route.path.indexOf('cantoneseTemplate') != -1) {
				name = 'cantoneseTemplate';
			}
			// 新一套数字人(市监局＋接入数字人)
			if (route.path.indexOf('DHTemplate') != -1) {
				name = 'DHTemplate';
			}
			if (route.path.indexOf('knowledgeDetails-search') != -1) {
				name = 'knowledgeDetailsSearch';
			}
			// 基础应用
			if (route.path.indexOf('basicTemplate') != -1) {
				name = 'basicTemplate';
			}
			if (route.path.indexOf('twoCitiesPlam-cq') != -1) {
				name = 'twoCitiesPlamCq';
			}
			if (route.path.indexOf('policyHelp-cq') != -1) {
				name = 'policyHelpCq';
			}
			// 智库数据分析
			if (route.path.indexOf('TTAnalysis') != -1) {
				name = 'TTAnalysis';
			}
			// 勿动这个放到最后
			if (route.path.indexOf('knowledgeDetails-searchNew') != -1) {
				name = 'knowledgeDetailsSearchNew';
			}
			if (route.path.indexOf('knowledgeDetails-searchNew-hk') != -1) {
				name = 'knowledgeDetailsSearchNewHk';
			}
			if (route.path.indexOf('knowledgeDetails-searchNew-inudtry') != -1) {
				name = 'knowledgeDetailsSearchNewInudtry';
			}
			if (route.path.indexOf('knowledgeDetails-searchNew-zh') != -1) {
				name = 'knowledgeDetailsSearchNewZh';
			}
			await router.push({ name: name, params: { appId: route.params.appId, conversationId: id || '' }, query });
		},

		recordState() {
			setLocalState(this.$state);
		},
		setPolicy(value: any) {
			this.policyList = value;
		},
		convertToNameTextArray(obj) {
			return Object.keys(obj).map((key) => ({ name: key, content: obj[key] }));
		},
		isFileLink(url: string) {
			// 常见网页扩展名
			const webExtensions = ['.html', '.htm', '.php', '.asp', '.aspx', '.jsp', '.do', '.action'];

			// 常见文件扩展名
			const fileExtensions = [
				'.pdf',
				'.doc',
				'.docx',
				'.xls',
				'.xlsx',
				'.ppt',
				'.pptx',
				'.jpg',
				'.jpeg',
				'.png',
				'.gif',
				'.bmp',
				'.svg',
				'.mp3',
				'.mp4',
				'.avi',
				'.mov',
				'.wmv',
				'.zip',
				'.rar',
				'.7z',
				'.tar',
				'.gz',
				'.exe',
				'.dmg',
				'.pkg',
				'.apk',
				'.ipa',
			];

			// 获取URL中的路径部分
			const path = new URL(url, window.location.href).pathname.toLowerCase();

			// 检查是否是文件扩展名
			for (const ext of fileExtensions) {
				if (path.endsWith(ext)) {
					return true;
				}
			}

			// 检查是否是网页扩展名
			for (const ext of webExtensions) {
				if (path.endsWith(ext)) {
					return false;
				}
			}

			// 默认情况下，没有扩展名或未知扩展名视为网页链接
			return false;
		},
	},
});
