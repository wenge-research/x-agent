<template>
    <div>
      <el-drawer
        title=""
        :visible.sync="drawerVisible"
        :modal="true"
        :modal-append-to-body="true"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :direction="direction"
        size="960px"
        :append-to-body="true"
        :wrapper-closable="false"
        style="
          position: absolute;
          width: 960px;
          box-sizing: border-box;
          right: 0;
          left: inherit;
          z-index: 3001;
        "
        :show-close="false"
        :withHeader="false"
      >
        <div class="vocabularyDrawer-Box" >
          <div class="magnifiedEditBox-header" >
            <div>
              <span>提示词库</span>
            </div>
            <iconpark-icon name="close-line" size="18" color="#828894" 
            @click.stop="closeDrawerFn" style="margin-bottom: 10px;cursor: pointer"></iconpark-icon>
          </div>
          <!-- 抽屉内容 -->
          <div class="vocabularyDrawer-Con-Box" >
            
            <!-- 头部 -->
            <!-- <div class="vocabularyDrawer-Con-Box-header" >
              <el-row>
                <el-col :span="6">
                  <div class="vocabularyDrawer-header-tabs" >
               
                    <div 
                      v-for="(item,index) in ['全部','我的']"
                      :class="{'vocabularyDrawer-header-tabs-active':vocabularyDrawerHeaderTabsActive === item}" 
                      @click="vocabularyDrawerHeaderTabsFn(item)"
                      :key="index"
                    >{{item}}</div>
                  </div>
                </el-col>
                <el-col :span="6" :offset="12" >
                  <el-input v-model="searchValue" placeholder="请输入" @keyup.enter.native="getPromptConfigList" >
                    <i slot="prefix" class="el-input__icon el-icon-search"></i>
                  </el-input>
                </el-col>
              </el-row>
            </div> -->
            
            <!-- 内容 -->
            <div class="vocabularyDrawer-Con-Box-Cont">
              <el-row  style="height: 100%;" >
                <el-col :span="9" style="height: 100%;" v-loading="vocabularyListLoding">
                  <!-- 词典库选项列表 -->
                  <div class="vocabularyList"  >
                    <div :class="{vocabularyItem:true, vocabularyItemActive:vocabularyListActiveIndex === index}" 
                      v-for="(item, index) in vocabularyListData" :key="index" 
                      @click="vocabularyItemFn(item,index)"   
                    >
                      <p>{{item.promptTitle}}</p>
                      <p>{{item.remark}}</p>
                    </div>
                  </div>
                </el-col>
                <el-col :span="15" style="height: 100%;" >
                  <div class="vocabularyContent" >
                    <div style="white-space:pre-wrap;line-height: 32px;" v-html="vocabularyContent" ></div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
          <div class="vocabularyDrawer-footer" >
            <el-button @click="copyVocabulary" >复制提示词</el-button>
            <el-button type="primary" @click.stop="insertVocabularyFn" >插入提示词</el-button>
          </div>
        </div>
      </el-drawer>
    </div>
</template>

<script>
/**
 * 提示词库抽屉
 * 1. 获取提示词库列表
 * 2. 相关方法：   insertVocabulary 插入提示词
 */ 


import { promptConfigGetPromptConfigList } from '@/api/workflow.js'

