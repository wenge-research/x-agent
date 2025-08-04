<template>
  <div class="outer" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
    <div class="header">
      <div class="headContent">
        <img
          src="/src/assets/assistantH5/kefuLogo.png"
          style="width: 140px; height: 36px"
        />
        <!-- <img
          @click="goTopersonalCenter"
          src="/src/assets/chatTheme/useraccount.svg"
          style="width: 20px; height: 20px"
        /> -->
        <iconpark-icon name="account-circle-line" color="#494C4F" size="24" @click="goTopersonalCenter"></iconpark-icon>
      </div>
      <div class="headCard">
        <!-- <span class="tryRun">试运行</span> -->
        <div>
          <img src="/src/assets/assistantH5/kefuhead.png" />
          <div class="text">
            <img
              v-if="isHttpsURL(getAppDetail()?.identityIcon)"
              :src="getAppDetail()?.identityIcon"
              style="width: 204px"
            />
            <div
              v-else
              style="width: 204px; font-weight: 500; font-size: 18px; color: #313436"
            >
              {{ getAppDetail()?.identityIcon }}
            </div>
            <p>{{ getAppDetail()?.greeting }}</p>
          </div>
        </div>
        <van-button class="startBtn" type="primary" size="large" @click="startChat"
          >开始对话</van-button
        >
        <!-- <img
          src="/src/assets/chatTheme/startBtn.png"
          @click="startChat"
          class="startBtn"
        /> -->
      </div>
      <div class="contentMid">
        <div class="rightContent">
          <img src="/src/assets/chatTheme/bianminfuwu1.svg" />
          <span>便捷功能</span>
        </div>
        <!-- <img src="/src/assets/chatTheme/xitong.svg" class="leftContent" /> -->
      </div>
      <div class="bottomCard">
        <div
          v-for="(item, index) in serviceListData"
          :key="item.id"
          @click="toBlankPage(item)"
        >
          <!-- <img :src="item.menuIcon" />  -->
          <img v-if="item.id == 1" src="/src/assets/assistantH5/rcxt.png" alt="">
          <img v-else-if="item.id == 5" src="/src/assets/assistantH5/yygl1.png" alt="">
          <div :class="[`name${index}`]">{{ item.menuName }}</div>
          <div :class="[`name${index}`, 'des']">{{ item.menuDes }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { serviceList } from "/@/api/chat/index";
import { ref, onBeforeMount, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { userInfo } from "os";
const router = useRouter();
const route = useRoute();
const openId = ref('')
const authorizedUserList = ref([])
onBeforeMount(() => {
  //   getServiceList();
  authorizedUserList.value = [
    'ophdfwhA6b90Ppc-WU0iEbX8gTfc', // 刘寒晓
    'ophdfwtc83kVQ5xx7TrjDZrIKj3w', // 秦擎
    'ophdfwv6u2k74NTgthZBWW4WvJqE', // 张奕
    'ophdfwkiR_KYipIrgvF_qJhBHI7I', // 小伙子
    'ophdfwtfGsFfb2bja5u0UYAjWywI', // 你猜
    'ophdfwiVhDYYCCGdTEVXNadLr28w', // 李贝西
    'ophdfwnH1SAcoYFjzo50EN47BsZE', // 李泽婷
    'ophdfwsNHzqQBFqeegNmKXYWaBaw', // 潘聪
    'ophdfwklqT5XUQ309y0AbEAdR7ZQ', // 周艳丽 
  ]
});
const serviceListData = computed(() => {
  openId.value = JSON.parse(sessionStorage.getItem("userInfo"))?.openId ? JSON.parse(sessionStorage.getItem("userInfo")).openId : '';
	return authorizedUserList.value.includes(openId.value) ? [ 
  {
    id: 1,
    menuName: "日程协同",
    menuDes: "日程管理 高效协同",
    menuIcon: "/src/assets/assistantH5/rcxt.png",
  },
  {
    id: 5,
    menuName: "预约管理",
    menuDes: "村使馆预约相关",
    menuIcon: "/src/assets/assistantH5/yygl1.png",
  },
  ] : [ 
    {
      id: 1,
      menuName: "日程协同",
      menuDes: "日程管理 高效协同",
      menuIcon: "/src/assets/assistantH5/rcxt.png",
    }
  ];
});
// const serviceListData = ref([ 
//   {
//     id: 1,
//     menuName: "日程协同",
//     menuDes: "日程管理 高效协同",
//     menuIcon: "/src/assets/assistantH5/rcxt.png",
//   },
//   {
//     id: 5,
//     menuName: "预约管理",
//     menuDes: "村使馆预约相关",
//     menuIcon: "/src/assets/assistantH5/yygl1.png",
//   },
//   // {
//   //   id: 2,
//   //   menuName: "公文写作",
//   //   menuDes: "精通各类公文撰写",
//   //   menuIcon: "/src/assets/assistantH5/gwxz.png",
//   // },
//   // {
//   //   id: 3,
//   //   menuName: "法律咨询",
//   //   menuDes: "快速检索相关规定",
//   //   menuIcon: "/src/assets/assistantH5/flzx.png",
//   // },
//   // {
//   //   id: 4,
//   //   menuName: "接诉即办",
//   //   menuDes: "群众诉求快速响应",
//   //   menuIcon: "/src/assets/assistantH5/jsjb.png",
//   // },
// ]);
const getServiceList = () => {
  serviceList({
    status: 1,
    pageNo: 1,
    pageSize: 1000,
    serviceType: "便民服务",
    applicationId: getAppDetail()?.applicationId,
  }).then((res) => {
    if (res.code == "000000") {
      serviceListData.value = res.data?.records;
    } else {
      serviceListData.value = [];
    }
  });
};
const toBlankPage = (item) => {  
//   window.location.href = item.menuUrl;
  // const hashPart = item.menuUrl.split('#')[1] || '';
  // const queryPart = hashPart.split('?')[1] || '';
  // const queryParams = new URLSearchParams(queryPart);
  // const appCode = queryParams.get('appCode');
  // const matterId = queryParams.get('matterId');
  // console.log(appCode, matterId)
  // router.push({ name: 'information', params: { appId: appCode, matterId: matterId } });
  // console.log("item", item)
  const userInfo = sessionStorage.getItem("userInfo") ? JSON.parse(sessionStorage.getItem("userInfo")) : {
    phone: ""
  };
  if(item.id == 1) {
    // 日程协同
    window.open(`https://localhost/zgcH5/#/scheduleCollection?phone=${userInfo?.phone}`) // 生产
  } else {
    // 预约管理
    window.open(`https://localhost/zgcH5/#/zlZsgyy?phone=${userInfo?.phone}`) // 生产
  }

};
const toPage = () => {
  router.push(`/enterpriseServices/zgc`);
};
const toPage2 = () => {
  window.location.href =
    "https://mp.weixin.qq.com/mp/appmsgalbum?__biz=MzI2MTAxMTg3OA==&action=getalbum&album_id=3785115104287195139#wechat_redirect";
};
const startChat = () => {
  router.push(`/chat/${getAppDetail()?.applicationCode}/`);
};
const goTopersonalCenter = () => {
  router.push(`/homePersonalCenter/${getAppDetail()?.applicationCode}`); 
};
const getAppDetail = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo ? appInfo : "";
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
		const userInfo = sessionStorage.getItem('userInfo')
			? JSON.parse(sessionStorage.getItem('userInfo'))
			: {
					userType: '',
			  };
		var ua = navigator.userAgent.toLowerCase();
		if (!userInfo?.userType || userInfo?.userType == 'resident') {
			// 居民
			// 判断是否在微信浏览器内
			if (ua.match(/MicroMessenger/i) == 'micromessenger') {
				wx.miniProgram.getEnv((res) => {
					if (res.miniprogram) {
						window.wx.miniProgram.exitMiniProgram();
					} else {
						console.log('不在小程序内');
            window.history.back();
					}
				});
			} else {
				console.log('不在微信浏览器内');
        window.history.back();
			}
		} else {
			// 判断是否在微信浏览器内
			if (ua.match(/MicroMessenger/i) == 'micromessenger') {
				wx.miniProgram.getEnv((res) => {
					if (res.miniprogram) {
						// wx.miniProgram.navigateBack();
            router.push(`/homePage/${getAppDetail()?.applicationCode}`);
					} else {
						console.log('不在小程序内');
						router.push(`/homePage/${getAppDetail()?.applicationCode}`);
					}
				});
			} else {
				console.log('不在微信浏览器内');
        router.push(`/homePage/${getAppDetail()?.applicationCode}`);
			}
		}
	}
};
const toToggleClick = () => {
  router.push(`/residentReporting/${getAppDetail()?.applicationCode}`);
};

