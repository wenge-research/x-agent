<template>
	<div class="stock-exchange">
		<div class="stock-exchange-head">
			<img class="logo" src="/@/assets/stockExchangeSZ/logo.png" alt="" />
			<div class="user-info">
				<div class="name">欢迎您，admin</div>
				<img class="avatar" src="/@/assets/stockExchangeSZ/user.png" alt="" />
			</div>
		</div>
		<div class="stock-exchange-content">
			<div class="left">
				<div class="left-head">
					<div class="left-head-top">
						<img class="zhaobiao" src="/@/assets/stockExchangeSZ/zhaobiao.png" alt="" />
						<div class="txt" :class="[biddingShowPopover ? 'clickBg' : '']">
							<div @click="popoverClick('bidding')" style="display: flex">
								<img class="platte" src="/@/assets/stockExchangeSZ/platte.svg" alt="" />
								自定义Prompt
							</div>
							<transition name="el-zoom-in-top">
								<div v-if="biddingShowPopover" class="popover">
									<img class="sanjiaoxing" src="/@/assets/stockExchangeSZ/sanjiaoxing.svg" alt="" />
									<el-input type="textarea" v-model="biddingData.promptContent" maxlength="1000" show-word-limit :rows="19" />
									<div class="popover-button">
										<div class="btn ok-btn" @click="updateSmartPromptConfig('bidding')">确定</div>
										<div class="btn close-btn" @click.stop="closePopover('bidding')">关闭</div>
										<div class="btn reset-btn" @click="resetHandler('bidding')">
											<img class="refresh" src="/@/assets/stockExchangeSZ/refresh.svg" alt="" /> 默认值
										</div>
									</div>
								</div>
							</transition>
						</div>
					</div>
					<div class="left-head-bottom">
						<div class="upload">
							<div v-if="!fileData.id" v-loading="uploadLoading" class="no-file">
								<el-upload
									class="upload-demo"
									ref="upload"
									action="#"
									:show-file-list="false"
									:before-upload="beforeUpload"
									:http-request="uploadHandler"
									accept=".doc, .docx, .pdf"
								>
									<img class="upload-icon" src="/@/assets/stockExchangeSZ/upload.svg" alt="" />
									<div>
										<div class="txt1">拖拽文件到此处或<span class="select-btn">选择文件</span></div>
										<div class="txt2">支持文档格式：PDF、Word</div>
									</div>
								</el-upload>
							</div>
							<div v-else class="file">
								<img class="file-img" src="/@/assets/stockExchangeSZ/file-icon.png" alt="" />
								<div style="flex: 1">
									<div class="file-name" @click="openFileHandler">{{ fileData.fileName }}</div>
									<div class="file-size">
										{{ opreateSize(fileData.fileSize) }} mb <img class="check-icon" src="/@/assets/stockExchangeSZ/check-icon.svg" alt="" />
									</div>
								</div>
								<div class="close-btn" @click="deleteFileHandler('bidding')"><img src="/@/assets/stockExchangeSZ/close-icon.svg" alt="" /></div>
							</div>
						</div>
						<div class="start-btn" @click="startExtracting('bidding')">开始<br />抽取</div>
					</div>
				</div>
				<div class="left-center">
					<img class="left-center-star" src="/@/assets/stockExchangeSZ/left-star.png" alt="" />
					<div class="left-center-txt">正在抽取招标文件</div>
					<!-- <img v-show="chouLoading" class="left-center-star" src="/@/assets/stockExchangeSZ/left-star.png" alt="" />
					<div v-show="chouLoading" class="left-center-txt">正在抽取招标文件</div> -->
					<div v-if="biddingBrCount" class="left-center-txt2">
						已找到<span>{{ biddingBrCount }}</span
						>项对投标文件要求
					</div>
				</div>
				<div class="left-bottom">
					<!-- <div class="table-head">
						<div class="title">抽取项</div>
						<div class="value">抽取内容</div>
					</div>
					<ul class="table-content">
						<li class="table-content-row">
							<div class="title">抽取项</div>
							<div class="value">抽取内容</div>
						</li>
					</ul> -->
					<div class="answerOutput" v-html="biddingText"></div>
				</div>
			</div>
			<div class="left">
				<div class="left-head">
					<div class="left-head-top">
						<img class="zhaobiao" src="/@/assets/stockExchangeSZ/toubiao.png" alt="" />
						<div class="txt" :class="[bidShowPopover ? 'clickBg' : '']">
							<div @click="popoverClick('bid')" style="display: flex">
								<img class="platte" src="/@/assets/stockExchangeSZ/platte.svg" alt="" />
								自定义Prompt
							</div>
							<transition name="el-zoom-in-top">
								<div v-if="bidShowPopover" class="popover">
									<img class="sanjiaoxing" src="/@/assets/stockExchangeSZ/sanjiaoxing.svg" alt="" />
									<el-input type="textarea" v-model="bidData.promptContent" maxlength="1000" show-word-limit :rows="19" />
									<div class="popover-button">
										<div class="btn ok-btn" @click="updateSmartPromptConfig('bid')">确定</div>
										<div class="btn close-btn" @click.stop="closePopover('bid')">关闭</div>
										<div class="btn reset-btn" @click="resetHandler('bid')">
											<img class="refresh" src="/@/assets/stockExchangeSZ/refresh.svg" alt="" /> 默认值
										</div>
									</div>
								</div>
							</transition>
						</div>
					</div>
					<div class="left-head-bottom">
						<div class="upload">
							<div v-if="!fileDataBid.id" v-loading="uploadLoadingBid" class="no-file">
								<el-upload
									class="upload-demo"
									ref="upload"
									action="#"
									:show-file-list="false"
									:before-upload="beforeUploadBid"
									:http-request="uploadHandlerBid"
									accept=".doc, .docx, .pdf"
								>
									<img class="upload-icon" src="/@/assets/stockExchangeSZ/upload.svg" alt="" />
									<div>
										<div class="txt1">拖拽文件到此处或<span class="select-btn">选择文件</span></div>
										<div class="txt2">支持文档格式：PDF、Word</div>
									</div>
								</el-upload>
							</div>
							<div v-else class="file">
								<img class="file-img" src="/@/assets/stockExchangeSZ/file-icon.png" alt="" />
								<div style="flex: 1">
									<div class="file-name" @click="openFileHandler">{{ fileDataBid.fileName }}</div>
									<div class="file-size">
										{{ opreateSize(fileDataBid.fileSize) }} mb <img class="check-icon" src="/@/assets/stockExchangeSZ/check-icon.svg" alt="" />
									</div>
								</div>
								<div class="close-btn" @click="deleteFileHandler('bid')"><img src="/@/assets/stockExchangeSZ/close-icon.svg" alt="" /></div>
							</div>
						</div>
						<div class="start-btn start-btn-toubiao" @click="startExtracting('bid')">开始<br />抽取</div>
					</div>
				</div>
				<div class="left-center left-center-toubiao">
					<img class="left-center-star" src="/@/assets/stockExchangeSZ/right-star.png" alt="" />
					<div class="left-center-txt">正在抽取投标文件</div>
					<!-- <img v-if="chouLoadingBid" class="left-center-star" src="/@/assets/stockExchangeSZ/right-star.png" alt="" />
					<div v-if="chouLoadingBid" class="left-center-txt">正在抽取投标文件</div> -->
					<div v-if="bidBrCount" class="left-center-txt2">
						已抽取<span>{{ bidBrCount }}</span
						>文件要求
					</div>
				</div>
				<div class="left-bottom">
					<!-- <div class="table-head">
						<div class="title">抽取项</div>
						<div class="value">抽取内容</div>
					</div>
					<ul class="table-content">
						<li class="table-content-row">
							<div class="title">抽取项</div>
							<div class="value">抽取内容</div>
						</li>
					</ul> -->
					<div class="answerOutput" v-html="bidText"></div>
				</div>
			</div>
		</div>
		<div class="stock-exchange-footer">
			<div v-if="chouLoading" class="left">
				<img class="loading" :class="[chouLoading ? 'chouquAnimation' : 'stopAnimation']" src="/@/assets/stockExchangeSZ/loading.png" alt="" />
				等待文件抽取完成
			</div>
			<div class="right">
				<div class="txt">
					<div @click="popoverClick('')" style="display: flex">
						<img class="platte" src="/@/assets/stockExchangeSZ/platte.svg" alt="" />
						自定义Prompt
					</div>
					<transition name="el-zoom-in-top">
						<div v-if="showPopver" class="popover">
							<img class="sanjiaoxing" src="/@/assets/stockExchangeSZ/sanjiaoxing.svg" alt="" />
							<el-select v-model="module" placeholder="请选择文件类型" style="margin-bottom: 12px" size="large">
								<el-option label="招标文件" value="bidding" />
								<el-option label="投标文件" value="bid" />
							</el-select>
							<el-input type="textarea" v-model="promptValue" maxlength="1000" show-word-limit :rows="17" />
							<div class="popover-button">
								<div class="btn ok-btn" @click="addSmartPromptConfig">确定</div>
								<div class="btn close-btn" @click.stop="closePopover('')">关闭</div>
								<div class="btn reset-btn" @click="resetHandler('')">
									<img class="refresh" src="/@/assets/stockExchangeSZ/refresh.svg" alt="" /> 默认值
								</div>
							</div>
						</div>
					</transition>
				</div>
				<div class="button" @click="openDrawer">开始审查</div>
			</div>
		</div>
		<!-- 审查结果 -->
		<transition name="el-fade-in-linear">
			<div v-if="drawerVisible" class="drawer">
				<div class="drawer-content" v-loading="drawerLoading">
					<div class="drawer-content-head">
						<div class="txt">审查结果</div>
						<div class="save" @click="batchSave"><img class="save-icon" src="/@/assets/stockExchangeSZ/save-icon.svg" alt="" />保存结果</div>
						<div class="close" @click="closeDrawer"><img class="close-drawer" src="/@/assets/stockExchangeSZ/close-drawer.svg" alt="" /></div>
					</div>
					<div class="drawer-content-box">
						<div class="left">
							<div class="title">招标文件抽取要素</div>
							<ul>
								<li v-for="(item, index) in examinationList" :key="index" class="left-item">
									<div class="label">投标人资格要求</div>
									<div class="value">{{ index }}</div>
								</li>
							</ul>
						</div>
						<div class="right">
							<div class="title">投标文件抽取要素</div>
							<ul>
								<li v-for="(item, index) in examinationList" :key="index" class="right-item">
									<div class="label">施工资质</div>
									<div class="value">{{ item }}</div>
								</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</transition>
	</div>
