import { App } from 'vue';
import { use } from 'echarts/core';

import { GridComponent, TooltipComponent, LegendComponent, DataZoomComponent, GraphicComponent, TitleComponent } from 'echarts/components';

import SvgIcon from './svgIcon/index.vue';
import YYMessage from './yy-message/index.vue';
import YYMessageChat from './yy-messagechat/index.vue';
import YYMessagechatLarge from './yy-messagechatLarge/index.vue';
import YYMessageNew from './yy-message-new/index.vue';
import YYMessageAssPc from './yy-message-assPc/index.vue';
import YYMessageAssH5 from './yy-message-assH5/index.vue';
import yyMessageHf from './yy-message-hf/index.vue';
import yyMessageTz from './yy-message-tz/index.vue';
import yyMessageSZR from './yy-message-szr/index.vue';
// 重庆高新区相关
import YYMessageCqH5 from './yy-message-cqH5/index.vue';
import YYMessageCqPolicyH5 from './yy-message-cqH5/indexPolicy.vue';
// 市监局
import YYMessageSAMR from './yy-message-SAMR/index.vue';
// 市监局
import YYMessageSAMRNewUI from './yy-message-SAMR-newUI/index.vue';
// 标准应用
import YYMessageAppTemplate from './yy-message-appTemplate/index.vue';
// 标准应用（粤语）
import YYMessageCantoneseTemplate from './yy-message-cantoneseTemplate/index.vue';
// 数字人(新版：市监局模板+接入数字人)
import YYMessageDHtemplate from './yy-message-DHtemplate/index.vue';
// 深圳网信办
import YYMessageSzcac from './yy-message-szcac/index.vue';
// 基础应用(后台管理-应用配置展示使用)
import YYMessageBasicTemplate from './yy-message-basicTemplate/index.vue';
// 四川xxxxxxx应用
import YYMessageDazhouTemplate from './yy-message-dazhouTemplate/index.vue';
// H5通用模板
import YYMessageMobileUniversalTemplate from './yy-message-mobileUniversalTemplate/index.vue';
import YYMessageMobileDazhouTemplate from './yy-message-mobileDazhouTemplate/index.vue';
// 郑小东模板(法阁君)
import YYMessageZhengxiaodong from './yy-message-zhengxiaodong/index.vue';
// xxxxxxx智能问答模板
import YYMessagelonghuaChat from './yy-message-longhuaChat/index.vue';
// 视频背景大屏
import YYMessageVideoLargeScreen from './yy-message-videoLargeScreen/index.vue';
// 智库数据分析
import YYMessageTtAnalysis from './yy-message-ttAnalysis/index.vue';
// xxxxxxx新版UI
import YYMessageLonghua from './yy-message-longhua/index.vue';

use([GridComponent, TooltipComponent, LegendComponent, DataZoomComponent, GraphicComponent, TitleComponent]);

export default {
	install(Vue: App) {
		Vue.component('SvgIcon', SvgIcon);
		Vue.component('YYMessage', YYMessage);
		Vue.component('YYMessageNew', YYMessageNew);
		Vue.component('YYMessageAssPc', YYMessageAssPc);
		Vue.component('YYMessageAssH5', YYMessageAssH5);
		Vue.component('YYMessageChat', YYMessageChat);
		Vue.component('YYMessagechatLarge', YYMessagechatLarge);
		Vue.component('yyMessageHf', yyMessageHf);
		Vue.component('yyMessageTz', yyMessageTz);
		Vue.component('YYMessageCqH5', YYMessageCqH5);
		Vue.component('YYMessageCqPolicyH5', YYMessageCqPolicyH5);
		Vue.component('yyMessageSZR', yyMessageSZR);
		Vue.component('YYMessageSAMR', YYMessageSAMR);
		Vue.component('YYMessageSAMRNewUI', YYMessageSAMRNewUI);
		Vue.component('YYMessageAppTemplate', YYMessageAppTemplate);
		Vue.component('YYMessageCantoneseTemplate', YYMessageCantoneseTemplate);
		Vue.component('YYMessageDHtemplate', YYMessageDHtemplate);
		Vue.component('YYMessageSzcac', YYMessageSzcac);
		Vue.component('YYMessageBasicTemplate', YYMessageBasicTemplate);
		Vue.component('YYMessageDazhouTemplate', YYMessageDazhouTemplate);
		Vue.component('YYMessageMobileUniversalTemplate', YYMessageMobileUniversalTemplate);
		Vue.component('YYMessageMobileDazhouTemplate', YYMessageMobileDazhouTemplate);
		Vue.component('YYMessageZhengxiaodong', YYMessageZhengxiaodong);
		Vue.component('YYMessagelonghuaChat', YYMessagelonghuaChat);
		Vue.component('YYMessageVideoLargeScreen', YYMessageVideoLargeScreen);
		Vue.component('YYMessageTtAnalysis', YYMessageTtAnalysis);
		Vue.component('YYMessageLonghua', YYMessageLonghua);
	},
};
