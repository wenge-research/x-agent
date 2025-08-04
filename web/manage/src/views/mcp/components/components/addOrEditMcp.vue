<!-- 子组件 -->
<template>
    <div class="addOrEditMcp">
        <el-drawer size="36%" title="配置工具" :visible.sync="localVisible" direction="rtl"
            @close="$emit('update:isShowAddMcp', false)">
            <div class="dra-content">
                <el-form :model="formInfo">
                    <el-form-item label="名称" required>
                        <el-input v-model="formInfo.name" placeholder="请输入名称"></el-input>
                    </el-form-item>
                    <el-form-item label="描述" required>
                        <el-input v-model="formInfo.desc" type="textarea" :autosize="{ minRows: 2, maxRows: 4 }"
                            placeholder="请输入描述"></el-input>
                    </el-form-item>
                </el-form>

                <div class="parameter">
                    <p class="title">API</p>
                    <div class="edit-api" @click="editApi()">
                        <iconpark-icon name="add-line" size="12" color="#1D2129"
                            style="margin-right: 4px;"></iconpark-icon>
                        <span>编辑API</span>
                    </div>
                </div>
                <div class="api-list" v-if="Object.keys(customData).length > 0">
                    <div class="title">
                        <div class="bule-box"></div>
                        <span class="name">输入参数</span>
                    </div>
                    <div class="api-item" v-for="(item, index) in customData.parameterList" :key="index">
                        <div class="item-top">
                            <p>{{ item.form }}</p>
                            <p class="isFlag">{{ item.type }}</p>
                            <p class="isFlag" v-show="item.required">必填</p>
                        </div>
                        <div class="item-desc">{{ item.description }}</div>
                    </div>
                </div>
                <div class="btn-box">
                    <el-button @click="cancelInfo">取消</el-button>
                    <el-button type="primary" @click="sumitApiInfo">确定</el-button>
                </div>
            </div>
        </el-drawer>

        <el-dialog title="编辑API" :modal="false" top="0" :visible.sync="dialogVisible" width="51%"
            custom-class="full-height-dialog" show-close>
            <div class="dialog-content">
                <div class="input-api">
                    <div class="title">
                        <div class="bule-box"></div>
                        <span class="name">API</span>
                    </div>
                    <div class="authentication">
                        <div class="icon-box" @click="selectAuth('1')"
                            :style="{ color: selectedAuth === '1' ? '#1D2129' : 'inherit' }">
                            <iconpark-icon name="lock-password-line"
                                :color="selectedAuth === '1' ? '#1D2129' : '#C9CDD4'"
                                style="margin-right: 6px; margin-bottom: 4px;"></iconpark-icon>
                            <span>鉴权</span>
                        </div>
                        <div @click="selectAuth('0')" :style="{ color: selectedAuth === '0' ? '#1D2129' : 'inherit' }">
                            无需鉴权</div>
                    </div>
                </div>
                <div class="request-box">
                    <div class="request-type">
                        <el-select v-model="requestType">
                            <el-option v-for="item in requestOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </div>
                    <div class="request-url">
                        <el-input v-model="requestUrl" placeholder="请输入API地址"></el-input>
                    </div>
                    <div class="run-box" @click="runApi">
                        <iconpark-icon name="send-plane-2-fill" color="#FFFFFF"
                            style="margin-right: 6px;"></iconpark-icon>
                        <span>试运行</span>
                    </div>
                </div>

                <div class="request-info">
                    <div class="title">
                        <div class="bule-box"></div>
                        <span class="name">输入参数</span>
                    </div>
                    <div class="request-config">
                        <div class="tab-item" v-for="(item, index) in tabs" :key="index"
                            :class="{ active: activeIndex === index }" @click="toggleActive(index)">
                            {{ item }}
                            <iconpark-icon v-if="item === 'Path'" name="question-line"
                                title="Path 参数必须以 ${} 方式拼接在 URL 上"></iconpark-icon>
                        </div>
                    </div>
                    <!-- 内容区域 -->
                    <div class="content-area">
                        <el-table class="no-bottom-border" :data="currentTableData" style="width: 100%">
                            <el-table-column label="参数名" width="200">
                                <template #default="{ row, column, $index }">
                                    <el-input v-model="row.paramName" placeholder="请输入参数名"
                                        @change="handleInputChange(row, 'name', $event)" />
                                </template>
                            </el-table-column>

                            <el-table-column label="描述" width="230">
                                <template #default="{ row }">
                                    <el-input v-model="row.description" placeholder="请输入描述"
                                        @change="handleInputChange(row, 'desc', $event)" />
                                </template>
                            </el-table-column>

                            <el-table-column label="参数类型" width="150">
                                <template #default="{ row }">
                                    <el-select v-model="row.type" placeholder="请选择类型"
                                        @change="handleInputChange(row, 'desc', $event)">
                                        <el-option v-for="item in jsTypes" :key="item" :label="item" :value="item" ></el-option>
                                    </el-select>
                                </template>
                            </el-table-column>

                            <el-table-column label="设定值" width="180">
                                <template #default="{ row }">
                                    <el-input v-model="row.defaults" placeholder="请输入设定值"
                                        @change="handleInputChange(row, 'value', $event)" max-width="240" />
                                </template>
                            </el-table-column>

                            <el-table-column label="必填" width="80">
                                <template #default="{ row }">
                                    <el-switch v-model="row.required" :active-value="true" :inactive-value="false"
                                        @change="handleInputChange(row, 'value', $event)" />
                                </template>
                            </el-table-column>

                            <el-table-column fixed="right" label="操作" width="80">
                                <template #default="scope">
                                    <el-button @click="deleteRow(scope.row)" type="text">
                                        <i class="el-icon-delete" style="color: #1d2129; font-size: 16px"></i>
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>

                        <div class="add-btn" @click="addRow">
                            <i class="el-icon-plus" style="color: #1d2129; font-size: 14px; margin-right: 14px"></i>
                            <span>新增参数</span>
                        </div>
                    </div>
                </div>


                <!-- josn数据 -->
                <div class="josn-data">
                    <div class="title">
                        <div class="left">
                            <div class="title1">
                                <div class="bule-box"></div>
                                <span class="name">试运行</span>
                            </div>
                        </div>
                        <div class="res-box">
                            <div class="name">响应结果</div>
                            <div>
                                <span class="desc" style="margin-right: 2px;">请求状态码: </span>
                                <span class="desc" style="margin-right: 12px;">200</span>
                                <span class="desc" style="margin-right: 2px;">内容类型: </span>
                                <span class="desc">application/json; charset=utf-8</span>
                            </div>
                        </div>
                    </div>
                    <div class="josn-box">
                        <div class="top-name">
                            <span>JSON</span>
                            <i class="el-icon-arrow-up" style="color: #1d2129; font-size: 12px"></i>
                        </div>
                        <div class="json-container">
                            <vue-json-pretty :data="jsonData" :print-width="120" class="json-content"></vue-json-pretty>
                        </div>
                    </div>
                </div>
            </div>
        </el-dialog>

    </div>
