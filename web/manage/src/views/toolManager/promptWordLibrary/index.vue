<!-- 提示词库 -->
<template>
  <div class='promptWordLibrary no-scrollbar'>
    <div v-if="!editPromptVisible" style="display: flex;flex-direction: column;height: calc(100vh - 96px - 24px);">
      <!-- <div class="promptWordLibrary-title">提示词</div> -->
      <div class="promptWordLibrary-htn">
        <el-input style="width: 334px;" v-model="keyValue" :placeholder="$t('pleaseEnterKeyword')"
          suffix-icon="el-icon-search" @input="searchPrompt" @keyup.enter.native="searchPrompt"></el-input>
        <el-button @click="createPromptLibrary" type="primary"><img src="@/assets/images/add-circle-fill.svg" alt="">
          创建提示词</el-button>
      </div>
      <div class="promptWordLibrary-list" v-loading="listLoading">
        <ul v-if="list.length" class="list no-scrollbar" style="width: 100%">
          <li @click="goPromptDetail()" class="list-item" v-for="(item, index) in list" :key="index"
            :style="{ height: type == 1 ? '164px' : '114px' }">

            <div class="preset" v-if="item.ownerType=='official'&&isAdmin">
              预置
            </div>

            <div class="list-item-top" @click="editDetailDialogVisible = true,editItem=item">
              <img v-if="item.icon" class="avatar" :src="item.icon" alt="" />
              <img v-else class="avatar" src="@/assets/images/defaultAvart.svg" alt="" />
              <div class="text">
                <div class="row-name">
                  {{ item.promptTitle }}
                </div>
                <!-- <div class="row-name">
                  {{ item.name }}
                </div> -->
              </div>
            </div>
            <div class="list-item-center">
              <div class="componentDesc" :title="item.remark">
                {{ item.remark || item.componentDesc }}
              </div>
            </div>
            <div class="list-item-bottom" :class="[type != 1 ? 'noBorder flex-start' : '']">
              <div v-if="item.tag && type == 2" class="row" style="padding-left: 60px">
                <div class="row-tag" v-for="(subItem, subIndex) in operateVal(item.tag)" :key="subIndex">
                  {{ subItem }}
                </div>
              </div>
              <div class="tips">{{ $t("creationTime") }}：{{ item.createTime }}</div>
              <div class="edit" v-if="isAdmin || item.ownerType">
              <div class="edit-btn" @click="bianjitishic(item)" style="margin-right:8px;">
                <iconpark-icon color="#494E57" name="edit-box-line" size="15.75" style="margin-right:5px;"></iconpark-icon>
                <span style="font-family: MiSans, MiSans;font-weight: 400;font-size: 14px;color: #494E57;line-height: 20px;text-align: left;font-style: normal;">{{ $t("edit") }}</span>
              </div>
              <div 
              style="width: 32px;
              height: 32px;"
              class="opts-box"
               v-if="isAdmin || item.ownerType"
              :class="{ 'opts-box-active': activeIndexMoreClick === index }">
                <el-dropdown trigger="click" @command="(value) => handleCommand(item,value)" placement="top-end"
                  class="opts-box-dropdown" @visible-change="handleVisibleChange($event, index)"> 
                  <span class="el-dropdown-link">
                    <iconpark-icon name="more-line" size="18" color="#383838"></iconpark-icon>
                  </span>
                  
                  <el-dropdown-menu slot="dropdown" class="opts-box-dropdown-menu">
                    <el-dropdown-item command="presetApp" v-if="item.ownerType!='official'&&isAdmin">
                      <iconpark-icon color="#494E57" name="share-box-line"></iconpark-icon>
                      <span style="color: #494E57">{{ "设为预置" }}</span>
                    </el-dropdown-item>
                    <el-dropdown-item command="presetApp" v-else-if="item.ownerType=='official'&&isAdmin">
                      <iconpark-icon color="#F53F3F" name="share-box-line"></iconpark-icon>
                      <span style="color: #F53F3F">{{ "取消预置" }}</span>
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
            
          </li>
        </ul>
       
        <div v-else class="no-data" style=" margin: auto;">
          <img src="@/assets/images/no-data.png" alt="" />
          <div class="txt1">{{ $t("noData") }}</div>
        </div>
        <div v-if="list.length" class="pagination">
          <div class="total">
            {{ $t("total") }}{{ pageObj.total }}{{ $t("items") }}
          </div>
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page.sync="pageObj.pageNo" :page-sizes="[12, 24, 36, 48]" :page-size="pageObj.pageSize" background
            layout="prev, pager, next, sizes" :total="pageObj.total">
          </el-pagination>
        </div>
      
  
      </div>
    </div>
    <div v-else>
      <div class="create-prompt-head">
        <div style="display: flex;flex-direction: row; align-items: center;">
          <div @click="back()" class="backBtn"><img src="@/assets/images/arrow-go-back-fill.svg" alt=""></div>
          <div class="create-prompt-head-left">
            <div class="voiceImg">
              <img src="@/assets/images/promptEdit.svg" alt="">
            </div>
            <!-- <div class="voiceImg" v-if="type == 1">
                            <img src="@/assets/images/voiceIdentify.svg" alt=""></div>
                        <div class="voiceImg" v-else>
                            <img src="@/assets/images/voiceOutput.svg" alt=""></div> -->
            <div class="text">
              <div>
                <span class="title">{{info.promptTitle}}</span>
                <span @click="xiugaiTishi"><i class="el-icon-edit" style="color:#828894;cursor: pointer;"></i></span>
              </div>
              <div class="descript"><span>{{info.remark}}</span></div>
            </div>
          </div>
        </div>
        <div style="display:flex;align-items: center;">
			<!-- <div
			  style="
			    display: flex;
			    align-items: center;
			    justify-content: center;
			    margin-right: 16px;
			  "
			   v-if="isAdmin"
			>
			  <el-switch
			    style="padding-right: 8px"
			    v-model="info.allVisible"
			    :active-value="1"
			    :inactive-value="0"
			    active-color="#1747E5"
			    inactive-color="#CED4E0"
			  >
			  </el-switch>
			  <span v-if="info.allVisible">设为预置</span>
			  <span v-else>设为预置</span>
			</div> -->
          <el-button type="primary" style="height: 40px; border-radius: 2px" @click="editPromptConfig()">
            <div class="create-prompt-head-button">
              <iconpark-icon name="save-line" color="#fff" size="16"></iconpark-icon><span>{{$t('save')}}</span>
            </div>
          </el-button>
        </div>
      </div>
      <div style="margin-top: 32px;">
        <el-row>
          <el-col :span="12">
            <div style="height: calc(100vh - 180px); border-radius: 2px;border: 1px solid #1747E5;">
              <div class="promptWordHead">
                <div class="word">
                  <div class="promptWord">{{ $t("promptWord") }}</div>
                  <div class="rightSpan">
                    <div class="replaceWord" @click="insertReplace"><img src="@/assets/images/t-box-line.svg"
                        alt="">{{$t("insertReplace")}}</div>
                    <div class="replaceWord" @click="insertVariable"><img src="@/assets/images/variable.svg"
                        alt="">{{$t("insertVariable")}}</div>
                    <div class="aiBtn" @click="aiGenerate">
                      <div class="headImgSpan">
                        <img src="@/assets/images/ai-btn.svg" alt="">
                        {{$t("AIGenerate")}}
                      </div>
                    </div>
                    <!-- @click.stop="aiBoxvisible = false" -->
                    <div class="aiBox" v-show="aiBoxvisible">
                      <div class="textContent">
                        <div class="aiBoxVisibleTextboxClass" ref="aiBoxvisible" v-html="aiBoxVisibleText">

                        </div>
                        <div style="display: flex;justify-content: space-between;height: 42px;line-height: 42px;">
                          <span>
                            <el-button type="primary" style="background: #1747E5;"
                              @click="aiReplaceText()">替换</el-button>
                            <el-button @click="aiBoxvisible = false">关闭</el-button>
                          </span>
                          <span>
                            <img @click="cpoyText(aiBoxVisibleText)" src="@/assets/images/file-copy-line.svg" alt="">
                            <img @click="autoOptimization()" v-show="!autoOptimizationLoading"
                              src="@/assets/images/refresh-line.svg" alt="">
                          </span>
                        </div>
                      </div>
                      <div style="margin: 0 8px 8px;">
                        <el-input v-model="aiValue" v-loading="autoOptimizationLoading" placeholder="输入关键词生成">
                          <img style="width: 17px;height: 17px;" slot="prefix" src="@/assets/images/ai-btn.svg" alt="">
                          <img @click="autoOptimization()" v-show="!autoOptimizationLoading"
                            style="width: 17px;height: 17px;" slot="suffix" src="@/assets/images/send.svg" alt="">
                        </el-input>
                      </div>
                    </div>
                    <!-- @click.stop="replaceBoxvisible = false" -->
                    <div class="replaceBox" v-show="replaceBoxvisible" @mousedown.stop @touchstart.stop
                      :style="{ top: dialogTop + 'px', left: dialogLeft + 'px' }">
                      <el-form :model="replaceform" class="demo-form-inline" label-position="top" size="mini">
                        <el-form-item label="空白引导">
                          <el-row>
                            <el-col :span="24">
                              <el-input size="small" v-model="replaceform.valueOne" @input="updateInsertedNodeValueOne"
                                placeholder="请输入"></el-input>
                            </el-col>

                          </el-row>
                        </el-form-item>
                        <el-form-item label="预设内容">
                          <el-row>
                            <el-col :span="24">
                              <el-input size="small" v-model="replaceform.valueTwo" @input="updateInsertedNodeValue"
                                placeholder="请输入"></el-input>
                            </el-col>
                          </el-row>

                        </el-form-item>
                      </el-form>
                    </div>
                    <div class="variableBox" v-if="variableBoxvisible1" @mousedown.stop @touchstart.stop
                      :style="{ top: dialogTop + 'px', left: dialogLeft + 'px'}">
                      <div class="variableBox-item" v-for="(item,index) in filteredVariableList" :key="index"
                        @click="insertSelectedVariable(item.label)">
                        <div v-if="item.label">{{item.label}}</div>
                      </div>
                      <div class="no-data1" v-if="filteredVariableList&&filteredVariableList.length==0">
                        无匹配的变量
                      </div>
                    </div>
                  </div>
                </div>
                <div class="editable-editor">
                  <div ref="editor" id="editable" contenteditable="true" spellcheck="false" @input="handleInput"
                    @blur="handleBlur" @change="handleInput" @keydown.enter="handleEnter"
                    @keydown.backspace="handleBackspace" @compositionstart="onCompositionStart"
                    @compositionend="onCompositionEnd" @click="handleSlotPlaceholderClick" class="editor">
                  </div>
                </div>

              </div>
              <!-- 输出 -->
              <div class="plugin-bd-first" v-if="descResultList.length">
                <div class="plugin-bd-tit">
                  {{ $t("output") }}
                </div>
                <div class="drawer-content">
                  <div v-for="(panel, index) in descResultList" :key="index" :name="index.toString()">
                    <div class="params-list">
                      <div class="params-list-item">
                        <div class="params-list-item-name require">
                          {{ panel.name_en }}
                          <!-- <span style="color: red" v-if="panel.required">*</span> -->
                          <span class="params-list-item-type">{{ panel.feild_type }}</span>
                        </div>
                        <div class="params-list-item-desc">
                          {{ panel.desc }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <!-- 变量 -->
            <div class="plugin-bd-first plugin-flex">
              <div class="plugin-bd-tit">
                包含{{ $t("variable") }}
                <!-- <span @click="addPanel"><em>+</em>{{ $t("addParam") }}</span> -->
              </div>
              <div>
                <el-button @click="addVariable" class="plugin-btn-color" type="text" icon="el-icon-plus">
                  {{ $t("addParam") }}
                </el-button>
              </div>
            </div>

            <div class="variable-table">
              <el-table :data="form.variables" style="width: 100%" :cell-style="hearderCellStyle"
                :header-cell-style="hearderCellStyle" :header-row-style="headerRowStyle">
                <el-table-column prop="value" label="变量名">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.label" size="small" placeholder="请输入变量名"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="value" label="变量值">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.value" size="small" placeholder="请输入值"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="type" label="变量类型" width="120">
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.type" size="small" placeholder="请选择类型">
                      <el-option label="String" value="string"></el-option>
                      <el-option label="Number" value="number"></el-option>
                      <el-option label="Boolean" value="boolean"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注">
                  <template slot-scope="scope">
                    <el-input size="small" v-model="scope.row.remark" placeholder="请输入备注"></el-input>
                  </template>
                </el-table-column>
                <el-table-column prop="required" label="必填" width="50" align="center">
                  <template slot-scope="scope">
                    <el-checkbox size="small" v-model="scope.row.required"></el-checkbox>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="50" align="center">
                  <template slot-scope="scope">
                    <i class="el-icon-delete" @click="deleteVariable(scope.$index)"></i>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <el-dialog :title="editFlag ? '编辑提示词' : $t('createPrompt')" :visible.sync="dialogVisible" width="30%" :before-close="handleClose">
      <el-form :model="formData" ref="ruleForm" class="demo-form-inline" :rules="rule">
        <el-form-item :label="$t('promptName')" prop="promptTitle">
          <el-input v-model="formData.promptTitle" :placeholder="$t('pleaseInput') + $t('promptName')"></el-input>
        </el-form-item>
        <el-form-item :label="$t('promptDes')">
          <el-input type="textarea" maxlength="200" v-model="formData.remark" :placeholder="$t('pleaseInput') + $t('promptDes')"></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <el-button plain @click="handleClose('ruleForm')" :loading="addLoading">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="onSubmit('ruleForm')" :loading="addLoading">{{$t('confirm')}}</el-button>
      </div>
    </el-dialog>
   
  </div>
</template>

<script>
  //这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
  //例如：import 《组件名称》 from '《组件路径》';
  import { promptConfigAddPromptConfig, promptConfigGetPromptConfigList, promptConfigDeletePromptConfig } from '@/api/workflow';
  import { fetchEventSource } from '@microsoft/fetch-event-source';
  import {promptPreset} from "@/api/app"
  import md5 from "js-md5";

  export default {
    //import引入的组件需要注入到对象中才能使用
    data ()
    {
      //这里存放数据
      return {
        activeIndexMoreClick:null,
        editorRef: null,
        isComposing: false,
        savedSelection: null,
        savaWindows: null,
        promptTitleHistory: '',
        editFlag: false,
        autoOptimizationLoading: false,
        aiBoxVisibleText: '',
        dialogTop: 0,
        dialogLeft: 0,
        isEditBlockDialogVisible: false,
        placeholderText: '请输入替换词',
        presetText: '',
        insertedNode: null,
        pageObj: {
          pageNo: 1,
          pageSize: 24,
          total: 0,
        },
        listLoading: false,
        replaceBoxvisiblebiaoti: false,
        lastSelection: null,
        list: [],
        keyValue: "",
        type: 1,
        dialogVisible: false,
        addLoading: false,
        form: {
          variables: [
            
          ],
        },
        formData: {
          promptTitle: "",
          remark: ""
        },
        editPromptVisible: false,
        info: {
          promptTitle: '',
          remark: '',
		  allVisible:null,
        },
        appForm: {
          modelInstruction: "",
          modelId: ""
        },
        variableList: [],
        parameterPanel: false,
        panelList: {},
        descResultList: [],
        rules: {
          name_en: [
            {
              required: true,
              message: this.$t("enterVariableName"),
              trigger: "blur",
            },
          ],
          feild_desc: [
            { required: true, message: this.$t("enterDescription"), trigger: "blur" },
          ],
          feild_type: [
            {
              required: true,
              message: this.$t("selectFieldType"),
              trigger: "change",
            },
          ],
          isRequired: [
            {
              required: true,
              message: this.$t("pleaseSelect"),
              trigger: "change",
            },
          ],
          max_length: [
            {
              required: true,
              message: this.$t("pleaseEnter"),
              trigger: "blur",
            },
          ],
        },
        editIndex: "",
        aiValue: "",
        aiBoxvisible: false,
        variableBoxvisible1: false,
        replaceBoxvisible: false,
        replaceform: {
          valueOne: "请插入空白替换词",
          valueTwo: "",
        },
        morenText: "请插入空白替换词",
        textInput: '',
        editableText: '',
        rule: {
          promptTitle: [
            { required: true, message: '请输入提示词名称', trigger: 'change' }
          ],
        },
        inVariable: false,
        isInSlotValue: false,
        currentSlotValue: "",
        headerRowStyle: {
          color: '#828894',
          fontSize: '12px',
          height: '16px',
          borderColor: 'transparent'
        },
        hearderCellStyle: {
          borderColor: 'transparent',
        },
        isHtml: false,
      };
    },
    //监听属性 类似于data概念
    computed: {
      tenantId ()
      {
        const tenantId = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.tenantId : "";
        return tenantId;
      },
	  isAdmin ()
	  {
	    const userType = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.powerType : "";
	    if(userType=='0'){
			return true
		}else{
			return false
		}
	  },
      filteredVariableList ()
      {
        if (!this.currentSlotValue) {
          return this.form.variables.filter(variable =>
            variable.label != ''
          );
        }
        const searchTerm = this.currentSlotValue.toLowerCase();
        return this.form.variables.filter(variable =>
          variable.label && variable.label.toLowerCase().includes(searchTerm)
        );
      }
    },
    //方法集合
    methods: {
      handleVisibleChange(val, index) {
      if(val){
        this.activeIndexMoreClick = index;
      }else {
        this.activeIndexMoreClick = null;
      }
      
    },
      deleteVariable (index)
      {
        this.form.variables.splice(index, 1);
      },
      addVariable ()
      {
        this.form.variables.push({
          label: "",
          value: "",
          type: "string",
          remark: "",
          required: false,
        });
      },
      cpoyText (content)
      {
        this.exeCommandCopyText(this.aiBoxVisibleText)
        this.$message({
          message: '复制成功',
          type: 'success',
        })
      },
      exeCommandCopyText (text)
      {
        // 创建一个临时的 textarea 元素
        const tempTextarea = document.createElement('textarea');
        // 将内容及其样式作为 HTML 赋值给 textarea
        tempTextarea.value = text;
        document.body.appendChild(tempTextarea);
        // 选中 textarea 中的内容
        tempTextarea.select();
        // 执行复制操作
        document.execCommand('copy');
        // 移除临时 textarea
        document.body.removeChild(tempTextarea);
      },
      aiReplaceText ()
      {
        this.$refs.editor.innerHTML = this.aiBoxVisibleText;
        this.updateText()
        this.aiBoxvisible = false;
      },
      autoOptimization ()
      {
        let that = this;
        that.autoOptimizationLoading = true;
        const params = {
          topic: this.info.promptTitle,
          description: this.aiValue,
          clientId: Math.floor(Math.random() * 10000000000),
        }
        let ctrlAbout = new AbortController();
        let manageAccessToken = sessionStorage.getItem("manageAccessToken");
        let timer = new Date().getTime();
        fetchEventSource(
          `${process.env.VUE_APP_BASE_API}/applicationInfo/generatePrompt`,
          {
            method: "POST",
            headers: {
              Authorization: `Bearer ${manageAccessToken}`,
              "Content-Type": "application/json",
              timestamp: timer,
              cipher: md5(timer + `/applicationInfo/generatePrompt${JSON.stringify(params)}xxxxxxxxxxx`),
            },
            signal: ctrlAbout.signal,
            body: JSON.stringify(params),
            openWhenHidden: true, //默认为false，监听visibilitychange，当页面不可见时关闭连接，当页面重新可见时重新打开连接。
            async onopen (response)
            {
            },
            onmessage (event)
            {
              let data = event.data;
              if (data) {
                let promptData = JSON.parse(data)
                that.aiBoxVisibleText = that.formatTextToHtml(promptData.prompt);
              }
            },
            onerror (err)
            {
              that.autoOptimizationLoading = false;
            },
            onclose ()
            {
              // 服务关闭
              that.autoOptimizationLoading = false;
              ctrlAbout.abort();
            },
          }
        );
      },
      handleCurrentChange (page)
      {
        this.pageObj.pageNo = page;
        this.searchPrompt();
      },
      handleSizeChange (size)
      {
        this.pageObj.pageSize = size;
        this.searchPrompt();
      },
      guanBiChaRu ()
      {
        this.replaceBoxvisible = false
      },
      searchPrompt ()
      {
        this.listLoading = true
        promptConfigGetPromptConfigList({
          promptTitle: this.keyValue,
          pageNo: this.pageObj.pageNo,
          pageSize: this.pageObj.pageSize,
          ownerType:"personalGrantTenant"
        }).then(res =>
        {
          if (res.code == '000000') {
            this.list = res.data.records
            this.pageObj.total = res.data.totalRow
            if (this.editPromptVisible) {
              const info = this.list.find(item => item.promptTitle == this.info.promptTitle);
              this.info = JSON.parse(JSON.stringify(info));
            }
          }
          this.listLoading = false
        })
      },
      createPromptLibrary ()
      {
        this.addLoading = false;
        this.dialogVisible = true
      },
      handleClose (formName)
      {
        this.dialogVisible = false
        this.editFlag = false
        this.$refs[formName].resetFields();
      },
      onSubmit (formName)
      {
        this.$refs[formName].validate((valid) =>
        {
          if (valid) {
            this.addLoading = true;
            promptConfigAddPromptConfig({
              promptTitle: this.formData.promptTitle,
              remark: this.formData.remark,
              params: this.form.variables,
              tenantId: this.tenantId,
              id: this.info.id,
            }).then(res =>
            {
              if (res.code == '000000') {
                this.$message.success('操作成功')
                this.searchPrompt()
                this.info = { ...this.formData }
                this.formData.promptTitle = ''
                this.formData.remark = ''
                this.form.variables = []
                this.editPromptVisible = true
                this.dialogVisible = false
              } else {
                this.$message.warning(res.msg)
              }
            })
            this.addLoading = false;
          } else {
            return false;
          }
        });

      },
      getPromptPresetApi(data){
        promptPreset({id:data.id}).then(res=>{
          if(res.code=="000000"){
            this.searchPrompt()
          }else{
            this.$message({
              type:"error",
              message:res.msg
            })
          }
        })
      },
      handleCommand (item, value)
      {
        if (value == 'editeApp') {
          this.bianjitishic(item)
        } else if (value == 'deleteApp') {
          this.deleteApp(item)
        }else if(value == 'presetApp'){
          this.getPromptPresetApi(item)
        }

      },
      bianjitishic (item)
      {
        this.editPromptVisible = true
        this.form.variables = item.params ? item.params : []
        this.info = item;
        this.editableText = item.content;
        this.handleInput = this.debounce(this.handleInput, 200);
        setTimeout(() =>
        {
          this.editorRef = this.$refs.editor;
          this.editorEvenLtenser();
          this.$refs.editor.innerHTML = item.content;
          document.addEventListener('mousedown', this.handleOutsideClick);
          document.addEventListener('touchstart', this.handleOutsideClick);
        }, 500);
      },
      deleteApp (item)
      {
        this.$confirm("请确认是否删除", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          confirmButtonClass: "confirm-ok",
          cancelButtonClass: "confirm-cancel",
        }).then(async () =>
        {
          let params = [item.id + ''];
          promptConfigDeletePromptConfig(params).then(res =>
          {
            if (res.code == '000000') {
              this.$message.success('删除成功')
              this.searchPrompt()
            }
          })
        });
      },
      back ()
      {
        this.editPromptVisible = false
      },
      goPromptDetail ()
      {

      },
      addPanel ()
      {
        this.parameterPanel = true;
        this.panelList = {};
      },
      addParameterPanel (item, type, index)
      {
        let form = 'formRefs' + index
        let list = type == 1 ? this.$refs[form][0] : this.$refs.formRefsList;
        list.validate((valid) =>
        {
          if (valid) {
            const newPanel = {
              name_en: item.name_en,
              feild_type: item.feild_type,
              feild_desc: item.feild_desc,
              max_length: item.max_length,
              isRequired: item.isRequired,
            };
            if (this.variableList && this.variableList.length > 0) {
              if (type == 2) {
                this.variableList.push(newPanel);
                this.variableListOver = JSON.parse(JSON.stringify(this.variableList));
              } else {
                this.variableListOver = JSON.parse(JSON.stringify(this.variableList));
              }
            } else {
              this.variableList = [newPanel];
              this.variableListOver = JSON.parse(JSON.stringify(this.variableList));
            }
            this.$nextTick(() =>
            {
              this.$forceUpdate()
            })
            type == 1 ? (this.editIndex = "") : (this.parameterPanel = false);
          }
        });
      },
      closeParameterPanel (type)
      {
        type == 1 ? (this.editIndex = "") : (this.parameterPanel = false);
      },
      insertReplace ()
      {
        if (!this.savedSelection) {
          this.$message.warning('请先点击插入位置');
          return;
        }
		console.log('执行到这')
        const selection = window.getSelection();
        const range = this.savedSelection;
        this.editorRef.focus();

        const insertSpan = document.createElement('span');
        insertSpan.classList.add('slot-placeholder');
        insertSpan.textContent = this.morenText;
        insertSpan.setAttribute('contenteditable', 'false');
        insertSpan.setAttribute('data-set', 1);

        let parent = range.startContainer.parentNode;
        while (parent) {
          if (parent.classList && parent.classList.contains('slot-placeholder')) {
            return;
          }
          parent = parent.parentNode;
        }


        let lineParent = range.startContainer;
        while (lineParent && !lineParent.classList?.contains('line') && !lineParent.classList?.contains('green')) {
          lineParent = lineParent.parentNode;
        }
        console.log('lineParent',lineParent)
        if (!lineParent) {

          lineParent = this.editorRef.querySelector('.line');
          if (!lineParent) {
            const newLineDiv = document.createElement('div');
            newLineDiv.classList.add('line');
            this.editorRef.appendChild(newLineDiv);
            lineParent = newLineDiv;
          }
        }
        if (lineParent.textContent.trim() === '') {
          lineParent.appendChild(insertSpan);
        } else {
          if (range.startContainer.nodeType === Node.TEXT_NODE) {
            const textNode = range.startContainer;
            const offset = range.startOffset;
            const beforeText = textNode.textContent.slice(0, offset);
            const afterText = textNode.textContent.slice(offset);

            const beforeTextNode = document.createTextNode(beforeText);
            const afterTextNode = document.createTextNode(afterText);

            lineParent.insertBefore(beforeTextNode, textNode);
            lineParent.insertBefore(insertSpan, textNode);
            lineParent.insertBefore(afterTextNode, insertSpan.nextSibling);
            lineParent.removeChild(textNode);
          } else {
            lineParent.insertBefore(insertSpan, range.startContainer.nextSibling);
          }
        }
        const newRange = document.createRange();
        newRange.setStartAfter(insertSpan);
        newRange.setEndAfter(insertSpan);
        selection.removeAllRanges();
        selection.addRange(newRange);

        this.insertedNode = insertSpan;
        this.replaceBoxvisible = true;
        this.getRect(insertSpan)
      },
      handleSlotPlaceholderClick (event)
      {
        this.saveSelection();
        const target = event.target;
        if (target.classList.contains('slot-placeholder')) {
          this.insertedNode = target;
          const dataSet = target.getAttribute("data-set");
          if (dataSet == 1) {
            this.replaceform.valueOne = this.morenText;
          } else {
            this.replaceform.valueOne = this.morenText;
            this.replaceform.valueTwo = target.textContent;
          }
          this.replaceBoxvisible = true;
          this.getRect(this.insertedNode);
        } else if (target.classList && target.classList.contains('slot-value')) {
          this.insertedNode = target;
          this.variableBoxvisible1 = true;
          this.getRect(this.insertedNode);
        }
      },
      getRect (none)
      {
        const rect = none.getBoundingClientRect();
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        const scrollLeft = window.pageXOffset || document.documentElement.scrollLeft;
        const top = rect.top + scrollTop;
        const left = rect.left + scrollLeft;
        this.dialogTop = top + 28;
        this.dialogLeft = left - 18;
      },
      insertVariable ()
      {
        if (!this.savedSelection) {
          this.$message.warning('请先点击插入位置');
          return;
        }
        const selection = window.getSelection();
        const range = this.savedSelection;
        this.editorRef.focus();
        const insertSpan = document.createElement('span');
        insertSpan.classList.add('slot-value');
        const startText = document.createTextNode('${');
        const endText = document.createTextNode('}');

        insertSpan.appendChild(startText);
        insertSpan.appendChild(endText);
        this.isInSlotValue = false;
        let parent = range.startContainer.parentNode;
        while (parent) {
          if (parent.classList && parent.classList.contains('slot-value')) {
            this.isInSlotValue = true;
            break;
          }
          parent = parent.parentNode;
        }
        let lineParent = range.startContainer;
        while (lineParent && !lineParent.classList?.contains('line')) {
          lineParent = lineParent.parentNode;
        }

        if (!lineParent) {
          lineParent = this.editorRef.querySelector('.line');
          if (!lineParent) {
            const newLineDiv = document.createElement('div');
            newLineDiv.classList.add('line');
            this.editorRef.appendChild(newLineDiv);
            lineParent = newLineDiv;
          }
        }
        if (lineParent.textContent.trim() === '') {
          lineParent.appendChild(insertSpan);
        } else {
          if (this.isInSlotValue) {
            const rangeOffset = range.startOffset;
            const slotValueLength = parent.textContent.length;
            if (rangeOffset < slotValueLength / 2) {
              parent.parentNode.insertBefore(insertSpan, parent);
            } else {
              parent.parentNode.insertBefore(insertSpan, parent.nextSibling);
            }
          } else if (range.startContainer.nodeType === Node.TEXT_NODE) {
            const textNode = range.startContainer;
            const offset = range.startOffset;
            const beforeText = textNode.textContent.slice(0, offset);
            const afterText = textNode.textContent.slice(offset);

            const beforeTextNode = document.createTextNode(beforeText);
            const afterTextNode = document.createTextNode(afterText);

            lineParent.insertBefore(beforeTextNode, textNode);
            lineParent.insertBefore(insertSpan, textNode);
            lineParent.insertBefore(afterTextNode, insertSpan.nextSibling);
            lineParent.removeChild(textNode);
          } else {
            lineParent.insertBefore(insertSpan, range.startContainer.nextSibling);
          }
        }
        const newRange = document.createRange();
        newRange.setStartAfter(startText);
        newRange.setEndAfter(startText);
        selection.removeAllRanges();
        selection.addRange(newRange);
        this.currentSlotValue = '';
        this.insertedNode = insertSpan;
        this.variableBoxvisible1 = true;
        this.getRect(insertSpan)
      },
      aiGenerate ()
      {
        this.aiBoxvisible = true
      },
      updateText ()
      {
        this.editableText = this.$refs.editor.innerHTML;
      },
      saveSelection ()
      {
        const selection = window.getSelection();
        this.savaWindows = selection;
        if (selection.rangeCount > 0) {
          const range = selection.getRangeAt(0);
          const preSelectionRange = range.cloneRange();
          preSelectionRange.selectNodeContents(this.editorRef);
          preSelectionRange.setEnd(range.startContainer, range.startOffset);
          const start = preSelectionRange.toString().length;
          this.savedSelection = selection.getRangeAt(0);
          return {
            start: start,
            end: start + range.toString().length
          };
        }
        return null;
      },
      editorEvenLtenser() {
          this.$refs.editor.addEventListener("paste", (event) => {
            event.preventDefault(); // 阻止默认粘贴行为
            const clipboardData = event.clipboardData || window.clipboardData;
            if (clipboardData) {
              const plainText = clipboardData.getData('text/plain'); // 获取纯文本
              const selection = window.getSelection();
              if (selection.rangeCount > 0) {
                const range = selection.getRangeAt(0);
                
                // 直接插入纯文本，避免HTML处理可能带来的问题
                const textNode = document.createTextNode(plainText);
                range.insertNode(textNode);
                
                // 将光标设置到插入内容的末尾
                const newRange = document.createRange();
                newRange.setStartAfter(textNode);
                newRange.setEndAfter(textNode);
                selection.removeAllRanges();
                selection.addRange(newRange);
              }
              setTimeout(() => {
                this.handleInput();
              }, 0);
            }
          })
        },

      updateInsertedNodeValueOne (val)
      {
        if (val) {
          if (this.replaceform.valueTwo) {
            this.insertedNode.textContent = this.replaceform.valueTwo;
            this.setAttributeDom(this.insertedNode, 2)
          } else {
            this.insertedNode.textContent = this.replaceform.valueOne;
            this.setAttributeDom(this.insertedNode, 1)
          }
        } else {
          if (this.replaceform.valueTwo) {
            this.insertedNode.textContent = this.replaceform.valueTwo;
            this.setAttributeDom(this.insertedNode, 2)
          } else {
            this.insertedNode.textContent = this.morenText;
            this.setAttributeDom(this.insertedNode, 1)
          }
        }
      },
      updateInsertedNodeValue (val)
      {
        if (this.insertedNode && val) {
          this.insertedNode.textContent = val;
          this.setAttributeDom(this.insertedNode, 2)
        } else {
          this.insertedNode.textContent = this.replaceform.valueOne ? this.replaceform.valueOne : this.morenText;
          this.setAttributeDom(this.insertedNode, 1)
        }
      },
      setAttributeDom (dom, number)
      {
        dom.setAttribute('data-set', number);
        dom.setAttribute('class', number == 1 ? 'slot-placeholder' : 'slot-placeholder slot-active');
      },
      handleOutsideClick (event)
      {
        const dialog = this.$el.querySelector('.replaceBox');
        const dialog1 = this.$el.querySelector('.variableBox');
        if (this.replaceBoxvisible && !dialog.contains(event.target)) {
          this.restoreSelection(this.savedSelection);
          if (!this.replaceform.valueTwo) {
            this.insertedNode.textContent = this.replaceform.valueOne ? this.replaceform.valueOne : this.morenText;
          }
          this.replaceform.valueOne = '';
          this.replaceform.valueTwo = "";
          this.replaceBoxvisible = false;
        } else if (this.variableBoxvisible1 && !dialog1.contains(event.target)) {
          this.restoreSelection(this.savedSelection);
          this.variableBoxvisible1 = false;
        }
      },
      insertSelectedVariable (variable)
      {
        if (this.insertedNode) {
          this.insertedNode.textContent = '${' + `${variable}` + '}';
          this.variableBoxvisible1 = false;
        }
      },
      restoreSelection (selection)
      {
        if (selection) {
          let charIndex = 0;
          const range = document.createRange();
          range.setStart(this.editorRef, 0);
          range.collapse(true);
          const nodeStack = [this.editorRef];
          let node;
          let foundStart = false;
          let stop = false;

          while (!stop && (node = nodeStack.pop())) {
            if (node.nodeType === Node.TEXT_NODE) {
              const nextCharIndex = charIndex + node.length;
              if (!foundStart && selection.start >= charIndex && selection.start <= nextCharIndex) {
                range.setStart(node, selection.start - charIndex);
                foundStart = true;
              }
              if (foundStart && selection.end >= charIndex && selection.end <= nextCharIndex) {
                range.setEnd(node, selection.end - charIndex);
                stop = true;
              }
              charIndex = nextCharIndex;
            } else {
              let i = node.childNodes.length;
              while (i--) {
                nodeStack.push(node.childNodes[i]);
              }
            }
          }

          const sel = window.getSelection();
          sel.removeAllRanges();
          sel.addRange(range);
        }
      },
      handleBlur ()
      {
        console.log(this.$refs.editor.innerHTML)
      },
      handleInput ()
      {
        this.currentSlotValue = '';
        if (this.isComposing) return;
        const savedSelection = this.saveSelection();
        const children = Array.from(this.editorRef.children);
        if (children.length === 0) {
          const newLineDiv = document.createElement('div');
          newLineDiv.classList.add('line');
          this.editorRef.appendChild(newLineDiv);
          return;
        }
        let newHtml = '';
        children.forEach((child) =>
        {
          if (child.classList.contains('line')) {
            let lineText = this.processTextNodes(child.childNodes);
            let formattedLine;
            formattedLine = this.formatHashText(lineText);
            newHtml += `<div class="line">${formattedLine}</div>`;
          } else {
            newHtml += child.outerHTML;
          }
        });
        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = newHtml;
        while (this.editorRef.firstChild) {
          this.editorRef.removeChild(this.editorRef.firstChild);
        }
        while (tempDiv.firstChild) {
          this.editorRef.appendChild(tempDiv.firstChild);
        }

        this.restoreSelection(savedSelection);
        this.checkCursorInSlotValue();
      },
      processTextNodes (nodes)
      {
        let result = '';
        nodes.forEach((node) =>
        {
          if (node.nodeType === Node.TEXT_NODE) {
            result += this.wrapSlotValues(node.textContent);
          } else if (node.nodeType === Node.ELEMENT_NODE) {
            if (node.classList.contains('slot-placeholder')) {
              result += node.outerHTML;
            } else {
              result += this.processTextNodes(node.childNodes);
            }
          }
        });
        return result;
      },
      wrapSlotValues (text)
      {
        let output = '';
        let pos = 0;
        while (pos < text.length) {
          const startIndex = text.indexOf('${', pos);
          if (startIndex === -1) {
            output += text.slice(pos);
            break;
          }
          output += text.slice(pos, startIndex);
          const endIndex = text.indexOf('}', startIndex + 2);
          if (endIndex === -1) {
            output += text.slice(startIndex);
            break;
          }
          output += `<span class="slot-value">${text.slice(startIndex, endIndex + 1)}</span>`;
          pos = endIndex + 1;
        }
        return output;
      },
      formatHashText (lineText)
      {
        if (lineText.trim().startsWith('#')) {
          return `<span class="green">${lineText}</span>`;
        }
        return lineText;
      },
      checkCursorInSlotValue ()
      {
        const selection = window.getSelection();
        if (selection.rangeCount > 0) {
          const range = selection.getRangeAt(0);
          let node = range.startContainer;
          while (node) {
            if (node.classList && node.classList.contains('slot-value')) {
              this.getRect(node)
              this.insertedNode = node;
              this.variableBoxvisible1 = true;
              const slotValueContent = node.textContent.replace('${', '').replace('}', '');
              this.currentSlotValue = slotValueContent;
              break;
            } else {
              this.variableBoxvisible1 = false;
            }
            node = node.parentNode;
          }
        }
      },

      handleEnter (event)
      {
        event.preventDefault();
        const selection = window.getSelection();
        const range = selection.getRangeAt(0);

        let currentLine = range.startContainer;
        while (currentLine && !currentLine.classList?.contains('line')) {
          currentLine = currentLine.parentNode;
        }


        const newLine = document.createElement('div');
        newLine.classList.add('line');

        if (currentLine) {

          const parent = currentLine.parentNode;
          const index = Array.from(parent.children).indexOf(currentLine);
          parent.insertBefore(newLine, parent.children[index + 1]);
        } else {

          this.editorRef.appendChild(newLine);
        }

        const newTextNode = document.createTextNode('');
        newLine.appendChild(newTextNode);


        const newRange = document.createRange();
        newRange.setStart(newTextNode, 0);
        newRange.setEnd(newTextNode, 0);
        selection.removeAllRanges();
        selection.addRange(newRange);

      },

      handleBackspace (event)
      {
        const selection = window.getSelection();
        const range = selection.getRangeAt(0);
        if (range.startContainer.nodeType === Node.TEXT_NODE) {
          const parentNode = range.startContainer.parentNode;
          if (parentNode.classList && parentNode.classList.contains('slot-placeholder')) {
            if (range.startOffset === range.startContainer.length) {
              event.preventDefault();
              const grandParentNode = parentNode.parentNode;
              grandParentNode.removeChild(parentNode);

              const brNodes = grandParentNode.querySelectorAll('br');
              brNodes.forEach((br) =>
              {
                grandParentNode.removeChild(br);
              });

              let newRange;
              const nextSibling = grandParentNode.nextSibling;
              if (nextSibling) {
                if (nextSibling.nodeType === Node.TEXT_NODE) {
                  newRange = document.createRange();
                  newRange.setStart(nextSibling, 0);
                  newRange.setEnd(nextSibling, 0);
                } else if (nextSibling.firstChild && nextSibling.firstChild.nodeType === Node.TEXT_NODE) {
                  newRange = document.createRange();
                  newRange.setStart(nextSibling.firstChild, 0);
                  newRange.setEnd(nextSibling.firstChild, 0);
                } else {
                  newRange = document.createRange();
                  newRange.setStartBefore(nextSibling);
                  newRange.setEndBefore(nextSibling);
                }
              } else {
                const prevSibling = grandParentNode.previousSibling;
                if (prevSibling) {
                  if (prevSibling.nodeType === Node.TEXT_NODE) {
                    newRange = document.createRange();
                    newRange.setStart(prevSibling, prevSibling.length);
                    newRange.setEnd(prevSibling, prevSibling.length);
                  } else if (prevSibling.lastChild && prevSibling.lastChild.nodeType === Node.TEXT_NODE) {
                    newRange = document.createRange();
                    newRange.setStart(prevSibling.lastChild, prevSibling.lastChild.length);
                    newRange.setEnd(prevSibling.lastChild, prevSibling.lastChild.length);
                  } else {
                    newRange = document.createRange();
                    newRange.setStartAfter(prevSibling);
                    newRange.setEndAfter(prevSibling);
                  }
                } else {
                  const firstChild = this.editorRef.firstChild;
                  if (firstChild.nodeType === Node.TEXT_NODE) {
                    newRange = document.createRange();
                    newRange.setStart(firstChild, 0);
                    newRange.setEnd(firstChild, 0);
                  } else if (firstChild.firstChild && firstChild.firstChild.nodeType === Node.TEXT_NODE) {
                    newRange = document.createRange();
                    newRange.setStart(firstChild.firstChild, 0);
                    newRange.setEnd(firstChild.firstChild, 0);
                  } else {
                    newRange = document.createRange();
                    newRange.setStartBefore(firstChild);
                    newRange.setEndBefore(firstChild);
                  }
                }
              }

              selection.removeAllRanges();
              selection.addRange(newRange);

              setTimeout(() =>
              {
                this.handleInput();
              }, 0);
            }
          }
        }
      },

      onCompositionStart ()
      {
        this.isComposing = true;
      },

      onCompositionEnd ()
      {
        this.isComposing = false;
        this.handleInput();
      },

      debounce (func, delay)
      {
        let timer = null;
        return function ()
        {
          const context = this;
          const args = arguments;
          clearTimeout(timer);
          timer = setTimeout(() =>
          {
            func.apply(context, args);
          }, delay);
        };
      },
      handleBlur ()
      {
        this.saveSelection();
      },
      insertText ()
      {
        const editableDiv = this.$refs.editableDiv;


        // 创建一个新的文本节点
        const textNode = document.createTextNode(this.replaceform.valueTwo);

        // 插入文本节点到选中的位置
        this.lastSelection.insertNode(textNode);

        // 清空输入框
        this.replaceform.valueOne = '';
        this.replaceform.valueTwo = '';
        this.replaceBoxvisible = false;
      },
      editPromptConfig ()
      {
        /*
            关键变量列表：
            this.editableText  提示词内容
        */

        let pramies = {
          id: this.info.id,
          promptId: this.info.promptId,
          promptTitle: this.info.promptTitle,
          remark: this.info.remark,
          params: this.form.variables,
          content: this.$refs.editor.innerHTML,
          tenantId: this.tenantId,
		  allVisible:this.info.allVisible,
        }
        promptConfigAddPromptConfig(pramies).then(res =>
        {
          if (res.code == '000000') {
            this.$message.success('操作成功')
            this.editPromptVisible = false
            this.searchPrompt()
          } else {
            this.$message.warning(res.msg)
          }
        })
      },
      xiugaiTishi ()
      {
        this.editFlag = true
        this.dialogVisible = true
        this.formData = {
          promptTitle: this.info.promptTitle,
          remark: this.info.remark,
        }
      },
      saveTtitle ()
      {
        promptConfigAddPromptConfig({
          promptTitle: this.formData.promptTitle,
          remark: this.formData.remark,
          id: this.info.id
        }).then(res =>
        {
          if (res.code == '000000') {
            this.replaceBoxvisiblebiaoti = false
            this.$message.success('操作成功')
            this.searchPrompt()
            this.formData.promptTitle = ''
            this.formData.remark = ''
          } else {
            this.$message.error(res.msg)
            this.replaceBoxvisiblebiaoti = false
          }
        })
      },
      formatTextToHtml (text)
      {
        const lines = text.split('\n');
        if (lines.indexOf(`<div class="line"`) > -1) return text
        let html = '';
        lines.forEach((line) =>
        {
          let formattedLine;
          if (line.trim().startsWith('#')) {
            formattedLine = `<span class="green">${line}</span>`;
          } else {
            formattedLine = line;
          }
          html += `<div class="line">${formattedLine}</div>`;
        });
        return html;
      },
    },


    //生命周期 - 创建完成（可以访问当前this实例）
    created ()
    {

    },
    //生命周期 - 挂载完成（可以访问DOM元素）
    mounted ()
    {
      this.searchPrompt()
    },
    beforeCreate () { }, //生命周期 - 创建之前
    beforeMount () { }, //生命周期 - 挂载之前
    beforeUpdate () { }, //生命周期 - 更新之前
    updated () { }, //生命周期 - 更新之后
    beforeDestroy ()
    {
      document.removeEventListener('mousedown', this.handleOutsideClick);
      document.removeEventListener('touchstart', this.handleOutsideClick);
    },
    destroyed () { }, //生命周期 - 销毁完成
    activated () { }, //如果页面有keep-alive缓存功能，这个函数会触发
  }
