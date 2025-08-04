<template>
	<div class="outer">
		<div class="info-box">
			<div class="title-box">
				<van-icon name="arrow-left" color="#fff" size="20" @click="backHome" />
			</div>
		</div>

		<div class="main-ctn">
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian2.png" alt="" />

				<div class="phone" @click="goAppendix(1)"></div>
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian3.png" alt="" />

				<div class="phone" style="bottom: 1.2%;" @click="goAppendix(1)"></div>
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian4.png" alt="" />
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian5.png" alt="" />
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian6.png" alt="" />
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian7.png" alt="" />
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian8.png" alt="" />
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian9.png" alt="" />
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian10.png" alt="" />
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian11.png" alt="" />
			</div>
			<div class="list-ctn">
				<img src="/@/assets/homePageMz/qian/2qian12.png" alt="" />
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
		background-color: #fff0ed;

		.info-box {
			width: 100%;
			height: 180px;
			background-image: url('/@/assets/homePageMz/qian/2qian1.png');
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

				.phone {
					width: calc(100% - 24px);
					height: 40px;
					position: absolute;
					bottom: 0px;
					left: 50%;
					transform: translateX(-50%);
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