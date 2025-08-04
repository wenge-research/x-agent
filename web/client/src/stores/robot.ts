import { defineStore } from 'pinia';
import { useChatStore } from './chat';
import { Session } from '/@/utils/storage';

export const useRobotStore = defineStore('robot-store', {
	state: (): Robot.RobotState => ({
		getresult: false,
		delayFlag: false,
		chatInterVal: null,
		player: null,
		videoDom: null,
		notShowIndividual: true,
		muted: true,
		personSize: [440,782],
		personPos: [],
		fromUser: '1',
		referX: 1920,
		referY: 1080,
		keyEve: false,
	}),
	actions: {
		breakChat(){
			this.sendtext("break", "break")
		},
		sendtext(msg, type){
			let param = {
				msgId: Math.round(Math.random() * 1000000000000000000).toString(36),
				toUser: Session.get('userId'),
				fromUser: this.fromUser,
				msgType: "text",
				createTime: Number(new Date()),
				content: msg.trim(),
			};
			if (type || msg == 'break') {
				param.systemflag = true;
			}
			if(this.ws){
				if(readyState == 1){
					//连接状态正常的时候才发送消息 如果发送消息的时候 需要重新计算倒计时
					// this.countDown()
					this.ws.sendSock(JSON.stringify(param))
				}else{
					console.log('长时间未使用连接已断开，请重新加载！')
				}
			}
			return false;
		},
		countDown(){
			//倒计时
			clearInterval(this.chatInterVal);
			let time = process.env.NODE_ENV == "development" ? 600 : 1 * 60 * 60; //暂定时间是1小时
			this.chatInterVal = setInterval(() => {
			// console.log(time, 'time')
			if (time <= 0) {
				clearInterval(this.chatInterVal);
				// 断开连接
				this.ws.closeWebSocket();
			}
			--time;
			}, 1000);
		},
		sendMsg(msg, type){
			useChatStore().isOpen = true
			useChatStore().rightFlag = 2
			//this.breakChat()
			this.sendtext(msg, type)
		}
	},
});
