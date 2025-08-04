<template>
    <div class="node">
        <div class="tit">
            <img src="@/assets/svg/gongzuoliujiedian-bianliang.svg" />
            <span class="label">{{ label }}</span>
        </div>
        <i class="el-icon-error" @click.stop="removeNode"></i>
        <ul>
          <li></li>
        </ul>
        <ul class="type-list">
            <li>
                <div class="content" :title="toolData.componentName">{{ toolData.componentName}}</div>
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    name: "knowledge",
    inject: ["getGraph", "getNode"],
    data() {
        return {
            node: {},
            toolData: {},
            label: "",
        };
    },
    mounted() {
        const self = this;
        this.node = this.getNode();
        this.label = this.node.data.label;
        this.toolData = this.node.data.toolData;
        // 监听数据改变事件
        this.node.on("change:data", ({ current }) => {
            self.label = current.label;
            self.toolData = current.toolData;
            
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
<style lang="scss">
  .know-list {
  li {
      background: #f0f4f9;
      margin: 6px 0;
      padding: 10px 10px 10px 40px;
      line-height: 20px;
      border-radius: 4px;
      position: relative;
      img{
        position: absolute;
        left: 10px;
        width: 20px;
      }
  }
 
  .name {
      font-size: 14px;
      height: 18px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: #333;
  }
}
</style>