<template>
  <div class="Ec-x6-icon" @click="handleDrawerClick">
    <el-drawer title="" :visible.sync="drawerVisible" :modal="false" :modal-append-to-body="false"
      :direction="direction" v-if="drawerVisible" size="500px" style="
        position: absolute;
        width: 500px;
        box-sizing: border-box;
        right: 0;
        left: inherit;
      " :show-close="false" :before-close="handleClose">
      <div style="display: flex;align-items: center;justify-content: space-between">
        <HeadTool v-if="drawerVisible" :label="sourceData.label" :imgWidth="24" :imgHeight="24" @remove="handleRemove"
          @copy="copyHandler" @input="inputHandler" @testNodes="openRunDrawer" imgSuffix="agent" />
        <div style="width: 1px;height: 20px;background: #D3D9E6;margin: 0 12px 12px;"></div>
        <iconpark-icon name="close-line" size="18" color="#828894" @click="closeDrawer"
          style="margin-bottom: 10px;cursor: pointer"></iconpark-icon>
      </div>
      <div class="drawer-content">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="0" :title="$t('model')">
            <modelSelect v-if="drawerVisible" :key="appForm.modelId" :id="appForm.modelId" @change="modelChange">
            </modelSelect>
          </el-collapse-item>
          <el-collapse-item name="1">
            <template #title>
              <div class="systemPromptTitle">
                <div>适用场景</div>
              </div>
            </template>
            <el-input v-model="appForm.sence" type="textarea" @change="changeAgentParams" :placeholder="modelTip" :rows="3"
              @keydown.delete="handleKeyDown"></el-input>
          </el-collapse-item>
          <el-collapse-item name="2">
            <template #title>
              <div class="systemPromptTitle">
                <div>Agent提示词</div>
              </div>
            </template>
            <el-input v-model="appForm.systemPrompt" type="textarea" :placeholder="userTip" :rows="3"
              @keydown.delete="handleKeyDown"></el-input>
          </el-collapse-item>
          <el-collapse-item name="3">
            <template #title>
              <div class="systemPromptTitle">
                <div>知识库</div>
              </div>
            </template>
            <div class="list-cont">
              <div class="marginTop32">
                <div>
                  <ul>
                    <li class="base-li flex-center just" v-for="(
                                item, index
                            ) in appForm.knnIdList" :key="index" @click="konwlwdgeClick(item)">
                      <div class="li-name flex-center">
                        <img src="@/assets/images/appManagement/zsk.svg" />
                        <span>{{
                          filterKnowledge(item)
                          }}</span>
                      </div>
                      <i class="el-icon-close" style="cursor: pointer" @click.stop="deleteKnowledgeId(item)"></i>
                    </li>
                  </ul>
                </div>
                <el-button icon="el-icon-plus" style="color: rgb(56, 61, 71);width: 100%;"
                  @click.stop="openDialog('addBase')">关联知识库</el-button>
              </div>
            </div>
          </el-collapse-item>
          <el-collapse-item name="4">
            <template #title>
              <div class="systemPromptTitle">
                <div>技能</div>
                <span class="btn-list">
                  <span :class="{'active' : activeName === 'first'}" @click.stop="handleClick('second')">MCP</span>
                  <!-- <li @click="handleClick('third')">插件</li> -->
                </span>
              </div>
            </template>
            <div class="list-cont">
              <div v-show="activeName === 'first'" class="marginTop32">
                <div>
                  <ul>
                    <li class="base-li flex-center just" v-for="(item, index) in appForm.mcpIdList" :key="index">
                      <template>
                        <div class="li-name flex-center">
                          {{ item.mcpName }}
                        </div>
                        <i class="el-icon-close" style="cursor: pointer" @click="removeItemMcp(index)"></i>
                      </template>
                    </li>
                  </ul>
                </div>
                <el-button icon="el-icon-plus" style="color: rgb(56, 61, 71);width: 100%;"
                  @click.stop="openDialogMcp">关联MCP</el-button>
              </div>
            </div>
          </el-collapse-item>
          <!-- <el-collapse-item name="4">
            <template #title>
              <div class="systemPromptTitle">
                <div>用户提示词</div>
              </div>
              <div style="margin-left: auto; pointer-events: auto;" @click.stop>
                <el-switch size="small" v-model="appForm.switch"></el-switch>
              </div>
            </template>
            <div class="sub">根据对话内容，在回复后自动提供3个提问建</div>
            <el-checkbox v-model="appForm.checked">用户自定义Prompt</el-checkbox>
            <el-input v-model="appForm.userPrompt" type="textarea" :placeholder="userTip" :rows="3"
              @keydown.delete="handleKeyDown"></el-input>
          </el-collapse-item> -->
        </el-collapse>
      </div>
    </el-drawer>
    <!-- 知识库参数设置 -->
    <knowledgeBaseSet v-if="setBaseVisible" :dialogVisible="setBaseVisible" :params="appForm" @clickConfig="clickConfig"
      @clickConfigParams="clickConfigParams"></knowledgeBaseSet>
    <!-- 新增知识库 -->
    <addKnowledgeBase v-if="addBaseVisible" :dialogVisible="addBaseVisible" :configData="appForm.knnIdList"
      @clickConfig="clickConfig" @clickConfigParams="clickConfigParams"></addKnowledgeBase>
    <!-- 放大框 -->
    <magnifiedEditBox v-if="drawerVisible" ref="magnifiedEditBox" @input="handleInput"
      @openVocabularyDrawer="openVocabularyDrawer(editName)" @update:visible="handleMagnifiedBoxVisibleChange" />
    <mcpDialog v-if="addSensitiveVisible" :dialogVisible="addSensitiveVisible" @clickConfig="clickConfigMcp"
      :configData="interceptWordHouses" @clickConfigParams="clickConfigParams"
      :appConfigForm="params.applicationInfo"></mcpDialog>
  </div>
