<template>
	<div class="right-aside" :data-type="route.name != 'chat'" :class="isMobile ? 'right-aside-mode' : ''">
		<div class="list" :class="{ record: isOpen && rightFlag == 1, helper: isOpen && rightFlag == 2 }">
			<div v-if="!isOpen && !isMobile">
				<div class="icon" @click="openList(1)">
					<!-- <img :src="chatImg" alt="" /> -->
					<h2>我的记录</h2>
					<i><CoolZhankai size="24" color="#272a31" /></i>
				</div>
				<div v-if="yayiFlag" class="icon" @click="openList(2)">
					<img :src="helperImg" alt="" />
					<h2><span class="tip">YAYI</span>助手</h2>
					<i><CoolZhankai size="24" color="#272a31" /></i>
				</div>
			</div>
			<div class="content" v-show="rightFlag == 1">
				<div class="title">
					<h2><img :src="chatImg" />我的记录</h2>
					<i @click="closeSide"><CoolShouqi size="24" color="var(--w-color-primary)" /></i>
				</div>
				<w-scrollbar :style="`height: ${setHeight}px;overflow:auto;`">
					<div class="chatList space-y-1" v-if="dataSources.length">
						<div v-for="(item, index) of dataSources" :key="index">
							<div class="chatItem" :class="isActive(item.id) && ['isActive', 'pr']" @click="handleSelect(item)">
								<div class="chatItemInput">
									<w-input v-if="item.isEdit" v-model="item.name" size="medium"></w-input>
									<span v-else>{{ item.name }}</span>
								</div>
								<div class="chatItemInputActive" v-if="isActive(item.id)">
									<template v-if="item.isEdit">
										<i @click="handleEdit(item, false, $event)">
											<CoolSaveLineWe size="20" color="var(--w-color-primary)" />
										</i>
									</template>
									<template v-else>
										<i style="margin-right: 16px">
											<CoolBianjibiaoti size="18" color="var(--w-color-primary)" @click="handleEdit(item, true, $event)" />
										</i>
										<w-popconfirm @ok="handleDelete(item.id, index, $event)" content="确认删除此会话?" placement="tr" ok-text="确认">
											<i>
												<CoolShanchu size="18" color="rgb(var(--danger-6))" />
											</i>
										</w-popconfirm>
									</template>
								</div>
								<div class="time">
									<i><CoolShijian size="16" color="#9A99AA" /></i>
									{{ item.createTime }}
								</div>
							</div>
						</div>
					</div>
					<div v-else>
						<w-empty>
							<template #image>
								<img class="nodata" :src="noDataImg" alt="" />
							</template>
							暂无记录
						</w-empty>
					</div>
				</w-scrollbar>
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
import { computed, watch, ref, onMounted, defineAsyncComponent } from 'vue';
import { useChatStore } from '/@/stores/chat';
import { useRobotStore } from '/@/stores/robot';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useThemeConfig } from '/@/stores/themeConfig';
import { storeToRefs } from 'pinia';
import { useRoute } from 'vue-router';
import chatImg from '/@/assets/chat/chat.png';
import helperImg from '/@/assets/chat/helper.svg';
import noDataImg from '/@/assets/chat/nodata.svg';
import { Session } from '/@/utils/storage';
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

const handleSelect = async (item) => {
	const { id } = item;
	if (isActive(id)) {
		return;
	}
	chatStore.setParamsDrawerVisible(false);
	chatStore.setUploadDrawerVisible(false);
	chatStore.dialogueLoading = false;
	await chatStore.setActive(id);
};
const isActive = (id: number) => {
	return chatStore.active === id;
};
const handleEdit = ({ id, name }: Chat.History, isEdit: boolean, event?: MouseEvent) => {
	event?.stopPropagation();
	chatStore.updateHistory(id, name, { isEdit });
};
const handleDelete = (id: number, index: number, event?: MouseEvent | TouchEvent) => {
	event?.stopPropagation();
	chatStore.deleteHistory(id, index);
};
const yayiFlag = ref(false);
// 页面加载时
onMounted(() => {
	yayiFlag.value = Session.get('yayiFlag') == 'true' ? true : false;
	let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
	setHeight.value = clientHeight - 64 - 120;
	if (route.params.conversationId && route.params.conversationId != '') {
		chatStore.initChatList(route.params.conversationId);
	}
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

watch(
	() => route.params.appId,
	(newVal: any) => {
		if (route.params.appId && route.params.appId != '') {
			chatStore.getConversationList(newVal, route.params.conversationId);
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
	&[data-type='true'] {
		position: absolute;
		right: 0;
		z-index: 200;
		height: 100%;
		.icon {
			position: absolute !important;
			right: 0 !important;
			left: auto !important;
		}
	}
	.list {
		width: 0px;
		height: 100%;
		position: absolute;
		right: 0;
		border-left: 1px solid #dfe2eb;
		cursor: pointer;
		position: relative;
		transition: width 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);
		.icon {
			position: absolute;
			top: 100px;
			left: -60px;
			width: 60px;
			height: 168px;
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			padding: 16px;
			border-radius: 8px 0px 0px 8px;
			background: linear-gradient(270deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.5) 100%);
			img {
				width: 32px;
				height: 32px;
			}
			h2 {
				color: #646479;
				text-align: center;
				font-size: var(--font16);
				padding: 4px 8px;
				font-weight: bold;
				display: flex;
				flex-direction: column;
				width: 34px;
			}
			.tip {
				display: inline-block;
				transform: rotate(90deg);
				margin-bottom: 20px;
			}
			&:nth-child(2) {
				top: 275px;
			}
		}
	}
	.record {
		width: 300px;
		background: rgb(245, 251, 253);
		transition: width 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);
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
		h2 {
			display: flex;
			align-items: center;
			color: #181b49;
			font-size: var(--font24);
		}
		img {
			width: 32px;
			height: 32px;
			margin-right: 8px;
		}
	}
	.chatList {
		padding: 0 20px;
		.chatItem {
			padding-right: 16px;
			padding-top: 18px;
			padding-bottom: 50px;
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

				> span {
					height: 28px;
					line-height: 28px;
				}
			}
			.chatItemInputActive {
				display: flex;
				position: absolute;
				z-index: 10;
				right: 20px;
				i {
					display: flex;
				}
			}
			.time {
				position: absolute;
				z-index: 10;
				bottom: 20px;
				color: #9a99aa;
				.cool-icon {
					vertical-align: -0.2em;
				}
			}
		}
		.isActive {
			background: rgba(53, 94, 255, 0.04);
			border-radius: 8px;
			border: 1px dashed #dadada;

			.chatItemInput {
				color: var(--w-color-primary);
			}
		}
		.pr {
			padding-right: 80px;
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
.scrollbarOut {
	height: 100%;
}
:deep(.scrollbarWap) {
	height: 100%;
	overflow: auto;
}
:deep(.w-scrollbar-track-direction-horizontal) {
	display: none;
}
</style>
