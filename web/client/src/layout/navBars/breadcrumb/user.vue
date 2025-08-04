<template>
	<div class="layout-navbars-breadcrumb-user pr15" :style="{ flex: layoutUserFlexNum }">
		<!-- <w-button v-if="!isLogin" type="primary" class="cooperation" shape="round" @click="onALinkClick('cooperation')">
			<template #icon>
				<CoolLianxiwomen size="1.5em" color="#fff" style="vertical-align: top" />
			</template>
			<template #default>体验申请</template>
		</w-button> -->
		<!-- <div class="routerName" v-if="!routeName" @click="changeVersion"><CoolYijianfankui size="16" color="currentColor" />社区反馈</div>
		<div class="routerName" v-if="!routeName" @click="gotoDocs"><CoolBangzhuzhongxinAe75pmh3 size="16" /><span class="text">帮助中心</span></div> -->
		<span style="width: 1px; height: 24px; background: #e4e8ee; margin: 0 14px"></span>

		<!-- <div v-if="!isLogin" @click="gotoLogin" class="loginTip">登录 / 注册</div> -->
		<w-dropdown trigger="hover" @select="onHandleCommandClick">
			<span v-if="isLogin" class="layout-navbars-breadcrumb-user-link layout-navbars-breadcrumb-user-icon">
				<div class="userName" v-if="userNumber">{{ userNumber.substr(0, 3) + '****' + userNumber.substr(7) }}</div>
				<div class="userName" v-if="!userNumber">问答</div>
				<img :src="userInfoPhoto" class="layout-navbars-breadcrumb-user-link-photo mr5" />
			</span>
			<template #content>
				<!-- <w-doption value="personal"> <CoolUser size="18" style="margin-right: 8px; vertical-align: -4px" />个人中心 </w-doption> -->
				<!-- <w-doption value="manage" v-if="userInfos.roles[0] === 'admin'">
					<CoolShezhi size="18" style="margin-right: 8px; vertical-align: -4px" />后台管理
				</w-doption> -->
				<w-doption value="logOut"
					><CoolTuichu size="18" style="margin-right: 8px; vertical-align: -4px" />{{ $t('message.user.dropdown5') }}</w-doption
				>
			</template>
		</w-dropdown>
	</div>
</template>

<script setup lang="ts" name="layoutBreadcrumbUser">
import { computed, reactive, onMounted, ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Modal } from 'winbox-ui-next';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';
import { useUserInfo } from '/@/stores/userInfo';
import { useThemeConfig } from '/@/stores/themeConfig';
import { Session, Local } from '/@/utils/storage';
import userInfoPhoto from '/@/assets/chat/avatar.png';

// 引入组件

// 定义变量内容
const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const stores = useUserInfo();
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);
const isLogin = ref(false);
const state = reactive({
	isScreenfull: false,
	disabledI18n: 'zh-cn',
	disabledSize: 'large',
});

const routeName = ref('');
const userNumber = Session.get('userNumber') || '';

// 设置分割样式
const layoutUserFlexNum = computed(() => {
	let num: string | number = '';
	const { layout, isClassicSplitMenu } = themeConfig.value;
	const layoutArr: string[] = ['defaults'];
	if (layoutArr.includes(layout) || (layout === 'classic' && !isClassicSplitMenu)) num = '1';
	else num = '';
	return num;
});


// 下拉菜单点击时
const onHandleCommandClick = (path: string) => {
	if (path === 'logOut') {
		Modal.open({
			title: t('message.user.logOutTitle'),
			content: t('message.user.logOutMessage'),
			closable: true,
			okText: t('message.user.logOutConfirm'),
			cancelText: t('message.user.logOutCancel'),
			onOk: () => {
				// 清除缓存/token等
				Session.clear();
				stores.logout();
				// 使用 reload 时，不需要调用 resetRoute() 重置路由
				window.location.reload();
			},
		});
	} else if (path === 'manage') {
		let routeUrl = router.resolve({
			path: '/manage',
		});
		window.open(routeUrl.href, '_blank');
	} else if (path === 'personal') {
		let routeUrl = router.resolve({
			path: '/personal',
		});
		window.open(routeUrl.href, '_blank');
	}
};
// 打开外部链接
const onALinkClick = (val) => {
	if (val === 'cooperation') {
		window.open('https://fs80.cn/okn0zl', '_blank');
		return;
	}
	const href = router.resolve({
		//使用resolve
		name: val, //这里是跳转页面的name
	});
	window.open(href.href, '_blank');
};
// 初始化组件大小/i18n
const initI18nOrSize = (value: string, attr: string) => {
	state[attr] = Local.get('themeConfig')[value];
};
// 页面加载时
onMounted(() => {
	if (Local.get('themeConfig')) {
		initI18nOrSize('globalI18n', 'disabledI18n');
	}
	if (Session.get('token')) {
		isLogin.value = true;
	}
	stores.userInfos.isLoginExpired = false;
	stores.loginExpiredSeach();
});
watch(
	() => stores.userInfos.isLoginExpired,
	(newVal: any) => {
		if (newVal) {
			stores.ModalTip();
		}
	}
);
watch(
	route,
	() => {
		routeName.value = route.name === 'docs';
	},
	{ immediate: true }
);
</script>

<style scoped lang="scss">
.layout-navbars-breadcrumb-user {
	display: flex;
	align-items: center;
	justify-content: flex-end;
	&-link {
		height: 100%;
		display: flex;
		align-items: center;
		white-space: nowrap;
		&-photo {
			width: 32px;
			height: 32px;
			border-radius: 100%;
		}
	}
	&-icon {
		padding: 0 10px;
		cursor: pointer;
		color: var(--next-bg-topBarColor);
		height: 50px;
		line-height: 50px;
		display: flex;
		align-items: center;
		&:hover {
			i {
				animation: logoAnimation 0.3s ease-in-out;
			}
		}
	}
	:deep(.w-dropdown) {
		color: var(--next-bg-topBarColor);
	}
	:deep(.w-badge) {
		height: 40px;
		line-height: 40px;
		display: flex;
		align-items: center;
	}
	:deep(.w-badge-dot) {
		top: 12px;
	}
	.svg-icon {
		display: flex;
		align-items: center;
	}
	.cooperation {
		border: none;
		height: 32px;
		background: linear-gradient(270deg, #6597ff 0%, #355eff 100%);
	}
	.userName {
		padding-right: 8px;
		font-size: var(--font16);
		padding-top: 3px;
	}
	.loginTip {
		padding: 2px 8px 0;
		cursor: pointer;
	}
	.routerName {
		font-size: 14px;
		font-weight: 400;
		color: #9a99aa;
		height: 28px;
		padding: 0 8px;

		border-radius: 8px;
		cursor: pointer;
		margin-right: 10px;
		display: flex;
		justify-content: space-around;
		align-items: center;
		padding-top: 2px;
		.text {
			height: 19px;
		}
		.cool-icon {
			margin-right: 6px;
		}
		&:hover {
			color: #355eff;
			background: RGBA(240, 243, 253, 1);
		}
	}
}
</style>
<style lang="scss">
.w-dropdown {
	--color-text-1: #646479;
}
</style>