</template>

<script>
import { deepCopy } from '../../../../utils/tool';
import VueJsonPretty from "vue-json-pretty";
import "vue-json-pretty/lib/styles.css";
import { runApiKnowledgeApi } from "./../../../../api/Kbm";
import { mcpTestApi } from "./../../../../api/mcp";
import axios from "axios";
export default {
    props: {
        isShowAddMcp: {
            type: Boolean,
            required: true
        },
        editCustomData: {
            type: Object,
            required: true,
            default: () => ({}),
        },
        isAdd: {
            type: Boolean,
            // required: true,
        }
    },
    components: {
        VueJsonPretty,
    },

    data() {
        return {
            customData: {},
            formInfo: {
                name: '',
                desc: '',
            },
            dialogVisible: false,
            requestUrl: "", //请求地址
            selectedAuth: '1', //是否鉴权
            requestType: "0", //请求类型
            requestOptions: [
                {
                    value: "0",
                    label: "GET",
                },
                {
                    value: "1",
                    label: "POST",
                },
            ],
            tabs: ["query", "header", "path", "body"],
            activeIndex: 0, // 默认激活第一个选项卡
            //表格数据
            // tableDataCollection: {
            //     0: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'query' }],
            //     1: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'header' }],
            //     2: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'path' }],
            //     3: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'body' }],
            // },
            tableDataCollection: {
                0: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'query' }],
                1: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'header' }],
                2: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'path' }],
                3: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'body' }],
            },
            formToTabMap: {
                query: 0,
                header: 1,
                path: 2,
                body: 3,
            },
            jsTypes: ['str', 'int', 'bool', 'float', 'None'],
            jsonData: {},
        }
    },
    created() {
        // 确保所有数据都完整初始化
        this.initFromProps()
    },

    watch: {
        editCustomData: {
            handler(newVal) {
                this.requestType = newVal.method;
                this.requestUrl = newVal.url;
                this.customData = { ...newVal }; // 父组件更新时同步
                this.getToolData(newVal);
                this.initTableData(newVal.parameterList);
            },
            deep: true,
        },

        isAdd(newVal) {
            console.log(newVal)
            if (newVal === true) {
                this.customData = {};
                this.requestType = '0';
                this.requestUrl = '';
                this.formInfo.name = '';
                this.formInfo.desc = '';
                this.jsonData = {},
                    this.tableDataCollection = {
                        0: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'query' }],
                        1: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'header' }],
                        2: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'path' }],
                        3: [{ paramName: "", description: "", type: '', defaults: "", required: false, form: 'body' }],
                    }
            }
        },
    },

    computed: {
        localVisible: {
            get() {
                return this.isShowAddMcp
            },
            set(value) {
                this.$emit('update:isShowAddMcp', value)
            }
        },
        // 计算属性获取当前tab的数据
        currentTableData() {
            return this.tableDataCollection[this.activeIndex] || [];
        },

        generatedParams() {
            const result = {
                query: {},
                header: {},
                path: {},
                body: {},
            };

            // 遍历 tableDataCollection 的每个下标（0: Query, 1: Header, 2: Path, 3: Body）
            Object.keys(this.tableDataCollection).forEach((key) => {
                const formType = this.tableDataCollection[key][0]?.form; // 获取 form 类型（Query/Header/Path/Body）
                const params = this.tableDataCollection[key]; // 获取当前 form 类型的所有参数

                // 遍历每个参数，填充到对应的 form 对象中
                params.forEach((param) => {
                    if (param.paramName && param.defaults !== undefined) {
                        result[formType][param.paramName] = param.defaults;
                    }
                });
            });

            return result;
        },
    },

    methods: {
        //初始化数据
        initFromProps() {
            console.log('this.isAdd', this.isAdd)
            if (!this.isAdd) {
                this.customData = { ...this.editCustomData }
                this.requestType = this.customData.method == 'GET' ? '0' : '1'
                this.requestUrl = this.customData.url
            } else {
                console.log('this.requestType111', this.requestType)
                this.customData = {};
                this.requestType = '0';
                this.requestUrl = ''
            }
        },
        initTableData(parameterList) {
            // 重置 tableDataCollection
            const initialCollection = {
                0: [],
                1: [],
                2: [],
                3: [],
            };

            // 判断是否有回显数据
            const hasParameterList = parameterList && parameterList.length > 0;

            //  有回显数据form 分类填充
            if (hasParameterList) {
                parameterList.forEach(item => {
                    const tabIndex = this.formToTabMap[item.form];
                    if (tabIndex !== undefined) {
                        initialCollection[tabIndex].push({
                            ...item,
                            required: item.required === "1",
                        });
                    }
                });
            } else {
                //无回显数据tabs添加默认空行
                initialCollection[0].push(this.getDefaultItem('query'));
                initialCollection[1].push(this.getDefaultItem('header'));
                initialCollection[2].push(this.getDefaultItem('path'));
                initialCollection[3].push(this.getDefaultItem('body'));
            }
            this.tableDataCollection = initialCollection;
        },
        getDefaultItem(form) {
            return {
                paramName: "",
                description: "",
                type: "",
                defaults: "",
                required: false,
                form,
            };
        },
        //回显数据
        getToolData(data) {
            this.formInfo.name = data.functionName;
            this.formInfo.desc = data.description
        },

        handleClose() {
            // this.dialogVisible = false;
        },
        //是否需要鉴权
        selectAuth(type) {
            this.selectedAuth = type;
        },
        //tabs切换
        toggleActive(index) {
            this.activeIndex = index;
        },
        handleInputChange(row, prop, value) {
            // 这里可以添加输入验证逻辑
            console.log("修改项:", prop, "新值:", value);
        },

        // 删除
        deleteRow(index) {
            this.tableDataCollection[this.activeIndex].splice(index, 1);
        },
        //添加参数
        addRow() {
            const newIndex = this.tableDataCollection[this.activeIndex].length;
            this.$set(this.tableDataCollection[this.activeIndex], newIndex, {
                paramName: "",
                description: "",
                type: '',
                defaults: "",
                required: false,
                form: this.tabs[this.activeIndex], // 根据 activeIndex 设置 form
                url: this.requestUrl
            });
        },

        //编辑API
        editApi() {
            if (this.formInfo.name == '') {
                this.$message.error('请填写API名称');
                return;
            }
            if (this.formInfo.desc == '') {
                this.$message.error('请填写API描述');
                return;
            }
            this.dialogVisible = true
        },

        cancelInfo() {
            this.$emit('submitData', false);
        },

        //携带api信息返回
        sumitApiInfo() {
			const extractedData = Object.values(this.tableDataCollection)
			    .flat()
			    .filter(item => item.paramName || item.description || item.type)
			    .map(item => ({
			        ...item,
			        required: item.required === true ? '1' : '0',
			    }));
            if (this.isAdd) {
				this.customData = {
				    url: this.requestUrl,
				    method: this.requestType == 0 ? "GET" : "POST",
				    parameterList: extractedData,
				    headers: '',
				    functionName: this.formInfo.name,
				    description: this.formInfo.desc
				}
                this.$emit('submitData', this.customData);
            } else {
               
                const updatedData = {
                    ...this.customData,
                    url: this.requestUrl,
                    method: this.requestType == 0 ? "GET" : "POST",
                    parameterList: extractedData,
                    functionName: this.formInfo.name,
                    description: this.formInfo.desc,
                };
                this.$emit('submitData', updatedData);
            }
        },

        //调试API
        async runApi() {
            const parmas = {
                url: `${this.requestUrl}`,
                method: this.requestType == "0" ? "GET" : "POST",
                formData: {},
                ...this.generatedParams
            }
            const extractedData1 = Object.values(this.tableDataCollection)
                .flat()
                .filter(item => item.paramName || item.description || item.type)
                .map(item => ({
                    ...item,
                    required: item.required === true ? '1' : '0',
                }));
            try {
                const res = await mcpTestApi(parmas)
                if (res.code == '000000') {
                    this.jsonData = res.data;
                    this.customData = {
                        url: this.requestUrl,
                        method: this.requestType == 0 ? "GET" : "POST",
                        parameterList: extractedData1,
                        headers: '',
                        functionName: this.formInfo.name,
                        description: this.formInfo.desc
                    }
                } else {
                    this.$message.error('请检查API参数信息')
                }
            } catch (error) {
                console.log(error)
            }
        },
    }
}
</script>

