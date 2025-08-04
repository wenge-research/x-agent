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
          <div class="content-left-header">选择插件</div>
          <div style="margin-bottom: 24px">
            <el-input
              placeholder="搜索插件名"
              v-model="searchKeyWord"
              style="width: 100%"
              @input="handleSearch"
            >
            <template #prefix>
              <div class="search-icon">
                <iconpark-icon name="search-2-line" color="#1d2129" size="20"></iconpark-icon>
              </div>  
            </template>
            </el-input>
          </div>
          <div class="classify-ctn">
            <div class="classify-title">{{ $t("classification") }}</div>
            <div class="classify-list-ctn">
              <div class="classify-list" v-for="(item,index) in labelList" :key="index+'labelList'" v-show="index!=labelList.length-1"
              :style="{backgroundColor:index==labelIndex?'#E7ECFC':'transparent'}" @click="handleLabelClick(item,index)">
                <div class="classify-icon">
                  <iconpark-icon :name="getLabelIcon(item.name)" :color="index==labelIndex?'#1747E5 ':'#1d2129'" size="16"></iconpark-icon>
                </div>
                <div class="classify-word" :style="{color:index==labelIndex?'#1747E5 ':'#1d2129'}">
                  {{ item.name }}
                </div>
              </div>
            </div>
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
              <el-checkbox v-model="showAdd" @change="checkboxChange" style="line-height: 32px;" v-if="activeTab!='user'">{{ "仅查看官方" }}</el-checkbox>
            </div>
            <iconpark-icon name="close-line" size="24" @click.stop="cancelConfig"></iconpark-icon>
          </div>
          <div class="content-right-body" v-loading="loading" v-if="list.length">
            <div class="list-box" >
              <ul>
                <li
                  class="base-li flex-center just"
                  v-for="item in list"
                  :key="item.componentId"
                >
                  <div class="li-name flex-center" >
                    <img :src="item.icon || '@/assets/images/default-plugin.svg'" />
                  </div>
                  <div class="li-content">
                    <div class="li-title">
                      {{ item.componentName }}
                      <div class="official-logo" v-if="item.ownerType=='official'">官方</div>
                      <div class="preset-logo" v-if="!item.isMe&&item.granted">授权</div>
                    </div>
                    <div class="li-introduce">{{ item.componentDesc }}</div>
                    <div class="li-content-bottom">
                      <span>{{sortFeild === 'update_time' ? '更新时间' : '创建时间'}}：{{ item.updateTime || item.createTime}}</span>
                    </div>
                  </div>
                  <el-button
                    v-if="item.status == 1"
                    class="add-btn"
                    type="primary"
                    size="small"
                    plain
                    @click.stop="addItem(item)"
                    ><div class="btn-add"> <iconpark-icon name="add-line" color="#1747E5" size="16"></iconpark-icon><span>添加</span></div></el-button
                  >
                  <span
                    v-else
                    class="limit-btn"
                    type="text"
                    size="small"
                    >未启用</span>
                </li>
              </ul>
            </div>
            <!-- <el-pagination
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
              </el-pagination> -->
          </div>
          <el-row v-loading="loading" style="height: 100%;" v-else>
            <div class="empty-data-ctn">
              <div class="empty-data">
                <img src="@/assets/images/empty_data.png" alt="">
                <p>暂无插件</p>
              </div>
            </div>
          </el-row>
          
        </div>
  
      </div>
    </el-drawer>
  </div>
</template>
    
<script>
import { pluginList } from "@/api/workflow";
import {apiGetLabelTypes} from "@/api/app"
export default {
  data() {
    return {
      pageNo: 1,
      pageSize: 9999,
      total: 0,
      list: [], //工作流 分页
      loading: false,
      activeId: "",
      searchKeyWord: "",
      tabsList: [
        {
          name: "插件商店",
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
      label:"",
      labelIndex:0,
      labelList:[],
      showAdd:false,
      labelLogoList:[
        {
          name:"",
          icon:"apps-fill"
        },
        {
          name:"新闻阅读",
          icon:"pages-line"
        },
        {
          name:"实用工具",
          icon:"tools-line"
        },
        {
          name:"便利生活",
          icon:"shopping-bag-4-line"
        },
        {
          name:"金融与商业",
          icon:"percent-line"
        },
        {
          name:"网页搜索",
          icon:"search-line"
        },
        {
          name:"图像",
          icon:"image-circle-line"
        },
        {
          name:"其他",
          icon:""
        }
      ]
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
  },
  computed:{
    getLabelIcon(){
      return (data)=>{
        if(data=="全部") return "apps-fill"
        return this.labelLogoList.find(item=>item.name==data).icon
      }
    },
    // filterList(){
    //   return this.list.filter(item=>this.activeTab!='user' || item.isMe || !item.isMe&&item.granted) || []
    // }
  },
  mounted() {
    this.getPluginListList();
    this.getPluginStoreList()
    this.activeId = "";
  },
  methods: {
    getPluginListList() {
      this.loading = true;
      const userInfo = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : null;
      const accountName = userInfo ? userInfo.accountName : '';
      // const ownerType=this.showAdd?'official':this.activeTab == 'user'?'personalGrant':'all'
      const ownerType=this.activeTab == 'user'?'personalGrant':this.showAdd?'official':'all'
      pluginList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        componentName: this.searchKeyWord,
        order: this.sortFeild, 
        sort: 'desc',
        // type:2,
        // createUser: this.activeTab == 'user' ? accountName : '',
        publishAppStore: this.activeTab == 'user' ? "" : '1',
        labels: this.label == "全部" ? "" : this.label,
        ownerType
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
    getPluginStoreList(){
      apiGetLabelTypes({type:1}).then(res=>{
        if (res.code == "000000") {
          this.labelList = res.data || [];
        } else {
          this.labelList = [];
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getPluginListList();
    },
    handleCurrentChange(val) {
      this.pageNo = val;
      this.getPluginListList();
    },
    handleSearch() {
      this.pageNo = 1;
      this.getPluginListList();
    },
    // 添加知识库
    addItem(data) {
      console.log(data);
      
      this.activeId = data.componentId;
      this.setConfig()
    },
    setConfig() {
      if(this.activeId) {
        const data = this.list.find(item => item.componentId == this.activeId);
        this.$emit("selectToolEmit", data);
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
      if(this.activeTab=='user'){
        this.label=''
        this.labelIndex=0
      }
      this.getPluginListList();
    },
    handleLabelClick(item,index){
      this.label=item.name
      this.labelIndex=index
      this.getPluginListList()
    },
    checkboxChange(){
      this.getPluginListList()
    }
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

        .el-input__inner{
          border-radius: 8px;
        }

        .search-icon{
          width: 20px;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .classify-ctn{
          margin-top: 16px;
          width: 100%;

          .classify-title{
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 14px;
            color: #86909C;
            line-height: 20px;
          }

          .classify-list-ctn{
            width: 100%;
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin-top: 12px;

            .classify-list{
              width: 100%;
              height: 40px;
              padding: 10px;
              box-sizing: border-box;
              display: flex;
              align-items: center;
              gap: 8px;
              cursor: pointer;
              border-radius: 4px;

              .classify-icon{
                width: 20px;
                height: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
              }

              .classify-word{
                height: 20px;
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 14px;
                color: #1D2129;
                line-height: 20px;
              }
            }

          }
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

        .official-logo{
          width: 32px;
          height: 20px;
          background: linear-gradient( 270deg, #8E65FF33 0%, #1747E533 100%);
          border-radius: 2px;
          text-align: center;
          line-height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          color: #1747E5;
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
</style>
    