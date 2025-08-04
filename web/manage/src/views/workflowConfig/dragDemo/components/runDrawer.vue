<template>
  <div class="Ec-x6-icon">
      <el-drawer
          title=""
          :visible.sync="drawerVisible" 
          :modal="false"
          :modal-append-to-body="false"
          :direction="direction"
          v-if="drawerVisible"
          size="456px"
          style="
              position: absolute;
              width: 456px;
              box-sizing: border-box;
              left: 66px;
              right: inherit;
          "
          :show-close="false"
      >
          <div class="drawer-header">
              <img src="@/assets/images/icon-contacts-fill.png" />
              <span>{{$t('test')}}</span>
              <i
                  class="el-icon-close custom-close-icon"
                  @click="closeDrawer"
              ></i>
          </div>
          <div class="drawer-content plugin" id="listenEnter">
              <div class="plugin-ft">
                  <div class="plugin-ft-cont">
                      <div class="radio-list">
                          <span
                              class="radio-list-left"
                              :class="{ active: runType === '1' }"
                              @click="runType = '1'"
                              >{{$t('input')}}</span
                          >
                          <span
                              class="radio-list-right"
                              :class="{ active: runType === '2' }"
                              @click="runType = '2'"
                              >{{$t('output')}}</span
                          >
                      </div>
                      <div class="typeIn" v-if="runType === '1'">
                          <el-form
                              :model="ruleForm"
                              :rules="rulesTypeIn"
                              ref="ruleForm"
                              label-width="0"
                              @submit.native.prevent="handleSubmit"
                              class="demo-ruleForm params-list"
                          >
                              <el-form-item
                                  label=""
                                  v-for="(panel, index) in startValues"
                                  :key="index"
                              >
                                  <div class="params-list-item">
                                      <div class="params-list-item-name">
                                          {{ panel.label }}
                                          <span
                                              class="params-list-item-type"
                                              :class="{
                                                  required: panel.required,
                                              }"
                                              >{{ ['default','image','doc','code','ppt','txt','excel'].includes(panel.type) ? "file" : panel.type }}</span
                                          >
                                      </div>
                                  </div>
                                  <el-upload
                                    v-if="['file','default','image','doc','code','ppt','txt','excel','array[file]'].includes(panel.type)"
                                    class="uploadOuter"
                                    :ref="'upload' + index"
                                    drag
                                    :multiple="panel.type === 'array[file]'"
                                    :action="actionUrl"
                                    :data="{ filePath: 'agent_source', rename:true }"
                                    :file-list="panel.fileList"
                                    :limit="panel.type === 'array[file]' ? null : 1"
                                    :on-success="(response, file, fileList) => handleIdentityIconSuccess(response, file, fileList, index)"
                                    :accept="acceptFile[panel.valueType]?.type"
                                    :before-upload="(file)=>beforeUpload(file,index)"
                                    >
                                    <i class="el-icon-upload"></i>
                                    <div class="el-upload__text">点击或拖拽文件上传</div>
                                    <!-- <span style=" font-size: 12px; color: #B4BCCC; ">支持文档格式：PDF、Word、TXT、</span> -->
                                  </el-upload>
                                  <div v-else-if="panel.type === 'number'">
                                        <el-input type="textarea"  v-if="panel.inputType=='textarea'" v-model="panel.value" @input="panel.value = panel.value.replace(/[^\d.]/g, '').replace(/(\..*)\./g, '$1')" @keyup.enter.native="runApi"></el-input>
                                        <el-select v-else-if="panel.inputType=='select'" v-model="panel.value" placeholder="请选择">
                                            <el-option
                                            v-for="ele in optionList(panel.selectValues)"
                                            :key="ele.value"
                                            :label="ele.label"
                                            :value="ele.value">
                                            </el-option>
                                        </el-select>
                                        <el-input v-else type="number" v-model="panel.value"  @keyup.enter.native="runApi"></el-input>
                                    </div>
                                    <div v-else>
                                        <!-- {{ panel }} -->
                                        <el-input type="textarea"  v-if="panel.inputType=='textarea'" v-model="panel.value"  @keyup.enter.native="runApi"></el-input>
                                        <el-select v-else-if="panel.inputType=='select'" v-model="panel.value" placeholder="请选择">
                                            <el-option
                                            v-for="ele in optionList(panel.selectValues)"
                                            :key="ele.value"
                                            :label="ele.label"
                                            :value="ele.value">
                                            </el-option>
                                        </el-select>
                                        <el-input v-else v-model="panel.value" @keyup.enter.native="runApi"></el-input>
                                    </div>

                                  <!-- <el-input v-else-if="panel.type === 'number'" type="number" v-model="panel.value" @keyup.enter.native="runApi"></el-input>
                                  <el-input v-else  v-model="panel.value" @keyup.enter.native="runApi"></el-input> -->

                              </el-form-item>
                          </el-form>
                          <el-button
                              type="primary"
                              class="run-btn"
                              @click="runApi"
                              :loading="runApiLoading"
                              ><i class="el-icon-video-play" style="margin-right: 8px;"></i>{{$t('test')}}</el-button
                          >
                      </div>
                      <div class="typeIn" v-if="runType === '2'">
                          <div class="output" id="output">
                            <vue-json-pretty v-if="isJsonString(outputJsonData)"
                                  :showNum="false"
                                  :data="JSON.parse(outputJsonData)"/>
                              <span class="output-text" v-for="(value, key) in outputJsonData" :key="key"><strong>{{ key }}:</strong> {{ value }}</span>
                          </div>
                          <!-- <div class="tips">
                                  <span
                                      ><img
                                          src="@/assets/images/icon-contacts-fill.png"
                                      />点击开始运行查看结果</span
                                  >
                              </div> -->
                          <el-button
                              type="primary"
                              class="run-btn"
                              @click="runApi"
                              :loading="runApiLoading"
                              ><i class="el-icon-video-play" style="margin-right: 8px;"></i>{{$t('test')}}</el-button
                          >
                      </div>
                  </div>
              </div>
          </div>
      </el-drawer>
  </div>
