<template>
  <div class="page-wrap" v-loading="loading" :style="type == 'details' ? 'background-color: #fff;' : ''">
    <div v-if="type != 'details'">
      <div class="page-header">
        <el-row class="header-wrap" type="flex" justify="space-between" align="middle">
          <el-col style="
              font-size: 18px;
              display: flex;
              align-items: center;
              font-weight: 550;
            " :span="6">
            <img style="
                cursor: pointer;
                width: 18px;
                height: 18px;
                margin-right: 10px;
              " src="@/assets/images/arrow-go-back-fill.svg" @click="closePage" />
            <span v-if="type == 'add'">{{ $t("newSlice") }} {{ $t("mcp") }}</span>
            <span v-else>修改 {{ $t("mcp") }}</span>
          </el-col>
          <el-col>
            <el-row type="flex" justify="end" align="middle">
              <!-- <span style="font-weight: normal;">{{isktFlag}}</span>
							<el-switch
                              v-model="isktFlag"
                              active-value="开通"
                              inactive-value="关闭"
                              active-color="#1747E5"
                              inactive-color="#EAECF0">
                            </el-switch> -->
              <el-button @click="addSaveDraft('1')" style="
                  width: 154p;
                  border: 1px solid #1747e5;
                  color: #1747e5;
                  border-radius: 2px;
                  margin: 0 10px;
                ">{{ $t("saveDraft") }}</el-button>
              <el-button type="primary" @click="addSaveDraft('2')">{{
                $t("publishMcp")
              }}</el-button>
            </el-row>
            <!-- <el-row type="flex" justify="end" align="middle" v-else>
				
				<el-button type="primary" @click="addEdit">{{
				  $t("edit")
				}}</el-button>
				 
			  </el-row> -->
          </el-col>
        </el-row>
      </div>
      <div class="line"></div>
      <div class="form-box-c">
        <div class="form-box">
          <div class="formOuter">
            <div class="uploadImg">
              <div>
                <el-form :model="appForm" :rules="rules" ref="ruleForm" class="demo-ruleForm">
                  <el-form-item label="" prop="mcpName">
                    <div class="mcp-title">
                      服务名称<span style="color: red; margin-left: 5px">*</span>
                    </div>
                    <el-input v-model="appForm.mcpName" show-word-limit maxlength="50" style="width: 100%"
                      placeholder="请输入MCP服务名称" />
                  </el-form-item>
                  <el-form-item label="">
                    <div class="mcp-title flex-center">
                      图标
                      <span class="img-tips">仅支持JPG、PNG格式图片，大小在20</span>

                    </div>
                    <div class="uploadOuter">
                      <el-upload :action="actionUrl" :data="{ filePath: 'agent_source' }"
                        :class="{ hideBox: uploadBtnLogo }" :file-list="fileListLogo" :show-file-list="false" :limit="1"
                        class="logoAppUpload" list-type="picture-card" :on-remove="handleLogoRemove"
                        :on-success="handleLogoSuccess">
                        <div style="position: relative; height: 100%" @mouseenter="imgMouseenter"
                          @mouseleave="imgMouseleave" v-if="appForm.icon">
                          <img v-if="appForm.icon" :src="appForm.icon" style="
                              width: 80px;
                              height: 80px;
                              border-radius: 4px;
                              background: #dcdfe6;
                            " />
                          <!--  -->
                          <div v-show="showDeleteIcon" class="opts-btn">
                            <iconpark-icon name="edit-line" size="18" color="#FFFFFF"></iconpark-icon>
                            <iconpark-icon name="delete-bin-4-line" class="delete" size="18" color="#FFFFFF"
                              @click.stop="deleteLogo"></iconpark-icon>
                          </div>
                        </div>
                        <div v-else style="
                            height: 100%;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            background: #f2f4f7;
                          ">
                          <iconpark-icon name="add-line" size="24" color="#8c939d"></iconpark-icon>
                        </div>
                      </el-upload>
                      <el-button class="ai-btn" type="primary" :loading="imgLoading" @click="getImageUrl">
                        <img src="@/assets/images/ai-btn.svg" alt="">
                        AI生成
                      </el-button>
                    </div>
                  </el-form-item>
                  <el-form-item label="" prop="mcpType">
                    <div class="mcp-title">
                      服务类型<span style="color: red; margin-left: 5px">*</span>
                    </div>
                    <div class="application-type-box">
                      <el-select class="app-type-select" placeholder="请选择服务类型" style="width: 100%"
                        v-model="appForm.mcpType">
                        <el-option v-for="item in applicationTypeLists" :key="item.value" :value="item.value"
                          :label="item.label"></el-option>
                      </el-select>
                    </div>
                  </el-form-item>
                  <el-form-item label="" prop="description">
                    <div class="mcp-title">描述</div>
                    <el-input class="inputTextarea" type="textarea" :rows="5" v-model="appForm.description"
                      show-word-limit maxlength="200" style="width: 100%; text-align: left" placeholder="请输入描述" />
                  </el-form-item>

                  <div class="mcp-title">安装方式</div>
                  <div class="flex sse-item">
                    <div class="sse" v-for="(item, index) in installWayList" :key="index">
                      <img style="
                          cursor: pointer;
                          width: 28px;
                          height: 28px;
                          margin: 0 10px;
                        " :src="item.img" />
                      <div>
                        <el-radio v-model="appForm.installWay" :label="item.label">{{ item.displayLabel || item.label }}
                        </el-radio>
                      </div>
                    </div>
                  </div>
                  <div class="tool-box" v-if="appForm.installWay == 'custom'">
                    <div class="title">工具</div>
                    <!-- v-if="Object.keys(toolData.length > 0)" -->
                    <!-- 修改项 -->
                    <div class="tool-data" v-if="mcpFunctionList.length > 0">
                      <div class="tool-item" v-for="(item, index) in mcpFunctionList" :key="index">
                        <div class="item-top">
                          <div class="top-left">
                            <div class="l-icon">
                              <iconpark-icon name="tools-line" size="14" color="#1D2129"></iconpark-icon>
                            </div>
                            <span>{{ item.functionName }}</span>
                          </div>
                          <div class="top-right">
                            <iconpark-icon name="edit-box-line" size="14" color="#1D2129"
                              style="margin-right: 4px; cursor: pointer;"
                              @click="editTool(item, item.mcpId)"></iconpark-icon>
                            <iconpark-icon name="delete-bin-4-line" size="14" color="#1D2129" style="cursor: pointer;"
                              @click="delTool(item, item.functionId)"></iconpark-icon>
                          </div>
                        </div>
                        <div class="item-bottom">
                          {{ item.description }}
                        </div>
                      </div>
                      <div class="tool-btn">
                        <div class="btn-left">
                          <div class="add-btn" @click="addTool('add')">
                            <iconpark-icon name="add-line" size="12" color="#1747E5"
                              style="margin-right: 4px;"></iconpark-icon>
                            <span>配置工具</span>
                          </div>
                        </div>
                        <!-- <div class="btn-right">
                          <span>工具配置完成后需点击</span>
                          <div class="verify">验证</div>
                        </div> -->
                      </div>
                    </div>
                    <div class="not-tool-data" v-else>
                      <img style="width: 38px; height: 34px; margin-bottom: 16px;"
                        src="../../../assets/images/not_data.svg" alt="">
                      <div>暂无工具</div>
                      <div class="decs">通过添加第三方接口配置工具</div>
                      <div class="add-btn" @click="addTool('add')">
                        <iconpark-icon name="add-line" size="12" color="#1747E5"
                          style="margin-right: 4px;"></iconpark-icon>
                        <span>配置工具</span>
                      </div>
                    </div>
                  </div>
                  <div style="margin-bottom: 20px" v-if="
                    appForm.installWay == 'npx' || appForm.installWay == 'uvx'
                  ">
                    <div class="mcp-title" style="margin-bottom: 20px">
                      MCP服务配置<span>使用JSON表示，请确保格式正确</span>
                      <!-- <el-button type="primary" @click="addJson">JSON格式</el-button> -->
                    </div>
                    <codemirror v-model="appForm.npxUvxService" :options="options" class="codemirror-editor">
                    </codemirror>
                  </div>
                  <el-form-item label="" prop="url" v-if="appForm.installWay == 'sse'">
                    <div class="mcp-title flex" style="justify-content: space-between">
                      <div>
                        服务地址<span style="color: red; margin-left: 5px">*</span>
                      </div>
                      <div>
                        <span style="font-weight: normal">{{ isApiFlag }}</span>
                        <el-switch v-model="isApiFlag" active-value="需鉴权" inactive-value="无需鉴权" active-color="#1747E5"
                          inactive-color="#EAECF0">
                        </el-switch>
                      </div>
                    </div>
                    <div style="display: flex">
                      <el-input v-model="appForm.url" show-word-limit maxlength="500" style="width: 100%"
                        placeholder="请输入" />
                    </div>
                  </el-form-item>
                  <el-form-item label="" prop="url" v-if="isApiFlag == '需鉴权'">
                    <div class="mcp-title">
                      API Key<span style="color: red; margin-left: 5px">*</span>
                    </div>
                    <div style="display: flex">
                      <el-input v-model="appForm.apiKey" show-word-limit maxlength="500" style="width: 100%"
                        placeholder="请输入" />
                    </div>
                  </el-form-item>
                  <el-button v-show="appForm.installWay !== 'custom'" type="primary" @click="addCheck">查询</el-button>
                </el-form>
                <div class="checkList" v-if="mcpFunctionList.length > 0 && appForm.installWay !== 'custom'">
                  <p class="check-title">工具 {{ mcpFunctionList.length }}</p>
                  <div class="check-list" :class="mcpFunctionList.length > 0 ? 'check-list-auto' : ''">
                    <div class="list-item" v-for="(item, index) in mcpFunctionList" :key="index">
                      <div class="list-item-top">
                        <img src="@/assets/images/tools-line.svg" style="cursor: pointer; width: 24px; height: 24px"
                          alt="" />
                        <div class="text">{{ item.functionName }}</div>
                      </div>
                      <div class="list-item-label" :title="item.description">
                        {{ item.description }}
                      </div>
                    </div>
                  </div>
                </div>
                <div v-if="mcpFunctionList.length == 0 && checkFlag" class="no-data" style="margin-top: 100px">
                  <img src="@/assets/images/no-data.png" alt="" />
                  <div class="txt1">暂无工具</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <div class="page-header">
        <el-row class="header-wrap" type="flex" justify="space-between" align="middle">
          <el-col style="
              font-size: 18px;
              font-weight: 550;
              display: flex;
              align-items: center;
            " :span="6">
            <img style="
                cursor: pointer;
                width: 18px;
                height: 18px;
                margin-right: 10px;
              " src="@/assets/images/arrow-go-back-fill.svg" @click="closePage" />
            服务详情
          </el-col>
          <el-col v-if="isAdmin || userName == appForm.createUserName">
            <el-row type="flex" justify="end" align="middle">
              <el-button @click="type = 'edit'">{{ $t("edit") }}</el-button>
              <el-button style="
                  width: 154p;
                  color: #ff0000;
                  border-radius: 4px;
                  margin: 0 10px;
                " icon="el-icon-remove-outline" v-if="appForm.mcpServerUserStatus == 1"
                @click="addUpdateStatus('xjApp')">取消开通</el-button>
              <el-button type="primary" v-else @click="addUpdateStatus('sjApp')" class="open-service">开通服务</el-button>
            </el-row>
          </el-col>
        </el-row>
      </div>
      <div class="line"></div>
      <div class="form-box-c">
        <div class="form-box">
          <div class="list-item-top">
            <img :src="appForm.icon" alt="" />
            <div class="text">{{ appForm.mcpName }}</div>
            <div class="label-btn">{{ appForm.mcpType }}</div>
          </div>
          <div class="list-item-label" :title="appForm.description">
            {{ appForm.description }}
          </div>
          <div class="checkList">
            <p class="check-title">工具 {{ mcpFunctionList.length }}</p>
            <div class="check-list check-list-auto">
              <div class="list-item" v-for="(item, index) in mcpFunctionList" :key="index">
                <div class="list-item-top">
                  <div class="logo-ctn">
                    <img src="@/assets/images/tools-line.svg" style="cursor: pointer; width: 21.33px; height: 21.33px"
                      alt="" />
                  </div>
                  <div class="text">{{ item.functionName }}</div>
                </div>
                <div class="list-item-label" :title="item.description">
                  {{ item.description }}
                </div>
              </div>
              <div v-if="mcpFunctionList.length == 0" class="no-data">
                <img src="@/assets/images/no-data.png" alt="" />
                <div class="txt1">暂无工具</div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
    <!-- 子组件 -->
    <addOrEditMcp :isShowAddMcp.sync="isShowAddMcp" :editCustomData="editToolData" @submitData="handleSubmitData"
      :isAdd="isAdd" />
	<el-dialog title="温馨提示" :visible.sync="deleteDialogVisible" width="560px" class="deleteDialog"
		:before-close="cancelDelete" append-to-body>
		<p class="desc">内容尚未保存，是否确认退出？</p>
		<span slot="footer" class="dialog-footer">
			<el-button @click="deleteDialogVisible = false">{{ $t("cancel") }}</el-button>
			<el-button type="primary" @click="confirmDelete">确定</el-button>
		</span>
	</el-dialog>
  </div>
