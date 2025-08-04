<script lang="ts" setup>
import { defineAsyncComponent, computed, ref, onMounted, onUnmounted, nextTick, h, watch } from 'vue';
import { Modal, Message } from 'winbox-ui-next';
window.$Modal = Modal;
import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import mila from 'markdown-it-link-attributes';
import mic from 'markdown-it-container';
 
import hljs from 'highlight.js';
import { useBasicLayout } from '/@/hooks/useBasicLayout';

import { useCopyCode } from '/@/hooks/useCopyCode';
import { useChatStore } from '/@/stores/chat';
import { useKnowledgeState } from '/@/stores/knowledge';
import { copyText } from '/@/utils/format';
import { useRoute } from 'vue-router';
import { Session } from '/@/utils/storage';
import { v4 as uuidv4 } from 'uuid';
import { getById } from '/@/api/knowledge';
import axios from 'axios';
const route = useRoute();

const { appId } = route.params as { appId: string };
// 引入默认样式
// import 'highlight.js/scss/default.scss'
// 引入个性化的vs2015样式
// import 'highlight.js/styles/atom-one-dark.css';
// import 'highlight.js/styles/atom-one-light.css';
// import 'highlight.js/styles/base16/cupertino.css';
// import 'highlight.js/styles/github.css';
// import 'highlight.js/styles/base16/one-light.css';

import 'highlight.js/styles/github.css';
// import hotIcon from './hot.svg';
const hotIcon =
	'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 18 18" width="50" height="50"><defs><linearGradient id="a" y2="100%" x2="50%" y1="0%" x1="50%"><stop offset="0%" stop-color="#FF9DAB"></stop><stop offset="100%" stop-color="#F54B5B"></stop></linearGradient></defs><g fill-rule="evenodd" fill="none"><path d="M0 0h18v18H0z"></path><path fill-rule="nonzero" fill="url(#a)" d="M12.269 4.77c.279 1.115-.249 2.404-1.241 3.008-.001-1.76-.189-3.43-1.22-4.829C9.24 2.184 8.37 1.52 7.523 1c.066 1.4.056 4.068-1.629 5.49C4.21 7.913 3 9.254 3 11.632c0 3.955 3.123 5.05 5.92 5.118 2.797.068 6.08-1.905 6.08-5.532 0-3.626-1.11-5.198-2.731-6.449Z"></path></g></svg>';
interface Props {
	inversion?: boolean;
	error?: boolean;
	text?: string;
	loading?: boolean;
	asRawText?: boolean;
	citations?: any;
	dialogueFileIds?: any;
	dialogueFolderIds?: any;
	paragraph?: string;
	skillId?: string;
	params?: any;
	isHighlightCode?: boolean;
	imgUrl?: any;
	matterGuide?: any;
	finishReason?: any;
	businessScenarioLists?: any;
	businessScenario?: any;
}

const props = defineProps<Props>();
const emits = defineEmits(['update:isHighlightCode', 'handleFillForm']);
const chatStore = useChatStore();
const knowledgeState = useKnowledgeState();
const { isMobile } = useBasicLayout();
// scene组件所需参数
const paramData = ref();

// 业务场景代码
const SceneProcess = defineAsyncComponent(() => import('../scene/index.vue'));

const drawerVisible = ref(false);

const closeDrawer = () => {
	drawerVisible.value = false;
};
// 侧拉流程按钮
const sidePullProcessTitle = computed(() => props.matterGuide?.sceneManagement?.aliasName);
const sidePullProcessSubTitle = computed(() => props.matterGuide?.sceneManagement?.matterDesc);
// 侧拉流程
const sidePullProcess = computed(() => props.matterGuide?.matterNameList?.filter((item: any) => item?.display == '侧拉流程'));

// 侧拉流程分组
// const sidePullProcessGroupList = computed(() =>

const sidePullProcessGroupList = () => {
	const groupedData = {};

	sidePullProcess.value.forEach((item) => {
		if (!groupedData[item.groupName]) {
			groupedData[item.groupName] = [];
		}
		groupedData[item.groupName].push(item);
	});
	// console.log("groupedData", groupedData)
	const resultArray = Object.keys(groupedData);
	// const resultArray = [groupedData];
	// console.log('resultArray', resultArray);
	console.log('resultArray', resultArray);
	return resultArray;
};

const processHandler = (name: string) => {
	paramData.value = sidePullProcess.value?.filter((item: any) => item.groupName == name);
	console.log('paramData', paramData);
	drawerVisible.value = true;
};

// 弹框表单
const popUpForm = computed(() => props.matterGuide?.matterNameList?.filter((item: any) => item?.display == '弹框表单'));
// 内嵌卡片
const embeddedCard = computed(() => props.matterGuide?.matterNameList?.filter((item: any) => item?.display == '内嵌卡片'));
// 内嵌按钮-上传图片
const embeddedButtonUploadImage = computed(() => props.matterGuide?.matterNameList?.filter((item: any) => item?.display == '内嵌按钮-上传图片'));
// 内嵌按钮-跳转外网
const embeddedButtonJumpToExternalNetwork = computed(() =>
	props.matterGuide?.matterNameList?.filter((item: any) => item?.display == '内嵌按钮-跳转外网')
);

