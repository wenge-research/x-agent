<template>
	<div class="instruct">
		<h2>技能应用管理</h2>
		<div class="search_box">
			<div>
				<w-select  v-model="categoryType" placeholder="所属分类" @change="changeText" clearable>
					<w-option v-for="(item, index) in categoryData" :label="item.name" :value="item.id" :key="index">{{ item.name }}</w-option>
				</w-select>

				<w-input-search v-model="keyWords" @search="changeText" :style="{width:'240px'}" placeholder="请输入关键词" clearable @clear="changeText" @change="changeText" />
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
				<w-table-column :width="200" title="技能ID" data-index="id"></w-table-column>
				<w-table-column title="技能名称" data-index="name" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column  title="所属分类" data-index="categories" :tooltip="true" :ellipsis="true">
				</w-table-column>
				<w-table-column  title="技能Prompt" data-index="prompt" :tooltip="true" :ellipsis="true">
				</w-table-column>
				<w-table-column title="示例" data-index="promptShow" :tooltip="true" :ellipsis="true">
				</w-table-column>
				<w-table-column title="创建时间" :sortable="{sortDirections: ['ascend', 'descend'],}" data-index="createDate"></w-table-column>
				<w-table-column title="创建人" data-index="createUser">
				</w-table-column>
				<w-table-column :width="200" title="操作" fixed="right">
					<template #cell="{ record }">
						<w-button type="text" size="small" @click="editFun(record)">编辑</w-button>
						<w-button v-if="record.status == 0" type="text" size="small" @click="publishFun(record)">上线</w-button>
						<w-popconfirm @ok="downFun(record)" content="确定下线？">
							<w-button v-if="record.status == 1" type="text" size="small">下线</w-button>
						</w-popconfirm>
						<!-- <w-button type="text" size="small" @click="copyFun(record)">复制</w-button> -->
						<w-button type="text" size="small" @click="delFun(record)">删除</w-button>
					</template>
				</w-table-column>
				</template>
			</w-table>
		</div>
		<w-modal modal-class="instructModal" :footer="false" v-model:visible="visibleInstruct" @cancel="handleCancel">
			<template #title>{{instructModalType == 'add' ? '新增技能' : '编辑技能'}}</template>
			<div>
				<w-form ref="formRef"
				:model="state.form"
				>
					<w-form-item field="name" label="技能名称" 
					:rules="[{ required: true, message: '请输入技能名称'}]"
					:validate-trigger="['change']">
					<w-select placeholder="必填项" v-model="state.form.name" allow-create :max-length="64" :min-length="1">
						<w-option v-for="(item,index) in categoryAllList" :key="index">{{item.name}}</w-option>
					</w-select>
						
					</w-form-item>
					<w-form-item field="isBeta" label="是否标记beta">
						<w-checkbox v-model="state.form.isBeta"></w-checkbox>
					</w-form-item>
					<w-form-item field="category" label="所属分类" :rules="[{ required: true, type: 'array',minLength: 1,message: '请选择所属分类',}]"
					:validate-trigger="['change']">
						<w-select :style="{width:'100%'}" placeholder="多选项" multiple v-model="state.form.category">
							<w-option  v-for="(item, index) in categoryData" :key="index" :value="item.id">{{item.name}}</w-option>
						</w-select>
					</w-form-item>
					<w-form-item field="prompt" label="Prompt" :rules="[{required: true, message: '请输入Prompt' }]"
					:validate-trigger="['change']">
						<w-textarea v-model="state.form.prompt" :max-length="4000" show-word-limit placeholder="请输入模版内容，插值参数通过大括号{}中填写定义，支持英文、数据、下划线，且不能以数字开头，长度2-30。示例：文章内容：{artile}，请以{number}字数进行总结" :auto-size="{minRows:4,maxRows:4}" />
					</w-form-item>
					<w-form-item field="prompt" label="变量" >
						<w-table :columns="columns" :data="promptData" :pagination="false" style="width: 100%;">
							<template #name="{ rowIndex }">
								<w-input v-model="promptData[rowIndex].name" />
							</template>
							<template #type="{ rowIndex }">
							<w-select style="width: 100px" v-model="promptData[rowIndex].type">
								<w-option v-for="value in options" :key="value.id" :value="value.id">{{value.name}}</w-option>
							</w-select>
							</template>
							<template #value ="{ rowIndex }">
								<w-input v-model="promptData[rowIndex].value" />
							</template>
							<template  #option="{ rowIndex }">
								<w-button type="text" size="small" @click="delList(rowIndex)"><CoolRemoveCircleLineWe  size="16" color="rgb(var(--gray-5))"/></w-button>
							</template>
							<template #empty>
								<div class="nodata">暂无数据</div>
							</template>
							
						</w-table>
						<div @click="addList" style="position:absolute; bottom: 74px;left: 114px;cursor:pointer;"><CoolAddCircleLineWe  size="16" color="rgb(var(--gray-5))"/></div>
					</w-form-item>
					<w-form-item row-class="submit-item">
						<w-space>
							<w-button @click="visibleInstruct = false">取消</w-button>
							<w-tooltip :content="state.form.promptShow" v-if="state.form.prompt.length > 0"  @popup-visible-change="getComplete">
								<w-button>查看完整示例</w-button>
							</w-tooltip>
							<w-button native-type="submit" type="primary" @click="handleSubmit">确定</w-button>
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
import { listChatBasePrompt } from '/@/api/knowledge';
import { pagePrompt,getPromptById,listIndustry,addInstrctList,editInstrctList,promptUp,promptDown,promptDelete } from '/@/api/manage'
const pagination = ref({total: 0,pageSize: 10,'show-total': true,'show-page-size': true, 'show-jumper': true})
const tableData = ref([])
const instructModalType = ref('add')
const categoryType = ref('')
const publishType = ref('')
const keyWords = ref('')
const visibleInstruct = ref(false)