</template>
<script>
// 引入 uuid 模块
import { getAiImage } from "@/api/app";
import { debounce } from "lodash";
import addOrEditMcp from "./components/addOrEditMcp.vue";
import {
  addMcpServer,
  updateMcpServer,
  checkMcp,
  getDetail,
  updateStatus,
} from "@/api/mcp";
// import VueJsonEditor from 'vue-json-editor'
export default {
  components: {
    addOrEditMcp
  },

  data() {
    return {
      editToolData: {},  //编辑工具item数据
      toolData: null,
      isShowAddMcp: false,
	  deleteDialogVisible: false,
      isktFlag: "",
      userName: "",
      options: {
        line: true,
        theme: "rubyblue", // 主题
        tabSize: 8, // 制表符的宽度
        indentUnit: 2, // 一个块应该缩进多少个空格（无论这在编辑语言中意味着什么）。默认值为 2。
        firstLineNumber: 1, // 从哪个数字开始计算行数。默认值为 1。
        readOnly: false, // 只读
        autorefresh: true,
        smartIndent: true, // 上下文缩进
        lineNumbers: true, // 是否显示行号
        styleActiveLine: true, // 高亮选中行
        viewportMargin: Infinity, //处理高度自适应时搭配使用
        showCursorWhenSelecting: true, // 当选择处于活动状态时是否应绘制游标
        mode: "javascript",
      },

      appForm: {
        // icon: require("@/assets/images/appManagement/mcp.png"),
        icon: this.getRandomHeadImgDefaultBgColor(),
        mcpName: "",
        installWay: "sse",
        mcpType: "",
        description: "",
        url: "",
        mcpFunctionList: [],
        npxUvxService: {
          mcpServers: {
            占位: {
              command: "占位",
              args: "列表占位 (list[str])",
              env: "字典或者None占位（dict[str, str] | None）",
            },
          },
        },
        apiKey: "",
      },
      outputJsonData: {
        name: "John Doe",
        age: 30,
        email: "johndoe@example.com",
        hobbies: ["reading", "gaming", "traveling"],
        address: { street: "123 Main Street", city: "New York", state: "NY" },
      },
      mcpFunctionList: [],
      uploadBtnLogo: false,
      checkFlag: false,
      isApiFlag: false,
      fileListLogo: [],
      actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
      rules: {
        mcpName: [
          {
            required: true,
            message: "请输入服务名称",
            trigger: "blur",
          },
        ],
        mcpType: [
          {
            required: true,
            message: "请选择项目类型",
            trigger: "change",
          },
        ],
        // url: [{
        // 	required: true,
        // 	message: "请输入服务地址",
        // 	trigger: "blur"
        // }, ]
      },
      showDeleteIcon: false,
      installWayList: [
        {
          label: "npx",
          img: require("@/assets/images/npx.svg"),
        },
        {
          label: "uvx",
          img: require("@/assets/images/uvx.svg"),
        },
        {
          label: "sse",
          img: require("@/assets/images/sse.svg"),
        },
        {
          label: "custom",
          displayLabel: "自定义",
          img: require("@/assets/images/zdyi.svg"),
        },
      ],
      applicationTypeLists: [
        {
          label: "地图服务",
          value: "地图服务",
        },
        {
          label: "网页搜索",
          value: "网页搜索",
        },
        {
          label: "内容生产",
          value: "内容生产",
        },
        {
          label: "社交通讯",
          value: "社交通讯",
        },
        {
          label: "数据库",
          value: "数据库",
        },
        {
          label: "效率工具",
          value: "效率工具",
        },
        {
          label: "其他",
          value: "其他",
        },
      ],
      loading: false,
      imgLoading: false,
      isAdd: true
    };
  },
  props: {
    dialogVisibleApplication: {
      type: Boolean,
      default: false,
    },
    params: Object,
    type: String,
  },
  //监听属性 类似于data概念
  computed: {
    isAdmin() {
      const userType = sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))?.powerType
        : "";
      if (userType == "0") {
        return true;
      } else {
        return false;
      }
    },
  },
  async mounted() {
    this.appForm.npxUvxService = JSON.stringify(
      this.appForm.npxUvxService,
      null,
      2
    );
    if (this.type == "edit" || this.type == "details") {
      this.appForm = this.params;
      this.getDetails();
    }
    this.userName = sessionStorage.getItem("user")
      ? JSON.parse(sessionStorage.getItem("user"))?.accountName
      : "";
  },
  watch: {
    "appForm.npxUvxService"(newValue) {
      if (newValue != "") {
        this.appForm.npxUvxService = JSON.stringify(
          JSON.parse(newValue),
          null,
          2
        );
      }
    },
  },
  methods: {
    // 接收子组件传递的数据
    handleSubmitData(data) {
      if (data) {
        this.toolData = data;
        console.log('this.mcpFunctionList1', this.mcpFunctionList)
        if (this.isAdd) {
          this.mcpFunctionList.push(data)
        } else {
          this.mcpFunctionList = this.mcpFunctionList.map(item =>
            item.id == data.id ? data : item
          );
        }
      }
      this.isShowAddMcp = false;
      this.isAdd = false

    },
    //添加工具
    addTool() {
      this.isAdd = true
      this.editToolData = false
      this.isShowAddMcp = true;
    },
    //编辑工具
    editTool(item, id) {
      console.log(item)
      this.editToolData = { ...item }
      this.isShowAddMcp = true;
      this.isAdd = false
    },
    //删除工具
    delTool(item, id) {
      this.mcpFunctionList = this.mcpFunctionList.filter(i => i.functionId !== id);
      this.appForm.mcpFunctionList = this.appForm.mcpFunctionList.filter(i => i.functionId !== id);
      console.log(id, item)
    },
    imgMouseenter() {
      if (this.appForm.icon) {
        this.showDeleteIcon = true;
      }
    },
    imgMouseleave() {
      console.log("imgMouseleave");
      this.showDeleteIcon = false;
    },
    deleteLogo() {
      this.appForm.icon = this.getRandomHeadImgDefaultBgColor();
      return;
    },
    cancelTemplate() {
      if (this.type == "add") {
        this.$emit("cancelApplication", false);
      } else {
        this.$emit("cancelEditApplication", false);
      }
    },

    //保存
    addSaveDraft: debounce(function (type) {
      this.$refs.ruleForm.validate(async (valid) => {
        if (valid) {
          this.appForm.status = type;
          if (this.appForm.installWay == "sse") {
            if (this.appForm.url == "") {
              this.$message.warning("服务地址不能为空");
              return;
            }
            if (this.isApiFlag == "需鉴权") {
              if (this.appForm.apiKey == "") {
                this.$message.warning("Api Key不能为空");
                return;
              }
            }
          } else {
            this.appForm.url = "";
            this.appForm.apiKey = "";
          }
          if (this.appForm.installWay == "custom") {
            this.appForm.mcpFunctionList = this.mcpFunctionList
          }
          this.loading = true;
          addMcpServer(this.appForm)
            .then((data) => {
              if (data.code == "000000") {
                this.$message.success(data.msg);
                setTimeout(() => {
                  this.$emit("close", false);
                }, 1000);
              } else {
                this.$message.warning(data.msg);
              }
            })
            .finally(() => {
              this.loading = false;
            });

          // } else {

          // }
        } else {
          return false;
        }
      });
    }, 1000),
    //编辑
    addEdit() {
      this.$refs.ruleForm.validate(async (valid) => {
        console.log('valid', valid)
        if (valid) {
          this.loading = true;
          addMcpServer(this.appForm)
            .then((data) => {
              if (data.code == "000000") {
                this.$message.success(data.msg);
                setTimeout(() => {
                  this.$emit("close", false);
                }, 1000);
              } else {
                this.$message.warning(data.msg);
              }
            })
            .finally(() => {
              this.loading = false;
            });
        } else {
          return false;
        }
      });
    },
    //查询服务
    addCheck: debounce(function (type) {
      this.$refs.ruleForm.validate(async (valid) => {
        if (valid) {
          if (this.appForm.installWay == "custom") {
            //  this.appForm.mcpFunctionList.push(this.toolData)
            if (this.isAdd) {
              this.appForm.mcpFunctionList.push(this.toolData)
            } else {
              this.appForm.mcpFunctionList = this.toolData
            }
          }
          checkMcp(this.appForm).then((data) => {
            if (data.code == "000000") {
              this.checkFlag = true;
              this.mcpFunctionList = data.data;
            } else {
              this.$message.warning(data.msg);
            }
          });
        } else {
          return false;
        }
      });
    }, 1000),
    //查询服务
    addUpdateStatus: debounce(function (type) {
      let dataItem = {};
      if (type == "sjApp") {
        dataItem = {
          mcpId: this.appForm.mcpId,
          status: "1",
        };
      } else {
        dataItem = {
          mcpId: this.appForm.mcpId,
          status: "0",
        };
      }
      this.loading = true;

      updateStatus(dataItem)
        .then((data) => {
          if (data.code == "000000") {
            this.$message.success(data.msg);
            this.getDetails();
          } else {
            this.$message.warning(data.msg);
          }
        })
        .finally(() => {
          this.loading = false;
        });
    }, 1000),
    getDetails() {
      getDetail(this.appForm.mcpId).then((data) => {
        if (data.code == "000000") {
          this.appForm = data.data;
          this.mcpFunctionList = data.data.mcpFunctionList;
        }
      });
    },
    handleLogoRemove(file, fileList) {
      this.uploadBtnLogo = false;
      this.appForm.icon = "";
      this.fileListLogo = [];
    },
    handleLogoSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnLogo = true;
        this.appForm.icon =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListLogo = [];
      } else {
        this.uploadBtnLogo = false;
        this.appForm.icon = "";
        this.fileListLogo = [];
      }
    },

    getRandomHeadImgDefaultBgColor() {
      const imgList = [
        require("@/assets/images/appManagement/mcp.png"),
        require("@/assets/images/appManagement/mcp-default-2.svg"),
        require("@/assets/images/appManagement/mcp-default-3.svg"),
        require("@/assets/images/appManagement/mcp-default-4.svg"),
        require("@/assets/images/appManagement/mcp-default-5.svg"),
        require("@/assets/images/appManagement/mcp-default-6.svg"),
        require("@/assets/images/appManagement/mcp-default-7.svg"),
      ];
      const randomIndex = Math.floor(Math.random() * imgList.length);
      return imgList[randomIndex];
    },
    getImageUrl() {
      this.imgLoading = true;
      getAiImage({
        topic: this.appForm.mcpName,
        description: this.appForm.description,
      })
        .then((res) => {
          if (res.code == "000000") {
            this.appForm.icon =
              res.data || this.getRandomHeadImgDefaultBgColor();
          } else {
            this.$message.warning("生成失败");
          }

          this.imgLoading = false;
        })
        .catch(() => {
          this.imgLoading = false;
        });
    },
    cancelDelete() {
				this.deleteDialogVisible = false
		},
    closePage() {
		if(this.type=='add'){
			if(this.appForm.mcpName!=''||this.appForm.mcpType!=''||this.appForm.description!=''||this.appForm.url!=''||this.appForm.apiKey!=''){
				  this.deleteDialogVisible = true
			}else{
			this.$emit("close", false);
		}			
		}else{
			this.$emit("close", false);
		}
      
    },
	confirmDelete(){
		this.$emit("close", false);
	}
  },
};
</script>
<style lang="scss" scoped>
::v-deep .main {
  background-color: #f6f6f6 !important;
}

