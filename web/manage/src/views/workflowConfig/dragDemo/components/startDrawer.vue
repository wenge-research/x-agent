<template>
  <div class="Ec-x6-icon">
    <el-drawer title="" :visible.sync="drawerVisible" :modal="false" :modal-append-to-body="false"
      :direction="direction" v-if="drawerVisible" size="520px" style="
                position: absolute;
                width: 520px;
                box-sizing: border-box;
                right: 0;
                left: inherit;
            " :show-close="false" :before-close="handleClose">
            <div v-if="isWorkflow">
              <div class="drawer-header">
                  <img src="@/assets/svg/gongzuoliujiedian-kaishi.svg" />
                  <span>{{ $t("start") }}</span>
                  <i class="el-icon-close custom-close-icon" @click="closeDrawer"></i>
                  <div class="sub">{{ $t("startNodeDescription") }}</div>
              </div>
              <div class="trigger-tab">
                  <div class="tab-item" :class="triggerTabs === 'input' ? 'active' : ''" @click="handTrigger('input')">输入
                  </div>
                  <div class="tab-item" :class="triggerTabs === 'trigger' ? 'active' : ''"
                      @click="handTrigger('trigger')">触发器</div>
              </div>
              <div class="drawer-content no-scrollbar" v-show="triggerTabs === 'input'">
                  <div class="section-title">
                      {{ $t("input") }}
                      <span @click="addVariable" class="add-icon"><i class="el-icon-plus"></i>添加</span>
                  </div>
                  <div class="tab-list">
                      <el-collapse v-model="activeNames">
                          <el-collapse-item v-for="(item, index) in variables" :key="index"
                              :name="item.id ? item.id : index.toString()">
                              <template #title>
                                  <b>{x}</b> {{ item.name }}
                                  <!-- <span v-if="index === 0"
                                  >({{ $t("defaultParameterQuestion") }})</span
                              > -->
                                  <span class="type">{{ item.type }}</span>
                                  <iconpark-icon name="delete-bin-4-line"
                                      v-if="!['CONVERSATION_NAME', 'USER_INPUT', 'question'].includes(item.name)"
                                      class="delete-icon" @click.stop="removeVariable(index)"></iconpark-icon>
                              </template>
                              <el-form :model="item" ref="formRefs[index]" :rules="rules" label-width="80px"
                                  @submit.prevent>
                                  <el-form-item :label="$t('variableName')" prop="name">
                                      <el-input size="medium" v-model="item.name"
                                          :disabled="['CONVERSATION_NAME', 'USER_INPUT'].includes(item.name)"
                                          @input="handleInput($event, index)" placeholder="请输入，只允许字母、数字"
                                          @blur="inputBlur"></el-input>
                                  </el-form-item>
                                  <el-form-item :label="$t('fieldType')" prop="type">
                                      <el-cascader v-model="item.type" style="width: 100%" popper-class="cus-cascader"
                                          :options="options" :ref="item.id ? item.id : 'cascader' + index" :props="{
                                              expandTrigger: 'hover',
                                              emitPath: false,
                                          }" clearable @change="cascaderChange($event, item, index)">
                                      </el-cascader>
                                  </el-form-item>
                                  <!-- 选择为普通/下拉框/多行输入 -->
                                  <el-form-item label="输入类型" prop="inputType" v-if="!fileOptions.includes(item.type)">
                                      <el-select v-model="item.inputType" placeholder="请选择" size="medium">
                                          <el-option v-for="item in mySelectOptions" :key="item.value" :label="item.label"
                                              :value="item.value">
                                          </el-option>
                                      </el-select>
                                  </el-form-item>

                                  <!-- 选择为下拉框时 -->
                                  <el-form-item label="选项" prop="selectValues"
                                      v-if="!fileOptions.includes(item.type) && item.inputType == 'select'">
                                      <!-- <el-input
                                      size="medium"
                                      v-model="item.selectValues"
                                      type="textarea"
                                      :rows="2"
                                      maxlength="50"
                                      @blur="inputBlur"
                                  ></el-input> -->
                                      <div>
                                          <div class="select-ctn" v-for="(value, valueIndex) in selectValuesArray(item)"
                                              :key="valueIndex + 's'"
                                              :style="{ marginTop: valueIndex == 0 ? '0' : '10px' }">
                                              <el-input v-model="selectValuesArray(item)[valueIndex]" placeholder="请输入"
                                                  size="medium" style="flex: 1;"
                                                  @input="updateSelectValues(item, valueIndex, $event)"></el-input>
                                              <iconpark-icon name="delete-bin-4-line" size="20"
                                                  @click.stop="removeValue(item, valueIndex)"></iconpark-icon>
                                          </div>
                                      </div>

                                      <el-button size="medium" style="padding: 0;" type="text"
                                          @click="addNewValue(item)">新增选项</el-button>
                                  </el-form-item>

                                  <el-form-item :label="$t('description')" prop="description">
                                      <el-input size="medium" v-model="item.description" type="textarea" :rows="2"
                                          maxlength="50" @blur="inputBlur"></el-input>
                                  </el-form-item>

                                  <!-- <el-form-item
                                  :label="$t('maxLength')"
                                  prop="maxLength"
                              >
                                  <el-input-number
                                      size="medium"
                                      v-model="item.maxLength"
                                      :min="0"
                                      @change="inputChange"
                                  ></el-input-number>
                              </el-form-item>
                              <el-form-item
                                  :label="$t('isRequired')"
                                  prop="required"
                              >
                                  <el-switch
                                      size="medium"
                                      v-model="item.required"
                                      :disabled="index === 0"
                                      active-color="#1C50FD"
                                      @blur="switchChange"
                                  >
                                  </el-switch>
                              </el-form-item> -->
                              </el-form>
                          </el-collapse-item>
                      </el-collapse>
                  </div>
              </div>
              <div class="trigger-box" v-show="triggerTabs === 'trigger'">
                  <div class="trigger-title">
                      <p>触发器设置</p>
                      <div>
                          <el-switch v-model="formInline.status" active-color="#13ce66" inactive-color="#C9CDD4">
                          </el-switch>
                      </div>
                  </div>

                  <!-- 定时任务 -->
                  <div class="task-timer" v-show="formInline.status">
                      <div class="task-timer-form">
                          <el-form :inline="true" :model="formInline" class="demo-form-inline" label-position="top">
                              <el-form-item label="触发周期" label-width="140px" required>
                                  <div style="display: flex">
                                      <!-- 周期类型选择 -->
                                      <el-select v-model="formInline.cycleType" style="width: 140px; margin-right: 8px"
                                          @change="handleCycleTypeChange">
                                          <el-option label="每天" value="day"></el-option>
                                          <el-option label="每周" value="week"></el-option>
                                          <el-option label="每月" value="month"></el-option>
                                      </el-select>
                                      <!-- 每周选项（单选） -->
                                      <el-select v-if="formInline.cycleType === 'week'" v-model="formInline.weekDay"
                                          placeholder="选择星期" style="width: 120px; margin-right: 8px">
                                          <el-option v-for="day in weekDayOptions" :key="day.value" :label="day.label"
                                              :value="day.value"></el-option>
                                      </el-select>

                                      <!-- 每月选项（单选） -->
                                      <el-select v-if="formInline.cycleType === 'month'" v-model="formInline.monthDay"
                                          placeholder="选择日期" style="width: 120px; margin-right: 8px">
                                          <el-option v-for="day in monthDayOptions" :key="day" :label="`${day}号`"
                                              :value="day"></el-option>
                                      </el-select>

                                      <!-- 时间选择 -->
                                      <el-time-select v-model="formInline.time" :picker-options="{
                                          start: '00:00',
                                          step: '01:00',
                                          end: '24:00',
                                      }" placeholder="选择时间" style="width: 120px; margin-right: 8px"></el-time-select>
                                  </div>
                              </el-form-item>
                          </el-form>
                      </div>
                      <!-- 定时任务参数 -->
                      <div class="trigger-params">
                          <el-collapse v-model="activeName" accordion>
                              <el-collapse-item title="输入值" name="1">
                                  <el-table :data="variables" style="width: 100%" height="360" class="no-border-table">
                                      <el-table-column prop="name" label="变量名" width="180px">
                                          <template #default="{ row, $index }">
                                              <div class="name-box">
                                                  <div class="name">{{ row.name }}<span
                                                          style="color: red; margin-left: 4px">*</span></div>
                                                  <div class="type">{{ row.type }}</div>
                                              </div>
                                          </template>
                                      </el-table-column>
                                      <el-table-column prop="name" label="变量值" width="270px">
                                          <template #default="{ row, $index }">
                                              <el-input v-model="row.config" placeholder="请输入默认执行的内容"
                                                  v-if="!['default', 'image', 'doc', 'ppt', 'txt', 'excel', 'array[file]'].includes(row.type)">
                                              </el-input>

                                              <div v-else class="upload-box">
                                                  <el-upload class="upload-demo" :action="actionUrl"
                                                      :data="{ filePath: 'agent_source', rename: true }"
                                                      :on-success="(response, file, fileList) => handleUploadSuccess($index, response, file, fileList)"
                                                      :before-upload="beforeUpload" :show-file-list="false">
                                                      <div class="upload-icon">
                                                          <iconpark-icon name="folder-2-line" color="#1D2129"
                                                              style="margin-right: 4px;"></iconpark-icon>
                                                          <span v-if="!row.fileName">文件上传</span>
                                                          <span v-else>{{ row.fileName }}</span>
                                                      </div>
                                                  </el-upload>
                                              </div>
                                          </template>
                                      </el-table-column>
                                  </el-table>
                              </el-collapse-item>
                          </el-collapse>
                      </div>

                      <!-- <div class="run-box" @click="runApi">
                          <iconpark-icon name="play-circle-line" color="#1D2129"
                              style="margin-right: 4px;"></iconpark-icon>
                          <span>试运行</span>
                      </div> -->
                  </div>

              </div>
            </div>
            <div v-else>
              <div class="drawer-header" style="padding: 0;">
                <img src="@/assets/svg/gongzuoliujiedian-kaishi.svg" />
                <span>{{ $t("start") }}</span>
                <i class="el-icon-close custom-close-icon" @click="closeDrawer"></i>
                <div class="sub">{{$t('dialogueStartNodeDescription')}}</div>
              </div>
              <div class="drawer-content no-scrollbar">
                <div class="section-title">
                  {{ $t("input") }}
                </div>
                <div class="param-list">
                  <div class="param-list-item" v-for="(item, index) in variables" :key="index"
                  :name="item.id ? item.id : index.toString()" v-show="item.name !== 'CONVERSATION_NAME'">
                    <div>
                      <div class="name-ctn">
                        <span class="name">{{item.name}}<sup style="color: #FF0000;">*</sup></span>
                        <span class="type">{{item.type}}</span>
                      </div>
                      
                    </div>
                    <div>
                      <span class="description">{{item.description}}</span>
                    </div>
                  </div>
                  <div class="param-list-item">
                    <div>
                      <div class="name-ctn">
                        <span class="name">upload_file</span>
                        <span class="type">file</span>
                      </div>
                      
                    </div>
                    <div>
                      <span class="description">用户上传的文档</span>
                    </div>
                    <div class="upload-switch">
                      <el-switch v-model="appForm.fileFlag" active-color="#1747E5" inactive-color="#CED4E0" 
                      >
                      </el-switch>
                    </div>
                    
                  </div>
                </div>
                <div class="section-title">
                  <div class="carry-title">
                    携带上下文
                  </div>
                  <div class="switch">
                    <el-switch v-model="appForm.multi_dialogue_flag" active-color="#1747E5" inactive-color="#CED4E0"
                      active-value="是" inactive-value="否">
                    </el-switch>
                  </div>
                </div>
                <div class="description">开启后，将会结合上下文信息改写用户本轮输</div>
                
                <el-form @submit.prevent style="margin-top: 16px;" v-if="appForm.multi_dialogue_flag=='是'">
                  <el-form-item label="" style="width: 100%;display: inline-flex;">
                    <div class="carry-rounds">
                        <div class="carry-title">
                        <span>携带上下文轮数</span>
                        <div class="icon-ctn">
                          <el-tooltip popper-class="workflow-tooltip" effect="light" :content="'设置带入模型上下文的对话历史轮数。轮数越多，多轮对话的相关性越高，消耗的资源也越多。'" placement="top">
                            <iconpark-icon name="question-line" color="#C9CDD4" size="16" style="margin-left: 5px;"></iconpark-icon>
                          </el-tooltip>
                        </div>
                        </div>
                        <el-input-number v-model="appForm.multi_dialogue_num" size="small" :max="32" :min="1" class="carry-count">
        
                        </el-input-number>
                    </div>
                    
                  </el-form-item>
                  <!-- <el-form-item label="" style="width: 100%;display: inline-flex;margin-bottom: 0px;">
                    <el-checkbox v-model="appForm.cusChecked" @change="appForm.system_promt = ''">自定义改写指令</el-checkbox>
                  </el-form-item>
                  <el-form-item label="" style="width: 100%;display: inline-flex;" v-if="appForm.cusChecked">
                    <el-input v-model="appForm.system_promt" type="textarea" :rows="7" :maxlength="2000" resize="none" show-word-limit>
                    </el-input>
                    
                  </el-form-item> -->
        
                </el-form>
              </div>
            </div>
        </el-drawer>
    </div>
