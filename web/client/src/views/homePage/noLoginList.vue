<template>
  <div class="outer">
    <div class="info-box">
      <div class="title-box">
        <van-icon name="arrow-left" color="#333" size="20" @click="backHome" />
        <div class="title">未登录人员名单</div>
      </div>
      <div>
        <van-dropdown-menu class="dropMenu">
          <van-dropdown-item
            v-model="valueName"
            :options="optionList"
            @close="changeItem"
          >
            <div class="dropdown_search">
              <w-input
                v-model="searchText"
                class="searchInput"
                placeholder="搜索关键词"
                @input="changeText"
              >
                <template #prefix
                  ><cool-sousuo size="1em" color="currentColor"></cool-sousuo
                ></template>
              </w-input>
            </div>
          </van-dropdown-item>
        </van-dropdown-menu>
      </div>
    </div>
    <div class="header" v-if="listLogin.length">
      <div v-for="(item, index) in listLogin" class="list">
        <img src="/src/assets/homePage/touxiang.svg" class="touxiang" />
        <div class="flex">
          <div class="name">
            {{ item.label }}
          </div>
          <div class="name dept">
            {{ item.value }}
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <w-empty>
        <template #image>
          <img class="nodata" :src="noDataImg" alt="" />
        </template>
        暂无记录
      </w-empty>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { todayNoLogin } from "/@/api/chat/index";
import { ref, onBeforeMount } from "vue";
import { useRouter, useRoute } from "vue-router";
import { showNotify } from "vant";
const router = useRouter();
const route = useRoute();
const searchText = ref("");
const optionList = ref([]);
const listLoginOne = ref([]);
const optionListOne = ref([]);
const listLogin = ref([]);
const valueName = ref("全部部门");
import noDataImg from "/@/assets/chat/nodata.svg";
onBeforeMount(() => {
  todayNoLoginList();
});
const backHome = () => {
  // let appCode = sessionStorage.getItem('applicationCode');
  // router.push(`/homePage/${appCode}`);
  router.push(`/homePage/zgc`);
};
const changeText = (val: string) => {
  if (!searchText.value) {
    optionList.value = optionListOne.value;
  }
  optionList.value = optionListOne.value.filter(
    (item) => item.text.indexOf(searchText.value) > -1
  );
  valueName.value = "全部部门";
};
const changeItem = (val: string) => {
  if (valueName.value == "全部部门") {
    listLogin.value = listLoginOne.value;
  } else {
    listLogin.value = listLoginOne.value.filter(
      (item) => item.value.indexOf(valueName.value) > -1
    );
  }
};
const todayNoLoginList = async () => {
  const res = await todayNoLogin({});
  if (res?.code === "000000") {
    if (res?.data?.result.length) {
      listLogin.value = res?.data?.result;
      listLoginOne.value = res?.data?.result;
      const uniqueArr = [];
      res?.data?.result.forEach((item) => {
        if (!uniqueArr.some((i) => i.value == item.value)) {
          uniqueArr.push({
            text: item.value,
            value: item.value,
          });
        }
      });
      uniqueArr.unshift({
        text: "全部部门",
        value: "全部部门",
      });
      optionList.value = uniqueArr;
      optionListOne.value = uniqueArr;
    } else {
      listLogin.value = [];
      optionList.value = [];
    }
  }
};
</script>
<style lang="scss" scoped>
.outer {
  width: 100%;
  height: 100%;
  background: #f0f3fa;
  .info-box {
    width: 100%;
    height: auto;
    background: #fff;
    display: flex;
    justify-content: space-between;
    padding: 0 20px;
    .title-box {
      width: 100%;
      height: 48px;
      display: flex;
      align-items: center;
      position: relative;

      .title {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #333333;
        line-height: 24px;
        margin-left: 8px;
      }
    }
  }
  .header {
    overflow-y: auto;
    height: calc(100% - 48px);
    .list {
      display: flex;
      align-items: center;
      margin: 0 16px;
      .touxiang {
        width: 28px;
        height: 28px;
        margin-right: 12px;
      }
      .name {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #353535;
      }
      .dept {
        font-size: 14px;
        color: #999999;
      }
    }
  }
}
.flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 18px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

::v-deep .van-dropdown-menu__bar {
  box-shadow: 0 0 0 transparent;
  .van-dropdown-menu__title:after {
    border-color: transparent transparent #646479 #646479;
  }
  .van-dropdown-menu__title--active:after {
    border-color: transparent transparent #1989fa #1989fa;
  }
}

.dropMenu {
  ::v-deep .van-popup {
    padding-top: 50px;
    border-top: 1px solid rgba(0, 0, 0, 0.12);
  }
  .dropdown_search {
    width: 90%;
    position: absolute;
    top: 10px;
    left: 5%;
    .searchInput {
      border-radius: 18px;
      background: #f7f8fa;
      border: 1px solid #f7f8fa;
    }
  }
}

.nodata {
  margin: 100px auto 0;
  height: 100px;
}
</style>
