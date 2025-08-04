<template>
	<div class="account-dialog">
		<w-modal v-model:visible="state.isUpgrade" hide-cancel :mask-closable="false" @cancel="closeModal" :unmount-on-close="true" modal-class="accountModal">
			<template #title>
				<div style="height: 65px;margin-top: -35px;margin-bottom: 10px;"><CoolYayiLogoNew size="100" /></div>
				<span>欢迎来到雅意YAYI平台</span>
			</template>
			<div class="detail">
				<h3>系统已为您的账户充值 <span>28</span>元！</h3>
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
					<p>· 若费用不足，您可以进入【<span class="center" @click="voucherCenter">充值管理</span>】去充值费用。</p>
				</div>
			</div>
			<template #footer>
				<w-button @click="closeModal" type="primary">我知道了{{time}}s</w-button>
			</template>
		</w-modal>
	</div>
</template>

<script setup lang="ts" name="layoutUpgrade">
import { reactive, computed, onMounted,ref,watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import { Local, Session } from '/@/utils/storage';
import { useUserInfo } from '/@/stores/userInfo';
import { serviceChargePolicy } from '/@/api/personal'
const stores = useUserInfo();
const router = useRouter();

// 定义变量内容
const { t } = useI18n();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const msgData = ref([])
const state = reactive({
	isUpgrade: false,
	version: __VERSION__,
	guide: __GUIDE__,
	isLoading: false,
	btnTxt: '',
});
const timer = ref(null)
const time = ref(12)

const voucherCenter = () => {
	state.isUpgrade = false;
	router.push('/personal/voucherPage')
};


// 延迟显示，防止刷新时界面显示太快
const delayShow = () => {
	setTimeout(() => {
		state.isUpgrade = true;
	}, 2000);
};
const closeModal = () => {
	clearInterval(timer.value)
	state.isUpgrade = false;
	stores.userInfos.firstLogin = false
	Session.set('firstLogin',false);
			
}
const init = async() => {
	try {
		const res = await serviceChargePolicy()
		msgData.value = res?.data
	} catch (err) {
		throw new Error(err)
	}
}
// 页面加载时
onMounted(() => {
	delayShow();
	timer.value = setInterval(() => {
		if(time.value <= 0){
			closeModal()
		}else{
			time.value = time.value - 1
		}
		
	}, 1000);
	init()
});

</script>
<style lang="scss">
.accountModal{
		width: 480px;
		background: linear-gradient(130deg, #DFEAFC 0%, #FFFFFF 100%), linear-gradient(180deg, #EDE7FF 0%, rgba(239,243,251,0) 100%);
		.w-modal-header{
			border-radius: 12px 12px 0 0;
			padding: 35px 36px 23px 36px;
			background: url('../../assets/home/modalBg.png') no-repeat
		}
		.w-modal-title{
			flex-direction: column;
			align-items: flex-start;
		}
		.w-icon{
			width: 1.5em;
			height: 1.5em;
		}
		.w-modal-close-btn{
			margin-top: -38px;
		}
		.w-modal-footer{
			text-align: center;
			.w-statistic-value {
				color:#fff;
				font-size: 18px;
				font-weight: 400;
			}
		}
		.w-btn-primary{
			width: 200px;
			height: 40px;
			line-height: 40px;
			background: linear-gradient(90deg, #7E9DFF 0%, #355EFF 100%);
			border-radius: 8px;
			font-size: var(--font18);
			border: none;
		}
		.detail{
			padding: 18px 0px;
			font-size: var(--font16);
			color: #181B49;
			h3{
				font-size: 18px;
				color: #181B49;
				font-weight: bold;
				margin-bottom: 24px;
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
				display: flex;
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
