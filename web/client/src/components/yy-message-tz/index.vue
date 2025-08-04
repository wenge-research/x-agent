<template>
  <div ref="messageRef" class="flex w-full items-start"
    :class="[{ 'flex-row-reverse': inversion, 'mb-4': inversion, 'mt-4': inversion, 'mb-3': !inversion }, className]">
    <div :class="['flex-shrink-0', inversion ? 'ml-2' : 'mr-3']">
      <AvatarComponent v-if="!isMobile" :isLoading="loading" :image="inversion" />
    </div>
    <div class="fontSize16 max-w-full items-start">
      <p v-if="!inversion" class="text-xs text-[#b4bbc4] mb-2" :class="[!inversion ? 'text-right' : 'text-left']">
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
      <div class="flex items-end gap-1 relative" :class="[inversion ? 'flex-row-reverse' : 'flex-row']" ref="textRef">
        <w-tooltip v-if="inversion" placement="tr" :arrow-style="{ right: 0, left: 'inherit' }" :update-at-scroll="true"
          background-color="#fff" :content-style="{ padding: '8px 16px', borderRadius: '4px' }">
          <template #content v-if="inversion">
            <CoolBianji_9oe2ecpc title="编辑" @click="edit()" size="16" color="#9A99AA"
              style="margin-right: 10px;cursor:pointer;" />
            <CoolFuzhi_9oe2ecmc title="复制" @click="copy(question, imgUrl)" size="14" color="#9A99AA"
              style="cursor:pointer;" />
          </template>
          <TextComponent :inversion="inversion" :loading="loading" :citations="citations"
            :dialogueFileIds="dialogueFileIds" :dialogueFolderIds="dialogueFolderIds" :paragraph="paragraph || ''"
            :skillId="skillId" :text="inversion ? question : answer" :as-raw-text="false" :asRawText="asRawText"
            :imgUrl="imgUrl" :params="param" v-model:isHighlightCode="isHighlightCode"></TextComponent>
        </w-tooltip>

        <TextComponent v-else :inversion="inversion" :loading="loading" :citations="citations"
          :dialogueFileIds="dialogueFileIds" :dialogueFolderIds="dialogueFolderIds" :paragraph="paragraph || ''"
          :skillId="skillId" :text="inversion ? question : answer" :as-raw-text="false" :asRawText="asRawText"
          :params="param" :imgUrl="imgUrl" v-model:isHighlightCode="isHighlightCode" :matterGuide="matterGuide"
          :finishReason="finishReason" @handleFillForm="handleFillForm">
          <template #footer>
            <!-- <div @click="opendialog"
              style="cursor:pointer;display:flex;align-items:center;position:absolute;top: 0px;right: 0;font-family: MiSans, MiSans;font-weight: 400;font-size: 14px;color: #646479;line-height: 20px;text-align: left;line-height: 5px;font-style: normal;width: 108px;height: 36px;background: rgba(64, 133, 244, .12);border-radius: 0px 12px 0px 8px;">
              <span
                style="display:inline-block;width:14px;height:14px;border-radius:50%;border:1px solid rgb(100, 100, 121);margin-right:3px;position: relative;top: 2px;">
                <img src="/src/assets/chatImages/mzsm.svg">
              </span>
              <p>免责声明</p>
            </div> -->
            <div style="padding: 0 0 1rem 1rem;margin-top: -20px;display: flex;align-items: center"
              v-if="props.suggestOrg && showSuggestOrg">
              <span style="color: #000000;font-size:16px;">您也可以通过</span>
              <w-button @click="sendQuestion(props.suggestOrg)" size="small"
                style="background-color: #409EFF;color: #fff;border-radius: 5px">
                {{ props.suggestOrg }}
              </w-button>
              <span style="color: #000000;font-size:16px;">查询或咨询</span>
            </div>
            <div v-if="!inversion && !loading" style="background: #fff;padding-top: 0;">
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
                        <img class="pc-img" v-if="!isMobile" style="height: 20px" src="/src/assets/zc/send1.png" alt="" />
                      </div>
                    </div>
                  </template>
                </div>
              </div>
              <!-- 暂时隐藏问题推荐、答案溯源 -->
              <!-- <div>
								<p style="border-top:1px solid rgba(0,0,0,0.12);margin:26px 0 16px;"></p>
								<el-tabs v-model="activeName" type="card" class="answerTabs" @tab-click="handleClick">
									<el-tab-pane label="问题推荐" name="first">
										<div v-for="item in tjList" style="display:flex;margin-bottom:24px;align-items:center;width:calc(100vw - 80px);" v-if="tjList.length>0">
											<img src="/src/assets/chatImages/fasong.svg" style="margin-right:5px;width:15px;height:15px;">
											<p style="font-size:16px;color: #646479;line-height:24px;" @click="sendQuestion(item?.question)">{{ item?.question }}</p>
										</div>
										<div v-else style="padding:30px 0;">
											<img src="/src/assets/chatImages/quexing.svg" style="width:88px;margin:auto;">
										</div>
									</el-tab-pane>
									<el-tab-pane label="答案溯源" name="second">
										<div v-for="item in syList" style="display:flex;margin-bottom:24px;align-items:center;width:calc(100vw - 80px);" v-if="syList.length>0">
											<img src="/src/assets/chatImages/link.svg" style="margin-right:5px;width:15px;height:15px;">
											<p style="font-size:16px;color: #646479;line-height:24px;">
												<a :href="item.text" target="blank">{{ item.text }}</a>
											</p>
										</div>
										<div v-else  style="padding:30px 0;">
											<img src="/src/assets/chatImages/quexing.svg" style="width:88px;margin:auto;">
										</div>
									</el-tab-pane>
								</el-tabs>
								<p style="border-top:1px solid rgba(0,0,0,0.12);margin:16px 0"></p>
							</div> -->
              <div class="relate-source" v-if="syList.length>0">
								<span class="title">相关来源 {{syList.length}}</span>
								<ul>
									<li  v-for="(item, index) in syList" :key="index">
										<w-link v-if="item.fileLink && !chatStore.isFileLink(item?.fileLink)" :href="item.fileLink" target="_blank" icon >{{item.fileName !=null?item.fileName :item.fileLink }}</w-link>
										
										<w-link v-else @click.stop="openPreview(item)" icon >{{item.fileName !=null?item.fileName :item.fileLink }}</w-link>
									</li>
								</ul>
							</div>
              <div class="show-preview" v-if="showPreview">
								<div  v-for="(item, index) in tjList" :key="index">
                  <img :src="item.answer" style="width:300px;" v-if="item.answer && item.answer.indexOf('mp4') === -1">
                  <video width="640" height="360" controls autoplay v-if="item.answer && item.answer.indexOf('mp4') > -1">
                    <source :src="item.answer" type="video/mp4">
                    您的浏览器不支持 video 标签。
                  </video>
                </div>
							</div>
              <div class="btns adaption">
                <div class="left">
                  <div class="rebuildBtn">
                    <template v-if="!chatStore.isSensitive">
                      <w-button type="text" class="cxsc" @click="handleSelect('toggleRender')">
                        <template #icon>
                          <CoolRefreshLineWe size="14" color="#383D47"/>
                        </template>
                        <span style="color:#383D47;">重新生成</span>
                      </w-button>
                    </template>
                  </div>
                  <div class="rebuildBtn">
                    <w-button type="text" class="cxsc" @click="opendialog">
                      <img src="/src/assets/chatTheme/mzsm.svg" style="width:16px;height:16px;margin-right:3px;">
                      <span style="color:#383D47;font-size:14px;">免责声明</span>
                    </w-button>
                  </div>
                </div>
                <div v-if="appId != '20'" style="position:relative;">
                  <!-- 踩和赞 暂时隐藏 日用品商场(台州)PC-->
                                  <w-popconfirm v-if="!isup" update-at-scroll :popup-container="textRef"
										content-class="popoverClass" @popup-visible-change="popupchange" ok-text="提交"
										@ok="onSubmit(1)" placement="top" trigger="click" mini>
										<w-button type="text" shape="circle">
											<CoolThumbUpLineWe v-if="!isup" size="14" color="#828894" />
                      						<CoolThumbUpFillWe v-else size="14" color="#828894" />
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
									<CoolThumbUpLineWe v-if="!isup" size="14" color="#828894" />
									<CoolThumbUpFillWe v-else size="14" color="#828894" />
								</w-button>
								<w-popconfirm v-if="!isdown" update-at-scroll :popup-container="textRef"
										content-class="popoverClass" @popup-visible-change="popupchange" ok-text="提交"
										@ok="onSubmit(0)" :content="!isdown ? '踩' : '已踩'" placement="top" trigger="click" mini>
										<w-button type="text" shape="circle">
											<CoolThumbDownLineWe size="14" color="#828894" />
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
										<CoolThumbDownLineWe v-if="!isdown" size="14" color="#828894" />
										<CoolThumbDownFillWe v-else size="14" color="#828894" />
									</w-button>

                  <w-tooltip :content="!iscopy ? '复制' : '已复制'" placement="top" mini>
                    <w-button type="text" shape="circle" @click="handleSelect('copyText')">
                      <CoolFuzhi_9oe2ecmc v-if="!iscopy" size="14" color="#828894" />
                      <CoolCheckLineWe v-else size="14" color="#828894" />
                    </w-button>
                  </w-tooltip>
                  <!-- <w-tooltip content="播放" placement="top" mini>
                    <w-button class="bofang" type="text" shape="circle" v-if="!inversion" @click="openBofang">
                      <CoolVolumeUpLineWe size="14" color="#828894" />
                    </w-button>
                  </w-tooltip> -->
                  <w-button class="bofang" type="text" shape="circle" v-if="!inversion" @click="openBofang">
                      <CoolVolumeUpLineWe size="14" color="#828894" />
                  </w-button>
				  <transition name="el-zoom-in-bottom">
					  <div v-if="bofangVisible" class="bofang-popver">
						<div class="bofang-popver-item" @click="handleBofang('en')"><img class="bobao-img" src="/src/assets/zc/en.svg" alt=""> 英文播报</div>
						<div class="bofang-popver-item" @click="handleBofang('zh')"><img class="bobao-img" src="/src/assets/zc/zh.svg" alt=""> 中文播报</div>
				  	  </div>
				  </transition>

				  
                </div>
              </div>
            </div>
          </template>
          <!-- <template #question>
				
				</template> -->
        </TextComponent>

      </div>
      <!-- 1.智能客服（以下简称智能问答服务），旨在为广大社会公众提供咨询问题的自动快速答复服务，部分答复内容采用AI技术生成，生成式答复内容有歧义或不够准确的，请您访问来源链接中的原文，以原文内容为准。
