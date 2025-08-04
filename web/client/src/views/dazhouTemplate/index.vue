<template>
	<div class="container_chat relative" :style="{ backgroundImage: 'url(' + (backgroundImageUrl() ? backgroundImageUrl() : '/src/assets/municipalSupervisionBureau/bg.png') + ')' }"  :class="wrapClass" @click="openAllDialog">
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
					<!-- <img v-if="backgroundImageUrl()" :src="backgroundImageUrl()" alt="" class="backgroundImageUrl" />
					<img v-else src="/src/assets/municipalSupervisionBureau/bg.png" alt="" class="backgroundImageUrl" /> -->
					<div class="chatDivFlex-left">
						<!-- <img src="/src/assets/municipalSupervisionBureau/left-logo.png" class="left-logo" alt="" /> -->
						<img v-if="attributionLogo()" class="left-logo" :src="attributionLogo()" />
						<iconpark-icon name="writer-gaixie" color="#fff" size="20" class="writer-gaixie"></iconpark-icon>
						<iconpark-icon name="account-circle-fill" color="#fff" size="20"></iconpark-icon>
					</div>
          <startPage v-if="!showLayoutCenter" @sendStartParams="sendStartParams"></startPage>
					<div class="chat-content" v-else>
						<div class="top-logo 666" v-if="!isMobile">
							<!-- <el-tooltip content="新建对话">
								<img src="/src/assets/municipalSupervisionBureau/add-icon.png" class="add-icon" alt="" @click="refreshPage" />
							</el-tooltip> -->

							
							<img v-if="logoUrl()" @click="showLayoutCenter = false" class="lh-logo 22" :src="logoUrl()" />
							<div class="right">
								<!-- 返回旧版 -->
								<div v-if="backOldVersionLink" class="change-box">
									<CoolBanbenqiehuan size="16" color="#4085F4" />
									<span @click="backOld">返回旧版</span>
								</div>
								<!-- 调节字体大小 -->
								<div @click="mouseenter" class="set-font" style="margin-right: 24px">
									<iconpark-icon class="ishover" name="font-size-2" color="#2065D6" size="16"></iconpark-icon>
									<iconpark-icon class="nothover" name="font-size-2" color="#383D47" size="16"></iconpark-icon>

									<div v-show="showliderValue" @mouseleave="mouseleave" class="font-box">
										<div>字号大小</div>
										<div class="font-box-slider">
											<el-slider
												v-model="sliderValue"
												:step="1"
												show-stops
												:marks="sliderMarks"
												:min="0"
												:max="2"
												:show-tooltip="false"
												@change="sliderChange"
											>
											</el-slider>
										</div>
									</div>
								</div>
								<!-- 历史对话 -->
								<!-- <div v-if="historyFlag" class="chat-box" @click="historyDialogClick" title="历史对话">
									<img :src="chatLine" alt="历史对话" />
								</div> -->
								<!-- 市监局数字人 -->
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
								<LayoutCenter @handleFillForm="handleFillForm" @playVoiceParent="playVoiceParent" />
							</div>
							<div
								v-if="menuServiceFlag || policyListFlag || (isSZR && showSZR)"
								class="chat-bottom-people"
								:class="[isSZR && showSZR ? 'showSZR' : '', (menuServiceFlag || policyListFlag) && 'lhgpt']"
							>
								<layoutCenterRight v-if="menuServiceFlag || policyListFlag" />
								<div v-show="isSZR && showSZR" class="szr" id="ttsa" style="margin-left: 30px; width: 100%; height: 100%"></div>
								<img v-if="!szrVisible" style="margin-left: 30px" src="/src/assets/municipalSupervisionBureau/virtual-human.png" />
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
			<!-- xxxxxxx政务问答 -->
			<div v-if="aboutWebsite" class="footer-copyrightInfo" :class="{ 'footer-copyrightInfo-mobile': isMobile }">
				<div v-html="aboutWebsite"></div>
				<!-- <div class="copyrightInfo-inner">
					<div class="copyright-left" style="width: 495px">
						<p>
							<a href="https://www.szlhq.gov.cn/lhxinqu/fzlm/gywm/" title="关于我们" target="_blank">关于我们</a>&nbsp;&nbsp;|&nbsp;&nbsp;
							<a href="https://www.szlhq.gov.cn/lhxinqu/fzlm/wzdt/" title="网站地图" target="_blank">网站地图</a>&nbsp;&nbsp;|&nbsp;&nbsp;
							<a href="https://www.szlhq.gov.cn/lhxinqu/fzlm/wzsm/" title="版权保护" target="_blank">版权保护</a>&nbsp;&nbsp;|&nbsp;&nbsp;
							<a href="https://www.szlhq.gov.cn/lhxinqu/fzlm/ysbh/" title="隐私声明" target="_blank">隐私声明</a>
							&nbsp;&nbsp;|&nbsp;&nbsp;
							<a href="https://www.szlhq.gov.cn/wzasm/index.html" target="_blank" title="无障碍声明"><span>无障碍声明</span></a>
						</p>
						<p>深圳市xxxxxxx区人民政府办公室主办<span style="margin-left: 30px; color: #666">网站技术维护电话：0755-23332038</span></p>

						<p></p>
					</div>

					<div class="copyright-right" style="width: 60%">
						<div class="copyrightR-info">
							<div class="tel">
								<p class="beian">
									<span class="link" @click="goToOtherLink('https://beian.miit.gov.cn/#/Integrated/index')">备案许可证号：粤ICP备17147563号-1</span>
								</p>
								<p class="beian" style="display: flex">
									<span
										class="link"
										style="display: flex"
										@click="goToOtherLink('http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=44030902000263')"
										><img
											alt="粤公网安备44030902000263号"
											style="width: 20px; height: 20px; margin-right: 6px"
											aria-hidden="true"
											:src="icon1"
										/>粤公网安备44030902000263号</span
									><span style="margin-left: 6px">网站标识码：4403920006</span>
								</p>
							</div>
							<div class="copyrightR-slogan">
								<span
									><span @click="goToOtherLink('http://bszs.conac.cn/sitename?method=show&amp;id=278CB4A58F6C2E73E053012819AC872B')"
										><img
											id="imgConac"
											style="cursor: pointer"
											vspace="0"
											hspace="0"
											border="0"
											:src="icon2"
											alt="党政机关"
											data-bd-imgshare-binded="1" /></span
								></span>
								<span id="_span_jiucuo"
									><span
										@click="
											goToOtherLink(
												'https://zfwzgl.www.gov.cn/exposure/jiucuo.html?site_code=4403920006&url=http%3A%2F%2Fwww.szlhq.gov.cn%2Fxxgk%2Findex.html'
											)
										"
										><img
											style="width: 92px; margin: 0; border: 0; cursor: pointer"
											src="https://zfwzgl.www.gov.cn/exposure/images/jiucuo.png?v=4403920006"
											role="imagelink"
											tabindex="0"
											aria-label="政府网站找错"
											alt="政府网站找错"
											setedaria="true"
											id="aria2tq9wcbrpf8"
											class="_e0f2a83eeffae7d02f75f35e50a5cac6" /></span
								></span>
								<span><img :src="icon3" alt="无障碍" /></span>
							</div>
						</div>
					</div>
				</div> -->
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
import mittBus from '/@/utils/mitt';
import 'splitpanes/dist/splitpanes.css';
import { computed, defineAsyncComponent, onMounted, onUpdated, ref, watch, nextTick, onUnmounted } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { useRoute, useRouter } from 'vue-router';
import { useKnowledgeState } from '/@/stores/knowledge';
import { fileTree, userKnowledgesSize } from '/@/api/knowledge';
import { apiGetAuthTokenBySubscriptionNew } from '/@/api/digitalPeople';
import { Message, Modal } from 'winbox-ui-next';
import particlesOpt from './config/particles1';
import { loadSlim } from 'tsparticles-slim';
import chatLine from '/@/assets/ai/chat-history-line.svg';
const knowledgeState = useKnowledgeState();
const previewData: any = computed(() => knowledgeState.previewData);
const chatStore = useChatStore();
import { formatDate } from '/@/utils/formatTime';
const Information = defineAsyncComponent(() => import('/@/views/information/index-pc.vue'));
const LayoutCenter = defineAsyncComponent(() => import('./components/layoutCenter.vue'));
const layoutCenterPdf = defineAsyncComponent(() => import('./components/layoutCenterPdf.vue'));
const historicalDialogue = defineAsyncComponent(() => import('/@/views/lh-gpt/components/historicalDialogue.vue'));

