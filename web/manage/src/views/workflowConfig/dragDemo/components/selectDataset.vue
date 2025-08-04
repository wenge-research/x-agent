<template>
  <div>
  <el-drawer
    title=""
    :visible.sync="dialogVisible"

    :modal-append-to-body="true"
    :close-on-click-modal="true"
    :append-to-body="true"
    :wrapper-closable="false"
    size="1140px"
    :show-close="false"
    :withHeader="false"
    class="knowledgeDrawer"
  >
  <div class="knowledge-content">
    <div class="content-left">
      <div class="content-left-header">选择数据库服务</div>
      <div style="margin-bottom: 24px">
        <el-input
          placeholder="输入数据库名称"
          prefix-icon="el-icon-search"
          v-model="searchKeyWord"
          style="width: 100%"
          @input="handleSearch"
        >
        </el-input>
      </div>
      <!-- <div class="">
        <el-button
          type="primary"
          icon="el-icon-circle-plus"
          style="width: 100%"
          @click="addKnowledge"
          >创建词库</el-button
        >
      </div> -->
    </div>
    <div class="content-right">
      <div class="content-right-header">
        <div class="flex-center">
          <div class="tabs">
            <div v-for="(tab, index) in tabsList" :key="index" class="tab-item" :class="{'active': tab.value === activeTab}" @click.stop="handleTabClick(tab.value)">{{ tab.name }}</div>
          </div>
        </div>
        <iconpark-icon name="close-line" size="24" @click.stop="cancelAss"></iconpark-icon>
        
      </div>
      <div class="content-right-body" v-loading="loading">
        <div class="list-box">
          <ul v-if="!showAdd">
            <li
              class="base-li flex-center just"
              v-for="item in datasetList"
              :key="item.id"
            >
              <div class="li-name flex-center" >
                <img src="@/assets/images/sjk-icon1.png" v-if="item.type==1"/>
                <img src="@/assets/images/sjk-icon2.png" v-else/>
              </div>
              <div class="li-content">
                <div class="li-title">{{ item.name }}</div>
                <div class="li-introduce">{{ item.describe }}</div>
                <div class="li-content-bottom">
                  <!-- <span class="li-num">87个文件</span> -->
                  <span>更新时间：{{ item.updateTime || item.createTime }}</span>
                </div>
              </div>
              <el-button
                class="delete-btn"
                type="danger"
                icon="el-icon-remove-outline"
                size="small"
                v-if="isDatasetAdd(item.collectId)"
                @click="removeDataset(item.collectId)"
                >{{ $t("remove") }}</el-button
              >
              <el-button
                class="add-btn"
                type="primary"
                icon="el-icon-circle-plus-outline"
                size="small"
                v-else
                @click="addDataset(item.collectId)"
                >添加</el-button
              >
            </li>
          </ul>
          <ul v-if="showAdd">
            <li
              class="base-li flex-center just"
              v-for="item in datasetAllList"
              :key="item"
            >
              <div class="li-name flex-center">
                <img src="@/assets/images/appManagement/zsk.svg" />
              </div>
              <div class="li-content">
                <div class="li-title">{{ filterSelectKnowledge(item) }}</div>
                <div class="li-introduce">{{ filterSelectKnowledgeDesc(item) }}</div>
                <div class="li-content-bottom">
                  <!-- <span class="li-num">87个文件</span> -->
                  <span>更新时间：{{ filterSelectKnowledgeTime(item) }}</span>
                </div>
              </div>
              <el-button
               class="delete-btn"
                icon="el-icon-remove-outline"
                type="danger"
                @click="detSelectItem(item)"
                size="small"
                >{{ $t("remove") }}</el-button
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
    <!-- <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="setKnowledgeIds">{{
        $t("confirm")
      }}</el-button>
      <el-button @click="cancelAss">{{ $t("cancel") }}</el-button>
    </span> -->
    
  </el-drawer>
  <!-- <AddSensitiveLibrary
      v-if="addSensitiveDialogVisible"
      :dialogVisible="addSensitiveDialogVisible"
      @closeDialog="closeAddSensitiveLibraryDialog"
      :row="false"
      @submitDialog="submitSensitiveLibraryDialog"
    ></AddSensitiveLibrary> -->
  </div>
</template>
  
<script>
// import { getInterceptWordHouseListAll } from "@/api/toolManager";
import { getDataCollectInfoList } from "@/api/dataSj";
// import AddSensitiveLibrary from "@/views/toolManager/dictionaryManage/components/addDialog";
export default {
  // components: {
  //   AddSensitiveLibrary
  // },
  data() {
    return {
      pageNo: 1,
      pageSize: 10,
      total: 0,
      datasetList: [], //知识库 分页
      loading: false,
      datasetAllList: [],
      showAdd: false,
      searchKeyWord: "",
      tabsList: [
        {
          name: "全部",
          value: "",
        },
        // {
        //   name: "我的",
        //   value: "user",
        // },
      ],
      activeTab: '',
      addSensitiveDialogVisible: false,

    };
  },
  computed:{
    isDatasetAdd(){
        return (item)=>{
            return this.collectIdList.includes(item)
        }
    }
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    collectIdList:{
      type:Array,
      default:() => [] 
    },
    configData: Array,
    appConfigForm: Object,
  },
  mounted() {
    this.getDatasetList()
    this.getDatasetAllList();
  },
  methods: {
    getDatasetList() {
      this.loading = true;
      getDataCollectInfoList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        name: this.searchKeyWord
      }).then((res) => {
        if (res.code == "000000") {
          this.datasetList = res.data?.records
          this.total = res.data.totalRow;
          this.loading = false;
        } else {
          this.datasetList = [];
        }
      });
    },
    getDatasetAllList() {
      getDataCollectInfoList({
        pageNo: 1,
        pageSize: 2000,
      }).then((res) => {
        if (res.code == "000000") {
          this.datasetAllList = res.data?.records;
        } else {
          this.datasetAllList = [];
        }
      });
    },
    // 取消添加
    cancelAss() {
      this.$emit("clickConfig", false);
    },
    handleSearch() {
      this.pageNo = 1;
      this.getDatasetList();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getDatasetList();
    },
    handleCurrentChange(val) {
      this.pageNo = val;
      this.getDatasetList();
    },
    addDataset(id){
      // console.log(this.collectIdList);
      const arr=this.collectIdList
      arr.push(id)
      console.log(arr);
      
      this.$emit("update:collectIdList",arr)
    },
    removeDataset(id){
      const arr=this.collectIdList
      const index=arr.findIndex(item=>item==id)
      console.log(arr.splice(index,1));
      
      // this.$emit("update:collectIdList",arr.splice(index,1))
    }
  },
};
</script>
  
<style lang="scss" scoped>
.knowledgeDrawer {
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
  