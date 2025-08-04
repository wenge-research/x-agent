<template>
	<div class="slice-info">
		<div class="header">
			<div class="header-left">
				<img class="back-icon" src="@/assets/images/arrow-go-back-fill.svg" @click="handleClose">
				<img class="type-icon" :src="sqlDetails.icon" alt=""> 
				<div class="title">{{sqlDetails.tableName}}</div>
				<div style="margin: 0 auto;" class="answer-span">
				   <span :class="status=='0'?'answer-on':''" @click="status = 0">结构</span>
				   <span :class="status=='1'?'answer-on':''" @click="status = 1">数据</span>
				</div>
			</div>
		</div>
		<div class="main">
				<div class="historical-table" v-if="status == 0">
					<el-table :data="tableData" style="width: 100%" height="250" :header-cell-style="{ background: '#F2F3F5' }">
						<el-table-column prop="fieIdName" label="存储字段名称">
							<template slot-scope="scope">
							  <div class="file-wrapper">
							      <el-input							       
							        v-model="scope.row.fieldName"
							        style="width: 100%" :disabled="scope.row.fieldName=='id'||scope.row.fieldName=='uuid'?true:false"
							      >
							      </el-input>
							  </div>
							</template>
						</el-table-column>
						<el-table-column prop="comment" label="描述">
							<template slot-scope="scope">
							  <div class="file-wrapper">
							      <el-input							       
							        v-model="scope.row.comment"
							        style="width: 100%" :disabled="scope.row.fieldName=='id'||scope.row.fieldName=='uuid'?true:false"
							      >
							      </el-input>
							  </div>
							</template>
						</el-table-column>
						<el-table-column prop="fieIdType" label="数据类型">
							<template slot-scope="scope">
							  <div class="file-wrapper">							    
								  <el-select size="medium" :disabled="scope.row.fieldName=='id'||scope.row.fieldName=='uuid'?true:false" v-model="scope.row.fieldType">
								    <el-option
								      v-for="(item, index) in panelTypeList"
								      :key="index"
								      :label="item.label"
								      :value="item.value"
								    ></el-option>
								  </el-select>
							  </div>
							</template>
						</el-table-column>
						<el-table-column prop="requestFlag" label="必要" width="280">
							<template slot-scope="scope">
							  <div class="file-wrapper">
							     <el-switch
							       style="padding-right: 8px"
							       v-model="scope.row.requestFlag"
							       active-value="1"
							       inactive-value="0"
								     :disabled="scope.row.fieldName=='id'||scope.row.fieldName=='uuid'?true:false"
							       active-color="#1747E5"
							       inactive-color="#CED4E0"
							     >
							     </el-switch>
							  </div>
							</template>
						</el-table-column>
						<el-table-column prop="state" label="操作" width="180">
							<template slot-scope="scope">
							  <div class="file-wrapper" v-if="scope.row.fieldName!='id'&&scope.row.fieldName!='uuid'">
							    <img style="width: 20px;" src="@/assets/images/remove.svg" @click="handleRemove(scope.row)">
							  </div>
							</template>
						</el-table-column>
						
					</el-table>
					<div style="display: flex;justify-content: space-between;margin-top: 20px;">
					  <el-button
					    icon="el-icon-circle-plus"
					    style="width: 168px;"
					    @click="addZd"
					    >添加字段</el-button
					  >
					  <div>
					    <el-button
					      type="primary"
					      style="width: 168px;"
					      @click="upBinfo"
					      >更新表结构</el-button
					    >
					  </div>
					</div>
				</div>
				<div class="historical-table" v-if="status == 1">
					<el-table :data="tablecolumnData" style="width: 100%" height="250" :header-cell-style="{ background: '#F2F3F5' }">
						<el-table-column v-for="column in tableColumns" 
								:label="column" min-width="100">
								
								<template slot-scope="scope">		
								       <el-input
								         v-model="scope.row[column]"										 
								         style="width: 100%"
										  :disabled="column=='id'||column=='uuid'?true:false"
								       ></el-input>
								</template>
						</el-table-column>
											
					</el-table>
				
					<div class="pagination">
						<div class="total-num">
							{{ $t('totalNum', { total: total }) }}
						</div>
						<el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" background
							:page-size="pageSize" :current-page="pageNo" 
							layout="prev, pager, next, sizes, jumper" :total="total">
						</el-pagination>
					</div>
					<div style="display: flex;justify-content: space-between;margin-top: 20px;">
					  <el-button
					    icon="el-icon-circle-plus"
					    style="width: 168px;"
					    @click="addSjZd"
					    >添加数据</el-button
					  >
					  <div>
					    <el-button
					      type="primary"
					      style="width: 168px;"
					      @click="upSjinfo"
					      >更新表数据</el-button
					    >
					  </div>
					</div>
				</div>
		</div>
		
	</div>
</template>

