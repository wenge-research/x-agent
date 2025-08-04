<template>
	<div class="instructType">
		<h2>技能分类管理</h2>
		<div class="search_box">
			<div>
				<w-input-search v-model="keyWords" :style="{width:'240px'}" placeholder="请输入关键词" clearable  @change="changeText" />
			</div>
			<w-button type="primary" @click="addFun" >
			<template #icon>
				<icon-plus />
			</template>
			新增
			</w-button>
		</div>
		<div>
			<w-table :data="tableData" :scroll="scroll" :pagination="pagination" @page-change="changepage" @page-size-change="changePagesize" :bordered="false">
				<template #columns>
				<w-table-column :width="200" title="类目ID" data-index="id"></w-table-column>
				<w-table-column title="类目名称" data-index="name" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="描述" data-index="describes" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="创建时间" :sortable="{sortDirections: ['ascend', 'descend'],}" data-index="createDate"></w-table-column>
				<w-table-column  title="创建人" data-index="createUser"></w-table-column>
				<w-table-column :width="200" title="操作" fixed="right">
					<template #cell="{ record }">
						<w-button type="text" size="small" @click="editFun(record)">编辑</w-button>
						<w-button v-if="record.status == 0" type="text" size="small" @click="publishFun(record)">上线</w-button>
						<w-popconfirm @ok="downFun(record)" content="确定下线？">
							<w-button v-if="record.status == 1" type="text" size="small">下线</w-button>
						</w-popconfirm>
						<w-button type="text" size="small" @click="delFun(record)">删除</w-button>
					</template>
				</w-table-column>
				</template>
			</w-table>
		</div>
		<w-modal modal-class="instructModal" :footer="false" v-model:visible="visibleInstruct" @cancel="handleCancel">
			<template #title>{{instructModalType == 'add' ? '新增分类' : '编辑分类'}}</template>
			<div>
				<w-form ref="formRef"
				:model="state.form"
				>
					<w-form-item field="name" label="类目名称" 
					:rules="[{ required: true, message: '请输入类目名称'}]"
					:validate-trigger="['change']">
						<w-textarea v-model="state.form.name" :max-length="64" :min-length="1" show-word-limit placeholder="必填项" :auto-size="{minRows:1,maxRows:1}" />
					</w-form-item>
					<w-form-item field="describes" label="描述">
						<w-textarea v-model="state.form.describes" :max-length="500" show-word-limit placeholder="请输入" :auto-size="{minRows:8,maxRows:8}" />
					</w-form-item>
					<w-form-item row-class="submit-item">
						<w-space>
							<w-button @click="visibleInstruct = false">取消</w-button>
							<w-button native-type="submit" type="primary" @click="handleSubmit">确定</w-button>
						</w-space>
					</w-form-item>
				</w-form>
			</div>
		</w-modal>
	</div>

</template>

