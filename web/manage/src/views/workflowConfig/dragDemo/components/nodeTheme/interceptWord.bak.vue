<template>
    <div class="node">
        <div class="tit">
            <img
                src="@/assets/svg/gongzuoliujiedian-chajianjiedian.svg"
            />
            <span class="label">{{ label }}</span>
        </div>
        <i class="el-icon-error" @click.stop="removeNode"></i>
        <div class="node-cont" :id="node.id">
            <ul
                class="know-list"
                v-if="configurations && configurations.length"
            >
                <li v-for="(el, index) in configurations" :key="index">
                    <div class="name" :title="el.name">{{
                            el.name
                        }}
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
export default {
    name: "interceptWord",

    inject: ["getGraph", "getNode"],
    data() {
        return {
            node: {},
            configurations: [],
           
            label: "",
        };
    },
    mounted() {
      const self = this;
        this.node = this.getNode();
        this.label = this.node.data.label;
        this.configurations = this.node.data.configurations && JSON.parse(this.node.data.configurations);
        // 监听数据改变事件
        this.node.on("change:data", ({ current }) => {
            self.label = current.label;
            self.configurations = JSON.parse(current.configurations);
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
