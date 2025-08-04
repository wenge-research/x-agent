<template>
  <div class="outer">
    <div class="outer-top">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane
          :label="$t('governmentUser')"
          name="first"
          v-if="permissions('GovernmentUsers')"
        ></el-tab-pane>
        <el-tab-pane
          :label="$t('residentUser')"
          name="second"
          v-if="permissions('ResidentialUsers')"
        ></el-tab-pane>
        <el-tab-pane
          :label="$t('userAudit')"
          name="third"
          v-if="permissions('UserReview')"
        ></el-tab-pane>
      </el-tabs>
      <div class="outer-low">
        <div class="leftNav" v-if="activeName != 'second'">
          <p
            style="
              margin-bottom: 24px;
              font-weight: 500px;
              font-size: 18px;
              color: #494c4f;
            "
          >
            {{ $t("departmentList") }}
          </p>
          <el-tree
            :data="treeData"
            :props="defaultProps"
            @node-click="handleNodeClick"
            :expand-on-click-node="false"
            style="overflow-y: auto; height: calc(100% - 90px)"
          >
            <span slot-scope="{ node, data }">
              <img
                src="@/assets/images/openFile.svg"
                style="width: 15px"
                v-if="node.expanded"
              />
              <img
                src="@/assets/images/closeFile.svg"
                style="width: 15px"
                v-else
              />
              <span
                style="
                  color: #383d47;
                  font-size: 16px;
                  margin-left: 8px;
                  font-weight: 400;
                "
                >{{ node.label }}</span
              >
            </span>
          </el-tree>
          <span
            style="
              width: 232px;
              height: 40px;
              border: 1px solid #1c50fd;
              position: absolute;
              bottom: 16px;
              border-radius: 4px;
              line-height: 40px;
              text-align: center;
              cursor: pointer;
            "
          >
            <img
              src="@/assets/images/organization-chart.svg"
              style="
                width: 14px;
                height: 14px;
                margin-right: 6px;
                vertical-align: middle;
              "
            />
            <span
              @click="deptDialogVisible = true"
              style="font-size: 16px; color: #1c50fd"
              >{{ $t("departmentManagement") }}</span
            >
          </span>
        </div>
        <div
          :class="['rightNav', activeName == 'second' ? 'rightNavSecond' : '']"
        >
          <div
            style="display: flex; flex-wrap: wrap; margin-bottom: 20px"
            v-if="activeName != 'third'"
          >
            <div
              style="
                display: flex;
                justify-content: flex-start;
                flex-wrap: wrap;
              "
            >
              <div
                style="margin: 0px 24px 12px 0px"
                v-if="activeName != 'second'"
              >
                <span style="margin-right: 20px">{{ $t("position") }}</span>
                <el-select
                  v-model="workPosition"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                >
                  <el-option
                    v-for="(item, index) in positionList"
                    :key="index"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </div>
              <div
                style="margin: 0px 24px 12px 0px"
                v-if="activeName != 'second'"
              >
                <span style="margin-right: 20px">{{ $t("status") }}</span>
                <el-select
                  v-model="status"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                >
                  <el-option :label="$t('available')" value="1"></el-option>
                  <el-option :label="$t('locked')" value="2"></el-option>
                  <el-option :label="$t('canceled')" value="4"></el-option>
                </el-select>
              </div>
              <div
                v-if="activeName != 'second'"
                style="margin: 0px 24px 12px 0px"
              >
                <span style="margin-right: 20px">{{ $t("userType") }}</span>
                <el-select
                  v-model="userType"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                >
                  <el-option :label="$t('gov')" value="gov"></el-option>
                  <el-option
                    :label="$t('staffMember')"
                    value="gov-street"
                  ></el-option>
                  <el-option
                    :label="$t('communityMember')"
                    value="gov-community"
                  ></el-option>
                </el-select>
              </div>
              <div
                style="margin: 0px 0 12px 0px"
                v-if="activeName != 'second'"
              >
                <span style="margin-right: 20px">{{ $t("creationTime") }}</span>
                <el-date-picker
                  v-model="dateRange"
                  clearable
                  type="daterange"
                  :range-separator="$t('to')"
                  :start-placeholder="$t('startDate')"
                  value-format="yyyy-MM-dd"
                  :end-placeholder="$t('endDate')"
                  style="width: 250px"
                ></el-date-picker>
              </div>
            </div>
            <div
              style="
                display: flex;
                justify-content: flex-start;
                flex-wrap: wrap;
              "
            >
              <div style="margin-right: 8px">
                <el-input
                  v-model="condition"
                  clearable
                  :placeholder="$t('enterUserNameOrPhoneNumber')"
                  prefix-icon="el-icon-search"
                  style="width: 274px"
                ></el-input>
              </div>
              <el-button type="primary" style="height: 40px" @click="search">{{
                $t("search")
              }}</el-button>
              <el-button @click="resetSearch" style="height: 40px">{{
                $t("reset")
              }}</el-button>
              <el-button
                type="primary"
                style="height: 40px;"
                @click="addUserDialog"
                v-if="activeName != 'second'"
                ><i class="el-icon-circle-plus" style="margin-right:8px;"></i>{{ $t("addUser") }}</el-button
              >
            </div>
          </div>
          <div
            style="
              display: flex;
              justify-content: space-between;
              margin-bottom: 20px;
              flex-wrap: wrap;
            "
            v-if="activeName == 'third'"
          >
            <div
              style="
                display: flex;
                justify-content: flex-start;
                flex-wrap: wrap;
              "
            >
              <div style="margin: 0 24px 12px 0">
                <span style="margin-right: 20px">{{ $t("position") }}</span>
                <el-select
                  v-model="formList.workPosition"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                >
                  <el-option
                    v-for="item in workPositionColumns"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </div>
              <!-- <div style="margin: 0 24px 12px 0">
              <span style="margin-right: 20px">手机号</span>
              <el-input
                v-model="formList.phone"
                clearable
                placeholder="请输入手机号"
                style="width: 150px"
              ></el-input>
            </div>
            <div style="margin: 0 24px 12px 0">
              <span style="margin-right: 20px">座机号</span>
              <el-input
                v-model="formList.phone"
                clearable
                placeholder="请输入座机号"
                style="width: 150px"
              ></el-input>
            </div> -->
              <div style="margin: 0 24px 12px 0">
                <span style="margin-right: 20px">{{ $t("status") }}</span>
                <el-select
                  v-model="formList.workStatus"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                >
                  <el-option
                    v-for="item in workStatusColumns"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </div>
              <div style="margin-right: 24px">
                <span style="margin-right: 20px">{{ $t("userType") }}</span>
                <el-select
                  v-model="userType"
                  :placeholder="$t('selectPlaceholder')"
                  clearable
                >
                  <el-option
                    :label="$t('staffMember')"
                    value="staff-street"
                  ></el-option>
                  <el-option
                    :label="$t('communityMember')"
                    value="staff-community"
                  ></el-option>
                  <el-option :label="$t('other')" value="other"></el-option>
                </el-select>
              </div>
              <div style="margin: 0 24px 12px 0" v-if="activeName != 'second'">
                <span style="margin-right: 20px">{{
                  $t("applicationTime")
                }}</span>
                <el-date-picker
                  v-model="formList.dateRange"
                  clearable
                  style="width: 272px"
                  type="daterange"
                  :range-separator="$t('to')"
                  :start-placeholder="$t('startDate')"
                  value-format="yyyy-MM-dd"
                  :end-placeholder="$t('endDate')"
                ></el-date-picker>
              </div>
            </div>
            <div
              style="
                display: flex;
                justify-content: flex-start;
                flex-wrap: wrap;
              "
            >
              <div style="margin: 0 24px 12px 0">
                <el-input
                  v-model="formList.condition"
                  clearable
                  :placeholder="$t('enterUserNameOrPhoneNumber')"
                  prefix-icon="el-icon-search"
                  style="width: 270px"
                ></el-input>
              </div>
              <el-button
                type="primary"
                style="margin-right: 8px; height: 40px"
                @click="search"
                >{{ $t("search") }}</el-button
              >
              <el-button @click="resetSearch" style="height: 40px">{{
                $t("reset")
              }}</el-button>
            </div>
          </div>
          <div v-if="activeName == 'first'">
            <!-- <el-checkbox style="margin-right:36px;">选择全部</el-checkbox> -->
            <span style="margin-right: 21px">
              <span
                style="
                  color: #383d47;
                  font-size: 16px;
                  font-weight: 500;
                  margin-right: 5px;
                "
                >{{ $t("selected") }}</span
              >
              <span style="color: #1c50fd; font-weight: 500; font-size: 16px">{{
                tablCheckData.length
              }}</span>
            </span>
            <span
              style="margin-right: 21px; cursor: pointer"
              @click="roleConfig()"
            >
              <img
                src="@/assets/images/user-settings-line.svg"
                style="width: 15px; vertical-align: middle; margin-right: 4px"
              />
              <span
                style="color: #3666ea; font-size: 16px; vertical-align: middle"
                >{{ $t("batchRoleConfiguration") }}</span
              >
            </span>
            <span
              style="margin-right: 21px; cursor: pointer"
              @click="deregister"
            >
              <img
                src="@/assets/images/forbid-line.svg"
                style="width: 15px; vertical-align: middle; margin-right: 4px"
              />
              <span
                style="color: #3666ea; font-size: 16px; vertical-align: middle"
                >{{ $t("cancelSelected") }}</span
              >
            </span>
            <span style="cursor: pointer" @click="deleteData">
              <img
                src="@/assets/images/delete-bin-4-line.svg"
                style="width: 15px; vertical-align: middle; margin-right: 4px"
              />
              <span
                style="color: #3666ea; font-size: 16px; vertical-align: middle"
                >{{ $t("deleteSelected") }}</span
              >
            </span>
          </div>
          <div style="margin-top: 16px" v-if="activeName != 'third'">
            <el-table
              ref="multipleTable"
              :data="tableData"
              tooltip-effect="dark"
              style="width: 100%"
              @selection-change="handleSelectionChange"
              :max-height="activeName == 'first' ? 530 : 560"
              class="table"
              :key="timer"
              v-loading="isLoading"
            >
              <el-table-column type="selection" width="55"></el-table-column>
              <el-table-column
                type="index"
                width="60"
                :label="$t('sequenceNumber')"
              ></el-table-column>
              <el-table-column
                prop="username"
                :label="$t('userName')"
                width="180"
              ></el-table-column>
              <el-table-column
                prop="deptName"
                :label="$t('department')"
                width="140"
              ></el-table-column>
              <el-table-column
                prop="workPosition"
                :label="$t('position')"
              ></el-table-column>
              <el-table-column
                prop="mainDuty"
                :label="$t('mainWork')"
              ></el-table-column>
              <el-table-column
                prop="workStatus"
                :label="$t('workingCondition')"
              >
                <template slot-scope="scope">
                  <span>{{
                    statusWorkingCondition(scope.row.workStatus)
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="accountName"
                :label="$t('accountName')"
                width="140"
              ></el-table-column>
              <el-table-column
                prop="phone"
                :label="$t('phoneNumber')"
                width="140"
              ></el-table-column>
              <el-table-column
                prop="landline"
                :label="$t('landline')"
                width="140"
              ></el-table-column>
              <el-table-column
                :label="$t('userType')"
                width="100"
                v-if="activeName != 'second'"
              >
                <template slot-scope="scope">
                  <span v-if="scope.row.userType == 'gov'">{{
                    $t("gov")
                  }}</span>
                  <span v-if="scope.row.userType == 'gov-street'">{{
                    $t("staffMember")
                  }}</span>
                  <span v-if="scope.row.userType == 'gov-community'">{{
                    $t("communityMember")
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="createTime"
                :label="$t('creationTime')"
                width="180"
              ></el-table-column>
              <el-table-column
                :label="$t('userStatus')"
                v-if="activeName != 'second'"
              >
                <template slot-scope="scope">
                  <span v-if="scope.row.status == 1">
                    <span
                      style="
                        display: inline-block;
                        margin-right: 8px;
                        width: 8px;
                        height: 8px;
                        border-radius: 50%;
                        background: #1abc7c;
                      "
                    ></span>
                    <span style="font-size: 16px; color: #383d47">{{
                      $t("available")
                    }}</span>
                  </span>
                  <span v-if="scope.row.status == 2">
                    <span
                      style="
                        display: inline-block;
                        margin-right: 8px;
                        width: 8px;
                        height: 8px;
                        border-radius: 50%;
                        background: #dc2544;
                      "
                    ></span>
                    <span style="font-size: 16px; color: #383d47">{{
                      $t("locked")
                    }}</span>
                  </span>
                  <span v-if="scope.row.status == 4">
                    <span
                      style="
                        display: inline-block;
                        margin-right: 8px;
                        width: 8px;
                        height: 8px;
                        border-radius: 50%;
                        background: #b4bccc;
                      "
                    ></span>
                    <span style="color: #b4bccc; font-size: 16px">{{
                      $t("canceled")
                    }}</span>
                  </span>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('action')"
                width="260"
                v-if="activeName == 'first'"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    size="small"
                    @click="roleConfig(scope.row)"
                    v-if="scope.row.status == 1"
                    >{{ $t("roleConfiguration") }}</el-button
                  >
                  <el-button
                    type="text"
                    size="small"
                    v-if="scope.row.status == 1"
                    @click="editUser(scope.row)"
                    >{{ $t("edit") }}</el-button
                  >
                  <el-button
                    type="text"
                    size="small"
                    v-if="scope.row.status == 1"
                    @click="chagePassword(scope.row)"
                    >{{ $t("changePassword") }}</el-button
                  >
                  <el-button
                    type="text"
                    size="small"
                    v-if="scope.row.status == 1"
                    @click="deregister(scope.row)"
                    >{{ $t("clearAll") }}</el-button
                  >

                  <el-button
                    type="text"
                    size="small"
                    v-if="scope.row.status == 2"
                    @click="unlock(scope.row)"
                    >{{ $t("unlock") }}</el-button
                  >
                  <el-button
                    type="text"
                    size="small"
                    v-if="scope.row.status == 4"
                    @click="deleteData(scope.row)"
                    >{{ $t("delete") }}</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div style="margin-top: 16px" v-if="activeName == 'third'">
            <el-table
              ref="multipleTable"
              :data="tableData"
              tooltip-effect="dark"
              style="width: 100%"
              @selection-change="handleSelectionChange"
              max-height="550"
              class="table"
            >
              <el-table-column type="selection" width="55"></el-table-column>
              <el-table-column
                type="index"
                width="60"
                :label="$t('sequenceNumber')"
              ></el-table-column>
              <el-table-column
                prop="username"
                :label="$t('name')"
              ></el-table-column>
              <el-table-column
                prop="workPosition"
                :label="$t('position')"
              ></el-table-column>
              <el-table-column
                prop="deptName"
                :label="$t('department')"
              ></el-table-column>
              <el-table-column
                prop="phone"
                :label="$t('phoneNumber')"
              ></el-table-column>
              <el-table-column
                prop="landline"
                :label="$t('landline')"
              ></el-table-column>
              <el-table-column
                prop="mainDuty"
                :label="$t('mainWork')"
              ></el-table-column>
              <el-table-column :label="$t('userType')" width="100">
                <template slot-scope="scope">
                  <span v-if="scope.row.userType == 'staff-street'">{{
                    $t("staffMember")
                  }}</span>
                  <span v-if="scope.row.userType == 'staff-community'">{{
                    $t("communityMember")
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="createTime"
                :label="$t('submissionTime')"
                width="180"
              ></el-table-column>
              <el-table-column :label="$t('status')">
                <template slot-scope="scope">
                  <span v-if="scope.row.workStatus == 'on'">
                    <span
                      style="
                        display: inline-block;
                        width: 8px;
                        height: 8px;
                        background: #1c50fd;
                        border: 3px solid rgba(28, 80, 253, 0.1);
                        border-radius: 50%;
                        margin-right: 8px;
                      "
                    ></span>
                    <span style="font-size: 16px; color: #383d47">{{
                      $t("employed")
                    }}</span>
                  </span>
                  <span
                    v-if="
                      scope.row.workStatus == 'transferred' ||
                      scope.row.workStatus == 'sick'
                    "
                  >
                    <span
                      style="
                        display: inline-block;
                        margin-right: 8px;
                        width: 8px;
                        height: 8px;
                        border-radius: 50%;
                        background: #ffb24f;
                      "
                    ></span>
                    <span
                      style="font-size: 16px; color: #383d47"
                      v-if="scope.row.workStatus == 'transferred'"
                      >{{ $t("transferred") }}</span
                    >
                    <span
                      style="font-size: 16px; color: #383d47"
                      v-if="scope.row.workStatus == 'sick'"
                      >{{ $t("sickLeave") }}</span
                    >
                  </span>
                  <span
                    v-if="
                      scope.row.workStatus == 'retire' ||
                      scope.row.workStatus == 'off' ||
                      scope.row.workStatus == 'retired'
                    "
                  >
                    <span
                      style="
                        display: inline-block;
                        margin-right: 8px;
                        width: 8px;
                        height: 8px;
                        border-radius: 50%;
                        background: #b4bccc;
                      "
                    ></span>
                    <span
                      style="color: #b4bccc; font-size: 16px"
                      v-if="scope.row.workStatus == 'retire'"
                      >{{ $t("retired") }}</span
                    >
                    <span
                      style="color: #b4bccc; font-size: 16px"
                      v-if="scope.row.workStatus == 'off'"
                      >{{ $t("resigned") }}</span
                    >
                    <span
                      style="color: #b4bccc; font-size: 16px"
                      v-if="scope.row.workStatus == 'retired'"
                      >{{ $t("vacation") }}</span
                    >
                  </span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('action')" width="100">
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    size="small"
                    @click="toExamine(scope.row)"
                    >{{ $t("review") }}</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div style="text-align: right; margin-top: 20px">
            <el-pagination
              background
              layout="total, prev, pager, next, sizes, jumper"
              popper-class="slectStyle"
              :current-page="currentPage"
              :total="total"
              :page-size="pageSize"
              @current-change="handleCurrentChange"
              @size-change="handleSizeChange"
              :page-sizes="[10, 20, 30, 40]"
            ></el-pagination>
          </div>
        </div>
      </div>
    </div>
    <el-dialog
      :title="row ? $t('editUser') : $t('addNewUser')"
      :visible.sync="dialogVisible"
      width="720px"
      class="dialog"
      top="20px"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <el-form
        :model="roleForm"
        :rules="row ? roless : roles"
        ref="roleForm"
        class="roleForm"
        label-position="top"
      >
        <div style="display: flex; justify-content: space-between">
          <div style="width: calc(50% - 10px); margin-right: 20px">
            <el-form-item :label="$t('userType')" prop="powerType">
              <el-radio-group v-model="roleForm.powerType">
                <el-radio label="2">{{ $t("normalUser") }}</el-radio>
                <el-radio label="1">{{ $t("adminUser") }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item :label="$t('userName')" prop="userName">
              <el-input v-model="roleForm.userName"></el-input>
            </el-form-item>
            <el-form-item :label="$t('accountName')" prop="accountName">
              <el-input
                v-model="roleForm.accountName"
                autocomplete="off"
              ></el-input>
            </el-form-item>
            <el-form-item
              :label="$t('loginPassword')"
              prop="password"
              style="position: relative"
            >
              <el-input
                v-model="roleForm.password"
                :type="pwdType"
                autocomplete="new-password"
                :disabled="!!(row && row.id)"
              ></el-input>
              <span
                style="
                  position: absolute;
                  right: 10px;
                  top: 5px;
                  cursor: pointer;
                "
                v-if="!(row && row.id)"
              >
                <img
                  src="@/assets/images/eye-line.svg"
                  style="width: 18px"
                  @click="pwdInputChange"
                  v-if="pwdType == 'password'"
                />
                <img
                  src="@/assets/images/eye-off-line.svg"
                  style="width: 18px"
                  @click="pwdInputChange"
                  v-if="pwdType == 'text'"
                />
              </span>
            </el-form-item>
            <el-form-item :label="$t('phoneNumber')" prop="phone">
              <el-input v-model="roleForm.phone"></el-input>
            </el-form-item>
            <el-form-item
              v-if="roleForm.userType != 'gov'"
              :label="$t('landline')"
              prop="landline"
            >
              <el-input v-model="roleForm.landline"></el-input>
            </el-form-item>
            <el-form-item :label="$t('identificationType')" prop="idType">
              <el-select v-model="roleForm.idType" style="width: 100%">
                <el-option :label="$t('idCard')" value="1"></el-option>
                <el-option :label="$t('passport')" value="2"></el-option>
                <el-option
                  :label="$t('householdRegister')"
                  value="3"
                ></el-option>
                <el-option :label="$t('militaryId')" value="4"></el-option>
                <el-option
                  :label="$t('hongKongMacauTaiwanId')"
                  value="5"
                ></el-option>
                <el-option :label="$t('policeId')" value="6"></el-option>
                <el-option :label="$t('borderPass')" value="7"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('age')" prop="age">
              <el-input
                :placeholder="$t('inputPlaceholder')"
                v-model="roleForm.age"
              >
                <template slot="append">{{ $t("year") }}</template>
              </el-input>
            </el-form-item>
            <el-form-item :label="$t('gender')" prop="sex">
              <el-radio-group v-model="roleForm.sex">
                <el-radio :label="$t('male')"></el-radio>
                <el-radio :label="$t('female')"></el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
          <div style="width: calc(50% - 10px)">
            <el-form-item :label="$t('expirationTime')" prop="expireTime">
              <el-date-picker
                v-model="roleForm.expireTime"
                type="date"
                :placeholder="$t('selectDate')"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
            <el-form-item :label="$t('department')" prop="deptId">
              <el-cascader
                popper-class="cascaderStyle"
                :options="treeData"
                :props="{
                  value: 'deptId',
                  label: 'deptName',
                  checkStrictly: true,
                }"
                clearable
                v-model="roleForm.deptId"
                :show-all-levels="false"
                style="width: 100%"
              ></el-cascader>
            </el-form-item>
            <!-- 人员类型 -->
            <el-form-item :label="$t('personType')" prop="userType">
              <el-select
                v-model="roleForm.userType"
                style="width: 100%"
                @change="userTypeChange"
              >
                <el-option :label="$t('gov')" value="gov"></el-option>
                <el-option
                  :label="$t('staffMember')"
                  value="gov-street"
                ></el-option>
                <el-option
                  :label="$t('communityMember')"
                  value="gov-community"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="roleForm.userType != 'gov'"
              :label="$t('position')"
              prop="workPosition"
            >
              <el-select
                v-model="roleForm.workPosition"
                style="width: 100%"
                multiple
              >
                <el-option
                  v-for="(item, index) in workPositionList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="roleForm.userType != 'gov'"
              :label="$t('mainWork')"
              prop="mainDuty"
            >
              <el-input
                v-model="roleForm.mainDuty"
                autocomplete="off"
              ></el-input>
            </el-form-item>
            <el-form-item
              :label="$t('passwordExpiration')"
              prop="passwordExpiredTime"
            >
              <el-date-picker
                v-model="roleForm.passwordExpiredTime"
                type="date"
                :placeholder="$t('selectDate')"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
            <el-form-item :label="$t('email')" prop="email">
              <el-input v-model="roleForm.email"></el-input>
            </el-form-item>
            <el-form-item :label="$t('identificationNumber')" prop="idNo">
              <el-input v-model="roleForm.idNo"></el-input>
            </el-form-item>
            <!-- 工作状态 -->
            <el-form-item
              v-if="roleForm.userType != 'gov'"
              :label="$t('workingCondition')"
              prop="workStatus"
            >
              <el-select v-model="roleForm.workStatus" style="width: 100%">
                <el-option :label="$t('employed')" value="on"></el-option>
                <el-option :label="$t('resigned')" value="off"></el-option>
                <el-option
                  :label="$t('transferred')"
                  value="transferred"
                ></el-option>
                <el-option :label="$t('vacation')" value="retired"></el-option>
                <el-option :label="$t('sickLeave')" value="sick"></el-option>
                <el-option :label="$t('retired')" value="retire"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
        <el-form-item :label="$t('remarks')" prop="remark">
          <el-input type="textarea" v-model="roleForm.remark"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAddUser">{{
          $t("confirm")
        }}</el-button>
        <el-button @click="cancelSubmit">{{ $t("cancel") }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t('changePassword')"
      :visible.sync="pwdDialogVisible"
      width="540px"
      class="dialog"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <el-form
        :model="pwdForm"
        :rules="pwdRules"
        ref="pwdForm"
        class="pwdForm"
        label-position="top"
      >
        <el-form-item
          :label="$t('loginPassword')"
          prop="password"
          style="position: relative"
        >
          <el-input
            v-model="pwdForm.password"
            :type="pwdType"
            autocomplete="new-password"
            :placeholder="$t('enterNewPassword')"
          >
            <!-- <template slot="append">
                              <span style="position:absolute;left:-28px;top:12px;cursor:pointer;">
                                  <img src="@/assets/images/eye-line.svg" style="width:18px;" @click="pwdInputChange" v-if="pwdType=='password'">
                                  <img src="@/assets/images/eye-off-line.svg" style="width:18px;" @click="pwdInputChange" v-if="pwdType=='text'">
                              </span>
                              <span @click="useDefaultValue" style="cursor:pointer;">
                                  <img src="@/assets/images/refresh-line.svg" style="width:14px;height:14px;vertical-align:middle;margin-right:4px;"/>
                                  <span style="color:#1C50FD;vertical-align:middle;">重置密码</span>
                              </span>
                          </template> -->
          </el-input>
          <span
            style="position: absolute; right: 10px; top: 5px; cursor: pointer"
          >
            <img
              src="@/assets/images/eye-line.svg"
              style="width: 18px"
              @click="pwdInputChange"
              v-if="pwdType == 'password'"
            />
            <img
              src="@/assets/images/eye-off-line.svg"
              style="width: 18px"
              @click="pwdInputChange"
              v-if="pwdType == 'text'"
            />
          </span>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPwd">{{
          $t("confirm")
        }}</el-button>
        <el-button @click="cancelSubmitPwd">{{ $t("cancel") }}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="$t('roleConfiguration')"
      :visible.sync="roleDialogVisible"
      width="720px"
      class="roleDialog"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div
        style="
          font-family: MiSans, MiSans;
          font-weight: 500;
          font-size: 16px;
          color: #383d47;
          margin-bottom: 8px;
        "
        v-if="quantityConfig"
      >
        <span>{{ $t("selected") }}</span>
        <span style="color: #1c50fd; margin: 0 5px">{{
          tablCheckData.length
        }}</span>
        <span>{{ $t("users") }}</span>
      </div>
      <div style="display: flex; justify-content: space-between">
        <div class="leftrole">
          <div style="display: flex; justify-content: space-between">
            <span
              style="
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 16px;
                color: #383d47;
              "
              >{{ $t("allAvailableRoles") }}</span
            >
            <span
              style="
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 16px;
                color: #1c50fd;
                cursor: pointer;
              "
              @click="chooseAll"
              >{{ $t("selectAll") }}</span
            >
          </div>
          <el-input
            :placeholder="$t('inputPlaceholder')"
            style="margin: 12px 0"
            v-model="roleName"
            clearable
          >
            <el-button
              slot="prepend"
              icon="el-icon-search"
              @click="getroleList"
            ></el-button>
          </el-input>
          <div class="checkboxOuter">
            <div
              v-for="item in roleListData"
              :key="item.roleId"
              style="margin-bottom: 12px"
            >
              <el-checkbox :label="item.roleId" v-model="chooseRole">{{
                item.roleName
              }}</el-checkbox>
            </div>
          </div>
        </div>
        <div
          style="
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: rgba(0, 0, 0, 0.05);
            text-align: center;
            margin-top: 200px;
          "
        >
          <img
            src="@/assets/images/arrow-right-fill.svg"
            style="margin-top: 8px"
          />
        </div>
        <div class="rightrole">
          <div
            style="
              padding: 12px 16px 8px;
              display: flex;
              justify-content: space-between;
              background: linear-gradient(
                0deg,
                rgba(28, 80, 253, 0) 0%,
                rgba(142, 101, 255, 0.1) 100%
              );
            "
          >
            <span
              style="
                font-family: MiSans, MiSans;
                font-weight: 500;
                font-size: 16px;
                color: #383d47;
              "
            >
              <span>{{ $t("authorizeRoles") }}</span>
              <span style="margin-left: 8px; color: #1c50fd">{{
                chooseRole.length
              }}</span>
            </span>
            <span
              style="
                font-family: MiSans, MiSans;
                font-weight: 400;
                font-size: 16px;
                color: #1c50fd;
                cursor: pointer;
                position: relative;
                bottom: 3px;
              "
              @click="clearRole"
            >
              <img
                src="@/assets/images/eraser-line.svg"
                style="width: 18px; margin-right: 3px; vertical-align: middle"
              />
              <span style="vertical-align: middle">{{ $t("clear") }}</span>
            </span>
          </div>
          <div style="padding: 12px 16px" class="checkboxOuters">
            <div
              v-for="item in findCooseRole()"
              :key="item.roleId"
              class="checkResult"
            >
              <img
                src="@/assets/images/indeterminate-circle-fill.svg"
                @click="deleteRole(item.roleId)"
                style="
                  width: 14px;
                  margin-right: 8px;
                  vertical-align: middle;
                  cursor: pointer;
                "
              />
              <span>{{ item.roleName }}</span>
            </div>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="bindRoleList">{{
          $t("confirm")
        }}</el-button>
        <el-button @click="roleDialogVisible = false">{{
          $t("cancel")
        }}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="$t('departmentManagement')"
      :visible.sync="deptDialogVisible"
      width="720px"
      class="roleDialog deptDialog"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <el-tree
        :data="treeData2"
        :props="defaultProps"
        @node-click="handleDialogNodeClick"
        :expand-on-click-node="false"
        style="height: calc(100% - 100px); overflow-y: auto"
      >
        <span
          class="dept-item"
          slot-scope="{ node, data }"
          v-if="!data.isInput"
        >
          <span
            style="
              color: #383d47;
              font-size: 16px;
              margin-left: 8px;
              font-weight: 400;
            "
            >{{ node.label }}</span
          >
          <div class="icon-box" v-if="curDeptId == data.deptId">
            <span>
              <i
                @click.stop="() => openDialog(data, true, node)"
                class="el-icon-plus"
              ></i>
            </span>
            <span style="margin-left: 16px">
              <i
                @click.stop="() => openDialog(data, false)"
                class="el-icon-edit"
              ></i>
            </span>
            <span
              v-if="node.isLeaf"
              style="margin-left: 16px"
              @click.stop="() => deleteOauthDeptFun(data)"
            >
              <i class="el-icon-delete"></i>
            </span>
          </div>
        </span>
        <span class="dept-item" slot-scope="{ node, data }" v-else>
          <el-input
            :placeholder="$t('inputPlaceholder')"
            v-model="data.deptName"
          ></el-input>
          <div class="icon-box">
            <span>
              <i
                @click.stop="() => addOauthDeptFun(data)"
                class="el-icon-check"
              ></i>
            </span>
            <span style="margin-left: 16px">
              <i
                @click.stop="() => closeDialog(data)"
                class="el-icon-close"
              ></i>
            </span>
          </div>
        </span>
      </el-tree>
      <el-button
        style="color: #0e45fc"
        type="text"
        icon="el-icon-plus"
        @click="addFirstDept"
        >{{ $t("addFirstLevelDepartment") }}</el-button
      >
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDialog2">{{ $t("cancel") }}</el-button>
      </span>
    </el-dialog>

    <el-drawer
      :title="$t('review')"
      :visible.sync="addQaVisible"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :wrapperClosable="false"
      class="elDrawer"
    >
      <div slot class="qa-box">
        <div>
          {{ $t("submissionTime") }}：<span style="color: #828894">{{
            setForm.createTime
          }}</span>
        </div>
        <div class="flex-center userInfo">
          <div
            style="
              width: 3px;
              height: 18px;
              background: #1c50fd;
              margin-right: 8px;
            "
          ></div>
          <span>{{ $t("userInfo") }}</span>
        </div>
        <div class="content">
          <el-form :model="setForm" ref="setForm" label-width="80px">
            <el-form-item :label="$t('name')">
              <el-input
                v-model="setForm.username"
                :placeholder="$t('inputPlaceholder')"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('personType')">
              <el-select
                style="width: 100%"
                v-model="setForm.userType"
                :placeholder="$t('selectPlaceholder')"
                clearable
                @change="userTypeCheckChange"
              >
                <el-option
                  v-for="item in userTypeColumns"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('department')" prop="deptId">
              <el-cascader
                popper-class="cascaderStyle"
                :options="treeData"
                :props="{
                  value: 'deptId',
                  label: 'deptName',
                  checkStrictly: true,
                }"
                clearable
                v-model="setForm.deptId"
                :show-all-levels="false"
                style="width: 100%"
              ></el-cascader>
            </el-form-item>
            <el-form-item
              v-if="setForm.userType != 'resident'"
              :label="$t('position')"
            >
              <el-select
                style="width: 100%"
                v-model="setForm.workPosition"
                :placeholder="$t('selectPlaceholder')"
                clearable
                multiple
              >
                <el-option
                  v-for="item in workPositionColumnsCheck"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="setForm.userType != 'resident'"
              :label="$t('status')"
            >
              <el-select
                style="width: 100%"
                v-model="setForm.workStatus"
                :placeholder="$t('selectPlaceholder')"
                clearable
              >
                <el-option
                  v-for="item in workStatusColumns"
                  :key="item.value"
                  :label="item.text"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('phoneNumber')">
              <el-input
                v-model="setForm.phone"
                :placeholder="$t('inputPlaceholder')"
              ></el-input>
            </el-form-item>
            <el-form-item
              v-if="setForm.userType != 'resident'"
              :label="$t('landline')"
            >
              <el-input
                v-model="setForm.landline"
                :placeholder="$t('inputPlaceholder')"
              ></el-input>
            </el-form-item>
            <el-form-item
              v-if="setForm.userType != 'resident'"
              :label="$t('mainWork')"
            >
              <el-input
                v-model="setForm.mainDuty"
                type="textarea"
                :autosize="{ minRows: 6, maxRows: 4 }"
                :placeholder="$t('enterQuestion')"
              ></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin: 40px 0 16px">
          <el-radio-group
            v-model="setForm.checkStatus"
            style="margin-bottom: 12px"
          >
            <el-radio label="pass">{{ $t("approved") }}</el-radio>
            <el-radio label="reject">{{ $t("rejected") }}</el-radio>
          </el-radio-group>
          <el-input
            v-model="setForm.checkOpinion"
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 4 }"
            :placeholder="$t('fillInReviewOpinionHere')"
          ></el-input>
        </div>
        <div class="footer">
          <el-button type="primary" @click="handleAddQaDialog">{{
            $t("submit")
          }}</el-button>
          <el-button @click="addQaVisible = false">{{ $t("close") }}</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import md5 from "js-md5";
import {
  userList,
  deptTree,
  addOauthDept,
  deleteOauthDept,
  roleList,
  bindRole,
  addUser,
  deregisterUser,
  unlockUser,
  deleteUser,
  userSearchRole,
  checkStaff,
} from "@/api/app";
const positionList = [
  { label: "党工委书记", value: "党工委书记" },
  { label: "党工委副书记", value: "党工委副书记" },
  { label: "办事处主任", value: "办事处主任" },
  { label: "纪工委书记", value: "纪工委书记" },
  { label: "党工委委员", value: "党工委委员" },
  { label: "退役军人服务站站长（街道）", value: "退役军人服务站站长（街道）" },
  { label: "办事处副主任", value: "办事处副主任" },
  { label: "武装部部长", value: "武装部部长" },
  {
    label: "退役军人服务站副站长（街道）",
    value: "退役军人服务站副站长（街道）",
  },
  { label: "副主任（正科级）", value: "副主任（正科级）" },
  { label: "科长", value: "科长" },
  { label: "副科长", value: "副科长" },
  { label: "一级调研员", value: "一级调研员" },
  { label: "二级调研员", value: "二级调研员" },
  { label: "三级调研员", value: "三级调研员" },
  { label: "四级调研员", value: "四级调研员" },
  { label: "一级主任科员", value: "一级主任科员" },
  { label: "二级主任科员", value: "二级主任科员" },
  { label: "三级主任科员", value: "三级主任科员" },
  { label: "四级主任科员", value: "四级主任科员" },
  { label: "科员", value: "科员" },
  { label: "主任（事业单位）", value: "主任（事业单位）" },
  { label: "副主任（事业单位）", value: "副主任（事业单位）" },
  { label: "六级职员", value: "六级职员" },
  { label: "七级职员", value: "七级职员" },
  { label: "八级职员", value: "八级职员" },
  { label: "九级职员", value: "九级职员" },
  { label: "社区党委/党总支/党支部书记", value: "社区党委/党总支/党支部书记" },
  {
    label: "社区党委/党总支/党支部副书记",
    value: "社区党委/党总支/党支部副书记",
  },
  { label: "社区居委会主任", value: "社区居委会主任" },
  { label: "退役军人服务站站长（社区）", value: "退役军人服务站站长（社区）" },
  { label: "社区党委/党总支/党支部委员", value: "社区党委/党总支/党支部委员" },
  { label: "社区居委会副主任", value: "社区居委会副主任" },
  { label: "社区服务站站长", value: "社区服务站站长" },
  { label: "社工", value: "社工" },
  { label: "其他", value: "其他" },
];

export default {
  data() {
    const validatePassword = (rule, value, callback) => {
      const regex =
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[~@#$%*_\-+=:,.\?$\]{}])[A-Za-z\d~@#$%*_\-+=:,.\?$\]{}]{8,16}$/;
      if (!value) {
        callback(new Error(this.$t("passwordCannotBeEmpty")));
      } else if (!regex.test(value)) {
        callback(new Error(this.$t("passwordMustBe8To16Characters")));
      } else {
        callback();
      }
    };
    return {
      positionList,
      pwdDialogVisible: false,
      pwdForm: {
        password: "",
      },
      pwdRules: {
        password: [{ validator: validatePassword, trigger: "blur" }],
      },
      row: "",
      pwdType: "password",
      rowData: "",
      roleName: "",
      chooseRole: [],
      roleListData: [],
      quantityConfig: false,
      tablCheckData: [],
      deptId: "",
      userName: "",
      condition: "",
      accountName: "",
      status: "",
      userType: "",
      workPosition: "",
      dateRange: [],
      currentPage: 1,
      total: 0,
      pageSize: 10,
      roleDialogVisible: false,
      roles: {
        powerType: [
          {
            required: true,
            message: this.$t("cannotBeEmpty"),
            trigger: "blur",
          },
        ],
        userName: [
          {
            required: true,
            message: this.$t("cannotBeEmpty"),
            trigger: "blur",
          },
        ],
        accountName: [
          {
            required: true,
            message: this.$t("cannotBeEmpty"),
            trigger: "blur",
          },
        ],
        deptId: [
          {
            required: true,
            message: this.$t("cannotBeEmpty"),
            trigger: "blur",
          },
        ],
        password: [{ validator: validatePassword, trigger: "blur" }],
      },
      roless: {
        powerType: [
          {
            required: true,
            message: this.$t("cannotBeEmpty"),
            trigger: "blur",
          },
        ],
        userName: [
          {
            required: true,
            message: this.$t("cannotBeEmpty"),
            trigger: "blur",
          },
        ],
        accountName: [
          {
            required: true,
            message: this.$t("cannotBeEmpty"),
            trigger: "blur",
          },
        ],
        deptId: [
          {
            required: true,
            message: this.$t("cannotBeEmpty"),
            trigger: "blur",
          },
        ],
      },
      roleForm: {
        powerType: "2",
        userName: "",
        accountName: "",
        password: "",
        phone: "",
        idType: "",
        age: "",
        expireTime: "",
        deptId: [],
        passwordExpiredTime: "",
        email: "",
        idNo: "",
        sex: "",
        remark: "",
        workPosition: "",
        mainDuty: "",
      },
      treeData: [],
      treeData2: [],
      tableData: [],
      defaultProps: {
        label: "deptName",
      },
      dialogVisible: false,
      deptDialogVisible: false,
      curDeptId: null,
      activeName: "first",
      formList: {},
      workStatusColumns: [
        {
          text: this.$t("employed"),
          value: "on",
        },
        {
          text: this.$t("resigned"),
          value: "off",
        },
        {
          text: this.$t("transferred"),
          value: "transferred",
        },
        {
          text: this.$t("vacation"),
          value: "retired",
        },
        {
          text: this.$t("sickLeave"),
          value: "sick",
        },
        {
          text: this.$t("retired"),
          value: "retire",
        },
      ],
      workPositionColumns: [
        { text: "党工委书记", value: "党工委书记" },
        { text: "党工委副书记", value: "党工委副书记" },
        { text: "办事处主任", value: "办事处主任" },
        { text: "纪工委书记", value: "纪工委书记" },
        { text: "党工委委员", value: "党工委委员" },
        {
          text: "退役军人服务站站长（街道）",
          value: "退役军人服务站站长（街道）",
        },
        { text: "办事处副主任", value: "办事处副主任" },
        { text: "武装部部长", value: "武装部部长" },
        {
          text: "退役军人服务站副站长（街道）",
          value: "退役军人服务站副站长（街道）",
        },
        { text: "副主任（正科级）", value: "副主任（正科级）" },
        { text: "科长", value: "科长" },
        { text: "副科长", value: "副科长" },
        { text: "一级调研员", value: "一级调研员" },
        { text: "二级调研员", value: "二级调研员" },
        { text: "三级调研员", value: "三级调研员" },
        { text: "四级调研员", value: "四级调研员" },
        { text: "一级主任科员", value: "一级主任科员" },
        { text: "二级主任科员", value: "二级主任科员" },
        { text: "三级主任科员", value: "三级主任科员" },
        { text: "四级主任科员", value: "四级主任科员" },
        { text: "科员", value: "科员" },
        { text: "主任（事业单位）", value: "主任（事业单位）" },
        { text: "副主任（事业单位）", value: "副主任（事业单位）" },
        { text: "六级职员", value: "六级职员" },
        { text: "七级职员", value: "七级职员" },
        { text: "八级职员", value: "八级职员" },
        { text: "九级职员", value: "九级职员" },
        {
          text: "社区党委/党总支/党支部书记",
          value: "社区党委/党总支/党支部书记",
        },
        {
          text: "社区党委/党总支/党支部副书记",
          value: "社区党委/党总支/党支部副书记",
        },
        { text: "社区居委会主任", value: "社区居委会主任" },
        {
          text: "退役军人服务站站长（社区）",
          value: "退役军人服务站站长（社区）",
        },
        {
          text: "社区党委/党总支/党支部委员",
          value: "社区党委/党总支/党支部委员",
        },
        { text: "社区居委会副主任", value: "社区居委会副主任" },
        { text: "社区服务站站长", value: "社区服务站站长" },
        { text: "社工", value: "社工" },
        { text: "其他", value: "其他" },
      ],
      setForm: {},
      addQaVisible: false,
      userTypeColumns: [
        {
          text: this.$t("resident"),
          value: "resident",
        },
        {
          text: this.$t("staffMember"),
          value: "staff-street",
        },
        {
          text: this.$t("communityMember"),
          value: "staff-community",
        },
      ],
      timer: null,
      isLoading:false
    };
  },
  computed: {
    // 街道工作人员职务
    workPositionList() {
      return this.roleForm.userType == "gov-street"
        ? [
            { label: "党工委书记", value: "党工委书记" },
            { label: "党工委副书记", value: "党工委副书记" },
            { label: "办事处主任", value: "办事处主任" },
            { label: "纪工委书记", value: "纪工委书记" },
            { label: "党工委委员", value: "党工委委员" },
            {
              label: "退役军人服务站站长（街道）",
              value: "退役军人服务站站长（街道）",
            },
            { label: "办事处副主任", value: "办事处副主任" },
            { label: "武装部部长", value: "武装部部长" },
            {
              label: "退役军人服务站副站长（街道）",
              value: "退役军人服务站副站长（街道）",
            },
            { label: "副主任（正科级）", value: "副主任（正科级）" },
            { label: "科长", value: "科长" },
            { label: "副科长", value: "副科长" },
            { label: "一级调研员", value: "一级调研员" },
            { label: "二级调研员", value: "二级调研员" },
            { label: "三级调研员", value: "三级调研员" },
            { label: "四级调研员", value: "四级调研员" },
            { label: "一级主任科员", value: "一级主任科员" },
            { label: "二级主任科员", value: "二级主任科员" },
            { label: "三级主任科员", value: "三级主任科员" },
            { label: "四级主任科员", value: "四级主任科员" },
            { label: "科员", value: "科员" },
            { label: "主任（事业单位）", value: "主任（事业单位）" },
            { label: "副主任（事业单位）", value: "副主任（事业单位）" },
            { label: "六级职员", value: "六级职员" },
            { label: "七级职员", value: "七级职员" },
            { label: "八级职员", value: "八级职员" },
            { label: "九级职员", value: "九级职员" },
            { label: "其他", value: "其他" },
          ]
        : this.roleForm.userType == "gov-community"
        ? [
            {
              label: "社区党委/党总支/党支部书记",
              value: "社区党委/党总支/党支部书记",
            },
            {
              label: "社区党委/党总支/党支部副书记",
              value: "社区党委/党总支/党支部副书记",
            },
            { label: "社区居委会主任", value: "社区居委会主任" },
            {
              label: "退役军人服务站站长（社区）",
              value: "退役军人服务站站长（社区）",
            },
            {
              label: "社区党委/党总支/党支部委员",
              value: "社区党委/党总支/党支部委员",
            },
            { label: "社区居委会副主任", value: "社区居委会副主任" },
            { label: "社区服务站站长", value: "社区服务站站长" },
            { label: "社工", value: "社工" },
            { label: "其他", value: "其他" },
          ]
        : [];
    },
    workPositionColumnsCheck() {
      return this.setForm.userType == "staff-street"
        ? [
            { label: "党工委书记", value: "党工委书记" },
            { label: "党工委副书记", value: "党工委副书记" },
            { label: "办事处主任", value: "办事处主任" },
            { label: "纪工委书记", value: "纪工委书记" },
            { label: "党工委委员", value: "党工委委员" },
            {
              label: "退役军人服务站站长（街道）",
              value: "退役军人服务站站长（街道）",
            },
            { label: "办事处副主任", value: "办事处副主任" },
            { label: "武装部部长", value: "武装部部长" },
            {
              label: "退役军人服务站副站长（街道）",
              value: "退役军人服务站副站长（街道）",
            },
            { label: "副主任（正科级）", value: "副主任（正科级）" },
            { label: "科长", value: "科长" },
            { label: "副科长", value: "副科长" },
            { label: "一级调研员", value: "一级调研员" },
            { label: "二级调研员", value: "二级调研员" },
            { label: "三级调研员", value: "三级调研员" },
            { label: "四级调研员", value: "四级调研员" },
            { label: "一级主任科员", value: "一级主任科员" },
            { label: "二级主任科员", value: "二级主任科员" },
            { label: "三级主任科员", value: "三级主任科员" },
            { label: "四级主任科员", value: "四级主任科员" },
            { label: "科员", value: "科员" },
            { label: "主任（事业单位）", value: "主任（事业单位）" },
            { label: "副主任（事业单位）", value: "副主任（事业单位）" },
            { label: "六级职员", value: "六级职员" },
            { label: "七级职员", value: "七级职员" },
            { label: "八级职员", value: "八级职员" },
            { label: "九级职员", value: "九级职员" },
            { label: "其他", value: "其他" },
          ]
        : this.setForm.userType == "staff-community"
        ? [
            {
              label: "社区党委/党总支/党支部书记",
              value: "社区党委/党总支/党支部书记",
            },
            {
              label: "社区党委/党总支/党支部副书记",
              value: "社区党委/党总支/党支部副书记",
            },
            { label: "社区居委会主任", value: "社区居委会主任" },
            {
              label: "退役军人服务站站长（社区）",
              value: "退役军人服务站站长（社区）",
            },
            {
              label: "社区党委/党总支/党支部委员",
              value: "社区党委/党总支/党支部委员",
            },
            { label: "社区居委会副主任", value: "社区居委会副主任" },
            { label: "社区服务站站长", value: "社区服务站站长" },
            { label: "社工", value: "社工" },
            { label: "其他", value: "其他" },
          ]
        : [];
    },
    tenantId() {
        const tenantId = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.tenantId : "";
        return tenantId;
    }
  },
  created() {
    this.getUserList();
    this.getDeptTree();
  },
  methods: {
    userTypeChange(v) {
      this.roleForm.workPosition = "";
      if (v == "gov") {
        this.roleForm.landline = "";
        this.roleForm.mainDuty = "";
        this.roleForm.workStatus = "";
      }
    },
    userTypeCheckChange(v) {
      this.setForm.workPosition = "";
      if (v == "resident") {
        this.setForm.landline = "";
        this.setForm.mainDuty = "";
        this.setForm.workStatus = "";
      }
    },
    clearInput(arr) {
      // 检查输入是否为数组，如果不是则返回一个空数组
      if (!Array.isArray(arr)) {
        console.error(this.$t("filterTreeFunctionExpectsArray"));
        return [];
      }

      // 使用map创建数组的浅拷贝，并过滤出!isAdd的项
      let filteredArr = arr
        .map((item) => ({ ...item }))
        .filter((item) => !item.isAdd);

      // 使用队列实现迭代处理嵌套的子项，避免递归
      let queue = [...filteredArr]; // 初始队列设为filteredArr的副本，避免修改原数组
      while (queue.length) {
        let item = queue.shift();
        if (item.isInput) item.isInput = false;
        // 如果当前节点有子节点，则创建子节点的过滤副本
        if (item.children && Array.isArray(item.children)) {
          item.children = item.children
            .map((child) => ({ ...child })) // 创建子项的副本
            .filter((child) => !child.isAdd); // 过滤子项

          // 将子项加入队列，进行迭代处理
          queue.push(...item.children);
        }
      }

      return filteredArr;
    },
    addFirstDept() {
      this.treeData2.push({
        pid: "0",
        isInput: true,
        isAdd: true,
      });
    },
    openDialog(data, flag, node) {
      if (flag) {
        data.children.push({
          pid: data.deptId,
          isInput: true,
          isAdd: flag,
        });
        this.$set(node, "expanded", true);
      } else {
        // data.isInput = true
        this.$set(data, "isInput", true);
        this.$set(data, "isAdd", flag);
      }
    },
    closeDialog(data) {
      this.treeData2 = this.clearInput(this.treeData2);
    },
    closeDialog2(data) {
      this.deptDialogVisible = false;
      this.treeData2 = this.clearInput(this.treeData2);
    },
    pwdInputChange() {
      this.pwdType = this.pwdType == "password" ? "text" : "password";
    },
    deregister(row) {
      if (!row.id && this.tablCheckData.length <= 0) {
        this.$message({
          type: "warning",
          message: this.$t("pleaseSelectUserFirst"),
        });
        return;
      }
      this.$confirm(this.$t("thisOperationWillCancelUser"), this.$t("tips"), {
        confirmButtonText: this.$t("confirm"),
        cancelButtonText: this.$t("cancel"),
        type: "warning",
      })
        .then(() => {
          deregisterUser({
            userIdList: row.id
              ? [row.id]
              : this.tablCheckData.map((item) => item.id),
          }).then((res) => {
            if (res.code == "000000") {
              this.getUserList();
              this.$message({
                type: "success",
                message: this.$t("successed"),
              });
            } else {
              this.$message({
                type: "error",
                message: "error",
              });
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: this.$t("cancelledOperation"),
          });
        });
    },
    unlock(row) {
      if (!row.id && this.tablCheckData.length <= 0) {
        this.$message({
          type: "warning",
          message: this.$t("pleaseSelectUserFirst"),
        });
        return;
      }
      this.$confirm(this.$t("thisOperationWillUnlockUser"), this.$t("tips"), {
        confirmButtonText: this.$t("confirm"),
        cancelButtonText: this.$t("cancel"),
        type: "warning",
      })
        .then(() => {
          unlockUser({
            userIdList: row.id
              ? [row.id]
              : this.tablCheckData.map((item) => item.id),
          }).then((res) => {
            if (res.code == "000000") {
              this.getUserList();
              this.$message({
                type: "success",
                message: this.$t("successed"),
              });
            } else {
              this.$message({
                type: "error",
                message: "error",
              });
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: this.$t("cancelledOperation"),
          });
        });
    },
    deleteData(row) {
      this.$confirm(this.$t("thisOperationWillDeleteUser"), this.$t("tips"), {
        confirmButtonText: this.$t("confirm"),
        cancelButtonText: this.$t("cancel"),
        type: "warning",
      })
        .then(() => {
          deleteUser({
            userIdList: row.id
              ? [row.id]
              : this.tablCheckData.map((item) => item.id),
          }).then((res) => {
            if (res.code == "000000") {
              this.getUserList();
              this.$message({
                type: "success",
                message: this.$t("successed"),
              });
            } else {
              this.$message({
                type: "error",
                message: "error",
              });
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: this.$t("cancelledOperation"),
          });
        });
    },
    chagePassword(row) {
      this.pwdType = "password";
      this.row = row;
      this.roleForm = {
        powerType: row.powerType,
        userName: row.username,
        accountName: row.accountName,
        password: "",
        phone: row.phone,
        idType: row.idType,
        age: row.age,
        expireTime: row.expireTime,
        deptId: [row.deptId],
        passwordExpiredTime: row.passwordExpiredTime,
        email: row.email,
        idNo: row.idNo,
        sex: row.sex,
        remark: row.remark,
        mainDuty: row.mainDuty,
        workPosition: row?.workPosition ? row.workPosition.split(",") : [],
        landline: row.landline,
        userType: row.userType,
        workStatus: row.workStatus,
      };
      this.pwdDialogVisible = true;
    },
    editUser(row) {
      this.pwdType = "password";
      this.row = row;
      (this.roleForm = {
        powerType: row.powerType,
        userName: row.username,
        accountName: row.accountName,
        password: row.password,
        phone: row.phone,
        idType: row.idType,
        age: row.age,
        expireTime: row.expireTime,
        deptId: row.deptIds,
        passwordExpiredTime: row.passwordExpiredTime,
        email: row.email,
        idNo: row.idNo,
        sex: row.sex,
        remark: row.remark,
        mainDuty: row.mainDuty,
        workPosition: row?.workPosition ? row.workPosition.split(",") : [],
        landline: row.landline,
        userType: row.userType,
        workStatus: row.workStatus,
      }),
        (this.dialogVisible = true);
    },
    useDefaultValue() {
      this.pwdForm.password = "Zkwg.1234560";
    },
    bindRoleList() {
      let userId = [];
      if (this.quantityConfig) {
        //批量
        userId = this.tablCheckData.map((item) => item.id);
      } else {
        userId = [this.rowData.id];
      }
      bindRole({
        userIdList: userId,
        roleIdList: this.chooseRole || [],
      }).then((res) => {
        if (res.code == "000000") {
          this.roleDialogVisible = false;
          this.getUserList();
          this.$message({
            type: "success",
            message: this.$t("successed"),
          });
        } else {
          this.$message({
            type: "error",
            message: this.$t("failed"),
          });
        }
      });
    },
    chooseAll() {
      this.chooseRole = this.roleListData.map((item) => item.roleId);
    },
    deleteRole(roleId) {
      this.chooseRole = this.chooseRole.filter((item) => item != roleId);
    },
    findCooseRole() {
      let arr = [];
      this.chooseRole.forEach((element) => {
        let item = this.roleListData.find((item) => item.roleId == element);
        arr.push(item);
      });
      return arr;
    },
    clearRole() {
      this.chooseRole = [];
    },
    getroleList() {
      roleList({
        pageNo: 1,
        pageSize: 1000,
        roleName: this.roleName,
      }).then((res) => {
        if (res.code == "000000") {
          this.roleListData = res.data?.records.filter(item=>item.status=="1") || [];
        } else {
          this.roleListData = [];
        }
      });
    },
    getDeptTree() {
      deptTree({
        pageNo: 1,
        pageSize: 1000,
        tenantId: this.tenantId
      }).then((res) => {
        if (res.code == "000000") {
          this.treeData = res.data || [];
          this.treeData2 = JSON.parse(JSON.stringify(this.treeData));
        } else {
          this.treeData = [];
          this.treeData2 = [];
        }
      });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getUserList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getUserList();
    },
    resetSearch() {
      this.deptId = "";
      this.currentPage = 1;
      (this.userName = ""),
        (this.status = ""),
        (this.workPosition = ""),
        (this.userType = "");
        (this.condition = "");
      this.dateRange = [];
      if (this.activeName == "third") {
        this.formList = {
          workPosition: "",
          workStatus: "",
          dateRange: "",
          condition: "",
        };
        this.getThirdList();
      } else {
        this.getUserList();
      }
    },
    search() {
      this.currentPage = 1;
      if (this.activeName == "third") {
        this.getThirdList();
      } else {
        this.getUserList();
      }
    },
    getUserList() {
      this.isLoading=true
      userList({
        deptId: this.deptId,
        pageNo: this.currentPage,
        pageSize: this.pageSize,
        accountName: this.userName,
        condition: this.condition,
        status: this.status,
        workPosition: this.workPosition,
        startDate: this.dateRange ? this.dateRange[0] : "",
        endDate: this.dateRange ? this.dateRange[1] : "",
        userType: this.activeName == "second" ? "resident" : this.userType,
        moduleType:
          this.activeName == "first"
            ? "gov"
            : this.activeName == "second"
            ? "resident"
            : "staff",
      }).then((res) => {
        this.isLoading=false
        if (res.code == "000000") {
          this.total = res.data?.totalRow || 0;
          this.tableData = res.data?.records || [];
          this.timer = new Date().getTime();
        } else {
          this.total = 0;
          this.tableData = [];
        }
      });
    },
    getUserSearchRole() {
      userSearchRole({ userId: this.rowData.id }).then((res) => {
        if (res.code == "000000") {
          this.chooseRole = res.data?.map((item) => item.roleId);
        } else {
          this.chooseRole = [];
        }
      });
    },
    roleConfig(row) {
      this.row = "";
      this.roleName = "";
      this.chooseRole = [];
      if (row) {
        this.rowData = row;
        this.getroleList();
        this.getUserSearchRole();
        this.roleDialogVisible = true;
        this.quantityConfig = false;
      } else {
        //批量配置
        if (this.tablCheckData.length > 0) {
          this.getroleList();
          this.roleDialogVisible = true;
          this.quantityConfig = true;
        } else {
          this.$message({
            type: "warning",
            message: this.$t("pleaseSelectAtLeastOneUser"),
          });
        }
      }
    },
    addUserDialog() {
      this.row = "";
      this.roleForm = {
        powerType: "2",
        userName: "",
        accountName: "",
        password: "",
        phone: "",
        idType: "",
        age: "",
        expireTime: "",
        deptId: [],
        passwordExpiredTime: "",
        email: "",
        idNo: "",
        sex: "",
        remark: "",
        mainDuty: "",
        workPosition: "",
        landline: "",
        userType: "",
        workStatus: "",
      };
      this.dialogVisible = true;
    },
    submitAddUser() {
      this.$refs["roleForm"].validate((valid) => {
        if (valid) {
          let md5password = this.roleForm.password
            ? md5(this.roleForm.password)
            : "";
          let password = this.row ? this.roleForm.password : md5password;
          const workPosition = this.roleForm?.workPosition?.length
            ? this.roleForm.workPosition.join(",")
            : "";
          addUser({
            id: this.row ? this.row.id : "",
            ...this.roleForm,
            password: password,
            deptId:
              this.roleForm.deptId && this.roleForm.deptId.slice(-1)
                ? this.roleForm.deptId.slice(-1).join(",")
                : "",
            workPosition,
          }).then((res) => {
            if (res.code == "000000") {
              this.dialogVisible = false;
              this.getUserList();
              this.$message({
                type: "success",
                message: this.$t("successed"),
              });
            } else {
              this.$message({
                type: "error",
                message: res.msg,
              });
            }
          });
        } else {
          return false;
        }
      });
    },
    submitPwd() {
      this.$refs["pwdForm"].validate((valid) => {
        const workPosition = this.roleForm?.workPosition?.length
          ? this.roleForm.workPosition.join(",")
          : "";
        if (valid) {
          addUser({
            id: this.row ? this.row.id : "",
            ...this.roleForm,
            password: this.pwdForm.password ? md5(this.pwdForm.password) : "",
            deptId:
              this.roleForm.deptId && this.roleForm.deptId.slice(-1)
                ? this.roleForm.deptId.slice(-1).join(",")
                : "",
            workPosition,
          }).then((res) => {
            if (res.code == "000000") {
              this.pwdDialogVisible = false;
              this.getUserList();
              this.$message({
                type: "success",
                message: this.$t("successed"),
              });
            } else {
              this.$message({
                type: "error",
                message: res.msg,
              });
            }
          });
        } else {
          return false;
        }
      });
    },
    cancelSubmitPwd() {
      this.$refs["pwdForm"].resetFields();
      this.pwdDialogVisible = false;
    },
    cancelSubmit() {
      this.$refs["roleForm"].resetFields();
      this.dialogVisible = false;
    },
    handleSelectionChange(val) {
      this.tablCheckData = val;
    },
    handleNodeClick(data) {
      this.deptId = data.deptId;
      this.currentPage = 1;
      this.getUserList();
    },
    handleDialogNodeClick(data, data2) {
      if (!data.isInput) this.curDeptId = data.deptId;
    },
    // 删除部门
    deleteOauthDeptFun(row) {
      this.$confirm(
        this.$t("thisOperationWillDeleteDepartment"),
        this.$t("tips"),
        {
          confirmButtonText: this.$t("confirm"),
          cancelButtonText: this.$t("cancel"),
          type: "warning",
        }
      )
        .then(() => {
          deleteOauthDept([row.id.toString()]).then((res) => {
            if (res.code == "000000") {
              this.getDeptTree();
              this.$message({
                type: "success",
                message: this.$t("successed"),
              });
            } else {
              this.$message({
                type: "error",
                message: "error",
              });
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: this.$t("cancelledOperation"),
          });
        });
    },
    // 删除部门
    addOauthDeptFun(data) {
      let params = {};
      if (data.isAdd) {
        params.deptName = data.deptName;
        params.pid = data.pid;
      } else {
        params = data;
      }
      addOauthDept(params).then((res) => {
        if (res.code == "000000") {
          this.getDeptTree();
          this.$message({
            type: "success",
            message: this.$t("successed"),
          });
        } else {
          this.$message({
            type: "error",
            message: res.msg,
          });
        }
      });
    },
    handleClick(val) {
      this.currentPage = 1;
      this.activeName = val.name;
      this.formList = {};
      this.userType = "";
      if (val.name == "third") {
        this.getThirdList();
      } else {
        this.deptId = "";
        this.userName = "";
        this.condition = "";
        this.status = "";
        this.workPosition = "";
        this.dateRange = [];
        this.getUserList();
      }
    },
    getThirdList() {
      userList({
        pageNo: this.currentPage,
        pageSize: this.pageSize,
        ...this.formList,
        startDate: this.formList.dateRange ? this.formList.dateRange[0] : "",
        endDate: this.formList.dateRange ? this.formList.dateRange[1] : "",
        userType: this.activeName == "second" ? "resident" : this.userType,
        moduleType:
          this.activeName == "first"
            ? "gov"
            : this.activeName == "second"
            ? "resident"
            : "staff",
      }).then((res) => {
        if (res.code == "000000") {
          this.total = res.data?.totalRow || 0;
          this.tableData = res.data?.records || [];
        } else {
          this.total = 0;
          this.tableData = [];
        }
      });
    },
    toExamine(row) {
      this.addQaVisible = true;
      this.setForm = JSON.parse(JSON.stringify(row));
      this.setForm.workPosition = row.workPosition?.split(",");
      if (row?.deptIds) {
        this.setForm.deptId = row.deptIds;
      }
      if (!this.setForm.checkStatus || "waiting" === this.setForm.checkStatus) {
        this.setForm.checkStatus = "pass";
      }
    },
    handleAddQaDialog() {
      const workPosition = this.setForm?.workPosition?.length
        ? this.setForm.workPosition.join(",")
        : "";
      checkStaff({
        userId: this.setForm.id,
        checkOpinion: this.setForm.checkOpinion,
        checkStatus: this.setForm.checkStatus,
        workPosition,
        userName: this.setForm.username,
        userType: this.setForm.userType,
        deptId:
          this.setForm.deptId && this.setForm.deptId.slice(-1)
            ? this.setForm.deptId.slice(-1).join(",")
            : "",
        workStatus: this.setForm.workStatus,
        phone: this.setForm.phone,
        landline: this.setForm.landline,
        mainDuty: this.setForm.mainDuty,
      }).then((res) => {
        if (res.code == "000000") {
          this.$message.success(this.$t("successed"));
          this.addQaVisible = false;
          this.getThirdList();
        } else {
          this.$message({
            type: "warning",
            message: res.msg,
          });
        }
      });
    },
    // 用户显示权限
    permissions(code) {
      const permission = JSON.parse(sessionStorage.getItem("permission"));
      const permissionButton = [];
      this.getUserPermissions(permission, permissionButton);
      const item = permissionButton.find(
        (n) => n.menuCode === code && n.isHidden == 0
      );
      return !!item;
    },
    getUserPermissions(permission, array) {
      permission.forEach((item) => {
        if (item.children && Array.isArray(item.children)) {
          if (item.menuCode == "userManage") {
            array.push(...item.children);
          } else {
            this.getUserPermissions(item.children, array);
          }
        }
      });
    },
    statusWorkingCondition(status) {
      switch (status) {
        case "on":
          return this.$t("employed");
        case "off":
          return this.$t("resigned");
        case "transferred":
          return this.$t("transferred");
        case "retired":
          return this.$t("vacation");
        case "sick":
          return this.$t("sickLeave");
        case "retire":
          return this.$t("retired");
      }
    },
  },
};
</script>
<style lang="scss">
/* 自定义整个滚动条 */
.checkboxOuters::-webkit-scrollbar,
.checkboxOuter::-webkit-scrollbar {
  width: 8px;
  height: 121px;
  background: #fff;
}

/* 自定义滚动条轨道 */
.checkboxOuters::-webkit-scrollbar-track,
.checkboxOuter::-webkit-scrollbar-track {
  background: #fff; /* 轨道颜色 */
}

/* 自定义滚动条的滑块（thumb） */
.checkboxOuters::-webkit-scrollbar-thumb,
.checkboxOuter::-webkit-scrollbar-thumb {
  background: #e1e4eb; /* 滑块颜色 */
  border-radius: 6px;
}
.cascaderStyle {
  .is-checked .el-radio__inner {
    background: #1c50fd;
    border-color: #1c50fd;
  }
  .el-cascader-node.is-selectable.in-checked-path {
    color: #1c50fd !important;
  }
}
.slectStyle {
  .el-select-dropdown__item.selected {
    color: #1c50fd;
  }
}
</style>
<style lang="scss" scoped>
.outer {
  width: 100%;
  height: 100%;
  padding: 32px 24px;
  border-radius: 4px;
  // padding: 10px 0;
  // background: #fff;
  font-family: MiSans, MiSans;
  // border: 1px solid #e1e4eb;
  ::v-deep .el-tabs__header {
    margin: 0;
    .el-tabs__nav {
      margin-left: 24px;
    }
    .el-tabs__item.is-active {
      font-family: MiSans, MiSans;
      font-weight: 600;
      font-size: 18px;
      color: #383d47;
    }
    .el-tabs__item {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 18px;
      color: #383d47;
    }
    .el-tabs__active-bar {
      background-color: #1c50fd;
    }
  }
}
.outer-top {
  padding-top: 10px;
  background: #fff;
  width: 100%;
  height: 100%;
  border: 1px solid #e1e4eb;
  border-radius: 4px;
}
.outer-low {
  display: flex;
  justify-content: flex-start;
  // width: 100%;
  // height: calc(100% - 20px);
}

.leftNav {
  width: 264px;
  height: calc(100vh - 141px);
  background: #fff;
  border-radius: 4px 0 0 4px;
  // border: 1px solid #e1e4eb;
  padding: 16px;
  border-right: 1px solid #e1e4eb;
  // margin-right: 8px;
  position: relative;
}

.rightNav {
  width: calc(100% - 264px);
  height: 100%;
  background: #fff;
  border-radius: 0px 8px 8px 0;
  // border-radius: 4px;
  // border: 1px solid #e1e4eb;
  padding: 24px;

  ::v-deep .el-input__inner:focus,
  ::v-deep .el-date-editor:focus,
  ::v-deep .el-date-editor.is-active {
    border-color: #1c50fd !important;
  }

  .el-button {
    &.el-button-default {
      border-radius: 4px;
      color: #383d47;
      border: 1px solid #c4c6cc;
    }

    &.el-button--primary {
      background-color: #1c50fd;
      color: #fff;
      border-color: #1c50fd;
    }
  }

  ::v-deep .el-input__inner {
    border-radius: 8px !important;
  }

  ::v-deep .el-date-editor .el-range-separator {
    width: 8%;
  }

  ::v-deep .el-checkbox .el-checkbox__inner {
    width: 15px;
    height: 15px;
    border-color: #b4bccc;
  }

  ::v-deep .el-checkbox__label {
    font-size: 16px;
    color: #383d47;
    font-weight: 400;
  }

  ::v-deep .table {
    &::before{
      height: 0;
    }
    .el-table__body-wrapper {
        scrollbar-width: thin;
    }
    .has-gutter {
      th {
        font-size: 14px;
        color: #828894;
        background: #f2f5fa;
      }
    }

    tr {
      color: #383d47;
      font-size: 16px;
      font-weight: 400;
      .el-checkbox__input.is-checked .el-checkbox__inner,
      .el-checkbox__input.is-indeterminate .el-checkbox__inner {
        background: #1c50fd;
        border-color: #1c50fd;
      }
    }

    .el-button--text {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 16px;
      color: #1c50fd;
    }
  }

  ::v-deep .el-table__fixed-header-wrapper {
    height: 48px;

    .el-table__header {
      height: 48px !important;
    }
    th {
      background: #f2f5fa;

      &:last-child {
        display: block;
        .cell {
          color: #828894;
          font-size: 14px;
        }
      }
    }
  }

  ::v-deep .el-pager li {
    border: 1px solid #dddfe8;
    color: #272a31;
    background: #fff;
    &:not(.disabled).active {
      background-color: #fff !important;
      border: 1px solid #1c50fd;
      color: #272a31 !important;
    }
  }

  ::v-deep .btn-prev,
  ::v-deep .btn-next {
    border: 1px solid #dddfe8;
    color: #272a31;
    background: #fff;
  }
}

.rightNavSecond {
  width: calc(100%);
}
::v-deep .dialog {
  .el-dialog__header {
    padding: 20px 32px;
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
    .el-dialog__title {
      color: #383d47;
      font-size: 20px;
      font-weight: 500;
    }
  }
  .el-dialog__body {
    padding: 0 32px;
    max-height: 700px;
    overflow: auto;
    .el-input__inner {
      border-radius: 4px;
    }
    .el-input__inner:focus {
      border-color: #1c50fd;
    }
    .el-input-group--append {
      .el-input-group__append {
        padding: 0 12px;
        background: none;
        border-radius: 0 4px 4px 0;
      }
      .el-input__inner {
        border-radius: 4px 0 0 4px;
      }
    }
    .el-form-item__label {
      color: #383d47;
      font-size: 16px;
      font-weight: 500;
    }
  }
  .el-dialog__footer {
    text-align: left;
    .el-button {
      border-radius: 4px;
    }
    .el-button--primary {
      background: #1c50fd;
      border-color: #1c50fd;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
  .roleForm {
    .el-form-item {
      margin-bottom: 28px;
      .el-radio__input.is-checked .el-radio__inner {
        border-color: #1c50fd;
        background: #1c50fd;
      }
      .el-radio__input.is-checked + .el-radio__label {
        color: #1c50fd;
      }
    }
    .el-form-item__label {
      line-height: 20px;
      padding-bottom: 0;
      margin-bottom: 8px;
    }
  }
}
::v-deep .roleDialog {
  .el-dialog__header {
    padding: 20px 32px;
    background: linear-gradient(
      180deg,
      rgba(43, 88, 213, 0.1) 0%,
      rgba(43, 88, 213, 0) 100%
    );
    .el-dialog__title {
      color: #383d47;
      font-size: 20px;
      font-weight: 500;
    }
  }
  .el-dialog__body {
    padding: 0 32px;
    .leftrole {
      width: 300px;
      padding: 12px 20px;
      border-radius: 4px;
      border: 1px solid #e1e4eb;
      height: 450px;
      min-height: 450px;
    }
    .rightrole {
      width: 300px;
      // padding: 12px 20px;
      border-radius: 4px;
      border: 1px solid #e1e4eb;
      height: 450px;
      min-height: 450px;
    }
    .el-input__inner:focus {
      border-color: #1c50fd;
    }
    .checkboxOuter {
      height: 350px;
      overflow-y: auto;
      .el-checkbox__label {
        width: 235px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #383d47;
      }
      .el-checkbox__input.is-checked .el-checkbox__inner {
        background-color: #1c50fd;
        border-color: #1c50fd;
      }
      .el-checkbox__input {
        position: relative;
        top: -3px;
      }
    }
    .checkboxOuters {
      height: 400px;
      overflow-y: scroll;
      .checkResult {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #383d47;
        vertical-align: middle;
        margin-bottom: 12px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
  .el-dialog__footer {
    text-align: left;
    .el-button {
      border-radius: 4px;
    }
    .el-button--primary {
      background: #1c50fd;
      border-color: #1c50fd;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
}
::v-deep .deptDialog {
  .el-dialog__body {
    padding: 32px;
    .el-tree-node__content {
      height: 48px;
      .dept-item {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
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

.flex-center {
  display: flex;
  align-items: center;
}

.userInfo {
  margin: 32px 0 20px 0;
  span {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 18px;
    color: #383d47;
    line-height: 28px;
  }
}

.content {
  ::v-deep .el-form-item__label {
    font-size: 16px;
    color: #383d47;
  }
  ::v-deep .el-input__inner {
    font-size: 16px;
    color: #494c4f;
  }
  ::v-deep .el-textarea__inner {
    font-size: 16px;
    color: #494c4f;
  }
}
</style>
