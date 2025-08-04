<template>
  <div class="app-store">
    <div class="app-store-search">
      <div class="col">
        <div class="col-label">分类</div>
        <el-select
          v-model="search.publishType"
          style="width: 152px"
          @change="searchHandler"
        >
          <el-option
            v-for="(item, index) in classificationList"
            :key="index"
            :label="item.name"
            :value="item.name"
          />
        </el-select>
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
        <el-input
          v-model="search.applicationName"
          placeholder="输入应用名"
          prefix-icon="el-icon-search"
          clearable
          @keydown.enter.native="searchHandler"
        />
      </div>
      <div class="col">
        <el-button
          type="primary"
          style="border-radius: 2px"
          @click="searchHandler"
          >搜索</el-button
        >
        <el-button plain style="border-radius: 2px" @click="resetHandler">重置</el-button>
      </div>
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
          :key="timer"
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

          <el-table-column prop="publishType" :label="$t('classification')" width="200">
            <template slot-scope="scoped">
              <div v-if="scoped.row.publishType" class="table-classification">
                {{ scoped.row.publishType }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="applicationType"
            :label="$t('applicationType')"
             width="200"
          >
            <template slot-scope="scoped">
              <div
                v-if="scoped.row.applicationType"
                class="table-classification"
              >
                {{ applicationType(scoped.row.applicationType) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="createUserName"
            :label="$t('creator')"
            width="180"
          >
          </el-table-column>
          <el-table-column
            prop="createTime"
            :label="$t('submissionTime')"
            width="180"
          ></el-table-column>
          <el-table-column :label="$t('action')" width="100">
            <template slot-scope="scoped">
              <el-button
                type="text"
                @click="reviewHandler(scoped.row)"
                style="color: #1747e5"
                >{{ $t("review") }}</el-button
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
    <ListingReviewDetailsStore v-model="drawer" :sourceData="reviewData" @closeDrawer="closeDrawer" />
  </div>
</template>

<script>
// components
import ListingReviewDetailsStore from './listing-review-details-store';
import { apiGetLabelTypes, apiGetListPage } from "@/api/app";
export default {
  components: { ListingReviewDetailsStore },
  data() {
    return {
      classificationList: [], // 分类
      search: {
        publishType: "全部", // 分类
        time: "", // 提交时间
        applicationName: "", // 应用名
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
      timer: null
    };
  },
  computed: {
    submitTimeStart() {
      return this.search.time?.length ? this.search.time[0] : "";
    },
    submitTimeEnd() {
      return this.search.time?.length ? this.search.time[1] : "";
    },
  },
  mounted() {
    this.getLabelTypes();
    this.queryList();
  },
  methods: {
    // 应用类型转义
    applicationType(val) {
      let value = ""
      switch (val) {
        case 'qa':
          value = 'LLM';
          break;
        case 'dialogue':
          value = '对话流';
          break;
        case 'text-agent':
          value = '文本生成';
          break;
        case 'workflow':
          value = '工作流';
          break;
      
        default:
          break;
      }
      return value;
    },
    // 分类
    async getLabelTypes() {
      let res = await apiGetLabelTypes({ type: 2 });
      if (res.code == "000000") {
        this.classificationList = res.data || [];
      }
    },
    searchHandler() {
      this.pageData.pageNo = 1;
      this.queryList();
    },
    resetHandler() {
      this.search.publishType = "全部";
      this.search.time = "";
      this.search.applicationName = "";
      this.searchHandler();
    },
    async queryList() {
      let params = {
        ...this.search,
        ...this.pageData,
        publishType: this.search.publishType == '全部' ? '' : this.search.publishType,
        submitTimeStart: this.submitTimeStart,
        submitTimeEnd: this.submitTimeEnd,
        messageSource: 1, // 1-应用审核 2-插件审核
        auditStatusList: [2],
      };
      delete params.time;
      delete params.total;
      this.loading = true;
      const res = await apiGetListPage(params);
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
    }
  },
};
</script>

<style lang="scss" scoped>
.app-store {
  height: 100%;
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
</style>