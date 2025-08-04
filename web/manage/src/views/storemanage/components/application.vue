<template>
  <div
    ref="applicationContainerRef"
    class="application-container"
    v-infinite-scroll="load"
    :infinite-scroll-disabled="disabled"
    @scroll="handleScroll"
  >
    <div class="banner-container">
      <el-carousel
        class="banner-carousel"
        indicator-position="none"
        trigger="click"
        :autoplay="false"
        height="256px"
      >
        <el-carousel-item class="banner-item">
          <div class="banner-content">
            <div class="banner-title">
              <span class="title">智川</span>
              <span class="title-es">X-Agent</span>
            </div>
            <div class="banner-desc">无须编程，简单几步即可快速搭建AI应用</div>
            <div class="banner-btn" @click="createApp">立即创建</div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <div
      ref="searchTypeRef"
      class="application-search-type"
      :class="{ sticky: isSticky }"
    >
      <div class="type-list">
        <div
          v-for="(item, index) in appTypeList"
          :key="index"
          class="type-item"
          :class="{ active: activeType === item.value }"
          @click="changeType(item)"
        >
          {{ item.name }}
        </div>
      </div>
      <div
        class="collect-btn"
        :class="[favoriteFlag == 1 ? 'collect-btn-selected' : '']"
        @click="collectSearch"
      >
        收藏
      </div>
    </div>
    <div class="application-content">
      <div class="list-box">
        <div class="listOuter no-scrollbar" v-if="appList.length">
          <div class="list-content">
            <div
              v-for="(item, index) in appList"
              :key="item.applicationId + index"
              class="listItem"
              @click="reviewHandler(item)"
            >
              <div class="listContent">
                <div class="head-img-box">
                  <img
                    v-if="item.facadeImageUrl"
                    :src="item.facadeImageUrl"
                    class="headImg"
                  />
                  <img
                    v-else
                    :src="getRandomHeadImgDefaultBgColor(index)"
                    class="defaultHeadImg"
                  />
                </div>
                <div class="textContent">
                  <div class="title-content">
                    <p class="title" :title="item.applicationName">
                      {{ item.applicationName }}
                    </p>
                    <iconpark-icon
                      v-if="item?.favoriteFlag == 1"
                      name="star-s-fill"
                      size="16"
                      color="#1747E5"
                      @click.stop="collectionHandler(item.applicationId, 0)"
                    ></iconpark-icon>
                    <iconpark-icon
                      v-else
                      :name="isSC[index]?'star-s-fill':'star-s-line'"
                      size="16"
                      :color="isSC[index]?'#1747E5':'#86909C'"
                      @click.stop="collectionHandler(item.applicationId, 1)"
                       @mouseenter="isSC[index]= true; isSC = [...isSC]"
                    @mouseleave="isSC[index]= false; isSC = [...isSC]"
                    ></iconpark-icon>
                  </div>
                  <div class="list-type" v-if="item.type">
                    <span class="type-item">{{
                      getApplicationTypeLabel(item.type)
                    }}</span>
                  </div>
                </div>
              </div>
              <div class="desc-content">
                <p class="desc">{{ item.introduce }}</p>
              </div>
              <div class="date">
                <div class="list-update-time">
                  <span class="list-user-icon"
                    ><iconpark-icon name="user-3-line" size="16"></iconpark-icon
                  ></span>
                  <span class="create-user" v-if="item.accountName">{{
                    item.accountName
                  }}</span>
                  <span class="point" style="margin-right: 8px"></span>
                  <span>{{ item.updateTime }}</span>
                </div>
                <div
                  style="display: flex; align-items: center; cursor: pointer"
                >
                  <div class="edit-btn" @click.stop="reviewHandler(item)">
                    <span style="color: #494e57">立即体验</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- <div v-if="appList.length" class="pagination">
          <div class="total">
              {{ $t("total") }}{{ pageObj.total }}{{ $t("items") }}
          </div>
          <el-pagination
              @size-change="handleAppListSizeChange"
              @current-change="handleAppListCurrentChange"
              :current-page.sync="pageObj.pageNo"
              :page-sizes="[12, 24, 36, 48]"
              :page-size="pageObj.pageSize"
              background
              layout="prev, pager, next, sizes"
              :total="pageObj.total"
          >
          </el-pagination>
        </div> -->
        <div v-if="!appList.length && !listLoading" class="no-data">
          <img src="@/assets/images/no-data.png" alt="" />
          <div class="txt1">{{ $t("noData") }}</div>
        </div>
        <p v-if="listLoading" class="loading-tip">加载中...</p>
        <p v-if="noMore && !listLoading && appList.length" class="loading-tip">
          没有更多了
        </p>
      </div>
    </div>
    <el-backtop target=".application-container"></el-backtop>
    <ApplicationDetails
      v-model="drawer"
      :sourceData="reviewData"
      @closeDrawer="closeDrawer"
    />
  </div>
