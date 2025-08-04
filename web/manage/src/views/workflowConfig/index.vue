<template>
    <div style="height: 100%; overflow: hidden">
        <div class="outer">
            <p class="outerTitle">{{ $t("workflow") }}</p>
            <div class="appOuter" v-loading="workflowLoading">
                <div class="headerContent">
                    <div>
                        <el-input
                            placeholder="输入工作流名称"
                            class="input"
                            v-model="applicationName"
                            @keyup.enter.native="handleTypeChange"
                            @input="handleTypeChange"
                            clearable
                        >
                            <el-button
                                slot="append"
                                icon="el-icon-search"
                                @click="handleTypeChange"
                            ></el-button>
                        </el-input>
                        <el-select
                            v-model="type"
                            @change="handleTypeChange"
                        >
                            <el-option label="全部" value=""></el-option>
                            <el-option
                                label="工作流"
                                :value="2"
                            ></el-option>
                            <el-option label="对话流" :value="3"></el-option>
                        </el-select>
                    </div>
                    <el-button
                        type="primary"
                        v-permission="'createApp'"
                        @click="createApp"
                    >
                        <img src="@/assets/images/add-circle-fill.svg" />
                        <span>{{ $t("createWorkflow") }}</span>
                    </el-button>
                </div>
                <div class="list-box">
                    <div class="listOuter no-scrollbar" v-if="appList.length">
                        <div class="list-content">
                            <div
                                v-for="(item, index) in appList"
                                :key="item.applicationId"
                                class="listItem"
                                @click.stop="editApp(item)"
                            >
                            <div class="preset" v-if="item.ownerType=='official'&&isAdmin">预置</div>
                                <div class="listContent">
                                    <div class="head-img-box">
                                        <img v-if="item.icon" :src="item.icon" class="headImg" />
                                        
                                        <img v-else :src="getRandomHeadImgDefaultBgColor(index, item)" class="defaultHeadImg" />
                                    </div>
                                    <div class="textContent">
                                      <div class="title-content">
                                        <p class="title" :title="item.applicationName">{{ item.componentName }}</p>
                                        <!-- <div
                                          class="sign"
                                          v-if="item.status == 1 || item.status == 2"
                                          :class="`${
                                            item.status == 1 || item.status == 2
                                              ? 'successSign'
                                              : 'failSign'
                                          }`"
                                        > 
                                          <i v-if="item.status == 1 || item.status == 2" class="el-icon-success" style="color: #3FB816"></i>
                                          
                                        </div> -->
                                      </div>
                                      <div class="list-type" v-if="item.type">
                                        <span class="type-item">{{ item.type === 2 ? '工作流' : '对话流' }}</span>
                                      </div>
                                    </div>
                                </div>
                                <div class="desc-content">
                                  <p class="desc" :title="item.componentDesc">{{ item.componentDesc }}</p>
                                </div>
                                <div class="date">
                                  <div class="list-update-time">
                                      <span class="list-user-icon"><iconpark-icon name="user-3-line" size="16"></iconpark-icon></span>
                                      <span class="create-user" v-if="item.username">{{ item.username }}</span>
                                      <span class="point" style="margin-right: 8px"></span>
                                      <span>{{ item.updateTime || item.createTime }}</span>
                                    </div>
                                    <div
                                        style="
                                            display: flex;
                                            align-items: flex-end;
                                            cursor: pointer;
                                        "
                                        v-permission="'preview'"
                                    >
                                    <div class="edit-btn" v-if="isAdminOrUser(item)" v-permission="'editeApp'" command="editeApp" @click.stop="editApp(item)">
                                      <iconpark-icon class="edit-icon" color="#494E57" name="edit-box-line"></iconpark-icon>
                                      <span style="color: #494E57">{{ $t("edit") }}</span>
                                    </div>
                                        <div class="opts-box" @click.stop v-if="isAdminOrUser(item)">
                                            <el-dropdown
                                                class="opts-box-dropdown"
                                                placement="top-end"
                                                trigger="click"
                                                @command="
                                                    (value) =>
                                                        handleCommand(
                                                            value,
                                                            item
                                                        )
                                                "
                                            >
                                                <span class="el-dropdown-link">
                                                    <iconpark-icon
                                                        name="more-line"
                                                        size="18"
                                                        color="#383838"
                                                    ></iconpark-icon>
                                                </span>
                                                <el-dropdown-menu
                                                    slot="dropdown"
                                                    class="opts-box-dropdown-menu"
                                                >
                                                <el-dropdown-item command="presetApp" v-if="item.ownerType!='official'&&isAdmin">
                                                    <iconpark-icon color="#494E57" name="share-box-line"></iconpark-icon>
                                                    <span style="color: #494E57">{{ "设为预置" }}</span>
                                                </el-dropdown-item>
                                                <el-dropdown-item command="presetApp" v-else-if="item.ownerType=='official'&&isAdmin">
                                                    <iconpark-icon color="#F53F3F" name="share-box-line"></iconpark-icon>
                                                    <span style="color: #F53F3F">{{ "取消预置" }}</span>
                                                </el-dropdown-item>
                                                    <el-dropdown-item
                                                        v-permission="'copyApp'"
                                                        command="copyApp"
                                                    >
                                                        <iconpark-icon
                                                            color="#494E57"
                                                            name="file-copy-line"
                                                        ></iconpark-icon>
                                                        <span
                                                            style="
                                                                color: #494e57;
                                                            "
                                                            >{{
                                                                $t("copy")
                                                            }}</span
                                                        >
                                                    </el-dropdown-item>
                                                    <el-dropdown-item
                                                        v-permission="
                                                            'deleteApp'
                                                        "
                                                        command="deleteApp"
                                                    >
                                                        <iconpark-icon
                                                            color="#494E57"
                                                            name="delete-bin-4-line"
                                                        ></iconpark-icon>
                                                        <span
                                                            style="
                                                                color: #494e57;
                                                            "
                                                            >{{
                                                                $t("delete")
                                                            }}</span
                                                        >
                                                    </el-dropdown-item>
                                                    <el-dropdown-item
                                                        v-permission="
                                                            'grantApp'
                                                        "
                                                        command="grantApp"
                                                    >
                                                        <iconpark-icon
                                                            color="#494E57"
                                                            name="user-add-line"
                                                        ></iconpark-icon>
                                                        <span
                                                            style="
                                                                color: #494e57;
                                                            "
                                                            >{{
                                                                $t(
                                                                    "authorization"
                                                                )
                                                            }}</span
                                                        >
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
                            {{ $t("total") }}{{ pageObj.total
                            }}{{ $t("items") }}
                        </div>
                        <el-pagination
                            @size-change="handleAppListSizeChange"
                            @current-change="handleAppListCurrentChange"
                            :current-page.sync="pageObj.pageNo"
                            :page-sizes="[20, 30, 40, 50]"
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
                :close-on-press-escape="false"
                destroy-on-close
                :close-on-click-modal="false"
            >
                <!-- 能力设置放入新页面 -->
                <abilitySetting
                    v-if="dialogVisible"
                    ref="abilitySetting"
                    :componentId="componentId"
                    @configCloseDialog="configCloseDialog"
                    @openEditDialog="openEditDialog"
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
                        <el-button class="btn" @click="editApp(commentData)">
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
                        <el-tab-pane :label="$t('overview')" name="first">
                            <div class="tabContentTitle">
                                {{ $t("appInfo") }}
                            </div>
                            <div class="tabContents">
                                <div class="contentInner">
                                    <div class="title">
                                        <span>{{ $t("publicPublish") }}</span>
                                        <el-switch
                                            disabled
                                            v-model="previewItem.status"
                                            :active-value="'1'"
                                        ></el-switch>
                                    </div>
                                    <p class="desc">
                                        {{ $t("publicAccessLink") }}
                                    </p>
                                    <div class="secondLine">
                                        <p class="inp">
                                            <span>{{
                                                previewItem.clientLink
                                            }}</span>
                                            <!-- <img src="@/assets/images/shuaxin.svg"> -->
                                        </p>
                                        <el-button
                                            @click="
                                                cpoyText(previewItem.clientLink)
                                            "
                                        >
                                            <img
                                                src="@/assets/images/copy-line.svg"
                                            />
                                            <span>{{ $t("copyLink") }}</span>
                                        </el-button>
                                    </div>
                                    <div class="thirdLine">
                                        <el-button
                                            type="primary"
                                            @click="openPreview"
                                        >
                                            <img
                                                src="@/assets/images/computer.svg"
                                            />
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
                                            v-model="previewItem.status"
                                            :active-value="'2'"
                                            disabled
                                        ></el-switch>
                                    </div>
                                    <p class="desc">
                                        {{ $t("apiAccessCredentials") }}
                                    </p>
                                    <div class="secondLine">
                                        <p class="inp">
                                            {{ previewItem.apiKey }}
                                        </p>
                                        <el-button @click="cpoyText()">
                                            <img
                                                src="@/assets/images/copy-line.svg"
                                            />
                                            <span>{{ $t("copy") }}</span>
                                        </el-button>
                                    </div>
                                    <div class="thirdLine">
                                        <el-button
                                            class="borderBtn"
                                            @click="devBuilding"
                                        >
                                            <img
                                                src="@/assets/images/pie-chart.svg"
                                            />
                                            <span>{{ $t("usage") }}</span>
                                        </el-button>
                                        <el-popover
                                            placement="right"
                                            trigger="hover"
                                        >
                                            <p>{{ previewItem.apiSecret }}</p>
                                            <el-button slot="reference">{{
                                                $t("viewKey")
                                            }}</el-button>
                                        </el-popover>
                                    </div>
                                </div>
                            </div>
                        </el-tab-pane>
                        <el-tab-pane :label="$t('analysis')" name="sixth">
                            <!-- 分析 -->
                            <Analysis
                                v-if="activeName == 'sixth'"
                                ref="Analysis"
                                :data="previewItem"
                            />
                        </el-tab-pane>
                        <!-- <el-tab-pane
                        :label="$t('settings')"
                        name="second"
                    ></el-tab-pane>
                    <el-tab-pane
                        :label="$t('hitTest')"
                        name="third"
                    ></el-tab-pane> -->
                        <el-tab-pane :label="$t('dialogLog')" name="fourth">
                            <div style="margin-bottom: 20px">
                                <span style="margin-right: 20px">{{
                                    $t("questionTime")
                                }}</span>
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
                                    v-if="activeName == 'fourth'"
                                >
                                    <el-option
                                        v-for="item in verifyStatusColumns"
                                        :key="item.value"
                                        :label="item.text"
                                        :value="item.value"
                                    ></el-option>
                                </el-select>
                                <el-input
                                    :placeholder="
                                        $t('enterKeywordForQuestionOrAnswer')
                                    "
                                    style="width: 334px; margin-right: 16px"
                                    v-model="text"
                                    clearable
                                >
                                </el-input>
                                <el-button
                                    type="primary"
                                    @click="searchDialog"
                                    >{{ $t("search") }}</el-button
                                >
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
                                                style="
                                                    color: #6295fc;
                                                    cursor: pointer;
                                                "
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
                                            <span
                                                v-if="
                                                    scope.row.verifyStatus == 0
                                                "
                                            >
                                                {{ $t("pendingVerification") }}
                                            </span>
                                            <span
                                                v-if="
                                                    scope.row.verifyStatus == 5
                                                "
                                            >
                                                {{
                                                    $t("pendingReverification")
                                                }}
                                            </span>
                                            <span
                                                v-if="
                                                    scope.row.verifyStatus == 6
                                                "
                                            >
                                                {{
                                                    $t("pendingReverification")
                                                }}
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
                                                    border: 3px solid
                                                        rgba(28, 80, 253, 0.1);
                                                    border-radius: 4px;
                                                    margin-right: 8px;
                                                "
                                            ></div>
                                            <span
                                                v-if="
                                                    scope.row.verifyStatus == 1
                                                "
                                            >
                                                {{ $t("verifiedModified") }}
                                            </span>
                                            <span
                                                v-if="
                                                    scope.row.verifyStatus == 2
                                                "
                                            >
                                                {{ $t("verifiedCorrect") }}
                                            </span>
                                        </div>
                                        <div
                                            v-if="scope.row.verifyStatus == 3"
                                            class="flex aligns"
                                        >
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
                                        <div
                                            v-if="scope.row.verifyStatus == 4"
                                            class="flex aligns"
                                        >
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
                                    fixed="right"
                                    :label="$t('action')"
                                    width="100"
                                >
                                    <template slot-scope="scope">
                                        <el-button
                                            @click="
                                                viewClick(
                                                    scope.row,
                                                    scope.$index
                                                )
                                            "
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
                        <el-tab-pane :label="$t('answerReview')" name="fifth">
                            <div style="margin-bottom: 20px">
                                <span style="margin-right: 20px">{{
                                    $t("questionTime")
                                }}</span>
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

                                <span
                                    style="margin-right: 20px"
                                    v-if="activeName == 'fifth'"
                                    >{{ $t("reviewStatus") }}</span
                                >
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
                                    :placeholder="
                                        $t('enterKeywordForQuestionOrAnswer')
                                    "
                                    style="width: 334px; margin-right: 16px"
                                    v-model="text"
                                    clearable
                                >
                                </el-input>
                                <el-button
                                    type="primary"
                                    @click="searchDialog"
                                    >{{ $t("search") }}</el-button
                                >
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
                                                style="
                                                    color: #6295fc;
                                                    cursor: pointer;
                                                "
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
                                            v-if="scope.row.verifyStatus == 2"
                                            class="flex aligns"
                                        >
                                            {{ $t("correctAnswer") }}
                                        </div>
                                        <div
                                            v-if="scope.row.verifyStatus == 1"
                                            class="flex aligns"
                                        >
                                            {{ $t("incorrectAnswer") }}
                                        </div>
                                        <div
                                            v-if="scope.row.verifyStatus == 3"
                                            class="flex aligns"
                                        >
                                            {{ $t("maliciousQuestion") }}
                                        </div>
                                        <div
                                            v-if="scope.row.verifyStatus == 4"
                                            class="flex aligns"
                                        >
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
                                        <div
                                            v-if="scope.row.auditStatus == 0"
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
                                            <span>
                                                {{ $t("questionVerification") }}
                                            </span>
                                        </div>
                                        <div
                                            v-if="scope.row.auditStatus == 1"
                                            class="flex aligns"
                                        >
                                            <div
                                                style="
                                                    width: 8px;
                                                    height: 8px;
                                                    background: #4bbe25;
                                                    border: 3px solid
                                                        rgba(28, 80, 253, 0.1);
                                                    border-radius: 4px;
                                                    margin-right: 8px;
                                                "
                                            ></div>
                                            <span>
                                                {{ $t("reviewPassed") }}
                                            </span>
                                        </div>
                                        <div
                                            v-if="scope.row.auditStatus == 2"
                                            class="flex aligns"
                                        >
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
                                        <div
                                            v-if="scope.row.auditStatus == 3"
                                            class="flex aligns"
                                        >
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
                                            @click="
                                                reviewAnswers(
                                                    scope.row,
                                                    scope.$index
                                                )
                                            "
                                            type="text"
                                            style="color: #6295fc"
                                            >{{ $t("review") }}</el-button
                                        >
                                        <el-button
                                            v-else
                                            @click="
                                                reviewAnswers(
                                                    scope.row,
                                                    scope.$index
                                                )
                                            "
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
                        <!-- <el-tab-pane :label="$t('configurationManagement')" name="seventh">
            <ConfigManage
              v-if="activeName == 'seventh'"
              ref="ConfigManage"
              :data="previewItem"
            />
          </el-tab-pane> -->
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
                                <p
                                    style="
                                        font-size: 16px;
                                        color: #646479;
                                        margin-bottom: 16px;
                                        cursor: pointer;
                                    "
                                >
                                    {{ item.text }}
                                </p>
                            </el-collapse-item>
                        </el-collapse>
                    </div>
                </div>
                <p style="text-align: center" v-else>{{ $t("noData") }}</p>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="cancelSource">{{
                        $t("cancel")
                    }}</el-button>
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
                                :class="[
                                    'userList',
                                    activeIndex == 0 ? 'activeList' : '',
                                ]"
                                @click="clickUser(0)"
                            >
                                {{ $t("originalAnswer") }}
                            </div>
                            <div
                                :class="[
                                    'userList',
                                    activeIndex == 1 ? 'activeList' : '',
                                ]"
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
                                    <el-radio-group
                                        v-model="formList.verifyStatus"
                                    >
                                        <el-radio :label="2">{{
                                            $t("correct")
                                        }}</el-radio>
                                        <el-radio :label="1">{{
                                            $t("incorrect")
                                        }}</el-radio>
                                        <el-radio :label="3">{{
                                            $t("maliciousQuestion")
                                        }}</el-radio>
                                        <el-radio :label="4">{{
                                            $t("noAction")
                                        }}</el-radio>
                                    </el-radio-group>
                                </div>
                                <div class="flex-center" v-else>
                                    <img
                                        v-if="setForm.verifyStatus != 3"
                                        :src="
                                            require('@/assets/images/checkbox-circle-fill.svg')
                                        "
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
                                                <span class="formKey">
                                                    {{ $t("verifier") }}
                                                </span>
                                                <span class="formValue">
                                                    {{ setForm.verifyUserName }}
                                                </span>
                                            </div>
                                            <div class="flex just">
                                                <span class="formKey">
                                                    {{ $t("department") }}
                                                </span>
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
                                            :src="
                                                require('@/assets/images/information-line.svg')
                                            "
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
                                <div
                                    v-for="(item, index) in sourceTableList"
                                    :key="item"
                                >
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
                                                              : $t(
                                                                    'noKnowledgeBase'
                                                                )) +
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
                                                        font-family: MiSans,
                                                            MiSans;
                                                        font-weight: 500;
                                                        font-size: 16px;
                                                        color: #383d47;
                                                    "
                                                >
                                                    {{
                                                        item.route
                                                            ? (item.knowledgeName
                                                                  ? item.knowledgeName
                                                                  : $t(
                                                                        "noKnowledgeBase"
                                                                    )) +
                                                              "-" +
                                                              item.route.join(
                                                                  "|"
                                                              )
                                                            : ""
                                                    }}
                                                </div>
                                            </el-tooltip>
                                            <p
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
                                                {{ item.text }}
                                            </p>
                                        </el-collapse-item>
                                    </el-collapse>
                                </div>
                            </div>
                            <p style="text-align: center" v-else>
                                {{ $t("noData") }}
                            </p>
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
                        <el-button @click="handleClose">{{
                            $t("close")
                        }}</el-button>
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
                width="750px"
                class="grantDialog"
                destroy-on-close
                :close-on-click-modal="false"
        :close-on-press-escape="false"
            >
                <GrantData
                    :data-id="grantData.componentId"
                    v-if="grantVisible"
                    data-type="workflow"
                    @cancelGrant="cancelGrant"
                    :queryCurrentTenantUserFlag="true"
                ></GrantData>
            </el-dialog>
            <!-- 创建问答助手 名称弹窗 -->
            <createApplication
                v-if="dialogVisibleApplication"
                :dialogVisibleApplication="dialogVisibleApplication"
                :type="createType"
                :params="editItem"
                @cancelApplication="cancelApplication"
                @confirmApplication="confirmApplication"
                @confirmEditApplication="confirmEditApplication"
            ></createApplication>
            <!-- 删除应用 -->
            <delApplication
                v-if="deleteDialogVisible"
                deleteName="工作流"
                :deleteDialogVisible="deleteDialogVisible"
                @configCancelDelete="configCancelDelete"
                :params="deleteItem"
            ></delApplication>
            <!-- 知识库详情 -->
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
                :showBtn="false"
                :isAppManage="true"
                @clickBack="clickBack"
              />
            </el-drawer>
        </div>
    </div>
</template>
<script>
import {
    appList,
    dialogRecord,
    sourceList,
    copyWorkflowApp,
    defaultApp,
    verifyRecord,
    deptTree,
    getCheckRecord,
    recordCheckRecord,
} from "@/api/workflow";
import KbmCreate from "@/views/Kbm/component/KbmCreate.vue";
import drawerAnswer from "@/views/appManage/components/drawerAnswer";
import abilitySetting from "@/views/workflowConfig/components/abilitySetting.vue";
import publishWay from "@/views/appManage/components/publishWay";
import axios from "axios";
import GrantData from "@/views/appManage/components/GrantData.vue";
import createApplication from "@/views/workflowConfig/components/createApplication.vue";
import Analysis from "@/views/appManage/components/analysis.vue";
import ConfigManage from "@/views/appManage/components/configManage.vue";
import delApplication from "./components/deleteApplication.vue";
import { saveWorkflowComponent, updateWorkflowComponent } from "@/api/workflow";
import {workflowPreset} from "@/api/app"
export default {
    components: {
        KbmCreate,
        drawerAnswer,
        GrantData,
        abilitySetting,
        createApplication,
        publishWay,
        delApplication,
        Analysis,
        ConfigManage,
    },
    computed: {
      //用于下面的resize 改变图表尺寸，在容器大小发生改变时需要手动调用
      KonwledgeList() {
        return this.$store.state.tab.KonwledgeList;
      },
      isAdmin(){
        return JSON.parse(sessionStorage.getItem("user")).powerType==0
      },
      isAdminOrUser(){
        return (data)=>{
            let obj=JSON.parse(sessionStorage.getItem("user"))
            return this.isAdmin || obj.accountName == data.createUser
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
    data() {
        return {
          dialogVisibleKnowLedge: false,
          rowData: {},
          typelist: "",
            pageObj: {
                pageNo: 1,
                pageSize: 20,
                total: 0,
            },
            createType: "add",
            defaultWorkflowImage: require("@/assets/images/default-icon-workflow.svg"), // 默认图像的URL
            defaultDialogueImage: require("@/assets/images/default-icon-dialogue.svg"), // 默认图像的URL
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
                            start.setTime(
                                start.getTime() - 3600 * 1000 * 24 * 7
                            );
                            picker.$emit("pick", [start, end]);
                        },
                    },
                    {
                        text: this.$t("lastMonth"),
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(
                                start.getTime() - 3600 * 1000 * 24 * 30
                            );
                            picker.$emit("pick", [start, end]);
                        },
                    },
                    {
                        text: this.$t("lastThreeMonths"),
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(
                                start.getTime() - 3600 * 1000 * 24 * 90
                            );
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
            commentData: {},
            previewItem: "",
            previewDialogVisible: false,
            workflowLoading: false,
            deleteItem: "",
            applicationName: "",
            type: "",
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
            componentId: "",
        };
    },
    created() {
        console.log("this.$route-------", this.$route);
        this.getAppList();
        this.getDeptTree();
        // 复制工作流自动打开编辑功能
        if (this.$route?.query?.workflow_id) {
            this.componentId = this.$route?.query?.workflow_id;
            this.dialogVisible = true;
        }
    },
    methods: {
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
        handleTypeChange() {
            this.pageObj.pageNo = 1;
            this.getAppList();
        },
        sendMessage() {
            const iframe = this.$refs.iframe;
            if (iframe) {
                const iframeWindow = iframe.contentWindow;
                // 现在可以安全地使用iframeWindow对象了
                iframeWindow.postMessage(
                    { param1: "测试传入子组件的参数" },
                    "*"
                );
            }
        },
        getWorkflowPresetApi(data){
            workflowPreset({id:data.id}).then(res=>{
                if(res.code=="000000"){
                    this.getAppList()
                }else{
                    this.$message({
                        type:"error",
                        message:res.msg
                    })
                }
            })
        },
        handleCommand(value, item) {
            if (value == "copyApp") this.copyAppInfo(item);
            if (value == "editeApp") this.editApp(item);
            if (value == "deleteApp") this.openDelete(item);
            if (value == "grantApp") this.grantApp(item);
            if (value == "presetApp") this.getWorkflowPresetApi(item)
        },
        copyAppInfo(item) {
            this.$confirm(this.$t("continueCopyWorkflow"), this.$t("tips"), {
                confirmButtonText: this.$t("confirm"),
                cancelButtonText: this.$t("cancel"),
                type: "warning",
            })
                .then(() => {
                    copyWorkflowApp({ componentId: item.componentId }).then(
                        (res) => {
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
                        }
                    );
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
                    this.sourceTableList =
                        res.data.sourceAnswerResultList || [];
                } else {
                    this.sourceTableList = [];
                }
            });
        },
        searchDialog() {
            this.currentPage = 1;
            if (this.activeName == "fourth") {
                this.getDialogRecord();
            } else if (this.activeName == "fifth") {
                this.getDialogRecordAnswer();
            }
        },
        getDialogRecord() {
            let deptId = "";
            if (this.verifyDeptId.length > 0) {
                deptId = this.verifyDeptId[this.verifyDeptId.length - 1];
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
                verifyStatus: this.verifyStatus,
                text: this.text,
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
            if (this.activeName == "fourth") {
                this.getDialogRecord();
            } else if (this.activeName == "fifth") {
                this.getDialogRecordAnswer();
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.activeName == "fourth") {
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
            this.verifyDeptId = "";
            this.deptId = "";
            this.auditStatus = "";
            if (tab.name == "fourth") {
                this.getDialogRecord();
            } else if (tab.name == "fifth") {
                this.getDialogRecordAnswer();
            }
        },
        previewApp(item) {
            this.activeName = "first";
            this.commentData = item;
            this.previewItem = item.applicationInfo;
            this.previewDialogVisible = true;
        },
        editApp(item) {
            let obj = {
                2:"workflow",
                3:"dialogue"
            }
            this.$store.commit('setWorkFlowType', obj[item.type])
            this.editItem = item;
            this.componentId = item?.componentId;
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
                case 1:
                    return this.$t("publicPublish");
                case 2:
                    return this.$t("privatePublish");
                case 3:
                    return this.$t("unPublish");
                case 4:
                    return this.$t("temporaryStorage");
                case 5:
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
            this.componentId = "";
            this.defaultAppInfo();
            this.dialogVisibleApplication = true;
        },
        editComponent(editItem) {
            this.createType = "edit";
            this.editItem = editItem;
            this.dialogVisibleApplication = true;
        },
        configCloseDialog() {
            this.dialogVisible = false;
            this.getAppList();
        },
        openEditDialog() {
            this.editComponent(this.editItem);
        },

        getAppList() {
            this.workflowLoading = true;
            appList({
                pageNo: this.pageObj.pageNo,
                pageSize: this.pageObj.pageSize,
                order: "create_time",
                sort: "desc",
                applicationName: this.applicationName,
                type: this.type ? this.type : null,
                ownerType:"personalGrantTenant"
            })
                .then((res) => {
                    this.workflowLoading = false;
                    if (res.data?.records?.length) {
                        this.appList = res.data.records;
                        this.pageObj.total = res.data?.totalRow || 0;
                    } else {
                        this.appList = [];
                    }
                })
                .catch((error) => {
                    this.workflowLoading = false;
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
                    const url = window.URL.createObjectURL(
                        new Blob([res.data])
                    );
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
            this.verifyDeptId = "";
            this.deptId = "";
            this.auditStatus = "";
            if (this.activeName == "fourth") {
                this.getDialogRecord();
            } else if (this.activeName == "fifth") {
                this.getDialogRecordAnswer();
            }
        },
        viewClick(row, index) {
            this.setForm = row;
            this.setIndex = index;
            this.$set(
                this.formList,
                "verifyAnswer",
                row.verifyAnswer || row.answer
            );
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
                            return this.$message.warning(
                                this.$t("isLastDataOnPage")
                            );
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
                            return this.$message.warning(
                                this.$t("isLastDataOnPage")
                            );
                        }
                        this.setForm =
                            this.dialogTableDataAnswer[this.setIndex];
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
                    this.componentId = this.editItem?.componentId;
                }
            });
        },
        confirmEditApplication(val) {
            this.dialogVisibleApplication = false;
            updateWorkflowComponent(val).then((res) => {
                if (res.code == "000000") {
                    this.$message({
                        type: "success",
                        message: this.$t("success"),
                    });
                    this.editItem.componentName = val.componentName;
                    this.editItem.componentDesc = val.componentDesc;
                    this.editItem.icon = val.icon;
                    this.$refs.abilitySetting.upadateAbility(val);
                } else {
                    this.$message({
                        type: "error",
                        message: res.msg,
                    });
                }
            });
        },
        confirmApplication(val) {
            this.dialogVisibleApplication = false;
            let params = {
                componentDesc: val.componentDesc,
                componentName: val.componentName,
                type: val.type,
                icon: val.icon,
            };
            saveWorkflowComponent(params).then((res) => {
                if (res.code == "000000") {
                    this.getAppList();
                    this.editApp(res.data.component)
                } else {
                    this.$message({
                        type: "error",
                        message: res.msg,
                    });
                }
            });
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
        getRandomHeadImgDefaultBgColor(index, data) {
            const colors = ['#2BCBCB', '#2AA3CB', '#2B7BCB', '#5B90F9', '#5B69F9', '#9C5BF9', '#C45BF9'];
            const workflowImgList = [
                require('@/assets/images/default-workflow-1.svg'),
                require('@/assets/images/default-workflow-2.svg'),
                require('@/assets/images/default-workflow-3.svg'),
                require('@/assets/images/default-workflow-4.svg'),
                require('@/assets/images/default-workflow-5.svg'),
                require('@/assets/images/default-workflow-6.svg'),
                require('@/assets/images/default-workflow-7.svg'),
            ];
            const dialogueImgList = [
                require('@/assets/images/default-dialogue-1.svg'),
                require('@/assets/images/default-dialogue-2.svg'),
                require('@/assets/images/default-dialogue-3.svg'),
                require('@/assets/images/default-dialogue-4.svg'),
                require('@/assets/images/default-dialogue-5.svg'),
                require('@/assets/images/default-dialogue-6.svg'),
                require('@/assets/images/default-dialogue-7.svg'),
            ];
            const imgList = data.type == 2 ? workflowImgList : dialogueImgList;
            
            if (index !== undefined && typeof index === 'number' && index >= 0) {
                return imgList[index % imgList.length];
            }
            const randomIndex = Math.floor(Math.random() * imgList.length);
            return imgList[randomIndex];
        },
    },
};
</script>
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>
<style>
.el-tooltip__popper.is-dark {
    max-width: 580px !important;
}
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
</style>
<style lang="scss" src="@/views/appManage/commonList.scss" scoped></style>