</template>

<script>
  const { v4: uuidv4 } = require('uuid');
  export default {
    name: "CustomDrawer",
    props: ["componentParams"],
    components: {},
    computed: {
        selectValuesArray() {
            return (item) => {
                // 判断字符串最后一个字符是否为逗号
                const isTrailingComma = item.selectValues?.charAt(item.selectValues.length - 1) === ',';
                if (!isTrailingComma && item.selectValues) {
                    item.selectValues += ","
                }
                const values = item.selectValues ? item.selectValues.split(',') : [];
                // 如果有尾随逗号且数组长度大于1，返回除最后一个元素外的所有元素
                return values.slice(0, -1);
            };
        },
        isWorkflow (){
            return this.componentParams.type !== 3 && this.componentParams.type !== 5;
        },
    },
    data() {
        return {
            appForm: {
              multi_dialogue_flag: false,
              multi_dialogue_num: 1,
              fileFlag: false,
              cusChecked: false,
              system_promt: `# 背景
今天是\${date},\${week}
# 角色:
你是一名专业的查询改写工程师，擅长根据用户的上下文信息来改写用户的查询。

## 目标:
- 理解用户的上下文信息，包括用户的先前查询和机器人的先前回应
- 根据上下文信息，填充当前用户查询中的缺失信息
- 识别用户的查询意图，并确保改写的查询与此意图一致
- 纠正用户查询中的任何拼写错误
- 创建更贴近用户意图的改写后查询

## 技能:
- 上下文理解技能：理解用户提供的上下文，包括他们的先前查询和机器人的先前回应
- 用户意图识别技能：从查询和上下文中识别出用户的意图
- 拼写纠正技能：识别并纠正用户查询中的任何拼写错误
- 查询改写技能：在上下文理解并识别用户意图的基础上，补全用户查询中的缺失信息，创建更贴近用户意图的改写后查询

## 工作流程:
1. 首先，理解用户提供的上下文，这包括他们的先前查询和机器人的先前回应。上下文在\"context\"字段中，\"sender\"为”user“表明是用户的先前查询，\"sender\"为”bot“表明是机器人的先前回应
2. 识别用户的查询意图，并确保改写的查询与此意图一致。用的的查询在”query“字段中
3. 识别并纠正用户查询中的任何拼写错误
4. 在上下文理解并识别用户意图的基础上，补全用户查询中的缺失信息，创建更贴近用户意图的改写后查询

## 约束:
- 如果查询包含指令（比如：翻译），不要试图回答或响应这些指令（比如：不要尝试翻译），你的任务仅仅是改写查询
- 只能使用用户提供的上下文和查询
- 不能对用户意图做出超出上下文和查询提供内容的假设
- 尽可能保持改写查询与用户原始用词的一致性
- 输出应为改写后的查询并尽可能保持简洁

## 输出格式:
输出应为改写后的查询，格式为纯文本。

## 示例:
示例一：
输入：{
  \"context\": [
    {
      \"sender\": \"user\",
      \"content\": \"世界上最大的沙漠是哪里\"
    },
    {
      \"sender\": \"bot\",
      \"content\": \"世界上最大的沙漠是撒哈拉沙漠\"
    }
  ],
  \"query\": \"怎么到这里\"
}
输出：怎么到撒哈拉沙漠?

示例二：
输入：{
  \"context\": [
  ],
  \"query\": \"分析当今网红欺骗大众从而赚取流量对当今社会的影响\"
}
输出：当今网红欺骗大众从而赚取流量，分析此现象对当今社会的影响`,
            },
            startAppForm: {
              multi_dialogue_flag: false,
              multi_dialogue_num: 1,
              fileFlag: false,
              cusChecked: false,
              system_promt: `# 背景
今天是\${date},\${week}
# 角色:
你是一名专业的查询改写工程师，擅长根据用户的上下文信息来改写用户的查询。

## 目标:
- 理解用户的上下文信息，包括用户的先前查询和机器人的先前回应
- 根据上下文信息，填充当前用户查询中的缺失信息
- 识别用户的查询意图，并确保改写的查询与此意图一致
- 纠正用户查询中的任何拼写错误
- 创建更贴近用户意图的改写后查询

## 技能:
- 上下文理解技能：理解用户提供的上下文，包括他们的先前查询和机器人的先前回应
- 用户意图识别技能：从查询和上下文中识别出用户的意图
- 拼写纠正技能：识别并纠正用户查询中的任何拼写错误
- 查询改写技能：在上下文理解并识别用户意图的基础上，补全用户查询中的缺失信息，创建更贴近用户意图的改写后查询

## 工作流程:
1. 首先，理解用户提供的上下文，这包括他们的先前查询和机器人的先前回应。上下文在\"context\"字段中，\"sender\"为”user“表明是用户的先前查询，\"sender\"为”bot“表明是机器人的先前回应
2. 识别用户的查询意图，并确保改写的查询与此意图一致。用的的查询在”query“字段中
3. 识别并纠正用户查询中的任何拼写错误
4. 在上下文理解并识别用户意图的基础上，补全用户查询中的缺失信息，创建更贴近用户意图的改写后查询

## 约束:
- 如果查询包含指令（比如：翻译），不要试图回答或响应这些指令（比如：不要尝试翻译），你的任务仅仅是改写查询
- 只能使用用户提供的上下文和查询
- 不能对用户意图做出超出上下文和查询提供内容的假设
- 尽可能保持改写查询与用户原始用词的一致性
- 输出应为改写后的查询并尽可能保持简洁

## 输出格式:
输出应为改写后的查询，格式为纯文本。

## 示例:
示例一：
输入：{
  \"context\": [
    {
      \"sender\": \"user\",
      \"content\": \"世界上最大的沙漠是哪里\"
    },
    {
      \"sender\": \"bot\",
      \"content\": \"世界上最大的沙漠是撒哈拉沙漠\"
    }
  ],
  \"query\": \"怎么到这里\"
}
输出：怎么到撒哈拉沙漠?

示例二：
输入：{
  \"context\": [
  ],
  \"query\": \"分析当今网红欺骗大众从而赚取流量对当今社会的影响\"
}
输出：当今网红欺骗大众从而赚取流量，分析此现象对当今社会的影响`,
            },
            actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
            copiedVariables: [
                {
                    description: "",
                    maxLength: 20,
                    name: "",
                    required: true,
                    type: "string",
                    inputType: "",
                    selectValues: "",
                    input: ''
                },
            ], //复制一个variables用于触发器
            activeName: '1',
            triggerTabs: 'input',  //是否是触发器
            options: [
                {
                    value: "string",
                    label: "String",
                },
                {
                    value: "number",
                    label: "Number",
                },
                {
                    value: "integer",
                    label: "Integer",
                },
                {
                    value: "file",
                    label: "File",
                    children: [
                        {
                            value: "default",
                            label: "default",
                        },
                        {
                            value: "image",
                            label: "image",
                        },
                        {
                            value: "doc",
                            label: "doc",
                        },
                        // {
                        //     value: "code",
                        //     label: "code",
                        // },
                        {
                            value: "ppt",
                            label: "ppt",
                        },
                        {
                            value: "txt",
                            label: "txt",
                        },
                        {
                            value: "excel",
                            label: "excel",
                        },
                    ],
                },
                {
                    value: "boolean",
                    label: "Boolean",
                },
                {
                    value: "object",
                    label: "Object",
                },
                {
                    value: "array",
                    label: "Array",
                    children: [
                        {
                            value: "array[string]",
                            label: "String",
                        },
                        {
                            value: "array[number]",
                            label: "Number",
                        },
                        {
                            value: "array[integer]",
                            label: "Integer",
                        },
                        {
                            value: "array[boolean]",
                            label: "Boolean",
                        },
                        {
                            value: "array[object]",
                            label: "Object",
                        },
                        {
                            value: "array[file]",
                            label: "file",
                        }
                    ]
                }
            ],
            fileOptions: ["default", "image", "doc", "code", "ppt", "txt", "excel", "array[file]"],
            drawerVisible: false,
            direction: "rtl",
            size: "100%",
            activeNames: ['0', '1'],
            variables: [
                {
                    description: "",
                    maxLength: 20,
                    name: "",
                    required: true,
                    type: "string",
                    inputType: "",
                    selectValues: "",
                    config: ''
                },
            ],
            startVariables: [
                {
                    description: "",
                    maxLength: 20,
                    name: "",
                    required: true,
                    type: "string",
                    inputType: "",
                    selectValues: ""
                },
            ],
            mySelectOptions: [
                {
                    value: "",
                    label: "普通输入框"
                },
                {
                    value: "select",
                    label: "下拉框"
                },
                {
                    value: "textarea",
                    label: "多行输入框"
                }
            ],
            selectForm: {
                value: "",

            },
            curNode: {},
            parentNodes: [],
            formRefs: [],
            rules: {
                name: [
                    {
                        required: true,
                        message: this.$t("pleaseEnterVariableName"),
                        trigger: "blur",
                    },
                ],
                type: [
                    {
                        required: true,
                        message: this.$t("pleaseSelectFieldType"),
                        trigger: "change",
                    },
                ],
            },
            formInline: {
                id: '',
                status: false,
                cycleType: "day", // 默认每天
                time: "", // 时间次数
                date: "", // 时间间隔
                weekDay: null, // 选择的星期（单选）
                monthDay: null, // 选择的日期（单选）
            },
            cronExpression: "", // 存储生成的cron表达式
            weekDayOptions: [
                { label: "周一", value: "MON" },
                { label: "周二", value: "TUE" },
                { label: "周三", value: "WED" },
                { label: "周四", value: "THU" },
                { label: "周五", value: "FRI" },
                { label: "周六", value: "SAT" },
                { label: "周日", value: "SUN" },
            ],
            monthDayOptions: Array.from({ length: 31 }, (_, i) => i + 1),
            tableData: [{
                date: '2016-05-02',
                name: '',
            }, {
                date: '2016-05-04',
                name: '',
            }, {
                date: '2016-05-01',
                name: '',
            }],
        };
    },
    created() {

    },
    watch: {
        appForm: {
            handler(newVal, oldVal) {
                this.$emit(
                    "updateAppForm",
                    {
                        appForm: JSON.stringify(newVal),
                    },
                    this.curNode
                );
                // const differences = this.compareObjects(oldVal, newVal)
                // console.log("differences", differences)
            },
            deep: true,
        },
        // 监听 variables 数组的变化
        variables: {
            handler(newVal, oldVal) {
                this.copiedVariables = newVal.map(item => ({
                    ...item, // 扩展运算符用于浅拷贝对象
                    input: null // 添加 input 属性，默认值为 null
                }));
                this.$emit(
                    "updateVariables",
                    { variables: JSON.stringify(newVal) },
                    this.curNode,
                );
            },
            deep: true,
        },

        formInline: {
            handler(newVal) {
                this.cronExpression = this.getCronExpression();
                const status = newVal.status ? '1' : '0';
                this.$emit(
                    "getTriggerData",
                    { status: status, crontab: this.cronExpression, id: this.formInline.id },
                    this.curNode,
                );
            },
            deep: true
        }
    },
    methods: {

        // runApi() {
        //     const cronExpression = this.getCronExpression();
        //     sessionStorage.setItem('crontab',cronExpression);
        // },
        handleUploadSuccess(index, response, file, fileList) {
            const fileName = response.fileName || file.name;
            this.variables[index].config = response.data[0].url;
            this.$set(this.variables, index, {
                ...this.variables[index],
                fileName: fileName
            });
        },
        beforeUpload(file) {
            // 文件上传前的验证逻辑
            const allowedTypes = ['default', 'image', 'doc', 'ppt', 'txt', 'excel', 'docx', 'png', 'jpg', 'jpeg', 'gif'];
            const fileType = file.name.split('.').pop().toLowerCase();

            // 根据你的需求添加验证逻辑
            if (!allowedTypes.includes(fileType)) {
                this.$message.error('不支持的文件类型');
                return false;
            }

            return true;
        },
        //是否选中触发器
        handTrigger(value) {
            this.triggerTabs = value;
        },

        // 获取最终的cron表达式
        getCronExpression() {
            const timeParts = this.formInline.time?.split(":") || ["0", "0"];
            const hour = timeParts[0] || "0";
            const minute = timeParts[1] || "0";

            switch (this.formInline.cycleType) {
                case "day":
                    return `0 ${minute} ${hour} * * ?`;
                case "week":
                    return `0 ${minute} ${hour} ? * ${this.formInline.weekDay || "*"}`;
                case "month":
                    return `0 ${minute} ${hour} ${this.formInline.monthDay || "*"} * ?`;
                default:
                    return "";
            }
        },



        getWeekDayLabel(value) {
            const weekDayMap = {
                MON: "星期一",
                TUE: "星期二",
                WED: "星期三",
                THU: "星期四",
                FRI: "星期五",
                SAT: "星期六",
                SUN: "星期日",
            };
            return weekDayMap[value] || "";
        },
        handleCycleTypeChange() {
            // 切换周期类型时清空之前的选择
            this.formInline.weekDay = null;
            this.formInline.monthDay = null;
        },
        // 获取最终的cron表达式
        getCronExpression() {
            const timeParts = this.formInline.time?.split(":") || ["0", "0"];
            const hour = timeParts[0] || "0";
            const minute = timeParts[1] || "0";

            switch (this.formInline.cycleType) {
                case "day":
                    return `0 ${minute} ${hour} * * ?`;
                case "week":
                    return `0 ${minute} ${hour} ? * ${this.formInline.weekDay || "*"}`;
                case "month":
                    return `0 ${minute} ${hour} ${this.formInline.monthDay || "*"} * ?`;
                default:
                    return "";
            }
        },
        //反向解析Cron 表达式
        parseCronExpression(cronExpression) {
            if (!cronExpression) {
                this.formInline = {
                    status: false,
                    time: '00:00',
                    cycleType: 'day',
                    weekDay: '',
                    monthDay: ''
                };
                return;
            }

            const parts = cronExpression.split(' ');
            if (parts.length < 6) {
                console.error('Invalid cron expression');
                return;
            }

            const minute = parts[1];
            const hour = parts[2];
            const dayOfMonth = parts[3];
            const month = parts[4];
            const dayOfWeek = parts[5];

            // 设置时间
            this.formInline.time = `${hour.padStart(2, '0')}:${minute.padStart(2, '0')}`;

            // 判断周期类型
            if (dayOfWeek !== '?') {
                // 周周期
                this.formInline.cycleType = 'week';
                this.formInline.weekDay = dayOfWeek !== '*' ? dayOfWeek : '';
                this.formInline.monthDay = '';
            } else if (dayOfMonth !== '*') {
                // 月周期
                this.formInline.cycleType = 'month';
                this.formInline.monthDay = dayOfMonth;
                this.formInline.weekDay = '';
            } else {
                // 日周期
                this.formInline.cycleType = 'day';
                this.formInline.weekDay = '';
                this.formInline.monthDay = '';
            }
        },

        handleInput(value, itemIndex) {
            // 使用正则表达式过滤非法字符
            this.variables[itemIndex].name = value.replace(/[^a-zA-Z0-9_]/g, '');
            //  如果以数字开头，清空输入
            if (/^\d/.test(this.variables[itemIndex].name)) {
                this.variables[itemIndex].name = '';
            }
            this.variables.forEach((item, index) => {
                if (item.name === value && index !== itemIndex) {
                    this.variables[itemIndex].name = '';
                }
            })
        },
        inputBlur() {
            this.$EventBus.$emit("saveWorkflow");
        },
        inputChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        cascaderChange(val, item, index) {
            let ref = item.id ? item.id : 'cascader' + index
            this.$refs[ref][0].dropDownVisible = false;
        },
        switchChange() {
            this.$EventBus.$emit("saveWorkflow");
        },
        openDrawer() {
            let node = this.$store.state.workflow.editNode;

            let parentNodes = this.$store.state.workflow.parentNodes;
            const data = node.store.data.data;
            data.crontab && this.parseCronExpression(data.crontab);
            this.formInline.status = data.status == '1' ? true : false;
            this.formInline.id = data.id;
            this.drawerVisible = true;
            this.curNode = node;
            this.variables = node.data.variables
                ? Array.isArray(node.data.variables)
                    ? node.data.variables
                    : JSON.parse(node.data.variables) : this.startVariables;

            this.variables.map((item, index) => {
                if (!this.variables[index].inputType) {
                    this.$set(this.variables[index], "inputType", "")
                    this.$set(this.variables[index], "selectValues", "")
                }
            })
            this.appForm = node.data.appForm
            ? JSON.parse(node.data.appForm)
            : JSON.parse(JSON.stringify(this.startAppForm));
            this.parentNodes = parentNodes;

        },
        closeDrawer() {
            this.drawerVisible = false;
            // this.$EventBus.$emit("saveWorkflowAgain");
        },
        async addVariable() {
            let id = await uuidv4()?.replace(/-/g, '')
            const newVariable = {
                name: "",
                description: "",
                id: id,
                type: "string",
                maxLength: 20,
                required: true,
                inputType: "",
                selectValues: "",
                input: '',
            };
            this.variables.push(newVariable);
            this.activeNames.push(id); // 初始化表单引用
            this.formRefs.push(null); // 初始化表单引用
            this.$EventBus.$emit("saveWorkflowAgain");
        },
        removeVariable(index) {
            this.variables.splice(index, 1);
            this.formRefs.splice(index, 1);
            this.$EventBus.$emit("saveWorkflow");
        },
        submitForm() {
            let allValid = true;
            this.formRefs.forEach((formRef, index) => {
                if (formRef) {
                    formRef.validate((valid) => {
                        if (!valid) {
                            allValid = false;
                        }
                    });
                }
            });

            if (allValid) {
                console.log(this.$t("allFormValidationPassed"));
                // 提交逻辑
            } else {
                console.log(this.$t("formValidationFailed"));
            }
        },
        handleClose(done) {
            this.$emit("updateVariables", JSON.stringify(this.variables), this.curNode);
        },
        updateSelectValues(item, index, value) {
            const arr = [...this.selectValuesArray(item), '']
            arr[index] = value
            this.$set(item, 'selectValues', arr.join(','));
        },
        addNewValue(item) {
            const values = item.selectValues.concat(',');
            this.$set(item, 'selectValues', values);
        },
        removeValue(item, index) {
            const values = [...this.selectValuesArray(item)];
            values.splice(index, 1);
            values.push('')

            this.$set(item, 'selectValues', values.join(','));
        }
    },
  };
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style scoped lang="scss">
.trigger-tab {
    background: rgba(201, 205, 212, 0.1);
    border-radius: 4px;
    height: 32px;
    display: flex;
    align-items: center;
    padding: 0 1px;
    cursor: pointer;

    .tab-item {
        width: 262px;
        height: 28px;
        text-align: center;
        line-height: 28px;
    }

    .active {
        background: #FFFFFF;
        box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.1);
        border-radius: 2px;

    }
}

