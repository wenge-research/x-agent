import app from '../../../../main';
export function nodeConfig(item, x, y, store) {
    const time = new Date().getTime();

    // 基础端口配置
    const basePortConfig = {
        circle: {
            r: 4,
            magnet: true,
            stroke: "#1c50fd",
            strokeWidth: 1,
            fill: "#fff",
        },
    };

    // 创建端口组
    const createPortGroups = (hasLeft = true, hasRight = true) => {
        const groups = {};
        if (hasLeft) {
            groups.left = {
                position: "left",
                attrs: basePortConfig,
            };
        }
        if (hasRight) {
            groups.right = {
                position: "right",
                attrs: basePortConfig,
            };
        }
        return groups;
    };

    // 创建端口项
    const createPortItems = (groups, time) => {
        const items = [];
        if (groups.left) {
            items.push({
                id: `in-${time}`,
                group: "left",
            });
        }
        if (groups.right) {
            items.push({
                id: `out-${time}`,
                group: "right",
            });
        }
        return items;
    };

    // 创建条件节点的特殊端口项
    const createConditionPortItems = (time) => {
        return [
            {
                id: `in-${time}`,
                group: "left",
            },
            {
                id: `out-${time}-if`,
                group: "right",
                args: {
                    x: "100%",
                    y: 68,
                },
            },
            {
                id: `out-else`,
                group: "right",
                args: {
                    x: "100%",
                    y: "90%",
                },
            },
        ];
    };

    // 创建QA类型节点的特殊端口项
    const createQATypePortItems = (time) => {
        return [
            {
                id: `in-${time}`,
                group: "left",
            },
            {
                id: `out-other`,
                group: "right",
                args: {
                    x: "100%",
                    y: 80 + 44,
                },
            },
        ];
    };

    // 基础节点配置工厂
    const createBaseNodeConfig = (
        shape,
        width,
        height,
        item,
        hasLeft = true,
        hasRight = true,
        customAttrs = {}
    ) => {
        const portGroups = createPortGroups(hasLeft, hasRight);
        const portItems =
            hasLeft || hasRight ? createPortItems(portGroups, time) : [];

        return {
            x,
            y,
            width,
            height,
            shape,
            data: item,
            ports: {
                groups: portGroups,
                items: portItems,
            },
            ...customAttrs,
            time,
        };
    };

    // 特殊节点配置
    const specialNodes = {
        output: () => {
            const workFlowType = store.state.workflow.workFlowType;
            return createBaseNodeConfig(
                "dag-output",
                296,
                workFlowType === "dialogue" ? 104 : 80,
                item,
                false,
                true
            );
        },
        onlyIn: () =>
            createBaseNodeConfig("dag-onlyIn", 296, 82, item, true, false),
        model: () => createBaseNodeConfig("dag-node", 296, 132, item),
        knowledge: () => createBaseNodeConfig("dag-knowledge", 296, 138, item),
        knowledgeAdd: () =>
            createBaseNodeConfig("dag-knowledgeAdd", 296, 138, item),
        sqlAdd: () => createBaseNodeConfig("dag-sqlAdd", 296, 138, item),
        sceneManage: () =>
            createBaseNodeConfig("dag-sceneManage", 296, 132, item),
        paramsFilter: () =>
            createBaseNodeConfig("dag-paramsFilter", 296, 104, item),
        interceptWord: () =>
            createBaseNodeConfig("dag-interceptWord", 296, 132, item),
        tool: () => createBaseNodeConfig("dag-tool", 296, 134, item),
        existAgent: () => createBaseNodeConfig("dag-existAgent", 296, 134, item),
        globalCondition: () => createBaseNodeConfig("dag-globalCondition", 296, 134, item, false, true),
        deduce: () => createBaseNodeConfig("dag-tool", 296, 134, item),
        mcp: () => createBaseNodeConfig("dag-mcp", 296, 134, item),
        iteration: () => createBaseNodeConfig("dag-iteration", 296, 132, item),
        agent: () => createBaseNodeConfig("dag-agent", 296, 132, item),
        fileParsing: () =>
            createBaseNodeConfig("dag-textParsing", 296, 104, item),
        changqijiyi: () =>
            createBaseNodeConfig("dag-textParsing", 296, 104, item),
        wenda: () => createBaseNodeConfig("dag-textParsing", 296, 104, item),
        xiaoxi: () => createBaseNodeConfig("dag-textParsing", 296, 104, item),
        shujuku: () => createBaseNodeConfig("dag-textParsing", 296, 104, item),
        yitushibie: () =>
            createBaseNodeConfig("dag-textParsing", 296, 104, item),
        textParsing: () =>
            createBaseNodeConfig("dag-textParsing", 296, 104, item),
        code: () => createBaseNodeConfig("dag-textParsing", 296, 104, item),
        variable: () => createBaseNodeConfig("dag-textParsing", 296, 104, item),
        custom: () => createBaseNodeConfig("dag-custom", 296, 104, item),
        http: () => createBaseNodeConfig("dag-http", 296, 104, item),
        aiImage: () => createBaseNodeConfig("dag-aiImage", 296, 134, item),
        aiVideo: () => createBaseNodeConfig("dag-aiVideo", 296, 134, item),
        dataset: () => createBaseNodeConfig("dag-dataset", 296, 138, item),
        diedai: () => ({
            ...createBaseNodeConfig("dag-diedai", 400, 282, item),
            attrs: {
                body: {
                    fill: "#f0f4fa",
                },
            },
            embeddable: false,
        }),
        condition: () => ({
            ...createBaseNodeConfig("dag-condition", 296, 124, item),
            ports: {
                groups: {
                    left: {
                        position: "left",
                        attrs: basePortConfig,
                    },
                    right: {
                        position: {
                            name: "absolute",
                            args: { x: 1, y: 0 },
                        },
                        attrs: basePortConfig,
                    },
                },
                items: createConditionPortItems(time),
            },
        }),
        qaType: () => ({
            ...createBaseNodeConfig("dag-qaType", 296, 184, item),
            ports: {
                groups: {
                    left: {
                        position: "left",
                        attrs: basePortConfig,
                    },
                    right: {
                        position: {
                            name: "absolute",
                            args: { x: 1, y: 0 },
                        },
                        attrs: basePortConfig,
                    },
                },
                items: createQATypePortItems(time),
            },
        }),
    };

    // 获取配置
    const config = specialNodes[item.type] ? specialNodes[item.type]() : null;

    if (!config) {
        console.warn(`Unknown node type: ${item.type}`);
        return createBaseNodeConfig("dag-custom", 296, 104, item);
    }
    console.log(config,"config");
    return config;
}
export function getNodesAndEdgesConfig(flag, component, graph) {
    const allNodes = graph.getNodes();
    const allEdges = graph.getEdges();
    let startNode = {};
    let msg = "";
    let casesIdArr = [];

    // 公共函数：处理输入参数
    const processInputs = (inputs, nodeLabel) => {
        if (!inputs.every((item) => item.type) && flag === 2) {
            msg = "请配置" + nodeLabel + "输入参数";
            return { error: true };
        }
        return {
            inputs: inputs.map((item) => ({
                label: item.label,
                variable: item.label,
                desc: nodeLabel,
                referenceNodeId: item.selectedGroup,
                value: item.selectedGroup ? item.value : item.cusInput,
                type: isFileType(item.type) ? "file" : item.type,
                valueType: item.selectedGroup ? "reference" : "string",
                maxLength: 20,
                required: false,
            })),
        };
    };

    // 公共函数：判断是否是文件类型
    const isFileType = (type) => {
        const fileTypes = [
            "default",
            "image",
            "doc",
            "code",
            "ppt",
            "txt",
            "excel",
        ];
        return type && fileTypes.includes(type);
    };

    // 公共函数：创建基础节点结构
    const createBaseNode = (node, nodeLabel, inputResult, nodeType = 1) => {
        const nodeStore = node.store.data;
        return {
            nodeId: nodeStore.id,
            componentId: component ? component.componentId : null,
            nodeName: nodeLabel,
            label: nodeLabel,
            nodeType: nodeType,
            settings: {},
            input: inputResult.inputs,
            output: [],
        };
    };

    // 公共函数：处理输出参数
    const processOutputs = (variables, defaultDesc = "") => {
        return variables.map((item) => ({
            label: item.name || item.label,
            variable: item.name || item.label,
            desc: item.description || defaultDesc,
            referenceNodeId: "",
            value: "",
            type: isFileType(item.type) ? "file" : item.type,
            valueType: item.type,
            maxLength: item.maxLength || 500,
            required: item.required !== undefined ? item.required : true,
            inputType: item.inputType,
            selectValues: item.selectValues || "",
            question: item.config
        }));
    };

    // 处理节点数据
    const nodes = allNodes?.map((node) => {
        const nodeStore = node.store.data;
        const nodeStoreData = nodeStore.data;
        const nodeLabel = nodeStoreData.label;
        const nodeType = nodeStoreData.type;
        const appForm = nodeStoreData.appForm
            ? JSON.parse(nodeStoreData.appForm)
            : {};
        const configItems = nodeStoreData.configItems
            ? JSON.parse(nodeStoreData.configItems)
            : [];
        const inputs = appForm.inputs || [];
        const item_result = appForm.item_result || [];
        const outputs = appForm.outputs || [];
        const workFlowOutputs = nodeStoreData.outputs ? JSON.parse(nodeStoreData.outputs) : [];//工作流输出


        // 处理输入参数
        const inputResult = processInputs(inputs, nodeLabel);
        if (inputResult.error) return { nodes: [], edges: [], msg: msg };

        let tempNode = createBaseNode(node, nodeLabel, inputResult);

        // 根据节点类型处理特殊逻辑
        switch (nodeType) {
            case "output":
                handleOutputNode();
                break;
            case "onlyIn":
                handleOnlyInNode();
                break;
            case "condition":
                handleConditionNode();
                break;
            case "http":
                handleHttpNode();
                break;
            case "qaType":
                handleQaTypeNode();
                break;
            case "knowledge":
                handleKnowledgeNode();
                break;
            case "mcp":
                handleMcpNode();
                break;
            case "sceneManage":
                handleSceneManageNode();
                break;
            case "fileParsing":
                handleFileParsingNode();
                break;
            case "knowledgeAdd":
                handleKnowledgeAddNode();
                break;
            case "sqlAdd":
                handleSqlAddNode();
                break;
            case "textParsing":
                handleTextParsingNode();
                break;
            case "code":
                handleCodeNode();
                break;
            case "custom":
                handleCustomNode();
                break;
            case "model":
                handleModelNode();
                break;
            case "agent":
                handleAgentNode();
                break;
            case "existAgent":
                handleExistAgentNode();
                break;
            case "globalCondition":
                handleGlobalConditionNode();
                break;
            case "changqijiyi":
                handleChangqijiyiNode();
                break;
            case "paramsFilter":
                handleParamsFilterNode();
                break;
            case "interceptWord":
                handleInterceptWordNode();
                break;
            case "variable":
                handleVariableNode();
                break;
            case "iteration":
                handleIterationNode();
                break;
            case "diedai":
                handleDiedaiNode();
                break;
            case "dataset":
                handleDatasetNode();
                break;
            case "aiImage":
                handleAiImageNode();
                break;
            case "aiVideo":
                handleAiVideoNode();
                break;
            default:
                handleDefaultNode();
        }

        if (nodeStore.parent) {
            tempNode.settings.nodeParent = nodeStore.parent;
        }

        return tempNode;

        // 各种节点类型的处理函数
        function handleOutputNode() {
            let variables = Array.isArray(nodeStoreData.variables)
                ? nodeStoreData.variables
                : typeof nodeStoreData.variables === "string"
                ? JSON.parse(nodeStoreData.variables)
                : [];

            if (!variables && flag == 2) {
                msg = app.$t("pleaseConfigureStartNodeParams");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.output = processOutputs(variables);
            const status = nodeStoreData.status;
            const crontab = nodeStoreData.crontab;
            const id = nodeStoreData.id;
            console.log('id....',id)
            const config = {}
            tempNode.output.forEach(item => {
                if (item.label && item.question) {
                  config[item.label] = item.question;
                }
              });
               
            let trigger = {
                triggerType: '1', 
                crontabType: '1', 
                triggerRange: '1', 
                status, 
                crontab,
                config,
                id
            }
            if(appForm.fileFlag){
              tempNode.output.push({
                label: "upload_file",
                variable: "upload_file",
                type: "file",
                referenceNodeId: "",
                value: "upload_file",
                valueType: "file",
                direction: 1,
            })
            }
            tempNode.settings = { 
              output: tempNode.output,
              multi_dialogue_flag: appForm.multi_dialogue_flag,
              multi_dialogue_num: appForm.multi_dialogue_num,
              fileFlag: appForm.fileFlag,
              cusChecked: appForm.cusChecked,
              system_promt: appForm.system_promt,
             };
             if(nodeStoreData.status === '1'){
              tempNode.settings.trigger = trigger;
             }
            startNode = tempNode;
        }

        function handleOnlyInNode() {
            tempNode.nodeType = 10;
            tempNode.settings = {
                format: appForm.format,
                reasonVar: appForm.reasonVar,
                reasonFormatter: appForm.reasonFormatter,
                interval: appForm.interval,
                streamVar: appForm.streamVar,
                arrayToStrFlag: appForm.arrayToStrFlag,
                streamFlag: appForm.streamFlag,
                content: appForm.content,
            };
        }

        function handleConditionNode() {
            tempNode.nodeType = 5;
            let variables = nodeStoreData.variables
                ? JSON.parse(nodeStoreData.variables)
                : [];

            if (!variables && flag == 2) {
                msg = app.$t("pleaseConfigureConditionNodeParameters");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            const allConditions = variables.reduce(
                (acc, item) => acc.concat(item.conditions),
                []
            );
            const uniqueInputs = allConditions.filter(
                (condition, index, self) =>
                    index ===
                    self.findIndex(
                        (t) =>
                            t.selectedGroup === condition.selectedGroup &&
                            t.value === condition.value
                    )
            );

            if (!uniqueInputs.every((item) => item.left !== "")) {
                msg = app.$t("pleaseConfigureConditionNodeParameters");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.input = uniqueInputs.map((item) => ({
                label: item.value,
                variable: item.value,
                desc: app.$t("conditionNode"),
                referenceNodeId: item.selectedGroup,
                value: item.value,
                type: isFileType(item.type) ? "file" : item.type,
                valueType: "reference",
                maxLength: 20,
                required: false,
            }));

            tempNode.settings = { cases: variables };
            casesIdArr = casesIdArr.concat(variables);
        }

        function handleHttpNode() {
            tempNode.nodeType = 2;
            let nodeApi = JSON.parse(nodeStoreData.nodeApi);
            tempNode.settings = nodeApi.settings;
            tempNode.settings.appForm = appForm;
            tempNode.settings.api_source = 'http';
            tempNode.input = nodeStoreData.startNodevariables.map((el) => {
              let item = el.label ? el : el[0];
              return {
                  label: item.label,
                  desc: "tool",
                  variable: item.label,
                  referenceNodeId: item.selectedGroup,
                  value: item.selectedGroup ? item.value : item.cusInput,
                  type: isFileType(item.orginType) ? "file" : item.orginType,
                  valueType: item.selectedGroup ? "reference" : "string",
                  maxLength: 20,
                  required: false,
              };
          });

          tempNode.output = appForm.endNode.map((item) => ({
              desc: '',
              direction: 1,
              label: item.name,
              maxLength: 20,
              referenceNodeId: "",
              required: true,
              type: isFileType(item.type) ? "file" : item.type,
              value: "",
              valueType: item.type,
              variable: item.name,
          }));
        }

        function handleQaTypeNode() {
            let configurationList = nodeStoreData.configurationList
                ? JSON.parse(nodeStoreData.configurationList)
                : [];
            tempNode.nodeType = 7;

            if (!inputs && flag == 2) {
                msg = app.$t(
                    "pleaseConfigureQuestionClassificationNodeParams"
                );
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "string",
                    desc: "result",
                    referenceNodeId: "",
                    value: "string",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
            ];

            tempNode.settings = {
                modelId: appForm.modelId,
                configurationList: configurationList,
            };
            casesIdArr = casesIdArr.concat(configurationList);
        }

        function handleKnowledgeNode() {
            tempNode.nodeType = 9;

            if (!inputs || (!appForm?.knowledgeIds && flag == 2)) {
                msg = app.$t("pleaseConfigureKnowledgeSearchNodeParams");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "array[string]",
                    desc: "consequat",
                    referenceNodeId: "exercitation sunt",
                    value: "result",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
            ];

            tempNode.settings = {
                param: {
                    contentScore: appForm?.contentScore,
                    rangeContentScore: appForm?.rangeContentScore,
                    qaTitleScore: appForm?.qaTitleScore,
                    qaRangeTitleScore: appForm?.qaRangeTitleScore,
                    qaContentScore: appForm?.qaContentScore,
                    qaRangeContentScore: appForm?.qaRangeContentScore,
                    filterNum: appForm?.filterNum,
                    prepareNum: appForm?.prepareNum,
                },
                knowledgeId: appForm?.knowledgeIds,
                type: appForm?.type,
                strategy: [
                    "documentLibrary",
                    "multiMediaLibrary",
                    "structuredStrategy",
                    "knowledgeContent",
                    "WebsiteStrategy",
                    "networkStrategy",
                    "findQaTitle",
                    "findQaContent",
                ],
                filterNum: 3,
                prepareNum: 60,
                contentScore: 1.2,
                promptTemplate:
                    "【】中的内容为知识库检索后的内容，请直接回答知识库内容【{content}】，不需要多余修饰",
            };
        }

        function handleMcpNode() {
            let configurations = JSON.parse(nodeStoreData.configurations);

            if (
                !configurations.every(
                    (item) => item.interceptWordContent !== ""
                ) &&
                flag == 2
            ) {
                msg = app.$t("pleaseConfigureSensitiveWordParameters");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            if (!inputs && flag == 2) {
                msg = app.$t("pleaseConfigureSensitiveWordParameters");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.nodeType = 34;
            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "string",
                    desc: "",
                    referenceNodeId: "",
                    value: "true",
                    valueType: "string",
                    maxLength: 20,
                    required: true,
                    direction: 1,
                },
            ];

            let mcpServerId = configurations.map((item) => item.mcpId);
            tempNode.settings = {
                mcpServerId: mcpServerId,
            };
        }
        function handleDatasetNode() {
            console.log(appForm,"dataset");
            
            if (!inputs && flag == 2) {
                msg = app.$t("pleaseConfigureSensitiveWordParameters");
                return { nodes: nodes, edges: edges, msg: msg };
            }
            tempNode.nodeType = 39;
            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "array[string]",
                    desc: "",
                    referenceNodeId: "",
                    value: "true",
                    valueType: "array[string]",
                    maxLength: 20,
                    required: true,
                    direction: 1,
                },
            ];
            //let mcpServerId = configurations.map((item) => item.mcpId);
            tempNode.settings = {
              collectIdList:appForm.collectIdList
            };
        }

        function handleSceneManageNode() {
            tempNode.nodeType = 26;

            if (!inputs || (!appForm?.sceneId && flag == 2)) {
                msg = "请配置场景参数";
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.output = [
                {
                    label: "拦截标识",
                    variable: "interceptFlag",
                    type: "boolean",
                    desc: "拦截标识，true：拦截，false-不拦截",
                    referenceNodeId: "",
                    value: "false",
                    valueType: "boolean",
                    maxLength: 10,
                    required: true,
                    direction: 1,
                },
                {
                    label: "场景数据",
                    variable: "matterGuide",
                    type: "object",
                    desc: "场景对象，包含场景数据和事项数据",
                    referenceNodeId: "",
                    value: null,
                    valueType: "object",
                    maxLength: 10,
                    required: true,
                    direction: 1,
                },
                {
                    label: "新问题",
                    variable: "newQuestion",
                    type: "string",
                    desc: "处理后的新问题，与Query有关",
                    referenceNodeId: "",
                    value: "",
                    valueType: "string",
                    maxLength: 10,
                    required: true,
                    direction: 1,
                },
                {
                    label: "系统提示词",
                    variable: "systemPrompt",
                    type: "string",
                    desc: "系统提示词，后面节点一般要跟上大模型节点",
                    referenceNodeId: "",
                    value: "",
                    valueType: "string",
                    maxLength: 100,
                    required: true,
                    direction: 1,
                },
            ];

            tempNode.settings = {
                sceneId: appForm?.sceneId,
            };
        }

        function handleFileParsingNode() {
            tempNode.nodeType = 23;
            tempNode.input = inputs.map((item) => ({
                label: "File",
                variable: "File",
                desc: "文档解析",
                referenceNodeId: item.selectedGroup,
                value: item.selectedGroup ? item.value : item.cusInput,
                type: isFileType(item.type) ? "file" : item.type,
                valueType: item.selectedGroup ? "reference" : "string",
                maxLength: 20,
                required: false,
            }));

            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "array[string]",
                    desc: "文档解析",
                    referenceNodeId: "",
                    value: "string",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
            ];

            tempNode.settings = {
                slice: appForm.slice,
            };
        }

        function handleKnowledgeAddNode() {
            tempNode.nodeType = 35;
            tempNode.input = inputs.map((item) => ({
                label: "File",
                variable: "File",
                desc: "知识库写入",
                referenceNodeId: item.selectedGroup,
                value: item.selectedGroup ? item.value : item.cusInput,
                type: isFileType(item.type) ? "file" : item.type,
                valueType: item.selectedGroup ? "reference" : "string",
                maxLength: 20,
                required: false,
            }));

            tempNode.output = [
                {
                    label: "documentId",
                    variable: "documentId",
                    type: "string",
                    desc: "知识库写入",
                    referenceNodeId: "",
                    value: "string",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
                {
                    label: "fileName",
                    variable: "fileName",
                    type: "string",
                    desc: "知识库写入",
                    referenceNodeId: "",
                    value: "string",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
                {
                    label: "fileUrl",
                    variable: "fileUrl",
                    type: "string",
                    desc: "知识库写入",
                    referenceNodeId: "",
                    value: "string",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
            ];

            tempNode.settings = {
                knowledgeId: appForm?.knowledgeIds[0].knowledgeId,
            };
        }

        function handleSqlAddNode() {
            tempNode.nodeType = 37;
            tempNode.input = inputs.map((item) => ({
                label: item.label,
                variable: item.label,
                desc: "SQL自定义",
                referenceNodeId: item.selectedGroup,
                value: item.selectedGroup ? item.value : item.cusInput,
                type: isFileType(item.type) ? "file" : item.type,
                valueType: item.selectedGroup ? "reference" : "string",
                maxLength: 20,
                required: false,
            }));

            tempNode.output = [
                {
                    label: "rowNum",
                    variable: "rowNum",
                    type: "integer",
                    desc: "SQL自定义",
                    referenceNodeId: "",
                    value: "integer",
                    valueType: "integer",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
                {
                    label: "outputList",
                    variable: "outputList",
                    type: "array[object]",
                    desc: "SQL自定义",
                    referenceNodeId: "",
                    value: "array[object]",
                    valueType: "array[object]",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
            ];

            if (appForm.sqlIds) {
                appForm.sqlIds[0].sqlFormatter = appForm.systemPrompt;
                appForm.sqlIds[0].dbFrom = appForm.sqlIds[0].knowledgeId
                    ? "knowledge"
                    : "defaultDb";
                tempNode.settings = appForm.sqlIds[0];
            } else {
                tempNode.settings = {};
            }
        }

        function handleTextParsingNode() {
            tempNode.nodeType = 24;
            tempNode.settings = appForm;
            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "array[string]",
                    desc: "文本解析",
                    referenceNodeId: "",
                    value: "string",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
            ];
        }

        function handleCodeNode() {
            tempNode.nodeType = 27;

            if (configItems && configItems.length) {
                tempNode.output = configItems.map((item) => ({
                    label: item.name,
                    variable: item.name,
                    desc: "代码",
                    referenceNodeId: "",
                    value: "string",
                    valueType: "string",
                    direction: 1,
                    type: isFileType(item.type) ? "file" : item.type,
                    maxLength: 20,
                    required: false,
                }));
            }

            tempNode.settings = {
                url: "https://localhost/code/execute",
                code: appForm.code,
                language: appForm.language,
                funcName: "main",
            };
        }

        function handleCustomNode() {
            tempNode.nodeType = 33;
            tempNode.input = nodeStoreData.startNodevariables.map((el) => {
                let item = el.label ? el : el[0];
                return {
                    label: item.label,
                    desc: "tool",
                    variable: item.label,
                    referenceNodeId: item.selectedGroup,
                    value: item.selectedGroup ? item.value : item.cusInput,
                    type: isFileType(item.type) ? "file" : item.type,
                    valueType: item.selectedGroup ? "reference" : "string",
                    maxLength: 20,
                    required: false,
                };
            });

            tempNode.output = nodeStoreData.output;
            tempNode.settings = nodeStoreData.settings;
        }

        function handleModelNode() {
            tempNode.nodeType = 4;

            if (!appForm?.userPrompt && flag == 2) {
                msg = "请配置用户提示词";
                return { nodes: nodes, edges: edges, msg: msg };
            }

            if (!nodeStoreData?.inputs && flag == 2) {
                msg = app.$t("pleaseConfigureLargeModelNodeParams");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.output = [
                {
                    label: "text",
                    variable: "text",
                    type: "string",
                    desc: "consequat",
                    referenceNodeId: "exercitation sunt",
                    value: "string",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
                {
                    label: "reasoningContent",
                    variable: "reasoningContent",
                    type: "string",
                    desc: "reasoningContent",
                    referenceNodeId: "exercitation sunt",
                    value: "string",
                    valueType: "string",
                    maxLength: 5000,
                    required: true,
                    direction: 1,
                },
            ];

            tempNode.settings = { ...appForm };
        }

        function handleAiImageNode() {
            tempNode.nodeType = 40;
            console.log(appForm,"aiImage");
            // const regex = /^(10|[1-9])(\.\d+)?$/;
            // const value=String(appForm.inputs[2].cusInput)
            // if (!(regex.test(value) && parseFloat(value) >= 1 &&  parseFloat(value) <= 10) && flag == 2) {
            //     msg = "自由度输入0-10的浮点数";
            //     return { nodes: nodes, edges: edges, msg: msg };
            // }

            // if (!nodeStoreData?.inputs && flag == 2) {
            //     msg = app.$t("pleaseConfigureLargeModelNodeParams");
            //     return { nodes: nodes, edges: edges, msg: msg };
            // }
            let width;
            let height;
            switch(appForm.aspectRatio){
                case "1:1":width=1024,height=1024;break;
                case "3:4":width=864,height=1152;break;
                case "4:3":width=1152,height=864;break;
                case "16:9":width=1280,height=720;break;
                case "9:16":width=720,height=1280;break;
                case "2:3":width=832,height=1248;break;
                case "3:2":width=1248,height=832;break;
                case "21:9":width=1512,height=648;break;
            }
            if(!appForm.aspectRatio){
                msg = "请选择图片宽高比";
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "string",
                    desc: "AI生图",
                    referenceNodeId: "",
                    value: "string",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
            ];

            tempNode.settings = { 
                aiImageModelId:appForm.modelId,
                aiImageUserPrompt:appForm.aiImageUserPrompt,
                imageUrls:appForm.imageUrls?[appForm.imageUrls.urlPath]:null,
                width,
                height,
                ratio:appForm.aspectRatio
             };
        }

        function handleAiVideoNode() {
            tempNode.nodeType = 41;
            console.log(appForm,"aiVideo");

            // if (!nodeStoreData?.inputs && flag == 2) {
            //     msg = app.$t("pleaseConfigureLargeModelNodeParams");
            //     return { nodes: nodes, edges: edges, msg: msg };
            // }
            let width;
            let height;
            switch(appForm.ratio){
                case "1:1":width=1024,height=1024;break;
                case "3:4":width=864,height=1152;break;
                case "4:3":width=1152,height=864;break;
                case "16:9":width=1280,height=720;break;
                case "9:16":width=720,height=1280;break;
                case "2:3":width=832,height=1248;break;
                case "3:2":width=1248,height=832;break;
                case "21:9":width=1512,height=648;break;
            }

            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "string",
                    desc: "AI视频",
                    referenceNodeId: "",
                    value: "string",
                    valueType: "string",
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
            ];
            // let resolution=appForm.videoParams?appForm.videoParams[0].cusInput:null;
            // let ratio=appForm.videoParams?appForm.videoParams[1].cusInput:null;
            // let duration=appForm.videoParams?appForm.videoParams[2].cusInput:null;
            // let framepersecond=appForm.videoParams?appForm.videoParams[3].cusInput:null;
            // let camerafixed=appForm.videoParams?appForm.videoParams[4].cusInput:null;
            tempNode.settings = { 
                aiVideoModelId:appForm.modelId,
                aiVideoUserPrompt:appForm.aiVideoUserPrompt,
                imageUrls:appForm.imageUrl?appForm.imageUrl.urlPath:null,
                subjectImageUrl:appForm.subjectImageUrl?[appForm.subjectImageUrl.urlPath]:null,
                promptOptimizer:appForm.promptOptimizer,
                resolution:appForm.resolution,
                ratio:appForm.ratio,
                duration:appForm.duration,
                framepersecond:appForm.framepersecond,
                camerafixed:appForm.camerafixed,
                width,
                height
             };
        }

        function handleAgentNode() {
            if (
                allEdges.find(
                    (item) =>
                        item.store.data.target.cell == tempNode.nodeId &&
                        item.store.data.source.cell === startNode.nodeId
                )
            ) {
                tempNode.input = startNode.output;
                tempNode.input[0].referenceNodeId = startNode.nodeId;
                tempNode.input[0].valueType = "reference";
                tempNode.input[0].value = "question";
            }

            tempNode.nodeType = 38;
            let mapIds = appForm.mcpIdList?.map((item) => item.mcpId);
            let knnIds = appForm.knnIdList?.map((item) => item.knowledgeId);

            tempNode.settings = {
                mcpIdList: mapIds,
                knnIdList: knnIds,
                param: {
                    contentScore: 1.49,
                    rangeContentScore: 0.88,
                    qaTitleScore: 1.88,
                    qaRangeTitleScore: 0.91,
                    qaContentScore: 1.88,
                    qaRangeContentScore: 0.88,
                    filterNum: 10,
                    inputType: 1,
                },
                knnType:
                    "findQaTitle,findQaContent,builtIn,knowledgeContent,documentLibrary,WebsiteStrategy,structuredStrategy,multiMediaLibrary",
                systemPrompt: appForm.systemPrompt,
                userPrompt: appForm.userPrompt,
                scene: appForm.sence,
                modelId: appForm.modelId,
            };
        }
        function handleExistAgentNode() {
            tempNode.nodeType = 38;
            tempNode.settings = {
                componentId: nodeStoreData.toolData.type === 'dialogue' ? nodeStoreData.toolData.applicationId + '_5' : nodeStoreData.toolData.applicationId,
                scene: appForm.sence,
            };
        }
        function handleGlobalConditionNode() {
            tempNode.nodeType = 408;
            tempNode.settings = {
                condition: appForm.condition,
            };
        }

        function handleChangqijiyiNode() {
            tempNode.nodeType = 22;
            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "array[string]",
                    desc: "",
                    nodeId: nodeStore.id,
                    referenceNodeId: "",
                    value: "result",
                    valueType: "reference",
                    maxLength: 20,
                    required: true,
                    direction: 1,
                },
            ];

            tempNode.settings = {
                modelId: "f570229417ef4d79814ff51a65447eb5",
            };
        }

        function handleParamsFilterNode() {
            let filterVariables = nodeStoreData.filterVariables
                ? JSON.parse(nodeStoreData.filterVariables)
                : [];
            let extractParameters = [];

            tempNode.nodeType = 8;

            if (!inputs && flag == 2) {
                msg = app.$t("pleaseConfigureParameterExtractorNodeParams");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            if (!filterVariables && flag == 2) {
                msg = app.$t("pleaseConfigureParameterExtractorNodeParams");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.output = filterVariables.map((item) => {
                extractParameters.push({
                    fieldName: item.name,
                    description: item.description,
                    fieldType: item.type,
                });

                return {
                    label: item.name,
                    variable: item.name,
                    desc: item.description,
                    referenceNodeId: "",
                    value: "",
                    type: isFileType(item.type) ? "file" : item.type,
                    valueType: isFileType(item.type) ? "file" : item.type,
                    maxLength: item.maxLength,
                    required: item.required,
                };
            });

            tempNode.settings = {
                modelId: appForm.modelId,
                userPrompt: appForm.userPrompt,
                systemPrompt: appForm.systemPrompt,
                extractParameters,
                extractJsonParameters: extractParameters,
            };
        }

        function handleInterceptWordNode() {
            let configurations = JSON.parse(nodeStoreData.configurations);

            if (
                !configurations.every(
                    (item) => item.interceptWordContent !== ""
                ) &&
                flag == 2
            ) {
                msg = app.$t("pleaseConfigureSensitiveWordParameters");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            if (!inputs && flag == 2) {
                msg = app.$t("pleaseConfigureSensitiveWordParameters");
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.nodeType = 20;
            tempNode.output = [
                {
                    label: "result",
                    variable: "result",
                    type: "string",
                    desc: "",
                    referenceNodeId: "",
                    value: "true",
                    valueType: "string",
                    maxLength: 20,
                    required: true,
                    direction: 1,
                },
            ];

            tempNode.settings = {
                configurations: configurations,
            };
        }

        function handleVariableNode() {
            tempNode.nodeType = 25;
            tempNode.output = [
                {
                    label: appForm.inputs[0]?.label,
                    variable: appForm.inputs[0]?.label,
                    type: appForm.cusType,
                    desc: "consequat",
                    referenceNodeId: "exercitation sunt",
                    value: appForm.inputs[0]?.label,
                    valueType: appForm.cusType,
                    maxLength: 500,
                    required: true,
                    direction: 1,
                },
            ];
        }

        function handleIterationNode() {
            tempNode.nodeType = 32;
            tempNode.settings = {
                componentId: nodeStoreData.toolData.componentId,
                asyncFlag: appForm.asyncFlag,
            };

            if (!appForm?.selectOutput && flag == 2) {
                msg = "请配置工作流输出节点";
                return { nodes: nodes, edges: edges, msg: msg };
            }

            tempNode.output = workFlowOutputs.map((item) => ({
                label: item.label,
                variable: item.label,
                desc: nodeLabel,
                referenceNodeId: item.selectedGroup,
                value: item.selectedGroup ? item.value : item.cusInput,
                type: isFileType(item.type) ? "file" : item.type,
                valueType: item.selectedGroup ? "reference" : "string",
                maxLength: 20,
                required: false,
          }));
        }

        function handleDiedaiNode() {
            let componentId = "";
            let componentldList = [];
            let childrens = node.getDescendants();

            if (childrens.length) {
                childrens.forEach((item) => {
                    if (
                        item.store.data.shape !== "dag-edge" &&
                        graph.isRootNode(item)
                    ) {
                        componentldList.push(item.store.data.id);
                        componentId = item.store.data.id;
                    }
                });
            }

            tempNode.nodeType = 6;
            tempNode.settings = {
                componentId: componentId,
                componentldList: componentldList,
                parallel: appForm.parallel,
                process_num: appForm.process_num,
                asyncFlag: appForm.asyncFlag,
                item_result: item_result.map((item) => ({
                    label: item.label,
                    variable: item.label,
                    desc: nodeLabel,
                    referenceNodeId: item.selectedGroup,
                    value: item.selectedGroup ? item.value : item.cusInput,
                    type: isFileType(item.type) ? "file" : item.type,
                    valueType: item.selectedGroup ? "reference" : "string",
                    maxLength: 20,
                    required: false,
                })),
            };

            tempNode.output = outputs.map((item) => ({
                label: item.label,
                variable: item.label,
                desc: nodeLabel,
                referenceNodeId: item.selectedGroup,
                value: item.selectedGroup ? item.value : item.cusInput,
                type: isFileType(item.type)
                    ? "file"
                    : [
                          "string",
                          "integer",
                          "boolean",
                          "object",
                          "number",
                      ].includes(item.type)
                    ? `array[${item.type}]`
                    : item.type,
                valueType: item.selectedGroup ? "reference" : "string",
                maxLength: 20,
                required: false,
            }));
        }

        function handleDefaultNode() {
            tempNode.nodeType = 21;
            tempNode.settings = {
                componentId: nodeStoreData.toolData.componentId,
            };

            tempNode.input = nodeStoreData.startNodevariables.map((el) => {
                let item = el.label ? el : el[0];
                return {
                    label: item.label,
                    desc: "tool",
                    variable: item.label,
                    referenceNodeId: item.selectedGroup,
                    value: item.selectedGroup ? item.value : item.cusInput,
                    type: isFileType(item.type) ? "file" : item.type,
                    valueType: item.selectedGroup ? "reference" : "string",
                    maxLength: 20,
                    required: false,
                };
            });

            tempNode.output = nodeStoreData.endNode.map((item) => ({
                desc: item.desc,
                direction: item.direction,
                label: item.label,
                maxLength: item.maxLength,
                referenceNodeId: item.referenceNodeId,
                required: item.required,
                type: isFileType(item.type) ? "file" : item.type,
                value: item.selectedGroup ? item.value : item.cusInput,
                valueType: item.valueType,
                variable: item.variable,
            }));
        }
    });
    function updateInputTypes(nodes) {
      // 创建节点ID到节点的映射
      const nodeMap = new Map();
      //配置全局添加节点的值开始
      const globalConditionNodes = nodes.filter(node => node.nodeType === 408)
      const globalConditionNodeIds = []
      const globalConditionNodeVal = {}
      const edgs = []
      globalConditionNodes.forEach(item => {
        globalConditionNodeIds.push(item.nodeId)
        globalConditionNodeVal[item.nodeId] = item.settings.condition
      });
      allEdges.forEach(item => {
        if(globalConditionNodeIds.includes(item.store.data.source.cell)){
          let sourceId = item.store.data.source.cell
          let targetId = item.store.data.target.cell
          let obj = {}
          obj['targetId'] = targetId
          obj['condition'] = globalConditionNodeVal[sourceId]
          edgs.push(obj)
        }
      });
      nodes.forEach(node => nodeMap.set(node.nodeId, node));
      //配置全局添加节点的值结束
      // 遍历所有节点处理输入
      return nodes.map(node => {
        // 如果没有 input 字段则直接返回原节点
        if (!node || !node.input || !Array.isArray(node.input)) {
            return node;
        }
        const allConditions = []
        edgs.forEach(edge => {
          if (edge.targetId === node.nodeId) {
            allConditions.push(edge.condition)
          }
        })
        if(allConditions.length){
          node.settings.conditionList = allConditions
        }
        return {
          ...node,
          input: node.input.map(input => {
            if (!input.referenceNodeId) return input;

            // 查找引用节点
            const sourceNode = nodeMap.get(input.referenceNodeId);
            if (!sourceNode) return input;

            // 查找目标字段
            const targetField = sourceNode.output.find(
              field => field.variable === input.value
            );

            return {
              ...input,
              type: targetField ? targetField.type : input.type
            };
          })
        }
      });
    }
    // 处理边数据
    const sourceNodeIds = casesIdArr.map((el) => el.sourceNodeId);
    const edges = allEdges.map((edge) => {
        const edgeStoreData = edge.store.data;
        const obj = {
            componentId: component ? component.componentId : null,
            sourceNodeId: edgeStoreData.source.cell,
            targetNodeId: edgeStoreData.target.cell,
        };

        if (sourceNodeIds.includes(edgeStoreData.source.cell)) {
            obj.caseId = edgeStoreData.source.port;
        }

        return obj;
    });

    // 更新节点类型
    let updatedNodes = updateInputTypes(nodes);
    updatedNodes = updatedNodes.filter(node => node.nodeType !== 408);
    return {
        nodes: updatedNodes,
        edges: edges,
        msg: msg,
    };
}
