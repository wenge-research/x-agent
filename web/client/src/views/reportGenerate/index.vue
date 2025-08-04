<template>
	<loginDialog v-model:modelValue="dialogVisible" v-if="dialogVisible" @login-success="checkLoginStatus" />
	<!-- 确定退出弹窗 -->
	<el-dialog v-model="outLoginDialogVisible" width="300" center top="35vh">
		<span>
			您确定要退出登录吗?
		</span>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="sureOutLogin">确定</el-button>
				<el-button type="primary" @click="outLoginDialogVisible = false">
					取消
				</el-button>
			</div>
		</template>
	</el-dialog>

	<div class="relative container_chat" :class="wrapClass" @click="openAllDialog">
		
		<div class="user-menu" v-if="isToken && isHovered" @mouseenter="handleMouseEnter"
			@mouseleave="handleMouseLeave">
			<div @click="outLoginDialogVisible = true">退出登录</div>
		</div>
		<w-layout :class="{ MobileH: !isMobile }">

			<w-layout-content v-if="curRouteId == policyUrl && previewData.active && route.params.appId != 0">
				<layoutCenterPdf v-if="previewData.active && route.params.appId != 0" />
			</w-layout-content>
			<w-layout-content v-if="curRouteId == judicialUrl && previewData.active && route.params.appId != 0"
				class="sfLayout">
				<layoutCenterPdf v-if="previewData.active && route.params.appId != 0" />
			</w-layout-content>
			<w-layout-sider v-if="curRouteId == policyUrl" class="flex w-layout-sider-right zcflex"
				:resize-directions="previewData.active && route.params.appId != 0 ? ['left'] : []">
				<div class="chatDivFlex" :class="{ chatDivFlex2: !isMobile, bgorImg: !backgroundImageUrl() }"
					:style="particlesContainerStyle">
					<img v-if="backgroundImageUrl()" :src="backgroundImageUrl()" alt="" class="backgroundImageUrl" />
					<!-- <img v-else src="/src/assets/municipalSupervisionBureau/bg.png" alt="" class="backgroundImageUrl" /> -->
					<!-- <img v-else src="/src/assets/bg13.png" alt="" class="backgroundImageUrl"> -->
					<div class="chatDivFlex-left" v-if="!implantSwitch">
						<!-- <img src="/src/assets/municipalSupervisionBureau/left-logo.png" class="left-logo" alt="" /> -->
						<img v-if="attributionLogo()" class="left-logo" :src="attributionLogo()" />
						<div class="top-logo" v-if="!isMobile">
							<div style="flex: 1">
								<img v-if="logoUrl()" class="lh-logo" :src="logoUrl()" />
							</div>
						</div>
						<div class="footer">
							<div style="display: flex; align-items: center; margin-right: 6px; margin-top: 6px;">
								<div class="top">
									<div class="smallImg" @click="historyDialogClick">
										<!-- 历史对话 -->

										<div class="chat-box" title="历史对话">
											<!-- <img :src="chatLine" alt="历史对话" /> -->
											<iconpark-icon name="chat-history-line" color="#86909C"
												size="20"></iconpark-icon>
										</div>
									</div>
									<div class="bigImg" v-if="getmzsm()">
										<el-popover placement="right-end" :width="400" :show-arrow="false">
											<div class="disclaimer-title" style="margin-left:8px;">免责声明</div>
											<div v-html="getmzsm()" class="disclaimer-txt" style="margin-left:8px;">
											</div>
											<template #reference>
												<iconpark-icon name="information-line" size="20"
													color="#3F4247 "></iconpark-icon>
											</template>
										</el-popover>
									</div>
								</div>

								<div class="smallImg"
									style="display: flex;justify-content: center;align-items: center;">
									<el-popover class="box-item" :width="216" trigger="click" placement="right-start">
										<!-- v-show="showliderValue" -->
										<div @mouseleave="mouseleave" class="font-box">
											<div style="font-family: MiSans, MiSans;
						font-weight: 500;
						font-size: 14px;
						color: #383d47;
						line-height: 20px;">字号大小</div>
											<div class="font-box-slider">
												<el-slider v-model="sliderValue" :step="1" show-stops
													:marks="sliderMarks" :min="0" :max="2" :show-tooltip="false"
													@change="sliderChange">
												</el-slider>
											</div>
											<div style="height: 15px;"></div>
										</div>
										<template #reference>
											<div>
												<iconpark-icon class="ishover" name="font-size-2" color="#2065D6"
													size="20" v-show="showliderValue"
													@click="font = true"></iconpark-icon>
												<iconpark-icon class="nothover" name="font-size-2" color="#383D47"
													size="20" v-show="!showliderValue"></iconpark-icon>
											</div>
										</template>

									</el-popover>
								</div>
							</div>
							<div class="bigImg" @mouseenter="handleMouseEnter" @mouseleave="handleMouseLeave">
								<div class="img" @click="toLogin">
									<iconpark-icon name="user-3-fill" color="#797F8A" size="20"></iconpark-icon>
								</div>
							</div>
						</div>
					</div>
					<div class="history" v-if="isHistory && !chatStore.isShare">
			<div class="top">
				<div class="left">历史对话</div>
				<div class="right">
					<iconpark-icon name="close-line" color="#3F4247" size="20"
						@click="isHistory = false"></iconpark-icon>
				</div>
			</div>
			<div class="content">
				<div class="nodata" v-if="!isToken">
					<div class="img">
						<img src="../../assets/nodataImg.png" alt="">
					</div>
					<div class="txt" style="cursor:pointer;" @click="toLogin">
						登录后可查看
					</div>
				</div>
				<div class="data" v-else>
					<historicalDialogue></historicalDialogue>
				</div>
			</div>
		</div>

					<div class="chat-content"
						:class="{ historyBorder: isHistory, number: isNumber, chatEmbed: implantSwitch }"
						v-show="!chatStore.isShare">
						<div class="chat-bottom" :class="{ 'chat-bottom-mobile': isMobile }">
							<div class="chat-bottom-content" :class="[isSZR && showSZR ? '' : '']">
								<!-- 问答 -->
								<LayoutCenter @handleFillForm="handleFillForm" @playVoiceParent="playVoiceParent" />
							</div>
							<div v-if="menuServiceFlag || policyListFlag || (isSZR && showSZR)"
								class="chat-bottom-people"
								:class="[isSZR && showSZR ? 'showSZR' : '', (menuServiceFlag || policyListFlag) && 'lhgpt']">
								<layoutCenterRight v-if="menuServiceFlag || policyListFlag" />
								<div v-show="isSZR && showSZR" class="szr" id="ttsa"
									style="margin-left: 30px; width: 100%; height: 100%">
								</div>
								<img v-if="!szrVisible" style="margin-left: 30px"
									src="/src/assets/municipalSupervisionBureau/virtual-human.png" />
							</div>
						</div>
					</div>
					<div class="numberPeople" v-if="isNumber && !chatStore.isShare">
						<div class="content">
							<div class="txt">
								数字人<br>展示区域
							</div>
						</div>
					</div>
					<div v-if="chatStore.isShare" class="share">
						<div class="share-top">
							<div style="height: 32px;width: 100%;padding:0 368px 0 296px;display: flex;justify-content: space-between;
							align-items: center;">
								<div class="title">
									分享对话
								</div>
								<div class="close" @click="closeShare">
									<iconpark-icon name="close-line" color="#3F4247" size="25"></iconpark-icon>
								</div>
							</div>
						</div>
					</div>
				</div>
			</w-layout-sider>
			<w-layout-sider v-if="curRouteId == judicialUrl" class="flex w-layout-sider-right"
				:style="particlesContainerStyle"
				:resize-directions="previewData.active && route.params.appId != 0 ? ['left'] : []">
				<vue-particles id="wft-tsparticles" :particlesInit="particlesInit" :options="particlesOpt" />
				<div class="chatDivFlex">
					<!-- <LayoutCenter /> -->
				</div>
			</w-layout-sider>
			<!-- xxxxxxx政务问答 -->
		
		</w-layout>

		<el-dialog :show-close="false" v-model="formVisible" width="760px" :close-on-press-escape="false"
			:close-on-click-modal="false">
			<Information :formParams="formParams" @close="closedialogForm" v-if="formVisible" />
		</el-dialog>
		<el-drawer title="历史对话" v-model="historyDialog" direction="rtl" :before-close="handleClose" class="elDrawer"
			size="30%">
			<historicalDialogue :historyDialog="historyDialog" @closeDialog="closeDialog"></historicalDialogue>
		</el-drawer>


	</div>

