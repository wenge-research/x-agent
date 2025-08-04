<template>
    <div class="api-setting">
        <div class="api-setting-url">
            <el-input
                :placeholder="$t('pleaseEnterContent')"
                v-model="request.url"
                class="input-with-select"
            >
                <el-select
                    v-model="request.method"
                    slot="prepend"
                    style="width: 100px"
                    :placeholder="$t('pleaseSelect')"
                >
                    <el-option label="POST" :value="'POST'">POST</el-option>
                    <el-option label="GET" :value="'GET'">GET</el-option>
                </el-select>
                <!-- <el-button slot="append" @click="sendRequest">验证</el-button> -->
            </el-input>
        </div>
        <div class="api-setting-request">
            <div class="api-setting-request-top">
                <!-- <div class="radio-list">
                    <span
                        class="radio-list-left"
                        :class="{ active: runType === '1' }"
                        @click="runType = '1'"
                        >请求</span
                    >
                    <span
                        class="radio-list-right"
                        :class="{ active: runType === '2' }"
                        @click="runType = '2'"
                        >响应</span
                    >
                </div> -->
                <div class="tab-list">
                    <span
                        :class="{ active: tabNum === '1' }"
                        @click="tabNum = '1'"
                        >Params({{ parameters.tab1.length }})</span
                    >
                    <span
                        :class="{ active: tabNum === '2' }"
                        @click="tabNum = '2'"
                        >Headers({{ parameters.tab2.length }})</span
                    >
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
                v-if="runType === '1' && tabNum === '1'"
            ></dynamic-params>
            <dynamic-params
                :parameters="parameters.tab2"
                v-if="runType === '1' && tabNum === '2'"
            ></dynamic-params>
            <dynamic-tree-params v-if="runType === '2'"></dynamic-tree-params>
        </div>
       
    </div>
</template>

<script>
import ParamList from "./ParamList.vue";
import DynamicParams from "./DynamicParams.vue";
import DynamicTreeParams from "./DynamicTreeParams.vue";
import { apiValidate } from "@/api/workflow";

export default {
    props: {
        nodeData: Object,
    },
    components: {
        ParamList,
        DynamicParams,
        DynamicTreeParams,
    },
    data() {
        return {
            parameters: {
                tab1: [],
                tab2: [],
            },
            jsonData: {},
            runType: "1",
            jsonKey: 1,
            tabNum: "1",
            request: {},
        };
    },
    mounted() {
        let noedeData = JSON.parse(JSON.stringify(this.nodeData));
        this.request = noedeData?.settings;
        let params = JSON.parse(this.request.requestBody);
        let tempArr = Object.keys(params).map((key) => ({
            name: key,
            value: params[key],
        }));
        let tabs2 = Object.keys(this.request.headers).map((key) => ({
            name: key,
            value: this.request.headers[key],
        }));
        let tabs1 = tempArr.filter(
            (item) => this.request.url.indexOf(item.name) < 0
        );
        this.parameters.tab1 = tabs1.map((item) => {
            if(item.value.indexOf('${') > -1) {
              let val = item.value.slice(2,-1)
              return {
                name: item.name,
                value: val,
                selectedGroup: "1",
              }
            } else {
              return {
                  name: item.name,
                  value: item.value,
                  selectedGroup: "1",
              };
            }
        });
        this.parameters.tab2 = tabs2.map((item) => {
            if(item.value.indexOf('${') > -1) {
              let val = item.value.slice(2,-1)
              return {
                name: item.name,
                value: val,
                selectedGroup: "1",
              }
            } else {
              return {
                  name: item.name,
                  value: item.value,
                  selectedGroup: "1",
              };
            }
        });
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
    },
};
</script>
<style lang="scss" scoped>
.api-setting-url {
    padding: 0 0;
    width: calc(100% - 90px);
    .el-input-group__append button.el-button {
        background: #1c50fd;
        color: #fff;
        height: 36px;
    }
}
.api-setting-request-top {
    display: flex;
    padding: 0 0;
}
.radio-list {
    margin: 20px 0 0 0;
    width: 180px;
    position: relative;
    right: 6px;
    padding: 0 20px;

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
            background: #d1e0fe;
            z-index: 1;
            color: #1c50fd;
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
    border-bottom: 1px solid rgba(0, 0, 0, 0.12);
    padding: 28px 20px 0 0;
    margin: 0 0 0 10px;

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
            span {
                position: absolute;
                right: 10px;
                font-size: 14px;
                color: #1c50fd;
                cursor: pointer;
            }
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
