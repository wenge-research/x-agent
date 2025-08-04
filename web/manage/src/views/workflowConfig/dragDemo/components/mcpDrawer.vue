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
                    imgSuffix="MCP"
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
            <div class="sub">MCP服务</div>
            <div class="drawer-content">
                <el-collapse v-model="activeNames">
                    <el-collapse-item name="0">
                        <template slot="title">
                            <div class="title-flex">
                                <span>{{ $t('inputVariable') }}</span>
                                <div class="icon-ctn">
                                <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'输入MCP所需参数，节点运行时将自动传入这些参数以调用该服务'" placement="top" :effect="'light'">
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
                    <el-collapse-item name="1" :title="$t('sensitiveWord')">
                        <template #title>
                            MCP服务
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
                                        v-for="(item, index) in configurations"
                                        :key="index"
                                    >
                                        <template>
                                            <div class="li-name flex-center">
                                                {{ item.mcpName }}
                                            </div>
                                            <i
                                                class="el-icon-close"
                                                style="cursor: pointer"
                                                @click="removeItem(index)"
                                            ></i>
                                        </template>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </el-collapse-item>
                    <el-collapse-item name="3" :title="$t('output')">
                        <div class="result-cont">
                            result<span>array[object]</span>
                        </div>
                    </el-collapse-item>
                </el-collapse>
            </div>
        </el-drawer>
        <!-- <addSensitiveDialog
            v-if="addSensitiveVisible"
            :dialogVisible="addSensitiveVisible"
            @clickConfig="clickConfig"
            :configData="interceptWordHouses"
            @clickConfigParams="clickConfigParams"
            :appConfigForm="params.applicationInfo"
        ></addSensitiveDialog> -->
        <mcpDialog
            v-if="addSensitiveVisible"
            :dialogVisible.sync="addSensitiveVisible"
            @clickConfig="clickConfig"
            :configData="interceptWordHouses"
            @clickConfigParams="clickConfigParams"
            :appConfigForm="params.applicationInfo"
        ></mcpDialog>
    </div>
</template>

<script>
// mixins
import drawerMixins from "./drawerMixins";
import mcpDialog from "./selectMcp.vue";
// import addSensitiveDialog from "@/views/appManage/components/addSensitiveDialog.vue";
export default {
    name: "CustomDrawer",
    components: { mcpDialog },
    props: ["panels", "params"],
    mixins: [drawerMixins],
    data() {
        return {
            configurations: [],
            interceptWordHouses: [],
            startConfigurations: [
                {
                    interceptWordType: "",
                    interceptWordContent: "",
                },
            ],
            addSensitiveVisible: false,
        };
    },
    watch: {
        configurations: {
            handler(newVal, oldVal) {
                this.$emit(
                    "updateAppForm",
                    { configurations: JSON.stringify(newVal) },
                    this.curNode
                );
            },
            deep: true,
        },
    },
    mounted() {
        
    },
    beforeDestroy() {},
    methods: {
        clickConfigParams(type, data, selectList) {
            console.log(type, data, selectList);
            this.interceptWordHouses = data;
            if (selectList.length > 0) {
                this.configurations = [...selectList] || [];
                this.$EventBus.$emit("saveWorkflow");
            }
        },
        openDialog() {
            this.addSensitiveVisible = true;
        },
        clickConfig() {
            this.addSensitiveVisible = false;
        },
        removeItem(index) {
            this.configurations.splice(index, 1);
			this.interceptWordHouses = this.configurations.map(
			    (item) => item.mcpId
			);
            this.$EventBus.$emit("saveWorkflow",this.configurations);
        },
        openDrawer() {
            this.initDrawer();
            this.configurations = this.nodeStoreData.configurations
                ? JSON.parse(this.nodeStoreData.configurations)
                : [];
            this.interceptWordHouses = this.configurations.map(
                (item) => item.mcpId
            );
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