<script lang="ts" setup>
import { onMounted, ref,reactive,unref,computed,watch } from 'vue';
import { IconPlus } from 'winbox-ui-next/es/icon';
import { Modal, Message } from 'winbox-ui-next';
import { getCategoryList,categoryUp,categoryDown,addCategory,deleteCategory } from '/@/api/manage'   
const pagination = ref({total: 0,pageSize: 10,'show-total': true,'show-page-size': true, 'show-jumper': true})
const tableData = ref([])
const instructModalType = ref('add')
const keyWords = ref('')
const visibleInstruct = ref(false)
const formRef = ref(null)
const state = reactive({
  form: {
	id: '',
	name: '',
    describes: '',
  },
});
const changeText = () => {
	current.value = 1
	init()
}
const current = ref(1)
const size = ref(10)
const loading = ref(false)
const init = async() => {
	loading.value = true
	let data = {
		current: current.value,
		size: size.value,
		keyword: keyWords.value,
	}
	let res = await getCategoryList(data);
     if(res.code === 200){
		loading.value = false
		tableData.value = res.data.records
		pagination.value.total = res.data.total
		//document.querySelector('.w-table-body').scrollTop = 0
     }else{
		loading.value = false
	 }
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
const addFun = () => {
	instructModalType.value = 'add'
	visibleInstruct.value = true
}
const editFun = (item) => {
	instructModalType.value = 'edit'
	visibleInstruct.value = true
	state.form.name = item.name
	state.form.id = item.id
	state.form.describes = item.describes
	
}
const publishFun = async(item) => {
	const res = await categoryUp(item.id)
	if(res?.code === 200) {
        visibleInstruct.value = false
		Message.success('发布成功')
		init()
    }else{
		Message.error(res.msg)
	}
}
const downFun = async(item) => {
	const res = await categoryDown(item.id)
	if(res?.code === 200) {
        visibleInstruct.value = false
		Message.success('下架成功')
		init()
    }else{
		Message.error(res.msg)
	}
}
const delFun = (item) => {
	Modal.warning({
		title: '您确定要删除该分类吗？',
		content: `删除后分类将无法恢复，请谨慎操作`,
		closable: true,
		okText: '确定',
		cancelText: '取消',
		hideCancel: false,
		modalClass: 'delInstructModal',
		onOk: async() => {
			const res = await deleteCategory(item.id)
			if(res?.code === 200) {
				Message.success('删除成功')        
				init()
			}
		},
	});
}
const handleCancel = () => {
	unref(formRef).resetFields();
	visibleInstruct.value = false
}
const handleSubmit = () => {
  unref(formRef).validate((errors: Object) => {
    if (!errors) {
		submit(state.form)
    }
  })
}
// 提交数据
const submit = async (data: object) => {
  try {
	let type = instructModalType.value == 'add' ? 'post' : 'put'
    const res = await addCategory(data,type)
    if(res?.code === 200) {
        visibleInstruct.value = false
		let msg = instructModalType.value == 'add' ? '添加成功' : '编辑成功'
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
watch(
	() => visibleInstruct.value,
	(val) => {
		if(!val){
			state.form.name = ''
			state.form.describes = ''
		}
	}
);
const scroll = ref({
      y: 500,
	  x: 1080
});
</script>

<style lang="scss" scoped>
.instructType {
	h2{
		height: 28px;
		font-size: var(--font20);
		font-weight: bold;
		color: #181B49;
		line-height: 28px;
		margin-bottom: 20px;
	}
	h3{
		font-size: var(--font16);
		font-weight: bold;
		color: #181B49;
		-webkit-line-clamp: 1;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	.search_box{
		display: flex;
		margin-bottom: 20px;
		justify-content: space-between;
		.w-btn{
			font-size: var(--font16);
		}
		:deep(.w-select){
			width: 160px;
			margin-right: 12px;
		}

	}
	:deep(.w-table){
		.w-table-header{
			background: none;
		}
		.w-table-th{
			color: #646479;
			border-bottom: 1px solid #E4E8EE;
		}
		.w-table-pagination{
			margin-top: 30px;
		}
		.table-p{
			font-size: var(--font14);
			color: #9A99AA;
			line-height: 20px;
			-webkit-line-clamp: 1;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			overflow: hidden;
			text-overflow: ellipsis;
		}
	}
	.w-btn-text {
		height: 22px;
		padding: 0;
		color: rgb(var(--primary-6));
		margin-right: 10px;
	}
	:deep(.w-badge-status-dot){
		width: 8px;
		height: 8px;
		margin-right: 6px;
	}

}

</style>
<style lang="scss">
	.instructModal{
		width: 600px;
		.submit-item {
			.w-form-item-content{
				justify-content:flex-end;
			}
		}
	}
	.delInstructModal{
		width: 480px;
		padding: 32px;
		.w-modal-header{
			padding: 5px 32px;
			margin-bottom: 8px;
		}
		.w-modal-body{
			padding-left: 60px;
			color: #646479;
			font-size: var(--font16);
		}
		.w-modal-footer{
			padding: 0px;
			text-align: right;
		}
	}

</style>

