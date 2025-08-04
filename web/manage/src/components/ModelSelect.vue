<template>
  <div class="largeModel model-setting-wrapper">
    <el-popover
      width="435"
      trigger="manual"
      popper-class="model-setting-popover"
      v-model="visibleChange"
      
    >
      <div slot="reference" @click="visibleChange = !visibleChange" class="model-setting-btn" :class="{ 'active': visibleChange}">
        <div class="model-setting-btn-inner">
          <img
            :src="getImg()"
            v-if="form.modelId"
          />
          <span class="modelName" v-if="form.modelId && filterModleName(form.modelId)">{{
            filterModleName(form.modelId)
          }}</span>
          <span class="modelName" v-else>请选择模型</span>
        </div>
        
        <i class="el-icon-arrow-down el-icon--right"></i>
      </div>
      
      <div class="abilityInter">
        <!-- 新版模型选择 -->
        <div class="moXingXuanZe">
  <div style="display: flex;">
    <el-input
      style="flex: auto;"
      size="mini"
      spellcheck="false"
      placeholder="搜索大模型"
      v-model="moXingXuanInput"
      @keyup.enter.native="soSuoMoXingFn"
      @input="soSuoMoXingFn"
    >
      <i slot="prefix" class="el-input__icon el-icon-search"></i>
    </el-input>
    <el-checkbox v-model="showPersonanl" @change="soSuoMoXingFn" style="width: 130px;margin: 0 10px 0 120px;">仅看个人</el-checkbox>
  </div>
  <div class="moXingXuanZeConBox">
    <el-collapse v-model="moXingMianBan">
      <!-- 外层厂商 -->
      <el-collapse-item
        v-for="(waiBuItem, waiBuIndex) in fenGeModleOptions"
        :name="waiBuItem.manufacturer"
        :key="waiBuIndex"
        :title="waiBuItem.manufacturer"
      >
        <!-- 内层模型 -->
        <div
          v-for="neiBuItem in waiBuItem.modelArr"
          :key="neiBuItem.modelId"
        >
          <div
            class="model-item"
            :class="{ 'active': form.modelId === neiBuItem.modelId }"
            @click.stop="modelChange(neiBuItem.modelId)"
          >
            <div class="model-item-content">
              <img
                style="margin-right: 8px"
                :src="chatGptIdList[neiBuItem.manufacturer]"
              />
              <div class="cont">
                <div class="modelName">{{ neiBuItem.modelName }}</div>
                <el-tooltip
                    :content="neiBuItem.desc"
                    :disabled="!neiBuItem.desc"
                    placement="top"         
                    effect="light"         
                    :popper-class="'workflow-tooltip'"  
                  >
                  <div class="desc">{{ neiBuItem.desc || "暂无描述"}}</div>
                </el-tooltip>
                <div class="tags" v-if="neiBuItem.labels"><b v-for="(item,i) in neiBuItem.labels.split(',')"
                  :key="i">{{item}}</b></div>
              </div>
            </div>
            <!-- <div>
              <iconpark-icon
                name="check-line"
                v-show="form.modelId === neiBuItem.modelId"
              ></iconpark-icon>
            </div> -->
          </div>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</div>

      </div>
        
      
    </el-popover>
  </div>
</template>

<script>
import { modelList } from "@/api/app";
 
