<!-- eslint-disable vue/no-async-in-computed-properties -->
<template>
	<div class="centerInitItem" :class="{ centerInitItemMobile: isMobile }">
	

		<w-grid  v-if="listItem.length && (!storesUserInfo.offline || route.params.appId != 21)" :cols="24" :col-gap="16" :row-gap="16">
			<transition-group
			appear
			name="animate__animated animate__fadeIn"
			enter-active-class="animate__fadeIn"
			leave-active-class="animate__fadeIn"
			
			>
				<w-grid-item v-show="isShow" class="item"  v-for="(item, index) in listItem" :key="index" @click="sendMessage(item)" :span="{ xs: 24, sm: 24, md: 24, lg: 8, xl: 8, xxl: 8 }">
					<div  :key="index">
						<div class="itemTitle">
								<i><img class="star" :src="starImg" alt="" /></i>{{ item.introduction }}
						</div>
						<p class="itemCon">{{ item.textContent }}</p>
					</div>
				</w-grid-item>
			</transition-group>
        </w-grid>
	</div>
</template>

<script lang="ts" setup>
import { ref,computed,watch, onMounted } from 'vue';
import starImg from '/@/assets/chat/star.svg';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import mittBus from '/@/utils/mitt';
import { useChatStore } from '/@/stores/chat';
import { useRoute,useRouter } from 'vue-router';
import { useUserInfo } from '/@/stores/userInfo';
import { debounce } from 'lodash-es'

const route = useRoute();
const router = useRouter();
const isShow = ref(true)
  const chatStore = useChatStore();
  const storesUserInfo = useUserInfo();
// 移动端自适应相关
const { isMobile } = useBasicLayout();
// let listItem = ref([
// 	{
// 		title: '提出复杂的问题',
// 		content: '帮我写一篇关于“全国两会”总结性报告，并量化各项指标及情感倾向',
// 	},
// 	{
// 		title: '获取更好的答案',
// 		content: '“全国两会”的关注度最高的5条政策性建议',
// 	},
// 	{
// 		title: '收获更优的创意灵感',
// 		content: '以鲁迅的口吻帮我写一篇“全国两会”的宣传',
// 	},
// ]);

const listItem = computed(() => {
	if (!isMobile.value) {
		// eslint-disable-next-line vue/no-async-in-computed-properties
		setTimeout(() => {
			document.querySelector('.center-side').scrollTop = 999999999
		},10)
	}
	
	return chatStore.dialogueParams.examples ?? []
})

const sendMessage = debounce((item) => {
	if(chatStore.dialogueLoading) return
	mittBus.emit('setsendMessage', item);
},500)

onMounted(() => {})

watch(
	() => route.params.appId,
	() => {
		if(route.params.appId && route.params.appId != ''){
			isShow.value = false
			setTimeout(() => {
                isShow.value = true
            },500)
		}
	}
);
</script>

<style scoped lang="scss">
.centerInitItem {
	padding-top: 120px;
	&.centerInitItemMobile {
		padding-top: 10px;
	}
	.gotofeedback{
		display: inline-block;
		cursor: pointer;
		color: var(--w-color-primary);
	}

	.title {
		height: 60px;
		line-height: 60px;
		color: #181b49;
		font-size: var(--font28);
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.subtitle {
		color: #9a99aa;
		font-size: var(--font16);
		text-align: center;
	}
	.small {
		font-size: var(--font14);
		padding: 36px 0 32px 0;
	}
	.item {
		background: rgba(255, 255, 255, 0.3);
		border-radius: 8px;
		border: 1px solid #ffffff;
		padding: 16px 23px 23px;
		transition: box-shadow .2s cubic-bezier(0,0,1,1);
		cursor: pointer;
		// &:hover{
		// 	box-shadow: 0 2px 16px #262a3233;
		// }
		.itemTitle {
			font-size: var(--font16);
			margin-bottom: 16px;
			display: flex;
			align-items: center;

			i {
				margin-right: 5px;
			}
		}
		.itemCon {
			line-height: 24px;
			color: #646479;
			font-size: var(--font14);
			-webkit-line-clamp: 2;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			overflow: hidden;
			text-overflow: ellipsis;

		}
		.star {
			width: 20px;
			height: 20px;
		}
	}
}

@media(any-hover:hover){
	.item:hover {
		box-shadow: 0 2px 16px #262a3233;
	}
}
</style>
