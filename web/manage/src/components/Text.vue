<template>
  <div class="text-black" style="position:relative;"
	:style="!inversion && isLoading ?';':''">
    <div ref="textRef" >
   <!-- 知识库 -->
    <div v-if="!inversion" style="padding:12px 16px;">
      <div class="markdown-body icon-box" v-if="!asRawText && isLoading">
					<div class="loading-icon" style="display: flex;align-items: center;">
						<img class="rotating-image" style="height: 20px;margin-right: 12px;" src="@/assets/loading.svg" alt="" />
						<div class="icon4">正在思考中</div>
					</div>
				</div>
				<div  class="markdown-body icon-box">
						<!-- <div class="icon5" style="display:flex;align-items: center;"><img style="height: 28px;margin-right: 12px;" src="@/assets/yhdwb.svg" alt="" />已回答完毕</div>
					  <iconpark-icon class="arrow" size="18" name='arrow-up-s-line'></iconpark-icon> -->
				</div>
				<div class="suyuan" v-if="progressList.length>0 && answerFlag">
					<div v-for="(item,index) in progressList" :key="index">
						<div v-if="item.progress=='问题理解'">
							<div class="flex" style="align-items: center;">
								<img style="height: 18px;margin-right: 12px;" src="./yljwt.svg" alt="" /><span class="suyuan-c0">{{item.progress}}</span>
								<iconpark-icon @click="showRelateSource1 = !showRelateSource1" class="arrow" size="18" :name="showRelateSource1?'arrow-up-s-line':'arrow-down-s-line'"></iconpark-icon>
							</div>
							<div class="suyuan-mt10" v-show="showRelateSource1">
								<span class="suyuan-btns" v-for="(itm,idx) in item.resultList" :key="idx">{{itm}}</span>
							</div>
						</div>
						<div v-else-if="item.progress=='mcp服务'">
							<div class="flex suyuan-mt10" style="align-items: center;" v-if="item.resultList.length>0">
								<img style="height: 18px;margin-right: 12px;" src="./mcp.svg" alt="" /><span class="suyuan-c0">{{item.progress}}</span>
								<iconpark-icon @click="showRelateSource3 = !showRelateSource3" class="arrow" size="18" :name="showRelateSource3?'arrow-up-s-line':'arrow-down-s-line'"></iconpark-icon>
							</div>
							<div class="suyuan-mt10" v-show="showRelateSource3">
								<div class="suyuan-box">
								<div v-for="(itm,idx) in item.resultList" :key="idx">
									<div class="flex" style="align-items: center;margin-bottom: 20px;" v-if="idx==0">
										<img style="height: 18px;margin-right: 12px;" src="./tools-line.svg" alt="" /><span class="suyuan-c0">{{itm.name}}</span>
										
									</div>
									
									<div style="margin-bottom: 10px;">{{(idx & 1) === 0 ? "输入" : "输出"}}</div>
									<div class="suyuan-mcp">{{itm.content}}</div>
								</div>	
								</div>
							</div>
						</div>
						<div v-else-if="item.progress=='检索知识库'">
							<div class="flex suyaun-lwjs suyuan-mt10" style="align-items: center;flex-wrap: wrap;"  v-if="item.resultList.filter(el => el.url).length>0">
								<img style="height: 18px;margin-right: 12px;" src="./yjszsk.svg" alt="" /><span class="suyuan-c0">{{item.progress}}</span>
								<span class="suyuan-c9">已获取{{item.resultList.filter(el => el.url).length}}个</span>
								<span class="suyuan-c6 suyuan-max-w200" :title="itm1" v-for="(itm1,idx) in progressList[0].resultList" :key="idx">{{itm1}}</span>
								<span class="suyuan-c9">相关材料</span>
								<iconpark-icon @click="showRelateSource4 = !showRelateSource4" class="arrow"  size="18" :name="showRelateSource4?'arrow-up-s-line':'arrow-down-s-line'"></iconpark-icon>
							</div>
							<div class="suyuan-mt10" v-show="showRelateSource4">
								<!-- <div class="suyaun-xgwy suyaun-p20 suyuan-mt10" v-for="(itm,idx) in item.resultList" :key="idx">{{idx}} <span class="suyuan-c6">{{itm}}</span><span class="suyuan-c9"></span></div> -->
								<div style="margin-left: 25px;">
								<div v-for="(itm,idx) in item.resultList.filter(el => el.url)" :key="idx" @click="previewPic(itm)">
									<div class="flex suyuan-box-hover" style="align-items: center;margin-bottom: 5px;cursor: pointer;height: 30px;">
										<span class="suyuan-c6" style="margin-left:0;margin-right:10px;">{{idx+1}}</span>
										<iconpark-icon class="arrow"  size="18" name="attachment-line"></iconpark-icon>
										<div class="on"><span class="suyuan-c6 ":title="itm.title">{{itm.title}}</span></div>
										<!-- <div class="on"><span class="suyuan-c9">【{{itm.module[0]}}】</span></div> -->
									</div>
									
									<!-- <div style="margin-bottom: 10px;">{{(idx & 1) === 0 ? "输入" : "输出"}}</div> -->
									<!-- <div class="suyuan-mcp">{{itm.content}}</div> -->
								</div>	
								</div>
							</div>
						</div>
						<div v-else>
							<div class="flex suyaun-lwjs suyuan-mt10" style="align-items: center;flex-wrap: wrap;"  v-if="item.resultList.length>0"><img style="height: 18px;margin-right: 12px;" src="./yhwjs.svg" alt="" /><span class="suyuan-c0">{{item.progress}}</span>
							<span class="suyuan-c9">已获取{{item.resultList.length}}个</span>
							<span class="suyuan-c6 suyuan-max-w200" :title="itm1" v-for="(itm1,idx) in progressList[0].resultList" :key="idx">{{itm1}}</span>
							<span class="suyuan-c9">相关材料</span>
							<iconpark-icon @click="showRelateSource2 = !showRelateSource2" class="arrow"  size="18" :name="showRelateSource2?'arrow-up-s-line':'arrow-down-s-line'"></iconpark-icon>
							</div>
							<div class="suyuan-mt10" v-show="showRelateSource2">
								<!-- <div class="suyaun-xgwy suyaun-p20 suyuan-mt10" v-for="(itm,idx) in item.resultList" :key="idx">{{idx}} <span class="suyuan-c6">{{itm}}</span><span class="suyuan-c9"></span></div> -->
								<div style="margin-left: 25px;">
								<div v-for="(itm,idx) in item.resultList" :key="idx" @click="previewPic(itm)">
									<div class="flex suyuan-box-hover" style="align-items: center;margin-bottom: 5px;cursor: pointer;height: 30px;">
										<span class="suyuan-c6" style="margin-left:0;margin-right:10px;">{{idx+1}}</span>
										<iconpark-icon class="arrow"  size="18" name="attachment-line"></iconpark-icon>
										<div class="on"><span class="suyuan-c6 ":title="itm.title">{{itm.title}}</span></div>
										<!-- <div class="on"><span class="suyuan-c9">【{{itm.module[0]}}】</span></div> -->
									</div>
									
									<!-- <div style="margin-bottom: 10px;">{{(idx & 1) === 0 ? "输入" : "输出"}}</div> -->
									<!-- <div class="suyuan-mcp">{{itm.content}}</div> -->
								</div>	
								</div>
							</div>
						</div>
					</div>
				</div>
				<!--   -->
				<div
					v-if="!asRawText && !inversion && answer"
					class="markdown-body"
					v-html="answer"
				/>
			
      <div>
			
    </div>
		
		</div>
		<div style="display:flex;height:58px;align-items:center;margin-bottom:12px;" v-if="inversion && fileList?.length>0" >
				<div v-for="(item,index) in fileList" :key="index"
				style="display:flex;align-items:center;margin-right:8px;
			 width: 240px;height: 58px;background: #FFFFFF;border-radius: 8px;border: 1px solid #D0DFFD;gap:0">
						<div style="width:56px;height:56px;display:flex;justify-content:center;align-items:center">
							<div style="width:40px;height:40px;display:flex;justify-content:center;align-items:center;">
								<svg t="1744274231278" class="icon" viewBox="0 0 1024 1024" version="1.1"
								xmlns="http://www.w3.org/2000/svg" p-id="4942" width="40" height="40"
								style="width: 100%;height: 100%;"
								v-if="item.fileType == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || item.fileType == 'application/vnd.ms-excel'">
								<path
									d="M658.102857 139.995429a17.408 17.408 0 0 1 0.182857 2.486857v739.035428a18.029714 18.029714 0 0 1-20.845714 17.554286L177.664 835.364571a35.84 35.84 0 0 1-31.414857-35.108571V223.707429a35.84 35.84 0 0 1 31.414857-35.108572l459.702857-63.634286a18.212571 18.212571 0 0 1 20.699429 14.994286zM343.771429 365.714286H256l102.4 146.285714L256 658.285714h87.771429l58.514285-83.602285L460.8 658.285714H548.571429l-102.4-146.285714 102.4-146.285714h-87.771429L402.285714 449.316571 343.771429 365.714286z"
									fill="#18AB66" p-id="4943"></path>
								<path
									d="M658.285714 192.950857h182.857143c20.187429 0 36.571429 15.872 36.571429 35.474286v567.149714c0 19.602286-16.384 35.474286-36.571429 35.474286h-182.857143V192.950857z"
									fill="#8CD5B3" p-id="4944"></path>
							</svg>
							<svg t="1744274189696" class="icon" viewBox="0 0 1024 1024" version="1.1"
								xmlns="http://www.w3.org/2000/svg" p-id="4799" width="40" height="40"
								v-if="item.fileType == 'text/plain'">
								<path
									d="M658.285714 146.285714v146.285715a36.571429 36.571429 0 0 0 36.571429 36.571428l146.249143-0.036571 0.036571 512.365714a36.315429 36.315429 0 0 1-36.315428 36.242286H219.172571A36.571429 36.571429 0 0 1 182.857143 841.435429V182.564571C182.857143 162.523429 199.131429 146.285714 219.172571 146.285714H658.285714z m0 475.428572h-292.571428v73.142857h292.571428v-73.142857z m0-146.285715h-292.571428v73.142858h292.571428v-73.142858z m-182.857143-146.285714h-109.714285v73.142857h109.714285V329.142857z"
									fill="#7E56EB" p-id="4800"></path>
								<path
									d="M658.285714 146.285714l182.857143 182.857143h-146.285714a36.571429 36.571429 0 0 1-36.571429-36.571428V146.285714z"
									fill="#CBBBF7" p-id="4801"></path>
							</svg>
							<svg t="1744273886599" class="icon" viewBox="0 0 1024 1024" version="1.1"
								xmlns="http://www.w3.org/2000/svg" p-id="4513" width="40" height="40"
								v-if="item.fileType == 'application/msword' || item.fileType == 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'">
								<path
									d="M177.700571 188.598857l459.702858-63.634286a18.212571 18.212571 0 0 1 20.882285 17.554286v738.998857a18.029714 18.029714 0 0 1-20.845714 17.554286L177.664 835.364571a35.84 35.84 0 0 1-31.414857-35.108571V223.707429a35.84 35.84 0 0 1 31.414857-35.108572zM475.428571 370.212571v176.859429l-73.142857-70.509714-72.777143 70.875428L329.142857 370.212571H256v283.574858h73.142857l73.142857-70.875429 73.142857 70.875429h73.142858v-283.574858h-73.142858z"
									fill="#2862FF" p-id="4514"></path>
								<path
									d="M658.285714 192.950857h182.857143c20.187429 0 36.571429 15.872 36.571429 35.474286v567.149714c0 19.602286-16.384 35.474286-36.571429 35.474286h-182.857143V192.950857z"
									fill="#93B0FF" p-id="4515"></path>
							</svg>
							<svg t="1744274063569" class="icon" viewBox="0 0 1024 1024" version="1.1"
								xmlns="http://www.w3.org/2000/svg" p-id="4656" width="40" height="40"
								v-if="item.fileType == 'application/pdf'">
								<path
									d="M655.433143 146.285714l0.365714 0.402286L655.835429 292.571429a36.571429 36.571429 0 0 0 32.329142 36.315428l4.242286 0.256 142.189714-0.036571 0.109715 511.744c0 20.370286-15.981714 36.864-35.620572 36.864H224.914286a36.205714 36.205714 0 0 1-35.620572-36.278857V182.564571c0-20.041143 16.091429-36.278857 35.84-36.278857h430.299429zM529.92 347.428571h-71.68c0 57.673143-16.347429 125.696-43.958857 188.452572-27.648 63.012571-65.024 116.918857-103.936 148.699428l42.276571 58.989715c104.96-71.387429 221.184-120.32 333.568-103.936l16.420572-70.912c-95.817143-32.548571-172.690286-130.230857-172.690286-221.293715z m-24.941714 151.003429a351.378286 351.378286 0 0 0 61.184 71.899429c-35.218286 6.436571-69.705143 17.005714-103.094857 30.464a644.169143 644.169143 0 0 0 41.874285-102.4z"
									fill="#F50458" p-id="4657"></path>
								<path
									d="M655.835429 146.285714l178.834285 182.857143h-142.262857a36.571429 36.571429 0 0 1-36.571428-36.571428V146.285714z"
									fill="#FB9BBC" p-id="4658"></path>
							</svg>
							<svg t="1745819200537" class="icon" viewBox="0 0 1024 1024" version="1.1"
							 xmlns="http://www.w3.org/2000/svg" p-id="4958" width="40" height="40"
							 v-if="item.fileType=='image/jpeg' || item.fileType == 'image/png'"><path d="M234.678857 476.379429l79.213714-79.250286 217.929143 217.892571 138.642286-138.642285 118.857143 118.857142v-360.594285H234.678857v241.737143zM195.035429 155.428571h633.929142c21.869714 0 39.606857 17.737143 39.606858 39.606858v633.929142c0 21.869714-17.737143 39.606857-39.606858 39.606858H195.035429a39.606857 39.606857 0 0 1-39.606858-39.606858V195.035429c0-21.869714 17.737143-39.606857 39.606858-39.606858z" fill="#29BBB6" p-id="4959">
							</path><path d="M650.678857 432.749714a59.428571 59.428571 0 1 1 0-118.857143 59.428571 59.428571 0 0 1 0 118.857143z" fill="#94DDDA" p-id="4960"></path>
						</svg>
						</div>
						</div>
            <!-- 右边头部 -->
						<div style="display:flex;flex-direction:column ;width:168px;height:40px;justify-content:center">
							<!-- .slice(0,item.fileName.lastIndexOf('.')) -->
						<div style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;height:20px;font-family: MiSans, MiSans;font-weight: 400;font-size: 14px;color: #383D47;text-align: left;font-style: normal;margin-right:8px;">
								{{item.fileName.slice(0,item.fileName.lastIndexOf('.'))}}
							</div>
              <!-- 右边底部 -->
							<div style="height: 20px;display:flex;justify-content:flex-start;flex-direction:row">

							<div style="font-family: MiSans, MiSans;