</template>

<script>
import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';
import { fetchEventSource } from "@microsoft/fetch-event-source";
import {
  saveWorkflow,
  copyWorkflowApp,
} from "@/api/workflow";
import md5 from "js-md5";

export default {
  props: ["componentParams", "endNode", "startDrawerData"],
  name: "runDrawer",
  components: { VueJsonPretty },
  computed: {
//    生成下拉框数据
    optionList(){
        return (data)=>{
            if(!data) return []
            const arr=[]
            data.split(",").map(item=>{
                if(item){
                    arr.push({
                    value:item,
                    label:item
                    })
                }
            })
            return arr
        }
    }
  },
  data() {
      return {
          startValues:{},
          actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
          ruleForm: {
              name: "",
          },
          rulesTypeIn: {
              rawQuery: [
                  {
                      required: true,
                      message: this.$t("pleaseEnterRawQuery"),
                      trigger: "blur",
                  },
              ],
          },
          drawerVisible: false,
          runType: "1",
          direction: "ltr",
          runApiLoading: false,
          outputDataByText: "",
          outputJsonData: '',
          uploadDataObj: {},
          nodeData: {},
		  nodeIdList:[],
          acceptFile:{
            doc:{
                name:".doc,.docx",
                type:".doc,.docx"
            },
            image:{
                name:".jpg,.png,.svg等",
                type:"image/*"
            },
            txt:{
                name:".txt",
                type:".txt"
            },
            ppt:{
                name:".ppt,.pdf",
                type:".ppt,.pdf"
            },
            excel:{
                name:".xls,.xlsx",
                type:".xls,.xlsx"
            },
            default:{
                name:"任意",
                type:""
            },
            "array[file]":{
                name:"任意",
                type:""
            }
          }
      };
  },
  watch: {
    startValues: {
      handler(newVal) {
        const obj = {};
        newVal.forEach(item => {
          obj[item.label] = item.value;
        });
        sessionStorage.setItem('startValues', JSON.stringify(obj));
      },
      deep: true,
    },
  },
  mounted() {
    // document.addEventListener( "keydown",
    //     (event) =>{
    //       if(this.drawerVisible && event.key === 'Enter')
    //       this.runApi()
    //     }
    // )
  },
  methods: {
      handleSubmit() {
        // 处理输入框的逻辑
        e.preventDefault()
      },
      scrollToBottom() {
        this.$nextTick(() => {
            const outputElement = document.getElementById('output');
            if (outputElement) {
                outputElement.scrollTop = outputElement.scrollHeight;
            }
        });
      },
      handleIdentityIconSuccess(response, file, fileList, index) {
        if (response && response.code === '000000') {
          let arr = this.uploadDataObj[index] && Array.isArray(this.uploadDataObj[index]) ? JSON.parse(JSON.stringify(this.uploadDataObj[index])) : []
          arr.push(response.data[0])
          this.uploadDataObj[index] = arr
        } else {
        }
      },
      isJsonString(str) {
          try {
              JSON.parse(str);
          } catch (e) {
              return false;
          }
          return true;
      },
      runApi() {
          let params = {};
          let that = this;
          this.runApiLoading = true;
		  this.nodeIdList = [];
          let values = this.startValues && Array.isArray(this.startValues) ? this.startValues : this.startValues ? JSON.parse(this.startValues) : []
		  values.forEach((el, index) => {
              if(['default','image','doc','code','file','ppt','txt','excel','array[file]'].includes(el.type) && this.uploadDataObj[index]){
                let resObj = this.uploadDataObj[index]
                let arr = []
				
                resObj.forEach(item => {
                  let extensionArr = item.fileName.split('.')
                  let obj = {
                      "type": 'default',
                      "transferMethod": "remote_url",
                      "remoteUrl": item.urlPath,
                      "filename": item.fileName,
                      "extension": extensionArr[extensionArr.length - 1]
                  }
                  arr.push(obj)
                })
                if(el.type === 'array[file]' || arr.length > 1){		
                    params[el.label] = arr
                }else{
                  params[el.label] = arr[0]
                }
                let ref = 'upload' + index
                that.$refs[ref][0].clearFiles();
                this.uploadDataObj[index] = null
              } else {
                params[el.label] = el.value;
              }
          });
          that.$EventBus.$emit("apiStarting", this.nodeData);
          let paData = {
              componentId: this.componentParams.componentId,
              inputs: params,
              clientId: Math.floor(Math.random() * 10000000000),
          };
          let ctrlAbout = new AbortController();
          let manageAccessToken = sessionStorage.getItem("manageAccessToken");
          let timer = new Date().getTime();
          that.$EventBus.$emit("clearIterationList");
          fetchEventSource(
              `${process.env.VUE_APP_BASE_API}/workflow/dialogueRun`,
              {
                  method: "post",
                  headers: {
                      Authorization: `Bearer ${manageAccessToken}`,
                      "Content-Type": "application/json",
                      timestamp: timer,
                      cipher: md5(timer + `/workflow/dialogueRun${JSON.stringify(paData)}xxxxxxxxxxx`),
                  },
                  signal: ctrlAbout.signal,
                  body: JSON.stringify(paData),
                  openWhenHidden: true, //默认为false，监听visibilitychange，当页面不可见时关闭连接，当页面重新可见时重新打开连接。
                  onmessage(event) {
                      // 成功之后操作
                      that.runType = "2";
                      let data = JSON.parse(event.data);
                    //   console.log(data.nodeId,"LLLLLLLLLLLLLLLLLLLL");
                      
                      if(!data.nodeId) {
						if(data.output){
							that.outputJsonData = data.output;
						}
                        that.scrollToBottom()
                      }else{
						  that.nodeIdList.push(data.nodeId)
					  }
					  that.$EventBus.$emit("apiEndingOutput", data);					  
					  that.$EventBus.$emit("apiEnding", that.outputJsonData);
					  that.$EventBus.$emit("apiEndingNodeId", that.nodeIdList,true);
                  },
                  onerror() {
                      console.log(11);
                      // 服务异常
                      console.log("shibai")
                  },
                  onclose() {
                      that.runApiLoading = false;
                      // 服务关闭
                      console.log("guanbi5")
                      // that.$EventBus.$emit("apiEnding", that.outputJsonData);
					  that.$EventBus.$emit("apiEndingNodeId", that.nodeIdList,false);
                  },
              }
          );
      },
      openDrawer() {
          this.drawerVisible = true;
          setTimeout(() => {
            this.startValues = JSON.parse(this.startDrawerData)
            const storedValues = sessionStorage.getItem('startValues');
              if (storedValues) {
                const storedObj = JSON.parse(storedValues);
                this.startValues.forEach(item => {
                  if (storedObj[item.label] !== undefined) {
                    item.value = storedObj[item.label];
                  }
                  if(item.inputType=='select'){
                    item.value=""
                  }
                });
              }
          }, 100);
      },
      closeDrawer() {
        this.drawerVisible = false;
        // this.startValues.forEach(item=>{
        //     item.value=""
        // })
        
      },
      beforeUpload(file,index){
        const {name} =file;
        const arr=name.split(".")
        const fileType=arr[arr.length-1]
        const type=this.startValues[index].valueType
        if(type=="image"){
            // console.log("这是图片");
            
            if(!file.type.includes("image")){
                this.$message({
                    message: '文件格式错误',
                    center: true,
                    type:"info"
                })
                return false;
            }
        }
        else if(type=="default" ||type=="array[file]"){
            return true
        }
        else if(!this.acceptFile[type].type.includes(fileType)){
            this.$message({
                message: '文件格式错误',
                center: true,
                type:"info"
            })
            return false;
        }
	
    }
  },
};
</script>
<style scoped lang="scss">
.custom-icon {
    font-size: 24px;
    margin-right: 10px;
}

