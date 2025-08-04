import openApiRequest from './bundle-es.js';

let currentIndex = 0; // 用于跟踪当前播放的音频块索引
let playIndex = 0; // 用于跟踪当前播放的音频块索引
let playArr = {}; // 播放的音频
let playJson = {}; // 播放的音频
let oldChunks = [];
let oldClientId = '';
let isPlaying = false; // 标志位，确保 startPlay 只被调用一次

function splitTextIntoChunks(answer, previousAnswer, previousChunks) {
  if (answer === previousAnswer) {
    return [];
  }

  let chunks = [];
  let start = previousAnswer?.length;
  let end = start;

  while (end < answer?.length) {
    if (answer[end] === '，' || answer[end] === '。' || answer[end] === '：' || answer[end] === '？' || answer[end] === '！' || answer[end] === '>') {
      let chunk = answer.slice(start, end + 1);
      if (chunk) {
        chunks.push(chunk);
      }
      start = end + 1;
    }
    end++;
  }

  previousAnswer = answer;
  previousChunks = chunks;

  return chunks;
}

function ttsSync(text, index, onStop) {
  let cleanedText = text.replace(/(\\n)/g, '');
  cleanedText = cleanedText.replace(/<a[^>]*>.*?<\/a>/g, '');
  let ttsConfig = JSON.parse(localStorage.getItem('ttsConfig'));
  const requests = new openApiRequest(
    {
      urlText: '',
      urlTts: '',
      AppKey: ttsConfig.apiKey,
      AppSecret: ttsConfig.apiSecret
    }
  );

  requests.ttsSync({
    data: {
      speaker: ttsConfig.voicePeople,
      ignore_limit: true,
      gen_srt: true,
      streaming: true,
      audio_type: "mp3",
      text: cleanedText,
      speed: "1.0",
    },
    onMessage(res) {
      const audioBlob = new Blob([res], { type: 'audio/mpeg' });
      const audioUrl = URL.createObjectURL(audioBlob);
      //console.log(playArr.length + cleanedText + audioUrl);
      playArr[index] = audioUrl;

      // 确保 startPlay 只被调用一次
      if (playIndex === 0 && !isPlaying) {
        isPlaying = true;
        setTimeout(startPlay(onStop), 1000);
      }
    },
    onError(err) {
      console.log(err, 'err');
    }
  });
}

function startPlay(onStop) {
  if (playIndex >= playArr?.length) {
    // 所有音频片段播放完毕
    stopPlay(onStop);
    return;
  }

  playJson[playIndex] = new Audio();
  playJson[playIndex].src = playArr[playIndex];
  //playJson[playIndex].playbackRate = 1.2;

  playJson[playIndex].addEventListener('ended', () => {
    playIndex++;
    setTimeout(startPlay(onStop), 500); // 延迟 500ms 后播放下一个音频片段
  });

  playJson[playIndex].play().catch((error) => {
    console.error('播放错误:', error);
    setTimeout(startPlay(onStop), 500); // 延迟 500ms 后尝试播放下一个音频片段
  });
}

// 主播放逻辑
async function textToSpeechAndPlay(newChunks, clientId, onStop) {
  if (oldClientId !== clientId) {
    oldClientId = clientId;
    stopPlay(); // 传递回调
  }

  if (newChunks?.length > oldChunks?.length) {
    oldChunks = newChunks;
    ttsSync(oldChunks[currentIndex], currentIndex, onStop);
    currentIndex++;
  }
}

function stopPlay(callback) {
  for (const key in playJson) {
    if (Object.prototype.hasOwnProperty.call(playJson, key)) {
      const element = playJson[key];
      if (element) {
        element.removeEventListener('ended', () => {});
        element.pause();
        playJson[key] = null;
      }
    }
  }

  currentIndex = 0;
  playIndex = 0;
  oldChunks = [];
  playArr = [];
  playJson = {};
  isPlaying = false; // 重置标志位
  // 执行回调（如果有）
  if (callback) callback();
}

export { textToSpeechAndPlay, splitTextIntoChunks, stopPlay };