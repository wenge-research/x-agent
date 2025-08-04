<template>
  <div class="Ec-x6-icon">
    <el-drawer title="" :visible.sync="drawerVisible" :modal="false" :modal-append-to-body="false"
      :direction="direction" v-if="drawerVisible" size="500px" style="
                position: absolute;
                width: 500px;
                box-sizing: border-box;
                right: 0;
                left: inherit;
            " :show-close="false">
      <div style="
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                ">
        <HeadTool v-if="drawerVisible" :label="sourceData.label" :imgWidth="24" :imgHeight="24" @remove="handleRemove"
          @copy="copyHandler" @input="inputHandler" @testNodes="openRunDrawer" imgSuffix="daimazhihang" />
        <div style="
                        width: 1px;
                        height: 20px;
                        background: #d3d9e6;
                        margin: 0 12px 12px;
                    "></div>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer"
          style="margin-bottom: 10px; cursor: pointer"></iconpark-icon>
      </div>
      <div class="sub">编写代码，处理输入变量来生成返回值</div>
      <div class="drawer-content">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="0">
            <template slot="title">
              <div class="title-flex">
                <span>{{ $t('input') }}</span>
                <div class="icon-ctn">
                  <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'输入代码运行时所需的变量，可在代码中引用'" placement="top" :effect="'light'">
                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                  </el-tooltip>
                </div>
              </div>
            </template>
            <inputList v-if="drawerVisible" :inputs="appForm.inputs" @updateInputList="updateInputList"
              :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList>
          </el-collapse-item>
          <el-collapse-item name="1" title="代码">
            <div class="content">
              <div class="select-app">
                <el-select v-model="appForm.language" size="mini" :placeholder="$t('selectPlaceholder')"
                  style="width: 120px; float: right" @change="onChangeLanguage">
                  <el-option label="JavaScript" value="JavaScript">
                  </el-option>
                  <el-option label="Python" value="Python">
                  </el-option>
                </el-select>
              </div>
              <codemirror v-model="appForm.code" :options="options" class="codemirror-editor"></codemirror>
              <el-button type="text" class="edit-button" @click="openLargeEditor">
                <iconpark-icon color="#1c50fd" size="22" name="expand-diagonal-line"></iconpark-icon>
              </el-button>
            </div>
          </el-collapse-item>
          <el-collapse-item name="2" :title="$t('output')">
            <div class="tip-cont"><span class="tip">变量名</span><span>变量类型</span></div>
            <ConfigJson :configItems="configItems" @updateConfig="updateConfig" />
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-drawer>

    <!-- 大型编辑区域 -->
    <div v-if="largeEditorVisible" class="large-editor-container">
      <div class="large-editor-header">
        <span>代码编辑</span>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeLargeEditor"
          style="margin-bottom: 10px; cursor: pointer"></iconpark-icon>
      </div>
      <codemirror v-model="appForm.code" :options="options" class="large-codemirror-editor"></codemirror>
    </div>
  </div>
</template>