</template>

<script lang="ts" setup>
import mittBus from '/@/utils/mitt';
import 'splitpanes/dist/splitpanes.css';
import { computed, defineAsyncComponent, onMounted, onUpdated, ref, toRef, watch, nextTick, onUnmounted, provide, h, toRaw } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { useRoute, useRouter } from 'vue-router';
import { useKnowledgeState } from '/@/stores/knowledge';
import { fileTree, userKnowledgesSize } from '/@/api/knowledge';
import { apiGetAuthTokenBySubscriptionNew } from '/@/api/digitalPeople';
import { apiUploadShare } from '/@/api/chat/index'
import { Message, Modal } from 'winbox-ui-next';
import particlesOpt from './config/particles1';
import { loadSlim } from 'tsparticles-slim';
import { SuccessFilled } from '@element-plus/icons-vue'
import chatLine from '/@/assets/ai/chat-history-line.svg';
import html2canvas from 'html2canvas';
import domtoimage from 'dom-to-image';
import axios from 'axios'
const knowledgeState = useKnowledgeState();
const previewData: any = computed(() => knowledgeState.previewData);
const chatStore = useChatStore();
import { formatDate } from '/@/utils/formatTime';
import jsZip from 'jszip'
import { ElMessage } from 'element-plus';
import { uploadMinio } from '/@/api/chat/index'
import useClipboard1 from 'vue-clipboard3'
import QrcodeVue from 'qrcode.vue';
import checkboxline from '/@/assets/checkbox-line-circle.png'
// 使用插件
const { toClipboard } = useClipboard1()
const Information = defineAsyncComponent(() => import('/@/views/information/index-pc.vue'));
const LayoutCenter = defineAsyncComponent(() => import('./components/layoutCenter.vue'));
const layoutCenterPdf = defineAsyncComponent(() => import('./components/layoutCenterPdf.vue'));
const historicalDialogue = defineAsyncComponent(() => import('/@/views/lh-gpt/components/historicalDialogue.vue'));
const loginDialog = defineAsyncComponent(() => import('./components/login.vue'));

