<template>
  <div>
  <el-drawer
    title=""
    :visible.sync="dialogVisible"

    :modal-append-to-body="true"
    :close-on-click-modal="true"
    :append-to-body="true"
    :wrapper-closable="false"
    size="720px"
    :show-close="false"
    :withHeader="false"
    class="knowledgeDrawer"
  >
  <div class="knowledge-content">
    <!-- <div class="content-left">
      <div class="content-left-header">{{ $t('selectSensitiveWordLibrary') }}</div>
      <div style="margin-bottom: 24px">
        <el-input
          :placeholder="$t('searchTermLibraryName')"
          prefix-icon="el-icon-search"
          v-model="searchKeyWord"
          style="width: 100%"
          @input="handleSearch"
        >
        </el-input>
      </div>
      <div class="">
        <el-button
          type="primary"
          icon="el-icon-circle-plus"
          style="width: 100%"
          @click="addKnowledge"
          >创建词库</el-button
        >
      </div>
    </div> -->
    <div class="content-right">
      <div class="content-title-ctn">
        <div class="flex" style="gap: 20px;">
           <div class="content-title">{{ $t('selectSensitiveWordLibrary') }}</div>
            <el-checkbox v-model="showAdd" style="line-height: 32px;">{{ $t("onlyAdd") }}</el-checkbox>
        </div>
       
        <iconpark-icon name="close-line" size="24" @click.stop="cancelAss"></iconpark-icon>
      </div>
      <div class="content-right-header">
        <div class="flex-center" style="justify-content: space-between;width: 100%;">
          <div class="tabs">
            <div v-for="(tab, index) in tabsList" :key="index" class="tab-item" :class="{'active': tab.value === activeTab}" @click.stop="handleTabClick(tab.value)">{{ tab.name }}</div>
          </div>
          <div class="functions">
             <el-select v-model="sortFeild" style="width: 120px;" @change="handleSearch">
              <el-option
                v-for="item in sortList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
            <el-input
              :placeholder="$t('searchTermLibraryName')"
              v-model="searchKeyWord"
              style="width: 176px"
              @input="handleSearch"
            >
            <template #prefix>
              <iconpark-icon name="search-2-line" class="el-input__icon" size="16" color="#1d2129"></iconpark-icon>
            </template>
            </el-input>

            <div class="btn-add" @click="addKnowledge">
              <div class="icon-ctn">
                <iconpark-icon name="add-line" size="16" color="#ffffff"></iconpark-icon>
              </div>
              <div class="btn-word">{{ $t("added") }}</div>
            </div>
          </div>
         
          <!-- <el-checkbox v-model="showAdd">{{ $t("onlyAdd") }}</el-checkbox> -->
        </div>
        
      </div>
      <div class="content-right-body" v-loading="loading" v-if="konwlwdgeList.length">
        <div class="list-box">
          <ul v-if="!showAdd">
            <li
              class="base-li flex-center just"
              v-for="item in konwlwdgeList"
              :key="item.id"
              @click="editMenu(item)"
            >
              <div class="li-name flex-center" >
                <img src="@/assets/images/appManagement/mgck.svg" />
              </div>
              <div class="li-content">
                <div class="li-title">
                  {{ item.name }}
                  <!-- <div class="official" v-if="item.ownerType=='official'">
                    官方
                  </div> -->
                </div>
                <div class="li-introduce">{{ item.remark }}</div>
                <div class="li-content-bottom">
                  <!-- <span class="li-num">87个文件</span> -->
                  <span>{{sortFeild === 'update_time' ? '更新时间' : '创建时间'}}：{{ item.updateTime || item.createTime }}</span>
                </div>
              </div>
              <el-button
                class="delete-btn"
                type="danger"
                v-if="filterKnowledge(item)"
                @click.stop="detItem(item)"
                size="small"
                ><div class="btn-add"> <iconpark-icon name="delete-bin-4-line" color="#F53F3F" size="16"></iconpark-icon><span>{{ $t("remove") }}</span></div></el-button
              >
              <el-button
                v-else
                class="add-btn"
                type="primary"
                plain
                size="small"
                @click.stop="addItem(item)"
                ><div class="btn-add"> <iconpark-icon name="add-line" color="#1747E5" size="16"></iconpark-icon><span>添加</span></div></el-button
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
                <img src="@/assets/images/appManagement/mgck.svg" />
              </div>
              <div class="li-content">
                <div class="li-title">{{ filterSelectKnowledge(item) }}</div>
                <div class="li-introduce">{{ filterSelectKnowledgeDesc(item) }}</div>
                <div class="li-content-bottom">
                  <!-- <span class="li-num">87个文件</span> -->
                  <span>{{sortFeild === 'update_time' ? '更新时间' : '创建时间'}}：{{ filterSelectKnowledgeTime(item) }}</span>
                </div>
              </div>
              <el-button
               class="delete-btn"
                type="danger"
                @click="detSelectItem(item)"
                size="small"
                ><div class="btn-add"> <iconpark-icon name="delete-bin-4-line" color="#F53F3F" size="16"></iconpark-icon><span>{{ $t("remove") }}</span></div></el-button
              >
            </li>
          </ul>
        </div>
        <!-- <el-pagination
          style="margin-top: 20px; color: #272a31;text-align: right;"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNo"
          :page-sizes="[10, 30, 50, 100, 200]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :pager-count="5"
          background
        >
        </el-pagination> -->
      </div>
      <el-row v-loading="loading" style="height: 100%;" v-else>
        <div class="empty-data-ctn">
          <div class="empty-data">
            <img src="@/assets/images/empty_data.png" alt="">
            <p>暂无敏感词</p>
          </div>
        </div>
      </el-row>
    </div>

  </div>
    <!-- <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="setKnowledgeIds">{{
        $t("confirm")
      }}</el-button>
      <el-button @click="cancelAss">{{ $t("cancel") }}</el-button>
    </span> -->
    
  </el-drawer>
  <AddSensitiveLibrary
      v-if="addSensitiveDialogVisible"
      :dialogVisible="addSensitiveDialogVisible"
      @closeDialog="closeAddSensitiveLibraryDialog"
      :row="false"
      @submitDialog="submitSensitiveLibraryDialog"
    ></AddSensitiveLibrary>
    <!-- 编辑敏感词库 -->
     
    
  <editSensitiveLibrary
  :viewSensitiveDialogVisible.sync="viewSensitiveDialogVisible"
    :sensitiveRow="sensitiveRow"
    :sensitiveArr.sync="knowledgeIdArr"
    @closeSensitiveDialog="closeSensitiveDialog"
    @changeSensitive="setKnowledgeIds"
  ></editSensitiveLibrary>
   
    
  </div>