.custom-close-icon {
    font-size: 20px;
    cursor: pointer;
    color: #999;
}

.custom-close-icon:hover {
    color: #666;
}

.section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    font-size: 16px;
    color: #383d47;
}

.Ec-x6-icon ::v-deep .el-drawer__wrapper {
    border-radius: 0;
}
.Ec-x6-icon ::v-deep .el-drawer__body {
    overflow-y: auto;
    position: relative;
    overflow-x: hidden;
}
.Ec-x6-icon ::v-deep .el-drawer {
    padding: 16px;
    .el-form-item {
        margin-bottom: 8px;
    }
    .el-select,
    .el-input-number {
        width: 100%;
    }
}

.Ec-x6-icon ::v-deep .el-drawer__header {
    padding: 0;
    margin-bottom: 0;
}

.drawer-header {
  position: relative;
  padding: 0 0 10px;
  margin-bottom: 20px;
  img {
      width: 22px;
  }
  span {
      position: relative;
      bottom: 4px;
      left: 10px;
      font-size: 18px;
      color: #181b49;
      font-weight: bold;
  }
  .custom-close-icon {
      position: absolute;
      right: 0;
      top: 2px;
      cursor: pointer;
  }
  .sub {
      font-size: 14px;
      color: #828894;
      line-height: 18px;
      margin: 10px 0 20px;
      text-align: left;
      font-style: normal;
  }
}
.plugin {
  position: relative;
  color: #383d47;
  &-hd {
      background: linear-gradient(
          180deg,
          rgba(43, 88, 213, 0.1) 0%,
          rgba(43, 88, 213, 0) 100%
      );
      border-radius: 8px 0px 0px 8px;
      padding: 20px;
      height: calc(100vh - 112px);
      position: relative;
      .eidt-api {
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translate(-50%);
          width: 80%;
          border-color: #1c50fd;
          color: #1c50fd;
          border-radius: 4px;
      }
      &-tit {
          font-size: 16px;
          line-height: 20px;
          position: relative;
          i {
              position: absolute;
              right: 0;
              cursor: pointer;
          }
      }
      &-link {
          font-size: 14px;
          margin: 20px 0 40px;
      }
      &-info {
          position: relative;
          li {
              margin: 16px 0;
          }
          &-left {
              font-size: 14px;
              color: #828894;
              line-height: 18px;
          }
          &-right {
              position: absolute;
              right: 0;
              &.green {
                  color: #30be14;
              }
              &.status {
                  &::before {
                      content: "";
                      width: 8px;
                      height: 8px;
                      border-radius: 100%;
                      background: #30be14;
                      position: absolute;
                      left: -16px;
                      top: 3px;
                  }
              }
          }
      }
  }
  &-bd {
      padding: 8px 0 0 20px;
      ::v-deep .el-tabs {
          .el-tabs__item {
              font-size: 16px;
              color: #383d47;
              padding-right: 6px;
          }
          .el-tabs__item.is-active {
              color: #383d47;
          }
          .el-tabs__active-bar {
              background-color: #1c50fd;
          }
          .el-tabs__nav-wrap::after {
              height: 1px;
              background: rgba(0, 0, 0, 0.12);
          }
          .el-tabs__content {
              height: calc(100vh - 175px);
              overflow: auto;
          }
      }
      &-tit {
          font-size: 16px;
          color: #383d47;
          line-height: 28px;
          position: relative;
          padding: 0 0 0 10px;
          span {
              color: #1c50fd;
              font-size: 14px;
              margin: 0 0 0 36px;
              cursor: pointer;
              position: relative;
              em {
                  font-size: 22px;
                  position: absolute;
                  left: -16px;
                  top: -5px;
              }
          }
          &::before {
              content: "";
              width: 3px;
              height: 18px;
              background: #1c50fd;
              position: absolute;
              left: -0;
              top: 4px;
          }
      }
      &-first {
          position: relative;
          .drawer-content {
              padding: 0 20px 0 0;
          }
      }
      ::v-deep .el-form {
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          padding: 26px 16px 16px;
          margin: 0 0 16px 0;
          position: relative;
          i.hide {
              position: absolute;
              right: 12px;
              top: 4px;
              cursor: pointer;
              font-size: 20px;
          }
      }
      .params-list {
          margin: 10px 0 0 0;
          &-item {
              border-radius: 4px;
              border: 1px solid #e1e4eb;
              padding: 16px;
              margin: 0 0 16px 0;
              position: relative;
              &-name {
                  font-size: 16px;
                  color: #383d47;
                  line-height: 20px;
                  font-weight: bold;
              }
              &-type {
                  background: #f2f5fa;
                  border-radius: 4px;
                  font-size: 12px;
                  color: #828894;
                  position: relative;
                  padding: 4px 12px;
                  left: 10px;
                  &.require {
                      &::after {
                          content: "*";
                          position: absolute;
                          left: -12px;
                          top: 3px;
                          color: red;
                          font-size: 20px;
                      }
                  }
              }
              &-desc {
                  font-size: 14px;
                  color: #828894;
                  margin: 10px 0 0 0;
              }
              &-tool {
                  font-size: 20px;
                  position: absolute;
                  right: 16px;
                  top: 16px;
                  color: #828894;
                  i {
                      margin: 0 0 0 10px;
                      cursor: pointer;
                  }
              }
          }
      }
      &-second,
      &-third {
          .params-list {
              overflow: hidden;
              &-item {
                  width: calc(50% - 20px);
                  float: left;
                  margin-right: 20px;
                  background: #ffffff;
                  box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
                  border-radius: 4px;
                  border: 1px solid #e1e4eb;
                  padding: 16px 16px 4px;
              }
              ul {
                  li {
                      margin: 16px 0;
                      position: relative;
                      span {
                          font-size: 14px;
                          color: #828894;
                      }
                      i {
                          position: absolute;
                          right: 0;
                          border-radius: 10px;
                          border: 1px solid #6c49db;
                          color: #6c49db;
                          font-size: 12px;
                          padding: 3px 10px;
                      }
                      em {
                          position: absolute;
                          right: 0;
                          font-size: 14px;
                          color: #383d47;
                      }
                  }
              }
          }
      }
      &-response {
          background: #ffffff;
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          margin: 10px 20px 0 0;
      }
      &-third {
          &-hd {
              margin: 10px 0;
              span {
                  font-size: 14px;
                  color: #828894;
                  margin: 0 30px 0 0;
              }
          }
          .el-form {
              margin: 0 20px 16px 0;
              float: left;
              padding: 20px 20px 0 0;
              width: calc(50% - 20px);
          }
      }
  }
  &-ft {
      padding: 8px 0 0 0;
      position: relative;
      .run-btn {
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translate(-50%);
          width: calc(100% - 64px);
          border-radius: 4px;
      }
      &-tit {
          position: relative;
          padding: 8px 20px 15px 50px;
          font-size: 16px;
          color: #383d47;
          border-bottom: 1px solid #e1e4eb;
          img {
              position: absolute;
              width: 24px;
              left: 20px;
          }
          .clear {
              position: absolute;
              right: 20px;
              font-size: 16px;
              color: #828894;
              cursor: pointer;
          }
      }
      &-cont {
          height: calc(100vh - 210px);
          .radio-list {
              text-align: center;
              margin: 16px 24px;
              span {
                  cursor: pointer;
                  font-size: 16px;
                  color: #828894;
                  background: #f2f5fa;
                  border-radius: 4px;
                  display: inline-block;
                  width: 50%;
                  text-align: center;
                  height: 40px;
                  line-height: 40px;
                  position: relative;
                  &.active {
                      background: #d1e0fe;
                      z-index: 1;
                      color: #1c50fd;
                  }
              }
              &-left {
                  left: 10px;
              }
              &-right {
                  right: 10px;
              }
          }
          .output {
              background: #f8f8f8;
              border-radius: 4px;
              height: calc(100vh - 320px);
              margin: 10px 32px;
              padding: 10px 0;
              overflow: auto;
              line-height: 24px;
              .output-text {
                padding: 6px 16px;
                display: block;
              }
              ::v-deep .vjs-key {
                color: #005cc5;
                width: fit-content;
                text-align: right;
                min-width: 72px;
              }
              ::v-deep .vjs-value-string {
                  color: #666;
              }
              ::v-deep .vjs-tree-node {
                  background: #f8f8f8 !important;
              }
          }
          .tips {
              position: absolute;
              text-align: center;
              left: 50%;
              top: 35%;
              transform: translateX(-50%);
              line-height: 40px;
              font-size: 16px;
              color: #383d47;
              img {
                  display: block;
                }
                ::v-deep .vjs-key {
                  color: #005cc5;
                  width: fit-content;
                  text-align: right;
                  min-width: 72px;
                }
                ::v-deep .vjs-value-string {
                    color: #666;
                }
                ::v-deep .vjs-tree-node {
                    background: #f8f8f8 !important;
                }
            }
            .tips {
                position: absolute;
                text-align: center;
                left: 50%;
                top: 35%;
                transform: translateX(-50%);
                line-height: 40px;
                font-size: 16px;
                color: #383d47;
                img {
                    display: block;
                    margin: 0 auto;
                }
            }
        }

      ::v-deep .el-form {
        //   padding: 0 32px 16px;
        padding:0 32px;
          margin: 0 0 16px 0;
          position: relative;
          height: calc(100vh - 309px);
          overflow: auto;
      }
      .uploadOuter {
          background: #f9fafc;
          border-radius: 4px;
          height: 120px;
          margin: 0 0 10px 0;
          ::v-deep .el-upload-dragger{
            line-height: 24px;
            height: 120px;
            background: #F9FAFC;
            border-radius: 4px;
            .el-icon-upload {
                font-size: 40px;
                color: #1c50fd;
                margin: 10px 0 0px;
                line-height: 50px;
            }
          }
          ::v-deep .el-upload-list {
            position: relative;
            bottom: 20px;
          }
      }
      .params-list {
          &-item {
              padding: 16px 0;
              position: relative;
              &-name {
                  font-size: 16px;
                  color: #383d47;
                  line-height: 20px;
                  font-weight: bold;
              }
              &-type {
                  background: #f2f5fa;
                  border-radius: 4px;
                  font-size: 12px;
                  color: #828894;
                  position: relative;
                  padding: 4px 12px;
                  left: 10px;
                  &.require {
                      &::after {
                          content: "*";
                          position: absolute;
                          left: -12px;
                          top: 3px;
                          color: red;
                          font-size: 20px;
                      }
                  }
              }
              &-desc {
                  font-size: 14px;
                  color: #828894;
                  margin: 10px 0 0 0;
              }
              &-tool {
                  font-size: 20px;
                  position: absolute;
                  right: 16px;
                  top: 16px;
                  color: #828894;
                  i {
                      margin: 0 0 0 10px;
                      cursor: pointer;
                  }
              }
          }
      }
  }
}
</style>