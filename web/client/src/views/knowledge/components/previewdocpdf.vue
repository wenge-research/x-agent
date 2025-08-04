<template>
  <div class="docx-preview-wrap">
    <h1>kaimo test docx-preview</h1>
    <h4>
      <input type="file" id="file" accept=".docx" />
      <button @click="handlePreview">预览</button>
    </h4>
    <div id="bodyContainer"></div>
  </div>
</template>

<script>
import { defaultOptions, renderAsync } from "docx-preview";
console.log(defaultOptions);
export default {
  name: "DocxPreview",
  data() {
    return {
      docxOptions: {
        className: "kaimo-docx-666", // string：默认和文档样式类的类名/前缀
        inWrapper: true, // boolean：启用围绕文档内容的包装器渲染
        ignoreWidth: false, // boolean：禁用页面的渲染宽度
        ignoreHeight: false, // boolean：禁止渲染页面高度
        ignoreFonts: false, // boolean：禁用字体渲染
        breakPages: true, // boolean：在分页符上启用分页
        ignoreLastRenderedPageBreak: true, // boolean：在 lastRenderedPageBreak 元素上禁用分页
        experimental: false, // boolean：启用实验功能（制表符停止计算）
        trimXmlDeclaration: true, // boolean：如果为true，解析前会从​​ xml 文档中移除 xml 声明
        useBase64URL: false, // boolean：如果为true，图片、字体等会转为base 64 URL，否则使用URL.createObjectURL
        useMathMLPolyfill: false, // boolean：包括用于 chrome、edge 等的 MathML polyfill。
        showChanges: false, // boolean：启用文档更改的实验性渲染（插入/删除）
        debug: false, // boolean：启用额外的日志记录
      },
    };
  },
  methods: {
    handlePreview() {
      let file = document.getElementById("file").files[0];
      console.log(file);
      // 将file转为buffer
      let fr = new FileReader();
      fr.readAsArrayBuffer(file);
      fr.addEventListener(
        "loadend",
        (e) => {
          console.log("loadend---->", e);
          let buffer = e.target.result;
          this.docxRender(buffer);
        },
        false
      );
    },
    // 渲染docx
    docxRender(buffer) {
      let bodyContainer = document.getElementById("bodyContainer");
      renderAsync(
        buffer, // Blob | ArrayBuffer | Uint8Array, 可以是 JSZip.loadAsync 支持的任何类型
        bodyContainer, // HTMLElement 渲染文档内容的元素,
        null, // HTMLElement, 用于呈现文档样式、数字、字体的元素。如果为 null，则将使用 bodyContainer。
        this.docxOptions // 配置
      ).then((res) => {
        console.log("res---->", res);
      });
    },
  },
};
</script>
