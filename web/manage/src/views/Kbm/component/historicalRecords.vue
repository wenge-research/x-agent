<template>
  <!-- 历史记录 -->
  <el-drawer
    title="操作记录"
    :visible.sync="drawer"
    :before-close="handleClose"
    @opened="openedDrawer"
    :append-to-body="true"
    size="960px"
    custom-class="historical-records"
  >
    <template>
      <div class="table">
        <div class="table-search flex items-center">
          <div class="table-search-time flex items-center">
            <div class="table-search-time-label">删除时间</div>
            <el-date-picker
              v-model="time"
              type="datetimerange"
              range-separator="到"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd HH:mm:ss"
              @change="onChange"
            >
            </el-date-picker>
          </div>
          <el-input
            placeholder="输入问题关键词"
            v-model="keyword"
            style="width: 336px"
            clearable
            @keydown.enter.native="onChange"
          >
            <i
              slot="prefix"
              class="el-input__icon el-icon-search"
              style="color: #4e596a; cursor: pointer"
              @click="onChange"
            ></i>
          </el-input>
        </div>
        <div class="table-content">
          <div class="table-content-header flex items-center">
            <div class="table-content-header-selected flex items-center">
              已选<span>{{ multipleSelection?.length }}</span
              ><iconpark-icon
                name="eraser-line"
                size="16"
                color="#86909C"
                style="margin-left: 12px"
              ></iconpark-icon>
            </div>
            <el-checkbox
              v-model="checkAll"
              class="table-content-header-checkbox"
              @change="handleSelectAllChange"
              >全部</el-checkbox
            >
            <div
              class="table-content-header-withdraw flex items-center pointer"
              @click="handleWithdrawSelectAllChange"
            >
              <iconpark-icon
                name="reset-left-line"
                size="15"
                color="#1747e5"
                style="margin-right: 6px"
              ></iconpark-icon
              >撤回所选
            </div>
          </div>
          <div class="table-content-body">
            <el-table
              ref="multipleTable"
              :data="tableData"
              tooltip-effect="dark"
              style="width: 100%"
              height="612"
              class="history-table"
              show-overflow-tooltip
              @selection-change="handleSelectionChange"
              v-loading="tableLoading"
            >
              <el-table-column type="selection" width="55"> </el-table-column>
              <el-table-column label="问题">
                <template slot-scope="scope">{{ scope.row.title }}</template>
              </el-table-column>
              <el-table-column prop="updateUser" label="操作人" width="120">
              </el-table-column>
              <el-table-column
                prop="updateTime"
                label="删除时间"
                show-overflow-tooltip
                width="180"
              >
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template slot-scope="scope">
                  <el-button type="text" @click="recoverKnowledgeData(scope.row)"
                    >撤回</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
            <div class="table-content-body-pagination flex items-center justify-between">
              <div class="total flex items-center">
                总计<span>{{ total }}</span
                >条，共<span>{{ pages }}</span
                >页
              </div>
              <el-pagination
                background
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pageNo"
                :page-sizes="[10, 20, 30, 40, 50]"
                :page-size="pageSize"
                layout="prev, pager, next, sizes, jumper"
                :total="total"
              >
              </el-pagination>
            </div>
          </div>
        </div>
      </div>
    </template>
  </el-drawer>
</template>

<script>
// api
import { apiGetDeleteKnowledgeData, apiRecoverKnowledgeData } from "@/api/Kbm";

