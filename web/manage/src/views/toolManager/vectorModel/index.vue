<template>
  <div class="xl-model">
    <div class="xl-model-title">{{ $t("quantitativeModelManagement") }}</div>
    <div class="xl-model-content">
      <div class="xl-model-content-right" v-xl_loading="xl_loading">
        <div class="head">
          <el-input
            :placeholder="$t('enterPluginKeywords')"
            v-model="xl_paramSearch.keyword"
            style="width: 334px"
            @keydown.native.enter="xl_searchHandler"
            clearable suffix-icon="el-icon-search"
          >
          </el-input>
          <el-button
            plain
            style="border: 1px solid #1747E5; background: #1747E5;color:#fff; border-radius: 2px;"
            @click="xl_deployingLargeModel"
          >
            <img src="@/assets/images/add-circle-fill.svg" alt="" style="
                width: 14px;
                height: 14px;
                vertical-align: bottom;
                margin-right: 5px;
            ">
            {{ $t("addAVectorizedModel") }}
          </el-button>
        </div>
        <ul v-if="xl_list.length" class="xl_list">
          <li class="xl_list-item" v-for="(item, index) in xl_list" :key="index">
            <div class="xl_list-item-top">
              <div class="text">{{ item.vectorName }}</div>
              <!-- <div class="tips">{{ item.status }}</div> -->
            </div>
            <div class="xl_list-item-bottom">
              <div class="row">
                <div class="label">{{ $t("vectorDimension") }}</div>
                <div class="value">{{ item.dimension }}</div>
              </div>
              <div class="row mt12">
                <div class="label">{{ $t("modelEncoding") }}</div>
                <div class="flex">
                  <div class="value">{{ item.vectorCode }}</div>
                  <!-- <img
                    class="copy"
                    @click="xl_cpoyText(item.vectorCode)"
                    src="@/assets/images/copy-line1.svg"
                  /> -->
                </div>
              </div>
            </div>
            <div class="xl_list-item-bottomTip flex centerJustify">
              <div class="boxTips">
                <div class="box"></div>
                <div class="tips">正常</div>
              </div>

              <!-- <el-dropdown
                trigger="click"
                @command="(value) => xl_handleCommand(value, item)"
              >
                <span class="el-dropdown-link">
                  <i
                    style="transform: rotate(90deg); color: #848587"
                    class="el-icon-more"
                  ></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="editeApp">
                    <img
                      style="height: 15px; margin-right: 8px"
                      src="@/assets/images/edit-line.svg"
                    />
                    <span>{{ $t("edit") }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="deleteApp">
                    <img
                      style="height: 15px; margin-right: 8px"
                      src="@/assets/images/delete-bin-4-line.svg"
                    />
                    <span>{{ $t("delete") }}</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown> -->
            </div>
          </li>
        </ul>
        <div v-if="xl_list.length" class="pagination">
          <div class="total">{{ $t("total") }}{{ xl_pageObj.total }}{{ $t("items") }}</div>
          <el-pagination
            @size-change="xl_handleSizeChange"
            @current-change="xl_handleCurrentChange"
            :current-page.sync="xl_pageObj.pageNo"
            :page-sizes="[12, 24, 36, 48]"
            :page-size="xl_pageObj.pageSize"
            background
            layout="prev, pager, next, sizes"
            :total="xl_pageObj.total"
          >
          </el-pagination>
        </div>
        <div v-else class="no-data">
          <img src="@/assets/images/no-data.png" alt="" />
          <div class="txt1">{{ $t("noFoundVectorizedModel") }}</div>
          <div class="txt2">{{ $t("addAVectorizedModel") }}</div>
          <el-button type="primary" style="font-size: 16px">{{
            $t("addAVectorizedModel")
          }}</el-button>
        </div>
      </div>
    </div>
    <!-- 部署向量化大模型 -->
    <el-dialog
      :title="xl_victorTitle"
      :visible.sync="xl_dialogVisible"
      top="3%"
      width="560px"
      height="684px"
      :modal-append-to-body="false"
      append-to-body
      custom-class="xl-model-dialog weight1"
      :before-close="xl_closeDialog"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="dialog-config">
        <el-form ref="xl_largeModelForm" :model="xl_largeModelForm" :xl_rules="xl_rules">
          <el-form-item prop="vectorName" :label="$t('nameOfVectorizedModel')">
            <el-input
              v-model="xl_largeModelForm.vectorName"
              :placeholder="$t('pleaseEnter')"
              clearable
            />
          </el-form-item>
          <el-form-item prop="vectorCode" :label="$t('modelEncoding')">
            <el-input
              v-model="xl_largeModelForm.vectorCode"
              :placeholder="$t('pleaseEnter')"
              clearable
            />
          </el-form-item>
          <el-form-item
            class="select-form"
            prop="dimension"
            :label="$t('numberOfDimensions')"
          >
            <el-radio-group v-model="xl_largeModelForm.dimension">
              <el-radio :label="768">768</el-radio>
              <el-radio :label="1024">1024</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="uri" :label="$t('callAPIInterfaceAddress')">
            <el-input v-model="xl_largeModelForm.uri" :placeholder="$t('pleaseEnter')" />
          </el-form-item>
        </el-form>
      </div>
      <div class="dialog-footer">
        <el-button plain @click="xl_closeDialog" :xl_loading="xl_addLoading">{{
          $t("cancel")
        }}</el-button>
        <el-button type="primary" @click="xl_onSubmit" :xl_loading="xl_addLoading">{{
          $t("confirm")
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getDenseVectorList,
  addDenseVector,
  updateDenseVector,
  deleteDenseVector,
} from "@/api/toolManager";

export default {
  data() {
    return {
      xl_pageObj: {
        pageNo: 1,
        pageSize: 12,
        total: 0,
      },
      xl_paramSearch: {
        keyword: "",
      },
      xl_list: [],
      xl_loading: false,

      xl_dialogVisible: false,
      xl_addLoading: false,
      xl_largeModelForm: {
        vectorName: "",
        vectorCode: "",
        uri: "",
        dimension: "",
      },
      xl_rules: {
        dimension: [
          {
            required: true,
            message: this.$t("pleaseSelect"),
            trigger: "change",
          },
        ],
        vectorName: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
        vectorCode: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
        // uri: [
        //   {
        //     required: true,
        //     message: "请输入API 域名",
        //     trigger: "blur",
        //   },
        // ],
      },
      xl_dialogTitle: "",
      xl_victorTitle: this.$t("addAVectorizedModel"),
    };
  },
  mounted() {
    this.xl_getLlmPageList();
  },
  methods: {
    xl_searchHandler() {
      this.xl_pageObj.pageNo = 1;
      this.xl_getLlmPageList();
    },
    xl_handleCurrentChange(page) {
      this.xl_pageObj.pageNo = page;
      this.xl_getLlmPageList();
    },
    xl_handleSizeChange(size) {
      this.xl_pageObj.pageSize = size;
      this.xl_getLlmPageList();
    },
    // 查询大模型信息表列表
    async xl_getLlmPageList() {
      const params = {
        keyword: this.xl_paramSearch.keyword,
        pageSize: this.xl_pageObj.pageSize,
        pageNo: this.xl_pageObj.pageNo,
      };
      this.xl_loading = true;
      const res = await getDenseVectorList(params);
      if (res.code == "000000") {
        this.xl_list = res.data.records || [];
        this.xl_pageObj.total = res.data?.totalRow || 0;
      }
      this.xl_loading = false;
    },
    xl_deployingLargeModel() {
      this.xl_dialogVisible = true;
      this.xl_victorTitle = this.$t("addAVectorizedModel");
      this.xl_dialogTitle = "创建";
      this.xl_largeModelForm = {}
    },
    xl_closeDialog() {
      this.$refs.xl_largeModelForm.resetFields();
      this.xl_dialogVisible = false;
    },
    xl_onSubmit() {
      this.$refs.xl_largeModelForm.validate((valid) => {
        if (valid) {
          if (this.xl_victorTitle == "创建向量化模型") {
            this.xl_addLlm();
          } else {
            this.xl_editLlm();
          }
        }
      });
    },
    // 新增
    async xl_addLlm() {
        this.xl_addLoading = true;
      const params = {
        ...this.xl_largeModelForm,
      };
      try {
        const res = await addDenseVector(params);
        if (res.code == "000000") {
          this.$message.success(`${this.xl_dialogTitle}成功`);
          this.xl_closeDialog();
          this.xl_searchHandler();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.xl_addLoading = false;
      }
      this.xl_addLoading = false;
    },
    async xl_editLlm() {
      this.xl_addLoading = true;
      const params = {
        ...this.xl_largeModelForm,
      };
      try {
        const res = await updateDenseVector(params);
        if (res.code == "000000") {
          this.$message.success(`${this.xl_dialogTitle}成功`);
          this.xl_closeDialog();
          this.xl_searchHandler();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.xl_addLoading = false;
      }
      this.xl_addLoading = false;
    },
    xl_exeCommandCopyText(text) {
      try {
        const t = document.createElement("textarea");
        t.nodeValue = text;
        t.value = text;
        document.body.appendChild(t);
        t.select();
        document.execCommand("copy");
        document.body.removeChild(t);
        return true;
      } catch (e) {
        console.log(e);
        return false;
      }
    },
    xl_cpoyText(content) {
      this.xl_exeCommandCopyText(content);
      this.$message({
        message: this.$t("copySuccessed"),
        type: "success",
      });
    },
    xl_handleCommand(value, item) {
      if (value == "editeApp") {
        this.xl_dialogVisible = true;
        this.xl_victorTitle = this.$t("editAVectorizedModel");
        this.xl_largeModelForm = item;
      } else {
        this.$confirm("请确认是否删除", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          confirmButtonClass: "confirm-ok",
          cancelButtonClass: "confirm-cancel",
        }).then(async () => {
          const res = await deleteDenseVector([item.id]);
          if (res.code == '000000') {
             this.$message.success('删除成功')
             this.xl_searchHandler();
          }
        });
      }
    },
  },
};
</script>
<style lang="scss">
.xl-model-dialog {
  background: #fff !important;
  border-radius: 4px;
  .el-dialog__header {
    padding: 16px 32px 16px;
    // background: linear-gradient(
    //   180deg,
    //   rgba(43, 88, 213, 0.1) 0%,
    //   rgba(43, 88, 213, 0) 100%
    // );
    //   display: block !important;
    .el-dialog__title {
      height: 24px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
    }
  }
  .el-dialog__body {
    padding: 0 32px 32px !important;
    background: #fff !important;
    .el-input__inner {
      border-radius: 2px;
    }
    .el-radio .el-radio__input.is-checked .el-radio__inner {
        border-color: #1747E5;
        background: #1747E5;
    }
    .el-input__inner:focus {
      border-color: #1747E5;
    }

    .select-form {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      .el-form-item__content {
        width: 100%;
        .el-select {
          width: 100%;
        }
      }
    }

    .checkboxOuter {
      height: 410px;
      padding: 0 8px;
      overflow-y: scroll;
    }
    .el-form-item__label {
      color: #383d47;
      font-size: 16px;
      font-weight: 500;
    }
  }
  .dialog-footer {
    // text-align: right;
    display: flex;
    justify-content: end;
    .el-button {
      border-radius: 2px;
    }
    .el-button--primary {
      background: #1747E5;
      border-color: #1747E5;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
}
.confirm-ok {
  background: #1c50fd !important;
  border-color: transparent !important;
}
.confirm-cancel {
  &:hover {
    background: none !important;
    color: #1c50fd !important;
    border-color: #1c50fd !important;
  }
}
</style>
<style lang="scss" scoped>
::v-deep .btn-prev {
  background-color: transparent !important;
  border-radius: 4px !important;
  border: 1px solid #dddfe8;
}
::v-deep .el-pagination.is-background .btn-next,
.el-pagination.is-background .btn-prev,
.el-pagination.is-background .el-pager li {
  background-color: transparent !important;
  border-radius: 4px !important;
  border: 1px solid #dddfe8;
}
::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
  background: transparent;
  border: 1px solid #3666ea;
  font-size: 16px;
  color: #3666ea;
}
::v-deep .el-pagination.is-background .el-pager li {
  background: transparent !important;
  border-radius: 4px;
  border: 1px solid #dddfe8;
}
::v-deep .el-pagination .el-select .el-input .el-input__inner {
  font-size: 16px;
  color: #272a31;
}
.xl-model {
  padding: 24px;
  height: 100%;
  &-title {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #383d47;
    line-height: 40px;
    text-align: left;
    font-style: normal;
    margin-bottom: 24px;
  }
  &-content {
    width: 100%;
    height: calc(100vh - 76px - 24px);
    display: flex;
    margin-top: 16px;
    &-left {
      height: 100%;
      width: 240px;
      padding: 0 16px 16px;
      background: #ffffff;
      border-radius: 4px;
      border: 1px solid #e1e4eb;
      margin-right: 8px;
      .label {
        padding-left: 8px;
        height: 80px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #383d47;
        line-height: 80px;
      }
      .xl_list {
        &-item {
          height: 40px;
          padding: 0 10px;
          margin-bottom: 4px;
          display: flex;
          align-items: center;
          border-radius: 4px;
          cursor: pointer;
          &:hover {
            background: rgba(63, 138, 251, 0.08);
          }
          img {
            width: 24px;
            height: 24px;
            margin-right: 8px;
          }
          .name {
            font-family: MiSans, MiSans;
            font-size: 16px;
            color: #383d47;
          }
        }
      }
      .bgColor {
        background: rgba(28, 80, 253, 0.05);
        border: 1px solid #1c50fd;
      }
    }
    &-right {
      height: 100%;
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #ffffff;
      border-radius: 8px;
      padding: 0 0 20px;
    //   border: 1px solid #e1e4eb;
      .head {
        display: flex;
        justify-content: space-between;
        height: 40px;
        width: 100%;
      }
      .xl_list {
        flex: 1;
        margin-top: 28px;
        display: flex;
        flex-wrap: wrap;
        overflow: auto;
        align-content: flex-start;

        &-item {
          margin-right: 1.3%;
          margin-bottom: 16px;
          width: 24%;
          //   height: 141px;
          background: #ffffff;
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          &:nth-child(4n) {
            margin-right: 0;
          }
          &-top {
            display: flex;
            align-items: center;
            margin: 12px 16px;
            // height: 56px;
            // border-bottom: 1px solid rgba(0, 0, 0, 0.12);
            img {
              width: 32px;
              height: 32px;
            }
            .text {
              font-family: MiSans, MiSans;
              font-weight: 550;
              font-size: 16px;
              color: #383d47;
              line-height: 24px;
            }
          }
          &-bottom {
            padding: 0px 16px 14px;
            .row {
              display: flex;
              align-items: center;
              justify-content: space-between;
              .label {
                font-family: MiSans, MiSans;
                font-size: 14px;
                color: #768094;
                width: 56px;
                white-space: nowrap;
                margin-right: 12px;
                height: 18px;
                line-height: 18px;
              }
              .flex {
                width: calc(100% - 60px);
                justify-content: flex-end;
              }
              .value {
                font-family: MiSans, MiSans;
                font-size: 14px;
                color: #383d47;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                height: 18px;
                line-height: 18px;
                // width: 200px;
                text-align: right;
              }
              .copy {
                width: 16px;
                height: 16px;
                margin-left: 4px;
                cursor: pointer;
              }
            }
            .mt12 {
              margin-top: 12px;
            }
          }
          &-bottomTip {
            margin: 0px 16px 16px;
            .boxTips {
              background: rgba(75, 190, 37, 0.1);
              display: flex;
              height: 20px;
              width: 50px;
              line-height: 20px;
              border-radius: 14px;
              text-align: center;
              padding: 0 8px;
              align-items: center;
              .tips {
                font-family: MiSans, MiSans;
                font-size: 12px;
                color: #4bbe25;
              }
              .box {
                width: 5px;
                height: 5px;
                background: #4bbe25;
                border-radius: 3px;
                margin-right: 4px;
              }
            }
          }
        }
      }
    }
  }
}

.flex {
  display: flex;
}
.centerJustify {
  align-items: center;
  justify-content: space-between;
}

::v-deep .el-dropdown-menu__item {
  display: flex;
  align-items: center;
}
::v-deep .el-radio .el-radio__input.is-checked + .el-radio__label {
    color: #494E57;
}
</style>
