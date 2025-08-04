<template>
	<div class="layout-footer pb15">
		<div class="layout-footer-warp">
			<div>we-design-pro</div>
			<div class="mt5">北京市 中科闻歌公司版权所有</div>
		</div>
	</div>
</template>

<script setup lang="ts" name="layoutFooter">
// 此处需有内容（注释也得），否则缓存将失败
</script>

<style scoped lang="scss">
.layout-footer {
	width: 100%;
	display: flex;
	&-warp {
		margin: auto;
		color: var(--secondary-text-color);
		text-align: center;
		animation: error-num 0.3s ease;
	}
}
</style>
