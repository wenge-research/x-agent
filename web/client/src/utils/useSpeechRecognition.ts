// src/composables/useSpeechRecognition.ts
import { ref, Ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAliToken } from '/@/api/knowledge';

type BtnStatus = 'UNDEFINED' | 'CONNECTING' | 'OPEN' | 'CLOSING' | 'CLOSED'
interface SpeechRecognitionOptions {
  sttId?: Ref<number>
  applicationId?: Ref<string>
  appId?: Ref<string>
}
export default function useSpeechRecognition(options: SpeechRecognitionOptions = {}) {
  // 参数默认值处理
  const sttId = options.sttId || ref(36)
  const applicationId = options.applicationId || ref("xxxxxxxx")
  const appId = options.appId || ref("zxxxxxxxx")
  // 状态管理
  const btnStatus: Ref<BtnStatus> = ref('CLOSED')
  const resultText = ref('')
  const resultArr = ref([''])
  const countdownSeconds = ref(0)
  const isRecording = ref(false)
  const chatFlag = ref(false)

  // WebSocket相关
  let iatWS: WebSocket | null = null
  // 音频处理相关
  let audioContext: AudioContext | null = null
  let scriptProcessor: ScriptProcessorNode | null = null
  let audioInput: MediaStreamAudioSourceNode | null = null
  let audioStream: MediaStream | null = null
  let countdownInterval: number | null = null

  // WebSocket URL生成
  const getWebSocketUrl = async () => {
  //  return getAliToken({applicationId: applicationId.value});
  return getAliToken({ttsId: sttId.value});
  }
  // 公共方法
  const toggleRecording = (type: string = '') => {

    if (btnStatus.value === 'UNDEFINED' || btnStatus.value === 'CLOSED') {
      connectWebSocket(type)
    } else {
      stopRecording()
    }
  }
  // 生成 UUID
  const generateUUID = () => {
    return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    ).replace(/-/g, '')
  }


  const connectWebSocket = async (type: string = '') => {
    changeBtnStatus('CONNECTING')
    try {
      let res = await getWebSocketUrl()
      let url = `wss://nls-gateway.cn-shanghai.aliyuncs.com/ws/v1?token=${res.data}`
      iatWS = new WebSocket(url)
      setupWebSocketHandlers(type)
    } catch (error) {
      ElMessage.error('浏览器不支持WebSocket')
    }
  }

  const setupWebSocketHandlers = (type: string) => {
    if (!iatWS) return

    iatWS.onopen = () => {
      if (type === 'toChat') chatFlag.value = true
      changeBtnStatus('OPEN')
      console.log(appId)
      const startTranscriptionMessage = {
        header: {
          appkey: appId,
          namespace: "SpeechTranscriber",
          name: "StartTranscription",
          task_id: generateUUID(),
          message_id: generateUUID()
        },
        payload: {
          format: "pcm",
          sample_rate: 16000,
          enable_intermediate_result: true,
          enable_punctuation_prediction: true,
          enable_inverse_text_normalization: true
        }
      }
      iatWS.send(JSON.stringify(startTranscriptionMessage))
      startRecording()
    }

    iatWS.onmessage = (e) => {
      handleWsMessage(e.data)
    }

    iatWS.onerror = () => {
      ElMessage.error('录音异常，请检查设备及权限')
      stopRecording()
    }

    iatWS.onclose = (e) => {
      console.log(e)
      stopRecording()
    }
  }

  const startRecording = async () => {
    try {
      audioStream = await navigator.mediaDevices.getUserMedia({ audio: true })
      setupAudioContext()
      isRecording.value = true
    } catch (error) {
      ElMessage.error('无法访问麦克风')
      stopRecording()
    }
  }

  const setupAudioContext = () => {
    audioContext = new (window.AudioContext || window.webkitAudioContext)({
      sampleRate: 16000
    })

    audioInput = audioContext.createMediaStreamSource(audioStream!)
    scriptProcessor = audioContext.createScriptProcessor(2048, 1, 1)

    scriptProcessor.onaudioprocess = (event) => {
      processAudioData(event)
    }

    audioInput.connect(scriptProcessor)
    scriptProcessor.connect(audioContext.destination)
  }

  const processAudioData = (event: AudioProcessingEvent) => {
    const inputData = event.inputBuffer.getChannelData(0)
    const inputData16 = new Int16Array(inputData.length)
    for (let i = 0; i < inputData.length; ++i) {
      inputData16[i] = Math.max(-1, Math.min(1, inputData[i])) * 0x7FFF
    }

    if (iatWS?.readyState === WebSocket.OPEN) {
      iatWS.send(inputData16.buffer)
    }
  }

  const stopRecording = () => {
    cleanupResources()
    changeBtnStatus('CLOSED')
    isRecording.value = false
    chatFlag.value = false
  }

  const cleanupResources = () => {
    // 清理音频资源
    scriptProcessor?.disconnect()
    audioInput?.disconnect()
    audioStream?.getTracks().forEach(track => track.stop())
    if (audioContext && audioContext.state !== "closed")
      audioContext?.close()

    // 清理WebSocket
    if (iatWS) {
      iatWS.close()
      iatWS = null
    }

    // 清理定时器
    if (countdownInterval) {
      clearInterval(countdownInterval)
      countdownInterval = null
    }
  }

  const changeBtnStatus = (status: BtnStatus) => {
    btnStatus.value = status
    updateStatusDisplay(status)
  }

  const updateStatusDisplay = (status: BtnStatus) => {
    switch (status) {
      case 'CONNECTING':
        break
      case 'OPEN':
        startCountdown()
        break
      case 'CLOSING':
        break
      case 'CLOSED':
        break
    }
  }

  const startCountdown = () => {
    countdownSeconds.value = 600
    countdownInterval = window.setInterval(() => {
      countdownSeconds.value -= 1
      if (countdownSeconds.value <= 0) {
        stopRecording()
      }
    }, 1000)
  }

  const handleWsMessage = (data: string) => {
    try {
      const jsonData = JSON.parse(data);
      if (jsonData.payload?.result) {
        console.log(jsonData.payload.index);
        let index = jsonData.payload.index
        if (index) {
          resultArr.value[index] = jsonData.payload.result;
          // If final, reset and set the result
          resultText.value = resultArr.value.join('');
        }
      }
    } catch (error) {
      console.error('消息解析错误:', error);
    }
  }

  // 暴露给组件的内容
  return {
    // 状态
    btnStatus,
    resultText,
    resultArr,
    isRecording,
    countdownSeconds,
    // 参数状态
    currentTtsId: sttId,
    currentappId: applicationId,
    // 方法
    toggleRecording,
    stopRecording
  }
}