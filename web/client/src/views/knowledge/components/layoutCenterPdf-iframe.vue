<template>
	<div class="relative flex flex-col h-full w-full">
		<tagsView></tagsView>
		<div class="center-side-pdf" id="pdf" v-if="pdfUrl">
			<iframe :src="pdfUrl" frameborder="0" ref="iframe" id="iframeId"></iframe>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, computed, onMounted, watch, onUnmounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useKnowledgeState } from '/@/stores/knowledge';

const knowledgeState: any = useKnowledgeState();
const previewData: any = computed(() => knowledgeState.previewData);
const tagsView = defineAsyncComponent(() => import('./tagsView.vue'));
// const url = '/public/pdfjs-3.9.17/web/viewer.html?file=';
// const page = 2
// const iframe = ref()
// const preview = ref(url + pdfUrl);
// window.open(preview.value);
// const pdfUrl = `https://tilake.localhost/wos/images/aiWrite67/858f65c82a2b1dc9ae0d7c1e58e5e60.pdf#page=${page}#view=Fitpage`;
const pdfUrl = ref('')
const iframe = ref()
const isDown = ref(false)
const analysisUrl = () => {
	let {active, currItem, params} = previewData.value;
	let item = currItem;
	let condition = '';
	for(let key in params){
		condition += `#${key}=${params[key]}`
	}
	pdfUrl.value = item.fileUrl + condition
	setTimeout(()=>{
		const iframeWindow = iframe.value.contentWindow;
		const getSelection = () => {
			const range = iframeWindow.getSelection()
			let selectedText = range.toString();
			console.log(selectedText)
		}
		iframeWindow.addEventListener('mousedown', (e) => {
			console.log(e)
			isDown.value = true
			getSelection()
		})
		iframeWindow.addEventListener('mousemove', (e) => {
			// if(isDown.value){
			console.log(e)
			getSelection()
			// }
		})
		iframeWindow.addEventListener('mouseup', (e) => {
			console.log(e)
			isDown.value = false
			getSelection()
		})
		navigator.clipboard.readText().then((res) => {
    console.log('剪切板内容:', res);
}).catch((err) => {
    console.log('获取剪切板内容失败:', err);
});
	// 	var selectedText = iframeWindow.getSelection().toString();
	// console.log(selectedText);
	},)
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