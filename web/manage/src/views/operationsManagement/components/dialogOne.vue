<template>
    <el-drawer
        title=""
        :visible.sync="dialogVisibleApplication"
        :modal="true"
        :modal-append-to-body="true"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        size="960px"
        :append-to-body="true"
        :wrapper-closable="false"
        :show-close="false"
        :withHeader="false"
        :before-close="closeDrawer"
        style="height: 100%;"
      >
         <div class="vocabularyDrawer-Box" >
            <div class="magnifiedEditBox-header" >
            <div>
              <span>选择项目</span>
            </div>
            <iconpark-icon name="close-line" size="18" color="#828894" @click.stop="closeDrawer" 
            style="margin-bottom: 10px;cursor: pointer"></iconpark-icon>
          </div>

                   <!-- 抽屉内容 -->
                   <div class="vocabularyDrawer-Con-Box" >
                  <!-- 头部 -->
            <div class="vocabularyDrawer-Con-Box-header" >
              <el-row>
                <el-col :span="6">
                  <div class="vocabularyDrawer-header-tabs" >
                    <!-- <div :class="{'vocabularyDrawer-header-tabs-active':vocabularyDrawerHeaderTabsActive === '全部'}" @click="vocabularyDrawerHeaderTabsActive = '全部'">全部</div>
                    <div :class="{'vocabularyDrawer-header-tabs-active':vocabularyDrawerHeaderTabsActive === '我的'}" @click="vocabularyDrawerHeaderTabsActive = '我的'">我的</div> -->
                    <div 
                      v-for="(item,index) in ['全部','我的']"
                      :class="{'vocabularyDrawer-header-tabs-active':vocabularyDrawerHeaderTabsActive === item}" 
                      @click="vocabularyDrawerHeaderTabsFn(item)"
                      :key="index"
                    >{{item}}</div>
                  </div>
                </el-col>
                <!-- <el-col :span="4" :offset="7">
                    <el-select v-model="applicationValue" placeholder="请选择">
                        <el-option
                            v-for="item in applicationType"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-col> -->
                <el-col :span="6" :offset="12" >
                  <el-input v-model="searchValue" @input="getApplicationList" placeholder="请输入" @keyup.enter.native="getApplicationList" >
                    <i slot="prefix" @click="getApplicationList" class="el-input__icon el-icon-search"></i>
                  </el-input>
                </el-col>
              </el-row>
            </div>
            <div style="flex:1;" v-loading="loading">
              <div class="list-content no-scrollbar" >
                <div
                    v-for="(item, index) in appList"
                    :key="index"
                    class="listItem"
                >
                    <div class="listContent">
                    <div class="head-img-box">
                        <img v-if="item.logo" :src="item.logo" class="headImg" />
                        <img v-else :src="getRandomHeadImgDefaultBgColor(index)" class="defaultHeadImg" />
                        
                    </div>
                    <div class="textContent">
                        <div class="title-content">
                        <p class="title" :title="item.applicationName">{{ item.applicationName }}</p>
                        <div
                            class="sign"
                            v-if="item.publishStatus == 1 || item.publishStatus == 2"
                            :class="`${
                            item.publishStatus == 1 || item.publishStatus == 2
                                ? 'successSign'
                                : 'failSign'
                            }`"
                        > 
                            <i v-if="item.publishStatus == 1 || item.publishStatus == 2" class="el-icon-success" style="color: #3FB816"></i>
                            
                        </div>
                        </div>
                        <div class="list-type" v-if="item.type && getApplicationTypeLabel(item.type)">
                        <span class="type-item">{{ getApplicationTypeLabel(item.type) }}</span>
                        </div>
                    </div>
                    </div>
                    <div class="desc-content">
                    <p class="desc">{{ item.introduce }}</p>
                    </div>
                    <div class="date">
                    <div class="list-update-time">
                        <span class="list-user-icon"><iconpark-icon name="user-3-line" size="16"></iconpark-icon></span>
                        <span class="create-user" v-if="item.accountName">{{ item.accountName }}</span>
                        <span class="point" style="margin-right: 8px"></span>
                        <span>{{ item.updateTime }}</span>
                    </div>
                        <div
                            style="display: flex; align-items: flex-end; cursor: pointer"
                            v-permission="'preview'"
                        >
                            <div
                            class="opts-box" @click.stop style="width: auto;"
                            >
                                <el-button class="chakantongji" @click="selectContent(item)" type="text" style="padding: 0;">查看统计</el-button>
                            </div>
                        </div>
                    </div>
                </div>
              </div>
              <el-pagination
              style="margin-top: 20px; color: #272a31;text-align: right;"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="pageObj.pageNo"
              :page-sizes="[10, 30, 50, 100, 200]"
              :page-size="pageObj.pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pageObj.total"
              :pager-count="5"
              background
              >
              </el-pagination>
            </div>
          </div>
        </div>

    </el-drawer>

</template>
<script>
import { applicationTypes } from '@/utils/constants';
import {getApplicationInfoList} from "@/api/index.js";

export default {
    name: 'dialogOne',
    props: {
        dialogVisibleApplication: {
            type: Boolean,
            default: false,
        },
        
    },
    mounted(){
        this.getApplicationList()
    },
    data() {
        return {
            loading: false, 
            appList: [],
            pageObj: {
                pageNo: 1,
                pageSize: 10,
                total: 100
            },
            listData: [
                {
                    name: '智慧城市管理',
                    id: 'APP001',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '交通监控系统',
                    id: 'APP002',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '环境监测平台',
                    id: 'APP003',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '市政设施管理',
                    id: 'APP004',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '应急指挥中心',
                    id: 'APP005',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '数据分析平台',
                    id: 'APP006',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '社区服务系统',
                    id: 'APP007',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '智慧停车管理',
                    id: 'APP008',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '公共安全监控',
                    id: 'APP009',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '智慧教育平台',
                    id: 'APP010',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '医疗资源管理',
                    id: 'APP011',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '垃圾分类系统',
                    id: 'APP012',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '能源管理平台',
                    id: 'APP013',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '公共交通调度',
                    id: 'APP014',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '市民服务中心',
                    id: 'APP015',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '城市规划系统',
                    id: 'APP016',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '天气预警系统',
                    id: 'APP017',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '智慧园区管理',
                    id: 'APP018',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '文化旅游平台',
                    id: 'APP019',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
                {
                    name: '政务服务系统',
                    id: 'APP020',
                    icon: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export/images/logo.png`,
                },
            ],
            direction: 'rtl',
            vocabularyDrawerHeaderTabsActive: '全部',
            searchValue: '',
            applicationValue: '',
            applicationType: [
                {
                    value: null,
                    label: '全部'
                },
                {
                    value: '1',
                    label: '客服'
                },
                {
                    value: '2',
                    label: '搜索'
                },
                {
                    value: '3',
                    label: '写作'
                },
                {
                    value: '4',
                    label: '报告'
                }
            ]
        }
    },
    methods: {
        handleSizeChange(val){
          this.pageObj.pageSize=val
          this.getApplicationList()
        },
        handleCurrentChange(val){
          this.pageObj.pageNo = val
          this.getApplicationList()
        },
        selectContent(item){
            this.$EventBus.$emit("switchingModule", {status:false});
            this.$EventBus.$emit("selectProject", item);
            this.$EventBus.$emit("selectProjectClose", false);
        },
        async getApplicationList(){
            this.loading = true
            let params = {
                pageNo: this.pageObj.pageNo,
                pageSize: this.pageObj.pageSize,
                applicationName: this.searchValue,
                operationXiazhuan: true,
                onlyMine:this.vocabularyDrawerHeaderTabsActive == '我的'
            }
            let res = await getApplicationInfoList(params);
            this.loading = false
            if(res.code == '000000'){
                this.appList = res.data.records
                console.log(res)
                this.pageObj.pageNo=res.data.pageNumber
                console.log(this.pageObj.pageNo,'当前页数')
                this.pageObj.pageSize=res.data.pageSize
                console.log(this.pageObj.pageSize,'当前一页展示多少')
                this.pageObj.total = res.data.totalRow
                console.log(this.pageObj.total,'总共有多少')
            }
        },
        closeDrawer() {
            this.$EventBus.$emit("selectProjectClose", false);
        },
        vocabularyDrawerHeaderTabsFn(item){
            this.vocabularyDrawerHeaderTabsActive = item;
            this.vocabularyListActiveIndex = 0;
            this.getApplicationList();
        },
        getRandomHeadImgDefaultBgColor(index) {
            const colors = ['#5B90F9', '#BF8EE8', '#43CBC5', '#558CF8', '#EAB778', '#EB8293', '#8874E8'];
            const imgList = [
                require('@/assets/images/appManagement/default-logo-1.svg'),
                require('@/assets/images/appManagement/default-logo-2.svg'),
                require('@/assets/images/appManagement/default-logo-3.svg'),
                require('@/assets/images/appManagement/default-logo-4.svg'),
            ]
            if (index !== undefined && typeof index === 'number' && index >= 0) {
                return imgList[index % imgList.length];
            }
            const randomIndex = Math.floor(Math.random() * imgList.length);
            return imgList[randomIndex];
        },
        getApplicationTypeLabel(value) {
            return applicationTypes.find(item => item.value === value)?.label || ""
        }
    }
}
</script>
<style lang="scss" scoped>

