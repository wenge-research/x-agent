<template>
  <w-grid :cols="24" :row-gap="16" class="panel">
    <w-grid-item
      class="panel-col"
      :span="{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12, xxl: 6 }"
    >
      <w-space>
        <w-avatar :size="54" class="col-avatar">
          <img
            alt="avatar"
            src="//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/288b89194e657603ff40db39e8072640.svg~tplv-49unhts6dw-image.image"
          />
        </w-avatar>
        <w-statistic
          :title="$t('message.workplace.onlineContent')"
          :value="373.5"
          :precision="1"
          :value-from="0"
          animation
          show-group-separator
        >
          <template #suffix>
            
            W+ <span class="unit">{{ $t('message.workplace.pecs') }}</span>
          </template>
        </w-statistic>
      </w-space>
    </w-grid-item>
    <w-grid-item
      class="panel-col"
      :span="{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12, xxl: 6 }"
    >
      <w-space>
        <w-avatar :size="54" class="col-avatar">
          <img
            alt="avatar"
            src="//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/fdc66b07224cdf18843c6076c2587eb5.svg~tplv-49unhts6dw-image.image"
          />
        </w-avatar>
        <w-statistic
          :title="$t('message.workplace.putIn')"
          :value="368"
          :value-from="0"
          animation
          show-group-separator
        >
          <template #suffix>
            <span class="unit">{{ $t('message.workplace.pecs') }}</span>
          </template>
        </w-statistic>
      </w-space>
    </w-grid-item>
    <w-grid-item
      class="panel-col"
      :span="{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12, xxl: 6 }"
    >
      <w-space>
        <w-avatar :size="54" class="col-avatar">
          <img
            alt="avatar"
            src="//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/77d74c9a245adeae1ec7fb5d4539738d.svg~tplv-49unhts6dw-image.image"
          />
        </w-avatar>
        <w-statistic
          :title="$t('message.workplace.newDay')"
          :value="8874"
          :value-from="0"
          animation
          show-group-separator
        >
          <template #suffix>
            <span class="unit">{{ $t('message.workplace.pecs') }}</span>
          </template>
        </w-statistic>
      </w-space>
    </w-grid-item>
    <w-grid-item
      class="panel-col"
      :span="{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12, xxl: 6 }"
      style="border-right: none"
    >
      <w-space>
        <w-avatar :size="54" class="col-avatar">
          <img
            alt="avatar"
            src="//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/c8b36e26d2b9bb5dbf9b74dd6d7345af.svg~tplv-49unhts6dw-image.image"
          />
        </w-avatar>
        <w-statistic
          :title="$t('message.workplace.newFromYesterday')"
          :value="2.8"
          :precision="1"
          :value-from="0"
          animation
        >
          <template #suffix> % <icon-caret-up class="up-icon" /> </template>
        </w-statistic>
      </w-space>
    </w-grid-item>
    <w-grid-item :span="24">
      <w-divider class="panel-border" />
    </w-grid-item>
  </w-grid>
</template>

<script lang="ts" setup>
  import { IconCaretUp } from 'winbox-ui-next/es/icon';
</script>

<style lang="scss" scoped>
  .w-grid.panel {
    margin-bottom: 0;
    padding: 16px 20px 0 20px;
    background: #fff;
  }
  .panel-col {
    padding-left: 43px;
    border-right: 1px solid rgb(var(--gray-2));
  }
  .col-avatar {
    margin-right: 12px;
    background-color: var(--color-fill-2);
  }
  .up-icon {
    color: rgb(var(--red-6));
  }
  .unit {
    margin-left: 8px;
    color: rgb(var(--gray-8));
    font-size: var(--font12);
  }
  :deep(.panel-border) {
    margin: 4px 0 0 0;
  }
</style>
