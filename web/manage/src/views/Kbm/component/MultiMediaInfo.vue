<template>
  <div class="slice-info">
    <div class="header">
      <div class="header-left">
        <img class="back-icon" src="@/assets/images/arrow-go-back-fill.svg" @click="handleClose" />
        <img v-if="rowData.type == 2" class="type-icon" :src="require(`@/assets/images/icon-picture.svg`)" alt="">
        <img v-else-if="rowData.type == 3" class="type-icon" :src="require(`@/assets/images/icon-video.svg`)" alt="">
        <img v-else class="type-icon" :src="require(`@/assets/images/icon-mp3.svg`)" alt="">
        <div class="title">{{rowData.fileName}}</div>
        <el-tooltip popper-class="document-info-tip" effect="light" class="item" placement="top">
          <img class="tip-icon" src="@/assets/images/information-line.svg">
          <template slot="content">
            <div class="document-info-wrapper">
              <!-- <div class="sentence">{{ rowData.fileName }}</div> -->
              <div class="title">{{$t('basicInformation')}}</div>
                <div class="info">
                  <div class="item">
                    <span>{{$t('fileFormat')}}</span><span>{{ rowData.fileType }}</span>
                  </div>
                  <div class="item">
                    <span>{{$t('originalFileSize')}}</span><span>{{ rowData.fileSize }}</span>
                  </div>
                  <div class="item">
                    <span>{{$t('uploadDate')}}</span><span>{{ rowData.createTime }}</span>
                  </div>
                  <div class="item">
                    <span>{{$t('characterCount')}}</span><span>{{ rowData.wordCount }}</span>
                  </div>
                </div>
                <div class="title">{{$t('importConfigInfo')}}</div>
                <div class="info">
                  <div class="item"><span>{{$t('averageParagraphLength')}}</span><span>--</span></div>
                  <div class="item">
                    <span>{{$t('paragraphCount')}}</span><span>{{ rowData.paragraphsNum + $t('paragraph') }}</span>
                  </div>
                </div>
            </div>
          </template>
        </el-tooltip>
      </div>
      <div class="header-right btns-box">
        <el-button
          icon="el-icon-download"
          plain
          @click="handleOpenLink(rowData.fileUrl)"
          >
            <!-- <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" d="M12 3c5.392 0 9.878 3.88 10.819 9-.94 5.12-5.427 9-10.819 9-5.392 0-9.878-3.88-10.819-9C2.121 6.88 6.608 3 12 3Zm0 16a9.005 9.005 0 0 0 8.777-7 9.005 9.005 0 0 0-17.554 0A9.005 9.005 0 0 0 12 19Zm0-2.5a4.5 4.5 0 1 1 0-9 4.5 4.5 0 0 1 0 9Zm0-2a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5Z" data-follow-fill="#848587"/></svg> -->
            {{$t('download')}}
          </el-button>
      </div>
    </div>
    <div class="main">
      <!-- <div class="left">
        <div class="sentence">{{ rowData.fileName }}</div>
        <el-button
          plain
          size="small"
          icon="el-icon-link"
          type="primary"
          @click="handleOpenLink(rowData.fileUrl)"
          >{{$t('viewSource')}}</el-button
        >
        <div class="title">{{$t('basicInformation')}}</div>
        <div class="info">
          <div class="item">
            <span>{{$t('fileFormat')}}</span><span>{{ rowData.fileType }}</span>
          </div>
          <div class="item">
            <span>{{$t('originalFileSize')}}</span><span>{{ rowData.fileSize }}</span>
          </div>
          <div class="item">
            <span>{{$t('uploadDate')}}</span><span>{{ rowData.createTime }}</span>
          </div>
          <div class="item">
            <span>{{$t('characterCount')}}</span><span>{{ rowData.wordCount }}</span>
          </div>
        </div>
        <div class="title">{{$t('importConfigInfo')}}</div>
        <div class="info">
          <div class="item"><span>{{$t('averageParagraphLength')}}</span><span>--</span></div>
          <div class="item">
            <span>{{$t('paragraphCount')}}</span><span>{{ rowData.paragraphsNum + $t('paragraph') }}</span>
          </div>
        </div>
      </div> -->
      <div class="mindle" v-if="detailType == 2">
        <div class="title-wrapper">
          <span class="text-title">
          <!-- <i class="el-icon-picture"></i> -->
            {{$t('originalImage')}}
          </span>
          <div class="preview-btn" @click="preview(rowData)">
            <i class="el-icon-zoom-in"></i>
            {{ $t('zoomIn') }}
          </div>
        </div>
        <div class="img-box">
          <el-image :src="pictureUrl" :preview-src-list="pictureList">
          </el-image>
        </div>
        <div class="text-search">
          <span class="text-title">
            <!-- <i class="el-icon-document"></i> -->
            {{$t('recognizedText')}}
          </span>
        </div>
        <div
          class="mindle-msg"
          style="height: 420px"
          v-loading="allTextLoading"
        >
          {{ allText }}
        </div>
      </div>
      <div class="mindle wav-mindle" v-else>
        <div class="text-search">
          <span class="text-title">
            <!-- <img
              src="@/assets/images/wav.svg"
              style="width: 24px; height: 24px; vertical-align: middle"
            /> -->
            {{$t('transcribedText')}}
          </span>
        </div>
        <div class="divid-line"></div>
        <div
          class="mindle-msg"
          style="height: 750px"
          v-loading="allTextLoading"
        >
          <div class="play-voice">
            <!-- <div class="play-btn" @click="handlePlayVoice">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" fill="#1747E5" d="M12 22C6.477 22 2 17.523 2 12S6.477 2 12 2s10 4.477 10 10-4.477 10-10 10ZM10.622 8.415a.4.4 0 0 0-.622.332v6.506a.4.4 0 0 0 .622.332l4.879-3.252a.401.401 0 0 0 0-.666l-4.88-3.252Z" data-follow-fill="#848587"/></svg>
              {{ $t('playVoice') }}
            </div>
            <audio :src="audioUrl" ref="audioRef"></audio> -->
          </div>
          {{ allText }}
        </div>
      </div>
      <div class="con" v-loading="dataLoading">
        <div class="title">
          <div class="title-text">{{ $t('sliceInfo') }}</div>
          <!-- <div class="icon">
            <i class="el-icon-s-operation"></i>{{$t('sliceInfo')}}<span class="num">{{
              total
            }}</span>
          </div> -->
          <div class="search">
            <!-- <el-input v-model="query" size="small" :placeholder="$t('inputPlaceholder')" @input="handleSearch" clearable>
              <i slot="prefix" class="el-input__icon el-icon-search" />
            </el-input> -->
            <div style="margin-right: 10px"></div>
            <el-button
              plain
              size="small"
              icon="el-icon-plus"
              @click="addSlice('add')"
              >{{$t('added')}}</el-button
            >
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
                {{ item.content}}
              </div>
              <div class="operating">
                <div class="info">
                  <div class="type">{{$t('typeOriginalSlice')}}</div>
                  <div class="divider"></div>
                  <div class="chrt">{{$t('characters')}} {{ item.content.length }}</div>
                </div>
                <div class="btns">
                  <i class="el-icon-edit" @click="setSlice(item)"></i>
                  <i class="el-icon-delete" @click="remSlice(item)"></i>
                  <!-- <el-switch
                   v-model="value"
                   :active-value="$t('yes')"
                  :inactive-value="$t('no')"
                  active-color="#1747E5"
                  inactive-color="#CED4E0"
                  :class="[value == $t('yes') ? 'switch-on' : 'switch-off']"
                  :width="24">
                  </el-switch> -->
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="dataList?.length" class="pagination">
          <div class="total-num">
            {{ $t('totalNum', { total: total }) }}
          </div>
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :page-size="pageSize"
            :current-page="pageNo"
            :page-sizes="[20, 50, 100, 150, 200]"
            layout="prev, pager, next, sizes, jumper"
            :total="total"
            background
          >
          </el-pagination>
        </div>
        <div v-else class="no-data">
          {{ $t('noData') }}
        </div>
      </div>
    </div>

    <el-dialog
      class="my-dialog"
      append-to-body
      :title="operateInfo"
      :visible.sync="operateVisible"
      width="500px"
      :close-on-click-modal="false"
      :show-close="false"
      :close-on-press-escape="false"
    >
      <el-input
        size="small"
        type="textarea"
        :autosize="{ minRows: 8, maxRows: 18 }"
        :placeholder="$t('enterKnowledgeBaseDescription')"
        v-model="content"
        maxlength="5000"
        show-word-limit
      >
      </el-input>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="operateLoading" @click="operateVisible = false"
          >{{ $t('cancel') }}</el-button>
        <el-button
          :loading="operateLoading"
          type="primary"
          @click="handleOperate"
          >{{ $t('confirm') }}</el-button>
      </span>
    </el-dialog>
    <ComFilePreview ref="ComFilePreview" imageHeight="67vh" ></ComFilePreview>
  </div>
