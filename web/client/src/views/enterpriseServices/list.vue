<template>
	<div @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		<div v-if="showList">
			<van-nav-bar title="找项目" left-text="" fixed left-arrow @click-left="goBack" />
			<div class="project-list" ref="projectList">
				<div class="project-list-hd">
					<img class="feature" src="/src/assets/enterpriseServices/project.png" alt="" />
				</div>
				<div class="project-list-bd">
					<van-tabs v-model:active="active" class="van-tabs" @change="clickTab">
						<van-tab v-for="(item, index) in columnList" :title="item.columnName" :key="index">
							<div v-for="(el, i) in subList" :key="i" class="list" @click="toPageDetail(el)">
								<div class="list-hd">
									<img class="feature" :src="el.thumbnails[0].thumbnailUrl" alt="" />
								</div>
								<div class="list-bd">{{ el.title }}</div>
								<div class="list-ft">{{ el.author }}</div>
							</div>
						</van-tab>
					</van-tabs>
				</div>
			</div>
		</div>
		<div v-else>
			<van-nav-bar title="项目详情" left-text="" fixed left-arrow @click-left="showList = true" />
			<div class="project-detail">
				<div class="detail-cont" v-html="content"></div>
			</div>
		</div>
	</div>
</template>

<script>
import { Tab, Tabs, NavBar } from 'vant';
import { getColumnList, getStoryByColumnId, getStoryById } from '/@/api/enterpriseServices';

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
			columnList: [],
			activeList: [],
			subList: [],
			showList: true,
			content: '',
			active: 0,
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
		this.getColumnList();
		this.addScrollListener();
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
		toPageDetail(el) {
			this.content = el.content;
			this.showList = false;
		},
		clickTab() {
			this.getList();
		},
		addScrollListener() {
			const projectList = this.$refs.projectList;

			projectList.addEventListener('scroll', this.handleScroll);
		},
		removeScrollListener() {
			const projectList = this.$refs.projectList;
			projectList.removeEventListener('scroll', this.handleScroll);
		},
		handleScroll() {
			const projectList = this.$refs.projectList;
			const vanTabs = document.querySelector('.van-tabs__wrap');

			if (projectList.scrollTop > 132) {
				vanTabs.classList.add('fixed');
			} else {
				vanTabs.classList.remove('fixed');
			}
		},
		getList() {
			getStoryByColumnId({
				columnId: this.activeList[this.active],
			})
				.then((res) => {
					if (res.code == '000000') {
						this.subList = res.data;
					}
				})
				.catch((err) => {});
		},
		getColumnList() {
			getColumnList({})
				.then((res) => {
					if (res.code == '000000') {
						this.columnList = res.data;
						this.activeList = res.data.map((el) => el.columnId);
						this.getList();
					}
				})
				.catch((err) => {});
		},
		toPage1() {
			sessionStorage.setItem('pointId', '');
			this.pointId = sessionStorage.getItem('pointId');
		},
		toPage2() {
			sessionStorage.setItem('pointId', '');
			this.pointId = sessionStorage.getItem('pointId');
		},
		homeBack() {
			sessionStorage.setItem('pointId', '');
			this.pointId = sessionStorage.getItem('pointId');
		},
		comeBack() {
			window.location.href = 'https://dibrain.localhost/wg-agent-client/#/homePage/zgc';
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
#nav_bar {
	background: #1683a2;
}
.project-list {
	background: #10b8b3;
	height: 100vh;
	overflow: auto;
	padding-top: 46px;
	box-sizing: border-box;
	.project-list-hd {
		img {
			width: 100%;
		}
	}
	.project-list-bd {
		background: #f4f6f9;
		padding: 20px 0;
		border-radius: 12px 12px 0px 0px;
		min-height: calc(100vh - 132px);
		::v-deep .van-tabs__wrap {
			background: #f4f6f9;
			overflow-x: auto;
			&.fixed {
				position: fixed;
				z-index: 1;
				top: 42px;
				width: 100%;
				padding: 20px 0 20px 0;
				height: 80px;
			}
		}
		::v-deep .van-tabs__nav {
			background: none;
			.van-tabs__line {
				display: none;
			}
		}
		::v-deep .van-tab {
			border-radius: 20px;
			background: #fff;
			height: 40px;
			line-height: 40px;
			color: #313436;
			margin: 0 6px;
			width: fit-content;
			flex: none;
			padding: 0 12px;
			&.van-tab--active {
				background: #ddedef;
				color: #169e9a;
			}
		}
		.list {
			margin: 10px 12px 20px;
			border-radius: 8px 8px 0px 0px;
		}
		.list-hd {
			position: relative;
			img {
				width: 100%;
				border-radius: 8px 8px 0px 0px;
			}
		}
		.list-bd {
			font-family: MiSans, MiSans;
			font-weight: 500;
			font-size: 16px;
			color: #313436;
			line-height: 20px;
			text-align: justify;
			font-style: normal;
			margin: 8px 0;
		}
		.list-ft {
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 14px;
			color: #797991;
			line-height: 20px;
			text-align: justify;
			font-style: normal;
		}
	}
}
.project-detail {
	background: #fff;
	height: 100vh;
	overflow-y: auto;
	overflow-x: hidden;
	.detail-cont {
		padding: 60px 0 0;
		::v-deep img {
			width: 100%;
			transform: scale(1.2);
		}
	}
}
</style>
