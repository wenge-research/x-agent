<template>
  <div class="config-manage">
    <div class="inner" v-if="tabData.length>0">
      <div style="display: flex;margin-bottom: 20px;" >
        <div :class="type==item.type? 'on flex-tab':'flex-tab'" v-for="(item,index) in tabData" @click="addItem(item)" :key="index">
          <img src="@/assets/images/sql.png"  style="width:20px;margin-right: 10px;"/>
           {{item.typeName}}     
        </div>
      </div>
      <div v-loading="loading" v-if="type==1">
        <div style="margin-top: 16px">
          <el-table
            
            ref="multipleTable"
            :data="tableData1"
            tooltip-effect="dark"
            style="width: 100%"
            class="table"
            max-height="590"
          >
            <el-table-column
              type="index"
              width="60"
              :label="$t('sequenceNumber')"
            ></el-table-column>
            <el-table-column prop="createTime" label="提交时间"></el-table-column>
            <el-table-column prop="investorName" label="投资方名称"></el-table-column>
            <el-table-column prop="investmentIntentOverview" label="投资意向概况">
				 <template #default="scope">
					 
					<p class="showOverTooltip" :title="scope.row.investmentIntentOverview">{{scope.row.investmentIntentOverview}}</p >
					 
				</template>
								
			</el-table-column>
			<el-table-column prop="plannedTotalInvestment" label="计划总投资"></el-table-column>
			<el-table-column prop="investmentContactPerson" label="投资联系人"></el-table-column>
			<el-table-column prop="contactInformation" label="联系方式"></el-table-column>
            <el-table-column :label="$t('operation')" width="120">
              <template slot-scope="scope">
                <el-button type="text" @click="addQaVisibleTrue(scope.row)"
                  >详情</el-button
                >
                <el-button type="text" @click="deleteHandler1(scope.row)"
                  >{{$t('delete')}}</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div style="text-align: right; margin-top: 20px">
          <el-pagination
            background
            layout="total, prev, pager, next, sizes, jumper"
            popper-class="slectStyle"
            :current-page="currentPage1"
            :total="total1"
            :page-size="pageSize"
            @current-change="handleCurrentChange1"
            @size-change="handleSizeChange1"
            :page-sizes="[10, 20, 30, 40]"
          ></el-pagination>
        </div>
      </div>
	  <div v-loading="loading" v-else>
	    <div style="margin-top: 16px">
	      <el-table
	        size="small"
	        ref="multipleTable"
	        :data="tableData2"
	        tooltip-effect="dark"
	        style="width: 100%"
	        class="table"
	        max-height="590"
	      >
	        <el-table-column
	          type="index"
	          width="60"
	          :label="$t('sequenceNumber')"
	        ></el-table-column>
			 <el-table-column prop="createTime" label="提交时间"></el-table-column>
	        <el-table-column prop="name" label="姓名"></el-table-column>
	        <el-table-column prop="companyName" label="公司名称"></el-table-column>
			<el-table-column prop="contactInformation" label="联系方式"></el-table-column>
	        <el-table-column prop="content" label="咨询内容">
				<template #default="scope">
					 
					<p class="showOverTooltip" :title="scope.row.content">{{scope.row.content}}</p >
					 
				</template>
			</el-table-column>
	  			
	        <el-table-column :label="$t('operation')" width="120">
	          <template slot-scope="scope">
	            <el-button type="text" @click="addQaVisibleTrue(scope.row)"
	              >详情</el-button
	            >
	            <el-button type="text" @click="deleteHandler2(scope.row)"
	              >{{$t('delete')}}</el-button
	            >
	          </template>
	        </el-table-column>
	      </el-table>
	    </div>
	    <div style="text-align: right; margin-top: 20px">
	      <el-pagination
	        background
	        layout="total, prev, pager, next, sizes, jumper"
	        popper-class="slectStyle"
	        :current-page="currentPage2"
	        :total="total2"
	        :page-size="pageSize"
	        @current-change="handleCurrentChange2"
	        @size-change="handleSizeChange2"
	        :page-sizes="[10, 20, 30, 40]"
	      ></el-pagination>
	    </div>
	  </div>
    </div>
    <div style="text-align: center;margin-top: 60px;">
		<img src="@/assets/images/no-data.png" style="width: 150px; height: auto;">
		<div>暂无数据</div>
	</div>
	<el-drawer class="my-drawer" title="详情" :visible.sync="addQaVisible" :modal-append-to-body="false" :close-on-click-modal="false"
	   :close-on-press-escape="false" :wrapperClosable="false" size="29.2%" append-to-body>
	  <div slot class="qa-box">
	    <div class="content" v-if="type==1">
	        <div class="content-c6">提交时间</div>
			<div class="content-c3">{{QaVisiObj.createTime}}</div>
			<div class="content-c6">投资方名称</div>
			<div class="content-c3">{{QaVisiObj.investorName}}</div>
			<div class="content-c6">投资意向概况</div>
			<div class="content-c3">{{QaVisiObj.investmentIntentOverview}}</div>
			<div class="content-c6">计划总投资</div>
			<div class="content-c3">{{QaVisiObj.plannedTotalInvestment}}</div>
			<div class="content-c6">投资联系人</div>
			<div class="content-c3">{{QaVisiObj.investmentContactPerson}}</div>
			<div class="content-c6">联系方式</div>
			<div class="content-c3">{{QaVisiObj.contactInformation}}</div>
	    </div>
		<div class="content" v-else>
		    <div class="content-c6">提交时间</div>
			<div class="content-c3">{{QaVisiObj.createTime}}</div>
			<div class="content-c6">姓名</div>
			<div class="content-c3">{{QaVisiObj.name}}</div>
			<div class="content-c6">公司名称</div>
			<div class="content-c3">{{QaVisiObj.companyName}}</div>
			<div class="content-c6">联系方式</div>
			<div class="content-c3">{{QaVisiObj.contactInformation}}</div>
			<div class="content-c6">咨询内容</div>
			<div class="content-c3">{{QaVisiObj.content}}</div>
		</div>
	    <div class="footer">
	      <el-button @click.stop="addQaVisible = false">{{ $t('cancel') }}</el-button>
	      
	    </div>
	  </div>
	</el-drawer>
  </div>
