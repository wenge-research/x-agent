<!-- eslint-disable vue/no-async-in-computed-properties -->
<template>
	<div class="centerInitItem" :class="{ centerInitItemMobile: isMobile }">
		<div class="avator" v-if="curRouteId == judicialUrl">
			<w-avatar :size="28"><img :src="BotAvatar" /></w-avatar>
		</div>
		<div class="bg" v-if="!isMobile" :class="{ getclass: curRouteId == judicialUrl, bgMobile: isMobile }">
			<div class="suggestList">
				<template v-if="curRouteId == policyUrl">
					<div class="left_box flex">
						<div class="flex">
						</div>
					</div>
				</template>
				<template v-if="curRouteId == judicialUrl&&!isMobile">
					<h2 class="title">您好，我是您的司法问答助手</h2>
					<h3 class="sub_title">请输入您想了解的司法文件名称，或对司法文件相关内容进行提问，以下是一些问题示例：</h3>
					<ul class="sf_ul">
						<li v-for="(item, index) in suggestList" :key="index" @click="sendMessage(item)">{{ index + 1 }}. {{ item }}</li>
					</ul>
				</template>

				<div class="text-[#9A99AA] flex cursor-pointer" v-if="newStatus">
					<span @click="change(true)">
						<CoolRefreshLineWe :class="{ rotate: state.animate }" size="14" />
						重新生成
					</span>
				</div>
			</div>
		</div>
		<div class="avator" v-if="curRouteId == judicialUrl"></div>
	</div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted, reactive } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import mittBus from '/@/utils/mitt';
import { useChatStore } from '/@/stores/chat';
import { useKnowledgeState } from '/@/stores/knowledge';
import { useRoute } from 'vue-router';
import { debounce } from 'lodash-es';
import BotAvatar from '/@/assets/zc/jqr.png';
import { listRecommendQuestion } from '/@/api/knowledge';



const route = useRoute();
const isShow = ref(true);
const chatStore = useChatStore();
const knowledgeState = useKnowledgeState();
const currentLibrary: any = computed(() => knowledgeState.currentLibrary);
const previewData: any = computed(() => knowledgeState.previewData);
const isChat = computed(() => route.path.indexOf('chat') != -1);
const suggestList: any = ref([]);
const policyUrl: any = ref(localStorage.getItem(`${route.params.appId}appId`));
const judicialUrl: any = ref(import.meta.env.VITE_JUDICIAL_QA);
const newStatus = ref(false);
const state = reactive({
	newList: [], //处理后的新数据
	timeStart: 0, //截取第几组的起始参数
	timeEnd: 1, //截取第几组的结束参数
	group: 0, //默认为0组
	num: 3, //每页展示列表的数量
	clickNum: 0, //点击的次数
	animate: false, //切换动画
});

// 移动端自适应相关
// const { isMobile } = useBasicLayout();
const isMobile = ref(false);

const sendMessage = debounce((item, itemValue) => {
	if (chatStore.dialogueLoading) return;
	mittBus.emit('promptInsert', item);
	chatStore.setPolicy(itemValue);
}, 500);
const curRouteId = ref('');
onMounted(() => {
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
	console.log(curRouteId.value,12345);
	
	setTimeout(() => {
		document.querySelector('.center-side').scrollTop = 999999999;
	}, 500);

	watch(
		() => previewData.value.active,
		(val: any) => {
			if (curRouteId.value == judicialUrl.value) {
				getSuggestList();
			}
		},
		{ immediate: true, deep: true }
	);
	watch(
		() => currentLibrary.value.fileCount,
		(val: any) => {
			if (val) {
				if (curRouteId.value == judicialUrl.value) {
					getSuggestList();
				}
			}
		},
		{ immediate: true, deep: true }
	);
});
const getSuggestList = async () => {
	if (isChat.value) {
		return;
	}
	let params = {
		fileIds: previewData.value.active ? [previewData.value.active] : [],
		knowledgeBaseId: sessionStorage.getItem('curRouteId'),
	};
	let res = await listRecommendQuestion(params);
	if (res.code == 200) {
		suggestList.value = res.data;
	}
};
const change = (flag) => {
	if (!isChat.value) {
		if (newStatus.value) {
			newStatus.value = false;
		}
		getSuggestList();
		return;
	}
};



watch(
	() => route.params.appId,
	() => {
		if (route.params.appId && route.params.appId != '') {
			isShow.value = false;
			setTimeout(() => {
				isShow.value = true;
			}, 500);
		}
	}
);
</script>

