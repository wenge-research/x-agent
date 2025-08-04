<template>
  <div class="new-add-function-tool-wrapper">
    <div class="content-box">
      <div class="content-box-head">
        <div class="tianjia">{{ "添加功能" }}</div>
        <!-- <div class="tabs-box">
          <div
            class="tabs-item"
            :class="{ active: activeName == 'first' }"
            @click="activeName = 'first'"
          >
            {{ $t("function") }}
          </div>
          <div
            class="tabs-item"
            :class="{ active: activeName == 'second' }"
            @click="activeName = 'second'"
          >
            {{ $t("plugInUnit") }}
          </div>
          <div
            class="tabs-item"
            :class="{ active: activeName == 'mcp' }"
            @click="activeName = 'mcp'"
          >
            <span>🔥</span>
            <span> {{ $t("mcp") }}</span>
          </div>
        </div> -->
        <div class="guanbi" @click="closeFunctionOrTool">
          <iconpark-icon
            name="close-line"
            size="18"
            color="#494E57"
          ></iconpark-icon>
        </div>
      </div>

      <div v-show="activeName == 'first'" class="list-content" style="height: calc(100% - 46px);">
        <div class="list-box" v-loading="loading">
          <ul>
            <li
              class="list-li erwe"
              v-for="(item, index) in funtionList"
              :key="index"
            >
              <div class="flex-center just">
                <div class="flex-center">
                  <!-- <img class="icon-img" :src="item.pluginIcon" /> -->
                  <!-- <iconpark-icon  class="icon-img" size="24" :name="getIcon(item.pluginName)"></iconpark-icon> -->
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
                  @change="changeStatus($event, index)"
                >
                </el-switch>
                <!-- <div class="right-opreate">
                  <div v-if="item.status == '否'" class="add-icon" @click="changeStatus(item,index)"><iconpark-icon name="add-line" color="#1747E5" size="14" style="margin-right: 8px"></iconpark-icon>添加</div>
                  <div v-else class="delete-icon" @click="changeStatus(item,index)"><iconpark-icon name="delete-bin-4-line" color="#B52119" size="14" style="margin-right: 8px"></iconpark-icon>移除</div>
                </div> -->
              </div>
              <p class="tips">{{ item.remark }}</p>
            </li>
          </ul>
        </div>
      </div>
      <div v-show="activeName == 'second'" class="list-content" style="height: calc(100% - 46px);">
        <div class="flex-center just" style="margin-bottom: 20px">
          <el-input
            :placeholder="$t('enterPluginName')"
            suffix-icon="el-icon-search"
            v-model="searchToolName"
            style="width: 100%"
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
                <p class="tips">{{ item.remark }}{{ item.status }}</p>
              </div>
              <el-button
                v-if="item.status === '是'"
                type="text"
                size="small"
                style="color: #b52119"
                @click="delTool(index)"
                class="delete-btn"
                ><iconpark-icon
                  name="delete-bin-4-line"
                  color="#D33A22"
                  size="14"
                  style="margin-right: 6px"
                ></iconpark-icon
                >{{ $t("remove") }}</el-button
              >
              <el-button
                v-else
                type="text"
                size="small"
                style="color: #494e57"
                @click="addTool(index)"
                ><iconpark-icon
                  name="add-line"
                  color="#1747E5"
                  size="14"
                  style="margin-right: 6px"
                ></iconpark-icon
                >{{ $t("add") }}</el-button
              >
            </li>
          </ul>
        </div>
      </div>
      <div v-show="activeName == 'mcp'" class="list-content" style="height: calc(100% - 46px);">
        <div class="flex-center just" style="margin-bottom: 20px">
          <el-input
            placeholder="搜索MCP服务名"
            prefix-icon="el-icon-search"
            v-model="mcpName"
            style="width: 100%"
          >
          </el-input>
        </div>
        <div class="list-box" style="height: 70vh;">
          <ul>
            <li
              class="list-li erwedd flex-center just"
              v-for="(item, index) in mcpList"
              :key="index"
            >
              <div style="width: 100%">
                <div class="flex-center just">
                  <div class="flex-center">
                    <img class="icon-img" :src="item.icon" />
                    <span class="text">{{ item.mcpName }}</span>

                    <div
                      class="tool-ctn flex-center"
                      style="margin-left: 16px; gap: 8px"
                      @click="expandTools(index)"
                    >
                      <span>工具{{ item.total }}</span>
                      <iconpark-icon
                        name="arrow-down-s-line"
                        color="#86909c"
                        :style="{
                          transform: mcpToolList[index]
                            ? 'rotate(180deg)'
                            : 'rotate(0deg)',
                        }"
                        style="transition: 0.5s"
                      ></iconpark-icon>
                      
                    </div>
                  </div>

                  <div class="btns-ctn">
                    <!-- 已添加/删除 -->
                    <div
                      class="btn added-remove"
                      v-if="item.status == '是'"
                      ref="removeBtnRef"
                      @click="curMcpHover == index ? delMcp(index) : () => {}"
                      @mouseover="btnMouseOver($event, index)"
                      @mouseleave="btnMouseOut($event, index)"
                    >
                      <span v-if="curMcpHover != index">{{
                        $t("alreadyAdd")
                      }}</span>
                      <div
                        class="flex-center remove-btn"
                        v-if="curMcpHover == index"
                      >
                        <iconpark-icon
                          name="delete-bin-4-line"
                          color="#D33A22"
                          size="15"
                        ></iconpark-icon>
                        <span>{{ $t("remove") }}</span>
                      </div>
                    </div>

                    <!-- 添加按钮 -->
                    <div class="btn add" v-else @click="addMcp(index)">
                      <iconpark-icon
                        name="add-line"
                        color="#1d2129"
                        size="12"
                      ></iconpark-icon>
                    </div>

                    <!-- <el-button
                      v-if="item.status == '是'"
                      type="text"
                      size="small"
                      style="color: #b52119"
                      @click="delMcp(index)"
                      class="delete-btn"
                      ><iconpark-icon
                        name="delete-bin-4-line"
                        color="#D33A22"
                        size="14"
                        style="margin-right: 6px"
                      ></iconpark-icon
                      >{{ $t("alreadyAdd") }}</el-button
                    > -->
                    <!-- <el-button
                      v-else
                      type="text"
                      size="medium"
                      style="color: #000"
                      @click="addMcp(index)"
                      ><iconpark-icon
                        name="add-line"
                        color="#000"
                        size="20"
                        style="margin-right: 6px"
                      ></iconpark-icon
                      >{{ $t("add") }}</el-button
                    > -->
                  </div>
                </div>
                <p class="tips list-item-label" :title="item.description">
                  {{ item.description ? item.description : "暂无描述" }}
                </p>
                <div
                  class="tool-list"
                  :style="{
                    'height': mcpToolList[index] ? mcpToolHeightList[index]+'px' : '0px',
                  }"
                >
                <div class="tool-list-fa" ref="toolListRef">
                   <div
                    class="tool-card flex-center"
                    v-for="(item, index) in item.mcpFunctionList"
                    :key="index + 'mcpTools'"
                  >
                    <div class="logo-ctn">
                      <iconpark-icon name="tools-line" size="14" color="#000"></iconpark-icon>
                    </div>
                    <div class="intro-ctn">
                      <p class="intro-name">
                        {{ item.functionName }}
                      </p>
                      <p class="intro-word">
                        {{ item.description ? item.description : "暂无描述" }}
                      </p>
                    </div>
                  </div>
                </div>
                 
                </div>
              </div>
              <!-- <el-button
                v-if="item.status == '是'"
                type="text"
                size="small"
                style="color: #b52119"
                @click="delMcp(index)"
                class="delete-btn"
                ><iconpark-icon
                  name="delete-bin-4-line"
                  color="#D33A22"
                  size="14"
                  style="margin-right: 6px"
                ></iconpark-icon
                >{{ $t("remove") }}</el-button
              >
              <el-button
                v-else
                type="text"
                size="small"
                style="color: #494e57"
                @click="addMcp(index)"
                ><iconpark-icon
                  name="add-line"
                  color="#1747E5"
                  size="14"
                  style="margin-right: 6px"
                ></iconpark-icon
                >{{ $t("add") }}</el-button
              > -->
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { applicationPluginList, applicationPluginDataList } from "@/api/app";
import {
  getMcpServerList,
  deleteMcpServer,
  getMyOpenMcpServerList,
  getDetail,
} from "@/api/mcp";
import { debounce } from "lodash";
export default {
  name: "addFunctionOrToolNew",
  props: {
    getconfigFuntionListFlag: {
      type: Boolean,
      default: false,
    },
    funcOrToolArr: Array,
    mcpIdList: Array,
    params: Object,
    pluginList: {
      type: Array,
      default: () => [],
    },
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
      // drawerVisible: false,
      visibleChange: false,
      oldFuncOrToolArr: [],
      mcpNameList: [],
      isMcpList: [],
      mcpHoverList: [],
      curMcpHover: -1,
      mcpToolList: [],
      mcpToolHeightList:[],
      mcpName: "",
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
    };
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
    },
    mcpList() {
      if (this.mcpName) {
        return this.mcpNameList.filter((item) => {
          return item.mcpName.includes(this.mcpName);
        });
      } else {
        return this.mcpNameList;
      }
    },
  },
  mounted() {
    this.searchToolName = "";
    this.oldFuncOrToolArr = JSON.parse(JSON.stringify(this.funcOrToolArr));
    this.getfuntionList();
    this.getLlmPageList();
    this.$EventBus.$on("deleteMcps", (val) => {
      this.mcpNameList.forEach((element, index) => {
        if (val.includes(element.mcpId)) {
          element.status = "是";
        } else {
          element.status = "否";
        }
      });
    });
    this.$EventBus.$on("deleteOrTool", (val) => {
      console.log("this.toolList", val);
      this.toolList.forEach((element) => {
        if (val.includes(element.pluginCode)) {
          element.status = "是";
        } else {
          element.status = "否";
        }
      });
      console.log("this.toolList", this.toolList);
    });
  },
  methods: {
    getIcon(name) {
      const obj=this.iconOptions.find((item) => {
        return item.name === name;
      })
      return obj?obj.icon:'gongneng-duihuatiyan';
    },
    closeFunctionOrTool() {
      this.$emit("closeFunctionOrTool", false);
      const flag = this.compareArrays(
        this.funcOrToolArr,
        this.oldFuncOrToolArr
      );
      if (!flag) {
        this.$EventBus.$emit("changeApplicationStatus", true);
        this.$EventBus.$emit("saveApplication");
      }
    },
    closePopover() {
      this.$refs.myPopover.doClose();
    },
    // 查询mcp列表
    getLlmPageList() {
      const params = {
        mcpName: this.mcpName,
        pageNo: 1,
        pageSize: 12000,
      };
      this.loading = true;
      getMyOpenMcpServerList(params).then((res) => {
        if (res.code == "000000") {
          if (res.data) {
            this.mcpList = res.data || [];
            this.mcpNameList = res.data || [];
            this.mcpHoverList = [];
            this.mcpToolList = [];
            this.mcpNameList.forEach((element, index) => {
              this.mcpHoverList.push(false);
              this.mcpToolList.push(false);
              if (this.mcpIdList.includes(element.mcpId)) {
                element.status = "是";
              } else {
                element.status = "否";
              }
              this.getMcpDetail(element.mcpId, index);
            });
            this.mcpList.forEach((element) => {
              if (this.mcpIdList.includes(element.mcpId)) {
                element.status = "是";
              } else {
                element.status = "否";
              }
            });
          }
        }
      });
    },
    getMcpDetail(mcpId, index) {
      getDetail(mcpId).then((res) => {
        if (res.code == "000000") {
          const { mcpFunctionList } = res.data;
          this.mcpNameList[index] = {
            ...this.mcpNameList[index],
            mcpFunctionList,
            total: mcpFunctionList.length,
          };
        }
      });
    },
    // visibleChangeShow() {
    //   console.log(this.funcOrToolArr, '-------')
    //   this.visibleChange = true;
    //   this.searchToolName = "";
    //   this.oldFuncOrToolArr = JSON.parse(JSON.stringify(this.funcOrToolArr));
    //   this.getfuntionList();
    // },
    // closeDrawer() {
    //   this.drawerVisible = false;
    // },
    // 查询列表
    getfuntionList() {
      this.loading = true;
      applicationPluginList({
        applicationId: this.params.applicationId,
      }).then((res) => {
        if (res.code == "000000") {
          this.listAllData = res.data.filter(
            (item) =>
              ![
                "wenshuFlag",
                "virtual",
                "DisableIP",
                "Authentication",
              ].includes(item.pluginCode)
          );
          this.getconfigFuntionListFlag && this.getconfigFuntionList();
          this.funtionList = [];
          this.toolList = [];
          if (this.pluginList.length) {
            this.listAllData.forEach((element) => {
              const matchingObject = this.pluginList.find(
                (item) => item.pluginId === element.pluginId
              );
              if (matchingObject) {
                element.status = matchingObject.status;
              } else {
                element.status = "否";
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
            if (this.funcOrToolArr.includes(element.pluginCode)) {
              element.status = "是";
            } else {
              element.status = "否";
            }
          });
          this.loading = false;
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
                element.status = "否";
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
      // if(item.status == '是') {
      //   item.status = '否'
      // } else {
      //   item.status = '是'
      // }
      // let value = item.value;
      console.log("==-->>", value, index, this.funtionList[index]);
      switch (this.funtionList[index].pluginCode) {
        case "virtual":
          this.$emit("changeStatus", "virtualHumanFlag", value);
          break;
        case "voice":
          this.$emit("changeStatus", "voiceDialogueFlag", value);
          break;
        case "DisableIP":
          this.$emit("changeStatus", "ipFlag", value);
          break;
        case "recommendation":
          this.$emit("changeStatus", "recommendQuestionsShowFlag", value);
          break;
		case "association":
		  this.$emit("changeStatus", "associationQuestionsShowFlag", value);
		  break;
        case "answerSource":
          this.$emit("changeStatus", "sourceShowFlag", value);
          break;
        case "TouchAnswer":
          this.$emit("changeStatus", "polishFlag", value);
          break;
        default:
          this.$emit("changeStatus", this.funtionList[index].pluginCode, value);
          break;
      }
      // this.setConfig();
      this.setConfig(this.funtionList[index].pluginGroup);
    },
    //mcp增加及删除
    delMcp: debounce(function (index) {
      this.$nextTick(() => {
        this.$set(this.mcpNameList[index], "status", "否");
        // this.mcpList[index].status = "否";
        this.setMcpConfig(index);
        console.log("this.isMcpList", index, this.mcpList);
      });
    }, 400),
    addMcp: debounce(function (index) {
      this.$nextTick(() => {
        this.$set(this.mcpNameList[index], "status", "是");
        // this.$set(this.mcpList[index],"status","是")
        // this.mcpList[index].status = "是";
        this.setMcpConfig(index);
        console.log("this.isMcpList", index, this.mcpList);
        this.$forceUpdate();
      });
    }, 400),
    setMcpConfig(index) {
      this.isMcpList = [];
      this.mcpList.forEach((element) => {
        if (element.status == "是") {
          this.isMcpList.push(element);
        }
      });

      // this.addPluginData();
      this.$emit("addMcpDataEmit", this.isMcpList, this.mcpList[index].status);
      console.log("this.isMcpList", this.isMcpList);
    },
    //插件增加及删除
    delTool(index) {
      this.toolList[index].status = "否";
      switch (this.toolList[index].pluginCode) {
        case "interception":
          this.$emit("changeStatus", "sensitiveFlag", "否");
          break;
        default:
          break;
      }
      this.setConfig(this.toolList[index].pluginGroup);
    },
    addTool(index) {
      this.toolList[index].status = "是";
      // 选择开启的时候，默认开启的开关
      console.log("==-->>", this.toolList[index]);

      switch (this.toolList[index].pluginCode) {
        case "interception":
          this.$emit("changeStatus", "sensitiveFlag", "是");
          break;
        default:
          break;
      }
      this.setConfig(this.toolList[index].pluginGroup);
    },
    setConfig(type) {
      console.log("setConfig");

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
      this.$emit("addPluginDataEmit", this.selectIds, type);
      console.log("this.selectIds", this.selectIds);
    },
    cancelConfig() {
      this.selectIds = [];
      this.$emit("clickConfig", false);
    },
    handleHide() {
      this.visibleChange = false;
      const flag = this.compareArrays(
        this.funcOrToolArr,
        this.oldFuncOrToolArr
      );
      if (!flag) {
        this.$EventBus.$emit("changeApplicationStatus", true);
        this.$EventBus.$emit("saveApplication");
      }
    },
    compareArrays(arr1, arr2) {
      if (arr1.length !== arr2.length) {
        return false;
      }
      const sortedArr1 = arr1.slice().sort();
      const sortedArr2 = arr2.slice().sort();
      for (let i = 0; i < sortedArr1.length; i++) {
        if (sortedArr1[i] !== sortedArr2[i]) {
          return false;
        }
      }
      return true;
    },
    // MCP按钮mouseover
    btnMouseOver(e, index) {
      this.curMcpHover = index;
      this.$set(this.mcpHoverList, index, true);
    },
    // MCP按钮mouseout
    btnMouseOut(e, index) {
      this.curMcpHover = -1;
      this.$set(this.mcpHoverList, index, false);
    },
    // 展开工具
    expandTools(index) {
      this.$set(this.mcpToolList, index, !this.mcpToolList[index]);
      this.$set(this.mcpToolHeightList,index,this.$refs.toolListRef[index].clientHeight)
    },
  },
};
</script>
<style lang="scss" scoped>
.new-add-function-tool-wrapper {
  // cursor: pointer;
  .add-function-tool-btn {
    display: flex;
    align-items: center;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #494e57;
    height: 40px;
    border-radius: 2px;
    padding: 0 16px;
    .btn-icon {
      margin-right: 8px;
    }
    &.active {
      background: #f0f2f5;
    }
  }
}
.list-content {
  overflow-y: auto;
  
  /* 隐藏 WebKit 浏览器的滚动条 */
  &::-webkit-scrollbar {
    display: none !important;
  }
  
  /* Firefox 隐藏滚动条 */
  scrollbar-width: none;
  
  /* IE/Edge 隐藏滚动条 */
  -ms-overflow-style: none;
}
</style>
<style lang="scss">
.new-add-function-tool-wrapper {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  background: #ffffff;
  // box-shadow: 0px 4px 12px 0px rgba(21,34,51,0.1);
  // border-radius: 2px;
  // border: 0px;
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
  .content-box {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 16px;
    &-head {
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 16px;
      height: 32px;
      .tianjia {
        position: absolute;
        left: 0;
        height: 32px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #1D2129;
        line-height: 32px;
      }
      .guanbi {
        position: absolute;
        right: 0;
        cursor: pointer;
      }
      .list-content {
        flex: 1;
        overflow-y: auto;
        display: flex;
        flex-direction: column;
        
        /* 隐藏 WebKit 浏览器的滚动条 */
        &::-webkit-scrollbar {
          display: none !important;
        }
        
        /* Firefox 隐藏滚动条 */
        scrollbar-width: none;
        
        /* IE/Edge 隐藏滚动条 */
        -ms-overflow-style: none;
      }
    }
  }
  .tabs-box {
    // width: 184px;
    // height: 28px;
    background: #f2f4f7;
    border-radius: 4px;
    display: flex;
    align-items: center;
    // padding: 2px;
    .tabs-item {
      // width: 92px;
      padding: 7px 12px;
      // height: 28px;
      // line-height: 28px;
      text-align: center;
      border-radius: 2px;
      font-size: 14px;
      color: #828894;
      cursor: pointer;
      &.active {
        font-weight: 500;
        color: #494e57;
        background: #ffffff;
        box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
      }
    }
  }
  .list-box {
    flex: 1;
    // overflow-y: auto;
    ul {
      // padding-right: 8px;
    }
  }
  .list-li {
    width: 100%;
    margin-bottom: 12px;
    // height: 76px;
    padding: 12px;
    background: #ffffff;
    border-radius: 2px;
    border: 1px solid #d5d8de;
    font-family: MiSans, MiSans;

    &:hover {
      .btns-ctn {
        display: block;
      }
    }

    .icon-img {
      width: 32px;
      height: 32px;
      border-radius: 2px;
      margin-right: 8px;
    }
    .text {
      font-weight: 500;
      font-size: 16px;
      color: #494e57;
      line-height: 32px;
      text-align: left;
      font-style: normal;
    }

    .tool-ctn {
      color: #86909c;
      font-size: 14px;
      cursor: pointer;
    }
    .list-item-label {
      color: #666;
      margin-top: 20px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      line-height: 22px;
    }

    // btn
    .btns-ctn {
      display: none;
      .btn {
        height: 32px;
        border-radius: 4px;
        cursor: pointer;
      }

      .add {
        width: 32px;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #bcc1cc33;
        color: #1d2129;
        font-size: 12px;
      }

      .added-remove {
        line-height: 32px;
        padding: 0 12px;
        background-color: #bcc1cc33;
        display: flex;
        justify-content: center;
        align-content: center;
        font-size: 14px;
        color: #86909c;

        &:hover {
          background-color: #ffebe8;
        }

        .remove-btn {
          align-content: center;
          gap: 7px;
          color: #f53f3f;
        }
      }
    }

    .tips {
      width: 100%;
      font-weight: 400;
      font-size: 14px;
      color: #828894;
      line-height: 20px;
      text-align: left;
      font-style: normal;
      margin-top: 8px;
      // height: 20px;
      // overflow: hidden;
      // text-overflow: ellipsis;
      // white-space: nowrap;
    }

    .tool-list {
      margin-top: 12px;
      transition: height 0.5s ease-in-out;
      overflow: hidden;
      will-change: height;

      .tool-list-fa{
        display: flex;
        flex-direction: column;
        gap: 8px;
      }

      .tool-card {
        width: 100%;
        height: 48px;
        background: #f7f8fa;
        border-radius: 4px;
        padding: 0 12px;
        box-sizing: border-box;
        gap: 12px;

        .logo-ctn {
          width: 24px;
          height: 24px;
          border-radius: 2px;
          background: #e7e7e7;
          display: flex;
          justify-content: center;
          align-content: center;

          svg{
            height: 14px;
            width: 14px;
          }
        }

        .intro-ctn {
          width: calc(100% - 36px);
          font-size: 12px;
          display: flex;
          flex-direction: column;
          gap: 4px;
          justify-content: center;

          .intro-name {
            line-height: 16px;
            color: #1d2129;
          }

          .intro-word {
            width: 100%;
            line-height: 16px;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 1;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
      }
    }
    .right-opreate {
      width: 80px;
      height: 32px;
      font-size: 14px;
      color: #494e57;
      font-weight: 600;
      border-radius: 2px;
      cursor: pointer;

      .add-icon {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        &:hover {
          background: rgba(23, 71, 229, 0.1);
        }
      }
      .delete-icon {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        &:hover {
          background: rgba(181, 33, 25, 0.1);
          color: #b52119;
        }
      }
    }
  }
  .erwedd {
    .el-button {
      padding: 0 15px;
      border-radius: 2px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494e57;
      line-height: 28px;
      > span {
        display: inline-flex;
        align-items: center;
        justify-content: center;
      }
      &:hover {
        background: rgba(23, 71, 229, 0.1);
      }
    }
    .delete-btn {
      &:hover {
        background: rgba(181, 33, 25, 0.1);
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
