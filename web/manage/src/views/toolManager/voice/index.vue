<template>
  <div class="voice">
    <template v-if="pageType == 'list'">
      <!-- <ul class="title-layout">
        <li
          class="voice-title"
          :class="[type == 1 ? 'selected' : '']"
          @click="checkType(1)"
        >
          {{ $t("speechToText") }}
        </li>
        <li
          class="voice-title"
          :class="[type == 2 ? 'selected' : '']"
          @click="checkType(2)"
        >
          {{ $t("textToSpeech") }}
        </li>
      </ul> -->
      <!-- <ul class="title-layout">
        <li
          class="voice-title selected"
        >
          {{ $t("vioceManage") }}
        </li>
      </ul> -->
      <div class="voice-content">
        <div class="voice-content-right" v-loading="loading">
          <div class="head">
            <!-- <ul class="head-tag">
              <li
                :class="[paramSearch.tag == '' ? 'seleted' : '']"
                @click="searchHandler('全部')"
              >
                {{ $t("all") }}
              </li>
              <li
                v-for="(item, index) in categoryList"
                :key="index"
                :class="[paramSearch.tag == item ? 'seleted' : '']"
                @click="searchHandler(item)"
              >
                {{ item }}
              </li>
            </ul> -->
            <!-- <el-tabs v-model="paramSearch.tag" @tab-click="searchHandler(paramSearch.tag)">
                <el-tab-pane :label="$t('all')" :name="$t('all')" :key="'全部'"></el-tab-pane>
                <el-tab-pane v-for="(item,index) in categoryList" :label="item.key" :name="item.value" :key="item.key"></el-tab-pane>
            </el-tabs> -->
            <div>
              <el-select v-model="paramSearch.tag" placeholder="请选择" @change="searchHandler(paramSearch.tag)">
                <el-option v-for="item in categoryList" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
              <el-input :placeholder="$t('inputKeyword')" suffix-icon="el-icon-search" v-model="paramSearch.name"
                style="width: 334px;padding-left: 16px;" @keydown.native.enter="searchHandler('')" clearable>
              </el-input>
            </div>
            <div class="tig">
              <el-button plain style="
                  color: #1747E5;
                  background: #F2F3F5;
                  margin-left: 16px;
                  border-radius: 2px;
                " @click="pronunciationSettings" ref="popoverTrigger">
                <i class="el-icon-circle-plus" style="margin-right: 8px;"></i>{{ $t('commonPronunciationSettings') }}
                <!-- <iconpark-icon  name="equalizer-3-line" size="16" color="#1747E5" style="margin-right:5px;"></iconpark-icon> -->
              </el-button>
              <el-popover popper-class="auto-fill-popover" placement="right-start" width="360" trigger="click"
                style="position: absolute; right: 490px;top: 50px;"  v-model="visibleVoice" ref="myPopover"
                :visible-arrow="false">
                <div class="auto-fill-content">
                  <h1 class="title">{{ $t('commonPronunciation') }}({{ totalVoiceRow }}/{{ currentStep }})</h1>
                  <div class="popover-list" v-for="(item, index) in voiceList" :key="index">
                    <div class="popover-list-left">
                      <img style="width: 24px; height:24px;" src="@/assets/images/voiceOutput.svg" alt="">
                      <h1 class="voicev-text">{{ item.fullStyle }}</h1>
                      <iconpark-icon name="volume-up-line" size="16" color="#494E57"
                        style="cursor: pointer"></iconpark-icon>
                    </div>
                    <!-- <iconpark-icon @click="handelDelete(index)" name="indeterminate-circle-line" size="16" color="#494E57"
                      style="cursor: pointer"></iconpark-icon> -->
                  </div>
                  <div class="list-but">{{ $t('listCommonPronunciation') }}</div>
                  <h1 class="title">{{ $t('voiceIdentify') }}({{ totalRecognitionRow }}/{{ totalCurrentStep }})</h1>
                  <div class="popover-list" v-for="(item, index) in recognitionList" :key="index">
                    <div class="popover-list-left">
                      <img style="width: 24px; height:24px;" src="@/assets/images/voiceIdentify.svg" alt="">
                      <!-- <h1 class="voicev-text">{{ item.tagList.join(', ') }}</h1> -->
                      <h1 class="voicev-text">{{ item.componentName }}</h1>
                    </div>
                    <!-- <iconpark-icon name="indeterminate-circle-line" size="16" color="#494E57"
                      style="cursor: pointer"></iconpark-icon> -->
                  </div>
                  <div class="list-but">{{ $t('listAddvoiceIdentify') }}</div>
                </div>
              </el-popover>
              <el-button plain style="
                  color: #fff;
                  background: #1747E5;
                  margin-left: 16px;
                  border-radius: 2px;
                " @click="createVoice({ type: 'create' })">
                <i class="el-icon-circle-plus" style="margin-right: 8px;"></i>{{ $t('createModel') }}{{ $t('voiceManagement') }}
              </el-button>
            </div>
          </div>
          <ul v-if="list.length" class="list">
            <li class="list-item" v-for="(item, index) in list" :key="index" style="cursor:default;">
              <div class="videologo">{{ item.category }}</div>
              <div class="list-item-top">
                <img v-if="item.category === '语音识别'" class="avatar" src="@/assets/images/voiceIdentifySquare.png" alt="" />
                <img v-else class="avatar" src="@/assets/images/voiceOuputSquare.png" alt="" />
                <div class="text">
                  {{ item.componentName }}
                  <!-- <div class="text-tit">
                    <div class="row-name">{{ item.componentName }}</div>
                    <div class="row-category"><span>{{item.category}}</span></div>
                    <div class="sex" v-if="item.sex">
                      <iconpark-icon name="men-line" v-if="item.sex == '男'" size="12.67" color="#4D77EF"></iconpark-icon>
                      <iconpark-icon name="women-line" v-else size="12.67" color="red"></iconpark-icon>
                    </div>
                  </div> -->
                  <!-- <div class="text-bot">
                    <span class="text-bot-list" v-for="(tag, index) in item.tagList" :key="index">{{ tag }}</span>
                  </div> -->
                  <!-- <div class="text-bot" v-for="(tag, index) in item.tagList" :key="index">
                    <span class="text-bot-list">{{ tag }}</span>
                  </div> -->
                  <!-- <div class="type" v-if="item.type">
                    {{ item.type }}种风格
                  </div> -->
                </div>
              </div>
              <div class="introduce">{{ item.introduce }}</div>
              <div class="list-item-bottom">
                <div class="list-item-bottom-flex" style="margin-bottom: 8px;">
					<div style="display: flex;">
                  <div class="bottom-left" >
                    <!-- <div class="selectedCircle" @click=" checkedIndex = index">
                    <iconpark-icon name="check-line"  size="16" color="#fff" style="background: #1747E5;"
                    :class="[checkedIndex==index?'':'isShow']"></iconpark-icon>
                  </div> -->
                    <div class="txt" :style="getTextStyle(item.frequenceUseFlag)" v-if="item.category === '语音识别'"
                    @click="toggleText(item)"> {{ getFrequenceText(item.frequenceUseFlag) }}</div>
                    <!-- <iconpark-icon name="question-line" size="13.33" style="margin-left:5px;"></iconpark-icon> -->
                  </div>
                  <div  class="tips" @click="handleListening(item)">
                    <div class="tips-left" v-if="item.category === '语音输出'">
                      <i class="el-icon-caret-right" style="width: 9.67px;height: 12.61px;"></i>
                      <i v-if="false" class="el-icon-video-pause" style="width: 9.67px;height: 12.61px;"></i>
                      <div class="text"
                        style="margin-left:8px;font-family: MiSans, MiSans;font-weight: 400;font-size: 14px;color: #36383D;line-height: 20px;text-align: left;font-style: normal;">
                        {{ $t("audition") }}</div>
                    </div>
                  </div>
				  </div>
                  <div class="bottom-right">
                    <!-- <div class="edit" @click="goConfig(item, { type: 'edit' })">
                      <iconpark-icon name="edit-box-line" size="15.75" color="#36383D"
                        style="margin-right:5px;"></iconpark-icon>
                      <span style="font-family: MiSans, MiSans;font-weight: 400;font-size: 14px;color: #36383D;
                      line-height: 20px;text-align: left;font-style: normal;">{{ $t("edit") }}</span>
                    </div> -->
                    <div class="opts-box edit-right" :class="{ 'opts-box-active': activeIndexMoreClick === index }">
                      <el-dropdown trigger="click" @command="(value) => handleCommand(value, item)" placement="top-end"
                        class="opts-box-dropdown" @visible-change="handleVisibleChange($event, index)">
                        <span class="el-dropdown-link">
                          <!-- <i
                            style="transform: rotate(90deg); color: #828894"
                            class="el-icon-more"
                            ></i> -->
                          <iconpark-icon name="more-line" size="18" color="#383838"></iconpark-icon>
                        </span>
                        <!-- 进入编辑页 -->
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
                </div>

              </div>
            </li>
          </ul>
          <div v-if="list.length" class="pagination">
            <div class="total">
              {{ $t("total") }}{{ pageObj.total }}{{ $t("items") }}
            </div>
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
              :current-page.sync="pageObj.pageNo" :page-sizes="[12, 24, 36, 48]" :page-size="pageObj.pageSize"
              background layout="prev, pager, next, sizes" :total="pageObj.total">
            </el-pagination>
          </div>
          <div v-else class="no-data">
            <img src="@/assets/images/no-data.png" alt="" />
            <div class="txt1">{{ $t("notFoundVoice") }}</div>
            <!-- <div class="txt2">{{ $t("clickCreateVoice") }}</div>
            <el-button type="primary" style="width: 112px; font-size: 16px">{{
              $t("create")
            }}</el-button> -->
          </div>
        </div>
      </div>
    </template>
    <!-- 创建语音 -->
    <AddOrEdit v-else :info="detailForm" :isChange="change" :sourceData="voiceForm" @openDialog="openDialog"
      @back="back" @edit="createVoice" />
    <!-- :title="dialogTitle==`${$t('edit')}` ? `${$t('edit')}${$t('vioceManage')}` : `新增${$t('vioceManage')}`" -->
    <el-dialog :visible.sync="dialogVisible" top="3%" width="560px" height="744px" :modal-append-to-body="false"
      append-to-body custom-class="voice-dialog weight1" :before-close="closeDialog" :close-on-click-modal="false"
      :close-on-press-escape="false">
      <div slot="title" style="margin-left:30px!important;margin-bottom: 8px; font-family: MiSans, MiSans;