.trigger-box {
    margin-top: 18px;


    .trigger-title {
        font-size: 16px;
        color: #1D2129;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .task-timer {


        .timer-header {
            display: flex;
            align-items: center;

            .name {
                font-size: 18px;
                color: #1d2129;
                margin-right: 16px;
            }

            .info {
                font-weight: 400;
                font-size: 14px;
                color: #86909c;
            }

            .times {
                box-sizing: border-box;
                padding: 2px 4px;
                font-weight: 400;
                font-size: 12px;
                color: #7e56eb;
                background: #ebddfe;
                border-radius: 2px;
            }
        }

        .task-timer-form {
            // margin-top: 6px;
            border-bottom: 1px solid #E7E7E7;
        }
    }

    .trigger-params {
        margin-top: 28px;

        .name-box {
            display: flex;

            .name {
                font-weight: 400;
                font-size: 14px;
                color: #1D2129;
                margin-right: 4px;
            }

            .type {
                padding: 0 4px;
                background: #EBEEF2;
                border-radius: 2px;
                font-weight: 400;
                font-size: 12px;
                color: #1D2129;
            }
        }

        .upload-box {
            padding: 4px 15px;
            border: 1px solid #DCDFE6;
            border-radius: 4px;
            display: flex;

            .upload-icon {
                display: flex;
                align-items: center;
                font-weight: 400;
                font-size: 14px;
                color: #494C4F;
            }
        }
    }

}

