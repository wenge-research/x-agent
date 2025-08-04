<template>
	<div class="left-side">
		<div class="left-side-small">
			<div class="listWrap">
				<div
					v-for="(item, index) in treeList"
					:class="{ 'active-nav': item.id == chatStore.categoryId }"
					:key="index"
					class="list"
					@click="collapseOpenTo(item.id)"
				>
					<span class="icon"><SvgIcon :name="`cool-${item.icon}`" :size="24"></SvgIcon></span>
					<span class="text">{{ item.name }}</span>
				</div>
			</div>
			<div class="collapseBtn" @click="collapseOpen">
				<CoolZhankai v-if="!chatStore.collapsedMeun" size="20" color="#9A99AA" />
				<CoolShouqi v-else size="20" color="#9A99AA" />
			</div>
		</div>
		<div class="gridList" v-if="treeList.length">
			<div class="searchInput">
				<w-input v-model="searchText" class="searchInput-item" placeholder="关键词" clearable @clear="searchText = ''">
					<template #prefix><cool-sousuo size="1em" color="currentColor"></cool-sousuo></template>
				</w-input>
			</div>
			<!-- 下面这个列表diff+patch花了230+ms 需要优化组件的实现 看到的人自己去找一下我不知道谁负责  -->
			<w-grid :cols="24" :col-gap="12" :row-gap="12" v-if="leftList?.length > 0">
				<w-grid-item
					class="item"
					v-for="(item, index) in leftList"
					:key="index"
					@click="handleAppClick(item)"
					:span="{ xs: 24, sm: 24, md: 24, lg: 24, xl: 24, xxl: 24 }"
				>
					<div class="itemWrap" :key="index">
						<div class="itemCon">
							<span v-show="item.isBeta" class="isBeta">beta</span>
							<p class="titles">
								<span class="icon">{{ item?.name.charAt(0) }}</span
								><span :title="item.name" class="itemName">{{ item.name }}</span>
							</p>
							<p class="content" :title="item.description">{{ item.promptShow }}</p>
						</div>
					</div>
				</w-grid-item>
			</w-grid>
			<div class="noData" v-else>
				<template v-if="chatStore.categoryId == -1">
					<img :src="noresult" alt="" />
					<div>
						<p>搜索无结果</p>
						<p>换个关键词试试</p>
					</div>
				</template>
				<template v-else>
					<img :src="noDataImg" alt="" />
					<!-- <div><p>技能走丢了</p><p>前往<span class="active" @click="gotoRecommend">推荐</span>中心</p></div> -->
				</template>
			</div>
		</div>
	</div>
</template>

<script lang="ts" name="navMenuVertical" setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { debounce } from 'lodash-es';
import mittBus from '/@/utils/mitt';
import { useChatStore } from '/@/stores/chat';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import noDataImg from '/@/assets/chat/nocategory.svg';
import noresult from '/@/assets/chat/noresult.svg';
import other from '/@/utils/other';
const { isxl } = useBasicLayout();
const currentAppId = ref();
const searchText = ref('');
const basicTreeList = ref([]);
const state = reactive({
	treeListMap: {},
	allList: [],
});

const leftList = ref([]);
const route = useRoute();
const router = useRouter();
const chatStore = useChatStore();
const { appId } = route.params as { appId: string };
const categoryId = ref('');
const collapseOpen = () => {
	chatStore.collapsedMeun = !chatStore.collapsedMeun;
	setTimeout(() => {
		document.querySelector('.gridList').scrollTop = 0;
	}, 100);
};
const gotoRecommend = () => {
	chatStore.categoryId = '-1';
};
const collapseOpenTo = (id) => {
	chatStore.categoryId = id;
	chatStore.collapsedMeun = false;
	document.querySelector('.gridList').scrollTop = 0;
};
watch(
	searchText,
	debounce((val) => {
		gotoRecommend();
		setTimeout(() => {
			const cat = treeList.value.find((l) => l.name === val.trim());
			if (cat) {
				leftList.value = cat.apps;
				return;
			}
			let arr = [];
			let arr2 = treeList.value[0].apps;
			arr2.forEach((item) => {
				if (item.id && item.name.indexOf(val) > -1) {
					arr.push(item);
				}
			});
			leftList.value = arr;
		}, 0);
	}, 0)
);
const treeList = computed(() => {
	return chatStore.appTreeList;
});
if (!appId) {
	router.push({ name: 'chat', params: { appId: 21 } });
}
const handleAppClick = (item: object) => {
	currentAppId.value = item.id;
	mittBus.emit('promptInsert', item);
};
onMounted(() => {
	nextTick(() => {
		setTimeout(() => {
			let ele = document.querySelector('.active-nav');
			if (ele && !isInViewport(ele)) document.querySelector('.active-nav').scrollIntoView(false);
		}, 300);
	});
});

