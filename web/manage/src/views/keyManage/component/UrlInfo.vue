<template>
  <div class="url-info">
    <div class="header">
      <div class="header-left">
        <img class="back-icon" src="@/assets/images/arrow-go-back-fill.svg" @click="handleClose">
        <img class="type-icon" :src="require(`@/assets/images/url.svg`)" alt="">
        <div class="title">{{urlInfo.pageUrl}}</div>
        <el-tooltip popper-class="document-info-tip" effect="light" class="item" placement="top">
          <img class="tip-icon" src="@/assets/images/information-line.svg">
          <template slot="content">
            <div class="document-info-wrapper">
              <!-- <div class="sentence">{{ rowData.fileName }}</div> -->
              <div class="title">{{$t('basicInformation')}}</div>
              <div class="info">
                <div class="item"><span>{{$t('fileFormat')}}</span><span>{{$t('urlLink')}}</span></div>
                <div class="item"><span>{{$t('updateFrequency')}}</span><span>{{$t('doNotAutoUpdate')}}</span></div>
                <div class="item"><span>{{$t('uploadDate')}}</span><span>{{ urlInfo.createTime }}</span></div>
                <div class="item"><span>{{$t('updateDate')}}</span><span>{{ urlInfo.updateTime }}</span></div>
                <div class="item"><span>{{$t('characterCount')}}</span><span>{{ urlInfo.wordCount }}</span></div>
              </div>
              <div class="title">{{$t('importConfigInfo')}}</div>
              <div class="info">
                <div class="item"><span>{{$t('averageParagraphLength')}}</span><span>{{ urlInfo.avgParagraphsCount }}</span></div>
                <div class="item"><span>{{$t('paragraphCount')}}</span><span>{{ urlInfo.paragraphsNum }}</span></div>
                <div class="item"><span>{{$t('segmentRules')}}</span><span>{{$t('autoSegmentation')}}</span></div>
              </div>
            </div>
          </template>
        </el-tooltip>
      </div>
      <div class="header-right btns-box">
        <el-button
          class="icon-self"
          plain
          @click="handleOpenLink(urlInfo.pageUrl)"
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
            <i class="el-icon-s-operation"></i>{{ $t('sliceInfo') }}<span class="num">{{ urlInfo.dataList.length }}</span>
          </div> -->
          <div class="search">
            <el-input v-model="fileName" size="small" :placeholder="$t('inputPlaceholder')" clearable  @input="handleSearch">
              <i slot="suffix" class="el-input__icon" />
            </el-input>
            <!-- <el-button style="margin-left: 10px" plain size="small" icon="el-icon-search" type="success" @click="getUrlDataLists()">{{$t('query')}}</el-button> -->
            <div style="margin-right: 10px;"></div>
            <el-button plain size="small" icon="el-icon-plus" @click="addSlice('add')">{{$t('added')}}</el-button>
            <!-- <el-button plain size="small">更新频率</el-button> -->
          </div>
        </div>
        <!-- <div class="options">
          <div class="option" v-for="item in SLICE_OPTIONS" :key="item.id" :class="{ active: sliceType === item.id }"
            @click="handleChangeSliceType(item.id)">
            {{ item.label }}
          </div>
        </div> -->
        <div class="tabs">
          <div :class="{'tabs-item': true, 'tabs-item-active': currentTab == item.value}" v-for="item in tabList" :key="item.label" @click="handleCurrentTab(item.value)">
            {{ item.label }}
          </div>
        </div>
        <div v-if="urlInfo.dataList?.length" class="data">
          <div class="list">
            <div class="item" v-for="item in urlInfo.dataList" :key="item.id" @click="handelChangeSliceItemType(item)"
              :class="{ active: item.id === sliceItemType }">
              <div class="message">
                {{ item.content }}
              </div>
              <div class="operating">
                <div class="info">
                  <div class="type">{{$t('type')}} {{ item.category }}</div>
                  <div class="divider"></div>
                  <div class="chrt">{{$t('characters')}} {{ item.wordCount }}</div>
                </div>
                <div class="btns">
                  <i class="el-icon-copy-document"></i>
                  <i class="el-icon-edit" @click="setSlice(item)"></i>
                  <i class="el-icon-delete" @click.stop="deleteHandler(item)"></i>
                  <!-- <el-switch v-model="item.enable" :active-value="1" :inactive-value="2" active-color="#13ce66"
                    inactive-color="#ff4949">
                  </el-switch> -->
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="urlInfo.dataList?.length" class="pagination">
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
      <div class="right" v-loading="relevanceLoading">
        <div class="title">
          <i class="el-icon-s-flag"></i>{{$t('sliceRelatedKnowledgePoints')}}<span class="num">{{ relevanceData.length }}</span>
        </div>
        <div class="data">
          <div class="list">
            <div class="item" v-for="item in relevanceData" :key="item.id">
              <div class="message">
                {{ item.content }}
              </div>
              <div class="operating">
                <div class="info">
                  <div class="type">{{$t('type')}}: {{ item.category }}</div>
                </div>
                <div class="btns">
                  <i class="el-icon-edit"></i>
                  <i class="el-icon-delete"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- <el-dialog title="更新频率" :visible.sync="updateVisible" width="560px" :modal-append-to-body="false"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false">
    </el-dialog> -->

    <el-dialog class="my-dialog" append-to-body :title="operateInfo" :visible.sync="operateVisible" width="500px"
      :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
      <el-input size="small" type="textarea" :autosize="{ minRows: 8, maxRows: 18 }" :placeholder="$t('pleaseEnterKnowledgeBaseDescription')"
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
import {getSliceDetail, addUrlSlice, setUrlSlice, getKnowledgeUrlDataDetail, getUrlDataList, apiDeleteKnowledgeUrlData} from '@/api/index.js'
import { debounce } from 'lodash';
export default {
  props: {
    urlId: {
      type: String,
      default: ''
    },
    knowledgeId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      content: '',
      contentCondition: '',
      fileName: '', // 文件名称
      dataLoading: true,
      // sliceType: 1,
      sliceItemType: 1,
      urlInfo: {
        dataList: []
      },
      operateInfo: '',
      operateLoading: false,
      operateVisible: false,
      pageNo: 1,
      pageSize: 50,
      total: 0,
      sliceId: '',
      relevanceData: [],
      relevanceLoading: false,
      tabList: [
        { label: this.$t('all'), value: '' },
        { label: this.$t('originalTextSlice'), value: 'file' },
        { label: this.$t('addTextSlice'), value: 'new' },
        { label: this.$t('copySlice'), value: 'copy' },
      ],
      currentTab: '',
    }
  },
  created() {
    this.getSliceDetailData()
  },
  watch: {
    urlId(newVal) {
      this.getSliceDetailData()
    }
  },
  methods: {
    handleCurrentTab(value) {
      this.currentTab = value
      this.getUrlDataLists()
    },
    handleSearch: debounce(async function(value) {
      this.getUrlDataLists()
    }, 500),
    // handleChangeSliceType(type) {
    //   this.sliceType = type
    // },
    addSlice() {
      this.operateInfo = this.$t('addSliceInfo')
      this.content = ''
      this.sliceId = ''
      this.operateVisible = true
    },
    setSlice(item) {
      this.operateInfo = this.$t('editSliceInfo')
      this.operateVisible = true
      this.sliceId = item.id
      this.content = item.content
    },
    // 删除切片
    deleteHandler(data) {
      this.$confirm(this.$t('confirmDeleteFolder'), this.$t('tips'),{
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        confirmButtonClass: 'confirm-ok',
        cancelButtonClass: 'confirm-cancel'
      }).then(() => {
        const params = {
          id: [data.id]
        }
        apiDeleteKnowledgeUrlData(params).then(res => {
          if(res.code == '000000') {
            this.dataLoading = true;
            setTimeout(() => {
              this.getUrlDataLists()
            }, 1000);
            this.$message({
              message: res.msg,
              type: "success",
            });
          }else {
            this.$message({
              message: res.msg,
              type: 'error'
            });
          }
        })
      })
    },
    async handleOperate() {
      this.operateLoading = true
      let res
      if (this.operateInfo === this.$t('addSliceInfo')) {
        res = await addUrlSlice({
          knowledgeId: this.knowledgeId,
          url: this.urlInfo.pageUrl,
          content: this.content,
          urlId: this.urlId
        })
      } else {
        res = await setUrlSlice({
          id: this.sliceId,
          content: this.content,
          knowledgeId: this.knowledgeId
        })
      }
      if (res.code === "000000") {
        this.operateVisible = false
        this.dataLoading = true
        setTimeout(() => {
          this.getSliceDetailData()
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
      this.operateLoading = false
      this.content = ''
    },
    handleClose() {
      this.$emit('close')
    },
    async handelChangeSliceItemType(item) {
      this.sliceItemType = item.id
      this.relevanceLoading = true
      const res = await getKnowledgeUrlDataDetail({
        parentId: item.id
      })
      this.relevanceData = res.data
      this.relevanceLoading = false
      console.log(res.data)
    },
    handleOpenLink(url) {
      console.log(url)
      window.location.href = url
    },
    async getSliceDetailData() {
      this.dataLoading = true
      const res = await getSliceDetail({
        urlId: this.urlId,
        pageNo: this.pageNo,
        pageSize: this.pageSize
      })
      this.urlInfo = res.data
      this.total = res.data.totalRow
      this.dataLoading = false
    },
    handleCurrentChange(n) {
      this.pageNo = n
      this.getSliceDetailData()
    },
    handleSizeChange() {
      this.pageSize = n
      this.getSliceDetailData()
    },
    async getUrlDataLists() {
      this.dataLoading = true
      const res = await getUrlDataList({
        urlId: this.urlId,
        content: this.fileName,
        pageNo: this.pageNo,
        pageSize: this.pageSize
      })
      this.urlInfo.dataList = res.data.list
      this.total = res.data.total
      this.dataLoading = false
    },
  },
}
</script>

<style lang="scss" scoped>
@import "./kbm.scss";

.url-info {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

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
      width: 80%;
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
        max-width: 40%;
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
      width: 20%;
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
    width: 80%;
    margin: 0 auto;
    box-sizing: border-box;
    flex-grow: 1;
    height: 100%;
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

    .con {
      flex-grow: 1;
      padding: 20px;
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
      .options {
        display: flex;
        margin-top: 25px;

        .option {
          width: 88px;
          height: 35px;
          background: #F2F4F7;
          border-radius: 4px;
          margin-right: 12px;
          line-height: 35px;
          text-align: center;
          font-weight: bold;
          cursor: pointer;
        }

        .option.active {
          background: #EAEFFC;
          color: #5C83ED;
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
            border-radius: 4px;
            border: 1px solid #E1E4EB;
            width: 100%;
            padding: 16px;
            box-sizing: border-box;
            cursor: pointer;
            margin-bottom: 12px;

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

    .right {
      flex-shrink: 0;
      width: 500px;
      padding: 20px;
      box-sizing: border-box;
      height: 100%;
      border-left: 1px solid #EEEEEE;
      display: flex;
      flex-direction: column;

      .title {
        font-weight: bold;
        font-size: 20px;
        color: #383D47;
        line-height: 28px;

        .num {
          color: #3D68FD;
          margin-left: 10px;
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
            background: #F4F6FF;
            border-radius: 4px;
            border: 1px solid #E1E4EB;
            width: 100%;
            padding: 16px;
            box-sizing: border-box;
            cursor: pointer;
            margin-bottom: 12px;

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
                font-weight: 400;
                font-size: 14px;
                color: #768094;
                line-height: 18px;
                font-weight: bold;
              }

              .btns {
                display: flex;
                align-items: center;

                i {
                  margin: 0 8px;
                  cursor: pointer;
                }
              }
            }
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