<!-- 模型管理 -->
<template>
<div class='modelManagement no-scrollbar'>
    <div style="line-height: 46px;">
        <!-- <div class="modelTitle">{{$t("model")}}</div> -->
        <ul class="tabUl">
            <li v-for="(item,index) in tabList" :key="index" @click="activeIndex = item.label">
                <span :class="[activeIndex==item.label ? 'activecolor' : 'defaultColor','tabName']">{{item.label}}</span>
            </li>
        </ul>
    </div>
    <div v-if="activeIndex=='大模型'">
        <div class="large-model-content">
            <div class="large-model-content-right" v-loading="loading">
                <div class="head">
                    <div>
                        <el-select v-model="paramSearch.manufacturer" @change="searchHandler(paramSearch.manufacturer)">
                            <template #prefix>
                                <img v-if="paramSearch.manufacturer == '全部供应商'" src="@/assets/images/all.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == '雅意'" src="@/assets/images/yayi.png" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == 'Kimi'" src="@/assets/images/kimi.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == 'DeepSeek'" src="@/assets/images/deepseek.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == '文心一言'" src="@/assets/images/wenxinyiyan.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == '智谱清言'" src="@/assets/images/zhipuqingyan.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == '豆包'" src="@/assets/images/doubao.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == '通义千问'" src="@/assets/images/tongyi.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == '中国移动'" src="@/assets/images/deepseek.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == '百川'" src="@/assets/images/baichuan.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == '星火'" src="@/assets/images/xinghuo.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == 'openAI'" src="@/assets/images/openai.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                                <img v-if="paramSearch.manufacturer == 'MINIMAX'" src="@/assets/images/MiniMax.png" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
                            </template>
                            <el-option
                                v-for="(item, index) in manufacturerOptions"
                                :key="index"
                                :label="item"
                                :value="item">
                                <span style="float:left">
                                    <img v-if="item == '全部供应商'" src="@/assets/images/all.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == '雅意'" src="@/assets/images/yayi.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == 'Kimi'" src="@/assets/images/kimi.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == 'DeepSeek'" src="@/assets/images/deepseek.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == '文心一言'" src="@/assets/images/wenxinyiyan.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == '智谱清言'" src="@/assets/images/zhipuqingyan.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == '豆包'" src="@/assets/images/doubao.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == '通义千问'" src="@/assets/images/tongyi.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == '中国移动'" src="@/assets/images/deepseek.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == '百川'" src="@/assets/images/baichuan.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == '星火'" src="@/assets/images/xinghuo.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == 'openAI'" src="@/assets/images/openai.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <img v-if="item == 'MINIMAX'" src="@/assets/images/MiniMax.png" alt="" style="width: 13px;height: 13px; margin-right:10px;">
                                    <!-- <img src="@/assets/images/all.png" alt="" style="width: 13px;height: 13px; margin-right:10px;"> -->
                                </span>
                                <span style="float: left;  font-size: 16px">{{ item }}</span>
                            </el-option>
                        </el-select>
                        <el-input
                            :placeholder="$t('inputLargeModelName')"
                            suffix-icon="el-icon-search"
                            v-model="paramSearch.modelName"
                            style="width: 334px;margin-left: 16px;"
                            @keydown.native.enter="searchHandler('')"
                            clearable
                        >
                        </el-input>
                </div>
                <div>
                  <!-- <el-button plain
                  style="border-radius: 2px;background: linear-gradient(270deg, rgb(142 101 255 / 15%) 0%, rgb(23 71 229 / 15%) 100%);color: #1747e5;border: none;height: 40px;width: 180px;position: relative;top: 2px;margin: 0 6px 0 0;"
                  @click="createApp">
                  一键部署DeepSeek
                  <img src="@/assets/images/deepseek.png" alt=""
                    style="width: 22px; vertical-align: middle; margin-right: 5px; position: relative; bottom: 1px;">
                </el-button> -->
                  <el-button
                      plain
                      style="width: 134px; border: 1px solid #1747E5; background: #1747E5;color:#fff; border-radius: 2px;"
                      @click="deployingLargeModel"
                  >
                      <img src="@/assets/images/add-circle-fill.svg" alt="" style="
                          width: 14px;
                          height: 14px;
                          vertical-align: bottom;
                          margin-right: 5px;
                      ">
                      {{ $t("creatLargeModel") }}
                  </el-button>
                </div>
                </div>
                <ul v-if="list.length" class="list">
                <li class="list-item" v-for="(item, index) in list" :key="index">
                  <div class="preset" v-if="item.ownerType=='official'&&isAdmin">
                    预置
                  </div>
                    <div class="list-item-top">
                    <img
                        v-if="item.manufacturer == '雅意'"
                        src="@/assets/images/yayi.png"
                        alt=""
                    />
                    <img
                        v-if="item.manufacturer == 'Kimi'"
                        src="@/assets/images/kimi.png"
                        alt=""
                    />
                    <img
                        v-if="item.manufacturer == 'DeepSeek'"
                        src="@/assets/images/deepseek.png"
                        alt=""
                    />
                    <img
                        v-if="item.manufacturer == '文心一言'"
                        src="@/assets/images/wenxinyiyan.png"
                        alt=""
                    />
                    <img
                        v-if="item.manufacturer == '智谱清言'"
                        src="@/assets/images/zhipuqingyan.png"
                        alt=""
                    />
                    <img
                        v-if="item.manufacturer == '豆包'"
                        src="@/assets/images/doubao.png"
                        alt=""
                    />
                    <img
                      v-if="item.manufacturer == '通义千问'"
                      src="@/assets/images/tongyi.png"
                      alt=""
                    />
                    <img
                      v-if="item.manufacturer == '中国移动'"
                      src="@/assets/images/deepseek.png"
                      alt=""
                    />
                    <img
                        v-if="item.manufacturer == '百川'"
                        src="@/assets/images/baichuan.png"
                        alt=""
                    />
                    <img
                      v-if="item.manufacturer == '星火'"
                      src="@/assets/images/xinghuo.png"
                      alt=""
                    />
                    <img
                      v-if="item.manufacturer == 'openAI'"
                      src="@/assets/images/openai.png"
                      alt=""
                    />
                    <img
                      v-if="item.manufacturer == 'MINIMAX'"
                      src="@/assets/images/MiniMax.png"
                      alt=""
                    />
                   <div> 
						<div class="text">{{ item.modelName }}</div>
						<div class="lable" v-if="item.labels">
						<div class="tipspan" v-for="item in item.labels.split(',')">{{ item }}</div>
						</div>
				   </div>
                    </div>
                    <div class="list-item-bottom">
					
                    <!-- <div class="row">
                        <div class="label">模型ID</div>
                        <div class="value" style="cursor: pointer;"  @click="cpoyText(item.modelId)">{{ item.modelId }}</div>
                    </div>
                    <div class="row mt12">
                        <div class="label">{{ $t("modelVendor") }}</div>
                        <div class="value">{{ item.manufacturer }}</div>
                    </div>
                    <div class="row mt12">
                        <div class="label">{{ $t("baseModel") }}</div>
                        <div class="value">{{ item.model || item.modelName }}</div>
                    </div> -->
					<div class="list-item-label" :title="item.desc">
						{{ item.desc }}
					
					</div>
                    <div class="row mt12">
						<div class="tips">
							<span class="list-user-icon"><iconpark-icon name="user-3-line"
									size="16"></iconpark-icon></span>
							<span class="create-user" v-if="item.createUser">{{item.createUser}}</span>
							<span class="point" style="margin-right: 8px"></span>
							{{item.updateTime || item.createTime}}发布
						</div>
                        <!-- <div class="label" style="color:#3FB816" v-if="item.status == '启用'"><span style="font-size:30px;vertical-align: text-top;padding-right:5px;">·</span>{{ $t("activeStatus") }}</div>
                        <div class="label" style="color:#BCC1CC" v-else><span style="font-size:30px;vertical-align: text-top;padding-right:5px;">·</span>{{ $t("disabled") }}</div> -->
                        <div style="display: flex;">
							<div style="margin-right: 10px;">
								<el-switch @change="addSwitch($event,item)"  active-color="#1747E5" inactive-color="#B4BCCC" v-model="item.status"  />
							</div>
							<div class="edit" >
							    <div
							        v-if="isAdmin || item.ownerType"
							        class="opts-box"
							        :class="{ 'opts-box-active': activeIndexMoreClick === index }"
							    >  
								    <!-- <div> -->
										
									<!-- </div> -->
							        <el-dropdown
							        trigger="click"
							        @command="(value) => handleCommand(value,item)"
							        placement="top-end"
							        class="opts-box-dropdown"
									
							        @visible-change="handleVisibleChange($event, index)"
							        >
							        <span class="el-dropdown-link">
							            <!-- <i
							            style="transform: rotate(90deg); color: #828894;cursor: pointer;"
							            class="el-icon-more"
							            ></i> -->
							            <iconpark-icon name="more-line" size="18" color="#383838"></iconpark-icon>
							        </span>
							        <!-- api接入详情 -->
							        
							        <el-dropdown-menu slot="dropdown" class="opts-box-dropdown-menu">
							            <el-dropdown-item
							              v-permission="'presetApp'"
							              command="presetApp"
							              v-if="item.ownerType!='official'&&isAdmin"
							              style="display: block;"
							            >
							            <iconpark-icon color="#494E57" name="share-box-line"></iconpark-icon>
							            <span style="color: #494E57">{{ "设为预置" }}</span>
							            </el-dropdown-item>
							            <el-dropdown-item
							              v-permission="'presetApp'"
							              command="presetApp"
							              v-else-if="item.ownerType=='official'&&isAdmin"
							              style="display: block;"
							            >
							            <iconpark-icon color="#F53F3F" name="share-box-line"></iconpark-icon>
							            <span style="color: #F53F3F">{{ "取消预置" }}</span>
							            </el-dropdown-item>
							            <el-dropdown-item command="editeApp" @click="goPluginDetail(item)">
							              <iconpark-icon  color="#494E57" name="edit-box-line"></iconpark-icon>
							              <span style="color: #494E57">{{ $t("edit") }}</span>
							            </el-dropdown-item>
							            <el-dropdown-item
							              v-permission="'deleteApp'"
							              command="deleteApp"
							            >
							            <iconpark-icon color="#494E57"  name="delete-bin-4-line"></iconpark-icon>
							            <span style="color: #494E57">{{ $t("delete") }}</span>
							            </el-dropdown-item>
							        </el-dropdown-menu>
							        </el-dropdown>
							    </div>
							    </div>
						</div>
						
                    </div>
                    </div>
                </li>
                </ul>
                <div v-if="list.length" class="pagination">
                <div class="total">
                    {{ $t("total") }}{{ pageObj.total }}{{ $t("items") }}
                </div>
                <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
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
                <div class="txt1">{{ $t("notFoundLargeModel") }}</div>
                <!-- <div class="txt2">{{ $t("clickDeployLargeModel") }}</div> -->
                <!-- <el-button type="primary" style="width: 112px; font-size: 16px">{{
                    $t("deployLargeModel")
                }}</el-button> -->
                </div>
            </div>
        </div>
    </div>
    <div v-if="activeIndex=='向量化模型'">
      <div class="xl-model-content">
          <div class="xl-model-content-right" v-xl_loading="xl_loading">
              <div class="head">
              <el-input
                  :placeholder="$t('inputVectorizedModel')"
                  v-model="xl_paramSearch.keyword"
                  style="width: 334px"
                  @keydown.native.enter="xl_searchHandler"
                  clearable suffix-icon="el-icon-search"
              >
              </el-input>
              <el-button
                  plain
                  style="border: 1px solid #1747E5; background: #1747E5;color:#fff; border-radius: 2px;"
                  @click="xl_deployingLargeModel"
              >
                  <img src="@/assets/images/add-circle-fill.svg" alt="" style="
                      width: 14px;
                      height: 14px;
                      vertical-align: bottom;
                      margin-right: 5px;
                  ">
                  {{ $t("addAVectorizedModel") }}
              </el-button>
              </div>
              <ul v-if="xl_list.length" class="xl_list">
              <li class="xl_list-item" v-for="(item, index) in xl_list" :key="index">
                  <div class="xl_list-item-top">
                  <div class="text">{{ item.vectorName }}</div>
                  <!-- <div class="tips">{{ item.status }}</div> -->
                  </div>
                  <div class="xl_list-item-bottom">
                  <div class="row">
                      <div class="label">{{ $t("vectorDimension") }}</div>
                      <div class="value">{{ item.dimension }}</div>
                  </div>
                  <div class="row mt12">
                      <div class="label">{{ $t("modelEncoding") }}</div>
                      <div class="flex">
                      <div class="value">{{ item.vectorCode }}</div>
                      <!-- <img
                          class="copy"
                          @click="xl_cpoyText(item.vectorCode)"
                          src="@/assets/images/copy-line1.svg"
                      /> -->
                      </div>
                  </div>
                  </div>
                  <div class="xl_list-item-bottomTip flex centerJustify">
                  <div class="boxTips">
                      <div class="box"></div>
                      <div class="tips">正常</div>
                  </div>

                  <!-- <el-dropdown
                      trigger="click"
                      @command="(value) => xl_handleCommand(value, item)"
                  >
                      <span class="el-dropdown-link">
                      <i
                          style="transform: rotate(90deg); color: #848587"
                          class="el-icon-more"
                      ></i>
                      </span>
                      <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item command="editeApp">
                          <img
                          style="height: 15px; margin-right: 8px"
                          src="@/assets/images/edit-line.svg"
                          />
                          <span>{{ $t("edit") }}</span>
                      </el-dropdown-item>
                      <el-dropdown-item command="deleteApp">
                          <img
                          style="height: 15px; margin-right: 8px"
                          src="@/assets/images/delete-bin-4-line.svg"
                          />
                          <span>{{ $t("delete") }}</span>
                      </el-dropdown-item>
                      </el-dropdown-menu>
                  </el-dropdown> -->
                  </div>
              </li>
              </ul>
              <div v-if="xl_list.length" class="pagination">
              <div class="total">{{ $t("total") }}{{ xl_pageObj.total }}{{ $t("items") }}</div>
              <el-pagination
                  @size-change="xl_handleSizeChange"
                  @current-change="xl_handleCurrentChange"
                  :current-page.sync="xl_pageObj.pageNo"
                  :page-sizes="[12, 24, 36, 48]"
                  :page-size="xl_pageObj.pageSize"
                  background
                  layout="prev, pager, next, sizes"
                  :total="xl_pageObj.total"
              >
              </el-pagination>
              </div>
              <div v-else class="no-data">
              <img src="@/assets/images/no-data.png" alt="" />
              <div class="txt1">{{ $t("noFoundVectorizedModel") }}</div>
              <!-- <div class="txt2">{{ $t("addAVectorizedModel") }}</div>
              <el-button type="primary" style="font-size: 16px">{{
                  $t("addAVectorizedModel")
              }}</el-button> -->
              </div>
          </div>
      </div>
    </div>
    <div v-if="activeIndex=='语音'">
      <Voice />
    </div>
    
    <!-- 部署大模型 -->
    <el-dialog
      :title="dialogTitle + $t('largeModel')"
      :visible.sync="dialogVisible"
      top="3%"
      width="560px"
      height="684px"
      :modal-append-to-body="false"
      append-to-body
      custom-class="large-model-dialog weight1"
      :before-close="closeDialog"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="dialog-config">
        <el-form ref="largeModelForm" :model="largeModelForm" :rules="rules">
          <div style="display: flex;">
			  <el-form-item
				class="select-form"
				prop="manufacturer"
				:label="$t('chooseSupplier')"
				style="width: 40%;margin-right: 2%;"
			  >
				<el-select v-model="largeModelForm.manufacturer" filterable @change="addchange(largeModelForm.manufacturer)" clearable>
					<template #prefix>
					    <img :src="manufacturerIcon" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
					   
					</template>
				  <el-option
					v-for="(item, index) in manufacturerList"
					:key="index"
					:label="item.manufacturer"
					:value="item.manufacturer"
				  >
				  <span style="float:left">
				        <img :src="item.manufacturerIcon" alt="" style="width: 13px;height: 13px; margin-right:10px;">         
				      </span>
				      <span style="float: left;  font-size: 16px">{{ item.manufacturer }}</span>
				  </el-option>
				</el-select>
			  </el-form-item>
			  <el-form-item
				class="select-form"
				prop="model"
				:label="$t('baseModel')"
				style="width: 58%;"
				
			  >
				<el-select multiple
					  filterable
					  allow-create
					  default-first-option
					  multiple-limit="1"
					  placeholder="请输入" style="width:100%;padding-left: 30px;" v-model="largeModelForm.model" clearable>
					<template #prefix>
					    <img :src="manufacturerIcon" alt="" style="width: 14px;height: 14px; margin-top:13px;margin-left: 4px;">
					   
					</template>
				  <el-option
					v-for="(item, index) in manufacturerrenList"
					:key="index"
					:label="item"
					:value="item"
				  >
				  <span style="float:left">
				          <img :src="manufacturerIcon" alt="" style="width: 13px;height: 13px; margin-right:10px;">         
				        </span>
				        <span style="float: left;  font-size: 16px">{{ item }}</span>
				    </el-option>
				  </el-select>
				</el-select>
			  </el-form-item>
		  </div>
		  <!-- <div>模型接入的具体信息，可参考<span style="color: #1747E5;">操作指引</span></div> -->
          <!-- <el-form-item prop="modelName" :label="$t('modelName1')">
            <el-input
              v-model="largeModelForm.modelName"
              :placeholder="$t('pleaseInput')"
              clearable
            />
          </el-form-item>
          <el-form-item prop="model" :label="$t('baseModel')">
            <el-input
              v-model="largeModelForm.model"
              :placeholder="$t('pleaseInput')"
              clearable
            />
          </el-form-item> -->
          
         <!-- <el-form-item prop="uri" :label="$t('apiDomain')">
            <el-input v-model="largeModelForm.uri" :placeholder="$t('pleaseInput')" />
          </el-form-item> -->
		  <el-form-item prop="apiUrl" label="接口地址">		  			   
		    <el-input v-model="largeModelForm.apiUrl" :placeholder="$t('pleaseInput')" />
		  </el-form-item>
          <el-form-item prop="appKey" label="API Key">
			   <div style="float: right;cursor: pointer;" @click="addClick"><span style="color: #1747E5;">从供应商获取 API Key</span></div>
            <el-input v-model="largeModelForm.appKey" :placeholder="$t('pleaseInput')" />
          </el-form-item>
		  <el-form-item label="Secret Key">
		    <el-input v-model="largeModelForm.appSecret" :placeholder="$t('pleaseInput')" />
		  </el-form-item>
		  <el-form-item prop="modelName" :label="$t('modelName')">
		    <el-input
		      v-model="largeModelForm.modelName"
		      :placeholder="$t('pleaseInput')"
		      clearable
		    />
		  </el-form-item>
		  <el-form-item :label="$t('descModel')">
		    <el-input v-model="largeModelForm.desc" type="textarea" :placeholder="$t('pleaseInput')" />
		  </el-form-item>
		  <!-- <el-form-item prop="appKey" label="标签"> -->
		  <!-- <div class="el-form-item-label">标签</div>
		   <div style="display: flex;">
			   <div>标签1</div>
			   <div>标签1</div>
			   <div>标签1</div>
			   <el-input v-model="largeModelForm.appKey" :placeholder="$t('pleaseInput')" />
			</div> -->
		  <!-- </el-form-item> -->
		  <div class="form-item">
		      <div class="el-form-item-label"><span style="color: red;margin-right:4px;">*</span>标签</div>
			  <div class="content tag-add-content">
					<el-select
					  v-model="tagNameList"
					  multiple
					  filterable
					  allow-create
					  default-first-option
					  multiple-limit="3"
					  placeholder="请输入标签" style="width:100%;">
					  <el-option
						v-for="item in tagNameOptions"
						:key="item.value"
						:label="item.label"
						:value="item.value">
					  </el-option>
					</el-select>
				 </div>
		      <!-- <div class="content tag-add-content">
		          <el-input v-model="tagName" placeholder="请输入"></el-input>
		          <el-button type="text" @click="addtagName"><iconpark-icon name="add-line" size="14"
		                  color="#fff"></iconpark-icon></el-button>
		      </div>
		      <div class="tag-list" v-if="tagNameList.length>0">
		          <div class="tag-item" v-for="(item,index) in tagNameList" :key="index">{{item}}<iconpark-icon name="close-large-line" size="14" color="#86909C"
		                  style="margin-left: 10px;cursor: pointer;" @click="remove(index)"></iconpark-icon></div>
		      </div> -->
		  </div>
        </el-form>
      </div>
      <div class="dialog-footer">
        <div>
            <el-switch
                v-model="largeModelFormStatusCode"
                active-color="#1747E5"
                inactive-color="#B4BCCC">
            </el-switch> 
            <span style="margin-left: 10px;">{{largeModelFormStatusCode?'启用':'禁用'}}</span>
        </div>
            <div>
                <el-button plain @click="closeDialog" :loading="addLoading"
                >{{ $t('cancel') }}</el-button
                >
                <el-button type="primary" @click="onSubmit()" :loading="addLoading"
                >{{$t('confirm')}}</el-button
                >
            </div>
      </div>
    </el-dialog>
    <!-- 部署向量化大模型 -->
    <el-dialog
      :title="xl_victorTitle"
      :visible.sync="xl_dialogVisible"
      top="3%"
      width="560px"
      height="684px"
      :modal-append-to-body="false"
      append-to-body
      custom-class="xl-model-dialog weight1"
      :before-close="xl_closeDialog"
      :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="dialog-config">
        <el-form ref="xl_largeModelForm" :model="xl_largeModelForm" :xl_rules="xl_rules">
          <el-form-item prop="vectorName" :label="$t('nameOfVectorizedModel')">
            <el-input
              v-model="xl_largeModelForm.vectorName"
              :placeholder="$t('pleaseEnter')"
              clearable
            />
          </el-form-item>
          <el-form-item prop="vectorCode" :label="$t('modelEncoding')">
            <el-input
              v-model="xl_largeModelForm.vectorCode"
              :placeholder="$t('pleaseEnter')"
              clearable
            />
          </el-form-item>
          <el-form-item
            class="select-form"
            prop="dimension"
            :label="$t('numberOfDimensions')"
          >
            <el-radio-group v-model="xl_largeModelForm.dimension">
              <el-radio :label="768">768</el-radio>
              <el-radio :label="1024">1024</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="uri" :label="$t('callAPIInterfaceAddress')">
            <el-input v-model="xl_largeModelForm.uri" :placeholder="$t('pleaseEnter')" />
          </el-form-item>
        </el-form>
      </div>
      <div class="dialog-footer">
        <el-button plain @click="xl_closeDialog" :xl_loading="xl_addLoading">{{
          $t("cancel")
        }}</el-button>
        <el-button type="primary" @click="xl_onSubmit" :xl_loading="xl_addLoading">{{
          $t("confirm")
        }}</el-button>
      </div>
    </el-dialog>
    <!-- 一键部署deepseek -->
    <createDeepseek v-if="dialogVisibleDeepseek" :dialogVisibleDeepseek="dialogVisibleDeepseek" 
      :params="editItem" @cancelApplication="cancelApplication" @confirmApplication="confirmApplication"
      ></createDeepseek>
      <!-- 删除大模型 -->
      <el-dialog
    :title="$t('deleteApp')"
    :visible.sync="deleteDialogVisible"
    width="560px"
    class="deleteDialog"
    :before-close="cancelDelete"
    append-to-body
    :show-close="false"
  >
    <p class="desc">{{ $t("deletionWarning") }}</p>
    <el-input
      placeholder="请输入要删除的大模型名称"
      v-model="deleteItemName"
    ></el-input>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancelDelete">{{ $t("cancel") }}</el-button>
      <el-button type="primary" @click="confirmDelete">确定删除</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
