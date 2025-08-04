import request from "@/utils/request";

// 大模型管理 - 分页
export function apiGetLlmPageList(data) {
    return request({
        url: "/llmInfo/getLlmPageList",
        method: "post",
        data,
    });
}
// 大模型管理 - 一键部署deepseek
export function deployLlm(data) {
    return request({
        url: "/llmInfo/deployLlm",
        method: "post",
        data,
    });
}
// 大模型管理 - 查询大模型厂商列表
export function apiGetLlmManufacturer(data) {
    return request({
        url: "/llmInfo/getLlmManufacturer",
        method: "post",
        data,
    });
}
// 大模型管理 - 部署编辑,新增大模型
export function apiEditLlm(data) {
    return request({
        url: "/llmInfo/editLlm",
        method: "post",
        data,
    });
}
// 大模型管理 - 部署编辑,新增大模型
export function apiAddLlm(data) {
    return request({
        url: "/llmInfo/addLlmInfo",
        method: "post",
        data,
    });
}
// 音频管理 - 查询音频标签
export function apiGetTtsTags(data) {
    return request({
        url: "/voiceComponentInfo/getTtsTags",
        method: "post",
        data,
    });
}
// 音频管理 - 删除音频
export function apiDeleteVoice(data) {
    return request({
        url: "/voiceComponentInfo/deleteInfo",
        method: "post",
        data,
    });
}
// 音频管理 - 创建保存
export function apiAddInfo(data) {
    return request({
        url: "/voiceComponentInfo/addInfo",
        method: "post",
        data,
    });
}

// 查看知识库分数详情
export function getScoreData(data) {
    return request({
        url: "/dialogue/getScoreData",
        method: "post",
        data,
    });
}

// 词典管理 敏感词列表
export function getInterceptWordHouseList(data) {
    return request({
        url: "/interceptWordHouse/getInterceptWordHouseList",
        method: "post",
        data,
    });
}

// 敏感词库-新增词库
export function addInterceptWordHouse(data) {
    return request({
        url: "/interceptWordHouse/addInterceptWordHouse",
        method: "post",
        data,
    });
}

// 敏感词库-编辑词库
export function updateInterceptWordHouse(data) {
    return request({
        url: "/interceptWordHouse/updateInterceptWordHouse",
        method: "post",
        data,
    });
}

// 敏感词库-删除词库
export function deleteInterceptWordHouse(data) {
    return request({
        url: "/interceptWordHouse/deleteInterceptWordHouse",
        method: "post",
        data,
    });
}

// 敏感词库-开启关闭
export function updateStatus(data) {
    return request({
        url: "/interceptWordHouse/updateStatus",
        method: "post",
        data,
    });
}

// 敏感词-分页列表
export function getInterceptWordList(data) {
    return request({
        url: "/interceptWord/getInterceptWordList",
        method: "post",
        data,
    });
}

// 敏感词-新增词
export function addInterceptWord(data) {
    return request({
        url: "/interceptWord/addInterceptWord",
        method: "post",
        data,
    });
}

// 敏感词-编辑词
export function updateInterceptWord(data) {
    return request({
        url: "/interceptWord/updateInterceptWord",
        method: "post",
        data,
    });
}

// 敏感词-删除词
export function deleteInterceptWord(data) {
    return request({
        url: "/interceptWord/deleteInterceptWord",
        method: "post",
        data,
    });
}

// 敏感词-开启关闭
export function interceptWordUpdateStatus(data) {
    return request({
        url: "/interceptWord/updateStatus",
        method: "post",
        data,
    });
}

// 获取分类
export function getInterceptWordTypeList() {
    return request({
        url: "/interceptWord/getInterceptWordTypeList",
        method: "get",
    });
}

// 已关联应用个数
export function applicationRelList(data) {
    return request({
        url: "/interceptWordHouseAppRel/getInterceptWordHouseApplicationRelList",
        method: "post",
        data,
    });
}


// 处理方式下拉框的接口
export function getInterceptWordHandlingMethodList() {
    return request({
        url: "/interceptWord/getInterceptWordHandlingMethodList",
        method: "get",
    });
}

// 下载模版
export function downloadInterceptWordDataTemp(params) {
    return request({
        url: "/interceptWord/downloadInterceptWordDataTemp",
        method: "get",
        data: params,
        responseType: 'blob',
    });
}

// 导入模版
export function importInterceptWordData(data) {
    return request({
        url: "/interceptWord/importInterceptWordData",
        method: "post",
        data,
    });
}

