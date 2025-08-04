<template>
  <div class="mattertype-details" v-loading="addLoading">
    <div class="mattertype-details-head">
      <div class="left">
        <iconpark-icon
          class="back-icon"
          name="arrow-go-back-fill"
          size="16"
          color="#828894"
          @click="comeBack"
        ></iconpark-icon>
        <div>
            <img v-if="dialogForm.icon" :src="dialogForm.icon" alt="" style="width: 40px;height: 40px;padding-right: 5px;"/>
              <img v-else src="@/assets/images/scene-icon.png" alt="" style="width: 40px;height: 40px;padding-right: 5px;"/>
        </div>
        <div>
          <div class="left-top">
            {{ dialogForm.sceneName }}
            <iconpark-icon
              name="edit-line"
              size="18"
              color="#828894"
              @click="updateScene"
              :title="$t('edit')"
            ></iconpark-icon>
          </div>
          <div class="left-desc">{{ dialogForm.matterDesc }}</div>
        </div>
      </div>
      <div class="center" v-if="false">
            <span v-for="(item,index) in tabList" :key="index" :class="activeIndex == index ? 'active' : 'default'">
                <!-- <iconpark-icon :name="item.iconName" :color="activeIndex == index ? '#494E57':'#828894'" /> -->
                {{$t(item.label)}}</span>
      </div>
      <div class="right">
        <el-switch
          v-model="sourceData.status"
          active-value="1"
          inactive-value="0"
        />
        <div>
          {{ sourceData.status == "1" ? $t("enabled") : $t("notEnabled") }}
        </div>
        <el-button
          type="primary"
          class="btns"
          @click="save"
          >
          <img src="@/assets/images/save-line1.svg" />
          <span>
          {{ $t("save") }}
          </span>
          </el-button>
      </div>
    </div>
    <div class="mattertype-details-content">
      <div class="base-info">
        <div class="base-info-title">
          <img src="@/assets/images/base-info-img.png" alt="" />
          <div>{{ $t("basicInformation") }}</div>
        </div>
        <div class="base-info-form">
          <el-form :model="baseInfo" ref="baseInfo">
            <el-form-item :label="$t('triggerPromptWords')">
              <el-input
                v-model="baseInfo.systemPrompt"
                :placeholder="$t('pleaseEnter')"
                clearable
                type="textarea"
                :rows="6"
                show-word-limit
                maxlength="2000"
              />
            </el-form-item>
            <el-form-item :label="$t('sceneAlias')">
              <el-input
                v-model="baseInfo.aliasName"
                :placeholder="$t('pleaseEnter')"
                clearable
              />
            </el-form-item>
            <el-form-item :label="$t('sceneNotes')">
              <el-input
                v-model="baseInfo.matterDesc"
                :placeholder="$t('pleaseEnter')"
                clearable
                type="textarea"
                :rows="4"
                show-word-limit
                maxlength="2000"
              />
            </el-form-item>
            <!-- 处理方式 -->
            <el-form-item :label="$t('handlingMethod')" class="form-column">
              <el-select v-model="processList.way" style="width: 100%">
                <el-option
                  v-for="item in processingList"
                  :key="item.id"
                  :label="item.method_name"
                  :value="item.method_name"
                  clearable
                />
              </el-select>
              <div
                v-for="(itemData, index) in handledingListData"
                :key="index"
                class="handle-item"
              >
                <el-select
                  v-model="itemData.name"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                  style="width: 140px; margin-right: 8px"
                  @change="handleSelect"
                >
                  <el-option
                    v-for="item in handlingFourList"
                    :key="item.name"
                    :label="item.name"
                    :value="item.name"
                    :disabled="item.disabled"
                  ></el-option>
                </el-select>
                <el-input
                  v-model="itemData.content"
                  :placeholder="placrholdername[itemData.name]"
                  class="input"
                ></el-input>
                <iconpark-icon
                  name="forbid-line"
                  size="16"
                  style="margin-left: 12px; cursor: pointer"
                  @click="deleteClick(itemData.name, index)"
                ></iconpark-icon>
              </div>
              <div
                style="margin-top: 12px"
                v-if="handledingListData.length < 4"
              >
                <el-button
                  plain
                  style="border-radius: 4px; border: 1px solid #c4c6cc"
                  @click="addNewHandleing"
                >
                  <div class="addbtn">
                    <iconpark-icon
                      name="add-line"
                      color="#828894"
                    ></iconpark-icon>
                    <span class="add-name">{{ $t("add") }}</span>
                  </div>
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="collect-form">
        <div class="collect-form-head">
          <div class="collect-form-head-title">
            <iconpark-icon
              name="survey-fill"
              size="22"
              color="#828894"
            ></iconpark-icon>
            <div>{{ $t("executionMatters") }}</div>
          </div>
          <el-button type="text" style="color: #1c50fd" @click="addItem"
            ><i class="el-icon-plus"></i> {{ $t("addItem") }}</el-button
          >
        </div>
        <div class="fields">
          <div v-for="(groupItem, groupIdx) in newGroupList" :key="groupIdx">
            <div class="fields-group">
              <div>{{ groupItem.title || $t("groupName") }}</div>
              <el-switch
                v-if="groupItem.title"
                style="margin-left: auto"
                v-model="groupItem.status"
                active-value="1"
                inactive-value="0"
                @change="switchChange(groupItem)"
              />
            </div>
            <div
              class="fields-item"
              v-for="(item, index) in groupItem.list"
              :key="index"
              :class="[item.id == selectFormId ? 'row-border' : '']"
              @click="formClick(item)"
              draggable="true"
              @dragstart="dragStart(index, groupItem.list)"
              @dragover="dragOver(index, groupItem.list)"
              @drop="drop(index, groupItem.list)"
            >
              <img
                v-if="index != 0"
                class="scene-triangle"
                src="@/assets/images/scene-triangle.png"
                alt=""
              />
              <div class="left">
                <iconpark-icon
                  name="draggable"
                  size="22"
                  color="#B4BCCC"
                ></iconpark-icon>
              </div>
              <div class="center">
                <div class="fields-name">
                  {{ matterNameOperate(item.matterId) || $t("matterName") }}
                </div>
                <div v-if="item.requiredFlag == 1" class="fields-tips">
                  {{ $t("necessary") }}
                </div>
              </div>
              <div class="right">
                <img src="@/assets/images/scene-file.png" alt="" />
                <!-- <img src="@/assets/images/scene-link.png" alt=""> -->
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="form-config">
        <div class="form-config-title">
          <div class="txt">{{ $t("itemConfiguration") }}</div>
          <div
            v-show="matterGuideFiledList.length"
            class="delete-group"
            @click="deleteGroup"
          >
            删除
          </div>
        </div>
        <div class="form-config-content">
          <el-form
            :model="fieldInformationForm"
            :rules="rules"
            ref="overallForm"
            class="configForm"
          >
            <el-form-item label="">
              <el-checkbox
                v-model="fieldInformationForm.requiredFlag"
                @change="
                  checkboxChange(
                    fieldInformationForm.requiredFlag,
                    'requiredFlag'
                  )
                "
                >{{ $t("necessary") }}</el-checkbox
              >
            </el-form-item>
            <el-form-item :label="$t('matterName')" prop="matterId">
              <el-select
                v-model="fieldInformationForm.matterId"
                :placeholder="$t('selectPlaceholder')"
                style="flex: 1"
                filterable
                @change="
                  selectChange(fieldInformationForm.matterId, 'matterId')
                "
              >
                <el-option
                  v-for="(item, index) in matterIdList"
                  :key="index"
                  :label="item.matterName"
                  :value="item.matterId"
                />
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('operationTips')" prop="operationTips">
              <el-input
                v-model="fieldInformationForm.operationTips"
                :placeholder="$t('pleaseEnter')"
                @input="
                  inputChange(
                    fieldInformationForm.operationTips,
                    'operationTips'
                  )
                "
              />
            </el-form-item>
            <el-form-item
              :label="$t('additionalInstructions')"
              prop="extraExplanation"
            >
              <el-input
                v-model="fieldInformationForm.extraExplanation"
                :placeholder="$t('pleaseEnter')"
                type="textarea"
                :rows="4"
                maxlength="2000"
                show-word-limit
                @input="
                  inputChange(
                    fieldInformationForm.extraExplanation,
                    'extraExplanation'
                  )
                "
              />
            </el-form-item>
            <el-form-item
              :label="$t('grouping')"
              class="Grouping"
              prop="filedCodeGroup"
            >
              <el-select
                v-model="fieldInformationForm.filedCodeGroup"
                :placeholder="$t('selectPlaceholder')"
                style="flex: 1"
                filterable
                @change="
                  selectChange(
                    fieldInformationForm.filedCodeGroup,
                    'filedCodeGroup'
                  )
                "
              >
                <el-option
                  v-for="(item, index) in filedCodeGroupList"
                  :key="index"
                  :label="item.name"
                  :value="item.groupId"
                />
              </el-select>
              <el-button
                plain
                style="
                  border-radius: 4px;
                  border: 1px solid #1c50fd;
                  color: #1c50fd;
                  margin-left: 12px;
                "
                @click="openGroupDialog"
                >{{ $t("groupManagement") }}</el-button
              >
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
    <!-- 编辑事项 -->
    <add-scene
      v-if="addSceneDialogVisible"
      ref="AddScene"
      :dialogVisibleApplication="addSceneDialogVisible"
      :dialogType="dialogType"
      :params="dialogForm"
      @updateSceneManagement="updateSceneManagement"
      @closeDialog="closeDialog"
    ></add-scene>
    <!-- 分组管理 -->
    <group-dialog
      ref="groupDialog"
      v-model="groupDialogVisible"
      :list="filedCodeGroupList"
      :groupId="sourceData.id"
      :sceneId="sourceData.sceneId"
      @close="closeGroupDialog"
      @refreshGroupList="refreshGroupList"
    ></group-dialog>
  </div>
