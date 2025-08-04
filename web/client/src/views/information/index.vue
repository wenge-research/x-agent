<template>
	<div class="information-view" v-if="showView">
		<div class="info-box">
			<div class="title-box">
				<div class="close"><img :src="close" alt="" @click="handleClose" /></div>
				<div class="title">{{ activeForm.title }}</div>
			</div>
			<div class="dialogue-box">
				<div class="head"><img :src="head" alt="" /></div>
				<div class="message-box">
					<div class="mark">请填写以下个人信息后点击提交</div>
					<div class="message">{{ activeForm.message }}</div>
				</div>
			</div>
		</div>
		<div class="con-box">
			<div class="form-item" v-for="item in activeForm.forms" :key="item.basicInfo">
				<div class="title-box">
					<div class="solid"></div>
					<div class="title">{{ item.basicInfo }}</div>
					<!-- <div class="btn" v-if="item.type === 'addForm'">
            <van-button icon="plus" type="text" size="normal" @click="addOptions(item.filedName)">添加</van-button>
          </div> -->
				</div>
				<div class="form-box" v-if="item.type === 'form'">
					<van-form ref="formRef" @submit="handleAffirmSubmit" required>
						<template v-for="cItem in item.configs" :key="cItem.filedId">
							<!-- 输入表单 -->
							<van-field
								v-model="params[cItem.field]"
								:placeholder="cItem.placeholder"
								:label="cItem.label"
								:rules="checkingRule(cItem)"
								v-if="cItem.type === 'input'"
								:disabled="cItem.clientFormStatus == 0"
							/>

							<!-- 上传表单 -->
							<van-field
								:label="cItem.label"
								:disabled="cItem.clientFormStatus == 0"
								v-if="['file', 'image'].includes(cItem.type)"
								:rules="[{ required: true, message: cItem.prompt }]"
							>
								<template #input>
									<!-- 上传提示语 -->
									<div class="prompt" v-if="cItem.prompt">
										<div class="icon">!</div>
										{{ cItem.prompt }}
									</div>
									<van-uploader :preview-full-image="true" max-count="1" v-model="params[cItem.field]" :name="cItem.field" :after-read="afterRead" />
								</template>
							</van-field>

							<!-- 单个日期选择 -->
							<van-field
								v-if="cItem.type === 'datePicker'"
								v-model="params[cItem.field]"
								is-link
								readonly
								:disabled="cItem.clientFormStatus == 0"
								:rules="[{ required: true, message: cItem.prompt }]"
								name="datePicker"
								:label="cItem.label"
								:placeholder="cItem.placeholder"
								@click="handleShowPickerCalendar(cItem.field)"
							/>

							<!-- 日历选择 -->
							<van-field
								v-model="params[cItem.field]"
								is-link
								readonly
								name="datePicker"
								:disabled="cItem.clientFormStatus == 0"
								:rules="[{ required: true, message: cItem.prompt }]"
								:label="cItem.label"
								:placeholder="cItem.placeholder"
								@click="handleShowCalendar(cItem.field)"
								v-if="cItem.type === 'date'"
							/>

							<!-- 单选框 -->
							<van-field
								name="radio"
								:label="cItem.label"
								:rules="[{ required: true, message: cItem.prompt }]"
								:disabled="cItem.clientFormStatus == 0"
								v-if="cItem.type === 'radio'"
							>
								<template #input>
									<van-radio-group v-model="params[cItem.field]" shape="dot" direction="horizontal" @change="handleRadioChange">
										<van-radio :name="radio.label" checked-color="#149E9A" v-for="radio in cItem.options" :key="radio.filedId">
											{{ radio.label }}
										</van-radio>
									</van-radio-group>
								</template>
							</van-field>

							<!-- 选择框 -->
							<van-field
								v-model="params[cItem.field]"
								is-link
								readonly
								:label="cItem.label"
								:disabled="cItem.clientFormStatus == 0"
								:placeholder="cItem.placeholder"
								@click="handleShowPicker(cItem)"
								v-if="cItem.type === 'select'"
							/>

							<!-- 填写医疗机构 -->
							<div class="medical" v-if="cItem.type === 'multilInput'">
								<div class="mark">{{ cItem.label }}</div>
								<van-field
									v-for="(medicalOption, index) in medicalType === '本市城镇职工医保' ? cItem.options : TEMPORARY_OPTIONS"
									:placeholder="cItem.placeholder"
									:key="medicalOption.filedId"
									v-model="params[cItem.field][index].medicalInput"
									:disabled="cItem.clientFormStatus == 0"
								/>
							</div>
						</template>
					</van-form>
				</div>
				<!-- <div class="addform-box" v-if="item.type === 'addForm'">
          <div v-if="params[item.filedName].length !== 0" v-for="(option, optionIndex) in params[item.filedName]"
            :key="optionIndex">
            <div class="option">
              <template v-if="['workExperience'].includes(item.basicInfo)">
                <div class="companyName">
                  {{ option.companyName }}
                </div>
                <div class="mark">
                  <div class="occupation">{{ option.occupation }}</div>
                  <div class="employmentPeriod">{{ option.employmentPeriod }}</div>
                </div>
                <div class="jobContent">
                  {{ option.jobContent }}
                </div>
              </template>
              <template v-if="['vocationalSkills'].includes(item.basicInfo)">
                <div class="technicalTitle">
                  {{ option.technicalTitle }}
                </div>
                <div class="mark">
                  <div class="level">{{ option.level }}</div>
                  <div class="manageTime">{{ option.manageTime }}</div>
                </div>
              </template>
              <template v-if="['foreignLanguageSkills', 'csCertificate'].includes(item.basicInfo)">
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
						<div v-if="params[labelSon.field].length !== 0" v-for="(option, optionIndex) in params[labelSon.field]" :key="optionIndex">
							<div class="option">
								<template v-if="['addFormGzjl'].includes(item.type)">
									<div class="companyName">
										{{ option.companyName }}
									</div>
									<div class="mark">
										<div class="occupation">{{ option.occupation }}</div>
										<div class="employmentPeriod">{{ option.employmentPeriod }}</div>
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
										<div class="manageTime">{{ option.manageTime }}</div>
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
			<div style="padding: 16px;color: #434649">本人郑重承诺所填写信息和提供材料均真实有效，无任何虚假申报情况，本人遵纪守法，无刑事犯罪记录，未参加过国家禁止的社会活动，无政治、经济问题。以上如不真实本人愿意永久放弃申报资格，并承担由此带来的相应后果。</div>
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

		<van-popup v-model:show="showDatePicker" position="bottom">
			<van-date-picker @confirm="datePickerOnConfirm" @cancel="showDatePicker = false" />
		</van-popup>

		<!-- 选择框 -->
		<van-popup v-model:show="showColumns" round position="bottom">
			<van-picker
				:columns-field-names="{
					text: 'label',
					value: 'label',
				}"
				:columns="columns"
				:title="columnsTitle"
				@cancel="showColumns = false"
				@confirm="onConfirm"
			/>
		</van-popup>

		<!-- 添加option -->
		<van-popup v-model:show="addOptionShow" round position="bottom" :close-on-click-overlay="false">
			<div class="option-box">
				<div class="head">
					<div class="cancel" @click="handleCancel">取消</div>
					<div class="title">{{ operation === 'add' ? '添加' : '编辑' }}{{ dialogTitle }}</div>
					<div class="confirm" @click="handleConfirm">保存</div>
				</div>
				<div class="con">
					<van-form ref="formOpt" required @submit="handleAffirmOptionSubmit">
						<template v-for="cItem in optionForm" :key="cItem.filedId">
							<!-- 输入表单 -->
							<van-field
								v-model="optionParams[cItem.field]"
								:placeholder="cItem.placeholder"
								:label="cItem.label"
								:rules="checkingRule(cItem)"
								v-if="cItem.type === 'input'"
							/>
							<!-- 日历选择 -->
							<van-field
								v-model="optionParams[cItem.field]"
								is-link
								readonly
								name="datePicker"
								:rules="[{ required: true, message: cItem.prompt }]"
								:label="cItem.label"
								:placeholder="cItem.placeholder"
								@click="handleShowCalendar(cItem.field)"
								v-if="cItem.type === 'date'"
							/>
						</template>
					</van-form>
				</div>
				<div class="rem" v-if="operation === 'set'">
					<van-button type="default" @click="remOptions">删除</van-button>
				</div>
			</div>
		</van-popup>

		<!-- 信息声明 -->
		<van-popup v-model:show="statementShow" position="bottom" :close-on-click-overlay="false" :duration="0">
			<div class="statement-box">
				<MarkdownMessage :text="statement" />

				<div class="close" @click="statementShow = false">
					<van-icon name="cross" size="20px" />
				</div>
			</div>
		</van-popup>
	</div>
	<van-calendar
		v-model:show="showPicker"
		type="range"
		@confirm="dateOnConfirm"
		:min-date="minDate"
		:max-date="maxDate"
		:close-on-click-overlay="false"
		color="#149E9A"
	/>
