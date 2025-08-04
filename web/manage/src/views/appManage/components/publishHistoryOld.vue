<template>
  <el-drawer
    title="发布历史"
    :visible.sync="drawer"
    direction="rtl"
    :before-close="handleClose"
    @open="openDrawer"
    append-to-body
    custom-class="publishHistoryDrawer"
  >
    <ul class="list" v-loading="loading">
      <li v-for="(item, index) in historyList" :key="index" class="list-item">
        <div class="drop"></div>
        <div class="line"></div>
        <div class="list-item-time">{{ item.createTime }}</div>
        <div class="list-item-content">{{ item.publishDesc }}</div>
      </li>
    </ul>
  </el-drawer>
</template>

<script>
// api
import { apiGetAppPublishRecordList } from "@/api/app";
export default {
  name: "PublishHistory",
  props: {
    applicationId: {
      type: String,
    },
    value: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      drawer: false,
      historyList: [],
      loading: false
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
    openDrawer() {
      this.getAppPublishRecordList();
    },
    handleClose() {
      this.drawer = false;
      this.historyList = [];
      this.$emit("close", 'drawerVisiblePublishHistory');
    },
    async getAppPublishRecordList() {
      this.loading = true;
      try {
        const params = {
          applicationInfoId: this.applicationId,
        };
        let res = await apiGetAppPublishRecordList(params);
        if (res.code == "000000") {
          this.historyList = res.data?.records || [];
        }
      } catch (error) {
        this.loading = false;
      }
      this.loading = false;
    },
  },
};
</script>

<style lang="scss">
.publishHistoryDrawer {
  .el-drawer__header {
    padding: 32px 32px 0;
    margin-bottom: 24px;
  }
  .el-drawer__header > :first-child {
    height: 32px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 20px;
    color: #36383d;
    line-height: 32px;
  }
  .el-drawer__body {
    padding: 0 32px 32px;
  }
  .list {
    height: 100%;
    &-item {
      margin-bottom: 8px;
      padding-left: 20px;
      padding-bottom: 24px;
      position: relative;
      .drop {
        position: absolute;
        top: 6px;
        left: 0;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: #1747e5;
        border: 1px solid #ffffff;
      }
      .line {
        position: absolute;
        top: 6px;
        left: 3px;
        width: 2px;
        height: 90%;
        background: rgba(23, 71, 229, 0.1);
      }
      &-time {
        height: 20px;
        font-weight: 600;
        font-size: 14px;
        color: #36383d;
        line-height: 20px;
        margin-bottom: 12px;
      }
      &-content {
        width: 468px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 24px;
      }
    }
  }
}
</style>
