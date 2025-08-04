<template>
	<w-layout-header class="top-header">
		<img
        v-if="logoUrl()"
        class="lh-logo"
        title="xxxxxxx政府在线"
        @click="goToHome('https://www.szlhq.gov.cn/')"
        :src="logoUrl()"
        alt="xxxxxxx政府在线"
		/>
		<img @click="changeFontSize" style="cursor: pointer; height: 20px;margin-left: auto" title="切换字体大小" src="/src/assets/zc/zt.png" alt="切换字体大小" />
	</w-layout-header>
</template>

<script setup lang="ts" name="layoutHeader">
import { ref } from 'vue';
import { useRoute } from 'vue-router';
const curStatus = ref(false);
const route = useRoute();
const logoUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.logo : '';
};
const goToHome = (url) => {
	window.open(url, '_blank');
};
const changeFontSize = () => {
	curStatus.value = !curStatus.value;
	window.document.documentElement.setAttribute('data-size', curStatus.value ? 2 : 1);
};
</script>

<style scoped lang="scss">
.top-header {
	height: 64px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-left: 32px;
	padding-right: 42px;
	.lh-logo {
		height: 36px;
	}
}
</style>
