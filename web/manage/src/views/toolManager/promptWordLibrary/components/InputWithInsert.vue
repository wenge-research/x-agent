<template>
  <div>
    <textarea
      ref="textarea"
      v-model="inputText"
      @click="updateCursorPosition"
      @input="updateCursorPosition"
    ></textarea>
    <button @click="handleInsertVariable">插入变量</button>
    <div
      v-if="showDropdown"
      :style="dropdownStyle"
      class="dropdown"
    >
      <select
        v-model="selectedVariable"
        @change="insertVariable"
        @blur="showDropdown = false"
        ref="dropdown"
      >
        <option value="" disabled>选择变量</option>
        <option v-for="(variable, index) in variables" :key="index" :value="variable">
          {{ variable }}
        </option>
      </select>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      inputText: '', // 输入框的内容
      cursorPosition: 0, // 光标位置
      selectedVariable: '', // 当前选中的变量
      variables: ['{{name}}', '{{age}}', '{{email}}', '{{address}}'], // 变量列表
      showDropdown: false, // 是否显示下拉菜单
      dropdownStyle: {}, // 下拉菜单的样式
    };
  },
  methods: {
    // 更新光标位置
    updateCursorPosition() {
      this.cursorPosition = this.$refs.textarea.selectionStart;
    },
    // 插入变量
    insertVariable() {
      if (this.selectedVariable) {
        const before = this.inputText.slice(0, this.cursorPosition);
        const after = this.inputText.slice(this.cursorPosition);
        this.inputText = before + this.selectedVariable + after;
        this.cursorPosition += this.selectedVariable.length;
        this.$nextTick(() => {
          this.$refs.textarea.setSelectionRange(this.cursorPosition, this.cursorPosition);
        });
        this.selectedVariable = ''; // 清空选中项
        this.showDropdown = false; // 隐藏下拉菜单
      }
    },
    // 计算下拉菜单的位置
    calculateDropdownPosition() {
      const textarea = this.$refs.textarea;
      const rect = textarea.getBoundingClientRect();
      const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
      const scrollLeft = window.pageXOffset || document.documentElement.scrollLeft;

      // 获取光标的像素位置
      const cursorPos = this.getCursorPixelPosition(textarea, this.cursorPosition);

      this.dropdownStyle = {
        position: 'absolute',
        top: `${rect.top + scrollTop + cursorPos.top + 20}px`, // 20px 是偏移量，避免遮挡光标
        left: `${rect.left + scrollLeft + cursorPos.left}px`,
      };
    },
    // 获取光标的像素位置
    getCursorPixelPosition(textarea, cursorIndex) {
      // 创建一个临时的 span 元素来测量光标位置
      const span = document.createElement('span');
      span.textContent = textarea.value.slice(0, cursorIndex).replace(/\n/g, '\u200B'); // 处理换行符
      span.style.whiteSpace = 'pre-wrap'; // 保持换行和空格
      span.style.visibility = 'hidden'; // 隐藏 span
      span.style.position = 'absolute'; // 绝对定位
      document.body.appendChild(span);

      // 获取 span 的位置
      const rect = span.getBoundingClientRect();
      document.body.removeChild(span); // 移除 span

      return {
        top: rect.height,
        left: rect.width,
      };
    },
    // 处理插入变量按钮点击
    handleInsertVariable() {
      this.updateCursorPosition(); // 立即更新光标位置
      this.showDropdown = true; // 显示下拉菜单
      this.$nextTick(() => {
        this.calculateDropdownPosition(); // 计算下拉菜单位置
        this.$refs.dropdown.focus(); // 聚焦到下拉菜单
      });
    },
  },
};
</script>

<style scoped>
textarea {
  width: 100%;
  height: 100px;
  margin-bottom: 10px;
}

button {
  padding: 5px 10px;
  font-size: 14px;
  cursor: pointer;
}

.dropdown {
  z-index: 1000; /* 确保下拉菜单在最上层 */
}

select {
  padding: 5px;
  font-size: 14px;
}
</style>