<template>
	<div class="relative flex flex-col h-full w-full" :class="{ centerMobile: isMobile }">
		<div class="center-side-parent" id="bg-main" :style="`height: 100%;`">
			<!-- <div @click="showSZRHandler" style="cursor: pointer">{{ showSZR ? '关闭数字人' : '打开数字人' }}</div> -->
			<w-scrollbar ref="layoutScrollbarRef" class="center-side1">
				<div class="container-center" :class="[showSZR ? 'pt208' : '']">
					<img v-if="showSZR" src="/src/assets/chongqing/people.png" style="width: 100%; height: 617px; position: absolute; top: 10px" />
					<!-- <div v-show="showSZR" class="szr" style="width: 100%; height: 100%"></div> -->

					<!-- 大学城青年综合服务平台-mobile -->
					<div v-if="isMobile && !showSZR && !chatList.length" class="cq-head">
						<div class="cq-head-box">
							<img v-if="facadeImageUrl" :src="facadeImageUrl" class="cq-head-box-left" /> 
							<img v-else src="/src/assets/chongqing/top-people.png" class="cq-head-box-left" /> 
							<div class="cq-head-box-right">
								<!-- <img class="img1" src="/src/assets/chongqing/title1.png" alt="" /> -->
								<img v-if="getAppDetail()?.identityIcon?.includes('.png')" :src="getAppDetail()?.identityIcon" class="img1" />
								<div v-else class="title1">{{ getAppDetail()?.identityIcon }}</div>
								<!-- <div class="title1">您好，我是科学城帮帮帮成员小嗨</div> -->
								<div class="title2">{{ getAppDetail()?.greeting }}</div>
							</div>
						</div>
					</div>
					<div :style="{ overflow: isMobile ? 'hidden' : 'scroll' }" class="center-side" :class="[isMobile && !showSZR && !chatList.length ? 'height1' : 'height2']" v-if="!loading">
						<CenterInitItem v-if="curRouteId == judicialUrl"></CenterInitItem>
						<div :class="['message-list', { 'message-list-one': chatList.length }]">
							<div class="question-box" v-if="curRouteId == policyUrl && !isMobile">
								<img class="jqr-icon" style="height: 121px" src="/src/assets/zc/jqr1.png" alt="" />
								<div>
									<div style="display: flex; align-items: center; margin-bottom: 8px">
										<img style="width: 300px" :src="identityIconUrl() ? identityIconUrl() : '/src/assets/chatImages/pageTitle.svg'" />
									</div>
									<div class="box">
										<div class="title">{{ getAppDetail()?.greeting }}</div>
									</div>
									<div class="quest-list">
										<div
											class="item"
											:class="{ active: curQuestion == item.question }"
											v-for="(item, index) in tjQuestList"
											@click="sendQuestion(item.question)"
											:key="index"
										>
											<div class="box">
												<span style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap">{{ item.question }}</span>
												<img style="height: 20px" src="/src/assets/zc/send1.png" />
											</div>
										</div>
									</div>
								</div>
							</div>
							<div
								style="display: flex; align-items: flex-end"
								:style="{ padding: isMobile ? ' 0 10px' : ' 0 30px',marginRight: isMobile ? '32px' : '' }"
								:class="[showSZR ? '' : 'pt42']"
								v-if="curRouteId == policyUrl && getAppDetail()?.prologue"
							>

								<w-avatar style="margin-right: 8px" :size="32"><img :src="getAppDetail()?.robotIcon || BotAvatar" /></w-avatar>
								<div
									style="flex: 1"
									class="fontSize16 text-black min-w-[20px] text-box-1716763096571080706 bg-[#fff] dark:bg-[#fff] message-request text-black left-answer"
								>
									<img src="/src/assets/chongqing/left-top1.svg" alt="" class="left-top" />
									<div class="inner">
										{{
											getAppDetail()?.prologue || '您好，很高兴为您服务！请问有什么可以帮您的？'
										}}
									</div>
								</div>
							</div>
							<div :style="{ padding: isMobile ? '0' : ' 0 30px' }" v-for="(item, index) of chatList" :key="index">
								<YYMessageCqH5
									:answer="item.answer"
									:dialogueId="item.dialogueId"
									:citations="item.citations || []"
									:createTime="item.createTime"
									:id="item.id + ''"
									:dialogueFileIds="item.dialogueFileIds || []"
									:dialogueFolderIds="item.dialogueFolderIds || []"
									:paragraph="item.paragraph || ''"
									:skillId="item.skillId || ''"
									:param="item.param"
									:question="item.question"
									:loading="item.loading"
									:inversion="true"
									:isLast="false"
									:key="index + 'r'"
									:feedBackStatus="item.feedBackStatus"
									:imgUrl="item.imgUrl"
									:sensitive="item.sensitive"
									:stopChatLoading="stopChatLoading"
									:matterGuide="item.matterGuide"
									:benkeGuide="item.benkeGuide"
									:finishReason="item.finishReason"
								>
								</YYMessageCqH5>
								<YYMessageCqH5
									:answer="item.answer"
									:dialogueId="item.dialogueId"
									:citations="item.citations || []"
									:createTime="item.createTime"
									:id="item.id + ''"
									:dialogueFileIds="item.dialogueFileIds || []"
									:dialogueFolderIds="item.dialogueFolderIds || []"
									:paragraph="item.paragraph || ''"
									:skillId="item.skillId || ''"
									:param="item.param"
									:question="item.question"
									:loading="item.loading"
									:isLast="index == chatList.length - 1"
									:key="index + 'l'"
									:feedBackStatus="item.feedBackStatus"
									:imgUrl="item.imgUrl"
									:sensitive="item.sensitive"
									:stopChatLoading="stopChatLoading"
									:suggestOrg="item.suggestOrg"
									:contentQaType="item.contentQaType"
									:answerFlag="item.answerFlag"
									:plainText="item.plainText"
									:matterGuide="item.matterGuide"
									:benkeGuide="item.benkeGuide"
									:finishReason="item.finishReason"
								>
								</YYMessageCqH5>
							</div>
						</div>
						<div class="text-center" :style="{ height: isMobile ? '56px' : '2rem' }" style="position: relative; z-index: 1000; bottom: 8px">
							<w-button
								type="outline"
								:loading="stopChatLoading"
								shape="round"
								class="stopBtn"
								v-if="!isSensitive && dialogueLoading && !chatStore.uploadImgStatus"
								@click="handleclickStopChat()"
							>
								<span style="margin-right: 5px; display: inline-block; width: 11px; height: 11px; background: #1a6dd2"></span>
								停止生成
							</w-button>
						</div>
						<div v-if="!isMobile" :style="{ 'padding-bottom': '60px' }"></div>
					</div>
					<ChatModule ref="chatModuleRef"></ChatModule>
				</div>
				<!-- <div @click="gotoTop" class="scroll-top" v-if="!isMobile" :style="{ right: rightNum }">
					<img :src="sendIcon" alt="" />
				</div> -->
			</w-scrollbar>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, computed, onMounted, watch, onUnmounted, onBeforeMount } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { useThemeConfig } from '/@/stores/themeConfig';
