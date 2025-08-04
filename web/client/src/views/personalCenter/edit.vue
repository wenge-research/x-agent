<template>
	<div class="information-view" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		<div class="info-box">
			<div class="title-box">
				<div class="close">
					<van-icon name="arrow-left" color="#fff" @click="backHome" />
				</div>
				<div class="title">基本信息</div>
			</div>
		</div>
		<div class="con-box">
			<div class="form-item">
				<div class="form-box">
					<van-form ref="formRef" @submit="handleSubmit" @failed="onFailed">
						<!-- 人员类型 -->
						<van-field
							v-model="params.userTypeName"
							name="picker"
							is-link
							readonly
							required
							label="人员类型"
							placeholder="请选择人员类型"
							right-icon="arrow"
							@click="openPopup('人员类型', 'userType')"
							:rules="[{ required: true, message: '请选择人员类型' }]"
						/>
						<!-- 姓名 -->
						<van-field
							required
							v-model="params.userName"
							placeholder="请输入姓名"
							label="姓名"
							:rules="[{ required: true, message: '请输入姓名' }]"
						/>
						<template v-if="params.userType.indexOf('staff') > -1 || params.userType.indexOf('gov') > -1">
							<!-- 部门 -->
							<van-field
								required
								v-model="params.deptIdName"
								is-link
								readonly
								label="部门"
								placeholder="请选择部门"
								right-icon="arrow"
								@click="showColumns = true"
								:rules="[{ required: true, message: '请选择部门' }]"
							/>

							<!-- 职务 -->
							<Picker
								label="职务"
								text-column="text"
								:label-width="250"
								:dict-data-list="popupInfo.workPositionColumns"
								value-column="value"
								v-model="params.workPosition"
								placeholder="请选择"
								:multiple="true"
								:readonly="false"
								required
								:rules="[{ required: true, message: '请选择职务' }]"
								v-if="params.userType.indexOf('street') > -1"
							/>
							<Picker
								label="职务"
								text-column="text"
								:label-width="250"
								:dict-data-list="popupInfo.workPositionGovColumns"
								value-column="value"
								v-model="params.workPosition"
								placeholder="请选择"
								:multiple="true"
								:readonly="false"
								required
								:rules="[{ required: true, message: '请选择职务' }]"
								v-if="params.userType.indexOf('community') > -1"
							/>
							<!-- <van-field
                required
                v-model="params.workPosition"
                is-link
                readonly
                label="职务"
                placeholder="请选择职务"
                right-icon="arrow"
                @click="openPopup('职务', 'workPosition')"
                :rules="[{ required: true, message: '请选择职务' }]"
                v-if="params.userType.indexOf('staff') > -1"
              />
              <van-field
                required
                v-model="params.workPosition"
                is-link
                readonly
                label="职务"
                placeholder="请选择职务"
                right-icon="arrow"
                @click="openPopup('职务', 'workPosition')"
                :rules="[{ required: true, message: '请选择职务' }]"
                v-if="params.userType.indexOf('gov') > -1"
              /> -->
							<!-- 主要工作 -->
							<van-field v-model="params.mainDuty" placeholder="请输入主要工作" label="主要工作" />

							<!-- 工作状态 -->
							<van-field
								v-model="params.workStatusName"
								is-link
								readonly
								label="工作状态"
								placeholder="请选择工作状态"
								right-icon="arrow"
								@click="openPopup('工作状态', 'workStatus')"
							/>
						</template>
						<template v-else>
							<!-- 身份证号 -->
							<van-field :rules="checkingRule(1)" v-model="params.idNo" placeholder="请输入身份证号" label="身份证号" />
						</template>

						<!-- 联系电话 -->
						<van-field :rules="checkingRule(2)" v-model="params.phone" placeholder="请输入联系电话" label="联系电话" />
						<!-- v-if="params.userType == 'staff-street'" -->
						<van-field v-if="params.userType != 'resident'" v-model="params.landline" placeholder="请输入座机" label="座机" />
						<van-button
							round
							style="margin-top: 32px"
							color="#149E9A"
							:loading="submitLoading"
							type="success"
							loading-text="提交中..."
							native-type="submit"
							>提交</van-button
						>
					</van-form>
				</div>
			</div>
		</div>
		<!-- 选择框 -->
		<van-popup v-model:show="curpopupInfo.showColumns" round position="bottom">
			<van-picker :columns="curpopupInfo.columnsValue" :title="curpopupInfo.title" @cancel="curpopupInfo.showColumns = false" @confirm="onConfirm" />
		</van-popup>
		<!-- 选择框 -->
		<van-popup v-model:show="showColumns" round position="bottom">
			<!-- <van-picker
				:columns-field-names="{ text: 'deptName', value: 'deptId', children: 'children' }"
				:columns="deptOptions"
				title="部门"
				@cancel="showColumns = false"
				@confirm="onFinish"
			/> -->
			<van-cascader
				v-model="params.deptId"
				title="请选择部门"
				:options="deptOptions"
				:field-names="{ text: 'deptName', value: 'deptId', children: 'children' }"
				@close="showColumns = false"
				@change="onFinish"
			/>
		</van-popup>
	</div>
