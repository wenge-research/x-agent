<template>
  <vue-office-docx :src="pdfurl" @rendered="rendered" />
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue'
import VueOfficeDocx from '@vue-office/docx';
import '@vue-office/docx/lib/index.css';

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

const rendered = async () => {
  
};
</script>
