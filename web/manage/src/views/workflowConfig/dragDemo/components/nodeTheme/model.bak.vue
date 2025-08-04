<template>
  <div class="node new-node">
    <div class="content">
      <div class="tit new-tit flex items-center">
        <img
          v-if="label === '知识检索'"
          src="@/assets/svg/gongzuoliujiedian-zhishijiansuo.svg"
        />
        <img
          v-else-if="label === '参数提取器'"
          src="@/assets/svg/gongzuoliujiedian-canshutiqu.svg"
        />
        <img
          v-else-if="label === 'HTTP'"
          src="@/assets/svg/gongzuoliujiedian-HTTP.svg"
        />
        <img
          v-else-if="label === '敏感词拦截'"
          src="@/assets/svg/gongzuoliujiedian-chajianjiedian.svg"
        />
        <img v-else src="@/assets/svg/gongzuoliujiedian-bianliang.svg" />
        <span class="label">
          <span v-show="!editLabel">{{ label }}</span>
          <el-input
            v-show="editLabel"
            v-model="label"
            @blur="inputBlur"
            @input="inputChange"
          />
        </span>
        <span class="icon-outer">
          <iconpark-icon name="play-circle-line"></iconpark-icon>
        </span>

        <el-dropdown @command="handleCommand">
          <span class="icon-outer el-dropdown-link">
            <i class="el-icon-more"></i>
          </span>
          <el-dropdown-menu slot="dropdown" placement="top-end">
            <el-dropdown-item command="rename">重命名</el-dropdown-item>
            <el-dropdown-item command="copy">创建副本</el-dropdown-item>
            <el-dropdown-item command="delete">删除</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div class="model" v-if="modelId">
        <img :src="chatGptIdList[modelId]" />
        <span class="modelName">{{ chatGptNameList[modelId] }}</span>
      </div>
      <ul class="type-list" v-if="userPrompt">
        <li>
          <div class="name">{{ tips }}</div>
          <div class="content" :title="userPrompt">{{ userPrompt }}</div>
        </li>
      </ul>
    </div>
    <i class="el-icon-error" @click.stop="removeNode"></i>
  </div>
</template>

<script>
export default {
  name: "model",
  inject: ["getGraph", "getNode"],
  data() {
    return {
      node: {},
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
      userPrompt: "",
      tips: "",
      label: "",
      editLabel: false,
    };
  },
  mounted() {
    const self = this;
    this.node = this.getNode();
    this.label = this.node.data.label;
    this.modelId = this.node.data.modelId;
    this.userPrompt = this.node.data.userPrompt;
    this.tips = this.node.data.tips;
    // 监听数据改变事件
    this.node.on("change:data", ({ current }) => {
      self.label = current.label;
      self.modelId = current.modelId;
      self.userPrompt = current.userPrompt;
      self.tips = current.tips;
    });
  },
  methods: {
    removeNode() {
      this.getGraph().removeNode(this.node);
      this.$EventBus.$emit("removeNode");
    },
    handleCommand(command) {
      switch (command) {
        case "rename":
          this.editLabel = true;
          break;
        case "copy":
          this.$emit("copy", this.node);
          break;
        case "delete":
          this.removeNode();
          break;

        default:
          break;
      }
    },
    inputBlur() {
      this.editLabel = false;
    },
    inputChange() {
      this.$emit("input", this.label, this.node);
    },
  },
};
</script>
<style lang="scss" src="./node.scss" scoped></style>
