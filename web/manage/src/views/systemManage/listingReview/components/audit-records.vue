<template>
  <div class="audit-records">
    <div class="audit-records-head">
      <iconpark-icon
        name="arrow-go-back-fill"
        color="#36383D"
        size="16"
        style="margin-right: 16px; cursor: pointer"
        @click="comeBack"
      ></iconpark-icon>
      审核记录
    </div>
    <div class="audit-records-content">
      <div class="audit-records-search">
        <el-select
          v-model="search.auditStatusList"
          style="width: 128px"
          @change="searchHandler"
          placeholder="全部结果"
        >
		  <el-option label="全部" value="" />
          <el-option label="通过" value="1" />
          <el-option label="驳回" value="0" />
        </el-select>
        <el-input
          v-model="search.applicationName"
          placeholder="输入关键词"
          prefix-icon="el-icon-search"
          clearable
          style="width: 330px"
          @keydown.enter.native="searchHandler"
        />
      </div>
      <div v-loading="loading">
        <div style="margin-top: 16px">
          <el-table
            ref="multipleTable"
            :data="tableData"
            tooltip-effect="dark"
            style="width: 100%"
            class="table"
            max-height="630"
          >
            <el-table-column
              type="index"
              width="60"
              :label="$t('sequenceNumber')"
            ></el-table-column>
            <el-table-column prop="applicationName" :label="$t('appInfo')">
              <template slot-scope="scoped">
                <div class="table-item">
                  <img v-if="scoped.row.facadeImageUrl" :src="scoped.row.facadeImageUrl" alt="" />
                  <div>
                    <div class="applicationName">
                      {{ scoped.row.applicationName }}
                    </div>
                    <div class="introduce">{{ scoped.row.introduce }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="auditUserName" :label="$t('reviewer')">
            </el-table-column>
            <el-table-column prop="auditTime" :label="$t('auditTime')">
            </el-table-column>
            <el-table-column
              prop="auditStatus"
              :label="$t('result')"
              width="180"
            >
              <template slot-scope="scoped">
                <div>
                  {{ scoped.row.auditStatus == "1" ? "通过" : "驳回" }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="auditFailLableOne"
              :label="$t('reason')"
              width="180"
            >
              <template slot-scope="scoped">
                <div v-if="scoped.row.auditFailLableOne">
                  {{ scoped.row.auditFailLableOne }}
                </div>
                <div v-if="scoped.row.auditFailLableTwo">
                  {{ scoped.row.auditFailLableTwo }}
                </div>
                <div v-else>-</div>
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
  </div>
</template>

<script>
import { apiGetListPage } from "@/api/app";
export default {
  props: {
    messageSource: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      search: {
        auditStatusList: "", // 结果
        applicationName: "", // 应用名
      },
      loading: false,
      tableData: [],
      pageData: {
        pageNo: 1,
        pageSize: 10,
        total: 0,
      },
    };
  },
  mounted() {
    this.queryList();
  },
  methods: {
    searchHandler() {
      this.pageData.pageNo = 1;
      this.queryList();
    },
    resetHandler() {
      this.search.publishType = "全部";
      this.search.time = "";
      this.search.applicationName = "";
      this.queryList();
    },
    async queryList() {
      let params = {
        ...this.search,
        ...this.pageData,
        auditStatusList: this.search.auditStatusList
          ? [this.search.auditStatusList]
          : [0, 1],
        messageSource: this.messageSource,
      };
      delete params.time;
      delete params.total;
      this.loading = true;
      const res = await apiGetListPage(params);
      if (res.code == "000000") {
        this.tableData = res.data?.records || [];
        this.pageData.total = res.data?.totalRow || 0;
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
    comeBack() {
      this.$emit("comeBackList");
    },
  },
};
</script>

<style lang="scss" scoped>
.audit-records {
  height: 100%;
  width: 100%;
  &-head {
    display: flex;
    align-items: center;
    padding: 0 32px;
    width: 100%;
    height: 80px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.12);
    font-family: MiSans, MiSans;
    font-weight: 600;
    font-size: 18px;
    color: #36383d;
  }
  &-content {
    padding: 24px 32px 32px;
    height: calc(100% - 80px);
  }
  &-search {
    display: flex;
    align-items: center;
    padding-bottom: 24px;
    gap: 16px;
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
    }
  }
  .table {
    &-item {
      display: flex;
      img {
        // width: 64px;
        height: 44px;
        margin-right: 12px;
      }
      .facadeImageUrl {
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
</style>