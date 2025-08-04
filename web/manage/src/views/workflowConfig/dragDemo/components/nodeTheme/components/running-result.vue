<template>
  <transition name="el-zoom-in-top">
    <div class="running-result">
      <div class="running-result-head">
        <span>运行结果</span>
        <iconpark-icon
          name="close-line"
          color="#828894"
          size="16"
          style="cursor: pointer"
          @click.stop="closeRunningResult"
        ></iconpark-icon>
      </div>
      <div v-if="outputJsonData&&outputJsonData.iterationNum>=0" style="margin-top: 15px;">
        <el-pagination
        background
        :pager-count="iterationList.length<=7?7:5"
        :current-page.sync="pageNo"
        :layout="iterationList.length<=7?'pager':'prev, pager, next'"
        :page-size="1"
        :total="iterationList.length">
      </el-pagination>
      </div>
      
      
      <div class="running-result-content" v-if="outputJsonData&&outputJsonData.iterationNum>=0">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="0" v-if="iterationList[pageNo-1]?.elapsedTime" title="耗时">
            <div class="running-result-content-output">
              <div class="error">{{ iterationList[pageNo-1]?.elapsedTime }}ms</div>
            </div>
          </el-collapse-item>
          <el-collapse-item name="1"  v-if="iterationList[pageNo-1]?.errorMessage" title="错误信息">
            <div class="running-result-content-output">
              <div class="error">{{ iterationList[pageNo-1]?.errorMessage }}</div>
            </div>
          </el-collapse-item>
          <!-- 输入 -->
          <el-collapse-item v-if="inputList" name="2" title="输入">
            <div
              v-for="(item, index) in inputList"
              :key="index"
              class="running-result-content-input"
            >
              <div class="label">{{ item.type }}</div>
              <div class="value">{{ item.value }}</div>
            </div>
          </el-collapse-item>
          <!-- 输出 -->
          <el-collapse-item name="3">
            <template slot="title">
              <div class="systemPromptTitle">
                <div class="title-flex">
                  <span>输出</span>
                </div>
                <div class="copy-icon" @click="cpoyText(JSON.stringify(iterationList[pageNo-1]?.output))">
                  <iconpark-icon name="file-copy-line"></iconpark-icon>
                </div>
              </div>
            </template>
            <div class="running-result-content-output">
              <div class="error">{{ iterationList[pageNo-1]?.output }}</div>
            </div>
          </el-collapse-item>
          
        </el-collapse>
      </div>
      <div class="running-result-content" v-else>
        <el-collapse v-model="activeNames">
          <el-collapse-item name="0" v-if="outputJsonData?.elapsedTime" title="耗时">
            <div class="running-result-content-output">
              <div class="error">{{ outputJsonData?.elapsedTime }}ms</div>
            </div>
          </el-collapse-item>
          <el-collapse-item name="1"  v-if="outputJsonData?.errorMessage" title="错误信息">
            <div class="running-result-content-output">
              <div class="error">{{ outputJsonData?.errorMessage }}</div>
            </div>
          </el-collapse-item>
          <!-- 输入 -->
          <el-collapse-item v-if="inputList" name="2" title="输入">
            <div
              v-for="(item, index) in inputList"
              :key="index"
              class="running-result-content-input"
            >
              <div class="label">{{ item.type }}</div>
              <div class="value">{{ item.value }}</div>
            </div>
          </el-collapse-item>
          <!-- 输出 -->
          <el-collapse-item name="3">
            <template slot="title">
              <div class="systemPromptTitle">
                <div class="title-flex">
                  <span>输出</span>
                </div>
                <div class="copy-icon" @click="cpoyText(JSON.stringify(outputJsonData?.output))">
                  <iconpark-icon name="file-copy-line"></iconpark-icon>
                </div>
              </div>
            </template>
            <div class="running-result-content-output">
              <div class="error">{{ outputJsonData?.output }}</div>
            </div>
          </el-collapse-item>
          
        </el-collapse>
      </div>
    </div>
  </transition>
