import request from "@/utils/request";


// sql - 新增
export function addComponentNodeTableInfo(data) {
    return request({
        url: "/componentNodeTableInfo/addComponentNodeTableInfo",
        method: "post",
        data,
    });
}
// sql - 列表
export function getComponentNodeTableInfoList(data) {
    return request({
        url: "/componentNodeTableInfo/getComponentNodeTableInfoList",
        method: "post",
        data,
    });
}
// sql - 删除
export function deleteMcpServer(data) {
    return request({
        url: "/componentNodeTableInfo/deleteNodeTableInfor",
        method: "post",
        data,
    });
}
// sql - 查询表字段列表
export function addNodeTableFields(data) {
    return request({
        url: "/componentNodeTableField/getNodeTableFields",
        method: "post",
        data,
    });
}

// sql - 查询表数据列表
export function getNodeTableDataList(data) {
    return request({
        url: "/componentNodeTableField/getNodeTableDataList",
        method: "post",
		data
    });
}
// sql - 查询表数据列表
export function addComponentNodeTableField(data) {
    return request({
        url: "/componentNodeTableField/addComponentNodeTableField",
        method: "post",
		data
    });
}
// sql - 查询表数据列表
export function addNodeTableData(data) {
    return request({
        url: "/componentNodeTableField/addNodeTableData",
        method: "post",
		data
    });
}