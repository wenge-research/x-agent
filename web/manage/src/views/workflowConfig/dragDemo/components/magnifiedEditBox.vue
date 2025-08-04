<template>
  <div class="magnifiedEdit-drawer">
      <!-- 工作流编辑 放大的编辑框 -->
      <el-drawer
      title=""
      :visible.sync="drawerVisible"
      :modal="false"
      :modal-append-to-body="false"
      :direction="direction"
      v-if="drawerVisible"
      size="500px"
      :style="{
          position: 'absolute',
          width: width,
          boxSizing: 'border-box',
          right: right,
          left: 'inherit',
      }"
      :show-close="false"
      @close="onClose"
      >   
      <div class="magnifiedEditBox">
          <div class="magnifiedEditBox-header" >
            <div>
              <span>输入</span>
              <!-- <span class="autoOptimization">
                  <svg class="icon" aria-hidden="true">
                      <use xlink:href="#icon-AIshengcheng"></use>
                  </svg>
                  <span>自动优化</span>
              </span> -->
              <span class="txt-box" style="cursor: pointer" @click.stop="openVocabularyDrawer" >
                  <iconpark-icon name="pages-line" size="18" title="提示词库" color="#828894" ></iconpark-icon>
                  <span >提示词库</span>
              </span>
            </div>
            <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer" style="margin-bottom: 10px;cursor: pointer"></iconpark-icon>
          </div>
          <div class="magnifiedEditBox-content">
              <el-input
                  class="magnifiedEditBox-content-textarea"
                  type="textarea"
                  v-model="dataText"
                  @blur="inputBlur"
                  @keydown.delete="handleKeyDown"
                  resize="none"
              ></el-input>
          </div>
      </div>
      </el-drawer>
  </div>
</template>
<script>
import drawerMixins from './drawerMixins'
export default {
  name: "magnifiedEditBox",
  mixins: [drawerMixins],
  components: {
  },
  props: {
      width: {
          type: String,
          default: '796px'
      },
      right: {
          type: String,
          default: '505px'
      }
  },
  data() {
      return {
          dataText: "",
          promptConfigList: []
      }
  },
  watch: {
    dataText(val) {
      this.$emit('input', val);
    },
  },
  mounted() {
    
  },
  beforeDestroy() {
  },
  methods: {
      // 赋值数据
    setDataText(content){
        this.dataText = content;
    },
    openDarwer(data){
      this.drawerVisible = true;
      this.dataText = data;
      this.$emit('update:visible', true);
    },
    openVocabularyDrawer(){
        this.$emit("openVocabularyDrawer");
    },
    insertVocabulary(vocabularyContent) {
      this.dataText += vocabularyContent;
    },
    closeDrawer() {
      this.drawerVisible = false;
      this.$emit('update:visible', false);
    },
    onClose() {
      this.closeDrawer();
    },
    inputBlur() {
      this.$EventBus.$emit("saveWorkflow");
    },
    handleKeyDown(event) {
      event.preventDefault()
    }
  }
}
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style lang="scss" scoped>
.magnifiedEditBox{
  height: 100%;
  display: flex;
  flex-direction: column;
  .magnifiedEditBox-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 40px;
      div{
          display: flex;
          >span{
              margin-right: 30px;
          }
          >span:nth-child(1){
              font-size: 16px;
              font-weight: 600;
          }
          .autoOptimization{
              cursor: pointer;
              span{
                  color: #1747E5;
                  margin-left: 5px;
              }
          }
          .txt-box{
              margin-top: -2px;
              display: flex;
              align-items: center;
              span{
                  margin-left: 5px;
              }
          }
      }
  }
  .magnifiedEditBox-content {
      height: 100%;
  }
}

.magnifiedEditBox-content-textarea{
  height: 100%;
}
::v-deep .magnifiedEditBox-content-textarea .el-textarea__inner{
  height: 100%;
}

</style>