
// components
import HeadTool from "./components/head-tool.vue";
import RunningState from "./components/running-state.vue";
import RunningResult from "./components/running-result.vue";

export default {
    components: {
        RunningState,
        HeadTool,
        RunningResult
    },
    data() {
        return {
            isRunning: false,
            isRunningBtn: true,
            isRunningText:'运行成功',
            outputJsonData:null,
            iterationList:[],
            showSub: true,
            appForm: {
            },
            configurations: {},
            filterVariables: [],
            node: {},
            chatGptIdListNew: {
              智谱清言: require("@/assets/images/zpqy.png"),
              文心一言: require("@/assets/images/wxyy.png"),
              DeepSeek: require("@/assets/images/deepseek.png"),
              Kimi: require("@/assets/images/kimi.png"),
              雅意: require("@/assets/images/yayi.png"),
              豆包: require("@/assets/images/doubao.png"),
              百川: require("@/assets/images/baichuan.png"),
              星火: require("@/assets/images/xinghuo.png"),
              openAI: require("@/assets/images/openai.png"),
              MINIMAX: require("@/assets/images/MiniMax.png"),
              通义千问: require("@/assets/images/tongyi.png"),
            },
            chatGptIdList: {
                f570229417e36672814ff51a695447eb5: require("@/assets/images/zpqy.png"),
                f570229417ef4672814ff51a695447eb5: require("@/assets/images/wxyy.png"),
                f570229417ef4672814ff51a65447eb5: require("@/assets/images/deepseek.png"),
                f570229417ef4d72814ff51a65447eb5: require("@/assets/images/kimi.png"),
                f570229417ef4d79814ff51a65447eb5: require("@/assets/images/yayi.png"),
                e4cc6a8d45f74e6099cae1f56a0fe5e9: require("@/assets/images/doubao.png"),
            },
            chatGptNameList: {
                f570229417e36672814ff51a695447eb5: "智谱清言",
                f570229417ef4672814ff51a695447eb5: "文心一言",
                f570229417ef4672814ff51a65447eb5: "deepseek",
                f570229417ef4d72814ff51a65447eb5: "kimi",
                f570229417ef4d79814ff51a65447eb5: "雅意",
                e4cc6a8d45f74e6099cae1f56a0fe5e9: "豆包",
            },
            configurationList: [],
            configItems: [],
            startNodevariables: [],
            variables: [],
            tips: "",
            label: "",
            img: "",
            defaultLabel: "Query",
            toolData: {},
            showResult: false,
			      apiEndingNodesList:[]
        };
    },
    mounted() {
        this.$EventBus.$on("apiStarting", (data) => {
            this.handleCloseResult();
			this.isRunningBtn = true
			this.isRunningText = '运行成功'
            if (data?.id == this.node?.id) {
                this.isRunning = true;
            } else {
                this.isRunning = false;
            }
			
        });
        this.$EventBus.$on("clearIterationList", () => {
            this.iterationList=[]
            // console.log(this.iterationList,")))))))");
            
        });
		this.$EventBus.$on("apiEndingOutput", (data) => {
            
			// if (data?.nodeId == this.node?.id) {
				if(data.iterationNum>=0 && data.status=="WAITING_NEXT"){
                    if(this.iterationList.length==0){
                        this.iterationList.push(data)
                    }else{
                        const arr=this.iterationList.map(item=>item.iterationNum)
                        if(!arr.includes(data.iterationNum)){
                            this.iterationList.push(data)
                        }
                    }
					// this.outputJsonData = data;
                    // console.log(this.iterationList,"@@@@");
                    this.$EventBus.$emit("sendIterationList",this.iterationList)
				}
                if(data?.nodeId == this.node?.id){
                    if(data){
                        this.outputJsonData = data;
                    }
                }
			    
			// }
		  
		});
		this.$EventBus.$on("apiEndingNodeId", (data,flag) => {
			if(data.includes(this.node.id)){
				this.isRunning = true;
			} else{
				this.isRunning = false;
			}
			let lastNodeId = data[data.length - 1]
				// console.log(lastNodeId,'lastNodeId')
			if (lastNodeId == this.node.id) {
			    this.isRunningBtn = true
				if(!flag){
					this.isRunningText = '运行失败'
				}
				
			} else {
			   	this.isRunningBtn = true
				this.isRunningText = '运行成功'
			}
		});
		this.$EventBus.$on("apiEndingNodesList", (data) => {
		    this.apiEndingNodesList = data
		});
		
		
        this.initData();
		
    },
    methods: {
        getIcon(type){
          const obj = {
            'string':'string',
            'number':'Number',
            'integer':'integer',
            'file':'file-default',
            'default':'file-default',
            'image':'Arrayfile-image',
            'doc':'Arrayfile-doc',
            'code':'Arrayfile-code',
            'ppt':'Arrayfile-ppt',
            'txt':'Arrayfile-txt',
            'excel':'Arrayfile-excel',
            'boolean':'Boolean',
            'object':'Object',
            'array':'array',
            'array[string]':'ArrayString',
            'array[number]':'ArrayNumber',
            'array[integer]':'ArrayInteger',
            'array[boolean]':'ArrayBoolean',
            'array[object]':'ArrayObject',
            'array[file]':'Arrayfile',
          }
          return obj[type] ? obj[type] : 'string'
        },
        hideCurNode() {
          this.showSub = !this.showSub
        },
        initData() {
            this.node = this.getNode();
            console.log(this.node.data,"getNode");
            
            this.updateData(this.node.data)
            // 监听数据改变事件
            this.node.on("change:data", ({ current }) => {
                this.updateData(current)
            });
            this.$EventBus.$on("input", (name) => {
              this.node.label = name;
              this.label = name;
              this.appForm.label = name
            });
        },

        updateData(data) {
          this.label = data.label;
          this.startNodevariables = data.startNodevariables;
          this.toolData = data.toolData;
          this.img = data.img;
            this.appForm = data.appForm
                ? JSON.parse(data.appForm)
                : {};
            this.variables = data.variables && Array.isArray(data.variables) ? data.variables : data.variables ? JSON.parse(data.variables) : []
            this.configItems = data.configItems ? JSON.parse(data.configItems) : [{ "name": "result", "type": "array[string]", "level": 0, "children": [], expanded: true }]
            this.configurations =
                data.configurations &&
                JSON.parse(data.configurations);
            const time = new Date().getTime();
            let caseId = `out-other`;
            this.configurationList =
                data.configurationList ?
                JSON.parse(data.configurationList) : [{
                  caseId: caseId,
                  className: "分类一",
                  content: "其他",
                  sourceNodeId: "52c674c7-9b28-4ab1-a3f8-8771d90ee165"
                  }
                ];
            this.filterVariables =
                data.filterVariables &&
                JSON.parse(data.filterVariables);
            this.tips = data.tips;
        },
        removeNode() {
            this.getGraph().removeNode(this.node);
            this.$EventBus.$emit("removeNode");
        },
        inputChange(v) {
            this.$emit("input", v, this.node);
        },
        copyHandler() {
            this.$emit("copy", this.node);
        },
        detailHandler() {
            this.$emit("detail", this.node);
        },
        removeHandler() {
            this.removeNode();
        },
        // 测试节点
        openRunDrawer() {
            console.log("测试节点this.node1", this.node)
            this.$EventBus.$emit("openRunDrawer", this.node);
        },
        // 展开/收起运行结果
        handleOpenResult(showResult) {
            this.showResult = showResult;
        },
        // 收起运行结果
        handleCloseResult() {
            if (this.$refs.RunningState) {
                this.showResult = false;
                this.$refs.RunningState.isOpen = false;
            }

        }
    },
};
