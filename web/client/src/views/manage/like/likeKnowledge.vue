<template>
	<div class="like">
		<h2>知识库问答赞踩</h2>
		<div class="search_box">
			<div>
				<w-select  v-model="type" placeholder="赞踩类型" @change="lickChange" clearable>
					<w-option v-for="(item, index) in likeData" :label="item.name" :value="item.id" :key="index">{{ item.name }}</w-option>
				</w-select>
				<w-select  v-model="applicationValue" placeholder="知识库名称" @change="applicationChange" clearable>
					<w-option v-for="(item, index) in applicationData" :label="item.name" :value="item.id" :key="index">{{ item.name }}</w-option>
				</w-select>
				<w-range-picker
					v-model="rangeValue"
					@change="applicationChange"
				/>
				<w-input-search v-model="keyWords" :style="{width:'240px'}" placeholder="请输入关键词/手机号" clearable  @change="changeText" />
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
				<w-table-column  :width="130" title="赞踩类型" data-index="type">
					<template #cell="{ record }" >
						<div class="feedbackCss">
							<i class="likeIcon" :class="[record.type == 1 ? 'like' : 'cai']">
								<CoolDianzanxuanzhong v-if="record.type == 1" size="18" color="#fff"/>
								<CoolDiancaixuanzhong v-else size="18" color="#fff"/>
							</i>
							<div style="flex: 1;">
								<h3>{{record.type == 1 ? '赞' : '踩'}}</h3>
							</div>
						</div>
					</template>
				</w-table-column>
				<w-table-column  title="权限" data-index="category"></w-table-column>
				<w-table-column  title="知识库名称" data-index="appName"></w-table-column>
				<w-table-column  title="赞踩反馈">
					<template #cell="{ record }" >
						<div class="feedbackCss">
							<div>
								<template v-if="record.type == 1 && record.upvoteLabel">
									<p class="dotParent" v-for="(item,index) in record.upvoteLabel" :key="index"><span class="dot"></span>{{item}}</p>
								</template>
								<template v-if="record.type == 0 && record.downvoteLabel">
									<p class="dotParent" v-for="(item,index) in record.downvoteLabel" :key="index"><span class="dot"></span>{{item}}</p>
								</template>
								<w-tooltip :content="record.feedback">
									<p class="feedback" v-if="record.feedback">{{record.feedback}}</p>
								</w-tooltip>
							</div>

						</div>
					</template>
				</w-table-column>
				<w-table-column title="问题及答案" data-index="name" :width="180">
					<template #cell="{ record }" >
						<w-tooltip :content="record.question"  content-class="tooltip-h3">
							<h3>{{record.question}}</h3>
						</w-tooltip>
						<w-tooltip :content="record.answer">
							<p class="table-p">{{record.answer}}</p>
						</w-tooltip>
						
					</template>
				</w-table-column>
				<w-table-column  title="应用及参数" data-index="app" :width="180">
					<template #cell="{ record }">
						<h3>{{record.app}}</h3>
						<w-tooltip :content="record.dialogueParam">
							<!-- <span v-for="(value,key,index) in JSON.parse(record.dialogueParam)"  :key="index">{{value}}</span> -->
							<p class="table-p">{{record.dialogueParam}}</p>
						</w-tooltip>
					</template>
				</w-table-column>
				<w-table-column  title="用户手机" data-index="feedbackUser"></w-table-column>
				<w-table-column  title="反馈用户" data-index="feedbackUserName">
					<template #cell="{ record }">
						<p>{{record.feedbackUserName}}</p>
						<p class="table-p">{{record.feedbackTime}}</p>
					</template>
				</w-table-column>
				<w-table-column  title="状态" data-index="auditStatus" >
					<template #cell="{ record }">
						<p v-if="record.auditStatus == 0"><w-badge type="processing" />未审核</p>
						<p v-else-if="record.auditStatus == 1"><w-badge type="success" />赞同</p>
						<p v-else-if="record.auditStatus == 2"><w-badge type="danger" />不赞同</p>
						<w-tooltip :content="record.auditComment">
							<p class="table-p">{{record.auditComment}}</p>
						</w-tooltip>
					</template>
				</w-table-column>
				<w-table-column :width="100" title="操作"  fixed="right">
					<template #cell="{ record }">
						<w-button v-if="record.auditStatus == 0" type="text" size="small" @click="handleClick(record)">审核</w-button>
					</template>
				</w-table-column>
				</template>
			</w-table>
		</div>
		<w-modal modal-class="exportModal" v-model:visible="visibleExport" @ok="handleSubmit" @cancel="resetFields">
			<template #title>
			导出
			</template>
			<div>
				<w-form ref="formRef"
				:model="form"
				:style="{ width: '100%' }"
				>

					<w-form-item field="rangeValue" label="反馈时间">
						<w-range-picker v-model="form.rangeValue" style="width: 100%;" />
					</w-form-item>
					<w-form-item field="optionsDianCai" label="赞踩类型">
						<w-checkbox-group v-model="form.optionsDianCai" >
							<w-checkbox value="1">赞</w-checkbox>
							<w-checkbox value="0">踩</w-checkbox>
						</w-checkbox-group>
					</w-form-item>
					<w-form-item field="optionsCheck" label="审核结果">
						<w-checkbox-group v-model="form.optionsCheck" >
							<w-checkbox value="1">赞同</w-checkbox>
							<w-checkbox value="2">不赞同</w-checkbox>
						</w-checkbox-group>
					</w-form-item>
				</w-form>
			</div>
		</w-modal>
		<w-modal modal-class="likeModal" :footer="false" v-model:visible="visible" @cancel="handleCancel">
			<template #title>
			审核
			</template>
			<div>
			<w-carousel
				:style="{
				width: '100%',
				height: '580px',
				}"
				animation-name="fade"
				v-model:current="currentCarousel"
				@change="changeCarousel"
			>
			<w-carousel-item v-for="(item,index) in tableData" :key="index">
				<div class="carousel-body">
					<div class="modal-title">
						<h3 class="question">{{item.question}}</h3>
						<p>
							<div class="ds">应用<span class="params">{{item.app}}</span></div>
							<div class="ds">应用参数<span class="params">{{item.dialogueParam}}</span></div>
						</p>
						<p class="answer">{{item.answer}}</p>
					</div>
					<div class="check_foot">
						<div class="footer">
							<div class="feedbackCss">
								<i class="likeIcon" :class="[item.type == 1 ? 'like' : 'cai']">
									<CoolDianzanxuanzhong v-if="item.type == 1" size="18" color="#fff"/>
									<CoolDiancaixuanzhong v-else size="18" color="#fff"/>
								</i>
							</div>
							<div class="describ">
								<p><span class="mr">{{item.feedbackUser}}</span><span  class="time">{{item.feedbackTime}}</span></p>
								<p class="feedback">{{item.feedback}}</p>
							</div>
						</div>
						<div v-if="!item.auditStatus == 0">
							{{item.auditStatus == 1 ? '赞同': '不赞同'}}
							<!-- <div>{{item.auditComment}}</div> -->
						</div>
						<div v-if="item.auditStatus == 0">
							<w-button type="secondary" @click="agree(item)"><CoolThumbUpLineWe  size="18"/>赞同</w-button>
							<w-popconfirm content-class="disagreePopconfirm" @popup-visible-change="popupChange" okText="提交" @ok="disagree(item)">
								<template #content>
									<h2>不赞同的原因</h2>
									<w-textarea v-model="auditComment" placeholder="不赞同原因，非必填" :auto-size="{ minRows: 2,maxRows: 2 }"/>
								</template>
								<template #icon></template>
								<w-button type="secondary" status="danger"><CoolThumbDownLineWe  size="18" />不赞同</w-button>
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
import { onMounted, ref, reactive,computed} from 'vue';
import { feedbackPage,feedbackExport,listApp,feedbackAudit } from '/@/api/manage'
const loading = ref(false)
const pagination = ref({total: 0,pageSize: 10,'show-total': true,'show-page-size': true, 'show-jumper': true})
const applicationValue = ref('')
const type = ref('')
const auditComment = ref('')
const formRef = ref(null)
const visibleExport = ref(false)
const currentCarousel = ref(1)
const rangeValue = ref(['', ''])
const keyWords = ref('')
const size = ref(10)
const current = ref(1)
const applicationData = ref([])
const tableData = ref([])
const likeData = ref([{
	id: '1',
	name: '赞'
},{
	id: '0',
	name: '踩'
}])
const feedbackContent = (record) => {
	let str = ''
	if(record.type == 1 && record.upvoteLabel){
		record.upvoteLabel.forEach((item,index) => {
			if(index == record.upvoteLabel.length - 1){
				str += item
			}else{
				str += item + ','
			}
		})
	}
	if(record.type == 0 && record.downvoteLabel){
		record.downvoteLabel.forEach((item,index) => {
		if(index == record.downvoteLabel.length - 1){
				str += item
			}else{
				str += item + ','
			}
		})
	}
	if(record.feedback){
		if(!record.downvoteLabel && !record.upvoteLabel){
			str += record.feedback
		}else{
			str += `.${record.feedback}`
			
			
		}
		
	}
	return str
};
const form = ref({
        rangeValue:['',''],
		optionsDianCai: ['1'],
		optionsCheck: ['1'],
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
const lickChange = () => {
	current.value = 1
	init()
}
const visible = ref(false)
const handleClick = (item) => {
	let index = tableData.value.findIndex((value)=>value.id == item.id);
	currentCarousel.value = index + 1
	visible.value = true
}
const handleSubmit = () => {
	let startTime = ''
	let endTime = ''
	if(form.value.rangeValue){
		startTime = form.value.rangeValue[0]  ==  '' ? '' : form.value.rangeValue[0] + ' 00:00:00'
		endTime = form.value.rangeValue[1]  ==  '' ? '' : form.value.rangeValue[1] + ' 23:59:59'
	}
	let data = {
		"auditStatus": (form.value.optionsCheck.length > 1 || form.value.optionsCheck.length == 0) ? 3 : form.value.optionsCheck[0],
		"endTime": endTime,
		"startTime": startTime,
		"dialogueType": 'KNOWLEDGE_BASE',
		"type": (form.value.optionsDianCai.length > 1 || form.value.optionsDianCai.length == 0) ? 2 : form.value.optionsDianCai[0]
	}
	exportXlsx(data)
}
const resetFields = () => {
	form.value.rangeValue = ['','']
	form.value.optionsDianCai = ['1']
	form.value.optionsCheck = ['1']
}

const exportXlsxClick = () => {
	visibleExport.value = true
}
const exportXlsx = async(data) => {
	let res = await feedbackExport(data);
	downLoadXls(res)

}
const downLoadXls = (res) => {
	// const fileNames = res.headers['content-disposition']
    // if(fileNames) {
      //解码
      const fileName = '赞踩管理.xlsx'
      // 处理返回的文件流
    //   const content = res.data
      const blob = new Blob([res],{
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
        // type: "application/octet-stream; charset=utf-8"

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
        //IE10 + 下载
        navigator.msSaveBlob(blob,fileName)
      }
    // }
}
const getAppList = async() => {
	let res = await listApp();
	applicationData.value = res.data

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
		size: size.value,
		keyword: keyWords.value,
		startTime: startTime,
		endTime: endTime,
		type: type.value,
		dialogueType: 'KNOWLEDGE_BASE'
	}
	let res = await feedbackPage(data);
     if(res.code === 200){
		loading.value = false
		tableData.value = res.data.records
		pagination.value.total = res.data.total
		document.querySelector('.w-table-body').scrollTop = 0
     }else{
		loading.value = false
	}
}
const popupChange = (val) => {
	if(!val){
		auditComment.value = ''
	}
}
const changeCarousel = (i) => {
	currentCarousel.value = i
	// setTimeout(()=> {
	// 	document.querySelector('.w-carousel-item-slide-out').style.display= "none !important"
	// },500)
	
}
const handleCancel = () => {
	init()
}

const agree = async(item) => {
	let data = {
		auditStatus: 1,
		id: item.id,
	}
	let res = await feedbackAudit(data);
	if(res.code == 200){
		item.auditStatus = 1
		item.checkStatus = true
	}
}
const disagree = async(item) => {
	item.auditComment = auditComment.value
	let data = {
		auditStatus: 2,
		id: item.id,
		auditComment: auditComment.value
	}
	let res = await feedbackAudit(data);
	if(res.code == 200){
		item.auditStatus = 2
		item.checkStatus = true
	}
}
onMounted(() => {
	getAppList()
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
.like {
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
		:deep(.w-picker){
			width: 220px;
			margin-right: 12px;
			height: 36px;
			border-radius: 3px;
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
			margin-top: 8px;
		}

		.feedbackCss{
			display: flex;
			align-items: center;
			.likeIcon{
				width: 36px;
				height: 36px;
				display: flex;
				align-items: center;
				justify-content: center;
				border-radius: 50%;
				margin-right: 16px;
			}
			.like{
				background: linear-gradient(180deg, #355EFF 0%, #7FA9FF 100%);
			}
			.cai{
				background: linear-gradient(180deg, #F54B5B 0%, #FFA7AF 100%);
			}
			.dotParent{
				display: flex;
				align-items: center;
				color: #646479;
			}
			.dot{
				width: 2px;
				height: 2px;
				margin-right: 6px;
				display: inline-block;
				background: #646479;
			}
			.feedback{
				// margin-top: 4px;
				color: #646479;
			}
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
.w-tooltip{
	overflow-y: hidden !important;
    }
.tooltip-h3{
	max-height:180px;	
	overflow-y: auto;
	}
	.likeModal{
		width: 1200px;
		.w-modal-body{
			border: 1px solid #E4E8EE;
			margin: 0px 44px 36px 44px;
			padding: 25px 36px;
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
				height: 172px;
				overflow: auto;
			}
			.ds{
				display: flex;
				color:#9A99AA;
			}
			h3{
				font-size: var(--font14);
				font-weight: bold;
				color: #181B49;
				-webkit-line-clamp: 2;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				overflow: hidden;
				text-overflow: ellipsis;
				margin-bottom: 10px;
			}
			p{
				font-size: var(--font14);
				color: #646479;
				display: flex;
			}
			span{
				margin-right: 30px;
			}
			.params{
				margin-left: 5px;
				overflow: hidden;
				color:#646479;
			}
			.answer{
				height: 254px;
				margin-top: 24px;
				overflow: auto;
			}
			
			
		}
		.check_foot{
			height: 80px;
			margin-top: 24px;
			padding-top: 28px;
			border-top: 1px dashed #E4E8EE;
			display: flex;
			justify-content: space-between;
			.footer{
				display: flex;
				.mr{
					margin-right: 10px;
					margin-bottom: 12px;
				}
				.time{
					color: #9A99AA;
					font-size: var(--font14);
				}
				.feedback{
					color: #646479;
					-webkit-line-clamp: 1;
					display: -webkit-box;
					-webkit-box-orient: vertical;
					overflow: hidden;
					text-overflow: ellipsis;
				}
				
			}
			.w-btn{
				margin-left: 10px;
				.cool-icon{
					margin-right: 8px;
				}
			}
		}

		.feedbackCss{
			display: flex;
			.likeIcon{
				width: 36px;
				height: 36px;
				display: flex;
				align-items: center;
				justify-content: center;
				border-radius: 50%;
				margin-right: 16px;
			}
			.like{
				background: linear-gradient(180deg, #355EFF 0%, #7FA9FF 100%);
			}
			.cai{
				background: linear-gradient(180deg, #F54B5B 0%, #FFA7AF 100%);
			}
		}
		.w-carousel-arrow-left{
			width: 48px;
			height: 48px;
			background: #fff;
			left: -65px;
			box-shadow: 0px 6px 20px 0px rgba(30,64,175,0.1);
		}
		.w-carousel-arrow-right{
			width: 48px;
			height: 48px;
			background: #fff;
			right: -65px;
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
	.disagreePopconfirm{
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