import request from "@/utils/request";

// 相似问题合并 - 根据重复度阀值设置重复度
export function apiGetKnowledgeDataScope(data) {
    return request({
        url: "/knowledgeData/getKnowledgeDataScope",
        method: "post",
        data,
    });
}

// 相似问题合并 - 查询被删除的QA
export function apiGetDeleteKnowledgeData(data) {
    return request({
        url: "/knowledgeData/getDeleteKnowledgeData",
        method: "post",
        data,
    });
}

// 相似问题合并 - 删除QA对
export function apiDeleteKnowledgeData(data) {
    return request({
        url: "/knowledgeData/deleteKnowledgeData",
        method: "post",
        data,
    });
}

// 相似问题合并 - 恢复删除的QA对数据
export function apiRecoverKnowledgeData(data) {
    return request({
        url: "/knowledgeData/recoverKnowledgeData",
        method: "post",
        data,
    });
}

//调试添加知识库api接口
export function runApiKnowledgeApi(data) {
    return request({
        url: "/knowledgeApi/runApi",
        method: "post",
        data,
    });
}

//保存编辑添加知识库api接口
export function addOrUpdatKnowledgeApi(data) {
    return request({
        url: "/knowledgeApi/addOrUpdate",
        method: "post",
        data,
    });
}

//知识库api接口列表
export function getKnowledgeApi(data) {
    return request({
        url: "/knowledgeApi/pageQuery",
        method: "post",
        data,
    });
}

//知识库api根据ids列表批量删除api数据
export function delKnowledgeApi(data) {
    return request({
        url: "/knowledgeApi/deleteByIdList",
        method: "post",
        data,
    });
}

//api详情
export function queryDetailById(data) {
    return request({
        url: "/knowledgeApi/queryDetailById?id="+data.knowledgeApiId,
        method: "get"
       
    });
}

//api切片列表
export function getApiDatas(data) {
    return request({
        url: "/apiData/getApiDatas",
        method: "post",
        data,
    });
}
//请求记录
export function pageQuery(data) {
    return request({
        url: "/knowledgeApiRequestRecord/pageQuery",
        method: "post",
        data,
    });
}
//删除api切片
export function deleteApiData(data) {
    return request({
        url: "/apiData/deleteApiData",
        method: "post",
        data,
    });
}

//添加api数据到es
export function addApiDataApi(data) {
    return request({
        url: "/apiData/addApiData",
        method: "post",
        data,
    });
}