<style scoped lang="scss">
.centerInitItem {
	display: flex;
	padding-top: 20px;
	.avator {
		width: 36px;
	}
	.bg {
		flex: 1;
		// width: calc(100% - 40px);
		background-size: 100% 100%;
		padding: 20px 24px 0;
		border-radius: 12px;
		//background: linear-gradient(180deg, rgba(217,232,255,0.5) 0%, rgba(255,255,255,0.7) 100%), linear-gradient(180deg, rgba(184,172,255,0.2) 0%, rgba(235,244,252,0) 100%), linear-gradient(180deg, rgba(126,242,255,0.1) 0%, rgba(235,244,252,0) 100%);
	}
	.bgMobile {
		padding: 20px 24px;
	}
	.getclass {
		background: #fff url('/@/assets/zc/card.png') no-repeat;
	}
	&.centerInitItemMobile {
		padding-top: 0;
	}
	.box {
		border-radius: 8px;
		width: 100%;
		padding: 20px;
		overflow: hidden;
		background: url('/@/assets/zc/card.png') #f9f9f9 no-repeat;
		background-size: cover;
	}
	.title {
		height: 40px;
		line-height: 40px;
		font-weight: 500;
		color: #008dd6ff;
		font-size: 20px;
		display: flex;
		align-items: center;
		justify-content: flex-start;
	}
	.subtitle {
		color: #646479;
		font-size: var(--font14) !important;
		text-align: center;
		display: flex;
		justify-content: space-between;
		margin-bottom: 13px;
		.change {
			color: #9a99aa;
			cursor: pointer;
			.cool-icon {
				margin-right: 5px;
			}
			.rotate {
				-webkit-animation: rotate 0.2s linear forwards;
				-moz-animation: rotate 0.2s linear forwards;
				-o-animation: rotate 0.2s linear forwards;
				animation: rotate 0.2s linear forwards;
			}
		}
	}
	.suggestList {
		max-width: 1000px;
		width: 100%;
		border-radius: 12px 12px 0px 0px;
		p {
			font-size: var(--font20);
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #181b49;
			.change {
				i {
					transform: translateY(-200%);
					display: inline-block;
					width: 5px;
					height: 5px;
					border-radius: 50%;
					background: #f54b5b;
				}
				.line {
					transform: translateY(15%);
					display: inline-block;
					width: 1px;
					height: 13px;
					background: #d0d5dc;
					margin: 0 16px;
				}
				span {
					font-size: var(--font12);
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #9a99aa;
					line-height: 12px;
				}
			}
		}
		.department {
			:deep(.w-tabs-content) {
				padding-top: 0 !important;
				height: 280px;
				overflow-y: auto;
			}
			width: 100%;
			height: 333px;
			background: #ffffff linear-gradient(180deg, rgba(217, 232, 255, 0.5) 0%, rgba(244, 249, 255, 0.5) 42%, rgba(255, 255, 255, 0) 100%);
			box-shadow: 0px 2px 4px 0px rgba(18, 47, 83, 0.1);
			:deep(.w-tabs-tab-title) {
				font-size: 16px;
				font-weight: 500;
				color: #181b49;
				line-height: 22px;
			}
			:deep(.w-tabs-content-list) {
				padding: 0 4px;
			}
		}
		.guarantee {
			width: 60%;
			:deep(.w-tabs-content) {
				padding-top: 0 !important;
				height: 416px;
				overflow-y: auto;
			}
			/* width: 510px; */
			background: #ffffff;
			border-radius: 8px;
			:deep(.w-tabs-content-item) {
				padding: 0 4px;
			}
			:deep(.w-tabs-tab) {
				width: calc(15vw - 5px);
				height: 40px;
				background-color: #eaeeff;
				padding: 0 !important;
				margin: 0 !important;
				line-height: 40px;
				border-radius: 0 8px 0 0;
				.w-tabs-tab-title {
					font-size: 16px;
					font-weight: 500;
					margin-left: 50%;
					transform: translate(-50%, 0);
					color: #646479;
				}
			}
			:deep(.w-tabs-tab-active) {
				background-color: #fff;
				.w-tabs-tab-title {
					color: #355eff;
				}
			}
			:deep(.w-tabs-nav::before) {
				height: 0px;
			}
			:deep(.w-tabs-nav-ink) {
				display: none;
			}
		}
		.messageTab {
			border-radius: 8px;
			padding-bottom: 10px;
			ul {
				margin-top: 8px;
				width: 100%;
				box-sizing: border-box;
				list-style-type: disc;
				font-size: 20px; /* 修改项目符号大小为20px */
				color: #ff0000;
				li {
					width: 100%;
					box-sizing: border-box;
					padding: 0 8px;
					list-style: none;
					font-size: var(--font14);
					font-weight: 400;
					color: #646479;
					line-height: 36px;
					border-bottom: 1px dashed #dedede;
					cursor: pointer;
				}
				li:hover {
					background-color: #f5f5f5;
				}
			}
		}
		.sf_ul {
			margin-top: 8px;
			width: 100%;
			box-sizing: border-box;
			li {
				width: 100%;
				box-sizing: border-box;
				padding: 0 8px;
				list-style: none;
				display: block;
				// height: 34px;
				line-height: 34px;
				// background: rgba(244, 246, 249, 0.5);
				background: rgba(53, 94, 255, 0.04);
				border-radius: 8px;
				font-size: var(--font14);
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #355eff;
				cursor: pointer;
				margin-bottom: 10px;
				// overflow: hidden;
				// white-space: nowrap;
				// text-overflow: ellipsis;
			}
			li:hover {
				background: rgba(53, 94, 255, 0.06);
			}
		}
		.sub_title {
			font-size: 16px;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #646479;
			margin: 14px 0;
		}
		.middle-box {
			/* width: 376px; */
			height: 120px;
			background: #ffffff linear-gradient(180deg, rgba(217, 232, 255, 0.5) 0%, rgba(244, 249, 255, 0.5) 42%, rgba(255, 255, 255, 0) 100%);
			box-shadow: 0px 2px 4px 0px rgba(18, 47, 83, 0.1);
			border-radius: 8px;
			padding: 16px;
			margin-bottom: 12px;
			.img-list {
				justify-content: space-between;
				cursor: pointer;
				img {
					width: 108px;
					height: 88px;
				}
			}
		}
	}
	.small {
		font-size: var(--font14);
		padding: 36px 0 32px 0;
	}
	.item {
		background: rgba(53, 94, 255, 0.03);
		//background: linear-gradient(270deg, rgba(99,171,255,0.1) 0%, rgba(101,151,255,0.06) 100%);
		border-radius: 8px;
		cursor: pointer;
		&:nth-child(1) {
			.icon {
				background: rgba(21, 167, 216, 0.1);
				color: rgba(21, 167, 216, 1);
			}
		}
		&:nth-child(2) {
			.icon {
				background: rgba(246, 163, 106, 0.1);
				color: rgba(246, 163, 106, 1);
			}
		}
		&:nth-child(3) {
			.icon {
				background: rgba(102, 0, 255, 0.1);
				color: rgba(102, 0, 255, 1);
			}
		}

		.itemTitle {
			font-size: var(--font14);
			padding: 14px 12px 16px 12px;
			color: #181b49;
			position: relative;
			.isBeta {
				width: 37px;
				height: 16px;
				background: #355eff;
				border-radius: 0px 8px 0px 7px;
				color: #ffffff;
				font-size: 12px;
				line-height: 16px;
				text-align: center;
				position: absolute;
				right: 0;
				top: 0px;
			}
			.titleBold {
				font-weight: bold;
				font-size: var(--font14);
			}
			.content {
				margin-top: 10px;
				-webkit-line-clamp: 2;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				overflow: hidden;
				text-overflow: ellipsis;
				color: #646479;
			}
			.icon {
				width: 24px;
				height: 24px;
				border-radius: 50%;
				display: inline-block;
				text-align: center;
				line-height: 24px;
				margin-right: 8px;
				font-weight: bold;
			}
			.more {
				display: none;
			}
		}
		.itemCon {
			line-height: 24px;
			color: #646479;
			font-size: var(--font14);
			-webkit-line-clamp: 2;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			overflow: hidden;
			text-overflow: ellipsis;
		}
		&:hover {
			background: rgba(53, 94, 255, 0.06);
			.itemTitle {
				// color: #355eff;
			}
			.content {
				// color: #355eff;
			}
		}
	}
}
@keyframes rotate {
	0% {
		transform: rotateZ(0);
	}

	100% {
		transform: rotateZ(180deg);
	}
}
@media (any-hover: hover) {
	.item:hover {
		// box-shadow: 0 2px 16px #262a3233;
		.more {
			display: block !important;
		}
	}
}
.left_box {
	padding: 0 10px 0;
	justify-content: space-between;
}
.img-box {
	width: 160px;
	height: 155px;
	background: url('/@/assets/zc/left.png');
	background-size: 100% 248px;
}
.left_title {
	color: #355eff;
	width: 382px;
	margin-top: 15px;
	position: relative;
	img {
		width: 382px;
		height: 45px;
	}
	.text {
		font-size: 24px;
		font-family: PingFangSC, PingFang SC;
		font-weight: 700;
		color: #181b49;
		margin-top: 18px;
		display: flex;
		align-items: center;
		.mianze {
			font-size: 14px;
			font-family: PingFangSC, PingFang SC;
			font-weight: 400;
			color: #6b6b6b;
			margin-left: 4px;
		}
	}


}
.sub_title {
	font-size: 24px;
	font-family: PingFangSC-Regular, PingFang SC;
	font-weight: 400;
	color: #646479;
	margin-top: 10px;
}

.dotUl {
	width: 4px;
	height: 4px;
	border-radius: 2px;
	background-color: #646479;
	display: inline-block;
	vertical-align: middle;
}
.mz-dialog {
	padding: 22px;
	.mz-dialog-title {
		font-size: 20px !important;
		font-family: PingFangSC, PingFang SC;
		font-weight: 700 !important;
		color: #181b49 !important;
	}
	.wenben {
		font-size: 14px;
		font-family: PingFangSC, PingFang SC;
		font-weight: 400;
		color: #9b9ba9;
		margin-top: 16px;
	}
}
</style>
