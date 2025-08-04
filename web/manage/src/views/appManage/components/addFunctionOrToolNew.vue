<template>
  <div class="add-function-tool-wrapper">
    <el-popover
      placement="bottom-end"
      title="添加"
      width="560"
      trigger="click"
      content=""
      popper-class="add-function-tool-popover"
      @show="visibleChangeShow"
      @hide="handleHide"
      ref="myPopover"
    >
      <div slot="reference" class="add-function-tool-btn" :class="{ 'active': visibleChange}">
        <iconpark-icon class="btn-icon" name="function-add-fill" color="#494E57" size="20"></iconpark-icon>功能/插件
      </div>
      <div class="content-box">
        <div class="tabs-box">
          <div class="tabs-item" :class="{ active: activeName == 'first' }" @click="activeName = 'first'">{{ $t('function') }}</div>
          <div class="tabs-item" :class="{ active: activeName == 'second' }" @click="activeName = 'second'">{{ $t('plugInUnit') }}</div>
        </div>
        <div v-show="activeName == 'first'" class="list-content">
          <div class="list-box" v-loading="loading">
            <ul>
              <li
                class="list-li erwe"
                v-for="(item, index) in funtionList"
                :key="index"
              >
                <div class="flex-center just">
                  <div class="flex-center">
                    <img class="icon-img" :src="item.pluginIcon" />
                    <span class="text">{{ item.pluginName }}</span>
                  </div>
                  <el-switch
                    v-model="item.status"
                    active-color="#4157FE"
                    inactive-color="#CED4E0"
                    active-value="是"
                    inactive-value="否"
                    @change="changeStatus($event,index)"
                  >
                </el-switch>
                </div>
                <p class="tips">{{ item.remark }}</p>
                  
                
                
              </li>
            </ul>
          </div>
        </div>
        <div v-show="activeName == 'second'" class="list-content">
          <div class="flex-center just" style="margin-bottom: 20px;">
            <el-input
              :placeholder="$t('inputToolKeywords')"
              suffix-icon="el-icon-search"
              v-model="searchToolName"
              style="width: 286px"
            >
            </el-input>
            <!-- <el-button
              type="primary"
              icon="el-icon-circle-plus"
              size="medium"
              plain
              style="color: #1c50fd; border-color: #1c50fd; background: none"
            >
              {{ $t("createTools") }}
            </el-button> -->
          </div>
          <div class="list-box">
            
            <ul>
              <li
                class="list-li erwedd flex-center just"
                v-for="(item, index) in showToolList"
                :key="index"
              >
                <div class="">
                  <div class="flex-center">
                    <img class="icon-img" :src="item.pluginIcon" />
                    <span class="text">{{ item.pluginName }}</span>
                  </div>
                  <p class="tips">{{ item.remark }}</p>
                </div>
                <el-button
                  v-if="item.status==='是'"
                  type="text"
                  size="small"
                  style="color: #d82225"
                  @click="delTool(index)"
                  ><iconpark-icon name="delete-bin-4-line" color="#d82225" size="14" style="margin-right: 4px;"></iconpark-icon>{{ $t("remove") }}</el-button
                >
                <el-button
                  v-else
                  type="text"
                  size="small"
                  icon="el-icon-plus"
                  style="color: #1c50fd"
                  @click="addTool(index)"
                  >{{ $t("add") }}</el-button
                >
                
              </li>
            </ul>
          </div>
        </div>
      
        <!-- <span slot="footer" class="dialog-footer">
        
          <el-button @click="cancelConfig">{{ $t("cancel") }}</el-button>
          <el-button type="primary" @click="setConfig">{{
            $t("confirm")
          }}</el-button>
        </span> -->
      </div>
    </el-popover>
  </div>
