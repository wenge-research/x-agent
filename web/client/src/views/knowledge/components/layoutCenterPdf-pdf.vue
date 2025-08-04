<template>
	<div class="relative flex flex-col h-full w-full">
		<tagsView></tagsView>
		<div class="center-side-pdf" id="pdf" v-if="pdfUrl">
			<vue-pdf-app
				:pdf="pdfUrl"
				:page-number="pageNumber"
				:pageScale="scale"
				:config="config"
				:id-config="idConfig"
				theme="light"
				@after-created="afterCreated"
				@open="open"
				@pages-rendered="pagesRendered"
				file-name="Custom fileName"
				style="position: relative"
			>
			</vue-pdf-app>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, computed, onMounted, watch, onUnmounted, reactive } from 'vue';
import { storeToRefs } from 'pinia';
import { useKnowledgeState } from '/@/stores/knowledge';
const knowledgeState: any = useKnowledgeState();
const previewData: any = computed(() => knowledgeState.previewData);
const tagsView = defineAsyncComponent(() => import('./tagsView.vue'));
import { VuePdfApp } from "vue-pdf-app";
import "vue-pdf-app/dist/icons/main.css";
// const pdfUrl = ref('')
const pageNumber = ref(1)
const scale = '1'
const config = reactive({
	toolbar: {
		toolbarViewerLeft: {
		// pageNumber: false,
		},
	},
	// toolbar: false
})
interface Props {
	pdfUrl?: string
}
const props = defineProps<Props>();
const idConfig = reactive({
	// cursorHandTool: 'vuePdfAppCursorHandTool',
	// cursorSelectTool: 'vuePdfAppCursorSelectTool',
	// documentProperties: 'vuePdfAppDocumentProperties',
	// download: 'vuePdfAppDownload',
	// findbar: 'vuePdfAppFindbar',
	// findEntireWord: 'vuePdfAppFindEntireWord',
	// findHighlightAll: 'vuePdfAppFindHighlightAll',
	// findInput: 'vuePdfAppFindInput',
	// findMessage: 'vuePdfAppFindMessage',
	// findMatchCase: 'vuePdfAppFindMatchCase',
	// findNext: 'vuePdfAppFindNext',
	// findPrevious: 'vuePdfAppFindPrevious',
	// findResultsCount: 'vuePdfAppFindResultsCount',
	// firstPage: 'vuePdfAppFirstPage',
	// lastPage: 'vuePdfAppLastPage',
	// nextPage: 'vuePdfAppNextPage',
	// numPages: 'vuePdfAppNumPages',
	// openFile: 'vuePdfAppOpenFile',
	// pageNumber: 'vuePdfAppPageNumber',
	// pageRotateCcw: 'vuePdfAppPageRotateCcw',
	// pageRotateCw: 'vuePdfAppPageRotateCw',
	// presentationMode: 'vuePdfAppPresentationMode',
	// previousPage: 'vuePdfAppPreviousPage',
	// print: 'vuePdfAppPrint',
	// scrollHorizontal: 'vuePdfAppScrollHorizontal',
	// scrollVertical: 'vuePdfAppScrollVertical',
	// scrollWrapped: 'vuePdfAppScrollWrapped',
	// sidebarToggle: 'vuePdfAppSidebarToggle',
	// spreadEven: 'vuePdfAppSpreadEven',
	// spreadNone: 'vuePdfAppSpreadNone',
	// spreadOdd: 'vuePdfAppSpreadOdd',
	// toggleFindbar: 'vuePdfAppToggleFindbar',
	// viewAttachments: 'vuePdfAppViewAttachments',
	// viewBookmark: 'vuePdfAppViewBookmark',
	// viewOutline: 'vuePdfAppViewOutline',
	// viewThumbnail: 'vuePdfAppViewThumbnail',
	// zoomIn: 'vuePdfAppZoomIn',
	// zoomOut: 'vuePdfAppZoomOut'
})
const analysisUrl = () => {
	let {active, currItem, params} = previewData.value;
	let item = currItem;
	let condition = '';
	// for(let key in params){
	// 	condition += `#${key}=${params[key]}`
	// }
	if(params?.page){
		pageNumber.value = params?.page
	}
	pdfUrl.value = item.fileUrl + condition
}
watch(
	() => previewData.value,
	(newVal: any) => {
		pdfUrl.value = ''
		setTimeout(()=>{
			analysisUrl()
		})
	},
	{ immediate: true, deep: true }
);
const afterCreated = (pdfApp) => {
	// window._pdfApp = pdfApp
	console.log('===***=== After created')
}
const open = (pdfApp) => {
	// window._pdfApp = pdfApp
	console.log('===***=== Opened')
}
const pagesRendered = (pdfApp) => {
	// window._pdfApp = pdfApp
	console.log('===***=== Pages rendered')
}
</script>

<style scoped lang="scss">
.center-side-pdf{
	width: 100%;
	height: 100%;
	padding: 0 10px;
	box-sizing: border-box;
	iframe{
		width: 100%;
		height: 100%;
		border: none;
	}
}
</style>
<style lang="scss">

iframe{
	// 滚动条的宽度
	&::-webkit-scrollbar {
		width: 5px;
		height: 5px;
	}
	// 滚动条的设置
	&::-webkit-scrollbar-thumb {
		background-color: rgba(144, 147, 153, 0.3);
		background-clip: padding-box;
		min-height: 28px;
		border-radius: 5px;
		transition: 0.3s background-color;
	}
	&::-webkit-scrollbar-thumb:hover {
		background-color: rgba(144, 147, 153, 0.5);
	}
}
</style>