// xxxxxxx政务问答
import icon1 from '/@/assets/img/P020180126374347718565.png';
import icon2 from '/@/assets/img/red.png';
import icon3 from '/@/assets/img/wza2022120603.png';
import { blob } from 'stream/consumers';
import { resolve } from 'path';
import { useClipboard } from '@vueuse/core';
import { wgs84togcj02 } from '/@/utils/geolocation';
const layoutCenterRight = defineAsyncComponent(() => import('./components/layoutCenterRight.vue'));
const isSelected = ref([])
provide('select', isSelected)
const qrcodeUrl = ref('')
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
//登录后二级菜单显示隐藏控制
const isHovered = ref(false);
const handleMouseEnter = () => {
	if (isToken.value) {
		isHovered.value = true;
	}
};

const handleMouseLeave = () => {
	isHovered.value = false;
};

//登录组件显示隐藏
const dialogVisible = ref(false);
const isQRcode = ref(false)
const qrcodeValue = ref('https://example.com');
const showDialog = () => {
	dialogVisible.value = true;
};
// 使用 provide 提供 dialogVisible 和 showDialog
provide('dialogVisible', dialogVisible);
provide('showDialog', showDialog);
const isTouch = ref(false)
//分享截图
const checkBoxHidden = ref(true)
const hidden = ref([])
const textToCopy = ref('')
provide('hidden', hidden)
const shareRef = ref(null)
const saveImgFlag = ref(false)
function getRealContentHeight(element) {
	const clone = element.cloneNode(true);
	clone.style.position = 'absolute';
	clone.style.visibility = 'hidden';
	clone.style.height = 'auto';
	document.body.appendChild(clone);
	const height = clone.scrollHeight;
	document.body.removeChild(clone);
	return height + 50;
}
const captureImg2 = async (element) => {
	try {
		const scrollHeight = shareRef.value.scrollHeight
		const domImg = await domtoimage.toBlob(element, {
			height: getRealContentHeight(element),
			quality: 0.5,
			bgcolor: '#ffffff',
			style: {
				transform: `translate(-296px)`
			}
		})
		console.log(domImg, '截图后的图片是什么')
		let dataUrl
		dataUrl = URL.createObjectURL(domImg)
		console.log(dataUrl, '临时的链接数据')
		return {
			dataUrl,
			filename: `分享的图片.png`,
			blob: domImg
		}
	} catch (err) {
		console.log(err)
		return null
	} finally {
		checkBoxHidden.value = true
		isSelected.value.forEach((item, index) => {
			hidden.value[index] = true
			item.checked = false
		})
	}
}
const imgUrl = ref('')
const handleNumber = () => {

	if (isNumber.value == false) {
		initAll()
	}
	isNumber.value = !isNumber.value
}
//根据index顺序排序
const changeSort = (newArr: any) => {
	return newArr.sort((a, b) => {
		return a['sort'] - b['sort']
	})
}
//保存图片
const handleSaveImg = (type) => {
	const isSave = isSelected.value.some(item => {
		return item.checked == true
	})
	if (!isSave) {
		ElMessage({
			type: 'warning',
			message: '请选择至少一张要分享的图片'
		})
		return
	}

	if (type == 'baocun') {
		isSelected.value.forEach((item, index) => {
			if (!item.checked) {
				hidden.value[index] = false
			} else {
				hidden.value[index] = true
			}
		})
		checkBoxHidden.value = false
	}
	nextTick(async () => {
		if (!shareRef.value) return;
		// const zip = new jsZip()
		// const folder = zip.folder('想要分享的图片')
		// const result = await captureImg(shareRef.value);
		if (type == 'baocun') {
			saveImgFlag.value = true
			isQRcode.value = true;
			ElMessage({
				type: 'info',
				message: '图片正在生成，请稍等'
			})
			try {
				const newArr = toRaw(saveSelected.value)
				changeSort(newArr)
				console.log(newArr, '传入分享 API 里的参数')
				let res = await apiUploadShare({ dialogueCacheList: newArr })
				if (res.code == '000000') {
					const shareLink = `【智川】这是我和${JSON.parse(window.localStorage.getItem(`${route.params.appId}`)).applicationName}的聊天对话，你也来试试吧~ 【点击链接直接打开】${window.location.href}?key=${res.data}`
					// 更新二维码的值
					qrcodeValue.value = `${window.location.href}?key=${res.data}`
					console.log(qrcodeValue.value, '二维码的值')
					await toClipboard(shareLink)
					ElMessage({
						type: 'success',
						message: '生成图片分享成功'
					})
					const result = await captureImg2(shareRef.value)
					saveImgFlag.value = false
					console.log(result, '最终的结果是什么呢，让我看看')
					const url = result?.dataUrl
					console.log(url, '什么格式的')
					const link = document.createElement('a')
					link.href = url;
					link.download = '分享的图片.png'
					link.click()
					isQRcode.value = false;
					link.remove()
					URL.revokeObjectURL(url);
				} else {
					console.error('API 返回错误码:', res.code)
					ElMessage({
						type: 'error',
						message: '生成图片分享失败'
					})
				}
			} catch (error) {
				console.error('生成图片分享失败:', error)
				ElMessage({
					type: 'error',
					message: '生成图片分享失败，请稍后重试'
				})
			} finally {
				console.log('最后都要执行的')
				saveSelected.value = []
				// chatStore.isShare = false
				checkBoxHidden.value = true
				isSelected.value.forEach((item, index) => {
					hidden.value[index] = true
					item.checked = false
				})
			}


		}
		if (type == 'fuzhi') {
			try {
				console.log(saveSelected.value instanceof (Array), '来查看一下是不是数组')
				const newArr = toRaw(saveSelected.value)
				changeSort(newArr)
				console.log(newArr, '传入分享API里的参数')
				let res = await apiUploadShare({ dialogueCacheList: newArr })
				console.log(res, '走到这一步了吗')
				if (res.code == '000000') {
					await toClipboard(`【智川】这是我和${JSON.parse(window.localStorage.getItem(`${route.params.appId}`)).applicationName}的聊天对话，你也来试试吧~ 【点击链接直接打开】${window.location.href}` + `?key=${res.data}`)
					ElMessage({
						type: 'success',
						plain: false,
						message: '已复制链接，快去分享吧'
					})
				}
			} catch (error) {
				console.log('失败了', error)
			} finally {
				console.log('最后都要执行的')
				saveSelected.value = []
				// chatStore.isShare = false
				checkBoxHidden.value = true
				isSelected.value.forEach((item, index) => {
					hidden.value[index] = true
					item.checked = false
				})
			}

			// if(res.data.code == '000000'){
			// 	try{
			// 	await	toClipboard(`【智川】这是我和${JSON.parse(window.localStorage.getItem(`${route.params.appId}`)).applicationName}的聊天对话，你也来试试吧~ 【点击链接直接打开】${res.data.data[0].url}`)
			// 	ElMessage({
			// 		type:'success',
			// 		plain:false,
			// 		message:'已复制链接，快去分享吧'
			// 	})
			// 	}catch{
			// 		console.log('失败')
			// 	}
			// 	}
		}

	}
	)
	isSelected.value.forEach(item => {
		item.checked = false
	})
}
const isHistory = ref(false)
const isNumber = ref(false)
const font = ref(false)
const select = ref(false)
const chatList = computed(() => chatStore.chatList
)


