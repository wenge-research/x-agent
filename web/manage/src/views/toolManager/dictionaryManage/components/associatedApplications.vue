<template>
  <el-dialog
    :title="$t('associatedApplications')"
    :visible.sync="dialogVisible"
	@close="cancelSubmit"
    width="540px"
    class="dialog dialog-app"
    :close-on-click-modal="false"
        :close-on-press-escape="false"
  >
    <div class="application-list">
      <div v-for="(item, index) in applicationDataList" :key="index" class="application">
        <span
          style="
            width: 48px;
            height: 48px;
            background: #e9edf7;
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 12px;
          "
        >
          <img :src="item.applicationInfo.facadeImageUrl || defaultImage" class="headImg" />
        </span>

        <div class="name">
          {{ item.applicationInfo.applicationName }}
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancelSubmit">{{ $t("close") }}</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  props: {
    dialogVisible: {
      type: Boolean,
      default: false,
    },
    applicationDataList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      defaultImage: require('@/assets/images/applicationlogo.svg'), // 默认图像的URL
    };
  },
  watch: {},
  mounted() {},
  methods: {
    cancelSubmit() {
      this.$emit("closeAppDialog");
    },
  },
};
</script>

<style lang="scss" scoped>
.dialog-app {
  ::v-deep .el-dialog__body {
    max-height: 400px;
    overflow-y: auto;
    padding: 20px !important;
  }
  ::v-deep .el-dialog__footer {
    text-align: left;
  }
}
.application-list {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
}
.application {
  display: flex;
  align-items: center;
  border-radius: 4px;
  border: 1px solid #e1e4eb;
  padding: 12px 16px 12px 12px;
  margin-bottom: 12px;
  .name {
    font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #383d47;
    line-height: 20px;
    width: 154px;
    -webkit-line-clamp: 2; // 设置两行文字溢出
    display: -webkit-box; /** 对象作为伸缩盒子模型显示 **/
    -webkit-box-orient: vertical; /** 设置或检索伸缩盒对象的子元素的排列方式 **/
    overflow: hidden; /** 隐藏超出的内容 **/
  }
}
.headImg {
  width: 48px;
  border-radius: 4px;
}
</style>
