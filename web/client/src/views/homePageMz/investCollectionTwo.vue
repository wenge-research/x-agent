<template>
	<div class="outer">
		<div class="info-box">
			<div class="title-box">
				<iconpark-icon name="arrow-left-wide-line" color="#494C4F" @click="backHome" size="20"></iconpark-icon>
				<div class="title">咨询留言</div>
			</div>
		</div>
		<div class="line"></div>
		<div class="form-ctn">
			<div class="form-item">
				<div class="form-label">
					<span class="label">姓名</span>
					<span class="required">*</span>
				</div>

				<div class="form-input">
					<input type="text" class="input" placeholder="请输入姓名" v-model="investForm.name" />
				</div>
			</div>
            <div class="form-item">
            	<div class="form-label">
            		<span class="label">公司名称</span>
            		<span class="required">*</span>
            	</div>
            
            	<div class="form-input">
            		<input type="text" class="input" placeholder="请输入公司名称" v-model="investForm.companyName" />
            	</div>
            </div>
			<div class="form-item">
				<div class="form-label">
					<span class="label">联系方式</span>
					<span class="required">*</span>
				</div>
			
				<div class="form-input">
					<input type="text" class="input" placeholder="请输入联系方式" v-model="investForm.contactInformation" />
				</div>
			</div>
			<div class="form-item">
				<div class="form-label">
					<span class="label">咨询内容</span>
					<span class="required">*</span>
				</div>

				<div class="form-input">
					<textarea class="textarea" placeholder="请输入咨询内容" v-model="investForm.content"></textarea>
				</div>
			</div>

			

			<div class="form-item">
				<div class="btn" @click="submit">提交</div>
			</div>
		</div>
	</div>
</template>


<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { addLhmzMessage } from '/@/api/llmz';
import { ElMessage } from 'element-plus';

const router = useRouter();

const backHome = () => {
	router.push(`/homePageMz`);
};

// 投资线索收集
interface Invest {
	name: string;
	content: string;
	contactInformation: string;
	companyName: string;
}
const investForm = ref<Invest>({
	name: '',
	content: '',
	contactInformation: '',
	companyName: '',
});

const getAddInvestmentLead = (data) => {
	addLhmzMessage(data).then((res) => {
		if (res.code == '000000') {
			console.log(res);
			ElMessage({
				type: 'success',
				message: res.msg,
			});
			router.push(`/homePageMz`);
			investForm.value = {
				name: '',
				content: '',
				contactInformation: '',
				companyName: '',
			};
		}
	});
};

const submit = () => {
	const { name, content, contactInformation, companyName } = investForm.value;
	if (!name || !content || !contactInformation || !companyName) {
		ElMessage({
			type: 'warning',
			message: '请完整填写信息',
		});
		return;
	}
	getAddInvestmentLead({ ...investForm.value });
};
</script>

<style lang="scss" scoped>
.outer {
	width: 100%;
	height: 100%;
	// background-color: #f6f8fa;
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

	.form-ctn {
		width: 100%;
		padding: 0 12px;
		margin-top: 24px;
		box-sizing: border-box;
		padding-bottom: 4px;

		.form-item {
			width: 100%;
			position: relative;
			margin-bottom: 16px;
			.form-label {
				width: 100%;
				height: 24px;
				font-size: 0;
				font-family: MiSans Regular;

				.label {
					display: inline-block;
					line-height: 24px;
					color: #313436;
					font-size: 16px;
				}

				.required {
					display: inline-block;
					line-height: 20px;
					color: #ff0000;
					font-size: 16px;
				}
			}

			.form-input {
				width: 100%;
				margin-top: 8px;
				display: flex;
				align-items: center;
				background-color: #f4f6f9;
				border-radius: 8px;
				.input {
					flex: 1;
					width: 100%;
					height: 48px;
					padding: 12px;
					border-radius: 8px;
					box-sizing: border-box;
					outline: none;
					background-color: rgb(0, 0, 0, 0);

					&::placeholder {
						color: #b4bccc;
						font-size: 16px;
					}
				}

				.tips {
					height: 24px;
					line-height: 24px;
					color: #313436;
					font-family: MiSans Regular;
					font-size: 16px;
					margin-right: 20px;
				}

				.textarea {
					width: 100%;
					height: 176px;
					background-color: rgb(0, 0, 0, 0);
					padding: 12px;
					border-radius: 8px;
					box-sizing: border-box;
					resize: none;

					&::placeholder {
						color: #b4bccc;
						font-size: 16px;
					}
				}
			}

			.btn {
				width: 100%;
				height: 48px;
				background: #169e9a;
				border-radius: 8px;
				line-height: 48px;
				text-align: center;
				font-family: MiSans Medium;
				font-weight: 500;
				color: #fff;
			}
		}
	}
}
</style>