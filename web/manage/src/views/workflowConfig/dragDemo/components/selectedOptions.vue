<template>
  <div>
    <el-select
      v-model="selectedOptions"
      popper-class="cus-select"
      :multiple = "multiple"
      allow-create
      default-first-option
      @change="onChangeVoidValue"
      :placeholder="placeholder"
      style="width: 100%;"
    >
      <el-option
        v-for="(item, index) in allOptions"
        :key="index"
        :label="item.label"
        :value="item.value"
      >
        <span style="float: left">{{ item.label }}</span>
        <el-button
          v-if="item.custom"
          type="text"
          size="small"
          @click.stop="deleteCustomOption(index)"
          style="float: right; color: red;"
        >
        <iconpark-icon name="delete-bin-4-line"></iconpark-icon>
        </el-button>
      </el-option>
      <!-- 自定义添加选项 -->
      <el-option
        v-if="!showAddOption"
        disabled
        class="add-option"
        value=""
      >
        <el-button type="text" @click="showAddOption = true"><iconpark-icon class="icon" color="#409EFF" name="add-line"></iconpark-icon>
        <span style="color: #409EFF;">自定义</span>
        </el-button>
      </el-option>
      <el-option
        v-else
        disabled
        class="add-option"
        value=""
      >
        <el-input
          v-model="newOptionLabel"
          placeholder="请输入新选项"
          size="mini "
          style="width: 320px;margin-right: 10px;"
          maxlength="20"
          show-word-limit
          @keyup.enter.native="addOption"
          @blur="inputBlur"
        >
        </el-input>
        <span>
            <el-button type="text" @click="cancelAddOption" style="color: #666;">取消</el-button>
            <el-button type="text" @click="addOption" >确定</el-button>
        </span>
      </el-option>
    </el-select>
  </div>
</template>

<script>
export default {
  props: {
    multiple: {
      type: Boolean,
      default: () => true,
    },
    model: {
      type: Number,
      default: () => 1,
    },
    placeholder: {
      type: String,
      default: () => '请选择或添加选项',
    },
    delimiter: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      selectedOptions: null,
      allOptions: [
        { label: '换行(\\n)', value: '\n' },
        { label: '制表符(\\t)', value: '\t' },
        { label: '句号(。)', value: '。' },
        { label: '逗号(，)', value: '，' },
        { label: '分号(；)', value: '；' },
        { label: '空格( )', value: ' ' },
        // 其他默认选项...
      ],
      showAddOption: false,
      newOptionLabel: '',
    };
  },
  watch: {
    model: {
            handler(newVal, oldVal) {
              this.selectedOptions = this.model === 2 ? this.delimiter : this.delimiter[0] ? this.delimiter[0] : '';
            },
            deep: true,
        },
    },
  mounted() {
    this.selectedOptions = this.model === 2 ? this.delimiter : this.delimiter[0] ? this.delimiter[0] : '';
  },
  methods: {
    inputBlur() {
      this.$EventBus.$emit("saveWorkflow");
    },
    onChangeVoidValue() {
      let arr = typeof this.selectedOptions === 'string' ? [this.selectedOptions] : this.selectedOptions;
      this.$emit(
          "setSelected",
          arr
      );
      this.$EventBus.$emit("saveWorkflow");
    },
    addOption() {
      if (this.newOptionLabel.trim()) {
        const newOption = {
          label: this.newOptionLabel,
          value: this.newOptionLabel,
          custom: true,
        };
        this.allOptions.push(newOption);
        this.multiple && this.selectedOptions.push(newOption.value);
        this.cancelAddOption();
      }
    },
    cancelAddOption() {
      this.showAddOption = false;
      this.newOptionLabel = '';
    },
    deleteCustomOption(index) {
      const optionToDelete = this.allOptions[index];
      this.allOptions.splice(index, 1);
      const selectedIndex = this.selectedOptions.indexOf(optionToDelete.value);
      if (selectedIndex !== -1) {
        this.selectedOptions.splice(selectedIndex, 1);
      }
    },
  },
};
</script>

<style scoped lang="scss">
.add-option {
  font-size: 14px;
  color: #409EFF;
  height: 36px;
  .icon{
    position: relative;
    top: 2px;
    margin-right: 4px;
  }
}
.el-input__suffix {
  display: flex;
  align-items: center;
}
</style>