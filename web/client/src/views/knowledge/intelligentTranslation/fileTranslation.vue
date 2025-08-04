<template>
	<div class="file-translation">
		<div class="file-translation-tabs">
			<div class="left">
				<el-tabs v-model="leftTabs" @tab-click="leftTabsClick">
					<el-tab-pane label="检测语言" name="auto" key="auto"></el-tab-pane>
					<el-tab-pane label="繁体中文" name="zh-tw" key="zh-tw"></el-tab-pane>
					<el-tab-pane label="简体中文" name="zh" key="zh"></el-tab-pane>
					<el-tab-pane label="英语" name="en" key="en"></el-tab-pane>
				</el-tabs>
			</div>
			<div class="center">
				<img @click="switchingLeftAndRight" src="/src/assets/intelligentTranslation/change-icon.png" alt="" />
			</div>
			<div class="right">
				<el-tabs v-model="rightTabs" @tab-click="rightTabsClick">
					<el-tab-pane label="繁体中文" name="zh-tw"></el-tab-pane>
					<el-tab-pane label="简体中文" name="zh"></el-tab-pane>
					<el-tab-pane label="英语" name="en"></el-tab-pane>
				</el-tabs>
			</div>
		</div>
		<div class="file-translation-content">
			<!-- 上传 -->
			<div v-if="!showFile">
				<el-upload
					class="upload-demo"
					ref="upload"
					action="#"
					:show-file-list="false"
					:before-upload="beforeUpload"
					:http-request="uploadHandler"
					accept=".docx, .pdf, .xls, .xlsx, .pptx"
				>
					<iconpark-icon name="upload-cloud-2-fill" size="96" color="#B4BCCC" class="upload-icon"></iconpark-icon>
					<div>
						<div class="txt1">拖拽文件到此处或<span class="select-btn">选择文件</span></div>
						<div class="txt2">支持文档格式：PDF、Word、Excel、PPTX</div>
						<div class="txt2">大小不可超过15MB</div>
					</div>
				</el-upload>
			</div>
			<div v-else class="upload-after">
				<div class="file-list" :class="[fileUrl && 'bg']">
					<img src="/src/assets/intelligentTranslation/word.svg" alt="" />
					<div style="flex: 1">
						<div class="name">{{ fileParams?.name }}</div>
						<div v-if="fileParams?.size && sizeChange(fileParams.size)" class="size">{{ sizeChange(fileParams.size) }}MB</div>
					</div>
					<iconpark-icon name="close-circle-fill" color="#B4BCCC" size="20" class="close-icon" @click="clearFile"></iconpark-icon>
				</div>
				<div v-if="fileUrl" class="download" @click="downloadFile">
					<iconpark-icon name="download-line" size="20" color="#FFFFFF" class="download-icon"></iconpark-icon>下载文档
				</div>
				<div v-else class="translate" @click="translateTextOrFile">
					<iconpark-icon
						v-if="loading"
						class="loading-icon"
						:class="[loading && 'loading']"
						name="loader-4-line"
						size="20"
						color="#FFFFFF"
					></iconpark-icon
					>{{ loading ? '正在翻译' : '翻译' }}
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import axios from 'axios';
import { Message } from 'winbox-ui-next';