</template>

<script>
import axios from 'axios';
import { Message } from 'winbox-ui-next';
import { formatDate } from '/@/utils/formatTime';
import useEventSource from '/@/hooks/useEventSource';
import {
	apiDocumentDialogue,
	apiGetSmartPromptConfigList,
	apiAddSmartPromptConfig,
	apiUpdateSmartPromptConfig,
	apiMatching,
	apiBatchSave,
} from '/@/api/stockExchangeSZ';
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
		};
	},
	mounted() {
		// 招标
		this.getSmartPromptConfigList('bidding');
	},
	methods: {
		async popoverClick(module) {
			console.log('module', module);
			if (module) {
				if (!this[`${module}ShowPopover`]) {
					await this.getSmartPromptConfigList(module);
				}
				this[`${module}ShowPopover`] = !this[`${module}ShowPopover`];
				if (this[`${module}ShowPopover`]) {
					this.showPopver = false;
				}
			} else {
				this.showPopver = !this.showPopver;
				if (this.showPopver) {
					this.biddingShowPopover = false;
					this.bidShowPopover = false;
				}
				console.log('showPopver', this.showPopver);
			}
		},
		closePopover(module) {
			if (module) {
				this[`${module}ShowPopover`] = false;
			} else {
				this.showPopver = false;
				this.module = '';
				this.promptValue = '';
			}
		},
		// 字节转换kb
		opreateSize(num) {
			return num ? (num / 1024 / 1024).toFixed(2) : 0;
		},
		// 打开上传文件
		openFileHandler() {
			window.open(this.fileData.urlPath, '_blank');
		},
		// 删除上传文件
		deleteFileHandler(module) {
			if (module == 'bidding') {
				this.fileData = {};
				this.biddingText = '';
				this.biddingBrCount = 0;
			} else {
				this.fileDataBid = {};
				this.bidText = '';
				this.bidBrCount = 0;
			}
		},
		// 上传校验
		beforeUpload(file) {
			console.log('file', file.type);
			if (
				file.type != 'application/msword' &&
				file.type != 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' &&
				file.type != 'application/pdf'
			) {
				Message.warning('文件格式错误');
				return false;
			}
		},
		// 上传
		async uploadHandler(param) {
			const formData = new FormData();
			formData.append('file', param.file);
			formData.append('rename', true);
			formData.append('filePath', 'agent_source');
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
		// 投标
		beforeUploadBid(file) {
			console.log('file', file.type);
			if (
				file.type != 'application/msword' &&
				file.type != 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' &&
				file.type != 'application/pdf'
			) {
				Message.warning('文件格式错误');
				return false;
			}
		},
		// 投标上传
		async uploadHandlerBid(param) {
			const formData = new FormData();
			formData.append('file', param.file);
			formData.append('rename', true);
			formData.append('filePath', 'agent_source');
			try {
				this.uploadLoadingBid = true;
				const response = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/wos/file/upload`, formData, {
					headers: {
						'Content-Type': 'audio/wave;multipart/form-data',
					},
				});
				if (response.status === 200) {
					this.fileDataBid = response?.data?.data?.length ? response.data.data[0] : {};
				}
				this.uploadLoadingBid = false;
			} catch (error) {
				this.uploadLoadingBid = false;
				console.error('Error :', error);
			}
		},
		// 抽取
		startExtracting(module) {
			if (module == 'bidding' && !this.fileData.urlPath) {
				return Message.warning('请上传招标文件');
			} else if (module == 'bid' && !this.fileDataBid.urlPath) {
				return Message.warning('请上传投标文件');
			}
			if (module == 'bidding') {
				this.chouLoading = true;
			} else {
				this.chouLoadingBid = true;
			}
			const clientId = Math.round(Math.random() * 1000000000000000000).toString(36);
			const abortController = new AbortController();
			let url = import.meta.env.VITE_API_URL + import.meta.env.VITE_STREEM_PATH + import.meta.env.VITE_BASE_API_URL + '/dialogue/documentDialogue';
			useEventSource(
				url,
				{
					fileUrl: module == 'bidding' ? this.fileData.urlPath : this.fileDataBid.urlPath, // 文件地址
					fileType: module == 'bidding' ? this.fileData.fileType : this.fileDataBid.fileType, // 文件类型
					customizePrompt: this[`${module}CustomizePrompt`], // 自定义promty
					module, // 模块，bidding-招标，bid-投标，gx_matter-关芯智巡
					question: '',
					clientId,
					appId: localStorage.getItem(`${this.$route.params.appId}appId`),
				},
				abortController,
				(list) => {
					this[`${module}Text`] = JSON.parse(list) ? JSON.parse(list)?.answer : '';
					this[`${module}BrCount`] = (this[`${module}Text`].match(/<br\/>/g) || []).length + 1;
				}
			);
			this.$nextTick(() => {
				if (module == 'bidding') {
					this.chouLoading = false;
				} else {
					this.chouLoadingBid = false;
				}
			});
		},
		// 获取prompt
		async getSmartPromptConfigList(module) {
			const params = {
				module, // 模块，bidding-招标，bid-投标，gx_matter-关芯智巡
				applicationId: localStorage.getItem(`${this.$route.params.appId}appId`),
			};
			const res = await apiGetSmartPromptConfigList(params);
			console.log('res', res);
			if (res.code == '000000') {
				this[`${module}Data`] = res.data?.length ? res.data[0] : {};
				this[`${module}CustomizePrompt`] = this[`${module}Data`]?.promptContent;
			}
		},
		// 新增prompt
		async addSmartPromptConfig() {
			if (!this.module) return Message.warning('文件类型不能为空');
			if (!this.promptValue) return Message.warning('内容不能为空');
			const params = {
				promptContent: this.promptValue,
				module: this.module,
				applicationId: localStorage.getItem(`${this.$route.params.appId}appId`),
				promptId: '',
				id: '',
				remark: '',
			};
			const res = await apiAddSmartPromptConfig(params);
			console.log('res', res);
			if (res.code == '000000') {
				Message.success(res.msg);
				this.closePopover('');
			}
		},
		// 修改prompt
		async updateSmartPromptConfig(module) {
			const params = {
				...this[`${module}Data`],
			};
			const res = await apiUpdateSmartPromptConfig(params);
			console.log('res', res);
			if (res.code == '000000') {
				this[`${module}Data`] = res.data?.length ? res.data[0] : {};
				this[`${module}ShowPopover`] = false;
				Message.success(res.msg);
			}
		},
		// 重置prompt
		resetHandler(module) {
			if (module) {
				this[`${module}Data`].promptContent = this[`${module}CustomizePrompt`];
			} else {
				this.promptValue = '';
			}
		},
		// 打开审查结果
		openDrawer() {
			this.drawerVisible = true;
			this.matching();
		},
		// 关闭审查结果
		closeDrawer() {
			this.examinationList = [];
			this.drawerVisible = false;
		},
		// 开始审查 - 匹配招投标文件
		async matching(module) {
			this.drawerLoading = true;
			const params = {
				appId: localStorage.getItem(`${this.$route.params.appId}appId`),
				bidding: this.biddingText,
				bid: this.bidText,
			};
			const res = await apiMatching(params);
			console.log('res', res);
			if (res.code == '000000') {
				this.examinationList = res.data || [];
			}
			this.drawerLoading = false;
		},
		async batchSave() {
			try {
				let terms = [];
				for (let key in this.examinationList) {
					let obj = {};
					obj.term = key;
					obj.result = this.examinationList[key];
					terms.push(obj);
				}
				const createTime = formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS');
				const params = {
					terms,
					applicationId: localStorage.getItem(`${this.$route.params.appId}appId`),
					createTime,
					biddingFileUrl: this.fileData.urlPath,
					bidFileUrl: this.fileDataBid.urlPath,
				};

				const res = await apiBatchSave(params);
				if (res.code == '000000') {
					console.log('res', res);
					Message.success(res.msg);
					this.closeDrawer();
				}
			} catch (error) {}
		},
	},
};
</script>

<style lang="scss" scoped>
.stock-exchange {
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
			width: 350px;
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
		height: calc(100vh - 104px - 90px);
		width: 100%;
		padding: 24px 114px;
		display: flex;
		justify-content: space-between;
		.left {
			width: 49.5%;
			height: 100%;
			background: rgba(255, 255, 255, 0.3);
			border: 1px solid rgba(255, 255, 255, 1);
			border-radius: 16px 16px 8px 8px;
			&-head {
				height: 181px;
				padding: 0 24px;
				&-top {
					height: 66px;
					display: flex;
					align-items: center;
					.zhaobiao {
						width: 96px;
						height: 26px;
						margin-right: 36px;
					}
					.txt {
						position: relative;
						display: flex;
						align-items: center;
						height: 40px;
						font-family: MiSans, MiSans;
						font-size: 16px;
						color: #1c50fd;
						line-height: 40px;
						cursor: pointer;
						padding: 0 16px;
						border-radius: 8px;
						&:hover {
							background: rgba(28, 80, 253, 0.1);
						}
						.platte {
							margin-right: 4px;
						}
						.popover {
							position: absolute;
							height: 520px;
							width: 676px;
							padding: 26px;
							background: #fff;
							top: 44px;
							left: -48px;
							box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, 0.1);
							border: 1px solid #e1e4eb;
							border-radius: 16px;
							.sanjiaoxing {
								position: absolute;
								top: -10px;
								left: 96px;
							}
							&-button {
								margin-top: 18px;
								display: flex;
								align-items: center;
								.btn {
									width: 88px;
									height: 40px;
									line-height: 40px;
									text-align: center;
									border-radius: 8px;
								}
								.ok-btn {
									background: #1c50fd;
									border: 1px solid #1c50fd;
									font-family: MiSans, MiSans;
									font-size: 16px;
									color: #ffffff;
								}
								.close-btn {
									margin-left: 12px;
									border: 1px solid #1c50fd;
									font-family: MiSans, MiSans;
									font-size: 16px;
									color: #1c50fd;
								}
								.reset-btn {
									margin-left: auto;
									display: flex;
									align-items: center;
									.refresh {
										width: 15px;
										height: 15px;
										margin-right: 4px;
									}
								}
							}
						}
					}
					.clickBg {
						background: rgba(28, 80, 253, 0.1);
					}
				}
				&-bottom {
					display: flex;
					.upload {
						flex: 1;
						margin-right: 12px;
						.no-file {
							width: 100%;
							height: 100%;
							display: flex;
							align-items: center;
							justify-content: center;
							background: #f9fafc;
							border-radius: 8px;
							border: 1px solid rgba(0, 0, 0, 0.12);
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
						&-icon {
							margin-right: 18px;
							width: 37px;
							height: 33px;
						}
					}
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
			.left-center-toubiao {
				background: rgba(142, 101, 255, 0.04) url('/src/assets/stockExchangeSZ/bg-one.png') repeat center center;
			}
			&-bottom {
				height: calc(100% - 181px - 56px);
				width: 100%;
				background: #fff;
				border-radius: 0 0 8px 8px;
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
					height: 98%;
					overflow: auto;
					&::-webkit-scrollbar {
						display: none;
					}
				}
			}
		}
		.right {
			width: 49.5%;
			height: 100%;
			background: rgba(255, 255, 255, 0.5);
			border-radius: 16px 16px 8px 8px;
			border: 1px solid rgba(255, 255, 255, 1);
		}
	}
	&-footer {
		width: 100%;
		height: 90px;
		padding: 0 112px;
		background: #fff;
		display: flex;
		align-items: center;
		justify-content: space-between;
		.left {
			display: flex;
			align-items: center;
			justify-content: center;
			font-family: MiSans, MiSans;
			font-weight: 500;
			font-size: 20px;
			color: #383d47;

			.loading {
				margin-right: 8px;
				width: 32px;
				height: 32px;
			}
			.chouquAnimation {
				animation: rotate 2s linear infinite;
			}
		}
		@keyframes rotate {
			from {
				transform: rotate(0deg);
			}
			to {
				transform: rotate(360deg);
			}
		}
		.right {
			display: flex;
			align-items: center;
			margin-left: auto;
			.txt {
				position: relative;
				display: flex;
				align-items: center;
				height: 40px;
				font-family: MiSans, MiSans;
				font-size: 16px;
				color: #1c50fd;
				line-height: 40px;
				cursor: pointer;
				padding: 0 16px;
				border-radius: 8px;
				.platte {
					margin-right: 4px;
				}
				.popover {
					position: absolute;
					height: 520px;
					width: 676px;
					padding: 26px;
					background: #fff;
					bottom: 44px;
					right: -48px;
					box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, 0.1);
					border: 1px solid #e1e4eb;
					border-radius: 16px;
					.sanjiaoxing {
						position: absolute;
						bottom: -10px;
						right: 96px;
						transform: rotate(180deg);
					}
					&-button {
						margin-top: 18px;
						display: flex;
						align-items: center;
						.btn {
							width: 88px;
							height: 40px;
							line-height: 40px;
							text-align: center;
							border-radius: 8px;
						}
						.ok-btn {
							background: #1c50fd;
							border: 1px solid #1c50fd;
							font-family: MiSans, MiSans;
							font-size: 16px;
							color: #ffffff;
						}
						.close-btn {
							margin-left: 12px;
							border: 1px solid #1c50fd;
							font-family: MiSans, MiSans;
							font-size: 16px;
							color: #1c50fd;
						}
						.reset-btn {
							margin-left: auto;
							display: flex;
							align-items: center;
							.refresh {
								width: 15px;
								height: 15px;
								margin-right: 4px;
							}
						}
					}
				}
			}
			.button {
				width: 261px;
				height: 56px;
				line-height: 56px;
				text-align: center;
				background: linear-gradient(270deg, #194ee7 0%, #51b4ff 100%);
				border-radius: 12px;
				border: 2px solid rgba(42, 122, 253, 0.1);
				cursor: pointer;
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 20px;
				color: #ffffff;
			}
		}
	}
	.drawer {
		position: absolute;
		right: 0;
		top: 0;
		width: 100%;
		height: 100vh;
		background: rgba(0, 0, 0, 0.5);
		backdrop-filter: blur(6px);
		&-content {
			width: 58%;
			height: 100%;
			background: #fff;
			border-radius: 12px 0px 0px 12px;
			margin-left: auto;
			&-head {
				width: 100%;
				height: 80px;
				background: linear-gradient(180deg, rgba(59, 136, 255, 0.1) 0%, rgba(28, 80, 253, 0) 100%);
				border-radius: 12px 0px 0px 12px;
				padding: 0 32px;
				display: flex;
				align-items: center;
				.txt {
					font-family: MiSans, MiSans;
					font-weight: 500;
					font-size: 24px;
					color: #383d47;
					line-height: 26px;
				}
				.save {
					margin-left: 26px;
					width: 118px;
					height: 40px;
					background: #1c50fd;
					border-radius: 8px;
					border: 1px solid #1c50fd;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 16px;
					color: #ffffff;
					line-height: 20px;
					display: flex;
					align-items: center;
					justify-content: center;
					cursor: pointer;
					&-icon {
						width: 14px;
						height: 14px;
						margin-right: 4px;
					}
				}
				.close {
					width: 40px;
					height: 40px;
					display: flex;
					align-items: center;
					justify-content: center;
					margin-left: auto;
					cursor: pointer;
					&-drawer {
						width: 24px;
						height: 24px;
					}
				}
			}
			&-box {
				display: flex;
				justify-content: space-between;
				padding: 0 32px;
				height: 100%;

				.left,
				.right {
					width: 480px;
					height: calc(100% - 80px);
					border-radius: 16px;
					padding: 30px;
					.title {
						height: 32px;
						font-family: MiSans, MiSans;
						font-weight: 500;
						font-size: 24px;
						color: #383d47;
						line-height: 32px;
						margin-bottom: 28px;
					}
					ul {
						height: calc(100% - 32px);
						overflow: auto;
						&::-webkit-scrollbar {
							display: none;
						}
					}
					&-item {
						padding: 18px 20px;
						border-radius: 8px;
						margin-bottom: 12px;
						.label {
							height: 24px;
							font-family: MiSans, MiSans;
							font-weight: 500;
							font-size: 16px;
							color: #828894;
							line-height: 24px;
						}
						.value {
							font-family: MiSans, MiSans;
							font-weight: 500;
							font-size: 18px;
							color: #383d47;
							line-height: 26px;
							margin-top: 8px;
						}
					}
				}
				.left {
					background: rgba(28, 80, 253, 0.05);
					&-item {
						border: 1px solid #1c50fd;
						background: rgba(28, 80, 253, 0.2);
					}
				}
				.right {
					background: rgba(142, 101, 255, 0.05);
					&-item {
						border: 1px solid #8e65ff;
						background: rgba(142, 101, 255, 0.2);
					}
				}
			}
		}
	}
}
</style>