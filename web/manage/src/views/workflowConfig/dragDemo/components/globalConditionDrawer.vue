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
          @copy="copyHandler" @input="inputHandler" @testNodes="openRunDrawer" imgSuffix="quanjutiaozhuantiaojian" />
        <div style="
                        width: 1px;
                        height: 20px;
                        background: #d3d9e6;
                        margin: 0 12px 12px;
                    "></div>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer"
          style="margin-bottom: 10px; cursor: pointer"></iconpark-icon>
      </div>
      <div class="sub">全局跳转条件对所有agent生效</div>
      <div class="drawer-content" :key="inputKey">
        <el-input v-model="appForm.condition" type="textarea" @change="changeAgentParams" placeholder="在此输入执行的条件" :rows="5"></el-input>
      </div>
    </el-drawer>
  </div>
</template>

<script>
  // mixins
  import drawerMixins from "./drawerMixins";

  export default {
    props: ["componentParams"],
    name: "globalConditionDrawer",
    components: {},
    mixins: [drawerMixins],
    data ()
    {
      return {
        appForm: {
          condition: '',
        },

      };
    },
    mounted () { },
    methods: {

    },
  };
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>