let chatMode = computed(() => route.path.indexOf('chat') != -1);
window.setsendMessage = (content: string, param: any, param_origin: any) => {
	try {
		if (chatStore.dialogueLoading) return;
		const data = {
			appId,
			content,
			conversationId: route.params.conversationId,
			param: JSON.stringify({ ...param_origin, ...param }),
		};
		if (props.dialogueFileIds) {
			data.dialogueFileIds = props.dialogueFileIds;
		}
		if (props.dialogueFolderIds) {
			data.dialogueFolderIds = props.dialogueFolderIds;
		}
		if (props?.paragraph) {
			data.paragraph = props?.paragraph;
		}
		if (props?.skillId) {
			data.skillId = props?.skillId;
		}
		chatStore.setChatList(data);
	} catch (error) {}
};
useCopyCode();
const textRef = ref<HTMLElement>();

const mdi = new MarkdownIt({
	html: true,
	linkify: false,
	highlight(code, language) {
		const validLang = !!(language && hljs.getLanguage(language));
		if (validLang) {
			const lang = language ?? '';
			return highlightBlock(hljs.highlight(code, { language: lang }).value, lang);
		}
		return highlightBlock(hljs.highlightAuto(code).value, '');
	},
});

mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' });
const recommendKey = [
	{ name: '热点摘要', key: '内容进行概述', type: 1, param: {} },
	{ name: '观点分析', key: '各方观点', type: 2, param: { get_news_num: 1 } },
];
const curText = ref('');
// 注册插件，并指定自定义容器的名称和渲染方式

// 注册插件，并指定自定义容器的名称和渲染方式
mdi.use(mic, 'r', {
	// 验证自定义容器是否符合规则
	validate: function (params) {
		return params.trim().match(/^r\s+(.*)$/);
	},
	// 渲染自定义容器的开头
	render: function (tokens, idx) {
		var m = tokens[idx].info.trim().match(/^r\s+(.*)$/);
		if (tokens[idx].nesting === 1) {
			const arr = m[1].split(',');
			let result = '';
			// 生成一个 10 到 999 之间的随机整数
			const random = Math.floor(Math.random() * (999 - 10 + 1)) + 10;
			if (arr[1]) {
				result = '<span>相关：</span>';
				recommendKey.forEach((element, index) => {
					const text = arr[0].split('.')[1];
					const key = element.type === 1 ? `请对${text} 的${element.key}，详细列举：` : `关于${text} 的${element.key}，详细列举：`;
					result += `
						<button type="button"  onclick='setsendMessage("${key}",${JSON.stringify(element.param)},${
						props.params
					})'  class="w-btn w-btn-outline recommend-item recommend-item-${index}">
							${element.name}
						</button>
					`;
				});
			}
			if (!chatMode.value) {
				const key = arr[0];
				const params = {};
				return `
					<div  class="recommend-title target-${idx * random} recommend-title-pointer" onclick='setsendMessage("${key}",${JSON.stringify(params)},${
					props.params
				})'>
					${arr[0]}
					</div>
				`;
			} else {
				const hotStr = arr[1] ? `<xy-button  class="recommend-hot">${hotIcon}${arr[1]}</xy-button>` : ``;
				// 返回一个带有标题和隐藏按钮的div元素
				return `
					<div  class="recommend-title target-${idx * random}">
					${arr[0]}
					${hotStr}
					</div>
					<div class="recommend-popup" style="margin-bottom:3px"  target=".target-${idx * random}" type="custom" >
					
					${result}
					</div>
					`;
			}
		} else {
			return '';
		}
	},
});
// 自定义插件
const nullLinkPlugin = (md) => {
	const defaultRender = md.renderer.rules.link_open || ((tokens, idx, options, env, self) => self.renderToken(tokens, idx, options));

	md.renderer.rules.link_open = (tokens, idx, options, env, self) => {
		const token = tokens[idx];
		const hrefIndex = token.attrIndex('href');
		const targetIndex = token.attrIndex('target');
		// const hrefIndex = token.attrIndex('href');

		if (hrefIndex !== -1 && token.attrs[hrefIndex][1] === 'null') {
			// 将链接地址为 null 的 href 替换为 javascript:void(0)
			token.attrs[hrefIndex][1] = 'javascript:void(0)';
			token.attrs[targetIndex][1] = '';
		}

		return defaultRender(tokens, idx, options, env, self);
	};
};
mdi.use(nullLinkPlugin);
mdi.use(mila, { attrs: { target: '_blank', rel: 'noopener' } });