</template>

<script>
import {
  apiGetMatterList,
  apiEditMatter,
  apiGetMatterGuideGroupList,
  apiCheckNameCodeExists,
} from "@/api/issueManagement.js";
import {
  apiGetSceneDetail,
  apiGetSceneMatterGroupRefList,
  apiAddSceneMatterGroupRef,
  apiAddSceneManagement,
} from "@/api/scene.js";
import { getInterceptWordHandlingMethodList } from "@/api/toolManager";
import addScene from "./addScene";
import groupDialog from "./components/group-dialog";
// import { IconParkIcon} from "@icon-park/vue";
// import { Home } from "@icon-park/vue";

// 检索类型
const searchTypeList = [
  {
    label: "精确匹配",
    value: "1",
  },
  {
    label: "模糊匹配",
    value: "2",
  },
];
export default {
  components: {
    addScene,
    groupDialog,
    // IconParkIcon
  },
  props: {
    baseForm: {
      type: Object,
      default: () => {},
    },
    sceneId: {
      type: [String, Number],
    },
  },
  data() {
    return {
      searchTypeList,
      filedCodeGroupList: [], // 分组列表
      value: "是",
      sourceData: {}, // 事项详情
      dialogVisible: false,
      groupDialogVisible: false,
      title: "",
      selectFormId: "",
      dialogForm: {
        icon: "", // 图片
        sceneName: "",
        matterDesc: "",
      }, // 新增所需字段
      baseInfo: {
        systemPrompt: "", // 触发提示词
        aliasName: "", // 场景别名
        matterDesc: "", // 场景备注
        processing: "", // 处理方式
      }, // 基础信息
      matterGuideFiledList: [],
      fieldInformationForm: {
        matterId: "", // 事项
        operationTips: "", // 操作提示
        extraExplanation: "", // 额外说明
        requiredFlag: 0, // 是否必要，1-必要，0-不必要
        filedCodeGroup: "", // 分组
        // optionList: [],
      }, // 字段信息表单
      rules: {
        matterId: [
          { required: true, message: "事项不能为空", trigger: "change" },
        ],
        filedCodeGroup: [
          { required: true, message: "分组不能为空", trigger: "change" },
        ],
      },
      key: "",
      addLoading: false,
      // 新
      addSceneDialogVisible: false,
      dialogType: "",
      matterIdList: [], // 事项列表
      newGroupList: [],
      baseAdd: false,
      eventAdd: false,
      processingList: [], // 处理方式
      handledingListData: [],
      handlingFourList: [
        {
          name: this.$t("limitedAnswer"),
        },
        {
          name: this.$t("addPrefix"),
        },
        {
          name: this.$t("addSuffix"),
        },
        {
          name: this.$t("replacementIssues"),
        },
      ],
      placrholdername: {
        "": this.$t("pleaseEnter"),
        限定答案: this.$t("enterALimitedAnswer"),
        添加前缀: this.$t("enterPrefix"),
        添加后缀: this.$t("enterSuffix"),
        替换问题: this.$t("enterTheReplacementContent"),
      },
      handleWordList: {
        answer: this.$t("limitedAnswer"),
        preQuestion: this.$t("addPrefix"),
        extendQuestion: this.$t("addSuffix"),
        replaceQuestion: this.$t("replacementIssues"),
      },
      processList: {
        way: "",
      },
      draggedItem: "",
      tabList: [
        {label: "basicInformation",value: 1,iconName: ""},
        {label: "executionMatters",value: 2,iconName: "array"},
      ],
      activeIndex: 0
    };
  },
  computed: {
    groupList() {
      const groupedArray = this.matterGuideFiledList?.reduce((acc, curr) => {
        const title = this.filedCodeGroupList?.find(
          (i) => i.groupId == curr.filedCodeGroup
        )?.name;
        const foundItem = acc.find((item) => item.title === title);
        // console.log("foundItem", foundItem);
        // console.log("curr", curr);
        if (foundItem) {
          foundItem.list.push(curr);
        } else {
          acc.push({
            title: title,
            filedCodeGroup: curr.filedCodeGroup,
            status: curr.status,
            list: [curr],
          });
        }
        return acc;
      }, []);
      // console.log("groupedArray", groupedArray);
      return groupedArray;
    },
    tenantId() {
        const tenantId = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.tenantId : "";
        return tenantId;
    }
  },
  watch: {
    groupList: {
      handler(n) {
        this.newGroupList = Object.assign([], [...n]);
      },
    },
  },
  async mounted() {
    this.getMatterList();
    this.handlingMethodList();
    // 查场景详情
    await this.getSceneDetail();
    await this.getSceneMatterGroupRefList();
    await this.getMatterGuideGroupList();
  },
  methods: {
    dragStart(index, data) {
      this.draggedItem = index;
    },
    dragOver(index, data) {
      event.preventDefault();
    },
    drop(index, data) {
      // const draggedItem = data[this.draggedItem];
      // this.matterGuideFiledList = this.matterGuideFiledList.filter(i => i.id != draggedItem.id)
      // this.matterGuideFiledList.splice(idx, 0, draggedItem)
      const draggedItem = data[this.draggedItem];
      data.splice(this.draggedItem, 1);
      data.splice(index, 0, draggedItem);
    },
    // 修改model框基本信息
    updateSceneManagement(data) {
      this.addSceneDialogVisible = false;
      for (let key in this.dialogForm) {
        this.dialogForm[key] = data[key];
      }
    },
    // 关闭model框
    closeDialog() {
      this.addSceneDialogVisible = false;
    },
    //
    // 打开分组管理弹窗
    openGroupDialog() {
      this.groupDialogVisible = true;
    },
    // 关闭分组管理弹窗
    closeGroupDialog() {
      this.groupDialogVisible = false;
    },
    // 保存分组刷新分组list
    refreshGroupList() {
      this.groupDialogVisible = false;
      this.getMatterGuideGroupList();
    },
    // 返回列表
    comeBack() {
      this.$emit("comeBack");
    },
    // 详情
    async getSceneDetail(id) {
      //   this.addLoading = true;
      const params = {
        param: this.sceneId || id,
      };
      const res = await apiGetSceneDetail(params);
      if (res.code == "000000") {
        this.sourceData = res.data || {};
        console.log("sourceData", this.sourceData);
        for (let key in this.baseInfo) {
          this.baseInfo[key] = this.sourceData[key];
        }
        for (let key in this.dialogForm) {
          this.dialogForm[key] = this.sourceData[key];
        }
        // 收集表单分组
        this.matterGuideFiledList = this.sourceData?.matterGuideFiledList || [];
        if (this.matterGuideFiledList?.length) {
          this.selectFormId = this.matterGuideFiledList[0].id;
        }
        if (this.baseInfo.processing) {
          let process = JSON.parse(this.baseInfo.processing);
          this.processList.way = process && process.way;
          this.handledingListData = [];
          for (var key in process) {
            if (key != "way") {
              this.handledingListData.push({
                name: this.handleWordList[key],
                content: process[key],
              });
            }
          }
        }
      }
      //   this.addLoading = false;
    },
    // 执行事项分组
    async getSceneMatterGroupRefList() {
      const params = {
        sceneId: this.sceneId,
      };
      const res = await apiGetSceneMatterGroupRefList(params);
      if (res.code == "000000") {
        this.matterGuideFiledList = res.data || [];
        if (this.matterGuideFiledList?.length) {
          this.selectFormId = this.matterGuideFiledList[0].id;
        }
        this.matterGuideFiledList.forEach((item) => {
          item.filedCodeGroup = item.groupId;
        });
        if (this.matterGuideFiledList?.length) {
          for (let key in this.fieldInformationForm) {
            this.fieldInformationForm[key] = this.matterGuideFiledList[0][key];
            this.fieldInformationForm.filedCodeGroup =
              this.matterGuideFiledList[0]?.groupId;
          }
        }
      }
    },
    // 事项列表
    async getMatterList() {
      let params = {
        pageNo: 1,
        pageSize: 999,
        showFlag: true
      };
      const res = await apiGetMatterList(params);
      if (res.code == "000000") {
        this.matterIdList = res.data?.records || [];
      }
    },
    // 根据matterId转事项名称
    matterNameOperate(id) {
      if (!this.matterIdList.length) return "";
      const name = this.matterIdList.find((i) => i.matterId == id)?.matterName;
      return name;
    },
    // 新增(执行事项-事项配置)
    async addSceneMatterGroupRef() {
      // const newGroupMatterList = this.matterGuideFiledList.filter((item) => {
      //   if ("newId" in item) {
      //     delete item.id;
      //     delete item.newId;
      //   }
      //   return true;
      // });
      // const groupMatterList = newGroupMatterList.reduce((acc, item) => {
      //   let found = acc.find(
      //     (element) => element.groupId === item.filedCodeGroup
      //   );
      //   item.sceneId = this.sceneId;
      //   item.groupId = item.filedCodeGroup;
      //   if (found) {
      //     found.matterList.push(item);
      //   } else {
      //     acc.push({ groupId: item.filedCodeGroup, matterList: [item] });
      //   }
      //   return acc;
      // }, []);
      // console.log("groupMatterList", groupMatterList);
      // groupMatterList.forEach((element) => {
      //   if (element.matterList && element.matterList.length) {
      //     element.matterList.forEach((ele, index) => {
      //       ele.sorted = index + 1;
      //     });
      //   }
      // });
      this.newGroupList.forEach((element) => {
        if (element.list && element.list.length) {
          element.list.forEach((ele, index) => {
            ele.sorted = index + 1;
          });
        }
      });
      const groupMatterList = this.newGroupList.map((item) => {
        return {
          groupId: item.filedCodeGroup,
          matterList: item.list,
        };
      });
      const params = {
        sceneId: this.sceneId,
        groupMatterList,
        tenantId: this.tenantId
      };
      const res = await apiAddSceneMatterGroupRef(params);
      if (res.code == "000000") {
        this.eventAdd = true;
      } else {
        this.$message.warning(res.msg);
      }
    },
    // 保存
    async save() {
      await this.addSceneManagement();
      await this.addSceneMatterGroupRef();
      if (this.eventAdd && this.baseAdd) {
        this.$emit("refreshList");
      }

      // const matterGuideFiledList = this.matterGuideFiledList.filter((item) => {
      //   if ("newId" in item) {
      //     delete item.id;
      //     delete item.newId;
      //   }
      //   return true;
      // });
      // this.matterGuideFiledList.forEach((ele) => {
      //   if (["select", "radio", "checkbox"].includes(ele.formType)) {
      //     ele.optionList = ele.optionList?.filter((i) => i.lable);
      //     ele.optionList?.forEach((k) => {
      //       k.value = k.lable;
      //     });
      //   }
      //   for (let key in ele) {
      //     if (
      //       [
      //         "requiredFlag",
      //         "uniqueFlag",
      //         "tableShowFlag",
      //         "exportFlag",
      //         "formShowFlag",
      //         "searchFlag",
      //       ].includes(key)
      //     ) {
      //       ele[key] = ["是", "true", true].includes(ele[key]) ? "是" : "否";
      //     }
      //   }
      // });

      // const params = {
      //   ...this.baseInfo,
      //   id: this.sourceData.id,
      //   showFlag: this.sourceData.showFlag,
      //   matterGuideFiledList,
      // };
      // this.addLoading = true;
      // const res = await apiEditMatter(params);
      // if (res.code == "000000") {
      //   console.log("res", res);
      //   this.$message.success(this.$t("success"));
      //   this.$emit("refreshList");
      // } else {
      //   this.$message.warning(res.msg);
      // }
      // this.addLoading = false;
    },
    // 更新(新增)场景-基本信息
    async addSceneManagement() {
      this.handledingListData.forEach((item) => {
        if (item.name == this.$t("limitedAnswer")) {
          this.processList.answer = item.content;
        }
        if (item.name == this.$t("addPrefix")) {
          this.processList.preQuestion = item.content;
        }
        if (item.name == this.$t("addSuffix")) {
          this.processList.extendQuestion = item.content;
        }
        if (item.name == this.$t("replacementIssues")) {
          this.processList.replaceQuestion = item.content;
        }
      });
      let params = {
        sceneId: this.sceneId,
        status: this.sourceData.status,
        ...this.dialogForm,
        ...this.baseInfo,
        processing: JSON.stringify(this.processList),
        tenantId: this.tenantId
      };
      if (this.sourceData?.id) {
        params.id = this.sourceData.id;
      }
      const res = await apiAddSceneManagement(params);
      if (res.code == "000000") {
        this.baseAdd = true;
      } else {
        this.$message.warning(res.msg);
      }
    },
    // 修改新增基本信息
    updateScene() {
      this.addSceneDialogVisible = true;
      this.dialogType = "edit";
    },
    // 分组列表
    async getMatterGuideGroupList() {
      const res = await apiGetMatterGuideGroupList({
        sceneId: this.sceneId,
      });
      if (res.code == "000000") {
        this.filedCodeGroupList = res.data || [];
      }
    },
    // 收集表单点击
    formClick(data) {
      console.log("newId", data);
      this.key = data?.id ? "id" : "newId";
      this.selectFormId = data[this.key];

      const sourceData = this.matterGuideFiledList.find(
        (item) => item[this.key] == data[this.key]
      );
      console.log("sourceData", sourceData);
      console.log("data", data);
      for (let key in this.fieldInformationForm) {
        this.fieldInformationForm[key] = sourceData[key];
        this.fieldInformationForm.filedCodeGroup =
          sourceData?.groupId || sourceData?.filedCodeGroup;
        if (
          [
            "requiredFlag",
            "uniqueFlag",
            "tableShowFlag",
            "exportFlag",
            "formShowFlag",
            "searchFlag",
          ].includes(key)
        ) {
          this.fieldInformationForm[key] = [
            "是",
            true,
            "true",
            1,
            "1",
          ].includes(sourceData[key])
            ? true
            : false;
        }
      }
      console.log("fieldInformationForm", this.fieldInformationForm);
    },
    // 收集表单 - 添加
    addItem() {
      if (this.matterGuideFiledList?.length) {
        this.$refs.overallForm.validate((valid) => {
          if (valid) {
            this.addItemHandler();
          }
        });
      } else {
        this.addItemHandler();
      }
    },
    addItemHandler() {
      this.fieldInformationForm = {
        matterId: "",
        operationTips: "",
        extraExplanation: "",
        requiredFlag: 0,
        filedCodeGroup: "",
        status: "1",
        // optionList: [],
      };
      const sourceData = Object.assign(
        {},
        {
          newId: new Date().getTime(),
          id: new Date().getTime(),
          ...this.fieldInformationForm,
        }
      );
      this.matterGuideFiledList.unshift(sourceData);
      this.selectFormId = sourceData.newId;
    },
    // 表单配置 - 下拉框变动
    selectChange(v, fields) {
      console.log("v------", v);
      console.log("fields------", fields);
      if (!this.matterGuideFiledList.length) return;
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData[fields] = v;
      // if (v == "select") {
      //   sourceData.optionList = [];
      // }
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
      console.log("sourceData----------", sourceData);
      console.log("matterGuideFiledList----------", this.matterGuideFiledList);
    },
    // filedCodeBlur(v) {
    //   if (!v) return;
    //   this.checkNameCodeExists(v);
    // },
    // 表单配置 - input框变动
    inputChange(v, fields) {
      console.log("v------", v);
      console.log("fields------", fields);
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData[fields] = v;
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
      console.log("sourceData----------", sourceData);
      console.log("matterGuideFiledList----------", this.matterGuideFiledList);
    },
    // 表单配置 - checkbox变动
    checkboxChange(v, fields) {
      console.log("v------", v);
      console.log("fields------", fields);
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData[fields] = v;
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
      console.log("sourceData----------", sourceData);
      console.log("matterGuideFiledList----------", this.matterGuideFiledList);
    },
    // 表单配置 - radio变动
    radioChange(v, fields) {
      console.log("v------", v);
      console.log("fields------", fields);
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      sourceData[fields] = v;
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
      console.log("sourceData----------", sourceData);
      console.log("matterGuideFiledList----------", this.matterGuideFiledList);
    },
    // 添加选项
    addOption() {
      // let obj = {
      //   lable: "",
      //   value: "",
      //   idx: new Date().getTime(),
      // };
      // this.fieldInformationForm.optionList.push(obj);
    },
    // 删除分组
    deleteGroup() {
      console.log("this.gourp", this.selectFormId);
      this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
        confirmButtonText: this.$t("confirm"),
        cancelButtonText: this.$t("cancel"),
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(() => {
        this.matterGuideFiledList = this.matterGuideFiledList.filter(
          (i) => i.id != this.selectFormId
        );
        if (this.matterGuideFiledList?.length) {
          this.selectFormId = this.matterGuideFiledList[0].id;
          // for (let key in this.fieldInformationForm) {
          //   this.fieldInformationForm[key] = this.matterGuideFiledList[0][key];
          //   if (
          //     [
          //       "requiredFlag",
          //       "uniqueFlag",
          //       "tableShowFlag",
          //       "exportFlag",
          //       "formShowFlag",
          //       "searchFlag",
          //     ].includes(key)
          //   ) {
          //     this.fieldInformationForm[key] = [
          //       "是",
          //       true,
          //       "true",
          //       1,
          //       "1",
          //     ].includes(this.matterGuideFiledList[0][key])
          //       ? true
          //       : false;
          //   }
          // }
        } else {
          this.selectFormId = "";
          for (let key in this.fieldInformationForm) {
            this.fieldInformationForm[key] = "";
          }
        }
      });
    },
    optionItemInput(data, lable) {
      data.value = lable;
      console.log("data-----------", data);
      // console.log(
      //   "this.fieldInformationForm.optionList",
      //   this.fieldInformationForm.optionList
      // );
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      // sourceData.optionList = this.fieldInformationForm.optionList;
      console.log("sourceData---3333", sourceData);
      //   console.log("sourceIdx", sourceIdx);
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
      //   console.log("matterGuideFiledList", this.matterGuideFiledList);
    },
    // 删除选项
    deleteOption(idx) {
      let sourceData = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      // sourceData.optionList = sourceData.optionList.filter(
      //   (ele, index) => index != idx
      // );
      // this.fieldInformationForm.optionList = sourceData.optionList;
      const sourceIdx = this.matterGuideFiledList.find(
        (i) => i.id == this.selectFormId
      );
      console.log("sourceData----删除选项", sourceData);
      console.log("sourceIdx", sourceIdx);
      this.matterGuideFiledList.splice(sourceIdx, sourceData);
      console.log("matterGuideFiledList", this.matterGuideFiledList);
    },
    async handlingMethodList() {
      let res = await getInterceptWordHandlingMethodList();
      if (res.code == "000000") {
        this.processingList = res.data || [];
      }
    },
    addNewHandleing() {
      this.handledingListData.push({
        name: "",
      });
    },
    handleSelect() {
      this.setDisable();
    },
    setDisable() {
      this.handlingFourList.forEach((item) => {
        let isdisable = this.handledingListData.findIndex(
          (val) => val.name == item.name
        );
        if (isdisable != -1) {
          item.disabled = true;
        } else {
          item.disabled = false;
        }
      });
    },
    deleteClick(name, index) {
      this.handledingListData.splice(index, 1);
      this.handlingFourList.forEach((item) => {
        if (item.name == name) {
          item.disabled = false;
        }
      });
    },
    switchChange(data) {
      console.log("switchChange----data", data);
      console.log("matterGuideFiledList", this.matterGuideFiledList);
      this.matterGuideFiledList.forEach((item) => {
        if (item.filedCodeGroup == data.filedCodeGroup) {
          item.status = data.status;
        }
      });
    },
    // 字段名判重
    // async checkNameCodeExists(filedCode) {
    //   const params = {
    //     matterId: this.sourceData.matterId,
    //     filedCode,
    //   };
    //   const res = await apiCheckNameCodeExists(params);
    //   if (res.code != "000000") {
    //     this.$message.warning(this.$t("duplicateFieldNamesPleaseModify"));
    //   }
    // },
  },
};
</script>

