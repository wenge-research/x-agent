<template>
    <div class="header-tab">
        <div class="tabs-item" :class="{ 'active-tab': activeTab === item.value }" v-for="(item, index) in tabs"
            :key="index" @click="setActiveTab(item)">
            <span>{{ item.name }}</span>
        </div>
    </div>

    <div class="history-list" v-loading="dataLoading" element-loading-background="rgba(0, 0, 0, 0)">
        <!-- 近一周数据 -->
        <template v-if="filteredWeekData.length">
            <div class="times">近一周</div>
            <div class="list-box">
                <div class="list-item" v-for="(item, index) in filteredWeekData" :key="index">
                    <video v-if="item.mediaType == 1" :src="item.url" controls
                        style="width: 100%; height: 100%; border-radius: 8px;"></video>
                    <img v-else style="width: 100%; height: 100%; border-radius: 8px;" :src="item.url">
                </div>
            </div>
        </template>

        <!-- 近一月数据 -->
        <template v-if="filteredMonthData.length">
            <div class="times">近一月</div>
            <div class="list-box">
                <div class="list-item" v-for="(item, index) in filteredMonthData" :key="index">
                    <video v-if="item.mediaType == 1" :src="item.url" controls
                        style="width: 100%; height: 100%; border-radius: 8px;"></video>
                    <img v-else style="width: 100%; height: 100%; border-radius: 8px;" :src="item.url">
                </div>
            </div>
        </template>

        <!-- 空状态 -->
        <div v-if="!dataLoading && !filteredWeekData.length && !filteredMonthData.length" class="empty-state">
            暂无数据
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { getImgVideoList } from '/@/api/chat/index';

// 所选tab 
const activeTab = ref('all');

// 头部tabs数据
const tabs = ref([
    { name: '全部', value: 'all' },
    { name: '图像', value: 'images' },
    { name: '短片', value: 'videos' }, // 修正拼写：videoes → videos
]);

// 数据状态
const dataLoading = ref(false);
const picVideoList = ref({
    recentWeek: { records: [] },
    recentMonth: { records: [] }
});

// 计算属性：根据activeTab筛选数据
const filteredWeekData = computed(() => {
    return filterData(picVideoList.value.recentWeek?.records || []);
});

const filteredMonthData = computed(() => {
    return filterData(picVideoList.value.recentMonth?.records || []);
});

// 数据筛选方法
const filterData = (data) => {
    if (activeTab.value === 'all') return data;
    if (activeTab.value === 'images') return data.filter(item => item.mediaType === 2);
    if (activeTab.value === 'videos') return data.filter(item => item.mediaType === 1);
    return [];
};

const setActiveTab = (item) => {
    activeTab.value = item.value;
};

const getDataList = async () => {
    try {
        dataLoading.value = true;
        const res = await getImgVideoList({
            pageNo: 1,
            pageSize: 20,
            displayScope: 1
        });
        if (res.code === '000000') {
            picVideoList.value = {
                recentWeek: res.data.recentWeek || { records: [] },
                recentMonth: res.data.recentMonth || { records: [] }
            };
        }
    } catch (error) {
        console.error('获取数据失败:', error);
    } finally {
        dataLoading.value = false;
    }
};

onMounted(() => {
    getDataList();
});
</script>

<style scoped lang="scss">
.header-tab {
    display: flex;
    justify-content: space-between;
    width: 260px;

    .tabs-item {
        opacity: 0.5;
        border-radius: 32px;
        padding: 6px 22px;
        display: flex;
        align-items: center;
        font-weight: 400;
        font-size: 14px;
        color: #FFFFFF;
        cursor: pointer;
    }

    .active-tab {
        background: rgba(255, 255, 255, 0.1);
        opacity: 1;
    }
}

.history-list {
    margin-top: 26px;
    width: 100%;
    height: 80vh;
    overflow-y: auto;

    .times {
        font-size: 18px;
        color: #FFFFFF;
    }

    .list-box {
        width: 100%;
        display: flex;
        justify-content: flex-start;
        flex-wrap: wrap;
        margin-bottom: 24px;

        .list-item {
            width: 188px;
            height: 188px;
            border-radius: 8px;
            margin-right: 14px;
            margin-top: 12px;
        }
    }
}

.empty-state {
    color: #999;
    text-align: center;
    padding: 20px;
}
</style>