export default {
  name: "vocabularyDrawer",
  data() {
    return {
      vocabularyListLoding: false,
      drawerVisible: false,
      direction: 'rtl',
      activeName: '全部',
      searchValue: '',
      vocabularyListData: [],
      vocabularyListActiveIndex: 0,
      vocabularyDrawerHeaderTabsActive: '全部',
      promptConfigList: [],
      vocabularyContent: ''
    }
  },
  mounted() {
    // this.getPromptConfigList();
    this.vocabularyListData = [{
      content: `#请根据上面问题
#角色
你是一个回复打分专家，擅长对用户传入的问题和答案判断结果是否正确进行打分
#输入
{{session_id}}: 代表提问的问题编号
{{query_id}}: 代表对话轮次，一个问题可以进行多轮问答
{{input}}: 代表用户输入的问题
{{reference_output}}: 代表预期输出的答案
#流程
1.问题理解：对用户输入{{input}}进行语义理解，精准把握其核心意图
2.答案分析：分析预期输出{{reference_output}}，核验其内容是否清晰呈现问题的答案或解决方案
3.对比分析：将实际输出{{output}}与预期输出{{reference_output}}进行对比打分，确认是否精准回应问题的核心意图，以及与预期输出是否一致或接近
#输出
生成评分{{score}}和评分理由{{score_reason}},评分100分为满分，答案越匹配则分值越高`,
      promptTitle: "默认提示词",
      remark: ""
    }]
    this.synchronousContent();
  },
  beforeDestroy() {
    // 组件销毁前移除事件监听
  },
  watch: {
    'vocabularyListActiveIndex':{
      handler(newVal, oldVal) {
        this.synchronousContent();
      },
    }
  },
  methods: {
    openDarwer(){
      this.drawerVisible = true;
    },
    closeDrawerFn(){
      this.drawerVisible = false;
    },
    // 防抖
    debounce() {
      let biaozhun = sessionStorage.getItem('vocabularyDrawerDebounce');
      let nowTime = Date.now();
      if(biaozhun){
        sessionStorage.setItem('vocabularyDrawerDebounce', nowTime);  
        return true;
      }
      
      if(nowTime - biaozhun > 1000){
        sessionStorage.setItem('vocabularyDrawerDebounce', nowTime);  
        return true;
      }
      return false;
      
    },
    synchronousContent(){
      let item = this.vocabularyListData[this.vocabularyListActiveIndex];
      this.vocabularyContent = item? item.content : '';
    },
    removeHtmlTags(html) {
        // 创建一个临时的DOM元素
        const tempElement = document.createElement('div');
        // 将HTML字符串设置为元素的内容
        tempElement.innerHTML = html;
        // 提取并返回文本内容
        return tempElement.textContent || tempElement.innerText || '';
    },
    vocabularyDrawerHeaderTabsFn(item){
      this.vocabularyDrawerHeaderTabsActive = item;
      this.vocabularyListActiveIndex = 0;
      this.getPromptConfigList()
      
    },
    async getPromptConfigList() {
      let type = this.vocabularyDrawerHeaderTabsActive === '全部' ? null : 'user';
      let params = {
        createUser:type,
        promptTitle:this.searchValue,
        pageNo:1,
        pageSize:1000
      }
      this.vocabularyListLoding = true;
      const res = await promptConfigGetPromptConfigList(params);
      this.vocabularyListLoding = false;
      if (res.code == "000000") {
        this.vocabularyListData = res.data.records ;
      }   
      this.synchronousContent();
    },
    vocabularyItemFn(item,index){
      this.vocabularyListActiveIndex = index
    },
    // 插入提示词
    insertVocabularyFn(){
      let text = this.removeHtmlTags(this.vocabularyContent);
      this.$emit("insertVocabulary", text);
      this.closeDrawerFn();
    },
    // 复制提示词
    async copyVocabulary(){
      // try {
      //   await navigator.clipboard.writeText(this.vocabularyContent);
      //   this.$message.success('复制成功！');
      // } catch (err) {
      //   this.$message.error('复制失败');
      // }

    
      var input = document.createElement("input"); // 创建input对象
      input.value = this.vocabularyContent; // 设置复制内容
      document.body.appendChild(input); // 添加临时实例
      input.select(); // 选择实例内容
      document.execCommand("Copy"); // 执行复制
      document.body.removeChild(input); // 删除临时实例
      this.$message.success('复制成功！');


    }
  }
}
</script>

<style lang="scss" src="../../workflowConfig/dragDemo/components/nodeTheme/node.scss" scoped></style>
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

.magnifiedEditBox-header{
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 40px;
  margin-bottom: 16px;
}


.vocabularyDrawer-Box{
  padding: 25px;
  display: flex;
  flex-direction: column;
  height: 100%;
  .vocabularyDrawer-Con-Box{
    display: flex;
    flex-direction: column;
    flex: 1;
    margin-bottom: 24px;
    .vocabularyDrawer-Con-Box-Cont{
      height: calc(100vh - 210px);
    }
    .vocabularyDrawer-Con-Box-header{
      margin-bottom: 16px;
      .vocabularyDrawer-header-tabs{
        display: flex;
        align-items: center;
        div{
          cursor: pointer;
          padding: 0 12px;
          font-size: 18px;
          color: #828894;
          line-height: 28px;
        }
        .vocabularyDrawer-header-tabs-active{
          color: #603ECA;
        }
      }
    }
    .vocabularyList{
      height: 100%;
      overflow-y: auto;
      .vocabularyItem{
        box-sizing: border-box;
        padding: 10px;
        border: 1px solid #D5D8DE;
        border-radius: 4px;
        width: 320px;
        height: 80px;
        padding: 10px 16px;
        margin-bottom: 12px;
        cursor: pointer;
        user-select: none;
        p:nth-child(1){
          font-size: 16px;
          color: #494E57;
          line-height: 32px;
        }
        p:nth-child(2){
          font-size: 14px;
          color: #828894;
          line-height: 16px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
      .vocabularyItemActive{
        border: 1px solid #1747E5;
        background-color: #f3f6ff;
      }
    }
    .vocabularyContent{
      height: 100%;
      overflow: auto;
      background-color: #F2F4F7;
      box-sizing: border-box;
      padding: 16px;
      
    }
  }
  .vocabularyDrawer-footer{
    display: flex;
    justify-content: flex-end;
  }
}



</style>
