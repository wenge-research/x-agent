<script lang="ts" setup>
import { computed, ref, onMounted, onUnmounted, nextTick, h, watch } from 'vue';
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
const route = useRoute();
import mittBus from '/@/utils/mitt';
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
	syList?: any;
	matterGuide?: any;
	syFlag?: boolean;
	finishReason?: any;
	syAnswerFlag?: any;
	syText?: any;
}

const props = defineProps<Props>();
const emits = defineEmits(['update:isHighlightCode']);
const chatStore = useChatStore();
const knowledgeState = useKnowledgeState();
const { isMobile } = useBasicLayout();
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

mdi.use(mdKatex, {
	blockClass: 'katexmath-block rounded-md p-[10px]',
	errorColor: ' #cc0000',
});
const recommendKey = [
	{ name: '热点摘要', key: '内容进行概述', type: 1, param: {} },
	{ name: '观点分析', key: '各方观点', type: 2, param: { get_news_num: 1 } },
];
const workDataList = ref([]);
const personalDataList = ref([]);
const personalDataObject = ref({
	education: '学历',
	name: '姓名',
	phone: '电话',
});
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
		isMobile.value ? '' : 'px-4 py-2',
		!isMobile.value ? (props.inversion ? 'bg-[#e0eaff]' : 'bg-[#e0eaff]') : '',
		props.inversion ? (isMobile.value ? ' ' : ' ') : isMobile.value ? ' ' : 'py-4 ',
		!isMobile.value ? (props.inversion ? 'dark:bg-[#a1dc95]' : 'dark:bg-[#1e1e20]') : '',
		!isMobile.value ? (props.inversion ? 'message-request' : 'message-reply') : '',
		!isMobile.value ? (props.inversion ? 'text-black' : 'text-wrap') : '',
		{ 'text-red-500': props.error },
		props.inversion ? (props.imgUrl ? 'isImgCss' : '') : props.imgUrl ? '' : '',
		!isMobile.value ? (props.inversion ? 'texts-message-requests' : 'text-message-reply') : '',
		!isMobile.value ? (props.inversion ? 'border-right' : 'border-left') : '',
		isMobile.value && props.inversion ? 'right-question' : 'left-answer',
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
		let strsss = props.text ? props.text.replace(/\\n/g, '\n').replace('#', '# ') : '';
		let startText = ['&e', '&en', '&ens', '&ensp', , '</', '</b', '</br', '<b', '<br', '<h', '<hr', '<hr '];
		let endText = ['&ensp;', '>', '查询或咨询。'];
		startText.forEach((i) => {
			if (strsss.endsWith(i)) {
				curText.value = strsss?.slice(0, strsss.lastIndexOf(i));
			}
		});
		endText.forEach((i) => {
			if (strsss.endsWith(i)) {
				curText.value = '';
			}
		});
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
	// if(!props.inversion){
	//   mittBus.on('dialogueId', (id) => {
	//     // setTimeout(() => {
	//     let guide = JSON.parse(sessionStorage.getItem('matterGuide'))
	//     if (Array.isArray(guide)) {
	//       workDataList.value = guide;

	//     } else {
	//       personalDataList.value = convertToNameTextArray(guide);
	//     }
	//     // }, 1000);
	//   });
	// }
});
function convertToNameTextArray(obj) {
	return Object.keys(obj).map((key) => ({ name: key, content: obj[key] }));
}
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

const signUpClick = (item) => {
	let param = {
		textContent: item == '确认无误' ? item : '报名' + item,
		params: props.params,
	};
	mittBus.emit('setsendMessage', param);
};
const syOpen = ref(true);
const switchSyHandler = () => {
	syOpen.value = !syOpen.value;
};
// 跳转溯源
const jumpToDetail = (data: any) => {
	if (data.fileLink) window.open(data.fileLink);
};
// 处理业务
const jumpToUrl = (data: any) => {
	// /twoCitiesPlam-cq/ 重庆大学城问答专用字段universityToken
	// const universityCityToken = sessionStorage.getItem('universityCityToken');
	// if (data.url) window.open(`${data.url}&token=${universityCityToken}`);
	if (data.url) {
		const id = data.url.split('id=') ? data.url.split('id=')[1] : '';
		window.parent.postMessage({ id }, '*');
	}
};
const geTailTalk = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.tailTalk : '';
};
</script>

