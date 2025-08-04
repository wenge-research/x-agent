export const padStart = val => {
    if (!val) {
        return "00";
    }
    return val.toString().padStart(2, "0");
};
/**
 * 格式化日期时间
 */
export const formatDate = val => {
    if (!val) {
        return "";
    }
    let date = new Date(val);
    let y = date.getFullYear();
    let m = date.getMonth() + 1;
    let d = date.getDate();
    let h = date.getHours();
    let mm = date.getMinutes();
    let s = date.getSeconds();
    let str = {
        date: [y, m, d].map(item => padStart(item)).join("-"),
        timer: [h, mm, s].map(item => padStart(item)).join(":")
    };
    return str;
};

// 计算星期几
// 计算指定时间是星期几
export const getweekday = date => {
    // date例如:'2022-03-05'
    var weekArray = new Array(
        "星期日",
        "星期一",
        "星期二",
        "星期三",
        "星期四",
        "星期五",
        "星期六"
    );
    var week = weekArray[new Date(date).getDay()];
    return week;
};
/**
 * 对象数组的深度拷贝.
 * source是原数据，extendObj是新增的键值对
 */
export const objArrDeepCopy = (source,extendObj) => {
  var sourceCopy = source instanceof Array ? [] : {};
  for (var item in source) {
      sourceCopy[item] = typeof source[item] === 'object' ? objArrDeepCopy(source[item],extendObj) : source[item];
      if(typeof extendObj === 'object'&& !(sourceCopy instanceof Array)){
          for(var param in extendObj){
              sourceCopy[param] = extendObj[param];
          }
      }
  }
  return sourceCopy;
}

/**
 * 解密
 * 加解密公钥：zhichuan-ai-public
 */
export const decryptData = (encryptedData) => {
    const decrypted = CryptoJS.AES.decrypt(encryptedData, 'xxxx-ai-xxxx').toString(CryptoJS.enc.Utf8);
    return decrypted;
}

/**
 * 文件大小换算
 * 1024b =1kb 1024kb=1mb
 */
export const  formatFileSize=(bytes)=>{
    if (bytes < 1024) {
      return `${bytes} B`; 
    } else if (bytes < 1024 * 1024) {
      const kB = bytes / 1024;
      return `${kB.toFixed(1)} KB`; 
    } else {
      const MB = bytes / (1024 * 1024);
      return `${MB.toFixed(1)} MB`; 
    }
  }
// 字符串json转换成object
export const processParam = (param) => {
  // 判断是否为对象（包括数组、日期等，但不包括 null）
  if (typeof param === 'object' && param !== null) {
    return param;
  }

  // 处理字符串情况
  if (typeof param === 'string') {
    // 检查字符串是否为空或仅包含空白字符
    if (param.trim() === '') {
      return param; // 返回原空字符串
    }

    try {
      return JSON.parse(param);
    } catch (e) {
      // 解析失败，返回原字符串
      return param;
    }
  }

  // 其他类型直接返回
  return param;
}

//深拷贝
export function deepCopy(obj) {
  if (obj === null || typeof obj !== 'object') {
    return obj
  }
  
  const copy = Array.isArray(obj) ? [] : {}
  
  Object.keys(obj).forEach(key => {
    copy[key] = deepCopy(obj[key])
  })
  
  return copy
}