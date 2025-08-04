<template>
  <el-drawer
    title="详情"
    :visible.sync="drawer"
    size="29%"
    custom-class="suggestionFeedback-details"
    @open="openDrawer"
    @close="closeDrawer"
  >
    <div class="suggestionFeedback-details-content" v-loading="loading">
      <div class="row1">{{ sourceData.type }}</div>
      <div class="row2">
        {{ sourceData.content }}
      </div>
      <div v-if="srcList.length" class="row3">图片</div>
      <div class="row4">
        <el-image
          v-for="(item, index) in srcList"
          :key="index"
          style="width: 154px; height: 154px"
          :src="item"
          :preview-src-list="[item]"
          fit="cover"
        >
        </el-image>
      </div>
      <div class="row5">
        <div class="title">来源信息</div>
        <ul>
          <li v-for="(item, index) in infoList" :key="index">
            <div class="label">{{ item.label }}</div>
            <div class="value">{{ sourceData[item.key] || '-' }}</div>
          </li>
        </ul>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import {
  apiGetDataById,
  apiGetListByPid,
  apiUpdateserverPublishAudit,
  apiServerPublishAuditGetDataById,
} from "@/api/app";

const infoList = [
  {
    label: "姓名",
    key: "createUserName",
  },
  {
    label: "联系电话",
    key: "createUserPhone",
  },
  {
    label: "来源应用",
    key: "applicationName",
  },
  {
    label: "应用ID",
    key: "applicationId",
  },
  {
    label: "提交时间",
    key: "createTime",
  },
];

export default {
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    sourceData: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      infoList,
      drawer: false,
      auditStatus: 0,
      auditFailLableOne: "",
      auditFailLableTwo: "",
      detailsData: {},
      applicationInfo: {},
      llmInfoList: [], // 模型
      applicationPluginList: [], // 插件
      knowledgeInfoList: [], // 知识库
      componentInfoList: [], // 工作流
      reviewList: [],
      reviewList2: [],
      loading: false,
      loading2: false,
      iframeLoading: false,
    };
  },
  computed: {
    iframeUrl() {
      return this.applicationInfo.clientLink || "";
    },
    srcList() {
      return this.sourceData.imgsUrl ? this.sourceData.imgsUrl.split(',') : []
    }
  },
  watch: {
    value: {
      handler(n) {
        this.drawer = n;
      },
    },
  },
  methods: {
    openDrawer() {
    },
    closeDrawer() {
      this.drawer = false;
      this.$emit("closeDrawer");
    },
  },
};
</script>
<style lang="scss">
.suggestionFeedback-details {
  .el-drawer__body {
    overflow: hidden;
  }
  .el-drawer__header {
    padding: 32px 32px 8px;
    margin-bottom: 16px;
  }
  .el-drawer__header > :first-child {
    font-family: MiSans, MiSans;
    font-weight: 600;
    font-size: 20px;
    color: #1d2129;
    line-height: 32px;
  }
}
</style>
<style lang="scss" scoped>
.suggestionFeedback-details {
  &-content {
    padding: 0 32px;
    height: calc(100vh - 88px);
    overflow: auto;
    .row1 {
      width: 64px;
      height: 24px;
      line-height: 24px;
      text-align: center;
      background: #ebddfe;
      border-radius: 2px;
      font-family: MiSans, MiSans;
      font-size: 12px;
      color: #7e56eb;
    }
    .row2 {
      margin-top: 8px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 18px;
      color: #1d2129;
      line-height: 28px;
    }
    .row3 {
      margin-top: 16px;
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 16px;
      color: #1d2129;
      line-height: 24px;
    }
    .row4 {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-top: 8px;
      margin-bottom: 24px;
      // img {
      //   width: 154px;
      //   height: 154px;
      //   border-radius: 2px;
      //   object-fit: contain;
      // }
    }
    .row5 {
      border-top: 1px solid #e7e7e7;
      .title {
        margin-top: 24px;
        margin-bottom: 12px;
        height: 24px;
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 16px;
        color: #1d2129;
        line-height: 24px;
      }
      ul {
        li {
          display: flex;
          align-items: center;
          margin-bottom: 16px;
          .label {
            width: 56px;
            font-family: MiSans, MiSans;
            font-size: 14px;
            color: #86909c;
            line-height: 20px;
            margin-right: 38px;
          }
          .value {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #1d2129;
            line-height: 20px;
          }
        }
      }
    }
  }
}
</style>