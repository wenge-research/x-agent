import request from "@/utils/request";


// 测评数据列表
export function apiGetApplicationEvaluationList(data) {
    return request({
        url: "/applicationEvaluation/list",
        method: "post",
        data
    });
}
// 智能测评数据插入
export function apiInsertApplicationEvaluation(data) {
    return request({
        url: "applicationEvaluation/insert",
        method: "post",
        data
    });
}
// 数据集导入
export function apiImportApplicationDateset(data) {
    return request({
        url: "/applicationDateset/import",
        method: "post",
        data,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}
// 数据集列表
export function apiSelectApplicationDateset(data) {
    return request({
        url: "/applicationDateset/select",
        method: "post",
        data
    });
}

// 数据集删除
export function apiDeleteApplicationDateset(data) {
    return request({
        url: "/applicationDateset/delete",
        method: "post",
        data
    });
}


// 调用大模型测试运行接口
export function apiRunApplicationModelId(data) {
    return request({
        url: "/applicationEvaluation/modelId",
        method: "post",
        data
    });
}


// 测评数据删除
export function apiDeleteApplicationEvaluation(data) {
    return request({
        url: "/applicationEvaluation/delete",
        method: "post",
        data
    });
}


// 数据集导出
export function apiExportApplicationDateset(data) {
    return request({
        url: "/applicationDateset/export",
        method: "post",
        data,
        responseType: 'blob',
    });
}
// 数据集导入
export function downloadEvaluationDataTemp(data) {
    return request({
        url: "/applicationEvaluation/downloadEvaluationDataTemp",
        method: "get",
        data,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}