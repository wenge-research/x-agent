<template>
  <el-drawer
    title=""
    :visible.sync="drawer"
    :with-header="false"
    size="100%"
    custom-class="listingReviewDetails"
    @open="openDrawer"
    v-loading="loading"
  >
    <div class="listingReviewDetails-content">
      <div class="header">
        <div class="header-left">
          <iconpark-icon
            name="close-large-fill"
            size="16"
            color="#1D2129"
            style="margin-right: 16px; cursor: pointer"
            @click="closeDrawer"
          ></iconpark-icon>
          上架审核
        </div>
        <div class="header-right">
          <el-button plain style="width: 80px; border-radius: 2px" @click="prevHandler"
            >上一条</el-button
          >
          <el-button plain style="width: 80px; border-radius: 2px" @click="nextHandler"
            >下一条</el-button
          >
        </div>
      </div>
      <div class="footer">
        <div class="footer-left" v-loading="iframeLoading">
          <iframe
            :src="iframeUrl"
            width="100%"
            height="100%"
            title="示例框架"
            frameborder="0"
            allowfullscreen
          ></iframe>
        </div>
        <div class="footer-right">
          <div
            style="
              width: 100%;
              height: calc(100% - 323px);
              padding: 32px 24px 24px;
              overflow-y: auto;
            "
          >
            <div class="title">{{ applicationInfo.applicationName }}</div>
            <ul class="row2">
              <li v-if="applicationInfo.type" class="row2-item">
                {{ applicationType(applicationInfo.type) }}
              </li>
              <li v-if="applicationInfo.publishType" class="row2-item">
                {{ applicationInfo.publishType }}
              </li>
            </ul>
            <div v-if="applicationInfo.introduce" class="row3">
              {{ applicationInfo.introduce }}
            </div>
            <div class="creator">
              <div class="creator-title">创作人</div>
              <div class="creator-content">
                <div class="creator-content-icon">
                  <iconpark-icon
                    name="user-line"
                    color="#828894"
                    size="16"
                  ></iconpark-icon>
                </div>
                <div class="creator-content-info">
                  <div class="name">{{ applicationInfo.createUser }}</div>
                  <div class="time">{{ applicationInfo.createTime }} 发布</div>
                </div>
              </div>
            </div>
            <div class="configuration">
              <div class="configuration-title">配置项</div>
              <div v-if="llmInfoList.length" class="configuration-item">
                <div class="configuration-item-label">模型</div>
                <ul class="configuration-item-info">
                  <div
                    class="configuration-item-info-item"
                    v-for="(item, index) in llmInfoList"
                    :key="index"
                  >
                    <img
                      v-if="item?.modelName == '雅意'"
                      src="@/assets/images/yayi.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == 'Kimi'"
                      src="@/assets/images/kimi.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == 'DeepSeek'"
                      src="@/assets/images/deepseek.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == '文心一言'"
                      src="@/assets/images/wenxinyiyan.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == '智谱清言'"
                      src="@/assets/images/zhipuqingyan.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == '豆包'"
                      src="@/assets/images/doubao.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == '通义千问'"
                      src="@/assets/images/tongyi.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == '中国移动'"
                      src="@/assets/images/deepseek.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == '百川'"
                      src="@/assets/images/baichuan.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == '星火'"
                      src="@/assets/images/xinghuo.png"
                      alt=""
                    />
                    <img
                      v-if="item?.modelName == 'openAI'"
                      src="@/assets/images/openai.png"
                      alt=""
                    />
                    {{ item?.modelName }}
                  </div>
                </ul>
              </div>
              <div v-if="applicationPluginList.length" class="configuration-item">
                <div class="configuration-item-label">插件</div>
                <ul class="configuration-item-info">
                  <div
                    class="configuration-item-info-item"
                    v-for="(item, index) in applicationPluginList"
                    :key="index"
                  >
                    <img src="@/assets/images/chajian.svg" alt="" class="img" />
                    {{ item?.pluginName }}
                  </div>
                </ul>
              </div>
              <div v-if="knowledgeInfoList.length" class="configuration-item">
                <div class="configuration-item-label">知识库</div>
                <ul class="configuration-item-info">
                  <div
                    class="configuration-item-info-item"
                    v-for="(item, index) in knowledgeInfoList"
                    :key="index"
                  >
                    <img src="@/assets/images/zhishiku.svg" alt="" class="img" />
                    {{ item?.knowledgeName }}
                  </div>
                </ul>
              </div>
              <div v-if="componentInfoList.length" class="configuration-item">
                <div class="configuration-item-label">工作流</div>
                <ul class="configuration-item-info">
                  <div
                    class="configuration-item-info-item"
                    v-for="(item, index) in componentInfoList"
                    :key="index"
                  >
                    <img src="@/assets/images/zhishiku.svg" alt="" class="img" />
                    {{ item?.componentName }}
                  </div>
                </ul>
              </div>
            </div>
          </div>
          <div class="shenhe">
            <div class="shenhe-one">
              <div class="shenhe-one-title">审核</div>
              <el-radio-group v-model="auditStatus" @change="radioChange">
                <el-radio :label="1">通过</el-radio>
                <el-radio :label="0">驳回</el-radio>
              </el-radio-group>
            </div>
            <div v-if="auditStatus == 0" class="shenhe-two">
              <div class="shenhe-two-title">原因</div>
              <el-select
                v-model="auditFailLableOne"
                style="margin: 8px 0 16px; width: 100%"
                @change="selectChange"
              >
                <el-option
                  v-for="(item, index) in reviewList"
                  :key="index"
                  :label="item.name"
                  :value="item.name"
                />
              </el-select>
              <el-input
                v-if="auditFailLableOne == '其他'"
                type="textarea"
                v-model="auditFailLableTwo"
                placeholder="请输入"
              />
              <el-select v-else v-model="auditFailLableTwo" style="width: 100%">
                <el-option
                  v-for="(item, index) in reviewList2"
                  :key="index"
                  :label="item.name"
                  :value="item.name"
                />
              </el-select>
            </div>
            <div class="btn" @click="onSubmit">提交后打开下一条</div>
          </div>
        </div>
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
  },
  watch: {
    value: {
      handler(n) {
        this.drawer = n;
      },
    },
  },
  methods: {
    // 应用类型转义
    applicationType(val) {
      let value = "";
      switch (val) {
        case "qa":
          value = "LLM";
          break;
        case "dialogue":
          value = "对话流";
          break;
        case "text-agent":
          value = "文本生成";
          break;
        case "workflow":
          value = "工作流";
          break;

        default:
          break;
      }
      return value;
    },
    openDrawer() {
      this.auditStatus = 0;
      this.auditFailLableOne = "";
      this.auditFailLableTwo = "";
      this.getDataById();
      this.getListByPid();
    },
    closeDrawer() {
      this.drawer = false;
      this.$emit("closeDrawer");
    },
    async getDataById() {
      this.loading = true;
      this.iframeLoading = true;
      try {
        const parmas = {
          id: this.sourceData?.id,
        };
        let res = await apiGetDataById(parmas);
        if (res.code == "000000") {
          this.detailsData = res.data || {};
          this.applicationInfo = res.data?.applicationInfo || {};
          this.llmInfoList = res.data?.llmInfoList || [];
          this.applicationPluginList = res.data?.applicationPluginList || [];
          this.knowledgeInfoList = res.data?.knowledgeInfoList || [];
          this.componentInfoList = res.data?.componentInfoList || [];
        }
      } catch (error) {
        this.loading = false;
        this.iframeLoading = false;
      }
      this.loading = false;
      setTimeout(() => {
        this.iframeLoading = false;
      }, 4000);
    },
    async getListByPid(pid) {
      const parmas = {
        pid: pid || 0,
      };
      let res = await apiGetListByPid(parmas);
      if (res.code == "000000") {
        if (pid) {
          this.reviewList2 = res.data || [];
        } else {
          this.reviewList = res.data || [];
        }
      }
    },
    radioChange() {
      this.auditFailLableOne = "";
      this.auditFailLableTwo = "";
    },
    selectChange() {
      this.auditFailLableTwo = "";
      const id = this.reviewList.find((item) => item.name == this.auditFailLableOne)?.id;
      this.getListByPid(id);
    },
    // 审核
    async updateserverPublishAudit() {
      this.loading = true;
      const params = {
        id: this.detailsData?.id,
        auditStatus: this.auditStatus,
      };
      if (this.auditStatus == "0") {
        params.auditFailLableOne = this.auditFailLableOne;
        params.auditFailLableTwo = this.auditFailLableTwo;
      }
      let res = await apiUpdateserverPublishAudit(params);
      if (res.code == "000000") {
        console.log("res", res);
        this.$message.success(res.msg);
        // 下一条
        this.nextHandler();
      }
    },
    onSubmit() {
      this.updateserverPublishAudit();
    },
    // 上一条
    prevHandler() {
      this.auditStatus = 0;
      this.auditFailLableOne = "";
      this.auditFailLableTwo = "";
      this.serverPublishAuditGetDataById("UP");
    },
    // 下一条
    nextHandler() {
      this.auditStatus = 0;
      this.auditFailLableOne = "";
      this.auditFailLableTwo = "";
      this.serverPublishAuditGetDataById("DOWN");
    },
    async serverPublishAuditGetDataById(upAnddown) {
      this.loading = true;
      const params = {
        id: this.detailsData.id,
        upAnddown,
      };
      let res = await apiServerPublishAuditGetDataById(params);
      if (res.code == "000000") {
        console.log("res", res);
        this.detailsData = res.data || {};
        this.applicationInfo = res.data?.applicationInfo || {};
        this.llmInfoList = res.data?.llmInfoList || [];
        this.applicationPluginList = res.data?.applicationPluginList || [];
        this.knowledgeInfoList = res.data?.knowledgeInfoList || [];
        this.componentInfoList = res.data?.componentInfoList || [];
      } else {
        this.$message.warning(res.msg);
      }
      this.loading = false;
    },
  },
};
</script>
<style lang="scss">
.listingReviewDetails {
  .el-drawer__body {
    overflow: hidden;
  }
}
</style>
<style lang="scss" scoped>
.listingReviewDetails {
  &-content {
    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      height: 80px;
      padding: 0 32px 0 40px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.12);
      background: #f7f8fa;
      &-left {
        display: flex;
        align-items: center;
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 18px;
        color: #36383d;
      }
      &-right {
        display: flex;
        align-items: center;
      }
    }
    .footer {
      width: 100%;
      height: calc(100% - 80px);
      display: flex;
      align-items: center;
      &-right {
        width: 19%;
        height: calc(100vh - 80px);
        .title {
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 18px;
          color: #36383d;
          line-height: 20px;
        }
        .row2 {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-top: 12px;
          &-item {
            height: 24px;
            line-height: 24px;
            padding: 0 8px;
            background: #ebeef2;
            border-radius: 2px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #36383d;
          }
        }
        .row3 {
          margin-top: 24px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #36383d;
          line-height: 22px;
        }
        .creator {
          margin-top: 24px;
          &-title {
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 16px;
            color: #36383d;
            line-height: 24px;
          }
          &-content {
            display: flex;
            align-items: center;
            margin-top: 12px;
            &-icon {
              width: 40px;
              height: 40px;
              background: #f7f8fa;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              margin-right: 12px;
            }
            &-info {
              .name {
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 14px;
                color: #36383d;
                line-height: 20px;
              }
              .time {
                margin-top: 4px;
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 12px;
                color: #828894;
                line-height: 16px;
              }
            }
          }
        }
        .configuration {
          margin-top: 24px;
          &-title {
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 16px;
            color: #36383d;
            line-height: 24px;
          }
          &-item {
            &-label {
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 19px;
              margin-top: 10px;
            }
            &-info {
              margin-top: 8px;
              display: flex;
              align-items: center;
              flex-wrap: wrap;
              gap: 12px;
              &-item {
                width: 149px;
                height: 36px;
                display: flex;
                align-items: center;
                padding: 0 8px;
                background: #ffffff;
                border-radius: 2px;
                border: 1px solid #c9ccd1;
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 14px;
                color: #36383d;
                img {
                  width: 20px;
                  height: 20px;
                  margin-right: 8px;
                }
              }
            }
          }
        }
        .shenhe {
          position: relative;
          width: 100%;
          height: 323px;
          border-top: 1px solid rgba(0, 0, 0, 0.12);
          padding: 24px;
          &-one {
            display: flex;
            align-items: center;
            justify-content: space-between;
            &-title {
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 16px;
              color: #36383d;
              line-height: 24px;
            }
          }
          &-two {
            margin-top: 24px;
            &-title {
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 20px;
            }
          }
          .btn {
            position: absolute;
            left: 24px;
            right: 24px;
            bottom: 24px;
            margin-top: 63px;
            height: 40px;
            line-height: 40px;
            text-align: center;
            background: #1747e5;
            border-radius: 2px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 16px;
            color: #ffffff;
            cursor: pointer;
          }
          :deep(.el-radio__inner::after) {
            width: 6px;
            height: 6px;
          }
        }
      }
      &-left {
        flex: 1;
        height: calc(100vh - 80px);
      }
    }
  }
}
</style>