2.本智能问答服务未能理解您的问题或答复内容不够准确时，建议您向中关村街道政务服务中心(便民服务中心)咨询。地址：海淀区知春路55号，电话：82674090。
3.您在使用本智能问答服务中遇到任何问题或建议意见，欢迎您通过答复右下角按钮反馈，我们将不断改进、完善。 -->
    </div>
    <el-dialog width="435px" class="mzsmDialog" v-model="dialogVisible">
      <div class="dialogTitle">免责声明</div>
      <p>1、智能客服（以下简称智能问答服务），旨在为广大社会公众提供咨询问题的自动快速答复服务，部分答复内容采用AI技术生成，生成式答复内容有歧义或不够准确的，请您访问来源链接中的原文，以原文内容为准。</p>
      <p>2、本智能问答服务未能理解您的问题或答复内容不够准确时，建议您向中关村街道政务服务中心(便民服务中心)咨询。地址：海淀区知春路55号，电话：82674090。</p>
      <p>3、您在使用本智能问答服务中遇到任何问题或建议意见，欢迎您通过答复右下角按钮反馈，我们将不断改进、完善。</p>
      <!-- <div v-html="getmzsm()" style="padding:0 15px;line-height:24px;font-size:16px;color:#181b49;white-space:pre-wrap;">
      </div> -->
      <!-- <div style="text-align:center;margin-top:24px;">
        <el-button type="primary" @click="closedialog">我知道了</el-button>
      </div> -->
    </el-dialog>
    <comDocPreview ref="previewRef" />
  </div>
