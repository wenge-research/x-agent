<template>
  <div class="select-template-container">
    <el-drawer :title="$t('selectTemplate')" :visible.sync="templateVisible" width="960px" class="selectTemplate"
      destroy-on-close :before-close="cancelTemplate" append-to-body>
      <div slot="title" class="flex-center select-template-title">
        <div>{{ $t('selectTemplate') }}</div>
        <div class="tabs-box">
          <div
            class="temp-btn flex-center just"
            :class="{ active: templateType == 'PC' }"
            @click="changeTemplate('PC')"
          >
            <div class="temp-btn-content">
              <iconpark-icon name="macbook-line" size="16"></iconpark-icon>
              <span> PC </span>
            </div>
            <i class="el-icon-success" style="color: #4bbe25"></i>
            
          </div>
          <div
            class="temp-btn flex-center just"
            :class="{ active: templateType == 'H5' }"
            @click="changeTemplate('H5')"
            style="margin-left: 10px"
          >
            <div class="temp-btn-content"><iconpark-icon name="smartphone-line" size="16"></iconpark-icon><span> H5 </span></div>
            <i class="el-icon-success" style="color: #4bbe25"></i>
            
          </div>
        </div>
      </div>
      <div class="drawer-box">
        <div class="title-tab">
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane v-for="(item, index) in localizedSelectTypeList" :key="item.value" :label="item.name"
              :name="item.value"></el-tab-pane>
          </el-tabs>
          <!-- <el-checkbox v-model="checked" @change="isUseClike">{{  $t('onlyAvailableForViewing') }}</el-checkbox> -->
        </div>
        <div class="templateList templateList-pc" v-if="templateType == 'PC'">
          <div class="templateList-item" :class="{ disabled: item.isDisabled, active: templateSign == item.templateId }" 
          v-for="item in processedTemplateList"
            :key="item.id" @mouseover="changeTemplatePC(item)">
            <!-- <span class="tips" v-if="defaultId == item.templateId">{{
                $t("currentTemplate")
              }}</span> -->
              <span 
  class="tips" 
  v-if="(defaultId && defaultId === item.templateId) || (!defaultId && item.defaultFlag === 1)"
>
  {{ $t("currentTemplate") }}
</span>
            <div class="item-title">
              <div class="templateName" :class="{ active: templateSign == item.templateId && !item.isDisabled }" :title="item.templateName">
                {{
                  item.templateName }}</div>
              <div class="notUse" v-show="item.isDisabled">{{ $t('moudelIsNotUse') }}</div>
            </div>
            <div class="templateItem" :class="{ active: templateSign == item.templateId }">
              <!-- v-if="item.defaultFlag == 0" -->
              <div class="imgBox">
                <el-image :src="item.picturePath" fit="cover" class="imgs"></el-image>
              </div>
            </div>
            <div class="useItem" v-if="templateSign === item.templateId" v-show="!item.isDisabled">
              <el-button :disabled="item.isDisabled" class="useBtn" type="primary"
                @click="confirmTemplate">{{ $t('useThisMode') }}</el-button>
            </div>
          </div>
        </div>
        <div class="templateList" v-if="templateType == 'H5'">
          <div class="templateList-item templateList-item-h5" :class="{ active: mobileTemplateSign == item.templateId }" v-for="item in mobileTemplateDataList" :key="item.id"
            @click="changeTemplateH5(item)" style="width: 20%;">
            <div class="templateItem" :class="{
              active: mobileTemplateSign == item.templateId,
            }">
              <span class="tips" v-if="item.defaultFlag == 0">{{
                $t("default")
              }}</span>
              <el-image :src="item.picturePath" class="imgsH5">
              </el-image>
            </div>
            <div class="templateName" :class="{ active: mobileTemplateSign == item.templateId }"
              :title="item.templateName">{{ item.templateName }}</div>
          </div>
        </div>
      </div>
      <div class="footer-btn flex-center">
        <el-button @click="cancelTemplate">{{ $t("cancel") }}</el-button>
        <el-button type="primary" @click="confirmTemplate">{{
          $t("select")
        }}</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import { templateList } from "@/api/app";
