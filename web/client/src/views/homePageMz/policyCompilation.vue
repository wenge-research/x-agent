<template>
	<div class="outer">
		<div class="info-box">
			<div class="title-box">
				<iconpark-icon name="arrow-left-wide-line" color="#494C4F" @click="backHome" size="20"></iconpark-icon>
				<div class="title">政策汇编</div>
			</div>
		</div>

		<div class="search-ctn" id="search-box">
			<div class="search">
				<iconpark-icon name="search-2-line" size="20" color="#1d2129"></iconpark-icon>
				<input type="text" placeholder="搜索政策名/奖项名" v-model="industryParams.queryContent" @input="inputSearch" />
			</div>

			<div class="select">
				<div class="part" @click="showPopup('industry')">
					<div class="label" :class="{ 'label-active': curIndustry != 0 }">
						{{ industryList[curIndustry].value ? industryList[curIndustry].value : '全部产业' }}
					</div>
					<img
						v-if="curIndustry == 0"
						src="/@/assets/homePageMz/policy/arrowDown.png"
						alt=""
						class="arrow"
						:style="{ transform: isShow && curType == 'industry' ? 'rotateZ(180deg)' : 'rotateZ(0)' }"
					/>
					<img
						v-else
						src="/@/assets/homePageMz/policy/arrowDownActive.png"
						alt=""
						class="arrow"
						:style="{ transform: isShow && curType == 'industry' ? 'rotateZ(180deg)' : 'rotateZ(0)' }"
					/>
				</div>
				<div class="part-line"></div>
				<div class="part" @click="showPopup('region')">
					<div class="label" :class="{ 'label-active': curRegion != 0 }">
						{{ regionList[curRegion].value ? regionList[curRegion].value : '区/市级' }}
					</div>
					<img
						v-if="curRegion == 0"
						src="/@/assets/homePageMz/policy/arrowDown.png"
						alt=""
						class="arrow"
						:style="{ transform: isShow && curType == 'region' ? 'rotateZ(180deg)' : 'rotateZ(0)' }"
					/>
					<img
						v-else
						src="/@/assets/homePageMz/policy/arrowDownActive.png"
						alt=""
						class="arrow"
						:style="{ transform: isShow && curType == 'region' ? 'rotateZ(180deg)' : 'rotateZ(0)' }"
					/>
				</div>
			</div>
		</div>
		<div class="popup-ctn" @click="closePopup" v-show="isShow">
			<div class="popup" @click.stop>
				<div
					class="popup-list"
					:class="{ 'active-popup': curType == 'industry' ? curIndustry == index : curRegion == index }"
					v-for="(item, index) in curType == 'industry' ? industryList : regionList"
					:key="item.label + 'industryList'"
					@click.stop="changeTab(index)"
				>
					{{ item.label }}
				</div>
			</div>
		</div>
		<!-- <van-popup v-model:show="show" :style="{ padding: '64px' }" position="top" round>内容</van-popup> -->

		<div class="policy-ctn">
			<div class="list" v-for="(item, index) in policyList" :key="index + 'policyList'" @click="goPolicyDetail(index)">
				<p class="policy-title">{{ item.region }}</p>
				<div class="intro">
					<div class="type">{{ item.industryType }}</div>
					<div class="area">
						<!-- <p>市工业信息化局</p>
						<div class="gap-line"></div> -->
						<p>{{ item.regionLevel }}</p>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { queryIndustryType, queryAll } from '/@/api/llmz';
import { debounce } from 'lodash';

const router = useRouter();

const backHome = () => {
	router.push(`/homePageMz`);
};

// 数据
const policyList = ref([]);

// policyList.value = [{}, {}, {}];

const isShow = ref(false);
const curType = ref('');
const showPopup = (type: string) => {
	isShow.value = true;
	curType.value = type;
};

interface ListType {
	label: string;
	value: string;
}

const industryList = ref<ListType[]>([]);
industryList.value = [
	{
		label: '全部',
		value: '',
	},
	// {
	// 	label: '现代时尚',
	// 	value: '现代时尚',
	// },
	// {
	// 	label: '低空经济',
	// 	value: '低空经济',
	// },
	// {
	// 	label: '软件相关',
	// 	value: '软件相关',
	// },
	// {
	// 	label: '生物医药',
	// 	value: '生物医药',
	// },
];
const curIndustry = ref(0);

const regionList = ref<ListType[]>([]);
regionList.value = [
	{
		label: '全部',
		value: '',
	},
	{
		label: '区级',
		value: '区级',
	},
	{
		label: '市级',
		value: '市级',
	},
];
const curRegion = ref(0);