font-weight: 500;
font-size: 20px;
color: #36383D;
line-height: 32px;
text-align: left;
font-style: normal;
text-transform: none;">{{ modalTitle }}</div>
      <div class="dialog-config">
        <el-form ref="voiceForm" :model="voiceForm" :rules="rules" label-position="top">
          <!-- <div class="form-flex"> -->
          <el-form-item prop="category" :label="$t('type')">
            <el-select v-model="voiceForm.category" placeholder="请选择" style="width: 100%;">
              <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="componentName" :label="$t('nameOne')">
            <el-input v-model="voiceForm.componentName" :placeholder="$t('pleaseInput')" clearable type="text"
              maxlength="100" show-word-limit />
          </el-form-item>
          <!-- <el-form-item label="" style="margin-left: 24px">
              <el-upload
                v-if="!voiceForm.headPortraitUrl"
                :action="actionUrl"
                :data="{ filePath: 'agent_source' }"
                :file-list="avatarList"
                :show-file-list="false"
                :limit="1"
                class="logoAppUpload"
                list-type="picture-card"
                :on-success="handleLogoSuccess"
              >
                <iconpark-icon
                  name="edit-line"
                  color="#fff"
                  size="16"
                ></iconpark-icon>
              </el-upload>
              <div
                v-else
                @mouseenter="showDeleteIcon = true"
                @mouseleave="showDeleteIcon = false"
                class="url-layout"
              >
                <img class="url-img" :src="voiceForm.headPortraitUrl" alt="" />
                <iconpark-icon
                  v-show="showDeleteIcon"
                  @click="deleteImg"
                  class="delete-icon"
                  name="delete-bin-4-line"
                ></iconpark-icon>
              </div>
            </el-form-item> -->
          <!-- </div> -->

          <el-form-item prop="introduce" :label="$t('description')">
            <el-input v-model="voiceForm.introduce" :placeholder="$t('pleaseInputAgain')" clearable type="textarea"
              :rows="4" maxlength="1000" show-word-limit />
          </el-form-item>
          <el-form-item prop="appId" label="APP ID">
            <el-input v-model="voiceForm.appId" :placeholder="$t('pleaseInputAgain')" />
          </el-form-item>
          <el-form-item prop="apiSecret" label="API Secret">
            <el-input v-model="voiceForm.apiSecret" :placeholder="$t('pleaseInputAgain')" />
          </el-form-item>
          <el-form-item prop="apiKey" label="API Key">
            <el-input v-model="voiceForm.apiKey" :placeholder="$t('pleaseInputAgain')" />
          </el-form-item>
        </el-form>
      </div>
      <div class="dialog-footer">
        <el-button plain @click="closeDialog" :loading="addLoading">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="onSubmit" :loading="addLoading">{{ $t('confirm') }}</el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="listeningVisible" height="640px" width="560px" top="3%" :before-close="closeListening">
      <div slot="title"
        style="position:absolute;top:32px;
    left:32px;width: 40px;height: 32px; font-family: MiSans, MiSans;font-weight: 500;font-size: 20px;color: #36383D;line-height: 32px;text-align: left;font-style: normal;text-transform: none;">
        {{ $t("audition") }}
      </div>
      <div class="listening-content">
        <div class="content-header" v-for="(item, index) in listeningList" :key="index">
          <div class="header-left">
            <div class="img">
              <img v-if="listeningList[0]?.category == '语音识别'" class="avatar" src="@/assets/images/voiceIdentify.svg"
                alt="" />
              <img v-else class="avatar" src="@/assets/images/voiceOutput.svg" alt="" />
            </div>
          </div>
          <!-- v-for="(item,index) in listeningList" :key="index" -->
          <div class="header-content">
            <div class="infomation">
              <div class="name">{{ item.componentName }}</div>
              <div class="sex" v-if="item.sex">
                <iconpark-icon name="men-line" v-if="item.sex == '男'" size='18' color="#4D77EF"></iconpark-icon>
                <iconpark-icon name="women-line" v-else size="18" color="red"></iconpark-icon>
              </div>
            </div>
            <div class="introduce">{{ item.introduce }}</div>
            <!-- v-if="tag !=null" -->
            <div class="tag">
              <!-- <div class="tag-detail" v-for="(item2,index) in tag" :key="index">
                {{item2}}
              </div> -->
              <div class="tag-detail" v-for="(tag, index) in item.tagList" :key="index">
                {{ tag }}
              </div>
              <!-- <div class="tag-detail">
                情感
              </div>
              <div class="tag-detail">
                游戏
              </div>
              <div class="tag-detail">
                动漫
              </div> -->
            </div>

          </div>
          <div class="header-right">
            <div class="edit" @click="goEdit(item);">
              <iconpark-icon name="edit-line" size="12" style="margin-right:6px;"></iconpark-icon>
              <span
                style="font-family: MiSans, MiSans;font-weight: 400;font-size: 14px;color: #36383D;line-height: 20px;text-align: left;font-style: normal;">编辑</span>
            </div>
          </div>
        </div>
        <div class="content-type">
          <div class="type-title">
            <div class="left"></div>
            <div class="txt">风格</div>
          </div>
          <div class="type-content-box">
            <div class="type-content" v-for="(item, index) in styleList" :key="index">
              <div class="detail-type">
                <div class="icon">
                  <iconpark-icon name="volume-up-line" size='16' @click="handlePlayVoice"></iconpark-icon>
                </div>
                <div class="type-txt">
                  <div class="detail-txt">{{ item.style }}</div>
                </div>
                <div class="swiper" :style="{ color: item.frequenceUseFlag === 0 ? '#1747E5' : '#86909C' }" 
                @click="handleIsShow(item)"> {{ getStyleText(item.frequenceUseFlag) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" style="height: 88px;padding-bottom:0!important;">
        <el-button @click="closeListening" style=" width: 72px;
    height: 40px;
    margin-top:36px;
    margin-right:12px;">关闭</el-button>
      </div>
    </el-dialog>

    <el-drawer
      :visible.sync="voiceDrawerVisible"
      :size="560"
      :with-header="false"
      custom-class="voice-drawer-ctn">

      <div class="title-ctn">常用语音设置</div>
      <div class="tip-ctn">添加常用后可在应用中选择使用</div>
    </el-drawer>
  </div>
</template>

<script>
import { ttsList, pageQueryVoice, updateVoiceInformation, updateVoiceRecognitionStatus, voiceInfoDetail, textToVoice } from "@/api/app";
import { apiGetTtsTags, apiDeleteVoice, apiAddInfo } from "@/api/toolManager";
import AddOrEdit from "./addOrEdit.vue";

export default {
  components: { AddOrEdit },
  data() {
    return {
      isActive: false,
      isFavorite:false,
      value1: false,
      currentStep:8,
      totalCurrentStep:3,
      visibleVoice: false,
      styleList: [],
      //tag的数量
      tag: [],
      listeningList: [],
      //控制试听弹窗
      listeningVisible: false,
      checkedIndex: 0,
      isChecked: false,
      activeIndexMoreClick: null,
      detailForm: {},
      showDeleteIcon: false,
      //   type: 1,
      pageType: "list",
      modalTitle: '', // 弹窗标题
      pageObj: {
        pageNo: 1,
        pageSize: 12,
        total: 0,
      },
      paramSearch: {
        name: "", // 模型名称
        tag: "全部", // 厂商名称
      },
      voiceList: [],
      recognitionList: [],
      totalVoiceRow: 0,
      totalRecognitionRow: 0,
      list: [],
      categoryList: [{ label: '全部', value: '全部' }, { label: '语音识别', value: '语音识别' }, { label: '语音输出', value: '语音输出' }],
      loading: false,
      title: "",
      dialogVisible: false,
      dialogTitle: '',
      addLoading: false,
      actionUrl: `${process.env.VUE_APP_BASE_API}/wos/file/upload`,
      avatarList: [],
      voiceForm: {
        category: "",
        componentName: "", // 名称
        introduce: "", // 描述
        headPortraitUrl: "", // 头像
        appId: "", // API Id
        apiSecret: "", // API Secret
        apiKey: "", // API Key
      },
      change: false,
      rules: {
        type: [
          {
            required: true,
            message: '请选择类型',
            trigger: 'blur'
          }
        ],
        category: [
          {
            required: true,
            message: "请选择类型",
            trigger: "blur",
          },
        ],
        componentName: [
          {
            required: true,
            message: "请输入名称",
            trigger: "blur",
          },
        ],
        apiSecret: [
          {
            required: true,
            message: "请输入API Secret",
            trigger: "blur",
          },
        ],
        apiKey: [
          {
            required: true,
            message: "请输入API Key",
            trigger: "blur",
          },
        ],
      },
      typeList: [
        {
          label: '语音识别',
          value: '语音识别'
        },
        {
          label: '语音输出',
          value: '语音输出'
        }
      ],
      audioUrl: '',
      voiceDrawerVisible:false
    };
  },
  computed: {
  
  },
  mounted() {
    // this.getTtsTags();
    this.ttsList();
    document.addEventListener('click', this.handleClickOutside);
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside);
  },
  methods: {
    handleClickOutside(event) {
      const popoverElement = this.$refs.myPopover.$el; // el-popover 的 DOM 元素
      const btnElement=this.$refs.popoverTrigger.$el;
      // console.log(event.srcElement,btnElement);
      // console.log(popoverElement.contains(event.srcElement),btnElement.contains(event.srcElement));
      
      if (!popoverElement.contains(event.srcElement) && !btnElement.contains(event.srcElement) ) {
        this.visibleVoice = false;
      }
    },
    handlePlayVoice() {
      textToVoice({
        applicationId: '00759c5a4ae74622971ee51142287e2d',
      }).then((res) => {
        if (res.code === 200) {
          this.audioUrl = res?.data?.fileUrl
          setTimeout(() => {
            this.$refs.audioRef.play()
          }, 500)
        }
      })
    },
    handleIsShow(item) {
      const isFrequent = !item.frequenceUseFlag; 
      const frequenceUseFlagValue = isFrequent ? 1 : 0;
      updateVoiceInformation({
        configId:item.configId,
        frequenceUseFlag:frequenceUseFlagValue
      }).then((res) => {
        if(res.code == "000000") {
          item.frequenceUseFlag = frequenceUseFlagValue;
        }
      })
    },
    goEdit(item) {
      // this.listeningVisible = false
      this.dialogVisible = true
      this.voiceForm = item
      // this.goConfig(item)
    },
    closeListening() {
      this.listeningVisible = false
      this.listeningList = []
    },
    handleListening(item) {
      this.listeningList = []
      this.listeningVisible = true
      this.listeningList.push(item)
      voiceInfoDetail(item.id).then((res) => {
        if(res.code == "000000") {
         this.styleList = res.data.voiceComponentInfoConfigList
        }
      })
      // if (this.listeningList[0].tag != null) {
      //   this.tag = this.listeningList[0].tag.split(',')
      // }
      // updateVoiceInformation({
      //   configId:item.configId,
      //   frequenceUseFlag:item.frequenceUseFlag
      // }).then((res) => {
      //   if(res.code == "000000") {
      //   console.log(res,'sss');
        
      //   }
      // })
    },
    handleVisibleChange(val, index) {
      if (val) {
        this.activeIndexMoreClick = index;
      } else {
        this.activeIndexMoreClick = null;
      }

    },
    operateVal(val) {
      return val ? val.split(",") : [];
    },
    searchHandler(val) {
      if (val == "全部") {
        this.paramSearch.tag = "全部";
      } else {
        this.paramSearch.tag = val;
      }
      this.pageObj.pageNo = 1;
      this.ttsList();
    },
    handleCurrentChange(page) {
      this.pageObj.pageNo = page;
      this.ttsList();
    },
    handleSizeChange(size) {
      this.pageObj.pageNo = size;
      this.ttsList();
    },
    // 查询音频信息表列表
    async ttsList() {
      const params = {
        ...this.paramSearch,
        tag: '',
        category: this.paramSearch.tag == "全部" ? '' : this.paramSearch.tag,
        pageSize: this.pageObj.pageSize,
        pageNo: this.pageObj.pageNo,
      };
      this.loading = true;
      const res = await ttsList(params);
      if (res.code == "000000") {
        console.log("res", res);
        this.list = res.data.records || [];
        this.list.forEach(item => {
          if (item.category == '语音识别') {
            item.sex = '男'
            item.type = 11
          }
        })
        this.pageObj.total = res.data?.totalRow || 0;
      }
      this.loading = false;
    },
    // 查询音频类型
    // async getTtsTags() {
    //   const res = await apiGetTtsTags({});
    //   if (res.code == "000000") {
    //     this.categoryList = res.data || [];
    //   }
    // },
    checkType(type) {
      //   this.type = type;
      this.ttsList();
    },
    createVoice({ type, item }) {
      //   this.title = this.type == 1 ? "speechToText" : "textToSpeech";
      this.modalTitle = type === 'create' ? '新增语音' : ''
      this.voiceForm = {}
      this.dialogVisible = true;
      // if(type=="edit"){
      //   this.dialogTitle = this.$t("edit")
      //   this.voiceForm = item
      // }else {
      //   this.dialogTitle = this.$t("createModel")
      // }
    },
    closeDialog() {
      if (this.$t('edit') != '编辑') {
        this.$refs.voiceForm.resetFields();
        this.voiceForm.category = 0
      }

      this.dialogVisible = false;
    },
    onSubmit() {
      //   console.log("hhhh")
      this.$refs.voiceForm.validate((valid) => {
        if (valid) {
          this.dialogVisible = false;
           let params = { ...this.voiceForm }
                apiAddInfo(params).then(res=> {
                    if(res.code == '000000'){
                        this.$message.success(res.msg)
                        this.ttsList()  
                    }
                })
          // if (this.pageType != "detail") {
          //   this.pageType = "detail";
          //   this.detailForm = this.voiceForm
          // }
          // this.change = !this.change;
        }
      });
    },
    // 新增
    async editLlm() {
      this.addLoading = true;
      const params = {
        applicationId: this.data?.applicationId,
        ...this.voiceForm,
      };
      try {
        const res = await apiEditLlm(params);
        if (res.code == "000000") {
          this.$message.success(`${this.dialogTitle}成功`);
          this.closeDialog();
          this.search();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.addLoading = false;
      }
      this.addLoading = false;
    },
    // 头像上传
    deleteImg(file, fileList) {
      // this.uploadBtnLogo = false;
      this.voiceForm.headPortraitUrl = "";
      this.fileListLogo = [];
    },
    handleLogoSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        // this.uploadBtnLogo = true;
        this.voiceForm.headPortraitUrl =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListLogo = [];
      } else {
        // this.uploadBtnLogo = false;
        this.voiceForm.headPortraitUrl = "";
        this.fileListLogo = [];
      }
    },
    openDialog() {
      this.dialogVisible = true;
    },
    deleteApp(item) {
      this.$confirm("请确认是否删除", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => {
        let params = [item.id + ''];
        apiDeleteVoice({ ids: params }).then(res => {
          if (res.code == '000000') {
            this.$message.success('删除成功')
            this.ttsList()
          }
        })
      });
    },
    // 返回列表
    back() {
      this.pageType = "list";
      for (let key in this.voiceForm) {
        this.voiceForm[key] = "";
      }
    },
    // 进入配置
    goConfig(item, { type }) {
      this.modalTitle = type === 'edit' ? '编辑语音' : ''
      this.dialogVisible = true
      this.voiceForm = item
      // this.pageType = "detail";
      // if(item.category==='语音识别') {
      //     this.type = 1
      // }else {
      //     this.type = 2
      // }
    },
    handleCommand(value, item) {
      //   if (value == "copyApp") this.copyAppInfo(item);
      if (value == "editeApp") this.goConfig(item, { type: 'edit' })
      if (value == "deleteApp") this.deleteApp(item);
      //   if (value == "grantApp") this.grantApp(item);
      //   if (value == "editeModelApp") this.editModelApp(item);
      //   if (value == "detailModelApp") this.detailModelApp(item);
      //   if (value == "deleteModelApp") this.deleteModelApplist(item);
    },
    pronunciationSettings() {
      this.visibleVoice = !this.visibleVoice
      // this.voiceDrawerVisible=true
      this.fetchVoiceOutput();
      this.fetchVoiceRecognition();
    },
    // 获取语音输出数据
    fetchVoiceOutput() {
      pageQueryVoice({
        pageNo: 1,
        pageSize: 8,
        frequenceUseFlag:1,
        tag: '',
        category:'语音输出',
      }).then((res) => {
        if (res.code == "000000") {
          this.voiceList = res.data.records || []
          this.totalVoiceRow = res.data.totalRow
        }
      })
    },
    // 获取语音识别数据
    fetchVoiceRecognition() {
      ttsList({
        pageNo: 1,
        pageSize: 8,
        frequenceUseFlag:1,
        category:'语音识别',
      }).then((res) => {
        if (res.code == "000000") {
          this.recognitionList = res.data.records || []
          this.totalRecognitionRow = res.data.totalRow
        }
      })

    },
    toggleText(item) {
      const isFrequent = !item.frequenceUseFlag; 
      const frequenceUseFlagValue = isFrequent ? 1 : 0;
      updateVoiceRecognitionStatus({
        id:item.id,
        frequenceUseFlag:frequenceUseFlagValue
      }).then((res) => {
        if(res.code == "000000") {
          item.frequenceUseFlag = frequenceUseFlagValue;
        }
      })
    },
    getFrequenceText(frequenceUseFlag) {
      if (frequenceUseFlag === 1) {
        return "已添加常用" || "取消常用";
      } else if (frequenceUseFlag === 0) {
        return "添加到常用" || "添加常用";
      }
      return ""; 
    },
    getStyleText(frequenceUseFlag) {
      if (frequenceUseFlag === 1) {
        return  "取消常用";
      } else if (frequenceUseFlag === 0) {
        return  "添加常用";
      }
      return ""; 
    },
    getTextStyle(frequenceUseFlag) {
      if (frequenceUseFlag === 1) {
        return {
          color: '#1D2129',
          width: '94px',
          height: '32px',
          background: 'rgba(188,193,204,0.2)',
          borderRadius: '4px',
          textAlign: 'center',
          lineHeight: '32px'
        
        };
      } else {
        return {
          color: '#1747E5'
        };
      }
    }
  },
};
</script>
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>
<style lang="scss">
.voice-dialog {
  background: #fff !important;

  .el-dialog__header {
    position: relative;
    display: flex;
    width: 560px;
    height: 72px;
    padding: 0 32px;

    // background: linear-gradient(
    //   180deg,
    //   rgba(43, 88, 213, 0.1) 0%,
    //   rgba(43, 88, 213, 0) 100%
    // );
    //   display: block !important;
    .el-dialog__title {
      margin-top: 32px !important;
      display: inline-block;
      width: 80px;
      height: 32px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #36383D;
      line-height: 32px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }

    .el-dialog__headerbtn {
      position: absolute;
      right: 32px;
      top: 32px;
      display: flex;
      justify-content: center;
      align-items: center;
      // margin-top:32px;
      // margin-right:32px;
      width: 32px;
      height: 32px;

      .el-dialog__headerbtn {
        width: 15px;
        height: 15px;
        background: #36383D;
      }
    }
  }

  .el-dialog__body {
    height: 672px;
    padding: 0 32px 32px !important;
    background: #fff !important;

    .el-form-item {
      width: 496px;
      height: 68px;
      margin-bottom: 20px;
      ;

      &:first-child {
        margin-top: 8px;
        margin-bottom: 24px;

      }

      &:last-child {
        margin-bottom: 8px;
      }

      &:nth-child(3) {
        height: 124px;
      }

      .el-form-item__label {
        margin-bottom: 8px;
        height: 20px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 20px;
        text-align: left;
        font-style: normal;
        text-transform: none;
      }
    }

    .el-input__inner {
      border-radius: 4px;
    }

    .el-input__inner:focus {
      border-color: #1c50fd;
    }

    .el-textarea .el-input__count {
      width: 41px;
      height: 16px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #BCC1CC;
      line-height: 16px;
      text-align: left;
      font-style: normal;
      position: absolute;

      bottom: 9px;
      left: 12px;
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
    height: 40px;
    margin-top: 24px;
    text-align: right;

    .el-button {
      border-radius: 2px;
    }

    .el-button--primary {
      width: 72px;
      background: #1747E5;
      border-color: #1747E5;
      height: 40px;
    }

    .el-button--default {
      margin-right: 16px;
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
      width: 72px;
      height: 40px;
    }
  }
}

.confirm-ok {
  background: #1c50fd !important;
  border-color: transparent !important;
}

.confirm-cancel {
  margin-right: 16px;

  &:hover {
    background: none !important;
    color: #1c50fd !important;
    border-color: #1c50fd !important;
  }
}
</style>
<style lang="scss" scoped>
::v-deep .el-upload--picture-card {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 1);
  opacity: 0.4;
  backdrop-filter: blur(2px);
  display: flex;
  justify-content: center;
}


