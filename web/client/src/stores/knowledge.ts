import { defineStore } from 'pinia';
import { router } from '/@/router';
import { h } from 'vue';
import { Session } from '/@/utils/storage';
import { getPersonagePrice } from '/@/api/personal'
import { listDialogues, dialogue, listFile, listConversations, updateConversation, deleteConversation, addConversation,closeDialogueConn,feedback } from '/@/api/chat';

import useEventSource from '/@/hooks/useEventSource';
import { Message,Notification,Modal } from 'winbox-ui-next'
const setLocalState = (state: knowledge.knowledgeState) => {
	Session.set('chatStorage', state);
};

export const useKnowledgeState = defineStore('knowledge-store', {
	state: (): knowledge.knowledgeState => ({
		moving: false,
		dataItem: {},
		addEditModal: {
			show: false,
			type: 1,
			callback: null,
		},
		fileList: {},
		fileActive: null,
		currentLibrary:{},
		fileUpdate: {
			accept: '.doc, .docx, .pdf, .txt',
			suffixArr: ['doc', 'docx', 'pdf', 'txt'],
			list: [],
			status: false,
			isGetTree: false
		},
		previewData: {
			currItem: {},
			active: '',
			params: {}
		},
		dblclickName: '',
		textFileIds: [],
		citationText: '',
		knowledgesSize: {},
		param1:""
	}),
	getters: {
		// getChatHistoryByCurrentActive(state: Chat.ChatState) {
		// 	const index = state.history.findIndex((item) => item.id === state.active);
		// 	if (index !== -1) return state.history[index];
		// 	return null;
		// },
		// getChatByid(state: Chat.ChatState) {
		// 	return (id?: number) => {
		// 		if (id) return state.chat.find((item) => item.id === id)?.data ?? [];
		// 		return state.chat.find((item) => item.id === state.active)?.data ?? [];
		// 	};
		// },
	},
	actions: {
		setparam1(val:string){
		  this.param1 = val
		},
		setMoving(val:any){
			this.moving = val;
		},
		setTextFileIds(val:any){
			this.textFileIds = val || []
		},
		setCurrentLibrary(item:any){
			this.currentLibrary = item;
		},
		resetCurrentLibrary(){
			this.currentLibrary = {};
		},
		setFileList(data:any){
			if(data instanceof Array && this.fileList?.id){
				this.fileList.child = data;
			}else{
				this.fileList = data
			}
		},
		resetFileList(){
			this.fileList = {};
		},
		modelOpen(type: number,item:any|null=null) {
			this.addEditModal.show = true;
			this.addEditModal.type = type;
			if(item){
				this.dataItem = item
			}
		},
		modelClose() {
			this.addEditModal.show = false;
			this.addEditModal.type = 1;
			this.dataItem = {}
		},
		setModeCallback(callback:Function|null=null){
			this.addEditModal.callback = callback;
		},
		setFileUpdate(params: any) {
			for (let i in params) {
				this.fileUpdate[i] = params[i];
			}
		},
		setPreviewData(params: any) {
			for (let i in params) {
				this.previewData[i] = params[i];
			}
		},
		setDblclickName(val:string) {
			this.dblclickName = val || ''
		},
		restoreData() {
			this.fileUpdate['status'] = false;
			this.addEditModal.show = false;
			this.previewData = {
				currItem: {},
				active: '',
				params: {}
			}
		},
		setCitationText(val:string) {
			this.citationText = val
		},
		setKnowledgesSize(val:any){
			this.knowledgesSize = val;
		},
		recordState() {
			setLocalState(this.$state);
		},
	},
});
