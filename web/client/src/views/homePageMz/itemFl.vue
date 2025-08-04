<template>
  <div class="outer">
    <div class="info-box">
      <div class="title-box">
        <div class="close">
          <van-icon name="arrow-left" color="#333" @click="backHome" />
        </div>
        <div class="title">附录</div>
      </div>
    </div>
    <div style="padding: 0 12px;box-sizing: border-box;">
    	<img src="/src/assets/homePageMz/hyzc.png"/>
    </div>
	
	
  </div>
</template>
<script lang="ts" setup>
import { ref, onBeforeMount } from "vue";
import { useRouter, useRoute } from "vue-router";
const router = useRouter();
const route = useRoute();
const searchText = ref("");
const optionList = ref([]);
const listLoginOne = ref([]);
const optionListOne = ref([]);
const listLogin = ref([]);
onBeforeMount(() => {
  
});
const backHome = () => {
  router.push(`/homePageMz`);
};

</script>
<style lang="scss" scoped>
.outer {
  width: 100%;
  height: 100%;
  background-color: #F6f8fa;
  .info-box {
    width: 100%;
 //    height: 180px;
	// background-image: url('/src/assets/homePageMz/fang1.png');
	// background-repeat: no-repeat;
	// background-size: 100% 180px;
 //    display: flex;
 //    justify-content: space-between;
    padding: 0 20px;
	background-color: #fff;
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
       width: 100%;
	   text-align: center;
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
