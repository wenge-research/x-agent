<template>
	<div class="instruct">
		<div class="content">
			<div class="title">雅意大模型指令集</div>
				<div class="description">只需一个指令，打开行业认知窗口</div>
				<w-input-search
					v-model="keyWords"
					class="searchInput"
					placeholder="请输入关键词"
					clearable
					@change="changeText"
				>
				</w-input-search>
				<div class="keyWords">
					<div v-for="(item,index) in wordsList" :key="index" class="tag" :class="{active: activeWordsIndex == index}" @click="changeTag(index)">
						{{item.name}}
					</div>
				</div>
				<w-spin :loading="loading" style="width: 100%;">
					<div class="instructList" :style="`min-height: ${setHeight}px;`">
						<w-scrollbar :style="`height: ${setHeight}px;overflow:auto;`">
							<div v-if="instructList.length">
								<w-grid :cols="24" :col-gap="20" :row-gap="10">
									<w-grid-item  class="item" @mouseenter="enter(index)"
										@mouseleave="leave()"  v-for="(item, index) in instructList" :key="index"  :span="{ xs: 24, sm: 12, md: 8, lg: 6, xl: 6, xxl: 6 }">
										<div  :key="index">
											<div class="itemTitle">{{item.name}}</div>
											<p class="itemCon">{{item.prompt}}</p>
										</div>
										<div class="operate">
											<span class="detail" @click="detailFun(item)"><CoolZhilingxiangqing  size="14" />指令详情</span>
											<span class="go" @click="goFun(item)">去执行<CoolQianwang  size="14" /></span>
										</div>
									</w-grid-item>
								</w-grid>
							</div>

							<div v-else>
								<w-empty>
									<template #image>
									<img class="nodata" :src="noDataImg" alt="">
									</template>
										暂无记录
								</w-empty>
							</div>
						</w-scrollbar>
					</div>
				</w-spin>
				<w-modal v-model:visible="visible" hide-cancel ok-text="去执行" modal-class="instructDetailModal" :render-to-body="false" @ok="goFun">
					<template #title>
						{{promptName}}
					</template>
					<div>
						<w-textarea v-model="instext" :max-length="1000" show-word-limit :auto-size="{
							minRows:10,
							maxRows:10
						}" />
						<!-- <div class="detail">{{instext}}</div> -->
					</div>
				</w-modal>
		</div>
	</div>

</template>

<script lang="ts" setup>
import { onMounted, ref,watch } from 'vue';
import router from '/@/router';
import { pagePrompt,listIndustry,listPrompt } from '/@/api/manage'
import { useChatStore } from '/@/stores/chat';
import noDataImg from '/@/assets/chat/nodata.svg';
const chatStore = useChatStore();
const visible = ref(false)
const instext = ref('')
const activeWordsIndex = ref(0)
const activeIndex = ref(-1)
const wordsList = ref([])
const promptName = ref('')
const instructList = ref([])
const loading = ref(false)
const industryType = ref(0)
const keyWords = ref('')
const changeTag = (index) => {
	industryType.value = wordsList?.value[index].id
	activeWordsIndex.value = index
}
const initListIndustry = async() => {
	let res = await listIndustry();
	if(res.code === 200){
		wordsList.value = res.data
		wordsList.value.unshift({name: "推荐",id: 0})
		
     }
}
const enter = (index: any) => {
  activeIndex.value = index
}
const leave = () => {
  activeIndex.value = -1
}
const detailFun = (item) => {
	visible.value = true
	promptName.value = item.name
	instext.value = item.prompt

}
const goFun = (item) => {
	chatStore.chatInputText = item ? item.prompt : instext.value
	router.push('/chat/21')
}
const changeText = () => {
	init()
}
const init = async() => {
	loading.value = true
	let data = {
		keyword: keyWords.value,
		industryId: industryType.value == 0 ? '': industryType.value,
		recommend: industryType.value == 0 ? 1: ''
	}
	let res = await listPrompt(data);
     if(res.code === 200){
		loading.value = false
		instructList.value = res.data
     }else{
		loading.value = false
	}
}
const setHeight = ref(0)

