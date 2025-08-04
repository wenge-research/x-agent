<template>
	<div class="apply">
		<h2>申请管理</h2>
		<div class="search_box">
			<div>
				<w-select  v-model="tradeType" placeholder="所属行业" @change="changeText" clearable>
					<w-option v-for="(item, index) in tradeData" :label="item.content" :value="item.code" :key="index">{{ item.content }}</w-option>
				</w-select>
				<w-select  v-model="examineType" placeholder="审核状态" clearable @change="changeText">
					<w-option v-for="(item, index) in examineData" :label="item.name" :value="item.id" :key="index">{{ item.name }}</w-option>
				</w-select>
				<w-input-search v-model="keyWords" :style="{width:'240px'}" placeholder="请输入关键词" clearable @search="changeText"  @change="changeText" />
			</div>
			<w-button type="primary" @click="exportXlsxClick">
			<template #icon>
				<CoolDaochu  size="16" color="#fff"/>
			</template>
			导出
			</w-button>
		</div>
		<div>
		<w-table ref="table" :data="tableData" :scroll="scroll" :loading="loading" :bordered="false" :pagination="pagination" @page-change="changepage" @page-size-change="changePagesize">
				<template #columns>
				<w-table-column title="姓名" data-index="username" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="手机号" data-index="phoneNumber" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="邮箱" data-index="email" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="公司名称" data-index="company" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="所属行业" data-index="trade"></w-table-column>
				<w-table-column title="业务场景" data-index="scenario" :tooltip="true" :ellipsis="true"></w-table-column>
				<w-table-column title="服务对象" data-index="scenarioType">
					<template #cell="{ record }">
						<p v-if="record.scenarioType == 0">内部</p>
						<p v-else-if="record.scenarioType == 1">外部</p>
					</template>
				</w-table-column>
				
				<w-table-column title="申请时间" :width="180" data-index="createTime" :sortable="{sortDirections: ['ascend', 'descend'],}"></w-table-column>
				<w-table-column title="状态" data-index="auditStatus">
					<template #cell="{ record }">
						<p v-if="record.auditStatus == 0"><w-badge type="processing" />待审核</p>
						<p v-else-if="record.auditStatus == 1"><w-badge type="success" />已通过</p>
						<p v-else-if="record.auditStatus == 2"><w-badge type="danger" />已拒绝</p>
					</template>
				</w-table-column>
				<w-table-column title="操作" fixed="right" :width="100" >
					<template #cell="{ record }">
					<w-button type="text" size="small" @click="handleClick(record)">审核</w-button>
					</template>
				</w-table-column>
				</template>
			</w-table>
		</div>
		<w-modal modal-class="exportModal" v-model:visible="visibleExport" @ok="handleSubmit" @cancel="resetFields">
			<template #title>导出</template>
			<div>
				<w-form ref="formRef" :model="form" :style="{ width: '100%' }">
					<w-form-item field="rangeValue" label="申请时间">
						<w-range-picker v-model="form.rangeValue" style="width: 100%;" />
					</w-form-item>
					<w-form-item field="result" label="审批结果">
						<w-checkbox-group v-model="form.result" >
							<w-checkbox value="0">待审核</w-checkbox>
							<w-checkbox value="1">已通过</w-checkbox>
							<w-checkbox value="2">已拒绝</w-checkbox>
						</w-checkbox-group>
					</w-form-item>
				</w-form>
			</div>
		</w-modal>
		<w-modal modal-class="applyModal" :footer="false" v-model:visible="visible" @cancel="handleCancel">
			<template #title>审核</template>
			<div>
			<w-carousel
				:style="{
				width: '100%',
				height: '370px',
				}"
				animation-name="fade"
				v-model:current="currentCarousel"
				@change="changeCarousel"
			>
			<w-carousel-item v-for="(item,index) in tableData" :key="index">
				<div class="carousel-body">
					<div class="modal-title">
						<h3 class="question" :title="item.username">{{item.username}}</h3>
						<p><span class="label">手机号</span><span class="describ">{{item.phoneNumber}}</span></p>
						<p><span class="label">邮箱</span><span class="describ">{{item.email}}</span></p>
						<p><span class="label">申请时间</span><span class="describ">{{item.createTime}}</span></p>
						<p><span class="label">公司名称</span><span class="describ">{{item.company}}</span></p>
						<p><span class="label">所属行业</span><span class="describ">{{item.trade}}</span></p>
						<p><span class="label">服务对象</span><span class="describ">{{item.scenarioType == 0 ? '内部': '外部'}}</span></p>
						<p><span class="label">业务场景</span><span class="describ" :title="item.scenario">{{item.scenario}}</span></p>
					</div>
					<div class="check_foot">
						<div v-if="!item.auditStatus == 0">
							<div v-if="item.auditStatus == 1" class="card approvedCard">
								<span class="icon"><CoolTongguo color="#fff"  size="18"/></span>
								已通过且发送邮件，邀请码：{{item.invitationCode}}
							</div>
							<div v-if="item.auditStatus == 2"  class="card disapprovedCard">
								<span class="icon"><CoolJujue color="#fff"  size="18"/></span>
								已拒绝，理由：{{item.auditComment}}
							</div>
						</div>
						<div v-if="item.auditStatus == 0" style="display: flex;">
							<w-button type="secondary" :loading="sendLoading" @click="approved(item)">
								<template v-if="!sendLoading"><CoolTongguo  size="18"/>通过且发送邮箱</template>
								<template v-else>发送中</template>
							</w-button>
							<w-popconfirm content-class="popconfirm" @popup-visible-change="popupChange" okText="提交" @cancel="cancelReason" @ok="disApproved(item)">
								<template #content>
									<h2>拒绝原因</h2>
									<w-textarea v-model="disApprovedComment" placeholder="拒绝原因，非必填" />
								</template>
								<template #icon></template>
								<w-button type="secondary" status="danger"><CoolJujue  size="18" />拒绝</w-button>
							</w-popconfirm>
						</div>
					</div>
				</div>
			</w-carousel-item>
			</w-carousel>
			</div>
		</w-modal>
	</div>

