import Vue from 'vue'
import locale from 'element-ui/lib/locale';
import VueI18n from 'vue-i18n';
//页面中自定义变量
//element-ui自带多语言配置
import zhLocale from 'element-ui/lib/locale/lang/zh-CN';
import zhTWLocale from 'element-ui/lib/locale/lang/zh-TW';
import enLocale from 'element-ui/lib/locale/lang/en';
 
Vue.use(VueI18n)
const messages = {
  en: {
    ...require('./en.json'),
    ...enLocale
  },
  zh: {
    ...require('./zh-CN.json'),
    ...zhLocale
  },
  hk: {
    ...require('./yue-HK.json'),
    ...zhTWLocale
  },
}
 
const i18n = new VueI18n({
  locale:  localStorage.getItem('locale') || 'zh',//从localStorage中拿到用户的语言选择，如果没有，那默认中文。
  messages,
  silentTranslationWarn: true,
})
locale.i18n((key, value) => i18n.t(key, value)) //为了实现element插件的多语言切换
 
export default i18n