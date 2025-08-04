<template>
	<div>
		<!-- <button @click="toggleRecording">
      {{ isRecording ? 'Stop Recording' : 'Start Recording' }}
    </button>
    <p v-if="recordingStatus">{{ recordingStatus }}</p> -->
		<i
			v-if="!props.dialogueInputLoading && isHaveTtsId()"
			class="vedioBtn"
			id="btn_control"
			@click="toggleRecording"
			:style="props.isMobile ? '' : 'top:calc(50% - 13px);right:96px;color: rgb(65, 134, 244);'"
		>
			<img v-if="!isRecording" src="/src/assets/chatImages/yuyin-new.png" class="sendVoiceBtn" />
			<div v-else class="vedioLoaingBtn" :style="props.isMobile ? '' : 'top:-2px;'">
				<div class="time-box">
					<span class="start-taste-line">
						<hr class="hr1" />
						<hr class="hr2" />
						<hr class="hr3" />
						<hr class="hr4" />
						<hr class="hr5" />
						<hr class="hr6" />
						<hr class="hr7" />
						<hr class="hr8" />
						<hr class="hr9" />
					</span>
				</div>
				<CoolStopCircleLineWe size="28" color="var(--w-color-primary)" />
			</div>
		</i>
	</div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import mittBus from '/@/utils/mitt';
import { ElMessage } from 'element-plus'

const isRecording = ref(false);
const recordingStatus = ref('');
const chunks = ref<Blob[]>([]);
const mediaRecorder = ref<MediaRecorder | null>(null);
const stream = ref<MediaStream | null>(null);
const timeoutId = ref<NodeJS.Timeout | null>(null);

interface Props {
	isMobile?: boolean;
	dialogueInputLoading?: boolean;
}

const props = defineProps<Props>();
const route = useRoute();

const isHaveTtsId = () => {
	let appInfo = JSON.parse(window.localStorage.getItem(`${route.params.appId}`));
	return appInfo && appInfo.voiceDialogueFlag == '是' ? true : false;
};

const toggleRecording = async () => {
	if (!isRecording.value) {
		startRecording();
	} else {
		stopRecording();
	}
};

const startRecording = async () => {
	try {
		stream.value = await navigator.mediaDevices.getUserMedia({ audio: true });
		mediaRecorder.value = new MediaRecorder(stream.value, { mimeType: 'audio/webm' });
		mediaRecorder.value.addEventListener('dataavailable', handleDataAvailable);
		mediaRecorder.value.addEventListener('stop', handleStop);
		mediaRecorder.value.start(1000); // Record at 1000ms intervals
		isRecording.value = true;
		recordingStatus.value = 'Recording...';
		timeoutId.value = setTimeout(stopRecording, 60 * 1000); // Stop recording after 60 seconds
	} catch (error) {
    ElMessage.error('录音异常，请检查是否插入麦克风设备及开启麦克风权限')
		recordingStatus.value = 'Failed to start recording.';
	}
};

const stopRecording = () => {
	if (mediaRecorder.value) {
		mediaRecorder.value.stop();
	}
	if (stream.value) {
		stream.value.getTracks().forEach((track) => track.stop());
	}
	isRecording.value = false;
	clearTimeout(timeoutId.value);
	sendAudio();
};

const handleDataAvailable = (event: BlobEvent) => {
	if (event.data.size > 0) {
		chunks.value.push(event.data);
	}
};

const handleStop = () => {
	recordingStatus.value = 'Recording stopped.';
};

const sendAudio = async () => {
	const blob = new Blob(chunks.value, { type: 'audio/webm' });
	const formData = new FormData();
	formData.append('file', blob, 'recording.webm');
	formData.append('foldersId', 'sefddwfwef');
  chunks.value = []
	try {
		const response = await axios.post(`${import.meta.env.VITE_API_URL}${import.meta.env.VITE_BASE_API_URL}/wos/file/upload`, formData, {
			headers: {
				'Content-Type': 'audio/wave;multipart/form-data',
			},
		});
		if (response.status === 200) {
			recordingStatus.value = 'Audio sent successfully!';
			audioTransforText(response.data);
		} else {
			recordingStatus.value = 'Failed to send audio.';
		}
	} catch (error) {
		console.error('Error sending audio:', error);
		recordingStatus.value = 'Failed to send audio.';
	}
};
const emit = defineEmits(['sendAudio']);