</template>

<script lang="ts" setup>
import MarkdownMessage from './component/markdownMessage.vue';
import cloneDeep from 'lodash/cloneDeep';
import { useRoute, useRouter } from 'vue-router';
import { ref } from 'vue';
import close from '/@/assets/img/close.png';
import head from '/@/assets/zc/jqr-new.png';
import { INFORMATION_TYPE, ADDFORM_TYPE_FORM, TEMPORARY_OPTIONS } from './content/index';
import { getFormInfo, uploadPic, submitFormInfo, getFormInfoNew } from '/@/api/chat/index';
import { showToast } from 'vant';

const showDatePicker = ref(false);
const fillTip = ref('');
const statement = ref('');
const route = useRoute();
const router = useRouter();
const matterId = route.params.matterId;
const statementShow = ref(false);
const addOptionShow = ref(false);
const submitLoading = ref(false);
const showView = ref(false);
const activeForm = ref({});
const params = ref({});

const agt = ref(false);
const showPicker = ref(false);
const minDate = new Date(2010, 0, 1);
const maxDate = new Date();
const activeCalendar = ref('');
const formatDate = (date) => `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
const handleShowCalendar = (field) => {
	activeCalendar.value = field;
	showPicker.value = true;
};

const handleShowPickerCalendar = (field) => {
	activeCalendar.value = field;
	showDatePicker.value = true;
};
const dateOnConfirm = (values) => {
	const [start, end] = values;
	params.value[activeCalendar.value] = `${formatDate(start)} - ${formatDate(end)}`;
	optionParams.value[activeCalendar.value] = `${formatDate(start)} - ${formatDate(end)}`;
	showPicker.value = false;
};

const datePickerOnConfirm = ({ selectedValues }) => {
	params.value[activeCalendar.value] = `${selectedValues[0]}-${selectedValues[1]}-${selectedValues[2]}`;
	showDatePicker.value = false;
};

// const getFormInfoData = async () => {
//   showView.value = false
//   const { data } = await getFormInfo({
//     matterId: matterId
//   })
//   activeForm.value = data
//   params.value = data.params
//   statement.value = data.matterGuide.statement
//   fillTip.value = data.matterGuide.completeFillTip
//   // activeForm.value = INFORMATION_TYPE
//   // params.value = INFORMATION_TYPE.params
//   showView.value = true

// }

const getFormInfoData = async () => {
	showView.value = false;
	const { data } = await getFormInfoNew({
		matterId: matterId,
	});
	activeForm.value = data;
	params.value = data.params;
	statement.value = data.matterGuide.statement;
	fillTip.value = data.matterGuide.completeFillTip;
	// const url = window.location.href;
	// const hashPart = url.split('#')[1] || '';
	// const queryPart = hashPart.split('?')[1] || '';
	// const queryParams = new URLSearchParams(queryPart);
	// const fromType = queryParams.get('fromType');
	// if (fromType) {
	// 	activeForm.value.forms = activeForm.value?.forms?.filter(form => {
  // 		const basicInfo = form.basicInfo || '';
  // 		return !basicInfo.includes('随调随迁') && !basicInfo.includes('落户信息');
	// 	});
	// }
	// activeForm.value = INFORMATION_TYPE
	// params.value = INFORMATION_TYPE.params
	showView.value = true;
};

getFormInfoData();
const formRef = ref();
const formOpt = ref();

const emits = defineEmits(['close']);
const handleClose = () => {
	const url = window.location.href;
	const hashPart = url.split('#')[1] || '';
	const queryPart = hashPart.split('?')[1] || '';
	const queryParams = new URLSearchParams(queryPart);
	const fromType = queryParams.get('fromType');
	if (fromType) {
		fromType == 'talent' ? router.push({ name: 'talent', params: { appId: route.params.appId } }) : router.push({ name: 'previewChat', params: { appId: route.params.appId } });
	} else {
		router.back();
	}
};

const handleSubmit = () => {
	const ary = formRef.value.map((n) => {
		return n.validate();
	});
	Promise.all(ary)
		.then((res) => {
			handleAffirmSubmit();
		})
		.catch((error) => {
			console.log(error);
		});
};
const handleAffirmSubmit = async () => {
	if (!agt.value) {
		showToast('请阅读并同意《个人信息收集声明》后再进行提交');
		return false;
	}
	submitLoading.value = true;
	const items = convertToFieldArray(params.value);
	await submitFormInfo({
		items: items,
		matterId: matterId,
	});
	submitLoading.value = false;
	handleClose();
	showToast(fillTip.value);
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

const afterRead = async ({ file }, detail) => {
	const formData = new FormData();
	formData.append('file', file);
	const res = await uploadPic(formData);
	params.value[detail.name] = [{ url: res.data }];
	formRef.value.forEach((n) => {
		n.validate(detail.name);
	});
};

// 输入框校验规则
const checkingRule = (cItem) => {
	if (cItem.checkRuleCode === '2001') {
		return [
			{
				validator: (value, rule) => {
					const reg = /^1[3-9]\d{9}$/;
					if (!value) {
						return '手机号不能为空';
					} else if (!reg.test(value)) {
						return '请输入正确的手机号';
					}
					return true;
				},
			},
		];
	}
	if (cItem.checkRuleCode === '1001') {
		return [
			{
				validator: (value, rule) => {
					const reg = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}[Xx0-9]$/;
					if (!value) {
						return '身份证号不能为空';
					} else if (!reg.test(value)) {
						return '请输入正确的身份证号';
					}
					return true;
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
const addOptions = (type, item, labelSon) => {
	// operation.value = 'add'
	// optionParams.value = ADDFORM_TYPE_FORM[type].params
	// resetOptionParams(optionParams)
	// optionForm.value = ADDFORM_TYPE_FORM[type].configs
	// optionType.value = type
	// dialogTitle.value = labelSon.label;
	// addOptionShow.value = true
	operation.value = 'add';
	dialogStatus.value = 'add';
	optionParams.value = ADDFORM_TYPE_FORM[item.type].params;
	resetOptionParams(optionParams);
	optionForm.value = ADDFORM_TYPE_FORM[item.type].configs;
	optionType.value = labelSon.field;
	dialogTitle.value = labelSon.label;
	addOptionShow.value = true;
};

const resetOptionParams = (optionParams) => {
	Object.keys(optionParams.value).forEach((key) => {
		optionParams.value[key] = '';
	});
};

const handleCancel = () => {
	operation.value = 'cancel';
	if (dialogStatus.value == 'set') {
		addOptionShow.value = false;
		return;
	}
	init();
};

const init = () => {
	addOptionShow.value = false;
	formOpt.value.resetValidation();
	if (operation.value === 'cancel') {
		params.value[optionType.value] = optionInit.value;
	}
};

const handleConfirm = () => {
	formOpt.value.submit();
};

const handleAffirmOptionSubmit = () => {
	const paramsInfo = JSON.parse(JSON.stringify(optionParams.value));
	if (operation.value === 'add') {
		params.value[optionType.value] = [];
		params.value[optionType.value].push(paramsInfo);
	} else {
		params.value[optionType.value].splice(optionIndex.value, 1, paramsInfo);
	}
	init();
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
	addOptionShow.value = true;
};

const columns = ref([]);
const showColumns = ref(false);
const columnsTitle = ref('');
const selectField = ref('');
const handleShowPicker = (item) => {
	columns.value = item.options;
	columnsTitle.value = item.label;
	selectField.value = item.field;
	showColumns.value = true;
};
const onConfirm = ({ selectedValues }) => {
	params.value[selectField.value] = selectedValues[0];
	showColumns.value = false;
};

const remOptions = () => {
	params.value[optionType.value].splice(optionIndex.value, 1);
	addOptionShow.value = false;
	formOpt.value.resetValidation();
};

const medicalType = ref('本市城镇职工医保');
const handleRadioChange = (name) => {
	medicalType.value = name;
};
</script>

<style lang="scss" scoped>
.information-view {
	width: 100%;
	height: 100%;
	overflow-y: scroll;
	position: fixed;
	left: 0;
	top: 0;
	background: #f5faf7;
	display: flex;
	flex-direction: column;
	z-index: 1001;

	.info-box {
		width: 100%;
		height: auto;
		background: #299890 url('/@/assets/img/info-box.png') center bottom no-repeat;
		background-size: 100% auto;

		.title-box {
			width: 100%;
			height: 48px;
			display: flex;
			align-items: center;
			justify-content: center;
			position: relative;

			.title {
				font-weight: 500;
				font-size: 18px;
				font-weight: bold;
				color: #ffffff;
			}

			.close {
				flex-shrink: 0;
				position: absolute;
				width: 20px;
				height: 20px;
				left: 25px;

				img {
					width: 100%;
					height: 100%;
				}
			}
		}
	}

	.dialogue-box {
		padding: 8px 20px 30px 20px;
		display: flex;
		box-sizing: border-box;

		.head {
			width: 50px;
			height: 50px;
			border-radius: 50%;
			border: 2px solid #ffffff;
			flex-shrink: 0;
			margin-right: 10px;
		}

		.message-box {
			padding: 12px;
			box-sizing: border-box;
			background: rgba(255, 255, 255, 0.9);
			border-radius: 0px 12px 12px 12px;

			.mark {
				font-weight: 500;
				font-size: 16px;
				color: #434649;
			}

			.message {
				margin-top: 5px;
				font-weight: 400;
				font-size: 16px;
				color: #797991;
				line-height: 24px;
				letter-spacing: 2px;
			}
		}
	}

	.con-box {
		width: 100%;
		height: auto;
		// flex-grow: 1;
		// overflow-y: scroll;
		padding-bottom: 123px;

		.form-item {
			.title-box {
				height: 56px;
				display: flex;
				background: #f5faf7;
				align-items: center;
				padding: 0 16px;
				box-sizing: border-box;
				border-bottom: 1px solid #d7dcd9;
				position: relative;

				.btn {
					position: absolute;
					right: 0;
					color: #3aaaa6;
				}

				.solid {
					width: 3px;
					height: 18px;
					background: #169e9a;
					margin-right: 13px;
				}

				.title {
					font-weight: 500;
					font-size: 18px;
					color: #434649;
				}
			}

			.form-box {
				padding: 16px;
				box-sizing: border-box;
				padding-bottom: 0;

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

				.medical {
					margin: 10px 0;

					.mark {
						font-weight: 400;
						font-size: 16px;
						color: #797991;
						margin-bottom: 10px;
					}
				}
			}

			.addform-box {
				&-row {
					display: flex;
					align-items: center;
					justify-content: space-between;
					margin-top: 12px;
					margin-bottom: 12px;
					&-label {
						margin-left: 16px;
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

	.submit-box {
		width: 100%;
		background: #ffffff;
		position: fixed;
		bottom: 0px;
		padding: 18px 24px 18px 24px;
		left: 0;
		box-sizing: border-box;
		display: flex;
		flex-direction: column;

		.agt {
			display: flex;
			justify-content: center;
			align-items: center;
			font-weight: 400;
			font-size: 16px;
			color: #383d47;

			.statement {
				color: #355eff;
			}
		}

		.btns {
			display: flex;
			justify-content: space-between;
			margin-top: 20px;

			.btn {
				width: 47%;
			}
		}
	}
}

.option-box {
	box-sizing: border-box;

	.head {
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 2px solid #e3e3e3;
		padding: 15px 0;
		background: linear-gradient(180deg, rgba(22, 158, 154, 0.2) 0%, rgba(22, 158, 154, 0) 100%);

		.cancel {
			flex: 1;
			text-align: center;
			font-weight: 400;
			font-size: 16px;
			color: #383d47;
		}

		.title {
			flex: 4;
			text-align: center;
			font-weight: 500;
			font-size: 18px;
			color: #383d47;
		}

		.confirm {
			flex: 1;
			text-align: center;
			font-weight: 500;
			font-size: 16px;
			color: #169e9a;
		}
	}

	.con {
		padding: 20px;
		box-sizing: border-box;
	}

	.rem {
		padding: 0px 18px 18px 18px;
	}
}

.statement-box {
	padding: 16px;
	width: 100%;
	// height: 100vh;
	// overflow-y: scroll;
	position: relative;

	.close {
		position: absolute;
		top: 10px;
		right: 10px;
	}
}

:deep(.van-button) {
	width: 100%;
}

:deep(.van-cell:after) {
	display: none;
}

:deep(.van-cell) {
	flex-direction: column;
	background-color: initial;
	border-bottom: 1px solid #d7dcd9;
}

:deep(.van-field__label) {
	width: auto;
	font-size: 15px;
	font-weight: bold;
	margin-bottom: 5px;
}

:deep(.van-cell__right-icon) {
	display: none;
}

:deep(.van-overlay) {
	background: rgba(0, 0, 0, 0.08) !important;
}

:deep(.van-field__control--custom) {
	flex-direction: column;
	align-items: flex-start;
}
</style>