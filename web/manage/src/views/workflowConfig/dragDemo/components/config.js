import app from "../../../../main";
const config = [
    // {
    //     type: "output",
    //     img: "kaishi",
    //     label: app.$t("start"),
    //     variables: [
    //         {
    //             description: "question",
    //             maxLength: 20,
    //             name: "question",
    //             required: true,
    //             type: "string",
    //         },
    //     ],
    //     attribute: "process",
    // },
    {
        type: "onlyIn",
        img: "jieshu",
        label: app.$t("end"),
        variables: [{ name: "", value: "", type: "", selectedGroup: "" }],
        attribute: "process",
        intro:"支持中间过程的消息输出，支持流式和非流式两种方式"
    },
    {
        type: "knowledge",
        img: "zhishijiansuo",
        label: app.$t("knowledgeRetrieval"),
        variables: [
            { name: "result", description: "result", type: "array[string]" },
        ],
        attribute: "knowledge",
        intro:"这个功能让你能够从知识库中查找与用户问题有关的文本内容。"
    },
    {
        type: "knowledgeAdd",
        img: "workflow-zhishixieru",
        label: app.$t("knowledgeAdd"),
        variables: [
            { name: "documentId", description: "documentId", type: "string" },
            { name: "fileName", description: "fileName", type: "string" },
            { name: "fileUrl", description: "fileUrl", type: "string" },
        ],
        attribute: "knowledge",
        intro:"这个功能让你能够从知识库中查找与用户问题有关的文本内容。"
    },
    {
            type: "sqlAdd",
            img: "workflow-shujuku",
            label: app.$t("sqlAdd"),
            variables: [
                { name: "rowNum", description: "rowNum", type: "integer" },
                { name: "outputList", description: "outputList", type: "array[object]" }
            ],
            attribute: "knowledge",
            intro:"通过自定义SQL查询、修改、删除、增加指定数据库表的数据。"
    },
    // {
    //     type: "message",
    //     img: "message",
    //     label: app.$t('message'),
    //     variables: [
    //         { name: "result", description: "result", type: "array[object]" },
    //     ]
    // },
    // {
    //     type: "qa",
    //     img: "qa",
    //     label: app.$t('qa'),
    //     variables: [
    //         { name: "result", description: "result", type: "array[object]" },
    //     ]
    // },
    {
        type: "variable",
        img: "bianliang",
        label: app.$t("variable"),
        variables: [
            { name: "text", description: "text", type: "array[object]" },
        ],
        attribute: "knowledge",
        intro:"用于读取和写入项目中的变量，变量名称必须与项目中的变量名称相匹配"
    },
    {
        type: "dataset",
        img: "workflow-shujuku",
        label: app.$t("dataset"),
        variables: [
            { name: "result", description: "result", type: "array[string]" },
        ],
        attribute: "knowledge",
        intro:"这个功能让你能够从数据集中查找与用户问题有关的文本内容。"
    },
    {
        type: "condition",
        img: "tiaojianfenzhi",
        label: app.$t("conditionNode"),
        attribute: "convert",
        intro:"若设定的条件成立则运行对应的分支，不成立则运行“否则“分支"
    },
    {
        type: "qaType",
        img: "wentifenlei",
        label: app.$t("questionClassifier"),
        variables: [
            {
                name: "result",
                description: "result",
                type: "array[object]",
            },
        ],
        attribute: "convert",
        intro:"定义用户问题的分类条件，大模型能够根据分类描述定义对话的进展方式"
    },
    {
        type: "model",
        img: "workflow-damoxing",
        label: app.$t("largeModel"),
        variables: [
            { name: "text", description: "text", type: "string" },
            { name: "reasoningContent", description: "reasoningContent", type: "string" },
        ],
        attribute: "process",
        intro:"调用大语言模型,使用变量和提示词生成回复"
    },
    {
        type: "paramsFilter",
        img: "canshutiqu",
        label: app.$t("parameterExtractor"),
        variables: [
            { name: "result", description: "result", type: "array[object]" },
        ],
        attribute: "convert",
        intro:"支持对输入进行参数提取"
    },
    {
      type: "tool",
      img: "gongjujiedian",
      label: app.$t("plugInUnit"),
      variables: [
          { name: "result", description: "result", type: "array[object]" },
      ],
      attribute: "tool",
      intro:"支持自定义工具"
    },
    {
      type: "deduce",
      img: "gongjujiedian",
      label: '推演',
      variables: [
          { name: "result", description: "result", type: "array[object]" },
      ],
      attribute: "tool",
      intro:"推演工具"
    },
    {
      type: "mcp",
      img: "MCP",
      label: 'MCP',
      variables: [
          { name: "result", description: "result", type: "array[object]" },
      ],
      attribute: "tool",
      intro:"MCP服务"
    },
    {
      type: "http",
      img: "disanfangjiekou",
      label: 'HTTP',
      variables: [{ name: "httpResult", description: "httpResult", type: "string" }],
      nodeApi: '{"nodeName":"api","nodeType":2,"input":[],"output":[{"label":"httpResult","variable":"httpResult","type":"array[object]","desc":"API节点的默认出参","referenceNodeId":"api","value":"data","valueType":"reference","maxLength":20,"required":true,"direction":1}],"settings":{"url":"","Accept":"*/*","method":"GET","headers":[],"contentType":"application/json","requestBody":[],"responseBody":"","responseType":false},"next":null}',
      attribute: "tool",
      intro:"支持通过 HTTP 请求获取数据。"
    },
    {
      type: "interceptWord",
      img: "workfolw-anquanlanjie",
      label: app.$t("sensitiveWordInterception"),
      variables: [
          { name: "result", description: "result", type: "array[object]" },
      ],
      attribute: "tool",
      intro:"支持对敏感词进行拦截"
    },
    {
      type: "aiImage",
      img: "workfolw-tuxiangshengcheng",
      label: "AI生图",
      variables: [
          { name: "result", description: "result", type: "string" },
      ],
      attribute: "tool",
      intro:"通过文字描述生成图片，支持添加参考图"
    },
    {
      type: "aiVideo",
      img: "workfolw-shipinshengcheng",
      label: "AI视频",
      variables: [
          { name: "result", description: "result", type: "string" },
      ],
      attribute: "tool",
      intro:"通过文字描述或图片生成视频"
    },
    {
        type: "iteration",
        img: "workflow-gongzuoliu",
        label: app.$t("workflow"),
        variables: [
            { name: "result", description: "result", type: "array[object]" },
        ],
        attribute: "process",
        intro:"可引用当前工作流的输入输出"
    },
    {
        type: "changqijiyi",
        img: "changqijiyi",
        label: "长期记忆",
        variables: [
            {
                name: "result",
                description: "result",
                type: "array[string]",
            },
        ],
        attribute: "knowledge",
        intro:"用于调用长期记忆，获取用户的个性化信息，智能体必须打开长期记忆"
    },
    // {
    //     type: "wenda",
    //     img: "wenda",
    //     label: '问答',
    //     variables: [
    //         { name: "result", description: "result", type: "array[string]" },
    //     ],
    // },
    // {
    //     type: "xiaoxi",
    //     img: "xiaoxi",
    //     label: '消息',
    //     variables: [
    //         { name: "result", description: "result", type: "array[string]" },
    //     ],
    // },
    // {
    //     type: "shujuku",
    //     img: "shujuku",
    //     label: '数据库',
    //     variables: [
    //         { name: "result", description: "result", type: "array[string]" },
    //     ],
    // },
    // {
    //     type: "yitushibie",
    //     img: "yitushibie",
    //     label: '意图识别',
    //     variables: [
    //         { name: "result", description: "result", type: "array[string]" },
    //     ],
    // },
    {
        type: "diedai",
        img: "diedai",
        label: '迭代',
        parent: true,
        variables: [
            { name: "result", description: "result", type: "array[string]" },
        ],
        attribute: "convert",
        intro:"迭代节点"
    },
    {
        type: "code",
        img: "daimazhihang",
        label: '代码',
        variables: [
            { name: "result", description: "result", type: "array[string]" },
        ],
        attribute: "convert",
        intro:"编写代码，处理输入变量来生成返回值"
    },
    {
        type: "textParsing",
        img: "wenbenchuli",
        label: "文本解析",
        variables: [
            { name: "result", description: "result", type: "array[string]" },
        ],
        attribute: "component",
        intro:"用于处理多个字符串类型变量的格式"
    },
    {
        type: "fileParsing",
        img: "wendangtiquqi",
        label: "文档解析",
        variables: [
            { name: "result", description: "result", type: "array[string]" },
        ],
        attribute: "component",
        intro:"解析并读取文档文件中的信息，并转化为文本"
    },
    {
        type: "customNode",
        img: "tiaojianfenzhi",
        label: "自定义节点",
        variables: [
            { name: "result", description: "result", type: "array[string]" },
        ],
        attribute: "customize",
    },
    {
        type: "customNode",
        img: "tiaojianfenzhi",
        label: "自定义节点",
        variables: [
            { name: "result", description: "result", type: "array[string]" },
        ],
        attribute: "customize",
    },
    {
        type: "sceneManage",
        img: "changjing",
        label: app.$t("sceneManage"),
        variables: [
            { name: "systemPrompt", description: "系统提示词", type: "string" },
            { name: "newQuestion", description: "新问题", type: "string" },
            { name: "matterGuide", description: "场景对象", type: "object" },
            { name: "interceptFlag", description: "拦截标识", type: "boolean" },
        ],
        attribute: "process",
        intro:"允许应用调用业务场景，如：事项办理、流程处置等"
    },
    {
        type: "agent",
        img: "agent",
        label: '新Agent',
        variables: [],
        attribute: "mulAgent",
    },
    {
        type: "existAgent",
        img: "yiyouagent",
        label: '已有的单Agent应用',
        variables: [],
        attribute: "mulAgent",
    },
    {
        type: "globalCondition",
        img: "quanjutiaozhuantiaojian",
        label: '全局跳转条件',
        variables: [],
        attribute: "mulAgent",
    },
];

export default config;
