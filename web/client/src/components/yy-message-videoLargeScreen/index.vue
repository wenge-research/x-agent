<template>
	<div ref="messageRef" class="items-start w-full yy-message-videoLargeScreen"
		:class="[{ 'flex-row-reverse': inversion, 'mb-4': inversion, 'mt-4': inversion, 'mb-3': !inversion,'share-row-reverse':!inversion&&chatStore.$state.inShare}, className]">
		<div :class="['flex-shrink-0', inversion ? 'ml8' : 'mr8']">
			<AvatarComponent v-if="!isMobile" :isLoading="loading" :image="inversion" />
		</div>
		<div class="items-start max-w-full fontSize16" style="max-width: calc(100vw - 64px - 112px);overflow: hidden;">
			<p v-if="!inversion && props?.param" class="text-xs text-[#b4bbc4] mb-2" :class="[!inversion ? 'text-right' : 'text-left']">
			<div class="flex flex-wrap items-center justify-start">
				<div class="tagList" v-for="(item, key) in paramData" :key="key">
					<span>{{ key }}</span>：<span v-if="typeof (item) === 'object'">
						<span v-for="(val, index) in item" :key="index + val">
							{{ val }}
						</span>
					</span>
					<span v-else>{{ item == '无' ? '雅意' : item }}</span>
					<span class="tip">
						|
					</span>
				</div>
			</div>
			</p>
			<div class="relative" :class="[inversion ? 'flex-row-reverse' : 'flex-row']" ref="textRef">
				<w-tooltip v-if="inversion" placement="tr" :arrow-style="{ right: 0, left: 'inherit' }" :update-at-scroll="true"
					background-color="#fff" :content-style="{ padding: '8px 16px', borderRadius: '4px' }">
					<template #content v-if="inversion">
						<CoolBianji_9oe2ecpc title="编辑" @click="edit()" size="16" color="#9A99AA"
							style="margin-right: 10px;cursor:pointer;" />
						<CoolFuzhi_9oe2ecmc title="复制" @click="copy(question, imgUrl)" size="14" color="#9A99AA"
							style="cursor:pointer;" />
					</template>
					<ChatTextComponent :inversion="inversion" :loading="loading" :citations="citations"
						:dialogueFileIds="dialogueFileIds" :dialogueFolderIds="dialogueFolderIds" :paragraph="paragraph || ''"
						:skillId="skillId" :text="inversion ? question : answer" :as-raw-text="false" :asRawText="asRawText"
						:imgUrl="imgUrl" :params="param" v-model:isHighlightCode="isHighlightCode"></ChatTextComponent>
				</w-tooltip>
				<ChatTextComponent v-else :inversion="inversion" :loading="loading" :citations="citations"
					:dialogueFileIds="dialogueFileIds" :dialogueFolderIds="dialogueFolderIds" :paragraph="paragraph || ''"
					:skillId="skillId" :text="inversion ? question : answer" :as-raw-text="false" :asRawText="asRawText"
					:params="param" :imgUrl="imgUrl" v-model:isHighlightCode="isHighlightCode" :matterGuide="matterGuide" :finishReason="finishReason" @handleFillForm="handleFillForm">
					<!-- <template #header>
						<div class="relate-source" v-if="syList.length>0">
								<span class="title" @click="showRelateSource = !showRelateSource">找到 {{syList.length}} 条来源<iconpark-icon class="arrow" :class="{'down' : showRelateSource}" size="20" name="arrow-down-s-line"></iconpark-icon></span>
								<ul v-show="showRelateSource">
									<li  v-for="(item, index) in syList" :key="index">
                    <div v-if="!isMobile">
                      <w-link v-if="item.fileLink && !chatStore.isFileLink(item?.fileLink)" :href="item.fileLink" target="_blank" icon >{{item.fileName !=null?item.fileName :item.fileLink }}</w-link>
                    	
										</div>
                    <div v-else>
                      <w-link v-if="item.fileLink && item.sourceType === 4"  @click.stop="openPopup(item)" icon >{{item.fileName !=null?item.fileName :item.fileLink }}</w-link>
                    </div>
									</li>
								</ul>
							</div>
					</template> -->
					<!-- <template #footer>
						<div style="padding: 0 0 1rem 1rem;margin-top: -20px;display: flex;align-items: center"
							v-if="props.suggestOrg && showSuggestOrg">
							<span style="color: #000000;font-size:16px;">您也可以通过</span>
							<w-button @click="sendQuestion(props.suggestOrg)" size="small"
								style="background-color: #409EFF;color: #fff;border-radius: 5px">
								{{ props.suggestOrg }}
							</w-button>
							<span style="color: #000000;font-size:16px;">查询或咨询</span>
						</div>
						<div v-if="!inversion && !loading"
							style="background: linear-gradient( 180deg, rgba(116,187,250,0) 0%, rgba(64,133,244,0.1) 100%);padding-top: 0;">
							<div class="question" v-if="questionList.length && chatMode">
								<div class="btns">
									<span class="title">{{ moreQuesion }}</span>
								</div>
								<div :class="isMobile ? 'mobileList' : 'list'">
									<w-skeleton v-if="!questionList.length && requestLoading" :animation="true">
										<w-space direction="vertical" :style="{ width: '100%' }" size="default">
											<w-skeleton-line :rows="3" />
										</w-space>
									</w-skeleton>
									<template v-for="(item, index) in questionList">
										<div class="item" v-if="index < 2" :key="index" @click="sendQuestion(item)">
											<div class="box" :style="{ 'justify-content': isMobile ? 'flex-start' : 'space-between' }">
												<img v-if="isMobile" style="height: 18px;margin-right: 12px;" src="/src/assets/zc/send.svg"
													alt="" />
												<span>{{ item }}</span>
												<img class="pc-img" v-if="!isMobile" style="height: 20px" src="/src/assets/zc/send1.png"
													alt="" />
											</div>
										</div>
									</template>
								</div>
							</div>
							
						</div>
					</template> -->
					<!-- <template #question>

				</template> -->
				</ChatTextComponent>
				<div v-if="false" class="btns adaption">
					<!-- 播放 -->
					<w-tooltip content="播放" placement="top" mini>
						<w-button type="text" shape="circle" v-if="!inversion" @click="handleBofang">
							<!-- <CoolVolumeUpLineWe size="18" /> -->
							<iconpark-icon name="volume-up-line" color="#797F8A" size="20"></iconpark-icon>
						</w-button>
					</w-tooltip>
					<!-- 复制 -->
					<w-tooltip :content="!iscopy ? '复制' : '已复制'" placement="top" mini>
							<w-button type="text" shape="circle" @click="handleSelect('copyText')">
								<!-- <CoolFuzhi_9oe2ecmc v-if="!iscopy" size="14" />
								<CoolCheckLineWe v-else size="14" /> -->
								<iconpark-icon v-if="!iscopy" name="file-copy-line" color="#797F8A" size="20"></iconpark-icon>
								<iconpark-icon v-else name="check-line" color="#797F8A" size="20"></iconpark-icon>
							</w-button>
					</w-tooltip>
					<!-- 重新生成 -->
					<div>
						<template v-if="!chatStore.isSensitive">
							<w-button type="text" class="cxsc" @click="handleSelect('toggleRender')">
								<iconpark-icon name="reset-left-line" color="#797F8A" size="18"></iconpark-icon>
							</w-button>
						</template>
					</div>
					<!-- 分享-->
					<div class="zhuanfa" @click="handleShare">
						<iconpark-icon name="share-forward-line" color="#797F8A" size="20"></iconpark-icon>
					</div>
								<!-- 声明 -->
								<!-- <div class="rebuildBtn">
									<w-button type="text" class="cxsc" @click="opendialog">
										<img src="/src/assets/chatTheme/mzsm.svg" style="width:16px;height:16px;margin-right: 3px">
										<span style="color:#A6A5B8;font-size:14px;">免责声明</span>
									</w-button>
								</div> -->
								<div v-if="appId != '20'" style="position:relative;flex: 1;display: flex;flex-wrap: nowrap;justify-content: flex-end">
									<!-- 踩和赞 暂时隐藏 关芯客服H5 -->
                                  <w-popconfirm v-if="!isup" update-at-scroll :popup-container="textRef"
										content-class="popoverClass" @popup-visible-change="popupchange" ok-text="提交"
										@ok="onSubmit(1)" placement="top" trigger="click" mini>
										<w-button type="text" shape="circle">
											<!-- <CoolThumbUpLineWe v-if="!isup" size="14" />
											<CoolThumbUpFillWe v-else size="14" /> -->
											<iconpark-icon v-if="!isup" name="thumb-up-line" color="#797F8A" size="20"></iconpark-icon>
                      <iconpark-icon v-else name="thumb-up-fill" color="#797F8A" size="20"></iconpark-icon>
										</w-button>
										<template #content>
											<div class="popcontent">
												<h2>{{ chatStore.zanForm.popTitle }}</h2>
												<w-checkbox-group v-model="zanValue" direction="vertical">
													<w-checkbox v-for="(item, index) in chatStore.zanForm.options" :key="index"
														:value="item.id">{{ item.content }}</w-checkbox>
												</w-checkbox-group>
												<w-textarea v-model="zanText" :placeholder="chatStore.zanForm.placeholder" :max-length="1000"
													show-word-limit :auto-size="{ minRows: 6, maxRows: 10 }" />
											</div>
										</template>
								</w-popconfirm>
								<w-button type="text" v-else shape="circle">
									<!-- <CoolThumbUpLineWe v-if="!isup" size="14" />
									<CoolThumbUpFillWe v-else size="14" /> -->
									<iconpark-icon v-if="!isup" name="thumb-up-line" color="#797F8A" size="20"></iconpark-icon>
									<iconpark-icon v-else name="thumb-up-fill" color="#797F8A" size="20"></iconpark-icon>
								</w-button>
								<w-popconfirm v-if="!isdown" update-at-scroll :popup-container="textRef"
										content-class="popoverClass" @popup-visible-change="popupchange" ok-text="提交"
										@ok="onSubmit(0)" :content="!isdown ? '踩' : '已踩'" placement="top" trigger="click" mini>
										<w-button type="text" shape="circle">
											<!-- <CoolThumbDownLineWe size="14" /> -->
											<iconpark-icon name="thumb-down-line" color="#797F8A" size="20"></iconpark-icon>
										</w-button>
										<template #content>
											<div class="popcontent">
												<h2>{{ chatStore.caiForm.popTitle }} </h2>
												<w-checkbox-group v-model="caiValue" direction="vertical">
													<w-checkbox v-for="(item, index) in chatStore.caiForm.options" :key="index"
														:value="item.id">{{ item.content }}</w-checkbox>
												</w-checkbox-group>
												<w-textarea v-model="caiText" :placeholder="chatStore.caiForm.placeholder" :max-length="1000"
													show-word-limit :auto-size="{ minRows: 6, maxRows: 10 }" />
                        						<!-- <div class="public-option">
                            						<div>是否公开:</div>
                            						<w-radio-group v-model="basePublic" direction="vertical">
														    <w-radio :value="true">是</w-radio>
														    <w-radio :value="false">否</w-radio>
													</w-radio-group>
                          						</div> -->
											</div>
										</template>
									</w-popconfirm>
									<w-button type="text" v-else shape="circle">
										<!-- <CoolThumbDownLineWe v-if="!isdown" size="14" />
										<CoolThumbDownFillWe v-else size="14" /> -->
										<iconpark-icon v-if="!isdown" name="thumb-down-line" color="#797F8A" size="20"></iconpark-icon>
										<iconpark-icon v-else name="thumb-down-fill" color="#797F8A" size="20"></iconpark-icon>
									</w-button>
									
									
								</div>
				</div>
			</div>
			<div class="question-list" v-if="isMobile && tjList.length > 0">
	        	<span class="title">猜你还想了解</span>
	        	<div class="itemList" v-for="(item, index) in tjList" @click="sendQuestion(item.question)" :key="index">
                <span class="name">{{ item.question }}</span>
	        	</div>
	        </div>
		</div>
		<el-dialog v-model="dialogVisible" width="90%" class="mzsmDialog">
			<div class="dialogTitle">免责声明</div>
			<div v-html="getmzsm()" style="padding:0 15px;line-height:24px;font-size:16px;color:#181b49;white-space:pre-wrap;"></div>
			<div style="text-align:center;margin-top:24px;">
				<el-button type="primary" @click="closedialog">我知道了</el-button>
			</div>
		</el-dialog>
		<SharePage v-if="shareVisible" :chatList="isSelected" @close="closeShare" />
    <comLinkPreview :visible="isPopupVisible" :url="popupUrl" :title="popupTitle" @close="closePopup" />
    <Information v-if="showForm" :formParams="formParams"  @close="close"/>
	</div>
