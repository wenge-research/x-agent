<template>
	<div class="left-side">
		<div class="list">
			<div class="public" v-if="libraryPublic.length">
				<div class="library" v-for="(item, index) in libraryPublic" :key="index" @click="jump(item)">
					<div class="icon" :style="{ 'background-color': bgColor[item.icon] }">
						{{ item.icon }}
					</div>
					<div class="info">
						<p class="name">{{ item.name }}</p>
						<p class="number" v-if="item.fileCount">{{ item.fileCount }}个文件</p>
					</div>
				</div>
			</div>
			<div class="list-title">
				<p>我的知识库</p>
			</div>
			<div class="search">
				<w-input v-model="searchText" class="searchInput" placeholder="关键词" clearable @input="changeText" @clear="changeText('')">
					<template #prefix><cool-sousuo size="1em" color="currentColor"></cool-sousuo></template>
				</w-input>
				<w-button type="primary" @click="modalOpen(null)">
					<template #icon>
						<CoolXinzeng size="18" color="#fff" />
					</template>
				</w-button>
			</div>
			<div class="init" v-show="!library.length">
				<img :src="init_centent" alt="" />
				<!-- <w-upload
					:accept="fileUpdate.accept"
					action
					multiple
					:custom-request="handleChange"
					:show-file-list="false"
				>
					<template #upload-button>
						<p>暂无数据<span>立即上传</span></p>
					</template>
				</w-upload> -->
				<p>暂无数据 <span @click="modalOpen">立即创建</span></p>
			</div>
			<div class="library" v-for="(item, index) in library" :key="index">
				<div class="icon" @click="jump(item)" :style="{ 'background-color': bgColor[item.icon] }">
					{{ item.icon }}
				</div>
				<div class="info" @click="jump(item)">
					<p class="name">{{ item.name }}</p>
					<p class="number">{{ item.fileCount || 0 }}个文件</p>
				</div>
				<w-popover placement="bottom" trigger="hover" content-class="nav-select-popover nav-handle-popover">
					<div class="ability">
						<CoolMore_2LineWe size="16" />
					</div>
					<template #content>
						<contextmenu :item="item" @currentContextmenuClick="currentContextmenuClick" />
					</template>
				</w-popover>
			</div>
		</div>
		<div class="footer">
			<w-progress :percent="percent" :show-text="false" />
			<p>
				<span class="size"> {{ ThousandWithNumber(knowledgesSize.charCount) }} / {{ ThousandWithNumber(knowledgesSize.capacity) }}字 </span>
				<span class="upgradation"> 升级扩容 </span>
				<!-- @click="expandOpen" -->
			</p>
			<div class="expand" v-if="expand.show">
				<p class="expand-title">知识库容量</p>
				<p>当前知识库总容量100,000字</p>
				<p>剩余可用67,052字</p>
				<hr />
				<div class="detail">
					<span> 政策解读 </span>
					<span> 5,183 </span>
					<hr />
				</div>
				<div class="detail">
					<span> 政策解读 </span>
					<span> 5,183 </span>
					<hr />
				</div>
				<div class="expand-button">
					<span>升级扩容</span>
				</div>
			</div>
		</div>
	</div>
	<delModal ref="delModalRef" />
</template>

