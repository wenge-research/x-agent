<template>
  <div>
    <div
      class="plugin"
      v-if="
        !editDialogVisible &&
        !editDetailDialogVisible &&
        !editModelDialogVisible
      "
    >
      <div class="plugin-content">
        <div class="plugin-content-right" v-loading="loading">
          <div class="head">
            <div>
              <el-select
                v-model="paramSearch.applicationIdNew"
                @change="searchHandler"
                filterable
                style="margin-right: 16px"
                v-if="false"
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
                v-model="paramSearch.name"
                @keydown.enter.native="searchHandler"
                @input="searchHandler"
                style="width: 334px"
                clearable
              >
                <template v-slot:suffix>
                  <i class="el-icon-search" @click="searchHandler"></i>
                </template>
              </el-input>
			  <el-select
			    v-model="labels"
			    @change="getPluginList"
			    filterable
			    style="margin-left: 16px"
			   
			  >
			    <el-option value="" label="全部"></el-option>
			    <el-option
			      v-for="item in typeList"
			      :key="item.id"
			      :label="item.name"
			      :value="item.name"
			    >
			    </el-option>
			  </el-select>
              <!-- <el-button type="primary" style="margin-left: 16px" @click="searchHandler">
              {{ $t("query") }}
            </el-button> -->
            </div>
            <el-button
              plain
              style="
                background: #1747e5;
                color: #fff;
                margin-left: 16px;
                border-radius: 2px;
              "
              @click="createVoice"
            >
              <i class="el-icon-circle-plus" style="margin-right: 8px;"></i>{{ $t("createPlugin") }}
            </el-button>
          </div>
          <ul v-if="list.length" class="list no-scrollbar">
            <li
              class="list-item"
              v-for="(item, index) in list"
              :key="index"
              :style="{ height: type == 1 ? '180px' : '114px' }"
              @click="(editDetailDialogVisible = true), (editItem = item)"
            >
              <!-- <div v-if="item.publishAppStore == 1" class="row-tips">
                已上架
              </div> -->
              <div class="preset" v-if="item.ownerType=='official'&&isAdmin">
                预置
              </div>
              <div
                class="list-item-top"
              >
                <img v-if="item.icon" class="avatar" :src="item.icon" alt="" />
                <img
                  v-else
                  class="avatar"
                  src="@/assets/images/default-plugin.svg"
                  alt=""
                />
                <div class="text">
                  <div
                    class="row-name"
                    :title="item.componentName"
                    v-if="paramSearch.applicationId == 1"
                  >
                    {{ item.componentName }}
                    <div class="successSign" v-if="item.publishAppStore == 1"> 
                        <i class="el-icon-success" style="color: #3FB816;font-size: 12px;"></i> 
                      </div>
                  </div>
                  <div
                    class="row-name"
                    :title="item.name"
                    v-if="paramSearch.applicationId == 2"
                  >
                    {{ item.name }}
                  </div>
                  <div class="row-other">
                    <div
                      class="tipsTpye"
                      :class="[
                        paramSearch.applicationId == 1 ? 'api' : 'moxingsuanfa',
                      ]"
                    >
                      <div class="tips">
                        <div
                          class="tips-name"
                          v-if="item.type === 1"
                        >
                          API
                        </div>
                        <div
                          class="tips-name"
                          v-if="item.type === 6"
                        >
                          代码
                        </div>
                      </div>
                    </div>
                    <!-- <div v-if="item.labels" class="labels">
                      
                    </div> -->
                    <div
                        class="labels-item"
                        v-for="(item, index) in opreateLabels(item.labels)"
                        :key="index"
                      >
                        {{ item }}
                      </div>
                  </div>
                </div>
              </div>
              <div class="list-item-center">
                <div
                  class="componentDesc"
                  :title="item.componentDesc"
                  v-if="paramSearch.applicationId == 1"
                >
                  {{ item.componentDesc }}
                </div>
                <div
                  class="componentDesc"
                  :title="item.description"
                  v-if="paramSearch.applicationId == 2"
                >
                  {{ item.description }}
                </div>
              </div>
              <div
                class="list-item-bottom"
                :class="[type != 1 ? 'noBorder flex-start' : '']"
              >
                <div
                  v-if="item.tag && type == 2"
                  class="row"
                  style="padding-left: 60px"
                >
                  <div
                    class="row-tag"
                    v-for="(subItem, subIndex) in operateVal(item.tag)"
                    :key="subIndex"
                  >
                    {{ subItem }}
                  </div>
                </div>
                <div style=" width:100%;display: flex; justify-content: space-between; align-items: center;">
                  <div class="tips">
                  {{ $t("creationTime") }}：{{
                    item.updateTime || item.createTime
                  }}
                </div>
                <div class="edit" v-if="isAdminOrUser(item)">
                  <div class="edit-btn" @click.stop="editApp(item)" style="margin-right:8px;">
                  <iconpark-icon color="#494E57" name="edit-box-line" size="15.75" style="margin-right:5px;"></iconpark-icon>
                  <span style="font-family: MiSans, MiSans;font-weight: 400;font-size: 14px;color: #494E57;line-height: 20px;text-align: left;font-style: normal;">{{ $t("edit") }}</span>
                </div>
                </div>
                <div
                  v-if="isAdminOrUser(item)"
                  class="opts-box"
                  :class="{ 'opts-box-active': activeIndexMoreClick === index }"
                @click.stop
              >
              <el-dropdown
                trigger="click"
                @command="(value) => handleCommand(value, item)"
                placement="top-end"
                class="opts-box-dropdown"
                @visible-change="handleVisibleChange($event, index)"
              >
                <span class="el-dropdown-link">
                  <!-- <i
                    style="transform: rotate(90deg); color: #828894"
                    class="el-icon-more"
                  ></i> -->
                  <iconpark-icon name="more-line" size="18" color="#383838" style="margin-top:2px;"></iconpark-icon>
                </span>
                <!-- api接入详情 -->
                <el-dropdown-menu
                  slot="dropdown"
                  class="opts-box-dropdown-menu"
                  v-if="paramSearch.applicationId == 1"
                >
                  <!-- <el-dropdown-item
                    command="editeApp"
                  >
                  <iconpark-icon color="#494E57" name="edit-box-line"></iconpark-icon>
                  <span style="color: #494E57;">{{ $t("edit") }}</span>
                  </el-dropdown-item> -->
                  <el-dropdown-item
                    v-permission="'presetApp'"
                    command="presetApp"
                    v-if="item.ownerType!='official'&&isAdmin"
                    style="display: block;"
                  >
                  <iconpark-icon color="#494E57" name="share-box-line"></iconpark-icon>
                  <span style="color: #494E57">{{ "设为预置" }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-permission="'presetApp'"
                    command="presetApp"
                    v-else-if="item.ownerType=='official'&&isAdmin"
                    style="display: block;"
                  >
                  <iconpark-icon color="#F53F3F" name="share-box-line"></iconpark-icon>
                  <span style="color: #F53F3F">{{ "取消预置" }}</span>
                  </el-dropdown-item>

                  <el-dropdown-item
                    v-permission="'deleteApp'"
                    command="deleteApp"
                  >
                  <iconpark-icon color="#494E57" name="delete-bin-4-line"></iconpark-icon>
                  <span style="color: #494E57">{{ $t("delete") }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-permission="'grantApp'"
                    command="grantApp"
                  >
                    <iconpark-icon color="#494E57" name="user-add-line"></iconpark-icon>
                    <span style="color: #494E57">{{ $t("authorization") }}</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
                <!-- 算法模型详情 -->
                <el-dropdown-menu
                  slot="dropdown"
                  v-if="paramSearch.applicationId == 2"
                >
                  <el-dropdown-item command="editeModelApp">
                    <img
                      style="height: 15px; margin-right: 8px"
                      src="@/assets/images/edit-line.svg"
                    />
                    <span>{{ $t("edit") }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="detailModelApp">
                    <img
                      style="height: 15px; margin-right: 8px"
                      src="@/assets/images/chat-1-line.svg"
                    />
                    <span>{{ $t("details") }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="deleteModelApp">
                    <img
                      style="height: 15px; margin-right: 8px"
                      src="@/assets/images/delete-bin-4-line.svg"
                    />
                    <span>{{ $t("delete") }}</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              </div>
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
          </div>
        </div>
      </div>
      <Createplugin
        v-if="dialogVisible"
        :dialogVisible="dialogVisible"
        @confirmApplication="confirmApplication"
        @cancelCreateplugin="cancelCreateplugin"
      ></Createplugin>
      <!-- <el-dialog
      :visible.sync="editDialogVisible"
      width="100%"
      fullscreen
      :show-close="false"
      class="flexDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    > 
      //  能力设置放入新页面 
      <PluginSetting v-else
        :appConfigForm="editItem"
        @configCloseDialog="configCloseDialog"
        @configShowSecretKey="configShowSecretKey"
      ></PluginSetting>
     </el-dialog> -->
      <!-- <el-dialog
      :visible.sync="editDetailDialogVisible"
      width="100%"
      fullscreen
      :show-close="false"
      class="flexDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      // 查看插件详情页面 
      <pluginDetails
        v-if="editDetailDialogVisible"
        :appConfigForm="editItem"
        @configCloseDialog="configCloseDialog"
        @configShowSecretKey="configShowSecretKey"
        @configOpenModelDialog="configOpenModelDialog"
      >
      </pluginDetails>
    </el-dialog> -->
      <!-- <el-dialog
      :visible.sync="editModelDialogVisible"
      width="100%"
      fullscreen
      :show-close="false"
      class="flexDialog"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      // 模型算法页面 
      <modelAlgorithmPlugin
        v-if="editModelDialogVisible"
        :appConfigForm="editItem"
        @configCloseDialog="configCloseDialog"
        @configShowSecretKey="configShowSecretKey"
      >
      </modelAlgorithmPlugin>
    </el-dialog> -->
      <!-- 删除应用 -->
      <delApplication
        v-if="deleteDialogVisible"
        deleteName="插件"
        :deleteDialogVisible="deleteDialogVisible"
        @configCancelDelete="configCancelDelete"
        :params="deleteItem"
      ></delApplication>
    </div>
    <!-- 模型算法页面 -->
    <modelAlgorithmPlugin
      v-if="editModelDialogVisible"
      :appConfigForm="editItem"
      @configCloseDialog="configCloseDialog"
      @configShowSecretKey="configShowSecretKey"
    >
    </modelAlgorithmPlugin>
    <!--  查看插件详情页面  -->
    <pluginDetails
      v-if="editDetailDialogVisible"
      :appConfigForm="editItem"
      @configCloseDialog="configCloseDialog"
      @configShowSecretKey="configShowSecretKey"
      @configOpenModelDialog="configOpenModelDialog"
    >
    </pluginDetails> 
    <el-dialog
      :visible.sync="editDialogVisible"
      width="100%"
      fullscreen
      :show-close="false"
      class="flexDialog-edit"
      destroy-on-close
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    > 
      <PluginSetting
        v-if="editDialogVisible"
        :appConfigForm="editItem"
        @configCloseDialog="configCloseDialog"
        @configShowSecretKey="configShowSecretKey"
      ></PluginSetting>
     </el-dialog>
     <!-- 授权 -->
     <el-dialog
      :title="$t('authorizeTo')"
      :visible.sync="grantVisible"
      width="720px"
      top="10vh"
      class="grantDialog"
      destroy-on-close
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <GrantData
        :data-id="grantData.componentId"
        dataType="plugin"
        v-if="grantVisible"
        data-type="app"
        :queryCurrentTenantUserFlag="true"
        @cancelGrant="cancelGrant"
      ></GrantData>
      </el-dialog>
  </div>
</template>
<script>
import AddOrEdit from "./addOrEdit.vue";
import Createplugin from "./Createplugin.vue";
import PluginSetting from "./components/PluginSetting.vue";
import modelAlgorithmPlugin from "@/views/systemManage/pluginManage/components/modelAlgorithmPlugin.vue";
import pluginDetails from "@/views/toolManager/pluginManage/components/pluginDetails.vue";
import delApplication from "@/views/workflowConfig/components/deleteApplication.vue";
import GrantData from "@/views/appManage/components/GrantData.vue";

import {
  saveComponent,
  pluginList,
  getModelPluginApiList,
  addModelPluginApi,
  addCodePlugin,
  deleteModelPluginApi,
} from "@/api/workflow";
import {pluginPreset,apiGetLabelTypes} from "@/api/app"

export default {
  components: {
    AddOrEdit,
    Createplugin,
    delApplication,
    PluginSetting,
    modelAlgorithmPlugin,
    pluginDetails,
    GrantData,
  },
  data() {
    return {
      activeIndexMoreClick:null,
      showDeleteIcon: false,
      editDialogVisible: false,
      grantVisible: false,
      grantData: {},
      editModelDialogVisible: false,
      editDetailDialogVisible: false,
      editItem: {},
      type: 1,
	  typeList:[],
	  labels:'',
      pageType: "list",
      pageObj: {
        pageNo: 1,
        pageSize: 24,
        total: 0,
      },
      paramSearch: {
        name: "", // 模型名称
        tag: "", // 厂商名称
        applicationId: 1,
        applicationIdNew: 1,
      },
      list: [],
      loading: false,
      dialogVisible: false,
      deleteItem: "",
      deleteDialogVisible: false,
      addLoading: false,
      actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
      pluginForm: {
        componentName: "", // 名称
        componentDesc: "", // 描述
        headPortraitUrl: "", // 头像
        appId: "", // API Id
        apiSecret: "", // API Secret
        apiKey: "", // API Key
      },
      change: false,
      applicationIdList: [
        {
          applicationName: this.$t("apiAccessLabel"),
          applicationId: 1,
        },
        {
          applicationName: this.$t("modelAlgorithmLabel"),
          applicationId: 2,
        },
        // {
        //   applicationName: this.$t("keywordFilterLabel"),
        //   applicationId: 3,
        // },
        // {
        //   applicationName: this.$t("customLabel"),
        //   applicationId: 4,
        // },
        
      ],
    };
  },
  computed: {
    tenantId() {
      const tenantId = sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))?.tenantId
        : "";
      return tenantId;
    },
    isAdmin(){
      return JSON.parse(sessionStorage.getItem("user")).powerType==0
    },
    isAdminOrUser(){
      return (data)=>{
        let obj=JSON.parse(sessionStorage.getItem("user"))
        return this.isAdmin || obj.accountName == data.createUser
      }
    }
  },
  mounted() {
    this.getPluginList();
	this.getLabelTypes();
  },
  methods: {
	// 分类
	async getLabelTypes() {
	  let res = await apiGetLabelTypes({ type: 1 });
	  if (res.code == "000000") {
	    this.typeList = res.data || [];
	    this.typeList = this.typeList.filter(item => item.name != '全部')
	  }
	},
    grantApp(item) {
      this.grantData = item;
      this.grantVisible = true;
    },
    cancelGrant() {
      this.grantVisible = false;
    },
    handleVisibleChange(val, index) {
      if(val){
        this.activeIndexMoreClick = index;
      }else {
        this.activeIndexMoreClick = null;
      }
      
    },
    opreateLabels(labels) {
      return labels ? labels?.split(",") : [];
    },
    configShowSecretKey() {},

    configCloseDialog() {
      this.editDialogVisible = false;
      this.editModelDialogVisible = false;
      this.editDetailDialogVisible = false;
      this.paramSearch.applicationId == 1
        ? this.getPluginList()
        : this.getModelPluginApiList();
      this.$emit("displayHeadHandler", true);
    },
    configOpenModelDialog() {
      this.$emit("displayHeadHandler", false)
      this.editDialogVisible = true;
      this.editDetailDialogVisible = false;
    },
    getPluginList() {
      this.loading = true;
      pluginList({
        pageNo: this.pageObj.pageNo,
        pageSize: this.pageObj.pageSize,
        componentName: this.paramSearch.name,
        ownerType:"personalGrantTenant",
        labels:this.labels
      })
        .then((res) => {
          if (res.data?.records?.length) {
            this.list = res.data.records || [];
            this.pageObj.total = res.data?.totalRow || 0;
          } else {
            this.list = [];
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },

    editApp(item) {
      this.editItem = item;
      this.$emit("displayHeadHandler", false);
      this.editDialogVisible = true;
    },
    editModelApp(item) {
      this.editItem = item;
      this.editModelDialogVisible = true;
      this.editDialogVisible = false;
    },
    detailModelApp(item) {
      this.editItem = item;
      this.editDetailDialogVisible = true;
    },
    getPluginPresetApi(data){
      pluginPreset({id:data.id}).then(res=>{
        if(res.code=="000000"){
          this.getPluginList()
        }else{
          this.$message({
            type:"error",
            message:res.msg
          })
        }
      })
    },
    handleCommand(value, item) {
      if (value == "copyApp") this.copyAppInfo(item);
      if (value == "editeApp") this.editApp(item);
      if (value == "deleteApp") this.openDelete(item);
      if (value == "grantApp") this.grantApp(item);
      if (value == "editeModelApp") this.editModelApp(item);
      if (value == "detailModelApp") this.detailModelApp(item);
      if (value == "deleteModelApp") this.deleteModelApplist(item);
      if (value == 'presetApp') this.getPluginPresetApi(item);
      
    },
    openDelete(item) {
      this.deleteItem = item;
      this.deleteDialogVisible = true;
    },
    configCancelDelete() {
      this.deleteDialogVisible = false;
      this.getPluginList();
    },
    deleteModelApplist(item) {
      this.$confirm("请确认是否删除", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(() => {
        deleteModelPluginApi([item.id]).then((res) => {
          if (res.code == "000000") {
            this.getModelPluginApiList();
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        });
      });
    },
    confirmApplication(params) {
      this.dialogVisible = false;
      if (params.type == 1) {
        saveComponent({
          componentName: params.name,
          description: params.description,
          icon: params.icon,
          status: params.status,
          type: params.type,
          labels: params.labels?.length ? params.labels.join(",") : "",
          tenantId: this.tenantId,
        }).then((res) => {
          if (res.code == "000000") {
            this.$message.success(this.$t("success"));
            this.$set(this.paramSearch, "applicationIdNew", 1);
            this.paramSearch.applicationId = 1;
            this.applicationIdChange();
            this.getPluginList();
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        });
      } else if (params.type == 2) {
        addModelPluginApi({
          ...params,
          labels: params.labels?.length ? params.labels.join(",") : "",
        }).then((res) => {
          if (res.code == "000000") {
            this.$set(this.paramSearch, "applicationIdNew", 2);
            this.paramSearch.applicationId = 2;
            this.applicationIdChange();
            this.getModelPluginApiList();
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        });
      }else if (params.type == 6) {
        addCodePlugin({
          componentName: params.name,
          description: params.description,
          icon: params.icon,
          status: params.status,
          type: 6,
          labels: params.labels?.length ? params.labels.join(',') : '',
          tenantId: this.tenantId
        }).then((res) => {
          if (res.code == "000000") {
            this.$message.success(this.$t('success'))
            this.$set(this.paramSearch, "applicationIdNew", 1);
            this.paramSearch.applicationId = 1;
            this.applicationIdChange();
            this.getPluginList();
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        });
      }
    },
    cancelCreateplugin() {
      this.dialogVisible = false;
    },
    operateVal(val) {
      return val ? val.split(",") : [];
    },
    searchHandler() {
      this.list = [];
      this.pageObj.pageNo = 1;
      this.paramSearch.applicationId = this.paramSearch.applicationIdNew;
      if (this.paramSearch.applicationId == 1) {
        this.getPluginList();
      } else if (this.paramSearch.applicationId == 2) {
        this.getModelPluginApiList();
      } else {
        this.list = [];
        this.pageObj.total = 0;
      }
    },
    getModelPluginApiList() {
      getModelPluginApiList({
        pageNo: this.pageObj.pageNo,
        pageSize: this.pageObj.pageSize,
        name: this.paramSearch.name,
      }).then((res) => {
        if (res.data?.records?.length) {
          this.list = res.data.records || [];
          this.pageObj.total = res.data?.totalRow || 0;
        } else {
          this.list = [];
        }
      });
    },
    handleCurrentChange(page) {
      this.pageObj.pageNo = page;
      this.paramSearch.applicationId == 1
        ? this.getPluginList()
        : this.getModelPluginApiList();
    },
    handleSizeChange(size) {
      this.pageObj.pageSize = size;
      this.paramSearch.applicationId == 1
        ? this.getPluginList()
        : this.getModelPluginApiList();
    },
    createVoice() {
      this.dialogVisible = true;
    },
    applicationIdChange() {
      this.$forceUpdate();
    },
    goPluginDetail(item) {
      this.editApp(item);
    },
  },
};
</script>
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>
<style lang="scss">
.flexDialog-edit {
  .el-dialog__body {
    padding: 0!important;
  }
  .el-dialog__header {
    background: #fff!important;
    padding: 0!important;
  }
}
.plugin-dialog {
  background: #fff !important;
  .el-dialog__header {
    padding: 32px 32px 16px;
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
    //   display: block !important;
    .el-dialog__title {
      height: 24px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
    }
  }
  .el-dialog__body {
    padding: 0 32px 32px !important;
    background: #fff !important;
    .el-input__inner {
      border-radius: 4px;
    }
    .el-input__inner:focus {
      border-color: #1c50fd;
    }

    .select-form {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      .el-form-item__content {
        width: 100%;
        .el-select {
          width: 100%;
        }
      }
    }

    .checkboxOuter {
      height: 410px;
      padding: 0 8px;
      overflow-y: scroll;
    }
    .el-form-item__label {
      color: #383d47;
      font-size: 16px;
      font-weight: 500;
    }
  }
  .dialog-footer {
    text-align: left;
    .el-button {
      border-radius: 4px;
    }
    .el-button--primary {
      background: #1c50fd;
      border-color: #1c50fd;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
}
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
<style lang="scss" src="./flexDialog.scss" scoped></style>
<style lang="scss" scoped>
::v-deep .el-popper[x-placement^="bottom"] .popper__arrow {
  display: none;
}
::v-deep .btn-prev {
  background-color: transparent !important;
  border-radius: 4px !important;
  border: 1px solid #dddfe8;
}

.plugin {
  padding: 24px 0 0;
  height: 100%;
  .title-layout {
    display: flex;
    font-size: 24px;
  }
  &-title {
    height: 40px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #383d47;
    line-height: 40px;
  }
  &-content {
    width: 100%;
    height: calc(100vh - 96px - 24px);
    display: flex;
    // margin-top: 16px;
    &-right {
      width: 100%;
      height: 100%;
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #ffffff;
      border-radius: 4px;
      //   padding: 20px 24px;
      //   border: 1px solid #e1e4eb;
      .head {
        display: flex;
        justify-content: space-between;
        height: 40px;
        width: 100%;
        &-tag {
          display: flex;
          align-items: center;
          li {
            padding: 0 12px;
            margin-right: 12px;
            height: 40px;
            line-height: 40px;
            background: #f2f5fa;
            border-radius: 4px;
            cursor: pointer;
          }
          .seleted {
            background: rgba(54, 102, 234, 0.1);
            color: #3666ea;
          }
        }
        ::v-deep .el-input__suffix {
          line-height: 40px;
          margin-right: 6px;
          cursor: pointer;
          .el-input__suffix-inner {
            .el-icon-search {
              font-size: 16px;
              color: #828894;
            }
          }
        }
      }
      .list {
        margin-top: 24px;
        overflow-y: auto;
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
        flex-wrap: wrap;
        grid-gap: 24px 24px;
        &-item {
          padding: 16px;
          background: #ffffff;
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          display: flex;
          flex-direction: column;
          position: relative;
          cursor: pointer;
          .preset{
            width: 39px;
            height: 23px;
            background: #EBEEF2;
            border-radius: 0px 7px 0px 8px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #1D2129;
            line-height: 23px;
            text-align: center;
            position: absolute;
            top: 0;
            right: 0;
          }
          .row-tips {
            position: absolute;
            right: 0;
            top: 0;
            width: 51px;
            height: 23px;
            line-height: 23px;
            text-align: center;
            background: #ebeef2;
            border-radius: 0px 1px 0px 2px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #383838;
          }
          &:hover {
            box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
          }
          &:nth-child(4n) {
            margin-right: 0;
          }
          &-top {
            display: flex;
            align-items: center;
            .avatar {
              margin-right: 12px;
              width: 40px;
              height: 40px;
            }
            .text {
              .row-other {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
                margin-top: 4px;
                .tipsTpye {
                  background: #f0f4fa;
                  border-radius: 2px;
                  padding: 2px 4px;
                  .tips-name {
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 12px;
                    color: #828894;
                    line-height: 16px;
                  }
                }
                .labels {
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  flex-wrap: wrap;
                  gap: 8px;
                  &-item {
                    padding: 4px;
                    background: #ebeef2;
                    border-radius: 2px;
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 12px;
                    color: #383838;
                  }
                }
                .labels-item {
                  padding: 4px;
                    background: #ebeef2;
                    border-radius: 2px;
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 12px;
                    color: #383838;
                }
                .api {
                  background: #d0dffd;
                  .tips-name {
                    font-weight: 400;
                    font-size: 12px;
                    color: #1747e5;
                  }
                }
                .moxingsuanfa {
                  background: #ebddfe;
                  .tips-name {
                    font-weight: 400;
                    font-size: 12px;
                    color: #7e56eb;
                  }
                }
              }
            }
          }
          &-center {
            margin-top: 12px;
            height: 44px;
            .componentDesc {
              font-size: 14px;
              color: #a6a6a6;
              line-height: 22px;
              height: 100%;
              text-align: justify;
              display: -webkit-box;
              -webkit-box-orient: vertical;
              overflow: hidden;
              text-overflow: ellipsis;
              -webkit-line-clamp: 2;
            }
          }
          &-bottom {
            margin-top: 20px;
            flex: 1;
            display: flex;
            align-items: center;
            font-family: MiSans, MiSans;
            font-size: 14px;
            color: #768094;
            .tips {
              display: flex;
              align-items: center;
              color: #383d47;
              font-size: 12px;
              color: #a6a6a6;
              .circle {
                margin-right: 5px;
                width: 14px;
                height: 14px;
                border-radius: 50%;
                background: #b4bccc;
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                .el-icon-caret-right {
                  font-size: 10px;
                  color: #fff;
                }
              }
            }
          }
          .noBorder {
            border-top: none;
          }
          .flex-start {
            align-items: flex-start;
          }
          .text {
            // flex: 1;
            width: calc(100% - 120px);
          }
          .row {
            display: flex;
            align-items: center;
            margin-bottom: 4px;
            &-name {
              height: 24px;
              font-family: MiSans, MiSans;
              font-weight: 600;
              font-size: 16px;
              color: #383d47;
              line-height: 24px;
              max-width: calc(100%);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
            &-gender {
              width: 16px;
              height: 24px;
            }
            &-tag {
              margin-right: 8px;
              height: 20px;
              line-height: 18px;
              border-radius: 4px;
              border: 1px solid #e1e4eb;
              padding: 0 8px;
              font-family: MiSans, MiSans;
              font-size: 12px;
              color: #768094;
            }
          }
          .menu {
            height: 20px;
            margin-left: auto;
            transform: rotate(90deg);
            cursor: pointer;
          }
          .edit {
            height: 32px;
            // position: absolute;
            margin-left: 60px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            // right: 16px;
            // bottom: 16px;
            display: flex;
            &-btn{
              width: 68px;
              height: 32px;
              display: flex;
              justify-content: center;
              align-items: center;
              &:hover{
                background-color:rgba(180, 188, 204, 0.2) ;
                cursor: pointer;
              }
              span{
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 14px;
                color: #494E57;
                line-height: 20px;
                text-align: left;
                font-style: normal;
              }
            }
            
          }
        }
      }
    }
  }
  .no-scrollbar {
    overflow: auto;
    /* 确保内容仍然可以滚动 */
    -ms-overflow-style: none;
    /* Internet Explorer 10+ */
    scrollbar-width: none;
    /* Firefox */
  }
.dialog-config {
  .form-flex {
    display: flex;
    align-items: center;
  }
  .url-layout {
    display: flex;
    align-items: flex-end;
  }
  .url-img {
    width: 68px;
    height: 68px;
    border-radius: 50%;
    position: relative;
  }
  .delete-icon {
    position: absolute;
    right: 0;
    top: 0;
    cursor: pointer;
  }
}
::v-deep .el-dialog__body {
  padding: 40px 20px 0;
}

::v-deep .el-dropdown-menu__item {
  display: flex;
  align-items: center;
}
}
::v-deep .el-dialog {
  border-radius: 4px;
  .el-dialog__body {
    padding: 16px 32px;
  }
  .el-dialog__header {
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
    border-radius: 8px 8px 0px 0px;
    padding: 32px 32px 16px 32px;
  }
  .el-dialog__footer {
    text-align: left;
  }
}

.row-name{
  display: flex;
  align-items: center;
  .successSign{
    display: flex;
    align-items: center;
    margin-left: 4px;
  }
}
</style>
 