</template>

<script setup lang="ts" name="Message">
const Information = defineAsyncComponent(() => import('/@/views/information/index.vue'));
import { setScrollPosition } from '/@/utils/other';
import { computed, ref, watch, onBeforeMount, defineAsyncComponent, onMounted, onUnmounted, inject, nextTick } from 'vue';
import AvatarComponent from './Avatar.vue';
import SharePage from './SharePage.vue';
import { useRoute } from 'vue-router';
import ChatTextComponent from './ChatText.vue';
import { useChatStore } from '/@/stores/chat';
import { Session } from '/@/utils/storage';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { feedback, apiFeedback } from '/@/api/chat';
import { Message } from 'winbox-ui-next';
import { listDialogueQuestion, tjQuestion, sourceAnswer } from '/@/api/chat';
import MarkdownIt from 'markdown-it';
import mic from 'markdown-it-container';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css';
import { useRouter } from 'vue-router';
import mittBus from '/@/utils/mitt';
import { textToSpeechAndPlay, stopPlayback, isPlaying } from '/@/utils/voiceFun';
import { formatDate } from '/@/utils/formatTime';

import { copyText } from '/@/utils/format';
// const { isMobile } = useBasicLayout();
const isMobile = ref(true);
import comLinkPreview from '/@/components/comLinkPreview.vue';

