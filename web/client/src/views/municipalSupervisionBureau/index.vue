<template>
	<div class="container_chat relative" :class="wrapClass" @click="openAllDialog">
		<w-layout :class="{ MobileH: !isMobile }">
			<w-layout-content v-if="curRouteId == policyUrl && previewData.active && route.params.appId != 0">
				<layoutCenterPdf v-if="previewData.active && route.params.appId != 0" />
			</w-layout-content>
			<w-layout-content v-if="curRouteId == judicialUrl && previewData.active && route.params.appId != 0" class="sfLayout">
				<layoutCenterPdf v-if="previewData.active && route.params.appId != 0" />
			</w-layout-content>
			<w-layout-sider
				v-if="curRouteId == policyUrl"
				class="w-layout-sider-right flex zcflex"
				:resize-directions="previewData.active && route.params.appId != 0 ? ['left'] : []"
			>
				<div class="chatDivFlex" :class="{ chatDivFlex2: !isMobile }" :style="particlesContainerStyle">
					<div class="chatDivFlex-left">
						<img src="/src/assets/municipalSupervisionBureau/left-logo.png" class="left-logo" alt="" />
            <iconpark-icon class="volume-down" name="volume-down-line" v-if="chatStore.streamVoiceFlag " @click="stopPlayVoice" size="24" color="#fff"></iconpark-icon>
            <iconpark-icon class="volume-down" name="volume-mute-line" v-else @click="chatStore.streamVoiceFlag = true;" size="24" color="#fff"></iconpark-icon>
						<iconpark-icon name="writer-gaixie" color="#fff" size="20" class="writer-gaixie"></iconpark-icon>
						<iconpark-icon name="account-circle-fill" color="#fff" size="20"></iconpark-icon>
					</div>
					<div class="chat-content">
						<div class="top-logo" v-if="!isMobile">
							<img src="/src/assets/municipalSupervisionBureau/add-icon.png" class="add-icon" alt="" @click="refreshPage" />
							<img v-if="logoUrl()" class="lh-logo" :src="logoUrl()" />
							<img v-else class="lh-logo" src="/src/assets/municipalSupervisionBureau/default-img.png" />
							<div class="right">
								<div v-show="ttsa" class="right-bobao">
									自动播报
									<el-switch v-model="isPlaying" @change="bobaoHandler"> </el-switch>
								</div>
								<iconpark-icon name="font-size-2" color="#383D47" size="16" style="margin-right: 24px"></iconpark-icon>
								<el-tooltip :content="showSZR ? '关闭数字人' : '连接数字人'">
									<div v-if="isSZR" @click="showSZRHandler" class="right-box">
										<img v-if="!showSZR" src="/src/assets/municipalSupervisionBureau/left-icon.png" class="left-icon" alt="" />
										<iconpark-icon name="account-circle-fill" color="#383D47" size="16"></iconpark-icon>
										<img v-if="showSZR" src="/src/assets/municipalSupervisionBureau/right-icon.png" class="right-icon" alt="" />
									</div>
								</el-tooltip>
							</div>
							<!-- <div class=" right">
                <div class="font-setting">
                  <div>
                    字号设置：<span @click="changeFontSize('2')"
                      :style="{ color: sizeType === '2' ? '#169E9A' : '#333333' }">大</span> |
                    <span @click="changeFontSize('1')"
                      :style="{ color: sizeType === '1' ? '#169E9A' : '#333333' }">中</span> |
                    <span @click="changeFontSize('0')"
                      :style="{ color: sizeType === '0' ? '#169E9A' : '#333333' }">小</span>
                  </div>
                </div>
                <div class="chat-box" @click="historyDialogClick" title="历史对话">
									<img :src="chatLine" alt="历史对话" />
								</div>
                <div class="time_box">
                  <div>{{ date }}&nbsp;&nbsp;</div>
                  <div>{{ time }}</div>
                </div>
              </div> -->
						</div>

						<!-- <div class="time-center" v-if="!isMobile">
              <div class="right">
                <div class="font-setting">
                  <div>
                    字号设置：<span @click="changeFontSize('2')"
                      :style="{ color: sizeType === '2' ? '#4186f4' : '#333333' }">大</span> |
                    <span @click="changeFontSize('1')"
                      :style="{ color: sizeType === '1' ? '#4186f4' : '#333333' }">中</span> |
                    <span @click="changeFontSize('0')"
                      :style="{ color: sizeType === '0' ? '#4186f4' : '#333333' }">小</span>
                  </div>
                </div>
                <div class="time_box">
                  <div style="font-weight: bold; color: #404467; font-size: 20px; margin-top: -4px">{{ time }}</div>
                  <div style="font-size: 14px; color: #404467">{{ date }}</div>
                  <div class="change-box">
                    <CoolBanbenqiehuan size="18" color="#4085F4" />
                    <span @click="backOld">返回旧版</span>
                  </div>
                </div>
              </div>
            </div> -->
						<div class="chat-bottom" :class="{ 'chat-bottom-mobile': isMobile }">
							<div class="chat-bottom-content" :class="[isSZR && showSZR ? 'pl48' : 'pl48']">
								<LayoutCenter @handleFillForm="handleFillForm" />
							</div>
							<div
								v-show="isSZR && showSZR"
								class="chat-bottom-people"
								v-loading="szrLoading"
								element-loading-text="加载中..."
								element-loading-spinner="el-icon-loading"
								element-loading-background="rgba(0, 0, 0, 0)"
							>
								<div id="ttsa" style="width: 100%; height: 100%"></div>
							</div>
						</div>
					</div>
				</div>
			</w-layout-sider>
			<w-layout-sider
				v-if="curRouteId == judicialUrl"
				class="w-layout-sider-right flex"
				:style="particlesContainerStyle"
				:resize-directions="previewData.active && route.params.appId != 0 ? ['left'] : []"
			>
				<vue-particles id="wft-tsparticles" :particlesInit="particlesInit" :options="particlesOpt" />
				<div class="chatDivFlex">
					<LayoutCenter />
				</div>
			</w-layout-sider>
		</w-layout>

		<el-dialog :show-close="false" v-model="formVisible" width="760px" :close-on-press-escape="false" :close-on-click-modal="false">
			<Information :formParams="formParams" @close="closedialogForm" v-if="formVisible" />
		</el-dialog>
		<el-drawer title="历史对话" v-model="historyDialog" direction="rtl" :before-close="handleClose" class="elDrawer" size="30%">
			<historicalDialogue :historyDialog="historyDialog" @closeDialog="closeDialog"></historicalDialogue>
		</el-drawer>
	</div>