</template>

<script setup lang="ts" name="Message">
import { setScrollPosition } from '/@/utils/other';
import { computed, ref, watch, onBeforeMount, onMounted, onUnmounted, defineAsyncComponent } from 'vue';
import AvatarComponent from './Avatar.vue';
import { useRoute } from 'vue-router';
import TextComponent from './Text.vue';
import { useChatStore } from '/@/stores/chat';
import { Session } from '/@/utils/storage';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { feedback, apiFeedback } from '/@/api/chat';
import { Message } from 'winbox-ui-next';
import { listDialogueQuestion, tjQuestion, sourceAnswer, translateText } from '/@/api/chat';
import MarkdownIt from 'markdown-it';
import mic from 'markdown-it-container';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css';
import mittBus from '/@/utils/mitt';
import { textToSpeechAndPlay, stopPlayback, isPlaying } from '/@/utils/voiceFun';
import { formatDate } from '/@/utils/formatTime';
import { getRandomConfig } from '/@/utils/ttsConfig';

import { copyText } from '/@/utils/format';
const comDocPreview = defineAsyncComponent(() => import('/@/components/comDocPreview.vue'));
const previewRef = ref(null);

const openPreview = (item: Object) => {
	previewRef.value.openPreview(item);
};

const { isMobile } = useBasicLayout();
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
	(ev: 'handleFillForm'): void;
}
const props = defineProps<Props>();
const chatStore = useChatStore();
const asRawText = ref(props.inversion);
const showSuggestOrg = ref(false);
const showPreview = ref(false);
const bofangVisible = ref(false);
const voiceText = ref('');
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

