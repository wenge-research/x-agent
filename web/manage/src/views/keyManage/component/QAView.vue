<template>
  <div class="qa-view-wrapper">
    <div class="qa-view-left">
      <div class="left-search">
        <el-input
          v-model="filterText"
          size="small"
          :placeholder="$t('enterKnowledgetreeName')"
          clearable
        >
          <i slot="prefix" class="el-input__icon el-icon-search" />
        </el-input>
      </div>
      <!-- <div class="all-text" :class="{'current-node': isAll}" @click="handleAllClick">
        {{ $t('allText') }}
      </div> -->
      <el-tree ref="tree" :data="categoryData" :props="defaultProps" v-loading="themeLoading" default-expand-all @node-click="handleNodeClick" :filter-node-method="filterNode" node-key="id">
        <div class="theme-item" slot-scope="{ node, data }">
          <span class="info">
            <!-- <i :class="node.expanded ? 'el-icon-folder-opened' : 'el-icon-folder'"></i> -->
            <img v-if="node.expanded" class="folder-opened" :src="require('@/assets/images/folder-open.svg')" alt="">
            <img v-else class="folder" :src="require('@/assets/images/folder.svg')" alt="">
            <span>{{ node.label }}</span>
          </span>
          <!-- <span class="option">
            <el-dropdown>
              <i class="el-icon-arrow-down el-icon-more-outline"></i>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <el-button type="text" icon="el-icon-circle-plus" @click="addtheme(node)">{{$t('addTopic')}}</el-button>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button type="text" icon="el-icon-edit" @click="rentheme(node)">{{$t('edit')}}</el-button>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button type="text" icon="el-icon-delete" @click="remtheme(node)">{{$t('delete')}}</el-button>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </span> -->
        </div>
      </el-tree>
      <div class="tree-btn" @click="themeVisible = true">
        <img :src="require('@/assets/images/archive-line.svg')" alt="">
        <span class="classisfy-btn">
          {{ $t('sortMnagement') }}
        </span>
      </div>
    </div>
    <div class="qa-view-right">
      <div class="right-header">
        <div class="form-box">
          <el-form :inline="true" :model="seachform">
            <el-form-item :label="$t('isPreciseAnswer')">
              <el-select clearable v-model="seachform.accurate" :placeholder="$t('pleaseSelectIsPreciseAnswer')" @change="getKnowledgeDataListData" style="width: 132px">
                <el-option v-for="item in ACCURATE_OPTIONS" :key="item.id" :label="item.type" :value="item.label">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="">
              <el-input v-model="seachform.title" class="my-input" :placeholder="$t('pleaseEnterQuestionOrAnswer')" clearable @input="handleSearch">
                <i slot="prefix" class="el-input__icon el-icon-search" />
              </el-input>
            </el-form-item>
            <el-form-item>
              <div class="icon-refresh" @click="handleAllSearch">
                <svg width="16" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" d="M5.463 4.433A9.961 9.961 0 0 1 12 2c5.523 0 10 4.477 10 10 0 2.136-.67 4.116-1.81 5.74L17 12h3A8 8 0 0 0 6.46 6.228l-.997-1.795Zm13.074 15.134A9.961 9.961 0 0 1 12 22C6.477 22 2 17.523 2 12c0-2.136.67-4.116 1.81-5.74L7 12H4a8 8 0 0 0 13.54 5.772l.997 1.795Z" data-follow-fill="#848587"/></svg>
              </div>
            </el-form-item>
            <!-- <el-form-item :label="$t('question')">
              <el-input v-model="seachform.title" size="small" :placeholder="$t('pleaseEnterQuestion')"></el-input>
            </el-form-item>
            <el-form-item :label="$t('answer')">
              <el-input v-model="seachform.content" size="small" :placeholder="$t('pleaseEnterAnswer')"></el-input>
            </el-form-item>
            <el-form-item :label="$t('topic')">
              <el-cascader clearable :placeholder="$t('pleaseSelectTopic')" :props="{ value: 'id', label: 'type' }" v-model="seachform.category"
                size="small" :options="categoryData"></el-cascader>
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-setting" size="small" @click="themeVisible = true">{{$t('operateTopic')}}</el-button>
            </el-form-item> -->

            <!-- <el-form-item>
              <el-button type="primary" size="small" @click="getKnowledgeDataListData">{{$t('search')}}</el-button>
            </el-form-item> -->
          </el-form>
        </div>
        <div class="btns-box">
          <el-button class="icon-export" plain @click="exportQa">
            <svg width="16" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill-rule="nonzero" d="M13 10h5l-6 6-6-6h5V3h2v7Zm-9 9h16v-7h2v8a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1v-8h2v7Z" data-follow-fill="#848587"/></svg>
            {{$t('exportDate')}}</el-button>
          <el-button type="primary" plain icon="el-icon-plus" @click="addQaDialog">{{$t('add')}}</el-button>
          <el-button type="primary" icon="el-icon-document-add" @click="importVisible = true">{{$t('dataImport')}}</el-button>
        </div>
      </div>
      <!-- <div class="option-box">
        <div>
          <el-popconfirm :title="$t('confirmDelete')" @confirm="deleteInBatches">
            <el-button :disabled="multipleSelection.length === 0" slot="reference" type="danger" size="small"
              icon="el-icon-delete">{{$t('batch')}}{{$t('delete')}}</el-button>
          </el-popconfirm>
        </div>
        <div>
          <el-button type="text" size="small" icon="el-icon-download" @click="handleDownloadKnowledgeDataTemp"
            :loading="downLoading">{{$t('downloadTemplate')}}</el-button>
          <el-button type="warning" size="small" icon="el-icon-circle-plus" @click="exportQa">{{$t('exportDate')}}</el-button>
        </div>
      </div> -->
      <div class="table-btns">
        <div class="selected-num">{{ $t('selected') }}<span class="num">{{multipleSelection.length}}</span></div>
        <el-checkbox v-model="isSelectedAll" @change="handleSelectedAll">{{$t('all')}}</el-checkbox>
        <el-button type="text" :disabled="multipleSelection.length === 0" @click="handleClearSelection">{{$t('clearSelected')}}</el-button>
        <el-popconfirm :title="$t('confirmDelete')" @confirm="deleteInBatches" popper-class="deleteIn-batches-popconfirm">
          <el-button type="text" :disabled="multipleSelection.length === 0" slot="reference"
            icon="el-icon-delete">{{$t('batch')}}{{$t('delete')}}</el-button>
        </el-popconfirm>
      </div>
      <div class="table-box">
        <el-table 
          ref="table" 
          v-loading="tableLoading" 
          class="my-table" 
          :data="tableData" 
          height="100%"
          @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="fileName" :label="$t('knowledge')" min-width="400">
            <template slot-scope="scope">
              <div class="con">
                <div class="question">{{ scope.row.title }}</div>
                <div class="answer">{{ scope.row.content }} </div>
              </div>
            </template>
          </el-table-column>
          <!-- <el-table-column prop="id" label="ID" width="320"></el-table-column>
          <el-table-column prop="userName" label="创建者" width="120"></el-table-column>
          <el-table-column prop="categoryName" :label="$t('topic')" width="200"></el-table-column> -->
          <el-table-column prop="suggest" :label="$t('isRecommended')" width="120"></el-table-column>
          <el-table-column prop="accurate" :label="$t('preciseAnswer')" width="120"></el-table-column>
          <el-table-column prop="updateTime" :label="$t('importTime')" width="200"></el-table-column>
          <el-table-column prop="status" :label="$t('enabledStatus')" width="120">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="$t('yes')"
                :inactive-value="$t('no')"
                active-color="#1747E5"
                inactive-color="#EAECF0"
                :width="24"
                :class="[scope.row.status == $t('yes') ? 'switch-on' : 'switch-off']"
                @change="handleStatusChange(scope.row)"
              >
              </el-switch>
              <span class="switch-status-text">{{ scope.row.status == $t('yes') ? $t('active') : $t('inactive') }}</span>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('action')" width="90">
            <template slot-scope="scope">
              <div class="btns">
                <el-button type="text" @click="setQaDialogue(scope.row)">{{$t('edit')}}</el-button>
                <el-button type="text" @click="remQaDialogue(scope.row.id)">{{$t('delete')}}</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <div class="total-num">
          {{ $t('totalNum', { total: total }) }}
        </div>
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="pageSize"
          :current-page="pageNo" :page-sizes="[20, 50, 100, 150, 200]" layout="prev, pager, next, sizes, jumper" background
          :total="total">
        </el-pagination>
      </div>
      <el-drawer class="my-drawer" :title="setForm.id ? $t('editQa') : $t('addQa')" :visible.sync="addQaVisible" :modal-append-to-body="false" :close-on-click-modal="false"
        :show-close="false" :close-on-press-escape="false" :wrapperClosable="false" size="29.2%" append-to-body>
        <div slot class="qa-box">
          <div class="content">
            <el-form :model="setForm" ref="setForm">
              <el-form-item :label="$t('question')">
                <el-input v-model="setForm.title" :placeholder="$t('pleaseEnterQuestion')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('relevantAnswer')">
                <el-input type="textarea" :autosize="{ minRows: 6, maxRows: 4 }" v-model="setForm.content"
                  :placeholder="$t('pleaseEnterAnswer')" maxlength="2000" show-word-limit></el-input>
              </el-form-item>
              <el-form-item :label="$t('link')">
                <el-input v-model="setForm.link" :placeholder="$t('pleaseEnterLink')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('topic')">
                <el-cascader v-if="addQaVisible" clearable :placeholder="$t('pleaseSelectTopic')" :props="{ value: 'id', label: 'type', checkStrictly: true }"
                  v-model="setForm.category" :options="categoryData"></el-cascader>
              </el-form-item>
              <el-form-item label="">
                <div class="switch-item">
                  <div class="switch-left">
                    <div class="switch-label">{{$t('isRecommended')}}</div>
                    <div class="switch-desc">{{ $t('isRecommendedTip') }}</div>
                  </div>
                  <el-switch
                    v-model="setForm.suggest"
                    :active-value="$t('recommend')"
                    :inactive-value="$t('notRecommend')"
                    active-color="#1747E5"
                    inactive-color="#EAECF0"
                    :width="24"
                    :class="[setForm.suggest == $t('recommend') ? 'switch-on' : 'switch-off']"
                  >
                  </el-switch>
                </div>
                <!-- <el-radio v-model="setForm.suggest" :label="$t('recommend')">{{$t('yes')}}</el-radio>
                <el-radio v-model="setForm.suggest" :label="$t('notRecommend')">{{$t('no')}}</el-radio> -->
              </el-form-item>
              <el-form-item label="">
                <div class="switch-item">
                  <div class="switch-left">
                    <div class="switch-label">{{$t('isPreciseAnswer')}}</div>
                    <div class="switch-desc">{{ $t('preciseAnswerTip') }}</div>
                  </div>
                  <el-switch
                    v-model="setForm.accurate"
                    :active-value="$t('yes')"
                    :inactive-value="$t('no')"
                    active-color="#1747E5"
                    inactive-color="#EAECF0"
                    :width="24"
                    :class="[setForm.accurate == $t('yes') ? 'switch-on' : 'switch-off']"
                  >
                  </el-switch>
                </div>
                <!-- <el-radio v-model="setForm.accurate" :label="$t('yes')">{{$t('yes')}}</el-radio>
                <el-radio v-model="setForm.accurate" :label="$t('no')">{{$t('no')}}</el-radio> -->
              </el-form-item>
              <el-form-item label="">
                <div class="switch-item">
                  <div class="switch-left">
                    <div class="switch-label">{{$t('isRefinementNeeded')}}</div>
                    <div class="switch-desc">{{ $t('isRefinementNeededTip') }}</div>
                  </div>
                  <el-switch
                    v-model="setForm.polishFlag"
                    :active-value="$t('yes')"
                    :inactive-value="$t('no')"
                    active-color="#1747E5"
                    inactive-color="#EAECF0"
                    :width="24"
                    :class="[setForm.polishFlag == $t('yes') ? 'switch-on' : 'switch-off']"
                  >
                  </el-switch>
                </div>
                <!-- <el-radio v-model="setForm.polishFlag" :label="$t('yes')">{{$t('yes')}}</el-radio>
                <el-radio v-model="setForm.polishFlag" :label="$t('no')">{{$t('no')}}</el-radio> -->
              </el-form-item>
            </el-form>
          </div>
          <div class="footer">
            <el-button :disabled="submitLoading" @click.stop="addQaVisible = false">{{ $t('cancel') }}</el-button>
            <el-button :loading="submitLoading" type="primary" @click.stop="handleAddQaDialog">{{ $t('confirm') }}</el-button>
          </div>
        </div>
      </el-drawer>

      <el-dialog :title="$t('tips')" :visible.sync="remVisible" width="400px" :modal-append-to-body="false"
        :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
        <span>{{$t('confirmDelete')}}</span>
        <span slot="footer" class="dialog-footer">
          <el-button :loading="remLoading" @click="remVisible = false">{{ $t('cancel') }}</el-button>
          <el-button :loading="remLoading" type="primary" @click="handleRemQaDialog">{{ $t('confirm') }}</el-button>
        </span>
      </el-dialog>

      <el-dialog class="my-dialog" :title="$t('importExcelFile')" :visible.sync="importVisible" width="720px" :modal-append-to-body="false"
        :close-on-click-modal="false" :close-on-press-escape="false" append-to-body>
        <div class="import-header">
          <el-select v-model="replaceFlag" style="width: 144px">
            <el-option v-for=" item  in  IMPORT_OPTIONS " :key="item.id" :label="item.label" :value="item.con">
            </el-option>
          </el-select>
          <el-button type="text" icon="el-icon-download" @click="handleDownloadKnowledgeDataTemp"
            :loading="downLoading">{{$t('downloadTemplate')}}</el-button>
        </div>
        <div style="margin-bottom: 16px;"></div>
        <el-upload action="#" drag accept=".xlsx" 
        :class="isDrag?'el-upload-dragger':'drag'"
        :before-upload="beforeupload" 
        :show-file-list="false"
        @dragenter.native="handleDragEnter"
        @dragleave.native="handleDragLeave">
          <img class="icon-upload"  src="@/assets/images/upload-cloud-2-fill.svg" alt="">
          <div class="el-upload__text" v-show="!isDrag">{{$t('qaDragXlsxFileHere')}}</div>
          <div class="el-upload__condition" v-show="!isDrag">{{$t('supportedSize')}}</div>
          <div v-show="isDrag" class="draged">松开文件上传</div>
        
        </el-upload>
      
        <el-table
          style="width: 100%"
          :data="uploadFile"
          height="360px"
          class="import-table"
        >
          <el-table-column
            prop="name"
            :label="$t('file')"
          >
            <template slot-scope="scope">
              <div class="file-wrapper">
                <img :src="require(`@/assets/images/excel.svg`)" alt="">
                <div class="file-name">
                  <div class="name">{{ scope.row.name }}</div>
                  <div class="size">{{ (scope.row.size / 1024).toFixed(2) }}KB</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('action')" width="60">
            <template slot-scope="scope">
              <div class="btns">
                <el-button type="text" @click="handleRemovefileItem(scope.row.uid)"
                  >{{$t('delete')}}</el-button
                >
              </div>
            </template>
          </el-table-column>
        </el-table>
        <!-- <div class="file-item" v-for="item in uploadFile" :key="item.uid">
          {{ item.name }} <i @click="handleRemovefileItem" class="el-icon-close"></i>
        </div> -->
        <div style="margin-bottom: 12px;"></div>
        <span slot="footer" class="dialog-footer">
          <el-button :loading="importLoading" @click="handleCancelImport">{{ $t('cancel') }}</el-button>
          <el-button :loading="importLoading" type="primary" @click="handleImport">{{ $t('confirmImport') }}</el-button>
        </span>
      </el-dialog>

      <el-dialog class="my-dialog catalog-manage-dialog" :title="$t('operateTopic')" :visible.sync="themeVisible" width="560px" :modal-append-to-body="false"
        :close-on-click-modal="false" :close-on-press-escape="false" append-to-body>
        <el-tree :data="categoryData" :props="defaultProps" v-loading="themeLoading" default-expand-all draggable :expand-on-click-node="false">
          <div class="theme-item" slot-scope="{ node, data }" @mouseenter="mouseenter(data)" @mouseleave="mouseleave(data)">
            <div v-if="renthemeVisible && typeId == node.data.id" class="info" :class="{ 'theme-name-input': renthemeVisible && typeId == node.data.id }">
              <el-input
                ref="themeName"
                v-model="themeName"
                :placeholder="$t('enterKnowledgetreeName')"
                clearable
                @blur="handleRenTheme(name)"
              >
              </el-input>
            </div>
            <div v-else-if="data.isAddingChild" class="add-child-input">
              <el-input ref="newChildInput" v-model="newChildName" @blur="handleAddTheme(newChildName)" :placeholder="$t('pleaseEnter')"></el-input>
            </div>
            <template v-else>
              <span class="info">
                <!-- <i :class="node.expanded ? 'el-icon-folder-opened' : 'el-icon-folder'"></i> -->
                <span>{{ node.label }}</span>
              </span>
              <div class="options">
                <i class="icon el-icon-plus" @click="addtheme(node)"></i>
                <img  v-if="node.label !== '全部'" class="icon" src="@/assets/images/edit-line2.svg" alt="" @click="rentheme(node)"> 
                <i  v-if="node.label !== '全部'" class="icon el-icon-delete" @click="remtheme(node)"></i>
                <!-- <el-dropdown>
                  <i class="el-icon-arrow-down el-icon-more-outline"></i>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item>
                      <el-button type="text" icon="el-icon-circle-plus" @click="addtheme(node)">{{$t('addTopic')}}</el-button>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <el-button type="text" icon="el-icon-edit" @click="rentheme(node)">{{$t('edit')}}</el-button>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <el-button type="text" icon="el-icon-delete" @click="remtheme(node)">{{$t('delete')}}</el-button>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown> -->
              </div>
            </template>
          </div>
        </el-tree>
        <el-input
          v-if="addFirstLevelVisible"
          ref="themeName"
          v-model="themeName"
          :placeholder="$t('pleaseEnter')"
          clearable
          @blur="handleAddTheme(themeName)"
        >
        </el-input>
        <!-- <div class="tree-btn">
          <el-button type="text" icon="el-icon-plus" @click="handleAddFirstLevel">{{$t('addFirstLevel')}}</el-button>
        </div> -->
        <span slot="footer" class="dialog-footer">
          <el-button @click="handleCancel">{{ $t('cancel') }}</el-button>
          <el-button type="primary" @click="handleConfirm">{{$t('createDirectory')}}</el-button>
        </span>
      </el-dialog>


      <el-dialog :title="$t('addTheme')" :visible.sync="addthemeVisible" width="400px" :modal-append-to-body="false"
        :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
        <el-input v-model="themeName" size="small" :placeholder="$t('pleaseEnterTopicName')" />
        <span slot="footer" class="dialog-footer">
          <el-button :loading="addthemeLoading" @click="addthemeVisible = false">{{ $t('cancel') }}</el-button>
          <el-button :loading="addthemeLoading" type="primary" @click="handleAddTheme">{{ $t('confirm') }}</el-button>
        </span>
      </el-dialog>

      <!-- <el-dialog :title="$t('renameTopic')" :visible.sync="false" width="400px" :modal-append-to-body="false"
        :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false">
        <el-input v-model="themeName" size="small" :placeholder="$t('pleaseEnterTopicName')" />
        <span slot="footer" class="dialog-footer">
          <el-button :loading="renthemeLoading" @click="renthemeVisible = false">{{ $t('cancel') }}</el-button>
          <el-button :loading="renthemeLoading" type="primary" @click="handleRenTheme">{{ $t('confirm') }}</el-button>
        </span>
      </el-dialog> -->

      <el-dialog :title="$t('deleteTopic')" :visible.sync="remthemeVisible" width="400px" :modal-append-to-body="false"
        :close-on-click-modal="false" :show-close="false" :close-on-press-escape="false" append-to-body>
        <span>{{$t('confirmDelete')}}</span>
        <span slot="footer" class="dialog-footer">
          <el-button :loading="remthemeLoading" @click="remthemeVisible = false">{{ $t('cancel') }}</el-button>
          <el-button :loading="remthemeLoading" type="primary" @click="handleRemTheme">{{ $t('confirm') }}</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { SUGGEST_OPTIONS, ACCURATE_OPTIONS, IMPORT_OPTIONS } from '@/views/Kbm/content/index'