import { storeToRefs } from 'pinia';
import { useRoute } from 'vue-router';
import { getPresetQuestionList } from '/@/api/knowledge';
import { apiGetAuthTokenBySubscriptionNew } from '/@/api/digitalPeople';
import { Message } from 'winbox-ui-next';
const { isMobile, isxl } = useBasicLayout();
const chatStore = useChatStore();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
import { setScrollPosition } from '/@/utils/other';
import sendIcon from '/@/assets/chat/top.svg';
const CenterInitItem = defineAsyncComponent(() => import('./centerInitItem.vue'));
const ChatModule = defineAsyncComponent(() => import('./chatModule/index-cqH5.vue'));

import mittBus from '/@/utils/mitt';

const dialogueLoading = computed(() => chatStore.dialogueLoading);
const loading = computed(() => chatStore.chatLoading);
const chatList = computed(() => chatStore.chatList);
const isSensitive = computed(() => chatStore.isSensitive);
const stopChatLoading = ref(false);
const dialogVisible = ref(false);
import BotAvatar from '/@/assets/chongqing/default-avatar.png';
const route = useRoute();
const maxWidth = ref(1000);
const rightNum = ref('8%');
const curQuestion = ref('');
const chatModuleRef = ref(null);
const policyUrl: any = ref(localStorage.getItem(`${route.params.appId}appId`));
const judicialUrl: any = ref(import.meta.env.VITE_JUDICIAL_QA);
const tjQuestList = ref([]);
const headchoose = ref('1');
const handleclickStopChat = async () => {
	stopChatLoading.value = true;
	await chatStore.clearChat();
	stopChatLoading.value = false;
	chatStore.scrollbarBottom = true;
	setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
};

