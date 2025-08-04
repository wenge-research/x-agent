<template>
    <div class="index-detail-result-img">
        <div class="index-detail-result-img-header" >
            <div class="index-detail-result-img-header-left pt20 pb20" >
                <div  >
					<span v-if="type=='zh'">找到约 {{imgList&&imgList.length}} 张相关照片</span>
					<span v-else>Found about {{imgList&&imgList.length}} relevant pictures</span>
				</div>
                <div>
                    <el-dropdown @command="fileSizeCommand">
                        <span class="el-dropdown-link">
                            {{type=='zh'?fileSizeTextZh:fileSizeText}}
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                        </span>
                        <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item v-for="(item,index) in fileSizeList" :key="index" :command="item">{{item.label}}</el-dropdown-item>
                        </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
                <div>
                    <el-dropdown @command="colorsCommand">
                        <span class="el-dropdown-link">
                           {{type=='zh'?colorsTextZh:colorsText}}
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                        </span>
                        <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item v-for="(item,index) in colorsList" :key="index" :command="item"> {{type=='zh'?item.label1:item.label}}</el-dropdown-item>
                        </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </div>
            <div class="index-detail-result-img-header-right pt20 pb20">
                <div>{{type=='zh'?'排序':'Sequence'}}</div>
                <div>
                    <el-dropdown>
                        <span class="el-dropdown-link">
                             {{type=='zh'?'创建时间':'Create time'}}
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                        </span>
                        <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item>{{type=='zh'?'创建时间':'Create time'}}</el-dropdown-item>
                            <!-- <el-dropdown-item>Action 2</el-dropdown-item>
                            <el-dropdown-item>Action 3</el-dropdown-item>
                            <el-dropdown-item disabled>Action 4</el-dropdown-item>
                            <el-dropdown-item divided>Action 5</el-dropdown-item> -->
                        </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </div>
        </div>
        <div class="clearfix index-detail-result-img-content">
            <div class="index-detail-result-img-content-item" v-for="(item,index) in imgList" :key="index" @click="preImg">
                <div class="index-detail-result-img-content-item-img">
                    <el-image
                        style="width: 100%; height: 100%"
                        :src="item.fileUrl"
                        :zoom-rate="1.2"
                        :max-scale="7"
                        :min-scale="0.2"
                        :preview-src-list="[item.fileUrl]"
                        fit="cover"
                        />
                </div>
                <div class="index-detail-result-img-content-item-info">
                    <div class="index-detail-result-img-content-item-info-title">{{item.title}}</div>
                </div>
            </div>
        </div>
           <div class="not-data" v-if="imgList.length==0">
               <div class="not-data-container">
                   <img src="../../assets/img/not_data.png" alt="">
                   <p>暂无数据</p>
               </div>
           </div>
       
    </div>
</template>
<script lang="ts" setup >
/**
 * 图片结果组件
 * */ 

import { ArrowDown } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue';
import { getSearchSourceFileList } from "/@/api/knowledge";
let imgList = ref<any[]>([
    
])
interface Props {
    fileName: string;
	type: string;
    applicationId: string; // 新增
    question: string; // 新增
}
let open = ref(false)
let imgVisbleURL = ref("");
const props = defineProps<Props>();
//fileSize筛选
let fileSizeText = ref('All sizes');
let fileSizeTextZh = ref('全部尺寸');
let fileSizeValue = ref('');
let fileSizeList = ref([
    {
        label: 'All sizes',
        value: ''
    },
    {
        label: '1MB',
        value: 1
    },
   {
        label: '2MB',
        value: 2
    },
    {
        label: '3MB',
        value: 3
    },
    {
        label: '4MB',
        value: 4
    },
    {
        label: '5MB',
        value: 5
    },
    {
        label: '6MB',
        value: 6
    },
]);
const fileSizeCommand = (item: any) => {
	if(props.type=='zh'){
		fileSizeTextZh.value = item.label;
	}else{
		fileSizeText.value = item.label;
	}
    fileSizeValue.value = item.value;
    getSearchSourceFileListData();
};
//colors筛选
let colorsText = ref('All colors');
let colorsTextZh = ref('全部颜色');
let colorsValue = ref('');
let colorsList = ref([
    {
        label: 'All colors',
		label1: '全部颜色',
        value: ''
    },
    {
        label: 'Red',
		label1: '红色',
        value: 0
    },
    {
        label: 'White',
		label1: '白色',
        value:1
    },
    {
        label: 'Purple',
		label1: '紫色',
        value: '2'
    },
    {
        label: 'Green',
		label1: '绿色',
        value: 3
    },
]);
const colorsCommand = (item: any) => {
	if(props.type=='zh'){
		colorsTextZh.value = item.label1;
	}else{
		colorsText.value = item.label;
	}
    colorsValue.value = item.value;
    getSearchSourceFileListData();
};
//获取图片列表
const getSearchSourceFileListData = async () => {
    const res = await getSearchSourceFileList({
       applicationId: props.applicationId,
        pageNo: 1,
        pageSize: 20,
        question: props.question,
        types: [2],   
        // fileTypes: ["png"],
    });
    if(res.data){
        imgList.value = res.data.records;
    }
};
//预览图片
const preImg = (item:any) => {
    imgVisbleURL.value = item.fileUrl;
    open.value = true;
};
onMounted(() => {
    getSearchSourceFileListData();
});
defineExpose({
    getSearchSourceFileListData
})
</script>
<style lang="scss" scoped >

.clearfix:after {
    content: "";
    display: block;
    height: 0;
    clear: both;
    visibility: hidden; 
}

.not-data{
	text-align: center;
	margin: 40px auto;
	img{
		margin: 10px auto;
	}
}

.index-detail-result-img{
    
    .index-detail-result-img-header{
        display: flex;
        justify-content: space-between;
        .index-detail-result-img-header-left{
            display: flex;
            color: #828894;
            >div{
                margin-right: 25px;
            }
        }
        .index-detail-result-img-header-right{
            display: flex;color: #828894;
            >div{
                margin-left: 25px;
            }
        }
    }
    .index-detail-result-img-content{
        // >div:nth-child(4n){
        //     margin-right: 0;
        // }
        .index-detail-result-img-content-item{
            float: left;
            // width: 18%;
            height: 200px;
            margin-right: 16px;
            text-align: center;
            margin-bottom: 29px;
            cursor: pointer;
            &-img{
                overflow: hidden;
                height: 100%;
                .el-image{
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }
            }
        }
        
    }
}
</style>
