<template>
  <div class="dialogCode">
    <el-row>
      <el-col :span="18">
        <div class="code-cont">
          <div class="select-app" v-show="activeName === 'first'">
            <el-select v-model="appForm.language" size="mini" :placeholder="$t('selectPlaceholder')"
              style="width: 120px; float: right" @change="onChangeLanguage">
              <el-option label="JavaScript" value="JavaScript">
              </el-option>
              <el-option label="Python" value="Python">
              </el-option>
            </el-select>
          </div>
          <el-tabs v-model="activeName">
            <el-tab-pane label="代码" name="first">
              <div class="code-bd">
                <codemirror v-model="appForm.code" :options="options" class="large-codemirror-editor"></codemirror>
              </div>

            </el-tab-pane>
            <el-tab-pane label="参数" name="second">
              <div class="params-bd">
                <codeParams :nodeStart="nodeStart" @updateDataList="updateDataList" :nodeCode="nodeCode"></codeParams>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

      </el-col>
      <el-col :span="6">
        <div class="plugin-ft">
          <div class="plugin-ft-tit">
            {{ $t("pluginDebug") }}
          </div>
          <div class="plugin-ft-cont">
            <div class="radio-list">
              <span class="radio-list-left" :class="{ active: runType === '1' }" @click="runType = '1'">{{ $t("input")
                }}</span>
              <span class="radio-list-right" :class="{ active: runType === '2' }" @click="runType = '2'">{{ $t("output")
                }}</span>
            </div>
            <div class="typeIn" v-if="runType === '1'">
              <el-form :model="ruleForm" :rules="rulesTypeIn" ref="ruleForm" label-width="0"
                class="demo-ruleForm params-list">
                <el-form-item label="" v-for="(panel, index) in nodeStart?.output" :key="index">
                  <div class="params-list-item">
                    <div class="params-list-item-name">
                      <span>
                        {{ panel.label }}
                        <span class="params-list-item-type" :class="{
                            required: panel.required,
                          }">{{ panel.type }}</span>
                      </span>
                      <span v-if="
                          panel.type == 'file' || panel.type == 'array[file]'
                        ">
                        <el-radio-group v-model="panel.radio">
                          <el-radio label="1">文件</el-radio>
                          <el-radio label="2">地址</el-radio>
                        </el-radio-group>

                      </span>
                    </div>
                    <PanelCompontent v-if="panel.radio == '1'" :panelType="panel.type" :panelValue="panel.value">
                    </PanelCompontent>
                    <el-input v-else v-model="panel.value" type="textarea" :rows="2" placeholder=""
                      style="margin-top: 10px"></el-input>
                  </div>
                </el-form-item>
              </el-form>
              <div style="width: 100%; text-align: center">
                <el-button class="run-btn" @click="runApi" :loading="runApiLoading">
                  <iconpark-icon name="play-circle-line" size="20" color="#36383D"
                    style="margin-right: 6px"></iconpark-icon>{{ $t("startRun") }}
                </el-button>
              </div>
            </div>
            <div class="typeIn" v-if="runType === '2'">
              <div class="output" id="output">
                <vue-json-pretty v-if="isJsonString(outputJsonData)" :showNum="false"
                  :data="JSON.parse(outputJsonData)" />
                <span class="output-text" v-if="outputJsonData" v-for="(value, key) in outputJsonData"
                  :key="key"><strong>{{ key }}:</strong> {{ value }}</span>
                <div v-else style="width: 182px; height: 144px; margin: 20% auto">
                  <img style="width: 100%; height: 100%" src="@/assets/images/checkResult.png" alt="" />
                  <p style="
                      font-size: 16px;
                      color: #494e57;
                      text-align: center;
                    ">
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
                <el-button class="run-btn" @click="runApi" :loading="runApiLoading"><iconpark-icon
                    name="play-circle-line" size="20" color="#36383D" style="margin-right: 6px"></iconpark-icon>{{
                  $t("startRun") }}</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
  import codeParams from "@/views/toolManager/pluginManage/components/codeParams.vue";
  import
  {
    runComponent,
    updateComponent,
    apiValidate,
  } from "@/api/workflow";
  import VueJsonPretty from "vue-json-pretty";
  import "vue-json-pretty/lib/styles.css";
  import { fetchEventSource } from "@microsoft/fetch-event-source";
  import PanelCompontent from "./panelCompontent.vue";
  import Createplugin from "../Createplugin.vue";
  import md5 from "js-md5";

  export default {
    props: {
      nodeCode: Object,
      nodeStart: Object,
      editItem: Object,
    },
    watch: {
      'appForm.code' (newValue) {
        if (newValue === '') {
          this.appForm.code = this.languageCode[this.appForm.language];
        }
        this.$emit("updateAppForm", JSON.stringify(this.appForm));
      },
      'appForm.language' (newValue) {
        this.$emit("updateAppForm", JSON.stringify(this.appForm));
      },
    },
    components: { codeParams, VueJsonPretty, PanelCompontent, Createplugin },
    data ()
    {
      return {
        languageCode: {
          JavaScript: "// 在这里，您可以通过 自定义参数名  获取节点中的输入变量，并通过 'result' 输出结果\nfunction main(arg1, arg2) {\n    // 构建输出对象\n    const result = arg1 + arg2; // 拼接 arg1 和 arg2 的值\n    return { 'result': result };\n}",
          Python: "# 您可以通过自定义参数名,需要与输入的参数名对应\n# 通过返回一个字典,作为代码节点的输出结果\n# 下面是一个示例\ndef main(arg1: str, arg2: str) -> str:\n    return {\n        'result': arg1 + arg2\n    }",
        },
        options: {
          line: true,
          theme: "rubyblue", // 主题
          tabSize: 4, // 制表符的宽度
          indentUnit: 2, // 一个块应该缩进多少个空格（无论这在编辑语言中意味着什么）。默认值为 2。
          firstLineNumber: 1, // 从哪个数字开始计算行数。默认值为 1。
          readOnly: false, // 只读
          autorefresh: true,
          smartIndent: true, // 上下文缩进
          lineNumbers: true, // 是否显示行号
          styleActiveLine: true, // 高亮选中行
          viewportMargin: Infinity, //处理高度自适应时搭配使用
          showCursorWhenSelecting: true, // 当选择处于活动状态时是否应绘制游标
          mode: "javascript",
        },
        appForm: {
          "url": " https://localhost/code/execute",
          "funcName":"main",
          language: "Python",
          code: "",
        },

        activeName: "first",
        runType: "1",
        loading: false,
        runApiLoading: false,
        outputJsonData: {},
        ruleForm: {
          name: "",
        },
        fileListLogo: [],
        actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
        rulesTypeIn: {
          rawQuery: [
            {
              required: true,
              message: this.$t("enterRawQuery"),
              trigger: "blur",
            },
          ],
        },
        activeName: "first",
        outputFormRefs: [],
        drawerLoading: false,
      };
    },
    computed: {

    },

    beforeDestroy () { },
    mounted ()
    {
      this.appForm.code = this.nodeCode.settings.code ?  this.nodeCode.settings.code : this.languageCode[this.appForm.language];
      this.appForm.language = this.nodeCode.settings.language ?  this.nodeCode.settings.language : 'Python'
    },
    methods: {
      updateDataList (data)
      {
        this.$emit("updateDataList", data);
      },
      onChangeLanguage ()
      {
        this.appForm.code = this.languageCode[this.appForm.language];
      },
      scrollToBottom ()
      {
        this.$nextTick(() =>
        {
          const outputElement = document.getElementById("output");
          if (outputElement) {
            outputElement.scrollTop = outputElement.scrollHeight;
          }
        });
      },
      isJsonString (str)
      {
        try {
          JSON.parse(str);
        } catch (e) {
          return false;
        }
        return true;
      },
      runApi ()
      {
        let params = {};
        let that = this;
        this.runApiLoading = true;
        this.nodeStart?.output?.forEach((el) =>
        {
          params[el.label] = el.value;
        });
        let paData = {
          componentId: this.editItem.componentId,
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
          onmessage (event)
          {
            // 成功之后操作
            that.runType = "2";
            let data = JSON.parse(event.data);
            if (!data.nodeId) {
              that.outputJsonData = data.output;
              that.scrollToBottom();
            }
          },
          onerror ()
          {
            console.log(11);
            // 服务异常
            console.log("shibai");
          },
          onclose ()
          {
            that.runApiLoading = false;
            // 服务关闭
            console.log("guanbi");
            that.$EventBus.$emit("apiEnding", that.outputJsonData);
            ctrlAbout.abort();
          },
        });
      },
      closeDialog ()
      {
        this.$emit("configCloseDialog", false);
      },

      submitAddApp (isFabu)
      {
        let params = JSON.parse(JSON.stringify(this.appForm));
        let startOutput = JSON.parse(JSON.stringify(params.nodes[0].output));
        let output1 = JSON.parse(JSON.stringify(params.nodes[1].output));
        let output2 = JSON.parse(JSON.stringify(params.nodes[1].output));
        startOutput = startOutput.map((el) =>
        {
          el.type = "string";
          el.value = el.label;
          el.nodeId = params.nodes[1].nodeId;
          el.referenceNodeId = params.nodes[0].nodeId;
          el.valueType = "reference";
          el.direction = 0;
          return el;
        });
        params.nodes[1].input = startOutput;
        output1 = output1.map((el) =>
        {
          el.type = "string";
          el.value = el.label;
          el.nodeId = params.nodes[2].nodeId;
          el.referenceNodeId = params.nodes[1].nodeId;
          el.direction = 0;
          return el;
        });
        params.nodes[2].input = output1;
        output2 = output2.map((el) =>
        {
          el.type = "string";
          el.value = Array.isArray(el.value)
            ? "${" + el.value.join(".") + "}"
            : el.value;
          return el;
        });
        params.nodes[1].output = output2;
        params.tenantId = this.tenantId;
        params.clickPublish = false;
        if (isFabu) {
          this.drawerLoading = true;
        } else {
          this.loading = true;
        }
        const dataParams = {
          ...params,
          labels: Array.isArray(params.labels)
            ? params.labels.join(",")
            : params.labels,
        };
        if (isFabu) {
          dataParams.publishAppStore = 2;
          dataParams.publishDesc = this.fabuForm.publishDesc;
          dataParams.clickPublish = true;
          dataParams.labels = Array.isArray(this.fabuForm.labels)
            ? this.fabuForm.labels?.join(",")
            : this.fabuForm.labels;
        }
        updateComponent(dataParams)
          .then((res) =>
          {
            if (isFabu) {
              this.drawerLoading = false;
            } else {
              this.loading = false;
            }
            if (res.code == "000000") {
              if (isFabu) {
                this.closeDialog();
                this.$message.success(res.msg);
              }
            } else {
              this.$message({
                type: "error",
                message: res.msg,
              });
            }
          })
          .finally(() =>
          {
            if (isFabu) {
              this.drawerLoading = false;
            } else {
              this.loading = false;
            }
          });
      },


      onSubmit ()
      {
        this.submitAddApp(true);
      },
    },
  };
