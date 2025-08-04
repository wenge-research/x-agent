<template>
  <div class="dynamic-params">
    <el-table
      ref="table"
      :data="parameters"
      border
       row-key="id"
      style="width: 100%"
      default-expand-all
      :tree-props="{ children: 'children'}"
    >
      <el-table-column prop="name" :label="$t('parameterName')">
        <template slot-scope="scope">
          <el-input v-model="scope.row.name" size="small"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="type" :label="$t('parameterType')">
        <template slot-scope="scope">
          <el-select v-model="scope.row.type" :placeholder="$t('pleaseSelect')" size="small">
            <el-option label="String" value="string"></el-option>
            <el-option label="Number" value="number"></el-option>
            <el-option label="Boolean" value="boolean"></el-option>
            <el-option label="Object" value="object"></el-option>
            <el-option label="Array" value="array"></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column :label="$t('operation')">
        <template slot-scope="scope">
          <el-button size="mini" type="danger" icon="el-icon-delete" @click="deleteParameter(scope.$index, scope.row)"></el-button>
          <el-button v-if="scope.row.type === 'object'" size="mini" type="primary" icon="el-icon-plus" @click="addParameter(scope.row)"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <span class="add-btn" @click="addParameter(null)"><em>+</em>{{$t('addParameter')}}</span>
  </div>
</template>

<script>
export default {
  data() {
    return {
      parameters: [
        { id: 1, name: '', type: '', children: []}
      ]
    };
  },
  methods: {
    addParameter(parent) {
      const newId = this.parameters.reduce((maxId, item) => Math.max(item.id, maxId), 0) + 1;
      const newRow = { id: newId, name: '', type: '', children: []};

      if (parent) {
        // 使用 Vue.set 方法确保数组变化能被检测
        this.$set(parent.children, parent.children.length, newRow);
      } else {
        this.parameters.push(newRow);
      }
    },
    deleteParameter(index, row) {
      if (row.children && row.children.length > 0) {
        this.$message.error('请先删除子参数');
        return;
      }

      if (row.parent) {
        const parent = row.parent;
        const childIndex = parent.children.findIndex(child => child.id === row.id);
        if (childIndex !== -1) {
          parent.children.splice(childIndex, 1);
          if (parent.children.length === 0) {
          }
        }
      } else {
        this.parameters.splice(index, 1);
      }
    }
  }
};
</script>


<style lang="scss" scoped>
.dynamic-params {
  margin: 10px 0;
    padding: 0 20px;
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
                  /* 输入框样式 */
          .el-input {
              width: 85%;
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
