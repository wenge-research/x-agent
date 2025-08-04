<template>
  <!-- 运行状态 -->
  <div
    v-if="show"
    class="running-state flex items-center"
    :class="[start ? 'running-start' : 'running-end']"
  >
    <iconpark-icon
      v-if="start"
      name="loader-4-line"
      color="#1C50FD"
      class="start-icon"
    ></iconpark-icon>
	<span v-else>
		<iconpark-icon
		  v-if="isRunningText=='运行成功'"
		  name="checkbox-circle-line"
		  color="#5EC72E"
		  class="end-icon"
		></iconpark-icon>
		<iconpark-icon
		  v-else
		  name="indeterminate-circle-fill"
		  color="#e75a70"
		  class="end-icon"
		></iconpark-icon>
	</span>
   
    <div class="status">{{ start ? "试运行中" : isRunningText }}</div>
	<div v-if="isShowBtn" class="open">
		<div v-if="showBtn" class="open" @click="openResultHandler">
		  {{ isOpen ? "收起结果" : "展开结果" }}
		</div>
	</div>
  </div>
</template>

<script>
export default {
  props: {
    show: {
      type: Boolean,
      default: false,
    },
	isShowBtn: {
	  type: Boolean,
	  default: true,
	},
	isRunningText: {
	  type: String,
	  default: '运行成功',
	},
  },
  data() {
    return {
      start: false,
      isOpen: false,
      showBtn: false
    };
  },
  methods: {
    openResultHandler() {
      this.isOpen = !this.isOpen;
      this.$emit("openResultHandler", this.isOpen);
    },
  },
  mounted() {
    this.$EventBus.$on("apiStarting", () => {
      this.start = true;
    });
    this.$EventBus.$on("apiEnding", (data) => {
      this.showBtn = true;
      this.start = false;
    });
	this.$EventBus.$on("isRunningText", (data) => {
	  this.isRunningText = data;
	});
  },
};
</script>

<style lang="scss" src="./components.scss" scoped></style>