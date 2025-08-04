import { defineStore } from 'pinia';
import Cookies from 'js-cookie';
import { Modal } from 'winbox-ui-next';
import { Session, Local } from '/@/utils/storage';
import { useLoginApi } from '/@/api/login';
const { isLoginExpiredApi, logout } = useLoginApi();
/**
 * 用户信息
 * @methods setUserInfos 设置用户信息
 */
export const useUserInfo = defineStore('userInfo', {
	state: (): UserInfosState => ({
		userInfos: {
			userName: '',
			firstLogin: false,
			photo: '',
			time: 0,
			roles: [],
			authBtnList: [],
			isLoginExpired: false,
			userNumber: '',
			timer: null,
		},
		offline: '',
	}),
	actions: {
		async setUserInfos() {
			// 存储用户信息到浏览器缓存
			if (Session.get('userInfo')) {
				this.userInfos = Session.get('userInfo');
			} else {
				const userInfos: any = await this.getApiUserInfo();
				this.userInfos = userInfos;
			}
		},
		// 模拟接口数据
		async getApiUserInfo() {
			return new Promise((resolve) => {
				setTimeout(() => {
					// 模拟数据，请求接口时，记得删除多余代码及对应依赖的引入
					const userName = Cookies.get('userName');
					const userNumber = Cookies.get('userNumber');
					const firstLogin = Cookies.get('firstLogin');
					// 模拟数据
					let defaultRoles: Array<string> = [];
					let defaultAuthBtnList: Array<string> = [];
					// admin 页面权限标识，对应路由 meta.roles，用于控制路由的显示/隐藏
					let adminRoles: Array<string> = ['admin'];
					// admin 按钮权限标识
					// let adminAuthBtnList: Array<string> = ['btn.add', 'btn.del', 'btn.edit', 'btn.link'];
					// test 页面权限标识，对应路由 meta.roles，用于控制路由的显示/隐藏
					let testRoles: Array<string> = ['common'];
					// test 按钮权限标识
					// let testAuthBtnList: Array<string> = ['btn.add', 'btn.link'];
					// 不同用户模拟不同的用户权限
					if (Session.get('role') == 1) {
						defaultRoles = adminRoles;
						// defaultAuthBtnList = adminAuthBtnList;
					} else {
						defaultRoles = testRoles;
						// defaultAuthBtnList = testAuthBtnList;
					}
					// 用户信息模拟数据
					const userInfos = {
						userName: userName,
						firstLogin: firstLogin,
						userNumber: userNumber,
						photo: userName === 'admin' ? '' : '',
						time: new Date().getTime(),
						roles: defaultRoles,
						authBtnList: defaultAuthBtnList,
					};
					resolve(userInfos);
				}, 0);
			});
		},
		async isLoginExpiredFun() {
			let res = await isLoginExpiredApi();
			if (res.code == 200 && res.data) {
				this.ModalTip();
			}
		},
		loginExpiredSeach() {
			this.userInfos.timer = setInterval(() => {
				this.isLoginExpiredFun();
			}, 1000 * 60 * 60 * 24 * 7);
		},
		clearLoginExpiredSeach() {
			clearInterval(this.userInfos.timer);
		},
		ModalTip() {
			this.clearLoginExpiredSeach();
			Session.clear();
			this.logout();
			Modal.warning({
				title: '该设备登录已失效',
				content: '请重新登录',
				closable: false,
				maskClosable: false,
				okText: '重新登录',
				modalClass: 'warningLoginModal',
				width: '480px',
				onOk: () => {
					sessionStorage.removeItem('wxAccessToken');
					window.location.reload();
				},
			});
		},
		async logout() {
			await logout();
		},
	},
});