<script lang="ts" name="navMenuVertical1" setup>
import { defineAsyncComponent, computed, nextTick, onBeforeMount, onMounted, reactive, ref, watch } from 'vue';
import { IconPlus } from 'winbox-ui-next/es/icon';
import { useRoute, useRouter } from 'vue-router';
import { userPage } from '/@/api/knowledge';
import { useChatStore } from '/@/stores/chat';
import { useRobotStore } from '/@/stores/robot';
import { useKnowledgeState } from '/@/stores/knowledge';
import { Session } from '/@/utils/storage';
import init_centent from '/@/assets/knowledge/init_centent.png';
import { ThousandWithNumber } from '/@/utils/format.ts';
const knowledgeState = useKnowledgeState();
const contextmenu = defineAsyncComponent(() => import('./contextmenu.vue'));
const delModal = defineAsyncComponent(() => import('./delModal.vue'));
const delModalRef = ref();
const basicTreeList = ref([]);
const treeList = ref([]);
const knowledgesSize: any = computed(() => knowledgeState.knowledgesSize);
const percent = computed(() => {
	let num = 0;
	if (knowledgesSize.value.capacity) {
		num = Number(knowledgesSize.value.charCount) / Number(knowledgesSize.value.capacity);
	}
	return num;
});
const bgColor = ref({
	'💻': 'rgba(53,94,255,0.06)',
	'📝': 'rgba(7, 190, 184, 0.06)',
	'🧩': 'rgba(102, 0, 255, 0.06)',
	'📍': 'rgba(255, 98, 0, 0.06)',
	'🌠': 'rgba(245, 75, 91, 0.06)',
	'📖': 'rgba(53, 94, 255, 0.06)',
});
const currentAppId = ref();
const clickedExpendKey = ref();
const state = reactive({
	treeListMap: {},
});
const route = useRoute();
const router = useRouter();
const chatStore = useChatStore();
const robotStore = useRobotStore();
const { appId } = route.params as { appId: string };
const searchText = ref('');
let records: any = [];
const libraryPublic: any = ref([]);
const library: any = ref([]);
const expand = reactive({
	show: false,
});
const modalOpen = (item: any) => {
	let type = item?.id ? 2 : 1;
	knowledgeState.modelOpen(type, item);
};
const jump = (item: any) => {
	router.push({
		path: '/knowledge/' + item.id,
	});
};
// 关闭页签
// todo: 关闭之后需要调取删除已打开的画布页签的接口（批量删除）
const currentContextmenuClick = (item: any) => {
	switch (item.value) {
		case 3:
			modalOpen(item);
			break;
		case 4:
			delModalRef.value.handleClick(item);
			break;
	}
};
const getList = async () => {
	try {
		let params = {
			size: 100000,
		};
		let res = await userPage(params);
		if (res?.code === 200 && res?.data) {
			let list = res.data.records;
			libraryPublic.value = list.filter((item: any) => {
				if (item.authority != 2) {
					if (item.creator == Session.get('userId')) {
						return true;
					} else if (Number(item.fileCount)) {
						return true;
					}
				}
				return false;
			});
			library.value = list.filter((item: any) => item.authority === 2);
			records = library.value;
		}
	} catch (err) {
		throw new Error();
	}
};

const expandOpen = () => {
	expand.show = true;
};

const filterTree = (val, tree, newArr = []) => {
	if (!val) {
		newArr = tree;
		return newArr;
	}
	newArr = tree.filter((i: any) => {
		return i.name.indexOf(val) != -1;
	});
	return newArr;
};

const changeText = (val) => {
	searchText.value = val;
	library.value = filterTree(val, records);
};
onBeforeMount(async () => {
	try {
		getList();
		knowledgeState.setModeCallback(getList);
	} catch (err) {
		throw new Error();
	}
});
onMounted(() => {
	nextTick(() => {});
});
// 展开项的key值
let expendKey = computed(() => {
	let currentKey;
	for (const key in state.treeListMap) {
		if (state.treeListMap[key].includes(currentAppId.value)) {
			currentKey = key;
			break;
		}
	}
	return clickedExpendKey.value || currentKey;
});