<style lang="scss" scoped>
.addOrEditMcp {

    .dra-content {
        padding: 0 30px 30px 30px;
        position: relative;
        height: calc(100% - 60px);
        overflow-y: auto;

        .parameter {
            font-size: 16px;
            color: #1D2129;
            margin-top: 30px;

            .edit-api {
                margin-top: 8px;
                border-radius: 4px;
                border: 1px solid #E7E7E7;
                display: flex;
                align-content: center;
                justify-content: center;
                padding: 10px 0;
                font-weight: 400;
                font-size: 14px;
                color: #494C4F;
                cursor: pointer;
            }
        }

        .btn-box {
            display: flex;
            justify-content: end;
            position: absolute;
            right: 30px;
            bottom: 0;
        }


        .api-list {
            margin-top: 24px;
            height: 400px;
			overflow-y: auto;
            .title {
                display: flex;
                align-items: center;

                .bule-box {
                    width: 4px;
                    height: 18px;
                    background: #1747E5;
                    border-radius: 0px 2px 2px 0px;
                    margin-right: 10px;
                }
            }

            .api-item {
                margin-top: 12px;
                padding: 12px;
                border-radius: 4px;
                border: 1px solid #E7E7E7;

                .item-top {
                    font-size: 14px;
                    color: #1D2129;
                    display: flex;
                    align-items: center;

                    .isFlag {
                        padding: 4px;
                        background: #EBEEF2;
                        border-radius: 2px;
                        font-weight: 400;
                        font-size: 12px;
                        color: #1D2129;
                        margin-left: 4px;
                    }
                }

                .item-desc {
                    font-weight: 400;
                    font-size: 12px;
                    color: #86909C;
                    margin-top: 12px;
                }
            }
        }
    }

    .dialog-content {

        .input-api {
            display: flex;
            justify-content: space-between;

            .title {
                display: flex;
                align-items: center;

                .bule-box {
                    width: 4px;
                    height: 18px;
                    background: #1747E5;
                    border-radius: 0px 2px 2px 0px;
                    margin-right: 10px;
                }

                .name {
                    font-size: 18px;
                    color: #1D2129;
                }
            }

            .authentication {
                display: flex;
                align-items: var();
                font-weight: 400;
                font-size: 14px;
                color: #C9CDD4;
                cursor: pointer;

                .icon-box {
                    display: flex;
                    // align-items: center;
                    margin-right: 6px;
                }
            }
        }

        .request-box {
            display: flex;
            margin-top: 18px;

            .title {
                display: flex;
                align-items: center;

                .bule-box {
                    width: 4px;
                    height: 18px;
                    background: #1747E5;
                    border-radius: 0px 2px 2px 0px;
                    margin-right: 10px;
                }
            }

            .request-type {
                width: 12%;
                height: 40px;
                margin-right: 8px;
            }

            .request-url {
                width: 76%;
                height: 40px;
                margin-right: 8px;
            }

            .run-box {
                padding: 6px 12px 6px 17px;
                display: flex;
                align-items: center;
                justify-content: center;
                background: #1747E5;
                border-radius: 4px;
                font-weight: 400;
                font-size: 14px;
                color: #FFFFFF;
                cursor: pointer;
            }
        }

        .request-info {
            margin-top: 30px;

            .title {
                display: flex;
                align-items: center;
                margin-bottom: 14px;

                .bule-box {
                    width: 4px;
                    height: 18px;
                    background: #1747E5;
                    border-radius: 0px 2px 2px 0px;
                    margin-right: 10px;
                }

                .name {
                    font-weight: 500;
                    font-size: 18px;
                    color: #1D2129;
                }
            }


            .request-config {
                display: flex;
                align-items: center;
                justify-content: space-between;
                height: 32px;
                box-sizing: border-box;
                background: #f7f8fa;
                border-radius: 4px;

                .tab-item {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    width: 25%;
                    height: 28px;
                    cursor: pointer;
                }

                .active {
                    background: #ffffff;
                    box-shadow: 0px 4px 8px 0px rgba(0, 0, 0, 0.1);
                    border-radius: 2px;
                }
            }

            .add-btn {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 124px;
                height: 40px;
                border-radius: 4px;
                border: 1px solid #e7e7e7;
                margin-top: 6px;
                cursor: pointer;
            }
        }

        .josn-data {
            margin-top: 24px;

            .title1 {
                display: flex;
                align-items: center;
                margin-bottom: 14px;

                .bule-box {
                    width: 4px;
                    height: 18px;
                    background: #1747E5;
                    border-radius: 0px 2px 2px 0px;
                    margin-right: 10px;
                }

                .name {
                    font-weight: 500;
                    font-size: 18px;
                    color: #1D2129;
                }
            }

            .title {

                .res-box {
                    display: flex;
                    justify-content: space-between;

                    .name {
                        font-size: 14px;
                        color: #1D2129;
                    }

                    .desc {
                        font-weight: 400;
                        font-size: 12px;
                        color: #86909C;
                    }
                }

                .left {
                    display: flex;
                    align-items: center;

                    h3 {
                        font-size: 18px;
                        color: #1d2129;
                        margin-right: 12px;
                    }

                    .icon-box {
                        margin-right: 12px;
                        width: 60px;
                        height: 24px;
                        background: #e8ffe9;
                        border-radius: 2px;
                        display: flex;
                        justify-content: center;
                        align-items: center;

                        span {
                            font-weight: 400;
                            font-size: 12px;
                            color: #009a29;
                            margin-left: 4px;
                        }
                    }

                    .status {
                        font-weight: 400;
                        font-size: 12px;
                        color: #86909c;
                        margin-right: 12px;
                    }

                    .code {
                        font-weight: 400;
                        font-size: 12px;
                        color: #1d2129;
                        margin-right: 20px;
                    }
                }

                .right {
                    width: 76px;
                    height: 32px;
                    border-radius: 4px;
                    border: 1px solid #1747e5;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;

                    span {
                        font-weight: 400;
                        font-size: 14px;
                        color: #1747e5;
                        margin-left: 6px;
                    }
                }
            }

            .josn-box {
                margin-top: 12px;

                .top-name {
                    background: #f2f3f5;
                    border-radius: 3px 3px 0px 0px;
                    height: 47px;
                    display: flex;
                    align-items: center;

                    span {
                        margin-left: 15px;
                        font-size: 14px;
                        color: #1d2129;
                        margin-right: 4px;
                    }
                }

                .json-container {
                    width: 100%;
                    height: 280px;
                    /* 固定高度 */
                    border: 1px solid #ebeef5;
                    border-radius: 4px;
                    overflow-y: auto;
                    /* 垂直滚动 */
                    padding: 15px;
                    background-color: #f2f3f5;
                    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
                }
            }
        }
    }

    ::v-deep .el-form-item__label {
        font-size: 14px;
        color: #1D2129;
    }

    ::v-deep .el-dialog {
        padding: 0;
        margin: 0;
        margin-left: calc(13% - 10px);
    }

    ::v-deep .full-height-dialog {
        display: flex;
        flex-direction: column;
        max-height: calc(100vh - 20px) !important;
        /* 减去一些边距 */
        min-height: 100vh !important;
        margin-top: 0 !important;
        margin-bottom: 0 !important;
        top: 0 !important;
        overflow-y: auto;
    }

    :v-deep .custom-dialog {
        z-index: 2 !important;
    }
}
</style>