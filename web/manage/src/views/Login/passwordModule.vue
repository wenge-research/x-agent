<template>
  


    <el-dialog
      :visible.sync="dialogVisible"
      @close="handleClose"
      width="480px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      
    >
      <div class="tanKuangNeiRong" >


        <!-- 忘记密码 -->
        <div class="wangJiMiMaBox" v-if="type == 'wangJi'" >
          <div class="xiaoBiaoTi mb20" >{{ $t("forgotPassword") }}</div>
          <div>
            <el-form :model="wangJiMiMaForm" :rules="wangJiMiMaRules" ref="wangJiMiMaFormDom">

              <el-form-item prop="cellPhoneNumber">
                <el-input
                  v-model="wangJiMiMaForm.cellPhoneNumber"
                  :placeholder="$t('pleaseEnterYourMobilePhoneNumber')"
                  class="shouJiInput"
                >
                  <div slot="prepend" class="xuanZeQianZhui " >
                    <el-select v-model="dianHuanQianZhui"  >
                      <el-option label="+86" value="86"></el-option>
                    </el-select>
                  </div>
                </el-input>
              </el-form-item>
              <el-form-item prop="verificationCode">
                <el-input
                  v-model="wangJiMiMaForm.verificationCode"
                  :placeholder="$t('pleaseEnterTheVerificationCode')"
                >
                  <el-button type="text" slot="suffix" @click="countDown" :disabled="!isSend" :loading="yanZhenMaLoading" >{{codeName}}</el-button>
                </el-input>
              </el-form-item>

              <el-form-item  prop="newPw">
                <el-input
                  type="password"
                  v-model="wangJiMiMaForm.newPw"
                  :placeholder="$t('pleaseEnterNewPassword')"
                  show-password
                />
              </el-form-item>
              <el-form-item  prop="confirmNewPw">
                <el-input
                  type="password"
                  v-model="wangJiMiMaForm.confirmNewPw"
                  :placeholder="$t('reEnterNewPassword')"
                  show-password
                />
              </el-form-item>
            </el-form>
          </div>
          <div class="xiaoYiHaoFuWenZi" >
            {{ $t("passwordRequirement") }}
          </div>
          <div class="mt25" >
            <div class="mb15"><el-button style="width:100%;" type="primary" @click="zhaoHuiMiMaConfirm" >{{ $t('confirm') }}</el-button></div>
            <div><el-button style="width:100%;" plain @click="handleClose" >{{ $t('cancel') }}</el-button></div>
          </div>
        </div>


        <!-- 首次登录设置密码 -->
        <div class="shouCiDengLuSheZhiMiMaBox" v-if="type == 'shouCi'" >
          <div class="xiaoBiaoTi mb10" >{{ $t("setPasswordForFirstLogin") }}</div>
          <div class="xiaoYiHaoFuWenZi mb15" >{{ $t("YouCanLoginWithPasswordAfterSettingIt") }}</div>
          <div>
            <el-form :model="shouCiDengLuForm" :rules="shouCiDengLuRules" ref="shouCiDengLuFormDom">
              <el-form-item  prop="newPw">
                <el-input
                  type="password"
                  v-model="shouCiDengLuForm.newPw"
                  :placeholder="$t('pleaseEnterNewPassword')"
                  show-password
                />
              </el-form-item>
              <el-form-item  prop="confirmNewPw">
                <el-input
                  type="password"
                  v-model="shouCiDengLuForm.confirmNewPw"
                  :placeholder="$t('reEnterNewPassword')"
                  show-password
                />
              </el-form-item>
            </el-form>

          </div>
          <div class="xiaoYiHaoFuWenZi mb35" >{{ $t("passwordRequirement") }}</div>
          <div>
            <el-button style="width:100%;" type="primary" @click="shouCiSheZhiSubmit" >{{ $t('confirm') }}</el-button>
          </div>
        </div>

        
        <!-- 修改密码 -->
        <div class="passwordModule-content" v-if="type == 'xiuGai'" >
          <div class="flex justify-between" >
            <div class="xiaoBiaoTi mb20" >{{ $t("changePassword") }}</div>
            <!-- <div class="xiaoYiHaoFuWenZi wangJiMiMa" @click="qieHuanWangJi" >忘记密码</div> -->
          </div>
          <el-form :model="form" :rules="rules" ref="form">
            <el-form-item prop="oldPw">
              <el-input
                type="password"
                v-model="form.oldPw"
                :placeholder="$t('pleaseEnterOriginalPassword')"
                show-password
              />
            </el-form-item>
            <el-form-item  prop="newPw">
              <el-input
                type="password"
                v-model="form.newPw"
                :placeholder="$t('pleaseEnterNewPassword')"
                show-password
              />
            </el-form-item>
            <el-form-item  prop="confirmNewPw">
              <el-input
                type="password"
                v-model="form.confirmNewPw"
                :placeholder="$t('pleaseReEnterOriginalPassword')"
                show-password
              />
            </el-form-item>
          </el-form>
          <div class="xiaoYiHaoFuWenZi" >
            {{ $t("passwordRequirement") }}
          </div>
          <div class="mt25" >
            <div class="mb15"><el-button style="width:100%;" type="primary" @click="onSubmit">{{ $t('confirm') }}</el-button></div>
            <div><el-button style="width:100%;" plain @click="handleClose" >{{ $t('cancel') }}</el-button></div>
          </div>
        </div>


      </div>
      
    </el-dialog>



    



  
