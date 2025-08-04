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
          <div class="content-left-header">关联工作流</div>
          <div style="margin-bottom: 24px">
            <el-input
              placeholder="搜索工作流名称"
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
              <el-select v-model="sortFeild" style="width: 140px;margin-right: 16px;" @change="handleSearch">
                <el-option
                  v-for="item in sortList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </div>
            <iconpark-icon name="close-line" size="24" @click.stop="cancelConfig"></iconpark-icon>
            
          </div>
          <div class="content-right-body" v-loading="loading" v-if="list.length">
            <div class="list-box" >
              <ul>
                <li
                  class="base-li flex-center just"
                  v-for="item in list"
                  @click.stop="detailHandler(item.componentId)"
                  :key="item.componentId"
                >

                  <div class="li-name flex-center" >
                    <img :src="item.icon || '@/assets/images/appManagement/workflow.svg'" />
                  </div>
                  <div class="li-content">
                    <div class="li-title">
                      {{ item.componentName }}
                      <div class="theofficial" v-if="item.ownerType=='official'">官方</div>
                      <div class="preset-logo" v-if="!item.isMe&&item.granted">授权</div>
                    </div>
                    <div class="li-introduce">{{ item.componentDesc }}</div>
                    <div class="li-content-bottom">
                      <!-- <span class="li-num">87个文件</span> -->
                      <span>{{sortFeild === 'update_time' ? '更新时间' : '创建时间'}}：{{ item.updateTime || item.createTime}}</span>
                    </div>
                  </div>
                  <el-button
                    :disabled="componentId === item.componentId"
                    class="add-btn"
                    plain
                    type="primary"
                    size="small"
                    @click.stop="addItem(item)"
                    ><div class="btn-add"><iconpark-icon name="add-line" color="#1747E5" size="16"></iconpark-icon><span>添加</span></div></el-button
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
          <el-row v-loading="loading" style="height: 100%;" v-else>
            <div class="empty-data-ctn">
              <div class="empty-data">
                <img src="@/assets/images/empty_data.png" alt="">
                <p>暂无工作流</p>
              </div>
            </div>
          </el-row>
        </div>
  
      </div>
      </el-drawer>
  </div>
  </template>
    
  <script>
  import { appList } from "@/api/workflow";
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
            name: "推荐",
            value: "",
          },
          {
            name: "我的",
            value: "user",
          },
        ],
        activeTab: '',
        sortFeild: "update_time",
        sortList: [
          {
            value: "update_time",
            label: "最近更新"
          },
          {
            value: "create_time",
            label: "创建时间"
          }
        ],
      };
    },
    props: {
      dialogVisible: {
        type: Boolean,
        default: false,
      },
      componentId: {
        type: String,
      },
    },
    computed:{
      isAdmin(){
        return JSON.parse(sessionStorage.getItem('user')).powerType==0
      },
      // filterList(){
      //   return this.list.filter(item=>this.isAdmin || this.activeTab!='user' || item.isMe || !item.isMe&&item.granted) || []
      // }
    },
    mounted() {
      this.getWorkFlowList();
      this.activeId = "";
    },
    methods: {
      detailHandler(componentId) {   
          const routeUrl = window.location.origin + window.location.pathname + '#/workflow/workFlowEdit?componentId=' + componentId;
            window.open(routeUrl, '_blank');
      },
      getWorkFlowList() {
        this.loading = true;
        const userInfo = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : null;
        const accountName = userInfo ? userInfo.accountName : '';
        appList({
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          applicationName: this.searchKeyWord,
          order: this.sortFeild, 
          sort: 'desc',
          type:2,
          // createUser: this.activeTab == 'user' ? accountName : '',
          ownerType:this.activeTab == 'user' ? 'personalGrant' : 'official'
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
        this.getWorkFlowList();
      },
      handleCurrentChange(val) {
        this.pageNo = val;
        this.getWorkFlowList();
      },
      handleSearch() {
        this.pageNo = 1;
        this.getWorkFlowList();
      },
      // 添加知识库
      addItem(data) {
        this.activeId = data.componentId;
        this.setConfig()
      },
      setConfig() {
        if(this.activeId) {
          const data = this.list.find(item => item.componentId == this.activeId);
          this.$emit("selectIterationEmit", data);
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
        this.getWorkFlowList();
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

            .btn-add{
              display: flex;
              align-items: center;
              gap: 8px;

              span{
                display: inline-block;
                height: 16px;
                line-height: 16px;
              }
            }
          }
        }
        .empty-data-ctn{
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;

          .empty-data{
            width: 150px;

            img{
              width: 150px;
            }
            p{
              font-family: MiSans, MiSans;
              text-align: center;
              color: rgb(130, 136, 148);
            }
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
    ::v-deep .el-button--primary.is-plain {
        color: #1747E5 !important;
        background: #fff !important;
        border-color: #1747E5 !important;
        border-radius: 8px;
        // padding: 7px 16px;
    }
  
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
        display: flex;
        gap: 8px;
        .theofficial{
          width: 32px;
          height: 20px;
          background: linear-gradient( 270deg, #8E65FF33 0%, #1747E533 100%);
          border-radius: 2px;
          width: 32px;
          height: 20px;
          color: #1747E5;
          border-radius: 2px;
          text-align: center;
          font-size: 12px;
          font-family: MiSans, MiSans;
          font-weight: 400;
        }

        .preset-logo{
          width: 32px;
          height: 20px;
          background: #F2F3F5;
          border-radius: 2px;
          text-align: center;
          line-height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          color: #86909C;
        }
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
    &:hover {
      background: #F2F4F7;
    }
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
  </style>
    