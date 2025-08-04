<template>
	<div class="information-view" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		<div class="info-box">
			<div class="title-box">
				<div class="close">
					<van-icon name="arrow-left" color="#fff" @click="back" />
				</div>
				<div class="title">个人中心</div>
				<div class="back-gateway" @click="backGateway">
					<img style="width: 18px; height: 18px; margin-right: 4px" src="/src/assets/homePage/function-line.svg" />返回门户
				</div>
			</div>
			<!-- 用户信息 -->
			<div class="user-information" v-if="userInfo.username || userInfo.phone">
				<img style="width: 56px; height: 56px; margin-right: 16px" src="/src/assets/homePage/zhushoutouxiang.png" />
				<div>
					<div class="name">
						{{ userInfo.username }}
					</div>
					<div class="phone">
						{{ userInfo.phone }}
					</div>
				</div>
			</div>
		</div>
		<div class="person-bottom">
			<div class="basic-info flex">
				<div class="flex">
					<img style="width: 40px; height: 40px; margin-right: 16px" src="/src/assets/homePage/basicInfo.png" />
					<div class="name">基本信息</div>
				</div>

				<div class="flex" @click="basicInfo">
					<img style="width: 18px; height: 18px; margin-right: 4px" src="/src/assets/homePage/free.png" />
					<div class="certified">已认证</div>
					<img style="width: 20px; height: 20px; margin-left: 16px" src="/src/assets/homePage/arrow-right-s-line.png" />
				</div>
			</div>
			<!-- 历史对话 -->
			<div v-loading="loading">
				<div class="history-dialogue">历史对话</div>
				<div v-if="dialogueList.length">
					<div v-for="(item, index) in dialogueList" :key="index" class="dialogueList" @click="chatClick(item)">
						<div class="name">{{ item.question }}</div>
						<div class="time flex flex-just">
							<div>{{ item.createTime }}</div>
							<div style="width: 40px; height: 40px; display: flex; align-items: center; justify-content: center" @click.stop="recordDelete(item)">
								<img class="chatImg" :src="chatLine" />
							</div>
						</div>
					</div>
				</div>
				<w-empty v-else>
					<template #image>
						<img class="nodata" :src="noDataImg" alt="" />
					</template>
					<div class="noName">暂无数据</div>
				</w-empty>
			</div>
		</div>
	</div>
</template>
<script lang="ts" setup>
import { useRouter, useRoute } from 'vue-router';
import { useChatStore } from '/@/stores/chat';
import { Message } from 'winbox-ui-next';
import { ref, onMounted } from 'vue';
import { fillInPersonal, getUserDetail } from '/@/api/manage/index';
import { recordGetRecord, recordLogicDelete } from '/@/api/chat';
import noDataImg from '/@/assets/chat/nocategory.svg';
import chatLine from '/@/assets/ai/delete-bin-4-line.svg';
const router = useRouter();
const route = useRoute();
const userInfo = ref({});
const chatStore = useChatStore();
const dialogueList = ref([]);
onMounted(() => {
	getUserDetailFun();
	recordGetRecordList();
});
// 返回
const back = () => {
	router.back();
};
const basicInfo = () => {
	router.push(`/personalCenter/${getAppDetail()?.applicationCode}`);
};
// 返回首页
const backHome = () => {
	// router.push('/previewChat/zgc');
	router.push(`/previewChat/${getAppDetail()?.applicationCode}`);
};
const backGateway = () => {
	if (!userInfo.value?.userType || userInfo.value?.userType == 'resident') {
		// 居民
		router.push(`/previewChat/${getAppDetail()?.applicationCode}`);
	} else {
		router.push(`/homePage/${getAppDetail()?.applicationCode}`);
	}
};
const getUserDetailFun = async () => {
	try {
		const res = await getUserDetail({
			userId: sessionStorage.getItem('userId'),
		});
		userInfo.value = res.data;
		sessionStorage.setItem('userInfo', JSON.stringify(userInfo.value));
	} catch (err) {
		throw new Error(err);
	}
};
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo : '';
};
const touchStartX = ref(0);
const handleTouchStart = (event) => {
	touchStartX.value = event.touches[0].clientX;
};