import { mapState } from 'vuex'
export default {
  name: "selectTemplate",
  props: {
    defaultId: {
      type: String,
      default: ""
    },
    type: {
      type: String,
      default: ""
    }
  },
  // inject: ['agentValue'], // 注入父组件提供的数据
  data() {
    return {
      // defaultId: "658034ccf7294013be719cf2ec4df81c",
      templateVisible: false,
      templateType: 'PC',
      templateSign: '',
      mobileTemplateSign: '',
      templateDataList: [],
      processedTemplateList: [], // 处理后的模板列表
      mobileTemplateDataList: [],
      checked: true,   //是否仅看可用
      activeName: '',  //tab默认
      // smart_qa[智能问答],smart_report[智能报告],smart_search[智能搜索],smart_translate[智能翻译]，smart_review[智能审查]
      selectTypeList: [
      { nameKey: 'all', value: '' }, // 使用国际化key替代中文
      { nameKey: 'smart_qa', value: 'smart_qa' },
      { nameKey: 'smart_report', value: 'smart_report' },
      { nameKey: 'smart_search', value: 'smart_search' },
      { nameKey: 'smart_translate', value: 'smart_translate' },
      { nameKey: 'smart_review', value: 'smart_review' }
    ]
    }
  },
  computed: {
    currentModelType() {
      return this.$store.state.workflow.modelType;
    },
    //国际化tab选项
    localizedSelectTypeList() {
    return this.selectTypeList.map(item => ({
      name: this.$t(item.nameKey), // 使用vue-i18n的t方法
      value: item.value
    }))
  }
  },
  watch: {
    // 监听 modelType 变化
    currentModelType: {
      handler() {
        this.processTemplates();
      },
      immediate: true
    }
  },
  methods: {
    //是否仅看可用
    isUseClike(e) {
      this.processTemplates();
    },
    //模板tab选项
    handleClick(tab, event) {
      this.getCurList();
    },
    getCurList(){
      this.templateType === "PC" && this.getTemplateList();
      this.templateType === "H5" && this.getMobileTemplateList();
    },
    openDrawer(data) {
      this.type === 'text-agent' && this.$message.warning('当前模式只能选多入参模板');
      this.templateVisible = true;
      this.templateType = "PC";
      this.templateSign = '';
      this.mobileTemplateSign = '';
      if (data?.templateId) {
        this.templateSign = data.templateId;
      }
      if (data?.mobileTemplateId) {
        this.mobileTemplateSign = data.mobileTemplateId;
      }
      this.getCurList();
    },
    closeDrawer() {
      this.templateVisible = true;
    },
    changeTemplate(type) {
      this.templateType = type;
      this.getCurList()
    },
    //切换PC模版
    changeTemplatePC(data) {
      this.templateSign = data.templateId;
    },
    // 切换H5模板
    changeTemplateH5(data) {
      this.mobileTemplateSign = data.templateId;
    },
    cancelTemplate() {
      this.templateVisible = false;
    },
    confirmTemplate() {
      if (!this.templateSign && !this.mobileTemplateSign) {
        this.$message({
          type: "warning",
          message: "请至少选择一个H5模板和PC模板后再确认",
        });
        return;
      }

      if (this.templateSign) {
        this.defaultId = this.templateSign
      } else {
        this.$message({
          type: "warning",
          message: "请至少选择一个PC模板后再确认",
        });
        return;
      }

      if (this.mobileTemplateSign) {

      } else {
        this.$message({
          type: "warning",
          message: "请至少选择一个H5模板后再确认",
        });
        return;
      }
      this.$emit("updateTemplate", {
        templateId: this.templateSign,
        mobileTemplateId: this.mobileTemplateSign,
        templateRoute: this.templateRoute,
      })

      this.templateVisible = false;
    },
    clickConfig(model) {
      this.selectTemplateVisible = false;
      if (model.type == "PC") {
        this.setForm.templateId = model.ids;
      } else {
        this.setForm.mobileTemplateId = model.ids;
      }
    },
    // 根据 modelType 处理禁用状态
    processTemplates() {
      // 先处理禁用状态
      let processed = this.templateDataList.map(item => {
        const isDisabled = (this.currentModelType === 'qa' ||
          this.currentModelType === 'dialogue') &&
          item.isMultiple === 1;
        return {
          ...item,
          isDisabled
        };
      });
      // 再根据复选框状态过滤
      this.processedTemplateList = this.checked ?
        processed.filter(item => !item.isDisabled) :
        processed;
      if(this.type === 'text-agent'){
        this.processedTemplateList = processed.filter(item => !item.isDisabled && item.templateRoute === 'intelligentReport')
      }
    },
    getTemplateList() {
      const params = {
        form: this.templateType,
		status: "1",
        intelligenceType: this.activeName === '0' ? '' : this.activeName
      }
      templateList(params).then((res) => {
        if (res.code == "000000") {
          this.templateDataList = res.data?.records || [];
          this.processTemplates(); // 处理模板数据
        } else {
          this.templateDataList = [];
          this.processedTemplateList = [];
        }
      });
    },
    getMobileTemplateList() {
      templateList({ form: this.templateType, intelligenceType: this.activeName === '0' ? '' : this.activeName }).then((res) => {
        if (res.code == "000000") {
          this.mobileTemplateDataList = res.data?.records || [];
        } else {
          this.mobileTemplateDataList = [];
        }
      });
    },
  }
}
</script>
<style lang="scss" scoped>
.mt16 {
  margin-top: 16px;
}

