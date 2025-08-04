<template>
  <div class="dialogCode" v-if="typeNode">
    <el-row>
      <el-col :span="18" style="background: #F7F8FA">
        <div class="code-cont">
          <div class="select-app" v-show="activeName === 'first'">
            <el-select v-model="appForm.codeLanguage" size="mini" :placeholder="$t('selectPlaceholder')"
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
                <codeParams :dataList="appForm.input" :configItems="appForm.output"  :nodeStart="nodeStart" @updateDataList="updateDataList" :nodeCode="nodeCode"></codeParams>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

      </el-col>
      <el-col :span="6">
        <div class="node-rig">
          <div class="node-rig-tit">
            {{ $t("basicInfo") }}
          </div>
          <el-form :model="appForm" :rules="rules" ref="ruleForm" class="demo-ruleForm">
            <el-form-item :label="$t('nodeName')" prop="nodeName">
              <el-input v-model="appForm.nodeName" show-word-limit :maxlength="6" style="width: 100%" />
            </el-form-item>
            <el-form-item :label="$t('nodeDescription')">
              <el-input class="inputTextarea" type="textarea" :rows="5" v-model="appForm.nodeDesc" show-word-limit
                maxlength="1000" style="width: 100%" />
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import CodeParams from "./codeParams.vue";
import VueJsonPretty from "vue-json-pretty";
import "vue-json-pretty/lib/styles.css";
import { fetchEventSource } from "@microsoft/fetch-event-source";
import PanelCompontent from "./panelCompontent.vue";
import Createplugin from "../Createplugin.vue";

export default {
  props: {
    nodeCode: Object,
    nodeStart: Object,
    editItem: Object,
    typeNode: {
      type: Number,
      default: 2,
    },
    appNodeForm: {
      type: Object,
      default: () => {}
    }
  },
  watch: {
    'appForm.code'(newValue) {
      if (newValue === '') {
        this.appForm.code = this.languageCode[this.appForm.codeLanguage];
      }
      this.$emit("updateAppForm", JSON.stringify(this.appForm));
    },
    'appForm.codeLanguage'(newValue) {
      this.$emit("updateAppForm", JSON.stringify(this.appForm));
    },
    appNodeForm: {
      handler(newVal) {
          if (this.typeNode === 2 && newVal) {
          this.appForm = Object.assign({}, this.appNodeForm,newVal);
        }
      },
       deep: true,
    },
  },
  components: { CodeParams, VueJsonPretty, PanelCompontent, Createplugin },
  data() {
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
        // "url": "http://172.16.100.37:1216/execute",
        // "funcName": "main",
        codeLanguage: "JavaScript",
        code: "",
        nodeName: "",
        nodeDesc: "",
        input:[],
        output:[]
      },
      activeName: "first",
      runType: "1",
      loading: false,
      runApiLoading: false,
      outputJsonData: {},
      ruleForm: {
        name: "",
      },
      rules: {
        nodeName: [
            { required: true, message: "请输入节点名称", trigger: "blur" },
          ],
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
      outputFormRefs: [],
      drawerLoading: false,
    };
  },
  computed: {
  },
  beforeDestroy() { },
  mounted() { 
    this.appForm.code = this.languageCode[this.appForm.codeLanguage];
    this.appForm.codeLanguage = this.nodeCode.settings.codeLanguage ? this.nodeCode.settings.codeLanguage : 'Python'
  },
  methods: {
    updateDataList(data) {
      const jsonData = JSON.parse(data)
      this.appForm.input = jsonData.dataList
      this.appForm.output = jsonData.configItems
      this.$emit("updateDataList", data);
    },
    onChangeLanguage() {
      this.appForm.code = this.languageCode[this.appForm.codeLanguage];
    },

  },
};
</script>

<style lang="scss" scoped>
.dialogCode {
  width: 100%;
  position: relative;
  margin-top: 12px;
}

.node-rig-tit {
  height: 60px;
  border-bottom: 1px solid #E5E6EA;
  font-family: MiSans, MiSans;
  font-weight: 500;
  font-size: 22px;
  color: #1D2129;
  line-height: 60px;
  text-align: left;
  font-style: normal;
  padding-left: 24px;
}

.code-cont {
  padding: 20px;
  position: relative;
  height: calc(100vh - 80px);
  background-color: #F7F8FA;

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

::v-deep .el-form {
  padding: 24px;
}

// .el-input--suffix .el-input__inner {
//   padding-right: 220px;
// }
.node-conten {
  padding: 24px 0 0 24px;

  li {
    margin-bottom: 24px;
  }

  
}
.node-mide {
    display: flex;
    justify-content: space-between;
    margin-bottom: 24px;
  }

  .node-text {
    height: 20px;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #1D2129;
    line-height: 20px;
    text-align: left;
    font-style: normal;
    text-transform: none;
    margin-bottom: 8px;
  }

  .node-left {
    width: 40px;
    height: 40px;
    background: #696FE2;
    border-radius: 2px;
    margin: 2px;
  }

  .rig {
    text-align: right;
    margin-right: 10px;
  }
.demo-ruleForm {
  ::v-deep .el-form-item__label {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #383d47;
    line-height: 32px;
  }
  ::v-deep .el-form-item {
    // margin-bottom: 16px;
    .el-form-item__content {
      width: 100%;
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
        width: 0px;
      }

      .el-tabs__content {
        height: calc(100vh - 175px);
        overflow: auto;
      }

      .plugin-bd[data-v-34b0738c] .el-tabs .el-tabs__item.is-active {
        color: #603eca;
      }

      .el-tabs__nav-wrap::after {
        width: 0px;
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
        background: #ffffff;
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
      // overflow-y: scroll;
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