<template>
	<div class="delModal">
		<w-modal width="520px" modal-class="knowledgeModal knowledgeDelModalBG" :mask-closable="false" v-model:visible="visible">
			<template #title>
				您确定要删除当前知识库吗？
			</template>
			<div class="tips">删除知识库后，知识库内的所有文件将立即被永久删除，此操作不可撤销，请输入「{{dataItem.name}}」用以确认。</div>
			<w-input v-model="name" placeholder="请输入知识库名称以确认" clearable />
			<template #footer>
				<w-button @click="handleCancel">取消</w-button>
				<w-button type="primary" @click="handleOk">确定</w-button>
			</template>
		</w-modal>
	</div>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, toRefs, onMounted, onUnmounted, watch, ref } from 'vue';
import { useKnowledgeState } from '/@/stores/knowledge';
import { Message,Notification,Modal } from 'winbox-ui-next';
import { deleteKnowledge,userKnowledgesSize } from '/@/api/knowledge';
export default defineComponent({
	name: 'delModal',
	props: {
		type: {
			type: Number,
			default: 2
		}
	},
	setup(props, { emit }) {
		const knowledgeState = useKnowledgeState();
		const addEditModal:any = computed(() => knowledgeState.addEditModal)
		const visible = ref(false)
		const name = ref('')
		const dataItem:any = ref({})
		let callback:any = null;
		const handleClick = (item:any,callbackFn:Function|null = null) =>  {
			callback = callbackFn
			name.value = '';
			visible.value = true;
			dataItem.value = item
		}
		const handleOk = async () =>  {
			if(dataItem.value.name == name.value){
				let params = {
					id:dataItem.value.id,
					name: name.value
				}
				let res = await deleteKnowledge(params)
				if (res?.code === 200 && res?.data) {
					refreshKnowledgesSize();
					Message.success(`删除成功！`);
					if(callback){
						callback && callback()
					}else{
						addEditModal.value.callback && addEditModal.value.callback()
					}
					handleCancel()
				}else{
					Message.warning(res.msg);
				}
			}else{
				Message.warning('知识库名称不一致')
			}
		}
		const refreshKnowledgesSize = async () => {
			let resSize = await userKnowledgesSize();
			if (resSize?.code === 200 && resSize?.data) {
				knowledgeState.setKnowledgesSize(resSize.data);
			}
		};
		const handleCancel = () =>  {
			visible.value = false;
		}
		return {
			visible,
			name,
			dataItem,
			handleClick,
			handleOk,
			handleCancel
		};
	},
});
</script>

<style lang="scss" scoped>

.tips{
	font-size: var(--font14);
	font-family: PingFangSC-Regular, PingFang SC;
	font-weight: 400;
	color: #646479;
	line-height: var(--font24);
	margin-bottom: 44px;
	text-align:justify;
	text-align-last: left;
}
.w-input{
	margin-bottom: 24px;
}
</style>
