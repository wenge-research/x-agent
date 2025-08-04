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
                    {{ $t("sqlDescription") }}
            </div>
            <div class="drawer-content">
                <el-collapse v-model="activeNames" >
                    <el-collapse-item name="1">
                    <template slot="title">
                        <div class="title-flex">
                            <span>{{ $t('input') }}</span>
                            <div class="icon-ctn">
                            <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'添加可在SQL中直接引用的输入变量'" placement="top" :effect="'light'">
                                <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                            </el-tooltip>
                            </div>
                        </div>
                    </template>
                     <inputList v-if="drawerVisible" :inputs="appForm.inputs" @updateInputList="updateInputList"
                       :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList>
                  </el-collapse-item>
                    <el-collapse-item name="2">
                        <template #title>
                            数据库
                           
                            <el-button
                                type="text"
                                icon="el-icon-plus"
                                style="
                                    color: #1c50fd;
                                    position: absolute;
                                    right: 10px;
                                "
                                @click.stop="openDialog('addBase')"
                            ></el-button>
                        </template>
                        <div class="marginTop32">
                            <div>
                                <ul>
                                    <li
                                        class="base-li flex-center just"
                                        v-for="(
                                            item, index
                                        ) in appForm.sqlIds"
                                        :key="index"
                                       
                                    >
                                        <div class="li-name flex-center">
                                            <img
                                                src="@/assets/images/appManagement/zsk.svg"
                                            />
                                            <span>{{
                                                item.tableName
                                            }}</span>
                                        </div>
                                        <i
                                            class="el-icon-close"
                                            style="cursor: pointer"
                                            @click="deleteKnowledgeId(item,index)"
                                        ></i>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </el-collapse-item>
					<el-collapse-item name="3">
					    <template slot="title">
                            <div class="title-flex">
                                <span>SQL</span>
                                <div class="icon-ctn">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'要执行的SQL语句，可以直接使用输入参数中的变量'" placement="top" :effect="'light'">
                                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                                </el-tooltip>
                                </div>
                            </div>
                        </template>
					    <div class="marginTop32">
					        <div>
					            <el-input
					              v-model="appForm.systemPrompt"
					              type="textarea"
					              :rows="3"
					             
					            ></el-input>
					        </div>
					    </div>
					</el-collapse-item>
                    <el-collapse-item name="4" :title="$t('output')">
                        <div class="result-cont">
                            rowNum<span>integer</span>
                        </div>
                        <div class="result-cont">
                            outputList<span>array[object]</span>
                        </div>
                        
                    </el-collapse-item>
                </el-collapse>
            </div>
        </el-drawer>
        
        <!-- 新增知识库 -->
        <sqlDialog
            v-if="addBaseVisible"
            :dialogVisible="addBaseVisible"
			:isRadio="true"
            :configData="appForm.sqlIds"
            @clickConfig="clickConfig"
            @clickConfigParams="clickConfigParams"
        ></sqlDialog>
    </div>
</template>

<script>
// mixins
import drawerMixins from "./drawerMixins";
import sqlDialog from "./selectSql.vue";
import inputList from "./inputList";



export default {
    props: ["panels", "params"],
    name: "CustomDrawer",
    components: {
      inputList,
      sqlDialog,
    },
    mixins: [drawerMixins],
    data() {
        return {
          
            appForm: {
                contentScore: 1.49,
                rangeContentScore: 0.88,
                qaTitleScore: 1.88,
                qaRangeTitleScore: 0.91,
                qaContentScore: 1.88,
                qaRangeContentScore: 0.88,
                filterNum: 10,
                inputType: 1,
                cusInput: "",
                type: '',
                prepareNum: 60,
                knowledgeIds: [],
                selectList: [],
                inputs: [],
            },
            startAppForm: {
              contentScore: 1.49,
                rangeContentScore: 0.88,
                qaTitleScore: 1.88,
                qaRangeTitleScore: 0.91,
                qaContentScore: 1.88,
                qaRangeContentScore: 0.88,
                filterNum: 10,
                inputType: 1,
                cusInput: "",
                type: 'findQaTitle,findQaContent,builtIn,knowledgeContent,documentLibrary,WebsiteStrategy,structuredStrategy,multiMediaLibrary',
                selectList:[["","findQaTitle"],["","findQaContent"],["","builtIn"],["","knowledgeContent"],["documentLibrary"],["WebsiteStrategy"],["structuredStrategy"],["multiMediaLibrary"]],
                prepareNum: 60,
                knowledgeIds: [],
				sqlIds:[],
                inputs: [],
            },
            konwlwdgeAllList: [],
            
            knowledgeId: [],
            setBaseVisible: false, // 设置知识库参数
            addBaseVisible: false, // 新增知识库
            konwlwdgeList: [], //知识库数据
            knowledgeIdArr: [],
        };
    },
    mounted() {
        this.$EventBus.$on('addKnowledgeToApp', (val) => {
            this.addKnowledgeToApp(val)
        })
        this.$EventBus.$on('deleteKnowledgeToApp', (val) => {
            this.deleteKnowledgeToApp(val)
        })
    },
    methods: {
       
        // 打开弹窗
        openDialog(type) {
			console.log('type',type)
            if (type == "setBase") {
                this.setBaseVisible = true;
            } else if (type == "addBase") {
                this.knowledgeIdArr = this.appForm.sqlIds;
                this.addBaseVisible = true;
            }
        },
       
        // 删除知识库
        deleteKnowledgeId(item,index) {
			console.log(index,'12555')
            this.knowledgeIdArr.splice(index, 1);
			this.appForm.sqlIds.splice(index, 1);
        },
        clickConfig() {
            this.addBaseVisible = false;
            this.setBaseVisible = false;
            this.$EventBus.$emit("saveWorkflow");
        },
        // 弹窗回调带参数
        clickConfigParams(type, data) {          
			if (data.length > 0) {
				this.knowledgeIdArr = data 
				this.appForm.sqlIds = data 
				console.log('111',data)
				console.log('111',this.appForm.sqlIds)
			}
           console.log(this.knowledgeIdArr,this.appForm.sqlIds)
        },
        addKnowledgeToApp(data) {
            // this.knowledgeIdArr = this.appForm.knowledgeIds
            if(!this.appForm.sqlIds.includes(data.knowledgeId)) {
                this.appForm.sqlIds.push(data.knowledgeId);
            
            }
        },
        deleteKnowledgeToApp(data) {
            const index = this.appForm.sqlIds && this.appForm.sqlIds.findIndex((items) => items == data.knowledgeId);
            this.appForm.sqlIds.splice(index, 1);
        },
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
