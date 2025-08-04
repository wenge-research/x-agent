<template>
    <div class="api-setting">
        <div class="api-setting-url">
            <el-select
                v-model="request.method"
                slot="prepend"
                style="width: 100px"
                :placeholder="$t('pleaseSelect')"
            >
                <el-option label="POST" :value="'POST'">POST</el-option>
                <el-option label="GET" :value="'GET'">GET</el-option>
            </el-select>
            <el-input
                :placeholder="$t('pleaseEnterContent')"
                v-model="request.url"
                class="input-with-select" style="margin-left: 12px;height: 40px;line-height: 40px;"
            >
            </el-input>
            <el-button slot="append" @click="sendRequest" style="background-color: #1747E5;color: #fff;border-radius:2px;margin-left:12px;">{{$t('update')}}</el-button>
        </div>
        <div class="api-setting-request">
            <div class="api-setting-request-top">
                <div class="radio-list">
                    <span
                        class="radio-list-left"
                        :class="{ active: runType === '1' }"
                        @click="runType = '1'"
                        >{{$t('request')}}</span
                    >
                    <span
                        class="radio-list-right"
                        :class="{ active: runType === '2' }"
                        @click="runType = '2'"
                        >{{$t('response')}}</span
                    >
                </div>
                <div class="tab-list"  v-if="runType === '1'">
                    <!-- <span
                        :class="{ active: tabNum === '1' }"
                        @click="tabNum = '1'"
                        >Params({{ parameters.tab1.length }})</span
                    >
                    <span
                        :class="{ active: tabNum === '2' }"
                        @click="tabNum = '2'"
                        >Headers({{ parameters.tab2.length }})</span
                    > -->
                    <el-select @change="typeRequestChange"
                        v-model="typeRequest"
                        slot="prepend"
                        :placeholder="$t('pleaseSelect')"
                    >
                        <el-option :label="'Params'+`${parameters.tab1.length}`" :value="'1'">Params({{ parameters.tab1.length }})</el-option>
                        <el-option :label="'Headers'+`${parameters.tab2.length}`" :value="'2'">Headers({{ parameters.tab2.length }})</el-option>
                    </el-select>
                    <!-- <span
                        :class="{ active: tabNum === '3' }"
                        @click="tabNum = '3'"
                        >Authorization</span
                    >
                    <span
                        :class="{ active: tabNum === '4' }"
                        @click="tabNum = '4'"
                        >Body</span
                    > -->
                </div>
            </div>

            <dynamic-params
                :parameters="parameters.tab1"
                :hasValue="true"
                v-if="runType === '1' && tabNum === '1'"
            ></dynamic-params>
            <dynamic-params
                :parameters="parameters.tab2"
                :hasValue="true"
                v-if="runType === '1' && tabNum === '2'"
            ></dynamic-params>
            <dynamic-params :parameters="parameters.responseTab" v-if="runType === '2'"></dynamic-params>
        </div>
        <!-- <div class="api-setting-response">
            <div class="api-setting-response-tit">{{$t('responseResult')}}验证</div>
            <div class="api-setting-response-bd">
                <div class="api-setting-response-bd-left">
                    <p style="color:#494E57;font-size:14px;font-weight: 500;">Response    
                        <strong style="color:#494E57;padding-left: 10px;font-weight: 400;">Headers</strong>
                        <span>
                            <span>
                                <img style="width: 14px;height: 14px;margin-right: 5px;vertical-align: bottom;" src="@/assets/images/terminal-box-line.svg" alt="">
                            </span>{{$t('parseToNodeOutputParams')}}
                        </span>
                    </p>
                    <vue-json-pretty :showNum="false" :data="jsonData" :key="jsonKey" />
                </div>
                <div class="api-setting-response-bd-right">
                    <p style="border-left: none;color:#494E57;font-size:14px;font-weight: 500;">{{$t('nodeOutputParams')}}</p>
                    <vue-json-pretty :showNum="false" :data="jsonData" :key="jsonKey" />
                </div>
            </div>
        </div> -->
    </div>
