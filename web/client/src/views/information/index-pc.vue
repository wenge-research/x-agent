<template>
	<div class="information-view" v-loading="showView">
		<div class="left-box">
			<div class="message-box">
				<div class="title">请填写右侧信息后点击提交</div>
				<div class="message">{{ activeForm.message }}</div>
			</div>
			<!-- <div class="gx">
				<img :src="gx" alt="" />
			</div> -->
		</div>
		<div class="right-box">
			<div class="title">
				{{ activeForm.title }}
			</div>
			<div class="form-item" v-for="item in activeForm.forms" :key="item.basicInfo">
				<div class="title-box">
					{{ item.basicInfo }}
					<!-- <div class="btn" v-if="item.type === 'addForm'" @click="addOptions(item.filedName)">
            添加
          </div> -->
				</div>
				<div class="form-box" v-if="item.type === 'form'">
					<w-form ref="formRef" :model="params" :label-col-props="{ span: 24 }" :wrapper-col-props="{ span: 24 }" label-position="left">
						<template v-for="cItem in item.configs" :key="cItem.filedId">
							<w-form-item
								:label="cItem.label"
								v-if="!['multilInput'].includes(cItem.type)"
								:rules="checkingRule(cItem)"
								:field="cItem.field"
								:validate-trigger="['change', 'input']"
							>
								<!-- 单日期表单 -->
								<w-date-picker
									style="width: 100%"
									v-model="params[cItem.field]"
									v-if="cItem.type === 'datePicker'"
									:disabled="cItem.clientFormStatus == 0"
								/>
								<!-- 日期表单 -->
								<w-range-picker v-model="params[cItem.field]" v-if="cItem.type === 'date'" :disabled="cItem.clientFormStatus == 0" />
								<!-- 输入表单 -->
								<w-input
									v-model="params[cItem.field]"
									v-if="cItem.type === 'input'"
									:placeholder="cItem.placeholder"
									:disabled="cItem.clientFormStatus == 0"
								/>
								<!-- 上传表单 -->
								<w-upload
									v-if="['file', 'image'].includes(cItem.type)"
									:data="{ field: cItem.field }"
									list-type="picture-card"
									action="/"
									:disabled="cItem.clientFormStatus == 0"
									:show-file-list="false"
									:custom-request="uploadRequest"
								>
									<template #upload-button>
										<!-- 上传提示语 -->
										<div class="prompt" v-if="cItem.prompt">
											<div class="icon">!</div>
											{{ cItem.prompt }}
										</div>
										<div class="upload-box">
											<div class="ready" v-if="params[cItem.field] && params[cItem.field]?.length">
												<img :src="params[cItem.field]" />
											</div>
											<div class="wait" v-else>+</div>
										</div>
									</template>
								</w-upload>
								<!-- 复选表单 -->
								<w-radio-group
									v-if="cItem.type === 'radio'"
									:disabled="cItem.clientFormStatus == 0"
									v-model="params[cItem.field]"
									@change="handleRadioChange"
								>
									<w-radio :value="option.label" v-for="option in cItem.options" :key="option.id">
										{{ option.label }}
									</w-radio>
								</w-radio-group>

								<!-- 选择器 -->
								<w-select
									v-if="cItem.type === 'select'"
									v-model="params[cItem.field]"
									:disabled="cItem.clientFormStatus == 0"
									:placeholder="cItem.placeholder"
								>
									<w-option v-for="option in cItem.options" :key="option.filedId" :value="option.label">{{ option.label }}</w-option>
								</w-select>
							</w-form-item>
							<!-- 填写医疗机构 -->
							<div class="medical" v-if="cItem.type === 'multilInput'">
								<div class="medical-label">{{ cItem.label }}</div>
								<w-form :label-col-props="{ span: 24 }" :wrapper-col-props="{ span: 24 }">
									<w-form-item
										v-for="(medicalOption, index) in medicalType === '本市城镇职工医保' ? cItem.options : TEMPORARY_OPTIONS"
										:key="medicalOption.filedId"
									>
										<w-input
											v-model="params[cItem.field][index].medicalInput"
											type="text"
											:disabled="cItem.clientFormStatus == 0"
											:placeholder="cItem.placeholder"
										/>
									</w-form-item>
								</w-form>
							</div>
						</template>
					</w-form>
				</div>

				<!-- <div class="addform-box" v-if="item.type === 'addForm'">
          <div v-if="params[item.filedName]?.length !== 0" v-for="(option, optionIndex) in params[item.filedName]"
            :key="optionIndex">
            <div class="option">
              <template v-if="['workExperience'].includes(item.filedName)">
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
              <template v-if="['vocationalSkills'].includes(item.filedName)">
                <div class="technicalTitle">
                  {{ option.technicalTitle }}
                </div>
                <div class="mark">
                  <div class="level">{{ option.level }}</div>
                  <div class="manageTime">{{ option.manageTime[0] }}至{{ option.manageTime[1] }}</div>
                </div>
              </template>
              <template v-if="['foreignLanguageSkills', 'csCertificate'].includes(item.filedName)">
                <div class="tMark">
                  <div class="language">{{ option.language }}</div>
                  <div class="level">{{ option.level }}</div>
                </div>
              </template>


              <div class="set" @click="setOptions(item.filedName, option, optionIndex)">
                <van-icon name="arrow" size="20px" />
              </div>
            </div>
          </div>
        </div> -->

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

		<div class="submit-box">
			<div class="agt">
				<van-checkbox shape="square" checked-color="#149E9A" v-model="agt">
					我已阅读并同意<span class="statement" @click.stop="statementShow = true">《个人信息收集声明》</span>
				</van-checkbox>
			</div>
			<div class="btns">
				<div class="btn">
					<van-button color="#149E9A" :loading="submitLoading" @click="handleSubmit" type="success" loading-text="提交中...">提交</van-button>
				</div>
				<div class="btn">
					<van-button type="default" @click="handleClose">取消</van-button>
				</div>
			</div>
		</div>

		<!-- 添加option -->
		<el-dialog :show-close="false" v-model="optionVisible" width="500" :close-on-press-escape="false" :close-on-click-modal="false">
			<div class="option-box">
				<div class="title">{{ operation === 'add' ? '添加' : '编辑' }}{{ dialogTitle }}</div>
				<div class="con">
					<w-form ref="formOpt" :model="optionParams" :label-col-props="{ span: 24 }" :wrapper-col-props="{ span: 24 }" label-position="left">
						<template v-for="cItem in optionForm" :key="cItem.filedId">
							<w-form-item
								:label="cItem.label"
								v-if="!['multilInput'].includes(cItem.type)"
								:rules="checkingRule(cItem)"
								:field="cItem.field"
								:validate-trigger="['change', 'input']"
							>
								<!-- 输入表单 -->
								<w-input v-model="optionParams[cItem.field]" v-if="cItem.type === 'input'" :placeholder="cItem.placeholder" />
								<!-- 日期表单 -->
								<w-range-picker v-model="optionParams[cItem.field]" v-if="cItem.type === 'date'" />
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
</template>

