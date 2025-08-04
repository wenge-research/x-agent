<template>
  <div class="txt-preview">
    <pre>{{ fileContent }}</pre>
  </div>
</template>

<script>
import iconv from 'iconv-lite';

export default {
  name: 'previewTxt',
  props: {
    fileUrl: {
      type: String,
      required: true
    },
    fileName: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      fileContent: ''
    };
  },
  mounted() {
    this.loadTxtFile();
  },
  methods: {
    async loadTxtFile() {
      try {
        const response = await fetch(this.fileUrl);
        const arrayBuffer = await response.arrayBuffer();

        // 尝试多种编码解码
        let text = this.tryDecodeText(arrayBuffer);
        this.fileContent = text;
      } catch (error) {
        this.fileContent = '无法加载文本文件内容。';
        console.error('加载TXT文件失败:', error);
      }
    },

    // 尝试多种编码解码文本
    tryDecodeText(arrayBuffer) {
      // 首先尝试 UTF-8
      let text = this.decodeWithUTF8(arrayBuffer);
      if (text && !this.hasInvalidChars(text)) {
        return text;
      }

      // 如果 UTF-8 失败或包含乱码，尝试 GBK
      text = this.decodeWithGBK(arrayBuffer);
      if (text && !this.hasInvalidChars(text)) {
        return text;
      }

      // 如果 GBK 也失败，返回 UTF-8 的结果（可能带有乱码，但至少有内容）
      return this.decodeWithUTF8(arrayBuffer);
    },

    // 使用 TextDecoder 解码 UTF-8
    decodeWithUTF8(arrayBuffer) {
      try {
        const decoder = new TextDecoder('utf-8');
        return decoder.decode(arrayBuffer);
      } catch (e) {
        console.warn('使用 UTF-8 解码失败。');
        return ''; // 解码失败返回空字符串
      }
    },

    // 使用 iconv-lite 解码 GBK
    decodeWithGBK(arrayBuffer) {
      try {
        // 将 ArrayBuffer 转换为 Buffer
        const buffer = Buffer.from(arrayBuffer);
        // 使用 iconv-lite 解码 GBK
        return iconv.decode(buffer, 'gbk');
      } catch (e) {
        console.warn('使用 GBK 解码失败。');
        return ''; // 解码失败返回空字符串
      }
    },

    // 更准确的乱码检测
    hasInvalidChars(text) {
      if (!text) return true;
      
      // 统计特殊字符的比例
      const totalChars = text.length;
      if (totalChars === 0) return false;
      
      // 统计替换字符（通常是乱码的标志）
      const replacementCharCount = (text.match(/\uFFFD/g) || []).length;
      
      // 统计连续问号
      const multipleQuestionMarkCount = (text.match(/\?{3,}/g) || []).length;
      
      // 如果替换字符超过总字符的10%，或者有多个连续问号，则认为是乱码
      return (replacementCharCount / totalChars) > 0.1 || multipleQuestionMarkCount > 0;
    }
  }
};
</script>

<style scoped>
.txt-preview {
  width: 100%;
  height: 100%;
  padding: 20px;
  overflow: auto;
  white-space: pre-wrap;
  background-color: #f9f9f9;
  border: 1px solid #eaeaea;
  font-family: monospace;
}
</style>