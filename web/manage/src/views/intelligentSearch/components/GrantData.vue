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
      grantType: "user",
      userCondition: "",
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
      userCheckData: [],
      userTableDatas: [],
      grantData: {},
      userLoading: false,
      tenantLoading: false,
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
  },
  methods: {
    confirmGrant() {
      let _this = this;
      if (!this.userCheckData || !this.userCheckData.length) {
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
      this.grantType = value;
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
      this.getGrantDataList(this.dataId, this.dataType, this.grantType);
    },
    userInput(value) {
      this.getData(value);
    },
    userSelectionChange(val) {
      this.userCheckData = val;
    },
    saveGrant() {
      let targetIdList = [];
      switch (this.grantType) {
        case "user":
          targetIdList = this.userCheckData.map((item) => item.id);
          break;
        case "tenant":
          targetIdList = this.userCheckData.map((item) => item.tenantId);
          break;
      }
      let param = {
        // dataId: this.grantData.applicationId,
        // dataType: 'app',
        dataId: this.dataId,
        dataType: this.dataType,
        targetType: this.grantType,
        targetIdList: targetIdList,
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
      userList({
        pageNo: 1,
        pageSize: 99999,
        condition: condition,
      }).then((res) => {
        if (res.code == "000000") {
          // this.totals = res.data?.totalRow||0;
          this.userTableDatas = res.data.records || [];
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
        pageNo: 1,
        pageSize: 99999,
        tenantName: condition,
      }).then((res) => {
        if (res.code == "000000") {
          // this.totals = res.data?.totalRow||0;
          this.userTableDatas = res.data.records || [];
          this.tenantLoading = false;
        } else {
          // this.totals = 0;
          this.userTableDatas = [];
        }
      });
    },
    getGrantDataList(dataId, dataType, targetType) {
      getGrantDataList({ dataId, dataType, targetType }).then((res) => {
        setTimeout(() => {
          this.initSelection(res.data);
        }, 2100);
      });
    },
    initSelection(selectList) {
      // 根据 this.tableData 中的 selected 属性初始化选中状态
      if (selectList && selectList.length) {
        let targetIdList = selectList.map((item) => item.targetId);
        targetIdList.forEach((item) => {
          this.userTableDatas.forEach((row) => {
            switch (this.grantType) {
              case "user":
                // 数字转为字符串
                if (String(row.id) == item) {
                  this.$refs.multipleTable.toggleRowSelection(row, true);
                }
                break;
              case "tenant":
                if (row.tenantId === item) {
                  this.$refs.multipleTable.toggleRowSelection(row, true);
                }
                break;
            }
          });
        });
      }
    },

    // 删除选中
    toggleSelection(rows) {
      if (rows) {
        this.$refs.multipleTable.toggleRowSelection(rows);
      } else {
        // 全部清除选中
        this.userCheckData = [];
        this.$refs.multipleTable.clearSelection();
      }
    },
  },
  mounted() {
    this.getData();
  },
};
</script>

<template>
  <div>
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
            @input="userInput"
          >
          </el-input>
          <el-table
            ref="multipleTable"
            @selection-change="userSelectionChange"
            :data="userTableDatas"
            tooltip-effect="dark"
            style="width: 100%"
            class="table"
            :show-header="false"
            max-height="504px"
            v-loading="userLoading"
          >
            <el-table-column prop="username" :label="$t('userName')" key="slot"
              ><template #default="scope">
                <el-button
                  class="user-name-btn"
                  type="primary"
                  v-if="scope.row.username"
                  >{{ scope.row.username[0] }}</el-button
                >
                <span style="margin-left: 10px">{{ scope.row.username }}</span>
              </template>
            </el-table-column>
            <el-table-column type="selection" width="55"></el-table-column>
          </el-table>
        </div>
        <div v-else-if="grantType === 'tenant'">
          <el-table
            ref="multipleTable"
            @selection-change="userSelectionChange"
            :data="userTableDatas"
            tooltip-effect="dark"
            style="width: 100%"
            class="table"
            :show-header="false"
            max-height="504px"
            v-loading="tenantLoading"
          >
            <el-table-column
              prop="tenantName"
              :label="$t('tenantName')"
              key="slot"
              ><template #default="scope">
                <div class="flex-c">
                  <img
                    src="@/assets/images/appManagement/zhtx.svg"
                    style="width: 28px; height: 28px"
                  />
                  <span style="margin-left: 10px">{{
                    scope.row.tenantName
                  }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column type="selection" width="55"></el-table-column>
          </el-table>
        </div>
      </div>
      <div class="grantData-item right">
        <div class="right-btn flex padding16">
          <span
            >{{ $t("selected") }}
            <i style="color: #1c50fd">{{
              userCheckData.length > 0 ? userCheckData.length : 0
            }}</i>
            {{ $t("individual")
            }}{{ grantType === "user" ? $t("user") : $t("tenants") }}</span
          >
          <span
            style="color: #494E57; cursor: pointer; display: inline-flex;align-items: center;"
            @click="toggleSelection()"
            >
            <iconpark-icon name="brush-3-line"></iconpark-icon>

            {{ $t("clearall") }}
            </span
          >
        </div>
        <ul class="padding16">
          <li
            v-for="(item, index) in userCheckData"
            :key="index"
            class="select-data"
          >
            <div class="flex">
              <div class="flex-c">
                <el-button
                  v-if="grantType === 'user' && item.username"
                  class="user-name-btn"
                  type="primary"
                  >{{ item.username[0] }}</el-button
                >
                <img
                  v-else
                  src="@/assets/images/appManagement/zhtx.svg"
                  style="width: 28px; height: 28px"
                />
                <span style="margin-left: 10px">{{
                  grantType === "user" ? item.username : item.tenantName
                }}</span>
              </div>
              <span style="cursor: pointer" @click="toggleSelection(item)">
                <i class="el-icon-close"></i>
              </span>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <div style="margin-top: 24px; text-align: right;">
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

<style scoped lang="scss">
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
  border: 1px solid #D5D8DE;
  display: flex;
  height: 580px;
}
.grantData-item {
  flex: 1;
}
.user-name-btn {
  width: 28px; height: 28px; padding: 0px;border: 0px; border-radius: 2px; text-align: center; line-height: 28px;background-color: #2E90FA;
}
.right {
  border-left: 1px solid #D5D8DE;
  height: 100%;
  background: #F2F4F7;
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
  background: #F2F4F7;
  font-weight: 500;
  font-size: 16px;
  color: #494E57;
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