</template>

<script>
import {
  getMediaDatas,
  addMediaData,
  deleteMediaData,
  getDataByMediaId,
  getImage,
  textToVoice
} from "@/api/index.js";
import { debounce } from 'lodash';
import ComFilePreview from '@/components/ComFilePreview'

export default {
  props: {
    rowData: {
      type: Object,
      default: () => {
        return {};
      },
    },
    knowledgeId: {
      type: String,
      default: "",
    },
  },
  components: {
    ComFilePreview
  },
  data() {
    return {
      sliceId: "",
      operateInfo: "",
      content: "",
      operateLoading: false,
      operateVisible: false,
      dataLoading: true,
      dataList: [],
      pageNo: 1,
      pageSize: 50,
      total: 0,
      detailType: null, //显示文件详情类型
      searchText: "", // 输入框
      value: "yes",
      allText: "",
      allTextLoading: false,
      pictureUrl: "",
      pictureList: [],
      tabList: [
        { label: this.$t('all'), value: '' },
        { label: this.$t('originalTextSlice'), value: 'file' },
        { label: this.$t('addTextSlice'), value: 'new' },
        { label: this.$t('copySlice'), value: 'copy' },
      ],
      currentTab: '',
      query: '',
      audioUrl: ''
    };
  },
  async created() {
    this.getFileListData();
    this.getDataByMediaId();
    this.getImage();
  },
  watch: {
    "rowData.fileId"(newVal) {
      this.getFileListData();
      this.getDataByMediaId();
      this.getImage();
    },
  },
  methods: {
    handlePlayVoice() {
      textToVoice({
        applicationId: '00759c5a4ae74622971ee51142287e2d',
        text: this.allText,
      }).then((res) => {
        if (res.code === 200) {
          this.audioUrl = res?.data?.fileUrl
          setTimeout(() => {
            this.$refs.audioRef.play()
          }, 500)
        }
      })
    },
    preview(row) {
      this.$refs.ComFilePreview.openPreview(row.fileUrl, row.fileType,'');
    },
    handleCurrentTab(value) {
      this.currentTab = value
      this.handleSearch()
    },
    handleSearch: debounce(async function(value) {}, 500),
    handleClose() {
      this.$emit("close");
    },
    handleOpenLink(url) {
      fetch(url)
        .then((response) => response.blob())
        .then((blob) => {
          const downloadUrl = window.URL.createObjectURL(blob);
          const link = document.createElement("a");
          link.href = downloadUrl;
          link.download = this.rowData.fileName;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(downloadUrl);
        })
        .catch((error) => {
          this.$message({
            message: $t('errorDownloadingFile'),
            type: "error",
          });
        });
    },
    handleCurrentChange(n) {
      this.pageNo = n;
      this.getFileListData();
    },
    handleSizeChange(n) {
      this.pageSize = n;
      this.getFileListData();
    },
    async getFileListData() {
      this.dataLoading = true;
      this.pictureList = [];
      this.detailType = this.rowData.type;
      this.pictureUrl = this.rowData.fileUrl;
      this.pictureList.push(this.pictureUrl);
      const { data } = await getMediaDatas({
        multiMediaId: this.rowData.fileId,
        pageNo: this.pageNo,
        pageSize: this.pageSize,
      }); 
      this.dataList = data.list;
      this.total = data.total;
      this.dataLoading = false;
    },
    async getDataByMediaId() {
      this.allTextLoading = true;
      const { data } = await getDataByMediaId({
        multiMediaId: this.rowData.fileId,
        pageNo: this.pageNo,
        pageSize: this.pageSize,
      });
      this.allText = data;
      this.allTextLoading = false;
    },
    async getImage() {
      this.allTextLoading = true;
      const { data } = await getImage({
        fileId: this.rowData.fileId,
      });
    },
    addSlice() {
      this.operateInfo = this.$t('addSliceInfo');
      this.content = "";
      this.sliceId = "";
      this.operateVisible = true;
    },
    setSlice(item) {
      this.operateInfo = this.$t('editSliceInfo');
      this.content = item.content;
      this.sliceId = item.id;
      this.operateVisible = true;
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
        const res = await deleteMediaData(params);
        if (res.code === "000000") {
          this.dataLoading = true;
          setTimeout(() => {
            this.getFileListData();
          }, 1500);
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
      })
    },
    async handleOperate() {
      this.operateLoading = true;
      const res = await addMediaData({
        multiMediaName: this.rowData.fileName,
        multiMediaId: this.rowData.fileId,
        foldersId: this.rowData.foldersId,
        content: this.content,
        // content: JSON.stringify({
        //   text: this.content,
        // }),
        knowledgeId: this.knowledgeId,
        id: this.sliceId,
      });
      if (res.code === "000000") {
        this.operateVisible = false;
        this.dataLoading = true;
        setTimeout(() => {
          this.getFileListData();
        }, 1500);
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
      this.content = "";
      this.sliceId = "";
      this.operateLoading = false;
    },
  },
};
</script>



