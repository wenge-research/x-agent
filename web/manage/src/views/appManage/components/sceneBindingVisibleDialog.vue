<template>
  <el-dialog
    :title="$t('selectSceneLibrary')"
    :visible.sync="dialogVisible"
    width="680px"
    class="knowledgeDialog"
    :before-close="cancelAss"
    append-to-body
  >
    <div class="flex-center just" style="margin-bottom: 16px">
      <el-input
        :placeholder="$t('pleaseInputAgain')"
        prefix-icon="el-icon-search"
        v-model="sceneName"
        style="width: 334px"
        size="small"
        @input="getSceneApplicationRefList()"
      >
      </el-input>
      <el-checkbox v-model="showAdd" @change="showChange">{{
        $t("onlyAdd")
      }}</el-checkbox>
    </div>
    <div style="max-height: 500px; overflow-y: auto" v-loading="loading">
      <ul v-if="!showAdd">
        <li
          class="base-li flex-center just"
          v-for="(item, index) in konwlwdgeList"
          :key="index"
        >
          <div class="li-name flex-center">
            <img src="@/assets/images/appManagement/changjing.svg" />
            <span>{{ item.sceneName }}</span>
          </div>
          <el-button
            v-if="item.checked"
            type="text"
            icon="el-icon-delete"
            style="color: #d82225"
            @click="detItem(item)"
            ></el-button
          >
          <el-button
            v-else
            type="text"
            icon="el-icon-plus"
            style="color: #494E57"
            @click="addSceneApplicationRef(item)"
            ></el-button
          >
        </li>
      </ul>
      <ul v-if="showAdd">
        <li
          class="base-li flex-center just"
          v-for="(item, index) in konwlwdgeListSelected"
          :key="index"
        >
          <div class="li-name flex-center">
            <img src="@/assets/images/appManagement/zsk.svg" />
            <span>{{ item.sceneName }}</span>
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
      :pager-count="5"
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
import {
  apiGetSceneApplicationRefList,
  apiAddSceneApplicationRef,
  apiDeleteSceneApplicationRef,
  apiGetSceneManagementList,
} from "@/api/scene";
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
      sceneName: "",
      konwlwdgeAllList: [], //知识库
      konwlwdgeListSelected: [], // 已绑定知识库
      konwlwdgeListSelectedId: [],
      konwlwdgeListSelectedIdOld: [],
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
  async mounted() {
    await this.getSceneApplicationRefListAll(true);
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
    // 全部
    getSceneApplicationRefList(start) {
      apiGetSceneManagementList({
        pageNo: this.showAdd ? 1 : this.pageNo,
        pageSize: this.showAdd ? 9999 : this.pageSize,
        sceneName: this.sceneName,
      }).then((res) => {
        if (res.code == "000000") {
          // 已绑定
          const list = this.konwlwdgeAllList.map((i) => i.sceneId);
          console.log("list", list);
          if(start) {
            this.konwlwdgeListSelectedIdOld = JSON.parse(JSON.stringify(list));
            this.konwlwdgeListSelectedId = JSON.parse(JSON.stringify(list));
          }else {
            this.konwlwdgeListSelectedId = JSON.parse(JSON.stringify(list));
          }
          this.konwlwdgeList = res.data?.records || [];
          this.konwlwdgeList.forEach((item) => {
            if (list.includes(item.sceneId)) {
              console.log("if");
              item.checked = true;
            } else {
              console.log("else");
              item.checked = false;
            }
          });
          this.konwlwdgeListSelected = this.konwlwdgeList.filter(
            (item) => item.checked
          );
          this.total = res.data.totalRow;
        } else {
          this.konwlwdgeList = [];
        }
      });
    },
    // 已绑定
    getSceneApplicationRefListAll(start) {
      this.loading = true;
      apiGetSceneApplicationRefList({
        pageNo: 1,
        pageSize: 9999,
        applicationId: this.appConfigForm.applicationId,
      }).then(async (res) => {
        if (res.code == "000000") {
          this.konwlwdgeAllList = res.data || [];
          await this.getSceneApplicationRefList(start);
        } else {
          this.konwlwdgeAllList = [];
        }
        this.loading = false;
      });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getSceneApplicationRefList();
    },
    handleCurrentChange(val) {
      this.pageNo = val;
      this.getSceneApplicationRefList();
    },
    // 添加知识库
    async addSceneApplicationRef(data) {
      console.log("data----------", data);
      let res = await apiAddSceneApplicationRef({
        sceneId: data.sceneId,
        applicationId: this.appConfigForm.applicationId,
      });
      if (res.code == "000000") {
        this.$emit("sceneChange")
        await this.getSceneApplicationRefListAll();
      } else {
        this.$message.warning(res.msg);
      }
    },
    // 移除知识库
    async detItem(data) {
      const params = {
        sceneId: data.sceneId,
        applicationId: this.appConfigForm.applicationId,
      };
      let res = await apiDeleteSceneApplicationRef(params);
      if (res.code == "000000") {
        this.$emit("sceneChange")
        await this.getSceneApplicationRefListAll();
      }
    },
    // 移除知识库
    async detSelectItem(data) {
      const params = {
        sceneId: data.sceneId,
        applicationId: this.appConfigForm.applicationId,
      };
      let res = await apiDeleteSceneApplicationRef(params);
      if (res.code == "000000") {
        this.$emit("sceneChange")
        await this.getSceneApplicationRefListAll();
      }
    },
    // 确认添加知识库
    setKnowledgeIds() {
      this.$emit("clickConfigParams", "addBaseVisible", this.knowledgeIdArr);
    },
    // 取消添加
    cancelAss() {
      const flag = this.compareArrays(this.konwlwdgeListSelectedIdOld, this.konwlwdgeListSelectedId);
      if(!flag) {
        this.$EventBus.$emit("changeApplicationStatus", true);
        this.$EventBus.$emit("saveApplication");
      }
      this.knowledgeIdArr = [];
      this.$emit("clickConfig", false);
    },
    konwlwdgeClick(item) {
      this.$store.commit("setKonwledge", item);
    },
    async showChange(val) {
      await this.getSceneApplicationRefListAll();
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
};
</script>

<style lang="scss" scoped>
.knowledgeDialog {
  ::v-deep .el-dialog {
    border-radius: 4px;
    .el-dialog__body {
      padding: 0px 32px 16px;
    }
    .el-dialog__header {
      background-color: #ffffff;
      padding: 32px 32px 16px;
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

.base-li {
  height: 48px;
  background: #ffffff;
  border-radius: 2px;
  border: 1px solid #e1e4eb;
  padding: 0 12px;
  margin-bottom: 8px;
  box-sizing: border-box;
  cursor: pointer;
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
  &:hover {
    background: #F2F4F7;
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