// xxxxxxx政务问答
import icon1 from '/@/assets/img/P020180126374347718565.png';
import icon2 from '/@/assets/img/red.png';
import icon3 from '/@/assets/img/wza2022120603.png';
import bottom from '/@/assets/mobileDazhouTemplate/bottom.png';
const layoutCenterRight = defineAsyncComponent(() => import('./components/layoutCenterRight.vue'));
const startPage = defineAsyncComponent(() => import('./components/startPage.vue'));

// 移动端自适应相关
const { isMobile } = useBasicLayout();
const historyDialog = ref(false);
// 判断应用
const isLhgpt = ref(true);
const sliderValue = ref(0);
const sliderMarks = ref({
	0: '常规',
	1: '较大',
	2: '最大',
});

// 虚拟人类型
const virtualHumanType = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.virtualHumanId == 'xxxxxxx' ? '市监局数字人' : '小冰数字人';
});

// 市监局数字人
let ttsa = null;
const serverUrl = ref('https://open.xmov.ai/api');
const username = ref('aaaa');
const appId = ref('xxxxxxx');
const appSecret = ref('560afa7e94a34907b4a05002a0a26965');

const currentIndex = ref(0);
const oldChunks = ref([]);
const isPlaying = ref(false);
const playIndex = ref(0); // 用于跟踪当前播放的音频块索引

