<template>
  <div class="app-details">
    <!-- 查看应用详情 -->
    <el-dialog
      :visible.sync="previewDialogVisible"
      width="100%"
      fullscreen
      :show-close="false"
      class="flexDialog preViewDialog"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="app-details-content">
        <div class="headBar">
          <div class="leftSlide">
            <div class="closeImg"  @click="closePriview">
              <img
              src="@/assets/images/arrow-go-back-fill.svg"
            />
            </div>
            <div class="titleIcon">
              <div class="title-headImg" v-if="previewItem.facadeImageUrl">
              <img :src="previewItem.facadeImageUrl" alt="">
            </div>
            <div class="title-info">
              <p>
                {{ previewItem.applicationName }}
              </p>
              <p :title="previewItem.introduce">{{ previewItem.introduce }}</p>
            </div>
            </div>
          </div>
          <div class="centerSlide">
            <el-tabs type="card" v-model="activeName" @tab-click="handleClick">
              <el-tab-pane
                v-if="permissions('overview')"
                :label="$t('overview')"
                name="first"
              ></el-tab-pane>
              <!-- <el-tab-pane
                v-if="permissions('analysis')"
                :label="$t('analysis')"
                name="sixth"
              ></el-tab-pane> -->
              <!-- <el-tab-pane
                v-if="permissions('setUp')"
                :label="$t('settings')"
                name="second"
              ></el-tab-pane> -->
              <el-tab-pane
                v-if="permissions('hitTesting')"
                :label="$t('hitTest')"
                name="third"
              ></el-tab-pane>
              <el-tab-pane
                v-if="permissions('dialogueLog')"
                :label="$t('dialogLog')"
                name="fourth"
              ></el-tab-pane>
              <!-- <el-tab-pane
                :label="$t('intelligentDistribution')"
                name="eighth"
                v-if="permissions('intelligentDistribution')"
              ></el-tab-pane> -->
              <!-- <el-tab-pane
                v-if="permissions('answerReview')"
                :label="$t('answerReview')"
                name="fifth"
              ></el-tab-pane> -->
              <el-tab-pane
                v-if="permissions('configManage')"
                :label="$t('configurationManagement')"
                name="seventh"
              ></el-tab-pane>
			  <el-tab-pane
			    :label="$t('businessData')"
			    name="businessData"
			  ></el-tab-pane>
            </el-tabs>
          </div>
          <div class="rightSlide" v-permission="'editeApp'">
            <el-button class="btn" @click="editApp(1)">
              <iconpark-icon class="edit-icon" color="#1747E5" name="edit-box-line"></iconpark-icon>
              <span>{{ $t("edit") }}</span>
            </el-button>
          </div>
        </div>
        <div
          class="app-preview-content"
        >
            <div class="preview-content" v-if="activeName == 'first'">
              <div v-if="previewItem.publishStatus == 1 || previewItem.publishStatus == 2">
                <div class="tabContentTitle">
                  <span class="line"></span>
                  <span>{{ $t("appInfo") }}</span>
                </div>
                <div class="tabContents">
                  <div v-if="!['text-agent'].includes(previewItem.type)" class="contentInner">
                    <div class="title">
                      <span>{{ $t("publicPublish") }}</span>
                      <!-- <el-switch
                        disabled
                        v-model="previewItem.publishStatus"
                        :active-value="'1'"
                      ></el-switch> -->
                    </div>
                    <p class="desc">{{ $t("publicAccessLink") }}</p>
                    <div class="secondLine">
                      <p class="inp">
                        <span>{{ previewItem.clientLink }}</span>
                        <!-- <img src="@/assets/images/shuaxin.svg"> -->
                      </p>
                      <el-button @click="cpoyText(previewItem.clientLink)">
                        <img src="@/assets/images/copy-line.svg" />
                        <span>{{ $t("copyLink") }}</span>
                      </el-button>
                    </div>
                    <div class="thirdLine">
                      <el-button type="primary" @click="openPreview">
                        <img src="@/assets/images/computer.svg" />
                        <span>{{ $t("demo") }}</span>
                      </el-button>
                      <el-button @click="devBuilding">{{
                        $t("embedThirdParty")
                      }}</el-button>
                      <el-button @click="devBuilding">{{
                        $t("accessRestrictions")
                      }}</el-button>
                    </div>
                  </div>
                  <div class="contentInner">
                    <div class="title">
                      <span>{{ $t("privatePublish") }}</span>
                      <!-- <el-switch
                        v-model="previewItem.publishStatus"
                        :active-value="'2'"
                        disabled
                      ></el-switch> -->
                    </div>
                    <p class="desc">{{ $t("apiAccessCredentials") }}</p>
                    <div class="secondLine">
                      <p class="inp">{{ previewItem.apiKey }}</p>
                      <el-button @click="cpoyText(previewItem.apiKey)">
                        <img src="@/assets/images/copy-line.svg" />
                        <span>{{ $t("copy") }}</span>
                      </el-button>
                    </div>
                    <div class="thirdLine">
                      <el-button class="borderBtn" @click="devBuilding">
                        <img src="@/assets/images/pie-chart.svg" />
                        <span>{{ $t("usage") }}</span>
                      </el-button>
                      <el-popover placement="right" trigger="hover">
                        <p>{{ previewItem.apiSecret }}</p>
                        <el-button slot="reference">{{
                          $t("viewKey")
                        }}</el-button>
                      </el-popover>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="no-data" style="margin-top: 130px;">
                <img src="@/assets/images/un-publish.png" alt="" style=" width: 172px; height: auto; "/>
                 <div class="txt1" style="margin:20px 0 52px 0; font-size: 16px; color: #383838;">请先完成应用发布</div>
                 <el-button type="primary"  class="addButton" style="width: 240px;"
                 size="large" @click="editApp(3)">
                  <span>去发布</span>
                 </el-button>
            </div>
            </div>
            <div class="preview-content" v-if="activeName == 'sixth'">
              <!-- 分析 -->
              <Analysis
                v-if="activeName == 'sixth'"
                ref="Analysis"
                :data="previewItem"
              />
            </div>
            
            
            <div class="preview-content" v-if="activeName == 'second'"></div>
            <div class="preview-content third" v-if="activeName == 'third'">
              <score-view
                :applicationId="previewItem.applicationId"
              ></score-view>
            </div>
            
            
            <div class="preview-content" v-if="activeName == 'fourth'">
              <div style="margin-bottom: 20px">
                <el-form :inline="true" class="demo-form-inline">
                  <el-form-item :label="$t('questionTime')">
                    <el-date-picker
                      v-model="dateRange"
                      type="datetimerange"
                      format="yyyy-MM-dd HH:mm:ss"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      align="right"
                      unlink-panels
                      range-separator="-"
                      :start-placeholder="$t('startDate')"
                      :end-placeholder="$t('endDate')"
                      :picker-options="pickerOptions"
                      :default-time="['00:00:00', '23:59:59']"
                      clearable
                      style="width: 356px;"
                    ></el-date-picker>
                  </el-form-item>
                  <el-form-item :label="$t('verificationDepartment')">
                    <el-cascader
                      popper-class="cascaderStyle"
                      :options="treeData"
                      :props="{
                        value: 'deptId',
                        label: 'deptName',
                        checkStrictly: true,
                      }"
                      clearable
                      filterable
                      v-model="verifyDeptId"
                      :show-all-levels="false"
                      style="width: 200px;"
                    ></el-cascader>
                  </el-form-item>
                 <!-- <el-form-item :label="$t('verificationStatus')">
                    <el-select
                      style="width: 148px;"
                      v-model="verifyStatus"
                      :placeholder="$t('selectPlaceholder')"
                      clearable
                      filterable
                      v-if="activeName == 'fourth'"
                    >
                      <el-option
                        v-for="item in verifyStatusColumns"
                        :key="item.value"
                        :label="item.text"
                        :value="item.value"
                      ></el-option>
                    </el-select>
                  </el-form-item> -->
                  <el-form-item :label="$t('unanswerable')" v-if="false">
                    <el-select
                      style="width: 148px;"
                      v-model="noAnswerFlag"
                      :placeholder="$t('selectPlaceholder')"
                      clearable
                      filterable
                      v-if="activeName == 'fourth'"
                    >
                      <el-option
                        v-for="item in unanswerableList"
                        :key="item.value"
                        :label="item.text"
                        :value="item.value"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                  <!-- <el-form-item label="赞踩情况">
                    <el-select
                      style="width: 148px;"
                      v-model="feedbackType"
                      :placeholder="$t('selectPlaceholder')"
                      clearable
                      filterable
                      v-if="activeName == 'fourth'"
                    >
                      <el-option
                        v-for="item in likeAndStepOnList"
                        :key="item.value"
                        :label="item.text"
                        :value="item.value"
                      ></el-option>
                    </el-select>
                  </el-form-item> -->
                  <el-form-item :label="$t('userType')" v-if="false">
                    <el-select
                      style="width: 148px"
                      v-model="userType"
                      :placeholder="$t('selectPlaceholder')"
                      clearable
                      filterable
                      v-if="activeName == 'fourth'"
                    >
                      <el-option
                        v-for="item in userTypeList"
                        :key="item.value"
                        :label="item.text"
                        :value="item.value"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="$t('sourceUser')" v-if="false">
                    <el-input
                      :placeholder="$t('pleaseEnter')"
                      style="width: 276px;"
                      v-model="userName"
                      clearable
                      @keydown.native.enter="searchDialog"
                    >
                    </el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-input
                      :placeholder="$t('enterKeywordForQuestionOrAnswer')"
                      style="width: 322px; margin-right: 16px;"
                      v-model="text"
                      clearable
                    >
                    </el-input>
                    <el-button type="primary" @click="searchDialog">{{
                      $t("search")
                    }}</el-button>
                    <el-button @click="resetSearch" plain>{{
                      $t("reset")
                    }}</el-button>
					<el-button
					  type="warning"
					  @click="exportDialog"
					  v-if="activeName == 'fourth'"
					  plain
					  >{{ $t("export") }}</el-button
					>
					<el-popover
					  style="border: none;"
					  placement="bottom-start"
					  :visible-arrow="false"
					  trigger="click" v-model="visiblePopover">
					  <el-button
					  slot="reference"
					  type="text"
					  style="margin: 0 10px;"
					  >
						  <iconpark-icon style="margin: 0px 5px 0 0;vertical-align: bottom;" name="settings-4-line" size="16"  color="#1747E5"></iconpark-icon>
					      <span style="color: #1747E5;">调优设置</span></el-button
					>
					
					<div class="knowledgeset-ctn">
					  <div class="knowledgeset-title-ctn">
					    <div class="knowledgeset-title">
					      调优设置
					    </div>
					    
					  </div>
					
					  <div class="knowledgeset-options">
					    	    
					    <div class="knowledgeset-options-list">
					      <div class="left-ctn-tiaoyou">
					        <div class="left-ctn-tiaoyou-title">开启调优</div>
					        <div class="left-ctn-tiaoyou-words">允许修改问题的答案并添加到选定卸讯库约QA对</div>
					      </div>
					      <div class="right-ctn">
					        <el-switch v-model="modelAnswerFlag" active-color="#1747E5"
                inactive-color="#B4BCCC" />
					        
					      </div>
					    </div>
						<div class="knowledgeset-options-list" v-if="modelAnswerFlag">
						  <div class="left-ctn-tiaoyou">
						    <div class="left-ctn-tiaoyou-title">选择知识库<span style="color: red;font-weight: normal;">*</span></div>
						   
						  </div>						  
						</div>
						<div class="knowledgeset-options-list" v-if="modelAnswerFlag">
						 
						  <div class="right-ctn" style="width: 100%;">
						    <el-select
						      style="width: 100%;"
						      v-model="knowledgeId"
						      :placeholder="$t('selectPlaceholder')"
						      clearable
						      filterable
						    
						    >
						      <el-option
						        v-for="item in knowledgeIds"
						        :key="item.knowledgeId"
						        :label="item.knowledgeName"
						        :value="item.knowledgeId"
						      ></el-option>
						    </el-select>
						    
						  </div>
						</div>
						<div  class="dialog-footer" style="display: flex;justify-content: end;">
						  <el-button @click="cancelDelete">{{ $t("cancel") }}</el-button>
						  <el-button type="primary" @click="addBindKnn">确定</el-button>
						</div>
					  </div>
					</div>										  
					</el-popover>
					
                    
                  </el-form-item>
                </el-form>
              </div>
              <el-table
                :data="dialogTableData"
                size="small"
                max-height="528px"
                style="width: 100%"
                class="headTable"
              >
                <el-table-column
                  prop="question"
                  :label="$t('question')"
                ></el-table-column>
                <el-table-column
                  prop="answer"
                  :label="$t('answer')"
                  width="90"
                  align="center"
                >
                  <template slot-scope="scope">
                    <el-popover
                      popper-class="details-popover"
                      placement="top"
                      :title="scope.row.question"
                      width="720"
                      trigger="click"
                    >
                      <div class="details-popover-content">
                        {{ scope.row.answer }}
                      </div>
                      <div
                        slot="reference"
                        style="color: #1747E5; cursor: pointer"
                      >
                        {{ $t("details") }}
                      </div>
                    </el-popover>
                  </template>
                </el-table-column>
                
                <el-table-column
                  prop="createTime"
                  :label="$t('questionTime')"
                  width="150"
                ></el-table-column>
                <el-table-column
                  prop="userName"
                  :label="$t('sourceUser')"
                  width="180"
                ></el-table-column>
                <el-table-column
                  prop="verifyDeptName"
                  :label="$t('verificationDepartment')"
                  width="180"
                ></el-table-column>
                <el-table-column
                  prop="verifyStatus"
                  :label="$t('verificationStatus')"
                  width="180"
                >
                  <template slot-scope="scope">
                    <div
                      v-if="
                        scope.row.verifyStatus == 0 ||
                        scope.row.verifyStatus == 5 ||
                        scope.row.verifyStatus == 6
                      "
                      class="flex aligns"
                    >
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #ffb24f;
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      <span v-if="scope.row.verifyStatus == 0">
                        {{ $t("pendingVerification") }}
                      </span>
                      <span v-if="scope.row.verifyStatus == 5">
                        {{ $t("pendingReverification") }}
                      </span>
                      <span v-if="scope.row.verifyStatus == 6">
                        {{ $t("pendingReverification") }}
                      </span>
                    </div>
                    <div
                      v-if="
                        scope.row.verifyStatus == 1 ||
                        scope.row.verifyStatus == 2
                      "
                      class="flex aligns"
                    >
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #1c50fd;
                          border: 3px solid rgba(28, 80, 253, 0.1);
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      <span v-if="scope.row.verifyStatus == 1">
                        {{ $t("verifiedModified") }}
                      </span>
                      <span v-if="scope.row.verifyStatus == 2">
                        {{ $t("verifiedCorrect") }}
                      </span>
                    </div>
                    <div v-if="scope.row.verifyStatus == 3" class="flex aligns">
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #fc3d30;
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      {{ $t("maliciousQuestion") }}
                    </div>
                    <div v-if="scope.row.verifyStatus == 4" class="flex aligns">
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #b4bccc;
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      {{ $t("noAction") }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="feedbackType"
                  :label="$t('likesAndStompsValue')"
                  width="80"
                  align="center"
                >
                  <template slot-scope="scoped">
                    <div v-if="scoped.row.feedbackType == 0">
                      <img  src="../../../assets/images/cai.svg" alt="" style="width: 24px;vertical-align: middle;">
                      踩
                    </div>
                   <div v-if="scoped.row.feedbackType == 1">
                    <img  src="../../../assets/images/zan.svg" alt="" style="width: 24px;vertical-align: middle;">
                    赞
                   </div>
                    
                    <!-- <iconpark-icon
                      v-if="scoped.row.feedbackType == 1"
                      name="thumb-up-line"
                    ></iconpark-icon>
                    <iconpark-icon
                      v-if="scoped.row.feedbackType == 0"
                      name="thumb-down-line"
                    ></iconpark-icon> -->
                  </template>
                </el-table-column>
                <el-table-column
                  prop="feedbackContent"
                  :label="$t('feedbackContent')"
                  width="280"
                  show-overflow-tooltip
                >
                </el-table-column>
                <el-table-column
                  fixed="right"
                  :label="$t('action')"
                  width="120"
                >
                  <template slot-scope="scope">
                    <el-button
                      @click="viewClick(scope.row, scope.$index)"
                      type="text"
                      style="color: #1747E5"
                      >{{ $t("view") }}</el-button
                    >
                    <el-button
                      v-if="false"
                      @click="viewScore(scope.row, scope.$index)"
                      type="text"
                      style="color: #1747E5"
                      v-permission="'findScore'"
                      >{{ $t("findScore")}}</el-button
                    >
                    <!-- <el-button @click="toSource(scope.row)" type="text" size="small"
                    >溯源</el-button
                  > -->
                  </template>
                </el-table-column>
              </el-table>
              <div style="text-align: right; margin-top: 32px">
                <el-pagination
                  background
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                  :page-size="dialogPagesize"
                  :current-page="currentPage"
                  :page-sizes="[10, 20, 30, 40]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="dialogTotal"
                ></el-pagination>
              </div>
            </div>
            
            
            <div class="preview-content" v-if="activeName == 'eighth'">
              <div style="margin-bottom: 20px">
                <span style="margin-right: 20px">{{ $t("questionTime") }}</span>
                <el-date-picker
                  v-model="dateRange"
                  type="datetimerange"
                  format="yyyy-MM-dd HH:mm:ss"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  align="right"
                  unlink-panels
                  range-separator="-"
                  :start-placeholder="$t('startDate')"
                  :end-placeholder="$t('endDate')"
                  :picker-options="pickerOptions"
                  clearable
                  style="width: 400px; margin-right: 16px"
                ></el-date-picker>
                <span style="margin-right: 20px">{{
                  $t("verificationDepartment")
                }}</span>

                <el-cascader
                  popper-class="cascaderStyle"
                  :options="treeData"
                  :props="{
                    value: 'deptId',
                    label: 'deptName',
                    checkStrictly: true,
                  }"
                  clearable
                  filterable
                  v-model="verifyDeptId"
                  :show-all-levels="false"
                  style="width: 200px; margin-right: 16px"
                ></el-cascader>

                <span style="margin-right: 20px">{{
                  $t("verificationStatus")
                }}</span>

                <el-select
                  style="width: 200px; margin-right: 16px"
                  v-model="verifyStatus"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                  filterable
                  v-if="activeName == 'eighth'"
                >
                  <el-option
                    v-for="item in verifyStatusColumns"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  ></el-option>
                </el-select>
                <span style="margin-right: 20px">{{ $t("unanswerable") }}</span>
                <el-select
                  style="width: 148px; margin-right: 16px"
                  v-model="noAnswerFlag"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                  filterable
                  v-if="activeName == 'eighth'"
                >
                  <el-option
                    v-for="item in unanswerableList"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  ></el-option>
                </el-select>
                <span style="margin-right: 20px">{{
                  $t("likeAndStepOn")
                }}</span>

                <el-select
                  style="width: 148px; margin-right: 16px"
                  v-model="feedbackType"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                  filterable
                  v-if="activeName == 'eighth'"
                >
                  <el-option
                    v-for="item in likeAndStepOnList"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  ></el-option>
                </el-select>
                <span style="margin-right: 20px">{{ $t("userType") }}</span>

                <el-select
                  style="width: 148px; margin-right: 16px"
                  v-model="userType"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                  filterable
                  v-if="activeName == 'eighth'"
                >
                  <el-option
                    v-for="item in userTypeList"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  ></el-option>
                </el-select>
                <span style="margin-right: 20px">{{
                  $t("distributionDepartment")
                }}</span>

                <el-cascader
                  popper-class="cascaderStyle"
                  :options="treeData"
                  :props="{
                    value: 'deptId',
                    label: 'deptName',
                    checkStrictly: true,
                  }"
                  clearable
                  filterable
                  v-model="distritDeptId"
                  :show-all-levels="false"
                  style="width: 200px; margin-right: 16px"
                ></el-cascader>
                <el-input
                  :placeholder="$t('enterKeywordForQuestionOrAnswer')"
                  style="width: 476px; margin-right: 16px; margin-top: 12px"
                  v-model="text"
                  clearable
                >
                </el-input>
                <el-button type="primary" @click="searchDialog">{{
                  $t("search")
                }}</el-button>
                <el-button @click="resetSearch" plain>{{
                  $t("reset")
                }}</el-button>
                <el-button
                  type="warning"
                  @click="exportDialog"
                  v-if="activeName == 'eighth'"
                  plain
                  >{{ $t("export") }}</el-button
                >
              </div>
              <el-table
                size="small"
                :data="dialogTableData"
                max-height="528px"
                style="width: 100%"
                class="headTable"
              >
                <el-table-column
                  prop="question"
                  :label="$t('question')"
                ></el-table-column>
                <el-table-column
                  prop="answer"
                  :label="$t('answer')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-popover
                      placement="top-start"
                      :title="scope.row.question"
                      width="400"
                      trigger="click"
                    >
                      <div style="padding: 0 12px 12px">
                        {{ scope.row.answer }}
                      </div>
                      <div
                        slot="reference"
                        style="color: #6295fc; cursor: pointer"
                      >
                        {{ $t("details") }}
                      </div>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="userName"
                  :label="$t('sourceUser')"
                  width="180"
                ></el-table-column>
                <el-table-column
                  prop="createTime"
                  :label="$t('questionTime')"
                  width="180"
                ></el-table-column>
                <el-table-column
                  prop="verifyDeptName"
                  :label="$t('verificationDepartment')"
                  width="180"
                ></el-table-column>
                <el-table-column
                  prop="verifyStatus"
                  :label="$t('verificationStatus')"
                  width="180"
                >
                  <template slot-scope="scope">
                    <div
                      v-if="
                        scope.row.verifyStatus == 0 ||
                        scope.row.verifyStatus == 5 ||
                        scope.row.verifyStatus == 6
                      "
                      class="flex aligns"
                    >
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #ffb24f;
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      <span v-if="scope.row.verifyStatus == 0">
                        {{ $t("pendingVerification") }}
                      </span>
                      <span v-if="scope.row.verifyStatus == 5">
                        {{ $t("pendingReverification") }}
                      </span>
                      <span v-if="scope.row.verifyStatus == 6">
                        {{ $t("pendingReverification") }}
                      </span>
                    </div>
                    <div
                      v-if="
                        scope.row.verifyStatus == 1 ||
                        scope.row.verifyStatus == 2
                      "
                      class="flex aligns"
                    >
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #1c50fd;
                          border: 3px solid rgba(28, 80, 253, 0.1);
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      <span v-if="scope.row.verifyStatus == 1">
                        {{ $t("verifiedModified") }}
                      </span>
                      <span v-if="scope.row.verifyStatus == 2">
                        {{ $t("verifiedCorrect") }}
                      </span>
                    </div>
                    <div v-if="scope.row.verifyStatus == 3" class="flex aligns">
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #fc3d30;
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      {{ $t("maliciousQuestion") }}
                    </div>
                    <div v-if="scope.row.verifyStatus == 4" class="flex aligns">
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #b4bccc;
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      {{ $t("noAction") }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="feedbackType"
                  :label="$t('likesAndStompsValue')"
                  width="180"
                >
                  <template slot-scope="scoped">
                    <iconpark-icon
                      v-if="scoped.row.feedbackType == 1"
                      name="thumb-up-line"
                    ></iconpark-icon>
                    <iconpark-icon
                      v-if="scoped.row.feedbackType == 0"
                      name="thumb-down-line"
                    ></iconpark-icon>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="divisionDeptName"
                  :label="$t('distributionDepartment')"
                  width="180"
                >
                </el-table-column>
                <el-table-column
                  fixed="right"
                  :label="$t('action')"
                  width="120"
                >
                  <template slot-scope="scope">
                    <el-button
                      @click="viewClick(scope.row, scope.$index)"
                      type="text"
                      style="color: #6295fc"
                      >{{ $t("view") }}</el-button
                    >
                    <el-button
                      @click="viewScore(scope.row, scope.$index)"
                      type="text"
                      style="color: #24c131"
                      v-permission="'viewScore'"
                      >{{ $t("findScore") }}</el-button
                    >
                    <!-- <el-button @click="toSource(scope.row)" type="text" size="small"
                    >溯源</el-button
                  > -->
                  </template>
                </el-table-column>
              </el-table>
              <div style="text-align: right; margin-top: 32px">
                <el-pagination
                  background
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                  :page-size="dialogPagesize"
                  :current-page="currentPage"
                  :page-sizes="[10, 20, 30, 40]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="dialogTotal"
                ></el-pagination>
              </div>
            </div>
            
            
            <div class="preview-content" v-if="activeName == 'fifth'">

            
              <div style="margin-bottom: 20px">
                <el-form :inline="true" class="demo-form-inline">
                  <el-form-item :label="$t('questionTime')">
                    <el-date-picker
                      v-model="dateRange"
                      type="datetimerange"
                      format="yyyy-MM-dd HH:mm:ss"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      align="right"
                      unlink-panels
                      range-separator="-"
                      :start-placeholder="$t('startDate')"
                      :end-placeholder="$t('endDate')"
                      :picker-options="pickerOptions"
                      clearable
                      style="width: 356px;"
                    ></el-date-picker>
                  </el-form-item>
                  <el-form-item :label="$t('verificationOffice')">
                    <el-cascader
                      popper-class="cascaderStyle"
                      :options="treeData"
                      :props="{
                        value: 'deptId',
                        label: 'deptName',
                        checkStrictly: true,
                      }"
                      clearable
                      v-model="deptId"
                      :show-all-levels="false"
                      style="width: 200px; margin-right: 16px"
                    ></el-cascader>
                  </el-form-item>
                  <el-form-item :label="$t('reviewStatus')">
                    <el-select
                      style="width: 148px; margin-right: 16px"
                      v-model="auditStatus"
                      :placeholder="$t('selectPlaceholder')"
                      clearable
                    >
                      <el-option
                        v-for="item in auditStatusColumns"
                        :key="item.value"
                        :label="item.text"
                        :value="item.value"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item >
                    <el-input
                      :placeholder="$t('enterKeywordForQuestionOrAnswer')"
                      style="width: 334px; margin-right: 16px"
                      v-model="text"
                      clearable
                    >
                    </el-input>
                    <el-button type="primary" @click="searchDialog">{{
                      $t("search")
                    }}</el-button>
                    <el-button @click="resetSearch" plain>{{
                      $t("reset")
                    }}</el-button>
                  </el-form-item>
                </el-form>
                
              </div>
              <el-table
                size="small"
                :data="dialogTableDataAnswer"
                max-height="600px"
                style="width: 100%"
                class="headTable"
              >
                <el-table-column
                  prop="question"
                  :label="$t('question')"
                ></el-table-column>
                <el-table-column
                  prop="verifyAnswer"
                  :label="$t('verifiedAnswer')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-popover
                      popper-class="details-popover"
                      placement="top"
                      :title="scope.row.question"
                      width="720"
                      trigger="click"
                    >
                      <div style="padding: 0 12px 12px">
                        {{ scope.row.verifyAnswer }}
                      </div>
                      <div
                        slot="reference"
                        style="color: #1747E5; cursor: pointer"
                      >
                        {{ $t("details") }}
                      </div>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="userName"
                  :label="$t('sourceUser')"
                  width="180"
                ></el-table-column>
                <el-table-column
                  prop="createTime"
                  :label="$t('questionTime')"
                  width="180"
                ></el-table-column>
                <el-table-column
                  prop="verifyDeptName"
                  :label="$t('verificationDepartment')"
                  width="180"
                ></el-table-column>
                <el-table-column
                  prop="verifyStatus"
                  :label="$t('verificationStatus')"
                  width="180"
                >
                  <template slot-scope="scope">
                    <div v-if="scope.row.verifyStatus == 2" class="flex aligns">
                      {{ $t("correctAnswer") }}
                    </div>
                    <div v-if="scope.row.verifyStatus == 1" class="flex aligns">
                      {{ $t("incorrectAnswer") }}
                    </div>
                    <div v-if="scope.row.verifyStatus == 3" class="flex aligns">
                      {{ $t("maliciousQuestion") }}
                    </div>
                    <div v-if="scope.row.verifyStatus == 4" class="flex aligns">
                      {{ $t("noAction") }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="auditStatus"
                  :label="$t('auditStatus')"
                  width="180"
                >
                  <template slot-scope="scope">
                    <div v-if="scope.row.auditStatus == 0" class="flex aligns">
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #ffb24f;
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      <span> {{ $t("questionVerification") }} </span>
                    </div>
                    <div v-if="scope.row.auditStatus == 1" class="flex aligns">
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #4bbe25;
                          border: 3px solid rgba(28, 80, 253, 0.1);
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      <span> {{ $t("reviewPassed") }} </span>
                    </div>
                    <div v-if="scope.row.auditStatus == 2" class="flex aligns">
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #fc3d30;
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      {{ $t("reviewFailed") }}
                    </div>
                    <div v-if="scope.row.auditStatus == 3" class="flex aligns">
                      <div
                        style="
                          width: 8px;
                          height: 8px;
                          background: #b4bccc;
                          border-radius: 4px;
                          margin-right: 8px;
                        "
                      ></div>
                      {{ $t("notProcessed") }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  fixed="right"
                  :label="$t('action')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-button
                      v-if="scope.row.auditStatus == 0"
                      @click="reviewAnswers(scope.row, scope.$index)"
                      type="text"
                      style="color: #1747E5"
                      >{{ $t("review") }}</el-button
                    >
                    <el-button
                      v-else
                      @click="reviewAnswers(scope.row, scope.$index)"
                      type="text"
                      style="color: #1747E5"
                      >{{ $t("view") }}</el-button
                    >
                    <!-- <el-button @click="toSource(scope.row)" type="text" size="small"
                    >溯源</el-button
                  > -->
                  </template>
                </el-table-column>
              </el-table>
              <div style="text-align: right; margin-top: 32px">
                <el-pagination
                  background
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                  :page-size="dialogPagesize"
                  :current-page="currentPage"
                  :page-sizes="[10, 20, 30, 40]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="dialogTotal"
                ></el-pagination>
              </div>
            </div>
            
            <!-- 配置管理 -->
            
            <div class="preview-content" v-if="activeName == 'seventh'">
              <ConfigManage
                v-if="activeName == 'seventh'"
                ref="ConfigManage"
                :data="previewItem"
              />
            
            </div>
          <!-- 业务数据 -->
          
          <div class="preview-content" v-if="activeName == 'businessData'">
            <businessData
              v-if="activeName == 'businessData'"
              ref="businessData"
              :data="previewItem"
            />
          
          </div>
        </div>
      </div>
    </el-dialog>
    <el-drawer
      :title="$t('questionVerification')"
      :visible.sync="addQaVisible"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :wrapperClosable="false"
      :before-close="handleClose"
      class="elDrawer"
      size="55%"
    >
      <div slot class="qa-box">
        <div class="userInfo">
          <span>{{ setForm.question }}</span>
        </div>
        <div class="flex aligns just" style="margin-bottom: 12px">
          <div class="flex aligns user-list-box" :style="modelAnswerFlag?'width:51%;':'width:100%;'">
            <div
              :class="['userList', activeIndex == 0 ? 'activeList' : '']"
              @click="clickUser(0)"
            >
              {{ $t("originalAnswer") }}
            </div>
            <div
              :class="['userList', activeIndex == 1 ? 'activeList' : '']"
              @click="clickUser(1)"
            >
              {{ $t("traceability") }}
            </div>
            <!-- <div
              :class="['userList', activeIndex === 2 ? 'activeList' : '']"
              @click="clickUser(2)"
              v-permission="'rewriting'"
            >
              {{ $t("rewritingProblem") }}
            </div> -->
            <div
              :class="['userList', activeIndex === 3 ? 'activeList' : '']"
              @click="clickUser(3)"
              v-permission="'wholeProcess'"
            >
              上下文
            </div>
			<div
			  :class="['userList', activeIndex === 4 ? 'activeList' : '']"
			  @click="clickUser(4)"
			  v-permission="'wholeProcess'"
			>
			  赞踩
			</div>
          </div>
          <div class="flex-center just" style="width: 49.5%" v-if="modelAnswerFlag">
            <div class="flex-center">
              <div
                class="line"
              ></div>
              <span
              class="line-text"
                style="
                  font-family: MiSans, MiSans;
                  font-weight: 500;
                  font-size: 18px;
                  color: #383d47;
                "
                >{{ $t("verifyty") }}</span
              >
            </div>
            <!-- <div class="flex-center">
              
              <div
                class="flex-center"
                v-if="
                  setForm.verifyStatus == 0 ||
                  setForm.verifyStatus == 5 ||
                  setForm.verifyStatus == 6
                "
              >
                <el-radio-group v-model="formList.verifyStatus">
                  <el-radio :label="2">{{ $t("correct") }}</el-radio>
                  <el-radio :label="1">{{ $t("incorrect") }}</el-radio>
                  <el-radio :label="3">{{
                    $t("maliciousQuestion")
                  }}</el-radio>
                  <el-radio :label="4">{{ $t("noAction") }}</el-radio>
                </el-radio-group>
              </div>
              <div class="flex-center" v-else>
                <img
                  v-if="setForm.verifyStatus != 3"
                  :src="require('@/assets/images/checkbox-circle-fill.svg')"
                />
                <i
                  v-else
                  style="color: #f00; font-size: 22px"
                  class="iconfont el-icon-error"
                ></i>
                <span
                  style="margin: 0 4px 0 8px"
                  v-if="setForm.verifyStatus == 1"
                  >{{ $t("verifiedModified") }}</span
                >
                <span
                  style="margin: 0 4px 0 8px"
                  v-if="setForm.verifyStatus == 2"
                  >{{ $t("verifiedCorrect") }}</span
                >
                <span
                  style="margin: 0 4px 0 8px"
                  v-if="setForm.verifyStatus == 3"
                  >{{ $t("maliciousQuestion") }}</span
                >
                <span
                  style="margin: 0 4px 0 8px"
                  v-if="setForm.verifyStatus == 4"
                  >{{ $t("noAction") }}</span
                >
                <el-popover
                  placement="left-start"
                  width="300"
                  trigger="hover"
                >
                  <div style="padding: 0 12px 12px">
                    <div class="flex just">
                      <span class="formKey"> {{ $t("verifier") }} </span>
                      <span class="formValue">
                        {{ setForm.verifyUserName }}
                      </span>
                    </div>
                    <div class="flex just">
                      <span class="formKey"> {{ $t("department") }} </span>
                      <span class="formValue">
                        {{ setForm.verifyDeptName }}
                      </span>
                    </div>
                    <div class="flex just">
                      <span class="formKey">
                        {{ $t("verificationTime") }}
                      </span>
                      <span class="formValue">
                        {{ setForm.createTime }}
                      </span>
                    </div>
                  </div>
                  <img
                    slot="reference"
                    :src="require('@/assets/images/information-line.svg')"
                  />
                </el-popover>
              </div>
            </div> -->
          </div>
        </div>
        <div class="content">
          <!-- 溯源 -->
          <el-input
            v-model="reviseQuestion.result"
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 50 }"
            class="leftText"
            disabled
            v-if="activeIndex == 2"
          ></el-input>
          <!-- 全流程 -->
          <div v-if="activeIndex == 3" class="traceability":style="modelAnswerFlag?'width:51%;':'width:100%;'">
            <div v-if="wholeProcessList.length > 0">
              <div v-for="(item, index) in wholeProcessList" :key="item">
                <el-collapse>
                  <el-collapse-item :name="index">
                    <el-tooltip
                      slot="title"
                      class="item"
                      effect="dark"
                      :content="item.step"
                      placement="top"
                    >
                      <div
                        style="
                          white-space: nowrap;
                          overflow: hidden;
                          text-overflow: ellipsis;
                          font-weight: bold;
                          font-family: MiSans, MiSans;
                          font-weight: 500;
                          font-size: 16px;
                          color: #383d47;
                        "
                      >
                        {{ item.step }}
                      </div>
                    </el-tooltip>
                    <div
                      v-if="item.prompt"
                      style="
                        border: 1px solid rgb(141 25 224 / 49%);
                        border-radius: 10px;
                        margin-bottom: 10px;
                        padding: 10px;
                      "
                    >
                      <p
                        style="
                          font-weight: bold;
                          color: #000;
                          font-size: 18px;
                          border-bottom: 1px solid #abadaa4a;
                        "
                      >
                        参数
                      </p>

                      <p
                        style="
                          margin-bottom: 16px;
                          cursor: pointer;
                          font-family: MiSans, MiSans;
                          font-weight: 400;
                          font-size: 14px;
                          color: #828894;
                          line-height: 22px;
                          border-bottom: 1px solid #abadaa4a;
                          padding: 5px;
                        "
                        @click="copyText(item.prompt)"
                      >
                        {{ item.prompt }}
                      </p>
                    </div>
                    <div
                      v-if="item.result"
                      style="
                        border: 1px solid rgb(141 25 224 / 49%);
                        border-radius: 10px;
                        padding: 10px;
                      "
                    >
                      <p
                        style="
                          font-weight: bold;
                          color: #000;
                          font-size: 18px;
                        "
                      >
                        结果
                      </p>
                      <p
                        style="
                          margin-bottom: 16px;
                          cursor: pointer;
                          font-family: MiSans, MiSans;
                          font-weight: 400;
                          font-size: 14px;
                          color: #828894;
                          line-height: 22px;
                          border-bottom: 1px solid #abadaa4a;
                          padding: 5px;
                        "
                        @click="copyText(item.result)"
                      >
                        {{ item.result }}
                      </p>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
            <p style="text-align: center" v-else>{{ $t("noData") }}</p>
          </div>
          <div v-if="activeIndex == 4" class="traceability":style="modelAnswerFlag?'width:51%;':'width:100%;'">
            <div class="feedback" v-if="setForm.feedbackType == 1||setForm.feedbackType == 0">
                <div class="feedback-left">
					<div v-if="setForm.feedbackType == 0">
					   <img  src="../../../assets/images/cai.svg" alt="" style="width: 24px;vertical-align: middle;">                  
					 </div>
					<div v-if="setForm.feedbackType == 1">
					 <img  src="../../../assets/images/zan.svg" alt="" style="width: 24px;vertical-align: middle;">   
					</div>
				</div>
				<div class="feedback-box">
					<div style="color: #666;">
						<span v-if="setForm.feedbackUserName">{{setForm.feedbackUserName}}</span>
						<span v-if="setForm.feedbackTime">{{setForm.feedbackTime}}</span>
					</div>
					<div style="margin-top: 10px;" v-if="setForm.feedbackContent">{{setForm.feedbackContent}}</div>
				</div>
            </div>
            <p style="text-align: center" v-else>{{ $t("noData") }}</p>
          </div>
          <div v-if="activeIndex == 1" class="traceability":style="modelAnswerFlag?'width:51%;':'width:100%;'">
            <div v-if="sourceTableList.length > 0">
              <div v-for="(item, index) in sourceTableList" :key="item">
                <el-collapse>
                  <el-collapse-item :name="index">
                    <el-tooltip
                      slot="title"
                      class="item"
                      effect="dark"
                      :content="
                        item.route
                          ? (item.knowledgeName
                              ? item.knowledgeName
                              : $t('noKnowledgeBase')) +
                            '-' +
                            item.route.join('|')
                          : ''
                      "
                      placement="top"
                    >
                      <div
                        style="
                          white-space: nowrap;
                          overflow: hidden;
                          text-overflow: ellipsis;
                          font-weight: bold;
                          font-family: MiSans, MiSans;
                          font-weight: 500;
                          font-size: 16px;
                          color: #383d47;
                        "
                      >
                        {{
                          item.route
                            ? (item.knowledgeName
                                ? item.knowledgeName
                                : $t("noKnowledgeBase")) +
                              "-" +
                              item.route.join("|")
                            : ""
                        }}
                      </div>
                    </el-tooltip>
                    <div
                      v-html="item.text"
                      style="
                        cursor: pointer;
                        font-family: MiSans, MiSans;
                        font-weight: 400;
                        font-size: 14px;
                        color: #828894;
                        line-height: 22px;
                      "
                    >
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
            <p style="text-align: center" v-else>{{ $t("noData") }}</p>
          </div>

          <el-input
            v-model="setForm.answer"
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 50 }"
            :placeholder="$t('inputPlaceholder')"
            class="leftText"
            disabled
			:style="!modelAnswerFlag?'width: 100%;':'width: 50%;'"
            v-if="activeIndex == 0"
          ></el-input>
          <el-input
            v-model="formList.verifyAnswer"
            type="textarea"
			v-if="modelAnswerFlag"
            :autosize="{ minRows: 6, maxRows: 50 }"
            :placeholder="$t('inputPlaceholder')"
            :class="[
              setForm.verifyStatus == 0 ||
              setForm.verifyStatus == 5 ||
              setForm.verifyStatus == 6
                ? 'verifyStatusAnswer'
                : '',
            ]"
          ></el-input>
        </div>
        <div class="footerDrawer" v-if="modelAnswerFlag">
          <el-button @click="handleClose">{{ $t("close") }}</el-button>
          
          <el-button
            type="primary"
            v-if="
              setForm.verifyStatus == 0 ||
              setForm.verifyStatus == 5 ||
              setForm.verifyStatus == 6
            "
            @click="handleAddQaDialog(0)"
            >{{ $t("submitAndClose") }}</el-button
          >
          <el-button
            type="primary"
            v-if="
              setForm.verifyStatus == 0 ||
              setForm.verifyStatus == 5 ||
              setForm.verifyStatus == 6
            "
            @click="submitVerification"
            >{{ $t("submitAndVerifyNext") }}</el-button
          >
        </div>
      </div>
    </el-drawer>
    <el-drawer
      :title="$t('findScore')"
      :visible.sync="scoreQaVisible"
      size="40%"
    >
      <score-view
        v-if="scoreQaVisible"
        :question="setForm.question"
        :applicationId="previewItem.applicationId"
      ></score-view>
    </el-drawer>
    <drawerAnswer
      v-if="addQaVisibleAnswer"
      :addQaVisibleAnswer="addQaVisibleAnswer"
      :setForm="setForm"
      @handlecloseDraw="handlecloseDraw"
      @handleAddQaDialogAns="handleAddQaDialogAns"
    ></drawerAnswer>
  </div>
