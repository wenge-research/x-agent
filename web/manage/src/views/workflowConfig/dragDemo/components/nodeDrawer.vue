<template>
  <div class="Ec-x6-icon">
    <el-drawer :visible.sync="visible" :direction="direction" :modal="false" :modal-append-to-body="false" size="400px"
      style="position: absolute;width: 400px; box-sizing: border-box" :show-close="false">
      <div v-if="type === 'multi_agent'">
        <div class="drawer-header">
          <h3>{{ $t('node') }}</h3>
        </div>
        <section class="listBar" @click.stop="">
          <div class="listBar-cot listBar-agent">
            <div v-for="(item, index) in configList.filter(item => item.attribute === 'mulAgent')" :key="item.img + index"
              class="drag-cot" draggable="true" @drag="drag(item)" @click="clickItem(item)" @dragend="dragend(item)">
              <p>
                <svg class="icon" aria-hidden="true">
                  <use :xlink:href="`#icon-` + item.img"></use>
                </svg>
                {{ item.label }}
              </p>
            </div>
          </div>
        </section>
      </div>
      <div v-else>
        <div class="drawer-header">
          <h3>{{ $t("appendTo") }}{{ $t('node') }}</h3>
          <div class="node-manage" @click="openNodeManage">
            <i class="el-icon-s-tools"></i>
            <h3 style="padding-left: 7px; cursor: pointer;">{{ $t("customLabelNode") }}</h3>
          </div>
          <!-- <i class="el-icon-d-arrow-left" @click="closeDrawer"></i> -->
        </div>
        <section class="listBar" @click.stop="">
          <div class="listBar-cot">
            <!-- <div
              v-for="(item, index) in configList"
              :key="item.img + index"
              class="drag-cot"
              draggable="true"
              @drag="drag(item)" @click="clickItem(item)"
              @dragend="dragend(item)"
            > -->
            <div class="subTitle"><span>基础</span>
              <p></p>
            </div>
            <div v-for="(item, index) in configList.filter(item => item.attribute === 'process')" :key="item.img + index"
              class="drag-cot" draggable="true" @drag="drag(item)" @click="clickItem(item)" @dragend="dragend(item)">
              <el-tooltip popper-class="workflow-tooltip" :content="item.intro" placement="top" effect="light" :enterable="false" :open-delay="300">
                <p>
                  <svg class="icon" aria-hidden="true">
                    <use :xlink:href="`#icon-` + item.img"></use>
                  </svg>
                  {{ item.label }}
                </p>
              </el-tooltip>
              
            </div>
            <div class="subTitle"><span>工具</span>
              <p></p>
            </div>
            <div v-for="(item, index) in configList.filter(item => item.attribute === 'tool')" :key="item.img + index"
              class="drag-cot" draggable="true" @drag="drag(item)" @click="clickItem(item)" @dragend="dragend(item)">
              <el-tooltip popper-class="workflow-tooltip" :content="item.intro" placement="top" effect="light" :enterable="false" :open-delay="300">
                <p>
                  <svg class="icon" aria-hidden="true">
                    <use :xlink:href="`#icon-` + item.img"></use>
                  </svg>
                  {{ item.label }}
                </p>
              </el-tooltip>
            </div>
            <div class="subTitle"><span>转换</span>
              <p></p>
            </div>
            <div v-for="(item, index) in configList.filter(item => item.attribute === 'convert')" :key="item.img + index"
              class="drag-cot" draggable="true" @drag="drag(item)" @click="clickItem(item)" @dragend="dragend(item)">
              <el-tooltip popper-class="workflow-tooltip" :content="item.intro" placement="top" effect="light" :enterable="false" :open-delay="300">
                <p>
                  <svg class="icon" aria-hidden="true">
                    <use :xlink:href="`#icon-` + item.img"></use>
                  </svg>
                  {{ item.label }}
                </p>
              </el-tooltip>
            </div>
            <div class="subTitle"><span style="width: 20%;">知识&数据</span>
              <p style="width: 80%;"></p>
            </div>
            <div v-for="(item, index) in configList.filter(item => item.attribute === 'knowledge')" :key="item.img + index"
              class="drag-cot" draggable="true" @drag="drag(item)" @click="clickItem(item)" @dragend="dragend(item)">
              <el-tooltip popper-class="workflow-tooltip" :content="item.intro" placement="top" effect="light" :enterable="false" :open-delay="300">
                <p>
                  <svg class="icon" aria-hidden="true">
                    <use :xlink:href="`#icon-` + item.img"></use>
                  </svg>
                  {{ item.label }}
                </p>
              </el-tooltip>
            </div>
            <div class="subTitle"><span>组件</span>
              <p></p>
            </div>
            <div v-for="(item, index) in configList.filter(item => item.attribute === 'component')" :key="item.img + index"
              class="drag-cot" draggable="true" @drag="drag(item)" @click="clickItem(item)" @dragend="dragend(item)">
              <el-tooltip popper-class="workflow-tooltip" :content="item.intro" placement="top" effect="light" :enterable="false" :open-delay="300">
                <p>
                  <svg class="icon" aria-hidden="true">
                    <use :xlink:href="`#icon-` + item.img"></use>
                  </svg>
                  
                  {{ item.label }}
                </p>
              </el-tooltip>
            </div>
            <div class="subTitle"><span style="width: 15%;">自定义</span>
              <p style="width: 85%;"></p>
            </div>
            <div v-for="(item, index) in NodeList" :key="index"
              class="drag-cot" draggable="true" @drag="drag(item)" @click="clickItem(item)" @dragend="dragend(item)">
              <p>
                <svg class="icon" aria-hidden="true">
                  <use :xlink:href="`#icon-` + item.img"></use>
                </svg>
                {{ item.label}}
              </p>
            </div>
            <div class="subTitle" v-if="configList.filter(item => item.attribute === 'other').length"><span>其他</span>
              <p></p>
            </div>
            <div v-for="(item, index) in configList.filter(item => item.attribute === 'other')" :key="item.img + index"
              class="drag-cot" draggable="true" @drag="drag(item)" @click="clickItem(item)" @dragend="dragend(item)">
              <p>
                <svg class="icon" aria-hidden="true">
                  <use :xlink:href="`#icon-` + item.img"></use>
                </svg>
                {{ item.label }}
              </p>
            </div>
            <!-- </div> -->
          </div>
        </section>
      </div>
    </el-drawer>
    
  </div>