const initSzr = () => {
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
			}
		},
		webrtcCallback: (online: any) => {
			console.log('online: ', online);
			if (!online) {
				closeRoom();
			}
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
	});
};

// 关闭连接
const closeRoom = () => {
	ttsa?.closeRoom();
	ttsa = null;
	mittBus.off('textToSpeechAndPlay', () => {});
	mittBus.off('stopTalkSAMR', () => {});
	mittBus.off('samrPlaying', () => {});
	mittBus.off('samrEnding', () => {});
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

const signature = ref('');
// const projectId = ref('c42d6a35-884b-11ef-b3d0-bf8debdda611');
// 闻歌数字人
const projectId = ref('0d35d490-ac74-11ef-8ed4-6faaa3762e9b');
const oldClientId = ref('');
let Client = null;
const szrVisible = ref(true);
// 闻歌数字人
const start = (e, value: string) => {
	console.log('e', e);
	if (!signature.value || !projectId.value) {
		alert('请输⼊签名和项目ID');
		return;
	}
	console.log("Client", Client)
	Client = new window.RTCInteraction({
		mountClass: 'szr',
		signature: signature.value,
		projectId: projectId.value,
		onError(errorMes) {
			console.log(errorMes, 'errorMes');
			Message.warning(errorMes?.message)
			showSZR.value = false;
		},
		onInited(res) {
			console.log('inited', res);
			Client?.startRTC();
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
// const closeSZR = () => {
// 	console.log('关闭>>>>>>>>>>>>>>>>');
// 	Client.endRTC();
// };
// const initSZR = () => {
// 	console.log('初始>>>>>>>>>>>>>>>>');
// 	Client.startRTC();
// };
// const playSZR = (value:string) => {
// 	console.log('讲话>>>>>>>>>>>>>>>>', value);
// 	Client.talk(value, false);
// };
const playVoiceParent = (value: string) => {
	// console.log('讲话>>>>>>>>>>>>>', value);
	// console.log('讲话>>>>>>>>>>>>>Client', Client);
	// if (Client?.ele) {
	// 	Client.talk(value, false);
	// 	mittBus.emit('startTalk');
	// }
};

// 新代码逻辑
const showSZR = ref(false);

const showSZRHandler = async () => {
	showSZR.value = !showSZR.value;
	if(virtualHumanType.value == '小冰数字人') {
		if(showSZR.value) {
			if(!signature.value) {
				await getAuthTokenBySubscriptionNew();
			}
			nextTick(() => {
				start();
			});
		} else {
			Client?.endRTC()
			Client = null;
			mittBus.off('szrPlayStart', () => {});
			mittBus.off('stopTalk', () => {});
			mittBus.off('startTalk', () => {});
			mittBus.off('onTalkEnd', () => {});
		};
	} else {
		if(showSZR.value) {
			nextTick(async () => {
				await initSzr();
			});
		} else {
			closeRoom();
		}
	}
};

// 获取数字人token
const getAuthTokenBySubscriptionNew = async () => {
	let res = await apiGetAuthTokenBySubscriptionNew();
	console.log('res=========', res);
	signature.value = res.data.data;
};

// 是否显示虚拟人
const isSZR = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.virtualHumanFlag == '是' ? true : false;
});

// 返回旧版
const backOldVersionLink = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.backOldVersionLink;
});
// 历史对话
const historyFlag = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.historyFlag == '是' ? true : false;
});
// 底部栏目
const aboutWebsite = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.aboutWebsite;
});
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

