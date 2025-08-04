<template>
	<div class="container_chat relative" :class="wrapClass" @click="openAllDialog">
		<w-layout :class="{ MobileH: !isMobile }" style="flex-direction: column">
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
					<img v-if="backgroundImageUrl()" :src="backgroundImageUrl()" alt="" class="backgroundImageUrl" />
					<img v-else src="/src/assets/municipalSupervisionBureau/bg-1.png" alt="" class="backgroundImageUrl" />
					<!-- <div class="chatDivFlex-left">
						<img src="/src/assets/municipalSupervisionBureau/left-logo.png" class="left-logo" alt="" />
						<iconpark-icon name="writer-gaixie" color="#fff" size="20" class="writer-gaixie"></iconpark-icon>
						<iconpark-icon name="account-circle-fill" color="#fff" size="20"></iconpark-icon>
					</div> -->
					<div class="chatDivFlex-top">
						<div class="logo-box">
							<!-- <img v-if="attributionLogo()" class="left-logo" :src="attributionLogo()" />
							<img v-else src="/src/assets/municipalSupervisionBureau/left-logo.png" class="left-logo" alt="" /> -->
							<img v-if="logoUrl()" class="right-logo" :src="logoUrl()" />
							<img v-else class="right-logo" src="/src/assets/municipalSupervisionBureau/default-new.png" />
						</div>
						<div class="add-box">
							<div class="new-chat" @click="refreshPage">
								<img src="/src/assets/municipalSupervisionBureau/new-chat.png" alt="" />
								新对话
							</div>
							<!-- <iconpark-icon name="account-circle-fill" color="#fff" size="26"></iconpark-icon> -->
						</div>
					</div>
					<div class="chat-content">
						<!-- <div class="top-logo" v-if="!isMobile">
							<img src="/src/assets/municipalSupervisionBureau/add-icon.png" class="add-icon" alt="" @click="refreshPage" />
							<img class="lh-logo" :src="logoUrl() ? logoUrl() : '/src/assets/municipalSupervisionBureau/default-img.png'" />
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
						</div> -->

						<div class="chat-bottom" :class="{ 'chat-bottom-mobile': isMobile }">
							<div class="chat-bottom-content">
								<LayoutCenter @handleFillForm="handleFillForm" />
							</div>
							<!-- <div class="chat-bottom-content" :class="[isSZR && showSZR ? 'pl48' : 'pl48']">
								<LayoutCenter @handleFillForm="handleFillForm" />
							</div> -->
							<!-- 便民服务 -->
							<div v-if="menuServiceFlag || policyListFlag" class="chat-bottom-service">
								<!-- <layoutCenterRight v-if="menuServiceFlag || policyListFlag" /> -->
								<layoutCenterRight />
							</div>
							<!-- 数字人 -->
							<!-- <div
								v-show="isSZR && showSZR"
								class="chat-bottom-people"
								v-loading="szrLoading"
								element-loading-text="加载中..."
								element-loading-spinner="el-icon-loading"
								element-loading-background="rgba(0, 0, 0, 0)"
							>
								<div id="ttsa" style="width: 100%; height: 100%"></div>
							</div> -->
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
			<!-- 底部栏 -->
			<div v-if="aboutWebsite" class="footer-copyrightInfo" :class="{ 'footer-copyrightInfo-mobile': isMobile }">
				<!-- <div v-html="aboutWebsite" class="footer-copyrightInfo-outer"></div> -->
				<div class="footbox p50">
					<div class="width1920">
						<span class="footbox_part1"
							><span style="cursor: pointer" @click="goToOtherLink('http://bszs.conac.cn/sitename?method=show&amp;id=1173ACE1BC20303BE053022819AC389E')"
								><img
									data-v-31ffa5e2=""
									src="https://amr.sz.gov.cn/szsjrobot/img/garment.dbad965b.png"
									width="31"
									height="38"
									alt="党政机关标识"
									class="footimg_1" /></span
							><i data-v-31ffa5e2="">|</i>主办单位：深圳市市场监督管理局（深圳市知识产权局） <em data-v-31ffa5e2="">网站标识码：4403000004</em
							><em data-v-31ffa5e2=""><a href="http://beian.miit.gov.cn/">粤ICP备15042059号</a></em
							><em data-v-31ffa5e2="" @click="goToOtherLink('http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=44030402002947')"
								><span
									class="yuegongwanganbei"
									title="网站备案信息"
									><img src="https://amr.sz.gov.cn/szsjrobot/img/beian.8f2cac31.png" alt="微信公众号简介" width="23" /> 粤公网安备 44030402002947号</span
								></em
							></span
						><i data-v-31ffa5e2="">|</i
						><span class="footbox_part2"
							><span class="footbox_part3"
								><a href="http://amr.sz.gov.cn/fz/wzdt/index.html" target="_blank" title="网站地图">网站地图</a><em data-v-31ffa5e2="">-</em
								><a href="http://amr.sz.gov.cn/fz/wzbz/index.html" target="_blank" title="网站概况">网站概况</a><em data-v-31ffa5e2="">-</em
								><a href="http://amr.sz.gov.cn/fz/bqbh/index.html" target="_blank" title="版权保护">版权保护</a><em data-v-31ffa5e2="">-</em
								><a href="http://amr.sz.gov.cn/fz/yssm/index.html" target="_blank" title="隐私声明">隐私声明</a><em data-v-31ffa5e2="">-</em
								><a href="http://amr.sz.gov.cn/fz/lxwm/content/post_2288275.html" target="_blank" title="联系我们">联系我们</a></span
							></span
						>
						<div data-v-31ffa5e2="">
							<span class="footbox_part1"
								>办公地址：深圳市福田区深南大道7010号工商物价大厦<em data-v-31ffa5e2="">办公时间：09：00-12：00，14：00-18：00（工作日）</em></span
							><a href="http://amr.sz.gov.cn/hdjl/scwwb/index.html" target="_blank"
								><img
									data-v-31ffa5e2=""
									src="https://amr.sz.gov.cn/szsjrobot/img/weibo.5389f594.png"
									alt="政务微博"
									width="26"
									height="23"
									class="footright" /></a
							><a href="http://amr.sz.gov.cn/fz/wxGzh/index.html" target="_blank"
								><img
									data-v-31ffa5e2=""
									src="data:image/png;base64,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"
									alt="微信公众号简介"
									width="24"
									height="19"
									class="footright" /></a
							><a
								data-v-31ffa5e2=""
								href="https://zfwzgl.www.gov.cn/exposure/jiucuo.html?site_code=4403000004&amp;url=http%3A%2F%2Famr.sz.gov.cn%2F"
								target="_blank"
								><img
									data-v-31ffa5e2=""
									src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAG4AAAA3CAYAAADt2n/EAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA2ZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDpGNDgwNTMzRTE3NEVFNjExOEE2RTkyREE0MTA0MDE2NiIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpERjVBQUFEMTRFMTkxMUU2QTZFREU2NEMzMDI4QUMwOSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpERjVBQUFEMDRFMTkxMUU2QTZFREU2NEMzMDI4QUMwOSIgeG1wOkNyZWF0b3JUb29sPSJBZG9iZSBQaG90b3Nob3AgQ1M2IChXaW5kb3dzKSI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOkY0ODA1MzNFMTc0RUU2MTE4QTZFOTJEQTQxMDQwMTY2IiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkY0ODA1MzNFMTc0RUU2MTE4QTZFOTJEQTQxMDQwMTY2Ii8+IDwvcmRmOkRlc2NyaXB0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"
									alt="微信公众号简介"
									width="50"
									height="25"
							/></a>
						</div>
					</div>
				</div>
			</div>
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
const layoutCenterRight = defineAsyncComponent(() => import('./components/layoutCenterRight.vue'));

