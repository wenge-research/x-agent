<template>
  <div class="result-page">
    <iframe 
      ref="iframe" 
      :srcdoc="iframeContent" 
      style="width: 100%; height: 100%; border: none;"
      @load="onIframeLoad"
    ></iframe>
    
    <!-- 报告生成中的加载状态 -->
    <div v-if="reportLoading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p class="loading-text">页面正在生成中...</p>
    </div>

    <!-- PDF生成中的加载状态 -->
    <div v-if="pdfLoading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p class="loading-text">正在生成PDF...</p>
    </div>
    
    <div v-if="error" class="error-overlay">
      <p class="error-text">PDF生成失败</p>
      <button @click="exportPdf" class="retry-button">重试</button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, defineProps, watch, onMounted, defineExpose } from 'vue';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

const props = defineProps({
  reslultThml: {
    type: String,
    required: true,
    default: ''
  },
});

const iframe = ref<HTMLIFrameElement | null>(null);
const reportLoading = ref(false); // 报告生成中的加载状态
const pdfLoading = ref(false);    // PDF生成中的加载状态
const error = ref(false);
const iframeContent = ref('');

// 添加基础样式和打印样式
const addBaseStyles = (html: string) => {
  const style = `
    <style>
      body {
        margin: 0;
        padding: 20px;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, 
                    Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
        line-height: 1.6;
        color: #333;
        background-color: #fff;
      }
      
      * {
        box-sizing: border-box;
        margin: 0;
        padding: 0;
      }
      
      img, svg {
        max-width: 100%;
        height: auto;
      }
      
      .page-break {
        page-break-after: always;
        break-after: page;
      }
      
      @media print {
        body {
          padding: 0;
          margin: 0;
        }
        
        .no-print, .no-print * {
          display: none !important;
        }
        
        * {
          -webkit-print-color-adjust: exact !important;
          color-adjust: exact !important;
          print-color-adjust: exact !important;
        }
      }
    </style>
  `;
  
  if (!html.includes('<head>')) {
    return html.replace('<html', `<html><head>${style}</head>`);
  }
  
  return html.replace('</head>', `${style}</head>`);
};

const defaultHtml = `
  <!DOCTYPE html>
  <html lang="en">
  <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Loading</title>
      <style>
          body {
              font-family: Arial, sans-serif;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              height: 100vh;
              margin: 0;
              background-color: #ffffff;
          }
          .loading-spinner {
              border: 4px solid rgba(0, 0, 0, 0.1);
              border-radius: 50%;
              border-top: 4px solid #3498db;
              width: 40px;
              height: 40px;
              animation: spin 1s linear infinite;
              margin-bottom: 16px;
          }
          .loading-text {
              color: #666;
              font-size: 16px;
          }
          @keyframes spin {
              0% { transform: rotate(0deg); }
              100% { transform: rotate(360deg); }
          }
      </style>
  </head>
  <body>
      <div class="loading-spinner"></div>
      <p class="loading-text">报告生成中......</p>
  </body>
  </html>
`;

watch(() => props.reslultThml, (newVal) => {
  if (newVal) {
    reportLoading.value = true;
    error.value = false;

    let processedHtml = newVal
      .replace(/^```html\s*|\s*```$/g, '')
      .trim();

    iframeContent.value = addBaseStyles(processedHtml);
  } else {
    iframeContent.value = defaultHtml;
  }
}, { immediate: true });

const onIframeLoad = () => {
  reportLoading.value = false;
};

onMounted(() => {
  if (!props.reslultThml || props.reslultThml === '') {
    iframeContent.value = defaultHtml;
  }
});

