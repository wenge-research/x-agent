<template>
  <el-dialog :title="type == 'edit' ? $t('edit') : $t('createModel')" :visible.sync="dialogVisibleDeepseek"
    width="560px" :before-close="cancelTemplate" class="applicationDialog" append-to-body :close-on-click-modal="false"
        :close-on-press-escape="false">
    <div style="position: relative">
      <div class="formOuter">
        <div class="uploadImg">

          <div>
            <el-form :model="appForm" :rules="rules" ref="ruleForm" class="demo-ruleForm">
              <el-form-item label="">
                <div class="type-radio-group">
                  <div v-for="item in typeList" :key="item.value" class="type-radio"
                    :class="{ active: appForm.type === item.value }" @click="handleTypeChange(item.value)">
                    <div class="title">
                      <img v-if="item.icon" :src="item.icon" alt="">
                      <span>{{ item.label }}</span>
                    </div>
                    <div class="desc">{{ item.desc }}</div>
                  </div>
                </div>
              </el-form-item>
              <div class="tips" style=" font-size: 14px; color: #36383D; ">选择要部署的模型</div>
              <el-form-item :label="`当前仅支持DeepSeek系列开源模型`" prop="componentName">
                <el-select style="width: 100%;" v-model="appForm.componentName" placeholder="请选择">
                  <template #prefix>
                    <img src="@/assets/images/deepseek.png" alt="" v-if="appForm.componentName"
                      style="width: 22px; vertical-align: middle; margin-right: 5px; position: relative; bottom: 1px;">
                </template>
                  <el-option v-for="item in deepSeekList" :key="item.value" :label="item.label" :value="item.value">
                    <img src="@/assets/images/deepseek.png" alt=""
                      style="width: 22px; vertical-align: middle; margin-right: 5px; position: relative; bottom: 1px;">
                    <span>{{ item.label }}</span>
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item
                    :label="`服务器IP`"
                    prop="ip"
                    v-if="appForm.type == 3"
                >
                    <el-input
                        v-model="appForm.ip"
                        show-word-limit
                        maxlength="100"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item
                    :label="`账号`"
                    prop="account"
                    v-if="appForm.type == 3"
                >
                    <el-input
                        v-model="appForm.account"
                        show-word-limit
                        maxlength="100"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item
                    :label="`密码`"
                    prop="password"
                    v-if="appForm.type == 3"
                >
                    <el-input
                        v-model="appForm.password"
                        show-word-limit
                        maxlength="100"
                        style="width: 100%"
                    />
                </el-form-item>
            </el-form>
            <el-table v-if="appForm.type == 2" :data="tableData" style="width: 100%;font-size: 12px;">
              <el-table-column prop="name" label="" width="100">
              </el-table-column>
              <el-table-column prop="tip1" label="配置要求">
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancelTemplate">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="confirmTemplate">一键部署</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data ()
    {
      return {
        tableData: [{
          name: 'CPU',
          tip1: '推荐使用多核心的高性能CPU，如Inteli7或AMD Ryzen7及以上。',
          tip2: '至少需要4核8线程的处理器。',
        }, {
          name: 'GPU',
          tip1: '推荐使用NVIDIA GPU，至少需要8GB显存。',
          tip2: '支持CUDA的GPU(如NVIDIA GTX1080、RTX2080、RTX3060及以上)会显著加速模型推理。',
        }, {
          name: '内存',
          tip1: '至少需委16GB RAM，推荐32GB或更高。',
        }, {
          name: '存储',
          tip1: '至少需要20GB的可用磁盘空间用于存储模型和临时文件。',
          tip2: '推荐使用SSD以提高加载速度。',
        }, {
          name: '操作系统',
          tip1: 'Windows 10或Windows 11。',
        }],
        deepSeekList: [{
          value: '1_5b',
          label: 'DeepSeek-1.5'
        }, {
          value: '7b',
          label: 'DeepSeek-7b'
        }, {
          value: '8b',
          label: 'DeepSeek-8b'
        }],
        appForm: {
          type: 2,
          componentName: "",
        },
        rules: {
          componentName: [
            {
              required: true,
              message: this.$t("pleaseEnterActivityName"),
              trigger: "blur",
            },
          ],
          ip: [
            {
              required: true,
              message: '请输入服务器IP',
              trigger: "blur",
            },
          ],
          account: [
            {
              required: true,
              message: '请输入账号',
              trigger: "blur",
            },
          ],
          password: [
            {
              required: true,
              message: '请输入密码',
              trigger: "blur",
            },
          ],
        },
        typeList: [
          { label: '本地部署', value: 2, desc: '在本地部署模型，能获得更快的模型运行速度。', icon: require("@/assets/images/workflow-select.svg") },
          { label: '服务器部署', value: 3, desc: '使用云服务器运行模型，需用户提供服务器接。', icon: require("@/assets/images/dialogue-select.svg") }
        ],
      };
    },
    props: {
      dialogVisibleDeepseek: {
        type: Boolean,
        default: false,
      },
      params: Object,
      type: String,
    },
    mounted ()
    {
      if (this.type == "edit") {
        this.appForm = JSON.parse(JSON.stringify(this.params));
      }
    },
    methods: {
      cancelTemplate ()
      {
        this.$emit("cancelApplication", false);
      },
      confirmTemplate ()
      {
        this.$emit("confirmApplication", this.appForm.componentName);
      },
      handleTypeChange (value)
      {
        if (this.type == 'edit') {
          return;
        }
        this.appForm.type = value;

      },
    },
  };
</script>

<style lang="scss" scoped>
  .applicationDialog {
    ::v-deep .el-dialog {
      border-radius: 4px;

      .el-dialog__body {
        padding: 16px 32px;
      }

      .el-dialog__header {
        background: #fff;
        border-radius: 8px 8px 0px 0px;

        .el-dialog__headerbtn {
          top: 36px;
          right: 32px;
        }
      }

      .el-dialog__footer {
        text-align: right;
        padding: 10px 32px 20px;
      }
    }
  }



  .demo-ruleForm {
    ::v-deep .el-form-item__label {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #828894;
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

  .type-radio-group {
    display: flex;
    gap: 16px;

    .type-radio {
      flex: 1;
      height: 96px;
      background: #FFFFFF;
      border-radius: 2px;
      border: 1px solid #D5D8DE;
      padding: 12px;
      cursor: pointer;

      .title {
        display: flex;
        align-items: center;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 16px;
        color: #383D47;
        line-height: 24px;
        margin-bottom: 12px;

        img {
          width: 24px;
          height: 24px;
          margin-right: 8px;
        }
      }

      .desc {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #828894;
        line-height: 18px;
      }

      &.active {
        background: rgba(28, 80, 253, 0.05);
        border: 1px solid #1747E5;
      }

    }
  }
</style>