<template>
	<div style="position: relative" :class="wrapClass">
		<img v-if="!props.inversion" src="/src/assets/chongqing/left-top1.svg" alt="" class="left-top" />
		<img v-else src="/src/assets/chongqing/right-top1.svg" alt="" class="right-top" />
		<div ref="textRef" class="leading-relaxed break-words inner" :class="maxHight">
			<!-- 溯源 -->
			<div v-if="syList?.length" class="traceability" :class="[syOpen ? '' : 'closeSy']">
				<div class="traceability-title" @click="switchSyHandler">
					基于{{ syList.length }}个来源
					<iconpark-icon v-show="syOpen" name="arrow-up-s-line" color="#828894"></iconpark-icon>
					<iconpark-icon v-show="!syOpen" name="arrow-down-s-line" color="#828894"></iconpark-icon>
				</div>
				<el-collapse-transition>
					<ul v-show="syOpen" class="traceability-list">
						<li class="traceability-list-item" v-for="(sy, index) in syList" :key="index" @click="jumpToDetail(sy)">
							<iconpark-icon name="link-m"></iconpark-icon>
							<span class="txt">{{ sy.fileName }}</span>
						</li>
					</ul>
				</el-collapse-transition>
			</div>
			<!-- 知识库 -->
			<div v-if="!inversion">
				<div
					v-if="!asRawText && text"
					class="markdown-body"
					:class="['markdown-body-as-img-' + isMarkdownImg.length, chatStore.isLightTheme ? 'hljs-atom-one-light' : 'hljs-atom-one-dark']"
					v-html="text"
				/>
				<!-- 重庆显示工作数据 -->
				<!-- <div class="work-data-list" v-if="workDataList.length">
          <div v-for="(item, index) in workDataList" :key="index" class="list">
            <div class="name">
              {{ item.name }}
            </div>
            <div class="content">
              {{ item.briefIntroduce }}
            </div>
            <div class="flex-just">
              <div class="job-details">岗位详情</div>
              <div>
                <w-button
                  style="
                    background-color: #0182e5;
                    color: #fff;
                    border-radius: 20px;
                    height: 40px;
                  "
                  @click="signUpClick(item.name)"
                >
                  报名
                </w-button>
              </div>
            </div>
          </div>
        </div> -->
				<!-- 重庆显示个人数据 -->
				<!-- <div class="personal-data" v-if="personalDataList.length">
          <div v-for="(item, index) in personalDataList" :key="index" class="flex">
            <div class="name">
              {{ personalDataObject[item.name] }}
            </div>
            <div class="content">
              {{ item.content }}
            </div>
          </div>
          <div class="flex-just" style="margin-top: 10px;">
            <div class="job-details">信息有误</div>
            <div>
              <w-button
                style="
                  background-color: #0182e5;
                  color: #fff;
                  border-radius: 20px;
                  height: 40px;
                "
                @click="signUpClick('确认无误')"
              >
                确认无误
              </w-button>
            </div>
          </div>
        </div> -->
				<div class="markdown-body icon-box" v-if="!asRawText && !text">
					<div class="loading-icon">
						<div :class="{ active: curNum == 1 }" class="icon1"></div>
						<div :class="{ active: curNum == 2 }" class="icon2"></div>
						<div :class="{ active: curNum == 3 }" class="icon3"></div>
					</div>
					<span> AI飞速思考中，请稍等...</span>
				</div>
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
			<div v-if="finishReason === 'ANSWER_COMPLETE' && matterGuide?.length" class="matterGuide">
				<ul class="matterGuide-list">
					<li v-for="item in matterGuide" :key="item.id" class="matterGuide-list-item">
						<img :src="item.surfaceDrawingFile" alt="" class="img" />
						<div class="name">{{ item.positionName }}</div>
						<div class="time">{{ item.workingHoursDescription }}</div>
						<div class="money">{{ item.salaryAndTreatment }}</div>
						<div class="btn" @click="jumpToUrl(item)">申请</div>
					</li>
				</ul>
			</div>
			<!-- <div v-if="finishReason === 'ANSWER_COMPLETE' && syFlag" class="traceability-tips" v-html="geTailTalk()"></div> -->
			<div v-if="syAnswerFlag" class="traceability-tips">
				<p style="padding: 6px 0; border-top: 1px solid rgba(0, 0, 0, 0.12); height: 1px; margin-top: 12px" />
				<p style="font-size: 14px;color: #646479;padding-bottom: 6px;">{{ syText }}</p>
			</div>
		</div>

		<slot name="footer" v-if="!asRawText && text"></slot>
		<slot name="question"></slot>
	</div>
</template>

