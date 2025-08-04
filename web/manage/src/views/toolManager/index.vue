<!-- 工具模块 -->
<template>
<div class='toolIndex' :class="[!displayHead ? 'hideHead' : '']">
    <div v-if="displayHead" style="line-height: 46px;">
        <ul class="tabUl">
            <li v-for="(item,index) in tabList" :key="index" @click="activeIndex = index">
                <span :class="[activeIndex==index ? 'activecolor' : 'defaultColor','tabName']">{{item.label}}</span>
            </li>
        </ul>
    </div>
    <div v-if="activeIndex == 0">
      <PluginManage @displayHeadHandler="displayHeadHandler" />
    </div>
    <div v-if="activeIndex == 1" style="flex:1;">
      <PromptWordLibrary />
    </div>
    <div v-if="activeIndex == 2">
      <DictionaryManage />
    </div>
    <div v-if="activeIndex == 3">
      <SynonymManage />
    </div>
    <div v-if="activeIndex == 4">
      <CorrectionWords />
    </div>
</div>
</template>

<script>
//这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
//例如：import 《组件名称》 from '《组件路径》';
import PromptWordLibrary from "./promptWordLibrary/index.vue"
import Voice from "./voice/index.vue"
import DictionaryManage from "./dictionaryManage/index.vue"
import PluginManage from "./pluginManage/index.vue"
import SynonymManage from "./synonymManage/index.vue"
import CorrectionWords from "./correctionWords/index.vue"
export default {
//import引入的组件需要注入到对象中才能使用
components: {
    PromptWordLibrary,
    Voice,
    DictionaryManage,
    PluginManage,
    SynonymManage,
    CorrectionWords,
},
data() {
//这里存放数据
return {
    tabList: [
        {
            label: '插件',
            value: '插件'
        },
        {
            label: '提示词',
            value: '提示词'
        },
        {
            label: '敏感词',
            value: '敏感词'
        },
        // {
        //     label: '同义词',
        //     value: '同义词'
        // },
        // {
        //     label: '纠错词',
        //     value: '纠错词'
        // }
        
    ],
    activeIndex: 0,
    displayHead: true, // 是否显示头部
};
},
//监听属性 类似于data概念
computed: {},
//监控data中的数据变化
watch: {},
//方法集合
methods: {
    displayHeadHandler(hide) {
        this.displayHead = hide;
    }
},
//生命周期 - 创建完成（可以访问当前this实例）
created() {

},
//生命周期 - 挂载完成（可以访问DOM元素）
mounted() {

},
beforeCreate() {}, //生命周期 - 创建之前
beforeMount() {}, //生命周期 - 挂载之前
beforeUpdate() {}, //生命周期 - 更新之前
updated() {}, //生命周期 - 更新之后
beforeDestroy() {}, //生命周期 - 销毁之前
destroyed() {}, //生命周期 - 销毁完成
activated() {}, //如果页面有keep-alive缓存功能，这个函数会触发
}
</script>

<style scoped lang="scss">
.toolIndex {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 32px;
    overflow: hidden;
    .tabUl {
        display: flex;
        justify-content: flex-start;
        li {
            margin-right: 16px;
            .tabName {
                font-size: 24px;
                line-height: 28px;
                text-align: right;
                cursor: pointer;
            }
            .defaultColor{
                color: #828894;
                font-weight: 400;
            }
            .activecolor {
                color: #383D47;
                font-weight: 600;
            }
        }
    }
}
.hideHead {
    padding: 0;
}
</style>