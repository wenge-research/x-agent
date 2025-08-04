<template>
  <div class="Ec-x6-icon">
    <el-drawer
      :visible.sync="visible"
      :direction="direction"
      :modal="false"
      :modal-append-to-body="false"
      size="400px"
      style="position: absolute; width: 400px; box-sizing: border-box"
      :show-close="false"
    >
      <div class="drawer-header">
        <h3>展示</h3>
        <i class="el-icon-d-arrow-left" @click="closeDrawer"></i>
      </div>
      <!-- 展示设置 -->
      <showSetting
        :params="component.applicationInfo"
        @clickConfig="clickConfig"
        @clickConfigParams="clickConfigParams"
      ></showSetting>
    </el-drawer>
  </div>
</template>

<script>
import showSetting from "./showSetting";

export default {
  name: "showDrawer",
  components: { showSetting },
  props: {
    component: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      visible: false,
      direction: "ltr",
    };
  },
  mounted() {
    
  },
  methods: {
    openDrawer() {
      this.visible = true;
    },
    closeDrawer() {
      this.visible = false;
    },
    clickConfigParams() {},
    clickConfig() {},
  },
};
</script>
<style lang="scss" scoped>
.Ec-x6-icon ::v-deep .el-drawer__wrapper {
  border-radius: 0;
  left: 58px;
}
.Ec-x6-icon ::v-deep .el-drawer__body {
  overflow-y: scroll;
  position: relative;
}
.Ec-x6-icon ::v-deep .el-drawer {
  padding: 16px;
}

.Ec-x6-icon ::v-deep .el-drawer__header {
  padding: 0;
  margin-bottom: 0;
}
.drawer-header {
  position: relative;
  h3 {
    font-size: 18px;
    color: #383d47;
  }
  .el-icon-d-arrow-left {
    position: absolute;
    right: 0;
    top: 2px;
    cursor: pointer;
  }
}
</style>
