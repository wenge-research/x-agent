<template>
	<div class="outer">
		<div class="header">
			<div class="headContent" @click="homeClick">
                <div class="click-event" @click.stop="eventClick">
                </div>
				<img style="height: 350px" src="/src/assets/chongqing/bianzu1.png" class="chongqing-img" />
				<img style="height: calc(100vh - 350px)" src="/src/assets/chongqing/xiaoyan.png" class="chongqing-img1" />
			</div>
		</div>
	</div>
</template>
<script lang="ts" setup>
import { serviceList } from '/@/api/chat/index';
import { ref, onBeforeMount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
const router = useRouter();
const route = useRoute();
onBeforeMount(() => {
	getServiceList();
});
const serviceListData = ref([]);
const getServiceList = () => {
	serviceList({
		status: 1,
		pageNo: 1,
		pageSize: 1000,
    serviceType: '便民服务',
		applicationId: getAppDetail()?.applicationId,
	}).then((res) => {
		if (res.code == '000000') {
			serviceListData.value = res.data?.records;
		} else {
			serviceListData.value = [];
		}
	});
};
const toBlankPage = (item) => {
	window.location.href = item.menuUrl;
	// const hashPart = item.menuUrl.split('#')[1] || '';
	// const queryPart = hashPart.split('?')[1] || '';
	// const queryParams = new URLSearchParams(queryPart);
	// const appCode = queryParams.get('appCode');
	// const matterId = queryParams.get('matterId');
	// console.log(appCode, matterId)
	// router.push({ name: 'information', params: { appId: appCode, matterId: matterId } });
};
const startChat = () => {
	router.push(`/chat/${getAppDetail()?.applicationCode}/`);
};
const goTopersonalCenter = () => {
	console.log(getAppDetail()?.applicationCode, 9999)
	router.push(`/personalCenter/${getAppDetail()?.applicationCode}`);
};
const getAppDetail = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	console.log(appInfo, 10000)
	return appInfo ? appInfo : '';
};
const eventClick = () => {
    router.push(`/policyHelp-cq/${getAppDetail()?.applicationCode}`);
}
const homeClick = () => {
    router.push(`/twoCitiesPlam-cq/${getAppDetail()?.applicationCode}`);
}
</script>
<style lang="scss" scoped>
.outer {
	width: 100%;
	height: 100%;
    .headContent {
        position: relative;
    }
    .click-event {
        width: 50px;
        height: 70px;
        background: transparent;
        position: absolute;
        top: 90px;
        left: 23px;
    }
    .chongqing-img {
        width: 100%;
    }
    .chongqing-img1 {
        width: 100%;
    }
}
</style>
