<template>
	<div @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		<div>
			<van-nav-bar v-if="showHead" title="找人才" left-text="" fixed left-arrow @click-left="goBack" />
			<div class="project-list" ref="projectList" :class="[showHead ? 'pt46' : '']">
				<div class="project-list-hd">
					<img class="feature" src="/src/assets/enterpriseServices/talent.png" alt="" />
				</div>
				<div class="project-list-bd">
					<ul>
						<li>
							<img class="feature" src="/src/assets/enterpriseServices/talent1.png" alt="" />
							<div class="cont">
								<div class="tit">
									<img src="/src/assets/zhongguancun/icon1.png" alt="" />
									<div class="tit-txt">留学回国人才引进支持项目申请</div>
								</div>
								<div class="btn-list">
									<span class="btn btn-one" @click="toPageDetails('/applicationPolicy/zgc')" style="background: #fff; color: #313436"
										><iconpark-icon class="icon" name="book-open-line" color="#313436"></iconpark-icon>申请政策</span
									>
									<span class="btn btn-one" @click="toPage1(1)"
										><iconpark-icon class="icon" name="draft-line" color="#fff"></iconpark-icon>立即申请</span
									>
									<!-- <span class="btn"  @click="toPage2" style="background: #fff;color: #797991;"><iconpark-icon class="icon" name="book-open-line"></iconpark-icon>通知</span> -->
								</div>
							</div>
						</li>
						<!-- <li>
              <img
                class="feature"
                src="/src/assets/enterpriseServices/talent2.png"
                alt=""
              />
              <div class="cont">
                <div class="tit">
                  <img
                    src="/src/assets/enterpriseServices/talent-tit2.png"
                    alt=""
                  />市区人才服务保障政策一览
                </div>
                <div class="btn-list" @click="toPage4">
                  <span class="btn btn-big">立即查看</span>
                </div>
              </div>
            </li> -->
						<li>
							<img class="feature" src="/src/assets/zhongguancun/2.png" alt="" />
							<div class="cont">
								<div class="tit">
									<img src="/src/assets/zhongguancun/icon2.png" alt="" />
									<div class="tit-txt">北京市引进人才管理办法(试行)</div>
								</div>
								<div class="btn-list" @click="toPageDetails('/talentDetails/zgc')">
									<span class="btn btn-big">立即查看</span>
								</div>
							</div>
						</li>
						<li>
							<img class="feature" src="/src/assets/zhongguancun/3.png" alt="" />
							<div class="cont">
								<div class="tit">
									<img src="/src/assets/zhongguancun/icon3.png" alt="" />
									<div class="tit-txt">北京市促进留学人员来京创业和工作暂行办法</div>
								</div>
								<div class="btn-list" @click="toPageDetails('/overseasStudy/zgc')">
									<span class="btn btn-big">立即查看</span>
								</div>
							</div>
						</li>
						<li>
							<img class="feature" src="/src/assets/zhongguancun/4.png" alt="" />
							<div class="cont">
								<div class="tit">
									<img src="/src/assets/zhongguancun/icon4.png" alt="" />
									<div class="tit-txt">北京市引进毕业生管理办法</div>
								</div>
								<div class="btn-list" @click="toPageDetails('/graduateManagement/zgc')">
									<span class="btn btn-big">立即查看</span>
								</div>
							</div>
						</li>
						<li>
							<img class="feature" src="/src/assets/zhongguancun/5.png" alt="" />
							<div class="cont">
								<div class="tit">
									<img src="/src/assets/zhongguancun/icon5.png" alt="" />
									<div class="tit-txt">工作居住证办理指南</div>
								</div>
								<div class="btn-list" @click="toPage5">
									<span class="btn btn-big">立即查看</span>
								</div>
							</div>
						</li>
						<li>
							<img class="feature" src="/src/assets/enterpriseServices/talent3.png" alt="" />
							<div class="cont">
								<div class="tit">
									<img src="/src/assets/zhongguancun/icon6.png" alt="" />
									<div class="tit-txt">街道人才库个人申请</div>
								</div>
								<div class="btn-list" @click="toPage1(2)">
									<span class="btn btn-big">立即申请</span>
								</div>
							</div>
						</li>
					</ul>
				</div>
			</div>
		</div>
		<van-dialog
			v-model:show="showDialog"
			confirm-button-text="我已全部阅读，符合上述条件"
			confirm-button-color="#169E9A"
			title="温馨提示"
			class="vanDialog"
			:showConfirmButton="false"
		>
			<template #default>
				<div class="talent-bg">
					<img src="/@/assets/images/talent-bg.png" />
				</div>
				<div class="close-icon" @click="handleClose">
					<iconpark-icon class="icon" name="close-large-fill" color="#fff"></iconpark-icon>
				</div>
				<div class="van-name">
					<div>
						申报人员应符合《北京市促进留学人员来京创业和工作暂行办法》（京政发〔2009〕14号）有关规定，我国公派或自费出国（境）在国（境）外留学一年以上并已于近期回国或入境（一般应在回国或入境两年半内），一般在申报单位工作满3个月（以在申请单位签订合同、缴纳社保和个税时间为准），无刑事犯罪记录，引进时年龄原则上不超过45周岁（个人能力、业绩和贡献突出的可放宽至50周岁）。且符合以下条件之一：
					</div>
					<div>1.在国（境）外取得硕士及以上学位；</div>
					<div>2.出国（境）前已具有高级职称，出国（境）进行博士后研究或进修。</div>

					<van-button class="btn-bg" color="#149E9A" @click="handleConfirm" type="success">我已全部阅读，符合上述条件</van-button>
				</div>
			</template>
		</van-dialog>
	</div>