interface Props {
	finishReason?: String;
	matterGuide?: {};
	answer?: string;
	citations?: [];
	createTime?: string;
	id?: string;
	dialogueFileIds?: any;
	dialogueFolderIds?: any;
	paragraph?: string;
	skillId?: string;
	param?: any;
	question: string;
	inversion?: boolean;
	loading?: boolean;
	isLast?: boolean;
	feedBackStatus?: string;
	imgUrl?: [];
	sensitive?: boolean;
	stopChatLoading?: boolean;
	suggestOrg?: string;
	contentQaType?: string;
	answerFlag?: boolean;
	plainText?: string;
	dialogueId?: string;
}
interface Emit {
	(ev: 'regenerate'): void;
	(ev: 'delete'): void;
}

// const isSelected = inject('select')
const router = useRouter();
const isSelected = ref([]);
const hidden = ref<boolean[]>([]);
const props = defineProps<Props>();
const chatStore = useChatStore();
const asRawText = ref(props.inversion);
const showSuggestOrg = ref(false);
const className = computed(() => {
	if (props.inversion) {
		return isMobile.value ? 'isPaddingRightMobile' : 'isPaddingRight';
	} else {
		return isMobile.value ? 'isPaddingLeftMobile' : 'isPaddingLeft';
	}
});
const isQuestionStatus = computed(() => !props.inversion && !props.loading && props.isLast);
const questionList = ref([]);
const requestLoading = ref(true);
const showRelateSource = ref(false);
const isPopupVisible = ref(false);
const popupUrl = ref('');
const popupTitle = ref('');

const openPopup = (item: object) => {
	popupUrl.value = item.fileLink;
	popupTitle.value = item.fileName ? item.fileName : item.fileLink;
	isPopupVisible.value = true;
};

const closePopup = () => {
	isPopupVisible.value = false;
};
const closeShare = () => {
	chatStore.$state.isShare = false;
	shareVisible.value = false;
};

const zanValue = ref([]);
const caiValue = ref([]);
const zanText = ref('');
const caiText = ref('');
const basePublic = ref(true);
const timer2 = ref(null);
const iscopy = ref(false);

