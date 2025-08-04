<template>
  <div class="outer">
	  <div class="info-box">
	    <div class="title-box">
	      <div class="close">
	        <van-icon name="arrow-left" size="20" color="#333" @click="backHome" />
	      </div>
	      <div class="title">惠企政策链接</div>
	    </div>
	  </div>
	<div class="main-ctn">
		<div class="prelative" style="padding: 0 12px;box-sizing: border-box;">
			<img src="/src/assets/homePageMz/hqzc/hqzc1.png"/>
			<div class="dv_handler handler1"><a href="http://www.szlhq.gov.cn/bmxxgk/fzhggjtjj/" target="_blank"></a></div>
			<div class="dv_handler handler2"><a href="http://www.szlhq.gov.cn/bmxxgk/jjcjj/" target="_blank"></a></div>
			<div class="dv_handler handler3"><a href="http://www.szlhq.gov.cn/bmxxgk/rlzyj/" target="_blank"></a></div>
			<div class="dv_handler handler4"><a href="http://www.szlhq.gov.cn/bmxxgk/zfhjsj/" target="_blank"></a></div>
			<div class="dv_handler handler5"><a href="http://www.szlhq.gov.cn/bmxxgk/kjcxj/" target="_blank"></a></div>
		</div>
		<div class="prelative" style="padding: 0 12px;box-sizing: border-box;">
			<img src="/src/assets/homePageMz/hqzc/hqzc2.png"/>
			<div class="dv_handler handler6"><a href="https://www.szsiq.com" target="_blank"></a></div>
		</div>
		<div style="padding: 0 12px;box-sizing: border-box;">
			<img src="/src/assets/homePageMz/hqzc/hqzc3.png"/>
		</div>
		<div style="padding: 0 12px;box-sizing: border-box;">
			<img src="/src/assets/homePageMz/hqzc/hqzc4.png"/>
		</div>
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
  background-color: #fff;
  .info-box {
     width: 100%;
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
  .main-ctn{
	  height: calc(100% - 50px);
	  overflow-y: auto;
  }
}
.prelative{
	position: relative;
}
.dv_handler{
	width: 90%;
	height: 44px;
	position: absolute;
	a{
		display: inline-block;
		width: 100%;
		height: 100%;
	}
}
.handler1{
	top: 30%;
}
.handler2{
	top: 44%;
}
.handler3{
	top: 57%;
}
.handler4{
	top: 70%;
}
.handler5{
	top: 85%;
}
.handler6{
	top: 45%;
}
.flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 18px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}



.nodata {
  margin: 100px auto 0;
  height: 100px;
}
</style>
