<template>
  <el-dialog
    :title="node.item ? node.item.data.label : ''"
    :visible.sync="visible"
    width="600px"
  >
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item :label="$t('name')">
        <el-input v-model="label" :placeholder="$t('modifyName')"></el-input>
      </el-form-item>
    </el-form>
    <footer class="footer">
      <el-button type="primary" @click="submit">{{$t('confirm')}}</el-button>
    </footer>
  </el-dialog>
</template>

<script>
export default {
  name: "dialogMysql",

  data() {
    return {
      visible: false,
      bool: true,
      node: {},
      label: "",
    };
  },
  mounted() {},
  methods: {
    init(item) {
      this.node = item;
      this.label = item.item.data.label;
    },
    submit() {
      var node = this.$parent.getNodeById(this.node.item.id);
      node.setData(
        Object.assign({}, this.node.item.data, { label: this.label })
      );
      // this.node.setData({})
      // this.node.item.data.label = this.label;
      this.visible = false;
    },
  },
};
</script>
<style scoped>
section {
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer {
  margin-top: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
