<template>
	<div class="user">
		<h2>用户管理</h2>
		<div class="search_box">
			<div>
				<w-select  v-model="userType" placeholder="用户类型" @change="changeText" clearable>
					<w-option v-for="(item, index) in userData" :label="item.name" :value="item.id" :key="index">{{ item.name }}</w-option>
				</w-select>
				<w-select  v-model="roleType" placeholder="角色"  @change="changeText" clearable>
					<w-option v-for="(item, index) in roleData" :label="item.name" :value="item.id" :key="index">{{ item.name }}</w-option>
				</w-select>
				<w-input-search v-model="keyWords" :style="{width:'240px'}" placeholder="请输入关键词" clearable  @change="changeText" />
			</div>
			<w-button type="primary" @click="addFun">
			<template #icon>
				<icon-plus />
			</template>
			新增
			</w-button>
		</div>
		<div>
			<w-table :data="tableData" :scroll="scroll" :loading="loading" :bordered="false" :pagination="pagination" @page-change="changepage" @page-size-change="changePagesize">
				<template #columns>
				<w-table-column title="手机号" data-index="phoneNumber"></w-table-column>
				<w-table-column title="用户名" data-index="name"></w-table-column>
				<w-table-column title="用户类型" data-index="type">
					<template #cell="{ record }">
						<span>{{record.type == 0 ? '闻歌员工' : '外部客户'}}</span>
					</template>
				</w-table-column>
				<w-table-column title="有效期" data-index="valid"></w-table-column>
				<w-table-column title="注册时间" data-index="createTime" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="最近登陆" data-index="loginTime" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="敏感词检测" >
					<template #cell="{ record }">
						<w-switch v-model="record.sensitiveCheck"  @change="sensitiveCheckFun(record)" checked-color="rgb(var(--primary-6))">
							<template #checked>
								开启
							</template>
							<template #unchecked>
								关闭
							</template>
							</w-switch>
					</template>
				</w-table-column>
				<w-table-column title="操作" fixed="right" :width="200">
					<template #cell="{ record }">
						<w-button type="text" size="small" @click="editFun(record)">编辑</w-button>
						<w-button type="text" size="small" @click="delFun(record)">删除</w-button>
						<w-button type="text" size="small" @click="gotoRecord(record.id)">问答记录</w-button>
					</template>
				</w-table-column>
				</template>
			</w-table>
		</div>
		<w-modal modal-class="userModal" :footer="false" v-model:visible="visibleUser" @cancel="handleCancel">
			<template #title>{{userModalType == 'add' ? '添加用户' : '编辑用户'}}</template>
			<div>
				<w-form ref="formRef"
				:model="state.form"
				>
					<w-form-item field="phoneNumber" label="手机号" 
					:rules="[
					{ required: true, message: '请输入手机号' },
					state.rules.phoneNumber
					]"
					:validate-trigger="['change']">
						<w-input v-model="state.form.phoneNumber" :disabled="userModalType == 'edit'" placeholder="必填项"/>
					</w-form-item>
					<w-form-item field="name" label="用户名">
						<w-input v-model="state.form.name" placeholder="请输入"/>
					</w-form-item>
					<w-form-item field="email" label="邮箱" :rules="[
					state.rules.email
					]"
					:validate-trigger="['change','input']">
						<w-input v-model="state.form.email" placeholder="请输入"/>
					</w-form-item>
					<w-form-item field="role" label="用户">
						<w-radio-group v-model="state.form.role" >
							<w-radio :value="0">普通用户</w-radio>
							<w-radio :value="1">管理员</w-radio>
						</w-radio-group>
					</w-form-item>
					<w-form-item field="validRange" label="有效时间">
						<w-range-picker v-model="state.form.validRange"  />
					</w-form-item>
					<w-form-item field="remark" label="备注">
						<w-textarea v-model="state.form.remark" placeholder="请输入" :auto-size="{minRows:2,maxRows:5}" />
					</w-form-item>
					<w-form-item row-class="submit-item">
						<w-space>
							<w-button @click="visibleUser = false">取消</w-button>
							<w-button native-type="submit" type="primary" @click="handleSubmit">确定</w-button>
						</w-space>
					</w-form-item>
				</w-form>
			</div>
		</w-modal>
	</div>
</template>

<script lang="ts" setup>
import { onMounted, ref,reactive,unref,watch } from 'vue';
import { Modal, Message } from 'winbox-ui-next';
import { IconPlus } from 'winbox-ui-next/es/icon';
import { userPage,userSave,userDelete,userDetail,userSensitiveCheck,clearToken } from '/@/api/manage'
import router from '/@/router';
const pagination = ref({total: 40,pageSize: 10,'show-total': true,'show-page-size': true, 'show-jumper': true})
const userType = ref('')
const roleType = ref('')
const keyWords = ref('')
const visibleUser = ref(false)
const loading = ref(false)
const userModalType = ref('add')
const userData = ref([
	{
		id: 0,
		name: '闻歌员工'
	},
	{
		id: 1,
		name: '外部客户'
	}
])
const current = ref(1)
const size = ref(10)
const tableData = ref([])
const roleData = ref([
	{
		id: 1,
		name: '管理员'
	},
	{
		id: 0,
		name: '普通用户'
	}
])
const formRef = ref(null)

