<template>
	<div v-if="visible" class="opinions-and-suggestions">
		<div class="opinions-and-suggestions-content">
			<div class="tit-cac">
				<iconpark-icon name="arrow-left-wide-line" size="16" color="#494C4F" @click.stop="close"></iconpark-icon>
				{{ title }}
			</div>
			<div class="content-cac">
				<ul class="list">
					<li
						v-for="item in typeList"
						:key="item.id"
						class="list-item"
						:class="[item.id == currentId ? 'bg' : '']"
						@click="
							currentId = item.id;
							currentType = item.menuName;
						"
					>
						<iconpark-icon v-if="currentId == item.id" :name="item.menuIcon" color="#fff" size="28"></iconpark-icon>
						<iconpark-icon v-else :name="item.menuIcon" color="#9197AB" size="28"></iconpark-icon>
						<span class="list-item-name">{{ item.menuName }}</span>
					</li>
				</ul>
				<el-form :model="params" style="margin-top: 22px">
					<el-form-item label="我要反馈" prop="content" :rules="[{ required: true, message: '请输入姓名', trigger: 'blur' }]">
						<el-input v-model="params.content" type="textarea" placeholder="输入反馈内容" style="width: 100%" :rows="6" />
						<div class="upload-bottom">
							<div class="outer" v-for="(item, index) in fileList" :key="index">
								<img :src="item.url" alt="" class="upload-img" />
								<iconpark-icon name="close-circle-fill" class="delete-icon" size="18" color="#9197AB" @click="deleteImg(item)"></iconpark-icon>
							</div>

							<el-upload
								v-if="fileList.length < 3"
								class="upload-demo"
								drag
								action="#"
								multiple
								:show-file-list="false"
								:before-upload="beforeUpload"
								:http-request="uploadHandler"
								:accept="'.jpg,.jpeg,.png,.gif,.webp,.svg'"
							>
								<img src="/src/assets/sz-cac/upload.png" alt="" class="upload" />
							</el-upload>
						</div>
					</el-form-item>

					<div class="contact">联系方式<span>(选填)</span></div>
					<el-input v-model="params.createUserName" placeholder="姓名" />
					<el-input v-model="params.createUserPhone" placeholder="联系电话" />
					<div class="submit-btn" @click="onSubmit">提交</div>
				</el-form>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, ref, onMounted, watch } from 'vue';
import { Message } from 'winbox-ui-next';
import axios from 'axios';
import { apiAddSuggestionFeedback } from '/@/api/chat/index';
import { useRoute } from 'vue-router';

const props = defineProps({
	visible: {
		type: Boolean,
		required: true,
	},
	content: {
		type: String,
		required: true,
	},
	title: {
		type: String,
		required: true,
	},
});

