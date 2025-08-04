<template>
  <div class="centerInitItem-longhua">
    <div style="height: 290px; overflow: hidden; width: 100%; position: relative">
      <div class="service">便民服务</div>
      <div v-show="currentPage == 1" class="flex serviceList slide-right">
        <div v-for="(item, index) in list1" :key="index" style="width: 25%">
          <div @click="longhuaClick(item.menuUrl)" class="list">
            <img :src="item.menuIcon" alt="" style="width: 48px; height: 48px" />
            <div class="name" style="margin-top: 10px">
              {{ item.menuName }}
            </div>
          </div>
        </div>
      </div>
      <div v-show="currentPage == 2" class="flex serviceList slide-left">
        <div v-for="(item, index) in list2" :key="index" style="width: 25%">
          <div @click="longhuaClick(item.menuUrl)" class="list">
            <img :src="item.menuIcon" alt="" style="width: 48px; height: 48px" />
            <div class="name" style="margin-top: 10px">
              {{ item.menuName }}
            </div>
          </div>
        </div>
      </div>
      <div v-if="pageNo > 1" class="check-line">
        <span
          :class="[currentPage == 1 ? 'selected' : '']"
          @click="currentPage = 1"
        ></span>
        <span
          :class="[currentPage == 2 ? 'selected' : '']"
          @click="currentPage = 2"
        ></span>
      </div>
    </div>
    <div class="guarantee messageTab">
      <w-tabs style="height: 100%" v-model="activename" @change="tabsChange">
        <w-tab-pane key="2" title="政策文件">
          <ul style="overflow-y: scroll">
            <li
              v-for="(item, index) in policyDocumentList"
              :key="index"
              @click="sendMessage(item.url)"
            >
              <div class="title">
                {{ item.title }}
              </div>
            </li>
          </ul>
          <div
            v-if="policyDocumentList.length"
            class="fontSize16"
            style="
              color: #4085f4;
              margin: 14px auto 0;
              height: 24px;
              cursor: pointer;
              text-align: center;
            "
            @click="sendMessageClickone"
          >
            查看更多>>
          </div>
        </w-tab-pane>
        <w-tab-pane key="3" title="政策解读">
          <ul style="overflow-y: scroll">
            <li
              v-for="(item, index) in policyInterpretationList"
              :key="index"
              @click="sendMessage(item.url)"
            >
              <div class="title">
                {{ item.title }}
              </div>
            </li>
          </ul>
          <div
            v-if="policyInterpretationList.length"
            class="fontSize16"
            style="
              color: #4085f4;
              margin: 14px auto 0;
              height: 24px;
              cursor: pointer;
              text-align: center;
            "
            @click="sendMessageClick"
          >
            查看更多>>
          </div>
        </w-tab-pane>
      </w-tabs>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted } from "vue";
import { useBasicLayout } from "/@/hooks/useBasicLayout";
import { useKnowledgeState } from "/@/stores/knowledge";
import { useRoute } from "vue-router";
import { get4317Json, get4316Json, get4310Json } from "/@/api/knowledge";
import { serviceList } from "/@/api/chat/index";
import { formatDate } from "/@/utils/formatTime";
import { Modal } from "winbox-ui-next";
const time = ref("");
const date = ref("");
const pageNo = ref(1);
const currentPage = ref(1);
const route = useRoute();
const isShow = ref(true);
const knowledgeState = useKnowledgeState();
const currentLibrary: any = computed(() => knowledgeState.currentLibrary);
const previewData: any = computed(() => knowledgeState.previewData);

const list1 = ref([]);
const list2 = ref([]);
const policyDocumentList = ref([]);
const policyInterpretationList = ref([]);

// 移动端自适应相关
const { isMobile } = useBasicLayout();
const activename = ref("2");
const culturalList = ref([]);

const sendMessage = (url) => {
  window.open(url, "_blank");
};

const sendMessageClickone = () => {
  window.open("https://www.szlhq.gov.cn/xxgk/zcfg/qgfxwj/qzcxwj/", "_blank");
};
const sendMessageClick = () => {
  window.open("https://www.szlhq.gov.cn/xxgk/zcfg/zcjd/", "_blank");
};

