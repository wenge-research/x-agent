<template>
	<div class="page-details">
		<div class="page-details-head">
			<div class="page-list-search-top">
				<iconpark-icon name="arrow-left-wide-line" size="20" color="#1DB9A2" @click="comeBackList"></iconpark-icon>
				详情
			</div>
		</div>
		<div class="page-details-content">
			<div class="page-details-content-title">{{ detailsData.title }}</div>
			<div class="page-details-content-row">
				<div>{{ source }}</div>
				<div class="pushTimeStr">{{ detailsData.pushTimeStr }}</div>
				<div class="view" @click="viewHandler"><iconpark-icon name="link-m" color="#2155C9"></iconpark-icon>查看原文</div>
			</div>
			<div class="page-details-content-article" v-html="content">
			</div>
			<div class="page-details-content-line"><span></span>完<span></span></div>
		</div>
		<div class="page-details-back">
			<div class="to-back" @click="comeBackList">返回列表</div>
			<div class="to-top" @click="toTopHandler"><iconpark-icon name="skip-up-line" size="20" color="#494C4F"></iconpark-icon></div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
const router = useRouter();
const route = useRoute();

const detailsData = route?.query?.data ? JSON.parse(route.query.data) : {};

const source = computed(() => (detailsData?.source ? detailsData.source.split('：')[1] : ''));
const content = computed(() => (detailsData?.content ? detailsData.content.replace(/\n/g, '<br /><p style="margin-bottom: 0px"></p>') : ''));
// 返回列表
const comeBackList = () => {
	router.back();
};

const viewHandler = () => {
	if (detailsData?.url) {
		window.open(detailsData.url);
	}
};

const PageRef = ref(null);
// 回到顶部
const toTopHandler = () => {
  // console.log("PageRef", PageRef)
	// if (PageRef.value) {
	// 	PageRef.value.scrollTop = 0;
	// }
  window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
};
</script>

<style lang="scss" scoped>
.page-details {
	width: 100vw;
	background: #f3f5fa;
	padding-bottom: 60px;
	&-head {
		position: sticky;
		top: 0;
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100%;
		height: 44px;
		background: #02236b;
		font-family: MiSans, MiSans;
		font-weight: 500;
		font-size: 18px;
		color: #ffffff;
		iconpark-icon {
			position: absolute;
			left: 24px;
		}
	}
	&-content {
		padding: 16px 12px 0;
		&-title {
			font-family: MiSans, MiSans;
			font-weight: 600;
			font-size: 20px;
			color: #181b49;
			line-height: 28px;
		}
		&-row {
			margin: 22px 0 22px;
			display: flex;
			align-items: center;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 14px;
			color: #818999;
			line-height: 20px;
			.pushTimeStr {
				margin-left: 16px;
			}
			.view {
				display: flex;
				align-items: center;
				margin-left: auto;
				color: #2155c9;
				iconpark-icon {
					margin-right: 6px;
				}
			}
		}
		&-article {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 16px;
			color: #2e394f;
			line-height: 28px;
		}
		&-line {
			margin-top: 30px;
			display: flex;
			justify-content: center;
			align-items: center;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 12px;
			color: #b4bccc;
			line-height: 14px;
			span {
				display: inline-block;
				height: 1px;
				width: 32px;
				background: rgba(0, 0, 0, 0.12);
				margin-right: 16px;
				margin-left: 16px;
			}
		}
	}
	&-back {
		margin-top: 24px;
		display: flex;
		justify-content: space-between;
		padding: 0 12px;
		.to-back {
			width: 295px;
			height: 40px;
			line-height: 40px;
			text-align: center;
			border-radius: 4px;
			border: 1px solid #2155c9;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 16px;
			color: #2155c9;
		}
		.to-top {
			display: flex;
			align-items: center;
			justify-content: center;
			width: 40px;
			height: 40px;
			border-radius: 4px;
			border: 1px solid #c4c6cc;
		}
	}
}
</style>