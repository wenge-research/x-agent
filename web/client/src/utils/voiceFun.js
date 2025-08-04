import { textToVoice, rsjTextToVoice,csTextToVoice } from '/@/api/chat';
import mittBus from '/@/utils/mitt';
let audioInstance = null; // 用于存储当前播放的Audio实例
let isPlaying = false; // 标记播放状态
let timeoutId = null; // 存储延迟请求的定时器ID
let pendingAudioBlobs = []; // 存储已请求但尚未播放的音频Blob
let currentIndex = 0; // 用于跟踪当前播放的音频块索引
let chunks = [];
let ttsId = '';


// 分割文本为块的函数
function splitTextIntoChunks(text, minChunkSize = 15) {
  // 验证输入
  if (typeof text !== 'string') {
    throw new TypeError('Text must be a string.');
  }
  if (typeof minChunkSize !== 'number' || minChunkSize <= 0) {
    throw new TypeError('Chunk size must be a positive number.');
  }

  let startIndex = 0;

  while (startIndex < text.length) {
    let endIndex = startIndex + minChunkSize;

    // 确保endIndex不会超出文本范围
    endIndex = Math.min(endIndex, text.length);

    // 如果当前段落的末尾不是标点符号，寻找下一个最近的标点符号
    while (endIndex < text.length && !/[.,;!?…，。！？、；]/.test(text[endIndex - 1])) {
      const punctuationIndex = text.slice(endIndex).search(/[.,;!?…，。！？、；]/);
      if (punctuationIndex !== -1) {
        endIndex += punctuationIndex + 1; // 移动到标点符号后的字符
      } else {
        // 如果直到文本末尾都没有找到标点符号，则分割到文本末尾
        endIndex = text.length;
      }
    }

    // 处理分割结果，确保分割段末尾是标点符号
    if (endIndex > startIndex) {
      const chunk = text.substring(startIndex, endIndex).trim();
      chunks.push(chunk);
    }

    // 更新起始索引到下一个分割点之后
    startIndex = endIndex;
  }

  return chunks;
}

// 向后端请求音频数据的函数
async function requestAudioFromBackend(textChunk) {
  return new Promise((resolve) => {
    // let api = ttsId === '8' ? rsjTextToVoice : textToVoice;
    console.log(ttsId,"@@@@@@");
    let api=null
    let isTtsId =false
    switch(ttsId){
      case '8':api=rsjTextToVoice;break;
      case '70':api=csTextToVoice;isTtsId=true;break;
      default:api=textToVoice;break;
    }
    const resData = api({
      applicationId: localStorage.getItem('appId'),
      text: textChunk,
      ttsId:isTtsId?ttsId:undefined
    });

    resolve(resData ? resData : null);
  });
}

// 异步函数来播放并管理音频块的播放顺序
async function playNextAudio(delayMs) {
  if (isPlaying && currentIndex < chunks.length) {
    if (!pendingAudioBlobs[currentIndex]) {
      // 如果音频块尚未请求，则先请求
      pendingAudioBlobs[currentIndex] = await requestAudioFromBackend(chunks[currentIndex]);
    }
    const audioBlob = pendingAudioBlobs[currentIndex];
    console.log(audioBlob,"audioBlob");
    
    if (audioBlob) {
      if(ttsId==70){
        console.log("进入了");
        loadAudio(audioBlob).then(() => {
          // 音频加载完成后可以播放
          playAudio();
        });
      }else{
        const audioUrl = URL.createObjectURL(audioBlob);
        audioInstance.src = audioUrl;
        
        audioInstance.play().catch(error => {
          console.error('播放错误:', error)
        });
      }
      
      // 请求下一个音频块，但延迟执行
      timeoutId = setTimeout(() => {
        if (currentIndex < chunks.length) {
          requestNextAudio(currentIndex + 1);
        }
      }, delayMs);
    }
    
  } else {
    // 播放结束或已停止，清理资源
    cleanup();
  }
}

// 请求下一个音频块的辅助函数
async function requestNextAudio(index) {
  if (index < chunks.length) {
    pendingAudioBlobs[index] = await requestAudioFromBackend(chunks[index]);
  }
}

// 清理资源和状态的函数
function cleanup() {
  if (audioInstance) {
    audioInstance.pause();
    audioInstance.currentTime = 0;
    audioInstance = null;
  }

  if (timeoutId) {
    clearTimeout(timeoutId);
    timeoutId = null;
  }
  chunks = [];
  pendingAudioBlobs = [];
  currentIndex = 0; // 重置索引
  isPlaying = false;
  mittBus.emit('stopPlayer');
}

// 停止播放的方法
function stopPlayback() {
  cleanup();
}

// 主播放逻辑
async function textToSpeechAndPlay(text, chunkSize = 15, delayMs = 1, id) {
  ttsId = id
  // 分割文本为块
  chunks = splitTextIntoChunks(text, chunkSize);
  isPlaying = true;
  audioInstance = new Audio();
  audioInstance.addEventListener('ended', () => {
    console.log(currentIndex,chunks);
    
    if (currentIndex < chunks.length - 1)
      audioInstance.pause();
    currentIndex++;
    // 播放下一个音频块前，先清除已播放的音频块的引用
    delete pendingAudioBlobs[currentIndex - 1];
    playNextAudio(); // 递归调用播放下一个
  });
  playNextAudio(chunks,delayMs);
}

// 创建一个 AudioContext 对象
const audioContext = new (window.AudioContext || window.webkitAudioContext)();

// 存储音频数据
let audioBuffer = null;

// 加载音频文件的函数
async function loadAudio(url) {
    try {
        const response = await fetch(url);
        const arrayBuffer = await response.arrayBuffer();
        audioBuffer = await audioContext.decodeAudioData(arrayBuffer);
        console.log('音频加载完成');
    } catch (error) {
        console.error('加载音频失败:', error);
    }
}

// 播放音频的函数
function playAudio() {
    if (!audioBuffer) {
        console.error('音频未加载');
        return;
    }
    
    // 创建音频源
    const source = audioContext.createBufferSource();
    source.buffer = audioBuffer;
    
    // 连接到音频输出（扬声器）
    source.connect(audioContext.destination);
    source.onended=()=>{
      console.log(currentIndex,chunks);
    
    if (currentIndex < chunks.length - 1)
      audioInstance.pause();
    currentIndex++;
    // 播放下一个音频块前，先清除已播放的音频块的引用
    delete pendingAudioBlobs[currentIndex - 1];
    playNextAudio(); // 递归调用播放下一个
    }
    // 开始播放
    source.start(0);
    console.log('开始播放音频');
}

export { textToSpeechAndPlay, stopPlayback, isPlaying }