<template>
  <div class="preview-container" v-loading="previewLoading">
    <!-- <iframe
      ref="iframe"
      style="
        width: 100%;
        height: 100%;
      "
      id="myiframe"
    ></iframe> -->
    <div style="overflow:scroll;height:calc(100% - 98px);" class="container">
      <div class="message-list">
        <!-- 你可以问我 -->
        <div class="quest-list" v-if="qaAllData.presetQuestionList?.length > 0">
          <div class="quest-list-title">您可以这么问我</div>
          <div class="quest-list-item" v-for="(item, index) in qaAllData.presetQuestionList" :key="index">
            <div class="item" @click="sendQuestion(item)">
              <span
                style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;font-family: MiSans, MiSans;font-weight: 400;font-size: 14px;color: #3F4247;line-height: 20px;text-align: left;font-style: normal;">{{ item }}</span>
              <iconpark-icon name="navigation-fill" size="16" color="#1477E3"></iconpark-icon>
            </div>
          </div>
        </div>
        <!-- 打招呼 -->
        <div style="margin-top:32px;display:flex;" v-if="qaAllData.prologue" class="introduce">
          <!-- qaAllData.robotIcon -->
          <div class="avatar" v-if="qaAllData.robotIcon">
            <img :src="qaAllData.robotIcon" alt="">
          </div>
          <div class="prologue">
            {{ qaAllData.prologue || '您好，很高兴为您服务！请问有什么可以帮您的？' }}
          </div>
        </div>
        <!-- 消息内容 -->
        <div v-for="(item, index) of chatList" :key="index">
          <yyMessage :qaAllData="qaAllData" :inversion="true" :param="item.param" :answer="item.answer"
            :dialogueId="item.dialogueId" :businessScenarioLists="item.businessScenarioLists"
            :businessScenario="item.businessScenario" :citations="item.citations || []" :createTime="item.createTime"
            :id="item.id + ''" :isAiQuestion="item.isAiQuestion" :dialogueFileIds="item.dialogueFileIds || []"
            :dialogueFolderIds="item.dialogueFolderIds || []" :paragraph="item.paragraph || ''"
            :skillId="item.skillId || ''" :question="item.question" :loading="item.loading" :isLast="false"
            :key="index + 'r'" :feedBackStatus="item.feedBackStatus" :imgUrl="item.imgUrl" :sensitive="item.sensitive"
            :stopChatLoading="stopChatLoading" :matterGuide="item?.matterGuide" :finishReason="item.finishReason"
            :clientId="item.clientId" :index="index" align="right" :item="item" :closeTalk="closeTalk" />
          <yyMessage :qaAllData="qaAllData" :answer="item.answer" :dialogueId="item.dialogueId"
            :businessScenarioLists="item.businessScenarioLists" :businessScenario="item.businessScenario"
            :citations="item.citations || []" :createTime="item.createTime" :id="item.id + ''"
            :dialogueFileIds="item.dialogueFileIds || []" :dialogueFolderIds="item.dialogueFolderIds || []"
            :paragraph="item.paragraph || ''" :skillId="item.skillId || ''" :param="item.param"
            :isAiQuestion="item.isAiQuestion" :question="item.question" :loading="item.loading"
            :isLast="index == chatList.length - 1" :key="index + 'l'" :feedBackStatus="item.feedBackStatus"
            :imgUrl="item.imgUrl" :sensitive="item.sensitive" :stopChatLoading="stopChatLoading"
            :suggestOrg="item.suggestOrg" :contentQaType="item.contentQaType" :answerFlag="item.answerFlag"
            :plainText="item.plainText" :matterGuide="item?.matterGuide" :finishReason="item.finishReason"
            :clientId="item.clientId" :item="item" :closeTalk="closeTalk" align="left" />
        </div>
      </div>
    </div>
    <div class="chatModule"
      :style="workflowOrdialogues ? 'position:absolute;left:50%;transform:translateX(-50%);bottom:25px;' : 'position:absolute;left: 50%;transform: translateX(-50%);bottom:15px;'">
      <div class="basicTemplate-chatInput chatInput-pc ">
        <div class="textarea">
          <div class="fileList" v-if="fileList?.length > 0">
            <div class="file-detail" v-for="(item, index) in fileList" :key="item.id" @mouseenter="deleteIndex = index"
              @mouseleave="deleteIndex = -1">
              <div class="left">
                <div class="img">
                  <svg t="1744274231278" class="icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="4942" width="40" height="40"
                    v-if="item.fileType == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || item.fileType == 'application/vnd.ms-excel'">
                    <path
                      d="M658.102857 139.995429a17.408 17.408 0 0 1 0.182857 2.486857v739.035428a18.029714 18.029714 0 0 1-20.845714 17.554286L177.664 835.364571a35.84 35.84 0 0 1-31.414857-35.108571V223.707429a35.84 35.84 0 0 1 31.414857-35.108572l459.702857-63.634286a18.212571 18.212571 0 0 1 20.699429 14.994286zM343.771429 365.714286H256l102.4 146.285714L256 658.285714h87.771429l58.514285-83.602285L460.8 658.285714H548.571429l-102.4-146.285714 102.4-146.285714h-87.771429L402.285714 449.316571 343.771429 365.714286z"
                      fill="#18AB66" p-id="4943"></path>
                    <path
                      d="M658.285714 192.950857h182.857143c20.187429 0 36.571429 15.872 36.571429 35.474286v567.149714c0 19.602286-16.384 35.474286-36.571429 35.474286h-182.857143V192.950857z"
                      fill="#8CD5B3" p-id="4944"></path>
                  </svg>
                  <svg t="1744274189696" class="icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="4799" width="40" height="40"
                    v-if="item.fileType == 'text/plain'">
                    <path
                      d="M658.285714 146.285714v146.285715a36.571429 36.571429 0 0 0 36.571429 36.571428l146.249143-0.036571 0.036571 512.365714a36.315429 36.315429 0 0 1-36.315428 36.242286H219.172571A36.571429 36.571429 0 0 1 182.857143 841.435429V182.564571C182.857143 162.523429 199.131429 146.285714 219.172571 146.285714H658.285714z m0 475.428572h-292.571428v73.142857h292.571428v-73.142857z m0-146.285715h-292.571428v73.142858h292.571428v-73.142858z m-182.857143-146.285714h-109.714285v73.142857h109.714285V329.142857z"
                      fill="#7E56EB" p-id="4800"></path>
                    <path
                      d="M658.285714 146.285714l182.857143 182.857143h-146.285714a36.571429 36.571429 0 0 1-36.571429-36.571428V146.285714z"
                      fill="#CBBBF7" p-id="4801"></path>
                  </svg>
                  <svg t="1744273886599" class="icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="4513" width="40" height="40"
                    v-if="item.fileType == 'application/msword' || item.fileType == 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'">
                    <path
                      d="M177.700571 188.598857l459.702858-63.634286a18.212571 18.212571 0 0 1 20.882285 17.554286v738.998857a18.029714 18.029714 0 0 1-20.845714 17.554286L177.664 835.364571a35.84 35.84 0 0 1-31.414857-35.108571V223.707429a35.84 35.84 0 0 1 31.414857-35.108572zM475.428571 370.212571v176.859429l-73.142857-70.509714-72.777143 70.875428L329.142857 370.212571H256v283.574858h73.142857l73.142857-70.875429 73.142857 70.875429h73.142858v-283.574858h-73.142858z"
                      fill="#2862FF" p-id="4514"></path>
                    <path
                      d="M658.285714 192.950857h182.857143c20.187429 0 36.571429 15.872 36.571429 35.474286v567.149714c0 19.602286-16.384 35.474286-36.571429 35.474286h-182.857143V192.950857z"
                      fill="#93B0FF" p-id="4515"></path>
                  </svg>
                  <svg t="1744274063569" class="icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="4656" width="40" height="40"
                    v-if="item.fileType == 'application/pdf'">
                    <path
                      d="M655.433143 146.285714l0.365714 0.402286L655.835429 292.571429a36.571429 36.571429 0 0 0 32.329142 36.315428l4.242286 0.256 142.189714-0.036571 0.109715 511.744c0 20.370286-15.981714 36.864-35.620572 36.864H224.914286a36.205714 36.205714 0 0 1-35.620572-36.278857V182.564571c0-20.041143 16.091429-36.278857 35.84-36.278857h430.299429zM529.92 347.428571h-71.68c0 57.673143-16.347429 125.696-43.958857 188.452572-27.648 63.012571-65.024 116.918857-103.936 148.699428l42.276571 58.989715c104.96-71.387429 221.184-120.32 333.568-103.936l16.420572-70.912c-95.817143-32.548571-172.690286-130.230857-172.690286-221.293715z m-24.941714 151.003429a351.378286 351.378286 0 0 0 61.184 71.899429c-35.218286 6.436571-69.705143 17.005714-103.094857 30.464a644.169143 644.169143 0 0 0 41.874285-102.4z"
                      fill="#F50458" p-id="4657"></path>
                    <path
                      d="M655.835429 146.285714l178.834285 182.857143h-142.262857a36.571429 36.571429 0 0 1-36.571428-36.571428V146.285714z"
                      fill="#FB9BBC" p-id="4658"></path>
                  </svg>
                  <svg t="1745819200537" class="icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="4958" width="40" height="40"
                    v-if="item.fileType == 'image/jpeg' || item.fileType == 'image/png'">
                    <path
                      d="M234.678857 476.379429l79.213714-79.250286 217.929143 217.892571 138.642286-138.642285 118.857143 118.857142v-360.594285H234.678857v241.737143zM195.035429 155.428571h633.929142c21.869714 0 39.606857 17.737143 39.606858 39.606858v633.929142c0 21.869714-17.737143 39.606857-39.606858 39.606858H195.035429a39.606857 39.606857 0 0 1-39.606858-39.606858V195.035429c0-21.869714 17.737143-39.606857 39.606858-39.606858z"
                      fill="#29BBB6" p-id="4959">
                    </path>
                    <path
                      d="M650.678857 432.749714a59.428571 59.428571 0 1 1 0-118.857143 59.428571 59.428571 0 0 1 0 118.857143z"
                      fill="#94DDDA" p-id="4960"></path>
                  </svg>
                </div>
              </div>
              <div class="text">
                <div class="text-header">
                  {{ item.fileName }}
                </div>
                <div class="text-kb">
                  {{ changeKb(item.fileSize) }}
                </div>
              </div>
              <div class="delete" @click="deleteFile(index)" v-show="deleteIndex == index">
                <iconpark-icon name="close-circle-fill" size="18" color="#1D2129"></iconpark-icon>
              </div>
            </div>

          </div>
          <!-- :style="{height:changeTextarea()<2?'48px':''}" -->
          <el-input ref="inputTextarea" :class="fileList?.length > 0 ? 'noTopBorder' : 'border-radius:8px'"
            class="textareaInput" type="textarea" :placeholder="qaAllData.inputPlaceholder"
            :autosize="{ minRows: 1, maxRows: 3 }" :maxlength="3000" v-model="text" resize="none"
            @keydown.native="handleKeyDown($event)" @input="changeRow"></el-input>
          <el-button type="primary" v-show="!$store.state.workflow.dialogueInputLoading" @click.native="setinput"
            class="sendBtn" :disabled="(fileList.length == 0 && text == '') || isJieXi" style="width: 24px!important;padding:0!important;
         height: 24px!important;">
            <img src="@/assets/send.svg" alt="" v-if="(fileList.length != 0 || text != '')">
            <img src="@/assets/not-send.svg" alt="" v-else>
          </el-button>
          <el-upload style="position: absolute;right:48px;bottom:11px;z-index:4;" ref="upload" action="#"
            :multiple="true"
            :limit="10" :on-exceed="handleExceed" :on-success="handleUploadSuccess" :on-change="handleChange"
            :file-list="fileListOrgin" :show-file-list='false' :http-request="uploadHandler"
            :before-upload="beforeUpload"
            :accept="type != 7 && qaAllData.type != 'workflow' && type != 5 ? 'pdf,word,txt,ofd,jpg,jpeg,png' : 'pdf,word,txt,xlsx,xls'"
            :disabled="isJieXi">
            <el-tooltip style="width: 100%;height: 100%;z-index:3;" effect="dark" placement="top">
              <template #content>
                <div class="uploadtxt">最多上传10个文档，单个最大15MB</div>
                <div class="uploadtxt">{{ type != 7 && qaAllData.type != 'workflow' && type
                  != 5 ? '支持.doc、.docx、.pdf、.txt、.jpg、.png、.ofd格' :'支持.doc、.docx、.xls、.xlsx、.pdf、.txt格' }}</div>
                <div class="uploadtxt">式文件</div>
              </template>
              <div style="width: 32px;height: 32px;display: flex; justify-content: center; align-items: center;border-radius: 8px;
	z-index:3;" class="el-upload-div">
                <div style="width: 16.67px;height: 16.67px;border-radius: 50%;display: flex;
	justify-content: center;align-items: center;;
	
	 ">
                  <iconpark-icon name="add-circle-line" color="#3F4247" size="20"></iconpark-icon>
                </div>
              </div>
            </el-tooltip>
          </el-upload>
          <el-tooltip content="停止生成" placement="top" v-show="$store.state.workflow.dialogueInputLoading">
            <div style="width: 20px;height: 20px;border-radius: 50%;border:2px solid black;display:flex;
			justify-content: center;align-items: center;bottom:14px;right:14px;" class="loadingAnimate" @click="closeTalk">
              <!-- @click="handleclickStopChat()" -->
              <iconpark-icon name="stop-fill" size="16" color="black" style="border-radius: 4px;" :height="15"
                :width="15"></iconpark-icon>
              <!-- :animationData="toJSON" -->
            </div>
          </el-tooltip>
          <div class="refresh" @click="refreshTalk">
            <iconpark-icon name="brush-3-fill"></iconpark-icon>
          </div>
        </div>

      </div>
      <div class="chatModule-tips">内容由AI生成，无法确保真实准确，仅供参考</div>
      <!-- <getAudio></getAudio> -->
    </div>
  </div>
