<template>
  <el-dialog
    :title="$t('executeSequentialSteps')"
    :visible.sync="dialogVisible"
    width="480px"
    class="orderStep"
    :before-close="cancelConfig"
    append-to-body
  >
    <div class="flex-center just order-step-content">
      <div class="check-box">
        <span class="title">{{ $t("optionalSteps") }}</span>
        <div class="marginTop20">
          <el-checkbox-group v-model="checkList">
            <div
              v-for="item in orderStepList"
              :key="item.lable"
              style="margin-bottom: 16px"
            >
              <el-checkbox :label="item.lable" style="font-size: 16px">{{
                item.name
              }}</el-checkbox>
            </div>
          </el-checkbox-group>
        </div>
      </div>
      <div class="check-box">
        <span class="title">{{ $t("stepsEnabled") }}</span>
        <div class="marginTop20">
          <div
            class="item flex-center just"
            v-for="(item, index) in checkList"
            :key="item"
            draggable="true"
            @dragstart="dragStart(index)"
            @dragover="dragOver(index)"
            @drop="drop(index)"
          >
            <div class="flex-center">
              <img src="@/assets/images/appManagement/dragDrop.svg" class="drop-icon"  />
              <span class="item-name">{{ filterCheck(item) }}</span>
            </div>
            <i
              class="el-icon-close"
              style="cursor: pointer"
              @click="delCheck(item)"
            ></i>
          </div>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancelConfig">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="setOrderStep">{{
        $t("confirm")
      }}</el-button>
    </span>
  </el-dialog>
</template>
        
<script>
export default {
  data() {
    return {
      checkList: [],
      orderStepList: [
        { lable: "builtIn", name: "内置问题" },
        { lable: "subjectTalk", name: "讨论话题" },
        { lable: "findQaContent", name: "检索QA【答案】" },
        { lable: "findAnswerByModel", name: "大模型发散" },
        { lable: "interceptSensitive", name: "安全拦截" },
        { lable: "findQaTitle", name: "检索QA【问题】" },
        { lable: "finalCollectStrategy", name: "检索知识库" },
        // { lable: "yayiKnowldegeStrategy", name: "雅意知识库" },
        // { lable: "businessScenario", name: "业务场景" },
        // // { lable: "wenshuContent", name: "问数检索" },
      ],
      draggedItem: "",
    };
  },
  props: {
    dialogVisible: Boolean,
    params: Array,
  },
  mounted() {
    this.configParams();
  },
  methods: {
    // 回显
    configParams() {
      if (this.params.length > 0) {
        this.checkList = this.params;
      } else {
        this.checkList = [];
      }
    },
    filterCheck(item) {
      let findItem = this.orderStepList.find((items) => items.lable == item);
      return findItem?.name;
    },
    delCheck(item) {
      let filterArr = this.checkList.filter((items) => items != item);
      this.checkList = filterArr;
    },
    setOrderStep() {
      this.$emit("clickConfigParams", "orderStepVisible", this.checkList);
    },
    cancelConfig() {
      this.$emit("clickConfig", false);
    },
    dragStart(index) {
      this.draggedItem = index;
    },
    dragOver(index) {
      event.preventDefault();
    },
    drop(index) {
      const draggedItem = this.checkList[this.draggedItem];
      this.checkList.splice(this.draggedItem, 1);
      this.checkList.splice(index, 0, draggedItem);
    },
  },
};
</script>
        
<style lang="scss" scoped>
.orderStep {
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__body {
      padding: 0px 32px 16px;
    }
    .el-dialog__header {
      background-color: #ffffff;
      padding: 24px;
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

.check-box {
  flex: 1;
  height: 100%;
  max-width: calc(50% - 0px);
  box-sizing: border-box;
  overflow-y: auto;
  padding: 12px;
  .title {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 16px;
    color: #383d47;
    line-height: 20px;
    text-align: left;
    font-style: normal;
  }
  .item {
    width: 176px;
    height: 32px;
    background: #ffffff;
    border-radius: 2px;
    align-items: center;
    padding: 0 8px;
    margin-bottom: 8px;
    border: 1px solid #D5D8DE;
    cursor: move;

    .item-name {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #494c4f;
      line-height: 20px;
      text-align: left;
      font-style: normal;
    }
  }
  .drop-icon {
    width: 16px;
    height: 16px;
    margin-right: 5px;
  }
  &:nth-child(2) {
    border-left: 1px solid #d5d8de;
    background: #F2F4F7;
  }
}
::v-deep .el-checkbox__label {
  font-size: 16px;
}
::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #1c50fd;
  border-color: #1c50fd;
}
::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #383d47;
}
.marginTop20 {
  margin-top: 20px;
}

.flex-center {
  display: flex;
  align-items: center;
}

.just {
  justify-content: space-between;
}
.order-step-content {
  height: 384px;
  background: #FFFFFF;
  border-radius: 2px;
  border: 1px solid #D5D8DE;
}
</style>
        