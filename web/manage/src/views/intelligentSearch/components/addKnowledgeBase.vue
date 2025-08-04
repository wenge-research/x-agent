<template>
  <el-dialog
    :title="$t('associateKnowledgeBase')"
    :visible.sync="dialogVisible"
    width="720px"
    class="knowledgeDialog"
    :before-close="cancelAss"
    append-to-body
  >
    <div class="flex-center just" style="margin-bottom: 16px">
      <el-input
        :placeholder="$t('searchKnowledgeBaseName')"
        prefix-icon="el-icon-search"
        v-model="searchKeyWord"
        style="width: 334px"
        @input="knowledgeList"
      >
      </el-input>
      <el-checkbox v-model="showAdd">{{ $t("onlyAdd") }}</el-checkbox>
    </div>
    <div style="max-height: 500px; overflow-y: auto" v-loading="loading">
      <ul v-if="!showAdd">
        <li
          class="base-li flex-center just"
          v-for="item in konwlwdgeList"
          :key="item.knowledgeId + item.id"
        >
          <div class="li-name flex-center" @click="konwlwdgeClick(item)">
            <img src="@/assets/images/appManagement/zsk.svg" />
            <span>{{ item.knowledgeName }}</span>
          </div>
          <el-button
            v-if="filterKnowledge(item)"
            type="text"
            icon="el-icon-delete"
            style="color: #d82225"
            @click="detItem(item)"
            >{{ $t("remove") }}</el-button
          >
          <el-button
            v-else
            type="text"
            style="color: #1c50fd"
            @click="addItem(item)"
            ><div class="flex-center"> <iconpark-icon name="add-line" color="#1747E5" size="16"></iconpark-icon> {{ $t("add") }}</div></el-button
          >
        </li>
      </ul>
      <ul v-if="showAdd">
        <li
          class="base-li flex-center just"
          v-for="item in knowledgeIdArr"
          :key="item"
        >
          <div class="li-name flex-center">
            <img src="@/assets/images/appManagement/zsk.svg" />
            <span>{{ filterSelectKnowledge(item) }}</span>
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
      style="margin-top: 20px; color: #272a31"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNo"
      :page-sizes="[10, 30, 50, 100, 200]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      background
    >
    </el-pagination>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="setKnowledgeIds">{{
        $t("confirm")
      }}</el-button>
      <el-button @click="cancelAss">{{ $t("cancel") }}</el-button>
    </span>
  </el-dialog>
</template>
  
<script>
import { getKnowledgeInfoList } from "@/api/index";
import { template } from "@antv/x6/lib/util/string/string";
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
  },
  mounted() {
    this.knowledgeList();
    this.knowledgeAllList();
    this.knowledgeIdArr = this.configData || [];
  },
  methods: {
    // 显示关联知识库
    filterKnowledge(item) {
      if (this.knowledgeIdArr) {
        return this.knowledgeIdArr.includes(item.knowledgeId);
      } else {
        return false;
      }
    },
    // 显示关联知识库
    filterSelectKnowledge(item) {
      let findItem = this.konwlwdgeAllList.find(
        (items) => items.knowledgeId == item
      );
      return findItem?.knowledgeName;
    },
    knowledgeList() {
      this.loading = true;
      getKnowledgeInfoList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        knowledgeName: this.searchKeyWord,
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeList = res.data?.records;
          this.total = res.data.totalRow;
          this.loading = false;
        } else {
          this.konwlwdgeList = [];
        }
      });
    },
    knowledgeAllList() {
      getKnowledgeInfoList({
        pageNo: 1,
        pageSize: 99999,
        ownerType: "personalGrant"
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeAllList = res.data?.records;
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
    addItem(data) {
      this.knowledgeIdArr.push(data.knowledgeId);
    },
    // 移除知识库
    detItem(data) {
      let filterArr = this.knowledgeIdArr.filter(
        (items) => items != data.knowledgeId
      );
      this.knowledgeIdArr = filterArr;
    },
    // 移除知识库
    detSelectItem(data) {
      let filterArr = this.knowledgeIdArr.filter((items) => items != data);
      this.knowledgeIdArr = filterArr;
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
      this.$store.commit('setKonwledge', item)
    }
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
    cursor: pointer;

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
</style>
  