<template>
	<div class="outer" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		<div class="header">
			<div class="headContent">
				<img src="/src/assets/chatTheme/kefuLogo.png" style="width: 140px; height: 36px" />
				<img @click="goTopersonalCenter" src="/src/assets/chatTheme/useraccount.svg" style="width: 20px; height: 20px" />
			</div>
			<div class="headCard">
				<span class="tryRun">试运行</span>
				<div>
					<img src="/src/assets/chatTheme/kefuhead.png" />
					<div class="text">
						<img v-if="getAppDetail()?.identityIcon" :src="getAppDetail()?.identityIcon" style="width: 204px" />
						<p>{{ getAppDetail()?.greeting }}</p>
					</div>
				</div>
				<img src="/src/assets/chatTheme/startBtn.png" @click="startChat" class="startBtn" />
			</div>
			<div class="slider">
				<div class="slider-li">
					<img src="/src/assets/enterpriseServices/slider1.png" style="position: relative" />
					<span class="click" @click="toToggleClick"></span>
					<img src="/src/assets/enterpriseServices/slider2.png" />
					<span class="click" style="right: 0" @click="toPage"></span>
				</div>
				<div class="slider-li">
					<img src="/src/assets/enterpriseServices/slider3.png" style="position: relative" />
					<span class="click" @click="toPage2" style="width: 59%"></span>
					<img src="/src/assets/enterpriseServices/slider41.png" />
					<span class="click" style="right: 0; width: 39%" @click="toPage3"></span>
				</div>
			</div>
			<div class="contentMid">
				<div class="rightContent">
					<img src="/src/assets/chatTheme/bianminfuwu.svg" />
					<span>便民服务</span>
				</div>
				<img src="/src/assets/chatTheme/xitong.svg" class="leftContent" />
			</div>
			<div class="bottomCard">
				<div v-for="item in serviceListData" :key="item.id" @click="toBlankPage(item)">
					<span>{{ item.menuName }}</span>
					<img :src="item.menuIcon" />
				</div>
			</div>
		</div>
	</div>
</template>
<script lang="ts" setup>
import { serviceList } from '/@/api/chat/index';
import { ref, onBeforeMount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
const router = useRouter();
const route = useRoute();
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
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
		const userInfo = sessionStorage.getItem('userInfo')
			? JSON.parse(sessionStorage.getItem('userInfo'))
			: {
					userType: '',
			  };
		var ua = navigator.userAgent.toLowerCase();
		if (!userInfo?.userType || userInfo?.userType == 'resident') {
			// 居民
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
		} else {
			// 判断是否在微信浏览器内
			if (ua.match(/MicroMessenger/i) == 'micromessenger') {
				wx.miniProgram.getEnv((res) => {
					if (res.miniprogram) {
						// wx.miniProgram.navigateBack();
						router.push(`/homePage/${getAppDetail()?.applicationCode}`);
					} else {
						console.log('不在小程序内');
						router.push(`/homePage/${getAppDetail()?.applicationCode}`);
					}
				});
			} else {
				console.log('不在微信浏览器内');
				router.push(`/homePage/${getAppDetail()?.applicationCode}`);
			}
		}
	}
};
onBeforeMount(() => {
	getServiceList();
});
const serviceListData = ref([]);
const getServiceList = () => {
	serviceList({
		status: 1,
		pageNo: 1,
		pageSize: 1000,
		serviceType: '便民服务',
		applicationId: getAppDetail()?.applicationId,
	}).then((res) => {
		if (res.code == '000000') {
			serviceListData.value = res.data?.records;
		} else {
			serviceListData.value = [];
		}
	});
};
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
const toPage = () => {
	router.push(`/enterpriseServices/zgc`);
};
const toPage2 = () => {
  var ua = navigator.userAgent.toLowerCase();
  // 判断是否在微信浏览器内
  if (ua.match(/MicroMessenger/i)=="micromessenger") {
    wx.miniProgram.getEnv((res) => {
      if (res.miniprogram) {
        wx.miniProgram.navigateTo({
          url: "/pages/middlePage/index",
          success: (res) => {
            console.log(res, 2222); // 页面跳转成功的回调函数
          },
          fail: (err) => {
            console.log(err, 3333); // 页面跳转失败的回调函数
          },
        });
      } else {
        window.location.href =
        "https://dibrain.localhost/guanxin-mp/appmsgalbum?__biz=MzI2MTAxMTg3OA==&action=getalbum&album_id=3785115104287195139#wechat_redirect";
      }
    })
  } else {
    window.location.href =
    "https://dibrain.localhost/guanxin-mp/appmsgalbum?__biz=MzI2MTAxMTg3OA==&action=getalbum&album_id=3785115104287195139#wechat_redirect";
  }
};
const toPage3 = () => {
	const userInfo = sessionStorage.getItem('userInfo')
		? JSON.parse(sessionStorage.getItem('userInfo'))
		: {
				openId: '',
		  };
	window.location.href = `https://localhost/zgcH5/#/csgyy?userOpenId=${userInfo?.openId}`;
};
const startChat = () => {
	router.push(`/chat/${getAppDetail()?.applicationCode}/`);
};
const goTopersonalCenter = () => {
	router.push(`/homePersonalCenter/${getAppDetail()?.applicationCode}`);
};

