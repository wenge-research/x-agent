<template>
  <div class="matter-view">
    <div class="header">
      <div class="title">{{$t('matterManagement')}}</div>
    </div>
    <div class="main">
      <div class="form-box">
        <div class="form">
          <el-select
            clearable
            v-model="statusSearch"
            size="small"
            :placeholder="$t('selectProgress')"
          >
            <el-option :label="$t('unapproved')" value="0"> </el-option>
            <el-option :label="$t('alreadyApproved')" value="1"> </el-option>
          </el-select>
          <div style="margin-left: 10px"></div>
          <el-select
            clearable
            v-model="matterId"
            size="small"
            :placeholder="$t('selectMatterType')"
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
          <el-input
            v-model="nameCardPhone"
            size="small"
            :placeholder="$t('enterNameIdPhone')"
          >
            <i slot="suffix" class="el-input__icon el-icon-search" />
          </el-input>
          <div style="margin-left: 10px"></div>
          <el-date-picker
            v-model="times"
            size="small"
            type="datetimerange"
            :range-separator="$t('to')"
            :start-placeholder="$t('startDate')"
            :end-placeholder="$t('endDate')"
          >
          </el-date-picker>
          <div style="margin-left: 10px"></div>
          <el-button
            type="primary"
            size="small"
            @click="getMatterGuideInfoListData"
            >{{$t('search')}}</el-button
          >
        </div>
        <div class="option">
          <el-button
            type="primary"
            icon="el-icon-download"
            size="small"
            @click="exportApiFun"
            >{{ $t('exportDate') }}</el-button
          >
        </div>
      </div>

      <div class="table-box">
        <el-table
          v-loading="tableLoading"
          border
          stripe
          :data="tableData"
          height="100%"
        >
          <el-table-column
            prop="matterName"
            :label="$t('matterType')"
            width=""
          ></el-table-column>
          <el-table-column
            prop="peopleName"
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
            prop="createTime"
            :label="$t('submissionTime')"
            width=""
          ></el-table-column>
          <el-table-column
            prop="dealUser"
            :label="$t('processingPersonnel')"
            width=""
          ></el-table-column>
          <el-table-column
            prop="dealTime"
            :label="$t('processingTime')"
            width=""
          ></el-table-column>
          <el-table-column fixed="right" :label="$t('action')" width="230">
            <template slot-scope="scope">
              <div class="btns">
                <el-button type="text" @click="viewDetail(scope.row)"
                  >{{$t('details')}}</el-button
                >
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
          :page-sizes="[20, 50, 100, 150, 200]"
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
            {{$t('submissionTime')}} <span>{{ detailInfo.createTime }}</span>
          </div>
        </div>
        <div class="content">
          <div
            class="forms"
            v-for="item in detailInfo.forms"
            :key="item.basicInfo"
          >
            <div class="title">{{ item.basicInfo }}</div>
            <template v-for="cItem in item.configs">
              <template v-if="item.type === 'form'">
                <div class="item" :key="cItem.filedId">
                  <div class="label">{{ cItem.label }}</div>
                  <div class="value">
                    <el-image
                      :src="cItem.value"
                      v-if="['idCardFrontPic', 'idCardBackPic'].includes(cItem.field)||
                        cItem?.value?.includes('.png') ||
                        cItem?.value?.includes('.jpg')"
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
                    <template v-else>
                      {{ cItem.value }}
                    </template>
                  </div>
                </div>
              </template>

              <template v-if="item.type === 'addForm'">
                <div
                  class="option"
                  v-for="(option, optionIndex) in JSON.parse(cItem.value)"
                  :key="optionIndex"
                >
                  <div
                    class="option-work"
                    v-if="['工作经历'].includes(cItem.field)"
                  >
                    <div class="employmentPeriod">
                      <!-- {{ option.employmentPeriod[0] }}{{$t('to')}}{{
                        option.employmentPeriod[1]
                      }} -->
                      {{ option.employmentPeriod }}
                    </div>
                    <div class="info">
                      <div class="companyName">{{ option.companyName }}</div>
                      <div class="occupation">{{ option.occupation }}</div>
                      <div class="jobContent">{{ option.jobContent }}</div>
                    </div>
                  </div>

                  <div
                    class="option-skill"
                    v-if="['职业技能'].includes(cItem.field)"
                  >
                    <div class="technicalTitle">
                      {{ option.technicalTitle }}
                    </div>
                    <div class="level">{{ option.level }}</div>
                    <div class="manageTime">
                      <!-- {{ option.manageTime[0] }}{{$t('to')}}{{ option.manageTime[1] }} -->
                      {{ option.manageTime }}
                    </div>
                  </div>

                  <div
                    class="option-base"
                    v-if="['外语特长', '计算机等级证书'].includes(cItem.field)"
                  >
                    <div class="language">{{ option.language }}</div>
                    <div class="level">{{ option.level }}</div>
                  </div>
                </div>
              </template>
            </template>
          </div>

          <div class="handle">
            <el-radio-group :disabled="detailStatus === 1" v-model="status">
              <el-radio :label="0">{{$t('pending')}}</el-radio>
              <el-radio :label="1">{{$t('processed')}}</el-radio>
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
          <el-button :loading="submitLoading" @click="detailVisible = false"
            >{{$t('cancel')}}</el-button
          >
          <el-button
            :loading="submitLoading"
            type="primary"
            @click="handleSubmit"
            :disabled="detailStatus === 1"
            >{{$t('submit')}}</el-button
          >
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import {
  getMatterGuideInfoList,
  getMatterGuideInfoDetail,
  matterGuideInfoUpdate,
  getMatterGuideList,
  exportApi,
} from '@/api/index.js'
import { TASK_TYPE } from './content/index'
import axios from 'axios'
import moment from 'moment'
export default {
  data() {
    return {
      TASK_TYPE,
      submitLoading: false,
      detailVisible: false,
      nameCardPhone: null,
      matterId: null,
      matterIdData: [],
      status: '',
      tableLoading: false,
      tableData: [],
      pageNo: 1,
      pageSize: 50,
      total: 0,
      detailLoading: true,
      detailInfo: {},
      statusSearch: null,
      remark: '',
      infoId: '',
      detailStatus: '',
      times: [],
      timer: null
    }
  },
  created() {
    this.getMatterGuideInfoListData();
    this.getMatterGuideListFun()
    const _this = this;
    this.timer = setInterval(() => {
      console.log("this", _this)
        _this.getMatterGuideInfoListData();
      }, 5 * 60 * 1000);
  },
  beforeDestroy() {
    // 组件销毁时清除定时器
      clearInterval(this.timer);
  },
  methods: {
    formatDate() {
      const date = new Date()
      const year = date.getFullYear()
      let month = date.getMonth() + 1
      let day = date.getDate()
      let hours = date.getHours()
      let minutes = date.getMinutes()

      // 如果月份或日期小于10，则在前面添加0
      month = month < 10 ? '0' + month : month
      day = day < 10 ? '0' + day : day
      hours = hours < 10 ? '0' + hours : hours
      minutes = minutes < 10 ? '0' + minutes : minutes

      return year + month + day + '_' + hours + minutes
    },
    async viewDetail(row) {
      this.detailVisible = true
      this.infoId = row.infoId
      this.status = row.status
      this.detailStatus = row.status
      this.getMatterGuideInfoDetail()
    },
    async getMatterGuideInfoDetail() {
      this.detailLoading = true
      const { data } = await getMatterGuideInfoDetail({
        infoId: this.infoId,
      })
      this.remark = data.remark
      this.detailLoading = false
      this.detailInfo = data
    },
    async getMatterGuideListFun() {
      const { data } = await getMatterGuideList({})
      this.matterIdData = data
      let uniqueMatterIdsMap = new Map();
      this.matterIdData?.forEach(item => {
        if (!uniqueMatterIdsMap.has(item.matterId)) {
            uniqueMatterIdsMap.set(item.matterId, item);
        }
      });
      // 从 Map 中提取不重复的数据项
      this.matterIdData = Array.from(uniqueMatterIdsMap.values());
    },
    async handleSubmit() {
      this.submitLoading = true
      try {
        const res = await matterGuideInfoUpdate({
          status: this.status,
          infoId: this.infoId,
          remark: this.remark,
        })
        if (res.code === '000000') {
          this.detailVisible = false
          this.getMatterGuideInfoListData()
          this.$message({
            message: res.msg,
            type: 'success',
          })
        } else {
          this.$message({
            message: res.msg,
            type: 'error',
          })
        }
        this.submitLoading = false
      } catch (error) {
        this.$message({
          message: error,
          type: 'error',
        })
        this.submitLoading = false
      }
    },
    handleCurrentChange(n) {
      this.pageNo = n
      this.getMatterGuideInfoListData()
    },
    handleSizeChange() {
      this.pageSize = n
      this.getMatterGuideInfoListData()
    },

    async getMatterGuideInfoListData() {
      this.tableLoading = true;
      const { data } = await getMatterGuideInfoList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        status: this.statusSearch,
        nameCardPhone: this.nameCardPhone,
        matterId: this.matterId,
        startTime:
          this.times && this.times.length
            ? moment(this.times[0]).format('YYYY-MM-DD HH:mm:ss')
            : '',
        endTime:
          this.times && this.times.length
            ? moment(this.times[1]).format('YYYY-MM-DD HH:mm:ss')
            : '',
      })
      this.tableData = data.records
      this.total = data.total
      this.tableLoading = false
    },
    async exportApiFun() {
      axios({
        method: 'POST',
        url: `${process.env.VUE_APP_BASE_API}/matterGuideInfo/export`,
        // url: `https://localhost/smart-agent-api-demo/matterGuideInfo/export`,
        headers: {
          Authorization: 'Bearer ' + sessionStorage.getItem('manageAccessToken'),
        },
        responseType: 'blob',
        data: {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          status: this.statusSearch,
          nameCardPhone: this.nameCardPhone,
          matterId: this.matterId,
          startTime:
            this.times && this.times.length
              ? moment(this.times[0]).format('YYYY-MM-DD HH:mm:ss')
              : '',
          endTime:
            this.times && this.times.length
              ? moment(this.times[1]).format('YYYY-MM-DD HH:mm:ss')
              : '',
        },
      })
        .then((res) => {
          let data = res.data
          console.log(data)
          const url = window.URL.createObjectURL(new Blob([res.data]))
          const link = document.createElement('a')
          link.href = url
          link.setAttribute('download', this.formatDate() + '.xlsx')
          document.body.appendChild(link)
          link.click()
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
          console.log('config-res-error:', error)
        })
    },
  },
}
</script>

