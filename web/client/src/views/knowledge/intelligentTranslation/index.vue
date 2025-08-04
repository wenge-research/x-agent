<template>
	<div class="intelligent-translation">
		<div class="intelligent-translation-head">
			<img class="logo" src="/@/assets/intelligentTranslation/logo.png" alt="" />
			<div class="user-info">
				<div class="name">欢迎您，admin</div>
				<img class="avatar" src="/@/assets/intelligentTranslation/user.png" alt="" />
			</div>
		</div>
		<div class="intelligent-translation-content">
			<div class="box">
				<ul class="tabs">
					<li class="tabs-items" :class="[tabs == 'text' && 'selected']" @click="tabsClick('text')">文本翻译</li>
					<li class="tabs-items" :class="[tabs == 'file' && 'selected']" @click="tabsClick('file')">文档翻译</li>
				</ul>
				<div class="translation">
					<!-- 文本翻译 -->
					<TextTranslation v-if="tabs == 'text'" />
					<!-- 文档翻译 -->
					<FileTranslation v-if="tabs == 'file'" />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import TextTranslation from './textTranslation.vue';
import FileTranslation from './fileTranslation.vue';

export default {
	components: {
		TextTranslation,
		FileTranslation,
	},
	data() {
		return {
			tabs: 'text',
		};
	},
	methods: {
		tabsClick(tabs) {
			this.tabs = tabs;
		},
	},
};
</script>

<style lang="scss" scoped>
.intelligent-translation {
	width: 100vw;
	height: 100vh;
	background: url('/@/assets/intelligentTranslation/bg.png') no-repeat;
	background-size: 100% 100%;
	position: relative;
	&-head {
		padding: 0 34px 0 32px;
		height: 88px;
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: space-between;
		.logo {
			width: 173px;
			height: 40px;
		}
		.user-info {
			display: flex;
			align-items: center;
			.name {
				font-family: MiSans, MiSans;
				font-size: 14px;
				color: #434649;
			}
			.avatar {
				width: 24px;
				height: 24px;
				margin-left: 8px;
			}
		}
	}
	&-content {
		height: calc(100vh - 88px);
		width: 100%;
		padding: 8px 152px 56px;
		display: flex;

		.box {
			height: 100%;
			width: 100%;
			background: #fff;
			border-radius: 16px;
			padding: 20px 24px;

			.tabs {
				display: flex;
				align-items: center;
        margin-bottom: 28px;
				&-items {
					margin-right: 12px;
					width: 88px;
					height: 40px;
					line-height: 40px;
					text-align: center;
					background: #f2f5fa;
					border-radius: 8px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 16px;
					color: #828894;
					cursor: pointer;
				}
				.selected {
					background: #d1e0fe;
					color: #1c50fd;
					font-weight: 600;
				}
			}
      .translation {
        width: 100%;
        height: calc(100% - 40px - 28px);
      }
		}
	}
}
</style>