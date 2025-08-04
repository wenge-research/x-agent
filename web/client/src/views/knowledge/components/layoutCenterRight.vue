<!-- eslint-disable vue/no-async-in-computed-properties -->
<template>
	<div class="centerInitItem">
		<div style="height: 250px; overflow: auto; width: 100%">
			<div class="service">便民服务</div>
			<div class="flex serviceList">
				<div v-for="(item, index) in list1" :key="index">
					<div @click="longhuaClick(item.link)" class="list">
						<img :src="item.url" alt="" style="width: 40px; height: 40px" />
						<div class="name" style="margin-top: 10px">
							{{ item.name1 }}
						</div>
						<div class="name">
							{{ item.name2 }}
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="guarantee messageTab">
			<w-tabs style="height: 100%" v-model="activename" @change="tabsChange">
				<w-tab-pane key="2" title="政策文件">
					<ul style="overflow-y: scroll">
						<li v-for="(item, index) in policyDocumentList" :key="index" @click="sendMessage(item.url)">
							<div class="dotUl"></div>
							{{ item.title }}
						</li>
					</ul>
					<div class="fontSize14" style="color: #355eff; margin: 5px auto; width: 100px; cursor: pointer"
						@click="sendMessageClickone">查看更多></div>
				</w-tab-pane>
				<w-tab-pane key="3" title="政策解读">
					<ul style="overflow-y: scroll">
						<li v-for="(item, index) in policyInterpretationList" :key="index" @click="sendMessage(item.url)">
							<div class="dotUl"></div>
							{{ item.title }}
						</li>
					</ul>
					<div class="fontSize14" style="color: #355eff; margin: 5px auto; width: 100px; cursor: pointer"
						@click="sendMessageClick">查看更多></div>
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
import { getPolicyLink, get4317Json, get4316Json, get4310Json } from '/@/api/knowledge';
import { formatDate } from '/@/utils/formatTime';
import { Modal } from 'winbox-ui-next';
const time = ref('');
const date = ref('');

const route = useRoute();
const isShow = ref(true);
const knowledgeState = useKnowledgeState();
const currentLibrary: any = computed(() => knowledgeState.currentLibrary);
const previewData: any = computed(() => knowledgeState.previewData);

const list1 = ref([
	{
		url: new URL('/@/assets/img/gangao.png', import.meta.url).href,
		title: '办理往来港澳通行证',
		name1: '办理往来',
		name2: '港澳通行证',
	},
	{
		url: new URL('/@/assets/img/shenfen.png', import.meta.url).href,
		title: '换领居民身份证',
		name1: '换领',
		name2: '居民身份证',
	},
	{
		url: new URL('/@/assets/img/jiuye.png', import.meta.url).href,
		title: '就业登记',
		name1: '基层就业',
		name2: '补贴',
	},
	{
		url: new URL('/@/assets/img/rencai.png', import.meta.url).href,
		title: '高层次人才奖励补贴',
		name1: '高层次人才',
		name2: '奖励补贴',
	},
	{
		url: new URL('/@/assets/img/baoxian.png', import.meta.url).href,
		title: '失业保险金申领',
		name1: '失业保险金',
		name2: '申领',
	},
	{
		url: new URL('/@/assets/img/wufanzui.png', import.meta.url).href,
		title: '无犯罪记录证明',
		name1: '无犯罪',
		name2: '记录证明',
	},
	{
		url: new URL('/@/assets/img/shebao.png', import.meta.url).href,
		title: '社保查询',
		name1: '社保查询',
	},
	{
		url: new URL('/@/assets/img/gongjijin.png', import.meta.url).href,
		title: '公积金查询',
		name1: '公积金查询',
	},
]);
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

// 获取链接
const getPolicyLinkClick = async () => {
	let res = await getPolicyLink({});
	if (res.code == 200) {
		res.data.forEach((item) => {
			if (item.module == '便民服务') {
				culturalList.value = item.policyLinkList;
			}
			list1.value.forEach((item) => {
				culturalList.value.forEach((itemSugger) => {
					if (item.title == itemSugger.title) {
						item.link = itemSugger.link;
					}
				});
			});
		});
	}
};
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
