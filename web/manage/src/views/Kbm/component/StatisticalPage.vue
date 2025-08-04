<template>
  <div class="page-wrap">
    <div class="page-header">
      <el-row
        class="header-wrap"
        type="flex"
        justify="space-between"
        align="middle"
      >
        <el-col style="font-size: 18px;display: flex;align-items: center;" :span="6">
          <img
            style="cursor: pointer;width: 18px;height: 18px;margin-right: 10px;"
            src="@/assets/images/arrow-go-back-fill.svg"
            @click="closePage"
          />
          {{ $t("statisticalAnalysis") }}
        </el-col>
        <el-col>
          <el-row type="flex" justify="end" align="middle">
            <el-form
              class="flex demo-form-inline"
              size="small"
              :inline="true"
              ref="searchForm"
            >
              <el-form-item :label="$t('kbm')">
                <el-select v-model="knowledgeName" filterable @change="KnowledgeChange" style="width: 248px" placeholder="请选择">
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
				  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('statisticsRange')">
                <el-date-picker
                  clearable
                  type="daterange"
				  v-model="dateRange"
                  :range-separator="$t('to')"
                  :start-placeholder="$t('startDate')"
                  value-format="yyyy-MM-dd"
                  style="width: 248px"
                  :end-placeholder="$t('endDate')"
                ></el-date-picker>
              </el-form-item>
            </el-form>
            <el-button type="primary" @click="addSearch" style="margin: 0 10px">{{
              $t("search")
            }}</el-button>
            <!-- <el-button icon="el-icon-download">{{
              $t("exportReport")
            }}</el-button> -->
          </el-row>
        </el-col>
      </el-row>
    </div>
    <div class="page-content">
      <div class="left-content">
        <div class="card-list">
          <div style="margin-right: 16px" class="card-list-div">
            <StatisticalOperation ref="childRef" type="1" :loading="loading1" :knowledgeCountInfoObj="knowledgeCountInfoObj"
              style="background: #d4dbff"
            ></StatisticalOperation>
          </div>
          <div class="card-list-div">
            <StatisticalOperation
              style="background: #c3deff" type="2" :loading="loading2" :applicationCountInfoObj="applicationCountInfoObj"
            ></StatisticalOperation>
          </div>
        </div>
        <div class="page-content-left-three-line">
          <div class="page-content-left-three">
            <StatisticalLineChart :loading="loading3" :xAxisData="xAxisData" :yAxisData="yAxisData" />
          </div>
        </div>
        <div class="flex page-content-left-three-pie">
          <div style="margin-right: 16px" class="card-list-div">
            <StatisticalPieChart :loading="loading1" type="1" :knowledgeCountInfoObj="knowledgeCountInfoObj"></StatisticalPieChart>
          </div>
          <div class="card-list-div">
            <StatisticalPieChart :loading="loading2" type="2" :applicationCountInfoObj="applicationCountInfoObj"></StatisticalPieChart>
          </div>
        </div>
      </div>
      <div class="page-content-right">
        <div>
          <DepartmentTotalCount :getDeptKnowledgeDataObj="getDeptKnowledgeDataObj" />
        </div>
        <div style="height: 80%;">
          <DepartmentOverview :getDeptKnowledgeDataObj="getDeptKnowledgeDataObj" :loading="loading4" />
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import StatisticalOperation from "./StatisticalOperation.vue";
import StatisticalLineChart from "./StatisticalLineChart.vue";
import StatisticalPieChart from "./StatisticalPieChart.vue";
import DepartmentOverview from "./DepartmentOverview.vue";
// import DepartmentOverviewChild from "./DepartmentOverviewChild.vue";
import DepartmentTotalCount from "./DepartmentTotalCount.vue";
import {  knowledgeCountInfo , applicationCountInfo , knowledgeCountTrend , getDeptKnowledgeData ,getKnowledgeList } from "@/api/operation";
export default {
  components: {
    StatisticalOperation,
    StatisticalLineChart,
    StatisticalPieChart,
    DepartmentOverview,
    DepartmentTotalCount
  },
  data() {
    return {
		loading1:false,
		loading2:false,
		loading3:false,
		loading4:false,
		overType:1,
		type:1,
		dateRange:[],
		options:[],
		knowledgeName:'',
		startTime: '',
		endTime: '',
		xAxisData:[],
		yAxisData:[],
		applicationCountInfoObj:{},
		knowledgeCountInfoObj:{},
		getDeptKnowledgeDataObj:{},
	};
  },
  mounted() {
    this.getKnowledgeCountInfo()
	this.getApplicationCountInfo()
	this.getDeptKnowledgeDatas()
	this.getTokenConsumption()
	this.getKnowledgeLists()
  },
  methods: {
	  //查询事件
	addSearch(){
		if(this.dateRange.length>0){
			this.startTime = this.dateRange[0] + ' 00:00:00'
			this.endTime = this.dateRange[1] + ' 23:59:59'
		}
		this.getKnowledgeCountInfo()
		this.getApplicationCountInfo()
		this.getDeptKnowledgeDatas()
		this.getTokenConsumption()
	},
	//知识库下拉
	KnowledgeChange(e){
		this.knowledgeName = e
	},
	//知识库数据总量
	getKnowledgeCountInfo() {
	    this.loading1 = true;
	  	knowledgeCountInfo({
	  	  startTime: this.startTime,
	  	  endTime: this.endTime,
		  knowledgeId:this.knowledgeName
	  	}).then((data) => {  	   
	  	    if (data.code == "000000") {
	  	        this.knowledgeCountInfoObj = data.data
	  	    }
	  	}).finally(() => {
	      this.loading1 = false;
	    }); 
	},
	//关联应用数总量
	getApplicationCountInfo() {
		this.loading2 = true;
		applicationCountInfo({
		  startTime: this.startTime,
		  endTime: this.endTime,
		  knowledgeId:this.knowledgeName
		}).then((data) => {	  	    
			if (data.code == "000000") {
				this.applicationCountInfoObj = data.data
			}
		}).finally(() => {
		  this.loading2 = false;
		});
	},
	//部门课室数
	getDeptKnowledgeDatas(){
		this.loading4 = true;
		getDeptKnowledgeData({
		  startTime: this.startTime,
		  endTime: this.endTime,
		  knowledgeId:this.knowledgeName
		}).then((data) => {	  	    
			if (data.code == "000000") {
				this.getDeptKnowledgeDataObj = data.data
			}
		}).finally(() => {
		  this.loading4 = false;
		});
	},
	//折线图
	getTokenConsumption() {
	  this.loading3 = true;
	  knowledgeCountTrend({
		  startTime: this.startTime,
		  endTime: this.endTime,
		  knowledgeId:this.knowledgeName
	  })
	    .then((data) => {
	      if (data.code == "000000") {
	        this.xAxisData = data.data.map((item) => item.date);
	        this.yAxisData = data.data.map((item) => item.totalCount);
	      }
	    })
	    .finally(() => {
	      this.loading3 = false;
	    });
	},
	// 知识库列表
	getKnowledgeLists(){
		getKnowledgeList({})
	  .then((data) => {
		if (data.code == "000000") {
		    data.data.forEach(item => {
			  this.options.push({
				label: item.knowledgeName,
				value: item.knowledgeId
			  });
			});
		}
	  })
	},
    closePage() {
      this.$emit("close", false);
    }
  },
};
</script>
<style lang="scss" scoped>
.page-wrap {
  width: 100%;
  height: 100%;
  background-color: #f7f8fa;
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
  .page-content {
	width: 100%;
    height: calc(100% - 72px);
    padding: 24px 32px;
    display: flex;
	justify-content: space-between;
    .left-content {
      width: 67%;
	  height: 100%;
    }
    .card-list {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
	  width: 100%;
	  
    }
	.card-list-div{
		width: 50%;
	}
    .page-content-right {
      background: linear-gradient(135deg, #6df6d0 0%, #b9d9fa 100%),
        radial-gradient(
          42% 75% at 100% 0%,
          rgba(0, 206, 255, 0.51) 0%,
          rgba(162, 214, 255, 0) 100%
        );
      border-radius: 4px;
      width: 30%;
      height: 100%;
    }
	.page-content-left-three-line{
		margin-bottom: 16px;
		height: calc(50% - 120px);
	}
	.page-content-left-three-pie{
		width: 100%;
		height: calc(50% - 120px);
	}
	.page-content-left-three{
		height: 100%;
	}
  }
}
::v-deep .el-date-editor .el-range-separator {
  width: 8%;
}
</style>
