<template>
  <div class="suggestionFeedback">
    <div class="suggestionFeedback-head">
      <div class="title">{{ $t("suggestionFeedback") }}</div>
    </div>
    <div class="app-store">
      <div class="app-store-search">
        <div class="col">
          <div class="col-label">{{ $t("sourceApplication") }}</div>

          <el-input
            v-model="search.applicationName"
            placeholder="请输入应用名称"
            style="width: 328px"
          />
        </div>
        <div class="col">
          <div class="col-label">提交时间</div>
          <el-date-picker
            v-model="search.time"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd HH:mm:ss"
            :default-time="['00:00:00', '23:59:59']"
            style="width: 388px"
          >
          </el-date-picker>
        </div>
        <div class="col">
          <el-button
            type="primary"
            style="border-radius: 2px"
            @click="searchHandler"
            >搜索</el-button
          >
          <el-button plain style="border-radius: 2px" @click="resetHandler"
            >重置</el-button
          >
        </div>
        <div class="col" style="margin-left: auto">
          <el-button
            plain
            class="export-btn"
            style="
              border-radius: 2px;
              border: 1px solid #1747e5;
              color: #1747e5;
            "
            @click="exportHandler"
            ><iconpark-icon
              class="btns-icon"
              name="share-2-line"
              color="#1747E5"
              size="16"
            ></iconpark-icon
            ><span>导出列表</span></el-button
          >
        </div>
      </div>
      <div v-loading="loading">
        <div>
          <el-table
            ref="multipleTable"
            :data="tableData"
            tooltip-effect="dark"
            style="width: 100%"
            class="table"
            max-height="666"
            :key="timer"
          >
            <el-table-column
              type="index"
              width="60"
              :label="$t('sequenceNumber')"
            ></el-table-column>
            <el-table-column
              prop="applicationName"
              :label="$t('sourceApplication')"
              width="180"
            >
            </el-table-column>

            <el-table-column prop="applicationId" label="应用ID" width="300">
            </el-table-column>
            <el-table-column prop="type" label="反馈类型" width="120">
            </el-table-column>
            <el-table-column prop="content" :label="$t('content')">
            </el-table-column>
            <el-table-column prop="imgsUrl" label="图片" width="80">
              <template slot-scope="scoped">
                <div>{{ picNum(scoped.row.imgsUrl) }}</div>
              </template>
            </el-table-column>
            <el-table-column
              prop="createUserName"
              label="姓名"
              width="120"
            ></el-table-column>
            <el-table-column
              prop="createUserPhone"
              label="联系电话"
              width="140"
            ></el-table-column>
            <el-table-column
              prop="createTime"
              label="提交时间"
              width="180"
            ></el-table-column>
            <el-table-column :label="$t('action')" width="100">
              <template slot-scope="scoped">
                <el-button
                  type="text"
                  @click="reviewHandler(scoped.row)"
                  style="color: #1747e5"
                  >{{ $t("view") }}</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div style="text-align: right; margin-top: 30px">
          <el-pagination
            background
            layout="total, prev, pager, next, sizes, jumper"
            popper-class="slectStyle"
            :current-page="pageData.pageNo"
            :total="pageData.total"
            :page-size="pageData.pageSize"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
            :page-sizes="[10, 20, 30, 40]"
          ></el-pagination>
        </div>
      </div>
    </div>
    <Details
      v-model="drawer"
      :sourceData="reviewData"
      @closeDrawer="closeDrawer"
    />
  </div>
</template>

