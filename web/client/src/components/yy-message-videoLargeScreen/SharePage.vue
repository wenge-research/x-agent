<template>
  <div class="popup-overlay" @click="close">
    <div class="popup-content">
      <div class="tit">
        <iconpark-icon name="arrow-left-s-line" color="#494C4F" class="btn"></iconpark-icon>
        <span>分享对话</span>
      </div>
      <div class="share-content" ref="shareRef">
        <div v-if="isQRcode" class="share-content-title">
          <img v-if="logoUrl()" class="lh-logo" :src="logoUrl()" style="height: 40px; margin: 0 auto" />
        </div>
        <div v-for="(item, index) of isSelected" :key="item.id" class="main" :class="{ hide: !chatStore.hidden[index] }" >
          <YYMessageMobileUniversalTemplate :answer="item.answer" :dialogueId="item.dialogueId"
            :businessScenarioLists="item.businessScenarioLists" :businessScenario="item.businessScenario"
            :citations="item.citations || []" :createTime="item.createTime" :id="item.id + ''"
            :isAiQuestion="item.isAiQuestion" :dialogueFileIds="item.dialogueFileIds || []"
            :dialogueFolderIds="item.dialogueFolderIds || []" :paragraph="item.paragraph || ''"
            :skillId="item.skillId || ''" :param="item.param" :question="item.question" :loading="item.loading"
            :inversion="true" :isLast="false" :key="index + 'r'" :feedBackStatus="item.feedBackStatus"
            :imgUrl="item.imgUrl" :sensitive="item.sensitive" :stopChatLoading="stopChatLoading"
            :matterGuide="item?.matterGuide" :finishReason="item.finishReason" :clientId="item.clientId"
            @handleFillForm="handleFillForm" :index="index" align="right"></YYMessageMobileUniversalTemplate>
          <YYMessageMobileUniversalTemplate :answer="item.answer" :dialogueId="item.dialogueId"
            :businessScenarioLists="item.businessScenarioLists" :businessScenario="item.businessScenario"
            :citations="item.citations || []" :createTime="item.createTime" :id="item.id + ''"
            :dialogueFileIds="item.dialogueFileIds || []" :dialogueFolderIds="item.dialogueFolderIds || []"
            :paragraph="item.paragraph || ''" :skillId="item.skillId || ''" :param="item.param"
            :isAiQuestion="item.isAiQuestion" :question="item.question" :loading="item.loading"
            :isLast="index == chatList.length - 1" :key="index + 'l'" :feedBackStatus="item.feedBackStatus"
            :imgUrl="item.imgUrl" :sensitive="item.sensitive" :stopChatLoading="stopChatLoading"
            :suggestOrg="item.suggestOrg" :contentQaType="item.contentQaType" :answerFlag="item.answerFlag"
            :plainText="item.plainText" :matterGuide="item?.matterGuide" :finishReason="item.finishReason"
            :clientId="item.clientId" @handleFillForm="handleFillForm" @playVoice="playVoice(item.answer)" align="left"
            :index="index">
          </YYMessageMobileUniversalTemplate>
          <div class="checkBox" @click.stop="handleSelected(item, index)" v-if="checkBoxHidden">
            <!-- <iconpark-icon v-if="!isSelected[index].checked" name="checkbox-circle-fill" size="20" color="#2065D6"></iconpark-icon>
          <iconpark-icon name="checkbox-blank-circle-line" size="20" color="#F7F8FA" v-else></iconpark-icon> -->
            <img src="../../assets/selected.png" alt="" v-if="!isSelected[index].checked" />
            <img src="../../assets//notselected.png" alt="" v-else />
          </div>
        </div>
        <div v-if="isQRcode" style="position: relative; height: 120px; width: 378px;top: -18px; background-color: #FFFFFF; padding-left: 16px;padding-top:16px;padding-bottom:10px">
          <img style="width: 295px; left: 16px;" src="../../assets/img/bg.png" alt="" />
          <qrcode-vue style="position: absolute; right: 66px; bottom: 48px;" :value="qrcodeValue" :size="56" />
        </div>
      </div>
      <div class="share-footer">
        <div v-if="!isQRcode">
          <el-button @click.stop="handleSaveImg('fuzhi')" style="width: 126px!important;
          background: #C4C6CC!important;
									font-family: MiSans, MiSans!important;