.magnifiedEditBox-header{
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 40px;
  margin-bottom: 16px;
}

.vocabularyDrawer-Box{
    box-sizing: border-box;
  padding: 25px;
  display: flex;
  flex-direction: column;
  height: 100%;

//   overflow: hidden!important;
  .vocabularyDrawer-Con-Box{
    display: flex;
    flex-direction: column;
    flex: 1;
    margin-bottom: 24px;
    .vocabularyDrawer-Con-Box-header{
      margin-bottom: 16px;
      .vocabularyDrawer-header-tabs{
        display: flex;
        align-items: center;
        div{
          cursor: pointer;
          padding: 0 12px;
          font-size: 18px;
          color: #828894;
          line-height: 28px;
        }
        .vocabularyDrawer-header-tabs-active{
          color: #603ECA;
        }
      }
    }
  }
}


.list-content{
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
    flex-wrap: wrap;
    grid-gap: 24px 24px;
    margin-bottom: 18px;
    overflow-y: scroll;
    min-height: 0;
    height: calc(100vh - 180px);
    overflow: auto;
}

.listItem {
    height: 188px;
    padding: 16px;
    position: relative;
    background: #FFFFFF;
    border-radius: 4px;
    border: 1px solid #D5D8DE;
    cursor: pointer;
    .sign {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        line-height: 20px;
        text-align: left;
        font-style: normal;
        line-height: 20px;
        display: inline-block;
        > img {
        width: 15px;
        height: 15px;
        margin-right: 2px;
        }
        > img,
        > span {
        vertical-align: middle;
        }
    }
    .successSign {
        color: #1abc7c;
        .point {
        display: inline-block;
        width: 5px;
        height: 5px;
        background: #3FB816;
        border-radius: 50%;
        margin: 0 4px;
        }
        // background: rgba(26, 188, 124, 0.1);
    }
    .failSign {
        color: #768094;
        // background: rgba(118, 128, 148, 0.1);
    }

    .listContent {
        display: flex;
        justify-content: flex-start;
        margin-bottom: 16px;

        // align-items: center;
        // gap: 16px;
        // padding: 16px;
        .head-img-box {
        min-width: 56px;
        width: 56px;
        height: 56px;
        border-radius: 8px 0px 0px 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        background: #F2F4F7;
        border-radius: 2px;
        position: relative;
        }
        .headImg {
        width: 56px;
        border-radius: 8px 0px 0px 8px;
        max-height: 100%;
        object-fit: cover;
        // height:  144px;
        }
        .defaultHeadImg {
        width: 56px;
        max-height: 100%;
        object-fit: cover;
        }
        .item-icon {
        display: inline-flex;
        justify-content: center;
        align-items: center;
        width: 20px;
        height: 20px;
        background: #FFFFFF;
        box-shadow: 0px 2px 4px 0px rgba(0,0,0,0.1);
        position: absolute;
        bottom: -8px;
        right: -8px;
        z-index: 2;
        border-radius: 50%;
        img {
            width: 14px;
            height: 14px;
        }
        }
        .textContent {
        width: 100%;
        // padding: 16px;
        font-family: MiSans, MiSans;
        position: relative;
        overflow: hidden;
        .title-content {
            display: flex;
            align-items: center;
            margin-bottom: 8px;

        }
        .title {
            max-width: calc(100% - 32px);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            font-weight: 500;
            font-size: 18px;
            color: #494E57;
            line-height: 24px;
            text-align: left;
            font-style: normal;
            margin-right: 4px;
        }
        .list-type {
            .type-item {
            display: inline-block;
            height: 24px;
            background: #EBEEF2;
            border-radius: 2px;
            padding: 0px 8px;
            line-height: 24px;
            font-size: 12px;
            color: #494E57;
            }
        }

        .date {
            font-weight: 400;
            font-size: 14px;
            color: #828894;
            line-height: 24px;
            text-align: left;
            font-style: normal;
            width: calc(100% - 32px);
            position: absolute;
            left: 16px;
            bottom: 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            .period-box {
            display: flex;
            align-items: center;
            .point {
                width: 8px;
                height: 8px;
                position: relative;
                background: rgba(28, 80, 253, 0.1);
                border-radius: 50%;
                margin: 4px;
                &::before {
                content: "";
                width: 4px;
                height: 4px;
                background: #1c50fd;
                border-radius: 50%;
                position: absolute;
                left: 2px;
                top: 2px;
                }
            }
            }
        }
        // >p:last-child{
        //     padding: 1px 4px 4px;
        //     display: inline-block;
        //     height: 24px;
        //     background: #fff;
        //     border-radius: 4px;
        //     >img {
        //         width: 15px;
        //         height: 15px;
        //         border-radius: 4px;
        //         margin-right: 3px;
        //     }
        //     >span{
        //         font-family: MiSans, MiSans;
        //         font-weight: 400;
        //         font-size: 14px;
        //         color: #768094;
        //         line-height: 18px;
        //         text-align: left;
        //         font-style: normal;
        //     }
        //     >span,>img{
        //         vertical-align: middle;
        //     }
        // }
        }
    }

    .fotterOuter {
        display: flex;
        justify-content: space-between;
        margin-top: 28px;
        .footerItem {
        width: 33.3%;
        text-align: center;
        cursor: pointer;
        &:not(:last-child) {
            border-right: 1px solid rgba(0, 0, 0, 0.12);
        }
        img,
        span {
            vertical-align: middle;
        }
        > span {
            color: #3666ea;
        }
        > img {
            width: 15px;
            height: 15px;
            margin-right: 3px;
        }
        }
    }
    .desc-content {
        .desc {
        width: 100%;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
        text-overflow: ellipsis;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        text-align: left;
        font-style: normal;
        line-height: 22px;
        }
    }
    .list-update-time {
        max-width: calc(100% - 36px);
        display: flex;
        align-items: center;
        > span {
        display: inline-block;
        margin-right: 4px;
        font-weight: 400;
        font-size: 12px;
        color: #828894;
        }
        .list-user-icon {
        width: 24px;
        height: 24px;
        background: #F7F8FA;
        border-radius: 50%;
        text-align: center;
        line-height: 24px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        }
        .create-user {
        margin-right: 8px;
        }
        .point {
        width: 3px;
        height: 3px;
        border-radius: 50%;
        background: #BCC1CC;
        margin-right: 8px;
        }
    }
    .date {
        font-weight: 400;
        font-size: 12px;
        color: #828894;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        width: calc(100% - 32px);
        position: absolute;
        left: 16px;
        bottom: 16px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .period-box {
        display: flex;
        align-items: center;
        .point {
            width: 8px;
            height: 8px;
            position: relative;
            background: rgba(28, 80, 253, 0.1);
            border-radius: 50%;
            margin: 4px;
            &::before {
            content: "";
            width: 4px;
            height: 4px;
            background: #1c50fd;
            border-radius: 50%;
            position: absolute;
            left: 2px;
            top: 2px;
            }
        }
        }
    }
    .opts-box {
        .chakantongji{
            display: none;
        }
        
    }
    }
.listItem:hover {
box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
.chakantongji{
    display: block;
}
}


</style>
