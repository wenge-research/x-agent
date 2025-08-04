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
                    imgSuffix="wendangtiquqi"
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
            <div class="sub">解析并读取文档文件中的信息，并转化为文本</div>
            <div class="drawer-content">
                <el-collapse v-model="activeNames" >
                    <el-collapse-item name="0" :title="$t('input')">
                        <inputList v-if="drawerVisible" :inputs="appForm.inputs"
                            :key="inputKey" @updateInputList="updateInputList" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData" :single="true" :defalutType="'file'" :defalutVal="'File'" :defalutValTip="'仅支持引用File、Array[file]'"></inputList>
                        <el-form ref="formRefs" @submit.prevent style="margin: 30px 0 0 0;">
                          <el-form-item label="是否输出文档切片" style="margin: 0;">
                            <el-switch
                              size="small" 
                                  v-model="appForm.slice"
                              >
                              </el-switch>
                          </el-form-item>
                        </el-form>
                    </el-collapse-item>
                    <el-collapse-item name="2" :title="$t('output')">
                        <div class="result-cont">
                            result<span>array[string]</span>
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
import selectedOptions from "./selectedOptions";
export default {
    props: ["panels", "params"],
    name: "CustomDrawer",
    components: {
        inputList, selectedOptions
    },
    mixins: [drawerMixins],
    data() {
        return {
            appForm: {
              slice: false,
            },
            startAppForm: {
              slice: false,
            },
        };
    },
    watch: {
    },
    mounted() {
    },
    methods: {
    },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style lang="scss" scoped>
.select-app{
  overflow: hidden;
    height: 44px;
    line-height: 34px;
    margin: 6px 0 0 0;
    font-weight: bold;
    font-size: 16px;
    color: #383D47;
}
</style>