<script>
import {
  apiGetSuggestionFeedbackListPage,
  apiSuggestionFeedbackExport,
} from "@/api/app";
// components
import Details from "./details";
export default {
  components: { Details },
  data() {
    return {
      search: {
        applicationName: "", // 分类
        time: "", // 提交时间
      },
      loading: false,
      tableData: [],
      pageData: {
        pageNo: 1,
        pageSize: 10,
        total: 0,
      },
      drawer: false,
      reviewData: {},
      timer: null,
      btnLoading: false
    };
  },
  computed: {
    createTimeStart() {
      return this.search.time?.length ? this.search.time[0] : "";
    },
    createTimeEnd() {
      return this.search.time?.length ? this.search.time[1] : "";
    },
  },
  mounted() {
    this.queryList();
  },
  methods: {
    // 应用类型转义
    picNum(val) {
      if (!val) return 0;
      return val.split(",")?.length;
    },
    searchHandler() {
      this.pageData.pageNo = 1;
      this.queryList();
    },
    resetHandler() {
      this.search.time = "";
      this.search.applicationName = "";
      this.searchHandler();
    },
    async queryList() {
      let params = {
        ...this.search,
        ...this.pageData,
        createTimeStart: this.createTimeStart,
        createTimeEnd: this.createTimeEnd,
      };
      delete params.time;
      delete params.total;
      this.loading = true;
      const res = await apiGetSuggestionFeedbackListPage(params);
      if (res.code == "000000") {
        this.tableData = res.data?.records || [];
        this.pageData.total = res.data?.totalRow || 0;
        this.timer = new Date().getTime();
      }
      this.loading = false;
    },
    handleCurrentChange(page) {
      this.pageData.pageNo = page;
      this.queryList();
    },
    handleSizeChange(size) {
      this.pageData.pageSize = size;
      this.queryList();
    },
    reviewHandler(data) {
      this.reviewData = JSON.parse(JSON.stringify(data));
      this.drawer = true;
    },
    closeDrawer() {
      this.drawer = false;
      this.resetHandler();
    },
    // 导出
    exportHandler() {
      let params = {
        ...this.search,
        ...this.pageData,
        createTimeStart: this.createTimeStart,
        createTimeEnd: this.createTimeEnd,
      };
      delete params.time;
      delete params.total;
      this.btnLoading = true;
      apiSuggestionFeedbackExport(params).then((res) => {
        const url = window.URL.createObjectURL(new Blob([res]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", '建议和反馈' + new Date().getTime() + ".xlsx");
        document.body.appendChild(link);
        link.click();
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.suggestionFeedback {
  width: 100%;
  height: 100%;
  &-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 32px;
    width: 100%;
    height: 80px;
    .title {
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 24px;
      color: #1d2129;
    }
    .tabs {
      display: flex;
      align-items: center;
      background: #f0f1f5;
      height: 36px;
      padding: 2px;
      border-radius: 4px;
      &-items {
        padding: 0 16px;
        height: 34px;
        line-height: 34px;
        border-radius: 2px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #828894;
        cursor: pointer;
      }
      .selected {
        background: #fff;
        font-weight: 600;
        font-size: 16px;
        color: #36383d;
      }
    }
    .audit-records-btn {
      width: 124px;
      height: 40px;
      background: #ffffff;
      border-radius: 2px;
      border: 1px solid #c9ccd1;
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 16px;
      color: #36383d;
      cursor: pointer;
      &:hover {
        color: #1747e5;
        border: 1px solid #1747e5;
      }
    }
  }
  ::v-deep .table {
    .is-checked .el-switch__core {
      background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
    }
    .el-switch .el-switch__core {
      border-radius: 12px;
      border: none;
      &:after {
        top: 2px;
      }
    }
    .has-gutter {
      th {
        font-size: 14px;
        color: #828894;
        background: #f2f5fa;
      }
    }

    tr {
      color: #383d47;
      font-size: 16px;
      font-weight: 400;
      .el-checkbox__input.is-checked .el-checkbox__inner,
      .el-checkbox__input.is-indeterminate .el-checkbox__inner {
        background: #1c50fd;
        border-color: #1c50fd;
      }
    }

    .el-button--text {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #1c50fd;
    }
  }
  ::v-deep .el-pager li {
    border: 1px solid #dddfe8;
    color: #272a31;
    background: #fff;
    .el-select-dropdown__item.selected {
      color: #1c50fd;
    }

    &:not(.disabled).active {
      background-color: #fff !important;
      border: 1px solid #1c50fd;
      color: #272a31 !important;
    }
  }
  ::v-deep .btn-prev,
  ::v-deep .btn-next {
    border: 1px solid #dddfe8;
    color: #272a31;
    background: #fff;
  }
  .app-store {
    padding: 4px 32px 32px;
    height: calc(100% - 80px);
    width: 100%;
    &-search {
      display: flex;
      align-items: center;
      padding-bottom: 24px;
      gap: 24px;
      .col {
        display: flex;
        align-items: center;
        &-label {
          margin-right: 12px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 16px;
          color: #36383d;
          line-height: 20px;
        }
        .export-btn {

          .btns-icon,span{
            line-height: 16px;
            vertical-align: middle;
          }

          .btns-icon{
            margin-right: 8px;
          }


          span {
            display: inline-block;
            height: 16px;
          }
        }
      }
    }
    .table {
      &-item {
        display: flex;
        img {
          width: 64px;
          height: 64px;
          margin-right: 12px;
        }
        .applicationName {
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 16px;
          color: #603eca;
          line-height: 24px;
        }
        .introduce {
          margin-top: 4px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #828894;
          line-height: 18px;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2; /* 控制显示的行数 */
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
      &-classification {
        display: inline-block;
        padding: 0 8px;
        height: 24px;
        background: #ebeef2;
        border-radius: 2px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #36383d;
        line-height: 24px;
      }
    }
  }
}
</style>