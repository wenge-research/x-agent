<template>
  <el-drawer
    :visible.sync="drawer"
    direction="rtl"
    :before-close="handleClose"
    @open="openDrawer"
    append-to-body
    :size="710"
    custom-class="publishHistoryDrawer"
    :show-close="false"
    :withHeader="false"
  >
    <slot name="title">
      <div class="drawer-header">
        <div class="drawer-title">{{ $t('releaseHistory') }}</div>
        <div class="drawer-search-form">
          <el-input
            :placeholder="$t('inputPlaceholder')"
            size="small"
            style="width: 158px; margin-right: 8px"
            class="input-with-select"
            v-model="versionRemark"
          >
            <el-button
              style="padding: 12px"
              slot="append"
              icon="el-icon-search"
              @click="searchList"
            ></el-button>
          </el-input>

          <el-date-picker
            v-model="dateArr"
            size="small"
            type="datetimerange"
            :range-separator="$t('to')"
            :start-placeholder="$t('startDate')"
            :end-placeholder="$t('endDate')"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="margin-left: 10px; width: 321px;padding-right: 0;"
            :default-time="['00:00:00', '23:59:59']"
            @change="searchList"
            align="right"
          >
          </el-date-picker>
        </div>
        <i class="el-icon-close" @click="handleClose"></i>
      </div>
    </slot>
    <div class="history-list" v-loading="loading">
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in historyList"
          :key="item.id"
          color="#409EFF"
          :hide-timestamp="true"
        >
          <div class="history-item-header">
            <div class="history-item-header-left">
              <span style="margin-right: 30px">{{ item.createTime }}</span>
              <span>{{ item.appVersionNumber }}</span>
            </div>
            <div class="history-item-header-right">
              <span
                v-if="item.appVersionNumber === appVersionNumber"
                class="current-versions"
                >{{ $t('currentVersion') }}</span
              >
              <el-popover
                v-else
                v-model="item.popoverVisible"
                placement="bottom"
                width="220"
                :visible-arrow="true"
                style="margin-right: 10px"
                @hide="backVersionRemark = ''"
              >
                <div class="history-popover-content">
                  <div class="content">
                    <el-input
                      :placeholder="$t('inputPlaceholder')"
                      size="small"
                      v-model="backVersionRemark"
                    >
                    </el-input>
                    <div class="footer-btn">
                      <el-button
                        size="mini"
                        @click="
                          (item.popoverVisible = false),
                            (backVersionRemark = '')
                        "
                        >{{ $t('cancel') }}</el-button
                      >
                      <el-button
                        type="primary"
                        size="mini"
                        @click="backVersions(item)"
                        >{{ $t('confirm') }}</el-button
                      >
                    </div>
                  </div>
                </div>
                <el-button type="text" slot="reference">{{ $t('rollback') }}</el-button>
              </el-popover>
              <el-button type="text" @click="editDesc(index)">
                <i class="el-icon-edit-outline"></i>
              </el-button>
              <el-popover
                v-if="item.appVersionNumber !== appVersionNumber"
                placement="bottom"
                width="220"
                :visible-arrow="true"
                v-model="item.isDelete"
                style="margin-right: 10px"
              >
                <div class="history-popover-content delete-content">
                  <div class="content">
                    <div>
                      <i class="el-icon-info" style="color: red"></i>
                      {{ $t('deletionWarning') }}？
                    </div>
                    <div class="footer-btn">
                      <el-button size="mini" @click="item.isDelete = false"
                        >{{ $t('cancel') }}</el-button
                      >
                      <el-button
                        type="primary"
                        size="mini"
                        @click="deleteVersion(item)"
                        >{{ $t('confirm') }}</el-button
                      >
                    </div>
                  </div>
                </div>
                <el-button
                  type="text"
                  style="margin-left: 10px"
                  slot="reference"
                  ><i class="el-icon-delete"></i
                ></el-button>
              </el-popover>
              <div v-else class="no-delete"></div>
            </div>
          </div>
          <div class="history-item-content">
            <el-input
              v-if="editIndex === index"
              type="textarea"
              ref="historyTextarea"
              autosize
              style="width: 300px"
              :placeholder="$t('inputPlaceholder')"
              v-model="item.publishDesc"
              @blur="saveDesc(item)"
            >
            </el-input>
            <span v-else class="history-item-publishDesc">{{
              item.publishDesc
            }}</span>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-drawer>
</template>