<style lang="scss" scoped>
.matter-view {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .header {
    width: 100%;
    height: 64px;
    background: #f3f5fb;
    display: flex;
    align-items: center;
    padding: 0 10px;
    flex-shrink: 0;

    .title {
      font-size: 18px;
      font-weight: bold;
    }
  }

  .main {
    box-sizing: border-box;
    flex-grow: 1;
    width: 100%;
    height: 100%;
    padding: 20px;
    display: flex;
    flex-direction: column;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    background: #ffffff;
    overflow: hidden;

    .form-box {
      width: 100%;
      display: flex;
      justify-content: space-between;

      .form {
        display: flex;

        .el-input {
          width: 334px;
        }
      }
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

      .btns {
        display: flex;
        justify-content: center;
      }

      .el-button {
        padding: 0 5px;
      }

      .btns {
        display: flex;
        justify-content: center;
      }
    }
  }
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
        font-weight: 500;
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
        margin-top: 20px;
        border-bottom: 1px solid #e7e9ef;
        padding-bottom: 20px;

        .employmentPeriod {
          font-weight: 500;
          font-size: 16px;
          color: #383d47;
          margin-right: 20px;
          flex: 1;
        }

        .info {
          flex: 1;

          .companyName {
            font-weight: 500;
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
        margin-top: 20px;
        border-bottom: 1px solid #e7e9ef;
        padding-bottom: 20px;

        .technicalTitle {
          font-weight: 500;
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
        font-weight: 500;
        font-size: 14px;
        color: #383d47;
        margin-top: 20px;
        border-bottom: 1px solid #e7e9ef;
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
</style>
<style scoped>
/deep/ .el-drawer__body {
  flex: 1;
  overflow: auto;
  position: relative;
}
</style>
