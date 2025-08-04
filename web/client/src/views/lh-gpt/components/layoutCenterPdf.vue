<template>
	<div class="relative flex flex-col h-full w-full">
		<tagsView></tagsView>
		<div class="center-side-pdf" id="pdf" v-if="pdfUrl">
			<div class="doc-viewer" ref="elementRef"></div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, ref, computed, onMounted, watch, nextTick, toRaw, onBeforeUnmount } from 'vue';
import { storeToRefs } from 'pinia';
import { useKnowledgeState } from '/@/stores/knowledge';
import '../../../../public/pdf/image-viewer.umd';
import '../../../../public/pdf/style.css';
import mittBus from '/@/utils/mitt';
const knowledgeState: any = useKnowledgeState();
const previewData: any = computed(() => knowledgeState.previewData);
const tagsView = defineAsyncComponent(() => import('./tagsView.vue'));
const pdfUrl = ref('');
const pageNumber = ref(1);
const appRef: any = ref(null);
const elementRef = ref(null);
const addEldivs = [];
const analysisUrl = () => {
	let { active, currItem, params } = previewData.value;
	let item = currItem;
	// let condition = '';
	// // for(let key in params){
	// // 	condition += `#${key}=${params[key]}`
	// // }
	if (params?.page) {
		pageNumber.value = params?.page;
	}
	pdfUrl.value = item.fileUrl;
	nextTick(() => {
		appRef.value = ImageViewer.createViewer(elementRef.value, {
			defaultUrl: pdfUrl.value,
			resourcePath: 'pdf/',
			disableCORSOriginCheck: true,
			disableDocAnnotation: true, //PDF源文件可能自带 Annotation 数据，如果你不想渲染它们，可以设置为 true
			disableDraggingAndDropping: true, //是否禁用拖拽打开PDF文档，拖拽打开文档的功能默认是开启的
			disablePopupList: false,
			showAnnotationMenu: false,
			appConfig: {
				toolbar: {
					toolbarViewerLeft: {
						pageNumber: false,
					},
				},
			},
			toolbarControl: {
				download: false,
			},
		});
		console.log(appRef.value);
		// 下面手动触发find时候这里几个事件没有反应
		appRef.value.eventBus.on('updatefindcontrolstate', (...e) => console.log(e, appRef.value.findController));
		// 这里能拿到pageMatches和pageMatchesLength但是靠这个没法还原出选区的定位 后面看到的人搜这个事件 研究研究源码继续往下看吧
		appRef.value.eventBus.on('updatetextlayermatches', (...e) => console.log(e, appRef.value.findController));
		appRef.value.eventBus.on('documentinit', (evt) => {
			// 这里能触发搜索 但是不高亮 而且不会触发一系列find事件 后面看到的人研究下吧 query是要搜索的字符串
			// appRef.value.findController.executeCommand(
			// 	{
			// 		type: '',
			// 		query: '1',
			// 		phraseSearch: true,
			// 		caseSensitive: false,
			// 		entireWord: false,
			// 		highlightAll: true,
			// 		findPrevious: false,
			// 		matchDiacritics: false,
			// 	},
			// 	appRef.value
			// );

			setTimeout(() => {
				//定位页码
				appRef.value.eventBus.dispatch('pagenumberchanged', {
					source: appRef.value.toolbar,
					value: pageNumber.value,
				});
				// let selectbox = `<div class="widget selection-widget highlight-wrapper selected" data-z-index="1" data-id="widget_9feef01da806f7714bc8e5a2cee30917" style="left: 107.555px; top: 200.469px; height: 35.0705px; width: 591.006px;"><svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="591.0058352941177px" height="35.07053529411763px"><line class="text-cursor" x1="26.003664705882358" y1="21.994764705882375" x2="26.003664705882358" y2="35.07053529411763"></line><rect class="text-box" width="535.2659441176471" height="13.400747058823526" x="55.73989117647059" y="0"></rect><rect class="text-box" width="26.003664705882358" height="13.075770588235258" x="0" y="21.994764705882375"></rect></svg></div>`
				// let pageBox = document.querySelectorAll('.page')[pageNumber.value - 1 ]
				// let appenDiv = document.createElement('div');
				// appenDiv.className = 'widget selection-widget highlight-wrapper selected';
				// appenDiv.setAttribute('data-id','widget_9feef01da806f7714bc8e5a2cee30917')
				// appenDiv.setAttribute('data-z-index','1')
				// appenDiv.setAttribute('style','left: 107.555px; top: 300.469px; height: 35.0705px; width: 591.006px;')
				// appenDiv.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="591.0058352941177px" height="35.07053529411763px">
				// 	<rect class="text-box" width="591.006" height="35.0705" x="0" y="0"></rect>
				// </svg>`
				// console.log(appenDiv)
				// pageBox.append(appenDiv)
				// addEldivs.push(appenDiv)
				appRef.value.UIManager.eventBus.on('selection-changed', (evt) => {
					// let li_els = document.querySelectorAll('.pdf-document-viewer .annotation-control-bar__bottom li');
					// console.log(li_els)
					// console.log(evt)
					// var selectedText = window.getSelection().toString();
					// console.log(selectedText);
				});
				appRef.value.UIManager.eventBus.on('text-selected', (evt) => {
					console.log(evt);
					// addEldivs.map(el=>{
					// 	el.remove();
					// })
					// console.log(evt)
					mittBus.emit('instructSet', evt.text);
					appRef.value.eventBus.on('ctrlChange', (evt) => {
						console.log(evt);
					});
				});
				appRef.value.UIManager.eventBus.on('show-widget-toolbar', (evt) => {
					// console.log(evt);
				});
				appRef.value.UIManager.eventBus.on('text-copied', (evt) => {
					// console.log(evt);
				});
				appRef.value.UIManager.eventBus.on('widget-created', (evt) => {
					// console.log(evt);
				});
				appRef.value.UIManager.eventBus.on('widget-drawing-end', (evt) => {
					// console.log(evt);
				});
			});
		});
		// appRef.value.SquareAnnotationElement()
		// appRef.value.changeAnnotationType(ImageViewer.AnnotationType.Highlight);
		// appRef.value.registerAnnotationOperation(
		// 	AnnotationType[AnnotationType.SelectText],
		// 	'bottom',
		// 	{
		// 		type: 'translate',
		// 		classNames: ['my-translate'],
		// 		dataset: [['my-dataset', 'true']],
		// 		allowChecked: false,
		// 		handleDOMEvents: {
		// 		click (ctrl, event) {
		// 			// do something
		// 		}
		// 		},
		// 		render() {
		// 		const li = document.createElement('li');
		// 		li.innerText = '译';
		// 		return li;
		// 		},
		// 	},
		// 	);
		appRef.value.addEventListener('annot-click', ({ event, annotation }) => {
			// 获取 Annotation 原始数据
			const annotData = annotation.toJSON();
			console.log(annotData);
			// console.log()
			appRef.value.UIManager.copy().then((val) => {
				console.log(val);
			});
			// console.log(appRef.value.annotationService.copy())

			// appRef.value.eventBus.on('annot-removed', (e) => {
			// 	console.log('annot-removed',e)
			// },{},false)
		});
	});
};
const AppDestroy = () => {
	toRaw(appRef.value).destroy();
};
onMounted(() => {
	nextTick(() => {
		analysisUrl();
	});
	watch(
		() => previewData.value,
		(newVal: any) => {
			if (newVal && appRef.value) {
				pdfUrl.value = '';
				if (appRef.value) {
					AppDestroy();
				}
				setTimeout(() => {
					analysisUrl();
				});
			}
		},
		{ immediate: true, deep: true }
	);
});
onBeforeUnmount(() => {});
</script>

<style scoped lang="scss">
.center-side-pdf {
	width: 100%;
	height: 100%;
	padding: 0 10px;
	box-sizing: border-box;
	.doc-viewer {
		height: 100%;
	}
}
</style>
<style>
html[dir='ltr'] [data-id='toolbarViewerRight'] {
	display: none;
}
.pdf-document-viewer .toolbar {
	z-index: 999;
}
.pdf-document-viewer .annotation-control-bar__bottom {
	opacity: 0;
	display: none;
}
/* .pdf-document-viewer .annotation-control-bar__bottom li:nth-child(1){
	opacity: 1;
	display: inline-flex;
} */
</style>