</template>
<script>
import { appList, apiGetLabelTypes, apiEditMyFavorite } from "@/api/app";
import { debounce } from "lodash";
// components
import ApplicationDetails from "./application-details";
export default {
  components: { ApplicationDetails },
  props: {
    applicationName: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      isSC:[],
      appList: [],
      pageObj: {
        pageNo: 1,
        pageSize: 36,
        total: 0,
      },
      listLoading: false,
      activeType: "",
      appTypeList: [
        {
          name: "全部",
          value: "",
        },
        {
          name: "聊天助手",
          value: "qa,dialogue,workflow,text-agent",
        },
        {
          name: "AI翻译",
          value: "translation",
        },
        {
          name: "AI搜索",
          value: "search",
        },
        {
          name: "文案写作",
          value: "writing",
        },
        {
          name: "行业报告",
          value: "reporting",
        },
        {
          name: "图片创作",
          value: "creative_generation",
        },
        {
          name: "学习助手",
          value: "study_assistant",
        },
        {
          name: "合规审查",
          value: "compliance_audit",
        },
      ],
      isSticky: false,
      stickyOffset: 0, // 吸顶触发的滚动位置
      drawer: false,
      reviewData: {},
      favoriteFlag: "",
      publishType: "",
    };
  },
  computed: {
    noMore() {
      return this.appList.length >= this.pageObj.total;
    },
    disabled() {
      return this.listLoading || this.noMore;
    },
  },
  mounted() {
    this.getLabelTypes();
    this.getAppList();
    this.stickyOffset = this.$refs.searchTypeRef.offsetTop;
    this.appList.forEach(item=>{
      this.isSC.push(false)
    })
    console.log(this.isSC,'this.isSC')
  },
  beforeDestroy() {
    window.removeEventListener("scroll", this.handleScroll);
  },
  methods: {
    closeDrawer() {
      this.drawer = false;
      this.$emit("refreshList");
    },
    reviewHandler(data) {
      this.reviewData = JSON.parse(JSON.stringify(data));
      this.drawer = true;
    },
    handleScroll() {
      const container = this.$refs.applicationContainerRef;
      const scrollTop = container.scrollTop;
      if (scrollTop >= this.stickyOffset) {
        this.isSticky = true;
      } else {
        this.isSticky = false;
      }
    },
    getAppList() {
        this.listLoading = true;
      appList({
        pageNo: this.pageObj.pageNo,
        pageSize: this.pageObj.pageSize,
        order: "",
        sort: "",
        applicationName: this.applicationName || "",
        type: this.activeType || "",
        publishAppStore: 1, // 是否发布应用商店
        publishType: this.publishType == "全部" ? "" : this.publishType,
        favoriteFlag: this.favoriteFlag,
      }).then((res) => {
        this.listLoading = false;
        if (res.code == "000000") {
          this.appList = res.data?.records;
          this.appList.forEach(item=>{
            this.isSC.push(false)
          })
          console.log(this.isSC)
          // this.appList.push(...res.data?.records);
          this.pageObj.total = res.data?.totalRow || 0;
        } else {
          this.appList = [];
        }
      });
    },
    load() {
      console.log("load");
      this.listLoading = true;
      this.pageObj.pageNo += 1;
      this.getAppList();
    },
    handleAppListCurrentChange(page) {
      this.pageObj.pageNo = page;
      this.getAppList();
    },
    handleAppListSizeChange(size) {
      this.pageObj.pageSize = size;
      this.pageObj.pageNo = 1;
      this.getAppList();
    },
    getRandomHeadImgDefaultBgColor(index) {
      const imgList = [
        require("@/assets/images/appManagement/default-logo-1.svg"),
        require("@/assets/images/appManagement/default-logo-2.svg"),
        require("@/assets/images/appManagement/default-logo-3.svg"),
        require("@/assets/images/appManagement/default-logo-4.svg"),
        require("@/assets/images/appManagement/default-logo-5.svg"),
        require("@/assets/images/appManagement/default-logo-6.svg"),
        require("@/assets/images/appManagement/default-logo-7.svg"),
      ];
      if (index !== undefined && typeof index === "number" && index >= 0) {
        return imgList[index % imgList.length];
      }
      const randomIndex = Math.floor(Math.random() * imgList.length);
      return imgList[randomIndex];
    },

    permissions(code) {
      const permissionButton = this.getUserPermissions();
      const item = permissionButton.find((n) => n.menuCode === code);
      return !!item;
    },
    getUserPermissions() {
      const permission = JSON.parse(sessionStorage.getItem("permission"));
      const permissionButton = [];
      permission.forEach((item) => {
        if (item.children && Array.isArray(item.children)) {
          item.children.forEach((child) => {
            permissionButton.push({
              isHidden: child.isHidden,
              menuCode: child.menuCode,
            });
          });
        }
      });
      return permissionButton;
    },
    openPreview(data) {
      window.open(data.clientLink, "_blank");
    },
    handleSearch() {
      this.pageObj.pageNo = 1;
      this.pageObj.total = 0;
      this.appList = [];
      this.getAppList();
    },
    changeType(item) {
      if (this.activeType === item.value) return;
      this.activeType = item.value;
      this.publishType = item.name;
      this.handleSearch();
    },
    getApplicationTypeLabel(value) {
      return (
        this.appTypeList.find((item) => item.value.indexOf(value) !== -1)
          ?.name || ""
      );
    },
    createApp() {
      this.$router.push({
        name: "appmanage",
        params: {
          openDialog: true,
        },
      });
    },
    async getLabelTypes() {
      let res = await apiGetLabelTypes({ type: 2 });
      if (res.code == "000000") {
        this.appTypeList = res.data || [];
      }
    },
    // 收藏
    async collectionHandler(applicationId, favoriteFlag) {
      const params = {
        applicationId,
        favoriteFlag,
        type:1
      };
      const res = await apiEditMyFavorite(params);
      if (res.code == "000000") {
        // this.handleSearch();
        this.getAppList();
        this.$message.success(res?.msg);
      }
    },
    // 收藏检索
    collectSearch() {
      this.favoriteFlag = this.favoriteFlag == 1 ? 0 : 1;
      this.handleSearch();
    },
  },
};
</script>
<style lang="scss" scoped>
.application-container {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding: 0 32px 16px;
  .banner-container {
    height: 276px;
    :deep(.banner-carousel) {
      .el-carousel__item {
        background: linear-gradient(
          243deg,
          #f3fbff 0%,
          #f3fbff 0%,
          #e3f6ff 100%
        );
        border-radius: 4px;
        .banner-content {
          height: 100%;
          background: url("../../../assets/images/banner.png") no-repeat;
          background-position: top right;
          background-size: cover;
          padding: 35px 24px 24px 200px;
          .banner-title {
            font-family: MiSans, MiSans;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 48px;
            color: #1948e5;
            line-height: 74px;
            .title-es {
              color: #000000;
              margin-left: 12px;
            }
          }
          .banner-desc {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 24px;
            color: #000000;
            line-height: 40px;
            margin-bottom: 24px;
          }
          .banner-btn {
            width: 160px;
            height: 48px;
            background: #1a1a1d;
            border-radius: 24px;
            font-family: MiSans, MiSans;
            line-height: 48px;
            text-align: center;
            font-size: 18px;
            color: #ffffff;
            cursor: pointer;
          }
        }
      }
      .el-carousel__arrow--left,
      .el-carousel__arrow--right {
        display: none !important;
      }
    }
  }
  .application-search-type {
    padding-bottom: 16px;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .type-list {
      display: flex;
      gap: 16px;
      .type-item {
        cursor: pointer;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 18px;
        color: #828894;
        line-height: 40px;
        &.active {
          font-weight: 500;
          color: #603eca;
        }
      }
    }
    .collect-btn {
      width: 68px;
      height: 40px;
      line-height: 40px;
      text-align: center;
      border-radius: 4px;
      border: 1px solid #e7e7e7;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #1d2129;
      cursor: pointer;
    }
    .collect-btn-selected {
      background: rgba(23, 71, 229, 0.1);
      border: none;
      color: #1747e5;
    }
  }
  .sticky {
    position: fixed;
    top: 96px;
    width: 100%;
    z-index: 999; /* 确保元素在最上层 */
    // transition: top 0.3s ease; /* 添加过渡效果 */
  }
  .application-content {
    .list-box {
    }
    .listOuter {
      .list-content {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
        flex-wrap: wrap;
        grid-gap: 24px 24px;
      }

      .listItem {
        height: 188px;
        padding: 16px;
        position: relative;
        background: #ffffff;
        border-radius: 4px;
        border: 1px solid #d5d8de;
        cursor: pointer;
        .sign {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          line-height: 20px;
          text-align: left;
          font-style: normal;
          line-height: 20px;
          display: inline-block;
          > img {
            width: 15px;
            height: 15px;
            margin-right: 2px;
          }
          > img,
          > span {
            vertical-align: middle;
          }
        }
        .successSign {
          color: #1abc7c;
          .point {
            display: inline-block;
            width: 5px;
            height: 5px;
            background: #3fb816;
            border-radius: 50%;
            margin: 0 4px;
          }
          // background: rgba(26, 188, 124, 0.1);
        }
        .failSign {
          color: #768094;
          // background: rgba(118, 128, 148, 0.1);
        }

        .listContent {
          display: flex;
          justify-content: flex-start;
          margin-bottom: 16px;

          // align-items: center;
          // gap: 16px;
          // padding: 16px;
          .head-img-box {
            min-width: 56px;
            width: 56px;
            height: 56px;
            border-radius: 8px 0px 0px 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 16px;
            background: #f2f4f7;
            border-radius: 4px;
            position: relative;
          }
          .headImg {
            width: 56px;
            border-radius: 8px 0px 0px 8px;
            max-height: 100%;
            object-fit: cover;
            border-radius: 4px;
            // height:  144px;
          }
          .defaultHeadImg {
            width: 56px;
            max-height: 100%;
            object-fit: cover;
            border-radius: 4px;
          }
          .item-icon {
            display: inline-flex;
            justify-content: center;
            align-items: center;
            width: 20px;
            height: 20px;
            background: #ffffff;
            box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.1);
            position: absolute;
            bottom: -8px;
            right: -8px;
            z-index: 2;
            border-radius: 50%;
            img {
              width: 14px;
              height: 14px;
            }
          }
          .textContent {
            width: 100%;
            // padding: 16px;
            font-family: MiSans, MiSans;
            position: relative;
            overflow: hidden;
            .title-content {
              display: flex;
              align-items: center;
              margin-bottom: 8px;
            }
            .title {
              width: calc(100% - 32px);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              font-weight: 500;
              font-size: 18px;
              color: #494e57;
              line-height: 24px;
              text-align: left;
              font-style: normal;
              margin-right: 4px;
            }
            .list-type {
              .type-item {
                display: inline-block;
                height: 24px;
                background: #ebeef2;
                border-radius: 2px;
                padding: 0px 8px;
                line-height: 24px;
                font-size: 12px;
                color: #494e57;
              }
            }

            .date {
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 24px;
              text-align: left;
              font-style: normal;
              width: calc(100% - 32px);
              position: absolute;
              left: 16px;
              bottom: 16px;
              display: flex;
              align-items: center;
              justify-content: space-between;
              .period-box {
                display: flex;
                align-items: center;
                .point {
                  width: 8px;
                  height: 8px;
                  position: relative;
                  background: rgba(28, 80, 253, 0.1);
                  border-radius: 50%;
                  margin: 4px;
                  &::before {
                    content: "";
                    width: 4px;
                    height: 4px;
                    background: #1c50fd;
                    border-radius: 50%;
                    position: absolute;
                    left: 2px;
                    top: 2px;
                  }
                }
              }
            }
            // >p:last-child{
            //     padding: 1px 4px 4px;
            //     display: inline-block;
            //     height: 24px;
            //     background: #fff;
            //     border-radius: 4px;
            //     >img {
            //         width: 15px;
            //         height: 15px;
            //         border-radius: 4px;
            //         margin-right: 3px;
            //     }
            //     >span{
            //         font-family: MiSans, MiSans;
            //         font-weight: 400;
            //         font-size: 14px;
            //         color: #768094;
            //         line-height: 18px;
            //         text-align: left;
            //         font-style: normal;
            //     }
            //     >span,>img{
            //         vertical-align: middle;
            //     }
            // }
          }
        }

        .fotterOuter {
          display: flex;
          justify-content: space-between;
          margin-top: 28px;
          .footerItem {
            width: 33.3%;
            text-align: center;
            cursor: pointer;
            &:not(:last-child) {
              border-right: 1px solid rgba(0, 0, 0, 0.12);
            }
            img,
            span {
              vertical-align: middle;
            }
            > span {
              color: #3666ea;
            }
            > img {
              width: 15px;
              height: 15px;
              margin-right: 3px;
            }
          }
        }
        .desc-content {
          .desc {
            width: 100%;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
            overflow: hidden;
            text-overflow: ellipsis;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #494e57;
            line-height: 22px;
          }
        }
        .list-update-time {
          max-width: calc(100% - 36px);
          display: flex;
          align-items: center;
          > span {
            display: inline-block;
            margin-right: 4px;
            font-weight: 400;
            font-size: 12px;
            color: #828894;
          }
          .list-user-icon {
            width: 24px;
            height: 24px;
            background: #f7f8fa;
            border-radius: 50%;
            text-align: center;
            line-height: 24px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
          }
          .create-user {
            margin-right: 8px;
          }
          .point {
            width: 3px;
            height: 3px;
            border-radius: 50%;
            background: #bcc1cc;
            margin-right: 8px;
          }
        }
        .date {
          font-weight: 400;
          font-size: 12px;
          color: #828894;
          line-height: 24px;
          text-align: left;
          font-style: normal;
          width: calc(100% - 32px);
          position: absolute;
          left: 16px;
          bottom: 16px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          .period-box {
            display: flex;
            align-items: center;
            .point {
              width: 8px;
              height: 8px;
              position: relative;
              background: rgba(28, 80, 253, 0.1);
              border-radius: 50%;
              margin: 4px;
              &::before {
                content: "";
                width: 4px;
                height: 4px;
                background: #1c50fd;
                border-radius: 50%;
                position: absolute;
                left: 2px;
                top: 2px;
              }
            }
          }
        }
        .edit-btn {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #494e57;
          line-height: 24px;
          margin-right: 8px;
          padding: 0px 6px;
          .edit-icon {
            margin-right: 4px;
          }
          &:hover {
            background: rgba(180, 188, 204, 0.2);
          }
        }
      }
      .listItem:hover {
        box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
      }
    }
    .no-data {
      margin-top: 86px;
    }
    .loading-tip {
      text-align: center;
      margin-top: 24px;
      font-weight: 400;
      font-size: 14px;
      color: #828894;
      font-style: normal;
      line-height: 22px;
    }
  }
}
</style>