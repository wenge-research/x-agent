<template>
	<div class="datapanel" v-show="datapanelParasList.length">
		<w-scrollbar ref="panelScrollbarRef" class="datapanel-center-side" style="height: 176px; overflow: auto">
			<div class="panelList" ref="panel">
				<div class="panelItem" :active="active == index" v-for="(item, index) in datapanelParasList" :key="index" @click="penelItemConfirm(index)">
					<i v-if="config.icon">
						<CoolDocx class="fileIcon" v-if="item[config.name].indexOf('.doc') != -1 || item[config.name].indexOf('.docx') != -1" size="20" />
						<CoolPdf class="fileIcon" v-if="item[config.name].indexOf('.pdf') != -1" size="20" />
						<CoolTxt class="fileIcon" v-if="item[config.name].indexOf('.txt') != -1" size="20" />
					</i>
					<span class="label" v-html="highlightText(item[config.name])"></span>
					<span class="des" v-if="config.desc" v-html="highlightText(item[config.desc])"></span>
				</div>
			</div>
		</w-scrollbar>
	</div>
</template>

<script lang="ts" setup>
import { ref, defineAsyncComponent, watch, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useChatStore } from '/@/stores/chat';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { setScrollPosition } from '/@/utils/other';

const route = useRoute();
const router = useRouter();
const chatStore = useChatStore();
const { isMobile } = useBasicLayout();
const datapanelParas = computed(() => chatStore.datapanelParas);
const config: any = computed(() => {
	const isChat = route.path.indexOf('chat') != -1;
	let c = isChat ? datapanelParas.value.config.chat : datapanelParas.value.config.document;
	let index = datapanelParas.value.show === 1 ? 0 : 1;
	return c[index];
});
const keywords = computed(() => {
	let reg = datapanelParas.value.show === 1 ? /\/[^/]*$/ : /@([^@\s]*)$/;
	let text: any = chatStore.chatInputTextValue.match(reg);
	if (!text) {
		return false;
	}
	return text[0] ? text[0].slice(1) : '';
});
const datapanelParasList: any = ref([]);
const active = ref(0);
const panel = ref();
const penelItemConfirm = (index: number) => {
	if (!datapanelParas.value.show) {
		return;
	}
	console.log(datapanelParasList.value[index]);
	datapanelParas.value.value = datapanelParasList.value[index][config.value.name];
	datapanelParas.value.show = 0;
};
const penelItemMove = (type: string) => {
	if (!datapanelParas.value.show) {
		return;
	}
	let list = datapanelParasList.value;
	if (type == 'up') {
		if (active.value == 0) {
			active.value = list.length - 1;
		} else {
			active.value--;
		}
	}
	if (type == 'down') {
		if (active.value == list.length - 1) {
			active.value = 0;
		} else {
			active.value++;
		}
	}
	setTimeout(() => {
		if (!panel.value) return;
		let scrollHeight = panel.value?.scrollHeight;
		let index = active.value;
		let scrollTop: number = (scrollHeight / list.length) * index || 1;
		setScrollPosition('.datapanel-center-side', '', scrollTop);
	}, 100);
};
const highlightText = (text: any) => {
	if (!keywords.value) {
		return text;
	}
	const regex = new RegExp(keywords.value, 'gi');
	text = String(text);
	return text.replace(regex, (match) => `<span style="color:#355EFF">${match}</span>`);
};
const getList = async () => {
	const isChat = route.path.indexOf('chat') != -1;
	let index = datapanelParas.value.show === 1 ? 0 : 1;
	let apis = isChat ? datapanelParas.value.chat : datapanelParas.value.document;
	let api = apis[index];
	let params: any = {
		keyword: keywords.value,
	};
	if (datapanelParas.value.show === 2 && !isChat) {
		params.knowledgeBaseId = route.params.appId;
	}
	let res = await api(params);
	if (res.code == 200 && res.data) {
		datapanelParasList.value = res.data || [];
	}
};

watch(
	() => keywords.value,
	(newVal: any) => {
		if (newVal === false) {
			datapanelParas.value.show = 0;
			return;
		}
		getList();
	},
	{ immediate: true, deep: true }
);

onMounted(async () => {
	// if (route.params.conversationId && route.params.conversationId != '') {
	// 	await chatStore.initFileList({ conversationId: route.params.conversationId });
	// }
	nextTick(() => {
		// getList()
		document.addEventListener('keydown', (e) => {
			if (e.key == 'ArrowUp') {
				penelItemMove('up');
			}
			if (e.key == 'ArrowDown') {
				penelItemMove('down');
			}
			if (e.key == 'ArrowRight') {
				penelItemConfirm(active.value);
			}
		});
	});
});
onUnmounted(() => {
	document.removeEventListener('keydown', () => {});
});
// 监听路由的变化
watch(
	() => route.params.conversationId,
	(newVal) => {
		datapanelParas.value.show = 0;
	}
);
</script>

<style scoped lang="scss">
.datapanel {
	--color-bg-1: transparent;
	--color-bg-5: transparent;
	--color-neutral-3: #c8cbd4;
	background: #fff;
	border-radius: 16px 16px 0px 0px;
	position: relative;
	.flex {
		display: flex;
		align-items: center;
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
	.panelList {
		width: 100%;
		padding: 12px 15px 4px 15px;
		box-sizing: border-box;
	}

	.panelItem {
		padding: 0 12px;
		box-sizing: border-box;
		display: inline-flex;
		align-items: center;
		width: 100%;
		height: 40px;
		border-radius: 8px;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		cursor: pointer;
		.label {
			color: #181b49;
			font-size: var(--font14);
			margin-right: 8px;
		}
		i {
			margin-right: 11px;
		}
		.des {
			font-size: var(--font12);
			font-size: 12px;
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #646479;
		}
		&:hover {
			background: rgba(53, 94, 255, 0.3);
		}
		&[active='true'] {
			background: rgba(53, 94, 255, 0.6);
		}
	}
	.isMore200 {
		max-height: 200px;
		top: -190px !important;
	}
	.visible {
		top: 15px !important;
		transition: top 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);
	}
}
</style>
