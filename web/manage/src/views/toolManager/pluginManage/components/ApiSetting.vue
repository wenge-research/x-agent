<template>
    <div>
        <!-- <el-button size="small" style="position: absolute;right: 60px;top: 14px;" type="primary" @click="updateApi(true)"
            >{{$t('updateApi')}}</el-button
        > -->
        <request-form
            @send-request="handleSendRequest"
            ref="requestForm"
            :nodeData="nodeData"
        ></request-form>
        <response-display :response="response"></response-display>
    </div>
</template>

<script>
import RequestForm from "./RequestForm.vue";
import ResponseDisplay from "./ResponseDisplay.vue";

export default {
    props: {
        nodeData: Object,
    },
    components: {
        RequestForm,
        ResponseDisplay,
    },
    data() {
        return {
            response: null,
        };
    },
    methods: {
        updateApi(closeDrawerFlag) {
            let urlParams = this.parseUrlQueryParams(this.$refs.requestForm.request.url)
           // let allParams = urlParams.concat(this.$refs.requestForm.parameters.tab1)
            let params = urlParams ? urlParams.reduce((acc, item) => {
                acc[item.name] = item.value;
                return acc;
            }, {}) : {};
           
            this.$refs.requestForm.request.headers = this.$refs.requestForm.parameters.tab2;
            this.$refs.requestForm.request.requestBody = this.$refs.requestForm.parameters.tab1;
            this.$refs.requestForm.request.responseBody = this.$refs.requestForm.parameters.responseTab;
            this.$emit("updateApi", this.$refs.requestForm.request, urlParams, closeDrawerFlag);
        },
         parseUrlQueryParams(url) {
          if(!url) return
          // 解析 URL 查询参数
          const urlParams = new URLSearchParams(new URL(url).search);

          // 将查询参数转换成数组对象的形式
          const queryParams = Array.from(urlParams.entries()).map(([name, value]) => ({
            name,
            value
          }));

          return queryParams;
        },
        convertArrayToJson() {
          this.$refs.requestForm.request.requestBody = this.$refs.requestForm.parameters.tab1.reduce((acc, item) => {
                acc[item.name] = item.value;
                return acc;
            }, {});
        },
        handleSendRequest() {
            this.updateApi(false)
            this.$emit("getApiValidate");
        },
    },
};
</script>
