<template>
  <div class="department-overview" v-loading="loading">
    <div class="department-overview-two" v-if="isFlag">
      <div class="department-overview-two-header">
        <div>
          <span style="font-size: 18px; font-weight: 600;">知识维护数排名</span>
          <iconpark-icon name="question-line"></iconpark-icon>
        </div>
        <div class="flex">
          <!-- <div style="margin-right: 13px;">
           <el-select style="width: 120px;" size="small" placeholder="请选择">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </div> -->
          <div>
            <el-select v-model="KnowledgeValue" @change="KnowledgeChange" style="width: 120px;" size="small" placeholder="请选择">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </div>
        </div>
      </div>
      <div class="department-overview-two-content">
        <div
          class="department-overview-two-content-item"
          v-for="(item, index) in getDeptKnowledgeDataObj.deptDataList"
          :key="index"
		  @click="overClick(item,index)"
        >
          <div class="department-overview-two-content-item-left">
            <span
              class="department-overview-two-content-item-sort"
              :style="{
                backgroundImage: `url(${judgeBackgroundIcon(index + 1)})`,
              }"
              >{{ index + 1 }}</span
            >
            <span
              class="department-overview-two-content-item-title"
              :title="item.deptName"
              :style="{ maxWidth: '380px' }"
              >{{ item.deptName }}</span
            >
            <i class="el-icon-arrow-right"></i>
          </div>
          <div>
            <span
              class="department-overview-two-content-item-number"
              :class="judgeNumberColor(index + 1)"
              >{{ KnowledgeValue == 'allCount'?item.allCount:KnowledgeValue == 'wddCount'?item.wddCount:KnowledgeValue == 'wjCount'?item.wjCount:KnowledgeValue == 'urlCount'?item.urlCount:KnowledgeValue == 'jghCount'?item.jghCount:item.allCount }}</span
            >
          </div>
        </div>
      </div>
    </div>
	<div class="department-overview-two" v-else>
	  <div class="department-overview-two-header">
	    <div>
		  <img
		    style="cursor: pointer;width: 18px;height: 18px;margin-right: 10px;"
		    src="@/assets/images/arrow-go-back-fill.svg"
		    @click="isFlag = true"
		  />
	      <span style="font-size: 18px; font-weight: 600;">部门详细</span>
	      
	    </div>
	    <!-- <div class="flex">
	      <div>
	        <el-select v-model="KnowledgeValue" @change="KnowledgeChange" style="width: 120px;" size="small" placeholder="请选择">
	          <el-option
	            v-for="item in options"
	            :key="item.value"
	            :label="item.label"
	            :value="item.value"
	          >
	          </el-option>
	        </el-select>
	      </div>
	    </div> -->
	  </div>
	  <div class="department-overview-two-content">
		<div v-if="qnAOverviewListchildrenData!='null'">
			<div
			  class="department-overview-two-content-item"
			  v-for="(item, index) in qnAOverviewListchildrenData"
			  :key="index"
			>
			  <div class="department-overview-two-content-item-left">
				<span
				  class="department-overview-two-content-item-sort"
				  :style="{
					backgroundImage: `url(${judgeBackgroundIcon(index + 1)})`,
				  }"
				  >{{ index + 1 }}</span
				>
				<span
				  class="department-overview-two-content-item-title department-overview-two-content-item-title-wd"
				  :title="item.deptName"
				  :style="{ maxWidth: '110px' }"
				  >{{ item.deptName }}</span
				>
			   
			  </div>
			  <div class="progress-bar">
				<el-progress :percentage="item.allCount/qnAOverviewListchildrenData[0].allCount*100" :show-text="false" :stroke-width="14" class="gradient-progress"></el-progress>
			  </div>
			  <div>
				<span
				  class="department-overview-two-content-item-number"
				 
				  >{{ KnowledgeValue == 'allCount'?item.allCount:KnowledgeValue == 'wddCount'?item.wddCount:KnowledgeValue == 'wjCount'?item.wjCount:KnowledgeValue == 'urlCount'?item.urlCount:KnowledgeValue == 'jghCount'?item.jghCount:item.allCount }}</span
				>
			  </div>
			</div>
		</div>
		 <div style="text-align: center;" v-if="qnAOverviewListchildrenData==null||qnAOverviewListchildrenData.length==0">暂无数据</div>
	  </div>
	</div>
  </div>
