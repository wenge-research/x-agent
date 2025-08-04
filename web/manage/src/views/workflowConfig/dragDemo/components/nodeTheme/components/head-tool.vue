<template>
    <!-- 头部通用功能 -->
    <div class="head-tool flex items-center">
        <!-- <iconpark-icon size="22" v-if="showDraw" class="icon-draw" :class="{'right': showSub}" name="arrow-down-s-line" @click.stop="handleHideNodes"></iconpark-icon> -->
        <svg class="icon" aria-hidden="true">
          <use :xlink:href="`#icon-` + imgSuffix"></use>
        </svg>
        <!-- <img v-if="imgSuffix" :src="require('@/assets/svg/gongzuoliujiedian-'+ imgSuffix +'.svg')" 
        :style="{ width: `${imgWidth}px`, height: `${imgHeight}px` }"
        /> -->
        <span class="label">
            <span v-show="!editLabel" :title="name" @dblclick="handleEditLabel">{{ name }}</span>
            <el-input
                v-show="editLabel"
                v-model="name"
                @blur="inputBlur"
                @input="inputChange"
            />
        </span>
        <el-tooltip
            v-if="imgSuffix !== 'quanjutiaozhuantiaojian' && imgSuffix !== 'yiyouagent' && imgSuffix !== 'agent' && imgSuffix !== 'tiaojianfenzhi' && imgSuffix !== 'kaishi' && imgSuffix !== 'jieshu'"
            class="item"
            effect="dark"
            content="测试该节点"
            placement="top"
        >
            <span class="icon-outer" @click="handleTestNodes">
                <iconpark-icon name="play-circle-line"></iconpark-icon>
            </span>
        </el-tooltip>

        <el-dropdown @command="handleCommand" v-if="imgSuffix !== 'kaishi'">
            <span class="icon-more el-dropdown-link">
                <i class="el-icon-more"></i>
            </span>
            <el-dropdown-menu slot="dropdown" placement="top-end">
                <el-dropdown-item command="rename">重命名</el-dropdown-item>
                <el-dropdown-item command="copy">创建副本</el-dropdown-item>
                <el-dropdown-item command="delete">删除</el-dropdown-item>
                <el-dropdown-item command="detail" v-show="imgSuffix === 'workflow-gongzuoliu'">查看工作流</el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
    </div>
</template>

<script>
export default {
    name: "HeadTool",
    props: {
        label: {
            type: String,
            default: "",
        },
        showSub: {
            type: Boolean,
            default: true,
        },
        showDraw: {
            type: Boolean,
            default: false,
        },
        imgWidth: {
            type: [String, Number],
            default: 18,
        },
        imgHeight: {
            type: [String, Number],
            default: 18,
        },
        imgSuffix: {
            type: String,
            default: "kaishi",
        },
    },
    data() {
        return {
            name: "",
            editLabel: false,
        };
    },
    watch: {
        label: {
            handler(n) {
                //   console.log("是这个n=======", n)
                //   console.log("editLabel=======", this.editLabel)
                this.name = n;
            },
            deep: true,
            immediate: true,
        },
    },
    methods: {
        handleCommand(command) {
            switch (command) {
                case "rename":
                    this.handleEditLabel();
                    break;
                case "copy":
                    this.$emit("copy");
                    // this.$EventBus.$emit("copy");
                    this.$EventBus.$emit("saveWorkflow");
                    break;
                case "delete":
                    this.$emit("remove");
                    this.$EventBus.$emit("saveWorkflow");
                    break;
                case "detail":
                    this.$emit("detail");
                    break;

                default:
                    break;
            }
        },
        inputBlur() {
            this.editLabel = false;
            this.$EventBus.$emit("saveWorkflow");
        },
        inputChange() {
            this.$emit("input", this.name);
            //this.$EventBus.$emit("input", this.name);
        },
        // 测试节点
        handleEditLabel() {
            this.editLabel = true;
            this.$nextTick(() => {
                const inputElement = this.$el.querySelector('.label input');
                if (inputElement) {
                    inputElement.focus();
                }
            });
        },
        // 测试节点
        handleTestNodes() {
            // this.$EventBus.$emit("openRunDrawer");
            this.$emit("testNodes");
        },
        // 测试节点
        handleHideNodes() {
            // this.$EventBus.$emit("openRunDrawer");
            this.$emit("hideNode");
        },
    },
};
</script>

<style lang="scss" src="./components.scss" scoped></style>