</template>

<script lang="ts" setup>
import 'splitpanes/dist/splitpanes.css';
import { computed, defineAsyncComponent, onMounted, onUpdated, ref, watch, nextTick, onUnmounted } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { useRoute, useRouter } from 'vue-router';
import { useKnowledgeState } from '/@/stores/knowledge';
import { fileTree, userKnowledgesSize } from '/@/api/knowledge';
import { Message, Modal } from 'winbox-ui-next';
import particlesOpt from './config/particles1';
import { loadSlim } from 'tsparticles-slim';
import { stopPlay } from '/@/utils/newVoiceFun';
import chatLine from '/@/assets/ai/chat-history-line.svg';
import mittBus from '/@/utils/mitt';
const knowledgeState = useKnowledgeState();
const previewData: any = computed(() => knowledgeState.previewData);
const chatStore = useChatStore();
import { formatDate } from '/@/utils/formatTime';
const Information = defineAsyncComponent(() => import('/@/views/information/index-pc.vue'));
const LayoutCenter = defineAsyncComponent(() => import('./components/layoutCenter.vue'));
const layoutCenterPdf = defineAsyncComponent(() => import('./components/layoutCenterPdf.vue'));
const historicalDialogue = defineAsyncComponent(() => import('/@/views/lh-gpt/components/historicalDialogue.vue'));

// 移动端自适应相关
const { isMobile } = useBasicLayout();
const historyDialog = ref(false);

const wrapClass = computed(() => {
	return [isMobile.value ? 'container_chat_mobile' : 'flex', curRouteId.value == import.meta.env.VITE_JUDICIAL_QA ? 'chat-bg2' : ''];
});
const route = useRoute();
const router = useRouter();
const openAllDialog = () => {
	chatStore.uploadDrawerVisible = false;
	chatStore.paramsDrawerVisible = false;
};
const particlesContainerStyle = computed(() => {
	return {
		width: '100%',
		height: '100%',
	};
});

const refreshPage = () => {
	chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
};

const logoUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.logo : '';
};

const particlesInit = async (engine: any) => {
	await loadSlim(engine);
};

