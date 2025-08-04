<template>
    <div class="create-evaluation-task" v-loading="submitLoading">
        <div class="header">
            <div class="back" @click="backPage">
                <img src="@/assets/images/arrow-go-back-fill.svg" />
            </div>
            <span>创建评测任务</span>
        </div>
        <div class="content">
            <div class="content-tab">
                <!-- <el-radio-group size="small" v-model="tabPosition" style="margin-bottom: 30px;">
                    <el-radio-button label="left">1 选择评测对象</el-radio-button>
                    <el-radio-button label="center">2 选择评测数据集</el-radio-button>
                    <el-radio-button label="right">3 设置评测规则</el-radio-button>
                </el-radio-group> -->
                <div class="step-wrap">
                    <div class="step-item">
                        <div class="step-item-icon" :style="{ 'background': tabPosition == 'left' ? 'rgba(23, 71, 229,1)' : 'rgba(23, 71, 229,0.1)' }">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" :fill="tabPosition == 'left' ? '#fff':'#1747E5'" d="M17 3h-4V2h-2v1H7a3 3 0 0 0-3 3v3a5 5 0 0 0 5 5h6a5 5 0 0 0 5-5V6a3 3 0 0 0-3-3Zm-6 5.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0Zm5 0a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0ZM4 23a8 8 0 1 1 16 0H4Z" data-follow-fill="#848587"/></svg>
                        </div>
                        <div class="step-item-inner">选择评测对象</div>
                    </div>
                    <div class="line"></div>
                    <div class="step-item">
                        <div class="step-item-icon" :style="{ 'background': tabPosition == 'center' ? 'rgba(23, 71, 229,1)' : tabPosition == 'right' ? 'rgba(23, 71, 229,0.1)' : '#F2F3F5'}">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" :fill="tabPosition == 'center' ? '#fff':tabPosition == 'right' ? '#1747E5':'rgba(134, 144, 156, 1)'" d="M21 9v3c0 2.485-4.03 4.5-9 4.5S3 14.485 3 12V9c0 2.485 4.03 4.5 9 4.5s9-2.015 9-4.5ZM3 14c0 2.485 4.03 4.5 9 4.5s9-2.015 9-4.5v3c0 2.485-4.03 4.5-9 4.5S3 19.485 3 17v-3Zm9-2.5c-4.97 0-9-2.015-9-4.5s4.03-4.5 9-4.5 9 2.015 9 4.5-4.03 4.5-9 4.5Z" data-follow-fill="#848587"/></svg>
                        </div>
                        <div class="step-item-inner">选择评测数据集</div>
                    </div>
                    <div class="line"></div>
                    <div class="step-item">
                        <div class="step-item-icon" :style="{ 'background': tabPosition == 'right' ? 'rgba(23, 71, 229,1)' : '#F2F3F5'}">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" :fill="tabPosition == 'right' ? '#fff':'rgba(134, 144, 156, 1)'" d="M6.17 18a3.001 3.001 0 0 1 5.66 0H22v2H11.83a3.001 3.001 0 0 1-5.66 0H2v-2h4.17Zm6-7a3.001 3.001 0 0 1 5.66 0H22v2h-4.17a3.001 3.001 0 0 1-5.66 0H2v-2h10.17Zm-6-7a3.001 3.001 0 0 1 5.66 0H22v2H11.83a3.001 3.001 0 0 1-5.66 0H2V4h4.17Z" data-follow-fill="#848587"/></svg>
                        </div>
                        <div class="step-item-inner">设置评测规则</div>
                    </div>
                </div>
            </div>
            <div class="form-wrap">
                <transition name="el-fade-in-linear">
                    <addEvaluationObj v-if="tabPosition == 'left'" :params="addForm" @cancel="backPage" @next="nextStep" @update="updateAddForm"></addEvaluationObj>
                </transition>
                <transition name="el-fade-in-linear">
                    <addEvaluationData v-if="tabPosition == 'center'" :params="addForm" @cancel="backPage" @next="nextStep" @update="updateAddForm"></addEvaluationData>
                </transition>
                <transition name="el-fade-in-linear">
                    <addEvaluationRule v-if="tabPosition == 'right'" :params="addForm" @cancel="backPage" @next="nextStep" @submit="submitForm" @update="updateAddForm"></addEvaluationRule>
                </transition>
            </div>

        </div>
		<el-dialog title="温馨提示" :visible.sync="deleteDialogVisible" width="560px" class="deleteDialog"
			 append-to-body>
			<p class="desc">内容尚未保存，是否确认退出？</p>
			<span slot="footer" class="dialog-footer">
				<el-button @click="deleteDialogVisible = false">{{ $t("cancel") }}</el-button>
				<el-button type="primary" @click="confirmDelete">确定</el-button>
			</span>
		</el-dialog>
    </div>
