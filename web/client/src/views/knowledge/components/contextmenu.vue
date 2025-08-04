<template>
	<transition name="w-zoom-in-center">
		<ul class="nav-list-2">
			<li class="list-2-item flex-s-b" v-for="(item, idx) in tagOptions" :key="idx" @click="onCurrentContextmenuClick(item.value)">
				<CoolAddLineWe v-if="item.value === 1" size="14" class="item-icon"/>
				<CoolUploadLineWe v-else-if="item.value === 2" size="14" class="item-icon"/>
				<CoolEditLineWe v-else-if="item.value === 3" size="14" class="item-icon"/>
				<CoolDeleteBinLineWe v-else size="14" class="item-icon"/>
				<span class="item-text text-overflow">{{ item.label }}</span>
			</li>
		</ul>
	</transition>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, toRefs, onMounted, onUnmounted, watch, ref } from 'vue';

export default defineComponent({
	name: 'layoutTagsViewContextmenu',
	props: {
		type: {
			type: Number,
			default: 1
		},
		item: {
			type: Object,
			default: () => {
				return {};
			},
		},
	},
	setup(props, { emit }) {
		const mode = computed(() => props.type)
		const delTips:any = computed(() => {
			let isWjj = props.item.type === 1;
			let isChild = props.item?.isChild ? props.item?.isChild.length : false;
			let msg = '您确认要删除该文档吗？一旦删除不可恢复';
			if(isWjj){
				msg = '您确认要删除该目录及其下的所有文档吗？一旦删除不可恢复';
			}else if(isChild){
				msg = '您确认要删除根目录及其子目录下的所有文档吗？一旦删除不可恢复';
			}
			return msg
		})
		const tagOptions: any = computed(()=>{
			let add = [
				{
					label: '新增目录',
					value: 1,
				},
				{
					label: '上传文件',
					value: 2,
				},
			]
			let zs = [
				{
					label: '编辑',
					value: 3,
				},
				{
					label: '删除',
					value: 4,
				}
			]
			let wd = [
				{
					label: '新增目录',
					value: 1,
				},
				{
					label: '上传文件',
					value: 2,
				},
				{
					label: '重命名',
					value: 3,
				},
				{
					label: '删除',
					value: 4,
				},
			]
			let file = [];
			if(props.item.type === 0){
				file = [...zs]
				file[0].label = '重命名';
				return file
			}else if(props.item.level >= 6){
				wd.splice(0,1)
				return wd
			}
			if(props.type == 1){
				return zs
			}else if(props.type == 2){
				return wd
			}else if(props.type == 3){
				return add
			}
			return props.type == 1 ? zs : wd
		});
		// 当前项菜单点击
		const onCurrentContextmenuClick = (value: number) => {
			emit('currentContextmenuClick', Object.assign({}, { value }, props.item));
		};
		return {
			tagOptions,
			mode,
			delTips,
			onCurrentContextmenuClick,
		};
	},
});
</script>

<style lang="scss">

.nav-select-popover {
	padding: 0 !important;
}
.nav-handle-popover {
	width: auto !important;
	min-width: auto !important;
	border-radius: 8px !important;
    overflow: hidden;
}
.w-popover-content{
	padding: 0 !important;
}
.nav-list-2 {
	padding: 12px 10px;
	background: #FFFFFF;
	box-shadow: 0px 4px 8px 0px rgba(51,51,51,0.08);
	max-height: 400px;
	overflow-x: hidden;
	overflow-y: auto;
	.list-2-item {
		height: 32px;
		line-height: 32px;
		cursor: pointer;
		padding: 0 10px;
		font-size: var(--font14);
		color: #646479;
		border-radius: 4px;
		&:hover {
			background: rgba(53, 94, 255, 0.06);
			color: #355eff !important;
			// .item-text,.item-icon > svg g g path:nth-child(2) {
			// 	color: #355eff !important;
			// 	stroke: #355eff;
			// }
		}
		&.active {
			background-color: #f8f8f8;
		}
		.item-text {
			display: inline-block;
			width: calc(100% - 22px);
			font-size: var(--font14);
			text-align: left;
		}
		.item-icon > svg {
			size: var(--font24);
			cursor: pointer;
			margin-right: 8px;
		}
	}
}
</style>
