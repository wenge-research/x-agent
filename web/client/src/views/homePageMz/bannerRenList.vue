<template>
	<div class="outer">
		<div class="info-box">
			<div class="title-box">
				<van-icon name="arrow-left" color="#fff" size="20" @click="backHome" />
			</div>
		</div>

		<div class="main-ctn">
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/ren/1ren2.png" alt="" />

				<div class="phone">
					<a href="tel:075581718836"></a>
				</div>
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/ren/1ren3.png" alt="" />

				<div class="methods">
					<a href="https://www.szlhq.gov.cn/bmxxgk/rlzyj/zcfg_166996/zcwj_166997/content/post_9310060.html"
						target="_blank"></a>
				</div>

				<div class="operate">
					<a href="https://www.szlhq.gov.cn/bmxxgk/rlzyj/zcfg_166996/zcwj_166997/content/post_9458816.html"
						target="_blank"></a>
				</div>

				<div class="skill">
					<a href="https://www.szlhq.gov.cn/lhrlzyj/gkmlpt/content/9/9310/post_9310043.html#14103"
						target="_blank"></a>
				</div>
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/ren/1ren4.png" alt="" />

				<div class="support">
					<a href="https://www.szlhq.gov.cn/bmxxgk/rlzyj/zcfg_166996/zcwj_166997/content/post_10445890.html"
						target="_blank"></a>
				</div>
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/ren/1ren5.png" alt="" />

				<div class="excellent">
					<a href="https://www.szlhq.gov.cn/xxgk/zcfg/qgfxwj/qzcxwj/content/post_9481677.html"
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
</script>
<style lang="scss" scoped>
	.outer {
		width: 100%;
		height: 100%;
		background-color: #e2f6ff;

		.info-box {
			width: 100%;
			height: 180px;
			background-image: url('/src/assets/homePageMz/ren/1ren1.png');
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
			// height: calc(100% - 200px);
			overflow-y: auto;
			padding: 0 12px;
		}

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

			.phone {
				width: 100%;
				height: 60px;
				
				position: absolute;
				top: 25%;
				left: 0;
			}

			.methods {
				width: 100%;
				height: 65px;
				
				position: absolute;
				top: 38.6%;
				left: 0;
			}

			.operate {
				width: 100%;
				height: 70px;
				
				position: absolute;
				top: 43%;
				left: 0;
			}

			.skill {
				width: 100%;
				height: 70px;
				
				position: absolute;
				bottom: 0.8%;
				left: 0;
			}

			.support {
				width: 100%;
				height: 65px;
				
				position: absolute;
				bottom: 0px;
				left: 0;
			}

			.excellent {
				width: 100%;
				height: 70px;
				
				position: absolute;
				bottom: 3.6%;
				left: 0;
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