//登录
const toLogin = () => {
	if (isToken.value) return;
	isHistory.value = false;
	dialogVisible.value = true;
	console.log('dialogVisible.value', dialogVisible.value)
};

const outLoginDialogVisible = ref(false)

//退出登录
const sureOutLogin = () => {
	outLoginDialogVisible.value = false;
	localStorage.removeItem('userInfo');
	checkLoginStatus();
	Message.success('您已退出登录');
};
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
const saveSelected = ref([]) //用于保存勾选了对话
const handleSelected = (item, index) => {
	console.log(isSelected.value[index], '改变了没有', index, '索引')
	isSelected.value[index].checked = !isSelected.value[index].checked
	const saveSelectedIndex = saveSelected.value.indexOf(isSelected.value[index])
	console.log(saveSelectedIndex)
	if (saveSelectedIndex != -1) {
		saveSelected.value.splice(saveSelectedIndex, 1)
	} else {
		saveSelected.value.push(item)
	}
	console.log(saveSelected.value)
}
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
	ttsa.setup().then((res: any) => {
		console.log("setup====", res)
	}).catch((error: any) => {
		console.log("setup====error", error)
		Message.warning(error.message);
	});
};

// 关闭连接
const closeRoom = () => {
	ttsa?.closeRoom();
	ttsa = null;
	mittBus.off('textToSpeechAndPlay', () => { });
	mittBus.off('stopTalkSAMR', () => { });
	mittBus.off('samrPlaying', () => { });
	mittBus.off('samrEnding', () => { });
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
const getmzsm = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	let disclaimer = appInfo ? appInfo.disclaimer : '暂无数据';
	return disclaimer?.replace(/\n/g, '<br /><p style="margin-bottom: 6px"></p>');
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
	if (virtualHumanType.value == '小冰数字人') {
		if (showSZR.value) {
			if (!signature.value) {
				await getAuthTokenBySubscriptionNew();
			}
			nextTick(() => {
				start();
			});
		} else {
			Client?.endRTC()
			Client = null;
			mittBus.off('szrPlayStart', () => { });
			mittBus.off('stopTalk', () => { });
			mittBus.off('startTalk', () => { });
			mittBus.off('onTalkEnd', () => { });
		};
	} else {
		if (showSZR.value) {
			nextTick(async () => {
				await initSzr();
			});
		} else {
			closeRoom();
		}
	}
};

