<template>
  <div class="outer">
    <div class="header">
      <img src="/src/assets/assistantH5/doc-bg.png" style="position: absolute; top: 0" />
      <div class="headContent">
        <img
          src="/src/assets/assistantH5/xiezuo.png"
          style="width: 36px; height: 36px; margin-left: 10px"
        />
        <div class="document">公文写作</div>
      </div>
      <div class="title-textarea">
        <div class="titleName">输入您想写的的文章主题</div>
        <div style="position: relative">
          <w-textarea
            ref="textareaRef"
            v-model="text"
            :placeholder="placeholder"
            :auto-size="{ minRows: 1, maxRows: 6 }"
            :max-length="500"
            @click="toDocumentClick"
          />
          <img src="/src/assets/zhushou/mic1.svg" class="sendVoiceBtn" />
        </div>
      </div>

      <div class="contentMid">
        <div class="rightContent">
          <span>常用模版</span>
        </div>
        <div class="tabs-list">
          <div
            v-for="(item, index) in tabsList"
            :key="item.id"
            @click="toTablist(item, index)"
            :class="['list', activeIndex == index ? 'activeList' : '']"
          >
            <div :class="[`name${index}`]">{{ item.menuName }}</div>
          </div>
        </div>
      </div>
      <div class="bottomCard">
        <div
          v-for="(item, index) in serviceListData"
          :key="item.id"
          @click="toBlankPage(item)"
          :class="['cardlist', `cardlist${index % 4}`]"
        >
          <div style="display: flex; align-items: center; margin-bottom: 8px">
            <div class="box">
              <img
                v-if="index%4 == 0"
                src="/src/assets/assistantH5/gongwen0.svg"
                style="width: 14px; height: 14px"
              />
              <img
                v-if="index%4 == 1"
                src="/src/assets/assistantH5/gongwen1.svg"
                style="width: 14px; height: 14px"
              />
              <img
                v-if="index%4 == 2"
                src="/src/assets/assistantH5/gongwen2.svg"
                style="width: 14px; height: 14px"
              />
              <img
                v-if="index%4 == 3"
                src="/src/assets/assistantH5/gongwen3.svg"
                style="width: 14px; height: 14px"
              />
            </div>
            <div class="name">{{ item.menuName }}</div>
          </div>
          <div>{{ item.menuDes }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { serviceList } from "/@/api/chat/index";
import { ref, onBeforeMount, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
const router = useRouter();
const route = useRoute();
onBeforeMount(() => {
  //   getServiceList();
});
const tabsList = ref([
  {
    id: 1,
    menuName: "全部",
  },
  {
    id: 2,
    menuName: "公文",
  },
  {
    id: 3,
    menuName: "日常/办公",
  },
]);
const activeIndex = ref(0);
const serviceListData = ref([
  {
    id: 1,
    menuName: "会议纪要",
    menuDes: "在项目伊始时做一个用户研究的作用好过于在项目落地后再对50个用户进行测试",
  },
  {
    id: 2,
    menuName: "会议纪要",
    menuDes: "产品创造于工厂，而品牌创造于心灵",
  },
  {
    id: 3,
    menuName: "会议纪要",
    menuDes:
      "好的界面设计并不始于图片，而是始于对人的理解，比如人们喜欢什么，为什么人们会试用某种特定的软件，他们可能与之怎样交互",
  },
  {
    id: 4,
    menuName: "会议纪要",
    menuDes:
      "但无论是哪种类型用户，都一定会有同种共性——好奇心。好奇心足以牵引着一个人对所产生事物的关注程度和好感度，从而使得他由被动接受信息直接转型为主动了解信息",
  },
  {
    id: 5,
    menuName: "会议纪要",
    menuDes:
      "但无论是哪种类型用户，都一定会有同种共性——好奇心。好奇心足以牵引着一个人对所产生事物的关注程度和好感度，从而使得他由被动接受信息直接转型为主动了解信息",
  },
  {
    id: 5,
    menuName: "会议纪要",
    menuDes:
      "但无论是哪种类型用户，都一定会有同种共性——好奇心。好奇心足以牵引着一个人对所产生事物的关注程度和好感度，从而使得他由被动接受信息直接转型为主动了解信息但无论是哪种类型用户，都一定会有同种共性——好奇心。好奇心足以牵引着一个人对所产生事物的关注程度和好感度，从而使得他由被动接受信息直接转型为主动了解信息",
  },
]);
const text = ref("");
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
};
const toTablist = (item, index) => {
  activeIndex.value = index;
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
const toToggleClick = () => {
  router.push(`/residentReporting/${getAppDetail()?.applicationCode}`);
};

const isHttpsURL = (url) => {
  return /^https:\/\/.+/.test(url);
};
const placeholder = computed(() => {
  return (
    JSON.parse(window.localStorage.getItem(`${route.params.appId}`))?.inputPlaceholder ||
    "如：我的职业是研究员 ，帮我写一篇关于[主题]的研究报告。"
  );
});
const toDocumentClick = () => {
  router.push(`/documentWriting/${getAppDetail()?.applicationCode}`);   
}
</script>
<style lang="scss" scoped>
@import "/@/theme/mixins/index.scss";
.outer {
  width: 100%;
  height: 100%;
  background: #fff;
  overflow-y: auto;
  .header {
    width: 100%;
    height: 168px;
    background: linear-gradient(180deg, #b1e1ff 0%, #e2f4ff 100%);
    position: relative;

    .headContent {
      display: flex;
      align-items: center;
      padding: 16px;
      .document {
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 18px;
        color: #383d47;
        line-height: 28px;
        margin-left: 12px;
      }
    }
    .title-textarea {
      padding: 0 12px 12px 12px;
      .titleName {
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 16px;
        color: #434649;
      }

      :deep(.w-textarea) {
        line-height: 1.5 !important;
        @include add-size($font-size-base16, $size);
        padding: 12px 16px;
      }
      .active {
        :deep(.w-textarea) {
          padding: 16px 90px 4px 16px;
        }
      }

      .w-textarea-wrapper {
        margin-top: 12px;
        box-shadow: 0px 6px 20px 0px rgba(24, 104, 87, 0.1);
        border-radius: 4px;
        border: 1px solid #b4bccc;
        width: 100%;
        height: 104px;
      }
      .sendVoiceBtn {
        position: absolute;
        bottom: 14px;
        right: 26px;
      }
    }

    .contentMid {
      margin: 16px 12px 18px;

      .rightContent {
        display: flex;
        align-items: center;
        margin-bottom: 10px;
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
      .tabs-list {
        display: flex;
        align-items: center;
        .list {
          background: #f4f6f9;
          border-radius: 20px;
          height: 40px;
          line-height: 40px;
          font-weight: 400;
          font-size: 14px;
          color: #828894;
          padding: 0 16px;
          margin-right: 8px;
        }
        .activeList {
          background: #1477e3;
          color: #ffffff;
        }
      }
    }

    .bottomCard {
      display: flex;
      justify-content: space-between;
      flex-wrap: wrap;
      padding: 0 12px;

      .cardlist {
        display: flex;
        flex-direction: column;
        width: calc(50% - 5px);
        background: #ffffff;
        border-radius: 4px;
        border: 1px solid #e6e8ed;
        padding: 14px 12px;
        position: relative;
        margin-bottom: 12px;

        div {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          color: #828894;
          line-height: 18px;
          text-align: justify;
          font-style: normal;
        }
        .box {
          width: 24px;
          height: 24px;
          background: #0075ff;
          border-radius: 12px;
          padding: 5px;
        }
        .name {
          font-weight: 500;
          font-size: 14px;
          color: #383d47;
          margin-left: 8px;
        }
      }

      .cardlist1 {
        .box {
          background: #e85985;
        }
      }
      .cardlist2 {
        .box {
          background: #2bcac8;
        }
      }
      .cardlist3 {
        .box {
          background: #ff9700;
        }
      }
    }
  }
}
.slider {
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
      width: 100%;
      position: absolute;
      right: 0;
      top: 0;
    }
  }
}
</style>
