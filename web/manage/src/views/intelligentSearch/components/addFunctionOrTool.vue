<template>
  <el-dialog
    :configName="$t('add')"
    :visible.sync="dialogVisible"
    width="700px"
    class="applicationDialog"
    :before-close="cancelConfig"
    append-to-body
  >
    <el-tabs v-model="activeName">
      <el-tab-pane :label="$t('function')" name="first">
        <div class="list-box" v-loading="loading">
          <ul>
            <li
              class="list-li flex-center just"
              v-for="(item, index) in funtionList"
              :key="index"
            >
              <div class="flex-center">
                <img class="icon-img" :src="item.pluginIcon" />
                <div>
                  <span class="text">{{ item.pluginName }}</span>
                  <p class="tips">{{ item.remark }}</p>
                </div>
              </div>
              <el-switch
                v-model="item.status"
                active-color="#4157FE"
                inactive-color="#CED4E0"
                :active-value="$t('yes')"
                inactive-value="$t('no')"
                @change= "changeStatus($event,index)"
              >
              </el-switch>
            </li>
          </ul>
        </div>
      </el-tab-pane>
      <el-tab-pane :label="$t('plugInUnit')" name="second">
        <div class="list-box">
          <!-- <div class="flex-center just">
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
          </div> -->
          <ul>
            <li
              class="list-li flex-center just"
              v-for="(item, index) in toolList"
              :key="index"
            >
              <div class="flex-center">
                <img class="icon-img" :src="item.pluginIcon" />
                <div>
                  <span class="text">{{ item.pluginName }}</span>
                  <p class="tips">{{ item.remark }}</p>
                </div>
              </div>
              <el-button
                v-if="item.status==='是'"
                type="text"
                icon="el-icon-delete"
                style="color: #d82225"
                @click="delTool(index)"
                >{{ $t("remove") }}</el-button
              >
              <el-button
                v-else
                type="text"
                icon="el-icon-plus"
                style="color: #1c50fd"
                @click="addTool(index)"
                >{{ $t("add") }}</el-button
              >
            </li>
          </ul>
        </div>
      </el-tab-pane>
    </el-tabs>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="setConfig">{{
        $t("confirm")
      }}</el-button>
      <el-button @click="cancelConfig">{{ $t("cancel") }}</el-button>
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
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    funcOrToolArr: Array,
    params: Object,
  },
  mounted() {
    this.getfuntionList();
  },
  methods: {
    // 查询列表
    getfuntionList() {
      this.loading = true;
      applicationPluginList({
        applicationId: this.params.applicationId,
      }).then((res) => {
        if (res.code == "000000") {
          this.listAllData = res.data;
          this.getconfigFuntionList();
        } 
      });
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
        } else {
          if (res.msg == '应用已不存在') {
            this.$message.error('应用编码不能为空'); 
          } else {
            this.$message.error(res.msg);
          }
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
      padding: 16px 32px;
    }
    .el-dialog__header {
      background: linear-gradient(
        180deg,
        rgba(43, 88, 213, 0.1) 0%,
        rgba(43, 88, 213, 0) 100%
      );
      border-radius: 8px 8px 0px 0px;
    }
    .el-dialog__footer {
      text-align: center;
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
}
.list-li {
  padding: 16px;
  height: 72px;
  background: #f2f5fa;
  border-radius: 4px;
  margin-top: 12px;
  .icon-img {
    width: 40px;
    height: 40px;
    border-radius: 4px;
    margin-right: 16px;
  }
  .text {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 16px;
    color: #383d47;
    line-height: 20px;
    text-align: left;
    font-style: normal;
  }
  .tips {
    margin-top: 8px;
    font-size: 14px;
    color: #828894;
    line-height: 18px;
    text-align: left;
    font-style: normal;
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
      