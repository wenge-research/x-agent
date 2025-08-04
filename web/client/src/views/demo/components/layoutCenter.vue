<template>
    <div class="center-side">
      <div class="panel">
        <DataPanel />

      </div>
      <!-- <w-grid :cols="24" :col-gap="16" :row-gap="16" style="margin-top: 16px">
        <w-grid-item
          :span="{ xs: 24, sm: 24, md: 24, lg: 12, xl: 12, xxl: 12 }"
        >
           <DataPanel />
        </w-grid-item>
        <w-grid-item
          :span="{ xs: 24, sm: 24, md: 24, lg: 12, xl: 12, xxl: 12 }"
        >
           <DataPanel />
        </w-grid-item>
      </w-grid> -->
    </div>
</template>

<script lang="ts" setup>
  import DataPanel from './data-panel.vue';
</script>

<style scoped lang="scss">
  .center-side {
    flex: 1;
    overflow: auto;
    margin: 0 16px;
  }
</style>
