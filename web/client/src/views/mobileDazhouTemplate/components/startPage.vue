<template>
  <div class="chat-container">
    <div class="chat-container-logo"><img class="lh-logo"
        :src="logoUrl() ? logoUrl() : '/src/assets/chatImages/pageTitle.svg'" /></div>
    <div class="welcome-message">
      <h2>您好，我是xxxxxxx市智能问答助手</h2>
      <p>我已接入DeepSeek大模型，支持深度思考
        有什么问题可以尽管问我</p>
    </div>
    <!-- 底部输入区域 -->
    <div class="input-container" :style="{ bottom: keyboardHeight + 'px' }">
      <div class="input-wrapper">
        <!-- 选项区域 -->
        <!-- 自适应输入框 -->
        <div class="input-area">
          <div ref="inputField" class="input-field" contenteditable="true" placeholder="请输入您的问题..." @input="handleInput"
            @keydown.enter.prevent="handleSend"></div>
        </div>
        <!-- 自适应输入框 -->
        <div class="input-area">
          <div class="options-row">
            <w-select v-model="model"  placeholder="请选择模型" style="width: auto;min-width: 220px;color: #828894">
              <w-option v-for="(item, index) in llmList"  :label="item.modelName" :value="item.modelId"></w-option>
            </w-select>

            <w-button type="primary"    class="send-btn"
              @click="handleSend">
              <img style="width: 32px; height: 32px" src="/src/assets/mobileUniversalTemplate/send.svg"
			/>
            </w-button>
          </div>
        </div>
      </div>
    </div>
    <!-- <div class="bottom-list">
      <a target="_blank" href="http://10.158.248.228/wg-agent-client/#/appTemplate/6dd58d5818d544118e6f5fb981232e0c/35989"><img  :src="link1" alt="" ></img></a>
      <a target="_blank" href="http://10.158.248.228/wg-agent-client/#/appTemplate/e1dace5ebad94aa7a3e778da4755e11a/36002"><img  :src="link2" alt="" ></img></a>
      <a target="_blank" href="http://10.158.248.228/wg-agent-client/#/knowledgeDetails/2c89359a9d6d4e50af86227613409d05"><img  :src="link3" alt="" ></img></a>
      <a target="_blank" href="http://10.158.248.228/wg-agent-client/#/knowledgeDetails/3c3f0e4743f6419791800b276a0c9f1d"><img  :src="link4" alt="" ></img></a>
      <a target="_blank" href="http://10.158.248.228/wg-agent-client/#/appTemplate/6dd58d5818d544118e6f5fb981232e0c/35989"><img  :src="link5" alt="" ></img></a>
    </div> -->
  </div>
</template>

