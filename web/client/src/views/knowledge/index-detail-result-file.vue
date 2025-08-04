<template>
    <div class="index-detail-result-file">
        <div class="left-box">
            <div class="flex justify-between left-box-header pt20 pb20">
                <div><span v-if="type == 'zh'">搜索到 {{ totalPage }} 条相关资源</span><span v-else>Search for {{ totalPage }}
                        relevant resources</span></div>
                <div class="flex justify-between" style="margin-right: 24px;">
                    <div style="margin-right:8px">{{ type == 'zh' ? '排序' : 'Sequence' }}</div>
                    <el-dropdown @command="CommandSequence">
                        <span class="el-dropdown-link">
                            {{ type == 'zh' ? relevanceTextZh : relevanceText }}
                            <el-icon class="el-icon--right">
                                <iconpark-icon name="arrow-down-s-line"></iconpark-icon>
                            </el-icon>
                        </span>

                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item :command="item" v-for="(item, index) in relevanceData"
                                    :key="index">{{ type == 'zh' ? item.title1 : item.title }}</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </div>
            <div class="popular-left">
                <div class="popular-list" v-for="(item, index) in resultList" :key="index">
                    <div class="img" @click="handlePreview(item)">
                        <img :src="list" alt="">
                    </div>
                    <div class="info">
                        <div class="title" :title="item.fileName">{{ item.fileName }}</div>
                        <div class="info-text" :title="item.content">
                            {{ item.content }}
                        </div>
                        <div class="info-footer">
                            <span class="item">发布时间:{{ item.createTime }}</span>
                            <!-- <span class="item">来源：{{item.url}}</span> -->
                        </div>
                    </div>
                </div>
            </div>
            <div class="left-box-content">
                <div class="left-box-content-item" v-for="(item, index) in filterData" :key="index">
                    <div class="left-box-content-item-img">
                        <el-image :src="item.documentPic" :preview-src-list="[item.documentPic]" fit="cover" alt=""
                            @click="preImg" />
                    </div>
                    <div class="left-box-content-item-content">
                        <div class="left-box-content-item-content-title" @click="openPreview(item)">
                            <span v-html="item.title"></span>
                        </div>
                        <div class="left-box-content-item-content-desc" @click="openPreview(item)">
                            <div>{{ item.hitContent }}</div>
                        </div>
                        <div class="left-box-content-item-content-hit" @click="openPreview(item)">
                            <el-icon>
                                <Flag />
                            </el-icon>
                            <span>{{ type == 'zh' ? '命中段落' : 'Hit the paragraph' }} ：{{ item.hitParagraphNum }}</span>
                        </div>
                        <div class="left-box-content-item-content-total">
                            <div>{{ item.paragraphsNum }} {{ type == 'zh' ? '页' : 'pages in total' }} </div>
                            <div v-show="!item.isShow"> {{ type == 'zh' ? '记录时间' : 'Creation time' }} {{ item.createTime }}</div>
                            <div v-show="!item.isShow"> {{ type == 'zh' ? '创建时间' : 'Recording time' }} {{ item.recordTime }}</div>
                            <div class="show-more" @click="checkIshow(item)" v-if="type == 'zh'">
                                {{ !item.isShow ? '显示更多' : '关闭' }}</div>
                            <div class="show-more" @click="checkIshow(item)" v-else>{{ !item.isShow ? 'SHOW MORE' : 'Hide' }}
                            </div>
                        </div>
                    </div>
                    <div class="right-hide-box" v-show="item.isShow">
                        <div class="title">
                            <div class="heights">{{ type == 'zh' ? '元数据' : 'Metadata' }}</div>
                            <div class="text">{{ type == 'zh' ? '管辖权' : 'Jurisdiction' }}</div>
                        </div>
                        <div class="format">
                            <div>{{ type == 'zh' ? '格式' : 'Format' }}</div>
                            <div>{{ item.fileType }}</div>
                        </div>
                        <div class="storage-location">
                            <div>{{ type == 'zh' ? '存储位置' : 'Storage location' }}</div>
                            <div>{{ item.fileUrl }}</div>
                        </div>
                        <div class="file-size">
                            <div>{{ type == 'zh' ? '文件大小' : 'File size' }}</div>
                            <div>{{ item.fileSize }}KB</div>
                        </div>
                        <div class="recording-time">
                            <div>{{ type == 'zh' ? '记录时间' : 'record time' }}</div>
                            <div>{{ item.recordTime }}</div>
                        </div>
                        <div class="create-time">
                            <div>{{ type == 'zh' ? '创建时间' : 'create time' }}</div>
                            <div>{{ item.createTime }}</div>
                        </div>
                        <div class="author">
                            <div>{{ type == 'zh' ? '作者' : 'Author' }}</div>
                            <div>{{ item.createUser }}</div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
        <div class="right-box pl24 pt24">
            <div class="flex justify-between right-box-one">
                <div>{{ type == 'zh' ? '筛选' : 'Filter' }}</div>
                <!-- <div><el-checkbox v-model="checked" @change="changeIshow">{{type=='zh'?'显示元数据':'Display metadata'}}</el-checkbox></div> -->
            </div>

            <div class="right-box-there mt10">
                <div>
                    <div class="grayColor">{{ type == 'zh' ? '发布时间' : 'Creation time' }}</div>
                    <div class="w246">
                        <el-date-picker v-model="dateValue1" type="daterange" value-format="YYYY-MM-DD"
                            start-placeholder="开始时间" end-placeholder="结束时间" range-separator="到" size="mini">
                        </el-date-picker>
                    </div>
                </div>
                <!-- <div>
                <div class="grayColor">{{type=='zh'?'收录时间':'Recording time'}}</div>
                <div class="w246">
                    <el-date-picker
                        v-model="dateValue2"
                        type="daterange"
                        value-format="YYYY-MM-DD"
                        start-placeholder="YYYY-MM-DD"
                        end-placeholder="YYYY-MM-DD"   
                        range-separator="to"
                        size="mini">
                    </el-date-picker>
                </div>
            </div> -->
            </div>
            <div class="source-box">
                <div class="source">来源</div>
                <el-select v-model="valueData" placeholder="请选择" size="large" @change="handleSelectChange">
                    <el-option v-for="item in options" :key="item.knowledgeId" :label="item.knowledgeName"
                        :value="item.knowledgeId" />
                </el-select>
            </div>
            <div class=" optionPlateDox">
                <div class="mb5 grayColor" style="margin-top:24px;">{{ type == 'zh' ? '格式' : 'Format' }}</div>
                <div>
                    <span class="cursor-pointer optionPlateDox-item" v-for="(item, index) in formatOption"
                        :class="{ 'optionPlateDoxActive': item == formatActive }" :key="index" @click="formatActive = item">
                        {{ item }}
                    </span>
                </div>
            </div>
            <div class="footer-but">
                <el-button type="primary" @click="addPrimary">搜索</el-button>
                <el-button>重置</el-button>
            </div>
            <!-- <div class=" optionPlateDox mt10">
            <div class="mb5 grayColor">{{type=='zh'?'类型':'Tpye'}}</div>
            <div>
                <span 
                    class="cursor-pointer optionPlateDox-item" 
                    v-for="(item,index) in TpyeOption" 
                    :class="{'optionPlateDoxActive':item.value==TpyeActive}"
                    :key="index"
                    @click="TpyeActive=item.value"
                >
                    {{type=='zh'?item.title1:item.title}}
                </span>
            </div>
        </div> -->
        </div>
        <!-- <el-dialog
      title="预览文件"
      v-model="open"
      width="1200px"
      :modal="true"
      :append-to-body="true"
      :close-on-click-modal="false"
      @close="open = false"
    >
        <iframe :src="imgVisbleURL" frameborder="0" scrolling-="auto" style="width:100%;height:600px;"></iframe>
    </el-dialog> -->
        <comDocPreview ref="previewRef" />
    </div>
