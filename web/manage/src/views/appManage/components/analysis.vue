<template>
  <div class="analysis">
    <div class="analysis-content">
      <!-- <div class="analysis-content-title">{{ $t("overviewIndicator") }}</div> -->
      <ul class="analysis-content-statistics">
        <li>
          <div class="analysis-content-statistics-top">
            <img class="title-icon" src="@/assets/images/used.png" alt="" />
            <div class="label">{{ $t("visits") }}</div>
            <el-tooltip
              class="item"
              effect="dark"
              content="--"
              placement="right"
            >
              <img
                class="question-icon"
                src="@/assets/images/question-icon.png"
                alt=""
              />
            </el-tooltip>
          </div>
          <div class="analysis-content-statistics-bottom">
            <div class="num">
              {{ sourceData?.VISIT_TOTAL?.total?.toLocaleString()
              }}<span class="unit">{{ sourceData?.VISIT_TOTAL?.unit }}</span>
            </div>
            <div class="unit">
              {{ $t("monthlyChange")
              }}<span
                class="percent"
                :class="[
                  sourceData?.VISIT_TOTAL?.QOQ?.includes('-')
                    ? ''
                    : 'percent-red',
                ]"
                ><i
                  :class="[
                    sourceData?.VISIT_TOTAL?.QOQ?.includes('-')
                      ? 'el-icon-caret-bottom'
                      : 'el-icon-caret-top',
                  ]"
                ></i>
                {{ sourceData?.VISIT_TOTAL?.QOQ }}</span
              >
            </div>
          </div>
        </li>
        <li>
          <div class="analysis-content-statistics-top">
            <img class="title-icon" src="@/assets/images/used.png" alt="" />
            <div class="label">{{ $t("totalUsage") }}</div>
            <el-tooltip
              class="item"
              effect="dark"
              content="--"
              placement="right"
            >
              <img
                class="question-icon"
                src="@/assets/images/question-icon.png"
                alt=""
              />
            </el-tooltip>
          </div>
          <div class="analysis-content-statistics-bottom">
            <div class="num">
              {{ sourceData?.USAGE_TOTAL?.total?.toLocaleString()
              }}<span class="unit">{{ sourceData?.USAGE_TOTAL?.unit }}</span>
            </div>
            <div class="unit">
              {{ $t("monthlyChange")
              }}<span
                class="percent"
                :class="[
                  sourceData?.USAGE_TOTAL?.QOQ?.includes('-')
                    ? ''
                    : 'percent-red',
                ]"
                ><i
                  :class="[
                    sourceData?.USAGE_TOTAL?.QOQ?.includes('-')
                      ? 'el-icon-caret-bottom'
                      : 'el-icon-caret-top',
                  ]"
                ></i>
                {{ sourceData?.USAGE_TOTAL?.QOQ }}</span
              >
            </div>
          </div>
        </li>
        <li>
          <div class="analysis-content-statistics-top">
            <img
              class="title-icon"
              src="@/assets/images/total-user.png"
              alt=""
            />
            <div class="label">{{ $t("totalUserCount") }}</div>
            <el-tooltip
              class="item"
              effect="dark"
              content="--"
              placement="right"
            >
              <img
                class="question-icon"
                src="@/assets/images/question-icon.png"
                alt=""
              />
            </el-tooltip>
          </div>
          <div class="analysis-content-statistics-bottom">
            <div class="num">
              {{ sourceData?.USAGE_USER_TOTAL?.total?.toLocaleString()
              }}<span class="unit">{{
                sourceData?.USAGE_USER_TOTAL?.unit
              }}</span>
            </div>
            <div class="unit">
              {{ $t("monthlyChange")
              }}<span
                class="percent"
                :class="[
                  sourceData?.USAGE_USER_TOTAL?.QOQ?.includes('-')
                    ? ''
                    : 'percent-red',
                ]"
                ><i
                  :class="[
                    sourceData?.USAGE_USER_TOTAL?.QOQ?.includes('-')
                      ? 'el-icon-caret-bottom'
                      : 'el-icon-caret-top',
                  ]"
                ></i>
                {{ sourceData?.USAGE_USER_TOTAL?.QOQ }}</span
              >
            </div>
          </div>
        </li>
        <li>
          <div class="analysis-content-statistics-top">
            <img class="title-icon" src="@/assets/images/add-user.png" alt="" />
            <div class="label">{{ $t("todayNewUserCount") }}</div>
            <el-tooltip
              class="item"
              effect="dark"
              content="--"
              placement="right"
            >
              <img
                class="question-icon"
                src="@/assets/images/question-icon.png"
                alt=""
              />
            </el-tooltip>
          </div>
          <div class="analysis-content-statistics-bottom">
            <div class="num">
              {{ sourceData?.USAGE_ADD_USER_TOTAL?.total?.toLocaleString()
              }}<span class="unit">{{
                sourceData?.USAGE_ADD_USER_TOTAL?.unit
              }}</span>
            </div>
            <!-- <div class="unit">
              环比<span class="percent"
                ><i class="el-icon-caret-bottom"></i> {{ sourceData.USAGE_ADD_USER_TOTAL.QOQ }}</span
              >
            </div> -->
          </div>
        </li>
        <li>
          <div class="analysis-content-statistics-top">
            <img class="title-icon" src="@/assets/images/percent.png" alt="" />
            <div class="label">{{ $t("todayUserUsageRate") }}</div>
            <el-tooltip
              class="item"
              effect="dark"
              content="--"
              placement="right"
            >
              <img
                class="question-icon"
                src="@/assets/images/question-icon.png"
                alt=""
              />
            </el-tooltip>
          </div>
          <div class="analysis-content-statistics-bottom">
            <div class="num">
              {{ sourceData?.USAGE_USER_DAY_RETENTION_RATE?.total
              }}<span class="unit">{{
                sourceData?.USAGE_USER_DAY_RETENTION_RATE?.unit
              }}</span>
            </div>
            <!-- <div class="unit">
              环比<span class="percent"
                ><i class="el-icon-caret-bottom"></i> {{ sourceData.USAGE_USER_DAY_RETENTION_RATE.QOQ }}</span
              >
            </div> -->
          </div>
        </li>
      </ul>
      <div class="analysis-head">
        <ul class="analysis-head-tabs">
          <li :class="[tabs == 1 ? 'selected' : '']" @click="tabsClick(1)">
            {{ $t("usageStatistics") }}
          </li>
          <li :class="[tabs == 2 ? 'selected' : '']" @click="tabsClick(2)">
            {{ $t("qualityOfQ&A") }}
          </li>
        </ul>
        <div style="display: flex; align-items: center">
          <el-date-picker
            v-model="time"
            type="daterange"
            :range-separator="$t('to')"
            :start-placeholder="$t('startDate')"
            :end-placeholder="$t('endDate')"
            value-format="yyyy-MM-dd"
          >
          </el-date-picker>
          <el-button type="primary" @click="searchHandler">{{
            $t("search")
          }}</el-button>
          <el-button plain @click="resetHandler">{{ $t("reset") }}</el-button>
        </div>
        <!-- <div class="analysis-head-update">
        更新时间：{{ date || "2024-07-11" }}(每天0点更新数据）
      </div> -->
      </div>

      <div v-show="tabs == 1" class="analysis-content-show">
        <div style="flex: 1" v-loading="loading">
          <!-- <div class="analysis-content-title">{{ $t("detailedMetrics") }}</div> -->
          <div class="analysis-content-show-chart">
            <div class="chart-top">
              <div class="echart" ref="echartOne"></div>
              <div class="echart" ref="echartTwo"></div>
            </div>
            <div class="chart-bottom">
              <div class="echart" ref="echartThree"></div>
              <div class="echart" ref="echartFour"></div>
            </div>
          </div>
        </div>
        <div style="width: 512px">
          <div
            style="
              display: flex;
              align-items: center;
              justify-content: space-between;
            "
          >
            <div class="analysis-content-title">
              {{ $t("top50QuestionRanking") }}
              <el-tooltip
                class="item"
                effect="dark"
                content="--"
                placement="right"
              >
                <img
                  class="question-icon"
                  src="@/assets/images/question-icon.png"
                  alt=""
                />
              </el-tooltip>
            </div>
            <el-select
              v-model="top50Type"
              style="margin-top: 16px; width: 166px"
              @change="selectChange"
            >
              <el-option :label="$t('mostFrequentQuestions')" value="count" />
              <el-option :label="$t('timeSorting')" value="time" />
            </el-select>
          </div>
          <ul
            v-if="top50TypeList.length"
            class="analysis-content-show-ranking"
            v-loading="loading2"
          >
            <li
              v-for="(item, index) in top50TypeList"
              :key="index"
              class="analysis-content-show-ranking-item"
              :class="`item${index + 1}`"
            >
              <div class="top" :class="`top${index + 1}`">{{ index + 1 }}</div>
              <div class="txt" :class="`txt${index + 1}`">
                {{ item.question }}
              </div>
              <div
                v-if="top50Type == 'count'"
                class="number"
                :class="`number${index + 1}`"
              >
                {{ item.count }}
              </div>
              <div v-else class="number time">{{ item.createTime }}</div>
            </li>
          </ul>
          <div v-else class="analysis-content-show-ranking no-data">
            <img class="no-data-img" src="@/assets/images/no-data.png" alt="" />
            <div class="no-data-text">{{ $t("noData") }}</div>
          </div>
        </div>
      </div>
      <div v-show="tabs == 2" class="analysis-content-answer">
        <div class="chart-top">
          <div v-loading="loading5" class="chart-item">
            <div class="title">
              <div class="name">{{ $t("likeRanking") }}</div>
              <el-tooltip
                class="item"
                effect="dark"
                content="--"
                placement="bottom"
              >
                <img
                  class="question-icon"
                  src="@/assets/images/question-icon.png"
                  alt=""
                />
              </el-tooltip>
              <div class="unit">{{ $t("unit") }}：{{ $t("order") }}</div>
              <el-button
                type="text"
                style="
                  margin-left: auto;
                  width: 88px;
                  border-radius: 4px;
                  border: 1px solid #1c50fd;
                  color: #1c50fd;
                "
                @click="exportFile(1, deptId1)"
                ><img
                  src="@/assets/images/export-icon.svg"
                  class="export-icon"
                  alt=""
                />
                {{ $t("export") }}</el-button
              >
            </div>
            <div v-if="level1 == 2" class="level-two">
              <div class="dept-name">{{ deptName1 }}</div>
              <div class="come-back" @click="comeBack(1)">
                <iconpark-icon
                  name="arrow-go-back-fill"
                  size="16"
                  color="#1C50FD"
                ></iconpark-icon
                >{{ $t("returnToDepartmentRanking") }}
              </div>
            </div>
            <div
              v-show="chartDataFive.length"
              class="echart"
              ref="echartFive"
              :style="{
                height:
                  level1 == 2
                    ? 'calc(100% - 56px - 40px)'
                    : 'calc(100% - 40px)',
              }"
            ></div>
            <div
              v-show="!chartDataFive.length"
              class="echart no-data"
              :style="{
                height:
                  level1 == 1
                    ? 'calc(100% - 40px)'
                    : 'calc(100% - 56px - 40px)',
              }"
            >
              {{ $t("noData") }}
            </div>
          </div>
          <div v-loading="loading8" class="chart-item">
            <div class="title">
              <div class="name">{{ $t("rankingByNumberOfClicks") }}</div>
              <el-tooltip
                class="item"
                effect="dark"
                content="--"
                placement="bottom"
              >
                <img
                  class="question-icon"
                  src="@/assets/images/question-icon.png"
                  alt=""
                />
              </el-tooltip>
              <div class="unit">{{ $t("unit") }}：{{ $t("order") }}</div>
              <el-button
                type="text"
                style="
                  margin-left: auto;
                  width: 88px;
                  border-radius: 4px;
                  border: 1px solid #1c50fd;
                  color: #1c50fd;
                "
                @click="exportFile(0, deptId0)"
                ><img
                  src="@/assets/images/export-icon.svg"
                  class="export-icon"
                  alt=""
                />
                {{ $t("export") }}</el-button
              >
            </div>
            <!-- 二级title -->
            <div v-if="level0 == 2" class="level-two">
              <div class="dept-name">{{ deptName0 }}</div>
              <div class="come-back" @click="comeBack(0)">
                <iconpark-icon
                  name="arrow-go-back-fill"
                  size="16"
                  color="#1C50FD"
                ></iconpark-icon
                >{{ $t("returnToDepartmentRanking") }}
              </div>
            </div>
            <div
              v-show="chartDataSix.length"
              ref="echartSix"
              class="echart"
              :style="{
                height:
                  level0 == 2
                    ? 'calc(100% - 56px - 40px)'
                    : 'calc(100% - 40px)',
              }"
            ></div>
            <div
              v-show="!chartDataSix.length"
              class="echart no-data"
              :style="{
                height:
                  level0 == 1
                    ? 'calc(100% - 40px)'
                    : 'calc(100% - 56px - 40px)',
              }"
            >
              {{ $t("noData") }}
            </div>
          </div>
        </div>
        <div class="chart-bottom">
          <div v-loading="loading6" class="chart-item">
            <div class="title">
              <div class="name">
                {{ $t("rankingOfQ&AAdditionsInTheKnowledgeBase") }}
              </div>
              <el-tooltip
                class="item"
                effect="dark"
                content="--"
                placement="bottom"
              >
                <img
                  class="question-icon"
                  src="@/assets/images/question-icon.png"
                  alt=""
                />
              </el-tooltip>
              <div class="unit">{{ $t("unit") }}：{{ $t("strip") }}</div>
              <el-button
                type="text"
                style="
                  margin-left: auto;
                  width: 88px;
                  border-radius: 4px;
                  border: 1px solid #1c50fd;
                  color: #1c50fd;
                "
                @click="exportFile(2, deptId3)"
                ><img
                  src="@/assets/images/export-icon.svg"
                  class="export-icon"
                  alt=""
                />
                {{ $t("export") }}</el-button
              >
            </div>
            <div v-if="level3 == 2" class="level-two">
              <div class="dept-name">{{ deptName3 }}</div>
              <div class="come-back" @click="comeBack(3)">
                <iconpark-icon
                  name="arrow-go-back-fill"
                  size="16"
                  color="#1C50FD"
                ></iconpark-icon
                >{{ $t("returnToDepartmentRanking") }}
              </div>
            </div>
            <div
              v-show="chartDataSeven.length"
              class="echart"
              ref="echartSeven"
              :style="{
                height:
                  level3 == 2
                    ? 'calc(100% - 56px - 40px)'
                    : 'calc(100% - 40px)',
              }"
            ></div>
            <div
              v-show="!chartDataSeven.length"
              class="echart no-data"
              :style="{
                height:
                  level3 == 1
                    ? 'calc(100% - 40px)'
                    : 'calc(100% - 56px - 40px)',
              }"
            >
              {{ $t("noData") }}
            </div>
          </div>
          <div v-loading="loading7" class="chart-item">
            <div class="title">
              <div class="name">{{ $t("rankingOfQ&AReviewNumbers") }}</div>
              <el-tooltip
                class="item"
                effect="dark"
                content="--"
                placement="bottom"
              >
                <img
                  class="question-icon"
                  src="@/assets/images/question-icon.png"
                  alt=""
                />
              </el-tooltip>
              <div class="unit">{{ $t("unit") }}：{{ $t("order") }}</div>
              <el-button
                type="text"
                style="
                  margin-left: auto;
                  width: 88px;
                  border-radius: 4px;
                  border: 1px solid #1c50fd;
                  color: #1c50fd;
                "
                @click="exportFile(3, deptId4)"
                ><img
                  src="@/assets/images/export-icon.svg"
                  class="export-icon"
                  alt=""
                />
                {{ $t("export") }}</el-button
              >
            </div>
            <div v-if="level4 == 2" class="level-two">
              <div class="dept-name">{{ deptName4 }}</div>
              <div class="come-back" @click="comeBack(4)">
                <iconpark-icon
                  name="arrow-go-back-fill"
                  size="16"
                  color="#1C50FD"
                ></iconpark-icon
                >{{ $t("returnToDepartmentRanking") }}
              </div>
            </div>
            <div
              v-show="chartDataEight.length"
              class="echart"
              ref="echartEight"
              :style="{
                height:
                  level4 == 2
                    ? 'calc(100% - 56px - 40px)'
                    : 'calc(100% - 40px)',
              }"
            ></div>
            <div
              v-show="!chartDataEight.length"
              class="echart no-data"
              :style="{
                height:
                  level4 == 1
                    ? 'calc(100% - 40px)'
                    : 'calc(100% - 56px - 40px)',
              }"
            >
              {{ $t("noData") }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from "echarts";
import axios from "axios";
import {
  apiQuestionChartsTop50,
  apiApplicationOverviewIndicators,
  apiGetApplicationOverviewIndicatorsByType,
  apiGetApplicationLikeStepRanking,
  apiGetApplicationAddQARanking,
  apiGetApplicationDialogueReviewRanking,
} from "@/api/analysis";
export default {
  name: "Analysis",
  props: {
    data: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      time: "",
      date: "",
      top50Type: "count",
      top50TypeList: [],
      echart: null,
      sourceData: {},
      chartDataOne: [],
      chartDataTwo: [],
      chartDataThree: [],
      chartDataFour: [],
      chartDataFive: [],
      chartDataSix: [],
      chartDataSeven: [],
      chartDataEight: [],
      loading: false,
      loading2: false,
      tabs: 1, // 1: 使用统计 2: 问答质量
      level1: 1, //  1：点赞排名 2：点赞下钻
      deptId1: "",
      deptName1: "",
      level0: 0, //  1：点踩排名 2：点踩下钻
      deptId0: "",
      deptName0: "",
      level3: 0, //  1：知识库问答添加数排名 2：知识库问答添加数排名下钻
      deptId3: "",
      deptName3: "",
      level4: 0, //  1：问答审核数排名 2：问答审核数下钻
      deptId4: "",
      deptName4: "",
      loading5: false,
      loading6: false,
      loading7: false,
      loading8: false,
    };
  },
  computed: {
    startTime() {
      return this.time && this.time?.length ? `${this.time[0]} 00:00:00` : "";
    },
    endTime() {
      return this.time && this.time?.length ? `${this.time[1]} 23:59:59` : "";
    },
  },
  mounted() {
    this.applicationOverviewIndicators();
    this.initChartData();
    this.questionChartsTop50();
  },
  methods: {
    async initChartData() {
      this.loading = true;
      if (this.tabs == 1) {
        await this.getApplicationOverviewIndicatorsByType(1);
        await this.getApplicationOverviewIndicatorsByType(2);
        await this.getApplicationOverviewIndicatorsByType(3);
        await this.getApplicationOverviewIndicatorsByType(4);
      } else {
        this.getApplicationLikeStepRanking(0);
        this.getApplicationLikeStepRanking(1);
        this.getApplicationAddQARanking();
        this.getApplicationDialogueReviewRanking();
      }
      this.loading = false;
    },
    searchHandler() {
      this.initChartData();
    },
    resetHandler() {
      this.time = "";
      this.initChartData();
    },
    async questionChartsTop50() {
      this.loading2 = true;
      const params = {
        applicationId: this.data?.applicationId,
        top50Type: this.top50Type,
      };
      const res = await apiQuestionChartsTop50(params);
      if (res.code == "000000") {
        this.top50TypeList = res.data || [];
      }
      this.loading2 = false;
    },
    async applicationOverviewIndicators() {
      const params = {
        applicationId: this.data?.applicationId,
      };
      const res = await apiApplicationOverviewIndicators(params);
      if (res.code == "000000") {
        this.sourceData = res.data || [];
      }
    },
    async getApplicationOverviewIndicatorsByType(type) {
      const params = {
        applicationId: this.data?.applicationId,
        type,
        startTime: this.startTime,
        endTime: this.endTime,
      };
      const res = await apiGetApplicationOverviewIndicatorsByType(params);
      if (res.code == "000000") {
        switch (type) {
          case 1:
            this.chartDataOne = res.data || [];
            break;
          case 2:
            this.chartDataTwo = res.data || [];
            break;
          case 4:
            this.chartDataThree = res.data || [];
            break;
          case 3:
            this.chartDataFour = res.data || [];
            break;

          default:
            break;
        }

        this.$nextTick(() => {
          this.initChartOne();
          this.initChartTwo();
          this.initChartThree();
          this.initChartFour();
        });
      }
    },
    selectChange() {
      this.questionChartsTop50();
    },
    initChartOne() {
      var myChart = echarts.init(this.$refs.echartOne);
      const xData = this.chartDataOne.map((i) => i.endTime);
      const yData1 = this.chartDataOne.map((i) => i.showCount);
      const yData2 = this.chartDataOne.map((i) => i.showCountPc);
      const flag = yData1?.every((item) => item == 0);
      var option = {
        title: {
          text: this.$t("usageCount"),
          textStyle: {
            color: "#383D47",
            fontSize: 16,
          },
        },
        color: ["#2DB8FE", "#8E65FF"],
        legend: {
          x: "-1%",
          top: "9%",
          textStyle: {
            color: "#8C8C8C",
            fontSize: 14,
          },
          icon: "rect",
          itemHeight: 4,
          data: [this.$t("webVersion"), this.$t("mobileVersion")],
          show: false,
        },
        xAxis: {
          type: "category",
          axisLine: {
            lineStyle: {
              color: "#828894",
            },
          },
          axisTick: {
            // 控制刻度是在轴线内侧还是外侧
            inside: true,
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: 20,
          },
          boundaryGap: true,
          data: xData,
        },
        yAxis: {
          type: "value",
          axisLine: {
            show: false, // 隐藏Y轴线
          },
          axisTick: {
            show: false,
          },
          axisLabel: {
            // 字体大小
            fontSize: 14,
            // 字体颜色
            color: "#828894",
          },
          min: flag ? 0 : null,
          max: flag ? 100 : null,
        },
        series: [
          {
            name: this.$t("webVersion"),
            data: yData1,
            type: "line",
            symbol: "circle",
            symbolSize: 3,
            label: {
              show: true,
              position: "right",
              formatter: function (params) {
                if (params.value > 0) {
                  return params.value;
                } else {
                  return "";
                }
              },
            },
          },
          // {
          //   name: "移动版",
          //   data: yData2,
          //   type: "line",
          //   symbol: "none",
          // },
        ],
      };
      myChart.setOption(option);
    },
    initChartTwo() {
      var myChart = echarts.init(this.$refs.echartTwo);
      const xData = this.chartDataTwo.map((i) => i.endTime);
      const yData1 = this.chartDataTwo.map((i) => i.showCount);
      const yData2 = this.chartDataTwo.map((i) => i.showCountPc);
      const flag = yData1?.every((item) => item == 0);
      var option = {
        title: {
          text: this.$t("userCount"),
          textStyle: {
            color: "#383D47",
            fontSize: 16,
          },
        },
        color: ["#2DB8FE", "#8E65FF"],
        legend: {
          x: "-1%",
          top: "9%",
          textStyle: {
            color: "#8C8C8C",
            fontSize: 14,
          },
          icon: "rect",
          itemHeight: 4,
          data: [this.$t("webVersion"), this.$t("mobileVersion")],
          show: false,
        },
        xAxis: {
          type: "category",
          axisLine: {
            lineStyle: {
              color: "#828894",
            },
          },
          axisTick: {
            // 控制刻度是在轴线内侧还是外侧
            inside: true,
          },
          axisLabel: {
            rotate: 20,
          },
          boundaryGap: true,
          data: xData,
        },
        yAxis: {
          type: "value",
          axisLine: {
            show: false, // 隐藏Y轴线
          },
          axisTick: {
            show: false,
          },
          axisLabel: {
            // 字体大小
            fontSize: 14,
            // 字体颜色
            color: "#828894",
          },
          min: flag ? 0 : null,
          max: flag ? 100 : null,
        },
        series: [
          {
            name: this.$t("webVersion"),
            data: yData1,
            type: "bar",
            symbol: "circle",
            symbolSize: 3,
            label: {
              show: true,
              position: "top",
              formatter: function (params) {
                if (params.value > 0) {
                  return params.value;
                } else {
                  return "";
                }
              },
            },
          },
          // {
          //   name: "移动版",
          //   data: yData2,
          //   type: "bar",
          // },
        ],
      };
      myChart.setOption(option);
    },
    initChartThree() {
      var myChart = echarts.init(this.$refs.echartThree);
      const xData = this.chartDataThree.map((i) => i.endTime);
      const yData1 = this.chartDataThree.map((i) => i.showCount);
      const yData2 = this.chartDataThree.map((i) => i.showCountPc);
      var option = {
        title: {
          text: this.$t("userUsageRate"),
          textStyle: {
            color: "#383D47",
            fontSize: 16,
          },
        },
        color: ["#2DB8FE", "#8E65FF"],
        legend: {
          x: "-1%",
          top: "9%",
          textStyle: {
            color: "#8C8C8C",
            fontSize: 14,
          },
          icon: "rect",
          itemHeight: 4,
          data: [this.$t("webVersion"), this.$t("mobileVersion")],
          show: false,
        },
        xAxis: {
          type: "category",
          axisLine: {
            lineStyle: {
              color: "#828894",
            },
          },
          axisTick: {
            // 控制刻度是在轴线内侧还是外侧
            inside: true,
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: 20,
          },
          boundaryGap: true,
          data: xData,
        },
        yAxis: {
          type: "value",
          axisLine: {
            show: false, // 隐藏Y轴线
          },
          axisTick: {
            show: false,
          },
          axisLabel: {
            // 字体大小
            fontSize: 14,
            // 字体颜色
            color: "#828894",
            formatter: function (val) {
              return `${val ? val * 100 : 0}`;
            },
          },
        },
        series: [
          {
            name: this.$t("webVersion"),
            data: yData1,
            type: "line",
            symbol: "circle",
            symbolSize: 3,
            label: {
              show: true,
              position: "right",
              formatter: function (params) {
                if (params.value > 0) {
                  return params.value;
                } else {
                  return "";
                }
              },
            },
            areaStyle: {
              normal: {
                color: new echarts.graphic.LinearGradient(
                  0,
                  0,
                  0,
                  1,
                  [
                    {
                      offset: 0,
                      color: `rgba(45, 184, 254, 0.4)`,
                    },
                    {
                      offset: 1,
                      color: "rgba(45, 184, 254, 0.1)",
                    },
                  ],
                  false
                ),
              },
            },
          },
          // {
          //   name: "移动版",
          //   data: yData2,
          //   type: "line",
          //   symbol: "none",
          // },
        ],
      };
      myChart.setOption(option);
    },
    initChartFour() {
      var myChart = echarts.init(this.$refs.echartFour);
      const xData = this.chartDataFour.map((i) => i.endTime);
      const yData1 = this.chartDataFour.map((i) => i.showCount);
      const yData2 = this.chartDataFour.map((i) => i.showCountPc);
      const flag = yData1?.every((item) => item == 0);
      var option = {
        title: {
          text: this.$t("newUserCount"),
          textStyle: {
            color: "#383D47",
            fontSize: 16,
          },
        },
        color: ["#2DB8FE", "#8E65FF"],
        legend: {
          x: "-1%",
          top: "9%",
          textStyle: {
            color: "#8C8C8C",
            fontSize: 14,
          },
          icon: "rect",
          itemHeight: 4,
          data: [this.$t("webVersion"), this.$t("mobileVersion")],
          show: false,
        },
        xAxis: {
          type: "category",
          axisLine: {
            lineStyle: {
              color: "#828894",
            },
          },
          axisTick: {
            // 控制刻度是在轴线内侧还是外侧
            inside: true,
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: 20,
          },
          boundaryGap: true,
          data: xData,
        },
        yAxis: {
          type: "value",
          axisLine: {
            show: false, // 隐藏Y轴线
          },
          axisTick: {
            show: false,
          },
          axisLabel: {
            // 字体大小
            fontSize: 14,
            // 字体颜色
            color: "#828894",
          },
          min: flag ? 0 : null,
          max: flag ? 100 : null,
        },
        series: [
          {
            name: this.$t("webVersion"),
            data: yData1,
            type: "line",
            symbol: "circle",
            symbolSize: 3,
            label: {
              show: true,
              position: "right",
              formatter: function (params) {
                if (params.value > 0) {
                  return params.value;
                } else {
                  return "";
                }
              },
            },
          },
          // {
          //   name: "移动版",
          //   data: yData2,
          //   type: "line",
          //   symbol: "none",
          // },
        ],
      };
      myChart.setOption(option);
    },
    // 点赞点踩排名
    async getApplicationLikeStepRanking(feedbackType, deptId) {
      if (feedbackType == 1) {
        this.loading5 = true;
      } else {
        this.loading8 = true;
      }

      const params = {
        applicationId: this.data?.applicationId,
        startTime: this.startTime,
        endTime: this.endTime,
        feedbackType, // 1-赞 0-踩
        deptId, //不传值返回的部门维度列表   传值返回部门下用户维度列表
      };
      const res = await apiGetApplicationLikeStepRanking(params);
      if (res.code == "000000") {
        switch (feedbackType) {
          case 1:
            this.chartDataFive = res.data || [];
            this.$nextTick(() => {
              this.initChartFive();
            });
            break;
          case 0:
            this.chartDataSix = res.data || [];
            this.$nextTick(() => {
              this.initChartSix();
            });
            break;

          default:
            break;
        }
      }
      if (feedbackType == 1) {
        this.loading5 = false;
      } else {
        this.loading8 = false;
      }
    },
    // 点赞数排名
    initChartFive() {
      var myChart = echarts.init(this.$refs.echartFive);
      var salvProName = this.chartDataFive.map((i) => i.showName);
      var salvProValue = this.chartDataFive.map((i) => i.count);
      var salvProMax = []; //背景按最大值
      for (let i = 0; i < salvProValue.length; i++) {
        salvProMax.push(salvProValue[0]);
      }
      var option = {
        backgroundColor: "#fff",
        grid: {
          left: "2%",
          right: "2%",
          bottom: "2%",
          top: "2%",
          containLabel: true,
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "none",
          },
          formatter: function (params) {
            return params[0].name + " : " + params[0].value;
          },
        },
        xAxis: {
          show: false,
          type: "value",
        },
        yAxis: [
          {
            type: "category",
            inverse: true,
            axisLabel: {
              show: true,
              textStyle: {
                color: "#828894",
              },
            },
            splitLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLine: {
              show: false,
            },
            data: salvProName,
          },
          {
            type: "category",
            inverse: true,
            axisTick: "none",
            axisLine: "none",
            show: true,
            axisLabel: {
              textStyle: {
                color: "#383D47",
                fontSize: "14",
              },
            },
            data: salvProValue,
          },
        ],
        series: [
          {
            name: "值",
            type: "bar",
            zlevel: 1,
            itemStyle: {
              normal: {
                barBorderRadius: 0,
                color: "#769CFE",
              },
            },
            barWidth: 12,
            data: salvProValue,
          },
          {
            name: "背景",
            type: "bar",
            barWidth: 12,
            barGap: "-100%",
            data: salvProMax,
            itemStyle: {
              normal: {
                color: "rgba(255,255,255,1)",
                barBorderRadius: 0,
              },
            },
          },
        ],
      };
      myChart.setOption(option);
      const that = this;
      myChart.on("click", function (params) {
        if (that.level1 == 2) return;

        that.deptId1 = that.chartDataFive.find(
          (i) => i.showName == params.name
        )?.keyId;
        that.deptName1 = that.chartDataFive.find(
          (i) => i.showName == params.name
        )?.showName;
        that.level1 = 2;
        that.getApplicationLikeStepRanking(1, that.deptId1);
      });
      setTimeout(() => {
        myChart.resize();
      }, 500);
    },
    // 点踩数排名
    initChartSix() {
      var myChart = echarts.init(this.$refs.echartSix);
      var salvProName = this.chartDataSix.map((i) => i.showName);
      var salvProValue = this.chartDataSix.map((i) => i.count);
      var salvProMax = []; //背景按最大值
      for (let i = 0; i < salvProValue.length; i++) {
        salvProMax.push(salvProValue[0]);
      }
      var option = {
        backgroundColor: "#fff",
        grid: {
          left: "2%",
          right: "2%",
          bottom: "2%",
          top: "2%",
          containLabel: true,
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "none",
          },
          formatter: function (params) {
            return params[0].name + " : " + params[0].value;
          },
        },
        xAxis: {
          show: false,
          type: "value",
        },
        yAxis: [
          {
            type: "category",
            inverse: true,
            axisLabel: {
              show: true,
              textStyle: {
                color: "#828894",
              },
            },
            splitLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLine: {
              show: false,
            },
            data: salvProName,
          },
          {
            type: "category",
            inverse: true,
            axisTick: "none",
            axisLine: "none",
            show: true,
            axisLabel: {
              textStyle: {
                color: "#383D47",
                fontSize: "14",
              },
            },
            data: salvProValue,
          },
        ],
        series: [
          {
            name: "值",
            type: "bar",
            zlevel: 1,
            itemStyle: {
              normal: {
                barBorderRadius: 0,
                color: "#769CFE",
              },
            },
            barWidth: 12,
            data: salvProValue,
          },
          {
            name: "背景",
            type: "bar",
            barWidth: 12,
            barGap: "-100%",
            data: salvProMax,
            itemStyle: {
              normal: {
                color: "rgba(255,255,255,1)",
                barBorderRadius: 0,
              },
            },
          },
        ],
      };
      myChart.setOption(option);
      const that = this;
      myChart.on("click", function (params) {
        if (that.level0 == 2) return;
        that.deptId0 = that.chartDataSix.find(
          (i) => i.showName == params.name
        )?.keyId;
        that.deptName0 = that.chartDataSix.find(
          (i) => i.showName == params.name
        )?.showName;
        that.level0 = 2;
        that.getApplicationLikeStepRanking(0, that.deptId0);
      });
      setTimeout(() => {
        myChart.resize();
      }, 500);
    },
    // 返回上一级
    comeBack(num) {
      this[`deptName${num}`] = "";
      this[`level${num}`] = 1;
      if ([0, 1].includes(num)) {
        this.getApplicationLikeStepRanking(num);
      } else if (num == 3) {
        this.getApplicationAddQARanking();
      } else {
        this.getApplicationDialogueReviewRanking();
      }
    },
    // 知识库问答添加数排名
    async getApplicationAddQARanking(deptId) {
      this.loading6 = true;
      const params = {
        applicationId: this.data?.applicationId,
        startTime: this.startTime,
        endTime: this.endTime,
        deptId,
      };
      const res = await apiGetApplicationAddQARanking(params);
      if (res.code == "000000") {
        this.chartDataSeven = res.data || [];
        this.$nextTick(() => {
          this.initChartSeven();
        });
      }
      this.loading6 = false;
    },
    // 问答审核数排名
    async getApplicationDialogueReviewRanking(deptId) {
      this.loading7 = true;
      const params = {
        applicationId: this.data?.applicationId,
        startTime: this.startTime,
        endTime: this.endTime,
        deptId,
      };
      const res = await apiGetApplicationDialogueReviewRanking(params);
      if (res.code == "000000") {
        this.chartDataEight = res.data || [];
        this.$nextTick(() => {
          this.initChartEight();
        });
      }
      this.loading7 = false;
    },
    // 知识库问答添加数排名
    initChartSeven() {
      var myChart = echarts.init(this.$refs.echartSeven);
      var salvProName = this.chartDataSeven.map((i) => i.showName);
      var salvProValue = this.chartDataSeven.map((i) => i.count);
      var salvProMax = []; //背景按最大值
      for (let i = 0; i < salvProValue.length; i++) {
        salvProMax.push(salvProValue[0]);
      }
      var option = {
        backgroundColor: "#fff",
        grid: {
          left: "2%",
          right: "2%",
          bottom: "2%",
          top: "2%",
          containLabel: true,
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "none",
          },
          formatter: function (params) {
            return params[0].name + " : " + params[0].value;
          },
        },
        xAxis: {
          show: false,
          type: "value",
        },
        yAxis: [
          {
            type: "category",
            inverse: true,
            axisLabel: {
              show: true,
              textStyle: {
                color: "#828894",
              },
            },
            splitLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLine: {
              show: false,
            },
            data: salvProName,
          },
          {
            type: "category",
            inverse: true,
            axisTick: "none",
            axisLine: "none",
            show: true,
            axisLabel: {
              textStyle: {
                color: "#383D47",
                fontSize: "14",
              },
            },
            data: salvProValue,
          },
        ],
        series: [
          {
            name: "值",
            type: "bar",
            zlevel: 1,
            itemStyle: {
              normal: {
                barBorderRadius: 0,
                color: "#769CFE",
              },
            },
            barWidth: 12,
            data: salvProValue,
          },
          {
            name: "背景",
            type: "bar",
            barWidth: 12,
            barGap: "-100%",
            data: salvProMax,
            itemStyle: {
              normal: {
                color: "rgba(255,255,255,1)",
                barBorderRadius: 0,
              },
            },
          },
        ],
      };
      myChart.setOption(option);
      const that = this;
      myChart.on("click", function (params) {
        if (that.level3 == 2) return;

        that.deptId3 = that.chartDataSeven.find(
          (i) => i.showName == params.name
        )?.keyId;
        that.deptName3 = that.chartDataSeven.find(
          (i) => i.showName == params.name
        )?.showName;
        that.level3 = 2;
        that.getApplicationAddQARanking(that.deptId3);
      });
      setTimeout(() => {
        myChart.resize();
      }, 500);
    },
    // 问答审核数排名
    initChartEight() {
      var myChart = echarts.init(this.$refs.echartEight);
      var salvProName = this.chartDataEight.map((i) => i.showName);
      var salvProValue = this.chartDataEight.map((i) => i.count);
      var salvProMax = []; //背景按最大值
      for (let i = 0; i < salvProValue.length; i++) {
        salvProMax.push(salvProValue[0]);
      }
      var option = {
        backgroundColor: "#fff",
        grid: {
          left: "2%",
          right: "2%",
          bottom: "2%",
          top: "2%",
          containLabel: true,
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "none",
          },
          formatter: function (params) {
            return params[0].name + " : " + params[0].value;
          },
        },
        xAxis: {
          show: false,
          type: "value",
        },
        yAxis: [
          {
            type: "category",
            inverse: true,
            axisLabel: {
              show: true,
              textStyle: {
                color: "#828894",
              },
            },
            splitLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLine: {
              show: false,
            },
            data: salvProName,
          },
          {
            type: "category",
            inverse: true,
            axisTick: "none",
            axisLine: "none",
            show: true,
            axisLabel: {
              textStyle: {
                color: "#383D47",
                fontSize: "14",
              },
            },
            data: salvProValue,
          },
        ],
        series: [
          {
            name: "值",
            type: "bar",
            zlevel: 1,
            itemStyle: {
              normal: {
                barBorderRadius: 0,
                color: "#769CFE",
              },
            },
            barWidth: 12,
            data: salvProValue,
          },
          {
            name: "背景",
            type: "bar",
            barWidth: 12,
            barGap: "-100%",
            data: salvProMax,
            itemStyle: {
              normal: {
                color: "rgba(255,255,255,1)",
                barBorderRadius: 0,
              },
            },
          },
        ],
      };
      myChart.setOption(option);
      const that = this;
      myChart.on("click", function (params) {
        if (that.level4 == 2) return;
        that.deptId4 = that.chartDataEight.find(
          (i) => i.showName == params.name
        )?.keyId;
        that.deptName4 = that.chartDataEight.find(
          (i) => i.showName == params.name
        )?.showName;
        if (that.deptId4) {
          that.level4 = 2;
          that.getApplicationDialogueReviewRanking(that.deptId4);
        }
      });
      setTimeout(() => {
        myChart.resize();
      }, 500);
    },
    tabsClick(tabs) {
      this.tabs = tabs;
      this.initChartData();
    },
    // 导出
    exportFile(type, deptId) {
      switch (type) {
        case 0:
          this.getApplicationLikeStepRankingExport(type, deptId);
          break;
        case 1:
          this.getApplicationLikeStepRankingExport(type, deptId);
          break;
        case 2:
          // 知识库问答添加数排名
          this.getApplicationAddQARankingExport(deptId);
          break;
        case 3:
          // 问答审核数排名
          this.getApplicationDialogueReviewRankingExport(deptId);
          break;

        default:
          break;
      }
    },
    // 点赞点踩数据导出
    async getApplicationLikeStepRankingExport(feedbackType, deptId) {
      const params = {
        applicationId: this.data?.applicationId,
        startTime: this.startTime,
        endTime: this.endTime,
        feedbackType, // 1-赞 0-踩
        deptId, //不传值返回的部门维度列表   传值返回部门下用户维度列表
      };
      axios({
        method: "POST",
        url: `${process.env.VUE_APP_BASE_API}/applicationAnalysis/getApplicationLikeStepRankingExport`,
        headers: {
          Authorization:
            "Bearer " + sessionStorage.getItem("manageAccessToken"),
        },
        responseType: "blob",
        data: params,
      })
        .then((res) => {
          let data = res.data;
          console.log(data);
          const url = window.URL.createObjectURL(new Blob([res.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute(
            "download",
            `${
              feedbackType == 1
                ? this.$t("likeRanking")
                : this.$t("rankingByNumberOfClicks")
            }` + ".xls"
          );
          document.body.appendChild(link);
          link.click();
        })
        .catch((error) => {
          console.log("config-res-error:", error);
        });
    },
    // 知识库问答添加数排名
    async getApplicationAddQARankingExport(deptId) {
      const params = {
        applicationId: this.data?.applicationId,
        startTime: this.startTime,
        endTime: this.endTime,
        deptId,
      };
      axios({
        method: "POST",
        url: `${process.env.VUE_APP_BASE_API}/applicationAnalysis/getApplicationAddQARankingExport`,
        headers: {
          Authorization:
            "Bearer " + sessionStorage.getItem("manageAccessToken"),
        },
        responseType: "blob",
        data: params,
      })
        .then((res) => {
          let data = res.data;
          console.log(data);
          const url = window.URL.createObjectURL(new Blob([res.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute(
            "download",
            `${this.$t("rankingOfQ&AAdditionsInTheKnowledgeBase")}` + ".xls"
          );
          document.body.appendChild(link);
          link.click();
        })
        .catch((error) => {
          console.log("config-res-error:", error);
        });
    },
    // 问答审核数排名
    async getApplicationDialogueReviewRankingExport(deptId) {
      const params = {
        applicationId: this.data?.applicationId,
        startTime: this.startTime,
        endTime: this.endTime,
        deptId,
      };
      axios({
        method: "POST",
        url: `${process.env.VUE_APP_BASE_API}/applicationAnalysis/getApplicationDialogueReviewRankingExport`,
        headers: {
          Authorization:
            "Bearer " + sessionStorage.getItem("manageAccessToken"),
        },
        responseType: "blob",
        data: params,
      })
        .then((res) => {
          let data = res.data;
          console.log(data);
          const url = window.URL.createObjectURL(new Blob([res.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute(
            "download",
            `${this.$t("rankingOfQ&AReviewNumbers")}` + ".xls"
          );
          document.body.appendChild(link);
          link.click();
        })
        .catch((error) => {
          console.log("config-res-error:", error);
        });
    },
  },
};
</script>

<style lang="scss" scoped>
.analysis {
  ::v-deep .el-button {
    margin-left: 8px;
    border-radius: 4px;
  }

  ::v-deep .el-date-editor .el-range-separator {
    width: 7%;
  }

  &-head {
    margin: 40px 0 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    &-tabs {
      display: flex;
      align-items: center;

      li {
        padding: 0 12px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        background: #f2f5fa;
        border-radius: 4px;
        font-family: MiSans, MiSans;
        font-size: 16px;
        color: #828894;
        cursor: pointer;
        margin-right: 16px;
      }
      .selected {
        background: #d1e0fe;
        color: #1c50fd;
      }
    }

    &-update {
      height: 22px;
      font-family: MiSans, MiSans;
      font-size: 16px;
      color: #828894;
      text-align: center;
    }
  }

  &-content {
    &-title {
      display: flex;
      align-items: center;
      margin-top: 16px;
      height: 40px;
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 18px;
      color: #383d47;
      line-height: 40px;
    }

    &-statistics {
      margin-top: 8px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      li {
        width: 19.3%;
        padding: 18px 20px 14px 24px;
        height: 108px;
        background: #ffffff;
        border-radius: 4px;
        border: 1px solid #e1e4eb;
      }

      &-top {
        display: flex;
        align-items: center;

        .title-icon {
          width: 24px;
          height: 24px;
        }

        .label {
          margin: 0 4px 0 8px;
          height: 20px;
          font-family: MiSans, MiSans;
          font-size: 16px;
          color: #383d47;
          line-height: 20px;
        }

        .question-icon {
          width: 14px;
          height: 14px;
          cursor: pointer;
        }
      }

      &-bottom {
        margin-top: 12px;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .num {
          display: flex;
          height: 41px;
          font-family: DINPro, DINPro;
          font-weight: 500;
          font-size: 32px;
          color: #383d47;
        }

        .unit {
          display: flex;
          align-items: center;
          margin-left: 4px;
          font-family: MiSans, MiSans;
          font-size: 16px;
          color: #828894;

          .percent {
            margin-left: 8px;
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 16px;
            color: #1abc7c;
            line-height: 20px;
          }

          .percent-red {
            color: #dc2544;
          }
        }
      }
    }

    &-show {
      display: flex;
      justify-content: space-between;
      height: 486px;

      .question-icon {
        margin-left: 4px;
        width: 20px;
        height: 20px;
        cursor: pointer;
      }

      &-ranking {
        margin-top: 8px;
        width: 100%;
        height: calc(100% - 64px);
        overflow: auto;
        padding: 16px 16px 0;
        border: 1px solid #e1e4eb;
        border-radius: 4px;

        &-item {
          display: flex;
          align-items: center;
          padding: 14px 0;
          border-bottom: 1px solid rgba(0, 0, 0, 0.12);

          &:last-child {
            border-bottom: none;
          }

          .top {
            width: 24px;
            height: 24px;
            line-height: 24px;
            text-align: center;
            background: url("~@/assets/images/top.png") no-repeat;
            background-size: 100% 100%;
            color: #fff;
          }

          .top1 {
            background: url("~@/assets/images/top-one.png") no-repeat;
            background-size: 100% 100%;
          }

          .top2 {
            background: url("~@/assets/images/top-two.png") no-repeat;
            background-size: 100% 100%;
          }

          .top3 {
            background: url("~@/assets/images/top-three.png") no-repeat;
            background-size: 100% 100%;
          }

          .txt {
            margin-left: 12px;
            flex: 1;
            height: 20px;
            font-family: MiSans, MiSans;
            font-size: 16px;
            color: #383d47;
            line-height: 20px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .txt1,
          .txt2,
          .txt3 {
            font-weight: 600;
          }

          .number {
            width: 78px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 18px;
            color: #828894;
            line-height: 24px;
            text-align: center;
            font-style: normal;
          }

          .time {
            margin-left: 8px;
            width: 160px;
            font-size: 16px;
          }

          .number1 {
            color: #dc2544;
            font-weight: 600;
          }

          .number2 {
            color: #ff912a;
            font-weight: 600;
          }

          .number3 {
            color: #1c50fd;
            font-weight: 600;
          }
        }

        .item1,
        .item2,
        .item3 {
          padding: 18px 0;
        }
      }
      .no-data {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        &-img {
          width: 240px;
          height: 164px;
        }
        &-text {
          margin-top: 16px;
          font-size: 18px;
          color: #383d47;
        }
      }

      &-chart {
        flex: 1;
        height: 100%;
        overflow: auto;
        margin-top: 8px;

        .chart-top {
          display: flex;
          align-items: center;

          .echart {
            margin-bottom: 16px;
          }
        }

        .chart-bottom {
          display: flex;
          align-items: center;
        }

        .echart {
          width: 49.5%;
          height: 322px;
          border: 1px solid #e1e4eb;
          border-radius: 4px;
          margin-right: 16px;
          padding: 16px;
        }
      }
    }

    &-answer {
      width: 100%;
      height: 486px;
      overflow: auto;
      .chart-top {
        display: flex;
        align-items: center;
        margin-bottom: 16px;
      }
      .chart-item {
        width: 49.5%;
        height: 322px;
        border: 1px solid #e1e4eb;
        border-radius: 4px;
        margin-right: 16px;
        padding: 16px;
        .title {
          display: flex;
          align-items: center;
          .name {
            height: 24px;
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 18px;
            color: #383d47;
            line-height: 24px;
          }
          .question-icon {
            width: 15px;
            height: 15px;
            margin: 0 16px 0 6px;
          }
          .unit {
            height: 20px;
            font-family: PingFangSC, PingFang SC;
            font-weight: 400;
            font-size: 14px;
            color: #828894;
            line-height: 20px;
          }
          .export-icon {
            width: 15px;
            height: 15px;
            margin-right: 2px;
          }
          ::v-deep .el-button {
            span {
              display: flex;
              align-items: center;
              justify-content: center;
            }
          }
        }
        .echart {
          height: calc(100% - 40px);
          width: 100%;
        }
        .no-data {
          display: flex;
          align-items: center;
          justify-content: center;
        }
        .level-two {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin: 16px 0;
          .dept-name {
            height: 24px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 18px;
            color: #383d47;
            line-height: 24px;
          }
          .come-back {
            display: flex;
            align-items: center;
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 16px;
            color: #1c50fd;
            line-height: 20px;
            margin-left: 2px;
            cursor: pointer;
          }
        }
      }

      .chart-bottom {
        display: flex;
        align-items: center;
      }
    }
  }
}
</style>