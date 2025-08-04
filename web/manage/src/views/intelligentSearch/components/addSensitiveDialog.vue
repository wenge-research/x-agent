<template>
  <el-dialog
    :title="$t('selectSensitiveWordLibrary')"
    :visible.sync="dialogVisible"
    width="680px"
    class="knowledgeDialog"
    :before-close="cancelAss"
    append-to-body
  >
    <div class="flex-center just" style="margin-bottom: 16px">
      <el-input
        :placeholder="$t('searchTermLibraryName')"
        prefix-icon="el-icon-search"
        v-model="searchKeyWord"
        style="width: 334px"
        @input="knowledgeList"
      >
      </el-input>
      <el-checkbox v-model="showAdd" @change="showChange">{{ $t("onlyAdd") }}</el-checkbox>
    </div>
    <div style="max-height: 500px; overflow-y: auto" v-loading="loading">
      <ul v-if="!showAdd">
        <li class="base-li flex-center just" v-for="item in konwlwdgeList" :key="item.name + item.id">
          <div class="li-name flex-center">
            <img src="@/assets/images/appManagement/zsk.svg" />
            <span>{{ item.name }}</span>
          </div>
          <el-button
            v-if="item.applicationId"
            type="text"
            icon="el-icon-delete"
            style="color: #d82225"
            @click="detItem(item)"
            >{{ $t("remove") }}</el-button
          >
          <el-button
            v-else
            type="text"
            icon="el-icon-plus"
            style="color: #1c50fd"
            @click="addItem(item)"
            >{{ $t("add") }}</el-button
          >
        </li>
      </ul>
      <ul v-if="showAdd">
        <li class="base-li flex-center just" v-for="(item, index) in konwlwdgeAllList" :key="index">
          <div class="li-name flex-center">
            <img src="@/assets/images/appManagement/zsk.svg" />
            <span>{{ item.name }}</span>
          </div>
          <el-button
            type="text"
            icon="el-icon-delete"
            style="color: #d82225"
            @click="detSelectItem(item)"
            >{{ $t("remove") }}</el-button
          >
        </li>
      </ul>
    </div>
    <el-pagination
      style="margin-top: 20px; color: #272a31; text-align: right"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNo"
      :page-sizes="[10, 30, 50, 100, 200]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      background
      v-if="!showAdd"
    >
    </el-pagination>
    <!-- <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="setKnowledgeIds">{{
        $t("confirm")
      }}</el-button>
      <el-button @click="cancelAss">{{ $t("cancel") }}</el-button>
    </span> -->
  </el-dialog>
</template>

<script>
import {
  getInterceptWordHouseListAll,
  addInterceptWordHouseApplicationRel,
  deleteInterceptWordHouseApplicationRel,
} from "@/api/toolManager";
export default {
  data() {
    return {
      pageNo: 1,
      pageSize: 10,
      total: 0,
      konwlwdgeList: [], //知识库 分页
      loading: false,
      knowledgeIdArr: [],
      showAdd: false,
      searchKeyWord: "",
      konwlwdgeAllList: [], //知识库
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    configData: Array,
    appConfigForm: Object,
  },
  mounted() {
    this.knowledgeList();
  },
  methods: {
    // 显示关联知识库
    filterKnowledge(item) {
      if (this.knowledgeIdArr) {
        return this.knowledgeIdArr.includes(item.id);
      } else {
        return false;
      }
    },
    // 显示关联知识库
    filterSelectKnowledge(item) {
      let findItem = this.konwlwdgeAllList.find((items) => items.id == item);
      return findItem?.name;
    },
    knowledgeList() {
      getInterceptWordHouseListAll({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        keyword: this.searchKeyWord,
        applicationId: this.appConfigForm.applicationId,
        order: "create_time",
        sort: "desc"
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeList = res.data?.records;
          this.total = res.data.totalRow;
        } else {
          this.konwlwdgeList = [];
        }
      });
    },
    knowledgeAllList() {
      getInterceptWordHouseListAll({
        pageNo: 1,
        pageSize: 99999,
        applicationId: this.appConfigForm.applicationId,
        order: "create_time",
        sort: "desc"
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeAllList = res.data?.records.filter(item => item.applicationId);
          console.log(this.konwlwdgeAllList, 999)
        } else {
          this.konwlwdgeAllList = [];
        }
      });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.knowledgeList();
    },
    handleCurrentChange(val) {
      this.pageNo = val;
      this.knowledgeList();
    },
    // 添加知识库
    async addItem(data) {
      let res = await addInterceptWordHouseApplicationRel({
        interceptWordHouseId: data.id,
        applicationId: this.appConfigForm.applicationId,
      });
      if (res.code == "000000") {
        this.knowledgeList();
      } else {
        this.$message.warning(res.msg);
      }
    },
    // 移除知识库
    async detItem(data) {
      let res = await deleteInterceptWordHouseApplicationRel([data.relId]);
      if (res.code == "000000") {
        this.knowledgeList();
      }
    },
    // 移除知识库
    async detSelectItem(data) {
      let res = await deleteInterceptWordHouseApplicationRel([data.relId]);
      if (res.code == "000000") {
        this.knowledgeAllList();
      }
    },
    // 确认添加知识库
    setKnowledgeIds() {
      this.$emit("clickConfigParams", "addBaseVisible", this.knowledgeIdArr);
    },
    // 取消添加
    cancelAss() {
      this.knowledgeIdArr = [];
      this.$emit("clickConfig", false);
    },
    konwlwdgeClick(item) {
      this.$store.commit("setKonwledge", item);
    },
    showChange(val) {
      if (val) {
        this.knowledgeAllList();
      } else {
        this.knowledgeList();
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.knowledgeDialog {
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

.base-li {
  height: 48px;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #e1e4eb;
  padding: 0 12px;
  margin-bottom: 8px;

  .li-name {
    font-weight: 400;
    font-size: 14px;
    color: #383d47;
    text-align: left;
    font-style: normal;
    // cursor: pointer;

    > img {
      width: 24px;
      height: 24px;
      color: #a4bffe;
      margin-right: 5px;
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

::-webkit-scrollbar {
  display: none;
}

.el-icon-delete {
  color: #fc3d30;
}
</style>
