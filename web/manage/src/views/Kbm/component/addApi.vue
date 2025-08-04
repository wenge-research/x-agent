<template>
  <div class="addApi">
    <div class="header">
      <div class="frist">
        <div class="img-box" :class="{ active: isNext }">
          <img
            :src="
              isNext
                ? require('@/assets/images/get_2.png')
                : require('@/assets/images/get_1.png')
            "
            alt=""
          />
        </div>
        <p>数据获取</p>
      </div>
      <div class="setp-line"></div>
      <div class="second">
        <div class="img-box" :class="{ active: isNext }">
          <img
            :src="
              isNext
                ? require('@/assets/images/database-2-fill-2.png')
                : require('@/assets/images/database-2-fill-1.png')
            "
            alt=""
          />
        </div>
        <p>数据存储</p>
      </div>
    </div>
    <div v-show="!isNext">
      <div class="api-box">
        <div class="title">
          <div class="left">服务API</div>
          <div class="right">
            <img src="../../../assets/images/jianquan.png" alt="" />
            <span class="text-one">鉴权</span>
            <span class="text-two">无需鉴权</span>
          </div>
        </div>
        <div class="request-box">
          <div class="request-type">
            <el-select v-model="requestType">
              <el-option
                v-for="item in requestOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </div>
          <div class="request-url">
            <el-input
              v-model="requestUrl"
              placeholder="请输入API地址"
            ></el-input>
          </div>
        </div>
      </div>

      <div class="request-info">
        <div class="title">请求<span @click="remUrlVisible = true">通过JSON写入</span></div>
        <div class="request-config">
          <div
            class="tab-item"
            v-for="(item, index) in tabs"
            :key="index"
            :class="{ active: activeIndex === index }"
            @click="toggleActive(index)"
          >
            {{ item }}
          </div>
        </div>
        <!-- 内容区域 -->
        <div class="content-area">
          <el-table
            class="no-bottom-border"
            :data="currentTableData"
            style="width: 100%"
          >
            <el-table-column label="参数名" width="260">
              <template #default="{ row, column, $index }">
                <el-input
                  v-model="row.name"
                  placeholder="请输入参数名"
                  @change="handleInputChange(row, 'name', $event)"
                />
              </template>
            </el-table-column>

            <el-table-column label="描述" width="360">
              <template #default="{ row }">
                <el-input
                  v-model="row.desc"
                  placeholder="请输入描述"
                  @change="handleInputChange(row, 'desc', $event)"
                />
              </template>
            </el-table-column>

            <el-table-column label="设定值">
              <template #default="{ row }">
                <el-input
                  v-model="row.value"
                  placeholder="请输入设定值"
                  @change="handleInputChange(row, 'value', $event)"
                  max-width="240"
                />
              </template>
            </el-table-column>

            <el-table-column fixed="right" label="操作" width="80">
              <template #default="scope">
                <el-button @click="deleteRow(scope)" type="text">
                  <i
                    class="el-icon-delete"
                    style="color: #1d2129; font-size: 16px"
                  ></i>
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="add-btn" @click="addRow">
            <i
              class="el-icon-plus"
              style="color: #1d2129; font-size: 14px; margin-right: 14px"
            ></i>
            <span>新增参数</span>
          </div>
        </div>
      </div>

      <!-- josn数据 -->
      <div class="josn-data">
        <div class="title">
          <div class="left">
            <h3>响应</h3>
            <div class="icon-box">
              <i class="el-icon-success" style="color: #009a29"></i>
              <span>通过</span>
            </div>
            <span class="status">请求状态码</span>
            <span class="code">200</span>
            <span class="status">内容类型</span>
            <span class="code">application/json; charset=utf-8</span>
          </div>
          <div class="right" @click="runApi">
            <i class="el-icon-s-operation" style="color: #1747e5"></i>
            <span>调试</span>
          </div>
        </div>
        <div class="josn-box">
          <div class="top-name">
            <span>JSON</span>
            <i
              class="el-icon-arrow-up"
              style="color: #1d2129; font-size: 12px"
            ></i>
          </div>
          <div class="json-container">
           <vue-json-pretty
              :data="jsonData"
              :print-width="120"
              class="json-content"
            ></vue-json-pretty>
          </div>
        </div>
      </div>

      <!-- 定时任务 -->
      <div class="task-timer">
        <div class="timer-header">
          <div class="name">定时任务设置</div>
          <div class="info">
            <span>{{
              formInline.cycleType === "day"
                ? "每天"
                : formInline.cycleType === "week"
                ? "每周"
                : formInline.cycleType === "month"
                ? "每月"
                : ""
            }}</span
            ><span>
              <span>{{
                formInline.cycleType === "week"
                  ? getWeekDayLabel(formInline.weekDay)
                  : formInline.cycleType === "month"
                  ? `${formInline.monthDay}号`
                  : ""
              }}</span>
            </span>
            <span class="times">{{ formInline.time }}</span>
            <span>自动执行</span>
            <span class="times">{{ formInline.times }}</span>
            <span>次请求，每次请求间隔</span>
            <span class="times">{{ formInline.date }}</span> 秒
          </div>
        </div>

        <div class="task-timer-form">
          <el-form
            :inline="true"
            :model="formInline"
            class="demo-form-inline"
            label-position="top"
          >
            <el-form-item label="周期" label-width="140px">
              <div style="display: flex">
                <!-- 周期类型选择 -->
                <el-select
                  v-model="formInline.cycleType"
                  style="width: 140px; margin-right: 8px"
                  @change="handleCycleTypeChange"
                >
                  <el-option label="每天" value="day"></el-option>
                  <el-option label="每周" value="week"></el-option>
                  <el-option label="每月" value="month"></el-option>
                </el-select>
                <!-- 每周选项（单选） -->
                <el-select
                  v-if="formInline.cycleType === 'week'"
                  v-model="formInline.weekDay"
                  placeholder="选择星期"
                  style="width: 120px; margin-right: 8px"
                >
                  <el-option
                    v-for="day in weekDayOptions"
                    :key="day.value"
                    :label="day.label"
                    :value="day.value"
                  ></el-option>
                </el-select>

                <!-- 每月选项（单选） -->
                <el-select
                  v-if="formInline.cycleType === 'month'"
                  v-model="formInline.monthDay"
                  placeholder="选择日期"
                  style="width: 120px; margin-right: 8px"
                >
                  <el-option
                    v-for="day in monthDayOptions"
                    :key="day"
                    :label="`${day}号`"
                    :value="day"
                  ></el-option>
                </el-select>

                <!-- 时间选择 -->
                <el-time-select
                  v-model="formInline.time"
                  :picker-options="{
                    start: '00:00',
                    step: '01:00',
                    end: '24:00',
                  }"
                  placeholder="选择时间"
                  style="width: 120px; margin-right: 8px"
                ></el-time-select>
              </div>
            </el-form-item>
            <el-form-item label="请求次数">
              <el-input-number
                v-model="formInline.times"
                controls-position="right"
                :min="0"
                :max="100"
                style="width: 150px"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="请求间隔">
              <el-input-number
                v-model="formInline.date"
                controls-position="right"
                :min="0"
                :max="60"
                style="width: 150px"
              ></el-input-number>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="last-box">
        <div class="nextStep" @click="nextStep">下一步</div>
      </div>
    </div>

    <div v-show="isNext" class="next-page">
      <apiDataStorage
        @close-drawer="nextStep"
        :response-data="responseData"
        :nextPageParms="nextPageParms"
        @close-closeDrawer="closeDrawer"
      />
    </div>
	<el-dialog title="导入json" :visible.sync="remUrlVisible" width="800px" :modal-append-to-body="false"
	  :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
	 <div>
		 <codemirror v-model="optVal" :options="options" class="codemirror-editor">
		 </codemirror>
	 </div>
	  <span slot="footer" class="dialog-footer">
	    <el-button  @click="remUrlVisible = false">{{ $t('cancel') }}</el-button>
	    <el-button  type="primary" @click="handleRemUrl">{{ $t('confirm') }}</el-button>
	  </span>
	</el-dialog>
  </div>