</template>
<script>
import {
  applicationPluginList,
  applicationPluginDataList,
} from "@/api/app";
export default {
  name: 'addFunctionOrToolNew',
  props: {
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
  data() {
    return {
      activeName: "first",
      listAllData: [],
      funtionList: [],
      toolList: [],
      searchToolName: "",
      selectIds: [],
      loading: false,
      drawerVisible: false,
      visibleChange: false,
      oldFuncOrToolArr: [],
    }
  },
  computed: {
    showToolList() {
      if (this.searchToolName) {
        return this.toolList.filter((item) => {
          return item.pluginName.includes(this.searchToolName);
        });
      } else {
        return this.toolList;
      }
    }
  },
  methods: {
    closePopover() {
      this.$refs.myPopover.doClose();
    },
    visibleChangeShow() {
      console.log(this.funcOrToolArr, '-------')
      this.visibleChange = true;
      this.searchToolName = "";
      this.oldFuncOrToolArr = JSON.parse(JSON.stringify(this.funcOrToolArr));
      this.getfuntionList();
    },
    closeDrawer() {
      this.drawerVisible = false;
    },
    // 查询列表
    getfuntionList() {
      this.loading = true;
      applicationPluginList({
        applicationId: this.params.applicationId,
      }).then((res) => {
        if (res.code == "000000") {
          this.listAllData = res.data.filter((item)=> !['wenshuFlag', 'virtual', 'DisableIP', 'Authentication'].includes(item.pluginCode));
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
          this.toolList.forEach((element) => {
            if(this.funcOrToolArr.includes(element.pluginCode)) {
              element.status = '是'
            }else {
              element.status = '否'
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
      // addApplicationPluginData({
      //   applicationId: this.params.applicationId,
      //   pluginList: this.selectIds,
      // }).then((res) => {
      //   if (res.code == "000000") {
      //     this.$message.success(this.$t("successed"));
      //     this.$emit("clickConfigParams", "toolVisible");
      //   }
      // });
    },
    changeStatus(value, index) {
      console.log('==-->>', value, index,this.funtionList[index]);
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
      this.setConfig();
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
      this.setConfig();
    },
    addTool(index) {
      this.toolList[index].status = '是';
      // 选择开启的时候，默认开启的开关
      console.log('==-->>', this.toolList[index]);
      
      switch (this.toolList[index].pluginCode) {
        case 'interception':
          this.$emit("changeStatus", "sensitiveFlag", '是');
          break;
        default:
          break
      }
      this.setConfig();
    },
    setConfig() {
      console.log('setConfig');
      
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
      // this.addPluginData();
      this.$emit('addPluginDataEmit', this.selectIds)
    },
    cancelConfig() {
      this.selectIds = [];
      this.$emit("clickConfig", false);
    },
    handleHide() {
      this.visibleChange = false;
      const flag = this.compareArrays(this.funcOrToolArr, this.oldFuncOrToolArr);
      if(!flag) {
        this.$EventBus.$emit("changeApplicationStatus", true);
        this.$EventBus.$emit("saveApplication");
      }
    },
    compareArrays(arr1, arr2) {
      if (arr1.length!== arr2.length) {
        return false;
      }
      const sortedArr1 = arr1.slice().sort();
      const sortedArr2 = arr2.slice().sort();
      for (let i = 0; i < sortedArr1.length; i++) {
        if (sortedArr1[i]!== sortedArr2[i]) {
          return false;
        }
      }
      return true;
    }
  },
}
</script>
<style lang="scss" scoped>
.add-function-tool-wrapper {
  cursor: pointer; 
  .add-function-tool-btn {
    display: flex;
    align-items: center;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #494E57;
    height: 40px;
    border-radius: 2px;
    padding: 0 16px;
    .btn-icon {
      margin-right: 8px;
    
    }
    &.active {
      background: #F0F2F5;
    }
  } 
  

}
</style>
<style lang="scss">
.add-function-tool-popover {
  width: 560px;
  box-sizing: border-box;
  background: #FFFFFF;
  box-shadow: 0px 4px 12px 0px rgba(21,34,51,0.1);
  border-radius: 2px;
  border: 0px;
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
  .content-box {
    max-height: 588px;
    display: flex;
    flex-direction: column;
    padding: 0 24px 16px;
    .list-content {
      flex: 1; 
      overflow-y: auto;
      display: flex;
      flex-direction: column;
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
  .list-box {
    flex: 1;
    overflow-y: auto;
    ul {
      padding-right: 8px;
    }
  }
  .list-li {
    margin-bottom: 12px;
    height: 76px;
    padding: 12px;
    background: #ffffff;
    border-radius: 2px;
    border: 1px solid #D5D8DE;
    font-family: MiSans, MiSans;

    .icon-img {
      width: 24px;
      height: 24px;
      border-radius: 2px;
      margin-right: 8px;
    }
    .text {
      
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
      margin-top: 8px;
    }
  }
  .erwedd {
    .el-button {
      padding: 0 12px;
      border-radius: 2px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #1C50FD;
      line-height: 32px;
      > span {
        display: inline-flex;
        align-items: center;
        justify-content: center;
      }
      
    }
  }

  .flex-center {
    display: flex;
    align-items: center;
  }

  .just {
    justify-content: space-between;
  }
  .popper__arrow {
    display: none;
  }
}
</style>