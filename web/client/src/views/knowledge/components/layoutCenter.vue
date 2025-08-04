<template>
	<div class="relative flex flex-col h-full w-full">
		<div class="SecondaryTitle" v-if="isMobile">
			<h2 class="px-4">{{ getTitle }}</h2>
			<w-button @click="handleClick">我的记录</w-button>
			<w-drawer
				width="80%"
				:render-to-body="false"
				@ok="handleOk"
				@cancel="handleCancel"
				:footer="false"
				:header="false"
				:visible="visible"
				:mask-closable="true"
				unmountOnClose
			>
				<LayoutAsideRight />
			</w-drawer>
		</div>
		<div class="center-side-parent" id="bg-main">
			<w-scrollbar ref="layoutScrollbarRef" class="center-side" :style="`height: ${setHeight}px;overflow:auto;`">
				<div class="container-center px-3">
					<div v-if="!loading">
						<CenterInitItem></CenterInitItem>
						<div v-if="chatList.length" style="padding-top: 8rem"></div>
						<div class="message-list">
							<div v-for="(item, index) of chatList" :key="index">
								<YYMessage
									:answer="item.answer"
									:citations="item.citations || []"
									:createTime="item.createTime"
									:id="item.id + ''"
									:param="item.param"
									:question="item.question"
									:loading="item.loading"
									:inversion="true"
									:key="index + 'r'"
									:feedBackStatus="item.feedBackStatus"
								></YYMessage>
								<YYMessage
									:answer="item.answer"
									:citations="item.citations || []"
									:createTime="item.createTime"
									:id="item.id + ''"
									:param="item.param"
									:question="item.question"
									:loading="item.loading"
									:key="index + 'l'"
									:feedBackStatus="item.feedBackStatus"
								></YYMessage>
							</div>
						</div>
						<div class="text-center" style="height: 2rem">
							<w-button type="outline" v-if="dialogueLoading" :loading="stopChatLoading" shape="round" @click="handleclickStopChat()">
								<template #icon>
									<CoolCheckboxBlankFillWe size="17" style="vertical-align: sub" />
								</template>
								停止生成
							</w-button>
						</div>
						<div style="padding-bottom: 16rem"></div>
					</div>
					<ChatModule></ChatModule>
				</div>
		
			</w-scrollbar>
		</div>
		<div class="fade bottom" :style="{ height: isMobile ? '225px' : '270px' }">
			<div class="background">
				<div class="image" style="background-position: 50% 100%"></div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, computed, onMounted, watch, onUnmounted } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
import { useThemeConfig } from '/@/stores/themeConfig';
import { storeToRefs } from 'pinia';
import { useRoute } from 'vue-router';
const { isMobile } = useBasicLayout();
const chatStore = useChatStore();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
import { setScrollPosition } from '/@/utils/other';
import { Session } from '/@/utils/storage';

const CenterInitItem = defineAsyncComponent(() => import('./centerInitItem.vue'));
const ChatModule = defineAsyncComponent(() => import('./chatModule/index.vue'));
const LayoutAsideRight = defineAsyncComponent(() => import('./LayoutAsideRight.vue'));

const dialogueLoading = computed(() => chatStore.dialogueLoading);
const loading = computed(() => chatStore.chatLoading);
const chatList = computed(() => chatStore.chatList);
const stopChatLoading = ref(false);

const route = useRoute();

const getTitle = computed(() => {
	let title = '';
	if (route.params.appId == 21) {
		title = '对话';
	}
	chatStore.appTreeList.forEach((item) => {
		item.appList.forEach((i) => {
			if (i.id == route.params.appId) {
				title = i.name;
			}
		});
	});
	return title;
});

const handleclickStopChat = async () => {
	stopChatLoading.value = true;
	await chatStore.clearChat();
	stopChatLoading.value = false;
	chatStore.scrollbarBottom = true;
	setScrollPosition(isMobile.value?'.message-list':'.center-side');
};

const visible = computed(() => {
	return themeConfig.value.isVisible;
});
const handleClick = () => {
	themeConfig.value.isVisible = true;
};
const handleOk = () => {
	themeConfig.value.isVisible = false;
};
const handleCancel = () => {
	themeConfig.value.isVisible = false;
};
watch(
	() => route.params.conversationId,
	() => {
		if (route.params.conversationId && route.params.conversationId != '' && isMobile) {
			themeConfig.value.isVisible = false;
		}
	},
	{ immediate: true }
);
watch(
	() => route.params.appId,
	() => {
		if (route.params.appId && route.params.appId != '' && isMobile) {
			themeConfig.value.isCollapse = false;
		}
	},
	{ immediate: true }
);
watch(
	route,
	() => {
	},
	{ immediate: true }
);

const handleScroll = (event) => {
	const dom = document.querySelector('.center-side');
	// const dom = event
	const clientHeight = dom.clientHeight;
	const scrollTop = dom.scrollTop;
	const scrollHeight = dom.scrollHeight;

	if (clientHeight + scrollTop + 15 >= scrollHeight) {
		chatStore.scrollbarBottom = true;
	} else {
		chatStore.scrollbarBottom = false;
	}
};
const setHeight = ref(0);
onMounted(() => {
	chatStore.getFeedbackList();
	resizeScrollHeight();
	const dom = document.querySelector('.center-side');
	dom.addEventListener('scroll', handleScroll);
	window.addEventListener('resize', resizeScrollHeight);
});
const resizeScrollHeight = () => {
	let clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
	let h = isMobile.value ? clientHeight - 64 - 36 : clientHeight - 64;
	setHeight.value = h;
};
onUnmounted(() => {
	try {
		const dom = document.querySelector('.center-side');
		dom.removeEventListener('scroll', handleScroll);
	} catch (error) {}
});
</script>

<style scoped lang="scss">
.center-side {
  &::-webkit-scrollbar {
	  display:none;
  }
}
.fade {
	position: absolute;
	height: 270px;
	width: calc(100% - 5px);
	z-index: 1;
	overflow: hidden;
	clip-path: inset(0px);
	pointer-events: none;
	&.bottom {
		bottom: 0;
	}
	.background {
		height: 100%;
		transition-property: transform;
		transition-duration: 1000ms;
		transition-timing-function: cubic-bezier(0.75, 0, 0.25, 1);
		.image {
			height: 100%;
			width: 100%;
			background-image: url('../../../assets/chat/bg.png');
			// filter: blur(30px);
			-webkit-mask-image: -webkit-gradient(linear, 0 181, 0 0, from(rgba(0, 0, 0, 1)), color-stop(80%, rgba(0, 0, 0, 1)), to(rgba(0, 0, 0, 0)));
		}
	}
}
.SecondaryTitle {
	display: flex;
	align-items: center;
	justify-content: space-between;
	:deep(.w-drawer-body) {
		padding: 0;
	}
}
.center-side-parent {
	flex: 1;
	//overflow: auto;
	height: 100%;
	position: relative;
	// scroll-behavior: smooth;
	.container-center {
		margin: 0 auto;
		max-width: 1000px;
	}
	:deep(.w-scrollbar-container) {
		position: initial;
	}
}
</style>
<style scoped lang="scss">
.secondaryTitleDrawer {
	:deep(.w-drawer-body) {
		padding: 0;
	}
}
</style>