<script>
  // mixins
  import drawerMixins from "./drawerMixins";
  import ConfigJson from './ConfigJson.vue';
  // 文件内引入
  // 引入样式、主题、代码风格等配置或样式文件
  export default {
    props: ["panels", "params"],
    name: "CustomDrawer",
    components: {
      ConfigJson,
    },
    mixins: [drawerMixins],
    data () {
      return {
        configItems: [{ "name": "result", "type": "array[string]", "level": 0, "children": [], expanded: true }],
        startConfigItems: [{ "name": "result", "type": "array[string]", "level": 0, "children": [], expanded: true }],
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
          language: "JavaScript",
          code: "",
        },
        drawerVisible: false,
        largeEditorVisible: false,
        languageCode: {
          JavaScript: "// 在这里，您可以通过 自定义参数名  获取节点中的输入变量，并通过 'result' 输出结果\nfunction main(arg1, arg2) {\n    // 构建输出对象\n    const result = arg1 + arg2; // 拼接 arg1 和 arg2 的值\n    return { 'result': result };\n}",
          Python: "# 您可以通过自定义参数名,需要与输入的参数名对应\n# 通过返回一个字典,作为代码节点的输出结果\n# 下面是一个示例\ndef main(arg1: str, arg2: str) -> str:\n    return {\n        'result': arg1 + arg2\n    }",
        },
        startAppForm: {
          inputs: [],
          language: "JavaScript",
          code: "",
        },
      };
    },
    watch: {
      // 监听 configItems 数组的变化
      drawerVisible: {
        handler (newVal, oldVal) {
          if(!newVal){
            this.closeLargeEditor()
          }
        },
        deep: true,
      },
      configItems: {
        handler (newVal, oldVal) {
          // 当 configItems 发生变化时，通知父组件更新
          this.$emit(
            "updateCellData",
            { configItems: JSON.stringify(newVal) },
            this.curNode
          );
        },
        deep: true,
      },
      'appForm.code' (newValue) {
        if (newValue === '') {
          this.appForm.code = this.languageCode[this.appForm.language];
        }
      },
    },
    mounted () {
      if (this.appForm.code === '') {
        this.appForm.code = this.languageCode[this.appForm.language];
      }
    },

    methods: {
      initDrawer() {
          let node = this.$store.state.workflow.editNode;
          let parentNodes = this.$store.state.workflow.parentNodes;
          this.nodeStoreData = node.store.data.data;
          this.appForm = this.nodeStoreData.appForm
              ? JSON.parse(this.nodeStoreData.appForm)
              : JSON.parse(JSON.stringify(this.startAppForm));
          this.configItems = this.nodeStoreData.configItems
              ? JSON.parse(this.nodeStoreData.configItems)
              : JSON.parse(JSON.stringify(this.startConfigItems));
          this.inputKey++
          this.curNode = node;
          this.parentNodes = parentNodes;
          this.drawerVisible = true;
          if(this.curNode._parent){
              let curParentNodesData = this.curNode._parent.store.data.data
              let inputs = JSON.parse(this.curNode._parent.store.data.data.inputs)
              inputs = inputs.map(item => {
                return {
                  ...item,
                  pid: this.curNode._parent.id,
                  name: curParentNodesData.label
                }
              })
              this.curParentNodeInputs = inputs
            }
      },
      onChangeLanguage() {
        this.appForm.code = this.languageCode[this.appForm.language];
      },
      addJson() {
        this.configItems.push({ "name": "result", "type": "array[string]", "level": 0, "children": [], expanded: true });
      },
      updateConfig(updatedConfig) {
        this.configItems = updatedConfig;
      },
      inputBlur() {
        this.$EventBus.$emit("saveWorkflow");
      },
      openLargeEditor() {
        this.largeEditorVisible = !this.largeEditorVisible;
      },
      closeLargeEditor() {
        this.largeEditorVisible = false;
      },
    },
  };
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style lang="scss" scoped>
  .Ec-x6-icon{
    ::v-deep .vue-codemirror{
      .cm-s-rubyblue span.cm-comment {
          font-style: normal;
      }
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
    top: 0;
    right: 50px;
  }

  .tip-cont {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 12px;
    color: #828894;
    line-height: 16px;
    padding-left: 20px;

    span.tip {
      display: inline-block;
      width: 230px;
    }
  }
  .codemirror-container {
    position: relative;
  }

  .codemirror-editor {
    width: 100%;
    height: 300px; /* 根据需要调整高度 */
  }

  .edit-button {
    position: absolute;
    top: 0px;
    right: 16px;
    z-index: 10;
  }

  .large-editor-container {
    position: fixed;
    top: 88px;
    right: 503px;
    width: 800px;
    height: calc(100% - 90px);
    padding-bottom: 50px;
    background-color: white;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    z-index: 1000;
    display: flex;
    flex-direction: column;
  }

  .large-editor-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #d3d9e6;
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
  
.title-flex{
  display: flex;
  align-items: center;
  height: 20px;

  span{
    display: inline-block;
    height: 20px;
    line-height: 20px;
  }

  .icon-ctn{
    height: 16px;
    line-height: 19px;
    position: relative;

  }
}
</style>

