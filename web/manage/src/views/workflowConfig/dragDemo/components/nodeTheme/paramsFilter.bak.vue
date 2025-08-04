<template>
    <div class="node">
        <div class="tit">
            <img
                src="@/assets/svg/gongzuoliujiedian-canshutiqu.svg"
            />
            <span class="label">{{ label }}</span>
        </div>
        <div class="model" v-if="modelId">
            <img :src="chatGptIdList[modelId]" />
            <span class="modelName">{{ chatGptNameList[modelId] }}</span>
        </div>
        <i class="el-icon-error" @click.stop="removeNode"></i>
        <ul class="type-list">
            <li v-for="(el, index) in filterVariables" :key="index">
                <div class="name">{{ el.name }}</div>
                <div class="content2" :title="el.description">{{ el.description }}</div>
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    name: "paramsFilter",

    inject: ["getGraph", "getNode"],
    data() {
        return {
            node: {},
            filterVariables:[],
            chatGptIdList: {
                f570229417e36672814ff51a695447eb5: require("@/assets/images/zpqy.png"),
                f570229417ef4672814ff51a695447eb5: require("@/assets/images/wxyy.png"),
                f570229417ef4672814ff51a65447eb5: require("@/assets/images/deepseek.png"),
                f570229417ef4d72814ff51a65447eb5: require("@/assets/images/kimi.png"),
                f570229417ef4d79814ff51a65447eb5: require("@/assets/images/yayi.png"),
                e4cc6a8d45f74e6099cae1f56a0fe5e9: require("@/assets/images/doubao.png"),
            },
            chatGptNameList: {
                f570229417e36672814ff51a695447eb5: "智谱清言",
                f570229417ef4672814ff51a695447eb5: "文心一言",
                f570229417ef4672814ff51a65447eb5: "deepseek",
                f570229417ef4d72814ff51a65447eb5: "kimi",
                f570229417ef4d79814ff51a65447eb5: "雅意",
                e4cc6a8d45f74e6099cae1f56a0fe5e9: "豆包",
            },
            modelId: "",
            label: "",
        };
    },
    mounted() {
        const self = this;
        this.node = this.getNode();
        this.label = this.node.data.label;
        this.filterVariables = this.node.data.filterVariables && JSON.parse(this.node.data.filterVariables);
        this.modelId = this.node.data.modelId;
        // 监听数据改变事件
        this.node.on("change:data", ({ current }) => {
            self.label = current.label;
            self.filterVariables = JSON.parse(current.filterVariables);
            self.modelId = current.modelId;
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
