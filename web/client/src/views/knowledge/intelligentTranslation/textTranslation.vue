<template>
	<div class="text-translation">
		<div class="left">
			<el-tabs v-model="leftTabs" @tab-click="leftTabsClick">
				<el-tab-pane label="检测语言" name="auto" key="auto"></el-tab-pane>
				<el-tab-pane label="繁体中文" name="zh-tw" key="zh-tw"></el-tab-pane>
				<el-tab-pane label="简体中文" name="zh" key="zh"></el-tab-pane>
				<el-tab-pane label="英语" name="en" key="en"></el-tab-pane>
			</el-tabs>
			<div class="left-input">
				<el-input
					v-model="leftValue"
					type="textarea"
					maxlength="2000"
					show-word-limit
					:autosize="{ minRows: 15, maxRows: 20 }"
					placeholder="请输入"
					@keydown.enter="pressEnterKey"
					@input="inputChange"
					clearable
				/>
				<div class="left-annex">
					<div class="close">
						<!-- <iconpark-icon name="mic-fill" size="16" color="#B4BCCC" style="margin-right: 18px" @click="openYY('ly')"></iconpark-icon> -->
						<Voice />
						<iconpark-icon
							name="volume-up-fill"
							size="16"
							:color="leftVolumeColor"
							@click="handleBofang('leftValue', 'leftVolumeColor')"
						></iconpark-icon>
					</div>

					<iconpark-icon v-if="leftValue" name="close-circle-fill" size="16" color="#B4BCCC" @click="clearLeftValue"></iconpark-icon>
				</div>
			</div>
		</div>
		<div class="center">
			<img @click="switchingLeftAndRight" src="/src/assets/intelligentTranslation/change-icon.png" alt="" />
		</div>
		<div class="right">
			<el-tabs v-model="rightTabs" @tab-click="rightTabsClick">
				<el-tab-pane label="繁体中文" name="zh-tw"></el-tab-pane>
				<el-tab-pane label="简体中文" name="zh"></el-tab-pane>
				<el-tab-pane label="英语" name="en"></el-tab-pane>
			</el-tabs>
			<div class="right-input">
				<el-input v-model="rightValue" type="textarea" maxlength="2000" show-word-limit :autosize="{ minRows: 15, maxRows: 20 }" :disabled="true" />
				<div class="right-annex">
					<iconpark-icon
						name="volume-up-fill"
						size="16"
						:color="rightVolumeColor"
						@click="handleBofang('rightValue', 'rightVolumeColor')"
					></iconpark-icon>
					<div class="copy">
						<iconpark-icon v-if="rightValue" name="file-copy-line" size="16" color="#828894" title="复制" @click="copy"></iconpark-icon>
						<div class="right-value-length">{{ rightValueLength }} / 2000</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import axios from 'axios';
import { Message } from 'winbox-ui-next';
import { apiTranslateTextOrFile } from '/@/api/knowledge';
import { copyText } from '/@/utils/format';
import { textToSpeechAndPlay, stopPlayback, isPlaying } from '/@/utils/voiceFun';
import { useChatStore } from '/@/stores/chat';
import { useKnowledgeState } from '/@/stores/knowledge';
import Voice from './voice.vue';
import useEventSource from '/@/hooks/useEventSource';
import { setScrollPosition } from '/@/utils/other';

