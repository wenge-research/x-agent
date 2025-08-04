<template>
    <div class="explore-page">
        <div v-for="(task, taskIndex) in taskSplit" :key="taskIndex" class="task-card">
            <div class="task-header" @click="toggleTask(taskIndex)">
                <div class="left">
                    <div class="icon">
                        <el-icon color="#444444" size="14">
                            <search />
                        </el-icon>
                    </div>
                    <span class="task-title">网页探索：{{ task.content }}</span>
                    <div class="result-box">
                        <span class="result-count">共{{ task.urlList.length }}个结果</span>
                    </div>
                </div>
                <el-icon :class="{ 'rotate-icon': activeTask === taskIndex }"><arrow-down /></el-icon>
            </div>
            <div v-if="activeTask === taskIndex" class="result-list">
                <div v-for="(result, resultIndex) in task.urlList" :key="resultIndex" class="result-item">
                    <div class="item-txt" @click="goToUrl(result)">
                        <span>{{ resultIndex + 1 }}</span>
                        <div class="icon">
                            <el-icon>
                                <Link />
                            </el-icon>
                        </div>
                        <span>{{ result.title }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ElIcon } from 'element-plus';
import { Search, ArrowDown, Link } from '@element-plus/icons-vue';
import { ref, defineProps, watch } from 'vue';

const props = defineProps({
    taskSplit: {
        type: Array,
        required: true,
        default: () => []
    },
});

interface Result {
    title: string;
}

interface Task {
    title: string;
    results: Result[];
}

const activeTask = ref<number | null>(null);
//数据列表
const tasks = ref<Task[]>([
    {
        title: '网页探索任务：营养保健品市场概况搜索',
        results: Array.from({ length: 7 }, (_, i) => ({ title: 'AIGC设计工作应用指南指南' }))
    },
    {
        title: '网页探索任务：营养保健品市场概况搜索',
        results: Array.from({ length: 7 }, (_, i) => ({ title: 'AIGC设计工作应用指南指南' }))
    },
]);

const toggleTask = (index: number) => {
    activeTask.value = activeTask.value === index ? null : index;
};

//跳转对应网页
const goToUrl = (item: any) => {
    window.open(item.url);
};
</script>

<style scoped lang="scss">
.explore-page {
    .task-card {
        margin-bottom: 20px;
        border-radius: 8px;
        background-color: #ffffff;
        padding: 10px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .task-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        cursor: pointer;
        position: relative;

        .left {
            display: flex;
            align-items: center;
        }

        .icon {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 32px;
            height: 32px;
            background: #F2F3F5;
            border-radius: 4px;
            margin-right: 16px;
        }
    }

    .task-title {
        flex-grow: 1;
        font-size: 14px;
        color: #1D2129;
        margin-right: 8px;
    }

    .result-box {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 71px;
        height: 24px;
        background: #EBEEF2;
        border-radius: 4px;
        font-weight: 400;
        font-size: 12px;
        color: #86909C;

        .result-count {
            color: #909399;
        }

    }

    .rotate-icon {
        transform: rotate(180deg);
    }

    .result-list {
        margin-top: 10px;
    }

    .result-item {
        padding: 5px 0;
        padding-left: 52px;
        font-weight: 400;
        font-size: 14px;
        color: #3F4247;

        .item-txt {
            display: flex;
            align-items: center;
            cursor: pointer;

            .icon {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 20px;
                height: 20px;
                background: #EAEEF5;
                border-radius: 50%;
                margin: 0 8px;
            }
        }
    }

    .el-icon {
        transition: transform 0.3s ease;
    }
}
</style>