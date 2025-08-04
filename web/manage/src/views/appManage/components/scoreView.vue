<script>
import {getScoreData} from "@/api/toolManager";

export default {
  name: "scoreView",
  data() {
    return {
      orderStepList: [
        { lable: "findQaContent", name: "检索QA【答案】" },
        { lable: "findAnswerByModel", name: "大模型发散" },
        { lable: "findQaTitle", name: "检索QA【问题】" },
        { lable: "finalCollectStrategy", name: "检索知识库" },
        { lable: "documentLibrary", name: "文档库" },
        { lable: "structuredStrategy", name: "结构化数据" },
        { lable: "networkStrategy", name: "互联网" },
        { lable: "multiMediaLibrary", name: "多媒体库" },
        { lable: "knowledgeContent", name: "知识库非精准QA" },
        { lable: "yayiKnowldegeStrategy", name: "雅意知识库" },
        { lable: "builtIn", name: "内置问题" },
        { lable: "subjectTalk", name: "讨论话题" },
        { lable: "interceptSensitive", name: "安全拦截" },

      ],
      detailStepList: [
        { lable: "documentLibrary", name: "文档库" },
        { lable: "structuredStrategy", name: "结构化数据" },
        { lable: "networkStrategy", name: "互联网" },
        { lable: "multiMediaLibrary", name: "多媒体库" },
        { lable: "knowledgeContent", name: "知识库非精准QA" },
        { lable: "WebsiteStrategy", name: "url网站" }
      ],
      strategy: 'findQaContent',
      strategyDetail: '',
      scoreList: [],
      tableLoading: false,
      testQuestion: this.question || '',
      options: [
        {label: '重排得分', value: 'score'},
        {label: 'ES得分', value: 'es_score'},
      ],
      activeScore: 'score',
    };
  },
  props:{
    question: {
      type: String,
      default: ''
    },
    applicationId: {
      type: String,
      default: ''
    }
  },
  methods: {
    findScore() {
      this.tableLoading = true;
      getScoreData({
        question: this.testQuestion,
        strategy: this.strategy,
        strategyDetail: this.strategyDetail,
        applicationId: this.applicationId
      }).then(res=>{
        this.scoreList = [...res.data];
        this.tableLoading = false;
      }).catch(error => {
        this.tableLoading = false;
      })
    }
  },
  mounted() {
  },
}
</script>

<template>
  <div class="score-view">
    <div class="score-view-item">
      <div class="item-title" style="min-width: 60px">输入问题</div>
      <el-input type="textarea" v-model="testQuestion" resize="none" :rows="4" clearable placeholder="请输入问题"></el-input>
    </div>
    <div class="score-view-item flex-center just">
      <div class="flex-center">
        <div class="flex-center">
          <span style="margin-right: 8px;">策略</span>
          <el-select v-model="strategy">
            <el-option v-for="item in orderStepList" :label="item.name" :value="item.lable"></el-option>
          </el-select>
        </div>
        <div class="flex-center" style="margin-left: 20px">
          <span style="margin-right: 8px;">子策略</span>
          <el-select v-model="strategyDetail" style="margin-right: 20px" clearable>
            <el-option v-for="item in detailStepList" :label="item.name" :value="item.lable"></el-option>
          </el-select>
        </div>
      </div>
      <el-button class="btn" type="primary" @click="findScore">
        <iconpark-icon name="focus-3-line" size="18" color="#fff" style="margin-right: 8px;"></iconpark-icon>命中测试</el-button>
    </div>
    <div class="score-view-item flex-center just">
      <span class="item-title" style="margin-bottom: 0px;">命中结果</span>
      <el-select
        class="score-select"
        v-model="activeScore"
        default-first-option
        size="mini"
        >
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
    </div>
    <div class="score-list" v-loading="tableLoading">
      <!-- <el-table
          height="500"
        :data="scoreList"
          v-loading="tableLoading"
        style="width: 100%">
        <el-table-column
          prop="title"
          :label="$t('title')"
        width="180">
        </el-table-column>
        <el-table-column
          prop="content"
          :label="$t('content')"
        width="220">
          <template slot-scope="scope">
            <el-tooltip class="item" effect="dark"  placement="top">
              <div slot="content" style="max-width: 550px">
                {{scope.row.content}}
              </div>
             <span style="display: inline-block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 200px;">
                    {{ scope.row.content }}
                  </span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          prop="esScore"
          :label="$t('ESScore')" >
        </el-table-column>
        <el-table-column
          prop="rearrangeScore"
          :label="$t('reorganizationScore')" >
        </el-table-column>
      </el-table> -->
      <div class="list-item" v-for="(item, index) in scoreList" :key="index">
        <div class="list-item-title">
          <div class="title">
            <p>{{ item.title }}</p>
          </div>
          
          <div class="score" v-if="item.rearrangeScore && activeScore === 'score'">
            <img src="../../../assets/images/appManagement/mzcs.svg" alt="">{{ item.rearrangeScore }}
          </div>
          <div class="score es" v-if="item.esScore && activeScore === 'es_score'">
            <img src="../../../assets/images/appManagement/esdf.svg" alt="">{{ item.esScore}}
          </div>
        </div>
        <div class="list-item-content" :title="item.content">
          {{ item.content }}
        </div>
      </div>
      <div class="list-no-data" style="padding: 24px" v-if="scoreList.length == 0 && !tableLoading">
        <p style="text-align: center;">暂无数据</p>
      </div>
    </div>
    
  </div>
</template>

<style scoped lang="scss">
.score-view {
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  .score-view-item {
    margin-bottom: 16px;
    .item-title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 18px;
      color: #494E57;
      line-height: 32px;
      margin-bottom: 8px;
    }
    :deep(.score-select) {
      width: 92px;
      border: 0px;
      font-weight: 400;
      font-size: 16px;
      color: #494E57;
      .el-input__inner {
        border: 0px;
        background-color: transparent;
      }
    }
    :deep(.btn)  {
      height: 40px;
      padding: 0 16px;
      > span {
        display: inline-flex;
        align-items: center;
      }
    }
  }
  .score-list {
    flex: 1;
    overflow-y: auto;
    padding: 0 6px;
    .list-item {
      margin-bottom: 12px;
      padding: 16px;
      border-radius: 2px;
      border: 1px solid #D5D8DE;
      background: #FFFFFF;
      .list-item-title {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 16px;
        color: #494E57;
        line-height: 20px;
        .title {
          flex: 1;
          margin-right: 16px;
          overflow: hidden;
          p {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
        .score {
          display: inline-flex;
          align-items: center;
          font-weight: 500;
          font-size: 16px;
          color: #7E56EB;
          line-height: 20px;
          img {
            width: 20px;
            height: 20px;
            margin-right: 4px;
          }
          &.es {
            color: #1747E5;
          }
        }
      }
      .list-item-content {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 22px;
        width: 100%;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 3;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
}
.flex-center {
  display: flex;
  align-items: center;
}
.just {
  justify-content: space-between;
}
</style>