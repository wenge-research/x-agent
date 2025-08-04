<template>
  <div class="container_demo">
    <div class="SecondaryTitle" v-if="isMobile">
      <h2>1111111</h2>
      <w-button @click="handleClick">我的记录</w-button>
      <w-drawer :visible="visible" @ok="handleOk" @cancel="handleCancel" unmountOnClose>
        <template #title>
          Title
        </template>
        <div>You can cusstomize </div>
      </w-drawer>
    </div>
    <LayoutAside />
    <LayoutCenter />
    <LayoutAsideRight v-show="!isMobile" />
  </div>
</template>

<script lang="ts" setup>

  
  import { ref,watch,defineAsyncComponent } from 'vue';
  import { useBasicLayout } from '/@/hooks/useBasicLayout'

  const LayoutAside = defineAsyncComponent(() => import('/@/layout/component/aside.vue'));
  const LayoutCenter = defineAsyncComponent(() => import('./components/layoutCenter.vue'));
  const LayoutAsideRight = defineAsyncComponent(() => import('/@/layout/component/asideRight.vue'));
  // 移动端自适应相关
  const { isMobile } = useBasicLayout()

  const visible = ref(false)

  const handleClick = () => {
    visible.value = true
  }
  const handleOk = () => {
    visible.value = false
  }
  const handleCancel = () => {
    visible.value = false
  }

  
</script>



<style lang="scss" scoped> 
.container_demo {
  padding-bottom: 0;
  display: flex;
  height: 100%;
  justify-content: space-between;

  .SecondaryTitle{
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}




</style>

