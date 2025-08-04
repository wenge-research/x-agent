<template>
  <div style="height: 100%;overflow: hidden;">
  <div class="outer" v-if="!dialogVisibleKnowLedge">
    <p class="outerTitle">{{ $t("intelligentSearch") }}</p>
    <div class="appOuter" v-loading="workflowLoading">
      <div class="headerContent">
        <el-input
          :placeholder="$t('pleaseEnterKeyword')"
          class="input"
          v-model="applicationName"
          clearable
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="getAppList"
          ></el-button>
        </el-input>
        <el-button type="primary" v-permission="'createApp'" @click="createApp">
          <img src="@/assets/images/add-circle-fill.svg" />
          <span>{{ $t("createIntelligentSearch") }}</span>
        </el-button>
      </div>
      <div class="list-box">
        <div class="listOuter" v-if="appList.length">
          <div class="list-content">
            <div
              v-for="item in appList"
              :key="item.applicationId"
              class="listItem"
              @click="previewApp(item)"
            >
              <div class="listContent">
                <div class="head-img-box" :style="item.logo ? '' : `background:${getRandomHeadImgDefaultBgColor(index)}`">
                  <img v-if="item.logo" :src="item.logo" class="headImg" />
                  <img v-else :src="defaultImage" class="defaultHeadImg" />
                  <div class="item-icon">
                    <img src="@/assets/images/icon-zhinengti.svg" alt="">
                  </div>
                </div>
                <div class="textContent">
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
                    <span class="point" v-if="item.publishStatus == 1 || item.publishStatus == 2"></span>
                    <span>{{ statusToSrt(item.publishStatus) }}</span>
                  </div>
                </div>
              </div>
              <div class="desc-content">
                <p class="desc" :title="item.introduce">{{ item.introduce }}</p>
              </div>
              <div class="date">
                <div class="list-update-time">
                  <span style="margin-right: 8px">最近编辑</span>
                  <span>{{ item.updateTime }}</span>
                </div>
                <div
                  style="display: flex; align-items: flex-end; cursor: pointer"
                  v-permission="'preview'"
                >
                  <div
                    class="opts-box" @click.stop
                  >
                    <el-dropdown
                      class="opts-box-dropdown"
                      trigger="click"
                      @command="(value) => handleCommand(value, item)"
                    >
                      <span class="el-dropdown-link">
                        <iconpark-icon name="more-2-line" size="16"></iconpark-icon>
                      </span>
                      <el-dropdown-menu slot="dropdown" class="opts-box-dropdown-menu">
                        <el-dropdown-item
                          v-permission="'copyApp'"
                          command="copyApp"
                        >
                        <iconpark-icon color="#494E57" name="file-copy-line"></iconpark-icon>
                          <span style="color: #494E57">{{ $t("copy") }}</span>
                        </el-dropdown-item>
                        <el-dropdown-item
                          v-permission="'editeApp'"
                          command="editeApp"
                        >
                        <iconpark-icon color="#494E57" name="edit-line"></iconpark-icon>
                          <span style="color: #494E57">{{ $t("edit") }}</span>
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
      <div v-else class="no-data">
          <img src="@/assets/images/no-data.png" alt="" />
          <div class="txt1">{{ $t("noData") }}</div>
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
                    <el-button slot="reference">{{ $t("viewKey") }}</el-button>
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
              max-height="600px"
              style="width: 100%"
              class="headTable"
            >
              <el-table-column
                prop="question"
                :label="$t('question')"
              ></el-table-column>
              <el-table-column prop="answer" :label="$t('answer')" width="100">
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
                      scope.row.verifyStatus == 1 || scope.row.verifyStatus == 2
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
              <el-table-column fixed="right" :label="$t('action')" width="120">
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
              <el-table-column fixed="right" :label="$t('action')" width="100">
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
      size="50%"
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
                  <el-radio :label="3">{{ $t("maliciousQuestion") }}</el-radio>
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
                <el-popover placement="left-start" width="300" trigger="hover">
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
      width="750px"
      top="10vh"
      class="grantDialog"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <GrantData
        :data-id="grantData.applicationId"
        v-if="grantVisible"
        data-type="app"
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
    <KbmCreate
      v-if="dialogVisibleKnowLedge"
      :rowData="rowData"
      :type="typelist"
      :isAppManage="true"
      @clickBack="clickBack"
    />
  </div>