const timer = ref(null);
const textRef = ref<HTMLElement>();
const messageRef = ref<HTMLElement>();
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

onMounted(() => {
	if (!props.inversion) {
		mittBus.on('dialogueId', (appInfo) => {
			setTimeout(() => {
				appInfo.sourceShowFlag === '1' && sourceAnswerList();
        appInfo.recommendQuestionsShowFlag === '1' && tjQuestionList();
				mittBus.all.delete('dialogueId', () => {});
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
		setScrollPosition(isMobile.value ? '.message-list' : '.center-side', 'auto');
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
		if (res.data.length > 0) {
			showPreview.value = res.data.find((obj) => obj.sourceType == 3);
			if (res.data[0].sourceType == 1) {
				syList.value = res.data.filter((obj, index, self) => obj.fileLink && index === self.findIndex((t) => t.fileName === obj.fileName));
			} else {
				syList.value = res.data.filter((obj, index, self) => obj.fileLink && index === self.findIndex((t) => t.fileLink === obj.fileLink));
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
const voiceStatus = ref('stop');
const openBofang = () => {
	bofangVisible.value = !bofangVisible.value;
};
const handleBofang = (language: string) => {
	let appInfo = localStorage.getItem(`${route.params.appId}`) ? JSON.parse(localStorage.getItem(`${route.params.appId}`)) : '';

	let text = props.plainText ?? props.answer ?? '';
	text = text.replace(/:::r|:::|/g, '');
	let newText = text.split('<p style="padding: 6px 0;border-top: 1px solid rgba(0, 0, 0, 0.12);height: 1px;margin-top:12px;"/>') || [];
	// play_tts(newText?newText[0]:'');
	bofangVisible.value = false;
	if (isPlaying) {
		stopPlayback();
	} else {
		if (language == 'en') {
			// 中英文翻译
			translateTextHandler(newText ? newText[0] : '', 'zh', 'en', appInfo);
		} else {
			textToSpeechAndPlay(newText ? newText[0] : '', 15, 1, appInfo?.ttsId);
		}
	}
};
// 中英互译
const translateTextHandler = async (text, srcLang, tgtLang, appInfo) => {
	const res = await translateText({
		text,
		srcLang,
		tgtLang,
	});
	if (res?.code == '000000') {
		voiceText.value = res?.data;
		textToSpeechAndPlay(voiceText.value, 15, 1, appInfo?.ttsId);
	}
};

// 语言合成代码开始*

const btnStatus_tts = ref('UNDEFINED');
const audioPlayer = new AudioPlayer('./tts');
let APPID =  '';
let API_SECRET =  '';
let API_KEY =  '';
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
			//   btnControlFun()
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
  APPID =  tts_config.APPID;
  API_SECRET =  tts_config.API_SECRET;
  API_KEY =  tts_config.API_KEY;
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

const handleFillForm = () => {
	emit('handleFillForm');
};
</script>
<style scoped lang="scss">
@import '/@/theme/mixins/index.scss';

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
		font-weight: 500;
		font-size: 18px;
		color: #434649;
		line-height: 24px;
		padding: 17px 0 12px 29px;
		// background: linear-gradient(180deg, #C5D6FF 0%, rgba(255, 255, 255, 0) 100%);
	}

	p {
		font-size: 14px;
		color: #646479;
		line-height: 22px;
		padding: 0 29px;
		margin-bottom: 10px;
	}

	.el-button {
		width: calc(100% - 30px);
		padding: 24px 0;
		background: #3976f6 !important;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 18px;
		color: #fff !important;
		font-style: normal;
	}
}

:deep(.formDialog) {
	border-radius: 12px;
	padding: 0;

	.el-dialog__header {
		display: none;
	}

	.el-dialog__headerbtn {
		display: none;
	}
}

.fontSize16 {
	@include add-size($font-size-base16, $size);
}

.font16 {
	@include add-size($font-size-base16, $size);
}

.isPaddingLeft {
	padding-right: 36px;
}

.isPaddingLeftMobile {
	padding-right: 2%;
}

.isPaddingRight {
	padding-left: 36px;
	padding-right: initial;
}

.isPaddingRightMobile {
	padding-left: 2%;
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

.btns {
	display: flex;
	align-items: flex-end;
	justify-content: space-between;
	margin-top: 6px;

	.left {
		display: flex;
	}

	.cool-icon {
		// color: #3DACA8;
	}

	.w-btn {
		width: 24px;
		height: 24px;

		// background: #fff;
		margin-left: 10px;
		// box-shadow: 0px 6px 20px 0px rgba(30,64,175,0.1);
	}

	.w-btn:hover {
		color: #3daca8;
	}

	.rebuildBtn {
		@include add-size($font-size-base14, $size);
		// color: #3DACA8;
		height: 24px;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 12px;
		margin-left: 0;
		cursor: pointer;

		.w-btn {
			margin-right: 40px;
		}

		:deep(.w-btn-icon) {
			margin-right: 3px;
		}

		.cxsc {
			width: 75px;
			margin: 0;
			padding: 0;
			margin-right: 14px;
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

.flex-row {
	:deep(.w-trigger-popup) {
		// z-index: 1 !important;
	}
}
.relate-source {
	color: #646479;
	font-size: 14px;

	.title {
		font-weight: 600;
		font-size: 14px;
	}
	ul {
		margin-top: 10px;
		padding: 20px;
		background: #f4f6f9;
		border-radius: 4px;

		li {
			cursor: pointer;

			&:hover {
				color: #181b49;
			}
		}
	}
}

.bofang {
	position: relative;
}
.bofang-popver {
	position: absolute;
	top: -76px;
	right: -14px;
	width: 110px;
	height: 72px;
	background: #fff;
	padding: 4px;
	box-shadow: 0px 4px 6px 0px rgba(0, 0, 0, 0.1);
	border-radius: 8px;

	&-item {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 32px;
		border-radius: 4px;
		cursor: pointer;
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 14px;
		color: #383d47;
		&:hover {
			background: rgba(180, 188, 204, 0.2);
		}
		.bobao-img {
			margin-right: 6px;
			width: 15px;
			height: 15px;
		}
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
		// color: #3DACA8;

		.rebuildBtn {
			// color: #3DACA8 !important;
			margin-right: 16px;

			.cool-icon {
				// color: #3DACA8;
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

	.public-option {
		display: flex;
		align-items: center;
	}

	.w-radio-group {
		display: flex;
		align-items: center;
		margin-left: 8px;
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