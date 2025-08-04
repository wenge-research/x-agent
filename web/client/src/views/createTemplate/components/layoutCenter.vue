<template>
	<div>
		<div class="center-parent">
			<div class="header-tabs">
				<div class="tabs-item" :class="{ 'active-tab': activeTab === item.value }" v-for="(item, index) in tabs"
					:key="index" @click="setActiveTab(item)">
					<iconpark-icon :name="item.icon" color="#ffffff" style="margin-right: 4px;"></iconpark-icon>
					<span>{{ item.name }}</span>
				</div>
			</div>
			<w-scrollbar ref="layoutScrollbarRef" class="center-side1">
				<div class="container-center" v-if="activeTab !== 'history'">
					<div :style="{ overflow: isMobile ? 'hidden' : 'scroll', height: heightSide }" class="center-side"
						v-if="!loading">
						<CenterInitItem v-if="curRouteId == judicialUrl"></CenterInitItem>

						<div class="message-list">
							<div v-if="activeTab === 'home'">
								<div class="appTemplate-head" style="margin-left: 0;"
									v-if="curRouteId == policyUrl && !isMobile && (virtualHumanLogo || getAppDetail()?.identityIcon || getAppDetail()?.greeting)">
									<div v-if="virtualHumanLogo" style="height: 56px;width: 380px;overflow: hidden;display: flex;
								justify-content: center;align-items: center;">
										<img :src="virtualHumanLogo" alt="" class="appTemplate-head-facadeImageUrl" />
									</div>

									<div v-if="getAppDetail()?.identityIcon || getAppDetail()?.greeting"
										style="flex: 1;overflow: hidden;margin-left: 16px;padding: 22px 0">
										<div class="title">
											<img v-if="getAppDetail()?.identityIcon?.includes('.png')"
												:src="getAppDetail()?.identityIcon" style="height: 26px" />
											<div v-else class="title-text">{{ getAppDetail()?.identityIcon }}</div>
										</div>
										<div class="message">{{ getAppDetail()?.greeting }}</div>
									</div>
								</div>
							</div>
							<div v-else class="answer-content">
								<el-scrollbar ref="scrollbarRef" :native="false" :wrap-style="{ overflowX: 'hidden' }"
									v-loading="resLoading" class="force-hide-scrollbar"
									element-loading-background="rgba(0, 0, 0, 0)">
									<div v-for="(item, index) in resDataList" :key="index" class="message-item"
										v-if="activeTab == 'images'">
										<div class="title-info">
											<div class="title-left">
												<div class="left-box">
													<iconpark-icon name="image-add-line" color="rgba(255,255,255,0.7)"
														style="margin-right: 4px;"></iconpark-icon>
													<div>参考人像主题</div>
												</div>
												<div class="left-box">
													<iconpark-icon name="flower-fill" color="green"
														style="margin-right: 4px;"></iconpark-icon>
													<div>豆包图像3.0</div>
												</div>
												<div class="left-box">
													<div>{{ item.ratio }}</div>
												</div>
											</div>
											<div class="title-right"></div>
										</div>
										<div class="item-question">{{ item.topic }}</div>
										<div class="image-container">
											<img :src="item.url" alt="" class="hoverable-image">
											<div class="image-hover-buttons">
												<button @click.stop="saveToPersonalCenter(item)" class="hover-button">
													<iconpark-icon name="save"></iconpark-icon>
													保存到个人中心
												</button>
												<button @click.stop="downloadImage(item)" class="hover-button">
													<iconpark-icon name="download"></iconpark-icon>
													下载到本地
												</button>
											</div>
										</div>
									</div>
									<div v-if="activeTab == 'videoes'" class="message-item"
										v-for="(item, index) in resDataList" :key="index">
										<div class="title-info">
											<div class="title-left">
												<div class="left-box">
													<iconpark-icon name="image-add-line" color="rgba(255,255,255,0.7)"
														style="margin-right: 4px;"></iconpark-icon>
													<div>参考人像主题</div>
												</div>
												<div class="left-box">
													<iconpark-icon name="flower-fill" color="green"
														style="margin-right: 4px;"></iconpark-icon>
													<div>豆包图像3.0</div>
												</div>
												<div class="left-box">
													<!-- <div>{{ item.ratio }}</div> -->
												</div>
											</div>
											<div class="title-right"></div>
										</div>
										<div class="item-question">{{ item.content }}</div>
										<video class="hoverable-image" :src="item.url" controls width="640" @error="handleVideoError">
											您的浏览器不支持视频播放。
										</video>
									</div>
								</el-scrollbar>
							</div>
						</div>

					</div>
					<!-- <div class="center-side" :style="{ overflow: isMobile ? 'hidden' : 'scroll', height: heightSide }"></div> -->
					<ChatModule ref="chatModuleRef"></ChatModule>
				</div>
				<!-- 历史记录 -->
				<div v-else style="width: 100%; height: 800px; padding: 16px; padding-top: 36px;">
					<div v-if="props.isToken">
						<history />
					</div>
					<div v-else style="display: flex; justify-content: center; color: #fff; font-size: 20px;">登录后可查看历史记录
					</div>
				</div>
			</w-scrollbar>
		</div>
		<div v-if="activeTab === 'home'" style="width: 100vw; height: 530px; margin-top: 160px; padding: 16px;">
			<example />
		</div>
	</div>

