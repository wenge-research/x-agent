<template>
	<div class="outer">
		<div class="info-box">
			<div class="title-box">
				<div class="close">
					<van-icon name="arrow-left" color="#333" @click="backHome" size="20" />
				</div>
				<div class="title">了解民治</div>
			</div>
		</div>
		<div class="tab">
			<div class="tab-item" v-for="(item,inx) in permissionList" :key="inx" @click="addTab(inx)">
				<img :src="index===inx?item.tabOn:item.tab">
				
			</div>
		</div>
		<div class="main-ctn">
			<div  v-if="index==0">
				<div>
					<img src="/src/assets/homePageMz/understand/understand-1-1.png" />
				</div>
				<div>
					<img src="/src/assets/homePageMz/understand/understand-1-2.png" />
				</div>
			</div>
			<div v-if="index==1">
				<div>
					<img src="/src/assets/homePageMz/understand/understand-2-1.png" />
				</div>
				<div>
					<img src="/src/assets/homePageMz/understand/understand-2-2.png" />
				</div>
				<div>
					<img src="/src/assets/homePageMz/understand/understand-2-3.png" />
				</div>
				<div>
					<img src="/src/assets/homePageMz/understand/understand-2-4.png" />
				</div>
				<div>
					<img src="/src/assets/homePageMz/understand/understand-2-5.png" />
				</div>
				<div>
					<img src="/src/assets/homePageMz/understand/understand-2-6.png" />
				</div>
			</div>
			<div v-if="index==2">
				<div>
					<img src="/src/assets/homePageMz/understand/understand-3-1.png" />
				</div>
				<div>
					<img src="/src/assets/homePageMz/understand/understand-3-2.png" />
				</div>
			</div>
		</div>
	</div>
</template>
<script lang="ts" setup>
	import { ref, onBeforeMount } from 'vue';
	import { useRouter, useRoute } from 'vue-router';
	import image1 from '/src/assets/homePageMz/understand/tab-icon01-on.png';
	import image2 from '/src/assets/homePageMz/understand/tab-icon01.png';
	import image3 from '/src/assets/homePageMz/understand/tab-icon02-on.png';
	import image4 from '/src/assets/homePageMz/understand/tab-icon02.png';
	import image5 from '/src/assets/homePageMz/understand/tab-icon03-on.png';
	import image6 from '/src/assets/homePageMz/understand/tab-icon03.png';
	const router = useRouter();
	const route = useRoute();
	const searchText = ref('');
	const index = ref(0);
	const optionList = ref([]);
	const listLoginOne = ref([]);
	const optionListOne = ref([]);
	const listLogin = ref([]);
	const permissionList = ref([
		{
			tabOn:image1,
			tab:image2
	    },
		{
			tabOn:image3,
			tab:image4
		},
		{
			tabOn:image5,
			tab:image6
		}
	]);
	onBeforeMount(() => { });
	const backHome = () => {
		router.push(`/homePageMz`);
	};
    const addTab = (inx) => {
		index.value  = inx
	}
	
</script>
<style lang="scss" scoped>
	.outer {
		width: 100%;
		// height: 100%;
		background-color: #FEFEFE;

		.info-box {
			width: 100%;
			height: 250px;
			background: url('/src/assets/homePageMz/understand/understand.png') top 40px left;
			background-repeat: no-repeat;
			background-size: 100% 210px;
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
					width: 100%;
					text-align: center;
				}
			}
		}

		.main-ctn {
			width: 100%;
			height: 60vh;
			overflow-y: auto;
			padding: 0 12px;
			box-sizing: border-box;

		}

		.flex {
			display: flex;
			align-items: center;
			justify-content: space-between;
			width: 100%;
			padding: 18px 0;
			border-bottom: 1px solid rgba(0, 0, 0, 0.12);
		}

        .tab{
			display: flex;
			align-items: center;
			justify-content: space-between;
			margin: 20px auto;
			.tab-item{
				text-align: center;
				width: 33%;
			}
			img{
				height: 60px;
				display: inline-block;
			}
		}

		.nodata {
			margin: 100px auto 0;
			height: 100px;
		}
	}
</style>