<style lang="scss" scoped>
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
    display: flex;
    border-radius: 4px;
    background: #ffffff;
    overflow: hidden;

    .left {
      flex-shrink: 0;
      width: 287px;
      height: 100%;
      padding: 30px 20px;
      box-sizing: border-box;
      background: linear-gradient(
        180deg,
        rgba(43, 88, 213, 0.1) 0%,
        rgba(43, 88, 213, 0) 100%
      );

      .sentence {
        font-weight: bold;
        font-size: 18px;
        color: #383d47;
        line-height: 26px;
        margin-bottom: 20px;
      }

      .el-button {
        width: 100%;
      }

      .title {
        font-weight: bold;
        font-size: 16px;
        color: #383d47;
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

    .mindle {
      width: 59%;
      flex-shrink: 0;
      padding: 24px 24px 32px 32px;
      box-sizing: border-box;
      border-right: 1px solid rgba(0, 0, 0, 0.12);
      display: flex;
      flex-direction: column;
      .title-wrapper {
        display: flex;
        align-items: center;
        justify-content: space-between;
      }
      .preview-btn {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #494E57;
        line-height: 24px;
        cursor: pointer;
      }
      .text-title {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 22px;
        color: #494E57;
        line-height: 32px;
      }

      .img-box {
        margin: 28px 0px;
        text-align: center;
        ::v-deep .el-image__preview {
          height: 456px;
        }
        img {
          height: 288px;
          width: 504px;
        }
      }

      .text-search {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
      .mindle-msg {
        flex: 1;
        margin-top: 20px;
        font-weight: 400;
        font-size: 14px;
        color: #383d47;
        line-height: 24px;
        font-style: normal;
        overflow-y: scroll;
        .play-voice {
          margin-bottom: 14px;
          display: flex;
          align-items: center;
          justify-content: flex-end;
          .play-btn {
            display: flex;
            align-items: center;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #1747E5;
            line-height: 20px;
            cursor: pointer;
            svg {
              width: 14px;
              margin-right: 6px;
            }
          }
        }
        &::-webkit-scrollbar {
          width: 5px;
          height: 7px;
        }

        &::-webkit-scrollbar-thumb {
          border-radius: 5px;
          box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
          background: #535353;
        }

        &::-webkit-scrollbar-track {
          border-radius: 5px;
          background: #fff;
          cursor: pointer;
        }
      }
      .divid-line {
        margin: 26px 0 0;
        height: 1px;
        background: rgba(0, 0, 0, 0.12);
      }
    }
    .wav-mindle {
      .mindle-msg {
        margin-top: 16px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 22px;
      }
    }
    .con {
      flex-grow: 1;
      padding: 20px;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      overflow-y: scroll;
      .el-switch {
        height: 16px;
        ::v-deep .el-switch__core {
          height: 16px;
          &:after {
            top: 2px;
            width: 10px;
            height: 10px;
          }
        }
        &.switch-on {
          ::v-deep .el-switch__core {
            &:after {
              margin-left: -10px;
            }
          }
        }
      }
      .title {
        font-weight: bold;
        font-size: 20px;
        color: #383d47;
        line-height: 28px;
        display: flex;
        justify-content: space-between;

        .num {
          color: #3d68fd;
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
        margin-top: 20px;

        .list {
          height: 100%;
          overflow-y: scroll;

          &::-webkit-scrollbar {
            width: 5px;
            height: 7px;
          }

          &::-webkit-scrollbar-thumb {
            border-radius: 5px;
            box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
            background: #535353;
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
</style>