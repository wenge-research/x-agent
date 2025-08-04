<template>
  <el-container style="height: 100%; position: relative">
    <!-- <div class="first-loading" v-if="loadingImg">
     <div class="headLoading">
      <img src="@/assets/images/logo.svg" class="firstImg">
     <div class="loadingText">欢迎使用智川 X-Agent</div>
     <div class="noticeText">首次登录资源加载，请耐心等候</div>
     </div>
    <el-progress :percentage="progress" show-text='false' :stroke-width="10" class="loading"></el-progress>
    </div> -->
    <el-aside :width="`${width}px`">
      <div class="nav">
        <div style="margin: 0 10px;" :class="show ? 'settings' : ''" @mouseover="show = (width == '240') ? true : false"
          @mouseleave="show = false">
          <div v-if="show">
            <span style="color: #494E57;font-size: 16px;">{{ $t("settings") }}</span>
            <!-- <div style="margin-top: 16px;color: #828894;font-size: 14px;">
                    <div style="margin: 16px 0 10px;">{{$t("viewLanguage")}}</div>
                    <LanguageSwitcher
                        v-show="width == '240'"
                        v-permission="'switchLanguage'"
                    ></LanguageSwitcher>    
                </div> -->
            <div style="padding-top: 10px;">
              <ul class="operate">
                <li @click="operation">
                  <!-- <img src="@/assets/images/file-history-line1.svg" /> -->
                  <iconpark-icon name="file-history-line" size="18" class="iconpark"></iconpark-icon>
                  {{ $t('operationLog') }}
                </li>
                <li @click="userInfoZx">
                  <!-- <img src="@/assets/images/shopping-bag-4-line.svg" /> -->
                  <i class="el-icon-s-custom" style="margin-right: 10px;"></i>
                  {{ $t('userInfoZx') }}
                </li>
                <li @click="quite">
                  <!-- <img src="@/assets/images/logout-box-r-line.svg" /> -->
                  <iconpark-icon name="logout-box-r-line" color="#FF0000" size="18" class="iconpark"></iconpark-icon>
                  {{ $t('logout') }}{{ $t("login") }}
                </li>
              </ul>
            </div>
          </div>

          <div class="login-box" :style="{ 'padding-left': !show ? '20px' : '10px','margin':!show?'12px 12px 16px 12px':'12px 10px 16px 10px' }">
            <div class="info">
              <div class="head" :style="{background: !show?'#fff':'#F3F3F3','margin-left':!show?'8px':'0'}">
                <!-- <i class="el-icon-s-custom"></i> -->
                 <iconpark-icon name="user-2-line" color="#C9CDD4" size="14"></iconpark-icon>
              </div>
              <div v-show="width == '240'" class="message">
                <!-- {{ $t("welcome") }} -->
                <div class="identity">{{ username }}</div>
              </div>
            </div>
            <!-- <transition class="el-zoom-in-top"> -->
            <div v-show="width == '240'" class="logout">
              <!-- 退出 -->
              <!-- <img src="@/assets/images/loginout.svg" @click="quite"> -->
              <!-- <iconpark-icon
                    name="list-check"
                    size="18"
                    color="#1C50FD"
                    class="hide"
                ></iconpark-icon> -->
              <!-- <iconpark-icon v-show="!show" :name="!show ? 'menu-2-fill' : 'close-line'" size="18" class="show"></iconpark-icon> -->
              <iconpark-icon name="arrow-up-s-line" v-show="!show"  size="18" class="show"></iconpark-icon>

            </div>
            <!-- </transition> -->

            <!-- <LanguageSwitcher
                v-show="width == '240'"
                v-permission="'switchLanguage'"
            ></LanguageSwitcher> -->
          </div>
        </div>
        <h3>
          <img
            class="logo-new"
            v-show="!systemLogo && !isFold"
            :src="require('../assets/images/logo-new.png')"
          />
          <img
            v-show="!systemLogo && isFold"
            class="fold"
            @click="foldHandler(false)"
            :src="require('../assets/images/logo-single.svg')"
          />
          <img v-show="systemLogo" class="logo-new logo-new-height" :src="systemLogo" />
          
          <!-- <iconpark-icon
            v-if="!isFold && !systemLogo"
            name="indent-decrease"
            title="收起"
            @click="foldHandler(true)"
          ></iconpark-icon> -->
        </h3>
        <transition name="el-fade-in-linear">
          <common-aside ref="commonAside"></common-aside>
        </transition>
      </div>
      <passwordModule :visible.sync="passwordModuleVisible" :type="passwordXiuGaiLeiXing"
        @qieHuanBanKuai="miMaQieHuanBanKuai"></passwordModule>
    </el-aside>
    <el-container>
      <!-- <el-header><common-header></common-header></el-header> -->
      <el-main>
        <div class="main no-scrollbar">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { logout } from "@/api";
