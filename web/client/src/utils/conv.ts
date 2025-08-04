import chineseConv from "chinese-conv";
// 繁体转简体
export function tccTransferSimple(text: string) {
  return text ? chineseConv.sify(text) : text;
}
// 简体转繁体
export function simpleTransferTcc(text: string) {
  return text ? chineseConv.tify(text) : text;
}