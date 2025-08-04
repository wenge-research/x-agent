<template>
	<div class="addEditModal">
		<w-modal width="520px" modal-class="knowledgeModal knowledgeDelModalBG" popup-container=".addEditModal" :mask-closable="false" v-model:visible="addEditModal.show" @close="resetFields">
			<template #title>
				{{statusBlur ? '创建':'编辑'}}知识库
			</template>
			<w-form ref="wFrom" :model="state.form" :rules="state.rules" auto-label-width>
				<w-form-item field="name" label="知识库名称">
					<w-input v-model="state.form.name" :max-length="20" show-word-limit/>
				</w-form-item>
				<w-form-item field="descr" label="简介">
					<w-input v-model="state.form.descr" :max-length="40" show-word-limit/>
				</w-form-item>
				<w-form-item field="icon" label="图标">
					<ul class="iconList">
						<li class="icon" :style="{'background-color': state.bgColor[item]}" :active="item == state.form.icon" v-for="(item,index) in state.iconList" :key="index" @click="state.form.icon = item">
							<span>{{ item }}</span>
						</li>
					</ul>
				</w-form-item>
			</w-form>
			<template #footer>
				<w-button @click="handleCancel">取消</w-button>
				<w-button type="primary" :loading="!handleOkStatus" @click="handleOk">确定</w-button>
			</template>
		</w-modal>
	</div>
</template>

<script lang="ts" setup>
import { computed, defineComponent, reactive, unref, onMounted, onUnmounted, watch, ref } from 'vue';
import { useKnowledgeState } from '/@/stores/knowledge';
import { Message,Notification,Modal } from 'winbox-ui-next';
import { createKnowledge, editKnowledge } from '/@/api/knowledge';
import { IconList } from 'winbox-ui-next/es/icon';
import { useRoute, useRouter } from 'vue-router';
import { debounce } from 'lodash-es'
	const route = useRoute();
	const router = useRouter();
	const knowledgeState = useKnowledgeState();
	const addEditModal:any = computed(() => knowledgeState.addEditModal)
	const dataItem:any = computed(() => knowledgeState.dataItem)
	const wFrom = ref()
	let statusBlur = computed(()=> addEditModal.value.type === 1)
	const handleOkStatus = ref(true)
	const state = reactive({
		form: {
			name: '',
			descr: '',
			icon: '💻'
		},
		iconList: [
			'💻','📝','🧩','📍','🌠','📖'
		],
		bgColor: {
			'💻': 'rgba(53,94,255,0.06)',
			'📝': 'rgba(7, 190, 184, 0.06)',
			'🧩': 'rgba(102, 0, 255, 0.06)',
			'📍': 'rgba(255, 98, 0, 0.06)',
			'🌠': 'rgba(245, 75, 91, 0.06)',
			'📖': 'rgba(53, 94, 255, 0.06)'
		},
		rules:{
			name: [
				{ required: true, message: '请输入知识库名称' },
				{ type: 'number', maxLength: 20, message: '知识库名称不能超过20个字符' },
			]
		}
	})
	const handleOk = () =>  {
		unref(wFrom).validate(async (errors: Object) => {
			if (!errors) {
				if(!handleOkStatus.value){
					return
				}
				handleOkStatus.value = false
				let api = statusBlur.value ? createKnowledge : editKnowledge
				let params = Object.assign(state.form,{
					"authority": 2,
				})
				let id = dataItem.value.id;
				if(!statusBlur.value && id){
					params['id'] = id
				}
				let res = await api(params)
				handleOkStatus.value = true
				if (res?.code === 200 && res?.data) {
					Message.success(`${statusBlur.value? '创建' : '编辑'}成功！`);
					if(statusBlur.value){
						router.push({
							path: '/knowledge/'+res.data.id,
						})
					}
					addEditModal.value.callback && addEditModal.value.callback()
					resetFields()
				}else{
					Message.warning(res.msg);
				}
			}
		})
	}
	const resetFields = () => {
		knowledgeState.modelClose()
		unref(wFrom).resetFields()
	}
	const setStateForm = () => {
		state.form.name = dataItem.value?.name || ''
		state.form.descr = dataItem.value?.descr || ''
		state.form.icon = dataItem.value?.icon || '💻'
		handleOkStatus.value = true
	}
	const handleCancel = () =>  {
		resetFields()
		knowledgeState.modelClose()
	}
	watch(
		() => addEditModal.value.show,
		(val) => {
			setStateForm()
		}
	);
</script>

<style lang="scss" scoped>
:deep(.w-form){
	.w-row{
		display: block;
	}
	.w-form-item-label-col{
		display: block;
		margin-bottom: 8px;
		.w-form-item-label{
			position: relative;
			.w-form-item-label-required-symbol{
				transform: translate(-100%,-50%);
				position: absolute;
				top: 50%;
			}
		}
	}
}
:deep(.w-modal-footer) {
  .w-btn {
    border-radius: 4px;
  }
}
.iconList{
	width: 100%;
	display: flex;
	justify-content: space-between;
	.icon{
		// margin-right: 12px;
		// margin-bottom: 12px;
		display: inline-flex;
		align-items: center;
		justify-content: center;
		list-style: none;
		width: 60px;
		height: 60px;
		background: rgba($color: #6600FF, $alpha: 0.06);
		border-radius: 8px;
		font-size: var(--font24);
		font-family: AppleColorEmoji;
		color: #355EFF;
		cursor: pointer;
		&[active = true]{
			border: 1px solid #355EFF;
		}
	}
}
</style>