</template>

<script>
import ParamList from "./ParamList.vue";
import DynamicParams from "./DynamicParams.vue";
import { apiValidate } from "@/api/workflow";
import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';
export default {
    props: {
        nodeData: Object,
    },
    components: {
        ParamList,
        DynamicParams,
        VueJsonPretty
    },
    data() {
        return {
            parameters: {
                tab1: [{ name: "", value: "", type: "", children: [] }],
                tab2: [{ name: "", value: "", type: "" }],
                responseTab: [{ name: "", value: "", type: "", children: []  }],
            },
            jsonData: {},
            runType: "1",
            jsonKey: 1,
            tabNum: "1",
            request: {},
            typeRequest: '1'
        };
    },
    mounted() {
        let noedeData = JSON.parse(JSON.stringify(this.nodeData));
        this.request = noedeData?.settings;
        this.parameters.tab2 = this.request.headers ? this.request.headers : [];
        this.parameters.tab1 = this.request.requestBody ? this.request.requestBody : [];
        this.parameters.responseTab = this.request.responseBody ? this.request.responseBody : [];
        this.$EventBus.$on("apiValidate", (res) =>{
            this.jsonData = JSON.parse(res.data);
            this.jsonKey++
        });
        //this.sendRequest();
    },
    methods: {
        sendRequest() {
            this.$emit("send-request", this.request);
        },
        typeRequestChange(item) {
            if(item === '1') {
                this.tabNum = '1'
            }else if (item === '2') {
                this.tabNum = '2'
            }
        }
    },
};
</script>
<style lang="scss" scoped>

.api-setting-url {
    display: flex;
    padding: 0 20px;
    .el-input-group__append button.el-button {
        background: #1c50fd;
        color: #fff;
        height: 36px;
    }
    .el-input--medium .el-input__inner {
        height: 40px;
        line-height: 40px;
    }
}
.api-setting-request-top {
    // display: flex;
    padding: 0 20px;
}
.radio-list {
    margin: 20px 0 0 0;
    // width: 180px;
    position: relative;
    right: 6px;
    // padding: 0 20px;

    span {
        cursor: pointer;
        font-size: 14px;
        color: #828894;
        background: #f2f5fa;
        border-radius: 4px;
        display: inline-block;
        width: 50%;
        text-align: center;
        height: 36px;
        line-height: 36px;
        position: relative;
        &.active {
            background: #fff;
            z-index: 1;
            color: #494E57;
            background: #FFFFFF;
            box-shadow: 0px 4px 8px 0px rgba(0,0,0,0.1);
            border-radius: 2px;
        }
    }
    &-left {
        left: 6px;
    }
    &-right {
        right: 6px;
    }
}
.tab-list {
    flex: auto;
    // border-bottom: 1px solid rgba(0, 0, 0, 0.12);
    padding: 28px 20px 0 0;
    span {
        margin: 0 20px 0 0;
        cursor: pointer;
        &.active {
            color: #1c50fd;
            border-bottom: 2px solid #1c50fd;
            display: inline-block;
            padding: 0 0 10px 0;
        }
    }
}
.api-setting-response {
    height: 300px;
    width: 100%;
    bottom: 18px;
    position: absolute;
    &-tit {
        padding: 10px 20px;
        border: 1px solid #eee;
        border-bottom: none;
    }
    &-bd {
        display: flex;
        p {
            font-size: 16px;
            padding: 10px 20px;
            color: #383d47;
            border: 1px solid #eee;
            border-bottom: none;
            position: relative;
            & > span {
                // position: absolute;
                // right: 10px;
                float: right;
                font-size: 14px;
                // color: #1c50fd;
                cursor: pointer;
            }
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
        &-left {
            flex: 1;
        }
        &-right {
            flex: 1;
        }
    }
}
</style>
