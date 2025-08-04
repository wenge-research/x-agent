import axios from "axios";
import Vue from "vue";
import Router from "vue-router";
// import { getCookies } from "@/utils/auth";
// import { getIP } from "@/utils";
import { MessageBox, Message } from "element-ui";
import router from "@/router";
import app from '../main';
// getIP();
//创建一个axios
const service = axios.create({
    baseURL: process.env.VUE_APP_WORKFLOW_API, 
    // withCredentials: true, // 请求头中是否携带cookie
    timeout: 180000 //请求超时
});

//请求拦截器
service.interceptors.request.use(
    config => {
        //请求发送前
        let manageAccessToken = sessionStorage.getItem('manageAccessToken')
        config.headers.Authorization = `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNzEwZjZjZjktM2EwYi00MGQ5LWExMGEtNWZjZDQ3NzNmYzZiIiwiZXhwIjoxNzI4NTQzMjIzLCJpc3MiOiJTRUxGX0hPU1RFRCIsInN1YiI6IkNvbnNvbGUgQVBJIFBhc3Nwb3J0In0.8mAT5XrO0WlxDS2faeP5hLlqPBHNnqAMAmtDkiRatGE`
        config.headers.language = app.$i18n.locale === 'en' ? 'en_US' : app.$i18n.locale === 'zh' ? 'zh_CN' : 'yue_CN'; 
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
      console.log(app.$t('confirm'))
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
        // Message.warning('系统异常，请稍后重试...')
        return Promise.reject(error);
    }
);

export default service;
