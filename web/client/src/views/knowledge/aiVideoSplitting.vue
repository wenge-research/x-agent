<template>
	<div class="file-parsing">
		<div class="file-parsing-head">
			<img class="logo" src="/@/assets/aiVideoSplitting/logo.svg" alt="" />
			<div class="user-info">
				<div class="name">欢迎您，admin</div>
				<img class="avatar" src="/@/assets/stockExchangeSZ/user.png" alt="" />
			</div>
		</div>
		<div class="file-parsing-content">
			<div v-if="!start" class="before-upload">
				<img class="img-1" src="/@/assets/aiVideoSplitting/img-1.png" alt="" />
				<div class="tip-text">上传需要拆分的视频</div>
				<div class="upload">
					<div v-if="!fileData.id" v-loading="uploadLoading" class="no-file">
						<el-upload
							class="upload-demo"
							ref="upload"
							action="#"
							:show-file-list="false"
							:before-upload="beforeUpload"
							:http-request="uploadHandler"
							accept=".mp4,.mov,.mkv"
						>
							<img class="upload-icon" src="/@/assets/stockExchangeSZ/upload.svg" alt="" />
							<div>
								<div class="txt1">拖拽视频到此处或<span class="select-btn">选择视频</span></div>
								<div class="txt2">支持视频格式：mp4、mov、mkv</div>
							</div>
						</el-upload>
					</div>
					<div v-else class="file">
						<img class="file-img" src="/@/assets/aiVideoSplitting/video.png" alt="" />
						<div style="flex: 1">
							<div class="file-name" @click="openFileHandler">{{ fileData.fileName }}</div>
							<div class="file-size">
								{{ opreateSize(fileData.fileSize) }} mb <img class="check-icon" src="/@/assets/stockExchangeSZ/check-icon.svg" alt="" />
							</div>
						</div>
						<div class="close-btn" @click="deleteFileHandler"><img src="/@/assets/stockExchangeSZ/close-icon.svg" alt="" /></div>
					</div>
				</div>
				<div class="start-btn" @click="startSplitting">开始拆分</div>
			</div>
			<div v-else class="left">
				<div class="left-head">
					<div class="file">
						<img class="file-img" src="/@/assets/aiVideoSplitting/video.png" alt="" />
						<div style="flex: 1">
							<div class="file-name" @click="openFileHandler">{{ fileData.fileName }}</div>
							<div class="file-size">
								{{ opreateSize(fileData.fileSize) }} mb <img class="check-icon" src="/@/assets/stockExchangeSZ/check-icon.svg" alt="" />
							</div>
						</div>
						<div class="close-btn" @click="deleteFileHandler">清空视频<iconpark-icon name="close-circle-line" color="#0674fe"></iconpark-icon></div>
					</div>
				</div>
				<div class="left-center">
					<!-- <img class="left-center-star" :class="[chouLoading && 'left-center-star-move']" src="/@/assets/stockExchangeSZ/left-star.png" alt="" /> -->
					<img v-if="chouLoading" class="left-center-star" src="/@/assets/stockExchangeSZ/left-star.png" alt="" />
					<div v-if="chouLoading" class="left-center-txt">正在分析视频</div>
					<div v-else class="left-center-txt">解析完成</div>
				</div>
				<div class="left-progress">
					<div class="left-progress-inner" :style="{ width: progressInnerWidth + '%' }"></div>
				</div>
				<div class="left-bottom">
					<!-- <div
						v-if="text"
						class="answerOutput markdown-body"
						v-html="text"
						:class="['markdown-body-as-img-' + isMarkdownImg.length, true ? 'hljs-atom-one-light' : 'hljs-atom-one-dark']"
					></div> -->
					<ul class="left-bottom-content">
						<li v-for="(item, index) in videoContentList" :key="index" class="left-bottom-content-item">
							<div class="left-bottom-content-item-info">
								<div class="videoPreview">
									视频链接<span v-if="item.videoPreview"><a :href="`https://sourceyoya.localhost/${item.videoPreview}`" target="_blank">{{ `https://sourceyoya.localhost/${item.videoPreview}` }}</a></span>
								</div>
								<div class="caption">{{ item.caption }}</div>
							</div>
							<div class="left-bottom-content-item-video">
								<video v-if="item.videoPreview" id="myVideo" controls :src="`https://sourceyoya.localhost/${item.videoPreview}`"></video>
							</div>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import axios from 'axios';
