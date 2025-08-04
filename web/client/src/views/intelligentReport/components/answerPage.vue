<template>
    <div class="answer-page">
        <div class="answer-title">
			<div class="container-right-box" style="border-bottom: 1px solid rgba(0, 0, 0, 0.12);"  :style="zkFlag?rightBoxStyle:'width:150px;border:none;'">
				<div class="title-lfet">
				   <div class="icon" @click="zkInput" v-if="zkFlag">
					   <img src="../../../assets/img/zk.png" alt="">
				   </div>
				   <div class="icon" @click="sqInput" v-else>
				   		 <img src="../../../assets/img/sq.png" alt="">
				   </div>
				   <div style="margin: 0 auto;" v-if="zkFlag">
					   <span :class="status=='0'?'answer-on':''" @click="status = 0">过程</span>
					   <span :class="status=='1'?'answer-on':''" @click="status = 1">输入</span>
				   </div>
				</div>
			</div>
			<div class="container-left-box" style="display: flex;justify-content: space-between;border-bottom: none;"  :style="zkFlag?leftBoxStyle:'border:none;'">
				<div class="title-lfet">
				   <div class="icon" @click="backInput">
					   <img src="../../../assets/img/icon_return.png" alt="">
				   </div>
				   <div>生成结果</div>
				</div>
				<div class="title-right" @click="sumitAnswer">
					
					<div class="icon">
						<img src="../../../assets/img/icon_download.png" alt="">
					</div>
					<div>保存到本地</div>
				</div>
			</div>
				
        </div>
        <div class="answer-container">
            
            <div class="container-right-box" v-if="zkFlag"  :style="rightBoxStyle">
               
				<div class="suyuan" v-if="status=='0'" ref="listContainer">
					<vue3-seamless-scroll 
					    :list="interligentLeftList" 
					    direction="up" 
					    :hover="true"
						:singleHeight="0"
						:step="2"
					    class="scroll-container"
					  >
					    <div v-for="(item,index) in interligentLeftList " :key="index">
					    	<div style="position: relative;" >
					    		<div class="suyuan-height"></div>
					    		<div class="suyaun-lwjs flex suyuan-mt10" style="align-items: center;flex-wrap: wrap;">
					    			<img style="height: 28px;margin-right: 12px;" v-if="item.nodeName=='开始'" src="./workflow-ks.svg" alt="" />
					    			<img style="height: 28px;margin-right: 12px;" v-else-if="item.nodeName=='结束'" src="./workflow-js.svg" alt="" />
					    			<img style="height: 28px;margin-right: 12px;" v-else src="./tongyong.svg" alt="" />
					    			<span class="suyuan-c0">{{item.nodeName}}</span>
					    			<iconpark-icon @click="item.flag = !item.flag" class="arrow"  size="18" :name="item.flag?'arrow-up-s-line':'arrow-down-s-line'"></iconpark-icon>
					    		</div>
					    		<div v-if="item.flag">
					    			<div class="suyuan-mt10 suyuan-box" v-if="item.input">
					    				<div class="suyuan-box-header">输入</div>
					    				
					    				<div class="suyuan-text">
					    					{{item.input}}
					    				</div>
					    			</div>
					    			<div class="suyuan-mt10 suyuan-box">
					    				<div class="suyuan-box-header">输出</div>
					    				<div class="suyuan-text" v-if="item.output">
					    					{{item.output}}
					    				</div>
					    				
					    			</div>
					    		</div>
					    	</div>
					    </div>
					  </vue3-seamless-scroll>
												
				</div>
				<div v-if="status=='1'" class="textarea">
					<el-form label-position="top" label-width="auto" :model="fromList" style="max-width: 650px">
						<el-form-item v-for="(item, index) in fromList" :key="index" :label="item.desc" label-position="top">
							<template #default>
								<div v-if="isFileUploadType(item.type)">
									<el-upload ref="`upload_${index}`" action="#" :multiple="true" :limit="5"
										:show-file-list="false" :http-request="createUploadHandler(item)"
										:before-upload="beforeUpload" :on-remove="handleRemove(item)"
										accept="image/*, pdf, .docx, .txt">
										<div class="upload-trigger">
											<div class="icon-box">
												<img src="../../../assets/img/updade_icon.png" alt="">
											</div>
											<div>点击上传文件，支持.doc、.docx等格式</div>
											<!-- <div class="upload-icon">
												<iconpark-icon name="add-line" color="#3F4247" />
											</div> -->
										</div>
									</el-upload>
									<div class="fileList" v-if="item.files?.length > 0">
										<div class="file-detail" v-for="(elem, elemIndex) in item.files" :key="elem.uid">
											<div class="left">
												<div class="img">
													<iconpark-icon name="file-word-2-fill" size="28"
														color="#2862FF"></iconpark-icon>
												</div>
											</div>
											<div class="text">
												<div class="text-header">
													{{ elem.fileName }}
												</div>
												<div class="text-kb">
													{{ (elem.size / 1024).toFixed(2) }}KB
												</div>
											</div>
											<div class="delete" @click="deleteFile(item, elemIndex)">
												<iconpark-icon name="close-large-line" size="10" color="#fff"></iconpark-icon>
											</div>
										</div>
									</div>
								</div>
								<div v-else style="height: 40px;width: 100%;">
									<div style="display: flex;width: 100%;">
									
									
									<div v-if="item.desc=='部门名称'||item.desc.includes('季度信息')" style="height: 40px;width: 100%;">
										<el-select v-if="item.desc=='部门名称'" style="height: 40px;width: 100%;" v-model="item.value" placeholder="请选择" size="large">
											<el-option label="办公室(法制科)" value="办公室(法制科)" />
											<el-option label="规划与项目管理科" value="规划与项目管理科" />
											<el-option label="政务服务科" value="政务服务科" />
											<el-option label="数据要素科" value="数据要素科" />
											<el-option label="应用推进科(数字安全科)" value="应用推进科(数字安全科)" />
											<el-option label="大数据管理部" value="大数据管理部" />
											<el-option label="数字化平台部" value="数字化平台部" />
											<el-option label="应用系统管理部" value="应用系统管理部" />
											<el-option label="信息安全管理部" value="信息安全管理部" />
											<el-option label="政务大厅管理部" value="政务大厅管理部" />
											<el-option label="智慧政务推进部" value="智慧政务推进部" />
											<el-option label="政务信息管理部" value="政务信息管理部" />
										</el-select>
										<el-select v-else style="height: 40px;width: 100%;" v-model="item.value" placeholder="请选择" size="large">
											<el-option label="第一季度" value="第一季度" />
											<el-option label="第二季度" value="第二季度" />
											<el-option label="第三季度" value="第三季度" />
											<el-option label="第四季度" value="第四季度" />
											
										</el-select>
									</div>
									<el-input v-else style="height: 40px;" v-model="item.value" :placeholder="'请输入' + item.desc" />
									</div>
								</div>
							</template>
						</el-form-item>
					</el-form>
					<!-- 按钮盒子 -->
					<div class="btn-box">
						<w-button type="primary" class="sendBtn" @click="setInput"
							:disabled="hasEmptyFormValues">
							发送
						</w-button>
						<w-button type="" class="cencelBtn" @click="cenelInput">
							重置
						</w-button>
					</div>
				</div>
            </div>
			<div class="container-left-box" :style="leftBoxStyle">
			  <div class="scrollbar">
			    <searchResult :searchText="searchSureText" ref="childRef"/>
			  </div>
			</div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { Vue3SeamlessScroll } from "vue3-seamless-scroll";
