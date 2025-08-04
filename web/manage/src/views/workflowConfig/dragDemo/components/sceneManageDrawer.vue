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
                    imgSuffix="changjing"
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
            <div class="drawer-content">
                <el-collapse v-model="activeNames">
                    <el-collapse-item name="0">
                        <template slot="title">
                            <div class="title-flex">
                                <span>{{ $t('input') }}</span>
                                <div class="icon-ctn">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'输入场景所需参数，节点运行时将自动传入这些参数以调用该场景'" placement="top" :effect="'light'">
                                    <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                                </el-tooltip>
                                </div>
                            </div>
                        </template>
                        <inputList
                            v-if="drawerVisible"
                            :inputs="appForm.inputs"
                            :key="inputKey"
                            @updateInputList="updateInputList"
                            :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"
                            :single="true"
                            :defalutVal="'Query'"
                        ></inputList>
                    </el-collapse-item>
                    <el-collapse-item name="1" :title="$t('sceneManage')">
                        <el-select v-model="appForm.sceneId" filterable @change="onScenechange" placeholder="请选择">
                            <el-option
                                :value="item.sceneId"
                                :label="item.sceneName"
                                v-for="(item,index) in konwlwdgeList"  :key="index"
                            ></el-option>
                            
                        </el-select>
                    </el-collapse-item>
                    <el-collapse-item name="3" :title="$t('output')">
                        <div class="result-cont">
                          系统提示词:systemPrompt<span>string</span>
                        </div>
                        <div class="result-cont">
                          新问题:newQuestion<span>string</span>
                        </div>
                        <div class="result-cont">
                          场景对象:matterGuide<span>object</span>
                        </div>
                        <div class="result-cont">
                          拦截标识:interceptFlag<span>boolean</span>
                        </div>
                        
                    </el-collapse-item>
                </el-collapse>
            </div>
        </el-drawer>
    </div>
</template>

<script>
// mixins
import drawerMixins from "./drawerMixins";
import inputList from "./inputList";
import {
    apiGetSceneManagementList,
} from "@/api/scene";
export default {
    props: ["panels", "params"],
    name: "CustomDrawer",
    components: {
        inputList,
    },
    mixins: [drawerMixins],
    data() {
        return {
          konwlwdgeList:[],
            appForm: {
              sceneId: "",
              sceneName: "",
            },
        };
    },
    mounted() {
      this.getSceneApplicationRefList();
    },

    methods: {
        onScenechange(val){
          this.appForm.sceneName = this.konwlwdgeList.find(item => item.sceneId == val)?.sceneName || '';
          this.$EventBus.$emit("saveWorkflow");
        },
        getSceneApplicationRefList() {
            apiGetSceneManagementList({
                pageNo: 1,
                pageSize: 9999,
                sceneName: '',
            }).then((res) => {
                if (res.code == "000000") {
                    this.konwlwdgeList = res.data?.records || [];
                } else {
                    this.konwlwdgeList = [];
                }
            });
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
        .tip {
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
