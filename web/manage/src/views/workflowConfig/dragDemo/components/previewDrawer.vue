<template>
  <div class="Ec-x6-icon">
    <el-drawer title="" :visible.sync="drawerVisible" :modal="false" :modal-append-to-body="false"
      :direction="direction" v-if="drawerVisible" size="550px"
      style="
        position: absolute;
        width: 550px;
        box-sizing: border-box;
        right: 0;
        left: inherit;
          " :show-close="false">
      <div class="drawer-header">
        <img src="@/assets/images/icon-contacts-fill.png" />
        <span>预览调试</span>
        <i class="el-icon-close custom-close-icon" @click="closeDrawer"></i>
      </div>
      <div class="drawer-content" v-loading="updateLoading" style="height:calc(100% - 26px)">
        <PreView ref="previewRef" :params="componentParams.applicationInfo" :type="componentParams.type"
          style="width: 100%;height:100%"></PreView>
      </div>
    </el-drawer>
  </div>
</template>

<script>
  import 'vue-json-pretty/lib/styles.css';
  import PreView from "@/views/appManage/components/preview.vue";
  import
    {
      queryWorkflowDetail,
    } from "@/api/workflow";
  export default {
    props: ["componentParams"],
    name: "runDrawer",
    components: { PreView },
    computed: {

    },
    data ()
    {
      return {
        updateLoading: false,
        drawerVisible: false,
        direction: "rtl",
        appForm: {}
      };
    },
    watch: {

    },
    mounted ()
    {
      console.log(this.componentParams,'那么是这个this.appForm')
      sessionStorage.setItem('nodeInfo',JSON.stringify(this.componentParams))
   console.log(this.appForm,'是来自这里的this.appForm吗？')
    },
    methods: {

      openDrawer ()
      {
        this.drawerVisible = true;
        this.queryWorkflowDetail(this.componentParams.componentId)

      },
      queryWorkflowDetail (componentId)
      {
        this.updateLoading = true
        queryWorkflowDetail({
          componentId: componentId.slice(0, -2),
          type: Number(componentId.slice(-1))
        })
          .then((res) =>
          {
            if (res.code == "000000") {
              this.appForm = res.data.applicationInfo
              this.updateLoading = false
              this.$nextTick(() => {
                // this.$refs.previewRef.render();
              })
            } else {
              this.$message({
                type: "error",
                message: res.msg,
              });
            }
          })
          .catch((err) =>
          {
            this.$message({
              type: "error",
              message: err,
            });
          });
      },
      closeDrawer ()
      {
        this.drawerVisible = false;
      },
    },
  };
</script>
<style scoped lang="scss">
// ::v-deep .el-drawer__title{
//   height: 100%;
// }
  .custom-icon {
    font-size: 24px;
    margin-right: 10px;
  }

  .custom-close-icon {
    font-size: 20px;
    cursor: pointer;
    color: #999;
  }

  .custom-close-icon:hover {
    color: #666;
  }

  .section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    font-size: 16px;
    color: #383d47;
  }

  .Ec-x6-icon ::v-deep .el-drawer__wrapper {
    border-radius: 0;
  }

  .Ec-x6-icon ::v-deep .el-drawer__body {
    overflow-y: auto;
    position: relative;
    overflow-x: hidden;
  }

  .Ec-x6-icon ::v-deep .el-drawer {
    padding: 16px 16px 0 16px;

    .el-form-item {
      margin-bottom: 8px;
    }

    .el-select,
    .el-input-number {
      width: 100%;
    }
  }

  .Ec-x6-icon ::v-deep .el-drawer__header {
    padding: 0;
    margin-bottom: 0;
  }

  .drawer-header {
    position: relative;

    img {
      width: 22px;
    }

    span {
      position: relative;
      bottom: 4px;
      left: 10px;
      font-size: 18px;
      color: #181b49;
      font-weight: bold;
    }

    .custom-close-icon {
      position: absolute;
      right: 0;
      top: 2px;
      cursor: pointer;
    }

    .sub {
      font-size: 14px;
      color: #828894;
      line-height: 18px;
      margin: 10px 0 20px;
      text-align: left;
      font-style: normal;
    }
  }
</style>