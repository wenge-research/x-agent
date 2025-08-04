<template>
	<div class="left-side">
		<div :class="{'my-app': true, 'my-app-active': currentAppId == 0}" @click="handleRoute(0)">
			<img src="/src/assets/chat/old/icon_0.png" alt="">
			推荐
		</div>
		<hr />
		<div :class="{'my-app': true, 'my-app-active': currentAppId == 21}" @click="handleRoute(21)">
			<img src="/src/assets/chat/icon-myapp.png" alt="">
			智能问答
		</div>
		<!-- <div :class="{ 'my-app': true, 'my-app-active': currentAppId == '数字人' }" @click="handleRoute('数字人')">
			<img src="/src/assets/chat/icon-myapp.png" alt="" />
			数字人
		</div> -->
		<hr />
		<!-- <p class="recommend-app">推荐应用</p> -->
		<w-input-search
			v-model="searchText"
			class="searchInput"
			placeholder="关键词"
			clearable
			@input="changeText"
			@clear="changeText('')"
		>
		</w-input-search>
		<w-collapse
			:active-key="[expendKey]"
			:show-expand-icon="true"
			:bordered="false"
			class="left-collapse"
			accordion
			expand-icon-position="left"
			@change="handleExpandChange"
		>
			<w-collapse-item v-for="(tree, index) of treeList" :header="tree.name" :key="tree.name" class="collapse-item">
				<template #expand-icon="{ active }">
					<!-- <img :src="`/src/assets/chat/icon_${index % 5}.png`" v-if="active" class="expand-icon" />
					<img :src="`/src/assets/chat/icon_${index % 5}.png`" v-else class="expand-icon" /> -->
					<img :src="getIcon(index)" class="expand-icon" />
				</template>
				<template #extra>
					<span class="collapse-item-extra">{{ tree.appList.length }}</span>
				</template>
				<template v-if="tree?.num > 0">
					<div v-for="app of tree.appList" :key="app.id"
						:class="{ 'collapse-item-app': true, 'active-nav': app.id === currentAppId }" @click="handleAppClick(app.id)">
						<CoolCheckboxBlankCircleFillWe
							size="6"
							:color="app.id === currentAppId ? '#355EFF' : '#9A99AA'"
						/>
						<span class="collapse-item-app-name">{{ app.name }}</span>
					</div>
				</template>
			</w-collapse-item>
		</w-collapse>
	</div>
</template>

<script lang="ts" name="navMenuVertical" setup>
import { computed, nextTick, onBeforeMount, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getAppList } from '/@/api/chat'
import { useChatStore } from '/@/stores/chat';
import { useRobotStore } from '/@/stores/robot';
import other from '/@/utils/other';
const basicTreeList = ref([])
const treeList = ref([])
const currentAppId = ref()
const clickedExpendKey = ref()
const state = reactive({
	treeListMap: {}
})
const route = useRoute()
const router = useRouter()
const chatStore = useChatStore();
const robotStore = useRobotStore();
const { appId } = route.params as { appId: string }
const searchText = ref('')

const filterTree = (val, tree, newArr = []) => {
  if (!(tree.length && val)) {  // 如果搜索关键字为空直接返回源数据
    return tree
  }
  for (let item of tree) {
    if (item.id && item.name.indexOf(val) > -1) { // 匹配到关键字的逻辑
      newArr.push(item)  // 如果匹配到就在数值中添加记录
      continue  // 匹配到了就退出循环了此时如果有子集也会一并带着
    }

    if (item.appList && item.appList.length) { // 如果父级节点没有匹配到就看看是否有子集，然后做递归
      let subArr = filterTree(val, item.appList) // 缓存递归后的子集数组
      if (subArr && subArr.length) {  // 如果子集数据有匹配到的节点
        let node = { ...item, appList: subArr }  // 关键逻辑，缓存父节点同时将递归后的子节点作为新值
        newArr.push(node)  // 添加进数组
      }
    }
  }
  return newArr
}

const changeText = val => {
	searchText.value = val
	treeList.value = filterTree(val,basicTreeList.value)
	chatStore.appTreeList = filterTree(val,basicTreeList.value)
}
onBeforeMount(async () => {
	try {
		const res = await getAppList()
		if (res?.code === 200 && res?.data) {
			basicTreeList.value = other.deepClone(res.data)
			treeList.value = res.data
			chatStore.appTreeList = res.data
			//console.log(filterTree('',res.data))
			createMapObj()
		}
	} catch (err) {
		throw new Error()
	}
})
if (!appId) {
	router.push({ name: 'chat', params: { appId:0 } })
}
const createMapObj = () => {
	const mapObj = {}
	for (const tree of treeList.value) {
		const name = tree.name
		const appIdList = tree.appList.map(app => app.id)
		mapObj[name] = appIdList
	}
	state.treeListMap = mapObj
}

