<template>
    <div class="effectEvaluationTable-page">
        <div class="page-title">
            效果测评
        </div>
        <div class="seach-box">
            <div class="search-form">
                <el-select style="margin-right: 20px;" v-model="searchData.status" placeholder="请选择"
                    @change="getApplicationEvaluationList">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
                <el-date-picker v-model="dateArr" format="yyyy-MM-dd HH:mm:ss" value-format="yyyy-MM-dd HH:mm:ss"
                    type="datetimerange" :default-time="['00:00:00', '23:59:59']" range-separator="至"
                    start-placeholder="开始日期" end-placeholder="结束日期" @change="getApplicationEvaluationList">
                </el-date-picker>
            </div>
            <div class="search-btn">
                <el-button type="primary" @click="createEvaluation">创建</el-button>
            </div>
        </div>
        <div class="table-box">
            <el-table v-loading="tableLoading" height="100%" :data="tableData" class="my-table">
                <el-table-column width="60" prop="id" label="序号">
                    <template slot-scope="scope">
                        {{ scope.$index + 1 }}
                    </template>
                </el-table-column>
                <el-table-column prop="taskName" label="任务名称">
                </el-table-column>
                <el-table-column prop="evaluationObject" label="评测对象">
                </el-table-column>
                <el-table-column prop="evaluationRules" label="评测规则">
                    <template slot-scope="scope">
                        <!-- <div>{{ scope.row.evaluationRule }}</div> -->
                        <el-popover placement="right-start" trigger="hover">
                            <div class="effectEvaluationTable-popover-title">
                                <img style="width: 18px; height: 18px; margin-right: 8px"
                                    :src="getImg(scope.row.llmInfoId)" alt=""></img>
                                {{ scope.row.refereeModel }}
                            </div>
                            <div class="effectEvaluationTable-popover-content">
                                {{ scope.row.evaluationRules }}
                                <!-- {{ context }} -->
                            </div>
                            <el-button slot="reference" type="text">规则详情</el-button>
                        </el-popover>
                    </template>
                </el-table-column>
                <el-table-column prop="evaluationDataset" label="评测数据集">
                </el-table-column>
                <el-table-column prop="evaluationTime" label="评测时间" sortable>
                </el-table-column>
                <el-table-column prop="runningFrequently" label="运行时长(s)">
                </el-table-column>
                <el-table-column prop="evaluationStatus" label="评测状态">
                    <template slot-scope="scope">
                        <div class="status-box">
                            <div class="status-icon"
                                :class="{ 'warning': scope.row.evaluationStatus == 2, 'error': scope.row.evaluationStatus == 3 }">
                            </div>
                            {{ getStatusText(scope.row.evaluationStatus) }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column fixed="right" :label="$t('action')" width="200">
                    <template slot-scope="scope">
                        <div class="btns">
                            <el-button type="text" :disabled="scope.row.evaluationStatus == 2" @click="exportData(scope.row)">下载结果</el-button>
                            <!-- <el-button type="text" :disabled="scope.row.evaluationStatus == 2">复制</el-button> -->
                            <el-button type="text" :disabled="scope.row.evaluationStatus == 2"
                                @click="deleteData(scope.row)">删除</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>

        </div>
        <div class="pagination">
            <div class="total-num">
                {{ $t('totalData', { total: total, page: totalPage }) }}
            </div>
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="pageSize"
                :current-page="pageNo" :page-sizes="[20, 50, 100, 150, 200]" layout="prev, pager, next, sizes, jumper"
                background :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
import { apiGetApplicationEvaluationList, apiDeleteApplicationEvaluation, apiExportApplicationDateset } from "@/api/effectEvaluation";
import { mapActions } from "vuex";

export default {
    name: "effectEvaluationTable",
    data() {
        return {
            chatGptIdList: {
                智谱清言: require("@/assets/images/zpqy.png"),
                通义千问: require("@/assets/images/tongyi.png"),
                文心一言: require("@/assets/images/wxyy.png"),
                DeepSeek: require("@/assets/images/deepseek.png"),
                Kimi: require("@/assets/images/kimi.png"),
                雅意: require("@/assets/images/yayi.png"),
                豆包: require("@/assets/images/doubao.png"),
                中国移动: require("@/assets/images/deepseek.png"),
                百川: require("@/assets/images/baichuan.png"),
                星火: require("@/assets/images/xinghuo.png"),
                openAI: require("@/assets/images/openai.png"),
                MINIMAX: require("@/assets/images/MiniMax.png")
            },
            dateArr: [],
            options: [{
                value: null,
                label: "全部"
            }, {
                value: 1,
                label: "已完成"
            }, {
                value: 2,
                label: "运行中"
            }, {
                value: 3,
                label: "任务失败"
            }],
            searchData: {
                status: ""
            },
            tableData: [],
            tableLoading: false,
            pageSize: 20,
            pageNo: 1,
            total: 1,
            totalPage: 1,
            context: `#角色\n你是一个问题回复打分专家，擅长针对用户传入的问题和答案判断结果是否正确\n#输入\n{{ input }}：代表问题\n{{ output }}：代表回答\n{{ ffreference_output }}：专家答案\n#工作流程\n1.理解问题语义:仔细阅读并理解{{ input }}，确保对问题的意图、上下文及核心需求有准确把握。\n2.分析标准答案:将{{ reference_output }} 作为判断依据，确认其内容是否清晰表达了问题的答案或解决方案。\n3.将{{ output }}与{{ reference_outputl }} 进行对比，关注以下几点：\n- 是否准确回答了问题核心意图。`
        }
    },
    mounted() {
        this.getApplicationEvaluationList()
        this.fetchModleOptions()
    },
    methods: {
        ...mapActions(['fetchModleOptions']),
        // 原组件方法
        getImg(val){
            if(val){
                let manufacturer = this.$store.state?.workflow?.modleOptions.find(item=>item.modelId==val)?.manufacturer
                return this.chatGptIdList[manufacturer]
            }
        },
        handleCurrentChange(n) {
            this.pageNo = n;
            this.getApplicationEvaluationList();
        },
        handleSizeChange(n) {
            this.pageSize = n;
            this.getApplicationEvaluationList();
        },
        exportData(val){
            apiExportApplicationDateset({datasetId: val.datasetId}).then((res)=>{
                const url = window.URL.createObjectURL(new Blob([res]));
                const link = document.createElement("a");
                link.href = url;
                link.setAttribute("download", val.evaluationDataset);
                document.body.appendChild(link);
                link.click();
            })
        },
        deleteData(val) {
            apiDeleteApplicationEvaluation({ id: val.id, datasetId: val.datasetId }).then(res => {
                if (res.code == '000000') {
                    this.$message.success('删除成功')
                    this.getApplicationEvaluationList()
                }
            })
        },
        createEvaluation() {
            this.$emit('createEvaluation')
        },
        getStatusText(data) {
            if (data == 1) {
                return '已完成'
            } else if (data == 2) {
                return '运行中'
            } else if (data == 3) {
                return '任务失败'
            } else {
                return ''
            }
        },
        getApplicationEvaluationList() {
            this.tableLoading = true;
            let params = {
                pageNo: this.pageNo,
                pageSize: this.pageSize,
                evaluationStatus: this.searchData.status,
                startTime: this.dateArr.length > 0 ? this.dateArr[0] : null,
                endTime: this.dateArr.length > 0 ? this.dateArr[1] : null
            }
            apiGetApplicationEvaluationList(params).then(res => {
                console.log(res)
                if (res.code === '000000') {
                    this.tableData = res.data.records
                    this.total = res.data.totalRow
                    this.totalPage = res.data.totalPage
                }
                this.tableLoading = false;
            }).catch(() => {
                this.tableLoading = false;
            })
        }
    },

}
</script>

<style lang="scss" scoped>
.effectEvaluationTable-page {
    width: 100%;
    height: 100%;
    padding: 32px;
    background: #fff;
    display: flex;
    flex-direction: column;

    .page-title {
        margin-bottom: 24px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 24px;
        color: #494E57;
        line-height: 40px;
        text-align: left;
        font-style: normal;
    }

    .seach-box {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;
    }

    ::v-deep .my-table.el-table {
        .el-table__header {
            .el-table__cell {
                padding: 3px 0;
                border-right: 1px solid #fff;
                background: #F2F4F7;

                .cell {
                    padding: 3px 12px;
                    background: #F2F4F7;
                    font-family: MiSans, MiSans;
                    font-weight: 500;
                    font-size: 14px;
                    color: #828894;
                    line-height: 20px;
                }
            }
        }

        .el-button--text:not(.is-disabled) {
            // color: #1747E5;
        }

        // 斑马格
        .el-table__body {
            tr.el-table__row--striped td.el-table__cell {
                background: #F7F8FA;
            }
        }
    }

    .status-box {
        display: flex;
        align-items: center;

        .status-icon {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: #009A29;
            margin-right: 6px;
        }

        .warning {
            background: #FF7C00;
        }

        .error {
            background: #F53F3F;
        }
    }

}
</style>
<style lang="scss">
.effectEvaluationTable-popover-title {
    display: flex;
    align-items: center;
    padding: 10px 16px;
    font-size: 14px;
    font-weight: 500;
}

.effectEvaluationTable-popover-content {
    white-space: pre-line;
    width: 260px;
    max-height: 280px;
    overflow: auto;
    font-size: 12px;
    line-height: 18px;
    padding: 16px;
    padding-top: 0;
}

.table-box {
    margin-top: 24px;
    flex-grow: 1;
    overflow: hidden;

    ::v-deep .el-table .is-cell-link {
        cursor: pointer;
    }

    .btns {
        display: flex;
    }

    .el-button {
        padding: 0 5px;
    }


    
}
</style>