</template>
<script lang="ts" setup>
import { getSearchSourceFileList, getFileListByAppId } from "/@/api/knowledge";
import { computed, onMounted, ref, defineAsyncComponent } from 'vue'
import { Flag } from '@element-plus/icons-vue'
import { useRoute } from "vue-router";
const route = useRoute();
const comDocPreview = defineAsyncComponent(() => import('/@/components/comDocPreview.vue'));
import list from "/@/assets/ai/oumen.png";
interface Props {
    fileName: string;
    type: string;
    applicationId: string; // 新增
    question: string; // 新增
}
const props = defineProps<Props>();
let knowledgeId = ref("");
let leftBoxContentList = ref<any[]>([])
let checked = ref(false)
let imgVisbleURL = ref("");
let open = ref(false)
const valueData = ref('');
let totalPage = ref(0)
let formatOption = ref(['全部', 'pdf', 'doc', 'txt', 'ppt', 'xls']);
let formatActive = ref("全部");
let relevanceText = ref('All')
let relevanceTextZh = ref('全部')
let relevanceValue = ref('')
let relevanceData = ref([
    {
        title: 'All',
        title1: '全部',
        value: ""
    },
    {
        title: 'highest relevance',
        title1: '相关度高到低',
        value: 1
    },
    {
        title: 'lowest relevance',
        title1: '相关度低到高',
        value: 0
    },
])
let dateValue1 = ref([])
let dateValue2 = ref([])
const options = ref([])
let TpyeOption = ref([
    {
        title: 'all',
        title1: '全部',
        value: 'all'
    },
    {
        title: 'Uncategorized',
        title1: '未分类',
        value: 'uncategorized'
    },
    {
        title: 'Police',
        title1: '警方',
        value: 'police'
    },
    {
        title: 'City supervisor',
        title1: '市监',
        value: 'city_supervisor'
    },
    {
        title: 'Civil administration',
        title1: '民政管理',
        value: 'civil_administration'
    },
    {
        title: 'Other',
        title1: '其他',
        value: 'other'
    }
]);
let TpyeActive = ref('all');
const filterData = computed(() => {
    const createdStartTimestamp = dateValue1.value && dateValue1.value.length > 0 && dateValue1.value[0] ? new Date(dateValue1.value[0]).getTime() : -Infinity;
    const createdEndTimestamp = dateValue1.value && dateValue1.value.length > 0 && dateValue1.value[1] ? new Date(dateValue1.value[1]).getTime() : Infinity;
    const recordedStartTimestamp = dateValue2.value && dateValue2.value.length > 0 && dateValue2.value[0] ? new Date(dateValue2.value[0]).getTime() : -Infinity;
    const recordedEndTimestamp = dateValue2.value && dateValue2.value.length > 0 && dateValue2.value[1] ? new Date(dateValue2.value[1]).getTime() : Infinity;
    return leftBoxContentList.value.filter(item => {
        const createdTimestamps = new Date(item.createTime).getTime();
        const recordedTimestamps = new Date(item.recordTime).getTime();
        if (formatActive.value !== 'all' && TpyeActive.value !== 'all') {
            return createdTimestamps >= createdStartTimestamp && createdTimestamps <= createdEndTimestamp && recordedTimestamps >= recordedStartTimestamp && recordedTimestamps <= recordedEndTimestamp && item.fileType === formatActive.value && item.type === TpyeActive.value;
        }
        if (formatActive.value !== 'all' && TpyeActive.value === 'all') {
            return createdTimestamps >= createdStartTimestamp && createdTimestamps <= createdEndTimestamp && recordedTimestamps >= recordedStartTimestamp && recordedTimestamps <= recordedEndTimestamp && item.fileType === formatActive.value;
        }
        if (TpyeActive.value !== 'all' && formatActive.value === 'all') {
            return createdTimestamps >= createdStartTimestamp && createdTimestamps <= createdEndTimestamp && recordedTimestamps >= recordedStartTimestamp && recordedTimestamps <= recordedEndTimestamp && item.type === TpyeActive.value;
        }
        return createdTimestamps >= createdStartTimestamp && createdTimestamps <= createdEndTimestamp && recordedTimestamps >= recordedStartTimestamp && recordedTimestamps <= recordedEndTimestamp;
    });
});
const resultList = ref<any[]>([])
const previewClicklist = ref({})
//获取文件列表数据
const getSearchSourceFileListData = async () => {
    const res = await getSearchSourceFileList({
        applicationId: props.applicationId,
        pageNo: 1,
        pageSize: 20,
        question: props.question,
        types: [0],
        fileTypes: formatOption.value,
        knowledgeId: valueData.value
    });
    if (res.data) {
        resultList.value = res.data.records;
        totalPage.value = res.data.totalPage;
    }

};