export default {
	components: { Voice },
	data() {
		return {
			clientId: '',
			leftTabs: 'auto',
			leftValue: '',
			rightTabs: 'zh-tw',
			rightValue: '',
			recorder: new RecorderManager('./iat'),
			APPID: 'xxxxxxx',
			API_SECRET: 'xxxxxxx',
			API_KEY: 'ddc4e7ce8e45298cd90d5a6a956f2dc8',
			iatWS: null,
			btnStatus: 'UNDEFINED',
			resultVal: '',
			text: '',
			resultText: '',
			resultTextTemp: '',
			insertpromptbox: null,
			isInsertPrompt: false,
			textFileIds: [],
			textFolderIds: [],
			instructObj: {},
			insertpromptText: '',
			chatStore: useChatStore(),
			knowledgeState: useKnowledgeState(),
			countdownInterval: null,
			chatSBFlag: false,
			chatFlag: false,
			leftVolumeColor: '#828894',
			rightVolumeColor: '#828894',
			timerId: null,
		};
	},
  mounted() {
    this.clientId = Math.round(Math.random() * 1000000000000000000).toString(36)
  },
	computed: {
		rightValueLength() {
			return this.rightValue.length;
		},
	},
	methods: {
		leftTabsClick(tab) {
			this.leftTabs = tab.paneName;
			if (this.rightValue) {
				this.translateTextOrFile();
			}
		},
		rightTabsClick(tab) {
			this.rightTabs = tab.paneName;
			this.rightValue = '';
			this.translateTextOrFile();
		},
		// 翻译
		async translateTextOrFile() {
			// let formData = new FormData();
			// formData.append('text', this.leftValue);
			// formData.append('srcLang', this.leftTabs);
			// formData.append('tgtLang', this.rightTabs);
			// formData.append('translateType', 'text');
			// try {
			// 	const res = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/intelligentTranslation/translateTextOrFile`, formData, {
			// 		headers: {
			// 			'Content-Type': 'audio/wave;multipart/form-data',
			// 		},
			// 	});
			// 	if (res.data?.code == '000000') {
			// 		this.rightValue = res.data?.data || '';
			// 	}
			// } catch (error) {
			// 	console.error('Error :', error);
      // }

			
			// const res = await apiTranslateTextOrFile(params);
			// if (res.code == '000000') {
			// 	this.rightValue = res.data || '';
			// }
			
			if(!this.leftValue) {
				return;
			}
			const clientId = Math.round(Math.random() * 1000000000000000000).toString(36);
			const params = {
				text: this.leftValue,
				srcLang: this.leftTabs,
				tgtLang: this.rightTabs,
				translateType: 'text', //text-文本  file-文件
        clientId,
			};
			const abortController = new AbortController();
			let url = import.meta.env.VITE_API_URL + import.meta.env.VITE_STREEM_PATH + import.meta.env.VITE_BASE_API_URL + '/intelligentTranslation/executeTranslateTextWG'; 
			useEventSource(
				url,
				params,
				abortController,
				(list) => {
					console.log("list", list)
					this.rightValue = JSON.parse(list) ? JSON.parse(list)?.context : '';
					if(this.rightValue) {
						setScrollPosition('.markdown-body', 'auto');
					}
				}
			);
		},
		pressEnterKey() {
			this.translateTextOrFile();
		},
		debounce(func, delay) {
			let timer;
			return function () {
				const context = this;
				const args = arguments;
				clearTimeout(timer);
				timer = setTimeout(function () {
					func.apply(context, args);
				}, delay);
			};
		},
		inputChange() {
			let that = this;
			clearTimeout(this.timerId);
			this.timerId = setTimeout(function () {
				that.translateTextOrFile();
			}, 1000);
		},
		// 复制
		copy() {
			copyText({ text: this.rightValue });
			Message.success('复制成功');
		},
		// 左侧清空value
		clearLeftValue() {
			this.leftValue = '';
			this.rightValue = '';
		},
		// 播放语音
		async handleBofang(value, bofangName) {
			this[bofangName] = '#1C50FD';
			let text = this[value] || '';
			// text = text.replace(/:::r|:::|/g, '');
			if (isPlaying) {
				stopPlayback();
				this[bofangName] = '#828894';
			} else {
				let appInfo = localStorage.getItem(`${this.$route.params.appId}`) ? JSON.parse(localStorage.getItem(`${this.$route.params.appId}`)) : '';
				await textToSpeechAndPlay(text, 15, 1, appInfo?.ttsId);
				setTimeout(() => {
					this[bofangName] = '#828894';
				}, 1000);
			}
		},
		openYY(type) {
			// this.btnControlFun();
			this.connectWebSocket(type);
		},
		// 录音相关开始
		btnControlFun(type) {
			if (this.btnStatus === 'UNDEFINED' || this.btnStatus === 'CLOSED') {
				this.connectWebSocket(type);
			} else {
				// 结束录音
				this.recorder.stop();
			}
		},
		connectWebSocket(type) {
			// this.text = this.text || '介绍下深圳';
			// console.log(this.text);
			// if(this.text){
			// 	setDialogueData(this.text)
			// }
			// return

			const websocketUrl = this.getWebSocketUrl();
			if ('WebSocket' in window) {
				this.iatWS = new WebSocket(websocketUrl);
			} else if ('MozWebSocket' in window) {
				this.iatWS = new MozWebSocket(websocketUrl);
			} else {
				alert('浏览器不支持WebSocket');
				return;
			}
			this.changeBtnStatus('CONNECTING');
			this.iatWS.onopen = (e) => {
				// 开始录音
				if (type == 'toChat') {
					this.chatFlag = true;
				}
				this.recorder.start({
					sampleRate: 16000,
					frameSize: 1280,
				});
				var params = {
					common: {
						app_id: this.APPID,
					},
					business: {
						language: 'zh_cn',
						domain: 'iat',
						accent: 'mandarin',
						vad_eos: 2400000,
						dwa: 'wpgs',
					},
					data: {
						status: 0,
						format: 'audio/L16;rate=16000',
						encoding: 'raw',
					},
				};
				this.iatWS.send(JSON.stringify(params));
				this.changeBtnStatus('OPEN');
			};
			this.iatWS.onmessage = (e) => {
				this.chatSBFlag = true;
				this.renderResult(e.data);
			};
			this.iatWS.onerror = (e) => {
				ElMessage.error('录音异常，请检查是否插入麦克风设备及开启麦克风权限');
				this.recorder.stop();
				this.changeBtnStatus('CLOSED');
			};
			this.iatWS.onclose = (e) => {
				this.recorder.stop();
				this.changeBtnStatus('CLOSED');
			};
		},
		getWebSocketUrl() {
			// 请求地址根据语种不同变化
			var url = 'wss://iat-api.xfyun.cn/v2/iat';
			var host = 'iat-api.xfyun.cn';
			var apiKey = this.API_KEY;
			var apiSecret = this.API_SECRET;
			var date = new Date().toGMTString();
			var algorithm = 'hmac-sha256';
			var headers = 'host date request-line';
			var signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v2/iat HTTP/1.1`;
			var signatureSha = CryptoJS.HmacSHA256(signatureOrigin, apiSecret);
			var signature = CryptoJS.enc.Base64.stringify(signatureSha);
			var authorizationOrigin = `api_key="${apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
			var authorization = btoa(authorizationOrigin);
			url = `${url}?authorization=${authorization}&date=${date}&host=${host}`;
			return url;
		},
		changeBtnStatus(status) {
			this.btnStatus = status;
			if (status === 'CONNECTING') {
				this.resultVal = '建立连接中';
				this.text = '';
				this.resultText = '';
				this.resultTextTemp = '';
			} else if (status === 'OPEN') {
				this.countdown();
			} else if (status === 'CLOSING') {
				this.resultVal = '关闭连接中';
			} else if (status === 'CLOSED') {
				this.text = '';
				this.resultVal = '开始录音';
			}
		},
		countdown() {
			let seconds = 60;
			this.countdownInterval = setInterval(() => {
				seconds = seconds - 1;
				if (seconds <= 0) {
					clearInterval(this.countdownInterval);
					this.recorder.stop();
				} else {
					this.resultVal = `录音中（${seconds}s）`;
				}
			}, 1000);
		},
		renderResult(resultData) {
			console.log('识别结束识别结束', resultData);
			this.recorder.stop();
			this.changeBtnStatus('CLOSED');
			// 识别结束
			let jsonData = JSON.parse(resultData);
			if (jsonData.data && jsonData.data.result) {
				let data = jsonData.data.result;
				let str = '';
				let ws = data.ws;
				for (let i = 0; i < ws.length; i++) {
					str = str + ws[i].cw[0].w;
				}
				// 开启wpgs会有此字段(前提：在控制台开通动态修正功能)
				// 取值为 "apd"时表示该片结果是追加到前面的最终结果；取值为"rpl" 时表示替换前面的部分结果，替换范围为rg字段
				if (data.pgs) {
					if (data.pgs === 'apd') {
						// 将resultTextTemp同步给resultText
						this.resultText = this.resultTextTemp;
					}
					// 将结果存储在resultTextTemp中
					this.resultTextTemp = this.resultText + str;
				} else {
					this.resultText = this.resultText + str;
				}
				this.text = this.resultTextTemp || this.resultText || '';
			}
			// this.text = this.text || 'xxxxxxx副区长是谁';
			this.text = this.text.trim();
			console.log(this.text);
			if (this.text) {
				// setDialogueData(this.text)
				this.setInput();
			} else {
				// ElMessage.error('没有声音输入，将退出沉浸式问答！')
				// quitYY()
				this.chatSBFlag = false;
			}
			if (jsonData.code === 0 && jsonData.data.status === 2) {
				this.iatWS.close();
			}
			if (jsonData.code !== 0) {
				this.iatWS.close();
			}
		},
		async setInput() {
			if (this.chatStore.dialogueLoading) {
				return;
			}
			let str = this.text;
			if (this.isInsertPrompt) {
				let ids = await getInsertHtmlId();
				// knowledgeState.setTextFileIds(ids);
				this.textFileIds = ids.fileIds || [];
				this.textFolderIds = ids.folderIds || [];
				if (ids.skillId) {
					this.instructObj.id = ids.skillId;
				}
				str = this.insertpromptText;
			}
			if (this.instructObj?.name) {
				str = this.instructObj?.name + str;
			}
			console.log(str.trim(), 'str.trim()');
			this.setDialogueData(str.trim());
			this.text = '';
			cleatInput();
		},
		cleatInput() {
			this.insertpromptbox && (this.insertpromptbox.innerHTML = '');
			this.isInsertPrompt = false;
			this.insertpromptText = '';
			this.text = '';
		},
		//录音相关结束

		async setDialogueData(text, params) {
			let cvImgUrl = this.chatStore.fileUpdateCV.url;
			if (text == '' && cvImgUrl == '') {
				clearQuery();
				return;
			}

			if (!route.params.conversationId || route.params.conversationId == '' || this.chatStore.isSensitive) {
				this.chatStore.isSensitive = false;
				await this.chatStore.addHistory({ appId: route.params.appId }, { name: text });
			}
			let arr = {};
			if (params) {
				param.value.forEach((item) => {
					Object.keys(params).forEach((param) => {
						if (item.key == param) {
							item.defaultValue = params[param];
						}
					});
				});
			}
			this.chatStore.dialogueParamsList = param.value;

			param.value.forEach((item) => {
				arr[item.key] = item.defaultValue;
			});
			arr.promptId = promptId.value == '' ? route.params.appId : promptId.value;
			const data = {
				appId: promptId.value == '' ? route.params.appId : promptId.value,
				content: text,
				templateId: selectedTemplateId.value,
				templateName: selectedTemplateName.value,
				conversationId: route.params.conversationId,
				param: JSON.stringify(arr),
			};
			if (!isChat.value) {
				data['dialogueFileIds'] = textFileIds.value;
				data['dialogueFolderIds'] = textFolderIds.value;
				if (instructObj.value?.id) {
					data['skillId'] = instructObj.value.id;
				}
				if (citationText.value) {
					data['paragraph'] = citationText.value;
				}
			} else {
				if (this.chatStore.categoryId != '-1') {
					data['categoryId'] = this.chatStore.categoryId;
				}
				if (cvImgUrl != '') {
					data['imgUrl'] = [cvImgUrl];
				}
			}
			this.chatStore.fileUpdateCV.url = '';
			await this.chatStore.setChatList(data);
			promptId.value = '';
			this.clearQuery();
			this.instructSet('');
		},
		clearQuery() {
			if (this.$route.query.q || this.$route.query.q == '') {
				let url = window.location.href.split('?')[0];
				window.history.pushState({}, 0, url);
			}
		},
		instructSet(val) {
			this.knowledgeState.setCitationText(val);
			this.instructObj = {};
		},
		// 左右语言切换
		switchingLeftAndRight() {
			if (this.leftTabs == 'auto') return;
			let leftVal = this.leftTabs;
			let rightVal = this.rightTabs;
			this.leftTabs = rightVal;
			this.rightTabs = leftVal;
			this.translateTextOrFile();
		},
	},
};
</script>