const emit = defineEmits(['close']);
const isLoaded = ref(false);
const currentId = ref(1);
const currentType = ref('使用建议');
const params = ref({ content: '', createUserName: '', createUserPhone: '' });
const typeList = ref([
	{
		id: 1,
		menuName: '使用建议',
		menuIcon: 'pencil-ruler-2-fill',
	},
	{
		id: 2,
		menuName: 'BUG反馈',
		menuIcon: 'bug-fill',
	},
	{
		id: 3,
		menuName: '操作体验',
		menuIcon: 'compass-fill',
	},
	{
		id: 4,
		menuName: '其他反馈',
		menuIcon: 'mail-fill',
	},
]);
const onIframeLoad = () => {
	isLoaded.value = true;
};
const close = () => {
	emit('close');
};
const route = useRoute();
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo : '';
};
const addSuggestionFeedback = () => {
	const imgsUrl = fileList.value?.map((item) => item.urlPath)?.join(',');
	apiAddSuggestionFeedback({
		...params.value,
		applicationId: getAppDetail()?.applicationId,
		type: currentType.value,
		imgsUrl,
	}).then((res) => {
		if (res.code == '000000') {
			console.log('res', res);
			Message.success('反馈成功');
			close();
		} else {
			Message.warning(res.data?.msg);
		}
	});
};
const onSubmit = () => {
	if (!params.value.content) return Message.warning('反馈内容不能为空');
	addSuggestionFeedback();
};
const fileData = ref({});
// 上传校验
const beforeUpload = (file) => {
	const isImageType = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/svg+xml', 'image/bmp', 'image/tiff'].includes(
		file.type.toLowerCase()
	);
	if (!isImageType) {
		Message.warning('文件格式错误');
		return false;
	}
};
const fileList = ref([]);
// 删除图片
const deleteImg = (data: any) => {
	console.log('data', data);
	console.log('fileList', fileList.value);
	fileList.value = fileList.value.filter((item) => item.id != data.id);
};
// 上传
const uploadHandler = async (param) => {
	const formData = new FormData();
	formData.append('file', param.file);
	formData.append('rename', true);
			formData.append('filePath', 'agent_source');
	try {
		const response = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/wos/file/upload`, formData, {
			headers: {
				'Content-Type': 'audio/wave;multipart/form-data',
			},
		});
		if (response.status === 200) {
			fileData.value = response?.data?.data?.length ? response.data.data[0] : {};
			fileList.value.push(fileData.value);
			console.log('fileList', fileList);
		}
	} catch (error) {
		console.error('Error :', error);
	}
};
watch(
	() => props.visible,
	(newVal: any) => {
		currentType.value = '使用建议';
		currentId.value = 1;
		params.value.content = '';
		params.value.createUserName = '';
		params.value.createUserPhone = '';
		fileData.value = {};
		fileList.value = [];
	},
	{
		immediate: true,
		deep: true,
	}
);
onMounted(() => {
	currentType.value = '使用建议';
	currentId.value = 1;
	params.value.content = '';
	params.value.createUserName = '';
	params.value.createUserPhone = '';
	fileData.value = {};
	fileList.value = [];
});
</script>

<style scoped lang="scss">
.opinions-and-suggestions {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 1000;

	::v-deep .el-form-item {
		flex-direction: column !important;
		align-items: flex-start !important;
		margin-bottom: 24px;
	}
	::v-deep .el-form-item__content {
		width: 100%;
	}
	::v-deep .el-form-item__label {
		font-family: MiSans, MiSans;
		font-weight: 600;
		font-size: 16px;
		color: #313436;
		line-height: 24px;
	}
	::v-deep .el-textarea__inner {
		background: #f4f6f9;
		border-radius: 4px 4px 0 0;
		border: none;
		box-shadow: none;
		padding: 10px 12px;
	}
	::v-deep .el-upload-dragger {
		width: 64px;
		height: 64px;
		border-radius: 4px;
		border: 1px solid #d0d5dc;
		padding: 0;
		display: flex;
		align-items: center;
		justify-content: center;
		background: transparent;
	}
	::v-deep .upload-demo {
		height: 64px;
	}
	::v-deep .el-input__wrapper {
		margin-top: 12px;
		background: #f4f6f9;
		border-radius: 4px;
		height: 48px;
		border: none;
		box-shadow: none;
		.el-input__inner {
			height: 100%;
			font-size: 16px;
		}
	}
	.tit-cac {
		background: #f4f6f9;
		// position: fixed;
		width: 100%;
		// bottom: 85vh;
		text-align: center;
		padding: 10px 0;
		font-family: MiSans, MiSans;
		font-weight: 600;
		font-size: 18px;
		line-height: 24px;
		color: #434649;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		position: relative;
		.btn {
			position: absolute;
			font-size: 20px;
			right: 10px;
			top: 12px;
		}
		iconpark-icon {
			position: absolute;
			top: 14px;
			left: 24px;
		}
	}
	.content-cac {
		height: 100%;
		overflow-y: auto;
		padding: 4px 12px 0;
		.upload-bottom {
			width: 100%;
			height: 88px;
			display: flex;
			align-items: center;
			background: #f4f6f9;
			border-radius: 0 0 4px 4px;
			margin-top: -1px;
			padding: 12px;
			.upload-img {
				width: 64px;
				height: 64px;
				margin-right: 12px;
				object-fit: cover;
				border-radius: 4px;
			}
			.outer {
				position: relative;
				.delete-icon {
					position: absolute;
					right: 6px;
					top: -7px;
					cursor: pointer;
				}
			}
		}
		.upload {
			width: 26px;
			height: 26px;
		}
		.contact {
			height: 24px;
			font-family: MiSans, MiSans;
			font-weight: 500;
			font-size: 16px;
			color: #313436;
			line-height: 24px;

			span {
				margin-left: 10px;
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 16px;
				color: #b4bccc;
				line-height: 24px;
			}
		}
		.submit-btn {
			margin-top: 24px;
			width: 100%;
			height: 48px;
			line-height: 48px;
			text-align: center;
			background: #2155c9;
			border-radius: 4px;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 16px;
			color: #ffffff;
		}
		.list {
			display: flex;
			justify-content: flex-start;
			flex-wrap: wrap;
			gap: 11px;
			&-item {
				display: flex;
				justify-content: center;
				flex: 0 0 48%;
				height: 56px;
				border-radius: 4px;
				background: #f4f6f9;
				align-items: center;
				margin-top: 12px;

				&-name {
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #9197ab;
					text-align: left;
					font-style: normal;
					margin-left: 15px;
				}

				img {
					width: 24px;
					height: 24px;
				}
			}
			.bg {
				background: #2d82e4;
				.list-item-name {
					color: #ffffff;
				}
			}
		}
	}
}

.opinions-and-suggestions-content {
	// position: fixed;
	background: #fff;
	width: 100%;
	height: 100vh;
	bottom: 0;
}

.popup-content iframe {
	width: 100%;
	height: 100%;
	border: none;
}
</style>