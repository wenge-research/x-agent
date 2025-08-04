<template>
	<div>
		<i class="vedioBtn" id="btn_control" @click="toggleRecording"
			:style="isMobile ? '' : 'top:calc(50% - 13px);right:96color: rgb(65, 134, 244);px;'">
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
	import { defineAsyncComponent, ref, onMounted, defineEmits, computed, onUnmounted, watch, nextTick } from 'vue';
	import mittBus from '/@/utils/mitt';
	import { useRoute } from 'vue-router';
	import { useBasicLayout } from '/@/hooks/useBasicLayout';
	import useSpeechRecognition from '/@/utils/useSpeechRecognition'
	const emit = defineEmits(['changeResultText']);
	// 创建响应式参数（初始值可为空）
	const ttsId = ref < number > ()
	const applicationId = ref < string > ()

	// 初始化语音识别（此时参数尚未设置）
	const {
		btnStatus,
		toggleRecording,
		isRecording,
		resultText,
		stopRecording,
		currentTtsId,
		currentAppId
	} = useSpeechRecognition({
		ttsId,
		applicationId
	})

	// 移动端自适应相关
	const { isMobile } = useBasicLayout();
	const route = useRoute();
	onMounted(() => {
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