const handleTouchEnd = (event) => {
	const touchEndX = event.changedTouches[0].clientX;
	const deltaX = touchEndX - touchStartX.value;

	// 如果从左向右滑动超过一定距离，返回上一页
	if (deltaX > 50) {
		var ua = navigator.userAgent.toLowerCase();
		// 判断是否在微信浏览器内
		if (ua.match(/MicroMessenger/i) == 'micromessenger') {
			wx.miniProgram.getEnv((res) => {
				if (res.miniprogram) {
					wx.miniProgram.navigateBack();
				} else {
					console.log('不在小程序内');
					if (getAppDetail()?.mobileTemplateRoute == 'assistantMobile') {
						router.push(`/assistantHome/${getAppDetail()?.applicationCode}`);
					} else {
						router.push(`/previewChat/${getAppDetail()?.applicationCode}`);
					}
				}
			});
		} else {
			console.log('不在微信浏览器内');
			if (getAppDetail()?.mobileTemplateRoute == 'assistantMobile') {
				router.push(`/assistantHome/${getAppDetail()?.applicationCode}`);
			} else {
				router.push(`/previewChat/${getAppDetail()?.applicationCode}`);
			}
		}
	}
};
const loading = ref(false);
const recordGetRecordList = async () => {
	loading.value = true;
	let res = await recordGetRecord({
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
		pageNo: 1,
		pageSize: 999,
		verifyDeptId: '',
		verifyStatus: '',
		text: '',
		conversationId: '',
		deleted: 0,
		userId: sessionStorage.getItem('userId') || '',
	});
	dialogueList.value = Object.values(
		res.data.list.reduce((acc, current) => {
			if (!acc[current.conversationId] || new Date(acc[current.conversationId].createTime).getTime() > new Date(current.createTime).getTime()) {
				acc[current.conversationId] = current;
			}
			return acc;
		}, {})
	);
	dialogueList.value.sort((a, b) => Number(b.conversationId) - Number(a.conversationId));
	loading.value = false;
};

const chatClick = (item: any) => {
	router.push(`/chat/${getAppDetail()?.applicationCode}`);
	setTimeout(() => {
		chatStore.getChatRecordsList(item.conversationId);
	}, 1000);
};

const recordDelete = async (item: any) => {
	let res = await recordLogicDelete({
		applicationId: localStorage.getItem(`${route.params.appId}appId`),
		dialogueId: item.dialogueId,
	});
	if (res.code == '000000') {
		setTimeout(() => {
			recordGetRecordList();
		}, 1010);
	}
};
</script>

<style lang="scss" scoped>
.information-view {
	width: 100%;
	height: 100vh;
	position: fixed;
	left: 0;
	top: 0;

	.info-box {
		width: 100%;
		height: 150px;
		background: url('/@/assets/homePage/bg-2.png') no-repeat;
		background-size: 100% 100%;

		.title-box {
			width: 100%;
			height: 48px;
			padding: 0 20px;
			display: flex;
			align-items: center;
			justify-content: space-between;
			position: relative;

			.title {
				font-weight: 500;
				font-size: 18px;
				font-weight: bold;
				color: #ffffff;
				position: absolute;
				left: 50%;
				transform: translateX(-50%);
			}

			.close {
				width: 20px;
				height: 20px;
			}
			.back-gateway {
				display: flex;
				align-items: center;
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 16px;
				color: #ffffff;
				line-height: 20px;
			}
		}
		.user-information {
			padding: 10px 20px;
			display: flex;
			align-items: center;
			.name {
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 20px;
				color: #ffffff;
				line-height: 28px;
			}
			.phone {
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 16px;
				color: #ffffff;
				line-height: 20px;
			}
		}
	}

	.person-bottom {
		width: 100%;
		height: calc(100vh - 140px);
		overflow-y: auto;
		background: #ffffff;
		border-radius: 12px 12px 0px 0px;
		position: absolute;
		bottom: 0;
		padding: 16px;
		.basic-info {
			width: 100%;
			background: linear-gradient(270deg, rgba(191, 255, 253, 0.19) 0%, rgba(23, 166, 164, 0.19) 100%);
			border-radius: 8px;
			padding: 16px 15px;
			justify-content: space-between;
			.name {
				font-family: MiSans, MiSans;
				font-weight: 500;
				font-size: 16px;
				color: #181b49;
				line-height: 24px;
			}
			.certified {
				font-family: MiSans, MiSans;
				font-weight: 400;
				font-size: 14px;
				color: #11b658;
				line-height: 18px;
			}
		}
	}
}

.flex {
	display: flex;
	align-items: center;
}
.flex-just {
	justify-content: space-between;
}
.dialogueList {
	background: #f4f6f9;
	padding: 12px;
	border-radius: 8px;
	margin-bottom: 8px;
	cursor: pointer;
	.time {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 14px;
		color: #b4bccc;

		.chatImg {
			width: 15px;
		}
	}
	.name {
		font-family: MiSans, MiSans;
		font-weight: 400;
		font-size: 16px;
		color: #383d47;
		line-height: 24px;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
		transition: max-height 0.3s ease-in-out; /* 平滑过渡效果 */
		margin-bottom: 10px;
	}
}

.history-dialogue {
	font-family: MiSans, MiSans;
	font-weight: 600;
	font-size: 16px;
	color: #434649;
	line-height: 22px;
	margin: 26px 0 10px;
}

.nodata {
	margin: 100px auto 0;
	height: 150px;
}

.noName {
	font-size: 18px;
}
</style>