<style lang="scss">
@import './style.scss';
@import '/@/theme/mixins/index.scss';
// 溯源
.traceability {
	margin-bottom: 8px;
	&-title {
		display: flex;
		align-items: center;
		font-family: MiSans, MiSans;
		font-weight: 500;
		font-size: 14px;
		color: #828894;
		line-height: 16px;
		margin-bottom: 8px;
		iconpark-icon {
			margin-left: 8px;
		}
	}
	&-list {
		padding: 14px 8px 2px;
		background: #f4f6f9;
		border-radius: 4px;
		&-item {
			display: flex;
			align-items: center;
			margin-bottom: 12px;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 14px;
			line-height: 28px;
			color: #383d47;
			width: calc(100vw - 142px);
			iconpark-icon {
				margin-right: 6px;
			}
			.txt {
				white-space: nowrap; /* 禁止换行 */
				overflow: hidden; /* 超出部分隐藏 */
				text-overflow: ellipsis; /* 显示省略号 */
			}
		}
	}
	&-tips {
		margin-top: 4px;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 14px;
		color: #828894;
		line-height: 20px;
	}
}
// 关闭溯源样式
.closeSy {
	.traceability-title {
		display: flex;
		align-content: center;
		justify-content: center;
		width: 115px;
		height: 36px;
		margin-bottom: 0;
		background: #f4f6f9;
		border-radius: 12px;
		margin-bottom: 0;
	}
}
// 业务
.matterGuide {
	background: #f2f6fa;
	border-radius: 8px;
	width: calc(100vw - 120px);
	overflow: hidden;
	&-list {
		display: flex;
		padding: 8px 0 0 8px;
		width: 100%;
		overflow-x: auto;
		&-item {
			min-width: 112px;
			white-space: nowrap;
			padding-right: 8px;
			margin-bottom: 8px;

			.img {
				height: 76px;
				border-radius: 4px;
			}
			.name {
				margin: 8px 0 4px;
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 14px;
				color: #383d47;
				line-height: 20px;
				white-space: nowrap; /* 禁止换行 */
				overflow: hidden; /* 超出部分隐藏 */
				text-overflow: ellipsis; /* 显示省略号 */
			}
			.time {
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 12px;
				color: #2e394f;
				line-height: 16px;
			}
			.money {
				margin: 4px 0 8px;
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 12px;
				color: #00b097;
				line-height: 18px;
			}
			.btn {
				width: 67px;
				height: 32px;
				line-height: 32px;
				text-align: center;
				background: #00a9e4;
				border-radius: 24px;
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 16px;
				color: #ffffff;
				text-align: center;
			}
		}
	}
}

.whitespace-pre-wrap {
	white-space: pre-wrap;
	color: var(--n-text-color);
	word-break: break-all;
}

.text-black {
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
		background: linear-gradient(180deg, rgba(53, 127, 255, 0.2) 0%, rgba(53, 127, 255, 0) 50px), rgba(255, 255, 255, 0.7);
		border-radius: 12px 12px 12px 0px;
		border: 1px solid;
		border-image: linear-gradient(180deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0)) 1 1;
		// backdrop-filter: blur(4px);
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

.texts-message-requests {
	// background-color: #1A6DD2 !important;
	// border: 1px solid #ffffff !important;
	background: linear-gradient(135deg, #01a8e5 0%, #0136e5 100%) !important;
	border-radius: 12px 12px 0px 12px !important;
	// backdrop-filter: blur(4px) !important;
	> div {
		color: #fff;
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
	font-family: MiSans, MiSans;
	font-weight: 400;
	font-size: 16px;
	color: #383d47;
	line-height: 24px;
	@include add-size($font-size-base16, $size);

	.loading-icon {
		height: 10px;
		width: 18px;
		position: relative;
		margin-right: 10px;

		.icon1 {
			position: absolute;
			left: 0;
			top: 50%;
			transform: translateY(-50%);
			width: 3px;
			height: 3px;
			border-radius: 50%;
			background: #3c82f3;
		}

		.icon2 {
			position: absolute;
			left: 50%;
			top: 0;
			transform: translateX(-50%);
			width: 4px;
			height: 4px;
			border-radius: 50%;
			background: #3c82f3;
		}

		.icon3 {
			position: absolute;
			right: 0;
			top: 50%;
			transform: translateY(-50%);
			width: 5px;
			height: 5px;
			border-radius: 50%;
			background: #3c82f3;
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

.work-data-list {
	.list {
		background: #f0f6fc;
		border-radius: 8px;
		padding: 8px 12px;
		margin-bottom: 8px;
	}
	.name {
		font-family: MiSans, MiSans;
		font-weight: 600;
		font-size: 16px;
		color: #383d47;
		line-height: 24px;
		margin: 3px 0 6px;
	}
	.content {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 14px;
		color: #383d47;
		line-height: 22px;
	}

	.job-details {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #0182e5;
		line-height: 24px;
	}
}

.personal-data {
	background: #f0f6fc;
	border-radius: 8px;
	padding: 8px 12px;
	.flex {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin: 10px 0;
	}
	.name {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #818999;
		line-height: 20px;
	}
	.content {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #383d47;
		line-height: 20px;
	}
	.job-details {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #0182e5;
		line-height: 24px;
	}
}

.flex-just {
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.right-question {
	background: url('/@/assets/chongqing/right-bottom.svg') no-repeat center bottom;
	background-size: 100% auto;
	padding: 0 9px 36px !important;
	position: relative;
	margin-bottom: 16px;
	flex: 1;
	.inner {
		background: #fff;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #2e394f;
		line-height: 24px;
		padding: 8px 8px;
		border-radius: 0 0 8px 8px;
		min-height: 48px;
	}
	.left-top {
		margin-bottom: -1px;
	}
	.right-top {
		margin-bottom: -1px;
	}
}
</style>
