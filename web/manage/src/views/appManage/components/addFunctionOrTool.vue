<template>
  <el-dialog
    :configName="$t('add')"
    :title="$t('add')"
    :visible.sync="dialogVisible"
    width="720px"
    class="applicationDialog"
    :before-close="cancelConfig"
    append-to-body
  >
    <!-- <div class="tabs-box">
      <div class="tabs-item" :class="{ active: activeName == 'first' }" @click="activeName = 'first'">{{ $t('function') }}</div>
      <div class="tabs-item" :class="{ active: activeName == 'second' }" @click="activeName = 'second'">{{ $t('plugInUnit') }}</div>
    </div> -->
    
    <div v-show="activeName == 'first'">
      <div class="list-box" v-loading="loading">
        <ul>
          <li
            class="list-li erwe"
            v-for="(item, index) in funtionList"
            :key="index"
            v-show="!useLessNameList.includes(item.pluginName)"
          >
            <div class="flex-center just">
              <div class="flex-center">
                <!-- <img class="icon-img" :src="item.pluginIcon" /> -->
                 <svg class="icon-img" aria-hidden="true">
                    <use
                      :xlink:href="`#icon-` + getIcon(item.pluginName)"
                    ></use>
                  </svg>
                <span class="text">{{ item.pluginName }}</span>
              </div>
              <el-switch
                v-model="item.status"
                active-color="#4157FE"
                inactive-color="#CED4E0"
                active-value="是"
                inactive-value="否"
                @change= "changeStatus($event,index)"
              >
            </el-switch>
            </div>
            <p class="tips">{{ item.remark }}</p>
              
            
            
          </li>
        </ul>
      </div>
    </div>
    <div v-show="activeName == 'second'">
      <div class="flex-center just" style="margin-bottom: 20px;">
        <el-input
          :placeholder="$t('inputToolKeywords')"
          suffix-icon="el-icon-search"
          v-model="searchToolName"
          style="width: 286px"
        >
        </el-input>
        <el-button
          type="primary"
          icon="el-icon-circle-plus"
          size="medium"
          plain
          style="color: #1c50fd; border-color: #1c50fd; background: none"
        >
          {{ $t("createTools") }}
        </el-button>
      </div>
      <div class="list-box">
        
        <ul>
          <li
            class="list-li erwedd"
            v-for="(item, index) in toolList"
            :key="index"
          >
            <div class="flex-center just">
              <div class="flex-center">
                <img class="icon-img" :src="item.pluginIcon" />
                <span class="text">{{ item.pluginName }}</span>
              </div>
              <el-button
                v-if="item.status==='是'"
                type="text"
                size="mini"
                icon="el-icon-delete"
                style="color: #d82225"
                @click="delTool(index)"
                >{{ $t("remove") }}</el-button
              >
              <el-button
                v-else
                type="text"
                size="mini"
                icon="el-icon-plus"
                style="color: #1c50fd"
                @click="addTool(index)"
                >{{ $t("add") }}</el-button
              >
            </div>
            <p class="tips">{{ item.remark }}</p>
          </li>
        </ul>
      </div>
    </div>
    
    <span slot="footer" class="dialog-footer">
     
      <el-button @click="cancelConfig">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="setConfig">{{
        $t("confirm")
      }}</el-button>
    </span>
  </el-dialog>
</template>
      