//判断是否登录
const isToken = ref(false);
const checkLoginStatus = () => {
	const storedUserInfoString = localStorage.getItem('userInfo');
	if (storedUserInfoString) {
		const storedUserInfo = JSON.parse(storedUserInfoString);
		isToken.value = storedUserInfo?.accessToken ? true : false;
	} else {
		isToken.value = false;
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
// const historyFlag = computed(() => {
// 	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
// 	return appInfo?.historyFlag == '是' ? true : false;
// });
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

// const refreshPage = () => {

// 	if (chatList.value.length) {
// 		chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
// 	} else {
// 		const bottomDistance = 204
// 		const chat = document.querySelector('.chat-bottom-content')
// 		console.log(chat, '组件是什么啊')
// 		ElMessage({
// 			message: '已是最新对话',
// 			icon: h('img', { src: checkboxline, style: { width: '100%', height: '100%' } }),
// 			customClass: 'el-message-addChat',
// 			offset: document.documentElement.clientHeight - bottomDistance,
// 			repeatNum: 0,
// 			grouping: true,
// 			appendto: chat,
// 		})
// 	}


// };

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
	// historyDialog.value = true;
	if (isHistory.value == false) {
		initAll()
	}
	isHistory.value = !isHistory.value;

};
const initAll = () => {
	isHistory.value = false;
	isNumber.value = false
	chatStore.isShare = false
}
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

// 判断是否为嵌入模式
const implantSwitch = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.implantSwitch == '是' ? true : false;
})

