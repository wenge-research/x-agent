<template>
	<div>
		<div class="left_box flex">
			<div class="flex">
				<div class="img-box">
					<img src="/src/assets/zc/left.png" alt="" />
				</div>
				<div class="left_title">
					<img src="/src/assets/img/wenda.png" alt="" />
				</div>
			</div>
			<div class="time_box">
				<div style="color:rgba(24, 27, 73, 1);margin-top: -4px;">{{ time }}</div>
				<div style="font-size: 16px">{{ date }}</div>
			</div>
		</div>
	</div>
	
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { formatDate } from '/@/utils/formatTime';
const time = ref('');
const date = ref('');

onMounted(() => {
	setInterval(() => {
		date.value = formatDate(new Date(), 'YYYY/mm/dd');
		time.value = formatDate(new Date(), 'HH:MM:SS');
	}, 1000);
});
</script>

<style scoped lang="scss">
.left_box {
	padding: 41px 130px 0;
	justify-content: space-between;
}
.img-box {
	width: 160px;
	img {
		width: 160px;
	}
}
.left_title {
	color: #355eff;
	width: 228px;
	margin-top: 15px;
	img {
		width: 228px;
		height: 100px;
	}
}
.sub_title {
	font-size: 24px;
	font-family: PingFangSC-Regular, PingFang SC;
	font-weight: 400;
	color: #646479;
	margin-top: 10px;
}
.time_box {
	height: 56px;
	padding: 0 8px;
	margin-top: 16px;
	font-size: 24px;
	font-family: DINPro-Medium, DINPro;
	font-weight: 500;
	color: rgba(24, 27, 73, 1);
	border-right: 1px solid rgba(24, 27, 73, 1);
}
</style>
