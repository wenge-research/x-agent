<template>
    <div class="Ec-x6-icon" @click="settingModel=false">
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
                    imgSuffix="workflow-zhishixieru"
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
                    {{ $t("functionDescription") }}
            </div>
            <div class="drawer-content">
                <el-collapse v-model="activeNames" >
                    <el-collapse-item name="0">
                        <template slot="title">
                            <div class="title-flex">
                                <span>{{ $t('input') }}</span>
                                <div class="icon-ctn">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'需要写入知识库的内容，支持文档写入'" placement="top" :effect="'light'">
                                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                                </el-tooltip>
                                </div>
                            </div>
                        </template>
                      <inputList v-if="drawerVisible" :inputs="appForm.inputs" style="margin-bottom: 10px;"
                          :key="inputKey" @updateInputList="updateInputList" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData" :single="true" :defalutType="'file'" :defalutVal="'File'" :defalutValTip="'仅支持引用File、Array[file]'"></inputList>
                  </el-collapse-item>
                    <el-collapse-item name="2">
                        <template #title>
                            <div class="title-flex">
                                <span>知识库</span>
                                <div class="icon-ctn">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'所有上传的文档将存入选择知识库中，仅支持添加一个知识库'" placement="top" :effect="'light'">
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
                            <el-popover
                                style="border: none;margin: 0px 0px 0px 10px;position: absolute;right: 40px;display: inline-block;height: 45px;"
                                placement="bottom-start"
                                :visible-arrow="false"
                                v-model="settingModel"
                                trigger="click">
                                <el-button
                                @click.stop
                                slot="reference"
                                type="text"
                                ><iconpark-icon name="settings-4-line" size="16" color="#1d2129"></iconpark-icon></el-button
                            >

                            <div class="knowledgeset-ctn">
                                <div class="knowledgeset-title-ctn">
                                <div class="knowledgeset-title">
                                    知识库设置
                                </div>
                                <div class="advancedset-ctn" @click.stop="openDialog('setBase')" v-if="isAdmin">
                                    <div class="advancedset-word">高级设置</div>
                                    <div class="advancedset-icon">
                                    <iconpark-icon name="arrow-right-double-fill" size="16" color="#86909C"></iconpark-icon>
                                    </div>
                                </div>
                                </div>

                                <div class="knowledgeset-options">
                                <div class="knowledgeset-options-list">
                                    <div class="left-ctn">
                                    <div class="words">重排模型</div>
                                    <div class="tooltip-ctn"> 
                                        <el-tooltip popper-class="workflow-tooltip-dark" content="对初步检索出的候选内容进行二次排序，提升传递给大模型的信息质量。" placement="top" effect="light">
                                        <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                                        </el-tooltip>
                                    </div>
                                    </div>
                                    <div class="knowledgeset-model">
                                    <el-select v-model="appForm.rearrangeModel" style="width: 100%;" size="small" placeholder="请选择">
                                        <el-option label="雅意" value="yayi"></el-option>
                                        <el-option label="火山引擎" value="volcengine"></el-option>
                                    </el-select>
                                    </div>
                                </div>
                                <div class="knowledgeset-options-list">
                                    <div class="left-ctn">
                                    <div class="words">检索内容匹配度</div>
                                    <div class="tooltip-ctn"> 
                                        <el-tooltip popper-class="workflow-tooltip-dark" content="对检索出的候选内容根据设定的匹配度召回并提供给大模型，低于设定值的段落将不被召回。" placement="top" effect="light">
                                        <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                                        </el-tooltip>
                                    </div>
                                    </div>
                                    <div class="right-ctn">
                                    <el-slider
                                        v-model="appForm.contentScore"
                                        :min="0"
                                        :max="10"
                                        :step="0.1"
                                    ></el-slider>
                                    <el-input-number
                                        class="score-input"
                                        v-model="appForm.contentScore"
                                        controls-position="right"
                                        :min="0"
                                        :max="10"
                                        :step="0.1"
                                        size="small"
                                        style="width: 80px"
                                    ></el-input-number>
                                    <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultValue('contentScore', 1.49)">
                                        <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
                                    </span>
                                    </div>
                                </div>
                                <div class="knowledgeset-options-list">
                                    <div class="left-ctn">
                                    <div class="words">重排内容匹配度</div>
                                    <div class="tooltip-ctn"> 
                                        <el-tooltip popper-class="workflow-tooltip-dark" content="对检索出的候选内容进行重新排序时，根据设定的匹配度召回并提供给大模型，低于设定值的段落将不被召回。" placement="top" effect="light">
                                        <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                                        </el-tooltip>
                                    </div>
                                    </div>
                                    <div class="right-ctn">
                                    <el-slider
                                        v-model="appForm.rangeContentScore"
                                        :min="0"
                                        :max="10"
                                        :step="0.1"
                                    ></el-slider>
                                    <el-input-number
                                        class="score-input"
                                        v-model="appForm.rangeContentScore"
                                        controls-position="right"
                                        :min="0"
                                        :max="10"
                                        :step="0.1"
                                        size="small"
                                        style="width: 80px"
                                    ></el-input-number>
                                    <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultValue('rangeContentScore', 1.49)">
                                        <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
                                    </span>
                                    </div>
                                </div>
                                <div class="knowledgeset-options-list">
                                    <div class="left-ctn">
                                    <div class="words">最大召回数量</div>
                                    <div class="tooltip-ctn"> 
                                        <el-tooltip popper-class="workflow-tooltip-dark" content="从知识库中召回并提供给大模型的段落数量上限。" placement="top" effect="light">
                                        <iconpark-icon name="question-line" size="16" color="#C9CDD4"></iconpark-icon>
                                        </el-tooltip>
                                    </div>
                                    </div>
                                    <div class="right-ctn">
                                    <el-slider
                                        v-model="appForm.filterNum"
                                        :min="0"
                                        :max="10"
                                        :step="0.1"
                                    ></el-slider>
                                    <el-input-number
                                        class="score-input"
                                        v-model="appForm.filterNum"
                                        controls-position="right"
                                        :min="0"
                                        :max="10"
                                        :step="0.1"
                                        size="small"
                                        style="width: 80px"
                                    ></el-input-number>
                                    <span class="score-reset-btn" style="cursor: pointer;" @click="setDefaultValue('filterNum', 10)">
                                        <iconpark-icon name="refresh-line" size="16" color="#828894"></iconpark-icon>
                                    </span>
                                    </div>
                                </div>
                                </div>
                            </div>

                                
                            </el-popover>
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
                                        ) in appForm.knowledgeIds"
                                        :key="index"
                                        @click="konwlwdgeClick(item)"
                                    >
                                        <div class="li-name flex-center">
                                            <img
                                                src="@/assets/images/appManagement/zsk.svg"
                                            />
                                            <span>{{
                                                filterKnowledge(item)
                                            }}</span>
                                        </div>
                                        <i
                                            class="el-icon-close"
                                            style="cursor: pointer"
                                            @click="deleteKnowledgeId(item)"
                                        ></i>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </el-collapse-item>
                    <el-collapse-item name="3" :title="$t('output')">
                        <div class="result-cont">
                            documentId<span>string</span>
                        </div>
                        <div class="result-cont">
                            fileName<span>string</span>
                        </div>
                        <div class="result-cont">
                            fileUrl<span>string</span>
                        </div>
                    </el-collapse-item>
                </el-collapse>
            </div>
        </el-drawer>
        <!-- 知识库参数设置 -->
        <knowledgeBaseSet
            v-if="setBaseVisible"
            :dialogVisible="setBaseVisible"
            :params="appForm"
            @clickConfig="clickConfig"
            @clickConfigParams="clickConfigParams"
        ></knowledgeBaseSet>
        <!-- 新增知识库 -->
        <addKnowledgeBase
            v-if="addBaseVisible"
            :dialogVisible="addBaseVisible"
            :configData="appForm.knowledgeIds"
            @clickConfig="clickConfig"
			:isRadio="true"
            @clickConfigParams="clickConfigParams"
        ></addKnowledgeBase>
    </div>