const isup = ref(false);
const isdown = ref(false);
const isfinish = ref(false);
let isHighlightCode = ref(false);
const dialogVisible = ref(false);
const shareVisible = ref(false);
const timer = ref(null);
const textRef = ref<HTMLElement>();
const messageRef = ref<HTMLElement>();
const shareMessageRef = ref<HTMLElement>();
const route = useRoute();
const previewDocFlag = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo && appInfo.sourceShowFlag === '1';
};
let chatMode = computed(() => route.path.indexOf(localStorage.getItem(`${route.params.appId}appId`)) != -1);
const { appId } = route.params as { appId: string };
const paramData = computed(() => {
	if (props.param) {
		let params = props.param;
		try {
			params = JSON.parse(props.param);
		} catch (error) {}
		let param_ = {};
		for (const key in params) {
			let value = params[key] || '';
			try {
				value = JSON.parse(value);
			} catch (error) {}
			try {
				let keyObj: any = chatStore.dialogueParamsListOld.filter((item) => {
					return item.key === key;
				})[0];
				const range_ = range(keyObj);
				range_.forEach((rangeItem: any) => {
					if (typeof value === 'object') {
						value = value.map((valKey: any) => {
							return valKey === rangeItem.value ? rangeItem.label : valKey;
						});
					} else {
						value = value == rangeItem.value ? rangeItem.label : value;
					}
				});

				if (param_['数据源']) {
					if (param_['数据源'].indexOf('离线') != -1) {
						continue;
					}
				}
				if (keyObj.isSystem !== true) {
					param_[keyObj.name] = value;
				}
			} catch (error) {}
		}
		return param_;
	}
	return {};
});
const moreQuesion = ref('');
const getQuestionList = async () => {
	requestLoading.value = true;
	let res = await listDialogueQuestion({
		question: props.question,
	});
	requestLoading.value = false;
	questionList.value = [];

	if (res?.data && res?.data.length) {
		if (props.answerFlag) {
			moreQuesion.value = '您可能还想了解：';
		} else {
			moreQuesion.value = '您是否想问：';
		}
		appear(res.data[0], 0, res.data);
	}
	showSuggestOrg.value = true;
};
// 流式输出函数
const appear = (content, index, data) => {
	if (!content) return;
	clearTimeout(timer2.value);
	var speed = 50; //设置定时的速度 越来越快
	var count = 1;
	function changeContent() {
		questionList.value[index] = content.substring(0, count); //截取字符串
		setScrollPosition(isMobile.value ? '.message-list' : '.center-side', 'auto');
		count++;
		if (count != content.length + 1) {
			timer2.value = setTimeout(changeContent, speed);
		} else {
			if (index < data.length - 1) appear(data[index + 1], index + 1, data);
		}
	}
	changeContent();
};
const sendQuestion = (item: string) => {
	if (chatStore.dialogueLoading) return;
	let param = props.param;
	if (typeof param === 'object') {
		param = JSON.stringify(props.param);
	}
	// let appId = typeof props.param === 'string'?JSON.parse(props.param)?.promptId : props.param?.promptId
	const routeParams = route.params;
	const data: any = {
		knowledgeBaseId: routeParams.appId,
		appId: routeParams.appId,
		// appId:  appId,
		content: item,
		conversationId: route.params.conversationId,
		param,
	};
	if (props?.dialogueFileIds) {
		data.dialogueFileIds = props?.dialogueFileIds;
	}
	if (props?.dialogueFolderIds) {
		data.dialogueFolderIds = props?.dialogueFolderIds;
	}
	if (props?.paragraph) {
		data.paragraph = props?.paragraph;
	}
	if (props?.skillId) {
		data.skillId = props?.skillId;
	}
	chatStore.setChatList(data);
	return;
};
const handleSelect = (key: 'copyText' | 'delete' | 'toggleRender', source: string = '') => {
	if (key === 'up') {
		isup.value = true;
		isdown.value = false;
		isfinish.value = true;
		return;
	}
	if (key === 'down') {
		isdown.value = true;
		isup.value = false;
		isfinish.value = true;
		return;
	}
	if (key === 'copyText') {
		clearTimeout(timer.value);
		iscopy.value = true;
		let text = props.plainText ?? props.answer ?? '';
		text = text.replace(/:::r|:::|/g, '');
		if (text === chatStore.errorText) {
			return;
		}
		copyText({ text });
		timer.value = setTimeout(() => {
			iscopy.value = false;
		}, 2000);
		return;
	}
	if (key === 'toggleRender') {
		if (chatStore.dialogueLoading) return;
		let param = props.param;
		if (typeof param === 'object') {
			param = JSON.stringify(props.param);
		}
		if (source && source != '') {
			let data = JSON.parse(param);
			data.web_source_list = source;
			param = JSON.stringify(data);
		}
		// let appId = typeof props.param === 'string'?JSON.parse(props.param)?.promptId : props.param?.promptId
		const routeParams = route.params;
		const data: any = {
			knowledgeBaseId: routeParams.appId,
			appId: routeParams.appId,
			// appId:  appId,
			content: props.question,
			conversationId: route.params.conversationId,
			param,
		};
		if (props?.dialogueFileIds) {
			data.dialogueFileIds = props?.dialogueFileIds;
		}
		if (props?.dialogueFolderIds) {
			data.dialogueFolderIds = props?.dialogueFolderIds;
		}
		if (props?.imgUrl) {
			data.imgUrl = props?.imgUrl;
		}
		if (props?.paragraph) {
			data.paragraph = props?.paragraph;
		}
		if (props?.skillId) {
			data.skillId = props?.skillId;
		}
		chatStore.setChatList(data);
		return;
	}
};

const range = (val) => {
	let value = val.range;
	if (val.type === 'ENUM') {
		value = value.map((item) => {
			if (item.indexOf('|') > -1) {
				let [value, label] = item.split('|').map((s) => s.trim());

				return { label, value };
			}
			let [value, label] = [item, item];
			return { label, value };
		});
	}
	return value;
};

onBeforeMount(() => {
	// 暂时隐藏问题推荐、答案溯源
});
onMounted(() => {
	if (!props.inversion) {
		mittBus.on('dialogueId', (appInfo) => {
			setTimeout(() => {
				appInfo.sourceShowFlag === '1' && sourceAnswerList();
				appInfo.recommendQuestionsShowFlag === '1' && tjQuestionList();
				mittBus.all.delete('dialogueId', () => {});
			}, 1000);
		});
	}
});

onUnmounted(() => {
	mittBus.all.delete('dialogueId', () => {});
});
const tjList = ref([]);
const tjQuestionList = async () => {
	let res = await tjQuestion({
		question: props.question,
		dialogueId: props.dialogueId,
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
	});
	if (res?.code == '000000') {
		tjList.value = res.data || [];
	} else {
		tjList.value = [];
	}
};

