import request from "@/utils/request";
//数据库列表
export function getDataCollectInfoList(data) {
    return request({
        url: "/dataCollectInfo/getDataCollectInfoList",
        method: "post",
        data,
    });
}
//添加数据库
export function addDataCollectInfo(data) {
    return request({
        url: "/dataCollectInfo/addDataCollectInfo",
        method: "post",
        data,
    });
}
//直连数据库
export function addCollectBaseConfig(data) {
    return request({
        url: "/collectBaseConfig/addCollectBaseConfig",
        method: "post",
        data,
    });
}
//删除首页数据表
export function deleteDataCollectInfo(data) {
    return request({
        url: "/dataCollectInfo/deleteDataCollectInfo",
        method: "post",
        data,
    });
}
//删除详情数据表
export function deleteCollectBaseConfig(data) {
    return request({
        url: "/collectBaseConfig/deleteCollectBaseConfig",
        method: "post",
        data,
    });
}
//获取数额集详情
export function getCollectBaseConfig(data) {
    return request({
        url: "/collectBaseConfig/getCollectBaseConfig",
        method: "post",
        data,
    });
}
//编辑数据库的数据集
export function editCollectData(data) {
    return request({
        url: "/collectBaseConfig/editCollectData",
        method: "post",
        data,
    });
}
//编辑数据库
export function editCollectConfig(data) {
    return request({
        url: "/collectBaseConfig/editCollectConfig",
        method: "post",
        data,
    });
}
//获取数据集的表数据
export function getCollectDataList(data) {
    return request({
        url: "/collectBaseConfig/getCollectDataList",
        method: "post",
        data,
    });
}