import CommonHeader from "../components/CommonHeader";
import CommonAside from "../components/CommonAside";
import LanguageSwitcher from "../components/LanguageSwitcher";
import { mapActions } from 'vuex';
import passwordModule from "@/views/Login/passwordModule.vue"
import {getOauthTenantInfoByTenantId} from "@/api/app";

export default {
  components: {
    CommonHeader,
    CommonAside,
    LanguageSwitcher,
    passwordModule
  },
  created() {
    this.initUser();
  },
  data() {
    return {
      //首次加载的图片
      loadingImg: '',
      //控制进度条进度
      progress: 0,
      passwordXiuGaiLeiXing: "xiuGai",
      username: "",
      show: false,
      width: "240",
      systemLogo: "",
      setTitlt: true,
      isFold: false,
      passwordModuleVisible: false
    };
  },
  mounted() {
    // this.isFirstLogin();
    this.systemLogo = sessionStorage.getItem("systemLogo");
    const userStr = sessionStorage.getItem("user");
    const user = JSON.parse(userStr);
    this.setTitlt && this.getOauthTenantInfoByTenantId(user.tenantId)
  },
  methods: {
    //首次加载完毕之后
    showLoadingImg() {
      //   setInterval(()=>{
      //     this.loadingImg = ''
      // sessionStorage.removeItem('loadingImg')
      //   },1000)
      let timer
    
     timer = setInterval(()=>{
        if(this.progress<99){
          this.progress=this.progress+11
        }else{
          //控制进度条
          this.progress=99
          this.loadingImg = ''
          sessionStorage.removeItem('loadingImg')
        }

      }, 400)
    },
    // 判断是否为首次登录
    isFirstLogin() {
      const userObj = JSON.parse(sessionStorage.getItem("user"));
      if (userObj.firstLoginFlag) {
        this.passwordXiuGaiLeiXing = "shouCi";
        this.passwordModuleVisible = true;
      }
    },
    miMaQieHuanBanKuai(value) {
      this.passwordXiuGaiLeiXing = value;
    },
    foldHandler(isFold) {
      // 折叠左侧菜单
      this.isFold = isFold;
      this.$refs.commonAside.isFold = isFold;
      this.width = isFold ? "80" : "240";
      this.$store.commit("setWidthSide", this.width);
      // this.$emit("foldHandler", isFold);
    },
    // foldHandler(isFold) {
    //   this.width = isFold ? "80" : "240";
    //   this.$store.commit("setWidthSide", this.width);
    // },
    initUser() {
      const userStr = sessionStorage.getItem("user");
      const user = JSON.parse(userStr);
      this.loadingImg = sessionStorage.getItem('loadingImg')
      this.username = user.userName;
    },
    quite() {
      this.$confirm("确认退出登录吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => {
        const res = await logout();
        console.log(res);
        sessionStorage.clear();
        this.$router.replace({
          name: "login",
        });
      });
    },
    async getOauthTenantInfoByTenantId(tenantId) {
      const res = await getOauthTenantInfoByTenantId({tenantId});
      if(res?.data?.favicon || res?.data?.title){
        this.setTitlt = false
      }
      document.title = res?.data?.title ? res.data.title : '智川X-Agent开发平台';
      // 动态设置 favicon
      const favicon = document.querySelector("link[rel*='icon']");
      if (favicon) {
        favicon.href = res?.data?.favicon ? res?.data?.favicon : '/favicon.ico';
      }
    },
    // 操作日志
    operation() {
      this.$router.push({ name: "operateLog" });
    },
	// 个人中心
	userInfoZx() {
	  this.$router.push({ name: "userCenter" });
	},
    // 修改密码
    password() {
      // this.$router.push({ name: "password" });
      this.passwordXiuGaiLeiXing = "xiuGai";
      this.passwordModuleVisible = true;
    },
    // operateHandler() {
    //   this.show = !this.show;
    // },
  },
};
</script>

<style lang="scss" scoped>
.el-aside {
  width: 216px;
}

.el-header {
  background: RGBA(242, 245, 250, 1);
}

.el-main {
  padding: 0;
}

