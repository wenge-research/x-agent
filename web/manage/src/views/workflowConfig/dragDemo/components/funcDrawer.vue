<template>
  <div class="Ec-x6-icon">
    <el-drawer
      :visible.sync="visible"
      :direction="direction"
      :modal="false"
      :modal-append-to-body="false"
      size="auto"
      style="position: absolute;  box-sizing: border-box;background: transparent;"
      :show-close="false"
    >
    <div class="setting-close" @click.stop="visible=false">
       <iconpark-icon name="close-large-line" color="#1d2129" size="18"></iconpark-icon>
    </div>
      <!-- <div class="drawer-header">
        <h3>功能</h3>
        <i class="el-icon-d-arrow-left" @click="closeDrawer"></i>
      </div> -->
      <settingDia
        @configCloseDialog="configCloseDialog"
        ref="settingDia"
        :appConfigForm="component?.applicationInfo"
        @refreshAppList="refreshAppList"
      ></settingDia>
    </el-drawer>
  </div>
</template>

<script>
import settingDia from "@/views/workflowConfig/components/settingDia/index.vue";

export default {
  name: "funcDrawer",
  components: {
    settingDia,
  },
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
  watch: {
    component: {
      handler(n) {
        console.log("n=========", n)
      }
    }
  },
  mounted() {
    console.log("component", this.component)
  },
  methods: {
    openDrawer() {
      this.visible = true;
    },
    closeDrawer() {
      this.visible = false;
    },
    configCloseDialog() {},
    refreshAppList() {},
  },
};
</script>
<style lang="scss" scoped>
.Ec-x6-icon ::v-deep .el-drawer__wrapper {
  border-radius: 0;
  left: 58px;
}
.Ec-x6-icon ::v-deep .el-drawer__body {
  overflow-y: auto;
  position: relative;
}
.setting-close{
  width: 32px;
  height: 32px;
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 6666;
  cursor: pointer;
}
//  .Ec-x6-icon ::v-deep .el-drawer {
//   padding: 16px;
// }

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