// 移动端自适应相关
const { isMobile } = useBasicLayout();
const historyDialog = ref(false);

// 底部栏目
const aboutWebsite = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.aboutWebsite;
});

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

const attributionLogo = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.attributionLogo : '';
};

const backgroundImageUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.backgroundImageUrl : '';
};

// 便民服务
const menuServiceFlag = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.menuServiceFlag == '是' ? true : false;
});

// 政策列表（政策文件/政策解读）
const policyListFlag = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.policyListFlag == '是' ? true : false;
});

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
	ttsa
		.setup()
		.then((res: any) => {
			console.log('setup====', res);
		})
		.catch((error: any) => {
			console.log('setup====error', error);
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
		title: 'amr.sz.gov.cn 显示',
		content: `您访问的链接即将离开“深圳市市场监督管理局（深圳市知识产权局）门户网站，是否继续？`,
		closable: true,
		okText: '确定',
		cancelText: '取消',
		hideCancel: false,
		modalClass: 'myConfirm sjj',
		onOk: async () => {
			window.open(url, '_blank');
		},
	});
};
const historyDialogClick = () => {
	historyDialog.value = true;
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
		width: 100% !important;
		// width: 59% !important;
		padding: 56px 72px;
		flex: 1;
	}

	.chatDivFlex {
		// display: flex;
		// justify-content: space-between;
		width: 100%;
		height: 100%;
		// padding: 12px 12px 12px 0;
		overflow: hidden;

		&-top {
			width: 72%;
			height: 70px;
			display: flex;
			justify-content: space-between;
			align-items: center;
			z-index: 2;
			margin-bottom: 8px;
			.logo-box {
				display: flex;
				z-index: 2;
				.left-logo {
					width: 40px;
					height: 40px;
				}
				.right-logo {
					height: 60px;
					margin-left: 10px;
				}
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
		}

		// &-left {
		// 	width: 64px;
		// 	height: 100%;
		// 	padding: 12px 0 22px;
		// 	display: flex;
		// 	flex-direction: column;
		// 	align-items: center;
		// 	justify-content: space-between;

		// 	.left-logo {
		// 		width: 44px;
		// 	}
		// 	.writer-gaixie {
		// 		margin-top: auto;
		// 		margin-bottom: 24px;
		// 	}
		// }
	}

	.chatDivFlex2 {
		position: relative;
		z-index: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		.backgroundImageUrl {
			width: 100%;
			height: 100%;
			position: absolute;
			top: 0;
			left: 0;
		}

		.chat-content {
			// position: absolute;
			// flex: 1;
			// width: 50%;
			width: 72%;
			height: calc(100% - 80px - 22px);
			z-index: 2;
			border-radius: 4px;

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

					&-open {
						display: flex;
						align-items: center;
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
				width: 100%;
				height: 100%;
				border-radius: 4px;
				display: flex;
				justify-content: center;
				align-items: center;

				&-content {
					flex: 1;
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
				&-service {
					height: 100%;
					width: 35%;
				}
			}
		}
	}
}

:deep(.w-layout-sider-light) {
	box-shadow: none;
}

.footer-copyrightInfo {
	width: 100vw;
	// height: 102px;
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
/* 市监局 */
.footbox {
	background: #fff;
	padding: 20px 0;
	color: #666666;
	font-size: 12px;
	text-align: center;
	/* min-width: 1442px; */
	width: 100%;
	height: 100%;
	margin-top: auto;

	.yuegongwanganbei {
		cursor: pointer;
		&:hover {
			text-decoration: underline;
		}
	}
}

.footbox img {
	display: inline-block;
	line-height: 0;
	vertical-align: middle;
}

.footbox i {
	padding: 0 15px;
	font-style: normal;
}

.footbox em {
	padding: 0 5px;
	font-style: normal;
}

.footbox a {
	color: #666666;
}

.footright {
	margin-right: 10px;
}

.sp_nav {
	display: none;
}

.footwx {
	position: relative;
}

.footbox img.wxma2 {
	position: absolute;
	top: -130px;
	left: -80px;
	display: none;
}

.footwx:hover .wxma2 {
	display: block;
}
</style>
<style>
.myConfirm {
	background: #292a2d;
	border: none;
}
.sjj {
	height: 148px;
	width: 468px;
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
