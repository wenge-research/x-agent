import { getKnowledgeInfoList } from "@/api/index";
import { modelList } from "@/api/app";

export default {
  state: {
    editNode: {},
    parentNodes: [],
    childNodes: [],
    funcData: {}, // 功能数据源
    funcPluginList: [], // 功能插件数据源
    showDrawerData: {}, // 展示数据源
    knowledgeList: [], // 知识库数据源
    modleOptions: [], // 大模型数据
    workFlowType: 'workflow', // 工作流类型 workflow 工作流  dialogue 对话流
    dialogueInputLoading:false,
    dialogueLoading:false,
    scrollbarBottom:false,
    messageDom:null,
    tempFileList:[],
    fileUploadList:[],
    fiileUpdateCV:{
      accept: '.png,.jpg,.jpeg',
      suffixArr: ['png', 'jpg', 'jpeg'],
      list: [],
      status: false,
      url: '',
    },
    modelType: '',   //模型模式（qa,dialogue,text-agent,Workflow,multi_agent）
    mulAgentParams: {},   //多模态判断全局条件，场景一致
  },
  mutations: {
    //存放editNode
    setEditNode(state,val) {
      state.editNode = val
    },
    setMulAgentParams(state, obj) {
      state.mulAgentParams[obj.key] = obj.value
    },
    setParentNodes(state,val) {
      state.parentNodes = val
    },
    setChildNodes(state,val) {
      state.childNodes = val
    },
    setFuncData(state,val) {
      state.funcData = val
    },
    setFuncPluginList(state,val) {
      state.funcPluginList = val
    },
    setShowDrawerData(state,val) {
      state.showDrawerData = val
    },
    setKnowledgeList(state, val) {
      state.knowledgeList = val;
    },
    setModleOptions(state, val) {
      state.modleOptions = val;
    },
    setWorkFlowType(state, val) {
      state.workFlowType = val;
    },
    setmodelType(state, val) {
      state.modelType = val;
    },
  },
  actions: {
    fetchKnowledgeList({ commit }) {
      getKnowledgeInfoList({
        pageNo: 1,
        pageSize: 1000,
        order: "",
        sort: "",
        ownerType:'all'
      }).then((res) => {
        if (res.code === "000000") {
          commit('setKnowledgeList', res.data?.records || []);
        } else {
          commit('setKnowledgeList', []);
        }
      });
    },
    // 应用板块 获取大模型选择的数据
    fetchModleOptions({ commit }) {
      modelList({
        id: "",
        modelId: "",
        modelName: "",
        status: "启用",
        ownerType:'all'
      }).then((res) => {
        if (res.code === "000000") {
          let data = res.data.filter(item => item.status !== '禁用')
          commit('setModleOptions', data || []);
        } else {
          commit('setModleOptions', []);
        }
      });
    },
  }
}