import { getKnowledgeDataList, importKnowledgeData, getKnowledgeDataTypeList, addKnowledgeData, deleteKnowledgeData, addKnowledgeDataType, updateKnowledgeDataType, deleteKnowledgeDataType } from '@/api/index.js'
import axios from "axios";
import moment from "moment/moment";
import {formatDate} from "@/utils/tool";
import { debounce } from 'lodash';

export default {
  props: {
    knowledgeId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      isDrag:false,
      filterText: '',
      isAll: true,
      defaultProps: {
        children: 'children',
        label: 'type'
      },
      SUGGEST_OPTIONS,
      ACCURATE_OPTIONS,
      IMPORT_OPTIONS,
      tableData: [],
      categoryData: [],
      multipleSelection: [],
      isSelectedAll: false,
      importData: {},
      remthemeVisible: false,
      remthemeLoading: false,
      renthemeLoading: false,
      renthemeVisible: false,
      categoryLoading: false,
      addthemeLoading: false,
      addthemeVisible: false,
      themeLoading: false,
      themeVisible: false,
      tableLoading: true,
      downLoading: false,
      addQaVisible: false,
      importVisible: false,
      remVisible: false,
      submitLoading: false,
      importLoading: false,
      remLoading: false,
      replaceFlag: '否',
      qaId: '',
      seachform: {
        title: '', // 问题
        content: '', // 答案
        // link: '', // 链接
        category: [], // 主题
        // suggest: '', // 推荐
        accurate: '', // 精准回答
        polishFlag: '' // 润色
      },
      setForm: {
        title: '',
        content: '',
        link: '',
        category: [],
        suggest: '', // 推荐
        accurate: '', // 精准回答
        polishFlag: '' // 润色
      },
      rules: {
        title: [{ required: true, message: this.$t('pleaseEnterQuestion'), trigger: 'blur' }],
        content: [{ required: true, message: this.$t('pleaseEnterAnswer'), trigger: 'blur' }],
        link: [{ required: true, message: this.$t('pleaseEnterLink'), trigger: 'blur' }],
        category: [{ required: true, message: this.$t('pleaseSelectCategory'), trigger: 'change' }],
        suggest: [{ required: true, message: this.$t('pleaseSelectIsRecommended'), trigger: 'change' }],
        accurate: [{ required: true, message: this.$t('pleaseSelectIsPreciseAnswer'), trigger: 'change' }],
        polishFlag: [{ required: true, message: this.$t('pleaseSelectIsRefinementNeededn'), trigger: 'change' }],
      },
      pageNo: 1,
      pageSize: 50,
      total: 0,
      totalPage: 0,

      themeName: '',
      typeId: '',
      pid: '',
      uploadForm: new FormData(),
      uploadFile: [],
      addFirstLevelVisible: false,
      firstThemeName: '',
      newChildName: '',
      foldersId: '',
      intervalTime: null
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
    knowledgeId(newVal) {
      console.log('QA watch')
      this.getKnowledgeDataListData()
    },
  },
  async created() {
    await this.getKnowledgeDataTypeListData()
    await this.getKnowledgeDataListData()
  },
  methods: {
    handleDragLeave(e){
      console.log('我的文件出来咯，芜湖~~~~')
      e.preventDefault();
      this.isDrag=false
    },
    handleDragEnter(e){
      console.log('我的文件进来了，芜湖~~~~~~~~')
      e.preventDefault();
      this.isDrag=true
    },
    handleAllSearch() {
      this.getKnowledgeDataTypeListData()
      this.getKnowledgeDataListData()
    },
    handleCancel() {
      this.themeVisible = false
      this.seachform.category = []
      this.isAll = true
      this.getKnowledgeDataTypeListData()
      this.getKnowledgeDataListData()
    },
    async handleConfirm() {
      this.themeVisible = false
      this.seachform.category = []
      this.isAll = true
      this.getKnowledgeDataTypeListData()
      this.getKnowledgeDataListData()
    },
    removeNodesWithIsAddingChild(nodes) {
      for (let i = nodes.length - 1; i >= 0; i--) {
        if (nodes[i].isAddingChild === true) {
          nodes.splice(i, 1);
        } else if (nodes[i].children && nodes[i].children.length > 0) {
          this.removeNodesWithIsAddingChild(nodes[i].children);
        }
      }
    },
    getParentIds(node, childId) {
      if (node.parent) {
        this.getParentIds(node.parent, childId);
        this.seachform.category.push(node.data.id) // 输出父节点的 ID
      }
      // console.log(childId); // 输出最初点击的子节点 ID
    },
    getParentNodeIds(node) {
      const parentIds = [];
      let currentNode = node;

      while (currentNode) {
        parentIds.unshift(currentNode.data.id);
        currentNode = this.findParentNode(this.categoryData, currentNode.data.id);
        console.log(9090, currentNode)
      }
      
      return parentIds;
    },

    findParentNode(treeData, childId) {
      for (const node of treeData) {
        if (node.children && node.children.length > 0) {
          const foundNode = this.findParentNode(node.children, childId);
          if (foundNode) {
            return node;
          }
        }
        if (node.id === childId) {
          return null; // 找到子节点，返回父节点（即当前节点）
        }
      }
      return null;
    },
    handleAddFirstLevel () {
      this.removeNodesWithIsAddingChild(this.categoryData);
      this.isAddingChild = false
      this.renthemeVisible = false
      this.addFirstLevelVisible = true
      this.themeName= ''
      this.firstThemeName= ''
      this.typeId = "0"
    },
    mouseenter(data) {
      this.$set(data, 'show', true)
    },
    mouseleave(data) {
      this.$set(data, 'show', false)
    },
    async handleStatusChange(row) {
      await addKnowledgeData({
        ...row,
        knowledgeId: this.knowledgeId,
        status: row.status,
      })
      setTimeout(() => {
        this.getKnowledgeDataListData();
      }, 1000)
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.type.indexOf(value) !== -1;
    },
    handleSearch: debounce(function() {
      this.getKnowledgeDataListData()
    }, 500),
    handleSelectedAll() {
      this.$refs.table.toggleAllSelection();
    },
    handleClearSelection() {
      this.$refs.table.clearSelection();
    },
    handleAllClick() {
      this.isAll = true
      this.foldersId = ''
      this.seachform.category = []
      this.$nextTick(() => {
        this.$refs.tree.setCurrentKey(null);
      })
      this.getKnowledgeDataListData()
    },
    handleNodeClick(data, node) {
      this.isAll = false
      this.seachform.category = []
      this.getParentIds(node, node.data.id)
      this.foldersId = node.data.id
      this.getKnowledgeDataListData()
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
      this.isSelectedAll = val.length === this.pageSize ||  val.length === this.tableData.length
    },
    addQaDialog() {
      this.setForm.title = ''
      this.setForm.content = ''
      this.setForm.link = ''
      this.setForm.suggest = this.$t('recommend')
      this.setForm.accurate = ''
      this.setForm.polishFlag = ''
      this.setForm.id = ''
      this.setForm.id ? this.setForm.category = [] : this.setForm.category = this.categoryData[0]?.id || []
      this.addQaVisible = true
      console.log(this.setForm, '---')
    },
    handleRemovefileItem() {
      this.uploadForm = new FormData();
      this.uploadFile = []
    },
    beforeupload(file) {
      this.isDrag=false
      this.uploadForm = new FormData()
      console.log('12345', file)
      this.uploadForm.append('file', file);
      this.uploadFile = [file]
      return false;
    },
    handleCancelImport() {
      this.importVisible = false
      this.uploadFile = []
      this.uploadForm = new FormData();
    },
    async handleImport() {
      if (this.uploadFile.length === 0) {
        this.$message({
          message: this.$t('pleaseSelectUploadFile'),
          type: 'warning'
        });
        return false
      }
      this.importLoading = true
      this.uploadForm.append('knowledgeId', this.knowledgeId)
      let category = Array.isArray(this.seachform.category) ? this.seachform.category.join('/') : this.seachform.category
      this.uploadForm.append('category', category)
      this.uploadForm.append('replaceFlag', this.replaceFlag === '否' ? false : true)
      const res = await importKnowledgeData(this.uploadForm)
      if (res.code === '000000') {
        this.handleCancelImport()
        this.tableLoading = true
        // this.intervalTime = setInterval(() => {
          this.getKnowledgeDataListData()
        // }, 2000)
        this.$message({
          message: res.msg,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
      this.importLoading = false
    },
    addtheme(node) {
      this.themeName = ''
      if (node === 'root') {
        this.removeNodesWithIsAddingChild(this.categoryData);
        this.isAddingChild = false
        this.renthemeVisible = false
        this.themenName = ''
        this.addFirstLevelVisible = true
        this.typeId = "0"
      } else {
        // 设置当前节点的 isAddingChild 为 true
        node.data.children ? '' : this.$set(node.data, 'children', []);
        if (node.data.children.findIndex(item => item.isAddingChild) === -1) {
          // 调用递归函数重置 isAddingChild 属性
          this.removeNodesWithIsAddingChild(this.categoryData);
          node.data.children.push({  isAddingChild: true, type: '' })
        }
        this.isAddingChild = true
        this.renthemeVisible = false
        this.themenName = ''
        this.addFirstLevelVisible = false
        // this.$set(node.data.children, 'isAddingChild', true);
        this.typeId = node.data.id
        // 清空新子节点名称
        this.newChildName = '';
        // 确保输入框聚焦
        this.$nextTick(() => {
          this.$refs.newChildInput.focus();
        });
      }
      // this.addthemeVisible = true
    },
    removeNodesWithIsAddingChild(nodes) {
      for (let i = nodes.length - 1; i >= 0; i--) {
        if (nodes[i].isAddingChild === true) {
          nodes.splice(i, 1);
        } else if (nodes[i].children && nodes[i].children.length > 0) {
          this.removeNodesWithIsAddingChild(nodes[i].children);
        }
      }
    },
    async handleAddTheme(name) {
      name ? this.themeName = name : ''
      if (!this.themeName) {
        this.$message.error(this.$t('enterSortName'))
        return
      }
      this.addthemeLoading = true
      const res = await addKnowledgeDataType({
        pid: this.typeId,
        type: this.themeName,
        knowledgeId: this.knowledgeId
      })
      if (res.code === '000000') {
        this.getKnowledgeDataTypeListData()
        this.addthemeVisible = false
        this.addFirstLevelVisible = false
        this.$message({
          message: res.msg,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
      this.addthemeLoading = false
    },
    rentheme(node) {
      if (this.renthemeVisible) {
        return
      }
      this.themeName = node.data.type
      this.typeId = node.data.id
      this.renthemeVisible = true
      this.removeNodesWithIsAddingChild(this.categoryData);
      this.$set(node.data, 'isAddingChild', false)
      this.addFirstLevelVisible = false
      this.$nextTick(() => {
        this.$refs.themeName.focus()
      })
    },
    async handleRenTheme(name) {
      name ? this.themeName = name : ''
      if (!this.themeName) {
        this.$message.error(this.$t('enterSortName'))
        return
      }
      this.renthemeLoading = true
      const res = await updateKnowledgeDataType({
        id: this.typeId,
        type: this.themeName,
        knowledgeId: this.knowledgeId
      })
      if (res.code === '000000') {
        this.getKnowledgeDataTypeListData()
        this.renthemeVisible = false
        this.$message({
          message: res.msg,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
      this.renthemeLoading = false
    },
    remtheme(node) {
      this.remthemeVisible = true
      this.typeId = node.data.id
      this.pid = node.data.pid
    },
    async handleRemTheme(node) {
      this.remthemeLoading = true
      const res = await deleteKnowledgeDataType({
        idList: [this.typeId],
      })
      if (res.code === '000000') {
        this.getKnowledgeDataTypeListData()
        this.remthemeVisible = false
        this.$message({
          message: res.msg,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
      this.remthemeLoading = false
    },
    async getKnowledgeDataListData() {
      if (this.knowledgeId === '') {
        this.tableLoading = false
        return false
      }
      this.tableLoading = true
      const { data } = await getKnowledgeDataList({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        knowledgeId: this.knowledgeId,
        ...this.seachform,
        category: Array.isArray(this.seachform.category) ? this.seachform.category.join('/'): this.seachform.category
      })
      this.total = data.total
      this.tableData = data.list
      this.tableLoading = false
    },
    async handleAddQaDialog() {
      this.submitLoading = true
      const res = await addKnowledgeData({
        ...this.setForm,
        category: Array.isArray(this.setForm.category) ? this.setForm.category.join('/') : this.setForm.category,
        knowledgeId: this.knowledgeId,
        status: '是',
      })
      if (res.code === '000000') {
        this.addQaVisible = false
        this.tableLoading = true
        setTimeout(() => {
          this.getKnowledgeDataListData()
        }, 1500)
        this.$message({
          message: res.msg,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
      this.submitLoading = false
    },
    // 下载模版
    async handleDownloadKnowledgeDataTemp() {
      window.location.href = process.env.VUE_APP_API_NEW + '/knowledgeData/downloadKnowledgeDataTemp' // 生产
    },
    handleCurrentChange(n) {
      this.pageNo = n
      this.getKnowledgeDataListData()
    },
    handleSizeChange(n) {
      this.pageSize = n
      this.getKnowledgeDataListData()
    },
    async getKnowledgeDataTypeListData() {
      this.themeLoading = true
      const { data } = await getKnowledgeDataTypeList({
        pageNo: 1,
        pageSize: 10,
        knowledgeId: this.knowledgeId
      })
      this.categoryData = data.records
      this.foldersId = data.records.find((item) => {
        return item.type == '全部'
      })?.id
      this.seachform.category = [this.foldersId]
      this.$nextTick(() => {
        this.$refs.tree.setCurrentKey(this.foldersId);
      })
      this.themeLoading = false
    },
    setQaDialogue(item) {
      this.setForm.title = item.title
      this.setForm.content = item.content
      if (item.category) {
        this.setForm.category = item.category.split('/').map(Number);
      }
      this.setForm.suggest = item.suggest
      this.setForm.accurate = item.accurate
      this.setForm.polishFlag = item.polishFlag
      this.setForm.id = item.id
      this.setForm.link = item.link

      console.log(this.setForm, '---')
      this.addQaVisible = true
    },
    remQaDialogue(id) {
      this.qaId = id
      this.remVisible = true
    },
    async handleRemQaDialog() {
      this.remLoading = true
      const res = await deleteKnowledgeData({
        id: [this.qaId]
      })
      if (res.code === '000000') {
        this.tableLoading = true
        setTimeout(() => {
          this.getKnowledgeDataListData()
        }, 1500)
        this.remVisible = false
        this.$message({
          message: res.msg,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
      this.remLoading = false
    },
    async deleteInBatches() {
      const res = await deleteKnowledgeData({
        id: this.multipleSelection.map(n => n.id)
      })
      if (res.code === '000000') {
        this.remVisible = false
        this.tableLoading = true
        this.multipleSelection = []
        setTimeout(() => {
          this.getKnowledgeDataListData()
        }, 1500)
        this.$message({
          message: res.msg,
          type: 'success'
        });
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
    },
    exportQa() {
      this.tableLoading = true
      let _this = this;
      axios({
        method: 'POST',
        url: `${process.env.VUE_APP_BASE_API}/knowledgeData/exportData`,
        headers: {
          Authorization: 'Bearer ' + sessionStorage.getItem('manageAccessToken'),
        },
        responseType: 'blob',
        data: {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          knowledgeId: this.knowledgeId,
          ...this.seachform,
          category: Array.isArray(this.seachform.category) ? this.seachform.category.join('/') : this.seachform.category
        },
      })
          .then((res) => {
            let date = new Date();
            let fullYear = date.getFullYear();
            let month = date.getMonth();
            month = month < 10 ? `0${month}` : `${month}`;

            let day = date.getDate();
            let hours = date.getHours();
            hours = hours < 10 ? `0${hours}` : `${hours}`;
            let minutes = date.getMinutes();
            let fileName = `${fullYear}${month}${day}_${hours}${minutes}.xlsx`;
            const url = window.URL.createObjectURL(new Blob([res.data]))
            const link = document.createElement('a')
            link.href = url
            link.setAttribute('download', fileName)
            document.body.appendChild(link)
            link.click()
            _this.tableLoading = false
          })
          .catch((error) => {
            console.log('config-res-error:', error)
            _this.tableLoading = false
          })
    }
  },
  beforeDestroy() {
    // 清除定时器
    if (this.intervalTime) {
      clearInterval(this.intervalTime);
      this.timeintervalTimer = null;
    }
  }
}

</script>

<style lang="scss" scoped>
@import "./Kbm.scss";

.qa-view-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.qa-view-left {
  width: 294px;
  height: 100%;
  padding: 24px 24px 30px 32px;
  border-right: 1px solid rgba(0,0,0,0.12);
  .left-search {
    margin-bottom: 16px;
  }
  .el-input {
    width: 240px;
    ::v-deep .el-input__inner {
      height: 40px;
      line-height: 40px;
    }
  }
  ::v-deep .el-input__inner::-webkit-input-placeholder {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #B4BCCC;
    line-height: 20px;
  }
  ::v-deep .el-input__inner::-moz-placeholder {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #B4BCCC;
    line-height: 20px;
  }
  ::v-deep .el-input__inner:-ms-input-placeholder {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #B4BCCC;
    line-height: 20px;
  }
  ::v-deep .el-input__inner::placeholder {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #B4BCCC;
    line-height: 20px;
  }
  .all-text {
    padding: 6px 0 6px 8px;
    margin-bottom: 4px;
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 16px;
    color: #494E57;
    line-height: 28px;
    &.current-node {
      background: #F4F5F8;
      border-radius: 8px;
    }
    &:hover {
      background: #F4F5F8;
      border-radius: 8px;
    }
  }
  ::v-deep .el-tree {
    height: calc(100% - 150px);
    overflow-y: auto;
    .el-tree-node.is-current > .el-tree-node__content {
      background: #F4F5F8;
      border-radius: 8px;
    }
    .el-tree-node:focus > .el-tree-node__content {
      background: #F4F5F8;
      border-radius: 8px;
    }
    .el-tree-node__content {
      height: 40px;
      margin-bottom: 4px;
      border-radius: 2px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
      // padding: 8px 12px;
      &:hover {
        background: #F4F5F8;
        border-radius: 8px;
      }
    }
    .theme-item {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-right: 10px;
    }
    .info {
      display: flex;
      align-items: center;
      img {
        width: 18px;
        margin-right: 8px;
      }
    }
  }
  .tree-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 8px 0;
    border-radius: 2px;
    border: 1px solid #1747E5;
    cursor: pointer;
    img {
      width: 18px;
    }
    .classisfy-btn{
      margin-left: 8px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 14px;
      color: #1747E5;
      line-height: 20px;
    }
  }
}
.qa-view-right {
  width: calc(100% - 294px);
  height: 100%;
  padding: 24px 32px 32px 24px;
  display: flex;
  flex-direction: column;
  .right-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: nowrap;
  }
  .form-box {
    .el-form-item {
      margin-bottom: 0px;
    }
    .my-input {
      width: 348px;
    }
  }
  .btns-box {
    display: flex;
    align-items: center;
    .icon-export {
      svg {
        vertical-align: text-top;
        fill: #494E57;
      }
      &:hover {
        svg {
          fill: #1747E5;
        }
      }
    }
  }

  .option-box {
    display: flex;
    margin: 10px 0;
    justify-content: space-between;
  }
  .table-btns {
    margin-top: 24px;
    display: flex;
    align-items: center;
    .selected-num, .el-checkbox, .el-button {
      margin-right: 16px;
    }
    .selected-num {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #828894;
      .num {
        margin-left: 4px;
        color: #1747E5;
      }
    }
  }
  .table-box {
    flex-grow: 1;
    ::v-deep .my-table.el-table .el-table__header .el-table__cell .cell {
      padding: 10px 12px; 
    }
    .con {
      flex: 1;

      .question {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 14px;
        color: #0BA7B9;
        line-height: 20px;

        .time {
          font-weight: 400;
          font-size: 14px;
          color: #8a8f99;
          line-height: 19px;
          text-align: left;
          font-style: normal;
        }
      }

      .answer {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #828894;
        line-height: 20px;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        -webkit-line-clamp: 1;
      }

      .info {
        margin-top: 10px;

        span {
          display: inline-block;
          height: 28px;
          background: #F5F6F7;
          border-radius: 4px;
          font-weight: 400;
          font-size: 14px;
          color: #3d4042;
          text-align: justify;
          font-style: normal;
          margin-right: 8px;
          padding: 0 8px;
          line-height: 28px;
        }
      }
    }

    .btns {
      display: flex;
      // justify-content: center;
    }
  }

  .qa-box {
    position: relative;
    padding-bottom: 60px;
    width: 100%;
    display: flex;
    flex-direction: column;
    height: 100%;

    .content {
      position: relative;
      padding: 0 32px;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      flex-grow: 1;
      overflow-y: auto;
    }

    .footer {
    }

    .el-select {
      width: 100%;
    }

    .el-date-editor {
      width: 100%;
    }

    .btns {
      display: flex;
      justify-content: flex-end;
    }
  }

  .theme-item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-right: 10px;

    .info {
      font-size: 15px;
      i {
        margin-right: 5px;
      }
    }


  }
}
.import-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

::v-deep .el-upload-dragger {
  width: 100%;
  position: relative;
  height: 112px;
  background: #F9FAFC;
  border-radius: 2px;
  border: 1px dotted rgba(0,0,0,0.12);
  .el-upload-dragger{
    width: 100%;
    position: relative;
  height: 112px;
  border-radius: 2px;
    background-color: #f0f8ff; /* 浅蓝色 */
  border: 1px dotted #409eff; /* 蓝色边框 */
  }
  .icon-upload {
    width: 22px;
    height: 20px;
    margin: 16px 0 8px;
  }
  .el-icon-upload {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 20px;
    color: #494E57;
    line-height: 20px;
  }
  .el-upload__text {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #494E57;
    line-height: 20px;
  }
  .el-upload__condition {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 12px;
    color: #B4BCCC;
    line-height: 16px;
  }
  .draged{
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 14px;
    color: #494E57;
    line-height: 20px;
  position: absolute;
  top:50%;
  left:50%;
  transform:translate(-50%,-50%) ;
}
} 

.file {
  margin: 10px 0;
  display: flex;
  justify-content: space-between;

  .name {
    font-weight: bold;
  }
}

.file-item {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  font-weight: bold;
  margin-top: 8px;

  i {
    cursor: pointer;
  }
}
.catalog-manage-dialog {
  ::v-deep .el-tree {
    height: calc(100% - 150px);
    max-height: 400px;
    overflow-y: auto;
    .el-tree__empty-block {
      display: none;
    }
    .el-tree-node:focus > .el-tree-node__content {
      background: #F0F2F5;
      border-radius: 8px;
    }
    .el-tree-node__content {
      height: 40px;
      margin-bottom: 4px;
      border-radius: 2px;
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px !important;
      color: #494E57;
      line-height: 20px;
      // padding: 8px 12px;
      &:hover {
        background: #F0F2F5;
        border-radius: 8px;
      }
    }
    .theme-item {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-right: 10px;
    }
    .info {
      display: flex;
      align-items: center;
      img {
        width: 18px;
        margin-right: 8px;
      }
    }
    .theme-name-input {
      width: 100%;
    }
    .add-child-input {
      width: 100%;
    }
    .options {
      display: flex;
      align-items: center;
      .icon {
        margin-right: 4px;
        width: 16px;
        height: 16px;
        font-size: 16px;
        color: #828894;
        cursor: pointer;  
      }
    }
  }
}
.file-wrapper {
  display: flex;
  align-items: center;
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 14px;
  color: #494E57;
  line-height: 20px;
  .file-name {
    .name {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #494E57;
      line-height: 20px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      -webkit-line-clamp: 1;
    }
    .size {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 12px;
      color: #BCC1CC;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      -webkit-line-clamp: 1;
    }
  }
  img {
    width: 24px;
    height: 24px;
    margin-right: 20px;
  }
  
}
.import-table {
  margin-bottom: 28px;
  ::v-deep .el-table__body-wrapper {
    height: auto !important;
    max-height: 100%;
  }
}
</style>
<style>
.deleteIn-batches-popconfirm {
  .el-popconfirm {
    padding: 8px;
  }
}
</style>