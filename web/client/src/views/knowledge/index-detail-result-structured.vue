<template>
  <div class="index-detail-result-structured">
    <div v-if="isLoading" class="loading">加载中...</div>
	<div v-else>
		<div class="markdown-content" v-html="highlightText(compiledMarkdown, props.question)"></div>
	</div>
    
  </div>
</template>

<script lang="ts" setup>
import { ref , onMounted, defineProps } from 'vue'
import { getStructuredData } from "/@/api/knowledge";
interface Props {
  applicationId: string; // 新增
  question: string; // 新增
}
import { useChatStore } from '/@/stores/chat';
const chatStore = useChatStore();
const isLoading = ref(true);
const compiledMarkdown = ref('');
const props = defineProps<Props>();
const getStructuredDataFun = async () => {
  compiledMarkdown.value = ''
  isLoading.value = true;
  let res = await getStructuredData({
    applicationId: props.applicationId,
    question: chatStore.plainText,
  });
  compiledMarkdown.value = res.data.data;
  isLoading.value = false;
};
// 转义正则表达式特殊字符
const escapeRegExp = (string: string) => {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
};
// 定义高亮文本的函数
const highlightText = (text: string, keyword: string) => {
  // 检查 text 和 keyword 是否为空
  if (!text || !keyword) return text;
  const escapedKeyword = escapeRegExp(keyword);
  const regex = new RegExp(escapedKeyword, 'gi');
  return text.replace(regex, (match) => `<span style="color:red;">${match}</span>`);
};
// const dialogueInputLoading = computed(() => {
// 	return chatStore.dialogueLoading;
// });
onMounted(async () => {
	console.log('执行了这里',chatStore.dialogueLoading)
	if(!chatStore.dialogueLoading){
		 getStructuredDataFun();
	}
 
});
defineExpose({
    getStructuredDataFun
})
</script>

<style lang="scss" scoped>
.index-detail-result-structured {
  height: 528px;
  background: #FFFFFF;
  border-radius: 8px;
  margin-top: 20px;
  display: flex; // 使用 flex 布局
  flex-direction: column;
}
.loading {
  flex: 1; // 让加载提示占据剩余空间
  display: flex;
  justify-content: center; // 水平居中
  align-items: center; // 垂直居中;
}
:deep(.markdown-content) {
  flex: 1;
    display: flex;
    justify-content: center;
    padding: 16px;
     table {
      width: 100%;
      height: 100%; // 让表格高度填充 .markdown-content
      border-collapse: collapse;
    }
  table tbody {
    width: 100%;
     height: 100%;
    border-collapse: collapse;
    margin: 1em 0;
     display: table-row-group;
    border: 1px solid #E5E6EA; // 给表格添加较粗的外边框
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); // 添加阴影提升立体感
  }
  thead th {
    border: 1px solid #E5E6EA; // 给表头单元格添加边框
    padding: 12px 15px; // 增加内边距，让内容更舒展
    text-align: left; // 文字左对齐 
    background-color: #F7F8FA;
    font-weight: 600; // 表头文字加粗
    color: #1D2129; // 表头文字颜色
  }
  tbody th,
  tbody td {
    border: 1px solid #E5E6EA; // 给单元格添加边框
    padding: 12px 15px; // 增加内边距，让内容更舒展
    text-align: left; // 文字左对齐
    width: auto; 
    font-size: 14px;
  }
  tbody tr {
    height: auto; // 让行高自适应内容
  }
  // tbody th {
  //   background-color: #F7F8FA; // 表头背景色
  //   font-weight: 600; // 表头文字加粗
  //   color: #1D2129; // 表头文字颜色
  //   border: 1px solid #E5E6EA // 表头底部边框加粗
  // }

  // tbody tr:nth-child(even) {
  //   background-color: #f9f9f9; // 偶数行背景色
  // }

  tbody tr:hover {
    background-color: #f1f3f5; // 鼠标悬停时的背景色
    transition: background-color 0.3s ease; // 添加过渡效果
  }
}
</style>