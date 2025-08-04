<!-- 模型管理 -->
<template>
	<div>
		<div class='modelManagement no-scrollbar' v-if="!showStatistical">
			<div style="line-height: 46px;">
				<div class="modelTitle">数据</div>

			</div>

			<div class="large-model-content">
				<div class="large-model-content-right" v-loading="loading">
					<div class="head">
						<div>
							<!-- <el-select class="app-type-select" style="width: 200px;margin-right: 16px;" v-model="status"
								@change="getLlmPageList">
								<el-option value="" label="全部类型"></el-option>
								<el-option value="1" label="已开通"></el-option>
								<el-option value="0" label="未开通"></el-option>
							</el-select> -->
							<el-input placeholder="输入数据集/数据库名称" prefix-icon="el-icon-search"
								v-model="paramSearch.name" style="width: 385px;"
								@keydown.native.enter="searchHandler('')" @input="searchHandler" clearable>
							</el-input>
						</div>
						<div>
							<el-button plain
								style="width: 154px;background-color: #1c50fd;  color:#fff; border-radius: 2px;"
								@click="addShowStatistical">
								<i class="el-icon-circle-plus" style="margin-right: 8px;"></i>新增数据库
							</el-button>
						</div>
					</div>
					<ul v-if="list.length" class="list no-scrollbar">
						<li class="list-item" v-for="(item, index) in list" :key="index" @click="addDe(item)">
							<div class="list-item-top">
								<div class="flex">
									<div class="list-item-top-icon">
										<img src="@/assets/images/sjk-icon1.png" alt="" v-if="item.type==1" />
										<img src="@/assets/images/sjk-icon2.png" alt="" v-else />
									</div>									
									<div class="text" :title="item.name">{{ item.name }}</div>																		
								</div>
								
							</div>
							<div>
								<span class="label-btn">{{ item.type==1?'Excel上传':'直连数据库' }}</span>
								<span class="label-btn">{{ item.tableNum?item.tableNum:'0' }}张表</span>
							</div>
							<div class="list-item-bottom">
								<div class="list-item-label" :title="item.description">
									{{ item.describe }}

								</div>
								<div class="row mt12">
									<div class="tips">
										<span class="list-user-icon"><iconpark-icon name="user-3-line"
												size="16"></iconpark-icon></span>
										<span class="create-user"
											v-if="item.createUserName">{{item.createUserName}}</span>
										<span class="point" style="margin-right: 8px"></span>
										{{item.updateTime || item.createTime}}<span style="margin-left: 5px;">发布</span>
									</div>
									<div class="edit" @click.stop>
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
			<dataForm ref="StatisticalPage" :type="type" :params="mcpParams" v-if="showStatistical"
				@close="showStatisticalPage"></dataForm>
		</transition>
		<el-dialog title="删除数据" :visible.sync="deleteDialogVisible" width="560px" class="deleteDialog"
			:before-close="cancelDelete" append-to-body>
			<p class="desc">{{ $t("deletionWarning") }}</p>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelDelete">{{ $t("cancel") }}</el-button>
				<el-button type="primary" @click="confirmDelete">确定删除</el-button>
			</span>
		</el-dialog>
		<el-dialog class="my-dialog" :title="$t('addSql')" :visible.sync="createVisible" width="560px" :modal-append-to-body="false"
		  :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
		  <div class="task-dialog">
			<div class="sql-title">创建方式</div>
		    <div class="options">
		      <div class="option" :class="{ 'option-active': item.id === taskActive }"
		        v-for="item in dataList" :key="item.id" @click="handleTaskChange(item.id)">
		         <div><img :src="item.icon" alt=""></div>
		        <div>{{ item.label }}</div>
		      </div>
		    </div>
		    <div class="xlsx-con" v-if="[1].includes(taskActive)">
		      <el-form :model="sqlForm" ref="sqlForm" :rules="rules">
		        		        
		        <el-form-item :label="$t('sqlName')" prop="name">
		          <el-input v-model="sqlForm.name" placeholder="请输入数据库名称"></el-input>
		        </el-form-item>
		        <el-form-item :label="$t('sqlMs')" prop="describe">
		          <el-input type="textarea" v-model="sqlForm.describe" placeholder="请输入描述"></el-input>
		        </el-form-item>
		      </el-form>
		     
		    </div>
		    <div class="sql-con" v-if="[2].includes(taskActive)">
		      <el-form :model="sqlForm" ref="sqlForm" :rules="rules">
		        <el-form-item :label="$t('sqlName')" prop="name">
		          <el-input v-model="sqlForm.name" placeholder="请输入数据库名称"></el-input>
		        </el-form-item>
				<el-form-item :label="$t('sqlMs')" prop="describe">
				  <el-input type="textarea" v-model="sqlForm.describe" placeholder="请输入描述"></el-input>
				</el-form-item>
				<div class="sql-title">数据源信息</div>
				<div class="sql-box">
					<el-form-item :label="$t('sqlType')" prop="desc">
					  <el-select placeholder="请选择数据源类型" v-model="sqlForm.type"
						style="width: 100% ">
						<el-option v-for="item in networkChannelList" :label="item.label" :value="item.value"
						  :key="item.value"></el-option>
					  </el-select>
					</el-form-item>
					<div class="inline-form-item">
					  <el-form-item label="数据库地址ip" prop="host">
						<el-input v-model="sqlForm.host" placeholder="请输入数据库地址ip"></el-input>
					  </el-form-item>
					  <el-form-item label="端口号" prop="port">
						<el-input v-model="sqlForm.port" placeholder="请输入端口号"></el-input>
					  </el-form-item>
					</div>
					
					<el-form-item label="数据库名" prop="database">
					  <el-input v-model="sqlForm.database" placeholder="请输入数据库名"></el-input>
					</el-form-item>
					<div class="inline-form-item">
					  <el-form-item :label="$t('userName')" prop="username">
						<el-input v-model="sqlForm.username" placeholder="请输入用户名"></el-input>
					  </el-form-item>
					  <el-form-item :label="$t('password')" prop="pwkey">
						<el-input type="password" v-model="sqlForm.pwkey" placeholder="请输入密码"></el-input>
					  </el-form-item>
					</div>
				</div>
		      </el-form>
		    </div>
		    
		  </div>
		  <div slot="footer" class="dialog-footer my-dialog-footer">
		    <el-button :loading="createLoading" @click="createVisible = false">{{ $t('cancel') }}</el-button>
		    <el-button :loading="createLoading" type="primary" @click="handleCreateTask">{{$t('confirm')}}</el-button>
		  </div>
		</el-dialog>
	</div>
