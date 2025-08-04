<template>
  <div class="analysis">
    <div class="analysis-head">
      <div>
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
    <div class="analysis-content">
      <div class="analysis-content-title">{{ $t("overviewIndicator") }}</div>
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
                  sourceData?.VISIT_TOTAL?.QOQ.includes('-')
                    ? ''
                    : 'percent-red',
                ]"
                ><i
                  :class="[
                    sourceData?.VISIT_TOTAL?.QOQ.includes('-')
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
                  sourceData?.USAGE_TOTAL?.QOQ.includes('-')
                    ? ''
                    : 'percent-red',
                ]"
                ><i
                  :class="[
                    sourceData?.USAGE_TOTAL?.QOQ.includes('-')
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
                  sourceData?.USAGE_USER_TOTAL?.QOQ.includes('-')
                    ? ''
                    : 'percent-red',
                ]"
                ><i
                  :class="[
                    sourceData?.USAGE_USER_TOTAL?.QOQ.includes('-')
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
                ><i class="el-icon-caret-bottom"></i> {{ sourceData?.USAGE_ADD_USER_TOTAL.QOQ }}</span
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
                ><i class="el-icon-caret-bottom"></i> {{ sourceData?.USAGE_USER_DAY_RETENTION_RATE.QOQ }}</span
              >
            </div> -->
          </div>
        </li>
      </ul>
      <div class="analysis-content-show">
        <div style="flex: 1" v-loading="loading">
          <div class="analysis-content-title">{{ $t("detailedMetrics") }}</div>
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
            <div class="no-data-text">{{$t('noData')}}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from "echarts";
import {
  apiQuestionChartsTop50,
  apiApplicationOverviewIndicators,
  apiGetApplicationOverviewIndicatorsByType,
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
      loading: false,
      loading2: false,
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
      await this.getApplicationOverviewIndicatorsByType(1);
      await this.getApplicationOverviewIndicatorsByType(2);
      await this.getApplicationOverviewIndicatorsByType(3);
      await this.getApplicationOverviewIndicatorsByType(4);
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
    margin-top: 5px;
    display: flex;
    align-items: center;
    justify-content: space-between;

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
        height: calc(100% - 64px);
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
  }
}
</style>