const handleRoute = (appId: string | number) => {
	chatStore.dialogueLoading = false
	robotStore.breakChat()
	currentAppId.value = appId
	router.push({ name: 'chat', params: { appId } })
}

const handleAppClick = (appId: string | number) => {
	chatStore.setParamsDrawerVisible(false)
	chatStore.setUploadDrawerVisible(false)
	chatStore.dialogueLoading = false
	robotStore.breakChat()
	currentAppId.value = appId
	handleRoute(appId)
}

const handleExpandChange = (key: Array<string>) => {
	clickedExpendKey.value = key.toString() || 'all-closed'
}

const getIcon = (index: number) => {
	return new URL(`/src/assets/chat/icon_${index % 10}.png`, import.meta.url).href;
}
onMounted(() => {
	nextTick(() => {
		setTimeout(() => {
			// document.querySelector('.active-nav').scrollIntoView()
			// document.querySelector('.active-nav').scrollIntoView(false);
			let ele = document.querySelector('.active-nav')
			if (ele && !isInViewport(ele)) document.querySelector('.active-nav').scrollIntoView(false);
		}, 300);
		
	})
})
// 展开项的key值
let expendKey = computed(() => {
	let currentKey
	for (const key in state.treeListMap) {
		if (state.treeListMap[key].includes(currentAppId.value)) {
			currentKey = key
			break
		}
	}
	return clickedExpendKey.value || currentKey
})

const isInViewport = (element) => {
  var rect = element.getBoundingClientRect();
  return (
    rect.top >= 0 &&
    rect.left >= 0 &&
    rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
    rect.right <= (window.innerWidth || document.documentElement.clientWidth)
  );
}

watch(() => route.params.appId, (newVal: any) => {
	currentAppId.value = newVal
	clickedExpendKey.value = undefined
	nextTick(() => {
		setTimeout(() => {
			let ele = document.querySelector('.active-nav')
			if (ele && !isInViewport(ele)) document.querySelector('.active-nav').scrollIntoView(false);
		}, 300);
		
	})
}, { immediate: true, deep: true })

</script>
<style lang="scss" scoped>
.left-side {
	.searchInput{
		width: 200px;
		margin-top: 20px;
		margin-left: 25px;
		border-radius: 8px;
		background: transparent;
	}
	.my-app {
		display: flex;
		justify-content: flex-start;
		align-items: center;
		margin: 14px 20px;
		padding:  4px 0;
		font-size: 16px;
		font-weight: 400;
		color: #646479;
		line-height: 22px;
		cursor: pointer;
		&-active{
			background: rgba(53,94,255,0.05);
			border-radius: 8px;
			font-weight: bold;
			color: #355EFF;
		}
		>img {
			width: 44px;
			height: 44px;
			margin-right: 8px;
		}
	}

	.recommend-app {
		margin: 28px 0 20px 24px;
		font-size: 16px;
		font-weight: 400;
		color: #9A99AA;
		line-height: 22px;
	}

	hr {
		margin: 0 16px 0 24px;
	}

	.w-collapse {
		border-radius: 0;
	}

	:deep(.left-collapse) {
		margin-top: 20px;
		.expand-icon {
			width: 44px;
			height: 44px;
		}

		.w-collapse-item-active {
			.w-collapse-item-header-title {
				font-weight: bold;
				color: #181B49;
			}

			.w-collapse-item-header-extra {
				font-weight: bold;
				color: #181B49;
			}
		}

		.collapse-item {
			margin: 0 0 10px 0;
			border-bottom: none;
			user-select: none;

			.w-collapse-item-header {
				padding-top: 15px;
				padding-bottom: 15px;
				padding-left: 70px;
				font-weight: 400;
				color: #646479;
				line-height: 22px;
				border: none;
				background: none;

				.w-collapse-item-header-title {
					font-size: 16px;
				}
			}
			.w-collapse-item-header-extra{
				margin-right: 5px;
			}
			.w-collapse-item-icon-hover {
				left: 20px;
			}

			.w-collapse-item-content {
				margin: 0 16px;
				padding: 0;
				background: rgba(53, 94, 255, 0.04);
				border-radius: 8px;

				.w-collapse-item-content-box {
					padding: 10px 0;

					>div {
						&:hover{
							color: #355EFF;
						}
						display: flex;
						justify-content: flex-start;
						align-items: center;
						margin-bottom: 10px;
						font-size: 16px;
						font-weight: 400;
						color: #646479;
						line-height: 24px;
						cursor: pointer;
						padding: 4px 4px 4px 24px;
						&:last-child {
							margin-bottom: 0;
						}

						>img {
							width: 20px;
							margin-right: 15px;
						}
					}

					>div.active-nav {
						font-weight: bold;
						color: #355EFF;
					}
					.collapse-item-app-name {
						margin-left: 15px;
					}
				}
			}

			.w-icon-hover {
				&::before {
					display: none;
				}
			}
		}
	}
}
</style>