</template>

<script lang="ts" setup>
import { getVidioeData, getImgData, getGeneraImage, changeModel, saveMine } from '/@/api/chat/index';
import { defineAsyncComponent, ref, computed, onMounted, onBeforeUnmount, watch, onUnmounted, compile, provide, nextTick } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { useThemeConfig } from '/@/stores/themeConfig';
import { storeToRefs } from 'pinia';
import { useRoute, useRouter } from 'vue-router';
import { getPresetQuestionList } from '/@/api/knowledge';
import { apiGetShare } from '/@/api/chat/index'
import mittBus from '/@/utils/mitt';
const { isMobile, isxl } = useBasicLayout();
const chatStore = useChatStore();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
import { setScrollPosition } from '/@/utils/other';
import sendIcon from '/@/assets/chat/top.svg';
const CenterInitItem = defineAsyncComponent(() => import('./centerInitItem.vue'));
const ChatModule = defineAsyncComponent(() => import('./chatModule/index.vue'));
const history = defineAsyncComponent(() => import('./chatModule/components/history.vue'));
const example = defineAsyncComponent(() => import('./chatModule/components/example.vue'));
const loading = computed(() => chatStore.chatLoading);
import { ElMessage } from 'element-plus';
// const chatList = computed(() => chatStore.chatList);
const stopChatLoading = ref(false);
const dialogVisible = ref(false);
const route = useRoute();
const router = useRouter()
const maxWidth = ref(1000);
const rightNum = ref('8%');
const curQuestion = ref('');
const chatModuleRef = ref(null);
const policyUrl: any = ref(localStorage.getItem(`${route.params.appId}appId`));
const judicialUrl: any = ref(import.meta.env.VITE_JUDICIAL_QA);
const tjQuestList = ref([]);

const props = defineProps({
	isToken: {
		type: Boolean,
		required: true
	},
});
const handleVideoError = (e) => {
	console.error("视频加载失败:", e);
};

//头部tabs数据
const tabs = ref([
	{ name: '首页', value: 'home', icon: 'home-3-fill' },
	{ name: '图片生成', value: 'images', icon: 'image-add-line' },
	{ name: '视频创作', value: 'videoes', icon: 'movie-ai-line' },
	{ name: '历史', value: 'history', icon: 'file-history-line' }
])
//所选tab 
const activeTab = ref('home');

const setActiveTab = (item) => {
	resDataList.value = []
	resLoading.value = false
	activeTab.value = item.value;
	if (item.value == 'history' && !props.isToken) {
		ElMessage.error('请登录后查看')
	}
	console.log('isToken', props.isToken)
}

