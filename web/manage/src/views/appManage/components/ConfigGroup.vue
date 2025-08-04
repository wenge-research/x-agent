<template>
  <div class="config-group">
    <div class="config-group-label">
      <div class="label" @click="changeShowItem()">
        <iconpark-icon class="icon-arrow" v-if="!disabled&&isArrowLeft" style="cursor: pointer" color="#494E57" size="18"
          :name="showItem ? 'arrow-down-s-line' : 'arrow-right-s-line'"></iconpark-icon>
        <!-- <img v-if="icon" class="icon" :src="icon" alt=""> -->
        <svg v-if="getIcon(label)" class="icon" aria-hidden="true">
          <use :xlink:href="`#icon-` + getIcon(label)"></use>
        </svg>
        <span style="margin-right: 8px;">{{ label }}</span>
        <div class="config-lable-logo" v-if="isConfigLogo">
          <img :src="configLogoImg" alt="">
        </div>
        <iconpark-icon class="icon-arrow" v-if="!disabled&&!isArrowLeft" style="cursor: pointer" color="#494E57" size="18"
          :name="showItem ? 'arrow-down-s-line' : 'arrow-right-s-line'"></iconpark-icon>
        

        <div class="config-switch" @click.stop v-if="showSwitch">
          <el-switch
          v-model="sValue"
          active-color="#4157FE"
          inactive-color="#CED4E0"
          active-value="是"
          inactive-value="否"
          @change="changeSwitchStatus"
          >
          </el-switch>
          <span>场景启用</span>
        </div>
        
      </div>
      <div class="flex" style="align-items: center;">
        <div v-if="(!showItem || disabled) && tip" class="tip" v-html="tip"></div>
        <slot v-if="showItem || !showTip" name="button"></slot>
      </div>
      
    </div>
    <div v-show="showItem" class="config-group-item">
      <slot></slot>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    label: {
      type: String,
      default: '',
    },
    icon: {
      type: String,
      default: '',
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    showTip: {
      type: Boolean,
      default: true,
    },
    tip: {
      type: String,
      default: '',
    },
    showItem: {
      type: Boolean,
      default: true,
    },
    showSwitch:{
      type: Boolean,
      default: false,
    },
    switchValue:{
      type: String,
      default: "否",
    },
    isArrowLeft:{
      type:Boolean,
      default:false
    },
    isConfigLogo:{
      type:Boolean,
      default:false
    },
    configLogoImg:{
      type:String,
      default:""
    }
  },
  watch:{
    switchValue:{
      immediate:true,
      deep:true,
      handler(cur,pre){
        this.sValue=cur
      }
    }
  },
  data() {
    return {
      // showItem: true,
      iconOptions: [{
        name: "对话体验",
        icon: "gongneng-duihuatiyan"
      }, {
        name: "安全拦截",
        icon: "anquanlanjie"
      }, {
        name: "问题建议",
        icon: "gongneng-tuijianwenti"
      }, {
        name: "语音设置",
        icon: "gongneng-yuyinshezhi"
      }, {
        name: "答案溯源",
        icon: "gongneng-daansuyuan"
      }, {
        name: "答案润色",
        icon: "daanrunse"
      }, {
        name: "输出结果评价",
        icon: "gongneng-shuchujieguopingfen"
      },{
        name:"模糊问题引导",
        icon:"gongneng-mohuwentiyindao"
      },{
        name:"问题联想",
        icon:"gongneng-wentilianxiang"
      },{
        name:"快捷指令",
        icon:"gongneng-kuaijiezhiling"
      },{
        name:"场景设置",
        icon:"gongneng-changjing"
      },{
        name:"检索引擎",
        icon:"gongneng-lianwangsousuo"
      },{
        name:"禁用IP",
        icon:"gongneng-jinyongIP"
      },{
        name:"问数检索",
        icon:"gongneng-wenshujiansuo"
      }],
      sValue:"否"
    }
  },
  methods: {
    getIcon(name) {
      if (name) {
        let data = this.iconOptions.find((item) => {
          return item.name === name;
        })
        if (data) {
          return data.icon;
        } else {
          return false;
        }
      } else {
        return false;
      }
    },
    changeShowItem() {
      this.showItem = !this.showItem;
      this.$emit('change', this.showItem);
    },
    changeSwitchStatus(val){
      this.$emit("update:switchValue",this.sValue)
    }
  }
}
</script>
<style scoped lang="scss">
.config-group {
  background: #ffffff;
  border-radius: 2px;
  border: 1px solid #d5d8de;
  padding: 12px;
  margin-bottom: 16px;

  .config-group-label {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-weight: 600;
    font-size: 16px;
    color: #494E57;
    line-height: 24px;

    .label {
      display: inline-flex;
      align-items: center;
      cursor: pointer;

      .icon-arrow {
        margin-right: 8px;
      }

      .icon {
        margin-right: 8px;
        width: 20px;
        height: 20px;
        background: #8A93E7;
        border-radius: 2px;
      }

      .config-lable-logo{
        margin-left: -6px;   
        height: 20px;
        img{
           width: 20px;
          height: 16px;
        }
      }
    }

    .tip {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #828894;
      line-height: 20px;
      display: flex;
      align-items: center;
    }

    .config-switch{
      height: 20px;
      display: flex;
      align-items: center;
      gap: 4px;

      ::v-deep .el-switch__core{
        width: 24px !important;
        height: 16px;

        &:after{
          width: 10px;
          height: 10px;
          top: 2px;
        }
      }
      ::v-deep .el-switch.is-checked .el-switch__core::after {
          left: 100%;
          margin-left: -11px;
      }


      span{
        display: inline-block;
        height: 20px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #1D2129;
        line-height: 20px;
      }
    }
  }
}
</style>