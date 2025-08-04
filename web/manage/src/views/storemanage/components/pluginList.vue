<template>
  <div class="plugin-container">
    <div v-if="!editDetailDialogVisible" style="height: 100%">
      <div ref="searchTypeRef" class="application-search-type sticky">
        <div class="type-list">
          <div
            v-for="(item, index) in appTypeList"
            :key="index"
            class="type-item"
            :class="{ active: activeType === item.name }"
            @click="changeType(item)"
          >
            {{ item.name }}
          </div>
        </div>
      </div>
      <div
        class="plugin-content"
        v-infinite-scroll="load"
        :infinite-scroll-disabled="disabled"
      >
        <ul v-if="list.length" class="list">
          <li
            class="list-item"
            v-for="(item, index) in list"
            :key="index"
            :style="{ height: '180px' }"
          >
            <div class="list-item-top" @click="detailModelApp(item)">
              <img v-if="item.icon" class="avatar" :src="item.icon" alt="" />
              <img
                v-else
                class="avatar"
                src="@/assets/images/default-plugin.svg"
                alt=""
              />
              <div class="top-info">
                <div class="top-title">
                  <div class="title-name" :title="item.componentName">
                    {{ item.componentName }}
                  </div>
                  <div class="top-type" v-if="false">官方</div>
                </div>
                <div class="item-type" v-if="false">实用工具</div>
              </div>
            </div>
            <div class="list-item-center">
              <div class="componentDesc" :title="item.componentDesc">
                {{ item.componentDesc }}
              </div>
            </div>
            <div class="list-item-bottom">
              <div class="tips">
                {{ $t("creationTime") }}：{{
                  item.updateTime || item.createTime
                }}
              </div>
            </div>
          </li>
        </ul>
        <div v-if="!list.length && !loading" class="no-data">
          <img src="@/assets/images/no-data.png" alt="" />
          <div class="txt1">{{ $t("noData") }}</div>
        </div>
        <p v-if="loading" class="loading-tip">加载中...</p>
        <p v-if="noMore && !loading && list.length" class="loading-tip">
          没有更多了
        </p>
      </div>
    </div>

    <!--  查看插件详情页面  -->
    <pluginDetails
      v-if="editDetailDialogVisible"
      :appConfigForm="editItem"
      @configCloseDialog="configCloseDialog"
      @configShowSecretKey="configShowSecretKey"
      @configOpenModelDialog="configOpenModelDialog"
      :hideEdit="true"
    >
    </pluginDetails>
    <el-dialog
      :visible.sync="editDialogVisible"
      width="100%"
      fullscreen
      :show-close="false"
      class="flexDialog-edit"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    > 
      <PluginSetting
        v-if="editDialogVisible"
        :appConfigForm="editItem"
        @configCloseDialog="configCloseDialog"
        @configShowSecretKey="configShowSecretKey"
      ></PluginSetting>
     </el-dialog>
  </div>
