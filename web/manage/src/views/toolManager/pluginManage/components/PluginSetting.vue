<template>
  <div class="pluginDetails-outerDialog" v-loading="loading">
    <div class="headBar">
      <div class="leftSlide">
        <iconpark-icon
          name="arrow-go-back-fill"
          @click="closeDialog"
          color="#36383D"
          size="18"
          style="cursor: pointer"
        ></iconpark-icon>
        <img v-if="appForm.icon" class="avatar" :src="appForm.icon" alt="" />
        <img
          v-else
          class="avatar"
          src="@/assets/images/default-plugin.svg"
          alt=""
        />
        <div class="titleIcon">
          <div class="txt1">
            {{ appForm.componentName || $t("noApplicationName") }}
            <iconpark-icon
              name="edit-line"
              style="cursor: pointer; margin-left: 10px"
              size="16"
              color="#828894"
              @click="editInfo"
            ></iconpark-icon>
          </div>
          <div class="txt2">
            {{ appForm.componentDesc || $t("noDescription") }}
          </div>
        </div>
      </div>
      <div class="rightSlide">
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
		      v-model="appForm.allVisible"
		      :active-value="1"
		      :inactive-value="0"
		      active-color="#1747E5"
		      inactive-color="#CED4E0"
		    >
		    </el-switch>
		    <span v-if="appForm.allVisible">设为预置</span>
		    <span v-else>设为预置</span>
		  </div> -->
        <div
          style="
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 16px;
          "
        >
          <el-switch
            style="padding-right: 8px"
            v-model="appForm.status"
            :active-value="1"
            :inactive-value="0"
            active-color="#1747E5"
            inactive-color="#CED4E0"
          >
          </el-switch>
          <span v-if="appForm.status">{{ $t("activeStatus") }}</span>
          <span v-else>{{ $t("inactiveStatus") }}</span>
        </div>
        <el-button
          plain
          v-permission="'issue'"
          @click="submitAddApp(false)"
          style="
            width: 92px;
            height: 40px;
            border-radius: 2px;
            font-weight: 500;
            font-size: 16px;
            color: #36383d;
          "
        >
          <iconpark-icon
            name="save-line"
            size="18"
            color="#36383D"
            style="margin-right: 6px; color: #fff"
          ></iconpark-icon>
          <span>{{ $t("save") }}</span>
        </el-button>
        <el-button
          plain
          v-permission="'issue'"
          @click="openFabuDrawer"
          type="primary"
          style="
            width: 140px;
            height: 40px;
            background: #4d77ef;
            border-radius: 2px;
            color: #fff;
            margin-left: 16px;
          "
        >
          <iconpark-icon
            name="send-plane-2-fill"
            size="18"
            color="#fff"
            style="margin-right: 6px"
          ></iconpark-icon>
          <span>{{ $t("saveAndPublish") }}</span>
        </el-button>
      </div>
    </div>
    <div class="dialogPower plugin" v-if="appForm.type === 1">
      <el-row>
        <!-- <el-col :span="5"
                    ><div class="plugin-hd">
                        <div class="plugin-hd-tit" @click="copyText">
                            {{ $t("apiUrl") }}
                            <i class="el-icon-copy-document"></i>
                        </div>
                        <div class="plugin-hd-link" ref="copyLink">
                            {{ nodeApi?.settings?.url }}
                        </div>
                        <div class="plugin-hd-tit">{{ $t("basicInfo") }}</div>
                        <ul class="plugin-hd-info">
                            <li>
                                <span class="plugin-hd-info-left">{{
                                    $t("requestMethod")
                                }}</span
                                ><span class="plugin-hd-info-right green">{{
                                    nodeApi?.settings?.method
                                }}</span>
                            </li>
                            <li>
                                <span class="plugin-hd-info-left">{{
                                    $t("authMethod")
                                }}</span
                                ><span class="plugin-hd-info-right">{{
                                    $t("noAuth")
                                }}</span>
                            </li>
                            <li>
                                <span class="plugin-hd-info-left">{{
                                    $t("debugStatus")
                                }}</span
                                ><span
                                    class="plugin-hd-info-right green status"
                                    >{{ $t("passed") }}</span
                                >
                            </li>
                        </ul>
                        <el-button
                            plain
                            class="eidt-api"
                            icon="el-icon-s-operation"
                            @click="apiDrawer = true"
                            >{{ $t("editApi") }}</el-button
                        >
                    </div>
                </el-col> -->
        <el-col :span="17">
          <div class="plugin-bd">
            <el-tabs v-model="activeName" @tab-click="handleClick">
              <el-tab-pane label="起始值" name="first">
                <div class="plugin-bd-first">
                  <div class="plugin-bd-tit">{{ $t("variable") }}</div>
                  <div class="drawer-content">
                    <div
                      v-for="(panel, index) in nodeStart?.output"
                      :key="index"
                      :name="index.toString()"
                    >
                      <el-form
                        :model="panel"
                        ref="formRefs[index]"
                        :rules="rules"
                        label-width="80px"
                        label-position="left"
                        @submit.prevent
                        v-if="editIndex === index"
                      >
                        <el-row :gutter="10">
                          <el-col :span="22">
                            <el-form-item
                              :label="$t('variableName')"
                              prop="label"
                            >
                              <el-input
                                size="medium"
                                @change="panel.variable = panel.label"
                                v-model="panel.label"
                              ></el-input>
                            </el-form-item>
                            <el-form-item :label="$t('fieldType')" prop="type">
                              <el-select size="medium" v-model="panel.type">
                                <el-option
                                  v-for="(item, index) in panelTypeList"
                                  :key="index"
                                  :label="item.label"
                                  :value="item.value"
                                ></el-option>
                              </el-select>
                            </el-form-item>
                            <el-form-item
                              :label="$t('description')"
                              prop="desc"
                            >
                              <el-input
                                size="medium"
                                v-model="panel.desc"
                                type="textarea"
                                :rows="2"
                              ></el-input>
                            </el-form-item>
                            <el-form-item
                              :label="$t('maxLength')"
                              prop="maxLength"
                            >
                              <el-input
                                size="medium"
                                v-model="panel.maxLength"
                                type="textarea"
                                :rows="2"
                              ></el-input>
                            </el-form-item>
                            <el-form-item
                              :label="$t('isRequired')"
                              prop="required"
                            >
                              <el-switch
                                v-model="panel.required"
                                active-color="#1747E5"
                                inactive-color="#CED4E0"
                              >
                              </el-switch>
                              <!-- <el-radio-group
                                                        size="medium"
                                                        v-model="panel.required"
                                                    >
                                                        <el-radio
                                                            :label="true"
                                                            >{{
                                                                $t("yes")
                                                            }}</el-radio
                                                        >
                                                        <el-radio
                                                            :label="false"
                                                            >{{
                                                                $t("no")
                                                            }}</el-radio
                                                        >
                                                    </el-radio-group> -->
                            </el-form-item>
                          </el-col>
                          <el-col :span="2">
                            <i
                              class="el-icon-check hide"
                              @click="editIndex = -1"
                            ></i>
                            <i
                              class="el-icon-close delete"
                              @click="removePanel(index)"
                            ></i>
                          </el-col>
                        </el-row>
                      </el-form>
                      <div class="params-list" v-else>
                        <div class="params-list-item">
                          <div class="params-list-item-name require">
                            {{ panel.label }}
                            <span class="params-list-item-type">{{
                              panel.type
                            }}</span>
                            <span
                              style="
                                font-size: 14px;
                                color: #828894;
                                font-weight: 400px;
                                margin-left: 100px;
                              "
                              >{{ panel.desc }}</span
                            >
                          </div>

                          <div class="params-list-item-tool">
                            <i
                              class="el-icon-edit-outline"
                              @click="editIndex = index"
                            ></i>
                            <i
                              class="el-icon-delete"
                              @click="removePanel(index)"
                            ></i>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="plugin-bd-add" @click="addPanel">
                    <span
                      ><i class="el-icon-plus"></i> {{ $t("addParam") }}</span
                    >
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane :label="$t('apiAccess')" name="second">
                <div class="plugin-bd-second">
                  <div class="apiUrl">
                    <div class="title">
                      <div>
                        {{ $t("apiUrl") }}
                        <span class="font">
                          <span>·</span> {{ nodeApi?.settings?.method }}</span
                        >
                      </div>
                      <span class="editHandler" @click="apiDrawer = true"
                        ><i
                          class="el-icon-s-operation operationIcon"
                          style="vertical-align: middle"
                        ></i
                        >{{ $t("editApi") }}</span
                      >
                    </div>
                    <div>{{ nodeApi?.settings?.url }}</div>
                  </div>
                  <div class="plugin-bd-tit">
                    {{ $t("request") }}
                  </div>
                  <div
                    class="plugin-bd-response"
                    v-if="nodeApi?.settings?.requestBody"
                  >
                    <vue-json-pretty
                      :showNum="false"
                      :data="arrayToNestedJson(nodeApi?.settings?.requestBody)"
                    />
                  </div>
                  <div class="plugin-bd-tit">
                    {{ $t("response") }}
                  </div>
                  <div
                    class="plugin-bd-response"
                    v-if="nodeApi?.settings?.responseBody"
                  >
                    <vue-json-pretty
                      :showNum="false"
                      :data="arrayToNestedJson(nodeApi?.settings?.responseBody)"
                    />
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane :label="$t('outputValue')" name="third">
                <div class="plugin-bd-third" v-if="nodeEnd && nodeEnd.settings">
                  <div class="plugin-bd-third-hd">
                    <span>{{ $t("outputMode") }}</span>
                    <el-radio
                      v-model="nodeEnd.settings.responseModel"
                      :label="0"
                      >{{ $t("returnParams") }}</el-radio
                    >
                    <el-radio
                      v-model="nodeEnd.settings.responseModel"
                      :label="1"
                      >{{ $t("returnTemplate") }}</el-radio
                    >
                  </div>
                  <div class="plugin-bd-tit">
                    {{ $t("output") }}
                  </div>
                  <div class="params-list">
                    <div
                      v-for="(panel, index) in nodeApi?.output"
                      :key="index"
                      :name="index.toString()"
                    >
                      <el-form
                        :model="panel"
                        ref="outputFormRefs[index]"
                        label-width="80px"
                        label-position="top"
                        @submit.prevent
                        inline
                      >
                        <!-- <i
                                                    class="el-icon-arrow-up hide"
                                                    @click="editIndex = -1"
                                                ></i> -->
                        <el-form-item
                          style="width: 320px"
                          :label="$t('paramName')"
                        >
                          <el-input
                            v-model="panel.label"
                          ></el-input>
                        </el-form-item>
                        <el-form-item
                          style="width: 200px"
                          :label="$t('requestMethod')"
                        >
                          <el-select
                            v-model="panel.valueType"
                            style="width: 100%"
                          >
                            <el-option
                              label="String"
                              value="string"
                            ></el-option>
                            <el-option
                              label="integer"
                              value="integer"
                            ></el-option>
                            <el-option
                              label="引用"
                              value="reference"
                            ></el-option>
                          </el-select>
                        </el-form-item>
                        <el-form-item
                          style="width: 320px"
                          :label="$t('paramsValue')"
                        >
                          <el-cascader
                            style="width: 320px"
                            v-if="panel.valueType === 'reference'"
                            v-model="panel.value"
                            :options="nodeApi?.settings.responseBody || []"
                            @change="handleChange($event, index)"
                            :props="{
                              label: 'name',
                              value: 'name',
                              checkStrictly: true,
                            }"
                            clearable
                          ></el-cascader>
                          <el-input
                            v-else
                            v-model="panel.value"
                          ></el-input>
                        </el-form-item>
                        <el-form-item
                          style="width: 40px;text-align: center;"
                          label="删除"
                        >
                          <i
                          class="el-icon-delete"
                          style="cursor: pointer;"
                          @click="removeOutputPanel(index)"
                        ></i>
                        </el-form-item>
                      </el-form>
                      <div class="params-list-item" v-if="false">
                        <div class="params-list-item-name">
                          {{ panel.label }}
                          <span class="params-list-item-type">{{
                            panel.valueType === "reference"
                              ? "引用"
                              : panel.valueType === "integer"
                              ? "integer"
                              : "String"
                          }}</span>
                        </div>
                        <div class="params-list-item-tool">
                          <i
                            class="el-icon-edit-outline"
                            @click="editIndex = index"
                          ></i>
                          <i
                            class="el-icon-delete"
                            @click="removeOutputPanel(index)"
                          ></i>
                        </div>
                        <ul class="params-list-item-way">
                          <li>
                            <span>{{ $t("type") }}</span
                            ><i>{{
                              panel.valueType === "reference"
                                ? "引用"
                                : panel.valueType === "integer"
                                ? "integer"
                                : "String"
                            }}</i>
                          </li>
                          <li>
                            <span>{{ $t("value") }}</span
                            ><em>{{
                              Array.isArray(panel.value)
                                ? "${" + panel.value.join(".") + "}"
                                : panel.value
                            }}</em>
                          </li>
                        </ul>
                      </div>
                    </div>
                    <div>
                      <el-button style="width: 100%">
                        <span @click="addOutputPanel"
                          ><img
                            style="
                              width: 14px;
                              height: 14px;
                              vertical-align: bottom;
                              margin-right: 5px;
                            "
                            src="@/assets/images/add-line.svg"
                            alt=""
                          />{{ $t("addParam") }}</span
                        >
                      </el-button>
                    </div>
                  </div>
                  <!-- v-if="nodeEnd?.settings?.responseModel ===1" -->
                  <div class="plugin-bd-tit" style="margin-top: 20px;">
                    {{ $t("replyTemplate") }}
                  </div>
                  <!-- v-if="nodeEnd?.settings?.responseModel ===1" -->
                  <div style="margin: 10px 20px 0 0">
                    <el-input
                      type="textarea"
                      :rows="5"
                      :placeholder="`可以根据参数名，在此定义返回结果的格式。例如：今天{{ city }}的温度为{{ keyword }}`"
                      v-model="nodeEnd.settings.responseTemplate"
                      maxlength="2000"
                      show-word-limit
                    >
                    </el-input>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs></div
        ></el-col>
        <el-col :span="7">
          <div class="plugin-ft">
            <div class="plugin-ft-tit">
              <!-- <img src="@/assets/images/icon-contacts-fill.png"/> -->
              {{ $t("pluginDebug") }}
              <!-- <span class="clear" @click="clearAllParams"><i class="el-icon-takeaway-box"></i>{{ $t("clearContent") }}</span> --> 
            </div>
            <div class="plugin-ft-cont">
              <div class="radio-list">
                <span
                  class="radio-list-left"
                  :class="{ active: runType === '1' }"
                  @click="runType = '1'"
                  >{{ $t("input") }}</span
                >
                <span
                  class="radio-list-right"
                  :class="{ active: runType === '2' }"
                  @click="runType = '2'"
                  >{{ $t("output") }}</span
                >
              </div>
              <div class="typeIn" v-if="runType === '1'">
                <el-form
                  :model="ruleForm"
                  :rules="rulesTypeIn"
                  ref="ruleForm"
                  label-width="0"
                  class="demo-ruleForm params-list"
                >
                  <!-- <el-form-item label="" prop="rawQuery">
                                        <div class="params-list-item">
                                            <div class="params-list-item-name">
                                                rawQuery
                                                <span
                                                    class="params-list-item-type require"
                                                    >String</span
                                                >
                                            </div>
                                        </div>
                                        <el-input
                                            v-model="ruleForm.rawQuery"
                                        ></el-input>
                                    </el-form-item>
                                    <el-form-item label="">
                                        <div class="params-list-item">
                                            <div class="params-list-item-name">
                                                chatHistory
                                                <span
                                                    class="params-list-item-type"
                                                    >String</span
                                                >
                                            </div>
                                        </div>
                                        <el-input
                                            v-model="ruleForm.chatHistory"
                                        ></el-input>
                                    </el-form-item>
                                    <el-form-item label="">
                                        <div class="params-list-item">
                                            <div class="params-list-item-name">
                                                fileUrls
                                                <span
                                                    class="params-list-item-type"
                                                    >Array&lt;String&gt;</span
                                                >
                                            </div>
                                        </div>
                                        <div class="uploadOuter">
                                            <el-upload
                                                :action="actionUrl"
                                                :data="{ filePath: 'agent_source' }"
                                                :file-list="fileListLogo"
                                                :show-file-list="false"
                                                :limit="1"
                                                :on-remove="handleLogoRemove"
                                                :on-success="handleLogoSuccess"
                                            >
                                                <i class="el-icon-upload"></i>
                                                <div class="el-upload__text">
                                                    {{ $t("dragFileHere") }}，{{
                                                        $t("or")
                                                    }}<em>{{
                                                        $t("clickUpload")
                                                    }}</em>
                                                </div>
                                                <div
                                                    class="el-upload__tip"
                                                    slot="tip"
                                                >
                                                    支持.xlsx、.json、.jsonl、.png、.jpg、.jpeg、.pdf、.wav、.docx、.csv、.txt格式，仅支持单个文件且不超过20MB
                                                </div>
                                            </el-upload>
                                        </div>
                                    </el-form-item> -->
                  <el-form-item
                    label=""
                    v-for="(panel, index) in nodeStart?.output"
                    :key="index"
                  >
                    <div class="params-list-item">
                      <div class="params-list-item-name">
                        <span>
                          {{ panel.label }}
                          <span
                            class="params-list-item-type"
                            :class="{
                              required: panel.required,
                            }"
                            >{{ panel.type }}</span
                          >
                        </span>
                        <span
                          v-if="
                            panel.type == 'file' || panel.type == 'array[file]'
                          "
                        >
                        <el-radio-group v-model="panel.radio">
                          <el-radio label="1">文件</el-radio>
                          <el-radio label="2">地址</el-radio>
                        </el-radio-group>
                          
                        </span>
                      </div>
                      <PanelCompontent
                        v-if="panel.radio == '1'"
                        :panelType="panel.type"
                        :panelValue="panel.value"
                      ></PanelCompontent>
                      <el-input
                        v-else
                        v-model="panel.value"
                        type="textarea"
                        :rows="2"
                        placeholder=""
                        style="margin-top: 10px"
                      ></el-input>
                    </div>
                  </el-form-item>
                </el-form>
                <div style="width: 100%; text-align: center">
                  <el-button
                    class="run-btn"
                    @click="runApi"
                    :loading="runApiLoading"
                  >
                    <iconpark-icon
                      name="play-circle-line"
                      size="20"
                      color="#36383D"
                      style="margin-right: 6px"
                    ></iconpark-icon
                    >{{ $t("startRun") }}
                  </el-button>
                </div>
              </div>
              <div class="typeIn" v-if="runType === '2'">
                <div class="output" id="output">
                  <vue-json-pretty
                    v-if="isJsonString(outputJsonData)"
                    :showNum="false"
                    :data="JSON.parse(outputJsonData)"
                  />
                  <span
                    class="output-text"
                    v-if="outputJsonData"
                    v-for="(value, key) in outputJsonData"
                    :key="key"
                    ><strong>{{ key }}:</strong> {{ value }}</span
                  >
                  <div
                    v-else
                    style="width: 182px; height: 144px; margin: 20% auto"
                  >
                    <img
                      style="width: 100%; height: 100%"
                      src="@/assets/images/checkResult.png"
                      alt=""
                    />
                    <p
                      style="
                        font-size: 16px;
                        color: #494e57;
                        text-align: center;
                      "
                    >
                      点击开始运行查看结果
                    </p>
                  </div>
                </div>
                <!-- <div class="tips">
                                    <span
                                        ><img
                                            src="@/assets/images/icon-contacts-fill.png"
                                        />点击开始运行查看结果</span
                                    >
                                </div> -->
                <div style="width: 100%; text-align: center">
                  <el-button
                    class="run-btn"
                    @click="runApi"
                    :loading="runApiLoading"
                    ><iconpark-icon
                      name="play-circle-line"
                      size="20"
                      color="#36383D"
                      style="margin-right: 6px"
                    ></iconpark-icon
                    >{{ $t("startRun") }}</el-button
                  >
                </div>
              </div>
            </div>
          </div></el-col
        >
      </el-row>
    </div>
    <codeSetting v-if="appForm.type === 6" @updateDataList="updateDataList" @updateAppForm="updateAppForm" :editItem="editItem" :nodeCode="nodeCode" :nodeStart="nodeStart"></codeSetting>
    <el-drawer
      append-to-body
      :title="$t('editApi')"
      :visible.sync="apiDrawer"
      style="width: 100%"
      size="70%"
    >
      <ApiSetting
        v-if="apiDrawer"
        :nodeData="nodeApi"
        @getApiValidate="getApiValidate"
        @updateApi="updateApi"
      ></ApiSetting>
    </el-drawer>
    <Createplugin
      v-if="dialogVisible"
      :dialogVisible="dialogVisible"
      @confirmApplication="confirmApplication"
      @cancelCreateplugin="cancelCreateplugin"
      :params="paramsData"
      modelType="0"
      type="edit"
    ></Createplugin>
    <!-- 保存并发布 -->
    <el-drawer
      append-to-body
      title="发布至商店"
      :visible.sync="fabuDrawer"
      style="width: 100%"
      size="30%"
      custom-class="fabuDrawer"
    >
      <div v-loading="drawerLoading">
        <div class="fabuDrawer-head">
          <el-form :model="fabuForm">
            <el-form-item label="插件类型">
              <el-select
                v-model="fabuForm.labels"
                multiple
                :placeholder="$t('selectPlaceholder')"
                style="width: 100%"
              >
                <el-option
                  v-for="item in typeList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.name"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="更新说明">
              <el-input
                type="textarea"
                v-model="fabuForm.publishDesc"
                maxlength="500"
                show-word-limit
                :rows="5"
                placeholder="请输入"
              />
            </el-form-item>
          </el-form>
        </div>
        <div v-if="updateRecordList.length" class="label">更新记录</div>
        <ul v-if="updateRecordList.length" class="list">
          <li
            v-for="(item, index) in updateRecordList"
            :key="index"
            class="list-item"
          >
            <div class="drop"></div>
            <div class="line"></div>

            <div class="list-item-time">
              {{ item.createTime }}
              <span v-if="index == 0" class="current">当前</span>
            </div>
            <div class="list-item-content">{{ item.publishDesc }}</div>
          </li>
        </ul>
        <div class="footer">
          <el-button plain @click="fabuDrawer = false">取消</el-button>
          <el-button type="primary" @click="onSubmit">确定</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import ApiSetting from "@/views/toolManager/pluginManage/components/ApiSetting.vue";