</script>
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>
<style scoped lang="scss">
::v-deep .el-popper[x-placement^="bottom"] .popper__arrow {
  display: none;
}
  ::v-deep .el-table--enable-row-hover .el-table__body tr:hover>td.el-table__cell {
    background-color: transparent;
  }

  ::v-deep .slot-placeholder {
    background-color: rgba(186, 192, 255, .2);
    color: rgba(148, 152, 247, .7);
    word-break: break-all;
    font-size: 14px;
    display: inline-block;
    border-radius: 4px;
    padding: 0 3px;
    margin-right: 5px;
    cursor: pointer;
  }

  ::v-deep .slot-active {
    color: #4e40e5 !important;
    font-weight: 500;
  }

  ::v-deep .el-table .el-table__cell {
    padding: 0;
  }

  ::v-deep .el-table::before {
    height: 0;
  }

  ::v-deep {
    .el-table--enable-row-transition .el-table__body td.el-table__cell .cell {
      height: 40px;
      display: flex;
      align-items: center;
    }
  }

  .editable-editor {
    height: calc(100vh - 230px);
    overflow: auto;
    padding: 10px 16px 0 16px;
    color: #828894;
  }

  .editor {
    outline: none;
    min-height: 100px;
  }

  ::v-deep .line {
    display: block;
    min-height: 20px;
    line-height: 24px;
    font-size: 14px;
  }

  ::v-deep .green {
    color: #603ECA;
    display: inline-block;
  }

  ::v-deep br {
    display: none;
  }

  .plugin-flex {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .editable-div {
    outline: none;
  }

  .editable-box {
    height: calc(100vh - 230px);
    overflow: auto;
    padding: 0 16px;

    .editable-div {
      outline: none;
    }
  }

  .aiBoxVisibleTextboxClass {
    height: 340px;
    overflow-y: auto;
  }

  ::v-deep .slot-value {
    color: #6295FC !important;
    padding: 0 3px;
  }

  // 弹框
  .replaceBox {
    position: fixed;
    z-index: 99;
    padding: 16px;
    box-sizing: border-box;
    width: 354px;
    height: 164px;
    background: #FFFFFF;
    box-shadow: 0px 0px 12px 0px rgba(26, 36, 70, 0.08);
    border-radius: 8px;

    .el-form-item {
      margin-bottom: 0;
      line-height: 24px;
      position: relative;
    }

    ::v-deep(.el-form-item__label) {
      padding-bottom: 4px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 14px;
      color: #494E57;
      line-height: 24px !important;
    }
  }

  .replaceBoxBiaotiKuang {
    border: 1px solid #e1e4eb;
    top: 50px;
    left: 400px;
  }

  // 关闭按钮
  .replaceBoxGuanbi {
    position: absolute;
    right: 10px;
    top: 10px;
    cursor: pointer;
    width: 20px;
    height: 20px;
  }

  .no-scrollbar {
    overflow: auto;
    /* 确保内容仍然可以滚动 */
    -ms-overflow-style: none;
    /* Internet Explorer 10+ */
    scrollbar-width: none;
    /* Firefox */
  }

  /* 适用于 Chrome, Safari, Edge */
  .no-scrollbar::-webkit-scrollbar {
    display: none;
  }

  .variable-table {
    width: 100%;
  }

  .promptWordLibrary {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding-top: 24px;
   position: relative;
    &-title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 24px;
      color: #383d47;
      //   line-height: 40px;
      text-align: left;
      font-style: normal;
      margin-bottom: 24px;
    }

    &-htn {
      display: flex;
      justify-content: space-between;

      .el-button {
        background: #1747E5;
        height: 40px;
        border-radius: 2px;

        img {
          width: 14px;
          height: 14px;
          vertical-align: bottom;
          margin-right: 5px;
        }
      }
    }

    &-list {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
      .list {
        margin-top: 24px;
        overflow: scroll;
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
        grid-gap: 24px 24px;
        // height: 700px;
        &-item {
          // height: 164px;
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

          &:hover {
            box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
          }

          &:nth-child(4n) {
            margin-right: 0;
          }

          &-top {
            display: flex;
            align-items: center;
            padding-top: 16px;
            margin: 0 16px;
            height: 60px;
            padding-bottom: 12px;
            .avatar {
              margin-right: 12px;
              width: 32px;
              height: 32px;
              border: 1px solid #e1e4eb;
              border-radius: 50%;
            }
            .text{
              height: 24px;
              .row-name{
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 16px;
                color: #494E57;
                line-height: 24px;
                text-align: left;
                font-style: normal;
              }
            }
          }

          &-center {
            display: flex;
            align-items: center;
            margin: 0 16px;
            margin-bottom: 12px;
            .componentDesc {
              display: -webkit-box; 
              -webkit-box-orient: vertical; 
              -webkit-line-clamp: 2;
              white-space: normal;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              height: 44px;
              line-height: 22px;
              text-align: justify;
              font-style: normal;
              // overflow: hidden;
              // text-overflow: ellipsis;
              // white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis; 
            }
          }

          &-bottom {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: space-between;
            // font-family: MiSans, MiSans;
            // font-size: 14px;
            // color: #768094;
            margin: 0 16px;
            margin-bottom:16px;
            height: 32px!important;
            .tips {
              display: flex;
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 12px;
              color: #828894;
              line-height: 16px;
              text-align: left;
              font-style: normal;
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

          }

          .tipsTpye {
            background: #f0f4fa;
            border-radius: 4px;
            padding: 4px 6px;
            margin: -4px 0 0 6px;

            .tips-name {
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 12px;
              color: #828894;
              line-height: 16px;
            }
          }

          .row {
            display: flex;
            align-items: center;
            margin-bottom: 4px;

            &-name {
              margin-right: 6px;
              //   height: 24px;
              font-family: MiSans, MiSans;
              font-weight: 600;
              font-size: 16px;
              color: #383d47;
              line-height: 24px;
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
    }
     
    .create-prompt-head {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: -8px;

      .backBtn {
        margin-right: 16px;
        cursor: pointer;
        width: 20px;
        height: 20px;

        img {
          width: 100%;
          height: 100%;
        }
      }

      &-left {
        display: flex;
        align-items: center;

        .voiceImg {
          height: 40px;
          width: 40px;

          img {
            width: 100%;
            height: 100%;
          }
        }

        .text {
          display: flex;
          flex-direction: column;
        }

        .descript {
          font-size: 14px;
          color: #828894;
          line-height: 20px;
          margin-left: 16px;
        }

        .title {
          margin: 0 8px 0 16px;
          font-weight: 500;
          font-size: 18px;
          color: #494E57;
          line-height: 24px;
          text-align: left;
        }
      }

      &-button {
        display: flex;
        align-items: center;

        iconpark-icon {
          margin-right: 6px;
        }
      }
    }

    .promptWordHead {
      height: 48px;
      line-height: 48px;
      background: #F0F5FF;
      position: relative;

      .word {
        padding-left: 12px;
        color: #494E57;
        display: flex;
        justify-content: space-between;

        .promptWord {
          font-size: 16px;

        }

        .rightSpan {
          font-size: 14px;
          display: flex;

          &>div {
            cursor: pointer;
            margin: 0 15px;

            img {
              vertical-align: middle;
              width: 12px;
              height: 12px;
              margin-right: 6px;
            }
          }

          .aiBtn {
            position: relative;
            width: 84px;
            height: 32px;
            background: linear-gradient(270deg, rgba(142, 101, 255, 0.15) 0%, rgba(23, 71, 229, 0.15) 100%);
            border-radius: 2px;
            margin-top: 8px;
          }

          .headImgSpan {
            position: absolute;
            color: #1747E5;
            height: 32px;
            line-height: 32px;
            font-size: 14px;
            padding-left: 10px;
          }

          .aiBox {
            position: absolute;
            z-index: 99;
            top: 50px;
            right: -15px;
            width: 416px;
            background: linear-gradient(270deg, rgba(142, 101, 255, 0.15) 0%, rgba(23, 71, 229, 0.15) 100%), #FFFFFF;
            box-shadow: 0px 0px 12px 0px rgba(26, 36, 70, 0.08);
            border-radius: 4px;

            .textContent {
              line-height: 20px;
              background: #FFFFFF;
              border-radius: 2px;
              margin: 8px 8px;
              padding: 12px;

              img {
                width: 15px;
                height: 17px;
                cursor: pointer;
              }

              .el-button {
                width: 60px;
                height: 32px;
                line-height: 32px;
                padding: 0;
                margin-top: 10px;
              }
            }
          }

          // .replaceBox {
          //     .replaceBoxGuanbi{
          //         position: absolute;
          //         right: 10px;
          //         top: 10px;
          //         cursor: pointer;
          //         span{
          //             width: 20px;
          //             height: 20px;
          //         }
          //     }
          //     position: absolute;
          //     z-index: 99;
          //     padding: 16px;
          //     width: 354px;
          //     height: 164px;
          //     background: #FFFFFF;
          //     box-shadow: 0px 0px 12px 0px rgba(26,36,70,0.08);
          //     border-radius: 8px;
          //     .el-form-item {
          //         margin-bottom: 0;
          //         line-height: 26px;
          //         position: relative;
          //     }
          //     .el-form-item__label {
          //         padding-bottom: 0;
          //         line-height: 20px !important;
          //     }
          //     .el-input--mini {
          //         line-height: 13px;
          //         // position: absolute;
          //         // top: -14px;
          //     }
          // }
          // .replaceBoxBiaotiKuang{
          //     border: 1px solid #e1e4eb;
          //     top: 10px;
          // }
          .variableBox {
            position: fixed;
            z-index: 99;
            min-width: 128px;
            padding: 3px;
            box-sizing: border-box;
            background: #FFFFFF;
            box-shadow: 0px 0px 12px 0px rgba(26, 36, 70, 0.08);
            border-radius: 2px;

            &-item {
              div {
                height: 32px;
                line-height: 32px;
                font-size: 14px;
                color: #36383D;
                padding: 0px 9px;

                &:hover {
                  background: rgba(188, 193, 204, 0.2);
                }
              }
            }

            .no-data1 {
              height: 32px;
              line-height: 32px;
              font-size: 14px;
              color: #36383D;
              padding: 0px 9px;
            }
          }
        }
      }

      .inputtext {
        height: 800px;

        .el-textarea__inner {
          height: 100%;
        }
      }
    }

    .plugin-btn-color {
      color: #494E57;
    }

    .plugin-bd {
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
        padding: 0 0 0px 10px;

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
        padding: 0 20px;

        .drawer-content {
          overflow-x: hidden;
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

    .variable-form {
      flex: 1;
    }

    .save-form {
      display: flex;
      background: #f7f9fc;
      border-radius: 4px;
      margin-top: 12px;
      padding: 16px 10px;
      align-items: flex-start;

      .save-img {
        width: 60px;
        display: flex;
        margin-top: 6px;

        img {
          width: 24px;
          margin-left: 6px;
          cursor: pointer;
        }
      }
    }

    .params-list {
      margin-bottom: 12px;

      &-item {
        height: 40px;
        border: 1px solid #D5D8DE;
        border-radius: 4px;
        display: flex;
        align-items: center;
        padding: 10px;
        position: relative;

        &-name {
          font-size: 16px;
          color: #383d47;
          line-height: 20px;
          font-weight: bold;
        }

        &-type {
          background: #ebeef2;
          border-radius: 4px;
          font-size: 12px;
          color: #828894;
          padding: 2px 4px;
          margin: 0 54px 0 6px;
        }

        &-desc {
          font-size: 14px;
          color: #828894;
          flex: 1;
        }

        &-tool {
          img {
            width: 16px;
            margin: 0 0 0 10px;
            cursor: pointer;
          }
        }
      }
    }

    .dialog-footer {
      text-align: right;

      .el-button {
        border-radius: 2px;
        font-size: 14px;
      }

      .el-button--primary {
        background: #1747E5;
        border-color: #1747E5;
      }

      .el-button--default {
        border-color: #c4c6cc;
        color: #383d47;

      }
    }
  }
 
 
 
  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
  }
</style>