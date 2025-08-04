<template>
  <div class="listing-review">
    <div v-if="pageType == 'list'" class="listing-review-head">
      <div class="title">{{ $t('listingReview') }}</div>
      <ul class="tabs">
        <li class="tabs-items" :class="[tabs == 1 ? 'selected' : '']" @click="tabs = 1">
          应用商店
        </li>
        <li class="tabs-items" :class="[tabs == 2 ? 'selected' : '']" @click="tabs = 2">
          插件商店
        </li>
		<!-- <li class="tabs-items" :class="[tabs == 3 ? 'selected' : '']" @click="tabs = 3">
		  MCP商店
		</li> -->
      </ul>
      <div
        class="audit-records-btn"
        @mouseenter="isHover = true"
        @mouseleave="isHover = false"
        @click="auditRecordsHandler"
      >
        <iconpark-icon
          v-if="!isHover"
          name="file-history-line"
          size="18"
          color="#36383D"
          style="margin-right: 8px"
        ></iconpark-icon>
        <iconpark-icon
          v-else
          name="file-history-line"
          size="18"
          color="#1747E5"
          style="margin-right: 8px"
        ></iconpark-icon>
        审核记录
      </div>
    </div>
    <div v-if="pageType == 'list'" class="listing-review-content">
      <AppStore v-if="tabs == 1"  />
      <PlugIn v-else-if="tabs == 2" />
	  <mcpIn v-else-if="tabs == 3" />
    </div>
    <!-- 审核记录 -->
    <AuditRecords v-else-if="pageType == 'auditRecords'" :messageSource="tabs" @comeBackList="comeBackList" />
  </div>
</template>

<script>
// components
import AppStore from "./components/app-store";
import PlugIn from "./components/plug-in";
import mcpIn from "./components/mcp-in";
import AuditRecords from "./components/audit-records";
export default {
  components: { AppStore, PlugIn, AuditRecords,mcpIn },
  data() {
    return {
      isHover: false,
      pageType: 'list', // list: 首页 auditRecords: 审核记录 listingReview: 上架审核
      isAuditRecords: false,
      tabs: 1, // 应用：1 插件：2
    };
  },
  methods: {
    // 审核记录
    auditRecordsHandler() {
      this.pageType = 'auditRecords';
    },
    comeBackList() {
      this.pageType = 'list';
    }
  }
};
</script>

<style lang="scss" scoped>
.listing-review {
  width: 100%;
  height: 100%;
  &-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 32px;
    width: 100%;
    height: 88px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.12);
    .title {
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 24px;
      color: #36383d;
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
  &-content {
    padding: 24px 32px 32px;
    height: calc(100% - 88px);
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
}
</style>