const wrapClass = computed(() => {
	return [
		'min-w-[20px]',
		'text-box-' + appId,
		isMobile.value ? 'p-4' : 'px-4 py-2',
		props.inversion ? 'bg-[#e0eaff]' : 'bg-[#e0eaff]',
		props.inversion ? (isMobile.value ? ' ' : ' ') : isMobile.value ? ' ' : 'py-4 ',
		props.inversion ? 'dark:bg-[#a1dc95]' : 'dark:bg-[#1e1e20]',
		props.inversion ? 'message-request' : 'message-reply',
		props.inversion ? 'text-black' : 'text-wrap',
		{ 'text-red-500': props.error },
		props.inversion ? (props.imgUrl ? 'isImgCss' : '') : props.imgUrl ? '' : '',
		props.inversion ? 'text-message-request' : 'text-message-reply',
		props.inversion ? 'border-right' : 'border-left',
	];
});
const isChat = computed(() => route.path.indexOf('chat') != -1);
const maxHight = computed(() => {
	return [props.inversion ? (isChat.value ? '' : 'maxHeight260') : isChat.value ? '' : ''];
});

const handleImageLoad = (event) => {
	const dom = (document.querySelector('.center-side').scrollTop = 9999999);
};

const isMarkdownImg = ref([]);
const addLoadEventToImgTags = (htmlString) => {
	var regex = /(<img\s+[^>]*)(>)/g;
	var replacement = `$1  onclick="showPreviewImg(event)" $2`;
	if (appId == '20') {
		replacement = `$1  onclick="showPreviewImg(event)" onload="document.querySelector('.center-side').scrollTop = 999999999"$2`;
	}
	// var replacement = `$1  onclick="showPreviewImg(event)" onload="document.querySelector('.center-side').scrollTop = 999999999"$2`;
	// var replacement = `$1  onclick="showPreviewImg(event)" $2`;
	var newHtmlString = htmlString.replace(regex, replacement);

	return newHtmlString;
};
// 定义一个函数，接受一个对象作为参数，返回一个字符串
const textToMarkdown = (text: string, citations: any) => {
	// 判断json格式
	try {
		const jsonObj = JSON.parse(text); // 将字符串解析为一个对象
		if(typeof jsonObj === 'object' && jsonObj !== null){
      const jsonFormatted = JSON.stringify(jsonObj, null, 2);
		  text = '```json\n' + jsonFormatted + '\n```';
    }
	} catch (error) {}

	// 截取:::r 结束的字符串
	if (text.endsWith('\n:')) {
		text = text.slice(0, -3);
	}
	if (text.endsWith('\n::')) {
		text = text.slice(0, -4);
	}
	if (text.endsWith('\n:::')) {
		text = text.slice(0, -5);
	}
	if (text.endsWith('\n:::r')) {
		text = text.slice(0, -6);
	}
	if (text.endsWith('\n:::r ')) {
		text = text.slice(0, -7);
	}

	// 初始化一个空字符串，用于存储结果
	let result = '';
	if (!citations) {
		return text;
	}
	// 是否包含图片
	var regexImage = /!\[.*?\]\(.*?\)/g;
	// isMarkdownImg.value = regexImage.test(text);
	isMarkdownImg.value = text.match(regexImage) || [];

	// 遍历对象的 res_info 属性，获取每个键值对
	for (const key in citations) {
		// 获取当前键值对的值，即一个子对象
		const value = citations[key];
		const { no, fileUrl = '#', fileName } = value;
		// 将子对象的 title 和 context 属性拼接成 markdown 的链接引用格式，并添加到结果字符串中
		if (route.path.indexOf('chat') != -1) {
			result += `[${no}]: ${fileUrl ? encodeURI(fileUrl) : null} \'${fileName}\'\n\n`;
		} else {
			result += `[${no}]: ${fileUrl ? null : null} \'${fileName}\'\n\n`;
		}
	}
	// 在结果字符串末尾添加一个空行
	result += '\n';
	// 将对象的 res 属性的值，即一个字符串，替换其中的数字为上标引用，并在每个数字后面加上一个空格，并添加到结果字符串中
	result += text.replace(/\[(\d+)\]/g, (match, p1) => {
		// 使用回调函数替换匹配的内容
		const num = Number(p1); // 将 $1 转换为数字
		// return `<a href="${res_info[num].title}">${res_info[num].context || ''}</a>`; // 返回对应的 context
		return `[${num}][${num}]`; // 返回对应的 context
	});
	// 返回结果字符串
	return result;
};
const text = computed(() => {
	const value = props.text ?? '';
	if (!props.asRawText) {
		// 针对markdown格式、//n 做特殊处理
		let strsss = props.text ? props.text.replace(/\\n/g, '\n') : '';
		// let startText = ['&e', '&en', '&ens', '&ensp', , '</', '</b', '</br', '<b', '<br', '<h', '<hr', '<hr '];
		// let endText = ['&ensp;', '>', '查询或咨询。'];
		// startText.forEach((i) => {
		// 	if (strsss.endsWith(i)) {
		// 		curText.value = strsss?.slice(0, strsss.lastIndexOf(i));
		// 	}
		// });
		// endText.forEach((i) => {
		// 	if (strsss.endsWith(i)) {
		// 		curText.value = '';
		// 	}
		// });
		// const value = strsss
		// 	? textToMarkdown(`[<span class='link-btn'>测试链接跳转&nbsp;&nbsp;&gt;</span>](http://coolbox.localhost/#/detail/12888)`, props.citations)
		// 	: '';
		const value = strsss ? textToMarkdown(curText.value || strsss, props.citations) : '';
		let htmlStr = mdi.render(value);
		htmlStr = addLoadEventToImgTags(htmlStr);

		return htmlStr;
	}
	return value;
});