</template>
<script>
import axios from 'axios'
import { addConversation } from '@/api/index'
import { closeDialogueConn, apiGetText } from '@/api/workflow'
import yyMessage from '../../../components/yy-message.vue';
import {
  addApplication,
} from "@/api/app";
import { mapMutations } from "vuex";
import { Message } from 'element-ui';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import md5 from 'js-md5';
import { template } from '@antv/x6/lib/util/string/string';
import { useRoute } from 'vue-router/composables';
export default {
  name: 'preview',
  components: {
    yyMessage,
  },
  props: {
    params: {
      type: Object,
      default: () => {
        return {}
      }
    },
    type: {
      type: String | Number,
    },
  },
  data() {
    return {
      stopChatLoading: false,
      dataStore: {},
      qaAllData: [],
      //对话框输入文本
      text: '',
      //用于保存对话和工作流要解析的文件
      realFileList: [],
      fileUrlList: [],
      //展示的文件列表
      fileList: [],
      //   {
      //     id:1,
      //     fileName:'123',
      //     fileSize:'100KB'
      //   },
      // {id:2,
      //   fileName:'456',
      //   fileSize:'200KB'
      // },{id:3,
      //   fileName:'456',
      //   fileSize:'200KB'
      // }
      //控制删除按钮是否出现
      deleteIndex: -1,
      //真正的文件列表
      fileListOrgin: [],
      policyList: {},
      //展示对话的列表
      chatList: [],
      //答案
      previousAnswer: '',
      refList: [],
      //溯源相关的
      previewLoading: false,
      scrollbarBottom: true,
      answerStr: '',
      //控制删除
      abortController: null,
      //conversationId
      conversationId: null,
      plainText: '',
      errorText: '服务器开小差了，请稍后再试',
      timer: null,
      tjQuestListRecommend: [],
      clientId: '',
      chatListLength: 0,
      // textarea输入框组件
      textarea: null,
      outTextarea: null,
      //解析完的text
      fileAttachmentList: [],
      //控制解析时不能再上传
      isJieXi: false,
      totalFiles: 0,
      successCount: 0,
      nodeInfo: [],
      fileuploadList: [],
	  nodeIdList:[],
	  outputJsonData:{}
    }
  },
  mounted() {
    this.nodeInfo = JSON.parse(sessionStorage.getItem('nodeInfo'))
    this.textarea = document.querySelector('.textareaInput')
    this.outTextarea = document.querySelector('.textareaInput .el-textarea__inner')
    this.outTextarea.classList.add('height1')
    this.textarea.classList.add('height2')

    this.addNewConversation()
    this.qaAllData = JSON.parse(JSON.stringify(this.params))

    this.qaAllData.presetQuestionList.forEach((item, index) => {
      if (item == "") {
        this.qaAllData.presetQuestionList.splice(index, 1)
      }
    })

  },
  beforeDestroy() {
    this.closeTalk()
  },
  created() { },
  methods: {
    changeRow() {

      if (!this.textarea) {
        return
      } else {
        const lineHeight = parseInt(window.getComputedStyle(this.outTextarea).lineHeight)

        if (Math.floor(this.outTextarea?.scrollHeight / lineHeight) <= 2 && this.outTextarea?.scrollHeight == 62) {
          this.outTextarea.classList.remove('height1')
          this.textarea.classList.remove('height2')
        } if (Math.floor(this.outTextarea?.scrollHeight / lineHeight) <= 2 && this.outTextarea?.scrollHeight == 41) {
          this.outTextarea.classList.add('height1')
          this.textarea.classList.add('height2')
        }
        return (Math.floor(this.textarea?.scrollHeight / lineHeight))
      }
    },
    // 刷新对话
    async refreshTalk() {
      if (this.abortController) {
        this.abortController?.abort();
      }
      if (this.chatList.length > 0) {
        await this.closeTalk()
        this.temporarSave()
      } else {
        Message.info({
          message: '已是最新对话'
        })
      }
    },
    addNewConversation() {
      addConversation({ name: '新建对话' }).then((res) => {
        this.previewLoading = false
        this.conversationId = res.data.conversationId
      })
    },
    //文件相关的
    //删除对应文件
    deleteFile(index) {
      this.fileList.splice(index, 1)
      this.fileListOrgin.splice(index, 1)
      this.realFileList.splice(index, 1)
    },
    //文件的上传
    async uploadHandler(params) {
      const formData = new FormData();
      formData.append('file', params.file);
      formData.append('rename', true);
      formData.append('filePath', 'agent_source');
      try {
        Message.info({
          message: '正在上传中，请稍等'
        })
        const res = await axios.post(`${process.env.VUE_APP_BASE_API}/wos/file/upload`, formData, {
          headers: {
            'Content-Type': 'audio/wave;multipart/form-data',
          },
        });
        if (res.status == 200) {
          this.realFileList.push(res.data.data[0])
          this.fileUrlList.push(res.data.data[0].url)
          Message.success({
            message: '上传成功'
          })
        } else {
          Message.warning({
            message: '上传失败'
          })
        }
      } catch (err) {
      } finally {
      }
    },
    handleChange(file, fileList) {
      this.totalFiles = fileList.length;
    },
    handleUploadSuccess(params){
      this.successCount++;
       // 检查是否所有文件都上传完成
       if (this.successCount === this.totalFiles) {
        this.successCount = 0
        this.getText();
       }
    },
    async getText(){
      this.isJieXi = true
      if (this.qaAllData.type != 'workflow' && this.type != 5 && this.type != 7) {
          Message.info({
            message: '上传成功，正在解析文件中，请稍等'
          })
          let result = await apiGetText({ fileUrlList: this.fileUrlList })
          if (result.code == '000000') {
            Message.success({
              message: '解析完成'
            })
            const lastText = []
            result.data.forEach(item => {
              lastText.push({
                text: item.text
              })
            })
            this.fileAttachmentList = lastText
            this.isJieXi = false
          } else {
            Message.warning({
              message: '解析失败！'
            })
            return
          }
        } else {
          this.isJieXi = false
        }
    },
    //超出限制的回调
    handleExceed(file, uploadFile) {
      Message.warning({
        message: '文件数量超出限制，请删除一些后再添加！'
      })

    },
    //文件上传前的回调
    beforeUpload(file, fileList) {
      if (this.qaAllData.type != ' workflow' && this.type != 5 && this.type != 7) {
        if (file.type != 'application/msword' &&
          file.type != 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' &&
          file.type != 'application/pdf' && file.type != 'text/plain' && file.type != 'application/ofd'
          && file.type != 'image/jpeg' && file.type != 'image/png') {
          Message.warning({
            message: '文件格式错误',
          })
          return false;
        } else {
          const temp = {
            fileName: file.name,
            fileSize: file.size,
            fileType: file.type,
            uid: file.uid,

          }
          this.fileList.push(temp);
          // this.fileList = [...this.fileList]

        }
      } else {
        const fileExt = file?.name?.slice(file.name.lastIndexOf('.'))?.toLowerCase();
        if (
          file.type != 'application/msword' &&
          file.type != 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' &&
          file.type != 'application/pdf'
          && file.type != 'text/plain' && file.type != 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
          && file.type != 'application/vnd.ms-excel' && file.type != 'application/vnd.ms-excel.sheet.macroEnabled.12'
        ) {
          Message.warning({
            message: '文件格式错误',
          })
          return false;
        } else {
          const temp = {
            fileName: file.name,
            fileSize: file.size,
            fileType: file.type,
            uid: file.uid,
          }
          this.fileList.push(temp);
          this.fileList = [...this.fileList]

        }

        // 修正 Chrome 94 的 MIME 类型识别错误
        if (!file.type && !['.docx', '.doc'].includes(fileExt)) {
          Message.warning({
            message: '文件格式错误',
            center: true,
          })
          return false;
        }
      }
    },
    // kb之间的转换
    changeKb(b) {
      if (b < 1024) {
        b = b + 'B'
        return b
      } else if (1024 < b < 1024 * 1024) {
        b = (b / 1024).toFixed(1) + 'KB'
        return b
      } else if (b > 1024 * 1024) {
        b = (b / (1024 * 1024)).toFixed(1) + 'MB'
        return b
      }
    },
    //发送相关的
    handleKeyDown(e) {
      if (this.fileList.length > 0 || this.text != '') {
        if (this.$store.state.workflow.dialogueInputLoading ) {
          return
        } else {

          if (e.key == 'Enter') {
            e.stopPropagation();
            e.preventDefault();
            this.setinput()
            // this.$store.state.workflow.dialogueInputLoading = false
            this.$store.state.workflow.dialogueInputLoading = true;
            this.outTextarea.classList.add('height1')
            this.textarea.classList.add('height2')
          }
        }
      }


    },
    sendQuestion(item) {
      this.$store.state.workflow.dialogueInputLoading = true
      if (this.$store.state.workflow.dialogueLoading) return
      const data = {
        content: item,
      }
      this.setList(data)
      const message = document.querySelector('.message-list')

      message.scrollTop = message.scrollHeight
    },
    async useEventSource(url, params = {}, abortController, onmessage, onclose, onerror, headerParams = {}) {
      let timer = new Date().getTime();
      // 使用给定的 URL 和选项创建一个 EventSource 对
      let now = new Date();
      let hasMessage = false
      let clientType = 'PC';
      const source = await fetchEventSource(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          timestamp: timer,
          cipher: md5(timer + JSON.stringify(params) + '/dialogue/v3/dialogueByStreamxxxxxxxxxxx'),
          // Authorization: sessionStorage.getItem('wxAccessToken') ? `Bearer ${sessionStorage.getItem('wxAccessToken')}` : '', 
          Authorization: sessionStorage.getItem('manageAccessToken') ? `Bearer ${sessionStorage.getItem('manageAccessToken')}` : '',// 使用 Session 存储中的 token 进行授权
          'login-plat': `${sessionStorage.getItem('manageAccessToken')}`, // 使用 Session 存储中的 token 进行授权
          'clientType': clientType,
          ...headerParams
        },
        body: JSON.stringify(params),
        // 使用 abort signal 在需要时取消请求
        signal: abortController.signal,
        // 即使页面隐藏也打开连接
        openWhenHidden: true,
        onopen(event) {

        },
        onmessage(event) {
          const data = JSON.parse(event.data);
          hasMessage = true

          // 消息回调
          onmessage?.(event.data);
        },
        onclose() {
          const oncloseDate = new Date();
          if (oncloseDate.getTime() - now.getTime() < 1000 && !hasMessage) {
            Message.warning({
              message: '系统繁忙，请稍等',
              duration: 2000,
            });
          }
          // 结束回调
          onclose?.();
          // 当发生错误时，中止请求
          abortController.abort();
        },
        onerror(e) {

          // 失败回调
          onerror?.();
          // 当发生错误时，中止请求
          abortController.abort();
        },
      })
      return {
        close: () => {
          abortController.abort();
          source?.close();
        },
      };
    },
    async setList(data) {
      if (this.qaAllData.networkFlag == true) {
        data.networkFlag = '是'
      } else {
        data.networkFlag = '否'
      }
      const appType = this.qaAllData.type
      const workflowValue = appType == 'workflow' || this.type == 5 || this.type == 7
      data.clientId = Math.round(Math.random() * 1000000000000000000).toString(36);
      this.clientId = data.clientId
      let chatMode = this.$route.path.indexOf('chat') != -1
      if (!chatMode) {
        data.knowledgeBaseId = this.qaAllData.applicationId
      }
      data.type = this.$route.path.indexOf('chat') != -1 ? 'QA' : 'KNOWLEDGE_BASE'
      data.clientType = 'PC'
      this.previousAnswer = ''
      this.refList = []
      this.$store.state.workflow.dialogueLoading = true
      if (!data.content) {
        data.content = this.text
      }
      const { content, param, paragraph, skillId } = JSON.parse(JSON.stringify(data));
      data.ipAddress = sessionStorage.getItem('ipAddress') || ''
      const tempData = JSON.parse(
        JSON.stringify({
          // answer: "近三个月俄罗斯与乌克兰的热点信息:\n:::r 1. 英国将向乌克兰提供巡航导弹俄罗斯称将予以回应,100\n:::r 2. 俄罗斯别尔哥罗德州在与乌克兰接壤地区建立七个国土防御营,89\n:::r 3. 一名乌克兰公民因间谍罪被俄罗斯判处16年有期徒刑,70\n:::r 4. 美国首次批准将被没收的俄罗斯资金用于重建乌克兰,70\n:::r 5. 俄罗斯国防部称摧毁乌克兰海军最后一艘作战舰艇,58\n:::r 6. 乌克兰宣布对俄罗斯258个实体及个人实施制裁,50\n:::r 7. 中钢网新闻中心俄罗斯外交部：西方是乌克兰军事规划的幕后推手,39\n:::r 8. 13日，俄罗斯国防部发言人科纳申科夫通报,39\n:::r 9. 俄乌和平谈判中包括乌克兰宣布中立的选项,39\n:::r 10. 俄罗斯国防部承认俄军在巴赫穆特北部撤退,39",
          citations: [],
          // answer:'此前中国的探空火箭已经成功将探测器送入国际空间站进行科学实验和研究工作，此次计划的实施将\n```js 123123 ```\n为未来开展深层次的科学研究提供更加广阔的空间和资源平台。\n\n    目前该项目正在积极筹备当中，预计将于2021年发射升空的载人飞船也将搭载着宇航员前往火星和其他星球上执行各种重要的科研考察活动。同时还将建立一系列的基础设施和服务体系来支持未来的发展需求。',
          createTime: '',
          answer: '',
          param: param,
          question: content,
          loading: true,
          dialogueFileIds: JSON.parse(JSON.stringify(data)).dialogueFileIds,
          dialogueFolderIds: JSON.parse(JSON.stringify(data)).dialogueFolderIds,
          imgUrl: JSON.parse(JSON.stringify(data)).imgUrl,
          paragraph: paragraph,
          skillId: skillId,
        })
      )
      this.chatList.push(tempData)
      this.scrollbarBottom = true;
      // this.setScrollPosition('.center-side')
      //return
      this.answerStr = ''
      let dialogueByStream = tempData

      try {
        if (workflowValue) {
          if (this.realFileList?.length > 0) {
            this.fileuploadList = [...this.realFileList]

          } else {
            this.fileuploadList = []
          }
        }

        sessionStorage.setItem("outline", '11')
        this.abortController = new AbortController()
        let originalAnswer = ''
        let url = data.templateId ? process.env.VUE_APP_BASE_API + '/wrt-template-entity/kefu-text-generate-stream'
          : workflowValue ? process.env.VUE_APP_BASE_API + '/workflow/dialogueRun' : process.env.VUE_APP_BASE_API + '/dialogue/dialogueByStream'
        //  LLM的参数
        // let params = {
        //   applicationId:this.qaAllData.applicationId,
        //   clientId:data.clientId,
        //   content:data.content,
        //   conversationId: this.conversationId,
        //   dialogueFileIds: [],
        //   dialogueFolderIds:[],
        //   ipAddress: data.ipAddress,
        //   knowledgeBaseId: data.knowledgeBaseId,
        //   policyId: '',
        //   type: data.type,
        //   debuggerFlag: '否',
        //   clientType:'PC',
        //   searchWay: data.searchWay,
        //   searchType: data.searchType,
        // }
        //文字生成的参数
        let writeParams = {
          clientId: data.clientId,
          templateId: data.templateId,
          templateItems: [
            {
              id: 'ad7523d8-d128-45c3-9478-820b49c51249',
              name: 'TITLE',
              nameCn: '标题',
              valueDefault: '标题',
              value: data.templateName,
              sort: 6,
              style: '',
              isChecked: true,
            },
            {
              id: 'b5d551d3-4022-4262-91e0-780d36bf8d08',
              name: 'SUMMARY',
              nameCn: '摘要',
              valueDefault: '摘要',
              value: data.content,
              sort: 8,
              style: '',
              isChecked: true,
            },
          ]
        }
        let inputs = {

        }
        // 如果是工作流这块的
        if (workflowValue && data.dataParms && Array.isArray(data.dataParms)) {

          data.dataParms.forEach(item => {
            if (!('files' in item)) {
              inputs[item.label] = item.value;
            }
          });
        } else {
          // 其他情况
          inputs.question = data.content;
        }
        //文档提问
        let fileVals = {}

        if (workflowValue && this.fileuploadList?.length > 0) {
          let nodes = this.nodeInfo.nodes

          let startNode = nodes.find((item) => {
            return (item.nodeType === 1)
          }).output
          this.fileuploadList = this.fileuploadList.filter(item => item != null);
          let fileUploadList = this.fileuploadList.map(item => {
            let extensionArr = item.fileName.split('.')
            return {
              "type": 'default',
              "transferMethod": "remote_url",
              "remoteUrl": item.urlPath,
              "filename": item.fileName,
              "extension": extensionArr[extensionArr.length - 1]
            }
          })

          startNode.forEach(item => {
            if (item.type &&
              [
                "default",
                "image",
                "doc",
                "code",
                "file",
                "ppt",
                "array[file]",
                "txt",
                "excel",
              ].includes(item.type)) {

              fileVals[item.label] = fileUploadList

            }
          })
        }
        // ...fileVals

        let paramInputs = {
          ...inputs,
          ...fileVals,
        }
        let workflowParams
        if (workflowValue) {
          workflowParams = {
            "componentId": this.nodeInfo.componentId,
            "inputs": paramInputs,
            "clientId": Math.floor(Math.random() * 10000000000),
            "clientType": 'PC',
          }
        }
        let params = workflowValue ? workflowParams : data.templateId ? writeParams : {
          applicationId: this.qaAllData.applicationId,
          clientId: data.clientId,
          content: data.content,
          conversationId: this.conversationId,
          dialogueFileIds: [],
          dialogueFolderIds: [],
          ipAddress: data.ipAddress,
          knowledgeBaseId: data.knowledgeBaseId,
          policyId: '',
          type: data.type,
          debuggerFlag: '是',
          clientType: 'PC',
          searchWay: data.searchWay,
          searchType: data.searchType,
          attachmentList: JSON.parse(JSON.stringify(this.fileAttachmentList)),
          networkFlag: data.networkFlag
        }
        this.fileAttachmentList = []
        let headerParams = {}
        // 访问url带accountName参数 对话接口传入
        const accountName = window.location.hash?.split('accountName=')?.length >= 1 ? window.location.hash.split('accountName=')[1] : ''
        if (accountName) {
          headerParams['Account-name'] = accountName;
          headerParams['Application-id'] = this.qaAllData.applicationId
        }
        this.useEventSource(url, params, this.abortController,
          (list) => {
            try {
              dialogueByStream = JSON.parse(list);
              if (dialogueByStream && !dialogueByStream.answer && dialogueByStream.output && dialogueByStream.output.text) {
                dialogueByStream.answer = dialogueByStream.output.text
              }
              dialogueByStream.question = data.content
			  if(!dialogueByStream.nodeId) {
					if(dialogueByStream.output){
						this.outputJsonData = dialogueByStream.output;
					}
			    
			  }else{
			  	this.nodeIdList.push(dialogueByStream.nodeId)
			  }	
			   this.$EventBus.$emit("apiEnding", this.outputJsonData);
			  this.$EventBus.$emit("apiEndingOutput", dialogueByStream);					  			  
			  this.$EventBus.$emit("apiEndingNodeId", this.nodeIdList,true);
              if (dialogueByStream.nodeId) {
                return
              }
              
              sessionStorage.setItem("answerFlag", dialogueByStream.answerFlag)
              sessionStorage.setItem("dialogueId", dialogueByStream.dialogueId)
              if (dialogueByStream.finishReason == "ANSWER_COMPLETE") {
                dialogueByStream.matterGuide = dialogueByStream.matterGuide ? typeof dialogueByStream.matterGuide === 'string' ? JSON.parse(dialogueByStream.matterGuide) : dialogueByStream.matterGuide : null

                if (dialogueByStream.outline) {
                  sessionStorage.setItem("outline", JSON.stringify(dialogueByStream.outline))
                  // mittBus.emit('outline', dialogueByStream.outline);
                }
                sessionStorage.setItem("matterGuide", JSON.stringify(dialogueByStream && dialogueByStream.matterGuide) || '[]')
                // mittBus.emit('matterGuide', dialogueByStream && dialogueByStream.matterGuide);
              }
              if (dialogueByStream.isOverdue) {
                this.noticeData = {
                  msg: '余额不足',
                  id: -1,
                };
                this.isShowNoice = true;
              }
              if (sessionStorage.getItem('yayiFlag') === 'true') {
                const { answer = '' } = dialogueByStream;
                this.setAiRobotChat(answer.slice(originalAnswer.length, answer.length));
                originalAnswer = answer;
              }
              if (dialogueByStream.reasoningContent) {
                let reasoningContent = `<p style=" color: #8b8b8b;  ">${dialogueByStream.reasoningContent}</p>`
                this.answerStr = reasoningContent + dialogueByStream.answer
              } else {
                this.answerStr = dialogueByStream.answer;
              }
              this.plainText = dialogueByStream.plainText;
              if (dialogueByStream.reasoningContent) {
                let reasoningContents = dialogueByStream.reasoningContent
                  .split('\n\n') // 按双换行符分割段落
                  .filter(paragraph => paragraph.trim() !== '') // 过滤空段落
                  .map((paragraph, index) => (
                    `<span key=${index} class="reason-paragraph">
                    ${paragraph.trim()}
                  </span>`
                  ))
                  .join('');
                let reasoningContent = `<span class="reason-cont"><span class="reason-line"></span>${reasoningContents}</span>`
                let answer = dialogueByStream.answer ? dialogueByStream.answer : ''
                dialogueByStream.answer = reasoningContent + answer
              }
              this.refList = dialogueByStream.refList ? dialogueByStream.refList : this.refList
              if (dialogueByStream && dialogueByStream.answer && dialogueByStream.answer.length > 0) {
                dialogueByStream.answer = dialogueByStream.answer && dialogueByStream.answer.replace(/<span/g, '<div').replace(/<\/span>/g, '</div>')
                  // 转义 [xxx]: 开头的模式（保留方括号但防止被识别为链接引用）
                  .replace(/- \[(.*?)\]:/g, '- \\[$1\\]:')
                  // 处理带链接的条目（保留链接格式）
                  .replace(/(\[.*?\])(\(.*?\))/g, '$1$2') // 保持原有链接不变
                  .replace(/◹\[(\d+)\]◸/g, (match, p1) => {
                    return `<span class="ragTag" data-index="${p1}"><svg viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg" width="61" height="61"><path fill="#0071be" d="M11.997 5v3.336a3.517 3.517 0 0 1-.879 2.464 3.728 3.728 0 0 1-2.32 1.2v-1.44c.572-.189.962-.469 1.172-.84.211-.37.315-.824.315-1.362H8.798V5h3.2ZM7.2 5v3.336a3.502 3.502 0 0 1-1.903 3.26A3.733 3.733 0 0 1 4 12v-1.44c.571-.188.962-.468 1.171-.838.212-.371.316-.825.316-1.363H4V5h3.2Z" data-follow-fill="#9A99AA"></path></svg></span>`;
                  }) // 下标
              }
              this.chatList[this.chatList.length - 1] = JSON.parse(JSON.stringify(dialogueByStream));
              this.$store.state.workflow.dialogueInputLoading = true
              this.chatList[this.chatList.length - 1].loading = true;
              this.chatList = [...this.chatList]
              this.$store.state.workflow.scrollbarBottom = true
              //        setTimeout(()=>{
              //         if(this.chatListLength != this.chatList.length){
              //           const message=document.querySelector('.message-list')

              //  message.scrollTop = message.scrollHeight
              //         }
              //         this.chatListLength = this.chatList.length
              //        },700)

              this.setScrollPosition('.message-list');
              if (this.chatList[this.chatList.length - 1].answer == '很抱歉，我无法提供答案') {
                // this.$store.state.workflow.dialogueInputLoading = false
                this.closeTalk()
              }
              const dataNode = this.chatList;
              // 将问题传递给 Vue 组件或 Vuex 进行存储
              this.$store.dispatch('addQuestion', dataNode);

            } catch (error) {
              if (this.abortController) {
                this.abortController?.abort();
              }
              this.chatList[this.chatList.length - 1].answer = this.errorText;
              this.chatList[this.chatList.length - 1].question = data.content;
              sessionStorage.setItem("answerFlag", false)
              this.chatList[this.chatList.length - 1].loading = false;
              this.chatList = [...this.chatList]
              this.$store.state.workflow.dialogueLoading = false;
            }
          },
          () => {
            this.$store.state.workflow.dialogueLoading = false;
          },
          () => {
            if (this.abortController) {
              this.abortController?.abort();
            }
            this.chatList[this.chatList.length - 1].answer = this.errorText;
            this.chatList[this.chatList.length - 1].question = data.content;
            sessionStorage.setItem("answerFlag", false)
            this.chatList[this.chatList.length - 1].loading = false;
            this.$store.state.workflow.dialogueLoading = false;
            this.chatList = [...this.chatList]
            this.$store.state.workflow.dialogueInputLoading = false
            clearInterval(timer)
          },
          headerParams
        )
      } catch (err) {
        this.$store.state.workflow.dialogueInputLoading = false
        if (this.abortController) {
          this.abortController?.abort();
        }
        this.chatList[this.chatList.length - 1].answer = this.errorText;
        this.chatList[this.chatList.length - 1].question = data.content;
        sessionStorage.setItem("answerFlag", false)
        this.chatList[this.chatList.length - 1].loading = false;
        this.chatList = [...this.chatList]
        this.$store.state.workflow.dialogueLoading = false;
      } finally {
        this.$store.state.workflow.dialogueInputLoading = false
        this.$store.state.workflow.dialogueLoading = false
      }
    },
    //输入发送
    async setinput() {
      this.$store.state.workflow.dialogueInputLoading = true

      // 确定点了发送的时候才把每轮对话对应有没有上传文件记录保存下来
      if (this.fileList.length > 0) {

        this.$store.state.workflow.tempFileList.push(this.fileList)

        this.$store.state.workflow.tempFileList.push([])
      }
      // 回答没结束之前不允许发送第二次
      if (this.$store.state.workflow.dialogueLoading) {

        return
      }
      this.tjQuestListRecommend = [];
      let str = this.text;
      //只发送文件
      if (this.fileList.length > 0 && str == '') {
        for (let i = 0; i <= this.fileList.length - 1; i++) {
          if (i == this.fileList.length - 1) {
            str += this.fileList[i].fileName
          } else {
            str += this.fileList[i].fileName + '，'
          }
        }
      }
      await this.setList({
        clientType: 'PC',
        content: str.trim(),
      })
      //初始化处理
      const message = document.querySelector('.message-list')
      message.scrollTop = message.scrollHeight
      this.text = ''
      this.fileListOrgin = []
      this.fileList = []
      this.realFileList = []
      this.outTextarea.classList.add('height1')
      this.textarea.classList.add('height2')
    },
    // async  setDialogueData(text,params){
    //  let cvImgUrl = this.$store.state.workflow.fiileUpdateCV.url
    //  if(text == '' && cvImgUrl == ''){
    //   return
    //  }
    //  let arr = {}
    // //  if(params){
    // //   param.value.forEach((item) => {
    // // 	Object.keys(params).forEach((param) => {
    // // 		if (item.key == param) {
    // // 			item.defaultValue = params[param];
    // // 		}
    // // 	});
    // // });
    // //  }
    // this.$store.state.workflow.fiileUpdateCV.url=''
    // await this.setList(data)
    //   },
    //关闭对话
    async closeTalk() {
      if (this.abortController) {
        this.abortController?.abort()
      }
      if (this.clientId && this.chatList.length) {
        await closeDialogueConn({ clientId: this.clientId })
        this.$store.state.workflow.dialogueInputLoading = false
        this.chatList[this.chatList.length - 1].loading = false;
        this.chatList[this.chatList.length - 1].answer = this.answerStr || '停止生成';
        this.chatList = [...this.chatList]
        const message = document.querySelector('.message-list')
        message.scrollTop = message.scrollHeight
      }
    },
    //控制对话框滚动
    setScrollPosition(el) {
      const dom = document.querySelector(el)
      const clientHeight = dom.clientHeight;
      const scrollTop = dom.scrollTop;
      const scrollHeight = dom.scrollHeight;
      if (clientHeight + scrollTop + 15 >= scrollHeight) {
        this.$store.state.workflow.scrollbarBottom = true;
      } else {
        this.$store.state.workflow.scrollbarBottom = false;
      }

      this.$nextTick(() => {
        if (this.$store.state.workflow.scrollbarBottom) {

          dom.style.scrollBehavior = 'smooth';
          dom.scrollTop = dom.scrollHeight
        }

      })
    },


    //初始的
    ...mapMutations(["setFuncData"]),
    temporarSave() {
      this.previewLoading = true
      this.chatList = []
      this.addNewConversation()
      this.qaAllData = JSON.parse(JSON.stringify(this.params))
    },
  },
  unmounted() {
    this.$store.state.workflow.dialogueInputLoading = true
    this.$store.state.workflow.dialogueLoading = false
    this.$store.state.workflow.tempFileList = []
  },
  computed: {
    dialogueInputLoading() {
      return this.$store.state.workflow.dialogueInputLoading
    },
    workflowOrdialogues() {
      return this.qaAllData.type != 'workflow' && this.type != 5 && this.type != 7
    }
  }
}
</script>
<style lang="scss" scoped>
::v-deep .el-button {
  width: 24px;
  height: 24px;
}

