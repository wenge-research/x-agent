<template>
  <div class="grant-data">
    <div class="title-select">
      <el-select v-model="grantType" @change="onchange">
        <el-option
          v-for="item in grantTypeOption"
          :label="item.label"
          :value="item.value"
          :key="item.id"
        ></el-option>
      </el-select>
    </div>
    <div class="grantData">
      <div class="grantData-item padding16" style="width: 328px">
        <div v-if="grantType === 'user'">
          <el-input
            :placeholder="$t('inputName')"
            prefix-icon="el-icon-search"
            v-model="userCondition"
            clearable 
            @input="userInput"
          >
          </el-input>
          <el-checkbox-group v-model="selectedUsers" @change="handleSelectionChange">
            <div class="user-list no-scrollbar">
              <div v-for="user in userTableDatas" :key="user.id" class="user-item">
                <el-checkbox :label="user.id"><span class="first" v-if="user.username">{{ user.username[0] }}</span>{{ user.username }}</el-checkbox>
              </div>
            </div>
          </el-checkbox-group>
          <el-pagination v-show="!userLoading && userTotal > 0"
          style="margin: 10px 0 0 0;text-align: center;"
            small
            layout="prev, pager, next"
            :current-page.sync="pageNo" @current-change="userPagechange" :page-size="20"
            :total="userTotal">
          </el-pagination>
          
        </div>
        <div v-else-if="grantType === 'tenant'">
          <el-input
            placeholder="请输入租户名"
            prefix-icon="el-icon-search"
            v-model="tenantCondition"
            clearable
            @input="tenantInput"
          >
          </el-input>
          <el-checkbox-group v-model="selectedUsers" @change="handleTenantSelectionChange">
            <div class="user-list no-scrollbar">
              <div v-for="user in userTableDatas" :key="user.id" class="user-item">
                <el-checkbox :label="user.tenantId"><span class="first" v-if="user.tenantName">{{ user.tenantName[0] }}</span>{{ user.tenantName }}</el-checkbox>
              </div>
            </div>
          </el-checkbox-group>
          <el-pagination v-if="!tenantLoading && tenantTotal > 0"
          style="margin: 10px 0 0 0;text-align: center;"
            small
            layout="prev, pager, next"
            :current-page.sync="pageNo" @current-change="tenantPagechange" :page-size="20"
            :total="tenantTotal">
          </el-pagination>
        </div>
      </div>
      <div class="grantData-item right">
        <div class="right-btn flex padding16">
          <span
            >{{ $t("selected") }}
            <i style="color: #1c50fd">{{
              allSelectedUsers.length > 0 ? allSelectedUsers.length : 0
            }}</i>
            {{ $t("individual")
            }}{{ grantType === "user" ? $t("user") : $t("tenants") }}</span
          >
          <span
            style="
              color: #494e57;
              cursor: pointer;
              display: inline-flex;
              align-items: center;
            "
            @click="toggleSelection()"
          >
            <iconpark-icon name="brush-3-line"></iconpark-icon>

            {{ $t("clearall") }}
          </span>
        </div>
        <ul class="padding16">
          <li
            v-for="(item, index) in allSelectedUsers"
            :key="index"
            class="select-data"
          >
            <div class="flex">
              <div class="flex-c">
                <el-button
                  v-if="grantType === 'user'"
                  class="user-name-btn"
                  type="primary"
                  >{{ item.targetName[0] }}</el-button
                >
                <img
                  v-else
                  src="@/assets/images/appManagement/zhtx.svg"
                  style="width: 28px; height: 28px"
                />
                <span style="margin-left: 10px">{{
                  item.targetName
                }}</span>
              </div>
              <span style="cursor: pointer" v-if="grantType === 'user'" @click="removeSelectedUser(item)">
                <i class="el-icon-close"></i>
              </span>
              <span style="cursor: pointer" v-else @click="removeSelectedTenant(item)">
                <i class="el-icon-close"></i>
              </span>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <div class="dialog-footer-left">
        <el-switch v-model="newCopyPermission" style="margin-right: 8px;" :active-value="0" :inactive-value="1" />
        允许他人复制 
        <el-tooltip content="开启后，将允许授权用户复制该应用的副本">
          <iconpark-icon name="question-line" size="16" color="#BABFC6" style="margin-left: 4px;cursor:pointer"></iconpark-icon>
        </el-tooltip>
      </div>
      <div style="margin-top: 24px; text-align: right">
        <el-button @click="cancelGrant">{{ $t("cancel") }}</el-button>
        <el-button
          style="background: #1c50fd; color: #fff"
          type="primary"
          @click="confirmGrant"
          >{{ $t("confirm") }}</el-button
        >
      </div>
    </span>
  </div>
