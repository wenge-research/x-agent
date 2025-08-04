<template>
    <div>
        <el-select
            v-model="curValue"
            size="small"
            :placeholder="$t('selectVariable')"
            filterable
            clearable
            @change="changeSelect"
        >
            <el-option-group
                v-for="(group, index) in options"
                :key="index"
                :label="group.label"
            >
                <el-option
                    v-for="(item, i) in group.options"
                    :key="i"
                    :label="item.label"
                    :value="group.id + '_id_' + item.value"
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
      curParentNodeInputs: Array,//父级节点的参数，迭代节点用到
      curParentNodesData: Object,//父级节点的参数，迭代节点用到
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
          let selectedGroup = this.variables && this.variables.length ? this.variables[this.index].selectedGroup : ''
          this.curValue = selectedGroup ? selectedGroup + '_id_' + this.variables[this.index].value : ''
            let tempOptions = this.parentNodes && this.parentNodes.filter(
                (item) => item.data && item.data.variables && item.data.variables.length > 0 && item.data.type !== 'condition' && item.data.type !== 'onlyIn'
            );
            this.options = tempOptions && tempOptions.map((item) => {
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
              } else if(item.data.type === 'custom'){
                let output = item.data.output
                options = output.map((sub) => {
                  return {
                      parentName: item.data.label,
                      value: sub.label,
                      type: sub.type,
                      label: sub.label,
                  };
                })
              } else if(item.data.type === 'iteration'){
                let appForm = JSON.parse(item.data.appForm)
                let outputs = item.data.outputs ? JSON.parse(item.data.outputs) : []
                options = outputs.map((sub) => {
                  return {
                      parentName: item.data.label,
                      value: sub.label,
                      type: sub.type,
                      label: sub.label,
                  };
                })
              } else if(item.data.type === 'diedai'){
                let output = item.data.outputs ?  JSON.parse(item.data.outputs).filter(item => item.label) : []
                options = output.map((sub) => {
                  let type = ['string', 'integer', 'object', 'number', 'boolean', 'file'].includes(sub.type) ? `array[${sub.type}]` : sub.type
                  return {
                      parentName: item.data.label,
                      value: sub.label,
                      type: type,
                      label: sub.label,
                  };
                })
              } else if(item.data.type === 'code'){
                let configItems = item.data.configItems ? JSON.parse(item.data.configItems) : []
                options = configItems.map((sub) => {
                  return {
                      parentName: item.data.label,
                      value: sub.name,
                      type: sub.type,
                      label: sub.name,
                  };
                })
              } else if(item.data.type === 'variable'){
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
              }else if(item.data.type === 'http'){
                let appForm =typeof item.data.appForm === 'string'
                  ? JSON.parse(item.data.appForm)
                  : item.data.appForm;
                let variables = appForm.endNode && Array.isArray(appForm.endNode) ? appForm.endNode : appForm.endNode ? JSON.parse(appForm.endNode) : []
                options = variables.map((sub) => {
                  return {
                      parentName: item.data.label,
                      value: sub.name,
                      type: sub.type,
                      label: sub.name,
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
            //父级节点的参数，迭代节点用到
            if(this.curParentNodeInputs && this.curParentNodeInputs.length){
              let item_result = this.curParentNodesData.item_result ? JSON.parse(this.curParentNodesData.item_result) : [];
              let options = this.curParentNodeInputs.map((item) => {
                return {
                  parentName: item.name,
                  value: item.label,
                  type: item.type,
                  label: item.label,
                };
              });
              item_result.forEach((item) => {
                item.label && this.options.push({
                  label: this.curParentNodeInputs[0].name,
                  id: item.selectedGroup,
                  options: [
                    {
                      value: item.label,
                      type: item.type,
                      label: item.label,
                    },
                  ],
                })
              });
              this.options.push({
                  label: this.curParentNodeInputs[0].name,
                  id: this.curParentNodeInputs[0].pid,
                  options: options,
              })
            }
        },
        changeSelect(value) {
            // 获取选中的值及其所属的分组
            let id = value.split('_id_')[0]
            let val = value.split('_id_')[1]
            for (const group of this.options) {
                for (const option of group.options) {
                    if (option.value === val && group.id === id) {
                        this.selectedGroup = group.id;
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
<style lang="scss" scoped>
  ::v-deep .el-select-dropdown__item{
    span:first-child{
      margin-right: 4px;
    }
  }
</style>
