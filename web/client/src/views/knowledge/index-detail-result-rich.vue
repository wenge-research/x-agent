<template>
  <div class="index-detail-result-rich">
    <div class="resources">搜索到 {{ totalPage }} 条相关资源</div>
    <div class="list" v-for="(item,index) in chatStore.progressList" :key="index">
      <div class="list" v-if="item.progress=='联网检索'">
         <div class="list-box" v-for="(itm,inx) in item.resultList" :key="inx">
               <div class="title" v-html="highlightText(itm.title, props.question)" @click="handelOpen(itm.url)"></div>
                  <div class="info-text" v-html="highlightText(itm.content, props.question)">
                  </div>
                  <div class="info-footer"> 
                    <span class="item">{{itm.pubtime}}</span>
                    <span class="item">来源：{{itm.url}}</span>
                  </div>
          </div> 
      </div>
      
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, onMounted} from "vue";
import { useChatStore } from '/@/stores/chat'
interface Props {
  question: string; // 新增
}
const props = defineProps<Props>();
const chatStore = useChatStore();
const totalPage = ref(0);
const cachedProgressList = localStorage.getItem('progressList');
if (cachedProgressList) {
  chatStore.progressList = JSON.parse(cachedProgressList);
}
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
// 点击标题打开链接的方法
const handelOpen = (url: string) => {
  if (url) {
    window.open(url, '_blank');
  }
};
watch(
	() => chatStore.progressList,
	(newProgressList) => {
    let totalCount = 0;
    newProgressList.forEach(item => {
      // 检查 item.resultList 是否存在
      if (item.progress === '联网检索' && Array.isArray(item.resultList)) {
        totalCount += item.resultList.length;
      }
    });
    totalPage.value = totalCount;
    localStorage.setItem('progressList', JSON.stringify(newProgressList));
     localStorage.setItem('totalCount', JSON.stringify(totalCount));
  },
  { deep: true }
);
onMounted(() => {
  console.log('chatStore', chatStore);
   const cachedTotalCount = localStorage.getItem('totalCount');
  if (cachedTotalCount) {
    totalPage.value = JSON.parse(cachedTotalCount);
  }
});

</script>

<style lang="scss" scoped>
.index-detail-result-rich {
  .resources {
    height: 32px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 14px;
    color: #828894;
    line-height: 32px;
    text-align: left;
    font-style: normal;
    margin: 16px 0;
    border-bottom: 1px solid #E7E7E7;
  }
  .list:last-child  {
    // border-top: 1px solid #E7E7E7;
    // border-bottom: 1px solid #E7E7E7;
    height: 628px; 
    overflow-y: auto; 
    .title {
        height: 24px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 16px;
        color: #383D47;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        margin-top: 16px;
        text-decoration: none; 
        cursor: pointer;
    }
    .title:hover {
        text-decoration: underline;
    }
    .list-box {
      // border-top: 1px solid #E7E7E7;
      border-bottom: 1px solid #E7E7E7;
    }
    .info-text {
        height: 40px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #383D47;
        line-height: 20px;
        text-align: left;
        font-style: normal;
        margin-top: 12px;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2; /* 显示的行数 */
        overflow: hidden; /* 超出部分隐藏 */
        text-overflow: ellipsis; 
    }
    // .info-text span {
    //     height: 40px;
    //     font-family: MiSans, MiSans;
    //     font-weight: 400;
    //     font-size: 12px;
    //     color: #383D47;
    //     line-height: 20px;
    //     text-align: left;
    //     font-style: normal;
    //     margin-top: 12px;
    //     display: -webkit-box;
    //     -webkit-box-orient: vertical;
    //     -webkit-line-clamp: 2; /* 显示的行数 */
    //     overflow: hidden; /* 超出部分隐藏 */
    //     text-overflow: ellipsis; 
    // }
    .info-footer {
      margin: 16px 0;
      height: 16px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #86909C;
      line-height: 16px;
      text-align: left;
      font-style: normal;
      .item {
        margin-right: 24px;
      }
    }
    .highlight {
      color: blue;
    }
  }
}
</style>