<template>
    <div id="coverCot" style="width: 100%; height: 100%; overflow: hidden">
        <!-- <i
            class="el-icon-d-arrow-right"
            style="
                position: absolute;
                font-size: 20px;
                top: 20px;
                z-index: 1000;
                left: 0;
                cursor: pointer;
            "
            @click="showDrawerFn"
        ></i> -->
        <section class="section-cot" style="width: 100%; height: 100%">
            <div id="container" @click.stop="hideFn" style="width: 100%;">
                <!-- <MenuBar
                    v-if="showContextMenu"
                    ref="menuBar"
                    @callBack="contextMenuFn"
                /> -->
                <header>
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="上一步"
            placement="bottom"
          >
            <iconpark-icon class="iconpark"
            name="arrow-go-back-line" size="24"
          ></iconpark-icon>
          </el-tooltip> 
          <el-tooltip
            class="item"
            effect="dark"
            content="重做"
            placement="bottom"
          >
            <iconpark-icon class="iconpark"
            name="arrow-go-forward-line" size="24"
          ></iconpark-icon>
          </el-tooltip>  -->
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="缩略图"
            placement="bottom"
          >
            <iconpark-icon class="iconpark"
            name="map-2-line" size="24"
          ></iconpark-icon>
          </el-tooltip>  -->
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="项目"
            placement="bottom"
          >
            <i class="el-icon-menu" @click="showDrawerFn()" />
          </el-tooltip> 
         <el-tooltip
            class="item"
            effect="dark"
            content="长按shift多选"
            placement="bottom"
          >
            <i class="el-icon-crop" />
          </el-tooltip> -->
         <el-tooltip
            class="iconpark"
            effect="dark"
            content="放大"
            placement="bottom"
          >
            <!-- <i class="el-icon-zoom-in" /> -->
            <iconpark-icon class="iconpark"
            name="add-line" size="24" @click="zoomFn1(0.2)"
          ></iconpark-icon>
          </el-tooltip>
          <el-tooltip
            class="item"
            style="width: 50px;"
            effect="dark"
            content="缩小"
            placement="bottom"
          >
            <!-- <i class="el-icon-zoom-out" /> -->
            <iconpark-icon class="iconpark"
            name="subtract-line" size="24" @click="zoomFn2(-0.2)"
          ></iconpark-icon>
          </el-tooltip>
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="移动画板"
            placement="bottom"
          >
            <iconpark-icon class="iconpark"
            name="hand" size="24"
          ></iconpark-icon>
          </el-tooltip> -->
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="整理节点"
            placement="bottom"
          >
            <iconpark-icon class="iconpark"
            name="dashboard-line" size="24"
          ></iconpark-icon>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            content="收起面板"
            placement="bottom"
          >
            <iconpark-icon class="iconpark"
            name="flip-horizontal-2-line" size="24"
          ></iconpark-icon>
          </el-tooltip> -->
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="适应屏幕"
            placement="bottom"
          >
            <i class="el-icon-full-screen" @click="centerFn" />
          </el-tooltip> -->
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="运行"
            placement="bottom"
          >
            <i class="el-icon-video-play" @click="run()" />
          </el-tooltip> -->
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="保存"
            placement="bottom"
          >
            <i class="el-icon-upload" @click="saveFn()" />
          </el-tooltip> -->
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="加载保存页面"
            placement="bottom"
          >
            <i class="el-icon-link" @click="loadFn()" />
          </el-tooltip> -->
          <!-- <el-tooltip
            class="item"
            effect="dark"
            content="是否禁用"
            placement="bottom"
          >
            <i
              :class="{ 'el-icon-lock': isLock, 'el-icon-unlock': !isLock }"
              @click="lockFn()"
            />
          </el-tooltip> -->
        </header> 
                <div :id="idDrawCot" class="drawCotStyle" />
                <div class="tabs-cont">
                 
              <AgentPattern v-if="!$route.path.includes('workflow')&&isAdminOrUser" ref="llmRef" :hideLabel="true" :model="type" @updateModel="updateAgentPattern" style="height: 56px;"></AgentPattern>

                  <div class="tabs" :style="{ height: `${tabs.length * 65}px` }" :class="{ 'tabs-cont-active': $route.path.includes('workflow') }">
                    <div
                      v-for="(tab, index) in tabs"
                          :key="index"
                          class="tab"
                          :class="{ active: activeTab === index }"
                          @click="openTabDrawer(tab, index)"
                      >
                        <div class="icon">
                            <iconpark-icon :name="tab.activeIcon"  v-if="activeTab === index" color="#1c50fd" size="24"></iconpark-icon>
                        <iconpark-icon :name="tab.icon" v-else  size="24"></iconpark-icon>
                        </div>
                        <span>{{ tab.label }}</span>
                      </div>
                  </div>
                </div>
                <selectTool v-if="toolSettingVisible"
                @selectToolEmit="selectToolEmit"
                @clickConfig="toolSettingVisible = false"
                :dialogVisible="toolSettingVisible" ref="selectTool" />
                <selectAgent v-if="agentSettingVisible"
                @selectAgentEmit="selectAgentEmit"
                @clickConfig="agentSettingVisible = false"
                :dialogVisible="agentSettingVisible" ref="selectAgent" />
                <selectMcp v-if="selectMcpVisible"
                @selectMcpEmit="selectMcpEmit"
                @clickConfig="selectMcpVisible = false"
                :dialogVisible="selectMcpVisible" ref="selectMcp" />
                <selectIteration v-if="iterationSettingVisible"
                @selectIterationEmit="selectIterationEmit"
                @clickConfig="iterationSettingVisible = false"
                :dialogVisible="iterationSettingVisible" :componentId="params.componentId" ref="selectIteration" />
                <nodeDrawer ref="nodeDrawer" :type="type" @addNode="addNode" v-show="isAdminOrUser"/>
                <nodeManageDrawer ref="nodeManageDrawer"  />
                <funcDrawer ref="funcDrawer" :component="params"/>
                <!-- 单节点测试 -->
                <singleNodetestingDrawer
                    ref="singleNodetestingDrawer"
                    :componentParams="params"
                />
                <runDrawer
                    ref="runDrawer"
                    :startDrawerData="startDrawerData"
                    :endNode="endNode"
                    :componentParams="params"
                />
                <previewDrawer
                    ref="previewDrawer"
                    :componentParams="params"
                />
                <showDrawer ref="showDrawer" :component="params"/>
                <startDrawer
                    ref="startDrawer"
                    :componentParams="params"
                    @updateAppForm="updateAppForm"
                    @updateVariables="updateVariables"
                    @getTriggerData = "getTriggerData"
                />
                <conditionDrawer
                    ref="conditionDrawer"
                    @addPorts="addConditionPorts"
                    @removePorts="removePorts"
                    @updateConditionData="updateConditionData"
                    :graph="graph"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <questionClassifyDrawer
                    ref="questionClassifyDrawer"
                    @addPorts="addQaTypePorts"
                    @removePorts="removeQaTypePorts"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <endDrawer ref="endDrawer" :sourceData="nodeSource" @updateAppForm="updateAppForm" @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"/>
                <knowBaseDrawer
                    ref="knowBaseDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <datasetDrawer
                    ref="datasetDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <sceneManageDrawer
                    ref="sceneManageDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <apiDrawer
                    ref="apiDrawer"
                    :sourceData="nodeSource"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <diedaiDrawer
                    ref="diedaiDrawer"
                    :sourceData="nodeSource"
                    @updateAppForm="updateAppForm"
                    @updateCellData="updateCellData"
                    :params="params"
                />
                <toolDrawer
                    ref="toolDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <existAgentDrawer
                    ref="existAgentDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <globalConditionDrawer
                    ref="globalConditionDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <iterationDrawer
                    ref="iterationDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <changqijiyiDrawer
                    ref="changqijiyiDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <wendaDrawer
                    ref="wendaDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <xiaoxiDrawer
                    ref="xiaoxiDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <shujukuDrawer
                    ref="shujukuDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <yitushibieDrawer
                    ref="yitushibieDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <textParsingDrawer
                    ref="textParsingDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <agentDrawer
                    ref="agentDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <codeDrawer
                    ref="codeDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <customDrawer
                    ref="customDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <variableDrawer
                    ref="variableDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    @updateVariables="updateVariables"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <fileParsingDrawer
                    ref="fileParsingDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <paramsFilterDrawer
                    ref="paramsFilterDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    @updatefilterVariables="updatefilterVariables"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <interceptWordDrawer
                    ref="interceptWordDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <aiImageDrawer
                    ref="aiImageDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <aiVideoDrawer
                    ref="aiVideoDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <knowledgeAddDrawer
                    ref="knowledgeAddDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
				<sqlAddDrawer
				    ref="sqlAddDrawer"
				    @updateCellData="updateCellData"
				    @updateAppForm="updateAppForm"
				    :params="params"
				    :sourceData="nodeSource"
				    @remove="handleRemove"
				    @copy="copyHandler"
				    @input="inputHandler"
				/>
                <mcpDrawer
                    ref="mcpDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                <modelDrawer
                    ref="modelDrawer"
                    @updateCellData="updateCellData"
                    @updateAppForm="updateAppForm"
                    :params="params"
                    :sourceData="nodeSource"
                    @remove="handleRemove"
                    @copy="copyHandler"
                    @input="inputHandler"
                />
                
            </div>
        </section>
        <DialogCondition ref="dialogCondition"></DialogCondition>
        <DialogMysql ref="dialogMysql"></DialogMysql>
    </div>
