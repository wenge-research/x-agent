<template>
	<div class="page-list">
		<div v-if="!isSearch" class="page-list-head" :class="[type == 1 ? 'h160' : 'h120']">
			<div class="page-list-head-back">
				<iconpark-icon name="arrow-left-wide-line" size="20" color="#fff" @click="comeBackHandler"></iconpark-icon>
				<iconpark-icon name="search-2-line" size="20" color="#fff" @click="switchSearchHandler(true)"></iconpark-icon>
			</div>
			<img v-if="type == 1" src="/src/assets/sz-cac/1.png" class="page-list-head-img" />
			<img v-if="type == 2" src="/src/assets/sz-cac/2.png" class="page-list-head-img" />
			<img v-if="type == 3" src="/src/assets/sz-cac/3.png" class="page-list-head-img" />
			<img v-if="type == 4" src="/src/assets/sz-cac/4.png" class="page-list-head-img" />
			<ul v-if="type == 1" class="page-list-head-tabs">
				<li v-for="item in list" :key="item.value" :class="[tabs == item.value ? 'selected' : '']" @click="tabsHandler(item.value)">
					{{ item.label }}
				</li>
			</ul>
		</div>
		<div v-else class="page-list-search">
			<div class="page-list-search-top">
				<iconpark-icon name="arrow-left-wide-line" size="20" color="#fff" @click="switchSearchHandler(false)"></iconpark-icon>
				搜索
			</div>
			<div class="page-list-search-bottom">
				<el-input v-model="searchValue" placeholder="输入名称搜索" @keydown.enter="searchHandler">
					<template v-slot:prefix>
						<iconpark-icon name="search-2-line" size="20" color="#2155C9"></iconpark-icon>
					</template>
				</el-input>
			</div>
		</div>
		<ul
			v-if="sourceList.length"
			class="page-list-content infinite-list"
			ref="PageList"
			v-infinite-scroll="loadHandler"
			style="overflow: auto"
			v-loading="listLoading"
		>
			<li v-for="item in sourceList" :key="item?.id" class="page-list-content-item infinite-list-item" @click="toPageDetails(item)">
				<template v-if="type == 4 && item.files?.length">
					<div v-for="(sonItem, sonIndex) in JSON.parse(item.files)" :key="sonIndex">
						<video controls width="auto" height="auto" :src="sonItem" style="margin-bottom: 12px" />
					</div>
				</template>
				
				<div class="title">{{ item?.title }}</div>
				<!-- <div class="time">{{ item?.pushTimeStr }}</div> -->
			</li>
		</ul>
		<div v-else class="no-data">暂无数据</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from "vue-router";
// api
import { apiGetLegalAndRegulatoryDataList } from '/@/api/chat/index';

