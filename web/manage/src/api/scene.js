import request from "@/utils/request";
// 查询列表
export function apiGetSceneManagementList(data) {
  return request({
    url: "/sceneManagement/getSceneManagementList",
    method: "post",
    data,
  });
}
// 创建场景(新增/编辑用同一个接口 编辑传id参数)
export function apiAddSceneManagement(data) {
  return request({
    url: "/sceneManagement/addSceneManagement",
    method: "post",
    data,
  });
}
// 更新场景(暂时不用)
export function apiUpdateSceneManagement(data) {
  return request({
    url: "/sceneManagement/updateSceneManagement",
    method: "post",
    data,
  });
}
// 场景详情
export function apiGetSceneDetail(data) {
  return request({
    url: "/sceneManagement/getSceneDetail",
    method: "post",
    data,
  });
}
//   场景 - （执行事项-事项配置查询）
export function apiGetSceneMatterGroupRefList(data) {
  return request({
    url: "/sceneMatterGroupRef/getSceneMatterGroupRefList",
    method: "post",
    data,
  });
}
//   新增 - （执行事项-事项配置新增）
export function apiAddSceneMatterGroupRef(data) {
  return request({
    url: "/sceneMatterGroupRef/addSceneMatterGroupRef",
    method: "post",
    data,
  });
}
//   删除场景
export function apiDeleteSceneManagement(data) {
  return request({
    url: "/sceneManagement/deleteSceneManagement",
    method: "post",
    data,
  });
}
//   应用配置 -  绑定场景-列表
export function apiGetSceneApplicationRefList(data) {
  return request({
    url: "/sceneApplicationRef/getSceneApplicationRefList",
    method: "post",
    data,
  });
}
//   应用配置 -  绑定场景-添加
export function apiAddSceneApplicationRef(data) {
  return request({
    url: "/sceneApplicationRef/addSceneApplicationRef",
    method: "post",
    data,
  });
}
//   应用配置 -  绑定场景-删除
export function apiDeleteSceneApplicationRef(data) {
  return request({
    url: "/sceneApplicationRef/deleteSceneApplicationRef",
    method: "post",
    data,
  });
}