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
          <div class="">
            <el-button
              type="primary"
              icon="el-icon-circle-plus"
              style="width: 100%"
              @click="addKnowledge"
              >{{ $t("createWorkflow") }}</el-button
            >
          </div>
        </div>
        <div class="content-right">
          <div class="content-right-header">
            <div class="flex-center">
              <div class="tabs">
                <div
                  v-for="(tab, index) in tabsList"
                  :key="index"
                  class="tab-item"
                  :class="{ active: tab.value === activeTab }"
                  @click.stop="handleTabClick(tab.value)"
                >
                  {{ tab.name }}
                </div>
              </div>
              <el-select
                v-model="sortFeild"
                style="width: 140px; margin-right: 16px"
                @change="handleSearch"
              >
                <el-option
                  v-for="item in sortList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
              <el-checkbox v-model="showAdd" @change="checkboxChange">{{
                $t("onlyAdd")
              }}</el-checkbox>
            </div>
            <iconpark-icon
              name="close-line"
              size="24"
              @click.stop="cancelAss"
            ></iconpark-icon>
          </div>
          <div class="content-right-body" v-loading="loading">
            <div class="list-box">
              <ul v-if="!showAdd">
                <li
                  class="base-li flex-center just"
                  v-for="item in workfowList"
                  :key="item.componentId"
                >
                  <div class="li-name flex-center">
                    <img
                      :src="
                        item.icon ||
                        '@/assets/images/appManagement/workflow.svg'
                      "
                    />
                  </div>
                  <div class="li-content">
                    <div class="li-title">{{ item.componentName }}</div>
                    <div class="li-introduce">{{ item.componentDesc }}</div>
                    <div class="li-content-bottom">
                      <!-- <span class="li-num">87个文件</span> -->
                      <span
                        >{{sortFeild === 'update_time' ? '更新时间' : '创建时间'}}：{{
                          item.updateTime || item.createTime
                        }}</span
                      >
                    </div>
                  </div>
                  <el-button
                    class="delete-btn"
                    v-if="filterKnowledge(item)"
                    icon="el-icon-remove-outline"
                    type="danger"
                    @click="detItem(item)"
                    size="small"
                    >{{ $t("remove") }}</el-button
                  >
                  <el-button
                    v-else
                    class="add-btn"
                    icon="el-icon-circle-plus-outline"
                    type="primary"
                    size="small"
                    @click="addItem(item)"
                    >添加</el-button
                  >
                </li>
              </ul>
              <ul v-if="showAdd">
                <li
                  class="base-li flex-center just"
                  v-for="(item, index) in workFlowIdArr"
                  :key="index"
                >
                  <div class="li-name flex-center">
                    <img
                      :src="
                        filterSelectKnowledgeIcon(item) ||
                        '@/assets/images/appManagement/workflow.svg'
                      "
                    />
                  </div>
                  <div class="li-content">
                    <div class="li-title">
                      {{ filterSelectKnowledge(item) }}
                    </div>
                    <div class="li-introduce">
                      {{ filterSelectKnowledgeDesc(item) }}
                    </div>
                    <div class="li-content-bottom">
                      <!-- <span class="li-num">87个文件</span> -->
                      <span
                        >{{sortFeild === 'update_time' ? '更新时间' : '创建时间'}}：{{ filterSelectKnowledgeTime(item) }}</span
                      >
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
              style="margin-top: 20px; color: #272a31; text-align: right"
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
        <el-button type="primary" @click="setWorkflowIds">{{
          $t("confirm")
        }}</el-button>
        <el-button @click="cancelAss">{{ $t("cancel") }}</el-button>
      </span> -->
    </el-drawer>
    <createApplication
      v-if="dialogVisibleApplication"
      :dialogVisibleApplication="dialogVisibleApplication"
      :type="'add'"
      :params="editItem"
      @cancelApplication="cancelApplication"
      @confirmApplication="confirmApplication"
      @confirmEditApplication="confirmEditApplication"
    ></createApplication>
  </div>
</template>
  
