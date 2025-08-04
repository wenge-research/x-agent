<template>

<div class="project-detail" style="height: 100%;" >
  <div class="operationsManagement-title">
      <div class="operationsManagement-title-left">
          <div class="operationsManagement-title-left-back" @click="backFn" >
              <iconpark-icon name="arrow-go-back-fill" size="20"  ></iconpark-icon>
          </div>
          <div class="operationsManagement-title-left-content" >
              <div class="operationsManagement-title-left-backimg">
                <img class="headImg" v-if="data.logo" :src="data.logo" />
                <img class="defaultHeadImg" v-else :src="getRandomHeadImgDefaultBgColor()" />
              </div>
              <div>{{data.applicationName}}</div>
          </div>
      </div>
      <div class="operationsManagement-title-right">
		  <el-button @click="addReport" :loading="loadingReport" icon="el-icon-document-add" >
		      <!-- <iconpark-icon name="el-icon-document-add"></iconpark-icon> -->
		      <span>导出报告</span>
		  </el-button>
          <el-button @click="selectProjectOpen" >
              <iconpark-icon name="arrow-left-right-line"></iconpark-icon>
              <span>切换应用</span>
          </el-button>
      </div>
  </div>
  <div class="project-detail-content" >
    <div class="analysis">
      
      <div class="analysis-content">
        <ul class="analysis-content-statistics">
          <!-- 访问量 先注释掉 -->
          <!-- <li>
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
                {{ sourceData.VISIT_TOTAL?.total?.toLocaleString()
                }}<span class="unit">{{ sourceData.VISIT_TOTAL?.unit }}</span>
              </div>
              <div class="unit">
                {{ $t("monthlyChange")
                }}<span
                  class="percent"
                  :class="[
                    sourceData.VISIT_TOTAL?.QOQ.includes('-')
                      ? ''
                      : 'percent-red',
                  ]"
                  ><i
                    :class="[
                      sourceData.VISIT_TOTAL?.QOQ.includes('-')
                        ? 'el-icon-caret-bottom'
                        : 'el-icon-caret-top',
                    ]"
                  ></i>
                  {{ sourceData.VISIT_TOTAL?.QOQ }}</span
                >
              </div>
            </div>
          </li> -->
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
                }}<span class="unit">{{ sourceData.USAGE_TOTAL?.unit }}</span>
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
              <div class="unit">
                环比<span class="percent"
                  ><i class="el-icon-caret-bottom"></i> {{ sourceData?.USAGE_ADD_USER_TOTAL?.QOQ }}</span
                >
              </div>
            </div>
          </li>
          <li>
            <div class="analysis-content-statistics-top">
              <img class="title-icon" src="@/assets/images/percent.png" alt="" />
              <div class="label">使用用户平均留存率</div>
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
              <div class="unit">
                环比<span class="percent"
                  ><i class="el-icon-caret-bottom"></i> {{ sourceData?.USAGE_USER_DAY_RETENTION_RATE?.QOQ }}</span
                >
              </div>
            </div>
          </li>
        </ul>

        <div>
          <div class="project-detail-content-head-sousuolan" >
            <el-row>
              <el-col :span="6">
                <div class="analysis-head-left">
                  <div @click="analysisHeadLeftFn(1)" :class="analysisHeadLeft == 1 ? 'analysis-head-left-active' : ''" >使用统计</div>
                  <div @click="analysisHeadLeftFn(2)" :class="analysisHeadLeft == 2 ? 'analysis-head-left-active' : ''" >问答质量</div>
                </div>
              </el-col>
              <el-col :span="6" :offset="2">
                <div class="analysis-head-update" style="margin-top: 10px;">
                  更新时间：{{ date || "2024-07-11" }}(每天0点更新数据）
                </div>
              </el-col>
              <el-col :span="6">
                <el-date-picker
                  v-model="time"
                  type="daterange"
                  :range-separator="$t('to')"
                  :start-placeholder="$t('startDate')"
                  :end-placeholder="$t('endDate')"
                  value-format="yyyy-MM-dd"
                >
                </el-date-picker>
              </el-col>
              <el-col :span="3">
                <el-button type="primary" @click="searchHandler">{{
                  $t("search")
                }}</el-button>
                <el-button plain @click="resetHandler">{{ $t("reset") }}</el-button>
              </el-col>
            </el-row>
          </div>
          <div class="prjectDetail-content-neirong"  >
            <div class="analysis-content-show" v-show="analysisHeadLeft == 1" >
              <div style="flex: 1" v-loading="loading">
                <div class="analysis-content-show-chart">
                  <div class="activeUser" ref="echartActive"></div>
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
                  style="height: 604px;"
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

            <div class="QAsector" v-show="analysisHeadLeft == 2" v-loading="loadingTwo">
              <div class="QAsector-one" >
                <div class="QAsector-wendazhiliang-echart-box" >
                  <div class="QAsector-wendazhiliang-echart"  ref="QAsectorOneLeftEchart">
                  </div>
                  <div class="daochuButton" >
                    <el-button type="text" @click="daochuFn(1,$t('numberOfLikesRanked'))" >导出</el-button>
                  </div>
                </div>
                <div class="QAsector-wendazhiliang-echart-box" >
                  <div class="QAsector-wendazhiliang-echart" ref="QAsectorOneRightEchart">
                  </div>
                  <div class="daochuButton" >
                    <el-button type="text" @click="daochuFn(2,$t('numberOfDislikesRanked'))" >导出</el-button>
                  </div>
                </div>
              </div>
              <div class="QAsector-two">
                <div class="QAsector-wendazhiliang-echart-box" >
                  <div class="QAsector-wendazhiliang-echart" ref="QAsectorTwoLeftEchart">
                  </div>
                  <div class="daochuButton" >
                    <el-button type="text" @click="daochuFn(3,$t('numberOfQARanking'))" >导出</el-button>
                  </div>
                </div>
                <div class="QAsector-wendazhiliang-echart-box" >
                  <div class="QAsector-wendazhiliang-echart" ref="QAsectorTwoRightEchart">
                  </div>
                  <div class="daochuButton" >
                    <el-button type="text" @click="daochuFn(4,$t('numberOfQAReviewRanking'))" >导出</el-button>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>


      </div>
    </div>
  </div>
</div>
</template>

<script>
import axios from "axios";
import echarts from "echarts";
import {
  apiQuestionChartsTop50,
  apiApplicationOverviewIndicators,
  apiGetApplicationOverviewIndicatorsByType,
  apiGetApplicationOverviewQualityByType,
  getApplicationOverviewQualityByTypeExport,
  applicationStatistics,
  getApplicationActiveUser
} from "@/api/analysis";
import html2canvas from 'html2canvas';
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
      analysisHeadLeft:1, // 1:使用统计 2:问答质量
      time: "",
      date: "",
      top50Type: "count",
      top50TypeList: [],
      echart: null,
      sourceData: {},
      chartDataActive:[],
      chartDataOne: [],
      chartDataTwo: [],
      chartDataThree: [],
      chartDataFour: [],
      loading: false,
	  loadingReport: false,
      loading2: false,
      QAsectorOneLeftEchartData: [],
      QAsectorOneRightEchartData: [],
      QAsectorTwoLeftEchartData: [],
      QAsectorTwoRightEchartData: [],
      loadingTwo: false,
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
    // 这个是获取问答质量图表的方法 
    this.initChartWendaZhiLiang();
    
  },
  methods: {
    // 切换应用
    selectProjectOpen(){
      this.$EventBus.$emit("selectProjectOpen", true);
    },
	//导出报告
	addReport(){
		  const elementList = [this.$refs.echartOne,this.$refs.echartTwo,this.$refs.echartFour,this.$refs.echartThree]; // 获取目标元素
		  const fileImages = []
		  this.loadingReport = true;
		  elementList.forEach(id => {
			  html2canvas(id).then(canvas => {
				  // 将canvas转换为图片并添加到数组中
				  fileImages.push(canvas.toDataURL('image/png')); // 或者使用canvas.toBlob()来处理二进制数据流，根据需要选择使用方法
			      if(fileImages.length==4){
					   axios({
					     method: 'post',
					     url: `${process.env.VUE_APP_BASE_API}/applicationAnalysis/applicationStatistics`,
					     headers: {
					   	Authorization: 'Bearer ' + sessionStorage.getItem('manageAccessToken'),
					     },
					     data:{
					   	  fileImages:fileImages,
					   	  applicationId:this.data.applicationId,
					   	  applicationName:this.data.applicationName,
						  startTime:this.startTime,
						  endTime:this.endTime,
					     },
					     responseType: 'blob',			  
					   })
					   .then((res) => {
					    this.loadingReport = false
					     const applicationName = this.data.applicationName+'统计分析报告.docx'
					     const url = window.URL.createObjectURL(new Blob([res.data]))
					     const link = document.createElement('a')
					     link.href = url
					     link.setAttribute('download', applicationName)
					     document.body.appendChild(link)
					     link.click()
					     
					   })
					   .catch((error) => {
					     console.log('config-res-error:', error)
					     this.loadingReport = false
					   })
				  }
			  });
		  });
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
    daochuFn(type,fileName){
      let params = {
        applicationId:this.data.applicationId,
        type:type,
        startTime: this.startTime,
        endTime: this.endTime,
      }
      getApplicationOverviewQualityByTypeExport(params).then(res=>{
        let data = res.data;
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute(
          "download",
          `${fileName}` + ".xls"
        );
        document.body.appendChild(link);
        link.click();
      })
    },
    initQAsectorOneLeftEchart() {
      
      var myChart = echarts.init(this.$refs.QAsectorOneLeftEchart);
      const xData = this.QAsectorOneLeftEchartData.map((i) => i.name);
      const yData1 = this.QAsectorOneLeftEchartData.map((i) => i.value);

      const flag = null;
      var option = {
        title: {
          text: this.$t("numberOfLikesRanked"),
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
            barWidth: 20, // 设置柱子的宽度
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
    initQAsectorOneRightEchart() {
      var myChart = echarts.init(this.$refs.QAsectorOneRightEchart);
      const xData = this.QAsectorOneRightEchartData.map((i) => i.name);
      const yData1 = this.QAsectorOneRightEchartData.map((i) => i.value);
      
      const flag = null;
      var option = {
        title: {
          text: this.$t("numberOfDislikesRanked"),
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
            barWidth: 20, // 设置柱子的宽度
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
    initQAsectorTwoLeftEchart() {
      var myChart = echarts.init(this.$refs.QAsectorTwoLeftEchart);
      const xData = this.QAsectorTwoLeftEchartData.map((i) => i.name);
      const yData1 = this.QAsectorTwoLeftEchartData.map((i) => i.value);
      
      const flag = null;
      var option = {
        title: {
          text: this.$t("numberOfQARanking"),
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
            barWidth: 20, // 设置柱子的宽度
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
    initQAsectorTwoRightEchart() {
      var myChart = echarts.init(this.$refs.QAsectorTwoRightEchart);
      const xData = this.QAsectorTwoRightEchartData.map((i) => i.name);
      const yData1 = this.QAsectorTwoRightEchartData.map((i) => i.value);
      
      const flag = null;
      var option = {
        title: {
          text: this.$t("numberOfQAReviewRanking"),
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
            barWidth: 20, // 设置柱子的宽度
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
    analysisHeadLeftFn(type){
      this.analysisHeadLeft = type;
      // 重新渲染图表
      if(type == 1){
        this.chongxinxuanranEchersOne();
      }else if(type == 2){
        this.chongxinxuanranEchersTwo();
      }
    },

    backFn(){    
        this.$EventBus.$emit("switchingModule", {status:true});
    },
    // 创建问答质量图表
    async initChartWendaZhiLiang() {
      this.loadingTwo = true;
      await this.getWendaZhiLiang(1)
      await this.getWendaZhiLiang(2)
      await this.getWendaZhiLiang(3)
      await this.getWendaZhiLiang(4)
      this.loadingTwo = false;
    },
    async initChartData() {
      this.loading = true;
      await this.getApplicationActiveUserApi()
      await this.getApplicationOverviewIndicatorsByType(1);
      await this.getApplicationOverviewIndicatorsByType(2);
      await this.getApplicationOverviewIndicatorsByType(3);
      await this.getApplicationOverviewIndicatorsByType(4);
      this.loading = false;
    },
    searchHandler() {
      this.initChartData();
      this.initChartWendaZhiLiang();
    },
    resetHandler() {
      this.time = "";
      this.initChartData();
      this.initChartWendaZhiLiang();
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

    // 问答质量图表
    async getWendaZhiLiang(type) {
      // 接口拿不到applicationId参数会报错
      if(!this.data?.applicationId) return;
      const params = {
        applicationId: this.data?.applicationId,
        type,
        startTime: this.startTime,
        endTime: this.endTime,
      };
      let res = await apiGetApplicationOverviewQualityByType(params);
      
       let echerData = res.data?.map(item=>{
        return {
          name:item.dept_name,
          value:item.count
        }
       })
      
      
      if (res.code == "000000") {
        switch (type) {
          case 1:
            this.QAsectorOneLeftEchartData = echerData || [];
            break;
          case 2:
            this.QAsectorOneRightEchartData = echerData || [];
            break;
          case 3:
            this.QAsectorTwoLeftEchartData = echerData || [];
            break;
          case 4:
            this.QAsectorTwoRightEchartData = echerData || [];
            break;

          default:
            break;
        }

        this.$nextTick(() => {
            if(type == 1){
              this.initQAsectorOneLeftEchart();
            }else if(type == 2){
              this.initQAsectorOneRightEchart();
            }else if(type == 3){
              this.initQAsectorTwoLeftEchart();
            }else if(type == 4){
              this.initQAsectorTwoRightEchart();
            }
        });
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
            console.log(this.chartDataTwo,'柱状图数据');
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
    // 活跃用户数
    async getApplicationActiveUserApi(){
      const params ={
        applicationId: this.data?.applicationId,
        startTime: this.startTime,
        endTime: this.endTime,
      }
      const res=await getApplicationActiveUser(params)
      if(res.code=="000000"){
        console.log(res,"活跃人口");
        this.chartDataActive=res.data || []
        this.$nextTick(() => {
          this.initChartActive();
        });
      }
    },
    // 使用统计 重新渲染图表
    chongxinxuanranEchersOne(){
      this.$nextTick(() => {
        this.initChartOne();
        this.initChartTwo();
        this.initChartThree();
        this.initChartFour();
      });
    },
    // 问答质量 重新渲染图表
    chongxinxuanranEchersTwo(){
      this.$nextTick(() => {
          this.initQAsectorOneLeftEchart();
          this.initQAsectorOneRightEchart();
          this.initQAsectorTwoLeftEchart();
          this.initQAsectorTwoRightEchart();
      });
    },
    selectChange() {
      this.questionChartsTop50();
    },
    initChartActive() {
      var myChart = echarts.init(this.$refs.echartActive);
      const xData = this.chartDataActive.map((i) => i.date);
      const yData1 = this.chartDataActive.map((i) => i.activeUserCount);
      // const yData2 = this.chartDataThree.map((i) => i.showCountPc);
      var option = {
        title: {
          text: '活跃用户数',
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
    initChartOne() {
      var myChart = echarts.init(this.$refs.echartOne);
      const xData = this.chartDataOne.map((i) => i.endTime);
      const yData1 = this.chartDataOne.map((i) => i.showCount);
      const yData2 = this.chartDataOne.map((i) => i.showCountPc);
      const flag = yData1?.every((item) => item == 0);
      var option = {
        title: {
          text: '使用量',
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
          text: '使用用户数',
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
          }
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
          text: '使用用户平均留存率',
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
          text: '新增使用用户数',
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

.project-detail{
    .project-detail-content{
        min-height: calc(100% - 79px);
        // overflow: hidden;
        padding: 10px 25px;
        box-sizing: border-box;
    }
    .operationsManagement-title{
        padding: 0px 25px;
        height: 79px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 1px solid #efefef;
        .operationsManagement-title-left{
            display: flex;
            align-items: center;
            .operationsManagement-title-left-back{
                cursor: pointer;
                user-select: none;
                margin-right: 10px;
            }
            .operationsManagement-title-left-content{
                display: flex;
                align-items: center;
                .operationsManagement-title-left-backimg{
                    img{
                      // width: 30px;
                      height: 30px;
                      margin-right: 12px;
                      border-radius: 2px;
                    }
                    .headImg {
                      object-fit: cover;
                    }
                    .defaultHeadImg {
                      object-fit: cover;
                    }
                }
            }
        }
    }
    
    .project-detail-content{
      .project-detail-content-head-sousuolan{
        margin: 15px 0px;
      }  
      .prjectDetail-content-neirong{
        height: calc(100vh - 304px);

        .QAsector{
          height: 100%;
          display: flex;
          flex-direction: column;
          
          .QAsector-one,.QAsector-two{
            height: 50%;
            box-sizing: border-box;
            display: flex;
            justify-content: space-between;
            
            .QAsector-wendazhiliang-echart-box{
              position: relative;
              border: 1px solid #e1e4eb;
              box-sizing: border-box;
              margin-bottom: 10px;
              .QAsector-wendazhiliang-echart{
                width: 800px;
                height: 309px;
                padding: 16px;
              }
              .daochuButton{
                position: absolute;
                top: 10px;
                right: 10px;
              }
            }
          }


        }
      }

      .analysis{
        .analysis-head-left{
          display: flex;
          color: #828894;
          font-size: 20px;
          margin-top: 10px;
          div{
            cursor: pointer;
            user-select: none;
            margin-right: 10px;
          }
          .analysis-head-left-active{
            color: #603ECA;
          }
        }
      }
    }
}




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
        width: 24%;
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
        height: calc(100% - 0px);
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

        .activeUser{
          width: calc(100% - 16px);
          height: 322px;
          border: 1px solid #e1e4eb;
          border-radius: 4px;
          padding: 16px;
          margin-bottom: 16px;
          margin-right: 16px;
        }
      }
    }
  }
}
</style>