const syList = ref([]);
const sourceAnswerList = async () => {
	let res = await sourceAnswer({
		// dialogueId: route.params?.conversationId,
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
		dialogueId: sessionStorage.getItem('dialogueId'),
	});
	if (res?.code == '000000') {
		const list = res?.data?.sourceAnswerResultList ? res.data.sourceAnswerResultList : res.data;
		if (list.length > 0) {
			if (list[0].sourceType == 1) {
				syList.value = list.filter((obj, index, self) => obj.fileLink && index === self.findIndex((t) => t.fileName === obj.fileName));
			} else {
				syList.value = list.filter((obj, index, self) => obj.fileLink && index === self.findIndex((t) => t.fileLink === obj.fileLink));
			}
			setScrollPosition(isMobile.value ? '.message-list' : '.center-side', 'auto');
		} else {
			syList.value = [];
		}
	} else {
		syList.value = [];
	}
};

const emit = defineEmits<Emit>();
const handleBofang = () => { 
	let text = props.plainText ?? props.answer ?? '';
	text = text.replace(/:::r|:::|/g, '');
	let newText = text.split('<p style="padding: 6px 0;border-top: 1px solid rgba(0, 0, 0, 0.12);height: 1px;margin-top:12px;"/>') || [];
	// play_tts(newText?newText[0]:'');
	if (isPlaying) {
		stopPlayback();
	} else {
		textToSpeechAndPlay(newText ? newText[0] : '', 15, 1);
	}
};
//分享
const handleShare = () => {
	chatStore.hidden.splice(0);
	shareVisible.value = true;
	isSelected.value = [];
	chatStore.$state.isShare = true;
	console.log(chatStore.$state.isShare, 'chatStore.$state.isShare');
	chatStore.chatList?.forEach((item, index) => {
		item.sort = index;
		item.checked = false;
		item.sort = index;
		isSelected.value.push(item);
		console.log(isSelected, 'isSelected里有没有');
		chatStore.hidden.push(true);
	});
	console.log(chatStore.hidden, 'chatStore.hidden');
	chatStore.fileUploadList = [...chatStore.tempFileList];
};

// 语言合成代码开始*

const btnStatus_tts = ref('UNDEFINED');
const audioPlayer = new AudioPlayer('./tts');
let APPID = '';
let API_SECRET = '';
let API_KEY = '';
const btnControl = ref('');
const changeBtnStatus_tts = (status, type = '') => {
	btnStatus_tts.value = status;
	if (status === 'UNDEFINED') {
		btnControl.value = '立即合成';
	} else if (status === 'CONNECTING') {
		btnControl.value = '正在合成';
	} else if (status === 'PLAY') {
		btnControl.value = '停止播放';
	} else if (status === 'STOP') {
		if (type != 'quit') {
			// setTimeout(() => {
			// 	btnControlFun()
			// }, 100);
		}
		btnControl.value = '重新播放';
	}
};
audioPlayer.onPlay = () => {
	changeBtnStatus_tts('PLAY');
};
audioPlayer.onStop = (audioDatas) => {
	console.log(audioDatas);
	btnStatus_tts.value === 'PLAY' && changeBtnStatus_tts('STOP');
};
const getWebSocketUrl_tts = () => {
	var apiKey = API_KEY;
	var apiSecret = API_SECRET;
	var url = 'wss://tts-api.xfyun.cn/v2/tts';
	var host = location.host;
	var date = new Date().toGMTString();
	var algorithm = 'hmac-sha256';
	var headers = 'host date request-line';
	var signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v2/tts HTTP/1.1`;
	var signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret);
	var signature = CryptoJS.enc.Base64.stringify(signatureSha);
	var authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
	var authorization = btoa(authorizationOrigin);
	url = `${url}?authorization=${authorization}&date=${date}&host=${host}`;
	return url;
};
const encodeText = (text, type) => {
	if (type === 'unicode') {
		let buf = new ArrayBuffer(text.length * 4);
		let bufView = new Uint16Array(buf);
		for (let i = 0, strlen = text.length; i < strlen; i++) {
			bufView[i] = text.charCodeAt(i);
		}
		let binary = '';
		let bytes = new Uint8Array(buf);
		let len = bytes.byteLength;
		for (let i = 0; i < len; i++) {
			binary += String.fromCharCode(bytes[i]);
		}
		return window.btoa(binary);
	} else {
		return Base64.encode(text);
	}
};
let ttsWS;
const connectWebSocket_tts = (tt) => {
	const tts_config = getRandomConfig();
	APPID = tts_config.APPID;
	API_SECRET = tts_config.API_SECRET;
	API_KEY = tts_config.API_KEY;
	const url = getWebSocketUrl_tts();
	if ('WebSocket' in window) {
		ttsWS = new WebSocket(url);
	} else if ('MozWebSocket' in window) {
		ttsWS = new MozWebSocket(url);
	} else {
		alert('浏览器不支持WebSocket');
		return;
	}
	changeBtnStatus_tts('CONNECTING');

	ttsWS.onopen = (e) => {
		audioPlayer.start({
			autoPlay: true,
			sampleRate: 16000,
			resumePlayDuration: 1000,
		});
		changeBtnStatus_tts('PLAY');
		let ttext = tt || sayStr.value || '抱歉，未正确识别到语音，请重新提问。';
		var tte = 'UTF8';
		var params = {
			common: {
				app_id: APPID,
			},
			business: {
				aue: 'raw',
				auf: 'audio/L16;rate=16000',
				vcn: 'xiaoyan', //'aisjiuxu' 'aisxping' 'aisjinger' 'aisbabyxu' 'vixq' 'xiaoyan'
				speed: 70,
				volume: 50,
				pitch: 50,
				bgs: 0,
				tte,
			},
			data: {
				status: 2,
				text: encodeText(ttext, tte),
			},
		};
		ttsWS.send(JSON.stringify(params));
	};
	ttsWS.onmessage = (e) => {
		let jsonData = JSON.parse(e.data);
		// 合成失败
		if (jsonData.code !== 0) {
			console.error(jsonData);
			changeBtnStatus_tts('UNDEFINED');
			return;
		}
		audioPlayer.postMessage({
			type: 'base64',
			data: jsonData.data.audio,
			isLastData: jsonData.data.status === 2,
		});
		if (jsonData.code === 0 && jsonData.data.status === 2) {
			ttsWS.close();
		}
	};
	ttsWS.onerror = (e) => {
		console.error(e);
	};
	ttsWS.onclose = (e) => {
		// console.log(e);
	};
};

const play_tts = (tt) => {
	if (btnStatus_tts.value === 'UNDEFINED') {
		// 开始合成
		connectWebSocket_tts(tt);
	} else if (btnStatus_tts.value === 'CONNECTING') {
		// 停止合成
		changeBtnStatus_tts('UNDEFINED');
		ttsWS?.close();
		audioPlayer.reset();
		return;
	} else if (btnStatus_tts.value === 'PLAY') {
		audioPlayer.stop();
	} else if (btnStatus_tts.value === 'STOP') {
		audioPlayer.play();
	}
};

// 语言合成代码结束****************************

const feedbacksubmit = async (type) => {
	let data = {
		appId: appId,
		dialogueId: props.id,
		feedback: type == 1 ? zanText.value : caiText.value,
		type: type,
		dialogueType: chatMode.value ? 'QA' : 'KNOWLEDGE_BASE',
	};
	if (type == 1) {
		data.upvoteLabel = zanValue.value;
	} else {
		data.downvoteLabel = caiValue.value;
	}
	type == 1 ? handleSelect('up') : handleSelect('down');
	//chatStore.feedbacksubmit(data)
	let res = await feedback(data);
	let msg = data.type == 1 ? '点赞' : '点踩';
	if (res.code == 200) {
		Message.success(msg + '成功');
	} else {
		isup.value = false;
		isdown.value = false;
	}
};

const onSubmit = async (type) => {
	let data = {
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
		dialogueId: sessionStorage.getItem('dialogueId'),
		feedbackType: type,
		feedbackContent: type == 1 ? zanText.value : '',
		feedbackTime: formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS'),
		applicationType: 'guanxin-kefu',
	};
	if (type == 0) {
		data.title = props.question;
		data.basePublic = basePublic.value;
		data.content = caiText.value;
	}
	type == 1 ? handleSelect('up') : handleSelect('down');
	//chatStore.feedbacksubmit(data)
	let res = await apiFeedback(data);
	let msg = type == 1 ? '点赞' : '点踩';
	if (res.code == '000000') {
		Message.success(msg + '成功');
	} else {
		isup.value = false;
		isdown.value = false;
		if (res?.msg) {
			Message.warning(res.msg);
		}
	}
};

const copy = (val, imgUrl) => {
	let text = val || '';
	if (imgUrl) {
		text = imgUrl + text;
	}
	copyText({ text });
	Message.success('复制成功');
};
const edit = () => {
	mittBus.emit('editQestionToInput', props);
};
const opendialog = () => {
	dialogVisible.value = true;
};
const closedialog = () => {
	dialogVisible.value = false;
};
const getmzsm = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.disclaimer : '';
};
const popupchange = (val) => {
	if (!val) {
		caiValue.value = [];
		zanValue.value = [];
		caiText.value = '';
		zanText.value = '';
	}
};

const mdi = new MarkdownIt({
	html: true,
	linkify: false,
	highlight(code, language) {
		const validLang = !!(language && hljs.getLanguage(language));
		if (validLang) {
			const lang = language ?? '';
			return highlightBlock(hljs.highlight(code, { language: lang }).value, lang);
		}
		return highlightBlock(hljs.highlightAuto(code).value, '');
	},
});
mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' });
mdi.use(mic, 'r', {
	// 验证自定义容器是否符合规则
	validate: function (params) {
		return params.trim().match(/^r\s+(.*)$/);
	},
	// 渲染自定义容器的开头
	render: function (tokens, idx) {},
});
function highlightBlock(str: string, lang?: string) {
	let langStr = '';
	if (lang) {
		langStr = `<span class="code-block-header__lang">${lang}</span>`;
	}
	return `<pre class="code-block-wrapper"><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}