</template>

<script>
import "vue-json-pretty/lib/styles.css";
import VueJsonPretty from "vue-json-pretty";
import "vue-json-pretty/lib/styles.css";
import apiDataStorage from "./apiDataStorage.vue";
import { runApiKnowledgeApi } from "./../../../api/Kbm";
export default {
  components: {
    VueJsonPretty,
    apiDataStorage,
  },

  props: {
    knowledgeId: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
		options: {
		  line: true,
		  theme: "rubyblue", // 主题
		  tabSize: 8, // 制表符的宽度
		  indentUnit: 2, // 一个块应该缩进多少个空格（无论这在编辑语言中意味着什么）。默认值为 2。
		  firstLineNumber: 1, // 从哪个数字开始计算行数。默认值为 1。
		  readOnly: false, // 只读
		  autorefresh: true,
		  smartIndent: true, // 上下文缩进
		  lineNumbers: true, // 是否显示行号
		  styleActiveLine: true, // 高亮选中行
		  viewportMargin: Infinity, //处理高度自适应时搭配使用
		  showCursorWhenSelecting: true, // 当选择处于活动状态时是否应绘制游标
		  mode: "javascript",
		},
		optVal:'',
      nextPageParms: {}, //下一页参数
      responseData: {}, //响应后的数据
      resultId: null,
      knowledgeApiId: null,
	  remUrlVisible:false,
      requestUrl: "", //请求地址
      requestType: "0", //请求类型
      requestOptions: [
        {
          value: "0",
          label: "GET",
        },
        {
          value: "1",
          label: "POST",
        },
      ],
      tabs: ["Query", "Header", "Path", "Body"],
      activeIndex: 0, // 默认激活第一个选项卡
      //表格数据
      tableDataCollection: {
        0: [{ name: "", desc: "", value: "" }],
        1: [{ name: "", desc: "", value: "" }],
        2: [{ name: "", desc: "", value: "" }],
        3: [{ name: "", desc: "", value: "" }],
      },

      jsonData: {},
      formInline: {
        cycleType: "day", // 默认每天
        times: "", // 时间次数
        date: "", // 时间间隔
        weekDay: null, // 选择的星期（单选）
        monthDay: null, // 选择的日期（单选）
      },
      weekDayOptions: [
        { label: "周一", value: "MON" },
        { label: "周二", value: "TUE" },
        { label: "周三", value: "WED" },
        { label: "周四", value: "THU" },
        { label: "周五", value: "FRI" },
        { label: "周六", value: "SAT" },
        { label: "周日", value: "SUN" },
      ],
      monthDayOptions: Array.from({ length: 31 }, (_, i) => i + 1),
      isNext: false, //下一步
    };
  },
  computed: {
    // 计算属性获取当前tab的数据
    currentTableData() {
      return this.tableDataCollection[this.activeIndex] || [];
    },
  },
  methods: {
    getWeekDayLabel(value) {
      const weekDayMap = {
        MON: "星期一",
        TUE: "星期二",
        WED: "星期三",
        THU: "星期四",
        FRI: "星期五",
        SAT: "星期六",
        SUN: "星期日",
      };
      return weekDayMap[value] || "";
    },
    handleCycleTypeChange() {
      // 切换周期类型时清空之前的选择
      this.formInline.weekDay = null;
      this.formInline.monthDay = null;
    },
    // 获取最终的cron表达式
    getCronExpression() {
      const timeParts = this.formInline.time?.split(":") || ["0", "0"];
      const hour = timeParts[0] || "0";
      const minute = timeParts[1] || "0";

      switch (this.formInline.cycleType) {
        case "day":
          return `0 ${minute} ${hour} * * ?`;
        case "week":
          return `0 ${minute} ${hour} ? * ${this.formInline.weekDay || "*"}`;
        case "month":
          return `0 ${minute} ${hour} ${this.formInline.monthDay || "*"} * ?`;
        default:
          return "";
      }
    },
    //tabs切换
    toggleActive(index) {
      this.activeIndex = index;
    },
    handleInputChange(row, prop, value) {
      // 这里可以添加输入验证逻辑
      console.log("修改项:", prop, "新值:", value);
    },
    // 删除
    deleteRow(row) {
      this.tableDataCollection[this.activeIndex].splice(row.$index, 1);
    },
    //添加参数
    addRow() {
      const newIndex = this.tableDataCollection[this.activeIndex].length;
      this.$set(this.tableDataCollection[this.activeIndex], newIndex, {
        name: "",
        desc: "",
        value: "",
      });
	  console.log(this.tableDataCollection)
    },
    //调试API
    async runApi() {
      let scheduledTaskDescStr="";
      let scheduledTaskData="";
      // 验证必填字段
      console.log(this.formInline);
      
      if (this.formInline.time) {
        // this.$message.error("请选择时间");
        // 处理时间格式 (HH:mm)
        const [hour = "0", minute = "0"] = this.formInline.time.split(":");
        const displayTime = `${hour.padStart(2, "0")}:${minute.padStart(
          2,
          "0"
        )}`;

        // 构建周期描述
        let cycleDesc = "";
        switch (this.formInline.cycleType) {
          case "day":
            cycleDesc = `每天${displayTime}`;
            break;

          case "week":
            if (!this.formInline.weekDay) {
              this.$message.error("请选择星期");
              return;
            }
            // 从weekDayOptions中查找对应的中文标签
            const weekDay = this.weekDayOptions.find(
              (day) => day.value === this.formInline.weekDay
            );
            cycleDesc = `每周${weekDay?.label || ""}${displayTime}`;
            break;

          case "month":
            if (!this.formInline.monthDay) {
              this.$message.error("请选择日期");
              return;
            }
            cycleDesc = `每月${this.formInline.monthDay}号${displayTime}`;
            break;

          default:
            this.$message.error("未知的周期类型");
            return;
        }

        // 拼接最终结果
        scheduledTaskDescStr = `${cycleDesc} 请求${this.formInline.times}次 间隔${this.formInline.date}S`;
        scheduledTaskData = this.getCronExpression();
      }else{
        scheduledTaskDescStr = "";
        scheduledTaskData = "0 00 00 * * ?";
      }

      function objectToQueryString(arr) {
        if (!Array.isArray(arr) || arr.length === 0) return "";

        return arr
          .map((item) => {
            const key = item.name;
            const value = item.value ? String(item.value) : "";
            return `${key}=${value}`;
          })
          .join("&");
      }
      let myUrl =
        this.requestType == "0"
          ? this.requestUrl +
            "?" +
            objectToQueryString(this.tableDataCollection[0])
          : this.requestUrl;
      console.log(myUrl);

      const config = {
        url: this.requestUrl,
        Accept: "*/*",
        method: this.requestType == "0" ? "GET" : "POST",
        headers: [],
        contentType: "application/json",
        requestBody: this.tableDataCollection[0],
        responseType: false,
      };
      const parms = {
        requestType: this.requestType,
        scheduledTask: scheduledTaskData,
        scheduledTaskDesc: scheduledTaskDescStr,
        name: this.requestUrl,
        apiAddress: myUrl,
        storageType: "",
        knowledgeId: this.knowledgeId,
        authFlag: "0",
        config: config,
        responseConfigDesc: "[]",
      };
      this.nextPageParms = parms;
      // return
      try {
        const res = await runApiKnowledgeApi(parms);
        // const res = await addOrUpdatKnowledgeApi(parms);
        if (res.code === "000000") {
          this.resultId = res.data.id;
          this.knowledgeApiId = res.data.knowledgeApiId;
          this.jsonData = res.data;
          this.responseData = res.data;
        }
        // this.responseData = res;
        // console.log('this.responseData',this.responseData)
      } catch (error) {
        console.log("error", error);
      }

      // const res = await runApiKnowledgeApi();
    },
    //知识库api接口
    async getRunApiKnowledge() {
      try {
        const res = await runApiKnowledgeApi({ id: this.resultId });
        if ((res.code = "000000")) {
          this.jsonData = res.data.data;
        }
      } catch (error) {
        console.log("error", error);
      }
    },
    //下一步
    nextStep() {
      // 验证是否已经调试出json
      if (Object.keys(this.jsonData).length === 0) {
        this.$message.error("请先进行调试");
        return;
      }
      this.isNext = !this.isNext;
    },
    closeDrawer() {
      this.$emit("close-drawer");
      this.isNext=false
      this.jsonData={}
      this.tableDataCollection={
        0: [{ name: "", desc: "", value: "" }],
        1: [{ name: "", desc: "", value: "" }],
        2: [{ name: "", desc: "", value: "" }],
        3: [{ name: "", desc: "", value: "" }],
      }
      this.requestType=0
      this.requestUrl=""
    },
	handleRemUrl(){
		let optObj = Object.keys(JSON.parse(this.optVal))
		let optValues = Object.values(JSON.parse(this.optVal))
		// var obj = {}
		// var objList = []
		// optObj.forEach((item,index)=>{
		// 	obj.name = item
		// 	obj.value = optValues[index]
		// 	objList.push(obj)
		// })
		for(var i=0;i<optObj.length;i++){
			var obj = {
				name: optObj[i],
				 desc: "", 
				 value:optValues[i]
			}
			// obj.name = optObj[i]
			// obj.value = optValues[i]
			// obj.desc = ""
			this.$set(this.tableDataCollection[0],i, obj);
			// objList.push(obj)
		}
		
		
		console.log('objList',this.tableDataCollection)
		this.remUrlVisible = false
	}
  },
};
</script>

