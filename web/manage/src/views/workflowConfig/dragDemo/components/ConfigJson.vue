<template>
  <div class="config-json">
    <div
      v-for="(item, index) in configItems"
      :key="index"
      class="config-item"
    >
      <iconpark-icon @click="toggleExpand(index)" name="arrow-down-s-line" class="draw-icon" v-if="item.children && item.children.length > 0"></iconpark-icon>
      <el-input
        v-model="item.name"
        :placeholder="$t('variableName')"
        @input="validateVariableName(item)"
        :style="{ width: `${220 - item.level * 20}px` }"
        size="small"
        maxlength="20"
        class="variable-input"
      />
      <el-select v-model="item.type" size="small" class="type-select" style="width: 100px;">
        <el-option label="string" value="string"></el-option>
        <el-option label="number" value="number"></el-option>
        <el-option label="boolean" value="boolean"></el-option>
        <el-option label="object" value="object"></el-option>
        <el-option label="array[string]" value="array[string]"></el-option>
        <el-option label="array[number]" value="array[number]"></el-option>
        <el-option label="array[integer]" value="array[integer]"></el-option>
        <el-option label="array[boolean]" value="array[boolean]"></el-option>
        <el-option label="array[object]" value="array[object]"></el-option>
      </el-select>
      <!-- <i class="el-icon-remove-outline delete-btn" @click="removeItem(index)"></i>
      <i class="el-icon-circle-plus-outline add-btn" v-if="item.type === 'object'"  @click="addItem(index)"></i> -->
      <div v-if="item.children && item.children.length > 0" class="item-sub" v-show="item.expanded">
        <ConfigJson
          :configItems="item.children"
          @updateConfig="updateChildConfig(index, $event)"
        />
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'ConfigJson',
    props: {
      configItems: {
        type: Array,
        default: () => [],
      },
    },
    methods: {
      toggleExpand(index) {
        this.$set(this.configItems[index], 'expanded', !this.configItems[index].expanded);
      },
      addItem(parentIndex) {
        const newItem = {
          name: '',
          type: 'string',
          level: parentIndex === -1 ? 0 : this.configItems[parentIndex].level + 1,
          children: [],
        };
        if (parentIndex === -1) {
          this.configItems.push(newItem);
        } else {
          this.configItems[parentIndex].children.push(newItem);
        }
      },
      removeItem(index) {
        this.configItems.splice(index, 1);
      },
      updateChildConfig(index, updatedConfig) {
        this.$set(this.configItems, index, updatedConfig);
      },
      validateVariableName(item) {
        const regex = /^[a-zA-Z_$][a-zA-Z0-9_$]*$/;
        if (!regex.test(item.name)) {
          item.name = item.name.replace(/[^a-zA-Z0-9_$]/g, '');
        }
      },
    },
  };
  </script>
  <style lang="scss" scoped>
    .config-json {
      padding: 10px 0;
    }
    
    .config-item {
      position: relative;
      padding:  0 0 0 20px;
      margin-bottom: 6px;
      .draw-icon{
        position: absolute;
        left: -5px;
        top: 7px;
        font-size: 20px;
        width: 20px;
        cursor: pointer;
      }
    }
    
    .variable-input {
      width: 220px;
      margin-right: 10px;
    }
    
    .type-select {
      margin-right: 10px;
    }
    
    .delete-btn, .add-btn, .add-root-btn {
      cursor: pointer;
      margin-right: 5px;
      position: relative;
      top: 3px;
      font-size: 18px;
    }
    
    
    .add-root-btn {
      margin-top: 10px;
      width: 100%;
      text-align: center;
    }
    </style>