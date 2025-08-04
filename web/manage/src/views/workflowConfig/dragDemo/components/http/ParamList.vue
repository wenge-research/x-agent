<template>
  <div>
    <div v-for="(param, index) in params" :key="index">
      <input type="text" v-model="param.key" placeholder="Key" />
      <input type="text" v-model="param.value" placeholder="Value" />
      <button @click="removeParam(index)">Remove</button>
    </div>
    <button @click="addParam">Add Parameter</button>
  </div>
</template>

<script>
export default {
  props: ['modelValue', 'type'],
  data() {
    return {
      params: this.modelValue || []
    };
  },
  watch: {
    modelValue(newVal) {
      this.params = newVal;
    },
    params(newVal) {
      this.$emit('update:modelValue', newVal);
    }
  },
  methods: {
    addParam() {
      this.params.push({ key: '', value: '' });
    },
    removeParam(index) {
      this.params.splice(index, 1);
    }
  }
};
</script>