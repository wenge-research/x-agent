// components
import HeadTool from "./nodeTheme/components/head-tool.vue";
import inputList from "./inputList";
import newInputList from "./newInputList.vue";

import { modelList } from "@/api/app";

export default {
    props: ["sourceData"],
    components: { HeadTool, inputList,newInputList },
    data() {
        return {
            activeNames: ["0", "1", "2", "3", "4"],
            editName:'',
            modelTip:'系统提示词，可以使用${变量名}的方式引用输入参数中的变量',
            userTip:'用户提示词，可以使用${变量名}的方式引用输入参数中的变量',
            magnifiedBoxVisible: false,
            drawerVisible: false,
            chatGptIdList: {
              智谱清言: require("@/assets/images/zpqy.png"),
              文心一言: require("@/assets/images/wxyy.png"),
              DeepSeek: require("@/assets/images/deepseek.png"),
              Kimi: require("@/assets/images/kimi.png"),
              雅意: require("@/assets/images/yayi.png"),
              豆包: require("@/assets/images/doubao.png"),
              百川: require("@/assets/images/baichuan.png"),
              星火: require("@/assets/images/xinghuo.png"),
              openAI: require("@/assets/images/openai.png"),
              MINIMAX: require("@/assets/images/MiniMax.png")
            },
            appForm: {
                inputs: [],
                outputs: [],
                item_result: [],
                modelId:'',
                modelInfo: {},
            },
            startAppForm: {
                inputs: [],
                modelId:'',
                outputs: [],
                item_result: [],
            },
            nodeStoreData: {},
            curNode: {},
            curParentNodeInputs: [],
            curParentNodesData: {},
            curChildrenNodes: {},
            parentNodes: [],
            inputKey: 1,
            modleOptions: [],
            direction: "rtl",
        };
    },
    watch: {
        appForm: {
            handler(newVal, oldVal) {
                this.$emit(
                    "updateAppForm",
                    {
                        appForm: JSON.stringify(newVal),
                    },
                    this.curNode
                );
                // const differences = this.compareObjects(oldVal, newVal)
                // console.log("differences", differences)
            },
            deep: true,
        },
        "appForm.inputs": {
            handler(newVal, oldVal) {
                this.$emit(
                    "updateAppForm",
                    {
                        appForm: JSON.stringify(this.appForm),
                    },
                    this.curNode
                );
            },
            deep: true, // 如果 inputs 是一个对象数组，设置 deep 为 true 可以监听到数组内部对象的变化
        },
        "appForm.outputs": {
            handler(newVal, oldVal) {
                this.$emit(
                    "updateAppForm",
                    {
                        appForm: JSON.stringify(this.appForm),
                    },
                    this.curNode
                );
            },
            deep: true, // 如果 outputs 是一个对象数组，设置 deep 为 true 可以监听到数组内部对象的变化
        },
        "appForm.item_result": {
            handler(newVal, oldVal) {
                this.$emit(
                    "updateAppForm",
                    {
                        appForm: JSON.stringify(this.appForm),
                    },
                    this.curNode
                );
            },
            deep: true, // 如果 item_result 是一个对象数组，设置 deep 为 true 可以监听到数组内部对象的变化
        }
    },
    methods: {
        handleDrawerClick(e) {
          const isInside = e.target.closest('.model-setting-btn') !== null;
          if (!isInside) {
            this.$EventBus.$emit("hideModelSelect");
          }
        },
        modelChange(val){
          this.appForm.modelId = val
          this.appForm.modelInfo = this.$store.state?.workflow?.modleOptions.find(item=>item.modelId === val)
          this.$emit(
              "updateAppForm",
              {
                  appForm: JSON.stringify(this.appForm),
              },
              this.curNode
          );
        },
        compareObjects(obj1, obj2) {
            const diff = {};
        
            for (const key in obj1) {
                if (obj1[key] !== obj2[key]) {
                    diff[key] = {
                        oldValue: obj1[key],
                        newValue: obj2[key]
                    };
                }
            }
        
            for (const key in obj2) {
                if (!obj1.hasOwnProperty(key)) {
                    diff[key] = {
                        oldValue: undefined,
                        newValue: obj2[key]
                    };
                }
            }
        
            return diff;
        },
        handleRemove() {
            this.$emit("remove", this.curNode);
        },
        copyHandler() {
            this.$emit("copy", this.curNode);
        },
        detailHandler() {
            this.$emit("detail", this.curNode);
        },
        inputHandler(val) {
            this.$emit("input", val, this.curNode);
        },
        updateInputList(data) {
            this.appForm.inputs = JSON.parse(data.inputs);
            this.$emit("updateCellData", { inputs: data.inputs }, this.curNode);
        },
        updateOutputList(data) {
            this.appForm.outputs = JSON.parse(data.inputs);
            this.$emit("updateCellData", { outputs: data.inputs }, this.curNode);
        },
        updateItemResult(data) {
            this.appForm.item_result = JSON.parse(data.inputs);
            this.$emit("updateCellData", { item_result: data.inputs }, this.curNode);
        },
        openDrawer() {
            this.initDrawer();
        },
        closeDrawer() {
            this.drawerVisible = false;
        },

        handleClose(done) {
            this.$confirm(this.$t("confirmClose"))
                .then((_) => {
                    done();
                })
                .catch((_) => {});
        },
        initDrawer() {
          
            let node = this.$store.state.workflow.editNode;
            let parentNodes = this.$store.state.workflow.parentNodes;
            this.nodeStoreData = node.store.data.data;
            this.curChildrenNodes = this.$store.state.workflow.childNodes;
            this.appForm = this.nodeStoreData.appForm
                ? JSON.parse(this.nodeStoreData.appForm)
                : JSON.parse(JSON.stringify(this.startAppForm));
            this.inputKey++
            this.curNode = node;
            this.parentNodes = parentNodes;
            this.drawerVisible = true;
            if(this.curNode._parent){
              let curParentNodesData = this.curNode._parent.store.data.data
              this.curParentNodesData = curParentNodesData
              if(this.curNode._parent.store.data.data.inputs){
                let inputs = JSON.parse(this.curNode._parent.store.data.data.inputs)
                inputs = inputs.map(item => {
                  return {
                    ...item,
                    pid: this.curNode._parent.id,
                    name: curParentNodesData.label
                  }
                })
                this.curParentNodeInputs = inputs
              }
            }
        },
        // 测试节点
        openRunDrawer() {
            this.$EventBus.$emit("openRunDrawer", this.curNode);
        },
        handleInput(val){
          this.appForm[this.editName] = val
          this.$emit(
            "updateAppForm",
            {
                appForm: JSON.stringify(this.appForm),
            },
            this.curNode
        );
        },
        // 打开提示词库抽屉
        openVocabularyDrawer(type) {
          this.editName = type
          this.$refs.vocabularyDrawer.openDarwer(this.appForm[type])
        },
        // 插入提示词
        insertVocabularyFn(vocabularyContent) {
          let con = ''
          if(this.appForm[this.editName] == undefined){
            con = vocabularyContent
          }else {
            con = this.appForm[this.editName] + vocabularyContent
          }
          // 插入到当前内容
          this.$set(this.appForm, this.editName, con);
          // 赋值到放大框
          this.$refs.magnifiedEditBox.setDataText(this.appForm[this.editName]) ;
        },
        // 打开提问词放大框
        openMagnifiedEditBox(type) {
          this.editName = type
          // 通过事件总线触发打开事件
          this.$refs.magnifiedEditBox.openDarwer(this.appForm[type])
        },
        handleMagnifiedBoxVisibleChange(visible) {
          this.magnifiedBoxVisible = visible;
        },
        //多agent参数改变
        changeAgentParams(val){
          this.$store.commit("setMulAgentParams", {key:this.curNode.id, value:val});
        }
    },
    mounted() {
      this.$EventBus.$on("input", (name) => {
        this.label = name;
        this.appForm.label = name
      });
    },
};
