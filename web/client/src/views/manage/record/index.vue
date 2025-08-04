<template>
	<div class="manage">
		<h2>智能问答记录</h2>
		<div class="search_box">
			<div>
				<w-select  v-model="applicationValue" placeholder="应用行业" @change="applicationChange" clearable>
					<w-option v-for="(item, index) in applicationData" :label="item.name" :value="item.id" :key="index">{{ item.name }}</w-option>
				</w-select>
				<w-range-picker
					v-model="rangeValue"
					@change="applicationChange"
				/>
				<w-input-search v-model="keyWords" :style="{width:'240px'}" placeholder="请输入关键词" clearable  @change="changeText" />
			</div>
			<w-button type="primary" @click="exportXlsxClick">
			<template #icon>
				<CoolDaochu  size="16" color="#fff"/>
			</template>
			导出
			</w-button>
		</div>
		<div>
			<w-table ref="table" :data="tableData"  :scroll="scroll" :loading="loading" :bordered="false" :pagination="pagination" @page-change="changepage" @page-size-change="changePagesize">
				<template #columns>
				<w-table-column title="问题及答案" data-index="name">
					<template #cell="{ record }" >
						<img :src="record.imgUrl" alt="" style="max-width: 200px;">
						<w-tooltip :content="record.question">
							<h3>{{record.question}}</h3>
						</w-tooltip>
						<w-tooltip :content="record.answer">
							<p class="table-p">{{record.answer}}</p>
						</w-tooltip>
						
					</template>
				</w-table-column>
				<w-table-column  :width="400" title="应用行业" data-index="app" >
					<template #cell="{ record }">
						<h3>{{record.app}}</h3>
						<w-tooltip :content="record.dialogueParam">
							<p class="table-p">{{record.dialogueParam }}</p>
							<!-- <div class="tagList" v-for="(item,key) in paramData(record.dialogueParam)" :key="key" >
									<span>{{key}}</span>：<span v-if="typeof(item) === 'object'">
										<span  v-for="(val,index) in item" :key="index+val">
											{{val}}
										</span>
									</span>
									<span v-else>{{item}}</span>
									<span class="tip">
										|
									</span>
							</div> -->
						</w-tooltip>
					</template>
				</w-table-column>
				<w-table-column :width="200" title="用户" data-index="user"></w-table-column>
				<w-table-column :width="200" title="时间" data-index="questionTime" :ellipsis="true" :tooltip="true"></w-table-column>
				</template>
			</w-table>
		</div>

		<w-modal modal-class="exportRecordModal" v-model:visible="visibleExport" @ok="handleSubmit" @cancel="resetFields">
			<template #title>
			导出
			</template>
			<div>
				<w-form ref="formRef"
				:model="form"
				:style="{ width: '100%' }"
				>
					<w-form-item field="rangeValue" label="时间">
						<w-range-picker v-model="form.rangeValue" style="width: 100%;" />
					</w-form-item>
					<w-form-item field="appIds" label="应用类型">
						<w-transfer :title="['来源','目标']" :data="appIdsArr" v-model="form.appIds" show-search/>
					</w-form-item>

				</w-form>
			</div>
		</w-modal>
	</div>

</template>

