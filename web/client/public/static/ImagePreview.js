/*
* 预览图片 Object
* 2015-05-23 17:25
*
*/
function ImagePreview(){
	var that=this;
	//用来存储图片数据
	var IMGS={
		"num":0,
		"imgs":{}
	};
	var IMGP_BG=null;
	// var IMGP_P_BG=null;
	var IMGSHOW_BG=null;
	var IMGSHOW_BOX=null;
	// var IMGSHOW_NEXT=null;
	// var IMGSHOW_LAST=null;
	var IMGSHOW_CLOSE=null;
	// var FullScreen=null;
	var IMGSHOW_NOW=null;
	var IMGP_BOX=null;
	var IMGP_IMGS=null;
	var IMGP_NEXT=null;
	var IMGP_LAST=null;
	var IMGP_HTML="";
	var nowNum=0;//当前预览图片序号
	var moveW=0;
	_start();
	//初始化获得图片数据
	this.Image=function(imgObjBox){
		
		// var len=imgObjBox.children.length;
		// IMGP_HTML="";
		// for(var i=0;i<len;i++){
		// 	IMGS["imgs"][i]={"src":''}
		// 	IMGS["imgs"][i]["src"]=imgObjBox.children[i].firstElementChild.src;
		// 	IMGP_HTML+='<div class="imgp_k"><img data-num="'+i+'"src="'+imgObjBox.children[i].firstElementChild.src+'" class="imgp"/></div>';
		// }
		// IMGS["num"]=len;
		// IMGP_IMGS.innerHTML=IMGP_HTML;
		// IMGP_IMGS.style.width=len*(100+7)+"px";
		// IMGSHOW_NOW.src=IMGS["imgs"][0]["src"];//显示第一张图片
		// IMGP_IMGS.onclick=function(e){
		// 	//获取data-num
		// 	var n=e.target.dataset.num;//ie不识别
		// 	if(n){
		// 		nowNum=n;
		// 		that.show(nowNum);
		// 	}
		// }
	}
	IMGP_BG.onmousewheel=function(e){
		return false;
	}
	//显示图片
	this.show=function(n,src){
		nowNum=n;
		IMGP_BG.style.display="block";
		IMGSHOW_NOW.src= src;
	}
	//预览下一张
	// IMGSHOW_NEXT.onclick=function(){
	// 	nowNum++;
	// 	if(nowNum>(IMGS["num"]-1)){
	// 		nowNum=0;
	// 	}
	// 	that.show(nowNum);
	// }
	//预览上一张
	// IMGSHOW_LAST.onclick=function(){
	// 	nowNum--;
	// 	if(nowNum<0){
	// 		nowNum=IMGS["num"]-1;
	// 	}
	// 	that.show(nowNum);
	// }
	//关闭预览
	IMGSHOW_CLOSE.onclick=function(){
		document.body.style.overflow="auto";
		IMGP_BG.style.display="none";
		document.body.removeChild(IMGP_BG);
	}
	//点击外层关闭
	IMGSHOW_NOW.onclick=function(event){
		event.stopPropagation();
	}
	//关闭预览
	IMGSHOW_BOX.onclick=function(){
		document.body.style.overflow="auto";
		IMGP_BG.style.display="none";
		document.body.removeChild(IMGP_BG);
		
	}
	IMGP_NEXT.onclick=function(){
		var w_bg=IMGP_BOX.offsetWidth;
		var w=IMGP_IMGS.offsetWidth;
		var L=-IMGP_IMGS.offsetLeft;
		var wr=w-(L+w_bg);
		var moveL= wr>w_bg ? w_bg : wr;
		console.log(w_bg+" "+w+" "+L+" "+moveL+" "+wr);
		if(wr==0){
			IMGP_IMGS.style.left=0+"px";
		}else{
			IMGP_IMGS.style.left=-(L+moveL)+"px";
		}
	}
	IMGP_LAST.onclick=function(){
		var w_bg=IMGP_BOX.offsetWidth;
		var w=IMGP_IMGS.offsetWidth;
		var L=-IMGP_IMGS.offsetLeft;
		var wl=L;
		var moveL= wl>w_bg ? w_bg : wl;
		console.log(w_bg+" "+w+" "+L+" "+moveL+" "+wl);
		if(wl==0){
			IMGP_IMGS.style.left=-(w-w_bg)+"px";
		}else{
			IMGP_IMGS.style.left=-(L-moveL)+"px";
		}
		
	}
	function _start(){
		//创建预览照片盒子
		IMGP_BG=document.createElement("div");
		IMGP_BG.id="IMGP_BG";
		document.body.appendChild(IMGP_BG);
		//大图预览背景
		IMGSHOW_BG=document.createElement("div");
		IMGSHOW_BG.id="IMGSHOW_BG";
		IMGP_BG.appendChild(IMGSHOW_BG);
		//大图预览盒子
		IMGSHOW_BOX=document.createElement("div");
		IMGSHOW_BOX.id="IMGSHOW_BOX";
		IMGSHOW_BG.appendChild(IMGSHOW_BOX);
		//大图预览按钮下
		// IMGSHOW_NEXT=document.createElement("div");
		// IMGSHOW_NEXT.id="IMGSHOW_NEXT";
		// IMGSHOW_BG.appendChild(IMGSHOW_NEXT);
		//大图预览按钮上
		// IMGSHOW_LAST=document.createElement("div");
		// IMGSHOW_LAST.id="IMGSHOW_LAST";
		// IMGSHOW_BG.appendChild(IMGSHOW_LAST);
		//大图预览按钮关闭
		IMGSHOW_CLOSE=document.createElement("div");
		IMGSHOW_CLOSE.id="IMGSHOW_CLOSE";
		IMGSHOW_CLOSE.innerHTML="X";
		IMGSHOW_BG.appendChild(IMGSHOW_CLOSE);
		//全屏控制按钮
		// FullScreen=document.createElement("div");
		// FullScreen.id="FullScreen";
		// FullScreen.className="notFullScreen";
		// IMGSHOW_BG.appendChild(FullScreen);
		//正在预览
		IMGSHOW_NOW=document.createElement("img");
		IMGSHOW_NOW.id="IMGSHOW_NOW";
		IMGSHOW_BOX.appendChild(IMGSHOW_NOW);
		//--------------------------------
		// //创建待预览图片背景
		// IMGP_P_BG=document.createElement("div");
		// IMGP_P_BG.id="IMGP_P_BG";
		// IMGP_BG.appendChild(IMGP_P_BG);
		//创建待预览图片盒子背景
		IMGP_BOX=document.createElement("div");
		IMGP_BOX.id="IMGP_BOX";
		// IMGP_P_BG.appendChild(IMGP_BOX);
		//创建待预览图片盒子
		IMGP_IMGS=document.createElement("div");
		IMGP_IMGS.id="IMGP_IMGS";
		IMGP_BOX.appendChild(IMGP_IMGS);
		//创建待预览盒子按钮上
		IMGP_LAST=document.createElement("div");
		IMGP_LAST.id="IMGP_LAST";
		// IMGP_P_BG.appendChild(IMGP_LAST);
		//创建待预览盒子按钮下
		IMGP_NEXT=document.createElement("div");
		IMGP_NEXT.id="IMGP_NEXT";
		// IMGP_P_BG.appendChild(IMGP_NEXT);

		var bodyW=document.documentElement.clientHeight;
		IMGSHOW_BG.style.height=(bodyW)/bodyW*100+"%";
		IMGSHOW_BOX.style.lineHeight=(bodyW-5)+"px";
		// IMGSHOW_NEXT.style.top=((bodyW)-400)/2+"px";
		// IMGSHOW_LAST.style.top=((bodyW)-400)/2+"px";
		//IMGSHOW_NOW.src="./Uploads/3.0_banner.jpg"
	}
	window.onresize=function(){
		var bodyW=document.documentElement.clientHeight;
		IMGSHOW_BG.style.height=(bodyW)/bodyW*100+"%";
		IMGSHOW_BOX.style.lineHeight=(bodyW-5)+"px";
		// IMGSHOW_NEXT.style.top=((bodyW)-400)/2+"px";
		// IMGSHOW_LAST.style.top=((bodyW)-400)/2+"px";
	}
	function _setCss(obj,css){
		for(var v in css){
			obj.style[v]=css[v];
		}
	}


}