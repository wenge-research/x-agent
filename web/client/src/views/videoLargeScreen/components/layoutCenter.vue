<template>
	<div class="relative flex flex-col w-full h-full" :class="{ centerMobile: isMobile }">
		<div class="videoLargeScreen-layoutCenter center-side-parent" id="bg-main" :style="`height: 100%;`">
			<ChatModule ref="chatModuleRef"></ChatModule>
			<w-scrollbar ref="layoutScrollbarRef" class="center-side1">
				<div class="container-center-videoLargeScreen">
					<div style="" :style="{ overflow: isMobile ? 'hidden' : 'scroll', height: heightSide }" class="center-side" v-if="!loading">
						<div
							class="message-list"
							:style="!isSensitive && dialogueLoading && !chatStore.uploadImgStatus ? 'height:calc(100% - 60px);' : 'height:calc(100% - 16px);'"
						>
							<div v-if="curRouteId == policyUrl && isMobile">
								<div v-if="!chatList.length" class="videoLargeScreen-layoutCenter-prologue" style="display: flex" :style="{ padding: isMobile ? '' : ' 0 30px' }">
									<!-- <w-avatar v-if="getAppDetail()?.robotIcon" class="robotIcon" :size="36"><img :src="getAppDetail()?.robotIcon" /></w-avatar> -->
									<div class="txt message-request message-request-new">
										{{ getAppDetail()?.prologue || '您好，很高兴为您服务！请问有什么可以帮您的？' }}
									</div>
								</div>
							
							</div>
							<div :style="{ padding: isMobile ? '' : ' 0 30px' }" v-for="(item, index) of chatList" :key="index">
								<YYMessageVideoLargeScreen
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
									:finishReason="item.finishReason"
								></YYMessageVideoLargeScreen>
								<YYMessageVideoLargeScreen
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
									:finishReason="item.finishReason"
								></YYMessageVideoLargeScreen>
							</div>
						</div>
						<!-- <div
							class="text-center"
							:style="{ height: isMobile ? '56px' : '2rem' }"
							style="position: relative; z-index: 1000; bottom: 8px; display: flex; align-items: center; justify-content: center"
						>
							<w-button
								type="outline"
								:loading="stopChatLoading"
								shape="round"
								class="stopBtn"
								v-if="!isSensitive && dialogueLoading && !chatStore.uploadImgStatus"
								@click="handleclickStopChat()"
							>
								<span style="margin-right: 5px; display: inline-block; width: 11px; height: 11px; background: #4888ef"></span>
								停止生成
							</w-button>
						</div> -->
						<div v-if="!isMobile" :style="{ 'padding-bottom': '60px' }"></div>
					</div>
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
// const { isMobile, isxl } = useBasicLayout();
const isMobile = ref(true);
const chatStore = useChatStore();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
import { setScrollPosition } from '/@/utils/other';
import sendIcon from '/@/assets/chat/top.svg';
const ChatModule = defineAsyncComponent(() => import('./chatModule/index.vue'));

import mittBus from '/@/utils/mitt';

const dialogueLoading = computed(() => chatStore.dialogueLoading);
const loading = computed(() => chatStore.chatLoading);
const chatList = computed(() => chatStore.chatList);
const isSensitive = computed(() => chatStore.isSensitive);
const stopChatLoading = ref(false);
const dialogVisible = ref(false);
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
	// setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
};

const facadeImageUrl = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.facadeImageUrl : '';
});
const virtualHumanLogo = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.virtualHumanLogo : '';
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
	let height = 'calc(100% - 188px)';
	// if (isMobile.value) {
	// 	//判断是否Safari浏览器
	// 	if (userAgent.indexOf('Safari') > -1 && browserList.every((item) => userAgent.indexOf(item) == -1)) {
	// 		height = 'calc(100% - 134px)';
	// 	} else if (userAgent.indexOf('MicroMessenger') > -1 && userAgent.indexOf('wxwork') == -1) {
	// 		height = 'calc(100% - 68px)';
	// 	} else if (userAgent.indexOf('MicroMessenger') > -1 && userAgent.indexOf('wxwork') > -1) {
	// 		height = 'calc(100% - 98px)';
	// 	} else if (browserList.some((item) => userAgent.indexOf(item) == -1)) {
	// 		height = 'calc(100% - 90px)';
	// 	} else {
	// 		height = 'calc(100% - 98px)';
	// 	}
	// }
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
	chatList.value.forEach((item1, index1) => {
		chatStore.tempFileList.forEach((item2, index2) => {
			if (index1 == index2) {
				item1.fileList = item2;
			}
		});
	});
	console.log(chatList.value, '我来看看啊啊啊');
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
	console.log(Object.keys(route.query).length);
	if (Object.keys(route.query).length != 0) {
		const id = route.query;
		apiGetShare(id.key).then((res) => {
			console.log(res.data);
			chatStore.addChatList(res.data);
			console.log(chatList.value);
		});
	}
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
	if (!route.params.conversationId) {
		await chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
		setTimeout(() => {
			console.log(2, '关心客服h5');
			const data: any = {
				content: item,
				conversationId: route.params.conversationId,
				knowledgeBaseId: route.params.appId,
				appId: route.params.appId,
			};
			chatStore.setChatList(data);
			return;
		}, 100);
	} else {
		const data: any = {
			content: item,
			conversationId: route.params.conversationId,
			knowledgeBaseId: route.params.appId,
			appId: route.params.appId,
		};
		chatStore.setChatList(data);
		return;
	}
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
	try {
		const dom = document.querySelector('.center-side');
		dom.removeEventListener('scroll', handleScroll);
	} catch (error) {}
});
</script>