const isInViewport = (element) => {
	var rect = element.getBoundingClientRect();
	return (
		rect.top >= 0 &&
		rect.left >= 0 &&
		rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
		rect.right <= (window.innerWidth || document.documentElement.clientWidth)
	);
};

watch(
	() => categoryId.value,
	(newVal: any) => {
		let arr = chatStore.appTreeList.filter((item) => item.id == newVal);
		leftList.value = arr[0]?.apps;
	},
	{ immediate: true, deep: true }
);

watch(
	() => chatStore.categoryId,
	(newVal: any) => {
		if (newVal !== '') {
			categoryId.value = newVal;
		}
	},
	{ immediate: true, deep: true }
);

watch(
	() => route.params.appId,
	(newVal: any) => {
		currentAppId.value = newVal;
		nextTick(() => {
			setTimeout(() => {
				let ele = document.querySelector('.active-nav');
				if (ele && !isInViewport(ele)) document.querySelector('.active-nav').scrollIntoView(false);
			}, 300);
		});
	},
	{ immediate: true, deep: true }
);
</script>
<style lang="scss" scoped>
.left-side {
	height: 100%;
	display: flex;
	.left-side-small {
		padding: 20px 0;
		position: relative;
		.list {
			width: 60px;
			height: 60px;
			display: flex;
			align-items: center;
			justify-content: center;
			flex-direction: column;
			cursor: pointer;
			margin: 0 10px;
			color: #646479;
			margin-bottom: 4px;
			.cool-icon {
				margin-bottom: 5px;
			}
			.icon {
				height: 27px;
				line-height: 27px;
			}
			.text {
				font-size: var(--font12);
				font-weight: 400;
				line-height: 1.2;
				margin-top: 2px;
				white-space: nowrap;
				overflow: hidden;
				max-width: 50px;
				text-overflow: ellipsis;
			}
			&:hover {
				background: rgba(53, 94, 255, 0.06);
				border-radius: 8px;
				color: #355eff;
			}
		}
		.listWrap {
			height: calc(100% - 60px);
			overflow: auto;
			.active-nav {
				background: rgba(53, 94, 255, 0.06);
				border-radius: 8px;
				color: #355eff;
			}
		}
		.collapseBtn {
			width: 100%;
			text-align: center;
			cursor: pointer;
			position: absolute;
			bottom: 0px;
			left: 0;
			height: 60px;
			line-height: 60px;
		}
	}
	::-webkit-scrollbar {
		display: none;
	}
	::-webkit-scrollbar-thumb {
		display: none;
	}
	.searchInput {
		// width: 200px;
		padding-top: 8px;
		padding-bottom: 10px;
		position: sticky;
		top: 0;
		left: 0;
		right: 0;
		background: #fff;
		// backdrop-filter: blur(10px);
		z-index: 10;
		&-item {
			border-radius: 8px;
		}
	}
	.my-app {
		display: flex;
		justify-content: flex-start;
		align-items: center;
		margin: 14px 20px;
		padding: 4px 0;
		font-size: 16px;
		font-weight: 400;
		color: #646479;
		line-height: 22px;
		cursor: pointer;
		&-active {
			background: rgba(53, 94, 255, 0.05);
			border-radius: 8px;
			font-weight: bold;
			color: #355eff;
		}
		> img {
			width: 44px;
			height: 44px;
			margin-right: 8px;
		}
	}

	.recommend-app {
		margin: 28px 0 20px 24px;
		font-size: 16px;
		font-weight: 400;
		color: #9a99aa;
		line-height: 22px;
	}

	hr {
		margin: 0 16px 0 24px;
	}

	.w-collapse {
		border-radius: 0;
	}

	:deep(.left-collapse) {
		margin-top: 20px;
		.expand-icon {
			width: 44px;
			height: 44px;
		}

		.w-collapse-item-active {
			.w-collapse-item-header-title {
				font-weight: bold;
				color: #181b49;
			}

			.w-collapse-item-header-extra {
				font-weight: bold;
				color: #181b49;
			}
		}

		.collapse-item {
			margin: 0 0 10px 0;
			border-bottom: none;
			user-select: none;

			.w-collapse-item-header {
				padding-top: 15px;
				padding-bottom: 15px;
				padding-left: 70px;
				font-weight: 400;
				color: #646479;
				line-height: 22px;
				border: none;
				background: none;

				.w-collapse-item-header-title {
					font-size: 16px;
				}
			}
			.w-collapse-item-header-extra {
				margin-right: 5px;
			}
			.w-collapse-item-icon-hover {
				left: 20px;
			}

			.w-collapse-item-content {
				margin: 0 16px;
				padding: 0;
				background: rgba(53, 94, 255, 0.04);
				border-radius: 8px;

				.w-collapse-item-content-box {
					padding: 10px 0;

					> div {
						&:hover {
							color: #355eff;
						}
						display: flex;
						justify-content: flex-start;
						align-items: center;
						margin-bottom: 10px;
						font-size: 16px;
						font-weight: 400;
						color: #646479;
						line-height: 24px;
						cursor: pointer;
						padding: 4px 4px 4px 24px;
						&:last-child {
							margin-bottom: 0;
						}

						> img {
							width: 20px;
							margin-right: 15px;
						}
					}

					> div.active-nav {
						font-weight: bold;
						color: #355eff;
					}
					.collapse-item-app-name {
						margin-left: 15px;
					}
				}
			}

			.w-icon-hover {
				&::before {
					display: none;
				}
			}
		}
	}
	.gridList {
		width: 220px;
		padding: 0 8px 8px 8px;
		// height: calc(100% - 40px);
		overflow: auto;
		margin-top: 12px;
		position: relative;
		.noData {
			position: absolute;
			top: 200px;
			p {
				font-size: 16px;
				line-height: 28px;
				color: #646479;
				text-align: center;
			}
			.active {
				color: #355eff;
				cursor: pointer;
			}
		}
	}
	.appTree {
		padding: 16px 4px 4px 16px;
	}
	.more {
		width: 40px;
		height: 20px;
		background: #ccc;
		display: inline-block;
		text-align: center;
	}
	.w-tag {
		height: 32px;
		background: #f0f2f5;
		border-radius: 4px;
		border: none;
		margin-right: 8px;
		margin-bottom: 8px;
		cursor: pointer;
		color: #646479;
	}
	.active-tag {
		background: #355eff;
		color: #fff;
	}
	.item {
		background: #ffffff;
		border-radius: 8px;
		transition: box-shadow 0.2s cubic-bezier(0, 0, 1, 1);
		cursor: pointer;
		width: 200px;
		&:nth-child(4n + 1) {
			.icon {
				background: rgba(21, 167, 216, 0.1);
				color: rgba(21, 167, 216, 1);
			}
		}
		&:nth-child(4n + 2) {
			.icon {
				background: rgba(246, 163, 106, 0.1);
				color: rgba(246, 163, 106, 1);
			}
		}
		&:nth-child(4n + 3) {
			.icon {
				background: rgba(102, 0, 255, 0.1);
				color: rgba(102, 0, 255, 1);
			}
		}
		&:nth-child(4n + 4) {
			.icon {
				background: rgba(53, 94, 255, 0.1);
				color: rgba(53, 94, 255, 1);
			}
		}
		.itemWrap {
			padding: 14px 12px;
			display: flex;
			align-items: center;
			background: linear-gradient(180deg, rgba(255, 255, 255, 0.7) 0%, rgba(255, 255, 255, 0.6) 100%);
			border-radius: 8px;
			height: 110px;
			border: 1px solid #ffffff;
			position: relative;
			background: rgba(53, 94, 255, 0.03);
			&:hover {
				// border: 1px solid #355eff;
				background: rgba(53, 94, 255, 0.06);
			}
		}

		.active-nav {
			border: 1px solid #355eff;
			box-shadow: 0px 4px 8px 0px rgba(51, 51, 51, 0.1);
		}
		.itemTitle {
			width: 50px;
			height: 50px;
			border-radius: 50%;
			background: #fff;
			margin-right: 16px;
			img {
				max-width: 50px;
			}
		}
		.itemCon {
			line-height: 24px;
			font-size: var(--font16);
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			width: 100%;
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
			.titles {
				font-weight: bold;
				font-size: var(--font14);
				color: #181b49;
				line-height: 20px;
				margin-bottom: 6px;
				display: flex;
				align-items: center;
				.itemName {
					width: 150px;
					overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
					display: inline-block;
					padding-left: 12px;
				}
				.icon {
					width: 24px;
					height: 24px;
					border-radius: 50%;
					display: inline-block;
					text-align: center;
					line-height: 24px;
				}
			}
			.content {
				color: #646479;
				font-size: var(--font12);
				-webkit-line-clamp: 2;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				overflow: hidden;
				text-overflow: ellipsis;
				font-weight: 400;
				color: #646479;
				line-height: 20px;
				min-height: 40px;
			}
		}
	}
}
</style>