import codeSetting from "@/views/toolManager/pluginManage/components/codeSetting.vue";
import {processParam} from '@/utils/tool'
import md5 from "js-md5";

import {
  pluginQueryDetail,
  runComponent,
  updateComponent,
  updateCodePlugin,
  apiValidate,
} from "@/api/workflow";
import { apiGetAppPublishRecordList, apiGetLabelTypes } from "@/api/app";
import VueJsonPretty from "vue-json-pretty";
import "vue-json-pretty/lib/styles.css";
import { fetchEventSource } from "@microsoft/fetch-event-source";
import PanelCompontent from "./panelCompontent.vue";
import Createplugin from "../Createplugin.vue";

export default {
  components: { ApiSetting, codeSetting, VueJsonPretty, PanelCompontent, Createplugin },
  data() {
    return {
      // radio: "1",
      panelTypeList: [
        {
          label: "object",
          value: "object",
        },
        {
          label: "string",
          value: "string",
        },
        {
          label: "number",
          value: "number",
        },
        {
          label: "integer",
          value: "integer",
        },
        {
          label: "file",
          value: "file",
        },
        {
          label: "boolean",
          value: "boolean",
        },
        {
          label: "array[object]",
          value: "array[object]",
        },
        {
          label: "array[integer]",
          value: "array[integer]",
        },
        {
          label: "array[boolean]",
          value: "array[boolean]",
        },
        {
          label: "array[string]",
          value: "array[string]",
        },
        {
          label: "array[file]",
          value: "array[file]",
        },
      ],
      statusValue: false,
      apiDrawer: false,
      loading: false,
      runApiLoading: false,
      outputJsonData: {},
      component: {},
      editItem: {},
      runType: "1",
      ruleForm: {
        name: "",
      },
      fileListLogo: [],
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      rulesTypeIn: {
        rawQuery: [
          {
            required: true,
            message: this.$t("enterRawQuery"),
            trigger: "blur",
          },
        ],
      },
      activeName: "first",
      editIndex: -1,
      formRefs: [],
      outputFormRefs: [],
      rules: {
        label: [
          {
            required: true,
            message: this.$t("enterVariableName"),
            trigger: "blur",
          },
        ],
        desc: [
          {
            required: true,
            message: this.$t("enterDescription"),
            trigger: "blur",
          },
        ],
        type: [
          {
            required: true,
            message: this.$t("selectFieldType"),
            trigger: "change",
          },
        ],
        maxLength: [
          {
            required: true,
            message: this.$t("maxLength"),
            trigger: "change",
          },
        ],
        required: [
          {
            required: true,
            message: this.$t("selectIsRequired"),
            trigger: "change",
          },
        ],
      },
      appForm: {},
      nodeApi: {},
      nodeCode: {},
      nodeStart: {},
      nodeEnd: {},
      outputText: "",
      selectedValue: [],
      selectedLabel: "",
      outputText: "",
      outputDataByText: "",
      defaultProps: {
        children: "children",
        label: "label",
      },
      dialogVisible: false,
      paramsData: {},
      fabuDrawer: false,
      fabuForm: {
        labels: [],
      },
      typeList: [],
      updateRecordList: [],
      drawerLoading: false,
    };
  },
  props: {
    appConfigForm: Object,
  },
  computed: {
    tenantId() {
      const tenantId = sessionStorage.getItem("user")
        ? JSON.parse(sessionStorage.getItem("user"))?.tenantId
        : "";
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
	}
  },
  watch: {
    // 监听 panels 数组的变化
  },

  beforeDestroy() {},
  mounted() {
    this.editItem = JSON.parse(JSON.stringify(this.appConfigForm));
    this.getPluginDetail(this.editItem.componentId);
  },
  methods: {
    confirmApplication(params) {
      this.dialogVisible = false;
      this.appForm.componentName = params?.name;
      this.appForm.componentDesc = params?.description;
      this.appForm.icon = params?.icon;
      this.appForm.labels = params?.labels;
    },
    cancelCreateplugin() {
      this.dialogVisible = false;
    },
    editInfo() {
      this.dialogVisible = true;
    },
    handleChange(value, index) {
      this.selectedValue = value;
      this.selectedLabel = this.getLabelByValue(
        this.nodeApi?.settings.responseBody,
        value
      );
    },
    getLabelByValue(options, values) {
      let labels = [];
      let currentOptions = options;
      for (let val of values) {
        const foundOption = currentOptions.find(
          (option) => option.value === val
        );
        if (foundOption) {
          labels.push(foundOption.label);
          currentOptions = foundOption.children || [];
        }
      }

      return labels.join(" / ");
    },
    getPluginDetail(componentId) {
      this.loading = true;
      pluginQueryDetail({
        componentId: componentId,
      }).then((res) => {
        this.loading = false;
        if (res.data) {
          this.appForm = res.data;
          this.paramsData = {
            name: this.appForm.componentName,
            description: this.appForm.componentDesc,
            icon: this.appForm.icon,
            labels: this.appForm.labels,
          };
          this.nodeStart = res.data?.nodes?.find((item) => item.nodeType === 1)
          // if(this.nodeStart?.output?.length) {
          //   this.nodeStart.output.forEach(item => {
          //     if(!item?.radio) {
          //       this.$set(item, 'radio', '1')
          //     }
          //   })
          // }
          let nodeEnd = res.data?.nodes?.find((item) => item.nodeType === 10)
          nodeEnd.settings = processParam(nodeEnd.settings);
          this.nodeEnd = nodeEnd
          if(this.appForm.type === 1){
            let nodeApi = res.data?.nodes[1];
            let endInpt = nodeApi?.output;
            endInpt = endInpt.map((item) => {
              if (item.valueType === "reference" && item.value) {
                const cleanedString = item.value.replace(/\$\{|\}/g, ""); // 去掉 ${ 和 }
                const resultArray = cleanedString.split(".");
                item.value = resultArray;
              }
              return item;
            });
            nodeApi.output = endInpt;
            nodeApi.settings = processParam(nodeApi.settings);
            this.nodeApi = nodeApi;
          } else {
            let nodeCode = res.data?.nodes?.find((item) => item.nodeType === 27);
            nodeCode.settings = processParam(nodeCode.settings);
            this.nodeCode = nodeCode
          }
        } else {
          this.list = {};
        }
      });
    },
    removeDollarBraces(str) {
      // 正则表达式匹配 ${...} 格式的字符串
      const regex = /\$\{([^}]*)\}/g;
      // 使用 replace 方法替换匹配的部分
      return str.replace(regex, "$1");
    },
    arrayToNestedJson(array) {
      const result = {};
      let arr = JSON.parse(JSON.stringify(array));
      arr?.forEach((item) => {
        if (item.children && item.children.length > 0) {
          result[item.name] = this.arrayToNestedJson(item.children);
        } else {
          // result[item.name] = item.value;
          if (item.type === "number") {
            result[item.name] = Number(item.value);
          } else if (item.type === "null") {
            result[item.name] = null;
          } else {
            result[item.name] = item.value;
          }
        }
      });
      return result;
    },
    updateApi(settings, inputs, closeDrawerFlag) {
      let manageAccessToken = sessionStorage.getItem("manageAccessToken");
      this.nodeApi.settings = settings;
      // this.nodeApi.input = inputs.map(el => {
      //   return {
      //     "label": el.name,
      //     "variable": el.name,
      //     "type": "string",
      //     "desc": "",
      //     nodeId: this.nodeApi?.nodeId,
      //     referenceNodeId: this.nodeStart?.nodeId,
      //     "referenceNodeName": "开始",
      //     "valueType": /\$\{[^}]*\}/.test(el.value) ? 'reference' : 'string',
      //     "value": this.removeDollarBraces(el.value),
      //     "maxLength": 20,
      //     "required": true,
      //     "direction": 0
      //   }
      // })
      if (closeDrawerFlag) {
        this.apiDrawer = false;
      }
    },
    updateAppForm(data){
      let curData = JSON.parse(data);
      this.nodeCode.settings = curData;
    },
    getApiValidate() {
      this.apiDrawer = false
      // let params = JSON.parse(JSON.stringify(this.nodeApi));
      // params.input = []
      // if(params.settings && params.settings.url){
      //   apiValidate(params).then((res) => {
      //     this.$EventBus.$emit("apiValidate", res);
      //   });
      // }
    },
    clearAllParams() {
      this.nodeStart?.output.map((el) => (el.value = null));
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const outputElement = document.getElementById("output");
        if (outputElement) {
          outputElement.scrollTop = outputElement.scrollHeight;
        }
      });
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
      this.nodeStart?.output?.forEach((el) => {
        params[el.label] = el.value;
      });
      let paData = {
        componentId: this.editItem.componentId,
        inputs: params,
        clientId: Math.floor(Math.random() * 10000000000),
      };
      let ctrlAbout = new AbortController();
      let manageAccessToken = sessionStorage.getItem("manageAccessToken");
      let timer = new Date().getTime();
      fetchEventSource(`${process.env.VUE_APP_BASE_API}/workflow/dialogueRun`, {
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
          if (!data.nodeId) {
            that.outputJsonData = data.output;
            that.scrollToBottom();
          }
        },
        onerror() {
          console.log(11);
          // 服务异常
          console.log("shibai");
        },
        onclose() {
          that.runApiLoading = false;
          // 服务关闭
          console.log("guanbi");
          that.$EventBus.$emit("apiEnding", that.outputJsonData);
          ctrlAbout.abort();
        },
      });
    },
    handleLogoRemove(file, fileList) {
      this.uploadBtnLogo = false;
      this.appForm.logo = require("@/assets/images/applicationlogo.svg");
      this.fileListLogo = [];
    },
    handleLogoSuccess(response, file, fileList) {
      if ((response.code = "000000")) {
        this.uploadBtnLogo = true;
        this.appForm.logo =
          response.data && response.data[0] ? response.data[0].url : "";
        this.fileListLogo = [];
      } else {
        this.uploadBtnLogo = false;
        this.appForm.logo = "";
        this.fileListLogo = [];
      }
    },
    copyText() {
      let text = this.$refs.copyLink.innerHTML;
      this.exeCommandCopyText(text);
      this.$message({
        message: this.$t("copySuccessed"),
        type: "success",
      });
    },
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
    addPanel() {
      const newPanel = {
        label: "parameter",
        variable: "parameter",
        type: "string",
        desc: this.$t("paramDescription"),
        nodeId: this.nodeStart?.nodeId,
        referenceNodeName: this.$t("start"),
        maxLength: 20,
        valueType: "reference",
        required: true,
        direction: 1,
        radio: '1'
      };
      if (this.nodeStart?.output?.length > 0) {
        this.nodeStart?.output.push(newPanel);
      } else {
        this.nodeStart.output = [newPanel];
      }
      this.formRefs.push(null); // 初始化表单引用
    },
    removePanel(index) {
      this.nodeStart.output.splice(index, 1);
      this.formRefs.splice(index, 1);
    },
    addOutputPanel() {
      const newPanel = {
        label: "parameter",
        variable: "parameter",
        type: "string",
        desc: this.$t("paramDescription"),
        nodeId: this.nodeApi?.nodeId,
        referenceNodeId: this.nodeApi?.nodeId,
        referenceNodeName: "api",
        maxLength: 20,
        valueType: "string",
        required: true,
        direction: 1,
        radio: '1'
      };
      if (this.nodeApi?.output?.length > 0) {
        this.nodeApi?.output.push(newPanel);
      } else {
        this.nodeApi.output = [newPanel];
      }
      this.outputFormRefs.push(null); // 初始化表单引用
    },
    replaceTemplate(template, data) {
      return template.replace(/\$\{(\w+)\}/g, (match, key) => {
        return data[key] || match;
      });
    },
    removeOutputPanel(index) {
      this.nodeApi.output.splice(index, 1);
      this.outputFormRefs.splice(index, 1);
    },
    handleClick(tab, event) {
      console.log(tab, event);
    },
    closeDialog() {
      this.$emit("configCloseDialog", false);
    },
    /**
     * 在嵌套对象数组中查找指定路径的数据类型
     * @param {Array} data - 嵌套对象数组
     * @param {Array} path - 查找路径数组，如 ['data', 'taskId']
     * @returns {string|null} 返回找到的数据类型，如果找不到则返回null
     */
    findDataTypeInNestedArray(data, path) {
        // 如果路径为空或数据为空，返回null
        if (!path || path.length === 0 || !data || data.length === 0) {
            return null;
        }
        
        // 从第一个路径元素开始查找
        let current = data.find(item => item.name === path[0]);
        if (!current) {
            return null;
        }
        
        // 如果路径只有1个元素，直接返回当前项的type
        if (path.length === 1) {
            return current.type || null;
        }
        
        // 递归处理剩余路径
        const remainingPath = path.slice(1);
        return this.findDataTypeInNestedArray(current.children || [], remainingPath);
    },
    getApiParams(){
      let params = JSON.parse(JSON.stringify(this.appForm));
      let startOutput = JSON.parse(JSON.stringify(params.nodes[0].output));
      let output1 = JSON.parse(JSON.stringify(params.nodes[1].output));
      let responseBody = JSON.parse(JSON.stringify(params.nodes[1].settings.responseBody));
      console.log(JSON.stringify(responseBody),"responseBody")
      let output2 = JSON.parse(JSON.stringify(params.nodes[1].output));
      startOutput = startOutput.map((el) => {
        el.type = el.type;
        el.value = el.label;
        el.nodeId = params.nodes[1].nodeId;
        el.referenceNodeId = params.nodes[0].nodeId;
        el.valueType = "reference";
        el.direction = 0;
        return el;
      });
      params.nodes[1].input = startOutput;
      output1 = output1.map((el) => {
        el.type = "string";
        //引用类型根据responseBody获取类型
        if(el.valueType === 'reference'){
          el.type = this.findDataTypeInNestedArray(responseBody, el.value)
        }
        el.value = el.label;
        el.nodeId = params.nodes[2].nodeId;
        el.referenceNodeId = params.nodes[1].nodeId;
        el.direction = 0;
        return el;
      });
      params.nodes[2].input = output1;
      output2 = output2.map((el) => {
        el.type = "string";
        //引用类型根据responseBody获取类型
        if(el.valueType === 'reference'){
          el.type = this.findDataTypeInNestedArray(responseBody, el.value)
        }
        el.value = Array.isArray(el.value)
          ? "${" + el.value.join(".") + "}"
          : el.value;
          el.variable = el.label
          el.value  =  el.value === "${}" ? '' : el.value
        return el;
      });
      params.nodes[1].output = output2;
      params.tenantId = this.tenantId;
      params.clickPublish = false;
      return params
    },
    updateDataList (data)
    {
      let curData = JSON.parse(data);
      let data0 = curData[0]
      this.nodeStart.output = curData.map((el) => {
       let obj = {}
       if(el.direction){
        obj = el
       } else {
         obj = {
          ...data0,
          label: el.label,
          desc: el.desc,
          paramId: "",
          type: el.type,
          value: el.value,
          required: el.required,
          enabled: el.enabled,
        }
       }
        return obj;
      });
    },
    getCodeParams(){
      let params = JSON.parse(JSON.stringify(this.appForm));
      let nodeStart = params.nodes.find((item) => item.nodeType === 1)
      let endStart = params.nodes.find((item) => item.nodeType === 10)
      let nodeCode = params.nodes.find((item) => item.nodeType === 27)
      let inputs = nodeStart.output.map((el) => {
        return {
          "label": el.label,
          "variable": el.label,
          "type": el.type,
          "desc": el.desc,
          "value": '',
          "valueType": el.type,
          "required": true,
          "direction": 1
        }
      })
      let outputs = endStart.input.map((el) => {
        return {
          "label": el.label,
          "variable": el.label,
          "type": el.type,
          "desc": el.desc,
          "value": '',
          "valueType": el.type,
          "required": false,
          "direction": 1
        }
      })
      let params2 =  {
        ...params,
        "componentId": params.componentId,
        "inputs": inputs,
        "outputs": outputs,
        "url": nodeCode.settings.url,
        "code": nodeCode.settings.code,
        "language": nodeCode.settings.language,
        "tenantId": this.tenantId,
        "clickPublish": false,
      }
      return params2
    },
    submitAddApp(isFabu) {
      let params = {}
      if (this.appForm.type === 1) {
        params = this.getApiParams()
      } else if (this.appForm.type === 6){
        params = this.getCodeParams()
      }
      // if(this.appForm.type === 1 && !params.nodes[1].output[0].type){
      //   this.$message({
      //     type:"warning",
      //     message:"请配置响应参数"
      //   })
      //   return
      // }
      if (isFabu) {
        this.drawerLoading = true;
      } else {
        this.loading = true;
      }
      console.log(params,"params");
      
      const dataParams = {
        ...params,
		allVisible:this.appForm.allVisible,
        labels: Array.isArray(params.labels)
          ? params.labels.join(",")
          : params.labels,
      };
      if (isFabu) {
        dataParams.publishAppStore = 2;
        dataParams.publishDesc = this.fabuForm.publishDesc;
        dataParams.clickPublish = true;
        dataParams.labels = Array.isArray(this.fabuForm.labels)
          ? this.fabuForm.labels?.join(",")
          : this.fabuForm.labels;
      }
      let api = this.appForm.type === 1 ? updateComponent : updateCodePlugin
      api(dataParams)
        .then((res) => {
          if (isFabu) {
            this.drawerLoading = false;
          } else {
            this.loading = false;
          }
          if (res.code == "000000") {
            if (isFabu) {
              this.closeDialog();
              this.$message.success(res.msg);
            }
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        })
        .finally(() => {
          if (isFabu) {
            this.drawerLoading = false;
          } else {
            this.loading = false;
          }
          //this.getPluginDetail(this.editItem.componentId);
        });
    },
    openFabuDrawer() {
      this.fabuDrawer = true;
      this.getLabelTypes();
      this.getAppPublishRecordList();
    },
    // 分类
    async getLabelTypes() {
      let res = await apiGetLabelTypes({ type: 1 });
      if (res.code == "000000") {
        this.typeList = res.data || [];
        this.typeList = this.typeList.filter((item) => item.name != "全部");
        this.fabuForm.labels = Array.isArray(this.editItem.labels)
          ? this.editItem.labels : this.editItem?.labels ? this.editItem.labels?.split(',')
          : [];
      }
    },
    // 更新记录
    async getAppPublishRecordList() {
      const params = {
        messageSource: 2,
        applicationId: this.editItem.componentId,
      };
      let res = await apiGetAppPublishRecordList(params);
      if (res.code == "000000") {
        this.updateRecordList = res.data?.records || [];
      }
    },
    onSubmit() {
      this.submitAddApp(true);
    },
  },
};
</script>
<style lang="scss">
.fabuDrawer {
  .el-drawer__header {
    height: 72px;
    margin-bottom: 0;
    padding: 32px 32px 8px;
    font-family: MiSans, MiSans;
    font-weight: 600;
    font-size: 20px;
    color: #36383d;
    line-height: 32px;
  }
  .el-drawer__body {
    padding: 0 32px;
    position: relative;
  }
  .label {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #36383d;
    line-height: 32px;
    display: flex;
    align-items: center;
    margin-top: 32px;
    margin-bottom: 20px;
    &::before {
      content: "";
      display: inline-block;
      width: 4px;
      height: 18px;
      background: #1747e5;
      border-radius: 0px 2px 2px 0px;
      margin-right: 6px;
    }
  }
  .list {
    height: calc(100vh - 420px);
    overflow-y: auto;
    &-item {
      margin-bottom: 8px;
      padding-left: 20px;
      padding-bottom: 24px;
      position: relative;
      .drop {
        position: absolute;
        top: 6px;
        left: 0;
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: #1747e5;
        border: 1px solid #ffffff;
      }
      .line {
        position: absolute;
        top: 6px;
        left: 3px;
        width: 2px;
        height: 90%;
        background: rgba(23, 71, 229, 0.1);
      }
      .current {
        padding: 2px 8px;
        margin-left: 8px;
        background: linear-gradient(
          270deg,
          rgba(142, 101, 255, 0.2) 0%,
          rgba(23, 71, 229, 0.2) 100%
        );
        border-radius: 10px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 12px;
        color: #36383d;
      }
      &-time {
        height: 20px;
        font-weight: 600;
        font-size: 14px;
        color: #36383d;
        line-height: 20px;
        margin-bottom: 12px;
      }
      &-content {
        width: 468px;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 24px;
      }
    }
  }
  .footer {
    position: absolute;
    bottom: 0;
    right: 0;
    height: 88px;
    padding: 16px 32px 32px 0;
  }
}
</style>
<style lang="scss" scoped>
.flex {
  display: flex;
}
.pluginDetails-outerDialog {
  display: flex;
  height: 100vh;
  width: 100vw;
  flex-direction: column;
  overflow: hidden;
  .dialogPower {
    width: 100%;
    height: 100%;
    background: #F7F8FA;
    position: relative;
    // margin: 16px 32px;

    .left-content {
      height: calc(100vh - 230px);
      overflow-y: auto;
    }
    ::-webkit-scrollbar {
      width: 0;
    }

    ::v-deep .el-textarea__inner {
      font-family: MiSans, MiSans;
    }
  }
  .previewDebugging {
    flex: 1;
    background: #f2f5fa;
    border-radius: 0px 8px 8px 0px;
    border: 1px solid #e1e4eb;
    padding: 28px 32px 28px 32px;
    box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.1);
    position: relative;
  }
  .headBar {
    background: #FFFFFF !important;
    padding: 16px 32px 15px !important;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 0px !important;
    width: 100%;
    border-bottom: 1px solid rgba(0, 0, 0, 0.12);
    img {
      width: 15px;
      height: 15px;
    }
    .leftSlide {
      display: flex;
      justify-content: space-between;
      align-items: center;
      > img {
        margin-right: 16px;
        cursor: pointer;
      }
      .avatar {
        width: 40px;
        height: 40px;
        margin: 0 12px 0 14px;
      }
      .titleIcon {
        .txt1 {
          font-family: MiSans, MiSans;
          font-weight: 600;
          font-size: 18px;
          color: #36383d;
          line-height: 24px;
        }
        .txt2 {
          margin-top: 4px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #828894;
          line-height: 20px;
        }
      }
    }
    .rightSlide {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .preview {
        line-height: 36px;
        margin-right: 28px;
        cursor: pointer;
        > span {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 16px;
          color: #3666ea;
          line-height: 24px;
          text-align: left;
          font-style: normal;
          text-transform: none;
        }
        > img {
          margin-right: 5px;
        }
        > span,
        > img {
          vertical-align: middle;
        }
      }
      .btn {
        height: 40px;
        color: #3666ea;
        border: 1px solid #3666ea;
        border-radius: 4px;
        img {
          margin-right: 5px;
        }
        img,
        span {
          vertical-align: middle;
        }
      }
      .btns {
        color: #fff;
        height: 40px;
        background: #1747e5;
        border-radius: 4px;
        img {
          margin-right: 5px;
        }
        img,
        span {
          vertical-align: middle;
        }
      }
      ::v-deep .el-button {
        span {
          display: flex;
          align-items: center;
          height: 100%;
        }
      }
      ::v-deep .el-button--primary.is-plain {
        border-color: rgb(77, 119, 239);
      }
      ::v-deep .el-button--default.is-plain {
        &:hover {
          border-color: #c9ccd1;
        }
      }
    }
  }
}

