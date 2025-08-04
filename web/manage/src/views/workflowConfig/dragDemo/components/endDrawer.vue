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
                    imgSuffix="jieshu"
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
                支持中间过程的消息输出，支持流式和非流式两种方式
            </div>
            <div class="drawer-content">
                <el-collapse v-model="activeNames">
                    <el-collapse-item name="1" :title="$t('output')">
                        <inputList
                            v-if="drawerVisible"
                            :inputs="appForm.inputs"
                            :key="inputKey"
                            @updateInputList="updateInputList"
                            :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"
                        ></inputList>
                        <el-form ref="formRefs" @submit.prevent>
                            <el-form-item label="是否推流" style="margin: 10px 0;">
                                <el-switch
                                size="small" 
                                    v-model="appForm.streamFlag"
                                    active-value="是"
                                    inactive-value="否"
                                    @change="switchChange"
                                >
                                </el-switch>
                            </el-form-item>
                            <el-form-item label="选择推流的字段名称" v-if="appForm.streamFlag ===  '是'">
                                <el-select
                                size="small" 
                                    v-model="appForm.streamVar"
                                    clearable 
                                    placeholder="请选择"
                                    @change="onChange"
                                >
                                    <el-option
                                        :label="item.label"
                                        :value="item.label"
                                        v-for="(item, index) in appForm.inputs"
                                        :key="index"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="是否转文本" style="margin: 10px 0;" v-if="getStreamFlagType">
                              <el-switch
                              size="small" 
                                  v-model="appForm.arrayToStrFlag"
                                  active-value="是"
                                  inactive-value="否"
                                  @change="switchChange"
                              >
                              </el-switch>
                          </el-form-item>
                            <el-form-item label="推理字段">
                                <el-select
                                size="small" 
                                    v-model="appForm.reasonVar"
                                    clearable 
                                    placeholder="请选择"
                                    @change="onChange"
                                >
                                    <el-option
                                        :label="item.label"
                                        :value="item.label"
                                        v-for="(item, index) in appForm.inputs"
                                        :key="index"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="推流间隔(毫秒)">
                              <el-input-number size="small" v-model="appForm.interval" :step="20" @blur="inputBlur" @change="inputChange"></el-input-number>
                            </el-form-item>
                            <el-form-item label="输出格式">
                              <el-select
                              size="small" 
                                  v-model="appForm.format"
                                  placeholder="请选择"
                              >
                                  <el-option
                                      :label="item.label"
                                      :value="item.label"
                                      v-for="(item, index) in formats"
                                      :key="index"
                                  ></el-option>
                              </el-select>
                          </el-form-item>
                            <el-form-item label="自定义输出内容">
                              <el-input
                                v-model="appForm.content"
                                type="textarea"
                                :placeholder="contentTip"
                                :rows="3"
                                @blur="inputBlur"
                                @keydown.delete="handleKeyDown"
                              ></el-input>
                            </el-form-item>
                            <el-form-item label="自定义推理">
                              <el-input
                                v-model="appForm.reasonFormatter"
                                type="textarea"
                                :placeholder="contentTip"
                                :rows="3"
                                @blur="inputBlur"
                                @keydown.delete="handleKeyDown"
                              ></el-input>
                            </el-form-item>
                        </el-form>
                    </el-collapse-item>
                </el-collapse>
            </div>
        </el-drawer>
    </div>
</template>

<script>
import drawerMixins from "./drawerMixins";

export default {
    props: [],
    name: "endDrawer",
    mixins: [drawerMixins],
    data() {
        return {
            contentTip:'可以使用${变量名}的方式引用输入参数中的变量',
            appForm: {
                streamFlag: "否",
                arrayToStrFlag: "否",
                streamVar: "",
                reasonVar: "",
                reasonFormatter: "",
                content: "",
                format: "text",
                interval: 20,
                inputs: [],
            },
            formats:[
              {
                label: "text",
                value: "text"
              },
              {
                label: "json",
                value: "json"
              },
              {
                label: "markdown",
                value: "markdown"
              },
              {
                label: "html",
                value: "html"
              }
            ],
            startAppForm: {
                streamFlag: "否",
                arrayToStrFlag: "否",
                format: "text",
                reasonVar: "",
                reasonFormatter: "",
                content: "",
                streamVar: "",
                interval: 20,
                inputs: [],
            },
            streamFlag: false,
            drawerVisible: false,
            formRefs: null,
        };
    },
    computed: {
      getStreamFlagType(){
        let curSelect = this.appForm.inputs.find(item => item.label == this.appForm.streamVar)
        if(curSelect && curSelect.type.indexOf("array") > -1){
          return true
        } else {
          this.appForm.arrayToStrFlag = '否'
          return false
        }
      }
    },
    watch: {},
    mounted() {
        
    },
    methods: {
        handleKeyDown(event) {
          event.preventDefault()
        },
        inputBlur() {
            this.$EventBus.$emit("saveWorkflow");
        },
        inputChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        onChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        switchChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
    },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style lang="scss" scoped>
.delete-icon {
    font-size: 16px;
    cursor: pointer;
    color: #828894;
    margin-left: 10px;
    position: absolute;
    top: 9px;
    right: 6px;
}
</style>