const fileUrl = ref('');
const fileName = ref('');
const fileSize = ref('');

const beforeUpload = (file: any) => {
	console.log('file', file.type);
	if (!file?.type?.includes('image')) {
		Message.warning('图片格式错误');
		return false;
	}
};

const uploadLoading = ref(false);

const uploadHandler = async (param: any) => {
	uploadLoading.value = true;
	const formData = new FormData();
	formData.append('file', param.file);
	fileName.value = param?.file?.name;
	fileSize.value = (param?.file?.size / 1024)?.toFixed(2);
	try {
		const response = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/matterGuideFiled/uploadYYZZPic`, formData, {
			headers: {
				'Content-Type': 'audio/wave;multipart/form-data',
			},
		});
		if (response.status === 200) {
			console.log('response', response);
			uploadLoading.value = false;
			fileUrl.value = response?.data?.data || '';
			const paramObj = {
				yyzhUrl: fileUrl.value,
				applicationId: localStorage.getItem(`${route.params.appId}appId`),
				dialogueId: sessionStorage.getItem('dialogueId'),
			};
			const res = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/record/updateYYZFPicUrl`, paramObj);
			if (res.status == 200) {
				sendQuestion(embeddedButtonUploadImage.value[0]?.completeFillTip, 'img', null);
			}
		}
	} catch (error) {
		uploadLoading.value = false;
		console.error('Error :', error);
	}
};

const sendQuestion = (item: string, type: string, datas: any) => {
	if (chatStore.dialogueLoading) return;
	if (datas && datas.matterType == '事项-固定对话') {
		item = datas.fillTip;
	}
	let param = props.params;
	if (typeof param === 'object') {
		param = JSON.stringify(props.params);
	}
	// let appId = typeof props.param === 'string'?JSON.parse(props.param)?.promptId : props.param?.promptId
	const routeParams = route.params;
	const data: any = {
		knowledgeBaseId: routeParams.appId,
		appId: routeParams.appId,
		// appId:  appId,
		content: item,
		conversationId: route.params.conversationId,
		param,
	};
	if (props?.dialogueFileIds) {
		data.dialogueFileIds = props?.dialogueFileIds;
	}
	if (props?.dialogueFolderIds) {
		data.dialogueFolderIds = props?.dialogueFolderIds;
	}
	if (props?.paragraph) {
		data.paragraph = props?.paragraph;
	}
	if (props?.skillId) {
		data.skillId = props?.skillId;
	}
	// if(type == 'img' && fileUrl.value) {
	// 	console.log('type', type)
	// 	data.imgUrl = fileUrl.value
	// }
	chatStore.setChatList(data);
	return;
};

const previewPic = (url: string) => {
	window.open(url, '_blank');
};
// let regex = /@([^@\s]+\.(pdf|docx|doc|txt))/g;
let regex = /@([^@]+\.(pdf|docx|doc|txt))/g;
const isContainFile = computed(() => {
	if (!text.value || !props.inversion) return '';
	const matches = text.value.match(regex);
	return matches?.length;
});

const containFile = ref();

watch(
	() => containFile.value,
	(val) => {
		if (val) {
			TextToHtml();
		}
	}
);
watch(
	() => text.value,
	(val) => {
		if (val && !asRawText.value && timer.value) {
			clearInterval(timer.value);
		}
	}
);

const asRawText = ref(props.inversion);
const fileJump = async (id: any) => {
	let res = await getById(id);
	if (res.code == 200 && res.data) {
		let data = res.data;
		data.fileId = data.id;
		pdfJump(data, {});
	}
};
const TextToHtml = async () => {
	let fileIds = props.dialogueFileIds;
	let val = text.value;
	let matches = val.match(regex) || [];
	let files = fileIds.map((i: any, index: number) => {
		return {
			id: i,
			name: matches[index],
		};
	});
	let num = 0;
	let uuid = await uuidv4();
	let textArr = val.replace(regex, uuid).split(uuid);
	for (let i = 0; i < textArr.length; i++) {
		let item: any = textArr[i];
		let span = document.createElement('span');
		if (!item.trim()) {
			let file = files[num];
			if (!file) {
				break;
			}
			span.innerHTML = file?.name;
			span.className = 'attribution-items-file';
			span.onclick = () => {
				fileJump(file?.id);
			};
			num++;
		} else {
			span.innerHTML = item;
		}
		containFile.value.append(span);
	}
};

