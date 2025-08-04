<template>
    <div class="node" :id="node.id">
        <div>
            <div class="node-content">
                <HeadTool
                    :label="label"
                    @remove="removeHandler"
                    @testNodes="openRunDrawer"
                    :showSub="showSub"
                    :showDraw="true"
                    @hideNode="hideCurNode"
                    @copy="copyHandler"
                    @input="inputChange"
                    imgSuffix="jieshu"
                />
                <div v-show="showSub">
                    <div class="model flex justify-between mb8">
                        <div class="model-label">输出</div>
                        <div class="model-right flex items-center">
                            <ul
                                class="card-list flex items-center flex-wrap"
                                v-if="appForm.inputs && appForm.inputs.length"
                            >
                                <li
                                    v-for="(el, index) in appForm.inputs"
                                    :key="index"
                                    class="name flex items-center"
                                    :title="el.label"
                                >
                                    <iconpark-icon
                                        :name="getIcon(el.type)"
                                        class="icon"
                                        size="14"
                                    ></iconpark-icon>
                                    <span>
                                        {{ el.label }}
                                    </span>
                                </li>
                            </ul>
                            <div v-else class="no-config">未配置输入</div>
                        </div>
                    </div>
                    <!-- <div class="model flex items-center justify-between">
                        <div class="model-label">输出</div>
                        <div class="h-full flex items-center">
                            <div class="no-config">
                                <div class="result-cont">
                                    text<span>string</span>
                                </div>
                            </div>
                        </div>
                    </div> -->
                </div>
            </div>
        </div>
    </div>
</template>

<script>
// mixins
import nodeThemeMixins from "./nodeThemeMixins";
export default {
    name: "onlyIn",

    inject: ["getGraph", "getNode"],
    mixins: [nodeThemeMixins],
    data() {
        return {};
    },
    mounted() {},
    methods: {},
};
</script>
<style lang="scss" src="./common.scss" scoped></style>