import { Message } from 'winbox-ui-next';
import { formatDate } from '/@/utils/formatTime';
import useEventSource from '/@/hooks/useEventSource';
import MarkdownIt from 'markdown-it';
import { setScrollPosition } from '/@/utils/other';
import { apiAddCatalog, apiQueryVideoInfo } from '/@/api/chat';
export default {
	data() {
		return {
			biddingCustomizePrompt: '',
			bidCustomizePrompt: '',
			showPopver: false,
			biddingShowPopover: false,
			bidShowPopover: false,
			fileData: {}, // 招标文件信息
			fileDataBid: {}, // 投标文件信息
			biddingData: {}, // 招标信息
			bidData: {}, // 招标信息
			drawerVisible: false,
			drawerLoading: false,
			chouLoading: false,
			chouLoadingBid: false,
			uploadLoading: false,
			uploadLoadingBid: false,
			promptValue: '',
			module: '',
			examinationList: [], // 审查列表
			biddingText: '',
			bidText: '',
			biddingBrCount: 0,
			bidBrCount: 0,
			timer: null,
			start: false,
			mdi: null,
			isMarkdownImg: [],
			curText: '',
			progressInnerWidth: 0,
			requestId: '',
			videoContent: '',
			isAnalysis: false,
			videoContentList: [],
			progressIncrement: 1, // 每次收到数据时增加的进度值
		};
	},
	computed: {
		text() {
			const value = this.biddingText ?? '';
			if (this.biddingText) {
				// 针对markdown格式、//n 做特殊处理
				let strsss = this.biddingText ? this.biddingText.replace(/\\n/g, '\n') : '';
				// let startText = ['&e', '&en', '&ens', '&ensp', , '</', '</b', '</br', '<b', '<br', '<h', '<hr', '<hr '];
				// let endText = ['&ensp;', '>', '查询或咨询。'];
				// startText.forEach((i) => {
				// 	if (strsss.endsWith(i)) {
				// 		curText.value = strsss?.slice(0, strsss.lastIndexOf(i));
				// 	}
				// });
				// endText.forEach((i) => {
				// 	if (strsss.endsWith(i)) {
				// 		curText.value = '';
				// 	}
				// });
				// const value = strsss
				// 	? textToMarkdown(`[<span class='link-btn'>测试链接跳转&nbsp;&nbsp;&gt;</span>](http://coolbox.localhost/#/detail/12888)`, props.citations)
				// 	: '';
				const value = strsss ? this.textToMarkdown(this.curText || strsss, []) : '';
				let htmlStr = this.mdi.render(value);
				htmlStr = this.addLoadEventToImgTags(htmlStr);

				return htmlStr;
			}
			return value;
		},
	},
	watch: {
		videoContentList: {
			handler(n) {
				if(!this.chouLoading) return;
				if(!n.length) {

				}
			}
		}
	},
	mounted() {
		this.initMarkDown();
	},
	methods: {
		// 字节转换kb
		opreateSize(num) {
			return num ? (num / 1024 / 1024).toFixed(2) : 0;
		},
		// 打开上传文件
		openFileHandler() {
			window.open(this.fileData.urlPath, '_blank');
		},
		// 删除上传文件
		deleteFileHandler() {
			this.fileData = {};
			this.biddingText = '';
			this.biddingBrCount = 0;
			this.requestId = '';
			this.videoContentList = [];
			this.start = false;
		},
		// 上传校验
		beforeUpload(file) {
			console.log('file', file.type);
			if (file.type && file.type != 'video/mp4' && file.type != 'video/quicktime' && file.type != 'video/x-matroska') {
				Message.warning('文件格式错误');
				return false;
			}
		},
		// 上传
		async uploadHandler(param) {
			const formData = new FormData();
			console.log(formData, '第一次的');
			formData.append('file', param.file);
			formData.append('rename', true);
			formData.append('filePath', 'agent_source');
			console.log(formData, '最后一次的');
			try {
				this.uploadLoading = true;
				const response = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/wos/file/upload`, formData, {
					headers: {
						'Content-Type': 'audio/wave;multipart/form-data',
					},
				});
				if (response.status === 200) {
					this.fileData = response?.data?.data?.length ? response.data.data[0] : {};
				}
				this.uploadLoading = false;
			} catch (error) {
				this.uploadLoading = false;
				console.error('Error :', error);
			}
		},
		// 开始分析
		startExtracting() {
			if (!this.fileData.urlPath) {
				return Message.warning('请上传视频');
			}
			this.chouLoading = true;
			this.progressInnerWidth = 1; // 当前进度值
			const progressIncrement = 0.2; // 每次收到数据时增加的进度值
			this.start = true;
			const clientId = Math.round(Math.random() * 1000000000000000000).toString(36);
			const abortController = new AbortController();
			let url = import.meta.env.VITE_API_URL + import.meta.env.VITE_STREEM_PATH + import.meta.env.VITE_BASE_API_URL + '/workflow/dialogueRun';
			const matches = this.fileData?.fileName?.match(/^(.+)\.([^.]+)$/);
			console.log('matches', matches);
			let filename, extension;
			if (matches) {
				filename = matches[1];
				extension = matches[2];
			} else {
				console.log('未找到文件后缀');
			}

			useEventSource(
				url,
				{
					componentId: localStorage.getItem(`${this.$route.params.appId}appId`),
					inputs: {
						question: [
							{
								type: 'doc', // 文档类型
								transferMethod: 'remote_url', // 暂时只支持传文件链接，不支持本地文件路径，写死
								remoteUrl: this.fileData.urlPath, // 文件远程链接
								filename,
								extension, // 文件拓展名
							},
						],
					},
					clientId,
					clientType: 'PC',
				},
				abortController,
				(list) => {
					this.biddingText = JSON.parse(list) ? JSON.parse(list)?.answer : '';
					const finishReason = JSON.parse(list) ? JSON.parse(list)?.finishReason : '';
					console.log('finishReason', finishReason);
					if (!finishReason && this.progressInnerWidth != 100) {
						// 动态增加进度
						this.progressInnerWidth = Math.min(this.progressInnerWidth + progressIncrement, 98);
					}
					if (finishReason) {
						this.progressInnerWidth = 100;
						this.chouLoading = false;
					}
					if (this.biddingText) {
						setScrollPosition('.markdown-body', 'auto');
					}
				}
			);
			// 假设最新数据元素有一个唯一的类名或标识符，比如.latest-data
			// 获取最新元素
			const latestElement = document.querySelector('.start-btn');

			// 将鼠标光标定位到最新元素位置
			if (latestElement) {
				latestElement.scrollIntoView({ behavior: 'smooth', block: 'end' });
			}
		},
		initMarkDown() {
			this.mdi = new MarkdownIt({
				html: true,
				linkify: false,
				highlight(code, language) {
					const validLang = !!(language && hljs.getLanguage(language));
					if (validLang) {
						const lang = language ?? '';
						return highlightBlock(hljs.highlight(code, { language: lang }).value, lang);
					}
					return highlightBlock(hljs.highlightAuto(code).value, '');
				},
			});
		},
		textToMarkdown(text, citations) {
			// 判断json格式
			try {
				const jsonObj = JSON.parse(text); // 将字符串解析为一个对象
				if (typeof jsonObj === 'object' && jsonObj !== null) {
					const jsonFormatted = JSON.stringify(jsonObj, null, 2);
					text = '```json\n' + jsonFormatted + '\n```';
				}
			} catch (error) {}

			// 截取:::r 结束的字符串
			if (text.endsWith('\n:')) {
				text = text.slice(0, -3);
			}
			if (text.endsWith('\n::')) {
				text = text.slice(0, -4);
			}
			if (text.endsWith('\n:::')) {
				text = text.slice(0, -5);
			}
			if (text.endsWith('\n:::r')) {
				text = text.slice(0, -6);
			}
			if (text.endsWith('\n:::r ')) {
				text = text.slice(0, -7);
			}

			// 初始化一个空字符串，用于存储结果
			let result = '';
			if (!citations) {
				return text;
			}
			// 是否包含图片
			var regexImage = /!\[.*?\]\(.*?\)/g;
			// isMarkdownImg.value = regexImage.test(text);
			this.isMarkdownImg = text.match(regexImage) || [];

			// 遍历对象的 res_info 属性，获取每个键值对
			for (const key in citations) {
				// 获取当前键值对的值，即一个子对象
				const value = citations[key];
				const { no, fileUrl = '#', fileName } = value;
				// 将子对象的 title 和 context 属性拼接成 markdown 的链接引用格式，并添加到结果字符串中
				if (route.path.indexOf('chat') != -1) {
					result += `[${no}]: ${fileUrl ? encodeURI(fileUrl) : null} \'${fileName}\'\n\n`;
				} else {
					result += `[${no}]: ${fileUrl ? null : null} \'${fileName}\'\n\n`;
				}
			}
			// 在结果字符串末尾添加一个空行
			result += '\n';
			// 将对象的 res 属性的值，即一个字符串，替换其中的数字为上标引用，并在每个数字后面加上一个空格，并添加到结果字符串中
			result += text.replace(/\[(\d+)\]/g, (match, p1) => {
				// 使用回调函数替换匹配的内容
				const num = Number(p1); // 将 $1 转换为数字
				// return `<a href="${res_info[num].title}">${res_info[num].context || ''}</a>`; // 返回对应的 context
				return `[${num}][${num}]`; // 返回对应的 context
			});
			// 返回结果字符串
			return result;
		},
		addLoadEventToImgTags(htmlString) {
			var regex = /(<img\s+[^>]*)(>)/g;
			var replacement = `$1  onclick="showPreviewImg(event)" $2`;
			// if (appId == '20') {
			// 	replacement = `$1  onclick="showPreviewImg(event)" onload="document.querySelector('.center-side').scrollTop = 999999999"$2`;
			// }
			// var replacement = `$1  onclick="showPreviewImg(event)" onload="document.querySelector('.center-side').scrollTop = 999999999"$2`;
			// var replacement = `$1  onclick="showPreviewImg(event)" $2`;
			var newHtmlString = htmlString.replace(regex, replacement);

			return newHtmlString;
		},
		// 开始拆分
		async startSplitting() {
			if (!this.fileData.urlPath) {
				return Message.warning('请上传视频');
			}
			this.chouLoading = true;
			this.progressInnerWidth = 1; // 当前进度值
			this.start = true;
			await this.addCatalog(this.fileData.urlPath);
		},
		// 视频解析
		async addCatalog(videoUrl) {
			const res = await apiAddCatalog({ videoUrl });
			this.progressInnerWidth = Math.min(this.progressInnerWidth + this.progressIncrement, 98);
			if (res.code == '000000' && res.data?.requestId) {
				this.requestId = res.data.requestId;
				this.queryVideoInfo();
			}
		},
		// 视频内容解析
		async queryVideoInfo() {
			if (!this.requestId) return;
			try {
				this.progressInnerWidth = Math.min(this.progressInnerWidth + this.progressIncrement, 98);
				const res = await apiQueryVideoInfo({ requestId: this.requestId });
				if (res.code == '000000') {
					const data = res.data?.dataEntriesList || [];
					this.isAnalysis = res.data?.status == 1 ? false : true;
					if (data?.length) {
						this.videoContent = data[data.length - 1]?.caption;
						this.videoContentList = data.filter(item => item.videoPreview);
						// videoUrl.value = data[data.length - 1]?.videoUrl;
						this.progressInnerWidth = 100;
						this.chouLoading = false;
					}
					if (res.data?.status == 0) {
						// 如果状态为0，延迟后再次调用
						setTimeout(this.queryVideoInfo, 5000); // 1秒后重试
					}
				}
			} catch (error) {
				this.isAnalysis = false;
			}
		},
	},
};
</script>

