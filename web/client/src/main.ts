
import { createApp } from 'vue';
import pinia from '/@/stores/index';
import App from './App.vue';
import router from './router';
import { directive } from '/@/directive/index';
import { i18n } from '/@/i18n/index';
import globalComponents from '/@/components';
import WinboxUI from 'winbox-ui-next';
import 'winbox-ui-next/dist/winbox.css';
import '/@/theme/index.scss';
import * as CoolBoxIcons from '@coolbox/we-design/dist/vue3/es.js';
import '@coolbox/we-design/style/index.css';

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import * as CoolBoxIconsTilake from '@coolbox/tilake/dist/vue3/es.js';
import '@coolbox/tilake/style/index.css';

import 'animate.css';
// import VueAMap from 'vue-amap';
 

import Vue3Lottie from 'vue3-lottie';
import Particles from 'particles.vue3';
// import Vconsole from 'vconsole';
// let vConsole = new Vconsole();
const app = createApp(App);
app.use(Particles);
for (const [key, component] of Object.entries(CoolBoxIcons)) {
  app.component(key + 'We', component);
}
for (const [key, component] of Object.entries(CoolBoxIconsTilake)) {
  app.component(key, component);
}

directive(app);
import {
  Form,
  Field,
  CellGroup,
  Uploader,
  Button,
  DatePicker,
  Popup,
  Calendar,
  RadioGroup,
  Radio,
  Checkbox,
  CheckboxGroup,
  Toast,
  Icon,
  Picker,
  Cascader,
  Cell,
  DropdownMenu,
  DropdownItem,
  Search,
  Dialog,
  Swipe, 
  SwipeItem
} from 'vant';
import 'vant/lib/index.css'

app.use(Button);
app.use(Form);
app.use(Field);
app.use(CellGroup);
app.use(Uploader);
app.use(DatePicker);
app.use(Popup);
app.use(Calendar);
app.use(RadioGroup);
app.use(Radio);
app.use(Checkbox)
app.use(CheckboxGroup);
app.use(Toast);
app.use(Icon);
app.use(Picker);
app.use(Cascader);
app.use(Cell);
app.use(DropdownMenu);
app.use(DropdownItem);
app.use(Search);
app.use(Dialog);
app.use(Swipe);
app.use(SwipeItem);
app.use(ElementPlus)
// app.use(VueAMap);
app.use(pinia).use(router).use(WinboxUI).use(i18n).use(globalComponents).use(Vue3Lottie).mount('#app');
import '/@/assets/static/iconpark.js';
//import '/@/assets/static/xiaoiceRTC1.0.6.js';//数字人
//import '/@/assets/static/ttsa.min.1.0.3.js';//语音
//import '/@/assets/static/jweixin-1.3.2.js';//微信登录
import '/@/assets/static/base64.js';
import '/@/assets/static/jswebrtc.min.js';
import '/@/assets/static/three.min.js';
import '/@/assets/static/index.umd.js';
import '/@/assets/static/tts/index.umd.js';
import '/@/assets/static/ImagePreview.js';
import '/@/assets/static/wwLogin-1.2.7.js';
// VueAMap.initAMapApiLoader({
//      key: '667cfb680520053bc68aefcd738f66cd'
//  })