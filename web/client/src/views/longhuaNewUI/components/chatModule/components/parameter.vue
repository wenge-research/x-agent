<template>
	<div class="parameter">
		<div class="left_param">
			<div class="flex" style="flex: 1">
				<div style="display: flex; flex: 1" v-if="!storesUserInfo.offline || route.params.appId != 21">
					<div class="drawer drawerParam" ref="drawerRef" :style="{ top: top }" :class="[visibleClass, drawerClass]">
						<w-form :model="form" size="medium">
							<template v-for="(item, index) in options">
								<w-form-item v-if="index >= paramsNum" :key="index + item.key" :label="item.name">
									<ParamSetting @refresh="setDrawerHeight" :options="item" @changeParam="changeParam"></ParamSetting>
								</w-form-item>
							</template>
						</w-form>
					</div>
					<div style="display: flex; align-items: center; flex: 1">
						<div class="param_box">
							<template v-for="(item, index) in options" :key="index">
								<transition-group
									appear
									name="animate__animated animate__fadeIn"
									enter-active-class="animate__fadeIn"
									leave-active-class="animate__fadeIn"
								>
									<div v-if="index < paramsNum" style="float: left; width: 33%">
										<w-form :model="form" size="medium" :auto-label-width="true">
											<w-form-item :label="item.name + '：'">
												<ParamSetting :options="item" @changeParam="changeParam"></ParamSetting>
											</w-form-item>
										</w-form>
									</div>
								</transition-group>
							</template>
							<div v-if="options.length > paramsNum" class="setting" @click="handleClick">
								<CoolListSettingsFillWe :color="paramsDrawerVisible ? '#9A99AA' : 'var(--w-color-primary)'" size="20" />
							</div>
							<div class="uploadBtn" v-if="isShowFileUpload">
								<div class="drawer" style="height: 170px; top: -160px" :class="!uploadDrawerVisible ? 'visible' : ''">
									<Upload></Upload>
								</div>
								<w-button @click="fileUploadFun" type="primary" shape="circle" class="fileBtn">
									<CoolWeixuanzhong size="22" color="var(--w-color-primary)" />
									<span class="dot"></span>
								</w-button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="right_param">
			<div class="instrct" v-if="promptType == 'SIMPLE'" @click="getComplete"><img :src="promptImg" alt="" /> Prompt查看</div>
			<w-button type="outline" @click="newBuilt" :disabled="route.params.conversationId == '' || dialogueInputLoading">
				<template #icon>
					<CoolAddLineWe size="14" />
				</template>
				<template #default>新增对话</template>
			</w-button>
		</div>
		<w-modal v-model:visible="completeVisible" hide-cancel ok-text="复制" modal-class="completeModal" @ok="copyIns">
			<template #title> Prompt </template>
			<div>
				<div class="detail" v-html="promptText"></div>
			</div>
		</w-modal>
	</div>
</template>

<script lang="ts" setup>
import { ref, defineAsyncComponent, watch, computed, onMounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useChatStore } from '/@/stores/chat';
import { getDialogueParam } from '/@/api/chat';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import other from '/@/utils/other';
import { useRobotStore } from '/@/stores/robot';
import { copyText } from '/@/utils/format';
import { Message } from 'winbox-ui-next';
import promptImg from '/@/assets/chat/prompt.svg';
import { useUserInfo } from '/@/stores/userInfo';
const completeVisible = ref(false);
const robotStore = useRobotStore();
const Upload = defineAsyncComponent(() => import('../../upload.vue'));
const ParamSetting = defineAsyncComponent(() => import('/@/components/paramSetting/index.vue'));
const chatStore = useChatStore();
const route = useRoute();
const router = useRouter();
const drawerRef = ref(null);
const isShowFileUpload = ref(false);
const form = ref({});
const { isMobile } = useBasicLayout();
const paramsDrawerVisible = computed(() => chatStore.paramsDrawerVisible);
const uploadDrawerVisible = computed(() => chatStore.uploadDrawerVisible);
const paramsNum = computed(() => chatStore.paramsNum);
const storesUserInfo = useUserInfo();
const top = ref('');
const drawerHeight = ref(0);
const promptText = ref('');
const promptValue = ref('');
const promptType = ref(''); //SIMPLE

const dialogueInputLoading = computed(() => {
	return chatStore.dialogueLoading;
});
const options = computed(() => {
	let arr = chatStore.dialogueParamsList.filter((item) => {
		return item.key !== 'file_content' && item.key !== 'file_title' && !item.isSystem;
	});
	return arr;
});

const drawerClass = computed(() => {
	return drawerRef.value?.clientHeight > 200 ? 'isMore200' : '';
});
const visibleClass = computed(() => {
	return paramsDrawerVisible.value ? '' : 'visible';
});