</template>
<script>

import { questionChartsTop } from "@/api/operation";
export default {
  name: "DepartmentOverview",
  mounted() {
   
  },
  props: {
  	getDeptKnowledgeDataObj: {
  		type: Object,
  		default: () => {
  		  return {}
  		}
  	},
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      options: [{
		  label:'全部类型',
		  value:'allCount'
	  },{
		  label:'QA对',
		  value:'wddCount'
	  },{
		  label:'文档数据',
		  value:'wjCount'
	  },{
		  label:'URL',
		  value:'urlCount'
	  },{
		  label:'结构化数据',
		  value:'jghCount'
	  }],
	  KnowledgeValue:'allCount',
      listType: "count",
	  isFlag:true,
      qnAOverviewListData: [],
	  qnAOverviewListchildrenData: [],
      iconBackgroundOne: require("@/assets/images/qnAOverviewSort1.png"),
      iconBackgroundTwo: require("@/assets/images/qnAOverviewSort2.png"),
      iconBackgroundThree: require("@/assets/images/qnAOverviewSort3.png"),
      iconBackgroundOther: require("@/assets/images/qnAOverviewSortOther.png"),
    };
  },
  // watch: {
  //   loading(val) {
  // 		if(!val){
  // 			this.qnAOverviewListData = this.getDeptKnowledgeDataObj.deptDataList
  // 		}      
  //   },
  // },
  
  methods: {
	overClick(item,index){
		this.isFlag = false
		console.log('item.children',item)
		this.qnAOverviewListchildrenData = item.children
	},
    KnowledgeChange(e){
		console.log('e',e)
		this.KnowledgeValue = e
	},
    judgeBackgroundIcon(sort) {
      if (sort === 1) {
        return this.iconBackgroundOne;
      } else if (sort === 2) {
        return this.iconBackgroundTwo;
      } else if (sort === 3) {
        return this.iconBackgroundThree;
      } else {
        return this.iconBackgroundOther;
      }
    },
    judgeNumberColor(sort) {
      if (sort === 1) {
        return "department-overview-two-content-item-number-red";
      } else if (sort === 2) {
        return "department-overview-two-content-item-number-yellow";
      } else if (sort === 3) {
        return "department-overview-two-content-item-number-blue";
      } else {
        return "";
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.department-overview {
  width: 96;
  box-sizing: border-box;
  // padding-top: 20px;
  height: 100%;
  background-color: #fff;
  margin: 0 9px;
  border-radius: 4px;

  span {
    display: inline-block;
  }
  .department-overview-two {
    width: 100%;
    .department-overview-two-header {
      height: 63px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      // margin-bottom: 15px;
      padding: 0px 24px;
      .el-dropdown-link {
        cursor: pointer;
        color: #409eff;
      }
      .el-icon-arrow-down {
        font-size: 12px;
      }
    }
	.progress-bar {
	  width: 260px;
	  margin: 10px 0 0 10px;
	  float: left;
	}
	.gradient-progress {
	  ::v-deep .el-progress-bar__inner {
	    background: linear-gradient(to right, #6890fb, #44d0e5);
	    opacity: 0.8;
	  }
	}
    .department-overview-two-content {
      height: 60vh;
      overflow-y: auto;
      overflow-x: hidden;
      .department-overview-two-content-item {
        padding: 0px 15px;
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: 56px;
        border-top: 1px solid #d5d8de;
        color: #494e57;
        cursor: pointer;
        user-select: none;
        .department-overview-two-content-item-left {
          display: flex;
          align-items: center;
        }
        .department-overview-two-content-item-sort {
          background-size: 100% 100%;
          width: 24px;
          height: 24px;
          margin-right: 16px;
          text-align: center;
          line-height: 24px;
          font-size: 12px;
          color: #fff;
        }
        .department-overview-two-content-item-title {
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
		  display: block;
        }
		.department-overview-two-content-item-title-wd{
			width: 110px;
		}
        .department-overview-two-content-item-number {
          font-weight: 600;
          font-size: 18px;
        }
        .department-overview-two-content-item-number-red {
          color: #ff4d4f;
        }
        .department-overview-two-content-item-number-yellow {
          color: #ffc107;
        }
        .department-overview-two-content-item-number-blue {
          color: #1747e5;
        }
      }
    }
  }
}
</style>