.run-box {
    padding: 6px 0;
    border-radius: 4px;
    border: 1px solid #E7E7E7;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 400;
    font-size: 14px;
    color: #1D2129;
    cursor: pointer;
}

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

.add-icon {
    cursor: pointer;
    font-size: 14px;
    color: #383d47;

    i {
        margin: 0 4px 0 0;
    }
}

.delete-icon {
    font-size: 16px;
    cursor: pointer;
    color: #828894;
    margin-left: 10px;
    position: absolute;
    top: 16px;
    right: 16px;
}

.delete-icon:hover {
    color: #e6a23c;
}

.Ec-x6-icon ::v-deep .el-drawer__wrapper {
    border-radius: 0;
    top: 10px;
}

.Ec-x6-icon ::v-deep .el-collapse .el-collapse-item__wrap {
    border-top: 1px solid rgba(0, 0, 0, 0.12);
    padding-top: 20px;
}

.Ec-x6-icon ::v-deep .el-drawer__body {
    overflow-y: hidden;
    position: relative;
}

.Ec-x6-icon ::v-deep .el-drawer {
    padding: 16px;
}

.Ec-x6-icon ::v-deep .el-drawer__header {
    padding: 0;
    margin-bottom: 0;
}

.tab-list {
    overflow: auto;
    height: calc(100vh - 270px);

    .select-ctn {
        width: 100%;
        display: flex;
        align-items: center;
        gap: 10px;
    }
}

