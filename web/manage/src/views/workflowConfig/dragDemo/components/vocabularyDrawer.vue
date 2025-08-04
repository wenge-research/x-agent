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
          border-radius: 12px 0 0 12px;
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
            <div class="vocabularyDrawer-Con-Box-header" >
              <el-row>
                <el-col :span="6">
                  <div class="vocabularyDrawer-header-tabs" >
                    <!-- <div :class="{'vocabularyDrawer-header-tabs-active':vocabularyDrawerHeaderTabsActive === '全部'}" @click="vocabularyDrawerHeaderTabsActive = '全部'">全部</div>
                    <div :class="{'vocabularyDrawer-header-tabs-active':vocabularyDrawerHeaderTabsActive === '我的'}" @click="vocabularyDrawerHeaderTabsActive = '我的'">我的</div> -->
                    <div 
                      v-for="(item,index) in ownerList"
                      :class="{'vocabularyDrawer-header-tabs-active':vocabularyDrawerHeaderTabsActive === item.value}" 
                      @click="vocabularyDrawerHeaderTabsFn(item.value)"
                      :key="index"
                    >{{item.label}}</div>
                  </div>
                </el-col>
                <el-col :span="18">
                  <div class="flex" style="gap: 12px;justify-content: flex-end; ">
                    <el-input v-model="searchValue" placeholder="请输入" @keyup.enter.native="getPromptConfigList" style="width: 300px;">
                    <i slot="prefix" class="el-input__icon el-icon-search" style="color: #1d2129;"></i>
                    </el-input>
                    <div class="btn-add" @click="addPromptDialog">
                      <div class="icon-ctn">
                        <iconpark-icon name="add-line" size="16" color="#ffffff" ></iconpark-icon>
                      </div>
                      <div class="btn-word">
                        {{$t("added")}}
                      </div>
                    </div>
                  </div>
                  
                </el-col>
              </el-row>
            </div>
            
            <!-- 内容 -->
            <div class="vocabularyDrawer-Con-Box-Cont">
              <el-row  style="height: 100%;" v-if="vocabularyListData.length">
                <el-col :span="9" style="height: 100%;" v-loading="vocabularyListLoding">
                  <!-- 词典库选项列表 -->
                  <div class="vocabularyList">
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
                    <div v-html="vocabularyContent" ></div>
                  </div>
                </el-col>
              </el-row>
              <el-row v-loading="vocabularyListLoding" style="height: 100%;" v-else>
                <div class="empty-data-ctn">
                  <div class="empty-data">
                    <img src="@/assets/images/empty_data.png" alt="">
                    <p>暂无提示词</p>
                  </div>
                </div>
              </el-row>
              
            </div>
          </div>
          <div class="vocabularyDrawer-footer" >
            <el-button @click="copyVocabulary" >复制提示词</el-button>
            <el-button type="primary" @click.stop="insertVocabularyFn" >插入提示词</el-button>
          </div>
        </div>
      </el-drawer>

      <!-- 新增提示词 -->
      <el-dialog :title="$t('createPrompt')" :visible.sync="createPromptVisible" width="30%" append-to-body :before-close="()=>handleClose('ruleForm')">
        <el-form :model="formPromptData" ref="ruleForm" class="demo-form-inline" :rules="rule">
          <el-form-item :label="$t('promptName')" prop="promptTitle">
            <el-input v-model="formPromptData.promptTitle" :placeholder="$t('inputPlaceholder')"></el-input>
          </el-form-item>
          <el-form-item :label="$t('promptDes')">
            <el-input type="textarea" maxlength="200" v-model="formPromptData.remark" :placeholder="$t('inputPlaceholder')"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button plain @click="handleClose('ruleForm')" :loading="addLoading">{{ $t('cancel') }}</el-button>
          <el-button type="primary" @click="onSubmit('ruleForm')" :loading="addLoading">{{$t('confirm')}}</el-button>
        </div>
      </el-dialog>
    </div>