const isHttpsURL = (url) => {
  return /^https:\/\/.+/.test(url);
};
</script>
<style lang="scss" scoped>
.outer {
  width: 100%;
  height: 100%;
  background: #fff;
  // background-image: url("/src/assets/chatTheme/layoutBg.png");
  // background-repeat: no-repeat;
  // background-size: 100% 100%;

  .header {
    padding: 12px;
    width: 100%;
    height: 210px;
    background-image: url("/src/assets/assistantH5/mes-bg.png");
    background-size: 100% 100%;
    background-repeat: no-repeat;

    .headContent {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .headCard {
      position: relative;
      margin-top: 16px;
      background: rgba(255, 255, 255, 0.2);
      //   box-shadow: 0px 5px 6px 0px rgba(7, 29, 49, 0.1),
      //     0px 6px 6px 0px rgba(23, 97, 161, 0.1);
      box-shadow: 0px 5px 14px 0px rgba(7,29,49,0.1), 0px 6px 6px 0px rgba(23,97,161,0.1);
      border-radius: 4px;
      border-top: 1px solid;
      border-image: linear-gradient(169deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0)) 30 1;
      // backdrop-filter: blur(8px);
      padding-bottom: 0px;

      .tryRun {
        position: absolute;
        top: 0;
        right: 0;
        width: 52px;
        height: 24px;
        background: rgba(22, 158, 154, 0.1);
        border-radius: 0px 8px 0px 8px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #169e9a;
        line-height: 18px;
        text-align: left;
        font-style: normal;
        text-align: center;
        line-height: 18px;
        padding: 3px 8px;
      }

      > div {
        display: flex;
        justify-content: space-between;
        // padding-top: 23px;
        // padding-right: 16px;

        img {
          width: 122px;
          max-height: 140px;
        }

        .text {
          padding: 23px 16px 8px 0;
        }

        .text p:first-child {
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 18px;
          color: #169e9a;
          line-height: 24px;
          text-align: left;
          font-style: normal;
          letter-spacing: 1px;
        }

        .text p:last-child {
          margin-top: 6px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #313436;
          line-height: 20px;
          text-align: justify;
          font-style: normal;
          letter-spacing: 1px;
        }
      }

      ::v-deep .startBtn {
        width: calc(100%);
        max-width: none !important;
        background: linear-gradient(270deg, #2961fa 0%, #1a95d2 100%);
        border-radius: 0px 0px 4px 4px;
        .van-button__content {
          font-weight: 500;
          font-size: 18px;
        }
      }
    }

    .contentMid {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 40px 0 18px;

      .rightContent {
        display: flex;
        align-items: center;

        img {
          width: 20px;
          height: 16px;
          margin-right: 4px;

          span {
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 18px;
            color: #313436;
            line-height: 20px;
            text-align: justify;
            font-style: normal;
          }
        }
      }

      .leftContent {
        width: 20px;
        width: 20px;
      }
    }

    .bottomCard {
      display: flex;
      justify-content: flex-start;
      flex-wrap: wrap;
      gap: 9px;

      > div {
        display: flex;
        flex-direction: column;
        width: calc(50% - 5px);
        height: 112px;
        // background: rgba(255, 255, 255, 0.65);
        border-radius: 8px;
        padding: 14px 12px;
        position: relative;

        div {
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 18px;
          color: #1e647e;
          line-height: 24px;
          text-align: left;
          font-style: normal;
          margin-right: 9px;
          z-index: 999;
        }
        .name1 {
          color: #794F24; 
        }
        .name2 {
          color: #a0401f;
        }
        .name3 {
          color: #4f2881;
        }
        .des {
            font-size: 12px;
            line-height: 16px;
            margin-top: 2px;
        }
        img {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 112px;
          z-index: 1;
        }
      }
    }
  }
}
</style>
