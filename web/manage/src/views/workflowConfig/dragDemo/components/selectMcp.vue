<template>
  <div>
  <el-drawer
    title=""
    :visible.sync="dialogVisible"

    :modal-append-to-body="true"
    :close-on-click-modal="true"
    :append-to-body="true"
    :wrapper-closable="false"
    size="1140px"
    :show-close="false"
    :withHeader="false"
    class="knowledgeDrawer"
  >
  <div class="knowledge-content">
    <div class="content-left">
      <div class="content-left-header">选择MCP服务</div>
      <div style="margin-bottom: 24px">
        <el-input
          placeholder="输入MCP服务名称"
          v-model="searchKeyWord"
          style="width: 100%"
          @input="handleSearch"
        >
        <template #prefix>
          <div class="search-icon">
            <iconpark-icon name="search-2-line" color="#1d2129" size="20"></iconpark-icon>
          </div>  
        </template>
        </el-input>
      </div>
      <!-- <div class="">
        <el-button
          type="primary"
          icon="el-icon-circle-plus"
          style="width: 100%"
          @click="addKnowledge"
          >创建词库</el-button
        >
      </div> -->
      <div class="classify-ctn">
        <div class="classify-title">{{ $t("classification") }}</div>
        <div class="classify-list-ctn">
          <div class="classify-list" v-for="(item,index) in labelLogoList" :key="index+'labelLogoList'"
          :style="{backgroundColor:index==labelIndex?'#E7ECFC':'transparent'}" @click="handleLabelClick(item,index)">
            <div class="classify-icon">
              <iconpark-icon :name="item.icon" :color="index==labelIndex?'#1747E5 ':'#1d2129'" size="16"></iconpark-icon>
            </div>
            <div class="classify-word" :style="{color:index==labelIndex?'#1747E5 ':'#1d2129'}">
              {{ item.name }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="content-right">
      <!-- <div class="content-title-ctn">
        <div class="flex" style="gap: 20px;">
           <div class="content-title">选择MCP服务</div>
            <el-checkbox v-model="showAdd" style="line-height: 32px;">{{ $t("onlyAdd") }}</el-checkbox>
        </div>
       
        <iconpark-icon name="close-line" size="24" @click.stop="cancelAss"></iconpark-icon>
      </div> -->
      <div class="content-right-header">
        <div class="flex-center" style="width: 100%;">
          <div class="tabs">
            <div v-for="(tab, index) in tabsList" :key="index" class="tab-item" :class="{'active': tab.value === activeTab}" @click.stop="handleTabClick(tab.value)">{{ tab.name }}</div>
          </div>
          <el-select v-model="sortFeild" style="width: 140px;margin-right: 16px;" @change="handleSearch">
            <el-option
              v-for="item in sortList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
          <!-- <el-checkbox v-model="showAdd">{{ $t("onlyAdd") }}</el-checkbox> -->
          <!-- <div class="functions">
            <el-input
              placeholder="输入MCP服务名称"
              v-model="searchKeyWord"
              style="width: 176px"
              @input="handleSearch"
            >
            <template #prefix>
              <iconpark-icon name="search-2-line" class="el-input__icon" size="16" color="#1d2129"></iconpark-icon>
            </template>
            </el-input>
          </div> -->
        </div>
        <!-- <iconpark-icon name="close-line" size="24" @click.stop="cancelAss"></iconpark-icon> -->
        <div class="mcp-close-ctn" @click.stop="close">
          <iconpark-icon name="close-line" size="20" color="#1d2129"></iconpark-icon>
        </div>
      </div>
      <div class="content-right-body" v-loading="loading" v-if="konwlwdgeList.length">
        <div class="list-box">
          <ul v-if="!showAdd">
            <!-- <li
              class="base-li flex-center just"
              v-for="item in konwlwdgeList"
              style="gap: 16px;"
              :key="item.id"
            >
              <div class="li-name flex-center" >
                <img src="@/assets/images/appManagement/mcp.png" />
                 <img :src="item.icon" alt="">
              </div>
              <div class="li-content">
                <div class="li-title">{{ item.mcpName }}</div>
                <div class="li-introduce">{{ item.description }}</div>
                <div class="li-content-bottom">
                  <span class="li-num">87个文件</span>
                  <span>更新时间：{{ item.updateTime || item.createTime }}</span>
                </div>
              </div>
              <el-button
                class="delete-btn"
                type="danger"
                v-if="filterKnowledge(item)"
                @click="detItem(item)"
                size="small"
                ><div class="btn-add"> <iconpark-icon name="delete-bin-4-line" color="#F53F3F" size="14"></iconpark-icon><span>{{ $t("remove") }}</span></div></el-button
              >
              <el-button
                v-else
                class="add-btn"
                type="primary"
                plain
                size="small"
                @click="addItem(item)"
                ><div class="btn-add"> <iconpark-icon name="add-line" color="#1747E5" size="16"></iconpark-icon><span>添加</span></div></el-button
              >
            </li> -->
            <li class="mcp-list-ctn" v-for="(item,index) in konwlwdgeList" :key="index+'mcpList'" :style="{gap:mcpToolList[index]?'8px':'0'}">

              <div class="mcp-list">
                <img :src="item.icon" alt="" class="mcp-list-icon">
                <div class="mcp-list-msg">
                  <div class="mcp-title-ctn">
                    <div class="mcp-title">{{ item.mcpName }}</div>
                    <div class="mcp-tool" v-if="item.mcpFunctionList&&item.mcpFunctionList.length">
                      <div class="mcp-tool-word">工具{{ item.mcpFunctionList.length?item.mcpFunctionList.length:"" }}</div>
                      <div class="mcp-tool-arrow" :style="{transform:mcpToolList[index]?'rotate(180deg)':'rotate(0deg)'}" @click="toggleTool(index)">
                        <iconpark-icon name="arrow-down-s-line" size="16" color="#86909C"></iconpark-icon>
                      </div>
                    </div>
                  </div>
                  <div class="mcp-intro">
                    {{ item.description?item.description:"暂无描述" }}
                  </div>
                </div>

                <div class="mcp-btns">
                  <div class="btns-public mcp-exit" v-if="filterKnowledge(item)" :style="{background:mcpHoverList[index]?'#ffebe8':'rgba(188,193,204,0.2)'}" 
                  @mouseenter="mcpIn(index)" @mouseleave="mcpOut(index)" @click="mcpHoverList[index]?detItem(item):''">
                    <!-- <div class="mcp-exit-icon" v-if="mcpHoverList[index]">
                      <iconpark-icon name="delete-bin-4-line" size="14" color="#D33A22"></iconpark-icon>
                    </div> -->
                    <div class="mcp-exit-word" :style="{color:mcpHoverList[index]?'#D33A22':'#86909C'}">
                      {{mcpHoverList[index]?"移除": "已添加" }}
                    </div>
                  </div>
                  <div class="btns-public mcp-add" v-else @click="addItem(item)">
                    <iconpark-icon name="add-line" size="20" color="#1d2129"></iconpark-icon>
                  </div>
                </div>
              </div>
              <div class="mcp-tool-ctn" :style="{'height': mcpToolList[index] ? mcpToolHeightList[index]+'px' : '0px'}" v-if="item.mcpFunctionList">
                <div class="mcp-tool-h" ref="toolListRef">
                  <div class="mcp-tool-list" v-for="(ele,index) in item.mcpFunctionList" :key="index+'toolList'">
                    <div class="mcp-tool-top">
                      <div class="mcp-tool-icon">
                        <iconpark-icon name="tools-line" size="14" color="#1d2129"></iconpark-icon>
                      </div>
                      <div class="mcp-tool-name">{{ ele.functionName }}</div>
                    </div>

                    <div class="mcp-tool-intro">
                      {{ ele.description }}
                    </div>
                  </div>
                </div>
                
              </div>
            </li>
          </ul>
          <ul v-if="showAdd">
            <li
              class="base-li flex-center just"
              v-for="item in knowledgeIdArr"
              style="gap: 16px;"
              :key="item"
            >
              <div class="li-name flex-center">
                <img src="@/assets/images/appManagement/zsk.svg" />
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
                icon="el-icon-remove-outline"
                type="danger"
                @click="detSelectItem(item)"
                size="small"
                >{{ $t("remove") }}</el-button
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
            <p>暂无MCP服务</p>
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
  <!-- <AddSensitiveLibrary
      v-if="addSensitiveDialogVisible"
      :dialogVisible="addSensitiveDialogVisible"
      @closeDialog="closeAddSensitiveLibraryDialog"
      :row="false"
      @submitDialog="submitSensitiveLibraryDialog"
    ></AddSensitiveLibrary> -->
  </div>
</template>
  
<script>
// import { getInterceptWordHouseListAll } from "@/api/toolManager";
import { getMcpServerList } from "@/api/mcp";
import { transform } from "lodash";
// import AddSensitiveLibrary from "@/views/toolManager/dictionaryManage/components/addDialog";
export default {
  // components: {
  //   AddSensitiveLibrary
  // },
  data() {
    return {
      pageNo: 1,
      pageSize: 1000,
      total: 0,
      konwlwdgeList: [], //知识库 分页
      loading: false,
      knowledgeIdArr: [],
      showAdd: false,
      searchKeyWord: "",
      konwlwdgeAllList: [], //知识库
      tabsList: [
        {
          name: "官方",
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
      label:"",
      labelIndex:0,
      labelList:[],
      showAdd:false,
      labelLogoList:[
        {
          name:"全部",
          icon:"apps-fill"
        },
        {
          name:"地图服务",
          icon:"compass-3-line"
        },
        {
          name:"网页搜索",
          icon:"global-line"
        },
        {
          name:"内容生产",
          icon:"palette-line"
        },
        {
          name:"社交通讯",
          icon:"chat-smile-2-line"
        },
        {
          name:"数据库",
          icon:"database-2-line"
        },
        {
          name:"效率工具",
          icon:"keyboard-box-line"
        },
        {
          name:"其他",
          icon:"box-1-line"
        }
      ],
      mcpToolList:[],
      mcpHoverList:[],
      mcpToolHeightList:[]
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    configData: Array,
    appConfigForm: Object,
    dataList:Array
  },
  mounted() {
    this.initFetch()
    // this.knowledgeList();
    // this.knowledgeAllList();
    this.knowledgeIdArr = this.configData || [];
	console.log('this.knowledgeIdArr',this.knowledgeIdArr)
    this.knowledgeIdArrOld = this.configData ? JSON.parse(JSON.stringify(this.configData)) : [];
  },
  methods: {
    // 显示关联知识库
    filterKnowledge(item) {
      if (this.knowledgeIdArr) {
        return this.knowledgeIdArr.includes(item.mcpId);
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
      // const userInfo = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : null;
      // const accountName = userInfo ? userInfo.accountName : '';
      getMcpServerList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        mcpName: this.searchKeyWord,
        ownerType:this.activeTab == 'user' ? 'personal' : 'official',
        type:this.label=='全部'?"":this.label,
        sort:"desc",
        order:this.sortFeild
        // applicationId: this.appConfigForm.applicationId,
        // order: "create_time",
        // sort: "desc",
        // createUser: this.activeTab == 'user' ? accountName : ''
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeList = res.data?.records || []
          this.total = res.data.totalRow;
          this.loading = false;
          for(let i=0;i<this.total;i++){
             this.mcpToolList.push(false);
             this.mcpHoverList.push(false)
          }
        } else {
          this.konwlwdgeList = [];
        }
      });
    },
    knowledgeAllList() {
      getMcpServerList({
        pageNo: 1,
        pageSize: 2000,
        ownerType:"all",
        type:this.label=='全部'?"":this.label
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeAllList = res.data.records;
        } else {
          this.konwlwdgeAllList = [];
        }
      });
    },
    initFetch(){
      this.loading=true;
      Promise.all([getMcpServerList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        mcpName: this.searchKeyWord,
        ownerType:this.activeTab == 'user' ? 'personal' : 'official',
        type:this.label=='全部'?"":this.label,
        sort:"desc",
        order:this.sortFeild
        // applicationId: this.appConfigForm.applicationId,
        // order: "create_time",
        // sort: "desc",
        // createUser: this.activeTab == 'user' ? accountName : ''
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeList = res.data?.records || []
          this.total = res.data.totalRow;
          for(let i=0;i<this.total;i++){
             this.mcpToolList.push(false);
             this.mcpHoverList.push(false)
          }
        } else {
          this.konwlwdgeList = [];
        }
      }),getMcpServerList({
        pageNo: 1,
        pageSize: 2000,
        ownerType:"all",
        type:this.label=='全部'?"":this.label
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeAllList = res.data.records;
        } else {
          this.konwlwdgeAllList = [];
        }
      })]).then(res=>{
        this.loading=false
      })
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
      this.knowledgeIdArr.push(data.mcpId);
      this.setKnowledgeIds();
    },
    // 移除知识库
    detItem(data) {
      const index = this.knowledgeIdArr.findIndex(
        (items) => items === data.mcpId
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
      console.log(this.konwlwdgeAllList,this.knowledgeIdArr);
      
      let selectList = this.konwlwdgeAllList.filter(el=> this.knowledgeIdArr.includes(el.mcpId))
      console.log(selectList);
      this.$emit("update:configData",this.knowledgeIdArr);
      this.$emit("update:dataList",selectList);
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
    handleLabelClick(item,index){
      this.label=item.name
      this.labelIndex=index
      this.knowledgeList()
    },
    close(){
      console.log(666);
      this.$emit("update:dialogVisible",false)
    },
    toggleTool(index){
      this.$set(this.mcpToolList,index,!this.mcpToolList[index])
      this.$set(this.mcpToolHeightList,index,this.$refs.toolListRef[index].clientHeight)
    },
    mcpIn(index){
      this.$set(this.mcpHoverList,index,true)
    },
    mcpOut(index){
      this.$set(this.mcpHoverList,index,false)
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
      .el-input__inner{
        border-radius: 8px;
      }

      .search-icon{
        width: 20px;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      .classify-ctn{
        margin-top: 16px;
        width: 100%;

        .classify-title{
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 14px;
          color: #86909C;
          line-height: 20px;
        }

        .classify-list-ctn{
          width: 100%;
          display: flex;
          flex-direction: column;
          gap: 8px;
          margin-top: 12px;

          .classify-list{
            width: 100%;
            height: 40px;
            padding: 10px;
            box-sizing: border-box;
            display: flex;
            align-items: center;
            gap: 8px;
            cursor: pointer;
            border-radius: 4px;

            .classify-icon{
              width: 20px;
              height: 20px;
              display: flex;
              align-items: center;
              justify-content: center;
            }

            .classify-word{
              height: 20px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #1D2129;
              line-height: 20px;
            }
          }

        }
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
        position: relative;
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

        .mcp-close-ctn{
          width: 32px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
          position: absolute;
          top: 50%;
          transform: translateY(-50%);
          right: 32px;
          cursor: pointer;
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

          .mcp-list-ctn{
            width: 100%;
            padding: 16px;
            background: #FFFFFF;
            border-radius: 8px;
            border: 1px solid #E7E7E7;
            box-sizing: border-box;
            margin-bottom: 12px;
            display: flex;
            flex-direction: column;
            gap: 12px;

            .mcp-list{
              width: 100%;
              height: 44px;
              display: flex;
              align-items: center;
              gap: 16px;
              position: relative;

              .mcp-list-icon{
                width: 32px;
                height: 32px;
                border-radius: 2px;
              }

              .mcp-list-msg{
                height: 44px;
                display: flex;
                flex-direction: column;
                gap: 4px;

                .mcp-title-ctn{
                  height: 24px;
                  display: flex;
                  gap: 16px;

                  .mcp-title{
                    height: 24px;
                    font-family: MiSans, MiSans;
                    font-weight: 500;
                    font-size: 14px;
                    color: #1D2129;
                    line-height: 24px;
                  }

                  .mcp-tool{
                    height: 24px;
                    display: flex;
                    align-items: center;
                    gap: 8px;

                    .mcp-tool-word{
                      height: 16px;
                      font-family: MiSans, MiSans;
                      font-weight: 400;
                      font-size: 12px;
                      color: #86909C;
                      line-height: 16px;
                    }

                    .mcp-tool-arrow{
                      width: 20px;
                      height: 20px;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      cursor: pointer;
                      transition: .3s;
                    }
                  }
                }

                .mcp-intro{
                  height: 16px;
                  font-family: MiSans, MiSans;
                  font-weight: 400;
                  font-size: 12px;
                  color: #86909C;
                  line-height: 16px;
                  max-width: 500px;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                }
              }

              .mcp-btns{
                position: absolute;
                right: 16px;
                top: 50%;
                transform: translateY(-50%);
                
                .btns-public{
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  cursor: pointer;
                  border-radius: 4px;
                }

                .mcp-add{
                  width: 32px;
                  height: 32px;
                  background: rgba(188,193,204,0.2);
                }

                .mcp-exit{
                  width: 70px;
                  height: 32px;
                  padding: 6px 12px;
                  box-sizing: border-box;
                  display: flex;
                  gap: 8px;
                  background: rgba(188,193,204,0.2);
                  

                  .mcp-exit-word{
                    height: 20px;
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 14px;
                    color: #86909C;
                    line-height: 20px;
                  }

                  .mcp-exit-icon{
                    height: 20px;
                    width: 20px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                  }
                }
              }
            }

            .mcp-tool-ctn{
              width: 100%;
              overflow: hidden;
              transition: .3s;
              will-change: height;

              .mcp-tool-h{
                width: 100%;
                display: flex;
                flex-direction: column;
              }

              .mcp-tool-list{
                width: 100%;
                padding: 12px;
                background: #FFFFFF;
                border-radius: 8px;
                border: 1px solid #E7E7E7;
                box-sizing: border-box;
                display: flex;
                flex-direction: column;
                gap: 8px;
                margin-bottom: 8px;

                .mcp-tool-top{
                  width: 100%;
                  height: 20px;
                  display: flex;
                  align-items: center;
                  gap: 8px;

                  .mcp-tool-icon{
                    width: 20px;
                    height: 20px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    background: #F2F3F5;
                    border-radius: 2px;
                  }

                  .mcp-tool-name{
                    height: 20px;
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 14px;
                    color: #1D2129;
                    line-height: 20px;
                  }
                }

                .mcp-tool-intro{
                  width: 100%;
                  height: 16px;
                  font-family: MiSans, MiSans;
                  font-weight: 400;
                  font-size: 12px;
                  color: #86909C;
                  line-height: 16px;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                }
              }
            }

          }

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
  