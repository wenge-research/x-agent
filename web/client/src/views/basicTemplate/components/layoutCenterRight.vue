<template>
	<div class="centerInitItem">
		<!-- menuServiceFlag=true：便民服务 -->
		<div v-if="menuServiceFlag" style="height: 250px; overflow: auto; width: 100%">
			<div class="service">便民服务</div>
			<div class="flex serviceList">
				<div v-for="(item, index) in list1" :key="index">
					<div @click="longhuaClick(item.menuUrl)" class="list">
						<img :src="item.menuIcon" alt="" style="width: 40px; height: 40px" />
						<div class="name" style="margin-top: 10px">
							{{ item.menuName }}
						</div>
						<!-- <div class="name">
							{{ item.menuName }}
						</div> -->
					</div>
				</div>
			</div>
		</div>
		<!-- 政策文件/政策解读 -->
		<div v-if="policyListFlag" class="guarantee messageTab">
			<w-tabs style="height: 100%" v-model="activename" @change="tabsChange">
				<w-tab-pane key="2" title="政策文件">
					<ul style="overflow-y: scroll">
						<li v-for="(item, index) in policyDocumentList" :key="index" @click="sendMessage(item.url)">
							<div class="dotUl"></div>
							{{ item.title }}
						</li>
					</ul>
					<div class="fontSize14" style="color: #355eff; margin: 5px auto; width: 100px; cursor: pointer" @click="sendMessageClickone">查看更多></div>
				</w-tab-pane>
				<w-tab-pane key="3" title="政策解读">
					<ul style="overflow-y: scroll">
						<li v-for="(item, index) in policyInterpretationList" :key="index" @click="sendMessage(item.url)">
							<div class="dotUl"></div>
							{{ item.title }}
						</li>
					</ul>
					<div class="fontSize14" style="color: #355eff; margin: 5px auto; width: 100px; cursor: pointer" @click="sendMessageClick">查看更多></div>
				</w-tab-pane>
			</w-tabs>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useKnowledgeState } from '/@/stores/knowledge';
import { useRoute } from 'vue-router';
import { get4317Json, get4316Json, get4310Json } from '/@/api/knowledge';
import { serviceList } from '/@/api/chat/index';
import { formatDate } from '/@/utils/formatTime';
import { Modal } from 'winbox-ui-next';
const time = ref('');
const date = ref('');

const route = useRoute();
const isShow = ref(true);
const knowledgeState = useKnowledgeState();
const currentLibrary: any = computed(() => knowledgeState.currentLibrary);
const previewData: any = computed(() => knowledgeState.previewData);

const list1 = ref([]);
const policyDocumentList = ref([]);
const policyInterpretationList = ref([]);

// 移动端自适应相关
// const { isMobile } = useBasicLayout();
const isMobile = ref(false);
const activename = ref('2');
const culturalList = ref([]);


const sendMessage = (url) => {
	window.open(url, '_blank');
};

const sendMessageClickone = () => {
	window.open('https://www.szlhq.gov.cn/xxgk/zcfg/qgfxwj/qzcxwj/', '_blank');
};
const sendMessageClick = () => {
	window.open('https://www.szlhq.gov.cn/xxgk/zcfg/zcjd/', '_blank');
};

const curRouteId = ref('');
onMounted(() => {
	curRouteId.value = sessionStorage.getItem('curRouteId') as string;
	setTimeout(() => {
		document.querySelector('.center-side').scrollTop = 999999999;
	}, 500);
	setInterval(() => {
		date.value = formatDate(new Date(), 'YYYY/mm/dd');
		time.value = formatDate(new Date(), 'HH:MM:SS');
	}, 1000);

	watch(
		() => previewData.value.active,
		() => {
			getPolicyLinkClick();
			get4317JsonFun();
			get4310JsonFun();
		},
		{ immediate: true, deep: true }
	);
	watch(
		() => currentLibrary.value.fileCount,
		(val: any) => {
			if (val) {
				getPolicyLinkClick();
				get4317JsonFun();
				get4310JsonFun();
			}
		},
		{ immediate: true, deep: true }
	);
});
const tabsChange = (value) => {
	// console.log(value);
	// if(value)
};
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo : '';
};
// 获取链接
const getPolicyLinkClick = async () => {
	serviceList({
		status: 1,
		pageNo: 1,
		pageSize: 1000,
		applicationId: getAppDetail()?.applicationId,
	}).then((res) => {
		if (res.code == '000000') {
			list1.value = res.data?.records;
		} else {
			list1.value = [];
		}
	});
};