const facadeImageUrl = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.facadeImageUrl : '';
});

const visible = computed(() => {
	return themeConfig.value.isVisible;
});
const handleClick = () => {
	themeConfig.value.isVisible = true;
};
const handleOk = () => {
	themeConfig.value.isVisible = false;
};
const handleCancel = () => {
	themeConfig.value.isVisible = false;
};
const gotoTop = () => {
	const dom = document.querySelector('.center-side') as HTMLElement;
	dom.scrollTop = 0;
};
const changeHelper = () => {
	dialogVisible.value = true;
};
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo : '';
};

const identityIconUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.identityIcon : '';
};
// 数字人
const signature = ref('');
// const projectId = ref('c42d6a35-884b-11ef-b3d0-bf8debdda611');
// 闻歌数字人
const projectId = ref('0d35d490-ac74-11ef-8ed4-6faaa3762e9b');
const oldClientId = ref('');
let Client = null;
// 数字人
const start = (e, value: string) => {
	console.log('e', e);
	if (!signature.value || !projectId.value) {
		alert('请输入签名和项目ID');
		return;
	}
	Client = new window.RTCInteraction({
		mountClass: 'szr',
		signature: signature.value,
		projectId: projectId.value,
		onError(errorMes) {
			console.log(errorMes);
			showSZR.value = false;
			if (errorMes?.message) {
				Message.warning(errorMes.message);
			}
			Client = null;
			console.log('Clien-----onError', Client);
		},
		onInited(res) {
			console.log('inited', res);
			Client.startRTC();
		},
		onPlayStream(v3Orv4) {
			console.log('v3Orv4', v3Orv4);
		},
		onStopStream() {
			console.log('stop------');
			// Client.endRTC();
		},
		onTalkStart(talkRes) {
			console.log(talkRes, 'onTalkStart');
			mittBus.emit('startTalk', oldClientId.value);
		},
		onTalkEnd(talkRes) {
			console.log(talkRes, 'onTalkEnd');
			// mittBus.emit('onTalkEnd');
			mittBus.emit('onTalkEnd');
		},
	});
};

const showSZR = ref(false);

const showSZRHandler = async () => {
	showSZR.value = !showSZR.value;
	if (showSZR.value) {
		if (!signature.value) {
			await getAuthTokenBySubscriptionNew();
		}
		start();
	} else {
		Client?.endRTC();
	}
};

// 获取数字人token
const getAuthTokenBySubscriptionNew = async () => {
	let res = await apiGetAuthTokenBySubscriptionNew();
	console.log('res=========', res);
	signature.value = res.data.data;
};

// 播报去掉标点符号
const removeMarkdownSymbols = (text) => {
	// 去除加粗文本符号
	text = text.replace(/\*\*(.*?)\*\*/g, '$1');

	// 去除斜体文本符号
	text = text.replace(/[*_](.*?)[*_]/g, '$1');

	// 去除无序列表符号
	text = text.replace(/^\s*[-*]\s+/gm, '');

	// 去除有序列表符号
	text = text.replace(/^\s*\d+\.\s+/gm, '');

	// 去除链接文本符号
	text = text.replace(/\[([^\]]+)\]\([^)]+\)/g, '$1');

	// 去除图片文本符号
	text = text.replace(/!\[([^\]]+)\]\([^)]+\)/g, '');

	// 去除引用块符号
	text = text.replace(/^\s*>/gm, '');

	// 去除代码块符号
	text = text.replace(/```([\s\S]*?)```/g, '');

	// 去除水平分割线符号
	text = text.replace(/^[-_*]{3,}\s*$/gm, '');

	// 去除注释符号
	text = text.replace(/<!--(.*?)-->/gm, '');

	// 去除标题标记（#号）
	text = text.replace(/#+\s*/g, '');

	return text;
};

