import request from "@/utils/request";

// mcp - 列表
export function getMcpServerList(data) {
    return request({
        url: "/mcpServer/getMcpServerList",
        method: "post",
        data,
    });
}
// mcp - 新增
export function addMcpServer(data) {
    return request({
        url: "/mcpServer/addMcpServer",
        method: "post",
        data,
    });
}
// mcp - 修改
export function updateMcpServer(data) {
    return request({
        url: "/mcpServer/updateMcpServer",
        method: "post",
        data,
    });
}
// mcp - 删除
export function deleteMcpServer(data) {
    return request({
        url: "/mcpServer/deleteMcpServer",
        method: "post",
        data,
    });
}
// mcp - 查询
export function checkMcp(data) {
    return request({
        url: "/mcpServer/checkMcp",
        method: "post",
        data,
    });
}

// mcp - 查询
export function getDetail(mcpid) {
    return request({
        url: "/mcpServer/getDetail",
        method: "post",
		data:{
			mcpId:mcpid
		}
    });
}
export function getListAndDetail(data) {
    return request({
        url: "/mcpServer/getListAndDetail",
        method: "post",
        data,
    });
}
export function updateStatus(data) {
    return request({
        url: "/mcpServerUser/updateStatus",
        method: "post",
        data,
    });
}
export function newUpdateStatus(data){
    return request({
        url:"/mcpServer/updateMcpStatus",
        method:"POST",
        data
    })
}
export function getMyOpenMcpServerList(data) {
    return request({
        url: "/mcpServer/getMyOpenMcpServerList",
        method: "post",
        data,
    });
}

export function mcpTestApi(data) {
    return request({
        url: "/mcpServer/testApi",
        method: "post",
        data,
    });
}