</template>

<script>

export default {
  props: {
    inputList: {
      type: [Array, Object],
    },
	outputJsonData: {
	  type: Object,
	  default: {},
	}
  },
  data() {
    return {
      activeNames: ["0", "1", "2", "3"],
      // outputJsonData: null
      iterationList:[],
      pageNo:1
    };
  },
  mounted() {
    // this.$EventBus.$on("apiEndingOutput", (outputJsonData) => {
    //   this.outputJsonData = outputJsonData;
    // });
    this.$EventBus.$on("sendIterationList", (data) => {
      if(data&&Array.isArray(data)){
        this.iterationList = [...data];
      }else{
        this.iterationList = [];
      }
      // this.$forceUpdate()
    });
  },
  methods: {
    closeRunningResult() {
      this.$emit("closeRunningResult");
    },
    exeCommandCopyText(text) {
      try {
        const t = document.createElement('textarea')
        t.nodeValue = text
        t.value = text
        document.body.appendChild(t)
        t.select()
        document.execCommand('copy')
        document.body.removeChild(t)
        return true
      } catch (e) {
        console.log(e)
        return false
      }
    },
    cpoyText(content) {
      this.exeCommandCopyText(content)
      this.$message({
        message: '复制成功',
        type: 'success',
      })
    },
  }
};
</script>
<style lang="scss" scoped>
.running-result {
  position: absolute;
  top: 0;
  right: -408px;
  width: 400px;
  background: #ffffff;
  box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.12);
  border-radius: 4px;
  padding: 16px;

  &-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  &-content {
    margin-top: 16px;
    iconpark-icon {
      margin-right: 4px;
    }
    &-input {
      display: flex;
      align-items: center;
      justify-content: space-between;
      .label {
        font-size: 14px;
        color: #828894;
      }
      .value {
        font-size: 14px;
        color: #383d47;
      }
    }
    &-output {
      .error {
        font-size: 14px;
        color: #828894;
      }
    }
    &-input,
    &-output {
      padding-left: 20px;
    }
  }
  :deep(.el-collapse) {
    border: none;
  }
  :deep(.el-collapse-item) {
    margin-bottom: 8px;
  }
  :deep(.el-collapse-item__wrap) {
    border-bottom: none;
  }
  :deep(.el-collapse-item__arrow) {
    // display: none;
    position: absolute;
    left: 16px;
    top: 16px;
  }
  :deep(.el-collapse-item__header) {
    border-bottom: none;
    background: #f7f9fc;
    border-radius: 4px;
    padding: 16px 16px 16px 36px;
    position: relative;
  }
  :deep(.el-collapse-item__header.is-active) {
    padding: 16px 16px 16px 36px;
    border-radius: 8px 8px 0 0;
  }
  :deep(.el-collapse-item__content) {
    padding-bottom: 16px;
    background: #f7f9fc;
    border-radius: 4px;
    padding: 0 16px 16px 16px;
  }
  :deep(.el-collapse-item__content, .is-active) {
    border-radius: 0 0 8px 8px;
  }
  :deep(.el-pagination){
    display: flex;
    justify-content: flex-start;
    padding: 0;
    box-sizing: border-box;

    .btn-prev{
      margin: 0;
      margin-right: 10px;
    }
    .btn-next{
      margin: 0;
      margin-left: 10px;
    }

    .el-pager{
      display: flex;
      gap: 10px;
    }

    .el-pager li{
      margin: 0;
    }
  }

  :deep(.el-pager li.active){
    color: #409EFF !important;
    border: 1px solid #409EFF;
    cursor: default;
    background-color: transparent !important;
    font-weight: bold;
    font-size: 16px;
  }
  .systemPromptTitle{
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  div{
    >span{
      display: inline-block;
    }
  }

  .copy-icon{
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
}
}
</style>
