<!--调试 -->
<template>
  <div class="addDebug-conten">
    <div class="modal-header">
      <div class="header-top">
        <!-- <div class="time flex-center">
          <iconpark-icon name="calendar-line" size="16" color="#494E57"
            style="margin-left: 14px;cursor: pointer;margin-right: 9px;"></iconpark-icon>
          <h1 class="text">{{ $t("debugTime") }}</h1>
        </div>
        <div class="res flex-center">
          <iconpark-icon name="filter-2-line" size="16" color="#494E57"
            style="margin-left: 14px;cursor: pointer;margin-right: 9px;"></iconpark-icon>
          <h1 class="text">{{ $t("status") }}</h1>
        </div> -->
        <div class="search flex-center">
          <iconpark-icon name="search-2-line" size="16" color="#1D2129" style="margin-left: 14px;cursor: pointer;margin-right: 7px;left: 0;z-index:1;top: 12px;
              position: absolute;"></iconpark-icon>
          <el-select v-model="selectedQuestion" @change="handelQuestion">
            <el-option v-for="(question, index) in questions" :key="index"
              :label="question" :value="question" class="search-text" />
          </el-select>
          <!-- <iconpark-icon name="arrow-down-s-line" size="16" color="#494E57"
            style="margin-left: 14px;cursor: pointer;margin-right: 7px;"></iconpark-icon> -->
        </div>
      </div>
      <div class="debug-center" v-if="wholeProcessList.length != 0">
        <div class="debug-top">
          <!-- <h1 class="debug-res">耗时 {{ wholeProcessList[1].costTime }}ms</h1>
          <h1 class="separator">{{ wholeProcessList[0].tokenTotal }} Tokens</h1> -->
          <div class="debug-type flex-center">
            <iconpark-icon name="checkbox-circle-line" size="16" color="#009A29"
              style="margin-left: 7px;cursor: pointer;margin-right: 6px;"></iconpark-icon>
            <h3 class="success">{{ $t("successed") }}</h3>
          </div>
        </div>
        <div class="debug-text">
          <div class="title">
            <span class="text-left">logid</span>
            <span class="text-right">{{ allItems.dialogueId }}</span>
          </div>
          <div class="title">
            <span class="text-left">{{ $t("requestInitiationTime") }}</span>
            <span class="text-right">{{ wholeProcessList[1].createTime }}</span>
          </div>
          <div class="title">
            <span class="text-left">{{ $t('initialResponseTime') }}</span>
            <span class="text-right">{{ wholeProcessList[1].costTime }}ms</span>
          </div>
        </div>
      </div>
      <div class="debug-but">
        {{ $t("debugCallTTree") }}
        <!-- <ul>
          <li :class="[tabs == 1 ? 'selected' : '']" @click="handleDebugtpye(1)">
            {{ $t("debugCallTTree") }}
          </li>
          <li :class="[tabs == 2 ? 'selected' : '']" @click="handleDebugtpye(2)">
            {{ $t("debugFlameGraph") }}
          </li>
        </ul> -->
      </div>
      <div class="debug-bot">
        <el-tree :data="treeData" :props="defaultProps" :render-content="renderContent" @current-change="handleNodeDetails"
          node-key="id" />
        <!-- <div class="tree-ctn" v-for="(item,index) in treeList">
          <div class="tree-label-ctn">
            <img :src="item.img" alt="">
            <p>{{ item.label }}</p>
          </div>
          <div class="tree-child" v-if="item.children.length>0">
            <div class="empty-gap"></div>
            <div class="line-gap">
              <div class="line-gap-list" v-for="(ele,index) in item.children" :style="{height:`${36*index+16}px`}">

              </div>
            </div>
            <div class="label-gap">
              <div class="tree-label-ctn" v-for="ele in item.children">
                <img :src="ele.img" alt="">
                <p>{{ ele.label }}</p>
              </div>
            </div>
          </div>
        </div> -->
          
      </div>
    </div>

    <!--节点详情 -->
    <div class="previewDebugging" v-if="isNodeDetails">
      <div class="heaed">
        <div class="heaed-top">
          <h3 class="previewAndDebug">{{ $t("node") }}{{ $t("details") }}</h3>
          <iconpark-icon name="close-large-fill" size="16" color="#1D2129" @click="handleClose()"
            style="margin-left: 12px;cursor: pointer"></iconpark-icon>
        </div>
      </div>
      <div class="node-info">
        <div class="title">
          <h1 class="play">
            <iconpark-icon name="play-circle-line" size="18" color="#FFFFFF"
              style="margin-left: 7px;cursor: pointer"></iconpark-icon>
          </h1>
          <span class="tit-text">{{ selectedQuestion }}</span>
        </div>
        <div class="info-conten">
          <div class="info-min">
            <span class="info-type">{{ $t("type") }}</span>
            <span class="start">{{ $t('start') }}</span>
            <span class="info-type">{{ $t('status') }}</span>
            <span class="start">{{ $t('successed') }}</span>
          </div>
          <div class="info-min">
            <span class="info-type">{{ $t('workflowName') }}</span>
            <span class="start">{{ selectedNode.step}}</span>
            <span class="info-type">{{ $t('wholeCostTime') }}</span>
            <span class="start">{{ selectedNode.costTime || 0}}ms</span>
          </div>
          <div class="info-min">
            <span class="info-type">{{ $t('requestInitiationTime') }}</span>
            <span class="start">{{ selectedNode.createTime }}</span>
            <span class="info-type">{{ $t('initialResponseTime') }}</span>
            <span class="start">{{ selectedNode.costTime || 0 }}ms</span>
          </div>
          <div class="info-min">
            <span class="info-type">{{ $t('endTime') }}</span>
            <span class="start">{{ selectedNode.endTime }}</span>
            <!-- <span class="info-type">Tokens</span>
            <span class="start">{{ selectedNode.tokenTotal }}</span> -->
            <span class="info-type">{{ $t('startReplyTime') }}</span>
            <span class="start">{{ selectedNode.createTime }}</span>
          </div>
          <div class="info-min">
            <!-- <span class="info-type">开始回复时间</span>
            <span class="start">{{ selectedNode.createTime }}</span> -->
          </div>
        </div>
        <div class="input-conten">
          <h1 class="input-tit">{{ $t('input') }}</h1>
          <pre class="input-info" v-if="isJson">{{ formattedData }}</pre>
          <div style="white-space: normal;" class="input-info" v-else>{{ formattedData }}</div>
          <!-- <div class="input-info">{{selectedNode.prompt }}</div> -->
        </div>
        <div class="input-conten">
          <h1 class="input-tit">{{ $t('output') }}</h1>
          <!-- <div class="input-info">{{ selectedNode.result }}</div> -->
          <pre class="input-info" v-if="isOutput">{{ formOutput }}</pre>
          <div style="white-space: normal;" class="input-info" v-else>{{ formOutput }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAllStepByDialogId, dialogRecord } from "@/api/app";