</template>
<script>
import {
  deleteInvestmentLead,
  deleteLhmzMessage,
    getAllInvestmentLead,
	 getAllLhmzMessage,
	  getApplicationBusinessType,
} from "@/api/configManage";
export default {
  name: "ConfigManage",
  props: {
    data: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      searchForm: {
        keyword: "",
      },
	  QaVisiObj:{},
	  addQaVisible:false,
	  type:1,
      currentPage1: 1,
      total1: 0,
      tableData1: [],
	  currentPage2: 1,
	  total2: 0,
	  tableData2: [],
	  pageSize: 10,
      loading: false,
      addLoading: false,
      dialogTitle: "添加",
      dialogVisible: false,
      configForm: {
        keyInfo: "",
        valueInfo: "",
        remark: "",
      },
      tabData:[]
    };
  },
  created() {
	  console.log('data',this.data)
    this.ApigetApplicationConfigList();
	this.ApigetAllInvestmentLead();
  },
  methods: {
	  addQaVisibleTrue(data){
		  this.QaVisiObj = data
		  this.addQaVisible = true
	  },
	  addItem(item) {
		  this.type = item.type
		  if(this.type==1){
			  this.ApigetAllInvestmentLead();
		  }else{
			  this.ApigetAllLhmzMessage();
		  }
      
    },
	  //投资意向
    handleSizeChange1(val) {
      this.pageSize = val;
      this.ApigetAllInvestmentLead();
    },
    handleCurrentChange1(val) {
      this.currentPage1 = val;
      this.ApigetAllInvestmentLead();
    },
	async ApigetAllInvestmentLead() {
	  this.loading = true;
	  const params = {
	    pageNo: this.currentPage2,
	    pageSize: this.pageSize
	  };
	  try {
	    const res = await getAllInvestmentLead(params);
	    if (res.code == "000000") {
	      this.total1 = res.data?.totalRow || 0;
	      this.tableData1 = res.data?.records || [];
	    } else {
	      this.total1 = 0;
	      this.tableData1 = [];
	    }
	  } catch (error) {
	    this.loading = false;
	  }
	  this.loading = false;
	},
	//咨询
	handleSizeChange2(val) {
	  this.pageSize = val;
	  this.ApigetAllLhmzMessage();
	},
	handleCurrentChange2(val) {
	  this.currentPage2 = val;
	  this.ApigetAllLhmzMessage();
	},
	async ApigetAllLhmzMessage() {
	  this.loading = true;
	  const params = {
	    pageNo: this.currentPage1,
	    pageSize: this.pageSize
	  };
	  try {
	    const res = await getAllLhmzMessage(params);
	    if (res.code == "000000") {
	      this.total2 = res.data?.totalRow || 0;
	      this.tableData2 = res.data?.records || [];
	    } else {
	      this.total2 = 0;
	      this.tableData2 = [];
	    }
	  } catch (error) {
	    this.loading = false;
	  }
	  this.loading = false;
	},
   async ApigetApplicationConfigList() {
     this.loading = true;
     const params = {
        applicationId: this.data?.applicationId,
     };
     try {
       const res = await getApplicationBusinessType(params);
       if (res.code == "000000") {
         this.tabData = res.data
       } else {
         this.tabData = [];
       }
     } catch (error) {
       this.loading = false;
     }
     this.loading = false;
   },
    
   
    deleteHandler1(data) {
      this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => {
       
        try {
          const res = await deleteInvestmentLead([data.id]);
          if (res.code == "000000") {
            this.$message.success("删除成功");
            this.ApigetAllInvestmentLead();
          } else {
            this.$message.warning(res.msg);
          }
        } catch (error) {
          this.addLoading = false;
        }
      });
    },
	deleteHandler2(data) {
	  this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
	    confirmButtonText: this.$t('confirm'),
	    cancelButtonText: this.$t('cancel'),
	    confirmButtonClass: "confirm-ok",
	    cancelButtonClass: "confirm-cancel",
	  }).then(async () => {
	   
	    try {
	      const res = await deleteLhmzMessage([data.id]);
	      if (res.code == "000000") {
	        this.$message.success("删除成功");
	        this.ApigetAllLhmzMessage();
	      } else {
	        this.$message.warning(res.msg);
	      }
	    } catch (error) {
	      this.addLoading = false;
	    }
	  });
	},
  },
};
</script>
<style lang="scss">
/* 自定义整个滚动条 */
.checkboxOuter::-webkit-scrollbar {
  width: 8px;
  height: 121px;
  background: #fff;
}

