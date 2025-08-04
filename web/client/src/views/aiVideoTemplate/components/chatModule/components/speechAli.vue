<template>
	<div>
		<i class="vedioBtn" id="btn_control" @click="toggleRecording"
			:style="isMobile ? '' : 'bottom:13px;;right:100px;color: rgb(65, 134, 244);px;'">
			<iconpark-icon v-if="!isRecording" name="mic-fill" size="20" color="#2065D6"></iconpark-icon>
			<div v-else class="vedioLoaingBtn" :style="isMobile ? '' : 'top:-2px;'">
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

<script lang="ts" setup>
	import { defineAsyncComponent, defineProps, ref, onMounted, defineEmits, computed, onUnmounted, watch, nextTick } from 'vue';
	import mittBus from '/@/utils/mitt';
	import { useRoute } from 'vue-router';
	import { useBasicLayout } from '/@/hooks/useBasicLayout';
	import useSpeechRecognition from '/@/utils/useSpeechRecognition'

	const props = defineProps({
		appId: {
		type: String,
		required: true
	}
	});
	const emit = defineEmits(['changeResultText']);
	// 创建响应式参数（初始值可为空）
	const sttId = ref < number > ()
	const applicationId = ref < string > ()
	const appId = props.appId

	// 初始化语音识别（此时参数尚未设置）
	const {
		btnStatus,
		toggleRecording,
		isRecording,
		resultText,
		resultArr,
		stopRecording,
		currentTtsId,
		currentAppId
	} = useSpeechRecognition({
		sttId,
		appId,
		applicationId
	})

	// 移动端自适应相关
	const { isMobile } = useBasicLayout();
	const route = useRoute();
	onMounted(() => {
    let appInfo = localStorage.getItem(`${route.params.appId}`)?JSON.parse(localStorage.getItem(`${route.params.appId}`)):'';
    applicationId.value = appInfo?.applicationId
    sttId.value = appInfo?.sttId
	});
	onUnmounted(() => {
	});

	watch(
		() => resultText.value,
		(val) => {
			emit('changeResultText', val)
		}
	);

	watch(
		() => btnStatus.value,
		(val) => {
			let flag = false;
			if (val === 'UNDEFINED' || val === 'CLOSED' || val === 'CLOSING') {
				flag = false;
			} else {
				flag = true;
			}
			mittBus.emit('isVedioIng', flag);
		}
	);
	// 打开预览
const stop = () => {
	stopRecording();
};
const clear = () => {
	resultText.value = '';
	resultArr.value = [''];
};
// 导出方法
defineExpose({
	stop, clear
});
</script>

<style scoped lang="scss">
	.vedioBtn {
		position: absolute;
		right: 100px;
		bottom: 16px;
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
</style>