<style scoped lang="scss">
@import '/@/theme/mixins/index.scss';
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
	color: #4888ef;
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

.videoLargeScreen-layoutCenter {
	flex: 1;
	height: 100%;
	padding-top: 6%;

	&-header {
		height: 144px;
		display: flex;
		flex-direction: column;
		margin-bottom: 16px;
		background: #ffffff;
		border-radius: 8px;
		.top {
			height: 72px;
			display: flex;
			align-items: center;
			.title {
				margin-left: 12px;
				&-text {
					font-family: MiSans, MiSans;
					font-weight: 500;
					font-size: 18px;
					color: #000000;
					line-height: 24px;
					text-align: left;
					font-style: normal;
				}
			}
		}
		.message {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 14px;
			color: #3f4247;
			line-height: 20px;
			text-align: justify;
			font-style: normal;
		}
	}
	&-prologue {
		.robotIcon {
			margin-right: 8px;
		}
		.txt {
			flex: 1;
			padding: 12px 16px;
			// background: rgba(23, 23, 23, 0.04);
			background-color: #fff;
			border-radius: 2px 8px 8px 8px;
			font-family: MiSans, MiSans;
			font-weight: 400;
			@include add-size($font-size-base16, $size);
			color: #3f4247;
			line-height: 24px;
		}
		.message-request-new {
			margin-right: 64px;
			background: rgba(16, 67, 148, 0.7);
			border: 2px solid #34bbff;
			backdrop-filter: blur(10px);
			padding: 32px;
			font-family: MiSans, MiSans;
			font-size: 40px!important;
			color: #ffffff;
			line-height: 62px;
			border-radius: 0 24px 24px 24px;
		}
	}
	&-tjQuestList {
		margin-top: 16px;
		.label {
			font-family: MiSans, MiSans;
			font-weight: 500;
			font-size: 14px;
			color: #3f4247;
			line-height: 20px;
			text-align: justify;
			font-style: normal;
		}
		.question {
			display: inline-block;
			padding: 10px 16px;
			margin-top: 12px;
			margin-right: 12px;
			background: #ffffff;
			border-radius: 8px;
			border: 1px solid #d7dae0;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 14px;
			color: #3f4247;
			line-height: 20px;
		}
	}

	.container-center-videoLargeScreen {
		padding: 27vh 56px 0;
		height: 100%;
		width: 100%;
		position: relative;
		// background: #F2FAF4 ;
		// background-image:url('/src/assets/chatTheme/layoutBg.png');
		background-repeat: no-repeat;
		background-size: 100% 100%;
	}

	:deep(.w-scrollbar) {
		height: calc(100% - 96px);
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

// .videoLargeScreen-layoutCenter-prologue .txt {
// 	background-color: #fff;
// }
::v-deep .text-message-reply-left {
	background: #fff;
}
::v-deep .yy-message-videoLargeScreen .btns {
	background-color: #fff;
	margin-top: -4px;
	border-radius: 0 0 8px 8px;
	padding: 0 0 12px 0;
	:first-child {
		margin-left: 6px;
	}
}
.fontSize16 {
	@include add-size($font-size-base16, $size);
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
	// background: url('/@/assets/zc/wdzs-bg.png') #f9f9f9 no-repeat;
	// background-size: cover;
	// border-radius: 12px;
	border-radius: 8px;
	opacity: 0.5;
	backdrop-filter: blur(8px);
	padding: 16px 12px 0 12px;
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
	.message-list {
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
			font-family: MiSans, MiSans;
			font-weight: 500;
			font-size: 14px;
			color: #797f8a;
			.arrow {
				position: relative;
				left: 2px;
				top: 6px;
				&.down {
					transform: rotate(180deg);
				}
			}
		}
		.relate-source {
			position: relative;
			margin: 0 0 10px 0;
			ul {
				position: relative;
				li {
					position: relative;
					a {
						display: block;
						white-space: nowrap; // 防止文本换行
						overflow: hidden; // 超出部分隐藏
						text-overflow: ellipsis; // 超出部分用省略号表示
						width: calc(100vw - 90px);
						font-family: MiSans, MiSans;
						font-weight: 400;
						font-size: 14px;
						color: #34a4ff;
						line-height: 20px;
						margin: 16px 0;
					}
				}
			}
		}
	}
}
</style>