font-weight: 400!important;
font-size: 16px!important;
color: #3F4247!important;
line-height: 48px!important;
text-align: left!important;
font-style: normal!important;
border: 1px solid #C4C6CC!important;
border-radius: 8px!important;
height: 48px;">
            <iconpark-icon name="link-m" color="#494C4F" style="margin-right:8px;" size="20"></iconpark-icon>
            复制链接
          </el-button>
          <el-button @click.stop="handleSaveImg('baocun')" style="width: 213px; 
            background: #2065D6!important; 
border-radius: 8px!important;
font-family: MiSans, MiSans!important;
font-weight: 400!important;
font-size: 16px!important;
color: #FFFFFF!important;
line-height: 48px!important;
text-align: left!important;
font-style: normal!important;
text-transform: none!important;
border:0!important;
height: 48px;">
            <iconpark-icon name="image-add-line-dhhh734g" color="#FFFFFF" style="margin-right:8px;"
              size="20"></iconpark-icon>
            生成长图
          </el-button>
        </div>
        <div v-if="isQRcode">
          <el-button @click.stop="SaveImg('baocun')" :loading="saveImgFlag" style="width: 347px; 
            background: #2065D6!important; 
border-radius: 8px!important;
font-family: MiSans, MiSans!important;
font-weight: 400!important;
font-size: 16px!important;
color: #FFFFFF!important;
line-height: 48px!important;
text-align: left!important;
font-style: normal!important;
text-transform: none!important;
border:0!important;
height: 48px;">
            <iconpark-icon name="download-line" color="#FFFFFF" style="margin-right:8px;"
              size="20"></iconpark-icon>
            保存图片
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, ref, provide, computed, watch, nextTick, toRaw, onMounted, onUnmounted } from "vue";
import { ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import { apiUploadShare } from '/@/api/chat/index'
import domtoimage from 'dom-to-image';
import html2canvas from 'html2canvas';
import useClipboard1 from 'vue-clipboard3'
import QrcodeVue from 'qrcode.vue';
import { useChatStore } from '/@/stores/chat';
// 使用插件
const { toClipboard } = useClipboard1()
// import { useChatStore } from '/@/stores/chat';
const route = useRoute();
const props = defineProps({
  chatList: {
    type: Array,
    required: true
  }
});

const chatStore = useChatStore()
const isSelected = ref([...props.chatList]);
onMounted(()=>{
  console.log(chatStore.$state)
  chatStore.$state.inShare = true
})
onUnmounted(()=>{
  chatStore.$state.inShare = false
})
watch(() => props.chatList, (newList) => {
  isSelected.value = [...newList];
}, { deep: true });
// const chatStore = useChatStore();
// const chatList = computed(() => chatStore.chatList)

const emit = defineEmits(["close"]);
const close = () => {
  emit("close");
};
//根据index顺序排序
const changeSort = (newArr: any) => {
	return newArr.sort((a, b) => {
		return a['sort'] - b['sort']
	})
}
const qrcodeValue = ref('https://example.com');
const isQRcode = ref(false)
const saveImgFlag = ref(false)
const checkBoxHidden = ref(true)
const hidden = ref([])
const shareRef = ref(null);

const saveSelected = ref([]) //用于保存勾选了对话
const handleSelected = (item, index) => {
	console.log(isSelected.value[index], '改变了没有', index, '索引')
	isSelected.value[index].checked = !isSelected.value[index].checked
    console.log(isSelected.value[index].checked,'isSelected.value[index].checked')
	const saveSelectedIndex = saveSelected.value.indexOf(isSelected.value[index])
	console.log(saveSelectedIndex,'saveSelectedIndex')
	if (saveSelectedIndex != -1) {
		saveSelected.value.splice(saveSelectedIndex, 1)
	} else {
		saveSelected.value.push(item)
	}
	console.log(saveSelected.value,'saveSelected.value')
}
const getRealContentHeight = (element)=> {
	const clone = element.cloneNode(true);
	clone.style.position = 'absolute';
	clone.style.visibility = 'hidden';
	clone.style.height = 'auto';
	document.body.appendChild(clone);
	const height = clone.scrollHeight;
	document.body.removeChild(clone);
	return height + 50;
}
const captureImg2 = async (element) => {
  try {
    // 保存原始样式以便恢复
    const originalStyle = window.getComputedStyle(element);
    const originalOverflow = originalStyle.overflow;
    const originalHeight = originalStyle.height;
    
    // 临时修改样式以显示全部内容
    element.style.overflow = 'visible';
    element.style.height = 'auto';
    
    // 强制重绘
    await new Promise(resolve => requestAnimationFrame(resolve));
    
    // 计算完整高度
    const fullHeight = element.scrollHeight;
    
    // 使用dom-to-image截图完整高度
    const domImg = await domtoimage.toBlob(element, {
      height: fullHeight,
      width: element.clientWidth,
      quality: 0.95, // 提高质量
      bgcolor: '#ffffff',
      style: {
        position: 'absolute',
        top: '0',
        left: '0',
        width: `${element.clientWidth}px`,
        height: `${fullHeight}px`
      }
    });
    
    // 恢复原始样式
    element.style.overflow = originalOverflow;
    element.style.height = originalHeight;
    
    const dataUrl = URL.createObjectURL(domImg);
    return {
      dataUrl,
      filename: `分享的图片.png`,
      blob: domImg
    };
  } catch (err) {
    console.error('截图失败:', err);
    return null;
  } finally {
    checkBoxHidden.value = true;
    isSelected.value.forEach((item, index) => {
      hidden.value[index] = true;
      item.checked = false;
    });
  }
};
//保存图片
const handleSaveImg = (type) => {
	const isSave = isSelected.value.some(item => {
		return item.checked == true
	})
	if (!isSave) {
		ElMessage({
			type: 'warning',
			message: '请选择至少一张要分享的图片'
		})
		return
	}

	if (type == 'baocun') {
		isSelected.value.forEach((item, index) => {
			if (!item.checked) {
				chatStore.hidden[index] = false
			} else {
				chatStore.hidden[index] = true
			}
		})
    console.log(chatStore.hidden,'chatStore.hidden的数据')
		checkBoxHidden.value = false
	}
	nextTick(async () => {
		if (!shareRef.value) return;
		// const zip = new jsZip()
		// const folder = zip.folder('想要分享的图片')
		// const result = await captureImg(shareRef.value);
		if (type == 'baocun') {
      isQRcode.value = true
      checkBoxHidden.value = false
			ElMessage({
				type: 'info',
				message: '图片正在生成，请稍等'
			})
			try {
                const newArr = toRaw(saveSelected.value)
                changeSort(newArr)
                console.log(newArr, '传入分享 API 里的参数')
                let res = await apiUploadShare({ dialogueCacheList: newArr })
                if (res.code == '000000') {
                    const shareLink = `【智川】这是我和${JSON.parse(window.localStorage.getItem(`${route.params.appId}`)).applicationName}的聊天对话，你也来试试吧~ 【点击链接直接打开】${window.location.href}?key=${res.data}`
                    // 更新二维码的值
                    qrcodeValue.value = `${window.location.href}?key=${res.data}`
                    console.log(qrcodeValue.value, '二维码的值')
                    await toClipboard(shareLink)
                    ElMessage({
                        type: 'success',
                        message: '生成图片成功'
                    })
                } else {
                    console.error('API 返回错误码:', res.code)
                    ElMessage({
                        type: 'error',
                        message: '生成图片失败'
                    })
                }
            } catch (error) {
                console.error('生成图片失败:', error)
                ElMessage({
                    type: 'error',
                    message: '生成图片失败，请稍后重试'
                })
            }
		}
		if (type == 'fuzhi') {
			try {
				console.log(saveSelected.value instanceof (Array), '来查看一下是不是数组')
				const newArr = toRaw(saveSelected.value)
				changeSort(newArr)
				console.log(newArr, '传入分享API里的参数')
				let res = await apiUploadShare({ dialogueCacheList: newArr })
				console.log(res,'走到这一步了吗')
				if (res.code == '000000') {
					await toClipboard(`【智川】这是我和${JSON.parse(window.localStorage.getItem(`${route.params.appId}`)).applicationName}的聊天对话，你也来试试吧~ 【点击链接直接打开】${window.location.href}` + `?key=${res.data}`)
					ElMessage({
						type: 'success',
						plain: false,
						message: '已复制链接，快去分享吧'
					})
				}
			} catch {
				console.log('失败了')
			} finally {
				console.log('最后都要执行的')
				saveSelected.value = []
				checkBoxHidden.value = true
				isSelected.value.forEach((item, index) => {
					hidden.value[index] = true
					item.checked = false
				})
			}

			// if(res.data.code == '000000'){
			// 	try{
			// 	await	toClipboard(`【智川】这是我和${JSON.parse(window.localStorage.getItem(`${route.params.appId}`)).applicationName}的聊天对话，你也来试试吧~ 【点击链接直接打开】${res.data.data[0].url}`)
			// 	ElMessage({
			// 		type:'success',
			// 		plain:false,
			// 		message:'已复制链接，快去分享吧'
			// 	})
			// 	}catch{
			// 		console.log('失败')
			// 	}
			// 	}
		}

	}
	)
	isSelected.value.forEach(item => {
		item.checked = false
	})
}
//生成长图
const SaveImg = async (type) => {
  if (type == 'baocun') {
    saveImgFlag.value = true;
    const result = await captureImg2(shareRef.value);
    saveImgFlag.value = false;

    if (result) {
      const link = document.createElement('a');
      link.href = result.dataUrl;
      link.download = result.filename;
      link.click();
      link.remove();
      URL.revokeObjectURL(result.dataUrl);
    }

    // 恢复初始状态
    isSelected.value.forEach((item, index) => {
      hidden.value[index] = true;
      item.checked = false;
    });
    checkBoxHidden.value = true;
  }
};
const logoUrl = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo ? appInfo.logo : '';
};
</script>

