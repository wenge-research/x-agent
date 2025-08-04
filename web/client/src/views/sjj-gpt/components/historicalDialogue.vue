<template>
  <div v-if="dialogueList.length">
    <div v-for="(item, index) in dialogueList" :key="index" class="dialogueList" @click="chatClick(item)">
      <div class="time flex">
        <div>{{ item.createTime }}</div>
        <img class="chatImg" :src="chatLine"  @click.stop="recordDelete(item)"/>
      </div>
      <div class="name">{{ item.question }}</div>
    </div>
  </div>
  <w-empty v-else>
		<template #image>
			<img class="nodata" :src="noDataImg" alt="" />
		</template>
		<div class="noName">暂无数据</div>
	</w-empty>
</template>

<script lang="ts" setup>
import {
  defineAsyncComponent,
  ref,
  onMounted,
  computed,
  onUnmounted,
  watch,
  nextTick,
} from "vue";
import { useChatStore } from "/@/stores/chat";
import { useKnowledgeState } from "/@/stores/knowledge";
import mittBus from "/@/utils/mitt";
import { useRoute } from "vue-router";
import { useBasicLayout } from "/@/hooks/useBasicLayout";

import { ElMessage } from "element-plus";
import chatBG from "/@/assets/img/chatBG.png";
import chatLine from "/@/assets/ai/delete-bin-4-line.svg";
import { recordGetRecord, recordLogicDelete } from "/@/api/chat";
import noDataImg from '/@/assets/chat/nocategory.svg';
const emit = defineEmits(['closeDialog']);
interface Props {
  historyDialog?: Boolean,
}
const props = defineProps<Props>();
// 移动端自适应相关
const { isMobile } = useBasicLayout();
const route = useRoute();
// const router = useRouter();
const chatStore = useChatStore();
const dialogueList = ref([]);
onMounted(() => {
  recordGetRecordList();
});
const recordGetRecordList = async () => {
  let res = await recordGetRecord({
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
    pageNo: 1,
    pageSize: 999,
    verifyDeptId: "",
    verifyStatus: "",
    text: "",
    conversationId: "",
    deleted: 0
  });
  dialogueList.value = Object.values(
    res.data.list.reduce((acc, current) => {
      if (
        !acc[current.conversationId] ||
        new Date(acc[current.conversationId].createTime).getTime() >
          new Date(current.createTime).getTime()
      ) {
        acc[current.conversationId] = current;
      }
      return acc;
    }, {})
  );
  dialogueList.value.sort((a, b) => Number(b.conversationId) - Number(a.conversationId));
};
const recordDelete = async (item: any) => {
  let res = await recordLogicDelete({
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
    conversationId: item.conversationId
  })
  if (res.code == '000000') {
    setTimeout(() => {
      recordGetRecordList();
    }, 1010)
  }
}
const chatClick = (item: any) => {
  emit('closeDialog', false);
  chatStore.getChatRecordsList(item.conversationId);
  
}
watch(
	() => props.historyDialog,
	(val) => {
		if (val) {
			recordGetRecordList();
		}
	}
)
// 页面销毁时，关闭监听
onUnmounted(() => {});
</script>

<style scoped lang="scss">
@import "/@/theme/mixins/index.scss";
.dialogueList {
  background: rgba(255, 255, 255, 0.6);
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  .time {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #b4bccc;
    margin-bottom: 8px;
    .chatImg {
      display: none;
      width: 15px;
    }
  }
  .name {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #383d47;
    line-height: 24px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    transition: max-height 0.3s ease-in-out; /* 平滑过渡效果 */
  }
}
.dialogueList:hover {
  background: #fff;
  .time {
    .chatImg {
      display: block;
      width: 15px;
      cursor: pointer;
    }
  }
}

.flex {
  display: flex;
  justify-content: space-between;
}

.nodata {
	margin: 200px auto 0;
	height: 200px;
}

.noName {
  font-size: 18px;
}
</style>
