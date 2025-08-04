<template>
  <div class="Ec-x6-icon">
      <el-drawer
          title=""
          :visible.sync="drawerVisible"
          :modal="false"
          :modal-append-to-body="false"
          :direction="direction"
          v-if="drawerVisible"
          size="500px"
          style="
              position: absolute;
              width: 500px;
              box-sizing: border-box;
              right: 0;
              left: inherit;
          "
          :show-close="false"
          :before-close="handleClose"
      >
          <div
              style="
                  display: flex;
                  align-items: center;
                  justify-content: space-between;
              "
          >
              <HeadTool
                  v-if="drawerVisible"
                  :label="sourceData.label"
                  :imgWidth="24"
                  :imgHeight="24"
                  @remove="handleRemove"
                  @copy="copyHandler"
                  @input="inputHandler"
                  @testNodes="openRunDrawer"
                  imgSuffix="workflow-shujuku"
              />
              <div
                  style="
                      width: 1px;
                      height: 20px;
                      background: #d3d9e6;
                      margin: 0 12px 12px;
                  "
              ></div>
              <iconpark-icon
                  name="close-line"
                  size="18"
                  color="#828894"
                  @click="closeDrawer"
                  style="margin-bottom: 10px; cursor: pointer"
              ></iconpark-icon>
          </div>
          <div class="sub">
                  {{ $t("datasetDescription") }}
          </div>
          <div class="drawer-content">
              <el-collapse v-model="activeNames" >
                  <el-collapse-item name="0">
                      <template slot="title">
                          <div class="title-flex">
                              <span>{{ $t('input') }}</span>
                              <div class="icon-ctn">
                              <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'输入需要从数据集匹配的关键信息'" placement="top" :effect="'light'">
                                  <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                              </el-tooltip>
                              </div>
                          </div>
                      </template>
                    <inputList v-if="drawerVisible" :inputs="appForm.inputs"
                          :key="inputKey" @updateInputList="updateInputList" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData" :single="true" :defalutVal="'Query'" ></inputList>
                  </el-collapse-item>
                  <!-- <el-collapse-item name="1" :title="$t('knowledgeBaseType')">
                      <el-cascader v-model="appForm.selectList" :props="{ multiple: true}" :options="knowledgeBaseTypeList" style="width: 100%" @change="cascaderChange">

                        <template slot-scope="{ node, data }">
                          <span>{{ data.label }}</span>
                          <span v-if="data.tip"><span class="tip">{{ data.tip }}</span></span>
                        </template>

                      </el-cascader>
                  </el-collapse-item> -->
                  <el-collapse-item name="2">
                      <template #title>
                          <div class="title-flex">
                              <span>数据集</span>
                              <div class="icon-ctn">
                              <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'选择需要匹配的数据集范围，仅从选定的数据集中召回信息，可添加多个数据集'" placement="top" :effect="'light'">
                                  <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                              </el-tooltip>
                              </div>
                          </div>
                          <!-- <el-button
                              type="text"
                              icon="el-icon-s-operation"
                              style="color: rgb(56, 61, 71);margin: 0px 0px 0px 10px;position: absolute;right: 40px;"
                              @click.stop="openDialog('setBase')"
                              >{{ $t("parameterSettings") }}</el-button
                          > -->
                          <el-button
                              type="text"
                              icon="el-icon-plus"
                              style="
                                  color: #1c50fd;
                                  position: absolute;
                                  right: 10px;
                              "
                              @click.stop="openDialog"
                          ></el-button>
                      </template>
                      <div class="marginTop32">
                          <div>
                              <ul>
                                  <li
                                      class="base-li flex-center just"
                                      v-for="(
                                          item, index
                                      ) in appForm.collectIdList"
                                      :key="index"
                                  >
                                      <div class="li-name flex-center">
                                          <img
                                              src="@/assets/images/sjk-icon1.png" v-if="item.type==1"
                                          />
                                          <img
                                              src="@/assets/images/sjk-icon2.png" v-else
                                          />
                                          <span>{{
                                              filterDataset(item)
                                          }}</span>
                                      </div>
                                      <i
                                          class="el-icon-close"
                                          style="cursor: pointer"
                                          @click="deleteDatasetId(item)"
                                      ></i>
                                  </li>
                              </ul>
                          </div>
                      </div>
                  </el-collapse-item>
                  <el-collapse-item name="3" :title="$t('output')">
                      <div class="result-cont">
                          result<span>array[string]</span>
                      </div>
                  </el-collapse-item>
              </el-collapse>
          </div>
      </el-drawer>
      
      <datasetDialog
            v-if="addDatasetVisible"
            :dialogVisible="addDatasetVisible"
            :collectIdList.sync="appForm.collectIdList"
            @clickConfig="clickConfig"
            @clickConfigParams="clickConfigParams"
        ></datasetDialog>
  </div>
