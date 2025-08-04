<template>
    <div>
        <el-button size="small" style="position: absolute;right: 0px;top: 73px;" type="primary" @click="updateApi(true)"
            >{{$t('updateApi')}}</el-button
        >

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
            let allParams = urlParams.concat(this.$refs.requestForm.parameters.tab1)
            let headerParams = this.$refs.requestForm.parameters.tab2.reduce((acc, item) => {
                acc[item.name] = item.selectedGroup ? '${' + item.value + '}' : item.value;
                return acc;
            }, {});
            let params = allParams.reduce((acc, item) => {
                acc[item.name] = item.selectedGroup ? '${' + item.value + '}' : item.value;
                return acc;
            }, {});
            const resultArray = Object.keys(params).map(key => {
              return { [key]: params[key] };
            });
            this.$refs.requestForm.request.requestBody = resultArray;
            this.$refs.requestForm.request.headers = headerParams;
            this.$emit("updateApi", this.$refs.requestForm.request, allParams, this.$refs.requestForm.parameters.tab2, closeDrawerFlag);
        },
         parseUrlQueryParams(url) {
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
