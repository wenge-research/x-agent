import { NavBar } from 'vant';

export default {
  components: {
		'van-nav-bar': NavBar,
	},
  data() {
		return {
			touchStartX: 0,
		}
	},
	computed: {
		getAppDetail() {
  		let appInfo = JSON.parse(window.localStorage.getItem(`${this.$route?.params?.appId}`));
  		return appInfo ? appInfo : "";
		}
	},
  methods: {
    goBack() {
			this.$router.back();
		},
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
							that.$router.push(`/talent/${that.getAppDetail?.applicationCode}`); 
						}
					});
				} else {
					console.log('不在微信浏览器内');
					that.$router.push(`/talent/${that.getAppDetail?.applicationCode}`); 
				}
			}
		},
  }
}