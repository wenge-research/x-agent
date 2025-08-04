import axios from "axios";
import Vue from "vue";
import Router from "vue-router";
// import { getCookies } from "@/utils/auth";
// import { getIP } from "@/utils";
import { MessageBox, Message } from "element-ui";
import router from "@/router";
import app from '../main';
import md5 from "js-md5";
// getIP();
//创建一个axios
const service = axios.create({
    baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
    // withCredentials: true, // 请求头中是否携带cookie
    timeout: 180000 //请求超时
});
// 安全数字验证函数
const validateNumber = (value) => {
    if (value === null || value === undefined) return value;
    
    // 检查是否为数字或可转换为数字的字符串
    if (typeof value === 'string' && /^-?\d+$/.test(value)) {
      value = parseInt(value, 10);
    }
    
    if (typeof value !== 'number' || isNaN(value)) {
      return value; // 非数字值原样返回
    }
    
    // 检查是否在安全范围内
    if (!Number.isSafeInteger(value)) {
      throw new Error(`数值超出安全范围: ${value}`);
    }
    
    return value;
  };
  
  // 深度遍历对象验证数字
  const sanitizeNumbers = (obj) => {
    if (obj instanceof File || obj === null || typeof obj !== 'object') {
      return obj;
    }
  
    for (const key in obj) {
      if (Array.isArray(obj[key])) {
        obj[key] = obj[key].map(item => sanitizeNumbers(item));
      } else if (typeof obj[key] === 'object') {
        obj[key] = sanitizeNumbers(obj[key]);
      } else {
        //console.log(obj)
       // obj[key] = validateNumber(obj[key]);
      }
    }
    
    return obj;
  };
//请求拦截器
service.interceptors.request.use(
    config => {
        let timer = new Date().getTime();
        //请求发送前
        let manageAccessToken = sessionStorage.getItem('manageAccessToken')
        config.headers.Authorization = `Bearer ${manageAccessToken}`
        config.headers.language = app.$i18n.locale === 'en' ? 'en_US' : app.$i18n.locale === 'zh' ? 'zh_CN' : 'yue_CN';
        
      let requestData = {};
      let requestParam = '';
      // 处理 GET 参数
      if (config.method === 'get' || config.url === '/login') {
        // requestData = sanitizeNumbers({ ...config.params });
        // console.log("requestData", requestData)
        // config.params = requestData; // 更新净化后的参数
        // 处理 params 为指定格式
        if (config.params && Object.keys(config.params).length > 0) {
            // 将 params 对象转换为 key=value 的字符串格式
            const paramsString = Object.entries(config.params)
              .map(([key, value]) => `${key}=${value}`)
              .join('&');

           // console.log('转换后的 params 字符串:', paramsString);
            // 示例输出: "tenantId=1tenantId1=2"

            requestData = paramsString
            requestParam = paramsString
        } else {
            requestData = ""
            requestParam = ""
        }
      }
      
      // 处理 POST 数据
      if (['post', 'put', 'patch'].includes(config.method?.toLowerCase())) {
        if (config.data instanceof FormData) {
          // FormData 特殊处理
          const formDataObj = {};
          config.data.forEach((value, key) => {
            formDataObj[key] = value;
          });
          requestData = sanitizeNumbers(formDataObj);
        } else if (config.data instanceof Blob || config.data instanceof ArrayBuffer) {
          // 二进制数据不处理
          requestData = { binary_data: '[binary]' };
        } else {
          if(!Array.isArray(config.data)) {
            requestData = sanitizeNumbers(config.data ? { ...config.data } : {});
            config.data = requestData; // 更新净化后的数据
          } else {
            requestData = config.data;
          }
        }
      }
      //console.log("config", config)
      config.headers.timestamp = timer;
      const paramsData = config.method === 'get' ? requestData : JSON.stringify(requestData)
      const paramsUrl = config.method === 'get' && config.url.includes('?') ? config.url.replace(/\?/, '') : config.url
      // 安全计算 cipher
      config.headers.cipher = md5(timer + `${paramsUrl}${paramsData}${requestParam}axxxxxxxxxxx`);
      //console.log("clipher参数", timer + `${paramsUrl}${paramsData}${requestParam}axxxxxxxxxxx`)
        return config;
    },
    error => {
        //请求失败
        return Promise.reject(error);
    }
);
let __tag = 0;
let __powerTag = 0;
//响应拦截器
service.interceptors.response.use(
    response => {
      //console.log(app.$t('confirm'))
        const res = response.data;
        //依据需要调整code
        if (res.code !== 200) {
            //000024: 未登录;
            if (res.code === "000024") {
                if (__tag === 0) {
                    __tag = 1;
                    MessageBox.confirm(
                        "您已处于离线状态，是否返回重新登录？",
                        app.$t('tips'),
                        {
                            confirmButtonText: app.$t('confirm'),
                            customClass: "confirmClass",
                            showClose: false,
                            showCancelButton: false,
                            closeOnClickModal: false
                        }
                    ).then(() => {
                        router.replace({
                            name: 'login',
                        })
                    });
                }
                // 20013: 其他电脑登录强迫下线;
            } else if (res.code === 20013) {
                if (__tag === 0) {
                    __tag = 1;
                    MessageBox.confirm(
                        "当前账号已在其他电 脑登录，您已被强迫下线。是否返回重新登录？",
                        app.$t('tips'),
                        {
                            confirmButtonText: app.$t('confirm'),
                            showClose: false,
                            showCancelButton: false,
                            closeOnClickModal: false,
                            customClass: "confirmClass"
                        }
                    ).then(() => {
                        router.replace({
                            name: 'login',
                        })
                    });
                }
            } else {
                __tag = 0;
            }
            return res;
            // return Promise.reject(new Error(res.msg))
        } else {
            return res;
        }
    },
    error => {
        if (error.response && (error.response.status === 403 || error.response.status === 401)) {
          if (__tag === 0) {
            __tag = 1;
            MessageBox.confirm(
                "您已处于离线状态，是否返回重新登录？",
                app.$t('tips'),
                {
                    confirmButtonText: app.$t('confirm'),
                    customClass: "confirmClass",
                    showClose: false,
                    showCancelButton: false,
                    closeOnClickModal: false
                }
            ).then(() => {
                router.replace({
                    name: 'login',
                })
            });
          }
        } else {
            const style = document.createElement('style');
style.textContent = `
  .custom-message .el-message__closeBtn {
    right:10px;
    top:15px;
  }
`;
document.head.appendChild(style);
            Message.warning({
                message:'系统异常，请稍后重试...',
                showClose:true,
                customClass:'custom-message'
            })

        }
        return Promise.reject(error);
    }
);

export default service;
