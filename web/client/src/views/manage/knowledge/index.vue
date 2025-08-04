<template>
	<div class="knowledge">
		<h2>知识库管理</h2>
		<div class="search_box">
			<div>	
				<w-select v-model="roleValue" placeholder="角色查询" @change="changeText" clearable>
					<w-option v-for="(item, index) in roleData" :label="item.name" :value="item.id" :key="index">{{ item.name }}</w-option>
				</w-select>			
				<w-select  v-model="categoryType" placeholder="权限" @change="changeText" clearable>
					<w-option v-for="(item, index) in categoryData" :label="item.name" :value="item.id" :key="index">{{ item.name }}</w-option>
				</w-select>
				<w-input-search v-model="keyWords" :style="{width:'280px'}" placeholder="请输入知识库关键词/用户手机号" clearable  @change="changeText" />
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
				<w-table-column :width="200" title="知识库ID" data-index="id"></w-table-column>
				<w-table-column title="知识库名称"  data-index="name" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="描述" data-index="descr" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="创建人"   data-index="creatorName" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="类型" data-index="authority"  >
					<template #cell="{ record }">
						<p v-if="record.authority == 0">全公开</p>
						<p v-else-if="record.authority == 1">对内公开</p>
						<p v-else-if="record.authority == 2">私有</p>
					</template>
				</w-table-column>
				<w-table-column  title="文件数" data-index="fileCount"></w-table-column>
				<w-table-column title="字数统计"  data-index="charCount" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column  title="创建时间" :tooltip="true" :ellipsis="true" :sortable="{sortDirections: ['ascend', 'descend'],}" data-index="createTime"></w-table-column>
				
				<w-table-column title="操作" fixed="right" :width="200">
					<template #cell="{ record }">
						<w-button type="text" size="small" @click="editFun(record)">编辑</w-button>
						<w-button type="text" size="small" @click="delFun(record)">删除</w-button>
						<w-button type="text" size="small" @click="expansion(record)">扩容</w-button>
					</template>
				</w-table-column>
				</template>
			</w-table>
		</div>
		<w-modal modal-class="instructModal" :footer="false" v-model:visible="visibleInstruct" @cancel="handleCancel">
			<template #title>{{instructModalType == 'add' ? '新增知识库' : '编辑知识库'}}</template>
			<div>
				<w-form ref="formRef"
				:model="state.form"
				>
					<w-form-item field="name" label="知识库名称" 
					:rules="[{ required: true, message: '请输入知识库名称'}]"
					:validate-trigger="['change']">
						<w-input v-model="state.form.name" :max-length="20" :min-length="1" show-word-limit placeholder="必填项" />
					</w-form-item>
					<w-form-item field="icon" label="图标">
						<ul class="iconList">
							<li class="icon" :active="index == state.form.icon" v-for="(item,index) in iconList" :key="index" @click="state.form.icon = index">
								<span>{{ item }}</span>
							</li>
						</ul>
					</w-form-item>
					<w-form-item field="descr" label="简介">
						<w-textarea v-model="state.form.descr" :max-length="40" show-word-limit placeholder="请简单介绍下这个知识库" :auto-size="{minRows:4,maxRows:4}" />
					</w-form-item>
					<w-form-item field="authority" label="权限">
						<w-radio-group v-model="state.form.authority" style="line-height: 1;height: 16px;">
							<w-radio :value="0">全公开</w-radio>
							<w-radio :value="1">对内公开</w-radio>
							<w-radio :value="2">私有</w-radio>
						</w-radio-group>
					</w-form-item>


					<w-form-item row-class="submit-item">
						<w-space>
							<w-button @click="visibleInstruct = false">取消</w-button>
							<w-button :loading="loadingBtn" native-type="submit" type="primary" @click="handleSubmit">确定</w-button>
						</w-space>
					</w-form-item>
				</w-form>
			</div>
		</w-modal>
	</div>

</template>

<script lang="ts" setup>
import { onMounted, ref,reactive,unref,computed, watch } from 'vue';
import { IconPlus } from 'winbox-ui-next/es/icon';
import { Modal, Message } from 'winbox-ui-next';
import { Session } from '/@/utils/storage';
import { debounce } from 'lodash-es'

