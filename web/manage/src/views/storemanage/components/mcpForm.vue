<template style="background-color: #f6f6f6;">
	<div class="page-wrap" v-loading="loading">
		<div v-if="type!='details'">
			<div class="page-header">
				<el-row class="header-wrap" type="flex" justify="space-between" align="middle">
					<el-col style="font-size: 18px;display: flex;align-items: center;font-weight: 550;" :span="6">
						<img style="cursor: pointer;width: 18px;height: 18px;margin-right: 10px;"
							src="@/assets/images/arrow-go-back-fill.svg" @click="closePage" />
						<span v-if="type=='add'">{{ $t("newSlice") }} {{ $t("mcp") }}</span>
						<span v-else>修改 {{ $t("mcp") }}</span>
					</el-col>
					<el-col>
						<el-row type="flex" justify="end" align="middle">

							<el-button @click="addSaveDraft('1')"
								style="width: 154p; border: 1px solid #1747E5; color:#1747E5; border-radius: 2px;margin: 0 10px">{{
				  $t("saveDraft")
				}}</el-button>
							<el-button type="primary" @click="addSaveDraft('2')">{{
				  $t("publish")
				}}</el-button>
						</el-row>
						<!-- <el-row type="flex" justify="end" align="middle" v-else>
				
				<el-button type="primary" @click="addEdit">{{
				  $t("edit")
				}}</el-button>
				 
			  </el-row> -->
					</el-col>
				</el-row>
			</div>
			<div class="form-box">
				<div class="formOuter">
					<div class="uploadImg">
						<div>
							<el-form :model="appForm" :rules="rules" ref="ruleForm" class="demo-ruleForm">
								<el-form-item label="" prop="mcpName">
									<div class="mcp-title">服务名称<span style="color: red;margin-left: 5px;">*</span></div>
									<el-input v-model="appForm.mcpName" show-word-limit maxlength="50"
										style="width: 100%" placeholder="请输入MCP服务名称" />
								</el-form-item>
								<el-form-item label="">
									<div class="mcp-title">图标</div>
									<div class="uploadOuter">
										<el-upload :action="actionUrl" :data="{ filePath: 'agent_source' }"
											:class="{ hideBox: uploadBtnLogo }" :file-list="fileListLogo"
											:show-file-list="false" :limit="1" class="logoAppUpload"
											list-type="picture-card" :on-remove="handleLogoRemove"
											:on-success="handleLogoSuccess">

											<div style="position: relative; height: 100%" @mouseenter="imgMouseenter"
												@mouseleave="imgMouseleave" v-if="appForm.icon">
												<img v-if="appForm.icon" :src="appForm.icon" style="
							  width: 80px;
							  height: 80px;
							  border-radius: 4px;
							  background: #dcdfe6;
							" />
												<!--  -->
												<div v-show="showDeleteIcon" class="opts-btn">
													<iconpark-icon name="edit-line" size="18"
														color="#FFFFFF"></iconpark-icon>
													<iconpark-icon name="delete-bin-4-line" class="delete" size="18"
														color="#FFFFFF" @click.stop="deleteLogo"></iconpark-icon>

												</div>

											</div>
											<div v-else
												style="height: 100%; display: flex; align-items: center; justify-content: center; background: #f2f4f7">
												<iconpark-icon name="add-line" size="24"
													color="#8c939d"></iconpark-icon>
											</div>
										</el-upload>
										<!-- <el-button class="ai-btn" type="primary" :loading="imgLoading" @click="getImageUrl">
						<img src="@/assets/images/ai-btn.svg" alt="">
						AI生成
					  </el-button> -->
									</div>
								</el-form-item>
								<el-form-item label="" prop="mcpType">
									<div class="mcp-title">服务类型<span style="color: red;margin-left: 5px;">*</span></div>
									<div class="application-type-box">
										<el-select class="app-type-select" placeholder="请选择服务类型" style="width: 100%"
											v-model="appForm.mcpType">
											<el-option v-for="item in applicationTypeLists" :key="item.value"
												:value="item.value" :label="item.label"></el-option>
										</el-select>
									</div>
								</el-form-item>
								<el-form-item label="" prop="description">
									<div class="mcp-title">描述</div>
									<el-input class="inputTextarea" type="textarea" :rows="5"
										v-model="appForm.description" show-word-limit maxlength="200"
										style="width: 100%" placeholder="请输入描述" />
								</el-form-item>

								<div class="mcp-title">安装方式</div>
								<div class="flex sse-item">
									<div class="sse" v-for="(item,index) in installWayList" :key="index">
										<img style="cursor: pointer;width: 28px;height: 28px;margin:0 10px;"
											:src="item.img">
										<div class="custom-radio">
											<el-radio v-model="appForm.installWay" :label="item.label"
												style="margin-left: 20px;">{{item.label}}</el-radio>
										</div>
									</div>
								</div>
								<div style="margin-bottom: 20px;" v-if="appForm.installWay=='npx'||appForm.installWay=='uvx'">
									<div class="mcp-title" style="margin-bottom: 20px;">
										MCP服务配置<span>使用JSON表示，请确保格式正确</span></div>
									<codemirror v-model="appForm.npxUvxService" :options="options"
										class="codemirror-editor"></codemirror>
								</div>
								<el-form-item label="" prop="url" v-if="appForm.installWay=='sse'">
									<div class="mcp-title flex" style="justify-content: space-between;">
										<div>服务地址<span style="color: red;margin-left: 5px;">*</span></div>
										<div><span style="font-weight: normal;">{{isApiFlag}}</span>
											<el-switch
											  v-model="isApiFlag"
											  active-value="需鉴权"
											  inactive-value="无需鉴权"
											  active-color="#1747E5"
											  inactive-color="#EAECF0">
											</el-switch>
										</div>
									</div>
									<div style="display: flex;">
										<el-input v-model="appForm.url" show-word-limit maxlength="100"
											style="width: 100%" placeholder="请输入" />
										
									</div>
								</el-form-item>
								<el-form-item label="" prop="url" v-if="isApiFlag=='需鉴权'">
									<div class="mcp-title">API Key<span style="color: red;margin-left: 5px;">*</span></div>
									<div style="display: flex;">
										<el-input v-model="appForm.apiKey" show-word-limit maxlength="100"
											style="width: 100%" placeholder="请输入" />
										
									</div>
								</el-form-item>
								<el-button type="primary" @click="addCheck">查询</el-button>
							</el-form>
							<div class="checkList" v-if="mcpFunctionList.length>0">
								<div class="check-title">工具{{mcpFunctionList.length}}</div>
								<div class="check-list" :class="mcpFunctionList.length>0?'check-list-auto':''">
									<div class="list-item" v-for="(item,index) in mcpFunctionList" :key="index">
										<div class="list-item-top">
											<img src="@/assets/images/tools-line.svg"
												style="cursor: pointer;width: 24px;height: 24px;" alt="" />


											<!-- <iconpark-icon class="icon-arrow"  color="#494E57" size="30" name="tools-line"></iconpark-icon> -->
											<div class="text">{{ item.functionName }}</div>
										</div>
										<div class="list-item-label" :title="item.description">
											{{ item.description }}

										</div>
									</div>
									
								</div>
							</div>
							<div v-if="mcpFunctionList.length==0&&checkFlag" class="no-data" style="margin-top: 100px;">
								<img src="@/assets/images/no-data.png" alt="" />
								<div class="txt1">暂无工具</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div v-else>
			<div class="page-header">
				<el-row class="header-wrap" type="flex" justify="space-between" align="middle">
					<el-col style="font-size: 18px;display: flex;align-items: center;" :span="6">
						<img style="cursor: pointer;width: 18px;height: 18px;margin-right: 10px;"
							src="@/assets/images/arrow-go-back-fill.svg" @click="closePage" />
						服务详情
					</el-col>
					<el-col>
						<el-row type="flex" justify="end" align="middle">
							<el-button @click="type='edit'">{{
				  $t("edit")
				}}</el-button>

						</el-row>
					</el-col>
				</el-row>
			</div>
			<div class="form-box">
				<div class="list-item-top">
					<img :src="appForm.icon" alt="" />
					<div class="text">{{ appForm.mcpName }}</div>
					<div class="label-btn">{{ appForm.mcpType }}</div>
				</div>
				<div class="list-item-label" :title="appForm.description">
					{{ appForm.description }}

				</div>
				<div class="checkList">
					<div class="check-title">工具{{mcpFunctionList.length}}</div>
					<div class="check-list check-list-auto">
						<div class="list-item" v-for="(item,index) in mcpFunctionList" :key="index">
							<div class="list-item-top">
								<img src="@/assets/images/tools-line.svg"
									style="cursor: pointer;width: 24px;height: 24px;" alt="" />


								<!-- <iconpark-icon class="icon-arrow"  color="#494E57" size="30" name="tools-line"></iconpark-icon> -->
								<div class="text">{{ item.functionName }}</div>
							</div>
							<div class="list-item-label" :title="item.description">
								{{ item.description }}

							</div>
						</div>
						<div v-if="mcpFunctionList.length==0" class="no-data">
							<img src="@/assets/images/no-data.png" alt="" />
							<div class="txt1">暂无工具</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>
