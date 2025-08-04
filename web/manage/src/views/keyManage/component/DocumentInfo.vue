<template>
  <div class="slice-info">
    <div class="header">
      <div class="header-left">
        <img class="back-icon" src="@/assets/images/arrow-go-back-fill.svg" @click="handleClose">
        <!-- <img class="type-icon" :src="require(`@/assets/images/${rowData.fileType}.svg`)" alt=""> -->
        <div class="title">{{rowData.fileName}}</div>
        <el-tooltip popper-class="document-info-tip" effect="light" class="item" placement="top">
          <img class="tip-icon" src="@/assets/images/information-line.svg">
          <template slot="content">
            <div class="document-info-wrapper">
              <!-- <div class="sentence">{{ rowData.fileName }}</div> -->
              <div class="title">{{$t('basicInformation')}}</div>
              <div class="info">
                <div class="item"><span>{{$t('fileFormat')}}</span><span class="value">{{ rowData.fileType }}</span></div>
                <div class="item"><span>{{$t('originalFileSize')}}</span><span class="value">{{ rowData.fileSize }}</span></div>
                <div class="item"><span>{{$t('uploadDate')}}</span><span class="value">{{ rowData.createTime }}</span></div>
                <div class="item"><span>{{$t('characterCount')}}</span><span class="value">{{ rowData.wordCount }}</span></div>
              </div>
              <div class="title">{{$t('importConfigInfo')}}</div>
              <div class="info">
                <div class="item"><span>{{$t('averageParagraphLength')}}</span><span>{{ rowData.averageParagraphLength ? rowData.averageParagraphLength : '--' }}</span></div>
                <div class="item"><span>{{$t('paragraphCount')}}</span><span>{{ rowData.paragraphsNum ? rowData.paragraphsNum + $t('paragraph') : '--' }}</span></div>
              </div>
            </div>
          </template>
        </el-tooltip>
      </div>
      <div class="header-right btns-box">
         <el-button
            class="icon-self"
            plain
            @click="handleOpenLink(rowData.fileUrl)"
            >
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" d="M12 3c5.392 0 9.878 3.88 10.819 9-.94 5.12-5.427 9-10.819 9-5.392 0-9.878-3.88-10.819-9C2.121 6.88 6.608 3 12 3Zm0 16a9.005 9.005 0 0 0 8.777-7 9.005 9.005 0 0 0-17.554 0A9.005 9.005 0 0 0 12 19Zm0-2.5a4.5 4.5 0 1 1 0-9 4.5 4.5 0 0 1 0 9Zm0-2a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5Z" data-follow-fill="#848587"/></svg>
              {{$t('viewSource')}}
            </el-button
          >
      </div>
    </div>
    <div class="main">
      <div class="con" v-loading="dataLoading">
        <div class="title">
          <div class="title-text">{{ $t('sliceInfo') }}</div>
          <!-- <div class="icon">
            <i class="el-icon-s-operation"></i>{{$t('sliceInfo')}}<span class="num">{{ total }}</span>
          </div> -->
          <div class="search">
            <el-input v-model="query" size="small" :placeholder="$t('inputPlaceholder')" @input="handleSearch" clearable>
              <i slot="prefix" class="el-input__icon el-icon-search" />
            </el-input>
            <div style="margin-right: 10px;"></div>
            <el-button plain size="small" icon="el-icon-plus" @click="addSlice('add')">{{$t('added')}}</el-button>
            <!-- <el-button plain size="small">更新频率</el-button> -->
          </div>
        </div>
        <div class="tabs">
          <div :class="{'tabs-item': true, 'tabs-item-active': currentTab == item.value}" v-for="item in tabList" :key="item.label" @click="handleCurrentTab(item.value)">
            {{ item.label }}
          </div>
        </div>
        <div v-if="dataList?.length" class="data">
          <div class="list">
            <div class="item" v-for="item in dataList" :key="item.id">
              <div class="message">
                {{ item.content }}
              </div>
              <div class="operating">
                <div class="info">
                  <div class="type">{{$t('typeText')}}</div>
                  <div class="divider"></div>
                  <div class="chrt">{{$t('character')}}: {{ item.content.length }}</div>
                </div>
                <div class="btns">
                  <i class="el-icon-copy-document" @click="handleCopy(item)"></i>
                  <i class="el-icon-edit" @click="setSlice(item)"></i>
                  <!-- <el-switch
                    v-model="item.status"
                    :active-value="$t('yes')"
                    :inactive-value="$t('no')"
                    active-color="#1747E5"
                    inactive-color="#CED4E0"
                    :width="24"
                    :class="[item.status == $t('yes') ? 'switch-on' : 'switch-off']"
                    @change="handleStatusChange(item)"
                  >
                  </el-switch> -->
                  <i class="el-icon-delete" @click.stop="remSlice(item)"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="dataList?.length" class="pagination">
          <div class="total-num">
            {{ $t('totalNum', { total: total }) }}
          </div>
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="pageSize" background
            :current-page="pageNo" :page-sizes="[20, 50, 100, 150, 200]" layout="prev, pager, next, sizes, jumper"
            :total="total">
          </el-pagination>
        </div>
        <div v-else class="no-data">
          {{ $t('noData') }}
        </div>
      </div>
    </div>

    <el-dialog class="my-dialog" append-to-body :title="operateInfo" :visible.sync="operateVisible" width="960px"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false">
      <el-input type="textarea" class="input-textarea" :autosize="{ minRows: 14, maxRows: 18 }" :placeholder="$t('pleaseEnter')"
        v-model="content" maxlength="5000" show-word-limit>
      </el-input>
      <div slot="footer" class="dialog-footer my-dialog-footer">
        <el-button :loading="operateLoading" @click="operateVisible = false">{{ $t('cancel') }}</el-button>
        <el-button :loading="operateLoading" type="primary" @click="handleOperate">{{ $t('confirm') }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { getFileDatas, addFileData, deleteFileData, copyFileData } from '@/api/index.js'
import { debounce } from 'lodash';

export default {
  props: {
    rowData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    knowledgeId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      sliceId: '',
      operateInfo: '',
      content: '',
      operateLoading: false,
      operateVisible: false,
      dataLoading: true,
      dataList: [],
      pageNo: 1,
      pageSize: 50,
      total: 0,
      tabList: [
        { label: this.$t('all'), value: '' },
        { label: this.$t('originalTextSlice'), value: 'file' },
        { label: this.$t('addTextSlice'), value: 'new' },
        { label: this.$t('copySlice'), value: 'copy' },
      ],
      currentTab: '',
      query: '',
    }
  },
  async created() {
    this.getFileListData()
  },
  watch: {
    'rowData.fileId'(newVal) {
      this.getFileListData()
    }
  },
  methods: {
    handleSearch: debounce(async function(value) {
      this.dataLoading = true
      const { data } = await getFileDatas({
        content: value,
        type: this.currentTab,
        fileId: this.rowData.fileId,
        pageNo: this.pageNo,
        pageSize: this.pageSize,
      })
      this.dataList = data.list
      this.total = data.total
      this.dataLoading = false
    }, 500),
    async handleStatusChange(row) {
      // await updateStatus({
      //   knowledgeId: row.knowledgeId,
      //   status: row.status,
      // });
      // this.getFileListData();
    },
    handleCurrentTab(value) {
      this.currentTab = value
      this.getFileListData()
    },
    handleClose() {
      this.$emit('close')
    },
    handleOpenLink(url) {
      fetch(url)
        .then(response => response.blob())
        .then(blob => {
          const downloadUrl = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = downloadUrl;
          link.download = this.rowData.fileName;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(downloadUrl);
        })
        .catch(error => {
          this.$message({
            message: this.$t('downloadError'),
            type: 'error'
          });
        });
    },
    handleCurrentChange(n) {
      this.pageNo = n
      this.getFileListData()
    },
    handleSizeChange(n) {
      this.pageSize = n
      this.getFileListData()
    },
    async getFileListData() {
      this.dataLoading = true
      const { data } = await getFileDatas({
        content: this.query,
        type: this.currentTab,
        fileId: this.rowData.fileId,
        pageNo: this.pageNo,
        pageSize: this.pageSize,
      })
      this.dataList = data.list
      this.total = data.total
      this.dataLoading = false
    },
    addSlice() {
      this.operateInfo = this.$t('addSliceInfo')
      this.content = ''
      this.sliceId = ''
      this.operateVisible = true
    },
    async handleCopy(item) {
      // this.content = item.content
      // this.handleOperate()
      this.operateLoading = true
      const res = await copyFileData({
        id: item.id
      })
      if (res.code === "000000") {
        this.operateVisible = false
        this.dataLoading = true
        setTimeout(() => {
          this.getFileListData()
        }, 1500)
        this.$message({
          message: res.msg,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
      this.content = ''
      this.sliceId = ''
      this.operateLoading = false
    },
    setSlice(item) {
      console.log(item)
      this.operateInfo = this.$t('editSliceInfo')
      this.content = item.content
      this.sliceId = item.id
      this.operateVisible = true
    },
    remSlice(item) {
      this.$confirm(this.$t('confirmDeleteFolder'), this.$t('tips'),{
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        confirmButtonClass: 'confirm-ok',
        cancelButtonClass: 'confirm-cancel'
      }).then(async () => {
        const params = {
          id: [item.id]
        }
        const res = await deleteFileData(params)
        if (res.code === "000000") {
          this.dataLoading = true
          setTimeout(() => {
            this.getFileListData()
          }, 1500)
          this.$message({
            message: res.msg,
            type: 'success'
          });
        } else {
          this.$message({
            message: res.msg,
            type: 'error'
          });
        }
      })
    },
    async handleOperate() {
      this.operateLoading = true
      const res = await addFileData({
        fileName: this.rowData.fileName,
        fileId: this.rowData.fileId,
        foldersId: this.rowData.foldersId,
        content: this.content,
        knowledgeId: this.knowledgeId,
        id: this.sliceId
      })
      if (res.code === "000000") {
        this.operateVisible = false
        this.dataLoading = true
        setTimeout(() => {
          this.getFileListData()
        }, 1500)
        this.$message({
          message: res.msg,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
      this.content = ''
      this.sliceId = ''
      this.operateLoading = false
    }
  },
}
</script>



<style lang="scss">
.document-info-tip.el-tooltip__popper.is-light {
  width: 288px;
  // height: 329px;
  padding: 32px 24px 26px;
  background: #FFFFFF;
  box-shadow: 0px 2px 6px 0px rgba(26,36,70,0.1);
  border: none;
  .document-info-wrapper {
    box-sizing: border-box;
    .sentence {
      font-weight: bold;
      font-size: 18px;
      color: #383D47;
      line-height: 26px;
      margin-bottom: 20px;
    }

    .el-button {
      width: 100%;
    }

    .title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 16px;
      color: #494E57;
      line-height: 20px;
      margin-bottom: 16px;
    }
    .title:not(:first-child) {
      margin-top: 22px;
    }

    .info {

      .item {
        display: flex;
        justify-content: space-between;
        margin-bottom: 14px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 20px;
        .value {
          color: #494E57;
        }
      }
      .item:last-child {
        margin-bottom: 0;
      }
    }
  }
  .popper__arrow {
    display: none;
  }
}

</style>
<style lang="scss" scoped>
@import "./kbm.scss";

.slice-info {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .header {
    width: 100%;
    height: 80px;
    padding: 24px 32px;
    border-bottom: 1px solid rgba(0,0,0,0.12);
    background: #fff;
    display: flex;
    align-items: center;
    flex-shrink: 0;
    .header-left {
      width: 50%;
      display: flex;
      align-items: center;
      flex-shrink: 0;
      .back-icon {
        width: 20px;
        margin-right: 8px;
        cursor: pointer;
      }
      .type-icon {
        width: 28px;
        margin-right: 8px;
      }

      .title {
        max-width: 70%;
        margin-right: 8px;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        -webkit-line-clamp: 1;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #494E57;
        line-height: 28px;
      }
      .tip-icon {
        cursor: pointer;
      }
    }
    
    .header-right {
      width: 50%;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      .icon-self {
        svg {
          width: 16px;
          vertical-align: text-top;
          fill: #494E57;
        }
        &:hover {
          svg {
            fill: #1747E5;
          }
        }
      }
    }
  }

  .main {
    box-sizing: border-box;
    flex-grow: 1;
    width: 100%;
    height: 100%;
    padding-top: 24px;
    padding-bottom: 32px;
    display: flex;
    border-radius: 4px;
    background: #FFFFFF;
    overflow: hidden;

    .left {
      flex-shrink: 0;
      width: 287px;
      height: 100%;
      padding: 30px 20px;
      box-sizing: border-box;
      background: linear-gradient(180deg, rgba(43, 88, 213, 0.1) 0%, rgba(43, 88, 213, 0) 100%);

      .sentence {
        font-weight: bold;
        font-size: 18px;
        color: #383D47;
        line-height: 26px;
        margin-bottom: 20px;
      }

      .el-button {
        width: 100%;
      }

      .title {
        font-weight: bold;
        font-size: 16px;
        color: #383D47;
        line-height: 20px;
        margin-top: 20px;
      }

      .info {

        .item {
          display: flex;
          justify-content: space-between;
          margin-top: 16px;
          font-weight: 400;
          font-size: 14px;
          color: #768094;
          line-height: 18px;
        }
      }
    }

    .con {
      width: 51%;
      margin: 0 auto;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      .title {
        font-weight: bold;
        font-size: 20px;
        color: #383D47;
        line-height: 28px;
        display: flex;
        justify-content: space-between;

        .num {
          color: #3D68FD;
          margin-left: 10px;
        }

        .search {
          display: flex;
        }
      }


      .tabs{
        margin-top: 28px;
        display: flex;
        align-items: center;
        .tabs-item {
          position: relative;
          margin-right: 16px;
          padding-bottom: 8px;
          text-align: center;
          line-height: 32px;
          font-family: PingFang SC, PingFang SC;
          font-weight: 500;
          font-size: 18px;
          color: #828894;
          cursor: pointer;
          &:last-child {
            border-right: none;
          }
        }
        .tabs-item-active {
          // background: #0052D9;
          color: #603ECA;
          &::after {
            display: inline-block;
            content: '';
            width: 50%;
            height: 6px;
            border-radius: 2px;
            position: absolute;
            bottom: 0;
            left: 50%;
            transform: translateX(-50%);
            color: #603ECA;
            background: #603ECA;
          }
        }
      }
      .data {
        flex-grow: 1;
        height: 100%;
        position: relative;
        overflow: hidden;
        margin-top: 16px;


        .list {
          height: 100%;
          overflow-y: scroll;
          padding-right: 6px;
          &::-webkit-scrollbar {
            width: 5px;
            height: 7px;
          }

          &::-webkit-scrollbar-thumb {
            border-radius: 5px;
            box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
            background: #CED4E0;
          }

          &::-webkit-scrollbar-track {
            border-radius: 5px;
            background: #fff;
            cursor: pointer;
          }

          .item {
            background: #FFFFFF;
            border-radius: 2px;
            border: 1px solid #D5D8DE;
            width: 100%;
            padding: 16px;
            box-sizing: border-box;
            // cursor: pointer;
            margin-bottom: 12px;
            &:hover {
              background: rgba(28,80,253,0.05);
              border: 1px solid #1747E5;
            }
            .message {
              font-weight: 400;
              font-size: 14px;
              color: #383D47;
              line-height: 22px;
              margin-bottom: 12px;
            }

            .operating {
              display: flex;
              justify-content: space-between;
              align-items: center;

              .info {
                display: flex;
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 14px;
                color: #768094;
                line-height: 18px;
                font-weight: bold;

                .type {
                  margin-right: 16px;
                  color: #828894;
                }
                .divider {
                  width: 1px;
                  height: 16px;
                  background: rgba(0,0,0,0.12);
                }
                .chrt {
                  margin-left: 16px;
                  color: #828894;
                }
              }

              .btns {
                display: flex;
                align-items: center;

                i {
                  margin: 0 8px;
                  cursor: pointer;
                  color: #828894;
                }
              }
            }
          }

          .item.active {
            background: #F4F6FF;
            border: 1px solid #4384FF;
          }

          .item:last-child {
            margin-bottom: 0;
          }
        }
      }
    }

  }
}
.my-dialog {
  .input-textarea {
    ::v-deep .el-textarea__inner {
      height: 464px !important;
    }
    
  }
}
</style>