function highlightBlock(str: string, lang?: string) {
	emits('update:isHighlightCode', true);
	let langStr = '';
	if (lang) {
		langStr = `<span class="code-block-header__lang">${lang}</span>`;
	}
	return `<pre class="code-block-wrapper"><div class="code-block-header">${langStr}<span class="code-block-header__copy" onclick="copyBtn()">复制代码</span><span class="code-block-header__theme" onclick="themeBtn()">主题切换</span></div><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}

const pdfJump = (citation: any, page: any) => {
	let currItem: any = {};
	citation.id = citation.fileId;
	citation.name = citation.fileName;
	currItem = citation;
	let active = citation.id;
	let params = {
		currItem,
		active,
		params: {
			page: page?.pageNo ? page.pageNo : 1,
			paragraphs: page?.paragraphs ? page.paragraphs : [],
		},
	};
	knowledgeState.setPreviewData(params);
};

let timer = ref();
let curNum = ref(1);
// 当界面挂载出来后就会自动执行
onMounted(() => {
	timer.value = setInterval(() => {
		curNum.value += 1;
		if (curNum.value > 3) {
			curNum.value = 1;
		}
	}, 350);
});

// 页面卸载时
onUnmounted(() => {});
defineExpose({ textRef });

const truncate = (str: string, limit: number) => {
	return str.length > limit ? str.slice(0, limit - 1) + '...' : str;
};

const optimizeCitations = (citations: any[]) => {
	const obj = {};
	citations.forEach((item) => {
		if (item.fileId in obj) {
			obj[item.fileId] = {
				...obj[item.fileId],
				pages: [...obj[item.fileId].pages, ...item.pages],
			};
		} else {
			obj[item.fileId] = item;
		}
	});
	return Object.keys(obj).reduce((arr, i) => [...arr, obj[i]], []);
};

const handleFillForm = (info) => {
	emits('handleFillForm', info);
};

const geTailTalk = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.tailTalk : '';
};
</script>

<template>
	<div class="text-black" style="border: 1px solid #fff; position: relative" :class="wrapClass">
		<div ref="textRef" class="leading-relaxed break-words" :class="maxHight">
			<!-- 知识库 -->
			<div v-if="!inversion">
				<div
					v-if="!asRawText && text"
					class="markdown-body"
					:class="['markdown-body-as-img-' + isMarkdownImg.length, chatStore.isLightTheme ? 'hljs-atom-one-light' : 'hljs-atom-one-dark']"
					v-html="text"
				/>
				<div v-if="!asRawText && !text" class="markdown-body icon-box">
					<div class="loading-icon">
						<div :class="{ active: curNum == 1 }" class="icon1"></div>
						<div :class="{ active: curNum == 2 }" class="icon2"></div>
						<div :class="{ active: curNum == 3 }" class="icon3"></div>
						<div class="icon4">飞速思考中</div>
					</div>
					<!-- <span> AI飞速思考中，请稍等...</span> -->
				</div>
				<!-- <div>{{ matterGuide?.matterNameList }}</div> -->
				<!-- display=弹框表单 -->
				<div class="matter-box" v-if="finishReason === 'ANSWER_COMPLETE' && popUpForm?.length">
					<template v-for="(pop, popIdx) in popUpForm" :key="popIdx">
						<div class="message">{{ pop?.enterTip }}</div>
						<div class="btn" @click="handleFillForm(pop)">
							<img :src="pop?.iconUrl" alt="" />
							{{ pop?.aliasName }}
						</div>
					</template>
				</div>
				<!-- display=内嵌卡片 -->
				<div v-if="finishReason === 'ANSWER_COMPLETE' && embeddedCard?.length" class="businessScenarioLists">
					<img class="bg2" src="/src/assets/municipalSupervisionBureau/bg2.png" alt="" />
					<div class="businessScenarioLists-title">您是否想要办理以下业务</div>
					<div class="businessScenarioLists-content">
						<div
							v-for="(item, index) in embeddedCard"
							:key="index"
							class="businessScenarioLists-content-item"
							@click="sendQuestion(item.matterName, '', item)"
						>
							{{ item.aliasName }}
						</div>
					</div>
				</div>
				<!-- diaplay=侧拉流程 -->
				<div v-if="finishReason === 'ANSWER_COMPLETE' && sidePullProcess?.length" class="sidePullProcess" style="margin-top: 12px">
					<!-- {{ sidePullProcessBtnName || '点击填写信息' }} -->
					<div class="sidePullProcess-left">
						<div class="sidePullProcessTitle ellipsis">{{ sidePullProcessTitle }}</div>
						<div class="sidePullProcessSubTitle ellipsis">{{ sidePullProcessSubTitle }}</div>
					</div>
					<div class="sidePullProcess-right">
						<div
							v-for="(btnItem, btnIdx) in sidePullProcessGroupList()"
							:key="btnIdx"
							class="upload-btn"
							style="margin-left: 8px"
							@click="processHandler(btnItem)"
						>
							{{ btnItem }}
						</div>
					</div>
				</div>
				<!-- diaplay=内嵌按钮-上传图片 -->
				<div v-if="finishReason === 'ANSWER_COMPLETE' && embeddedButtonUploadImage?.length" style="margin-top: 12px">
					<template v-for="(uploadData, uploadIdx) in embeddedButtonUploadImage" :key="uploadIdx">
						<el-upload
							class="embeddedButtonUploadImage"
							ref="upload"
							action="#"
							:show-file-list="false"
							:before-upload="beforeUpload"
							:http-request="uploadHandler"
							accept="image/*"
						>
							<div v-if="!fileUrl" style="width: 100%; height: 100%" v-loading="uploadLoading">
								<div class="name">{{ uploadData.aliasName || '上传' }}</div>
								<div class="outer">
									<iconpark-icon name="image-add-line" size="28" color="#2065D6"></iconpark-icon>
									<div class="txt1">拖拽图片到这里，或点击上传</div>
									<div class="txt2">支持上传PNG、JPG、JPEG格式文件,大小在2MB以下</div>
								</div>
							</div>
							<div v-else style="width: 100%; height: 100%">
								<div class="name">{{ uploadData.aliasName || '上传' }}</div>
								<div class="outer2">
									<img :src="fileUrl" alt="" />
									<div class="outer2-right">
										<div class="outer2-right-name">{{ fileName }} <iconpark-icon name="checkbox-circle-fill" size="12" color="#1DB9A2"></iconpark-icon></div>
										<div class="outer2-right-size">{{ fileSize }}KB</div>
										<div class="outer2-right-btn">重新上传</div>
									</div>
								</div>
							</div>

							<!-- <div v-else>
							<div class="upload-btn" @click="previewPic(uploadData.fileUrl)">点击预览</div>
						</div> -->
						</el-upload>
					</template>
				</div>
				<!-- diaplay=内嵌按钮-跳转外网 -->
				<div v-if="finishReason === 'ANSWER_COMPLETE' && embeddedButtonJumpToExternalNetwork?.length" style="margin-top: 12px">
					<div v-for="(jumpData, jumpIdx) in embeddedButtonJumpToExternalNetwork" :key="jumpIdx">
						<a
							:href="jumpData.matterRoute"
							target="_blank"
							style="
								display: inline-block;
								padding: 0 8px;
								height: 32px;
								line-height: 32px;
								border-radius: 4px;
								background: rgba(237, 244, 252, 1);
								color: #2065d6;
								font-size: 14px;
							"
							>{{ jumpData.aliasName }}</a
						>
					</div>
				</div>
				<div class="markdown-body" style="display: block" v-html="geTailTalk()" v-if="finishReason === 'ANSWER_COMPLETE'" />
				<div v-if="asRawText" class="whitespace-pre-wrap" v-text="text" />
			</div>
			<div v-if="inversion && (paragraph || imgUrl)" class="whitespace-annex-box">
				<img v-if="imgUrl" :src="imgUrl" alt="" />
				<span v-else-if="paragraph">{{ paragraph }}</span>
			</div>
			<div
				v-if="inversion && !isContainFile"
				class="whitespace-pre-wrap"
				:class="inversion && (paragraph || imgUrl) ? 'textBorder' : ''"
				v-text="text"
			/>
			<div v-if="isContainFile" class="whitespace-pre-wrap" :class="inversion && (paragraph || imgUrl) ? 'textBorder' : ''" ref="containFile" />
			<!-- <template v-if="!text && !inversion">
				<span class="dark:text-white w-[2px] h-[20px] block animate-blink" />
			</template> -->
		</div>

		<slot name="footer" v-if="!asRawText && text"></slot>
		<slot name="question"></slot>
		<!-- 侧拉流程 -->
		<SceneProcess
			v-if="drawerVisible"
			:visible="drawerVisible"
			:paramsData="paramData"
			:title="sidePullProcessTitle"
			:subtitle="sidePullProcessSubTitle"
			@closeDrawer="closeDrawer"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import './style.scss';
@import '/@/theme/mixins/index.scss';

.matter-box {
	width: 100%;
	background: linear-gradient(270deg, rgba(32, 101, 214, 0.1) 0%, rgba(44, 175, 243, 0.1) 100%);
	border-radius: 8px;
	padding: 16px 12px;
	box-sizing: border-box;

	.message {
		font-weight: 500;
		font-size: 16px;
		color: #383d47;
		text-align: center;
	}

	.btn {
		cursor: pointer;
		width: 256px;
		height: 36px;
		background: #2065d6;
		border-radius: 12px;
		font-weight: 400;
		font-size: 18px;
		color: #ffffff;
		display: flex;
		justify-content: center;
		align-items: center;
		margin: 0 auto;
		margin-top: 5px;

		img {
			width: 15px;
			height: auto;
			margin-right: 5px;
		}
	}
}

.businessScenarioLists {
	margin-top: 12px;
	position: relative;
	width: 100%;
	padding: 12px 16px;
	background: linear-gradient(270deg, rgba(32, 101, 214, 0.1) 0%, rgba(44, 175, 243, 0.1) 100%);
	background-repeat: no;
	border-radius: 8px;

	.bg2 {
		position: absolute;
		right: 21px;
		top: 5px;
		width: 54px;
		height: 54px;
	}

	&-title {
		font-family: MiSans, MiSans;
		font-weight: 600;
		@include add-size($font-size-base14, $size);
		color: #383d47;
		line-height: 18px;
	}

	&-content {
		width: 100%;
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;

		&-item {
			width: 32.8%;
			padding: 0 4px;
			height: 40px;
			line-height: 40px;
			text-align: center;
			background: rgba(255, 255, 255, 0.8);
			border-radius: 8px;
			margin-top: 8px;
			font-family: MiSans, MiSans;
			font-weight: 400;
			@include add-size($font-size-base16, $size);
			color: #383d47;
			cursor: pointer;
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
		}
	}
}

.embeddedButtonUploadImage {
	padding: 18px 16px;
	background: #f4f6f9;
	border-radius: 12px;
	font-family: MiSans, MiSans;
	cursor: pointer;
	:deep(.el-upload) {
		display: flex;
		flex-direction: column;
	}
	.name {
		font-family: MiSans, MiSans;
		font-weight: 600;
		@include add-size($font-size-base16, $size);
		color: #383d47;
		line-height: 24px;
		width: 100%;
	}
	.outer {
		height: 146px;
		background: #ffffff;
		border-radius: 8px;
		border: 1px dashed #e1e4eb;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		width: 100%;
		margin-top: 14px;
		.txt1 {
			font-family: MiSans, MiSans;
			font-weight: 600;
			@include add-size($font-size-base14, $size);
			color: #383d47;
			line-height: 20px;
			margin: 12px 0 6px;
		}
		.txt2 {
			height: 16px;
			font-family: MiSans, MiSans;
			font-weight: 400;
			@include add-size(12px, $size);
			color: #828894;
			line-height: 16px;
		}
	}
	.outer2 {
		height: 136px;
		background: #ffffff;
		border-radius: 8px;
		display: flex;
		align-items: center;
		width: 100%;
		margin-top: 12px;
		padding: 8px;

		img {
			width: 120px;
			height: 120px;
			border-radius: 8px;
			border: 1px solid #e1e4eb;
		}
		&-right {
			margin-left: 24px;
			&-name {
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 14px;
				color: #383d47;
				line-height: 20px;
			}
			&-size {
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 12px;
				color: #828894;
				line-height: 16px;
				margin: 4px 0 32px;
			}
			&-btn {
				width: 80px;
				height: 32px;
				line-height: 32px;
				text-align: center;
				background: rgba(32, 101, 214, 0.1);
				border-radius: 8px;
				font-size: 14px;
				color: #2065d6;
			}
		}
	}
}

.upload-btn {
	display: inline-block;
	padding: 0 8px;
	height: 32px;
	line-height: 32px;
	text-align: center;
	background: #2065d6;
	border-radius: 4px;
	font-family: MiSans, MiSans;
	@include add-size($font-size-base14, $size);
	color: #ffffff;
	cursor: pointer;
}

.sidePullProcess {
	width: 100%;
	height: 84px;
	background: #edf4fc;
	border-radius: 8px;
	padding: 16px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	&-right {
		width: 30%;
		display: flex;
		align-items: center;
		justify-content: flex-end;
	}
	&-left {
		flex: 1;
		.ellipsis {
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
		}
		.sidePullProcessTitle {
			font-family: MiSans, MiSans;
			font-weight: 500;
			font-size: 18px;
			color: #383d47;
			line-height: 22px;
		}
		.sidePullProcessSubTitle {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 14px;
			color: #828894;
			line-height: 20px;
			margin-top: 8px;
		}
	}
}

.text-black {
	padding: 0 !important;
	.footer {
		border-top: 1px solid rgba(0, 0, 0, 0.1);

		.learn-more {
			flex: 0 0 auto;
			display: inline-block;
			min-width: 35px;
			padding-top: 2px;
			@include add-size($font-size-base14, $size);
		}
	}

	.attribution-container {
		flex: 1;
		padding-top: 2px;
		max-width: calc(100% - 38px);

		.attribution-items {
			display: flex;
			align-items: center;
			gap: 8px;
			flex-wrap: wrap;

			.pdfLink,
			.spanLink {
				margin: 0;
				display: flex;
				align-items: center;
				@include add-size($font-size-base14, $size);
				vertical-align: top;

				.fileIcon {
					margin-right: 4px;
				}
			}

			span {
				color: #646479;
			}

			a,
			span,
			.spanLink {
				min-width: var(--font20);
				font-weight: 400;
				cursor: pointer;
			}

			a,
			.spanLink {
				@extend %attribution-item;
				background: none;
				color: #646479;
			}

			.fileName {
				background: none;
			}
		}
	}

	.whitespace-annex-box {
		max-width: 400px;
		margin-bottom: 4px;
		text-align: justify;
		text-align-last: left;
	}

	.textBorder {
		border-top: 1px solid #ffffff;
		padding-top: 4px;
	}

	.attribution-items-file {
		background-color: rgba(53, 94, 255, 0.1);
		border-radius: 8px;
		cursor: pointer;
	}
}

.text-message-reply {
	// background: #e0eaff !important;
	background: #fff !important;
	padding: 0 !important;
	overflow: hidden;

	> div {
		width: 100%;
		padding: 4px 16px 8px;
		// padding-top: 1.5rem;
		background: #fff !important;
		box-sizing: border-box;
		text-align: justify;
	}

	> .btns {
		margin-top: 0 !important;
		border-radius: 1.5rem 1.5rem 1.5rem 0;
	}

	.question {
		// background: none !important;
		// border: none !important;
		padding: 0 !important;
	}

	.mz {
		position: absolute;
		top: 0;
		right: 0;
		height: 16px;
		padding: 12px 4px !important;
		background: rgba(64, 133, 244, 0.05);
		border-radius: 0px 12px 0px 8px;
		display: flex;
		align-items: center;
		justify-content: center;
		width: auto;
	}
}

.text-message-request {
	background-color: transparent !important;
	border: none !important;

	> div {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #383d47;
	}
}

.maxHeight260 {
	max-height: 260px;
	overflow: auto;
}

.isImgCss {
	color: #181b49 !important;
	background: rgba(53, 94, 255, 0.06) !important;

	img {
		margin-bottom: 10px;
		border-radius: 8px;
		background: #d8d8d8;
		border: 2px solid #ffffff;
	}
}

// .recommend-container {
// 	position: relative;
// }

.recommend-popup {
	// background-color: #f0f0f0;
	// padding: 10px;
	// border: 1px solid #ccc;
	@include add-size(12px, $size);
}

.recommend-hot {
	display: flex;
	float: right;
	@include add-size($font-size-base14, $size);
	align-items: center;

	svg {
		width: 16px;
		height: 16px;
		margin-right: 3px;
	}
}

.recommend-title {
	display: block;
}

.recommend-title-pointer {
	cursor: pointer;
}

.recommend-item {
	border-radius: 6px;

	padding: 1px 6px 1px 6px;
	margin: 0 4px;
	color: rgb(var(--primary-5));
	background-color: transparent;
	border-color: rgb(var(--primary-5));
}

.markdown-body {
	@include add-size($font-size-base16, $size);

	.loading-icon {
		height: 28px;
		width: 108px;
		position: relative;
		margin-right: 10px;

		.icon1 {
			position: absolute;
			left: 0;
			top: 63%;
			transform: translateY(-50%);
			width: 4px;
			height: 4px;
			border-radius: 50%;
			background: rgba(32, 101, 214, 0.2);
		}

		.icon2 {
			position: absolute;
			left: 10px;
			top: 8px;
			transform: translateX(-50%);
			width: 4px;
			height: 4px;
			border-radius: 50%;
			background: rgba(32, 101, 214, 0.5);
		}

		.icon3 {
			position: absolute;
			left: 16px;
			top: 63%;
			transform: translateY(-50%);
			width: 4px;
			height: 4px;
			border-radius: 50%;
			background: rgba(32, 101, 214, 0.2);
		}

		.icon4 {
			position: absolute;
			right: 0;
			top: 50%;
			transform: translateY(-50%);
		}

		.active {
			background: #fff;
		}
	}

	// .recommend-container:nth-child(n+5) .recommend-hot img { display: none; }
}

.icon-box {
	display: flex;
	align-items: center;
}

.yinyong-modal {
	width: 60% !important;
	margin: 40px 0 !important;
	padding: 24px 50px !important;

	.w-modal-title {
		@include add-size(20px, $size);
	}

	.w-modal-body {
		@include add-size($font-size-base16, $size);
		text-indent: 2rem;
	}
}

.border-right {
	border-radius: 16px 2px 16px 16px;
}

.border-left {
	border-radius: 2px 12px 12px 12px;
}

.link-btn {
	display: inline-block;
	border-radius: 8px;
	background: linear-gradient(to bottom, #afcbd3, #72acca);
	color: #56809e;
	padding: 4px 8px;
}
</style>
