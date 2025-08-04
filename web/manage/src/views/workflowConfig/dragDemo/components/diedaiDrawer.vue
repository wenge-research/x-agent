<template>
  <div class="Ec-x6-icon">
    <el-drawer title="" :visible.sync="drawerVisible" :modal="false" :modal-append-to-body="false"
      :direction="direction" v-if="drawerVisible" size="500px" style="
                position: absolute;
                width: 500px;
                box-sizing: border-box;
                right: 0;
                left: inherit;
            " :show-close="false" :before-close="handleClose">
      <div style="
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                ">
        <HeadTool v-if="drawerVisible" :label="sourceData.label" :imgWidth="24" :imgHeight="24" @remove="handleRemove"
          @copy="copyHandler" @input="inputHandler" @testNodes="openRunDrawer" imgSuffix="diedai" />
        <div style="
                        width: 1px;
                        height: 20px;
                        background: #d3d9e6;
                        margin: 0 12px 12px;
                    "></div>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer"
          style="margin-bottom: 10px; cursor: pointer"></iconpark-icon>
      </div>
      <div class="sub">迭代节点</div>
      <div class="drawer-content">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="0" :title="$t('input')">
            <inputList v-if="drawerVisible" :inputs="appForm.inputs" :defalutFirstVal="'变量名(第一个为迭代参数)'"
              :parentNodes="parentNodes" @updateInputList="updateInputList" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData"></inputList>
          </el-collapse-item>
          <el-collapse-item name="1" title="迭代设置">
            <el-form @submit.prevent>
              <el-form-item label="并行模式" style="width: 50%;display: inline-flex;">
                <el-switch
                size="small" 
                    v-model="appForm.parallel"
                    @change="switchChangeParallel"
                >
                </el-switch>
              </el-form-item>
              <el-form-item label="并发数" v-if="appForm.parallel" style="width: 50%;display: inline-flex;">
                <el-input-number v-model="appForm.process_num" size="small" :max="32" :min="1" style="
                display: inline-flex;
                width: 140px;
            "></el-input-number>
              </el-form-item>
              <div v-if="!appForm.parallel && drawerVisible">采用上次结果</div>
              <inputList v-if="!appForm.parallel && drawerVisible"
                  :inputs="appForm.item_result"
                  @updateInputList="updateItemResult"
                  :key="inputKey"
                  :parentNodes="curChildrenNodes"
              ></inputList>
              
              <el-form-item label="是否同步" style=" margin-top: 20px; ">
                <el-switch
                size="small" 
                    v-model="appForm.asyncFlag"
                    :active-value="$t('yes')"
                    :inactive-value="$t('no')"
                >
                </el-switch>
            </el-form-item>
            </el-form>
        </el-collapse-item>
          <el-collapse-item name="2" :title="$t('output')">
            <inputList
                v-if="drawerVisible"
                :inputs="appForm.outputs"
                @updateInputList="updateOutputList"
                :key="inputKey"
                :parentNodes="curChildrenNodes"
            ></inputList>
        </el-collapse-item>
        </el-collapse>
      </div>
    </el-drawer>
  </div>
</template>

<script>
  // mixins
  import drawerMixins from "./drawerMixins";
 
  export default {
    props: ["panels", "params"],
    name: "diedaiDrawer",
    components: {
    },
    mixins: [drawerMixins],
    data ()
    {
      return {
        appForm: {
            parallel: "false",
            process_num: 4,
            asyncFlag: this.$t('no'),
        },
        startAppForm: {
            parallel: "false",
            process_num: 4,
            asyncFlag: this.$t('no'),
        },
      };
    },
    watch: {
      
    },
    mounted () {
     
    },

    methods: {
      switchChangeParallel(val){
        this.appForm.item_result  = val ? [] : this.appForm.item_result;
      }
    },
  };
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>