const getFileList = async () => {
    console.log('111122')
    const res = await getFileListByAppId({
        applicationId: props.applicationId
    });
    console.log(res.data, 'aaaaaaaaaaaa')
    if (res.data) {
        options.value = res.data;
    }

}
const addPrimary = () => {
    getSearchSourceFileListData();
};
const handleSelectChange = (value: string) => {
    console.log('选中的值:', value);
    // 可在此添加其他逻辑，如根据选中值筛选数据等
};
//预览图片
const preImg = (item: any) => {
    console.log('item', item.documentPic)
    imgVisbleURL.value = item.documentPic;
    open.value = true;
};
const previewRef = ref(null);
const handlePreview = async (item:any) => {
    await getFileListByAppId({
    applicationId: localStorage.getItem(`${route.params.appId}appId`),
    pageNo: 1,
    pageSize: 20,
    question:  props.question,
    types: [0],   
    fileTypes: ["pdf"],
  });
  previewClicklist.value = item;
  previewRef.value.openPreview(item, '预览文件', 'pdf');
}


// const openPreview = (item:any) => {
// 	console.log('item',item.transPdfUrl)
// 	const fileType = 'pdf';
// 	previewRef.value.openPreview(item.transPdfUrl, '预览文件', fileType);
// };
const openPreview = (item: Object) => {
    previewRef.value.openPreview(item);
};
//显示隐藏元数据
const changeIshow = () => {
    leftBoxContentList.value.forEach((item: any) => {
        item.isShow = checked.value;
    });
};
const checkIshow = (item: any) => {
    item.isShow = !item.isShow;
    checked.value = leftBoxContentList.value.every((item: any) => item.isShow);
};