<style scoped lang="scss">
.addApi {
  padding: 0 32px;

  .header {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 16px;
    color: #1d2129;

    .frist {
      display: flex;
      align-items: center;

      .img-box {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 48px;
        height: 48px;
        background: #1747e5;
        border-radius: 50%;
        margin-right: 12px;
        transition: background 0.3s ease;

        &.active {
          background: #d0dffd;
        }

        img {
          width: 24px;
          height: 24px;
        }
      }
    }

    .second {
      display: flex;
      align-items: center;

      .img-box {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 48px;
        height: 48px;
        background: #f2f3f5;
        border-radius: 50%;
        margin-right: 12px;
        transition: background 0.3s ease;

        &.active {
          background: #1747e5;
        }

        img {
          width: 24px;
          height: 24px;
        }
      }
    }

    .setp-line {
      width: 182px;
      height: 4px;
      background: #e7e7e7;
      border-radius: 2px;
      margin-right: 14px;
      margin-left: 12px;
    }
  }

  .api-box {
    margin-top: 20px;
    margin-bottom: 20px;

    .title {
      display: flex;
      justify-content: space-between;
      margin-bottom: 14px;

      .left {
        font-family: MiSans, MiSans;
        font-size: 18px;
        color: #1d2129;
      }

      .right {
        display: flex;
        align-items: center;

        img {
          width: 14px;
          height: 16px;
          margin-right: 8px;
        }

        .text-one {
          font-weight: 400;
          font-size: 14px;
          color: #1d2129;
          margin-right: 8px;
        }

        .text-two {
          font-weight: 400;
          font-size: 14px;
          color: #c9cdd4;
        }
      }
    }

    .request-box {
      display: flex;

      .request-type {
        width: 112px;
        height: 40px;
        margin-right: 8px;
      }

      .request-url {
        width: 776px;
        height: 40px;
      }
    }
  }

  .request-info {
    .title {
      font-weight: 500;
      font-size: 18px;
      color: #1d2129;
      margin-bottom: 14px;
	  display: flex;
	  justify-content: space-between;
	  span{
		  color:#1747e5;
		  cursor: pointer;
	  }
    }

    .request-config {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 32px;
      box-sizing: border-box;
      background: #f7f8fa;
      border-radius: 4px;

      .tab-item {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 25%;
        height: 28px;
        cursor: pointer;
      }

      .active {
        background: #ffffff;
        box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
        border-radius: 2px;
      }
    }

    .add-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 124px;
      height: 40px;
      border-radius: 4px;
      border: 1px solid #e7e7e7;
      margin-top: 6px;
      cursor: pointer;
    }
  }

  .josn-data {
    margin-top: 24px;

    .title {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .left {
        display: flex;
        align-items: center;

        h3 {
          font-size: 18px;
          color: #1d2129;
          margin-right: 12px;
        }

        .icon-box {
          margin-right: 12px;
          width: 60px;
          height: 24px;
          background: #e8ffe9;
          border-radius: 2px;
          display: flex;
          justify-content: center;
          align-items: center;

          span {
            font-weight: 400;
            font-size: 12px;
            color: #009a29;
            margin-left: 4px;
          }
        }

        .status {
          font-weight: 400;
          font-size: 12px;
          color: #86909c;
          margin-right: 12px;
        }

        .code {
          font-weight: 400;
          font-size: 12px;
          color: #1d2129;
          margin-right: 20px;
        }
      }

      .right {
        width: 76px;
        height: 32px;
        border-radius: 4px;
        border: 1px solid #1747e5;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;

        span {
          font-weight: 400;
          font-size: 14px;
          color: #1747e5;
          margin-left: 6px;
        }
      }
    }

    .josn-box {
      margin-top: 12px;

      .top-name {
        background: #f2f3f5;
        border-radius: 3px 3px 0px 0px;
        height: 47px;
        display: flex;
        align-items: center;

        span {
          margin-left: 15px;
          font-size: 14px;
          color: #1d2129;
          margin-right: 4px;
        }
      }

      .json-container {
        width: 100%;
        height: 200px;
        /* 固定高度 */
        border: 1px solid #ebeef5;
        border-radius: 4px;
        overflow-y: auto;
        /* 垂直滚动 */
        padding: 15px;
        background-color: #f2f3f5;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      }
    }
  }

  .task-timer {
    margin-top: 20px;

    .timer-header {
      display: flex;
      align-items: center;

      .name {
        font-size: 18px;
        color: #1d2129;
        margin-right: 16px;
      }

      .info {
        font-weight: 400;
        font-size: 14px;
        color: #86909c;
      }

      .times {
        box-sizing: border-box;
        padding: 2px 4px;
        font-weight: 400;
        font-size: 12px;
        color: #7e56eb;
        background: #ebddfe;
        border-radius: 2px;
      }
    }

    .task-timer-form {
      // margin-top: 6px;
    }
  }

  .last-box {
    display: flex;
    justify-content: end;
    padding-right: 24px;

    .nextStep {
      box-sizing: border-box;
      padding: 10px 20px;

      background: #1747e5;
      border-radius: 4px;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      cursor: pointer;
    }
  }

  .next-page {
    margin-top: 24px;
  }
}

/* 去除表头底部边框 */
::v-deep .el-table__header th {
  border-bottom: none !important;
}

/* 去除表格行底部边框 */
::v-deep .no-bottom-border .el-table__body td {
  border-bottom: none;
}

::v-deep .no-bottom-border .el-input__inner {
  font-weight: 500;
  font-size: 14px;
  color: #494c4f;
}

::v-deep .el-table::before {
  height: 0;
}

::v-deep .no-bottom-border .el-table__fixed-right::before {
  height: 0;
}

::v-deep .no-bottom-border .el-table .cell {
  padding-left: 0;
}

::v-deep .el-table .el-table__cell {
  padding: 6px 0 0 0;
}

::v-deep .no-bottom-border .el-form--label-top .el-form-item__label {
  padding: 0;
}
.codemirror-editor {
  width: 100%;
  height: 300px;
  /* 根据需要调整高度 */
}
::v-deep .CodeMirror pre.CodeMirror-line,
.CodeMirror pre.CodeMirror-line-like {
  padding: 6px 4px !important;
}
</style>
