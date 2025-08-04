<template>
  <el-drawer
    title=""
    :visible.sync="drawer"
    :with-header="false"
    size="100%"
    custom-class="listingReviewDetails"
    @open="openDrawer"
    v-loading="loading"
  >
    <div class="listingReviewDetails-content">
      <div class="header">
        <div class="header-left">
          <iconpark-icon
            name="arrow-go-back-line-fen3c1b6"
            size="16"
            color="#494E57"
            style="margin-right: 16px; cursor: pointer"
            @click="closeDrawer"
          ></iconpark-icon>
          <img
            v-if="applicationInfo.facadeImageUrl"
            class="img"
            :src="applicationInfo.facadeImageUrl"
            alt=""
          />
          <div>
            <div class="applicationName">
              {{ applicationInfo.applicationName || "应用名称" }}
            </div>
          </div>
        </div>
        <div class="header-right">
          <!-- <el-button
            plain
            style="
              width: 92px;
              border-radius: 2px;
              color: #494e57;
              border: 1px solid #c9ccd1;
            "
            @click="collectHandler"
            ><iconpark-icon
              name="star-smile-line"
              size="16"
              color="#494E57"
              style="margin-right: 6px"
            ></iconpark-icon
            >收藏</el-button
          >
          <el-button
            plain
            style="
              width: 124px;
              border-radius: 2px;
              color: #494e57;
              border: 1px solid #c9ccd1;
            "
            @click="collapseDetailsHandler"
            >{{ showDetails ? "收起" : "展开" }}详细<iconpark-icon
              name="arrow-right-s-line"
              size="16"
              color="#494E57"
              style="margin-left: 6px"
            ></iconpark-icon
          ></el-button> -->
          <iconpark-icon
            size="16"
            color="#383838"
            name="share-line"
            title="分享"
            @click="cpoyText(applicationInfo.clientLink)"
          ></iconpark-icon>
          <iconpark-icon
            v-if="favoriteFlag == 1"
            name="star-s-fill"
            size="16"
            color="#383838"
            @click.stop="collectHandler(0)"
          ></iconpark-icon>
          <iconpark-icon
            v-else
            size="16"
            color="#383838"
            name="star-s-line"
            title="收藏"
            @click="collectHandler(1)"
          ></iconpark-icon>
		  <el-dropdown trigger="click" @command="(value) => handleCommand(value)"
		  	placement="top-end" class="opts-box-dropdown" 
		  >
		  	<span class="el-dropdown-link">
		  		<iconpark-icon name="more-line" size="18"
		  			color="#383838"></iconpark-icon>
		  	</span>
		  	<!-- api接入详情 -->
		  	<el-dropdown-menu slot="dropdown" class="opts-box-dropdown-menu">													
		  		<el-dropdown-item command="editeApp">
		  			<iconpark-icon color="#494E57"
		  				name="edit-box-line"></iconpark-icon>
		  			<span style="color: #494E57">复制应用</span>
		  		</el-dropdown-item>
		  		<el-dropdown-item v-permission="'deleteApp'" command="deleteApp">
		  			<iconpark-icon color="red"
		  				name="delete-bin-4-line"></iconpark-icon>
		  			<span style="color: #494E57">下架</span>
		  		</el-dropdown-item>
		  	</el-dropdown-menu>
		  </el-dropdown>
          <iconpark-icon
            v-if="!showDetails"
            size="16"
            color="#383838"
            name="menu-unfold-line"
            title="展开"
            @click="collapseDetailsHandler(true)"
          ></iconpark-icon>
          <iconpark-icon
            v-else
            size="16"
            color="#383838"
            name="menu-fold-line"
            title="收起"
            @click="collapseDetailsHandler(false)"
          ></iconpark-icon>
        </div>
      </div>
      <div class="footer">
        <div class="footer-left" v-loading="iframeLoading">
          <iframe
            :src="iframeUrl"
            width="100%"
            height="100%"
            title="示例框架"
            frameborder="0"
            allowfullscreen
          ></iframe>
        </div>
        <div v-if="showDetails" class="footer-right">
          <div
            style="width: 100%; height: 100vh; padding: 24px; overflow-y: auto"
          >
            <div class="row1">
              <div class="row1-left">
                <span>{{ detailsData.dialogueCount }}</span> 对话
              </div>
              <div class="row1-line"></div>
              <div class="row1-right">
                <span>{{ detailsData.collectCount }}</span> 收藏
              </div>
            </div>
            <div v-if="applicationInfo.introduce" class="row2">
              {{ applicationInfo.introduce }}
            </div>

            <div class="configuration">
              <div class="configuration-title">配置项</div>
              <div v-if="llmInfoList.length && applicationInfo?.type && !['dialogue', 'workflow'].includes(applicationInfo.type)" class="configuration-item">
                <div class="configuration-item-label">模型</div>
                <ul class="configuration-item-info">
                  <div
                    class="configuration-item-info-item"
                    v-for="(item, index) in llmInfoList"
                    :key="index"
                    :title="item.modelName"
                  >
                    <img
                      v-if="item.modelName == '雅意'"
                      src="@/assets/images/yayi.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == 'Kimi'"
                      src="@/assets/images/kimi.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == 'DeepSeek'"
                      src="@/assets/images/deepseek.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == '文心一言'"
                      src="@/assets/images/wenxinyiyan.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == '智谱清言'"
                      src="@/assets/images/zhipuqingyan.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == '豆包'"
                      src="@/assets/images/doubao.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == '通义千问'"
                      src="@/assets/images/tongyi.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == '中国移动'"
                      src="@/assets/images/deepseek.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == '百川'"
                      src="@/assets/images/baichuan.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == '星火'"
                      src="@/assets/images/xinghuo.png"
                      alt=""
                    />
                    <img
                      v-if="item.modelName == 'openAI'"
                      src="@/assets/images/openai.png"
                      alt=""
                    />
                  
                    
                     <div class="txt" >
                      {{ item.modelName }}
                     </div>
                  </div>
                </ul>
              </div>
              <div
                v-if="applicationPluginList.length"
                class="configuration-item"
              >
                <div class="configuration-item-label">插件</div>
                <ul class="configuration-item-info">
                  <div
                    class="configuration-item-info-item"
                    v-for="(item, index) in applicationPluginList"
                    :key="index"
                    :title="item.pluginName"
                  >
                    <img src="@/assets/images/chajian.svg" alt="" class="img" />
                    <div class="txt" >
                      {{ item.pluginName }}
                    </div>
                  </div>
                </ul>
              </div>
              <div v-if="knowledgeInfoList.length" class="configuration-item">
                <div class="configuration-item-label">知识库</div>
                <ul class="configuration-item-info">
                  <div
                    class="configuration-item-info-item"
                    v-for="(item, index) in knowledgeInfoList"
                    :key="index"
                    :title="item.knowledgeName"
                  >
                    <img
                      src="@/assets/images/zhishiku.svg"
                      alt=""
                      class="img"
                    />
                    <div class="txt">
                      {{ item.knowledgeName }}
                    </div>
                  </div>
                </ul>
              </div>
              <div v-if="componentInfoList.length" class="configuration-item">
                <div class="configuration-item-label">工作流</div>
                <ul class="configuration-item-info">
                  <div
                    class="configuration-item-info-item"
                    v-for="(item, index) in componentInfoList"
                    :key="index"
                    :title="item.componentName"
                  >
                    <img
                      src="@/assets/images/zhishiku.svg"
                      alt=""
                      class="img"
                    />
                    <div class="txt">
                      {{ item.componentName }}
                    </div>
                  </div>
                </ul>
              </div>
            </div>
            <div class="creator">
              <div class="creator-title">创作人</div>
              <div class="creator-content">
                <div class="creator-content-icon">
                  <iconpark-icon
                    name="user-line"
                    color="#828894"
                    size="16"
                  ></iconpark-icon>
                </div>
                <div class="creator-content-info">
                  <div class="name">{{ applicationInfo.createUser }}</div>
                  <div class="time">{{ applicationInfo.createTime }} 发布</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
	<el-dialog title="下架应用" :visible.sync="deleteDialogVisible" width="560px" class="deleteDialog"
		 append-to-body>
		<p class="desc">请确认是否要下架该应用</p>
		<span slot="footer" class="dialog-footer">
			<el-button @click="deleteDialogVisible = false">{{ $t("cancel") }}</el-button>
			<el-button type="primary" @click="confirmDelete">确定下架</el-button>
		</span>
	</el-dialog>
  </el-drawer>