<style lang="scss" scoped>
.mattertype-details {
  width: 100%;
  height: 100%;
  &-head {
    height: 96px;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 32px;
    .left {
      display: flex;
      align-items: center;
      .back-icon {
        width: 20px;
        margin-right: 16px;
      }
      iconpark-icon {
        cursor: pointer;
      }
      &-top {
        display: flex;
        align-items: center;
        height: 26px;
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 20px;
        color: #383d47;
        line-height: 26px;
        iconpark-icon {
          margin-left: 12px;
        }
      }
      &-desc {
        margin-top: 4px;
        height: 18px;
        font-family: MiSans, MiSans;
        font-size: 14px;
        color: #828894;
        line-height: 18px;
      }
    }
    .center {
        width: 236px;
        height: 40px;
        background: #F7F8FA;
        border-radius: 4px;
        display: flex;
        justify-content: space-around;
        .default {
            color:#828894;
            width: 50%;
            height: 40px;
            line-height: 40px;
            border-radius: 2px;
            display: inline-block;
            text-align: center;
            cursor: pointer;
        }
        .active {
            cursor: pointer;
            text-align: center;
            width: 50%;
            display: inline-block;
            color:#494E57;
            line-height: 40px;
            background: #FFFFFF;
            box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
            border-radius: 2px;
        }
    }
    .right {
      display: flex;
      align-items: center;

      .btns{
        width: 92px; 
        height: 40px;
        border-radius: 2px; 
        margin-left: 24px;
        display:flex;
        align-items: center;

        img{
          width: 16px;
          height: 16px;
          line-height: 16px;
          vertical-align: middle;
          margin-right: 8px;
        }

        span{
          display: inline-block;
          height: 16px;
          line-height: 16px;
          vertical-align: middle;
        }
      }
    }
  }
  &-content {
    width: 100%;
    height: calc(100% - 96px);
    display: flex;
    background: #fff;
    border-top: 1px solid #e1e4eb;
    .base-info {
      width: 520px;
      height: 100%;
      border-right: 1px solid rgba(0, 0, 0, 0.12);
      padding: 24px 32px;

      &-title {
        display: flex;
        align-items: center;
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 20px;
        color: #383d47;
        img {
          width: 24px;
          height: 24px;
          margin-right: 8px;
        }
      }
      &-form {
        margin-top: 24px;
        height: calc(100% - 24px - 24px);
        width: 100%;
        overflow: auto;
        &::-webkit-scrollbar {
          display: none;
        }
        .form-column {
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          ::v-deep .el-form-item__content {
            width: 100%;
          }
          ::v-deep .el-button {
            width: 100%;
            display: flex;
            align-items: center;
            border-radius: 4px;
            border: 1px solid #1c50fd;
            font-size: 16px;
            color: #1c50fd;
            span {
              display: flex;
              align-items: center;
              justify-content: center;
              width: 100%;
              iconpark-icon {
                margin-right: 6px;
              }
            }
          }
          .handle-item {
            display: flex;
            align-items: center;
            margin: 12px 0;
            img {
              margin-left: 12px;
            }
            .input {
              flex: 1;
            }
          }
          .addbtn {
            display: flex;
            align-items: center;
            .add-name {
              font-size: 16px;
              color: #828894;
            }
          }
        }
      }
    }
    .collect-form {
      width: 520px;
      height: 100%;
      border-right: 1px solid rgba(0, 0, 0, 0.12);
      padding: 24px 32px;
      &-head {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 16px;
        &-title {
          display: flex;
          align-items: center;
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 20px;
          color: #383d47;
          iconpark-icon {
            margin-right: 8px;
          }
        }
      }
      .fields {
        width: 100%;
        height: calc(100% - 56px);
        overflow: auto;
        &::-webkit-scrollbar {
          display: none;
        }
        &-group {
          width: 100%;
          height: 40px;
          margin-bottom: 16px;
          display: flex;
          align-items: center;
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 18px;
          color: #383d47;
          &::before {
            content: "";
            display: inline-block;
            width: 3px;
            height: 18px;
            background: #1c50fd;
            margin-right: 8px;
          }
        }
        &-item {
          width: 100%;
          height: 76px;
          padding-right: 16px;
          display: flex;
          align-items: center;
          background: #ffffff;
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          margin-bottom: 16px;
          cursor: pointer;
          position: relative;
          .scene-triangle {
            height: 16px;
            position: absolute;
            top: -17px;
            left: 50%;
            margin-left: -47px;
          }
          .left {
            width: 32px;
            height: 100%;
            border-right: 1px solid #e1e4eb;
            background: #f2f5fa;
            border-radius: 7px 0px 0px 7px;
            display: flex;
            align-items: center;
            justify-content: center;
          }
          .center {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: center;
            height: 100%;
            position: relative;
            padding: 16px;
            overflow: hidden;
            .fields-name {
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 16px;
              color: #383d47;
              line-height: 20px;
              margin-bottom: 4px;
            }
            .fields-tips {
              width: 32px;
              height: 20px;
              line-height: 20px;
              text-align: center;
              background: #ebeef2;
              border-radius: 4px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 12px;
              color: #828894;
              line-height: 16px;
            }
          }
          .right {
            margin-left: auto;
            img {
              width: 44px;
              height: 44px;
            }
          }
        }
        .row-border {
          border: 1px solid #1c50fd;
        }
      }
      ::v-deep .el-radio {
        width: auto !important;
      }
      ::v-deep .el-checkbox {
        width: auto !important;
      }
    }
    .form-config {
      flex: 1;
      height: 100%;
      padding: 24px 32px;
      .add-option {
        z-index: 99;
        max-width: 108px;
        display: flex;
        align-items: center;
        margin-bottom: 16px;
        height: 28px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #1c50fd;
        line-height: 28px;
        cursor: pointer;
        iconpark-icon {
          margin-right: 4px;
        }
      }
      .option-input {
        position: relative;
        margin-bottom: 8px;
      }
      .option-clear {
        position: absolute;
        right: 14px;
        top: 14px;
        cursor: pointer;
        color: #828894;
      }
      .Grouping {
        ::v-deep .el-form-item__content {
          display: flex;
        }
      }
      &-title {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 28px;
        .txt {
          display: flex;
          align-items: center;
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 18px;
          color: #383d47;
          line-height: 28px;
          &::before {
            content: "";
            display: block;
            width: 3px;
            height: 18px;
            background: #1c50fd;
            margin-right: 8px;
          }
        }

        .delete-group {
          font-weight: 600;
          font-size: 16px;
          color: #d82225;
          cursor: pointer;
        }
      }
      &-content {
        margin-top: 16px;
        width: 100%;
        height: calc(100% - 88px);
        overflow: auto;
        &::-webkit-scrollbar {
          display: none;
        }
        .configForm {
          .el-form-item {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            ::v-deep .el-form-item__content {
              width: 100%;
            }
            ::v-deep .el-select {
              width: 100%;
            }
          }
        }
      }
      ::v-deep .el-input-number .el-input__inner {
        text-align: left;
      }
      ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner,
      .el-checkbox__input.is-indeterminate .el-checkbox__inner {
        background: #1c50fd;
        border-color: #1c50fd;
      }
      ::v-deep .el-checkbox__label {
        color: #1c50fd;
      }
      ::v-deep .el-radio__inner::after {
        width: 6px;
        height: 6px;
      }
    }
  }
  ::v-deep .el-textarea__inner {
    color: #606266;
    font-family: MiSans, MiSans;
    font-size: 14px;
    border-radius: 4px;
  }
  ::v-deep .el-switch__core {
    width: 32px !important;
    height: 20px;
    border-radius: 12px;
    background: #ced4e0;
    border: none;
    margin-right: 4px;
    &::after {
      width: 14px;
      height: 14px;
      top: 3px !important;
    }
  }
  ::v-deep .el-switch.is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
    &::after {
      width: 14px;
      height: 14px;
      top: 3px !important;
    }
  }
  ::v-deep .el-form-item__label {
    height: 24px;
    font-family: MiSans, MiSans;
    font-size: 16px;
    color: #383d47;
    line-height: 24px;
    margin-bottom: 8px;
  }
}
</style>