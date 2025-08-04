<template>
  <div>
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
            @copy="copyHandler" @input="inputHandler" @testNodes="openRunDrawer" imgSuffix="disanfangjiekou" />

          <div style="
                  width: 1px;
                  height: 20px;
                  background: #d3d9e6;
                  margin: 0 12px 12px;
              "></div>
          <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer"
            style="margin-bottom: 10px; cursor: pointer"></iconpark-icon>
        </div>
        <div class="sub">
          支持通过 HTTP 请求获取数据。
        </div>
        <div class="drawer-content" :key="inputKey">
          <el-collapse v-model="activeNames">
            <el-collapse-item name="0" title="API">
              <div class="api-setting-url">
                <el-select v-model="nodeApi.settings.method" slot="prepend" style="width: 80px"
                  :placeholder="$t('pleaseSelect')" :disabled="true">
                  <el-option label="POST" :value="'POST'">POST</el-option>
                  <el-option label="GET" :value="'GET'">GET</el-option>
                </el-select>
                <el-input :placeholder="$t('pleaseEnterContent')" :disabled="true" v-model="nodeApi.settings.url"
                  class="input-with-select" style="margin-left: 12px;height: 40px;line-height: 40px;width: 260px">
                </el-input>
                <el-button slot="append" @click="apiDrawerFlag = true"
                  style="background-color: #1747E5;color: #fff;border-radius:2px;margin-left:12px;">{{$t('edit')}}</el-button>
              </div>
            </el-collapse-item>
            <el-collapse-item name="1" :title="$t('input')">
              <el-form :model="ruleForm" ref="ruleForm" label-width="0" class="demo-ruleForm">
                <el-form-item label="" v-for="(panel, index) in nodeApi.settings.requestBody" :key="index">
                  <inputList :defalutVal="panel.name" :defalutType="panel.type" :single="true" v-if="drawerVisible"
                    :inputs="startNodevariables[index]" @updateInputList="(value) => updateInputList(value, index)"
                    :parentNodes="parentNodes" :curParentNodeInputs="curParentNodeInputs"
                    :curParentNodesData="curParentNodesData"></inputList>
                </el-form-item>
              </el-form>
            </el-collapse-item>
            <el-collapse-item name="2" :title="$t('output')">
              <div class="result-cont" v-for="item in appForm.endNode">
                {{ item.name }}<span>{{ item.type }}</span>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </el-drawer>
    </div>
    <el-drawer :modal="false" :modal-append-to-body="false" :title="$t('editApi')" :visible.sync="apiDrawerFlag"
      v-if="apiDrawerFlag && drawerVisible" size="800px" style="
                position: absolute;
                width: 800px;
                box-sizing: border-box;
                right: 501px;
                top: 8px;
                border: 1px solid #ebeef5;
                left: inherit;">
      <ApiSetting :nodeData="nodeApi" @updateApi="updateApi"></ApiSetting>
    </el-drawer>
  </div>
</template>

<script>
  import ApiSetting from "@/views/toolManager/pluginManage/components/ApiSetting.vue";
  import drawerMixins from "./drawerMixins";

  export default {
    name: "apiDrawer",
    components: { ApiSetting },
    mixins: [drawerMixins],
    props: ["panels", "params"],
    data ()
    {
      return {
        ruleForm: {},
        voidValue: "",
        curNode: {},
        appForm: {
          endNode: [],
          method: 'GET',
          url: ''
        },
        drawerVisible: false,
        apiDrawerFlag: false,
        direction: "rtl",
        nodeApi: {
          "nodeName": "api",
          "nodeType": 2,
          "input": [

          ],
          "output": [
          ],
          "settings": {
            "url": "",
            "Accept": "*/*",
            "method": "GET",
            "headers": [
              {
                "key": "Authorization",
                "value": "Bearer 1_a89594606f2246539f67c85ad0ba801e"
              }
            ],
            "contentType": "application/json",
            "requestBody": [],
            "responseBody": '',
            "responseType": false
          },
          "next": null
        },
        parentNodes: [],
        variables: [
          {
            name: "",
            value: "",
            type: "",
            selectedGroup: "",
          },
        ],
        startNodevariables: [null, null, null, null, null, null, null],
      };
    },

    watch: {
      startNodevariables: {
        handler (newVal, oldVal)
        {
          this.$emit(
            "updateCellData",
            { startNodevariables: newVal },
            this.curNode
          );
        },
        deep: true,
      },
    },
    beforeDestroy () { },
    methods: {
      updateApi (settings, inputs, closeDrawerFlag)
      {
        this.nodeApi.settings = settings
        this.apiDrawerFlag = false
        this.appForm.endNode = this.nodeApi.settings.responseBody
        this.$emit('updateCellData', { nodeApi: JSON.stringify(this.nodeApi) }, this.curNode);
        this.$emit("updateAppForm",{appForm: JSON.stringify(this.appForm)}, this.curNode);
        this.startNodevariables = this.nodeApi.settings.requestBody.map((el) => {
                  return [ { "inputType": 1, "selectedGroup": "", "cusInput": "", desc: el.desc, "label": el.name, "lan": "", "value": "", type: el.type, orginType: el.type, } ]
              });
        this.inputKey++
      },
      openDrawer ()
      {
        let node = this.$store.state.workflow.editNode;
        let parentNodes = this.$store.state.workflow.parentNodes;
        let nodeStoreData = node.store.data.data;
        this.drawerVisible = true;
        this.curNode = node;
        this.parentNodes = parentNodes;
        this.inputKey++
        if (nodeStoreData.nodeApi) {
          this.nodeApi = JSON.parse(nodeStoreData.nodeApi)
        }
        this.startNodevariables = nodeStoreData.startNodevariables || [];
        this.appForm = nodeStoreData.appForm
                ? JSON.parse(nodeStoreData.appForm)
                : {};
      },
      updateInputList (data, i)
      {
        this.startNodevariables[i] = JSON.parse(data.inputs)
        this.$emit(
          "updateCellData",
          { startNodevariables: this.startNodevariables },
          this.curNode
        );
      },
      closeDrawer ()
      {
        this.drawerVisible = false;
      },


    },
  };
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style scoped lang="scss">
  .section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 20px 0 10px 0;
    font-size: 16px;
    color: #383d47;
  }
</style>