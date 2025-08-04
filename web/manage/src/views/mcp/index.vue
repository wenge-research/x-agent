<!-- 模型管理 -->
<template>
	<div>
		<div class='modelManagement no-scrollbar' v-if="!showStatistical">
			<div style="line-height: 46px;">
				<div class="modelTitle">MCP服务</div>

			</div>

			<div class="large-model-content">
				<div class="large-model-content-right" v-loading="loading">
					<div class="head">
						<div>
							<el-select class="app-type-select" style="width: 200px;margin-right: 16px;" v-model="status" @change="getLlmPageList">
							  <el-option value="" label="全部状态"></el-option>
							  <el-option value="1" label="已开通"></el-option>
							  <el-option value="0" label="未开通"></el-option>
							</el-select>
							<el-select class="app-type-select" style="width: 200px;margin-right: 16px;" v-model="addSelectType" @change="getLlmPageList">
							  <el-option value="" label="全部类别"></el-option>
							  <el-option :value="item.value" :label="item.label" v-for="(item,index) in applicationTypeLists" :key="index">
								  
							  </el-option>
							  
							</el-select>
							<el-input placeholder="输入MCP服务名称" prefix-icon="el-icon-search" v-model="paramSearch.mcpName"
								style="width: 385px;margin-right: 16px;" @keydown.native.enter="searchHandler('')" @input="searchHandler"
								clearable>
							</el-input>
							<el-checkbox v-model="isPerson" @change="changePerson">仅展示我创建的</el-checkbox>
						</div>
						<div>
							<el-button plain
								style="width: 154px; border: 1px solid #1747E5; color:#1747E5; border-radius: 2px;"
								@click="addShowStatistical">
								<img src="@/assets/images/add-circle-fill1.svg" alt="" style="
							  width: 14px;
							  height: 14px;
							  vertical-align: bottom;
							  margin-right: 8px;
							  
						  ">{{ $t("createModel") }}{{ $t("mcp") }}
							</el-button>
						</div>
					</div>
					<ul v-if="list.length" class="list no-scrollbar">
						<li class="list-item" v-for="(item, index) in list" :key="index" style="position: relative;">
							<div class="preset" v-if="item.ownerType=='official'&&isAdmin">预置</div>
							<div class="list-item-top" @click="addDe(item)">
								<div class="flex">
									<div class="list-item-top-icon">
										<img :src="item.icon" alt="" />
									    <img v-if="item.installWay=='npx'" class="installWay" style="width: 10px;height: 10px;" src="@/assets/images/npx.svg">
										<img v-else-if="item.installWay=='uvx'" class="installWay" style="width: 10px;height: 10px;" src="@/assets/images/uvx.svg">
										<img v-else style="width: 10px;height: 10px;" class="installWay" src="@/assets/images/sse.svg">
									</div>
									<div>
										<div class="text" :title="item.mcpName">{{ item.mcpName }}
										<!-- <i v-if="item.status == 2 "
												class="el-icon-success" style="color: #3FB816"></i> -->
									</div>
										<div><span class="label-btn">{{ item.mcpType }}</span></div>
									</div>
								</div>
								<div style="margin-left: 12px;">
									<span class="cancel" @click.stop="handleCommand('xjApp',item)" v-if="item.status==2">取消开通</span>
									<span class="obstacles" @click.stop="handleCommand('sjApp',item)" v-else>开通服务</span>
								</div>
							</div>
							<div class="list-item-bottom">
								<div class="list-item-label" @click="addDe(item)" :title="item.description">
									{{ item.description }}

								</div>
								<div class="row mt12">
									<div class="tips" @click="addDe(item)">
										<span class="list-user-icon"><iconpark-icon name="user-3-line"
												size="16"></iconpark-icon></span>
										<span class="create-user" v-if="item.createUserName">{{item.createUserName}}</span>
										<span class="point" style="margin-right: 8px"></span>
										{{item.updateTime || item.createTime}}发布
									</div>
									<div class="edit" v-if="isAdmin||userName==item.createUserName">
										<div class="opts-box"
											:class="{ 'opts-box-active': activeIndexMoreClick === index }">
											<el-dropdown trigger="click" @command="(value) => handleCommand(value,item)"
												placement="top-end" class="opts-box-dropdown"
												@visible-change="handleVisibleChange($event, index)">
												<span class="el-dropdown-link">
													<iconpark-icon name="more-line" size="18"
														color="#383838"></iconpark-icon>
												</span>
												<!-- api接入详情 -->
												<el-dropdown-menu slot="dropdown" class="opts-box-dropdown-menu">
													<!-- <el-dropdown-item command="xjApp"
														v-if="item.mcpServerUserStatus==1">
														<iconpark-icon color="#494E57"
															name="function-add-line"></iconpark-icon>
														<span style="color: #494E57">关闭</span>
													</el-dropdown-item>
													<el-dropdown-item command="sjApp" v-else>
														<iconpark-icon color="#494E57"
															name="function-add-line"></iconpark-icon>
														<span style="color: #494E57">{{ $t("listing") }}</span>
													</el-dropdown-item> -->
													<el-dropdown-item command="presetApp" v-if="isAdmin&&item.ownerType!='official'">
														<iconpark-icon color="#494E57"
															name="share-box-line"></iconpark-icon>
														<span style="color: #494E57">{{ "设为预置" }}</span>
													</el-dropdown-item>
													<el-dropdown-item command="presetApp" v-else-if="isAdmin">
														<iconpark-icon color="#F53F3F"
															name="share-box-line"></iconpark-icon>
														<span style="color: #F53F3F">{{ "取消预置" }}</span>
													</el-dropdown-item>
													<el-dropdown-item command="editeApp">
														<iconpark-icon color="#494E57"
															name="edit-box-line"></iconpark-icon>
														<span style="color: #494E57">{{ $t("edit") }}</span>
													</el-dropdown-item>
													<el-dropdown-item v-permission="'deleteApp'" command="deleteApp">
														<iconpark-icon color="#494E57"
															name="delete-bin-4-line"></iconpark-icon>
														<span style="color: #494E57">{{ $t("delete") }}</span>
													</el-dropdown-item>
												</el-dropdown-menu>
											</el-dropdown>
										</div>
									</div>
								</div>


							</div>
						</li>
					</ul>
					<div v-if="list.length" class="pagination">
						<div class="total">
							{{ $t("total") }}{{ pageObj.total }}{{ $t("items") }}
						</div>
						<el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
							:current-page.sync="pageObj.pageNo" :page-sizes="[12, 24, 36, 48]"
							:page-size="pageObj.pageSize" background layout="prev, pager, next, sizes"
							:total="pageObj.total">
						</el-pagination>
					</div>
					<div v-else class="no-data">
						<img src="@/assets/images/no-data.png" alt="" />
						<div class="txt1">暂无数据</div>
					</div>
				</div>
			</div>
		</div>
		<transition name="el-fade-in-linear">
			<mcpForm ref="StatisticalPage" :type="type" :params="mcpParams" v-if="showStatistical"
				@close="showStatisticalPage"></mcpForm>
		</transition>
		<el-dialog title="删除mcp服务" :visible.sync="deleteDialogVisible" width="560px" class="deleteDialog"
			:before-close="cancelDelete" append-to-body>
			<p class="desc">{{ $t("deletionWarning") }}</p>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelDelete">{{ $t("cancel") }}</el-button>
				<el-button type="primary" @click="confirmDelete">确定删除</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
	//这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
	//例如：import 《组件名称》 from '《组件路径》';
	import {
		getMcpServerList,
		updateStatus,
		deleteMcpServer,
		newUpdateStatus,
	} from "@/api/mcp";
	import {mcpServePreset} from "@/api/app"
	import Voice from "@/views/toolManager/voice/index.vue"
	import mcpForm from '@/views/mcp/components/mcpForm.vue'
	export default {
		//import引入的组件需要注入到对象中才能使用
		components: {
			Voice,
			mcpForm
		},
		name: "model",
		data() {
			//这里存放数据
			return {
				addSelectType:'',
				applicationTypeLists: [
				  {
				    label: "地图服务",
				    value: "地图服务",
				  },
				  {
				    label: "网页搜索",
				    value: "网页搜索",
				  },
				  {
				    label: "内容生产",
				    value: "内容生产",
				  },
				  {
				    label: "社交通讯",
				    value: "社交通讯",
				  },
				  {
				    label: "数据库",
				    value: "数据库",
				  },
				  {
				    label: "效率工具",
				    value: "效率工具",
				  },
				  {
				    label: "其他",
				    value: "其他",
				  },
				],
				activeIndexMoreClick: null,
				newItem: {},
				newItemName: '',
				deleteItemName: '',
				deleteDialogVisible: false,
				showStatistical: false,
				activeIndex: 0,
				pageObj: {
					pageNo: 1,
					pageSize: 24,
					total: 0,
				},
				userName:'',
                status:'',
				paramSearch: {
					mcpName: "", // 模型名称
				},
				list: [],
				manufacturerList: [],
				loading: false,
				type: 'add',
				dialogVisibleDeepseek: false,
				dialogVisible: false,
				addLoading: false,
				mcpId: '',
				mcpParams: {},
				isPerson:false
			};
		},
		//监听属性 类似于data概念
		computed: {
		  
			isAdmin ()
			{ 
			  const userType = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.powerType : "";
			  if(userType=='0'){
						return true
					}else{
						return false
					}
			}
		},

		//方法集合
		methods: {

			showStatisticalPage(data) {
				console.log(this.showStatistical)
				this.showStatistical = data
				this.getLlmPageList();
			},
			searchHandler(val) {
				this.pageObj.pageNo = 1;
				this.getLlmPageList();
			},
			handleCurrentChange(page) {
				this.pageObj.pageNo = page;
				this.getLlmPageList();
			},
			handleSizeChange(size) {
				this.pageObj.pageSize = size;
				this.getLlmPageList();
			},
			// 查询大模型信息表列表
			async getLlmPageList() {
				const params = {
					...this.paramSearch,
					pageSize: this.pageObj.pageSize,
					pageNo: this.pageObj.pageNo,
					mcpServerUserStatus:this.status,
					type:this.addSelectType,
					ownerType:this.isPerson?"personal":"personalGrantTenant"
				};
				this.loading = true;
				const res = await getMcpServerList(params);
				if (res.code == "000000") {
					// console.log("res", res);
					if (res.data) {
						this.list = res.data.records || [];
						this.pageObj.total = res.data?.totalRow || 0;
					}

				}
				this.loading = false;
			},
			changePerson(){
				this.getLlmPageList()
			},
			getMcpServePresetApi(data){
				mcpServePreset({id:data.id}).then(res=>{
					if(res.code=="000000"){
						this.getLlmPageList()
					}else{
						this.$message({
							type:"error",
							message:res.msg
						})
					}
				})
			},
			handleCommand(value, item) {
				if (value == 'editeApp') {
					console.log('点击编辑')
					this.type = 'edit'
					this.mcpParams = item
					this.showStatistical = true

				} else if (value == 'deleteApp') {
					this.mcpId = item.id
					this.deleteDialogVisible = true
				} else if(value == 'presetApp'){
					this.getMcpServePresetApi(item)
				} else {
					let dataItem = {}
					if (value == 'sjApp') {
						dataItem = {
							mcpId: item.mcpId,
							status: '2',
							id:item.id
						}
					} else {
						dataItem = {
							mcpId: item.mcpId,
							status: '1',
							id:item.id
						}
					}
					this.loading = true;

					newUpdateStatus(dataItem).then((data) => {
						if (data.code == "000000") {
							this.$message.success(data.msg);
							this.getLlmPageList();
						} else {
							this.$message.warning(data.msg);
						}
					}).finally(() => {
						this.loading = false;
					});
				}
			},
			handleVisibleChange(val, index) {
				if (val) {
					this.activeIndexMoreClick = index;
				} else {
					this.activeIndexMoreClick = null;
				}

			},
			createApp() {
				this.editItem = {};
				this.dialogVisibleDeepseek = true;
			},
			//删除弹窗关闭
			cancelDelete() {
				this.deleteDialogVisible = false
			},
			addShowStatistical() {
				this.showStatistical = true
				this.type = 'add'
			},
			addDe(item) {
				this.mcpParams = item
				this.showStatistical = true
				this.type = 'details'
			},
			confirmDelete() {
				deleteMcpServer(
					[this.mcpId]
				).then((data) => {
					if (data.code == "000000") {
						this.$message.success(data.msg);
						this.deleteDialogVisible = false
						setTimeout(() => {
							this.getLlmPageList();
						}, 1000)

					}
				})
			}
		},
		//生命周期 - 创建完成（可以访问当前this实例）
		created() {

		},
		//生命周期 - 挂载完成（可以访问DOM元素）
		mounted() {
			this.getLlmPageList();
			this.userName = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.accountName : ""
		},

	}