watch(
	() => props.feedBackStatus,
	(newVal: any) => {
		console.log('==>>>>>>>>>>>watch');
		isup.value = newVal == 1 ? true : false;
		isdown.value = newVal == 0 ? true : false;
	},
	{
		immediate: true,
		deep: true,
	}
);
watch(
	() => isQuestionStatus.value,
	(newVal: any) => {
		if (newVal == true && chatMode.value) {
			if (!props?.stopChatLoading) getQuestionList();
		}
	},
	{
		immediate: true,
		deep: true,
	}
);

const activeName = ref('first');
const handleClick = (tab: TabsPaneContext, event: Event) => {
	if (tab.props.name == 'first') {
		tjQuestionList();
	} else if (tab.props.name == 'second') {
		sourceAnswerList();
	}
};

const showForm = ref(false);
const formParams = ref({});
const handleFillForm = (info) => {
	showForm.value = true;
	formParams.value = info;
};
const close = () => {
	showForm.value = false;
	formParams.value = {};
};
</script>
<style scoped lang="scss">
@import '/@/theme/mixins/index.scss';
.yy-message-videoLargeScreen {
	display: flex;
	margin: 48px 0;
	.ml8 {
		margin-left: 8px;
	}
	.mr8 {
		margin-right: 8px;
	}
	.btns {
		display: flex;
		align-items: flex-end;
		justify-content: space-between;
		margin-top: 8px;

		.cool-icon {
			color: #9a99aa;
		}
		.zhuanfa {
			margin-bottom: 2px;
			width: 36px;
		}
		.w-btn {
			width: 36px;
			height: 36px;
			color: #646479;
			margin-right: 8px;
			// background: #fff;
			// margin-left: 14px;
			// box-shadow: 0px 6px 20px 0px rgba(30,64,175,0.1);
		}

		.rebuildBtn {
			@include add-size($font-size-base14, $size);
			color: #646479;
			height: 24px;
			display: flex;
			align-items: center;
			justify-content: center;
			border-radius: 12px;
			margin-left: 0;
			cursor: pointer;

			.w-btn {
				margin-right: 40px;
				font-weight: 500;
			}
			.w-btn-text:active {
				background: transparent;
			}
			:deep(.w-btn-icon) {
				margin-right: 3px;
			}

			.cxsc {
				width: 75px;
				margin: 0;
				padding: 0;
				margin-right: 8px;
			}

			.otherSource {
				position: relative;
				padding-left: 20px;
				@include add-size($font-size-base14, $size);

				> span {
					color: #355eff;
					cursor: pointer;
					display: inline-block;
					margin-right: 8px;
				}

				&::before {
					position: absolute;
					content: '';
					height: 14px;
					width: 1px;
					left: 0px;
					top: 4px;
					background: #d0d5dc;
				}
			}
		}
	}
}
.question-list {
	margin-top: 8px;
	.title {
		font-family: MiSans, MiSans;
		font-weight: 600;
		font-size: 14px;
		color: #3f4247;
		line-height: 20px;
		text-align: justify;
		font-style: normal;
	}
	.itemList {
		.name {
			display: inline-block;
			padding: 10px 16px;
			margin-top: 12px;
			border-radius: 8px;
			border: 1px solid #d7dae0;
			font-family: MiSans, MiSans;
			font-weight: 400;
			font-size: 14px;
			color: #3f4247;
			line-height: 20px;
			cursor: pointer;
		}
	}
}
:deep(.answerTabs) {
	.el-tabs__nav {
		width: 100%;
		border: none !important;
	}
	.el-tabs__item {
		width: 50%;
		border: 1px solid #d0d5dc !important;
		font-size: 16px;
		font-weight: 400;
		color: #181b49;
	}
	.is-active {
		color: #3976f6;
		border: 1px solid #3976f6 !important;
	}
	.el-tabs__header {
		border: none;
	}
}
:deep(.mzsmDialog) {
	border-radius: 12px;
	padding: 0;
	padding-bottom: 15px;
	.el-dialog__header {
		display: none;
	}
	.el-dialog__headerbtn {
		display: none;
	}
	.dialogTitle {
		padding: 36px 0 16px;
		border-radius: 12px 12px 0 0;
		font-family: MiSans, MiSans;
		font-weight: 500;
		font-size: 22px;
		color: #181b49;
		line-height: 20px;
		text-align: center;
		font-style: normal;
		background-image: linear-gradient(180deg, rgba(22, 158, 154, 0.2) 0%, rgba(22, 158, 154, 0) 99%);
	}
	.el-button {
		width: calc(100% - 30px);
		padding: 24px 0;
		border: 1px solid #169e9a !important;
		background: transparent !important;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 18px;
		color: #fff !important;
		font-style: normal;
		border-radius: 20px;
		> span {
			color: #169e9a !important;
		}
	}
}
.fontSize16 {
	@include add-size($font-size-base16, $size);
}

