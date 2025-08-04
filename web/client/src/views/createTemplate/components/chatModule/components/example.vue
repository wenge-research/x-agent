<template>
    <div class="example-tab">
        <div class="tabs-item" 
             :class="{ 'active-tab': activeTab === item.value }" 
             v-for="(item, index) in tabs"
             :key="index" 
             @click="setActiveTab(item)">
            <span>{{ item.name }}</span>
        </div>
    </div>

    <div class="history-list" v-loading="listLoading" element-loading-background="rgba(0, 0, 0, 0)">
        <div class="list-box">
            <div class="list-item" v-for="(item, index) in filteredList" :key="index">
                <video v-if="item.mediaType == 1" 
                      :src="item.url" 
                      controls 
                      style="width: 100%; height: 100%; border-radius: 8px;"></video>
                <img v-else 
                    style="width: 100%; height: 100%; border-radius: 8px;" 
                    :src="item.url">
            </div>
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
    { name: '短片', value: 'videos' }, // 修正拼写错误：videoes → videos
]);

// 列表数据
const listLoading = ref(false);
const imgVideoList = ref([]);

// 计算属性：根据 activeTab 筛选数据
const filteredList = computed(() => {
    if (activeTab.value === 'all') {
        return imgVideoList.value;
    } else if (activeTab.value === 'images') {
        return imgVideoList.value.filter(item => item.mediaType === 2);
    } else if (activeTab.value === 'videos') {
        return imgVideoList.value.filter(item => item.mediaType === 1);
    }
    return [];
});

const setActiveTab = (item) => {
    activeTab.value = item.value;
};

const getImgVideoData = async () => {
    try {
        listLoading.value = true;
        const res = await getImgVideoList({
            pageNo: 1,
            pageSize: 10,
            displayScope: 0,
        });
        if (res.code === '000000') {
            imgVideoList.value = res.data.all.records;
        }
    } catch (error) {
        console.error(error);
    } finally {
        listLoading.value = false;
    }
};

onMounted(() => {
    getImgVideoData();
});
</script>

<style scoped lang="scss">
.example-tab {
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
    height: 100%;
    overflow-y: auto;

    .list-box {
        width: 100%;
        display: flex;
        justify-content: flex-start;
        flex-wrap: wrap;
        margin-bottom: 24px;

        .list-item {
            width: 13%;
            height: 248px;
            border-radius: 8px;
            margin-right: 16px;
            margin-top: 12px;
        }
    }
}
</style>