font-weight: 400;
font-size: 12px;
color: #B4BCCC;
text-align: center;
font-style: normal;
display:flex;
justify-content:center;
align-items:center;
 width: fit-content; 
 margin-right:8px;">
							<span>
								{{item.fileName.slice(item.fileName.lastIndexOf('.')+1)}}
							</span>
							</div>

							<div style="
							font-family: MiSans, MiSans;
font-weight: 400;
font-size: 12px;
color: #B4BCCC;
text-align: left;
font-style: normal;
display:flex;
justify-content:center;
align-items:center;">{{changeFileSize(item.fileSize)}}</div>
							</div>

						</div>

						</div>
			</div>
		<div
				v-if="inversion && !isContainFile"
				class="whitespace-pre-wrap"
				:class="inversion && (paragraph || imgUrl) ? 'textBorder' : ''"
				style="display: flex;align-items: center;color: rgb(255, 255, 255);"
				v-html="text"
			/>
    </div>
		<slot name="footer" v-if="!asRawText && answer"></slot>
		<slot name="question"></slot>
  </div>
</template>

<script>
	var that = this
import hljs from 'highlight.js';
import MarkdownIt from 'markdown-it';
export default {

props:['asRawText','text','inversion','error','syFlag','loading',,'citations',
	'dialogueFileIds','dialogueFolderIds','pragraph','skillId','params','isHighlightCode','imgUrl','matterGuide',
	'finishReason','businessScenarioLists','businessScenario','index','item','paragraph','closeTalk'
],
data(){
return {
	answer:'',
	question:'',
	answerFlag:false,
	isLoading:true,
	fileList:[],
	isMarkdownImg:[],
	//控制溯源相关的
	progressList:[],
	//控制是否查看联网消息的
	showRelateSource1:false,
	showRelateSource2:false,
	showRelateSource3:false,
	showRelateSource4:false,
	mdi:new MarkdownIt({
	html: true,
	linkify: false,
	highlight(code, language) {
		const validLang = !!(language && hljs.getLanguage(language));
		if (validLang) {
			const lang = language ?? '';		
		    return `<pre class="code-block-wrapper"><div class="code-block-header"><span class="code-block-header__lang">${lang}</span></div><code class="hljs code-block-body ${lang}">${hljs.highlight(code, { language: lang }).value}</code></pre>`
		}
		return `<pre class="code-block-wrapper"><div class="code-block-header">${lang}</div><code class="hljs code-block-body ${lang}">${hljs.highlight(code, { language: lang }).value}</code></pre>`
		
	},
}),
}

},
mounted() {
	this.question = this.item.question
		if(this.$store.state.workflow.tempFileList[this.index]?.length>0){
			this.$store.state.workflow.tempFileList[this.index].forEach(item=>{
				this.fileList.push(item)
			})
			this.fileList = [...this.fileList]
		}
},
watch:{
	item:{
		deep:true,
		handler:function(val,oldVal){
			// if(this.answer){
			// 	this.answer = val.answer.replace(/[\*\-]/g, '')
			// }else{
			// 	this.answer = val.answer
			// }
      this.progressList = val && val.progressList && val.progressList.length ? val.progressList : this.progressList
			this.answer = val.answer
			if(this.answer){
				let strsss = this.answer?this.answer.replace(/\\n/g, '\n'):''
			const value = strsss?this.textToMarkdown(strsss,this.citations):''
			let htmlStr = this.mdi.render(value)
			htmlStr = this.addLoadEventToImgTags(htmlStr);
			this.answer = htmlStr
			// this.answer=htmlStr
			}
			this.answerFlag = val.answerFlag
			if(val.finishReason == 'ANSWER_COMPLETE' || val.loading == false){
				this.isLoading=false
				this.$store.state.workflow.dialogueInputLoading = false
			}	else{
				this.isLoading = true
				
			}
		}
	},
},
computed:{
	isContainFile(){
		if (!this.answer || this.inversion || !this.question) return '';
	if(/^\d+$/.test(text.value)){
		text.value = `${text.value}`
	}
		const matches = text.value.match(/@([^@]+\.(pdf|docx|doc|txt))/g);
	return matches?.length;
	
	},
	
},
methods:{
  previewPic(url){
    if(url){
      window.open(url, '_blank');
    }else{
      this.$message.warning('未配置url链接');
      return false;
    }
  },
	highlightBlock(str, lang){
	let langStr = '';
	if (lang) {
		langStr = `<span class="code-block-header__lang">${lang}</span>`;
	}
	return `<pre class="code-block-wrapper"><div class="code-block-header">${langStr}<span class="code-block-header__copy" onclick="copyBtn()">复制代码</span><span class="code-block-header__theme" onclick="themeBtn()">主题切换</span></div><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
},
	textToMarkdown(text, citations){
	// 判断json格式
	try {
		const jsonObj = JSON.parse(text); // 将字符串解析为一个对象
    if(typeof jsonObj === 'object' && jsonObj !== null){
      const jsonFormatted = JSON.stringify(jsonObj, null, 2);
		  text = '```json\n' + jsonFormatted + '\n```';
    }
	} catch (error) {}

	// 截取:::r 结束的字符串
	if (text.endsWith('\n:')) {
		text = text.slice(0, -3);
	}
	if (text.endsWith('\n::')) {
		text = text.slice(0, -4);
	}
	if (text.endsWith('\n:::')) {
		text = text.slice(0, -5);
	}
	if (text.endsWith('\n:::r')) {
		text = text.slice(0, -6);
	}
	if (text.endsWith('\n:::r ')) {
		text = text.slice(0, -7);
	}

	// 初始化一个空字符串，用于存储结果
	let result = '';
	if (!citations) {
		return text;
	}
	// 是否包含图片
	var regexImage = /!\[.*?\]\(.*?\)/g;
	// isMarkdownImg.value = regexImage.test(text);
	this.isMarkdownImg = text.match(regexImage) || [];

	// 遍历对象的 res_info 属性，获取每个键值对
	for (const key in citations) {
		// 获取当前键值对的值，即一个子对象
		const value = citations[key];
		const { no, fileUrl = '#', fileName } = value;
		// 将子对象的 title 和 context 属性拼接成 markdown 的链接引用格式，并添加到结果字符串中
		if (route.path.indexOf('chat') != -1) {
			result += `[${no}]: ${fileUrl ? encodeURI(fileUrl) : null} \'${fileName}\'\n\n`;
		} else {
			result += `[${no}]: ${fileUrl ? null : null} \'${fileName}\'\n\n`;
		}
	}
	// 在结果字符串末尾添加一个空行
	result += '\n';
	// 将对象的 res 属性的值，即一个字符串，替换其中的数字为上标引用，并在每个数字后面加上一个空格，并添加到结果字符串中
	result += text.replace(/\[(\d+)\]/g, (match, p1) => {
		// 使用回调函数替换匹配的内容
		const num = Number(p1); // 将 $1 转换为数字
		// return `<a href="${res_info[num].title}">${res_info[num].context || ''}</a>`; // 返回对应的 context
		return `[${num}][${num}]`; // 返回对应的 context
	});
	// 返回结果字符串
	return result;
},
addLoadEventToImgTags(htmlString){
	var regex = /(<img\s+[^>]*)(>)/g;
	var replacement = `$1  onclick="showPreviewImg(event)" $2`;
	
	// var replacement = `$1  onclick="showPreviewImg(event)" onload="document.querySelector('.center-side').scrollTop = 999999999"$2`;
	// var replacement = `$1  onclick="showPreviewImg(event)" $2`;
	var newHtmlString = htmlString.replace(regex, replacement);

	return newHtmlString;
},
render(tokens,idx){
	var m = tokens[idx].info.trim().match(/^r\s+(.*)$/);
		if (tokens[idx].nesting === 1) {
			const arr = m[1].split(',');
			let result = '';
			// 生成一个 10 到 999 之间的随机整数
			const random = Math.floor(Math.random() * (999 - 10 + 1)) + 10;
			if (arr[1]) {
				result = '<span>相关：</span>';
				recommendKey.forEach((element, index) => {
					const text = arr[0].split('.')[1];
					const key = element.type === 1 ? `请对${text} 的${element.key}，详细列举：` : `关于${text} 的${element.key}，详细列举：`;
					result += `
						<button type="button"  onclick='setsendMessage("${key}",${JSON.stringify(element.param)},${
						props.params
					})'  class="w-btn w-btn-outline recommend-item recommend-item-${index}">
							${element.name}
						</button>
					`;
				});
			}
			if (!chatMode.value) {
				const key = arr[0];
				const params = {};
				return `
					<div  class="recommend-title target-${idx * random} recommend-title-pointer" onclick='setsendMessage("${key}",${JSON.stringify(params)},${
					props.params
				})'>
					${arr[0]}
					</div>
				`;
			} else {
				const hotStr = arr[1] ? `<xy-button  class="recommend-hot">${hotIcon}${arr[1]}</xy-button>` : ``;
				// 返回一个带有标题和隐藏按钮的div元素
				return `
					<div  class="recommend-title target-${idx * random}">
					${arr[0]}
					${hotStr}
					</div>
					<div class="recommend-popup" style="margin-bottom:3px"  target=".target-${idx * random}" type="custom" >
					
					${result}
					</div>
					`;
			}
		} else {
			return '';
		}
},
changeFileSize(fileSize){
	if (fileSize < 1024) {
		fileSize = fileSize + 'K'
	} else if (fileSize < 1024 * 1024) {
		fileSize = (fileSize / 1024).toFixed(1) + 'KB'
	} else if (fileSize > (1024 * 1024)) {
		fileSize = (fileSize / (1024 * 1024)).toFixed(1) + 'MB'
	}
	return fileSize
}
}
}
</script>
<style lang="scss">
.reason-cont{
	padding:0 0 0 13px;
color:#8b8b8b;
line-height: 26px;
position:relative;
font-size: 13px;
.reason-line{
	border-left: 2px solid #e5e5e5;
	height: calc(100% - 10px);
	margin-top:5px;
	position:absolute;
	top:0;
	left:0;
}
.reason-paragraph{
  margin:1em 0;
}
}
.reasoncont{
	padding:0 0 0 13px;
	color:#8b8b8b;
line-height: 26px;
position:relative;
font-size: 13px;
.reasonline{
	border-left: 2px solid #e5e5e5;
	height: calc(100% - 10px);
	margin-top:5px;
	position:absolute;
	top:0;
	left:0;
}
.reasonparagraph{
  margin:1em 0;
}
}
.markdown-body{
	font-size: 16px!important;
	p{
		white-space: pre-wrap;
		word-break: break-all;
		margin:0.4em 0;
		line-height: 1.75em;
	}
	strong{
	font-weight: bolder!important;
}
h1{
	font-size: 1.3em;
	font-weight: bold;
	line-height: 2.2em;
}
h2{
	font-size: 1.2em;
	font-weight: bold;
	line-height: 2.2em;
}
h3{
	font-size: 1.1em;
	font-weight: bold;
	line-height: 2.2em;
}
p{
	white-space: pre-wrap;
	word-break: break-all;
	margin:0.4em 0;
	line-height: 1.75em;
}
ol{
	list-style-type: decimal;
	padding-left:1.6rem;
	li{
	white-space: normal;
	word-break: break-all;
	&::marker{
		unicode-bidi: isolate;
		font-variant-numeric: tabular-nums;
		text-transform: none;
		text-indent: 0px!important;
		text-align: start!important;
		text-align-last: auto !important;
	}
}
}
ul{
	list-style-type: disc;
	padding-left:1rem;
	li{
		white-space: normal;
		word-break: break-all;
		&::marker{
			unicode-bidi:isolate;
			font-variant-numeric: tubalar-nums;
      text-transform: none;
			text-indent: 0px!important;
			text-align: start!important;
			text-align-last:auto!important ;
		}
	}
}
hr{
	height: 0;
	border-top-width: 1px;
	color:inherit;
}
table{
	border:1px solid rgba(231,229,238,0.7);
	border-collapse: collapse;
	border-radius: 6px;
	border-spacing: 0;
	box-sizing: border-box;
	font-size: 15px;
	line-height: 1.75;
	overflow:auto;
	thead{
		tr{
			border:1px solid #efefef;
			th{
				background-color:#f5f5fa;
				border:1px solid #efefef;
				color:#001846;
				font-family: PingFangSc-Medium;
				font-weight: 500;
				letter-spacing: 0;
				padding:10px 6px;
				text-align: center;
			}
		}
	}
	tbody{
		tr{
			&:nth-child(odd){
				background-color: #fdfdfe;
			}
			td{
   border:1px solid #efefef;
	 color:#120649;
	 font-family: PingFangSc-Regular;
	 font-weight: 400;
	 letter-spacing: 0;
	 max-width: 220px;
	 min-width: 60px;
	 padding:10px;
	 text-align: center;
			}
		}
	}
}
}
</style>
<style lang="scss" scoped>
// @import '../assets/scss/style.scss';
.icon{
	width: 100%;
	height: 100%;
}
.textBorder{
	border-top:1px solid #ffffff;
	padding-top:4px;
}
.whitespace-pre-wrap{
	font-size: 16px;
	line-height: 1.625;
	white-space: pre-wrap;
	word-break: break-all;
}
.text-black{
  .leading-relaxed{

  }
  .break-words{

  }
  .markdown-body {
	font-size: 16px!important;
line-height: 28px;
	.loading-icon {
		height: 28px;
		width: 108px;
		position: relative;
		margin-right: 10px;
    .rotating-image {
  animation: rotate 1s linear infinite; /* 应用动画 */
}
		.icon1 {
			position: absolute;
			left: 0;
			top: 63%;
			transform: translateY(-50%);
			width: 4px;
			height: 4px;
			border-radius: 50%;
			background: rgba(32, 101, 214, 0.2);
		}

		.icon2 {
			position: absolute;
			left: 10px;
			top: 8px;
			transform: translateX(-50%);
			width: 4px;
			height: 4px;
			border-radius: 50%;
			background: rgba(32, 101, 214, 0.5);
		}

		.icon3 {
			position: absolute;
			left: 16px;
			top: 63%;
			transform: translateY(-50%);
			width: 4px;
			height: 4px;
			border-radius: 50%;
			background: rgba(32, 101, 214, 0.2);
		}

		.icon4 {
			position: absolute;
			right: 0;
			top: 50%;
			transform: translateY(-50%);
			font-size: 16px;
		}
       
		.active {
			background: #fff;
		}
	}

	// .recommend-container:nth-child(n+5) .recommend-hot img { display: none; }
}
.suyuan{
	margin-top: 20px;
	box-sizing: border-box;
	.suyuan-btns{
		padding: 4px 12px;
		background: #f3f3f3;
		margin-right: 10px;
		border-radius: 16px;
		display: inline-block;
		font-size: 14px;
		margin-bottom: 20px;
	}
	.suyuan-box{
		padding: 12px;
		background: #f3f5f7;
		word-break: break-word;
		max-height:300px;
		overflow-y: auto;
	}
	.suyuan-mcp{
		width: 100%;
		padding: 25px;
		background: #fff;
		border-radius: 6px;
		display: inline-block;
		font-size: 14px;
		margin-bottom: 20px;
		box-sizing: border-box;
		
	}
	.suyuan-c0{
		color: #000;
		font-weight: 550;
	}
	.suyuan-c6{
		color: #666;
		margin-left: 10px;
		font-size: 14px;
	}
	.suyuan-c9{
		color: #999;
		margin-left: 10px;
		font-size: 14px;
	}
	// .suyaun-p20{
	// 	margin-left: 20px;
	// }
	.suyuan-mb20{
		margin-bottom: 20px;
	}
	.suyuan-mt10{
		margin-top: 20px;
	}
	.suyuan-mr10 {
		margin-right: 10px;
	}
	.suyuan-box-hover:hover{
		// color: #2055f4;	
		.on{
			border-bottom: 1px solid #2055f4;
			span{
				color: #2055f4;
			}
			
		}
	}
	.suyuan-max-w200{
		// display: inline-block;
		max-width:200px;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 1;
		overflow: hidden;
		text-overflow: ellipsis;
	}
}
.icon5 {
        	font-size: 18px;
			color: #000;
        }
.icon-box {
	display: flex;
	align-items: center;

}

}
</style>