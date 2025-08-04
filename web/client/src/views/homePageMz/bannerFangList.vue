<template>
	<div class="outer">
		<div class="info-box">
			<div class="title-box">
				<van-icon name="arrow-left" color="#fff" size="20" @click="backHome" />
			</div>
		</div>

		<div class="main-ctn">
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/fang/4fang2.png" alt="">

				<div class="prerequisite" @click="goAppendix(5)"></div>
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/fang/4fang3.png" alt="">
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/fang/4fang4.png" alt="">

				<div class="total" @click="goAppendix(3)"></div>
				<div class="project" @click="goAppendix(4)"></div>

				<div class="methods">
					<a href="https://www.sz.gov.cn/szzt2010/yhyshj/yszc/content/post_11323990.html" target="_blank"></a>
				</div>

				<div class="excellent">
					<a href="https://www.szlhq.gov.cn/lhrlzyj/gkmlpt/content/9/9310/post_9310043.html#14103"
						target="_blank"></a>
				</div>
				<div class="bussiness">
					<a href="https://www.szlhq.gov.cn/wgkzl/jcgk/jchgk/zcfg_124769/content/mpost_9153163.html"
						target="_blank"></a>
				</div>
				<div class="support">
					<a href="https://www.szlhq.gov.cn/lhxinqu/fwztgb/kjcx37/zyzc70/content/post_8579700.html"
						target="_blank"></a>
				</div>
			</div>
		</div>
	</div>
</template>
<script lang="ts" setup>
	import { ref, onBeforeMount } from 'vue';
	import { useRouter, useRoute } from 'vue-router';
	const router = useRouter();
	const route = useRoute();
	const searchText = ref('');
	const optionList = ref([]);
	const listLoginOne = ref([]);
	const optionListOne = ref([]);
	const listLogin = ref([]);
	onBeforeMount(() => { });
	const backHome = () => {
		router.push(`/homePageMz`);
	};

	const goAppendix = (index : number) => {
		router.push({
			path: "/appendix",
			query: {
				appendix: index
			}
		})
	}
</script>
<style lang="scss" scoped>
	.outer {
		width: 100%;
		height: 100%;
		background-color: #f1f5eb;

		.info-box {
			width: 100%;
			height: 180px;
			background-image: url('/src/assets/homePageMz/fang/4fang1.png');
			background-repeat: no-repeat;
			background-size: 100% 180px;
			display: flex;
			justify-content: space-between;
			padding: 0 20px;

			.title-box {
				width: 100%;
				height: 48px;
				display: flex;
				align-items: center;
				position: relative;

				.title {
					font-family: MiSans, MiSans;
					font-weight: 500;
					font-size: 18px;
					color: #333333;
					line-height: 24px;
					margin-left: 8px;
				}
			}
		}

		.main-ctn {
			width: 100%;
			padding: 0 12px;
			// height: calc(100% - 200px);
			overflow-y: auto;
			box-sizing: border-box;

			.list-ctn {
				width: 100%;
				position: relative;

				img {
					width: 100%;
				}

				a {
					display: block;
					width: 100%;
					height: 100%;
				}

				.prerequisite {
					width: calc(100% - 24px);
					height: 35px;					
					position: absolute;
					bottom: 1.5%;
					left: 50%;
					transform: translateX(-50%);
				}

				.total {
					width: calc(100% - 24px);
					height: 35px;					
					position: absolute;
					top: 18.1%;
					left: 50%;
					transform: translateX(-50%);
				}

				.project {
					width: calc(100% - 24px);
					height: 35px;					
					position: absolute;
					top: 20.5%;
					left: 50%;
					transform: translateX(-50%);
				}

				.methods {
					width: 100%;
					height: 65px;					
					position: absolute;
					bottom: 18.4%;
					left: 0;
				}

				.excellent {
					width: 100%;
					height: 70px;					
					position: absolute;
					bottom: 14%;
					left: 0;
				}

				.bussiness {
					width: 100%;
					height: 70px;					
					position: absolute;
					bottom: 9.5%;
					left: 0;
				}

				.support {
					width: 100%;
					height: 70px;					
					position: absolute;
					bottom: 4.6%;
					left: 0;
				}

			}
		}
	}

	.flex {
		display: flex;
		align-items: center;
		justify-content: space-between;
		width: 100%;
		padding: 18px 0;
		border-bottom: 1px solid rgba(0, 0, 0, 0.12);
	}

	::v-deep .van-dropdown-menu__bar {
		box-shadow: 0 0 0 transparent;

		.van-dropdown-menu__title:after {
			border-color: transparent transparent #646479 #646479;
		}

		.van-dropdown-menu__title--active:after {
			border-color: transparent transparent #1989fa #1989fa;
		}
	}

	.dropMenu {
		::v-deep .van-popup {
			padding-top: 50px;
			border-top: 1px solid rgba(0, 0, 0, 0.12);
		}

		.dropdown_search {
			width: 90%;
			position: absolute;
			top: 10px;
			left: 5%;

			.searchInput {
				border-radius: 18px;
				background: #f7f8fa;
				border: 1px solid #f7f8fa;
			}
		}
	}

	.nodata {
		margin: 100px auto 0;
		height: 100px;
	}
</style>