<style lang="scss" scoped>
.text-translation {
	display: flex;
	width: 100%;
	height: 100%;
	.left {
		width: 49.5%;
		&-input {
			position: relative;
			width: 100%;
		}
		&-annex {
			display: flex;
			align-items: center;
			justify-content: space-between;
			position: absolute;
			width: 86%;
			bottom: 12px;
			left: 24px;

			.close {
				display: flex;
				align-items: center;
			}
		}
	}
	.center {
		width: 32px;
		height: 48px;
		display: flex;
		justify-content: center;
		align-items: center;

		img {
			width: 24px;
			height: 24px;
			cursor: pointer;
		}
	}
	.right {
		width: 49.5%;
		&-input {
			position: relative;
			width: 100%;
		}
		&-annex {
			width: 96%;
			display: flex;
			align-items: center;
			justify-content: space-between;
			position: absolute;
			bottom: 10px;
			left: 24px;
			z-index: 2;
			margin-right: 16px;
			cursor: pointer;

			.copy {
				display: flex;
				align-items: center;

				.right-value-length {
					margin-left: 14px;
					font-family: MiSans, MiSans;
					font-weight: 400;
					font-size: 14px;
					color: #b4bccc;
				}
			}
		}
	}
	iconpark-icon {
		cursor: pointer;
	}
	::v-deep(.el-tabs__header) {
		margin-bottom: 0;
		padding: 0 16px;
	}
	::v-deep(.el-tabs__nav-wrap:after) {
		display: none;
	}
	::v-deep(.el-tabs__item) {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 18px;
		color: #383d47;
		padding: 0 24px;
		height: 48px;
	}
	::v-deep(.el-tabs__item.is-active, .el-tabs__item:hover) {
		font-weight: 600;
	}
	::v-deep(.el-tabs__active-bar) {
		background: #1c50fd;
	}
	::v-deep(.el-textarea__inner) {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 24px;
		color: #b4bccc;
		line-height: 28px;
		padding: 12px 16px 6px;
		border-radius: 8px;
	}
	::v-deep(.el-textarea .el-input__count) {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 14px;
		color: #b4bccc;
		bottom: 12px;
	}
}
</style>