const curRouteId = ref("");
onMounted(() => {
  curRouteId.value = sessionStorage.getItem("curRouteId") as string;
  setTimeout(() => {
    document.querySelector(".center-side").scrollTop = 999999999;
  }, 500);
  setInterval(() => {
    date.value = formatDate(new Date(), "YYYY/mm/dd");
    time.value = formatDate(new Date(), "HH:MM:SS");
  }, 1000);

  watch(
    () => previewData.value.active,
    () => {
      getPolicyLinkClick();
      get4317JsonFun();
      get4310JsonFun();
    },
    { immediate: true, deep: true }
  );
  watch(
    () => currentLibrary.value.fileCount,
    (val: any) => {
      if (val) {
        getPolicyLinkClick();
        get4317JsonFun();
        get4310JsonFun();
      }
    },
    { immediate: true, deep: true }
  );
});
const tabsChange = (value) => {
  // console.log(value);
  // if(value)
};
const getAppDetail = () => {
  let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
  return appInfo ? appInfo : "";
};
// 获取链接
const getPolicyLinkClick = async () => {
  serviceList({
    status: 1,
    pageNo: 1,
    pageSize: 1000,
    serviceType: "便民服务",
    applicationId: getAppDetail()?.applicationId,
  }).then((res) => {
    if (res.code == "000000") {
      const list = res.data?.records;
      list1.value = list?.slice(0, 8);
      list2.value = list?.slice(8);
      if (list1.value.length > 7) {
        pageNo.value += 1;
      }
    } else {
      list1.value = [];
    }
  });
};
// 获取链接
const get4317JsonFun = async () => {
  let res = await get4317Json();
  get4316JsonFun(res.articles);
};
// 获取链接
const get4316JsonFun = async (articles) => {
  // let res = await get4316Json();
  policyDocumentList.value = articles
    .concat(res.articles)
    .sort((a, b) => new Date(b.date) - new Date(a.date))
    .slice(0, 6);
};
// 获取链接
const get4310JsonFun = async () => {
  let res = await get4310Json();
  policyInterpretationList.value = res.articles
    .sort((a, b) => new Date(b.date) - new Date(a.date))
    .slice(0, 4);
};
const longhuaClick = (url) => {
  if (!url) return;
  // if (url && url.indexOf("szlhq") == -1) {
  //   Modal.confirm({
  //     title: "szlhq.gov.cn 显示",
  //     content: `您访问的链接即将离开“xxxxxxx政府在线”门户网站，是否继续？`,
  //     closable: true,
  //     okText: "确定",
  //     cancelText: "取消",
  //     hideCancel: false,
  //     modalClass: "myConfirm",
  //     onOk: async () => {
  //       window.open(url, "_blank");
  //     },
  //   });
  // } else {
  //   window.open(url, "_blank");
  // }
  window.open(url, "_blank");
};
watch(
  () => route.params.appId,
  () => {
    if (route.params.appId && route.params.appId != "") {
      isShow.value = false;
      setTimeout(() => {
        isShow.value = true;
      }, 500);
    }
  }
);
</script>

<style scoped lang="scss">
@import "/@/theme/mixins/index.scss";

.fontSize16 {
  @include add-size($font-size-base16, $size);
}

.centerInitItem-longhua {
  padding: 0 20px;
  width: 37.5%;
  height: 100%;
  background: rgba(255, 255, 255, 0.65);
  border-radius: 30px 0 0 30px;
  // border: 1px solid #ffffff;
  backdrop-filter: blur(1px);

  .messageTab {
    height: calc(100% - 290px);
    /* 整个滚动条轨道 */
    ::-webkit-scrollbar {
      width: 3px; /* 垂直滚动条宽度 */
    }
    /* 滚动条滑块 */
    ::-webkit-scrollbar-thumb {
      background: #7bb4e0;
    }
    :deep(.w-tabs-content) {
      padding-top: 0 !important;
      height: calc(100% - 45px);
      max-height: 600px;
      overflow-y: auto;
    }
    :deep(.w-tabs-nav::before) {
      height: 0;
    }
    :deep(.w-tabs-nav-tab) {
      height: 45px;
      line-height: 45px;

      .w-tabs-tab-title {
        @include add-size(22px, $size);
        line-height: 28px;
        color: #333;
        margin-left: 50%;
        transform: translate(-50%, 0);
      }
    }
    :deep(.w-tabs-nav-tab-list) {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 16px;
    }
    :deep(.w-tabs-nav-ink) {
      background-color: #4186f4;
    }

    :deep(.w-tabs-tab-active) {
      .w-tabs-tab-title {
        @include add-size(22px, $size);
        color: #4085f4;
      }
    }

    :deep(.w-tabs-tab) {
      width: 120px;
    }

    padding-bottom: 10px;

    ul {
      width: 100%;
      box-sizing: border-box;
      list-style-type: disc;
      @include add-size(20px, $size);

      li {
        width: 100%;
        box-sizing: border-box;
        padding: 15px 5px;
        list-style: none;
        @include add-size(15, $size);
        font-weight: 400;
        color: #333;
        line-height: 1.8;
        border-bottom: 2px dashed #fff;
        cursor: pointer;
        .title {
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }
      }

      li:hover {
        color: #4085f4;
      }
    }
  }

  .service {
    @include add-size(22px, $size);
    font-weight: 500;
    color: #333;
    line-height: 28px;
    margin-bottom: 20px;
    margin-top: 25px;
  }
}

@keyframes rotate {
  0% {
    transform: rotateZ(0);
  }

  100% {
    transform: rotateZ(180deg);
  }
}

@media (any-hover: hover) {
  .item:hover {
    // box-shadow: 0 2px 16px #262a3233;
    .more {
      display: block !important;
    }
  }
}

.serviceList {
  flex-wrap: wrap;
  justify-content: space-between;

  .list {
    width: 100%;
    align-items: center;
    display: flex;
    flex-direction: column;
    margin-bottom: 10px;
    cursor: pointer;
    &:hover {
      .name {
        color: #4085f4;
      }
    }
  }

  .name {
    text-align: center;
    width: 76px;
    @include add-size(13px, $size);
    font-weight: 400;
    color: #333;
    line-height: 16px;
  }
}

.check-line {
  position: absolute;
  bottom: -4px;
  width: 100%;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  span {
    display: inline-block;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #000;
    opacity: 0.2;
    margin: 0 4px;
    cursor: pointer;
  }
  .selected {
    width: 30px;
    border-radius: 4px;
    background: #007aff;
    opacity: 1;
  }
}
</style>