</template>

<script>
import { Tab, Tabs, NavBar } from 'vant';

export default {
	meta: {
		title: '首页',
	},
	props: {},
	components: {
		'van-nav-bar': NavBar,
		[Tabs.name]: Tabs,
		[Tab.name]: Tab,
	},

	data() {
		return {
			showDialog: false,
			typeLi: 1,
			touchStartX: 0,
			showHead: false,
		};
	},

	mounted() {
		// }
		this.showHead = this.$route.query?.showHead ? true : false;
	},
	computed: {
		getAppDetail() {
			let appInfo = JSON.parse(window.localStorage.getItem(`${this.$route?.params?.appId}`));
			return appInfo ? appInfo : '';
		},
	},
	methods: {
		handleTouchStart(event) {
			if (!this.showHead) return;
			this.touchStartX = event.touches[0].clientX;
		},

		handleTouchEnd(event) {
			if (!this.showHead) return;
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
							that.$router.push(`/enterpriseServices/${this.getAppDetail?.applicationCode}`);
						}
					});
				} else {
					console.log('不在微信浏览器内');
					that.$router.push(`/enterpriseServices/${this.getAppDetail?.applicationCode}`);
				}
			}
		},
		goBack() {
			this.$router.back();
		},

		toPage1(type) {
			this.showDialog = true;
			this.typeLi = type;
		},
		toPage2() {
			window.location.href = 'https://localhost/guanxin-space/xxgk/tzgg/202406/t20240619_4657395_hd.shtml';
		},
		// toPage2() {
		//   window.location.href = "https://dibrain.localhost/guanxin-space/xxgk/tzgg/202406/t20240619_4657395_hd.shtml";
		// },
		toPage3() {
			this.showDialog = true;
		},
		toPage4() {
			window.location.href = 'https://localhost/guanxin-talent/kKwofkG7oENDNJgaEr5jCw';
		},
		// toPage4() {
		//   window.location.href = "https://dibrain.localhost/guanxin-talent/kKwofkG7oENDNJgaEr5jCw";
		// },
		toPage5() {
			window.location.href =
				'https://gf.zwdn.com:8083/a/guide/rsGuide/gongzuojzz?openId=oyWv_0g5GRHiq8gfv5hBtqRcf3ko&wxAccountId=gh_0f30f9db8810&index=true';
		},
		handleConfirm() {
			this.showDialog = false;
			// showHead == false:直接从人才库访问
			const showHead = this.showHead ? 'home' : 'talent'
			if (this.typeLi == 1) {
				if (import.meta.env.VITE_PUBLIC_PATH == '/wg-agent-client-demo') {
					window.location.href = `https://dibrain.localhost${
						import.meta.env.VITE_PUBLIC_PATH
					}/#/information/zgc/dcf12023622f490e9c14636d38891142?fromType=${showHead}`;
				} else {
					window.location.href = `https://localhost${
						import.meta.env.VITE_PUBLIC_PATH
					}/#/information/zgc/dcf12023622f490e9c14636d38891142?fromType=${showHead}`;
				}
			} else {
				if (import.meta.env.VITE_PUBLIC_PATH == '/wg-agent-client-demo') {
					window.location.href = `https://dibrain.localhost${
						import.meta.env.VITE_PUBLIC_PATH
					}/#/information/zgc/b88834c37c7643f7aa78d8fae36e3576?fromType=${showHead}`;
				} else {
					window.location.href = `https://localhost${
						import.meta.env.VITE_PUBLIC_PATH
					}/#/information/zgc/b88834c37c7643f7aa78d8fae36e3576?fromType=${showHead}`;
				}
			}
		},
		handleClose() {
			this.showDialog = false;
		},
		toPageDetails(path) {
			if (path) {
				this.$router.push(path);
			}
		},
	},
};
</script>