</template>

<script>
	//这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
	//例如：import 《组件名称》 from '《组件路径》';
	import {
		getDataCollectInfoList,
		addDataCollectInfo,
		addCollectBaseConfig,
        deleteDataCollectInfo
	} from "@/api/dataSj";
	import Voice from "@/views/toolManager/voice/index.vue"
	import dataForm from '@/views/dataSj/components/dataForm.vue'
	export default {
		//import引入的组件需要注入到对象中才能使用
		components: {
			Voice,
			dataForm
		},
		name: "model",
		data() {
			//这里存放数据
			return {
				sqlForm:{},
				sqlObj:new FormData(),
				activeIndexMoreClick: null,
				newItem: {},
				newItemName: '',
				deleteItemName: '',
				deleteDialogVisible: false,
				showStatistical: false,
				createVisible: false,
				activeIndex: 0,
				taskActive: 1,
				pageObj: {
					pageNo: 1,
					pageSize: 24,
					total: 0,
				},
				userName: '',
				status: '',
				paramSearch: {
					name: "", // 模型名称
				},
				list: [],
				manufacturerList: [],
				loading: false,
				type: 'add',
				dialogVisibleDeepseek: false,
				dialogVisible: false,
				addLoading: false,
				createLoading: false,
				mcpId: '',
				mcpParams: {},
				rules: {
				  name: [{ required: true, message: this.$t('pleaseEnterFileLinkName'), trigger: 'blur' }],
				  pwkey: [{ required: true, message: '请输入密码', trigger: 'blur' }],
				  username: [{ required: true, message: this.$t('pleaseEnterUsername'), trigger: 'blur' }],
				  port: [{ required: true, message: this.$t('pleaseEnterPort'), trigger: 'blur' }],
				  host: [{ required: true, message: '请输入数据库地址', trigger: 'blur' }],
				  database: [{ required: true, message: '请输入数据库名', trigger: 'blur' }],
				},
				networkChannelList:[
					{label:'mysql',value:'1'},
					],
				dataList:[
				  {
					id: 1,
					icon: require('@/assets/images/sjk-icon1.png'),
					bgColor: '#DDF3E8',
					label: app.$t('sqlSc')
				  },
				  {
					id: 2,
					icon: require('@/assets/images/sjk-icon2.png'),
					bgColor: '#DFEDFF',
					label: app.$t('sqlZl')
				  }
				]
			};
		},
		

		//方法集合
		methods: {
            handleTaskChange(id) {
              this.taskActive = id
              
            },
			handleCreateTask() {
			  if (this.taskActive === 1) {
			    this.fileManipulation()
			  } else {
			    this.fileManipulation1()
			  }
			},
			//获取数据表
			async fileManipulation(){
				try {
					this.sqlForm.type = this.taskActive
				  const res = await addDataCollectInfo(this.sqlForm)
				  if (res.code === '000000') {
				    this.createVisible = false
				    this.createLoading = false
				    this.pageObj.pageNo = 1;
					this.sqlForm.describe = ''
					this.sqlForm.name = ''
					this.sqlForm.type = ''
				    this.getLlmPageList();
				  } else {
						this.$message({
						  message: res.msg,
						  showClose: true,
						  type: 'error'
						});
				  }
				  this.createLoading = false
				} catch (error) {
				  this.$message({
				    message: error,
						  showClose: true,
				    type: 'error'
				  });
				  this.createLoading = false
				}
			},
			async fileManipulation1(){
				this.$refs['sqlForm'].validate(async (valid) => {
				  if (valid) {
						try {
							let sqlForm = {
								type:this.taskActive,					
								describe:this.sqlForm.describe,
								name:this.sqlForm.name,
								
							}
						  const res = await addDataCollectInfo(sqlForm)
						  if (res.code === '000000') {
							this.createVisible = false
							this.sqlForm.collectId = res.data.collectId
							this.CollectBaseConfig()
						  } else {
								this.$message({
								  message: res.msg,
								  showClose: true,
								  type: 'error'
								});
						  }
						  this.createLoading = false
						} catch (error) {
						  this.$message({
							message: error,
								  showClose: true,
							type: 'error'
						  });
						  this.createLoading = false
						}
					}
				})
			},
			async CollectBaseConfig(){
				try {
					
					this.sqlObj = new FormData();
					
					this.sqlObj.append('collectId', this.sqlForm.collectId)
					this.sqlObj.append('type', 'db')
					this.sqlObj.append('describe', this.sqlForm.describe)
					this.sqlObj.append('configNames', this.sqlForm.name)
					this.sqlObj.append('dbConfig.port', this.sqlForm.port)
					this.sqlObj.append('dbConfig.username', this.sqlForm.username)
					this.sqlObj.append('dbConfig.pwkey', this.sqlForm.pwkey)
					this.sqlObj.append('dbConfig.database', this.sqlForm.database)
					this.sqlObj.append('dbConfig.host', this.sqlForm.host)
				  const res = await addCollectBaseConfig(this.sqlObj)
				  if (res.code === '000000') {
						// this.createVisible = false
						this.pageObj.pageNo = 1;
						this.sqlForm.describe = ''
						this.sqlForm.name = ''
						this.sqlForm.collectId = ''
						this.sqlForm.port = ''
						this.sqlForm.username = ''
						this.sqlForm.type = ''
						this.sqlForm.pwkey = ''
						this.sqlForm.database = ''
						this.sqlForm.host = ''
						this.getLlmPageList();
				  } else {
						this.$message({
						  message: res.msg,
						  showClose: true,
						  type: 'error'
						});
				  }
				  // this.createVisible = false
				  // this.pageObj.pageNo = 1;
				  // this.getLlmPageList();
				  this.createLoading = false
				} catch (error) {
				  this.$message({
				    message: error,
						  showClose: true,
				    type: 'error'
				  });
				  this.createLoading = false
				}
			},
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
					pageNo: this.pageObj.pageNo
				};
				this.loading = true;
				const res = await getDataCollectInfoList(params);
				if (res.code == "000000") {
					// console.log("res", res);
					if (res.data) {
						this.list = res.data.records || [];
						this.pageObj.total = res.data?.totalRow || 0;
					}

				}
				this.loading = false;
			},
			handleCommand(value, item) {
				if (value == 'editeApp') {
					console.log('点击编辑')
					this.type = 'edit'
					this.mcpParams = item
					this.showStatistical = true

				} else if (value == 'deleteApp') {
					this.mcpId = item.collectId
					this.deleteDialogVisible = true
				} else {
					let dataItem = {}
					if (value == 'sjApp') {
						dataItem = {
							mcpId: item.mcpId,
							status: '1'
						}
					} else {
						dataItem = {
							mcpId: item.mcpId,
							status: '0'
						}
					}
					this.loading = true;

					updateStatus(dataItem).then((data) => {
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
				this.createVisible = true
				
			},
			addDe(item) {
				this.mcpParams = item
				this.showStatistical = true
				this.type = 'details'
			},
			confirmDelete() {
				deleteDataCollectInfo(
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
		height: 22px;
		// height: 45px;
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
                -ms-flex-wrap: wrap;
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
						.text {
							margin: 0 0px 0 12px;
							font-family: MiSans, MiSans;
							font-weight: 550;
							font-size: 18px;
							color: #383d47;
							line-height: 40px;
							white-space: nowrap;
							overflow: hidden;
							text-overflow: ellipsis;
						}
                        .list-item-top-icon{
							img{
								width: 40px;
								height: 40px;
							}
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
	.sql-title{
		margin-bottom: 10px;
		font-weight: 550;
		font-size: 16px;
		color: #333;
	}
	.sql-box{
		background-color: #f6f8fa;
		border-radius: 6px;
		padding: 10px;
		box-sizing: border-box;
	}
    .label-btn {
    	padding: 6px 7px;
    	background-color: #f3f5f7;
    	font-size: 14px;
    	margin: 0px 6px 0 12px;
    	display: inline-block;
		border-radius: 4px;
    }

		::v-deep .el-dialog__header {
			background: #fff !important;
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
        ::v-deep .el-dialog__body{
			padding: 20px;
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
	    ::v-deep .el-dialog__title {
	    	font-family: MiSans, MiSans;
	    	font-weight: 550;
	    	font-size: 20px;
	    	color: #333;
	    	line-height: 24px;
	    	text-align: left;
	    	font-style: normal;
	    	text-transform: none;
	    }
	    ::v-deep  .el-form-item__label{
	    	font-family: MiSans, MiSans;
	    	font-weight: 550;
	    	font-size: 16px;
	    	color: #333;
	    	line-height: 24px;
	    	text-align: left;
	    	font-style: normal;
	    	text-transform: none;
	    	margin-bottom: 10px;
	    }
	.task-dialog {
	  .title {
	    font-weight: 500;
	    font-size: 16px;
	    color: #383D47;
	    line-height: 24px;
	    margin-top: 20px;
	  }
	
	  .message {
	    font-weight: 400;
	    font-size: 14px;
	    color: #768094;
	    line-height: 18px;
	    margin-top: 4px;
	    margin-bottom: 8px;
	  }
	
	  .options {
	    display: flex;
	    justify-content: space-between;
	    background: #fff;
	    .option {
	      width: 252px;
	      height: 66px;
	      background: #fff;
		  border: 1px solid #eee;
	      border-radius: 6px;
	      text-align: center;
	      font-weight: 500;
	      font-size: 16px;
	      color: #494E57;
	      line-height: 24px;
	      cursor: pointer;
	      padding: 6px 0;   
	      img {
	        width: 24px;
	        height: 24px;
	       
	      }
	    }
	    .option-active {
	      background: #f4f7ff;
		  border: 1px solid #5470be;
	      // box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
	      color: #828894;
	    }
	  }
	
	  .xlsx-con, .sql-con {
	    margin-top: 16px;
	
	  }
	  .sql-con {
	    .inline-form-item {
	      display: flex;
	      align-items: center;
	      .el-form-item:first-child {
	        width: 65%;
	        margin-right: 16px;
	      }
	    }
	  }
	  
	}
</style>