//判断是否模块是否需要登录
const isLoginModlue = () => {
	const pcAuthChannelCode = ref('');
	pcAuthChannelCode.value = route.meta.pcAuthChannelCode;
	const webToken = sessionStorage.getItem('manageAccessToken');
	if (pcAuthChannelCode.value === 'pc_gov' && webToken) {
		const userInfo = {
			user: {},
			accessToken: webToken
		}
		localStorage.setItem('userInfo', JSON.stringify(userInfo));
	}
	const storedUserInfoString = localStorage.getItem('userInfo');
	if (storedUserInfoString) {
		const storedUserInfo = JSON.parse(storedUserInfoString);
		isToken.value = storedUserInfo?.accessToken ? true : false;
	} else {
		isToken.value = false;
	}

	if (pcAuthChannelCode.value === 'pc_gov' && !isToken.value) {
		dialogVisible.value = true;
	}
};
const closeShare = () => {
	// console.log('关闭了吗share')
	saveSelected.value = []
	chatStore.isShare = false;
	console.log(chatStore.isShare, '关闭了吗share');

}
const newChunks = ref('');
const handleVisibilityChange = () => {
	// if (document.visibilityState === 'hidden') {
	// alert('11')
	chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
	// }
};
// 联网检索
const networkFlag = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.networkFlag == '是' ? true : false;
});
onMounted(async () => {
	// checkLoginStatus();
	isLoginModlue();
	if (!route.params.conversationId) {
		await chatStore.addHistory({ appId: route.params.appId }, { name: '新建会话' });
	}
	chatStore.isNewWork = networkFlag.value;
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
	calcPaneSize();
	setInterval(() => {
		date.value = formatDate(new Date(), 'YYYY/mm/dd');
		time.value = formatDate(new Date(), 'HH:MM:SS');
	}, 1000);
	if (virtualHumanType.value == '小冰数字人') {
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
	document.addEventListener('beforeunload', handleVisibilityChange);
});
window.addEventListener('beforeunload', function (event) {

	if (virtualHumanType.value == '小冰数字人') {
		Client?.endRTC();
	} else {
		closeRoom();
	}

});
onUnmounted(() => {
	if (virtualHumanType.value == '小冰数字人') {
		Client?.endRTC();
	} else {
		closeRoom();
	}
	document.removeEventListener('beforeunload', handleVisibilityChange);
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
@keyframes shake {
	0% {
		transform: rotate(0deg);
	}

	25% {
		transform: rotate(-2deg);
	}

	50% {
		transform: rotate(0deg);
	}

	75% {
		transform: rotate(2deg);
	}

	100% {
		transform: rotate(0deg);
	}
}

.bgorImg {
	background-color: #EDF1F5;
}

.hide {
	display: none;
}

.yinchang {
	display: none;
}

.yinchang {
	display: none;
}

.historyBorder {
	border-radius: 0 16px 16px 0 !important;
}

.number {
	border-radius: 16px 0 0 16px !important;
}

.chatEmbed {
	background: transparent !important;
	border-radius: 0 !important;
}

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
	position: relative;

	.user-menu {
		background-color: #fff;
		position: absolute;
		left: 56px;
		bottom: 46px;
		box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.1);
		border-radius: 8px;
		box-sizing: border-box;
		padding: 12px;
		z-index: 99;
	}

	.user-menu:hover {
		cursor: pointer;
		color: #2065d6;
	}
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

	&>.splitpanes__splitter:before,
	&>.splitpanes__splitter:after {
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

// .container_chat :deep(.w-layout-sider-left) {
// 	width: 300px;
// 	min-width: 300px;
// 	max-width: 500px;
// 	box-shadow: 2px 0 6px rgb(0 21 41 / 1%);
// }

// .container_chat :deep(.w-layout-content) {
// 	flex: 1;
// 	max-width: calc(100vw - 1050px);
// }

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
		flex-direction: column;
		justify-content: end;
		width: 100%;
		height: 100%;
		padding: 8px 8px 8px 0;
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
			width: 100%;
			// height: 100%;
			// padding: 12px 0 22px;
			display: flex;
			// flex-direction: column;
			justify-content: space-between;
			align-items: center;
			z-index: 2;

			.top-logo {
				width: 100%;
				height: 40px;
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin-top: 10px;
				margin-left: 30px;

				.newTalk {
					display: flex;
					height: 40px;
					width: 108px;
					align-items: center;
					background: rgba(23, 71, 229, 0.1);
					// opacity: 0.1;
					backdrop-filter: blur(4px);
					border-radius: 8px;

					.img {
						margin-left: 18px;
						margin-right: 8px;
						width: 18px;
						height: 18px;
						display: flex;
						align-items: center;
						justify-content: center;
					}

					.txt {
						font-family: MiSans, MiSans;
						font-weight: 400;
						font-size: 16px;
						line-height: 20px;
						text-align: left;
						font-style: normal;
						text-transform: none;
						color: #1747E5;
					}

					&:hover {
						cursor: pointer;
						background: #D8E0FB;

						img {
							.add-icon {
								color: #1747E5 !important;
							}
						}

						// .txt{
						// 	color: #1747E5;
						// }
					}
				}

				.add-icon {
					width: 24px;
					height: 24px;
					// margin-right: 20px;
					margin-right: 3px;
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
						// margin-right: 24px;
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

					.el-popper {
						height: 150px !important;
					}

					.font-box {
						// position: absolute;
						// right: 0px;
						// top: 40px;
						width: 216px;
						height: 95px;
						background: #ffffff;
						border-radius: 8px;
						// padding: 12px 16px;
						z-index: 2;
						padding: 0 !important;

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

			.top {
				// height: 152px;
				display: flex;
				flex-direction: column;
				align-items: center;

				.smallImg {
					width: 56px;
					height: 56px;
					display: flex;
					flex-direction: column;
					align-items: center;
					justify-content: center;

					&:hover {
						background: rgb(134, 144, 156, 0.1);
						backdrop-filter: blur(4px);
						cursor: pointer;
						width: 56px;
						height: 56px;
						border-radius: 8px;
					}

					.disclaimer {
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

					.title {
						font-family: MiSans, MiSans;
						font-weight: 400;
						font-size: 12px;
						color: #86909C;
						line-height: 16px;
						text-align: right;
						font-style: normal;
					}
				}
			}

			.footer {
				display: flex;
				// flex-direction: column;
				align-items: center;
				justify-content: end;

				.bigImg {
					width: 40px;
					height: 40px;
					display: flex;
					justify-content: center;
					align-items: center;
					// margin-bottom: 16px;
					// margin-top: 24px;

					&:hover {
						cursor: pointer;
					}

					.img {
						width: 40px;
						height: 40px;
						display: flex;
						justify-content: center;
						align-items: center;
						// border-radius: 50%;
						// background: #EBEDF0;

						&:hover {
							background: rgb(134, 144, 156, 0.1);
							backdrop-filter: blur(4px);
							cursor: pointer;
							width: 40px;
							height: 40px;
							border-radius: 50%;
						}

						.login {
							font-family: MiSans, MiSans;
							font-weight: 500;
							font-size: 14px;
							color: #797F8A;
							line-height: 20px;
							text-align: left;
							font-style: normal;
						}
					}
				}

				.smallImg {
					width: 40px;
					height: 40px;
					display: flex;
					justify-content: center;
					align-items: center;

					&:first-child {
						// margin-top: 8px;
					}

					&:hover {
						background: rgb(134, 144, 156, 0.1);
						backdrop-filter: blur(4px);
						cursor: pointer;
						width: 40px;
						height: 40px;
						border-radius: 8px;
					}
				}
			}

			.left-logo {
				width: 44px;
			}

			.writer-gaixie {
				margin-top: auto;
				margin-bottom: 24px;
			}
		}
	}

	.history {
		position: absolute;
		right: 0;
		top: 0;
		overflow: scroll;
		background: rgba(255, 255, 255,);
		width: 320px;
		height: 100%;
		z-index: 2;
		border-radius: 8px 0px 0px 8px;
		border-right: 1px solid rgba(0, 0, 0, 0.12);

		&::-webkit-scrollbar {
			width: 0;
			/* 隐藏垂直滚动条 */
			height: 0;
			/* 隐藏水平滚动条 */
		}

		.top {
			width: 100%;
			// padding: 16px 24px 0 24px;
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 16px;

			.left {
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 16px;
				color: #3F4247;
				line-height: 24px;
				text-align: left;
				font-style: normal;
			}

			.right {
				width: 32px;
				height: 32px;
				display: flex;
				justify-content: center;
				align-items: center;

				&:hover {
					cursor: pointer;
					background: rgba(105, 105, 106, 0.1);
				}
			}
		}

		.content {
			overflow: scroll;

			.nodata {
				width: 100%;
				height: 100%;
				display: flex;
				flex-direction: column;
				align-items: center;

				.img {
					width: 120px;
					height: 120px;
					display: flex;
					justify-content: center;
					align-items: center;
					margin-top: 111px;

					img {
						width: 100%;
						height: 100%;
					}
				}

				.txt {
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 16px;
					color: #3F4247;
					line-height: 24px;
					text-align: right;
					font-style: normal;
				}
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
			height: 100%;
			border-radius: 16px;
			// padding: 24px 32px 24px 32px;
			z-index: 2;

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

						>div {
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
				height: calc(100% - 40px);
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
				// justify-content: center;
				// align-items: center;

				@media screen and (min-width:0) and (max-width:925px) {
					&-content {
						flex: 1;
						height: 100%;
						margin: 0 !important;
					}

					.pl48 {
						padding-left: 0 !important;
					}
				}

				@media screen and (min-width:925px)and (max-width:928px) {
					&-content {
						flex: 1;
						margin: 0 24px;
						height: 100%;
					}
				}

				@media screen and (min-width:928px) and (max-width:1920px) {
					&-content {
						width: 800px;
						height: 100%;
					}
				}

				@media screen and (min-width:1920px) {
					&-content {
						width: 800px;
						height: 100%;
					}
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

:deep(.el-slider) {
	padding: 10px;

	.el-slider__runway {
		background: #e1e4eb;

		.el-slider__button {
			width: 16px !important;
			height: 16px !important;
			border: none !important;
			background: #2065d6 !important;
		}
	}

	.el-slider__stop {
		width: 10px;
		height: 10px;
		margin-top: -2px;
		background: #e1e4eb;
	}

	.el-slider__bar {
		background: #e1e4eb;
	}

}

.numberPeople {
	height: 100%;
	width: 552px;
	background: rgba(255, 255, 255, 0.6);
	z-index: 2;
	border-radius: 0 16px 16px 0 !important;

	.content {
		width: 100%;
		height: 100%;
		padding: 16px 16px 16px 0;
		z-index: 3;
		background-color: #c2d6f4;
		background-clip: content-box;
		display: flex;
		justify-content: center;
		align-items: center;

		.txt {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 16px;
			color: #000000;
			line-height: 22px;
			text-align: right;
			font-style: normal;
		}
	}
}

.share {

	display: flex;
	flex-direction: column;
	flex: 1;
	height: 100%;
	z-index: 2;
	background: #FFFFFF;
	border-left: 1px solid rgba(0, 0, 0, 0.12);

	.share-top {
		display: flex;
		height: 64px;
		align-items: center;
		border-bottom: 1px solid rgba(0, 0, 0, 0.12);
		;

		.title {
			font-family: MiSans, MiSans;
			font-weight: 500;
			font-size: 20px;
			color: #3F4247;
			line-height: 28px;
			text-align: left;
			font-style: normal;
		}

		.close {
			width: 32px;
			height: 32px;
			display: flex;
			justify-content: center;
			align-items: center;

			&:hover {
				cursor: pointer;
			}
		}
	}

	&-content {
		margin: 0 368px 0 296px;
		flex: 1;
		overflow: scroll;
		scrollbar-width: none;
		/* Firefox */
		-ms-overflow-style: none;

		/* IE 和 Edge */
		.main {
			position: relative;
			background: #F4F6F9;
			border-radius: 6px;
			padding: 3px 16px 10px 40px;
			margin-top: 15px;
			width: 100%;

			&:first-child {
				margin-top: 8px;
			}
		}

		.share-content {
			padding-top: 28px;
		}

		.share-content-title {
			position: relative;
			background: #F4F6F9;
			border-radius: 8px;
			// padding: 3px 16px 10px 40px;
			padding-top: 28px;
			width: 100%;
			height: 68px;
			top: 20px;
		}
	}

	&-footer {
		border-top: 1px solid rgba(0, 0, 0, 0.12);
		;
		height: 64px;
		width: 100%;

		.left {
			width: 10px;
		}

		.right {
			width: 216px;
			height: 32px;
			display: flex;
			justify-content: space-between;
		}
	}

	.checkBox {
		width: 18px;
		height: 18px;
		position: absolute;
		top: 24px;
		left: 16px;
		z-index: 3;
		display: flex;
		justify-content: center;
		align-items: center;
		border-radius: 2px;

		&:hover {
			cursor: pointer;
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

.footer-copyrightInfo-mobile {}

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

.el-badge {
	display: none !important;
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
<style lang="scss">
.el-message-addChat {
	left: 50%;
	width: 160px;
	height: 40px;
	background: rgba(51, 51, 51, 0.8);
	border-radius: 8px;
	backdrop-filter: blur(4px);
	transform: translate(-28%);

	.el-message__icon {
		width: 20px;
		height: 20px;
	}

	.el-message__content {
		height: 22px;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #FFFFFF;
		line-height: 22px;
		text-align: left;
		font-style: normal;
	}
}
</style>