watch(
	() => route.params.appId,
	(newVal: any) => {
		currentAppId.value = newVal;
		clickedExpendKey.value = undefined;
		nextTick(() => {});
	},
	{ immediate: true, deep: true }
);
</script>
<style lang="scss" scoped>
.left-side {
	// position: relative;
	width: 100%;
	padding: 16px 16px 92px 16px;
	min-height: 100%;
	background: rgba(255, 255, 255, 0.5);
	box-sizing: border-box;
	border-right: 1px solid #dfe2eb;
	.init {
		margin-top: 56px;
		text-align: center;
		img {
			margin: 0 auto;
			height: 150px;
		}
		text-align: center;
		p {
			margin-bottom: var(--font16);
			font-size: var(--font14);
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #646479;
			span {
				color: #355eff;
				cursor: pointer;
			}
		}
	}
	.search {
		margin-top: 24px;
		margin-bottom: 16px;
		display: flex;
		align-items: center;
		justify-content: space-between;
		button {
			border-radius: 8px;
			background: linear-gradient(90deg, #7e9dff 0%, #355eff 100%);
			border: none;
		}
		.searchInput {
			&.searchWidth {
				width: 100% !important;
			}
			width: calc(100% - 45px);
			border-radius: 8px;
			background: transparent;
		}
	}
	.list {
		width: 100%;
		.library {
			margin-top: 12px;
			display: flex;
			align-items: center;
			width: 100%;
			height: 64px;
			background: linear-gradient(180deg, rgba(255, 255, 255, 0.7) 0%, rgba(255, 255, 255, 0.6) 100%);
			border-radius: 8px;
			border: 1px solid #ffffff;
			color: #181b49;
			cursor: pointer;
			position: relative;
			.icon {
				width: 48px;
				height: 48px;
				background: #ff6200;
				border-radius: 8px;
				margin: 0 12px 0 8px;
				display: inline-flex;
				align-items: center;
				justify-content: center;
				// background: rgba($color: #6600FF, $alpha: 0.06);
				border-radius: 8px;
				font-size: var(--font24);
				font-family: AppleColorEmoji;
			}
			.info {
				margin-right: 10px;
				width: 140px;
				.name {
					font-size: var(--font16);
					font-family: PingFangSC-Medium, PingFang SC;
					font-weight: 500;
					line-height: var(--font22);
					overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
				}
				.number {
					font-size: var(--font12);
					font-family: PingFangSC-Regular, PingFang SC;
					font-weight: 400;
					color: #9a99aa;
					line-height: var(--font18);
				}
			}
			.ability {
				position: absolute;
				right: 10px;
				color: rgba(154, 153, 170, 1);
			}
			&:hover {
				// box-shadow: 7px 4px 8px 3px rgba(51, 51, 51, 0.18);

				box-shadow: 0px 4px 8px 0px rgba(51, 51, 51, 0.08);
				color: #355eff;
				border: 1px solid #e4e8ee;
			}
		}
		.library:nth-of-type(1) {
			margin-top: 0;
		}

		.list-title {
			margin-top: 24px;
			font-size: var(--font20);
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			line-height: var(--font28);
		}
	}
	.footer {
		position: absolute;
		bottom: 0;
		left: 0;
		width: calc(100% - 1px);
		padding: 20px 24px;
		height: 86px;
		background: #fff;
		p {
			margin-top: 12px;
			display: flex;
			align-items: center;
			justify-content: space-between;
			font-size: var(--font14);
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #9a99aa;
			.upgradation {
				cursor: pointer;
				// color: #355eff;
				color: #666;
				cursor: no-drop;
			}
		}
		.expand {
			position: absolute;
			top: 12px;
			transform: translateY(-100%);
			width: calc(100% - 48px);
			padding: 20px;
			box-sizing: border-box;
			background: #ffffff linear-gradient(180deg, rgba(172, 193, 255, 0.2) 0%, rgba(235, 244, 252, 0) 100%),
				linear-gradient(180deg, rgba(126, 242, 255, 0.2) 0%, rgba(235, 244, 252, 0) 100%);
			.expand-title {
				font-size: var(--font16);
				font-family: MiSans-Regular, MiSans;
				font-weight: 400;
				color: #181b49;
				line-height: var(--font20);
				margin-bottom: 6px;
			}
			p {
				margin-top: 0;
				font-size: var(--font12);
				line-height: var(--font20);
			}
			p:nth-child(3) {
				margin-bottom: 15px;
			}
			.detail {
				width: 100%;
				height: 39px;
				display: flex;
				align-items: center;
				justify-content: space-between;
				font-size: var(--font12);
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				color: #9a99aa;
			}
			.expand-button {
				margin-top: 9px;
				font-size: var(--font14);
				font-family: MiSans-Regular, MiSans;
				font-weight: 400;
				color: #355eff;
				line-height: 19px;
				text-align: center;
				span {
					cursor: pointer;
				}
			}
		}
	}
}
</style>
