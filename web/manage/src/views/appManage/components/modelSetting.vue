<template>
  <div class="model-setting-wrapper">
    <el-popover
      placement="bottom-start"
      title=""
      width="435"
      trigger="click"
      content=""
      popper-class="model-setting-popover"
      ref="modelSettingPopoverRef"
      @show="visibleChange = true"
      @hide="handleHide"
    >
      <div
        slot="reference"
        class="model-setting-btn"
        :class="{ active: visibleChange }"
        :style="{background:bgColor,'border-radius':borderR,height:btnHeight}"
      >
        <div class="model-setting-btn-inner">
          <img :src="getImg()" v-if="form.modelId && filterModleName(form.modelId)" />
            <span class="modelName" v-if="form.modelId && filterModleName(form.modelId)" :title="filterModleName(form.modelId)">{{
            filterModleName(form.modelId)
          }}</span>
          <span class="modelName" v-else>请选择大模型</span>
        </div>

        <i class="el-icon-arrow-down el-icon--right" v-if="!hidArrow"></i>
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
                            popper-class="workflow-tooltip"  
                          >
                          <div class="desc">{{ neiBuItem.desc || '暂无描述'}}</div>
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
        

        <div
          class="model-item"
          v-if="false"
          v-for="item in modleOptions"
          :key="item.modelId"
          :label="item.modelName"
          :value="item.modelId"
          :class="{ active: form.modelId === item.modelId }"
          @click.stop="modelChange(item.modelId)"
        >
          <img
            style="width: 18px; height: 18px; margin-right: 8px"
            :src="chatGptIdList[item.manufacturer]"
          />
          <span class="modelName">{{ item.modelName }}</span>
        </div>

        <el-form
          :model="form"
          ref="ruleForm"
          class="demo-ruleForm"
          v-if="false"
        >
          <el-form-item :label="$t('selectModel')" prop="applicationName">
            <el-select
              :placeholder="$t('selectPlaceholder')"
              style="width: 100%"
              v-model="form.modelId"
              class="assModel"
              @change="modelChange"
            >
              <div
                slot="prefix"
                class="selectPrice el-input__icon"
                v-if="form.modelId"
              >
                <img
                  style="width: 18px; height: 18px; margin: 11px 8px 11px 4px"
                  :src="getImg()"
                />
              </div>
              <el-option
                v-for="item in modleOptions"
                :key="item.modelId"
                :label="item.modelName"
                :value="item.modelId"
              >
                <div style="display: flex; align-items: center; height: 34px">
                  <img
                    style="width: 18px; height: 18px; margin-right: 8px"
                    :src="chatGptIdList[item.manufacturer]"
                  />
                  <span class="modelName">{{ item.modelName }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="" v-if="false">
            <div class="flexOuter">
              <span>{{ $t("enableModelResponse") }}</span>
              <el-switch v-model="form.modelAnswerFlag"></el-switch>
            </div>
            <div class="flexOuter">
              <span>{{ $t("enableImageRecognition") }}</span>
              <el-switch v-model="form.ocrFlag"></el-switch>
            </div>
            <div class="flexOuter">
              <span style="width: 240px">{{
                $t("enableVideoRecognition")
              }}</span>
              <el-switch v-model="form.videoFlag"></el-switch>
            </div>
            <div class="flexOuter">
              <span>{{ $t("enableMultiTurnResponse") }}</span>
              <el-switch v-model="form.multiDialogueFlag"></el-switch>
            </div>
            <div class="flexOuter" v-if="form.multiDialogueFlag">
              <span>{{ $t("contextCarryCount") }}</span>
              <el-slider
                v-model="form.multiDialogueNum"
                :min="0"
                :max="10"
                :step="0.1"
              ></el-slider>
              <el-input-number
                v-model="form.multiDialogueNum"
                controls-position="right"
                :min="0"
                :max="10"
                :step="1"
                size="small"
                style="width: 100px; margin-left: 15px"
              ></el-input-number>
            </div>
            <div class="flexOuter">
              <span>{{ $t("enableDocumentQa") }}</span>
              <el-switch v-model="form.knowledgeFlag"></el-switch>
            </div>
            <div class="flexOuter">
              <span>{{ $t("enableQuestionRewriting") }}</span>
              <el-switch v-model="form.rewritingFlag"></el-switch>
            </div>
          </el-form-item>
          <!-- <el-form-item label="生成多样性" prop="introduce">
                <div style="margin-top: 30px">
                  <div class="flexOuter">
                    <span>生产随机性</span>
                    <el-slider
                      v-model="form.contentScore"
                      :min="0"
                      :max="10"
                      :step="0.1"
                    ></el-slider>
                    <el-input-number
                      v-model="form.contentScore"
                      controls-position="right"
                      :min="0"
                      :max="10"
                      :step="1"
                      size="small"
                      style="width: 100px; margin-left: 15px"
                    ></el-input-number>
                  </div>
                  <div class="flexOuter">
                    <span>Top P</span>
                    <el-slider
                      v-model="form.rangeContentScore"
                      :min="0"
                      :max="10"
                      :step="0.1"
                    ></el-slider>
                    <el-input-number
                      v-model="form.rangeContentScore"
                      controls-position="right"
                      :min="0"
                      :max="10"
                      :step="1"
                      size="small"
                      style="width: 100px; margin-left: 15px"
                    ></el-input-number>
                  </div>
                </div>
              </el-form-item>
              <el-form-item label="输入输出设置" prop="introduce">
                <div style="margin-top: 30px">
                  <div class="flexOuter">
                    <span>携带上下文轮数</span>
                    <el-slider
                      v-model="form.multiDialogueNum"
                      :min="0"
                      :max="10"
                      :step="1"
                    ></el-slider>
                    <el-input-number
                      v-model="form.multiDialogueNum"
                      controls-position="right"
                      :min="0"
                      :max="10"
                      :step="1"
                      size="small"
                      style="width: 100px; margin-left: 15px"
                    ></el-input-number>
                  </div>
                  <div class="flexOuter">
                    <span>最大回复长度</span>
                    <el-slider
                      v-model="form.rangeContentScore"
                      :min="0"
                      :max="10"
                      :step="1"
                    ></el-slider>
                    <el-input-number
                      v-model="form.rangeContentScore"
                      controls-position="right"
                :min="0"
                :max="10"
                :step="1"
                size="small"
                style="width: 100px; margin-left: 15px"
              ></el-input-number>
            </div>
          </div>
        </el-form-item> -->
        </el-form>
      </div>
      <div class="modelFallback" v-if="isModelFallback">
        <div class="left">
          模型兜底
          <el-tooltip
            class="item"
            effect="dark"
            content="在其他知识来源均未召回相关内容时，直接使用模型总结输出结果，避免回答失败。默认开启"
            placement="bottom-start"
          >
            <iconpark-icon
              name="question-line"
              style="margin-left: 4px;cursor: pointer"
              color="#86909C"
              size="18"
            ></iconpark-icon>
          </el-tooltip>
        </div>
        <el-switch v-model="form.modelAnswerFlag" @change="switchChange" />
      </div>
    </el-popover>
  </div>
  <!-- <div class="left-content-drawer">
    <el-drawer
      v-if="drawerVisible"
      :visible.sync="drawerVisible"
      :modal="false"
      :modal-append-to-body="true"
      direction="ltr"
      size="560px"
      style="
        width: 560px;
        box-sizing: border-box;
      "
      :show-close="false"
      :title="$t('modelSetting')"
    >
    
    </el-drawer>
  </div> -->
</template>
<script>
import { modelList } from "@/api/app";
export default {
  name: "modelSetting",
  props: {
    params: {
      type: Object,
      default: () => {},
    },
    hideLabel:{
      type:Boolean,
      default:false
    },
    isModelFallback:{
      type:Boolean,
      default:true
    },
    bgColor:{
      type:String,
      default:"#fff"
    },
    borderR:{
      type:String,
      default:"2px"
    },
    btnHeight:{
      type:String,
      default:"40px"
    },
    hidArrow:{
      type:Boolean,
      default:false
    }
  },
  data() {
    return {
      drawerVisible: false,
      showPersonanl: false,
      moXingXuanInput: "",
      moXingMianBan: [],
      modleOptionsXuanZeYong: [],
      form: {
        modelId: "",
      },
      chatGptIdList: {
        智谱清言: require("@/assets/images/zpqy.png"),
        通义千问: require("@/assets/images/tongyi.png"),
        文心一言: require("@/assets/images/wxyy.png"),
        DeepSeek: require("@/assets/images/deepseek.png"),
        中国移动: require("@/assets/images/deepseek.png"),
        Kimi: require("@/assets/images/kimi.png"),
        雅意: require("@/assets/images/yayi.png"),
        豆包: require("@/assets/images/doubao.png"),
        百川: require("@/assets/images/baichuan.png"),
        星火: require("@/assets/images/xinghuo.png"),
        openAI: require("@/assets/images/openai.png"),
        MINIMAX: require("@/assets/images/MiniMax.png")
      },
      visibleChange: false
    };
  },
  computed: {
    modleOptions() {
      return this.$store.state?.workflow?.modleOptions;
    },
    // 分隔厂商后的模型数据
    fenGeModleOptions() {
      let manufacturerKey = {};
      let manufacturerAmdModelArr = [];
      let modleArr = this.modleOptionsXuanZeYong;
      // 提取厂商
      modleArr.forEach((item) => {
        manufacturerKey[item.manufacturer] = item.manufacturer;
      });

      // 将厂商提取未数组
      let manufacturerArr = Object.keys(manufacturerKey);

      // 赋值给折叠面板
      this.moXingMianBan = JSON.parse(JSON.stringify(manufacturerArr));

      // 给厂商添加模型
      manufacturerArr.forEach((changShang) => {
        let obj = {
          manufacturer: changShang,
          modelArr: [],
        };
        // 筛选出相应厂商的模型
        obj.modelArr = modleArr.filter(
          (items) => items?.manufacturer == changShang
        );
        // 添加到数组
        manufacturerAmdModelArr.push(obj);
      });
      manufacturerAmdModelArr = manufacturerAmdModelArr.sort((a, b) => {
        if (a.manufacturer === '雅意') return -1; // a comes first
        if (b.manufacturer === '雅意') return 1;  // b comes first
        return 0; // keep original order for others
      });
      console.log(manufacturerAmdModelArr)
      // 抛出分离出厂商的模型数据
      return manufacturerAmdModelArr;
    },
    isAdmin(){
      return JSON.parse(sessionStorage.getItem("user")).powerType==0
    }
  },
  created() {
    // 获取模型选择数据
    this.soSuoMoXingFn();
  },
  mounted() {},
  watch: {
    // 监听
    params:{
      deep:true,
      immediate:true,
      handler(curVal, oldVal) {
        if (curVal) {
          this.form = this.params;
        }
      },
    }
  },
  mounted() {
    this.form = JSON.parse(JSON.stringify(this.params));
  },
  methods: {
    // 模型兜底
    switchChange(val) {
      this.$emit('switchChange', val)
    },
    // 搜索模型
    soSuoMoXingFn() {
      modelList({
        id: "",
        modelId: "",
        modelName: this.moXingXuanInput,
        status: "启用",
        ownerType:this.showPersonanl?'personal':'all'
      }).then((res) => {
        if (res.code === "000000") {
          let data = res.data.filter((item) => item.status !== "禁用");
          this.modleOptionsXuanZeYong = data || [];
        } else {
          this.modleOptionsXuanZeYong = [];
        }
      });
    },
    moXingMianBanChange(val) {},
    getImg() {
      if (this.form.modelId) {
        let manufacturer = this.$store.state?.workflow?.modleOptions.find(
          (item) => item.modelId == this.form.modelId
        )?.manufacturer;
        return this.chatGptIdList[manufacturer];
      }
    },
    openDrawer() {
      this.drawerVisible = true;
    },
    closeDrawer() {
      this.drawerVisible = false;
    },
    // 显示关联模型
    filterModleName(item) {
      let findItem = this.modleOptions.find((items) => items.modelId == item);
      return findItem?.modelName;
    },
    filterModleIcon(item) {
      let findItem = this.modleOptions.find((items) => items.modelId == item);
      const manufacturer = findItem?.manufacturer || "";
      return this.chatGptIdList[manufacturer] || "";
    },
    handleHide() {
      this.visibleChange = false;
      // this.$EventBus.$emit("saveApplication");
    },
    modelChange(val) {
      if (this.form.modelId == val) {
        this.$refs.modelSettingPopoverRef.doClose();
      } else {
        this.form.modelId = val;
        this.form.modelName =this.filterModleName(val)
        this.$EventBus.$emit("changeApplicationStatus", true);
        this.$EventBus.$emit("saveApplication");
        this.$emit("modelChanges",{...this.form})
        this.$refs.modelSettingPopoverRef.doClose();
      }
    },
  },
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
    color: #494c4f;
    height: 40px;
    justify-content: space-between;
    box-sizing: border-box;
    border-radius: 2px;
    padding: 0 16px;
    .model-setting-btn-inner {
      display: inline-flex;
      align-items: center;

      .modelName{
        display: inline-block;
        max-width: 240px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
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
      background: #f0f2f5;
    }
  }
}
</style>
<style lang="scss" scoped>
.model-setting-popover {
  width: 200px;
  box-sizing: border-box;
  background: #ffffff;
  box-shadow: 0px 2px 6px 0px rgba(26, 36, 70, 0.1) !important;
  border-radius: 2px;
  border: 0px !important;
  .el-popover__title {
    background: #ffffff;
    padding: 24px 24px 16px;
    font-family: MiSans, MiSans;
    font-weight: 600;
    font-size: 18px;
    color: #494e57;
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
            font-size: 12px;
            display: block;
            text-overflow: ellipsis;
            width: 340px;
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
      color: #494e57;
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
.moXingXuanZe {
  ::v-deep .el-collapse-item__header {
    height: 36px;
    line-height: 36px;
    font-size: 12px;
    color: #86909C;
  }
  ::v-deep .el-collapse-item__content {
    padding-bottom: 0;
  }
  ::v-deep .el-collapse-item__wrap {
    border: none;
  }
}

.moXingXuanZeConBox {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 3px;
  margin-top: 8px;
}
.modelFallback {
  height: 64px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid #e5e6ea;
  .left {
    display: flex;
    align-items: center;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #1d2129;
  }
  ::v-deep .el-switch__core {
    width: 32px!important;
  }
  ::v-deep .el-switch.is-checked .el-switch__core {
    border-color: #1747E5;
    background-color: #1747E5;
  }
}
</style>