const sendStreamTextTest = (textList: any) => {
	let count = 0;
	const intervalId = setInterval(() => {
		const ssml = removeMarkdownSymbols(textList[count++]);
		if (count >= textList.length) {
			count = 0;
			Client.talk(ssml, true);
			clearInterval(intervalId);
		}
	}, 200);
};

const newChunks = ref('');

// 页面加载时
onBeforeMount(() => {
	mittBus.on('openHelper', () => {
		changeHelper();
	});
});
watch(
	() => route.params.conversationId,
	() => {
		if (route.params.conversationId && route.params.conversationId != '' && isMobile) {
			themeConfig.value.isVisible = false;
		}
	},
	{ immediate: true }
);
watch(
	() => route.params.appId,
	() => {
		if (route.params.appId && route.params.appId != '' && isMobile) {
			themeConfig.value.isCollapse = false;
		}
	},
	{ immediate: true }
);
watch(
	route,
	() => {
		//切换路由清空会话记录
		chatStore.chatList = [];
		chatStore.dialogueLoading = false;
	},
	{ immediate: true }
);
const heightSide = computed(() => {
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	let browserList = ['MQQBrowser', 'Opera', 'Edg', 'Firefox', 'Chrome', 'IEMobile', 'wOSBrowser', 'BlackBerry', 'BrowserNG', 'WebOS'];
	let height = 'calc(100% - 98px)';
	if (isMobile.value) {
		//判断是否Safari浏览器
		if (userAgent.indexOf('Safari') > -1 && browserList.every((item) => userAgent.indexOf(item) == -1)) {
			height = 'calc(100% - 98px)';
		} else if (userAgent.indexOf('MicroMessenger') > -1 && userAgent.indexOf('wxwork') == -1) {
			height = 'calc(100% - 68px)';
		} else if (userAgent.indexOf('MicroMessenger') > -1 && userAgent.indexOf('wxwork') > -1) {
			height = 'calc(100% - 98px)';
		} else if (browserList.some((item) => userAgent.indexOf(item) == -1)) {
			height = 'calc(100% - 90px)';
		} else {
			height = 'calc(100% - 98px)';
		}
	}
	return height;
});
const userAgent = computed(() => {
	return navigator.userAgent;
});
const handleScroll = (event) => {
	const dom = document.querySelector('.center-side');
	// const dom = event
	const clientHeight = dom.clientHeight;
	const scrollTop = dom.scrollTop;
	const scrollHeight = dom.scrollHeight;
	if (clientHeight + scrollTop + 15 >= scrollHeight) {
		chatStore.scrollbarBottom = true;
	} else {
		chatStore.scrollbarBottom = false;
	}
};
const setHeight = ref(0);
const curRouteId = ref('');
onMounted(async () => {
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
	// chatStore.getFeedbackList();
	resizeScrollHeight();
	resizeMaxWidth();
	updateWindowHeight();
	getPresetQuestionListFun();
	const dom = document.querySelector('.center-side');
	dom.addEventListener('scroll', handleScroll);
	window.addEventListener('resize', resizeScrollHeight);
	window.addEventListener('resize', resizeMaxWidth);
	window.addEventListener('resize', updateWindowHeight);

	if (showSZR.value) {
		await getAuthTokenBySubscriptionNew();
		nextTick(() => {
			start();
		});
	}

	if (showSZR.value && Client) {
		mittBus.on('szrPlayStartH5', ({ chunksList, clientId }) => {
			if (oldClientId.value !== clientId) {
				if (oldClientId) {
					newChunks.value = [];
					// 打断
					Client?.breakTalking();
				}
				oldClientId.value = clientId;
			}

			if (!newChunks.value.length || chunksList.length > newChunks.value.length) {
				newChunks.value = chunksList;
				sendStreamTextTest(newChunks.value);
			}
		});
		mittBus.on('stopTalk', () => {
			Client?.breakTalking();
			newChunks.value = [];
			oldClientId.value = '';
		});
	}
});