</template>

<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router';
import { Message } from 'winbox-ui-next';
import { ref, onMounted, defineAsyncComponent } from 'vue';
import { fillInPersonal, getDeptStreet } from '/@/api/manage/index';
const Picker = defineAsyncComponent(() => import('./vanPicker.vue'));
const route = useRoute();
const router = useRouter();
const params = ref({
	userName: '',
	idNo: '',
	phone: '',
	userType: 'staff-street',
	userTypeName: '工作人员-街道工作人员',
	mainDuty: '',
	workStatus: '',
	workStatusName: '',
	deptId: '',
	deptIdName: '',
	workPosition: '',
	workPositionName: '',
	landline: '',
});
const showColumns = ref(false);
const deptOptions = ref([]);
// 弹框下拉数据
const popupInfo = ref({
	userTypeColumns: [
		{
			text: '居民',
			value: 'resident',
		},
		{
			text: '街道工作人员',
			value: 'staff-street',
		},
		{
			text: '社区工作人员',
			value: 'staff-community',
		},
	],
	workPositionColumns: [
		{ text: '党工委书记', value: '党工委书记' },
		{ text: '党工委副书记', value: '党工委副书记' },
		{ text: '办事处主任', value: '办事处主任' },
		{ text: '纪工委书记', value: '纪工委书记' },
		{ text: '党工委委员', value: '党工委委员' },
		{ text: '退役军人服务站站长（街道）', value: '退役军人服务站站长（街道）' },
		{ text: '办事处副主任', value: '办事处副主任' },
		{ text: '武装部部长', value: '武装部部长' },
		{ text: '退役军人服务站副站长（街道）', value: '退役军人服务站副站长（街道）' },
		{ text: '副主任（正科级）', value: '副主任（正科级）' },
		{ text: '科长', value: '科长' },
		{ text: '副科长', value: '副科长' },
		{ text: '一级调研员', value: '一级调研员' },
		{ text: '二级调研员', value: '二级调研员' },
		{ text: '三级调研员', value: '三级调研员' },
		{ text: '四级调研员', value: '四级调研员' },
		{ text: '一级主任科员', value: '一级主任科员' },
		{ text: '二级主任科员', value: '二级主任科员' },
		{ text: '三级主任科员', value: '三级主任科员' },
		{ text: '四级主任科员', value: '四级主任科员' },
		{ text: '科员', value: '科员' },
		{ text: '主任（事业单位）', value: '主任（事业单位）' },
		{ text: '副主任（事业单位）', value: '副主任（事业单位）' },
		{ text: '六级职员', value: '六级职员' },
		{ text: '七级职员', value: '七级职员' },
		{ text: '八级职员', value: '八级职员' },
		{ text: '九级职员', value: '九级职员' },
		{ text: '其他', value: '其他' },
	],
	workPositionGovColumns: [
		{ text: '社区党委/党总支/党支部书记', value: '社区党委/党总支/党支部书记' },
		{ text: '社区党委/党总支/党支部副书记', value: '社区党委/党总支/党支部副书记' },
		{ text: '社区居委会主任', value: '社区居委会主任' },
		{ text: '退役军人服务站站长（社区）', value: '退役军人服务站站长（社区）' },
		{ text: '社区党委/党总支/党支部委员', value: '社区党委/党总支/党支部委员' },
		{ text: '社区居委会副主任', value: '社区居委会副主任' },
		{ text: '社区服务站站长', value: '社区服务站站长' },
		{ text: '社工', value: '社工' },
		{ text: '其他', value: '其他' },
	],
	workStatusColumns: [
		{
			text: '在职',
			value: 'on',
		},
		{
			text: '离职',
			value: 'off',
		},
		{
			text: '调离',
			value: 'transferred',
		},
		{
			text: '休假',
			value: 'retired',
		},
		{
			text: '病休',
			value: 'sick',
		},
		{
			text: '退休',
			value: 'retire',
		},
	],
});
// 当前操作的弹框信息
const curpopupInfo = ref({
	showColumns: false,
	title: '',
	columnsValue: [],
	formValue: '',
});
const submitLoading = ref(false);
const userInfo = ref({});
onMounted(() => {
	getDeptStreetFun();
	userInfo.value = JSON.parse(sessionStorage.getItem('userInfo'));
	params.value = {
		userName: userInfo.value?.userName || userInfo.value?.username || '',
		idNo: userInfo.value?.idNo || '',
		phone: userInfo.value?.phone || '',
		userType: userInfo.value?.userType || '',
		userTypeName: userInfo.value?.userTypeName || '',
		mainDuty: userInfo.value?.mainDuty || '',
		workStatus: userInfo.value?.workStatus || '',
		workStatusName: userInfo.value?.workStatusName || '',
		deptId: userInfo.value?.deptId || '',
		deptIdName: userInfo.value?.deptName || '',
		workPosition: userInfo.value?.workPosition || '',
		workPositionName: userInfo.value?.workPosition || '',
		landline: userInfo.value?.landline || '',
	};
});
// 返回
const backHome = () => {
	router.back();
};
// 打开弹框
const openPopup = (title, value) => {
	console.log(title, value, 4444);
	curpopupInfo.value.showColumns = true;
	curpopupInfo.value.title = title;
	curpopupInfo.value.formValue = value;
	curpopupInfo.value.columnsValue = popupInfo.value[value + 'Columns'];
};
// 弹框确认
const onConfirm = (data) => {
	curpopupInfo.value.showColumns = false;
	if (curpopupInfo.value.formValue == 'userType' && params.value[curpopupInfo.value.formValue] == data.selectedOptions[0].value) return;
	params.value[curpopupInfo.value.formValue] = data.selectedOptions[0].value;
	params.value[curpopupInfo.value.formValue + 'Name'] = data.selectedOptions[0].text;
};
// 查询部门信息树
const getDeptStreetFun = async () => {
	try {
		const res = await getDeptStreet({
			pageNo: 1,
			pageSize: 9999,
			tenantId: sessionStorage.getItem('tenantId'),
		});
		// 调用函数
		deptOptions.value = handlerTree(res.data);
	} catch (err) {
		throw new Error(err);
	}
};
const handlerTree = (treeData) => {
	// 递归遍历每一项
	return treeData
		.map((item) => {
			// 如果当前项有 children 并且长度大于 0，则保留该项并递归处理其 children
			if (item.children && item.children.length > 0) {
				item.children = handlerTree(item.children);
				return item;
			}
			// 如果没有 children 或者 children 长度为 0，则删除 children 属性后返回该项
			delete item.children;
			return item;
		})
		.filter((item) => item); // 过滤掉可能的 undefined 或 null
};
const onFailed = () => {};
const onFinish = (data) => {
	params.value.deptIdName = data.selectedOptions[data.selectedOptions.length - 1].deptName;
	params.value.deptId = data.selectedOptions[data.selectedOptions.length - 1].deptId;
	// showColumns.value = false;
};
// 提交
const handleSubmit = async () => {
	let workList = params.value.workPosition.split(',');
	let workArray = workList && workList.filter((item) => item != '');
	try {
		const res = await fillInPersonal({
			userName: params.value.userName,
			idNo: params.value.idNo,
			phone: params.value.phone,
			userType: params.value.userType,
			mainDuty: params.value.mainDuty,
			workStatus: params.value.workStatus,
			deptId: params.value.deptId,
			workPosition: workArray && workArray.join(','),
			userId: userInfo.value.id,
			landline: params.value.landline,
		});
		if (res?.code === '000000') {
			Message.success('提交成功');
			backHome();
		} else {
			Message.error(res.msg);
		}
	} catch (err) {
		Message.error('提交失败');
		throw new Error(err);
	}
};
// 输入框校验规则
const checkingRule = (type) => {
	if (type == 2) {
		return [
			{
				validator: (value, rule) => {
					const reg = /^1[3-9]\d{9}$/;
					if (value && !reg.test(value)) {
						return '请输入正确的手机号';
					}
					return true;
				},
			},
		];
	}
	if (type == 1) {
		return [
			{
				validator: (value, rule) => {
					const reg = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}[Xx0-9]$/;
					if (value && !reg.test(value)) {
						return '请输入正确的身份证号';
					}
					return true;
				},
			},
		];
	}
	return [{ required: true, message: cItem.placeholder }];
};
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo : '';
};
const touchStartX = ref(0);
const handleTouchStart = (event) => {
	touchStartX.value = event.touches[0].clientX;
};

