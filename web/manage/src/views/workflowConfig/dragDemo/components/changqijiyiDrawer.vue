<template>
  <div class="Ec-x6-icon">
      <el-drawer
          title=""
          :visible.sync="drawerVisible"
          :modal="false"
          :modal-append-to-body="false"
          :direction="direction"
          v-if="drawerVisible"
          size="500px"
          style="
              position: absolute;
              width: 500px;
              box-sizing: border-box;
              right: 0;
              left: inherit;
          "
          :show-close="false"
          :before-close="handleClose"
      >
          <div
              style="
                  display: flex;
                  align-items: center;
                  justify-content: space-between;
              "
          >
              <HeadTool
                  v-if="drawerVisible"
                  :label="sourceData.label"
                  :imgWidth="24"
                  :imgHeight="24"
                  @remove="handleRemove"
                  @copy="copyHandler"
                  @input="inputHandler"
                  @testNodes="openRunDrawer"
                  imgSuffix="changqijiyi"
              />
              <div
                  style="
                      width: 1px;
                      height: 20px;
                      background: #d3d9e6;
                      margin: 0 12px 12px;
                  "
              ></div>
              <iconpark-icon
                  name="close-line"
                  size="18"
                  color="#828894"
                  @click="closeDrawer"
                  style="margin-bottom: 10px; cursor: pointer"
              ></iconpark-icon>
          </div>
          <div class="sub">
            用于调用长期记忆，获取用户的个性化信息，智能体必须打开长期记忆
          </div>
          <div class="drawer-content">
              <el-collapse v-model="activeNames" >
                  <el-collapse-item name="0">
                    <template slot="title">
                        <div class="title-flex">
                            <span>{{ $t('input') }}</span>
                            <div class="icon-ctn">
                            <el-tooltip popper-class="workflow-tooltip" effect="dark" :content="'输入需用于长期记忆匹配的关键信息'" placement="top" :effect="'light'">
                                <iconpark-icon name="question-line" size="16" color="#C9CDD4" style="margin-left: 5px;"></iconpark-icon>
                            </el-tooltip>
                            </div>
                        </div>
                    </template>
                    <inputList v-if="drawerVisible" :inputs="appForm.inputs"
                          :key="inputKey" @updateInputList="updateInputList" :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs" :curParentNodesData="curParentNodesData" :single="true" :defalutVal="'Query'" ></inputList>
                  </el-collapse-item>
                  <el-collapse-item name="3" :title="$t('output')">
                      <div class="result-cont">
                        result<span>array[object]</span>
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

export default {
  props: ["panels", "params"],
  name: "CustomDrawer",
  components: {
  },
  mixins: [drawerMixins],
  data() {
      return {
          appForm: {
             
          },
          
         
      };
  },
  mounted() {
      
  },
  methods: {
      
     
  },
};
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style scoped lang="scss">
.title-flex{
  display: flex;
  align-items: center;
  height: 20px;

  span{
    display: inline-block;
    height: 20px;
    line-height: 20px;
  }

  .icon-ctn{
    height: 16px;
    line-height: 19px;
    position: relative;

  }
}
</style>