export default {
  name: "MergeSimilarIssues",
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
      keyword: "",
      tableData: [],
      multipleSelection: [],
      time: "",
      pageNo: 1,
      pageSize: 20,
      total: 0,
      pages: 0,
      checkAll: false,
      tableLoading: false,
    };
  },
  computed: {
    updateTimeStart() {
      return this.time?.length ? this.time[0] : "";
    },
    updateTimeEnd() {
      return this.time?.length ? this.time[1] : "";
    },
  },
  watch: {
    value(n) {
      this.drawer = n;
    },
  },
  methods: {
    openedDrawer() {
      this.getDeleteKnowledgeData();
    },
    handleClose() {
      this.drawer = false;
      this.$emit("closeHistoricalRecordsDrawer");
    },
    // 选中行
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 选中全部
    handleSelectAllChange(val) {
      if (val) {
        this.$refs.multipleTable.toggleAllSelection();
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    handleWithdrawSelectAllChange() {
      if (!this.multipleSelection.length)
        return this.$message.warning("请选择要撤回的数据");
      this.recoverKnowledgeData();
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
      this.pageSize = val;
      this.getDeleteKnowledgeData();
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
      this.pageNo = val;
      this.getDeleteKnowledgeData();
    },
    // 查询被删除的QA
    async getDeleteKnowledgeData() {
      try {
        this.tableLoading = true;
        const params = {
          knowledgeId: this.knowledgeId,
          updateTimeStart: this.updateTimeStart,
          updateTimeEnd: this.updateTimeEnd,
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          title: this.keyword,
        };
        let res = await apiGetDeleteKnowledgeData(params);
        if (res.code == "000000") {
          this.tableData = res?.data?.list || [];
          this.total = res.data.total || 0;
          this.pages = res.data.pages || 0;
        }
      } catch (error) {
        this.tableLoading = false;
      } finally {
        this.checkAll = false;
      }
      this.tableLoading = false;
    },
    onChange() {
      this.pageNo = 1;
      this.getDeleteKnowledgeData();
    },
    // 恢复删除的QA
    async recoverKnowledgeData(data) {
      try {
        this.tableLoading = true;
        let list = [];
        if (data?.id) {
          list = [data.id];
        } else {
          list = this.multipleSelection.map((item) => item.id);
        }
        const res = await apiRecoverKnowledgeData({
          id: list,
        });
        if (res.code == "000000") {
          console.log("res", res);
          // this.$message.success(res.msg);
          setTimeout(() => {
            this.onChange();
          }, 1000);
        }
      } catch (error) {
        this.tableLoading = false;
      }
    },
  },
};
</script>

<style lang="scss">
.historical-records {
  .h-full {
    height: 100%;
  }
  .w-full {
    height: 100%;
  }
  .flex {
    display: flex;
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
    .table {
      width: 100%;
      height: 100%;
      &-search {
        height: 48px;
        &-time {
          margin-right: 24px;
          &-label {
            margin-right: 12px;
            font-family: HarmonyOS_Sans_SC;
            font-size: 14px;
            color: #4e596a;
          }
        }
      }
      &-content {
        margin-top: 20px;
        height: calc(100% - 48px - 32px);
        overflow: hidden;
        .el-checkbox__input.is-checked .el-checkbox__inner,
        .el-checkbox__input.is-indeterminate .el-checkbox__inner {
          background-color: #1c50fd;
          border-color: #1c50fd;
        }
        .el-checkbox__input.is-checked + .el-checkbox__label {
          color: #1c50fd;
        }
        &-header {
          margin-bottom: 8px;
          height: 32px;
          &-selected {
            font-family: HarmonyOS_Sans_SC;
            font-size: 14px;
            color: #86909c;
            span {
              display: inline-block;
              width: 16px;
              color: #1747e5;
              margin: 0 4px 0;
            }
          }
          &-checkbox {
            margin: 0 30px 0 24px;
          }
          &-withdraw {
            color: #1747e5;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
          }
        }
        &-body {
          height: calc(100% - 40px);
          &-pagination {
            padding-top: 24px;
            height: 56px;
            .total {
              font-family: HarmonyOS_Sans_SC;
              font-size: 14px;
              color: #272a31;
            }
            .el-pagination.is-background .el-pager li:not(.disabled).active {
              background-color: #1747e5;
            }
            .el-range-editor.is-active,
            .el-range-editor.is-active:hover,
            .el-select .el-input.is-focus .el-input__inner {
              border-color: #1747e5;
            }
          }
          // 表格样式
          .history-table.el-table {
            .el-table__header {
              .el-table__cell {
                padding: 0;
                border-right: 1px solid #fff;
                background: #f2f4f7;
                .cell {
                  background: #f2f4f7;
                  font-family: MiSans, MiSans;
                  font-weight: 500;
                  font-size: 14px;
                  color: #828894;
                  line-height: 20px;
                  padding: 10px 12px;
                }
              }
            }
            .el-table__cell {
              padding: 8px 0;
            }
            .el-button--text:not(.is-disabled) {
              color: #1747e5;
            }
            // 斑马格
            .el-table__body {
              tr.el-table__row--striped td.el-table__cell {
                background: #f7f8fa;
              }
            }
          }
        }
      }
    }
  }
}
</style>