const handleTouchEnd = (event) => {
	const touchEndX = event.changedTouches[0].clientX;
	const deltaX = touchEndX - touchStartX.value;

	// 如果从左向右滑动超过一定距离，返回上一页
	if (deltaX > 50) {
		var ua = navigator.userAgent.toLowerCase();
		// 判断是否在微信浏览器内
		if (ua.match(/MicroMessenger/i) == 'micromessenger') {
			wx.miniProgram.getEnv((res) => {
				if (res.miniprogram) {
					wx.miniProgram.navigateBack();
				} else {
					console.log('不在小程序内');
					router.push(`/personalCenter/${getAppDetail()?.applicationCode}`);
				}
			});
		} else {
			console.log('不在微信浏览器内');
      router.push(`/personalCenter/${getAppDetail()?.applicationCode}`);
		}
	}
};
</script>

<style lang="scss" scoped>
.information-view {
	width: 100%;
	height: 100vh;
	position: fixed;
	left: 0;
	top: 0;
	background: #428389;

	.info-box {
		width: 100%;
		height: auto;

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
			}
		}
	}

	.con-box {
		width: 100%;
		height: calc(100vh - 48px);
		overflow-y: scroll;
		background: #ffffff;
		border-radius: 8px 8px 0px 0px;
		.form-item {
			.form-box {
				padding: 16px;
				box-sizing: border-box;
			}
		}
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
	padding: 10px 0;
	// border-bottom: 1px solid #d7dcd9;
	.van-cell__value {
		height: 48px;
		padding: 12px;
		background: #f4f6f9;
		border-radius: 8px;
	}
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
:deep(.van-picker__toolbar) {
	background: linear-gradient(180deg, rgba(22, 158, 154, 0.2) 0%, rgba(22, 158, 154, 0) 100%);
}
</style>