<script>
import {
  applicationPluginList,
  applicationPluginDataList,
  addApplicationPluginData,
} from "@/api/app";
export default {
  data() {
    return {
      activeName: "first",
      listAllData: [],
      funtionList: [],
      toolList: [],
      searchToolName: "",
      selectIds: [],
      loading: false,
      iconOptions: [
        {
          name: "对话体验",
          icon: "gongneng-duihuatiyan",
        },
        {
          name: "安全拦截",
          icon: "anquanlanjie",
        },
        {
          name: "问题建议",
          icon: "gongneng-tuijianwenti",
        },
        {
          name: "语音对话",
          icon: "gongneng-yuyinshezhi",
        },
        {
          name: "答案溯源",
          icon: "gongneng-daansuyuan",
        },
        {
          name: "答案润色",
          icon: "daanrunse",
        },
        {
          name: "输出结果评价",
          icon: "gongneng-shuchujieguopingfen",
        },
        {
          name: "快捷指令",
          icon: "gongneng-kuaijiezhiling",
        },
        {
          name: "问题联想",
          icon: "gongneng-wentilianxiang",
        },
        {
          name: "模糊问题引导",
          icon: "gongneng-mohuwentiyindao",
        },
        {
          name:"场景设置",
          icon:"gongneng-changjing"
        },
        {
          name:"检索引擎",
          icon:"gongneng-lianwangsousuo"
        },
        {
          name:"禁用IP",
          icon:"gongneng-jinyongIP"
        },
        {
          name:"问数检索",
          icon:"gongneng-wenshujiansuo"
        }
      ],
      useLessNameList:["虚拟人","安全拦截","检索引擎","答案润色","场景设置","问数检索","模糊问题引导"]
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    getconfigFuntionListFlag: {
      type: Boolean,
      default: false,
    },
    funcOrToolArr: Array,
    params: Object,
    pluginList: {
      type: Array,
      default: () => []
    }
  },
  mounted() {
    this.getfuntionList();
  },
  methods: {
    getIcon(name) {
      // console.log(
      //   this.iconOptions.find((item) => {
      //     return item.name === name;
      //   }).icon
      // );
      const obj=this.iconOptions.find((item) => {
        return item.name === name;
      })
      return obj?obj.icon:'gongneng-duihuatiyan';
    },
    // 查询列表
    getfuntionList() {
      this.loading = true;
      applicationPluginList({
        applicationId: this.params.applicationId,
      }).then((res) => {
        if (res.code == "000000") {
          this.listAllData = res.data;
          this.getconfigFuntionListFlag && this.getconfigFuntionList();
          this.funtionList = [];
          this.toolList = [];
          if(this.pluginList.length) {
            this.listAllData.forEach((element) => {
              const matchingObject = this.pluginList.find(
                (item) => item.pluginId === element.pluginId
              );
              if (matchingObject) {
                element.status = matchingObject.status;
              } else {
                element.status = '否';
              }
            });
          }
          this.listAllData.forEach((element) => {
            // element.status = element.status ? true : false;
            if (element.pluginGroup == "插件") {
              this.toolList.push(element);
            } else {
              this.funtionList.push(element);
              
            }
          });
          this.loading = false;
        } 
      })
    },
    // 查询列表应用插件关联表
    getconfigFuntionList() {
      applicationPluginDataList({
        applicationId: this.params.applicationId,
      }).then((res) => {
        if (res.code == "000000") {
          this.funtionList = [];
          this.toolList = [];
          if (res.data.length) {
            let arr = res.data;
            this.listAllData.forEach((element) => {
              const matchingObject = arr.find(
                (item) => item.pluginId === element.pluginId
              );
              if (matchingObject) {
                element.status = matchingObject.status;
              } else {
                element.status = '否';
              }
            });
          }
          this.listAllData.forEach((element) => {
            // element.status = element.status ? true : false;
            if (element.pluginGroup == "插件") {
              this.toolList.push(element);
            } else {
              this.funtionList.push(element);
            }
          });
          this.loading = false;
        }
      });
    },
    // 新增应用插件关联表
    addPluginData() {
      addApplicationPluginData({
        applicationId: this.params.applicationId,
        pluginList: this.selectIds,
      }).then((res) => {
        if (res.code == "000000") {
          this.$message.success(this.$t("successed"));
          this.$emit("clickConfigParams", "toolVisible");
        }
      });
    },
    changeStatus(value, index) {
      console.log('==-->>', value, index,this.funtionList[index].pluginCode);
      switch (this.funtionList[index].pluginCode) {
        case 'virtual':
          this.$emit("changeStatus", "virtualHumanFlag",value);
          break;
        case 'voice':
          this.$emit("changeStatus", "voiceDialogueFlag",value);
          break;
        case 'DisableIP':
          this.$emit("changeStatus", "ipFlag",value);
          break;
        case 'recommendation':
          this.$emit("changeStatus", "recommendQuestionsShowFlag", value);
          break;
        case 'answerSource':
          this.$emit("changeStatus", "sourceShowFlag", value);
          break;
        case 'TouchAnswer':
          this.$emit("changeStatus", "polishFlag", value);
          break;
        default:
          break
      }
    },
    delTool(index) {
      this.toolList[index].status = '否';
      switch (this.toolList[index].pluginCode) {
        case 'interception':
          this.$emit("changeStatus", "sensitiveFlag", '否');
          break;
        default:
          break
      }
    },
    addTool(index) {
      this.toolList[index].status = '是';
      // 选择开启的时候，默认开启的开关
      switch (this.toolList[index].pluginCode) {
        case 'interception':
          this.$emit("changeStatus", "sensitiveFlag", '是');
          break;
        default:
          break
      }
    },
    setConfig() {
      this.selectIds = [];
      this.funtionList.forEach((element) => {
        if (element.status) {
          this.selectIds.push(element);
        }
      });
      this.toolList.forEach((element) => {
        if (element.status) {
          this.selectIds.push(element);
        }
      });
      this.addPluginData();
      this.$emit('addPluginDataEmit', this.selectIds)
    },
    cancelConfig() {
      this.selectIds = [];
      this.$emit("clickConfig", false);
    },
  },
};
</script>
      
<style lang="scss" scoped>
.applicationDialog {
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__body {
      padding: 0px 32px 16px;
    }
    .el-dialog__header {
      background-color: #ffffff;
      padding: 32px 32px 16px;
      font-weight: 500;
      font-size: 20px;
      color: #494E57;
      line-height: 24px;
      .el-dialog__headerbtn {
        top: 36px;
        right: 32px;
      }
    }
    .el-dialog__footer {
      text-align: right;
    }
  }
}
.tabs-box {
  height: 40px;
  background: #F2F4F7;
  border-radius: 4px;
  display: flex;
  align-items: center;
  padding: 2px;
  margin-bottom: 20px;
  .tabs-item {
    flex: 1;
    height: 36px;
    line-height: 36px;
    text-align: center;
    border-radius: 2px;
    font-size: 16px;
    color: #828894;
    cursor: pointer;
    &.active {
      font-weight: 500;
      color: #494E57;
      background: #FFFFFF;
      box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
    }
  }
}

