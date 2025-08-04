<template>
  <el-dialog append-to-body :title="itemDetail.label" :visible.sync="visible" width="300px">
    <section>
      <el-radio v-model="bool" :label="true">{{$t('trueCondition')}}</el-radio>
      <el-radio v-model="bool" :label="false">{{$t('falseCondition')}}</el-radio>
    </section>
    <footer class="footer">
      <el-button type="primary" @click="submit">{{$t('confirm')}}</el-button>
    </footer>
  </el-dialog>
</template>

<script>
export default {
  name: "ConditionDia",

  data() {
    return {
      visible: false,
      bool: true,
      itemDetail: {},
      edge: "",
    };
  },
  mounted() {},
  methods: {
    init(item, edge) {
      this.itemDetail = item;
      this.edge = edge;
    },
    submit() {
      if (this.bool) {
        if (this.itemDetail.data.t) {
          this.$parent.graph.removeEdge(this.itemDetail.data.t);
        }
        this.itemDetail.data.t = this.edge.id;

        this.edge.appendLabel({
          attrs: {
            text: {
              text: "true",
            },
          },
        });
      } else {
        if (this.itemDetail.data.f) {
          this.$parent.graph.removeEdge(this.itemDetail.data.f);
        }
        this.itemDetail.data.f = this.edge.id;
        this.edge.appendLabel({
          attrs: {
            text: {
              text: "false",
            },
          },
        });
      }

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