.font16 {
	@include add-size($font-size-base16, $size);
}

.isPaddingLeft {
	padding-right: 64px;
}

.isPaddingLeftMobile {
	padding-right: 0;
}

.isPaddingRight {
	padding-left: 64px;
	padding-right: initial;
}

.isPaddingRightMobile {
	padding-left: 0;
	padding-right: initial;
}

.tagList {
	color: #9a99aa;

	.tip {
		display: inline-block;
		padding: 0 5px;
	}

	&:last-child {
		.tip {
			display: none;
		}
	}
}

.bofang-btn {
	background: #fff !important;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
	width: 36px !important;
	height: 36px !important;
	color: #9a99aa !important;
}

.flex-row {
	:deep(.w-trigger-popup) {
		// z-index: 1 !important;
	}
}

// .question{
// 	border-top: 1px solid rgba(0, 0, 0, 0.1);
// 	.btns{
// 		margin-top: 20px;
// 		margin-bottom: 11px;
// 		color: #646479;
// 		.rebuildBtn{
// 			color: #646479 !important;
// 			margin-right:16px;
// 			.cool-icon{color: #646479;}
// 			span:hover{
// 				color: var(--color-text-1);
// 				background-color: var(--color-fill-2);
// 				border-color: transparent;
// 				.cool-icon{color: var(--color-text-1);}
// 			}
// 		}
// 		.tutle{
// 			font-size: var(--font14);
// 			font-family: PingFangSC-Regular, PingFang SC;
// 			font-weight: 400;
// 			color: #646479;
// 		}
// 	}
// 	.item{
// 		width: 100%;
// 		line-height: 32px;
// 		font-size: var(--font14);
// 		font-family: PingFangSC-Regular, PingFang SC;
// 		font-weight: 400;
// 		color: #646479;
// 		position: relative;
// 		padding-right: 25px;
// 		box-sizing: border-box;
// 		border-radius: 16px;
// 		cursor: pointer;
// 		.icon{
// 			position: absolute;
// 			right: 0;
// 			top: 6px;
// 			display: none;
// 		}
// 		&:hover{
// 			color: var(--w-color-primary);
// 			.icon{
// 				display: block;
// 			}
// 		}
// 	}
// 	.item:nth-child(1){
// 		margin-top: 0;
// 	}
// 	.questionfooter{
// 		margin-top: 5px;
// 		margin-bottom: 0;
// 	}
// }