</template>

<script>
/**
 * 提示词库抽屉
 * 1. 获取提示词库列表
 * 2. 相关方法：   insertVocabulary 插入提示词
 */ 


import { promptConfigGetPromptConfigList ,promptConfigAddPromptConfig} from '@/api/workflow.js'
import { createEditor } from '@wangeditor/editor';

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
      vocabularyDrawerHeaderTabsActive: 'official',
      promptConfigList: [],
      vocabularyContent: '',
      ownerList:[
        {
          label:"推荐",
          value:"official"
        },
        {
          label:"我的",
          value:"personal"
        }
      ],
      // 新增提示词
      formPromptData: {
        promptTitle: "",
        remark: ""
      },
      addLoading:false,
      rule: {
        promptTitle: [
          { required: true, message: '请输入提示词名称', trigger: 'change' }
        ],
      },
      createPromptVisible:false
    }
  },
  mounted() {
    this.getPromptConfigList();
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
      // let type = this.vocabularyDrawerHeaderTabsActive === '全部' ? null : 'user';
      // let ownerType=this.vocabularyDrawerHeaderTabsActive === '全部' ? 'official' : 'personal';
      let params = {
        // createUser:type,
        ownerType:this.vocabularyDrawerHeaderTabsActive,
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

    
      try {
        // 创建一个临时容器用于解析富文本
        const tempContainer = document.createElement('div');
        tempContainer.innerHTML = this.vocabularyContent;
        
        // 将容器添加到DOM中（必须在DOM中才能正确复制格式）
        tempContainer.style.position = 'fixed';
        tempContainer.style.opacity = '0';
        tempContainer.style.pointerEvents = 'none';
        document.body.appendChild(tempContainer);
        
        // 创建一个Range对象，选中容器内的所有内容
        const range = document.createRange();
        range.selectNodeContents(tempContainer);
        
        // 清除当前选区并设置新选区
        window.getSelection().removeAllRanges();
        window.getSelection().addRange(range);
        
        // 使用Clipboard API复制选中内容
        document.execCommand('copy');
        
        // 清理
        window.getSelection().removeAllRanges();
        document.body.removeChild(tempContainer);
        
        this.$message.success('复制成功！');
      } catch (error) {
        this.$message.error('复制失败，请手动复制！');
      }


    },
    handleClose (formName){
      this.createPromptVisible = false
      this.openDarwer()
      this.$refs[formName].resetFields();
    },
    addPromptDialog(){
      this.createPromptVisible = true
      this.closeDrawerFn()
      console.log(this.createPromptVisible);
    },
    onSubmit (formName){
      this.$refs[formName].validate((valid) =>
      {
        if (valid) {
          this.addLoading = true;
          promptConfigAddPromptConfig({
            ...this.formPromptData
          }).then(res=>{
            this.addLoading = false;
            if(res.code=="000000"){
              this.getPromptConfigList()
            }
          })
          this.handleClose('ruleForm')
        } else {
          return false;
        }
      });

    },
  }
}
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

.magnifiedEditBox-header{
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 40px;
  margin-bottom: 16px;

  span{
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
  }
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
      ::v-deep .el-input__inner{
        border-radius: 8px;
      }
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

      .btn-add{
        height: 40px;
        padding: 16px 20px;
        box-sizing: border-box;
        border-radius: 8px;
        background: #1747E5;
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;

        .icon-ctn{
          width: 20px;
          height: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .btn-word{
          height: 24px;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 16px;
          color: #FFFFFF;
          line-height: 24px;
        }
      }
    }
    .empty-data-ctn{
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;

      .empty-data{
        width: 150px;

        img{
          width: 150px;
        }
        p{
          font-family: MiSans, MiSans;
          text-align: center;
          color: rgb(130, 136, 148);
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
      line-height: 24px;
      color: #86909C;
      
    }
  }
  .vocabularyDrawer-footer{
    display: flex;
    justify-content: flex-end;
  }
}
</style>