</template>

<script>
  // mixins
  import drawerMixins from './drawerMixins'
  import magnifiedEditBox from '@/views/workflowConfig/dragDemo/components/magnifiedEditBox.vue'
  import modelSelect from "@/components/ModelSelect.vue";
  import knowledgeBaseSet from "@/views/appManage/components/knowledgeBaseSet.vue";
  import addKnowledgeBase from "@/views/appManage/components/addKnowledgeBase.vue";
  import mcpDialog from "@/views/workflowConfig/dragDemo/components/selectMcp.vue";
  export default {
    name: "modelDrawer",
    mixins: [drawerMixins],
    components: { magnifiedEditBox,mcpDialog, knowledgeBaseSet, addKnowledgeBase, modelSelect },
    props: [ "params"],
    data ()
    {
      return {
        interceptWordHouses: [],
        addSensitiveVisible: false,
        modelTip: '请输入适用场景',
        userTip: '请输入Agent提示词',
        magnifiedBoxVisible: false,
        knowledgeId: [],
        setBaseVisible: false, // 设置知识库参数
        addBaseVisible: false, // 新增知识库
        konwlwdgeList: [], //知识库数据
        knowledgeIdArr: [],
        editName: '',
        activeName: 'first',
        appForm: {
          knnIdList: [],
          mcpIdList: [],
          systemPrompt: "",
          userPrompt: "",
          sence: "",
          modelId: "",
          switch: true,
        },
        activeNames: ["0", "1", "2", "3", "4"],
      };
    },
    mounted ()
    {
      this.$EventBus.$on('addKnowledgeToApp', (val) =>
      {
        this.addKnowledgeToApp(val)
      })
      this.$EventBus.$on('deleteKnowledgeToApp', (val) =>
      {
        this.deleteKnowledgeToApp(val)
      })
    },
    beforeDestroy () { },
    methods: {
      handleKeyDown (event)
      {
        event.preventDefault()
      },
      handleClick (tab, event)
      {
        this.activeName = tab
      },
      konwlwdgeClick (item)
      {
        this.$store.commit('setKonwledge', null)

        if (this.filterKnowledge(item)) {
          this.$store.commit('setIsAddedToApp', true);
        } else {
          this.$store.commit('setIsAddedToApp', false);
        }

        this.$store.commit('setKonwledge', item)
      },
      // 打开弹窗
      openDialog (type)
      {
        if (type == "setBase") {
          this.setBaseVisible = true;
        } else if (type == "addBase") {
          this.knowledgeIdArr = this.appForm.knnIdList;
          this.addBaseVisible = true;
        }
      },
      filterKnowledge (item)
      {
        let findItem = this.$store.state?.workflow?.knowledgeList.find(
          (items) => items.knowledgeId == item.knowledgeId
        );
        return findItem?.knowledgeName;
      },
      // 删除知识库
      deleteKnowledgeId (item)
      {
        let filterArr = this.knowledgeIdArr.filter(
          (items) => items != item
        );
        this.knowledgeIdArr = this.appForm.knnIdList = filterArr || [];
      },
      clickConfig ()
      {
        this.addBaseVisible = false;
        this.setBaseVisible = false;
        this.$EventBus.$emit("saveWorkflow");
      },
      // 弹窗回调带参数
      clickConfigParams (type, data, selectList)
      {
        if (type == "addBaseVisible") {
          // 新增知识库
          if (data.length > 0) {
            this.knowledgeIdArr = this.appForm.knnIdList =
              data || [];
          }
          this.$emit("updateAppForm",{appForm: JSON.stringify(this.appForm)}, this.curNode);
        } else if (type == "setBaseVisible") {
          this.setBaseVisible = false;
          // 知识库参数设置
          this.appForm.rangeContentScore = data.rangeContentScore;
          this.appForm.qaTitleScore = data.qaTitleScore;
          this.appForm.qaRangeTitleScore = data.qaRangeTitleScore;
          this.appForm.qaContentScore = data.qaContentScore;
          this.appForm.qaRangeContentScore = data.qaRangeContentScore;
          this.appForm.filterNum = data.filterNum;
          this.appForm.prepareNum = data.prepareNum;
          this.$EventBus.$emit("saveWorkflow");
        } else if (type == "addSensitive") {
          this.interceptWordHouses = data;
          if (selectList.length > 0) {
            this.appForm.mcpIdList = selectList || [];
            this.$EventBus.$emit("saveWorkflow");
          }
          this.$emit("updateAppForm",{appForm: JSON.stringify(this.appForm)}, this.curNode);
        }
      },
      addKnowledgeToApp (data)
      {
        // this.knowledgeIdArr = this.appForm.knnIdList
        if (!this.appForm.knnIdList.includes(data.knowledgeId)) {
          this.appForm.knnIdList.push(data.knowledgeId);

        }
      },
      deleteKnowledgeToApp (data)
      {
        const index = this.appForm.knnIdList && this.appForm.knnIdList.findIndex((items) => items == data.knowledgeId);
        this.appForm.knnIdList.splice(index, 1);
        this.$emit("updateAppForm",{appForm: JSON.stringify(this.appForm)}, this.curNode);
      },
      openDialogMcp ()
      {
        this.addSensitiveVisible = true;
      },
      clickConfigMcp ()
      {
        this.addSensitiveVisible = false;
      },
      switchChange ()
      {
      },
      removeItemMcp (index)
      {
        this.appForm.mcpIdList.splice(index, 1);
        this.interceptWordHouses = this.appForm.mcpIdList.map(
          (item) => item.mcpId
        );
        this.$EventBus.$emit("saveWorkflow", this.appForm.mcpIdList);
        this.$emit("updateAppForm",{appForm: JSON.stringify(this.appForm)}, this.curNode);
      },
      openDrawer() {
            this.initDrawer();
            this.interceptWordHouses = this.appForm.mcpIdList ? this.appForm.mcpIdList.map(
                (item) => item.mcpId
            ) : []
            this.activeName = 'first'
        },
    },
  };