const router = useRouter();
const route = useRoute();
// type: 1-网信法律法规库  2-网信案例库  3-网信动态  4-普法动画
const { type } = route.query as { type: string };
// 缓存主路径 方便返回
const { mainPath } = route.query as { mainPath: string };
const tabs = ref('fl');
// 名称检索
const searchValue = ref('');
const isSearch = ref(false);
const list = ref([
	{
		label: '法律',
		value: 'fl',
	},
	{
		label: '行政法规',
		value: 'xzfg',
	},
	{
		label: '部门规章',
		value: 'bmgz',
	},
	{
		label: '司法解释',
		value: 'sfjs',
	},
	{
		label: '规范性文件',
		value: 'gfxwj',
	},
	{
		label: '政策文件',
		value: 'zcwj',
	},
	{
		label: '政策解读',
		value: 'zcjd',
	},
]);
const sourceList = ref([]);
const currentList = ref([]);
const listLoading = ref(false);
// 分页
const pageData = ref({
	pageNo: 1,
	pageSize: 10,
});
const PageList = ref(null);
// 法律法规库切换类型
const tabsHandler = (tab: string) => {
	tabs.value = tab;
	pageData.value.pageNo = 1;
	sourceList.value = [];
	// 滚动到列表顶部
	if (PageList.value) {
		PageList.value.scrollTop = 0;
	}
	getLegalAndRegulatoryDataList();
};
// 检索状态
const switchSearchHandler = (show: boolean) => {
	isSearch.value = show;
  if(!isSearch.value) {
    searchValue.value = "";
    searchHandler();
  }
};
// 条件检索
const searchHandler = () => {
  pageData.value.pageNo = 1;
  sourceList.value = [];
  getLegalAndRegulatoryDataList();
}
// 列表
const getLegalAndRegulatoryDataList = async () => {
	const params = {
		...pageData.value,
		type: type,
		tab: type == 1 ? tabs.value : '',
    keyword: searchValue.value
	};
	listLoading.value = true;
	const res = await apiGetLegalAndRegulatoryDataList(params);
	if (res.code == '000000') {
		currentList.value = res.data?.list;
		sourceList.value = sourceList.value.concat(res.data?.list);
	}
	listLoading.value = false;
};
// 滚动分页
const loadHandler = () => {
  console.log("触发")
	if(currentList.value.length >= 10) {
		pageData.value.pageNo += 1;
		getLegalAndRegulatoryDataList();
	}
};
// 跳转详情页
const toPageDetails = (data: any) => {
	if(type == 4) return;
  router.push({
    path: '/szPreviewChat/details',
    query: {
      data: JSON.stringify(data)
    }
  });
}
// 返回上一页
const comeBackHandler = (data: any) => {
  router.push({
    path: mainPath,
  });
}

onMounted(() => {
	getLegalAndRegulatoryDataList();
});
</script>

<style lang="scss" scoped>
.page-list {
	width: 100vw;
	height: 100vh;
	display: flex;
	flex-direction: column;
	&-head {
		display: flex;
		flex-direction: column;
		width: 100%;
		background: url('/@/assets/sz-cac/headbg.png') no-repeat;
		background-size: 100% 100%;
		&-back {
			width: 100%;
			height: 40px;
			padding: 0 18px 0 24px;
			display: flex;
			align-items: center;
			justify-content: space-between;
		}
		&-img {
			margin: 10px auto 0;
			width: 168px;
		}
		&-tabs {
			display: flex;
			align-items: center;
			padding-left: 24px;
			margin-top: auto;
			width: 100%;
			height: 40px;
			overflow-x: auto;
			li {
				margin-right: 24px;
				height: 100%;
				white-space: nowrap;
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 16px;
				color: rgba(255, 255, 255, 0.8);
				text-align: left;
				font-style: normal;
			}
			.selected {
				// display: flex;
				// align-items: center;
				position: relative;
				font-size: 18px;
				color: #fff;
				&::after {
					content: '';
					position: absolute;
					bottom: 0;
					left: 0;
					width: 100%;
					height: 3px;
					background: #fff;
				}
			}
		}
	}
	.h160 {
		height: 160px;
	}
	.h120 {
		height: 120px;
	}
	&-search {
		height: 92px;
		&-top {
			position: relative;
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
		&-bottom {
			height: 48px;
			background: #fff;
			box-shadow: 0px 0px 4px 0px rgba(0, 0, 0, 0.1);
			.el-input {
				border: none !important;
			}
			::v-deep(.el-input__wrapper) {
				border-radius: 0 !important;
				background: transparent !important;
				border: none !important;
				box-shadow: none;
				padding: 1px 11px 1px 24px;
			}
			::v-deep(.el-input__inner) {
				height: 48px !important;
				font-size: 16px;
			}
		}
	}
	&-content {
		padding: 4px 8px 32px;
		background: #f3f5fa;
		flex: 1;
		&-item {
			margin-top: 8px;
			padding: 12px;
			background: #ffffff;
			border-radius: 4px;
			.title {
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 18px;
				color: #383d47;
				line-height: 26px;
			}
			.time {
				margin-top: 4px;
				height: 20px;
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 14px;
				color: #c6c6d2;
				line-height: 20px;
			}
		}
	}
	.no-data {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 18px;
		color: #383d47;
    background: #f3f5fa;
	}
}
</style>