.no-border-table::v-deep .el-table {
    border: none;
}

.no-border-table::v-deep .el-table__header-wrapper th {
    border: none;
}

.no-border-table::v-deep .el-table__body-wrapper td {
    border: none;
}

.no-border-table::v-deep .el-table__row td {
    border-bottom: none;
}

.no-border-table::v-deep .el-table__header-wrapper {
    border-bottom: none;
}
</style>
<style scoped lang="scss">
  
  .description{
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 12px;
    color: #86909C;
    line-height: 16px;
  }

  .carry-rounds{
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 35px;

    .carry-title{
      display: flex;
      align-items: center;

      span{
        display: inline-block;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #1D2129;
        line-height: 20px;
      }

      .icon-ctn{
        height: 16px;
        line-height: 19px;
        position: relative;
      }
    }

    .carry-count{
      flex: 1;
      min-width: 140px;
      display: flex;
      height: 32px;
      background: #FFFFFF;
      border-radius: 4px;
      border: 1px solid #E7E7E7;

      ::v-deep .el-input-number__increase{
        border: none !important;
        background: none !important;

        .el-icon-plus:before{
          color: #1D2129;
        }
      }
      ::v-deep .el-input-number__decrease{
        border: none !important;
        background: none !important;
        .el-icon-plus:before{
          color: #86909C;
        }
      }
    }
  }

  .param-list{
    display: flex;
    flex-direction: column;
    gap: 12px;
    .param-list-item{
      width: 100%;
      border: 1px solid #E7E7E7;
      padding: 12px 16px;
      box-sizing: border-box;
      border-radius: 4px;
      position: relative;
      display: flex;
      flex-direction: column;
      gap: 6px;

      .name-ctn{
        width: 100%;
        height: 20px;
        display: flex;
        gap: 4px;
        align-items: center;
        span{
          display: inline-block;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          color: #1D2129;
          line-height: 16px;
        }

        .type{
          height: 20px;
          padding: 2px 4px;
          box-sizing: border-box;
          background:#EBEEF2 ;
          border-radius: 2px;
        }
      }

      .description{
        height: 16px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #86909C;
        line-height: 16px;
      }

      .upload-switch{
        position: absolute;
        top: 12px;
        right: 16px;
      }
      
    }
  }

//   ::v-deep .el-form-item__content{
//     width: 100%;
//   }

//   ::v-deep .el-textarea{
//     textarea{
      
//     }
//   }
  ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner, .el-checkbox__input.is-indeterminate .el-checkbox__inner {
    background-color: #1747E5;
    border-color: #1747E5;
  }
  

  ::v-deep .el-checkbox__label{
    color: #1D2129 !important;
  }

  ::v-deep .el-input__count{
    color: #C9CDD4;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 12px;
    background-color: rgba(0,0,0,0);
  }
</style>
<style lang="scss">
.workflow-tooltip{
  border: none !important;
  box-shadow: 0px 6px 12px 0px rgba(0,0,0,0.1);
  padding: 8px 12px !important;
  line-height: 20px;
  font-size: 14px;
  color: #1D2129;
  font-family: MiSans Regular;
  max-width: 336px !important;
  
  .popper__arrow{
    border-top-color: rgba(0,0,0,0) !important;
    bottom: -5px !important; 
  }
}
</style>