const showliderValue = ref(false);
const sliderChange = (size: string) => {
	window.document.documentElement.setAttribute('data-size', size);
};
const mouseenter = () => {
	showliderValue.value = !showliderValue.value;
};
const mouseleave = () => {
	showliderValue.value = false;
};

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

const attributionLogo = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.attributionLogo : '';
};

const logoUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.logo : '';
};

const backgroundImageUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.backgroundImageUrl : '';
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
const showLayoutCenter = ref(false);
const sendStartParams = (text, model) => {
	showLayoutCenter.value = true;
};
const time = ref('');
const date = ref('');
const mainDomW = ref(0);
const curRouteId = ref('');

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
// 点击返回旧版
const backOld = () => {
	window.open(backOldVersionLink.value, '_blank');
};
// xxxxxxx政务问答
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
const handleClose = () => {
	historyDialog.value = false;
};
const closeDialog = (val) => {
	historyDialog.value = val;
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
			Client?.talk(ssml, true);
		}
	}, 200);
};
const newChunks = ref('');
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
	if(virtualHumanType.value == '小冰数字人') {
		// 小冰数字人
	if (isSZR.value && showSZR.value) {
		await getAuthTokenBySubscriptionNew();
		nextTick(() => {
			start();
		});
	}

	mittBus.on('szrPlayStart', ({ chunksList, clientId }) => {
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
	} else {
		// 市监局数字人
		if (isSZR.value && showSZR.value) {
			nextTick(async () => {
				await initSzr();
			});
		}

	mittBus.on('textToSpeechAndPlay', ({ newChunks, clientId }) => {
		if (oldClientId.value !== clientId) {
			if (oldClientId.value) {
				currentIndex.value = 0;
				oldChunks.value = [];
				// 打断
				ttsa?.interrupt();
			}
			oldClientId.value = clientId;
		}
		if (newChunks.length > oldChunks.value.length && ttsa) {
			oldChunks.value = newChunks;
			if (currentIndex.value + 1 == newChunks.length) {
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
	}
	
});
window.addEventListener('beforeunload', function (event) {
	if(virtualHumanType.value == '小冰数字人') {
		Client?.endRTC();
	} else {
		closeRoom();
	}
});
onUnmounted(() => {
	if(virtualHumanType.value == '小冰数字人') {
		Client?.endRTC();
	} else {
		closeRoom();
	}
});
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
  background-repeat: no-repeat;
  background-size: cover;
}

:deep(.MobileH) {
	height: 100%;
	flex-direction: column;
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
	width: 100%;
	background: none;

	&.zcflex {
		padding: 0 0 0 0 !important;
	}

	&.flex {
		width: 100% !important;
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
		position: relative;

		.backgroundImageUrl {
			width: 100%;
			height: 100%;
			position: absolute;
			top: 0;
			left: 0;
			z-index: 1;
		}

		&-left {
			width: 64px;
			height: 100%;
			padding: 12px 0 22px;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: space-between;
			z-index: 2;

			.left-logo {
				width: 44px;
			}
			.writer-gaixie {
				margin-top: auto;
				margin-bottom: 24px;
			}
		}
	}

	.chatDivFlex2 {
		position: relative;
		// background: url(../../assets/municipalSupervisionBureau/bg.png) no-repeat;
		// background-size: 100% 100%;

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
			z-index: 2;
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
				margin-bottom: 24px;

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

					.set-font {
						padding: 9px;
						border-radius: 8px;
						position: relative;

						.nothover {
							display: block;
							height: 16px;
						}
						.ishover {
							display: none;
							height: 16px;
						}
						&:hover {
							background: rgba(32, 101, 214, 0.1);
							.nothover {
								display: none;
								height: 16px;
							}
							.ishover {
								display: block;
								height: 16px;
							}
						}
					}

					.chat-box {
						margin-right: 24px;
						cursor: pointer;
						img {
							width: 16px;
							height: 16px;
						}
					}

					.change-box {
						display: flex;
						justify-content: center;
						align-items: flex-end;
						border-radius: 15px;
						border: 1px solid #2065d6;
						font-size: 14px;
						font-family: PingFangSC, PingFang SC;
						font-weight: 400;
						color: #2065d6;
						padding: 2px 10px;
						cursor: pointer;
						margin-right: 24px;

						img {
							width: 14px;
							height: 16px;
							margin-right: 4px;
						}
					}

					.font-box {
						position: absolute;
						right: 0px;
						top: 40px;
						width: 216px;
						height: 95px;
						background: #ffffff;
						border-radius: 8px;
						padding: 12px 16px;
						font-family: MiSans, MiSans;
						font-weight: 500;
						font-size: 14px;
						color: #383d47;
						line-height: 20px;
						z-index: 2;

						&-slider {
							padding: 0 10px;
						}
					}

					.el-slider__button {
						width: 16px !important;
						height: 16px !important;
						border: none !important;
						background: #2065d6 !important;
					}
					.el-slider__stop {
						width: 10px;
						height: 10px;
						margin-top: -2px;
						background: #e1e4eb;
					}
					.el-slider__runway {
						background: #e1e4eb;
					}
					.el-slider__bar {
						background: #e1e4eb;
					}

					&-open {
						display: flex;
						align-items: center;
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
					width: 55%;
					height: 100%;
				}
				// .showSZR-content {
				// 	width: 60%;
				// }
				.pl48 {
					padding-left: 48px;
				}
				&-people {
					flex: 1;
					height: 100%;
					display: flex;
					align-items: flex-end;
					// padding-top: 24px;

					img {
						width: 388px;
						height: 100%;
					}
				}
				// 市监局数字人形象
				.showSZR {
					padding-right: 18px;
					justify-content: flex-end;
				}
				.lhgpt {
					justify-content: flex-start;
					margin-left: 30px;
				}
			}
		}
	}
}

:deep(.w-layout-sider-light) {
	box-shadow: none;
}

.footer-copyrightInfo {
	height: 10.08%;
	background: #fff;
	z-index: 999999;
	width: 100vw;
	&-outer {
		width: 100%;
	}
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
	float: left;
	/* margin-left:160px; */
}

.copyright-right {
	width: 60%;
	float: right;
}

.copyright-left p,
.copyright-left a,
.copyright-right p {
	font-size: 14px;
	color: #666;
	line-height: 1.8;
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
/* 市监局 */
.footbox {
    background: #191919;
    padding: 6px 0;
    color: #fff;
    font-size: 12px;
    text-align: center;
    /* min-width: 1442px; */
		width: 100%;
    margin-top: auto
}

.footbox img {
    display: inline-block;
    line-height: 0;
    vertical-align: middle
}

.footbox i {
    padding: 0 15px;
    font-style: normal
}

.footbox em {
    padding: 0 5px;
    font-style: normal
}

.footbox a {
    color: #fff
}

.footright {
    margin-right: 10px
}

.sp_nav {
    display: none
}

.footwx {
    position: relative
}

.footbox img.wxma2 {
    position: absolute;
    top: -130px;
    left: -80px;
    display: none
}

.footwx:hover .wxma2 {
    display: block
}
</style>
