<template>
  <div class="node">
      <div class="tit">
          <img
              src="@/assets/svg/gongzuoliujiedian-HTTP.svg"
          />
          <span class="label">{{ label }}</span>
      </div>
     
      <!-- <i class="el-icon-error" @click.stop="removeNode"></i> -->
  </div>
</template>

<script>
export default {
  name: "http",

  inject: ["getGraph", "getNode"],
  data() {
      return {
          node: {},
          label: "",
      };
  },
  mounted() {
      const self = this;
      this.node = this.getNode();
      this.label = this.node.data.label;
      // 监听数据改变事件
      this.node.on("change:data", ({ current }) => {
          self.label = current.label;
      });
  },
  methods: {
      removeNode() {
          this.getGraph().removeNode(this.node);
          this.$EventBus.$emit("removeNode");
      },
  },
};
</script>
<style lang="scss" src="./node.scss" scoped></style>
