<template>
  <el-drawer
    title=""
    :visible.sync="drawer"
    :with-header="false"
    size="100%"
    custom-class="listingReviewDetailsPlugin"
    @open="openDrawer"
    v-loading="loading"
  >
    <div class="listingReviewDetailsPlugin-content">
      <div class="header">
        <div class="header-left">
          <iconpark-icon
            name="close-large-fill"
            size="16"
            color="#36383D"
            style="margin-right: 16px; cursor: pointer"
            @click="closeDrawer"
          ></iconpark-icon>
          上架审核
        </div>
        <div class="header-right">
          <el-button
            plain
            style="width: 80px; border-radius: 2px"
            @click="prevHandler"
            >上一条</el-button
          >
          <el-button
            plain
            style="width: 80px; border-radius: 2px"
            @click="nextHandler"
            >下一条</el-button
          >
        </div>
      </div>
      <div class="footer">
        <div class="footer-left">
          <div class="rows row1">
            <img
              v-if="applicationInfo.icon"
              :src="applicationInfo.icon"
              alt=""
            />
            {{ applicationInfo.componentName }}
          </div>
          <ul class="row2" v-if="applicationInfo.labels">
            <li
              v-for="(item, index) in applicationType(applicationInfo.labels)"
              :key="index"
              class="row2-item"
            >
              {{ item }}
            </li>
          </ul>
          <div v-if="applicationInfo.componentDesc" class="row3">
            {{ applicationInfo.componentDesc }}
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
                <div v-if="detailsData.createTime" class="time">
                  {{ detailsData.createTime }} 提交
                </div>
              </div>
            </div>
          </div>
          <div class="application-call">
            <div class="application-call-title">应用调用</div>
            <div class="plugin-bd-tit">
              {{ $t("inputParameters") }}
            </div>
            <div style="margin-top: 16px; position: relative">
              <el-table
                ref="multipleTable"
                :data="tableData"
                tooltip-effect="dark"
                style="width: 100%"
                class="table"
                max-height="630"
              >
                <el-table-column
                  prop="name_en"
                  :label="$t('paramName')"
                ></el-table-column>
                <el-table-column
                  prop="feild_type"
                  :label="$t('parameterType')"
                ></el-table-column>
                <el-table-column prop="isRequired" :label="$t('isRequired')">
                  <template slot-scope="scope">
                    <span v-if="scope.row.isRequired"> 是 </span>
                    <span v-else> 否 </span>
                  </template>
                </el-table-column>
                <el-table-column prop="feild_desc" :label="$t('describe')">
                </el-table-column>
              </el-table>
            </div>
            <div class="plugin-bd-tit">
              {{ $t("outputParameters") }}
            </div>
            <div style="margin-top: 16px">
              <el-table
                ref="multipleTable"
                :data="tableDataResult"
                tooltip-effect="dark"
                style="width: 100%"
                class="table"
                max-height="630"
              >
                <el-table-column
                  prop="name_en"
                  :label="$t('paramName')"
                ></el-table-column>
                <el-table-column
                  prop="feild_type"
                  :label="$t('parameterType')"
                ></el-table-column>
                <el-table-column prop="feild_desc" :label="$t('describe')">
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
        <div class="footer-right">
          <div
            style="
              width: 100%;
              height: calc(100% - 323px);
              padding: 16px 24px 24px;
              overflow-y: auto;
            "
          >
            <div class="plugin-ft">
              <div class="plugin-ft-tit">
                <!-- <img src="@/assets/images/icon-contacts-fill.png"/> -->
                {{ $t("test") }}
                <!-- <span class="clear" @click="clearAllParams"><i class="el-icon-takeaway-box"></i>{{ $t("clearContent") }}</span> -->
              </div>
              <div class="plugin-ft-cont">
                <div class="radio-list">
                  <span
                    class="radio-list-left"
                    :class="{ active: runType === '1' }"
                    @click="runType = '1'"
                    >{{ $t("request") }}</span
                  >
                  <span
                    class="radio-list-right"
                    :class="{ active: runType === '2' }"
                    @click="runType = '2'"
                    >{{ $t("response") }}</span
                  >
                </div>
                <div class="typeIn" v-if="runType === '1'">
                  <el-form
                    :model="ruleForm"
                    :rules="rulesTypeIn"
                    ref="ruleForm"
                    label-width="0"
                    class="demo-ruleForm params-list"
                  >
                    <!-- <el-form-item label="" prop="rawQuery">
                                        <div class="params-list-item">
                                            <div class="params-list-item-name">
                                                rawQuery
                                                <span
                                                    class="params-list-item-type require"
                                                    >String</span
                                                >
                                            </div>
                                        </div>
                                        <el-input
                                            v-model="ruleForm.rawQuery"
                                        ></el-input>
                                    </el-form-item>
                                    <el-form-item label="">
                                        <div class="params-list-item">
                                            <div class="params-list-item-name">
                                                chatHistory
                                                <span
                                                    class="params-list-item-type"
                                                    >String</span
                                                >
                                            </div>
                                        </div>
                                        <el-input
                                            v-model="ruleForm.chatHistory"
                                        ></el-input>
                                    </el-form-item>
                                    <el-form-item label="">
                                        <div class="params-list-item">
                                            <div class="params-list-item-name">
                                                fileUrls
                                                <span
                                                    class="params-list-item-type"
                                                    >Array&lt;String&gt;</span
                                                >
                                            </div>
                                        </div>
                                        <div class="uploadOuter">
                                            <el-upload
                                                :action="actionUrl"
                                                :data="{ filePath: 'agent_source' }"
                                                :file-list="fileListLogo"
                                                :show-file-list="false"
                                                :limit="1"
                                                :on-remove="handleLogoRemove"
                                                :on-success="handleLogoSuccess"
                                            >
                                                <i class="el-icon-upload"></i>
                                                <div class="el-upload__text">
                                                    {{ $t("dragFileHere") }}，{{
                                                        $t("or")
                                                    }}<em>{{
                                                        $t("clickUpload")
                                                    }}</em>
                                                </div>
                                                <div
                                                    class="el-upload__tip"
                                                    slot="tip"
                                                >
                                                    支持.xlsx、.json、.jsonl、.png、.jpg、.jpeg、.pdf、.wav、.docx、.csv、.txt格式，仅支持单个文件且不超过20MB
                                                </div>
                                            </el-upload>
                                        </div>
                                    </el-form-item> -->
                    <el-form-item
                      label=""
                      v-for="(panel, index) in nodeStart?.output"
                      :key="index"
                    >
                      <div class="params-list-item">
                        <div class="params-list-item-name">
                          <span>
                            {{ panel.label }}
                            <span
                              class="params-list-item-type"
                              :class="{
                                required: panel.required,
                              }"
                              >{{ panel.type }}</span
                            >
                          </span>
                          <span
                            v-if="
                              panel.type == 'file' ||
                              panel.type == 'array[file]'
                            "
                          >
                            <el-radio v-model="panel.radio" label="1"
                              >文件</el-radio
                            >
                            <el-radio v-model="panel.radio" label="2"
                              >地址</el-radio
                            >
                          </span>
                        </div>
                        <PanelCompontent
                          v-if="panel.radio == '1'"
                          :panelType="panel.type"
                          :panelValue="panel.value"
                        ></PanelCompontent>
                        <el-input
                          v-else
                          v-model="panel.value"
                          placeholder=""
                          style="margin-top: 10px"
                        ></el-input>
                      </div>
                    </el-form-item>
                  </el-form>
                  <div style="width: 100%; text-align: center">
                    <el-button
                      class="run-btn"
                      @click="runApi"
                      :loading="runApiLoading"
                      style="
                        color: #1747e5;
                        border: 1px solid #1747e5;
                        border-radius: 2px;
                      "
                    >
                      <iconpark-icon
                        name="send-plane-2-fill"
                        size="20"
                        color="#1747E5"
                        style="margin-right: 6px"
                      ></iconpark-icon
                      >{{ $t("startRun") }}
                    </el-button>
                  </div>
                </div>
                <div class="typeIn" v-if="runType === '2'">
                  <div class="output" id="output">
                    <vue-json-pretty
                      v-if="isJsonString(outputJsonData)"
                      :showNum="false"
                      :data="JSON.parse(outputJsonData)"
                    />
                    <span
                      class="output-text"
                      v-if="outputJsonData"
                      v-for="(value, key) in outputJsonData"
                      :key="key"
                      ><strong>{{ key }}:</strong> {{ value }}</span
                    >
                    <div
                      v-else
                      style="width: 182px; height: 144px; margin: 20% auto"
                    >
                      <img
                        style="width: 100%; height: 100%"
                        src="@/assets/images/checkResult.png"
                        alt=""
                      />
                      <p
                        style="
                          font-size: 16px;
                          color: #494e57;
                          text-align: center;
                        "
                      >
                        点击开始运行查看结果
                      </p>
                    </div>
                  </div>
                  <!-- <div class="tips">
                                    <span
                                        ><img
                                            src="@/assets/images/icon-contacts-fill.png"
                                        />点击开始运行查看结果</span
                                    >
                                </div> -->
                  <div style="width: 100%; text-align: center">
                    <el-button
                      class="run-btn"
                      @click="runApi"
                      :loading="runApiLoading"
                      style="
                        color: #1747e5;
                        border: 1px solid #1747e5;
                        border-radius: 2px;
                      "
                      ><iconpark-icon
                        name="send-plane-2-fill"
                        size="20"
                        color="#1747E5"
                        style="margin-right: 6px"
                      ></iconpark-icon
                      >{{ $t("startRun") }}</el-button
                    >
                  </div>
                </div>
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
              <el-input v-if="auditFailLableOne == '其他'" type="textarea" v-model="auditFailLableTwo" placeholder="请输入" />
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
import { fetchEventSource } from "@microsoft/fetch-event-source";
import PanelCompontent from "@/views/toolManager/pluginManage/components/panelCompontent.vue";
import md5 from "js-md5";