<script>
	import { addNodeTableFields,getNodeTableDataList,addComponentNodeTableField,addNodeTableData } from "@/api/sql";
	export default {
		props: {
			sqlDetails: {
				type: Object,
				default: ''
			}
		},
		watch: {
		    'sqlDetails.tableId'(newValue) {
		    	if (newValue != '') {
		    		this.nodeTableFields()
		    		this.getNodeTableDataLists()
		    	}
		    
		    }
		},
		computed: {
		    getDynamicKey() {
		      return (index) => {
				  console.log(index)
		        // 根据index或其他逻辑返回你想绑定的键名
		        return index
		      };
		    }
		},
		data() {
			return {
				status:0,
				tableData: [],
				pageNo: 1,
				pageSize: 10,
				total: 0,
				tableColumns:[],
				tablecolumnData:[],
				panelTypeList:[
					{
						label:'varchar(255)',
						value:'varchar(255)',
					},
					{
						label:'int',
						value:'int',
					},
					{
						label:'‌decimal(12,4)',
						value:'‌decimal(12,4)',
					},
					{
						label:'‌datetime',
						value:'‌datetime',
					},
					{
						label:'‌text',
						value:'‌text',
					},
				],
				obj:{
					fieldName:'',
					fieldType:'varchar(255)',
					requestFlag:'0',
					comment:''
				},
				columnobj:{
					
				},
			}
		},
		mounted() {
			this.nodeTableFields()
			this.getNodeTableDataLists()
		},
		methods: {
			getInput(){
				console.log(11)
			},
			addZd(){
				this.tableData.push(JSON.parse(JSON.stringify(this.obj)))
			},
			handleRemove(data){
				let businessIdListId = this.tableData.map((item) => item.fieldName);
				let sqlDataIndex = businessIdListId.findIndex((items) => items == data.fieldName);
				// console.log('sqlDataIndex',businessIdListId,)
				// console.log('sqlDataIndex',sqlDataIndex)
				this.tableData.splice(sqlDataIndex, 1);
			},
			addSjZd(){
				
				this.tablecolumnData.push(JSON.parse(JSON.stringify(this.columnobj)))
				console.log('this.columnobj',this.tablecolumnData)
			},
			upSjinfo(){
				console.log('this.tablecolumnData',this.tablecolumnData)
				addNodeTableData({
				  dataList:this.tablecolumnData,
				  tableId: this.sqlDetails.tableId
				}).then((res) => {
				  if (res.code == "000000") {
				    this.$message.success(res.msg,'1');
				    setTimeout(() => {
				    	this.getNodeTableDataLists()
				    }, 500)
				  }   
				});
			},
			upBinfo(){
				
				for(var i=0;i<this.tableData.length;i++){
					if (!/^[a-zA-Z0-9_]+$/.test(this.tableData[i].fieldName)) {
					        // 如果不符合条件，可以清除非法字符或者提醒用户				      
					        // 或者显示错误信息
							this.$message.warning('存储字段名称输入只能包含英文字符或下划线！')
					       return
					}
				}
				addComponentNodeTableField({
				  fieldList:this.tableData,
				  tableId: this.sqlDetails.tableId
				}).then((res) => {
				  if (res.code == "000000") {
				    this.$message.success(res.msg,'1');
				    setTimeout(() => {
				    	this.nodeTableFields()
				    }, 500)
				  }   
				});
			},
			nodeTableFields() {
			  addNodeTableFields({
			   tableId: this.sqlDetails.tableId
			  }).then((res) => {
			    if (res.code == "000000") {
					if(res.data.length>0){
						this.tableData = res.data
						this.tableColumns = res.data.map((item) => item.fieldName);
						this.tableData .forEach(item => {   
							this.columnobj[item.fieldName] = ''
						})
					}else{
						this.tableData = [{
							  fieldName:'id',
							  fieldType:'int',
							  requestFlag:'1',
							  comment:'自增id'
					},{
							  fieldName:'uuid',
							  fieldType:'varchar(255)',
							  requestFlag:'1',
							  comment:'自增uuid'
						}];
					}
			        
					
			    } else {
			      
			    }	      
			  });
			},
			//关闭抽屉
			handleClose() {
				this.$emit('closeDialog')
			},
			//分页
			handleCurrentChange(n) {
				this.pageNo = n
				this.getNodeTableDataLists()
			},
			handleSizeChange(n) {
				this.pageSize = n
				this.getNodeTableDataLists()
			},
			getNodeTableDataLists() {		
				getNodeTableDataList({
				  pageNo: this.pageNo,
				  pageSize:this.pageSize,	     
				  tableId: this.sqlDetails.tableId
				}).then((res) => {
				  if (res.code == "000000") {
				     this.tablecolumnData = res.data.records
					 this.total =  res.data.totalRow
				  } else {
				    this.konwlwdgeList = [];
				  }	      
				});
					
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
				width: 100%;
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
	}

	.historical {
		padding: 32px;
		padding-top: 0;
        width: 100%;
		.historical-table {
			margin-top: 16px;
			 width: 100%;
		}
	}
    .main{
		padding: 20px;
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
	.answer-span span{
		cursor: pointer;
		display: inline-block;
		font-size: 16px ;
		width: 80px;
		height: 40px;
		text-align: center;
		line-height: 40px;
		color:#666;
		background-color: #f3f3f3;	
		border-radius: 2px;
		box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
		
	}
	.answer-on{
		display: inline-block;
		font-size: 16px !important;
		font-weight: 550 !important;
		color:#000 !important;
		background-color: #fff!important;
		box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
	}
</style>

