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
        >
            <!-- <div class="drawer-header">
                <img src="@/assets/svg/gongzuoliujiedian-zhishijiansuo.svg" /> 
                <span>工具节点</span>
                <i
                    class="el-icon-close custom-close-icon"
                    @click="closeDrawer"
                ></i>
            </div> -->
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
                    imgSuffix="gongjujiedian"
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
            <div class="sub" v-if="nodeType === 'deduce'">推演工具</div>
            <div class="sub" v-else>支持自定义工具</div>
            <div class="drawer-content" :key="inputKey">
                <el-collapse v-model="activeNames">
                    <el-collapse-item name="0">
                        <template slot="title">
                            <div class="title-flex">
                                <span>{{ $t('inputVariable') }}</span>
                                <div class="icon-ctn" v-if="nodeType != 'deduce'">
                                    <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'输入插件所需参数，节点运行时将自动传入这些参数以调用该插件'" placement="top" :effect="'light'">
                                        <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                                    </el-tooltip>
                                </div>
                            </div>
                        </template>
                        <el-form
                            :model="ruleForm"
                            ref="ruleForm"
                            label-width="0"
                            class="demo-ruleForm"
                        >
                            <el-form-item
                                label=""
                                v-for="(panel, index) in appForm.startNode"
                                :key="index"
                            >
                                <inputList :defalutVal="panel.label" :single="true" v-if="drawerVisible" :inputs="startNodevariables[index]" @updateInputList="(value) => updateInputList(value, index)"
                            :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData" :isTooltip="nodeType == 'deduce'?true:false"></inputList>
                            </el-form-item>
                        </el-form>
                    </el-collapse-item>

                    <el-collapse-item name="2" :title="$t('output')">
                        <div class="result-cont" v-for="item in appForm.endNode">
                            {{ item.label }}<span>{{ item.type }}</span>
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

export default {
    props: ["componentParams"],
    name: "runDrawer",
    components: {  },
    mixins: [drawerMixins],
    data() {
        return {
            ruleForm: {},
            nodeType: "tool",
            appForm: {
              startNode: {},
              endNode: {},
            },
            startNodevariables: [null,null,null,null,null,null,null],
            
        };
    },
    watch: {
        startNodevariables: {
            handler(newVal, oldVal) {
                this.$emit(
                    "updateCellData",
                    { startNodevariables: newVal },
                    this.curNode
                );
            },
            deep: true,
        },
    },
    mounted() {},
    methods: {
        openDrawer(type) {
            this.nodeType = type;
            this.initDrawer();
            this.appForm.startNode = this.nodeStoreData.startNode;
            this.appForm.endNode = this.nodeStoreData.endNode;
            this.startNodevariables = this.nodeStoreData.startNodevariables || [];
        },
        updateInputList(data,i) {
          this.startNodevariables[i] = JSON.parse(data.inputs)
          this.$emit(
                    "updateCellData",
                    { startNodevariables: this.startNodevariables },
                    this.curNode
                );
            // this.appForm.inputs = JSON.parse(data.inputs);
            // this.$emit("updateCellData", { inputs: data.inputs }, this.curNode);
        },
    },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style lang="scss" scoped>
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