import { recordGetRecord } from "@/api/workflow";

export default {
  data() {
    return {
      tabs: 1, // 1: 调用树 2: 火焰图
      isNodeDetails: false,
      wholeProcessList: [],
      treeData: [],//转换后的树形结构
      defaultProps: {
        children: 'children',
        label: 'label' // 假设你的节点对象中有一个name属性作为显示标签
      },
      selectedQuestion: "",
      allItems: [],
      selectedNode:null,
      questions: [], // 存储问题
      treeList:[
          {
            label:"用户输入",
            img:"",
            children:[]
          },
          {
            label:"安全拦截",
            img:"",
            children:[
              {
                label:"风险拦截",
                img:""
              }
            ]
          },
          {
            label:"问题理解",
            img:"",
            children:[
              {
                label:"问题改写",
                img:""
              },
              {
                label:"问题拆解",
                img:""
              }
            ]
          }
        ]
    }
  },
  props: {
    applicationId: {
      type: String,
      default: ""
    },
    conversationId: {
      type: String,
      default: ""
    }
  },
  created() {
    // if (this.questions && this.questions.length > 0) {
    //   this.selectedQuestion = this.questions[this.questions.length - 1];
    // }
  },
  computed: {
    isJson() {
      try {
        JSON.parse(this.selectedNode.prompt) ;
        return true;
      } catch (e) {
        return false;
      }
    },
    formattedData() {
      try {
        const parsed = JSON.parse(this.selectedNode.prompt) ;
        return parsed;
      } catch (e) {
        return this.selectedNode.prompt;
      }
    },
    isOutput() {
      try {
        JSON.parse(this.selectedNode.result) ;
        return true;
      } catch (e) {
        return false;
      }
    },
    formOutput() {
      try {
        const parsed = JSON.parse(this.selectedNode.result) ;
        return parsed;
      } catch (e) {
        return this.selectedNode.result ;
      }
    },
    // questions() {
    //   const questions = this.$store.state.debug.questions.filter(obj => obj.question && obj.question.trim() !== '')
    //     .map(obj => obj.question);
    //   return [...new Set(questions)];
    // }
  },
  mounted() {
    this.getQuestions()
  },
  methods: {
      renderContent(h, { node, data, store }) {
        const iconMapping = {
          '改写环节':require('@/assets/images/node4.png'),
          '改写环节-雅意':require('@/assets/images/node4.png'),
          '问题拆解': require('@/assets/images/problem.png'),
        '内置问题':require('@/assets/images/scene4.png'),
        '拦截yayi判别敏感词':require('@/assets/images/scene2.png'),
        '政策问答库【回答】':require('@/assets/images/node5.png'),
        '超时拦截':require('@/assets/images/node5.png'),
        '汇总所有知识库-原始数据':require('@/assets/images/node2.png'),
        '大模型常识发挥':require('@/assets/images/damoxing.png'),
        '文件库-原始文件数据':require('@/assets/images/node3.png'),
        '多媒体库-原始多媒体数据':require('@/assets/images/node4.png'),
        "政策向量库-向量库数据":require('@/assets/images/node4.png'),
        '结构化数据-原始数据':require('@/assets/images/shujuku.png'),
        '互联网原始数据':require('@/assets/images/shujuku.png'),
        '网页库-原始数据':require('@/assets/images/scene3.png'),
        '附件解析-原始数据':require('@/assets/images/scene2.png'),
        '引用知识库来源-大模型发散':require('@/assets/images/damoxing.png'),
        "政策问答库【问题】":require('@/assets/images/scene2.png'),
        '问答库-重排【问题】':require('@/assets/images/scene2.png'),
        '未指定步骤':require('@/assets/images/node2.png'),
        '汇总-重排':require('@/assets/images/node2.png'),
        // '互联网原始数据':require('@/assets/images/shujuku.png'),
        '汇总-大模型回答':require('@/assets/images/damoxing.png'),
        '讨论话题':require('@/assets/images/discuss.png')
    };
     const imgSuffix = iconMapping[data.label]
        return h('span', [
        h('img', {
          attrs: {
            src: imgSuffix||require('@/assets/images/discuss.png'), // 使用 attrs 来设置属性
            style: 'width: 16px; height: 16px; margin-right: 6px;' // 直接在 attrs 或使用 style 对象
          }
          // 或者使用 style 对象（推荐方式，因为更灵活）
          // style: { width: '16px', height: '16px', marginRight: '6px' }
        }),
        h('span', data.label)
      ])
    },
    
    handleNodeDetails(node) {
      this.isNodeDetails = true
      this.selectedNode = node;
    },
    handleClose() {
      this.isNodeDetails = false
    },
    handelQuestion(selected) {
      const filteredQuestions = this.questions;
      const index = filteredQuestions.indexOf(selected);
      recordGetRecord({
        applicationId: this.applicationId,
        pageNo: index + 1,
        pageSize: 1,
        verifyDeptId: "",
        verifyStatus: "",
        text: "",
        conversationId: this.conversationId,
        deleted: 0,
        userId: "",
      }).then((res) => {
        this.allItems = res.data.list[0]
        this.getDebugList()
        this.isNodeDetails = false
      })
    },
    handleDebugtpye(tabs) {
      this.tabs = tabs;
    },
    getQuestions() {
      dialogRecord({
        applicationId: this.applicationId,
        pageNo: 1,
        pageSize: 5,
      }).then((res) => {
        if (res.code == '000000') {
          const newQuestions = res.data.list.map(item => item.question).filter(q => q && q.trim() !== '');
          // 合并现有问题和新增问题，并去重
          this.questions = [...new Set([...this.questions, ...newQuestions])];
        } else {
          this.questions = []
        }
      })
    },
    // mapIconsToTreeData(treeData) {
    //   return treeData.map((node) => {
    //     return {
    //       ...node,
    //       icon: this.iconMapping[node.type] || require('@/assets/images/shujuku.png'), // 默认图标
    //     };
    //   });
    // },
    getDebugList() {
      getAllStepByDialogId({ dialogId: this.allItems.dialogueId }).then((res) => {
        if (res.code === "000000") {
          this.wholeProcessList = res.data
          const filteredData = res.data.filter(item => item.isShow == 1 && item.step !== '会话id');
          let arrTree = this.buildTreeFromFlatArray(filteredData);  
          this.treeData = arrTree[0].children || []              
        } else {
          this.treeData = [];
        }
      });
    },
    // 
    buildTreeFromFlatArray(arr) {
      const lookup = {};
      const tree = [];
      arr.forEach(node => {
        const children = [];
        lookup[node.id] = {
          label: node.step,
          children: children,
          prompt:node.prompt,
          result:node.result,
          costTime:node.costTime,
          createTime:node.createTime,
          endTime:node.endTime,
          tokenTotal:node.tokenTotal,
          step:node.step
        };
      });

      // arr.forEach(node => {
      //   if (tree.length === 0) {
      //     tree.push(lookup[node.id]);
      //     console.log(tree,'tree');
      //   } else {
      //     const parentNode = tree[tree.length - 1];
      //     console.log(parentNode,'parentNode');
      //     parentNode.children.push(lookup[node.id]);
      //   }
      // });
      // 将外层对象作为children的第一个元素
       // 第一个节点作为根节点
  const rootNode = lookup[arr[0].id];
  tree.push(rootNode);
  const outerNode = {
    label: rootNode.label,
    children: [],
    prompt: rootNode.prompt,
    result: rootNode.result,
    costTime: rootNode.costTime,
    createTime: rootNode.createTime,
    endTime: rootNode.endTime,
    tokenTotal: rootNode.tokenTotal,
    step: rootNode.step
  };
  
  // 先添加外层对象作为第一个子节点
  rootNode.children.unshift(outerNode);
  
  // 然后添加其余节点作为子节点
  for (let i = 1; i < arr.length; i++) {
    rootNode.children.push(lookup[arr[i].id]);
  }
      return tree;
    }
  }
}
</script>

