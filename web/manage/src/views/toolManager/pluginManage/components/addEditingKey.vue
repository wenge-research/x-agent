<template>
  <div>
    <div class="save-form">
      <el-form
        :model="panel"
        ref="formRefs"
        :rules="rules"
        @submit.prevent
        class="variable-form"
      >
        <el-form-item :label="$t('secretKeyLocation')" prop="header">
          <el-radio-group size="medium" v-model="panel.header">
            <el-radio :label="1">header</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('userAllName')" prop="name">
          <el-input size="medium" :placeholder="$t('pleaseInput')" v-model="panel.name">
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('contactInformation')" prop="mobile">
          <el-input size="medium" :placeholder="$t('pleaseInput')" v-model="panel.mobile">
          </el-input>
        </el-form-item>

        <!-- <el-form-item :label="$t('secretKeyValue')" prop="secretKey">
          <el-input
            size="medium"
            v-model="panel.secretKey"
            :placeholder="$t('pleaseInput')"
            disabled
            :class="[showSecret ? '' : 'isNoShowSecret']"
          >
            <div slot="append" @click="showSecret = !showSecret">
              <img
                v-if="showSecret"
                style="width: 18px; height: 18px; margin-left: 4px"
                :src="require('@/assets/images/eye-line.svg')"
              />
              <img
                v-else
                style="width: 18px; height: 18px; margin-left: 4px"
                :src="require('@/assets/images/eye-off-line.svg')"
              />
            </div>
          </el-input>
        </el-form-item> -->
        <el-form-item :label="$t('secretKeyExpirationTime')" prop="expireTime">
          <el-date-picker
            style="width: 100%"
            v-model="panel.expireTime"
            type="datetime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="YYYY-MM-DD"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item :label="$t('remark')" prop="remark">
          <el-input
            size="medium"
            v-model="panel.remark"
            type="textarea"
            :rows="4"
            :placeholder="$t('pleaseInput')"
          >
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <div class="dialog-footer">
      <el-button plain @click="closeDialog" :loading="addLoading">{{
        $t("cancel")
      }}</el-button>
      <el-button type="primary" style="background-color: #1747E5;border-radius: 2px;" @click="onSubmit" :loading="addLoading">{{
        $t("confirm")
      }}</el-button>
    </div>
  </div>
</template>

<script>
import { addModelPluginApiAuthUser, updateModelPluginApiAuthUser } from "@/api/workflow";
export default {
  props: {
    appForm: Object,
    addOrUpdate: String,
  },
  components: {},
  data() {
    return {
      panel: {
        header: 1,
      },
      rules: {
        name: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
        mobile: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
        expireTime: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
      },
      addLoading: false,
      showSecret: false,
    };
  },
  watch: {
    addOrUpdate(val) {
        console.log(val, 888)
      if (val == "add") {
        this.panel = {};
        this.panel.header = 1;
      } else {
        this.panel = JSON.parse(JSON.stringify(this.appForm));
        this.panel.header = 1;
      }
    },
  },
  mounted() {
    this.panel = JSON.parse(JSON.stringify(this.appForm));
    this.panel.header = 1;
    if (this.addOrUpdate == "add") {
        console.log(this.addOrUpdate, 1888)
        this.panel = {};
        this.panel.header = 1;
    }
  },
  methods: {
    addSecretKey() {
      this.$emit("addSecret");
    },
    onSubmit() {
      this.$refs.formRefs.validate((valid) => {
        if (valid) {
          if (this.addOrUpdate == "add") {
            console.log(111, this.appForm, this.panel);
            addModelPluginApiAuthUser({
              modelPluginApiId: this.appForm.modelPluginApiId,
              ...this.panel,
              enableFlag: 1
            })
              .then((res) => {
                if (res.code == "000000") {
                  this.$emit("isAddSecretClick");
                }
              })
              .catch((err) => {});
          } else {
            console.log(111, this.appForm, this.panel);
            updateModelPluginApiAuthUser({
              modelPluginApiId: this.appForm.modelPluginApiId,
              ...this.panel,
            })
              .then((res) => {
                if (res.code == "000000") {
                  this.$emit("isAddSecretClick");
                }
              })
              .catch((err) => {});
          }
        }
      });
    },
    closeDialog() {
      this.$emit("closeDialogSecret");
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
      color: #383d47;
      line-height: 20px;
      text-align: left;
      margin-bottom: 10px;
    }
  }
}

.isNoShowSecret {
  text-security: disc;
  -webkit-text-security: disc;
}
.dialog-footer {
    text-align: right;
}
</style>
