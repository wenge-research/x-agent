import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';
import { Modal, Message } from 'winbox-ui-next';
import { Session } from '/@/utils/storage';
import { requestEncryption } from '/@/utils/utils';
import qs from 'qs';
import { useUserInfo } from '/@/stores/userInfo';
import router from '/@/router';
import md5 from 'js-md5';

// 配置新建一个 axios 实例
const service: AxiosInstance = axios.create({
	baseURL: import.meta.env.VITE_API_URL,
	timeout: 1000 * 60 * 30,
	headers: {
		'Content-Type': 'application/json',
	},
	paramsSerializer: {
		serialize(params) {
			return qs.stringify(params, { allowDots: true });
		},
	},
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
	console.log('Number.isSafeInteger(value)', !Number.isSafeInteger(value));
	// 检查是否在安全范围内
	if (!Number.isSafeInteger(value)) {
		console.warn(`数值超出安全范围: ${value}`); // 改为警告
		return value; // 仍然返回原值
	}

	return value;
};

// 深度遍历对象验证数字
const sanitizeNumbers = (obj) => {
	if (obj === null || typeof obj !== 'object') {
		return obj;
	}
	// 如果是 File 或 Blob 对象，直接返回（不处理）
	if (obj instanceof File || obj instanceof Blob) {
		return obj;
	}
	for (const key in obj) {
		if (Array.isArray(obj[key])) {
			obj[key] = obj[key].map((item) => sanitizeNumbers(item));
		} else if (typeof obj[key] === 'object') {
			obj[key] = sanitizeNumbers(obj[key]);
		} else {
			obj[key] = validateNumber(obj[key]);
		}
	}

	return obj;
};
// 添加请求拦截器
service.interceptors.request.use(
	(config: AxiosRequestConfig) => {
		// 在发送请求之前做些什么 token
		if (sessionStorage.getItem('accountName')) {
			config.headers!['Account-name'] = sessionStorage.getItem('accountName');
		}
		if (sessionStorage.getItem('wxAccessToken')) {
			config.headers!['Authorization'] = `Bearer ${sessionStorage.getItem('wxAccessToken')}`;
		}
		if (sessionStorage.getItem('manageAccessToken')) {
			config.headers!['Authorization'] = `Bearer ${sessionStorage.getItem('manageAccessToken')}`;
		}
		if (localStorage.getItem('userInfo')) {
			const storedUserInfoString = localStorage.getItem('userInfo');
			const storedUserInfo = JSON.parse(storedUserInfoString);
			config.headers!['Authorization'] = `Bearer ${storedUserInfo.accessToken}`;
		}
		if (sessionStorage.getItem('loginPlat')) {
			config.headers!['login-plat'] = `${sessionStorage.getItem('loginPlat')}`;
		}
		let timer = new Date().getTime();
		config.headers.timestamp = timer;
		let requestData = {};
		// 处理 GET 参数
		if (config.method === 'get') {
			// requestData = sanitizeNumbers({ ...config.params });
			// config.params = requestData; // 更新净化后的参数
			// 处理 params 为指定格式
			if (config.params && Object.keys(config.params).length > 0) {
				// 将 params 对象转换为 key=value 的字符串格式
				const paramsString = Object.entries(config.params)
					.map(([key, value]) => `${key}=${value}`)
					.join('&');

				// 示例输出: "tenantId=1tenantId1=2"

				requestData = paramsString;
			} else {
				requestData = '';
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
				//不做处理接口
				if (config.url.indexOf('addDialogueCache') > 0) {
					requestData = config.data;
				} else {
					requestData = sanitizeNumbers(config.data ? { ...config.data } : {});
				}
				// 普通 JSON 数据
				config.data = requestData; // 更新净化后的数据
			}
		}
		let baseUrl = import.meta.env.VITE_BASE_API_URL;
		config.headers.timestamp = timer;
		let captureUrl = '';
		if (config.url && config.url.includes(baseUrl)) {
			const regex = new RegExp(`^${baseUrl}/`);
			captureUrl = config.url.replace(regex, '');
		}
		const paramsData = config.method === 'get' ? requestData : JSON.stringify(requestData);
		const paramsUrl = config.method === 'get' && captureUrl.includes('?') ? captureUrl.replace(/\?/, '') : captureUrl;
		// 安全计算 cipher
		config.headers.cipher = md5(`${timer}/${paramsUrl}${paramsData}xxxxxxx=`);
		// console.log("clipher参数", `${timer}/${paramsUrl}${paramsData}xxxxxxx=`)
		//   config.headers.cipher = md5(
		//     timer +
		//     JSON.stringify(requestData) +
		//     '/dialogue/v3/dialogueByStreamxxxxxxx='
		//   );
		// requestEncryption(config, timer);
		// config.headers!['Authorization'] = `1b79085f-df82-479a-8087-68048bbac8db`;
		return config;
	},
	(error) => {
		// 对请求错误做些什么
		return Promise.reject(error);
	}
);

// 添加响应拦截器
service.interceptors.response.use(
	(response) => {
		// 对响应数据做点什么
		const res = response.data;
		if (res.code && res.code !== 200 && res.code !== 10099 && res.code !== '000000') {
			// `token` 过期或者账号已在别处登录
			if (res.code === 20001 || res.status === 20001 || res.code == '000024' || res.status === '000024') {
				const modalFlag = sessionStorage.getItem('ModalFlag');
				if (!modalFlag) {
					sessionStorage.setItem('ModalFlag', '1');
					// 跳到龙眼GPT登录
					Modal.open({
						title: '登录超时',
						content: '是否重新登录？',
						closable: false,
						okText: '确定',
						cancelText: '取消',
						onOk: () => {
							sessionStorage.removeItem('wxAccessToken');
							window.location.href = sessionStorage.getItem('clientLink');
						},
						onCancel: () => {
							sessionStorage.removeItem('ModalFlag');
						},
					});
				}
			}
			return response.data;
		} else {
			return response.data;
		}
	},
	(error) => {
		const stores = useUserInfo();
		const res = error.response?.data;
		if (res.code === 401 || res.msg === 4001) {
			stores.userInfos.isLoginExpired = true;
			// Session.clear(); // 清除浏览器全部临时缓存
			// window.location.href = '/'; // 去登录页

			// Message.error(res.msg);
			return;
		}
		// 对响应错误做点什么
		// if (error.message.indexOf('timeout') != -1) {
		// 	Message.error('网络超时');
		// } else if (error.message == 'Network Error') {
		// 	Message.error('网络连接错误');
		// } else {
		// 	if (error.response.data) Message.error(error.response.statusText);
		// 	else Message.error('网络连接错误');
		// }
		return Promise.reject(error);
	}
);

// 导出 axios 实例
export default service;