</script>
<style lang="scss" src="./nodeTheme/node.scss" scoped></style>
<style lang="scss" scoped>
  .base-li {
    height: 40px;
    background: #ffffff;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    padding: 0 12px;
    margin-bottom: 8px;

    .li-name {
      font-weight: 400;
      font-size: 14px;
      color: #383d47;
      text-align: left;
      font-style: normal;

      >img {
        width: 22px;
        height: 22px;
        color: #a4bffe;
        margin-right: 5px;
      }
    }
  }

  .mcp-li {
    height: 40px;
    background: #ffffff;
    border-radius: 4px;
    border: 1px solid #e1e4eb;
    padding: 0 12px;
    margin-bottom: 8px;

    .li-name {
      font-weight: 400;
      font-size: 14px;
      color: #383d47;
      text-align: left;
      font-style: normal;

      >img {
        width: 22px;
        height: 22px;
        color: #a4bffe;
        margin-right: 5px;
      }
    }
  }

  .systemPromptTitle {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .btn-list {
      position: relative;
      left: 10px;

      span {
        margin: 0 0 0 10px;
        color: #999;

        &.active {
          color: #1747E5;
        }
      }
    }
  }

  .list-cont {
    margin: 10px 0 0 0;
  }

  .Ec-x6-icon .drawer-content {
    height: calc(100vh - 150px);
  }
</style>