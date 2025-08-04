<template>
  <div style="height: 100%;overflow: hidden;">
      <iframe src="https://localhost/gtibook_agent" style="width: 100%;height: 100%;overflow: hidden;" frameborder="0"></iframe>
    </div>
</template>
<script>


  export default {
    components: {

    },
    data ()
    {
      return {

      };
    },
    computed: {

    },
    watch: {

    },
    created ()
    {

    },
  };
</script>
<style>
  .el-tooltip__popper.is-dark {
    max-width: 580px !important;
  }
</style>
<style lang="scss" scoped>
  .outer {
    height: 100%;
    padding: 24px;
    background-color: #fff;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .hideBox ::v-deep .el-upload--picture-card {
      display: none;
    }

    .header {
      width: 100%;
      height: 64px;
      background: #f3f5fb;
      display: flex;
      align-items: center;
      padding: 0 10px;
      flex-shrink: 0;

      .title {
        font-size: 18px;
        font-weight: bold;
      }
    }

    .outerTitle {
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 24px;
      color: #383d47;
      line-height: 40px;
      text-align: left;
      font-style: normal;
      margin-bottom: 24px;
    }
  }
</style>