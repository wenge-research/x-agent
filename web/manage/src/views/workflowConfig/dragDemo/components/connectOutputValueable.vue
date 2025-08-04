<template>
    <div>
        <el-select
            v-model="curValue"
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
                    :value="item.value"
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
    name: "connectOutputValueable",
    props: {
      valueName: {
        type: String,
        default: 'value',
      },
      value: String,
      parentNodes: Array,
      variables: Array,
      index: Number,
    },
    components: {},
    data() {
        return {
            options: [],
            selectedGroup: "",
        };
    },
    computed: {
      curValue() {
      // 根据 variables 对象派生出新数据
      const data = JSON.parse(JSON.stringify(this.variables))
      return data[this.index][this.valueName];
    }
  },
  watch: {
    variables: {
      handler(newVal, oldVal) {
        console.log('curValue 变化了:', newVal, oldVal);
        // 在这里可以执行其他操作
      },
      deep: true // 深度监听
    }
  },
    mounted() {
        this.initData();
    },
    methods: {
        initData() {
            this.options = this.parentNodes.map((item) => {
              let options = item.output.map((sub) => {
                  return {
                      parentName: item.nodeName,
                      value: sub.variable,
                      nodeId: item.nodeId,
                      label: sub.variable,
                  };
                })
              return {
                  label: item.nodeName,
                  id: item.nodeId,
                  options: options,
              };
            });
        },
        changeSelect(value) {
            // 获取选中的值及其所属的分组
            for (const group of this.options) {
                for (const option of group.options) {
                    if (option.value === value) {
                        this.selectedGroup = group.id;
                        this.selectedValue = option.value;
                    }
                }
            }
            this.$emit("change", {
                value: value,
                selectedGroup: this.selectedGroup,
            });
        },
    },
};
</script>
