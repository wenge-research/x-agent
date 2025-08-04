<template>
	<div class="slice-info">
		<div class="header">
			<div class="header-left">
				<img class="back-icon" src="@/assets/images/arrow-go-back-fill.svg" @click="handleClose">
				<!-- <img class="type-icon" :src="require(`@/assets/images/${rowData.fileType}.svg`)" alt=""> -->
				<div class="title">接口详情</div>
			</div>
		</div>
		<div class="main">
			<div class="container-left">
				<div class="left-header">
					<div>
						<img style="width: 50px; height: 46px;" src="../../../assets/images/api_enter.png" alt="">
					</div>
					<p class="api-name">{{ apiData.name }}</p>
				</div>
				<div class="api-info">
					<div class="title">基础信息</div>
					<div class="api-info-container">
						<div class="info-panel">
							<div class="info-item">
								<span class="label">请求方式</span>
								<span class="value">{{ apiData.requestType == 0 ? 'GET' : 'POST' }}</span>
							</div>
							<div class="info-item">
								<span class="label">接口地址</span>
								<span class="value">{{ apiData.apiAddress }}</span>
							</div>
							<div class="info-item">
								<span class="label">定时任务</span>
								<span class="value">{{ apiData.scheduledTask }}</span>
							</div>
							<div class="info-item">
								<span class="label">上次请求时间</span>
								<span class="value">{{ apiData.scheduledTaskLastTime }}</span>
							</div>
							<div class="info-item">
								<span class="label">存储类型</span>
								<span class="value">{{ apiData.storageType == 0 ? '全量更新' : '增量更新' }}</span>
							</div>
						</div>
					</div>
				</div>

				<div class="api-record" @click="historical">
					<i class="el-icon-document-remove" style="margin-right: 6px;"></i>
					<span>请求记录</span>
				</div>
			</div>
			<div class="container-right">
				<div class="title">文本切片</div>
				<div class="right-header">
					<div class="data-tabs">
						<div class="tabs-item" :class="{ active: item.value === activeTab }"
							v-for="(item, index) in tabs" :key="index" @click="handleClick(item)">{{ item.name }}</div>
					</div>
					<!-- <div class="search-box">
						<div class="search">
							<el-input placeholder="请输入内容" prefix-icon="el-icon-search" v-model="searchVal">
							</el-input>
						</div>
						<div>
							<el-button type="primary" icon="el-icon-circle-plus-outline"
								@click="addSlice">新建</el-button>
						</div>
					</div> -->
				</div>
				<!-- 内容区域 -->
				<div class="card" v-for="(item, index) in cardData" :key="index">
					<div class="top">
						<div class="item-info">
							<div class="top-left-box">
								<span class="isUse" :class="{ notUse: item.status === false }">{{ item.status === true ?
									'可用' : '禁用' }}</span>
								<span>类型：</span>
								<span>{{ item.type == 'file' ? '原文切片' : item.type == 'new' ? '新建切片' : '复制切片' }}</span>
							</div>
							<div class="top-right-box">
								<span>字符：</span>
								<span>{{ item.content.length }}</span>
							</div>
						</div>
						<div class="item-operate" v-if="editingItem !== index">
							<div style="margin-right: 18px;">
								<el-switch v-model="item.status" active-color="#1747E5" inactive-color="#ddd"
									@change="handleStatusChange(item)" :width="36">
								</el-switch>
							</div>
							<div @click="toEdit(index)">
								<i class="el-icon-edit-outline" style="color: #1D2129; cursor: pointer;"></i>
							</div>
						</div>
						<div class="item-operate" v-if="editingItem === index">
							<div class="del-box" @click="delData(item)">
								删除切片
							</div>
							<div class="btn-box">
								<el-button plain size="mini" @click="cancelEdit()">取消</el-button>
								<el-button type="primary" size="mini" @click="submitEdit(item)">确定</el-button>
							</div>
						</div>
					</div>
					<div class="desc">
						<el-input :class="{ customTextarea: item.status === true }" type="textarea" placeholder="请输入内容"
							maxlength="1000" :disabled="editingItem !== index" v-model="item.content">
						</el-input>
					</div>
				</div>
				<div v-if="cardData.length == 0" style="margin-top: 100px;text-align: center;">
					暂无数据
				</div>
			</div>
		</div>

		<el-drawer title="请求记录" :append-to-body="true" :before-close="handleCloseHistorical"
			:visible.sync="historicalDrawer" size="820px">
			<div class="historical">
				<div>
					<el-date-picker v-model="dateValue" type="daterange" range-separator="到" start-placeholder="开始日期"
						end-placeholder="结束日期" @change="chooseDate" size="large">
					</el-date-picker>
				</div>

				<div class="historical-table">
					<el-table :data="tableData" style="width: 100%" :header-cell-style="{ background: '#F2F3F5' }">
						<el-table-column prop="createTime" label="请求时间" width="180">
						</el-table-column>
						<el-table-column prop="executeTime" label="执行时间(ms)" width="180">
						</el-table-column>
						<el-table-column prop="state" label="状态">
							<template slot-scope="scope">
								<div class="file-wrapper">

									<div class="fileName">{{ scope.row.state == 1 ? '成功' : '失败' }} </div>
								</div>
							</template>
						</el-table-column>
						<el-table-column prop="consumeToken" label="消耗tokens">
						</el-table-column>
					</el-table>

					<div class="pagination">
						<div class="total-num">
							{{ $t('totalNum', { total: total }) }}
						</div>
						<el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" background
							:page-size="pageSize" :current-page="pageNo" :page-sizes="[20, 50, 100, 150, 200]"
							layout="prev, pager, next, sizes, jumper" :total="total">
						</el-pagination>
					</div>
				</div>
			</div>
		</el-drawer>
	</div>
