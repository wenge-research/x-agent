<template>
  <!-- 分组管理 -->
  <el-dialog
    :title="$t('groupManagement')"
    :visible.sync="dialogVisible"
    top="3%"
    width="480px"
    height="640px"
    :modal-append-to-body="false"
    append-to-body
    custom-class="matter-dialog"
    :before-close="closeDialog"
    :close-on-click-modal="false"
        :close-on-press-escape="false"
    @opened="openDialog"
  >
    <div class="dialog-group">
      <ul class="list">
        <li
          class="list-item"
          v-for="(item, index) in groupList"
          :key="item.id"
          :id="`gourpItem${index}`"
        >
          <iconpark-icon
            :class="[item.focus ? 'focus-true' : 'focus-false']"
            name="draggable"
            size="16"
            color="#848587"
          ></iconpark-icon>
          <el-input
            v-if="item.focus"
            v-model="item.name"
            :placeholder="$t('pleaseEnter')"
            @focus="focusChange(item, true)"
            @blur="focusChange(item, false)"
            clearable
          />
          <div v-if="!item.focus" class="row" @click="focusChange(item, true)">
            <div class="row-name">{{ item.name }}</div>
            <iconpark-icon
              name="close-large-fill"
              size="12"
              color="#1D2129"
              @click.stop="deleteGroup(item)"
            ></iconpark-icon>
          </div>
        </li>
      </ul>
      <div class="add-group" @click="addGroup">
        <iconpark-icon
          name="add-line"
          color="#1C50FD"
          size="14"
        ></iconpark-icon>
        {{ $t("addGroup") }}
      </div>
    </div>
    <div class="dialog-footer">
      <el-button type="primary" @click="onSubmit" :loading="addLoading">{{
        $t("confirm")
      }}</el-button>
      <el-button plain @click="closeDialog" :loading="addLoading">{{
        $t("cancel")
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import {
  apiGetMatterGuideGroupList,
  apiDelMatterGuideGroupById,
  apiAddOrUpdateMatterGroupList,
} from "@/api/issueManagement.js";
export default {
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    groupId: {
      type: [String, Number],
    },
    sceneId: {
      type: [String, Number],
    },
    list: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      dialogVisible: false,
      addLoading: false,
      groupList: [],
    };
  },
  watch: {
    value(n) {
      this.dialogVisible = n;
    },
    list(n) {
      this.groupList = Object.assign([], [...n]);
      this.groupList.forEach((item) => {
        item.focus = false;
      });
    },
  },
  methods: {
    openDialog() {},
    closeDialog() {
      this.$emit("close");
    },
    onSubmit() {
      this.addOrUpdateMatterGroupList();
    },
    focusChange(data, istrue) {
      data.focus = istrue;
      const idx = this.groupList.findIndex((item) => item.id == data.id);
      this.groupList.splice(idx, data);
    },
    // 删除分组
    deleteGroup(data) {
      this.$confirm("请确认是否删除", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => {
        try {
          const res = await apiDelMatterGuideGroupById(data.id);
          if (res.code == "000000") {
            this.$message.success(this.$t("deleteSuccess"));
            this.getMatterGuideGroupList();
          } else {
            this.$message.warning(res.msg);
          }
        } catch (error) {}
      });
    },
    // 分组列表
    async getMatterGuideGroupList() {
      const res = await apiGetMatterGuideGroupList({ sceneId: this.sceneId });
      if (res.code == "000000") {
        this.groupList = res.data || [];
      }
    },
    // 新增/编辑分组
    async addOrUpdateMatterGroupList() {
      this.addLoading = true;
      const list = this.groupList.filter((item) => item.name != "");
      list.forEach((i) => {
        i.sceneId = this.sceneId;
      });
      const res = await apiAddOrUpdateMatterGroupList(list);
      if (res.code == "000000") {
        this.$message.success(this.$t("success"));
        this.dialogVisible = false;
        this.$emit("refreshGroupList");
      }
      this.addLoading = false;
    },
    addGroup() {
      const groupItem = {
        name: "",
        // groupId: this.groupId,
        focus: true,
      };
      this.groupList.push(groupItem);
      if(!this.groupList.length) return;
      this.$nextTick(() => {
        const idx = this.groupList.length - 1;
        const latestDataElement = document.getElementById(`gourpItem${idx}`);
        latestDataElement.scrollIntoView({
          behavior: "smooth",
          block: "start",
        });
        console.log("this.groupList>>>>>>>>>>>", this.groupList);
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.dialog-group {
  .list {
    max-height: 480px;
    width: 100%;
    overflow: auto;
    &::-webkit-scrollbar {
      display: none;
    }
    &-item {
      margin-bottom: 8px;
      position: relative;
      display: flex;
      ::v-deep .el-input__inner {
        padding-left: 36px;
        font-size: 16px;
      }
      .focus-true {
        position: absolute;
        top: 12px;
        left: 16px;
        z-index: 99;
      }
      .focus-false {
        margin-left: 15px;
      }
      .row {
        flex: 1;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        &-name {
          padding-left: 6px;
          height: 40px;
          line-height: 40px;
          font-family: MiSans, MiSans;
          font-size: 16px;
          color: #494c4f;
        }
        iconpark-icon {
          margin-right: 12px;
        }
      }
      iconpark-icon {
        cursor: pointer;
      }
    }
  }
  .add-group {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    height: 28px;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #1c50fd;
    line-height: 28px;
    cursor: pointer;
    iconpark-icon {
      margin-right: 4px;
    }
  }
}
</style>