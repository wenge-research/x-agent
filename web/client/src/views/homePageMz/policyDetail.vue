<template>
	<div class="outer">
		<div class="info-box">
			<div class="title-box">
				<iconpark-icon name="arrow-left-wide-line" color="#494C4F" @click="backHome" size="20"></iconpark-icon>
				<div class="title">详情</div>
			</div>
		</div>
		<div class="line"></div>

		<div class="policy-card">
			<p class="policy-title">{{ policyObj.region }}</p>
			<div class="intro">
				<div class="type">{{ policyObj.industryType }}</div>
				<div class="area">
					<!-- <p>市工业信息化局</p>
					<div class="gap-line"></div> -->
					<p>{{ policyObj.regionLevel }}</p>
				</div>
			</div>
		</div>

		<!-- 主要奖项 -->
		<div class="reward-ctn">
			<!-- <div class="count-ctn">
				<div class="count-line"></div>
				<div class="count-word">主要奖项 （1/5）</div>
			</div> -->

			<div class="reward-swiper" ref="swiperRef">
				<van-swipe :loop="true" :show-indicators="true" :width="swiperWidth">
					<van-swipe-item v-for="(item, index) in policyObj.policyRewardInfoList" :key="index + 'reward'">
						<div class="reward-card">
							<div class="before-after" v-show="item.beforeAfter">{{ item.beforeAfter }}</div>
							<div class="title">{{ item.mainAward }}</div>
							<div class="reward-list">
								<p class="type">奖励金额（万元）</p>
								<p class="word">{{ item.rewardAmount }}</p>
							</div>
							<div class="reward-list">
								<p class="type">奖励细则</p>
								<p class="word">
									{{ item.rewardDetails?item.rewardDetails:'暂无' }}
								</p>
							</div>
							<div class="reward-list">
								<p class="type">注意事项</p>
								<p class="word">{{ item.notes? item.notes:'暂无'}}</p>
							</div>
							<div class="reward-list">
								<p class="type">具体条件</p>
								<p class="word">{{ item.specificConditions? item.specificConditions:'暂无'}}</p>
							</div>
							<div class="reward-list">
								<p class="type">受理单位</p>
								<p class="word">{{ item.receivingUnit ? item.receivingUnit:'无'}}</p>
							</div>
						</div>
						<div class="reward-gap"></div>
					</van-swipe-item>
					<!-- <van-swipe-item>
						<div class="reward-card">1</div>
						<div class="reward-gap"></div>
					</van-swipe-item>
					<van-swipe-item>
						<div class="reward-card">1</div>
						<div class="reward-gap"></div>
					</van-swipe-item>
					<van-swipe-item>
						<div class="reward-card">1</div>
						<div class="reward-gap"></div>
					</van-swipe-item> -->

					<template #indicator="{ active, total }">
						<div class="count-ctn">
							<div class="count-line"></div>
							<div class="count-word">主要奖项 （{{ active + 1 }}/{{ total }}）</div>
						</div>
					</template>
				</van-swipe>
			</div>
		</div>
	</div>
</template>


<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { queryAll } from '/@/api/llmz';
const router = useRouter();

const backHome = () => {
	router.push(`/policyCompilation`);
};

const swiperWidth = ref(0);
const swiperHeight = ref(0);
const swiperRef = ref();

const policyObj = ref({});

// const getPolicyList= ()=>{
// 	queryAll().then(res=>)
// }
console.log('################', JSON.parse(sessionStorage.getItem('policyObj')));

policyObj.value = { ...JSON.parse(sessionStorage.getItem('policyObj')) };

onMounted(() => {
	swiperWidth.value = window.innerWidth - 24;
	swiperHeight.value = swiperRef.value.offsetHeight;
});
</script>

<style lang="scss" scoped>
.outer {
	width: 100%;
	height: 100%;
	overflow-x: hidden;
	background-color: #f7f8fa;
	.info-box {
		width: 100%;
		//    height: 180px;
		// background-image: url('/src/assets/homePageMz/fang1.png');
		// background-repeat: no-repeat;
		// background-size: 100% 180px;
		//    display: flex;
		//    justify-content: space-between;
		padding: 0 20px;
		background-color: #fff;
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
				width: 100%;
				text-align: center;
			}
		}
	}

	.line {
		width: 100%;
		height: 1px;
		background-color: #e7e7e7;
	}

	// 政策卡片
	.policy-card {
		width: 100%;
		background-color: #fff;
		padding: 12px;
		box-sizing: border-box;
		margin-bottom: 10px 12px 12px 12px;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		gap: 12px;

		.policy-title {
			width: 100%;
			line-height: 24px;
			color: #1d2129;
			font-family: MiSans Medium;
			font-weight: 500;
			font-size: 18px;
		}

		.intro {
			width: 100%;
			display: flex;
			justify-content: space-between;
			align-items: center;

			.type {
				height: 26px;
				box-sizing: border-box;
				display: inline-block;
				padding: 4px 8px;
				border: 1px solid #169e9a;
				border-radius: 4px;
				line-height: 18px;
				text-align: center;
				color: #169e9a;
				font-family: MiSans Regular;
				font-size: 12px;
			}

			.area {
				display: flex;
				align-items: center;
				height: 20px;
				gap: 12px;
				font-family: MiSans Regular;
				color: #86909c;
				font-size: 14px;

				p {
					height: 20px;
					line-height: 20px;
				}

				.gap-line {
					width: 1px;
					height: 16px;
					background-color: #e7e7e7;
				}
			}
		}
	}

	// 主要奖项
	.reward-ctn {
		width: 100%;
		position: relative;
		margin-top: 20px;
		padding-top: 28px;

		.count-ctn {
			width: 100%;
			height: 16px;
			// padding: 0 12px;
			box-sizing: border-box;
			display: flex;
			align-items: center;
			gap: 8px;
			position: absolute;
			z-index: 66;
			top: -28px;
			left: 0;

			.count-line {
				width: 2px;
				height: 16px;
				background-color: #169e9a;
			}

			.count-word {
				height: 16px;
				line-height: 14px;
				color: #1d2129;
				font-family: MiSans Medium;
				font-weight: 500;
				font-size: 14px;
			}
		}

		.reward-swiper {
			width: 100%;
			padding-left: 12px;
			box-sizing: border-box;
			margin-top: 12px;

			.van-swipe {
				overflow: visible;
				.van-swipe__track {
					height: 100%;

					.van-swipe-item {
						display: flex;

						.reward-card {
							flex: 1;
							background-color: #fff;
							border-radius: 8px;
							padding: 16px 12px;
							position: relative;
							box-sizing: border-box;

							.before-after {
								width: 40px;
								height: 24px;
								line-height: 24px;
								border-radius: 0 8px 0 8px;
								color: #169e9a;
								background: rgba(22, 158, 154, 0.1);
								font-family: MiSans Regular;
								font-size: 12px;
								text-align: center;
								position: absolute;
								top: 0;
								right: 0;
							}

							.title {
								height: 22px;
								line-height: 22px;
								font-family: MiSans Medium;
								font-weight: 500;
								color: #169e9a;
								font-size: 16px;
							}

							.reward-list {
								width: 100%;
								margin-top: 16px;

								.type {
									line-height: 16px;
									font-family: MiSans Regular;
									font-size: 12px;
									color: #86909c;
									margin-bottom: 8px;
								}

								.word {
									line-height: 22px;
									font-family: MiSans Regular;
									font-size: 14px;
									color: #1d2129;
									white-space: pre-wrap;
								}
							}
						}

						.reward-gap {
							width: 8px;
						}
					}
				}
			}
		}
	}
}
</style>