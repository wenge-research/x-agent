<template>
  <el-dialog
    :title="$t('executeSequentialSteps')"
    :visible.sync="dialogVisible"
    width="560px"
    class="orderStep"
    :before-close="cancelConfig"
    append-to-body
  >
    <div class="flex-center just">
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
      <el-button type="primary" @click="setOrderStep">{{
        $t("confirm")
      }}</el-button>
      <el-button @click="cancelConfig">{{ $t("cancel") }}</el-button>
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
        { lable: "yayiKnowldegeStrategy", name: "雅意知识库" },
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
      text-align: left;
    }
  }
}

.check-box {
  flex: 1;
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
    width: 236px;
    height: 40px;
    background: #f2f5fa;
    border-radius: 4px;
    align-items: center;
    padding: 0 10px;
    margin-bottom: 8px;
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
</style>
        