const state = reactive({
  form: {
	id: '',
    phoneNumber: '',
	name: '',
    email: '',
	role: 0,
    remark: '',
	sensitiveCheck: false,
    validRange: []
  },
  rules: {
    phoneNumber: {
      validator: (val: string, callback: (arg: string) => void) => {
        const reg = /^(?:(?:\+|00)86)?1[03456789]\d{9}$/
        if (isNaN(+val) || !reg.test(val)) {
          callback('请输入正确的手机号')
        } 
      }
    },
	email: {
      validator: (val: string, callback: (arg: string) => void) => {
        const reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
		if(val){
			if (!reg.test(val)) {
			callback('请输入正确的邮箱！')
			} 
		}
      }
    },
	
  },
});
const changeText = () => {
	current.value = 1
	init()
}

const changePagesize = (v) => {
	size.value = v
	pagination.value.pageSize = v
	init()
}
const changepage = (v) => {
	current.value = v
	init()
}
const init = async() => {
	loading.value = true
	let data = {
		current: current.value,
		size: size.value,
		keyword: keyWords.value,
		type: userType.value,
		role: roleType.value
	}
	let res = await userPage(data);
     if(res.code === 200){
		loading.value = false
		tableData.value = res.data.records
		pagination.value.total = res.data.total
     }else{
		loading.value = false
	}
}
const addFun = () => {
	userModalType.value = 'add'
	visibleUser.value = true
}
const editFun = async(item) => {
	userModalType.value = 'edit'
	const res = await userDetail(item.id)
	if(res?.code === 200) {
		visibleUser.value = true
		state.form = res.data
		if(res.data.validFrom == '' || !res.data.validFrom){
			state.form.validRange =  []
		}else{
			state.form.validRange =  [res.data.validFrom.split(' ')[0],res.data.validTo.split(' ')[0]]
		}
		
	}
}
const delFun = (item) => {
	Modal.open({
		title: '提示',
		content: `确定删除${item.name}?`,
		closable: true,
		okText: '确定',
		cancelText: '取消',
		onOk: async() => {
			const res = await userDelete(item.id)
			if(res?.code === 200) {
				Message.success('删除成功')        
				init()
			}
		},
	});
}

const clearLine = async(item) => {
	item.onlineState = !item.onlineState
	const res = await clearToken(item.id)
	if(res?.code === 200) {
		Message.success('离线成功')
	}
}

const sensitiveCheckFun = async(item) => {
	let data = {
		id: item.id,
		enable: item.sensitiveCheck
	}
	const res = await userSensitiveCheck(data)
	if(res?.code === 200) {
		Message.success('操作成功')
	}
}
const gotoRecord = (id) => {
	router.push({ name: `manageRecordChat`, params: { userId: id} });
}
const handleCancel = () => {
	unref(formRef).resetFields();
	visibleUser.value = false
}

const handleSubmit = () => {
  unref(formRef).validate((errors: Object) => {
    if (!errors) {
		let startTime = ''
		let endTime = ''
		if(state.form.validRange){
			startTime = state.form.validRange == '' ? '' : state.form.validRange[0] + ' 00:00:00'
			endTime = state.form.validRange == '' ? '' : state.form.validRange[1] + ' 23:59:59'
		}
		state.form.validFrom = startTime
		state.form.validTo = endTime
		submit(state.form)
    }
  })
}
// 提交数据
const submit = async (data: object) => {

  try {
    const res = await userSave(data)
    if(res?.code === 200) {
        visibleUser.value = false
		let msg = userModalType.value == 'add' ? '添加成功' : '编辑成功'
		Message.success(msg)
		unref(formRef).resetFields()
        
		init()
    }else{
		Message.error(res.msg)
	}
  } catch (err) {
    Message.error('提交失败')
    throw new Error(err)
  }
}

onMounted(() => {
	init()
	resizeScrollHeight()
	window.addEventListener('resize', resizeScrollHeight);
});
const resizeScrollHeight = () => {
	let clientHeight = document.documentElement.clientHeight ||  document.body.clientHeight;
	let h = clientHeight - 370
	scroll.value.y = h
};

const scroll = ref({
		y: 500,
		x: 1080
});
</script>

<style lang="scss" scoped>
.user {
	h2{
		height: 28px;
		font-size: var(--font20);
		font-weight: bold;
		color: #181B49;
		line-height: 28px;
		margin-bottom: 20px;
	}
	.search_box{
		display: flex;
		margin-bottom: 20px;
		justify-content: space-between;
		:deep(.w-select){
			width: 160px;
			margin-right: 12px;
		}

	}
	.w-btn-text {
		height: 22px;
		padding: 0;
		color: rgb(var(--primary-6));
		margin-right: 10px;
	}
	.w-btn-disabled{
		color: var(--color-primary-light-3);
	}
	:deep(.w-table-pagination){
		margin-top: 30px;
	}


}

</style>
<style lang="scss">
	.userModal{
		width: 480px;
		.submit-item {
			.w-form-item-content{
				justify-content:flex-end;
			}
		}
	}

</style>
