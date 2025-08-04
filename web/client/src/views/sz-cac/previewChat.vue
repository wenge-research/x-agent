<template>
  <div class="outer">
    <div class="header">
      <div class="headContent">
        <img src="/src/assets/sz-cac/pre-logo.png" style="width: 124px; height: 36px" />
        <!-- <img
          @click="goTopersonalCenter"
          src="/src/assets/chatTheme/useraccount.svg"
          style="width: 20px; height: 20px"
        /> -->
      </div>
      <div class="headCard">
        <!-- <span class="tryRun">试运行</span> -->
        <div>
          <img src="/src/assets/sz-cac/kfxf.png" />
          <div class="text">
            <img
              v-if="getAppDetail()?.identityIcon"
              :src="getAppDetail()?.identityIcon"
              style="width: 178px; margin-top: 10px"
            />
            <p>{{ getAppDetail()?.greeting }}</p>
          </div>
        </div>
        <img
          src="/src/assets/sz-cac/kaishiduihua.png"
          @click="startChat"
          class="startBtn"
        />
      </div>
      <div class="slider">
        <div class="slider-li">
          <img src="/src/assets/sz-cac/wxfl.png" style="position: relative" />
          <span class="click" @click="toPageList(1)"></span>
          <img src="/src/assets/sz-cac/wxalk.png" />
          <span class="click" style="right: 0" @click="toPageList(2)"></span>
        </div>
        <div class="slider-li">
          <img src="/src/assets/sz-cac/wxdt.png" style="position: relative" />
          <span class="click" @click="toPageList(3)"></span>
          <img src="/src/assets/sz-cac/pfdh.png" />
          <span class="click" style="right: 0" @click="toPageList(4)"></span>
        </div>
      </div>
      <div class="contentMid">
        <div class="rightContent">
          <img src="/src/assets/sz-cac/bianminfuwu.png" />
          <span>便捷入口</span>
        </div>
        <!-- <img src="/src/assets/chatTheme/xitong.svg" class="leftContent" /> -->
      </div>
      <div class="bottomCard">
        <div v-for="item in serviceListData" :key="item.id" @click="toBlankPage(item)">
          <span>{{ item.menuName }}</span>
          <img v-if="item.id == 1" src="/src/assets/sz-cac/gat1.png" />
          <img v-if="item.id == 2" src="/src/assets/sz-cac/gat2.png" />
          <img v-if="item.id == 3" src="/src/assets/sz-cac/gat3.png" />
        </div>
      </div>
      <!-- <div class="bottom-tips">深圳市互联网信息办公室 主办</div> -->
      <div class="bottom-tips">对话内容由AI生成</div>
    </div>
    <PolicyPrivacy
      :visible="isPopupVisible"
      :content="popupContent"
      :title="popupTitle"
      @close="closePopup"
    />
    <OpinionsAndSuggestions
      :visible="opinionsAndSuggestionsPopupVisible"
      :content="popupContent"
      :title="popupTitle"
      @close="closePopup"
    />
  </div>
</template>
<script lang="ts" setup>
import { serviceList } from "/@/api/chat/index";
import { ref, onBeforeMount } from "vue";
import { useRouter, useRoute } from "vue-router";
// 政策隐私
import PolicyPrivacy from "./policy-privacy.vue";
import OpinionsAndSuggestions from "./opinions-and-suggestions.vue";
const router = useRouter();
const route = useRoute();
const privacyPolicy = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo ? appInfo?.privacyPolicy : "";
};
const isPopupVisible = ref(false);
const opinionsAndSuggestionsPopupVisible = ref(false);
const popupContent = ref("");
const popupTitle = ref("");
const popupType = ref("");
const openPopup = () => {
  popupContent.value = privacyPolicy();
  popupTitle.value = popupType.value == "3" ? "隐私政策" : "意见和建议";
  popupType.value == "3"
    ? (isPopupVisible.value = true)
    : (opinionsAndSuggestionsPopupVisible.value = true);
};