</template>
<script>
import addEvaluationObj from './addEvaluationObj.vue'
import addEvaluationData from './addEvaluationData.vue'
import addEvaluationRule from './addEvaluationRule.vue'
import { apiInsertApplicationEvaluation } from "@/api/effectEvaluation";
export default {
    name: "createEvaluationTask",
    components: {
        addEvaluationObj,
        addEvaluationData,
        addEvaluationRule
    },
    data() {
        return {
            tabPosition: 'left',
            addForm: {},
			deleteDialogVisible: false,
            submitLoading: false
        }
    },
    methods: {
        updateAddForm(val){
            this.addForm = { ...this.addForm, ...val }
        },
        submitForm(val){
            console.log(this.addForm)
            this.submitLoading = true
            let params = {
                applicationId: this.addForm.applicationId,
                evaluationDataset: this.addForm.fileList[0].name,
                evaluationRules: this.addForm.cueWord,
                evaluationObject: this.addForm.applicationName,
                llmInfoId: this.addForm.modelId,
                datasetId: this.addForm.fileList[0].datasetId,
                taskName: this.addForm.taskName,
                refereeModel: this.addForm.modelName,
                cueWord: this.addForm.cueWord,
                applicationCode: this.addForm.applicationCode
            }

            apiInsertApplicationEvaluation(params).then(res=>{
                this.submitLoading = false
                if(res.code == '000000') {
                    this.$message.success('创建成功')
                    this.$emit('closePage')
                } else {
                    this.$message.error(res.message)
                }
            }).catch((error)=>{
                this.submitLoading = false
                this.$message.error(error)
            })
        },
        backPage(){
			if(this.addForm.taskName!=''||this.addForm.cueWord!=''||this.addForm.modelName!=''||this.addForm.fileList[0].name!=''){
				this.deleteDialogVisible = true
			}else{
				this.$emit('closePage')
			}
            
        },
        beforeStep(){
            console.log(1)
            return false
        },
		confirmDelete(){
           this.$emit('closePage')
        },
        nextStep(data) {
            this.tabPosition = data
        }
    }
}
</script>

<style lang="scss" scoped>
.create-evaluation-task{
    width: 100%;
    height: 100%;
    background-color: #F7F8FA;
    .header{
        display: flex;
        align-items: center;
        height: 72px;
        padding: 0 28px;
        font-size: 18px;
        background: #fff;
        .back{
            img{
                cursor: pointer;
                width: 20px;
                height: 20px;
                margin-right: 10px;
            }
        }
    }
    .content{
        padding: 0 28px;
        height: calc(100% - 72px);
        overflow: auto;
        .content-tab{
            margin: 24px auto;
            width: 55%;
            // display: flex;
            // justify-content: center;
            // ::v-deep .el-radio-button__inner{
            //     width: 300px;
            // }
            .step-wrap{
                display: flex;
                width: 100%;
                align-items: center;
                justify-content: space-between;
                color: #1D2129;
                .line{
                    height: 4px;
                    flex:1;
                    border-radius: 2px;
                    background-color: #E7E7E7;
                    margin: 0 16px;
                }
                .step-item{
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    .step-item-icon{
                        width: 48px;
                        height: 48px;
                        background: #F2F3F5;
                        border-radius: 50%;
                        font-size: 14px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-right: 12px;
                        svg{
                            width: 20px;
                            height: 20px;
                        }
                    }
                    .step-item-inner{
                        white-space: nowrap;
                        color: #1D2129;
                        font-weight: 500;
                        font-size: 16px;
                    }
                }
            }
        }
        .form-wrap{
            height: calc(100% - 120px);
            padding-bottom: 10px;
        }
    }
    
}
</style>
