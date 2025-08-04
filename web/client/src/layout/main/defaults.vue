<template>
  <w-layout class="layout-container">
    <w-scrollbar
      ref="layoutScrollbarRef"
      class="scrollbarWap"
      :class="{
        scrollbarWapMobile:
          isMobile &&
          !route.path.includes('/twoCitiesPlam-cq') &&
          !route.path.includes('/sz-cac') &&
          !route.path.includes('/mobileUniversalTemplate') &&
          !route.path.includes('/mobileDazhouTemplate'),
        scrollbarWapMobileLh:
          isMobile &&
          (route.path.includes('/knowledgeDetailLh') ||
            route.path.includes('/virtualTemplate') ||
            route.path.includes('/longhuaNewUI')),
        scrollbarLhChatH5: isMobile && route.path.includes('/longhuaChat'),
      }"
      :style="{
        overflow: isMobile || route.path.includes('/partyMassScreen') ? 'hidden' : 'auto',
      }"
      outer-class="scrollbarOut layout-backtop"
    >
      <headerSzcac v-if="isMobile && route.path.includes('/sz-cac')" />
      <headerLh v-if="isMobile && route.path.includes('/knowledgeDetailLh')" />
      <headerLh v-if="isMobile && route.path.includes('/virtualTemplate')" />
      <headerLh v-if="isMobile && route.path.includes('/longhuaNewUI')" />
      <headerSjj v-if="isMobile && route.path.includes('/knowledgeDetailSjj')" />
      <headerAssPc v-if="isMobile && route.path.includes('/assistantPc')" />
      <headerAssH5 v-if="isMobile && route.path.includes('/assistantMobile')" />
      <zhengxiaodongH5 v-if="isMobile && route.path.includes('/zhengxiaodong')" />
      <longhuaChatH5 v-if="isMobile && route.path.includes('/longhuaChat')" />
      <headerCqH5 v-if="isMobile && route.path.includes('/twoCitiesPlam-cq')" />
      <headerCqPolicyH5 v-if="isMobile && route.path.includes('/policyHelp-cq')" />
      <LayoutHeader v-if="isMobile && route.path.includes('/knowledgeDetails')" />
      <LayoutChatHeader
        v-if="
          isMobile &&
          !route.path.includes('/knowledgeDetails') &&
          !route.path.includes('/knowledgeDetailLh') &&
          !route.path.includes('/longhuaNewUI') &&
          !route.path.includes('/knowledgeDetailSjj') &&
          !route.path.includes('/virtualTemplate') &&
          !route.path.includes('/assistantPc') &&
          !route.path.includes('/assistantMobile') &&
          !route.path.includes('/twoCitiesPlam-cq') &&
          !route.path.includes('/policyHelp-cq') &&
          !route.path.includes('/sz-cac') &&
          !route.path.includes('/partyMassScreen') &&
          !route.path.includes('/basicTemplate') &&
          !route.path.includes('/mobileUniversalTemplate') &&
          !route.path.includes('/mobileDazhouTemplate') &&
          !route.path.includes('/dazhouTemplate') &&
          !route.path.includes('/zhengxiaodong') &&
          !route.path.includes('/longhuaChat') &&
          !route.path.includes('/videoLargeScreen')
        "
      />
      <LayoutMain ref="layoutMainRef" />
    </w-scrollbar>
  </w-layout>
</template>

<script setup lang="ts" name="layoutDefaults">
import { defineAsyncComponent, watch, onMounted, nextTick, ref } from "vue";
import { useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import { useThemeConfig } from "/@/stores/themeConfig";
import { NextLoading } from "/@/utils/loading";
import { useBasicLayout } from "/@/hooks/useBasicLayout";

// 引入组件
const LayoutHeader = defineAsyncComponent(() => import("/@/layout/component/header.vue"));
const headerLh = defineAsyncComponent(() => import("/@/layout/component/headerLh.vue"));
const headerSjj = defineAsyncComponent(() => import("/@/layout/component/headerSjj.vue"));
const LayoutChatHeader = defineAsyncComponent(
  () => import("/@/layout/component/chatHeader.vue")
);
const headerAssH5 = defineAsyncComponent(
  () => import("/@/layout/component/headerAssH5.vue")
);
const zhengxiaodongH5 = defineAsyncComponent(
  () => import("/@/layout/component/zhengxiaodongH5.vue")
);
const longhuaChatH5 = defineAsyncComponent(
  () => import("/@/layout/component/longhuaChatH5.vue")
);
const headerAssPc = defineAsyncComponent(
  () => import("/@/layout/component/headerAssPc.vue")
);
const headerCqH5 = defineAsyncComponent(
  () => import("/@/layout/component/headerCqH5.vue")
);
const headerCqPolicyH5 = defineAsyncComponent(
  () => import("/@/layout/component/headerCqPolicyH5.vue")
);
const LayoutMain = defineAsyncComponent(() => import("/@/layout/component/main.vue"));
const headerSzcac = defineAsyncComponent(
  () => import("/@/layout/component/headerSzcac.vue")
);

// 定义变量内容
const layoutScrollbarRef = ref<RefType>("");
const layoutMainRef = ref<InstanceType<typeof LayoutMain>>();
const route = useRoute();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
// 移动端自适应相关
const { isMobile } = useBasicLayout();
// 重置滚动条高度
const updateScrollbar = () => {
  // 更新父级 scrollbar
  //layoutScrollbarRef.value.handleResize();
  // 更新子级 scrollbar
  //layoutMainRef.value!.layoutMainScrollbarRef.handleResize();
};
// 重置滚动条高度，由于组件是异步引入的
const initScrollBarHeight = () => {
  nextTick(() => {
    setTimeout(() => {
      updateScrollbar();
    }, 500);
  });
};
// 页面加载时
onMounted(() => {
  initScrollBarHeight();
  NextLoading.done(600);
});
// 监听路由的变化，切换界面时，滚动条置顶
watch(
  () => route.path,
  () => {
    initScrollBarHeight();
  }
);
// 监听 themeConfig 配置文件的变化，更新菜单 w-scrollbar 的高度
watch(
  themeConfig,
  () => {
    updateScrollbar();
  },
  {
    deep: true,
  }
);
</script>
<style scoped>
.w-layout {
  flex-direction: row;
  flex-basis: auto;
  box-sizing: border-box;
  min-width: 0;
}

.scrollbarOut {
  width: 100%;
  height: 100%;
}

:deep(.scrollbarWap) {
  height: 100%;
  overflow: hidden;
}

:deep(.scrollbarWapMobile) {
  background: url(../../assets/chatImages/indexBg.png) no-repeat;
  background-size: cover;
}

:deep(.scrollbarWapMobileLh) {
  background: url(../../assets/zc/bg-m.png) no-repeat;
  background-size: cover;
}

:deep(.scrollbarLhChatH5) {
  background: url(../../assets/chatImages/longhuaBg.png) no-repeat;
  background-size: cover;
}
/* :deep(.w-scrollbar-track-direction-horizontal){
	display: none;
} */
</style>
