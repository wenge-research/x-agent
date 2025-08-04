<template>
	<div class="noice-dialog">
		<w-modal v-model:visible="state.isUpgrade" hide-cancel :mask-closable="false" :mask="false" @cancel="closeModal" :unmount-on-close="true" modal-class="noiceModal">
			<template #title>
				<span>账户<span class="active"> {{ noticeData?.msg}} </span></span>
			</template>
			<div class="detail">
				<h3>为不影响您的正常使用，建议您先充值帐户。</h3>
				<div class="describe">
					<h3>费用说明</h3>
					<p v-for="item in msgData" :key="item.relServerId">
						<span class="lable">{{item.serverName}}</span>
						<span>{{item.price}}/{{item.chargeLatitude}}</span>
					</p>
				</div>
				<div class="tip">
					<h3>提示</h3>
					<p>· 1 token 约等于 1.35 字符</p>
				</div>
			</div>
			<template #footer>
				<w-button @click="closeModal">稍后再说{{time}}s</w-button>
				<w-button @click="voucherCenter" type="primary">去充值</w-button>
			</template>
		</w-modal>
	</div>
</template>

<script setup lang="ts" name="layoutUpgrade">
import { reactive, onMounted,ref,defineProps, computed } from 'vue';
import { useChatStore } from '/@/stores/chat';
import { useRouter } from 'vue-router';
import mittBus from '/@/utils/mitt';
import { Local } from '/@/utils/storage';
import { serviceChargePolicy } from '/@/api/personal'
import { updateStatusByNoticeId } from '/@/api/personal'
const chatStore = useChatStore();
const router = useRouter();
const msgData = ref([])
const noticeData = computed(() => chatStore.noticeData)
// 定义变量内容
const state = reactive({
	isUpgrade: false,
	isLoading: false,
	btnTxt: '',
});
const timer = ref(null)
const time = ref(10)

const voucherCenter = () => {
	state.isUpgrade = false;
	changeNoiceStatus()
	router.push('/personal/voucherPage')
};

// 延迟显示，防止刷新时界面显示太快
const delayShow = () => {
	setTimeout(() => {
		state.isUpgrade = true;
	}, 0);
};
const closeModal = () => {
	clearInterval(timer.value)
	state.isUpgrade = false;
	chatStore.isShowNoice = false
	changeNoiceStatus()

}
//修改余额状态
const changeNoiceStatus = async()=> {
	if(noticeData.value.id != -1){
		await updateStatusByNoticeId(noticeData.value.id)
	}
	
}
const initMsgData = async() => {
	try {
		const res = await serviceChargePolicy()
		msgData.value = res?.data
	} catch (err) {
		throw new Error(err)
	}
}
// 页面加载时
onMounted(() => {
	initMsgData()
	init()
});
const init =()=> {
		delayShow();
		timer.value = setInterval(() => {
			if(time.value <= 0){
				closeModal()
			}else{
				time.value = time.value - 1
			}
			
		}, 1000);
}
defineExpose({state});
</script>
<style lang="scss">
.noiceModal{
		width: 400px;
		background: #fff;
		position: absolute;
		bottom: 32px;
		right: 24px;
		top: initial !important;
		.w-modal-header{
			border-radius: 8px 8px 0 0;
			padding: 25px 36px 23px 36px;
			background:linear-gradient(180deg, rgba(255,175,125,0.3) 0%, rgba(255,223,202,0) 68%, rgba(255,255,255,0) 100%), linear-gradient(180deg, rgba(255,135,127,0.3) 0%, rgba(251,245,239,0) 100%);
		}
		.w-modal-title{
			flex-direction: column;
			align-items: flex-start;
		}
		.w-icon{
			width: 1.5em;
			height: 1.5em;
		}
		.w-modal-footer{
			text-align: center;
			.w-statistic-value {
				color:#fff;
				font-size: 18px;
				font-weight: 400;
			}
		}
		.w-btn{
			height: 36px;
			line-height: 36px;
			border-radius: 4px;
			font-size: var(--font16);
		}
		.active{
			font-size: var(--font24);
			color: #FF6200;
		}
		.detail{
			font-size: var(--font16);
			color: #181B49;
			h3{
				height: 28px;
				font-size: 14px;
				font-weight: 400;
				color: #646479;
				line-height: 28px;
				margin-bottom: 32px;
				>span{
					color: #FF6200;
					font-size: 24px;
					display: inline-block;
					padding: 0 3px;
				}
			}
		}
		.describe{
			h3{
				padding-left: 8px;
				font-size: 16px;
				font-weight: bold;
				margin-bottom: 24px;
				&::before{
					height: 16px;
					width: 3px;
					content: '';
					display: inline-block;
					background: #355EFF;
					margin-right: 10px;
					padding-top: 6px;
					line-height: 20px;
					vertical-align: text-top;

				}
			}
			>p{
				color: #646479;
				padding-left: 8px;
				margin-bottom: 15px;
				font-size: 14px;
				padding-left: 22px;
				span{
					
					display: inline-block;
					text-align: left;
				}
				.lable{
					width: 100px;
					margin-right: 20px;
				}
			}
		}
		.tip{
			h3{
				font-size: 14px;
				font-weight: bold;
				margin-top: 24px;
				margin-bottom: 12px;
			}
			>p{
				font-size: 14px;
				font-weight: 400;
				color: #646479;
				margin-bottom: 14px;
				&:nth-child(3){
					margin-bottom: 0;
				}
			}
			.center{
				color: #355EFF;
				cursor: pointer;
			}
		}

}
</style>
