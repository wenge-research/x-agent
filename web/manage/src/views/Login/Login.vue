<template>
  <div class="login-view" :class="[token ? 'bgColor' : '']" v-loading="loading">
    <img
      src="@/assets/images/login/login-logo.png"
      alt=""
      class="login-view-logo"
    />
    <div v-if="!token" class="content">
      <div class="logo"></div>
      <div class="con">
        <div v-if="loginType == '1'">
          <div class="chart">
            <img v-if="iszgc" :src="logoSrc" alt="" />
            <div v-else class="chart-title">{{ $t("welcomeToZhichuan") }}</div>
          </div>
          <div class="dengLuFangShi flex mb15" >
            <div class="zhangHaotitle" :class="{'active':dengLuFangShiState==1}" @click="dengLuFangShiState=1" >{{ $t("accountLogin") }}</div>
            <div class="miMaTitle" :class="{'active':dengLuFangShiState==2}" @click="dengLuFangShiState=2" >{{ $t("smsLogin") }}</div>
          </div>
          <!-- 密码登录 -->
          <div class="form" v-show="dengLuFangShiState==1" >
            <el-form :model="loginForm" :rules="loginRules" ref="loginForm">
              <el-form-item prop="username">
                <el-input
                  v-model="loginForm.username"
                  autocomplete="off"
                  prefix-icon="el-icon-user-solid"
                  placeholder="请输入账号"
                  name="username"
                ></el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  type="password"
                  v-model="loginForm.passwordStr"
                  autocomplete="off"
                  placeholder="请输入密码"
                  class="password"
                  name="password"
                >
                  <img
                    slot="prefix"
                    class="lock-fill"
                    :src="require('@/assets/images/lock-fill.png')"
                  />
                </el-input>
              </el-form-item>
            </el-form>
            <div class="xieYi mb10" >登录即代表您已阅读并同意<el-link type="primary">用户协议</el-link>和<el-link type="primary">隐私政策</el-link></div>
            <el-button type="primary" class="loginBtn" @click="handleLogin">{{
              $t("login")
            }}</el-button>
            <div class="flex justify-between font14 mt15" >
              <div class="wangJiMiMaClass" @click="passwordModuleVisible = true" >忘记密码</div>
              <div></div>
              <div class="flex" > <span>没有账号？</span> <el-link type="primary">立即注册</el-link></div>
            </div>
          </div>
          <!-- 短信登录 -->
          <div class="duanXinForm" v-show="dengLuFangShiState==2" >
            <el-form :model="smsLoginForm" :rules="smsLoginRules" ref="smsLoginForm">
              <el-form-item prop="cellPhoneNumber">
                <el-input
                  v-model="smsLoginForm.cellPhoneNumber"
                  placeholder="请输入手机号"
                  class="shouJiInput"
                >
                  <div slot="prepend" class="xuanZeQianZhui" >
                    <el-select v-model="dianHuanQianZhui"  >
                      <el-option label="+86" value="86"></el-option>
                    </el-select>
                  </div>
                </el-input>
              </el-form-item>
              <el-form-item prop="verificationCode">
                <el-input
                  v-model="smsLoginForm.verificationCode"
                  placeholder="请输入验证码"
                >
                  <el-button type="text" slot="suffix"  @click="countDown" :disabled="!isSend" :loading="yanZhenMaLoading" >{{codeName}}</el-button>
                </el-input>
              </el-form-item>
            </el-form>
            <div class="xieYi mb10 flex " >
               <div style="height: 40px;position: relative;top: 2px;"><el-checkbox v-model="shouJiChecked"></el-checkbox></div>
               <div class="flex1 ml10" style="line-height: 20px;margin-left: 4px;">未注册的手机号将自动注册，勾选即代表您已阅读并同意<el-link type="primary">用户协议</el-link>和<el-link type="primary">隐私政策</el-link></div>
            </div>
            <el-button type="primary" class="loginBtn" @click="shouJiHandleLogin">{{
              $t("login")
            }}</el-button>
            <!-- <div class="flex justify-between font14 mt15" >
              <div>忘记密码</div>
              <div class="flex" > <span>没有账号？</span> <el-link type="primary">立即注册</el-link></div>
            </div> -->
          </div>
          <!-- 注释企微登录方式 -->
          <div class="bottom">
            <span></span>
            <div>{{ $t("otherLoginMethods") }}</div>
            <span></span>
          </div>
          <div class="wx-icon" @click="switchLoginTypeHandler('2')">
            <img src="@/assets/images/login/wx-icon.svg" alt="" />
          </div>
        </div>

        <passwordModule :visible.sync="passwordModuleVisible" type="wangJi"  ></passwordModule>
        <editPhoneDialog ref="editPhoneDialog" type="wangJi"  ></editPhoneDialog>

        <div v-show="loginType == '2'" class="con-wx">
          <div class="con-wx-head">
            <iconpark-icon
              name="arrow-left-wide-line"
              color="#494E57"
              class="arrow-left-wide-line"
              @click="switchLoginTypeHandler('1')"
            ></iconpark-icon>
            <div class="con-wx-head-txt">{{ $t("enterpriseWeChatLogin") }}</div>
          </div>
          <div class="con-wx-QRcode">
            <div id="wx-reg"></div>
          </div>
        </div>
        <!-- <template v-else>
          <div v-if="true" class="con-wx">
            <div class="con-wx-head">
              <iconpark-icon
                name="arrow-left-wide-line"
                color="#494E57"
                class="arrow-left-wide-line"
                @click="switchLoginTypeHandler('1')"
              ></iconpark-icon>
              <div class="con-wx-head-txt">企微登录</div>
            </div>
            <div class="con-wx-tips">
              <img src="@/assets/images/login/wx-icon.svg" alt="" />
              企业微信扫码登录
            </div>
            <div class="con-wx-code">
              <div id="wx-reg" style="width: 100%;height: 100%"></div>
            </div>
            <div class="con-wx-text">请使用企业微信扫描二维码登录</div>
          </div>
          <div v-else class="con-wx">
            <div class="con-wx-head">
              <iconpark-icon
                name="arrow-left-wide-line"
                color="#494E57"
                class="arrow-left-wide-line"
                @click="switchLoginTypeHandler('1')"
              ></iconpark-icon>
              <div class="con-wx-head-txt">企微登录</div>
              <iconpark-icon
                name="qr-code-line"
                size="24"
                class="qr-code-line"
              ></iconpark-icon>
            </div>
            <img
              src="@/assets/images/login/wx-icon.svg"
              alt=""
              class="con-wx-avatar"
            />
            <div class="con-wx-name">张德正@深圳市前途有限公司</div>
            <div class="con-wx-btn">在企业微信桌面端打开</div>
            <div class="con-wx-back" @click="switchLoginTypeHandler('1')">
              继续在浏览器中登录访问
            </div>
          </div>
        </template> -->
      </div>
    </div>
  </div>