</template>

<script>
// mixins
import drawerMixins from "./drawerMixins";
import inputList from "./inputList";
import datasetDialog from "./selectDataset.vue"
import { getDataCollectInfoList } from "@/api/dataSj";


export default {
  props: ["panels", "params"],
  name: "DatasetDrawer",
  components: {
    inputList,
    datasetDialog,
  },
  mixins: [drawerMixins],
  data() {
      return {
          appForm: {
            inputs: [],
            collectIdList:[]
          },
          addDatasetVisible:false,
          datasetList: [],
      };
  },
  computed:{
    filterDataset(){
        return (data)=>{
            return this.datasetList.find(item=>item.collectId==data)?.name
        }
    },
  },
  mounted() {
      this.getDatasetList()
  },
  methods: {
      getDatasetList() {
      getDataCollectInfoList({
        pageNo: 1,
        pageSize:9999 ,
      }).then((res) => {
        if (res.code == "000000") {
          this.datasetList = res.data?.records
        } else {
          this.datasetList = [];
        }
      });
    },
      // 打开弹窗
      openDialog() {
          this.addDatasetVisible = true;
      },
     
      clickConfig() {
          this.addDatasetVisible = false;
        //   this.$EventBus.$emit("saveWorkflow");
      },
      // 弹窗回调带参数
      clickConfigParams(type, data) {
          if (type == "addBaseVisible") {
              // 新增知识库
              if (data.length > 0) {
                  this.knowledgeIdArr = this.appForm.knowledgeIds =
                      data || [];
              }
          } else if (type == "setBaseVisible") {
              this.setBaseVisible = false;
              // 知识库参数设置
              this.appForm.contentScore = data.contentScore;
              this.appForm.rangeContentScore = data.rangeContentScore;
              this.appForm.qaTitleScore = data.qaTitleScore;
              this.appForm.qaRangeTitleScore = data.qaRangeTitleScore;
              this.appForm.qaContentScore = data.qaContentScore;
              this.appForm.qaRangeContentScore = data.qaRangeContentScore;
              this.appForm.filterNum = data.filterNum;
              this.appForm.prepareNum = data.prepareNum;
              this.$EventBus.$emit("saveWorkflow");
          }
      },
      deleteDatasetId(data){
        const index=this.appForm.collectIdList.findIndex(item=>item==data)
        this.appForm.collectIdList.splice(index,1)
      }
     
  },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style scoped lang="scss">
.base-li {
  height: 40px;
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

      > img {
          width: 22px;
          height: 22px;
          color: #a4bffe;
          margin-right: 5px;
      }
  }
}
.input-list {
  display: flex;
  align-items: center;
  .input-item {
      width: 120px;
      .tip{
          font-size: 12px;
          color: #828894;
          height: 24px;
      }
  }
  .el-select,
  .el-input {
      margin-right: 10px;
  }
}
.tip{
color: #b1b1b1b1;
font-size: 12px;
}
.title-flex{
display: flex;
align-items: center;
height: 20px;

span{
  display: inline-block;
  height: 20px;
  line-height: 20px;
}

.icon-ctn{
  height: 16px;
  line-height: 19px;
  position: relative;

}
}
</style>
