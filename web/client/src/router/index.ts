import { createRouter, createWebHashHistory } from 'vue-router';
import NProgress from 'nprogress';
import { notFoundAndNoPower, dynamicRoutes } from '/@/router/route';
import { getApplication,getWorkflowDetail } from '/@/api/chat';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useUserInfo } from '/@/stores/userInfo';
import {toPageDetail, getUrlParamsLegacy, filterPermissions} from '/@/utils/routeInfo';
import { getCurTtsConfig } from '/@/utils/ttsConfig.ts';
import { getCookie } from '/@/utils/utils.ts';
import { authorizationApi, userInfo } from '/@/api/personal';
const { isMobile } = useBasicLayout();

export const router = createRouter({
	history: createWebHashHistory(),
	routes: [...notFoundAndNoPower, ...dynamicRoutes],
});
const queryWorkflowDetail =(componentId:String, type: String) =>{
  getWorkflowDetail({
    componentId,
    type: type === 'workflow' ? 4 : 5,
  }).then((res:Object) => {
    if (res.code == "000000") {
		console.log('res',res)
      sessionStorage.setItem(`nodeData`, JSON.stringify(res?.data));
    }
  })
}
// 路由加载前
router.beforeEach(async (to, from, next) => {
	const stores = useUserInfo();
	const responseData = await getApplication({ applicationId: to.params?.appId });
	to.meta.pcAuthChannelCode = responseData.data?.pcAuthChannelCode; // 将数据存储到 meta
	const accountName = window.location.hash?.split('accountName=')?.length >=1 ? window.location.hash.split('accountName=')[1] : getCookie('accountName') ? getCookie('accountName') : ''
  sessionStorage.setItem('accountName', accountName)
	if (responseData?.code === '000000') {
		console.log(responseData.data);
		if(responseData.data?.publicSwitch == '1') {
			next('/forbidden')
		}
		localStorage.setItem(`${to.params?.appId}appId`, responseData.data?.applicationId);
		localStorage.setItem(`appId`, responseData.data?.applicationId);
    getCurTtsConfig(responseData.data?.applicationId)
    queryWorkflowDetail(responseData.data?.applicationId, responseData.data?.type)
		sessionStorage.setItem('curRouteId', responseData.data?.applicationId);
		sessionStorage.setItem('clientLink', responseData.data?.clientLink);
		sessionStorage.setItem('applicationQuickCommandList', JSON.stringify(responseData.data?.applicationQuickCommandList));
		sessionStorage.setItem('applicationCode', responseData.data?.applicationCode);
		sessionStorage.setItem('tenantId', responseData.data?.tenantId);
		sessionStorage.setItem('modelId', responseData.data?.modelId);
		sessionStorage.setItem('numberData', JSON.stringify({
			audioCount: responseData.data?.audioCount,
			fileCount: responseData.data?.fileCount,
			videoCount: responseData.data?.videoCount,
			imageCount: responseData.data?.imageCount,
		}));
		localStorage.setItem(`${to.params?.appId}`, JSON.stringify(responseData?.data));
		if(to.params?.appId) {
			document.title = responseData.data ? responseData.data.webTitle : '';
		}
        if (import.meta.env.VITE_PUBLIC_PATH == "/wg-agent-client") {
            var ua = navigator.userAgent.toLowerCase();
            // 判断是否在微信浏览器内
            if (ua.match(/MicroMessenger/i)=="micromessenger") {
                wx.miniProgram.getEnv((res) => {
                    if (res.miniprogram) {
                        document.title = responseData.data && responseData.data.webTitle == '关芯GPT' ? '关芯为您' : responseData.data && responseData.data.webTitle;
                    } else {
                        console.log('不在小程序内');
                    }
                })
            } else {
                console.log('不在微信浏览器内');
            }
        }
		document.querySelector('meta[name="SiteName"]').content = import.meta.env.VITE_SITE_NAME;
		document.querySelector('meta[name="SiteDomain"]').content = import.meta.env.VITE_SITE_DOMAIN;
		if (responseData.data.clientAuthChannelCode == 'h5_we_chat_office' && isMobile.value && !accountName) {
			let wxAccessToken = sessionStorage.getItem('wxAccessToken');
			if (process.env.NODE_ENV == 'development') {
				sessionStorage.setItem('wxAccessToken', '1_f3abf74f91284781ab60881c40a7987e');
				wxAccessToken = '1_f3abf74f91284781ab60881c40a7987e';
			}
			if (!wxAccessToken) {
				if (window.location.href.indexOf('code=') == -1) {
					const res = await authorizationApi({ url: location.href });
					if (res?.data) {
						window.location.href = res.data;
					}
				} else {
					let paramObject = getUrlParamsLegacy();
					const oldKey = localStorage.getItem("wxCode")
					if (oldKey && oldKey == paramObject.code) {
						const res = await authorizationApi({ url: location.href });
						if (res?.data) {
							window.location.href = res.data;
						}
						return;
					}
					localStorage.setItem("wxCode", paramObject.code)
					const res = await userInfo({
						code: paramObject.code,
						loginType: 'wx',
						applicationId: responseData.data?.applicationId,
						tenantId: responseData.data?.tenantId,
					});
					sessionStorage.setItem('wxAccessToken', res.data.accessToken);
					sessionStorage.setItem('userId', res.data.user.id);
					sessionStorage.setItem("userInfo", JSON.stringify(res.data.user));
					let userType = res.data.user.userType;
					console.log(res.data.permission, res.data.user.userType, 10000);
					sessionStorage.setItem('userType', res.data.user.userType);
					sessionStorage.setItem('permission', res.data.permission);
					if (responseData.data?.applicationCode == 'zgc') {
						if (!userType) {
							toPageDetail(to, from, next, responseData.data);
						} else if (userType == 'resident' || userType == 'staff-street' || userType == 'staff-community') {
							toPageDetail(to, from, next, responseData.data);
						} else {
							let permissions = res.data.permission;
							if (permissions && permissions.length) {
								permissions = filterPermissions(permissions);
							}
							if (!permissions || permissions.length == 0) {
								toPageDetail(to, from, next, responseData.data);
								return
							}

							permissions = permissions.filter((item) => {
								return item.menuCode == 'zgcGxh5';
							});
							if (!permissions || permissions.length == 0) {
								toPageDetail(to, from, next, responseData.data);
								return
							}

							if (userType == 'gov' || userType == 'gov-street' || userType == 'gov-community') {
								sessionStorage.setItem('permissionList', JSON.stringify(permissions));
								// if (to.name != 'homePage') {
									const path = to.name == "talent" ? '/talent/zgc' : '/homePage/zgc'
									router.push(path);
								  return
								// }
							}
							toPageDetail(to, from, next, responseData.data);
						}
					}else {
						toPageDetail(to, from, next, responseData.data);
					}
				}
			} else {
				toPageDetail(to, from, next, responseData.data);
			}
		} else {
			toPageDetail(to, from, next, responseData.data, accountName);
			// let wxAccessToken = sessionStorage.getItem('wxAccessToken');
			// if (responseData.data.pcAuthChannelCode == 'pc_gov' && !isMobile.value && !wxAccessToken && !accountName) {
			// 	sessionStorage.setItem('curUrl', location.href);
			// 	next('/pcLogin');
			// } else {
			// 	toPageDetail(to, from, next, responseData.data, accountName);
			// }
		}
	} else {
		next();
	}
});

// 路由加载后
router.afterEach(() => {
	NProgress.done();
});
// 路由报错
router.onError((error, to) => {
	if (error.message.includes('Failed to fetch dynamically imported module')) {
		window.history.pushState({}, '', import.meta.env.VITE_API_URL + '#' + to.path);
		window.location.reload();
	}
});

// 导出路由
export default router;
