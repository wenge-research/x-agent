<template>
	<div class="chatModule" @click="stop" :class="isMobile ? '': 'px-3'">
		<div class="con">
			<Parameter v-if="datapanelParas.show === 0"></Parameter>
			<Datapanel v-else></Datapanel>
			<ChatInput></ChatInput>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, computed } from 'vue';
import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { useChatStore } from '/@/stores/chat';
const { isMobile } = useBasicLayout();
const chatStore = useChatStore();
const datapanelParas = computed(() => chatStore.datapanelParas)
const Parameter = defineAsyncComponent(() => import('./components/parameter.vue'));
const Datapanel = defineAsyncComponent(() => import('./components/Datapanel.vue'));
const ChatInput = defineAsyncComponent(() => import('./components/chatInput.vue'));
const stop = (e) => {
	e.stopPropagation()
}
</script>

<style scoped lang="scss">
.chatModule {
	position: absolute;
	align-items: flex-end;
	justify-content: center;
	width: 62%;
	bottom: 16px;
	box-sizing: border-box;
	z-index: 100;
	left: 50%;
	transform: translateX(-50%);
	.con{
		width: 100%;
		border-radius: 16px;
		box-shadow: 0px 6px 20px 0px rgba(30,64,175,0.2);
	}
}
</style>