.first-loading {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-image: url('../assets/images/bg.png');
  background-position: center;
  background-size: cover;

  .headLoading {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    position: absolute;
    left: 50%;
    top: 25%;
    transform: translate(-50%);

    .firstImg {
      margin-bottom: 10px;
    }

    .loadingText {
      margin-bottom: 10px;
      font-size: 25px;
      font-weight: 530;
    }

    .noticeText {
      font-size: 12px;
      color: #9d9fa3
    }
  }


  .loading {
    width: 500px;
    margin-left: 50%;
    margin-top: 460px;
    transform: translate(-220px);
    transition: all 0.3s linear 0;
  }
}

.nav {
  width: 100%;
  height: 100%;
  position: relative;
  //   background: url("~@/assets/images/left-bg.png") no-repeat;
  background: #F5FAFD;
  background-size: 100% 100%;
  color: #33436F !important;

  .settings {
    width: 220px;
    height: 228px;
    background: #FFFFFF;
    box-shadow: 0px 2px 4px 0px rgba(26, 36, 70, 0.1);
    border-radius: 4px;
    position: absolute;
    bottom: 0;
    padding: 16px 10px;
    // margin: 0 8px;
    z-index: 99;
  }

  .login-box {
    position: relative;
    position: absolute;
    bottom: 0px;
    left: 0;
    // z-index: 2;
    display: flex;
    align-items: center;
    text-align: center;
    width: 216px;
    height: 36px;
    // background: #fff;
    // padding: 0 10px;
    box-sizing: border-box;
    // justify-content: space-between;
    margin: 12px 12px 16px 12px;
    padding-left: 0 !important;

    .logout {
      position: absolute;
      top: 8px;
      right: 8px;
      width: 24px;
      height: 24px;
      cursor: pointer;
      z-index: 99;
      display: flex;
      align-items: center;
      justify-content: center;

      *:hover {
        background: rgba(188, 193, 204, 0.20);
      }

      .show {
        display: block;
      }

      .hide {
        display: none;
      }

      &:hover {

        // background: #e8f0fc;
        // border-radius: 4px;
        .operate {
          //   display: block;
        }

        // .show {
        //   display: none;
        // }
        // .hide {
        //   display: block;
        // }
      }



      .list-check {
        width: 14px;
        height: 14px;
      }
    }

    .info {
      height: 36px;
      display: flex;
      justify-content: center;
      align-items: center;
      padding-right: 40px;

      .head {
        display: flex;
        justify-content: center;
        align-items: center;
        overflow: hidden;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: #fff;
        padding: 8px 0 8px 0;
        margin: 0 8px;

        i {
          color: #828894;
        }
      }

      .message {
        font-weight: bold;
        font-size: 14px;
        color: #383d47;
        line-height: 18px;

        .identity {
          font-weight: 400;
          font-size: 12px;
          color: #33436F;
          line-height: 16px;
        }
      }
    }
  }
}

h3 {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 22px 24px 16px;

  iconpark-icon {
    margin-right: 0;
    font-size: 16px;
    cursor: pointer;
  }

  .logo-new {
    width: 108px;
    display: block;
  }
  // 高度固定 宽度自适应,解决图片过长导致左侧栏样式问题
  .logo-new-height {
	  width: auto;
	  height: 40px;
  }
  .fold {
    width: 40px;
    height: 40px;
    display: block;
    cursor: pointer;
  }
}


.main {
  //   background: RGBA(242, 245, 250, 1);
  background: RGBA(255, 255, 255, 1);
  width: 100%;
  height: 100%;
  // padding: 20px;
  padding-top: 0;
}

.operate {
  // position: fixed;
  // display: block;
  // left: 224px;
  // bottom: 18px;
  // z-index: 100;
  padding: 4px;
  // background: #fff;
  // width: 128px;
  // box-shadow: 0px 2px 6px 0px rgba(26, 36, 70, 0.1);
  // border-radius: 4px;
  font-size: 14px;
  text-align: left;
  border-bottom: 1px solid #D5D8DE;

  li {
    display: inline-block;
    width: 100%;
    line-height: 40px;
    height: 40px;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    color: #494E57;
    padding-left: 8px;
    cursor: pointer;

    &:hover {
      background: #F2F3F5;
      border-radius: 2px;
    }

    .iconpark {
      padding-right: 8px;
    }

    &:nth-child(1) {
      margin-bottom: 4px;
    }
    &:last-child{
      color: #D33A22;
      .iconpark {
        color: #D33A22;
      }
    }
    img {
      width: 14px;
      margin-right: 6px;
    }
  }
}
.el-popper .popper__arrow {
  display: none;
}
</style>
