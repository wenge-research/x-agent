<template>
  <div class="issue-management">
    <template v-if="type == 'list'">
      <div class="issue-management-head">
        <ul class="issue-management-head-tabs">
          <li
            :class="[tabs == 1 ? 'tabs-selected' : '']"
            @click="tabsHandler(1)"
          >
            {{ $t("disposalOfMatters") }}
          </li>
          <li
            :class="[tabs == 2 ? 'tabs-selected' : '']"
            @click="tabsHandler(2)"
          >
            {{ $t("matterType") }}
          </li>
        </ul>
        <!-- <el-button
          v-if="tabs == 1"
          plain
          style="
            border-radius: 4px;
            border: 1px solid #1c50fd;
            color: #1c50fd;
            background: transparent;
          "
          ><iconpark-icon
            name="edit-line"
            color="#1c50fd"
            size="14"
          ></iconpark-icon>
          编辑</el-button
        > -->
      </div>

      <div class="issue-management-content">
        <MatterDisposal v-if="tabs == 1" ref="MatterDisposal" />
        <MatterType
          v-if="tabs == 2"
          ref="MatterType"
          :form="addFormInfo"
          @comeToDetailPage="comeToDetailPage"
        />
      </div>
    </template>
    <!-- 事项类型编辑 -->
    <MatterTypeDetails
      v-else
      ref="MatterTypeDetails"
      :matterTypeId="matterTypeId"
      @comeBack="comeBackList"
      @refreshList="refreshList"
      @openMatterTypeDialog="openMatterTypeDialog"
    />
  </div>
</template>

<script>
// components
import MatterType from "./matterType";
import MatterDisposal from "./matterDisposal";
import MatterTypeDetails from "./matterTypeDetails";
export default {
  components: {
    MatterType,
    MatterDisposal,
    MatterTypeDetails,
  },
  data() {
    return {
      tabs: 1, // 1-事项处置 2-事项类型
      type: "list", // list-展示列表 detail-展示详情
      matterTypeId: "", // 事项类型详情所需id参数
      addFormInfo: {}, // 创建事项弹框所需值
    };
  },
  methods: {
    tabsHandler(tabs) {
      this.tabs = tabs;
    },
    comeToDetailPage(id) {
      this.matterTypeId = id;
      this.type = "detail";
    },
    // 事项类型详情返回
    comeBackList() {
      this.type = "list";
      this.tabs = 2;
    },
    // 事项类型详情保存成功 - 刷新列表
    refreshList() {
      this.type = "list";
      this.tabs = 2;
    },
    // 打开事项类型新增弹框
    openMatterTypeDialog(form) {
      this.$refs.MatterType.openDialogEdit(form);
    },
  },
};
</script>

<style lang="scss" scoped>
.issue-management {
  width: 100%;
  height: 100%;
  padding: 0 12px 4px;
  &-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    &-tabs {
      height: 80px;
      display: flex;
      align-items: center;
      li {
        height: 28px;
        font-family: MiSans, MiSans;
        font-size: 22px;
        color: #828894;
        line-height: 28px;
        cursor: pointer;
        &:nth-child(1) {
          margin-right: 16px;
        }
      }
      .tabs-selected {
        font-weight: 600;
        color: #383d47;
      }
    }
  }
  &-content {
    height: calc(100% - 80px);
    width: 100%;
    background: #fff;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    padding: 20px 24px 24px;
  }
}
</style>