.question {
	width: 100%;

	// background: #fff;
	// border-radius: 20px;
	// border-top: 1px solid rgba(0, 0, 0, 0.1);
	.btns {
		width: 100%;
		padding: 0 1rem;
		box-sizing: border-box;
		margin-top: 0;
		margin-bottom: 11px;
		color: #646479;

		.rebuildBtn {
			color: #646479 !important;
			margin-right: 16px;

			.cool-icon {
				color: #646479;
			}

			span:hover {
				color: var(--color-text-1);
				background-color: var(--color-fill-2);
				border-color: transparent;

				.cool-icon {
					color: var(--color-text-1);
				}
			}
		}

		.tutle {
			@include add-size($font-size-base14, $size);
			font-family: PingFangSC-Regular, PingFang SC;
			font-weight: 400;
			color: #646479;
		}

		.title {
			color: #333;
			font-weight: bold;
		}
	}

	// .item {
	// 	width: 100%;
	// 	padding: 0 1rem;
	// 	box-sizing: border-box;
	// 	line-height: 34px;
	// 	border-radius: 16px;
	// 	// border: 1px solid #E4E8EE;
	// 	@include add-size($font-size-base14, $size);
	// 	font-family: PingFangSC-Regular, PingFang SC;
	// 	font-weight: 400;
	// 	color: #646479;
	// 	position: relative;
	// 	box-sizing: border-box;
	// 	cursor: pointer;
	// 	margin-top: 8px;
	// 	overflow: hidden;
	// 	// background: #f4f6f9;
	// 	// border-radius:5px;
	// 	color: #355eff;
	// 	// .icon{
	// 	// 	// position: absolute;
	// 	// 	// right: 1rem;
	// 	// 	margin-top: 6px;
	// 	// 	margin-bottom: 6px;
	// 	// 	float: right;
	// 	// 	display: none;
	// 	// }
	// 	// &:hover{
	// 	// 	color: var(--w-color-primary);
	// 	// 	background: rgba(255,255,255,0.5);
	// 	// 	.icon{
	// 	// 		display: block;
	// 	// 	}
	// 	// }
	// }

	// .item:nth-child(1) {
	// 	margin-top: 0;
	// }
	.list {
		display: flex;
		flex-wrap: wrap;
		widows: 100%;

		.item {
			width: calc(50% - 8px);

			// .text {
			// 	overflow: hidden;
			// 	white-space: nowrap;
			// 	text-overflow: ellipsis;
			// }
		}
	}

	.item {
		// flex: 1;
		padding: 0;
		margin-right: 16px;
		margin-bottom: 16px;

		.box {
			@include add-size($font-size-base14, $size);
			font-family: MicrosoftYaHei;
			color: #010101;
			padding: 6px 15px;
			background: #fff;
			cursor: pointer;
			border-radius: 12px;
			font-size: 15px;
			display: flex;
			// justify-content: space-between;
			align-items: center;

			.pc-img {
				display: none;
			}
		}
	}

	.item.active,
	.item:hover {
		.box {
			box-shadow: 0px 5px 8px 0px rgba(45, 55, 85, 0.1);
			font-family: MicrosoftYaHei;
			color: #4085f4;

			.pc-img {
				display: block;
			}
		}
	}

	.item:last-child {
		margin-right: 0;
	}

	.questionfooter {
		margin-top: 5px;
		margin-bottom: 0;
	}
}
</style>
<style lang="scss">
.popoverClass {
	background: linear-gradient(130deg, #ffffff 0%, #ffffff 100%), linear-gradient(180deg, #f7f5ff 0%, rgba(239, 243, 251, 0) 100%) !important;
	box-shadow: 0px 6px 20px 0px rgba(30, 64, 175, 0.06) !important;

	h2 {
		color: #646479;
	}

	.popcontent {
		width: 240px;
	}

	.w-popconfirm-icon {
		display: none !important;
	}

	.w-textarea-word-limit {
		background: #fff;
	}
}
</style>
<style lang="scss">
@import '/@/theme/mixins/index.scss';

.exampleModal {
	background: linear-gradient(130deg, #dfeafc 0%, #ffffff 100%), linear-gradient(180deg, #ede7ff 0%, rgba(239, 243, 251, 0) 100%);
	border-radius: 12px;

	.markdown-body {
		@include add-size($font-size-base16, $size);

		pre {
			overflow: initial;

			code.hljs {
				overflow: initial;
				white-space: pre-wrap;
				word-wrap: break-word;
				width: 100%;
			}
		}
	}

	.w-icon {
		width: 1.5em;
		height: 1.5em;
	}

	.detail {
		@include add-size($font-size-base16, $size);
		line-height: 32px;
		color: #181b49;
		height: 287px;
		overflow: auto;
		height: 540px;
		border-radius: 8px;

		> span {
			padding: 0 2px;
			color: #355eff;
		}
	}

	.exampleClass {
		background: #fff;
		border-radius: 8px;

		.ff {
			text-align: right;
			color: #355eff;
			cursor: pointer;
			display: flex;
			justify-content: space-between;
			height: 40px;
			background: #f6f8ff;
			border-radius: 8px 8px 0px 0px;
			line-height: 40px;
			padding: 0 10px;

			.tabs {
				color: #646479;
				@include add-size($font-size-base16, $size);
			}
		}
	}

	.w-tabs-tab-title {
		@include add-size($font-size-base16, $size);
	}

	.w-tabs-nav::before {
		height: 0;
	}

	.w-tabs-tab {
		margin: 0 10px;
	}

	.w-tabs-tab-active {
		.w-tabs-tab-title {
			color: #181b49;
		}
	}
}

.w-btn-size-default {
	@include add-size($font-size-base14, $size);
}

// .mz-dialog {
// 	padding: 0;
// 	border-radius: 12px;

// 	.mz-dialog-title {
// 		@include add-size(20px, $size);
// 		font-family: PingFangSC, PingFang SC;
// 		font-weight: 700 !important;
// 		color: #181b49 !important;
// 		background: linear-gradient( 180deg, #C5D6FF 0%, rgba(255,255,255,0) 100%);
// 	}

// 	.wenben {
// 		@include add-size($font-size-base14, $size);
// 		font-family: PingFangSC, PingFang SC;
// 		font-weight: 400;
// 		color: #9b9ba9;
// 		margin-top: 16px;
// 	}
// }
</style>