// 预加载图片
const preloadImages = async (doc: Document) => {
  const images = Array.from(doc.images);
  await Promise.all(images.map(img => {
    if (!img.complete) {
      return new Promise((resolve, reject) => {
        img.onload = resolve;
        img.onerror = reject;
      });
    }
    return Promise.resolve();
  }));
};
//html2canvas导出
const html2canvasExportPdf = async () => {
  if (!iframe.value) {
    error.value = true;
    return;
  }

  const iframeWindow = iframe.value.contentWindow;
  if (!iframeWindow) {
    error.value = true;
    return;
  }

  try {
    pdfLoading.value = true; // 显示PDF生成中的加载状态
    error.value = false;
    
    // 等待内容加载
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    const doc = iframeWindow.document;
    const body = doc.body;
    const html = doc.documentElement;
    
    // 预加载图片
    await preloadImages(doc);
    
    // 计算完整内容尺寸
    const width = Math.max(
      body.scrollWidth,
      body.offsetWidth,
      html.clientWidth,
      html.scrollWidth,
      html.offsetWidth
    );
    
    const height = Math.max(
      body.scrollHeight,
      body.offsetHeight,
      html.clientHeight,
      html.scrollHeight,
      html.offsetHeight
    );
    
    // 临时调整iframe和body样式
    const originalOverflow = body.style.overflow;
    const originalIframeWidth = iframe.value.style.width;
    const originalIframeHeight = iframe.value.style.height;
    
    body.style.overflow = 'visible';
    iframe.value.style.width = `${width}px`;
    iframe.value.style.height = `${height}px`;
    
    // 等待布局更新
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 使用html2canvas截图
    const canvas = await html2canvas(body, {
      scale: 2,
      useCORS: true,
      allowTaint: true,
      logging: false,
      windowWidth: width,
      windowHeight: height,
      scrollX: 0,
      scrollY: 0,
      backgroundColor: '#FFFFFF',
      foreignObjectRendering: false,
      ignoreElements: (element) => {
        return element.classList.contains('no-print');
      }
    });
    
    // 恢复原始样式
    body.style.overflow = originalOverflow;
    iframe.value.style.width = originalIframeWidth;
    iframe.value.style.height = originalIframeHeight;
    
    // 创建PDF
    const pdf = new jsPDF({
      unit: 'mm',
      format: 'a4',
      orientation: canvas.width > canvas.height ? 'landscape' : 'portrait'
    });
    
    const imgData = canvas.toDataURL('image/jpeg', 1.0);
    const pdfWidth = pdf.internal.pageSize.getWidth();
    const pdfHeight = (canvas.height * pdfWidth) / canvas.width;
    
    // 分页处理
    let heightLeft = pdfHeight;
    let position = 0;
    const pageHeight = pdf.internal.pageSize.getHeight();
    
    if (heightLeft < pageHeight) {
      pdf.addImage(imgData, 'JPEG', 0, 0, pdfWidth, pdfHeight);
    } else {
      while (heightLeft > 0) {
        pdf.addImage(imgData, 'JPEG', 0, position, pdfWidth, pdfHeight);
        heightLeft -= pageHeight;
        position -= pageHeight;
        
        if (heightLeft > 0) {
          pdf.addPage();
        }
      }
    }
    
    // 保存PDF
    pdf.save('document.pdf');
    
  } catch (err) {
    console.error('PDF导出失败:', err);
    error.value = true;
  } finally {
    pdfLoading.value = false; // 隐藏PDF生成中的加载状态
  }
};
//调用浏览器打印
const exportPdf = async () => {
  if (!iframe.value) {
    error.value = true;
    return;
  }

  const iframeWindow = iframe.value.contentWindow;
  if (!iframeWindow) {
    error.value = true;
    return;
  }

  try {
  
    error.value = false;
    
    // 等待内容加载
  
    const doc = iframeWindow.document;
    const body = doc.body;
    const html = doc.documentElement;
    
    // 预加载图片
    
    printIframeContent(iframe.value)
    // 保存PDF
    
  } catch (err) {
  } finally {
  }
};
function printIframeContent(iframeElement) {
  if (!iframeElement.contentWindow) {
    console.error('iframe未加载完成');
    return;
  }

  // 添加打印样式
  const style = document.createElement('style');
  style.innerHTML = `
    @media print {
      body > *:not(iframe) {
        display: none !important;
      }
      iframe {
        position: absolute;
        top: 0;
        left: 0;
        width: 100% !important;
        height: auto !important;
        border: none !important;
      }
    }
  `;
  document.head.appendChild(style);

  // 触发打印
  iframeElement.contentWindow.focus();
  iframeElement.contentWindow.print();

  // 移除样式
  setTimeout(() => {
    document.head.removeChild(style);
  }, 1000);
}
defineExpose({
  exportPdf
});
</script>

<style scoped lang="scss">
.result-page {
  width: 100%;
  height: 700px;
  position: relative;
  padding: 0;
  margin: 0;
  overflow: hidden;
  border: 1px solid #eee;
  border-radius: 4px;
}

.loading-overlay,
.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 10;
}

.loading-spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #3498db;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.loading-text {
  color: #666;
  font-size: 16px;
}

.error-text {
  color: #e74c3c;
  font-size: 18px;
  margin-bottom: 16px;
}

.retry-button {
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  
  &:hover {
    background-color: #2980b9;
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>