</template>

<script>
import { Graph, Path } from "@antv/x6";
import { getNodesAndEdgesConfig } from "@/views/workflowConfig/dragDemo/components/nodeConfig.js";
import "@antv/x6-vue-shape";
import AgentPattern from '@/views/appManage/components/agentPattern.vue';

import model from "./components/nodeTheme/model.vue";
import knowledge from "./components/nodeTheme/knowledge.vue";
import dataset from "./components/nodeTheme/dataset.vue"
import knowledgeAdd from "./components/nodeTheme/knowledgeAdd.vue";
import sqlAdd from "./components/nodeTheme/sqlAdd.vue";
import sceneManage from "./components/nodeTheme/sceneManage.vue";
import tool from "./components/nodeTheme/tool.vue";
import existAgent from "./components/nodeTheme/existAgent.vue";
import globalCondition from "./components/nodeTheme/globalCondition.vue";
import iteration from "./components/nodeTheme/iteration.vue";
import mcp from "./components/nodeTheme/mcp.vue";
import textParsing from "./components/nodeTheme/textParsing.vue";
import agent from "./components/nodeTheme/agent.vue";
import custom from "./components/nodeTheme/custom.vue";
import http from "./components/nodeTheme/http.vue";
import diedai from "./components/nodeTheme/diedai.vue";
import paramsFilter from "./components/nodeTheme/paramsFilter.vue";
import interceptWord from "./components/nodeTheme/interceptWord.vue";
import aiImage from "./components/nodeTheme/aiImage.vue";
import aiVideo from "./components/nodeTheme/aiVideo.vue";
import condition from "./components/nodeTheme/condition.vue";
import qaType from "./components/nodeTheme/qaType.vue";
import onlyout from "./components/nodeTheme/onlyOut.vue";
import onlyin from "./components/nodeTheme/onlyIn.vue";
import DataJson from "./components/data";
import MenuBar from "./components/menuBar";
import selectTool from "./components/selectTool";
import selectAgent from "./components/selectAgent";
import selectMcp from "./components/selectMcp";
import selectIteration from "./components/selectIteration";
import nodeDrawer from "./components/nodeDrawer";
import nodeManageDrawer from "./components/nodeManageDrawer";
import funcDrawer from "./components/funcDrawer";
import showDrawer from "./components/showDrawer";
import startDrawer from "./components/startDrawer";
import conditionDrawer from "./components/conditionDrawer";
import runDrawer from "./components/runDrawer";
import previewDrawer from "./components/previewDrawer";
import singleNodetestingDrawer from "./components/singleNodetestingDrawer";
import toolDrawer from "./components/toolDrawer";
import existAgentDrawer from "./components/existAgentDrawer";
import globalConditionDrawer from "./components/globalConditionDrawer";
import iterationDrawer from "./components/iterationDrawer";
import textParsingDrawer from "./components/textParsingDrawer";
import agentDrawer from "./components/agentDrawer";
import changqijiyiDrawer from "./components/changqijiyiDrawer";
import wendaDrawer from "./components/wendaDrawer";
import xiaoxiDrawer from "./components/xiaoxiDrawer";
import shujukuDrawer from "./components/shujukuDrawer";
import yitushibieDrawer from "./components/yitushibieDrawer";
import codeDrawer from "./components/codeDrawer";
import customDrawer from "./components/customDrawer";
import variableDrawer from "./components/variableDrawer";
import fileParsingDrawer from "./components/fileParsingDrawer";
import questionClassifyDrawer from "./components/questionClassifyDrawer";
import endDrawer from "./components/endDrawer";
import knowBaseDrawer from "./components/knowBaseDrawer";
import datasetDrawer from "./components/datasetDrawer.vue";
import sceneManageDrawer from "./components/sceneManageDrawer";
import paramsFilterDrawer from "./components/paramsFilterDrawer";
import interceptWordDrawer from "./components/interceptWordDrawer";
import aiImageDrawer from "./components/aiImageDrawer";
import aiVideoDrawer from "./components/aiVideoDrawer";
import knowledgeAddDrawer from "./components/knowledgeAddDrawer";
import mcpDrawer from "./components/mcpDrawer";
import sqlAddDrawer from "./components/sqlAddDrawer";
import modelDrawer from "./components/modelDrawer";
import apiDrawer from "./components/apiDrawer";
import diedaiDrawer from "./components/diedaiDrawer";
import DialogCondition from "./components/dialog/condition.vue";
import DialogMysql from "./components/dialog/mysql.vue";

const nodeStatusList = [
    [
        {
            id: "1",
            status: "running",
        },
        {
            id: "2",
            status: "default",
        },
        {
            id: "3",
            status: "default",
        },
        {
            id: "4",
            status: "default",
        },
    ],
    [
        {
            id: "1",
            status: "success",
        },
        {
            id: "2",
            status: "running",
        },
        {
            id: "3",
            status: "default",
        },
        {
            id: "4",
            status: "default",
        },
    ],
    [
        {
            id: "1",
            status: "success",
        },
        {
            id: "2",
            status: "success",
        },
        {
            id: "3",
            status: "running",
        },
        {
            id: "4",
            status: "running",
        },
    ],
    [
        {
            id: "1",
            status: "success",
        },
        {
            id: "2",
            status: "success",
        },
        {
            id: "3",
            status: "success",
        },
        {
            id: "4",
            status: "failed",
        },
    ],
];