::v-deep .btn-prev {
  background-color: transparent !important;
  border-radius: 4px !important;
  border: 1px solid #dddfe8;
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

::v-deep .el-tabs__item {
  color: #828894;
  font-size: 24px;
  font-weight: 400;
}

::v-deep .el-tabs__item.is-active {
  color: #603ECA;
  font-weight: 500;
}

::v-deep .el-tabs__active-bar {
  background-color: rgba(0, 0, 0, 0);
}

::v-deep .el-tabs__nav {
  background-color: rgba(0, 0, 0, 0);
  border-bottom: 2px solid #fff;
}

.voice {
  height: 100%;
  //   background: #fff;
  // margin: 0 32px;
  padding: 16px 0;

  .title-layout {
    display: flex;
  }

  .selected {
    color: #383d47;
    font-weight: 500;
  }

  &-title {
    height: 40px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #828894;
    line-height: 40px;
    cursor: pointer;

    &:nth-child(1) {
      margin-right: 16px;
    }
  }

  &-content {
    width: 100%;
    height: calc(100vh - 94px - 32px);
    display: flex;

    // margin-top: 16px;
    &-right {
      height: 100%;
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #ffffff;
      border-radius: 4px;
      padding: 0 0 20px 0;

      //   border: 1px solid #e1e4eb;
      .tig {
        position: relative;

        .auto-fill-popover {
          background: #FFFFFF !important;
          box-shadow: 0px 4px 9px 0px rgba(0, 0, 0, 0.1) !important;
          border: none !important;

          .auto-fill-content {
            padding: 16px;

            .title {
              height: 24px;
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 16px;
              color: #1D2129;
              line-height: 24px;
              text-align: left;
              font-style: normal;
              margin-bottom: 12px;
            }

            .popover-list {
              display: flex;
              justify-content: space-between;
              align-items: center;
              width: 328px;
              height: 40px;
              border-radius: 4px;
              border: 1px solid #E7E7E7;
              padding: 8px;
              margin-bottom: 8px;
              cursor: pointer;

              .popover-list-left {
                display: flex;
              }

              .voicev-text {
                margin: 0 8px;
              }
            }

            .list-but {
              height: 40px;
              background: #F7F8FA;
              border-radius: 4px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 12px;
              color: #C9CDD4;
              line-height: 40px;
              text-align: center;
              font-style: normal;
              margin-bottom: 16px;
            }

            .auto-fill-title {}

            .auto-fill-desc {
              padding-left: 32px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 18px;
              margin-bottom: 16px;
            }

            .auto-fill-footer {
              padding-left: 32px;

              .el-button {
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 16px;
                border-radius: 2px;
              }

              .el-button--primary {
                background: #1747E5;

              }
            }
          }
        }
      }

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
      }

      .list {
        flex: 1;
        margin-top: 24px;
        display: flex;
        flex-wrap: wrap;
        overflow: auto;
        align-content: flex-start;
        cursor: pointer;

        &-item {
          margin-right: 1.3%;
          margin-bottom: 16px;
          width: 24%;
          min-height: 118px;
          max-height: 164px;
          background: #ffffff;
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          display: flex;
          flex-direction: column;
          font-size: 14px;
          position: relative;

          .videologo{
            width: 63px;
            height: 23px;
            background: #F7F8FA;
            border-radius: 0px 7px 0px 8px;
            line-height: 23px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 12px;
            color: #1D2129;
            text-align: center;
            position: absolute;
            top: 0;
            right: 0;
          }

          &:nth-child(4n) {
            margin-right: 0;
          }

          &-top {
            display: flex;
            align-items: center;
            margin: 16px;
            height: 32px;

            .text {
              // display: flex;
              height: 32px;
              // align-items: center;
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 16px;
              color: #1D2129;
              line-height: 32px;

              // .row-name {
              //   font-family: MiSans, MiSans;
              //   font-weight: 500;
              //   font-size: 16px;
              //   color: #36383D;
              //   line-height: 24px;
              //   text-align: left;
              //   font-style: normal;
              // }
              // .text-tit {
              //   display: flex;
              //   align-items: center;
              // }
              // .text-bot {
              //   margin-top: 4px;
              //   .text-bot-list {
              //     padding: 4px;
              //     // width: 32px;
              //     // height: 20px;
              //     background: #EBEEF2;
              //     border-radius: 2px;
              //     font-size: 12px;
              //     margin-right: 8px;
              //   }
              // }
              // .type {
              //   height: 16px;
              //   font-family: MiSans, MiSans;
              //   font-weight: 400;
              //   font-size: 12px;
              //   color: #36383D;
              //   line-height: 16px;
              //   text-align: left;
              //   font-style: normal;
              //   text-transform: none;
              //   margin-left: 12px;
              //   background-color: #F7F8FA;
              // }

              // .sex {
              //   width: 13px;
              //   height: 13px;
              //   display: flex;
              //   justify-content: center;
              //   align-items: center;
              //   margin-left: 6px;
              // }
            }

            .avatar {
              margin-right: 12px;
              width: 32px;
              height: 32px;
              // border: 1px solid #e1e4eb;
              border-radius: 4px;
            }
          }

          &-bottom {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-family: MiSans, MiSans;
            font-size: 14px;
            color: #768094;
            height: 32px !important;
            // border-top: 1px solid rgba(0, 0, 0, 0.12);
            margin: 0 16px;

            .bottom-left {
              display: flex;
              align-items: center;
              height: 32px;

              .selectedCircle {
                width: 16px;
                height: 16px;
                border: 1px solid #B4BCCC;
                border-radius: 50%;
                overflow: hidden;
                display: flex;
                justify-content: center;
                align-items: center;

                &:hover {
                  cursor: pointer;
                }
              }

              .txt {
                // width: 56px;
                height: 20px;
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 14px;
                color: #1747E5;
                text-align: left;
                font-style: normal;
                text-transform: none;
                line-height: 20px;
                // margin-left: 8px;
                cursor: pointer;
              }

              // .txt.active {
              //   // background-color: #BCC1CC;
              //   color: #1747E5;
              //   #86909C
              //   // width: 108px;
              //   // height: 32px;
              //   // background: rgba(188,193,204,0.2);
              //   // border-radius: 4px;
              // }
            }

            &-flex {
              font-size: 12px;
              color: #3FB816;
              display: flex;
              justify-content: space-between;
              width: 100%;

              .bottom-right {
                // width: 108px;
                height: 32px;
                display: flex;
                justify-content: center;
                align-items: center;

                .edit {
                  display: flex;
                  padding: 0 7px 0 11px;
                  align-items: center;
                  width: 68px;
                  height: 32px;
                  margin-right: 8px;

                  &:hover {
                    cursor: pointer !important;
                    background: rgba(180, 188, 204, 0.2) !important;
                  }
                }

                .edit-right {
                  width: 32px;
                  height: 32px;
                  color: #36383D;
                }
              }

              span {
                display: inline-block;
                line-height: 30px;
              }

              strong {
                font-size: 20px;
                font-weight: 800;
              }
            }

            .tips {
              display: flex;
              width: 68px;
              height: 32px;

              .tips-left {
                margin: 8px 8px 6px 0px;
                width: 47px !important;
                height: 20px !important;
                display: flex;
                justify-content: space-between;
                align-items: center;

                &:hover {
                  cursor: pointer;
                  background: rgba(180, 188, 204, 0.2) !important;
                }

                .type {
                  height: 20px;
                  background: #F7F8FA;
                  border-radius: 2px;
                }
              }

            }
          }

          .isShow {
            display: none;
          }

          .el-icon-caret-right {
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 20px;
            color: #36383D;
          }

          .el-icon-video-pause {
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 20px;
            color: #36383D;
          }

          .noBorder {
            border-top: none;
          }

          .flex-start {
            align-items: flex-start;
          }

          .text {
            flex: 1;
          }

          .row {
            display: flex;
            align-items: center;
            margin-bottom: 4px;

            &-name {
              margin-right: 6px;
              height: 24px;
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 16px;
              color: #36383D;
              line-height: 24px;
              text-align: left;
              font-style: normal;
            }

            &-category {
              width: 56px;
              height: 20px;
              background: #F2F4F7;
              border-radius: 2px;
              padding: 4px;
              color: #494E57;
              font-size: 12px;
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

          .el-icon-more {}

          .introduce {
            margin-left: 16px;
            height: 44px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #828894;
            line-height: 18px;
            margin: 0 16px;
          }
        }
      }
    }
  }

  ::v-deep .voice-drawer-ctn{
    width: 560px;
    height: 100vh;
    padding:32px ;
    box-sizing: border-box;
    border-radius: 12px 0 0 12px;

    .title-ctn{
      width: 100%;
      height: 32px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #1D2129;
      line-height: 32px;
    }

    .tip-ctn{
      height: 20px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #86909C;
      line-height: 20px;
      margin: 16px 0;
    }
  }
}

.dialog-config {
  margin-left: 30px;

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

.dialog-footer {
  margin-right: 30px;
}
</style>
<style lang="scss" scoped>
::v-deep .el-dialog__body {
  padding: 0 !important;
}

::v-deep .el-dialog__header {
  position: relative;
  display: flex;
  justify-content: space-between;
  padding: 0 !important;
  margin: 0 !important;
  width: 560px;
  height: 72px !important;
  display: flex;
  align-items: flex-end;

}

::v-deep .el-dialog__headerbtn {
  width: 32px;
  height: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 32px;
  right: 32px;
}

.listening-content {
  height: 472px;
  margin-left: 32px;
  margin-top: 8px;

  .content-header {
    display: flex;
    width: 496px !important;
    height: 100px;
    background-color: #F7F8FA;
    border: 1px solid #D5D8DE;

    .header-left {
      width: 80px;
      height: 100px;

      .img {
        margin: 16px 0 36px 16px;
        width: 48px;
        height: 48px;
        border-radius: 50%;
        overflow: hidden;
        border: 1px solid #D5D8DE;
        background-color: #F7F8FA;

        img {
          width: 100%;
          height: 100%;
        }
      }
    }

    .header-content {
      flex: 1;

      .infomation {
        margin-top: 16px;
        display: flex;
        align-items: center;

        .name {
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 14px;
          color: #36383D;
          line-height: 20px;
          text-align: left;
          font-style: normal;
        }

        .sex {
          margin-left: 4px;
          display: flex;
          justify-content: center;
          align-items: center;
          width: 16px;
          height: 16px;
        }
      }

      .introduce {
        margin-top: 4px;
        height: 16px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #828894;
        line-height: 16px;
        text-align: left;
        font-style: normal;
      }

      .tag {
        margin-top: 8px;
        height: 20px;
        display: flex;
        // justify-content: space-between;
        align-items: center;

        .tag-detail {
          display: flex;
          justify-content: center;
          align-items: center;
          margin-right: 8px;
          width: 32px;
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          color: #36383D;
          line-height: 16px;
          text-align: left;
          font-style: normal;
          text-transform: none;
          background-color: #EBEEF2;
        }
      }
    }

    .header-right {
      width: 88px;
      height: 100px;
      display: flex;
      justify-content: center;
      align-items: center;

      .edit {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 56px;
        height: 24px;
        margin-top: 16px;
        // margin-right:16px;
        margin-bottom: 60px;

        &:hover {
          cursor: pointer;
          background: rgba(180, 188, 204, 0.2) !important;
        }
      }
    }
  }

  .content-type {
    margin-top: 24px;
    width: 496px;

    .type-title {
      width: 47px;
      height: 32px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;

      .left {
        margin-top: 7px;
        width: 4px;
        height: 18px;
        background: #1747E5;
        border-radius: 0px 2px 2px 0px;
      }

      .txt {
        height: 32px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #36383D;
        line-height: 32px;
        text-align: left;
        font-style: normal;
      }

    }
    .type-content-box {
      display: flex;
      width: 496px;
      justify-content: space-between;
      flex-wrap: wrap;
      .type-content {
      
       
      .detail-type {
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: 244px;
        height: 40px;
        margin-bottom: 8px;
        background: #FFFFFF;
        border-radius: 2px;
        border: 1px solid #D5D8DE;

        .icon {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 24px;
          height: 24px;
          border-radius: 2px;
          margin-left: 8px;
        }

        .type-txt {
          flex: 1;

          .detail-txt {
            height: 20px;
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #36383D;
            line-height: 20px;
            text-align: left;
            font-style: normal;
          }
        }

        .swiper {
          // width: 28px;
          height: 20px;
          line-height: 20px;
          margin-right: 8px;
          cursor: pointer;
          // border-radius: 8px;
          // overflow: hidden;
        }
      }
    }
    }
  }
}

.footer {
  width: 560px;
  height: 88px;

}
</style>