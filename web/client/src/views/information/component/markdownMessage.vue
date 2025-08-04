<template>
  <div>
    <div class="markdown-body" v-html="text" />
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue';
import MarkdownIt from 'markdown-it';
import mila from 'markdown-it-link-attributes';
import hljs from 'highlight.js';
const curText = ref('');
interface Props {
  text?: string;
  asRawText?: boolean;
}

const props = defineProps<Props>();

const isMarkdownImg = ref([]);
// 定义一个函数，接受一个对象作为参数，返回一个字符串
const textToMarkdown = (text: string, citations: any) => {
  // 判断json格式
  try {
    const jsonObj = JSON.parse(text); // 将字符串解析为一个对象
    if(typeof jsonObj === 'object' && jsonObj !== null){
      const jsonFormatted = JSON.stringify(jsonObj, null, 2);
		  text = '```json\n' + jsonFormatted + '\n```';
    }
  } catch (error) { }

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
    let strsss = props.text ? props.text.replace(/\\n/g, '\n'): '';
    // let startText = ['&e', '&en', '&ens', '&ensp', , '</', '</b', '</br', '<b', '<br', '<h', '<hr', '<hr '];
    // let endText = ['&ensp;', '>', '查询或咨询。'];
    // startText.forEach((i) => {
    //   if (strsss.endsWith(i)) {
    //     curText.value = strsss?.slice(0, strsss.lastIndexOf(i));
    //   }
    // });
    // endText.forEach((i) => {
    //   if (strsss.endsWith(i)) {
    //     curText.value = '';
    //   }
    // });
    // const value = strsss
    // 	? textToMarkdown(`[<span class='link-btn'>测试链接跳转&nbsp;&nbsp;&gt;</span>](http://coolbox.localhost/#/detail/12888)`, props.citations)
    // 	: '';
    const value = strsss ? textToMarkdown(curText.value || strsss, props.citations) : '';
    let htmlStr = mdi.render(value);

    return htmlStr;
  }
  return value;
});
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
mdi.use(mila, { attrs: { target: '_blank', rel: 'noopener' } });
</script>

<style lang="scss" scoped>
.markdown-body {
  height: 100%;
  overflow-y: auto;
}
</style>