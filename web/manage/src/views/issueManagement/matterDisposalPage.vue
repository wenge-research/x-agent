<template>
  <div class="matterDisposalPage">
    <div class="matterDisposalPage-head">{{ $t("disposalOfMatters") }}</div>
    <div class="matterDisposalPage-content">
      <div class="matter-view">
        <div class="main">
          <div class="form-box">
            <div class="form">
              <div class="form-label">{{ $t("progress") }}</div>
              <el-select
                clearable
                v-model="statusSearch"
                :placeholder="$t('selectProgress')"
              >
                <el-option :label="$t('unapproved')" value="0"> </el-option>
                <el-option :label="$t('alreadyApproved')" value="1">
                </el-option>
              </el-select>
              <div style="margin-left: 10px"></div>
              <div class="form-label">{{ $t("bindingApplications") }}</div>
              <el-select
                v-model="applicationId"
                @change="applicationIdChange"
                filterable
              >
                <el-option
                  v-for="item in applicationIdList"
                  :key="item.applicationId"
                  :label="item.applicationName"
                  :value="item.applicationId"
                >
                </el-option>
              </el-select>
              <div style="margin-left: 10px"></div>
              <div class="form-label">{{ $t("matterType") }}</div>
              <el-select
                v-model="matterId"
                :placeholder="$t('selectMatterType')"
                @change="matterChange"
                filterable
              >
                <el-option
                  v-for="item in matterIdData"
                  :key="item.matterId"
                  :label="item.matterName"
                  :value="item.matterId"
                >
                </el-option>
              </el-select>
              <div style="margin-left: 10px"></div>

              <!-- <el-input
            v-model="nameCardPhone"
            :placeholder="$t('enterNameIdPhone')"
          >
            <i slot="suffix" class="el-input__icon el-icon-search" />
          </el-input>
          <div style="margin-left: 10px"></div> -->

              <el-popover
                popper-class="search-popover"
                v-model="showPopover"
                ref="myPopover"
                placement="bottom"
                width="1200"
                trigger="click"
              >
                <el-badge
                  slot="reference"
                  :value="badgeNum"
                  :hidden="isHidden"
                  class="item"
                >
                  <div class="more-search">
                    <iconpark-icon
                      name="equalizer-line"
                      color="#1c50fd"
                      style="margin-right: 8px"
                    ></iconpark-icon>
                    {{ $t("accurateSearch") }}
                  </div>
                </el-badge>
                <div class="search-bg"></div>
                <!-- 动态检索条件 -->
                <div class="search-conditions">
                  <div
                    v-for="(item, index) in searchConditionList"
                    :key="index"
                    class="search-conditions-items"
                  >
                    <div class="filedName">{{ item.filedName }}</div>
                    <div style="flex: 1">
                      <el-input
                        v-if="['textarea', 'input'].includes(item.formType)"
                        v-model="item.searchValue"
                        :placeholder="item.placeholder"
                      />
                      <el-select
                        v-if="['select', 'radio'].includes(item.formType)"
                        style="width: 100%"
                        :placeholder="item.placeholder"
                        v-model="item.searchValue"
                      >
                        <el-option
                          v-for="(subItem, subIndex) in item.optionList"
                          :key="subIndex"
                        />
                      </el-select>
                      <el-date-picker
                        v-if="['datePicker'].includes(item.formType)"
                        v-model="item.searchValue"
                        style="width: 100%"
                        type="daterange"
                        value-format="yyyy-MM-dd"
                        :range-separator="$t('to')"
                        :start-placeholder="$t('startDate')"
                        :end-placeholder="$t('endDate')"
                      >
                      </el-date-picker>
                    </div>
                  </div>
                </div>

                <div v-if="searchConditionList.length" class="search-footer">
                  <div class="clear" @click="clearHandler">
                    <iconpark-icon
                      name="eraser-line"
                      color="#828894"
                      size="16"
                      style="margin-right: 4px"
                    ></iconpark-icon
                    >{{ $t("clearConditions") }}
                  </div>
                  <el-button
                    type="primary"
                    style="width: 72px; height: 40px; border-radius: 8px"
                    @click="searchHandler"
                    >{{ $t("search") }}</el-button
                  >
                </div>
                <div v-if="!searchConditionList.length" class="no-data">
                  {{ $t("noSearchCriteriaDataAvailableAtTheMoment") }}
                </div>
              </el-popover>

              <el-button type="primary" @click="searchPageHandler">{{
                $t("search")
              }}</el-button>
              <el-button plain @click="resetHandler">{{
                $t("reset")
              }}</el-button>
            </div>
            <div class="option">
              <el-button
                type="primary"
                @click="exportApiFun"
                ><i class="el-icon-download" style="margin-right: 8px;"></i>{{ $t("exportDate") }}</el-button
              >
            </div>
          </div>

          <div class="table-box">
            <el-table
              v-loading="tableLoading"
              :data="tableData"
              tooltip-effect="dark"
              style="width: 100%"
              class="table"
              max-height="660"
            >
              <el-table-column
                type="index"
                width="60"
                :label="$t('sequenceNumber')"
              ></el-table-column>
              <el-table-column
                prop="applicationName"
                :label="$t('bindingApplications')"
                width=""
              ></el-table-column>
              <el-table-column
                prop="matterName"
                :label="$t('matterType')"
                width=""
              ></el-table-column>
              <el-table-column prop="status" :label="$t('progress')" width="">
                <template slot-scope="scope">
                  <div class="file-type">
                    <span
                      :style="{
                        backgroundColor: TASK_TYPE[scope.row.status].color,
                      }"
                    ></span>
                    {{ TASK_TYPE[scope.row.status].label }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                v-for="(head, headIndex) in tableHeadList"
                :key="headIndex"
                :prop="head.filedCode"
                :label="head.filedName"
                width=""
              ></el-table-column>
              <!-- <el-table-column
            prop="userName"
            :label="$t('name')"
            width=""
          ></el-table-column>
          <el-table-column
            prop="idCard"
            :label="$t('idNumber')"
            width=""
          ></el-table-column>
          <el-table-column
            prop="phone"
            :label="$t('contactNumber')"
            width=""
          ></el-table-column> -->
              <el-table-column
                prop="createTime"
                :label="$t('submissionTime')"
              ></el-table-column>
              <el-table-column :label="$t('action')">
                <template slot-scope="scope">
                  <div class="btns">
                    <el-button type="text" @click="viewDetail(scope.row)">{{
                      $t("details")
                    }}</el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="pagination">
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :page-size="pageSize"
              :current-page="pageNo"
              :page-sizes="[10, 20, 30, 40, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
            >
            </el-pagination>
          </div>
        </div>

        <!-- 表单详情 -->
        <el-drawer
          :visible.sync="detailVisible"
          size="560px"
          :with-header="false"
          append-to-body
          :modal-append-to-body="false"
          :close-on-click-modal="false"
          :show-close="false"
          :close-on-press-escape="false"
          :wrapperClosable="false"
        >
          <div class="detail-view" v-loading="detailLoading">
            <div class="head">
              <div class="title">{{ detailInfo.title }}</div>
              <div class="time">
                {{ $t("submissionTime") }}
                <span>{{ detailInfo.createTime }}</span>
              </div>
            </div>
            <div class="content">
              <div
                class="forms"
                v-for="item in detailInfo.forms"
                :key="item.basicInfo"
              >
                <div class="title">{{ item.basicInfo }}</div>
                <div v-for="(cItem, cIndex) in item.configs" :key="cIndex">
                  <template v-if="item.type === 'form'">
                    <div class="item" :key="cItem.filedId">
                      <div class="label">{{ cItem.label }}</div>
                      <div class="value">
                        <el-image
                          v-if="
                            ['idCardFrontPic', 'idCardBackPic'].includes(
                              cItem.field
                            ) ||
                            cItem?.value?.includes('.png') ||
                            cItem?.value?.includes('.jpg')
                          "
                          :src="cItem.value"
                          :preview-src-list="[cItem.value]"
                        ></el-image>
                        <div v-else-if="['medicalInput'].includes(cItem.field)">
                          {{
                            cItem.value &&
                            JSON.parse(cItem.value)
                              .map((item) => item.medicalInput)
                              .toString()
                          }}
                        </div>
                        <div v-else-if="cItem.field == 'data_time'">
                          {{ handleDate(cItem.value) }}
                        </div>
                        <template v-else>
                          {{ cItem.value }}
                        </template>
                      </div>
                    </div>
                  </template>

                  <div
                    v-if="
                      [
                        'addFormGzjl',
                        'addFormZyjn',
                        'addFormWytc',
                        'addFormJsjdjzs',
                      ].includes(item.type)
                    "
                  >
                    <div class="workExperience-label">{{ cItem.label }}</div>
                    <div
                      class="row-right"
                      v-for="(option, optionIndex) in cItem.value &&
                      JSON.parse(cItem.value)"
                      :key="optionIndex"
                    >
                      <!-- 工作经历 -->
                      <div
                        class="option-work workExperience"
                        v-if="['addFormGzjl'].includes(cItem.type)"
                      >
                        <!-- <div class="workExperience-label">
                      {{ cItem.label }}
                    </div> -->
                        <div class="workExperience-item">
                          <div class="employmentPeriod">
                            {{ option.employmentPeriod[0] }}{{ $t("to")
                            }}{{ option.employmentPeriod[1] }}
                            <!-- {{ option.employmentPeriod }} -->
                          </div>
                          <div class="info">
                            <div class="companyName">
                              {{ option.companyName }}
                            </div>
                            <div class="occupation">
                              {{ option.occupation }}
                            </div>
                            <div class="jobContent">
                              {{ option.jobContent }}
                            </div>
                          </div>
                        </div>
                      </div>
                      <!-- 职业技能 -->
                      <div
                        class="option-skill workExperience"
                        v-if="['addFormZyjn'].includes(cItem.type)"
                      >
                        <!-- <div class="workExperience-label">{{ cItem.label }}</div> -->
                        <div class="workExperience-item">
                          <div class="technicalTitle">
                            {{ option.technicalTitle }}
                          </div>
                          <div class="level">{{ option.level }}</div>
                          <div class="manageTime">
                            {{ option.manageTime[0] }}{{ $t("to")
                            }}{{ option.manageTime[1] }}
                            <!-- {{ option.manageTime }} -->
                          </div>
                        </div>
                      </div>
                      <!-- '外语特长', '计算机等级证书' -->
                      <div
                        class="option-base workExperience"
                        v-if="
                          ['addFormWytc', 'addFormJsjdjzs'].includes(cItem.type)
                        "
                      >
                        <!-- <div class="workExperience-label">{{ cItem.label }}</div> -->
                        <div class="workExperience-item">
                          <div class="language">{{ option.language }}</div>
                          <div class="level">{{ option.level }}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="handle">
                <el-radio-group :disabled="detailStatus === 1" v-model="status">
                  <el-radio :label="0">{{ $t("pending") }}</el-radio>
                  <el-radio :label="1">{{ $t("processed") }}</el-radio>
                </el-radio-group>
                <div style="margin-top: 10px"></div>
                <el-input
                  :disabled="detailStatus === 1"
                  type="textarea"
                  :autosize="{ minRows: 6, maxRows: 4 }"
                  :placeholder="$t('fillRemarks')"
                  v-model="remark"
                >
                </el-input>
              </div>
            </div>
            <div class="footer">
              <el-button
                :loading="submitLoading"
                @click="detailVisible = false"
                >{{ $t("cancel") }}</el-button
              >
              <el-button
                :loading="submitLoading"
                type="primary"
                @click="handleSubmit"
                :disabled="detailStatus === 1"
                >{{ $t("submit") }}</el-button
              >
            </div>
          </div>
        </el-drawer>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getMatterGuideInfoList,
  apiGetDetailNew,
  matterGuideInfoUpdate,
  getMatterGuideList,
  exportApi,
  getApplicationInfoList,
} from "@/api/index.js";
import {
  apiGetSearchFiledByMatterId,
  apiGetTableHeadFiledByMatterId,
} from "@/api/issueManagement.js";
import { TASK_TYPE } from "./content/index";
import axios from "axios";
import moment from "moment";
export default {
  data() {
    return {
      TASK_TYPE,
      submitLoading: false,
      detailVisible: false,
      // nameCardPhone: null,
      matterId: null,
      applicationId: null,
      matterIdData: [],
      status: "",
      tableLoading: true,
      tableData: [],
      pageNo: 1,
      pageSize: 10,
      total: 0,
      detailLoading: true,
      detailInfo: {},
      statusSearch: null,
      remark: "",
      infoId: "",
      detailStatus: "",
      times: [],
      badgeNum: 0,
      tableHeadList: [],
      applicationIdList: [], // 应用数据源
      searchConditionList: [], // 动态检索条件数据源
      searchList: [], // 动态检索条件
      showPopover: false,
    };
  },
  computed: {
    isHidden() {
      return this.badgeNum ? false : true;
    },
  },
  async created() {
    // 应用
    await this.getApplicationInfoList();
    // 事项
    await this.getMatterGuideListFun();
    // 检索条件
    await this.getSearchFiledByMatterId();
    // 表头
    await this.getTableHeadFiledByMatterId();
    // 列表
    // this.getMatterGuideInfoListData();
  },
  methods: {
    searchPageHandler() {
      this.handleCurrentChange(1);
    },
    // 清空精确检索条件
    clearHandler() {
      this.searchConditionList.forEach((i) => {
        if (i.searchValue) {
          i.searchValue = "";
        }
      });
    },
    // 精确检索
    searchHandler() {
      this.showPopover = false;
      this.badgeNum = 0;
      this.badgeNum = this.searchConditionList.filter(
        (i) => i.searchValue
      )?.length;
      this.pageNo = 1;
      this.getMatterGuideInfoListData();
    },
    // 应用change
    applicationIdChange() {
      this.getMatterGuideListFun();
    },
    // 事项change
    async matterChange() {
      this.pageNo = 1;
      await this.getSearchFiledByMatterId();
      await this.getTableHeadFiledByMatterId();
      await this.getMatterGuideInfoListData();
    },
    // 应用列表
    async getApplicationInfoList() {
      const { data } = await getApplicationInfoList({
        pageNo: 1,
        pageSize: 100,
      });
      this.applicationIdList = data?.records || [];
      this.applicationId = this.applicationIdList.length
        ? this.applicationIdList[0]?.applicationId
        : "";
    },
    // 获取检索条件
    async getSearchFiledByMatterId() {
      const { data } = await apiGetSearchFiledByMatterId(this.matterId);
      this.searchConditionList = data || [];
      this.searchConditionList = this.searchConditionList.filter(
        (i) =>
          ![
            "checkbox",
            "file",
            "image",
            "addForm",
            "addFormGzjl",
            "addFormZyjn",
            "addFormWytc",
            "addFormJsjdjzs",
          ].includes(i.formType)
      );
      this.badgeNum = 0;
    },
    // 获取表头
    async getTableHeadFiledByMatterId() {
      const { data } = await apiGetTableHeadFiledByMatterId(this.matterId);
      this.tableHeadList = data || [];
      console.log("tableHeadList", this.tableHeadList);
    },
    handleDate(data) {
      const date = data ? JSON.parse(data) : ["", ""];
      return `${date[0]}${this.$t("to")}${date[1]}`;
    },
    formatDate() {
      const date = new Date();
      const year = date.getFullYear();
      let month = date.getMonth() + 1;
      let day = date.getDate();
      let hours = date.getHours();
      let minutes = date.getMinutes();

      // 如果月份或日期小于10，则在前面添加0
      month = month < 10 ? "0" + month : month;
      day = day < 10 ? "0" + day : day;
      hours = hours < 10 ? "0" + hours : hours;
      minutes = minutes < 10 ? "0" + minutes : minutes;

      return year + month + day + "_" + hours + minutes;
    },
    async viewDetail(row) {
      this.detailVisible = true;
      this.infoId = row.infoId;
      this.status = row.status;
      this.detailStatus = row.status;
      this.getMatterGuideInfoDetail();
    },
    async getMatterGuideInfoDetail() {
      this.detailLoading = true;
      const { data } = await apiGetDetailNew({
        infoId: this.infoId,
      });
      this.remark = data.remark;
      this.detailLoading = false;
      this.detailInfo = data;
    },
    // 事项
    async getMatterGuideListFun() {
      const { data } = await getMatterGuideList({
        applicationId: this.applicationId,
      });
      this.matterIdData = data || [];
      this.matterId = this.matterIdData?.length
        ? this.matterIdData[0]?.matterId
        : "";
      this.$nextTick(async () => {
        this.pageNo = 1;
        await this.getSearchFiledByMatterId();
        await this.getTableHeadFiledByMatterId();
        await this.getMatterGuideInfoListData();
      });
    },
    async handleSubmit() {
      this.submitLoading = true;
      try {
        const res = await matterGuideInfoUpdate({
          status: this.status,
          infoId: this.infoId,
          remark: this.remark,
        });
        if (res.code === "000000") {
          this.detailVisible = false;
          this.getMatterGuideInfoListData();
          this.$message({
            message: res.msg,
            type: "success",
          });
        } else {
          this.$message({
            message: res.msg,
            type: "error",
          });
        }
        this.submitLoading = false;
      } catch (error) {
        this.$message({
          message: error,
          type: "error",
        });
        this.submitLoading = false;
      }
    },
    handleCurrentChange(page) {
      this.pageNo = page;
      this.getMatterGuideInfoListData();
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.getMatterGuideInfoListData();
    },
    // 列表
    async getMatterGuideInfoListData() {
      this.tableLoading = true;
      const searchList = this.searchConditionList.map((i) => {
        return {
          filedCode: i.filedCode,
          searchType: i.searchType,
          formType: i.formType,
          searchValue:
            i.searchValue && i.formType == "datePicker"
              ? i.searchValue[0]
              : i.searchValue,
          searchValue2:
            i.searchValue && i.formType == "datePicker"
              ? i.searchValue[1]
              : null,
        };
      });
      const { data } = await getMatterGuideInfoList({
        searchList,
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        status: this.statusSearch,
        // nameCardPhone: this.nameCardPhone,
        // startTime:
        //   this.times && this.times.length
        //     ? moment(this.times[0]).format("YYYY-MM-DD HH:mm:ss")
        //     : "",
        // endTime:
        //   this.times && this.times.length
        //     ? moment(this.times[1]).format("YYYY-MM-DD HH:mm:ss")
        //     : "",
        matterId: this.matterId,
        applicationId: this.applicationId,
      });
      this.tableData = data.records;
      this.total = data.total;
      if (!this.tableHeadList.length) {
        this.tableData = [];
        this.total = 0;
      }
      console.log("tableData", this.tableData);
      this.tableLoading = false;
    },
    // 重置
    async resetHandler() {
      this.statusSearch = "";
      console.log("applicationIdList", this.applicationIdList);
      this.applicationId = this.applicationIdList?.length
        ? this.applicationIdList[0]?.applicationId
        : "";
      await this.getMatterGuideListFun();
      // this.nameCardPhone = "";
      this.times = [];
      this.getMatterGuideInfoListData();
    },
    async exportApiFun() {
      const searchList = this.searchConditionList.map((i) => {
        return {
          filedCode: i.filedCode,
          searchType: i.searchType,
          formType: i.formType,
          searchValue:
            i.searchValue && i.formType == "datePicker"
              ? i.searchValue[0]
              : i.searchValue,
          searchValue2:
            i.searchValue && i.formType == "datePicker"
              ? i.searchValue[1]
              : null,
        };
      });
      axios({
        method: "POST",
        url: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export`,
        // url: `https://localhost/smart-agent-api-demo/matterGuideInfo/export`,
        headers: {
          Authorization:
            "Bearer " + sessionStorage.getItem("manageAccessToken"),
        },
        responseType: "blob",
        data: {
          searchList,
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          status: this.statusSearch,
          // nameCardPhone: this.nameCardPhone,
          matterId: this.matterId,
          startTime:
            this.times && this.times.length
              ? moment(this.times[0]).format("YYYY-MM-DD HH:mm:ss")
              : "",
          endTime:
            this.times && this.times.length
              ? moment(this.times[1]).format("YYYY-MM-DD HH:mm:ss")
              : "",
        },
      })
        .then((res) => {
          let data = res.data;
          console.log(data);
          const url = window.URL.createObjectURL(new Blob([res.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", this.formatDate() + ".xlsx");
          document.body.appendChild(link);
          link.click();
          // let fileReader = new FileReader()
          // fileReader.onload = function () {
          //   try {
          //     if (res.data == 300) {
          //       // 说明是普通对象数据，后台转换失败
          //       // proxy.MSG.error(res.message);
          //       return
          //     } else {
          //       // 解析成对象失败，说明是正常的文件流
          //       let url = window.URL.createObjectURL(new Blob([data]))
          //       let link = document.createElement('a')
          //       link.style.display = 'none'
          //       link.href = url
          //       link.target = '_blank'
          //       link.setAttribute('download', '事项管理.xlsx')
          //       document.body.appendChild(link)
          //       link.click()
          //       window.URL.revokeObjectURL(url)
          //       document.body.removeChild(link)
          //     }
          //   } catch (err) {
          //     console.log(err)
          //   }
          // }
          // fileReader.readAsText(data)
        })
        .catch((error) => {
          console.log("config-res-error:", error);
        });
    },
  },
};
</script>
<style lang="scss">
.search-popover {
  position: relative;
}
.search-popover .search-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 79px;
  background: linear-gradient(
    180deg,
    rgba(43, 88, 213, 0.1) 0%,
    rgba(43, 88, 213, 0) 100%
  );
}
.search-popover .el-date-editor .el-range-separator {
  padding: 0 !important;
  width: 8% !important;
}
.search-popover {
  .popper__arrow {
    &::after {
      border-bottom-color: rgba(43, 88, 213, 0.1) !important;
    }
  }
}
</style>
<style lang="scss" scoped>
.matterDisposalPage {
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 16px 0px 0px 16px;
  padding: 32px;
  &-head {
    height: 40px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #383d47;
    line-height: 40px;
    margin-bottom: 24px;
  }
  &-content {
    height: calc(100% - 60px);
    width: 100%;
  }
}
.workExperience {
  display: flex;
  flex-direction: column;

  &-label {
    flex: 1;
    margin-bottom: 12px;
  }

  &-item {
    display: flex;
  }
}

.matter-view {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .main {
    box-sizing: border-box;
    flex-grow: 1;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    border-radius: 4px;
    background: #ffffff;
    overflow: hidden;

    .form-box {
      width: 100%;
      display: flex;
      justify-content: space-between;

      .form {
        display: flex;

        &-label {
          margin-right: 20px;
          height: 40px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 16px;
          color: #828894;
          line-height: 40px;
        }

        .el-input {
          width: 334px;
        }
      }
    }

    .el-button {
      border-radius: 4px;
    }

    .table-box {
      margin-top: 20px;
      flex-grow: 1;
      overflow: hidden;

      .file-type {
        display: flex;
        align-items: center;

        span {
          display: inline-block;
          width: 5px;
          height: 5px;
          border-radius: 50%;
          margin-right: 10px;
        }
      }

      .el-button {
        padding: 0 5px;
      }

      .btns {
        display: flex;
        // justify-content: center;
      }
    }
  }
}

.more-search {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 40px;
  border-radius: 4px;
  border: 1px solid #1c50fd;
  font-family: MiSans, MiSans;
  font-size: 16px;
  color: #1c50fd;
  margin-right: 24px;
  cursor: pointer;
}
.search-conditions {
  padding: 30px 36px 0;
  display: flex;
  align-content: center;
  flex-wrap: wrap;
  margin-top: 12px;
  // background: #ffffff;
  // box-shadow: 0px 4px 9px 0px rgba(0, 0, 0, 0.1);
  // background: linear-gradient( 180deg, rgba(43,88,213,0.1) 0%, rgba(43,88,213,0) 100%);

  &-items {
    // display: flex;
    width: 23%;
    margin-bottom: 24px;

    &:not(:nth-child(4n)) {
      margin-right: 24px;
    }
    .filedName {
      height: 40px;
      height: 20px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #383d47;
      line-height: 20px;
      white-space: nowrap;
      margin-bottom: 8px;
    }
  }
}
.search-footer {
  width: 100%;
  padding: 24px 36px 30px;
  display: flex;
  justify-content: space-between;
  .clear {
    height: 40px;
    font-family: MiSans, MiSans;
    font-size: 16px;
    color: #828894;
    display: flex;
    align-items: center;
    cursor: pointer;
  }
}
.no-data {
  width: 100%;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
::v-deep(.el-badge__content.is-fixed) {
  top: 2px !important;
  right: 40px !important;
}
::v-deep(
    .el-badge__content,
    .el-progress.is-exception .el-progress-bar__inner
  ) {
  background-color: #d82225;
}

.detail-view {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .head {
    box-shadow: 0 -1px 2px rgba(0, 0, 0, 0.1);
    padding: 20px 32px;
    box-sizing: border-box;
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );

    .time {
      font-weight: 400;
      font-size: 16px;
      color: #383d47;

      span {
        font-weight: 400;
        font-size: 16px;
        color: #828894;
        margin-left: 40px;
      }
    }

    .title {
      font-weight: bold;
      font-size: 20px;
      color: #383d47;
      margin-bottom: 10px;
    }
  }

  .content {
    flex-grow: 1;
    overflow-y: scroll;

    .forms {
      padding: 0 32px;
      box-sizing: border-box;
      margin-top: 20px;

      .title {
        font-weight: 600;
        font-size: 18px;
        color: #383d47;
        border-left: 3px solid #1d50fc;
        padding-left: 11px;
        margin-bottom: 20px;
      }

      .item {
        display: flex;
        justify-content: space-between;
        margin-bottom: 20px;

        .label {
          flex: 1;
          font-weight: 400;
          font-size: 16px;
          color: #383d47;
        }

        .value {
          flex: 1;
          font-weight: 400;
          font-size: 16px;
          color: #828894;
        }
      }

      .option-work {
        display: flex;
        justify-content: space-between;
        // margin-top: 20px;
        // border-bottom: 1px solid #e7e9ef;
        padding-bottom: 20px;

        .employmentPeriod {
          font-weight: 600;
          font-size: 16px;
          color: #383d47;
          margin-right: 20px;
          margin-bottom: 12px;
          flex: 1;
        }

        .info {
          flex: 1;

          .companyName {
            font-weight: 600;
            font-size: 16px;
            color: #383d47;
          }

          .occupation {
            font-weight: 400;
            font-size: 14px;
            color: #828894;
            margin: 10px 0;
          }

          .jobContent {
            font-weight: 400;
            font-size: 14px;
            color: #383d47;
          }
        }
      }

      .option-skill {
        display: flex;
        // margin-top: 20px;
        // border-bottom: 1px solid #e7e9ef;
        padding-bottom: 20px;

        .technicalTitle {
          font-weight: 600;
          font-size: 14px;
          color: #383d47;
          flex: 1;
        }

        .level {
          font-weight: 400;
          font-size: 14px;
          color: #828894;
          flex: 1;
        }

        .manageTime {
          font-weight: 400;
          font-size: 14px;
          color: #828894;
          flex: 2;
        }
      }

      .option-base {
        display: flex;
        font-weight: 600;
        font-size: 14px;
        color: #383d47;
        // margin-top: 20px;
        // border-bottom: 1px solid #e7e9ef;
        padding-bottom: 20px;

        .level {
          margin-left: 8px;
        }
      }
    }

    .handle {
      padding: 20px 32px;
      box-sizing: border-box;
    }
  }

  .footer {
    bottom: 0;
    width: 100%;
    height: 60px;
    padding: 10px;
    background-color: #fff;
    box-shadow: 0 -1px 2px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: flex-end;
  }
}
::v-deep .table {
  .is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
  }
  .el-switch .el-switch__core {
    border-radius: 12px;
    border: none;
    &:after {
      top: 2px;
    }
  }
  .has-gutter {
    th {
      font-size: 14px;
      color: #828894;
      background: #f2f5fa;
    }
  }

  tr {
    color: #383d47;
    font-size: 16px;
    font-weight: 400;
    .el-checkbox__input.is-checked .el-checkbox__inner,
    .el-checkbox__input.is-indeterminate .el-checkbox__inner {
      background: #1c50fd;
      border-color: #1c50fd;
    }
  }

  .el-button--text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #1c50fd;
  }
}
::v-deep .el-pager li {
  border: 1px solid #dddfe8;
  color: #272a31;
  background: #fff;
  .el-select-dropdown__item.selected {
    color: #1c50fd;
  }

  &:not(.disabled).active {
    background-color: #fff !important;
    border: 1px solid #1c50fd;
    color: #272a31 !important;
  }
}
::v-deep .btn-prev,
::v-deep .btn-next {
  border: 1px solid #dddfe8;
  color: #272a31;
  background: #fff;
}
</style>
<style scoped>
/deep/ .el-drawer__body {
  flex: 1;
  overflow: auto;
  position: relative;
}
</style>
  
  




