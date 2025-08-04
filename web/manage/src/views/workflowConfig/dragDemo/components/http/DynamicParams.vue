<template>
    <div class="dynamic-params">
      <el-table
      :data="parameters"
      border
      style="width: 100%"
    >
      <el-table-column prop="name" :label="$t('parameterName')">
        <template slot-scope="scope">
          <el-input v-model="scope.row.name" size="small"></el-input>
        </template>
      </el-table-column>
      <!-- <el-table-column prop="value" label="值">
        <template slot-scope="scope">
          <el-input v-model="scope.row.value" size="small"></el-input>
        </template>
      </el-table-column> -->
      <el-table-column prop="value" >
        <template slot-scope="scope">
          <connectValueable :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData" :variables="parameters" 
                        :index="scope.$index" :value="scope.row.value"  @change="onChangeVoidValue($event, scope.$index)"  ></connectValueable>
        </template>
      </el-table-column>
      <el-table-column :label="$t('operation')">
        <template slot-scope="scope">
          <el-button size="mini" type="danger" icon="el-icon-delete" @click="deleteParameter(scope.$index)"></el-button>
        </template>
      </el-table-column>
    </el-table>
        <span class="add-btn" @click="addParameter"><em>+</em>{{$t('addParameter')}}</span>
    </div>
</template>

<script>
import connectValueable from "@/views/workflowConfig/dragDemo/components/connectValueable.vue";

export default {
  components: {connectValueable},
  props: {
    parameters: {
      type: Array,
      required: true,
      default: () => []
    }
  },
    data() {
        return {
        };
    },
    computed: {
        parentNodes() {
            return this.$store.state.workflow.parentNodes;
        }
    },
    mounted() {
    },
    methods: {
      onChangeVoidValue(data, index) {
          this.$set(this.parameters[index], 'value', data.value);
          this.$set(this.parameters[index], 'selectedGroup', data.selectedGroup);
          this.$set(this.parameters[index], 'type', data.type);
        },
        addParameter() {
            this.parameters.push({ name: "", value: "" });
        },
        deleteParameter(index) {
            this.parameters.splice(index, 1);
        },
    },
};
</script>

<style lang="scss" scoped>
.dynamic-params {
  margin: 10px 0;
    padding: 0 0;
    height: calc(100vh - 500px);
    overflow: auto;


    /* 表头样式 */
    ::v-deep .el-table {
        th {
            border: none !important; /* 去掉表头边框 */
            background: #f2f5fa;
            font-size: 14px;
            color: #828894;
            padding: 8px 0;
        }
        td {
            border: none !important; /* 去掉表头边框 */
            font-size: 14px;
            padding: 8px 0;
            .el-input {
              width: 92%;
          }
        }
        tr {
            border-bottom: 1px solid #ebeef5 !important; /* 每一行只有下边框 */
            background-color: transparent; /* 无背景 */
            color: #000; /* 字体黑色 */
        }
    }

    /* 删除按钮颜色 */
    .el-button--primary {
        background-color: #007bff; /* 蓝色背景 */
        border-color: #007bff; /* 蓝色边框 */
        color: #fff; /* 白色字体 */
    }

    /* 鼠标悬停效果 */
    .el-table tbody tr:hover {
        background-color: #f5f7fa; /* 悬停背景 */
    }

    /* 输入框样式 */
    .el-input__inner {
        border: 1px solid #dcdfe6; /* 输入框边框 */
        background-color: #fff; /* 输入框背景 */
        color: #000; /* 输入框字体 */
    }
}
.add-btn {
    color: #3666ea;
    cursor: pointer;
    margin: 14px 0 0 16px;
    position: relative;
    display: block;
    em {
        font-size: 22px;
        position: absolute;
        left: -16px;
        top: -2px;
    }
}
</style>
