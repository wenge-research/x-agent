<template>
  <div>
      <el-select
          v-model="curValue"
          size="small"
          :placeholder="$t('selectVariable')"
          @change="changeSelect"
      >
          <el-option-group
              v-for="group in options"
              :key="group.label"
              :label="group.label"
          >
              <el-option
                  v-for="item in group.options"
                  :key="item.value"
                  :label="item.label"
                  :value="1 + '_id_' + item.value"
              >
                  <span style="float: left">{{ item.label }}</span>
                  <span
                      style="float: right; color: #8492a6; font-size: 12px"
                      >{{ item.type }}</span
                  >
              </el-option>
          </el-option-group>
      </el-select>
  </div>
</template>

<script>
export default {
  name: "connectVar",
  props: {
    valueName: {
      type: String,
      default: 'value',
    },
    value: String,
    parentNodes: Array,
    variables: Array,
    index: {
      type: Number,
      default: 0,
    },
  },
  components: {},
  data() {
      return {
          options: [],
          curValue: "",
          selectedGroup: "",
          selectedType: "",
      };
  },
watch: {
  
},
  mounted() {
      this.initData();
  },
  methods: {
      initData() {
        let selectedGroup = '1'
        this.curValue = selectedGroup ? selectedGroup + '_id_' + this.variables[this.index].value : ''
          let tempOptions = this.parentNodes.filter(
              (item) => item.data.variables && item.data.variables.length > 0
          );
          this.options = tempOptions.map((item) => {
            let options = []
            if(item.data.type === 'paramsFilter'){
              let filterVariables = item.data.filterVariables ? JSON.parse(item.data.filterVariables) : []
              options = filterVariables.map((sub) => {
                return {
                    parentName: item.data.label,
                    value: sub.name,
                    type: sub.type,
                    label: sub.name,
                };
              })
            }else if(item.data.type === 'code'){
              let configItems = item.data.configItems ? JSON.parse(item.data.configItems) : []
              options = configItems.map((sub) => {
                return {
                    parentName: item.data.label,
                    value: sub.name,
                    type: sub.type,
                    label: sub.name,
                };
              })
            }else if(item.data.type === 'variable'){
              let appForm =typeof item.data.appForm === 'string'
                ? JSON.parse(item.data.appForm)
                : item.data.appForm;
              let variables = item.data.variables && Array.isArray(item.data.variables) ? item.data.variables : item.data.variables ? JSON.parse(item.data.variables) : []
              options = variables.map((sub) => {
                return {
                    parentName: item.data.label,
                    value: appForm.inputs[0].label,
                    type: appForm.cusType,
                    label: appForm.inputs[0].label,
                };
              })
            } else {
              let variables = item.data.variables && Array.isArray(item.data.variables) ? item.data.variables : item.data.variables ? JSON.parse(item.data.variables) : []
              options = variables.map((sub) => {
                return {
                    parentName: item.data.label,
                    value: sub.name,
                    type: sub.type,
                    label: sub.name,
                };
              })
            }
            return {
                label: item.data.label,
                id: item.id,
                options: options,
            };
          });
      },
      changeSelect(value) {
          // 获取选中的值及其所属的分组
          let id = value.split('_id_')[0]
          let val = value.split('_id_')[1]
          for (const group of this.options) {
              for (const option of group.options) {
                  if (option.value === val && group.id === id) {
                      this.selectedGroup = '1';
                      this.selectedType = option.type;
                  }
              }
          }
          this.$emit("change", {
              value: val,
              type: this.selectedType,
              selectedGroup: this.selectedGroup,
          });
      },
  },
};
</script>