<script>
	// 引入 uuid 模块
	const {
		v4: uuidv4
	} = require('uuid');
	import {
		getAiImage
	} from '@/api/app';
	import {
		debounce
	} from 'lodash';
	import {
		addMcpServer,
		updateMcpServer,
		checkMcp,
		getDetail
	} from "@/api/mcp";
	// import VueJsonEditor from 'vue-json-editor'
	export default {
		data() {
			return {
				options: {
					line: true,
					theme: "rubyblue", // 主题
					tabSize: 4, // 制表符的宽度
					indentUnit: 2, // 一个块应该缩进多少个空格（无论这在编辑语言中意味着什么）。默认值为 2。
					firstLineNumber: 1, // 从哪个数字开始计算行数。默认值为 1。
					readOnly: false, // 只读
					autorefresh: true,
					smartIndent: true, // 上下文缩进
					lineNumbers: true, // 是否显示行号
					styleActiveLine: true, // 高亮选中行
					viewportMargin: Infinity, //处理高度自适应时搭配使用
					showCursorWhenSelecting: true, // 当选择处于活动状态时是否应绘制游标
					mode: "javascript",
				},
				appForm: {
					// icon: require('@/assets/images/appManagement/mcp.png'),
					icon:this.getRandomHeadImgDefaultBgColor(),
					mcpName: "",
					installWay: 'sse',
					mcpType: '',
					description: '',
					url: '',
					npxUvxService: '',
					apiKey:''
				},
				outputJsonData: {
					"example": "Hello, Vue-Json-Editor!"
				},
				mcpFunctionList: [],
				uploadBtnLogo: false,
				checkFlag: false,
				isApiFlag:false,
				fileListLogo: [],
				actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
				rules: {
					mcpName: [{
						required: true,
						message: "请输入服务名称",
						trigger: "blur"
					}, ],
					mcpType: [{
						required: true,
						message: "请选择项目类型",
						trigger: "change"
					}, ],
					// url: [{
					// 	required: true,
					// 	message: "请输入服务地址",
					// 	trigger: "blur"
					// }, ]
				},
				showDeleteIcon: false,
				installWayList: [{
					label: 'npx',
					img: require('@/assets/images/npx.svg')
				}, {
					label: 'uvx',
					img: require('@/assets/images/uvx.svg')
				}, {
					label: 'sse',
					img: require('@/assets/images/sse.svg')
				}],
				applicationTypeLists: [{
						label: '地图服务',
						value: '地图服务'
					},
					{
						label: '网页搜索',
						value: '网页搜索'
					},
					{
						label: '内容生产',
						value: '内容生产'
					},
					{
						label: '社交通讯',
						value: '社交通讯'
					},
					{
						label: '数据库',
						value: '数据库'
					},
					{
						label: '效率工具',
						value: '效率工具'
					},
					{
						label: '其他',
						value: '其他'
					}
				],
				loading: false,
				imgLoading: false
			};
		},
		props: {
			dialogVisibleApplication: {
				type: Boolean,
				default: false,
			},
			params: Object,
			type: String,
		},
		async mounted() {
			if (this.type == "edit" || this.type == "details") {
				this.appForm = this.params
				this.getDetails()
				//    this.appForm.icon = this.params.icon;
				//    this.appForm.mcpName = this.params.mcpName;
				//    this.appForm.url = this.params.url;
				//    this.appForm.mcpType = this.params.mcpType;
				// this.appForm.installWay = this.params.installWay;
				// this.appForm.description = this.params.description;
			}
		},
		methods: {
			
			updateJson(newJson) {
				this.outputJsonData = newJson;
				console.log('JSON updated:', newJson);
			},
			imgMouseenter() {
				console.log("imgMouseenter");
				if (this.appForm.icon) {
					this.showDeleteIcon = true;
				}
			},
			imgMouseleave() {
				console.log("imgMouseleave");
				this.showDeleteIcon = false;
			},
			deleteLogo() {
				this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
				return;
			},
			cancelTemplate() {
				if (this.type == "add") {
					this.$emit("cancelApplication", false);
				} else {
					this.$emit("cancelEditApplication", false);
				}
			},
			//保存
			addSaveDraft: debounce(function(type) {
				this.$refs.ruleForm.validate(async (valid) => {
					if (valid) {
						this.appForm.status = type
						// this.appForm.installWay = 'sse'
						// if (this.type == "add") {
						if(this.appForm.installWay == 'sse'){
							if(this.appForm.url == ''){
								this.$message.warning('服务地址不能为空');
								return
							}
							if(this.isApiFlag == '需鉴权'){
								if(this.appForm.apiKey == ''){
									this.$message.warning('Api Key不能为空');
									return
								}
							}
						}else{
							this.appForm.url = ''
							this.appForm.apiKey = ''
						}
						
							
						this.loading = true;
						addMcpServer(this.appForm).then((data) => {
							if (data.code == "000000") {
								this.$message.success(data.msg);
								setTimeout(() => {
									this.$emit("close", false)
								}, 1000)

							} else {
								this.$message.warning(data.msg);
							}
						}).finally(() => {
							this.loading = false;
						});

						// } else {

						// }
					} else {
						return false;
					}
				});
			}, 1000),
			//编辑
			addEdit() {
				this.$refs.ruleForm.validate(async (valid) => {
					if (valid) {
						this.loading = true;
						addMcpServer(this.appForm).then((data) => {
							if (data.code == "000000") {
								this.$message.success(data.msg);
								setTimeout(() => {
									this.$emit("close", false)
								}, 1000)

							} else {
								this.$message.warning(data.msg);
							}
						}).finally(() => {
							this.loading = false;
						});
					} else {
						return false;
					}
				});
			},
			//查询服务
			addCheck: debounce(function(type) {
				this.$refs.ruleForm.validate(async (valid) => {
					if (valid) {
						checkMcp(this.appForm).then((data) => {
							if (data.code == "000000") {
								this.checkFlag = true
								this.mcpFunctionList = data.data

							} else {
								this.$message.warning(data.msg);
							}
						})
					} else {
						return false;
					}
				});
			}, 1000),
			getDetails() {
				getDetail(this.appForm.mcpId).then((data) => {
					if (data.code == "000000") {
						this.mcpFunctionList = data.data.mcpFunctionList
					}
				})
			},
			handleLogoRemove(file, fileList) {
				this.uploadBtnLogo = false;
				this.appForm.icon = "";
				this.fileListLogo = [];
			},
			handleLogoSuccess(response, file, fileList) {
				if ((response.code = "000000")) {
					this.uploadBtnLogo = true;
					this.appForm.icon =
						response.data && response.data[0] ? response.data[0].url : "";
					this.fileListLogo = [];
				} else {
					this.uploadBtnLogo = false;
					this.appForm.icon = "";
					this.fileListLogo = [];
				}
			},

			getRandomHeadImgDefaultBgColor() {
				const imgList = [
					require('@/assets/images/appManagement/mcp.png'),
					require('@/assets/images/appManagement/mcp-default-2.svg'),
					require('@/assets/images/appManagement/mcp-default-3.svg'),
					require('@/assets/images/appManagement/mcp-default-4.svg'),
					require('@/assets/images/appManagement/mcp-default-5.svg'),
					require('@/assets/images/appManagement/mcp-default-6.svg'),
					require('@/assets/images/appManagement/mcp-default-7.svg'),
				]
				const randomIndex = Math.floor(Math.random() * imgList.length);
				return imgList[randomIndex];
			},
			getImageUrl() {
				this.imgLoading = true;
				getAiImage({
					topic: this.appForm.applicationName,
					description: this.appForm.introduce
				}).then((res) => {
					if (res.code == "000000") {
						this.appForm.icon = res.data || this.getRandomHeadImgDefaultBgColor();
					} else {
						this.$message.warning('生成失败')
					}

					this.imgLoading = false;
				}).catch(() => {

					this.imgLoading = false;
				});
			},
			closePage() {
				this.$emit("close", false);
			}
		},
	};