</template>

<script>
import { nodeList, pluginList } from "@/api/workflow";
import { nodeConfig } from "./nodeConfig.js";
const mouseXY = { x: null, y: null };
import config from "./config";
export default {
  name: "nodeDrawer",
  props: {
    type: String,
  },
  data() {
    return {
      visible: true,
      direction: "ltr",
      showMore: false,
      configList: config,
      directionRtl: "rtl",
      statusValue: true,
      isDefineNode: false,
      deduceData: {},
      NodeList:[]
    };
  },
  mounted() {
    document.getElementById("container").addEventListener(
      "dragover",
      function (e) {
        mouseXY.x = e.clientX;
        mouseXY.y = e.clientY;
      },
      false
    );
    this.getNodeList()
    this.getPluginListList()
    this.$EventBus.$on("showNodeList", () => {
      this.sureDrawerNode()
    })
  },
  methods: {
    getPluginListList() {
      const userInfo = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : null;
      const accountName = userInfo ? userInfo.accountName : '';
      pluginList({
        pageNo: 1,
        pageSize: 100,
        componentName: '推演',
        order: 'update_time', 
        sort: 'desc',
        // type:2,
        createUser: '',
        publishAppStore: "1"
      }).then((res) => {
        if (res.code == "000000") {
          this.deduceData = res.data?.records[0];
        } else {
          this.deduceData = {};
        }
      });
    },
    sureDrawerNode() {
      this.getNodeList()
    },
    getNodeList() {
      nodeList('CUSTOM').then((res) => {
        if (res.code == "000000") {
          this.NodeList = res.data.map(item => {
            item.type = 'custom'
            item.img = 'workflow-zidingyi'
            item.label = item.nodeName
            item.variables = [{ name: "result", description: "result", type: "array[string]" }]
            return item
          }) || []
        } else {
          this.NodeList = [];
        }
      });
    },
    drag: function () {
      // const parentRect = document
      //   .getElementById("container")
      //   .getBoundingClientRect();
      // let mouseInGrid = false;
      // if (
      //   mouseXY.x > parentRect.left &&
      //   mouseXY.x < parentRect.right &&
      //   mouseXY.y > parentRect.top &&
      //   mouseXY.y < parentRect.bottom
      // ) {
      //   mouseInGrid = true;
      // }
    },
    openDrawer() {
      this.visible = true;
    },
    closeDrawer() {
      this.visible = false;
    },
    //节点管理
    openNodeManage() {
      this.$EventBus.$emit("showVisibleNode");
    },
    addStartNode() {
      let variables = this.panDuanVariables()
      this.$emit("addNode", nodeConfig({ "type": "output", "img": "kaishi", "label": "开始", "variables": variables, "attribute": "process" }, 500, 400, this.$store));
    },
    addAgentNode() {
      this.$emit("addNode", nodeConfig({ "type": "agent", "img": "agent", "label": "新Agent", "variables": [], "attribute": "mulAgent" }, 1200, 400, this.$store), [], true);
    },
    addEndNode() {
      this.$emit("addNode", nodeConfig({ "type": "onlyIn", "img": "jieshu", "label": "结束", "variables": [{ "name": "", "value": "", "type": "", "selectedGroup": "" }], "attribute": "process" }, 1358, 380, this.$store));
    },
    // 根据类型判断工作流还是对话流
    panDuanVariables() {
      let workFlowType = this.$store.state.workflow.workFlowType;

      let data = [];
      let workflowState = [
        {
          description: "question",
          maxLength: 20,
          name: "question",
          required: true,
          type: "string",
        }
      ]

      let dialogueState = [
        {
          description: "USER_INPUT",
          maxLength: 20,
          name: "USER_INPUT",
          required: true,
          type: "string",
        },
        {
          description: "CONVERSATION_NAME",
          maxLength: 20,
          name: "CONVERSATION_NAME",
          required: true,
          type: "string",
        },
      ]

      if (workFlowType === 'dialogue') {
        data = dialogueState

      } else {
        data = workflowState
      }
      return data
    },

    dragend: function (item) {
      console.log(JSON.stringify(item))
      item.id = item.label + Date.now()
      const parentRect = document
        .getElementById("container")
        .getBoundingClientRect();
      let mouseInGrid = false;
      if (
        mouseXY.x > parentRect.left &&
        mouseXY.x < parentRect.right &&
        mouseXY.y > parentRect.top &&
        mouseXY.y < parentRect.bottom
      ) {
        mouseInGrid = true;
      }
      if (mouseInGrid === true) {
        this.$emit("addNode", nodeConfig(item, mouseXY.x, mouseXY.y, this.$store), this.deduceData);
      }
      // 新增节点
      if (!['iteration', 'tool', 'mcp'].includes(item?.type)) {
        this.$EventBus.$emit("saveWorkflow");
      }

    },
    clickItem: function (item) {
      item.id = item.label + Date.now()
      this.$emit("addNode", nodeConfig(item, 800, 300, this.$store), this.deduceData);
    },
  },
};
</script>
<style lang="scss" scoped>
.Ec-x6-icon ::v-deep .el-drawer__wrapper {
  border-radius: 0;
  left: 58px;
}