</template>
<script>
import { pluginList } from "@/api/workflow";
import { apiGetLabelTypes } from "@/api/app";
import pluginDetails from "@/views/toolManager/pluginManage/components/pluginDetails.vue";
import PluginSetting from "@/views/toolManager/pluginManage/components/PluginSetting.vue";
export default {
  components: { pluginDetails, PluginSetting },
  props: {
    applicationName: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      list: [],
      pageObj: {
        pageNo: 1,
        pageSize: 48,
        total: 0,
      },
      loading: false,
      label: "",
      appTypeList: [],
      activeType: "全部",
      editDetailDialogVisible: false,
      editItem: {},
      editDialogVisible: false,
    };
  },
  computed: {
    noMore() {
      return this.list.length >= this.pageObj.total;
    },
    disabled() {
      return this.loading || this.noMore;
    },
  },
  mounted() {
    this.getLabelTypes();
    this.getPluginList();
  },
  methods: {
    getPluginList() {
      this.loading = true;
      pluginList({
        pageNo: this.pageObj.pageNo,
        pageSize: this.pageObj.pageSize,
        componentName: this.applicationName || "",
        publishAppStore:1,   // 是否发布应用商店
        labels: this.label == "全部" ? "" : this.label,
      }).then((res) => {
        if (res.data?.records?.length) {
          this.list.push(...res.data.records);
          this.pageObj.total = res.data?.totalRow || 0;
        } else {
          this.list = [];
        }
        this.loading = false;
      });
    },
    load() {
      console.log("load");
      this.loading = true;
      this.pageObj.pageNo += 1;
      this.getPluginList();
    },
    handleSearch() {
      this.pageObj.pageNo = 1;
      this.pageObj.total = 0;
      this.list = [];
      this.getPluginList();
    },
    async getLabelTypes() {
      let res = await apiGetLabelTypes({ type: 1 });
      if (res.code == "000000") {
        this.appTypeList = res.data || [];
      }
    },
    changeType(item) {
      if (this.activeType === item.name) return;
      this.activeType = item.name;
      this.label = item.name;
      this.handleSearch();
    },
    detailModelApp(item) {
      this.editItem = item;
      this.editDetailDialogVisible = true;
      this.$emit("displayHead", false);
    },
    configCloseDialog() {
      // this.editDialogVisible = false;
      // this.editModelDialogVisible = false;
      this.editDetailDialogVisible = false;
      this.editDialogVisible = false;
      this.list = [];
      this.getPluginList();
      this.$emit("displayHead", true);
    },
    configShowSecretKey() {},
    configOpenModelDialog() {
      this.editDialogVisible = true;
    },
  },
};
</script>
<style lang="scss">
.flexDialog-edit {
  .el-dialog__body {
    padding: 0!important;
  }
  .el-dialog__header {
    background: #fff!important;
    padding: 0!important;
  }
}
</style>
<style lang="scss" scoped>
.plugin-container {
  width: 100%;
  height: 100%;
  padding: 0 32px 16px;

  .application-search-type {
    padding-bottom: 16px;
    background-color: #fff;
    .type-list {
      display: flex;
      gap: 16px;
      .type-item {
        cursor: pointer;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 18px;
        color: #828894;
        line-height: 40px;
        &.active {
          font-weight: 500;
          color: #603eca;
        }
      }
    }
  }

  .plugin-content {
    width: 100%;
    height: calc(100% - 56px);
    overflow-y: auto;
    &::-webkit-scrollbar {
      display: none; /* 隐藏滚动条 */
    }
    .list {
      flex: 1;
      display: flex;
      flex-wrap: wrap;
      overflow: auto;
      align-content: flex-start;
      &-item {
        margin-right: 1.3%;
        margin-bottom: 16px;
        width: 24%;
        background: #ffffff;
        border-radius: 4px;
        border: 1px solid #e1e4eb;
        display: flex;
        flex-direction: column;
        position: relative;
        cursor: pointer;
        &:hover {
          box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
        }
        &:nth-child(4n) {
          margin-right: 0;
        }
        &-top {
          display: flex;
          padding: 16px 0;
          margin: 0 16px;
          .avatar {
            margin-right: 12px;
            width: 56px;
            height: 56px;
            border-radius: 50%;
          }
          .top-info {
            flex: 1;
            overflow: hidden;
            .top-title {
              width: 100%;
              display: flex;
              align-items: center;
              overflow: hidden;
              margin-bottom: 4px;
              .title-name {
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 18px;
                color: #494e57;
                line-height: 32px;
                max-width: calc(100% - 48px);
                margin-right: 8px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }
              .top-type {
                background: linear-gradient(
                  270deg,
                  rgba(142, 101, 255, 0.2) 0%,
                  rgba(23, 71, 229, 0.2) 100%
                );
                border-radius: 10px;
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 12px;
                color: #383d47;
                line-height: 20px;
                padding: 0 8px;
              }
            }
            .item-type {
              display: inline-block;
              background: #ebeef2;
              border-radius: 2px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 12px;
              color: #494e57;
              line-height: 20px;
              padding: 0 4px;
            }
          }
        }
        &-center {
          display: flex;
          margin: 0 16px;
          height: 44px;
          .componentDesc {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #494e57;
            line-height: 22px;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
        &-bottom {
          flex: 1;
          display: flex;
          align-items: center;
          font-family: MiSans, MiSans;
          font-size: 14px;
          color: #768094;
          margin: 0 16px;
          .tips {
            display: flex;
            align-items: center;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #828894;
            line-height: 16px;
            .circle {
              margin-right: 5px;
              width: 14px;
              height: 14px;
              border-radius: 50%;
              background: #b4bccc;
              display: flex;
              align-items: center;
              justify-content: center;
              cursor: pointer;
              .el-icon-caret-right {
                font-size: 10px;
                color: #fff;
              }
            }
          }
        }
        .noBorder {
          border-top: none;
        }
        .flex-start {
          align-items: flex-start;
        }
        .text {
          // flex: 1;
        }
        .tipsTpye {
          background: #f0f4fa;
          border-radius: 4px;
          padding: 4px 6px;
          margin: -4px 0 0 6px;
          .tips-name {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #828894;
            line-height: 16px;
          }
        }
        .row {
          display: flex;
          align-items: center;
          margin-bottom: 4px;
          &-name {
            margin-right: 6px;
            height: 24px;
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 16px;
            color: #383d47;
            line-height: 24px;
          }
          &-gender {
            width: 16px;
            height: 24px;
          }
          &-tag {
            margin-right: 8px;
            height: 20px;
            line-height: 18px;
            border-radius: 4px;
            border: 1px solid #e1e4eb;
            padding: 0 8px;
            font-family: MiSans, MiSans;
            font-size: 12px;
            color: #768094;
          }
        }
        .menu {
          height: 20px;
          margin-left: auto;
          transform: rotate(90deg);
          cursor: pointer;
        }
        .edit {
          position: absolute;
          right: 10px;
          bottom: 10px;
          cursor: pointer;
        }
      }
    }
  }
  .loading-tip {
    text-align: center;
    margin-top: 24px;
    font-weight: 400;
    font-size: 14px;
    color: #828894;
    font-style: normal;
    line-height: 22px;
  }
}
</style>