import request from "@/utils/request";

/**
 * 运营板块的接口
 * */ 


// 应用总数
export function applicationCount(data) {
    return request({
        url: "/operationManagement/applicationCount",
        method: "get",
        data
    });
}

// 知识库总数
export function knowledgeCount(data) {
    return request({
        url: "/operationManagement/knowledgeCount",
        method: "get",
        data
    });
}

// 总使用量、总使用用户数
export function getUsingInfo(data) {
    return request({
        url: "/operationManagement/getUsingInfo",
        method: "get",
        data
    });
}


// 产生问答总数
export function getQaCount(data) {
    return request({
        url: "/operationManagement/getQaCount",
        method: "get",
        data
    });
}

// 运营-查阅问题排行榜Top50/topN
export function questionChartsTop(data) {
    return request({
        url: "/operationManagement/questionChartsTop50",
        method: "post",
        data,
    });
}

// 运营-token消耗
export function getTokenConsumption(data) {
    return request({
        url: "/operationManagement/getTokenConsumption",
        method: "post",
        data,
    });
}

// 知识库统计分析数据总量
export function knowledgeCountInfo(data) {
    return request({
        url: "/knowledgeInfo/knowledgeCount",
        method: "post",
        data
    });
}

// 知识库关联应用数统计分析数据总量
export function applicationCountInfo(data) {
    return request({
        url: "/knowledgeInfo/applicationCount",
        method: "post",
        data
    });
}

// 知识库数据量增长情况
export function knowledgeCountTrend(data) {
    return request({
        url: "/knowledgeInfo/knowledgeCountTrend",
        method: "post",
        data
    });
}
// // 部门科室数
export function getDeptKnowledgeData(data) {
    return request({
        url: "/knowledgeInfo/getDeptKnowledgeData",
        method: "post",
        data
    });
}
//知识库列表
export function getKnowledgeList(data) {
    return request({
        url: "/knowledgeInfo/getKnowledgeList",
        method: "post",
        data
    });
}
