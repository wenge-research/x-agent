<script setup>
import { ref, onBeforeMount , onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router'
import {queryAllMap } from '/@/api/llmz';
import AMapLoader from "@amap/amap-jsapi-loader";
 const router = useRouter();
 const route = useRoute();
 const mapTitle = ref('');
 const mapType = ref('');
 const mapText = ref('');
 const mapList = ref([]);
let map = null;
import image1 from '/src/assets/homePageMz/di/map-icon1.png';
import image2 from '/src/assets/homePageMz/di/map-icon2.png';
 // 获取产业类型

const getqueryAllMap = () => {
 	queryAllMap().then((res) => {
 		console.log(res);		
 		if (res.code == '000000') {
 			window._AMapSecurityConfig = {
 			    securityJsCode: "667cfb680520053bc68aefcd738f66cd",
 			  };
 			  AMapLoader.load({
 			    key: "667cfb680520053bc68aefcd738f66cd", // 申请好的Web端开发者Key，首次调用 load 时必填
 			    version: "2.0", // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
 			    plugins: ["AMap.Scale"], //需要使用的的插件列表，如比例尺'AMap.Scale'，支持添加多个如：['...','...']
 			  })
 			    .then((AMap) => {
 			      map = new AMap.Map("container", {
 			        // 设置地图容器id
 			        viewMode: "2D", // 地图模式
 			        zoom: 13, // 初始化地图级别
 			        center: [114.033715,22.654382], // 初始化地图中心点位置
 			      });
				  console.log(res.data,'数据');		
 				  
 				      // 添加一些分布不均的点到地图上,地图上添加三个点标记，作为参照
 				      res.data.forEach(function(marker) {
						  let img = null
						  if(marker.type=='综合体'){
							  img = image2
						  }else{
							  img = image1
						  }
 				         const markerInstance = new AMap.Marker({
 				              map: map,
 				              icon: new AMap.Icon({              
 				              image: img,	              
 				              size: new AMap.Size(30, 38), //图标大小	              
 				              imageSize: new AMap.Size(30, 38)	              
 				              }),
 				              position: [marker.longitude, marker.latitude],
 				              offset: new AMap.Pixel(-13, -20)
 				          });
 						  markerInstance.on("click", function (e) {
 						    console.log("你点击了Marker",marker);
							mapType.value = marker.type
							mapText.value = marker.address
							mapTitle.value = marker.name
							mapList.value = marker.contactInfoResults
 						  });
 				      });
 			    })
 			    .catch((e) => {
 			      console.log(e);
 			    });
 		}
 	});
 };
const backHome = () => {
	router.push(`/bannerDiList`);
};
const isShow = ref(false);
const addPhone = () => {
	 console.log("你点击了Marker",mapList.value[0].phone);
	 if(mapList.value.length==1){
		  window.location.href = `tel:${mapList.value[0].phone}`;
	 }else{
		 isShow.value = true;
		 console.log('有两个');
	 }
};
const addTel = (phone) => {
	window.location.href = `tel:${phone}`;
};

const closePopup = () => {
	isShow.value = false;
};
onMounted(() => {
	getqueryAllMap()
});
</script>
 
<template>
	<div class="outer">
		<div class="info-box">
			<div class="title-box">
				<div class="close">
					<van-icon name="arrow-left" size="20" color="#333" @click="backHome" />
				</div>
				<div class="title">大型综合体、商超</div>
			</div>
		</div>
		
		<div id="container" style="width: 100%;height: calc(100% - 50px);">
			<div :class="mapType=='综合体'?'map-bg map-bg1':'map-bg map-bg2'" v-if="mapText!=''">
				<div class="map-title">{{mapTitle}} <span :class="mapType=='综合体'?'map-type1':'map-type2'">{{mapType}}</span></div>
				<div class="map-text"><img style="height: 14px;margin-right: 10px;display: inline-block;" src="/src/assets/homePageMz/di/mapAddress.svg" alt="" />{{mapText}}</div>
				<div class="map-phone"><button @click="addPhone">电话联系</button></div>
			</div>
		</div>
		<div class="popup-ctn" @click="closePopup" v-show="isShow">
			<div class="popup" @click.stop>
				<div class="popup-title">电话联系</div>
				<div
					class="popup-list"					
					v-for="(item, index) in  mapList"
					:key="index"
					
				>
					
					<div><img style="height: 18px;margin-right: 12px;display: inline-block;color: #5a57b9;" src="/src/assets/homePageMz/di/user.svg" alt="" />{{ item.userName }} <span style="margin-left: 20px;">{{ item.phone }}</span></div>
					<button class="phone" @click="addTel(item.phone)">
						<img style="height: 18px;" src="/src/assets/homePageMz/di/phone.svg" alt="" />
					</button>
				</div>
			</div>
		</div>
    </div>
</template>
<style lang="scss" scoped>
	.outer {
		width: 100%;
		height: 100%;
		background-color: #fff;

		.info-box {
			padding: 0 20px;
            box-sizing: border-box;
			
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
        .map-bg{
			position: absolute;
			bottom: 0;
			z-index: 10000;
			width: 100%;
			height: 160px;
			// background-color: #fff;
			padding-top: 40px;
		}
		.map-title{
			font-size: 18px;
			color: #000;
			font-weight: 550;
			margin-bottom: 6px;
			padding: 0 10px;
		}
		.map-text{
			font-size: 14px;
			color: #666;
			padding: 0 10px;
		}
		.map-type1{
			font-size: 14px;			
			display: inline-block;
			padding: 6px 14px;
			border-radius: 20px;
			background-color: #cbe3f8;
			color: #49a1ee;
			margin-left: 14px;
		}
		.map-type2{
			font-size: 14px;		
			display: inline-block;
			padding: 6px 14px;
			border-radius: 20px;
			background-color: #fdd4e0;
			color: #e57095;
			margin-left: 14px;
		}
		.map-phone{
			background-color: #fff;
			width: 100%;	
			padding: 0 10px;
			height: 60px;
			// margin: 20px 0;
			button{
				width: 100%;
				height: 40px;
				text-align: center;
				line-height: 40px;
				border:1px solid #5a57b9;
				color:#5a57b9;
				margin-top: 10px;
			}
		}
		.phone{
			width: 50px;
			height:36px;
			background-color: #5a57b9;
			margin-top: 2px;
			margin-right: 5px;
			border-radius: 20px;
			img{
				margin: auto;
			}
		}
		.map-bg1{
			background-image: url('/@/assets/homePageMz/di/map-bg1.png');
			background-repeat: no-repeat;
			background-size: 100% 110px;
		}
		.map-bg2{
			background-image: url('/@/assets/homePageMz/di/map-bg2.png');
			background-repeat: no-repeat;
			background-size: 100% 110px;
		}
	}
	.popup-ctn {
		width: 100%;
		height: calc(100vh - 50px);
		position: fixed;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
		backdrop-filter: blur(2px);
	    z-index: 10000000;
		.popup {
			width: 100%;
			border-radius:  12px 12px 0 0;
			background-color: #fff;
			padding: 12px 12px 16px 12px;
			box-sizing: border-box;	
			position: absolute;
			bottom: 0;
		}
		.popup-title{
			font-size: 18px;
			color: #000;
			font-weight: 550;
			margin-bottom: 20px;
		}
		.popup-list{
			height: 50px;
			margin-bottom: 14px;
			background-color: #eeeffa;
			border-radius: 20px;
			line-height: 50px;
			padding-left: 20px;
			font-size: 18px;
			display: flex;
			justify-content: space-between;
			align-items: center;
		}
	}
</style>

