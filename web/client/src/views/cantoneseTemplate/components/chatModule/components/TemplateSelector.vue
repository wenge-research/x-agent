<template>
	<div class="template-box" v-if="templatesList.length > 0">
		<!-- 模板类型标题 -->
		<div class="template-tits">
			<div
				class="template-tit"
				v-for="(item, index) in templatesList"
				:key="index"
				@click="toggleTemplateList(index)"
				:class="{ active: currentIndex === index }"
			>
				{{ item.name }}
			</div>
		</div>

		<!-- 模板列表 -->
		<div class="template-list" v-for="(item, index) in templatesList" :key="`list-${index}`" :class="{ active: currentIndex === index }">
			<div
				class="template-item"
				v-for="(sub, i) in item.templates"
				:key="i"
				@click="selectTemplate(sub)"
				:class="{ selected: selectedTemplateId === sub.templateId }"
			>
				<div class="template-name">
          <span class="img-box"><img :src="index === 0 ? docIcon : officeIcon" alt="" /></span>
					{{ sub.templateName }}
				</div>
				<div class="template-detail">{{ sub.detail }}</div>
			</div>
		</div>
	</div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import officeIcon from '/@/assets/chatImages/icon-office.png';
import docIcon from '/@/assets/chatImages/icon-doc.png';

const emit = defineEmits(['template-selected']);

// 接收父组件传入的数据
const props = defineProps({
  templatesList: {
    type: Array,
    required: true
  },
	selectedTemplateId: {
		type: [Number, String],
		default: null,
	},
});
const selectedTemplateId = ref(props.selectedTemplateId);
// 数据定义
const currentIndex = ref(0); // 当前选中的模板类型索引

// 方法定义
function toggleTemplateList(index) {
	currentIndex.value = index;
}

function selectTemplate(sub) {
	selectedTemplateId.value = sub.templateId;
  emit('template-selected', sub.templateId, sub.templateName);
}

onMounted(() => {
});
</script>

<style scoped>
.template-box {
	padding: 20px 12px;
  background: #FFFFFF;
  box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
  border-radius: 8px;
  border: 1px solid #E1E4EB;
}

.template-tits {
	display: flex;
	justify-content: flex-start;
	margin-bottom: 10px;
  padding: 0 0 0 6px;
}

.template-tit {
	padding: 0 16px;
	margin-right: 5px;
	border-radius: 5px;
  height: 32px;
  line-height: 32px;
  background: #F4F6F9;
  border-radius: 20px;
  font-size: 14px;
	cursor: pointer;
	color: #828894;
	transition: background-color 0.3s, color 0.3s;
}

.template-tit.active {
	background-color: #007bff;
	color: #fff;
}

.template-list {
	display: none;
	flex-wrap: wrap;
	margin-top: 5px;
}

.template-list.active {
	display: flex;
}

.template-item {
	width: calc(25% - 12px);
	margin: 6px;
	padding: 10px;
	background: #FFFFFF;
  border-radius: 8px;
  border: 1px solid #E1E4EB;
	cursor: pointer;
}

.template-item.selected {
	background-color: #007bff;
	color: #fff;
	border-color: #007bff;
}

.template-name {
	display: flex;
	align-items: center;
}
.template-name .img-box{
	background: #E85985;
  width: 24px;
	height: 24px;
  border-radius: 100%;
  padding: 4px;
  margin-right: 5px;
}
.template-item:nth-child(6n) .template-name .img-box{
	background: #0075FF;
}
.template-item:nth-child(6n+1) .template-name .img-box{
	background: #FF9700;
}
.template-item:nth-child(6n+2) .template-name .img-box{
	background: #34B0FF;
}
.template-item:nth-child(6n+3) .template-name .img-box{
	background: #E85985;
}
.template-item:nth-child(6n+4) .template-name .img-box{
	background: #2BCAC8;
}
.template-item:nth-child(6n+5) .template-name .img-box{
	background: #3EC254;
}
.template-item:nth-child(6n+6) .template-name .img-box{
	background: #DC76E0;
}

.template-name img {
	width: 16px;
	height: 16px;
}

.template-detail {
	font-size: 0.8em;
	color: #6c757d;
}
</style>
