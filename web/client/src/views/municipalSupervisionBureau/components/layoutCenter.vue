<template>
	<div class="municipalSupervisionBureau relative flex flex-col h-full w-full" :class="{ centerMobile: isMobile }">
		<div class="center-side-parent" id="bg-main" :style="`height: 100%;`">
			<w-scrollbar ref="layoutScrollbarRef" class="center-side1">
				<div class="container-center">
					<div style="" :style="{ overflow: isMobile ? 'hidden' : 'scroll', height: heightSide }" class="center-side" v-if="!loading">
						<CenterInitItem v-if="curRouteId == judicialUrl"></CenterInitItem>

						<div class="message-list">
							<div class="municipalSupervisionBureau-head" v-if="curRouteId == policyUrl && !isMobile">
								<div class="title">
									<img v-if="getAppDetail()?.identityIcon?.includes('.png')" :src="getAppDetail()?.identityIcon" style="height: 26px" />
									<div v-else class="title-text">{{ getAppDetail()?.identityIcon }}</div>
								</div>
								<div class="message">{{ getAppDetail()?.greeting }}</div>
							</div>
							<!-- <div
								style="
									background: linear-gradient(180deg, #154df5 0%, #0076ff 22%, rgba(0, 160, 255, 0) 100%),
										radial-gradient(0% 100% at 78% -2%, #07fbff 0%, rgba(57, 118, 246, 0) 100%);
									border-radius: 12px 12px 0px 0px;
									display: flex;
									margin-bottom: 24px;
									padding: 12px;
								"
								v-if="curRouteId == policyUrl && isMobile"
							>
								<div class="fontSize16 text-black min-w-[20px] text-box-1716763096571080706 px-4 py-2 message-request text-black border-right">
									<div style="display: flex; justify-content: space-between">
										<div style="width: calc(100% - 30px)">
											<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px">
												<p
													style="
														font-family: MiSans, MiSans;
														font-weight: 500;
														font-size: 15px;
														color: #ffffff;
														line-height: 24px;
														text-align: justify;
														font-style: normal;
													"
												>
													您好！我是{{ getAppDetail()?.applicationName }}
												</p>
											</div>
											<div style="color: #fff; font-size: 12px; line-height: 19px; margin-bottom: 36px">{{ getAppDetail()?.greeting }}</div>
										</div>
										<div style="width: 90px; position: relative">
											<img
												src="/src/assets/chatImages/woman.png"
												style="width: 110px; max-width: 110px; position: absolute; top: -26px; right: -22px"
											/>
											<img
												src="/src/assets/chatImages/man.png"
												style="width: 110px; max-width: 110px; position: absolute; top: -26px; right: -22px"
												v-if="false"
											/>
											<span
												style="
													cursor: pointer;
													position: absolute;
													top: 94px;
													right: -24px;
													z-index: 100;
													display: inline-block;
													width: 78px;
													height: 24px;
													background: #3976f6;
													border-radius: 12px 0px 0px 12px;
												"
											>
												<img src="/src/assets/chatImages/shuaxin.svg" style="width: 14px; position: absolute; left: 6px; top: 4px" />
												<span style="color: #fff; font-size: 12px; position: absolute; left: 26px; top: 3px" @click="changeModel">更换助手</span>
											</span>
										</div>
									</div>
									<el-dialog v-model="dialogVisible" width="100%" top="0vh" class="headerIconDialog">
										<p class="title">更换助手</p>
										<div style="margin-top: 70px; text-align: center">
											<el-radio-group v-model="headchoose">
												<el-radio value="1" size="large">
													<img
														src="/src/assets/chatImages/manchat.png"
														style="width: 80px; height: 80px; border-radius: 50%; position: absolute; top: 9px"
													/>
													<img src="/src/assets/chatImages/men-line.svg" style="width: 16px; position: absolute; top: 115px; left: 40px" />
												</el-radio>
												<el-radio value="2" size="large">
													<img
														src="/src/assets/chatImages/womanchat.png"
														style="width: 80px; height: 80px; border-radius: 50%; position: absolute; top: 9px"
													/>
													<img src="/src/assets/chatImages/women-line.svg" style="width: 16px; position: absolute; top: 115px; left: 40px" />
												</el-radio>
											</el-radio-group>
										</div>
									</el-dialog>
									<div class="quest-list-mobile">
										<div class="triangle"></div>
										<div class="item" v-for="(item, index) in tjQuestList" @click="sendQuestion(item.question)" :key="index">
											<img style="height: 18px; margin-right: 12px" src="/src/assets/zc/send.svg" alt="" />
											<div>{{ item.question }}</div>
										</div>
									</div>
								</div>
							</div> -->

							<div class="quest-list">
								<div class="quest-list-title">您可以这么问我</div>
								<div v-for="(item, index) in tjQuestList" :key="index" class="quest-list-box">
									<div class="item" @click="sendQuestion(item.question)" :class="{ active: curQuestion == item.question }">
										<span style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap">{{ item.question }}</span>
										<iconpark-icon name="send-plane-fill" size="16" color="#1477E3"></iconpark-icon>
									</div>
								</div>
							</div>

							<!-- <div style="display: flex" :style="{ padding: isMobile ? ' 0 10px' : ' 0 30px' }" v-if="curRouteId == policyUrl">
								<w-avatar v-if="!isMobile" style="margin-right: 8px" :size="28"><img :src="getAppDetail()?.robotIcon || BotAvatar" /></w-avatar>
								<div
									style="border: 1px solid #fff"
									class="fontSize16 text-black min-w-[20px] text-box-1716763096571080706 px-4 py-2 bg-[#fff] dark:bg-[#fff] message-request text-black border-left"
								>
									{{ getAppDetail()?.prologue || '您好，很高兴为您服务！请问有什么可以帮您的？' }}
								</div>
							</div> -->
							<div :style="{ padding: isMobile ? ' 0 10px' : ' 0' }" v-for="(item, index) of chatList" :key="index">
								<YYMessageSAMR
									:answer="item.answer"
									:dialogueId="item.dialogueId"
									:businessScenarioLists="item.businessScenarioLists"
									:businessScenario="item.businessScenario"
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
									:matterGuide="item?.matterGuide"
									:finishReason="item.finishReason"
									@handleFillForm="handleFillForm(item?.matterGuide)"
								></YYMessageSAMR>
								<YYMessageSAMR
									:answer="item.answer"
									:dialogueId="item.dialogueId"
									:businessScenarioLists="item.businessScenarioLists"
									:businessScenario="item.businessScenario"
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
									:matterGuide="item?.matterGuide"
									:finishReason="item.finishReason"
									:clientId="item.clientId" 
									@handleFillForm="handleFillForm(item?.matterGuide)"
								>
								</YYMessageSAMR>
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
								<template #icon>
									<!-- <CoolRemoveCircleLineWe size="17" style="vertical-align: sub" /> -->
									<!-- <img src="/src/assets/zc/stop-new.png" alt="" /> -->
									<iconpark-icon name="stop-fill" size="16" color="#2065D6"></iconpark-icon>
								</template>
								停止生成
							</w-button>
						</div>
						<div v-if="!isMobile" :style="{ 'padding-bottom': '60px' }"></div>
					</div>
					<ChatModule ref="chatModuleRef"></ChatModule>

					<w-popover placement="top" width="460" trigger="hover" content-class="disclaimer-popover">
						<div class="disclaimer">
							<iconpark-icon class="no" name="information-line" color="#828894" size="14" style="margin-right: 4px"></iconpark-icon>
							<iconpark-icon class="yes" name="information-line" color="#2065D6" size="14" style="margin-right: 4px"></iconpark-icon>
							免责声明
						</div>
						<template #content>
							<div class="disclaimer-title">免责声明</div>
							<div v-html="getmzsm()" class="disclaimer-txt"></div>
							<!-- <el-input type="textarea" v-model="disclaimer" disabled autosize /> -->
						</template>
					</w-popover>
				</div>
				<!-- <div @click="gotoTop" class="scroll-top" v-if="!isMobile" :style="{ right: rightNum }">
					<img :src="sendIcon" alt="" />
				</div> -->
			</w-scrollbar>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, computed, onMounted, watch, onUnmounted } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { useThemeConfig } from '/@/stores/themeConfig';
import { storeToRefs } from 'pinia';
import { useRoute } from 'vue-router';
import { getPresetQuestionList } from '/@/api/knowledge';
const { isMobile, isxl } = useBasicLayout();
const chatStore = useChatStore();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
import { setScrollPosition } from '/@/utils/other';
import sendIcon from '/@/assets/chat/top.svg';
const CenterInitItem = defineAsyncComponent(() => import('./centerInitItem.vue'));
const ChatModule = defineAsyncComponent(() => import('./chatModule/index.vue'));
const dialogueLoading = computed(() => chatStore.dialogueLoading);
const loading = computed(() => chatStore.chatLoading);
const chatList = computed(() => chatStore.chatList);
const isSensitive = computed(() => chatStore.isSensitive);
const stopChatLoading = ref(false);
const dialogVisible = ref(false);
import BotAvatar from '/@/assets/zc/jqr-new.png';
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
const changeModel = () => {
	dialogVisible.value = true;
};
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo : '';
};
const getmzsm = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	let disclaimer = appInfo ? appInfo.disclaimer : '暂无数据';
	return disclaimer.replace(/\n/g, '<br /><p style="margin-bottom: 6px"></p>');
};

const identityIconUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.identityIcon : '';
};

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
			height = 'calc(100% - 134px)';
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
onMounted(() => {
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
	// if (!route.params.conversationId) {
	// 	await chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
	// }
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
	try {
		const dom = document.querySelector('.center-side');
		dom.removeEventListener('scroll', handleScroll);
	} catch (error) {}
});
const emit = defineEmits(['handleFillForm']);
const handleFillForm = (info) => {
	emit('handleFillForm', info);
};
</script>

<style scoped lang="scss">
@import '/@/theme/mixins/index.scss';
.center-side {
	&::-webkit-scrollbar {
		display: none;
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
	color: #2065d6;
	cursor: pointer;

	img {
		width: 17px;
		height: 17px;
	}

	&:hover {
		background: rgba(53, 94, 255, 0.06);
		color: #2065d6;
	}
}

.stopBtn.w-btn-loading {
	border: none;
	background: #fff;
	border-radius: 8px;
	color: #2065d6;
}
:deep(.w-btn-icon) {
	display: flex;
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
		// background: #F3F6FB;
		// background: linear-gradient(180deg, rgba(246, 250, 248, 0.8) 0%, rgba(233, 242, 239, 0.8) 100%);
		border-radius: 16px;
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
</style>
<style lang="scss">
@import '/@/theme/mixins/index.scss';

.disclaimer-popover {
	border-radius: 8px;

	.w-popover-content {
		padding: 16px 24px;
		width: 460px;
		background: #ffffff;
		box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
		border: 1px solid rgba(0, 0, 0, 0.12);
		border-radius: 8px;
	}
}

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

.municipalSupervisionBureau {
	&-head {
		background: #2065d6;
		border-radius: 16px;
		padding: 24px;
	}

	.message {
		margin-top: 8px;
		font-weight: 400;
		@include add-size($font-size-base16, $size);
		color: #ffffff;
		line-height: 24px;
	}

	.title {
		font-weight: bold;
		@include add-size($font-size-base20, $size);
		color: #ffffff;
		line-height: 26px;
		font-family: MiSans, MiSans;
		font-weight: 500;

		span {
			display: inline-block;
			height: 20px;
			min-width: 85px;
			line-height: 20px;
			font-weight: bold;
			font-size: 13px;
			color: #169e9a;
			padding: 0 10px;
			background: #c2e2de;
			border-radius: 12px;
			margin-left: 10px;
		}
	}

	.jqr-icon {
		position: absolute;
		left: 24px;
		bottom: 0;
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

.quest-list {
	padding-top: 22px;
	margin-left: 24px;

	&-title {
		font-family: MiSans, MiSans;
		font-weight: 500;
		@include add-size($font-size-base14, $size);
		color: #383d47;
		line-height: 20px;
	}

	.item {
		margin-top: 12px;
		@include add-size($font-size-base16, $size);
		display: inline-block;
		position: relative;
		font-family: MiSans, MiSans;
		color: #646479;
		padding: 10px 16px;
		background: rgba(255, 255, 255, 0.7);
		border-radius: 2px 16px 16px 16px;
		cursor: pointer;

		iconpark-icon {
			display: none;
		}
	}
	.item:hover {
		background: #ffffff;
		box-shadow: 0px 5px 8px 0px rgba(45, 55, 85, 0.1);
		border-radius: 2px 16px 16px 16px;
		color: #2065d6;
		padding: 10px 42px 10px 16px;
		iconpark-icon {
			display: block;
			position: absolute;
			top: 13px;
			right: 16px;
		}
	}
}

.centerMobile {
	.message-list {
		height: calc(100% - 60px);
		overflow: scroll;

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
}

.disclaimer {
	position: absolute;
	left: 50%;
	transform: translateX(-50%);
	bottom: 8px;
	width: 84px;
	height: 24px;
	background: transparent;
	border-radius: 12px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-family: MiSans, MiSans;
	font-weight: 400;
	font-size: 14px;
	color: #828894;
	line-height: 12px;
	cursor: pointer;

	.no {
		display: block;
	}
	.yes {
		display: none;
	}

	&:hover {
		color: #2065d6;
		background: rgba(32, 101, 214, 0.1);
		.no {
			display: none;
		}
		.yes {
			display: block;
		}
	}

	&-title {
		height: 24px;
		font-family: MiSans, MiSans;
		font-weight: 600;
		font-size: 18px;
		color: #383d47;
		line-height: 24px;
		margin-bottom: 12px;
	}

	&-txt {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 14px;
		color: #828894;
		line-height: 22px;
		text-align: justify;
		font-style: normal;
		max-height: 460px;
		overflow-y: auto;
	}
}
</style>