// 便民服务
const menuServiceFlag = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.menuServiceFlag == '是' ? true : false;
})
// 政策列表（政策文件/政策解读）
const policyListFlag = computed(() => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo?.policyListFlag == '是' ? true : false;
})

// 获取链接
const get4317JsonFun = async () => {
	let res = await get4317Json();
	get4316JsonFun(res.articles);
};
// 获取链接
const get4316JsonFun = async (articles) => {
	let res = await get4316Json();
	policyDocumentList.value = articles
		.concat(res.articles)
		.sort((a, b) => new Date(b.date) - new Date(a.date))
		.slice(0, 6);
};
// 获取链接
const get4310JsonFun = async () => {
	let res = await get4310Json();
	policyInterpretationList.value = res.articles.sort((a, b) => new Date(b.date) - new Date(a.date)).slice(0, 4);
};
const longhuaClick = (url) => {
	if (!url) return;
	if (url && url.indexOf('szlhq') == -1) {
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
	} else {
		window.open(url, '_blank');
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
@import '/@/theme/mixins/index.scss';

.fontSize14 {
	@include add-size($font-size-base14, $size);
}

.centerInitItem {
	padding: 0 24px;
	width: 400px;
	height: 100%;
	// background: rgba(225, 233, 237, 0.4);
	// border-radius: 0px 30px 30px 0px;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 16px;
	border: 1px solid #ffffff;
	backdrop-filter: blur(1px);

	.messageTab {
		height: calc(100% - 250px);

		:deep(.w-tabs-content) {
			padding-top: 0 !important;
			height: calc(100% - 56px);
			max-height: 600px;
			overflow-y: auto;
		}

		:deep(.w-tabs-nav-tab) {
			height: 56px;
			line-height: 56px;

			.w-tabs-tab-title {
				@include add-size(18px, $size);
				font-weight: 500;
				line-height: 28px;
				color: #181b49;
				margin-left: 50%;
				transform: translate(-50%, 0);
			}
		}

		:deep(.w-tabs-tab-active) {
			.w-tabs-tab-title {
				font-weight: bold;
				font-size: 18px;
				color: #355eff;
			}
		}

		:deep(.w-tabs-tab) {
			width: 143px;
		}

		padding-bottom: 10px;

		ul {
			margin-top: 8px;
			width: 100%;
			box-sizing: border-box;
			list-style-type: disc;
			@include add-size(20px, $size);
			color: #ff0000;

			li {
				width: 100%;
				box-sizing: border-box;
				padding: 12px 8px;
				list-style: none;
				@include add-size(15, $size);
				font-weight: 400;
				color: #646479;
				line-height: 22px;
				border-bottom: 1px dashed #dedede;
				cursor: pointer;
			}

			li:hover {
				background-color: #f5f5f5;
			}
		}
	}

	.service {
		@include add-size(18px, $size);
		font-weight: 500;
		color: #494c4f;
		line-height: 28px;
		margin-bottom: 10px;
		margin-top: 20px;
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

.serviceList {
	flex-wrap: wrap;
	justify-content: space-between;

	.list {
		width: 80px;
		align-items: center;
		display: flex;
		flex-direction: column;
		margin-bottom: 10px;
		cursor: pointer;
	}

	.name {
		text-align: center;
		width: 76px;
		@include add-size(13px, $size);
		font-weight: 400;
		color: #494c4f;
		line-height: 16px;
	}
}

.dotUl {
	width: 4px;
	height: 4px;
	border-radius: 2px;
	background-color: #646479;
	display: inline-block;
	vertical-align: middle;
}
</style>
