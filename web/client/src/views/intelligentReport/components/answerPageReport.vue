<!-- AI报告结果页 -->
<template>
    <div class="page-header">
        <div class="header-left">
            <div class="icon" @click="backInput">
                <img src="../../../assets/img/icon_return.png" alt="">
            </div>
            <div>XXX文档研究报告</div>
        </div>
        <div class="header-right">
            下载文稿
        </div>
    </div>
    <div class="report-page">

        <div class="page-left">
            <div class="container">
                <!-- 左侧目录 -->
                <nav class="sidebar">
                    <div v-for="(item, index) in toc" :key="index" class="toc-item" @click="scrollToSection(item.id)"
                        :class="{ active: activeSection === item.id }">
                        {{ item.text }}
                    </div>
                </nav>

                <!-- 右侧内容 -->
                <main class="content">
                    <article>
                        <!-- 富文本内容 -->
                        <section v-for="(section, index) in sections" :key="index" :id="section.id" class="section">
                            <h2>{{ section.title }}</h2>
                            <div v-html="section.content" class="rich-text"></div>
                        </section>
                    </article>
                </main>
            </div>
        </div>
        <div class="page-right">222</div>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import mittBus from '/@/utils/mitt';

// 富文本内容定义
const sections = ref([
    {
        id: 'section1',
        title: '第一章 引言',
        content: '<p>这是第一章的引言内容...这是第一章的引言内容...这是第一章的引言内容...这是第一章的引言内容...这是第一章的引言内容...这是第一章的引言内容...这是第一章的引言内容...这是第一章的引言内容...这是第一章的引言内容...这是第一章的引言内容...</p><p>可以包含多种HTML标签</p>'
    },
    {
        id: 'section2',
        title: '第二章 技术概述',
        content: '<ul><li>技术点1</li><li>技术点2</li></ul><p>详细的技术描述...</p>'
    },
    {
        id: 'section3',
        title: '第三章 实现步骤',
        content: '<ol><li>步骤1</li><li>步骤2</li><li>步骤3</li></ol>'
    },
    {
        id: 'section4',
        title: '第三章 实现步骤',
        content: '<ol><li>步骤1</li><li>步骤2</li><li>步骤3</li></ol>'
    },
    {
        id: 'section5',
        title: '第三章 实现步骤',
        content: '<ol><li>步骤1</li><li>步骤2</li><li>步骤3</li></ol>'
    },
    {
        id: 'section6',
        title: '第三章 实现步骤',
        content: '<ol><li>步骤1</li><li>步骤2</li><li>步骤3</li></ol>'
    },
    {
        id: 'section7',
        title: '第三章 实现步骤',
        content: '<ol><li>步骤1</li><li>步骤2</li><li>步骤3</li></ol>'
    },
    {
        id: 'section8',
        title: '第三章 实现步骤',
        content: '<ol><li>步骤1</li><li>步骤2</li><li>步骤3</li></ol>'
    }
]);

//返回提问页面
const backInput = () => {
    console.log(11)
    mittBus.emit('showInput', true);
}
// 自动生成目录
const toc = computed(() => {
    return sections.value.map(section => ({
        id: section.id,
        text: section.title
    }));
});

// 当前激活的目录项
const activeSection = ref('');

// 滚动到指定区域
const scrollToSection = (id: string) => {
    const element = document.getElementById(id);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
        activeSection.value = id;
    }
};
</script>

<style lang="scss" scoped>
.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-sizing: border-box;
    padding: 16px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.12);

    .header-left {
        display: flex;
        align-items: center;
        font-size: 18px;
        color: #494E57;

        .icon {
            cursor: pointer;
            width: 16px;
            height: 17px;
            margin-right: 12px;
        }
    }

    .header-right {
        cursor: pointer;
        padding: 6px 18px;
        background-color: #1747E5;
        font-size: 14px;
        color: #FFFFFF;
        border-radius: 2px;
    }
}

.report-page {
    display: flex;

    .page-left {
        width: 70%;
        min-height: 760px;
        margin-right: 20px;

        .container {
            display: flex;
            height: 100%;
        }

        .sidebar {
            width: 240px;
            padding: 20px;
            border-right: 1px solid #ddd;
        }

        .toc-item {
            padding: 8px 12px;
            margin: 4px 0;
            cursor: pointer;
            transition: all 0.2s;
        }

        .toc-item:hover,
        .toc-item.active {
            background: #e0e0e0;
            border-radius: 4px;
        }

        // .content {
        //     max-height: 800px;
        //     flex: 1;
        //     padding: 20px;
        //     overflow-y: auto;
        // }
        .content {
            max-height: 800px;
            flex: 1;
            padding: 20px;
            overflow-y: auto;
        }


        .rich-text {
            line-height: 1.8;
            color: #333;
        }

        .rich-text h3 {
            margin: 20px 0 10px;
            color: #2c3e50;
        }

        .rich-text p {
            margin-bottom: 15px;
        }

        .rich-text ul,
        .rich-text ol {
            margin-left: 30px;
            padding-left: 15px;
        }

        .rich-text li {
            margin-bottom: 8px;
        }
    }

    .page-right {
        width: 30%;
        min-height: 760px;
        border: 1px solid rgba(0, 0, 0, 0.12);
        border-top: none;
    }
}
</style>
