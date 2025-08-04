<template>
  <el-drawer :title="$t('answerReview')" :visible.sync="addQaVisibleAnswer" :modal-append-to-body="false" :close-on-click-modal="false"
    :close-on-press-escape="false" :wrapperClosable="false" :before-close="handleClose" class="elDrawer" size="50%">
    <div slot class="qa-box">
      <div class="userInfo">
        <span>{{ setForm.question }}</span>
      </div>
      <div class="flex aligns just" style="margin-bottom: 12px">
        <div class="flex-center" v-if="setForm.auditStatus != 0">
          <img v-if="setForm.auditStatus == 1 || setForm.auditStatus == 3"
            :src="require('@/assets/images/checkbox-circle-fill.svg')" />
          <i v-else style="color: #f00; font-size: 22px" class="iconfont el-icon-error"></i>
          <span style="margin: 0 4px 0 8px" v-if="setForm.auditStatus == 1">{{$t('reviewPassed')}}</span>
          <span style="margin: 0 4px 0 8px" v-if="setForm.auditStatus == 2">{{$t('reviewFailed')}}</span>
          <span style="margin: 0 4px 0 8px" v-if="setForm.auditStatus == 3">{{$t('noProcessing')}}</span>
          <el-popover placement="left-start" width="300" trigger="hover">
            <div style="padding: 0 12px 12px">
              <div class="flex just">
                <span class="formKey">{{$t('reviewer')}} </span>
                <span class="formValue">
                  {{ setForm.auditUserName }}
                </span>
              </div>
              <div class="flex just">
                <span class="formKey"> {{$t('department')}} </span>
                <span class="formValue">
                  {{ setForm.verifyDeptName }}
                </span>
              </div>
              <div class="flex just">
                <span class="formKey"> {{$t('verificationTime')}} </span>
                <span class="formValue">
                  {{ setForm.createTime }}
                </span>
              </div>
            </div>
            <img slot="reference" :src="require('@/assets/images/information-line.svg')" />
          </el-popover>
        </div>
        <div class="flex aligns  user-list-box">
          <div :class="['userList', activeIndex == 0 ? 'activeList' : '']" @click="clickUser(0)">
            {{$t('verifiedAnswer')}}
          </div>
          <div :class="['userList', activeIndex == 1 ? 'activeList' : '']" @click="clickUser(1)">
            {{$t('originalAnswer')}}
          </div>
        </div>
        <div class="flex-center just" style="width: 49.5%">
          <div class="flex-center">
            <div class="line"></div>
            <span class="line-text" style="
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 18px;
                color: #383d47;
              ">{{$t('originalAnswer')}}</span>
          </div>
        </div>
      </div>
      <div class="content">
        <el-input v-model="setForm.verifyAnswer" type="textarea" :autosize="{ minRows: 6, maxRows: 50 }"
          :placeholder="$t('inputPlaceholder')" class="leftText" disabled v-if="activeIndex == 0"></el-input>
        <el-input v-model="setForm.answer" type="textarea" :autosize="{ minRows: 6, maxRows: 50 }"
          :placeholder="$t('inputPlaceholder')" v-if="activeIndex == 1" class="leftText"></el-input>
        <!-- 溯源 -->
        <div class="traceability">
          <div v-if="sourceTableList.length > 0">
            <div v-for="(item, index) in sourceTableList" :key="item">
              <el-collapse>
                <el-collapse-item :name="index">
                  <el-tooltip slot="title" class="item" effect="dark" :content="
                      item.route
                        ? (item.knowledgeName ? item.knowledgeName : $t('noKnowledgeBase')) +
                          '-' +
                          item.route.join('|')
                        : ''
                    " placement="top">
                    <div style="
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        font-weight: bold;
                        font-family: MiSans, MiSans;
                        font-weight: 500;
                        font-size: 16px;
                        color: #383D47;
                      ">
                      {{
                      item.route
                      ? (item.knowledgeName ? item.knowledgeName : $t('noKnowledgeBase')) +
                      "-" +
                      item.route.join("|")
                      : ""
                      }}
                    </div>
                  </el-tooltip>
                  <p style="
                      margin-bottom: 16px;
                      cursor: pointer;
                      font-family: MiSans, MiSans;
                      font-weight: 400;
                      font-size: 14px;
                      color: #828894;
                      line-height: 22px;
                    ">
                    {{ item.text }}
                  </p>
                </el-collapse-item>
              </el-collapse>
            </div>
          </div>
          <p style="text-align: center" v-else>{{$t('noData')}}</p>
        </div>
      </div>
      <div class="flex-center just" style="margin-top: 20px;" v-if="setForm.auditStatus == 0">
        <div class="flex-center" style="margin-right: 20px;">
          <div class="line"></div>
          <span class="line-text" style="
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 18px;
              color: #383d47;
            ">{{$t('review')}}</span>
        </div>
        <div class="flex-center" v-if="setForm.auditStatus == 0">
          <el-radio-group v-model="formList.auditStatus">
            <el-radio label="1">{{$t('approved')}}</el-radio>
            <el-radio label="2">{{$t('rejected')}}</el-radio>
            <el-radio label="3">{{$t('noProcessing')}}</el-radio>
          </el-radio-group>
        </div>
        
      </div>
      <div class="footerDrawer">
        <el-button @click="handleClose">{{$t('close')}}</el-button>
        <el-button type="primary" v-if="setForm.auditStatus == 0" @click="handleAddQaDialog(0)">{{$t('closeAfterSubmit')}}</el-button>
        <el-button type="primary" v-if="setForm.auditStatus == 0" @click="submitVerification">{{$t('verifyNextAfterSubmit')}}</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script>
  import { sourceList } from "@/api/app";
  export default {
    data() {
      return {
        activeIndex: 0,
        formList: {
          auditStatus: ''
        },
        sourceTableList: [],
      };
    },
    props: {
      addQaVisibleAnswer: {
        type: Boolean,
        default: false,
      },
      setForm: {
        type: Object,
        default: () => { },
      },
    },
    mounted() {
      this.toSource(this.setForm);
    },
    methods: {
      toSource(row) {
        this.sourceTableList = [];
        sourceList({ dialogueId: row.dialogueId }).then((res) => {
          if (res.code == "000000") {
            this.sourceTableList = res.data.sourceAnswerResultList || [];
          } else {
            this.sourceTableList = [];
          }
        });
      },
      handleClose() {
        this.$emit("handlecloseDraw", false);
      },
      clickUser(type) {
        this.activeIndex = type;
        // if (type == 1) {
        //   this.toSource(this.setForm)
        // }
      },
      handleAddQaDialog(type) {
        this.$emit("handleAddQaDialogAns", this.formList.auditStatus, type);
        this.$set(this.formList, 'auditStatus', '')
      },
      submitVerification() {
        this.handleAddQaDialog(1);
      },
    },
  };
