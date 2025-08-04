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
        question: this.question,
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
  <div style="padding: 10px">
    <div style="display: flex;justify-content: space-between;align-items: center">
      <span style="min-width: 60px">{{ $t('question') }}：</span>
      <el-input v-model="question" clearable></el-input>
    </div>
    <div style="display: flex;align-items: center;margin-top: 10px">
      <div style="display: flex;align-items: center;">
        <span style="min-width: 60px">{{ $t("strategy")}}：</span>
        <el-select v-model="strategy">
          <el-option v-for="item in orderStepList" :label="item.name" :value="item.lable"></el-option>
        </el-select>
      </div>
      <div style="display: flex;align-items: center;margin-left: 20px">
        <span style="min-width: 80px;margin-left: 10px">{{ $t("subStrategy")}}：</span>
        <el-select v-model="strategyDetail" style="margin-right: 20px" clearable>
          <el-option v-for="item in detailStepList" :label="item.name" :value="item.lable"></el-option>
        </el-select>
        <el-button type="success" size="mini" @click="findScore">{{ $t("query")}}</el-button>
      </div>
    </div>
    <div>
      <el-table
          height="700"
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
      </el-table>
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>