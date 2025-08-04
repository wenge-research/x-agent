<template>
  <div class="structuring-info">
    <div class="header">
      <div class="header-left">
        <img
          class="back-icon"
          src="@/assets/images/arrow-go-back-fill.svg"
          @click="handleClose"
        />
        <div class="title">{{ row.desc }}</div>
      </div>
      <div class="header-right">
        <el-button
          type="primary"
          class="icon-primary-self"
          @click="runVisible = true"
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path
              fill-rule="nonzero"
              d="M4 8h16V5H4v3Zm10 11v-9h-4v9h4Zm2 0h4v-9h-4v9Zm-8 0v-9H4v9h4ZM3 3h18a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z"
              data-follow-fill="#848587"
            />
          </svg>
          {{ $t("startStructuring") }}
        </el-button>
      </div>
    </div>
    <div class="main">
      <div class="operating-box">
        <div class="form">
          <el-form :inline="true">
            <el-form-item>
              <el-input
                v-model="keyword"
                :placeholder="$t('pleaseEnterKeyword')"
                clearable
                @input="handleSearch"
                style="width: 344px"
              >
                <i slot="suffix" class="el-input__icon el-icon-search"
              /></el-input>
            </el-form-item>
            <!-- <el-form-item>
              <el-button type="primary" @click="getTableListData">{{$t('search')}}</el-button>
            </el-form-item> -->
          </el-form>
        </div>
        <div class="options">
          <el-button plain class="icon-plain-self" @click="tagVisible = true">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
              <path
                fill-rule="nonzero"
                d="M5 19h14V5H5v14ZM3 4a1 1 0 0 1 1-1h16a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V4Zm12 3v10h2V7h-2Zm-4 0v10h2V7h-2ZM7 17V7h2v10H7Z"
                data-follow-fill="#848587"
              />
            </svg>
            {{ $t("dataColumnManagement") }}
          </el-button>
        </div>
      </div>
      <div class="main-box">
        <div
          class="sql-box"
          v-if="row.type === 'datasource'"
          v-loading="sqlLoading"
        >
          <div class="title">{{ $t("libraryTableDirectory") }}</div>
          <el-input
            v-model="sqlName"
            size="small"
            @input="searchSqlTable"
            :placeholder="$t('enterTableNameSearch')"
          >
            <i slot="suffix" class="el-input__icon el-icon-search" />
          </el-input>
          <div class="list">
            <div
              :class="sqlIndex == index ? 'item on' : 'item'"
              v-for="(item, index) in sqlList"
              :key="index"
              @click="addSql(item, index)"
            >
              <img src="@/assets/images/sql.png" />
              {{ item }}
            </div>
          </div>
        </div>
        <div class="table-box">
          <el-table
            v-if="tableData.length"
            class="my-table"
            :data="tableData"
            max-height="650"
            width="100%"
            stripe
            v-loading="tableLoading"
            @selection-change="handleSelectionChange"
          >
            <el-table-column
              type="selection"
              width="55"
              v-if="row.type === 'excel'"
            ></el-table-column>
            <!-- <template v-if="row.type === 'excel'"> -->
            <el-table-column
              v-for="column in tableColumns"
              :key="column.columnName"
              :prop="column.columnName"
              :label="column.remarks"
              min-width="100"
              show-overflow-tooltip
            >
            </el-table-column>
            <!-- </template>
			<template v-else>
				<el-table-column v-for="column in tableColumns" :key="column" :prop="column"
				  :label="column" min-width="100">
				</el-table-column> 
			</template> -->
          </el-table>
          <div v-else class="no-data">
            <img class="no-data-img" src="@/assets/images/no-data.png" alt="" />
            <div class="no-data-text">{{ $t("noData") }}</div>
          </div>
        </div>
      </div>
      <div class="pagination">
        <div class="total-num">
          {{ $t("totalNum", { total: total }) }}
        </div>
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-size="pageSize"
          background
          :current-page="pageNo"
          :page-sizes="[20, 50, 100, 150, 200]"
          layout="prev, pager, next, sizes, jumper"
          :total="total"
        >
        </el-pagination>
      </div>
    </div>

    <el-drawer
      class="my-drawer"
      :title="$t('pleaseSelectColumnsToStructure')"
      :visible.sync="tagVisible"
      size="480px"
      append-to-body
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      :wrapperClosable="false"
    >
      <div slot class="tag-drawer" v-if="row.type === 'excel'">
        <div class="content">
          <div class="btns">
            <div class="left">
              {{ $t("selected") }}
              <span>{{ selectedTableColumns.length }}</span>
            </div>
            <div class="right">
              <el-button type="text" @click="handleCheckAll(true)">{{
                $t("selectAll")
              }}</el-button>
              <el-button type="text" @click="handleCheckAll(false)">{{
                $t("invertSelection")
              }}</el-button>
            </div>
          </div>
          <el-input
            v-model="columnName"
            :placeholder="$t('enterColumnNameSearch')"
            clearable
            @input="handleSearchColumn"
          >
            <i slot="prefix" class="el-input__icon el-icon-search" />
          </el-input>
          <div class="tags">
            <div
              class="item"
              v-for="item in allTableInfo"
              :key="item.columnName"
            >
              <el-checkbox
                v-model="item.flag"
                :label="item.remarks"
                size="medium"
                @change="
                  () => {
                    handleCheckChange(item.columnName, item.flag);
                  }
                "
              ></el-checkbox>
            </div>
          </div>
        </div>
        <div class="footer">
          <el-button
            :loading="tagLoading"
            @click="handlelCloseOseperationTag"
            >{{ $t("close") }}</el-button
          >
          <el-button
            :loading="tagLoading"
            type="primary"
            @click="handleOperationTag"
            >{{ $t("confirm") }}</el-button
          >
        </div>
      </div>
      <div slot class="tag-drawer" v-else>
        <div class="content">
          <div class="tags">
            <div
              class="item"
              v-for="item in allTableInfo"
              :key="item.columnName"
            >
              <el-checkbox
                v-model="item.flag"
                :label="item.columnName"
                size="medium"
                @change="
                  () => {
                    handleCheckChange(item.columnName, item.flag);
                  }
                "
              ></el-checkbox>
            </div>
          </div>
        </div>
        <div class="footer">
          <el-button
            :loading="tagLoading"
            @click="handlelCloseOseperationTag"
            >{{ $t("close") }}</el-button
          >
          <el-button
            :loading="tagLoading"
            type="primary"
            @click="handleOperationTag"
            >{{ $t("confirm") }}</el-button
          >
        </div>
      </div>
    </el-drawer>

    <el-dialog
      class="my-dialog"
      append-to-body
      :title="$t('aboutToStructureDataContinue')"
      :visible.sync="runVisible"
      width="560px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
      append-to-body
    >
      <div class="hint">
        {{
          multipleSelection.length === 0
            ? $t("noRowColumnManagement")
            : $t("willProcess") + multipleSelection.length + $t("structureData")
        }}
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="runLoading" @click="runVisible = false">{{
          $t("cancel")
        }}</el-button>
        <el-button
          :loading="runLoading"
          type="primary"
          @click="handleRunTask"
          >{{ $t("continue") }}</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getExcelTableDataList,
  chooseField,
  excelRunTask,
  getTableList,
  getDataSourceDataList,
  dataSourceRunTask,
} from "@/api/index.js";
import { debounce } from "lodash";