::v-deep .CodeMirror pre.CodeMirror-line,
.CodeMirror pre.CodeMirror-line-like {
  padding: 6px 4px !important;
}

.mcp-title {
  font-size: 14px;
  font-weight: 500;

  span {
    margin-left: 20px;
    color: #666;
    font-size: 14px;
    font-weight: normal;
  }

  .img-tips {
    color: #86909C;
    margin-left: 8px;
    display: inline-block;
    line-height: 20px;
  }
}

.el-radio {
  display: flex;
  flex-direction: row-reverse;
  /* 反转子元素的排列顺序 */
  justify-content: flex-end;
  margin-left: 0 !important;
}

::v-deep .el-radio__label {
  width: 46px;
  font-size: 18px;
  margin-right: 70px !important;
}

.page-wrap {
  width: 100%;
  min-height: 100vh;
  background-color: #f6f6f6;

  .el-icon-back {
    cursor: pointer;
  }

  .page-header {
    height: 72px;
    width: 100%;
    background: #fff;

    .header-wrap {
      height: 72px;
      padding: 0 28px;

      .open-service {
        &:hover {
          background: #1036c4 !important;
        }

        &:focus {
          background: #1036c4 !important;
        }
      }
    }

    .demo-form-inline {
      align-items: center;

      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .line {
    width: 100%;
    height: 1px;
    background: #e7e7e7;
  }
}

.sse-item {
  justify-content: space-between;
}

.sse {
  width: 198px;
  height: 40px;
  line-height: 40px;
  border: 1px solid #eee;
  margin: 10px 0 20px 0;
  display: flex;
  align-items: center;
  position: relative;
}

.checkList {
  margin: 20px 0;
}

.check-title {
  font-size: 18px;
  font-weight: 500;
  position: relative;
  padding-left: 14px;
  line-height: 20px;
  box-sizing: border-box;

  &::before {
    content: "";
    width: 4px;
    height: 20px;
    background: #1c50fd;
    position: absolute;
    left: 0;
  }
}

.check-list-auto {
  // height: 60vh;
  // overflow-y: auto;
}

.check-list {
  margin-top: 30px;

  .list-item {
    border: 1px solid #eee;
    padding: 18px;
    box-sizing: border-box;
    margin-bottom: 10px;

    .list-item-top {
      margin: 0;

      .text {
        font-size: 16px !important;
      }

      .logo-ctn {
        width: 32px;
        height: 32px;
        display: flex;
        justify-content: center;
        align-items: center;
        background: #e7e7e7;
        border-radius: 2px;

      }
    }

    img {
      width: 30px;
      height: 30px;
    }
  }
}

.list-item-top {
  display: flex;
  align-items: center;
  // margin: 16px 16px 12px;

  // height: 56px;
  // border-bottom: 1px solid rgba(0, 0, 0, 0.12);
  img {
    width: 44px;
    height: 44px;
  }

  .text {
    margin: 0 20px 0 20px;
    font-family: MiSans-Medium;
    font-weight: 550;
    font-size: 18px;
    color: #383d47;
    font-weight: 550;
  }

  .list-item-label {
    font-size: 14px;
  }

  .tips {
    //   width: 40px;
    padding: 0 8px;
    height: 20px;
    line-height: 20px;
    background: linear-gradient(270deg,
        rgba(142, 101, 255, 0.1) 0%,
        rgba(28, 80, 253, 0.1) 100%);
    border-radius: 4px;
    font-family: MiSans, MiSans;
    font-size: 12px;
    text-align: center;
    color: #383d47;
  }
}

.label-btn {
  padding: 6px 7px;
  background-color: #e1e4eb;
  font-size: 12px;
}

.list-item-label {
  color: #666;
  margin-top: 20px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 22px;
  font-size: 14px;
}

.inputTextarea {
  ::v-deep .el-textarea__inner {
    font-family: MiSans, MiSans;
  }
}

.logoAppUpload {
  text-align: center;

  ::v-deep .el-upload--picture-card {
    border: 0;
    width: 80px;
    height: 80px;
  }
}

.form-box-c {
  height: calc(100vh - 100px);
  overflow-y: auto;
}

.form-box {
  position: relative;
  width: 960px;
  // height: 100%;
  // margin-left: 20%;
  left: 50%;
  transform: translateX(-50%);
  background-color: #fff;
  margin-top: 20px;
  padding: 30px;
  box-sizing: border-box;
}

.demo-ruleForm {
  ::v-deep .el-form-item__label {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #383d47;
    line-height: 32px;
  }
}

.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
}

.aligns {
  align-items: center;
}

.just {
  justify-content: space-between;
}

.application-type-box {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 16px;

  .application-type {
    width: 202px;
    height: 80px;
    display: flex;
    justify-content: center;
    background: #ffffff;
    border-radius: 2px;
    border: 1px solid #d5d8de;
    align-items: center;
    cursor: pointer;

    .type-item {
      display: inline-flex;
      flex-direction: column;
      text-align: center;
      font-weight: 400;
      font-size: 14px;
      color: #494e57;
      line-height: 18px;

      img {
        width: 20px;
        height: 20px;
        margin: 0px auto 8px;
      }
    }

    &.active {
      background: rgba(28, 80, 253, 0.05);
      border: 1px solid #1747e5;
    }
  }
}

::v-deep .el-textarea .el-input__count {
  left: 15px !important;
}

.uploadOuter {
  width: 100%;
  display: flex;
  align-items: end;

  :deep(.ai-btn) {
    height: 32px;
    background: linear-gradient(270deg,
        rgba(142, 101, 255, 0.15) 0%,
        rgba(23, 71, 229, 0.15) 100%);
    border-radius: 2px;
    display: inline-flex;
    align-items: center;
    border: 0px;
    padding: 0px 8px;

    margin-left: 16px;
    font-size: 14px;
    color: #1747e5;

    span {
      display: inline-flex;
      align-items: center;
    }

    img {
      margin-right: 2px;
      width: 16px;
      height: 16px;
      border: none;
    }
  }
}

.tool-box {
  margin-bottom: 20px;

  .title {
    padding: 0 10px;
    border-left: 4px solid #1747E5;
    font-size: 18px;
    color: #1D2129;
  }

  .tool-data {
    width: 100%;

    .tool-item {
      width: 100%;
      padding: 16px;
      border-radius: 4px;
      border: 1px solid #E7E7E7;
      margin-top: 10px;

      .item-top {
        display: flex;
        justify-content: space-between;

        .top-left {
          display: flex;
          align-items: center;
          font-size: 16px;
          color: #1D2129;

          .l-icon {
            padding: 5px;
            background: #F2F3F5;
            border-radius: 2px;
            margin-right: 12px;
            cursor: pointer;
          }
        }

        .top-right {
          display: flex;
        }
      }

      .item-bottom {
        margin-top: 12px;
        font-weight: 400;
        font-size: 14px;
        color: #86909C;
      }
    }

    .tool-btn {
      margin-top: 12px;
      display: flex;
      justify-content: space-between;

      .btn-left {
        display: flex;
        align-items: center;
        cursor: pointer;
        font-weight: 400;
        font-size: 14px;
        color: #1747E5;
      }

      .btn-right {
        display: flex;
        align-items: center;
        font-weight: 400;
        font-size: 14px;
        color: #86909C;

        .verify {
          padding: 8px 12px;
          margin-left: 12px;
          background: #1747E5;
          border-radius: 4px;
          color: #FFFFFF;
          cursor: pointer;
        }
      }
    }
  }

  .not-tool-data {
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    font-size: 16px;
    color: #1D2129;

    .decs {
      font-weight: 400;
      font-size: 12px;
      color: #86909C;
      margin-top: 16px;
    }

    .add-btn {
      cursor: pointer;
      display: flex;
      align-items: center;
      padding: 6px 12px;
      margin-top: 16px;
      font-weight: 400;
      font-size: 14px;
      color: #1747E5;
      border-radius: 4px;
      border: 1px solid #1747E5;
    }
  }
}


::v-deep .el-date-editor .el-range-separator {
  width: 8%;
}

.codemirror-editor {
  width: 100%;
  height: 300px;
  /* 根据需要调整高度 */
}
</style>
