<template>
  <div class="password" v-loading="loading">
    <div class="password-title">{{$t('changePassword')}}</div>
    <div class="password-content">
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item :label="$t('originalPassword')" prop="oldPw">
          <el-input
            v-model="form.oldPw"
            :placeholder="$t('pleaseEnterNewPassword')"
            style="width: 480px"
          />
        </el-form-item>
        <el-form-item :label="$t('newPassword')" prop="newPw">
          <el-input
            v-model="form.newPw"
            :placeholder="$t('pleaseEnter')"
            style="width: 480px"
          />
        </el-form-item>
        <el-form-item :label="$t('reEnterNewPassword')" prop="confirmNewPw">
          <el-input
            v-model="form.confirmNewPw"
            :placeholder="$t('pleaseEnter')"
            style="width: 480px"
          />
        </el-form-item>
      </el-form>
      <div class="button">
        <el-button type="primary" @click="onSubmit">{{ $t('confirm') }}</el-button>
        <el-button plain @click="goBack">{{ $t('cancel') }}</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import md5 from "js-md5";
import { apiChangPw } from "@/api";
export default {
  data() {
    return {
      form: {
        oldPw: "",
        newPw: "",
        confirmNewPw: "",
      },
      rules: {
        oldPw: [
          {
            required: true,
            message: "请输入原密码",
            trigger: "blur",
          },
        ],
        newPw: [
          {
            required: true,
            message: "请输入新密码",
            trigger: "blur",
          },
        ],
        confirmNewPw: [
          {
            required: true,
            message: "请再次输入新密码1",
            trigger: "blur",
          },
        ],
      },
      loading: false,
    };
  },
  methods: {
    onSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.changPw();
        }
      });
    },
    goBack() {
      this.$router.go(-1); // 返回到上一个页面
    },
    async changPw() {
      this.loading = true;
      const userId = sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))?.id
        : "";
      const oldPw = md5(this.form.oldPw);
      const newPw = md5(this.form.newPw);
      const confirmNewPw = md5(this.form.confirmNewPw);
      const params = {
        userId: userId,
        oldPw,
        newPw,
        confirmNewPw,
      };
      try {
        const res = await apiChangPw(params);
        console.log(res);
        if (res.code == "000000") {
          this.$message.success("修改成功");
          this.$router.push({ name: "login" });
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.loading = false;
        console.log(error);
      }
      this.loading = false;
    },
  },
};
</script>

<style lang="scss" scoped>
.password {
  width: 100%;
  height: 100%;
  background: #f2f5fa;
  padding: 0 12px 4px 0;
  &-title {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #494E57;
    line-height: 40px;
    text-align: left;
    font-style: normal;
    background: #fff;
    padding: 32px;
  }
  &-content {
    width: 100%;
    height: calc(100% - 80px);
    background: #fff;
    padding-top: 64px;
    display: flex;
    align-items: center;
    flex-direction: column;
    border-radius: 4px;
  }

  .button {
    width: 480px;
  }
}
</style>