<template>
  <div>
    <el-drawer
      title=""
      :visible.sync="dialogVisible"
      :modal="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
      :append-to-body="true"
      :wrapper-closable="false"
      size="1140px"
      :show-close="false"
      :withHeader="false"
      class="addWorkFlowDrawer"
      >
      <div class="knowledge-content">
        <div class="content-left">
          <div class="content-left-header">选择Agent</div>
          <div style="margin-bottom: 24px">
            <el-input
              placeholder="搜索应用名称"
              prefix-icon="el-icon-search"
              v-model="searchKeyWord"
              style="width: 100%"
              @input="handleSearch"
            >
            </el-input>
          </div>
        </div>
        <div class="content-right">
          <div class="content-right-header">
            <div class="flex-center">
              <div class="tabs">
                <div v-for="(tab, index) in tabsList" :key="index" class="tab-item" :class="{'active': tab.value === activeTab}" @click.stop="handleTabClick(tab.value)">{{ tab.name }}</div>
              </div>
            </div>
            <iconpark-icon name="close-line" size="24" @click.stop="cancelConfig"></iconpark-icon>
            
          </div>
          <div class="content-right-body" v-loading="loading">
            <div class="list-box" >
              <ul>
                <li
                  class="base-li flex-center just"
                  v-for="item in list"
                  :key="item.id"
                >
                  <div class="li-name flex-center" >
                    <img :src="item.facadeImageUrl || '@/assets/images/default-plugin.svg'" />
                  </div>
                  <div class="li-content">
                    <div class="li-title">{{ item.applicationName }}<span class="type-item">{{ getApplicationTypeLabel(item.type) }}</span></div>
                    <div class="li-introduce">{{ item.introduce }}</div>
                    <div class="li-content-bottom">
                      <span>更新时间：{{ item.updateTime || item.createTime}}</span>
                    </div>
                  </div>
                  <el-button
                    class="add-btn"
                    icon="el-icon-circle-plus-outline"
                    type="primary"
                    size="small"
                    @click="addItem(item)"
                    >添加</el-button
                  >
                </li>
              </ul>
            </div>
            <el-pagination
              style="margin-top: 20px; color: #272a31;text-align: right;"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="pageNo"
              :page-sizes="[10, 30, 50, 100, 200]"
              :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              :pager-count="5"
              background
            >
            </el-pagination>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
    
<script>
import {
  appList,
} from "@/api/app";
import { applicationTypes } from '@/utils/constants';

export default {
  data() {
    return {
      pageNo: 1,
      pageSize: 10,
      total: 0,
      list: [], //工作流 分页
      loading: false,
      activeId: "",
      searchKeyWord: "",
      tabsList: [
        {
          name: "全部",
          value: "qa,dialogue",
        },
        {
          name: "AI客服",
          value: "qa",
        },
        {
          name: "对话流",
          value: "dialogue",
        },
      ],
      activeTab: 'qa,dialogue',
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
  },
  mounted() {
    this.getAgentList();
    this.activeId = "";
  },
  methods: {
    getApplicationTypeLabel(value) {
      return applicationTypes.find(item => item.value === value)?.label || ""
    },
    getAgentList() {
      this.loading = true;
     
      appList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        order: "",
        sort: "",
        applicationName: this.searchKeyWord,
        type: this.activeTab,
      }).then((res) => {
        if (res.code == "000000") {
          this.list = res.data?.records || [];
          this.total = res.data.totalRow || 0;
        } else {
          this.list = [];
        }
        this.loading = false;
      });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getAgentList();
    },
    handleCurrentChange(val) {
      this.pageNo = val;
      this.getAgentList();
    },
    handleSearch() {
      this.pageNo = 1;
      this.getAgentList();
    },
    // 添加知识库
    addItem(data) {
      this.activeId = data.id;
      this.setConfig()
    },
    setConfig() {
      if(this.activeId) {
        const data = this.list.find(item => item.id == this.activeId);
        this.$emit("selectAgentEmit", data);
      }
      this.$emit("clickConfig", false);
      this.$EventBus.$emit("saveWorkflow");
    },
    cancelConfig() {
      this.activeId = "";
      this.$emit("clickConfig", false);
    },
    handleTabClick(tab) {
      this.activeTab = tab;
      this.getAgentList();
    },
  },
};
</script>
    
