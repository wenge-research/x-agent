import request from "@/utils/request";

// mcp - 列表
export function currentAccount(data) {
    return request({
        url: "/user/currentAccount",
        method: "post",
        data,
    });
}
export function changPw(data) {
    return request({
        url: "/user/changPw",
        method: "post",
        data,
    });
}
export function uploadPic(data) {
    return request({
        url: "/matterGuideFiled/uploadPic",
        method: "post",
        data,
    });
}
export function updateCurrentAccount(data) {
    return request({
        url: "/user/updateCurrentAccount",
        method: "post",
        data,
    });
}
export function sendSmsAuthentication(data) {
    return request({
        url: "/auth/sendSmsAuthentication",
        method: "post",
        data,
    });
}
export function updatePhone(data) {
    return request({
        url: "/user/updatePhone",
        method: "post",
        data,
    });
}
