import request from "@/utils/request";

// 事项管理 - 分页
export function apiGetMatterList(data) {
    return request({
        url: "/matterGuide/getMatterList",
        method: "post",
        data,
    });
}
// 事项管理 - 是否启用事项
export function apiUpdateShowFlag(data) {
    return request({
        url: "/matterGuide/updateShowFlag",
        method: "post",
        data,
    });
}
// 事项管理 - 删除
export function apiDeleteMatter(data) {
    return request({
        url: "/matterGuide/deleteMatter",
        method: "post",
        data,
    });
}
// 创建事项 - 事项类型数据源
export function apiGetMatterGuideTypeList(data) {
    return request({
        url: "/matterGuideGroup/getMatterGuideTypeList",
        method: "post",
        data,
    });
}
// 创建事项 - 新增保存
export function apiAddMatter(data) {
    return request({
        url: "/matterGuide/addMatter",
        method: "post",
        data,
    });
}
// 事项详情
export function apiGetMatterById(id) {
    return request({
        url: `/matterGuide/getMatterById?id=${id}`,
        method: "get",
    });
}
// 创建事项 - 编辑保存
export function apiEditMatter(data) {
    return request({
        url: "/matterGuide/editMatter",
        method: "post",
        data,
    });
}
// 创建事项 - 分组列表
export function apiGetMatterGuideGroupList(data) {
    return request({
        url: "/matterGuideGroup/getMatterGuideGroupList",
        method: "post",
        data,
    });
}
// 创建事项 - 删除分组
export function apiDelMatterGuideGroupById(id) {
    return request({
        url: `/matterGuideGroup/delMatterGuideGroupById?id=${id}`,
        method: "get",
    });
}
// 创建事项 - 新增/编辑分组
export function apiAddOrUpdateMatterGroupList(data) {
    return request({
        url: `/matterGuideGroup/addOrUpdateMatterGroupList`,
        method: "post",
        data,
    });
}
// 创建事项 - 字段名判重
export function apiCheckNameCodeExists(data) {
    return request({
        url: `/matterGuideFiled/checkNameCodeExists`,
        method: "post",
        data,
    });
}
// 事项处置 - 获取检索条件
export function apiGetSearchFiledByMatterId(matterId) {
    return request({
        url: `/matterGuideInfo/getSearchFiledByMatterId?matterId=${matterId}`,
        method: "get",
    });
}
// 事项处置 - 获取表头字段信息
export function apiGetTableHeadFiledByMatterId(matterId) {
    return request({
        url: `/matterGuideInfo/getTableHeadFiledByMatterId?matterId=${matterId}`,
        method: "get",
    });
}