</template>
<script>
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
} from "@/api/app";
import { getKnowledgeInfoList } from "@/api/index";
import drawerAnswer from "./components/drawerAnswer";
import publishWay from "./components/publishWay";
import axios from "axios";
import GrantData from "@/views/appManage/components/GrantData.vue";
import createApplication from "@/views/intelligentSearch/components/createApplication.vue";
import abilitySetting from "@/views/intelligentSearch/components/abilitySetting.vue";
import Analysis from "./components/analysis.vue";
import ConfigManage from "./components/configManage.vue";
import delApplication from "@/views/appManage/components/deleteApplication.vue";
import KbmCreate from "@/views/Kbm/component/KbmCreate.vue";
import ScoreView from "@/views/intelligentSearch/components/scoreView.vue";

export default {
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
    KbmCreate
  },
  data() {
    return {
      pageObj: {
        pageNo: 1,
        pageSize: 12,
        total: 0,
      },
      workflowLoading: false,
      defaultImage: require('@/assets/images/applicationlogo.svg'), // 默认图像的URL
      ttsListData: [],
      actionUrl: `${process.env.VUE_APP_API_NEW}/wos/file/upload`,
      sourceTableList: [],
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
      verifyDeptName: "",
      verifyStatus: "",
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
          text:  this.$t("resident"),
          value: "gov",
        },
        {
          text: this.$t("staff"),
          value: "staff",
        },
      ],
      likeAndStepOnList: [
        {
          text: this.$t("like"),

          value: 1,
        },
        {
          text: this.$t("dislike"),

          value: 0,
        },
      ],
      unanswerableList: [
        {
          text: this.$t("yes"),

          value: true,
        },
        {
          text: this.$t("no"),

          value: false,
        },
      ],
      noAnswerFlag: "",
      feedbackType: "",
      userType: "",
    };
  },
  computed: {
    //用于下面的resize 改变图表尺寸，在容器大小发生改变时需要手动调用
    KonwledgeList() {
      return this.$store.state.tab.KonwledgeList;
    },
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
  methods: {
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
    },
    copyAppInfo(item) {
      this.$confirm(this.$t("continueCopyApp"), this.$t("tips"), {
        confirmButtonText: this.$t("confirm"),
        cancelButtonText: this.$t("cancel"),
        type: "warning",
      })
        .then(() => {
          copyApp({ applicationId: item.applicationId }).then((res) => {
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
      sourceList({ dialogueId: row.dialogueId }).then((res) => {
        if (res.code == "000000") {
          this.sourceTableList = res.data.sourceAnswerResultList || [];
          this.sourceTableList.forEach(item => {
            item.text = this.translateHtml(item.text)
          })
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
        fromTab: this.activeName == "eighth" ? 'allocate' : ''
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
      this.verifyStatus = "";
      this.noAnswerFlag = "";
      this.feedbackType = "";
      this.userType = "";
      this.verifyDeptId = "";
      this.distritDeptId = "";
      this.deptId = "";
      this.auditStatus = "";
      if (tab.name == "fourth" || tab.name == "eighth") {
        this.getDialogRecord();
      } else if (tab.name == "fifth") {
        this.getDialogRecordAnswer();
      }
    },
    previewApp(item) {
      if(!this.permissions('preview')) return;
      this.activeName = "first";
      this.previewItem = item;
      this.previewDialogVisible = true;
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
      this.workflowLoading = true;
      appList({
        pageNo: this.pageObj.pageNo,
                pageSize: this.pageObj.pageSize,
        order: "",
        sort: "",
        applicationName: this.applicationName,
        type: 'search'
      }).then((res) => {
        this.workflowLoading = false;
        if (res.code == "000000") {
          this.appList = res.data?.records;
          this.pageObj.total = res.data?.totalRow || 0;
        } else {
          this.appList = [];
        }
      });
    },
    handleAppListCurrentChange(page) {
        this.pageObj.pageNo = page;
        this.getAppList();
    },
    handleAppListSizeChange(size) {
        this.pageObj.pageSize = size;
        this.getAppList();
    },
    exportDialog() {
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
          question: this.question,
          answer: this.answer,
          applicationId: this.previewItem.applicationId,
          pageNo: this.currentPage,
          pageSize: this.dialogPagesize,
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
    confirmApplication(val) {
      this.dialogVisibleApplication = false;
      this.appForm.applicationName = val.applicationName;
      this.appForm.introduce = val.introduce;
      this.appForm.logo = val.logo;
      this.appForm.applicationCode = val?.applicationCode; 
      this.appForm.apiSecret = null
      this.appForm.createTime = null
      this.appForm.createUser = null
      this.appForm.updateTime = null
      this.appForm.updateUser = null
      this.appForm.tenantId = null
      this.dialogVisible = true;
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
      this.dialogVisibleKnowLedge = true;
      this.typelist = "view";
      this.rowData = row;
    },
    clickBack(val) {
      this.dialogVisibleKnowLedge = val;
    },
    viewScore(row, index) {
      this.setForm = row;
      this.setIndex = index;
      this.scoreQaVisible = true;
    },
  },
};
</script>
<style lang="scss" scoped>
.outer {
  height: 100%;
  padding: 24px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  .hideBox ::v-deep .el-upload--picture-card {
    display: none;
  }
  ::v-deep .headTable th {
    background: #f2f5fa;
  }
  ::v-deep .heightInput {
    .el-input-group__append {
      border-radius: 0 4px 4px 0;
    }
    .el-input__inner {
      border-radius: 4px 0 0 4px;
    }
  }
  ::v-deep .el-switch .el-switch__label.is-active {
    color: #3666ea;
  }
  ::v-deep .el-switch.is-checked .el-switch__core {
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%) !important;
    border-radius: 12px;
    border-color: transparent;
  }
  ::v-deep .el-slider__runway {
    height: 8px;
  }
  ::v-deep .el-slider__bar {
    height: 8px;
    background: rgba(28, 80, 253, 0.3);
  }
  ::v-deep .el-slider__button {
    width: 24px;
    height: 24px;
    background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
    box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.1);
    border: 1px solid #ffffff;
  }
  ::v-deep .el-radio {
    .el-radio__label {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #383d47;
      line-height: 20px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
    &.is-checked {
      .el-radio__label {
        color: #383d47;
      }
      .el-radio__inner {
        background: #3666ea;
        border-color: #3666ea;
      }
    }
  }
  .header {
    width: 100%;
    height: 64px;
    background: #f3f5fb;
    display: flex;
    align-items: center;
    padding: 0 10px;
    flex-shrink: 0;

    .title {
      font-size: 18px;
      font-weight: bold;
    }
  }
  .outerTitle {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 22px;
    color: #383d47;
    line-height: 28px;
    text-align: left;
    font-style: normal;
    margin-bottom: 16px;
  }
  .appOuter {
    flex: 1;
    background: #fff;
    // min-height: calc(100vh - 100px);
    // border-radius: 4px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    .headerContent {
      display: flex;
      justify-content: right;
      margin-bottom: 24px;
      ::v-deep .input {
        width: 344px;
        margin-right: 16px;
        .el-input__inner {
          border: 8px;
          border-radius: 4px 0 0 4px;
          border: 1px solid #e1e4eb;
          border-right: 0px;
        }
        .el-input-group__append {
          border-radius: 0 4px 4px 0;
          padding: 0 12px;
          background-color: transparent;
          .el-button {
            border: none;
          }
        }
      }
      .el-button--primary {
        border: none;
        border-radius: 4px;
        background: #3666ea;

        img {
          width: 15px;
          height: 15px;
          margin-right: 5px;
        }
        span,
        img {
          vertical-align: middle;
        }
      }
    }
    .list-box {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }
    .listOuter {
      padding-right: 8px;
      flex: 1;
      overflow: auto;
      .list-content {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
        flex-wrap: wrap;
        grid-gap: 24px 24px;
      }
      .listItem {
        height: 188px;
        padding: 16px;
        position: relative;
        background: #FFFFFF;
        border-radius: 4px;
        border: 1px solid #D5D8DE;
        cursor: pointer;

        .sign {
          bfont-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          line-height: 20px;
          text-align: left;
          font-style: normal;
          line-height: 20px;
          > img {
            width: 15px;
            height: 15px;
            margin-right: 2px;
          }
          > img,
          > span {
            vertical-align: middle;
          }
        }
        .successSign {
          color: #1abc7c;
          .point {
            display: inline-block;
            width: 5px;
            height: 5px;
            background: #3FB816;
            border-radius: 50%;
            margin: 0 4px;
          }
        }
        .failSign {
          color: #768094;
          // background: rgba(118, 128, 148, 0.1);
        }

        .listContent {
          display: flex;
          justify-content: flex-start;
          margin-bottom: 16px;

          // align-items: center;
          // gap: 16px;
          // padding: 16px;
          .head-img-box {
            min-width: 56px;
            width: 56px;
            height: 56px;
            border-radius: 8px 0px 0px 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 16px;
            background: #F2F4F7;
            border-radius: 2px;
            position: relative;
          }
          .headImg {
            width: 56px;
            border-radius: 8px 0px 0px 8px;
            // height:  144px;
          }
          .defaultHeadImg {
            width: 31px;
            height: 31px;
          }
          .item-icon {
            display: inline-flex;
            justify-content: center;
            align-items: center;
            width: 20px;
            height: 20px;
            background: #FFFFFF;
            box-shadow: 0px 2px 4px 0px rgba(0,0,0,0.1);
            position: absolute;
            bottom: -8px;
            right: -8px;
            z-index: 2;
            border-radius: 50%;
            img {
              width: 14px;
              height: 14px;
            }
          }
          .textContent {
            width: 100%;
            // padding: 16px;
            font-family: MiSans, MiSans;
            position: relative;
            overflow: hidden;

            .title {
              width: 100%;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              font-weight: 500;
              font-size: 18px;
              color: #494E57;
              line-height: 24px;
              text-align: left;
              font-style: normal;
              margin-bottom: 8px;
            }

            .desc {
              font-weight: 400;
              font-size: 14px;
              color: #828894;
              line-height: 24px;
              text-align: left;
              font-style: normal;
              width: calc(100% - 32px);
              position: absolute;
              left: 16px;
              bottom: 16px;
              display: flex;
              align-items: center;
              justify-content: space-between;
              .period-box {
                display: flex;
                align-items: center;
                .point {
                  width: 8px;
                  height: 8px;
                  position: relative;
                  background: rgba(28, 80, 253, 0.1);
                  border-radius: 50%;
                  margin: 4px;
                  &::before {
                    content: "";
                    width: 4px;
                    height: 4px;
                    background: #1c50fd;
                    border-radius: 50%;
                    position: absolute;
                    left: 2px;
                    top: 2px;
                  }
                }
              }
            }
            .desc-content {
              .desc {
                width: 100%;
                display: -webkit-box;
                -webkit-box-orient: vertical;
                -webkit-line-clamp: 2;
                overflow: hidden;
                text-overflow: ellipsis;
                font-weight: 400;
                font-size: 14px;
                color: #828894;
                text-align: left;
                font-style: normal;
                line-height: 22px;
              }
            }
            .date {
              font-weight: 400;
              font-size: 12px;
              color: #828894;
              line-height: 24px;
              text-align: left;
              font-style: normal;
              width: calc(100% - 32px);
              position: absolute;
              left: 16px;
              bottom: 16px;
              display: flex;
              align-items: flex-end;
              justify-content: space-between;
              .period-box {
                display: flex;
                align-items: center;
                .point {
                  width: 8px;
                  height: 8px;
                  position: relative;
                  background: rgba(28, 80, 253, 0.1);
                  border-radius: 50%;
                  margin: 4px;
                  &::before {
                    content: "";
                    width: 4px;
                    height: 4px;
                    background: #1c50fd;
                    border-radius: 50%;
                    position: absolute;
                    left: 2px;
                    top: 2px;
                  }
                }
              }
            }
            // >p:last-child{
            //     padding: 1px 4px 4px;
            //     display: inline-block;
            //     height: 24px;
            //     background: #fff;
            //     border-radius: 4px;
            //     >img {
            //         width: 15px;
            //         height: 15px;
            //         border-radius: 4px;
            //         margin-right: 3px;
            //     }
            //     >span{
            //         font-family: MiSans, MiSans;
            //         font-weight: 400;
            //         font-size: 14px;
            //         color: #768094;
            //         line-height: 18px;
            //         text-align: left;
            //         font-style: normal;
            //     }
            //     >span,>img{
            //         vertical-align: middle;
            //     }
            // }
          }
        }

        .fotterOuter {
          display: flex;
          justify-content: space-between;
          margin-top: 28px;
          .footerItem {
            width: 33.3%;
            text-align: center;
            cursor: pointer;
            &:not(:last-child) {
              border-right: 1px solid rgba(0, 0, 0, 0.12);
            }
            img,
            span {
              vertical-align: middle;
            }
            > span {
              color: #3666ea;
            }
            > img {
              width: 15px;
              height: 15px;
              margin-right: 3px;
            }
          }
        }
        .date {
          font-weight: 400;
          font-size: 12px;
          color: #828894;
          line-height: 24px;
          text-align: left;
          font-style: normal;
          width: calc(100% - 32px);
          position: absolute;
          left: 16px;
          bottom: 16px;
          display: flex;
          align-items: flex-end;
          justify-content: space-between;
          .period-box {
            display: flex;
            align-items: center;
            .point {
              width: 8px;
              height: 8px;
              position: relative;
              background: rgba(28, 80, 253, 0.1);
              border-radius: 50%;
              margin: 4px;
              &::before {
                content: "";
                width: 4px;
                height: 4px;
                background: #1c50fd;
                border-radius: 50%;
                position: absolute;
                left: 2px;
                top: 2px;
              }
            }
          }
        }
      }
      .listItem:hover {
        box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
      }
    }
  }
  ::v-deep .flexDialog {
    background-color: #f2f5fa;
    padding: 0 33px 33px;
    .el-dialog {
      box-shadow: none !important;
      overflow: visible;
      background: none !important;
    }
    .el-dialog__header {
      display: none;
    }
    .headBar {
      background: #f2f5fa;
      padding: 16px 0 16px 16px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 16px;
      width: 100%;
      position: absolute;
      top: 0;
      right: 0;
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
        .titleIcon {
          p:first-child {
            font-family: MiSans, MiSans;
            font-weight: 500;
            font-size: 20px;
            color: #383d47;
            line-height: 24px;
            text-align: left;
            font-style: normal;
            margin-bottom: 6px;
          }
          p:last-child {
            font-family: MiSans, MiSans;
            font-weight: 400;
            font-size: 14px;
            color: #768094;
            line-height: 18px;
            text-align: left;
            font-style: normal;
          }
        }
      }
      .rightSlide {
        display: flex;
        justify-content: space-between;
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
          background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
          border-radius: 4px;
          img {
            margin-right: 5px;
          }
          img,
          span {
            vertical-align: middle;
          }
        }
      }
    }
    .dialogTitle {
      margin-top: 30px;
      > img {
        width: 22px;
        height: 22px;
        margin-right: 4px;
      }
      > span {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        color: #383d47;
        line-height: 28px;
        text-align: left;
        font-style: normal;
      }
      > img,
      > span {
        vertical-align: middle;
      }
    }
    .el-dialog__body {
      background: #f2f5fa;
      padding: 77px 0 30px;
      position: relative;
      .verticalLine {
        width: 1px;
        background: rgba(0, 0, 0, 0.12);
        height: calc(100% + 32px);
        position: absolute;
        right: 0;
        top: 0;
      }
      .formOuter {
        width: 576px;
        margin-top: 24px;
        padding-right: 30px;
        .el-textarea {
          .el-textarea__inner {
            border-radius: 4px;
            border: 1px solid #e1e4eb;
          }
        }
        .el-input:not(.el-input-group) {
          .el-input__inner {
            border-radius: 4px;
            border: 1px solid #e1e4eb;
          }
        }
        .marginTop32 {
          margin-top: 32px;
          .el-upload-list__item {
            display: flex;
            align-items: center;
          }
          .identityUpload {
            .el-upload-list--picture-card .el-upload-list__item-thumbnail {
              height: auto !important;
            }
            .el-upload--picture-card {
              width: 104px;
              height: 104px;
              border: none;
              background: #f2f4f7;
              > div {
                height: 0;
                background: pink;
                position: relative;
                top: -19px;
              }
            }
            .el-upload-list__item {
              width: 104px;
              height: 104px;
            }
          }
        }
        .formTitle {
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 16px;
          color: #383d47;
          line-height: 33px;
          text-align: left;
          font-style: normal;
          text-transform: none;
          margin-bottom: 3px;
          > img {
            width: 16px;
            height: 16px;
          }
          > img,
          span {
            vertical-align: middle;
          }
        }
        .uploadImg {
          display: flex;
          justify-content: space-between;
          .logoUpload {
            .el-upload-list__item {
              display: flex;
              align-items: center;
            }
            .el-upload-list--picture-card .el-upload-list__item-thumbnail {
              height: auto !important;
            }
            .el-upload--picture-card {
              width: 104px;
              height: 104px;
              border: none;
              background: #f2f4f7;
              > div {
                height: 0;
                background: pink;
                position: relative;
                top: -19px;
              }
            }
            .el-upload-list__item {
              width: 104px;
              height: 104px;
            }
          }
          .el-textarea {
            margin-top: 16px;
          }
        }
        .formFlexOuter {
          display: flex;
          justify-content: flex-start;
          margin-top: 24px;
          .el-input-number__increase {
            border-radius: 0 4px 4px 0;
          }
          .el-input-number__decrease {
            border-radius: 4px 0 0 4px;
          }
          .assModel {
            .el-input__inner {
              height: 56px;
              font-size: 16px;
              color: #000;
              font-weight: 500;
            }
          }
          .robotImg {
            margin-left: 32px;
            position: relative;
            .el-upload-list--picture-card .el-upload-list__item-thumbnail {
              height: auto !important;
            }
            .el-upload-list__item {
              width: 40px !important;
              height: 40px !important;
              border-radius: 50%;
              display: flex;
              align-items: center;
            }
            .el-upload--picture-card {
              width: 40px;
              height: 40px;
              border-radius: 50%;
              position: relative;
              border: none;
              background: #f2f4f7;
              .el-icon-plus {
                font-size: 14px;
                position: absolute;
                top: calc(50% - 7px);
                right: calc(50% - 7px);
              }
            }
          }
          .widthSpan {
            display: inline-block;
            width: 113px;
            color: #768094 !important;
          }
          .el-switch__label > span {
            font-size: 16px !important;
          }
        }
      }
      .collapseStyle {
        border-top: none;
        .el-collapse-item__arrow {
          display: none;
        }
        .el-collapse-item__header {
          position: relative;
        }
        .el-collapse-item {
          .svgTransform {
            transform: rotate(-90deg);
            transition: all 0.2s;
          }
          &.is-active {
            .svgTransform {
              transform: rotate(0deg);
            }
          }
        }
        .el-collapse-item__wrap {
          padding-left: 10px;
        }
        .collapseHeader {
          > div {
            > span {
              color: #383d47;
              font-size: 16px !important;
              font-weight: 500;
            }
          }
          img,
          span {
            vertical-align: middle;
          }
        }
      }
    }
  }
  ::v-deep .configDialog {
    .el-dialog__title {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
    .flexOuter {
      display: flex;
      justify-content: flex-start;
      .el-slider__runway {
        width: 300px;
      }
      > span:first-child {
        display: inline-block;
        width: 350px;
        position: relative;
        top: 11px;
        font-size: 16px;
        color: #383d47;
      }
    }
    .footer {
      margin-top: 30px;
      .el-button--primary {
        border-radius: 4px;
        background: #3666ea;
        border-color: #3666ea;
      }
      .el-button.is-plain {
        border-radius: 4px;
        float: right;
        color: #3666ea;
        background: #fff;
        span,
        img {
          vertical-align: middle;
        }
      }
    }
  }
  ::v-deep .preViewDialog {
    .el-dialog {
      border-radius: 0 0 8px 8px;
      background: #f2f5fa;
    }
    .el-dialog__body {
      padding: 82px 0 30px;
    }
    .el-tabs {
      .el-tabs__item {
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
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 18px;
      color: #181b49;
      text-align: left;
      font-style: normal;
      border-left: 3px solid #3666ea;
      padding-left: 16px;
      margin: 28px 0 16px;
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
              margin-right: 3px;
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
              margin-right: 3px;
            }
            img,
            span {
              vertical-align: middle;
            }
          }
          .el-button--primary {
            border: 1px solid #3666ea;
            background: #3666ea;
            color: #fff;
          }
          .borderBtn {
            border: 1px solid #3666ea;
            color: #3666ea;
            margin-right: 16px;
          }
        }
      }
    }
  }
}

.elDrawer {
  ::v-deep .el-drawer__header {
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
    border-radius: 8px 8px 0px 0px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 20px;
    color: #383d47;
    line-height: 24px;
  }
  ::v-deep .el-drawer__body {
    padding: 0 24px 10px;
  }
}

.userInfo {
  margin: 0 0 20px 0;
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

.content {
  display: flex;
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
    width: 49.5%;
    background: #f2f5fa;
    height: calc(100vh - 256px) !important;
    margin-right: 16px;
    overflow-y: auto;
    padding: 6px 14px;
    ::v-deep .el-collapse-item__header {
      height: 40px;
      line-height: 40px;
      background-color: #f2f5fa;
      font-size: 16px;
      color: #383d47;
    }
    ::v-deep .el-collapse-item__content {
      background-color: #f2f5fa;
      font-size: 14px;
      color: #828894;
    }
  }
  ::v-deep .el-textarea__inner {
    background: #f2f5fa;
    height: calc(100vh - 256px) !important;
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #383d47;
    line-height: 24px;
  }
}

.verifyStatusAnswer {
  ::v-deep .el-textarea__inner {
    background: #fff;
  }
}
.footerDrawer {
  margin-top: 24px;
}

.userList {
  padding: 10px 28px;
  // height: 40px;
  background: #f2f5fa;
  border-radius: 4px;
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 16px;
  color: #828894;
  line-height: 20px;
  margin-right: 8px;
  cursor: pointer;
}

.activeList {
  background: rgba(28, 80, 253, 0.1);
  color: #1c50fd;
}

.formKey {
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 14px;
  color: #828894;
}
.formValue {
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 14px;
  color: #383d47;
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

::v-deep .el-upload-dragger {
  width: 100%;
}
::v-deep .el-upload {
  width: 100%;
}
::v-deep .el-date-editor {
  width: 100%;
}
::v-deep .el-cascader {
  width: 100%;
}
::v-deep .el-tree {
  width: 100%;
  height: 100%;
}



</style>