const handleClick = (e) => {
	e.stopPropagation();
	chatStore.setUploadDrawerVisible(false);
	chatStore.setParamsDrawerVisible(!paramsDrawerVisible.value);
};
const fileUploadFun = () => {
	chatStore.setParamsDrawerVisible(false);
	chatStore.setUploadDrawerVisible(!uploadDrawerVisible.value);
};
const changeParam = (val) => {
	options.value.map((item) => {
		if (item.key == val.key) {
			return (item.defaultValue = val.defaultValue);
		}
	});
};
const setDrawerHeight = () => {
	nextTick(() => {
		drawerClass.value = '';
		drawerClass.value = drawerRef.value?.clientHeight > 200 ? 'isMore200' : '';
		drawerHeight.value = drawerRef.value?.clientHeight;
		top.value = `${-drawerRef.value?.clientHeight + 10}px`;
	});
};

const getDialogueParams = async (appId: string) => {
	chatStore.dialogueParams = {};
	chatStore.dialogueParamsList = [];
	isShowFileUpload.value = false;
	const res = await getDialogueParam(appId);
	res.data = res.data || { streamSupport: true };
	chatStore.dialogueParams = res.data;
	promptValue.value = chatStore.dialogueParams.prompt;
	promptType.value = chatStore.dialogueParams.type;
	const { params, datasetFilesEnabled } = res.data;
	isShowFileUpload.value = datasetFilesEnabled;
	if (params?.length) {
		chatStore.dialogueParamsList = params;
		chatStore.dialogueParamsListOld = other.deepClone(params);
		setDrawerHeight();
	} else {
		chatStore.dialogueParamsList = [];
	}
};
const newBuilt = () => {
	chatStore.setParamsDrawerVisible(false);
	chatStore.setUploadDrawerVisible(false);
	chatStore.isSensitive = false;
	robotStore.breakChat();
	chatStore.dialogueLoading = false;
	let name = route.path.indexOf('chat') != -1 ? 'chat' :route.path.indexOf('knowledgeDetails') != -1 ? 'knowledgeDetails':'knowledge';

	router.push({ name: name, params: { appId: route.params.appId, conversationId: '' } });
};

const getComplete = () => {
	let str = promptValue.value;

	chatStore.dialogueParams.params &&
		chatStore.dialogueParams.params.forEach((item) => {
			let key = `{${item.key}}`;
			str = str.replace(new RegExp(key, 'g'), item.defaultValue);
		});

	str = str.replace(/\\n/g, '</br>');
	str = str.replace(/\r/g, ' ');
	str = str.replace(/{textContent}/g, '<span>' + chatStore.chatInputTextValue + '</span>');

	promptText.value = str;
	completeVisible.value = true;
};
const copyIns = () => {
	let str = promptValue.value;
	chatStore.dialogueParams.params &&
		chatStore.dialogueParams.params.forEach((item) => {
			let key = `{${item.key}}`;
			str = str.replace(new RegExp(key, 'g'), item.defaultValue);
		});
	str = str.replace(/\\n/g, '\n');
	let text = str.replace(/{textContent}/g, chatStore.chatInputTextValue);
	copyText({ text });
	Message.success('复制成功');
	completeVisible.value = false;
};

onMounted(async () => {
	// if(route.params.conversationId && route.params.conversationId != ''){
	//   await chatStore.initFileList({ conversationId: route.params.conversationId });
	// }
});
// 监听路由的变化
watch(
	() => route.params.conversationId,
	(newVal) => {
		if (route.params.appId) {
			if (newVal != chatStore.conversationsIdforUploadFlag) {
				//  if(route.params.conversationId && route.params.conversationId != ''){
				//   chatStore.initFileList({conversationId: route.params.conversationId});
				//  }else{
				//   chatStore.fileList =[]
				//  }
			}
			chatStore.conversationsIdforUploadFlag = 'new';
		}
	}
);

watch(
	() => route.params.appId,
	(newVal: any) => {
		if (route.params.appId && route.params.appId != '' && route.params.appId != 0) {
			getDialogueParams(newVal);
		}
	},
	{ immediate: true }
);
watch(
	() => isMobile.value,
	(newVal: any) => {
		chatStore.paramsNum = newVal ? 0 : 3;
		chatStore.paramsDrawerVisible = false;
	},
	{ immediate: true }
);
</script>