const toToggleClick = () => {
	router.push(`/problemFeedback/${getAppDetail()?.applicationCode}`);
};
</script>
<style lang="scss" scoped>
.outer {
	width: 100%;
	height: 100%;
	background-image: url('/src/assets/chatTheme/layoutBg.png');
	background-repeat: no-repeat;
	background-size: 100% 100%;

	.header {
		padding: 16px;
		width: 100%;
		height: 224px;
		background-image: url('/src/assets/chatTheme/indexBg.png');
		background-size: 100% 100%;
		background-repeat: no-repeat;

		.headContent {
			display: flex;
			justify-content: space-between;
			align-items: center;
		}

		.headCard {
			position: relative;
			margin-top: 16px;
			background: linear-gradient(180deg, rgba(22, 158, 154, 0) 0%, rgba(22, 158, 154, 0.3) 100%), rgba(255, 255, 255, 0.8);
			box-shadow: 0px 5px 14px 0px rgba(7, 49, 47, 0.1), 0px 6px 6px 0px rgba(23, 161, 157, 0.1);
			border-radius: 8px;
			backdrop-filter: blur(2px);
			padding-bottom: 49px;

			.tryRun {
				position: absolute;
				top: 0;
				right: 0;
				width: 52px;
				height: 24px;
				background: rgba(22, 158, 154, 0.1);
				border-radius: 0px 8px 0px 8px;
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 12px;
				color: #169e9a;
				line-height: 18px;
				text-align: left;
				font-style: normal;
				text-align: center;
				line-height: 18px;
				padding: 3px 8px;
			}

			> div {
				display: flex;
				justify-content: space-between;
				padding-top: 23px;
				padding-right: 16px;

				img {
					width: 122px;
				}

				.text {
					padding-bottom: 8px;
				}

				.text p:first-child {
					font-family: MiSans, MiSans;
					font-weight: 500;
					font-size: 18px;
					color: #169e9a;
					line-height: 24px;
					text-align: left;
					font-style: normal;
					letter-spacing: 1px;
				}

				.text p:last-child {
					margin-top: 6px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #169e9a;
					line-height: 20px;
					text-align: justify;
					font-style: normal;
					letter-spacing: 1px;
				}
			}

			::v-deep .startBtn {
				width: calc(100% + 20px);
				max-width: none !important;
				position: absolute;
				bottom: -10px;
				left: -10px;
			}
		}

		.contentMid {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin: 18px 0;

			.rightContent {
				display: flex;
				align-items: center;

				img {
					width: 20px;
					height: 16px;
					margin-right: 4px;

					span {
						font-family: MiSans, MiSans;
						font-weight: 500;
						font-size: 18px;
						color: #434649;
						line-height: 20px;
						text-align: justify;
						font-style: normal;
					}
				}
			}

			.leftContent {
				width: 20px;
				width: 20px;
			}
		}

		.bottomCard {
			display: flex;
			justify-content: flex-start;
			flex-wrap: wrap;
			gap: 9px;

			> div {
				display: flex;
				justify-content: space-between;
				width: calc(50% - 5px);
				height: 68px;
				background: rgba(255, 255, 255, 0.65);
				border-radius: 8px;
				padding: 14px 12px;
				align-items: center;

				span {
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 16px;
					color: #494c4f;
					line-height: 20px;
					text-align: left;
					font-style: normal;
					margin-right: 9px;
				}

				img {
					width: 44px;
					height: 44px;
				}
			}
		}
	}

	.headCard {
		position: relative;
		margin-top: 16px;
		background: linear-gradient(180deg, rgba(22, 158, 154, 0) 0%, rgba(22, 158, 154, 0.3) 100%), rgba(255, 255, 255, 0.8);
		box-shadow: 0px 5px 14px 0px rgba(7, 49, 47, 0.1), 0px 6px 6px 0px rgba(23, 161, 157, 0.1);
		border-radius: 8px;
		backdrop-filter: blur(2px);
		padding-bottom: 49px;

		.tryRun {
			position: absolute;
			top: 0;
			right: 0;
			width: 52px;
			height: 24px;
			background: rgba(22, 158, 154, 0.1);
			border-radius: 0px 8px 0px 8px;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 12px;
			color: #169e9a;
			line-height: 18px;
			text-align: left;
			font-style: normal;
			text-align: center;
			line-height: 18px;
			padding: 3px 8px;
		}

		> div {
			display: flex;
			justify-content: space-between;
			padding-top: 23px;
			padding-right: 16px;

			img {
				width: 122px;
			}

			.text {
				padding-bottom: 8px;
			}

			.text p:first-child {
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 18px;
				color: #169e9a;
				line-height: 24px;
				text-align: left;
				font-style: normal;
				letter-spacing: 1px;
			}

			.text p:last-child {
				margin-top: 6px;
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 14px;
				color: #169e9a;
				line-height: 20px;
				text-align: justify;
				font-style: normal;
				letter-spacing: 1px;
			}
		}

		::v-deep .startBtn {
			width: calc(100% + 20px);
			max-width: none !important;
			position: absolute;
			bottom: -10px;
			left: -10px;
		}
	}

	.contentMid {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin: 18px 0;

		.rightContent {
			display: flex;
			align-items: center;

			img {
				width: 20px;
				height: 16px;
				margin-right: 4px;

				span {
					font-family: MiSans, MiSans;
					font-weight: 500;
					font-size: 18px;
					color: #434649;
					line-height: 20px;
					text-align: justify;
					font-style: normal;
				}
			}
		}

		.leftContent {
			width: 20px;
			width: 20px;
		}
	}

	.bottomCard {
		display: flex;
		justify-content: flex-start;
		flex-wrap: wrap;
		gap: 9px;

		> div {
			display: flex;
			justify-content: justify-content;
			width: 167px;
			height: 68px;
			background: rgba(255, 255, 255, 0.65);
			border-radius: 8px;
			padding: 14px 12px;
			align-items: center;

			span {
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 16px;
				color: #494c4f;
				line-height: 20px;
				text-align: left;
				font-style: normal;
				margin-right: 9px;
			}

			img {
				width: 44px;
				height: 44px;
			}
		}
	}
}
.slider {
	.slider-li {
		margin: 10px 0;
		overflow: hidden;
		position: relative;
		.click {
			position: absolute;
			width: 50%;
			z-index: 10;
			top: 0;
			bottom: 0;
		}
		img {
			width: 100%;
			position: absolute;
			right: 0;
			top: 0;
		}
	}
}
</style>
