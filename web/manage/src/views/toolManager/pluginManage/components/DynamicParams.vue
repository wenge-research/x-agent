<template>
  <div class="dynamic-params">
    <el-table
      ref="table"
      :data="parameters"
      border
      row-key="paramId"
      style="width: 100%"
      max-height="520px"
      default-expand-all
      :tree-props="{ children: 'children' }"
    >
      <el-table-column prop="name" :label="$t('parameterName')">
        <template slot-scope="scope">
          <el-input v-model="scope.row.name" size="small"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="value" label="值" v-if="hasValue">
        <template slot-scope="scope">
          <el-input v-model="scope.row.value" size="small"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="type" :label="$t('parameterType')">
        <template slot-scope="scope">
          <el-select v-model="scope.row.type" :placeholder="$t('pleaseSelect')" size="small">
            <el-option label="String" value="string"></el-option>
            <el-option label="Number" value="number"></el-option>
            <el-option label="Boolean" value="boolean"></el-option>
            <el-option label="Object" value="object"></el-option>
            <el-option label="Array[Object]" value="array[object]"></el-option>
            <el-option label="Array[String]" value="array[string]"></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column :label="$t('operation')" width="120px;">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="deleteParameter(scope.row)">{{$t("delete")}}</el-button>
          <el-button v-if="scope.row.type === 'object'" size="mini" type="text" @click="addParameter(scope.row)">{{$t("add")}}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button style="margin-top: 16px;" @click="addParameter(null)"><i class="el-icon-plus"></i>{{$t('addParameter')}}</el-button>
  </div>
</template>

<script>
export default {
  props: {
    parameters: {
      type: Array,
      required: true,
      default: () => []
    },
    hasValue: {
      type: Boolean,
      required: false,
      default: () => false
    }
  },
  data() {
    return {
      nextId: 1
    };
  },
  methods: {
    generateRandomId() {
      // 使用当前时间戳和随机数生成一个唯一的 ID
      const timestamp = Date.now().toString(36); // 将时间戳转换为 36 进制字符串
      const randomNum = Math.random().toString(36).substr(2, 9); // 生成一个 9 位的随机字符串
      return `${timestamp}-${randomNum}`;
    },
    addParameter(parent) {
      const newId = this.generateRandomId();
      const newRow = { paramId: newId, name: '', value: '', type: '', children: [] };

      if (parent) {
        this.$set(parent.children, parent.children.length, newRow);
      } else {
        this.parameters.push(newRow);
      }
    },
    deleteParameter(row) {
      if (row.children && row.children.length > 0) {
        this.$message.error(this.$t("pleaseDeleteSubParamsFirst"));
        return;
      }

      this.removeRowById(this.parameters, row.paramId);
    },
    removeRowById(array, paramId) {
      for (let i = 0; i < array.length; i++) {
        if (array[i].paramId === paramId) {
          array.splice(i, 1);
          return true;
        }
        if (array[i].children && array[i].children.length > 0) {
          if (this.removeRowById(array[i].children, paramId)) {
            return true;
          }
        }
      }
      return false;
    }
  }
};
</script>

<style lang="scss" scoped>
.dynamic-params {
  margin: 10px 0;
  padding: 0 20px;
  height: calc(100vh - 270px);
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
.el-button--text {
    color: #1747E5 !important;
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