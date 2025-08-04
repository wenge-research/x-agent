<template>
	<div class="layoutCenterInit center-side-parent" id="bg-main">
		<div class="centerInit" :class="{ centerInitItemMobile: isMobile }">
			<!-- <w-upload
				class="init"
				:accept="fileUpdate.accept"
				action
				multiple
				draggable
				:custom-request="handleChange"
				:show-file-list="false"
			>
				<template #upload-button>
					<img class="initimg" :src="centerImg" alt="" />
					<div>将文件拖到此处，或<span>点击上传</span></div>
					<p>支持上传1个或多个文件</p>
					<p>仅支持上传word/pdf/txt文件，限制100000字</p>
				</template>
			</w-upload> -->
			<div class="init" @click="inputFileClick">
				<input type="file" ref="inputFile" @change="handleChange" style="opacity: 0; height: 0" :accept="fileUpdate.accept" multiple />
				<img class="initimg" :src="upload_init" alt="" />
				<div><span>点击上传</span></div>
				<!-- 将文件拖到此处，或 -->
				<p>支持上传一个或多个文件</p>
				<p>仅支持上传word/pdf/txt文件，{{ isPublic ? '限制' + ThousandWithNumber(knowledgesSize.capacity) + '字' : '不限制' }}</p>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, defineAsyncComponent, onMounted, computed } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useRouter } from 'vue-router';
import { fileUploadBatch } from '/@/api/knowledge';
import { useKnowledgeState } from '/@/stores/knowledge';
import { Message } from 'winbox-ui-next';
import { getSuffix, formatbytes } from '/@/utils/utils.ts';
import upload_init from '/@/assets/knowledge/upload_init.png';
import { v4 as uuidv4 } from 'uuid';
import { Session } from '/@/utils/storage';
import { ThousandWithNumber } from '/@/utils/format.ts';

const knowledgeState = useKnowledgeState();
const currentLibrary: any = computed(() => knowledgeState.currentLibrary);
const knowledgeFileList: any = computed(() => knowledgeState.fileList);
const knowledgesSize: any = computed(() => knowledgeState.knowledgesSize);
const isPublic: any = computed(() => {
	if (currentLibrary.value.creator == Session.get('userId')) {
		return true;
	}
	let status = currentLibrary.value.authority != 2;
	return !status;
});
const router = useRouter();
const isShow = ref(true);
// 移动端自适应相关
const { isMobile } = useBasicLayout();
const inputFile = ref();
const inputFileClick = () => {
	inputFile.value.click();
};
const fileUpdate: any = computed(() => knowledgeState.fileUpdate);
const CHUNK_SIZE = 10 * 1024 * 1024; // 10MB
const fileList: any = ref([]);
const handleChange = (e: any) => {
	const uploadFile = e.target.files;
	const uploadList = [];
	console.log(uploadFile);
	for (let i = 0; i < uploadFile.length; i++) {
		let file = uploadFile[i];
		let suffixArr = fileUpdate.value.suffixArr;
		let name = file.name;
		let size = file.size;
		let suffix = getSuffix(name);

		if (!suffixArr.includes(suffix)) {
			Message.warning('只允许导入' + suffixArr.join('、') + '格式的文件');
			break;
		}
		let fileParms = {
			name,
			format: suffix,
			size: formatbytes(size),
			file: file,
			id: uuidv4(),
			type: 0,
			url: '',
			isState: 2,
		};
		// const MAN_SIZE = CHUNK_SIZE * 5; // 50MB
		// if (size > MAN_SIZE * 2 * 2) {
		// 	Message.warning('文件大小超出200M');
		// 	break;
		// 	return;
		// }
		uploadList.push(fileParms);
		if (i == uploadFile.length - 1) {
			FileUploads(uploadList);
		}
	}
};
const handleDelete = (item: any) => {
	let index = fileList.value.findIndex((f: any) => f.id == item.id);
	fileList.value.splice(index, 1);
};
const FileUploads = async (files: any) => {
	// 循环上传
	let filesFilter = files;
	// let filesFilter = await files.filter((f: any) => {
	// 	return f.isState;
	// });
	if (!filesFilter.length) {
		return;
	}
	// return
	const fd = new FormData();
	filesFilter.forEach((f: any) => {
		fd.append('files', f.file);
	});
	fd.append('folderId', knowledgeFileList.value.id);
	fd.append('knowledgeId', currentLibrary.value.id);
	let res: any = fileUploadBatch(fd);
	knowledgeState.setFileUpdate({
		list: files,
		status: true,
	});
	return;
};
</script>

<style scoped lang="scss">
.layoutCenterInit {
	position: relative;
	height: 100%;
	overflow: auto;
	flex: 1;
}
.centerInit {
	position: absolute;
	left: 50%;
	top: 50%;
	transform: translate(-50%, -50%);
	max-width: 70%;
	min-width: 553px;
	height: 20vw;
	min-height: 340px;
	// background: rgba(255, 255, 255, 0);
	border-radius: 12px;
	border: 1px dashed #d0d5dc;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	&:hover {
		border-color: #355eff;
	}
	&.centerInitItemMobile {
		padding-top: 10px;
		height: 100%;
		padding: 0 0 0 20px;
		overflow-y: auto;
	}
	:deep(.w-upload),
	.init {
		// background-color: rgba(255, 255, 255, 0.5) !important;
		text-align: center;
		padding-top: 50px;
		padding-bottom: 30px;
		> input {
			display: block;
		}
		.initimg {
			display: inline-block;
			width: 120px;
		}
		div {
			margin-top: 30px;
			margin-bottom: 15px;
			font-size: var(--font20);
			font-family: PingFangSC-Medium, PingFang SC;
			font-weight: 500;
			color: #181b49;
			line-height: 28px;
			span {
				color: #355eff;
			}
		}
		p {
			font-size: var(--font14);
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #9a99aa;
			line-height: 24px;
		}
	}
}
</style>