//预览pdf
const prePdf = (item: any) => {
    if (!item.transPdfUrl) return
    open.value = true;
    imgVisbleURL.value = item.transPdfUrl;
};

//sequence
const CommandSequence = (command: any) => {
    if (props.type == 'zh') {
        relevanceTextZh.value = command.title1;
    } else {
        relevanceText.value = command.title;
    }

    relevanceValue.value = command.value;
    getSearchSourceFileListData();
};
onMounted(() => {
    getSearchSourceFileListData();
    getFileList();
});
defineExpose({
    getSearchSourceFileListData,
    getFileList
})
</script>
<style lang="scss" scoped>
::v-deep .el-date-editor.el-input__wrapper {
    width: 100% !important;
}

::v-deep .el-date-editor.el-input__wrapper {
    background: transparent;
    box-shadow: none !important;
}

::v-deep .el-checkbox__input.is-checked .el-checkbox__inner,
.el-checkbox__input.is-indeterminate .el-checkbox__inner {
    background-color: #365FAC;
    border-color: #365FAC;
    //   color: #365FAC !important;
}

::v-deep .el-checkbox__input.is-checked+.el-checkbox__label {
    color: #365FAC;
}

::v-deep .el-range-separator {
    color: #828894;
}
.popular-left {
    padding: 16px 0;
    border-bottom: 1px solid #E5E5E5;
    .popular-list {
        display: flex;
        .img {
            width: 108px;
            height: 144px;
            cursor: pointer;
        }
        .info {
            margin-left: 16px;
            position: relative;
			width: 100%;
            .title {
                font-weight: 500;
                font-size: 16px;
                color: #1D2129;
                line-height: 24px;
                text-align: left;
                font-style: normal;
				margin-bottom: 20px;
            }
			.info-text{
				 -webkit-line-clamp: 2; // 设置两行文字溢出
				display: -webkit-box; /** 对象作为伸缩盒子模型显示 **/
				-webkit-box-orient: vertical; /** 设置或检索伸缩盒对象的子元素的排列方式 **/
				overflow: hidden; /** 隐藏超出的内容 **/
				margin-bottom: 20px;

			}
            .info-footer {
                font-size: 12px;
                color: #86909C;
                line-height: 16px;
                text-align: left;
                
            }
        }
    }
}
:deep(.w246 .el-date-editor.el-input__wrapper) {
    border: 1px solid #dcdfe6; // 添加 1px 灰色边框
    cursor: pointer;
    border-radius: 4px; // 设置边框圆角
    height: 36px;
    transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1); // 添加边框颜色过渡效果

    &:hover {
        border-color: #409eff; // 鼠标悬停时边框颜色变为蓝色
    }

    &.is-focus {
        border-color: #409eff; // 输入框聚焦时边框颜色变为蓝色
    }
}