const resizeScrollHeight = () => {
	let clientHeight = document.documentElement.clientHeight ||  document.body.clientHeight;
	let h = clientHeight - 450
	setHeight.value = h
};
onMounted(() => {
	initListIndustry()
	init()
	resizeScrollHeight()
	
})
watch(
	industryType,
	() => {
		init();
	},
	{
		deep: true,
	}
);
</script>

<style lang="scss" scoped>
.instruct {
	background: url('../../assets/instruct/bg.png') no-repeat;
	background-size: 100%;
	padding-bottom: 0;
	height: 100%;
	padding: 140px 0px 40px 0px;
	background-position: 0px -13px;

	.content{
		// max-width: 1340px;
		margin: auto;
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	.title{
		height: 50px;
		font-size: 36px;
		font-weight: 400;
		color: #181B49;
		line-height: 50px;
	}
	.description{
		height: 20px;
		font-size: var(--font14);
		font-weight: 400;
		color: #9A99AA;
		line-height: 20px;
		margin: 10px 0 30px 0;
	}
	.searchInput{
		width: 600px;
		border-radius: 8px;
		margin-bottom: 38px;
		height: 44px;
		box-shadow: 0px 6px 20px 0px rgba(30,64,175,0.06);
	}
	.keyWords{
		display: flex;
		margin-bottom: 35px;
		.tag{
			padding: 3px 25px;
			font-size:var(--font16);
			color: #646479;
			cursor: pointer;
		}
		.active{
			height: 28px;
			background: linear-gradient(90deg, #7E9DFF 0%, #355EFF 100%);
			border-radius: 14px;
			color: #fff;
		}

	}
	.instructList{
		min-height: 500px;
		width: 100%;
		
		.w-grid{
			max-width: 1340px;
			margin: auto;
			margin-top: 10px;
		}
		.item{
			transition: all 0.3s ease;
			background: url('../../assets/instruct/card.svg') no-repeat;
			padding: 24px 24px 16px 24px;
			cursor: pointer;
			.itemTitle{
				font-size: var(--font16);
				color: #181B49;
				margin-bottom: 8px;
			}
			.itemCon{
				min-height: 72px;
				font-size: var(--font14);
				color: #9A99AA;
				line-height: 24px;
				display: -webkit-box; 
				-webkit-line-clamp:3;
				-webkit-box-orient: vertical;
				overflow: hidden; 
			}
			.operate{
				display: flex;
				margin-top: 14px;
				opacity: 0;
				
				>span{
					display: inline-block;
					text-align: center;
					width: 50%;

				}
				.detail{
					color: #646479;
					border-right: 1px solid #E4E8EE;
					.cool-icon{
						margin-right: 10px;
					}
				}
				.go{
					color: #355EFF;
					.cool-icon{
						margin-left: 10px;
					}
				}
			}
			&:hover{
				background: #fff;
				border-radius: 8px;
				transform: scale(1.05);
				box-shadow: 0px 6px 20px 0px rgba(30,64,175,0.06);
				.operate{
					opacity: 1;
				}
			}
		}
	}
	:deep(.instructDetailModal){
		background: linear-gradient(130deg, #DFEAFC 0%, #FFFFFF 100%), linear-gradient(180deg, #EDE7FF 0%, rgba(239,243,251,0) 100%);
		border-radius: 12px;
		width: 700px;
		.w-icon{
			width: 1.5em;
			height: 1.5em;
		}
		.w-modal-footer{
			text-align: center;
		}
		.w-btn-primary{
			width: 200px;
			height: 40px;
			line-height: 40px;
			background: linear-gradient(90deg, #7E9DFF 0%, #355EFF 100%);
			border-radius: 8px;
			font-size: var(--font18);
			margin-top: 10px;
			border: none;
		}
		.w-textarea-wrapper{
			background: transparent;
			border-radius: 8px;
			border-color: #D0D5DC;
			color: #181B49;
			line-height: 32px;
			padding-bottom: 40px;
			
		}
		.w-textarea{
			padding: 20px;
			font-size: var(--font16);
		}
		.w-textarea{
			cursor: inherit !important;
		}
		.detail{
			font-size: var(--font16);
			line-height: 32px;
			color: #181B49;
			height: 287px;
			overflow: auto;
		}
	}
	:deep(.w-spin){
		.w-empty-image{
			img{
				margin: auto;
			}
		}
	}


}

</style>
