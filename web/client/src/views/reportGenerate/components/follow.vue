<template>
    <div class="follow-container" v-if="taskSplit.length">
        <!-- 当没有选中项时显示所有卡片 -->
        <div v-if="!selectedFollowItem" class="container-item" v-for="(item, index) in taskSplit" :key="index" v-show="item.urlList?.length">
            <div class="item" v-show="item.urlList?.length">
                <!-- 自动轮播容器 -->
                <div class="url-carousel-container">
                    <div class="url-carousel-track" :style="getCarouselStyle(index)">
                        <div v-for="(urlItem, urlIndex) in item.urlList" :key="urlIndex" class="url-carousel-item"
                            :class="{ active: activeIndices[index] === urlIndex }"
                            @click="selectUrl(index, urlIndex, urlItem)">
                            <div class="item-header">
                                <div class="herder-icon">
                                    <el-icon size="16">
                                        <Compass />
                                    </el-icon>
                                </div>
                                <div class="header-container">
                                    <div class="name">{{ urlItem.title || '' }}</div>
                                    <div class="url">浏览网页：{{ urlItem.url || '' }}</div>
                                </div>
                            </div>
                            <div class="img-box">
                                <!-- <div>{{ urlItem.imageUrl }}</div> -->
                                <img style="" :src="urlItem.imageUrl" alt="">
                            </div>
                        </div>
                    </div>
                    <!-- 轮播指示器 -->
                    <div class="carousel-indicators" v-if="item.urlList?.length > 1">
                        <span v-for="(_, indicatorIndex) in item.urlList" :key="indicatorIndex"
                            :class="{ active: activeIndices[index] === indicatorIndex }"
                            @click="setActiveIndex(index, indicatorIndex)">
                        </span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 当有选中项时全屏展示该条数据 -->
        <div v-else class="fullscreen-item">
            <div class="back-button" @click="selectedFollowItem = null">返回</div>
            <div class="item">
                <div class="item-header">
                    <div class="herder-icon">
                        <el-icon size="16">
                            <Compass />
                        </el-icon>
                    </div>
                    <div class="header-container">
                        <div class="name">{{ selectedFollowItem.title }}</div>
                        <div class="url">{{ selectedFollowItem.url }}</div>
                    </div>
                </div>
                <div class="img-box">
                    <iframe style="width: 100%;height: 100%;" :src="selectedFollowItem.url" frameborder="0"></iframe>
                </div>
            </div>
        </div>
    </div>

    <div class="not-data" v-else>
        <div class="not-data-container">
            <img src="../../../assets/img/not_data.png" alt="">
            <p>当前暂无跟随任务</p>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { Compass } from '@element-plus/icons-vue';
import { ref, defineProps, watch, onMounted, onUnmounted } from 'vue';

const props = defineProps({
    taskSplit: {
        type: Array,
        required: true,
        default: () => []
    },
});

const activeIndices = ref<Record<number, number>>({});
const carouselTimers = ref<Record<number, number>>({});
const selectedFollowItem = ref<any>(null);

// 初始化轮播
const initCarousels = () => {
    if (props.taskSplit.length > 0) {
        props.taskSplit.forEach((item, index) => {
            if (item.urlList?.length && activeIndices.value[index] === undefined) {
                activeIndices.value = {
                    ...activeIndices.value,
                    [index]: 0
                };
                startCarousel(index, item.urlList.length);
            }
        });
    }
};

// 组件挂载时初始化
onMounted(() => {
    initCarousels();
});

// 监听taskSplit变化
watch(() => props.taskSplit, () => {
    initCarousels();
}, { deep: true });

// 获取轮播样式
const getCarouselStyle = (index: number) => {
    const currentIndex = activeIndices.value[index] || 0;
    const itemCount = props.taskSplit[index]?.urlList?.length || 1;
    return {
        transform: `translateY(-${currentIndex * 100}%)`,
        transition: 'transform 0.5s ease-in-out',
        height: `${itemCount * 100}%` // 确保轨道高度足够容纳所有项目
    };
};

// 启动轮播
const startCarousel = (index: number, totalItems: number) => {
    stopCarousel(index);
    carouselTimers.value[index] = setInterval(() => {
        activeIndices.value = {
            ...activeIndices.value,
            [index]: (activeIndices.value[index] + 1) % totalItems
        };
    }, 5000) as unknown as number;
};

// 停止轮播
const stopCarousel = (index: number) => {
    if (carouselTimers.value[index]) {
        clearInterval(carouselTimers.value[index]);
        delete carouselTimers.value[index];
    }
};

// 手动设置活动索引
const setActiveIndex = (index: number, newIndex: number) => {
    stopCarousel(index);
    activeIndices.value = {
        ...activeIndices.value,
        [index]: newIndex
    };
    startCarousel(index, props.taskSplit[index].urlList.length);
};

