<template>
    <div class="node">
        <div class="tit">
            <img src="@/assets/svg/gongzuoliujiedian-zhishijiansuo.svg" />
            <span class="label">{{ label }}</span>
        </div>
        <i class="el-icon-error" @click.stop="removeNode"></i>
        <div class="node-cont" :id="node.id">
            <ul
                class="know-list"
                v-if="appForm.knowledgeIds && appForm.knowledgeIds.length"
            >
                <li v-for="(el, index) in selectedList" :key="index">
                    <div class="name" :title="el.knowledgeName">
                        <img src="@/assets/svg/zhishiku-moren.svg" />{{
                            el.knowledgeName
                        }}
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import { getKnowledgeInfoList } from "@/api/index";

export default {
    name: "knowledge",
    inject: ["getGraph", "getNode"],
    data() {
        return {
            node: {},
            appForm: {},
            konwlwdgeAllList: [],
            selectedList: [],
            label: "",
        };
    },
    mounted() {
        this.knowledgeAllList();
    },
    methods: {
        removeNode() {
            this.getGraph().removeNode(this.node);
            this.$EventBus.$emit("removeNode");
        },
        init() {
            const self = this;
            this.node = this.getNode();
            this.label = this.node.data.label;
            this.appForm = this.node.data.appForm
                ? JSON.parse(this.node.data.appForm)
                : {};
            if (this.appForm.knowledgeIds) {
                this.selectedList = this.konwlwdgeAllList.filter((el) =>
                    this.appForm.knowledgeIds.includes(el.knowledgeId)
                );
            }
            // 监听数据改变事件
            this.node.on("change:data", ({ current }) => {
                self.label = current.label;
                if (current.appForm) {
                    self.appForm = current.appForm
                        ? JSON.parse(current.appForm)
                        : {};
                    if (self.appForm.knowledgeIds) {
                        self.selectedList = self.konwlwdgeAllList.filter((el) =>
                            self.appForm.knowledgeIds.includes(el.knowledgeId)
                        );
                    }
                }
            });
        },
        knowledgeAllList() {
            getKnowledgeInfoList({
                pageNo: 1,
                pageSize: 99999,
                ownerType: "personalGrant"
            }).then((res) => {
                if (res.code == "000000") {
                    this.konwlwdgeAllList = res.data?.records;
                    this.init();
                } else {
                    this.konwlwdgeAllList = [];
                }
            });
        },
    },
};
</script>
<style lang="scss" src="./node.scss" scoped></style>