<template>
	<div class="outer" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		<div class="header">
			<div class="headContent">
				<!-- <img src="/src/assets/homePage/logo.png" /> -->
			</div>
			<div class="bottomContent">
				<div class="content">
					<div class="itemImg" style="width:47%;" @click="toClickPage1">
						<img src="/src/assets/homePageMz/item-img01.png"  />
					</div>
					<div class="itemImg" style="width:49%;" @click="toClickPage2">
						<img src="/src/assets/homePageMz/item-img02.png"  />
					</div>
					<div class="itemImg" style="width:49%;" @click="toClickPage3">
						<img src="/src/assets/homePageMz/item-img03.png"  />
					</div>
					<div class="itemImg" style="width:47%;" @click="toClickPage4">
						<img src="/src/assets/homePageMz/item-img04.png"  />
					</div>
				</div>
				<div style="margin: 20px auto;" @click="toClickPage('understand')"><img src="/src/assets/homePageMz/ljmz.png"></div>
				<div class="bjzs">
					<img src="/src/assets/homePageMz/bjzs.png">
					便捷入口
				</div>
				<div class="bjzs-box">
					<div class="bjzs-item" @click="toClickPage('investCollection')">
						<img src="/src/assets/homePageMz/index-icon1.png">
						<span>投资线索收集</span>
					</div>
					<div class="bjzs-item" @click="toClickPage('policyCompilation')">
						<img src="/src/assets/homePageMz/index-icon2.png">
						<span>政策汇编</span>
					</div>
					<div class="bjzs-item" @click="toClickPage('itemQysq')">
						<img src="/src/assets/homePageMz/index-icon3.png">
						<span>企业诉求反馈</span>
					</div>
					<div class="bjzs-item" @click="toClickPage('itemHqzc')">
						<img src="/src/assets/homePageMz/index-icon4.png">
						<span>惠企政策</span>
					</div>
				</div>
				<div class="footer">民治街道经济发展办</div>
			</div>
		</div>
		<div class="ai-icon" @click="toClickAppTemplate"><img src="/src/assets/homePageMz/icon-ai.png"></div>
	</div>
</template>
<script lang="ts" setup>
import { serviceList, guanxinCheckPhone } from '/@/api/chat/index';
import { ref, onBeforeMount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showNotify } from 'vant';
const router = useRouter();
const route = useRoute();
const permissionList = ref([
	{
		menuIcon:('/src/assets/homePageMz/item-img01.png'),
		url:'',
		width:'47%'
    },
	{
		menuIcon:('/src/assets/homePageMz/item-img02.png'),
		url:'',
		width:'49%'
	},
	{
		menuIcon:('/src/assets/homePageMz/item-img03.png'),
		url:'',
		width:'47%'
	},
	{
		menuIcon:('/src/assets/homePageMz/item-img04.png'),
		url:'',
		width:'49%'
	}
]);
const permissionBoolean = ref(false);
onBeforeMount(() => {
	
});
const clickBack = () => {
	router.push(`/noLogin/zgc`);
};

const toBlankPage = (item) => {
	window.location.href = item.menuUrl;
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

const toClickPage1 = async (item) => {
	router.push(`/bannerRenList`);
};
const toClickPage2 = async (item) => {
	router.push(`/bannerQianList`);
};
const toClickPage3 = async (item) => {
	router.push(`/bannerDiList`);
};
const toClickPage4 = async (item) => {
	router.push(`/bannerFangList`);
};
const toClickPage = async (item) => {
	router.push(item);
};
const toClickAppTemplate = async (item) => {
	router.push(`/appTemplate/09cd641a835348a88ec879a6b767f6ae/81cc4d43df274f6fba294b6572fe17c0`);
};

</script>
<style lang="scss" scoped>
.outer {
	width: 100%;
	height: 100%;
	background-image: url('/src/assets/homePageMz/top.png');
	background-repeat: no-repeat;
	background-size: 100% 140px;

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
			height: 84vh;
			overflow-y: auto;
			background: rgba(255, 255, 255, 0.95);
			border-radius: 16px 16px 0px 0px;
			position: absolute;
			bottom: 0;
			padding: 12px;
			.content {
				display: flex;
				justify-content: space-between;
				align-items: center;
			    flex-wrap: wrap;
				// overflow-y: auto;
				// height: calc(78vh - 60px);
			}
			.itemImg {
				margin-bottom: 10px;
			}
			.bjzs{
				display: flex;
				align-items: center;
				font-size: 16px;
				img{
					width: 20px;
					margin-right: 14px;
				}
			}
			.bjzs-box{
				margin-top: 20px;
				display: flex;
				justify-content: space-between;
				align-items: center;
				flex-wrap: wrap;
				font-size: 14px;
				.bjzs-item{
					
					width: 48%;
					background-color: #eef4fc;
					padding: 12px 0;
					border-radius: 6px;
					margin-bottom: 20px;
					display: flex;
				    align-items: center;
				}
				img{
					width: 30px;
					// display: inline-block;
					margin-right:10px;
					margin-left: 20px;
				}
			}
			.footer{
				text-align: center;
				color: #666;
				font-size: 12px;
				margin-top: 50px;
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
.ai-icon{
	position: absolute;
	bottom: 30px;
	right:30px;
	width: 40px;
}
</style>