// 选择URL
const selectUrl = (cardIndex: number | null, urlIndex: number, urlItem: any) => {
    if (cardIndex !== null) {
        selectedFollowItem.value = urlItem;
        setActiveIndex(cardIndex, urlIndex);
    }
};

// 组件卸载时清除所有定时器
onUnmounted(() => {
    Object.values(carouselTimers.value).forEach(timer => {
        clearInterval(timer);
    });
});
</script>

<style scoped lang="scss">
.follow-container {
    display: flex;
    flex-wrap: wrap;
    gap: 24px;
    overflow-y: auto;
    max-height: 710px;
    position: relative;
}

.container-item {
    width: calc(50% - 12px);
    height: 342px;
    background: #FFFFFF;
    border-radius: 12px;
    background-image: linear-gradient(270deg, rgba(86, 0, 255, 1), rgba(0, 117, 255, 1));
    background-clip: padding-box;
    border: 2px solid transparent;
    padding: 2px;
    cursor: pointer;

    .item {
        width: 100%;
        height: 100%;
        background-color: #FFFFFF;
        border-radius: 8px;
        padding: 12px;
        box-sizing: border-box;
        position: relative;

        .url-carousel-container {
            height: 100%;
            width: 100%;
            overflow: hidden;
            position: relative;

            .url-carousel-track {
                width: 100%;
                will-change: transform;

                .url-carousel-item {
                    width: 100%;
                    height: 100%;
                    /* 固定每个轮播项的高度 */
                    background: #FFFFFF;
                    border-radius: 8px;
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

                    &.active {
                        border: 2px solid #1890ff;
                    }

                    .item-header {
                        display: flex;
                        margin-bottom: 12px;
                        padding: 8px;

                        .herder-icon {
                            width: 36px;
                            height: 36px;
                            background: #F7F8FA;
                            border-radius: 8px;
                            margin-right: 8px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                        }

                        .header-container {
                            .name {
                                font-weight: 500;
                                font-size: 12px;
                                color: #1D2129;
                            }

                            .url {
                                width: 200px;
                                font-weight: 400;
                                font-size: 12px;
                                color: #86909C;
                                overflow: hidden;
                                white-space: nowrap;
                                text-overflow: ellipsis;
                            }
                        }
                    }

                    .img-box {
                        width: 100%;
                        height: 200px;
                        padding: 0 8px;
                        box-sizing: border-box;
                        overflow-y: auto;
                        position: relative;

                        img {
                            width: 100%;
                            display: block;
                        }

                        iframe {
                            width: 100%;
                            height: 100%;
                            border: none;
                        }
                    }
                }
            }

            .carousel-indicators {
                position: absolute;
                bottom: 10px;
                left: 0;
                right: 0;
                display: flex;
                justify-content: center;
                gap: 6px;
                z-index: 2;

                span {
                    width: 8px;
                    height: 8px;
                    border-radius: 50%;
                    background-color: rgba(0, 0, 0, 0.2);
                    cursor: pointer;
                    transition: all 0.3s;

                    &.active {
                        background-color: #1890ff;
                        transform: scale(1.2);
                    }
                }
            }
        }
    }
}

.fullscreen-item {
    width: 100%;
    height: 676px;
    background: #FFFFFF;
    border-radius: 12px;
    border: 2px solid;
    background-image: linear-gradient(270deg, rgba(86, 0, 255, 1), rgba(0, 117, 255, 1));
    background-clip: padding-box;
    border: 2px solid transparent;
    padding: 2px;

    .item {
        width: 100%;
        height: 100%;
        background-color: #FFFFFF;
        border-radius: 8px;
        padding: 12px;
        box-sizing: border-box;

        .item-header {
            display: flex;
            margin-bottom: 12px;

            .herder-icon {
                width: 36px;
                height: 36px;
                background: #F7F8FA;
                border-radius: 8px;
                margin-right: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .header-container {
                .name {
                    font-weight: 500;
                    font-size: 12px;
                    color: #1D2129;
                }

                .url {
                    width: 900px;
                    font-weight: 400;
                    font-size: 12px;
                    color: #86909C;
                    overflow: hidden;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                }
            }
        }

        .img-box {
            width: 100%;
            height: 598px;
            border-radius: 4px;
            border: 1px solid #E7E7E7;
            overflow: hidden;


            iframe {
                width: 100%;
                height: 100%;
                border: none;
                display: block;
            }
        }
    }
}

.back-button {
    position: absolute;
    top: 16px;
    right: 24px;
    cursor: pointer;
    color: #409EFF;
    font-size: 14px;
    z-index: 11;
}

.not-data {
    width: 100%;
    height: 710px;
    display: flex;
    align-items: center;
    justify-content: center;

    .not-data-container {
        display: flex;
        flex-direction: column;
        align-items: center;

        img {
            width: 200px;
            height: 200px;
            margin-bottom: 20px;
        }

        p {
            font-size: 14px;
            color: #86909C;
        }
    }
}
</style>