</template>

<script>
// mixins
import drawerMixins from "./drawerMixins";
import knowledgeBaseSet from "@/views/appManage/components/knowledgeBaseSet.vue";
import addKnowledgeBase from "@/views/appManage/components/addKnowledgeBase.vue";
import inputList from "./inputList";

const knowledgeBaseTypeList = [
     {
          value: '',
          label: 'QA对',
          children: [
            {
            value: 'findQaTitle',
            label: 'QA问题',
            tip: '（检索精准类的问题）',
          }, {
            value: 'findQaContent',
            label: 'QA答案',
            tip: '（检索精准类的答案）',
          }, {
            value: 'builtIn',
            label: '内置问题',
            tip: '（完全匹配问题）',
          }, {
            value: 'knowledgeContent',
            label: '非精准',
            tip: '（检索非精准的答案）',
          }]
    },
    {
          value: 'documentLibrary',
          label: '文档',
    },
    {
          value: 'WebsiteStrategy',
          label: 'URL',
    },
    {
          value: 'structuredStrategy',
          label: '结构化数据',
    },
    {
          value: 'multiMediaLibrary',
          label: '多媒体',
    },
]

export default {
    props: ["panels", "params"],
    name: "CustomDrawer",
    components: {
      inputList,
        knowledgeBaseSet,
        addKnowledgeBase,
    },
    mixins: [drawerMixins],
    computed:{
        isAdmin(){
            return JSON.parse(sessionStorage.getItem("user")).powerType=="0" 
        },
    },
    data() {
        return {
            knowledgeBaseTypeList,
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
                inputs: [],
                rearrangeModel:"yayi"
            },
            konwlwdgeAllList: [],
            settingModel:false,
            options: [
                {
                    value: "knowledgeContent",
                    label: "QA",
                },
                {
                    value: "documentLibrary",
                    label: "文档",
                },
                {
                    value: "multiMediaLibrary",
                    label: "多媒体",
                },
                {
                    value: "structuredStrategy",
                    label: "结构化",
                },
                {
                    value: "WebsiteStrategy",
                    label: "网站网页数据",
                },
                {
                    value: "networkStrategy",
                    label: "联网检索",
                },
                {
                    value: "findQaTitle",
                    label: "检索QA【问题】",
                },
                {
                    value: "findQaContent",
                    label: "检索QA【答案】",
                },
            ],
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
        konwlwdgeClick(item) {
          this.$store.commit('setKonwledge', null)
          
          if(this.filterKnowledge(item)){
            this.$store.commit('setIsAddedToApp', true);
          }else {
            this.$store.commit('setIsAddedToApp', false);
          }
          
          this.$store.commit('setKonwledge', item)
        },
        cascaderChange(list) {
            this.appForm.selectList = list
            console.log(JSON.stringify(list))
            this.appForm.type = (this.appForm.selectList.map(item => item?.length >= 2 ? item[1] : item[0])).join()
            this.$EventBus.$emit("saveWorkflow");
        },
        
        // 打开弹窗
        openDialog(type) {
            if (type == "setBase") {
                this.setBaseVisible = true;
            } else if (type == "addBase") {
                this.knowledgeIdArr = this.appForm.knowledgeIds;
                this.addBaseVisible = true;
            }
        },
        filterKnowledge(item) {
            let findItem = this.$store.state?.workflow?.knowledgeList.find(
                (items) => items.knowledgeId == item.knowledgeId
            );
            return findItem?.knowledgeName;
        },
        // 删除知识库
        deleteKnowledgeId(item) {
            let filterArr = this.knowledgeIdArr.filter(
                (items) => items != item
            );
            this.knowledgeIdArr = this.appForm.knowledgeIds = filterArr || [];
        },
        clickConfig() {
            this.addBaseVisible = false;
            this.setBaseVisible = false;
            this.$EventBus.$emit("saveWorkflow");
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
        addKnowledgeToApp(data) {
            // this.knowledgeIdArr = this.appForm.knowledgeIds
            if(!this.appForm.knowledgeIds.includes(data.knowledgeId)) {
                this.appForm.knowledgeIds.push(data.knowledgeId);
            
            }
        },
        deleteKnowledgeToApp(data) {
            const index = this.appForm.knowledgeIds && this.appForm.knowledgeIds.findIndex((items) => items == data.knowledgeId);
            this.appForm.knowledgeIds.splice(index, 1);
        },
        setDefaultValue(key, value) {
            this.appForm[key] = value;
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
