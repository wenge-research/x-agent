<template>
	<div class="outer" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		<div class="header">
			<div class="headContent">
				<img src="/src/assets/homePage/logo.png" />
			</div>
			<div class="bottomContent">
				<div class="content">
					<div v-for="(item, index) in permissionList" :key="index" class="itemImg" @click="toClickPage(item)">
						<img :src="item.menuIcon" />
					</div>
				</div>
				<!-- <div class="noLogin" @click="clickBack">
          <img src="/src/assets/homePage/contacts-book-line.svg" />
          <div class="list">未登录人员名单</div>
        </div> -->
			</div>
		</div>
	</div>
</template>
<script lang="ts" setup>
import { serviceList, guanxinCheckPhone } from '/@/api/chat/index';
import { ref, onBeforeMount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showNotify } from 'vant';
const router = useRouter();
const route = useRoute();
const permissionList = ref([]);
const permissionBoolean = ref(false);
onBeforeMount(() => {
	let permission = JSON.parse(sessionStorage.getItem('permissionList'));
	if (permission && permission.length) {
		guanxinCheckPhoneList(permission[0].children);
	}
});
const clickBack = () => {
	// let appCode = sessionStorage.getItem('applicationCode');
	// router.push(`/noLogin/${appCode}`)
	router.push(`/noLogin/zgc`);
};
const serviceListData = ref({
	gxassistant: '/src/assets/homePage/2-page.png',
	gxZhixun: '/src/assets/homePage/3-page.png',
	gxservice: '/src/assets/homePage/1-page.png',
});
const toBlankPage = (item) => {
	window.location.href = item.menuUrl;
	// const hashPart = item.menuUrl.split('#')[1] || '';
	// const queryPart = hashPart.split('?')[1] || '';
	// const queryParams = new URLSearchParams(queryPart);
	// const appCode = queryParams.get('appCode');
	// const matterId = queryParams.get('matterId');
	// console.log(appCode, matterId)
	// router.push({ name: 'information', params: { appId: appCode, matterId: matterId } });
};
const startChat = () => {
	router.push(`/chat/${getAppDetail()?.applicationCode}/`);
};
const goTopersonalCenter = () => {
	console.log(getAppDetail()?.applicationCode, 9999);
	router.push(`/personalCenter/${getAppDetail()?.applicationCode}`);
};
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	console.log(appInfo, 10000);
	return appInfo ? appInfo : '';
};
const touchStartX = ref(0);
const handleTouchStart = (event) => {
	touchStartX.value = event.touches[0].clientX;
};

const handleTouchEnd = (event) => {
	const touchEndX = event.changedTouches[0].clientX;
	const deltaX = touchEndX - touchStartX.value;

	// 如果从左向右滑动超过一定距离，返回上一页
	if (deltaX > 50) {
		var ua = navigator.userAgent.toLowerCase();
		// 判断是否在微信浏览器内
		if (ua.match(/MicroMessenger/i) == 'micromessenger') {
			wx.miniProgram.getEnv((res) => {
				if (res.miniprogram) {
					window.wx.miniProgram.exitMiniProgram();
				} else {
					console.log('不在小程序内');
					window.history.back();
				}
			});
		} else {
			console.log('不在微信浏览器内');
			window.history.back();
		}
	}
};
const guanxinCheckPhoneList = async (permission) => {
	let userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
	const res = await guanxinCheckPhone({
		phone: userInfo.phone,
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
	});
	if (res?.code === '000000') {
		if (res?.data.result == null) {
			permissionList.value = permission.filter((item) => item.menuCode != 'gxZhixun' && item.menuCode != 'gxLiulian');
		} else {
			permission.forEach((item) => {
				if (item.menuCode == 'gxZhixun' || item.menuCode == 'gxLiulian') {
					item.menuUrl = item.menuUrl + `?phone=${userInfo.phone}`;
				}
			});
			permissionList.value = permission;
			permissionBoolean.value = res?.data.result;
		}
	} else {
		permissionList.value = permission.filter((item) => item.menuCode != 'gxZhixun' && item.menuCode != 'gxLiulian');
	}
};
const toClickPage = async (item) => {
	window.location.href = item.menuUrl;
};
</script>
<style lang="scss" scoped>
.outer {
	width: 100%;
	height: 100%;
	background-image: url('/src/assets/homePage/bg.png');
	background-repeat: no-repeat;
	background-size: 100% 100%;

	.header {
		width: 100%;
		height: 100%;
		position: relative;
		.headContent {
			display: flex;
			justify-content: center;
			align-items: center;
			img {
				margin-top: 7.5vh;
				width: 270px;
				height: 80px;
			}
		}
		.bottomContent {
			width: 100%;
			height: 78vh;
			overflow-y: auto;
			background: rgba(255, 255, 255, 0.95);
			border-radius: 16px 16px 0px 0px;
			position: absolute;
			bottom: 0;
			padding-top: 12px;
			.content {
				// overflow-y: auto;
				// height: calc(78vh - 60px);
			}
			.itemImg {
				padding: 8px 20px;
			}
		}
	}
}
.noLogin {
	width: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 10px 0;
	// position: absolute;
	// bottom: 20px;
	.list {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #999999;
		line-height: 22px;
		margin-left: 6px;
	}
}
</style>
