<template>
  <div>

  
  <el-drawer
    title=""
    :visible.sync="dialogVisible"
    :modal="true"
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
      <div class="content-left-header">{{ $t('associateKnowledgeBase') }}</div>
      <div style="margin-bottom: 24px">
        <el-input
          :placeholder="$t('searchKnowledgeBaseName')"
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
          >创建知识库</el-button
        >
      </div>
    </div> -->
    <div class="content-right">
      <div class="content-title-ctn">
        <div class="flex" style="gap: 20px;">
           <div class="content-title">{{ $t('associateKnowledgeBase') }}</div>
           <el-checkbox v-model="showAdd" @change="checkboxChange" style="line-height: 32px;">{{ $t("onlyAdd") }}</el-checkbox>
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
              :placeholder="$t('searchKnowledgeBaseName')"
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
          
          <!-- <el-checkbox v-model="showAdd" @change="checkboxChange">{{ $t("onlyAdd") }}</el-checkbox> -->
        </div>
        <!-- <iconpark-icon name="close-line" size="24" @click.stop="cancelAss"></iconpark-icon> -->
        
      </div>
      <div class="content-right-body" v-loading="loading" v-if="konwlwdgeList.length">
        <div class="list-box">
          <ul v-if="!showAdd" style="max-height: 700px;">
            <li
              class="base-li flex-center just"
              v-for="item in konwlwdgeList"
              :key="item.knowledgeId + item.id"
              @click="konwlwdgeClick(item)"
            >
              <div class="li-name flex-center" >
                <img src="@/assets/images/appManagement/zsk.svg" />
              </div>
              <div class="li-content" >
                <div class="li-title">
                  {{ item.knowledgeName }}
                  <div class="theofficial" v-if="item.ownerType=='official'">
                    官方
                  </div>
                  <div class="official" v-if="!item.isMe && item.granted">
                    授权
                  </div>
                </div>
                <div class="li-introduce">{{ item.introduce }}</div>
                <div class="li-content-bottom">
                  <!-- <span class="li-num">87个文件</span> -->
                  <span>{{sortFeild === 'update_time' ? '更新时间' : '创建时间'}}：{{ item.updateTime || item.createTime }}</span>
                </div>
              </div>
			  <div v-if="isRadio">
				  <el-button
				    class="delete-btn"
				    v-if="filterKnowledge(item)"
				    type="danger"
				    @click.stop="detItem(item)"
				    size="small"
				    ><div class="btn-add"> <iconpark-icon name="delete-bin-4-line" color="#F53F3F" size="14"></iconpark-icon><span>{{ $t("remove") }}</span></div></el-button
				  >
				  <el-button
				    v-else
				    type="primary"
				    size="small"
            plain
				    @click.stop="addRadioItem(item)"
				    > <div class="btn-add"> <iconpark-icon name="add-line" color="#1747E5" size="16"></iconpark-icon><span>添加</span></div></el-button
				  >
			  </div>
			  <div v-else>
			  		<el-button
			  		  class="delete-btn"
			  		  v-if="filterKnowledge(item)"
			  		  type="danger"
			  		  @click.stop="detItem(item)"
			  		  size="small"
			  		  ><div class="btn-add"> <iconpark-icon name="delete-bin-4-line" color="#F53F3F" size="14"></iconpark-icon><span>{{ $t("remove") }}</span></div></el-button
			  		>
			  		<el-button
			  		  v-else
			  		  type="primary"
			  		  size="small"
              plain
			  		  @click.stop="addItem(item)"
			  		  ><div class="btn-add"> <iconpark-icon name="add-line" color="#1747E5" size="16"></iconpark-icon><span>添加</span></div></el-button
			  		>		  
			  </div>
              
            </li>
          </ul>
          <ul v-if="showAdd" style="max-height: 700px;">
            <li
              class="base-li flex-center just"
              v-for="(item,index) in showAddList"
              :key="index+'ll'"
            >
              <div class="li-name flex-center">
                <img src="@/assets/images/appManagement/zsk.svg" />
              </div>
              <div class="li-content">
                <div class="li-title">
                  {{ filterSelectKnowledge(item) }}
                  <div class="theofficial" v-if="item.ownerType=='official'">
                    官方
                  </div>
                  <div class="official" v-if="!item.isMe && item.granted">
                    授权
                  </div>
                </div>
                <div class="li-introduce">{{ filterSelectKnowledgeDesc(item) }}</div>
                <div class="li-content-bottom">
                  <!-- <span class="li-num">87个文件</span> -->
                  <span>{{sortFeild === 'update_time' ? '更新时间' : '创建时间'}}：{{ filterSelectKnowledgeTime(item) }}</span>
                </div>
              </div>
              <el-button
               class="delete-btn"
                type="danger"
                @click.stop="detSelectItem(item)"
                size="small"
                ><div class="btn-add"> <iconpark-icon name="delete-bin-4-line" color="#F53F3F" size="14"></iconpark-icon><span>{{ $t("remove") }}</span></div></el-button
              >
            </li>
          </ul>
        </div>
        <!-- <el-pagination
          style="margin-top: 20px; color: #272a31;text-align: right;height: 32px;"
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
            <p>暂无知识库</p>
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
  <CreateKnowledge v-if="createKnowledgeVisible" :createKbmVisible="createKnowledgeVisible" @submitDialog="submitDialog" @cancelDialog="cancelDialog"></CreateKnowledge>
