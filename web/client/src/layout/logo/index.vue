<template>
	<div class="layout-logo" :class="isDocs ? 'layout-logo--bottom' : ''" v-if="setShowLogo" @click="onThemeConfigChange">
		<!-- <img :src="logoMini" class="layout-logo-medium-img" /> -->
		<div :class="isDocs ? 'layout-img--docs' : 'layout-img'">
			<CoolYayiLogoNew size="150" />
		</div>
		<template v-if="isDocs">
			<div class="layout-split"></div>
			<span class="layout-text">帮助中心</span>
		</template>
		<img v-else :src="test" alt="" class="test" />
		<!-- <span>{{ themeConfig.globalTitle }}</span> -->
	</div>
	<div class="layout-logo-size" v-else>
		<img :src="logoMini" class="layout-logo-size-img" />
	</div>
</template>

<script setup lang="ts" name="layoutLogo">
import { computed } from 'vue';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import logoMini from '/@/assets/logo-mini.svg';
import test from '/@/assets/login/test.png';
import router from '/@/router';

// 定义变量内容
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);

// 设置 logo 的显示。classic 经典布局默认显示 logo
const setShowLogo = computed(() => {
	let { isCollapse, layout } = themeConfig.value;
	return !isCollapse || layout === 'classic' || document.body.clientWidth < 1000;
});
const onThemeConfigChange = () => {
	router.push('/chat/21');
};
const isDocs = computed(() => {
	return router.currentRoute.value.fullPath === '/docs';
});
</script>

<style scoped lang="scss">
.layout-logo {
	width: 210px;
	height: 50px;
	margin-right: 45px;
	line-height: 50px;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: rgb(0 21 41 / 2%) 0px 1px 4px;
	color: var(--color-text-1);
	font-size: var(--font18);
	position: relative;
	cursor: pointer;
	// animation: logoAnimation 0.3s ease-in-out;
	&--bottom {
		width: 300px;
		height: 30px;
		align-items: flex-end;
	}
	span {
		white-space: nowrap;
		display: inline-block;
	}
	&-medium-img {
		width: 40px;
		margin-right: 5px;
	}
	.test {
		margin-left: 10px;
		width: 50px;
		height: 15px;
		margin-top: 25px;
		position: absolute;
		right: -30px;
	}
}

.layout-img,
.layout-img--docs {
	flex: 0 0 auto;
}

.layout-img {
	height: 145px;
}

.layout-img--docs {
	height: 94px;
}

.layout-split {
	flex: 0 0 1px;
	width: 1px;
	height: 24px;
	background: #d0d5dc;
	margin: 0 12px;
}

.layout-text {
	font-size: 18px;
	line-height: 1.2;
	font-weight: 400;
	color: #181b49;
}

.layout-logo-size {
	width: 100%;
	height: 50px;
	display: flex;
	cursor: pointer;
	animation: logoAnimation 0.3s ease-in-out;
	&-img {
		width: 20px;
		margin: auto;
	}
	&:hover {
		img {
			animation: logoAnimation 0.3s ease-in-out;
		}
	}
}
</style>
