<template>
  <!-- :style="align=='left'?'':'justify-content:flex-end;'" -->
  <div ref="messageRef" class="flex w-full items-start"
  :class="[{'mb-6':inversion,'mt-6':inversion,'mb-3':!inversion}]"
  :style="align=='left'?'':'justify-content:flex-end;'" 
  
  >
  <!-- 回答的头像 -->
 <div :class="['flex-shrink-0', inversion ? 'mr-3' : 'mr-3']" v-if="qaAllData.robotIcon && align=='left'">
  <div class="avatar">
   <img :src="qaAllData.robotIcon" alt="">
  </div>
 </div>
  <!-- 内容主体 -->
  <div class="contain" :class="align=='right'?'text-bg':'margin1'" style="max-width: 100%;">
  <div class="main-contain"
  :class="[inversion?'flex-row-reverse':'flex-row']" ref="textRef">
   <TextComponent :inversion="inversion" :loading="loading" :citations="citations"
   :dialogueFileIds="dialogueFileIds" :dialogueFoldIds="dialogueFolderIds" :paragraph="paragraph || ''"
    :skillId="skillId" :text="inversion ? question : answer" :as-raw-text="false" :asRawText="inversion"
            :imgUrl="imgUrl" :params="param" :isHighlightCode="isHighlightCode" :businessScenarioLists="businessScenarioLists" :businessScenario="businessScenario" :index="index"
    :item="item" :closeTalk="closeTalk"
            v-if="inversion"
   ></TextComponent>
   <TextComponent v-else :inversion="inversion" :syFlag="syFlag" :loading="loading" :citations="citations"
          :dialogueFileIds="dialogueFileIds" :dialogueFolderIds="dialogueFolderIds" :paragraph="paragraph || ''"
          :skillId="skillId" :text="inversion ? question : answer" :as-raw-text="false" :asRawText="inversion"
          :params="param" :imgUrl="imgUrl"  :matterGuide="matterGuide"
          :finishReason="finishReason"  :isHighlightCode="isHighlightCode" :businessScenarioLists="businessScenarioLists" :businessScenario="businessScenario"
					:index="index"
          :item="item"
          :closeTalk="closeTalk"></TextComponent>
  </div>
  </div>
  <!-- 提问人的头像 -->
 <div>
  <div v-if="qaAllData.userIcon&&align=='right'" :class="['flex-shrink-0', inversion ? 'mr-3' : 'mr-3']">
    <div class="avatar">
   <img :src="qaAllData.userIcon" alt="">
  </div>
  </div>
 </div>
  </div>
</template>

<script>
import TextComponent from './Text.vue'
export default {
data(){
return{
  // inversion:true,
  isHighlightCode:false,
  syFlag:false,
}
},

components:{
  TextComponent
},
props:[
  'qaAllData',
'align',
'inversion',
'answer',
'question',
'clientId',
'finishReason',
'matterGuide',
'businessScenarioLists',
'businessScenario',
'citations',
'createTime',
'id',
'dialogueFileIds',
'dialogueFolderIds',
'paragraph',
'skillId',
'param',
'isLast',
'loading',
'imgUrl',
'sensitive',
'stopChatLoading',
'feedBackStatus',
'suggestOrg',
'answerFlag',
'plainText',
'dialogueId',
'index',
'contentQaType',
'item',
'closeTalk'
],

}
</script>

<style scoped lang="scss">
.margin1{
  border-radius: 0 6px 6px 6px;
  margin-top:8px;
}
.contain{
  font-size: 16px!important;
  align-items: flex-start;
  .tagList {
	color: #9a99aa;

	.tip {
		display: inline-block;
		padding: 0 5px;
	}

	&:last-child {
		.tip {
			display: none;
		}
	}
}
.main-contain{
  display:flex;
  align-items: flex-end;
  gap:0.25rem;
  position:relative;
}
}
.flex-row-reverse{
flex-direction: row-reverse;

}
.flex-row{
  background-color:rgb(247, 248, 250) !important;
  flex-direction: row;
}
.text-xs{
  font-size: 0.75rem;
  line-height: 1rem;
}
.text-right{
  text-align: right;
}
.text-left{
  text-align: left;
}
.mb-2{
  margin-bottom:0.5rem
}
.text-bg{
  height: 100%;
  margin-right:10px;
  background-color: #4689f2;
  color:#fff!important;
  padding:5px 25px;
  border-top-left-radius: 6px;
  border-bottom-left-radius: 6px;
  border-bottom-right-radius: 6px;
}
.flex{
  display: flex;
}
.w-full{
  width: 100%;
}
.items-start{
  align-items: flex-start;
}
.mt-6{
  margin-top:1.5rem
}
.mb-6{
  margin-bottom:1.5rem
}
.mb-3{
  margin-bottom:0.75rem
}
.avatar{
  width: 28px;
  height: 28px;
  // background-color: transparent;
  background: #fff;
  display:flex;
  justify-content: center;
  align-items: center;
  // box-shadow: 0 0 13px rgba($color: #000000, $alpha: 0.1);
  img{
    width: 100%;
    height: 100%;
  }
}
.flex-shrink-0{
  flex-shrink: 0;
}
.mr-3{
  margin-right:0.75rem
}
</style>