//这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
//例如：import 《组件名称》 from '《组件路径》';
import {
  apiGetLlmPageList,
  apiGetLlmManufacturer,
  deployLlm,
  apiEditLlm,
  apiAddLlm,
  apiGetLlmManufacturerr,
  apiGetLlmManufacturerModelList,
  apiGetLlmManufacturerModel,
  apiGetLabelList
} from "@/api/toolManager";
import Voice from "@/views/toolManager/voice/index.vue"
import createDeepseek from "@/views/modelManagement/components/createDeepseek.vue";
import {deleteBigModel} from '@/api/index.js'
import {
  getDenseVectorList,
  addDenseVector,
  updateDenseVector,
  deleteDenseVector,
} from "@/api/toolManager";
import {llmInfoPreset} from "@/api/app"
export default {
//import引入的组件需要注入到对象中才能使用
components: {Voice,createDeepseek},
name: "model",
data() {
//这里存放数据
return {
  options: [{
    value: '对话',
    label: '对话'
  }, {
    value: '图标',
    label: '图标'
  }],
tagNameOptions: [{
    value: '深度思考',
    label: '深度思考'
  }, {
    value: '视觉理解',
    label: '视觉理解'
  }, {
    value: 'GUl Agent',
    label: 'GUl Agent'
  }, {
    value: '文本生成',
    label: '文本生成'
  }, {
    value: '图片生成',
    label: '图片生成'
  }, {
    value: '视频生成',
    label: '视频生成'
  }],
  activeIndexMoreClick:null,
  newItem:{}
,  newItemName:'',
manufacturerIcon:'',
  deleteItemName:'',
  deleteDialogVisible:false,
  tagNameList:[],
  tagName:'',
    tabList: [
        {
            label: '大模型',
            value: '大模型'
        },
        {
            label: '向量化模型',
            value: '向量化模型'
        },
        {
            label: '语音',
            value: '语音'
        },
    ],
    activeIndex: "大模型",
    pageObj: {
        pageNo: 1,
        pageSize: 12,
        total: 0,
      },
      manufacturerOptions: [
        {
            label: '全部供应商',
            value: '全部供应商',
            imgsrc: require('@/assets/images/all.png')
        },
        {
            label: '雅意',
            value: '雅意',
            imgsrc: require('@/assets/images/yayi.png')
        },
        {
            label: 'Kimi',
            value: 'Kimi',
            imgsrc: require('@/assets/images/kimi.png')
        },
        {
            label: 'DeepSeek',
            value: 'DeepSeek',
            imgsrc: require('@/assets/images/deepseek.png')
        },
        {
            label: '文心一言',
            value: '文心一言',
            imgsrc: require('@/assets/images/wenxinyiyan.png')
        },
        {
            label: '智谱清言',
            value: '智谱清言',
            imgsrc: require('@/assets/images/zhipuqingyan.png')
        },
        {
            label: '豆包',
            value: '豆包',
            imgsrc: require('@/assets/images/doubao.png')
        },
        {
            label: '百川',
            value: '百川',
            imgsrc: require('@/assets/images/baichuan.png')
        },
        {
            label: '星火',
            value: '星火',
            imgsrc: require('@/assets/images/xinghuo.png')
        },
        {
            label: 'openAI',
            value: 'openAI',
            imgsrc: require('@/assets/images/openai.png')
        }
      ],
      paramSearch: {
        modelName: "", // 模型名称
        status: "", // 状态
        manufacturer: "全部供应商", // 厂商名称
      },
      list: [],
      manufacturerList: [],
	  manufacturerrenList: [],
      loading: false,

      dialogVisibleDeepseek: false,
      dialogVisible: false,
      addLoading: false,
      largeModelForm: {
        manufacturer: "",
        modelName: "",
        model: "",
        desc: "",
        uri: "",
		apiUrl: "",
        tag: [],
        appKey: "",
		appSecret:'',
        status: '启用',
        // statusCode: false
      },
      largeModelFormStatusCode: false,
      rules: {
        manufacturer: [
          {
            required: true,
            message: "请选择供应商",
            trigger: "change",
          },
        ],
        statusCode: [
          {
            required: true,
            message: "请选择状态",
            trigger: "change",
          },
        ],
        modelName: [
          {
            required: true,
            message: "请输入模型名称",
            trigger: "blur",
          },
        ],
        model: [
          {
            required: true,
            message: "请选择基础模型",
            trigger: "blur",
          },
        ],
        apiUrl: [
          {
            required: true,
            message: "请输入接口地址",
            trigger: "blur",
          },
        ],
        appKey: [
          {
            required: true,
            message: "请输入API Key",
            trigger: "blur",
          },
        ],
		// appSecret: [
		//   {
		//     required: true,
		//     message: "请输入Secret Key",
		//     trigger: "blur",
		//   },
		// ],
      },
	  website:'',
      dialogTitle: "",
      xl_pageObj: {
        pageNo: 1,
        pageSize: 12,
        total: 0,
      },
      xl_paramSearch: {
        keyword: "",
      },
      xl_list: [],
      xl_loading: false,

      xl_dialogVisible: false,
      xl_addLoading: false,
      xl_largeModelForm: {
        vectorName: "",
        vectorCode: "",
        uri: "",
        dimension: "",
      },
      xl_rules: {
        dimension: [
          {
            required: true,
            message: this.$t("pleaseSelect"),
            trigger: "change",
          },
        ],
        vectorName: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
        vectorCode: [
          {
            required: true,
            message: this.$t("pleaseEnter"),
            trigger: "blur",
          },
        ],
        // uri: [
        //   {
        //     required: true,
        //     message: "请输入API 域名",
        //     trigger: "blur",
        //   },
        // ],
      },
      xl_dialogTitle: "",
	  largeModelFormModel: "",
      xl_victorTitle: this.$t("addAVectorizedModel"),
};
},
//监听属性 类似于data概念
computed: {
  isAdmin(){
    return JSON.parse(sessionStorage.getItem('user')).powerType==0
  }
},
//监控data中的数据变化
watch: {
    "largeModelFormStatusCode":{
        handler(newVal){
            if(newVal){
                this.largeModelForm.status = '启用'
            }else{
                this.largeModelForm.status = '禁用'
            }
        },
        deep: true
    }
},
//方法集合
methods: {
  async addSwitch(e,item){
	  console.log(e)
	  item.status = e ? '启用' : '禁用'
	  const params = {
	    applicationId: this.data?.applicationId,
	    ...item
	  };
	  try {
	    const res = await apiEditLlm(params);
	    if (res.code == "000000") {
	      this.$message.success(`${this.dialogTitle}成功`);
	      this.getLlmPageList();
	    } else {
	      this.$message.warning(res.msg);
	    }
	  } catch (error) {
	    this.addLoading = false;
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
  addchange(val){
	  console.log('val',val)
	  this.manufacturerList.forEach((item,index)=>{
		  if(item.manufacturer==val){
			  this.manufacturerIcon = item.manufacturerIcon
			  this.website = item.website
		  }
	  })
	  this.largeModelForm.model = ''
  },
  addtagName(){
	if(this.tagName==''){
		this.$message({
		  type:'error',
		  message:'请先输入标签'
		})
		return
	}
	if(this.tagNameList.length>=5){
		this.$message({
		  type:'error',
		  message:'最多输入5个标签'
		})
		return
	}
  	this.tagNameList.push(this.tagName)
  	this.tagName = ''
  	console.log('this.tagNameList',this.tagNameList)
  },
  remove(index){
  	this.tagNameList.splice(index, 1);			
  },
  handleVisibleChange(val, index) {
      if(val){
        this.activeIndexMoreClick = index;
      }else {
        this.activeIndexMoreClick = null;
      }
      
    },
  cancelDelete(){
    this.deleteDialogVisible = false;
    this.deleteItemName = ''
  },
  getLlmInfoPreset(data){
    llmInfoPreset({id:data.id}).then(res=>{
      if(res.code=='000000'){
        this.getLlmPageList()
      }else{
        this.$message({
          type:"error",
          message:res.msg
        })
      }
    })
  },
    handleCommand(value,item) {
        if(value == 'editeApp'){
          this.dialogTitle = '编辑'
		 
        this.dialogVisible = true
        this.largeModelForm = JSON.parse(JSON.stringify(item));
        this.largeModelFormStatusCode = item.status ? true : false
		
		this.largeModelForm.apiUrl = this.largeModelForm.uri
		console.log('点了编辑',this.largeModelForm.labels)
		if(this.largeModelForm.labels!=''&&this.largeModelForm.labels!=null){
			this.tagNameList = this.largeModelForm.labels.split(',')
			
		}
		if(this.largeModelForm.baseModelName!=''&&this.largeModelForm.baseModelName!=null){
			this.largeModelForm.model = this.largeModelForm.baseModelName.split(',')
			
		}else{
			this.largeModelForm.model = this.largeModelForm.model.split(',')
			console.log('largeModelForm.model',this.largeModelForm.model)
		}
		if(this.largeModelForm.labels!=''&&this.largeModelForm.labels!=null){
			this.tagNameList = this.largeModelForm.labels.split(',')
			
		}
		this.manufacturerList.forEach((item,index)=>{
		  if(item.manufacturer==this.largeModelForm.manufacturer){
			  this.manufacturerIcon = item.manufacturerIcon
			  this.website = item.website
		  }
		})
        }else if(value=='deleteApp'){
          this.deleteDialogVisible=true
          this.newItemName = item.modelName
          this.newItem=item
          console.log(this.newItemName)
        }else if(value=='presetApp'){
          this.getLlmInfoPreset(item)
        }
    },
    confirmDelete(){
     if(this.newItemName == this.deleteItemName){
      console.log('名字匹配成功')
      console.log(this.newItem,'即将要删除的的Item')
      deleteBigModel({
        modelId:[this.newItem.modelId]
      }).then(res=>{
        this.$message({
          type:'success',
          message:'删除成功'
        })
      }).catch(err=>{
        this.$message({
          type:'error',
          message:err.message
        })
      })
     }else{
      this.$message({
        type:'error',
        message:'大模型名字匹配失败'
      })
     }
     this.deleteDialogVisible = false;
     this.deleteItemName = ''
     this.getLlmPageList();
    },
    searchHandler(val) {
      if (val && val != "全部供应商") {
        this.paramSearch.manufacturer = val;
      } 
    //   else {
    //     this.paramSearch.manufacturer = "";
    //   }
      this.pageObj.pageNo = 1;
      this.getLlmPageList();
    },
    handleCurrentChange(page) {
      this.pageObj.pageNo = page;
      this.getLlmPageList();
    },
    handleSizeChange(size) {
      this.pageObj.pageSize = size;
      this.getLlmPageList();
    },
    // 查询大模型信息表列表
    async getLlmPageList() {
      const params = {
        ...this.paramSearch,
        manufacturer: this.paramSearch.manufacturer === '全部供应商' ? '':this.paramSearch.manufacturer,
        pageSize: this.pageObj.pageSize,
        pageNo: this.pageObj.pageNo,
        ownerType:"personalGrantTenant"
      };
      this.loading = true;
      const res = await apiGetLlmPageList(params);
      if (res.code == "000000") {
        // console.log("res", res);
        this.list = res.data.records || [];
		this.list.forEach((item) => {
			if(item.status=='启用'){
				item.status= true
			}else{
				item.status=false
			}
		})
        this.pageObj.total = res.data?.totalRow || 0;
      }
      this.loading = false;
    },
    // 查询大模型厂商列表
    async getLlmManufacturer() {
      const params = {
        ...this.paramSearch,
        pageSize: this.pageObj.pageSize,
        pageNo: this.pageObj.pageNo,
      };
      const res = await apiGetLlmManufacturer(params);
      if (res.code == "000000") {
        console.log("res", res);
        // this.manufacturerList = res.data;
        this.manufacturerOptions = res.data;
        this.manufacturerOptions = [
          '全部供应商',
          ...this.manufacturerOptions
        ]
      }
    },
	// 查询大模型厂商列表
	async getLlmManufacturerr() {
	  
	  const res = await apiGetLlmManufacturerr();
	  if (res.code == "000000") {
	    console.log("res", res);
	    this.manufacturerList = res.data;
	    
	  }
	},
	// 查询大模型厂商列表
	async getLlmManufacturerModelList() {
	 
	  const res = await apiGetLlmManufacturerModelList(this.largeModelForm.manufacturer);
	  if (res.code == "000000") {
	    console.log("res", res);
	    this.manufacturerrenList = res.data;
	    
	  }
	},
	// 查询大模型厂商列表
	async getLlmManufacturerModel() {
	  let obj = {
		  manufacturer:this.largeModelForm.manufacturer,
		  modelName:this.largeModelForm.model[0]
	  }
	  const res = await apiGetLlmManufacturerModel(obj);
	  if (res.code == "000000") {
	    console.log("res", res);
	    this.largeModelForm.modelName = res.data.modelName;
		this.largeModelForm.apiUrl = res.data.apiUrl;
		this.largeModelForm.manufacturerModelId = res.data.manufacturerModelId;
	    // this.website = res.data.website
		// this.largeModelFormModel = res.data.model;
	  }
	},
	// 查询标签数据列表
	async getLabelList() {
	  let obj = {
		  pageNo:1,
		  pageSize:999,
		  labelType:1,
		  labelName:this.labelName
	  }
	  const res = await apiGetLabelList(obj);
	  if (res.code == "000000") {
	    this.tagNameOptions = res.data?.length ? res.data : this.tagNameOptions;
	  }
	},
	
    cancelApplication ()
      {
        this.dialogVisibleDeepseek = false;
      },
      confirmApplication (val)
      {
        this.dialogVisibleDeepseek = false;
        let params = {
          "platform": "window",
          "manufacturer": "DeepSeek",
          "modelVersion": val,
          "hostIp": null,
          "hostUser": null,
          "hostPw": null,
          "hostPort": null
        };
        deployLlm(params).then((res) =>
        {
          if (res) {
            this.pageObj.pageNo = 1;
            this.getLlmPageList();
            const url = window.URL.createObjectURL(new Blob([res], { type: 'text/plain' }));
            const link = document.createElement("a");
            link.href = url;
            let name = val == '1_5b' ? '1.5' : val
            let fileName = "deepseek-" + name + ".bat";
            link.setAttribute(
              "download",
              fileName
            );
            document.body.appendChild(link);
            link.click();
          } else {
            this.$message({
              type: "error",
              message: res.msg,
            });
          }
        });
      },
    createApp ()
      {
        this.editItem = {};
        this.dialogVisibleDeepseek = true;
      },
    async deployLlm ()
      {
        
        const params = {
          
        };
        const res = await deployLlm(params);
        if (res.code == "000000") {
          this.pageObj.pageNo = 1;
          this.getLlmPageList();
        }
      },
    deployingLargeModel() {
      this.largeModelForm = {
        manufacturer: "",
        modelName: "",
        model: "",
        uri: "",
        appKey: "",
        status: "禁用",
      }
	  this.tagNameList = []
      this.dialogVisible = true;
      this.dialogTitle = "接入"
      // this.largeModelForm.manufacturer = "";
    },
    closeDialog() {
      this.$refs.largeModelForm.resetFields();
      this.dialogVisible = false;
    },
    onSubmit() {
      this.$refs.largeModelForm.validate((valid) => {
        if (valid) {
            if(this.dialogTitle == "编辑") {
                this.editLlm();
            }else {
                this.addLlm()
            }
        }
      });
    },
    // 编辑
    async editLlm() {
      if(this.tagNameList.length==0){
        this.$message.warning('请至少上传一个标签');
        return false
      }
	  // if(this.tagNameList.length>=5){
	  // 	this.$message({
	  // 	  type:'error',
	  // 	  message:'最多输入5个标签'
	  // 	})
	  // 	return
	  // }
	  for(var i=0;i<this.tagNameList.length;i++){
	  		  if(this.tagNameList[i].length>10){
	  			  this.$message({
	  			  	  type:'error',
	  			  	  message:'标签内容不得超过10个字符'
	  			  	})
	  			  	return
	  		  }
	  }
      this.addLoading = true;
	  this.largeModelForm.model = this.largeModelFormModel
      this.largeModelForm.status = this.largeModelFormStatusCode ? '启用' : '禁用'
      const params = {
        applicationId: this.data?.applicationId,
        ...this.largeModelForm,
        labels:this.tagNameList.join(','),
      };
      try {
        const res = await apiEditLlm(params);
        if (res.code == "000000") {
          this.$message.success(`${this.dialogTitle}成功`);
          this.closeDialog();
          this.searchHandler();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.addLoading = false;
      }
      this.addLoading = false;
    },
    // 新建
    async addLlm() {
      this.largeModelForm.status = this.largeModelFormStatusCode ? '启用' : '禁用'
      if(this.tagNameList.length==0){
        this.$message.warning('请至少上传一个标签');
        return false
      }
	  // if(this.tagNameList.length>=5){
	  // 	this.$message({
	  // 	  type:'error',
	  // 	  message:'最多输入5个标签'
	  // 	})
	  // 	return
	  // }
	  for(var i=0;i<this.tagNameList.length;i++){
		  if(this.tagNameList[i].length>10){
			  this.$message({
			  	  type:'error',
			  	  message:'标签内容不得超过10个字符'
			  	})
			  	return
		  }
	  }
	    this.addLoading = true;
	    this.largeModelForm.model = this.largeModelFormModel
      const params = {
        applicationId: this.data?.applicationId,
        ...this.largeModelForm,
        labels:this.tagNameList.join(),
      };
      try {
        const res = await apiAddLlm(params);
        if (res.code == "000000") {
          this.$message.success(`${this.dialogTitle}成功`);
          this.closeDialog();
          this.searchHandler();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.addLoading = false;
      }
      this.addLoading = false;
    },
    xl_searchHandler() {
      this.xl_pageObj.pageNo = 1;
      this.xl_getLlmPageList();
    },
    xl_handleCurrentChange(page) {
      this.xl_pageObj.pageNo = page;
      this.xl_getLlmPageList();
    },
    xl_handleSizeChange(size) {
      this.xl_pageObj.pageSize = size;
      this.xl_getLlmPageList();
    },
    // 查询大模型信息表列表
    async xl_getLlmPageList() {
      const params = {
        keyword: this.xl_paramSearch.keyword,
        pageSize: this.xl_pageObj.pageSize,
        pageNo: this.xl_pageObj.pageNo,
      };
      this.xl_loading = true;
      const res = await getDenseVectorList(params);
      if (res.code == "000000") {
        this.xl_list = res.data.records || [];
        this.xl_pageObj.total = res.data?.totalRow || 0;
      }
      this.xl_loading = false;
    },
    xl_deployingLargeModel() {
      this.xl_dialogVisible = true;
      this.xl_victorTitle = this.$t("addAVectorizedModel");
      this.xl_dialogTitle = "创建";
      this.xl_largeModelForm = {}
    },
    xl_closeDialog() {
      this.$refs.xl_largeModelForm.resetFields();
      this.xl_dialogVisible = false;
    },
    xl_onSubmit() {
      this.$refs.xl_largeModelForm.validate((valid) => {
        if (valid) {
          if (this.xl_victorTitle == "创建向量化模型") {
            this.xl_addLlm();
          } else {
            this.xl_editLlm();
          }
        }
      });
    },
    // 新增
    async xl_addLlm() {
        this.xl_addLoading = true;
      const params = {
        ...this.xl_largeModelForm,
      };
      try {
        const res = await addDenseVector(params);
        if (res.code == "000000") {
          this.$message.success(`${this.xl_dialogTitle}成功`);
          this.xl_closeDialog();
          this.xl_searchHandler();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.xl_addLoading = false;
      }
      this.xl_addLoading = false;
    },
    async xl_editLlm() {
      this.xl_addLoading = true;
      const params = {
        ...this.xl_largeModelForm,
      };
      try {
        const res = await updateDenseVector(params);
        if (res.code == "000000") {
          this.$message.success(`${this.xl_dialogTitle}成功`);
          this.xl_closeDialog();
          this.xl_searchHandler();
        } else {
          this.$message.warning(res.msg);
        }
      } catch (error) {
        this.xl_addLoading = false;
      }
      this.xl_addLoading = false;
    },
    xl_exeCommandCopyText(text) {
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
    xl_cpoyText(content) {
      this.xl_exeCommandCopyText(content);
      this.$message({
        message: this.$t("copySuccessed"),
        type: "success",
      });
    },
    xl_handleCommand(value, item) {
      if (value == "editeApp") {
        this.xl_dialogVisible = true;
        this.xl_victorTitle = this.$t("editAVectorizedModel");
        this.xl_largeModelForm = item;
      } else {
        this.$confirm("请确认是否删除", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          confirmButtonClass: "confirm-ok",
          cancelButtonClass: "confirm-cancel",
        }).then(async () => {
          const res = await deleteDenseVector([item.id]);
          if (res.code == '000000') {
             this.$message.success('删除成功')
             this.xl_searchHandler();
          }
        });
      }
    },
	addClick(){
		if(this.largeModelForm.manufacturer==''){
			this.$message.success('请先选择供应商')
			return
		}
		window.open(this.website)
		// window.location.href = this.website
	}
},
//生命周期 - 创建完成（可以访问当前this实例）
created() {

},
//生命周期 - 挂载完成（可以访问DOM元素）
mounted() {
    this.getLlmPageList();
    this.getLlmManufacturer();
    this.xl_getLlmPageList();
	this.getLlmManufacturerr()
	this.getLabelList()
	const userType = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user"))?.powerType : "";
	if(userType=='0'||userType=='1'){
		this.tabList = [
        {
            label: '大模型',
            value: '大模型'
        },
        {
            label: '向量化模型',
            value: '向量化模型'
        },
        {
            label: '语音',
            value: '语音'
        }]
	}else{
		this.tabList = [
		{
		    label: '大模型',
		    value: '大模型'
		},
		
		{
		    label: '语音',
		    value: '语音'
		}]
	}
	
},
watch: {
	'largeModelForm.manufacturer'(val){
		if(val){
			this.getLlmManufacturerModelList()
		}
	},
	'largeModelForm.model'(val){
		if(val!=''){
			this.getLlmManufacturerModel()
		}
	}
},
beforeCreate() {}, //生命周期 - 创建之前
beforeMount() {}, //生命周期 - 挂载之前
beforeUpdate() {}, //生命周期 - 更新之前
updated() {}, //生命周期 - 更新之后
beforeDestroy() {}, //生命周期 - 销毁之前
destroyed() {}, //生命周期 - 销毁完成
activated() {}, //如果页面有keep-alive缓存功能，这个函数会触发
}
</script>
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>
<style scoped lang="scss">
.modelManagement {
    padding: 32px 32px 18px 32px;
    .modelTitle {
        font-size: 24px;
        margin-bottom: 30px;
    }
    .tabUl {
        display: flex;
        justify-content: flex-start;
        li {
            margin-right: 16px;
            .tabName {
                font-size: 24px;
                line-height: 28px;
                text-align: right;
                cursor: pointer;
            }
            .defaultColor{
                color: #828894;
                font-weight: 400;
            }
            .activecolor {
                color: #383D47;
                font-weight: 600;
            }
        }
    }
}
.deleteDialog {
  ::v-deep .el-dialog__header {
    background: #fff !important;
  }
  ::v-deep .el-dialog__title {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 20px;
    color: #383d47;
    line-height: 24px;
    text-align: left;
    font-style: normal;
    text-transform: none;
  }
  ::v-deep .el-dialog__headerbtn {
    top: 36px;
  }
  .desc {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #768094;
    line-height: 20px;
    text-align: left;
    font-style: normal;
    margin-bottom: 8px;
  }
  ::v-deep .el-dialog__footer {
    text-align: right !important;
    .el-button {
      border-radius: 4px;
    }
    .el-button--primary {
      background: #1747E5;
      color: #fff;
      border-color: transparent;
    }
  }
}
::v-deep .btn-prev {
  background-color: transparent !important;
  border-radius: 4px !important;
  border: 1px solid #dddfe8;
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
.large-model {
  padding: 24px;
  height: 100%;
  &-title {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #383d47;
    line-height: 40px;
    text-align: left;
    font-style: normal;
    margin-bottom: 24px;
  }
  &-content {
    width: 100%;
    height: calc(100vh - 80px - 32px);
    display: flex;
    margin-top: 16px;
    &-left {
      height: 100%;
      width: 240px;
      padding: 0 16px 16px;
      background: #ffffff;
      border-radius: 4px;
      border: 1px solid #e1e4eb;
      margin-right: 8px;
      .label {
        padding-left: 8px;
        height: 80px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #383d47;
        line-height: 80px;
      }
      .list {
        &-item {
          height: 40px;
          padding: 0 10px;
          margin-bottom: 4px;
          display: flex;
          align-items: center;
          border-radius: 4px;
          cursor: pointer;
          position: relative;
          &:hover {
            background: rgba(63, 138, 251, 0.08);
          }
          img {
            width: 24px;
            height: 24px;
            margin-right: 8px;
          }
          .name {
            font-family: MiSans, MiSans;
            font-size: 16px;
            color: #383d47;
          }
        }
      }
      .bgColor {
        background: rgba(28,80,253,0.05);
        border: 1px solid #1C50FD;
      }
    }
    &-right {
      height: 100%;
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #ffffff;
      border-radius: 8px;
      padding: 0 0 20px;
    //   border: 1px solid #e1e4eb;
      .head {
        display: flex;
        justify-content: space-between;
        height: 40px;
        width: 100%;
      }
      .list {
        flex: 1;
        margin-top: 28px;
        display: flex;
        flex-wrap: wrap;
        overflow: auto;
        align-content: flex-start;
        position: relative;

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

        &-item {
          margin-right: 1.3%;
          margin-bottom: 16px;
          width: 24%;
          height: 182px;
          background: #ffffff;
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          position: relative;
          &:nth-child(4n) {
            margin-right: 0;
          }
		  &:hover {
		  	box-shadow: 0px 4px 8px 0px rgba(21, 34, 51, 0.1);
		  }
          &-top {
            display: flex;
            align-items: center;
            margin: 16px 16px 12px;
            // height: 56px;
            // border-bottom: 1px solid rgba(0, 0, 0, 0.12);
            img {
              width: 44px;
              height: 44px;
            }
            .text {
              margin: 0 6px 0 12px;
              font-family: MiSans, MiSans;
              font-weight: 550;
              font-size: 16px;
              color: #383d47;
            }
			.lable{
				margin: 10px 6px 0 12px;
				overflow: hidden;
				display: flex;
			}
            .tipspan {
              //   width: 40px;
              padding: 0 5px;
              height: 20px;
              line-height: 20px;
              // background: linear-gradient(
              //   270deg,
              //   rgba(142, 101, 255, 0.1) 0%,
              //   rgba(28, 80, 253, 0.1) 100%
              // );
			  background-color: #f2f3f5;
              border-radius: 4px;
              font-family: MiSans, MiSans;
              font-size: 10px;
              text-align: center;
              color: #666;
			  margin-right: 5px;
            }
          }
		  .list-item-label {
		  	color: #666;
		  	margin-top: 10px;
		  	display: -webkit-box;
		  	-webkit-box-orient: vertical;
		  	-webkit-line-clamp: 2;
		  	overflow: hidden;
		  	text-overflow: ellipsis;
		  	line-height: 22px;
		  	height: 45px;
		  	font-size: 14px;
		  }
		  .tips {
		    display: flex;
		    align-items: center;
		    font-family: MiSans, MiSans;
		    font-weight: 400;
		    font-size: 12px;
		    color: #828894;
		    line-height: 16px;
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
          &-bottom {
            padding: 0 16px 20px;
            .row {
              display: flex;
              align-items: center;
              justify-content: space-between;
              .label {
                font-family: MiSans, MiSans;
                font-size: 14px;
                color: #768094;
                width: 56px;
                white-space: nowrap;
                margin-right: 12px;
                height: 18px;
                line-height: 18px;
              }
              .value {
                font-family: MiSans, MiSans;
                font-size: 14px;
                color: #383d47;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                height: 18px;
                line-height: 18px;
              }
            }
            .mt12 {
              margin-top: 12px;
            }
          }
        }
      }
    }
  }
}
.xl-model {
  padding: 24px;
  height: 100%;
  &-title {
    font-family: MiSans, MiSans;
    font-weight: 500;
    font-size: 24px;
    color: #383d47;
    line-height: 40px;
    text-align: left;
    font-style: normal;
    margin-bottom: 24px;
  }
  &-content {
    width: 100%;
    height: calc(100vh - 80px - 32px);
    display: flex;
    margin-top: 16px;
    &-left {
      height: 100%;
      width: 240px;
      padding: 0 16px 16px;
      background: #ffffff;
      border-radius: 4px;
      border: 1px solid #e1e4eb;
      margin-right: 8px;
      .label {
        padding-left: 8px;
        height: 80px;
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #383d47;
        line-height: 80px;
      }
      .xl_list {
        &-item {
          height: 40px;
          padding: 0 10px;
          margin-bottom: 4px;
          display: flex;
          align-items: center;
          border-radius: 4px;
          cursor: pointer;
          &:hover {
            background: rgba(63, 138, 251, 0.08);
          }
          img {
            width: 24px;
            height: 24px;
            margin-right: 8px;
          }
          .name {
            font-family: MiSans, MiSans;
            font-size: 16px;
            color: #383d47;
          }
        }
      }
      .bgColor {
        background: rgba(28, 80, 253, 0.05);
        border: 1px solid #1c50fd;
      }
    }
    &-right {
      height: 100%;
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #ffffff;
      border-radius: 8px;
      padding: 0 0 20px;
    //   border: 1px solid #e1e4eb;
      .head {
        display: flex;
        justify-content: space-between;
        height: 40px;
        width: 100%;
      }
      .xl_list {
        flex: 1;
        margin-top: 28px;
        display: flex;
        flex-wrap: wrap;
        overflow: auto;
        align-content: flex-start;

        &-item {
          margin-right: 1.3%;
          margin-bottom: 16px;
          width: 24%;
          //   height: 141px;
          background: #ffffff;
          border-radius: 4px;
          border: 1px solid #e1e4eb;
          &:nth-child(4n) {
            margin-right: 0;
          }
          &-top {
            display: flex;
            align-items: center;
            margin: 12px 16px;
            // height: 56px;
            // border-bottom: 1px solid rgba(0, 0, 0, 0.12);
            img {
              width: 32px;
              height: 32px;
            }
            .text {
              font-family: MiSans, MiSans;
              font-weight: 550;
              font-size: 16px;
              color: #383d47;
              line-height: 24px;
            }
          }
          &-bottom {
            padding: 0px 16px 14px;
            .row {
              display: flex;
              align-items: center;
              justify-content: space-between;
              .label {
                font-family: MiSans, MiSans;
                font-size: 14px;
                color: #768094;
                width: 56px;
                white-space: nowrap;
                margin-right: 12px;
                height: 18px;
                line-height: 18px;
              }
              .flex {
                width: calc(100% - 60px);
                justify-content: flex-end;
              }
              .value {
                font-family: MiSans, MiSans;
                font-size: 14px;
                color: #383d47;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                height: 18px;
                line-height: 18px;
                // width: 200px;
                text-align: right;
              }
              .copy {
                width: 16px;
                height: 16px;
                margin-left: 4px;
                cursor: pointer;
              }
            }
            .mt12 {
              margin-top: 12px;
            }
          }
          &-bottomTip {
            margin: 0px 16px 16px;
            .boxTips {
              background: rgba(75, 190, 37, 0.1);
              display: flex;
              height: 20px;
              width: 50px;
              line-height: 20px;
              border-radius: 14px;
              text-align: center;
              padding: 0 8px;
              align-items: center;
              .tips {
                font-family: MiSans, MiSans;
                font-size: 12px;
                color: #4bbe25;
              }
              .box {
                width: 5px;
                height: 5px;
                background: #4bbe25;
                border-radius: 3px;
                margin-right: 4px;
              }
            }
          }
        }
      }
    }
  }
}

</style>
<style lang="scss">
.select-form .el-tag {
	margin-left: 30px !important;
}
.tag-add-content {
                display: flex;
                align-items: center;
                margin-bottom: 16px;

                .el-input {
                    margin-right: 8px;
                }

                .el-button {
                    background: #1747E5;
                    border-radius: 4px;
                    height: 40px;
                    width: 40px;
                    color: #fff;
                }
            }

            .tag-list {
                display: flex;
                align-items: center;
                flex-wrap: wrap;

                .tag-item {
                    padding: 6px 12px;
                    font-family: MiSans, MiSans;
                    font-weight: 400;
                    font-size: 14px;
                    color: #1D2129;
                    line-height: 20px;
                    background: rgba(188, 193, 204, 0.2);
                    border-radius: 4px;
                    margin-right: 8px;
                    margin-bottom: 8px;
                    display: flex;
                    align-items: center;
                }

            }
.large-model-dialog {
  background: #fff !important;
  border-radius: 4px;
  .el-dialog__header {
    padding: 16px 32px 16px;
    // background: linear-gradient(
    //   180deg,
    //   rgba(43, 88, 213, 0.1) 0%,
    //   rgba(43, 88, 213, 0) 100%
    // );
    //   display: block !important;
    .el-dialog__title {
      height: 24px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
    }
  }
  .el-dialog__body {
    padding: 0 32px 32px !important;
    background: #fff !important;
    .el-input__inner {
      border-radius: 4px;
    }
    .el-input__inner:focus {
      border-color: #1c50fd;
    }

    .select-form {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      .el-form-item__content {
        width: 100%;
        .el-select {
          width: 100%;
        }
      }
    }

    .checkboxOuter {
      height: 410px;
      padding: 0 8px;
      overflow-y: scroll;
    }
    .el-form-item__label {
      color: #383d47;
      font-size: 16px;
      font-weight: 500;
    }
	.el-form-item-label {
      color: #383d47;
      font-size: 16px;
      font-weight: 500;
	  margin-bottom: 10px;
    }
  }
  .dialog-footer {
    // text-align: right;
    display: flex;
    justify-content: space-between;
    height: 42px;
    line-height: 42px;
    .el-button {
      border-radius: 2px;
    }
    .el-button--primary {
      background: #1747E5;
      border-color: #1747E5;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
}
.xl-model-dialog {
  background: #fff !important;
  border-radius: 4px;
  .el-dialog__header {
    padding: 16px 32px 16px;
    // background: linear-gradient(
    //   180deg,
    //   rgba(43, 88, 213, 0.1) 0%,
    //   rgba(43, 88, 213, 0) 100%
    // );
    //   display: block !important;
    .el-dialog__title {
      height: 24px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #383d47;
      line-height: 24px;
    }
  }
  .el-dialog__body {
    padding: 0 32px 32px !important;
    background: #fff !important;
    .el-input__inner {
      border-radius: 4px;
    }
    .el-radio .el-radio__input.is-checked .el-radio__inner {
        border-color: #1747E5;
        background: #1747E5;
    }
    .el-input__inner:focus {
      border-color: #1747E5;
    }

    .select-form {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      .el-form-item__content {
        width: 100%;
        .el-select {
          width: 100%;
        }
      }
    }

    .checkboxOuter {
      height: 410px;
      padding: 0 8px;
      overflow-y: scroll;
    }
    .el-form-item__label {
      color: #383d47;
      font-size: 16px;
      font-weight: 500;
    }
  }
  .dialog-footer {
    // text-align: right;
    display: flex;
    justify-content: end;
    .el-button {
      border-radius: 2px;
    }
    .el-button--primary {
      background: #1747E5;
      border-color: #1747E5;
    }
    .el-button--default {
      border-color: #c4c6cc;
      color: #383d47;
      font-size: 16px;
    }
  }
}
.confirm-ok {
  background: #1c50fd !important;
  border-color: transparent !important;
}
.confirm-cancel {
  &:hover {
    background: none !important;
    color: #1c50fd !important;
    border-color: #1c50fd !important;
  }
}
</style>