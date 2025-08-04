<template>
	<el-drawer v-model="drawerVisible" direction="rtl" :before-close="handleClose" class="scene elDrawer" size="60%">
		<template v-slot:header>
			<div>
				<div class="title">{{ title }}</div>
				<div class="subtitle">{{ subtitle }}</div>
			</div>
		</template>
		<div class="elDrawer-content">
			<div class="steps">
				<ul>
					<li v-for="(item, index) in stepList" :key="index" class="steps-item">
						<div class="order" :class="[index + 1 == step ? 'order-selected' : '']">{{ index + 1 }}</div>
						<div class="txt" :class="[index + 1 == step ? 'txt-selected' : '']">{{ item.aliasName }}</div>
						<img v-if="index + 1 < stepList.length" class="arrow-right" src="/@/assets/scene/arrow-right.png" alt="" />
					</li>
				</ul>
			</div>

			<!-- currentTabsMatterType == '事项-填写表单' -->
			<div v-if="currentTabsMatterType == '事项-填写表单'" v-loading="formLoading" style="width: 100%; height: 100%">
				<div class="form">
					<div v-if="formData.enterTip" class="enter-tip">{{ formData.enterTip }}</div>
					<div v-if="formData.fillTip" class="fill-tip">{{ formData.fillTip }}</div>
					<div class="form-content">
						<!-- <div class="form-content-title">
						{{ activeForm.title }}
					</div> -->
						<div class="form-content-item" v-for="(item, index) in formList" :key="index">
							<!-- <div class="title-box">
							{{ item.group.name }}
							<div class="btn" v-if="item.type === 'addForm'" @click="addOptions(item.filedName)">添加</div>
						</div> -->
							<div v-for="(subItem, subIndex) in item.filedList" :key="subIndex">
								<div class="form-box">
									<w-form ref="formRef" :model="params" :label-col-props="{ span: 24 }" :wrapper-col-props="{ span: 24 }" label-position="left">
										<w-form-item
											:label="subItem.filedName"
											v-if="!['multilInput'].includes(subItem.formType)"
											:rules="checkingRule(subItem)"
											:field="subItem.requiredFlag == '是' ? subItem.filedCode : null"
											:validate-trigger="['change', 'input']"
										>
											<!-- 单日期表单 -->
											<w-date-picker
												style="width: 100%"
												v-model="params[subItem.filedCode]"
												v-if="subItem.formType === 'datePicker'"
												:disabled="subItem.clientFormStatus == 0"
											/>
											<!-- 日期表单 -->
											<w-range-picker
												v-model="params[subItem.filedCode]"
												v-if="subItem.formType === 'date'"
												:disabled="subItem.clientFormStatus == 0"
											/>
											<!-- 输入表单 -->
											<w-input
												v-if="subItem.formType === 'input'"
												v-model="params[subItem.filedCode]"
												:placeholder="subItem.placeholder"
												:disabled="subItem.clientFormStatus == 0"
											/>
											<!-- 输入表单-textarea -->
											<w-textarea
												v-if="subItem.formType === 'textarea'"
												v-model="params[subItem.filedCode]"
												:placeholder="subItem.placeholder"
												:disabled="subItem.clientFormStatus == 0"
												:auto-size="{ minRows: 3 }"
												:max-length="500"
												show-word-limit
											/>
											<!-- 上传表单 -->
											<w-upload
												v-if="['file', 'image'].includes(subItem.formType)"
												:data="{ field: subItem.filedCode }"
												list-type="picture-card"
												action="/"
												:disabled="subItem.clientFormStatus == 0"
												:show-file-list="false"
												:custom-request="uploadRequest"
											>
												<template #upload-button>
													<!-- 上传提示语 -->
													<div class="prompt" v-if="subItem.prompt">
														<div class="icon">!</div>
														{{ subItem.prompt }}
													</div>
													<div class="upload-box">
														<div class="ready" v-if="params[subItem.filedCode]">
															<img :src="params[subItem.filedCode]" />
														</div>
														<div class="wait" v-else>
															<img src="/@/assets/scene/add-icon.png" alt="" />
															<div v-if="subItem.filedName" class="filedName">{{ subItem.filedName }}</div>
															<div v-if="subItem.tip" class="tip">{{ subItem.tip }}</div>
														</div>
													</div>
												</template>
											</w-upload>
											<!-- 复选表单 -->
											<w-radio-group
												v-if="subItem.formType === 'radio'"
												:disabled="subItem.clientFormStatus == 0"
												v-model="params[subItem.filedCode]"
												@change="handleRadioChange"
											>
												<w-radio :value="option.lable" v-for="option in subItem.optionList" :key="option.filedId">
													{{ option.lable }}
												</w-radio>
											</w-radio-group>

											<!-- 选择器 -->
											<w-select
												v-if="subItem.formType === 'select'"
												v-model="params[subItem.filedCode]"
												:disabled="subItem.clientFormStatus == 0"
												:placeholder="subItem.placeholder"
											>
												<w-option v-for="option in subItem.optionList" :key="option.filedId" :value="option.lable">{{ option.lable }}</w-option>
											</w-select>
										</w-form-item>
										<!-- 填写医疗机构 -->
										<div class="medical" v-if="subItem.formType === 'multilInput'">
											<div class="medical-label">{{ subItem.filedName }}</div>
											<w-form :label-col-props="{ span: 24 }" :wrapper-col-props="{ span: 24 }">
												<!-- v-for="(medicalOption, index) in medicalType === '本市城镇职工医保' ? subItem.options : TEMPORARY_OPTIONS" -->
												<w-form-item
													v-for="(medicalOption, index) in medicalType === '本市城镇职工医保' ? subItem.options : []"
													:key="medicalOption.filedId"
												>
													<w-input
														v-model="params[subItem.filedCode]"
														type="text"
														:disabled="subItem.clientFormStatus == 0"
														:placeholder="subItem.placeholder"
													/>
												</w-form-item>
											</w-form>
										</div>
									</w-form>
								</div>

								<div class="addform-box" v-if="['addFormGzjl', 'addFormZyjn', 'addFormWytc', 'addFormJsjdjzs'].includes(item.type)">
									<div v-for="(labelSon, labelIndex) in item.configs" :key="labelIndex">
										<div class="addform-box-row">
											<div class="addform-box-row-label">{{ labelSon.label }}</div>
											<div v-if="params[labelSon.field]?.length == 0" class="addform-box-row-add" @click="addOptions(item.filedName, item, labelSon)">
												添加
											</div>
										</div>
										<div v-if="params[labelSon.field]?.length !== 0" v-for="(option, optionIndex) in params[labelSon.field]" :key="optionIndex">
											<div class="option">
												<template v-if="['addFormGzjl'].includes(item.type)">
													<div class="companyName">
														{{ option.companyName }}
													</div>
													<div class="mark">
														<div class="occupation">{{ option.occupation }}</div>
														<div class="employmentPeriod">{{ option.employmentPeriod[0] }} - {{ option.employmentPeriod[1] }}</div>
													</div>
													<div class="jobContent">
														{{ option.jobContent }}
													</div>
												</template>
												<template v-if="['addFormZyjn'].includes(item.type)">
													<div class="technicalTitle">
														{{ option.technicalTitle }}
													</div>
													<div class="mark">
														<div class="level">{{ option.level }}</div>
														<div class="manageTime">{{ option.manageTime[0] }}至{{ option.manageTime[1] }}</div>
													</div>
												</template>
												<template v-if="['addFormWytc', 'addFormJsjdjzs'].includes(item.type)">
													<div class="tMark">
														<div class="language">{{ option.language }}</div>
														<div class="level">{{ option.level }}</div>
													</div>
												</template>

												<div class="set" @click="setOptions(item.filedName, option, optionIndex, item, labelSon)">
													<van-icon name="arrow" size="20px" />
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>

					<!-- 添加option -->
					<el-dialog :show-close="false" v-model="optionVisible" width="500" :close-on-press-escape="false" :close-on-click-modal="false">
						<div class="option-box">
							<div class="title">{{ operation === 'add' ? '添加' : '编辑' }}{{ dialogTitle }}</div>
							<div class="con">
								<w-form ref="formOpt" :model="optionParams" :label-col-props="{ span: 24 }" :wrapper-col-props="{ span: 24 }" label-position="left">
									<template v-for="subItem in optionForm" :key="subItem.filedId">
										<w-form-item
											:label="subItem.label"
											v-if="!['multilInput'].includes(subItem.formType)"
											:rules="checkingRule(subItem)"
											:field="subItem.filedCode"
											:validate-trigger="['change', 'input']"
										>
											<!-- 输入表单 -->
											<w-input v-model="optionParams[subItem.filedCode]" v-if="subItem.formType === 'input'" :placeholder="subItem.placeholder" />
											<!-- 日期表单 -->
											<w-range-picker v-model="optionParams[subItem.filedCode]" v-if="subItem.formType === 'date'" />
										</w-form-item>
									</template>
								</w-form>
							</div>

							<div class="btns">
								<div class="btn">
									<van-button color="#149E9A" @click="handleConfirm" type="success">确认</van-button>
								</div>
								<div class="btn">
									<van-button type="default" @click="handleCancel">取消</van-button>
								</div>
								<div class="btn" v-if="operation === 'set'">
									<van-button type="default" @click="remOptions">删除</van-button>
								</div>
							</div>
						</div>
					</el-dialog>

					<!-- 信息声明 -->
					<el-dialog :show-close="false" v-model="statementShow" width="500" top="20px" :close-on-press-escape="false" :close-on-click-modal="false">
						<div class="statement-box">
							<div style="height: calc(100vh - 80px); overflow: auto">
								<MarkdownMessage :text="statement" />
							</div>

							<div class="close" @click="statementShow = false">
								<van-icon name="cross" size="20px" />
							</div>
						</div>
					</el-dialog>
				</div>
				<div class="submit-box">
					<div class="agt">
						<van-checkbox shape="square" checked-color="#1C50FD" v-model="agt">
							我已阅读并同意<span class="statement" @click.stop="statementShow = true">《个人信息收集声明》</span>
						</van-checkbox>
					</div>
					<div class="btns">
						<!-- <div class="btn">
							<van-button color="#149E9A" :loading="submitLoading" @click="handleSubmit" type="success" loading-text="提交中...">提交</van-button>
						</div>
						<div class="btn">
							<van-button type="default" @click="handleClose">取消</van-button>
						</div> -->
						<!-- <el-button plain style="width: 96px; height: 40px;border: 1px solid #2065d6;color: #2065d6;" v-if="step != 1" @click="prev">上一步</el-button> -->
						<!-- <el-button type="primary" style="width: 96px; height: 40px" v-if="index + 1 == step || stepList.length == step" @click="onSubmit"
						>提交</el-button
					> -->
						<el-button v-if="currentTabsMatterType == '事项-填写表单'" type="primary" style="width: 96px; height: 40px" @click="next"
							>下一步</el-button
						>
					</div>
				</div>
			</div>

			<div v-if="currentTabsMatterType == '事项-跳转外网'" class="link-data">
				<div class="enter-tip">{{ linkData.enterTip }}</div>
				<div class="url">
					<img src="/@/assets/scene/link-icon.png" alt="" />
					<div class="link" @click="openTargetPage(linkData.matterRoute)">{{ linkData.matterRoute }}</div>
				</div>
			</div>
		</div>
	</el-drawer>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, onUpdated, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