<style scoped lang="scss">
::v-deep .van-nav-bar {
	background: #1683a2;
	z-index: 99;
	.van-nav-bar__title,
	.van-icon {
		color: #fff;
	}
}
#nav_bar {
	background: #1683a2;
}
.pt46 {
	padding-top: 46px;
}
.project-list {
	background: #10b8b3;
	height: 100vh;
	overflow: auto;
	box-sizing: border-box;
	.project-list-hd {
		img {
			width: 100%;
			height: 132px;
		}
	}
	.project-list-bd {
		background: #f4f6f9;
		padding: 6px 0;
		border-radius: 12px 12px 0px 0px;
		min-height: calc(100vh - 178px);
		li {
			position: relative;
		}
		.feature {
			margin: 12px;
			width: calc(100% - 24px);
			height: 152px;
		}
		.cont {
			position: absolute;
			top: 0;
			bottom: 0;
			z-index: 10;
			left: 16px;
			right: 16px;
			padding: 16px;
			.tit {
				font-weight: 600;
				font-size: 18px;
				color: #313436;
				line-height: 24px;
				display: flex;
				align-items: center;
				&-txt {
					font-family: MiSans, MiSans;
					font-weight: 600;
					font-size: 18px;
					color: #494c4f;
					line-height: 24px;
					margin-left: 10px;
				}
			}
			img {
				width: 48px;
				display: inline;
			}
		}
		.btn-list {
			overflow: hidden;
			margin: 20px 0 0 0;
		}
		.btn {
			height: 48px;
			background: #169e9a;
			border-radius: 12px;
			text-align: center;
			// line-height: 48px;
			width: 40%;
			float: left;
			// margin: 0 5%;
			font-size: 16px;
			color: #fff;
			padding: 13px 0;

			.icon {
				position: relative;
				right: 6px;
				top: 2px;
			}
		}
		.btn-one {
			width: 46%;
			margin: 0 2%;
		}
		.btn-big {
			width: 100%;
			background: rgba(255, 255, 255, 0.8);
			border-radius: 12px;
			color: #313436;
		}
	}
}

::v-deep .van-dialog {
	background: linear-gradient(270deg, #31cdca 0%, #169e9a 100%);
}
.van-name {
	font-family: MiSans, MiSans;
	font-weight: 400;
	font-size: 16px;
	color: #313436;
	line-height: 24px;
	text-align: justify;
	background: #ffffff;
	border-radius: 12px;
	position: relative;
	margin: 56px 4px 4px 4px;
	padding: 7px 16px;
}

::v-deep .van-dialog__header {
	display: none;
}
::v-deep .van-dialog__content {
	.talent-bg {
		position: absolute;
		top: 0;
	}
	.close-icon {
		position: absolute;
		font-size: 20px;
		right: 20px;
		top: 16px;
	}
}

.btn-bg {
	width: 100%;
	margin: 16px 0 10px 0;
	border-radius: 12px;
	::v-deep .van-button__text {
		font-size: 16px;
	}
}
</style>