</script>
<style lang="scss" scoped>
	::v-deep .main {
		background-color: #f6f6f6 !important;
	}

	.mcp-title {
		font-size: 16px;
		font-weight: 550;

		span {
			margin-left: 20px;
			color: #666;
			font-size: 14px;
			font-weight: normal;
		}
	}

	.el-radio {
		display: flex;
		flex-direction: row-reverse;
		/* 反转子元素的排列顺序 */
		justify-content: flex-end;
		margin-left: 0 !important;
	}

	::v-deep .el-radio__label {
		width: 100px;
		font-size: 16px;
		margin-right: 40px !important;
	}

	.page-wrap {
		width: 100%;
		min-height: 100vh;
		background-color: #f6f6f6;

		.el-icon-back {
			cursor: pointer;
		}

		.page-header {
			height: 72px;
			width: 100%;
			background: #fff;

			.header-wrap {
				height: 72px;
				padding: 0 28px;
			}

			.demo-form-inline {
				align-items: center;

				.el-form-item {
					margin-bottom: 0;
				}
			}
		}


	}

	.sse-item {
		justify-content: space-between;
	}

	.sse {
		width: 280px;
		height: 40px;
		line-height: 40px;
		border: 1px solid #eee;
		margin: 10px 0 20px 0;
		display: flex;
		align-items: center;
		position: relative
	}

	.checkList {
		margin: 20px 0;
	}

	.check-title {
		font-size: 18px;
		font-weight: 500;
		position: relative;
		padding-left: 14px;
		line-height: 20px;
		box-sizing: border-box;

		&::before {
			content: "";
			width: 4px;
			height: 20px;
			background: #1c50fd;
			position: absolute;
			left: 2px;


		}
	}

	.check-list-auto {
		// height: 60vh;
		// overflow-y: auto;
	}

	.check-list {
		margin-top: 30px;

		.list-item {
			border: 1px solid #eee;
			padding: 18px;
			box-sizing: border-box;
			margin-bottom: 10px;

			.list-item-top {
				margin: 0;
			}

			img {
				width: 30px;
				height: 30px;
			}
		}
	}

	.list-item-top {
		display: flex;
		align-items: center;
		// margin: 16px 16px 12px;

		// height: 56px;
		// border-bottom: 1px solid rgba(0, 0, 0, 0.12);
		img {
			width: 44px;
			height: 44px;
		}

		.text {
			margin: 0 20px 0 20px;
			font-family: MiSans, MiSans;
			font-weight: 550;
			font-size: 16px;
			color: #383d47;
			font-weight: 550;
		}

		.list-item-label {
			font-size: 14px;
		}

		.tips {
			//   width: 40px;
			padding: 0 8px;
			height: 20px;
			line-height: 20px;
			background: linear-gradient(270deg,
					rgba(142, 101, 255, 0.1) 0%,
					rgba(28, 80, 253, 0.1) 100%);
			border-radius: 4px;
			font-family: MiSans, MiSans;
			font-size: 12px;
			text-align: center;
			color: #383d47;
		}
	}

	.label-btn {
		padding: 6px 7px;
		background-color: #e1e4eb;
		font-size: 14px;
	}

	.list-item-label {
		color: #666;
		margin-top: 20px;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
		text-overflow: ellipsis;
		line-height: 22px;
	}

	.inputTextarea {
		::v-deep .el-textarea__inner {
			font-family: MiSans, MiSans;
		}
	}

	.logoAppUpload {
		text-align: center;

		::v-deep .el-upload--picture-card {
			border: 0;
			width: 80px;
			height: 80px;
		}
	}

	.form-box {
		position: relative;
		width: 960px;
		// height: calc(100vh - 140px);
		// margin-left: 20%;
		left: 50%;
		transform: translateX(-50%);
		background-color: #fff;
		margin-top: 20px;
		padding: 30px;
	}

	.demo-ruleForm {
		::v-deep .el-form-item__label {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 16px;
			color: #383d47;
			line-height: 32px;
		}
	}

	.flex {
		display: flex;
	}

	.flex-center {
		display: flex;
		align-items: center;
	}

	.aligns {
		align-items: center;
	}

	.just {
		justify-content: space-between;
	}

	.application-type-box {
		width: 100%;
		display: flex;
		align-items: center;
		gap: 16px;

		.application-type {
			width: 202px;
			height: 80px;
			display: flex;
			justify-content: center;
			background: #FFFFFF;
			border-radius: 2px;
			border: 1px solid #D5D8DE;
			align-items: center;
			cursor: pointer;

			.type-item {
				display: inline-flex;
				flex-direction: column;
				text-align: center;
				font-weight: 400;
				font-size: 14px;
				color: #494E57;
				line-height: 18px;

				img {
					width: 20px;
					height: 20px;
					margin: 0px auto 8px;
				}
			}

			&.active {
				background: rgba(28, 80, 253, 0.05);
				border: 1px solid #1747E5;
			}
		}
	}

	.uploadOuter {
		width: 100%;
		display: flex;
		align-items: end;

		:deep(.ai-btn) {
			height: 32px;
			background: linear-gradient(270deg, rgba(142, 101, 255, .15) 0%, rgba(23, 71, 229, .15) 100%);
			border-radius: 2px;
			display: inline-flex;
			align-items: center;
			border: 0px;
			padding: 0px 8px;

			margin-left: 16px;
			font-size: 14px;
			color: #1747E5;

			span {
				display: inline-flex;
				align-items: center;
			}

			img {
				margin-right: 2px;
				width: 16px;
				height: 16px;
				border: none;
			}
		}
	}

	::v-deep .el-date-editor .el-range-separator {
		width: 8%;
	}

	.codemirror-editor {
		width: 100%;
		height: 300px;
		/* 根据需要调整高度 */
	}
</style>