const getKnowledgesSize = async () => {
	let resSize = await userKnowledgesSize();
	if (resSize?.code === 200 && resSize?.data) {
		knowledgeState.setKnowledgesSize(resSize.data);
	}
};
const time = ref('');
const sizeType = ref('0');
const date = ref('');
const mainDomW = ref(0);
const curRouteId = ref('');

const chunkList = computed(() => {
	return chatStore.chunks;
});

// 新代码逻辑
const showSZR = ref(false);

// 是否显示虚拟人
const isSZR = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.virtualHumanFlag == '是' ? true : false;
});
// 关闭/连接数字人
const showSZRHandler = () => {
	showSZR.value = !showSZR.value;
	if (showSZR.value) {
		initSzr();
		resetTimer();
	} else {
		closeRoom();
		clearInterval(timer.value);
	}
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
// let currentIndex = 0; // 用于跟踪当前播放的音频块索引
const currentIndex = ref(0);
const oldChunks = ref([]);
const isPlaying = ref(false);
const playIndex = ref(0); // 用于跟踪当前播放的音频块索引
const boBaoTime = ref();
const timer = ref(null);
// 播报开关
const bobaoHandler = () => {
	if (isPlaying.value) {
		resetTimer();
	} else {
		ttsa?.interrupt();
	}
};
const updateLocalTime = () => {
	const now = new Date().getTime();
	boBaoTime.value = now;
};

const resetTimer = () => {
	if (!ttsa) return;
	clearInterval(timer.value); // 清除之前的定时器
	updateLocalTime(); // 更新时间
	timer.value = setInterval(() => {
		closeRoom();
		showSZR.value = false;
	}, 15 * 60 * 1000); // 重新设置定时器，每隔 15 分钟更新一次
};

// 流式播报
const sendStreamTextTestHandler = (text, index, isStreamEnd) => {
	let cleanedText = text.replace(/(\\n)/g, '');
	cleanedText = removeMarkdownSymbols(cleanedText.replace(/<a[^>]*>.*?<\/a>/g, ''));
	if (isStreamEnd) {
		console.log('is_stream_end', isStreamEnd);
		console.log('cleanedText', cleanedText);
		ttsa.sendStreamText(cleanedText, { is_stream_end: true });
	} else {
		ttsa.sendStreamText(cleanedText);
	}
};

const oldClientId = ref('');
// 数字人初始
let ttsa = null;
const serverUrl = ref('https://open.xmov.ai/api');
const username = ref('aaaa');
const appId = ref('xxxxxxx');
const appSecret = ref('560afa7e94a34907b4a05002a0a26965');
const szrLoading = ref(false);
const initSzr = () => {
	szrLoading.value = true;
	if (!username.value || !appId.value || !appSecret.value || !serverUrl.value) {
		alert('服务地址,用户名，id,secret必填！');
		return;
	}
	ttsa = new window.XmovTTSA({
		container: '#ttsa',
		server: serverUrl.value,
		'//width': 1080,
		'//height': 600,
		account: {
			username: username.value,
			app_id: appId.value,
			app_secret: appSecret.value,
		},
		widgetCallback: (e: any) => {
			const { type, data } = e.callback_info || {};
			// console.log(type, data);
			if (type == 'voice_start') {
				mittBus.emit('samrPlaying', oldClientId.value);
			}
			if (type == 'voice_end') {
				mittBus.emit('samrEnding');
				// 清除计时器 重新计算
				resetTimer();
			}
		},
		webrtcCallback: (online: any) => {
			console.log('online: ', online);
			if (!online) {
				closeRoom();
			}
			szrLoading.value = false;
		},
		config: {
			auto_action: false,
			fps: 30,
			max_bitrate: 8,
			offline: false,
			tag: '',
		},
		videoOptions: {
			muted: false,
			showMutedButton: false,
		},
	});
	ttsa.setup().then((res:any) => {
		console.log("setup====", res)
	}).catch((error:any) => {
		console.log("setup====error", error)
		Message.warning(error.message);
		szrLoading.value = false;
	});
};
// 关闭连接
const closeRoom = () => {
	ttsa?.closeRoom();
	ttsa = null;
};
// 流式输出
// const sendStreamTextTest = (list: any) => {
// 	let count = 0;
// 	// const textList = [
// 	// 	'魔珐科技自2018年成立以来',
// 	// 	'在虚拟人打造、虚拟人领域相关技术以及产品和服务等多个维度持续领先探索',
// 	// 	'创下多个业内第一的行业标杆性事件',
// 	// 	'持续引领虚拟人赛道发展。',
// 	// 	'助力您的业务落地AIGC+3D虚拟人。',
// 	// ];
// 	let textList = list;
// 	const intervalId = setInterval(() => {
// 		const ssml = textList[count++];
// 		if (count >= textList.length) {
// 			count = 0;
// 			ttsa.sendStreamText(ssml, { is_stream_end: true });
// 			clearInterval(intervalId);
// 		} else {
// 			ttsa.sendStreamText(ssml);
// 		}
// 	}, 600);
// };

const policyUrl: any = ref(localStorage.getItem(`${route.params.appId}appId`));
const judicialUrl: any = ref(import.meta.env.VITE_JUDICIAL_QA);
// 计算pane的宽度百分比
const calcPaneSize = () => {
	const mainDom: any = document.querySelector('.container_chat');
	mainDomW.value = mainDom.offsetWidth;
};
const getTree = async () => {
	let res = await fileTree(curRouteId.value);
	if (res?.code === 200 && res?.data) {
		knowledgeState.setFileList(res.data);
		getKnowledgesSize();
	} else {
		Message.warning(res.msg);
	}
};
const changeFontSize = (size: string) => {
	sizeType.value = size;
	window.document.documentElement.setAttribute('data-size', size);
};
// 点击返回旧版
const backOld = () => {
	window.open('https://www.szlhq.gov.cn/znjqr', '_blank');
};
const goToOtherLink = (url) => {
	console.log(url);

	Modal.confirm({
		title: 'szlhq.gov.cn 显示',
		content: `您访问的链接即将离开“xxxxxxx政府在线”门户网站，是否继续？`,
		closable: true,
		okText: '确定',
		cancelText: '取消',
		hideCancel: false,
		modalClass: 'myConfirm',
		onOk: async () => {
			window.open(url, '_blank');
		},
	});
};
const historyDialogClick = () => {
	historyDialog.value = true;
};
const stopPlayVoice = () => {
  chatStore.streamVoiceFlag = false;
  stopPlay()
};
const handleClose = () => {
	historyDialog.value = false;
};
const closeDialog = (val) => {
	historyDialog.value = val;
};
onUnmounted(async () => {
	if (isSZR.value) {
		ttsa?.closeRoom();
	}
	clearInterval(timer.value);
});
window.addEventListener('beforeunload', function (event) {
	if (isSZR.value) {
		// 在页面即将刷新或关闭时执行的操作
		ttsa?.closeRoom();
	}
	clearInterval(timer.value);
});
onMounted(async () => {
	if (!route.params.conversationId) {
		await chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
	}
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
	calcPaneSize();
	setInterval(() => {
		date.value = formatDate(new Date(), 'YYYY/mm/dd');
		time.value = formatDate(new Date(), 'HH:MM:SS');
	}, 1000);
	// if (isSZR.value && showSZR.value) {
	// 	nextTick(async () => {
	// 		await initSzr();
	// 	});
	// }

	mittBus.on('textToSpeechAndPlay', ({ newChunks, clientId }) => {
		if (oldClientId.value !== clientId) {
			if (oldClientId.value) {
				currentIndex.value = 0;
				oldChunks.value = [];
				// 打断
				ttsa?.interrupt();
				// 清除计时器 重新计算
				resetTimer();
			}
			oldClientId.value = clientId;
		}
		// console.log("chunkList.value", chunkList.value)
		// console.log("newChunks", newChunks)
		if (newChunks.length > oldChunks.value.length && ttsa) {
			oldChunks.value = newChunks;
			if (!isPlaying.value) return;
			if (currentIndex.value + 1 == newChunks.length) {
				console.log('currentIndex', currentIndex.value);
				console.log('newChunks', newChunks.length);
				sendStreamTextTestHandler(oldChunks.value[currentIndex.value], currentIndex.value, true);
			} else {
				sendStreamTextTestHandler(oldChunks.value[currentIndex.value], currentIndex.value);
			}

			currentIndex.value++;
		}
	});
	mittBus.on('stopTalkSAMR', () => {
		// 打断播报
		ttsa?.interrupt();
		currentIndex.value = 0;
		oldChunks.value = [];
		oldClientId.value = '';
	});
	// window.removeEventListener('resize', checkDevice);
	// 每隔 15 分钟更新一次本地时间
	resetTimer();
});
// const checkDevice = () => {
//   if (window.innerWidth < 600) {
//     // 使用window.location.href进行跳转
//     router.push('/cha')
//   }
// }
onUpdated(() => {
	sessionStorage.removeItem('ModalFlag');
});

const formVisible = ref(false);
const formParams = ref({});
const handleFillForm = (info) => {
	formVisible.value = true;
	formParams.value = info;
};

const closedialogForm = () => {
	formVisible.value = false;
};
</script>

<style lang="scss" scoped>
:deep(.el-dialog__header) {
	display: none !important;
}
:deep(.el-dialog) {
	padding: 0 !important;
	border-radius: 12px !important;
}
:deep(.el-overlay) {
	z-index: 999 !important;
}
:deep(canvas) {
	margin-top: 100px !important;
}

.container_chat {
	padding-bottom: 0;
	height: 100%;
	justify-content: space-between;
}

:deep(.MobileH) {
	height: 100%;
}

.container_chat_mobile {
	background: none;
}

.container_chat_bg {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: calc(100vh - 105px);
	background: url(../../assets/zc/bg.png) no-repeat;
	background-size: 100% 100%;
}

.chat-bg2 {
	background: url(../../assets/zc/bg.png) no-repeat;
	background-size: 100% 100%;
}

:deep(.splitpanes--vertical) {
	.splitpanes__splitter {
		width: 7px !important;
		background-color: var(--color-neutral-3);
	}

	& > .splitpanes__splitter:before,
	& > .splitpanes__splitter:after {
		width: 7px;
	}
}

.container_chat :deep(.w-layout-sider-children),
.container_chat :deep(.w-layout-content) {
	width: 100%;
	display: flex;
	flex-direction: column;
	overflow: hidden;
}

.container_chat .sfLayout {
	justify-content: center;
}

.container_chat :deep(.w-layout-sider-left) {
	width: 300px;
	min-width: 300px;
	max-width: 500px;
	box-shadow: 2px 0 6px rgb(0 21 41 / 1%);
}

.container_chat :deep(.w-layout-content) {
	flex: 1;
	max-width: calc(100vw - 1050px);
}

.container_chat :deep(.w-layout-sider-right) {
	width: 59%;
	background: none;

	&.zcflex {
		padding: 0 0 0 0 !important;
	}

	&.flex {
		width: 59% !important;
		padding: 56px 72px;
		flex: 1;
	}

	.chatDivFlex {
		display: flex;
		justify-content: space-between;
		width: 100%;
		height: 100%;
		padding: 12px 12px 12px 0;
		overflow: hidden;

		&-left {
			width: 64px;
			height: 100%;
			padding: 12px 0 22px;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: space-between;

			.left-logo {
				width: 44px;
			}
			.add-box {
				display: flex;
				z-index: 2;
				.new-chat {
					width: 88px;
					height: 32px;
					border-radius: 4px;
					border: 1px solid #ffffff;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #ffffff;
					display: flex;
					align-items: center;
					justify-content: center;
					cursor: pointer;
					img {
						width: 20px;
						height: 20px;
						margin-right: 4px;
					}
				}
  		}
			.writer-gaixie {
				margin-top: 20px;
				margin-bottom: 24px;
				cursor: pointer;
			}
			.volume-down {
				margin-top: auto;
				position: relative;
				left: 1px;
				cursor: pointer;
			}
		}
	}

	.chatDivFlex2 {
		// position: relative;
		background: url(../../assets/municipalSupervisionBureau/bg.png) no-repeat;
		background-size: 100% 100%;

		.chat-content {
			// position: absolute;
			flex: 1;
			// width: 50%;
			height: 100%;
			padding: 15px 0 55px;
			background: rgba(255, 255, 255, 0.8);
			border-radius: 16px;
			border-image: linear-gradient(139deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0)) 1 1;
			backdrop-filter: blur(3px);
			padding: 16px 24px 0;
			// top: 0;
			// left: 50%;
			// transform: translateX(-50%);

			// .top-logo {
			//   height: 5.8%;
			//   margin-bottom: 1%;
			//   display: flex;
			//   align-items: flex-end;
			//   justify-content: space-between;

			//   >div {
			//     height: 100%;
			//     display: flex;
			//     align-items: flex-end;
			//   }

			//   .gh {
			//     height: 100%;
			//     margin-right: 22px;
			//   }

			//   .lh-logo {
			//     height: 80.2%;
			//     cursor: pointer;
			//   }

			//   .right {
			//     display: flex;

			//     .font-setting {
			//       margin-right: 18px;
			//       margin-top: 8px;

			//       >div {
			//         width: 170px;
			//         height: 28px;
			//         // background: #edf8fd;
			//         border-radius: 18px 18px 18px 18px;
			//         border: 1px solid #181b49;
			//         font-size: 12px;
			//         font-family: MicrosoftYaHei;
			//         color: #332f31;
			//         display: flex;
			//         justify-content: center;
			//         align-items: center;

			//         // margin-top: 52px;
			//         span {
			//           cursor: pointer;
			//           margin: 0 5px;
			//         }
			//       }
			//     }
			//     .chat-box {
			// 			margin-right: 16px;
			// 			margin-top: 8px;
			// 			line-height: 26px;
			//       height: 26px;
			// 			cursor: pointer;
			// 			img {
			// 				width: 22px;
			// 				height: 22px;
			// 			}
			// 		}
			//     .time_box {
			//       height: 28px;
			//       // padding: 0 8px;
			//       line-height: 28px;
			//       // font-size: 24px;
			//       // font-family: DINPro-Medium, DINPro;
			//       // font-weight: 500;
			//       // color: rgba(24, 27, 73, 1);
			//       display: flex;
			//       align-items: center;
			//       font-family: DINPro, DINPro;
			//       font-weight: 700;
			//       font-size: 18px;
			//       color: #181b49;
			//     }

			//     .change-box {
			//       display: flex;
			//       justify-content: center;
			//       align-items: flex-end;
			//       border-radius: 15px;
			//       border: 1px solid #4186f4;
			//       font-size: 14px;
			//       font-family: PingFangSC, PingFang SC;
			//       font-weight: 400;
			//       color: #4186f4;
			//       padding: 2px 10px;
			//       margin-top: 8px;
			//       margin-right: 8px;
			//       cursor: pointer;

			//       img {
			//         width: 14px;
			//         height: 16px;
			//         margin-right: 4px;
			//       }
			//     }
			//   }
			// }

			.top-logo {
				width: 100%;
				height: 40px;
				display: flex;
				align-items: center;

				.add-icon {
					width: 24px;
					height: 24px;
					margin-right: 20px;
					cursor: pointer;
				}

				.lh-logo {
					height: 40px;
				}

				.right {
					display: flex;
					align-items: center;
					margin-left: auto;
					cursor: pointer;

					&-bobao {
						font-family: MiSans, MiSans;
						font-weight: 400;
						font-size: 16px;
						color: #383d47;
						line-height: 20px;
						margin-right: 24px;
						.el-switch__core {
							min-width: 32px !important;
							height: 20px;
							border-radius: 12px;
							background: #ced4e0;
							.el-switch__action {
								width: 14px;
								height: 14px;
							}
						}
						.el-switch.is-checked .el-switch__core {
							border-color: #2065d6;
							background: #2065d6;
						}
					}

					&-box {
						display: flex;
						align-items: center;
						.left-icon,
						.right-icon {
							width: 16px;
							height: 16px;
						}
						.left-icon {
							margin-right: -6px;
						}
						.right-icon {
							margin-left: -6px;
						}
					}
				}

				.logo {
					// width: 138px;
					height: 32px !important;
					object-fit: contain;
				}
			}

			.time-center {
				display: flex;
				justify-content: space-between;
				z-index: auto;
				position: absolute;
				width: 100%;
				height: 18.5%;
				display: none;

				.left {
					display: flex;
					height: 100%;

					.left-logo {
						height: 100%;
						z-index: 4;
						margin-top: 10px;
					}

					.title1 {
						// width: 357px;
						height: 37%;
					}

					.title2 {
						// width: 229px;
						height: 24%;
						position: absolute;
						top: 30.6%;
						right: 40px;
					}

					.tip {
						position: absolute;
						top: 7.7%;
						right: -95px;

						font-size: 14px;
						font-family: PingFangSC, PingFang SC;
						font-weight: 400;
						color: #4186f4;
						padding: 1px 4px;
						border-radius: 4px;
						border: 1px solid #4186f4;
					}
				}

				.right {
					display: flex;

					.font-setting {
						margin-right: 40px;

						> div {
							width: 170px;
							height: 30px;
							background: #edf8fd;
							border-radius: 18px 18px 18px 18px;
							border: 1px solid #4186f4;
							font-size: 12px;
							font-family: MicrosoftYaHei;
							color: #333333;
							display: flex;
							justify-content: center;
							align-items: center;
							margin-top: 52px;

							span {
								cursor: pointer;
								margin: 0 5px;
							}
						}
					}

					.time_box {
						height: 56px;
						padding: 0 8px;
						font-size: 24px;
						font-family: DINPro-Medium, DINPro;
						font-weight: 500;
						color: rgba(24, 27, 73, 1);

						.change-box {
							display: flex;
							justify-content: center;
							align-items: flex-end;
							border-radius: 15px;
							border: 1px solid #4186f4;
							font-size: 14px;
							font-family: PingFangSC, PingFang SC;
							font-weight: 400;
							color: #4186f4;
							padding: 2px 10px;
							margin-top: 8px;
							cursor: pointer;

							img {
								width: 14px;
								height: 16px;
								margin-right: 4px;
							}
						}
					}
				}
			}

			.chat-bottom {
				// position: absolute;
				// left: 0;
				// top: 9%;
				width: 100%;
				height: calc(100% - 40px - 24px);
				margin-top: 24px;
				// min-width: 700px;
				// background: rgba(255, 255, 255, 0.6);
				// box-shadow: 0px 0px 10px 0px #357df2;
				// border-radius: 30px 30px 30px 30px;
				// opacity: 1;
				// border: 1px solid #ffffff;
				// background: rgba(225, 233, 245, 1);
				border-radius: 16px;
				// border: 1px solid #ffffff;
				// backdrop-filter: blur(1px);
				display: flex;
				justify-content: center;
				align-items: center;

				&-content {
					width: 60%;
					height: 100%;
				}
				.pl48 {
					padding-left: 48px;
				}
				&-people {
					flex: 1;
					height: 100%;
					padding-right: 18px;
					display: flex;
					align-items: flex-end;
					justify-content: flex-end;

					img {
						width: 388px;
					}
				}
			}
		}
	}
}