export default {
    name: "App",
    props: {
        params: Object,
        canvas: String,
        tabs: Array,
        type: String,
    },
    components: {
        MenuBar,
        selectTool,
        selectAgent,
        selectMcp,
        AgentPattern,
        selectIteration,
        nodeDrawer,
        nodeManageDrawer,
        funcDrawer,
        showDrawer,
        startDrawer,
        conditionDrawer,
        toolDrawer,
        existAgentDrawer,
        globalConditionDrawer,
        textParsingDrawer,
        agentDrawer,
        changqijiyiDrawer,
        wendaDrawer,
        xiaoxiDrawer,
        shujukuDrawer,
        yitushibieDrawer,
        codeDrawer,
        customDrawer,
        variableDrawer,
        fileParsingDrawer,
        iterationDrawer,
        runDrawer,
        previewDrawer,
        questionClassifyDrawer,
        endDrawer,
        paramsFilterDrawer,
        interceptWordDrawer,
        aiImageDrawer,
        aiVideoDrawer,
        knowledgeAddDrawer,
		sqlAddDrawer,
        mcpDrawer,
        knowBaseDrawer,
        datasetDrawer,
        sceneManageDrawer,
        modelDrawer,
        apiDrawer,
        diedaiDrawer,
        DialogCondition,
        DialogMysql,
        singleNodetestingDrawer,
        mcp
    },
    data() {
        return {
            toolSettingVisible:false,
            agentSettingVisible:false,
            selectMcpVisible:false,
            iterationSettingVisible:false,
            idDrawCot:'drawCot',
            nodeOption:{},
            activeTab: 0,
            startDrawerData: [],
            startNode: {},
            endNode: [],
            parentNodes: [],
            childNodes: [],
            graph: "",
            timer: "",
            isLock: false,
            showContextMenu: false,
            drawerObj:{
              'output':'startDrawer',
              'onlyIn':'endDrawer',
              'condition':'conditionDrawer',
              'qaType':'questionClassifyDrawer',
              'knowledge':'knowBaseDrawer',
              'sceneManage':'sceneManageDrawer',
              'model':'modelDrawer',
              'http':'apiDrawer',
              'diedai':'diedaiDrawer',
              'paramsFilter':'paramsFilterDrawer',
              'interceptWord':'interceptWordDrawer',
              'knowledgeAdd':'knowledgeAddDrawer',
			  'sqlAdd':'sqlAddDrawer',
              'mcp': 'mcpDrawer',
              'iteration':'iterationDrawer',
              'textParsing':'textParsingDrawer',
              'agent':'agentDrawer',
              'changqijiyi':'changqijiyiDrawer',
              'wenda':'wendaDrawer',
              'xiaoxi':'xiaoxiDrawer',
              'shujuku':'shujukuDrawer',
              'yitushibie':'yitushibieDrawer',
              'code':'codeDrawer',
              'custom':'customDrawer',
              'variable':'variableDrawer',
              'fileParsing':'fileParsingDrawer',
              'tool':'toolDrawer',
              'existAgent':'existAgentDrawer',
              'globalCondition':'globalConditionDrawer',
              'deduce':'toolDrawer',
              'aiImage':'aiImageDrawer',
              'aiVideo':'aiVideoDrawer',
              'dataset':'datasetDrawer'
            },
            nodeSource: {},
        };
    },
    computed:{
        isAdminOrUser(){
            let obj=JSON.parse(sessionStorage.getItem("user"))
            console.log(obj.accountName,this.params.applicationInfo.createUser,666);
            return obj.powerType==0 || obj.accountName == this.params.applicationInfo.createUser
        }
    },
    created() {
        this.idDrawCot = 'drawCot' + this.type;
    },
    mounted() {
        // 初始化 graph
        this.initGraph();
        // 按钮绑定
        this.keyBindFn();
        // 执行
        this.loadFn();
        this.$EventBus.$on("removeNode", (res) => {
            this.colseAllDrawer();
        });
        // // 创建副本
        // this.$EventBus.$on("copyHandler", (node) => {
        //     let num = 1;
        //     const nodeId = node.id;
        //    // 重命名
        //    const name = `${node?.data?.label}_${num}`
        //    this.handleCopy(nodeId, name);
        // });
        // 打开测试节点弹窗
        this.$EventBus.$on("openRunDrawer", (node) => {
            if(this.$refs.singleNodetestingDrawer) {
                let startNode = this.getNodesAndEdges(1,this.params).nodes.find(item => item.nodeType === 1)
                this.startDrawerData = JSON.stringify(startNode.output);
                this.colseLeftAllDrawer();
                this.$refs.singleNodetestingDrawer.openDrawer()
                this.$refs.singleNodetestingDrawer.nodeData = node;
                this.$refs.singleNodetestingDrawer.runType = '1';
                // 更换节点测试 清空输入框内容
                // if(this.startNode?.data?.variables) {
                //     // 更换节点测试 清空输入框内容
                //     const list = Array.isArray(this.startNode?.data?.variables) ? this.startNode?.data?.variables : JSON.parse(this.startNode?.data?.variables)
                //     list?.forEach(item => {
                //         item.value = "";
                //     })
                // }
                this.$refs.singleNodetestingDrawer.outputJsonData = "";
            }
        });
        
    },
    beforeDestroy() {
        this.$EventBus.$off("removeNode");
        this.$EventBus.$off('saveWorkflowAgain')
    },
    methods: {
      getNodesAndEdges(flag, component) {
        //判断是否有重复条件，场景
        const allNodes = this.graph.getNodes();
        const allNodeIds = allNodes.map((item) => item.id);
        let obj = JSON.parse(JSON.stringify(this.$store.state.workflow.mulAgentParams));
        const keys = Object.keys(obj);
        for (const key in obj) {
          if (!allNodeIds.includes(key)) {
            delete obj[key];
          }
        }
        const values = Object.values(obj);
        if(values.length !== new Set(values).size){
          this.$message.warning('节点存在相同的全局条件或者使用场景');
          return false
        }
        return getNodesAndEdgesConfig(flag, component, this.graph)
      },

      updateAgentPattern(val) {
          this.colseAllDrawer()
          this.colseLeftAllDrawer()
          this.params.type = val;
          this.$store.commit('setWorkFlowType', val)
          this.$EventBus.$emit("updateApplicationType", val);
        },
        // 删除节点
        handleRemove(node) {
            this.graph.removeNode(node)
            this.$refs.modelDrawer.closeDrawer();
        },
        // 创建副本
        copyHandler(node) {
            let num = 1;
            const nodeId = node.id;
           // 重命名
           const name = `${node?.data?.label}_${num}`
           this.handleCopy(nodeId, name);
        },
        // 重命名
        inputHandler(name, node) {
            const nodeId = node.id;
            this.handleInput(nodeId, name)
            this.nodeSource.label = name;
        },
        // 重命名方法
        handleInput(nodeId, name) {
            // 获取节点的数据模型
            const cell = this.graph.getCellById(nodeId);
            cell.prop('data/label', name)
        },
        // 创建副本方法
        handleCopy(nodeId, name) {
           // nodeId: 获取要复制的节点的ID
           // 获取节点的数据模型
           const oldNode = this.graph.getCell(nodeId);
           if(oldNode) {
               // 创建一个新的节点数据模型
               const newNode = oldNode?.clone();
               // 重命名
               newNode.data.label = name
               // graph.updateCell(newNode, { label: newLabel })
               // 设置新节点的位置，避免覆盖原节点
               newNode.position(oldNode.position().x + 30,oldNode.position().y + 30)
               // 添加新节点到图中
               this.graph.addNode(newNode);
           }
           
        },
        openTabDrawer(tab, index) {
          this.colseAllDrawer()
          if(tab.label === '调试'){
             this.$EventBus.$emit("saveWorkflowAgain");
          }
          if(tab.label === '测试'){
            let startNode = this.getNodesAndEdges(1,this.params).nodes.find(item => item.nodeType === 1);
            this.startDrawerData = JSON.stringify(startNode.output);
          }
          if(this.activeTab === index){
            this.$refs[tab.ref].closeDrawer();
            this.activeTab = -1;
          } else {
            console.log(tab,this.isAdminOrUser);
            
            this.colseLeftAllDrawer(tab)
            this.activeTab = index;
            this.$refs[tab.ref].openDrawer();
          }

        },
        updateVariables(data, node) {
            let cell = this.graph.getCellById(node.id);
            cell.setData(data);
            setTimeout(() => {
                let height = document.getElementById(node.id) && document.getElementById(node.id) && document.getElementById(node.id).offsetHeight
                cell.resize(296, height);
              }, 500);
        },
        getTriggerData(data, node) {
            let cell = this.graph.getCellById(node.id);
            cell.setData(data);
        },
        getNodeById(id) {
            return this.graph.getCellById(id);
        },
        hideFn() {
            this.showContextMenu = false;
        },
        getParentArr(id, data) {
            let edgeArr = data.filter((el) => el.shape === "dag-edge");
            let parentNode = edgeArr.find((el) => el.target.cell === id);
            if (parentNode) {
                this.parentNodes.push(
                    data.find((el) => el.id === parentNode.source.cell)
                );
                this.getParentArr(parentNode.source.cell, data);
            }
        },
        initGraph() {
            let that = this;
            // 注册节点
            Graph.registerNode(
                "dag-condition",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<condition @copy="copyHandler" @input="inputChange" />`,
                        // template: `<condition />`,
                        components: {
                            condition,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-knowledge",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<knowledge @copy="copyHandler" @input="inputChange" />`, 
                        // template: `<knowledge />`,
                        components: {
                            knowledge,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-knowledgeAdd",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<knowledgeAdd @copy="copyHandler" @input="inputChange" />`, 
                        // template: `<knowledgeAdd />`,
                        components: {
                            knowledgeAdd,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
			Graph.registerNode(
                "dag-dataset",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<dataset @copy="copyHandler" @input="inputChange" />`, 
                        // template: `<dataset />`,
                        components: {
                            dataset,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
			    "dag-sqlAdd",
			    {
			        inherit: "vue-shape",
			        width: 'auto',
			        height: 'auto',
			        component: {
			            template: `<sqlAdd @copy="copyHandler" @input="inputChange" />`, 			           
			            components: {
			                sqlAdd,
			            },
			            methods: {
			                copyHandler(option) {                                
			                    let num = 1;
			                    const nodeId = option.id;
			                    // 重命名
			                    const name = `${option?.data?.label}_${num}`
			                    _this.handleCopy(nodeId, name);
			                },
			                inputChange(name, data) {
			                    const nodeId = data.id;
			                    _this.handleInput(nodeId, name)
			                    _this.nodeSource.label = name;
			                }
			            },
			        },
			        ports: {
			            groups: {},
			        },
			    },
			    true
			);
            Graph.registerNode(
                "dag-sceneManage",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<sceneManage @copy="copyHandler" @input="inputChange" />`, 
                        // template: `<sceneManage />`,
                        components: {
                            sceneManage,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-tool",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<tool @copy="copyHandler" @input="inputChange" />`,
                        // template: `<tool />`,
                        components: {
                            tool,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-existAgent",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<existAgent @copy="copyHandler" @input="inputChange" />`,
                        // template: `<existAgent />`,
                        components: {
                            existAgent,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-globalCondition",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<globalCondition @copy="copyHandler" @input="inputChange" />`,
                        // template: `<globalCondition />`,
                        components: {
                            globalCondition,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-iteration",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<iteration @copy="copyHandler" @detail="detailHandler" @input="inputChange" />`,
                        // template: `<iteration />`,
                        components: {
                          iteration,
                        },
                        methods: {
                            copyHandler(option) {     
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            detailHandler(option) {   
                                const componentId = option.store.data.data.toolData.componentId;
                                const routeUrl = window.location.origin + window.location.pathname + '#/workflow/workFlowEdit?componentId=' + componentId;
                                  window.open(routeUrl, '_blank');
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-textParsing",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<textParsing @copy="copyHandler" @input="inputChange" />`,
                        // template: `<textParsing />`,
                        components: {
                          textParsing,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-agent",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<agent @copy="copyHandler" @input="inputChange" />`,
                        components: {
                          agent,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-custom",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<custom @copy="copyHandler" @input="inputChange" />`,
                        // template: `<custom />`,
                        components: {
                          custom,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-http",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        // template: `<http />`,
                        template: `<http @copy="copyHandler" @input="inputChange" />`,
                        components: {
                            http,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {
                            left: {
                                position: "left",
                                attrs: {
                                    circle: {
                                        r: 4,
                                        magnet: true,
                                        stroke: "#C2C8D5",
                                        strokeWidth: 1,
                                        fill: "#fff",
                                    },
                                },
                            },
                            right: {
                                position: "right",
                                attrs: {
                                    circle: {
                                        r: 4,
                                        magnet: true,
                                        stroke: "#C2C8D5",
                                        strokeWidth: 1,
                                        fill: "#fff",
                                    },
                                },
                            },
                        },
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-diedai",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<diedai @copy="copyHandler" @input="inputChange" />`,
                        components: {
                            diedai,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
              "dag-paramsFilter",
                {
                  inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<paramsFilter @copy="copyHandler" @input="inputChange" />`,
                        // template: `<paramsFilter />`,
                        components: {
                            paramsFilter,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-interceptWord",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<interceptWord @copy="copyHandler" @input="inputChange" />`,
                        // template: `<interceptWord />`,
                        components: {
                            interceptWord,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-aiImage",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<aiImage @copy="copyHandler" @input="inputChange" />`,
                        // template: `<aiImage />`,
                        components: {
                            aiImage,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-aiVideo",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<aiVideo @copy="copyHandler" @input="inputChange" />`,
                        // template: `<aiVideo />`,
                        components: {
                            aiVideo,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-mcp",
                {
                    inherit: "vue-shape",
                    width: 'auto',
                    height: 'auto',
                    component: {
                        template: `<mcp @copy="copyHandler" @input="inputChange" />`,
                        // template: `<MCP />`,
                        components: {
                            mcp,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-qaType",
                {
                    inherit: "vue-shape",
                    width: 296,
                    height: 36,
                    // attrs: {
                    //   label: {
                    //     text: '问题选择器',    // 文本
                    //     fill: '#333',    // 文字颜色
                    //     fontSize: 14,    // 文字大小
                    //   },
                    // },
                    component: {
                        template: `<qaType @copy="copyHandler" @input="inputChange" />`,
                        // template: `<qaType />`,
                        components: {
                            qaType,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                        },
                    },
                    ports: {
                        groups: {},
                    },
                },
                true
            );
            Graph.registerNode(
                "dag-output",
                {
                    inherit: "vue-shape",
                    width: 100,
                    component: {
                        template: `<onlyout />`,
                        components: {
                            onlyout,
                        },
                    },
                    ports: {
                        groups: {
                            right: {
                                position: "right",
                                attrs: {
                                    circle: {
                                        r: 4,
                                        magnet: true,
                                        stroke: "#C2C8D5",
                                        strokeWidth: 1,
                                        fill: "#fff",
                                    },
                                },
                            },
                        },
                    },
                },
                true
            );

            Graph.registerNode(
                "dag-onlyIn",
                {
                    inherit: "vue-shape",
                    width: 100,
                    height: 36,
                    component: {
                        template: `<onlyin @copy="copyHandler" @input="inputChange" />`,
                        components: {
                            onlyin,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                         },
                    },
                    ports: {
                        groups: {
                            left: {
                                position: "left",
                                attrs: {
                                    circle: {
                                        r: 4,
                                        magnet: true,
                                        stroke: "#C2C8D5",
                                        strokeWidth: 1,
                                        fill: "#fff",
                                    },
                                },
                            },
                        },
                    },
                },
                true
            );

            const _this = this

            Graph.registerNode(
                "dag-node",
                {
                    inherit: "vue-shape",
                    width: 294,
                    height: 36,
                    component: {
                        template: `<model @copy="copyHandler" @input="inputChange" />`,
                        // template: `<model />`,
                        components: {
                            model,
                        },
                        methods: {
                            copyHandler(option) {                                
                                let num = 1;
                                const nodeId = option.id;
                                // 重命名
                                const name = `${option?.data?.label}_${num}`
                                _this.handleCopy(nodeId, name);
                            },
                            inputChange(name, data) {
                                const nodeId = data.id;
                                _this.handleInput(nodeId, name)
                                _this.nodeSource.label = name;
                            }
                         },
                    },
                    ports: {
                        groups: {
                            left: {
                                position: "left",
                                attrs: {
                                    circle: {
                                        r: 4,
                                        magnet: true,
                                        stroke: "#C2C8D5",
                                        strokeWidth: 1,
                                        fill: "#fff",
                                    },
                                },
                            },
                            right: {
                                position: "right",
                                attrs: {
                                    circle: {
                                        r: 4,
                                        magnet: true,
                                        stroke: "#C2C8D5",
                                        strokeWidth: 1,
                                        fill: "#fff",
                                    },
                                },
                            },
                        },
                    },
                },
                true
            );

            Graph.registerEdge(
                "dag-edge",
                {
                    inherit: "edge",
                    attrs: {
                        line: {
                            stroke: "#C2C8D5",
                            strokeWidth: 2,
                        },
                    },

                    zIndex: 1000,
                },
                true
            );
            Graph.registerConnector(
                "algo-connector",
                (s, e) => {
                    const offset = 4;
                    const deltaY = Math.abs(e.y - s.y);
                    const control = Math.floor((deltaY / 3) * 2);

                    const v1 = { x: s.x, y: s.y + offset + control };
                    const v2 = { x: e.x, y: e.y - offset - control };

                    return Path.normalize(
                        `M ${s.x} ${s.y}
           L ${s.x} ${s.y + offset}
           C ${v1.x} ${v1.y} ${v2.x} ${v2.y} ${e.x} ${e.y - offset}
           L ${e.x} ${e.y}
          `
                    );
                },
                true
            );

            const graph = new Graph({
                grid: {
                    size: 10,
                    visible: true,
                    type: "dot", // 'dot' | 'fixedDot' | 'mesh'
                    args: {
                        color: "#DFE3E8", // 网格线/点颜色
                        thickness: 1, // 网格线宽度/网格点大小
                    },
                },
                embedding: {
                  enabled: true,
                  findParent({ node }) {
                    const bbox = node.getBBox()
                    return this.getNodes().filter((subNode) => {
                      const data = subNode.getData()
                      if (data && data.parent) {
                        const targetBBox = subNode.getBBox()
                        //迭代节点不能拖入
                        if(node.store.data.data.type === "diedai"){
                          return false
                        }
                        return bbox.isIntersectWithRect(targetBBox)
                      }
                      return false
                    })
                  },
                },
                background: {
                    color: "#F0F4FA", // 设置画布背景颜色
                },
                container: document.getElementById(this.idDrawCot),
                panning: {
                    enabled: true,
                    eventTypes: ["leftMouseDown", "mouseWheel"],
                },
                mousewheel: {
                    enabled: true,
                    modifiers: "ctrl",
                    factor: 1.1,
                    maxScale: 1.5,
                    minScale: 0.5,
                },
                highlighting: {
                    magnetAdsorbed: {
                        name: "stroke",
                        args: {
                            attrs: {
                                fill: "#fff",
                                stroke: "red",
                                strokeWidth: 10,
                            },
                        },
                    },
                },
                connecting: {
                    snap: true,
                    allowBlank: false,
                    allowNode: false,
                    allowLoop: false,
                    highlight: true,
                    connector: "rounded",
                    router:{
                      name: 'er',
                      args:{
                          direction:'L'
                      }
                    },
                    connectionPoint: "anchor",
                    anchor: "center",
                    validateMagnet() {
                        // return magnet.getAttribute('port-group') !== 'top'

                        // 限制连线配置
                        return true;
                    },
                    createEdge() {
                        return graph.createEdge({
                            shape: "dag-edge",
                            zIndex: 1000,
                        });
                    },
                },
                selecting: {
                    enabled: true,
                    multiple: true,
                    rubberEdge: true,
                    rubberNode: true,
                    modifiers: "shift",
                    rubberband: true,
                },
                keyboard: true,
                clipboard: true,
                history: true,
            });
            this.graph = graph;
            graph.on("blank:click", ({ e, x, y }) => {
                this.colseAllDrawer();
                this.colseLeftAllDrawer();
            });
            
            // 监听节点的 mouseenter 事件
            graph.on('node:mouseenter', ({ node }) => {
              const ports = node.getPorts();
              ports.forEach((port) => {
                node.portProp(port.id, 'attrs/circle/r', 6); // 放大半径
              });
            });

            // 监听节点的 mouseleave 事件
            graph.on('node:mouseleave', ({ node }) => {
              const ports = node.getPorts();
              ports.forEach((port) => {
                node.portProp(port.id, 'attrs/circle/r', 4); // 恢复默认半径
              });
            });
            graph.on("node:moved", ({ e, x, y }) => {
                // 节点移动
                this.$EventBus.$emit("saveWorkflow");
            });
            graph.on("edge:contextmenu", ({ e, x, y, edge, view }) => {
                this.showContextMenu = true;
                this.$nextTick(() => {
                    this.$refs.menuBar.initFn(e.offsetX, e.offsetY, {
                        type: "edge",
                        item: edge,
                    });
                });
            });
            graph.on("edge:selected", ({ cell }) => {
                cell.removeTools();
                cell.attr("line", { stroke: "#1c50fd", strokeWidth: 2 });
            });
            graph.on("edge:unselected", ({ cell }) => {
                cell.removeTools();
                cell.attr("line", { stroke: "#C2C8D5", strokeWidth: 2 });
            });
            graph.on("node:click", ({ e, x, y, node, view, cell }) => {
                let nodeType = node.store.data.data.type;
                if(this.type === 'multi_agent' && nodeType === "output"){
                  return
                }
                this.parentNodes = [];
                let allAncestors = this.getAncestors(cell.id)
                let allNode = this.graph.getNodes()
                this.childNodes = allNode.filter(item => {
                    return item.store.data.parent === cell.id
                })
                this.parentNodes = this.graph.toJSON().cells.filter(item => {
                    return allAncestors.includes(item.id)
                })
                // if(e.target.tagName !== 'ICONPARK-ICON' && e.target.tagName !== 'I'){
                //   this.graph.centerCell(cell);
                // }
                // graph.positionCell(node,'center', {
                //   padding: { top: 20, right: 20, bottom: 20, left: 20 }, // 边距
                //   animation: {
                //     duration: 2000, // 动画时长
                //   },
                // });
                
                this.nodeSource = node.data;
                this.colseAllDrawer();
                this.colseLeftAllDrawer();
                this.activeTab = -1
                //获取当前节点parentNodes
                //this.getParentArr(node.id, this.graph.toJSON().cells);
                this.parentNodes = this.parentNodes.filter(
                    (el) => el.shape !== "dag-condition"
                );
                this.childNodes = this.childNodes.filter(
                    (el) => el.shape !== "dag-condition"
                );
                //this.parentNodes = this.parentNodes.reverse();
                this.$store.commit("setEditNode", node);
                this.$store.commit("setParentNodes", this.parentNodes);
                this.$store.commit("setChildNodes", this.childNodes);
                if(e.target.className != 'el-input__inner' && e.target.className != 'open'  && e.target.closest('.running-result') === null){
                    console.log(this.drawerObj[nodeType]);
                    
                  this.$refs[this.drawerObj[nodeType]].openDrawer(nodeType);
                }
            });

            graph.on("node:contextmenu", ({ e, x, y, node, view }) => {
                this.showContextMenu = true;

                this.$nextTick(() => {
                    // this.$refs.menuBar.setItem({ type: 'node', item: node })
                    const p = graph.localToPage(x, y);
                    this.$refs.menuBar.initFn(p.x, p.y, {
                        type: "node",
                        item: node,
                    });
                });
            });

            graph.on("edge:connected", ({ edge }) => {
                that.colseAllDrawer();
                const sourceNodeId = edge.getSource().cell;
                const source = graph.getCellById(edge.source.cell);
                const target = graph.getCellById(edge.target.cell);
                const edges = graph.getEdges();
                if(that.type === 'multi_agent' && source.store.data.shape === "dag-output"){
                    // 查找当前 sourceNodeId 已有的输出边
                    const existingOutgoingEdges = edges.filter((e) => {
                      const source = e.getSource();
                      return source.cell === sourceNodeId;
                    });

                    // 如果已有至少一条输出边，删除新建的这条边
                    if (existingOutgoingEdges.length > 1) {
                      graph.removeCell(edge);
                      that.$message.warning('该节点已有一个输出连接，无法再添加新的输出边');
                    }
                }
                // 只允许输入
                if (target.data.type == "output") {
                    return graph.removeEdge(edge.id);
                }

                // 只允许输出
                if (source.data.type == "onlyIn") {
                    return graph.removeEdge(edge.id);
                }

                // 如果线源头的一端链接桩只允许输入
                if (/in/.test(edge.source.port)) {
                    return graph.removeEdge(edge.id);
                }

                // 目标一端链接桩只允许输出
                if (/out/.test(edge.target.port)) {
                    return graph.removeEdge(edge.id);
                }

                if (source.data.type == "condition") {
                    if (
                        target.data.t === edge.id ||
                        target.data.f === edge.id
                    ) {
                        return graph.removeEdge(edge.id);
                    }
                }

                edge.attr({
                    line: {},
                });
                // 节点与节点之间连线时触发保存
                this.$EventBus.$emit("saveWorkflow");
            });

            graph.on("node:change:data", ({ node }) => {
                const edges = graph.getIncomingEdges(node);
                const { status } = node.getData();
                edges?.forEach((edge) => {
                    if (status === "running") {
                        edge.attr(
                            "line/style/animation",
                            "running-line 30s infinite linear"
                        );
                    } else {
                        edge.attr("line/style/animation", "");
                    }
                });
            });
            graph.on("node:removed", ({ view, e }) => {
              that.colseAllDrawer();
            });
            let ctrlPressed = false
            const embedPadding = 20

            graph.on('node:embedding', (e) => {
              ctrlPressed = e.metaKey || e.ctrlKey
            })

            graph.on('node:embedded', () => {
              ctrlPressed = false
            })

            graph.on('node:change:size', ({ node, options }) => {
              if (options.skipParentHandler) {
                return
              }

              const children = node.getChildren()
              if (children && children.length) {
                node.prop('originSize', node.getSize())
              }
            })

            graph.on('node:change:position', ({ node, options }) => {
              if (options.skipParentHandler || ctrlPressed) {
                return
              }

              const children = node.getChildren()
              if (children && children.length) {
                node.prop('originPosition', node.getPosition())
              }

              const parent = node.getParent()
              if (parent && parent.isNode()) {
                let originSize = parent.prop('originSize')
                if (originSize == null) {
                  originSize = parent.getSize()
                  parent.prop('originSize', originSize)
                }

                let originPosition = parent.prop('originPosition')
                if (originPosition == null) {
                  originPosition = parent.getPosition()
                  parent.prop('originPosition', originPosition)
                }

                let x = originPosition.x
                let y = originPosition.y
                let cornerX = originPosition.x + originSize.width
                let cornerY = originPosition.y + originSize.height
                let hasChange = false

                const children = parent.getChildren()
                if (children) {
                  children.forEach((child) => {
                    const bbox = child.getBBox().inflate(embedPadding)
                    const corner = bbox.getCorner()

                    if (bbox.x < x) {
                      x = bbox.x
                      hasChange = true
                    }

                    if (bbox.y < y) {
                      y = bbox.y
                      hasChange = true
                    }

                    if (corner.x > cornerX) {
                      cornerX = corner.x
                      hasChange = true
                    }

                    if (corner.y > cornerY) {
                      cornerY = corner.y
                      hasChange = true
                    }
                  })
                }

                if (hasChange) {
                  parent.prop(
                    {
                      position: { x, y },
                      size: { width: cornerX - x, height: cornerY - y },
                    },
                    { skipParentHandler: true },
                  )
                }
              }
            })

            // 监听键盘事件
            document.addEventListener("keydown", (event) => {
                // 以下这段代码 会出现聚焦在input框时按delete键会删除元素的bug
                // if (event.key === "Delete") {
                //     const cells = this.graph.getSelectedCells();
                //     if (cells.length > 0) {
                //         graph.removeCells(cells);
                //         that.colseAllDrawer();
                //     }
                // }
            });
        },
        colseAllDrawer() {
            this.$refs.startDrawer.closeDrawer();
            this.$refs.endDrawer.closeDrawer();
            this.$refs.conditionDrawer.closeDrawer();
            this.$refs.questionClassifyDrawer.closeDrawer();
            this.$refs.knowBaseDrawer.closeDrawer();
            this.$refs.sceneManageDrawer.closeDrawer();
            this.$refs.paramsFilterDrawer.closeDrawer();
            this.$refs.interceptWordDrawer.closeDrawer();
            this.$refs.knowledgeAddDrawer.closeDrawer();
			      this.$refs.sqlAddDrawer.closeDrawer();
            this.$refs.mcpDrawer.closeDrawer();
            this.$refs.modelDrawer.closeDrawer();
            this.$refs.apiDrawer.closeDrawer();
            this.$refs.diedaiDrawer.closeDrawer();
            this.$refs.toolDrawer.closeDrawer();
            this.$refs.existAgentDrawer.closeDrawer();
            this.$refs.globalConditionDrawer.closeDrawer();
            this.$refs.iterationDrawer.closeDrawer();
            this.$refs.textParsingDrawer.closeDrawer();
            this.$refs.agentDrawer.closeDrawer();
            this.$refs.changqijiyiDrawer.closeDrawer();
            this.$refs.wendaDrawer.closeDrawer();
            this.$refs.xiaoxiDrawer.closeDrawer();
            this.$refs.shujukuDrawer.closeDrawer();
            this.$refs.yitushibieDrawer.closeDrawer();
            this.$refs.codeDrawer.closeDrawer();
            this.$refs.customDrawer.closeDrawer();
            this.$refs.variableDrawer.closeDrawer();
            this.$refs.fileParsingDrawer.closeDrawer();
            this.$refs.aiImageDrawer.closeDrawer()
            this.$refs.aiVideoDrawer.closeDrawer()
            this.$refs.datasetDrawer.closeDrawer()
            // if(this.$refs.previewDrawer && this.$refs.previewDrawer.drawerVisible){
            //   this.$refs.previewDrawer && this.$refs.previewDrawer.closeDrawer();
            //   this.activeTab = -1
            // }
            this.$EventBus.$emit("saveWorkflow")
        },
        colseLeftAllDrawer() {
            // this.$refs.nodeDrawer.closeDrawer();
            this.$refs.singleNodetestingDrawer.closeDrawer();
            this.$refs.funcDrawer.closeDrawer();
            this.$refs.runDrawer.closeDrawer();
            this.$refs.showDrawer.closeDrawer();
        },
        async showNodeStatus(statusList) {
            // const status = statusList.shift();
            // status?.forEach((item) => {
            //     const { id, status } = item;
            //     const node = this.graph.getCellById(id);
            //     const data = node.getData();
            //     node.setData({
            //         ...data,
            //         status: status,
            //     });
            // });
            // this.timer = setTimeout(() => {
            //     this.showNodeStatus(statusList);
            // }, 300);
        },
        // 初始化节点/边
        init(data = []) {
            const cells = [];
            data.forEach((item) => {
                if (item.shape === "dag-edge") {
                    cells.push(this.graph.createEdge(item));
                } else {
                    delete item.component;
                    cells.push(this.graph.createNode(item));
                }
            });
            this.graph.resetCells(cells);
        },
        // 获取所有上游祖先节点
        getAncestors(nodeId) {
          const ancestors = [];
          const visited = new Set();

          const traverseAncestors = (currentNodeId) => {
            if (visited.has(currentNodeId)) {
              return;
            }
            visited.add(currentNodeId);

            const incomingEdges = this.graph.getIncomingEdges(currentNodeId);
            if (incomingEdges) {
              incomingEdges.forEach(edge => {
                const sourceNodeId = edge.getSourceCellId();
                if (sourceNodeId) {
                  ancestors.push(sourceNodeId);
                  traverseAncestors(sourceNodeId);
                }
              });
            }
          };

          traverseAncestors(nodeId);
          return ancestors;
        },
        zoomFn1(num) {
			console.log('this.graph.zoom(num)',this.graph.zoom())
			if(this.graph.zoom()<1.8){
				this.graph.zoom(num);
			}
            
        },
		zoomFn2(num) {
			console.log('this.graph.zoom(num)',this.graph.zoom())
			if(this.graph.zoom()>=0.6){
				this.graph.zoom(num);
			}
		    
		},
        centerFn() {
            const num = 1 - this.graph.zoom();
            num > 1 ? this.graph.zoom(num * -1) : this.graph.zoom(num);
            this.graph.centerContent();
        },
        startFn(item) {
            this.timer && clearTimeout(this.timer);
            this.init(item);
            this.graph.centerContent();
            //非多agent
            if(this.type !== 'multi_agent'){
              this.showNodeStatus(Object.assign([], nodeStatusList));
              if(!item.length || !item.find((el) => el.shape === "dag-output")){
                this.$refs.nodeDrawer.addStartNode()
              }
              if(!item.length || !item.find((el) => el.shape === "dag-onlyIn")){
                this.$refs.nodeDrawer.addEndNode()
                this.$EventBus.$emit("saveWorkflowAgain");
              }
              this.getStartValueables(item);
            } else {
              if(!item.length || !item.find((el) => el.shape === "dag-output")){
                this.$refs.nodeDrawer.addStartNode()
                this.$refs.nodeDrawer.addAgentNode()
              }
            }
            
        },
        getStartValueables(cells) {
            let startNode = cells ? cells.find((el) => el.shape === "dag-output") : [];
            let endNode = cells ? cells.find((el) => el.shape === "dag-onlyIn") : [];
            this.startNode = startNode;
            this.endNode = endNode;
        },
       
        createMenuFn() {},
        keyBindFn() {
            // copy cut paste
            this.graph.bindKey(["meta+c", "ctrl+c"], () => {
                const cells = this.graph.getSelectedCells();
                if (cells.length && cells[0].store.data.data.type !== 'output') {
                    this.graph.copy(cells);
                }
                return false;
            });
            this.graph.bindKey(["meta+x", "ctrl+x"], () => {
                const cells = this.graph.getSelectedCells();
                if (cells.length) {
                    this.graph.cut(cells);
                }
                return false;
            });
            this.graph.bindKey(["meta+v", "ctrl+v"], () => {
                if (!this.graph.isClipboardEmpty()) {
                    const cells = this.graph.paste({ offset: 32 });
                    this.graph.cleanSelection();
                    this.graph.select(cells);
                }
                return false;
            });

            // undo redo
            this.graph.bindKey(["meta+z", "ctrl+z"], () => {
                if (this.graph.history.canUndo()) {
                    this.graph.history.undo();
                }
                return false;
            });
            // delete
            this.graph.bindKey(["delete"], () => {
                const select = this.graph.getSelectedCells();
                let allNodes = this.graph.getNodes();
                let allEdges = this.graph.getEdges();
                let startNode = allNodes.find((el) => el.store.data.shape === "dag-output");
                select?.forEach((item) => {
                    if(item.shape === 'dag-output'){
                      return false;
                    }
                    if(item.shape === 'dag-agent' && allEdges.find(item2=>item2.store.data.target.cell == item.id && item2.store.data.source.cell === startNode.id)){
                      return false;
                    }
                    if (/edge/.test(item.shape)) {
                        this.graph.removeEdge(item.id);
                    } else {
                        this.graph.removeNode(item.id);
                    }
                });
                return false;
            });
        },
        saveFn() {
            localStorage.setItem(
                "x6Json",
                JSON.stringify(this.graph.toJSON({ diff: true }))
            );
        },
        loadFn() {
            this.timer && clearTimeout(this.timer);
            const x6Json = this.canvas ? JSON.parse(this.canvas) : DataJson;
            this.startFn(x6Json.cells);
        },
        lockFn() {
            this.isLock = !this.isLock;
            if (this.isLock) {
                this.graph.enablePanning();
                this.graph.enableKeyboard();
            } else {
                this.graph.disablePanning();
                this.graph.disableKeyboard();
            }
        },
        contextMenuFn(type, node) {
            switch (type) {
                case "remove":
                    if (node.type == "edge") {
                        this.graph.removeEdge(node.item.id);
                    } else if (node.type == "node") {
                        this.graph.removeNode(node.item.id);
                    }
                    break;
                case "source":
                    this.$refs.dialogMysql.visible = true;
                    this.$refs.dialogMysql.init(node);
                    break;
            }

            this.showContextMenu = false;
        },

        showDrawerFn() {
            this.$refs.nodeDrawer.visible = !this.$refs.nodeDrawer.visible;
        },
        //firstAgentFlag:首次加载anget节点要默认连线
        addNode(option, deduceData, firstAgentFlag) {
            this.nodeOption = option;
            if(option.data.type === 'tool'){
              this.toolSettingVisible = true
            }else if(option.data.type === 'existAgent'){
              this.agentSettingVisible = true
            }else if(option.data.type === 'mcp'){
            //   this.selectMcpVisible = true
            const p = this.graph.pageToLocal(option.x, option.y);
              this.graph.addNode(Object.assign({}, option, p));
            }else if(option.data.type === 'iteration'){
              this.iterationSettingVisible = true
            }else if(option.data.type === 'deduce'){
              this.selectToolEmit(deduceData, true)
            } else if(option.data.type==='aiImage'){
                this.aiImageStart()
            } else {
              const p = this.graph.pageToLocal(option.x, option.y);
              this.graph.addNode(Object.assign({}, option, p));
            }
            if(firstAgentFlag){
              let allNodes = this.graph.getNodes();
              this.graph.addEdge({
                source: {
                  cell: allNodes[0].id,
                  port: allNodes[0].port.ports[0].id,
                },
                target: {
                  cell: allNodes[1].id,
                  port: allNodes[1].port.ports[0].id,
                },
                inherit: "edge",
                shape: "dag-edge",
                attrs: {
                    line: {
                        stroke: "#C2C8D5",
                        strokeWidth: 2,
                    },
                },
              });
            }
        },
        selectToolEmit(data, hideToolName) {
            let option = JSON.parse(JSON.stringify(this.nodeOption));
            let tempStartNode = data.nodes.find(
                (el) => el.nodeName === "开始"
            )
            option.data.startNode = tempStartNode?.output || [];
            let tempEndData = data.nodes.find(
                (el) => el.nodeName === "结束"
            )
            if(tempStartNode && tempStartNode.output) {
              option.data.startNodevariables = tempStartNode.output.map((el) => {
                  return [ { "inputType": 1, "selectedGroup": "", "cusInput": "", desc: el.desc, "label": el.label, "lan": "", "value": "", type: el.type, } ]
              });
            }
            if(tempEndData && tempEndData.input) {
              let tempData = tempEndData.input.map(el => {
                el.name = el.label
                return el
              })
              option.data.endNode = JSON.parse(JSON.stringify(tempData));
              option.data.variables = JSON.parse(JSON.stringify(tempData));
            }
            data.hideToolName = hideToolName
            option.data.toolData = data
            option.data.label = data.componentName
            const p = this.graph.pageToLocal(this.nodeOption.x, this.nodeOption.y);
            this.graph.addNode(Object.assign({}, option, p));
        },
        aiImageStart() {
            const arr=[{label:"width",type:"string",desc:""},{label:"height",type:"string",desc:""},{label:"guidanceScale",type:"string",desc:""}]
            let option = JSON.parse(JSON.stringify(this.nodeOption));
            // option.data.startNodevariables = arr.map((el) => {
            //     return [ { "inputType": 1, "selectedGroup": "", "cusInput": "", desc: el.desc, "label": el.label, "lan": "", "value": "", type: el.type, } ]
            // });
            
            const p = this.graph.pageToLocal(this.nodeOption.x, this.nodeOption.y);
            this.graph.addNode(Object.assign({}, option, p));
        },
        selectAgentEmit(data) {
            let option = JSON.parse(JSON.stringify(this.nodeOption));
            option.data.toolData = data
            option.data.label = data.applicationName
            const p = this.graph.pageToLocal(this.nodeOption.x, this.nodeOption.y);
            this.graph.addNode(Object.assign({}, option, p));
        },
        selectMcpEmit(data) {
            option.data.mcpData = data
            option.data.label = data.mcpName
            const p = this.graph.pageToLocal(this.nodeOption.x, this.nodeOption.y);
            this.graph.addNode(Object.assign({}, option, p));
        },
        selectIterationEmit(data) {
            let option = JSON.parse(JSON.stringify(this.nodeOption));
            let tempStartNode = data.nodes.find(
                (el) => el.nodeName === "开始"
            )
            option.data.startNode = tempStartNode?.output || [];
            let tempEndData = data.nodes.find(
                (el) => el.nodeName === "结束"
            )
            if(tempStartNode && tempStartNode.output) {
              option.data.startNodevariables = tempStartNode.output.map((el) => {
                  return {
                      label: el.label,
                      desc: el.desc,
                      name: "",
                      value: "",
                      type: el.type,
                      selectedGroup: "",
                  };
              });
            }
            if(tempEndData && tempEndData.input) {
              let tempData = tempEndData.input.map(el => {
                el.name = el.label
                return el
              })
              option.data.endNode = JSON.parse(JSON.stringify(tempData));
              option.data.variables = JSON.parse(JSON.stringify(tempData));
            }
            option.data.toolData = data
            //option.data.label = data.componentName
            const p = this.graph.pageToLocal(this.nodeOption.x, this.nodeOption.y);
            this.graph.addNode(Object.assign({}, option, p));
        },
        addConditionPorts(data, node) {
            // data.id是当前cell.id
            let cell = this.graph.getCellById(node.id);
            let height = 102
            data.forEach(item => {
              height += item.conditions.length ? item.conditions.length * 26 : 42
            })
            cell.resize(296, height);
            cell.setData(data);
            let index = data.length;
            let caseId = data[data.length - 1].caseId;
            cell.insertPort(index, {
                id: caseId,
                group: "right",
                attrs: {
                    
                },
                args: {
                    x: '100%',
                    y:68 + 42 * index,
                  },
            });
           
        },

        addQaTypePorts(data, node) {
            // data.id是当前cell.id
            let cell = this.graph.getCellById(node.id);
            let height = document.getElementById(node.id) && document.getElementById(node.id).offsetHeight
            cell.resize(296, height);
            let originDataLen = JSON.parse(cell.store.data.data.configurationList).length
            let index = data.length;
            if (index > originDataLen){
              let caseId = data[data.length - 1].caseId;
              cell.insertPort(index, {
                  id: caseId,
                  group: "right",
                  attrs: {
                    
                  },
                  args: {
                    x: '100%',
                    y:82+ 44 * index,
                  },
              });
            }
        },
        numberToChinese(num) {
            const chineseNums = [
                "零",
                "一",
                "二",
                "三",
                "四",
                "五",
                "六",
                "七",
                "八",
                "九",
            ];
            let result = "";
            for (let i = 0; i < num.toString().length; i++) {
                result += chineseNums[num.toString()[i]];
            }
            return result;
        },
        updateCellData(data, node) {
            // data.id是当前cell.id
            let cell = this.graph.getCellById(node.id);
            cell.setData(data);
        },
        updateConditionData(data, node) {
            // data.id是当前cell.id
            let cell = this.graph.getCellById(node.id);
            let variables = data.variables ? JSON.parse(data.variables) : [];
            let height = 82
            let topHeight = 42
            const ports = cell.getPorts();
            variables.forEach(item => {
              let curHeight = item.conditions.length > 1 ? (item.conditions.length - 1) * 26 + 42 : 42
              height += curHeight
              if(item.caseId && item.caseId.indexOf('else1') === -1){
                cell.portProp(item.caseId, 'args/y', topHeight + curHeight/2); // 恢复默认半径
              }
              topHeight += curHeight
            })
            cell.portProp('out-else', 'args/y', height - 15); // 恢复默认半径
            cell.setData(data);
            cell.resize(296, height);
        },
        
        updateAppForm(data, node) {
            // data.id是当前cell.id
            if(!node.id) return
            let cell = this.graph.getCellById(node.id);
            cell.setData(data);
            let type = cell.store.data.data.type
            if(type !== "onlyIn" && type !== "diedai"){
              setTimeout(() => {
                let height = document.getElementById(node.id) && document.getElementById(node.id).offsetHeight
                cell.resize(296, height);
              }, 500);
            }
        },
        updatefilterVariables(data, node) {
            // data.id是当前cell.id
            let cell = this.graph.getCellById(node.id);
            cell.setData(data);
            let modelId = cell.store.data.data.modelId
            let filterVariables = cell.store.data.data.filterVariables ? JSON.parse(cell.store.data.data.filterVariables) : []
            let height = modelId ? (filterVariables.length) * 84 + 80 : (filterVariables.length) * 84 + 40 
            cell.resize(296, height);
        },
        removeQaTypePorts(data, node, index) {
            // data.id是当前cell.id
            let cell = this.graph.getCellById(node.id);
            cell.removePortAt(index + 1);
            this.recalculatePorts(cell)
            setTimeout(() => {
              let height = document.getElementById(node.id) && document.getElementById(node.id).offsetHeight + 60
              cell.resize(296, height);
            }, 500);
        },
        recalculatePorts(cell) {
          const ports = cell.getPorts();
          const rightPorts = ports.filter(port => port.group === 'right');
          rightPorts.forEach((port, newIndex) => {
            cell.portProp(port.id, 'args/y', 82 + 44 * (newIndex+1));
          });
        },
        removePorts(data, node, index) {
            // data.id是当前cell.id
            let cell = this.graph.getCellById(node.id);
            cell.removePortAt(index + 1);
            cell.setData(data);
            const ports = cell.getPorts();
            const rightPorts = ports.filter(port => port.group === 'right');
            rightPorts.forEach((port, newIndex) => {
              cell.portProp(port.id, 'args/y', 68 + 42 * (newIndex+1));
            });
            cell.resize(160, (data.length + 1) * 38);
        },
    },
};
</script>

<style lang="scss" scoped>
header {
    display: flex;
    justify-content: flex-end;
    width: 100%;
    height: 50px;
    box-sizing: border-box;
    position: absolute;
    bottom: 0;
    left: -46%;
    z-index: 9;
    margin-left: 100px;
}

header i {
    margin: 8px;
    font-size: 30px;
}
.x6-node-selected .node {
    border:2px solid #1890ff;
    border-radius: 2px;
    // box-shadow: 0 0 0 4px #d4e8fe;
}

.x6-node-selected .node.success {
    border-color: #52c41a;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #ccecc0;
}

.x6-node-selected .node.failed {
    border-color: #ff4d4f;
    border-radius: 2px;
    box-shadow: 0 0 0 4px #fedcdc;
}

.x6-edge:hover path:nth-child(2) {
    stroke: #1890ff;
    stroke-width: 1px;
}

.x6-edge-selected path:nth-child(2) {
    stroke: #1890ff;
    stroke-width: 1.5px !important;
}

.section-cot {
    display: flex;
}

.section-cot #container {
    position: relative;
    flex: 1;
}

.section-cot #container .drawCotStyle {
    width: 100% !important;
    height: 100% !important;
}

::-webkit-scrollbar {
    width: 0;
}
</style>
<style lang="scss" scoped>
.drawer-tabs-container {
    width: 56px;
  display: flex;
  height: 100vh;

}
.tabs-cont{
    width: 56px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(4px);
  color: #828894;
  position: fixed;
  left: 0px;
  top: 77px;
//   z-index: 10000;
  bottom: 0;
  
}
.tabs {
  width: 56px;
  height: 56px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: center;
//   height: 195px;
//   margin-left:4px;
 
  .icon{
    width: 56px;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 24px;
    // margin-bottom:4px;
  }
  &.tabs-cont-active{
    height: 130px;
  }
}

.tab {
    height: 56px;
  cursor: pointer;
  text-align: center;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top:8px;
}

.tab i {
  font-size: 20px;
}

.tab span {
  font-size: 14px;
//   margin: 4px 0;
padding:4px auto;
  margin-top:8px;
}

.tab.active {
  color: #1c50fd;
  background: rgba(28,80,253,0.1);
  border-radius: 4px;
}

.custom-drawer {
  margin-left: 60px; /* 使抽屉紧贴在 tab 列表旁边 */
}
.item {
    width: 50px;
}
.iconpark {
    cursor: pointer;
}
</style>