/* 自定义滚动条轨道 */
.checkboxOuter::-webkit-scrollbar-track {
  background: #fff; /* 轨道颜色 */
}

/* 自定义滚动条的滑块（thumb） */
.checkboxOuter::-webkit-scrollbar-thumb {
  background: #e1e4eb; /* 滑块颜色 */
  border-radius: 6px;
}
.slectStyle {
  .el-select-dropdown__item.selected {
    color:  #1747E5;
  }
}
.config-manage-dialog {
  background: #fff !important;
  .el-dialog__header {
    padding: 32px 32px 16px;
    background: #fff !important;
    .el-dialog__title {
      height: 24px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
    }
    .el-dialog__headerbtn {
      top: 36px;
    }
  }
  .el-dialog__body {
    padding: 0 32px 32px !important;
    background: #fff !important;
    .el-input__inner {
      border-radius: 4px;
    }
    .el-input__inner:focus {
      border-color:  #1747E5;
    }
    .checkboxOuter {
      height: 410px;
      padding: 0 8px;
      overflow-y: scroll;
    }
    .el-form-item__label {
      color: #383d47;
      font-size: 16px;
      font-weight: 500;
    }
  }
  .dialog-footer {
    text-align: right;
    .el-button {
      border-radius: 4px;
    }
    .el-button--primary {
      background:  #1747E5;
      border-color:  #1747E5;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
}
.confirm-ok {
  background:  #1747E5 !important;
  border-color: transparent !important;
}
.confirm-cancel {
  &:hover {
    background: none !important;
    color:  #1747E5 !important;
    border-color:  #1747E5 !important;
  }
}
</style>
<style lang="scss" scoped>
.config-manage {
  width: 100%;
  height: 100%;
  font-family: MiSans, MiSans;
  .inner {
    width: 100%;
    height: calc(100% - 70px);
    background: #fff;
    border-radius: 4px;
    ::v-deep .el-input__inner:focus,
    ::v-deep .el-date-editor:focus,
    ::v-deep .el-date-editor.is-active {
      border-color:  #1747E5 !important;
    }
    ::v-deep .el-button {
      border-radius: 4px;
    }
  }
}
.el-button {
  &.el-button-default {
    border-radius: 4px;
    color: #383d47;
    border: 1px solid #c4c6cc;
  }

  &.el-button--primary {
    background-color:  #1747E5;
    color: #fff;
    border-color:  #1747E5;
  }
}

::v-deep .el-input__inner {
  border-radius: 8px !important;
}
::v-deep .el-textarea__inner {
  font-family: MiSans, MiSans;
  font-size: 14px;
  color: #606266;
  line-height: 20px;
}

::v-deep .el-date-editor .el-range-separator {
  width: 8%;
}

::v-deep .table {
  .is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%,  #1747E5 100%);
  }
  .el-switch .el-switch__core {
    border-radius: 12px;
    border: none;
    &:after {
      top: 2px;
    }
  }
  .has-gutter {
    th {
      font-size: 14px;
      color: #828894;
      background: #f2f5fa;
    }
  }

  tr {
    color: #383d47;
    font-size: 14px;
    font-weight: 400;
  }

  .el-button--text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color:  #1747E5;
  }
}

::v-deep .el-pager li {
  border: 1px solid #dddfe8;
  color: #272a31;
  background: #fff;
  .el-select-dropdown__item.selected {
    color:  #1747E5;
  }

  &:not(.disabled).active {
    background-color: #fff !important;
    border: 1px solid  #1747E5;
    color: #272a31 !important;
  }
}

::v-deep .btn-prev,
::v-deep .btn-next {
  border: 1px solid #dddfe8;
  color: #272a31;
  background: #fff;
}
.flex-tab
{
	display: flex; 
	align-items: center; 
	margin-right: 40px;
	height: 40px;
	width: 15%;
	padding-left: 20px;
	color: #333;
	border: 1px solid #e2e2e2;
	cursor: pointer;
}
.on{
	 border: 1px solid  #1747E5;
	 background-color: #f4f7ff;
}
.showOverTooltip{
    
    overflow: hidden;
    -webkit-line-clamp: 2;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
}
.my-drawer{
	.content{
		padding:0 20px;
		.content-c6{
			color: #999;
			margin-bottom: 14px;
		}
		.content-c3{
			color: #000;
			margin-bottom: 34px;
			line-height: 30px;
		}
	}
}
.footer{
	position: absolute;
	right: 20px;
	bottom: 40px;
}
.el-drawer__header{
	font-weight: 550 !important;
	color: #333 !important;
}
</style>
