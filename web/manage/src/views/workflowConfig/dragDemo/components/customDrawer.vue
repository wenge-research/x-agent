<template>
  <div class="Ec-x6-icon">
    <el-drawer title="" :visible.sync="drawerVisible" :modal="false" :modal-append-to-body="false"
      :direction="direction" v-if="drawerVisible" size="500px" style="
                position: absolute;
                width: 500px;
                box-sizing: border-box;
                right: 0;
                left: inherit;
            " :show-close="false">
      <div style="
          display: flex;
          align-items: center;
          justify-content: space-between;
      ">
        <HeadTool v-if="drawerVisible" :label="sourceData.label" :imgWidth="24" :imgHeight="24" @remove="handleRemove"
          @copy="copyHandler" @input="inputHandler" @testNodes="openRunDrawer" imgSuffix="workflow-zidingyi" />
        <div style="
            width: 1px;
            height: 20px;
            background: #d3d9e6;
            margin: 0 12px 12px;
        "></div>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer"
          style="margin-bottom: 10px; cursor: pointer"></iconpark-icon>
      </div>
      <div class="sub">自定义节点，处理输入变量来生成返回值</div>
      <div class="drawer-content">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="0" :title="$t('inputVariable')">
            <el-form :model="ruleForm" ref="ruleForm" label-width="0" class="demo-ruleForm">
              <el-form-item label="" v-for="(panel, index) in appForm.inputs" :key="index">
                <inputList :defalutVal="panel.label" :single="true" v-if="drawerVisible"
                  :inputs="startNodevariables[index]" @updateInputList="(value) => updateInputList(value, index)"
                  :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList>
              </el-form-item>
            </el-form>
          </el-collapse-item>

          <el-collapse-item name="2" :title="$t('output')">
            <div class="result-cont" v-for="item in appForm.output">
              {{ item.label }}<span>{{ item.type }}</span>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-drawer>


  </div>
</template>

<script>
  // mixins
  import drawerMixins from "./drawerMixins";
  // 文件内引入
  // 引入样式、主题、代码风格等配置或样式文件
  export default {
    props: ["panels", "params"],
    name: "CustomDrawer",
    components: {
    },
    mixins: [drawerMixins],
    data ()
    {
      return {
        ruleForm: {},
        appForm: {
          inputs: {},
          output: {},
        },
        drawerVisible: false,
        startNodevariables: [null,null,null,null,null,null,null],
      };
    },
    watch: {
        startNodevariables: {
            handler(newVal, oldVal) {
                this.$emit(
                    "updateCellData",
                    { startNodevariables: newVal },
                    this.curNode
                );
            },
            deep: true,
        },
    },
    mounted ()
    {

    },

    methods: {
      openDrawer ()
      {
        this.initDrawer();
        this.appForm.inputs = this.nodeStoreData.input && this.nodeStoreData.input.length ? this.nodeStoreData.input.filter((items) => items.label && items.enabled) : [];
        this.appForm.output = this.nodeStoreData.output;
        this.startNodevariables = this.nodeStoreData.startNodevariables || [];
      },
      updateInputList(data,i) {
        this.startNodevariables[i] = JSON.parse(data.inputs)
        this.$emit(
                  "updateCellData",
                  { startNodevariables: this.startNodevariables },
                  this.curNode
              );
          // this.appForm.inputs = JSON.parse(data.inputs);
          // this.$emit("updateCellData", { inputs: data.inputs }, this.curNode);
      },
    },
  };
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>