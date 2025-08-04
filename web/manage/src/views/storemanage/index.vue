<!-- 商店 -->
<template>
<div class='storemanage'>
    <div v-if="showHead" class="head">
        <ul class="tabUl">
            <li v-for="(item,index) in tabList" :key="index" @click="handleActiveChange(index)">
                <span :class="[activeIndex==index ? 'activecolor' : 'defaultColor','tabName']">{{item.label}}</span>
            </li>
        </ul>
        <div class="head-right">
            <el-input
              :placeholder="activeIndex == 0 ? '搜索应用' :activeIndex == 1 ? '搜索应用' : '搜索MCP'"
              class="input"
              v-model="applicationName"
              clearable
              @keydown.native.enter="handleSearch"
              @clear="handleSearch"
              
            >
              <el-button
                slot="append"
                icon="el-icon-search"
                @click="handleSearch"
              ></el-button>
            </el-input>
        </div>
    </div>
    <div class="storemanage-content" v-if="activeIndex == 0">
        <application ref="applicationRef" :applicationName="applicationName" @refreshList="handleSearch"></application>
    </div>
    <div class="storemanage-content" v-if="activeIndex == 1">
        <pluginList ref="pluginListRef" :applicationName="applicationName" @displayHead="displayHead"></pluginList>
    </div>
	<div class="storemanage-content" v-if="activeIndex == 2">
	    <mcpList ref="pluginListRef" :applicationName="applicationName" @displayHead="displayHead"></mcpList>
	</div>
</div>
</template>

<script>
import application from './components/application.vue';
import pluginList from './components/pluginList.vue';
import mcpList from './components/mcpList.vue';
export default {
//import引入的组件需要注入到对象中才能使用
components: {
    application,
    pluginList,
	mcpList
},
data() {
//这里存放数据
return {
tabList: [
        {
            label: '应用商店',
            value: '应用商店'
        },
        {
            label: '插件商店',
            value: '插件商店'
        },
		// {
		//     label: 'MCP商店',
		//     value: 'MCP商店'
		// },
    ],
    activeIndex: 0,
    applicationName: '',
    showHead: true
};
},
//监听属性 类似于data概念
computed: {},
//监控data中的数据变化
watch: {},
//方法集合
methods: {
    handleActiveChange(index){
        if(this.activeIndex == index){
            return;
        }
        this.applicationName = '';
        this.activeIndex = index;
    },
    handleSearch() {
        if(this.activeIndex == 0){
            this.$refs.applicationRef.handleSearch(this.applicationName);
        }else{
            this.$refs.pluginListRef.handleSearch(this.applicationName);
        }
    },
    displayHead(isShow) {
        this.showHead = isShow;
    }
},
//生命周期 - 创建完成（可以访问当前this实例）
created() {

},
//生命周期 - 挂载完成（可以访问DOM元素）
mounted() {

},

}
</script>

<style scoped lang="scss">
.storemanage {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    .head {
        padding: 32px 32px 24px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        .tabUl {
            display: flex;
            justify-content: flex-start;
            li {
                margin-right: 24px;
                .tabName {
                    font-size: 24px;
                    line-height: 40px;
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
        ::v-deep .input {
            width: 334px;
            margin-right: 16px;
            .el-input__inner {
                border: 8px;
                border-radius: 4px 0 0 4px;
                border: 1px solid #e1e4eb;
                border-right: 0px;
            }
            .el-input-group__append {
                border-radius: 0 4px 4px 0;
                padding: 0 12px;
                background-color: transparent;
                .el-button {
                    border: none;
                }
            }
        }
    }
    .storemanage-content {
        flex: 1;
        overflow: hidden;
    }
}
</style>