</template>

<script>
import md5 from "js-md5";
import { apiChangPw } from "@/api";
import { addUser } from "@/api/app.js";
import { sendSmsAuthentication , retrievePassword } from "@/api/index.js";
export default {
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    type:{
      type: String,
      default: "xiuGai"  // 类型  wangJi 忘记密码、xiuGai 修改密码、shouCi 首次登录
    }
  },
  watch: {
    visible(newVal) {
      this.dialogVisible = newVal;
    }
  },
  data() {

    // 验证手机号
    var validatePhone = (rule, value, callback) => {
      const regex = /^(13[0-9]|14[01456879]|15[0-3,5-9]|16[2567]|17[0-8]|18[0-9]|19[0-3,5-9])\d{8}$/;
      let aaa = regex.test(value);
      if (aaa) {
        callback();
      }else{
        callback(new Error('请输入正确的手机号'));
      }
    };

    // 验证密码
    var validateMiMa = (rule, password, callback) => {

      // 规则 1: 长度不少于 8 个字符
      const lengthRegex = /.{8,}/;
      // 规则 2: 仅由数字、英文字母或英文符号组成
      const validCharsRegex = /^[a-zA-Z0-9\W_]+$/;
      // 规则 3: 包含至少两种类型
      const digitRegex = /\d/;
      const letterRegex = /[a-zA-Z]/;
      const symbolRegex = /[\W_]/;

      // 检查长度
      if (!lengthRegex.test(password)) {
          callback(new Error('密码不符合要求'));
      }
      // 检查字符范围
      if (!validCharsRegex.test(password)) {
          callback(new Error('密码不符合要求'));
      }

      let typeCount = 0;
      if (digitRegex.test(password)) {
          typeCount++;
      }
      if (letterRegex.test(password)) {
          typeCount++;
      }
      if (symbolRegex.test(password)) {
          typeCount++;
      }

      // 检查是否包含至少两种类型
      let bbb = typeCount >= 2;
      if(bbb){
        callback(); 
      }else{
        callback(new Error('密码不符合要求'));
      }

    };


    return {

      isSend: true,  
      codeName: "获取",
      totalTime: 60, //一般是60
      yanZhengMadingshiqi:'', //定时器

      yanZhenMaLoading:false,

      smsCodeKey:"",
      dianHuanQianZhui: "86",
      
      wangJiMiMaForm: {
        cellPhoneNumber: "",
        verificationCode: "",
        code:"",
        newPw: "",
        confirmNewPw: "", 
      },
      wangJiMiMaRules:{
        newPw: [
          {
            validator: validateMiMa,
            trigger: "blur",
          },
          
        ],
        
        confirmNewPw: [
          {
            required: true,
            message: "请再次输入新密码",
            trigger: "blur",
          },
        ],
        cellPhoneNumber: [
          { validator:validatePhone, trigger: "blur" },
        ],
        verificationCode: [
          { required: true, message: "请输入验证码", trigger: "blur" }, 
        ]
      },
      shouCiDengLuForm: {
        newPw: "",
        confirmNewPw: "",
      },
      shouCiDengLuRules: {
        newPw: [
          {
            validator: validateMiMa,
            trigger: "blur",
          },
        ],
        confirmNewPw: [
          {
            required: true,
            message: "请再次输入新密码",
            trigger: "blur",
          },
        ],
      },
      dialogVisible: this.visible,
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
            message: "请再次输入新密码",
            trigger: "blur",
          },
        ],
      },
      loading: false,
    };
  },
  methods: {

    qieHuanWangJi(){
      this.$emit('qieHuanBanKuai','wangJi');
    },

    handleClose() {
      this.$emit('update:visible', false);
    },
    zhaoHuiMiMaConfirm(){
      this.$refs.wangJiMiMaFormDom.validate((valid) => {
        if (valid) {
          this.wangJiMiMaFn();
        }
      });
    },
    async wangJiMiMaFn(){

      if(this.wangJiMiMaForm.newPw!== this.wangJiMiMaForm.confirmNewPw){
        this.$message.warning("两次密码不一致");
        return;
      }
      const password = md5(this.wangJiMiMaForm.newPw);
      const confirmPassword = md5(this.wangJiMiMaForm.confirmNewPw);
      let params = {
        phoneNum:this.wangJiMiMaForm.cellPhoneNumber,
        code:this.wangJiMiMaForm.verificationCode,
        password:password,
        confirmPassword:confirmPassword,

        codeKey:this.smsCodeKey,
        // codeKey:"smart-agent:login_sms:bd0f081f35a541fc913b25c2a31c8963",
        
      }

      try {
        const res = await retrievePassword(params);
        console.log(res);
        if (res.code == "000000") {
          this.$message.success("操作成功");
          this.handleClose();
          this.$router.push({ name: "login" });
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        console.log(error);
      }

    },
    // 验证码限流
	  countDown() {
      if (!this.isSend) return
      
      // 判断手机号是否符合格式
      const regex = /^(13[0-9]|14[01456879]|15[0-3,5-9]|16[2567]|17[0-8]|18[0-9]|19[0-3,5-9])\d{8}$/;
      let aaa = regex.test(this.wangJiMiMaForm.cellPhoneNumber);
      if (!aaa) {
        this.$message({
          message: '请输入正确的手机号',
          type: "warning",
        });
        return false;
      }


      // 先进行截止
      this.isSend = false;

      this.yanZhenMaLoading = true;
      // 获取手机号的接口
      sendSmsAuthentication({
        phoneNum: this.wangJiMiMaForm.cellPhoneNumber,
      }).then((res) => {
        
        this.smsCodeKey = res.data;

        // 获取验证码成功后，进行倒计时
        this.codeName = this.totalTime + 'S'
        if(this.yanZhengMadingshiqi){
          clearInterval(this.yanZhengMadingshiqi)
        }
        this.yanZhengMadingshiqi = setInterval(() => {
          this.totalTime--
          this.codeName = this.totalTime + 'S'
          if (this.totalTime < 1) {
            clearInterval(this.yanZhengMadingshiqi)
            this.codeName = '获取'
            this.totalTime = 60
            this.isSend = true 
          }
        }, 1000)


      }).catch((err) => {
        this.$message({
          message: '接口异常',
          type: "error", 
        })
        this.isSend = true;
      }).finally(() => {
        this.yanZhenMaLoading = false;
      });
      
      
    },

    shouCiSheZhiSubmit() {
      this.$refs.shouCiDengLuFormDom.validate((valid) => {
        if (valid) {
          this.shouCiSheZhiMiMa();
        }
      });
    },

    // 首次登录设置密码
    async shouCiSheZhiMiMa(){
      this.loading = true;
      const userId = sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))?.id
        : "";

      if(this.shouCiDengLuForm.newPw !== this.shouCiDengLuForm.confirmNewPw){
        this.$message.warning("两次密码不一致");
        this.loading = false;
        return;
      }
      const password = md5(this.shouCiDengLuForm.newPw);
      let userObj = JSON.parse(sessionStorage.getItem("user"));
      const params = {
        ...userObj,
        userId: userId,
        password: password,
      };
      try {
        let res = await addUser(params);
        // const res = {};
        console.log(res);
        if (res.code == "000000") {
          this.$message.success("设置成功");

          // 删除首次登录标识
          delete userObj.firstLoginFlag;
          sessionStorage.setItem("user", JSON.stringify(userObj));
          this.handleClose();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.loading = false;
        console.log(error);
      }
      this.loading = false;


    },
    
    onSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
			// 判断密码是否符合格式
		  const passw = /^(?=.*[A-Za-z])(?=.*[\d\W])(?=.*[\W\d][^\w\s]*([A-Za-z]|\d))[\w\W]{8,}$/;
		  let aaa = passw.test(this.form.newPw);
		  if (!aaa) {
			this.$message({
			  message: '密码不符合规则，请重新输入',
			  type: "warning",
			});
			return false;
		  }
			if(this.form.newPw !== this.form.confirmNewPw){
			  this.$message.warning("两次密码不一致");
			  this.loading = false;
			  return;
			}
			
          this.changPw();
        }
      });
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
.passwordModule {
  

}

.xiaoBiaoTi{
  font-family: MiSans, MiSans;
  font-size: 18px;
  color: #494E57;
}
.xiaoYiHaoFuWenZi{
  font-size: 12px;
  color: #828894;
  line-height: 16px;
}
.tanKuangNeiRong{
  padding: 0px 40px 20px;
}
.xuanZeQianZhui{
  width: 40px;
  ::v-deep .el-input-group__prepend{
    background-color: #fff;
  }
  
}

::v-deep .shouJiInput .el-input-group__prepend{
  background-color: #fff;
}

// 修改密码
.passwordModule-content {
  .wangJiMiMa{
    cursor: pointer;
    user-select: none;
  }
}

</style>