</script>

<style lang="scss" scoped>
  .elDrawer {
    ::v-deep .el-drawer__header {
      background: linear-gradient(180deg,
          rgba(43, 88, 213, 0.1) 0%,
          rgba(43, 88, 213, 0) 100%);
      border-radius: 8px 8px 0px 0px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
    }

    ::v-deep .el-drawer__body {
      padding: 0 24px 10px;
    }
  }

  .userInfo {
    margin: 0 0 16px 0;

    span {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 24px;
      color: #383d47;
      line-height: 32px;
    }
  }

  .flex {
    display: flex;
  }

  .flex-center {
    display: flex;
    align-items: center;
  }

  .aligns {
    align-items: center;
  }

  .just {
    justify-content: space-between;
  }
  .qa-box {
    display: flex;
    flex-direction: column;
    height: 100%;
  }
  .content {
    display: flex;
    flex: 1;
    overflow: hidden;

    ::v-deep .el-textarea.leftText {
      margin-right: 16px;

      .el-textarea__inner {
        border: 1px solid #f2f5fa;
        color: #494E57;
      }
    }

    ::v-deep .el-textarea {
      width: 49.5%;
    }

    .traceability {
      width: 49.5%;
      background: #f2f5fa;
      height: 100% !important;
      margin-right: 16px;
      overflow-y: auto;
      padding: 6px 14px;

      ::v-deep .el-collapse-item__header {
        height: 64px;
        line-height: 64px;
        background-color: #F7F8FA;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 16px;
        color: #494E57;
      }

      ::v-deep .el-collapse-item__content {
        background-color: #F7F8FA;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 22px;
        padding-bottom: 16px;
      }
    }

    ::v-deep .el-textarea__inner {
      background: #F7F8FA;
      height: 100% !important;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #494E57;
      line-height: 24px;
    }
  }

  .footerDrawer {
    margin-top: 24px;
    text-align: right;
  }
  .user-list-box {
    padding: 2px;
    display: flex;
    background: #F7F8FA;
    border-radius: 2px;
  }
  .userList {
    padding: 0px 16px;
    // height: 40px;
    background: #f2f5fa;
    border-radius: 4px;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #828894;
    line-height: 28px;
    cursor: pointer;
    
    text-align: center;
    border-radius: 2px;

  }

  .activeList {
    background: #FFFFFF;
    box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
    color: #494E57;
  }

  .formKey {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #828894;
  }

  .formValue {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #383d47;
  }
  .line {
    width: 4px;
    height: 18px;
    background: #1747E5;
    border-radius: 0px 2px 2px 0px;
    margin-right: 4px;
  }
  .line-text {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #494E57;
    line-height: 32px;
  }
</style>