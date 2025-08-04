<template>
	<div class="container_file">
		<div class="tabs">
			<div class="tabsItem" v-for="(item, index) in routerArr" :key="index" :class="{ active: currentTab == item.cpn }" @click="changeTab(item)">
				<span class="icon"><SvgIcon :name="`cool-${item.icon}`" :size="16" /></span>
				<span class="text">{{ item.name }}</span>
			</div>
		</div>
		<div class="file">
			<component :is="currentTab" />
		</div>
	</div>
</template>
<script lang="ts" setup>
// @ts-ignore
import Intrduce from './components/intrduce.md';
// @ts-ignore
import Guide from './components/guide.md';
import Questions from './components/questions.md';
import Frequently from './components/frequently.md';
import DeploymentMethod from './components/deploymentMethod.md';
import { useRoute } from 'vue-router';
import { ref, reactive, shallowRef } from 'vue';
import 'github-markdown-css';

const currentTab = shallowRef(Intrduce);

const changeTab = (item) => {
	document.querySelector('.file').scrollTop = 0;
	currentTab.value = item.cpn;
};
const routerArr = reactive([
	{
		name: '雅意介绍',
		cpn: shallowRef(Intrduce),
		icon: 'Jiesao',
	},
	{
		name: '提示语指南',
		cpn: shallowRef(Guide),
		icon: 'Xinshou',
	},
	{
		name: '典型示例',
		cpn: shallowRef(Questions),
		icon: 'Dianxingyongli',
	},
	{
		name: '常见问题',
		cpn: shallowRef(Frequently),
		icon: 'Wenti',
	},
	{
		name: '关于我们',
		cpn: shallowRef(DeploymentMethod),
		icon: 'InformationLine',
	},
]);
</script>
<style lang="scss" scoped>
.container_file {
	width: 1200px;
	height: 100%;
	margin: auto;
	overflow-x: auto;
	display: flex;
	.tabs {
		width: 240px;
		margin-top: 24px;
		.tabsItem {
			height: 56px;
			line-height: 56px;
			text-align: center;
			color: #181b49;
			text-align: left;
			padding-left: 30px;
			cursor: pointer;
			.icon {
				margin-right: 18px;
				vertical-align: middle;
			}
		}
		.active {
			background: rgba(53, 94, 255, 0.06);
			border-right: 3px solid #355eff;
			color: #355eff;
		}
	}
	.file {
		width: 100%;
		min-width: 375px;
		margin: 0 auto;
		padding: 24px 280px 24px 84px;
		background: #ffffff;
		box-shadow: 0px 10px 20px 0px rgba(30, 66, 175, 0.06);
		height: 100%;
		overflow: auto;
		.markdown-body {
			max-width: 880px;
			background-color: #fff !important;
			color: #24292f !important;
			:deep(pre) {
				background-color: var(--color-canvas-subtle);
				code {
					color: #fff;
				}
			}
			:deep(code) {
				color: #24292f;
			}
		}
	}

	@media screen and (max-width: 1200px) {
		.file {
			padding: 40px 60px;
		}
	}
}
</style>
