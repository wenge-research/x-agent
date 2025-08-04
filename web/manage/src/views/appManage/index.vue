<template>
  <div  v-loading="listLoading" style="height: 100%;overflow: hidden;">
      <div
      class="outer"
    >
      <p class="outerTitle">{{ $t("appmanage") }}</p>
      <div class="appOuter" v-loading="workflowLoading">
        <div class="headerContent">
          <div class="header-left" >
            <el-input
              style="width: 200px;"
              :placeholder="$t('typeInAppName')"
              class="input"
              v-model="applicationName"
              clearable
              @keydown.native.enter="handleSearch"
              @input="handleSearch"
            >
              <el-button
                slot="append"
                icon="el-icon-search"
                @click="getAppList"
              ></el-button>
            </el-input>
            <el-select class="app-type-select" style="width: 200px;margin-right: 16px;" v-model="applicationType" @change="getAppList">
              <el-option value="" label="全部类型"></el-option>
              <el-option v-for="item in applicationTypeLists" :key="item.value" :value="item.value" :label="item.label"></el-option>
            </el-select>
            <!-- <el-select class="app-type-select" style="width: 200px;margin-right: 16px;" v-model="applicationStatus" @change="getAppList">
              <el-option value="" label="全部上架状态"></el-option>
              <el-option value="1" :label="$t('activeStatus')"></el-option>
              <el-option value="2" :label="$t('notEnabled')"></el-option>
            </el-select> -->
            <el-date-picker v-model="dateArr" style="width: 370px;padding-right: 0;" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="datetimerange" :range-separator="$t('to')" :start-placeholder="$t('startDate')" :end-placeholder="$t('endDate')" @change="getAppList">
            </el-date-picker>
          </div>
          <div class="header-right">
            
            <el-button
              type="primary"
              v-permission="'createApp'"
              class="create-btn"
              @click="createApp"
            >
              <img src="@/assets/images/add-circle-fill.svg" />
              <span>创建应用</span>
            </el-button>
            <el-button
              @click="importDSL"
            >
              <span>{{ $t('import')}}DSL</span>
            </el-button>
          </div>
        </div>
        
        <div class="list-box"  >
          <div class="listOuter no-scrollbar" v-if="appList.length">
            <div class="list-content">
              <div
                v-for="(item, index) in appList"
                :key="item.applicationId+index"
                class="listItem"
                @click="previewApp(item)"
              >
                <div v-if="item.publishAppStore == 1" class="listItem-tips">已上架</div>
                <div class="listContent">
                  <div class="head-img-box">
                    <img v-if="item.facadeImageUrl" :src="item.facadeImageUrl" class="headImg" />
                    <img v-else :src="getRandomHeadImgDefaultBgColor(index)" class="defaultHeadImg" />
                    
                  </div>
                  <div class="textContent">
                    <div class="title-content">
                      <p class="title" :title="item.applicationName">{{ item.applicationName }}</p>
                      <div
                        class="sign"
                        v-if="item.publishStatus == 1 || item.publishStatus == 2"
                        :class="`${
                          item.publishStatus == 1 || item.publishStatus == 2
                            ? 'successSign'
                            : 'failSign'
                        }`"
                      > 
                        <i v-if="item.publishStatus == 1 || item.publishStatus == 2" class="el-icon-success" style="color: #3FB816"></i> 
                        
                      </div>
                    </div>
                    <div class="list-type" v-if="item.type && getApplicationTypeLabel(item.type)">
                      <span class="type-item">{{ getApplicationTypeLabel(item.type) }}</span>
                    </div>
                  </div>
                </div>
                <div class="desc-content">
                  <p class="desc" :title="item.introduce">{{ item.introduce }}</p>
                </div>
                <div class="date">
                  <div class="list-update-time">
                    <span class="list-user-icon"><iconpark-icon name="user-3-line" size="16"></iconpark-icon></span>
                    <span class="create-user" v-if="item.accountName">{{ item.accountName }}</span>
                    <span class="point" style="margin-right: 8px"></span>
                    <span>{{ item.updateTime }}</span>
                  </div>
                  <div
                    style="display: flex; align-items: center; cursor: pointer"
                    v-permission="'preview'"
                  >
                    <div class="edit-btn" v-permission="'editeApp'" command="editeApp" @click.stop="editApp(item)" v-if="isAdminOrUser(item)">
                      <iconpark-icon class="edit-icon" color="#494E57" name="edit-box-line" size="15.75"></iconpark-icon>
                      <span style="color: #494E57">{{ $t("edit") }}</span>
                    </div>
                    <div
                      v-if="(permissions('copyApp') || permissions('deleteApp') ||  permissions('grantApp')) && isAdminOrUser(item)"
                      class="opts-box"
                      :class="{ 'opts-box-active': activeIndexMoreClick === index }"
                      @click.stop
                    >
                      <el-dropdown
                        class="opts-box-dropdown"
                        trigger="click"
                        placement="top-end"
                        @visible-change="handleVisibleChange($event, index)"
                        @command="(value) => handleCommand(value, item)"
                      >
                        <span class="el-dropdown-link">
                          <!-- <iconpark-icon name="more-2-line" size="16" color="#494E57"></iconpark-icon> -->
                          <iconpark-icon name="more-line" color="#383838" size="18"></iconpark-icon>
                        </span>
                        <el-dropdown-menu slot="dropdown" class="opts-box-dropdown-menu">
                          <el-dropdown-item
                            v-if="item.copyPermission == '0'"
                            v-permission="'copyApp'"
                            command="copyApp"
                          >
                          <iconpark-icon color="#494E57" name="file-copy-line"></iconpark-icon>
                            <span style="color: #494E57">{{ $t("copy") }}</span>
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
                          <el-dropdown-item
                            command="downloadDSL"
                          >
                            <i class="el-icon-download" style="color: #494E57;"></i>
                            <span style="color: #494E57">{{ $t("export") }}DSL</span>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </el-dropdown>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-if="appList.length" class="pagination">
            <div class="total">
                {{ $t("total") }}{{ pageObj.total }}{{ $t("items") }}
            </div>
            <el-pagination
                @size-change="handleAppListSizeChange"
                @current-change="handleAppListCurrentChange"
                :current-page.sync="pageObj.pageNo"
                :page-sizes="[12, 24, 36, 48]"
                :page-size="pageObj.pageSize"
                background
                layout="prev, pager, next, sizes"
                :total="pageObj.total"
            >
            </el-pagination>
          </div>
          <div v-else class="no-data" style="margin-top: 130px;">
              <img src="@/assets/images/no-data.png" alt="" style=" width: 150px; height: auto; "/>
              <!-- <div class="txt1">{{ $t("noData") }}</div> -->
               <div class="txt1" style="color:#b4bccc;margin:10px 0 52px 0; color: #828894;">没有找到应用</div>
               <!-- <el-button type="primary"  class="addButton" style="width: 240px;"
               size="large" @click="createApp">
               <i class="el-icon-circle-plus-outline iconSize"></i>
                <span>立即创建</span>
               </el-button> -->
          </div>
        </div>
      </div>
      <el-dialog
        :visible.sync="dialogVisible"
        width="100%"
        fullscreen
        :show-close="false"
        class="flexDialog"
        destroy-on-close
        :close-on-click-modal="false"
        :close-on-press-escape="false"
      >
        <!-- 能力设置放入新页面 -->
        <abilitySetting
          v-if="dialogVisible"
          :appConfigForm="editItem"
          @configCloseDialog="configCloseDialog"
          @configShowSecretKey="configShowSecretKey"
        ></abilitySetting>
      </el-dialog>
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
        <div class="headBar">
          <div class="leftSlide">
            <img
              src="@/assets/images/arrow-go-back-fill.svg"
              @click="closePriview"
            />
            <div class="titleIcon">
              <p>{{ previewItem.applicationName }}</p>
              <p>{{ previewItem.introduce }}</p>
            </div>
          </div>
          <div class="rightSlide" v-permission="'editeApp'">
            <el-button class="btn" @click="editApp(previewItem)">
              <img src="@/assets/images/edit-line.svg" />
              <span>{{ $t("edit") }}</span>
            </el-button>
          </div>
        </div>
        <div
          style="
            padding: 20px 24px;
            background: #fff;
            border-radius: 4px;
            min-height: calc(100vh - 112px);
          "
        >
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane
              v-if="permissions('overview')"
              :label="$t('overview')"
              name="first"
            >
              <div class="tabContentTitle">{{ $t("appInfo") }}</div>
              <div class="tabContents">
                <div class="contentInner">
                  <div class="title">
                    <span>{{ $t("publicPublish") }}</span>
                    <el-switch
                      disabled
                      v-model="previewItem.publishStatus"
                      :active-value="'1'"
                    ></el-switch>
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
                    <el-switch
                      v-model="previewItem.publishStatus"
                      :active-value="'2'"
                      disabled
                    ></el-switch>
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
            </el-tab-pane>
            <el-tab-pane
              v-if="permissions('analysis')"
              :label="$t('analysis')"
              name="sixth"
            >
              <!-- 分析 -->
              <Analysis
                v-if="activeName == 'sixth'"
                ref="Analysis"
                :data="previewItem"
              />
            </el-tab-pane>
            <!-- <el-tab-pane
              v-if="permissions('setUp')"
              :label="$t('settings')"
              name="second"
            ></el-tab-pane>
            <el-tab-pane
              v-if="permissions('hitTesting')"
              :label="$t('hitTest')"
              name="third"
            ></el-tab-pane> -->
            <el-tab-pane
              v-if="permissions('dialogueLog')"
              :label="$t('dialogLog')"
              name="fourth"
            >
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
                  style="width: 148px; margin-right: 16px"
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
                <span style="margin-right: 20px">{{ $t("unanswerable") }}</span>
                <el-select
                  style="width: 148px; margin-right: 16px"
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
                <span style="margin-right: 20px">{{
                  $t("likeAndStepOn")
                }}</span>

                <el-select
                  style="width: 148px; margin-right: 16px"
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
                <span style="margin-right: 20px">{{ $t("userType") }}</span>

                <el-select
                  style="width: 148px; margin-right: 16px"
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
                <el-input
                  :placeholder="$t('enterKeywordForQuestionOrAnswer')"
                  style="width: 476px; margin-right: 16px; margin-top: 12px"
                  v-model="text"
                  clearable
                >
                </el-input>
                <span style="margin-right: 20px">{{ $t("sourceUser") }}</span>
                <el-input
                  :placeholder="$t('pleaseEnter')"
                  style="width: 276px; margin-right: 16px; margin-top: 12px"
                  v-model="userName"
                  clearable
                  @keydown.native.enter="searchDialog"
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
              </div>
              <el-table
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
                  width="80"
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
                      style="color: #6295fc"
                      >{{ $t("view") }}</el-button
                    >
                    <el-button
                      @click="viewScore(scope.row, scope.$index)"
                      type="text"
                      style="color: #24c131"
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
            </el-tab-pane>
            <el-tab-pane
              :label="$t('intelligentDistribution')"
              name="eighth"
              v-if="permissions('intelligentDistribution')"
            >
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
            </el-tab-pane>
            <el-tab-pane
              v-if="permissions('answerReview')"
              :label="$t('answerReview')"
              name="fifth"
            >
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
                  $t("verificationOffice")
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
                  v-model="deptId"
                  :show-all-levels="false"
                  style="width: 200px; margin-right: 16px"
                ></el-cascader>

                <span style="margin-right: 20px" v-if="activeName == 'fifth'">{{
                  $t("reviewStatus")
                }}</span>
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
              </div>
              <el-table
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
                      placement="top-start"
                      :title="scope.row.question"
                      width="400"
                      trigger="click"
                    >
                      <div style="padding: 0 12px 12px">
                        {{ scope.row.verifyAnswer }}
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
                      style="color: #6295fc"
                      >{{ $t("review") }}</el-button
                    >
                    <el-button
                      v-else
                      @click="reviewAnswers(scope.row, scope.$index)"
                      type="text"
                      style="color: #6295fc"
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
            </el-tab-pane>
            <!-- 配置管理 -->
            <el-tab-pane
              v-if="permissions('configManage')"
              :label="$t('configurationManagement')"
              name="seventh"
            >
              <ConfigManage
                v-if="activeName == 'seventh'"
                ref="ConfigManage"
                :data="previewItem"
              />
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-dialog>
      <!-- 溯源 -->
      <el-dialog
        :title="$t('viewTraceability')"
        :visible.sync="sourceVisible"
        width="600px"
        class="deleteDialog"
        destroy-on-close
        :close-on-click-modal="false"
        :close-on-press-escape="false"
      >
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
                    font-size: 16px;
                    color: #646479;
                    margin-bottom: 16px;
                    cursor: pointer;
                  "
                >
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
        <p style="text-align: center" v-else>{{ $t("noData") }}</p>
        <span slot="footer" class="dialog-footer">
          <el-button @click="cancelSource">{{ $t("cancel") }}</el-button>
        </span>
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
            <div class="flex aligns">
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
              <div
                :class="['userList', activeIndex === 2 ? 'activeList' : '']"
                @click="clickUser(2)"
                v-permission="'rewriting'"
              >
                {{ $t("rewritingProblem") }}
              </div>
              <div
                :class="['userList', activeIndex === 3 ? 'activeList' : '']"
                @click="clickUser(3)"
                v-permission="'wholeProcess'"
              >
                {{ $t("wholeProcess") }}
              </div>
            </div>
            <div class="flex-center just" style="width: 49.5%">
              <div class="flex-center">
                <div
                  style="
                    width: 3px;
                    height: 18px;
                    background: #1c50fd;
                    margin-right: 8px;
                  "
                ></div>
                <span
                  style="
                    font-family: MiSans, MiSans;
                    font-weight: 500;
                    font-size: 18px;
                    color: #383d47;
                  "
                  >{{ $t("verify") }}</span
                >
              </div>
              <div class="flex-center">
                <!-- 已核实正确情况 -->
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
              </div>
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
            <div v-if="activeIndex == 3" class="traceability">
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

            <div v-if="activeIndex == 1" class="traceability">
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
                          margin-bottom: 16px;
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
              v-if="activeIndex == 0"
            ></el-input>
            <el-input
              v-model="formList.verifyAnswer"
              type="textarea"
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
          <div class="footerDrawer">
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
            <el-button @click="handleClose">{{ $t("close") }}</el-button>
          </div>
        </div>
      </el-drawer>
      <drawerAnswer
        v-if="addQaVisibleAnswer"
        :addQaVisibleAnswer="addQaVisibleAnswer"
        :setForm="setForm"
        @handlecloseDraw="handlecloseDraw"
        @handleAddQaDialogAns="handleAddQaDialogAns"
      ></drawerAnswer>
      <!-- 授权 -->
      <el-dialog
        :title="$t('authorizeTo')"
        :visible.sync="grantVisible"
        top="10vh"
        width="720px"
        class="grantDialog"
        destroy-on-close
        :close-on-click-modal="false"
        :close-on-press-escape="false"
      >
        <GrantData
          :data-id="grantData.applicationId"
          v-if="grantVisible"
          data-type="app"
          :queryCurrentTenantUserFlag="true"
          @cancelGrant="cancelGrant"
        ></GrantData>
      </el-dialog>
      <!-- 创建问答助手 名称弹窗 -->
      <createApplication
        v-if="dialogVisibleApplication"
        :dialogVisibleApplication="dialogVisibleApplication"
        :type="'add'"
        @cancelApplication="cancelApplication"
        @confirmApplication="confirmApplication"
      ></createApplication>
      <!-- 删除应用 -->
      <delApplication
        v-if="deleteDialogVisible"
        :deleteDialogVisible="deleteDialogVisible"
        @configCancelDelete="configCancelDelete"
        :params="deleteItem"
      ></delApplication>

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
    </div>
    <el-drawer
      v-if="dialogVisibleKnowLedge"
      :visible.sync="dialogVisibleKnowLedge"
      size="95%"
      :show-close="false"
      class="knowLedgeDetailDrawer"
      :withHeader="false"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
      
    >
      <KbmCreate
        v-if="dialogVisibleKnowLedge"
        :rowData="rowData"
        :type="typelist"
        :isAppManage="true"
        @clickBack="clickBack"
      />
    </el-drawer>
    <!-- 应用详情 -->
    <AppDetails ref="appDetailsRef" @editApp="editApp" ></AppDetails>

    <ImportApplication :visible="importVisible" @closeImport="importVisible = false" @updateList="updateList"></ImportApplication>
    </div>
 
</template>
<script>
const { v4: uuidv4 } = require('uuid');
import {
  appList,
  modelList,
  getAuthChannels,
  addApplication,
  dialogRecord,
  sourceList,
  templateList,
  ttsList,
  copyApp,
  defaultApp,
  verifyRecord,
  deptTree,
  getCheckRecord,
  recordCheckRecord,
  getReviseQuestion,
  getAllStepByDialogId,
  apiApplicationInfoExporte,
} from "@/api/app";
import { getKnowledgeInfoList } from "@/api/index";
import drawerAnswer from "./components/drawerAnswer";
import publishWay from "./components/publishWay";
import axios from "axios";
import GrantData from "@/views/appManage/components/GrantData.vue";
import createApplication from "@/views/appManage/components/createApplication.vue";
import abilitySetting from "@/views/appManage/components/abilitySetting.vue";
import Analysis from "./components/analysis.vue";
import ConfigManage from "./components/configManage.vue";
import delApplication from "./components/deleteApplication.vue";
import KbmCreate from "@/views/Kbm/component/KbmCreate.vue";
import ScoreView from "@/views/appManage/components/scoreView.vue";
import AppDetails from './components/appDetails.vue';
import { applicationTypes } from '@/utils/constants';
import { debounce } from 'lodash';
import ImportApplication from "@/views/appManage/components/importApplication.vue"

export default {
  props: ['openDialog'],
  components: {
    ScoreView,
    drawerAnswer,
    GrantData,
    createApplication,
    abilitySetting,
    publishWay,
    delApplication,
    Analysis,
    ConfigManage,
    KbmCreate,
    AppDetails,
    ImportApplication
  },
  data() {
    return {
      pageObj: {
        pageNo: 1,
        pageSize: 24,
        total: 0,
      },
      dateArr: [],
      // applicationStatus: "",
      listLoading: false,
      workflowLoading: false,
      defaultImage: require('@/assets/images/default-application-logo.svg'), // 默认图像的URL
      ttsListData: [],
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      sourceTableList: [],
      reviseQuestion: {},
      wholeProcessList: {},
      sourceVisible: false,
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
      dateRange: [],
      question: "",
      answer: "",
      currentPage: 1,
      dialogTotal: 0,
      dialogPagesize: 10,
      dialogTableData: [],
      activeName: "first",
      previewItem: "",
      previewDialogVisible: false,
      deleteItem: "",
      applicationName: "",
      applicationType: "",
      appForm: {},
      appList: [],
      dialogVisibles: false,
      dialogVisible: false,
      deleteDialogVisible: false,
      fabuDialogVisible: false,
      editItem: {},
      workPosition: "",
      workPositionColumns: [],
      addQaVisible: false,
      setForm: {},
      activeIndex: 0,
      formList: {},
      text: "",
      userName: "",
      verifyDeptName: "",
      verifyStatus: "",
      noAnswerFlag: "",
      feedbackType: "",
      userType: "",
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
      treeData: [],
      verifyDeptId: "",
      distritDeptId: "",
      setIndex: "",
      dialogTableDataAnswer: [],
      deptId: "",
      auditStatus: "",
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
      addQaVisibleAnswer: false,
      grantVisible: false,
      grantData: {},
      dialogVisibleApplication: false,
      scoreQaVisible: false,
      dialogVisibleKnowLedge: false,
      rowData: {},
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
      applicationTypeLists: applicationTypes,
      activeIndexMoreClick: null,
      importVisible: false
    };
  },
  computed: {
    //用于下面的resize 改变图表尺寸，在容器大小发生改变时需要手动调用
    KonwledgeList() {
      return this.$store.state.tab.KonwledgeList;
    },
    isAdminOrUser(){
      return (data)=>{
        let obj=JSON.parse(sessionStorage.getItem("user"))
        return obj.powerType==0 || obj.accountName == data.createUser
      }
    }
  },
  watch: {
    //监听isCollapse 因为头部水平扩展是一个动画需要时间，所以这里延迟300毫秒
    KonwledgeList(val) {
      console.log(val, 444);
      this.konwlwdgeClick(val);
    },
  },
  created() {
    this.getAppList();
    this.getDeptTree();
  },
  mounted() {
    setTimeout(() => {
      if(this.openDialog) {
        this.editItem = {};
        this.defaultAppInfo();
        this.dialogVisibleApplication = true;
        this.$router.replace({name: 'appmanage'})
      }
    }, 500);
  },
  
  methods: {
    updateList(){
      this.getAppList();
      this.getDeptTree();
    },
    importDSL(){
      this.importVisible = true
    },
    async downloadDSL(data){
      apiApplicationInfoExporte(data).then((res)=>{
        const url = window.URL.createObjectURL(new Blob([res]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", data.applicationName + ".xlsx");
        document.body.appendChild(link);
        link.click();
      })
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
    sendMessage() {
      const iframe = this.$refs.iframe;
      if (iframe) {
        const iframeWindow = iframe.contentWindow;
        // 现在可以安全地使用iframeWindow对象了
        iframeWindow.postMessage({ param1: "测试传入子组件的参数" }, "*");
      }
    },
    handleCommand(value, item) {
      if (value == "copyApp") this.copyAppInfo(item);
      if (value == "editeApp") this.editApp(item);
      if (value == "deleteApp") this.openDelete(item);
      if (value == "grantApp") this.grantApp(item);
      if (value == "downloadDSL") this.downloadDSL(item)
    },
    copyAppInfo(item) {
      this.$confirm(this.$t("continueCopyApp"), this.$t("tips"), {
        confirmButtonText: this.$t("confirm"),
        cancelButtonText: this.$t("cancel"),
        type: "warning",
      })
        .then(() => {
          this.listLoading = true
          copyApp({ applicationId: item.applicationId }).then((res) => {
          this.listLoading = false
            if (res.code == "000000") {
              this.getAppList();
              this.$message({
                type: "success",
                message: this.$t("copySuccessed"),
              });
            } else {
              this.$message({
                type: "error",
                message: this.$t("copyFailed"),
              });
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: this.$t("cancelCopy"),
          });
        });
    },
    openPreview() {
      window.open(this.previewItem.clientLink, "_blank");
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
    searchDialog() {
      this.currentPage = 1;
      if (this.activeName == "fourth" || this.activeName == "eighth") {
        this.getDialogRecord();
      } else if (this.activeName == "fifth") {
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
    devBuilding() {
      this.$message({
        type: "warning",
        message: this.$t("underConstruction"),
      });
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
    previewApp(item) {
      if(!this.permissions('preview')) return;
      this.$refs.appDetailsRef.openDialog(item);
      // this.activeName = "first";
      // this.previewItem = item;
      // this.previewDialogVisible = true;
    },
    editApp(item) {
      this.editItem = item;
      this.dialogVisible = true;
    },
    grantApp(item) {
      this.grantData = item;
      this.grantVisible = true;
    },
    closePriview() {
      this.previewDialogVisible = false;
      this.getAppList();
    },
    handleSearch: debounce(function() {
      this.pageObj.pageNo = 1;
      this.getAppList();
    }, 500),
    statusToSrt(status) {
      switch (status) {
        case "1":
          return this.$t("publicPublish");
        case "2":
          return this.$t("privatePublish");
        case "3":
          return this.$t("unPublish");
        case "4":
          return this.$t("temporaryStorage");
        case "5":
          return this.$t("stopUsing");
      }
    },
    openDelete(item) {
      this.deleteItem = item;
      this.deleteDialogVisible = true;
    },
    configCancelDelete() {
      console.log('configCancelDelete');
      
      this.deleteDialogVisible = false;
      this.getAppList();
    },
    // createAppTwo() {
    //   this.editItem = {};
    //   this.dialogVisible = true;
    // },
    cancelApplication() {
      this.dialogVisibleApplication = false;
    },
    createApp() {
      this.editItem = {};
      this.defaultAppInfo();
      this.dialogVisibleApplication = true;
    },
    configCloseDialog() {
      this.dialogVisible = false;
      this.getAppList();
    },
    getAppList() {
      console.log('getAppList')
      this.workflowLoading = true;
      appList({
        pageNo: this.pageObj.pageNo,
        pageSize: this.pageObj.pageSize,
        order: "",
        sort: "",
        startTime: this.dateArr ? this.dateArr[0]:null,
        endTime: this.dateArr ? this.dateArr[1]:null,
        applicationName: this.applicationName,
        type: this.applicationType ? this.applicationType : "",
      }).then((res) => {
        this.workflowLoading = false;
        if (res.code == "000000") {
          this.appList = res.data?.records;
          
          this.appList.forEach(item=>{
             if(new Date().getYear()  == new Date(item.updateTime).getYear()){
              if(new Date().getMonth() == new Date(item.updateTime).getMonth() ){
                if(new Date().getDate() == new Date(item.updateTime).getDate()){
                   item.updateTime = `最近编辑：${new Date(item.updateTime).getHours()<10?'0'+new Date(item.updateTime).getHours():new Date(item.updateTime).getHours()}:${new Date(item.updateTime).getMinutes()<10?'0'+new Date(item.updateTime).getMinutes():new Date(item.updateTime).getMinutes()}`
                }else{
                  item.updateTime = `最近编辑: ${(new Date(item.updateTime).getMonth()+1)<10?'0'+(new Date(item.updateTime).getMonth()+1):(new Date(item.updateTime).getMonth()+1)}-${new Date(item.updateTime).getDate()<10?'0'+new Date(item.updateTime).getDate():new Date(item.updateTime).getDate()} ${new Date(item.updateTime).getHours()<10?'0'+new Date(item.updateTime).getHours():new Date(item.updateTime).getHours()}:${new Date(item.updateTime).getMinutes()<10?'0'+new Date(item.updateTime).getMinutes():new Date(item.updateTime).getMinutes()}`
                }
              }else{
                  item.updateTime = `最近编辑: ${(new Date(item.updateTime).getMonth()+1)<10?'0'+(new Date(item.updateTime).getMonth()+1):(new Date(item.updateTime).getMonth()+1)}-${new Date(item.updateTime).getDate()<10?'0'+new Date(item.updateTime).getDate():new Date(item.updateTime).getDate()} ${new Date(item.updateTime).getHours()<10?'0'+new Date(item.updateTime).getHours():new Date(item.updateTime).getHours()}:${new Date(item.updateTime).getMinutes()<10?'0'+new Date(item.updateTime).getMinutes():new Date(item.updateTime).getMinutes()}`
              }
                }else{
                   item.updateTime = `最近编辑: ${item.updateTime}`
                }
          })

          // this.appList.forEach(item=>{
          //   item.updateTime = this.formatEditTime(item.updateTime)
          // })
          this.pageObj.total = res.data?.totalRow || 0;
        } else {
          this.appList = [];
        }
      }).catch((err) => {
        console.log('getAppList-err', err)
        this.workflowLoading = false;
      }).finally(()=> {
        this.$nextTick(() => {
            this.workflowLoading = false; // 确保DOM更新后再关闭loading
        });
      });
    },
    handleAppListCurrentChange(page) {
        this.pageObj.pageNo = page;
        this.getAppList();
    },
    handleAppListSizeChange(size) {
      this.pageObj.pageSize = size;
      this.pageObj.pageNo = 1;
        this.getAppList();
    },
    handleVisibleChange(val, index) {
      if(val){
        this.activeIndexMoreClick = index;
      }else {
        this.activeIndexMoreClick = null;
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
    viewClick(row, index) {
      this.setForm = row;
      this.setIndex = index;
      this.$set(this.formList, "verifyAnswer", row.verifyAnswer || row.answer);
      this.activeIndex = 0;
      this.$set(this.formList, "verifyStatus", "");
      this.addQaVisible = true;
    },
    handleAddQaDialog(type) {
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
            if (this.setIndex > this.dialogPagesize) {
              return this.$message.warning(this.$t("isLastDataOnPage"));
            }
            this.setForm = this.dialogTableData[this.setIndex];
            this.$set(this.formList, "verifyStatus", "");
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
    submitVerification() {
      this.handleAddQaDialog(1);
    },
    handleClose() {
      this.addQaVisible = false;
      this.getDialogRecord();
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
    cancelGrant() {
      this.grantVisible = false;
    },
    getDefaultParameters() {
      this.defaultAppInfo();
    },
    defaultAppInfo() {
      defaultApp({}).then((res) => {
        if (res.code == "000000") {
          this.appForm = res.data;
          this.editItem = res.data;
        }
      });
    },
    async confirmApplication(val) {
      this.dialogVisibleApplication = false;
      this.appForm.applicationName = val.applicationName;
      this.appForm.introduce = val.introduce;
      this.appForm.facadeImageUrl = val.facadeImageUrl;
      this.appForm.applicationCode = val?.applicationCode;
      this.appForm.type = val.type;
      this.appForm.apiSecret = null
      this.appForm.createTime = null
      this.appForm.createUser = null
      this.appForm.updateTime = null
      this.appForm.updateUser = null
      this.appForm.tenantId = null
      this.appForm.webTitle = this.appForm.applicationName;
      this.appForm.webIcon = this.appForm.facadeImageUrl;
      this.makeType = ""
      const res = await addApplication({
        ...this.appForm,
       
      })
      if(res.code == '000000') {
        this.editItem = res.data;
        this.dialogVisible = true;
      }
      
    },
    configShowSecretKey(val) {
      this.dialogVisible = false;
      this.previewApp(val);
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
    konwlwdgeClick(row) {
      if(!row) {
        this.$store.commit('setKonwledge', null);
        if(this.dialogVisibleKnowLedge) this.dialogVisibleKnowLedge = false;
      }else {
        this.dialogVisibleKnowLedge = true;
        this.typelist = "view";
        this.rowData = row;
      }
      
    },
    clickBack(val) {
      this.dialogVisibleKnowLedge = val;
      this.$store.commit('setKonwledge', null);
    },
    viewScore(row, index) {
      this.setForm = row;
      this.setIndex = index;
      this.scoreQaVisible = true;
    },
    getRandomHeadImgDefaultBgColor(index) {
      const colors = ['#5B90F9', '#BF8EE8', '#43CBC5', '#558CF8', '#EAB778', '#EB8293', '#8874E8'];
      const imgList = [
          require('@/assets/images/appManagement/default-logo-1.svg'),
          require('@/assets/images/appManagement/default-logo-2.svg'),
          require('@/assets/images/appManagement/default-logo-3.svg'),
          require('@/assets/images/appManagement/default-logo-4.svg'),
          require('@/assets/images/appManagement/default-logo-5.svg'),
          require('@/assets/images/appManagement/default-logo-6.svg'),
          require('@/assets/images/appManagement/default-logo-7.svg'),
      ];
      if (index !== undefined && typeof index === 'number' && index >= 0) {
        return imgList[index % imgList.length];
      }
      const randomIndex = Math.floor(Math.random() * imgList.length);
      return imgList[randomIndex];
    },
    getApplicationTypeLabel(value) {
      return applicationTypes.find(item => item.value === value)?.label || ""
    }
  },
};
</script>
<style lang="scss">
.el-tooltip__popper.is-dark {
  max-width: 580px !important;
}

.create-btn{

  img{
    margin-right: 8px;
  }
}

</style>
<style lang="scss" src="@/views/appManage/commonList.scss" scoped></style>
<style lang="scss"  src="@/assets/scss/dropdown.scss"></style>