//控制是否显示回答页面
const isAnswerShow = ref(false);
const handleclickStopChat = async () => {
	stopChatLoading.value = true;
	await chatStore.clearChat();
	stopChatLoading.value = false;
	chatStore.scrollbarBottom = true;
	setScrollPosition(isMobile.value ? '.message-list' : '.center-side');
};
provide('handleclickStopChat', handleclickStopChat);
provide('activeTabValue', activeTab)
// 保存到个人中心
const saveToPersonalCenter = async (item) => {
	try {
		const parms = {
			imageUrl: item.url,
			ratio: item.ratio,
			description: item.description
		}
		const res = await saveMine(parms)
		if (res.code == '000000') {
			ElMessage.success('保存成功')
		}
	} catch (error) {
		console.log(error)
	}
}

const downloadImage = async (item) => {
	try {
		// 1. 先请求后端获取签名 URL（假设你的后端提供 /get-signed-url 接口）
		const res = await fetch(`/get-signed-url?url=${encodeURIComponent(item.url)}`);
		const { signedUrl } = await res.json();

		// 2. 使用签名 URL 下载
		const link = document.createElement('a');
		link.href = signedUrl;
		link.download = item.url.split('/').pop() || `image_${Date.now()}.jpg`;
		link.click();
	} catch (error) {
		console.error('下载失败:', error);
		window.open(item.url, '_blank'); // 降级方案
	}
};


const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo : '';
};


const facadeImageUrl = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.facadeImageUrl : '';
});