// api
import { apiGetMatterGuideForm } from '/@/api/scene';
import { submitFormInfo, uploadPic } from '/@/api/chat/index';
import { v4 as uuidv4 } from 'uuid';
// components
const MarkdownMessage = defineAsyncComponent(() => import('./component/markdownMessage.vue'));
import { Message } from 'winbox-ui-next';
// props
interface Props {
	visible?: boolean;
	paramsData?: any;
	title?: string;
	subtitle?: string;
}
const props = defineProps<Props>();
const emits = defineEmits(['closeDrawer']);
// 变量
const drawerVisible = ref(props.visible);
const stepList = ref(props.paramsData);
const activeForm = ref({});
const params = ref({ address: '', birthday: '', user_name: '' });
const optionParams = ref({});
const statement = ref('');
const agt = ref(false);
const optionVisible = ref(false);
const statementShow = ref(false);
const step = ref(1);
// 表单填写文字
const formData = ref({});
const formList = ref([]);
// 类型
const currentTabsMatterType = computed(() => stepList.value[step.value - 1].matterType);
// 上传
const uploadRequest = async (option) => {
	const { data, fileItem } = option;
	console.log('data', data);
	const formData = new FormData();
	formData.append('file', fileItem?.file);
	const res = await uploadPic(formData);
	params.value[data.field] = res.data;
	formRef.value.forEach((n) => {
		n.validate(data.field);
	});
	console.log('params', params.value);
};
const formLoading = ref(false);
// 获取表单信息
const getMatterGuideForm = async (data: any) => {
	const params = {
		sceneId: data.sceneId,
		matterId: data.matterId,
	};
	formLoading.value = true;
	let res = await apiGetMatterGuideForm(params);
	console.log('res', res);
	if (res.code == '000000' && res.data) {
		formData.value = res.data?.matterGuide;
		formList.value = res.data?.formConfigList;
		statement.value = formData.value?.statement;
		console.log('formList.value', formList.value);
		console.log('formData.value', formData.value);
	}
	formLoading.value = false;
};
const checkingRule = (cItem) => {
	if (cItem.checkRuleCode === '2001') {
		return [
			{
				required: true,
				validator: (value, callback) => {
					console.log(value);
					const reg = /^1[3-9]\d{9}$/;
					if (!value) {
						callback('手机号不能为空');
					} else if (!reg.test(value)) {
						callback('请输入正确的手机号');
					}
					callback();
				},
			},
		];
	}
	if (cItem.checkRuleCode === '1001') {
		return [
			{
				required: true,
				validator: (value, callback) => {
					const reg = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}[Xx0-9]$/;
					if (!value) {
						callback('身份证号不能为空');
					} else if (!reg.test(value)) {
						callback('请输入正确的身份证号');
					}
					callback();
				},
			},
		];
	}
	return [{ required: true, message: cItem.prompt }];
};
// 关闭drawer
// const handleClose = () => {
// 	drawerVisible.value = false;
// 	emits('closeDrawer');
// };
const handleClose = () => {
	emits('closeDrawer');
	formRef.value.forEach((item) => {
		item.resetFields();
	});
	if (params.value.medicalInput) {
		params.value.medicalInput = params.value.medicalInput.map((n) => {
			return {
				medicalInput: '',
			};
		});
	}
};
const convertToFieldArray = (obj) => {
	return Object.keys(obj).map((key) => {
		if (key === 'idCardBackPic' || key === 'idCardFrontPic' || key == 'pictureUrl') {
			return { filedId: key, dataValue: obj[key][0].url };
		} else {
			return { filedId: key, dataValue: obj[key] };
		}
	});
};
const route = useRoute();
const matterId = route.params.matterId;
// 提交表单
const submitLoading = ref(false);
const formRef = ref();
const fillTip = ref();
const isNext = ref(true);
const onSubmit = async () => {
	const validationPromises = formRef.value.map(async (form) => {
		const validationResult = await form.validate();
		if (validationResult) {
			throw new Error('请完善表单信息');
		}
	});
	await Promise.all(validationPromises);
	if (!agt.value) {
		Message.warning('请阅读并同意《个人信息收集声明》后再进行提交');
		isNext.value = false;
	} else {
		submitLoading.value = true;
		const items = convertToFieldArray(params.value);
		let uuid = await uuidv4()?.replace(/-/g, '');
		const res = await submitFormInfo({
			items: items,
			matterId: stepList.value[0]?.matterId,
			traceId: uuid,
			applicationId: localStorage.getItem(`${route.params.appId}appId`),
		});
		console.log('res', res);
		if (res.code == '000000') {
			submitLoading.value = false;
			// handleClose();
			Message.success(fillTip.value || res.msg);
			agt.value = false;
			isNext.value = true;
		} else {
			Message.warning(res.msg);
		}
	}
};
const linkData = ref();
// 下一步
const next = async () => {
	await onSubmit();
	if (!isNext.value) return;
	step.value = step.value + 1;
	console.log('currentTabsMatterType', currentTabsMatterType.value);
	if (currentTabsMatterType.value == '事项-填写表单') {
		getMatterGuideForm(stepList.value[step.value - 1]);
	}
	if (currentTabsMatterType.value == '事项-跳转外网') {
		linkData.value = stepList.value[step.value - 1];
	}
};
// 外链跳转
const openTargetPage = (url: any) => {
	window.open(url, 'target');
};
const medicalType = ref('本市城镇职工医保');
const handleRadioChange = (name) => {
	medicalType.value = name;
};

