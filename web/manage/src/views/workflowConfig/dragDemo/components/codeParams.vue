<template>
  <div class="plugin-set">
    <div class="joinConfig">
      <div class="joinConfig-top">
        <div class="joinConfig-top-title">配置输入参数</div>
      </div>
      <div class="joinConfig-bottom">
        <el-table :data="dataList" style="width: 100%" :cell-style="hearderCellStyle"
          :header-cell-style="hearderCellStyle" :header-row-style="headerRowStyle">
          <el-table-column label="参数名" width="150">
            <template slot-scope="scope">
              <el-input size="small" v-model="scope.row.label" placeholder="请输入"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="参数描述">
            <template slot-scope="scope">
              <el-input size="small" v-model="scope.row.desc" placeholder="请输入"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="参数类型" width="150">
            <template slot-scope="scope">
              <el-select size="small" v-model="scope.row.type" placeholder="请选择">
                <el-option :label="item.label" :value="item.value" v-for="(item, index) in options"
                :key="index"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="设定值" width="200">
            <template slot-scope="scope">
              <el-input size="small" v-model="scope.row.value" placeholder="无"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="必填" width="50" align="center">
            <template slot-scope="scope">
              <el-checkbox size="small" v-model="scope.row.required"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column label="开启" width="50" align="center">
            <template slot-scope="scope">
              <el-switch size="small" v-model="scope.row.enabled"></el-switch>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="50" align="center">
            <template slot-scope="scope">
              <i class="el-icon-delete" @click="deleteRow(scope.$index)"></i>
            </template>
          </el-table-column>
        </el-table>
        <div>
          <el-button plain @click="addRow" icon="el-icon-plus" size="mini"
            style="border-radius: 2px;font-size: 14px;color: #36383D;margin: 8px 0 0 10px;">
            <span>新增参数</span>
          </el-button>
        </div>
      </div>
    </div>
    <div class="joinConfig">
      <div class="joinConfig-top">
        <div class="joinConfig-top-title">配置输出参数</div>
      </div>
      <div class="joinConfig-bottom">
        <el-row style="padding: 10px 0px 4px 20px;font-size: 12px;color: #828894;">
          <el-col :span="4" >参数名</el-col>
          <el-col :span="12" >参数描述</el-col>
          <el-col :span="4" >参数类型</el-col>
          <el-col :span="2" >开启</el-col>
          <el-col :span="2" >操作</el-col>
        </el-row>
        <configOutput :configItems="configItems" @updateConfig="updateConfig"></configOutput>
        <div>
          <el-button plain @click="addJson" icon="el-icon-plus" size="mini"
            style="border-radius: 2px;font-size: 14px;color: #36383D;margin: 8px 0 0 10px;">
            <span>新增参数</span>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import configOutput from './configOutput.vue';

  export default {
    props: {
      nodeStart: Object,
      nodeCode: Object,
      dataList: {
        type: Array,
        default: () => []
      },
      configItems: {
        type: Array,
        default: () => []
      }
    },
    components: {
      configOutput,
    },
    data ()
    {
      return {
        options: [
          {
              value: "string",
              label: "string",
          },
          {
              value: "number",
              label: "number",
          },
          {
              value: "integer",
              label: "integer",
          },
          {
              value: "boolean",
              label: "boolean",
          },
          {
              value: "object",
              label: "object",
          },
          {
              value: "array",
              label: "array",
              children: [
                  {
                      value: "array[string]",
                      label: "String",
                  },
                  {
                      value: "array[number]",
                      label: "Number",
                  },
                  {
                      value: "array[integer]",
                      label: "Integer",
                  },
                  {
                      value: "array[boolean]",
                      label: "Boolean",
                  },
                  {
                      value: "array[object]",
                      label: "Object",
                  },
                  {
                      value: "array[file]",
                      label: "file",
                  }
              ]
          }
      ],
        configItems: [],
        startConfigItems: [{
          "name": "result", "type": "array[string]", "level": 0, "children": []
        }],
        apiType: "GET",
        apiLink: "",
        apiOptions: [
          {
            value: "GET",
            label: "GET",
          },
          {
            value: "POST",
            label: "POST",
          },
        ],
        joinQueryActive: "Query",

        dataList: [],
        joinConfig: [
          {
            title: "Query",
          },
          {
            title: "Header",
          },
          {
            title: "Path",
          },
          {
            title: "Body",
          },
        ],
        headerRowStyle: {
          color: '#828894', // 表头行文字颜色
          fontSize: '12px', // 表头行文字大小
          height: '16px', // 表头行高
          borderColor: 'transparent' // 表头行边框颜色
        },
        hearderCellStyle: {
          borderColor: 'transparent', // 表头单元格边框颜色
        },
        combinedData: {},
      };
    },
    watch: {
      dataList: {
      handler(newVal, oldVal) {
        console.log('dataList changed:', newVal);
      },
      deep: true
    },
    configItems: {
      handler(newVal, oldVal) {
        console.log('configItems changed:', newVal);
      },
      deep: true
    },
    combinedData: {
      handler(newVal) {
        const jsonString = JSON.stringify(newVal);
        this.$emit("updateDataList", jsonString);
      },
      // deep: true,
    },
    },
    mounted() {
      //let output = JSON.parse(JSON.stringify(this.nodeStart.output));
      this.dataList = this.nodeStart.input.map(item =>{
        item.enabled = true
        return item
      })
      this.configItems = this.nodeCode.output.map(item =>{
        item.enabled = true
        item.children = item.children ? item.children : []
        return item
      })
    },
    methods: {
      addJson ()
      {
        this.configItems.push({ "label": "result", "type": "string", "level": 0, "children": [], expanded: true });
      },
      updateConfig (updatedConfig)
      {
        console.log(updatedConfig,'updatedConfig');
        
        this.configItems = updatedConfig;
      },
      deleteRow (index)
      {
        this.dataList.splice(index, 1);
      },
      addRow (index)
      {
        this.dataList.push({
          label: "",
          desc: "",
          type: "string",
          value: "",
          required: false,
          enabled: true,
        });
      },
    },
  };
</script>

<style lang="scss" scoped>
  ::v-deep .el-table .el-table__cell {
    padding: 6px 0;
  }

  ::v-deep .el-table::before {
    height: 0;
  }

  .joinConfig {
    border-radius: 2px;
    background: #fff;
    padding: 16px;
    margin-bottom: 16px;

    &-top {
      display: flex;
      justify-content: space-between;

      .joinConfig-top-title {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 32px;
        font-weight: 500;
        font-size: 18px;
        color: #36383d;
        line-height: 32px;
      }

      .joinConfig-top-title::before {
        display: block;
        width: 4px;
        height: 18px;
        content: "";
        margin-right: 7px;
        background: #1747e5;
        border-radius: 0px 2px 2px 0px;
      }
    }

    &-center {
      display: flex;
      margin: 12px 0;

      .joinConfig-center-item {
        width: 25%;
        height: 28px;
        line-height: 28px;
        font-size: 14px;
        color: #828894;
        border-radius: 4px;
        text-align: center;
        background: #f7f8fa;
        border: 2px solid #f7f8fa;
        box-sizing: border-box;
        cursor: pointer;
      }

      .joinConfig-center-item-active {
        background: #fff !important;
        color: #36383d;
      }
    }

    &-bottom {
      ::v-deep .el-table {
        .el-table__cel {
          padding: 0;
        }
      }
    }
  }

  .outConfig {
    height: 200px;
    background: #f7f8fa;
  }
  ::v-deep .el-col {
    padding: 0 10px;
  }
</style>