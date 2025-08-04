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
					<div class="chatHead" v-if="!isMobile">
						<div class="top-logo">
							<img v-if="logoUrl()" class="tz-logo" :src="logoUrl()" />
							<!-- <img v-else class="tz-logo" src="/src/assets/chatImages/tz-logo.png" /> -->
							<div class="right">
								<div class="voice-setting" v-if="hasStreamVoice()">
				                  <iconpark-icon name="volume-down-line" v-if="chatStore.streamVoiceFlag " @click="stopPlayVoice" size="24" color="#181b49"></iconpark-icon>
				                  <iconpark-icon name="volume-mute-line" v-else @click="chatStore.streamVoiceFlag = true;" size="24" color="#181b49"></iconpark-icon>
				                </div>
				                <div class="font-setting">
									<!-- <div>
										字号设置：<span @click="changeFontSize('2')" :style="{ color: sizeType === '2' ? '#169E9A' : '#fff' }">大</span> |
										<span @click="changeFontSize('1')" :style="{ color: sizeType === '1' ? '#169E9A' : '#fff' }">中</span> |
										<span @click="changeFontSize('0')" :style="{ color: sizeType === '0' ? '#169E9A' : '#fff' }">小</span>
									</div> -->
									<!-- <iconpark-icon name="font-size-2" size="20" color="#fff"></iconpark-icon> -->
									<!-- <div>
										<div>大</div>
										<div>中</div>
										<div>大小</div>
									</div> -->
									<!-- <iconpark-icon name="account-circle-fill" size="20" color="#fff" class="account-circle-fill"></iconpark-icon>
									<span>欢迎您，微信用户</span> -->
								</div>
								<!-- <div class="time_box">
									<div>{{ date }}&nbsp;&nbsp;</div>
									<div>{{ time }}</div>
								</div> -->
							</div>
						</div>
					</div>
					<div class="chat-content">
						<div class="chat-bottom" :class="{ 'chat-bottom-mobile': isMobile }">
							<LayoutCenter @handleFillForm="handleFillForm" />
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
	</div>
</template>

<script lang="ts" setup>
import 'splitpanes/dist/splitpanes.css';
import { computed, defineAsyncComponent, onMounted, onUpdated, ref, watch } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { useRoute, useRouter } from 'vue-router';
import { useKnowledgeState } from '/@/stores/knowledge';
import { fileTree, userKnowledgesSize } from '/@/api/knowledge';
import { Message, Modal } from 'winbox-ui-next';
import particlesOpt from './config/particles1';
import { loadSlim } from 'tsparticles-slim';
import { stopPlay } from '/@/utils/newVoiceFun';
const knowledgeState = useKnowledgeState();
const previewData: any = computed(() => knowledgeState.previewData);
const chatStore = useChatStore();
import { formatDate } from '/@/utils/formatTime';
const Information = defineAsyncComponent(() => import('/@/views/information/index-pc.vue'));
const LayoutCenter = defineAsyncComponent(() => import('/@/views/chat/components/layoutCenterTz.vue'));
const layoutCenterPdf = defineAsyncComponent(() => import('./components/layoutCenterPdf.vue'));
// 移动端自适应相关
const { isMobile } = useBasicLayout();

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
const stopPlayVoice = () => {
  chatStore.streamVoiceFlag = false;
  stopPlay()
};
const hasStreamVoice = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
 	return appInfo && appInfo.streamVoice === '是';
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
	// window.removeEventListener('resize', checkDevice);
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
		overflow: hidden;
	}

	.chatHead {
		width: 100%;
		height: 72px;
		padding: 0 40px;

		.top-logo {
			width: 100%;
			height: 100%;
			margin-bottom: 1%;
			display: flex;
			justify-content: space-between;
			align-items: center;
			> div {
				height: 100%;
				display: flex;
				align-items: flex-end;
			}

			.gh {
				height: 100%;
				margin-right: 22px;
			}

			.tz-logo {
				height: 61.2%;
				cursor: pointer;
			}

			.right {
				display: flex;
				align-items: center;

				.voice-setting{
		            position: relative;
		            right: 10px;
		            top:4px;
		            cursor: pointer;

		          }
		          .font-setting {
					margin-right: 18px;
					margin-top: 8px;
					color: #fff;
					display: flex;
					align-items: center;

          .account-circle-fill {
            margin: 0 14px 0 16px;
          }

					> div {
						width: 170px;
						height: 28px;
						// background: #edf8fd;
						border-radius: 18px 18px 18px 18px;
						border: 1px solid #181b49;
						font-size: 12px;
						font-family: MicrosoftYaHei;
						color: #332f31;
						display: flex;
						justify-content: center;
						align-items: center;

						// margin-top: 52px;
						span {
							cursor: pointer;
							margin: 0 5px;
						}
					}
				}

				.time_box {
					height: 28px;
					// padding: 0 8px;
					line-height: 28px;
					// font-size: 24px;
					// font-family: DINPro-Medium, DINPro;
					// font-weight: 500;
					// color: rgba(24, 27, 73, 1);
					display: flex;
					align-items: center;
					font-family: DINPro, DINPro;
					font-weight: 700;
					font-size: 18px;
					color: #181b49;
				}

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
					margin-right: 8px;
					cursor: pointer;

					img {
						width: 14px;
						height: 16px;
						margin-right: 4px;
					}
				}
			}
		}

		.tz-logo {
			width: 236px;
		}
	}

	.chatDivFlex2 {
		height: calc(100% - 85px);
		position: relative;
		background: url(../../assets/zc/tz-bg.png) no-repeat;
		background-size: 100% 100%;

		.chat-content {
			position: absolute;
			width: 50%;
			height: calc(100vh - 72px);
			padding: 15px 0 55px;
			top: 0;
			left: 50%;
			transform: translateX(-50%);

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

					.voice-setting{
			            position: relative;
			            right: 10px;
			            top:4px;
			            cursor: pointer;

			          }
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
				position: absolute;
				left: 0;
				// top: 20%;
				top: 9%;
				width: 100%;
				// height: 78%;
				height: 88%;
				min-width: 700px;
				// background: rgba(255, 255, 255, 0.6);
				// box-shadow: 0px 0px 10px 0px #357df2;
				// border-radius: 30px 30px 30px 30px;
				// opacity: 1;
				// border: 1px solid #ffffff;
				// background: rgba(225, 233, 245, 1);
				border-radius: 16px;
				// border: 1px solid #ffffff;
				backdrop-filter: blur(1px);
				display: flex;
				justify-content: center;
				align-items: center;
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
</style>
