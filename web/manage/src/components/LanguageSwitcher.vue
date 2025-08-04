<template>
    <div class="language-switcher">
        <transition class="el-zoom-in-top">
          <div class="logout">
            <!-- <iconpark-icon class="icon-hover" color="#1c50fd" name="translate"></iconpark-icon> -->
            <!-- <iconpark-icon class="icon" name="translate"></iconpark-icon> -->
            <ul class="operate">
              <li @click="changeLanguage(code)" :class="{'' : selectedLanguage === code}" v-for="(label, code) in languages"
              :key="code">
              <img v-if="label=='英文'" src="@/assets/images/ying.png" alt="">
              <img v-else src="@/assets/images/zhong.png" alt="">
                {{ label }}
                <img style="margin-left: 80px;" v-if="selectedLanguage === code" src="@/assets/images/check-line.svg" alt="">
              </li>
            </ul>
          </div>
        </transition>
    </div>
</template>

<script>
export default {
    data() {
        return {
            selectedLanguage: this.$i18n.locale,
            show:false,
            languages: {
                zh: "简体中文",
                hk: "繁体中文",
                en: "英文",
            },
        };
    },
    methods: {
      operateHandler() {
      this.show = !this.show;
    },
        changeLanguage(lang) {
            this.selectedLanguage  = lang
            localStorage.setItem("locale", this.selectedLanguage); // 保存语言设置到 localStorage
            location.reload();
        },
    },
};
</script>

<style scoped lang="scss">

.language-switcher {
    // position: absolute;
    // bottom: 0;
    // right: 44px;
    // // z-index: 10000;
    // display: flex;
    // align-items: center;
    // height: 64px;
    // box-sizing: border-box;
    // justify-content: flex-start;
    .logout {
        padding-bottom: 15px;
        margin-bottom: 6px;
      width: 100%;
      height: 100%;
      cursor: pointer;
      // z-index: 9999;
      display: flex;
    //   align-items: center;
      justify-content: center;
      border-bottom: 1px solid #D5D8DE;
      .icon{
          display: block;
        }
        .icon-hover{
          display: none;
        }
      &:hover {
        // background: #e8f0fc;
        // border-radius: 4px;
        .operate {
          display: block;
        }
        // .icon{
        //   display: none;
        // }
        // .icon-hover{
        //   display: block;
        // }
      }

      .operate {
        // position: fixed;
        // display: none;
        // z-index: 10000;
        // left: 196px;
        // bottom: 18px;
        padding: 4px;
        // background: #fff;
        // width: 128px;
        // box-shadow: 0px 2px 6px 0px rgba(26, 36, 70, 0.1);
        // border-radius: 4px;
            color: #494E57;
          width: 100%;
        li {
          width: 100%;
          height: 36px;
          line-height: 36px;
          font-size: 14px;
          display: flex;
          align-items: center;
          justify-content:  flex-start;
            padding-left: 4px;
            cursor: pointer;
          &:hover {
            // background: #e8f0fc;
            // border-radius: 4px;
          }
          &.active {
            color:  #1c50fd;
          }

          &:nth-child(1) {
            margin-bottom: 4px;
          }

          img {
            width: 14px;
            margin-right: 6px;
          }
        }
      }

      .list-check {
        width: 14px;
        height: 14px;
      }
    }

    .info {
      display: flex;
      justify-content: center;
      align-items: center;

      .head {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: #f3f5fb;
        margin-right: 8px;

        i {
          color: #b3bccd;
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
          color: #b4bccc;
          line-height: 16px;
        }
      }
    }
  }
</style>
