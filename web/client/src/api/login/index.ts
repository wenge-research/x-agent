import request from '/@/utils/request';
let baseUrl = import.meta.env.VITE_BASE_API_URL;
/**
 * （不建议写成 request.post(xxx)，因为这样 post 时，无法 params 与 data 同时传参）
 *
 * 登录api接口集合
 * @method signIn 用户登录
 * @method signOut 用户退出登录
 */
export function useLoginApi() {
	return {
		signIn: (data: object) => {
			return request({
				url: '/user/signIn',
				method: 'post',
				data,
			});
		},
		signOut: (data: object) => {
			return request({
				url: '/user/signOut',
				method: 'post',
				data,
			});
		},
		// 验证手机号是否已注册
		verifyPhone: (params: object) => {
			return request({
				url: baseUrl + '/auth/isExist',
				method: 'get',
				params,
			});
		},
		// 获取验证码
		getPhoneCode: (params: object) => {
			return request({
				url: baseUrl + '/sms/send',
				method: 'post',
				params,
			});
		},
		// 登录
		login: (data: object) => {
			// console.log('data',data)
			return request({
				// url: baseUrl + '/login',
				url: `${baseUrl}/login?mykey=${data.mykey}&username=${data.username}`,
				method: 'post',
			});
		},
		// 绑定手机号
		bindPhoneNumber: (data: object) => {
			return request({
				url: baseUrl + '/user/bindPhoneNumber',
				method: 'post',
				data,
			});
		},
		// 获取验证图片
		getVerifyImg: () => {
			return request({
				url: baseUrl + '/graphicVerify/getGraphicVerify',
				method: 'get',
			});
		},
		// 验证图片
		verifyImg: (params: object) => {
			return request({
				url: baseUrl + '/graphicVerify/verify',
				method: 'get',
				params,
			});
		},
		// 免登陆
		offline: () => {
			return request({
				url:baseUrl + '/offline',
				method: 'get'
			})
		},
		//企业微信扫码登陆参数
		loginByQROne: () => {
			return request({
				url:baseUrl + '/auth/corpWxLoginParam',
				method: 'get'
			})
		},
		//登录是否失效
		isLoginExpiredApi: () => {
			return request({
				url:baseUrl + '/auth/isLoginExpired',
				method: 'get'
			})
		},
		//退出
		logout: () => {
			return request({
				url:baseUrl + '/auth/logout',
				method: 'get'
			})
		},
	};
}