</template>
<script>
import {
  dialogRecord,
  getCheckRecord,
  deptTree,
  verifyRecord,
  sourceList,
  getAllStepByDialogId,
  getReviseQuestion,
  recordCheckRecord,
  bindKnn,
  getBindKnn
} from "@/api/app";
import ConfigManage from "./configManage.vue";
import businessData from "./businessData.vue";
import Analysis from "./analysis.vue";
import ScoreView from "./scoreView.vue";
import drawerAnswer from "./drawerAnswer.vue";
import axios from "axios";
export default {
  name: "appDetails",
  components: {
    ConfigManage,
	businessData,
    Analysis,
    ScoreView,
    drawerAnswer
  },
  data() {
    return {
	  visiblePopover: false,
	  modelAnswerFlag: false,
	  startModelAnswerFlag: false,
      previewDialogVisible: false,
      previewItem: "",
      activeName: 'first',
      workPosition: "",
      dateRange: [],
      timeStart: "",
      timeEnd: "",
      text: "",
      userName: "",
      verifyStatus: "",
      noAnswerFlag: "",
      feedbackType: "",
      userType: "",
      verifyDeptId: "",
      distritDeptId: "",
      deptId: "",
      auditStatus: "",
      currentPage: 1,
      dialogPagesize: 10,
      pickerOptions: {
        shortcuts: [
          {
            text: this.$t("lastWeek"),
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: this.$t("lastMonth"),
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: this.$t("lastThreeMonths"),
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
      treeData: [],
      dialogTableData: [],
      dialogTotal: 0,
      auditStatusColumns: [
        {
          text: this.$t("pendingReview"),
          value: 0,
        },
        {
          text: this.$t("reviewPassed"),
          value: 1,
        },
        {
          text: this.$t("reviewFailed"),
          value: 2,
        },
        {
          text: this.$t("notProcessed"),
          value: 3,
        },
      ],
      dialogTableDataAnswer: [],
      verifyStatusColumns: [
        {
          text: this.$t("pendingVerification"),
          value: 0,
        },
        {
          text: this.$t("verifiedModified"),
          value: 1,
        },
        {
          text: this.$t("verifiedCorrect"),
          value: 2,
        },
        {
          text: this.$t("maliciousQuestion"),
          value: 3,
        },
        {
          text: this.$t("noAction"),
          value: 4,
        },
        {
          text: this.$t("pendingReverification"),
          value: 5,
        },
      ],
      unanswerableList: [
        {
          text: "是",
          value: true,
        },
        {
          text: "否",
          value: false,
        },
      ],
      likeAndStepOnList: [
        {
          text: "赞",
          value: 1,
        },
        {
          text: "踩",
          value: 0,
        },
      ],
      typelist: "",
      userTypeList: [
        {
          text: "居民",
          value: "staff",
        },
        {
          text: "工作人员",
          value: "gov",
        },
      ],
      setForm: {},
      setIndex: "",
      activeIndex: 0,
      formList: {
		  verifyStatus:'2'
	  },
      addQaVisible: false,
      wholeProcessList: [],
      sourceTableList: [],
      reviseQuestion: {},
      sourceVisible: false,
      scoreQaVisible: false,
      addQaVisibleAnswer: false,
	  knowledgeIds:[],
	  knowledgeId:'',
	  startKnowledgeId:'',
    }
  },
  methods: {
    editApp(tab) {
      let previewItem = JSON.parse(JSON.stringify(this.previewItem));
      previewItem.tab = tab
      this.$emit("editApp", previewItem);
      this.closePriview()
    },
    openDialog(data) {
		console.log('data',data)
      this.getDeptTree();
      this.previewDialogVisible = true;
      this.activeName = 'first';
      this.previewItem = data;
	   this.knowledgeIds = data.knowledgeIds;
	   this.getBindKnns()
    },
    closePriview() {
      this.previewDialogVisible = false;
	  this.modelAnswerFlag = false
	  this.knowledgeId = ''
      this.$emit('closePriview');
    },
    permissions(code) {
      const permissionButton = this.getUserPermissions();
      const item = permissionButton.find((n) => n.menuCode === code);
      return !!item;
    },
    getUserPermissions() {
      const permission = JSON.parse(sessionStorage.getItem("permission"));
      const permissionButton = [];
      permission.forEach((item) => {
        if (item.children && Array.isArray(item.children)) {
          item.children.forEach((child) => {
            permissionButton.push({
              isHidden: child.isHidden,
              menuCode: child.menuCode,
            });
          });
        }
      });
      return permissionButton;
    },
    handleClick(tab, event) {
      this.workPosition = "";
      this.dateRange = [];
      this.timeStart = "";
      this.timeEnd = "";
      this.text = "";
      this.userName = "";
      this.verifyStatus = "";
      this.noAnswerFlag = "";
      this.feedbackType = "";
      this.userType = "";
      this.verifyDeptId = "";
      this.distritDeptId = "";
      this.deptId = "";
      this.auditStatus = "";
      this.currentPage = 1;
      this.dialogPagesize = 10;
      if (tab.name == "fourth" || tab.name == "eighth") {
        this.getDialogRecord();
      } else if (tab.name == "fifth") {
        this.getDialogRecordAnswer();
      }
    },
    getDialogRecord() {
      let deptId = "", disDeptId = "";
      if (this.verifyDeptId.length > 0) {
        deptId = this.verifyDeptId[this.verifyDeptId.length - 1];
      }
      if (this.distritDeptId.length > 0) {
        disDeptId = this.distritDeptId[this.distritDeptId.length - 1];
      }
      dialogRecord({
        timeStart: this.dateRange ? this.dateRange[0] : "",
        timeEnd: this.dateRange ? this.dateRange[1] : "",
        // question: this.question,
        // answer: this.answer,
        applicationId: this.previewItem.applicationId,
        pageNo: this.currentPage,
        pageSize: this.dialogPagesize,
        verifyDeptId: deptId,
        deptId: disDeptId,
        verifyStatus: this.verifyStatus,
        noAnswerFlag: this.noAnswerFlag,
        feedbackType: this.feedbackType,
        userType: this.userType,
        text: this.text,
        userName: this.userName,
        fromTab: this.activeName == "eighth" ? "allocate" : "",
      }).then((res) => {
        if (res.code == "000000") {
          this.dialogTableData = res.data.list || [];
          this.dialogTotal = res.data.total || 0;
        } else {
          this.dialogTableData = [];
          this.dialogTotal = 0;
        }
      });
    },
    getDialogRecordAnswer() {
      let deptId = "";
      if (this.deptId.length > 0) {
        deptId = this.deptId[this.deptId.length - 1];
      }
      getCheckRecord({
        timeStart: this.dateRange ? this.dateRange[0] : "",
        timeEnd: this.dateRange ? this.dateRange[1] : "",
        text: this.text,
        userName: this.userName,
        auditStatus: this.auditStatus,
        // verifyDeptName: this.verifyDeptName,
        applicationId: this.previewItem.applicationId,
        pageNo: this.currentPage,
        pageSize: this.dialogPagesize,
        deptId: deptId,
      }).then((res) => {
        if (res.code == "000000") {
          this.dialogTableDataAnswer = res.data.list || [];
          this.dialogTotal = res.data.total || 0;
        } else {
          this.dialogTableDataAnswer = [];
          this.dialogTotal = 0;
        }
      });
    },
    openPreview() {
      window.open(this.previewItem.clientLink, "_blank");
    },
    devBuilding() {
      this.$message({
        type: "warning",
        message: this.$t("underConstruction"),
      });
    },
    getDeptTree() {
      deptTree({
        pageNo: 1,
        pageSize: 1000,
      }).then((res) => {
        if (res.code == "000000") {
          this.treeData = res.data || [];
        }
      });
    },
    searchDialog() {
      this.currentPage = 1;
      if (this.activeName == "fourth" || this.activeName == "eighth") {
        this.getDialogRecord();
      } else if (this.activeName == "fifth") {
        this.getDialogRecordAnswer();
      }
    },
    resetSearch() {
      this.workPosition = "";
      this.dateRange = [];
      this.timeStart = "";
      this.timeEnd = "";
      this.text = "";
      this.userName = "";
      this.verifyStatus = "";
      this.noAnswerFlag = "";
      this.feedbackType = "";
      this.userType = "";
      this.verifyDeptId = "";
      this.distritDeptId = "";
      this.deptId = "";
      this.auditStatus = "";
      if (this.activeName == "fourth" || this.activeName == "eighth") {
        this.getDialogRecord();
      } else if (this.activeName == "fifth") {
        this.getDialogRecordAnswer();
      }
    },
    handleSizeChange(val) {
      this.currentPage = 1;
      this.dialogPagesize = val;
      if (this.activeName == "fourth" || this.activeName == "eighth") {
        this.getDialogRecord();
      } else if (this.activeName == "fifth") {
        this.getDialogRecordAnswer();
      }
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      if (this.activeName == "fourth" || this.activeName == "eighth") {
        this.getDialogRecord();
      } else if (this.activeName == "fifth") {
        this.getDialogRecordAnswer();
      }
    },
    exportDialog() {
      let deptId = "";
      if (this.verifyDeptId.length > 0) {
        deptId = this.verifyDeptId[this.verifyDeptId.length - 1];
      }
      axios({
        method: "POST",
        url: `${process.env.VUE_APP_BASE_API}/record/exportRecord`,
        // url: `https://localhost/smart-agent-api-demo/matterGuideInfo/export`,
        headers: {
          Authorization:
            "Bearer " + sessionStorage.getItem("manageAccessToken"),
        },
        responseType: "blob",
        data: {
          timeStart: this.dateRange ? this.dateRange[0] : "",
          timeEnd: this.dateRange ? this.dateRange[1] : "",
          // question: this.question,
          // answer: this.answer,
          applicationId: this.previewItem.applicationId,
          pageNo: this.currentPage,
          pageSize: this.dialogPagesize,
          verifyDeptId: deptId,
          verifyStatus: this.verifyStatus,
          noAnswerFlag: this.noAnswerFlag,
          feedbackType: this.feedbackType,
          userType: this.userType,
          text: this.text,
          userName: this.userName,
        },
      })
        .then((res) => {
          let data = res.data;
          console.log(data);
          const url = window.URL.createObjectURL(new Blob([res.data]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", this.formatDate() + ".xlsx");
          document.body.appendChild(link);
          link.click();
        })
        .catch((error) => {
          console.log("config-res-error:", error);
        });
    },
    formatDate() {
      const date = new Date();
      const year = date.getFullYear();
      let month = date.getMonth() + 1;
      let day = date.getDate();
      let hours = date.getHours();
      let minutes = date.getMinutes();

      // 如果月份或日期小于10，则在前面添加0
      month = month < 10 ? "0" + month : month;
      day = day < 10 ? "0" + day : day;
      hours = hours < 10 ? "0" + hours : hours;
      minutes = minutes < 10 ? "0" + minutes : minutes;

      return year + month + day + "_" + hours + minutes;
    },
    viewClick(row, index) {
      this.setForm = row;
	  console.log('this.setForm',this.setForm)
      this.setIndex = index;
      this.$set(this.formList, "verifyAnswer", row.verifyAnswer || row.answer);
      this.activeIndex = 0;
      this.$set(this.formList, "verifyStatus", "2");
      this.addQaVisible = true;
    },
    handleClose() {
      this.addQaVisible = false;
      this.getDialogRecord();
    },
    submitVerification() {
      this.handleAddQaDialog(1);
    },
    handleAddQaDialog(type) {
		recordCheckRecord({
		  applicationId: this.previewItem.applicationId,
		  dialogueId: this.setForm.dialogueId,
		  auditStatus: '1',
		}).then((res) => {
        if (res.code == "000000") {
			
        }
		  
      });
      verifyRecord({
        dialogueId: this.setForm.dialogueId,
        ...this.formList,
      }).then((res) => {
        if (res.code == "000000") {
          this.$message.success(this.$t("successed"));
          if (type == 0) {
            this.addQaVisible = false;
            setTimeout(() => {
              this.getDialogRecord();
            }, 1000);
          } else {          
            this.setIndex = this.setIndex + 1;
            if (this.setIndex > this.dialogPagesize || this.setIndex >= this.dialogTableData.length) {
              this.$message.closeAll();
              return this.$message.warning(this.$t("isLastDataOnPage"));
            }
            this.setForm = this.dialogTableData[this.setIndex];
            this.$set(this.formList, "verifyStatus", "2");
          }
        }
      });
    },
    clickUser(type) {
      this.activeIndex = type;
      if (type == 1) {
        this.toSource(this.setForm);
      } else if (type === 2) {
        this.toReviseQuestion(this.setForm);
      } else if (type === 3) {
        this.toWholeProcess(this.setForm);
      }
    },
    cancelSource() {
      this.sourceVisible = false;
    },
    toSource(row) {
      this.sourceTableList = [];
      // this.sourceVisible = true
      sourceList({ dialogueId: row.dialogueId,applicationId:this.previewItem.applicationId , fromManage: '1'}).then((res) => {
        if (res.code == "000000") {
          this.sourceTableList = res?.data?.sourceAnswerResultList ? res.data.sourceAnswerResultList : res.data;
          this.sourceTableList.forEach(item => {
            item.text = this.translateHtml(item.text)
          })
          // this.sourceTableList = res.data || [];
        } else {
          this.sourceTableList = [];
        }
      });
    },
    translateHtml(msg){
      var reg = /((http|https):\/\/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])/g;
      var textR = msg.replace(reg, "<a href='$1' target='_blank' style='color: #1c50fd;display: inline;'>$1</a>");
      return textR;
    },
    toReviseQuestion(row) {
      // this.sourceVisible = true
      getReviseQuestion({ dialogueId: row.dialogueId }).then((res) => {
        if (res.code === "000000") {
          this.reviseQuestion = res.data;
        } else {
          this.reviseQuestion = {};
        }
      });
    },
    copyText(text) {
      navigator.clipboard.writeText(text);
      this.$message({
        type: "success",
        message: "复制成功",
      });
    },
    toWholeProcess(row) {
      // this.sourceVisible = true
      getAllStepByDialogId({ dialogId: row.dialogueId }).then((res) => {
        if (res.code === "000000") {
          this.wholeProcessList = res.data;
        } else {
          this.wholeProcessList = [];
        }
      });
    },
    viewScore(row, index) {
      this.setForm = row;
      this.setIndex = index;
      this.scoreQaVisible = true;
    },
    reviewAnswers(row, index) {
      this.setForm = row;
      this.setIndex = index;
      this.addQaVisibleAnswer = true;
    },
    handlecloseDraw() {
      this.addQaVisibleAnswer = false;
      this.getDialogRecordAnswer();
    },
    handleAddQaDialogAns(auditStatus, type) {
      recordCheckRecord({
        applicationId: this.previewItem.applicationId,
        dialogueId: this.setForm.dialogueId,
        auditStatus: auditStatus,
      }).then((res) => {
        if (res.code == "000000") {
          this.$message.success(this.$t("successed"));
          if (type == 0) {
            this.addQaVisibleAnswer = false;
            setTimeout(() => {
              this.getDialogRecordAnswer();
            }, 1000);
          } else {
            this.setIndex = this.setIndex + 1;
            if (this.setIndex > this.dialogPagesize) {
              return this.$message.warning(this.$t("isLastDataOnPage"));
            }
            this.setForm = this.dialogTableDataAnswer[this.setIndex];
          }
        }
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
    cpoyText(content) {
      this.exeCommandCopyText(content);
      this.$message({
        message: this.$t("copySuccessed"),
        type: "success",
      });
    },
	cancelDelete(){
		this.visiblePopover = false
		this.knowledgeId = this.startKnowledgeId
		this.modelAnswerFlag = this.startModelAnswerFlag
	},
	//绑定知识库
	addBindKnn() {
	  if(this.knowledgeId==''){
		  this.$message({
		    message: '请先绑定知识库',
		    type: "error",
		  });
		  return false
	  }
	  bindKnn({
	    appId: this.previewItem.applicationId,
	    knnId: this.modelAnswerFlag ? this.knowledgeId : '',
	   
	  }).then((res) => {
	    if (res.code == "000000") {
	      this.$message.success(this.$t("successed"));
	      this.visiblePopover = false
        this.knowledgeId = this.modelAnswerFlag ? this.knowledgeId : ''
        this.startKnowledgeId = this.knowledgeId
	      }else{
			this.$message({
			  message: res.msg,
			  type: "error",
			});
		}
	    
	  });
	},
	//绑定知识库
	getBindKnns() {
	  getBindKnn({
	    appId: this.previewItem.applicationId	   
	  }).then((res) => {
	    if (res.code == "000000") {
	        if(res.data){
				if(this.knowledgeIds.length>0){
					this.knowledgeIds.forEach((item)=>{
						if(item.knowledgeId==res.data){
							this.knowledgeId = res.data
							this.startKnowledgeId = res.data
							this.modelAnswerFlag = true
							this.startModelAnswerFlag = true
						}
					})
					
				}
				
			}
	      
	      }else{
			this.$message({
			  message: res.msg,
			  type: "error",
			});
		}
	    
	  });
	},
  }
}
</script>
<style lang="scss" scoped>
.app-details {
  ::v-deep .preViewDialog {
    .el-dialog {
      border-radius: 0 0 4px 4px;
      background: #ffffff;
    }
    .el-dialog__header {
      display: none;
    }
    .el-dialog__body {
      padding: 0px;
      height: 100%;
      .app-details-content {
        width: 100%;
        height: 100%;
        overflow: hidden;
        display: flex;
        flex-direction: column;
      }
       
      .headBar {
        padding: 16px 32px;
        border-bottom: 1px solid rgba(0,0,0,0.12);
        margin-bottom: 0px;
        display: flex;
        align-items: center;
        > div {
          flex: 1;
        }
        img {
          width: 15px;
          height: 15px;
        }
        .leftSlide {
          display: flex;
          align-items: center;
          overflow: hidden;
          .closeImg{
           width: 32px;
           height: 32px;
            margin-right: 8px;
            display: flex;
            justify-content: center;
            align-items: center;
            cursor: pointer;
            > img {
            width: 20px;
            height: 20px;
            cursor: pointer;
          }
          }
          
          .titleIcon {
            display: flex !important;
            width: calc(100% - 60px);
            overflow: hidden;
            .title-headImg {
              width: 40px;
              height: 40px;
              border-radius: 2px;
              display: inline-flex;
              align-items: center;
              margin-right: 12px;
              img {
                width: 100%;
                height: auto;
              }
            }
            .title-info {
              display: flex;
              flex-direction: column;
              flex: 1;
              overflow: hidden;
            }
            p {
              max-width: calc(100% - 60px);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
            p:first-child {
              font-family: MiSans, MiSans;
              font-weight: 500;
              font-size: 18px;
              color: #494E57;
              line-height: 24px;
              text-align: left;
              font-style: normal;
              margin-bottom: 4px;
            }
            p:last-child {
              font-family: MiSans, MiSans;
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 20px;
              text-align: left;
              font-style: normal;
            }
          }
        }
        .centerSlide {
          display: flex;
          align-items: center;
          .el-tabs {
            .el-tabs__header {
              margin-bottom: 0px;
              border-bottom: 0px;
              .el-tabs__nav {
                border: 0px;
                height: 40px;
                background: #F7F8FA;
                border-radius: 4px;
                padding: 2px;
                .el-tabs__item {
                  height: 36px;
                  line-height: 36px;
                  border-radius: 2px;
                  padding: 0 24px;
                  font-family: MiSans, MiSans;
                  font-weight: 400;
                  font-size: 16px;
                  color: #828894;
                  &.is-active {
                    background: #FFFFFF;
                    box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
                    font-weight: 500;
                    color: #494E57;
                  }
                  &:hover {
                    background: rgba(188,193,204, 0.2);
                  }
                }
              }
            }
          }
        }
        .rightSlide {
          display: flex;
          align-items: center;
          justify-content: right;
          text-align: right;
          .btn {
            height: 40px;
            border-radius: 2px;
            border: 1px solid #1747E5;
            font-size: 16px;
            color: #1747E5;
            padding: 0px 12px;
            display: inline-flex;
            align-items: center;
            > span {
              display: inline-flex;
              align-items: center;
            }
            .edit-icon {
              margin-right: 8px;
            }
          }
        }
      }
      .app-preview-content {
        flex: 1;
        overflow: hidden;
        // padding: 24px 32px;
        background: #F7F8FA;
        .preview-content {
          height: 100%;
          width: 100%;
          padding: 24px 32px;
          background: #fff;
          &.third {
            background: #F7F8FA;
            padding: 24px 199px;
          }
        }
      }
    }
    .el-tabs {
      .el-tabs__item {
        border: 0px !important;
        color: #383d47;
        font-size: 18px;
      }
      .is-active {
        color: #181b49;
      }
      .el-tabs__active-bar {
        background: #3470ff;
      }
      .el-tabs__nav-wrap::after {
        height: 1px;
        background: rgba(0, 0, 0, 0.12);
      }
    }
    .tabContentTitle {
      display: flex;
      align-items: center;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 18px;
      color: #181b49;
      margin-bottom: 16px;
      .line {
        display: inline-block;
        width: 4px;
        height: 18px;
        background: #1747E5;
        border-radius: 0px 2px 2px 0px;
        margin-right: 8px;
      }
    }
    .tabContents {
      display: flex;
      justify-content: space-between;
      > .contentInner {
        width: calc(50% - 8px);
        height: 226px;
        padding: 26px;
        border-radius: 4px;
        border: 1px solid #e1e4eb;
        .title {
          display: flex;
          justify-content: space-between;
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 18px;
          color: #181b49;
          text-align: left;
          font-style: normal;
          margin-bottom: 24px;
        }
        .desc {
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #768094;
          line-height: 18px;
          text-align: left;
          font-style: normal;
          margin-bottom: 8px;
        }
        .secondLine {
          display: flex;
          justify-content: flex-start;
          margin-bottom: 24px;
          .inp {
            margin-right: 30px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            height: 40px;
            width: 500px;
            background: #f2f4f7;
            border-radius: 4px;
            border: 1px solid #e1e4eb;
            line-height: 36px;
            padding: 0 16px;
            position: relative;
            > img {
              width: 16px;
              height: 16px;
              position: absolute;
              right: 10px;
              top: 12px;
              cursor: pointer;
            }
          }
          .el-button {
            font-size: 16px;
            color: #3666ea;
            border-color: #3666ea;
            img {
              width: 15px;
              height: 15px;
              margin-right: 8px;
            }
            img,
            span {
              vertical-align: middle;
            }
          }
        }
        .thirdLine {
          .el-button {
            img {
              width: 15px;
              height: 15px;
              margin-right: 8px;
            }
            img,
            span {
              vertical-align: middle;
            }
          }
          .el-button--primary {
            border: 1px solid #1747E5;;
            background: #1747E5;;
            color: #fff;
          }
          .borderBtn {
            border: 1px solid #1747E5;;
            color: #1747E5;;
            margin-right: 16px;
          }
        }
      }
    }
  }
}
.elDrawer {
  ::v-deep .el-drawer__header {
    background: #ffffff;
    border-radius: 8px 8px 0px 0px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 20px;
    color: #494E57;
    line-height: 32px;
    padding: 24px;
    margin-bottom: 0px;

  }
  ::v-deep .el-drawer__body {
    padding: 0 32px 24px;
  }
}

.userInfo {
  margin: 0 0 16px 0;
  span {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
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
.qa-box {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.user-list-box {
  padding: 2px;
  display: flex;
  background: #F7F8FA;
  border-radius: 2px;
  justify-content: space-between;
  width: 100%;
}
.userList {
  padding: 0px 40px;
  // height: 40px;
  background: #f2f5fa;
  border-radius: 4px;
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 16px;
  color: #828894;
  line-height: 28px;
  cursor: pointer;
  
  text-align: center;
  border-radius: 2px;

}

.activeList {
  background: #FFFFFF;
  box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
  color: #494E57;
}
.content {
  display: flex;
  flex: 1;
  overflow: hidden;
  ::v-deep .el-textarea.leftText {
    margin-right: 16px;
    .el-textarea__inner {
      border: 1px solid #f2f5fa;
    }
  }
  ::v-deep .el-textarea {
    width: 49.5%;
  }
  .traceability {
    width: 100%;
    background: #F7F8FA;;
    height: 100% !important;
    margin-right: 16px;
    overflow-y: auto;
    padding: 6px 14px;
    ::v-deep .el-collapse-item__header {
      height: 64px;
      line-height: 64px;
      background-color: #F7F8FA;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 16px;
      color: #494E57;
    }
    ::v-deep .el-collapse-item__content {
      background-color: #F7F8FA;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #828894;
      line-height: 22px;
      padding-bottom: 16px;
    }
  }
  ::v-deep .el-textarea__inner {
    background: #F7F8FA;
    border-radius: 4px;
    height:  100% !important;
    font-family: MiSans, MiSans;
    font-size: 16px;
    color: #494E57;
    line-height: 24px;
  }
}

.verifyStatusAnswer {
  ::v-deep .el-textarea__inner {
    background: #fff;
    color: #494C4F;
  }
}
.footerDrawer {
  margin-top: 24px;
  text-align: right;
}
.line {
  width: 4px;
  height: 18px;
  background: #1747E5;
  border-radius: 0px 2px 2px 0px;
  margin-right: 4px;
}
.line-text {
  font-family: MiSans, MiSans;
  font-weight: 500;
  font-size: 18px;
  color: #494E57;
  line-height: 32px;
}
</style>
<style lang="scss">
.details-popover {
  width: 720px;
  background: #FFFFFF;
  box-shadow: 0px 2px 12px 0px rgba(0,0,0,0.1);
  border-radius: 8px;
  border: 1px solid rgba(0,0,0,0.12);
  margin-bottom: 0px;
  .el-popover__title {
    padding: 23px 24px 10px;
    background: #ffffff !important;
    font-weight: 500;
    font-size: 20px;
    color: #494E57;
    line-height: 27px;
    margin-bottom: 0px;
  }
  .details-popover-content {
    padding: 0 24px 24px !important;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #828894;
    line-height: 26px;
    max-height: 396px;
    overflow-y: auto;
  }
  .popper__arrow {
    display: none;
  }
}
.left-ctn-tiaoyou-title{
		   font-size: 16px;
		   color: #333;
		   font-weight: 550;
	   }
.left-ctn-tiaoyou-words{
   font-size: 12px;
   color: #666;
   // margin-top: 10px;
}
.feedback{
	display: flex;
	padding: 10px;
}
.feedback-left{
	margin-right: 10px;
}
.feedback-box{
	font-size: 14px;
}
</style>