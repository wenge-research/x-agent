<template>
	<div :id="route.path.indexOf('chat') == -1 ? 'absolute' : ''" class="right-aside" :class="isMobile ? 'right-aside-mode' : ''">
		<div class="list" :class="{ record: isOpen && rightFlag == 1, helper: isOpen && rightFlag == 2 }">
			<div v-if="!isOpen && !isMobile" class="listContent">
				<div class="icon" @click="openList(1)" :class="{ 'active-item': rightFlag == 1 }">
					<CoolJilu  size="16" />
					<h2>记录</h2>
				</div>
				<span class="line" v-if="yayiFlag"></span>
				<div v-if="yayiFlag" class="icon" @click="openList(2)" :class="{ 'active-item': rightFlag == 2 }">
					<CoolZhushou  size="16" />
					<h2>助手</h2>
				</div>
			</div>
			<div class="content" v-show="rightFlag == 1">
				<div class="title">
					<h2>对话记录</h2>
					<i @click="closeSide"><CoolShouqi size="16" color="#9A99AA" /></i>
				</div>
				<w-list
					class="chatList"
					v-if="dataSources.length"
					:data="dataSources"
					:bordered="false"
					:bottom-offset="0"
					:scrollbar="true"
					:max-height="setHeight"
					@reach-bottom="getNextPage"
				>
					<template #scroll-loading>
						<div v-if="isEnd" class="noMoreText">
							{{current == 1? '' : '暂无更多' }}
						</div>
						<w-spin v-else />
					</template>
					<template #item="{ item, index }">
						<w-list-item :key="index">
							<div class="chatItem group hover:!pr-20" :class="isActive(item.id) && ['isActive', '!pr-20']" @click="handleSelect(item)">
								<div class="chatItemInput">
									<w-input v-if="item.isEdit" v-model="item.name" size="medium"></w-input>
									<span v-else>{{ item.name }}</span>
								</div>
								<div class="chatItemInputActive hidden group-hover:flex" :class="isActive(item.id) && ['flex']">
									<template v-if="item.isEdit">
										<i style="margin-right: 12px" @click="handleEdit(item, false, $event)">
											<CoolTongguo size="16" color="#9a99aa" />
										</i>
										<i @click="handleCancel(item, false, $event)">
											<CoolCloseLineWe size="16" color="#9a99aa" />
										</i>
									</template>
									<template v-else>
										<i style="margin-right: 16px">
											<CoolEditTwoLineWe size="16" color="#9a99aa" @click="handleEdit(item, true, $event)" />
										</i>
										<w-popconfirm @ok="handleDelete(item.id, index, $event)" content="确认删除此会话?" placement="tr" ok-text="确认">
											<i>
												<CoolDeleteBinThreeLineWe size="16" color="#9a99aa" />
											</i>
										</w-popconfirm>
									</template>
								</div>
								<div class="time">
									<i><CoolShijian size="16" color="#9A99AA" /></i>
									{{ formatPast(item.createTime) }}
								</div>
							</div>
						</w-list-item>
					</template>
				</w-list>
				<w-empty v-else>
					<template #image>
						<img class="nodata" :src="noDataImg" alt="" />
					</template>
					暂无对话记录
				</w-empty>
			</div>
			<div class="content" v-show="rightFlag == 2 && yayiFlag">
				<div class="title">
					<h2><img :src="helperImg" />YAYI助手</h2>
					<i @click="closeSide"><CoolShouqi size="24" color="#272a31" /></i>
				</div>
				<div class="robot">
					<Robot></Robot>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts" name="layoutAside">
import { computed, watch, ref, onMounted, onBeforeMount, defineAsyncComponent } from 'vue';
import { useChatStore } from '/@/stores/chat';
import { useRobotStore } from '/@/stores/robot';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useThemeConfig } from '/@/stores/themeConfig';
import { storeToRefs } from 'pinia';
import { useRoute } from 'vue-router';
import { Message } from 'winbox-ui-next';
import helperImg from '/@/assets/chat/helper.svg';
import noDataImg from '/@/assets/chat/nodataconv.svg';
import { Session } from '/@/utils/storage';
import { formatPast } from '/@/utils/formatTime';
import other from '/@/utils/other';

// formatPast(param: string | Date, format: string = 'YYYY-mm-dd')
const Robot = defineAsyncComponent(() => import('./robot.vue'));
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const route = useRoute();
// 移动端自适应相关
const { isMobile, isxl } = useBasicLayout();
const isOpen = computed(() => {
	return chatStore.isOpen;
});
const rightFlag = computed(() => {
	return chatStore.rightFlag;
});
const chatStore = useChatStore();
const robotStore = useRobotStore();
const setHeight = ref(0);
const dataSources = computed(() => chatStore.history);
const dataSourcesold = ref([]);

const handleSelect = async (item) => {
	const { id } = item;
	if (isActive(id)) {
		return;
	}
	chatStore.setParamsDrawerVisible(false);
	chatStore.setUploadDrawerVisible(false);
	chatStore.dialogueLoading = false;
	await chatStore.setActive(id, item.isSensitive);
};
const isActive = (id: number) => {
	return chatStore.active === id;
};
const handleEdit = ({ id, name }: Chat.History, isEdit: boolean, event?: MouseEvent) => {
	if (!isEdit && name == '') {
		Message.warning('对话名称不能为空');
		return;
	}
	dataSourcesold.value = other.deepClone(dataSources.value);
	event?.stopPropagation();
	chatStore.updateHistory(id, name, { isEdit });
};
const handleCancel = (item: Chat.History, isEdit: boolean, event?: MouseEvent) => {
	event?.stopPropagation();
	item.isEdit = false;
	const index = dataSourcesold.value.findIndex((i) => i.id === item.id);
	if (index !== -1) {
		item.name = dataSourcesold.value[index].name;
	}
};
const handleDelete = (id: number, index: number, event?: MouseEvent | TouchEvent) => {
	event?.stopPropagation();
	chatStore.deleteHistory(id, index);
};
const yayiFlag = ref(false);
const feedBack = () => document.querySelector('#faterRobot #divMsg').click();

