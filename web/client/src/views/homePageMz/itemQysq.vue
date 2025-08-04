<template>
  <div class="outer">
	  <div class="info-box">
	    <div class="title-box">
	      <div class="close">
	        <van-icon name="arrow-left" size="20" color="#333" @click="backHome" />
	      </div>
	      <div class="title">企业诉求解决</div>
	    </div>
	  </div>
	<div class="main-ctn">
		<div>
			<img src="/src/assets/homePageMz/qysq1.png"/>
		</div>
		<div style="padding: 0 12px;box-sizing: border-box;">
			<img src="/src/assets/homePageMz/qysq2.png"/>
		</div>
		<div style="padding: 0 12px;box-sizing: border-box;">
			<img src="/src/assets/homePageMz/qysq3.png"/>
		</div>
		<div class="zxwt" @click="backZxwt">
			<img src="/src/assets/homePageMz/zxwt.png"/>
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
const backZxwt = () => {
  router.push(`/investCollectionTwo`);
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
.flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 18px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

.zxwt{
	position: fixed;
	bottom: 40px;
	left:50%;
	transform: translateX(-50%);
}

.nodata {
  margin: 100px auto 0;
  height: 100px;
}
</style>
