<script setup>
import { ref, onBeforeMount , onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router'
import {queryAllMapKj } from '/@/api/llmz';
import AMapLoader from "@amap/amap-jsapi-loader";
import { debounce } from 'lodash';
 const router = useRouter();
 const route = useRoute();
 const mapTitle = ref('');
 const mapType = ref('');
 const mapText = ref('');
 const mapImg = ref('');
 const mapObj = ref({});
 const mapList = ref([]);
 const mapPhoneList = ref([]);
let map = null;
import image1 from '/src/assets/homePageMz/di/map-icon3.png';
import image2 from '/src/assets/homePageMz/di/map-icon3.png';
 // 获取产业类型
const purpose = ref('');
const getqueryAllMap = (params) => {
 	queryAllMapKj(params).then((res) => {
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
 				     mapList.value = res.data
 				      // 添加一些分布不均的点到地图上,地图上添加三个点标记，作为参照
 				      res.data.forEach(function(marker) {
						  
 				         const markerInstance = new AMap.Marker({
 				              map: map,
 				              icon: new AMap.Icon({              
 				              image: image1,	              
 				              size: new AMap.Size(30, 30), //图标大小	              
 				              imageSize: new AMap.Size(30, 30)	              
 				              }),
 				              position: [marker.longitude, marker.latitude],
 				              offset: new AMap.Pixel(-13, -20)
 				          });
 						  markerInstance.on("click", function (e) {
 						    console.log("你点击了Marker",marker);
							mapType.value = marker.rent
							mapText.value = marker.addressDetail
							mapTitle.value = marker.industryName
							mapPhoneList.value = marker.contactInfoResults
							mapImg.value = marker.industryPic
							mapObj.value = marker
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
const isShow1 = ref(false);
const isShowQh = ref(true);
const addPhone = () => {
	 if(mapPhoneList.value.length==1){
		  window.location.href = `tel:${mapPhoneList.value[0].phone}`;
	 }else{
		 isShow.value = true;
		 console.log('有两个');
	 }
};
const addTel = (phone) => {
	window.location.href = `tel:${phone}`;
};
const addQh = () => {
	console.log('有两个');
	// if(isShowQh){
	// 	console.log('1有两个');
	// 	isShowQh.value = false ;
	// }else{
		isShowQh.value = !isShowQh.value ;
	// }
	mapText.value = ''
};
const addMapList = (item) => {
	isShow1.value = true;
	mapObj.value = item
};
const addLjgd = () => {
	isShow1.value = true;
};
const closePopup = () => {
	isShow.value = false;
};
const closePopup1 = () => {
	isShow1.value = false;
};
// 搜索
const inputSearch = debounce(() => {
	getqueryAllMap(purpose.value);
}, 500);

onMounted(() => {
	getqueryAllMap('')
});
</script>
 
<template>
	<div class="outer">
		<div class="info-box">
			<div class="title-box">
				<div class="close">
					<van-icon name="arrow-left" size="20" color="#333" @click="backHome" />
				</div>
				<div class="title">产业空间</div>
				<div class="close" @click="addQh">
					<img v-if="isShowQh" style="height: 24px;" src="/src/assets/homePageMz/di/map-qh.svg" alt="" />
					<img v-else style="height: 24px;" src="/src/assets/homePageMz/di/map-qh-on.svg" alt="" />
				</div>
			</div>
		</div>
		
		<div v-show="isShowQh" id="container" style="width: 100%;height: calc(100% - 50px);">
			
		</div>
		<div  v-show="!isShowQh">
			<div class="map-list">
				<div style="display: flex;margin-bottom: 20px;" @click="addMapList(item)" v-for="(item,index) in mapList" :key="index">
					<div class="map-left">
						<img :src="item.industryPic" />
					</div>
					<div class="map-right">
						<div class="map-title">{{item.industryName}} </div>
						<div class="map-text"><img style="height: 14px;margin-right: 5px;display: inline-block;" src="/src/assets/homePageMz/di/mapAddress.svg" alt="" />{{item.addressDetail}}</div>
						<div class="map-rent"><span class="map-type1">租金</span>{{item.rent}}</div>
					</div>
				</div>
			</div>
		</div>
		<div class="search">
			<iconpark-icon name="search-2-line" size="20" color="#1d2129"></iconpark-icon>
			<input type="text" placeholder="请输入" v-model="purpose" @input="inputSearch" />
		</div>
		<div class="map-bg" v-if="mapText!=''">
			<div style="display: flex;">
				<div class="map-left">
					<img :src="mapObj.industryPic" />
				</div>
				<div>
					<div class="map-title">{{mapObj.industryName}} </div>
					<div class="map-text"><img style="height: 14px;margin-right: 10px;display: inline-block;" src="/src/assets/homePageMz/di/mapAddress.svg" alt="" />{{mapObj.addressDetail}}</div>
					<div class="map-rent"><span class="map-type1">租金</span>{{mapObj.rent}}</div>
				</div>
			</div>
			
			<div class="map-phone" style="padding: 0;">
				<button class="btn1" @click="addPhone">电话联系</button>
				<button class="btn2" @click="addLjgd">了解更多</button>
			</div>
		</div>
		<div class="popup-ctn" @click="closePopup" v-show="isShow">
			<div class="popup" @click.stop>
				<div class="popup-title">电话联系</div>
				<div
					class="popup-list"					
					v-for="(item, index) in  mapPhoneList"
					:key="index"
					
				>
					
					<div><img style="height: 18px;margin-right: 12px;display: inline-block;color: #5a57b9;" src="/src/assets/homePageMz/di/user.svg" alt="" />{{ item.userName }} <span style="margin-left: 20px;">{{ item.phone }}</span></div>
					<button class="phone" @click="addTel(item.phone)">
						<img style="height: 18px;" src="/src/assets/homePageMz/di/phone.svg" alt="" />
					</button>
				</div>
			</div>
		</div>
		<div class="popup-ctn1" @click="closePopup1" v-show="isShow1">
			<div class="popup" @click.stop>
				<div class="popup-img"><img :src="mapObj.industryPic" /></div>
				<div class="popup-box">
					<div class="map-title">{{mapObj.industryName}} </div>
					<div class="map-text"><img style="height: 14px;margin-right: 10px;display: inline-block;" src="/src/assets/homePageMz/di/mapAddress.svg" alt="" />{{mapObj.addressDetail}}</div>
					<div class="map-rent"><span class="map-type1">租金</span>{{mapObj.rent}}</div>
				</div>
				<div class="popup-dv"></div>
				<div class="popup-tab">
					<div>
						<div class="c6">建筑面积(m2)</div>
						<div class="c3">{{mapObj.vacantArea}}</div>
					</div>
					<div>
						<div class="c6">单层面积(m2/层)</div>
						<div class="c3">{{mapObj.areaPerFloor}}</div>
					</div>
					<div>
						<div class="c6">园区空置总面积(m2)</div>
						<div class="c3">{{mapObj.totalArea}}</div>
					</div>
					<div>
						<div class="c6">楼栋总层数</div>
						<div class="c3">{{mapObj.serialNumber}}</div>
					</div>
				</div>
				<div class="popup-dv"></div>
				<div class="popup-footer">
					<div class="popup-footer-item">
						<div class="c3">所属社区</div>
						<div class="c6">{{mapObj.communityName}}</div>
					</div>
					<div class="popup-footer-item">
						<div class="c3">物业类型</div>
						<div class="c6">{{mapObj.collectiveProperty}}</div>
					</div>
					<div class="popup-footer-item">
						<div class="c3">用途</div>
						<div class="c6">{{mapObj.purpose}}</div>
					</div>
					
				</div>
				<div class="map-phone">
					<button class="btn1" @click="addPhone">电话联系</button>
					<button class="btn3" @click="closePopup1">关闭</button>
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
			min-height: 200px;
			background-color: #fff;
			padding: 20px;
			border-radius:  12px 12px 0 0;
		}
		.map-left{
			width: 100px;
			height: 100px;
			margin-right: 10px;
			img{
				width: 100%;
				height: 100%;
			}
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
		.map-rent{
			margin-top: 10px;
			font-size: 18px;
			font-weight: 550;
			color: #6055b5;
		}
		.map-type1{
			font-size: 14px;			
			display: inline-block;
			padding: 4px 10px;
			border-radius: 14px;
			background-color: #e6e9f6;
			color: #5a57b9;
			margin-right: 14px;
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
				width: 47%;
				height: 40px;
				text-align: center;
				line-height: 40px;
				margin-top: 10px;
			}
			.btn1{
				border:1px solid #5a57b9;
				color:#5a57b9;
			}
			.btn3{
				border:1px solid #666;
				color:#000;
				margin-left: 5%;
			}
			.btn2{
				background-color: #5a57b9;
				color:#fff;
				margin-left: 5%;
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
	.popup-ctn1 {
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
			box-sizing: border-box;	
			position: absolute;
			bottom: 0;
		}
		.popup-box{
			width: 100%;
			border-radius:  12px 12px 0 0;
			background-color: #fff;
			padding: 12px 12px 16px 12px;
		}
		.popup-img{
			width: 100%;
			img{
				border-radius:  12px 12px 0 0;
			}
			
		}
		.popup-tab{
			padding: 12px 12px 16px 12px;
			display: flex;
			justify-content: space-between;
		}
		.popup-dv{
			width: 100%;
			height: 10px;
			background-color: #eee;
		}
		.popup-footer-item{
			padding: 12px 12px 16px 12px;
			display: flex;
			justify-content: space-between;
		}
		.c6{
			color: #999;
			font-size: 12px;
		}
		.c3{
			color: #333;
			font-size: 12px;
		}
	}
	.search {
		width: 92%;
		height: 40px;
		background-color: #f7f8fa;
		border-radius: 8px;
		display: flex;
		padding: 10px 12px;
		box-sizing: border-box;
		gap: 9px;
	    z-index: 1000;
	    position: absolute;
		left: 4%;
		top: 50px;
		img {
			width: 20px;
			height: 20px;
		}
	
		input {
			flex: 1;
			height: 20px;
			border: none;
			outline: none;
			background: rgba(0, 0, 0, 0);
	
			&::placeholder {
				color: #c9cdd4;
				font-size: 14px;
				font-family: MiSans Regular;
			}
		}
	}
	.map-list{
		padding: 0 20px;
		height: 90vh;
		overflow-y: auto;
		margin-top: 50px;
	}
	.map-right{
		width: calc(100% - 120px);
	}
</style>