</script>

<style lang="scss" scoped>
  .outerDialog {
    display: flex;
    height: calc(100vh - 80px);
    flex-direction: column;
    overflow: hidden;

    .dialogCode {
      width: 100%;
      border-top: 1px solid #e1e4eb;
      position: relative;

      .left-content {
        height: calc(100vh - 230px);
        overflow-y: auto;
      }

      ::-webkit-scrollbar {
        width: 0;
      }

      ::v-deep .el-textarea__inner {
        font-family: MiSans, MiSans;
      }
    }

    
  }
  .code-cont {
      padding: 20px;
      position: relative;
      height: calc(100vh - 80px);

      .code-bd {
        height: calc(100vh - 176px);

        ::v-deep .vue-codemirror {
          .cm-s-rubyblue span.cm-comment {
            font-style: normal;
          }
        }
      }

      .large-codemirror-editor {
        flex: 1;
        width: 100%;
        height: 100%;

        ::v-deep .CodeMirror {
          height: 100%;
          line-height: 24px;
        }
      }

      .select-app {
        overflow: hidden;
        height: 34px;
        line-height: 34px;
        margin: 6px 0 0 0;
        font-weight: bold;
        font-size: 16px;
        color: #383d47;
        position: absolute;
        top: 16px;
        right: 20px;
        z-index: 100;
      }
    }

    .headBar {
      background: #FFFFFF !important;
      padding: 16px 32px 15px !important;
      display: flex;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      margin-bottom: 0px !important;
      width: 100%;
      position: absolute;
      top: 0;
      right: 0;
      border-bottom: 1px solid rgba(0, 0, 0, 0.12);

      img {
        width: 15px;
        height: 15px;
      }

      .leftSlide {
        display: flex;
        justify-content: space-between;
        align-items: center;

        >img {
          margin-right: 16px;
          cursor: pointer;
        }

        .avatar {
          width: 40px;
          height: 40px;
          margin: 0 12px 0 14px;
        }

        .titleIcon {
          .txt1 {
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 18px;
            color: #36383d;
            line-height: 24px;
          }

          .txt2 {
            margin-top: 4px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #828894;
            line-height: 20px;
          }
        }
      }

      .rightSlide {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .preview {
          line-height: 36px;
          margin-right: 28px;
          cursor: pointer;

          >span {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 16px;
            color: #3666ea;
            line-height: 24px;
            text-align: left;
            font-style: normal;
            text-transform: none;
          }

          >img {
            margin-right: 5px;
          }

          >span,
          >img {
            vertical-align: middle;
          }
        }

        .btn {
          height: 40px;
          color: #3666ea;
          border: 1px solid #3666ea;
          border-radius: 4px;

          img {
            margin-right: 5px;
          }

          img,
          span {
            vertical-align: middle;
          }
        }

        .btns {
          color: #fff;
          height: 40px;
          background: #1747e5;
          border-radius: 4px;

          img {
            margin-right: 5px;
          }

          img,
          span {
            vertical-align: middle;
          }
        }

        ::v-deep .el-button {
          span {
            display: flex;
            align-items: center;
            height: 100%;
          }
        }

        ::v-deep .el-button--primary.is-plain {
          border-color: rgb(77, 119, 239);
        }

        ::v-deep .el-button--default.is-plain {
          &:hover {
            border-color: #c9ccd1;
          }
        }
      }
    }
  .plugin {
    position: relative;
    color: #383d47;
    height: calc(100vh - 95px);

    &-hd {
      background: linear-gradient(180deg,
          rgba(43, 88, 213, 0.1) 0%,
          rgba(43, 88, 213, 0) 100%);
      border-radius: 8px 0px 0px 8px;
      padding: 20px;
      height: calc(100vh - 112px);
      position: relative;

      .eidt-api {
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translate(-50%);
        width: 80%;
        border-color: #1c50fd;
        color: #1c50fd;
        border-radius: 4px;
      }

      &-tit {
        font-size: 16px;
        line-height: 20px;
        position: relative;

        i {
          position: absolute;
          right: 0;
          cursor: pointer;
        }
      }

      &-link {
        font-size: 14px;
        margin: 20px 0 40px;
      }

      &-info {
        position: relative;

        li {
          margin: 16px 0;
        }

        &-left {
          font-size: 14px;
          color: #828894;
          line-height: 18px;
        }

        &-right {
          position: absolute;
          right: 0;

          &.green {
            color: #30be14;
          }

          &.status {
            &::before {
              content: "";
              width: 8px;
              height: 8px;
              border-radius: 100%;
              background: #30be14;
              position: absolute;
              left: -16px;
              top: 3px;
            }
          }
        }
      }
    }

    &-bd {
      padding: 24px 0 0 32px;

      ::v-deep .el-tabs {
        .el-tabs__item {
          font-size: 16px;
          color: #383d47;
          padding-right: 6px;
        }

        .el-tabs__item.is-active {
          color: #603eca;
        }

        .el-tabs__active-bar {
          background-color: #603eca;
        }

        .el-tabs__nav-wrap::after {
          height: 1px;
          background: rgba(0, 0, 0, 0.12);
        }

        .el-tabs__content {
          height: calc(100vh - 175px);
          overflow: auto;
        }

        .plugin-bd[data-v-34b0738c] .el-tabs .el-tabs__item.is-active {
          color: #603eca;
        }

        .el-tabs__nav-wrap::after {
          background: none;
        }
      }

      &-tit {
        font-size: 16px;
        color: #383d47;
        line-height: 28px;
        position: relative;
        padding: 0 0 0 10px;
        margin: 20px 0 30px;

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

        &::before {
          content: "";
          width: 3px;
          height: 18px;
          background: #1c50fd;
          position: absolute;
          left: -0;
          top: 4px;
        }
      }

      &-add {
        border: 1px solid #d5d8de;
        // width: 100%;
        width: 104px;
        height: 40px;
        line-height: 40px;
        text-align: left;
        cursor: pointer;
        padding: 0 6px;
      }

      &-first {
        position: relative;

        .drawer-content {
          padding: 0 20px 0 0;
          overflow-y: scroll;
          max-height: 500px;
        }
      }

      ::v-deep .el-form {
        border-radius: 4px;
        // border: 1px solid #e1e4eb;
        padding: 26px 16px 16px;
        margin: 0 0 16px 0;
        position: relative;

        i.hide {
          position: absolute;
          right: 42px;
          top: 4px;
          cursor: pointer;
          font-size: 20px;
        }

        i.delete {
          position: absolute;
          right: 12px;
          top: 4px;
          cursor: pointer;
          font-size: 20px;
        }
      }

      .params-list {
        margin: 10px 0 0 0;

        &-item {
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          padding: 16px;
          margin: 0 0 16px 0;
          position: relative;

          &-name {
            font-size: 16px;
            color: #383d47;
            line-height: 20px;
            // font-weight: bold;
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

      &-second,
      &-third {
        .params-list {
          overflow: hidden;

          &-item {
            width: calc(50% - 20px);
            float: left;
            margin-right: 20px;
            background: #ffffff;
            box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
            border-radius: 4px;
            border: 1px solid #e1e4eb;
            padding: 16px 16px 4px;
          }

          ul {
            li {
              margin: 16px 0;
              position: relative;

              span {
                font-size: 14px;
                color: #828894;
              }

              i {
                position: absolute;
                right: 0;
                border-radius: 10px;
                border: 1px solid #6c49db;
                color: #6c49db;
                font-size: 12px;
                padding: 3px 10px;
              }

              em {
                position: absolute;
                right: 0;
                font-size: 14px;
                color: #383d47;
              }
            }
          }
        }
      }

      &-response {
        background: #ffffff;
        border-radius: 4px;
        border: 1px solid #e1e4eb;
        margin: 10px 20px 0 0;
      }

      &-second {
        .apiUrl {
          background: #f2f4f7;
          width: 100%;
          line-height: 36px;
          border-radius: 2px;
          padding: 12px 16px;
          color: #494e57;

          .title {
            display: flex;
            justify-content: space-between;
          }

          .editHandler {
            cursor: pointer;
          }

          .font {
            color: #3fb816;

            span {
              font-size: 30px;
              vertical-align: middle;
            }
          }
        }

        .operationIcon {
          padding-right: 5px;
          font-size: 20px;
        }
      }

      &-third {
        &-hd {
          margin: 20px 0 30px;

          span {
            font-size: 14px;
            color: #828894;
            margin: 0 30px 0 0;
          }
        }

        .el-form {
          margin: 0 20px 16px 0;
          float: left;
          padding: 20px 20px 0 0;
          width: 98%;
        }
      }
    }

    &-ft {
      padding: 24px 0 0 0;
      border-left: 1px solid #e1e4eb;
      position: relative;
      background: #fff;

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
        padding: 0 32px 0 24px;
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
        height: calc(100vh - 166px);
        padding: 0 32px 0 24px;

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

        .output {
          background: #f7f8fa;
          border-radius: 4px;
          height: calc(100vh - 360px);
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
        height: calc(100vh - 310px);
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

    .el-radio .el-radio__input.is-checked+.el-radio__label {
      color: #494e57;
    }
  }
</style>