// watch(
// 	() => currentTabsMatterType,
// 	(val) => {
// 		console.log('val',val)
// 		if (val == '事项-填写表单') {
// 			getMatterGuideForm(stepList.value[0]);
// 		}
// 	}
// );
onMounted(() => {
	if (stepList.value?.length && stepList.value[0]?.matterType == '事项-填写表单') {
		getMatterGuideForm(stepList.value[0]);
	}
	if (stepList.value?.length && stepList.value[0]?.matterType == '事项-跳转外网') {
		linkData.value = stepList.value[step.value - 1];
	}
});
</script>

<style lang="scss" scoped>
.title {
	font-family: MiSans, MiSans;
	font-weight: 500;
	font-size: 24px;
	color: #383d47;
	line-height: 32px;
}
.subtitle {
	font-family: MiSans, MiSans;
	font-weight: 400;
	font-size: 16px;
	color: #828894;
	line-height: 20px;
	margin-top: 4px;
}
.elDrawer {
	&-content {
		height: 100%;
		.steps {
			width: 100%;
			height: 48px;
			padding: 0 77px;
			display: flex;
			justify-content: center;
			background: #f4f6f9;
			border-radius: 8px;

			ul {
				width: 100%;
				height: 100%;
				overflow-x: auto;
				overflow-y: hidden;
				display: flex;
				align-items: center;
				// justify-content: center;
				flex-wrap: nowrap;
			}

			&-item {
				flex: 0 0 auto;
				display: flex;
				align-items: center;
				justify-content: center;

				.order {
					width: 28px;
					height: 28px;
					line-height: 28px;
					text-align: center;
					border-radius: 50%;
					background: #b4bccc;
					font-family: MiSans, MiSans;
					font-size: 14px;
					color: #ffffff;
				}
				.order-selected {
					background: #2065d6;
				}
				.txt {
					margin: 0 12px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #383d47;
				}
				.txt-selected {
					font-weight: 600;
				}
				.arrow-right {
					width: 16px;
					height: 16px;
					margin-right: 12px;
				}
			}
		}
		.form {
			margin-top: 28px;
			width: 100%;
			height: calc(100% - 48px - 96px - 32px - 28px);

			.enter-tip {
				font-family: MiSans, MiSans;
				font-weight: 600;
				font-size: 20px;
				color: #383d47;
				line-height: 32px;
				padding: 0 20%;
			}
			.fill-tip {
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 14px;
				color: #383d47;
				line-height: 20px;
				padding: 0 20%;
				// margin-bottom: 24px;
			}
			&-content {
				// background-color: #f6fbfa;
				// border-top-right-radius: 12px;
				// border-bottom-right-radius: 12px;
				// padding: 20px 32px;
				padding: 0 20%;
				box-sizing: border-box;
				width: 100%;
				height: calc(100% - 48px);
				overflow-y: scroll;
				position: relative !important;

				&-title {
					font-weight: bold;
					font-size: 18px;
					color: #434649;
				}

				&-item {
					.title-box {
						height: 40px;
						background: linear-gradient(270deg, rgba(22, 158, 154, 0) 0%, rgba(22, 158, 154, 0.1) 100%);
						font-weight: 500;
						font-size: 16px;
						color: #434649;
						line-height: 40px;
						border-left: 5px solid #26a29e;
						padding-left: 15px;
						box-sizing: border-box;
						display: flex;
						cursor: pointer;

						.btn {
							position: absolute;
							right: 20px;
							color: #3aaaa6;
						}
					}

					.medical-label {
						font-weight: 400;
						font-size: 14px;
						color: #797991;
						margin-bottom: 10px;
					}

					.addform-box {
						&-row {
							display: flex;
							align-items: center;
							justify-content: space-between;
							margin-top: 12px;
							margin-bottom: 12px;
							&-label {
							}
							&-add {
								cursor: pointer;
								color: #3aaaa6;
								margin-right: 20px;
							}
						}
						.option {
							padding: 16px 0;
							margin: 0 16px;
							box-sizing: border-box;
							border-bottom: 1px solid #d7dcd9;
							position: relative;
							display: flex;
							flex-direction: column;
							justify-content: center;

							.set {
								position: absolute;
								right: 0;
								cursor: pointer;
							}

							.technicalTitle {
								font-weight: 500;
								font-size: 16px;
								color: #434649;
							}

							.companyName {
								font-weight: 500;
								font-size: 16px;
								color: #434649;
							}

							.tMark {
								display: flex;
								font-weight: 400;
								font-size: 16px;
								color: #434649;

								.level {
									margin-left: 8px;
								}
							}

							.mark {
								display: flex;
								font-weight: 400;
								font-size: 14px;
								color: #797991;
								margin-top: 8px;

								.employmentPeriod {
									margin-left: 25px;
								}

								.manageTime {
									margin-left: 25px;
								}
							}

							.jobContent {
								font-weight: 400;
								font-size: 14px;
								color: #434649;
								margin-top: 8px;
							}
						}
					}
				}
			}
			.option-box {
				padding: 24px;

				.title {
					font-weight: 500;
					font-size: 20px;
					color: #434649;
				}

				.btns {
					display: flex;
					justify-content: space-between;
					margin-top: 10px;

					.btn {
						flex: 1;
						margin: 0 5px;
					}
				}
			}
			.statement-box {
				padding: 16px;
				width: 100%;
				max-height: calc(100vh - 70px);
				overflow-y: hidden;
				position: relative;

				.close {
					position: absolute;
					top: 10px;
					right: 10px;
					cursor: pointer;
				}
			}
			.prompt {
				font-weight: 400;
				font-size: 14px;
				color: #797991;
				line-height: 18px;
				display: flex;
				align-items: center;
				margin-bottom: 10px;

				.icon {
					width: 17px;
					height: 17px;
					background: #ff742a;
					color: #ffffff;
					display: flex;
					justify-content: center;
					align-items: center;
					border-radius: 50%;
					font-weight: bold;
					margin-right: 8px;
				}
			}
			.upload-box {
				.ready {
					width: 276px;
					height: 244px;
					background: #ffffff;
					border-radius: 8px;
					border: 1px solid #e1e4eb;

					img {
						width: 100%;
						height: 100%;
					}
				}

				.wait {
					width: 276px;
					height: 244px;
					padding: 0 28px;
					background: #f4f6f9;
					border-radius: 8px;
					border: 1px solid #e1e4eb;
					display: flex;
					flex-direction: column;
					justify-content: center;
					align-items: center;
					font-size: 30px;
					color: #b4bccc;
					font-weight: bold;
					img {
						width: 72px;
						height: 72px;
					}
					.tip {
						font-family: MiSans, MiSans;
						font-weight: 400;
						font-size: 14px;
						color: #828894;
						line-height: 20px;
						text-align: center;
					}
					.filedName {
						font-family: MiSans, MiSans;
						font-weight: 600;
						font-size: 16px;
						color: #383d47;
						line-height: 24px;
						margin: 14px 0 8px;
					}
				}
			}
		}
		.submit-box {
			background: #ffffff;
			box-sizing: border-box;
			margin-top: 32px;
			padding: 0 20%;

			.agt {
				display: flex;
				align-items: center;
				font-size: 16px;
				color: #383d47;
				margin-bottom: 36px;

				.statement {
					color: #2065d6;
				}
			}
			.btns {
				display: flex;
				margin-top: 10px;

				.btn {
					width: 47%;
				}
			}
		}
		.link-data {
			padding: 0 20%;
			.enter-tip {
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 20px;
				color: #383d47;
				line-height: 32px;
				margin: 28px 0 24px;
			}
			.url {
				width: 100%;
				background: #f4f6f9;
				border-radius: 8px;
				padding: 12px 16px;
				display: flex;
				align-items: flex-start;
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 16px;
				line-height: 24px;
				color: #828894;

				.link {
					cursor: pointer;
					&:hover {
						color: #2065d6;
					}
				}

				img {
					margin-right: 12px;
					width: 20px;
					height: 20px;
				}
			}
		}
	}
}
</style>
<style lang="scss">
.scene,
.elDrawer {
	background: #fff;
	position: fixed;
	height: 100vh !important;
	right: -12px !important;
	top: -12px !important;
	border-radius: 16px 0px 0px 16px;
	.el-drawer__header {
		margin-bottom: 0px;
		height: 80px;
		padding: 24px 32px 0;
		background: linear-gradient(180deg, rgba(43, 88, 213, 0.1) 0%, rgba(43, 88, 213, 0) 100%);
	}

	.el-drawer__body {
		overflow: hidden;
	}

	.w-textarea-wrapper {
		border-radius: 8px;
	}

	.w-select {
		border-radius: 8px;
		height: 40px;
	}

	.w-form-item-label-col {
		margin-top: 24px;
		justify-content: flex-start !important;

		.w-form-item-label {
			font-family: MiSans, MiSans;
			font-weight: 600;
			font-size: 16px;
			color: #383d47;
			line-height: 20px;
			margin-bottom: 8px;
		}
	}

	.w-picker-size-default {
		height: 40px;
		border-radius: 8px;
		border: 1px solid #d3d9e6;
	}

	.w-input-wrapper {
		border-radius: 8px;
	}

	.w-input-size-default {
		height: 38px;
	}

	.w-form-item {
		margin-bottom: 0;
	}

	.van-button {
		width: 100%;
	}

	.van-checkbox__icon .van-icon {
		width: 16px;
		height: 16px;
		border-radius: 3px;
	}

	.van-checkbox__icon--checked .van-icon {
		width: 16px;
		height: 16px;
		background: #1c50fd;
		border-radius: 3px;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.el-button--primary {
		background: #1c50fd !important;
		color: #fff !important;
		border-radius: 8px;
	}
}
</style>