<style lang="scss" scoped>
@import './style.scss';
@import '/@/theme/mixins/index.scss';

.file-parsing {
	width: 100vw;
	height: 100vh;
	background: url('/@/assets/stockExchangeSZ/bg.png') no-repeat;
	background-size: 100% 100%;
	position: relative;
	&-head {
		padding: 0 34px 0 32px;
		height: 104px;
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: space-between;
		.logo {
			width: 222px;
			height: 56px;
		}
		.user-info {
			display: flex;
			align-items: center;
			.name {
				font-family: MiSans, MiSans;
				font-size: 14px;
				color: #434649;
			}
			.avatar {
				width: 24px;
				height: 24px;
				margin-left: 8px;
			}
		}
	}
	&-content {
		height: calc(100vh - 104px);
		// width: 63%;
		width: 1200px;
		padding: 24px 0 32px;
		margin: 0 auto;
		display: flex;
		justify-content: space-between;
		.left {
			// width: 49.5%;
			width: 100%;
			height: 100%;
			background: rgba(255, 255, 255, 0.3);
			border: 1px solid rgba(255, 255, 255, 1);
			border-radius: 16px 16px 8px 8px;
			&-head {
				height: 88px;
				.file {
					padding-left: 14px;
					padding-right: 8px;
					height: 100%;
					display: flex;
					align-items: center;
					border-radius: 16px;
					border: 1px solid #e1e4eb;

					&-img {
						margin-right: 16px;
						width: 64px;
						height: 64px;
					}
					&-name {
						height: 20px;
						font-family: MiSans, MiSans;
						font-size: 16px;
						color: #383d47;
						line-height: 20px;
					}
					&-size {
						display: flex;
						align-items: center;
						margin-top: 6px;
						height: 18px;
						font-family: MiSans, MiSans;
						font-size: 14px;
						color: #828894;
						line-height: 18px;
						.check-icon {
							margin-left: 12px;
							width: 14px;
							height: 14px;
						}
					}
					.close-btn {
						// width: 40px;
						height: 40px;
						display: flex;
						align-items: center;
						justify-content: center;
						color: #0674fe;
						cursor: pointer;
						iconpark-icon {
							margin-left: 4px;
						}
					}
				}
				&-bottom {
					display: flex;
					.start-btn {
						width: 86px;
						height: 86px;
						background: linear-gradient(#194ee7 0%, #51b4ff 100%);
						border-radius: 8px;
						border: 2px solid rgba(42, 122, 253, 0.1);
						font-family: MiSans, MiSans;
						font-weight: 500;
						font-size: 20px;
						color: #ffffff;
						line-height: 24px;
						display: flex;
						align-items: center;
						justify-content: center;
						cursor: pointer;
					}
					.start-btn-toubiao {
						background: linear-gradient(270deg, #8e65ff 0%, #8e65ff 100%);
					}
				}
			}
			&-center {
				display: flex;
				align-items: center;
				width: 100%;
				height: 56px;
				padding: 0 24px;
				background: rgba(28, 80, 253, 0.03) url('/@/assets/stockExchangeSZ/bg-one.png') repeat center center;
				background-size: 60px 113px;
				border-radius: 8px 8px 0px 0px;
				&-star {
					width: 24px;
					height: 24px;
					margin-right: 12px;
				}
				&-txt1 {
					height: 20px;
					font-family: MiSans, MiSans;
					font-weight: 500;
					font-size: 16px;
					color: #434649;
					line-height: 20px;
				}
				&-txt2 {
					height: 20px;
					font-family: MiSans, MiSans;
					font-size: 14px;
					color: #646479;
					line-height: 20px;
					margin-left: auto;
					span {
						color: #1c50fd;
						margin: 0 6px;
					}
				}
			}
			&-progress {
				width: 100%;
				height: 4px;
				background: rgba(42, 112, 245, 0.04);
				transition: width 0.5s ease; /* 平滑过渡效果 */
				&-inner {
					height: 100%;
					background: #2a70f5;
				}
			}
			.left-center-toubiao {
				background: rgba(142, 101, 255, 0.04) url('/src/assets/stockExchangeSZ/bg-one.png') repeat center center;
			}
			&-bottom {
				padding: 0 24px;
				height: calc(100% - 88px - 56px);
				width: 100%;
				background: #fff;
				border-radius: 0 0 8px 8px;
				&-content {
					// padding-top: 20px;
					padding-bottom: 20px;
					width: 100%;
					height: 100%;
					overflow: auto;
					&::-webkit-scrollbar {
						display: none;
					}
					&-item {
						margin-top: 16px;
						display: flex;
						align-items: center;
						gap: 24px;
						padding: 16px;
						border-radius: 4px;
						border: 1px solid #e7e7e7;
						&-info {
							flex: 1;
							.videoPreview {
								font-family: MiSans, MiSans;
								font-size: 14px;
								color: #434649;
								line-height: 20px;
								span {
									margin-left: 12px;
									font-size: 14px;
									color: #1c50fd;
									line-height: 20px;
								}
							}
							.caption {
								margin-top: 10px;
								font-family: MiSans, MiSans;
								font-size: 14px;
								color: #828894;
								line-height: 22px;
							}
						}
						&-video {
							width: 192px;
							height: 108px;
							border-radius: 2px;
							video {
								width: 100%; /* 不超过父容器宽度 */
								height: 100%;
							}
						}
					}
				}
				.table-head {
					display: flex;
					align-items: center;
					padding: 0 24px;
					width: 100%;
					height: 48px;
					line-height: 48px;
					border-bottom: 1px solid rgba(0, 0, 0, 0.12);
					.title {
						width: 200px;
					}
					.value {
						flex: 1;
					}
					.title,
					.value {
						font-family: MiSans, MiSans;
						font-weight: 600;
						font-size: 14px;
						color: #828894;
					}
				}
				.table-content {
					&-row {
						display: flex;
						align-items: center;
						margin: 0 24px;
						height: 48px;
						line-height: 48px;
						border-bottom: 1px solid rgba(0, 0, 0, 0.12);
						.title {
							width: 200px;
							font-family: MiSans, MiSans;
							font-size: 16px;
							color: #383d47;
						}
						.value {
							flex: 1;
							font-family: MiSans, MiSans;
							font-size: 16px;
							color: #828894;
						}
					}
				}
				.answerOutput {
					padding: 16px;
					font-family: MiSans, MiSans;
					font-size: 16px;
					color: #383d47;
					line-height: 28px;
					height: 100%;
					overflow: auto;
					&::-webkit-scrollbar {
						display: none;
					}
				}
			}
		}
		.before-upload {
			width: 100%;
			height: 100%;
			padding: 0 228px;
			display: flex;
			flex-direction: column;
			align-items: center;
			background: rgba(255, 255, 255, 0.6);
			border-radius: 16px;
			border-image: linear-gradient(180deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0.2)) 1 1;
			backdrop-filter: blur(3px);
			.img-1 {
				height: 144px;
				margin-top: 88px;
			}
			.tip-text {
				margin: 12px 0 36px;
				height: 27px;
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 20px;
				color: #101010;
				line-height: 27px;
			}
			.start-btn {
				margin-top: 36px;
				width: 190px;
				height: 40px;
				line-height: 40px;
				text-align: center;
				background: linear-gradient(90deg, #194ee7 0%, #51b4ff 100%);
				border-radius: 8px;
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 16px;
				color: #ffffff;
				cursor: pointer;
			}
			.upload {
				width: 100%;
				height: 120px;
				margin-right: 12px;
				&-icon {
					margin-right: 18px;
					width: 37px;
					height: 33px;
				}
				.file {
					padding-left: 14px;
					padding-right: 8px;
					height: 100%;
					display: flex;
					align-items: center;
					background: rgba(255, 255, 255, 0.8);
					border-radius: 8px;
					border: 1px solid #e1e4eb;

					&-img {
						margin-right: 16px;
						width: 64px;
						height: 64px;
					}
					&-name {
						height: 20px;
						font-family: MiSans, MiSans;
						font-size: 16px;
						color: #383d47;
						line-height: 20px;
					}
					&-size {
						display: flex;
						align-items: center;
						margin-top: 6px;
						height: 18px;
						font-family: MiSans, MiSans;
						font-size: 14px;
						color: #828894;
						line-height: 18px;
						.check-icon {
							margin-left: 12px;
							width: 14px;
							height: 14px;
						}
					}
					.close-btn {
						width: 40px;
						height: 40px;
						display: flex;
						align-items: center;
						justify-content: center;
						img {
							width: 16px;
							height: 16px;
							cursor: pointer;
						}
					}
				}
				.no-file {
					width: 100%;
					height: 100%;
					display: flex;
					align-items: center;
					justify-content: center;
					background: #f9fafc;
					border-radius: 8px;
					border: 1px dashed rgba(0, 0, 0, 0.12);
					.upload-demo {
						height: 100%;
						display: flex;
						align-items: center;
					}
					.txt1 {
						height: 20px;
						font-family: MiSans, MiSans;
						font-weight: 400;
						font-size: 16px;
						color: #383d47;
						line-height: 20px;
					}
					.select-btn {
						color: #1c50fd;
						cursor: pointer;
					}
					.txt2 {
						margin-top: 4px;
						height: 20px;
						font-family: MiSans, MiSans;
						font-weight: 400;
						font-size: 14px;
						color: #b4bccc;
						line-height: 20px;
					}
				}
			}
		}
	}
	@keyframes moveUpDown {
		0% {
			transform: translateY(0);
		}
		50% {
			transform: translateY(-12px);
		}
		100% {
			transform: translateY(0);
		}
	}

	.left-center-star-move {
		animation: moveUpDown 1s infinite;
	}
}
</style>