.Ec-x6-icon ::v-deep .el-drawer__body {
  overflow-y: auto;
  overflow-x: hidden;
  position: relative;
  -ms-overflow-style: none;  /* Internet Explorer 10+ */
  scrollbar-width: none;     /* Firefox */
  &::-webkit-scrollbar {
    display: none;
  }
}
.Ec-x6-icon ::v-deep .el-drawer {
  padding: 24px;
}

.Ec-x6-icon ::v-deep .el-drawer__header {
  padding: 0;
  margin-bottom: 0;
}
.drawer-header {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .node-manage {
    display: flex;
    align-items: center;
    // width: 104px;
    cursor: pointer;
    .el-icon-s-tools {
      font-size: 18px;
    }
  }
  h3 {
    font-size: 18px;
    color: #383D47;
  }

  .el-icon-d-arrow-left {
    position: absolute;
    right: 0;
    top: 2px;
    cursor: pointer;
  }
}

.listBar {
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;
  font-size: 14px;
  margin: 10px 0 0 0;
}

.listBar img {
  width: 12px;
  margin-right: 8px;
}

.arrow {
  transform: rotate(90deg);
  transition-duration: 0.3s;
}

.listBar-cot {
  //   display: flex;
  //   flex-wrap: wrap;
  box-sizing: border-box;
  width: 100%;
  text-align: left;
}

.subTitle {
  width: 100%;
  height: 60px;
  line-height: 60px;
  clear: both;
  display: flex;
  justify-content: space-between;

  span {
    width: 10%;
  }

  p {
    width: 90%;
    border-bottom: 1px solid rgba(0, 0, 0, 0.12);
    position: relative;
    top: 29px;
    height: 1px;
  }
}