::v-deep .el-tabs__active-bar {
  background-color: #1c50fd;
}

::v-deep .el-tabs__item.is-active {
  color: #383d47;
  font-size: 18px;
  font-weight: 500;
}
::v-deep .el-tabs__item {
  color: #383d47;
  font-size: 18px;
}
.list-box {
  height: 500px;
  overflow-y: auto;
  ul {
    display: flex;
    flex-wrap: wrap;
  }
}
.list-li {
  margin-right: 6px;
  margin-bottom: 12px;
  width: 322px;
  height: 100px;
  padding: 12px;
  background: #ffffff;
  border-radius: 2px;
  border: 1px solid #D5D8DE;
  .icon-img {
    width: 24px;
    height: 24px;
    border-radius: 2px;
    margin-right: 8px;
  }
  .text {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 16px;
    color: #494E57;
    line-height: 24px;
    text-align: left;
    font-style: normal;
  }
  .tips {
    font-weight: 400;
    font-size: 14px;
    color: #828894;
    line-height: 20px;
    text-align: left;
    font-style: normal;
    margin-top: 12px;
  }
  &:nth-child(2n) {
    margin-right: 0;
  }
}

.flex-center {
  display: flex;
  align-items: center;
}

.just {
  justify-content: space-between;
}
</style>
      