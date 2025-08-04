<template>
	<div class="tags-view" ref="tagsView">
			<div class="tab">
				<CoolDocx class="fileIcon" v-if="fileObj.name.indexOf('doc') != -1 || fileObj.name.indexOf('docx') != -1" size="15"/>
				<CoolPdf class="fileIcon" v-if="fileObj.name.indexOf('pdf') != -1" size="15"/>
				<CoolTxt class="fileIcon" v-if="fileObj.name.indexOf('txt') != -1" size="15"/>
				<span>
					{{ fileObj.name.length > 20 ? fileObj.name.slice(0,10)+'...'+fileObj.name.slice(fileObj.name.length-10,fileObj.name.length): fileObj.name }}
				</span>
				<CoolCloseLineWe class="close" size="15" @click="handleDelete()"/>
			</div>
	</div>
</template>
<script lang="ts" setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useKnowledgeState } from '/@/stores/knowledge';

const knowledgeState: any = useKnowledgeState();
const previewData: any = computed(() => knowledgeState.previewData);
let fileObj:any = ref({});
const handleDelete = () => {
	let params:any = {
		currItem: {},
		active: '',
		params: {}
	}
	knowledgeState.setPreviewData(params)
};

watch(
	() => previewData.value.currItem,
	(val: any) => {
		fileObj.value = previewData.value.currItem
	},
	{ immediate: true, deep: true }
);
</script>
<style lang="scss">
.tags-view{
	width: 100%;
	height: 32px;
	line-height: 32px;
	margin-top: 8px;
	overflow: hidden;
	padding: 0 10px;
	box-sizing: border-box;
	background-color: rgba($color: #000000, $alpha: 0);
	// border: 1px solid #e5e6eb;
	border-bottom-color: #fff0;
	border-radius: 5px;
	transition: padding .2s cubic-bezier(0,0,1,1),color .2s cubic-bezier(0,0,1,1);
	display: flex;
	align-items: center;
	.tab{
		width: 100%;
		font-size: var(--font12);
		font-family: PingFangSC-Regular, PingFang SC;
		font-weight: 400;
		cursor: pointer;
		color:rgba(53, 94, 255, 1);
		span{
			margin-left: 3px;
		}
		.close{
			margin-left: 10px;
		}
	}
}
</style>
