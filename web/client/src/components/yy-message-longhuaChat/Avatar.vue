<script lang="ts" setup>
import defaultAvatar from '/@/assets/zc/user-new.png';
import BotAvatar from '/@/assets/zc/jqr-new.png';
import { useRoute } from 'vue-router';
const route = useRoute();
const getAppInfo = ()=>{
    let appInfo = localStorage.getItem(`${route.params.appId}`)?JSON.parse(localStorage.getItem(`${route.params.appId}`)):'';
    return appInfo
}
const props = defineProps({
  image: {
    type: Boolean,
    default: true,
  },
});
</script>

<template>
  <!-- <canvas  ref="apngImg" ></canvas> -->
  <!-- <canvas  ref="apngImg" width="300" height="300" ></canvas> -->
  <template v-if="image">
    <w-avatar :size="28"><img :src="getAppInfo()?.userIcon || defaultAvatar" /></w-avatar>
  </template>
  <template v-else>
    <w-avatar :size="28" style="margin-top: 0"><img :src="getAppInfo()?.robotIcon || BotAvatar" /></w-avatar>
  </template>
</template>

<style lang="scss" scoped>
.w-avatar {
  width: 100%;
  height: 100%;
  background-color: transparent;
  box-shadow: 0 0 13px rgba($color: #000000, $alpha: 0.1);
  // margin-top: 16px;
}

.bot-img {
  width: 100%;
  height: 100%;
  background-color: transparent;
  position: relative;
}

.bot-img canvas,
.bot-img img {
  position: absolute;
  width: 300px;
  height: 300px;
  max-width: 300px;
  zoom: 0.26;
  left: -44px;
  top: -44px;
}
</style>
