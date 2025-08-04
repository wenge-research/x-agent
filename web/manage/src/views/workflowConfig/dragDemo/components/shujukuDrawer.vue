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
                    imgSuffix="shujuku"
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
            <div class="sub">用于处理多个字符串类型变量的格式</div>
            <div class="drawer-content">
                <div class="select-app">
                    <span>选择应用</span>
                    <el-select
                        v-model="appForm.model"
                        size="small"
                        :placeholder="$t('selectPlaceholder')"
                        style="width: 120px; float: right"
                        @change="onChangeModel"
                    >
                        <el-option label="字符串拼接" :value="1">
                        </el-option>
                        <el-option label="字符串分割" :value="2">
                        </el-option>
                    </el-select>
                </div>
                <el-collapse v-model="activeNames">
                    <el-collapse-item name="0" :title="$t('input')">
                      <inputList :key="inputListKey" :single="appForm.model === 2" v-if="drawerVisible" :inputs="appForm.inputs"
                             @updateInputList="updateInputList" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList>
                    </el-collapse-item>
                    <el-collapse-item name="1" :title="appForm.model === 1 ? '字符串拼接' : '字符串分割'">
                        <div v-if="appForm.model === 2">
                            <selectedOptions @setSelected="setSelected" :model="appForm.model" :delimiter="appForm.delimiter"></selectedOptions>
                        </div>
                        <div v-else>
                            <selectedOptions
                                :multiple="false"
                                @setSelected="setSelected"
                                :delimiter="appForm.delimiter"
                                :placeholder="'使用以下符号来自动连接数组中的每个项目'"
                            ></selectedOptions>
                            <el-input
                                v-model="appForm.text"
                                type="textarea"
                                :rows="3"
                                style="margin: 10px 0 0 0"
                                @blur="inputBlur"
                            ></el-input>
                        </div>
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
import selectedOptions from "./selectedOptions";
import inputList from "./inputList.vue";

export default {
    props: ["panels", "params"],
    name: "CustomDrawer",
    components: {
        selectedOptions, inputList
    },
    mixins: [drawerMixins],
    data() {
        return {
          inputListKey: 1,
            appForm: {
                model: 1,
                text: '',
                delimiter: [],
            },
            drawerVisible: false,
            activeNames: ["0", "1", "2"],
            direction: "rtl",
            curNode: {},
            parentNodes: [],
        };
    },
    watch: {},
    mounted() {
    },

    methods: {
        inputBlur() {
            this.$EventBus.$emit("saveWorkflow");
        },
        onChangeModel() {
            this.appForm.inputs = []
            this.inputListKey++
        },
        setSelected(data) {
            this.appForm.delimiter = JSON.parse(JSON.stringify(data));
        },
        

        
    },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style lang="scss" scoped>
.select-app {
    overflow: hidden;
    height: 44px;
    line-height: 34px;
    margin: 6px 0 0 0;
    font-weight: bold;
    font-size: 16px;
    color: #383d47;
}
</style>
