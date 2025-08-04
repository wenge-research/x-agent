import CryptoJS from 'crypto-js'

// 加密数据
// @param message
// @param key
// @returns {string}

export const encryptByAES = function(message, key) {
  key = 'u9dYuYlGVk39kkb5'
  var iv = 'LT0cYheDsyKGHENw'
  var keyHex = CryptoJS.enc.Utf8.parse(key)
  var ivHex = CryptoJS.enc.Utf8.parse(iv)
  var encrypted = CryptoJS.AES.encrypt(message, keyHex, {
    iv: ivHex,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  })
  return encrypted.toString()
}
