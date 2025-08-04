<template>
  <div class="Ec-x6-icon">
    <el-drawer
      :visible.sync="visible"
      :direction="direction"
      :modal="false"
      :modal-append-to-body="false"
      size="212px"
      style="position: absolute; width: 212px; box-sizing: border-box"
      :show-close="false"
    >
      <div class="drawer-header">
        <h3>测试</h3>
        <i class="el-icon-d-arrow-left" @click="closeDrawer"></i>
      </div>
    </el-drawer>
  </div>
</template>

<script>

export default {
  name: "testDrawer",

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
  
  },
};
</script>