const virtualHumanLogo = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.virtualHumanLogo : '';
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
		chatStore.dialogueLoading = false;
	},
	{ immediate: true }
);
const heightSide = computed(() => {
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	let browserList = ['MQQBrowser', 'Opera', 'Edg', 'Firefox', 'Chrome', 'IEMobile', 'wOSBrowser', 'BlackBerry', 'BrowserNG', 'WebOS'];
	// let height = 'calc(100% - 98px)';
	let height = 'calc(100% - 128px)'
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
// 临时存储接收的数据（用于 watch 监听）
const receivedData = ref(null);
const resDataList = ref([])
const scrollbarRef = ref(null)
const scrollToBottom = () => {
	if (scrollbarRef.value) {
		const scrollContainer = scrollbarRef.value.wrapRef;
		const imgs = scrollContainer.querySelectorAll('img');

		// 如果所有图片已加载，直接滚动
		if (Array.from(imgs).every(img => img.complete)) {
			scrollContainer.scrollTop = scrollContainer.scrollHeight;
		} else {
			// 否则监听图片加载完成后再滚动
			imgs.forEach(img => {
				img.onload = () => {
					scrollContainer.scrollTop = scrollContainer.scrollHeight;
				};
			});
		}
	}
};
const resLoading = ref(false); // 控制加载状态
// 处理接收的数据
const handleParamsData = async (data) => {
	let resData = data;
	receivedData.value = data; // 更新数据，触发 watch
	resLoading.value = true; // 开始加载，显示 Loading

	try {
		if (data.activeType === "create") {
			const res = await getGeneraImage(data);
			resData.url = res.data.imageUrl;
			resDataList.value.push(resData);
		} else {
			const res = await getVidioeData(data);
			resData.url = res.data;
			resDataList.value.push(resData);
		}

		await nextTick();
		scrollToBottom(); // 滚动到底部
	} catch (error) {
		console.error(error);
	} finally {
		resLoading.value = false;
	}
};
// 监听 receivedData 变化，更新 activeTab
watch(
	() => receivedData.value,
	(newData) => {
		if (newData) {
			if (newData.activeType === 'create') {
				activeTab.value = 'images'
			} else if (newData.activeType === 'video') {
				activeTab.value = 'videoes'
				// video
			}
			console.log("activeTab 已更新:", activeTab.value);
		}
	},
	{ deep: true } // 深度监听（如果数据是对象）
);

onMounted(() => {
	mittBus.on("sendParamsData", handleParamsData);
	mittBus.on('toggle-answer', (value) => {
		isAnswerShow.value = value
	})
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
	console.log(Object.keys(route.query).length)
	if (Object.keys(route.query).length != 0) {
		const id = route.query
		apiGetShare(id.key).then(res => {
			console.log(res.data)
			chatStore.addChatList(res.data)
		})
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

onBeforeUnmount(() => {
	mittBus.off('toggle-answer')
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
	() => route.query,
	() => {
		if (route.query.key) {
			console.log('改变了吧')
			router.go(0)
		}
	}
)
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
	} catch (error) { }
});
const emit = defineEmits(['handleFillForm', 'playVoiceParent']);
const handleFillForm = (info: any) => {
	emit('handleFillForm', info);
};
const playVoice = (value: string) => {
	// console.log('讲话>>>>>>>>>>>>>>>>', value);
	// Client.talk(value, false); 
	emit('playVoiceParent', value)
};
</script>

<style scoped lang="scss">
@import '/@/theme/mixins/index.scss';

.center-parent {
	// width: 100%;
	height: 100%;
	width: 100vw;
	// height: 100vh;
	position: relative;

	.header-tabs {
		display: flex;
		justify-content: space-between;
		width: 370px;
		position: absolute;
		left: 40%;
		top: -46px;

		.tabs-item {
			// background: rgba(255,255,255,0.1);
			opacity: 0.5;
			border-radius: 32px;
			padding: 6px 12px;
			display: flex;
			align-items: center;
			font-weight: 400;
			font-size: 14px;
			color: #FFFFFF;
			cursor: pointer;
		}

		.active-tab {
			background: rgba(255, 255, 255, 0.1);
			opacity: 1;
		}

	}
}

.message-list {
	margin-top: 30px;
	padding-bottom: 36px;

	.answer-content {
		height: 660px;
		width: 60vw;

		.message-item {
			margin-bottom: 16px;

			.title-info {
				width: 100%;
				display: flex;
				justify-content: space-between;

				.title-left {
					display: flex;
					margin-bottom: 12px;

					.left-box {
						display: flex;
						align-items: center;
						padding: 2px 6px;
						background: rgba(255, 255, 255, 0.05);
						border-radius: 4px;
						font-weight: 400;
						font-size: 14px;
						color: rgba(255, 255, 255, 0.7);
						margin-right: 6px;
					}
				}
			}
		}

		.item-question {
			font-weight: 400;
			font-size: 14px;
			color: #FFFFFF;
			margin-bottom: 6px;
		}


	}
}

.center-side {
	display: flex;
	justify-content: center;

	&::-webkit-scrollbar {
		display: none !important;
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

	.el-radio__input.is-checked+.el-radio__label {
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
	width: 80%;

	.container-center {
		height: 100%;
		width: 100%;
		position: relative;
		overflow-y: hidden;
		overflow-x: hidden;
		// background: #F3F6FB;
		// background: linear-gradient(180deg, rgba(246, 250, 248, 0.8) 0%, rgba(233, 242, 239, 0.8) 100%);
		border-radius: 16px;

		&::-webkit-scrollbar {
			display: none !important;
			/* 完全隐藏滚动条 */
		}
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

.prologue {
	background: #fff !important;
	padding: 8px 16px !important;
	border-radius: 2px 12px 12px 12px !important;
}

.fontSize16 {
	@include add-size($font-size-base16, $size);
}

.force-hide-scrollbar {
	--el-scrollbar-opacity: 0;
	/* 隐藏滚动条 */
}

.force-hide-scrollbar .el-scrollbar__bar {
	display: none !important;
}

.loading-container {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 100px;
	color: #999;
}

.loading-container .el-icon {
	margin-right: 8px;
	animation: rotate 1s linear infinite;
}

@keyframes rotate {
	from {
		transform: rotate(0deg);
	}

	to {
		transform: rotate(360deg);
	}
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

	>div:nth-child(2) {
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

.appTemplate {
	width: 100vw;
	display: flex;
	align-items: center;
	justify-content: center;

	&-head {
		// height: 112px;
		// background: linear-gradient( 179deg, rgba(55,162,239,0.2) 0%, rgba(2,109,187,0.03) 100%), #FFFFFF;
		// background: rgba(255, 255, 255, 0.1);
		backdrop-filter: blur(8px);
		border-radius: 12px;
		// padding: 0 24px 0 12px;
		padding: 8px 16px 8px 8px;
		display: flex;
		align-items: center;

		&-facadeImageUrl {
			height: 100%;
			// width: 100%;
		}

		.title {
			font-weight: bold;
			@include add-size($font-size-base20, $size);
			color: #383D47;
			line-height: 26px;
			font-family: MiSans, MiSans;
			font-weight: 600;
			margin-bottom: 8px;

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

			&-text {
				width: 100%;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
				font-weight: 600;
				font-size: 32px;
				line-height: 42px;
				text-align: left;
				font-style: normal;

				/* 渐变字体关键代码 */
				background: linear-gradient(90deg, #345FFF 0%, #63FFFD 100%);
				-webkit-background-clip: text;
				/* 关键：裁剪背景到文本 */
				background-clip: text;
				color: transparent;
				/* 隐藏原始颜色，显示渐变 */
			}
		}

		.message {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 16px;
			color: #000000;
			line-height: 24px;
			text-align: left;
			font-style: normal;
			// font-weight: 400;
			// @include add-size($font-size-base16, $size);
			// color: #828894;
			// line-height: 24px;
			// display: -webkit-box;
			// -webkit-box-orient: vertical;
			// -webkit-line-clamp: 3; /* 控制显示的行数，这里设为2行 */
			// overflow: hidden;
		}
	}

	.title {
		margin-bottom: 0 !important;

		&-text {
			// 			font-family: MiSans, MiSans;
			// font-weight: 500;
			// font-size: 20px;
			// color: #000000;
			// line-height: 32px;
			// text-align: left;
			// font-style: normal;
		}
	}

	.message {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #000000;
		line-height: 24px;
		text-align: left;
		font-style: normal;
	}
}

.quest-list {
	padding-top: 16px;
	// margin-left: 24px;

	&-title {
		// font-family: MiSans, MiSans;
		// font-weight: 500;
		// @include add-size($font-size-base14, $size);
		// color: #383d47;
		// line-height: 20px;
		font-family: MiSans, MiSans;
		font-weight: 500;
		font-size: 14px;
		color: #3F4247;
		line-height: 20px;
		text-align: justify;
		font-style: normal;
	}

	.item {
		margin-top: 12px;
		@include add-size($font-size-base16, $size);
		display: inline-block;
		position: relative;
		font-family: MiSans, MiSans;
		color: #646479;
		padding: 10px 16px;
		background: #FFFFFF;
		border-radius: 8px;
		cursor: pointer;

		iconpark-icon {
			display: none;
		}
	}

	.item:hover {
		background: #ffffff;
		box-shadow: 0px 5px 8px 0px rgba(45, 55, 85, 0.1);
		border-radius: 8px;
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
		// height: calc(100% - 60px);
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
	bottom: 0px;
	padding: 0 5px;
	height: 24px;
	background: transparent;
	border-radius: 12px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-family: MiSans, MiSans;
	font-weight: 400;
	@include add-size($font-size-base14, $size);
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


.image-container {
	position: relative;
	display: inline-block;
}

.hoverable-image {
	width: 600px;
	height: 500px;
	cursor: pointer;
	transition: opacity 0.3s;
}

.hoverable-image:hover {
	opacity: 0.9;
}

.image-hover-buttons {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	display: none;
	gap: 10px;
}

.image-container:hover .image-hover-buttons {
	display: flex;
}

.hover-button {
	background: rgba(0, 0, 0, 0.7);
	color: white;
	border: none;
	padding: 8px 10px;
	padding-right: 24px;
	border-radius: 4px;
	cursor: pointer;
	display: flex;
	align-items: center;
	gap: 5px;
	transition: background 0.3s;
}

.hover-button:hover {
	background: rgba(0, 0, 0, 0.9);
}
</style>
