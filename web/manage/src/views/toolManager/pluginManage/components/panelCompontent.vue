<!-- 插件类型 -->
<template>
<div class='panel'>
    <div v-if="panelType === 'string'">
        <el-input v-model="value" type="textarea"
        :rows="2"></el-input>
    </div>
    <div v-if="panelType === 'number' || panelType === 'integer'">
        <el-input-number v-model="value" controls-position="right" ></el-input-number>
    </div>
    <div v-if="panelType === 'file'">
        <el-upload
            class="upload-demo"
            drag
            action="https://jsonplaceholder.typicode.com/posts/"
            :multiple="false">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">拖拽文件到此处或选择文件
            <span class="tip"> 支持.xlsx、.json、.jsonl、.png、.jpg、.jpeg、.pdf、.wav、.docx、.csv、.text格式</span>
            </div>
        </el-upload>
    </div>
    <div v-if="panelType === 'array[file]'">
        <el-upload
            class="upload-demo"
            drag
            action="https://jsonplaceholder.typicode.com/posts/"
            :multiple="false">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">拖拽文件到此处或选择文件
            <span class="tip"> 支持.xlsx、.json、.jsonl、.png、.jpg、.jpeg、.pdf、.wav、.docx、.csv、.text格式</span>
            </div>
        </el-upload>
    </div>
    <div v-if="panelType === 'boolean'">
        <el-switch v-model="value" active-color="#1747E5" inactive-color="#CED4E0"></el-switch>
    </div>
    <div v-if="panelType === 'object' || panelType === 'array[object]' || panelType === 'array[integer]' || panelType === 'array[boolean]' || panelType === 'array[string]'">
        <el-input
            type="textarea"
            v-model="value"
            @input="validateJson"
            rows="10"
            cols="50"
            placeholder="请输入 JSON 格式文本"></el-input>
    </div>
</div>
</template>

<script>
//这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
//例如：import 《组件名称》 from '《组件路径》';

export default {
//import引入的组件需要注入到对象中才能使用
components: {},
name: 'PanelCompontent',
props:{
    panelType: {
        type: String,
        default: ""
    },
    panelValue: {
        type: [String,Number],
        default: ""

    }
},
data() {
//这里存放数据
return {
    jsonError: null,
    value: "",
};
},
//监听属性 类似于data概念
computed: {},
//监控data中的数据变化
watch: {
   panelValue: {
       handler(n) {
           this.value = n;
       }
   } 
},
//方法集合
methods: {
    validateJson() {
        try {
            this.jsonError = null;
            return JSON.stringify(JSON.parse(this.value), null, 2);
        } catch (error) {
            this.jsonError = `JSON 格式错误: ${error.message}`;
            this.formattedJson = null;
        }
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
.panel{
    /* width: 100px;
    height: 100px; */
    div {
        margin: 10px 0;
        .el-input, .el-input-number, .el-upload-dragger {
            width: 100% !important;
        }
    }
    ::v-deep .el-upload {
        width: 100% !important;

    }
     ::v-deep  .el-upload-dragger {
        width: 100% !important;
        .el-icon-upload {
            margin-top: 20px;
        }
        .el-upload__text {
            .tip {
                display: block;
                color: #BCC1CC;
                font-size: 12px;
                line-height: 16px;
            }
        }
    }
}
</style>