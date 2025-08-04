<template>
	<div @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		<van-nav-bar title="企业服务" left-text="" fixed left-arrow @click-left="goBack" />
		<div class="enterprise-services">
			<div class="enterprise-services-hd">
				<img src="/src/assets/enterpriseServices/title.png" alt="" />
			</div>
			<div class="enterprise-services-bd">
				<img class="feature" src="/src/assets/enterpriseServices/feature.png" alt="" />
				<img class="feature1" src="/src/assets/enterpriseServices/feature1.png" alt="" />
				<img class="feature2" src="/src/assets/enterpriseServices/feature2.png" alt="" />
				<img class="feature3" src="/src/assets/enterpriseServices/feature3.png" alt="" />
				<img class="feature4" src="/src/assets/enterpriseServices/feature4.png" alt="" />
				<!-- style="filter: grayscale(60%);pointer-events: none;opacity: 0.6;" -->
				<span class="left1" @click="toPage1"></span>
				<span class="left2" @click="toPage2"></span>
				<span class="right1" @click="toPage3"></span>
				<span class="right2" @click="toPage4"></span>
			</div>
			<div class="enterprise-services-ft">
				<img src="/src/assets/enterpriseServices/more.png" alt="" />
			</div>
		</div>
	</div>
</template>
<script>
import { NavBar } from 'vant';
export default {
	meta: {
		title: '',
	},
	props: {},
	components: {
		'van-nav-bar': NavBar,
	},

	data() {
		return {
			touchStartX: 0,
		};
	},
	computed: {
		getAppDetail() {
			let appInfo = JSON.parse(window.localStorage.getItem(`${this.$route?.params?.appId}`));
			return appInfo ? appInfo : '';
		},
	},
	mounted() {
		// }
	},
	methods: {
		handleTouchStart(event) {
			this.touchStartX = event.touches[0].clientX;
		},

		handleTouchEnd(event) {
			const touchEndX = event.changedTouches[0].clientX;
			const deltaX = touchEndX - this.touchStartX;
			const that = this;
			// 如果从左向右滑动超过一定距离，返回上一页
			if (deltaX > 50) {
				var ua = navigator.userAgent.toLowerCase();
				// 判断是否在微信浏览器内
				if (ua.match(/MicroMessenger/i) == 'micromessenger') {
					wx.miniProgram.getEnv((res) => {
						if (res.miniprogram) {
							wx.miniProgram.navigateBack();
						} else {
							console.log('不在小程序内');
							that.$router.push(`/previewChat/${this.getAppDetail?.applicationCode}`);
						}
					});
				} else {
					console.log('不在微信浏览器内');
					that.$router.push(`/previewChat/${this.getAppDetail?.applicationCode}`);
				}
			}
		},
		goBack() {
			this.$router.back();
		},
		toPage1() {
			this.$router.push('/projectList/zgc');
		},
		toPage2() {
			this.$router.push('/talent/zgc?showHead=true');
		},
		toPage3() {
			window.location.href =
				"https://dibrain.localhost/guanxin-lyjjWeChat/html/kjcs/index.html?orgCode=300309";
		},
		toPage4() {
			// window.location.href =
			// 	"https://localhost/guanxin-mp/appmsgalbum?__biz=MzI2MTAxMTg3OA==&action=getalbum&album_id=3785122194640666634#wechat_redirect";
			window.location.href =
				"https://dibrain.localhost/guanxin-mp/appmsgalbum?__biz=MzI2MTAxMTg3OA==&action=getalbum&album_id=3785122194640666634#wechat_redirect";
		},

		homeBack() {
			sessionStorage.setItem('pointId', '');
			this.pointId = sessionStorage.getItem('pointId');
		},
		comeBack() {
			window.location.href =
				"https://dibrain.localhost/wg-agent-client/#/homePage/zgc";
		},
	},
};
</script>
<style scoped lang="scss">
::v-deep .van-nav-bar {
	background: #1683a2;
	.van-nav-bar__title,
	.van-icon {
		color: #fff;
	}
}
.enterprise-services {
	position: relative;
	text-align: center;
	height: 100vh;
	background: url('/src/assets/enterpriseServices/bg2.png') no-repeat center;
	background-size: cover;
	padding: 46px 0 0 0;
	overflow: hidden;
	.enterprise-services-hd {
		position: relative;
		font-family: DOUYINSANSBOLD, DOUYINSANSBOLD;
		font-weight: bold;
		font-size: 28px;
		color: #ffffff;
		line-height: 40px;
		text-align: center;
		font-style: normal;
		padding: 50px 0 0 0;
	}
	.enterprise-services-bd {
		position: relative;
		width: 300px;
		height: 300px;
		margin: 60px auto;
		.feature {
			position: absolute;
			left: 60px;
			top: 46px;
			z-index: 100;
			width: 176px;
		}
		.feature1 {
			position: absolute;
		}
		.feature2 {
			position: absolute;
		}
		.feature3 {
			position: absolute;
		}
		.feature3 {
			position: absolute;
		}
		.left1 {
			position: absolute;
			z-index: 200;
			width: 70px;
			height: 150px;
			left: 0;
			top: 72px;
			z-index: 10;
		}
		.left2 {
			position: absolute;
			z-index: 200;
			width: 150px;
			height: 70px;
			left: 77px;
			top: 3px;
		}
		.right1 {
			position: absolute;
			z-index: 200;
			width: 70px;
			height: 150px;
			left: 227px;
			top: 72px;
			z-index: 10;
		}
		.right2 {
			position: absolute;
			z-index: 200;
			width: 150px;
			height: 70px;
			left: 77px;
			top: 223px;
		}
	}
}
</style>