<script>
import {
  appList,
  saveWorkflowComponent,
} from "@/api/workflow";
import {
  apiAddApplicationKnowledge,
  apiDeleteApplicationKnowledge,
} from "@/api/app";
import createApplication from "@/views/workflowConfig/components/createApplication.vue";
export default {
  components: {
    createApplication,
  },
  data() {
    return {
      pageNo: 1,
      pageSize: 10,
      total: 0,
      workfowList: [], //知识库 分页
      loading: false,
      workFlowIdArr: [],
      showAdd: false,
      searchKeyWord: "",
      workfowAllList: [], //知识库
      tabsList: [
        {
          name: "全部",
          value: "",
        },
        {
          name: "我的",
          value: "user",
        },
      ],
      activeTab: "",
      sortFeild: "update_time",
      sortList: [
        {
          value: "update_time",
          label: "最近更新",
        },
        {
          value: "create_time",
          label: "创建时间",
        },
      ],
      dialogVisibleApplication: false,
      editItem: {},
      workFlowIdArrOld: [],
      newWorkFlowIdArr: []
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    configData: Array,
    sourceData: {
      type: Object,
      default: () => {}
    }
  },
  mounted() {
    this.getWorkFlowList();
    this.getWorkFlowAllList();
    this.workFlowIdArr = this.configData || [];
    this.newWorkFlowIdArr = JSON.parse(JSON.stringify(this.configData));
    this.workFlowIdArrOld = JSON.parse(JSON.stringify(this.configData));
  },
  methods: {
    async addApplicationKnowledge(item) {
      const params = {
        applicationId: this.sourceData.applicationId,
        knowledgeId: item.componentId,
        type: "workflow",
      };
      let res = await apiAddApplicationKnowledge(params);
      if(res.code == '000000') {
      }
    },
    async deleteApplicationKnowledge(item) {
      const params = {
        applicationId: this.sourceData.applicationId,
        knowledgeId: item.componentId,
        type: "workflow",
      };
      let res = await apiDeleteApplicationKnowledge(params);
      if(res.code == '000000') {
      }
    },
    // 显示已添加
    checkboxChange(v) {
      if (v) {
        this.workFlowIdArr =  this.compareList(this.workfowList, this.newWorkFlowIdArr);
        this.total = this.workFlowIdArr.length;
      } else {
        this.getWorkFlowList();
      }
    },
    // 显示关联知识库
    filterKnowledge(item) {
      if (this.workFlowIdArr) {
        const list = this.workFlowIdArr.map(ele => ele.componentId);
        return list.includes(item.componentId);
      } else {
        return false;
      }
    },
    // 显示关联知识库
    filterSelectKnowledge(item) {
      let findItem = this.workfowAllList.find(
        (items) => items.componentId == item.componentId
      );
      return findItem?.componentName;
    },
    filterSelectKnowledgeDesc(item) {
      let findItem = this.workfowAllList.find(
        (items) => items.componentId == item.componentId
      );
      return findItem?.componentDesc;
    },
    filterSelectKnowledgeTime(item) {
      let findItem = this.workfowAllList.find(
        (items) => items.componentId == item.componentId
      );
      return findItem?.updateTime || findItem?.createTime;
    },
    filterSelectKnowledgeIcon(item) {
      let findItem = this.workfowAllList.find(
        (items) => items.componentId == item.componentId
      );
      return findItem?.icon || "";
    },
    getWorkFlowList() {
      this.loading = true;
      const userInfo = sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))
        : null;
      const accountName = userInfo ? userInfo.accountName : "";
      appList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        applicationName: this.searchKeyWord,
        order: this.sortFeild,
        sort: "desc",
        type: 2,
        createUser: this.activeTab == "user" ? accountName : "",
      }).then((res) => {
        if (res.code == "000000") {
          this.workfowList = res.data?.records || [];
          this.total = res.data.totalRow || 0;
          this.loading = false;
        } else {
          this.workfowList = [];
        }
        if(this.showAdd || this.newWorkFlowIdArr?.length) {
          this.workFlowIdArr =  this.compareList(this.workfowList, this.newWorkFlowIdArr);
          this.$emit('updateWorkflowIds', this.newWorkFlowIdArr)
        }
        this.total = this.showAdd ? this.workFlowIdArr.length : res.data.totalRow;
      });
    },
    compareList(array1, array2) {
      const filteredArray = array1.filter(item1 =>
        array2.some(item2 => item2.componentId === item1.componentId)
      );
      return filteredArray;
    },
    getWorkFlowAllList() {
      appList({
        pageNo: 1,
        pageSize: 2000,
      }).then((res) => {
        if (res.code == "000000") {
          this.workfowAllList = res.data?.records;
        } else {
          this.workfowAllList = [];
        }
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
      // this.workFlowIdArr.push(data.componentId);
      this.workFlowIdArr.push(data);
      this.newWorkFlowIdArr.push(data);
      // this.addApplicationKnowledge(data);
    },
    // 移除知识库
    detItem(data) {
      const index = this.workFlowIdArr.findIndex(
        (items) => items.componentId === data.componentId
      );
      if (index == -1) {
        return;
      }
      this.workFlowIdArr.splice(index, 1);
      this.newWorkFlowIdArr = this.newWorkFlowIdArr.filter(item => item.componentId != data.componentId)
      this.total = this.workFlowIdArr.length;
      this.$emit('updateWorkflowIds', this.newWorkFlowIdArr)
      // this.deleteApplicationKnowledge(data);
    },
    // 移除知识库
    detSelectItem(data) {
      const index = this.workFlowIdArr.findIndex(
        (items) => items.componentId === data.componentId
      );
      if (index == -1) {
        return;
      }
      this.workFlowIdArr.splice(index, 1);
      this.newWorkFlowIdArr = this.newWorkFlowIdArr.filter(item => item.componentId != data.componentId)
      this.total = this.workFlowIdArr.length;
      this.$emit('updateWorkflowIds', this.newWorkFlowIdArr)
    },
    // 确认添加知识库
    setWorkflowIds() {
      this.$emit("clickConfigParams", "addWorkflowVisible", this.workFlowIdArr);
    },
    // 取消添加
    cancelAss() {
      const flag = this.compareArrays(
        this.workFlowIdArr,
        this.workFlowIdArrOld
      );
      if (!flag) {
        this.$EventBus.$emit("changeApplicationStatus", true);
        this.$EventBus.$emit("saveApplication");
      }
      this.workFlowIdArr = [];
      this.$emit("clickConfig", false);
    },
    handleTabClick(tab) {
      this.activeTab = tab;
      this.getWorkFlowList();
    },
    addKnowledge() {
      this.dialogVisibleApplication = true;
    },
    confirmApplication(val) {
      this.dialogVisibleApplication = false;
      let params = {
        componentDesc: val.componentDesc,
        componentName: val.componentName,
        type: val.type,
        icon: val.icon,
      };
      saveWorkflowComponent(params).then((res) => {
        if (res.code == "000000") {
          this.getWorkFlowList();
          this.getWorkFlowAllList();
          this.$emit("updateAll");
        } else {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }
      });
    },

    cancelApplication() {
      this.dialogVisibleApplication = false;
    },
    compareArrays(arr1, arr2) {
      if (arr1.length !== arr2.length) {
        return false;
      }
      const sortedArr1 = arr1.slice().sort();
      const sortedArr2 = arr2.slice().sort();
      for (let i = 0; i < sortedArr1.length; i++) {
        if (sortedArr1[i] !== sortedArr2[i]) {
          return false;
        }
      }
      return true;
    },
  },
};
</script>
  
<style lang="scss" scoped>
.addWorkFlowDrawer {
  ::v-deep .el-drawer {
    background: #ffffff;
    box-shadow: 0px 1px 4px 4px rgba(0, 0, 0, 0.05);
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
      background: #f7f8fa;
      padding: 32px 24px;
      height: 100%;
      box-sizing: border-box;
      font-family: MiSans, MiSans;
      .content-left-header {
        font-weight: 500;
        font-size: 20px;
        color: #494e57;
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
              color: #603eca;
              position: relative;
              &:after {
                content: "";
                display: block;
                width: 20px;
                height: 3px;
                background: #603eca;
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
  border: 1px solid #d5d8de;
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
      color: #494e57;
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
        background: #ebeef2;
        border-radius: 2px;
        color: #494e57;
      }
    }
  }
  &:hover {
    background: #f2f4f7;
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
  