export default {
  props: {
    row: {
      type: Object,
      default: () => {
        return {};
      },
    },
    knowledgeId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      sqlIndex: 0,
      total: 0,
      title: "",
      pageNo: 1,
      pageSize: 50,
      runLoading: false,
      runVisible: false,
      tagLoading: false,
      tagVisible: false,
      tableLoading: true,
      tableColumns: [], // 表头
      tableData: [], // 数据
      selectedTag: [],
      tableName: "",
      multipleSelection: [],
      sqlLoading: true,
      sqlList: [],
      copySqlList:[],
      sqlName: '',
      keyword: "",
      columnName: "",
      allTableInfo: [], // 数据
      selectedColumns: []
    };
  },
  watch: {
    async "row.businessId"(newVal) {
      if (this.row.type === "datasource") {
        await this.getSqlListData();
      }
      this.getTableListData();
      this.mySearchSql=this.searchSqlTable()
    },
  },
  computed: {
    selectedTableColumns() {
      return this.allTableInfo.filter((n) => n.flag);
    },
  },
  async created() {
    if (this.row.type === "datasource") {
      await this.getSqlListData();
    }
    this.getTableListData();
    
  },
  methods: {
    addSql(item, index) {
      this.sqlIndex = index;
      this.tableName = item;
      this.getTableListData();
    },
    handleCheckChange(column, flag) {
      this.selectedColumns = this.selectedColumns.map((n) => {
        return {
          ...n,
          flag: n.columnName === column ? flag : n.flag,
        };
      });
    },
    handleSearch: debounce(async function (value) {
      this.getTableListData();
    }, 500),
    handleSearchColumn: debounce(async function (value) {
      this.allTableInfo = this.tableColumns.filter((n) => {
        let findObj = this.selectedColumns.find(
          (m) => m.columnName === n.columnName
        );
        n.flag = findObj?.flag;
        return n.remarks.includes(value);
      });
      // this.getTableListData()
    }, 500),
    handleCheckAll(flag) {
      this.allTableInfo = this.allTableInfo.map((n) => {
        return {
          ...n,
          flag: flag,
        };
      });
      this.selectedColumns = this.selectedColumns.map((n) => {
        let findObj = this.allTableInfo.find(
          (m) => m.columnName === n.columnName
        );
        return {
          ...n,
          flag: findObj ? flag : n.flag,
        };
      });
    },
    async getSqlListData() {
      this.sqlLoading = true;
      const res = await getTableList({
        businessId: this.row.businessId,
      })
      this.sqlList = res.data
      this.copySqlList=res.data;
      this.tableName = res.data[0]
      this.sqlLoading = false
    },
    handleRunTask() {
      if (this.multipleSelection.length === 0) {
        this.runTask([]);
      } else {
        const ids = this.multipleSelection.map((n) => n.id);
        this.runTask(ids);
      }
    },
    async runTask(ids) {
      this.runLoading = true;
      try {
        let res;
        if (this.row.type === "datasource") {
          res = await dataSourceRunTask({
            tableName: this.tableName,
            knowledgeId: this.knowledgeId,
            dataSourceId: this.row.businessId,
          });
        } else {
          res = await excelRunTask({
            tableName: this.tableName,
            knowledgeId: this.knowledgeId,
            excelId: this.row.businessId,
            ids: ids,
          });
        }
        if (res.code === "000000") {
          this.runVisible = false;
          this.$message({
            message: res.msg,
            type: "success",
          });
        } else {
          this.$message({
            message: res.msg,
            type: "error",
          });
        }
        this.runLoading = false;
      } catch (error) {
        this.$message({
          message: error,
          type: "error",
        });
        this.runLoading = false;
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    async handleOperationTag() {
      try {
        let res;
        // if(this.row.type === 'excel'){
        res = await chooseField({
          tableName: this.tableName,
          businessId: this.row.businessId,
          tableInfoVos: this.allTableInfo.map((n) => {
            return {
              ...n,
              flag: n.flag === true ? 2 : 1,
            };
          }),
        });
        // }else{
        // 	let list = []
        // 	this.allTableInfo.map(n => {
        // 		if(n.flag){
        // 			list.push({
        // 			  columnName:n.columnName
        // 			})
        // 		}
        // 	  })
        // 	res = await chooseField({
        // 	  tableName: this.tableName,
        // 	  businessId: this.row.businessId,
        // 	  tableInfoVos:list
        // 	})
        // }

        if (res.code === "000000") {
          this.tagVisible = false;
          this.getTableListData();
          this.$message({
            message: res.msg,
            type: "success",
          });
        } else {
          this.$message({
            message: res.msg,
            type: "error",
          });
        }
        this.tagLoading = false;
      } catch (error) {
        this.$message({
          message: error,
          type: "error",
        });
        this.tagLoading = false;
      }
    },
    handlelCloseOseperationTag() {
      this.tagVisible = false;
    },
    handleClose() {
      this.$emit("close");
    },
    handleCurrentChange(n) {
      this.pageNo = n;
      this.getTableListData();
    },
    handleSizeChange(n) {
      this.pageSize = n;
      this.getTableListData();
    },
    async getTableListData() {
      this.tableLoading = true;
      let res;
      if (this.row.type === "excel") {
        res = await getExcelTableDataList({
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          excelId: this.row.businessId,
          keyword: this.keyword,
        });
      } else {
        res = await getDataSourceDataList({
          dataSourceId: this.row.businessId,
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          bussiness: this.row.businessId,
          tableName: this.tableName,
          keyword: this.keyword,
        });
      }
      this.tableLoading = false
	   // if (this.row.type === 'excel') {
		   if(res.data.allTableInfo){
		   		  this.tableColumns = res.data.allTableInfo.map(n => {
		   		    return {
		   		      ...n,
		   		      flag: n.flag === 1 ? false : true
		   		    }
		   		  })
		   		  this.allTableInfo = res.data.allTableInfo.map(n => {
		   		    return {
		   		      ...n,
		   		      flag: n.flag === 1 ? false : true
		   		    }
		   		  })
		   		  this.selectedColumns = res.data.allTableInfo.map(n => {
		   		    return {
		   		      ...n,
		   		      flag: n.flag === 1 ? false : true
		   		    }
		   		  })
		   }
		   if(res.data.tableName||res.data.data.tableName){
			   this.tableName = res.data.tableName ? res.data.tableName : res.data.data.tableName ? res.data.data.tableName : ''
		   }
		   this.total = res.data.data.total
		   this.tableData = res.data.data.records
		// }else{
		// 	this.tableColumns = res.data.allTableInfo
		// 	this.allTableInfo = res.data.allTableInfo.map(n => {
		// 	  return {
		// 	    columnName:n,
		// 	    flag: true
		// 	  }
		// 	})
		// }
	 
    //  console.log('this.tableColumns',this.tableColumns)
    //   this.total = res.data.data.total
    //   this.tableData = res.data.data.records
    },
    searchSqlTable:debounce(function(){
      this.sqlLoading=true
      this.sqlList=this.copySqlList.filter(item=>item.includes(this.sqlName))
      if(!this.sqlList.length) this.tableData=[]
      this.addSql(this.sqlList[0],0)
      this.sqlLoading=false
    },400)
  }
}
</script>

<style lang="scss" scoped>
@import "./kbm.scss";

.structuring-info {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  .main {
    padding: 24px 32px 32px;
    box-sizing: border-box;
    flex-grow: 1;
    height: calc(100% - 80px);
    display: flex;
    flex-direction: column;
    border-radius: 4px;
    background: #ffffff;
    overflow: hidden;
  }
  .operating-box {
    margin-bottom: 24px;
    box-sizing: border-box;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .el-form-item {
      margin-bottom: 0px;
    }

    .options {
      display: flex;
      align-items: center;
      .el-button:focus {
        svg {
          fill: #1747e5;
        }
      }
    }
  }

  .main-box {
    display: flex;
    flex-grow: 1;
    box-sizing: border-box;

    .sql-box {
      width: 288px;
      flex-shrink: 0;
      margin-right: 20px;
      padding: 20px;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;

      .title {
        font-weight: 500;
        font-size: 16px;
        color: #383d47;
        line-height: 24px;
        margin-bottom: 12px;
      }

      .list {
        margin-top: 12px;
        flex-grow: 1;
        overflow-y: scroll;
        height: 70vh;
        &::-webkit-scrollbar {
          width: 5px;
          height: 7px;
        }

        &::-webkit-scrollbar-thumb {
          border-radius: 5px;
          box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
          background: #535353;
        }

        &::-webkit-scrollbar-track {
          border-radius: 5px;
          background: #fff;
          cursor: pointer;
        }

        .item {
          width: 100%;
          height: 40px;
          display: flex;
          align-items: center;
          padding: 0 12px;
          box-sizing: border-box;
          border-radius: 4px;
          margin-bottom: 5px;
          cursor: pointer;
          font-weight: 400;
          font-size: 16px;
          color: #383d47;
          line-height: 20px;

          img {
            width: 20px;
            // height: 16px;
            margin-right: 8px;
          }
        }
        .on {
          background: #e8edfd;
        }
        .item:hover {
          background: #e8edfd;
        }
      }
    }

    .table-box {
      width: 100%;
      height: 100%;
      // flex-grow: 1;
      ::v-deep .my-table.el-table .el-table__header .el-table__cell .cell {
        padding: 10px 12px;
      }
      ::v-deep .my-table.el-table--scrollable-x .el-table__body-wrapper {
        scrollbar-width: auto;
        &::-webkit-scrollbar {
          display: block;
        }
      }
      .no-data {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        &-img {
          width: 240px;
          height: 164px;
        }
        &-text {
          margin-top: 16px;
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }

  .header {
    width: 100%;
    height: 80px;
    padding: 24px 32px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.12);
    background: #fff;
    display: flex;
    align-items: center;
    flex-shrink: 0;
    .header-left {
      width: 80%;
      display: flex;
      align-items: center;
      flex-shrink: 0;
      .back-icon {
        width: 20px;
        margin-right: 8px;
        cursor: pointer;
      }
      .type-icon {
        width: 28px;
        margin-right: 8px;
      }

      .title {
        max-width: 70%;
        margin-right: 8px;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        -webkit-line-clamp: 1;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #494e57;
        line-height: 28px;
      }
      .tip-icon {
      }
    }

    .header-right {
      width: 20%;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      .icon-self {
        svg {
          width: 16px;
          vertical-align: text-top;
          fill: #494e57;
        }
        &:hover {
          svg {
            fill: #1747e5;
          }
        }
      }
    }
  }
}
.my-dialog {
  ::v-deep .el-dialog__header {
    padding-bottom: 0 !important;
  }
}
.hint {
  margin-bottom: 72px;
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 16px;
  color: #828894;
  line-height: 24px;
}

.tag-drawer {
  position: relative;
  padding-bottom: 60px;
  width: 100%;
  display: flex;
  flex-direction: column;
  height: 100%;

  .content {
    padding: 0 32px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    flex-grow: 1;

    .btns {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 14px;

      .left {
        font-weight: 400;
        font-size: 16px;
        color: #383d47;
        line-height: 24px;

        span {
          color: #2d5cfd;
        }
      }

      .right {
        display: flex;
      }
    }

    .tags {
      margin: 14px 0;
      flex-grow: 1;
      max-height: 620px;
      overflow-y: scroll;

      &::-webkit-scrollbar {
        width: 5px;
        height: 7px;
        display: none;
      }

      &::-webkit-scrollbar-thumb {
        border-radius: 5px;
        box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        background: #535353;
      }

      &::-webkit-scrollbar-track {
        border-radius: 5px;
        background: #fff;
        cursor: pointer;
      }

      .item {
        width: 100%;
        height: 40px;
        display: flex;
        align-items: center;
        padding: 0 12px;
        box-sizing: border-box;
        border-radius: 4px;
        margin-bottom: 5px;
        cursor: pointer;
      }

      .item:hover {
        background: #e8edfd;
      }
    }
  }

  .footer {
    position: absolute;
    bottom: 0;
    width: 100%;
    text-align: right;
    padding: 10px;
    background-color: #fff;
    // box-shadow: 0 -1px 2px rgba(0, 0, 0, 0.1);
  }
}
.my-drawer {
  ::v-deep .el-drawer__container .el-drawer__header {
    margin-bottom: 4px;
  }
}
</style>