</template>
  
<script>
import { getInterceptWordHouseListAll } from "@/api/toolManager";
import AddSensitiveLibrary from "@/views/toolManager/dictionaryManage/components/addDialog";
import editSensitiveLibrary from "@/views/appManage/components/editSensitiveDialog.vue";
import { template } from "@antv/x6/lib/util/string/string";
export default {
  components: {
    AddSensitiveLibrary,
    editSensitiveLibrary
  },
  data() {
    return {
      pageNo: 1,
      pageSize: 10000,
      total: 0,
      konwlwdgeList: [], //知识库 分页
      loading: false,
      knowledgeIdArr: [],
      showAdd: false,
      searchKeyWord: "",
      konwlwdgeAllList: [], //知识库
      tabsList: [
        {
          name: "推荐",
          value: "",
        },
        {
          name: "我的",
          value: "user",
        },
      ],
      activeTab: '',
      sortFeild: "update_time",
      sortList: [
        {
          value: "update_time",
          label: "最近更新"
        },
        {
          value: "create_time",
          label: "创建时间"
        }
      ],
      addSensitiveDialogVisible: false,
      knowledgeIdArrOld: [],
      viewSensitiveDialogVisible:false,
      sensitiveRow:{}
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
    this.knowledgeAllList();
    this.knowledgeIdArr = this.configData || [];
    this.knowledgeIdArrOld = this.configData ? JSON.parse(JSON.stringify(this.configData)) : [];
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
    filterSelectKnowledgeDesc(item) {
      let findItem = this.konwlwdgeAllList.find(
        (items) => items.id == item
      );
      return findItem?.remark;
    },
    filterSelectKnowledgeTime(item) {
      let findItem = this.konwlwdgeAllList.find(
        (items) => items.id == item
      );
      return findItem?.updateTime || findItem?.createTime;
    },
    knowledgeList() {
      this.loading = true;
      const userInfo = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : null;
      const accountName = userInfo ? userInfo.accountName : '';
      getInterceptWordHouseListAll({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        keyword: this.searchKeyWord,
        applicationId: this.appConfigForm.applicationId,
        order: "create_time",
        sort: "desc",
        createUser: this.activeTab == 'user' ? accountName : '',
        ownerType:this.activeTab == 'user' ? 'personal' : 'official'
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeList = res.data?.records.filter(el => el.status == '1');
          this.total = res.data.totalRow;
          this.loading = false;
        } else {
          this.konwlwdgeList = [];
        }
      });
    },
    knowledgeAllList() {
      getInterceptWordHouseListAll({
        pageNo: 1,
        pageSize: 2000,
        applicationId: this.appConfigForm.applicationId,
        order: "create_time",
        sort: "desc",
        ownerType:"all"
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
      this.knowledgeIdArr.push(data.id);
      this.setKnowledgeIds();
    },
    // 移除知识库
    detItem(data) {
      const index = this.knowledgeIdArr.findIndex(
        (items) => items === data.id
      );
      if (index == -1) {
        return;
      }
      this.knowledgeIdArr.splice(index, 1);
      this.setKnowledgeIds();
    },
    // 移除知识库
    detSelectItem(data) {
      const index = this.knowledgeIdArr.findIndex(
        (items) => items === data
      );
      if (index == -1) {
        return;
      }
      this.knowledgeIdArr.splice(index, 1);
      this.setKnowledgeIds();
    },
    // 确认添加知识库
    setKnowledgeIds() {
      let selectList = this.konwlwdgeAllList.filter(el=> this.knowledgeIdArr.includes(el.id))
      this.$emit("clickConfigParams", "addSensitive", this.knowledgeIdArr, selectList);
    },
    // 取消添加
    cancelAss() {
      const flag = this.compareArrays(this.knowledgeIdArr, this.knowledgeIdArrOld);
      if(!flag) {
        this.$EventBus.$emit("changeApplicationStatus", true);
        this.$EventBus.$emit("saveApplication");
      }
      this.knowledgeIdArr = [];
      this.$emit("clickConfig", false);
    },
    handleTabClick(tab) {
      this.activeTab = tab;
      this.pageNo = 1;
      this.knowledgeList();
    },
    handleSearch() {
      this.pageNo = 1;
      this.knowledgeList();
    },
    addKnowledge() {
      this.addSensitiveDialogVisible = true;
    },
    closeAddSensitiveLibraryDialog() {
      this.addSensitiveDialogVisible = false;
    },
    submitSensitiveLibraryDialog() {
      this.addSensitiveDialogVisible = false;
      this.knowledgeList();
      this.knowledgeAllList();
      this.$emit('updateAll');
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
    },
    editMenu(data){
      console.log(data);
      
      this.sensitiveRow={...data}
      this.viewSensitiveDialogVisible=true
      console.log(this.sensitiveRow);
      
    },
    closeSensitiveDialog(){
      this.viewSensitiveDialogVisible=false
    }
  },
};
</script>
  
<style lang="scss" scoped>
.knowledgeDrawer {
  ::v-deep .el-drawer {
    background: #FFFFFF;
    box-shadow: 0px 1px 4px 4px rgba(0,0,0,0.05);
    border-radius: 12px 0px 0px 12px;
    .el-drawe__body {
      padding: 0px;
    }
  }
  .knowledge-content {
    height: 100%;
    width: 100%;
    display: flex;
    overflow: hidden;
    .content-left {
      width: 224px;
      background: #F7F8FA;
      padding: 32px 24px;
      height: 100%;
      box-sizing: border-box;
      font-family: MiSans, MiSans;
      .content-left-header {
        font-weight: 500;
        font-size: 20px;
        color: #494E57;
        line-height: 32px;
        margin-bottom: 24px;
      }
    }
    .content-right {
      flex: 1;
      height: 100%;
      padding: 28px 0px 24px;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      .content-title-ctn{
        height: 32px;
        display: flex;
        justify-content: space-between;
        padding: 0 32px;
        box-sizing: border-box;
        margin-bottom: 16px;

        .content-title{
          height: 32px;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 20px;
          color: #1D2129;
          line-height: 32px;
        }
      }

      .content-right-header {
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 32px;
        font-family: MiSans, MiSans;
        margin-bottom: 16px;
        cursor: pointer;
        .tabs {
          display: flex;
          font-weight: 400;
          font-size: 18px;
          color: #828894;
          line-height: 28px;
          .tab-item {
            padding: 6px 0px;
            margin-right: 16px;
            cursor: pointer;
            &.active {
              font-weight: 500;
              font-size: 18px;
              color: #603ECA;
              position: relative;
              &:after {
                content: "";
                display: block;
                // width: 20px;
                height: 3px;
                background: #603ECA;
                border-radius: 2px;
                position: absolute;
                margin-left: -10px;
                left: 50%;
                bottom: 0px;
              }
            }
          }
        }
        
        .functions{
          height: 40px;
          display: flex;
          align-items: center;
          gap: 12px;

          .btn-add{
            height: 40px;
            padding: 8px 20px;
            box-sizing: border-box;
            display: flex;
            gap: 8px;
            align-items: center;
            background: #1747E5;
            border-radius: 8px;

            .icon-ctn{
              height: 20px;
              width: 20px;
              display: flex;
              align-items: center;
              justify-content: center;
            }

            .btn-word{
              height: 20px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #FFFFFF;
              line-height: 20px;
            }
          }
        }
        ::v-deep .el-input__inner{
          border-radius: 8px;
        }
      }
      .content-right-body { 
        flex: 1;
        overflow: hidden;
        display: flex;
        flex-direction: column;
        .list-box {
          flex: 1;
          overflow-y: auto;
          padding: 0px 32px;

          .btn-add{
            display: flex;
            align-items: center;
            gap: 8px;

            span{
              display: inline-block;
              height: 16px;
              line-height: 16px;
            }
          }
        }
      }

      .empty-data-ctn{
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;

        .empty-data{
          width: 150px;

          img{
            width: 150px;
          }
          p{
            font-family: MiSans, MiSans;
            text-align: center;
            color: rgb(130, 136, 148);
          }
        }
      }
    }
  }
}

.base-li {
  background: #ffffff;
  border-radius: 2px;
  border: 1px solid #D5D8DE;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;

  .li-name {
    font-weight: 400;
    font-size: 14px;
    color: #383d47;
    text-align: left;
    font-style: normal;
    cursor: pointer;

    > img {
      width: 36px;
      height: 36px;
      border-radius: 2px;
      margin-right: 16px;
    }
  }
  .li-content {
    flex: 1;
    overflow: hidden;
    cursor: pointer;
    .li-title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-bottom: 4px;
      display: flex;
      gap: 12px;
      align-items: center;

      .official{
        width: 32px;
        height: 20px;
        background: #F2F3F5;
        border-radius: 2px;
        width: 32px;
        height: 20px;
        background: #F2F3F5;
        border-radius: 2px;
        text-align: center;
      }
    }
    .li-introduce {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #828894;
      line-height: 16px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-bottom: 8px;
    }
    .li-content-bottom {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #828894;
      line-height: 20px;
      > span {
        margin-right: 12px;
      }
      .li-num {
        display: inline-block;
        line-height: 20px;
        padding: 0 4px;
        background: #EBEEF2;
        border-radius: 2px;
        color: #494E57;
      }
    }
  }
  &:hover {
    background: #F2F4F7;
  }
}
::v-deep .el-button--primary.is-plain {
    color: #1747E5 !important;
    background: #fff !important;
    border-color: #1747E5 !important;
    border-radius: 8px;
}
::v-deep .el-button--danger {
    color: #F53F3F !important;
    background-color: #FFEBE8 !important;
    border: none !important;
    border-radius: 8px;
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
  