::v-deep .el-checkbox__label {
    color: #828894;
}

.w246 {
    width: 344px !important;
}

.footer-but {
    margin-top: 24px;
}

.source-box {
    margin-top: 24px;

    .source {
        height: 20px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #383D47;
        line-height: 20px;
        text-align: left;
        font-style: normal;
    }
}

.index-detail-result-file {
    display: flex;

    .left-box {
        flex: 1;
        height: 100%;
        border-right: 1px solid rgba(0, 0, 0, 0.08);
        box-sizing: border-box;
    }

    .right-box {
        width: 368px;
        height: 100%;

        .grayColor {
            color: #828894;
            margin-bottom: 8px;
        }
    }
}

.left-box-header {
    color: #828894;
    border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.optionPlateDox {
    .optionPlateDox-item {
        display: inline-block;
        line-height: 24px;
        padding: 0 16px;
        color: #000;
        user-select: none;
    }

    .optionPlateDoxActive {
        color: #fff;
        background-color: #365FAC;
    }
}

.left-box-content {
    .left-box-content-item {
        padding: 16px 0;
        border-bottom: 1px solid #E5E5E5;
        display: flex;
        cursor: pointer;

        .left-box-content-item-img {
            width: 108px;
            height: 144px;

            img {
                width: 100%;
                height: 100%;
            }
        }

        .left-box-content-item-content {
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            flex: 1;
            margin-left: 16px;
            margin-right: 24px;

            .left-box-content-item-content-title {
                font-size: 16px;
                color: #383D47;

                .label-name {
                    color: #622AFF;
                }

            }

            .left-box-content-item-content-desc {
                font-size: 12px;
                color: #646479;
                line-height: 18px;
                margin-top: 8px;
                display: -webkit-box;
                -webkit-box-orient: vertical;
                -webkit-line-clamp: 2;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            .left-box-content-item-content-hit {
                display: flex;
                align-items: center;
                color: #365FAC;
                font-size: 12px;
                margin-bottom: 20px;
            }

            .left-box-content-item-content-total {
                display: flex;
                justify-content: space-between;
                color: #A6A5B8;
                font-size: 12px;

                .show-more {
                    color: #365FAC;
                    cursor: pointer;
                }
            }
        }

        .right-hide-box {
            width: 243px;
            height: 144px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 0 16px;
            box-sizing: border-box;
            border-left: 1px solid rgba(0, 0, 0, 0.08);

            .title {
                display: flex;

                .heights {
                    font-weight: 500;
                    font-size: 14px;
                    color: #365FAC;
                    line-height: 20px;
                    margin-right: 12px;
                }

                .text {
                    font-weight: 500;
                    font-size: 14px;
                    color: #828894;
                    line-height: 20px;
                }
            }

            .format,
            .storage-location,
            .file-size,
            .recording-time,
            .create-time,
            .author {
                display: flex;
                justify-content: space-between;
                font-size: 12px;

                div:first-child {
                    color: #A6A5B8;
                }

                div:last-child {
                    width: 120px;
                    white-space: nowrap;
                    text-align: right;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    color: #434649;
                }
            }
        }
    }
}
</style>