<style lang="scss" scoped>
  .addWorkFlowDrawer {
    ::v-deep .el-drawer {
      background: #FFFFFF;
      box-shadow: 0px 1px 4px 4px rgba(0,0,0,0.05);
      border-radius: 4px 0px 0px 4px;
      .el-drawe__body {
        padding: 0px;
      }
    }
    .knowledge-content {
      height: 100%;
      width: 100%;
      display: flex;
      overflow: hidden;
      .content-left {
        width: 224px;
        background: #F7F8FA;
        padding: 32px 24px;
        height: 100%;
        box-sizing: border-box;
        font-family: MiSans, MiSans;
        .content-left-header {
          font-weight: 500;
          font-size: 20px;
          color: #494E57;
          line-height: 32px;
          margin-bottom: 24px;
        }
      }
      .content-right {
        flex: 1;
        height: 100%;
        padding: 28px 0px 24px;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        .content-right-header {
          width: 100%;
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 0 32px;
          font-family: MiSans, MiSans;
          margin-bottom: 16px;
          cursor: pointer;
          .tabs {
            display: flex;
            font-weight: 400;
            font-size: 18px;
            color: #828894;
            line-height: 28px;
            .tab-item {
              padding: 6px 0px;
              margin-right: 16px;
              cursor: pointer;
              &.active {
                font-weight: 500;
                font-size: 18px;
                color: #603ECA;
                position: relative;
                &:after {
                  content: "";
                  display: block;
                  width: 20px;
                  height: 3px;
                  background: #603ECA;
                  border-radius: 2px;
                  position: absolute;
                  margin-left: -10px;
                  left: 50%;
                  bottom: 0px;
                }
              }
            }
          }
        }
        .content-right-body { 
          flex: 1;
          overflow: hidden;
          display: flex;
          flex-direction: column;
          .list-box {
            flex: 1;
            overflow-y: auto;
            padding: 0px 32px;
          }
        }
      }
    }
  }
  
  .base-li {
    background: #ffffff;
    border-radius: 2px;
    border: 1px solid #D5D8DE;
    padding: 16px;
    margin-bottom: 12px;
    cursor: pointer;
  
    .li-name {
      font-weight: 400;
      font-size: 14px;
      color: #383d47;
      text-align: left;
      font-style: normal;
      cursor: pointer;
  
      > img {
        width: 36px;
        height: 36px;
        border-radius: 2px;
        margin-right: 16px;
      }
    }
    .li-content {
      flex: 1;
      overflow: hidden;
      cursor: pointer;
      .li-title {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 14px;
        color: #494E57;
        line-height: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-bottom: 4px;
      }
      .li-introduce {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #828894;
        line-height: 16px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-bottom: 8px;
      }
      .li-content-bottom {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #828894;
        line-height: 20px;
        > span {
          margin-right: 12px;
        }
        .li-num {
          display: inline-block;
          line-height: 20px;
          padding: 0 4px;
          background: #EBEEF2;
          border-radius: 2px;
          color: #494E57;
        }
      }
    }
    .add-btn {
      display: block;
    }
    .limit-btn{
      color: #999;
    }
    /* &:hover {
      background: #F2F4F7;
      .add-btn {
        display: block;
      }
    } */
  }
  
  .flex-center {
    display: flex;
    align-items: center;
  }
  
  .just {
    justify-content: space-between;
  }
  
  ::-webkit-scrollbar {
    display: none;
  }
  .type-item{
    display: inline-block;
    height: 24px;
    background: #ebeef2;
    border-radius: 2px;
    padding: 0px 8px;
    line-height: 24px;
    font-size: 12px;
    color: #494e57;
    margin:  0 0 0 10px;
}
</style>
    