<script lang="ts" setup>
import cloneDeep from 'lodash/cloneDeep';
import { ref, computed } from 'vue';
import gx from '/@/assets/img/gx.png';
import { INFORMATION_TYPE, ADDFORM_TYPE_FORM, TEMPORARY_OPTIONS } from './content/index';
import { getFormInfo, uploadPic, submitFormInfo, getFormInfoNew } from '/@/api/chat/index';
import { Message } from 'winbox-ui-next';
import MarkdownMessage from './component/markdownMessage.vue';
import { json } from 'stream/consumers';
import { useRoute } from 'vue-router';
import { v4 as uuidv4 } from 'uuid';
const props = defineProps({
	formParams: {
		type: Object,
		default: () => {},
	},
});
const statement = ref('');
const statementShow = ref(false);
const agt = ref(false);
const activeForm = ref({});
const params = ref({});
const showView = ref(true);
const fillTip = ref();
// const getFormInfoData = async () => {
//   showView.value = true
//   const { data } = await getFormInfo({
//     matterId: props.formParams.matterId
//   })
//   activeForm.value = data
//   params.value = data.params
//   statement.value = data.matterGuide.statement
//   fillTip.value = data.matterGuide.completeFillTip
//   if (params.value.idCardFrontPic && params.value.idCardFrontPic.length === 0) {
//     params.value.idCardFrontPic = ''
//   }
//   // activeForm.value = INFORMATION_TYPE
//   // params.value = INFORMATION_TYPE.params
//   showView.value = false
// }
// 弹框表单
const popUpForm = computed(() => props.formParams?.matterNameList?.filter((item: any) => item?.display == '弹框表单'));
const getFormInfoData = async () => {
	showView.value = true;
	// 新数据的matterId
	const newMatterId = popUpForm.value?.length ? popUpForm.value[0]?.matterId : '';
	const matterId = newMatterId || props.formParams.matterId;
	const { data } = await getFormInfoNew({
		matterId,
	});
	activeForm.value = data;
	params.value = data.params;
	statement.value = data.matterGuide.statement;
	fillTip.value = data.matterGuide.completeFillTip;
	if (params.value.idCardFrontPic && params.value.idCardFrontPic.length === 0) {
		params.value.idCardFrontPic = '';
	}
	// activeForm.value = INFORMATION_TYPE
	// params.value = INFORMATION_TYPE.params
	showView.value = false;
};
getFormInfoData();