const closePopup = () => {
  popupType.value == "3"
    ? (isPopupVisible.value = false)
    : (opinionsAndSuggestionsPopupVisible.value = false);
};
onBeforeMount(() => {
  //   getServiceList();
});
const serviceListData = ref([
  // {
  // 	id: 3,
  // 	menuName: '隐私政策',
  // 	menuIcon: '/src/assets/sz-cac/gat3.png',
  // },
  {
    id: 2,
    menuName: "意见和建议",
    menuIcon: "/src/assets/sz-cac/gat2.png",
  },
  // {
  //   id: 1,
  //   menuName: "使用指南",
  //   menuIcon: "/src/assets/sz-cac/gat1.png",
  // },
]);
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
  popupType.value = item.id;
  if (item.id == 3) {
    openPopup();
  }
  if (item.id == 2) {
    openPopup();
  }
};
const toPage = () => {
  router.push(`/enterpriseServices/zgc`);
};
const toPage2 = () => {
  router.push(`/eTime/zgc`);
};
const startChat = () => {
  router.push(`/sz-cac/${getAppDetail()?.applicationCode}/`);
};
const goTopersonalCenter = () => {
  router.push(`/homePersonalCenter/${getAppDetail()?.applicationCode}`);
};
const getAppDetail = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo ? appInfo : "";
};
const toToggleClick = () => {
  router.push(`/residentReporting/${getAppDetail()?.applicationCode}`);
};
const toPageList = (type: any) => {
  const path = route.path;
  router.push({
    path: "/szPreviewChat/list",
    query: {
      type,
      mainPath: path,
    },
  });
};
</script>
<style lang="scss" scoped>
.outer {
  width: 100%;
  height: 100%;
  background: #f3f5fa;
  //   background-image: url("/src/assets/chatTheme/layoutBg.png");
  //   background-repeat: no-repeat;
  //   background-size: 100% 100%;

  .header {
    padding: 40px 16px 16px;
    width: 100%;
    height: 284px;
    background-image: url("/src/assets/sz-cac/pre-bg.png");
    background-size: 100% 100%;
    background-repeat: no-repeat;

    .headContent {
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .headCard {
      position: relative;
      margin-top: 16px;
      padding-bottom: 50px;

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
        padding-top: 23px;
        padding-right: 16px;

        img {
          width: 128px;
        }

        .text {
          padding-bottom: 8px;
        }

        .text p:first-child {
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 18px;
          color: #01e6fe;
          line-height: 24px;
          text-align: left;
          font-style: normal;
          letter-spacing: 1px;
        }

        .text p:last-child {
          margin-top: 12px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #01e6fe;
          line-height: 20px;
          text-align: justify;
          font-style: normal;
          letter-spacing: 1px;
        }
      }

      ::v-deep .startBtn {
        width: calc(100% + 20px);
        max-width: none !important;
        position: absolute;
        bottom: -8px;
        left: -10px;
      }
    }

    .contentMid {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 34px 0 18px;

      .rightContent {
        display: flex;
        align-items: center;

        img {
          width: 20px;
          height: 16px;
          margin-right: 4px;
        }
        span {
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 18px;
          color: #434649;
          line-height: 20px;
          text-align: justify;
          font-style: normal;
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
        justify-content: space-between;
        width: calc(50% - 5px);
        height: 68px;
        background: rgba(255, 255, 255, 0.65);
        border-radius: 8px;
        padding: 14px 12px;
        align-items: center;

        span {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 16px;
          color: #494c4f;
          line-height: 20px;
          text-align: left;
          font-style: normal;
          margin-right: 9px;
        }

        img {
          width: 48px;
          height: 48px;
        }
      }
    }
  }

  .bottomCard {
    display: flex;
    justify-content: flex-start;
    flex-wrap: wrap;
    gap: 9px;

    > div {
      display: flex;
      justify-content: justify-content;
      width: 167px;
      height: 68px;
      background: rgba(255, 255, 255, 0.65);
      border-radius: 8px;
      padding: 14px 12px;
      align-items: center;

      span {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #494c4f;
        line-height: 20px;
        text-align: left;
        font-style: normal;
        margin-right: 9px;
      }

      img {
        width: 44px;
        height: 44px;
      }
    }
  }
  .bottom-tips {
    height: 24px;
    text-align: center;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #b4bccc;
    line-height: 24px;
    margin-top: 28px;
  }
}
.slider {
  margin-top: 14px;
  .slider-li {
    margin: 10px 0;
    overflow: hidden;
    position: relative;
    .click {
      position: absolute;
      width: 50%;
      z-index: 10;
      top: 0;
      bottom: 0;
    }
    img {
      width: calc(50% - 7px);
      position: absolute;
      right: 0;
      top: 0;
    }
  }
}
</style>