.marginLeft20 {
  margin-left: 20px;
}

.marginTop20 {
  margin-top: 20px;
}

.marginTop32 {
  margin-top: 32px;
}

.flex-center {
  display: flex;
  align-items: center;
}

.just {
  justify-content: space-between;
}

.templateItem {
  border-radius: 4px;
  margin: 8px;

  .tips {
    position: absolute;
    top: 12px;
    left: 16px;
    width: 40px;
    height: 24px;
    background: rgba(73, 78, 87, .2);
    border-radius: 2px;
    font-size: 12px;
    color: #ffffff;
    line-height: 24px;
    text-align: center;
    backdrop-filter: blur(2px);
    z-index: 99;
  }
}

::v-deep .el-tabs__nav {
  font-family: MiSans, MiSans;
}

::v-deep .select-template-title {
    font-weight: bold;
  }

::v-deep .el-tabs__item.is-active {
  font-weight: bold;
}

::v-deep .el-tabs__item {
  padding: 3px 8px;
  font-size: 16px;
}
::v-deep .el-checkbox:last-of-type {
  margin-bottom: 10px;
}

::v-deep .el-drawer__close-btn {
  width: 32px;
  height: 32px;
}

.selectTemplate {
  ::v-deep .el-drawer {
    width: 970px !important;
    border-radius: 8px 0 0 8px;

    .el-drawer__header {
      height: auto;
      background-color: #ffffff;
      padding: 32px 32px 16px;
      font-weight: 500;
      font-size: 20px;
      color: #494E57;
      line-height: 24px;
      margin-bottom: 0px;

      .el-dialog__headerbtn {
        top: 36px;
        right: 32px;
      }
    }

    .el-drawer__body {
      flex: 1;
      overflow: hidden;
      display: flex;
      flex-direction: column;

      .drawer-box {
        padding: 0px 32px 32px 32px;
        height: calc(100vh - 100px);
        overflow-y: auto;

        .title-tab {
          margin-bottom: 4px;
          display: flex;
          align-items: center;
          justify-content: space-between;
        }
      }

      .footer-btn {
        width: 100%;
        height: 88px;
        background: #fff;
        padding: 16px 32px;
        justify-content: right;
      }
    }
  }


}

.select-template-title {
  display: flex;
  align-items: center;
  margin-bottom: 0px;

  .tabs-box {
    margin-left: 220px;
    height: 32px;
    background: #F2F4F7;
    border-radius: 2px;
    padding: 2px;
    display: flex;
  }
}

.temp-btn {
  width: 120px;
  height: 28px;
  background: #f2f5fa;
  border-radius: 2px;
  font-weight: 400;
  font-size: 14px;
  color: #494E57;
  line-height: 28px;
  padding: 0 8px;
  box-sizing: border-box;
  cursor: pointer;
  justify-content: space-between;

  .temp-btn-content {
    display: inline-flex;
    align-items: center;

    span {
      margin-left: 4px;
    }
  }

  i {
    display: none;
  }

  &.active {
    background: #FFFFFF;
    box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
    color: #494E57;

    i {
      display: block;
      margin-right: 5px;
    }
  }
}