.height2 {
  height: 48px !important;
}

::v-deep .height1 {
  height: 48px !important;
}

.noTopBorder {
  border-radius: 0 0 8px 8px !important;
}

::v-deep .el-textarea__inner {
  border: 0 !important;
  // height: 48px!important;
  // padding-top:15px;
  padding-top: 15px;
  padding-right: 75px;
  padding-left: 45px;
  // height: 48px!important;
  // &::placeholder{
  //   padding-top:10px;
  // }
}

.preview-container {
  // padding:0 16px;
  width: 100%;
  height: 100%;
  background: #fff;
  position: relative;

  .container {
    &::-webkit-scrollbar {
      display: none;
    }
  }

  .message-list {
    // height: calc(100% - 60px);
    height: 100%;
    overflow: scroll;

    &::-webkit-scrollbar {
      display: none;
    }

    .quest-list {

      // padding-top: 16px;
      &-title {
        font-family: MiSans, MiSans;
        font-weight: 600;
        font-size: 14px;
        color: #828894;
        line-height: 20px;
        margin-bottom: 4px;
      }

      &-item {
        .item {
          position: relative;
          margin-top: 8px;
          display: inline-block;
          position: relative;
          font-family: MiSans, MiSans;
          color: #646479;
          height: 42px;
          padding: 8px 16px;
          background: #ffffff;
          border-radius: 2px;
          border: 1px solid #d5d8de;
          font-size: 16px;
          color: #494e57;
          line-height: 24px;
          cursor: pointer;
          max-width: 100%;
          white-space: nowrap;
          /* 强制文本在一行显示 */
          overflow: hidden;
          /* 隐藏超出容器的内容 */
          text-overflow: ellipsis;

          /* 超出部分显示省略号 */
          iconpark-icon {
            display: none;
          }

          &:hover {
            background: #ffffff;
            box-shadow: 0px 5px 8px 0px rgba(45, 55, 85, 0.1);
            // border-radius: 8px;
            color: #2065d6;
            padding: 8px 42px 0 16px;

            iconpark-icon {
              display: block;
              position: absolute;
              top: 10px;
              right: 16px;
            }
          }
        }


        //   .item:hover {
        // 	background: #4d77ef;
        // 	color: #fff;
        // 	border: none;
        //    iconpark-icon {
        // 		display: block;
        // 	}
        // }
      }
    }

    .introduce {
      .avatar {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 8px;
        width: 28px;
        height: 28px;
        overflow: hidden;
        border-radius: 50%;

        img {
          width: 100%;
          height: 100%;
        }
      }

      .prologue {
        background: rgba(247, 248, 250) !important;
        padding: 11px 16px !important;
        border-radius: 2px 8px 8px !important;
        border: 1px solid rgb(255, 255, 255);
        max-width: 91%;
        font-size: 16px;
        color: rgba(0, 0, 0);
        min-width: 20px;
        // font-weight:500;
      }
    }
  }

  .chatModule {
    // padding:0 16px;
    // height: 128px;
    // width: calc(100% - 20px);
    width: 100%; // bottom: 16px;
    box-sizing: border-box;
    z-index: 100;

    // position: absolute;
    // left: 50%;
    // transform: translateX(-50%);
    // bottom: 25px;
    &-tips {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #bcc1cc;
      line-height: 16px;
      text-align: center;
      margin-top: 6px;
    }

    .basicTemplate-chatInput {
      width: 100%;
      position: relative;
      // padding: 10px;

      :deep(.w-textarea) {
        line-height: 1.5 !important;
        font-size: 16px;
        padding: 12px 40px 6px 50px;
      }

      .w-textarea-wrapper {
        background: rgba(255, 255, 255, 0.9);
        // box-shadow: 0px 4px 16px 0px rgba(0, 0, 0, 0.1);
        // border-radius: 16px;
        border: 1px solid #D5D8DE;
        // backdrop-filter: blur(2px);
        width: 100%;
      }
    }

    .chatInput-pc {
      :deep(.w-textarea) {
        // padding: 12px 120px 4px 62px;
        height: 48px !important;
      }

      .w-textarea-wrapper {
        height: 48px;
        border: 1px solid #d0d5dc;
        overflow-y: scroll;

        &::-webkit-scrollbar {
          display: none !important;
        }
      }

      // .lineHeight1 {
      // 	:deep(.w-textarea) {
      // 		line-height: 1.5 !important;
      // 	}
      // }
      // .lineHeight2 {
      // 	:deep(.w-textarea) {
      // 		line-height: 48px !important;
      // 	}
      // }
    }

    .textarea {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      border-radius: 8px;
      border: 1px solid #E7E7E7;
      backdrop-filter: blur(4px);
      background: rgba(235, 241, 249);
      overflow: hidden;

      .loadingAnimate {
        position: absolute;

        &:hover {
          cursor: pointer;
          // background-color: #F4F6F9;
        }
      }

      .fileList {
        width: 100%;
        // height: 72px;
        display: flex;
        align-items: center;
        overflow-x: auto;
        background-color: #ffffff;
        border-radius: 8px 8px 0 0;
        z-index: 3;

        .file-detail {
          position: relative;
          border-radius: 8px;
          display: flex;
          height: 56px;
          background-color: #F2F3F5;
          margin-left: 8px;
          margin-top: 8px;
          margin-bottom: 8px;

          &:first-child {
            margin-left: 8px;
          }

          .left {
            width: 56px;
            height: 56px;
            display: flex;
            justify-content: center;
            align-items: center;

            .img {
              width: 32px;
              height: 32px;
              display: flex;
              justify-content: center;
              align-items: center;

              .icon {
                width: 100%;
                height: 100%;
              }
            }
          }

          .text {
            flex: 1;

            .text-header {
              margin-top: 8px;
              width: 182px;
              height: 18px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #383D47;
              line-height: 18px;
              text-align: left;
              font-style: normal;
              width: 200px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }

            .text-kb {
              margin-top: 4px;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #B4BCCC;
              line-height: 18px;
              text-align: left;
              font-style: normal;
            }
          }

          .delete {
            position: absolute;
            top: -4px;
            right: -4px;
            width: 18px;
            height: 18px;
            // border-radius: 50%;
            // background: #3F4247;
            display: flex;
            justify-content: center;
            align-items: center;

            &:hover {
              cursor: pointer;
            }
          }
        }

      }

      .refresh {
        width: 32px;
        height: 32px;
        background: rgba(180, 188, 204, 0.2);
        border-radius: 2px;
        position: absolute;
        left: 8px;
        bottom: 8px;
        display: flex;
        align-items: center;
        cursor: pointer;
        justify-content: center;

        img {
          width: 20px;
          height: 20px;
        }
      }

      .sendBtn {
        // border-radius: 50%;
        // background: #2065d6;
        display: flex;
        align-items: center;
        justify-content: center;
        // position: absolute;
        // right: 0px;
        // top: 0px;
        // background: none;
        border: none;
        background-color: #fff;
        // font-weight: 500;
        // font-size: 14px;
        // color: #ffffff;
        // // line-height: 40px;
        // text-align: center;
        position: absolute;
        bottom: 11px;
        right: 12px;
        z-index: 999;

        img {
          z-index: 2;
          width: 23px;
          height: 23px;
          //   &:hover{
          //   cursor: pointer;
          // }
        }

        // :deep(.w-textarea) {
        // 	line-height: 1.5 !important;
        // 	@include add-size($font-size-base16, $size);
        // 	padding: 16px 120px 4px 16px;
        // 	backdrop-filter: blur(4px);
        // 	background: rgba(255,255,255,0.8);
        // }

        // .w-textarea-wrapper {
        // 	// background: rgba(255, 255, 255, 0.9);
        // 	background-color: rgba(244, 246, 246, 1);
        // 	box-shadow: 0px 4px 16px 0px rgba(0, 0, 0, 0.1);
        // 	border-radius: 12px 12px 12px 12px;
        // 	border: 1px solid rgba(255,255,255,0.8);;
        // 	backdrop-filter: blur(2px);
        // 	width: 100%;
        // 	font-family: MiSans, MiSans;

        // }
      }
    }
  }
}
</style>