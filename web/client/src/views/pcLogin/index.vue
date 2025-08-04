<template>
	<div class="login-view">
		<div class="content">
			<div class="logo"></div>
			<div class="con">
				<!-- <div class="chart">
					<img src="/src/assets/images/lg-logo.png" alt="" />
				</div> -->

				<div class="form" style=" margin: 80px; ">
					<div class="title">系统登录</div>
					<w-form :model="loginForm" :rules="loginRules" ref="loginForm">
						<w-form-item prop="username">
							<w-input v-model="loginForm.username" suffix-icon="w-icon-user-solid"></w-input>
						</w-form-item>
						<w-form-item prop="password">
							<w-input type="password" v-model="loginForm.passwordStr" suffix-icon="w-icon-lock" autocomplete="off"></w-input>
						</w-form-item>
					</w-form>
					<w-button type="primary" @click="handleLogin">登录</w-button>
				</div>
			</div>
		</div>
	</div>
</template>
<script setup lang="ts" name="logim">
import { ref } from 'vue';

import { userInfo } from '/@/api/personal';
import { Message } from 'winbox-ui-next';
import { useRouter } from 'vue-router';
import md5 from 'js-md5';
const router = useRouter();
const loginForm: any = ref({
	username: 'admin',
	password: '',
	passwordStr: '',
});
const handleLogin = async () => {
	loginForm.value.password = md5(loginForm.value.passwordStr);
	const res = await userInfo({
		password: loginForm.value.password,
		username: loginForm.value.username,
	});
	const { code, data, msg } = res;
	if ('000000' === code) {
		const accessToken = data.accessToken;
		const passwordExpiredTime = data.passwordExpiredTime;
		sessionStorage.setItem('wxAccessToken', accessToken);
		sessionStorage.setItem('userId', data.user.id);
		if (passwordExpiredTime) {
			Message.success(passwordExpiredTime);
		} else {
			Message.success('登录成功');
			if (sessionStorage.getItem('curUrl')) {
				window.location.href = sessionStorage.getItem('curUrl');
			} else {
				router.push({
					path: '/knowledgeDetails/zgc',
				});
			}
		}
	} else {
		Message.warning(msg);
	}
};
</script>

<style lang="scss" scoped>
.login-view {
	width: 100%;
	height: 100%;
	background: url('/@/assets/images/login.png') no-repeat;
	background-size: 100% 100%;
	display: flex;
	justify-content: center;
	align-items: center;

	.content {
		width: 1200px;
		height: 760px;
		display: flex;
		justify-content: space-between;
		border-radius: 8px;

		.logo {
			width: 720px;
			background: url('/@/assets/images/login-logo.png') no-repeat;
			background-size: 100% 100%;
		}

		.con {
			flex: 1;
			background: #ffffff;
			padding: 0 80px;
			box-sizing: border-box;

			.chart {
				// width: 196px;
				height: auto;
				margin: 73px 0 80px 0;
				box-sizing: border-box;

				img {
					height: 48px;
					// width: 100%;
				}
			}

			.form {
				display: flex;
				justify-content: center;
				width: 320px;
				flex-direction: column;

				.title {
					font-weight: bold;
					font-size: 24px;
					color: #383d47;
					line-height: 28px;
					margin-bottom: 32px;
				}
			}

			.w-input {
				width: 320px;
			}
		}
	}
}
:deep(.w-form-item-label-col) {
	display: none;
}
:deep(.w-form-item-wrapper-col) {
	flex: 1;
	width: 100%;
}
</style>
