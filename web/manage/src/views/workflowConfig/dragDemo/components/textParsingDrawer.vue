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
                    imgSuffix="wenbenchuli"
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
                             @updateInputList="updateInputList" :parentNodes="parentNodes"></inputList>
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
                                placeholder="可以使用${变量名}的方式引用输入参数中的变量"
                                :rows="3"
                                style="margin: 10px 0 0 0"
                                @blur="inputBlur"
								@input="inputsBlur"
                            ></el-input>
							<div class="variableBox" v-if="variableBoxFlag"  @mousedown.stop @touchstart.stop
							  >
							  <div class="variableBox-item" v-for="(item,index) in appForm.inputs" :key="index"
							    @click="insertSelectedVariable(item.label)">
							    <div v-if="item.label">{{item.label}}</div>
							  </div>
							  <div class="no-data1" v-if="appForm.inputs[0].label==''&&appForm.inputs.length==0">
							    无匹配的变量
							  </div>
							</div>
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
		  variableBoxFlag: false,
            appForm: {
                model: 1,
                text: '',
                inputs: [],
                delimiter: [],
            },
            startAppForm: {
                model: 1,
                text: '',
                inputs: [],
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
		inputsBlur() {
			if(this.appForm.text.includes('$')){
				this.variableBoxFlag = true
			}else{
				this.variableBoxFlag = false
			}   
		},
		insertSelectedVariable(label){
			this.appForm.text = this.appForm.text+'{'+label+'}'
			this.variableBoxFlag = false
		},
        onChangeModel() {
            this.appForm.inputs = []
            this.inputListKey++
        },
        setSelected(data) {
            this.appForm.delimiter = JSON.parse(JSON.stringify(data));
            this.$emit(
                "updateAppForm",
                {
                    appForm: JSON.stringify(this.appForm),
                },
                this.curNode
            );
           // this.$EventBus.$emit("saveWorkflowAgain");
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
.variableBox {
      position: fixed;
      z-index: 99;
      min-width: 128px;
      padding: 3px;
      box-sizing: border-box;
      background: #FFFFFF;
      box-shadow: 0px 0px 12px 0px rgba(26, 36, 70, 0.08);
      border-radius: 2px;
  
      &-item {
		  cursor: pointer;
        div {
          height: 32px;
          line-height: 32px;
          font-size: 14px;
          color: #36383D;
          padding: 0px 9px;
  
          &:hover {
            background: rgba(188, 193, 204, 0.2);
          }
        }
      }
  
      .no-data1 {
        height: 32px;
        line-height: 32px;
        font-size: 14px;
        color: #36383D;
        padding: 0px 9px;
      }
    }
</style>