export default {
	data() {
		return {
			leftTabs: 'auto',
			rightTabs: 'zh-tw',
			fileUrl: '',
			showFile: false,
			fileParams: null,
			loading: false,
		};
	},
	methods: {
		leftTabsClick(tab) {
			this.leftTabs = tab.paneName;
			this.fileUrl = '';
		},
		rightTabsClick(tab) {
			this.rightTabs = tab.paneName;
			this.fileUrl = '';
		},
		// 上传校验
		beforeUpload(file) {
			console.log('file', file.type);
			// 上传文件格式仅支持 .docx .pdf
			if (file.type != 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' && file.type != 'application/pdf' && file.type != 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' && file.type != 'application/vnd.ms-excel' && file.type != 'application/vnd.openxmlformats-officedocument.presentationml.presentation') {
				Message.warning('请上传.docx、.pdf、.xls、.xlsx、.pptx格式的文件');
				return false;
			}
			if ((file.size / 1024 / 1024).toFixed(2) > 15) {
				Message.warning('请上传小于15MB的文件');
				return false;
			}
			this.showFile = true;
		},
		// 上传
		async uploadHandler(param) {
			console.log('param=====', param);
			this.fileParams = param.file;
		},
		// 清除上传文件
		clearFile() {
			this.fileParams = {};
			this.fileUrl = '';
			this.showFile = false;
		},
		// 翻译
		async translateTextOrFile() {
			this.loading = true;
			let formData = new FormData();
			formData.append('file', this.fileParams);
			formData.append('srcLang', this.leftTabs);
			formData.append('tgtLang', this.rightTabs);
			formData.append('translateType', 'file');
			try {
				const res = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/intelligentTranslation/translateTextOrFile`, formData, {
					headers: {
						'Content-Type': 'audio/wave;multipart/form-data',
					},
				});
				if (res.data?.code == '000000') {
					console.log('res', res);
					this.fileUrl = res.data?.data?.fileUrlTranslate || '';
				} else {
					Message.error(res.data.msg);
				}
			} catch (error) {
				console.error('Error :', error);
			}
			this.loading = false;
		},
		sizeChange(size) {
			return size ? (size / 1024 / 1024).toFixed(2) : 0;
		},
		// 下载文档
		downloadFile() {
			window.open(this.fileUrl, '_blank');
		},
		// 左右语言切换
		switchingLeftAndRight() {
			if (this.leftTabs == 'auto') return;
			let leftVal = this.leftTabs;
			let rightVal = this.rightTabs;
			this.leftTabs = rightVal;
			this.rightTabs = leftVal;
		},
	},
};
</script>

<style lang="scss" scoped>
.file-translation {
	width: 100%;
	height: 100%;
	&-tabs {
		display: flex;
		width: 100%;
		.left {
			width: 49.5%;
		}
		.center {
			width: 32px;
			height: 48px;
			display: flex;
			justify-content: center;
			align-items: center;

			img {
				width: 24px;
				height: 24px;
				cursor: pointer;
			}
		}
		.right {
			width: 49.5%;
		}
		iconpark-icon {
			cursor: pointer;
		}
	}
	&-content {
		width: 100%;
		height: calc(100% - 48px);
		.upload-demo {
			width: 100%;
			height: 330px;
			background: #f9fafc;
			border-radius: 8px;
			border: 1px solid rgba(0, 0, 0, 0.12);
		}
		.upload-after {
			display: flex;
			flex-direction: column;
			align-items: center;
			width: 100%;
			height: 332px;
			border-radius: 8px;
			border: 1px solid #e1e4eb;

			.file-list {
				display: flex;
				align-items: center;
				width: 62%;
				height: 112px;
				margin: 66px 0 48px;
				padding: 0 40px 0 28px;
				background: #f2f3f5;
				border-radius: 8px;
				border: 1px solid #e1e4eb;

				img {
					width: 56px;
					height: 60px;
					margin-right: 36px;
				}
				.name {
					width: 100%;
					height: 32px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 20px;
					color: #383d47;
					line-height: 32px;
				}
				.size {
					width: 100%;
					margin-top: 8px;
					height: 24px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 18px;
					color: #828894;
					line-height: 24px;
				}
				.close-icon {
					margin-left: auto;
					cursor: pointer;
				}
			}
			.bg {
				border: 1px solid #e1e4eb;
				background: rgba(209, 224, 254, 0.5);
			}
			.download {
				display: flex;
				align-items: center;
				justify-content: center;
				width: 25%;
				height: 40px;
				background: #1c50fd;
				border-radius: 8px;
				font-family: MiSans, MiSans;
				font-size: 16px;
				color: #ffffff;
				cursor: pointer;
				&-icon {
					margin-right: 6px;
				}
			}
			.translate {
				display: flex;
				align-items: center;
				justify-content: center;
				width: 25%;
				height: 40px;
				background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
				border-radius: 8px;
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 16px;
				color: #ffffff;
				cursor: pointer;
			}
			.loading-icon {
				margin-right: 6px;
			}
			.loading {
				animation: rotate 2s linear infinite; /* 2秒钟旋转一次，线性过渡，无限循环 */
			}
			@keyframes rotate {
				from {
					transform: rotate(0deg);
				}
				to {
					transform: rotate(360deg);
				}
			}
		}
	}

	::v-deep(.el-tabs__header) {
		margin-bottom: 0;
		padding: 0 16px;
	}
	::v-deep(.el-tabs__nav-wrap:after) {
		display: none;
	}
	::v-deep(.el-tabs__item) {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 18px;
		color: #383d47;
		padding: 0 24px;
		height: 48px;
	}
	::v-deep(.el-tabs__item.is-active, .el-tabs__item:hover) {
		font-weight: 600;
	}
	::v-deep(.el-tabs__active-bar) {
		background: #1c50fd;
	}
	::v-deep(.el-upload) {
		width: 100%;
		height: 100%;
		.upload-icon {
			margin-right: 28px;
		}
		.txt1 {
			margin-bottom: 4px;
			height: 32px;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 20px;
			color: #383d47;
			line-height: 32px;
		}
		.select-btn {
			color: #1c50fd;
			margin-left: 2px;
		}
		.txt2 {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 16px;
			color: #b4bccc;
			line-height: 22px;
		}
	}
}
</style>