// 获取产业类型
const getIndustryType = () => {
	queryIndustryType().then((res) => {
		console.log(res);
		industryList.value = [
			{
				label: '全部',
				value: '',
			},
		];
		if (res.code == '000000') {
			res.data.map((item) => {
				industryList.value.push({
					label: item,
					value: item,
				});
			});
		}
	});
};

// 获取产业数据
interface IndustryParams {
	queryContent: string;
	regionLevel: string;
	industryType: string;
}
const industryParams = ref<IndustryParams>({
	queryContent: '',
	regionLevel: '',
	industryType: '',
});
const getIndustryList = (params: IndustryParams) => {
	queryAll(params).then((res) => {
		console.log(res);
		if (res.code == '000000') {
			policyList.value = [...res.data];
		}
	});
};
// 搜索
const inputSearch = debounce(() => {
	getIndustryList({ ...industryParams.value });
}, 500);

onMounted(() => {
	getIndustryType();
	getIndustryList({ ...industryParams.value });
});

const changeTab = (index) => {
	if (curType.value == 'industry') {
		curIndustry.value = index;
		industryParams.value.industryType = industryList.value[index].value;
	} else {
		curRegion.value = index;
		industryParams.value.regionLevel = regionList.value[index].value;
	}
	getIndustryList({ ...industryParams.value });
	closePopup();
};

const closePopup = () => {
	isShow.value = false;
};

const goPolicyDetail = (index: number) => {
	router.push('/policyDetail');
	sessionStorage.setItem('policyObj', JSON.stringify(policyList.value[index]));
};
</script>

<style lang="scss" scoped>
.outer {
	width: 100%;
	height: 100%;
	background-color: #f7f8fa;
	position: relative;
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

	.popup-ctn {
		width: 100%;
		height: calc(100vh - 148px);
		position: fixed;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
		backdrop-filter: blur(2px);

		.popup {
			width: 100%;
			border-radius: 0 0 12px 12px;
			background-color: #fff;
			padding: 12px 12px 16px 12px;
			box-sizing: border-box;
			display: grid;
			grid-template-columns: repeat(2, 1fr);
			gap: 12px;

			.popup-list {
				height: 40px;
				line-height: 40px;
				background-color: #f7f8fa;
				text-align: center;
				border-radius: 8px;
				color: #383d47;
				font-family: MiSans Regular;
			}

			.active-popup {
				background-color: rgb(231, 245, 244);
				font-weight: 500;
				font-family: MiSans Medium;
				color: #169e9a;
			}
		}
	}

	.search-ctn {
		width: 100%;
		padding: 12px;
		box-sizing: border-box;
		background-color: #fff;

		.search {
			width: 100%;
			height: 40px;
			background-color: #f7f8fa;
			border-radius: 8px;
			display: flex;
			padding: 10px 12px;
			box-sizing: border-box;
			gap: 9px;

			img {
				width: 20px;
				height: 20px;
			}

			input {
				flex: 1;
				height: 20px;
				border: none;
				outline: none;
				background: rgba(0, 0, 0, 0);

				&::placeholder {
					color: #c9cdd4;
					font-size: 14px;
					font-family: MiSans Regular;
				}
			}
		}

		.select {
			width: 100%;
			height: 32px;
			margin-top: 8px;
			display: flex;
			align-items: center;

			.part-line {
				width: 1px;
				height: 20px;
				background-color: #e7e7e7;
			}

			.part {
				flex: 1;
				height: 32px;
				display: flex;
				align-items: center;
				justify-content: center;
				gap: 8px;

				.label {
					height: 24px;
					line-height: 24px;
					color: #1d2129;
					font-family: MiSans Medium;
					font-size: 16px;
				}
				.label-active {
					color: #169e9a;
					font-family: MiSans Medium;
					font-weight: 500;
				}

				.arrow {
					width: 16px;
					height: 16px;
				}
			}
		}
	}

	.policy-ctn {
		width: 100%;
		margin-top: 16px;
		padding: 0 12px;
		box-sizing: border-box;
        overflow-y: auto;
		height: 80vh;
		.list {
			width: 100%;
			border-radius: 8px;
			background-color: #fff;
			padding: 12px;
			box-sizing: border-box;
			margin-bottom: 12px;
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			gap: 12px;

			.policy-title {
				width: 100%;
				line-height: 22px;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				-webkit-line-clamp: 2;
				overflow: hidden;
				color: #1d2129;
				font-family: MiSans Medium;
				font-weight: 500;
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
	}
}
</style>