<script setup>
  import { ref, onMounted, onUnmounted } from 'vue'
  import { useWindowSize } from '@vueuse/core'
  import { useRoute, useRouter } from 'vue-router';
  import sendIcon from '/@/assets/chatImages/send.svg';
  import link1 from '/@/assets/mobileDazhouTemplate/link1.png';
  import link2 from '/@/assets/mobileDazhouTemplate/link2.png';
  import link3 from '/@/assets/mobileDazhouTemplate/link3.png';
  import link4 from '/@/assets/mobileDazhouTemplate/link4.png';
  import link5 from '/@/assets/mobileDazhouTemplate/link5.png';
  import { useBasicLayout } from '/@/hooks/useBasicLayout';
  import { getLlmPageList } from '/@/api/chat';
  import { ElMessage } from 'element-plus';


  const emit = defineEmits(['sendStartParams']);

  const route = useRoute();
  const router = useRouter();
  const { height: windowHeight } = useWindowSize()
  // 移动端自适应相关
  const { isMobile } = useBasicLayout();
  // 响应式数据
  const inputField = ref(null)
  const messagesArea = ref(null)
  const keyboardHeight = ref(0)
  const model = ref('')
  const llmList = ref([])
  const useDeepThinking = ref(false)
  const useWebSearch = ref(false)
  const maxInputHeight = 150 // 最大输入高度
  const logoUrl = () =>
  {
    let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
    return appInfo ? appInfo.logo : '';
  };

  const handleInput = () =>
  {
    adjustInputHeight()
  }

  // 调整输入框高度
  const adjustInputHeight = () =>
  {
    if (!inputField.value) return

    inputField.value.style.height = 'auto'
    const newHeight = Math.min(inputField.value.scrollHeight, maxInputHeight)
    inputField.value.style.height = `${newHeight}px`
  }

  // 处理发送
  const handleSend = () =>
  {
    const text = inputField.value.innerText.trim()
    if (!text) return
    if (!model.value){
      ElMessage.warning('请选择模型');
      return
    } 

    // 清空输入
    inputField.value.innerText = ''
    adjustInputHeight()
    sessionStorage.setItem('dazhouText', text);
    sessionStorage.setItem('dazhouModel', model.value);
    emit('sendStartParams');
  }

  // 处理键盘事件
  const handleResize = () =>
  {
    const viewport = window.visualViewport
    if (!viewport) return

    const keyboardHeight = windowHeight.value - viewport.height
    keyboardHeight.value = Math.max(keyboardHeight, 0)

    // 滚动到底部
    if (messagesArea.value) {
      messagesArea.value.scrollTop = messagesArea.value.scrollHeight
    }
  }
  const apiGetLlmPageList = async () => {
    if(sessionStorage.getItem('llmList')){
      llmList.value = JSON.parse(sessionStorage.getItem('llmList'));
    } else {
      llmList.value = [];
      const params = {"modelName":"","status":"",fromClientFlag: '是',"manufacturer":"","pageSize":1000,"pageNo":1};
      const res = await getLlmPageList(params);
      if (res.code == "000000") {
        llmList.value = res.data.records || [];
        sessionStorage.setItem('llmList', JSON.stringify(llmList.value));
      }
    }
    model.value = sessionStorage.getItem('dazhouModel') ? sessionStorage.getItem('dazhouModel') : llmList.value[0].modelId;
  }
  // 生命周期
  onMounted(() =>
  {
    window.visualViewport?.addEventListener('resize', handleResize)
    apiGetLlmPageList()
  })

  onUnmounted(() =>
  {
    window.visualViewport?.removeEventListener('resize', handleResize)
  })
</script>

<style lang="scss" scoped>
  .chat-container {
    height: 100vh;
    display: flex;
    flex-direction: column;
    position: relative;
    z-index: 100;
  }

  .chat-container-logo {
    margin: 150px 44px 0;
  }

  .messages-area {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    padding-bottom: 100px;
    /* 给输入区域留出空间 */
  }

  .welcome-message {
    text-align: center;
    padding: 30px 40px;
    color: #666;

    h2 {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 22px;
      color: #000000;
      line-height: 32px;
      font-style: normal;
      margin: 0 0 10px 0;
    }

    p {
      font-weight: 400;
      font-size: 16px;
      color: #000000;
      line-height: 24px;
      font-style: normal;
    }
  }

  .input-container {
    background: #fff;
    padding: 12px;
    background: #FFFFFF;
    box-shadow: 0px 2px 8px 0px rgba(0, 0, 0, 0.06);
    border-radius: 12px;
    border: 1px solid #D7DAE0;
    margin: 0 20px;
  }

  .input-wrapper {
    max-width: 800px;
    margin: 0 auto;
  }

  .options-row {
    display: flex;
    gap: 15px;
    margin-bottom: 8px;
    padding: 10px 8px 0 4px;
    position: relative;
    width: 100%;

    :deep(.w-select) {
      border-radius: 8px;
      background: #F8F9F9;
    }
  }

  .input-area {
    display: flex;
    align-items: flex-end;
    gap: 8px;
  }

  .input-field {
    flex: 1;
    max-height: 150px;
    min-height: 40px;
    padding: 8px 6px;
    border: none;
    background: none;
    overflow-y: auto;
    line-height: 1.5;
    transition: all 0.2s;
  }

  .input-field:empty::before {
    content: attr(placeholder);
    color: #999;
  }

  .input-field:focus {
    outline: none;
    background: #fff;
    border-color: #07c160;
  }

  .send-btn {
    height: 44px;
    border-radius: 22px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: absolute;
    background: #fff !important;
    right: 0;
    top: 4px;
    padding: 0;
  }
  .bottom-list{
    position: fixed;
    bottom: 40px;
    text-align: center;
    width: 100%;
    a{
      margin: 10px auto;
      display: block;
      img{
        width: 120px;
      }
    }
  }
</style>