const uploadRequest = async (option) => {
	const { data, fileItem } = option;
	const formData = new FormData();
	formData.append('file', fileItem?.file);
	const res = await uploadPic(formData);
	params.value[data.field] = res.data;
	formRef.value.forEach((n) => {
		n.validate(data.field);
	});
};

const emits = defineEmits(['close']);
const handleClose = () => {
	emits('close');
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
const route = useRoute();
const submitLoading = ref(false);
const formRef = ref();
const handleSubmit = async () => {
	const validationPromises = formRef.value.map(async (form) => {
		const validationResult = await form.validate();
		if (validationResult) {
			throw new Error('请完善表单信息');
		}
	});
	await Promise.all(validationPromises);
	if (!agt.value) {
		Message.warning('请阅读并同意《个人信息收集声明》后再进行提交');
	} else {
		submitLoading.value = true;
		const items = convertToFieldArray(params.value);
		let uuid = await uuidv4()?.replace(/-/g, '');
		// 新数据的matterId
		const newMatterId = popUpForm.value?.length ? popUpForm.value[0]?.matterId : '';
		const matterId = newMatterId || props.formParams.matterId;
		const res = await submitFormInfo({
			items: items,
			matterId,
			applicationId: localStorage.getItem(`${route.params.appId}appId`),
			traceId: uuid,
		});
		if (res?.code == '000000') {
			handleClose();
			Message.success(fillTip.value);
		} else {
			Message.warning(res.msg);
		}
		submitLoading.value = false;
	}
};
const convertToFieldArray = (obj) => {
	return Object.keys(obj).map((key) => {
		return { filedId: key, dataValue: obj[key] };
	});
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

const optionParams = ref({});
const optionForm = ref({});
const optionType = ref('');
const dialogTitle = ref('');
const operation = ref('');
const dialogStatus = ref('');
const optionVisible = ref(false);
const addOptions = (type, item, labelSon) => {
	console.log(type, 'type');
	// operation.value = 'add';
	// optionParams.value = ADDFORM_TYPE_FORM[type].params;
	// resetOptionParams(optionParams);
	// optionForm.value = ADDFORM_TYPE_FORM[type].configs;
	// optionType.value = type;
	// optionVisible.value = true;
	operation.value = 'add';
	dialogStatus.value = 'add';
	optionParams.value = ADDFORM_TYPE_FORM[item.type].params;
	resetOptionParams(optionParams);
	optionForm.value = ADDFORM_TYPE_FORM[item.type].configs;
	optionType.value = labelSon.field;
	dialogTitle.value = labelSon.label;
	optionVisible.value = true;
};

const resetOptionParams = (optionParams) => {
	Object.keys(optionParams.value).forEach((key) => {
		optionParams.value[key] = '';
	});
};
const formOpt = ref();
const handleCancel = () => {
	operation.value = 'cancel';
	if (dialogStatus.value == 'set') {
		optionVisible.value = false;
		return;
	}
	init();
};

const init = () => {
	optionVisible.value = false;
	formOpt.value.resetFields();
	if (operation.value === 'cancel') {
		params.value[optionType.value] = optionInit.value;
	}
};

const handleConfirm = () => {
	formOpt.value.validate(async (errors: Object) => {
		if (!errors) {
			const paramsInfo = JSON.parse(JSON.stringify(optionParams.value));
			if (operation.value === 'add') {
				params.value[optionType.value] = [];
				params.value[optionType.value].push(paramsInfo);
			} else {
				params.value[optionType.value].splice(optionIndex.value, 1, paramsInfo);
			}
			init();
		}
	});
};

const optionInit = ref([]);
const optionIndex = ref('');
const setOptions = (type, option, index, item, labelSon) => {
	operation.value = 'set';
	dialogStatus.value = 'set';
	optionInit.value = cloneDeep(params.value[item.type]);
	optionParams.value = option;
	optionForm.value = ADDFORM_TYPE_FORM[item.type].configs;
	optionType.value = labelSon.field;
	optionIndex.value = index;
	optionVisible.value = true;
};

const remOptions = () => {
	params.value[optionType.value].splice(optionIndex.value, 1);
	optionVisible.value = false;
	formOpt.value.resetFields();
};

const medicalType = ref('本市城镇职工医保');
const handleRadioChange = (name) => {
	medicalType.value = name;
};
</script>

<style lang="scss" scoped>
.information-view {
	width: 100%;
	height: 520px;
	display: flex;
	position: relative;
	z-index: 99999;

	.submit-box {
		width: 480px;
		height: 104px;
		background: #ffffff;
		border-bottom-right-radius: 12px;
		position: absolute;
		right: 0;
		bottom: 0;
		padding: 0 32px;
		box-sizing: border-box;
		padding-top: 16px;

		.agt {
			display: flex;
			justify-content: center;
			align-items: center;
			font-weight: 400;
			font-size: 14px;
			color: #383d47;

			.statement {
				color: #355eff;
			}
		}

		.btns {
			display: flex;
			justify-content: space-between;
			margin-top: 10px;

			.btn {
				width: 47%;
			}
		}
	}

	.left-box {
		width: 280px;
		background: #379e96 url('/@/assets/img/form-bg-pc.png') center bottom no-repeat;
		background-size: 100% auto;
		border-top-left-radius: 12px;
		border-bottom-left-radius: 12px;
		position: relative;

		.message-box {
			background: rgba(255, 255, 255, 0.9);
			border-radius: 12px;
			padding: 16px;
			box-sizing: border-box;
			width: 232px;
			position: absolute;
			bottom: 200px;
			left: 50%;
			margin-left: -116px;

			.title {
				font-weight: 500;
				font-size: 16px;
				color: #434649;
				text-align: center;
			}

			.message {
				font-weight: 400;
				font-size: 14px;
				color: #646479;
				letter-spacing: 2px;
				margin-top: 8px;
			}
		}

		.gx {
			width: 200px;
			// height: 160px;
			position: absolute;
			bottom: 0;
			left: 25px;

			img {
				width: 100%;
				height: 100%;
			}
		}
	}

	.right-box {
		width: 480px;
		background-color: #f6fbfa;
		border-top-right-radius: 12px;
		border-bottom-right-radius: 12px;
		padding: 20px 32px;
		box-sizing: border-box;
		height: 100%;
		overflow-y: scroll;
		position: relative !important;
		padding-bottom: 104px;

		.title {
			font-weight: bold;
			font-size: 18px;
			color: #434649;
		}

		.form-item {
			margin-top: 20px;

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
		width: 80px;
		height: 80px;
		background: #ffffff;
		border-radius: 8px;
		border: 1px solid #e1e4eb;

		img {
			width: 100%;
			height: 100%;
		}
	}

	.wait {
		width: 80px;
		height: 80px;
		background: #ffffff;
		border-radius: 8px;
		border: 1px solid #e1e4eb;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 30px;
		color: #b4bccc;
		font-weight: bold;
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

:deep(.w-form-item-label-col) {
	justify-content: flex-start;
}

:deep(.w-form-item) {
	margin-bottom: 10px;
}

:deep(.van-button) {
	width: 100%;
}
</style>