<style scoped>
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-content {
  position: fixed;
  background: #f4f6f9;
  width: 100%;
  height: 100vh;
  bottom: 0;
}

.popup-content iframe {
  width: 100%;
  height: 100%;
  border: none;
}
.tit {
  position: fixed;
  width: 100%;
  /* bottom: 85vh; */
  top: 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
  text-align: center;
  padding: 0 50px;
  height: 44px;
  font-family: MiSans, MiSans;
  font-weight: 500;
  line-height: 44px;
  font-style: normal;
  font-size: 18px;
  color: #313436;
  .btn {
    position: absolute;
    font-size: 30px;
    left: 10px;
    top: 8px;
  }
  /* .title {
    font-size: 18px;
    color: #313436;
  } */
}

.share-content {
  /* position: fixed; */
  /* width: 100%; */
  /* bottom: 85vh;
  top: 60px; */
  margin-top: 60px;
  /* background-color: #FFFFFF; */
  margin-left: 16px; 
  margin-right: 16px;
  border-radius: 8px;
  position: relative;
  /* bottom: 64px; */
  /* height: 600px; */
  max-height: 680px;
  overflow-y: auto;
 
  .main {
    position: relative;
    /* padding: 16px 8px 23px 16px; */
    padding-top: 1px;
    border-radius: 8px;
    /* margin: 0 4px; */
    background: #FFFFFF;
    /* overflow: auto; */
    /* overflow-y: scroll;
    max-height: 680px; */
    /* width: 100%; */
    width: 100%;
    &:first-child {
          margin-top: 8px;
        }
  }
  .share-content-title {
    position: relative;
    background: #FFFFFF;
    /* border-radius: 8px; */
    /* padding: 3px 16px 10px 40px; */
    padding-top: 14px;
    width: 100%;
    height: 40px;
    z-index: 1;
    /* margin-bottom: 16px; */
    top: 2px;
  }
  .checkBox {
		width: 18px;
		height: 18px;
		position: absolute;
		top: 20px;
		left: 8px;
		z-index: 3;
		display: flex;
		justify-content: center;
		align-items: center;
		border-radius: 50%;
    object-fit: cover; 
    /* border: 1px solid #4888EF;  */
	}
}

.share-footer {
  position: fixed;
  width: 100%;
  bottom: 44px;	
  left: 32px;
}
.hide{
  display:none;
}
</style>
