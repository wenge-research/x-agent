<template>
  <div :class="isMobile ? 'chatModule-mobile' : 'chatModule'" @click="stop" ref="$el">
    <!-- 全类型 -->
    <chatInputAssH5Gaoqingqu v-if="assistantType == 'gaoxinqu'"></chatInputAssH5Gaoqingqu>
    <!-- 政策/活动/时间 -->
    <chatInputAssH5 v-else></chatInputAssH5>
  </div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, defineExpose, ref, onMounted, onUnmounted, computed } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
const { isMobile } = useBasicLayout();
const $el = ref(null);
const $height = ref(400);
const observer = new ResizeObserver((entries) => {
  $height.value = Math.max.apply(
    null,
    entries.map((entry) => entry.contentRect.height)
  );
});
const curRouteId = ref('');
onMounted(() => {
  observer.observe($el.value);
  curRouteId.value = sessionStorage.getItem('curRouteId') as string;

  updateWindowHeight();
  window.addEventListener('resize', updateWindowHeight);
});
onUnmounted(() => {
  observer.disconnect();
});
defineExpose({
  $height,
});
// 判断聊天应用
const assistantType = computed(() => {
	let type = "";
	const url = window.location.href;
	if(url?.includes('/hiPractice')) {
		type = 'hiPractice'
	} else if(url.includes('/hiActivity')) {
		type = 'hiActivity'
	} else if(url.includes('/policyHelp')) {
		type = 'policyHelp'
	} else {
		type="gaoxinqu"
	}
	return type;
});
const chatInputAssH5 = defineAsyncComponent(() => import('./components/chatInput-cqH5.vue'));
const chatInputAssH5Gaoqingqu = defineAsyncComponent(() => import('./components/chatInput-cqH5Gaoqingqu.vue'));
const stop = (e) => {
  e.stopPropagation();
};
const updateWindowHeight = () => {
  let html = document.querySelector(':root') as any;
  if (html && isMobile.value) {
    let userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    let browserList = ['MQQBrowser', 'Opera', 'Edg', 'Firefox', 'Chrome', 'IEMobile', 'wOSBrowser', 'BlackBerry', 'BrowserNG', 'WebOS'];
    if (userAgent.indexOf('iPhone') > -1 && browserList.every((item) => userAgent.indexOf(item) == -1)) {
      //判断是否Safari浏览器
      if (userAgent.indexOf('Safari') > -1) {
        html.style.setProperty('--i-bottom', `108px`);
      } else if (userAgent.indexOf('MicroMessenger') > -1 && userAgent.indexOf('wxwork') == -1) {
        html.style.setProperty('--i-bottom', `20px`);
      } else if (userAgent.indexOf('MicroMessenger') > -1 && userAgent.indexOf('wxwork') > -1) {
        html.style.setProperty('--i-bottom', `58px`);
      } else {
        html.style.setProperty('--i-bottom', `60px`);
      }
    } else {
      if (userAgent.indexOf('iPhone') > -1) {
        html.style.setProperty('--i-bottom', `50px`);
      } else {
        html.style.setProperty('--i-bottom', `70px`);
      }

    }
  }
};
</script>

<style scoped lang="scss">
.chatModule {
  position: absolute;
  // height: 128px;
  // width: calc(100% - 20px);
  width: 100%; // bottom: 16px;
  box-sizing: border-box;
  z-index: 100;
  left: 50%;
  transform: translateX(-50%);
  bottom: 0
}

.chatModule-mobile {
  position: absolute;
  height: 74px;
  width: 100vw;
  // width: calc(100% - 20px);
  left: 0;
  bottom: 20px;
  left: 0;
  box-sizing: border-box;
  z-index: 100;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  // left: 50%;
  // transform: translateX(-50%);
}
</style>