</template>
<script>
import {
  addGrantData,
  getGrantDataList,
  tenantList,
  userList,
} from "@/api/app";

export default {
  name: "GrantData",
  data() {
    return {
      selectedUsers: [], // 当前页选中的用户ID
      allSelectedUsers: [], // 所有已选用户对象
      grantType: "user",
      userCondition: "",
      tenantCondition: "",
      grantTypeOption: [
        {
          label: "用户",
          value: "user",
          id: "1",
        },
        {
          label: "租户",
          value: "tenant",
          id: "2",
        },
      ],
      userTableDatas: [],
      selectedUsersIds: [],
      userTotal: 0,
      tenantTotal: 0,
      pageNo:1,
      grantData: {},
      userLoading: false,
      tenantLoading: false,
      pageChangeFlag: false,
      time1: null,
      newCopyPermission: 1,
    };
  },
  props: {
    dataId: {
      type: String,
      request: true,
    },
    dataType: {
      type: String,
      request: true,
    },
    queryCurrentTenantUserFlag: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    // 当分页用户变化时，更新当前页的选中状态
    userTableDatas() {
      this.selectedUsers = this.getSelectedIdsForCurrentPage();
    }
  },
  methods: {
    // 处理分页变化
    handlePageChange(page) {
      this.currentPage = page;
      // 重置当前页的选中状态，但保留已选用户
      this.selectedUsers = this.getSelectedIdsForCurrentPage();
    },
    // 处理选择变化
    handleSelectionChange(selectedIds) {
      // 获取当前页所有用户
      const currentPageUsers = this.userTableDatas;
      
      // 更新所有已选用户
      let newSelectedUsers = [...this.allSelectedUsers];
      
      // 移除当前页之前选中的用户（避免重复）
      newSelectedUsers = newSelectedUsers.length ? newSelectedUsers.filter(
        user => !currentPageUsers.some(u => u.id === user.id)
      ) : []
      
      // 添加当前页新选中的用户
      selectedIds.forEach(id => {
        const item = currentPageUsers.find(u => u.id === id);
        if (item && ! (newSelectedUsers.length && newSelectedUsers.some(u => Number(u.id) === id))) {
          item.targetName = item.username;
          newSelectedUsers.push(item);
        }
      });
      
      this.allSelectedUsers = newSelectedUsers;
    },
    handleTenantSelectionChange(selectedIds) {
      // 获取当前页所有用户
      const currentPageUsers = this.userTableDatas;
      
      // 更新所有已选用户
      let newSelectedUsers = [...this.allSelectedUsers];
      
      // 移除当前页之前选中的用户（避免重复）
      newSelectedUsers = newSelectedUsers.length ? newSelectedUsers.filter(
        user => !currentPageUsers.some(u => u.tenantId === user.id)
      ) : []
      
      // 添加当前页新选中的用户
      selectedIds.forEach(id => {
        const item = currentPageUsers.find(u => u.tenantId === id);
        if (item && ! (newSelectedUsers.length && newSelectedUsers.some(u => u.tenantId === id))) {
          item.targetName = item.tenantName;
          item.id = item.tenantId;
          newSelectedUsers.push(item);
        }
      });
      
      this.allSelectedUsers = newSelectedUsers;
    },
    // 删除已选用户
    removeSelectedUser(item) {
      // 从所有已选用户中移除
      this.allSelectedUsers = this.allSelectedUsers.filter(user => user.id !== item.id);
      
      // 如果该用户在当前页，也从当前页选中状态中移除
      if (this.selectedUsers.includes(Number(item.id))) {
        this.selectedUsers = this.selectedUsers.filter(id => id !== Number(item.id));
      }
    },
    // 删除已选租户
    removeSelectedTenant(item) {
      this.allSelectedUsers = this.allSelectedUsers.filter(user => user.tenantId !== item.tenantId);
      if (this.selectedUsers.includes(item.tenantId)) {
        this.selectedUsers = this.selectedUsers.filter(id => id !== item.tenantId);
      }
    },
    // 获取当前页已选用户的ID
    getSelectedIdsForCurrentPage() {
      return this.grantType === 'user' ? this.getSelectedUsersForCurrentPage() : this.getSelectedTenantsForCurrentPage();
    },
    // 获取当前页已选用户的ID
    getSelectedUsersForCurrentPage() {
      const currentPageIds = this.userTableDatas.map(item => item.id);
      return this.allSelectedUsers
        .filter(item => currentPageIds.includes(Number(item.id)))
        .map(item => Number(item.id));
    },
    // 获取当前页已选用户的ID
    getSelectedTenantsForCurrentPage() {
      const currentPageIds = this.userTableDatas.map(item => item.tenantId);
      return this.allSelectedUsers
        .filter(item => currentPageIds.includes(item.tenantId))
        .map(item => item.tenantId);
    },
    userPagechange(page) {
        this.pageNo = page;
        this.getUserLists(this.userCondition);
    },
    tenantPagechange(page) {
        this.pageNo = page;
        this.getTenantLists(this.userCondition);
    },
    confirmGrant() {
      let _this = this;
      if (!this.allSelectedUsers || !this.allSelectedUsers.length) {
        this.$confirm(
          "没有选择数据，将会清空该数据的授权，是否继续?",
          this.$t("tips"),
          {
            confirmButtonText: this.$t("confirm"),
            cancelButtonText: this.$t("cancel"),
            type: "warning",
          }
        )
          .then(function () {
            _this.saveGrant();
            _this.cancelGrant();
          })
          .catch(function () {
            _this.$message({
              message: "已取消",
              type: "success",
            });
          });
      } else {
        this.saveGrant();
        this.cancelGrant();
      }
    },
    onchange(value) {
      this.pageNo = 1
      this.grantType = value;
      this.toggleSelection();
      this.getData();
    },
    getData(condition) {
      switch (this.grantType) {
        case "user":
          this.getUserLists(condition);
          break;
        case "tenant":
          this.tenantLoading = false;
          this.getTenantLists(condition);
          break;
      }
      if (!condition) {
        this.getGrantDataList(this.dataId, this.dataType, this.grantType);
      }
    },
    userInput(value) {
      this.pageNo = 1
      this.getData(value);
    },
    tenantInput(value) {
      this.pageNo = 1
      this.getData(value);
    },
    saveGrant() {
      let targetIdList = [];
      switch (this.grantType) {
        case "user":
          targetIdList = this.allSelectedUsers.map((item) => item.id);
          break;
        case "tenant":
          targetIdList = this.allSelectedUsers.map((item) => item.id);
          break;
      }
      let param = {
        // dataId: this.grantData.applicationId,
        // dataType: 'app',
        dataId: this.dataId,
        dataType: this.dataType,
        targetType: this.grantType,
        targetIdList: targetIdList,
        copyPermission: this.newCopyPermission
      };
      addGrantData(param).then((res) => {
        if ("000000" === res.code) {
          this.$message({
            message: this.$t("successed"),
            type: "success",
          });
          this.grantVisible = false;
        } else {
          this.$message({
            message: res.msg,
            type: "error",
          });
        }
      });
    },
    cancelGrant() {
      this.$emit("cancelGrant");
    },
    getUserLists(condition) {
      this.userLoading = true;
      let params = {
        pageNo: this.pageNo,
        pageSize: 20,
        cancelFlag: 0,
        condition: condition,
      }
      if(this.queryCurrentTenantUserFlag) {
        params.queryCurrentTenantUserFlag = this.queryCurrentTenantUserFlag;
      }
      userList(params).then((res) => {
        if (res.code == "000000") {
          // this.totals = res.data?.totalRow||0;
          this.userTableDatas = res.data.records || [];
          this.userTotal = res.data.totalRow || [];
          this.userLoading = false;
        } else {
          // this.totals = 0;
          this.userTableDatas = [];
        }
      });
    },
    getTenantLists(condition) {
      this.tenantLoading = true;
      tenantList({
        pageNo: this.pageNo,
        pageSize: 20,
        tenantName: condition,
        status: "1",
      }).then((res) => {
        if (res.code == "000000") {
          // this.totals = res.data?.totalRow||0;
          this.userTableDatas = res.data.records || [];
          this.tenantTotal = res.data.totalRow || [];
          this.tenantLoading = false;
        } else {
          // this.totals = 0;
          this.userTableDatas = [];
        }
      });
    },
    getGrantDataList(dataId, dataType, targetType) {
      getGrantDataList({ dataId, dataType, targetType }).then((res) => {
        this.newCopyPermission = res.data?.length ? res.data[0]?.copyPermission : 1;
        this.allSelectedUsers = res.data.map(item => {
          return {
            id: item.targetId,
            tenantId: item.targetId,
            targetId: item.targetId,
            username: item.targetName,
            targetName: item.targetName,
          };
        });
        this.selectedUsers = res.data.map(item => item.targetId);
      });
    },
   
 
    // 删除选中
    toggleSelection() {
      this.selectedUsers = []
      this.allSelectedUsers = []
    },
  },
  mounted() {
    this.getData();
  },
};
</script>
<style scoped lang="scss">
.grant-data {
  .dialog-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    &-left {
      display: flex;
      align-items: center;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #1d2129;
      ::v-deep(.el-switch__core) {
        width: 32px!important;
      }
      ::v-deep(.el-switch.is-checked .el-switch__core) {
        border-color: #1747E5;
        background-color: #1747E5;
      }
    }
  }
}
.table {
  ::v-deep .el-table tr:last-of-type td {
    border-bottom: 0;
  }
  ::v-deep .el-table::before {
    height: 0px;
  }
}
.title-select {
  position: absolute;
  top: 26px;
  left: 106px;

  ::v-deep .el-input__inner {
    width: 120px;
    border-radius: 2px;
    border: 1px solid #e1e4eb;
  }
}
.title-text {
  font-weight: 500;
  font-size: 20px;
  color: #383d47;
}
.grantData {
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #d5d8de;
  display: flex;
  height: 580px;
}
.grantData-item {
  flex: 1;
}
.user-name-btn {
  width: 28px;
  height: 28px;
  padding: 0px;
  border: 0px;
  border-radius: 2px;
  text-align: center;
  line-height: 28px;
  background-color: #2e90fa;
}
.right {
  border-left: 1px solid #d5d8de;
  height: 100%;
  background: #f2f4f7;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  ul {
    flex: 1;
    overflow-y: auto;
  }
}
::v-deep .el-table td.el-table__cell {
  border: 0px;
}
::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #1c50fd;
  border-color: #1c50fd;
}
.padding16 {
  padding: 16px;
}
.right-btn {
  height: 56px;
  border-radius: 0px 8px 0px 0px;
  background: #f2f4f7;
  font-weight: 500;
  font-size: 16px;
  color: #494e57;
  line-height: 20px;
}

.flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.flex-c {
  display: flex;
  align-items: center;
}
.select-data {
  font-size: 16px;
  color: #383d47;
  margin-bottom: 20px;
}
</style>
<style lang="scss" scoped>
  .user-authorization {
    display: flex;
    gap: 20px;
    padding: 20px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
  }
  
  .user-list-container, .selected-users-container {
    flex: 1;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    padding: 15px;
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
  }
  
  .user-list {
    max-height: 484px;
    overflow-y: auto;
    position: relative;
    .first{
      width: 28px;
      height: 28px;
      padding: 0px;
      border: 0px;
      border-radius: 2px;
      text-align: center;
      line-height: 28px;
      background-color: #2e90fa;
      display: inline-block;
      color: #fff;
      margin: 0 10px 0 0px;
    }
    ::v-deep(.el-checkbox){
      width: 100%;
    }
    ::v-deep(.el-checkbox__input){
      position: absolute;
      right: 2px;
      top: 8px;
    }
    ::v-deep(.el-checkbox__label){
      padding-left: 0;
    }
  }
  
  .user-item {
    padding: 12px 0;
    position: relative;
  }
  
  .selected-list {
    max-height: 400px;
    overflow-y: auto;
  }
  
  .selected-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .selected-item:hover {
    background-color: #f5f7fa;
  }
  </style>