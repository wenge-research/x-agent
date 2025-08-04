<template>
  <div class="report-answer-page">
    <div class="box-left">
      <div class="top-icons">
        <img src="../../../assets/img/icon_goBack.png" alt="" @click="goBack" />
      </div>

      <div class="left-title">{{ questionTxt }}</div>

      <div style="padding: 16px; padding-bottom: 0" v-for="(item, index) in taskSplit" :key="index" v-show="item.urlList.length > 0">
        <div class="left-container" :style="{ height: expandFlags[index] ? 'auto' : '100px' }">
          <div class="container-title" @click="toggleExpand(index)">
            <div v-show="isLoading" class="loading-spinner"></div>
            <img src="../../../assets/img/icon_webpage.png" alt="" />
            <p>规划agent 规划任务</p>
            <div style="cursor: pointer; margin-top: 4px">
              <el-icon v-show="expandFlags[index]">
                <ArrowUp />
              </el-icon>
              <el-icon v-show="!expandFlags[index]">
                <ArrowRight />
              </el-icon>
            </div>
          </div>

          <div class="is-complete">{{ item.content }}</div>

          <!-- 使用transition-group包裹URL列表 -->
          <transition-group name="fade" tag="div" v-if="expandFlags[index]">
            <div class="container-item" v-for="(urlItem, urlIndex) in item.urlList" :key="urlIndex"
              v-show="urlIndex <= displayedUrlIndex[index]">
              <div class="item-title">
                <img src="../../../assets/img/icon_webpage.png" alt="" />
                <span>网页搜索</span>
              </div>
              <div class="item-desc">{{ urlItem.title }}</div>
              <div class="item-url" @click="openNewUrl(urlItem.url)">{{ urlItem.url }}</div>
            </div>
          </transition-group>

          <div class="writer-box" v-show="expandFlags[index]">
            <div class="writer-title">
              <img src="../../../assets/img/icon_write.png" alt="" />
              <span>网页撰写</span>
            </div>
            <div class="writer-desc">市场报告分析</div>
          </div>
        </div>
      </div>
      <div style="padding: 16px;">
        <div class="run-box" v-for="(item, index) in taskSplit" :key="index">
          <div>运行</div>
          <div class="container-title" @click="runToggleExpand(index)">
            <div v-show="isLoading" class="loading-spinner"></div>
            <img src="../../../assets/img/icon_webpage.png" alt="" />
            <p>网页探索任务</p>
            <div style="cursor: pointer; margin-top: 4px">
              <el-icon v-show="runExpandFlags[index]">
                <ArrowUp />
              </el-icon>
              <el-icon v-show="!runExpandFlags[index]">
                <ArrowRight />
              </el-icon>
            </div>
          </div>

          <div class="run-title">{{ item.content }}</div>
          <div v-show="runExpandFlags[index]">
            <div class="text" v-html="compiledMarkdown(item.answer)"></div>
          </div>
          
        </div>
      </div>
    </div>

    <div class="box-right">
      <div class="top-tabs">
        <div></div>
        <div class="tabs">
          <div class="items" :class="{ 'active-tab': activeTab === item.name }" v-for="item in tabs" :key="item.name"
            @click="setActiveTab(item.name)">
            {{ item.label }}
          </div>
        </div>
        <div class="icon" @click="exportToPdf">
          <el-icon size="20">
            <Upload />
          </el-icon>
        </div>
      </div>

      <div class="container" :style="{
        padding: activeTab === 'generate' ? '0' : '20px',
        backgroundColor: activeTab === 'result' ? 'transparent' : '',
      }">
        <follow v-if="activeTab === 'follow'" :taskSplit="taskSplit" :urlImgList="urlImgList" />
        <explore v-if="activeTab === 'explore'" :taskSplit="taskSplit" />
        <generate v-if="activeTab === 'generate'" :taskSplit="taskSplit" />
        <result v-if="activeTab === 'result'" :reslultThml="reslultThml" ref="resultPageRef" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import useEventSource from '/@/hooks/useEventSource'
import mittBus from '/@/utils/mitt'
import { Upload, ArrowUp, ArrowRight } from '@element-plus/icons-vue'
import { ref, onMounted, watch } from 'vue'
import follow from './follow.vue'
import explore from './explore.vue'
import generate from './generate.vue'
import result from './result.vue'
import { marked } from 'marked';

const compiledMarkdown = (markdown) => {
      return marked(markdown);
    }

// 数据状态
const isLoading = ref(true)
const taskSplit = ref([])
const reslultThml = ref('')
const expandFlags = ref([])
const displayedUrlIndex = ref([])
const urlImgObj = ref({})