// 语音转文本
const audioTransforText = async (data: any) => {
	try {
		const response = await axios.post('/transcribe', { url: data.data[0].urlPath });
		if (response.status === 200) {
			emit('sendAudio', response.data);
		}
	} catch (error) {
		console.error('Error audioTransforText:', error);
	}
};

onMounted(() => {
	if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
		recordingStatus.value = 'Ready to record.';
	} else {
		recordingStatus.value = 'Your browser does not support recording.';
	}
});
onUnmounted(() => {
	mittBus.all.delete('setsendAudioMessage', () => {});
});
</script>

<style scoped lang="scss">
@import '/@/theme/mixins/index.scss';

.chatInput {
	width: 100%;
	position: relative;
	// padding: 10px;

	:deep(.w-textarea) {
		line-height: 1.5 !important;
		@include add-size($font-size-base16, $size);
		padding: 4px 120px 4px 62px;
	}

	.w-textarea-wrapper {
		// border: 1px solid #4085F4;
		border-bottom-right-radius: 12px;
		border-bottom-left-radius: 12px;
		width: 100%;
	}

	.sendBtn {
		width: 64px;
		height: 40px;
		background: linear-gradient(270deg, #31cdca 0%, #169e9a 100%);
		border-radius: 8px 2px 8px 8px;
		// border-radius: 22px;
		display: flex;
		align-items: center;
		justify-content: center;
		position: absolute;
		right: 0px;
		top: 0px;
		// background: none;
		border: none;
		font-weight: 500;
		font-size: 14px;
		color: #ffffff;
		line-height: 40px;
		text-align: center;

		&.yy {
			right: 60px;
		}
	}

	.sendBtnMobile {
		bottom: 24px;
	}

	.vedioBtn {
		position: absolute;
		right: 100px;
		bottom: 16px;
	}

	.sendVoiceBtn {
		width: 24px;
		border-radius: 50%;
		cursor: pointer;
	}

	.vedioLoaingBtn {
		height: 32px;
		cursor: pointer;
		display: flex;
		padding: 0 6px 0 14px;
		justify-content: space-between;
		align-items: center;
		border-radius: 18px;
		position: relative;
		top: 4px;
	}

	.time-box {
		margin-top: -6px;

		.start-taste-line {
			hr {
				background-color: var(--w-color-primary); //声波颜色
				width: 2px;
				height: 3.5px;
				margin: 0 0.1rem;
				display: inline-block;
				border: none;
				border-radius: 0.5px;
			}
		}

		hr {
			animation: note 0.4s ease-in-out;
			animation-iteration-count: infinite;
			animation-direction: alternate;
		}

		.hr1 {
			animation-delay: -1s;
		}

		.hr2 {
			animation-delay: -0.9s;
		}

		.hr3 {
			animation-delay: -0.8s;
		}

		.hr4 {
			animation-delay: -0.7s;
		}

		.hr5 {
			animation-delay: -0.6s;
		}

		.hr6 {
			animation-delay: -0.5s;
		}

		.hr7 {
			animation-delay: -0.4s;
		}

		.hr8 {
			animation-delay: -0.3s;
		}

		.hr9 {
			animation-delay: -0.2s;
		}

		.hr10 {
			animation-delay: -0.1s;
		}

		@keyframes note {
			from {
				transform: scaleY(1);
			}

			to {
				transform: scaleY(4);
			}
		}
	}
}

.chatInput-pc {
	:deep(.w-textarea) {
		max-height: 70px;
		padding: 16px 120px 4px 62px;
		min-height: 56px !important;
	}
	.w-textarea-wrapper {
		max-height: 70px;
		min-height: 56px;
		border: 1px solid #d0d5dc;
		overflow-y: scroll;
		&::-webkit-scrollbar {
			display: none !important;
		}
	}
}

.chatInput-mobile {
	position: fixed;
	left: 0;
	bottom: 0;
	width: 100%;
	padding: 0;

	.w-textarea-wrapper {
		width: calc(100vw - 70px);
		max-height: 70px;
		min-height: 56px;
		height: 44px;
		border: 1px solid #d0d5dc;
		overflow-y: scroll;
	}

	:deep(.w-textarea) {
		max-height: 70px;
		min-height: 56px !important;
	}
}
</style>