<template>
	<div class="citationContent">
		<div class="citationText">
			<div class="tips"><span>已选中内容：</span> <CoolCloseCircleFillWe class="close" size="20" @click="instructClear" /></div>
			<w-scrollbar ref="panelScrollbarRef" class="citationContent-center-side" style="max-height: 225px; overflow: auto">
				<div class="text" :class="shouldLineCamp ? 'line-clamp' : ''">
					{{ citationText }}
					<span v-if="shouldLineCamp" @click="textReaderAll">展开<CoolArrowRightSLineWe size="20" /></span>
				</div>
			</w-scrollbar>
		</div>
		<div class="instructbox">
			<div class="tips">要对这句话做点什么呢？</div>
			<div class="list">
				<w-button class="instruct" v-for="(item, index) in list" :key="index" @click="instructSend(item)">{{ item.name }}</w-button>
				<!-- <span class="instruct" v-for="(item, index) in list" :key="index" @click="instructSend(item)">{{ item.name }}</span> -->
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, defineExpose, watch, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useChatStore } from '/@/stores/chat';
import { useKnowledgeState } from '/@/stores/knowledge';
import mittBus from '/@/utils/mitt';
import { listKnowledgeBasePrompt } from '/@/api/knowledge';

const emit = defineEmits(['instructSend', 'instructClear']);
const route = useRoute();
const router = useRouter();
const chatStore = useChatStore();
const knowledgeState = useKnowledgeState();
const citationText = computed(() => knowledgeState.citationText);
const readerAll = ref(false);
const list: any = ref([]);
const calcTextLen = (str: string) => str.split('').reduce((sum, s) => sum + (/[\u4e00-\u9fa5]/.test(s) ? 2 : 1), 0);

const shouldLineCamp = computed(() => !readerAll.value && calcTextLen(knowledgeState.citationText) > 162);
const maxLen = computed(() => {
	const arr = knowledgeState.citationText.split('');
	let len = 0;
	let lastLen = 0;
	for (let index = 0; index < arr.length; index++) {
		const char = arr[index];
		len += /[\u4e00-\u9fa5]/.test(char) ? 2 : 1;
		if (len >= 158 && !lastLen) lastLen = index;
		if (len >= 162) return lastLen;
	}
	return knowledgeState.citationText.length;
});
const textReaderAll = () => {
	readerAll.value = true;
};
const getlist = async () => {
	let params: any = {
		keyword: '',
		knowledgeBaseId: route.params.appId,
	};
	let res = await listKnowledgeBasePrompt(params);
	if (res.code && res.data) {
		list.value = res.data;
	}
};
const instructSend = (item: any) => {
	emit('instructSend', item);
};
const instructClear = () => {
	emit('instructClear', '');
};
onMounted(() => {
	getlist();
});
</script>

<style scoped lang="scss">
.citationContent {
	background: #ffffff linear-gradient(176deg, #e9f2ff 0%, #ffffff 100%);
	border-radius: 16px 16px 0px 0px;
	position: relative;
	padding-bottom: 16px;
	margin-bottom: -16px;
	.citationText {
		width: 100%;
		padding: 20px 24px 18px 24px;
		box-sizing: border-box;
		font-size: var(--font16);
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		color: #646479;
		.tips {
			line-height: var(--font16);
			margin-bottom: 5px;
			display: flex;
			align-items: center;
			justify-content: space-between;
			.close {
				color: #7d7d92a1;
				cursor: pointer;
				&:hover {
					color: #355eff;
				}
			}
		}
		.text {
			position: relative;
			text-align: justify;
			word-break: break-all;
			text-align-last: left;
			line-height: 28px;
			// font-family: monospace;
			> span {
				position: absolute;
				right: 0;
				bottom: 0;
				display: inline-block;
				align-items: center;
				justify-content: center;
				color: #355eff;
				background: #fff;
				padding: 0 10px;
				cursor: pointer;
				> span {
					transform: translateY(5%);
					background: #fff;
				}
			}
			&.line-clamp {
				overflow: hidden;
				text-overflow: ellipsis;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				-webkit-line-clamp: 4;
			}
		}
	}
	.instructbox {
		width: 100%;
		padding: 16px 24px 20px 24px;
		box-sizing: border-box;
		.tips {
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: bold;
			color: #181b49;
			line-height: var(--font16);
			font-size: var(--font16);
			padding-bottom: 10px;
		}
		.list {
			.instruct {
				display: inline-block;
				height: 32px;
				line-height: 32px;
				border-radius: 4px;
				background: #fff;
				font-size: var(--font15);
				font-family: PingFangSC-Regular, PingFang SC;
				font-weight: 400;
				padding: 0 12px;
				margin: 10px 6px 0 6px;
				border: 1px solid #d0d5dc;
				border-radius: 4px;
				cursor: pointer;
				box-sizing: content-box;
				&[active='true'] {
					background: #355eff;
					color: #fff;
				}
				&:hover{
					color: #646479;
				}
			}

			.w-btn-default[type='button']:active{
				background: #355eff;
				color: #fff;
			}
		}
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
		.category {
			color: #181b49;
			font-size: var(--font14);
			margin-right: 8px;
			font-weight: bold;
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
			background: rgba(53, 94, 255, 0.03);
		}
		&[active='true'] {
			background: rgba(53, 94, 255, 0.06);
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