<script>
// api
import {
  apiGetApplicationVersionInfoList,
  apiDeleteApplicationVersionInfo,
  apiBackUpdateApplicationInfo,
  apiUpdateApplicationVersionInfo,
} from "@/api/app";
export default {
  name: "PublishHistory",
  props: {
    applicationInfoId: {
      type: Number,
    },
    appVersionNumber: {
      type: String,
    },
    value: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      popoverVisible: false,
      drawer: false,
      editIndex: -1,
      historyList: [],
      dateArr: [],
      versionRemark: "",
      loading: false,
      backVersionRemark: "",
    };
  },
  watch: {
    value: {
      handler(n) {
        this.drawer = n;
      },
    },
  },
  mounted() {},
  methods: {
    searchList() {
      this.getAppPublishRecordList();
    },
    deleteVersion(data) {
      data.isDelete = false;
      apiDeleteApplicationVersionInfo({
        id: data.id,
      }).then((res) => {
        if (res.code == "000000") {
          console.log(this.appVersionNumber,data.appVersionNumber);
          
          if (this.appVersionNumber == data.appVersionNumber) {
            this.$emit("back");
          } else {
            this.getAppPublishRecordList();
          }
        }
      });
    },
    editDesc(index) {
      this.editIndex = index;
      this.$nextTick(() => {
        this.$refs["historyTextarea"][0].focus();
      });
    },
    backVersions(data) {
      data.popoverVisible = false;
      apiBackUpdateApplicationInfo({
        id: data.id,
        applicationInfoId: data.applicationInfoId,
        backVersionRemark: this.backVersionRemark,
      }).then((res) => {
        if (res.code == "000000") {
          this.backVersionRemark = "";
          this.$emit("back");
        }
      });
    },
    saveDesc(data) {
      this.editIndex = -1;
      apiUpdateApplicationVersionInfo({
        id: data.id,
        publishDesc: data.publishDesc,
      }).then((res) => {
        if (res.code == "000000") {
          this.getAppPublishRecordList();
        }
      });
    },
    openDrawer() {
      this.getAppPublishRecordList();
    },
    handleClose() {
      this.drawer = false;
      this.historyList = [];
      this.versionRemark = null;
      this.dateArr = [];
      this.$emit("close", "drawerVisiblePublishHistory");
    },
    async getAppPublishRecordList() {
      console.log(1);
      this.loading = true;
      try {
        const params = {
          applicationInfoId: this.applicationInfoId,
          pageSize: 5,
          pageNo: 1,
          startTime: this.dateArr ? this.dateArr[0] : null,
          endTime: this.dateArr ? this.dateArr[1] : null,
          publishDesc: this.versionRemark === "" ? null : this.versionRemark,
        };
        let res = await apiGetApplicationVersionInfoList(params);
        if (res.code == "000000") {
          console.log(res.data, "历史版本列表");
          this.historyList = res.data?.list || [];
        }
      } catch (error) {
        this.loading = false;
      }
      this.loading = false;
    },
  },
};
</script>

<style lang="scss" scoped>
.publishHistoryDrawer {
  .drawer-header {
    height: 81px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 26px;
    ::v-deep .el-date-editor .el-range-separator{
      padding: 0;
    }
    .drawer-title {
      font-size: 22px;
    }
    .drawer-search-form {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      margin-right: 16px;
    }
    .el-icon-close {
      font-size: 20px;
    }
  }
  .history-list {
    padding: 0 26px;
    padding-top: 10px;
    .history-item-header {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      .history-item-header-right {
        display: flex;
        align-items: flex-start;
        .el-button {
          padding: 0;
          color: #4f4f4f;
        }
        .el-icon-edit-outline,
        .el-icon-delete {
          font-size: 18px;
          color: #4f4f4f;
        }
      }
    }
    .current-versions {
      color: #55c8a4;
      margin-right: 10px;
    }
  }
  .history-item-content {
    padding: 8px 0;
    .history-item-publishDesc {
      line-height: 40px;
    }
  }
}
.history-popover-content {
  padding: 8px 12px;
  background: #efefef;
  position: relative;
  .content {
    background: #fff;
    padding: 6px;
    border: 1px solid #bbb;
    .el-input {
      border: 0;
      ::v-deep .el-input__inner {
        border: 0;
      }
    }
  }
  .footer-btn {
    margin-top: 10px;
    text-align: right;
  }
}
.delete-content {
  background: #fff;
  .content {
    border: 0;
  }
}
.history-popover-content::before {
  content: "";
  width: 0;
  height: 0;
  border-top: 10px solid transparent;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-bottom: 10px solid #efefef;
  position: absolute;
  top: -20px;
  left: 50%;
}
.delete-content::before {
  border-bottom: 10px solid #fff;
  top: -20px;
  left: 80%;
}
.no-delete{
  width: 40px;
  height: 18px;
}
</style>