import { knowledgePage,createKnowledge,editKnowledge,deleteKnowledge } from '/@/api/manage'
const pagination = ref({total: 0,pageSize: 10,'show-total': true,'show-page-size': true, 'show-jumper': true})
const tableData = ref([])
const instructModalType = ref('add')
const categoryType = ref()
const keyWords = ref('')
const loadingBtn = ref(false)
const visibleInstruct = ref(false)
const iconList = ref(['💻','📝','🧩','📍','🌠','📖'])
const categoryData = ref([{
	name: '全部公开',
	id: 0
},{
	name: '对内公开',
	id: 1
},{
	name: '私有',
	id: 2
}])
const formRef = ref(null)
const file = ref('')
const roleValue = ref('')
const roleData = ref([{
		name: '普通用户',
		id: 0
	},{
		name: '管理员',
		id: 1
	},{
		name: '外部用户',
		id: 2
	},{
		name: '闻歌员工',
		id: 3
	}])
const state = reactive({
  form: {
	id: '',
	name: '',
	descr: '',
	authority: 2,
	icon: 0,
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
		name: keyWords.value,
		authority: categoryType.value,
		role: roleValue.value
	}
	let res = await knowledgePage(data);
	if(res.code === 200){
		loading.value = false
		tableData.value = res.data.records
		pagination.value.total = res.data.total
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
const editFun = async(item) => {
	state.form.name = item.name
	state.form.descr = item.descr
	state.form.id = item.id
	state.form.authority = item.authority
 	const result = iconList.value.findIndex(element => element == item.icon);
	state.form.icon = result
	instructModalType.value = 'edit'
	visibleInstruct.value = true

}
const delFun = (item) => {
	Modal.warning({
		title: '您确定要删除该知识库吗？',
		content: `删除后知识库将无法恢复，请谨慎操作`,
		closable: true,
		okText: '确定',
		cancelText: '取消',
		hideCancel: false,
		modalClass: 'delInstructModal',
		onOk: async() => {
			const res = await deleteKnowledge( {id: item.id,name: item.name});
			if(res?.code === 200) {
				Message.success('删除成功')        
				init()
			}
		},
	});
}

const expansion = (item) => {
	Message.info('敬请期待')
}
const handleCancel = () => {
	unref(formRef).resetFields();
	visibleInstruct.value = false
}
const handleSubmit = () => {
  unref(formRef).validate((errors: Object) => {
    if (!errors) {
		submit()
    }
  })
}
// 提交数据
const submit = debounce(async () => {
  loadingBtn.value = true
  try {
	let data = {
		name: state.form.name,
		descr: state.form.descr,
		authority: state.form.authority,
		icon: iconList.value[state.form.icon],
		id: state.form.id,
	}
	let api = instructModalType.value == 'add' ? createKnowledge : editKnowledge
    const res = await api(data)
	loadingBtn.value = false
    if(res?.code === 200) {
        visibleInstruct.value = false
		let msg = instructModalType.value == 'add' ? '新建成功' : '编辑成功'
		Message.success(msg)
		unref(formRef).resetFields()
		init()
    }else{
		Message.error(res.msg)
	}
  } catch (err) {
	loadingBtn.value = false
    Message.error('提交失败')
    throw new Error(err)
  }
},500)



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


watch(
	visibleInstruct,
	(val) => {
		if(!val){
			file.value = ''
			state.form = {
				id: '',
				name: '',
				descr: '',
				authority: 2,
				icon: 0,
			}
		}
	}
);

</script>

<style lang="scss" scoped>
.knowledge {
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
		width: 700px;
		.w-modal-body{
			padding-left: 0;
		}
		
		.submit-item {
			.w-form-item-content{
				justify-content:flex-end;
			}
		}
		.nodata{
			text-align: center;
			color: rgb(var(--gray-5));
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
	.iconList{
		width: 100%;
		.icon{
			margin-right: 12px;
			margin-bottom: 12px;
			display: inline-flex;
			align-items: center;
			justify-content: center;
			list-style: none;
			width: 60px;
			height: 60px;
			background: rgba($color: #6600FF, $alpha: 0.06);
			border-radius: 8px;
			font-size: var(--font24);
			font-family: AppleColorEmoji;
			color: #355EFF;
			cursor: pointer;
			&[active = true]{
				border: 1px solid #355EFF;
			}
		}
		.icon:nth-child(1n){
			background: rgba(53,94,255,0.06);
		}
		.icon:nth-child(2n){
			background: rgba(7, 190, 184, 0.06);
		}
		.icon:nth-child(3n){
			background: rgba(102, 0, 255, 0.06);
		}
		.icon:nth-child(4n){
			background: rgba(255, 98, 0, 0.06);
		}
		.icon:nth-child(5n){
			background: rgba(245, 75, 91, 0.06);
		}
		.icon:nth-child(6n){
			margin-right: 0;
			background: rgba(53, 94, 255, 0.06);
		}
	}

</style>