const options = ref([{
	name: '字符串',
	id: 0,
},
{
	name: '枚举值',
	id: 1,
}
])
const columns = ref([{
      title: '变量名',
      dataIndex: 'name',
      slotName: 'name'
    }, {
      title: '类型',
      dataIndex: 'type',
      slotName: 'type'
    }, {
      title: '示例',
      dataIndex: 'value',
      slotName: 'value'
    }, {
      title: '操作',
      dataIndex: 'option',
      slotName: 'option'
    }])
const promptData = ref([]);
const categoryData = ref([])
const formRef = ref(null)
const state = reactive({
  form: {
	id: '',
	name: '',
    prompt: '',
	category: [],
	promptShow: '',
	isBeta: false
  },
});

const addList = () => {
	promptData.value.push({
		name: '',
		type: '',
		value: '',
	})
}
const delList = (item) => {
	promptData.value.splice(item,1)

}


const getComplete = async() => {
	let str = state.form.prompt
	promptData.value.length && promptData.value.forEach((item) => {
		let key = `{${item.name}}`
		let s = item.value
		if(item.type == 1){ 
			const arr = item.value.split(',')
			s = arr[0] || ''
			
		} 
		str = str.replace(new RegExp(key,'g'),s);
	})

	state.form.promptShow = str


}
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
		category: categoryType.value,
		published: publishType.value
	}
	let res = await pagePrompt(data);
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
const initListIndustry = async() => {
	let res = await listIndustry();
	if(res.code === 200){
		categoryData.value = res.data
     }
}

const addFun = () => {
	instructModalType.value = 'add'
	visibleInstruct.value = true
}
const editFun = async(item) => {
	let res = await getPromptById(item.id,'get');
     if(res.code === 200){
		state.form.name = res.data.name
		state.form.prompt = res.data.prompt
		state.form.id = res.data.id
		state.form.category = res.data.appCategoryIds
		promptData.value = res.data.promptParam
		state.form.promptShow = res.data.promptShow
		state.form.isBeta = res.data.isBeta == 0 ? false : true
		
     }

	instructModalType.value = 'edit'
	visibleInstruct.value = true

}
const publishFun = async(item) => {
	const res = await promptUp(item.id)
	if(res?.code === 200) {
        visibleInstruct.value = false
		Message.success('发布成功')
		init()
    }else{
		Message.error(res.msg)
	}
}
const downFun = async(item) => {
	
	const res = await promptDown(item.id)
	if(res?.code === 200) {
        visibleInstruct.value = false
		Message.success('下架成功')
		init()
    }else{
		Message.error(res.msg)
	}
}
const copyFun = async(item) => {
	let data = {
		id: '',
		name: `${item.name}_副本`,
		prompt: item.prompt,
		industryId: item.industryId,
		published: 0,
		categories: item.categories,
	}
	const res = await promptSava(data)
	if(res?.code === 200) {
		Message.success('复制成功')
		init()
    }else{
		Message.error(res.msg)
	}
	
}
const delFun = (item) => {
	Modal.warning({
		title: '您确定要删除该技能吗？',
		content: `删除后技能将无法恢复，请谨慎操作`,
		closable: true,
		okText: '确定',
		cancelText: '取消',
		hideCancel: false,
		modalClass: 'delInstructModal',
		onOk: async() => {
			const res = await getPromptById(item.id,'delete');
			if(res?.code === 200) {
				Message.success('删除成功')        
				init()
			}
		},
	});
}


const categoryAllList = ref([])
const getList = async () =>{
  let params:any = {
    keyword: ''
  }
  let res = await listChatBasePrompt(params);
  if(res.code == 200 && res.data){
    categoryAllList.value = res.data || []
  }
}
const handleCancel = () => {
	unref(formRef).resetFields();
	visibleInstruct.value = false
}
const handleSubmit = () => {
  unref(formRef).validate((errors: Object) => {
    if (!errors) {
		getComplete()
		submit()
    }
  })
}
// 提交数据
const submit = async () => {
  try {
	if(promptData.value.length == 0){
		Message.warning('请输入变量')
		return
	}
	let api = instructModalType.value == 'add' ? addInstrctList : editInstrctList
	let data = {
		appCategoryIds: state.form.category,
		name: state.form.name,
		prompt: state.form.prompt,
		promptShow: state.form.promptShow,
		promptParam: promptData.value,
		isBeta: state.form.isBeta == false ? 0 : 1
	}
	if(instructModalType.value == 'edit'){
		data.id = state.form.id
	}
    const res = await api(data)
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
	getList()
	initListIndustry()
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

// watch(
//       () => state.form.prompt,
//       (str) => {
//         if(str.indexOf("{") != -1&&str.indexOf("}") != -1){
// 		let a1 =/\{(.+?)\}/g         //正则获取小括号
// 		let a2 = str.match(a1)?.toString() //因为输出的是数组，所以得转化成字符串
// 		var a3 = a2.replace(/{/g,"");         //用空字符替换掉括号
// 		a3 = a3.replace(/}/g,"");
// 		console.log(a3) //最终得到括号里面的数字
// 		}
//       }
//     );


watch(
	visibleInstruct,
	(val) => {
		if(!val){
			promptData.value = []
			state.form.name = ''
			state.form.prompt = ''
			state.form.category = []
			state.form.promptShow = ''
			state.form.isBeta = false
		}
	}
);

</script>

<style lang="scss" scoped>
.instruct {
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

</style>