// 获取模板数据
const getTemplates = async (data) => {
  const url =
    import.meta.env.VITE_API_URL +
    import.meta.env.VITE_STREEM_PATH +
    import.meta.env.VITE_BASE_API_URL +
    '/workflow/dialogueRun'
  const abortController = new AbortController()
  const clientId = Math.round(Math.random() * 1000000000000000000).toString(36)
  const parms = {
    inputs: {
      question: data.value,
    },
    componentId: JSON.parse(sessionStorage.getItem(`nodeData`)).componentId,
    //  applicationId: '4a729fdf9dea4b14bb3d6e12e5ebfd9d',
    clientId,
  }

  useEventSource(url, parms, abortController, (list) => {
    let anserData = JSON.parse(list);
    // console.log('初始数据', anserData);
    if (anserData?.answerFlag) {
      if (anserData.output?.step === '任务拆解') {
        taskSplit.value = anserData.output.manusTaskList;
        taskSplit.value.forEach((task) => {
          task.urlList = [];
          task.answer = '';
        });
        expandFlags.value = taskSplit.value.map((_, index) => index === 0);
        console.log('任务拆解', taskSplit.value);
      }

      if (anserData.output?.step === '跟随') {
    urlImgObj.value = anserData.output;
    const { taskId, urlList } = urlImgObj.value;
    const task = taskSplit.value.find((task) => task.taskId === taskId);
 
    if (task) {
        // 遍历 urlList 中的每个项
		if(urlList){
			urlList.forEach((item) => {
			    const { originalUrl, imageUrl } = item;
			    const existingIndex = task.urlList.findIndex((taskItem) => taskItem.url === originalUrl);
			    
			    if (existingIndex !== -1) {
			        // 如果找到匹配的 url，更新 image_url
			        task.urlList[existingIndex].imageUrl = imageUrl;
			    } else {
			        // 如果没有找到匹配的 url，添加一个新的对象到 urlList 中
			        task.urlList.push({ url: originalUrl, imageUrl });
			    }
			});
		}
        
    }
}

      if (anserData.output?.step === '网页探索') {
        const { taskId, resInfo } = anserData.output;
        const task = taskSplit.value.find((task) => task.taskId === taskId);
        if (task) task.urlList = resInfo;
      }

      if (anserData.output?.step === '生成') {
        const { taskId, text } = anserData.output;
        const task = taskSplit.value.find((task) => task.taskId === taskId);
        if (task) task.answer = text;
        console.log('生成');
      }

      if (anserData.output?.step === '结果') {
        isLoading.value = false;
        reslultThml.value = anserData?.answer;
      }
    }
  });
}

// 折叠展开控制
const toggleExpand = (index) => {
  expandFlags.value[index] = !expandFlags.value[index]
}

const runExpandFlags = ref(taskSplit.value.map(() => false)) // 初始状态都是折叠的
 
const runToggleExpand = (index) => {
  runExpandFlags.value[index] = !runExpandFlags.value[index]
}

// Tabs 控制
const tabs = [
  { name: 'follow', label: '跟随' },
  { name: 'explore', label: '探索' },
  { name: 'generate', label: '生成' },
  { name: 'result', label: '结果' },
]
const activeTab = ref('follow')
const setActiveTab = (name) => {
  activeTab.value = name
}

// 返回按钮
const goBack = () => {
  mittBus.emit('toggle-answer', false)
}

const questionTxt = ref('')

onMounted(() => {
  const id = localStorage.getItem('conversationId')
  const storedText = localStorage.getItem('storedText')
  const manusParms = {
    conversationId: id,
    value: storedText,
  }
  questionTxt.value = storedText
  getTemplates(manusParms)

  taskSplit.value.forEach((_, index) => {
    displayedUrlIndex.value[index] = -1 // 初始化为-1
  })
})

// 监听 taskSplit 变化以触发逐条显示动画
watch(
  () => taskSplit.value,
  (newVal) => {
    newVal.forEach((item, index) => {
      if (displayedUrlIndex.value[index] === undefined) {
        displayedUrlIndex.value[index] = -1;
      }

      if (displayedUrlIndex.value[index] < item.urlList.length - 1) {
        let current = displayedUrlIndex.value[index];
        const delay = 500; // 每条数据之间的延迟时间（毫秒）

        const animate = () => {
          if (current < item.urlList.length - 1) {
            current++;
            setTimeout(() => {
              displayedUrlIndex.value[index] = current;
              if (current < item.urlList.length - 1) {
                requestAnimationFrame(animate);
              }
            }, delay);
          }
        };

        requestAnimationFrame(animate);
      }
    });
  },
  { deep: true }
);

//打开新网页
const openNewUrl = (url) => {
  window.open(url, '_blank');
}

//导出结果
const resultPageRef = ref()
const exportToPdf = () => {
  resultPageRef.value.exportPdf();
}
</script>