</template>
<script>
import { Login, analyzePortalToken,sendSmsAuthentication } from "@/api/index.js";
import editPhoneDialog from "@/views/userCenter/editPhoneDialog.vue"

import { SUCCESS_CODE } from "@/api/config";
import md5 from "js-md5";
import { decryptData } from "@/utils/tool";
import passwordModule from "@/views/Login/passwordModule.vue"

export default {
  components:{
    passwordModule, editPhoneDialog
  },
  data() {

    // 验证手机号
    var validatePhone = (rule, value, callback) => {
      const regex = /^(13[0-9]|14[01456879]|15[0-3,5-9]|16[2567]|17[0-8]|18[0-9]|19[0-3,5-9])\d{8}$/;
      let aaa = regex.test(value);
      if (aaa) {
        callback();
      }else{
        callback(new Error('请输入手机号'));
      }
    };

    return {
      yanZhenMaLoading:false,
      passwordModuleVisible: false,
      
      isSend: true,  
      codeName: "获取",
      totalTime: 300, //一般是60
      yanZhengMadingshiqi:'', //定时器

      shouJiChecked:false,
      dianHuanQianZhui: "86",
      dengLuFangShiState: 1,
      loginForm: {
        username: "",
        password: "",
        passwordStr: "",
      },
      loginRules: {
        username: [
          { required: true, message: this.$t("enterAccount"), trigger: "blur" },
        ],
        passwordStr: [
          {
            required: true,
            message: this.$t("enterPassword"),
            trigger: "blur",
          },
        ],
      },
      smsCodeKey:"",
      smsLoginForm:{
        cellPhoneNumber:"",
        verificationCode:"",
      },
      smsLoginRules:{
        cellPhoneNumber: [
          { validator: validatePhone, trigger: "blur" },
        ],
        verificationCode: [
          { required: true, message: "请输入验证码", trigger: "blur" }, 
        ]
      },

      loading: false,
      token: "",
      loginType: "1", // 1: 密码登录 2: 企微认证
    };
  },
  computed: {
    logoSrc() {
      // switch (process.env.VUE_APP_ENV) {
      //   case 'zgc':
      //     return require('@/assets/images/zgccc.png');
      //   default:
      //     return require('@/assets/images/lg-logo.png');
      // }
      return require("@/assets/images/zgccc.png");
    },
    iszgc() {
      return process.env.VUE_APP_ENV == "zgc";
    },
  },
  mounted() {
    const token = sessionStorage.getItem("loginToken");
    this.token = sessionStorage.getItem("loginToken");
    this.preloadImportantComponents()
    if (token) {
      this.login(token);
    }
  },
  methods: {
    

    // 初始企微二维码
    initWxCode() {
      const _this = this;
      const wwLogin = ww.createWWLoginPanel({
        el: "#wx-reg",
        params: {
          login_type: "CorpApp",
          appid: "ww96b49bb6ab748217",
          agentid: process.env.VUE_APP_AGENTID,
          state: "QYWX-LOGIN",
          redirect_uri: process.env.VUE_APP_REDIRECTUIR,
          redirect_type: "callback",
        },
        onCheckWeComLogin({ isWeComLogin }) {
          console.log("onCheckWeComLogin");
          console.log(isWeComLogin);
        },
        onLoginSuccess({ code }) {
          console.log("onLoginSuccess");
          console.log({ code });
          _this.loginSystem(code)
        },
        onLoginFail(err) {
          console.log("onLoginFail");
          console.log(err);
        },
      });
      console.log("wwLogin", wwLogin);
    },
    // 切换登录方式
    switchLoginTypeHandler(type) {
      this.loginType = type;
      if (type == "2") {
        this.initWxCode();
      } else {
        // 查找包含iframe的父元素
        var iframeContainer = document.getElementById("wx-reg");
        // 如果找到了父元素，检查里面是否包含iframe，并移除它
        if (iframeContainer) {
          var iframe = iframeContainer.querySelector("iframe");
          if (iframe) {
            iframeContainer.removeChild(iframe);
          }
        }
      }
    },
    preloadImportantComponents() {
      import(/* webpackChunkName: "Main" */ '@/views/Main');
      import(/* webpackChunkName: "appManage" */ '@/views/appManage/index');
      import(/* webpackChunkName: "workflowConfig" */ '@/views/workflowConfig/index');
    },
    async login(token) {
      const jumpUrl = sessionStorage.getItem("jumpUrl");
      this.loading = true;
      const res = await analyzePortalToken(token);
      const { code, data, msg } = res;
      if (SUCCESS_CODE === code) {
        if(!data.permission.length){
          this.$message({
            message: "暂无访问权限，请联系平台管理员",
            type: "error",
          });
          return false
        }
        const manageAccessToken = data.accessToken;
        const passwordExpiredTime = data.passwordExpiredTime;
        sessionStorage.setItem("manageAccessToken", manageAccessToken);
        sessionStorage.setItem("user", JSON.stringify(data.user));
        sessionStorage.setItem("permission", JSON.stringify(data.permission));
        sessionStorage.setItem("systemLogo", data.systemLogo || "");
        if (passwordExpiredTime) {
          this.$message({
            message: passwordExpiredTime,
            type: "success",
          });
          
        } else {
          // this.$message({
          //   message: "登录成功",
          //   type: "success",
          // });
        }
        if (jumpUrl && !jumpUrl.includes("/appmanage")) {
          window.location.href = jumpUrl;
        } else {
          if(!data.user.phone){
            this.$refs.editPhoneDialog.open()
          } else {
            this.$router.push({
              name: "appmanage",
            });
          }
        }
      } else {
        this.$message({
          message: msg,
          type: "error",
        });
      }
      this.loading = false;
    },
    // 登录逻辑
    async loginSystem(QRCode) {
      console.log("loginType", this.loginType)
      let params = {};
      if (this.loginType == "1") {
        if(this.dengLuFangShiState == 1){
          // 账号密码登录
          this.loginForm.password = md5(this.loginForm.passwordStr);
          params = {
            mykey: this.loginForm.password,
            username: this.loginForm.username,
          };
        }else if(this.dengLuFangShiState == 2){
          // 短信登录
          params = {
            phoneNum:this.smsLoginForm.cellPhoneNumber,
            code:this.smsLoginForm.verificationCode,
            loginType: 'sms',

            codeKey:this.smsCodeKey,
            // codeKey:"smart-agent:login_sms:bd0f081f35a541fc913b25c2a31c8963",
            
          };
        }
        
      } else {
        params = {
          loginType: 'qywx',
          code: QRCode,
        };
      }
      
      let res = await Login(params);
      const { code, data, msg } = res;
      if (SUCCESS_CODE === code) {
        if(!data.permission.length){
          this.$message({
            message: "暂无访问权限，请联系平台管理员",
            type: "error",
          });
          return false
        }
        const manageAccessToken = data.accessToken;
        const passwordExpiredTime = data.passwordExpiredTime;
        sessionStorage.setItem("manageAccessToken", manageAccessToken);
        sessionStorage.setItem("user", JSON.stringify(data.user));
        sessionStorage.setItem("permission", JSON.stringify(data.permission));
        sessionStorage.setItem("systemLogo", data.systemLogo || "");
        //首次登录的加载图片
        sessionStorage.setItem('loadingImg','true')
        if (passwordExpiredTime) {
          this.$message({
            message: passwordExpiredTime,
            type: "success",
          });
          
        } else {
          this.$message({
            message: this.$t("login") + this.$t("successed"),
            type: "success",
          });
        }
        if(params.loginType === 'qywx' && !data.user.phone){
          this.$refs.editPhoneDialog.open()
        } else {
          this.$router.push({
            name: "appmanage",
          });
        }
      } else {
        this.$message({
          message: msg,
          type: "error",
        });
      }
    },
    

    // 验证码限流
	  countDown() {
      if (!this.isSend) return
      
      // 判断手机号是否符合格式
      const regex = /^(13[0-9]|14[01456879]|15[0-3,5-9]|16[2567]|17[0-8]|18[0-9]|19[0-3,5-9])\d{8}$/;
      let aaa = regex.test(this.smsLoginForm.cellPhoneNumber);
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
        phoneNum: this.smsLoginForm.cellPhoneNumber,
      }).then((res) => {
        if(res.code === "0000001"){
          this.$message.warning(res.msg);
        }else{
          this.smsCodeKey = res.data;
        }
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
            this.totalTime = 300
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

    

    handleLogin() {
      this.$refs["loginForm"].validate((valid) => {
        if (valid) {
          this.loginSystem();
        } else {
          return false;
        }
      });
    },
    shouJiHandleLogin(){
      this.$refs["smsLoginForm"].validate((valid) => {
        console.log(valid,"shouJiHandleLogin");
        
        if (valid) {
          if(!this.shouJiChecked){
            this.$message({
              message: '请勾选同意用户协议和隐私政策',
              type: "warning",
            });
            return false;
          }
          this.loginSystem();
        } else {
          return false;
        }
      });
    }
  },
};
</script>
<style>
.login-view iframe {
  width: 304px !important;
}
</style>
<style lang="scss" scoped>

.wangJiMiMaClass{
  cursor: pointer;
  user-select: none;
  color: #494E57;
  font-size: 14px;
}

::v-deep .shouJiInput .el-input-group__prepend{
  background-color: #fff;
}

.login-view {
  width: 100%;
  height: 100%;
  background: url("~@/assets/images/login/login-bg.png") no-repeat center;
  background-size: cover;
  display: flex;
  justify-content: center;
  // align-items: center;
  position: relative;

  &-logo {
    height: 56px;
    position: absolute;
    top: 64px;
    left: 80px;
  }

  .content {
    width: 1200px;
    // height: 520px;
    // height: 560px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-radius: 8px;

    .logo {
      width: 720px;
      // background: url("~@/assets/images/login-logo.png") no-repeat;
      // background-size: 100% 100%;
    }

    .con {
      // flex: 1;
      background: #ffffff;
      padding: 0 48px;
      box-sizing: border-box;
      box-shadow: 0px 10px 24px 0px rgba(81, 91, 102, 0.08);
      border-radius: 4px;
      height: 560px;
      width: 400px;
      .xieYi{
        font-size: 14px;
        color: #494E57;
      }
      &-wx {
        width: 304px;
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;

        &-QRcode {
          width: 100%;
          height: calc(100% - 88px);
          display: flex;
          align-items: center;
          justify-content: center;
          #wx-reg {
            margin-top: 8px;
            width: 100%;
          }
        }
        &-head {
          margin-top: 40px;
          width: 100%;
          display: flex;
          align-items: center;
          .arrow-left-wide-line {
            margin: 0 16px;
            cursor: pointer;
          }
          .qr-code-line {
            margin-left: auto;
            cursor: pointer;
          }
          &-txt {
            margin-left: 80px;
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 20px;
            color: #494e57;
            line-height: 40px;
            white-space: nowrap;
          }
        }
        &-tips {
          margin: 34px 0;
          display: flex;
          align-items: center;
          justify-content: center;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 16px;
          color: #494e57;
        }
        &-code {
          margin: 0 auto;
          padding: 6px;
          width: 248px;
          height: 248px;
          background: #ffffff;
          border-radius: 4px;
          border: 1px solid #d5d8de;
        }
        &-text {
          margin-top: 24px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #828894;
          line-height: 20px;
          text-align: center;
        }
        &-avatar {
          margin: 68px 0 20px;
          width: 80px;
          height: 80px;
          border-radius: 8px;
        }
        &-name {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 18px;
          color: #494e57;
          line-height: 28px;
          text-align: center;
        }
        &-btn {
          margin: 82px 0 24px;
          width: 232px;
          height: 44px;
          line-height: 44px;
          text-align: center;
          background: #267ef0;
          border-radius: 4px;
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 16px;
          color: #ffffff;
          cursor: pointer;
        }
        &-back {
          width: 154px;
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #267ef0;
          line-height: 20px;
          cursor: pointer;
        }
      }

      .chart {
        // width: 196px;
        height: auto;
        margin: 48px 0 16px 0;
        box-sizing: border-box;
        text-align: center;

        img {
          height:75px;
          // width: 100%;
        }
        &-title {
          height: 40px;
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 28px;
          color: #494e57;
          line-height: 40px;
          text-align: left;
        }
      }
      .dengLuFangShi{
        font-size: 16px;
        
        color: #494E57;
        font-family: MiSans, MiSans;
        div{
          margin-right: 20px;
          line-height: 24px;
          padding-bottom: 5px;
          cursor: pointer;
          user-select: none;
        }
        .active{
          color: #1747E5;
          border-bottom: 2px solid #1747E5
        }
      }
      
      .form {
        display: flex;
        width: 304px;
        height: 264px;
        flex-direction: column;
        .password {
          ::v-deep .el-input__prefix {
            left: 9px;
            top: 2px;
          }
        }
        ::v-deep .el-input__inner {
          border: 1px solid #b4bccc;
          border-radius: 2px;
        }
        ::v-deep .el-input__inner:hover {
          border: 1px solid #1747e5;
        }
        .lock-fill {
          width: 15px;
        }
        
        .loginBtn {
          margin-top: 30px;
          height: 40px;
          border: 1px solid #fff;
          background: #1747e5;
          border-radius: 2px;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 16px;
          color: #ffffff;
        }
      }

      .duanXinForm {
        display: flex;
        
        width: 304px;
        height: 264px;
        flex-direction: column;
        .xuanZeQianZhui{
          width: 50px;
        }
        .password {
          ::v-deep .el-input__prefix {
            left: 9px;
            top: 2px;
          }
        }
        ::v-deep .el-input__inner {
          border: 1px solid #b4bccc;
          border-radius: 2px;
        }
        ::v-deep .el-input__inner:hover {
          border: 1px solid #1747e5;
        }
        .lock-fill {
          width: 15px;
        }
        
        .loginBtn {
          margin-top: 10px;
          height: 40px;
          border: 1px solid #fff;
          background: #1747e5;
          border-radius: 2px;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 16px;
          color: #ffffff;
        }
      }

      .bottom {
        margin: 16px 0 16px;
        display: flex;
        align-items: center;
        width: 100%;
        height: 20px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 20px;
        span {
          flex: 1;
          height: 1px;
          background: rgba(0, 0, 0, 0.12);
        }
        div {
          margin: 0 16px;
        }
      }

      .wx-icon {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto;
        background: #ffffff;
        border: 1px solid #d5d8de;
        border-radius: 50%;
        cursor: pointer;
      }

      .el-input {
        width: 304px;
      }
    }
  }
  .login-content {
    width: 1200px;
    height: 760px;
  }
}
.bgColor {
  background: #fff;
}

.el-icon-user-solid {
  color: #b4bccc;
}
</style>