<script lang="ts" setup>
import { onMounted, ref, reactive,computed} from 'vue';
import { dialoguePage,dialogueExport,listApp,userDetail } from '/@/api/manage'
import { useRoute } from 'vue-router';
const route = useRoute();
const loading = ref(false)
const pagination = ref({total: 0,pageSize: 10,'show-total': true,'show-page-size': true, 'show-jumper': true})
const applicationValue = ref('')
const rangeValue = ref(['', ''])
const keyWords = ref('')
const size = ref(10)
const current = ref(1)
const appIdsArr = ref([])
const applicationData = ref([])
const tableData = ref([])
const userId = ref('')
const visibleExport = ref(false)
const form = ref({
	rangeValue:['',''],
	appIds: []
})
const changeText = (val) => {
	current.value = 1
	keyWords.value = val
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

const applicationChange = () => {
	current.value = 1
	init()
}
const exportXlsxClick = () => {
	visibleExport.value = true
}
const exportXlsx = async(data) => {
	let res = await dialogueExport(data);
	downLoadXls(res)

}
const downLoadXls = (res) => {
      const fileName = '问答记录.xlsx'
      const blob = new Blob([res],{
        type: "application/octet-stream; charset=utf-8"
      });
      if('download' in document.createElement('a')) {
        //非IE下载
        const a = document.createElement('a') //创建一个a标签
        a.download = fileName //指定文件名称
        a.style.display = 'none' //页面隐藏
        a.href = URL.createObjectURL(blob) // href用于下载地址
        document.body.appendChild(a) //插到页面上
        a.click() //通过点击触发
        URL.revokeObjectURL(a.href) //释放URL 对象
        document.body.removeChild(a) //删掉a标签
      }else{
        navigator.msSaveBlob(blob,fileName)
      }
}

const getAppList = async() => {
	let res = await listApp();
	applicationData.value = res.data
	res.data.map((item) => {
		let obj = {
			value: item.id,
			label: item.name,
		}
		appIdsArr.value.push(obj)
	})
}
const table = ref(null)
const init = async() => {
	loading.value = true
	let startTime = ''
	let endTime = ''
	if(rangeValue.value){
		startTime = rangeValue.value[0]  ==  '' ? '' : rangeValue.value[0] + ' 00:00:00'
		endTime = rangeValue.value[1]  ==  '' ? '' : rangeValue.value[1] + ' 23:59:59'
	}

	let data = {
		appId: applicationValue.value,
		current: current.value,
		dialogueType: 'QA',
		size: size.value,
		keyword: keyWords.value,
		startTime: startTime,
		endTime: endTime,
		userId: userId.value
	}
	let res = await dialoguePage(data);
     if(res.code === 200){
		loading.value = false
		tableData.value = res.data.records
		pagination.value.total = res.data.total
		document.querySelector('.w-table-body').scrollTop = 0
     }else{
		loading.value = false
	}


	
}
const handleSubmit = () => {
	let startTime = ''
	let endTime = ''
	if(form.value.rangeValue){
		startTime = form.value.rangeValue[0]  ==  '' ? '' : form.value.rangeValue[0] + ' 00:00:00'
		endTime = form.value.rangeValue[1]  ==  '' ? '' : form.value.rangeValue[1] + ' 23:59:59'
	}

	let data = {
		appIds: form.value.appIds,
		endTime: endTime,
		startTime: startTime,
		dialogueType: 'QA',
		userId: userId.value
	}
	exportXlsx(data)
	resetFields()
}
const resetFields = () => {
	form.value.rangeValue = ['','']
	form.value.appIds = []

}
const getUserName = async() => {
	let res = await userDetail(route.params.userId);
	keyWords.value = res.data.name

}
onMounted(() => {
	userId.value = route.params.userId
	getAppList()
	if(userId.value){
		getUserName()
	}
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
	  x:1080
});
</script>

<style lang="scss" scoped>
.manage {
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
		.w-btn{
			font-size: var(--font16);
		}
		:deep(.w-select){
			width: 160px;
			margin-right: 12px;
			margin-bottom: 12px;
		}
		:deep(.w-picker){
			width: 220px;
			margin-right: 12px;
			height: 36px;
			border-radius: 3px;
			margin-bottom: 12px;
		}

	}
	h3{
		font-size: var(--font16);
		font-weight: bold;
		color: #181B49;
		-webkit-line-clamp: 2;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
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
			-webkit-line-clamp: 2;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			overflow: hidden;
			text-overflow: ellipsis;
		}
	}
}

</style>
<style lang="scss">
	.exportRecordModal{
		width: 650px;
		.w-transfer{
			.w-scrollbar{
				height: 300px;
			}
		}
	}
</style>