<style scoped lang="scss">
.report-answer-page {
  box-sizing: border-box;
  width: 100vw;
  padding: 16px;
  display: flex;

  .box-left {
    width: 480px;
    height: 812px;
    background: rgba(255, 255, 255, 0.7);
    border-radius: 8px;
    backdrop-filter: blur(2px);
    margin-right: 8px;
    overflow: hidden;
    overflow-y: auto;

    .top-icons {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 56px;
      background: rgba(255, 255, 255, 0.5);
      border-bottom: 1px solid #e7e7e7;
      padding: 12px 16px;

      img {
        width: 20px;
        height: 20px;
        cursor: pointer;
      }
    }

    .left-title {
      padding: 12px 16px;
      height: 44px;
      background: #4888ef;
      border-radius: 8px;
      display: inline-block;
      margin: 16px;
      font-weight: 400;
      font-size: 14px;
      color: #fff;
      margin-bottom: 0;
    }

    .left-container {
      background: rgba(255, 255, 255, 0.7);
      border-radius: 8px;
      border: 1px solid #e7e7e7;
      backdrop-filter: blur(4px);
      padding: 16px;
      overflow: hidden;
      transition: height 0.3s ease;

      .container-title {
        display: flex;
        align-items: center;

        .loading-spinner {
          margin-right: 6px;
          width: 16px;
          height: 16px;
          border: 2px solid rgba(0, 0, 0, 0.1);
          border-radius: 50%;
          border-top: 4px solid #363cff;
          animation: spin 1s linear infinite;
        }

        @keyframes spin {
          0% {
            transform: rotate(0deg);
          }

          100% {
            transform: rotate(360deg);
          }
        }

        img {
          width: 16px;
          height: 16px;
          margin-right: 10px;
        }

        p {
          font-weight: 400;
          font-size: 14px;
          color: #3f4247;
          margin-right: 6px;
        }
      }

      .is-complete {
        margin-top: 14px;
        font-weight: 500;
        font-size: 14px;
        color: #3f4247;
      }

      .container-item {
        padding: 12px;
        background: #ffffff;
        border-radius: 8px;
        border: 1px solid #e7e7e7;
        margin-top: 12px;

        .item-title {
          padding: 4px;
          background: #f7f8fa;
          border-radius: 4px;
          display: flex;
          align-items: center;
          width: max-content;

          img {
            width: 13px;
            height: 13px;
            margin-right: 4px;
          }

          span {
            font-weight: 400;
            font-size: 12px;
            color: #1747e5;
            margin-top: 2px;
          }
        }

        .item-desc {
          font-weight: 400;
          font-size: 14px;
          color: #1d2129;
          margin-top: 8px;
        }

        .item-url {
          cursor: pointer;
          margin-top: 8px;
          font-weight: 400;
          font-size: 12px;
          color: #7e56eb;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          display: block;
          width: 90%;
        }
      }

      .writer-box {
        padding: 12px;
        background: #ffffff;
        border-radius: 8px;
        border: 1px solid #e7e7e7;
        margin-top: 12px;

        .writer-title {
          padding: 4px;
          background: #f7f8fa;
          border-radius: 4px;
          display: flex;
          align-items: center;
          width: max-content;

          img {
            width: 13px;
            height: 13px;
            margin-right: 4px;
          }

          span {
            font-weight: 400;
            font-size: 12px;
            color: #7e56eb;
            margin-top: 2px;
          }
        }

        .writer-desc {
          font-weight: 400;
          font-size: 14px;
          color: #1d2129;
          margin-top: 8px;
        }
      }
    }

    .run-box {
      padding: 16px;
      font-size: 14px;
      color: #86909C;
      background: #FFFFFF;
      border-radius: 8px;
      border: 1px solid #E7E7E7;
      margin-bottom: 16px;

      .container-title {
        display: flex;
        align-items: center;

        .loading-spinner {
          margin-right: 6px;
          width: 16px;
          height: 16px;
          border: 2px solid rgba(0, 0, 0, 0.1);
          border-radius: 50%;
          border-top: 4px solid #363cff;
          animation: spin 1s linear infinite;
        }

        @keyframes spin {
          0% {
            transform: rotate(0deg);
          }

          100% {
            transform: rotate(360deg);
          }
        }

        img {
          width: 16px;
          height: 16px;
          margin-right: 10px;
        }

        p {
          font-weight: 400;
          font-size: 14px;
          color: #3f4247;
          margin-right: 6px;
        }
      }

      .run-title {
        font-size: 14px;
        color: #1D2129;
        margin-top: 10px;
      }

      .text {
        font-weight: 400;
        font-size: 12px;
        color: #86909C;
        margin-top: 8px;
      }
    }
  }

  .box-right {
    flex: 1;
    height: 812px;
    background: rgba(255, 255, 255, 0.7);
    border-radius: 8px;
    backdrop-filter: blur(2px);

    .top-tabs {
      height: 56px;
      background: rgba(255, 255, 255, 0.5);
      border-radius: 8px 8px 0px 0px;
      border-bottom: 1px solid #e7e7e7;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 25px;

      .icon {
        cursor: pointer;
      }

      .tabs {
        width: 240px;
        height: 32px;
        background: #f7f8fa;
        border-radius: 16px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: 400;
        font-size: 14px;
        color: #86909c;
        cursor: pointer;
      }

      .items {
        width: 59px;
        height: 28px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .active-tab {
        background: #ffffff;
        box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
        border-radius: 14px;
        font-weight: 500;
        font-size: 14px;
        color: #1d2129;
      }
    }

    .container {
      padding: 23px 32px;
    }
  }
}

/* 添加过渡效果 */
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>