.el-row {
  margin-bottom: 20px;
  &:last-child {
    margin-bottom: 0;
  }
}
.el-col {
  border-radius: 4px;
}
.bg-purple-dark {
  background: #99a9bf;
}
.bg-purple {
  background: #d3dce6;
}
.bg-purple-light {
  background: #e5e9f2;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}
.plugin {
  position: relative;
  color: #383d47;
  // height: calc(100vh - 95px);
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
    padding: 24px 32px;
    background: #F7F8FA;
    ::v-deep .el-tabs {
      .el-tabs__item {
        font-size: 16px;
        color: #383d47;
        padding-right: 6px;
      }
      .el-tabs__item.is-active {
        color: #603eca;
      }
      .el-tabs__active-bar {
        background-color: #603eca;
      }
      .el-tabs__nav-wrap::after {
        height: 1px;
        background: rgba(0, 0, 0, 0.12);
      }
      .el-tabs__content {
        max-height: calc(100vh - 182px);
        overflow: auto;
        background: #fff;
        padding: 16px;
      }
      .el-tabs__header {
        margin: 0 0 15px 16px;
      }
      .plugin-bd[data-v-34b0738c] .el-tabs .el-tabs__item.is-active {
        color: #603eca;
      }
      .el-tabs__nav-wrap::after {
        background: none;
      }
    }
    &-tit {
      font-size: 16px;
      color: #383d47;
      line-height: 28px;
      position: relative;
      padding: 0 0 0 10px;
      margin: 0 0 12px;
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
    &-add {
      border: 1px solid #d5d8de;
      // width: 100%;
      width: 104px;
      height: 40px;
      line-height: 40px;
      text-align: left;
      cursor: pointer;
      padding: 0 6px;
    }
    &-first {
      position: relative;
      .drawer-content {
        padding: 0 20px 0 0;
        overflow-y: scroll;
        max-height: 500px;
      }
    }
    ::v-deep .el-form {
      border-radius: 4px;
      // border: 1px solid #e1e4eb;
      padding: 26px 16px 16px;
      margin: 0 0 16px 0;
      position: relative;
      i.hide {
        position: absolute;
        right: 42px;
        top: 4px;
        cursor: pointer;
        font-size: 20px;
      }
      i.delete {
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
          // font-weight: bold;
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
    &-second {
      .apiUrl {
        background: #f2f4f7;
        width: 100%;
        line-height: 36px;
        border-radius: 2px;
        padding: 12px 16px;
        color: #494e57;
        .title {
          display: flex;
          justify-content: space-between;
        }
        .editHandler {
          cursor: pointer;
        }
        .font {
          color: #3fb816;
          span {
            font-size: 30px;
            vertical-align: middle;
          }
        }
      }
      .operationIcon {
        padding-right: 5px;
        font-size: 20px;
      }
    }
    &-third {
      &-hd {
        margin: 20px 0 30px;
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
        width: 98%;
      }
    }
  }
  &-ft {
    padding: 24px 0 0 0;
    border-left: 1px solid #e1e4eb;
    position: relative;
    background: #fff;
    ::v-deep .run-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 40px;
      margin-top: 24px;
      border-radius: 2px;
      span {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100%;
      }
    }
    &-tit {
      position: relative;
      padding: 0 32px 0 24px;
      font-size: 20px;
      color: #494e57;
      height: 40px;
      line-height: 40px;
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
      height: calc(100vh - 144px);
      padding: 0 32px 0 24px;
      .radio-list {
        margin-top: 18px;
        text-align: center;
        background: #f0f1f5;
        padding: 2px;
        height: 38px;
        line-height: 38px;
        span {
          cursor: pointer;
          font-size: 16px;
          color: #828894;
          background: transparent;
          border-radius: 4px;
          display: inline-block;
          width: 50%;
          text-align: center;
          height: 34px;
          position: relative;
          &.active {
            background: #ffffff;
            z-index: 1;
            color: #36383d;
            border-radius: 2px;
            height: 34px;
          }
        }
        &-left {
          // left: 10px;
        }
        &-right {
          // right: 10px;
        }
      }
      .output {
        background: #f7f8fa;
        border-radius: 4px;
        height: calc(100vh - 360px);
        margin-top: 24px;
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
          margin: 0 auto;
        }
      }
    }

    ::v-deep .el-form {
      margin: 0 0 16px 0;
      position: relative;
      height: calc(100vh - 310px);
      overflow-y: scroll;
    }
    .uploadOuter {
      background: #f9fafc;
      border-radius: 4px;
      border: 1px solid rgba(0, 0, 0, 0.12);
      position: relative;
      padding: 20px 20px 20px 110px;
      text-align: left;
      line-height: 24px;
      ::v-deep .el-upload {
        text-align: left;
      }
      .el-icon-upload {
        position: absolute;
        font-size: 40px;
        left: 50px;
        transform: translate(0, -70%);
        top: 50%;
        color: #b4bccc;
      }
      .el-upload__text {
        text-align: left;
        font-size: 16px;
        em {
          color: #3666ea;
        }
      }
      .el-upload__tip {
        font-size: 14px;
        color: #b4bccc;
        margin: 0;
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
          display: flex;
          justify-content: space-between;
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
  .el-radio .el-radio__input.is-checked + .el-radio__label {
    color: #494e57;
  }
}
</style>
