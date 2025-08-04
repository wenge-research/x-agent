<template>
	<!-- <div class="cvUpload"> -->
	<div class="cvImg" v-if="backImgUrl">
		<img :src="backImgUrl" />
		<span class="close" @click="delCvImg"><CoolCloseLineWe size="20" /></span>
	</div>
	<div class="uploadImg" v-if="!dialogueInputLoading && isShow" @click="inputFileClick" :style="{ right: leftData + 'px' }">
		<input type="file" ref="inputFile" @change="handleChange" :accept="fileUpdate.accept" />
		<i>
			<CoolImg size="20" color="#9A99AA" />
		</i>
	</div>
	<!-- </div> -->
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useChatStore } from '/@/stores/chat';
import { uploadChatImg, delChatImg } from '/@/api/chat';
import { Message } from 'winbox-ui-next';
import mittBus from '/@/utils/mitt';
import { Session } from '/@/utils/storage';
import { getSuffix, formatbytes } from '/@/utils/utils.ts';

const imgId = ref('');
const leftData = ref(140);
const chatStore = useChatStore();

const inputFile = ref();
const inputFileClick = () => {
	inputFile.value.click();
};
const dialogueInputLoading = computed(() => {
	return chatStore.dialogueLoading;
});
const backImgUrl = computed(() => {
	return chatStore.fileUpdateCV.url;
});

const isShow = ref(false)

const fileUpdate: any = computed(() => chatStore.fileUpdateCV);
const CHUNK_SIZE = 10 * 1024 * 1024; // 10MB

const handleChange = (e: any) => {
	const uploadFile = e.target.files;
	const uploadList = [];
	for (let i = 0; i < uploadFile.length; i++) {
		let file = uploadFile[i];
		let suffixArr = fileUpdate.value.suffixArr;
		let name = file.name;
		let size = file.size;
		let suffix = getSuffix(name);

		if (!suffixArr.includes(suffix)) {
			Message.warning('只允许上传' + suffixArr.join('、') + '格式的文件');
			break;
		}
		let fileParms = {
			name,
			format: suffix,
			size: formatbytes(size),
			file: file,
			url: '',
		};
		// const MAN_SIZE = CHUNK_SIZE; // 10MB
		// if (size > MAN_SIZE) {
		// 	Message.warning('文件大小超出10M');
		// 	break;
		// 	return;
		// }
		uploadList.push(fileParms);
		if (i == uploadFile.length - 1) {
			FileUploads(uploadList);
		}
	}
};
const FileUploads = async (files: any) => {

	chatStore.dialogueLoading = true;
	chatStore.uploadImgStatus = true;
	let filesFilter = files;
	if (!filesFilter.length) {
		return;
	}
	const fd = new FormData();
	filesFilter.forEach((f: any) => {
		fd.append('img', f.file);
	});
	try {
		let res: any = await uploadChatImg(fd);
		chatStore.setFileUpdate({
			list: files,
			status: true,
		});

		chatStore.uploadImgStatus = false;
		chatStore.dialogueLoading = false;
		imgId.value = res.data[0].id;
		chatStore.fileUpdateCV.url = res.data[0].fileUrl;
	} catch (err) {
		chatStore.uploadImgStatus = false;
		chatStore.dialogueLoading = false;
		throw new Error(err);
	}
};
const delCvImg = async () => {
	if(chatStore.uploadImgStatus){
		return
	}
	chatStore.fileUpdateCV.url = '';
	chatStore.setFileUpdate({
		list: [],
		status: false,
	});
	await delChatImg(imgId.value);
};
onMounted(() => {
	mittBus.on('isVedioIng', (data) => {
		leftData.value = data ? 205 : 140;
	});
	isShow.value = Session.get('cvFlag') == 'true' ? true : false
})
onUnmounted(() => {
	mittBus.all.delete('isVedioIng', () => {});
});
</script>

<style scoped lang="scss">
// .cvUpload{
.cvImg {
	position: absolute;
	right: 0;
	top: -161px;
	height: 160px;
	width: 160px;
	border-radius: 8px;
	background: #d8d8d8;
	border: 2px solid #ffffff;
	display: flex;
	align-items: center;
	justify-content: center;
	img {
		max-height: 100%;
		width: auto;
	}
	.close {
		position: absolute;
		right: -1px;
		top: -1px;
		width: 24px;
		height: 24px;
		background: #ffffff;
		border-radius: 0px 8px 0px 12px;
		text-align: center;
		color: #9a99aa;
		cursor: pointer;
		&:hover {
			color: #355eff;
		}
	}
}
.uploadImg {
	width: 32px;
	height: 32px;
	border-radius: 50%;
	// background: rgba(255, 255, 255, 0.9);
	display: flex;
	align-items: center;
	justify-content: center;
	position: absolute;
	right: 140px;
	bottom: 14px;
	cursor: pointer;
	// box-shadow: 0px 6px 20px 0px rgba(30, 64, 175, 0.1);
	input {
		opacity: 0;
		height: 0;
		width: 0;
		display: block;
		overflow: hidden;
	}
	i {
		width: 36px;
		height: 36px;
		display: flex;
		justify-content: center;
		align-items: center;
	}
}

// }
</style>