.templateList {
  display: flex;
  // align-items: center;
  flex-direction: row;
  flex-wrap: wrap;

  &-item {
    margin-bottom: 8px;
  }
  

  .templateItem {
    background: rgba(28, 80, 253, 0.05);
    border-radius: 4px;
    margin: 8px;
    position: relative;
    cursor: pointer;
    border: 2px solid #fff;
    

    .tips {
      position: absolute;
      top: 5px;
      left: 5px;
      width: 40px;
      height: 24px;
      background: #333333;
      border-radius: 4px;
      font-size: 12px;
      color: #ffffff;
      line-height: 24px;
      text-align: center;
      opacity: 0.7;
      backdrop-filter: blur(1px);
      z-index: 99;
    }

    .imgs {
      width: 254px;
      height: 153px;
      border-radius: 4px;
    }

    &.active {
      // border: 2px solid #1747E5;
    }

    .imgsH5 {
      width: 100%;
      height: 252px;
      border-radius: 4px;
    }
  }

  .templateName {
    text-align: center;

    &.active {
      color: #1747E5;
      font-weight: bold;
    }
  }

  &.templateList-pc {
    .templateList-item {
    position: relative;
      box-sizing: border-box;
      padding-left: 4px;
      padding-right: 4px;
      position: relative;
      transition: all 0.3s ease;
      width: 288px;
      height: 224px;
      border-radius: 2px;
      margin-bottom: 16px;
      margin-right: 16px;
      background: #FFFFFF;
      // box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
      border: 1px solid #E7E7E7;
      border-radius: 4px;
      transition: all 0.3s ease;

      .tips {
    position: absolute;
    top: 12px;
    right: 16px;
    // width: 40px;
    padding: 0 4px;
    height: 24px;
    background: rgb(28 80 253);
    border-radius: 2px;
    font-size: 12px;
    color: #ffffff;
    line-height: 24px;
    text-align: center;
    backdrop-filter: blur(2px);
    z-index: 99;
  }

    
   

      .item-title {
        display: flex;
        align-items: center;
        // min-width: 200px;
        // justify-content: space-between;
        align-items: center;
        padding-right: 4px;

        .notUse {
          padding: 4px;
          background: #EBEEF2;
          border-radius: 2px;
          font-weight: 400;
          font-size: 12px;
          color: #777;
        }
      }

      &.disabled {
        // filter: grayscale(100%);
        opacity: 0.6;
        cursor: not-allowed;
      }

      .useItem {
        position: absolute;
        width: 280px;
        height: 47px;
        background: rgba(255, 255, 255, 0.9);
        border-radius: 0px 0px 3px 3px;
        bottom: 0;
        display: flex;
        justify-content: center;
        align-items: center;

        .el-button {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 260px;
          height: 32px;
          background: #1747E5;
          border-radius: 4px;
          font-weight: normal;
        }
      }

      .templateItem {
        pointer-events: none;
        margin: 0px;
        background: linear-gradient(180deg, #EAF1FF 0%, #D0DFFD 100%);
        padding: 23px 0px 0px 19px;
        position: relative;

        .imgBox {
          width: 255px;
          height: 150px;
          border-radius: 16px 0px 0px 0px;
          border: 4px solid #2E3338;
          border-right: 0px;
          border-bottom: 0px;
        }

        .imgs {
          // height: 174px;
          width: 251px;
          height: 146px;
          border-radius: 16px 0px 0px 0px;
        }
      }

      .templateName {
        pointer-events: none;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #494E57;
        line-height: 24px;
        text-align: center;
        // margin-top: 10px;
        padding: 10px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        &.active {
          // color: #1747E5;
          // font-weight: bold;
        }
      }

      &:nth-child(3n) {
        margin-right: 0px;
      }

      .disabled-mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.5);
        z-index: 1;
        pointer-events: none;
      }
    }

    .templateList-item-h5 {
      width: 25% !important;
    }
  }
}

:deep(.el-tabs__item) {
  color: #86909C;
  font-weight: normal;
}

:deep(.el-tabs__active-bar) {
  background-color: #603ECA;
}

:deep(.el-tabs__item.is-active) {
  color: #603ECA;
  cursor: pointer;
}

:deep(.el-tabs__item:hover) {
  color: #603ECA;
  cursor: pointer;
}

:deep(.el-tabs__nav-wrap::after) {
  background-color: #fff;
}

// :deep(.el-image__inner) {
//   width: 258px;
//   height: 146px;
// }

.templateList-pc .templateList-item.active {
  border: 1px solid #409EFF; 
  box-sizing: border-box; 
}
.templateList-pc .templateList-item.active .item-title,
.templateList-pc .templateList-item.active .templateItem {
  margin: -2px; 
}


</style>