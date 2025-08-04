<template>
  <div class="preview_content">
    <VuePdfApp
      v-if="pdfurl"
      :page-scale="'page-fit'"
      :theme="'light'"
      :page-number="1"
      :style="`width: 100%; height: 100%;`"
      :pdf="pdfurl"
      @pages-rendered="pagesRendered"
      :config="{toolbar: false}"></VuePdfApp>
  </div>
</template>


<script setup>
import { onMounted, ref, reactive } from 'vue'
import VuePdfApp from 'vue3-pdf-app'
import 'vue3-pdf-app/dist/icons/main.css'
const  pdfurl = ref('')

const props = defineProps({
  url: {
    type: String,
    default: "",
  }
});

// 这里必须使用异步去引用pdf文件，直接去import会报错，也不知道为什么
onMounted(async ()=> {
  pdfurl.value = props.url
})
const pagesRendered = function () {
  setTimeout(() => { 
    iframeLoding.value = false
  },10)
}

const iframeLoding = ref(false)

</script>
<style lang="scss" scoped>
@mixin text-ellipsis($line: 2) {
  overflow: hidden;
  word-break: break-all;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: $line;
  -webkit-box-orient: vertical;
}
.preview_content {
  height: calc(86vh);
  margin: 0 auto;
  width: 660px;
  border-radius: 8px;
  // border: 1px solid #E4E8EE;
  .w-spin {
    width: 100%;
    height: calc(100% - 6px);
  }
  :deep(){
    .w-spin-mask{
      background-color: #fff!important;
    }
  }

  #myFrame {
    height: 100%;
    width: 100%;
    overflow: auto;
  }
}
.w-btn{
  border-radius: 4px !important;
}
.title{
  display: flex;
  align-items: center;
  font-size: 16px;
  font-family: MiSans, MiSans;
  font-weight: 500;
  line-height: 22px;
  color: #181B49;
}
.content{
  color: #646479;
  padding-left: 32px
}
.pop-footer{
  display: flex;
  justify-content: flex-end;
  overflow: hidden;
  margin-top: 10px;
} 
.dialog-footer{
  display: flex;
  justify-content: center;
  .btn-left,.btn-right,.left-btn-box{
    display: flex;
  }
  .btn-left .left-btn-box {
    margin-right: 4px;
  }
}
.w-input-wrapper{
  width: 600px;
}
.title{
  max-width: 600px;
  font-size: 20px;
  color: #181B49;
  font-weight: 700;
  @include text-ellipsis(1);
}
</style>
<style lang="scss">
.pdf-app.light {
  background: #fff!important;
}
.pdf-app .pdfViewer .page {
    box-sizing: content-box!important;
    background-clip: content-box!important;
    -o-border-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAATCAYAAAByUDbMAAAA1ElEQVQ4jbWUWw6EIAxFy2NFs/8NzR4UJhpqLsdi5mOmSSMUOfYWqv3S0gMr4XlYH/64gZa/gN3ANYA7KAXALt4ktoQ5MI9YxqaG8bWmsIysMuT6piSQCa4whZThCu8CM4zP9YJaKci9jicPq3NcBWYoPMGUlhG7ivtkB+gVyFY75wXghOvh8t5mto1Mdim6e+MBqH6XsY+YAwjpq3vGF7weTWQptLEDVCZvPTMl5JZZsdh47FHW6qFMyvLYqjcnmdFfY9Xk/KDOlzCusX2mi/ofM7MPkzBcSp4Q1/wAAAAASUVORK5CYII=) 9 9 repeat!important;
    border-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAATCAYAAAByUDbMAAAA1ElEQVQ4jbWUWw6EIAxFy2NFs/8NzR4UJhpqLsdi5mOmSSMUOfYWqv3S0gMr4XlYH/64gZa/gN3ANYA7KAXALt4ktoQ5MI9YxqaG8bWmsIysMuT6piSQCa4whZThCu8CM4zP9YJaKci9jicPq3NcBWYoPMGUlhG7ivtkB+gVyFY75wXghOvh8t5mto1Mdim6e+MBqH6XsY+YAwjpq3vGF7weTWQptLEDVCZvPTMl5JZZsdh47FHW6qFMyvLYqjcnmdFfY9Xk/KDOlzCusX2mi/ofM7MPkzBcSp4Q1/wAAAAASUVORK5CYII=) 9 9 repeat!important;
    background-color: #fff!important;
}
.pdf-app #loadingBar .progress {
  display: none;
}
#viewerContainer {
  &::-webkit-scrollbar-thumb {
    background-color: #FFF;
  }
  &:hover::-webkit-scrollbar-thumb {        
    background-color: rgba(144, 147, 153, 0.3);
  }
}
</style>