export default {
  components: { PanelCompontent },
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
      reviewList: [],
      reviewList2: [],
      loading: false,
      loading2: false,
      runType: "1",
      ruleForm: {
        name: "",
      },
      rulesTypeIn: {
        rawQuery: [
          {
            required: true,
            message: this.$t("enterRawQuery"),
            trigger: "blur",
          },
        ],
      },
      runApiLoading: false,
      outputJsonData: {},
      nodeStart: {},
      tableData: [],
      tableDataResult: [],
    };
  },
  computed: {},
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
      return val ? val.split(",") : [];
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
      try {
        const parmas = {
          id: this.sourceData?.id,
        };
        let res = await apiGetDataById(parmas);
        if (res.code == "000000") {
          this.detailsData = res.data || {};
          this.applicationInfo = res.data?.component || {};
          this.nodeStart = res.data?.component?.nodes[0];
        }
      } catch (error) {
        this.loading = false;
      }
      this.loading = false;
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
      const id = this.reviewList.find(item => item.name == this.auditFailLableOne)?.id;
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
        this.detailsData = res.data || {};
        this.applicationInfo = res.data?.component || {};
        this.nodeStart = res.data?.component?.nodes[0];
      } else {
        this.$message.warning(res.msg);
      }
      this.loading = false;
    },
    runApi() {
      let params = {};
      let that = this;
      this.runApiLoading = true;
      this.nodeStart?.output?.forEach((el) => {
        params[el.label] = el.value;
      });
      let paData = {
        componentId: this.applicationInfo.componentId,
        inputs: params,
        clientId: Math.floor(Math.random() * 10000000000),
      };
      let ctrlAbout = new AbortController();
      let manageAccessToken = sessionStorage.getItem("manageAccessToken");
      let timer = new Date().getTime();
      fetchEventSource(`${process.env.VUE_APP_BASE_API}/workflow/dialogueRun`, {
        method: "post",
        headers: {
          Authorization: `Bearer ${manageAccessToken}`,
          "Content-Type": "application/json",
          timestamp: timer,
          cipher: md5(timer + `/workflow/dialogueRun${JSON.stringify(paData)}xxxxxxxxxxx`),
        },
        signal: ctrlAbout.signal,
        body: JSON.stringify(paData),
        openWhenHidden: true, //默认为false，监听visibilitychange，当页面不可见时关闭连接，当页面重新可见时重新打开连接。
        onmessage(event) {
          // 成功之后操作
          that.runType = "2";
          let data = JSON.parse(event.data);
          if (!data.nodeId) {
            that.outputJsonData = data.output;
            that.scrollToBottom();
          }
        },
        onerror() {
          console.log(11);
          // 服务异常
          console.log("shibai");
        },
        onclose() {
          that.runApiLoading = false;
          // 服务关闭
          console.log("guanbi");
          that.$EventBus.$emit("apiEnding", that.outputJsonData);
          ctrlAbout.abort();
        },
      });
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const outputElement = document.getElementById("output");
        if (outputElement) {
          outputElement.scrollTop = outputElement.scrollHeight;
        }
      });
    },
    isJsonString(str) {
      try {
        JSON.parse(str);
      } catch (e) {
        return false;
      }
      return true;
    },
  },
};
</script>
<style lang="scss">
.listingReviewDetailsPlugin {
  .el-drawer__body {
    overflow: hidden;
  }
}
</style>
<style lang="scss" scoped>
.listingReviewDetailsPlugin {
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
        width: 29%;
        height: calc(100vh - 80px);
        border-left: 1px solid rgba(0, 0, 0, 0.12);

        .plugin-ft {
          position: relative;
          height: 100%;
          ::v-deep .run-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            height: 40px;
            margin-top: 24px;
            border-radius: 2px;
            span {
              display: flex;
              align-items: center;
              justify-content: center;
              height: 100%;
            }
          }
          &-tit {
            position: relative;
            font-size: 20px;
            color: #494e57;
            height: 40px;
            line-height: 40px;
            img {
              position: absolute;
              width: 24px;
              left: 20px;
            }
            .clear {
              position: absolute;
              right: 20px;
              font-size: 16px;
              color: #828894;
              cursor: pointer;
            }
          }
          &-cont {
            height: calc(100% - 40px);
            .radio-list {
              margin-top: 18px;
              text-align: center;
              background: #f0f1f5;
              padding: 2px;
              height: 38px;
              line-height: 38px;
              span {
                cursor: pointer;
                font-size: 16px;
                color: #828894;
                background: transparent;
                border-radius: 4px;
                display: inline-block;
                width: 50%;
                text-align: center;
                height: 34px;
                position: relative;
                &.active {
                  background: #ffffff;
                  z-index: 1;
                  color: #36383d;
                  border-radius: 2px;
                  height: 34px;
                }
              }
              &-left {
                // left: 10px;
              }
              &-right {
                // right: 10px;
              }
            }
            .typeIn {
              height: calc(100% - 56px);
            }
            .output {
              background: #f7f8fa;
              border-radius: 4px;
              height: calc(100% - 88px);
              margin-top: 24px;
              padding: 10px 0;
              overflow: auto;
              line-height: 24px;
              .output-text {
                padding: 6px 16px;
                display: block;
              }
              ::v-deep .vjs-key {
                color: #005cc5;
                width: fit-content;
                text-align: right;
                min-width: 72px;
              }
              ::v-deep .vjs-value-string {
                color: #666;
              }
              ::v-deep .vjs-tree-node {
                background: #f8f8f8 !important;
              }
            }
            .tips {
              position: absolute;
              text-align: center;
              left: 50%;
              top: 35%;
              transform: translateX(-50%);
              line-height: 40px;
              font-size: 16px;
              color: #383d47;
              img {
                display: block;
                margin: 0 auto;
              }
            }
          }

          ::v-deep .el-form {
            margin: 0 0 16px 0;
            position: relative;
            height: calc(100% - 64px);
            overflow-y: scroll;
          }
          .uploadOuter {
            background: #f9fafc;
            border-radius: 4px;
            border: 1px solid rgba(0, 0, 0, 0.12);
            position: relative;
            padding: 20px 20px 20px 110px;
            text-align: left;
            line-height: 24px;
            ::v-deep .el-upload {
              text-align: left;
            }
            .el-icon-upload {
              position: absolute;
              font-size: 40px;
              left: 50px;
              transform: translate(0, -70%);
              top: 50%;
              color: #b4bccc;
            }
            .el-upload__text {
              text-align: left;
              font-size: 16px;
              em {
                color: #3666ea;
              }
            }
            .el-upload__tip {
              font-size: 14px;
              color: #b4bccc;
              margin: 0;
            }
          }
          .params-list {
            &-item {
              padding: 16px 0;
              position: relative;
              &-name {
                font-size: 16px;
                color: #383d47;
                line-height: 20px;
                font-weight: bold;
                display: flex;
                justify-content: space-between;
              }
              &-type {
                background: #f2f5fa;
                border-radius: 4px;
                font-size: 12px;
                color: #828894;
                position: relative;
                padding: 4px 12px;
                left: 10px;
                &.require {
                  &::after {
                    content: "*";
                    position: absolute;
                    left: -12px;
                    top: 3px;
                    color: red;
                    font-size: 20px;
                  }
                }
              }
              &-desc {
                font-size: 14px;
                color: #828894;
                margin: 10px 0 0 0;
              }
              &-tool {
                font-size: 20px;
                position: absolute;
                right: 16px;
                top: 16px;
                color: #828894;
                i {
                  margin: 0 0 0 10px;
                  cursor: pointer;
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
        padding: 24px 32px;
        .rows {
          display: flex;
          align-items: center;
        }
        .row1 {
          img {
            width: 40px;
            height: 40px;
            margin-right: 16px;
          }
        }
        .row2 {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-top: 16px;
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
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 16px;
          color: #36383d;
          line-height: 24px;
          margin-top: 20px;
          display: -webkit-box; /* 关键属性1：启用弹性盒子 */
          -webkit-box-orient: vertical; /* 关键属性2：垂直排列 */
          -webkit-line-clamp: 2; /* 关键属性3：限制行数 */
          overflow: hidden;
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
        .application-call {
          margin-top: 32px;
          &-title {
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 18px;
            color: #36383d;
            display: flex;
            align-items: center;
            &::before {
              content: "";
              display: inline-block;
              width: 4px;
              height: 18px;
              background: #1747e5;
              border-radius: 0px 2px 2px 0px;
              margin-right: 6px;
            }
          }
          .plugin-bd-tit {
            margin: 16px 0;
            font-size: 16px;
            color: #383d47;
            line-height: 28px;
            position: relative;
            font-weight: 600;
            span {
              color: #1c50fd;
              font-size: 14px;
              margin: 0 0 0 36px;
              cursor: pointer;
              position: relative;
              em {
                font-size: 22px;
                position: absolute;
                left: -16px;
                top: -5px;
              }
            }
          }
          ::v-deep .el-table .el-table__cell {
              padding: 4px 0!important;
            }
        }
      }
    }
  }
}
</style>