</template>

<script>
import {
  apiGetStoreDataByApplicationId,
  apiGetMyFavoriteByApplicationId,
  apiEditMyFavorite,
  updateApplicationPublishAppStoreState,
  copyApp
} from "@/api/app";
export default {
  props: {
    value: {
      type: Boolean, 
      default: false,
    },
    sourceData: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
	  deleteDialogVisible: false,
      drawer: false,
      detailsData: {},
      applicationInfo: {},
      llmInfoList: [], // 模型
      applicationPluginList: [], // 插件
      knowledgeInfoList: [], // 知识库
      componentInfoList: [], // 工作流
      reviewList: [],
      reviewList2: [],
      loading: false,
      loading2: false,
      iframeLoading: false,
      showDetails: true,
      favoriteFlag: 0
    };
  },
  computed: {
    iframeUrl() {
      return this.applicationInfo.clientLink || "";
    },
  },
  watch: {
    value: {
      handler(n) {
        this.drawer = n;
      },
    },
  },
  methods: {
    // 应用类型转义
    applicationType(val) {
      let value = "";
      switch (val) {
        case "qa":
          value = "LLM";
          break;
        case "dialogue":
          value = "对话流";
          break;
        case "text-agent":
          value = "文本生成";
          break;
        case "workflow":
          value = "工作流";
          break;

        default:
          break;
      }
      return value;
    },
    openDrawer() {
      this.getStoreDataByApplicationId();
      this.getMyFavoriteByApplicationId();
    },
    closeDrawer() {
      this.drawer = false;
      this.$emit("closeDrawer");
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
    async getStoreDataByApplicationId() {
      this.loading = true;
      this.iframeLoading = true;
      try {
        const parmas = {
          applicationId: this.sourceData?.applicationId,
        };
        let res = await apiGetStoreDataByApplicationId(parmas);
        if (res.code == "000000") {
          this.detailsData = res.data || {};
          this.applicationInfo = res.data?.applicationInfo || {};
          this.llmInfoList = res.data?.llmInfoList || [];
          this.applicationPluginList = res.data?.applicationPluginList || [];
          this.knowledgeInfoList = res.data?.knowledgeInfoList || [];
          this.componentInfoList = res.data?.componentInfoList || [];
        }
      } catch (error) {
        this.loading = false;
        this.iframeLoading = false;
      }
      this.loading = false;
      setTimeout(() => {
        this.iframeLoading = false;
      }, 4000);
    },
	async apiUpdateApplicationPublishAppStoreState() {
	  // this.loading = true;
	  // this.iframeLoading = true;
	  try {
	    const parmas = {
	      applicationId: this.sourceData?.applicationId,
		  publishAppStore:'0'
	    };
	    let res = await updateApplicationPublishAppStoreState(parmas);
	    if (res.code == "000000") {
			this.deleteDialogVisible = false
	         this.closeDrawer()
	    }
	  } catch (error) {
	   
	  }
	  // this.loading = false;
	  // setTimeout(() => {
	  //   this.iframeLoading = false;
	  // }, 4000);
	},
    // 收藏
    async collectHandler(favoriteFlag) {
      const params = {
        applicationId: this.sourceData?.applicationId,
        favoriteFlag,
        type:1
      };
      const res = await apiEditMyFavorite(params);
      if (res.code == "000000") {
        this.$message.success(res?.msg);
        this.getMyFavoriteByApplicationId();
        if( this.favoriteFlag==0){
          this.favoriteFlag=1
        }else{
          this.favoriteFlag=0
        }
      }
    },
	async apiCopyApp() {
      const params = {
        applicationId: this.sourceData?.applicationId,
        copyFlag:1
      };
      const res = await copyApp(params);
      if (res.code == "000000") {
        this.$message.success(res?.msg);
        this.closeDrawer()
      }
    },
	confirmDelete(){
		this.apiUpdateApplicationPublishAppStoreState()
	},
	handleCommand(value) {
		if (value == 'editeApp') {
			console.log('点击编辑')
			this.apiCopyApp()
		} else if (value == 'deleteApp') {
			this.deleteDialogVisible = true
		}
	},
    // 收起详细
    collapseDetailsHandler(value) {
      this.showDetails = value;
    },
    // 复制功能
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
    cpoyText(content) {
      this.exeCommandCopyText(content);
      this.$message({
        message: this.$t("copySuccessed"),
        type: "success",
      });
    },
    // 收藏详情
    async getMyFavoriteByApplicationId() {
      const params = {
        applicationId: this.sourceData?.applicationId,
      };
      const res = await apiGetMyFavoriteByApplicationId(params);
      if (res.code == "000000") {
        this.favoriteFlag = res.data.favoriteFlag;
      }
    },
  },
};
</script>
<style lang="scss">
.listingReviewDetails {
  .el-drawer__body {
    overflow: hidden;
  }
}
</style>
<style lang="scss" scoped>
.listingReviewDetails {
  &-content {
    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      height: 80px;
      padding: 0 32px 0 40px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.12);
      background: #fff;
      &-left {
        display: flex;
        align-items: center;
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 18px;
        color: #36383d;
        .img {
          width: 40px;
          height: 40px;
          margin-right: 12px;
        }
        .applicationName {
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 18px;
          color: #494e57;
          line-height: 24px;
        }
      }
      &-right {
        display: flex;
        align-items: center;
        gap: 36px;
        iconpark-icon {
          cursor: pointer;
        }
      }
    }
    .footer {
      width: 100%;
      height: calc(100% - 80px);
      display: flex;
      align-items: center;
      &-right {
        width: 19%;
        height: calc(100vh - 80px);
        .row1 {
          display: flex;
          gap: 48px;
          &-line {
            width: 1px;
            height: 24px;
            background: #e7e7e7;
            margin-top: 6px;
          }
          &-left,
          &-right {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #a6a6a6;
            span {
              height: 40px;
              font-family: DINPro, DINPro;
              font-weight: 600;
              font-size: 28px;
              color: #383838;
              line-height: 40px;
              margin-right: 4px;
            }
          }
        }
        .row2 {
          margin-top: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #383838;
        }
        .creator {
          margin-top: 32px;
          &-title {
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 16px;
            color: #36383d;
            line-height: 24px;
          }
          &-content {
            display: flex;
            align-items: center;
            margin-top: 12px;
            &-icon {
              width: 40px;
              height: 40px;
              background: #f7f8fa;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              margin-right: 12px;
            }
            &-info {
              .name {
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 14px;
                color: #36383d;
                line-height: 20px;
              }
              .time {
                margin-top: 4px;
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 12px;
                color: #828894;
                line-height: 16px;
              }
            }
          }
        }
        .configuration {
          margin-top: 32px;
          &-title {
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 16px;
            color: #36383d;
            line-height: 24px;
          }
          &-item {
            &-label {
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 19px;
              margin-top: 10px;
            }
            &-info {
              margin-top: 8px;
              display: flex;
              align-items: center;
              flex-wrap: wrap;
              gap: 12px;
              &-item {
                width: 149px;
                height: 36px;
                display: flex;
                align-items: center;
                padding: 0 8px;
                background: #ffffff;
                border-radius: 2px;
                border: 1px solid #c9ccd1;
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 14px;
                color: #36383d;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                .txt{
                  font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 14px;
                color: #36383d;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                }
                img {
                  width: 20px;
                  height: 20px;
                  margin-right: 8px;
                }
              }
            }
          }
        }
        .shenhe {
          position: relative;
          width: 100%;
          height: 323px;
          border-top: 1px solid rgba(0, 0, 0, 0.12);
          padding: 24px;
          &-one {
            display: flex;
            align-items: center;
            justify-content: space-between;
            &-title {
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 16px;
              color: #36383d;
              line-height: 24px;
            }
          }
          &-two {
            margin-top: 24px;
            &-title {
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 20px;
            }
          }
          .btn {
            position: absolute;
            left: 24px;
            right: 24px;
            bottom: 24px;
            margin-top: 63px;
            height: 40px;
            line-height: 40px;
            text-align: center;
            background: #1747e5;
            border-radius: 2px;
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 16px;
            color: #ffffff;
            cursor: pointer;
          }
          :deep(.el-radio__inner::after) {
            width: 6px;
            height: 6px;
          }
        }
      }
      &-left {
        flex: 1;
        height: calc(100vh - 80px);
      }
    }
  }
}
</style>