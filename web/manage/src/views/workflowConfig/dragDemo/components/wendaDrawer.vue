<template>
  <div class="Ec-x6-icon" @click="handleDrawerClick">
    <el-drawer
      title=""
      :visible.sync="drawerVisible"
      :modal="false"
      :modal-append-to-body="false"
      :direction="direction"
      v-if="drawerVisible"
      size="500px"
      style="
        position: absolute;
        width: 500px;
        box-sizing: border-box;
        right: 0;
        left: inherit;
      "
      :show-close="false"
      :before-close="handleClose"
    >
      <div style="display: flex;align-items: center;justify-content: space-between">
        <HeadTool
          v-if="drawerVisible"
          :label="sourceData.label"
          :imgWidth="24"
          :imgHeight="24"
          @remove="handleRemove"
          @copy="copyHandler"
          @input="inputHandler"
          @testNodes="openRunDrawer"
          imgSuffix="wenda"
        />
        <div style="width: 1px;height: 20px;background: #D3D9E6;margin: 0 12px 12px;"></div>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer" style="margin-bottom: 10px;cursor: pointer"></iconpark-icon>
      </div>
      <div class="sub">支持中间向用户提问问题,支持预置选项提问和开放式问题提问两种方式</div>
      <div class="drawer-content">
        <el-collapse v-model="activeNames" >
          <el-collapse-item name="0" :title="$t('model')">
            <modelSelect v-if="drawerVisible" :key="appForm.modelId" :id="appForm.modelId" @change="modelChange"></modelSelect>
          </el-collapse-item>
          <el-collapse-item name="1" :title="$t('input')">
            <el-checkbox v-model="appForm.historyFlag" style=" position: absolute; right: 20px; top: 12px; " @change="checkChange">对话历史</el-checkbox>
            <inputList  v-if="drawerVisible" :inputs="appForm.inputs"
                            :key="inputKey" @updateInputList="updateInputList" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList>
          </el-collapse-item>
          <el-collapse-item name="2" >
            <template #title>
              <div class="systemPromptTitle">
                <div>系统提示词</div>
                <div>
                  <span>
                    <iconpark-icon name="variable" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                  </span>
                  <span @click.stop="openVocabularyDrawer">
                    <iconpark-icon name="pages-line" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                  </span>
                  <span @click.stop="openMagnifiedEditBox">
                    <iconpark-icon name="expand-diagonal-line" size="18" color="#828894" style="cursor: pointer"></iconpark-icon>
                    <!-- <el-button type="text" size="small" @click.stop="openMagnifiedEditBox">放大</el-button> -->
                  </span>
                </div>
              </div>
            </template>
            <el-input
              v-model="appForm.systemPrompt"
              type="textarea"
              :rows="3"
              @blur="inputBlur"
              @keydown.delete="handleKeyDown"
              :disabled="magnifiedBoxVisible"
            ></el-input>
          </el-collapse-item>
          <el-collapse-item name="3" :title="$t('userPrompt')">
            <el-input
              v-model="appForm.userPrompt"
              type="textarea"
              :rows="3"
              @blur="inputBlur"
              @keydown.native="handleKeyDown"
            ></el-input>
          </el-collapse-item>
          <el-collapse-item name="4" :title="$t('outputVariable')">
            <div class="result-cont">text<span>string</span></div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-drawer>
    <!-- 放大框 -->
    <magnifiedEditBox v-if="drawerVisible" ref="magnifiedEditBox" @input="handleInput"  @openVocabularyDrawer="openVocabularyDrawer(editName)" @update:visible="handleMagnifiedBoxVisibleChange" />
    
    <vocabularyDrawer @insertVocabulary="insertVocabularyFn" />
  </div>
</template>

<script>
// mixins
import drawerMixins from './drawerMixins'
import magnifiedEditBox from './magnifiedEditBox'
import vocabularyDrawer from './vocabularyDrawer'
import modelSelect from "@/components/ModelSelect.vue";
export default {
  name: "modelDrawer",
  mixins: [drawerMixins],
  components: { magnifiedEditBox, vocabularyDrawer, modelSelect },
  data() {
    return {
      magnifiedBoxVisible: false,
      appForm: {
        historyFlag:false,
        userPrompt: "",
        systemPrompt: "",
        introduce: "",
        modelId: "",
        modelAnswerFlag: true,
        ocrFlag: true,
        videoFlag: true,
        multiDialogueFlag: true,
        rewritingFlag: true,
        contentScore: 1.76,
      },
      activeNames: ["0", "1", "2", "3"],
    };
  },
  mounted() {
  },
  beforeDestroy() {},
  methods: {

    // 打开提示词库抽屉
    openVocabularyDrawer() {
      this.$EventBus.$emit("openVocabularyDrawer");
    },
    // 插入提示词
    insertVocabularyFn(vocabularyContent) {
      let con = ''
      if(this.appForm.systemPrompt == undefined){
        con = vocabularyContent
      }else {
        con = this.appForm.systemPrompt + vocabularyContent
      }
      // 插入到当前内容
      this.$set(this.appForm, 'systemPrompt', con);
      // 赋值到放大框
      this.$refs.magnifiedEditBox.setDataText(this.appForm.systemPrompt);
    },
    // 打开提问词放大框
    openMagnifiedEditBox() {
      // 通过事件总线触发打开事件
      this.$EventBus.$emit("openMagnifiedEditBox",this.appForm.systemPrompt);
    },
    handleMagnifiedBoxVisibleChange(visible) {
      this.magnifiedBoxVisible = visible;
    },
    inputBlur() {
        this.$EventBus.$emit("saveWorkflow");
    },
    onChange() {
          this.$EventBus.$emit("saveWorkflow");
    },
    switchChange() {
          this.$EventBus.$emit("saveWorkflow");
    },
    checkChange() {
          this.$EventBus.$emit("saveWorkflow");
    },
    sliderChange() {
          this.$EventBus.$emit("saveWorkflow");
    },
    handleKeyDown(event) {
      consoe.log("event", event)
      event.preventDefault()
    }
  },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style lang="scss" scoped>
.systemPromptTitle{
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  div{
    >span{
      display: inline-block;
      margin-left: 10px;
    }
  }
}
</style>
