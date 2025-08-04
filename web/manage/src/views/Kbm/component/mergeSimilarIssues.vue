<template>
  <!-- 相似问题合并 -->
  <el-drawer
    title="相似问题合并"
    :visible.sync="drawer"
    :before-close="handleClose"
    :append-to-body="true"
    size="1200px"
    custom-class="merge-similar-issues"
  >
    <template>
      <!-- 匹配前 -->
      <div v-if="!showResult" class="page" v-loading="findLoading">
        <div class="page-body">
          <div class="page-body-img">
            <img src="@/assets/images/appManagement/qa-img.png" alt="" />
          </div>
          <div class="page-body-title">查找相似问题</div>

          <div class="page-body-threshold items-center">
            相似度阈值
            <el-tooltip content="通过大模型语义判断问题的相似程度，数值越大，相似度越高">
              <iconpark-icon
                name="question-line"
                color="#C9CDD4"
                size="14"
                style="margin-left: 4px"
              ></iconpark-icon>
            </el-tooltip>
          </div>

          <el-slider v-model="qaSimilarityScore" range :marks="marks"> </el-slider>
          <div class="page-body-match">
            将查找相似度
            <span
              >{{ Math.round(qaSimilarityScoreMin * 100) }}%-{{
                Math.round(qaSimilarityScoreMax * 100)
              }}%</span
            >
            的问题
          </div>
          <div class="page-body-find pointer" @click="findHandler(true)">开始查找</div>
          <div class="page-body-history flex items-center pointer" @click="openHistory">
            <iconpark-icon
              name="file-history-line"
              size="16"
              color="#4E596A"
              style="margin-right: 8px"
            ></iconpark-icon
            >历史记录
          </div>
        </div>
      </div>
      <!-- 匹配后 -->
      <div v-else class="list" v-loading="findLoading">
        <div class="list-head flex items-center">
          <div class="list-head-back" @click="findHandler(false)">
            <iconpark-icon
              name="arrow-go-back-fill"
              size="16"
              color="#4E596A"
            ></iconpark-icon>
          </div>
          <div class="list-head-mark flex-1">
            <div class="list-head-mark-find">
              找到<span>{{ totalQuestion }}</span
              >条符合的问题
            </div>
            <div class="list-head-mark-tips">请选择要删除的问题，每组至少保留1条</div>
          </div>
          <el-input
            placeholder="请输入"
            v-model="keyword"
            style="width: 334px"
            @input="inputChange"
            clearable
          >
            <i
              slot="prefix"
              class="el-input__icon el-icon-search"
              style="color: #4e596a"
            ></i>
          </el-input>
        </div>
        <div class="list-content">
          <ul class="list-content-header flex items-center">
            <li class="col1">相似问题</li>
            <li class="col2">答案</li>
            <li class="col3">相似度</li>
          </ul>
          <div class="list-content-body" :class="[!list.length ? 'nodataHeight' : '']">
            <template v-if="list.length">
              <div
                class="list-content-body-items"
                v-for="(item, index) in list"
                :key="index"
              >
                <ul
                  class="son flex items-center"
                  v-for="(sonItem, sonIndex) in item"
                  :key="sonIndex"
                >
                  <li class="col1">
                    <div class="flex items-center h-full">
                      <el-checkbox v-model="sonItem.isSelected" />
                      {{ sonItem.title }}
                    </div>
                  </li>
                  <li class="col2">
                    <el-popover placement="top" maxWidth="300" trigger="click">
                      <div style="padding: 12px">
                        {{ sonItem.content }}
                      </div>
                      <div slot="reference" class="details">
                        {{ $t("details") }}
                      </div>
                    </el-popover>
                  </li>
                  <!-- v-if="showItem(item, sonIndex)"
                :style="{ height: `${item.length * 100}%` }" -->
                  <li class="son-col3">
                    <div class="percent flex items-center justify-end">
                      {{ Math.round(sonItem.similarityScores * 100) }}<span>%</span>
                    </div>
                  </li>
                </ul>
              </div>
            </template>
            <div
              v-else
              class="list-content-body-nodata flex flex-col justify-center items-center"
            >
              <img src="@/assets/images/no-data.png" alt="" style="width: 182px" />
              <div class="txt">暂无数据</div>
            </div>
          </div>
        </div>
        <div v-if="list.length" class="list-footer flex items-center justify-between">
          <div class="flex items-center">
            <div
              class="btn1 flex items-center justify-center pointer"
              @click="automaticallySelectHandler"
            >
              <iconpark-icon
                name="list-check-2"
                size="16"
                color="#1c50fd"
                style="margin-right: 10px"
              ></iconpark-icon
              >自动勾选
            </div>
            <div class="tips">每组至少保留一条问题</div>
          </div>
          <div class="flex">
            <div
              class="btn2 flex items-center justify-center pointer"
              @click="handleClose"
            >
              取消
            </div>
            <div
              class="btn3 flex items-center justify-center pointer"
              @click="deleteSelectedQuestion"
            >
              删除所选问题
            </div>
          </div>
        </div>
      </div>
    </template>
  </el-drawer>