</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { Message } from 'winbox-ui-next'

import { consultPageList,consultExport,consultListTrade,consultAudit } from '/@/api/manage'
const loading = ref(false)
const pagination = ref({total: 0,pageSize: 10,'show-total': true,'show-page-size': true, 'show-jumper': true})
const tradeType = ref('')
const examineType = ref('')
const keyWords = ref('')
const sendLoading = ref(false)
const disApprovedComment = ref('')
const tradeData = ref([])
const examineData = ref([
	{
		id: 0,
		name: '待审核'
	},
	{
		id:1,
		name: '已通过'
	},
	{
		id:2,
		name: '已拒绝'
	}
])
const tableData = ref([])
const visibleExport = ref(false)
const visible = ref(false)
const form = ref({
	rangeValue:['',''],
	result: ['0'],
})
const currentCarousel = ref(1)
const current = ref(1)
const changePagesize = (v) => {
	pagination.value.pageSize = v
	init()
}
const changepage = (v) => {
	current.value = v
	init()
}
const changeText = () => {
	current.value = 1
	init()
}
const init = async() => {
	loading.value = true
	let data = {
		current: current.value,
		size: pagination.value.pageSize,
		keyword: keyWords.value,
		auditStatus: examineType.value, //审核状态 0 未审核；1 通过；2 拒绝
		trade: tradeType.value //行业
	}
	let res = await consultPageList(data);
     if(res.code === 200){
		loading.value = false
		tableData.value = res.data.records
		pagination.value.total = res.data.total
		document.querySelector('.w-table-body').scrollTop = 0
     }else{
		loading.value = false
	}
}
const exportXlsxClick = () => {
	visibleExport.value = true
}
const handleSubmit = () => {
	let startTime = ''
	let endTime = ''
	if(form.value.rangeValue){
		startTime = form.value.rangeValue[0]  ==  '' ? '' : form.value.rangeValue[0] + ' 00:00:00'
		endTime = form.value.rangeValue[1]  ==  '' ? '' : form.value.rangeValue[1] + ' 23:59:59'
	}
	let data = {
		"endTime": endTime,
		"startTime": startTime,
		"auditStatus": form.value.result
	}
	exportXlsx(data)
}
const exportXlsx = async(data) => {
	let res = await consultExport(data);
	downLoadXls(res)

}
const downLoadXls = (res) => {
      const fileName = '申请管理.xlsx'
      const blob = new Blob([res],{
        type: "application/octet-stream; charset=utf-8"
      });
      if('download' in document.createElement('a')) {
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
const resetFields = () => {
	form.value.rangeValue = ['','']
	form.value.result = ['0']
}

const handleClick = (item) => {
	let index = tableData.value.findIndex((value)=>value.id == item.id);
	currentCarousel.value = index + 1
	visible.value = true
}

const approved = async(item) => {
	sendLoading.value = true
	let data = {
		auditStatus: 1,
		id: item.id,
		phoneNumber: item.phoneNumber
	}
	let res = await consultAudit(data);
	if(res.code == 200){
		sendLoading.value = false
		Message.success('发送成功');
		item.invitationCode = res.data
		item.auditStatus = 1
	}else{
		Message.warning('邮箱无效');
	}
}
const cancelReason=()=>{
	disApprovedComment.value=""
}
const disApproved = async(item) => {
	let data = {
		auditStatus: 2,
		id: item.id,
		phoneNumber: item.phoneNumber,
		auditComment: disApprovedComment.value
	}
	let res = await consultAudit(data);
	if(res.code == 200){
		item.auditStatus = 2
		item.auditComment = disApprovedComment.value
	}
}
const initListTrade = async() => {
	let res = await consultListTrade();
	if(res.code === 200){
		tradeData.value = res.data
     }
}
onMounted(() => {
	initListTrade()
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
.apply {
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
		}

	}
	.w-btn-text {
		height: 22px;
		padding: 0;
		color: rgb(var(--primary-6));
		margin-right: 10px;
	}
	:deep(.w-table){
		.w-table-pagination{
			margin-top: 30px;
		}
		.w-badge-status-dot{
			width: 8px;
			height: 8px;
			margin-right: 6px;
		}
		.w-btn-text {
			height: 22px;
			padding: 0;
			color: rgb(var(--primary-6));
		}
	}

}

</style>
<style lang="scss">
	.applyModal{
		width: 800px;
		.w-modal-body{
			border: 1px solid #E4E8EE;
			margin: 0px 120px 60px 120px;
			padding: 25px 80px;
			border-radius: 8px;
		}

		.w-btn-secondary{
			--color-secondary: rgb(var(--primary-1));
			--color-text-2: rgb(var(--primary-6));
			--color-secondary-hover:rgb(var(--primary-2));
			--color-secondary-active:rgb(var(--primary-3));
		}
		.carousel-body{
			display: flex;
			height: 100%;
			flex-direction: column;
			justify-content: space-between;
		}
		.modal-title{
			color:#646479;
			.question{
				text-overflow: ellipsis;
				white-space: nowrap;
				width: 400px;
				overflow: hidden;
			}
			h3{
				font-size: var(--font20);
				font-weight: bold;
				color: #181B49;
				margin-bottom: 10px;
			}
			>p{
				font-size: var(--font14);
				height: 30px;
				line-height: 30px;
				display: flex;
				align-items: center;
				.label{
					width: 56px;
					color: #9A99AA;
					margin-right: 12px;
					display: inline-block;
				}
				.describ{
					color: #181B49;
					overflow: hidden;
					text-overflow:ellipsis;
					white-space: nowrap;
					display: inline-block;
					width: calc(100% - 68px)
				}
			}
		}
		.check_foot{
			height: 65px;
			margin-top: 24px;
			padding-top: 28px;
			border-top: 1px dashed #E4E8EE;
			display: flex;
			justify-content: space-between;
			.w-btn{
				margin-left: 10px;
				.cool-icon{
					margin-right: 8px;
				}
			}
			.card{
				height: 36px;
				width: 385px;
				line-height: 36px;
				border-radius: 4px;
				margin-left: 10px;
				color: #2AC592;
				background: linear-gradient(270deg, rgba(84,228,196,0) 0%, rgba(42,197,146,0.2) 100%);
				.icon{
					width: 43px;
					height: 100%;
					padding-left: 12px;
					margin-right: 18px;
					display: inline-block;
					background: url(../../../assets/manage/applybg.png) no-repeat left top;
				}
			}
			.disapprovedCard{
				color: #F54B5B;
				background: linear-gradient(270deg, rgba(84,228,196,0) 0%, rgba(245,75,91,0.2) 100%);
				.icon{
					background: url(../../../assets/manage/disapplybg.png) no-repeat left top;
				}
			}
		}
		.w-carousel-arrow-left{
			width: 48px;
			height: 48px;
			background: #fff;
			left: -105px;
			box-shadow: 0px 6px 20px 0px rgba(30,64,175,0.1);
		}
		.w-carousel-arrow-right{
			width: 48px;
			height: 48px;
			background: #fff;
			right: -105px;
			box-shadow: 0px 6px 20px 0px rgba(30,64,175,0.1);
		}
		.w-carousel-arrow > div:hover {
			background-color: #fff;
		}
		.w-carousel-arrow > div > svg{
			color: #646479;
		}
		.w-carousel-indicator-wrapper{
			display: none;
		}
	}
	.exportModal{
		width: 450px;
	}
	.popconfirm{
		.w-popconfirm-icon{
			display: none !important;
		}
		.w-popconfirm-body{
			margin-bottom: 5px;
		}
		h2{
			margin-bottom: 5px;
		}
	}
</style>