:deep(.w-layout-sider-light) {
	box-shadow: none;
}

.footer-copyrightInfo {
	height: 8.08%;
	background: #fff;
	z-index: 999999;
}

.copyrightInfo-inner {
	width: 70%;
	min-width: 1242px;
	height: 85px;
	margin: 0 auto;
	overflow: hidden;
	display: flex;
	align-items: center;
	*zoom: 1;
}

.copyright-left {
	width: 40%;
	/* margin-left:160px; */
}

.copyright-right {
	width: 60%;
}

.copyright-left {
	float: left;
}

.copyright-left p,
.copyright-left a,
.copyright-right p {
	font-size: 14px;
	color: #666;
	line-height: 1.8;
}

.copyright-left,
.copyright-right {
}

.copyright-right {
	float: right;
}

.copyrightR-info {
	float: right;
	width: auto;
}

.tel {
	float: left;
	margin-right: 10px;
}

.copyrightR-info p {
	text-align: left;
}

.copyrightR-slogan {
	width: 245px;
	float: right;
	margin-left: 0px;
	display: flex;
}

.copyrightR-slogan img {
	height: 46px;
}

a:hover {
	text-decoration: underline;
}

.beian {
	.link {
		cursor: pointer;
	}

	.link:hover {
		text-decoration: underline;
	}
}

.footer-copyrightInfo-mobile {
}

.chat-bottom-mobile {
	height: calc(100vh - 64px);
	background: rgba(225, 233, 245, 1);
	border-radius: 24px 24px 0px 0px;
	border: 1px solid #ffffff;
}
</style>
<style>
.myConfirm {
	background: #292a2d;
	border: none;
}

.el-message-box__content {
	padding: 0 15px;
	margin-bottom: 10px;
}

.el-overlay.is-message-box .el-overlay-message-box {
	bottom: auto;
}

.el-message-box__title,
.el-message-box__message {
	color: #e8eaed;
}

.elDrawer {
	background: #f1f3f7;
	border-radius: 16px 0px 0px 16px;
	.el-drawer__header {
		font-family: MiSans, MiSans;
		font-weight: 500;
		font-size: 18px;
		color: #383d47;
		margin-bottom: 0px;
	}
}
</style>
