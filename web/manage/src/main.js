import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import i18n from '@/locales/index'
import '@/assets/iconpark.js';
import '@/assets/iconfont/iconfont.js';
// 2. 立即加载的全局样式（关键CSS）
import '@/assets/scss/reset.scss' // 注意：建议压缩为critical.css只包含首屏样式
import 'element-ui/lib/theme-chalk/index.css' //element-ui样式
// 第三方包
import ElementUI from 'element-ui'
Vue.use(ElementUI)
//Message全局提示时常
import { Message } from 'element-ui'
Vue.prototype.$message = Message;
// 覆盖默认的 $message
Vue.prototype.$message = function(options) {
  if (typeof options === 'string') {
    options = {
      message: options,
      duration: 1000 // 默认2秒
    }
  } else {
    options.duration = options.duration || 1000 // 允许调用时自定义
  }
  return Message(options)
};

['success', 'warning', 'error', 'info'].forEach(type => {
  Vue.prototype.$message[type] = options => {
    if (typeof options === 'string') {
      options = {
        message: options,
        duration: 1500
      }
    }
    return Message({
      ...options,
      type,
      duration: options.duration || 1000
    })
  }
});
// 1. 最简化的核心初始化
const app = new Vue({
  router,
  store,
  i18n,
  render: h => h(App)
}).$mount('#app')

window.app = app


// 3. 异步加载第三方库和组件（按需加载）
setTimeout(() => {
   // 3.2 异步加载axios配置
  import('@/api/config').then(http => {
    Vue.prototype.$http = http.default
  })
  // 3.3 异步加载富文本编辑器相关资源
  Promise.all([
    import('@wangeditor/editor/dist/css/style.css'),
    import('highlight.js/styles/github.css')
  ])

  // 3.4 异步加载CodeMirror
  Promise.all([
    import("codemirror/lib/codemirror.css"),
    import("codemirror/theme/3024-day.css"),
    import("codemirror/theme/ayu-mirage.css"),
    import("codemirror/theme/monokai.css"),
    import('codemirror/theme/rubyblue.css'),
    import('vue-codemirror').then(({ codemirror }) => {
      Vue.component("codemirror", codemirror)
    })
  ])
  // 3.6 异步加载markdown编辑器
  import('@kangc/v-md-editor/lib/preview').then(VMdPreview => {
    Promise.all([
      import('@kangc/v-md-editor/lib/style/preview.css'),
      import('@kangc/v-md-editor/lib/theme/github'),
      import('@kangc/v-md-editor/lib/theme/style/github.css'),
      import('highlight.js')
    ]).then(([_, githubTheme, __, hljs]) => {
      VMdPreview.default.use(githubTheme.default, {
        Hljs: hljs.default
      })
      Vue.use(VMdPreview.default)
    })
  })

  // 3.7 异步加载EventBus
  import("@/utils/eventBus.js").then(({ EventBus }) => {
    Vue.prototype.$EventBus = EventBus
  })

  Vue.directive('permission', {
    inserted: function (el, binding) {
      let permission = JSON.parse(sessionStorage.getItem('permission'))
      const permissionButton = filterPermissions(permission);
      let treeToArray1 = treeToArray(permissionButton);
      const item = treeToArray1.find(n => n.menuCode === binding.value);
      if (!item) {
        el.style.display = 'none';
      }
    }
  });
  //  移除单选框属性报错  aria-hidden 
Vue.directive('removeAriaHidden', {
  bind(el, binding) {
    let ariaEls = el.querySelectorAll('.el-radio__original');
    ariaEls.forEach((item) => {
      item.removeAttribute('aria-hidden');
    });
  }
});
}, 0)
// 过滤权限
function filterPermissions(permission) {
  return permission
      .filter((item) => !item.clientType || item.clientType === "PC")
      .filter((item) => item.isHidden !== 1)
      .map((item) => {
        if (item.children) {
          item.children = filterPermissions(item.children);
        }
        return item;
      });
}

// 将树形解析成数组
function treeToArray(data) {
  let temp = [];
  data.forEach((item) => {
    if (item.children) {
      temp = temp.concat(treeToArray(item.children));
    }
    temp.push(item);
  });
  return temp;
}
Vue.config.productionTip = false
export default app;