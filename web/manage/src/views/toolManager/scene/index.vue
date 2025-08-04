<template>
  <div class="scene-index">
    <div v-if="pageType == 'list'" class="scene-index-list h-full">
      <div class="scene-index-title">
        <div>
            {{ $t("sceneManagement") }}
        </div>
        <div>
            <el-button
            type="primary"
            style="width: 120px; margin-left: auto;background:#1747E5;border-radius: 2px;"
            @click="addScene"
          >
            <i class="el-icon-circle-plus" style="margin-right: 8px;"></i>{{ $t("addScene") }}
          </el-button>
        </div>
      </div>
      <div class="scene-index-content" v-loading="loading">
        
        <div class="head">
          <el-select style="width: 192px;margin-right: 16px"
            v-model="paramSearch.applicationId"
            @change="applicationIdChange"
            clearable
            filterable
          >
            <el-option
              v-for="item in applicationIdList"
              :key="item.applicationId"
              :label="item.applicationName"
              :value="item.applicationId"
            >
            </el-option>
          </el-select>
          <el-input
            :placeholder="$t('inputKeyword')"
            prefix-icon="el-icon-search"
            v-model="paramSearch.sceneName"
            style="width: 334px"
            @keydown.native.enter="searchHandler"
            clearable
          >
          </el-input>
          
        </div>
        <ul v-if="list.length" class="list no-scrollbar">
          <li class="list-item" v-for="(item, index) in list" :key="index">
            <div class="list-item-top">
              <img v-if="item.icon" :src="item.icon" alt="" />
              <img v-else src="@/assets/images/scene-icon.png" alt="" />
              <div class="text">{{ item.sceneName }}</div>
              <div class="tips">
                <iconpark-icon
                  name="apps-fill"
                  size="14"
                  color="#B4BCCC"
                ></iconpark-icon
                >{{ item.appNum || 0 }}
              </div>
            </div>
            <div class="list-item-center">
              {{ item.matterDesc }}
            </div>
            <div class="list-item-bottom">
              <div>最近更新：{{ item.updateTime }}</div>
              <div class="opts-box"
              :class="{ 'opts-box-active': activeIndexMoreClick === index }">
                <el-dropdown
                  trigger="click"
                  @command="(value) => handleCommand(value, item)"
                  placement="top-end"
                  class="opts-box-dropdown"
                  @visible-change="handleVisibleChange($event, index)"
                >
                  <span class="el-dropdown-link">
                    <!-- <i
                      style="
                        transform: rotate(90deg);
                        font-size: 16px;
                        color: #828894;
                        cursor: pointer;
                      "
                      class="el-icon-more"
                    ></i> -->
                    <iconpark-icon name="more-line" size="18" color="#383838"></iconpark-icon>
                  </span>
                  <el-dropdown-menu slot="dropdown" class="opts-box-dropdown-menu">
                    <el-dropdown-item command="editeApp">
                      <iconpark-icon color="#494E57" name="edit-box-line"></iconpark-icon>
                      <span style="color: #494E57">{{ $t("edit") }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item command="deleteApp">
                      <iconpark-icon color="#494E57" name="delete-bin-4-line"></iconpark-icon>
                      <span style="color: #494E57">{{ $t("delete") }}</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </div>
          </li>
        </ul>
        <div v-if="list.length" class="pagination">
          <div class="total">
            {{ $t("total") }}{{ pageObj.total }}{{ $t("items") }}
          </div>
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page.sync="pageObj.pageNo"
            :page-sizes="[12, 24, 36, 48]"
            :page-size="pageObj.pageSize"
            background
            layout="prev, pager, next, sizes"
            :total="pageObj.total"
          >
          </el-pagination>
        </div>
        <div v-else class="no-data">
          <img src="@/assets/images/no-data.png" alt="" />
          <div class="txt1">{{ $t("noData") }}</div>
          <!-- <div class="txt2">{{ $t("clickDeployLargeModel") }}</div> -->
          <!-- <el-button type="primary" style="width: 112px; font-size: 16px;margin-top: 24px">{{
            $t("addScene")
          }}</el-button> -->
        </div>
      </div>
    </div>
    <div v-else class="h-full">
      <AddSceneDetails
        ref="MatterTypeDetails"
        :sceneId="sceneId"
        :baseForm="dialogData"
        @comeBack="comeBackList"
        @refreshList="refreshList"
        @openMatterTypeDialog="openMatterTypeDialog"
      />
    </div>

    <!-- 创建问答助手 名称弹窗 -->
    <AddScene
      v-if="addSceneDialogVisible"
      :dialogVisibleApplication="addSceneDialogVisible"
      :dialogType="dialogType"
      @addSceneManagement="addSceneManagementAfter"
      @closeDialog="closeDialog"
    />
  </div>
</template>

<script>

import { getApplicationInfoList } from "@/api/index.js";
import {
  apiGetSceneManagementList,
  apiDeleteSceneManagement,
} from "@/api/scene";
import AddScene from "./addScene.vue";
import AddSceneDetails from "./addSceneDetails.vue";

export default {
  components: { AddScene, AddSceneDetails },
  data() {
    return {
      activeIndexMoreClick:null,
      pageType: "list",
      pageObj: {
        pageNo: 1,
        pageSize: 12,
        total: 0,
      },
      paramSearch: {
        applicationId: "", // 应用id
        sceneName: "", // 场景名称
      },
      list: [],
      loading: false,

      addSceneDialogVisible: false,
      // 新字段
      applicationIdList: [],
      sceneId: "",
      dialogData: {},
    };
  },
  mounted() {
    this.getApplicationInfoList();
    this.getSceneApplicationRefList();
  },
  methods: {
    handleVisibleChange(val, index) {
      if(val){
        this.activeIndexMoreClick = index;
      }else {
        this.activeIndexMoreClick = null;
      }
      
    },
    searchHandler(curPage) {
      this.pageObj.pageNo = curPage ? this.pageObj.pageNo : 1;
      this.getSceneApplicationRefList();
    },
    // 应用change
    applicationIdChange() {
      this.getSceneApplicationRefList();
    },
    handleCurrentChange(page) {
      this.pageObj.pageNo = page;
      this.getSceneApplicationRefList();
    },
    handleSizeChange(size) {
      this.pageObj.pageSize = size;
      this.getSceneApplicationRefList();
    },
    // 应用列表
    async getApplicationInfoList() {
      const { data } = await getApplicationInfoList({
        pageNo: 1,
        pageSize: 100,
      });
      this.applicationIdList = data?.records || [];
      // this.paramSearch.applicationId = this.applicationIdList.length
      //   ? this.applicationIdList[0]?.applicationId
      //   : "";
    },
    // 查询列表
    async getSceneApplicationRefList() {
      const params = {
        ...this.paramSearch,
        pageSize: this.pageObj.pageSize,
        pageNo: this.pageObj.pageNo,
      };
      this.loading = true;
      const res = await apiGetSceneManagementList(params);
      if (res.code == "000000") {
        console.log("res", res);
        this.list = res.data.records || [];
        this.pageObj.total = res.data?.totalRow || 0;
      }
      this.loading = false;
    },
    handleCommand(value, item) {
      switch (value) {
        case "editeApp":
          this.sceneId = item.sceneId;
          this.pageType = "detail";
          break;
        case "deleteApp":
          this.deleteSceneManagement(item);
        default:
          break;
      }
    },
    // 事项类型详情返回
    comeBackList() {
      this.pageType = "list";
    },
    refreshList() {
      this.pageType = "list";
      this.searchHandler(true);
    },
    openMatterTypeDialog() {},
    addScene() {
      this.addSceneDialogVisible = true;
      this.dialogType = "add";
    },
    // 关闭model框
    closeDialog() {
      this.addSceneDialogVisible = false;
    },
    addSceneManagementAfter(data) {
      this.addSceneDialogVisible = false;
      this.sceneId = data?.sceneId;
      this.dialogData = JSON.parse(JSON.stringify(data)) || {};
      this.searchHandler();
      this.pageType = "detail";
    },
    // 删除场景
    deleteSceneManagement(data) {
      const list = [data.sceneId];
      this.$confirm("请确认是否删除", "提示")
        .then(async (_) => {
          const res = await apiDeleteSceneManagement(list);
          if (res.code == "000000") {
            this.$message.success("删除成功");
            this.searchHandler();
          } else {
            this.$message.warning(res?.msg);
          }
        })
        .catch((_) => {});
    },
  },
};
</script>
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>
<style lang="scss">
.confirm-ok {
  background: #1c50fd !important;
  border-color: transparent !important;
}
.confirm-cancel {
  &:hover {
    background: none !important;
    color: #1c50fd !important;
    border-color: #1c50fd !important;
  }
}
</style>
<style lang="scss" scoped>

::v-deep .btn-prev {
  background-color: transparent !important;
  border-radius: 4px !important;
  border: 1px solid #dddfe8;
}
::v-deep .el-popper[x-placement^=top] .popper__arrow::after{
    border:0;
}
::v-deep .el-pagination.is-background .btn-next,
.el-pagination.is-background .btn-prev,
.el-pagination.is-background .el-pager li {
  background-color: transparent !important;
  border-radius: 4px !important;
  border: 1px solid #dddfe8;
}
::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
  background: transparent;
  border: 1px solid #3666ea;
  font-size: 16px;
  color: #3666ea;
}
::v-deep .el-pagination.is-background .el-pager li {
  background: transparent !important;
  border-radius: 4px;
  border: 1px solid #dddfe8;
}
::v-deep .el-pagination .el-select .el-input .el-input__inner {
  font-size: 16px;
  color: #272a31;
}
::v-deep .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  .dropdown-txt {
    margin-left: 4px;
    color: #828894;
  }
}
.edit-detail{
  width: 24px;
  height: 24px;
  // background: rgba(180, 188, 204, .2);
  border-radius: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
&:hover{
  cursor: pointer;
  background: rgba(180, 188, 204, 0.2);
}

}
.scene-index {
  height: 100%;
  background: #fff;
  border-radius: 16px 0px 0px 16px;
  .h-full {
    height: 100%;
  }
  ::v-deep .el-input__inner {
    border-radius: 4px;
  }
  &-list {
    padding: 32px;
  }
  &-title {
    height: 40px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #383d47;
    line-height: 40px;
    display: flex;
    justify-content: space-between;
  }
  &-content {
    width: 100%;
    height: calc(100vh - 88px - 20px);
    display: flex;
    margin-top: 24px;
    display: flex;
    flex-direction: column;
    background: #ffffff;
    border-radius: 4px;
    .head {
      display: flex;
      justify-content: space-between;
      height: 40px;
      width: 100%;
    }
    .list {
      flex: 1;
      margin-top: 28px;
      display: flex;
      flex-wrap: wrap;
      overflow: auto;
      align-content: flex-start;

      &-item {
        margin-right: 1.3%;
        margin-bottom: 16px;
        padding: 16px;
        width: 24%;
        background: #ffffff;
        border-radius: 16px;
        border: 1px solid #e1e4eb;
        &:nth-child(4n) {
          margin-right: 0;
        }
        &-top {
          display: flex;
          align-items: center;
          height: 40px;
          img {
            width: 40px;
            height: 40px;
          }
          .text {
            font-family: MiSans, MiSans;
            font-weight: 600;
            font-size: 16px;
            color: #383d47;
            line-height: 20px;
            margin: 0 8px 0 12px;
          }
          .tips {
            padding: 4px 8px;
            background: rgba(130, 136, 148, 0.1);
            border-radius: 14px;
            font-family: MiSans, MiSans;
            font-size: 14px;
            color: #828894;
            display: flex;
            align-items: center;
            justify-content: center;

            iconpark-icon {
              margin-right: 4px;
            }
          }
        }
        &-center {
          font-family: MiSans, MiSans;
          font-size: 14px;
          color: #828894;
          line-height: 22px;
          margin: 12px 0;
        }
        &-bottom {
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          font-family: MiSans, MiSans;
          font-size: 14px;
          color: #828894;
        }
      }
    }
  }
}
</style>