import request from "@/utils/request";

// 问题排行榜top50
export function apiQuestionChartsTop50(data) {
    return request({
        url: "/applicationAnalysis/questionChartsTop50",
        method: "post",
        data,
    });
}
// 概览指标
export function apiApplicationOverviewIndicators(data) {
    return request({
        url: "/applicationAnalysis/applicationOverviewIndicators",
        method: "post",
        data,
    });
}
// 图表
export function apiGetApplicationOverviewIndicatorsByType(data) {
    return request({
        url: "/applicationAnalysis/getApplicationOverviewIndicatorsByType",
        method: "post",
        data,
    });
}
// 活跃用户数
export function getApplicationActiveUser(data){
    return request({
        url:"/applicationAnalysis/getApplicationActiveUser",
        method:"post",
        data
    })
}

// 点赞点踩排名
export function apiGetApplicationLikeStepRanking(data) {
    return request({
        url: "/applicationAnalysis/getApplicationLikeStepRanking",
        method: "post",
        data,
    });
}
// 点赞点踩排名 - 下载
export function apiGetApplicationLikeStepRankingExport(data) {
    return request({
        url: "/applicationAnalysis/getApplicationLikeStepRankingExport",
        method: "post",
        data,
        responseType: 'blob',
    });
}
// 知识库问答添加数排名
export function apiGetApplicationAddQARanking(data) {
    return request({
        url: "/applicationAnalysis/getApplicationAddQARanking",
        method: "post",
        data,
    });
}
// 知识库问答添加数排名 - 下载
export function apiGetApplicationAddQARankingExport(data) {
    return request({
        url: "/applicationAnalysis/getApplicationAddQARankingExport",
        method: "post",
        data,
    });
}
// 问答审核数排名
export function apiGetApplicationDialogueReviewRanking(data) {
    return request({
        url: "/applicationAnalysis/getApplicationDialogueReviewRanking",
        method: "post",
        data,
    });
}
// 问答审核数排名 - 下载
export function apiGetApplicationDialogueReviewRankingExport(data) {
    return request({
        url: "/applicationAnalysis/getApplicationDialogueReviewRankingExport",
        method: "post",
        data,
    });
}


// 运营 质量问答统计图
export function apiGetApplicationOverviewQualityByType(data) {
    return request({
        url: "/applicationAnalysis/getApplicationOverviewQualityByType",
        method: "post",
        data,
    });
}

// 运营问答质量 统计图下载
export function getApplicationOverviewQualityByTypeExport(params) {
    return request({
        url: "applicationAnalysis/getApplicationOverviewQualityByTypeExport",
        method: "post",
        params,
        responseType: 'blob',
    });
}
// 运营问答质量 统计图下载
export function applicationStatistics(data) {
    return request({
        url: "applicationAnalysis/applicationStatistics?applicationId="+data.applicationId,
        method: "post",
        data,
		// headers: {
		//     'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
		// }
    });
}