window.addEventListener('beforeunload', function (event) {
	if (Client) {
		// 在页面即将刷新或关闭时执行的操作
		Client.endRTC();
	}
});

//
const getPresetQuestionListFun = async () => {
	let res = await getPresetQuestionList({
		pageNo: 1,
		pageSize: 4,
		type: '推荐问题',
		status: 1,
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
	});
	tjQuestList.value = res.data.records;
};

const sendQuestion = async (item: string) => {
	curQuestion.value = item;
	console.log(curQuestion.value);

	if (chatStore.dialogueLoading) return;
	if (!route.params.conversationId) {
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
watch(
	() => chatStore.isOpen,
	() => {
		resizeMaxWidth();
	},
	{
		deep: true,
	}
);
watch(
	() => chatStore.collapsedMeun,
	() => {
		resizeMaxWidth();
	},
	{
		deep: true,
	}
);
const resizeScrollHeight = () => {
	let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
	let h = isMobile.value ? clientHeight - 64 - 36 : clientHeight - 64;
	setHeight.value = h;
};
const updateWindowHeight = () => {
	let windowHeight = window.innerHeight;
	let html = document.querySelector(':root') as any;
	if (windowHeight > 0 && html) {
		html.style.setProperty('--i-window-height', `${windowHeight}px`);
	}
};
const resizeMaxWidth = () => {
	let clientWidth = document.documentElement.clientWidth || document.body.clientWidth;
	let num = 0;
	if (chatStore.isOpen) {
		num = chatStore.rightFlag == 1 ? 300 : 440;
	} else {
		num = 0;
	}

	let w = chatStore.collapsedMeun ? clientWidth - num - 80 : clientWidth - num - 200;
	maxWidth.value = w > 1000 ? 1000 : w;
	rightNum.value = w > 1000 ? '8%' : '2.5%';
	let curW = (clientWidth - 1650) / 2 - 40;
	rightNum.value = clientWidth > 1650 && curW > 0 ? curW + 'px' : '2.5%';
};

onUnmounted(() => {
	mittBus.off('openHelper', () => {});
	if (Client) {
		Client.endRTC();
	}
	try {
		const dom = document.querySelector('.center-side');
		dom.removeEventListener('scroll', handleScroll);
	} catch (error) {}
});
</script>

<style scoped lang="scss">
@import '/@/theme/mixins/index.scss';
.cq-head {
	padding: 16px 8px 0;
	width: 100vw;
	height: 175px;
	background-image: linear-gradient(180deg, #D9EDF9 0%, #F2F6FA 60%);

	&-box {
		position: relative;
		padding-right: 16px;
		width: 100%;
		height: 159px;
		background: url('/@/assets/chongqing/bg.png');
		background-size: 100%;

		&-left {
			position: absolute;
			left: 0;
			bottom: 0;
			width: 119px;
			height: 175px;
		}

		&-right {
			margin-left: 119px;
			padding: 18px 0;
			.img1 {
				height: 48px;
				margin-bottom: 10px;
			}
			.title1 {
				width: 172px;
				height: 48px;
				font-family: DOUYINSANSBOLD, DOUYINSANSBOLD;
				font-weight: normal;
				font-size: 20px;
				color: #2e394f;
				line-height: 24px;
				text-align: left;
				font-style: normal;
				margin-bottom: 10px;
			}
			.title2 {
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 14px;
				color: #2e394f;
				line-height: 20px;
				display: -webkit-box;
				-webkit-line-clamp: 3; /* 指定行数 */
				-webkit-box-orient: vertical;
				overflow: hidden;
			}
		}
	}
}
:deep(.headerIconDialog) {
	height: 250px;
	.el-dialog__header {
		display: none;
	}
	.title {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 20px;
		color: #494c4f !important;
		line-height: 27px;
		text-align: center;
		font-style: normal;
	}
	.el-radio__label {
		border-radius: 50%;
		width: 100px;
		height: 100px;
		text-align: center;
		position: relative;
		border: 2px solid transparent;
	}
	.el-radio__input.is-checked + .el-radio__label {
		border: 2px solid #3976f6;
	}
	.el-radio__input {
		display: none;
	}
}

.containersMobile {
	height: calc(var(--i-window-height) - 134px);
}

.fade {
	position: absolute;
	height: 270px;
	width: calc(100% - 5px);
	z-index: 1;
	overflow: hidden;
	clip-path: inset(0px);
	pointer-events: none;

	&.bottom {
		bottom: 0;
	}

	.background {
		height: 100%;
		transition-property: transform;
		transition-duration: 1000ms;
		transition-timing-function: cubic-bezier(0.75, 0, 0.25, 1);

		.image {
			height: 100%;
			width: 100%;
			//background-image: url('../../../assets/chat/bg.png');
			background: #f4f6f9;
			// filter: blur(30px);
			-webkit-mask-image: -webkit-gradient(linear, 0 181, 0 0, from(rgba(0, 0, 0, 1)), color-stop(80%, rgba(0, 0, 0, 1)), to(rgba(0, 0, 0, 0)));
		}
	}
}

.stopBtn {
	background: #fff;
	border: none;
	border-radius: 8px;
	color: #1a6dd2;
	cursor: pointer;

	&:hover {
		background: rgba(53, 94, 255, 0.06);
		color: #9a99aa;
	}
}

.stopBtn.w-btn-loading {
	border: none;
	background: #fff;
	border-radius: 8px;
	color: #9a99aa;
}

.SecondaryTitle {
	display: flex;
	align-items: center;
	justify-content: space-between;

	:deep(.w-drawer-body) {
		padding: 0;
	}
}

.isSensitiveLine {
	width: 100%;
	height: 50px;
	position: relative;
	padding-left: 36px;
	padding-right: 36px;
	margin-top: 15px;

	.line {
		width: calc((100% - 284px) / 2);
		display: inline-block;
		border-top: 1px solid #e4e8ee;
		height: 5px;
	}

	.text {
		display: inline-block;
		width: 260px;
		height: 20px;
		margin: 0 12px;
		color: #9a99aa;
		@include add-size($font-size-base14, $size);
	}
}

.center-side-parent {
	flex: 1;
	height: 100%;

	.container-center {
		height: 100%;
		width: 100%;
		position: relative;
		// background-image: url('/src/assets/chongqing/digitalPeople.png');
		// background-size: 100% 100%;
	}

	.pt208 {
		padding-top: 208px;
	}

	.pt42 {
		margin-top: 42px;
	}

	:deep(.w-scrollbar) {
		height: 100%;
	}

	:deep(.w-scrollbar-container) {
		position: relative;
		width: 100%;
		height: 100%;
	}

	.center-side1 {
		position: relative;
	}
}

.fontSize16 {
	@include add-size($font-size-base16, $size);
}

.center-side {
	display: flex;
	flex-direction: column;
}
.height1 {
	height: calc(100% - 268px);
}
.height2 {
	height: calc(100% - 85px);
}
</style>
<style lang="scss">
@import '/@/theme/mixins/index.scss';

.secondaryTitleDrawer {
	:deep(.w-drawer-body) {
		padding: 0;
	}
}

.maxWidth800 .message-list .adaption {
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	align-items: flex-start;
	position: relative;
	height: 65px;

	> div:nth-child(2) {
		position: absolute;
		top: 35px;
		right: 12px;
	}
}

.box {
	// border-radius: 8px;
	width: 100%;
	// padding: 20px;
	padding-top: 16px;

	overflow: hidden;
	// background: url('/@/assets/zc/card.png') #f9f9f9 no-repeat;
	// background-size: cover;
}

.quest-list-mobile {
	width: 316px;
	background: rgba(255, 255, 255, 0.9);
	box-shadow: 0px 4px 8px 0px rgba(13, 28, 71, 0.08);
	backdrop-filter: blur(1px);
	padding: 12px 16px;
	border-radius: 8px;
	position: relative;
	.triangle {
		position: absolute;
		top: -8px;
		left: 30px;
		width: 0;
		height: 0;
		border-top: 10px solid #fff;
		border-right: 10px solid transparent;
		border-left: 10px solid transparent;
		border-bottom: 10px solid transparent;
		transform: rotate(-40deg);
	}
	.item {
		font-size: 14px;
		padding: 8px 13px;
		margin-bottom: 8px;
		@include add-size(16px, $size);
		border-radius: 6px;
		cursor: pointer;
		font-family: MiSans, MiSans;
		color: #181b49;
		display: flex;
		align-items: center;
		background: #edf1ff;
	}
}

.question-box {
	display: flex;
	// height: 144px;
	margin: 30px;
	background: url('/@/assets/zc/wdzs-bg.png') #f9f9f9 no-repeat;
	background-size: cover;
	border-radius: 12px;
	padding: 18px 18px 18px 141px;
	display: flex;
	position: relative;

	.box {
		padding: 0;

		.title {
			@include add-size($font-size-base16, $size);
			font-family: MicrosoftYaHei;
			color: #010101;
			margin-bottom: 8px;
		}
	}

	.jqr-icon {
		position: absolute;
		left: 8px;
		bottom: 0;
	}

	.quest-list {
		display: flex;
		align-items: center;
		justify-content: flex-start;
		flex-wrap: nowrap;

		.item {
			flex: 1;
			padding: 0;
			margin-right: 16px;

			.box {
				@include add-size($font-size-base14, $size);
				font-family: MicrosoftYaHei;
				color: #010101;
				padding: 6px 15px;
				background: #fff;
				cursor: pointer;
				border-radius: 12px;
				font-size: 15px;
				display: flex;
				justify-content: space-between;
				align-items: center;

				img {
					display: none;
				}
			}
		}

		.item.active,
		.item:hover {
			.box {
				box-shadow: 0px 5px 8px 0px rgba(45, 55, 85, 0.1);
				font-family: MicrosoftYaHei;
				color: #4085f4;

				img {
					display: block;
				}
			}
		}

		.item:last-child {
			margin-right: 0;
		}
	}

	.tip {
		padding: 2px 6px;
		background: rgba(64, 133, 244, 0.1);
		border-radius: 6px 6px 6px 2px;
		font-family: MicrosoftYaHei;
		font-size: 14px;
		color: #4085f4;
		margin-left: 6px;
	}
}

.centerMobile {
	background: #f0f6fc;
	.message-list {
		height: calc(100%);
		overflow: scroll;
		display: flex;
		flex-direction: column;
		// justify-content: flex-end;
		z-index: 9999;

		// .box {
		// 	border-radius: 8px;
		// 	width: 100%;
		// 	padding: 20px;
		// 	overflow: hidden;
		// 	background: url('/@/assets/zc/card.png') #f9f9f9 no-repeat;
		// 	background-size: cover;
		// }
		.title {
			height: 40px;
			line-height: 40px;
			font-weight: 500;
			color: #008dd6ff;
			font-size: 16px;
			// display: flex;
			// align-items: center;
			// justify-content: flex-start;
			// margin-bottom: 14px;
		}
	}
	.message-list-one {
		justify-content: flex-start;
	}
}

.message-request {
	// background: linear-gradient(180deg, rgba(53, 127, 255, 0.2) 0%, rgba(53, 127, 255, 0) 30px), rgba(255, 255, 255, 0.7);
	// border-radius: 12px 12px 12px 0px;
	// border: 1px solid;
	// border-image: linear-gradient(180deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0)) 1 1;
	// font-family: MiSans, MiSans;
	// font-weight: 400;
	// font-size: 16px;
	// color: #383d47;
	// line-height: 24px;
}
.left-answer {
	background: url('/@/assets/chongqing/left-bottom.svg') no-repeat center bottom;
	background-size: 100% auto;
	padding: 0 9px 36px!important;
	// position: relative;
	margin-bottom: 16px;
	.inner {
		background: #fff;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #2e394f;
		line-height: 24px;
		padding: 8px 8px;
		border-radius: 0 0 8px 8px;
		min-height: 48px;
		min-width: 288px;

		// .left-top {
		// 	width: 100%;
		// 	position: absolute;
		// 	left: 0;
		// 	top: 0;
		// }
	}
	.left-top {
		margin-bottom: -1px;
	}
}
</style>
