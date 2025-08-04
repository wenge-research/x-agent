<template>
  <div v-if="visible" class="popup-overlay" @click.stop="close">
    <div class="popup-content">
      <div class="tit" v-if="isLoaded">
        <iconpark-icon  name="close-line" color="#999" class="btn"></iconpark-icon>
        <span>{{ title }}</span>
      </div>
      <iframe :src="url" frameborder="0"  @load="onIframeLoad"></iframe>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref } from 'vue';

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  url: {
    type: String,
    required: true
  },
  title: {
    type: String,
    required: true
  }
});

const emit = defineEmits(['close']);
const isLoaded = ref(false);

const onIframeLoad = () => {
  isLoaded.value = true;
};
const close = () => {
  emit('close');
};
</script>

<style scoped>
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-content {
  position: fixed;
  background: #fff;
  width: 100%;
  height: 85vh;
  bottom: 0;
}

.popup-content iframe {
  width: 100%;
  height: 100%;
  border: none;
}
.tit{
    background: #fff;
    position: fixed;
    width: 100%;
    bottom: 85vh;
    border-bottom: 1px solid #eee;
    border-radius: 20px 20px 0 0;
    text-align: center;
    padding: 10px 50px;
    font-size: 16px;
    height: 44px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    .btn{
      position: absolute;
      font-size: 20px;
      left: 10px;
      top: 12px;
    }
}

</style>