</template>

<script>
import {
	queryDetailById,
	getApiDatas,
	pageQuery,
	deleteApiData,
	addApiDataApi
} from './../../../api/Kbm'
export default {
	props: {
		urlId: {
			type: String,
			default: ''
		},
		knowledgeId: {
			type: String,
			default: ''
		},
	},
	watch: {
		'urlId'(newValue) {
			if (newValue != '') {
				this.queryDetailByIdApiData()
				this.getKnowledgeApiData()
				this.getUrlListData()
			}

		}
	},
	data() {
		return {
			tabs: [{
				name: "全部",
				value: ""
			},
			{
				name: "原文切片",
				value: "file"
			},
			{
				name: "新建切片",
				value: "new"
			},
			{
				name: "复制切片",
				value: "copy"
			}
			],
			apiData: {},
			activeTab: '', //tabs的value
			editingItem: null, // 当前正在编辑的数据
			searchVal: '',
			cardData: [
			],
			historicalDrawer: false, //历史记录弹窗
			dateValue: '', //日期选择
			tableData: [],
			pageNo: 1,
			pageSize: 50,
			total: 0,
			type: '',
			startTime: '',
			endTime: '',
		}
	},
	async mounted() {
		console.log('111')
		this.queryDetailByIdApiData()
		this.getKnowledgeApiData()
		this.getUrlListData()
	},
	methods: {

		//切换状态
		async handleStatusChange(item) {
			const parms = {
				id: item.id,
				status: item.status === true ? '1' : '0'
			}
			
			try {
				const res = await addApiDataApi(parms);
				if(res.code == "000000") {
					this.$message.success(res.msg);
				} else {
					this.$message.warning(res.msg);

				}
			} catch (error) {
				console.log(error)
			}
		},

		//确定编辑
		submitEdit(item) {
			this.editingItem = null; // 取消编辑
		},
		//取消编辑
		cancelEdit() {
			console.log('取消')
			this.editingItem = null; // 取消编辑
		},
		//点击对应数据编辑
		toEdit(item) {
			this.editingItem = item; // 设置当前正在编辑的项目
		},
		//新建
		addSlice() {
			this.cardData.unshift({
				status: true,
				type: '新建切片',
				stringNum: 0,
				text: ''
			});
			this.editingItem = 0;
		},
		//删除切片
		delData(item) {
			// this.cardData.splice(index, 1);
			let paramList = []
			deleteApiData({
				param: [item.id]
			}).then((data) => {
				if (data.code == "000000") {
					this.$message.success(data.msg);
					this.editingItem = null;
					this.getKnowledgeApiData()
				} else {
					this.$message.warning(data.msg);
				}
			}).finally(() => {

			});

		},
		//tabs切换
		handleClick(item) {
			this.activeTab = item.value
			this.type = item.value
			this.getKnowledgeApiData()
		},
		//关闭抽屉
		handleClose() {
			this.$emit('close')
		},
		//关闭二级抽屉
		handleCloseHistorical(done) {
			done();
		},
		//打开历史记录
		 historical() {
			this.getUrlListData();
			this.historicalDrawer = true;
		},
		//日期范围
		chooseDate(e) {
			if (e && e.length === 2) {
				this.startTime = this.formatDate(e[0]);
				this.endTime = this.formatDate(e[1]);
				this.pageNo = 1
				this.getUrlListData()
			}
		},
		//转化日期格式
		formatDate(date) {
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从 0 开始
			const day = String(date.getDate()).padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
		//分页
		handleCurrentChange(n) {
			this.pageNo = n
			this.getUrlListData()
		},
		handleSizeChange(n) {
			this.pageSize = n
			this.getUrlListData()
		},
		async getUrlListData() {
			let res = await pageQuery({
				pageSize: this.pageSize,
				pageNo: this.pageNo,
				knowledgeApiId: this.urlId,
				endTime: this.endTime,
				startTime: this.startTime,
			})
			if (res.code == "000000") {
				this.tableData = res.data.records
				this.total = res.data.totalRow
			}

		},
		async getKnowledgeApiData() {
			let res = await getApiDatas({
				knowledgeApiId: this.urlId,
				type: this.type
			})
			if (res.code == "000000") {
				this.cardData = res.data.list;
				this.cardData.forEach(item => {
					item.status = item.status == 1 ? true : false
				})
			}

		},
		async queryDetailByIdApiData() {
			let res = await queryDetailById({
				knowledgeApiId: this.knowledgeId
			})
			if (res.code == "000000") {
				this.apiData = res.data
			}
		},
	},
}
</script>



<style lang="scss" scoped>
.slice-info {
	width: 100%;
	height: 100%;
	display: flex;
	flex-direction: column;
	overflow: hidden;

	.header {
		width: 100%;
		height: 64px;
		padding: 24px 32px;
		border-bottom: 1px solid rgba(0, 0, 0, 0.12);
		background: #fff;
		display: flex;
		align-items: center;
		flex-shrink: 0;

		.header-left {
			width: 50%;
			display: flex;
			align-items: center;
			flex-shrink: 0;

			.back-icon {
				width: 20px;
				margin-right: 8px;
				cursor: pointer;
			}

			.type-icon {
				width: 28px;
				margin-right: 8px;
			}

			.title {
				max-width: 70%;
				margin-right: 8px;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				overflow: hidden;
				text-overflow: ellipsis;
				-webkit-line-clamp: 1;
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 18px;
				color: #494E57;
				line-height: 28px;
			}

			.tip-icon {
				cursor: pointer;
			}


		}
	}

	.main {
		flex: 1;
		display: flex;
		overflow: hidden;
	}

	.container-left {
		position: relative;
		box-sizing: border-box;
		padding: 0 16px;
		width: 288px;
		height: 100%;
		background: #FFFFFF;
		border-right: 1px solid rgba(0, 0, 0, 0.12);
		flex-shrink: 0;

		.left-header {
			margin-top: 36px;
			display: flex;
			align-items: center;
			justify-content: center;
			flex-direction: column;

			.api-name {
				margin-top: 16px;
				font-size: 16px;
				color: #1D2129;
			}
		}

		.api-info {
			margin-top: 30px;

			.title {
				font-size: 16px;
				color: #1D2129;
			}
		}

		.api-info-container {
			font-family: Arial, sans-serif;
			margin-top: 16px;
		}

		.info-panel {
			margin-bottom: 15px;
		}

		.info-item {
			display: flex;
			justify-content: space-between;
			margin-bottom: 16px;
		}

		.label {
			min-width: 90px;
			font-weight: 400;
			font-size: 14px;
			color: #86909C;
			margin-right: 16px;
		}

		.value {
			font-weight: 400;
			font-size: 14px;
			color: #1D2129;
			word-break: break-all;
		}

		.api-record {
			width: 256px;
			height: 33px;
			border-radius: 4px;
			border: 1px solid #E7E7E7;
			display: flex;
			align-items: center;
			justify-content: center;
			font-weight: 400;
			font-size: 14px;
			color: #1D2129;
			position: absolute;
			bottom: 20px;
			cursor: pointer;
		}
	}

	.container-right {
		box-sizing: border-box;
		padding: 28px 32px;
		flex: 1;
		height: 100%;
		background: #F7F8FA;
		overflow-y: auto;

		.title {
			font-size: 22px;
			color: #1D2129;
			margin-bottom: 30px;
		}

		.right-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 16px;

			.data-tabs {
				display: flex;

				.tabs-item {
					margin-right: 16px;
					font-weight: 400;
					font-size: 18px;
					color: #86909C;
					line-height: 28px;
					cursor: pointer;
				}

				.active {
					color: #603ECA;
				}
			}

			.search-box {
				display: flex;

				.search {
					margin-right: 16px;
				}
			}
		}

		.card {
			width: 100%;
			// height: 80px;
			background-color: #FFFFFF;
			margin-bottom: 16px;
			box-sizing: border-box;
			padding: 16px;

			.top {
				display: flex;
				justify-content: space-between;
				margin-bottom: 14px;

				.item-info {
					display: flex;
					align-items: center;
					font-weight: 400;
					font-size: 14px;
					color: #86909C;

					.top-left-box {
						line-height: 20px;
						padding-right: 16px;
						border-right: 1px solid #E5E6EA;
						margin-right: 16px;

						.isUse {
							padding: 4px;
							background: #E5F4E9;
							border-radius: 2px;
							// opacity: 0.1;
							font-weight: 400;
							font-size: 12px;
							color: #009A29;
							margin-right: 10px;
						}

						.notUse {
							background: #EDEFF2;
							color: #86909C;
						}
					}
				}

				.item-operate {
					display: flex;
					align-items: center;
					padding-right: 12px;

					.del-box {
						font-weight: 400;
						font-size: 14px;
						color: #F53F3F;
						margin-right: 20px;
						cursor: pointer;
					}
				}
			}
		}
	}

}

.historical {
	padding: 32px;
	padding-top: 0;

	.historical-table {
		margin-top: 16px;
	}
}

.pagination {
	display: flex;
	// justify-content: flex-end;
	align-items: center;
	margin-top: 24px;
	position: relative;

	.tips {
		position: absolute;
		color: red;
		bottom: 8px;
		left: 0;
	}
}

.historical-table {
	display: flex;
	flex-direction: column;
	height: 80vh;
	/* 设置容器高度为视口高度 */
}

.table-container {
	flex: 1;
	/* 表格容器占据剩余空间 */
	overflow-y: auto;
	/* 启用垂直滚动条 */
}

.pagination-container {
	background: white;
	/* 设置背景色 */
	padding: 10px;
	box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1);
	/* 添加阴影效果 */
	position: sticky;
	/* 固定在底部 */
	bottom: 0;
	z-index: 1000;
	/* 确保分页在滚动时位于顶部 */
}

::v-deep .customTextarea .el-textarea__inner:disabled {
	background-color: #f5f7fa;
	/* 背景颜色置灰 */
	color: #606266;
	/* 字体颜色保持不变 */
}

::v-deep .el-date-editor .el-range-separator {
	width: 20px;
}
</style>