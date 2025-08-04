import request from "@/utils/request";

// 配置管理 - 列表
export function apiGetApplicationConfigList(data) {
    return request({
        url: "/applicationConfiguration/getApplicationConfigList",
        method: "post",
        data,
    });
}
// 配置管理 - 新增
export function apiAddApplicationConfig(data) {
    return request({
        url: "/applicationConfiguration/addApplicationConfig",
        method: "post",
        data,
    });
}
// 配置管理 - 修改
export function apiUpdateApplicationConfig(data) {
    return request({
        url: "/applicationConfiguration/updateApplicationConfig",
        method: "post",
        data,
    });
}
// 配置管理 - 删除
export function apiDeleteApplicationConfig(data) {
    return request({
        url: "/applicationConfiguration/deleteApplicationConfig",
        method: "post",
        data,
    });
}

//业务数据
// 删除投资线索 - 删除
export function deleteInvestmentLead(data) {
    return request({
        url: "/lhmz/deleteInvestmentLead",
        method: "post",
        data,
    });
}
// 删除咨询消息 - 删除
export function deleteLhmzMessage(data) {
    return request({
        url: "/lhmz/deleteLhmzMessage",
        method: "post",
        data,
    });
}
// 删除咨询消息 - 删除
export function getAllInvestmentLead(data) {
    return request({
        url: "/lhmz/investmentLead/getAllInvestmentLead",
        method: "get",
        params: data
    });
}
// 删除咨询消息 - 删除
export function getAllLhmzMessage(data) {
    return request({
        url: "/lhmz/lhmzMessage/getAllLhmzMessage",
        method: "get",
        params: data
    });
}
// 删除咨询消息 - 删除
export function getApplicationBusinessType(data) {
    return request({
        url: "/lhmz/getApplicationBusinessType",
        method: "get",
        params: data
    });
}