</script>
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>
<style scoped lang="scss">
	.modelManagement {
		padding: 32px 32px 12px 32px;
		box-sizing: border-box;
		overflow: hidden;

		.modelTitle {
			font-family: MiSans, MiSans;
			font-weight: 500;
			font-size: 24px;
			color: #383d47;
			line-height: 40px;
			text-align: left;
			font-style: normal;
			margin-bottom: 24px;
		}

		.tabUl {
			display: flex;
			justify-content: flex-start;

			li {
				margin-right: 16px;

				.tabName {
					font-size: 24px;
					line-height: 28px;
					text-align: right;
					cursor: pointer;
				}

				.defaultColor {
					color: #828894;
					font-weight: 400;
				}

				.activecolor {
					color: #383D47;
					font-weight: 600;
				}
			}
		}
	}

	.list-item-label {
		color: #666;
		margin-top: 10px;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
		text-overflow: ellipsis;
		line-height: 22px;
		height: 45px;
		font-size: 14px;
	}

	::v-deep .btn-prev {
		background-color: transparent !important;
		border-radius: 4px !important;
		border: 1px solid #dddfe8;
	}

	::v-deep .el-pagination.is-background .btn-next,
	.el-pagination.is-background .btn-prev,
	.el-pagination.is-background .el-pager li {
		background-color: transparent !important;
		border-radius: 4px !important;
		border: 1px solid #dddfe8;
	}

	::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
		background: transparent;
		border: 1px solid #3666ea;
		font-size: 16px;
		color: #3666ea;
	}

	::v-deep .el-pagination.is-background .el-pager li {
		background: transparent !important;
		border-radius: 4px;
		border: 1px solid #dddfe8;
	}

	::v-deep .el-pagination .el-select .el-input .el-input__inner {
		font-size: 16px;
		color: #272a31;
	}

	.large-model-content {
		width: 100%;
		height: calc(100vh - 80px - 32px);
		display: flex;
		margin-top: 16px;

		.preset{
			width: 39px;
			height: 23px;
			background: #EBEEF2;
			border-radius: 0px 7px 0px 8px;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 12px;
			color: #1D2129;
			line-height: 23px;
			text-align: center;
			position: absolute;
			top: 0;
			right: 0;
		}

		&-left {
			height: 100%;
			width: 240px;
			padding: 0 16px 16px;
			background: #ffffff;
			border-radius: 4px;
			border: 1px solid #e1e4eb;
			margin-right: 8px;

			.label {
				padding-left: 8px;
				height: 80px;
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 18px;
				color: #383d47;
				line-height: 80px;
			}

			.list {
				&-item {
					height: 40px;
					padding: 0 10px;
					margin-bottom: 4px;
					display: flex;
					align-items: center;
					border-radius: 4px;
					cursor: pointer;
					position: relative;

					&:hover {
						background: rgba(63, 138, 251, 0.08);
					}

					img {
						width: 24px;
						height: 24px;
						margin-right: 8px;
					}

					.name {
						font-family: MiSans, MiSans;
						font-size: 16px;
						color: #383d47;
					}
				}
			}

			.bgColor {
				background: rgba(28, 80, 253, 0.05);
				border: 1px solid #1C50FD;
			}
		}

		&-right {
			height: 100%;
			flex: 1;
			display: flex;
			flex-direction: column;
			background: #ffffff;
			border-radius: 8px;
			padding: 0 0 20px;

			//   border: 1px solid #e1e4eb;
			.head {
				display: flex;
				justify-content: space-between;
				height: 40px;
				width: 100%;
			}

			.list {
				flex: 1;
				margin-top: 28px;
				overflow: auto;
				display: grid;
				grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
				grid-template-rows: repeat(auto-fill,182px);
				flex-wrap: wrap;
				grid-gap: 24px 24px;
				&-item {
					height: 182px;
					background: #ffffff;
					border-radius: 4px;
					border: 1px solid #e1e4eb;
					cursor: pointer;

					&:nth-child(4n) {
						margin-right: 0;
					}

					&:hover {
						box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
					}

					&-top {
						display: flex;
						align-items: center;
						margin: 16px 16px 12px;
                        justify-content: space-between;
						// height: 56px;
						// border-bottom: 1px solid rgba(0, 0, 0, 0.12);
						
						
						.list-item-top-icon{
							position: relative;
							img {
								width: 40px;
								height: 40px;
							}
							.installWay{
								position: absolute;
								right: 0;
								bottom: 10px;
							}
						}
                       
						.label-btn {
							padding: 6px 7px;
							background-color: #e1e4eb;
							font-size: 14px;
							margin: 6px 6px 0 12px;
							display: inline-block;
						}

						.text {
							margin: 0 0px 0 12px;
							font-family: MiSans, MiSans;
							font-weight: 550;
							font-size: 18px;
							color: #383d47;
							width: 200px;
							white-space: nowrap;
							overflow: hidden;
							text-overflow: ellipsis;
						}
						.cancel{
							border: 1px solid #666;
							padding: 4px 8px;
							font-size: 14px;
							color: #333;
						}
                        .obstacles{
							border: 1px solid #1C50FD;
							padding: 4px 8px;
							font-size: 14px;
							color: #1C50FD;
						}
						.tips {
							padding: 4px 8px;
							line-height: 16px;
							background: linear-gradient(270deg,
									rgba(142, 101, 255, 0.1) 0%,
									rgba(28, 80, 253, 0.1) 100%);
							border-radius: 2px;
							font-family: MiSans, MiSans;
							font-size: 12px;
							text-align: center;
							color: #383d47;
						}
					}
                    .tips {
                      display: flex;
                      align-items: center;
                      font-family: MiSans, MiSans;
                      font-weight: 400;
                      font-size: 12px;
                      color: #828894;
                      line-height: 16px;
                      .circle {
                        margin-right: 5px;
                        width: 14px;
                        height: 14px;
                        border-radius: 50%;
                        background: #b4bccc;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        cursor: pointer;
                        .el-icon-caret-right {
                          font-size: 10px;
                          color: #fff;
                        }
                      }
					}
					&-bottom {
						padding: 0 16px 20px;

						.row {
							display: flex;
							align-items: center;
							justify-content: space-between;

							.label {
								font-family: MiSans, MiSans;
								font-size: 14px;
								color: #768094;
								width: 56px;
								white-space: nowrap;
								margin-right: 12px;
								height: 18px;
								line-height: 18px;
							}

							.value {
								font-family: MiSans, MiSans;
								font-size: 14px;
								color: #383d47;
								white-space: nowrap;
								overflow: hidden;
								text-overflow: ellipsis;
								height: 18px;
								line-height: 18px;
							}
						}

						.mt12 {
							margin-top: 20px;
						}
					}
				}
			}


		}
	}

	.deleteDialog {
		::v-deep .el-dialog__header {
			background: #fff !important;
		}

		::v-deep .el-dialog__title {
			font-family: MiSans, MiSans;
			font-weight: 500;
			font-size: 20px;
			color: #383d47;
			line-height: 24px;
			text-align: left;
			font-style: normal;
			text-transform: none;
		}

		::v-deep .el-dialog__headerbtn {
			top: 36px;
		}

		.desc {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 16px;
			color: #768094;
			line-height: 20px;
			text-align: left;
			font-style: normal;
			margin-bottom: 8px;
		}

		::v-deep .el-dialog__footer {
			text-align: right !important;

			.el-button {
				border-radius: 4px;
			}

			.el-button--primary {
				background: #1747E5;
				color: #fff;
				border-color: transparent;
			}
		}
	}
</style>