.listBar-cot .drag-cot {
  width: 50%;
  text-align: left;
  height: 48px;
  line-height: 48px;
  cursor: pointer;
  padding-left: 8px;
  float: left;

  &:hover {
    background: #FFFFFF;
    box-shadow: 0px 0px 10px 0px rgba(9, 31, 76, 0.12);
    border-radius: 4px;
    border: 1px solid #dbdbdb;
  }
}
.listBar-cot.listBar-agent .drag-cot {
  width: 100%;
    border-radius: 4px;
    border: 1px solid #E7E7E7;
    margin: 6px auto;
    padding: 0 0 0 12px;
    p .icon {
      margin-right: 6px;
    }
}


.listBar-cot .drag-cot p .icon {
  width: 22px;
  height: 22px;
  position: relative;
  top: 4px;
  margin-right: 2px;
}

.listBar-cot .drag-cot p {
  font-size: 14px;
  color: #383D47;
  cursor: pointer;

}

::v-deep .el-drawer__header>:first-child {
  font-family: MiSans, MiSans;
  font-weight: 500;
  font-size: 20px;
  color: #1D2129;
  line-height: 32px;
  text-align: left;
  font-style: normal;
  text-transform: none;
}

::v-deep .el-drawer__close-btn {
  font-size: 30px;
}

::v-deep .el-drawer {
  padding: 32px;
}

.drawer-conter {
  .drawer-header {
    display: flex;
    justify-content: space-between;

    h3 {
      height: 32px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #1D2129;
      line-height: 32px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
  }

  .title {
    margin-top: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;

    .title-left {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #252E40;
      line-height: 20px;
      text-align: justify;
      font-style: normal;
    }

    .title-right {
      width: 104px;
      height: 32px;
      border-radius: 4px;
      border: 1px solid #E7E7E7;
      line-height: 32px;

      span {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #1D2129;
        line-height: 20px;
        text-align: left;
        font-style: normal;
      }
    }
  }

  .node-list {
    margin-top: 16px;

    li {
      height: 56px;
      border-radius: 2px;
      border: 1px solid #E7E7E7;
      display: flex;
      padding: 10px;
      position: relative;
      margin-bottom: 12px;

      .node-left {
        width: 32px;
        height: 32px;
        background: #6970E2;
        border-radius: 2px;
        margin: 2px;
      }

      .introduce {
        margin-left: 10px;

        h3 {
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #1D2129;
          line-height: 20px;
          text-align: left;
          font-style: normal;
        }

        h4 {
          height: 16px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          color: #86909C;
          line-height: 16px;
          text-align: left;
          font-style: normal;
        }
      }

      .node-icon {
        position: absolute;
        top: 12px;
        right: 12px;
      }
    }
  }
}

.node-but {
  position: absolute;
  right: 0;
  bottom: 0;

  .el-icon-edit-outline {
    font-size: 14px;
  }
}

.headBar {
  background: #f2f5fa;
  padding: 16px 0 16px 16px;
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  width: 100%;
  position: absolute;
  top: 0;
  right: 0;

  img {
    width: 15px;
    height: 15px;
  }

  .leftSlide {
    display: flex;
    justify-content: space-between;
    align-items: center;

    >img {
      margin-right: 16px;
      cursor: pointer;
    }

    .titleIcon {
      p:first-child {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        color: #383d47;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        margin-bottom: 6px;
      }

      p:last-child {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #768094;
        line-height: 18px;
        text-align: left;
        font-style: normal;
      }
    }
  }

  .rightSlide {
    display: flex;
    justify-content: space-between;

    .preview {
      line-height: 36px;
      margin-right: 28px;
      cursor: pointer;

      >span {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #3666ea;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        text-transform: none;
      }

      >img {
        margin-right: 5px;
      }

      >span,
      >img {
        vertical-align: middle;
      }
    }

    .btn {
      height: 40px;
      color: #3666ea;
      border: 1px solid #3666ea;
      border-radius: 4px;

      img {
        margin-right: 5px;
      }

      img,
      span {
        vertical-align: middle;
      }
    }

    .btns {
      color: #fff;
      height: 40px;
      background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
      border-radius: 4px;

      img {
        margin-right: 5px;
      }

      img,
      span {
        vertical-align: middle;
      }
    }
  }
}
</style>