<style scoped lang="scss">
.parameter {
	--color-bg-1: transparent;
	--color-bg-5: transparent;
	--color-neutral-3: #c8cbd4;
	padding: 0 6px 0 22px;
	height: 58px;
	//background: #F5F8FF;
	border-radius: 16px 16px 0px 0px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	position: relative;
	margin-bottom: -10px;
	background: linear-gradient(180deg, rgba(244, 246, 249, 0) 0%, #f4f6f9 41%);
	backdrop-filter: blur(1px);
	.flex {
		display: flex;
		align-items: center;

		> div {
			margin-right: 10px;
		}
		.uploadBtn {
			width: 50px;
		}
		:deep(.w-select) {
			margin-right: 10px;
			border: none;
		}
	}
	.drawer {
		width: 100%;
		position: absolute;
		// top: -150px;
		top: -190px;
		// height: 200px;
		left: 0;
		//background: #f5f8ff;
		z-index: -1;
		padding: 15px 32px;
		border-radius: 16px 16px 0px 0px;
		transition: top 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);
		overflow: auto;

		.w-form {
			display: initial;
			.w-form-item {
				display: inline-block;
				width: 30%;
				margin-right: 5%;
				margin-bottom: 2px;
			}
			.w-form-item:nth-child(3n + 0) {
				margin-right: 0%;
			}
			:deep(.w-form-item-label-col) {
				margin-bottom: 0;
				.w-form-item-label {
					font-size: var(--font12);
				}
			}
		}

		.w-form-item-content {
			width: 100%;
			> div {
				width: 100%;
			}
		}
	}
	.drawerParam {
		padding-right: 228px;
	}
	.param_box {
		display: flex;
		align-items: center;
		flex: 1;
		.w-form {
			flex-direction: row;
		}
		.w-form-item {
			margin-bottom: 0;
			//margin-right: 36px;
		}
		:deep(.w-form-item-label-col) {
			margin-bottom: 0;
			padding-right: 0;
			.w-form-item-label {
				font-size: var(--font12);
			}
		}
		:deep(.w-form-item-wrapper-col) {
			min-height: initial;
		}
		.w-form-item-content {
			width: 100%;
			> div {
				width: 100%;
			}
		}
	}
	.isMore200 {
		max-height: 200px;
		top: -190px !important;
	}
	.visible {
		top: 15px !important;
		transition: top 0.2s cubic-bezier(0.34, 0.69, 0.1, 1);
	}

	.setting {
		cursor: pointer;
		width: 32px;
		height: 32px;
		display: flex;
		align-items: center;
	}
	.left_param {
		display: flex;
		flex: 1;
		.fileBtn {
			width: 32px;
			height: 32px;
			border-radius: 4px;
			border: none;
			background: #fff;
			position: relative;
			box-shadow: 0px 6px 16px 0px rgba(30, 64, 175, 0.1);
			.dot {
				position: absolute;
				top: -2px;
				right: -2px;
				width: 5px;
				height: 5px;
				border-radius: 50%;
				background: #f54b5b;
			}
		}
	}

	.right_param {
		display: flex;
		justify-content: center;
		align-items: center;
		.instrct {
			color: #355eff;
			font-size: var(--font16);
			display: flex;
			cursor: pointer;
			img {
				width: 18px;
				height: 18px;
				margin-right: 5px;
				padding-top: 2px;
			}
		}
		.w-btn {
			display: flex;
			align-items: center;
			font-size: var(--font14);
			border: none;
			color: var(--w-color-primary);
			width: 104px;
			height: 32px;
			border-radius: 16px;
			text-align: center;
			margin-bottom: 16px;
		}
		.w-btn-disabled {
			color: var(--color-primary-light-3);
		}
		:deep(.w-btn-icon) {
			margin-right: 5px;
		}
	}
}
</style>
<style lang="scss">
.completeModal {
	background: linear-gradient(130deg, #dfeafc 0%, #ffffff 100%), linear-gradient(180deg, #ede7ff 0%, rgba(239, 243, 251, 0) 100%);
	border-radius: 12px;
	width: 700px;
	.w-icon {
		width: 1.5em;
		height: 1.5em;
	}
	.w-modal-footer {
		text-align: center;
	}
	.w-btn-primary {
		width: 200px;
		height: 40px;
		line-height: 40px;
		background: linear-gradient(90deg, #7e9dff 0%, #355eff 100%);
		border-radius: 8px;
		font-size: var(--font18);
		margin-top: 10px;
		border: none;
	}
	.w-textarea-wrapper {
		background: transparent;
		border-radius: 8px;
		border-color: #d0d5dc;
		color: #181b49;
		line-height: 32px;
		padding-bottom: 40px;
	}
	.w-textarea {
		padding: 20px;
		font-size: var(--font16);
	}
	.w-textarea {
		cursor: inherit !important;
	}
	.detail {
		font-size: var(--font16);
		line-height: 32px;
		color: #181b49;
		height: 287px;
		overflow: auto;
		> span {
			padding: 0 2px;
			color: #355eff;
		}
	}
}
</style>
