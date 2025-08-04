<template>
	<el-dialog append-to-body :title="fileTitle" v-if="dialogVisible" class="preview-dialog" v-model="dialogVisible"
		:width="props.width ? props.width : '900px'" style="background: #fff !important;" top="2vh" :before-close="handleClose">
		<w-link :href="downloadUrl" class="download" target="_blank" icon >
			下载文档
		</w-link>
		<div class="preview-content" :style="{ height: '86vh', overflowY: 'auto' }">
			<previewPdf :url="downloadUrl" v-if="downloadUrl && downloadUrl.indexOf('pdf') > -1"></previewPdf>
			<previewWord :url="downloadUrl" v-if="downloadUrl && downloadUrl.indexOf('pdf') === -1"></previewWord>
			<!-- <span v-else>旧文件无法预览，需重新上传，后台转成pdf</span> -->
		</div>
	</el-dialog>
</template>

<script setup lang="ts" name="comDocPreview">
import { ref, computed } from 'vue';
import previewPdf from './previewPdf.vue';
import previewWord from './previewWord.vue';
import { useRoute } from 'vue-router';

interface Props {
	width?: string;
}

const props = defineProps<Props>();
const route = useRoute();

const dialogVisible = ref(false);
const downloadUrl = ref('');
const fileTitle = ref('');

// 获取应用信息
const getAppInfo = () => {
	const appInfoStr = localStorage.getItem(`${route.params.appId}`);
	return appInfoStr ? JSON.parse(appInfoStr) : null;
};

// 打开预览
const openPreview = (fileobj: { fileLink: string; fileName: string; transPdfUrl?: string }) => {
	console.log('fileobj', fileobj);
	dialogVisible.value = true;
	if(fileobj.transPdfUrl){
		downloadUrl.value = fileobj.transPdfUrl;
	}else{
		downloadUrl.value = fileobj.fileLink || fileobj.url;
	}
	fileTitle.value = fileobj.fileName || fileobj.title;
};

// 计算属性，用于判断当前文件类型
const isPdf = computed(() => !!fileUrl.value);
const isWord = computed(() => !!wordPreviewUrl.value);

// 关闭对话框
const handleClose = (done: () => void) => {
	dialogVisible.value = false;
	done();
};

// 导出方法
defineExpose({
	openPreview,
});
</script>
<style>
.preview-dialog .el-dialog__close {
	margin-top: 12px;
}

.preview-dialog .el-dialog__title {
	display: inline-block;
	width: 86%;
}
</style>
<style scoped lang="scss">
.preview-content {
	display: flex;
	justify-content: center;
	align-items: center;
	position: relative;

	.docxRef {
		position: absolute;
		width: 100%;
		position: absolute;
		left: 0;
		top: 0;
	}
}

.preview-content img {
	max-height: 100%;
	max-width: 100%;
}

.preview-dialog {
	::v-deep .el-dialog {
		margin-top: 2vh !important;
	}
}

.download {
	position: absolute;
	right: 50px;
	top: 16px;
}
</style>
