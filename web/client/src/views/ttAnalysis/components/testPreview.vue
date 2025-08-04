<template>
	<div class="test-preview-wrap" ref="tagsView">
		<div class="form-box">
			<div class="form-list">
				<div class="form-item" v-for="(item, index) in testList" :key="index">
					<div class="form-item-title">
						<span>{{ index + 1 }}、</span> {{ item.topic }}
					</div>
					<div class="form-item-content">
					
						<el-radio-group v-if="item.topicType == '单选'" v-model="item.selectAnswer">
							<el-radio v-for="(labelText, idx) in item.option" :value="selectIds[idx]">{{ selectIds[idx] }}. {{ labelText }}</el-radio>
						</el-radio-group>
                        <!-- <el-checkbox-group v-if="item.type == '多选题'" v-model="item.value">
                            <el-checkbox v-for="(labelText, idx) in item.options" :label="selectIds[idx] + '. ' + labelText" :value="selectIds[idx]" />
                        </el-checkbox-group> -->
					</div>
				</div>
			</div>
		</div>
		<div class="footer-btn">
			<el-button type="primary" @click="submitForm">保存</el-button>
		</div>
	</div>
</template>
<script lang="ts" setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useChatStore } from '/@/stores/chat';
import { apiGetApplicationInfoDialogueList,apiSaveDialogueRecord } from '/@/api/chat';
import { json } from 'stream/consumers';
import { ElMessage } from 'element-plus'

const route = useRoute();
const selectIds = Array.from({ length: 26 }, (_, i) => String.fromCharCode(i + 65));
const chatStore = useChatStore()
const testList = reactive([])
let detailData = reactive({})
const getApplicationInfoDialogueList = async ()=>{
	let res = await apiGetApplicationInfoDialogueList({dialogueId: route.params.dialogueId, pageNo: 1,pageSize: 1})
	console.log(res)
	if(res.code = "000000") {
		let arr = res.data?.records[0]?.answer ? JSON.parse(res.data.records[0].answer):[]
		arr.forEach(el=>{
			testList.push(el)
		})
		detailData = res.data?.records[0]
	}
}
const submitForm = ()=>{
	console.log(detailData)
	let params =  { 
		"id": detailData.id, 
		"applicationId": detailData.applicationId, 
		"componentId": detailData.componentId,
		"question": detailData.question, 
		"answer": JSON.stringify(testList), 
		"dialogueId": detailData.dialogueId,
		"clientId": detailData.clientId,
		"showRecommendedQuestions": detailData.showRecommendedQuestions,
	 	"output": detailData.output, 
	 	"answerFlag": detailData.answerFlag, 
	 	"answerType": detailData.answerType 
	}
	apiSaveDialogueRecord(params).then((res)=>{
		if(res.code = "000000"){
			ElMessage({
				message: '保存成功',
				type: 'success',
			})
		}
	})
}
console.log(selectIds);
// const testList = reactive([
// 	{
// 		title: '您目前的就业状态是？',
// 		options: ['在校未就业', '待业中', '已签订劳动合同', '自由职业', '自主创业', '其他'],
// 		type: '单选题',
// 		value: '',
// 	},
// 	{
// 		title: '您目前的就业状态是？',
// 		options: ['在校未就业', '待业中', '已签订劳动合同', '自由职业', '自主创业', '其他'],
// 		type: '单选题',
// 		value: '',
// 	},
// 	{
// 		title: '您目前的就业状态是？',
// 		options: [
// 			'在校未就业',
// 			'待业中',
// 			'已签订劳动合同',
// 			'自由职业',
// 			'自主创业',
// 			'其他',
// 			'自由职业',
// 			'自主创业',
// 			'其他',
// 			'自由职业',
// 			'自主创业',
// 			'其他',
// 			'自由职业',
// 			'自主创业',
// 			'其他',
// 		],
// 		type: '多选题',
// 		value: [],
// 	},
// ]);

const onSubmit = () => {
	console.log('submit!');
};

onMounted(() => {
	getApplicationInfoDialogueList()
});
</script>
<style lang="scss" scoped>
.test-preview-wrap {
	width: 100%;
	height: 100%;
	overflow: auto;
	.form-box {
		display: flex;
		justify-content: center;
        width: 90%;
		// padding: 0 100px;
        margin: 0 auto;
		padding-top: 50px;
		padding-bottom: 100px;
		.form-item {
			margin-top: 30px;
			.form-item-title {
				font-size: 16px;
				margin-bottom: 8px;
			}
			.form-item-content {
				padding: 0 20px;
			}
		}
	}
	.footer-btn{
		display: flex;
		align-items: center;
		justify-content: center;
		padding-bottom: 100px;
		.el-button {
			width: 120px;
			height: 40px;
		}
	}
}
</style>
