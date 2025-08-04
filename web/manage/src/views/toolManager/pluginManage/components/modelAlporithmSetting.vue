<template>
  <div>
    <div class="save-form">
      <el-form
        :model="appForm"
        ref="formRefs"
        :rules="rules"
        @submit.prevent
        class="variable-form"
      >
        <el-form-item :label="$t('apiRelativePath')" prop="pathApiLi">
          <el-input
            size="medium"
            :placeholder="$t('pleaseInput')"
            v-model="appForm.pathApiLi"
            disabled
          >
            <el-button
              slot="append"
              icon="el-icon-copy-document"
              @click="cpoyText(appForm.pathApiLi)"
            ></el-button>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('pluginType')" prop="category">
          <el-select size="medium" v-model="appForm.category" style="width: 100%">
            <el-option
              v-for="(item, index) in optionsType"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('documentationUrl')" prop="docFileUrl">
          <el-input
            size="medium"
            v-model="appForm.docFileUrl"
            :placeholder="$t('pleaseInput')"
          >
            <i slot="prefix" class="el-input__icon el-icon-link"></i>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('authenticationMethod')" prop="authMethod">
          <el-radio-group size="medium" v-model="appForm.authMethod">
            <el-radio label="0">{{ $t("noAuthorizationRequired") }}</el-radio>
            <el-radio label="1">API Key</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('generateSecretKey')" prop="required">
          <div class="secret-list">
            <el-button @click="addSecretKey" icon="el-icon-plus" type="text" style="color: #1747E5;position:absolute;right: 0px;top:-40px;">
            {{ $t("addSecretKey") }}
          </el-button>
            <div v-for="(item, index) in secretAuthUserList" :key="index" class="list">
              <div>
                <span class="name"> 秘钥位置 </span><span class="value"> Header </span>
              </div>
              <div class="flex-center">
                <span class="name"> 秘钥值 </span>
                <div :class="['value', 'value1', !item.showSecret ? 'isSecret' : '']" :title="item.secretKey">
                  {{ item.secretKey }}
                </div>
                <img
                  v-if="item.showSecret"
                  style="width: 18px; height: 18px; margin-left: 4px"
                  :src="require('@/assets/images/eye-line.svg')"
                  @click="isShowSecretClick(item)"
                />
                <img
                  v-else
                  style="width: 18px; height: 18px; margin-left: 4px"
                  :src="require('@/assets/images/eye-off-line.svg')"
                  @click="isShowSecretClick(item)"
                />
              </div>
              <div>
                <span class="name"> 备注 </span
                ><span class="value">
                  {{ item.remark }}
                </span>
              </div>
            </div>
          </div>
          
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <el-button style="border-radius: 2px;" plain @click="closeDialog" :loading="addLoading">{{
          $t("cancel")
        }}</el-button>
        <el-button style="border-radius: 2px;background: #1747E5;" type="primary" @click="onSubmit" :loading="addLoading">{{
          $t("confirm")
        }}</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    appForm: Object,
    secretAuthUserList: Array,
  },
  components: {},
  data() {
    return {
      panel: {},
      rules: {},
      optionsType: [
        {
          label: this.$t("addressParsing"),
          value: "1",
        },
        {
          label: this.$t("complaintLabeling"),
          value: "2",
        },
        {
          label: this.$t("modelInstruction"),
          value: "3",
        },
        {
          label: this.$t("other"),
          value: "4",
        },
      ],
      addLoading: false,
    };
  },
  watch: {
    appForm(val) {
      this.appForm.pathApiLi = process.env.VUE_APP_API_NEW + val.pathApi;
      this.$nextTick(() => {
        this.$forceUpdate();
      });
    },
  },
  mounted() {
    this.appForm.pathApiLi = process.env.VUE_APP_API_NEW + this.appForm.pathApi;
    this.$nextTick(() => {
      this.$forceUpdate();
    });
  },
  methods: {
    addSecretKey() {
      this.$emit("addSecret");
    },
    onSubmit() {
      this.$emit("saveAndPublishClick", this.appForm);
    },
    closeDialog() {
      this.$emit("closeDialogApi");
    },
    cpoyText(content) {
      this.exeCommandCopyText(content);
      this.$message({
        message: this.$t("copySuccessed"),
        type: "success",
      });
    },
    exeCommandCopyText(text) {
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
    isShowSecretClick(item) {
      console.log(item, 9090);
      item.showSecret = !item.showSecret;
      this.$nextTick(() => {
        this.$forceUpdate();
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.variable-form {
  ::v-deep .el-form-item {
    display: flex;
    flex-direction: column;
    .el-form-item__label {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #828894;
      line-height: 20px;
      text-align: left;
      margin-bottom: 10px;
    }
    .el-radio .el-radio__input.is-checked + .el-radio__label {
    color: #494E57;
}
  }
}

.secret-list {
    position: relative;
  .list {
    background: #fff;
    border-radius: 2px;
    border: 1px solid #D5D8DE;
    margin-bottom: 12px;
    padding: 12px 0 12px 12px;
  }
  .name {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #383d47;
    width: 100px;
    display: inline-block;
  }
  .isSecret {
    text-security: disc;
    -webkit-text-security: disc;
  }
  .value {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #828894;
  }
  .value1 {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
    width: calc(100% - 140px);
  }
}

.dialog-footer {
  position: absolute;
  bottom: 32px;
  right: 32px;
}

.flex-center {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

</style>