</div>
</template>
  
<script>
import { getKnowledgeInfoList } from "@/api/index";
import CreateKnowledge from "./knowledgeCreate.vue";
import { template } from "@antv/x6/lib/util/string/string";
export default {

  components: {
    CreateKnowledge
  },
  computed:{
    // isAddedShow(){
    //   return (data)=>{
    //     let index=this.konwlwdgeList.find(item=>{
    //       item.knowledgeId==data.id
    //     })
    //     return index>=0?true:false
    //   }
    // }
  },
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
      createKnowledgeVisible: false,
      knowledgeIdArrOld: [],
      newKnowledgeIdArr: [],
      showAddList:[]
    };
  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
	isRadio: {
	  type: Boolean,
	  default: false,
	},
    configData: Array,
  },
  mounted() {
    this.knowledgeList();
    this.knowledgeIdArr = this.configData || [];
    this.newKnowledgeIdArr = JSON.parse(JSON.stringify(this.knowledgeIdArr));
    this.knowledgeIdArrOld = JSON.parse(JSON.stringify(this.knowledgeIdArr));
  },
  watch:{
    configData:{
      deep:true,
      handler(cur,pre){
        this.knowledgeIdArr = this.configData || [];
        this.newKnowledgeIdArr = JSON.parse(JSON.stringify(this.configData));
        this.knowledgeIdArrOld = JSON.parse(JSON.stringify(this.configData));
      }
    } 
  },
  methods: {
    // 显示已添加
    checkboxChange(v) {
      if(v) {
        this.knowledgeIdArr =  this.compareList(this.konwlwdgeList, this.newKnowledgeIdArr);
        this.total = this.knowledgeIdArr.length;
      } else {
        this.knowledgeList();
      }
    },
    // 显示关联知识库
    filterKnowledge(item) {
		 console.log('conso',this.knowledgeIdArr)
      if (this.knowledgeIdArr) {
        const list = this.knowledgeIdArr.map(ele => ele.knowledgeId)
        return list.includes(item.knowledgeId);
      } else {
        return false;
      }
    },
    // 显示关联知识库
    filterSelectKnowledge(item) {
      let findItem = this.$store.state?.workflow?.knowledgeList.find(
        (items) => items.knowledgeId == item.knowledgeId
      );
      return findItem?.knowledgeName;
    },
    filterSelectKnowledgeDesc(item) {
      let findItem = this.$store.state?.workflow?.knowledgeList.find(
        (items) => items.knowledgeId == item.knowledgeId
      );
      return findItem?.introduce;
    },
    filterSelectKnowledgeTime(item) {
      let findItem = this.$store.state?.workflow?.knowledgeList.find(
        (items) => items.knowledgeId == item.knowledgeId
      );
      return findItem?.updateTime || findItem?.createTime;
    },
    isAddedShow(){
      this.showAddList=[]
      let ids=this.knowledgeIdArr.map(item=>item.knowledgeId)
      this.konwlwdgeList.map(item=>{
        if(ids.includes(item.knowledgeId)){
          this.showAddList.push(item)
        }
      })
    },
    knowledgeList() {
      this.loading = true;
      const userInfo = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : null;
      const accountName = userInfo ? userInfo.accountName : '';
      getKnowledgeInfoList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        knowledgeName: this.searchKeyWord,
        order: this.sortFeild,
        sort: 'desc',
        // createUser: this.activeTab == 'user' ? accountName : '',
        status: '是',
        ownerType:this.activeTab == 'user' ? "personalGrant" : 'official'
      }).then((res) => {
        if (res.code == "000000") {
          this.konwlwdgeList = res.data?.records;
          this.isAddedShow()
          this.total = res.data.totalRow;
          this.loading = false;
        } else {
          this.konwlwdgeList = [];
        }
        if(this.showAdd || this.newKnowledgeIdArr?.length) {
          this.knowledgeIdArr =  this.compareList(this.konwlwdgeList, this.newKnowledgeIdArr);
          this.isAddedShow()
          this.$emit('updateKnowledgeIds', this.newKnowledgeIdArr)
        }
        this.total = this.showAdd ? this.knowledgeIdArr.length : res.data.totalRow;
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
    handleSearch() {
      this.pageNo = 1;
      this.knowledgeList();
    },
	// 添加知识库单选
	addRadioItem(data) {
      // this.knowledgeIdArr.push(data.knowledgeId);
	  this.knowledgeIdArr = []
	  this.newKnowledgeIdArr = []
     this.knowledgeIdArr.push(data);
     this.newKnowledgeIdArr.push(data);
      this.setKnowledgeIds();
    },
    // 添加知识库
    addItem(data) {
      // this.knowledgeIdArr.push(data.knowledgeId);
      this.knowledgeIdArr.push(data);
      this.newKnowledgeIdArr.push(data);
      this.isAddedShow()
      this.setKnowledgeIds();
    },
    // 移除知识库
    detItem(data) {
      const index = this.knowledgeIdArr.findIndex(
        (items) => items.knowledgeId === data.knowledgeId
      );
      if (index == -1) {
        return;
      }
      this.knowledgeIdArr.splice(index, 1);
      this.newKnowledgeIdArr = this.newKnowledgeIdArr.filter(item => item.knowledgeId != data.knowledgeId)
      this.isAddedShow()
      this.$emit('updateKnowledgeIds', this.newKnowledgeIdArr)
      this.setKnowledgeIds();
    },
    // 移除知识库
    detSelectItem(data) {
      const index = this.knowledgeIdArr.findIndex(
        (items) => items.knowledgeId === data.knowledgeId
      );
      if (index == -1) {
        return;
      }
      this.knowledgeIdArr.splice(index, 1);
      this.newKnowledgeIdArr = this.newKnowledgeIdArr.filter(item => item.knowledgeId != data.knowledgeId)
      this.total = this.knowledgeIdArr.length;
      this.isAddedShow()
      this.$emit('updateKnowledgeIds', this.newKnowledgeIdArr)
      this.setKnowledgeIds();
    },
    // 确认添加知识库
    setKnowledgeIds() {
      this.$emit("clickConfigParams", "addBaseVisible", this.newKnowledgeIdArr);
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
    konwlwdgeClick(item) {
      // console.log(item,"..");
      
      this.$store.commit('setKonwledge', null)
      
      if(this.filterKnowledge(item)){
        this.$store.commit('setIsAddedToApp', true);
      }else {
        this.$store.commit('setIsAddedToApp', false);
      }
      
      this.$store.commit('setKonwledge', item)
      console.log("66");
      
    },
    handleTabClick(tab) {
      this.activeTab = tab;
      this.knowledgeList();
    },
    addKnowledge() {
      this.createKnowledgeVisible = true;
    },
    submitDialog() {
      this.createKnowledgeVisible = false;
      this.knowledgeList();
      this.$emit('updateAll')
    },
    cancelDialog() {
      this.createKnowledgeVisible = false;
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
    compareList(array1, array2) {
      const filteredArray = array1.filter(item1 =>
        array2.some(item2 => item2.knowledgeId === item1.knowledgeId)
      );
      return filteredArray;
    },
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
        display: flex;
        flex-direction: column;
        overflow: auto;
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
        border-radius: 2px;
        width: 32px;
        height: 20px;
        background: #F2F3F5;
        border-radius: 2px;
        text-align: center;
        color: rgb(158,166,175);
        font-size: 12px;
        font-family: MiSans, MiSans;
        font-weight: 400;
      }
      .theofficial{
        width: 32px;
        height: 20px;
        background: linear-gradient( 270deg, #8E65FF33 0%, #1747E533 100%);
        border-radius: 2px;
        width: 32px;
        height: 20px;
        color: #1747E5;
        border-radius: 2px;
        text-align: center;
        font-size: 12px;
        font-family: MiSans, MiSans;
        font-weight: 400;
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
  