<template>
    <div style="height: 100%; overflow: hidden">
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
                ></abilitySetting>
            </el-dialog>
        </div>
</template>
<script>
import abilitySetting from "@/views/workflowConfig/components/abilitySetting.vue";
export default {
    components: {
        abilitySetting,
    },
   
    data() {
        return {
          dialogVisible: false,
          componentId: "",
        };
    },
    created() {
        // 复制工作流自动打开编辑功能
        if (this.$route?.query?.componentId) {
            this.componentId = this.$route?.query?.componentId;
            this.dialogVisible = true;
        }
    },
    methods: {
    },
};
</script>
<style lang="scss" scoped src="@/assets/scss/dropdown.scss"></style>

<style lang="scss" src="@/views/appManage/commonList.scss" scoped></style>
<style lang="scss" scoped>
  ::v-deep.flexDialog .el-dialog__body{
    padding: 30px 0 0 0;
  }
  </style>