<style lang="scss" scoped>
/* 自定义节点样式 */
.custom-tree-node {
  display: flex;
  align-items: center;
  font-size: 14px;
  padding: 0 4px;
}

/* 文件夹图标颜色 */
.el-icon-error {
  color: #ffb800; /* 橙色 */
}

/* 文件图标颜色 */
.el-icon-error {
  color: #1890ff; /* 蓝色 */
}
.addDebug-conten {
  width: 100%;
  height: 100%;
  padding: 16px;
  position: relative;

  h1 {
    margin-bottom: 0;
  }

  .flex-center {
    display: flex;
    align-items: center;
  }

  .header-top {
    display: flex;
    align-items: center;
    justify-content: space-between;



    .time,
    .res {
      width: 88px;
      height: 40px;
      border-radius: 4px;
      border: 1px solid #E7E7E7;
    }

    .search {
      // width: 336px;
      position: relative;
      height: 40px;
      background: #FFFFFF;
      border-radius: 4px;
      // border: 1px solid #E7E7E7;
    }

    ::v-deep .el-input--suffix .el-input__inner {
      padding-right: 117px;
      padding-left: 38px;
    }

    .text {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494C4F;
      line-height: 20px;
      text-align: left;
      font-style: normal;
    }

    .search-text {
      padding: 4px 8px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #1D2129;
      line-height: 16px;
      text-align: left;
      border-radius: 2px;
      font-style: normal;
      background-color: #EBEEF2;
      text-transform: none;
    }
  }

  .debug-center {
    margin-top: 24px;

    .debug-top {
      display: flex;

      .debug-res,
      .separator {
        height: 24px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 16px;
        color: #1D2129;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        position: relative;
        margin-right: 11px;
      }

      .separator {
        margin-left: 11px;
      }

      .separator::before {
        content: "";
        position: absolute;
        width: 1px;
        height: 16px;
        background: #D8D8D8;
        left: -11px;
        top: 4px;
      }

      .debug-type {
        width: 60px;
        height: 24px;
        background-color: #E8FFE9;
        border-radius: 2px;

      }

      .success {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #009A29;
        text-align: left;
        font-style: normal;
        opacity: 1;
      }
    }

    .debug-text {
      margin-top: 24px;

      .title {
        margin-bottom: 8px;

        .text-left {
          width: 84px;
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #86909C;
          line-height: 20px;
          text-align: left;
          font-style: normal;
          display: inline-block;
        }

        .text-right {
          margin-left: 10px;
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #1D2129;
          line-height: 20px;
          text-align: left;
          font-style: normal;
        }
      }
    }
  }

  .debug-but {
    margin-top: 24px;
    height: 24px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 16px;
    color: #1D2129;
    line-height: 24px;
    text-align: left;
    font-style: normal;
    margin-bottom: 8px;
  }

  ul {
    display: flex;
  }

  li {
    width: 262px;
    height: 28px;
    // background: #F7F8FA;
    box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
    border-radius: 2px;
    text-align: center;
    line-height: 28px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 14px;
    color: #86909C;
    font-style: normal;
  }

  .selected {
    background: #FFFFFF;
    color: #1D2129;
  }

  .debug-bot {
    width: 528px;
    height: auto;
    background: #F7F8FA;
    overflow-y: auto;
    border-radius: 2px;
    max-height: 550px;
    /* 为 el-tree 添加连接线 */
    .el-tree {
      position: relative;
      background: #F7F8FA;
      padding-top: 15px;
      padding-bottom: 15px;
      font-size: 14px;
      font-weight: 400;
    }

    .tree-ctn{
      position: relative;

      .tree-label-ctn{
        height: 32px;
        padding: 8px;
        box-sizing: border-box;
        display: flex;
        align-items: center;
        gap: 8px;

        img{
          width: 16px;
          height: 16px;
        }

        p{
          height: 16px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #1D2129;
          line-height: 16px;
        }
      }

      .tree-child{
        margin-top: 4px;
        display: flex;
        gap: 10px;

        .empty-gap{
          width: 8px;
        }

        .line-gap{
          width: 12px;
          position: relative;

          .line-gap-list{
            width: 12px;
            border: 1px solid #E7E7E7;
            border-top: none;
            border-right:none ;
            border-bottom-left-radius: 8px;
            position: absolute;
          }
        }

        .title-gap{
          display: flex;
          flex-direction: column;
          gap: 4px;
        }
      }
    }

    ::v-deep .el-icon-caret-right:before {
      left: -4px;
      position: absolute;
      top: 4px;
    }

    // .el-tree::before {
    //   content: "";
    //   position: absolute;
    //   top: 10px;
    //   left: 12px;
    //   bottom: 10px;
    //   border-left: 1px solid #ccc;
    // }

    .el-tree-node__label {
      max-width: 485px;
    }

    ::v-deep .el-tree-node {
      position: relative;
      margin: 2px;
      white-space: normal;
    }

    ::v-deep .el-tree-node__content {
      margin-left: 10px;
      // height: 30px;
      // height: auto;
      position: relative;
      font-size: 14px;
      font-weight: 400;
      letter-spacing: 2px;
      padding-top: 3px;
      padding-bottom: 3px;
    }

    /* 节点前的竖线 */
    .el-tree-node__content::before {
      content: "";
      position: absolute;
      top: -10px;
      left: -12px;
      width: 12px;
      height: 1px;
      border-top: 1px solid #ccc;
    }

    /* 最后一个节点的竖线调整 */
    .el-tree-node:last-child::before {
      height: auto;
      bottom: auto;
      top: -10px;
    }

    .el-tree-node__children::before {
      content: "";
      position: absolute;
      top: 10px;
      left: 12px;
      bottom: 10px;
      border-top: 1px solid #ccc;
      background-color: #ccc;
      /* 自定义垂直线颜色 */
    }

    .el-tree-node::before,
    .el-tree-node::after {
      border-color: #ccc;
      /* 自定义水平线颜色 */
    }
  }

  .previewDebugging {
    width: 560px;
    height: 1800px;
    background: #FFFFFF;
    border-radius: 0px 0px 8px 0px;
    // border: 1px solid #e1e4eb;
    // padding: 16px 0 0;
    // box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.1);
    position: absolute;
    right: 560px;
    top: -57px;

    .heaed {
      .heaed-top {
        display: flex;
        height: 56px;
        background-color: #F7F8FA;
        justify-content: space-between;
        padding: 16px 16px 6px;
        .previewAndDebug {
          height: 32px;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 18px;
          color: #1D2129;
          line-height: 32px;
          text-align: left;
          font-style: normal;
        }
      }
    }

    .node-info {
      padding: 16px;

      .title {
        display: flex;
        margin-top: 8px;

        .play {
          width: 32px;
          height: 32px;
          background: #2AC592;
          border-radius: 2px;
          line-height: 38px;
          margin-right: 10px;
        }

        .tit-text {
          height: 24px;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 16px;
          color: #1D2129;
          line-height: 32px;
          text-align: justify;
          font-style: normal;
        }
      }

    }

    .info-conten {
      margin-top: 24px;

      .info-min {
        margin-bottom: 8px;

        .info-type {
          width: 84px;
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #86909C;
          line-height: 20px;
          text-align: left;
          font-style: normal;
          margin-right: 10px;
          display: inline-block;
        }

        .start {
          width: 162px;
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #1D2129;
          line-height: 20px;
          text-align: left;
          font-style: normal;
          display: inline-block;
        }
      }
    }

    .input-conten {
      margin-top: 16px;

      .input-tit {
        height: 24px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 16px;
        color: #1D2129;
        line-height: 24px;
        text-align: left;
        font-style: normal;
      }

      .input-info {
        width: 528px;
        height: calc((100vh - 430px)/2);
        background: #F7F8FA;
        border-radius: 4px;
        border: 1px solid #E7E7E7;
        margin: 8px 0;
        padding: 10px;
        letter-spacing: 2px;
        line-height: 24px;
        overflow-y: auto;
      }
    }
  }
}
</style>