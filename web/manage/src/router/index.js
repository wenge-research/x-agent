import Vue from 'vue'
import VueRouter from 'vue-router'
// import Cookie from 'js-cookie'
// import { ElMessage} from 'element-plus';
import { Message } from "element-ui";
import { authorizationApi, Login } from '@/api/index';
import { decryptData } from "@/utils/tool";
import md5 from "js-md5";
import { SUCCESS_CODE } from "@/api/config";
import app from '../main';


Vue.use(VueRouter)

// 完整路由代码
const router = new VueRouter({
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login/Login')
    },
    {
      path: '/home',
      component: () => import(/* webpackChunkName: "Main" */ '@/views/Main'),
      children: [
        {
          path: '/storemanage',
          name: 'storemanage',
          component: () => import(/* webpackChunkName: "storemanage" */ '@/views/storemanage/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/manual',
          name: 'manual',
          component: () => import(/* webpackChunkName: "manual" */ '@/views/manual/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/appmanage/:openDialog?',
          name: 'appmanage',
          component: () => import(/* webpackChunkName: "appManage" */ '@/views/appManage/index'),
          props: true,
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/operationsManagement',
          name: 'operationsManagement',
          component: () => import(/* webpackChunkName: "operationsManagement" */ '@/views/operationsManagement/operationsManagement'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/intelligentSearch',
          name: 'intelligentSearch',
          component: () => import(/* webpackChunkName: "intelligentSearch" */ '@/views/intelligentSearch/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/userManage',
          name: 'userManage',
          component: () => import(/* webpackChunkName: "UserManage" */ '@/views/UserManage/UserManage'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/suggestionFeedback',
          name: 'suggestionFeedback',
          component: () => import(/* webpackChunkName: "suggestionFeedback" */ '@/views/systemManage/suggestionFeedback/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/tenantManage',
          name: 'tenantManage',
          component: () => import(/* webpackChunkName: "tenantManage" */ '@/views/tenantManage/tenantManage'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/roleManage',
          name: 'roleManage',
          component: () => import(/* webpackChunkName: "RoleManage" */ '@/views/RoleManage/RoleManage'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/listingReview',
          name: 'listingReview',
          component: () => import(/* webpackChunkName: "listingReview" */ '@/views/systemManage/listingReview/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/kbm',
          name: 'kbm',
          component: () => import(/* webpackChunkName: "Kbm" */ '@/views/Kbm/Kbm'),
          meta: {
            requiresAuth: true
          },
        },
		{
		  path: '/mcp',
		  name: 'mcp',
		  component: () => import(/* webpackChunkName: "Kbm" */ '@/views/mcp/index'),
		  meta: {
		    requiresAuth: true
		  },
		},
		{
		  path: '/dataSj',
		  name: 'dataSj',
		  component: () => import(/* webpackChunkName: "Kbm" */ '@/views/dataSj/index'),
		  meta: {
		    requiresAuth: true
		  },
		},
        {
          path: '/keyManage',
          name: 'keyManage',
          component: () => import(/* webpackChunkName: "Kbm" */ '@/views/keyManage'),
          meta: {
            requiresAuth: true
          },
        },
        // {
        //   path: '/ModelManagement',
        //   name: 'ModelManagement',
        //   component: () => import('@/views/system/ModelManagement'),
        //   meta: {
        //     requiresAuth: true
        //   },
        // },
        {
          path: '/matterManage',
          name: 'matterManage',
          component: () => import(/* webpackChunkName: "MatterManage" */ '@/views/MatterManage/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/menuManage',
          name: 'menuManage',
          component: () => import(/* webpackChunkName: "menuManage" */ '@/views/systemManage/menuManage/menuManage.vue'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/workflow/workflowConfig',
          name: 'workflow',
          component: () => import(/* webpackChunkName: "workflowConfig" */ '@/views/workflowConfig/index.vue'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/workflow/workFlowEdit',
          name: 'workFlowEdit',
          component: () => import(/* webpackChunkName: "workflowConfig" */ '@/views/workflowConfig/workFlowEdit.vue'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/operateLog',
          name: 'operateLog',
          component: () => import(/* webpackChunkName: "operateLog" */ '@/views/operate-log/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/userCenter',
          name: 'userCenter',
          component: () => import(/* webpackChunkName: "userCenter" */ '@/views/userCenter/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/userAgreement',
          name: 'userAgreement',
          component: () => import(/* webpackChunkName: "userCenter" */ '@/views/userCenter/userAgreement'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/privacyAgreement',
          name: 'privacyAgreement',
          component: () => import(/* webpackChunkName: "userCenter" */ '@/views/userCenter/privacyAgreement'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/systemManage/dialogTemplate',
          name: 'dialogTemplate',
          component: () => import(/* webpackChunkName: "dialogTemplate" */ '@/views/dialogTemplate/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/tool',
          name: 'toolManager',
          component: () => import(/* webpackChunkName: "toolManager" */ '@/views/toolManager/index'),
          meta: {
            requiresAuth: true
          },
        },
        {
          path: '/effectEvaluation',
          name: 'effectEvaluation',
          component: () => import(/* webpackChunkName: "toolManager" */ '@/views/effectEvaluation/index'),
          meta: {
            requiresAuth: true
          },
        },
        // {
        //   path: '/toolManager/voice',
        //   name: 'voice',
        //   component: () => import('@/views/toolManager/voice/index'),
        //   meta: {
        //     requiresAuth: true
        //   },
        // },
        // {
        //   path: '/toolManager/prompt',
        //   name: 'prompt',
        //   component: () => import('@/views/toolManager/promptWordLibrary/index'),
        //   meta: {
        //     requiresAuth: true
        //   },
        // },
        {
          path: '/toolManager/scene',
          name: 'sceneManagement',
          component: () => import(/* webpackChunkName: "scene" */ '@/views/toolManager/scene'),
          meta: {
            requiresAuth: true
          },
        },
        // {
        //   path: '/toolManager/largeModel',
        //   name: 'largeModel',
        //   component: () => import('@/views/toolManager/largeModel/index'),
        //   meta: {
        //     requiresAuth: true
        //   },
        // },
        {
          path: '/embadingManage',
          name: 'modelmanagement',
          component: () => import(/* webpackChunkName: "modelManagement" */ '@/views/modelManagement/index'),
          meta: {
            requiresAuth: true
          },
        },
        // {
        //     path: '/embadingManage',
        //     name: 'vectorModel',
        //     component: () => import('@/views/toolManager/vectorModel/index'),
        //     meta: {
        //       requiresAuth: true
        //     },
        //   },
        {
          path: '/password',
          name: 'password',
          component: () => import(/* webpackChunkName: "password" */ '@/views/Login/password'),
          meta: {
            requiresAuth: true
          },
        },
        // {
        //   path: '/issueManagement',
        //   name: 'issueManagement',
        //   component: () => import('@/views/issueManagement/index'),
        //   meta: {
        //     requiresAuth: true
        //   },
        // },
        // 事项处置
        {
          path: '/sceneManage/disposalOfMatters',
          name: 'disposalOfMatters',
          component: () => import(/* webpackChunkName: "matterDisposalPage" */ '@/views/issueManagement/matterDisposalPage'),
          meta: {
            requiresAuth: true
          },
        },
        // 事项管理
        {
          path: '/sceneManage/eventManagement',
          name: 'eventManagement',
          component: () => import(/* webpackChunkName: "matterTypePage" */ '@/views/issueManagement/matterTypePage'),
          meta: {
            requiresAuth: true
          },
        },
      ]
    },
  ]
})

router.beforeEach(async (to, from, next) => {
  // 10/28新加微信认证功能
  console.log("to", to)
  console.log("from", from)
  if (to.fullPath.includes('from=wechat')) {
    let wxAccessToken = sessionStorage.getItem('wxAccessToken');
    // if (process.env.NODE_ENV == 'development') {
    //   sessionStorage.setItem('wxAccessToken', '2a766ed08690479fa31239fb218eb608');
    //   wxAccessToken = '2a766ed08690479fa31239fb218eb608';
    // }
    if (!wxAccessToken) {
      const res = await authorizationApi({ url: location.href, applicationId: ''});
      if (res?.data) {
        window.location.href = res.data;
      }
    }
  }

  // 2025/01/03新加免登录
  if (to.fullPath.includes("from=passwordFreeLogin")) {
    let authParams = to.query?.auth
      ? encodeURIComponent(to.query?.auth)
          ?.replace(/%20/g, "+")
          .replace(/%2F/g, "/")
          .replace(/%2B/g, "+")
      : "";
    const passwordValue = decryptData(decodeURIComponent(authParams));
    const user = passwordValue?.split("user=")[1]?.split("&pwd=")[0];
    const pwd = passwordValue?.split("pwd=")[1];
    if (user && pwd) {
      const params = {
        mykey: md5(pwd),
        username: user
      }
      const res = await Login(params);
      console.log(res);
      const { code, data, msg } = res;
      if (SUCCESS_CODE === code) {
        const manageAccessToken = data.accessToken;
        const passwordExpiredTime = data.passwordExpiredTime;
        sessionStorage.setItem("manageAccessToken", manageAccessToken);
        sessionStorage.setItem("user", JSON.stringify(data.user));
        sessionStorage.setItem("permission", JSON.stringify(data.permission));
        sessionStorage.setItem("systemLogo", data.systemLogo || "");
        if (passwordExpiredTime) {
          Message({
            message: passwordExpiredTime,
            type: "success",
          });
        } else {
          Message({
            message: app.$t("successed"),
            type: "success",
          });
        }
        next('/appmanage')
      } else {
        Message({
          message: msg,
          type: "error",
        });
      }
    }
  }
  const token = location.hash?.split("?portaltoken=")?.length > 1 ? location.hash?.split("?portaltoken=")[1] : ""
  const jumpUrl = location.href ? location.href : ""
  if (token) {
    sessionStorage.setItem('loginToken', token);
    sessionStorage.setItem('jumpUrl', jumpUrl);
  }
  // 在这里执行你的拦截逻辑
  // 检查用户是否已登录
  let manageAccessToken = sessionStorage.getItem('manageAccessToken');
  if (!manageAccessToken && to.path !== '/login') {
    if (!token) {
      Message({
        message: '登录已过期，请重新登录',
        type: 'warning',
      });
    }
    next({ name: 'login' }); // 假设'Login'是你的登录页面的名称
  } else {
    next();
  }
});
export default router