import { defineAsyncComponent, ref, computed, onMounted, watch, onUnmounted, compile } from 'vue';
import { sourceAnswer,getStepByDialogId } from '/@/api/chat';
import ChatTextComponent from "/src/components/yy-message-searchNew/ChatText.vue";
import searchResult from './chatModule/components/chatResult.vue';
import { ElMessage } from 'element-plus';
import mittBus from '/@/utils/mitt';
import { useChatStore } from "/@/stores/chat";
import { useRoute } from "vue-router";
import { setScrollPosition } from '/@/utils/other';
const childRef = ref<InstanceType<typeof searchResult>>();
const zkFlag = ref(true);
const status = ref('0');
const route = useRoute();
const syList = ref([]);
const showRelateSource1 = ref(false);
const showRelateSource2 = ref(false);
const showRelateSource3 = ref(false);
const showRelateSource4 = ref(false);
const showRelateSource5 = ref(true);
const deletFileIndex = ref(null)
const sumitAnswer = () => {
    // 通过ref调用子组件方法
  childRef.value?.handleSaveImg();
}
//表单数组
const interligentLeftList = computed(() => chatStore.interligentLeftList); 
const fromList = ref([]);
// 用于根据 type 返回组件类型的函数
const isFileUploadType = (type) => ['image', 'doc', 'code', 'file'].includes(type);
const hasEmptyFormValues = computed(() => {
    // 使用 every 检查是否所有项都为空
	if(chatStore.dialogueLoading){
		return false
	}else{
		return fromList.value.every(item => {
		    if (isFileUploadType(item.type)) {
		        // 文件上传类型检查文件列表是否为空
		        return !item.files?.length;
		    }
		    // 其他类型检查值是否为空或仅含空白字符
		    return !item.value?.trim();
		});
	}
    
});
// 删除方法定义
const deleteFile = (item, index) => {
	item.files.splice(index, 1);
	item.value = item.files.map(file => file.urlPath).join(', ');
	chatStore.setFileUploadList(fromList.value.flatMap(item => item.files));
};
const beforeUpload = (file) => {
	console.log('file', file.type);
	if (
		file.type != 'application/msword' &&
		file.type != 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' &&
		file.type != 'application/pdf'
		&& file.type != 'text/plain' && file.type != 'image/png'
	) {
		ElMessage({
			message: '文件格式错误',
			center: true,
		})
		return false;
	}
}
// 创建一个上传处理函数，该函数将 item 作为闭包的一部分
const createUploadHandler = (item) => (request) => uploadHandler(item, request);
//文件上传成功
const uploadHandler = async (item, params) => {
	const formData = new FormData();
	formData.append('file', params.file);
	formData.append('rename', true);
	formData.append('filePath', 'agent_source');

	try {
		uploading.value = true;
		const res = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/wos/file/upload`, formData, {
			headers: {
				'Content-Type': 'audio/wave;multipart/form-data',
			},
		});

		// 添加新文件到当前表单项的files数组
		const newFile = {
			...res.data.data[0],
			uid: Date.now() + Math.random().toString(36).substr(2), // 生成唯一ID
			size: params.file.size
		};
		item.files.push(newFile);
		item.value = item.files.map(file => file.urlPath).join(', ');
		// console.log('fromList.value.flatMap(item => item.files)',fromList.value.flatMap(item => item.files))
		// chatStore.setFileUploadList(fileList.value);
		chatStore.setFileUploadList(fromList.value.flatMap(item => item.files));
	} catch (err) {
		uploading.value = false;
		console.error('Error:', err);
	} finally {
		uploading.value = false;
	}
};
//移除文件清空上传value
const handleRemove = (item) => (file) => {
	const index = item.files.findIndex(f => f.uid === file.uid);
	if (index > -1) {
		deleteFile(item, index);
	}
};
const textFn = async () => {
	console.log('jiekou', fromList.value)
	chatStore.interligentLeftList = []
	chatStore.chatList = []
	const data: any = {
		dataParms: fromList.value,
		conversationId: route.params.conversationId,
		knowledgeBaseId: route.params.appId,
		appId: route.params.appId,
	};
	await chatStore.setChatList(data);
}

//提交按钮
const setInput = async () => {
	if(chatStore.dialogueLoading){
		ElMessage({
			message: '正在回答中，请回答结束'
		})
		return false;
	}else{
		mittBus.emit('instructSet', false);
		textFn();
	}
	
};
//重置按钮
const cenelInput = () => {
	
	fromList.value.forEach(item => {
		item.value = '';
	});
};
// 搜索关键词
const searchText = ref('设计有哪些要素？');
const searchSureText = ref('111');
const listContainer = ref(null);
const chatStore = useChatStore();
const chatList = computed(() => chatStore.chatList);
const isSensitive = computed(() => chatStore.isSensitive);
const dialogueLoading = computed(() => chatStore.dialogueLoading);

// 计算左侧容器样式
const leftBoxStyle = computed(() => ({
  // width: syList.value.length > 0 ? '70%' : '70%',
	'flex': 1,
  // 'flex-shrink': 0,
}))
 
// 计算右侧容器样式
const rightBoxStyle = computed(() => ({
  width: '650px',
  'flex-shrink': 0,
//   background: '#f0f0f0'
}))

//返回提问页面
const backInput = () => {
    location.reload(true);
    // history.back();
    // mittBus.emit('showInput', true);
}
const zkInput = () => {
    zkFlag.value = false
}
const sqInput = () => {
    zkFlag.value = true
}
// 搜索问题
const sendQuestion = async (item: string) => {
	searchSureText.value = item;
	searchText.value = item;
	// isSearch.value = false;
	// showResultInput.value = false;

	// if (chatStore.dialogueLoading) return;
	// const data: any = {
	// 	content: item,
	// 	conversationId: route.params.conversationId,
	// 	knowledgeBaseId: route.params.appId,
	// 	appId: route.params.appId,
	// 	searchWay: !isSearch.value ? valueName.value : activeName.value,
	// };
	// chatStore.setChatList(data);
	return;
};
watch(
	() => status.value,
	(val) => {
		interligentLeftList.value = chatStore.interligentLeftList
		console.log('interligentLeftList.value = chatStore.interligentLeftList',interligentLeftList.value)
	}
);
// setInterval(() => {
//   interligentLeftList.value = chatStore.interligentLeftList
// }, 2000);
onMounted(() => {
	fromList.value = chatStore.getStartParams();
	fromList.value.forEach(item => {
		if (item.type === 'file') {
			item.files = []; // 初始化为空数组，或从接口获取数据填充  
		}
	});
    mittBus.on("dialogueId", (appInfo) => {
        console.log(1111)
      setTimeout(() => {
        appInfo.sourceShowFlag === '1' && sourceAnswerList();
		
        //   appInfo.recommendQuestionsShowFlag === '1' && tjQuestionList();
                  //mittBus.all.delete('dialogueId', () => {});
      }, 3000);
    });
   getStepByDialogIdList();
   
});

onUnmounted(() => {
    searchSureText.value = '';
});
const getStepByDialogIdList = async () => {
	let res = await getStepByDialogId({
		dialogueId: sessionStorage.getItem('dialogueId'),
	});
	if (res?.code == '000000') {
		
	} else {
		syList.value = [];
	}
};

//溯源接口逻辑
const sourceAnswerList = async () => {
	let res = await sourceAnswer({
		// dialogueId: route.params?.conversationId,
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
		dialogueId: sessionStorage.getItem('dialogueId'),
	});
	if (res?.code == '000000') {
		if (res?.data?.sourceAnswerResultList.length > 0) {
			if (res.data.sourceAnswerResultList[0].sourceType == 1) {
				syList.value = res.data.sourceAnswerResultList.filter((obj, index, self) => obj.fileLink && index === self.findIndex((t) => t.fileName === obj.fileName));
			} else {
				syList.value = res.data.sourceAnswerResultList.filter((obj, index, self) => obj.fileLink && index === self.findIndex((t) => t.fileLink === obj.fileLink));
			}
      setScrollPosition(isMobile.value ? '.message-list' : '.center-side', 'auto');
		} else {
			syList.value = [];
		}
	} else {
		syList.value = [];
	}
};
const dialogVisible = ref(false);
const fileUrl = ref('');
const downloadUrl = ref('');
const fileTitle = ref('');
const openPreview = (fileobj) => {
    dialogVisible.value = true;
    downloadUrl.value = fileobj.fileLink;
    fileUrl.value = fileobj.fileLink?.indexOf('pdf') > -1 ? fileobj.fileLink : fileobj.transPdfUrl;
    fileTitle.value = fileobj.fileName;
};

</script>

<style scoped lang="scss">
.answer-page {
	// background-color: #fff;
	 background-image: linear-gradient(to right, white, transparent);
    box-sizing: border-box;
    border-radius: 10px;
    .answer-title {
        // 
        // padding: 16px 24px;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title-lfet {
            font-size: 18px;
            color: #494E57;
            display: flex;
            align-items: center;

            .icon {
                cursor: pointer;
                margin-right: 10px;
                width: 16px;
                height: 18px;
            }
        }

        .title-right {
            cursor: pointer;
            display: flex;
            align-items: center;
            border-radius: 4px;
            border: 1px solid #2065D6;
            padding: 6px 14px;
            font-size: 14px;
            color: #2065D6;

            .icon {
                margin-right: 8px;
                width: 13px;
                height: 14px;
            }
        }
    }

    .answer-container {
        display: flex;
    }

    .container-left-box {
        transition: width 0.3s ease;
        width: 68%;
        // max-height: 780px;
        border-left: 1px solid rgba(0, 0, 0, 0.12);
        display: flex;
        justify-content: center;
        box-sizing: border-box;
        padding: 28px;
        overflow: hidden !important;
    }

    .container-right-box {
        transition: width 0.3s ease;
        box-sizing: border-box;
        padding: 28px;

        span {
            font-weight: 400;
            font-size: 14px;
            color: #828894;
            margin-right: 16px;
        }
    }
}
.scrollbar::-webkit-scrollbar {
    display: none;
}
.suyuan::-webkit-scrollbar {
    display: none;
}

.relate-source {

    li {
        margin-bottom: 10px;
    }
    .title-box {
        padding-left: 6px;
        margin-bottom: 20px;
        display: flex;
        align-items: center;

        .icon {
            margin-right: 6px;
            width: 18px;
            height: 19px;
        }

        .title {
            font-size: 16px;
            color: #383D47;
        }
    }
}

:deep(.w-link) {
    font-weight: 400;
    font-size: 14px;
    color: #383D47;
}

:deep(.w-link:hover) {
    text-decoration: none !important;
}
.answer-on{
	display: inline-block;
	font-size: 16px !important;
	font-weight: 550 !important;
	color:#000 !important;
	background-color: #fff;
	padding: 6px 25px;
	border-radius: 20px;
	box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
}
.suyuan{
	box-sizing: border-box;
	height: 85vh;
	overflow-y: auto;
	.suyuan-height{
		position: absolute;
		left: 13px;
		top: 36px;
		display: inline-block;
		width: 2px;
		height: calc(100% - 30px);
		background-color: #999;
	}
	.suyuan-box{
		background: #f7f8fa;
		border: 1px solid #999;
		word-break: break-word;
		
		border-radius: 6px;
		margin-left: 40px;
	}
	.suyuan-box-header{
		padding:0 20px;
		height: 40px;
		line-height: 40px;
		
		color: #000 !important;
		background: #f2f3f5;
	}
	.suyuan-c0{
		font-size: 16px !important;
		color: #000 !important;
		font-weight: 550 !important;
	}
	.suyuan-c6{
		color: #666;
		margin-left: 10px;
		font-size: 14px;
	}
	.suyuan-c9{
		color: #999;
		margin-left: 10px;
		font-size: 14px;
	}
	// .suyaun-p20{
	// 	margin-left: 20px;
	// }
	.suyuan-mb20{
		margin-bottom: 20px;
	}
	.suyuan-mt10{
		margin-top: 20px;
	}
	.suyuan-mr10 {
		margin-right: 10px;
	}
	.suyuan-box-hover:hover{
		// color: #2055f4;	
		.on{
			border-bottom: 1px solid #2055f4;
			span{
				color: #2055f4;
			}
			
		}
	}
	.suyuan-text{
		padding:20px;
		max-height:240px;
		overflow-y: auto;
		box-sizing: border-box;
		word-wrap: break-word; /* 在单词边界处换行 */
	}
	.suyuan-max-w200{
		// display: inline-block;
		max-width:200px;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 1;
		overflow: hidden;
		text-overflow: ellipsis;
	}
}
.textarea {
	// background-color:#fff;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	border-radius: 12px;

	// border: 1px solid #FFFFFF;
	// backdrop-filter: blur(4px);
	.fileList {
		width: 100%;
		height: 60px;
		display: flex;
		align-items: center;
		overflow: scroll;

		.file-detail {
			position: relative;
			border-radius: 8px;
			display: flex;
			height: 50px;
			background-color: #F2F3F5;
			margin-left: 9px;

			&:first-child {
				margin-left: 14px;
			}

			.left {
				width: 56px;
				height: 56px;
				display: flex;
				justify-content: center;
				align-items: center;

				.img {
					width: 40px;
					height: 40px;
					display: flex;
					justify-content: center;
					align-items: center;
				}
			}

			.text {
				flex: 1;

				.text-header {
					margin-top: 8px;
					width: 182px;
					height: 18px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #383D47;
					line-height: 18px;
					text-align: left;
					font-style: normal;
					width: 200px;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}

				.text-kb {
					margin-top: 4px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #B4BCCC;
					line-height: 18px;
					text-align: left;
					font-style: normal;
				}
			}

			.delete {
				position: absolute;
				top: 0;
				right: 0;
				width: 15px;
				height: 15px;
				border-radius: 50%;
				background: #3F4247;
				display: flex;
				justify-content: center;
				align-items: center;

				&:hover {
					cursor: pointer;
				}
			}
		}

	}
}
.btn-box {
		display: flex;
	}

	.sendBtn {
		margin-right: 20px;
		width: 176px;
		height: 40px;
		background: #1747E5;
		border-radius: 8px;
		display: flex;
		align-items: center;
		justify-content: center;
		// position: absolute;
		// right: 0px;
		// top: 0px;
		// background: none;
		border: none;
		font-weight: 500;
		font-size: 14px;
		color: #ffffff;
		line-height: 40px;
		text-align: center;

		&.yy {
			right: 60px;
		}
	}

	.cencelBtn {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 72px;
		height: 40px;
		border-radius: 8px;
		border: 1px solid #D5D8DE;
		font-size: 14px;
		color: #494E57;
	}
</style>