export default {
  name: "modelSelect",
  props: ["id"],
  data() {
    return {
      showPersonanl: false,
      visibleChange: false,
      modleOptionsXuanZeYong: [],
      // modelId:'',
      chatGptIdList: {
        智谱清言: require("@/assets/images/zpqy.png"),
        通义千问: require("@/assets/images/tongyi.png"),
        文心一言: require("@/assets/images/wxyy.png"),
        DeepSeek: require("@/assets/images/deepseek.png"),
        Kimi: require("@/assets/images/kimi.png"),
        雅意: require("@/assets/images/yayi.png"),
        豆包: require("@/assets/images/doubao.png"),
        中国移动: require("@/assets/images/deepseek.png"),
        百川: require("@/assets/images/baichuan.png"),
        星火: require("@/assets/images/xinghuo.png"),
        openAI: require("@/assets/images/openai.png"),
        MINIMAX: require("@/assets/images/MiniMax.png")
      },
      
      // 移植过来的
      form: {
        modelId: ''
      },
      moXingXuanInput: '',
      moXingMianBan: [],
    };
  },
  computed: {
    modleOptions() {
      return this.$store.state?.workflow?.modleOptions;
    },
    // 分隔厂商后的模型数据
    fenGeModleOptions(){
      let manufacturerKey = {};
      let manufacturerAmdModelArr = [];
      let modleArr = this.modleOptionsXuanZeYong;
      // 提取厂商
      modleArr.forEach(item => {
        manufacturerKey[item.manufacturer] = item.manufacturer;
        
      });

      // 将厂商提取未数组
      let manufacturerArr = Object.keys(manufacturerKey)

      // 赋值给折叠面板
      this.moXingMianBan = JSON.parse(JSON.stringify(manufacturerArr));

      // 给厂商添加模型
      manufacturerArr.forEach(changShang => {
        let obj = {
          manufacturer: changShang,
          modelArr: []
        }
        // 筛选出相应厂商的模型
        obj.modelArr = modleArr.filter(items => items?.manufacturer == changShang);
        // 添加到数组
        manufacturerAmdModelArr.push(obj);
      })
      manufacturerAmdModelArr = manufacturerAmdModelArr.sort((a, b) => {
        if (a.manufacturer === '雅意') return -1; // a comes first
        if (b.manufacturer === '雅意') return 1;  // b comes first
        return 0; // keep original order for others
      });
      // 抛出分离出厂商的模型数据
      return manufacturerAmdModelArr;
    },
    isAdmin(){
      return JSON.parse(sessionStorage.getItem("user")).powerType==0
    }
  },
  created() {
    // 获取模型数据
    this.soSuoMoXingFn();
  },
  mounted() {
    this.form.modelId = this.id
    this.$EventBus.$on("hideModelSelect", () => {
      if(this.visibleChange){
        this.visibleChange = false;
      }
    });
  },
  beforeDestroy() {
    this.$EventBus.$off("hideModelSelect");
  },
  methods: {
    // 显示已添加
    checkboxChange(v) {
      if(v) {
        
      } else {
      }
    },
    // 原组件方法
    getImg(){
      if(this.form.modelId){
        let manufacturer = this.$store.state?.workflow?.modleOptions.find(item=>item.modelId==this.form.modelId)?.manufacturer
        return this.chatGptIdList[manufacturer]
      }
    },

    // 原组件方法
    onChange(val){
      this.$emit('change',val)
      if(this.visibleChange){
        this.visibleChange = false;
      }
    },

    handleHide() {
      this.visibleChange = false;
      
    },
    handleHide2() {
    },
    // 显示关联模型
    filterModleName(item) {
      let findItem = this.modleOptions.find((items) => items.modelId == item);
      return findItem?.modelName;
    },
    // 搜索模型
    soSuoMoXingFn(){
      modelList({
        id: "",
        modelId: "",
        ownerType: this.showPersonanl ? "personal" : 'all',
        modelName: this.moXingXuanInput,
        status: "启用",
      }).then((res) => {
        if (res.code === "000000") {
          let data = res.data.filter(item => item.status !== '禁用')
          this.modleOptionsXuanZeYong = data || [];
        } else {
          this.modleOptionsXuanZeYong = [];
        }
      });
    },
    modelChange(val) {
      if(this.form.modelId == val) {
        this.$refs.modelSettingPopoverRef && this.$refs.modelSettingPopoverRef.doClose();
      }else {
        this.form.modelId = val;
        this.onChange(val);
        this.$refs.modelSettingPopoverRef && this.$refs.modelSettingPopoverRef.doClose();
      }
    },
  },
  beforeDestroy() {},
};
</script>
<style lang="scss" scoped>
.model-setting-wrapper {
  cursor: pointer;
  .model-setting-btn {
    display: flex;
    align-items: center;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    border: 1px solid #D1D5DA;
    color: #494C4F;
    height: 40px;
    justify-content: space-between;
    box-sizing: border-box;
    border-radius: 2px;
    padding: 0 16px;
    .model-setting-btn-inner {
      display: inline-flex;
      align-items: center;
    }
    img {
      width: 20px;
      height: 20px;
      margin-right: 8px;
    }
    .el-icon-arrow-down {
      margin-left: 8px;
    }
    &.active {
      border-color: #409EFF;
    }
  }
}
</style>
<style lang="scss" scoped>

