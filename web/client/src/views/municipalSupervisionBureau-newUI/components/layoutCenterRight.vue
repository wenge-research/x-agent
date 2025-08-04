<template>
	<div class="centerInitItem">
		<!-- menuServiceFlag=true：便民服务 -->
		<div v-if="menuServiceFlag" style="height: 250px;overflow: hidden;width: 100%">
			<div class="service">便民服务</div>
			<div class="flex serviceList" style="height: calc(100% - 56px);overflow: auto;">
				<div v-for="(item, index) in list1" :key="index" class="serviceList-items">
					<div @click="longhuaClick(item.menuUrl)" class="list">
						<!-- <img :src="item.menuIcon" alt="" style="width: 40px; height: 40px" /> -->
						<img v-if="item.id == 1" src="/@/assets/municipalSupervisionBureau/1.png" alt="" style="width: 40px; height: 40px" />
						<img v-if="item.id == 2" src="/@/assets/municipalSupervisionBureau/2.png" alt="" style="width: 40px; height: 40px" />
						<img v-if="item.id == 3" src="/@/assets/municipalSupervisionBureau/3.png" alt="" style="width: 40px; height: 40px" />
						<img v-if="item.id == 4" src="/@/assets/municipalSupervisionBureau/4.png" alt="" style="width: 40px; height: 40px" />
						<img v-if="item.id == 5" src="/@/assets/municipalSupervisionBureau/5.png" alt="" style="width: 40px; height: 40px" />
						<img v-if="item.id == 6" src="/@/assets/municipalSupervisionBureau/6.png" alt="" style="width: 40px; height: 40px" />
						<img v-if="item.id == 7" src="/@/assets/municipalSupervisionBureau/7.png" alt="" style="width: 40px; height: 40px" />
						<img v-if="item.id == 8" src="/@/assets/municipalSupervisionBureau/8.png" alt="" style="width: 40px; height: 40px" />
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
					<ul v-if="policyDocumentList.length" style="overflow-y: scroll">
						<li v-for="(item, index) in policyDocumentList" :key="index" @click="sendMessage(item.url)">
							<div class="dotUl"></div>
							{{ item.title }}
						</li>
					</ul>
					<div v-else class="no-data">
						<img src="/src/assets/municipalSupervisionBureau/no-data.png" alt="" />
						暂无数据
					</div>
					<!-- <div class="fontSize14" style="color: #355eff; margin: 5px auto; width: 100px; cursor: pointer" @click="sendMessageClickone">查看更多></div> -->
				</w-tab-pane>
				<w-tab-pane key="3" title="政策解读">
					<ul v-if="policyInterpretationList.length" style="overflow-y: scroll">
						<li v-for="(item, index) in policyInterpretationList" :key="index" @click="sendMessage(item.url)">
							<div class="dotUl"></div>
							{{ item.title }}
						</li>
					</ul>
					<div v-else class="no-data">
						<img src="/src/assets/municipalSupervisionBureau/no-data.png" alt="" />
						暂无数据
					</div>
					<!-- <div class="fontSize14" style="color: #355eff; margin: 5px auto; width: 100px; cursor: pointer" @click="sendMessageClick">查看更多></div> -->
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
const { isMobile } = useBasicLayout();
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
			list1.value = [
				{
					id: 1,
					menuIcon: 'https://localhost/smart-agent-api-qs/wos/file/download?fileKey=gangao.7d58eb1d.png',
					menuName: '商事主体年度报告',
					menuSorted: 1,
					menuUrl: 'https://jg.amr.sz.gov.cn/annualpc/?platform=SJ#/',
					serviceCode: 'convenience_service ',
					serviceType: '便民服务',
				},
				{
					id: 2,
					menuIcon: 'https://localhost/smart-agent-api-qs/wos/file/download?fileKey=jiuye.png',
					menuName: '商事主体登记注册',
					menuSorted: 2,
					menuUrl: 'https://amr.sz.gov.cn/xxgk/qt/ztlm/ssdjzc/index.html',
					serviceCode: 'convenience_service ',
					serviceType: '便民服务',
				},
				{
					id: 3,
					menuIcon: 'https://localhost/smart-agent-api-qs/wos/file/download?fileKey=jiuye.png',
					menuName: '商事主体信用公示',
					menuSorted: 3,
					menuUrl: 'https://amr.sz.gov.cn/xyjggs.webui/xyjggs/index.aspx',
					serviceCode: 'convenience_service ',
					serviceType: '便民服务',
				},
				{
					id: 4,
					menuIcon: 'https://localhost/smart-agent-api-qs/wos/file/download?fileKey=rencai.png',
					menuName: '商事登记簿查询',
					menuSorted: 4,
					menuUrl: 'https://tyrz.gd.gov.cn/pscp/sso/static/?redirect_uri=https%3A%2F%2Famr.sz.gov.cn%2Fouter%2FentSelect%2FdoLogin.html&client_id=szjxgcxt',
					serviceCode: 'convenience_service ',
					serviceType: '便民服务',
				},
				{
					id: 5,
					menuIcon: 'https://localhost/smart-agent-api-qs/wos/file/download?fileKey=rencai.png',
					menuName: '政务公开',
					menuSorted: 4,
					menuUrl: 'https://amr.sz.gov.cn/xxgk/',
					serviceCode: 'convenience_service ',
					serviceType: '便民服务',
				},
				{
					id: 6,
					menuIcon: 'https://localhost/smart-agent-api-qs/wos/file/download?fileKey=baoxian.png',
					menuName: '政务服务',
					menuSorted: 5,
					menuUrl: 'https://amr.sz.gov.cn/zxbs/index.html',
					serviceCode: 'convenience_service ',
					serviceType: '便民服务',
				},
				{
					id: 7,
					menuIcon: 'https://localhost/smart-agent-api-qs/wos/file/download?fileKey=baoxian.png',
					menuName: '政民互动',
					menuSorted: 6,
					menuStatus: '1',
					menuUrl: 'https://amr.sz.gov.cn/hdjl/index.html',
					serviceCode: 'convenience_service ',
					serviceType: '便民服务',
				},
				{
					id: 8,
					menuIcon: 'https://localhost/smart-agent-api-qs/wos/file/download?fileKey=wufanzui.png',
					menuName: '专题服务',
					menuSorted: 6,
					menuUrl: 'https://amr.sz.gov.cn/xxgk/qt/ztlm/index.html',
					serviceCode: 'convenience_service ',
					serviceType: '便民服务',
				},
			];
		} else {
			list1.value = [];
		}
	});
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
	policyDocumentList.value = []
};
// 获取链接
const get4310JsonFun = async () => {
	let res = await get4310Json();
	policyInterpretationList.value = res.articles.sort((a, b) => new Date(b.date) - new Date(a.date)).slice(0, 4);
	policyInterpretationList.value = [];
};
const longhuaClick = (url) => {
	if (!url) return;
	// if (url && url.indexOf('szlhq') == -1) {
	// 	Modal.confirm({
	// 		title: 'szlhq.gov.cn 显示',
	// 		content: `您访问的链接即将离开“深圳市市场监督管理局，是否继续？`,
	// 		closable: true,
	// 		okText: '确定',
	// 		cancelText: '取消',
	// 		hideCancel: false,
	// 		modalClass: 'myConfirm',
	// 		onOk: async () => {
	// 			window.open(url, '_blank');
	// 		},
	// 	});
	// } else {
	// 	window.open(url, '_blank');
	// }
	window.open(url, '_blank');
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
	width: 100%;
	height: 100%;
	// background: rgba(225, 233, 237, 0.4);
	// border-radius: 0px 30px 30px 0px;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 0 4px 4px 0;
	border-left: 1px solid rgba(0, 0, 0, 0.12);
	backdrop-filter: blur(1px);

	.messageTab {
		padding-top: 40px;
		height: calc(100% - 290px);

		:deep(.w-tabs-content) {
			padding-top: 0 !important;
			height: calc(100% - 36px);
			max-height: 600px;
			overflow-y: auto;
		}

		:deep(.w-tabs-nav-tab) {
			height: 36px;
			line-height: 36px;
			background: #f5f5f5;
			border-radius: 4px;

			.w-tabs-tab-title {
				@include add-size(18px, $size);
				font-weight: 500;
				line-height: 28px;
				color: #797F8A;
				margin-left: 50%;
				transform: translate(-50%, 0);
			}
		}

		:deep(.w-tabs-nav::before) {
			height: 0 !important;
		}

		:deep(.w-tabs-nav-ink) {
			height: 0;
		}

		:deep(.w-tabs-nav-tab-list) {
			width: 100%;
		}

		:deep(.w-tabs-tab-active) {
			width: 49%!important;
			height: 32px!important;
			background: #ffffff;
			box-shadow: 0px 0px 4px 0px rgba(0, 0, 0, 0.1);
			border-radius: 2px;
			margin-left: 2px!important;
			.w-tabs-tab-title {
				font-weight: 500;
				font-size: 16px;
				color: #3f4247;
			}
		}

		:deep(.w-tabs-tab) {
			width: 50%;
			padding: 0;
			margin: 0;
			height: 100%;
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
		color: #3f4247;
		line-height: 28px;
		margin-bottom: 12px;
		margin-top: 16px;
		font-family: MiSans, MiSans;
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

	// &::after {
	// 	content: '';
	// 	width: 80px;
	// }

	&-items {
		width: 25%;
	}

	.list {
		width: 100%;
		align-items: center;
		display: flex;
		flex-direction: column;
		margin-bottom: 20px;
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
.no-data {
	width: 100%;
	height: 360px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	font-family: MiSans, MiSans;
	font-weight: 400;
	font-size: 16px;
	color: #b4bccc;
	img {
		width: 103px;
	}
}
</style>