</template>

<script>
import cloneDeep from "lodash/cloneDeep";
// components
import HistoricalRecords from "./historicalRecords.vue";
// api
import { apiGetKnowledgeDataScope, apiDeleteKnowledgeData } from "@/api/Kbm";
export default {
  name: "MergeSimilarIssues",
  components: { HistoricalRecords },
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    knowledgeId: {
      type: String,
    },
  },
  data() {
    return {
      drawer: false,
      input4: "",
      checkValue: false,
      arraySource: [],
      list: [],
      // sonList: [{}, {}, {}],
      qaSimilarityScore: [0, 100],
      marks: {
        0: "0",
        20: "",
        40: "",
        60: "",
        80: "",
        100: "100%",
      },
      showResult: false,
      findLoading: false,
      keyword: "",
    };
  },
  computed: {
    qaSimilarityScoreMin() {
      return this.qaSimilarityScore?.length ? this.qaSimilarityScore[0] / 100 : 0;
    },
    qaSimilarityScoreMax() {
      return this.qaSimilarityScore?.length ? this.qaSimilarityScore[1] / 100 : 1;
    },
    totalQuestion() {
      return this.list?.flat()?.length || 0;
    },
  },
  watch: {
    value(n) {
      this.drawer = n;
    },
  },
  methods: {
    handleClose() {
      this.drawer = false;
      this.showResult = false;
      this.qaSimilarityScore = [0, 100];
      this.$emit("closeDrawer");
    },
    showItem(dataList, idx) {
      return dataList?.length && Math.floor(dataList.length / 2) == idx ? true : false;
    },
    // 开始查找/返回设置阈值页
    findHandler(result) {
      if (result) {
        this.getKnowledgeDataScope(result);
      } else {
        this.showResult = result;
      }
    },
    // 打开记录记录
    openHistory() {
      this.$emit("openHistory");
      this.drawer = false;
    },
    // 根据重复度阀值设置重复度
    async getKnowledgeDataScope(result) {
      try {
        this.findLoading = true;
        const params = {
          knowledgeId: this.knowledgeId,
          qaSimilarityScoreMin: this.qaSimilarityScoreMin,
          qaSimilarityScoreMax: this.qaSimilarityScoreMax,
        };
        let res = await apiGetKnowledgeDataScope(params);
        if (res.code == "000000") {
          this.showResult = result;
          this.arraySource =
            res?.data?.map((group) =>
              group.map((item) => ({
                ...item,
                isSelected: false, // 默认设置为 false，也可以根据需求设置其他默认值
              }))
            ) || [];
          this.list = cloneDeep(this.arraySource);
        }
      } catch (error) {
        this.findLoading = false;
      }
      this.findLoading = false;
    },
    // 自动勾选
    automaticallySelectHandler() {
      this.list = this.list.map((group) => [
        { ...group[0], isSelected: false }, // 第一条保持 false
        ...group.slice(1).map((item) => ({ ...item, isSelected: true })), // 其余设为 true
      ]);
      console.log("自动勾选的list", this.list);
    },
    inputChange() {
      if (!this.arraySource?.length) return;
      this.findLoading = true;
      const sourceList = JSON.parse(JSON.stringify(this.arraySource));
      this.list = sourceList
        .map((group) => group.filter((item) => item.title.includes(this.keyword)))
        .filter((group) => group.length > 0); // 过滤掉空分组
      this.findLoading = false;
    },
    deleteSelectedQuestion() {
      const list = this.list
        ?.flat()
        ?.filter((item) => item.isSelected)
        ?.map((item) => item.id);
      if (!list?.length) return this.$message.warning("请选择要删除的问题");
      this.$confirm(`${this.$t("confirmDelete")}?`, `${this.$t("tips")}`, {
        confirmButtonText: this.$t("confirm"),
        cancelButtonText: this.$t("cancel"),
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => await this.deleteKnowledgeData(list));
    },
    // 删除QA对
    async deleteKnowledgeData(list) {
      try {
        this.findLoading = true;
        let res = await apiDeleteKnowledgeData({
          id: list,
        });
        if (res.code == "000000") {
          // this.$message.success(res.msg);
          setTimeout(() => {
            this.keyword = "";
            this.getKnowledgeDataScope(true);
          }, 1000);
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.findLoading = false;
      }
    },
  },
};
</script>

<style lang="scss">
.merge-similar-issues {
  .h-full {
    height: 100%;
  }
  .w-full {
    height: 100%;
  }
  .flex {
    display: flex;
  }
  .flex-col {
    flex-direction: column;
  }
  .flex-1 {
    flex: 1;
  }
  .items-center {
    align-items: center;
  }
  .justify-center {
    justify-content: center;
  }
  .justify-between {
    justify-content: space-between;
  }
  .justify-end {
    justify-content: flex-end;
  }
  .pointer {
    cursor: pointer;
  }
  .el-drawer__header {
    padding: 32px 32px 8px 32px;
    margin-bottom: 16px;
  }
  .el-drawer__body {
    padding: 0 32px 32px;
    .list {
      width: 100%;
      height: 100%;
      position: relative;
      &-head {
        height: 48px;
        &-back {
          width: 32px;
          height: 32px;
          text-align: center;
          line-height: 32px;
          cursor: pointer;
          margin-right: 8px;
        }
        &-mark {
          &-find {
            font-family: HarmonyOS_Sans_SC_Medium;
            font-size: 18px;
            color: #4e596a;
            line-height: 24px;
            span {
              margin: 0 4px;
              font-family: HarmonyOS_Sans_SC;
              font-size: 18px;
              line-height: 24px;
              color: #1c50fd;
            }
          }
          &-tips {
            margin-top: 6px;
            font-family: HarmonyOS_Sans_SC;
            font-size: 14px;
            color: #86909c;
            line-height: 18px;
          }
        }
      }
      &-content {
        margin-top: 24px;
        height: calc(100% - 48px - 56px - 54px);
        overflow: hidden;
        &-header {
          width: 100%;
          height: 40px;
          line-height: 40px;
          background: rgba(247, 248, 250, 1);
          font-family: HarmonyOS_Sans_SC;
          font-size: 14px;
          color: #4e596a;
        }
        &-body {
          width: 100%;
          max-height: calc(100% - 40px);
          overflow: auto;
          .el-checkbox__input.is-checked .el-checkbox__inner,
          .el-checkbox__input.is-indeterminate .el-checkbox__inner {
            background-color: #1c50fd;
            border-color: #1c50fd;
          }
          &::-webkit-scrollbar {
            display: none; /* Chrome, Safari, Opera */
          }
          &-items {
            position: relative;
            width: 100%;
            padding: 2px 0;
            border-bottom: 1px solid #e5e6ea;
            .son {
              width: 100%;
              margin-top: 4px;
              height: 38px;
            }
          }
          &-nodata {
            width: 100%;
            height: 100%;
            .txt {
              font-family: HarmonyOS_Sans_SC_Medium;
              font-size: 16px;
              color: #4e596a;
              line-height: 24px;
            }
          }
        }
        .col1 {
          width: 83%;
          height: 100%;
          padding-left: 12px;
          div {
            font-family: HarmonyOS_Sans_SC;
            font-size: 14px;
            color: #4e596a;
            .el-checkbox {
              margin-right: 8px;
            }
          }
        }
        .col2 {
          width: 7%;
          height: 100%;
          padding-left: 12px;
          border-left: 1px solid #fff;
          border-right: 1px solid #fff;
          .details {
            width: 100%;
            height: 100%;
            line-height: 38px;
            font-family: MiSans, MiSans;
            font-size: 14px;
            color: #1c50fd;
            cursor: pointer;
            z-index: 999;
          }
        }
        .col3 {
          width: 10%;
          height: 100%;
          padding-right: 12px;
          text-align: right;
        }
        .son-col3 {
          width: 10%;
          height: 100%;
          padding-right: 12px;
          text-align: right;
          .percent {
            // position: absolute;
            // top: 0;
            // right: 0;
            // width: 100%;
            height: 100%;
            font-family: Helvetica, Helvetica;
            font-weight: 600;
            font-size: 16px;
            color: #876afd;
            line-height: 38px;
            span {
              font-size: 16px;
              font-weight: 600;
            }
          }
        }
      }
      .nodataHeight {
        height: 100%;
      }
      &-footer {
        margin-top: 46px;
        width: 100%;
        height: 56px;
        padding: 16px 0 32px;
        .btn1 {
          width: 124px;
          height: 40px;
          border-radius: 2px;
          border: 1px solid #1c50fd;
          font-family: MiSans, MiSans;
          font-size: 14px;
          color: #1c50fd;
          margin-right: 12px;
        }
        .btn2 {
          width: 72px;
          height: 40px;
          border-radius: 2px;
          border: 1px solid #e7e7e7;
          font-family: MiSans, MiSans;
          font-size: 14px;
          color: #4e596a;
          margin-right: 16px;
        }
        .btn3 {
          width: 128px;
          height: 40px;
          background: #1c50fd;
          border-radius: 2px;
          font-family: HarmonyOS_Sans_SC;
          font-size: 14px;
          color: #ffffff;
        }
        .tips {
          font-family: HarmonyOS_Sans_SC;
          font-size: 16px;
          color: #86909c;
        }
      }
    }
    .page {
      width: 100%;
      height: 100%;
      position: relative;
      .el-slider__bar {
        background-color: rgb(23, 71, 229);
      }
      .el-slider__button {
        border: 2px solid rgb(23, 71, 229);
      }
      &-body {
        margin: 0 auto;
        width: 340px;
        &-img {
          text-align: center;
          img {
            height: 144px;
          }
        }
        &-title {
          margin-top: 16px;
          font-family: HarmonyOS_Sans_SC_Medium;
          font-size: 18px;
          color: #494e57;
          line-height: 24px;
          text-align: center;
        }
        &-threshold {
          display: inline-flex;
          margin-top: 48px;
          margin-bottom: 4px;
          font-family: HarmonyOS_Sans_SC_Medium;
          font-size: 14px;
          color: #4e596a;
          line-height: 20px;
          cursor: pointer;
        }
        &-match {
          margin-top: 40px;
          font-family: HarmonyOS_Sans_SC;
          font-size: 14px;
          color: #4e596a;
          line-height: 20px;
          text-align: center;
          span {
            color: #1c50fd;
          }
        }
        &-find {
          margin-top: 12px;
          width: 100%;
          height: 40px;
          line-height: 40px;
          text-align: center;
          background: #1c50fd;
          border-radius: 2px;
          font-family: HarmonyOS_Sans_SC;
          font-size: 14px;
          color: #ffffff;
        }
        &-history {
          height: 40px;
          border-radius: 2px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #4e596a;
          line-height: 20px;
          text-align: left;
          font-style: normal;
          position: absolute;
          bottom: 0;
          left: 50%; /* 相对父容器左移50% */
          transform: translateX(-50%); /* 自身向左回退50%宽度 */
        }
      }
    }
  }
}
</style>