.model-setting-popover {
  width: 200px;
  box-sizing: border-box;
  background: #FFFFFF;
  box-shadow: 0px 2px 6px 0px rgba(26,36,70,0.1) !important;
  border-radius: 2px;
  border: 0px !important;
  .el-popover__title {
    background: #ffffff;
    padding: 24px 24px 16px;
    font-family: MiSans, MiSans;
    font-weight: 600;
    font-size: 18px;
    color: #494E57;
    line-height: 24px;
    margin-bottom: 0px;
  }
  .abilityInter {
    width: 100%;
    // max-height: 400px;
    // overflow-y: auto;
    padding: 13px 5px 13px 13px;
    .model-item {
      display: flex;
      border-radius: 2px;
      align-items: center;
      justify-content: space-between;
      padding: 8px;
      cursor: pointer;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      margin: 8px 0;
      border-radius: 8px;
      &.active{
        background: #FFFFFF;
        border-radius: 8px;
        border: 1px solid #339DF4;
      }
      &:hover{
        background: #F7F8FA;
      }
      .model-item-content{
        display: flex;
        align-items: center;
        img{
          width: 40px;
          height: 40px;
        }
        .cont{
          flex: auto;
          .modelName{
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 14px;
            color: #1D2129;
            line-height: 20px;
            text-align: left;
            font-style: normal;
            text-transform: none;
          }
          .desc{
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #86909C;
            line-height: 30px;
            text-align: left;
            font-style: normal;
            white-space: nowrap;
            height: 30px;
            display: block;
            text-overflow: ellipsis;
            width: 340px;
            overflow: hidden;
          }
          .tags{
            white-space: nowrap;
            display: block;
            text-overflow: ellipsis;
            width: 340px;
            font-size: 12px;
            overflow: hidden;
            b{
              text-align: center;
              display: inline-block;
              margin: 0 10px 0 0;
              height: 20px;
              padding: 0 6px;
              background: #F2F3F5;
              border-radius: 4px;
              line-height: 20px;
              color: #86909C;
            }
          }
        }
      }
      
    }
  }

  .demo-ruleForm {
    .el-form-item__label {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
      margin-bottom: 4px;
    }
    .assModel {
      .el-input__inner {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 16px;
        color: #383d47;
      }
    }

    .flexOuter {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      width: 100%;
        .el-slider__runway {
        width: 140px;
      }
      > span:first-child {
        display: inline-block;
        width: 120px;
        position: relative;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #828894;
      }
    }

    .el-switch .el-switch__label.is-active {
      color: #3666ea;
    }
    .el-switch.is-checked .el-switch__core {
      background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%) !important;
      border-radius: 12px;
      border-color: transparent;
    }
    .el-slider__runway {
      height: 4px;
    }
    .el-slider__bar {
      height: 4px;
      background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
      border-radius: 4px;
    }
    .el-slider__button {
      width: 18px;
      height: 18px;
      background: #ffffff;
      box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.12);
      border: 1px solid #f2f5fa;
      margin-top: -4px;
    }
    .el-slider__input {
      width: 110px;
    }
    .el-slider__runway.show-input {
      margin-right: 120px;
    }
  }
  .popper__arrow {
    display: none;
  }
}
.moXingXuanZe{
    ::v-deep .el-collapse-item__header {
      height: 36px;
      line-height: 36px;
      font-size: 12px;
      color: #86909C;
    }
    ::v-deep .el-collapse-item__content {
     padding-bottom: 0;
    }
    ::v-deep .el-collapse-item__wrap{
      border: none;
    }
}

.moXingXuanZeConBox{
  max-height: 500px;
  overflow-y: auto;
  padding-right: 3px;
  margin-top: 8px;
}



</style>