// 所有敏感词库的列表
export function getInterceptWordHouseListAll(data) {
    return request({
        url: "/interceptWordHouse/getInterceptWordHouseListAll",
        method: "post",
        data,
    });
}

// 添加敏感词
export function addInterceptWordHouseApplicationRel(data) {
    return request({
        url: "/interceptWordHouseAppRel/addInterceptWordHouseApplicationRel",
        method: "post",
        data,
    });
}

// 移除敏感词
export function deleteInterceptWordHouseApplicationRel(data) {
    return request({
        url: "/interceptWordHouseAppRel/deleteInterceptWordHouseApplicationRel",
        method: "post",
        data,
    });
}

//   向量化模型管理-列表
export function getDenseVectorList(data) {
    return request({
        url: "/denseVector/getDenseVectorList",
        method: "post",
        data,
    });
}

// 添加接口
export function addDenseVector(data) {
    return request({
        url: "/denseVector/addDenseVector",
        method: "post",
        data,
    });
}

// 编辑接口
export function updateDenseVector(data) {
    return request({
        url: "/denseVector/updateDenseVector",
        method: "post",
        data,
    });
}

// 删除接口
export function deleteDenseVector(data) {
    return request({
        url: "/denseVector/deleteDenseVector",
        method: "post",
        data,
    });
}

//同义词列表数据
export function getSynonymWordList(data) {
    return request({
        url: "synonymWord/queryList",
        method: "post",
        data,
    });
}

// 新增同义词
export function addSynonymWord(data) {
    return request({
        url: "/synonymWord/addSynonymWordKey",
        method: "post",
        data,
    });
}

// 编辑同义词
export function updateSynonymWord(data) {
    return request({
        url: "/synonymWord/editSynonymWord",
        method: "post",
        data,
    });
}

//查询同义词下拉列表
export function getSynonymWordKeyList(data) {
    return request({
        url: "/synonymWord/querySelect",
        method: "post",
        data
    }); 
}

//下载导入同义词
export function downloadSynonymWordDataTemp(params) {
    return request({
        url: "/synonymWord/downloadSynonymWordDataTemp",
        method: "get",
        data: params,
        responseType: 'blob',
    }); 
}

//删除同义词
export function deleteSynonymWord(data) {
    return request({
        url: "/synonymWord/deleteSynonymWords",
        method: "post",
        data,
    });
}

// 导入同义词
export function importSynonymWordData(data) {
    return request({
        url: "/synonymWord/importSynonymWordData",
        method: "post",
        data,
    });
}

//查询纠错词列表
export function getCorrectWordList(data) {
    return request({
        url: "/correctWord/queryList",
        method: "post",
        data,
    }); 
}

//查询纠错词下拉列表
export function getCorrectWordKeyList(data) {
    return request({
        url: "/correctWord/querySelect",
        method: "post",
        data,
    });
}

//添加纠错词
export function addCorrectWord(data) {
    return request({
        url: "/correctWord/addCorrectWord",
        method: "post",
        data,
    });
}

//编辑纠错词
export function editCorrectWord(data) {
    return request({
        url: "/correctWord/editCorrectWord",
        method: "post",
        data,
    });
}

//删除纠错词
export function deleteCorrectWord(data) {
    return request({
        url: "/correctWord/deleteCorrectWord",
        method: "post",
        data,
    }); 
}

// 下载 导入纠错词模版
export function downloadCorrectWordDataTemp(params) {
    return request({
        url: "/correctWord/downloadCorrectWordDataTemp",
        method: "get",
        data: params,
        responseType: 'blob',
    });
}

// 导入纠错词
export function importCorrectWordData(data) {
    return request({
        url: "/correctWord/importCorrectWordData",
        method: "post",
        data,
    });
}
// 大模型管理 - 查询大模型厂商列表
export function apiGetLlmManufacturerr(data) {
    return request({
        url: "/llmManufacturerModel/getLlmManufacturer",
        method: "get",
        data: data,
    });
}
// 大模型管理 - 查询大模型厂商列表
export function apiGetLlmManufacturerModelList(data) {
    return request({
        url: "/llmManufacturerModel/getLlmManufacturerModelList?param="+data,
        method: "get"
    });
}
// 大模型管理 - 查询大模型厂商列表
export function apiGetLlmManufacturerModel(data) {
    return request({
        url: "/llmManufacturerModel/getLlmManufacturerModel",
        method: "get",
        params: data,
    });
}
// 大模型管理 - 查询大模型厂商列表
export function apiGetLabelList(data) {
    return request({
        url: "/label/getLabelList",
        method: "get",
        params: data,
    });
}