// 页面加载时
onMounted(() => {
	// if(route.path.indexOf('chat') === -1){
	// 	closeSide()
	// }
	yayiFlag.value = Session.get('yayiFlag') == 'true' ? true : false;
	let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
	setHeight.value = clientHeight - 64 - 100;
	if (route.params.conversationId && route.params.conversationId != '') {
		chatStore.initChatList(route.params.conversationId);
	}
});
onBeforeMount(() => {
	// if(route.path.indexOf('chat') === -1){
	// 	closeSide()
	// }
});

//监听屏幕宽度小于1200
watch(
	isxl,
	() => {
		chatStore.isOpen = false;
	},
	{
		deep: true,
	}
);

const openList = (val) => {
	chatStore.rightFlag = val;
	chatStore.rightFlag = val;
	chatStore.isOpen = !chatStore.isOpen;
	if (chatStore.rightFlag == 2) {
		robotStore.muted = false;
	}
};
const closeSide = () => {
	if (isMobile.value) {
		themeConfig.value.isVisible = false;
	} else {
		chatStore.isOpen = false;
		if (rightFlag.value == 2) {
			robotStore.muted = true;
			robotStore.breakChat();
		}
	}
};

let current = 0;
let pending = false;
let isEnd = ref(false);

const getNextPage = async () => {
	if (isEnd.value || pending) return;
	pending = true;

	try {
		isEnd.value = await chatStore.getConversationListByPage(route.params.appId as string, ++current, route.params.conversationId as string);
	} catch (error) {
		current--;
	}

	pending = false;
};

watch(
	() => route.params.appId,
	() => {
		if (route.params.appId && route.params.appId != '') {
			current = 0;
			isEnd.value = false;
			getNextPage();
		}
	},
	{ immediate: true }
);

watch(
	() => route.params.conversationId,
	(newVal: any) => {
		if (route.params.appId) {
			if (newVal != chatStore.conversationsId) {
				chatStore.getChatList(newVal);
			}
			chatStore.conversationsId = 'new';
		}
	}
);
</script>
<style scoped lang="scss">
.right-aside {
	position: relative;
	z-index: 3;
	.list {
		width: 0px;
		height: 100%;
		position: absolute;
		right: 0;
		border-left: 1px solid #dfe2eb;
		position: relative;
		transition: width 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);
		// .active-item,
		.icon:hover {
			background: rgba(53, 94, 155, 0.06) !important;
			border-radius: 4px;
		}
		.icon {
			width: 34px;
			display: flex;
			cursor: pointer;
			color: #646479;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			flex: 1;
			padding: 5px 0;
			h2 {
				text-align: center;
				font-size: var(--font12);
				font-weight: 500;
				margin-top: 5px;
			}
		}
		.listContent{
			position: absolute;
			left: -50px;
			top: 120px;
			width: 44px;
			//height: 120px;
			border-radius: 8px;
			padding: 5px;
			background: #fff;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			.line{
				width: 20px;
				height: 1px;
				display: inline-block;
				background: #E4E8EE;
				margin: 3px 0;
			}
		}
	}
	.record {
		width: 300px;
		background: #fff;
		transition: width right 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);
	}
	.helper {
		width: 440px;
		background: rgb(245, 251, 253);
		transition: width 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);
	}

	.title {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20px;
		line-height: 1;
		h2 {
			display: flex;
			align-items: center;
			color: #181b49;
			font-size: var(--font16);
			font-weight: 500;
		}
		i {
			display: flex;
			align-items: center;
			cursor: pointer;
		}
	}
	.chatList {
		padding: 0 10px;
		border: none;
		:deep(.w-list-item-main) {
			width: 100%;
		}
		:deep(.w-list-item) {
			padding: 0 !important;
			border-bottom: 0;
		}
		.noMoreText{
			color: #9a99aa;
		}
		.chatItem {
			padding-right: 16px;
			padding-top: 12px;
			padding-bottom: 30px;
			padding-left: 16px;
			display: flex;
			cursor: pointer;
			align-items: center;
			border-radius: 0.375rem;
			word-break: break-all;
			position: relative;
			text-overflow: ellipsis;
			border: 1px solid transparent;
			.chatItemInput {
				flex: 1 1 0%;
				position: relative;
				word-break: break-all;
				white-space: nowrap;
				text-overflow: ellipsis;
				overflow: hidden;
				font-size: var(--font14);
				color: #646479;
				min-height: 28px;

				> span {
					height: 28px;
					line-height: 28px;
				}
			}
			.chatItemInputActive {
				position: absolute;
				z-index: 10;
				right: 20px;
			}
			.time {
				position: absolute;
				z-index: 10;
				bottom: 12px;
				color: #9a99aa;
				.cool-icon {
					vertical-align: -0.2em;
				}
			}
			&:hover {
				background: rgba(53, 94, 255, 0.04);
				border-radius: 8px;
				.chatItemInput {
				}
			}
		}
		.isActive {
			background: rgba(53, 94, 255, 0.04);
			border-radius: 8px;

			.chatItemInput {
				color: var(--w-color-primary);
			}
		}
	}
	.nodata {
		margin: auto;
		height: 100px;
	}
}
.right-aside-mode {
	.list {
		width: 100%;
	}
	.chatItem {
		padding-bottom: 40px !important;
	}
	.time {
		bottom: 10px !important;
	}
}
</style>
